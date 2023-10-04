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

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.Base64;
import java.util.UUID;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * String utility methods to be instantiated
 *
 * @author Achim Zielesny
 */
public class StringUtilityMethods {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Decimal format for double numbers without decimals
     */
    private final DecimalFormat formatOfNumericValueWithoutDecimals = new DecimalFormat("#0");
    
    /**
     * Pattern for strings that represent numeric values
     */
    private final Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public StringUtilityMethods() {
        // Do nothing
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- String separation methods">
    /**
     * Splits a string into a string array after one or more whitespace
     * characters.
     *
     * @param aString String to split
     * @return A string array containing the trimmed split strings or null if
     * aString was null or empty
     */
    public String[] splitAndTrim(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return null;
        }
        // </editor-fold>
        String[] tmpItems = ModelDefinitions.WHITESPACE_PATTERN.split(aString.trim());
        if (tmpItems == null || tmpItems.length == 0) {
            return null;
        }
        // NOTE: Trim-operation is not necessary since all whitespace characters are removed
        return tmpItems;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- GUID creation methods">
    /**
     * String with globally unique ID
     *
     * @return String with globally unique ID
     */
    public String getGloballyUniqueID() {
        return ModelDefinitions.NON_WORDNUMERIC_CHARACTER_PATTERN.matcher((UUID.randomUUID()).toString()).replaceAll("");
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Number related methods">
    /**
     * Formats a double value to specified number of decimals. NOTE: Double
     * value is correctly rounded to the specified number of decimals.
     *
     * @param aValueRepresentation Double value representation
     * @param aNumberOfDecimals Number of decimals
     * @return Double value representation with specified number of decimals or
     * aValue if no format change is possible
     */
    public String formatDoubleValue(String aValueRepresentation, int aNumberOfDecimals) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueRepresentation == null || aValueRepresentation.isEmpty()) {
            return aValueRepresentation;
        }
        // </editor-fold>
        if (aNumberOfDecimals > 0) {
            String tmpNumberOfDecimalsString = this.createStringWithLength(aNumberOfDecimals, '0');
            if (aValueRepresentation.trim().equals("0")) {
                return "0." + tmpNumberOfDecimalsString;
            } else {
                String tmpFormatString = "#0." + tmpNumberOfDecimalsString + ";-#0." + tmpNumberOfDecimalsString;
                DecimalFormat tmpDecimalFormat = new DecimalFormat(tmpFormatString);
                if (this.isDoubleValue(aValueRepresentation)) {
                    // Replace possible ',' as decimal GENERAL_SEPARATOR by '.'
                    return tmpDecimalFormat.format(Double.valueOf(aValueRepresentation)).replace(',', '.');
                } else {
                    return aValueRepresentation;
                }
            }
        } else {
            if (this.isDoubleValue(aValueRepresentation)) {
                // Replace possible ',' as decimal GENERAL_SEPARATOR by '.'
                return this.formatOfNumericValueWithoutDecimals.format(Double.valueOf(aValueRepresentation)).replace(',', '.');
            } else {
                return aValueRepresentation;
            }
        }
    }

    /**
     * Formats a double value to specified number of decimals. NOTE: Double
     * value is correctly rounded to the specified number of decimals.
     * NOTE: DecimalFormat uses default rounding mode different from mathematical rounding!
     *
     * @param aValue Value
     * @param aNumberOfDecimals Number of decimals
     * @return Double value representation with specified number of decimals
     */
    public String formatDoubleValue(double aValue, int aNumberOfDecimals) {
        if (aNumberOfDecimals > 0) {
            String tmpNumberOfDecimalsString = this.createStringWithLength(aNumberOfDecimals, '0');
            if (aValue == 0) {
                return "0." + tmpNumberOfDecimalsString;
            } else {
                String tmpFormatString = "#0." + tmpNumberOfDecimalsString + ";-#0." + tmpNumberOfDecimalsString;
                DecimalFormat tmpDecimalFormat = new DecimalFormat(tmpFormatString);
                return tmpDecimalFormat.format(aValue).replace(',', '.');
            }
        } else {
            return this.formatOfNumericValueWithoutDecimals.format(aValue).replace(',', '.');
        }
    }

