/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2023  Achim Zielesny (achim.zielesny@googlemail.com)
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

import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import java.io.File;
import java.util.Comparator;
import junit.framework.TestCase;

/**
 * Test class for class Utility.java
 *
 * @author Achim Zielesny
 */
public class TestUtility extends TestCase {

    // <editor-fold defaultstate="collapsed" desc="Private static class StringComparator">
    /**
     * Comparator for strings
     */
    private static class StringComparator implements Comparator<String> {

        /**
         * Compares two strings
         *
         * @param aString1 The first string
         * @param aString2 The second string
         *
         * @return Standard comparison result: 1, 0, -1
         */
        public synchronized int compare(String aString1, String aString2) {
            return aString1.compareTo(aString2);
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public test methods">
    /**
     * Test method for 'de.gnwi.utility.Utility.getUniqueID()'
     */
    public void testGetUniqueID() {
        String tmpUniqueID = (new StringUtilityMethods()).getGloballyUniqueID();
        assertTrue("Test1", tmpUniqueID != null);
        assertTrue("Test2", !tmpUniqueID.contains(":"));
    }

    /**
     * Test methods for read/write of string array
     */
    public void testWriteReadStringArray() {
        String[] tmpStringArrayOriginal = new String[]{"This is line1", "This is line2", "This is line3"};
        String tmpPathname = System.getProperty("user.dir") + File.separatorChar + "~" + (new StringUtilityMethods()).getGloballyUniqueID() + ".txt";
        // Delete file if necessary
        assertTrue("Test1", (new FileUtilityMethods()).deleteSingleFile(tmpPathname));
        assertTrue("Test2", (new FileUtilityMethods()).writeStringArrayToFile(tmpStringArrayOriginal, tmpPathname));
        String[] tmpStringArray = (new FileUtilityMethods()).readStringArrayFromFile(tmpPathname, null);
        assertTrue("Test3", tmpStringArray != null);
        assertTrue("Test4", tmpStringArray.length == tmpStringArrayOriginal.length);
        for (int i = 0; i < tmpStringArray.length; i++) {
            assertTrue("Test5", tmpStringArray[i].equals(tmpStringArrayOriginal[i]));
        }
        // Delete file
        assertTrue("Test6", (new FileUtilityMethods()).deleteSingleFile(tmpPathname));
    }

    /**
     * Test of date and time methods
     */
    public void testDateTimeMethods() {

        String tmpDateTime1;
        String tmpDateTime2;
        String tmpTimestampStart;
        String tmpTimestampEnd;
        String tmpDifference;

        // <editor-fold defaultstate="collapsed" desc="getDateTimeInStandardFormat()">

        assertNotNull("Test1", ModelUtils.getTimestampInStandardFormat());
        tmpDateTime1 = ModelUtils.getTimestampInStandardFormat();
        ModelUtils.delay(1500);
        tmpDateTime2 = ModelUtils.getTimestampInStandardFormat();
        assertFalse("Test2", tmpDateTime1.equals(tmpDateTime2));

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="getDateTimeDifference">

        tmpTimestampStart = "2000/01/01 - 00:00:00";
        tmpTimestampEnd = "2000/01/02 - 00:00:00";
        tmpDifference = (new TimeUtilityMethods()).getDateTimeDifference(tmpTimestampStart, tmpTimestampEnd);
        assertTrue("Test3", tmpDifference.equals(String.format(ModelMessage.get("Format.DateTimeDifference.Days"), "1", "0", "0", "0")));
        tmpTimestampStart = "2000/01/01 - 00:00:00";
        tmpTimestampEnd = "2000/01/02 - 12:02:23";
        tmpDifference = (new TimeUtilityMethods()).getDateTimeDifference(tmpTimestampStart, tmpTimestampEnd);
        assertTrue("Test4", tmpDifference.equals(String.format(ModelMessage.get("Format.DateTimeDifference.Days"), "1", "12", "2", "23")));

        // </editor-fold>

    }

    /**
     * Test of diretory related methods
     */
    public void testDirectoryRelatedMethods() {
        String tmpPrefix = "testPrefix_";
        String tmpPath = System.getProperty("user.dir");
        for (int i = 1; i < 4; i++) {
            File tmpFile = new File(tmpPath + File.separatorChar + tmpPrefix + String.valueOf(i).trim());
            if (tmpFile.isFile()) {
                assertTrue("Test0", tmpFile.delete());
            }
            assertTrue("Test1", tmpFile.mkdirs());
        }
        assertTrue("Test2", ModelUtils.getNumberOfDirectoriesWithPrefix(tmpPath, tmpPrefix) == 3);
        // Delete directories
        assertTrue("Test3", (new FileUtilityMethods()).deleteAllDirectoriesWithPrefix(tmpPath, tmpPrefix));
    }

    /**
     * Test of round of double value
     */
    public void testRoundDoubleValue() {
        assertTrue("Test1", ModelUtils.roundDoubleValue(3.43, 1) == 3.4);
        assertTrue("Test2", ModelUtils.roundDoubleValue(3.43, 2) == 3.43);
        assertTrue("Test3", ModelUtils.roundDoubleValue(3.43, 3) == 3.43);
        assertTrue("Test4", ModelUtils.roundDoubleValue(3.53, 1) == 3.5);
        assertTrue("Test5", ModelUtils.roundDoubleValue(3.55, 1) == 3.6);
        assertTrue("Test6", ModelUtils.roundDoubleValue(3.55, 0) == 4.0);
    }

    /**
     * Test of format of double value
     */
    public void testFormatDoubleValue() {
        assertTrue("Test1", (new StringUtilityMethods()).formatDoubleValue(3.43, 1).equals("3.4"));
        assertTrue("Test1a", (new StringUtilityMethods()).formatDoubleValue(3.45, 1).equals("3.5"));
        assertTrue("Test2", (new StringUtilityMethods()).formatDoubleValue(3.43, 2).equals("3.43"));
        assertTrue("Test3", (new StringUtilityMethods()).formatDoubleValue(3.43, 3).equals("3.430"));
        assertTrue("Test4", (new StringUtilityMethods()).formatDoubleValue(3.53, 1).equals("3.5"));
        assertTrue("Test5", (new StringUtilityMethods()).formatDoubleValue(3.55, 1).equals("3.5"));
        assertTrue("Test5a", (new StringUtilityMethods()).formatDoubleValue(3.551, 1).equals("3.6"));
        assertTrue("Test6", (new StringUtilityMethods()).formatDoubleValue(3.56, 1).equals("3.6"));
        assertTrue("Test7", (new StringUtilityMethods()).formatDoubleValue(3.55, 0).equals("4"));
        assertTrue("Test7", (new StringUtilityMethods()).formatDoubleValue(3.5, 0).equals("4"));
    }

    /**
     * Test: Encode/decode string
     */
    public void testEncodeDecodeStringOld() {
        String tmpTest = "Achim";
        StringBuilder tmpBuffer = new StringBuilder(5000);
        for (int i = 0; i < 1000; i++) {
            tmpBuffer.append(tmpTest);
        }
        String tmpBase64 = (new StringUtilityMethods()).compressIntoBase64String(tmpBuffer.toString());
        String tmpTestDecoded = (new StringUtilityMethods()).decompressBase64String(tmpBase64);
        assertTrue("Test1", tmpTestDecoded.equals(tmpBuffer.toString()));
    }

    /**
     * Test: Encode/decode string
     */
    public void testEncodeDecodeString() {
        String tmpTest = "Achim";
        StringBuilder tmpBuffer = new StringBuilder(5000);
        for (int i = 0; i < 1000; i++) {
            tmpBuffer.append(tmpTest);
        }
        String tmpBase64 = (new StringUtilityMethods()).compressIntoBase64String(tmpBuffer.toString());
        String tmpTestDecoded = (new StringUtilityMethods()).decompressBase64String(tmpBase64);
        assertTrue("Test1", tmpTestDecoded.equals(tmpBuffer.toString()));
    }

    /**
     * Test method readJaggedStringArrayFromFile
     */
    public void testReadJaggedStringArrayFromFile() {
        String[] tmpStringArrayOriginal = new String[]{"A B C", "D E F", "G H I"};
        String tmpPathname = System.getProperty("user.dir") + File.separatorChar + "~" + (new StringUtilityMethods()).getGloballyUniqueID() + ".txt";
        // Delete file if necessary
        assertTrue("Test1", (new FileUtilityMethods()).deleteSingleFile(tmpPathname));
        assertTrue("Test2", (new FileUtilityMethods()).writeStringArrayToFile(tmpStringArrayOriginal, tmpPathname));
        String[][] tmpJaggedStringArray = (new FileUtilityMethods()).readJaggedStringArrayFromFile(tmpPathname, null);
        assertTrue("Test3", tmpJaggedStringArray != null);
        assertTrue("Test4", tmpJaggedStringArray.length == tmpStringArrayOriginal.length);
        assertTrue("Test5a", tmpJaggedStringArray[0][0].equals("A"));
        assertTrue("Test5b", tmpJaggedStringArray[0][1].equals("B"));
        assertTrue("Test5c", tmpJaggedStringArray[0][2].equals("C"));
        assertTrue("Test5a", tmpJaggedStringArray[1][0].equals("D"));
        assertTrue("Test5b", tmpJaggedStringArray[1][1].equals("E"));
        assertTrue("Test5c", tmpJaggedStringArray[1][2].equals("F"));
        assertTrue("Test5a", tmpJaggedStringArray[2][0].equals("G"));
        assertTrue("Test5b", tmpJaggedStringArray[2][1].equals("H"));
        assertTrue("Test5c", tmpJaggedStringArray[2][2].equals("I"));
        // Delete file
        assertTrue("Test6", (new FileUtilityMethods()).deleteSingleFile(tmpPathname));
    }

    /**
     * Test method isVersion1Higher
     */
    public void testIsVersion1Higher() {
        String version1;
        String version2;

        version1 = "1.0.0.0";
        version2 = "1.0.0.0";
        assertFalse("Test1", ModelUtils.isVersion1Higher(version1, version2));

        version1 = "2.0.0";
        version2 = "1.0.0.0";
        assertFalse("Test2", ModelUtils.isVersion1Higher(version1, version2));

        version1 = "2.0.0.0";
        version2 = "1.0.0.0";
        assertTrue("Test3", ModelUtils.isVersion1Higher(version1, version2));
    }

    /**
     * Test method startsWithDoubleValue
     */
    public void testStartsWithDoubleValue() {
        String tmpText;

        tmpText = "";
        assertFalse("Test1", (new StringUtilityMethods()).startsWithDoubleValue(tmpText));

        tmpText = "abc";
        assertFalse("Test2", (new StringUtilityMethods()).startsWithDoubleValue(tmpText));

        tmpText = " 1abc";
        assertFalse("Test3", (new StringUtilityMethods()).startsWithDoubleValue(tmpText));

        tmpText = " abc 1.25";
        assertFalse("Test4", (new StringUtilityMethods()).startsWithDoubleValue(tmpText));

        tmpText = "1";
        assertTrue("Test5", (new StringUtilityMethods()).startsWithDoubleValue(tmpText));

        tmpText = " 1.25 abc";
        assertTrue("Test6", (new StringUtilityMethods()).startsWithDoubleValue(tmpText));
    }

    /**
     * Test sortGenericArray
     */
    public void testSortGenericArray() {
        String[] tmpStringArray;
        int[] tmpIndexArray;
        StringComparator tmpStringComparator;

        tmpStringArray = new String[]{"5", "1", "4", "2", "3"};
        ModelUtils.sortGenericArray(tmpStringArray, false);
        assertTrue("Test1", tmpStringArray[0].equalsIgnoreCase("1"));
        assertTrue("Test1", tmpStringArray[1].equalsIgnoreCase("2"));
        assertTrue("Test1", tmpStringArray[2].equalsIgnoreCase("3"));
        assertTrue("Test1", tmpStringArray[3].equalsIgnoreCase("4"));
        assertTrue("Test1", tmpStringArray[4].equalsIgnoreCase("5"));

        tmpStringArray = new String[]{"5", "1", "4", "2", "3"};
        ModelUtils.sortGenericArray(tmpStringArray, true);
        assertTrue("Test2", tmpStringArray[4].equalsIgnoreCase("1"));
        assertTrue("Test2", tmpStringArray[3].equalsIgnoreCase("2"));
        assertTrue("Test2", tmpStringArray[2].equalsIgnoreCase("3"));
        assertTrue("Test2", tmpStringArray[1].equalsIgnoreCase("4"));
        assertTrue("Test2", tmpStringArray[0].equalsIgnoreCase("5"));

        tmpStringArray = new String[]{"5", "1", "4", "2", "3"};
        tmpIndexArray = ModelUtils.getSortedIndexArray(tmpStringArray, false);
        assertTrue("Test3", tmpIndexArray[0] == 1);
        assertTrue("Test3", tmpIndexArray[1] == 3);
        assertTrue("Test3", tmpIndexArray[2] == 4);
        assertTrue("Test3", tmpIndexArray[3] == 2);
        assertTrue("Test3", tmpIndexArray[4] == 0);

        tmpStringArray = new String[]{"5", "1", "4", "2", "3"};
        tmpIndexArray = ModelUtils.getSortedIndexArray(tmpStringArray, true);
        assertTrue("Test4", tmpIndexArray[4] == 1);
        assertTrue("Test4", tmpIndexArray[3] == 3);
        assertTrue("Test4", tmpIndexArray[2] == 4);
        assertTrue("Test4", tmpIndexArray[1] == 2);
        assertTrue("Test4", tmpIndexArray[0] == 0);

        tmpStringArray = new String[]{"5", "1", "4", "2", "3"};
        tmpStringComparator = new StringComparator();
        tmpIndexArray = ModelUtils.getSortedIndexArray(tmpStringArray, tmpStringComparator);
        assertTrue("Test5", tmpIndexArray[0] == 1);
        assertTrue("Test5", tmpIndexArray[1] == 3);
        assertTrue("Test5", tmpIndexArray[2] == 4);
        assertTrue("Test5", tmpIndexArray[3] == 2);
        assertTrue("Test5", tmpIndexArray[4] == 0);

        tmpStringArray = new String[]{"5", "1", "4", "2", "3"};
        tmpStringComparator = new StringComparator();
        ModelUtils.sortGenericArray(tmpStringArray, tmpStringComparator);
        assertTrue("Test6", tmpStringArray[0].equalsIgnoreCase("1"));
        assertTrue("Test6", tmpStringArray[1].equalsIgnoreCase("2"));
        assertTrue("Test6", tmpStringArray[2].equalsIgnoreCase("3"));
        assertTrue("Test6", tmpStringArray[3].equalsIgnoreCase("4"));
        assertTrue("Test6", tmpStringArray[4].equalsIgnoreCase("5"));
    }

    /**
     * Test encryption methods
     */
    public void testEncryptionMethods() {
        String tmpString = "My test string";
        String tmpKey = "My key";

        byte[] tmpStringByteArray = ModelUtils.getByteArrayFromString(tmpString);
        byte[] tmpKeyByteArray = ModelUtils.getByteArrayFromString(tmpKey);
        byte[] tmpEncryptedByteArray = (new MiscUtilityMethods()).encrypt(tmpStringByteArray, tmpKeyByteArray);
        byte[] tmpDecryptedByteArray = (new MiscUtilityMethods()).encrypt(tmpEncryptedByteArray, tmpKeyByteArray);
        String tmpDecryptedString = ModelUtils.getStringFromByteArray(tmpDecryptedByteArray);
        assertEquals("Test1", tmpString, tmpDecryptedString);
    }

    /**
     * Test ZIP utility
     */
    public void testZipUtility() {
        // String aDirectoryPath = "E://Data//My Garmin";
        // String aZipFilePathname = "E://Data//Temp//Test.zip";
        // String aPath = "E://Data//Temp";
        // assertTrue(Utility.copyDirectoryToZipFile(aDirectoryPath, aZipFilePathname));
        // assertTrue(Utility.extractDirectoryFromZipFile(aPath, aZipFilePathname));
    }
    
    /**
     * Tests specific operation
     */
    public void testSpecificOperation() {
        // FileUtilityMethods tmpUtilityFileMethods = new FileUtilityMethods();
        // String tmpPath = "G://";
        // String tmpName = "JobResults";
        // tmpUtilityFileMethods.deleteAllDirectoriesWithName(tmpPath, tmpName);
    }
    // </editor-fold>
}
