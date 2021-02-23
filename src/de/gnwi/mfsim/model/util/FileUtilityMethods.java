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

import de.gnwi.mfsim.model.preference.Preferences;
import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * File utility methods to be instantiated
 *
 * @author Achim Zielesny
 */
public class FileUtilityMethods {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Time utility methods
     */
    private final TimeUtilityMethods timeUtilityMethods = new TimeUtilityMethods();

    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Miscellaneous utility methods
     */
    private final MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public FileUtilityMethods() {
        // Do nothing
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Rename methods">
    /**
     * Renames directory. If renamed directory already exists an appendix is
     * generated in the form of "_V1", "_V2" etc.
     *
     * @param aDirectoryPath Full path of directory
     * @param aNewDirectoryName New name of directory (WITHOUT path information)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean renameDirectory(String aDirectoryPath, String aNewDirectoryName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || !(new File(aDirectoryPath)).isDirectory() || aNewDirectoryName == null || aNewDirectoryName.isEmpty()) {
            return false;
        }
        if ((new File(aDirectoryPath)).getName().equals(aNewDirectoryName)) {
            return true;
        }

        // </editor-fold>
        File tmpDirectory = new File(aDirectoryPath);
        File tmpNewDirectory = new File((new File(aDirectoryPath)).getParent() + File.separatorChar + aNewDirectoryName);
        int tmpCounter = 1;
        while (tmpNewDirectory.isDirectory()) {
            tmpNewDirectory = new File((new File(aDirectoryPath)).getParent() + File.separatorChar + aNewDirectoryName + "_V" + String.valueOf(tmpCounter++));
        }
        return tmpDirectory.renameTo(tmpNewDirectory);
    }

    /**
     * Renames single file
     *
     * @param aFilePathname Full pathname of file
     * @param newFileName New name of file (WITHOUT path information)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean renameSingleFile(String aFilePathname, String newFileName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilePathname == null || aFilePathname.isEmpty() || !(new File(aFilePathname)).isFile() || newFileName == null || newFileName.isEmpty()) {
            return false;
        }
        if ((new File(aFilePathname)).getName().equals(newFileName)) {
            return true;
        }

        // </editor-fold>
        File tmpFile = new File(aFilePathname);
        File tmpNewFile = new File((new File(aFilePathname)).getParent() + File.separatorChar + newFileName);
        return tmpFile.renameTo(tmpNewFile);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Copy methods">
    /**
     * Copies specified file to specified destination if file does NOT exist in
     * specified destination. NOTE: File will not be copied if specified
     * destination pathname already exists. Then true is returned.
     *
     * @param aSourceFilePathname Full pathname of source (may be null or empty
     * then false is returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * or empty then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyNonExistingSingleFile(String aSourceFilePathname, String aDestinationFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty() || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }
        if (aSourceFilePathname.equals(aDestinationFilePathname)) {
            // File is already in desired destination: Return true
            return true;
        }
        if (!(new File(aSourceFilePathname)).isFile()) {
            return false;
        }
        if ((new File(aDestinationFilePathname)).isFile()) {
            // File is already in desired destination: Return true
            return true;
        }
        // </editor-fold>
        try {
            FileInputStream tmpFileInputStream = new FileInputStream(aSourceFilePathname);
            FileOutputStream tmpFileOutputStream = new FileOutputStream(aDestinationFilePathname);
            // Parameter true: Close streams after operation
            return this.copyStream(tmpFileInputStream, tmpFileOutputStream, true);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Copies all non-existing files and sub-directories of specified directory
     * into destination directory
     *
     * @param aDirectorySourcePath Path of source directory
     * @param aDirectoryDestinationPath Path of destination directory
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyNonExistingFilesIntoDirectory(String aDirectorySourcePath, String aDirectoryDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectorySourcePath == null || aDirectorySourcePath.isEmpty() || aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectorySourcePath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        try {
            if (!ModelUtils.createDirectory(aDirectoryDestinationPath)) {
                return false;
            }
            File tmpSourceDirectory = new File(aDirectorySourcePath);
            for (File file : tmpSourceDirectory.listFiles()) {
                if (file.isDirectory()) {
                    if (!this.copyNonExistingFilesIntoDirectory(file.getAbsolutePath(), aDirectoryDestinationPath + File.separatorChar + file.getName())) {
                        return false;
                    }
                } else if (!this.copyNonExistingSingleFile(file.getAbsolutePath(), aDirectoryDestinationPath + File.separatorChar + file.getName())) {
                    return false;
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Copies all directories that start with specified prefix from source to
     * destination. If a directory with the same name already exists in
     * destination NOTHING is copied.
     *
     * @param aDirectorySourcePath Path of source directory
     * @param aPrefix Prefix of directories to be copied
     * @param aDirectoryDestinationPath Path of destination directory
     * @return Number of directories copied
     */
    public int copyPrefixDirectories(String aDirectorySourcePath, String aPrefix, String aDirectoryDestinationPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectorySourcePath == null || aDirectorySourcePath.isEmpty() || aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty() || aPrefix == null || aPrefix.isEmpty()) {
            return 0;
        }
        if (aDirectorySourcePath.equals(aDirectoryDestinationPath)) {
            // Source and destination are identical: Nothing to be copied
            return 0;
        }

        // </editor-fold>
        int tmpCounter = 0;
        try {
            String[] directoriesToBeCopied = this.getDirectoryPathsWithPrefix(aDirectorySourcePath, aPrefix);
            for (String tmpSingleDirectoryPath : directoriesToBeCopied) {
                String tmpSingleDirectoryDestinationPath = aDirectoryDestinationPath + File.separatorChar + (new File(tmpSingleDirectoryPath)).getName();
                if (!(new File(tmpSingleDirectoryDestinationPath)).isDirectory()) {
                    if (this.copyIntoDirectory(tmpSingleDirectoryPath, tmpSingleDirectoryDestinationPath)) {
                        tmpCounter++;
                    }
                }
            }
            return tmpCounter;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return tmpCounter;
        }
    }