    /**
     * True: String starts with a double value, false: Otherwise.
     *
     * @param aString String
     * @return True: String starts with a double value, false: Otherwise.
     */
    public boolean startsWithDoubleValue(String aString) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.trim().isEmpty()) {
            return false;
        }

        // </editor-fold>
        String tmpString = aString.trim();
        if (!Character.isDigit(tmpString.charAt(0))) {
            return false;
        }
        String[] tmpItems = ModelDefinitions.WHITESPACE_PATTERN.split(tmpString);
        if (tmpItems == null || tmpItems.length == 0) {
            return false;
        }
        return this.isDoubleValue(tmpItems[0]);
    }

    /**
     * Returns if aDoubleRepresentation represents a double value by parsing
     *
     * @param aDoubleRepresentation Double representation
     * @return true: aDoubleRepresentation represents a double value, false:
     * Otherwise
     */
    public boolean isDoubleValue(String aDoubleRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDoubleRepresentation == null || aDoubleRepresentation.isEmpty() || aDoubleRepresentation.equals("NaN")) {
            return false;
        }
        // </editor-fold>
        try {
            Double.valueOf(aDoubleRepresentation);
            return true;
        } catch (NumberFormatException anException) {
            // Do NOT log this exception since it is part of the function (which is not a nice implementation!)
            // Utility.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Returns number string with trailing zeros, i.e. 32 leads to "00032" with
     * maximum string length of 5
     *
     * @param aNumber Positive number
     * @param aResultStringLength Length of result string
     * @return Number string with trailing zeros or null if none could be
     * created
     */
    public String getNumberStringWithInitialZeros(int aNumber, int aResultStringLength) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 0 || aResultStringLength < 1) {
            return null;
        }
        // </editor-fold>
        String tmpNumberString = String.valueOf(aNumber);
        int tmpNumberOfZeros = aResultStringLength - tmpNumberString.length();
        if (tmpNumberOfZeros < 0) {
            return null;
        } else if (tmpNumberOfZeros == 0) {
            return tmpNumberString;
        } else {
            char[] tmpCharArray = new char[tmpNumberOfZeros];
            Arrays.fill(tmpCharArray, '0');
            String tmpZeroString = new String(tmpCharArray);
            return tmpZeroString + tmpNumberString;
        }
    }

    /**
     * Returns number of number string that was created with
     * getNumberStringWithInitialZeros()
     *
     * @param aNumberStringWithInitialZeros Number string with possible initial zeros
     * @return Number of number string that was created with
     * getNumberStringWithInitialZeros() or -1L if number could not be created
     */
    public int getNumberfromNumberStringWithInitialZeros(String aNumberStringWithInitialZeros) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberStringWithInitialZeros == null || aNumberStringWithInitialZeros.length() == 0) {
            return -1;
        }
        // </editor-fold>
        char[] tmpCharArray = aNumberStringWithInitialZeros.toCharArray();
        StringBuilder tmpBuffer = new StringBuilder(tmpCharArray.length);
        boolean tmpIsInitialZero = true;
        for (char tmpSingleChar : tmpCharArray) {
            if (tmpIsInitialZero && tmpSingleChar != '0') {
                tmpIsInitialZero = false;
            }
            if (!tmpIsInitialZero) {
                tmpBuffer.append(tmpSingleChar);
            }
        }
        if (tmpBuffer.length() == 0) {
            return 0;
        } else {
            return Integer.valueOf(tmpBuffer.toString());
        }
    }

    /**
     * Returns if anIntegerRepresentation represents an integer value by parsing
     *
     * @param anIntegerRepresentation Integer representation
     * @return true: anIntegerRepresentation represents an integer value, false:
     * Otherwise
     */
    public boolean isIntegerValue(String anIntegerRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIntegerRepresentation == null || anIntegerRepresentation.isEmpty()) {
            return false;
        }
        // </editor-fold>
        try {
            Integer.valueOf(anIntegerRepresentation);
            return true;
        } catch (NumberFormatException anException) {
            // Do NOT log this exception since it is part of the function (which is not a nice implementation!)
            // Utility.appendToLogfile(true, anException);
            return false;
        }
    }
    
    /**
     * Sorts a string array. 
     * If ALL single strings are integer value representations the array is 
     * sorted according ascending integer values.
     * 
     * @param aStringArray String array to be sorted
     */
    public void sortStringArray(String[] aStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null) {
            return;
        }
        if (aStringArray.length == 0) {
            return;
        }
        // </editor-fold>
        if (this.isIntegerValue(aStringArray[0])) {
            boolean tmpIsRepresentedIntegerArray = true;
            for (int i = 1; i < aStringArray.length; i++) {
                if (!this.isIntegerValue(aStringArray[i])) {
                    tmpIsRepresentedIntegerArray = false;
                    break;
                }
            }
            if (!tmpIsRepresentedIntegerArray) {
                Arrays.sort(aStringArray);
            } else {
                int[] tmpIntegerArray = new int[aStringArray.length];
                for (int i = 0; i < aStringArray.length; i++) {
                    tmpIntegerArray[i] = Integer.valueOf(aStringArray[i]);
                }
                Arrays.sort(tmpIntegerArray);
                for (int i = 0; i < aStringArray.length; i++) {
                    aStringArray[i] = String.valueOf(tmpIntegerArray[i]);
                }
            }
        } else {
            Arrays.sort(aStringArray);
        }
    }
    
    /**
     * Returns if a string represents a numeric value
     * 
     * @param aNumberRepresentation String representation (may be null or empty)
     * @return True: String representation is a numeric value, false: Otherwise
     */
    public boolean isNumeric(String aNumberRepresentation) {
        if (aNumberRepresentation == null || aNumberRepresentation.isEmpty()) {
            return false;
        } else {
            return this.numericPattern.matcher(aNumberRepresentation).matches();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Compress/decompress strings">
    /**
     * Comresses string into Base64 string that encodes the underlying
     * compressed UTF-8 byte array. Can be decompressed with method 
 StringUtilityMethods.decompressBase64String().
     *
     * @param aString String to be compressed
     * @return Compressed Base64 string or null if string could not be
     * compressed
     */
    public String compressIntoBase64String(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return null;
        }
        // </editor-fold>
        try {
            byte[] tmpOriginalByteArray = aString.getBytes("UTF-8");
            Deflater tmpCompresser = new Deflater();
            tmpCompresser.setInput(tmpOriginalByteArray);
            tmpCompresser.finish();
            ByteArrayOutputStream tmpByteArrayOutputStream = new ByteArrayOutputStream(tmpOriginalByteArray.length);
            byte[] tmpBuffer = new byte[ModelDefinitions.BUFFER_SIZE];
            while (!tmpCompresser.finished()) {
                int tmpCount = tmpCompresser.deflate(tmpBuffer);
                tmpByteArrayOutputStream.write(tmpBuffer, 0, tmpCount);
            }
            tmpByteArrayOutputStream.close();
            byte[] tmpCompressedByteArray = tmpByteArrayOutputStream.toByteArray();
            return Base64.getMimeEncoder().encodeToString(tmpCompressedByteArray);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Decompresses Base64 string that was compressed with method 
 StringUtilityMethods.compressIntoBase64String()
     *
     * @param aBase64String Base64 string (result string of method StringUtilityMethods.compressIntoBase64String())
     * @return Decompressed string or null if string could not be decompressed
     */
    public String decompressBase64String(String aBase64String) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBase64String == null || aBase64String.isEmpty()) {
            return null;
        }

        // </editor-fold>
        try {
            byte[] tmpCompressedByteArray = Base64.getMimeDecoder().decode(aBase64String);
            Inflater tmpDecompresser = new Inflater();
            tmpDecompresser.setInput(tmpCompressedByteArray);
            ByteArrayOutputStream tmpByteArrayOutputStream = new ByteArrayOutputStream(tmpCompressedByteArray.length);
            byte[] tmpBuffer = new byte[ModelDefinitions.BUFFER_SIZE];
            while (!tmpDecompresser.finished()) {
                int tmpCount = tmpDecompresser.inflate(tmpBuffer);
                tmpByteArrayOutputStream.write(tmpBuffer, 0, tmpCount);
            }
            tmpByteArrayOutputStream.close();
            byte[] tmpDecodedByteArray = tmpByteArrayOutputStream.toByteArray();
            return new String(tmpDecodedByteArray, 0, tmpDecodedByteArray.length, "UTF-8");
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Token related methods">
    /**
     * Returns first token of a string with (white)space separated tokens.
     *
     * @param aString String with (white)space separated tokens
     * @return First token of a string with (white)space separated tokens or
     * null if none can be found
     */
    public String getFirstToken(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return null;
        }
        // </editor-fold>
        return this.splitAndTrim(aString)[0];
    }

    /**
     * Returns last token of a string with (white)space separated tokens.
     *
     * @param aString String with (white)space separated tokens
     * @return Last token of a string with (white)space separated tokens or
     * null if none can be found
     */
    public String getLastToken(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return null;
        }
        // </editor-fold>
        String[] tmpTokens = this.splitAndTrim(aString);
        return tmpTokens[tmpTokens.length - 1];
    }
    
    /**
     * Returns all tokens of a string with (white)space separated tokens.
     *
     * @param aString String with (white)space separated tokens
     * @return All tokens of a string with (white)space separated tokens or null
     * if none can be found
     */
    public String[] getAllTokens(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return null;
        }
        // </editor-fold>
        return this.splitAndTrim(aString);
    }

    /**
     * Returns the token with specified index (starting at 0)
     * 
     * @param aString String with (white)space separated tokens
     * @param aTokenIndex Index of token (starting at 0)
     * @return Token at specified position or null if not available
     */
    public String getToken(String aString, int aTokenIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return aString;
        }
        if (aTokenIndex < 0) {
            return null;
        }
        // </editor-fold>
        String[] tmpTokens = this.splitAndTrim(aString);
        if (tmpTokens.length <= aTokenIndex) {
            return null;
        } else {
            return tmpTokens[aTokenIndex];
        }
    }
    
    /**
     * Replaces the token with specified index (starting at 0) with the
     * specified replacement
     *
     * @param aString String with (white)space separated tokens
     * @param aTokenIndex Index of token (starting at 0)
     * @param aTokenReplacement Replacement of token (if null/empty the original
     * string is returned)
     * @return String with replaced token or original string if token at
     * specified index can not be replaced
     */
    public String replaceToken(String aString, int aTokenIndex, String aTokenReplacement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return aString;
        }
        if (aTokenReplacement == null || aTokenReplacement.isEmpty()) {
            return aString;
        }
        if (aTokenIndex < 0) {
            return aString;
        }
        // </editor-fold>
        String[] tmpTokens = this.splitAndTrim(aString);
        if (tmpTokens.length <= aTokenIndex) {
            return aString;
        } else {
            StringBuilder tmpBuffer = new StringBuilder(aString.length() + aTokenReplacement.length());
            for (int i = 0; i < tmpTokens.length; i++) {
                if (tmpBuffer.length() > 0) {
                    tmpBuffer.append(" ");
                }
                if (i == aTokenIndex) {
                    tmpBuffer.append(aTokenReplacement);
                } else {
                    tmpBuffer.append(tmpTokens[i]);
                }
            }
            return tmpBuffer.toString();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Clone methods">
    /**
     * Clones a string
     *
     * @param aString String to be cloned
     * @return Cloned string
     */
    public String clone(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return aString;
        }

        // </editor-fold>
        return new String(aString);
    }

    /**
     * Clones a string array
     *
     * @param anArray String array to be cloned
     * @return Cloned array
     */
    public String[] clone(String[] anArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray == null || anArray.length == 0) {
            return anArray;
        }

        // </editor-fold>
        String[] tmpNewArray = new String[anArray.length];
        for (int i = 0; i < anArray.length; i++) {
            tmpNewArray[i] = new String(anArray[i]);
        }
        return tmpNewArray;
    }

    /**
     * Clones a string matrix
     *
     * @param aMatrix String matrix to be cloned
     * @return Cloned matrix
     */
    public String[][] clone(String[][] aMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0) {
            return aMatrix;
        }

        // </editor-fold>
        String[][] tmpNewMatrix = new String[aMatrix.length][];
        for (int i = 0; i < aMatrix.length; i++) {
            if (aMatrix[i].length == 0) {
                tmpNewMatrix[i] = aMatrix[i];
            } else {
                tmpNewMatrix[i] = new String[aMatrix[i].length];
                for (int k = 0; k < aMatrix[i].length; k++) {
                    tmpNewMatrix[i][k] = new String(aMatrix[i][k]);
                }
            }
        }
        return tmpNewMatrix;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Replaces anOldEnding of the string with aNewEnding. NOTE: Check of old
     * ending converts to lower case.
     *
     * @param aString String
     * @param anOldEnding Old ending
     * @param aNewEnding New Ending
     * @return String with replacement or original string if replacement could
     * not be performed
     */
    public String replaceEnding(String aString, String anOldEnding, String aNewEnding) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null) {
            return null;
        }
        if (aString.isEmpty()) {
            return "";
        }
        if (anOldEnding == null || anOldEnding.isEmpty() || aNewEnding == null || aNewEnding.isEmpty()) {
            return aString;
        }
        if (anOldEnding.length() > aString.length()) {
            return aString;
        }
        if (!aString.toLowerCase(Locale.ENGLISH).endsWith(anOldEnding.toLowerCase(Locale.ENGLISH))) {
            return aString;
        }

        // </editor-fold>
        return aString.substring(0, aString.length() - anOldEnding.length()) + aNewEnding;
    }

    /**
     * Generates string with defined length filled with specified character
     *
     * @param aLength Length of string
     * @param aFillCharacter Character the string will be filled with
     * @return String of defined length filled with specified character
     */
    public String createStringWithLength(int aLength, char aFillCharacter) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aLength < 0) {
            return null;
        }

        // </editor-fold>
        char[] tmpCharArray = new char[aLength];
        Arrays.fill(tmpCharArray, aFillCharacter);
        return new String(tmpCharArray);
    }

    /**
     * Generates a sortable integer representation, i.e. if value is 17 and
     * maximum value is 1000 then "0017" is returned.
     *
     * @param aValue Value (must be greater/equal 0)
     * @param aMaximumValue Maximum value (must be greater/equal 0)
     * @return Sortable integer representation of empty string if representation
     * can not be created
     */
    public String createSortablePositiveIntegerRepresentation(int aValue, int aMaximumValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue < 0 || aMaximumValue < aValue) {
            return "";
        }

        // </editor-fold>
        String tmpIntegerRepresentation = String.valueOf(aValue);
        return this.createStringWithLength(String.valueOf(aMaximumValue).length() - tmpIntegerRepresentation.length(), '0') + tmpIntegerRepresentation;
    }

    /**
     * Replaces every space character in aText by an underscore
     *
     * @param aText Text
     * @return Text with replacements
     */
    public String replaceSpaceByUnderscore(String aText) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aText == null || aText.isEmpty()) {
            return aText;
        }

        // </editor-fold>
        return aText.replace(' ', '_');
    }

    /**
     * Replaces every space character in aTexts by an underscore
     *
     * @param aTexts Text array
     * @return Text array with replacements
     */
    public String[] replaceSpaceByUnderscore(String[] aTexts) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTexts == null || aTexts.length == 0) {
            return aTexts;
        }

        // </editor-fold>
        for (int i = 0; i < aTexts.length; i++) {
            aTexts[i] = this.replaceSpaceByUnderscore(aTexts[i]);
        }
        return aTexts;
    }

    /**
     * Replaces every underscore in aText by a space character
     *
     * @param aText Text
     * @return Text with replacements
     */
    public String replaceUnderscoreBySpace(String aText) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aText == null || aText.isEmpty()) {
            return aText;
        }

        // </editor-fold>
        return aText.replace('_', ' ');
    }

    /**
     * Replaces every underscore in aTexts by a space character
     *
     * @param aTexts Text array
     * @return Text array with replacements
     */
    public String[] replaceUnderscoreBySpace(String[] aTexts) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTexts == null || aTexts.length == 0) {
            return aTexts;
        }

        // </editor-fold>
        for (int i = 0; i < aTexts.length; i++) {
            aTexts[i] = this.replaceUnderscoreBySpace(aTexts[i]);
        }
        return aTexts;
    }

    /**
     * aFirstStringArray and aSecondStringArray are concatenated to a new
     * returned string array
     *
     * @param aFirstStringArray First string array
     * @param aSecondStringArray Second string array
     * @return String array that consists of aFirstStringArray and concatenated
     * aSecondStringArray or null if concatenated array could not be created
     */
    public String[] getConcatenatedStringArrays(String[] aFirstStringArray, String[] aSecondStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFirstStringArray == null || aFirstStringArray.length == 0) {
            if (aSecondStringArray == null || aSecondStringArray.length == 0) {
                return null;
            } else {
                return aSecondStringArray;
            }
        }
        if (aSecondStringArray == null || aSecondStringArray.length == 0) {
            return aFirstStringArray;
        }

        // </editor-fold>
        String[] tmpConcatenatedStringArray = new String[aFirstStringArray.length + aSecondStringArray.length];
        int tmpIndex = 0;
        for (int i = 0; i < aFirstStringArray.length; i++) {
            tmpConcatenatedStringArray[tmpIndex++] = aFirstStringArray[i];
        }
        for (int i = 0; i < aSecondStringArray.length; i++) {
            tmpConcatenatedStringArray[tmpIndex++] = aSecondStringArray[i];
        }
        return tmpConcatenatedStringArray;
    }

    /**
     * Creates new string array with aStringForFirstPosition at index 0 and all
     * strings of aStringArray above so that aStringArray[i] = newArray[i + 1]
     *
     * @param aStringArray String array
     * @param aStringForFirstPosition String for first position
     * @return New string array with aStringForFirstPosition at index 0 and all
     * strings of aStringArray above so that aStringArray[i] = newArray[i + 1]
     */
    public String[] addStringAtFirstIndex(String[] aStringArray, String aStringForFirstPosition) {
        if (aStringArray == null || aStringArray.length == 0) {
            return new String[]{aStringForFirstPosition};
        }
        String[] newStringArray = new String[aStringArray.length + 1];
        newStringArray[0] = aStringForFirstPosition;
        for (int i = 1; i < newStringArray.length; i++) {
            newStringArray[i] = aStringArray[i - 1];
        }
        return newStringArray;
    }

    /**
     * Creates new array with all strings of aStringArray and aStringToAdd. The
     * new array is sorted.
     *
     * @param aStringArray String array
     * @param aStringToAdd String to add
     * @return Sorted new string array with aStringToAdd included
     */
    public String[] addStringAndSort(String[] aStringArray, String aStringToAdd) {
        String[] newStringArray = this.addStringAtFirstIndex(aStringArray, aStringToAdd);
        Arrays.sort(newStringArray);
        return newStringArray;
    }
    
    /**
     * Concatenates strings of string array with a single space character
     *
     * @param aStringArray Contains strings to be concatenated
     * @return Result string with concatenated strings separated by
     * single space character or null if strings could not be concatenated
     */
    public String concatenateStringsWithSpace(String[] aStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0) {
            return null;
        }
        // </editor-fold>
        StringBuilder tmpStringBuilder = new StringBuilder(aStringArray.length * 20);
        String tmpSingleSpace = " ";
        for (int i = 0; i < aStringArray.length; i++) {
            if (aStringArray[i] != null) {
                if (tmpStringBuilder.length() == 0) {
                    tmpStringBuilder.append(aStringArray[i]);
                } else {
                    tmpStringBuilder.append(tmpSingleSpace);
                    tmpStringBuilder.append(aStringArray[i]);
                }
            }
        }
        return tmpStringBuilder.toString();
    }
    
    /**
     * Reads jagged string array from specified section of line list. 
     * Each line is split after one or more whitespace characters.
     *
     * @param aLineList Line list (may be null then null
     * is returned)
     * @param aCommentLinePrefix Prefix of comment line to ignore (may be
     * null/empty)
     * @param aSectionTag Section tag (if null/empty then null is returned)
     * @return Jagged string array from specified section of line list 
     * or null if jagged string array could not be read
     */
    public String[][] readJaggedStringArrayPartFromLineList(LinkedList<String> aLineList, String aCommentLinePrefix, String aSectionTag) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aLineList == null || aLineList.isEmpty()) {
            return null;
        }
        if (aSectionTag == null || aSectionTag.isEmpty()) {
            return null;
        }
        // </editor-fold>
        try {
            LinkedList<String[]> tmpLinkedList = new LinkedList<String[]>();
            boolean tmpIsStarted = false;
            String tmpStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, aSectionTag);
            String tmpEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, aSectionTag);
            if (aCommentLinePrefix == null || aCommentLinePrefix.isEmpty()) {
                for (String tmpLine : aLineList) {
                    if (tmpIsStarted) {
                        if (tmpLine.trim().equalsIgnoreCase(tmpEndLine)) {
                            break;
                        }
                        String[] tmpItems = this.splitAndTrim(tmpLine.trim());
                        if (tmpItems != null) {
                            tmpLinkedList.add(tmpItems);
                        }
                    } else {
                        tmpIsStarted = tmpLine.trim().equalsIgnoreCase(tmpStartLine);
                    }
                }
            } else {
                for (String tmpLine : aLineList) {
                    if (tmpIsStarted) {
                        if (tmpLine.trim().equalsIgnoreCase(tmpEndLine)) {
                            break;
                        }
                        if (!tmpLine.startsWith(aCommentLinePrefix)) {
                            String[] tmpItems = this.splitAndTrim(tmpLine.trim());
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
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Protein distance force related methods">
    /**
     * Returns first protein distance force index in aLine, 
     * e.g. aLine = "1 myParticle 23 1.000 2.000 3.000" returns "23"
     * NOTE: No checks are performed for string 23
     * 
     * @param aLine Line with possible protein distance force index 
     * @return First protein distance force index or null if none is found
     */
    public String getFirstProteinBackboneForceIndex(String aLine) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aLine == null || aLine.isEmpty()) {
            return null;
        }
        // </editor-fold>
        int tmpStartPosition = aLine.indexOf(ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_START);
        if (tmpStartPosition < 0) {
            return null;
        } else {
            int tmpEndPosition = aLine.indexOf(ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_END);
            if (tmpEndPosition < 0 || tmpEndPosition < tmpStartPosition) {
                return null;
            } else {
                return aLine.substring(tmpStartPosition + 1, tmpEndPosition);
            }
        }
    }

    /**
     * Replaces first protein distance force index in aLine with aReplacement
     * e.g. aLine = "1 myParticle 23 1.000 2.000 3.000" returns "23"
     *      and aReplacement = "53" lead to result 
     *      "1 myParticle 53 1.000 2.000 3.000"
     * NOTE: No checks are performed for string 23
     * 
     * @param aLine Line with possible protein distance force index 
     * @param aReplacement Replacement
     * @return Line with possible replacement
     */
    public String replaceFirstProteinBackboneForceIndex(String aLine, String aReplacement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aReplacement == null) {
            return aLine;
        }
        // </editor-fold>
        String tmpFirstProteinBackboneForceIndex = this.getFirstProteinBackboneForceIndex(aLine);
        if (tmpFirstProteinBackboneForceIndex == null) {
            return aLine;
        } else {
            return aLine.replace(ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_START + tmpFirstProteinBackboneForceIndex + ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_END, aReplacement);
        }
    }
    // </editor-fold>
    // </editor-fold>

}
