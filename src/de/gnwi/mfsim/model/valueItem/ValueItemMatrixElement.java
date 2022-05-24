/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2022  Achim Zielesny (achim.zielesny@googlemail.com)
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
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.io.StringReader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Element of matrix that consists of a string value and a corresponding type format for value items
 *
 * @author Achim Zielesny
 */
public class ValueItemMatrixElement {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Value
     */
    private String value;

    /**
     * Protein data
     */
    private String proteinData;

    /**
     * ValueItemEnumDataType format
     */
    private ValueItemDataTypeFormat typeFormat;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param aValue Value
     * @param aTypeFormat ValueItemEnumDataType Format
     */
    public ValueItemMatrixElement(String aValue, ValueItemDataTypeFormat aTypeFormat) {
        this.proteinData = "";
        this.setValueAndTypeFormat(aValue, aTypeFormat);
    }

    /**
     * Constructor
     *
     * @param aTypeFormat ValueItemEnumDataType Format
     */
    public ValueItemMatrixElement(ValueItemDataTypeFormat aTypeFormat) {
        this.proteinData = "";
        this.setValueAndTypeFormat(aTypeFormat.getDefaultValue(), aTypeFormat);
    }

    /**
     * Constructor
     *
     * @param aXmlString XML representation of matrix element
     * @throws IllegalArgumentException Thrown when aXmlString is null/empty or can not be read
     */
    public ValueItemMatrixElement(String aXmlString) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXmlString == null || aXmlString.isEmpty()) {
            throw new IllegalArgumentException("aXmlString is null/empty.");
        }

        // </editor-fold>
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
     * @param aXmlElement The XML element for this instance
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemMatrixElement(Element aXmlElement) throws IllegalArgumentException {
        this.readXmlInformation(aXmlElement);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private constructor">
    /**
     * Private constructor. No checks are performed.
     *
     * @param aValue Value
     * @param aProteinData Protein data
     * @param aTypeFormat ValueItemEnumDataType Format
     */
    private ValueItemMatrixElement(String aValue, String aProteinData, ValueItemDataTypeFormat aTypeFormat) {
        this.proteinData = aProteinData;
        this.setValueAndTypeFormat(aValue, aTypeFormat);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="Miscellaneous methods">

    /**
     * Clones this instance
     *
     * @return Clone of this instance
     */
    public ValueItemMatrixElement getClone() {
        return new ValueItemMatrixElement(this.value, this.proteinData, this.typeFormat.getClone());
    }

    /**
     * Equals
     *
     * @param aMatrixElement Matrix element
     * @return True: Matrix elements are equal, false: otherwise
     */
    public boolean equals(ValueItemMatrixElement aMatrixElement) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrixElement == null) {
            return false;
        }

        // </editor-fold>
        if (!this.value.equals(aMatrixElement.getValue())) {
            return false;
        }
        if (!this.proteinData.equals(aMatrixElement.getProteinData())) {
            return false;
        }
        if (!this.typeFormat.equals(aMatrixElement.getTypeFormat())) {
            return false;
        }
        return true;
    }

    /**
     * Formats this.value according to this.typeFormat
     */
    public void formatValue() {
        this.value = this.formatValue(this.value);
    }

    /**
     * Formats value according to this.typeFormat
     *
     * @param aValue Value
     * @return Formatted value
     */
    public String formatValue(String aValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue == null || aValue.isEmpty()) {
            return aValue;
        }

        // </editor-fold>
        switch (this.typeFormat.getDataType()) {
            case NUMERIC:
                // <editor-fold defaultstate="collapsed" desc="NUMERIC">
                return this.stringUtilityMethods.formatDoubleValue(aValue, this.typeFormat.getNumberOfDecimals());

            // </editor-fold>
            case NUMERIC_NULL:
                // <editor-fold defaultstate="collapsed" desc="NUMERIC_NULL">
                if (!aValue.equals(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"))) {
                    return this.stringUtilityMethods.formatDoubleValue(aValue, this.typeFormat.getNumberOfDecimals());
                } else {
                    return aValue;
                }

            // </editor-fold>
            default:
                // <editor-fold defaultstate="collapsed" desc="Default">
                return aValue;

            // </editor-fold>
        }
    }

    /**
     * Returns if value has numeric null value
     *
     * @return True: Value has numeric null value, false: Otherwise
     */
    public boolean hasNumericNullValue() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.value == null || this.value.isEmpty()) {
            return false;
        }
        // </editor-fold>
        switch (this.typeFormat.getDataType()) {
            case NUMERIC:
                // <editor-fold defaultstate="collapsed" desc="NUMERIC">
                return false;
            // </editor-fold>
            case NUMERIC_NULL:
                // <editor-fold defaultstate="collapsed" desc="NUMERIC_NULL">
                return this.value.equals(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"));
            // </editor-fold>
            default:
                // <editor-fold defaultstate="collapsed" desc="Default">
                return false;
            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="XML related methods">
    /**
     * Returns a XML element of this instance
     *
     * @return A XML element of this instance
     */
    public Element getAsXmlElement() {
        Element tmpRoot = new Element(ValueItemMatrixElementXmlName.MATRIX_ELEMENT);
        // IMPORTANT: Set version of this XML definition
        tmpRoot.addContent(new Element(ValueItemMatrixElementXmlName.VERSION).addContent("Version 1.0.0"));
        tmpRoot.addContent(new Element(ValueItemMatrixElementXmlName.VALUE).addContent(this.value));
        tmpRoot.addContent(new Element(ValueItemMatrixElementXmlName.PROTEIN_DATA).addContent(this.proteinData));
        tmpRoot.addContent(this.typeFormat.getAsXmlElement());
        return tmpRoot;
    }

    /**
     * Creates an XML representation string of this instance
     *
     * @return An XML representation string of this instance
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
    // <editor-fold defaultstate="collapsed" desc="Value (get/set)">
    /**
     * Value
     *
     * @return Value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Formatted value
     *
     * @return Formatted value
     */
    public String getFormattedValue() {
        return this.formatValue(this.value);
    }

    /**
     * Value
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     * @return True: Value was changed, false: Otherwise
     */
    public boolean setValue(String aValue) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue == null) {
            throw new IllegalArgumentException("aValue is not allowed to be null.");
        }
        if (this.typeFormat.hasExclusiveSelectionTexts()) {
            if (!aValue.equals(this.typeFormat.getDefaultValue())) {
                throw new IllegalArgumentException("Exclusive selection text type format is invalid.");
            }
        }
        // </editor-fold>
        String tmpNewValue = this.formatValue(aValue);
        if (this.value.equals(tmpNewValue)) {
            return false;
        } else {
            this.value = tmpNewValue;
            return true;
        }
    }

    /**
     * Sets value without any additional format-change procedure
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setValueWithoutFormat(String aValue) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue == null) {
            throw new IllegalArgumentException("aValue is not allowed to be null.");
        }
        if (this.typeFormat.hasExclusiveSelectionTexts()) {
            if (!aValue.equals(this.typeFormat.getDefaultValue())) {
                throw new IllegalArgumentException("Exclusive selection text type format is invalid.");
            }
        }

        // </editor-fold>
        this.value = aValue;
        // Do NOT use this.formatValue();
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="ProteinData (get/set)">
    /**
     * Protein data
     *
     * @return Protein data (may be empty)
     */
    public String getProteinData() {
        return this.proteinData;
    }

    /**
     * Protein data
     *
     * @param aProteinData Protein data (may be null then conversion to empty string is performed)
     * @return True: Protein data changed, false: Otherwise
     */
    public boolean setProteinData(String aProteinData) {
        if (aProteinData == null) {
            if (this.proteinData.isEmpty()) {
                return false;
            } else {
                this.proteinData = "";
                return true;
            }
        } else {
            if (this.proteinData.equals(aProteinData)) {
                return false;
            } else {
                this.proteinData = aProteinData;
                return true;
            }
        }
    }

    /**
     * True: ValueItemMatrixElement contains protein data, false: Otherwise
     *
     * @return True: ValueItemMatrixElement contains protein data, false: Otherwise
     */
    public boolean hasProteinData() {
        return !this.proteinData.isEmpty();
    }

    /**
     * Removes protein data
     */
    public void removeProteinData() {
        this.proteinData = "";
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="ValueItemDataTypeFormat (get/set)">
    /**
     * ValueItemEnumDataType format
     *
     * @return ValueItemEnumDataType format
     */
    public ValueItemDataTypeFormat getTypeFormat() {
        return this.typeFormat;
    }

    /**
     * ValueItemEnumDataType format
     *
     * @param aTypeFormat ValueItemEnumDataType format
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setTypeFormat(ValueItemDataTypeFormat aTypeFormat) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTypeFormat == null) {
            throw new IllegalArgumentException("aTypeFormat is not allowed to be null.");
        }
        if (aTypeFormat.hasExclusiveSelectionTexts()) {
            if (!this.value.equals(aTypeFormat.getDefaultValue())) {
                throw new IllegalArgumentException("Exclusive selection text type format is invalid.");
            }
        }

        // </editor-fold>
        this.typeFormat = aTypeFormat;
        this.value = this.formatValue(this.value);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Value and type format (set)">
    /**
     * Sets value and type format
     *
     * @param aValue Value
     * @param aTypeFormat ValueItemEnumDataType Format
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void setValueAndTypeFormat(String aValue, ValueItemDataTypeFormat aTypeFormat) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue == null) {
            throw new IllegalArgumentException("aValue is not allowed to be null.");
        }
        if (aTypeFormat == null) {
            throw new IllegalArgumentException("aTypeFormat is not allowed to be null.");
        }
        if (aTypeFormat.hasExclusiveSelectionTexts()) {
            if (!aValue.equals(aTypeFormat.getDefaultValue())) {
                throw new IllegalArgumentException("Exclusive selection text type format is invalid.");
            }
        }

        // </editor-fold>
        // NOTE: Type format must be set FIRST (because it is used in method formatValue())
        this.typeFormat = aTypeFormat;
        this.value = this.formatValue(aValue);
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="XML related methods">
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
        if (anElement.getChild(ValueItemMatrixElementXmlName.VERSION) == null) {
            return false;
        }

        // </editor-fold>
        String tmpVersion = anElement.getChild(ValueItemMatrixElementXmlName.VERSION).getText();
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
    private boolean readXmlInformationV_1_0_0(Element anElement) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }

        // </editor-fold>
        try {
            this.value = anElement.getChild(ValueItemMatrixElementXmlName.VALUE).getText();
            this.proteinData = anElement.getChild(ValueItemMatrixElementXmlName.PROTEIN_DATA).getText();
            this.typeFormat = new ValueItemDataTypeFormat(anElement.getChild(ValueItemDataTypeFormatXmlName.TYPE_FORMAT));
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
