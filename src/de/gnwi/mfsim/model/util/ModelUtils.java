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

import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.job.JobInput;
import de.gnwi.mfsim.model.job.JobResult;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import java.io.*;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import org.jdom2.Element;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Utility class with static utility methods
 *
 * @author Achim Zielesny
 */
public final class ModelUtils {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Server socket for single instance test
     */
    private static ServerSocket serverSocketForSingleInstanceTest;
    
    /**
     * Last time stamp in standard format
     */
    private static String lastTimeStampInStandardFormat = "";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static synchronized methods">
    // <editor-fold defaultstate="collapsed" desc="- Log file related methods">
    /**
     * Deletes log file. NOTE: Log file related methods need to be synchronized.
     */
    public static synchronized void resetLogfile() {
        // Delete log file
        try {
            File tmpFile = new File(Preferences.getInstance().getLogfilePathname());
            if (!tmpFile.isFile()) {
                return;
            } else {
                tmpFile.delete();
            }
            Preferences.getInstance().setLogEvent(false);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            Preferences.getInstance().setLogEvent(false);
            return;
        }
        // Start new logging session
        ModelUtils.appendToLogfile(false, String.format(ModelDefinitions.MFSIM_SESSION_START_FORMAT, ModelDefinitions.APPLICATION_VERSION));
    }

