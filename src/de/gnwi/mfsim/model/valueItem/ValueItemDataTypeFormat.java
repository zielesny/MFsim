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

import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * ValueItemEnumDataType and format for matrix elements of value items
 *
 * @author Achim Zielesny
 */
public class ValueItemDataTypeFormat {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Time utility methods
     */
    private final TimeUtilityMethods timeUtilityMethods = new TimeUtilityMethods();

    /**
     * String with regex expression for allowed characters
     */
    private String allowedCharacters;

    /**
     * Pattern for allowed characters
     */
    private Pattern allowedCharactersPattern;

    /**
     * String with regex expression for allowed match
     */
    private String allowedMatch;

    /**
     * Pattern for allowed match
     */
    private Pattern allowedMatchPattern;

    /**
     * Default value
     */
    private String defaultValue;

    /**
     * True: Selection texts are exclusive, false: Otherwise
     */
    private boolean hasExclusiveSelectionTexts;

    /**
     * True: Editable type format, false: Otherwise
     */
    private boolean isEditable;

    /**
     * True: Value of first row in a matrix must be the same in whole column.
     * Only cell in first row is editable, all cells of column in other rows are
     * not editable. False: Otherwise
     */
    private boolean isFirstRowEditableOnly;

    /**
     * True: New default must be unique (unequal compared to passed default
     * values), false: Otherwise
     */
    private boolean isUniqueDefault;

    /**
     * Maximum value
     */
    private double maximumValue;

    /**
     * Minimum value
     */
    private double minimumValue;

    /**
     * Number of decimals
     */
    private int numberOfDecimals;

    /**
     * Texts for selection
     */
    private String[] selectionTexts;
    
    /**
     * Hashmap for selection texts
     */
    private HashMap<String, String> selectionTextsMap;

    /**
     * Forbidden texts
     */
    private String[] forbiddenTexts;

    /**
     * Single type
     */
    private ValueItemEnumDataType dataType;
    
    /**
     * True: Highlighted, false: Otherwise
     */
    private boolean isHighlighted;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    // <editor-fold defaultstate="collapsed" desc="- NUMERIC and NUMERIC_NULL type constructors">
    /**
     * Constructor
     */
    public ValueItemDataTypeFormat() {
        this.initalize();
    }

