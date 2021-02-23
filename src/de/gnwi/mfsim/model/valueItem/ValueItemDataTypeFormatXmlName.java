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
package de.gnwi.mfsim.model.valueItem;
/**
 * String constants for a ValueItemDataTypeFormat instance XML representation
 * 
 * @author Achim Zielesny
 */
public interface ValueItemDataTypeFormatXmlName {

    /**
     * <code>ALLOWED_CHARACTERS</code>: Name of AllowedCharacters
     */
    String ALLOWED_CHARACTERS = "AllowedCharacters";

    /**
     * <code>ALLOWED_MATCH</code>: Name of AllowedMatch
     */
    String ALLOWED_MATCH = "AllowedMatch";

    /**
     * <code>DEFAULT_VALUE</code>: Name of DefaultValue
     */
    String DEFAULT_VALUE = "DefaultValue";

    /**
     * <code>HAS_EXCLUSIVE_SELECTION_TEXTS</code>: Name of HasExclusiveSelectionTexts
     */
    String HAS_EXCLUSIVE_SELECTION_TEXTS = "HasExclusiveSelectionTexts";

    /**
     * <code>IS_EDITABLE</code>: Name of IsEditable
     */
    String IS_EDITABLE = "IsEditable";

    /**
     * <code>IS_FIRST_ROW_VALUE_ONLY</code>: Name of IsFirstRowEditableOnly
     */
    String IS_FIRST_ROW_VALUE_ONLY = "IsFirstRowEditableOnly";

    /**
     * <code>IS_UNIQUE_DEFAULT</code>: Name of IsUniqueDefault
     */
    String IS_UNIQUE_DEFAULT = "IsUniqueDefault";

    /**
     * <code>MAXIMUM_VALUE</code>: Name of MaximumValue
     */
    String MAXIMUM_VALUE = "MaximumValue";

    /**
     * <code>MINIMUM_VALUE</code>: Name of MinimumValue
     */
    String MINIMUM_VALUE = "MinimumValue";

    /**
     * <code>NUMBER_OF_DECIMALS</code>: Name of NumberOfDecimals
     */
    String NUMBER_OF_DECIMALS = "NumberOfDecimals";

    /**
     * <code>TYPE</code>: ValueItemEnumDataType of value item
     */
    String TYPE = "Type";

    /**
     * <code>SELECTION_TEXTS</code>: Texts for selection
     */
    String SELECTION_TEXTS = "SelectionTexts";

    /**
     * <code>SELECTION_SINGLE_TEXT</code>: Single text for selection
     */
    String SELECTION_SINGLE_TEXT = "SelectionSingleText";

    /**
     * <code>FORBIDDEN_TEXTS</code>: Forbidden texts
     */
    String FORBIDDEN_TEXTS = "ForbiddenTexts";

    /**
     * <code>FORBIDDEN_SINGLE_TEXT</code>: Forbidden single text
     */
    String FORBIDDEN_SINGLE_TEXT = "ForbiddenSingleText";

    /**
     * <code>TYPE_FORMAT</code>: ValueItemEnumDataType and format of value item
     */
    String TYPE_FORMAT = "ValueItemDataTypeFormat";

    /**
     * <code>VERSION</code>: Name of Version
     */
    String VERSION = "Version";

}