    /**
     * Appends single string to log file. NOTE: Log file related methods need to
     * be synchronised.
     *
     * @param anObject Object to be appended
     * @param anIsEvent True: Object is log event, false: Otherwise
     */
    public static synchronized void appendToLogfile(boolean anIsEvent, Object anObject) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anObject == null) {
            return;
        }
        // </editor-fold>
        if (anObject instanceof String) {
            String tmpString = (String) anObject;
            ModelUtils.appendSingleStringToFile(ModelUtils.getTimestampInStandardFormat() + ": " + tmpString, Preferences.getInstance().getLogfilePathname());
            if (anIsEvent) {
                Preferences.getInstance().setLogEvent(true);
            }
        } else if (anObject instanceof Exception) {
            Exception tmpException = (Exception) anObject;
            StringWriter tmpStringWriter = new StringWriter();
            tmpException.printStackTrace(new PrintWriter(tmpStringWriter));
            String tmpStackTrace = ModelUtils.getTimestampInStandardFormat() + ": " + tmpStringWriter.toString();
            ModelUtils.appendSingleStringToFile(tmpStackTrace, Preferences.getInstance().getLogfilePathname());
            if (anIsEvent) {
                Preferences.getInstance().setLogEvent(true);
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Timestamp related methods">
    /**
     * Returns "unique" current timestamp in standard format (see code)
     * NOTE: Method MUST be synchronised!
     *
     * @return "Unique" current timestamp in standard format or null if none
     * could be created.
     */
    public static synchronized String getUniqueTimestampInStandardFormat() {
        try {
            String tmpTimestampInStandardFormat = ModelUtils.getTimestampInStandardFormat();
            if (tmpTimestampInStandardFormat.equals(ModelUtils.lastTimeStampInStandardFormat)) {
                // Wait for 1.1 seconds since resolution is in seconds: This 
                // guarantees "uniqueness" on machine
                try {
                    Thread.sleep(1100L);
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, "Utility.getUniqueTimestampInStandardFormat: Exception during Thread.sleep() occurred.");
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
                tmpTimestampInStandardFormat = ModelUtils.getTimestampInStandardFormat();
            }
            ModelUtils.lastTimeStampInStandardFormat = tmpTimestampInStandardFormat;
            return tmpTimestampInStandardFormat;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Returns current timestamp in standard form (see
 ModelDefinitions.STANDARD_TIMESTAMP_FORMAT)
     *
     * @return Current timestamp in standard form or null if none could be 
     * created
     */
    public static synchronized String getTimestampInStandardFormat() {
        try {
            SimpleDateFormat tmpSimpleDateFormat = new SimpleDateFormat(ModelDefinitions.STANDARD_TIMESTAMP_FORMAT);
            Instant tmpInstant = Instant.now();
            Date tmpDate = Date.from(tmpInstant);
            return tmpSimpleDateFormat.format(tmpDate);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static methods">
    // <editor-fold defaultstate="collapsed" desc="- Encryption related methods">
    /**
     * Converts string to byte array.
     *
     * @param aString String
     * @return Byte array or null if none could be created
     */
    public static byte[] getByteArrayFromString(String aString) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return null;
        }

        // </editor-fold>
        return aString.getBytes();
    }

    /**
     * Converts byte array to string.
     *
     * @param aByteArray Byte array
     * @return String or null if none could be created
     */
    public static String getStringFromByteArray(byte[] aByteArray) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aByteArray == null || aByteArray.length == 0) {
            return null;
        }

        // </editor-fold>
        return new String(aByteArray);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- File/Directory related methods">
    // <editor-fold defaultstate="collapsed" desc="-- Create methods">
    /**
     * Creates directory and all non-existent ancestor directories if necessary
     *
     * @param aDirectoryPath Full directory path to be created
     * @return true: Directory already existed or was successfully created,
     * false: Otherwise
     */
    public static boolean createDirectory(String aDirectoryPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty()) {
            return false;
        }
        // </editor-fold>
        try {
            File tmpDirectory = new File(aDirectoryPath);
            if (!tmpDirectory.isDirectory()) {
                return tmpDirectory.mkdirs();
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    
    /**
     * Returns file pathname by adding Underscore + Number so that file does not
     * exist in specified directory.
     *
     * @param aDirectoryPath Directory path (directory must exist)
     * @param aFilePrefix File prefix (must start with a non-digit character but
     * no more checks)
     * @param aFileExtension File extension (dot is added if necessary)
     * @return File pathname by adding Underscore + Number so that file does not
     * exist in specified directory or null if file could not be created
     */
    public static String createNewFilePathname(String aDirectoryPath, String aFilePrefix, String aFileExtension) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty()) {
            return null;
        }
        if (!(new File(aDirectoryPath)).isDirectory()) {
            return null;
        }
        if (aFilePrefix == null || aFilePrefix.isEmpty()) {
            return null;
        }
        if (!aFilePrefix.substring(0, 1).matches("[a-zA-Z]")) {
            return null;
        }
        if (aFileExtension == null || aFileExtension.isEmpty()) {
            return null;
        }

        // </editor-fold>
        if (!aFileExtension.startsWith(".")) {
            aFileExtension = "." + aFileExtension;
        }
        String tmpNewFilePathName = aDirectoryPath + File.separatorChar + aFilePrefix + "_0" + aFileExtension;
        int tmpIndex = 1;
        while ((new File(tmpNewFilePathName)).isFile()) {
            tmpNewFilePathName = aDirectoryPath + File.separatorChar + aFilePrefix + "_" + String.valueOf(tmpIndex) + aFileExtension;
            tmpIndex++;
        }
        return tmpNewFilePathName;
    }

    /**
     * Creates empty file
     *
     * @param aFilePathname Full file pathname
     * @return True: Empty file was created, false: Otherwise
     */
    public static boolean createEmptyFile(String aFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilePathname == null || aFilePathname.isEmpty()) {
            return false;
        }
        if ((new File(aFilePathname)).isFile()) {
            return false;
        }
        // </editor-fold>
        try {
            File tmpFile = new File(aFilePathname);
            return tmpFile.createNewFile();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Miscellaneous methods">
    /**
     * Deletes single file
     *
     * @param aFilePathname Full pathname of file to be deleted (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public static boolean deleteSingleFile(String aFilePathname) {
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
     * Corrects extension of filename with desired extension if necessary
     *
     * @param aFilename Filename
     * @param aExtension Extension
     * @return Filename with corrected extension or null if extension could not
     * be corrected
     */
    public static String correctExtension(String aFilename, String aExtension) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilename == null || aFilename.isEmpty()) {
            return null;
        }
        if (aExtension == null || aExtension.isEmpty() || !aExtension.startsWith(".")) {
            return null;
        }

        // </editor-fold>
        if (aFilename.toLowerCase(Locale.ENGLISH).endsWith(aExtension.toLowerCase(Locale.ENGLISH))) {
            return aFilename;
        } else {
            return aFilename + aExtension;
        }

    }

    /**
     * Returns full file pathnames in specified directory path that end with
     * specified ending
     *
     * @param aDirectoryPath Path
     * @param aFileEnding File ending
     * @return Full file pathnames in specified path that end with specified
     * ending or null if none are found
     */
    public static String[] getFilesWithEnding(String aDirectoryPath, String aFileEnding) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aFileEnding == null || aFileEnding.isEmpty()) {
            return null;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            LinkedList<String> fileList = new LinkedList<String>();
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isFile()) {
                    if (tmpFile.getName().toLowerCase(Locale.ENGLISH).endsWith(aFileEnding.toLowerCase(Locale.ENGLISH))) {
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
     * Reads first line of text file
     *
     * @param aSourceFilePathname Full pathname of source (may be null then null
     * is returned)
     * @return First line of text file or null if first line could not be read
     */
    public static String getFirstLineOfTextFile(String aSourceFilePathname) {

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
            return tmpBufferedReader.readLine();
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
     * Reads last line of text file
     *
     * @param aSourceFilePathname Full pathname of source (may be null then null
     * is returned)
     * @return Last line of text file or null if last line could not be read
     */
    public static String getLastLineOfTextFile(String aSourceFilePathname) {

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
            String tmpLine = null;
            String tmpLastLine = null;
            while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                tmpLastLine = tmpLine;
            }
            return tmpLastLine;
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
     * Returns first file in target directory (or subdirectory) with defined
     * extension
     *
     * @param anExtension File extension
     * @param aTargetDirectoryPath Full target directory path
     * @return First file in target directory (or subdirectory) with defined
     * extension or null if none is found
     */
    public static String getFirstFilePathNameWithExtension(String anExtension, String aTargetDirectoryPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anExtension == null || anExtension.isEmpty() || aTargetDirectoryPath == null || aTargetDirectoryPath.isEmpty() || !(new File(aTargetDirectoryPath)).isDirectory()) {
            return null;
        }

        // </editor-fold>
        File tmpDirectory = new File(aTargetDirectoryPath);
        for (File file : tmpDirectory.listFiles()) {
            if (file.isDirectory()) {
                String tmpFirstFileWithExtension = ModelUtils.getFirstFilePathNameWithExtension(anExtension, aTargetDirectoryPath + File.separatorChar + file.getName());
                if (tmpFirstFileWithExtension != null && tmpFirstFileWithExtension.length() > 0) {
                    return tmpFirstFileWithExtension;
                }
            } else if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(anExtension.toLowerCase(Locale.ENGLISH))) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * Returns number of directories in specified path that start with specified
     * prefix
     *
     * @param aDirectoryPath Direcotry path
     * @param aPrefix Prefix
     * @return Number of directories in specified path that start with specified
     * prefix
     */
    public static int getNumberOfDirectoriesWithPrefix(String aDirectoryPath, String aPrefix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aPrefix == null || aPrefix.isEmpty() || !(new File(aDirectoryPath)).isDirectory()) {
            return 0;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            int tmpCounter = 0;
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isDirectory()) {
                    if (tmpFile.getName().startsWith(aPrefix)) {
                        tmpCounter++;
                    }
                }
            }
            return tmpCounter;
        } else {
            return 0;
        }
    }

    /**
     * Returns if a directory in specified path starts with specified prefix
     *
     * @param aDirectoryPath Direcotry path
     * @param aPrefix Prefix
     * @return True: At least one directory in specified path starts with
     * specified prefix, false: Otherwise
     */
    public static boolean hasDirectoryWithPrefix(String aDirectoryPath, String aPrefix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || aPrefix == null || aPrefix.isEmpty() || !(new File(aDirectoryPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        File[] tmpFileArray = new File(aDirectoryPath).listFiles();
        if (tmpFileArray != null) {
            for (File tmpFile : tmpFileArray) {
                if (tmpFile.isDirectory()) {
                    if (tmpFile.getName().startsWith(aPrefix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns single instance file pathname
     *
     * @return Single instance file pathname or null if file pathname could not
     * be created
     */
    public static String getSingleInstanceFilePathname() {
        String tmpPath = ModelUtils.getDpdDataPath();
        if (tmpPath == null) {
            return null;
        }
        return tmpPath + File.separatorChar + ModelDefinitions.SINGLE_INSTANCE_FILE_NAME;
    }

    /**
     * Returns DPD data path
     *
     * @return DPD data path or null if file pathname could not be created
     */
    public static String getDpdDataPath() {
        String tmpDpdDataPath = null;
        if (ModelDefinitions.LOCAL_USER_DATA_DIRECTORY_PATH == null) {
            tmpDpdDataPath = ModelDefinitions.USER_DIRECTORY_PATH + File.separatorChar + ModelDefinitions.MFSIM_DATA_DIRECTORY;
        } else {
            tmpDpdDataPath = ModelDefinitions.LOCAL_USER_DATA_DIRECTORY_PATH + File.separatorChar + ModelDefinitions.MFSIM_DATA_DIRECTORY;
        }
        return tmpDpdDataPath;
    }
    
    /**
     * Appends single string to file. NOTE: If destination pathname does not
     * already exists it is created.
     *
     * @param aString String (may be null or empty then false is returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public static boolean appendSingleStringToFile(String aString, String aDestinationFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty() || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }
        // </editor-fold>
        if (!(new File(aDestinationFilePathname)).isFile()) {
            return ModelUtils.writeSingleLineToTextFile(aString, aDestinationFilePathname);
        } else {
            PrintWriter tmpPrintWriter = null;
            try {
                // Parameter true: Append
                FileWriter tmpFileWriter = new FileWriter(aDestinationFilePathname, true);
                BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter, ModelDefinitions.BUFFER_SIZE);
                tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
                tmpPrintWriter.println(aString);
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
    }

    /**
     * Appends string list to file. NOTE: If destination pathname does not
     * already exists it is created.
     *
     * @param aStringList String list (may be null or empty then false is
     * returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public static boolean appendStringListToFile(LinkedList<String> aStringList, String aDestinationFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringList == null || aStringList.size() == 0 || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
            return false;
        }

        // </editor-fold>
        if (!(new File(aDestinationFilePathname)).isFile()) {
            return ModelUtils.writeStringListToFile(aStringList, aDestinationFilePathname);
        } else {
            PrintWriter tmpPrintWriter = null;
            try {
                // Parameter true: Append
                FileWriter tmpFileWriter = new FileWriter(aDestinationFilePathname, true);
                BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter, ModelDefinitions.BUFFER_SIZE);
                tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
                for (String tmpSingleString : aStringList) {
                    if (tmpSingleString != null) {
                        tmpPrintWriter.println(tmpSingleString);
                    } else {
                        // If string is null then write empty string to guarantee
                        // same size of list in read operation
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
    }

    /**
     * Writes single text line to file. NOTE: If destination file pathname
     * already exists nothing is done.
     *
     * @param aString String (may be null/empty then false is returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public static boolean writeSingleLineToTextFile(String aString, String aDestinationFilePathname) {
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
            tmpPrintWriter.println(aString);
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
     * Writes string list to file. NOTE: If destination pathname already exists
     * nothing is done.
     *
     * @param aStringList String list (may be null or empty then false is
     * returned)
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return true: Operation was successful, false: Operation failed
     */
    public static boolean writeStringListToFile(LinkedList<String> aStringList, String aDestinationFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringList == null || aStringList.size() == 0 || aDestinationFilePathname == null || aDestinationFilePathname.isEmpty()) {
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
            for (String tmpSingleString : aStringList) {
                if (tmpSingleString != null) {
                    tmpPrintWriter.println(tmpSingleString);
                } else {
                    // If string is null then write empty string to guarantee
                    // same size of list in read operation
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
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- String related methods">
    /**
     * Returns a string representation of an array of double values. Each double
     * value is separated by aSeparator.
     *
     * @param aDoubleArray Array of double vlaues
     * @param aSeparator Separator
     * @return Result string with double values separated by GENERAL_SEPARATOR
     * or null if result string could not be built
     */
    public static String concatenateDoubleValues(double[] aDoubleArray, String aSeparator) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDoubleArray == null || aDoubleArray.length == 0) {
            return null;
        }
        if (aSeparator == null) {
            aSeparator = "";
        }

        // </editor-fold>
        StringBuilder tmpStringBuilder = new StringBuilder(aDoubleArray.length * 20);
        for (int i = 0; i < aDoubleArray.length; i++) {
            if (tmpStringBuilder.length() == 0) {
                tmpStringBuilder.append(String.valueOf(aDoubleArray[i]));
            } else {
                tmpStringBuilder.append(aSeparator);
                tmpStringBuilder.append(String.valueOf(aDoubleArray[i]));
            }
        }
        return tmpStringBuilder.toString();
    }

    /**
     * Concatenates strings of string array
     *
     * @param aStringArray Contains strings to be concatenated
     * @param aSeparator Separator for concatenation
     * @return Result string with concatenated strings or null if strings could 
     * not be concatenated
     */
    public static String concatenateStrings(String[] aStringArray, String aSeparator) {
        return ModelUtils.concatenateStrings(aStringArray, aSeparator, 0);
    }

    /**
     * Concatenates strings of string array
     *
     * @param aStringArray Contains strings to be concatenated
     * @param aSeparator Separator for concatenation
     * @param aStartIndex The start index for aStringArray for start of
     * concatenation
     * @return Result string with concatenated strings or null if strings could 
     * not be concatenated
     */
    public static String concatenateStrings(String[] aStringArray, String aSeparator, int aStartIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0 || aStartIndex < 0 || aStartIndex >= aStringArray.length) {
            return null;
        }
        if (aSeparator == null) {
            aSeparator = "";
        }

        // </editor-fold>
        StringBuilder tmpStringBuilder = new StringBuilder(aStringArray.length * 20);
        for (int i = aStartIndex; i < aStringArray.length; i++) {
            if (aStringArray[i] != null) {
                if (tmpStringBuilder.length() == 0) {
                    tmpStringBuilder.append(aStringArray[i]);
                } else {
                    tmpStringBuilder.append(aSeparator);
                    tmpStringBuilder.append(aStringArray[i]);
                }
            }
        }
        return tmpStringBuilder.toString();
    }

    /**
     * Returns index of identity of anArray1 and anArray2. -1 is returned if
     * arrays are unequal in the first element with index 0, 0 is returned if
     * both arrays are equal in first element with index 0 but unequal in second
     * element with index 1 etc.
     *
     * @param anArray1 Array 1
     * @param anArray2 Array 2
     * @return Index of identity of anArray1 and anArray2 or -1 if arrays are
     * unequal in the first element with index 0
     */
    public static int getIdentityIndex(String[] anArray1, String[] anArray2) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray1 == null || anArray1.length == 0 || anArray2 == null || anArray2.length == 0) {
            return -1;
        }

        // </editor-fold>
        int tmpMinLength;
        if (anArray1.length < anArray2.length) {
            tmpMinLength = anArray1.length;
        } else {
            tmpMinLength = anArray2.length;
        }
        for (int i = 0; i < tmpMinLength; i++) {
            if (!anArray1[i].equals(anArray2[i])) {
                return i - 1;
            }
        }
        return tmpMinLength - 1;
    }

    /**
     * Returns number of pending zeros after decimal point (but INCLUSIVE
     * decimal point if possible)
     *
     * @param aString String
     * @return Number of pending zeros (INCLUSIVE decimal point if possible)
     */
    public static int getNumberOfPendingZerosAfterDecimalPoint(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return 0;
        }
        if (aString.indexOf(".") < 0) {
            return 0;
        }

        // </editor-fold>
        char[] tmpChars = aString.toCharArray();
        int tmpPendingZeroCounter = 0;
        for (int i = tmpChars.length - 1; i >= 0; i--) {
            if (tmpChars[i] == '0') {
                tmpPendingZeroCounter++;
            } else if (tmpChars[i] == '.') {
                tmpPendingZeroCounter++;
                break;
            } else {
                break;
            }
        }
        return tmpPendingZeroCounter;

    }

    /**
     * Returns string array with string representation of numbers from
     * aMinimumValue to aMaximumValue, e.g. aMinimumValue = 1 and aMaximumValue
     * = 3 returns {"1", "2", "3"}
     *
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @return String array with string representation of numbers from
     * aMinimumValue to aMaximumValue or null if string array could not be
     * created
     */
    public static String[] getNumberStringsForInterval(int aMinimumValue, int aMaximumValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMinimumValue > aMaximumValue) {
            return null;
        }

        // </editor-fold>
        String[] tmpNumberStrings = new String[aMaximumValue - aMinimumValue + 1];
        int tmpIndex = 0;
        for (int i = aMinimumValue; i <= aMaximumValue; i++) {
            tmpNumberStrings[tmpIndex++] = String.valueOf(i);
        }
        return tmpNumberStrings;
    }

    /**
     * Returns string array with string representation of powers of 2 from
     * aMinimumValue to aMaximumValue, e.g. aMinimumValue = 1 and aMaximumValue
     * = 8 returns {"1", "2", "4", "8"}
     *
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @return String array with string representation of powers of 2 from
     * aMinimumValue to aMaximumValue or null if string array could not be
     * created
     */
    public static String[] getNumberStringsForPowersOfTwo(int aMinimumValue, int aMaximumValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMinimumValue > aMaximumValue) {
            return null;
        }

        // </editor-fold>
        LinkedList<String> tmpNumberStringList = new LinkedList<String>();
        int tmpPower = 0;
        int tmpValue = (int) Math.pow(2.0, tmpPower++);
        while (tmpValue <= aMaximumValue) {
            if (tmpValue >= aMinimumValue) {
                tmpNumberStringList.add(String.valueOf(tmpValue));
            }
            tmpValue = (int) Math.pow(2.0, tmpPower++);
        }
        return tmpNumberStringList.toArray(new String[0]);
    }

    /**
     * Splits string into a string array according to regex matches
     *
     * @param aRegex The regular expression to match the split regions
     * @param aStringToBeSplitted The string to be splitted
     * @return String array according to regex matches or null if string could
     * not be split by supplied regex
     */
    public static String[] splitStringWithRegex(String aRegex, String aStringToBeSplitted) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aRegex == null || aStringToBeSplitted == null || aRegex.isEmpty() || aStringToBeSplitted.isEmpty()) {
            return null;
        }

        // </editor-fold>
        try {
            Pattern pattern = Pattern.compile(aRegex);
            return pattern.split(aStringToBeSplitted);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Splits string into a string array according to regex matches
     *
     * @param aPattern Pattern for regular expression to match the split regions
     * @param aStringToBeSplitted The string to be splitted
     * @return String array according to regex matches or null if string could
     * not be split by supplied regex pattern
     */
    public static String[] splitStringWithRegex(Pattern aPattern, String aStringToBeSplitted) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPattern == null || aStringToBeSplitted == null || aStringToBeSplitted.isEmpty()) {
            return null;
        }

        // </editor-fold>
        try {
            return aPattern.split(aStringToBeSplitted);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Splits string into string list according to regex matches
     *
     * @param aRegex The regular expression to match the split regions
     * @param aStringToBeSplitted The string to be splitted
     * @return Linked string list according to regex matches or null if string
     * could not be split by supplied regex
     */
    public static LinkedList<String> splitStringIntoListWithRegex(String aRegex, String aStringToBeSplitted) {
        String[] tmpSplittedStrings = ModelUtils.splitStringWithRegex(aRegex, aStringToBeSplitted);
        if (tmpSplittedStrings == null || tmpSplittedStrings.length == 0) {
            return null;
        }
        LinkedList<String> stringList = new LinkedList<String>();
        for (String tmpSingleString : tmpSplittedStrings) {
            stringList.add(tmpSingleString);
        }
        return stringList;
    }

    /**
     * Splits string into string list according to regex matches
     *
     * @param aPattern Pattern for regular expression to match the split regions
     * @param aStringToBeSplitted The string to be splitted
     * @return Linked string list according to regex matches or null if string
     * could not be split by supplied regex pattern
     */
    public static LinkedList<String> splitStringIntoListWithRegex(Pattern aPattern, String aStringToBeSplitted) {
        String[] tmpSplittedStrings = ModelUtils.splitStringWithRegex(aPattern, aStringToBeSplitted);
        if (tmpSplittedStrings == null || tmpSplittedStrings.length == 0) {
            return null;
        }
        LinkedList<String> stringList = new LinkedList<String>();
        for (String tmpSingleString : tmpSplittedStrings) {
            stringList.add(tmpSingleString);
        }
        return stringList;
    }

    /**
     * Returns trimmed substring after start string of passed string, e.g.
     * aString = "Start : Substring" and aStartString = "Start :" returns
     * "Substring".
     *
     * @param aString String
     * @param aStartString Start string
     * @return Trimmed substring after start string of passed string or empty
     * string
     */
    public static String getSubStringAfterStartString(String aString, String aStartString) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return "";
        }
        if (aStartString == null || aStartString.isEmpty()) {
            return aString;
        }
        if (!aString.startsWith(aStartString)) {
            return "";
        }

        // </editor-fold>
        return aString.substring(aStartString.length()).trim();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Filter related methods">
    /**
     * Filters job inputs according to defined filter criteria
     *
     * @param aJobInputsToBeFiltered Job inputs to be filtered
     * @return Filtered job inputs or null if all job inputs matched the filter
     * criteria
     */
    public static JobInput[] getFilteredJobInputs(JobInput[] aJobInputsToBeFiltered) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputsToBeFiltered == null || aJobInputsToBeFiltered.length == 0) {
            return null;
        }
        // </editor-fold>
        LinkedList<JobInput> tmpJobInputList = new LinkedList<JobInput>();
        for (JobInput tmpSingleJobInput : aJobInputsToBeFiltered) {
            boolean tmpIsFiltered = false;
            if (Preferences.getInstance().getJobInputFilterContainsPhrase().length() > 0 && !tmpIsFiltered) {
                tmpIsFiltered = tmpSingleJobInput.getDescription().indexOf(Preferences.getInstance().getJobInputFilterContainsPhrase()) < 0;
            }
            if (Preferences.getInstance().getJobInputFilterAfterTimestamp().length() > 0 && !tmpIsFiltered) {
                tmpIsFiltered = Preferences.getInstance().getJobInputFilterAfterTimestamp().compareTo(tmpSingleJobInput.getTimestamp()) > 0;
            }
            if (Preferences.getInstance().getJobInputFilterBeforeTimestamp().length() > 0 && !tmpIsFiltered) {
                tmpIsFiltered = Preferences.getInstance().getJobInputFilterBeforeTimestamp().compareTo(tmpSingleJobInput.getTimestamp()) < 0;
            }
            if (!tmpIsFiltered) {
                tmpJobInputList.add(tmpSingleJobInput);
            }
        }
        if (tmpJobInputList.size() > 0) {
            return tmpJobInputList.toArray(new JobInput[0]);
        } else {
            return null;
        }
    }

    /**
     * Filters job results according to defined filter criteria
     *
     * @param aJobResultsToBeFiltered Job results to be filtered
     * @return Filtered job results or null if all job results matched the
     * filter criteria
     */
    public static JobResult[] getFilteredJobResults(JobResult[] aJobResultsToBeFiltered) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultsToBeFiltered == null || aJobResultsToBeFiltered.length == 0) {
            return null;
        }
        // </editor-fold>
        LinkedList<JobResult> tmpFilteredJobResultList = new LinkedList<JobResult>();
        for (JobResult tmpSingleJobResult : aJobResultsToBeFiltered) {
            boolean tmpIsFiltered = false;
            if (Preferences.getInstance().getJobResultFilterContainsPhrase().length() > 0 && !tmpIsFiltered) {
                tmpIsFiltered = tmpSingleJobResult.getDescription().indexOf(Preferences.getInstance().getJobResultFilterContainsPhrase()) < 0;
            }
            if (Preferences.getInstance().getJobResultFilterAfterTimestamp().length() > 0 && !tmpIsFiltered) {
                tmpIsFiltered = Preferences.getInstance().getJobResultFilterAfterTimestamp().compareTo(tmpSingleJobResult.getTimestampExecutionEnd()) > 0;
            }
            if (Preferences.getInstance().getJobResultFilterBeforeTimestamp().length() > 0 && !tmpIsFiltered) {
                tmpIsFiltered = Preferences.getInstance().getJobResultFilterBeforeTimestamp().compareTo(tmpSingleJobResult.getTimestampExecutionEnd()) < 0;
            }
            if (!tmpIsFiltered) {
                tmpFilteredJobResultList.add(tmpSingleJobResult);
            }
        }
        if (tmpFilteredJobResultList.size() > 0) {
            return tmpFilteredJobResultList.toArray(new JobResult[0]);
        } else {
            return null;
        }
    }

    /**
     * Checks job results for allowed version
     *
     * @param aJobResultsToBeVersionChecked Job results to be filtered
     * @return Version-checked job results or null if all job results have a forbidden version
     */
    public static JobResult[] getVersionCheckedJobResults(JobResult[] aJobResultsToBeVersionChecked) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultsToBeVersionChecked == null || aJobResultsToBeVersionChecked.length == 0) {
            return null;
        }
        // </editor-fold>
        LinkedList<JobResult> tmpVersionCheckedJobResultList = new LinkedList<JobResult>();
        for (JobResult tmpSingleJobResult : aJobResultsToBeVersionChecked) {
            if (ModelUtils.isVersionOneEqualOrHigher(tmpSingleJobResult.getJobInput().getMFsimApplicationVersion(), ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION)){
                tmpVersionCheckedJobResultList.add(tmpSingleJobResult);
            }
        }
        if (tmpVersionCheckedJobResultList.size() > 0) {
            return tmpVersionCheckedJobResultList.toArray(new JobResult[0]);
        } else {
            return null;
        }
    }

    /**
     * Description of job input filter
     *
     * @param aNumberOfFilteredJobInputs Number of filtered job inputs
     * @return Description of job input filter
     */
    public static String getJobInputFilterDescription(int aNumberOfFilteredJobInputs) {
        StringBuilder tmpDescription = new StringBuilder(ModelDefinitions.BUFFER_SIZE_SMALL);
        if (Preferences.getInstance().getJobInputFilterContainsPhrase().length() > 0) {
            tmpDescription.append(String.format(ModelMessage.get("JobInputFilter.Contains"), Preferences.getInstance().getJobInputFilterContainsPhrase()));
        }
        if (Preferences.getInstance().getJobInputFilterAfterTimestamp().length() > 0) {
            if (tmpDescription.length() > 0) {
                tmpDescription.append(ModelMessage.get("JobFilter.Concatenation"));
            }
            tmpDescription.append(String.format(ModelMessage.get("JobInputFilter.After"), Preferences.getInstance().getJobInputFilterAfterTimestamp()));
        }
        if (Preferences.getInstance().getJobInputFilterBeforeTimestamp().length() > 0) {
            if (tmpDescription.length() > 0) {
                tmpDescription.append(ModelMessage.get("JobFilter.Concatenation"));
            }
            tmpDescription.append(String.format(ModelMessage.get("JobInputFilter.Before"), Preferences.getInstance().getJobInputFilterBeforeTimestamp()));
        }
        if (tmpDescription.length() > 0) {
            return String.format(ModelMessage.get("JobInputFilter.NumberOfFilteredJobInputs"), tmpDescription.toString(), aNumberOfFilteredJobInputs);
        } else {
            return ModelMessage.get("JobInputFilter.NoFilterDefined");
        }
    }

    /**
     * Description of job result filter
     *
     * @param aNumberOfFilteredJobResults Number of filtered job results
     * @return Description of job result filter
     */
    public static String getJobResultFilterDescription(int aNumberOfFilteredJobResults) {
        StringBuilder tmpDescription = new StringBuilder(ModelDefinitions.BUFFER_SIZE_SMALL);
        if (Preferences.getInstance().getJobResultFilterContainsPhrase().length() > 0) {
            tmpDescription.append(String.format(ModelMessage.get("JobResultFilter.Contains"), Preferences.getInstance().getJobResultFilterContainsPhrase()));
        }
        if (Preferences.getInstance().getJobResultFilterAfterTimestamp().length() > 0) {
            if (tmpDescription.length() > 0) {
                tmpDescription.append(ModelMessage.get("JobFilter.Concatenation"));
            }
            tmpDescription.append(String.format(ModelMessage.get("JobResultFilter.After"), Preferences.getInstance().getJobResultFilterAfterTimestamp()));
        }
        if (Preferences.getInstance().getJobResultFilterBeforeTimestamp().length() > 0) {
            if (tmpDescription.length() > 0) {
                tmpDescription.append(ModelMessage.get("JobFilter.Concatenation"));
            }
            tmpDescription.append(String.format(ModelMessage.get("JobResultFilter.Before"), Preferences.getInstance().getJobResultFilterBeforeTimestamp()));
        }
        if (tmpDescription.length() > 0) {
            return String.format(ModelMessage.get("JobResultFilter.NumberOfFilteredJobResults"), tmpDescription.toString(), aNumberOfFilteredJobResults);
        } else {
            return ModelMessage.get("JobResultFilter.NoFilterDefined");
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Adds string list to XML element
     *
     * @param aStringList String list
     * @param anElement XML element
     * @param aNewElementName New element name
     * @param aNewElementSingleItemName New element single item name
     */
    public static void addStringListToXmlElement(LinkedList<String> aStringList, Element anElement, String aNewElementName, String aNewElementSingleItemName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringList == null || aStringList.size() == 0) {
            return;
        }
        if (anElement == null) {
            return;
        }
        if (aNewElementName == null || aNewElementName.isEmpty()) {
            return;
        }
        if (aNewElementSingleItemName == null || aNewElementSingleItemName.isEmpty()) {
            return;
        }

        // </editor-fold>
        Element tmpElement = new Element(aNewElementName);
        for (String tmpSingleString : aStringList) {
            tmpElement.addContent(new Element(aNewElementSingleItemName).addContent(tmpSingleString));
        }
        anElement.addContent(tmpElement);
    }

    /**
     * Adds string array to XML element
     *
     * @param aStringArray String array
     * @param anElement XML element
     * @param aNewElementName New element name
     * @param aNewElementSingleItemName New element single item name
     */
    public static void addStringArrayToXmlElement(String[] aStringArray, Element anElement, String aNewElementName, String aNewElementSingleItemName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0) {
            return;
        }
        if (anElement == null) {
            return;
        }
        if (aNewElementName == null || aNewElementName.isEmpty()) {
            return;
        }
        if (aNewElementSingleItemName == null || aNewElementSingleItemName.isEmpty()) {
            return;
        }

        // </editor-fold>
        Element tmpElement = new Element(aNewElementName);
        for (String tmpSingleString : aStringArray) {
            tmpElement.addContent(new Element(aNewElementSingleItemName).addContent(tmpSingleString));
        }
        anElement.addContent(tmpElement);
    }

    /**
     * Reads string list from XML
     *
     * @param anElement The XML element containing the strings
     * @param aStringListElementName Element name of string list
     * @return String list or null if XML element could not be read
     */
    public static LinkedList<String> getStringListFromXml(Element anElement, String aStringListElementName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return null;
        }
        if (aStringListElementName == null || aStringListElementName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        Element tmpStringListElement = anElement.getChild(aStringListElementName);
        if (tmpStringListElement == null) {
            return null;
        }
        List<?> tmpSingleStringElementList = tmpStringListElement.getChildren();
        if (tmpSingleStringElementList == null || tmpSingleStringElementList.size() == 0) {
            return null;
        }
        LinkedList<String> tmpStringList = new LinkedList<String>();
        for (Object tmpSingleStringElement : tmpSingleStringElementList) {
            tmpStringList.add(((Element) tmpSingleStringElement).getText());
        }
        return tmpStringList;
    }

    /**
     * Reads string array from XML
     *
     * @param anElement The XML element containing the strings
     * @param aStringArrayElementName Element name of string list
     * @return String array or null if XML element could not be read
     */
    public static String[] getStringArrayFromXml(Element anElement, String aStringArrayElementName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return null;
        }
        if (aStringArrayElementName == null || aStringArrayElementName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        Element tmpStringListElement = anElement.getChild(aStringArrayElementName);
        if (tmpStringListElement == null) {
            return null;
        }
        List<?> tmpSingleStringElementList = tmpStringListElement.getChildren();
        if (tmpSingleStringElementList == null || tmpSingleStringElementList.size() == 0) {
            return null;
        }
        String[] tmpStringArray = new String[tmpSingleStringElementList.size()];
        int tmpIndex = 0;
        for (Object tmpSingleStringElement : tmpSingleStringElementList) {
            tmpStringArray[tmpIndex++] = ((Element) tmpSingleStringElement).getText();
        }
        return tmpStringArray;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Checks if another instance of this application is already running. NOTE:
     * If ServerSocket(1111) is already used by another program within the OS
     * this method fails!
     *
     * @return True: Another instance of this application is already running,
     * false: Otherwise
     */
    public static boolean isAnotherInstanceAlreadyRunning() {
        if (ModelUtils.serverSocketForSingleInstanceTest == null) {
            try {
                // Port number is arbitrary
                ModelUtils.serverSocketForSingleInstanceTest = new ServerSocket(1111);
                return false;
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Corrects value to be in interval [aMinimum, aMaximum]. If value already
     * is in this interval value is returned.
     *
     * @param aValue Value to be corrected
     * @param aMinimum Minimum value
     * @param aMaximum Maximum value
     * @return Corrected value in interval [aMinimum, aMaximum]
     */
    public static int correctIntegerValue(int aValue, int aMinimum, int aMaximum) {
        if (aValue < aMinimum) {
            return aMinimum;
        } else if (aValue > aMaximum) {
            return aMaximum;
        } else {
            return aValue;
        }
    }

    /**
     * Corrects value to be in interval [aMinimum, infinity). If value already
     * is in this interval value is returned.
     *
     * @param aValue Value to be corrected
     * @param aMinimum Minimum value
     * @return Corrected value in interval [aMinimum, infinity)
     */
    public static int correctIntegerValue(int aValue, int aMinimum) {
        if (aValue < aMinimum) {
            return aMinimum;
        } else {
            return aValue;
        }
    }
    
    /**
     * Corrects rotation value in degree to interval [0, 360]
     *
     * @param aRotationValue Rotation Value to be corrected
     * @return Corrected value
     */
    public static int correctRotationValue(int aRotationValue) {
        int tmpCorrectedRotationValue = aRotationValue % 360;
        if (tmpCorrectedRotationValue < 0) {
            return 360 + tmpCorrectedRotationValue;
        } else {
            return tmpCorrectedRotationValue;
        }
    }

    /**
     * Corrects value to be in interval [aMinimum, aMaximum]. If value already
     * is in this interval value is returned.
     *
     * @param aValue Value to be corrected
     * @param aMinimum Minimum value
     * @param aMaximum Maximum value
     * @return Corrected value in interval [aMinimum, aMaximum]
     */
    public static double correctDoubleValue(double aValue, double aMinimum, double aMaximum) {
        if (aValue < aMinimum) {
            return aMinimum;
        } else if (aValue > aMaximum) {
            return aMaximum;
        } else {
            return aValue;
        }
    }

    /**
     * Corrects value to be in interval [aMinimum, aMaximum]. If value already
     * is in this interval value is returned.
     *
     * @param aValue Value to be corrected
     * @param aMinimum Minimum value
     * @param aMaximum Maximum value
     * @return Corrected value in interval [aMinimum, aMaximum]
     */
    public static float correctFloatValue(float aValue, float aMinimum, float aMaximum) {
        if (aValue < aMinimum) {
            return aMinimum;
        } else if (aValue > aMaximum) {
            return aMaximum;
        } else {
            return aValue;
        }
    }

    /**
     * Corrects value to be in interval [aMinimum, aMaximum]. If value already
     * is in this interval value is returned.
     *
     * @param aValue Value to be corrected
     * @param aMinimum Minimum value
     * @param aMaximum Maximum value
     * @return Corrected value in interval [aMinimum, aMaximum]
     */
    public static long correctLongValue(long aValue, long aMinimum, long aMaximum) {
        if (aValue < aMinimum) {
            return aMinimum;
        } else if (aValue > aMaximum) {
            return aMaximum;
        } else {
            return aValue;
        }
    }

    /**
     * Delays program execution for the defined time interval
     *
     * @param aDelayInMilliseconds Delay in milliseconds (if less than 0 then 
     * there is no delay) 
     * @return true: Operat ion was successful, false: Operation
     * failed
     */
    public static boolean delay(long aDelayInMilliseconds) {
        if (aDelayInMilliseconds > 0) {
            // Perform delay
            try {
                Thread.sleep(aDelayInMilliseconds);
            } catch (InterruptedException anException) {
                ModelUtils.appendToLogfile(true, anException);
                // This should never happen!
                return false;
            }
        }
        return true;
    }

    /**
     * Returns number of decimals of string representation of a double value
     *
     * @param aDoubleValueRepresentation Representation of double value
     * @return Number of decimals of string representation of a double value
     */
    public static int getNumberOfDecimals(String aDoubleValueRepresentation) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDoubleValueRepresentation == null || aDoubleValueRepresentation.isEmpty()) {
            return 0;
        }

        // </editor-fold>
        char[] tmpChars = aDoubleValueRepresentation.toCharArray();
        boolean tmpDecimalPointOccured = false;
        int tmpDecimalCounter = 0;
        for (int i = 0; i < tmpChars.length; i++) {
            if (tmpChars[i] == '.') {
                tmpDecimalPointOccured = true;
            }
            if (Character.isDigit(tmpChars[i]) && tmpDecimalPointOccured) {
                tmpDecimalCounter++;
            }
        }
        return tmpDecimalCounter;
    }

    /**
     * Returns position of decimal GENERAL_SEPARATOR of string representation of
     * a double value
     *
     * @param aDoubleValueRepresentation Representation of double value
     * @return Position of decimal GENERAL_SEPARATOR of string representation of
     * a double value or -1 if there is none
     */
    public static int getPositionOfDecimalSeparator(String aDoubleValueRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDoubleValueRepresentation == null || aDoubleValueRepresentation.isEmpty()) {
            return -1;
        }

        // </editor-fold>
        return aDoubleValueRepresentation.indexOf(".");
    }

    /**
     * Checks if version 1 is higher than version 2
     *
     * @param aVersion1 Version 1 of form X.X.X.X
     * @param aVersion2 Version 2 of form X.X.X.X
     * @return True: Version 1 is higher, false: Otherwise
     */
    public static boolean isVersion1Higher(String aVersion1, String aVersion2) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aVersion1 == null || aVersion1.isEmpty() || !ModelDefinitions.VERSION_PATTERN.matcher(aVersion1).matches()) {
            return false;
        }
        if (aVersion2 == null || aVersion2.isEmpty() || !ModelDefinitions.VERSION_PATTERN.matcher(aVersion2).matches()) {
            return false;
        }

        // </editor-fold>
        String[] tmpItems1 = aVersion1.split("\\.");
        String[] tmpItems2 = aVersion2.split("\\.");
            if (Integer.parseInt(tmpItems1[0]) > Integer.parseInt(tmpItems2[0])) {
                return true;
            } else if (Integer.parseInt(tmpItems1[0]) < Integer.parseInt(tmpItems2[0])) {
                return false;
            }
            if (Integer.parseInt(tmpItems1[1]) > Integer.parseInt(tmpItems2[1])) {
                return true;
            } else if (Integer.parseInt(tmpItems1[1]) > Integer.parseInt(tmpItems2[1])) {
                return false;
            }
            if (Integer.parseInt(tmpItems1[2]) > Integer.parseInt(tmpItems2[2])) {
                return true;
            } else if (Integer.parseInt(tmpItems1[2]) > Integer.parseInt(tmpItems2[2])) {
                return false;
            }
            if (Integer.parseInt(tmpItems1[3]) > Integer.parseInt(tmpItems2[3])) {
                return true;
            }
            return false;
    }

    /**
     * Exits application with passed error value. NOTE: Single instance file is
     * deleted.
     *
     * @param anErrorValue Error value for application exit (0 = No error,
     * otherwise = Error occurred)
     */
    public static void exitApplication(int anErrorValue) {
        ModelUtils.deleteSingleFile(ModelUtils.getSingleInstanceFilePathname());
        System.exit(anErrorValue);
    }
    
    /**
     * Checks if version 1 is equal or higher than version 2
     *
     * @param aVersionOne Version 1 of form X.X.X.X
     * @param aVersionTwo Version 2 of form X.X.X.X
     * @return True: Version 1 is higher, false: Otherwise
     */
    public static boolean isVersionOneEqualOrHigher(String aVersionOne, String aVersionTwo) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aVersionOne == null || aVersionOne.isEmpty() || !ModelDefinitions.VERSION_PATTERN.matcher(aVersionOne).matches()) {
            return false;
        }
        if (aVersionTwo == null || aVersionTwo.isEmpty() || !ModelDefinitions.VERSION_PATTERN.matcher(aVersionTwo).matches()) {
            return false;
        }
        // </editor-fold>
        if (aVersionOne.equals(aVersionTwo)) {
            return true;
        } else {
            String[] tmpItems1 = aVersionOne.split("\\.");
            String[] tmpItems2 = aVersionTwo.split("\\.");
            if (Integer.parseInt(tmpItems1[0]) > Integer.parseInt(tmpItems2[0])) {
                return true;
            } else if (Integer.parseInt(tmpItems1[0]) < Integer.parseInt(tmpItems2[0])) {
                return false;
            }
            if (Integer.parseInt(tmpItems1[1]) > Integer.parseInt(tmpItems2[1])) {
                return true;
            } else if (Integer.parseInt(tmpItems1[1]) < Integer.parseInt(tmpItems2[1])) {
                return false;
            }
            if (Integer.parseInt(tmpItems1[2]) > Integer.parseInt(tmpItems2[2])) {
                return true;
            } else if (Integer.parseInt(tmpItems1[2]) < Integer.parseInt(tmpItems2[2])) {
                return false;
            }
            if (Integer.parseInt(tmpItems1[3]) > Integer.parseInt(tmpItems2[3])) {
                return true;
            }
            return false;
        }
    }
    
    /**
     * Rounds aValue to specified number of decimals. NOTE: No checks are
     * performed.
     *
     * @param aValue Value
     * @param aNumberOfDecimals Number of decimals
     * @return Rounded value
     */
    public static double roundDoubleValue(double aValue, int aNumberOfDecimals) {
        double tmpFactor = Math.pow(10, aNumberOfDecimals);
        return Math.floor(aValue * tmpFactor + 0.5) / tmpFactor;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Filename related methods">
    /**
     * Returns pathname of basic preferences file
     *
     * @param aPath Path of basic preferences file
     * @return Pathname of basic preferences file
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public static String getBasicPreferencesFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + ModelDefinitions.BASIC_PREFERENCES_FILENAME;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle set related methods">
    /**
     * Adds particle set file of job input to custom particle sets if necessary 
     * and makes this particle set the current one.
     * 
     * @param aJobInput Job input
     * @return True: Particle set was changed, false: Otherwise.
     */
    public static synchronized boolean addAndSetCurrentParticleSet(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return false;
        }
        // </editor-fold>
        try {
            String tmpParticleSetFilename = aJobInput.getParticleSetFilename();
            if (!Preferences.getInstance().hasParticleSetFilename(tmpParticleSetFilename)) {
                aJobInput.copyParticleSetFile(Preferences.getInstance().getCustomParticlesPath());
            }
            if (Preferences.getInstance().setCurrentParticleSetFilename(tmpParticleSetFilename)) {
                StandardParticleInteractionData.getInstance().reset();
                return true;
            } else {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Generic sorting">
    /**
     * Sorts array in ascending or descending order. Implements "Heapsort"
     * algorithm based on W.H. Press et al., Numerical recipes in FORTRAN 77,
     * Cambridge University Press, 1992
     *
     * @param <T> Comparable object
     * @param anArray Array to be sorted
     * @param anIsDescendingOrder True = Sort with descending order, false =
     * Sort with ascending order
     * @throws IllegalArgumentException Thrown if anArray is null
     */
    public static <T extends Comparable<T>> void sortGenericArray(T[] anArray, boolean anIsDescendingOrder) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray == null) {
            throw new IllegalArgumentException("anArray was null");
        }
        // Check, if sorting is necessary
        if (anArray.length <= 1) {
            return;
        }

        // </editor-fold>
        int i;
        int j;
        int k;
        int l;
        T tmpElement;

        k = anArray.length / 2 + 1;
        l = anArray.length;
        do {
            if (k > 1) {
                k--;
                tmpElement = anArray[k - 1];
            } else {
                tmpElement = anArray[l - 1];
                anArray[l - 1] = anArray[0];
                l--;
                if (l == 1) {
                    anArray[0] = tmpElement;
                    return;
                }
            }
            i = k;
            j = k + k;
            while (j <= l) {
                if (j < l) {
                    if (anIsDescendingOrder) {
                        if (anArray[j - 1].compareTo(anArray[j]) > 0) {
                            j++;
                        }
                    } else if (anArray[j - 1].compareTo(anArray[j]) < 0) {
                        j++;
                    }
                }
                if (anIsDescendingOrder) {
                    if (tmpElement.compareTo(anArray[j - 1]) > 0) {
                        anArray[i - 1] = anArray[j - 1];
                        i = j;
                        j = j + j;
                    } else {
                        j = l + 1;
                    }
                } else if (tmpElement.compareTo(anArray[j - 1]) < 0) {
                    anArray[i - 1] = anArray[j - 1];
                    i = j;
                    j = j + j;
                } else {
                    j = l + 1;
                }
            }
            anArray[i - 1] = tmpElement;
        } while (true);
    }

    /**
     * Sorts array according to aComparator. Implements "Heapsort" algorithm
     * based on W.H. Press et al., Numerical recipes in FORTRAN 77, Cambridge
     * University Press, 1992
     *
     * @param <T> Comparable object
     * @param anArray Array to be sorted
     * @param aComparator Comparator class for objects of anArray
     * @throws IllegalArgumentException Thrown if anArray is null
     */
    public static <T> void sortGenericArray(T[] anArray, Comparator<T> aComparator) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray == null) {
            throw new IllegalArgumentException("anArray was null");
        }
        // Check, if sorting is necessary
        if (anArray.length <= 1) {
            return;
        }

        // </editor-fold>
        int i;
        int j;
        int k;
        int l;
        T tmpElement;

        k = anArray.length / 2 + 1;
        l = anArray.length;
        do {
            if (k > 1) {
                k--;
                tmpElement = anArray[k - 1];
            } else {
                tmpElement = anArray[l - 1];
                anArray[l - 1] = anArray[0];
                l--;
                if (l == 1) {
                    anArray[0] = tmpElement;
                    return;
                }
            }
            i = k;
            j = k + k;
            while (j <= l) {
                if (j < l) {
                    if (aComparator.compare(anArray[j - 1], anArray[j]) < 0) {
                        j++;
                    }
                }
                if (aComparator.compare(tmpElement, anArray[j - 1]) < 0) {
                    anArray[i - 1] = anArray[j - 1];
                    i = j;
                    j = j + j;
                } else {
                    j = l + 1;
                }
            }
            anArray[i - 1] = tmpElement;
        } while (true);
    }

    /**
     * Returns index array for sorted objects in ascending or descending order:
     * anArray[indexArray[0]] smaller anArray[indexArray[1] ... for ascending order.
     * Implements "Heapsort" algorithm based on W.H. Press et al., Numerical
     * recipes in FORTRAN 77, Cambridge University Press, 1992
     *
     * @param <T> Comparable object
     * @param anArray Array to be sorted (is NOT changed)
     * @param anIsDescendingOrder True = Sort with descending order, false =
     * Sort with ascending order
     * @throws IllegalArgumentException Thrown if anArray is null
     * @return Sorted index array
     */
    public static <T extends Comparable<T>> int[] getSortedIndexArray(T[] anArray, boolean anIsDescendingOrder) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray == null || anArray.length == 0) {
            throw new IllegalArgumentException("anArray is null/empty");
        }
        // Check, if sorting is necessary
        if (anArray.length == 1) {
            return new int[]{0};
        }

        // </editor-fold>
        int i;
        int j;
        int k;
        int l;
        int tmpIndex;

        // Fill index array so that anArray[i] = anArray[tmpIndexArray[i]]
        int[] tmpIndexArray = new int[anArray.length];
        for (int tmpCounter = 0; tmpCounter < tmpIndexArray.length; tmpCounter++) {
            tmpIndexArray[tmpCounter] = tmpCounter;
        }

        k = anArray.length / 2 + 1;
        l = anArray.length;
        do {
            if (k > 1) {
                k--;
                tmpIndex = tmpIndexArray[k - 1];
            } else {
                tmpIndex = tmpIndexArray[l - 1];
                tmpIndexArray[l - 1] = tmpIndexArray[0];
                l--;
                if (l == 1) {
                    tmpIndexArray[0] = tmpIndex;
                    return tmpIndexArray;
                }
            }
            i = k;
            j = k + k;
            while (j <= l) {
                if (j < l) {
                    if (anIsDescendingOrder) {
                        if (anArray[tmpIndexArray[j - 1]].compareTo(anArray[tmpIndexArray[j]]) > 0) {
                            j++;
                        }
                    } else if (anArray[tmpIndexArray[j - 1]].compareTo(anArray[tmpIndexArray[j]]) < 0) {
                        j++;
                    }
                }
                if (anIsDescendingOrder) {
                    if (anArray[tmpIndex].compareTo(anArray[tmpIndexArray[j - 1]]) > 0) {
                        tmpIndexArray[i - 1] = tmpIndexArray[j - 1];
                        i = j;
                        j = j + j;
                    } else {
                        j = l + 1;
                    }
                } else if (anArray[tmpIndex].compareTo(anArray[tmpIndexArray[j - 1]]) < 0) {
                    tmpIndexArray[i - 1] = tmpIndexArray[j - 1];
                    i = j;
                    j = j + j;
                } else {
                    j = l + 1;
                }
            }
            tmpIndexArray[i - 1] = tmpIndex;
        } while (true);
    }

    /**
     * Returns index array according to aComparator, e.g. anArray[indexArray[0]]
     * smaller anArray[indexArray[1] ... for ascending order. Implements "Heapsort"
     * algorithm based on W.H. Press et al., Numerical recipes in FORTRAN 77,
     * Cambridge University Press, 1992
     *
     * @param <T> Object for comparator
     * @param anArray Array to be sorted (is NOT changed)
     * @param aComparator Comparator class for objects of anArray
     * @return Sorted index array
     * @throws IllegalArgumentException Thrown if anArray is null
     */
    public static <T> int[] getSortedIndexArray(T[] anArray, Comparator<T> aComparator) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray == null || anArray.length == 0) {
            throw new IllegalArgumentException("anArray is null/empty");
        }
        // Check, if sorting is necessary
        if (anArray.length == 1) {
            return new int[]{0};
        }

        // </editor-fold>
        int i;
        int j;
        int k;
        int l;
        int tmpIndex;

        // Fill index array so that anArray[i] = anArray[tmpIndexArray[i]]
        int[] tmpIndexArray = new int[anArray.length];
        for (int tmpCounter = 0; tmpCounter < tmpIndexArray.length; tmpCounter++) {
            tmpIndexArray[tmpCounter] = tmpCounter;
        }

        k = anArray.length / 2 + 1;
        l = anArray.length;
        do {
            if (k > 1) {
                k--;
                tmpIndex = tmpIndexArray[k - 1];
            } else {
                tmpIndex = tmpIndexArray[l - 1];
                tmpIndexArray[l - 1] = tmpIndexArray[0];
                l--;
                if (l == 1) {
                    tmpIndexArray[0] = tmpIndex;
                    return tmpIndexArray;
                }
            }
            i = k;
            j = k + k;
            while (j <= l) {
                if (j < l) {
                    if (aComparator.compare(anArray[tmpIndexArray[j - 1]], anArray[tmpIndexArray[j]]) < 0) {
                        j++;
                    }
                }
                if (aComparator.compare(anArray[tmpIndex], anArray[tmpIndexArray[j - 1]]) < 0) {
                    tmpIndexArray[i - 1] = tmpIndexArray[j - 1];
                    i = j;
                    j = j + j;
                } else {
                    j = l + 1;
                }
            }
            tmpIndexArray[i - 1] = tmpIndex;
        } while (true);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- OS related methods">
    /**
     * Checks if current operating system is Linux
     *
     * @return true: Current operating system is Linux, false: Otherwise
     */
    public static boolean isLinuxOperatingSystem() {
        return ModelDefinitions.OPERATING_SYSTEM.startsWith("Linux");
    }

    /**
     * Checks if current operating system is Microsoft Windows
     *
     * @return true: Current operating system is Microsoft Windows, false:
     * Otherwise
     */
    public static boolean isWindowsOperatingSystem() {
        return ModelDefinitions.OPERATING_SYSTEM.startsWith("Windows");
    }
    // </editor-fold>
    // </editor-fold>    

}