    /**
     * Constructor
     *
     * @param aNumberOfDecimals Number of decimals
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemDataTypeFormat(int aNumberOfDecimals) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }

        // </editor-fold>
        this.initalize();
        this.numberOfDecimals = aNumberOfDecimals;
    }

    /**
     * Constructor
     *
     * @param aNumberOfDecimals Number of decimals
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemDataTypeFormat(int aNumberOfDecimals, boolean anIsEditable) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }

        // </editor-fold>
        this.initalize();
        this.numberOfDecimals = aNumberOfDecimals;
        this.isEditable = anIsEditable;
    }

    /**
     * Constructor
     *
     * @param aNumberOfDecimals Number of decimals
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @param anIsNumericNullAllowed True: Null is allowed for numeric value,
     * false: Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemDataTypeFormat(int aNumberOfDecimals, boolean anIsEditable, boolean anIsNumericNullAllowed) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        // </editor-fold>
        this.initalize();
        this.numberOfDecimals = aNumberOfDecimals;
        this.isEditable = anIsEditable;
        if (anIsNumericNullAllowed) {
            this.dataType = ValueItemEnumDataType.NUMERIC_NULL;
        }
    }

    /**
     * Constructor
     *
     * @param aNumberOfDecimals Number of decimals
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(int aNumberOfDecimals, double aMinimumValue, double aMaximumValue) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aMaximumValue < aMinimumValue) {
            throw new IllegalArgumentException("aMaximumValue must be greater or equal to aMinimumValue.");
        }
        // </editor-fold>
        this.initalize();
        this.numberOfDecimals = aNumberOfDecimals;
        this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, aNumberOfDecimals);
        this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, aNumberOfDecimals);
    }

    /**
     * Constructor
     *
     * @param aNumberOfDecimals Number of decimals
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @param anIsNumericNullAllowed True: Null is allowed for numeric value,
     * false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(int aNumberOfDecimals, double aMinimumValue, double aMaximumValue, boolean anIsEditable, boolean anIsNumericNullAllowed) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aMaximumValue < aMinimumValue) {
            throw new IllegalArgumentException("aMaximumValue must be greater or equal to aMinimumValue.");
        }

        // </editor-fold>
        this.initalize();
        this.numberOfDecimals = aNumberOfDecimals;
        this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, aNumberOfDecimals);
        this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, aNumberOfDecimals);
        this.isEditable = anIsEditable;
        if (anIsNumericNullAllowed) {
            this.dataType = ValueItemEnumDataType.NUMERIC_NULL;
        }
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param aNumberOfDecimals Number of decimals
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, int aNumberOfDecimals) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        // NOTE: Define FIRST (since this.numberOfDecimals is used for formatting purposes of other variables)
        this.numberOfDecimals = aNumberOfDecimals;
        // NOTE: Formatting of default value NEEDS this.numberOfDecimals: Define after this.numberOfDecimals
        this.defaultValue = this.formatValue(aDefaultValue);
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param aNumberOfDecimals Number of decimals
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, int aNumberOfDecimals, boolean anIsEditable) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.isEditable = anIsEditable;
        // NOTE: Define FIRST (since this.numberOfDecimals is used for formatting purposes of other variables)
        this.numberOfDecimals = aNumberOfDecimals;
        // NOTE: Formatting of default value NEEDS this.numberOfDecimals: Define after this.numberOfDecimals
        this.defaultValue = this.formatValue(aDefaultValue);
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param aNumberOfDecimals Number of decimals
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @param anIsNumericNullAllowed True: Null is allowed for numeric value,
     * false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, int aNumberOfDecimals, boolean anIsEditable, boolean anIsNumericNullAllowed) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.isEditable = anIsEditable;
        // NOTE: Define FIRST (since this.numberOfDecimals is used for formatting purposes of other variables)
        this.numberOfDecimals = aNumberOfDecimals;
        // ... and SECOND (since this.dataType is used for formatting purposes of other variables)
        if (anIsNumericNullAllowed) {
            this.dataType = ValueItemEnumDataType.NUMERIC_NULL;
        }
        // NOTE: Formatting of default value NEEDS this.numberOfDecimals and this.dataType: Define after this.numberOfDecimals and this.dataType
        this.defaultValue = this.formatValue(aDefaultValue);
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param aNumberOfDecimals Number of decimals
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, int aNumberOfDecimals, double aMinimumValue, double aMaximumValue) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aMaximumValue < aMinimumValue) {
            throw new IllegalArgumentException("aMaximumValue must be greater or equal to aMinimumValue.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        // </editor-fold>
        this.initalize();
        // NOTE: Define FIRST (since this.numberOfDecimals is used for formatting purposes of other variables)
        this.numberOfDecimals = aNumberOfDecimals;
        this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, aNumberOfDecimals);
        this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, aNumberOfDecimals);
        // NOTE: Formatting of default value NEEDS this.numberOfDecimals: Define after this.numberOfDecimals
        this.defaultValue = this.formatValue(aDefaultValue);
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param aNumberOfDecimals Number of decimals
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, int aNumberOfDecimals, double aMinimumValue, double aMaximumValue, boolean anIsEditable) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aMaximumValue < aMinimumValue) {
            throw new IllegalArgumentException("aMaximumValue must be greater or equal to aMinimumValue.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        // </editor-fold>
        this.initalize();
        // NOTE: Define FIRST (since this.numberOfDecimals is used for formatting purposes of other variables)
        this.numberOfDecimals = aNumberOfDecimals;
        this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, aNumberOfDecimals);
        this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, aNumberOfDecimals);
        // NOTE: Formatting of default value NEEDS this.numberOfDecimals: Define after this.numberOfDecimals
        this.defaultValue = this.formatValue(aDefaultValue);
        this.isEditable = anIsEditable;
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param aNumberOfDecimals Number of decimals
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @param anIsNumericNullAllowed True: Null is allowed for numeric value,
     * false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, int aNumberOfDecimals, double aMinimumValue, double aMaximumValue, boolean anIsEditable, boolean anIsNumericNullAllowed)
            throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aMaximumValue < aMinimumValue) {
            throw new IllegalArgumentException("aMaximumValue must be greater or equal to aMinimumValue.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        // NOTE: Define FIRST (since this.numberOfDecimals is used for formatting purposes of other variables)
        this.numberOfDecimals = aNumberOfDecimals;
        // ... and SECOND (since this.dataType is used for formatting purposes of other variables)
        if (anIsNumericNullAllowed) {
            this.dataType = ValueItemEnumDataType.NUMERIC_NULL;
        }
        this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, aNumberOfDecimals);
        this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, aNumberOfDecimals);
        // NOTE: Formatting of default value NEEDS this.numberOfDecimals and this.dataType: Define after this.numberOfDecimals and this.dataType
        this.defaultValue = this.formatValue(aDefaultValue);
        this.isEditable = anIsEditable;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TEXT type constructors">
    /**
     * Constructor
     *
     * @param anIsEditable True: Editable type format, false: Otherwise
     */
    public ValueItemDataTypeFormat(boolean anIsEditable) {
        this.initalize();
        this.dataType = ValueItemEnumDataType.TEXT;
        this.isEditable = anIsEditable;
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param anIsEditable True: Editable type format, false: Otherwise
     */
    public ValueItemDataTypeFormat(String aDefaultValue, boolean anIsEditable) {
        this.initalize();
        this.dataType = ValueItemEnumDataType.TEXT;
        this.defaultValue = this.formatValue(aDefaultValue);
        this.isEditable = anIsEditable;
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param anAllowedCharacters String with regex expression for allowed
     * characters (not allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, String anAllowedCharacters) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        if (anAllowedCharacters == null) {
            throw new IllegalArgumentException("anAllowedCharacters is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.TEXT;
        this.defaultValue = this.formatValue(aDefaultValue);

        // <editor-fold defaultstate="collapsed" desc="Allowed characters">
        if (anAllowedCharacters.length() > 0) {
            try {
                this.allowedCharactersPattern = Pattern.compile(this.allowedCharacters);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("anAllowedCharacters is illegal.");
            }
        }
        this.allowedCharacters = anAllowedCharacters;

        // </editor-fold>
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param anAllowedCharacters String with regex expression for allowed
     * characters (not allowed to be null)
     * @param anAllowedMatch String with regex expression for allowed match (not
     * allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, String anAllowedCharacters, String anAllowedMatch) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        if (anAllowedCharacters == null) {
            throw new IllegalArgumentException("anAllowedCharacters is not allowed to be null.");
        }
        if (anAllowedMatch == null) {
            throw new IllegalArgumentException("anAllowedMatch is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.TEXT;
        this.defaultValue = this.formatValue(aDefaultValue);
        // <editor-fold defaultstate="collapsed" desc="Allowed characters">
        if (anAllowedCharacters.length() > 0) {
            try {
                this.allowedCharactersPattern = Pattern.compile(anAllowedCharacters);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("anAllowedCharacters is illegal.");
            }
        }
        this.allowedCharacters = anAllowedCharacters;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Allowed match">
        if (anAllowedMatch.length() > 0) {
            try {
                this.allowedMatchPattern = Pattern.compile(anAllowedMatch);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("allowedMatch is illegal.");
            }
        }
        this.allowedMatch = anAllowedMatch;

        // </editor-fold>
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param anIsUniqueDefault True: New default must be unique (unequal
     * compared to passed default values), false: Otherwise
     * @param anAllowedCharacters String with regex expression for allowed
     * characters (not allowed to be null)
     * @param anAllowedMatch String with regex expression for allowed match (not
     * allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, boolean anIsUniqueDefault, String anAllowedCharacters, String anAllowedMatch) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        if (anAllowedCharacters == null) {
            throw new IllegalArgumentException("anAllowedCharacters is not allowed to be null.");
        }
        if (anAllowedMatch == null) {
            throw new IllegalArgumentException("anAllowedMatch is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.TEXT;
        this.defaultValue = this.formatValue(aDefaultValue);
        this.isUniqueDefault = anIsUniqueDefault;

        // <editor-fold defaultstate="collapsed" desc="Allowed characters">
        if (anAllowedCharacters.length() > 0) {
            try {
                this.allowedCharactersPattern = Pattern.compile(anAllowedCharacters);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("anAllowedCharacters is illegal.");
            }
        }
        this.allowedCharacters = anAllowedCharacters;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Allowed match">
        if (anAllowedMatch.length() > 0) {
            try {
                this.allowedMatchPattern = Pattern.compile(anAllowedMatch);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("allowedMatch is illegal.");
            }
        }
        this.allowedMatch = anAllowedMatch;

        // </editor-fold>
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TEXT_EMPTY type constructors">
    /**
     * Constructor
     *
     * @param anAllowedCharacters String with regex expression for allowed
     * characters (not allowed to be null)
     * @param anAllowedMatch String with regex expression for allowed match (not
     * allowed to be null)
     * @param aForbiddenTexts Forbidden texts
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String anAllowedCharacters, String anAllowedMatch, String[] aForbiddenTexts) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anAllowedCharacters == null) {
            throw new IllegalArgumentException("anAllowedCharacters is not allowed to be null.");
        }
        if (anAllowedMatch == null) {
            throw new IllegalArgumentException("anAllowedMatch is not allowed to be null.");
        }
        if (aForbiddenTexts == null || aForbiddenTexts.length == 0) {
            throw new IllegalArgumentException("aForbiddenTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aForbiddenTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single forbidden text is not allowed to be null/empty.");
            }
        }

        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.TEXT_EMPTY;
        this.forbiddenTexts = aForbiddenTexts;
        // <editor-fold defaultstate="collapsed" desc="Allowed characters">
        if (anAllowedCharacters.length() > 0) {
            try {
                this.allowedCharactersPattern = Pattern.compile(anAllowedCharacters);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("anAllowedCharacters is illegal.");
            }
        }
        this.allowedCharacters = anAllowedCharacters;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Allowed match">
        if (anAllowedMatch.length() > 0) {
            try {
                this.allowedMatchPattern = Pattern.compile(anAllowedMatch);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("allowedMatch is illegal.");
            }
        }
        this.allowedMatch = anAllowedMatch;

        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SELECTION_TEXT type constructors">

    /**
     * Constructor
     *
     * @param aSelectionTexts Texts for selection (may be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String[] aSelectionTexts) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            throw new IllegalArgumentException("aSelectionTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single selection text is not allowed to be null/empty.");
            }
        }

        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.SELECTION_TEXT;
        this.initializeSelectionTexts(aSelectionTexts);
        this.defaultValue = this.formatValue(aSelectionTexts[0]);
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value (must be in aSelectionTexts)
     * @param aSelectionTexts Texts for selection (not allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, String[] aSelectionTexts) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            throw new IllegalArgumentException("aSelectionTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single selection text is not allowed to be null/empty.");
            }
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.SELECTION_TEXT;
        this.initializeSelectionTexts(aSelectionTexts);
        if (!this.selectionTextsMap.containsKey(aDefaultValue)) {
            throw new IllegalArgumentException("aDefaultValue is not in aSelectionTexts.");
        }
        this.defaultValue = this.formatValue(aDefaultValue);
    }

    /**
     * Constructor
     *
     * @param aSelectionTexts Texts for selection (may be null)
     * @param aHasExclusiveSelectionTexts True: Selection texts are exclusive,
     * false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String[] aSelectionTexts, boolean aHasExclusiveSelectionTexts) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            throw new IllegalArgumentException("aSelectionTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single selection text is not allowed to be null/empty.");
            }
        }

        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.SELECTION_TEXT;
        this.initializeSelectionTexts(aSelectionTexts);
        this.defaultValue = this.formatValue(aSelectionTexts[0]);
        this.hasExclusiveSelectionTexts = aHasExclusiveSelectionTexts;
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value (must be in aSelectionTexts)
     * @param aSelectionTexts Texts for selection (not allowed to be null)
     * @param aHasExclusiveSelectionTexts True: Selection texts are exclusive,
     * false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, String[] aSelectionTexts, boolean aHasExclusiveSelectionTexts) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            throw new IllegalArgumentException("aSelectionTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single selection text is not allowed to be null/empty.");
            }
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.SELECTION_TEXT;
        this.initializeSelectionTexts(aSelectionTexts);
        if (!this.selectionTextsMap.containsKey(aDefaultValue)) {
            throw new IllegalArgumentException("aDefaultValue is not in aSelectionTexts.");
        }
        this.defaultValue = this.formatValue(aDefaultValue);
        this.hasExclusiveSelectionTexts = aHasExclusiveSelectionTexts;
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value (must be in aSelectionTexts)
     * @param aSelectionTexts Texts for selection (not allowed to be null)
     * @param aHasExclusiveSelectionTexts True: Selection texts are exclusive,
     * false: Otherwise
     * @param anIsFirstRowEditableOnly True: Value of first row in a matrix must
     * be the same in whole column. Only cell in first row is editable, all
     * cells of column in other rows are not editable. False: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, String[] aSelectionTexts, boolean aHasExclusiveSelectionTexts, boolean anIsFirstRowEditableOnly) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            throw new IllegalArgumentException("aSelectionTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single selection text is not allowed to be null/empty.");
            }
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        // </editor-fold>
        this.initalize();
        this.dataType = ValueItemEnumDataType.SELECTION_TEXT;
        this.initializeSelectionTexts(aSelectionTexts);
        if (!this.selectionTextsMap.containsKey(aDefaultValue)) {
            throw new IllegalArgumentException("aDefaultValue is not in aSelectionTexts.");
        }
        this.defaultValue = this.formatValue(aDefaultValue);
        this.hasExclusiveSelectionTexts = aHasExclusiveSelectionTexts;
        this.isFirstRowEditableOnly = anIsFirstRowEditableOnly;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Arbitrary type constructors">
    /**
     * Constructor
     *
     * @param aType Type
     */
    public ValueItemDataTypeFormat(ValueItemEnumDataType aType) {
        this.initalize();
        this.dataType = aType;
    }

    /**
     * Constructor
     *
     * @param aType Type
     * @param anIsEditable True: Editable type format, false: Otherwise
     */
    public ValueItemDataTypeFormat(ValueItemEnumDataType aType, boolean anIsEditable) {
        this.initalize();
        this.dataType = aType;
        this.isEditable = anIsEditable;
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value (not allowed to be null)
     * @param aType Type
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, ValueItemEnumDataType aType) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.dataType = aType;
        this.defaultValue = this.formatValue(aDefaultValue);
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value (not allowed to be null)
     * @param anIsUniqueDefault True: New default must be unique (unequal
     * compared to passed default values), false: Otherwise
     * @param aType Type
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, boolean anIsUniqueDefault, ValueItemEnumDataType aType) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.dataType = aType;
        this.defaultValue = this.formatValue(aDefaultValue);
        this.isUniqueDefault = anIsUniqueDefault;
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value (not allowed to be null)
     * @param anIsUniqueDefault True: New default must be unique (unequal
     * compared to passed default values), false: Otherwise
     * @param anAllowedCharacters String with regex expression for allowed
     * characters (not allowed to be null)
     * @param aType Type
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, boolean anIsUniqueDefault, String anAllowedCharacters, ValueItemEnumDataType aType) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        if (anAllowedCharacters == null) {
            throw new IllegalArgumentException("anAllowedCharacters is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.dataType = aType;
        this.defaultValue = this.formatValue(aDefaultValue);
        this.isUniqueDefault = anIsUniqueDefault;

        // <editor-fold defaultstate="collapsed" desc="Allowed characters">
        if (anAllowedCharacters.length() > 0) {
            try {
                this.allowedCharactersPattern = Pattern.compile(anAllowedCharacters);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("anAllowedCharacters is illegal.");
            }
        }
        this.allowedCharacters = anAllowedCharacters;

        // </editor-fold>
    }

    /**
     * Constructor
     *
     * @param aDefaultValue Default value
     * @param aType Type
     * @param anIsEditable True: Editable type format, false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String aDefaultValue, ValueItemEnumDataType aType, boolean anIsEditable) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        // </editor-fold>
        this.initalize();
        this.dataType = aType;
        this.defaultValue = this.formatValue(aDefaultValue);
        this.isEditable = anIsEditable;
    }

    /**
     * Constructor
     *
     * @param anAllowedCharacters String with regex expression for allowed
     * characters (not allowed to be null)
     * @param anAllowedMatch String with regex expression for allowed match (not
     * allowed to be null)
     * @param aType Type
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ValueItemDataTypeFormat(String anAllowedCharacters, String anAllowedMatch, ValueItemEnumDataType aType) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anAllowedCharacters == null) {
            throw new IllegalArgumentException("anAllowedCharacters is not allowed to be null.");
        }
        if (anAllowedMatch == null) {
            throw new IllegalArgumentException("anAllowedMatch is not allowed to be null.");
        }

        // </editor-fold>
        this.initalize();
        this.dataType = aType;

        // <editor-fold defaultstate="collapsed" desc="Allowed characters">
        if (anAllowedCharacters.length() > 0) {
            try {
                this.allowedCharactersPattern = Pattern.compile(anAllowedCharacters);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("anAllowedCharacters is illegal.");
            }
        }
        this.allowedCharacters = anAllowedCharacters;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Allowed match">
        if (anAllowedMatch.length() > 0) {
            try {
                this.allowedMatchPattern = Pattern.compile(anAllowedMatch);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new IllegalArgumentException("allowedMatch is illegal.");
            }
        }
        this.allowedMatch = anAllowedMatch;

        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XML related constructors">
    /**
     * Constructor
     *
     * @param aXmlString XML representation of type format
     * @throws IllegalArgumentException Thrown when aXmlString is null/empty or
     * can not be read
     */
    public ValueItemDataTypeFormat(String aXmlString) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXmlString == null || aXmlString.isEmpty()) {
            throw new IllegalArgumentException("aXmlString is null/empty.");
        }

        // </editor-fold>
        this.initalize();
        try {
            if (!this.readXmlInformation(new SAXBuilder().build(new StringReader(aXmlString)).getRootElement())) {
                throw new IllegalArgumentException("Invalid XML string.");
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new IllegalArgumentException("Invalid XML string.");
        }
    }

    /**
     * Constructor
     *
     * @param aXmlElement XML element of type format
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemDataTypeFormat(Element aXmlElement) throws IllegalArgumentException {
        this.initalize();
        this.readXmlInformation(aXmlElement);
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private constructor">
    /**
     * Constructor
     *
     * @param aType Type
     * @param aDefaultValue Default value
     * @param aNumberOfDecimals Number of decimals
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @param aSelectionTexts Texts for selection
     * @param aForbiddenTexts Forbidden texts
     * @param aHasExclusiveSelectionTexts True: Selection texts are exclusive,
     * false: Otherwise
     * @param anIsEditable True: Editable format type, false: Otherwise
     * @param anIsFirstRowEditableOnly True: Value of first row in a matrix must
     * be the same in whole column. Only cell in first row is editable, all
     * cells of column in other rows are not editable. False: Otherwise
     * @param anIsUniqueDefault True: New default must be unique (unequal
     * compared to passed default values), false: Otherwise
     * @param anIsHighlighted True: Highlighted, false: Otherwise
     * @param anAllowedCharacters String with regex expression for allowed
     * characters (not allowed to be null)
     * @param anAllowedMatch String with regex expression for allowed match (not
     * allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    private ValueItemDataTypeFormat(
        ValueItemEnumDataType aType, 
        String aDefaultValue, 
        int aNumberOfDecimals, 
        double aMinimumValue, 
        double aMaximumValue, 
        String[] aSelectionTexts,
        String[] aForbiddenTexts, 
        boolean aHasExclusiveSelectionTexts, 
        boolean anIsEditable, 
        boolean anIsFirstRowEditableOnly, 
        boolean anIsUniqueDefault,
        boolean anIsHighlighted,
        String anAllowedCharacters,
        String anAllowedMatch
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfDecimals < 0) {
            throw new IllegalArgumentException("Number of decimals must be greater or equal to zero.");
        }
        if (aMaximumValue < aMinimumValue) {
            throw new IllegalArgumentException("aMaximumValue must be greater or equal to aMinimumValue.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aDefaultValue is not allowed to be null.");
        }
        if (anAllowedCharacters == null) {
            throw new IllegalArgumentException("anAllowedCharacters is not allowed to be null.");
        }
        if (anAllowedMatch == null) {
            throw new IllegalArgumentException("anAllowedMatch is not allowed to be null.");
        }

        // </editor-fold>
        this.dataType = aType;
        this.numberOfDecimals = aNumberOfDecimals;
        this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, aNumberOfDecimals);
        this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, aNumberOfDecimals);
        this.initializeSelectionTexts(aSelectionTexts);
        this.forbiddenTexts = aForbiddenTexts;
        // NOTE: No format conversion since private method
        this.defaultValue = aDefaultValue;
        this.hasExclusiveSelectionTexts = aHasExclusiveSelectionTexts;
        this.isEditable = anIsEditable;
        this.isFirstRowEditableOnly = anIsFirstRowEditableOnly;
        this.isUniqueDefault = anIsUniqueDefault;
        this.isHighlighted = anIsHighlighted;
        // <editor-fold defaultstate="collapsed" desc="Allowed characters">
        if (anAllowedCharacters.length() > 0) {
            this.allowedCharactersPattern = Pattern.compile(anAllowedCharacters);
        }
        this.allowedCharacters = anAllowedCharacters;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Allowed match">
        if (anAllowedMatch.length() > 0) {
            this.allowedMatchPattern = Pattern.compile(anAllowedMatch);
        }
        this.allowedMatch = anAllowedMatch;

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Check methods">
    /**
     * Checks representation of value
     *
     * @param aValue Value
     * @return True: Value corresponds to this type format, false: Otherwise
     */
    public boolean isValueAllowed(String aValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue == null) {
            return false;
        }

        // </editor-fold>
        switch (this.dataType) {
            case NUMERIC:
                return this.checkDoubleValueRepresentation(aValue);
            case NUMERIC_NULL:
                if (aValue.equals(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"))) {
                    return true;
                } else {
                    return this.checkDoubleValueRepresentation(aValue);
                }
            case TEXT:
                return this.isMatchText(aValue);
            case TEXT_EMPTY:
                return this.isMatchTextEmpty(aValue);
            case SELECTION_TEXT:
                return this.selectionTextsMap.containsKey(aValue);
            case TIMESTAMP:
                return this.timeUtilityMethods.isValidTimestampInStandardFormat(aValue);
            case TIMESTAMP_EMPTY:
                if (aValue.trim().isEmpty()) {
                    return true;
                } else {
                    return this.timeUtilityMethods.isValidTimestampInStandardFormat(aValue);
                }
            case DIRECTORY:
                if (aValue.isEmpty()) {
                    return true;
                } else {
                    return (new File(aValue)).isDirectory();
                }
            case FILE:
                if (aValue.isEmpty()) {
                    return true;
                } else {
                    return (new File(aValue)).isFile();
                }
            default:
                return true;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Is/has methods">
    /**
     * Checks if character is allowed
     *
     * @param aCharacter Character to be checked
     * @return True: Character is allowed, false: Otherwise
     */
    public boolean isCharacterAllowed(char aCharacter) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.allowedCharacters.isEmpty()) {
            return true;
        }

        // </editor-fold>
        return this.allowedCharactersPattern.matcher(String.valueOf(aCharacter)).matches();
    }

    /**
     * True: Text is in forbidden texts, false: Otherwise. NOTE: Comparison
     * IGNORES case!
     *
     * @param aText Text
     * @return True: Text is in forbidden texts, false: Otherwise
     */
    public boolean isInForbiddenTexts(String aText) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.dataType != ValueItemEnumDataType.TEXT_EMPTY) {
            return false;
        }

        // </editor-fold>
        return this.isInArrayIgnoreCase(aText, this.forbiddenTexts);
    }

    /**
     * True: Text is in selection texts, false: Otherwise
     *
     * @param aText Text
     * @return True: Text is in selection texts, false: Otherwise
     */
    public boolean hasSelectionText(String aText) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.dataType != ValueItemEnumDataType.SELECTION_TEXT) {
            return false;
        }
        // </editor-fold>
        return this.selectionTextsMap.containsKey(aText);
    }

    /**
     * True: Texts are in selection texts, false: Otherwise
     *
     * @param aTextArray Text array
     * @return True: Texts are in selection texts, false: Otherwise
     */
    public boolean hasSelectionTexts(String[] aTextArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.dataType != ValueItemEnumDataType.SELECTION_TEXT) {
            return false;
        }
        if (aTextArray == null || aTextArray.length < 1) {
            return false;
        }
        // </editor-fold>
        for (String tmpText : aTextArray) {
            if (!this.selectionTextsMap.containsKey(tmpText)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns matching protein backbone probe particle selection text if
     * available, i.e. if aGeneralProbe = "*PD1" then "GlyPD1" may be returned.
     *
     * @param aGeneralProbe General probe
     * @return Matching protein backbone probe particle selection text or null
     * if none is available
     */
    public String getMatchingProteinBackboneProbeParticleSelectionText(String aGeneralProbe) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aGeneralProbe == null || aGeneralProbe.isEmpty() || !aGeneralProbe.startsWith(ModelDefinitions.GENERAL_PROTEIN_BACKBONE_PROBE_START)) {
            return null;
        }
        // </editor-fold>
        String tmpProteinBackboneProbeEnding = aGeneralProbe.substring(ModelDefinitions.GENERAL_PROTEIN_BACKBONE_PROBE_START.length());
        for (String tmpSingleSelectionText : this.selectionTexts) {
            if (tmpSingleSelectionText.endsWith(tmpProteinBackboneProbeEnding)) {
                return tmpSingleSelectionText;
            }
        }
        return null;
    }

    /**
     * Checks if text matches allowedMatch regex and is not null/empty
     *
     * @param aText Text to be matched
     * @return True: Text matches allowedMatch regex or is null/empty, false:
     * Otherwise
     */
    public boolean isMatchText(String aText) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aText == null || aText.isEmpty()) {
            return false;
        }
        if (this.allowedMatch.isEmpty()) {
            return true;
        }

        // </editor-fold>
        return this.allowedMatchPattern.matcher(aText).matches();
    }

