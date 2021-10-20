/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2021  Achim Zielesny (achim.zielesny@googlemail.com)
 * 
 * Source code is available at <https://github.com/zielesny/MFsim>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.gnwi.mfsim.model.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Task for archiving a directory path in a ZIP file
 *
 * @author Achim Zielesny
 *
 */
public class ArchiveTask implements ProgressTaskInterface {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Miscellaneous utility methods
     */
    private final MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * File utility methods
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Property change support
     */
    private final PropertyChangeSupport propertyChangeSupport;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Task was started, false: Otherwise
     */
    private boolean isStarted;

    /**
     * True: Task is submitted to executor service, false: Otherwise
     */
    private boolean isSubmittedToExecutorService;

    /**
     * True: Task was stopped, false: Otherwise
     */
    private boolean isStopped;

    /**
     * True: Task finished, false: Otherwise
     */
    private boolean isFinished;

    /**
     * Directory path
     */
    private String directoryPath;

    /**
     * ZIP file pathname
     */
    private String zipFilePathname;

    /**
     * Number of files
     */
    private int numberOfFiles;

    /**
     * Variable for counting files
     */
    private int fileCounter;

    /**
     * ZIP output stream
     */
    ZipOutputStream zipOutputStream;

    /**
     * Pattern for file exclusion from archiving, i.e. files that match pattern
     * are NOT archived
     */
    private Pattern fileExclusionRegexPattern;

    /**
     * True: Archive file is uncompressed, false: Otherwise (archive file is
     * compressed)
     */
    private boolean isUncompressed;
    
    /**
     * Progress value
     */
    private int progressValue;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates a new instance
     *
     * @param aDirectoryPath Full path of directory (must exist otherwise an
     * exception is thrown)
     * @param aZipFilePathname Full path and name of ZIP file (is NOT allowed to
     * already exist otherwise an exception is thrown)
     * @param aFileExclusionRegexPatternString String with pattern for file
     * exclusion from archiving, i.e. files that match pattern are NOT archived
     * (may be null or empty)
     * @param anIsUncompressed True: Archive file is uncompressed, false:
     * Otherwise (archive file is compressed)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ArchiveTask(
        String aDirectoryPath, 
        String aZipFilePathname, 
        String aFileExclusionRegexPatternString, 
        boolean anIsUncompressed
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || !(new File(aDirectoryPath)).isDirectory()) {
            throw new IllegalArgumentException("Argument is illegal.");
        }
        if (aZipFilePathname == null || aZipFilePathname.isEmpty() || (new File(aZipFilePathname)).isFile()) {
            throw new IllegalArgumentException("Argument is illegal.");
        }
        // </editor-fold>
        this.isStarted = false;
        this.isStopped = false;
        this.isFinished = false;
        this.directoryPath = aDirectoryPath;
        this.zipFilePathname = aZipFilePathname;
        this.isUncompressed = anIsUncompressed;
        this.zipOutputStream = null;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.progressValue = -1;

