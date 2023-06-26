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
package de.gnwi.mfsim.model.valueItem;

import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.*;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Utility class with static utility methods for value items
 *
 * @author Achim Zielesny
 */
public final class ValueItemUtils {

    // <editor-fold defaultstate="collapsed" desc="Clone matrix related methods">
    /**
     * Clones a matrix of ValueItemMatrixElements
     *
     * @param aMatrix Matrix
     * @return Cloned matrix
     */
    public static ValueItemMatrixElement[][] getClonedMatrixOfValueItemMatrixElements(ValueItemMatrixElement[][] aMatrix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0) {
            return aMatrix;
        }
        // </editor-fold>
        ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[aMatrix.length][];
        for (int i = 0; i < aMatrix.length; i++) {
            if (aMatrix[i].length == 0) {
                tmpNewMatrix[i] = aMatrix[i];
            } else {
                tmpNewMatrix[i] = new ValueItemMatrixElement[aMatrix[i].length];
                for (int k = 0; k < aMatrix[i].length; k++) {
                    tmpNewMatrix[i][k] = aMatrix[i][k].getClone();
                }
            }
        }
        return tmpNewMatrix;
    }

    /**
     * Clones a matrix of ValueItemDataTypeFormats
     *
     * @param aMatrix Matrix
     * @return Cloned matrix
     */
    public static ValueItemDataTypeFormat[][] cloneMatrixOfValueItemDataTypeFormats(ValueItemDataTypeFormat[][] aMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0) {
            return aMatrix;
        }

        // </editor-fold>
        ValueItemDataTypeFormat[][] tmpNewMatrix = new ValueItemDataTypeFormat[aMatrix.length][];
        for (int i = 0; i < aMatrix.length; i++) {
            if (aMatrix[i].length == 0) {
                tmpNewMatrix[i] = aMatrix[i];
            } else {
                tmpNewMatrix[i] = new ValueItemDataTypeFormat[aMatrix[i].length];
                for (int k = 0; k < aMatrix[i].length; k++) {
                    tmpNewMatrix[i][k] = aMatrix[i][k].getClone();
                }
            }
        }
        return tmpNewMatrix;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Correct methods">
    /**
     * Corrects exclusive selection texts. NOTE: Fast hash implementation.
     *
     * @param aMatrixElements Matrix elements
     * @param anAdditionalSelectionTexts Additional selection texts (may be
     * null)
     */
    public static void correctExclusiveSelectionTexts(ValueItemMatrixElement[] aMatrixElements, String[] anAdditionalSelectionTexts) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrixElements == null || aMatrixElements.length == 0) {
            return;
        }
        for (int i = 0; i < aMatrixElements.length; i++) {
            if (!aMatrixElements[i].getTypeFormat().hasExclusiveSelectionTexts()) {
                return;
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create lists">
        // tmpCombinedList consists of all values and selection texts without
        // doublettes
        HashMap<String, String> tmpCombinedTable = new HashMap<String, String>(ModelDefinitions.DEFAULT_NUMBER_OF_SELECTION_TEXTS);
        if (anAdditionalSelectionTexts != null && anAdditionalSelectionTexts.length > 0) {
            for (String tmpAdditionalSelectionText : anAdditionalSelectionTexts) {
                if (!tmpCombinedTable.containsKey(tmpAdditionalSelectionText)) {
                    tmpCombinedTable.put(tmpAdditionalSelectionText, tmpAdditionalSelectionText);
                }
            }
        }
        // tmpValueList consists of all values without doublettes
        HashMap<String, String> tmpValueTable = new HashMap<String, String>(ModelDefinitions.DEFAULT_NUMBER_OF_SELECTION_TEXT_VALUES);
        for (int i = 0; i < aMatrixElements.length; i++) {
            if (!tmpValueTable.containsKey(aMatrixElements[i].getValue())) {
                tmpValueTable.put(aMatrixElements[i].getValue(), aMatrixElements[i].getValue());
            }
        }
        for (int i = 0; i < aMatrixElements.length; i++) {
            String[] tmpSelectionTexts = aMatrixElements[i].getTypeFormat().getSelectionTexts();
            for (String tmpSingleSelectionText : tmpSelectionTexts) {
                if (!tmpCombinedTable.containsKey(tmpSingleSelectionText)) {
                    tmpCombinedTable.put(tmpSingleSelectionText, tmpSingleSelectionText);
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create new selection texts">
        for (String tmpSingleValue : tmpValueTable.values()) {
            if (tmpCombinedTable.containsKey(tmpSingleValue)) {
                tmpCombinedTable.remove(tmpSingleValue);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Correct selection texts">
        for (int i = 0; i < aMatrixElements.length; i++) {
            String[] tmpNewSelectionTexts = new String[tmpCombinedTable.size() + 1];
            if (tmpCombinedTable.size() > 0) {
                // NOTE: Start with index 1 since index 0 will be value (see
                // below)
                int tmpIndex = 1;
                for (String tmpSingleSelectionText : tmpCombinedTable.values()) {
                    tmpNewSelectionTexts[tmpIndex++] = tmpSingleSelectionText;
                }
            }
            // Add value to selection texts at index 0 (see above)
            tmpNewSelectionTexts[0] = aMatrixElements[i].getValue();
            Arrays.sort(tmpNewSelectionTexts);
            // IMPORTANT: Default value must be value of matrix element!
            aMatrixElements[i].getTypeFormat().setDefaultValueAndSelectionTexts(aMatrixElements[i].getValue(), tmpNewSelectionTexts);
        }
        // </editor-fold>
    }

    /**
     * Corrects exclusive selection texts with complete selection texts. NOTE:
     * Fast hash implementation.
     *
     * @param aMatrixElements Matrix elements
     * @param aCompleteSelectionTexts Complete selection texts (may contain
     * dublettes)
     */
    public static void correctExclusiveSelectionTextsWithCompleteTexts(ValueItemMatrixElement[] aMatrixElements, String[] aCompleteSelectionTexts) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrixElements == null || aMatrixElements.length == 0) {
            return;
        }
        if (aCompleteSelectionTexts == null || aCompleteSelectionTexts.length == 0) {
            return;
        }
        for (int i = 0; i < aMatrixElements.length; i++) {
            if (!aMatrixElements[i].getTypeFormat().hasExclusiveSelectionTexts()) {
                return;
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create hash table with complete selection texts">
        HashMap<String, String> tmpCompleteSelectionTextTable = new HashMap<String, String>(ModelDefinitions.DEFAULT_NUMBER_OF_SELECTION_TEXTS);
        for (String tmpSelectionText : aCompleteSelectionTexts) {
            if (!tmpCompleteSelectionTextTable.containsKey(tmpSelectionText)) {
                tmpCompleteSelectionTextTable.put(tmpSelectionText, tmpSelectionText);
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Remove values from complete selection text table">
        for (int i = 0; i < aMatrixElements.length; i++) {
            if (!tmpCompleteSelectionTextTable.containsKey(aMatrixElements[i].getValue())) {
                tmpCompleteSelectionTextTable.remove(aMatrixElements[i].getValue());
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Correct selection texts">
        for (int i = 0; i < aMatrixElements.length; i++) {
            String[] tmpNewSelectionTexts = new String[tmpCompleteSelectionTextTable.size() + 1];
            if (tmpCompleteSelectionTextTable.size() > 0) {
                // NOTE: Start with index 1 since index 0 will be value (see
                // below)
                int tmpIndex = 1;
                for (String tmpSingleSelectionText : tmpCompleteSelectionTextTable.values()) {
                    tmpNewSelectionTexts[tmpIndex++] = tmpSingleSelectionText;
                }
            }
            // Add value to selection texts at index 0 (see above)
            tmpNewSelectionTexts[0] = aMatrixElements[i].getValue();
            Arrays.sort(tmpNewSelectionTexts);
            // IMPORTANT: Default value must be value of matrix element!
            aMatrixElements[i].getTypeFormat().setDefaultValueAndSelectionTexts(aMatrixElements[i].getValue(), tmpNewSelectionTexts);
        }

        // </editor-fold>
    }

    /**
     * Corrects unique value columns of matrix if necessary. NOTE: Slow
     * quadratic implementation since matrix is small (number of rows: O(10)).
     *
     * @param aMatrix Matrix
     * @param aRow Row with value change (-1 if not specified)
     */
    public static void correctUniqueValueColumns(ValueItemMatrixElement[][] aMatrix, int aRow) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0 || aMatrix[0].length == 0) {
            return;
        }
        if (aRow >= 0 && aRow >= aMatrix.length) {
            return;
        }
        boolean tmpIsMatch = false;
        for (int i = 0; i < aMatrix[0].length; i++) {
            if (aMatrix[0][i].getTypeFormat().isUniqueDefault()) {
                tmpIsMatch = true;
                break;
            }
        }
        if (!tmpIsMatch) {
            return;
        }

        // </editor-fold>
        if (aRow < 0) {

            // <editor-fold defaultstate="collapsed" desc="Row is not specified">
            for (int i = 0; i < aMatrix[0].length; i++) {
                if (aMatrix[0][i].getTypeFormat().isUniqueDefault()) {
                    LinkedList<String> tmpValueList = new LinkedList<String>();
                    for (int k = 0; k < aMatrix.length; k++) {
                        String tmpValue = aMatrix[k][i].getValue();
                        // IMPORTANT: Uniqueness means also conversion to captial letters
                        if (tmpValueList.contains(tmpValue.toUpperCase(Locale.ENGLISH))) {
                            String tmpNewValue = tmpValue;
                            int tmpCounter = 0;
                            while (tmpValueList.contains(tmpNewValue.toUpperCase(Locale.ENGLISH))) {
                                tmpNewValue = tmpValue + String.valueOf(++tmpCounter);
                            }
                            tmpValueList.addLast(tmpNewValue.toUpperCase(Locale.ENGLISH));
                            aMatrix[k][i].setValue(tmpNewValue);
                        } else {
                            tmpValueList.addLast(tmpValue.toUpperCase(Locale.ENGLISH));
                        }
                    }
                    break;
                }
            }

            // </editor-fold>
        } else {

            // <editor-fold defaultstate="collapsed" desc="Row is specified">
            for (int i = 0; i < aMatrix[0].length; i++) {
                if (aMatrix[0][i].getTypeFormat().isUniqueDefault()) {
                    String tmpValue = aMatrix[aRow][i].getValue();
                    LinkedList<String> tmpValueList = new LinkedList<String>();
                    for (int k = 0; k < aMatrix.length; k++) {
                        if (k != aRow) {
                            tmpValueList.addLast(aMatrix[k][i].getValue().toUpperCase(Locale.ENGLISH));
                        }
                    }
                    if (tmpValueList.contains(tmpValue.toUpperCase(Locale.ENGLISH))) {
                        String tmpNewValue = tmpValue;
                        int tmpCounter = 0;
                        while (tmpValueList.contains(tmpNewValue.toUpperCase(Locale.ENGLISH))) {
                            tmpNewValue = tmpValue + String.valueOf(++tmpCounter);
                        }
                        aMatrix[aRow][i].setValue(tmpNewValue);
                    }
                    break;
                }
            }

            // </editor-fold>
        }

    }

    /**
     * Corrects first row editable only columns.
     *
     * @param aMatrix Matrix
     */
    public static void correctFirstRowEditableOnlyColumns(ValueItemMatrixElement[][] aMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0 || aMatrix[0].length == 0) {
            return;
        }
        boolean tmpIsMatch = false;
        for (int i = 0; i < aMatrix[0].length; i++) {
            if (aMatrix[0][i].getTypeFormat().isFirstRowEditableOnly()) {
                tmpIsMatch = true;
                break;
            }
        }
        if (!tmpIsMatch) {
            return;
        }

        // </editor-fold>
        for (int i = 0; i < aMatrix[0].length; i++) {
            if (aMatrix[0][i].getTypeFormat().isFirstRowEditableOnly()) {
                for (int k = 1; k < aMatrix.length; k++) {
                    aMatrix[k][i].setValue(aMatrix[0][i].getValue());
                }
            }
        }
    }

    /**
     * Corrects first cell of first row editable only columns.
     *
     * @param aMatrix Matrix
     */
    public static void correctFirstCellOfFirstRowEditableOnlyColumns(ValueItemMatrixElement[][] aMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0 || aMatrix[0].length == 0) {
            return;
        }
        boolean tmpIsMatch = false;
        for (int i = 0; i < aMatrix[0].length; i++) {
            if (aMatrix[0][i].getTypeFormat().isFirstRowEditableOnly()) {
                tmpIsMatch = true;
                break;
            }
        }
        if (!tmpIsMatch) {
            return;
        }

        // </editor-fold>
        for (int i = 0; i < aMatrix[0].length; i++) {
            if (aMatrix[0][i].getTypeFormat().isFirstRowEditableOnly()) {
                aMatrix[0][i].getTypeFormat().setEditable(true);
            }
        }
    }

    /**
     * Corrects unnecessary pending zeros of numeric values
     *
     * @param aMatrix Matrix
     */
    public static void correctPendingZeros(ValueItemMatrixElement[][] aMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0 || aMatrix[0].length == 0) {
            return;
        }
        boolean tmpIsMatch = false;
        for (int i = 0; i < aMatrix[0].length; i++) {
            if (aMatrix[0][i].getTypeFormat().getDataType() == ValueItemEnumDataType.NUMERIC) {
                tmpIsMatch = true;
                break;
            }
        }
        if (!tmpIsMatch) {
            return;
        }

        // </editor-fold>
        for (int i = 0; i < aMatrix[0].length; i++) {
            if (aMatrix[0][i].getTypeFormat().getDataType() == ValueItemEnumDataType.NUMERIC) {
                ValueItemUtils.correctPendingZerosColumn(aMatrix, i);
            }
        }
    }

    /**
     * Corrects unnecessary pending zeros of numeric values in specified column
     *
     * @param aMatrix Matrix
     * @param aColumn Column
     */
    public static void correctPendingZerosColumn(ValueItemMatrixElement[][] aMatrix, int aColumn) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null || aMatrix.length == 0 || aMatrix[0].length == 0) {
            return;
        }
        if (aColumn < 0 || aColumn >= aMatrix[0].length) {
            return;
        }
        if (aMatrix[0][aColumn].getTypeFormat().getDataType() != ValueItemEnumDataType.NUMERIC) {
            return;
        }

        // </editor-fold>
        int tmpMaximumNumberOfUnnecessaryPendingZeros = Integer.MAX_VALUE;
        for (int k = 0; k < aMatrix.length; k++) {
            String tmpFormattedValue = aMatrix[k][aColumn].formatValue(aMatrix[k][aColumn].getValue());
            int tmpNumberOfPendingZeros = ModelUtils.getNumberOfPendingZerosAfterDecimalPoint(tmpFormattedValue);
            if (tmpNumberOfPendingZeros < tmpMaximumNumberOfUnnecessaryPendingZeros) {
                tmpMaximumNumberOfUnnecessaryPendingZeros = tmpNumberOfPendingZeros;
            }
        }
        if (tmpMaximumNumberOfUnnecessaryPendingZeros > 0) {
            for (int k = 0; k < aMatrix.length; k++) {
                // IMPORTANT: Suppress additional format procedure of ValueItemMatrixElement
                String tmpFormattedValue = aMatrix[k][aColumn].formatValue(aMatrix[k][aColumn].getValue());
                tmpFormattedValue = tmpFormattedValue.substring(0, tmpFormattedValue.length() - tmpMaximumNumberOfUnnecessaryPendingZeros);
                aMatrix[k][aColumn].setValueWithoutFormat(tmpFormattedValue);
            }
        } else {
            for (int k = 0; k < aMatrix.length; k++) {
                aMatrix[k][aColumn].formatValue();
            }
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Sort methods">
    /**
     * Sort the value items according their defined compareTo()-method
     *
     * @param aUnsortedValueItemList The list of value items to be sorted
     * @return Sorted value item list
     */
    public static LinkedList<ValueItem> sortValueItems(LinkedList<ValueItem> aUnsortedValueItemList) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aUnsortedValueItemList == null || aUnsortedValueItemList.size() == 0) {
            return aUnsortedValueItemList;
        }

        // </editor-fold>
        Collections.sort(aUnsortedValueItemList);
        return aUnsortedValueItemList;
    }

    /**
     * Sort the value items according their defined compareTo()-method
     *
     * @param aUnsortedValueItems The array of value items to be sorted
     * @return Sorted value items
     */
    public static ValueItem[] sortValueItems(ValueItem[] aUnsortedValueItems) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aUnsortedValueItems == null || aUnsortedValueItems.length == 0) {
            return aUnsortedValueItems;
        }
        // </editor-fold>
        Arrays.sort(aUnsortedValueItems);
        return aUnsortedValueItems;
    }
    // </editor-fold>

}