    /**
     * Copies directory (and all sub-directories) to destination.
     *
     * @param aDirectoryPath Full path of directory
     * @param aDestinationPath Full path of destination directory the specified
     * directory is copied to
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyDirectory(String aDirectoryPath, String aDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aDestinationPath == null || aDestinationPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return false;
        }
        if (!(new File(aDestinationPath)).isDirectory()) {
            return false;
        }
        // </editor-fold>
        try {
            String tmpDirectoryDestinationPath = aDestinationPath + File.separatorChar + (new File(aDirectoryPath)).getName();
            if (!ModelUtils.createDirectory(tmpDirectoryDestinationPath)) {
                return false;
            }
            return this.copyIntoDirectory(aDirectoryPath, tmpDirectoryDestinationPath);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Copies source directory (and all sub-directories) to destination and
     * deletes source directory
     *
     * @param aDirectorySourcePath Path of source directory
     * @param aDestinationPath Path of destination directory
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyAndDeleteDirectory(String aDirectorySourcePath, String aDestinationPath) {
        if (!this.copyDirectory(aDirectorySourcePath, aDestinationPath)) {
            return false;
        }
        if (!this.deleteDirectory(aDirectorySourcePath)) {
            return false;
        }
        return true;
    }

    /**
     * Copies all files and sub-directories of specified source directory into
     * destination directory
     *
     * @param aDirectorySourcePath Path of source directory
     * @param aDirectoryDestinationPath Path of destination directory
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyIntoDirectory(String aDirectorySourcePath, String aDirectoryDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectorySourcePath == null || aDirectorySourcePath.isEmpty() || aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectorySourcePath)).isDirectory()) {
            return false;
        }
        // </editor-fold>
        try {
            if (!ModelUtils.createDirectory(aDirectoryDestinationPath)) {
                return false;
            }
            File tmpSourceDirectory = new File(aDirectorySourcePath);
            for (File file : tmpSourceDirectory.listFiles()) {
                if (file.isDirectory()) {
                    if (!this.copyIntoDirectory(file.getAbsolutePath(), aDirectoryDestinationPath + File.separatorChar + file.getName())) {
                        return false;
                    }
                } else if (!this.copySingleFile(file.getAbsolutePath(), aDirectoryDestinationPath + File.separatorChar + file.getName())) {
                    return false;
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Copies specified file to specified destination. NOTE: File will not be
     * copied if specified destination pathname already exists. Then true is
     * returned.
     *
     * @param aSourceFilePathname Full pathname of source (may be null or empty
     * then false is returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * or empty then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copySingleFile(String aSourceFilePathname, String aDestinationFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty() || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }
        if (aSourceFilePathname.equals(aDestinationFilePathname)) {
            // File is already in desired destination: Return true
            return true;
        }
        if (!(new File(aSourceFilePathname)).isFile()) {
            return false;
        }
        if ((new File(aDestinationFilePathname)).isFile()) {
            return false;
        }
        // </editor-fold>
        try {
            FileInputStream tmpFileInputStream = new FileInputStream(aSourceFilePathname);
            FileOutputStream tmpFileOutputStream = new FileOutputStream(aDestinationFilePathname);
            // Parameter true: Close streams after operation
            return this.copyStream(tmpFileInputStream, tmpFileOutputStream, true);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Copies single file to destination and deletes source file. Source file
     * may be renamed if filename of destination pathname is different from
     * filename of source pathname.
     *
     * @param aSourceFilePathname Pathname of source file
     * @param aDestinationFilePathname Pathname of destination file
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyAndDeleteSingleFile(String aSourceFilePathname, String aDestinationFilePathname) {
        if (!this.copySingleFile(aSourceFilePathname, aDestinationFilePathname)) {
            return false;
        }
        if (!this.deleteSingleFile(aSourceFilePathname)) {
            return false;
        }
        return true;
    }

    /**
     * Copies specified file to specified directory. NOTE: File will not be
     * copied if specified file in specified directory already exists. Then
     * false is returned.
     *
     * @param aSourceFilePathname Full pathname of source (may be null or empty
     * then false is returned)
     * @param aDirectoryDestinationPath Full path of destination directory (may be null or empty then
     * false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copySingleFileToDirectory(String aSourceFilePathname, String aDirectoryDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty() || aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty()) {
            return false;
        }
        if (!(new File(aSourceFilePathname)).isFile()) {
            return false;
        }
        if (!(new File(aDirectoryDestinationPath)).isDirectory()) {
            return false;
        }
        // </editor-fold>
        String tmpDestinationFilePathname = aDirectoryDestinationPath + File.separatorChar + (new File(aSourceFilePathname)).getName();
        if ((new File(tmpDestinationFilePathname)).isFile()) {
            return false;
        }
        try {
            FileInputStream tmpFileInputStream = new FileInputStream(aSourceFilePathname);
            FileOutputStream tmpFileOutputStream = new FileOutputStream(tmpDestinationFilePathname);
            // Parameter true: Close streams after operation
            return this.copyStream(tmpFileInputStream, tmpFileOutputStream, true);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Copies file in inputstream to outputstream
     *
     * @param anInputStream Input stream (may be null then false is returned)
     * @param anOutputStream File output stream (may be null then false is
     * returned)
     * @param aCloseStreamFlag true: Streams are closed after operation, false:
     * Streams are not closed after operation
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyStream(InputStream anInputStream, OutputStream anOutputStream, boolean aCloseStreamFlag) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anInputStream == null || anOutputStream == null) {
            return false;
        }

        // </editor-fold>
        try {
            int tmpLength;
            byte[] tmpBuffer = new byte[ModelDefinitions.BUFFER_SIZE];
            while ((tmpLength = anInputStream.read(tmpBuffer)) != -1) {
                anOutputStream.write(tmpBuffer, 0, tmpLength);
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (aCloseStreamFlag) {
                try {
                    anInputStream.close();
                    anOutputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return false;
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Create/get methods">
    /**
     * Creates directory and all non-existent ancestor directories if necessary
     *
     * @param aPathSpecification File with path specification
     * @return true: Directory already existed or was successfully created,
     * false: Otherwise
     */
    public boolean createDirectory(File aPathSpecification) {
        try {
            if (!aPathSpecification.isDirectory()) {
                return aPathSpecification.mkdirs();
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    
    /**
     * Creates a unique directory in destination path with specified 
     * date-time-ending.
     * NOTE: This method should NOT be called from parallelised threads due to 
     *       passed timestamp and small time difference between check of already 
     *       existing directory and creation of directory.
     *
     * @param aDirectoryDestinationPath Destination path for unique directory to
     * be created in
     * @param aDirectoryPrefix Prefix for unique directory name (ending is
     * date-time string)
     * @return Directory information or null if unique directory could not be
     * created
     */
    public DirectoryInformation createUniqueDirectoryWithDateTimeEnding(String aDirectoryDestinationPath, String aDirectoryPrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryDestinationPath == null 
            || aDirectoryDestinationPath.isEmpty() 
            || !(new File(aDirectoryDestinationPath)).isDirectory() 
            || aDirectoryPrefix == null 
            || aDirectoryPrefix.isEmpty()
        ) {
            ModelUtils.appendToLogfile(true, "UtilityFileMethods.createUniqueDirectoryWithDateTimeEnding(): Checks were not successful.");
            return null;
        }
        // </editor-fold>
        try {
            String tmpUniqueTimestampInStandardFormat = ModelUtils.getUniqueTimestampInStandardFormat();
            String tmpPathOfUniqueDirectory = 
                aDirectoryDestinationPath + 
                File.separatorChar + 
                aDirectoryPrefix + 
                this.timeUtilityMethods.convertTimestampInStandardFormatIntoDirectoryEnding(tmpUniqueTimestampInStandardFormat);
            if (!ModelUtils.createDirectory(tmpPathOfUniqueDirectory)) {
                ModelUtils.appendToLogfile(true, "UtilityFileMethods.createUniqueDirectoryWithDateTimeEnding(): Directory could not be created = " + tmpPathOfUniqueDirectory);
                return null;
            }
            return new DirectoryInformation(tmpPathOfUniqueDirectory, tmpUniqueTimestampInStandardFormat);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns full pathname of new unique temporary directory. NOTE: New unique
     * temporary directory is NOT created!
     *
     * @return Full pathname of new unique temporary directory
     */
    public String getUniqueTemporaryDirectoryPath() {
        return Preferences.getInstance().getTempPath() + File.separatorChar + "TEMP_" + this.stringUtilityMethods.getGloballyUniqueID();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Clear/delete methods">
    /**
     * Clears directory from all files and all sub directories
     *
     * @param aDirectoryPath Path of directory to be cleared (may be null or
     * empty then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean clearDirectory(String aDirectoryPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            // Directory does not exist
            return false;
        }

        // </editor-fold>
        try {
            return this.clearDirectory(new File(aDirectoryPath));
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Clears directory from all sub directories only (files are NOT changed)
     *
     * @param aDirectoryPath Path of directory to be cleared from all sub
     * directories only (may be null then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean clearDirectoryFromSubDirectories(String aDirectoryPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            // Directory does not exist
            return false;
        }

        // </editor-fold>
        try {
            return this.clearDirectoryFromSubDirectories(new File(aDirectoryPath));
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Clears directory from all files and all sub directories
     *
     * @param aDirectory Directory object to be cleared (may be null then false
     * is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean clearDirectory(File aDirectory) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectory == null) {
            return false;
        }
        if (!aDirectory.isDirectory()) {
            // Directory does not exist
            return false;
        }

        // </editor-fold>
        return this.deleteDefinedFilesAndDirectories(aDirectory.listFiles());
    }

    /**
     * Clears directory from all sub directories only (files are NOT changed)
     *
     * @param aDirectory Directory object to be cleared from all sub directories
     * only (may be null then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean clearDirectoryFromSubDirectories(File aDirectory) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectory == null) {
            return false;
        }
        if (!aDirectory.isDirectory()) {
            // Directory does not exist
            return false;
        }

        // </editor-fold>
        File[] tmpFiles = aDirectory.listFiles();
        LinkedList<File> tmpDirectoryList = new LinkedList<File>();
        for (File tmpSingleFile : tmpFiles) {
            if (tmpSingleFile.isDirectory()) {
                tmpDirectoryList.add(tmpSingleFile);
            }
        }
        if (tmpDirectoryList.size() > 0) {
            return this.deleteDefinedFilesAndDirectories(tmpDirectoryList.toArray(new File[0]));
        } else {
            return true;
        }

    }

    /**
     * Clears temporary directory
     *
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean clearTemporaryDirectory() {
        return this.clearDirectory(Preferences.getInstance().getTempPath());
    }

    /**
     * Deletes all files in specified directory that start with specified prefix
     *
     * @param aDirectoryPath Directory path
     * @param aPrefix Prefix
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean deleteAllFilesWithPrefix(String aDirectoryPath, String aPrefix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aPrefix == null || aPrefix.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        try {
            File[] tmpFileArray = new File(aDirectoryPath).listFiles();
            if (tmpFileArray != null && tmpFileArray.length > 0) {
                for (File tmpFile : tmpFileArray) {
                    if (tmpFile.isFile()) {
                        if (tmpFile.getName().startsWith(aPrefix)) {
                            if (!tmpFile.delete()) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Deletes all directories in specified path that start with specified
     * prefix
     *
     * @param aDirectoryPath Directory path
     * @param aPrefix Prefix
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean deleteAllDirectoriesWithPrefix(String aDirectoryPath, String aPrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aPrefix == null || aPrefix.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null && tmpFileArray.length > 0) {
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isDirectory()) {
                    if (tmpFile.getName().startsWith(aPrefix)) {
                        if (!this.deleteDirectory(tmpFile)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Deletes all directories in specified path (and all sub directories) with 
     * specified name
     *
     * @param aDirectoryPath Directory path
     * @param aName Name
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean deleteAllDirectoriesWithName(String aDirectoryPath, String aName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aName == null || aName.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return false;
        }
        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null && tmpFileArray.length > 0) {
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isDirectory()) {
                    if (tmpFile.getName().equals(aName)) {
                        if (!this.deleteDirectory(tmpFile)) {
                            return false;
                        }
                    } else {
                        if (!this.deleteAllDirectoriesWithName(tmpFile.getAbsolutePath(), aName)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Deletes defined files and directories
     *
     * @param aFilesAndDirectoriesForDeletion Array of defined files and
     * directories
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean deleteDefinedFilesAndDirectories(File[] aFilesAndDirectoriesForDeletion) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilesAndDirectoriesForDeletion == null || aFilesAndDirectoriesForDeletion.length == 0) {
            return true;
        }
        // </editor-fold>
        try {
            for (File file : aFilesAndDirectoriesForDeletion) {
                if (file.isDirectory()) {
                    if (!this.deleteDirectory(file)) {
                        return false;
                    }
                } else {
                    // Perform delay: This can be necessary on specific OS due to dependencies of directories/files to be deleted
                    try {
                        Thread.sleep(Preferences.getInstance().getDelayForFilesInMilliseconds());
                    } catch (InterruptedException anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // This should never happen!
                        return false;
                    }
                    if (!file.delete()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Deletes directory and all sub directories
     *
     * @param aDirectoryPath Path of directory to be deleted (may be null or
     * empty then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean deleteDirectory(String aDirectoryPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            // Directory does not exist, i.e. it is deleted: Return true
            return true;
        }

        // </editor-fold>
        try {
            return this.deleteDirectory(new File(aDirectoryPath));
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Deletes directory and all sub directories
     *
     * @param aDirectory Directory object to be deleted (may be null then false
     * is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean deleteDirectory(File aDirectory) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectory == null) {
            return false;
        }
        if (!aDirectory.isDirectory()) {
            // Directory does not exist, i.e. it is deleted: Return true
            return true;
        }

        // </editor-fold>
        try {
            if (!this.deleteDefinedFilesAndDirectories(aDirectory.listFiles())) {
                return false;
            }
            return aDirectory.delete();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Deletes single file
     *
     * @param aFilePathname Full pathname of file to be deleted (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean deleteSingleFile(String aFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilePathname == null || aFilePathname.isEmpty()) {
            return false;
        }
        // </editor-fold>
        try {
            File tmpFile = new File(aFilePathname);
            if (!tmpFile.isFile()) {
                return true;
            } else {
                return tmpFile.delete();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Deletes multiple files
     *
     * @param aFilePathnames Array with file pathnames to be deleted
     * @return True: All files of aFilePathnames were successfully deleted,
     * false: Otherwise
     */
    public boolean deleteMultipleFiles(String[] aFilePathnames) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilePathnames == null || aFilePathnames.length == 0) {
            return false;
        }

        // </editor-fold>
        boolean tmpIsSuccess = true;
        for (String tmpFilePathname : aFilePathnames) {
            if (!this.deleteSingleFile(tmpFilePathname)) {
                tmpIsSuccess = false;
            }
        }
        return tmpIsSuccess;

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Move methods">
    /**
     * Moves file to destination (source file will be deleted). Source file may
     * be renamed if filename of destination pathname is different from filename
     * of source pathname.
     *
     * @param aSourceFilePathname Pathname of source file
     * @param aDestinationFilePathname Pathname of destination file
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean moveSingleFileByRename(String aSourceFilePathname, String aDestinationFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty() || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }
        if (!(new File(aSourceFilePathname)).isFile()) {
            return false;
        }
        if ((new File(aDestinationFilePathname)).exists()) {
            return false;
        }

        // </editor-fold>
        File tmpSourceFile = new File(aSourceFilePathname);
        return tmpSourceFile.renameTo(new File(aDestinationFilePathname));
    }

    /**
     * Moves file to destination (source file will be deleted). Source file may
     * be renamed if filename of destination pathname is different from filename
     * of source pathname.
     *
     * @param aSourceFilePathname Pathname of source file
     * @param aDestinationFilePathname Pathname of destination file
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean moveSingleFile(String aSourceFilePathname, String aDestinationFilePathname) {
        if (!this.moveSingleFileByRename(aSourceFilePathname, aDestinationFilePathname)) {
            return this.copyAndDeleteSingleFile(aSourceFilePathname, aDestinationFilePathname);
        } else {
            return true;
        }
    }

    /**
     * Moves directory into destination directory (source directory will be
     * deleted)
     *
     * @param aDirectorySourcePath Path of source directory
     * @param aDirectoryDestinationPath Path of destination directory
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean moveToDirectoryByRename(String aDirectorySourcePath, String aDirectoryDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectorySourcePath == null || aDirectorySourcePath.isEmpty() || aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectorySourcePath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        if (!ModelUtils.createDirectory(aDirectoryDestinationPath)) {
            return false;
        }
        File tmpSourceDirectory = new File(aDirectorySourcePath);
        File tmpDestinationDirectory = new File(aDirectoryDestinationPath);
        return tmpSourceDirectory.renameTo(new File(tmpDestinationDirectory, tmpSourceDirectory.getName()));
    }

    /**
     * Moves source directory into destination directory 
     * (source directory will be deleted)
     *
     * @param aDirectorySourcePath Path of source directory
     * @param aDirectoryDestinationPath Path of destination directory
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean moveToDirectory(String aDirectorySourcePath, String aDirectoryDestinationPath) {
        if (!this.moveToDirectoryByRename(aDirectorySourcePath, aDirectoryDestinationPath)) {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (aDirectorySourcePath == null || aDirectorySourcePath.isEmpty() || aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty()) {
                return false;
            }
            if (!(new File(aDirectorySourcePath)).isDirectory()) {
                return false;
            }
            // </editor-fold>
            return this.copyAndDeleteDirectory(aDirectorySourcePath, aDirectoryDestinationPath);
        } else {
            return true;
        }
    }

    /**
     * Moves content of source directory into destination directory 
     * (source directory will be deleted)
     *
     * @param aDirectorySourcePath Path of source directory
     * @param aDirectoryDestinationPath Path of destination directory
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean moveContentToDirectoryAndDelete(String aDirectorySourcePath, String aDirectoryDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectorySourcePath == null || aDirectorySourcePath.isEmpty() || aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectorySourcePath)).isDirectory()) {
            return false;
        }
        // </editor-fold>
        try {
            if (!ModelUtils.createDirectory(aDirectoryDestinationPath)) {
                return false;
            }
            File tmpSourceDirectory = new File(aDirectorySourcePath);
            for (File file : tmpSourceDirectory.listFiles()) {
                if (file.isDirectory()) {
                    if (!this.moveContentToDirectoryAndDelete(file.getAbsolutePath(), aDirectoryDestinationPath + File.separatorChar + file.getName())) {
                        return false;
                    }
                } else if (!this.moveSingleFile(file.getAbsolutePath(), aDirectoryDestinationPath + File.separatorChar + file.getName())) {
                    return false;
                }
            }
            if (!this.deleteDirectory(aDirectorySourcePath)) {
                return false;
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Write stack trace of exception to log file">

            // </editor-fold>
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- File to byte array conversion">
    /**
     * Returns the contents of a file in a byte array.
     *
     * @param aFilePathname Full pathname of file
     * @return Byte array with contents of the file or null if byte array could
     * not be created
     */
    public byte[] getByteArrayFromFile(String aFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilePathname == null || aFilePathname.isEmpty()) {
            return null;
        }

        // </editor-fold>
        InputStream tmpInputStream = null;
        try {
            File tmpFile = new File(aFilePathname);
            if (!tmpFile.isFile()) {
                return null;
            }
            tmpInputStream = new FileInputStream(tmpFile);
            // Get the size of the file
            long tmpLengthOfFile = tmpFile.length();
            // You cannot create an array using a long type. It needs to be an int type. Before converting to an int type, check to ensure that file is not larger than
            // Integer.MAX_VALUE.
            if (tmpLengthOfFile > Integer.MAX_VALUE) {
                return null;
            }
            // Create the byte array to hold the data
            byte[] tmpByteArray = new byte[(int) tmpLengthOfFile];
            // Read in the bytes
            int tmpOffset = 0;
            int numberOfBytes = 0;
            while (tmpOffset < tmpByteArray.length && (numberOfBytes = tmpInputStream.read(tmpByteArray, tmpOffset, tmpByteArray.length - tmpOffset)) >= 0) {
                tmpOffset += numberOfBytes;
            }
            // Ensure all the bytes have been read in
            if (tmpOffset < tmpByteArray.length) {
                return null;
            }
            // Return bytes
            return tmpByteArray;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            try {
                if (tmpInputStream != null) {
                    tmpInputStream.close();
                }
            } catch (IOException anException) {
                ModelUtils.appendToLogfile(true, anException);
                return null;
            }
        }
    }

    /**
     * Writes byte array to destination file.
     *
     * @param aByteArray Byte array
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * or empty then false is returned)
     * @return True: Operation was successful, false: Otherwise.
     */
    public boolean writeByteArrayToFile(byte[] aByteArray, String aDestinationFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aByteArray == null || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }

        // </editor-fold>
        FileOutputStream tmpFileOutputStream = null;
        try {
            this.deleteSingleFile(aDestinationFilePathname);
            tmpFileOutputStream = new FileOutputStream(aDestinationFilePathname);
            tmpFileOutputStream.write(aByteArray);
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            try {
                if (tmpFileOutputStream != null) {
                    tmpFileOutputStream.close();
                }
            } catch (IOException anException) {
                ModelUtils.appendToLogfile(true, anException);
                return false;
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ZIP file related methods">
    /**
     * Extracts directory (with all files and sub directories) from ZIP file
     * that was created with Utility.copyDirectoryToZipFile()
     *
     * @param aPath Full path for directory to extract (must exist, otherwise
     * false is returned)
     * @param aZipFilePathname Full path and name of ZIP file (must exist,
     * otherwise false is returned)
     * @return True: Operation was successful, false: Otherwise
     */
    public boolean extractDirectoryFromZipFile(String aPath, String aZipFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty() || !(new File(aPath)).isDirectory()) {
            return false;
        }
        if (aZipFilePathname == null || aZipFilePathname.isEmpty() || !(new File(aZipFilePathname)).isFile()) {
            return false;
        }

        // </editor-fold>
        try {
            ZipFile tmpZipFile = new ZipFile(new File(aZipFilePathname));
            Enumeration<?> tmpEnumeration = tmpZipFile.entries();
            while (tmpEnumeration.hasMoreElements()) {
                ZipEntry tmpEntry = (ZipEntry) tmpEnumeration.nextElement();
                File tmpFile = new File(aPath, this.correctFileSeparatorCharacter(tmpEntry.getName()));
                if (tmpEntry.isDirectory() && !tmpFile.exists()) {
                    tmpFile.mkdirs();
                } else {
                    if (!tmpFile.getParentFile().exists()) {
                        tmpFile.getParentFile().mkdirs();
                    }
                    InputStream tmpZipInputStream = tmpZipFile.getInputStream(tmpEntry);
                    BufferedOutputStream tmpBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
                    byte[] tmpBuffer = new byte[ModelDefinitions.BUFFER_SIZE];
                    int tmpLength;
                    while ((tmpLength = tmpZipInputStream.read(tmpBuffer)) != -1) {
                        tmpBufferedOutputStream.write(tmpBuffer, 0, tmpLength);
                    }
                    tmpZipInputStream.close();
                    tmpBufferedOutputStream.close();
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Returns directory name from ZIP file that was created with
     * Utility.copyDirectoryToZipFile()
     *
     * @param aZipFilePathname Full path and name of ZIP file (must exist,
     * otherwise false is returned)
     * @return Directory name from ZIP file or null
     */
    public String getDirectoryNameFromZipFile(String aZipFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aZipFilePathname == null || aZipFilePathname.isEmpty() || !(new File(aZipFilePathname)).isFile()) {
            return null;
        }

        // </editor-fold>
        try {
            ZipFile tmpZipFile = new ZipFile(new File(aZipFilePathname));
            Enumeration<?> tmpEnumeration = tmpZipFile.entries();
            if (tmpEnumeration.hasMoreElements()) {
                ZipEntry tmpEntry = (ZipEntry) tmpEnumeration.nextElement();
                String tmpEntryName = this.correctFileSeparatorCharacter(tmpEntry.getName());
                int tmpIndex = tmpEntryName.indexOf(File.separatorChar);
                if (tmpIndex > 0) {
                    return tmpEntryName.substring(0, tmpIndex);
                } else {
                    return tmpEntryName;
                }
            } else {
                return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns (first occurrence of) single file from ZIP file that was created
     * with Utility.copyDirectoryToZipFile()
     *
     * @param aZipFilePathname Full path and name of ZIP file (must exist,
     * otherwise false is returned)
     * @param aSingleFilename File name (without path) of single file
     * @param aDestinationPathname Full path and name for destination of the
     * single file (not allowed to already exist: If file already exists false
     * is returned)
     * @return True: Operation was successful, false: Otherwise
     */
    public boolean getSingleFileFromZipFile(String aZipFilePathname, String aSingleFilename, String aDestinationPathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aZipFilePathname == null || aZipFilePathname.isEmpty() || !(new File(aZipFilePathname)).isFile()) {
            return false;
        }
        if (aSingleFilename == null || aSingleFilename.isEmpty()) {
            return false;
        }
        if (aDestinationPathname == null || aDestinationPathname.isEmpty() || (new File(aDestinationPathname)).isFile()) {
            return false;
        }

        // </editor-fold>
        try {
            ZipInputStream tmpZipInputStream = new ZipInputStream(new FileInputStream(aZipFilePathname));
            ZipEntry tmpEntry = tmpZipInputStream.getNextEntry();
            boolean tmpIsExtracted = false;
            while (tmpEntry != null) {
                if (tmpEntry.getName().toLowerCase(Locale.ENGLISH).endsWith(aSingleFilename.toLowerCase(Locale.ENGLISH))) {
                    BufferedOutputStream tmpBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(aDestinationPathname)));
                    byte[] tmpBuffer = new byte[ModelDefinitions.BUFFER_SIZE];
                    int tmpLength;
                    while ((tmpLength = tmpZipInputStream.read(tmpBuffer)) != -1) {
                        tmpBufferedOutputStream.write(tmpBuffer, 0, tmpLength);
                    }
                    tmpBufferedOutputStream.close();
                    tmpIsExtracted = true;
                    break;
                }
                tmpEntry = tmpZipInputStream.getNextEntry();
            }
            tmpZipInputStream.close();
            return tmpIsExtracted;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Returns single directory with specified prefix from ZIP file that was
     * created with Utility.copyDirectoryToZipFile(). NOTE: The directory prefix
     * must be unique in all file pathnames of ZIP file.
     *
     * @param aZipFilePathname Full path and name of ZIP file (must exist,
     * otherwise false is returned)
     * @param aDirectoryPrefix name (without path) of single file
     * @param aDestinationPath Full path for destination of single directory
     * (not allowed to already exist: If directory already exists false is
     * returned)
     * @return True: Operation was successful, false: Otherwise
     */
    public boolean getSingleDirectoryFromZipFile(String aZipFilePathname, String aDirectoryPrefix, String aDestinationPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aZipFilePathname == null || aZipFilePathname.isEmpty() || !(new File(aZipFilePathname)).isFile()) {
            return false;
        }
        if (aDirectoryPrefix == null || aDirectoryPrefix.isEmpty()) {
            return false;
        }
        if (aDestinationPath == null || aDestinationPath.isEmpty() || !(new File(aDestinationPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        try {
            ZipInputStream tmpZipInputStream = new ZipInputStream(new FileInputStream(aZipFilePathname));
            ZipEntry tmpEntry = tmpZipInputStream.getNextEntry();
            boolean tmpIsExtracted = false;
            while (tmpEntry != null) {
                String tmpEntryName = this.correctFileSeparatorCharacter(tmpEntry.getName());
                if (!tmpEntry.isDirectory() && tmpEntryName.contains(aDirectoryPrefix)) {
                    String tmpSubFilePathname = tmpEntryName.substring(tmpEntryName.indexOf(aDirectoryPrefix));
                    String tmpFinalDestinationFilePathname = aDestinationPath + File.separatorChar + tmpSubFilePathname;
                    File tmpFile = new File(tmpFinalDestinationFilePathname);
                    if (!tmpFile.getParentFile().exists()) {
                        tmpFile.getParentFile().mkdirs();
                    }
                    BufferedOutputStream tmpBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
                    byte[] tmpBuffer = new byte[ModelDefinitions.BUFFER_SIZE];
                    int tmpLength;
                    while ((tmpLength = tmpZipInputStream.read(tmpBuffer)) != -1) {
                        tmpBufferedOutputStream.write(tmpBuffer, 0, tmpLength);
                    }
                    tmpBufferedOutputStream.close();
                    tmpIsExtracted = true;
                }
                tmpEntry = tmpZipInputStream.getNextEntry();
            }
            tmpZipInputStream.close();
            return tmpIsExtracted;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Returns single directory name with specified prefix from ZIP file that
     * was created with Utility.copyDirectoryToZipFile(). NOTE: The directory
     * prefix must be unique in all file pathnames of ZIP file.
     *
     * @param aZipFilePathname Full path and name of ZIP file (must exist,
     * otherwise false is returned)
     * @param aDirectoryPrefix name (without path) of single file
     * @return Full directory name that corresponds to specified prefix or null
     * if prefix could not be found
     */
    public String getSingleDirectoryNameFromZipFile(String aZipFilePathname, String aDirectoryPrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aZipFilePathname == null || aZipFilePathname.isEmpty() || !(new File(aZipFilePathname)).isFile()) {
            return null;
        }
        if (aDirectoryPrefix == null || aDirectoryPrefix.isEmpty()) {
            return null;
        }

        // </editor-fold>
        try {
            ZipInputStream tmpZipInputStream = new ZipInputStream(new FileInputStream(aZipFilePathname));
            ZipEntry tmpEntry = tmpZipInputStream.getNextEntry();
            String tmpFullDirectoryName = null;
            while (tmpEntry != null) {
                String tmpEntryName = this.correctFileSeparatorCharacter(tmpEntry.getName());
                if (tmpEntryName.contains(aDirectoryPrefix)) {
                    String tmpSubFilePathname = tmpEntryName.substring(tmpEntryName.indexOf(aDirectoryPrefix));
                    int tmpIndex = tmpSubFilePathname.indexOf(File.separatorChar);
                    if (tmpIndex > 0) {
                        tmpFullDirectoryName = tmpSubFilePathname.substring(0, tmpIndex);
                    } else {
                        tmpFullDirectoryName = tmpSubFilePathname;
                    }
                    break;
                }
                tmpEntry = tmpZipInputStream.getNextEntry();
            }
            tmpZipInputStream.close();
            return tmpFullDirectoryName;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Copies directory to ZIP file
     *
     * @param aDirectoryPath Full path of directory (must exist, otherwise false
     * is returned)
     * @param aZipFilePathname Full path and name of ZIP file (is NOT allowed to
     * already exist, otherwise false is returned)
     *
     * @return True: Operation was successful, false: Otherwise
     */
    public boolean copyDirectoryToZipFile(String aDirectoryPath, String aZipFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || !(new File(aDirectoryPath)).isDirectory()) {
            return false;
        }
        if (aZipFilePathname == null || aZipFilePathname.isEmpty() || (new File(aZipFilePathname)).isFile()) {
            return false;
        }

        // </editor-fold>
        try {
            ZipOutputStream tmpZipOutputStream = new ZipOutputStream(new FileOutputStream(aZipFilePathname));
            File tmpDirectory = new File(aDirectoryPath);
            boolean tmpIsSuccessful = this.copyDirectoryToZipOutputStream(tmpDirectory, tmpDirectory, tmpZipOutputStream);
            tmpZipOutputStream.close();
            return tmpIsSuccessful;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
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

                if (tmpFiles[i].isDirectory()) {
                    if (!this.copyDirectoryToZipOutputStream(tmpFiles[i], aBaseDirectory, aZipOutputStream)) {
                        return false;
                    }
                } else {
                    int tmpLength;
                    FileInputStream tmpFileInputStream = new FileInputStream(tmpFiles[i]);
                    ZipEntry tmpEntry = new ZipEntry(tmpFiles[i].getPath().substring(aBaseDirectory.getParent().length() + 1));
                    tmpEntry.setTime(tmpFiles[i].lastModified());
                    aZipOutputStream.putNextEntry(tmpEntry);
                    while ((tmpLength = tmpFileInputStream.read(tmpBuffer)) != -1) {
                        aZipOutputStream.write(tmpBuffer, 0, tmpLength);
                    }
                    tmpFileInputStream.close();
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- String array: Read from/write to file methods">
    /**
     * Reads string array from file
     *
     * @param aSourceFilePathname Full pathname of source (may be null/empty
     * then null is returned)
     * @param aCommentLinePrefix Prefix of comment line (may be null/empty)
     * @return String array or null if string array could not be read
     */
    public String[] readStringArrayFromFile(String aSourceFilePathname, String aCommentLinePrefix) {
        LinkedList<String> tmpStringList = this.readStringListFromFile(aSourceFilePathname, aCommentLinePrefix);
        if (tmpStringList == null) {
            return null;
        } else {
            return tmpStringList.toArray(new String[0]);
        }
    }

    /**
     * Reads string array from specified section of file
     *
     * @param aSourceFilePathname Full pathname of source (may be null then null
     * is returned)
     * @param aCommentLinePrefix Prefix of comment line to ignore (may be
     * null/empty)
     * @param aSectionTag Section tag (if null/empty then null is returned)
     * @return String array from specified section of file or null if string
     * array could not be read
     */
    public String[] readStringArrayPartFromFile(String aSourceFilePathname, String aCommentLinePrefix, String aSectionTag) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty() || !(new File(aSourceFilePathname)).isFile()) {
            return null;
        }
        if (aSectionTag == null || aSectionTag.isEmpty()) {
            return null;
        }

        // </editor-fold>
        BufferedReader tmpBufferedReader = null;
        try {
            FileReader tmpFileReader = new FileReader(aSourceFilePathname);
            tmpBufferedReader = new BufferedReader(tmpFileReader, ModelDefinitions.BUFFER_SIZE);
            LinkedList<String> tmpLinkedList = new LinkedList<String>();
            String tmpLine;
            boolean tmpIsStarted = false;
            String tmpStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, aSectionTag);
            String tmpEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, aSectionTag);
            if (aCommentLinePrefix == null || aCommentLinePrefix.isEmpty()) {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    if (tmpIsStarted) {
                        if (tmpLine.trim().equalsIgnoreCase(tmpEndLine)) {
                            break;
                        }
                        tmpLinkedList.add(tmpLine);
                    } else {
                        tmpIsStarted = tmpLine.trim().equalsIgnoreCase(tmpStartLine);
                    }
                }
            } else {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    if (tmpIsStarted) {
                        if (tmpLine.trim().equalsIgnoreCase(tmpEndLine)) {
                            break;
                        }
                        if (!tmpLine.startsWith(aCommentLinePrefix)) {
                            tmpLinkedList.add(tmpLine);
                        }
                    } else {
                        tmpIsStarted = tmpLine.trim().equalsIgnoreCase(tmpStartLine);
                    }
                }
            }
            return tmpLinkedList.toArray(new String[0]);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }

    /**
     * Reads string array from file. NOTE: First line must be size of string
     * array.
     *
     * @param aSourceFilePathname Full pathname of source (may be null then null
     * is returned)
     * @return String array or null if string array could not be read
     */
    public String[] readDefinedStringArrayFromFile(String aSourceFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty()) {
            return null;
        }

        // </editor-fold>
        BufferedReader tmpBufferedReader = null;
        try {
            if (!(new File(aSourceFilePathname)).isFile()) {
                return null;
            }
            FileReader tmpFileReader = new FileReader(aSourceFilePathname);
            tmpBufferedReader = new BufferedReader(tmpFileReader, ModelDefinitions.BUFFER_SIZE);
            int tmpSize = Integer.valueOf(tmpBufferedReader.readLine());
            String[] tmpStringArray = new String[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpStringArray[i] = tmpBufferedReader.readLine();
            }
            return tmpStringArray;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }

    /**
     * Writes string array to file. NOTE: If destination pathname already exists
     * nothing is done.
     *
     * @param aStringArray String array (may be null then false is returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean writeStringArrayToFile(String[] aStringArray, String aDestinationFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0 || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }

        // </editor-fold>
        PrintWriter tmpPrintWriter = null;
        try {
            if ((new File(aDestinationFilePathname)).isFile()) {
                return false;
            }
            FileWriter tmpFileWriter = new FileWriter(aDestinationFilePathname);
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter, ModelDefinitions.BUFFER_SIZE);
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
            for (int i = 0; i < aStringArray.length; i++) {
                if (aStringArray[i] != null) {
                    tmpPrintWriter.println(aStringArray[i]);
                } else {
                    // If string is null then write empty string to guarantee
                    // same dimension of string array in read operation
                    tmpPrintWriter.println("");
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpPrintWriter != null) {
                tmpPrintWriter.close();
            }
        }
    }

    /**
     * Writes string array to file. First line is the number of lines. NOTE: If
     * destination pathname already exists nothing is done.
     *
     * @param aStringArray String array (may be null then false is returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean writeDefinedStringArrayToFile(String[] aStringArray, String aDestinationFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0 || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }

        // </editor-fold>
        PrintWriter tmpPrintWriter = null;
        try {
            if ((new File(aDestinationFilePathname)).isFile()) {
                return false;
            }
            FileWriter tmpFileWriter = new FileWriter(aDestinationFilePathname);
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter, ModelDefinitions.BUFFER_SIZE);
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
            // Write number of lines
            tmpPrintWriter.println(String.valueOf(aStringArray.length));
            for (int i = 0; i < aStringArray.length; i++) {
                if (aStringArray[i] != null) {
                    tmpPrintWriter.println(aStringArray[i]);
                } else {
                    // If string is null then write empty string to guarantee
                    // same dimension of string array in read operation
                    tmpPrintWriter.println("");
                }
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpPrintWriter != null) {
                tmpPrintWriter.close();
            }
        }
    }

    /**
     * Reads jagged string array from file. Each line is splitted after one or
     * more whitespace characters.
     *
     * @param aSourceFilePathname Full pathname of source (may be null then null
     * is returned)
     * @param aCommentLinePrefix Prefix of comment line (may be null/empty)
     * @return Jagged string array or null if jagged string array could not be
     * read
     */
    public String[][] readJaggedStringArrayFromFile(String aSourceFilePathname, String aCommentLinePrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty()) {
            return null;
        }

        // </editor-fold>
        BufferedReader tmpBufferedReader = null;
        try {
            if (!(new File(aSourceFilePathname)).isFile()) {
                return null;
            }
            FileReader tmpFileReader = new FileReader(aSourceFilePathname);
            tmpBufferedReader = new BufferedReader(tmpFileReader, ModelDefinitions.BUFFER_SIZE);
            LinkedList<String[]> tmpLinkedList = new LinkedList<String[]>();
            String tmpLine;
            if (aCommentLinePrefix == null || aCommentLinePrefix.isEmpty()) {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpLine.trim());
                    if (tmpItems != null) {
                        tmpLinkedList.add(tmpItems);
                    }
                }
            } else {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    if (!tmpLine.startsWith(aCommentLinePrefix)) {
                        String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpLine.trim());
                        if (tmpItems != null) {
                            tmpLinkedList.add(tmpItems);
                        }
                    }
                }
            }
            return tmpLinkedList.toArray(new String[0][]);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }

    /**
     * Reads jagged string array from specified section of file. Each line is
     * splitted after one or more whitespace characters.
     *
     * @param aSourceFilePathname Full pathname of source (may be null then null
     * is returned)
     * @param aCommentLinePrefix Prefix of comment line to ignore (may be
     * null/empty)
     * @param aSectionTag Section tag (if null/empty then null is returned)
     * @return Jagged string array from specified section of file or null if
     * jagged string array could not be read
     */
    public String[][] readJaggedStringArrayPartFromFile(String aSourceFilePathname, String aCommentLinePrefix, String aSectionTag) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty() || !(new File(aSourceFilePathname)).isFile()) {
            return null;
        }
        if (aSectionTag == null || aSectionTag.isEmpty()) {
            return null;
        }

        // </editor-fold>
        BufferedReader tmpBufferedReader = null;
        try {
            FileReader tmpFileReader = new FileReader(aSourceFilePathname);
            tmpBufferedReader = new BufferedReader(tmpFileReader, ModelDefinitions.BUFFER_SIZE);
            LinkedList<String[]> tmpLinkedList = new LinkedList<String[]>();
            String tmpLine;
            boolean tmpIsStarted = false;
            String tmpStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, aSectionTag);
            String tmpEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, aSectionTag);
            if (aCommentLinePrefix == null || aCommentLinePrefix.isEmpty()) {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    if (tmpIsStarted) {
                        if (tmpLine.trim().equalsIgnoreCase(tmpEndLine)) {
                            break;
                        }
                        String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpLine.trim());
                        if (tmpItems != null) {
                            tmpLinkedList.add(tmpItems);
                        }
                    } else {
                        tmpIsStarted = tmpLine.trim().equalsIgnoreCase(tmpStartLine);
                    }
                }
            } else {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    if (tmpIsStarted) {
                        if (tmpLine.trim().equalsIgnoreCase(tmpEndLine)) {
                            break;
                        }
                        if (!tmpLine.startsWith(aCommentLinePrefix)) {
                            String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpLine.trim());
                            if (tmpItems != null) {
                                tmpLinkedList.add(tmpItems);
                            }
                        }
                    } else {
                        tmpIsStarted = tmpLine.trim().equalsIgnoreCase(tmpStartLine);
                    }
                }
            }
            if (tmpLinkedList.size() > 0) {
                return tmpLinkedList.toArray(new String[0][]);
            } else {
                return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- String list: Read from/write/append to file methods">
    /**
     * Reads string list from file
     *
     * @param aSourceFilePathname Full pathname of source (may be null/empty
     * then null is returned)
     * @param aCommentLinePrefix Prefix of comment line (may be null/empty)
     * @return String list or null if string array could not be read
     */
    public LinkedList<String> readStringListFromFile(String aSourceFilePathname, String aCommentLinePrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty()) {
            return null;
        }

        // </editor-fold>
        BufferedReader tmpBufferedReader = null;
        try {
            if (!(new File(aSourceFilePathname)).isFile()) {
                return null;
            }
            FileReader tmpFileReader = new FileReader(aSourceFilePathname);
            tmpBufferedReader = new BufferedReader(tmpFileReader, ModelDefinitions.BUFFER_SIZE);
            LinkedList<String> tmpLinkedList = new LinkedList<String>();
            String tmpLine;
            if (aCommentLinePrefix == null || aCommentLinePrefix.isEmpty()) {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    tmpLinkedList.add(tmpLine);
                }
            } else {
                while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                    if (!tmpLine.startsWith(aCommentLinePrefix)) {
                        tmpLinkedList.add(tmpLine);
                    }
                }
            }
            return tmpLinkedList;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Resource related methods">

    /**
     * Returns a resource input stream
     *
     * @param aClass A class with a resource relative to it (may be null then
     * null is returned)
     * @param aResourceName Resource name relative to class (may be null or
     * empty then null is returned)
     * @return Resource input stream or null if none were found
     */
    public InputStream getResourceInputStream(Class<?> aClass, String aResourceName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aClass == null || aResourceName == null || aResourceName.isEmpty()) {
            return null;
        }
        // </editor-fold>
        try {
            return aClass.getResourceAsStream(aResourceName);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Copies resource to destination if destination does not already exist.
     *
     * @param aClass A class with a resource relative to it (may be null then
     * false is returned)
     * @param aResourceName Resource name relative to class (may be null or
     * empty then false is returned)
     * @param aDestinationFilePathname Pathname for destination (may be null or
     * empty then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyResourceToDestination(Class<?> aClass, String aResourceName, String aDestinationFilePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aClass == null || aResourceName == null || aResourceName.isEmpty() || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }

        // </editor-fold>
        // Copy resource to destination
        InputStream tmpInputStream = null;
        FileOutputStream tmpFileOutputStream = null;
        try {
            if (!(new File(aDestinationFilePathname)).isFile()) {
                tmpFileOutputStream = new FileOutputStream(aDestinationFilePathname);
                tmpInputStream = this.getResourceInputStream(aClass, aResourceName);
                if (tmpInputStream == null) {
                    return false;
                } else {
                    // Parameter true: Close streams
                    return (new FileUtilityMethods()).copyStream(tmpInputStream, tmpFileOutputStream, true);
                }
            } else {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return false;
                }
            }
            if (tmpFileOutputStream != null) {
                try {
                    tmpFileOutputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return false;
                }
            }
        }
    }

    /**
     * Checks if defined resource exists
     *
     * @param aClass A class with a resource relative to it (may be null then
     * false is returned)
     * @param aResourceName Resource name relative to class (may be null or
     * empty then false is returned)
     * @return true: Resource exists, false: Otherwise
     */
    public boolean hasResource(Class<?> aClass, String aResourceName) {
        // Checks are performed in Utility.getResourceInputStream()
        InputStream tmpInputStream = null;
        try {
            tmpInputStream = this.getResourceInputStream(aClass, aResourceName);
            return tmpInputStream != null;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return false;
                }
            }
        }
    }

    /**
     * Copies encrypted resource to destination if destination does not already
     * exist.
     *
     * @param aClass A class with a resource relative to it (may be null then
     * false is returned)
     * @param aResourceName Resource name relative to class (may be null or
     * empty then false is returned)
     * @param aDestinationFilePathname Pathname for destination (may be null or
     * empty then false is returned)
     * @param aKeyByteArray Byte array with key
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean copyEncryptedResourceToDestination(Class<?> aClass, String aResourceName, String aDestinationFilePathname, byte[] aKeyByteArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aClass == null || aResourceName == null || aResourceName.isEmpty() || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty() || aKeyByteArray == null
                || aKeyByteArray.length == 0) {
            return false;
        }

        // </editor-fold>
        byte[] tmpEncryptedResourceByteArray = this.getResourceByteArray(aClass, aResourceName);
        if (tmpEncryptedResourceByteArray == null) {
            return false;
        }
        // Resource is encrypted so a second call to the encryption method reverses encryption
        byte[] tmpResourceByteArray = this.miscUtilityMethods.encrypt(tmpEncryptedResourceByteArray, aKeyByteArray);
        if (tmpResourceByteArray == null) {
            return false;
        }
        return this.writeByteArrayToFile(tmpResourceByteArray, aDestinationFilePathname);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Single string to file: Read/write methods">
    /**
     * Writes single string to file. NOTE: If destination file pathname already
     * exists nothing is done.
     *
     * @param aString String (may be null/empty then false is returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public boolean writeSingleStringToTextFile(String aString, String aDestinationFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty() || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }

        // </editor-fold>
        PrintWriter tmpPrintWriter = null;
        try {
            if ((new File(aDestinationFilePathname)).isFile()) {
                return false;
            }
            FileWriter tmpFileWriter = new FileWriter(aDestinationFilePathname);
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter, ModelDefinitions.BUFFER_SIZE);
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
            tmpPrintWriter.print(aString);
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpPrintWriter != null) {
                tmpPrintWriter.close();
            }
        }
    }

    /**
     * Reads a text file into a single string
     *
     * @param aSourcePathname Full pathname of the text file to be read
     * @return The string representation of the content of the text file or null
     * if nothing could be read
     */
    public String readTextFileIntoSingleString(String aSourcePathname) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourcePathname == null || aSourcePathname.isEmpty()) {
            return null;
        }
        if (!(new File(aSourcePathname)).isFile()) {
            return null;
        }

        // </editor-fold>
        BufferedReader tmpBufferedReader = null;
        try {
            FileReader tmpFileReader = new FileReader(aSourcePathname);
            tmpBufferedReader = new BufferedReader(tmpFileReader, ModelDefinitions.BUFFER_SIZE);
            StringBuilder tmpStringBuilder = new StringBuilder(ModelDefinitions.BUFFER_SIZE);
            String tmpSeparator = ModelDefinitions.LINE_SEPARATOR;
            String tmpLine;
            while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                if (tmpStringBuilder.length() == 0) {
                    tmpStringBuilder.append(tmpLine);
                } else {
                    tmpStringBuilder.append(tmpSeparator);
                    tmpStringBuilder.append(tmpLine);
                }
            }
            return tmpStringBuilder.toString();
        } catch (IOException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns full directory paths in specified directory path that start with
     * specified prefix
     *
     * @param aDirectoryPath Path
     * @param aPrefix Prefix
     * @return Full directory paths in specified path that start with specified
     * prefix or null if none are found
     */
    public String[] getDirectoryPathsWithPrefix(String aDirectoryPath, String aPrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aPrefix == null || aPrefix.isEmpty()) {
            return null;
        }
        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            LinkedList<String> directoryPathList = new LinkedList<String>();
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isDirectory()) {
                    if (tmpFile.getName().startsWith(aPrefix)) {
                        directoryPathList.addLast(tmpFile.getAbsolutePath());
                    }
                }
            }
            if (directoryPathList.size() > 0) {
                return directoryPathList.toArray(new String[0]);
            }
        }
        return null;
    }

    /**
     * Returns if filename in specified directory path exists that starts with
     * specified prefix
     *
     * @param aDirectoryPath Path
     * @param aFilePrefix File prefix
     * @return True: Filename exists in specified directory path that starts
     * with specified prefix, false: Otherwise
     */
    public boolean hasFilenameWithPrefix(String aDirectoryPath, String aFilePrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aFilePrefix == null || aFilePrefix.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isFile()) {
                    if (tmpFile.getName().startsWith(aFilePrefix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fills filenames in specified directory path that start with specified
     * prefix into aFilenameList
     *
     * @param aFilenameList List with filenames
     * @param aDirectoryPath Path
     * @param aFilePrefix File prefix
     */
    public void fillFilenamesWithPrefix(LinkedList<String> aFilenameList, String aDirectoryPath, String aFilePrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilenameList == null) {
            return;
        }
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aFilePrefix == null || aFilePrefix.isEmpty()) {
            return;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isFile()) {
                    if (tmpFile.getName().startsWith(aFilePrefix)) {
                        if (!aFilenameList.contains(tmpFile.getName())) {
                            aFilenameList.addLast(tmpFile.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns filenames in specified directory path that start with specified
     * prefix
     *
     * @param aDirectoryPath Path
     * @param aFilePrefix File prefix
     * @return Filenames in specified path that start with specified prefix or
     * null if none are found
     */
    public String[] getFilenamesWithPrefix(String aDirectoryPath, String aFilePrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aFilePrefix == null || aFilePrefix.isEmpty()) {
            return null;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return null;
        }
        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            LinkedList<String> tmpFileList = new LinkedList<String>();
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isFile()) {
                    if (tmpFile.getName().startsWith(aFilePrefix)) {
                        tmpFileList.addLast(tmpFile.getName());
                    }
                }
            }
            if (tmpFileList.size() > 0) {
                return tmpFileList.toArray(new String[0]);
            }
        }
        return null;
    }

    /**
     * Returns filenames in specified directory path that start with zeros
     * number string
     *
     * @param aDirectoryPath Path
     * @return Filenames in specified path that that start with zeros number
     * string or null if none are found
     */
    public String[] getFilenamesWithInitialZerosNumberString(String aDirectoryPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty()) {
            return null;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return null;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            LinkedList<String> tmpFileList = new LinkedList<String>();
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isFile()) {
                    if (ModelDefinitions.INITIAL_ZEROS_NUMBER_STRING_FILENAME_PATTERN.matcher(tmpFile.getName()).matches()) {
                        tmpFileList.addLast(tmpFile.getName());
                    }
                }
            }
            if (tmpFileList.size() > 0) {
                return tmpFileList.toArray(new String[0]);
            }
        }
        return null;
    }

    /**
     * Returns full file pathnames in specified directory path that start with
     * specified prefix
     *
     * @param aDirectoryPath Path
     * @param aFilePrefix File prefix
     * @return Full file pathnames in specified path that start with specified
     * prefix or null if none are found
     */
    public String[] getFilePathnamesWithPrefix(String aDirectoryPath, String aFilePrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aFilePrefix == null || aFilePrefix.isEmpty()) {
            return null;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return null;
        }
        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            LinkedList<String> fileList = new LinkedList<String>();
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isFile()) {
                    if (tmpFile.getName().startsWith(aFilePrefix)) {
                        fileList.addLast(tmpFile.getAbsolutePath());
                    }
                }
            }
            if (fileList.size() > 0) {
                return fileList.toArray(new String[0]);
            }
        }
        return null;
    }

    /**
     * Returns sorted (ascending) file pathnames of all files in specified
     * directory path
     *
     * @param aDirectoryPath Path
     * @return Sorted (ascending) file pathnames of all files in specified path
     * or null if none are found
     */
    public String[] getSortedFilePathnames(String aDirectoryPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty()) {
            return null;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return null;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            LinkedList<String> fileList = new LinkedList<String>();
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isFile()) {
                    fileList.addLast(tmpFile.getAbsolutePath());
                }
            }
            if (fileList.size() > 0) {
                Collections.sort(fileList);
                return fileList.toArray(new String[0]);
            }
        }
        return null;
    }

    /**
     * Returns filename without extension, e.g. "Test.txt" returns "Test"
     *
     * @param aFilename Filename with extension
     * @return Filename without extension or aFilename if aFilename does not
     * have an extension
     */
    public String getFilenameWithoutExtension(String aFilename) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilename == null || aFilename.isEmpty()) {
            return aFilename;
        }

        // </editor-fold>
        int tmpLastPointIndex = aFilename.lastIndexOf(".");
        if (tmpLastPointIndex <= 0) {
            return aFilename;
        } else {
            return aFilename.substring(0, tmpLastPointIndex);
        }
    }

    /**
     * Returns extension of filename, e.g. "Test.txt" returns ".txt"
     *
     * @param aFilename Filename with extension
     * @return Extension of filename or null/empty string if filename does not
     * have an extension (if filename was null then null is returned, if
     * filename was empty than empty string is returned)
     */
    public String getExtensionOfFilename(String aFilename) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilename == null || aFilename.isEmpty()) {
            return aFilename;
        }

        // </editor-fold>
        int tmpLastPointIndex = aFilename.lastIndexOf(".");
        if (tmpLastPointIndex <= 0) {
            return "";
        } else {
            return aFilename.substring(tmpLastPointIndex);
        }
    }

    /**
     * Returns incremented filename, e.g. filename = "Test.txt" and anIncrement
     * = 3 returns "Test_3.txt"
     *
     * @param aFilename File name
     * @param anIncrement Increment
     * @return Incremented filename or null if none could be created
     */
    public String getIncrementedFilename(String aFilename, int anIncrement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilename == null || aFilename.isEmpty()) {
            return null;
        }

        // </editor-fold>
        try {
            String tmpFilenameWithoutExtension = this.getFilenameWithoutExtension(aFilename);
            String tmpIncrementString = "_" + String.valueOf(anIncrement);
            String tmpIncrementedFilename = tmpFilenameWithoutExtension + tmpIncrementString;
            String tmpExtension = this.getExtensionOfFilename(aFilename);
            if (tmpExtension == null || tmpExtension.isEmpty()) {
                return tmpIncrementedFilename;
            } else {
                return tmpIncrementedFilename + tmpExtension;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns maximum image number of image directory path for movies
     *
     * @param anImageDirectoryPathForMovies Full image directory path for movies
     * @return Maximum image number of movie directory or -1 if none could be
     * detected
     */
    public int getMaximumImageNumberOfImageDirectoryForMovies(String anImageDirectoryPathForMovies) {
        if (anImageDirectoryPathForMovies == null || anImageDirectoryPathForMovies.isEmpty() || !(new File(anImageDirectoryPathForMovies)).isDirectory()) {
            return -1;
        }
        String[] tmpFileNameArray = this.getFilenamesWithInitialZerosNumberString(anImageDirectoryPathForMovies);
        if (tmpFileNameArray == null) {
            return -1;
        }
        int tmpMaxImageNumber = -1;
        for (String tmpFileName : tmpFileNameArray) {
            Matcher tmpMatcher = ModelDefinitions.INITIAL_ZEROS_NUMBER_STRING_FILENAME_PATTERN.matcher(tmpFileName);
            if (tmpMatcher.matches()) {
                int tmpImageNumber = this.stringUtilityMethods.getNumberfromNumberStringWithInitialZeros(tmpMatcher.group(1));
                tmpMaxImageNumber = Math.max(tmpImageNumber, tmpMaxImageNumber);
            }
        }
        return tmpMaxImageNumber;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Corrects ZIP entries: Replaces file separator character with the one used on current platform OS. NOTE: This correction is performed for UNIX (slash '/') and Windows (back-slash '\') ONLY.
     *
     * @param aStringWithFileAndPathnames String with file and path names @return Corrected string
     */
    private String correctFileSeparatorCharacter(String aStringWithFileAndPathnames) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringWithFileAndPathnames == null || aStringWithFileAndPathnames.isEmpty()) {
            return aStringWithFileAndPathnames;
        }
        // </editor-fold>
        return ModelDefinitions.FILE_SEPARATOR_CHARACTER_PATTERN.matcher(aStringWithFileAndPathnames).replaceAll(Matcher.quoteReplacement(String.valueOf(File.separatorChar)));
    }

    /**
     * Returns resource in form of a byte array
     *
     * @param aClass A class with a resource relative to it (may be null then
     * null is returned)
     * @param aResourceName Resource name relative to class (may be null or
     * empty then null is returned)
     * @return Resource in form of a byte array or null if none were found
     */
    private byte[] getResourceByteArray(Class<?> aClass, String aResourceName) {
        InputStream tmpInputStream = this.getResourceInputStream(aClass, aResourceName);
        if (tmpInputStream == null) {
            return null;
        }
        ByteArrayOutputStream tmpByteArrayOutputStream = null;
        try {
            tmpByteArrayOutputStream = new ByteArrayOutputStream();
            if (!this.copyStream(tmpInputStream, tmpByteArrayOutputStream, false)) {
                return null;
            }
            tmpByteArrayOutputStream.close();
            return tmpByteArrayOutputStream.toByteArray();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }
    // </editor-fold>

}