        if (aFileExclusionRegexPatternString != null && !aFileExclusionRegexPatternString.isEmpty()) {
            this.fileExclusionRegexPattern = Pattern.compile(aFileExclusionRegexPatternString);
        } else {
            this.fileExclusionRegexPattern = null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * True: Task has successfully finished operations, false: Otherwise
     *
     * @return True: Task has successfully finished operations, false:
     * Otherwise
     */
    public boolean isFinished() {
        return this.isStarted && this.isFinished;
    }

    /**
     * True: Task is executing jobs, false: Otherwise
     *
     * @return True: Task is executing jobs, false: Otherwise
     */
    public boolean isWorking() {
        return this.isStarted && !this.isFinished;
    }

    /**
     * True: Task has started operations, false: Otherwise
     *
     * @return True: Task has started operations, false: Otherwise
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    /**
     * True: Task is submitted to executor service, false: Otherwise
     *
     * @return True: True: Task is submitted to executor service, false: Otherwise
     */
    public boolean isSubmittedToExecutorService() {
        return this.isSubmittedToExecutorService;
    }

    /**
     * Sets information that task is submitted to executor service
     */
    public void setSubmittedToExecutorService() {
        this.isSubmittedToExecutorService = true;
    }

    /**
     * Stops execution of task
     */
    public void stop() {
        this.isStopped = true;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public property change support methods">
    /**
     * Add property change listener
     * 
     * @param aListener Listener
     */
    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.addPropertyChangeListener(aListener);
    }

    /**
     * Remove property change listener
     * 
     * @param aListener Listener
     */
    public void removePropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.removePropertyChangeListener(aListener);
    }    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public (overriden) methods">
    // <editor-fold defaultstate="collapsed" desc="- call">
    /**
     * This method will be called when the task is executed. The methods
     * calculates the graphical particle positions.
     *
     * @return True if the graphical particle positions have been calculated
     * successfully, otherwise false.
     * @throws Exception Thrown when an error occurred
     */
    @Override
    public  Boolean call() throws Exception {
        try {
            // <editor-fold defaultstate="collapsed" desc="Task starts. Set progress in percent to 0.">
            this.isStarted = true;
            this.setProgressValue(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Count files to archive">
            // Set this.numberOfFiles
            File tmpDirectory = new File(this.directoryPath);
            this.numberOfFiles = 0;
            this.countFilesInDirectory(tmpDirectory);
            if (this.numberOfFiles == -1) {
                // Fire property change to notify property change listeners about cancellation due to internal error
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                return this.returnCancelled();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Archive directory into ZIP file">
            this.fileCounter = 0;
            this.zipOutputStream = new ZipOutputStream(new FileOutputStream(this.zipFilePathname));
            if (this.isUncompressed) {
                // Do NOT compress (faster)
                this.zipOutputStream.setLevel(ZipOutputStream.STORED);
            } else {
                // Compress (slower)
                this.zipOutputStream.setLevel(ZipOutputStream.DEFLATED);
            }
            if (!this.copyDirectoryToZipOutputStream(tmpDirectory, tmpDirectory, this.zipOutputStream)) {
                // Fire property change to notify property change listeners about cancellation due to internal error
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                return this.returnCancelled();
            } else {
                this.zipOutputStream.close();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this.isFinished to true BEFORE setting final progress in percent to 100">
            this.isFinished = true;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task has successfully finished. IMPORTANT: Set progress in percent to 100">
            this.setProgressValue(100);

            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // Fire property change to notify property change listeners about cancellation due to internal error
            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
            return this.returnCancelled();
        } finally {
            this.releaseMemory();
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Sets internal variables according to internal cancellation and returns
     * false
     *
     * @return False
     */
    private boolean returnCancelled() {
        this.deleteZipFile();
        this.isFinished = true;
        return false;
    }

    /**
     * Deletes ZIP file
     */
    private void deleteZipFile() {
        if (this.zipOutputStream != null) {
            try {
                this.zipOutputStream.close();
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                // Do nothing
            }
        }
        this.fileUtilityMethods.deleteSingleFile(this.zipFilePathname);
    }

    /**
     * Copies directory (with all files and sub directories) to open ZIP output
     * stream
     *
     * @param aDirectory Directory
     * @param aBaseDirectory Base directory
     * @param aZipOutputStream ZIP output stream
     *
     * @return True: Operation was successful, false: Otherwise
     */
    private boolean copyDirectoryToZipOutputStream(File aDirectory, File aBaseDirectory, ZipOutputStream aZipOutputStream) {
        // NO checks are performed
        try {
            File[] tmpFiles = aDirectory.listFiles();
            byte[] tmpBuffer = new byte[ModelDefinitions.BUFFER_SIZE];
            for (int i = 0; i < tmpFiles.length; i++) {
                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                if (this.isStopped) {
                    return this.returnCancelled();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set progress">
                this.setProgressValue(this.miscUtilityMethods.getPercentWithMax99(this.fileCounter, this.numberOfFiles));

                // </editor-fold>
                if (tmpFiles[i].isDirectory()) {
                    if (!this.copyDirectoryToZipOutputStream(tmpFiles[i], aBaseDirectory, aZipOutputStream)) {
                        return false;
                    }
                } else if (this.fileExclusionRegexPattern == null || !this.fileExclusionRegexPattern.matcher(tmpFiles[i].getName()).matches()) {
                    int tmpLength;
                    FileInputStream tmpFileInputStream = new FileInputStream(tmpFiles[i]);
                    ZipEntry tmpEntry = new ZipEntry(tmpFiles[i].getPath().substring(aBaseDirectory.getParent().length() + 1));
                    tmpEntry.setTime(tmpFiles[i].lastModified());
                    aZipOutputStream.putNextEntry(tmpEntry);
                    while ((tmpLength = tmpFileInputStream.read(tmpBuffer)) != -1) {
                        aZipOutputStream.write(tmpBuffer, 0, tmpLength);
                    }
                    tmpFileInputStream.close();
                    this.fileCounter++;
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * *
     * Counts files in aDirectory and all sub directories with class variable
     * this.numberOfFiles
     *
     * @param aDirectory Directory
     */
    private void countFilesInDirectory(File aDirectory) {

        // NO checks are performed
        try {
            File[] tmpFiles = aDirectory.listFiles();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (tmpFiles[i].isDirectory()) {
                    this.countFilesInDirectory(tmpFiles[i]);
                    if (this.numberOfFiles == -1) {
                        return;
                    }
                } else {
                    if (this.fileExclusionRegexPattern == null || !this.fileExclusionRegexPattern.matcher(tmpFiles[i].getName()).matches()) {
                        this.numberOfFiles++;
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            this.numberOfFiles = -1;
        }
    }

    /**
     * Release memory
     */
    private void releaseMemory() {
        this.zipOutputStream = null;
        this.fileExclusionRegexPattern = null;
    }

    /**
     * Set progress value and fire property change
     * 
     * @param aNewValue New value
     */
    private void setProgressValue(int aNewValue) {
        int tmpOldValue = this.progressValue;
        this.progressValue = aNewValue;
        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_PROGRESS, tmpOldValue, this.progressValue);
    }
    // </editor-fold>

}