    /**
     * Checks if text matches allowedMatch regex, is not null and is not in
     * forbidden texts
     *
     * @param aText Text to be matched
     * @return True: Text matches allowedMatch regex, is not null and is not in
     * forbidden texts, false: Otherwise
     */
    public boolean isMatchTextEmpty(String aText) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aText == null) {
            return false;
        }
        if (aText.isEmpty()) {
            return true;
        }
        if (this.allowedMatch.isEmpty() && this.forbiddenTexts == null) {
            return true;
        }
        // </editor-fold>
        if (this.isInForbiddenTexts(aText)) {
            return false;
        } else {
            if (this.allowedMatch.isEmpty()) {
                return true;
            } else {
                return this.allowedMatchPattern.matcher(aText).matches();
            }
        }
    }

    /**
     * Checks if text is allowed (i.e. does not contain forbidden characters)
     *
     * @param aText Text to be checked
     * @return True: Text is allowed, false: Otherwise
     */
    public boolean hasTextAllowedCharacters(String aText) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.allowedCharacters.isEmpty()) {
            return true;
        }
        if (aText == null || aText.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.allowedCharactersPattern.matcher(aText).matches();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns clone of this instance
     *
     * @return Clone of this instance
     */
    public ValueItemDataTypeFormat getClone() {
        String[] tmpNewSelectionTexts = null;
        if (this.selectionTexts != null) {
            tmpNewSelectionTexts = this.selectionTexts.clone();
        }
        String[] tmpNewForbiddenTexts = null;
        if (this.forbiddenTexts != null) {
            tmpNewForbiddenTexts = this.forbiddenTexts.clone();
        }
        return 
            new ValueItemDataTypeFormat(
                this.dataType, 
                this.defaultValue, 
                this.numberOfDecimals, 
                this.minimumValue, 
                this.maximumValue, 
                tmpNewSelectionTexts, 
                tmpNewForbiddenTexts,
                this.hasExclusiveSelectionTexts, 
                this.isEditable, 
                this.isFirstRowEditableOnly, 
                this.isUniqueDefault,
                this.isHighlighted,
                this.allowedCharacters, 
                this.allowedMatch
            );
    }

    /**
     * Equals
     *
     * @param aTypeFormat ValueItemEnumDataType format
     * @return True: ValueItemEnumDataType formats are equal, false: otherwise
     */
    public boolean equals(ValueItemDataTypeFormat aTypeFormat) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTypeFormat == null) {
            return false;
        }
        // </editor-fold>
        if (this.dataType != aTypeFormat.getDataType()) {
            return false;
        }
        if (this.numberOfDecimals != aTypeFormat.getNumberOfDecimals()) {
            return false;
        }
        if (this.minimumValue != aTypeFormat.getMinimumValue()) {
            return false;
        }
        if (this.maximumValue != aTypeFormat.getMaximumValue()) {
            return false;
        }
        if (this.selectionTexts != null) {
            if (aTypeFormat.getSelectionTexts() == null) {
                return false;
            }
            String[] tmpSelectionTexts = aTypeFormat.getSelectionTexts();
            if (this.selectionTexts.length != tmpSelectionTexts.length) {
                return false;
            }
            for (int i = 0; i < this.selectionTexts.length; i++) {
                if (!this.selectionTexts[i].equals(tmpSelectionTexts[i])) {
                    return false;
                }
            }
        } else {
            if (aTypeFormat.getSelectionTexts() != null) {
                return false;
            }
        }
        if (this.forbiddenTexts != null) {
            if (aTypeFormat.getForbiddenTexts() == null) {
                return false;
            }
            String[] tmpForbiddenTexts = aTypeFormat.getForbiddenTexts();
            if (this.forbiddenTexts.length != tmpForbiddenTexts.length) {
                return false;
            }
            for (int i = 0; i < this.forbiddenTexts.length; i++) {
                if (!this.forbiddenTexts[i].equals(tmpForbiddenTexts[i])) {
                    return false;
                }
            }
        } else {
            if (aTypeFormat.getForbiddenTexts() != null) {
                return false;
            }
        }
        if (!this.defaultValue.equals(aTypeFormat.getDefaultValue())) {
            return false;
        }
        if (this.hasExclusiveSelectionTexts != aTypeFormat.hasExclusiveSelectionTexts()) {
            return false;
        }
        if (this.isEditable != aTypeFormat.isEditable()) {
            return false;
        }
        if (this.isFirstRowEditableOnly != aTypeFormat.isFirstRowEditableOnly()) {
            return false;
        }
        if (this.isUniqueDefault != aTypeFormat.isUniqueDefault()) {
            return false;
        }
        if (!this.allowedCharacters.equals(aTypeFormat.getAllowedCharacters())) {
            return false;
        }
        if (!this.allowedMatch.equals(aTypeFormat.getAllowedMatch())) {
            return false;
        }
        return true;
    }

    /**
     * Set minimum, maximum and default value
     * 
     * @param aMinimumValue Minimum value
     * @param aMaximumValue Maximum value
     * @param aDefaultValue Default value
     */
    public void setMinMaxDefaultValue(
        double aMinimumValue,
        double aMaximumValue,
        String aDefaultValue
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMaximumValue < aMinimumValue) {
            throw new IllegalArgumentException("Maximum value is smaller minimum value");
        }
        if (Double.valueOf(aDefaultValue) < aMinimumValue) {
            throw new IllegalArgumentException("Default value is smaller minimum value");
        }
        if (Double.valueOf(aDefaultValue) > aMaximumValue) {
            throw new IllegalArgumentException("Default value is greater maximum value");
        }
        // </editor-fold>
        this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, this.numberOfDecimals);
        this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, this.numberOfDecimals);
        this.defaultValue = this.formatValue(aDefaultValue);
    }
    
    /**
     * Converts data type to NUMERIC_NULL if type is NUMERIC or already 
     * NUMERIC_NULL
     * 
     * @return True: Data type is or is converted to NUMERIC_NULL, 
     * false: Data type is unchanged.
     */
    public boolean convertToNumericNull() {
        if (this.dataType == null) {
            return false;
        } else switch (this.dataType) {
            case NUMERIC:
                this.dataType = ValueItemEnumDataType.NUMERIC_NULL;
                return true;
            case NUMERIC_NULL:
                return true;
            default:
                return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Returns a XML element of this instance
     *
     * @return A XML element of this instance
     */
    public Element getAsXmlElement() {
        Element tmpRoot = new Element(ValueItemDataTypeFormatXmlName.TYPE_FORMAT);
        // IMPORTANT: Set version of this XML definition
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.VERSION).addContent("Version 1.0.0"));

        // NOTE: Order corresponds to methods readXmlInformationV...() und
        // initialize ()
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.TYPE).addContent(this.dataType.name()));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.NUMBER_OF_DECIMALS).addContent(String.valueOf(this.numberOfDecimals)));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.MINIMUM_VALUE).addContent(String.valueOf(this.minimumValue)));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.MAXIMUM_VALUE).addContent(String.valueOf(this.maximumValue)));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.DEFAULT_VALUE).addContent(this.defaultValue));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.HAS_EXCLUSIVE_SELECTION_TEXTS).addContent(String.valueOf(this.hasExclusiveSelectionTexts)));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.IS_EDITABLE).addContent(String.valueOf(this.isEditable)));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.IS_FIRST_ROW_VALUE_ONLY).addContent(String.valueOf(this.isFirstRowEditableOnly)));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.IS_UNIQUE_DEFAULT).addContent(String.valueOf(this.isUniqueDefault)));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.ALLOWED_CHARACTERS).addContent(this.allowedCharacters));
        tmpRoot.addContent(new Element(ValueItemDataTypeFormatXmlName.ALLOWED_MATCH).addContent(this.allowedMatch));
        this.addSelectionTextsToXmlElement(tmpRoot);
        this.addForbiddenTextsToXmlElement(tmpRoot);
        return tmpRoot;
    }

    /**
     * Creates an XML representation string of the ValueItemDataTypeFormat
     * instance
     *
     * @return An XML representation string of the ValueItemDataTypeFormat
     * instance
     */
    public String getAsXmlString() {
        Element tmpRoot = this.getAsXmlElement();
        XMLOutputter tmpOutputter = new XMLOutputter(Format.getPrettyFormat());
        Document tmpDocument = new Document();
        tmpDocument.setRootElement(tmpRoot);
        return tmpOutputter.outputString(tmpDocument);
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public Properties">
    // <editor-fold defaultstate="collapsed" desc="AllowedCharacters (get)">
    /**
     * String with regex expression for allowed characters
     *
     * @return String with regex expression for allowed characters
     */
    public String getAllowedCharacters() {
        return this.allowedCharacters;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AllowedMatch (get)">
    /**
     * String with regex expression for allowed match
     *
     * @return String with regex expression for allowed match
     */
    public String getAllowedMatch() {
        return this.allowedMatch;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DefaultValue (get/set)">
    /**
     * Default value
     *
     * @return The default value.
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Unique default value. NOTE: This method might fail in specific cases (see
     * code).
     *
     * @param anOtherValues Other values the returned default must not be equal
     * to
     * @return The unique default value.
     */
    public String getUniqueDefaultValue(String[] anOtherValues) {
        if (anOtherValues == null || anOtherValues.length == 0) {
            return this.defaultValue;
        }
        boolean tmpIsMatch = true;
        int tmpCounter = 0;
        String tmpUniqueDefaultValue = this.defaultValue;
        while (tmpIsMatch) {
            tmpIsMatch = false;
            for (String tmpOtherDefaultValue : anOtherValues) {
                if (tmpOtherDefaultValue.equals(tmpUniqueDefaultValue)) {
                    tmpIsMatch = true;
                    break;
                }
            }
            if (tmpIsMatch) {
                tmpUniqueDefaultValue = this.defaultValue + String.valueOf(++tmpCounter);
                int tmpSubtract = 1;
                while (!this.isMatchText(tmpUniqueDefaultValue) && tmpSubtract < this.defaultValue.length()) {
                    tmpUniqueDefaultValue = this.defaultValue.substring(0, this.defaultValue.length() - tmpSubtract++) + String.valueOf(tmpCounter);
                }
            }
        }
        return tmpUniqueDefaultValue;
    }

    /**
     * Default value
     *
     * @param aDefaultValue Default value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setDefaultValue(String aDefaultValue) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aValue is not allowed to be null.");
        }
        if (this.dataType == ValueItemEnumDataType.SELECTION_TEXT) {
            if (!this.selectionTextsMap.containsKey(aDefaultValue)) {
                throw new IllegalArgumentException("aDefaultValue is not in aSelectionTexts.");
            }
        }
        // </editor-fold>
        this.defaultValue = this.formatValue(aDefaultValue);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Default value and selection texts (set)">
    /**
     * Default value and texts for selection
     *
     * @param aDefaultValue Default value
     * @param aSelectionTexts Texts for selection
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setDefaultValueAndSelectionTexts(String aDefaultValue, String[] aSelectionTexts) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.dataType != ValueItemEnumDataType.SELECTION_TEXT) {
            throw new IllegalArgumentException("Type does not contain selection texts.");
        }
        if (aDefaultValue == null) {
            throw new IllegalArgumentException("aValue is not allowed to be null.");
        }
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            throw new IllegalArgumentException("aSelectionTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single selection text is not allowed to be null/empty.");
            }
        }
        boolean tmpIsMatch = false;
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText.equals(aDefaultValue)) {
                tmpIsMatch = true;
                break;
            }
        }
        if (!tmpIsMatch) {
            throw new IllegalArgumentException("this.defaultValue is not in aSelectionTexts.");
        }

        // </editor-fold>
        this.defaultValue = this.formatValue(aDefaultValue);
        this.initializeSelectionTexts(aSelectionTexts);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Editable (get/set)">
    /**
     * True: Editable type format, false: Otherwise
     *
     * @return True: Editable type format, false: Otherwise
     */
    public boolean isEditable() {
        return this.isEditable;
    }

    /**
     * True: Editable type format, false: Otherwise
     *
     * @param aValue Value
     */
    public void setEditable(boolean aValue) {
        this.isEditable = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ExclusiveSelectionTexts (get)">
    /**
     * True: Selection texts are exclusive, false: Otherwise
     *
     * @return True: Selection texts are exclusive, false: Otherwise
     */
    public boolean hasExclusiveSelectionTexts() {
        return this.hasExclusiveSelectionTexts;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FirstRowEditableOnly (get/set)">
    /**
     * True: Value of first row in a matrix must be the same in whole column.
     * Only cell in first row is editable, all cells of column in other rows are
     * not editable. False: Otherwise
     *
     * @return True: Value of first row in a matrix must be the same in whole
     * column. Only cell in first row is editable, all cells of column in other
     * rows are not editable. False: Otherwise
     */
    public boolean isFirstRowEditableOnly() {
        return this.isFirstRowEditableOnly;
    }

    /**
     * True: Value of first row in a matrix must be the same in whole column.
     * Only cell in first row is editable, all cells of column in other rows are
     * not editable. False: Otherwise
     *
     * @param aValue Value
     */
    public void setFirstRowEditableOnly(boolean aValue) {
        this.isFirstRowEditableOnly = aValue;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ForbiddenTexts (get/set)">
    /**
     * Forbidden texts
     *
     * @return Forbidden texts
     */
    public String[] getForbiddenTexts() {
        return this.forbiddenTexts;
    }

    /**
     * Forbidden texts
     *
     * @param aForbiddenTexts Forbidden texts
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setForbiddenTexts(String[] aForbiddenTexts) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.dataType != ValueItemEnumDataType.TEXT_EMPTY) {
            throw new IllegalArgumentException("Type is not TEXT_EMPTY.");
        }
        if (aForbiddenTexts == null || aForbiddenTexts.length == 0) {
            throw new IllegalArgumentException("aForbiddenTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aForbiddenTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single forbidden text is not allowed to be null/empty.");
            }
        }

        // </editor-fold>
        this.forbiddenTexts = aForbiddenTexts;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Highlight (get/set)">
    /**
     * Returns highlight
     *
     * @return True: Highlight, false: Otherwise
     */
    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    /**
     * Sets highlight
     * 
     * @param aValue Value for highlight
     */
    public void setHightlight(boolean aValue) {
        this.isHighlighted = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Integer (get)">
    /**
     * Returns if value item type represents an integer number
     *
     * @return True: Value item type represents an integer number, false:
     * Otherwise
     */
    public boolean isIntegerNumber() {
        return (this.dataType == ValueItemEnumDataType.NUMERIC || this.dataType == ValueItemEnumDataType.NUMERIC_NULL) && this.numberOfDecimals == 0;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MaximumValue (get/set)">
    /**
     * Maximum value
     *
     * @return Maximum value
     */
    public double getMaximumValue() {
        return this.maximumValue;
    }

    /**
     * Maximum value. NOTE: Passed value is first rounded to number of decimals.
     *
     * @param aMaximumValue Maximum value
     * @throws IllegalArgumentException Thrown if aMaximumValue is less than
     * existing minimum value
     */
    public void setMaximumValue(double aMaximumValue) throws IllegalArgumentException {
        if (ModelUtils.roundDoubleValue(aMaximumValue, this.numberOfDecimals) >= this.minimumValue) {
            this.maximumValue = ModelUtils.roundDoubleValue(aMaximumValue, this.numberOfDecimals);
        } else {
            throw new IllegalArgumentException("Maximum value '" + String.valueOf(aMaximumValue) + "' must be greater/equal than/to minimum value '" + String.valueOf(this.minimumValue) + "'");
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MaximumValueRepresentation (get)">
    /**
     * Maximum value representation
     *
     * @return Maximum value representation
     */
    public String getMaximumValueRepresentation() {
        if (this.maximumValue == Double.MAX_VALUE) {
            return ModelMessage.get("ValueItemDataTypeFormat.NotDefined");
        } else {
            return this.stringUtilityMethods.formatDoubleValue(this.maximumValue, this.numberOfDecimals);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MinimumValue (get/set)">
    /**
     * Minimum value
     *
     * @return Minimum value
     */
    public double getMinimumValue() {
        return this.minimumValue;
    }

    /**
     * Minimum value. NOTE: Passed value is first rounded to number of decimals.
     *
     * @param aMinimumValue Minimum value
     * @throws IllegalArgumentException Thrown if aMinimumValue is greater than
     * existing maximum value
     */
    public void setMinimumValue(double aMinimumValue) throws IllegalArgumentException {
        if (ModelUtils.roundDoubleValue(aMinimumValue, this.numberOfDecimals) <= this.maximumValue) {
            this.minimumValue = ModelUtils.roundDoubleValue(aMinimumValue, this.numberOfDecimals);
        } else {
            throw new IllegalArgumentException("Minimum value '" + String.valueOf(aMinimumValue) + "' must be less/equal than/to maximum value '" + String.valueOf(this.maximumValue) + "'");
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MinimumValueRepresentation (get)">
    /**
     * Minimum value representation
     *
     * @return Minimum value representation
     */
    public String getMinimumValueRepresentation() {
        if (this.minimumValue == -Double.MAX_VALUE) {
            return ModelMessage.get("ValueItemDataTypeFormat.NotDefined");
        } else {
            return this.stringUtilityMethods.formatDoubleValue(this.minimumValue, this.numberOfDecimals);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NumberOfDecimals (get)">
    /**
     * Number of decimals
     *
     * @return Number of decimals
     */
    public int getNumberOfDecimals() {
        return this.numberOfDecimals;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SelectionTexts (get/set)">
    /**
     * Returns first none default selection text or null if none exists
     * 
     * @return First none default selection text or null if none exists
     */
    public String getFirstNoneDefaultSelectionText() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.dataType != ValueItemEnumDataType.SELECTION_TEXT) {
            return null;
        }
        if (this.selectionTexts == null || this.selectionTexts.length == 0) {
            return null;
        }
        if (this.defaultValue == null || this.defaultValue.isEmpty()) {
            return null;
        }

        // </editor-fold>
        for (String tmpSingleSelectionText : this.selectionTexts) {
            if (!this.defaultValue.equals(tmpSingleSelectionText)) {
                return tmpSingleSelectionText;
            }
        }
        return null;
    }

    /**
     * Texts for selection
     *
     * @return Texts for selection
     */
    public String[] getSelectionTexts() {
        return this.selectionTexts;
    }

    /**
     * Texts for selection
     *
     * @param aSelectionTexts Texts for selection
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setSelectionTexts(String[] aSelectionTexts) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.dataType != ValueItemEnumDataType.SELECTION_TEXT) {
            throw new IllegalArgumentException("Type does not contain selection texts.");
        }
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            throw new IllegalArgumentException("aSelectionTexts must be defined.");
        }
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText == null || tmpSingleSelectionText.isEmpty()) {
                throw new IllegalArgumentException("Single selection text is not allowed to be null/empty.");
            }
        }
        boolean tmpIsMatch = false;
        for (String tmpSingleSelectionText : aSelectionTexts) {
            if (tmpSingleSelectionText.equals(this.defaultValue)) {
                tmpIsMatch = true;
                break;
            }
        }
        if (!tmpIsMatch) {
            this.defaultValue = this.formatValue(aSelectionTexts[0]);
        }
        // </editor-fold>
        this.initializeSelectionTexts(aSelectionTexts);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="UniqueDefault (get)">
    /**
     * True: New default must be unique (unequal compared to passed default
     * values), false: Otherwise
     *
     * @return True: New default must be unique (unequal compared to passed
     * default values), false: Otherwise
     */
    public boolean isUniqueDefault() {
        return this.isUniqueDefault;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ValueItemEnumDataType (get)">
    /**
     * Type
     *
     * @return Type
     */
    public ValueItemEnumDataType getDataType() {
        return this.dataType;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="Check methods">
    /**
     * Checks string representation of a double value between (inclusive)
     * aMinimum and (inclusive) aMaximum with a number of decimals which is
     * equal to aNumberOfDecimals.
     *
     * @param aDoubleValueRepresentation Text field
     * @param aMinimum Minimum value
     * @param aMaximum Maximum value
     * @param aNumberOfDecimals Number of decimals
     * @return true: String representation of a double value is between aMinimum
     * and aMaximum with a aNumberOfDecimals decimals, false: Otherwise
     */
    private boolean checkDoubleValueRepresentation(String aDoubleValueRepresentation, double aMinimum, double aMaximum, int aNumberOfDecimals) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDoubleValueRepresentation == null || aDoubleValueRepresentation.isEmpty()) {
            return false;
        }
        if (aMinimum > aMaximum) {
            return false;
        }
        if (aNumberOfDecimals < 0) {
            return false;
        }

        // </editor-fold>
        char[] tmpChars = aDoubleValueRepresentation.toCharArray();
        StringBuilder tmpBuffer = new StringBuilder(tmpChars.length);
        boolean tmpDecimalPointOccured = false;
        int tmpDecimalCounter = 0;
        for (int i = 0; i < tmpChars.length; i++) {
            boolean tmpIsCharacterHandled = false;
            // Minus sign must be in first position
            if (tmpBuffer.length() == 0 && tmpChars[i] == '-') {
                tmpBuffer.append(tmpChars[i]);
                tmpIsCharacterHandled = true;
            }
            // Decimal point is allowed to occur only once
            if (tmpChars[i] == '.' && !tmpDecimalPointOccured && aNumberOfDecimals > 0) {
                tmpDecimalPointOccured = true;
                tmpBuffer.append(tmpChars[i]);
                tmpIsCharacterHandled = true;
            }
            if (Character.isDigit(tmpChars[i])) {
                tmpBuffer.append(tmpChars[i]);
                if (tmpDecimalPointOccured) {
                    tmpDecimalCounter++;
                }
                tmpIsCharacterHandled = true;
            }
            if (!tmpIsCharacterHandled) {
                return false;
            }
        }
        if (tmpBuffer.length() > 0) {
            if (this.stringUtilityMethods.isDoubleValue(tmpBuffer.toString())) {
                if (tmpDecimalCounter > aNumberOfDecimals) {
                    return false;
                }
                double tmpDoubleValue = Double.valueOf(tmpBuffer.toString());
                if (tmpDoubleValue < aMinimum || tmpDoubleValue > aMaximum) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks string representation of a double value according to type format
     * of value item.
     *
     * @param aDoubleValueRepresentation Text field
     * @return true: String representation of a double value is consistent with
     * type format of value item, false: Otherwise
     */
    private boolean checkDoubleValueRepresentation(String aDoubleValueRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDoubleValueRepresentation == null || aDoubleValueRepresentation.isEmpty() || (this.dataType != ValueItemEnumDataType.NUMERIC && this.dataType != ValueItemEnumDataType.NUMERIC_NULL)) {
            return false;
        }

        // </editor-fold>
        return this.checkDoubleValueRepresentation(aDoubleValueRepresentation, this.minimumValue, this.maximumValue, this.numberOfDecimals);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Initialisation related methods">
    /**
     * Initializes type format
     */
    private void initalize() {
        this.dataType = ValueItemEnumDataType.NUMERIC;
        this.numberOfDecimals = 0;
        this.minimumValue = -Double.MAX_VALUE;
        this.maximumValue = Double.MAX_VALUE;
        this.selectionTexts = null;
        this.selectionTextsMap = null;
        this.forbiddenTexts = null;
        // NOTE: this.defaultValue is NOT allowed to be null
        this.defaultValue = "";
        this.hasExclusiveSelectionTexts = false;
        this.isEditable = true;
        this.isFirstRowEditableOnly = false;
        this.isUniqueDefault = false;
        // NOTE: this.allowedCharacters is NOT allowed to be null
        this.allowedCharacters = "";
        // NOTE: this.allowedMatch is NOT allowed to be null
        this.allowedMatch = "";
        this.isHighlighted = false;
    }

    /**
     * Initialises selection text array and map.
     * 
     * @param aSelectionTexts Selection texts
     */
    private void initializeSelectionTexts(String[] aSelectionTexts) {
        if (aSelectionTexts == null || aSelectionTexts.length == 0) {
            this.selectionTexts = null;
            this.selectionTextsMap = null;
        } else {
            this.selectionTexts = aSelectionTexts;
            this.selectionTextsMap = new HashMap<>(this.selectionTexts.length);
            for (String singleSelectionText : aSelectionTexts) {
                this.selectionTextsMap.putIfAbsent(singleSelectionText, singleSelectionText);
            }
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Miscellaneous methods">
    /**
     * Formats value according to type
     *
     * @param aValue Value
     * @return Formatted value or null if aValue was null
     */
    private String formatValue(String aValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue == null || aValue.isEmpty()) {
            return "";
        }
        // </editor-fold>
        switch (this.dataType) {
            case NUMERIC:
                return this.stringUtilityMethods.formatDoubleValue(aValue, this.numberOfDecimals);
            case NUMERIC_NULL:
                if (!aValue.equals(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"))) {
                    return this.stringUtilityMethods.formatDoubleValue(aValue, this.numberOfDecimals);
                } else {
                    return aValue;
                }
            default:
                return aValue;
        }
    }

    /**
     * Returns if aValue is in aStringArray (with sequential search)
     *
     * @param aString String value
     * @param aStringArray String array
     * @return True: aValue is in aStringArray, false: Otherwise
     */
    private boolean isInArray(String aValue, String[] aStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0) {
            return false;
        }
        if (aValue == null || aValue.isEmpty()) {
            return false;
        }

        // </editor-fold>
        for (String tmpSingleString : aStringArray) {
            if (aValue.equals(tmpSingleString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if aValue is in aStringArray
     *
     * @param aString String value
     * @param aStringArray String array
     * @return True: aValue is in aStringArray, false: Otherwise
     */
    private boolean isInArrayIgnoreCase(String aValue, String[] aStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0) {
            return false;
        }
        if (aValue == null || aValue.isEmpty()) {
            return false;
        }

        // </editor-fold>
        for (String tmpSingleString : aStringArray) {
            if (aValue.equalsIgnoreCase(tmpSingleString)) {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="XML related methods">
    /**
     * Adds selection texts to XML element
     *
     * @param anElement XML element to add the selection texts to
     */
    private void addSelectionTextsToXmlElement(Element anElement) {
        this.addTextArrayToXmlElement(anElement, this.selectionTexts, ValueItemDataTypeFormatXmlName.SELECTION_TEXTS, ValueItemDataTypeFormatXmlName.SELECTION_SINGLE_TEXT);
    }

    /**
     * Reads selection texts
     *
     * @param anElement The XML element containing the selection texts
     * @return Array of selection texts or null if XML element does not contain
     * selection texts
     */
    private String[] getSelectionTextsFromXml(Element anElement) {
        return this.getTextArrayFromXml(anElement, ValueItemDataTypeFormatXmlName.SELECTION_TEXTS);
    }

    /**
     * Adds forbidden texts to XML element
     *
     * @param anElement XML element to add the forbidden texts to
     */
    private void addForbiddenTextsToXmlElement(Element anElement) {
        this.addTextArrayToXmlElement(anElement, this.forbiddenTexts, ValueItemDataTypeFormatXmlName.FORBIDDEN_TEXTS, ValueItemDataTypeFormatXmlName.FORBIDDEN_SINGLE_TEXT);
    }

    /**
     * Reads forbidden texts
     *
     * @param anElement The XML element containing the forbidden texts
     * @return Array of forbidden texts or null if XML element does not contain
     * forbidden texts
     */
    private String[] getForbiddenTextsFromXml(Element anElement) {
        return this.getTextArrayFromXml(anElement, ValueItemDataTypeFormatXmlName.FORBIDDEN_TEXTS);
    }

    /**
     * Adds text array to XML element
     *
     * @param anElement XML element to add the text array to
     * @param aTextArray Text array
     * @param aTextArrayXmlName XML name for text array
     * @param aSingleTextXmlName XML name for single text
     */
    private void addTextArrayToXmlElement(Element anElement, String[] aTextArray, String aTextArrayXmlName, String aSingleTextXmlName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTextArray == null || aTextArray.length == 0 || anElement == null) {
            return;
        }
        if (aTextArrayXmlName == null || aTextArrayXmlName.isEmpty()) {
            return;
        }
        if (aSingleTextXmlName == null || aSingleTextXmlName.isEmpty()) {
            return;
        }

        // </editor-fold>
        Element tmpTextArrayElement = new Element(aTextArrayXmlName);
        for (String tmpSingleText : aTextArray) {
            tmpTextArrayElement.addContent(new Element(aSingleTextXmlName).addContent(tmpSingleText));
        }
        anElement.addContent(tmpTextArrayElement);
    }

    /**
     * Reads text array
     *
     * @param anElement The XML element containing the text array
     * @param aTextArrayXmlName XML name of text array
     * @return Text array or null if XML element does not contain text array
     */
    private String[] getTextArrayFromXml(Element anElement, String aTextArrayXmlName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return null;
        }
        if (aTextArrayXmlName == null || aTextArrayXmlName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        Element tmpTextArrayElement = anElement.getChild(aTextArrayXmlName);
        if (tmpTextArrayElement == null) {
            return null;
        }
        List<?> tmpSingleTextElementList = tmpTextArrayElement.getChildren();
        String[] tmpTextArray = new String[tmpSingleTextElementList.size()];
        for (int i = 0; i < tmpSingleTextElementList.size(); i++) {
            tmpTextArray[i] = ((Element) tmpSingleTextElementList.get(i)).getText();
        }
        return tmpTextArray;
    }

    /**
     * Reads XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformation(Element anElement) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }
        if (anElement.getChild(ValueItemDataTypeFormatXmlName.VERSION) == null) {
            return false;
        }

        // </editor-fold>
        String tmpVersion = anElement.getChild(ValueItemDataTypeFormatXmlName.VERSION).getText();
        if (tmpVersion == null || tmpVersion.isEmpty()) {
            return false;
        }

        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
        if (tmpVersion.equals("Version 1.0.0")) {
            return this.readXmlInformationV_1_0_0(anElement);
        }

        // </editor-fold>
        return false;
    }

    /**
     * Reads versioned XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformationV_1_0_0(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }

        // </editor-fold>
        try {
            // NOTE: This order corresponds to order in method initialize()
            this.dataType = ValueItemEnumDataType.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.TYPE).getText());
            this.numberOfDecimals = Integer.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.NUMBER_OF_DECIMALS).getText());
            this.minimumValue = Double.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.MINIMUM_VALUE).getText());
            this.maximumValue = Double.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.MAXIMUM_VALUE).getText());
            this.initializeSelectionTexts(this.getSelectionTextsFromXml(anElement));
            this.forbiddenTexts = this.getForbiddenTextsFromXml(anElement);
            this.defaultValue = anElement.getChild(ValueItemDataTypeFormatXmlName.DEFAULT_VALUE).getText();
            this.hasExclusiveSelectionTexts = Boolean.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.HAS_EXCLUSIVE_SELECTION_TEXTS).getText());
            this.isEditable = Boolean.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.IS_EDITABLE).getText());
            this.isFirstRowEditableOnly = Boolean.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.IS_FIRST_ROW_VALUE_ONLY).getText());
            this.isUniqueDefault = Boolean.valueOf(anElement.getChild(ValueItemDataTypeFormatXmlName.IS_UNIQUE_DEFAULT).getText());
            this.allowedCharacters = anElement.getChild(ValueItemDataTypeFormatXmlName.ALLOWED_CHARACTERS).getText();
            if (this.allowedCharacters.length() > 0) {
                this.allowedCharactersPattern = Pattern.compile(this.allowedCharacters);
            }
            this.allowedMatch = anElement.getChild(ValueItemDataTypeFormatXmlName.ALLOWED_MATCH).getText();
            if (this.allowedMatch.length() > 0) {
                this.allowedMatchPattern = Pattern.compile(this.allowedMatch);
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
