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

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainerXmlName;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType.COMPARTMENT_CONTAINER;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType.FLEXIBLE_MATRIX;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType.MATRIX;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType.OBJECT;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType.SCALAR;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType.VECTOR;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.DIRECTORY;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.FILE;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.MOLECULAR_STRUCTURE;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.MONOMER_STRUCTURE;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.NUMERIC;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.NUMERIC_NULL;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.SELECTION_TEXT;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.TEXT;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.TEXT_EMPTY;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.TIMESTAMP;
import static de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType.TIMESTAMP_EMPTY;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Value item for ValueItemContainer
 *
 * @author Neumann, Zielesny
 */
public class ValueItem extends ChangeNotifier implements Comparable<ValueItem>, ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="General hints">
    // A matrix allows ONLY one column with exclusive selection texts. This is NOT checked.
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility string methods
     */
    private StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();
    
    /**
     * Basic type
     */
    private ValueItemEnumBasicType basicType;

    /**
     * Name of block
     */
    private String blockName;

    /**
     * Change information
     */
    private ChangeInformation changeInformation;

    /**
     * Column with constant sum of numerical values for an insert operation or
     * -1 (then not defined)
     */
    private int constantSumColumn;

    /**
     * Current row of matrix
     */
    private int currentRow;

    /**
     * Current column of matrix
     */
    private int currentColumn;

    /**
     * Description
     */
    private String description;

    /**
     * Display name
     */
    private String displayName;

    /**
     * Error
     */
    private String error;

    /**
     * True: Change occurred after last call of initializeChangeDetection(),
     * false: Otherwise
     */
    private boolean hasChangeDetected;

    /**
     * Hint
     */
    private String hint;

    /**
     * File type ending for data type FILE
     */
    private String fileTypeEnding;

    /**
     * Acitivity status of value item: True: Active, false: Inactive
     */
    private boolean isActive;

    /**
     * True: Value item is displayed, false: Otherwise (i.e. it is excluded from
     * all display methods)
     */
    private boolean isDisplayed;

    /**
     * True: Value item is essential, false: Otherwise
     */
    private boolean isEssential;

    /**
     * Lock status of value item: True: Locked, false: Unlocked
     */
    private boolean isLocked;

    /**
     * True: Value item is used for Jdpd input, false: Otherwise
     */
    private boolean isJdpdInput;

    /**
     * True: Matrix will be cloned before change into this.lastClonedMatrix,
     * false: this.lastClonedMatrix will always be null
     */
    private boolean isMatrixClonedBeforeChange;

    /**
     * True: This instance is selected, false: Otherwise
     */
    private boolean isSelected;

    /**
     * Last cloned matrix
     */
    private ValueItemMatrixElement[][] lastClonedMatrix;

    /**
     * Matrix
     */
    private ValueItemMatrixElement[][] matrix;

    /**
     * Column names of matrix
     */
    private String[] matrixColumnNames;

    /**
     * Column width of matrix
     */
    private String[] matrixColumnWidths;

    /**
     * Supplementary data
     */
    private String[] supplementaryData;

    /**
     * Column of matrix for x-value of diagram
     */
    private int matrixDiagramXValueColumn;

    /**
     * Column of matrix for x-value of diagram
     */
    private int matrixDiagramYValueColumn;

    /**
     * Maximum number of rows of matrix
     */
    private int matrixMaximumNumberOfRows;

    /**
     * Flags for matrix columns to be omitted in output:
     * matrixOutputOmitColumns[i] = true: Column i has to be omitted in output
     */
    private boolean[] matrixOutputOmitColumns;

    /**
     * Name
     */
    private String name;

    /**
     * Array with node names
     */
    private String[] nodeNames;

    /**
     * Compartment container. NOTE: If not null then basicType must be
     * ValueItemEnumBasicType.COMPARTMENT_CONTAINER.
     */
    private CompartmentContainer compartmentContainer;

    /**
     * True: Value item is update notifier, false: Otherwise
     */
    private boolean isUpdateNotifier;

    /**
     * Value item container
     */
    private ValueItemContainer valueItemContainer;

    /**
     * Vertical position
     */
    private int verticalPosition;

    /**
     * Object. NOTE: This object is NOT saved, cloned or made persistent.
     */
    private Object object;

    /**
     * Name of data value item for this display value item
     */
    private String nameOfDataValueItem;

    /**
     * Name of dipslay value item for this data value item
     */
    private String nameOfDisplayValueItem;

    /**
     * Array for restoration of matrix row sorting
     */
    private int[] restoreArrayForMatrixRowSorting;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     */
    public ValueItem() {
        super();
        this.initialize();
    }

    /**
     * Constructor
     *
     * @param aXmlString XML representation of value item
     * @throws IllegalArgumentException Thrown when aXmlString is null/empty or
     * can not be read
     */
    public ValueItem(String aXmlString) throws IllegalArgumentException {
        super();
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXmlString == null || aXmlString.isEmpty()) {
            throw new IllegalArgumentException("aXmlString is null/empty.");
        }

        // </editor-fold>
        this.initialize();
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
     * @param aXmlElement The XML element containing the value item data
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItem(Element aXmlElement) throws IllegalArgumentException {
        super();
        this.initialize();
        if (!this.readXmlInformation(aXmlElement)) {
            throw new IllegalArgumentException("Can not read XML information.");
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a change receiver
     *
     * @param aChangeNotifier Object that notifies change
     * @param aChangeInfo Change information
     */
    @Override
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeNotifier == this.compartmentContainer && aChangeInfo.getChangeType() == ChangeTypeEnum.COMPARTMENT_CONTAINER_ERROR_CHANGE) {
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_ERROR_CHANGE);
        } else {
            this.notifyChangeReceiver(aChangeInfo.getChangeType());
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public compareTo() method">
    /**
     * Standard compareTo method for vertical position of value item
     *
     * @param aValueItem Value item to be compared
     * @return Standard compareTo result for vertical position of value item
     * @throws IllegalArgumentException Thrown if parameter aValueItem is
     * invalid
     */
    @Override
    public int compareTo(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            throw new IllegalArgumentException("Value item is null.");
        }
        // </editor-fold>
        if (this.verticalPosition < aValueItem.getVerticalPosition()) {
            return -1;
        } else if (this.verticalPosition > aValueItem.getVerticalPosition()) {
            return 1;
        } else {
            return 0;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Change detection related methods">
    /**
     * Initialize change detection
     */
    public void initializeChangeDetection() {
        this.hasChangeDetected = false;
    }

    /**
     * Returns if change occurred after last call of initializeChangeDetection()
     *
     * @return True: Change occurred after last call of
     * initializeChangeDetection(), false: Otherwise
     */
    public boolean hasChangeDetected() {
        return this.hasChangeDetected;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Data value item and display value item related methods">
    /**
     * Returns data value item
     *
     * @return Data value item or null if none is available
     */
    public ValueItem getDataValueItem() {
        if (this.valueItemContainer == null) {
            return null;
        } else {
            return this.valueItemContainer.getValueItem(this.nameOfDataValueItem);
        }
    }

    /**
     * Returns display value item
     *
     * @return Display value item or null if none is available
     */
    public ValueItem getDisplayValueItem() {
        if (this.valueItemContainer == null) {
            return null;
        } else {
            return this.valueItemContainer.getValueItem(this.nameOfDisplayValueItem);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Diagram data related methods">
    /**
     * Returns tab separated diagram data
     *
     * @return Tab separated diagram data or null if none are available
     */
    public String getTabSeparatedDiagramData() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.hasMatrixDiagram()) {
            return null;
        }

        // </editor-fold>
        StringBuilder tmpStringBuilder = new StringBuilder(ModelDefinitions.BUFFER_SIZE_SMALL);
        tmpStringBuilder.append(this.getMatrixColumnName(this.getMatrixDiagramXValueColumn()));
        tmpStringBuilder.append("\t");
        tmpStringBuilder.append(this.getMatrixColumnName(this.getMatrixDiagramYValueColumn()));
        tmpStringBuilder.append("\n");

        // Use correct decimal separator on local system
        switch (this.getTypeFormat(this.getMatrixDiagramXValueColumn()).getDataType()) {
            case NUMERIC:
                for (int i = 0; i < this.getMatrixRowCount(); i++) {
                    tmpStringBuilder.append(NumberFormat.getNumberInstance().format(this.getValueAsDouble(i, this.getMatrixDiagramXValueColumn())));
                    tmpStringBuilder.append("\t");
                    tmpStringBuilder.append(NumberFormat.getNumberInstance().format(this.getValueAsDouble(i, this.getMatrixDiagramYValueColumn())));
                    tmpStringBuilder.append("\n");
                }
                break;
            case TEXT:
                for (int i = 0; i < this.getMatrixRowCount(); i++) {
                    tmpStringBuilder.append(this.getValue(i, this.getMatrixDiagramXValueColumn()));
                    tmpStringBuilder.append("\t");
                    tmpStringBuilder.append(NumberFormat.getNumberInstance().format(this.getValueAsDouble(i, this.getMatrixDiagramYValueColumn())));
                    tmpStringBuilder.append("\n");
                }
                break;
            default:
                return null;
        }
        return tmpStringBuilder.toString();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Column default value set">
    /**
     * Returns if value item contains matrix with columns that may be set with a
     * specified value (i.e. that are completely editable with text or number)
     *
     * @return True: Value item contains matrix with columns that may be set
     * with a specified value, false: Otherwise
     */
    public boolean isColumnValueSet() {
        if (this.getBasicType() == ValueItemEnumBasicType.FLEXIBLE_MATRIX || this.getBasicType() == ValueItemEnumBasicType.MATRIX) {
            for (int i = 0; i < this.getMatrixColumnCount(); i++) {
                if (this.isColumnValueSet(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns if value item contains matrix with specified column that may be
     * set with a specified value (i.e. that column is completely editable with
     * text or number)
     *
     * @param aColumnIndex Index of column
     * @return True: Value item contains matrix with specified column that may
     * be set with a specified value, false: Otherwise
     */
    public boolean isColumnValueSet(int aColumnIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColumnIndex < 0 || aColumnIndex >= this.getMatrixColumnCount()) {
            return false;
        }

        // </editor-fold>
        if (this.getBasicType() == ValueItemEnumBasicType.FLEXIBLE_MATRIX || this.getBasicType() == ValueItemEnumBasicType.MATRIX) {
            boolean tmpIsColumnEditable = true;
            boolean tmpIsSingleAllowedValueSet = false;
            for (int k = 0; k < this.getMatrixRowCount(); k++) {
                ValueItemDataTypeFormat tmpValueItemDataTypeFormat = this.getValueItemMatrixElement(k, aColumnIndex).getTypeFormat();
                if (tmpValueItemDataTypeFormat.isEditable()) {
                    if (!(tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.NUMERIC
                            || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.NUMERIC_NULL
                            || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.TEXT
                            || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.TEXT_EMPTY
                            || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.SELECTION_TEXT)) {
                        tmpIsColumnEditable = false;
                        break;
                    } else {
                        tmpIsSingleAllowedValueSet = true;
                    }
                }
            }
            if (tmpIsColumnEditable && tmpIsSingleAllowedValueSet) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns selection text data type format with combined sorted selection
     * texts of all rows. NOTE: Protein backbone probe definitions are added
     * with wildcard (*PD1, *PD2 etc.) if available.
     *
     * @param aColumnIndex Index of column
     * @return Selection text data type format with combined sorted selection
     * texts of all rows or null if combination is not possible
     */
    public ValueItemDataTypeFormat getCombinedSelectionTextColumnDataTypeFormat(int aColumnIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColumnIndex < 0 || aColumnIndex >= this.getMatrixColumnCount()) {
            return null;
        }

        // </editor-fold>
        if (this.getBasicType() == ValueItemEnumBasicType.FLEXIBLE_MATRIX || this.getBasicType() == ValueItemEnumBasicType.MATRIX) {
            // NOTE: Capacity 100 seems a good guess for most applications
            HashMap<String, String> tmpSelectionTextsMap = new HashMap<>(100);
            for (int k = 0; k < this.getMatrixRowCount(); k++) {
                ValueItemDataTypeFormat tmpValueItemDataTypeFormat = this.getValueItemMatrixElement(k, aColumnIndex).getTypeFormat();
                // NOTE ValueItemEnumDataType.SELECTION_TEXT is always editable so additional if clause for check is NOT necessary
                if (tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.SELECTION_TEXT) {
                    String[] tmpSelectionTexts = tmpValueItemDataTypeFormat.getSelectionTexts();
                    for (String tmpSingleSelectionText : tmpSelectionTexts) {
                        tmpSelectionTextsMap.put(tmpSingleSelectionText, tmpSingleSelectionText);
                        // Check if *PD1, *PD2 etc. particle protein backbone probe and add
                        Matcher tmpProteinBackboneProbeMatcher = ModelDefinitions.PARTICLE_PROTEIN_BACKBONE_PROBE_CAPTURING_PATTERN.matcher(tmpSingleSelectionText);
                        if (tmpProteinBackboneProbeMatcher.matches()) {
                            String tmpNewSelectionText = ModelDefinitions.GENERAL_PROTEIN_BACKBONE_PROBE_START + tmpProteinBackboneProbeMatcher.group(1);
                            if (!tmpSelectionTextsMap.containsKey(tmpNewSelectionText)) {
                                tmpSelectionTextsMap.put(tmpNewSelectionText, tmpNewSelectionText);
                            }
                        }
                    }
                }
            }
            if (!tmpSelectionTextsMap.isEmpty()) {
                String[] tmpCombinedSelectionTextsArray = tmpSelectionTextsMap.values().toArray(new String[0]);
                // NOTE: this.stringUtilityMethods.sortStringArray() sorts integer representations ascending according to their integer values!
                this.stringUtilityMethods.sortStringArray(tmpCombinedSelectionTextsArray);
                ValueItemDataTypeFormat tmpCombinedSelectionTextsDataTypeFormat = new ValueItemDataTypeFormat(tmpCombinedSelectionTextsArray);
                return tmpCombinedSelectionTextsDataTypeFormat;
            }
        }
        return null;
    }

    /**
     * Returns selection text data type format with combined sorted texts of all
     * rows for specified column. NOTE: Column may NOT be editable. Protein
     * backbone probe definitions are added with wildcard (*PD1, *PD2 etc.) if
     * available.
     *
     * @param aColumnIndex Index of column
     * @return Selection text data type format with combined sorted selection
     * texts of all rows or null if combination is not possible
     */
    public ValueItemDataTypeFormat getCombinedTextColumnDataTypeFormat(int aColumnIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColumnIndex < 0 || aColumnIndex >= this.getMatrixColumnCount()) {
            return null;
        }

        // </editor-fold>
        if (this.getBasicType() == ValueItemEnumBasicType.FLEXIBLE_MATRIX || this.getBasicType() == ValueItemEnumBasicType.MATRIX) {
            // NOTE: Capacity 100 seems a good guess for most applications
            HashMap<String, String> tmpTextMap = new HashMap<>(100);
            boolean tmpIsNumeric = true;
            for (int k = 0; k < this.getMatrixRowCount(); k++) {
                ValueItemDataTypeFormat tmpValueItemDataTypeFormat = this.getValueItemMatrixElement(k, aColumnIndex).getTypeFormat();
                if (tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.SELECTION_TEXT
                        || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.TEXT
                        || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.TEXT_EMPTY
                        || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.NUMERIC
                        || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.NUMERIC_NULL) {
                    if (tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.SELECTION_TEXT
                            || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.TEXT
                            || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.TEXT_EMPTY
                            || tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.NUMERIC_NULL) {
                        tmpIsNumeric = false;
                    }
                    String tmpText = this.getValue(k, aColumnIndex);
                    tmpTextMap.put(tmpText, tmpText);
                    // Check if *PD1, *PD2 etc. particle protein backbone probe and add
                    Matcher tmpProteinBackboneProbeMatcher = ModelDefinitions.PARTICLE_PROTEIN_BACKBONE_PROBE_CAPTURING_PATTERN.matcher(tmpText);
                    if (tmpProteinBackboneProbeMatcher.matches()) {
                        String tmpNewText = ModelDefinitions.GENERAL_PROTEIN_BACKBONE_PROBE_START + tmpProteinBackboneProbeMatcher.group(1);
                        if (!tmpTextMap.containsKey(tmpNewText)) {
                            tmpTextMap.put(tmpNewText, tmpNewText);
                        }
                    }
                } else {
                    return null;
                }
            }
            if (!tmpTextMap.isEmpty()) {
                String[] tmpCombinedTextArray = tmpTextMap.values().toArray(new String[0]);
                if (tmpIsNumeric) {
                    tmpCombinedTextArray = this.getSortedNumericStrings(tmpCombinedTextArray);
                } else {
                    Arrays.sort(tmpCombinedTextArray);
                }
                ValueItemDataTypeFormat tmpCombinedTextDataTypeFormat = new ValueItemDataTypeFormat(tmpCombinedTextArray);
                return tmpCombinedTextDataTypeFormat;
            }
        }
        return null;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Insert/remove row for FLEXIBLE_MATRIX">

    /**
     * Returns if matrix row can be inserted
     *
     * @return True: Matrix row can be inserted, false: Otherwise
     */
    public boolean canInsertMatrixRow() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.basicType != ValueItemEnumBasicType.FLEXIBLE_MATRIX) {
            return false;
        }
        if (this.matrix == null) {
            return false;
        }
        if (this.getMatrixRowCount() >= this.getMatrixMaximumNumberOfRows()) {
            return false;
        }

        // </editor-fold>
        int tmpExclusiveSelectionTextColumn = this.getExclusiveSelectionTextColumn();
        if (tmpExclusiveSelectionTextColumn > -1) {
            return this.getTypeFormat(0, this.getExclusiveSelectionTextColumn()).getSelectionTexts().length > 1;
        } else {
            return true;
        }
    }

    /**
     * Adds matrix row with default values if matrix is flexible and column type
     * formats are defined
     *
     * @param aRowIndex Index for new row (is automatically corrected if
     * invalid)
     * @return True: Operation successful, false: Operation failed
     */
    public boolean insertMatrixRow(int aRowIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.canInsertMatrixRow()) {
            return false;
        }
        if (aRowIndex < 0 || aRowIndex > this.matrix.length) {
            aRowIndex = this.matrix.length;
        }

        // </editor-fold>
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[this.matrix.length + 1][this.matrix[0].length];
        int tmpExclusiveSelectionTextColumn = -1;
        int tmpOldRowIndex = 0;
        for (int i = 0; i < this.matrix.length + 1; i++) {
            if (i == aRowIndex) {
                // <editor-fold defaultstate="collapsed" desc="New row">
                for (int k = 0; k < this.matrix[0].length; k++) {
                    // <editor-fold defaultstate="collapsed" desc="New column of row">
                    if (this.isExclusiveSelectionTextColumn(k)) {
                        // <editor-fold defaultstate="collapsed" desc="Exclusive selection text column exists">
                        tmpExclusiveSelectionTextColumn = k;
                        ValueItemDataTypeFormat tmpTypeFormat = this.getTypeFormat(k).getClone();
                        String tmpNewValue = tmpTypeFormat.getFirstNoneDefaultSelectionText();
                        tmpTypeFormat.setDefaultValueAndSelectionTexts(tmpNewValue, tmpTypeFormat.getSelectionTexts());
                        tmpMatrix[i][k] = new ValueItemMatrixElement(tmpNewValue, tmpTypeFormat);

                        // </editor-fold>
                    } else if (this.getTypeFormat(k).isUniqueDefault()) {
                        // <editor-fold defaultstate="collapsed" desc="Unique default value">
                        String[] tmpOtherValues = new String[this.matrix.length];
                        for (int j = 0; j < this.matrix.length; j++) {
                            tmpOtherValues[j] = this.matrix[j][k].getValue();
                        }
                        tmpMatrix[i][k] = new ValueItemMatrixElement(this.getTypeFormat(k).getUniqueDefaultValue(tmpOtherValues), this.getTypeFormat(k).getClone());

                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Standard default value">
                        tmpMatrix[i][k] = new ValueItemMatrixElement(this.getTypeFormat(k).getClone());
                        // </editor-fold>
                    }
                    // </editor-fold>
                }
                // </editor-fold>
            } else {
                tmpMatrix[i] = this.matrix[tmpOldRowIndex++];
            }
        }
        // <editor-fold defaultstate="collapsed" desc="Correct exclusive selection texts if necessary">
        if (tmpExclusiveSelectionTextColumn > -1) {
            ValueItemMatrixElement[] tmpMatrixElementColumn = new ValueItemMatrixElement[tmpMatrix.length];
            for (int i = 0; i < tmpMatrix.length; i++) {
                tmpMatrixElementColumn[i] = tmpMatrix[i][tmpExclusiveSelectionTextColumn];
            }
            ValueItemUtils.correctExclusiveSelectionTexts(tmpMatrixElementColumn, null);
        }

        // </editor-fold>
        this.setMatrix(tmpMatrix);
        return true;
    }

    /**
     * Copies matrix row if matrix is flexible and column type formats are
     * defined
     *
     * @param aRowIndexToBeCopied Index for row to be copied (is automatically
     * corrected if invalid)
     * @return True: Operation successful, false: Operation failed
     */
    public boolean copyMatrixRow(int aRowIndexToBeCopied) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.canInsertMatrixRow()) {
            return false;
        }
        if (aRowIndexToBeCopied < 0 || aRowIndexToBeCopied > this.matrix.length - 1) {
            aRowIndexToBeCopied = this.matrix.length - 1;
        }

        // </editor-fold>
        int tmpTargetRowIndex = this.matrix.length;
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[this.matrix.length + 1][this.matrix[0].length];
        int tmpExclusiveSelectionTextColumn = -1;
        int tmpOldRowIndex = 0;
        for (int i = 0; i < this.matrix.length + 1; i++) {
            if (i == tmpTargetRowIndex) {
                // <editor-fold defaultstate="collapsed" desc="New row">
                for (int k = 0; k < this.matrix[0].length; k++) {
                    // <editor-fold defaultstate="collapsed" desc="New column of row">
                    if (this.isExclusiveSelectionTextColumn(k)) {
                        // <editor-fold defaultstate="collapsed" desc="Exclusive selection text column exists">
                        tmpExclusiveSelectionTextColumn = k;
                        ValueItemDataTypeFormat tmpTypeFormat = this.getTypeFormat(k).getClone();
                        String tmpNewValue = tmpTypeFormat.getFirstNoneDefaultSelectionText();
                        tmpTypeFormat.setDefaultValueAndSelectionTexts(tmpNewValue, tmpTypeFormat.getSelectionTexts());
                        tmpMatrix[i][k] = new ValueItemMatrixElement(tmpNewValue, tmpTypeFormat);

                        // </editor-fold>
                    } else if (this.getTypeFormat(k).isUniqueDefault()) {
                        // <editor-fold defaultstate="collapsed" desc="Unique default value">
                        String[] tmpOtherValues = new String[this.matrix.length];
                        for (int j = 0; j < this.matrix.length; j++) {
                            tmpOtherValues[j] = this.matrix[j][k].getValue();
                        }
                        tmpMatrix[i][k] = new ValueItemMatrixElement(this.getTypeFormat(k).getUniqueDefaultValue(tmpOtherValues), this.getTypeFormat(k).getClone());

                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Standard default value">
                        tmpMatrix[i][k] = this.matrix[aRowIndexToBeCopied][k].getClone();

                        // </editor-fold>
                    }

                    // </editor-fold>
                }

                // </editor-fold>
            } else {
                tmpMatrix[i] = this.matrix[tmpOldRowIndex++];
            }
        }

        // <editor-fold defaultstate="collapsed" desc="Correct exclusive selection texts if necessary">
        if (tmpExclusiveSelectionTextColumn > -1) {
            ValueItemMatrixElement[] tmpMatrixElementColumn = new ValueItemMatrixElement[tmpMatrix.length];
            for (int i = 0; i < tmpMatrix.length; i++) {
                tmpMatrixElementColumn[i] = tmpMatrix[i][tmpExclusiveSelectionTextColumn];
            }
            ValueItemUtils.correctExclusiveSelectionTexts(tmpMatrixElementColumn, null);
        }

        // </editor-fold>
        this.setMatrix(tmpMatrix);
        return true;
    }

    /**
     * Removes specified matrix row if matrix has more than one row
     *
     * @param aRowIndex Index of row to be removed (is automatically corrected
     * if invalid)
     * @return True: Operation successful, false: Operation failed
     */
    public boolean removeMatrixRow(int aRowIndex) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.basicType != ValueItemEnumBasicType.FLEXIBLE_MATRIX || this.matrix == null || this.matrix.length == 1) {
            return false;
        }
        if (aRowIndex < 0 || aRowIndex >= this.matrix.length) {
            aRowIndex = this.matrix.length - 1;
        }

        // </editor-fold>
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[this.matrix.length - 1][this.matrix[0].length];
        String tmpRemovedValueOfExclusiveSelectionTextColumn = null;
        int tmpExclusiveSelectionTextColumn = -1;
        int tmpNewRowIndex = 0;
        for (int i = 0; i < this.matrix.length; i++) {
            if (i != aRowIndex) {
                tmpMatrix[tmpNewRowIndex++] = this.matrix[i];
            } else {

                // <editor-fold defaultstate="collapsed" desc="Check exclusive selection text column">
                tmpExclusiveSelectionTextColumn = this.getExclusiveSelectionTextColumn();
                if (tmpExclusiveSelectionTextColumn > -1) {
                    tmpRemovedValueOfExclusiveSelectionTextColumn = this.matrix[i][tmpExclusiveSelectionTextColumn].getValue();
                }

                // </editor-fold>
            }
        }

        // <editor-fold defaultstate="collapsed" desc="Correct exclusive selection texts if necessary">
        if (tmpExclusiveSelectionTextColumn > -1) {
            ValueItemMatrixElement[] tmpMatrixElementColumn = new ValueItemMatrixElement[tmpMatrix.length];
            for (int i = 0; i < tmpMatrix.length; i++) {
                tmpMatrixElementColumn[i] = tmpMatrix[i][tmpExclusiveSelectionTextColumn];
            }
            ValueItemUtils.correctExclusiveSelectionTexts(tmpMatrixElementColumn, new String[]{tmpRemovedValueOfExclusiveSelectionTextColumn});
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Correct first row editable only columns if necessary">
        ValueItemUtils.correctFirstCellOfFirstRowEditableOnlyColumns(tmpMatrix);

        // </editor-fold>
        this.setMatrix(tmpMatrix);
        return true;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Status related methods">
    /**
     * True: Value item has specified status, false: Otherwise
     *
     * @param aStatus Value item status
     * @return True: Value item has specified status, false: Otherwise
     */
    public boolean hasStatus(ValueItemEnumStatus aStatus) {
        switch (aStatus) {
            case ALL:
            case UNDEFINED:
                return true;
            case ACTIVE:
                return this.isActive();
            case INACTIVE:
                return !this.isActive();
            case LOCKED:
                return this.isLocked();
            case JDPD_INPUT:
                return this.isJdpdInput();
            case UNLOCKED:
                return !this.isLocked();
            case HAS_ERROR:
                return this.hasError();
            case HAS_NO_ERROR:
                return !this.hasError();
            case HAS_HINT:
                return this.hasHint();
            case HAS_NO_HINT:
                return !this.hasHint();
            case SELECTED:
                return this.isSelected();
            case DESELECTED:
                return !this.isSelected();
            default:
                return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update notifier related methods">
    /**
     * Notifies dependent value items of the same container about a value change
     * for update
     */
    public void notifyDependentValueItemsForUpdate() {
        if (this.isUpdateNotifier && this.valueItemContainer != null) {
            this.valueItemContainer.notifyDependentValueItemsForUpdate(this);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Table-data schema related methods">
    /**
     * Returns schema value item (see code)
     *
     * @param aSchemaValueItemName Name of schema value item
     * @return Schema value item (see code) or null if schema value item could
     * not be created
     */
    public ValueItem getSchemaValueItem(String aSchemaValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSchemaValueItemName == null || aSchemaValueItemName.isEmpty()) {
            return null;
        }
        // </editor-fold>
        try {
            switch (this.basicType) {
                case MATRIX:
                case FLEXIBLE_MATRIX:
                case VECTOR:
                    ValueItem tmpSchemaValueItem = new ValueItem();
                    // NOTE: Use getCreationStandardTimeStampAppendString() to make schema unique
                    tmpSchemaValueItem.setName((new TimeUtilityMethods()).getCreationStandardTimeStampAppendString(aSchemaValueItemName));
                    tmpSchemaValueItem.setDisplayName(aSchemaValueItemName);
                    switch (this.basicType) {
                        case MATRIX:
                            tmpSchemaValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                            break;
                        case FLEXIBLE_MATRIX:
                            tmpSchemaValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                            break;
                        case VECTOR:
                            tmpSchemaValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
                            break;
                    }
                    ValueItemDataTypeFormat tmpSchemaMatrixElementTypeFormat = new ValueItemDataTypeFormat(false);

                    if (this.matrixColumnNames != null && this.matrixColumnNames.length > 0) {
                        tmpSchemaValueItem.setMatrixColumnNames(this.matrixColumnNames);
                        tmpSchemaValueItem.setMatrixColumnWidths(this.matrixColumnWidths);
                    } else {
                        return null;
                    }

                    if (this.matrix != null && this.matrix.length > 0) {
                        ValueItemMatrixElement[][] tmpSchemaMatrix = new ValueItemMatrixElement[this.matrix.length][];
                        for (int i = 0; i < this.matrix.length; i++) {
                            if (this.matrix[i] != null && this.matrix[i].length > 0) {
                                tmpSchemaMatrix[i] = new ValueItemMatrixElement[this.matrix[i].length];
                                for (int k = 0; k < this.matrix[i].length; k++) {
                                    ValueItemMatrixElement tmpValueItemMatrixElement = this.matrix[i][k];
                                    switch (tmpValueItemMatrixElement.getTypeFormat().getDataType()) {
                                        case DIRECTORY:
                                        case FILE:
                                        case NUMERIC:
                                        case NUMERIC_NULL:
                                        case SELECTION_TEXT:
                                        case TEXT:
                                        case TEXT_EMPTY:
                                        case TIMESTAMP:
                                        case TIMESTAMP_EMPTY:
                                            tmpSchemaMatrix[i][k] = new ValueItemMatrixElement(tmpValueItemMatrixElement.getValue(), tmpSchemaMatrixElementTypeFormat);
                                            break;
                                        case MOLECULAR_STRUCTURE:
                                        case MONOMER_STRUCTURE:
                                            tmpSchemaMatrix[i][k] = new ValueItemMatrixElement(ModelDefinitions.SCHEMA_WILDCARD_STRING, tmpSchemaMatrixElementTypeFormat);
                                            break;
                                        default:
                                            tmpSchemaMatrix[i][k] = new ValueItemMatrixElement(ModelDefinitions.SCHEMA_WILDCARD_STRING, tmpSchemaMatrixElementTypeFormat);
                                            break;
                                    }
                                }
                            } else {
                                return null;
                            }
                        }

                        tmpSchemaValueItem.setMatrix(tmpSchemaMatrix);
                        return tmpSchemaValueItem;
                    } else {
                        return null;
                    }
                case COMPARTMENT_CONTAINER:
                case OBJECT:
                case SCALAR:
                    return null;
                default:
                    return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns if aSchemaValueItem can be applied to this value item (i.e. if
     * information is compatible to this value item)
     *
     * @param aSchemaValueItem Schema value item
     * @return True: Information of aSchemaValueItem can be applied to this
     * value item, false: Otherwise
     */
    public boolean canApplySchemaValueItem(ValueItem aSchemaValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSchemaValueItem == null) {
            return false;
        }
        switch (this.basicType) {
            case COMPARTMENT_CONTAINER:
            case OBJECT:
            case SCALAR:
                return false;
        }
        // </editor-fold>
        try {
            if (this.basicType != aSchemaValueItem.getBasicType()) {
                if (this.basicType == ValueItemEnumBasicType.FLEXIBLE_MATRIX) {
                    if (aSchemaValueItem.getBasicType() != ValueItemEnumBasicType.MATRIX) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            // NOTE: Check ist removed for better applicability but may cause 
            // compatibility problems.
            // if (this.basicType != ValueItemEnumBasicType.FLEXIBLE_MATRIX) {
            //     if (this.matrix.length != aSchemaValueItem.getMatrix().length) {
            //         return false;
            //     }
            // }
            String[] tmpMatrixColumnNames = aSchemaValueItem.getMatrixColumnNames();
            if (this.matrixColumnNames.length != tmpMatrixColumnNames.length) {
                return false;
            }
            for (int i = 0; i < this.matrixColumnNames.length; i++) {
                if (!this.matrixColumnNames[i].equals(tmpMatrixColumnNames[i])) {
                    return false;
                }
            }
            // NOTE: Check ist removed for better applicability but may cause 
            // compatibility problems.
            // Old code:
            // Check first matrix element only
            // for (int i = 0; i < aSchemaValueItem.getMatrix()[0].length; i++) {
            //     if (!aSchemaValueItem.getValue(0, i).equals(ModelDefinitions.SCHEMA_WILDCARD_STRING)) {
            //         if (this.matrix[0][i].getTypeFormat().isEditable()) {
            //             if (!this.matrix[0][i].getTypeFormat().isValueAllowed(aSchemaValueItem.getValue(0, i))) {
            //                 return false;
            //             } else {
            //                 break;
            //             }
            //         }
            //     }
            // }
            // NOTE: Single "isValueAllowed" check is removed for better 
            // applicability but may cause compatibility problems.
            // Old code:
            // for (int i = 0; i < aSchemaValueItem.getMatrix().length; i++) {
            //     for (int k = 0; k < aSchemaValueItem.getMatrix()[i].length; k++) {
            //         if (!aSchemaValueItem.getValue(i, k).equals(ModelDefinitions.SCHEMA_WILDCARD_STRING)) {
            //             if (this.matrix[i][k].getTypeFormat().isEditable()) {
            //                 if (!this.matrix[i][k].getTypeFormat().isValueAllowed(aSchemaValueItem.getValue(i, k))) {
            //                     return false;
            //                 }
            //             }
            //         }
            //     }
            // }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Returns if aSchemaValueItem matches this value item (i.e. if the editable
     * and non-editable information is equal to this value item)
     *
     * @param aSchemaValueItem Schema value item
     * @return True: Information of aSchemaValueItem matches this value item,
     * false: Otherwise
     */
    public boolean matchesSchemaValueItem(ValueItem aSchemaValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSchemaValueItem == null) {
            return false;
        }
        switch (this.basicType) {
            case COMPARTMENT_CONTAINER:
            case OBJECT:
            case SCALAR:
                return false;
        }
        // </editor-fold>
        try {
            if (this.basicType != aSchemaValueItem.getBasicType()) {
                if (this.basicType == ValueItemEnumBasicType.FLEXIBLE_MATRIX) {
                    if (aSchemaValueItem.getBasicType() != ValueItemEnumBasicType.MATRIX) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if (this.matrix.length != aSchemaValueItem.getMatrix().length) {
                return false;
            }
            if (this.matrix[0].length != aSchemaValueItem.getMatrix()[0].length) {
                return false;
            }
            for (int k = 0; k < this.matrixColumnNames.length; k++) {
                if (!this.matrixColumnNames[k].equals(aSchemaValueItem.getMatrixColumnNames()[k])) {
                    return false;
                }
            }
            for (int i = 0; i < aSchemaValueItem.getMatrix().length; i++) {
                for (int k = 0; k < aSchemaValueItem.getMatrix()[i].length; k++) {
                    if (!aSchemaValueItem.getValue(i, k).equals(ModelDefinitions.SCHEMA_WILDCARD_STRING)) {
                        // Do compare ALL information (editable and non-editable)
                        if (!this.matrix[i][k].getValue().equals(aSchemaValueItem.getValue(i, k))) {
                            return false;
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
    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Returns a XML element representation of this ValueItem instance
     *
     * @return A XML element representation of this ValueItem instance
     */
    public Element getAsXmlElement() {
        Element tmpRoot = new Element(ValueItemXmlName.VALUE_ITEM);
        // IMPORTANT: Set version of this XML definition
        tmpRoot.addContent(new Element(ValueItemXmlName.VERSION).addContent("Version 1.0.0"));

        // NOTE: Order corresponds to methods readXmlInformationV...() und
        // initialize ()
        tmpRoot.addContent(new Element(ValueItemXmlName.NAME).addContent(this.name));
        tmpRoot.addContent(new Element(ValueItemXmlName.DISPLAY_NAME).addContent(this.displayName));
        tmpRoot.addContent(new Element(ValueItemXmlName.NAME_OF_DATA_VALUE_ITEM).addContent(this.nameOfDataValueItem));
        tmpRoot.addContent(new Element(ValueItemXmlName.NAME_OF_DISPLAY_VALUE_ITEM).addContent(this.nameOfDisplayValueItem));
        ModelUtils.addStringArrayToXmlElement(this.nodeNames, tmpRoot, ValueItemXmlName.NODE_NAMES, ValueItemXmlName.NODE_SINGLE_NAME);
        this.addMatrixOutputOmitColumnsToXmlElement(tmpRoot);
        this.addMatrixToXmlElement(tmpRoot);
        ModelUtils.addStringArrayToXmlElement(this.matrixColumnNames, tmpRoot, ValueItemXmlName.MATRIX_COLUMN_NAMES, ValueItemXmlName.MATRIX_SINGLE_COLUMN_NAME);
        ModelUtils.addStringArrayToXmlElement(this.matrixColumnWidths, tmpRoot, ValueItemXmlName.MATRIX_COLUMN_WIDTHS, ValueItemXmlName.MATRIX_SINGLE_COLUMN_WIDTH);
        ModelUtils.addStringArrayToXmlElement(this.supplementaryData, tmpRoot, ValueItemXmlName.SUPPLEMENTARY_DATA, ValueItemXmlName.SUPPLEMENTARY_DATA_ITEM);
        tmpRoot.addContent(new Element(ValueItemXmlName.MATRIX_DIAGRAM_X_VALUE_COLUMN).addContent(String.valueOf(this.matrixDiagramXValueColumn)));
        tmpRoot.addContent(new Element(ValueItemXmlName.MATRIX_DIAGRAM_Y_VALUE_COLUMN).addContent(String.valueOf(this.matrixDiagramYValueColumn)));
        tmpRoot.addContent(new Element(ValueItemXmlName.MATRIX_MAXIMUM_NUMBER_OF_ROWS).addContent(String.valueOf(this.matrixMaximumNumberOfRows)));
        tmpRoot.addContent(new Element(ValueItemXmlName.IS_ACTIVE).addContent(String.valueOf(this.isActive)));
        tmpRoot.addContent(new Element(ValueItemXmlName.IS_DISPLAYED).addContent(String.valueOf(this.isDisplayed)));
        tmpRoot.addContent(new Element(ValueItemXmlName.BASIC_TYPE).addContent(this.basicType.name()));
        tmpRoot.addContent(new Element(ValueItemXmlName.BLOCK_NAME).addContent(this.blockName));
        tmpRoot.addContent(new Element(ValueItemXmlName.DESCRIPTION).addContent(this.description));
        tmpRoot.addContent(new Element(ValueItemXmlName.ERROR).addContent(this.error));
        tmpRoot.addContent(new Element(ValueItemXmlName.HINT).addContent(this.hint));
        tmpRoot.addContent(new Element(ValueItemXmlName.VERTICAL_POSITION).addContent(String.valueOf(this.verticalPosition)));
        tmpRoot.addContent(new Element(ValueItemXmlName.IS_ESSENTIAL).addContent(String.valueOf(this.isEssential)));
        tmpRoot.addContent(new Element(ValueItemXmlName.IS_LOCKED).addContent(String.valueOf(this.isLocked)));
        tmpRoot.addContent(new Element(ValueItemXmlName.IS_JDPD_INPUT).addContent(String.valueOf(this.isJdpdInput)));
        tmpRoot.addContent(new Element(ValueItemXmlName.IS_MATRIX_CLONED_BEFORE_CHANGE).addContent(String.valueOf(this.isMatrixClonedBeforeChange)));
        // NOTE: this.lastClonedMatrix is NOT to be saved to XML
        tmpRoot.addContent(new Element(ValueItemXmlName.IS_UPDATE_NOTIFIER).addContent(String.valueOf(this.isUpdateNotifier)));
        tmpRoot.addContent(new Element(ValueItemXmlName.CONSTANT_SUM_COLUMN).addContent(String.valueOf(this.constantSumColumn)));
        this.addCompartmentContainer(tmpRoot);
        return tmpRoot;
    }

    /**
     * Returns a XML string representation of this ValueItem instance
     *
     * @return A XML string representation of this ValueItem instance
     */
    public String getAsXmlString() {
        Element tmpRoot = this.getAsXmlElement();
        XMLOutputter tmpOutputter = new XMLOutputter(Format.getPrettyFormat());
        Document tmpDocument = new Document();
        tmpDocument.setRootElement(tmpRoot);
        return tmpOutputter.outputString(tmpDocument);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Protein data related methods">
    /**
     * Returns if specified row contains ValueItemMatrixElement with protein
     * data. NOTE: If aRow does not exist false is returned.
     *
     * @param aRow Row
     * @return True: Specified row contains ValueItemMatrixElement with protein
     * data, False: Otherwise
     */
    public boolean isProteinDataInMatrixRow(int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aRow < 0) {
            return false;
        }
        if (aRow > this.matrix.length - 1) {
            return false;
        }
        // </editor-fold>
        for (ValueItemMatrixElement tmpSingleValueItemMatrixElement : this.matrix[aRow]) {
            if (tmpSingleValueItemMatrixElement.hasProteinData()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if a ValueItemMatrixElement in the matrix contains protein data
     *
     * @return True: At least one ValueItemMatrixElement in the matrix contains
     * protein data, False: Otherwise
     */
    public boolean hasProteinDataInMatrix() {
        for (int i = 0; i < this.getMatrixRowCount(); i++) {
            if (this.isProteinDataInMatrixRow(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sorts matrix rows so that rows that contain protein data have the lowest
     * index. Example: Row 0 (no protein data), Row 1 (protein data), Row 2
     * (protein data), Row 3 (no protein data) is sorted to: Row 2, Row 1, Row
     * 0, Row 3.
     */
    public void sortMatrixRowsWithProteinDataRowsFirst() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.getMatrixRowCount() == 0) {
            return;
        }
        // </editor-fold>
        boolean hasProteinData = false;
        for (int i = 0; i < this.getMatrixRowCount(); i++) {
            if (this.isProteinDataInMatrixRow(i)) {
                hasProteinData = true;
                break;
            }
        }
        if (hasProteinData) {
            LinkedList<Integer> tmpIndexList = new LinkedList<Integer>();
            for (int i = 0; i < this.getMatrixRowCount(); i++) {
                if (this.isProteinDataInMatrixRow(i)) {
                    tmpIndexList.addFirst(i);
                } else {
                    tmpIndexList.addLast(i);
                }
            }
            ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[this.getMatrixRowCount()][];
            this.restoreArrayForMatrixRowSorting = new int[this.getMatrixRowCount()];
            int tmpIndex = 0;
            for (Integer aRow : tmpIndexList) {
                this.restoreArrayForMatrixRowSorting[tmpIndex] = aRow;
                tmpNewMatrix[tmpIndex++] = this.matrix[aRow];
            }
            this.matrix = tmpNewMatrix;
        }
    }

    /**
     * Restores original matrix that was sorted with
     * sortMatrixRowsWithProteinDataRowsFirst()
     */
    public void restoreOriginalMatrixRowsAfterSorting() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.restoreArrayForMatrixRowSorting == null || this.restoreArrayForMatrixRowSorting.length == 0) {
            return;
        }
        if (this.restoreArrayForMatrixRowSorting.length != this.getMatrixRowCount()) {
            return;
        }
        // </editor-fold>
        ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[this.getMatrixRowCount()][];
        for (int i = 0; i < this.getMatrixRowCount(); i++) {
            tmpNewMatrix[this.restoreArrayForMatrixRowSorting[i]] = this.matrix[i];
        }
        this.matrix = tmpNewMatrix;
        this.restoreArrayForMatrixRowSorting = null;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Clones value item. NOTE: Returned cloned value item has the same value
     * item valueItemContainer as this instance.
     *
     * @return Cloned value item
     */
    public ValueItem getClone() {
        // NOTE: This order corresponds to order in method initialize() and readXmlInformationV...()
        // NOTE: There is NO notification since cloned value item will not have any change receivers at this stage
        ValueItem tmpClonedValueItem = new ValueItem();
        if (this.matrix != null) {
            tmpClonedValueItem.setMatrix(ValueItemUtils.getClonedMatrixOfValueItemMatrixElements(this.matrix));
        }
        if (this.matrixColumnNames != null) {
            tmpClonedValueItem.setMatrixColumnNames(this.stringUtilityMethods.clone(this.matrixColumnNames));
        }
        if (this.matrixColumnWidths != null) {
            tmpClonedValueItem.setMatrixColumnWidths(this.stringUtilityMethods.clone(this.matrixColumnWidths));
        }
        if (this.supplementaryData != null) {
            tmpClonedValueItem.setSupplementaryData(this.stringUtilityMethods.clone(this.supplementaryData));
        }
        tmpClonedValueItem.setCurrentMatrixPosition(this.currentRow, this.currentColumn);
        tmpClonedValueItem.setMatrixDiagramColumns(this.matrixDiagramXValueColumn, this.matrixDiagramYValueColumn);
        tmpClonedValueItem.setMatrixMaximumNumberOfRows(this.matrixMaximumNumberOfRows);
        tmpClonedValueItem.setActivity(this.isActive);
        tmpClonedValueItem.setDisplay(this.isDisplayed);
        tmpClonedValueItem.setBasicType(this.basicType);
        tmpClonedValueItem.setBlockName(this.blockName);
        tmpClonedValueItem.setValueItemContainer(this.valueItemContainer);
        tmpClonedValueItem.setDescription(this.description);
        tmpClonedValueItem.setError(this.error);
        tmpClonedValueItem.setHint(this.hint);
        tmpClonedValueItem.setDisplayName(this.displayName);
        tmpClonedValueItem.setNameOfDataValueItem(this.nameOfDataValueItem);
        tmpClonedValueItem.setNameOfDisplayValueItem(this.nameOfDisplayValueItem);
        tmpClonedValueItem.setVerticalPosition(this.verticalPosition);
        tmpClonedValueItem.setEssential(this.isEssential);
        tmpClonedValueItem.setLocked(this.isLocked);
        tmpClonedValueItem.setJdpdInput(this.isJdpdInput);
        tmpClonedValueItem.setMatrixClonedBeforeChange(this.isMatrixClonedBeforeChange);
        // NOTE: Constructor automatically sets this.lastClonedMatrix to null
        tmpClonedValueItem.setUpdateNotifier(this.isUpdateNotifier);
        tmpClonedValueItem.setName(this.name);
        if (this.nodeNames != null) {
            tmpClonedValueItem.setNodeNames(this.stringUtilityMethods.clone(this.nodeNames));
        }
        if (this.matrixOutputOmitColumns != null) {
            tmpClonedValueItem.setMatrixOutputOmitColumns(this.matrixOutputOmitColumns.clone());
        }
        tmpClonedValueItem.setConstantSumColumn(this.constantSumColumn);
        if (this.hasCompartments()) {
            tmpClonedValueItem.setCompartmentContainer(this.compartmentContainer.getClone());
        }
        tmpClonedValueItem.setSelected(this.isSelected);
        return tmpClonedValueItem;
    }

    /**
     * Returns default matrix element row
     *
     * @return Default matrix element row or null if not defined
     */
    public ValueItemMatrixElement[] getDefaultMatrixElementRow() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.matrix == null) {
            return null;
        }

        // </editor-fold>
        ValueItemMatrixElement[] tmpDefaultMatrixElementRow = new ValueItemMatrixElement[this.matrix[0].length];
        for (int k = 0; k < this.matrix[0].length; k++) {
            ValueItemMatrixElement tmpNewMatrixElement = new ValueItemMatrixElement(this.matrix[0][k].getTypeFormat().getClone());
            if (tmpNewMatrixElement.getTypeFormat().isFirstRowEditableOnly()) {
                tmpNewMatrixElement.getTypeFormat().setEditable(false);
            }
            tmpDefaultMatrixElementRow[k] = tmpNewMatrixElement;
        }
        return tmpDefaultMatrixElementRow;
    }

    /**
     * Returns sorted combined exclusive selection text list
     *
     * @return Sorted combined exclusive selection text list or null if none
     * exist
     */
    public LinkedList<String> getCombinedExclusiveSelectionTextList() {
        int tmpExclusiveSelectionTextColumn = this.getExclusiveSelectionTextColumn();
        if (tmpExclusiveSelectionTextColumn > -1) {
            LinkedList<String> tmpCombinedSelectionTextList = new LinkedList<String>();
            for (int i = 0; i < this.matrix.length; i++) {
                if (!tmpCombinedSelectionTextList.contains(this.matrix[i][tmpExclusiveSelectionTextColumn].getValue())) {
                    tmpCombinedSelectionTextList.addLast(this.matrix[i][tmpExclusiveSelectionTextColumn].getValue());
                }
                String[] tmpSelectionTexts = this.matrix[i][tmpExclusiveSelectionTextColumn].getTypeFormat().getSelectionTexts();
                for (String tmpSingleSelectionText : tmpSelectionTexts) {
                    if (!tmpCombinedSelectionTextList.contains(tmpSingleSelectionText)) {
                        tmpCombinedSelectionTextList.addLast(tmpSingleSelectionText);
                    }
                }
            }
            if (tmpCombinedSelectionTextList.size() > 0) {
                return tmpCombinedSelectionTextList;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns sorted combined exclusive selection texts
     *
     * @return Sorted combined exclusive selection texts or null if none exist
     */
    public String[] getSortedCombinedExclusiveSelectionTexts() {
        LinkedList<String> tmpCombinedSelectionTextList = this.getCombinedExclusiveSelectionTextList();
        if (tmpCombinedSelectionTextList != null) {
            String[] tmpCombinedSelectionTexts = tmpCombinedSelectionTextList.toArray(new String[0]);
            Arrays.sort(tmpCombinedSelectionTexts);
            return tmpCombinedSelectionTexts;
        } else {
            return null;
        }
    }

    /**
     * Standard toString method
     *
     * @return String representation of value item
     */
    @Override
    public String toString() {
        if (this.isActive) {
            // <editor-fold defaultstate="collapsed" desc="Active value item">
            if (this.hasError()) {
                // <editor-fold defaultstate="collapsed" desc="Value item has error">
                return String.format(ModelMessage.get("ValueItem.toString.Error"), this.displayName);

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Value item has no error">
                switch (this.basicType) {
                    case SCALAR:
                    case VECTOR:
                    case MATRIX:
                    case FLEXIBLE_MATRIX:
                    case OBJECT:
                    case COMPARTMENT_CONTAINER:
                        return this.displayName;
                    default:
                        return "";
                }

                // </editor-fold>
            }

            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Inactive value item">
            return String.format(ModelMessage.get("ValueItem.toString.Inactive"), this.displayName);

            // </editor-fold>
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public Properties">
    // <editor-fold defaultstate="collapsed" desc="- Active (get/set)">
    /**
     * Acitivity status of value item
     *
     * @return True: Active, false: Inactive
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * Acitivity status of value item
     *
     * @param anIsActive True: Active, false: Inactive
     */
    public void setActivity(boolean anIsActive) {
        if (anIsActive != this.isActive) {
            this.isActive = anIsActive;
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_ACTIVITY_CHANGE);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Display (get/set)">
    /**
     * True: Value item is displayed, false: Otherwise (i.e. it is excluded from
     * all display methods)
     *
     * @return True: Value item is displayed, false: Otherwise (i.e. it is
     * excluded from all display methods)
     */
    public boolean isDisplayed() {
        return this.isDisplayed;
    }

    /**
     * True: Value item is displayed, false: Otherwise (i.e. it is excluded from
     * all display methods)
     *
     * @param aValue True: Value item is displayed, false: Otherwise (i.e. it is
     * excluded from all display methods)
     */
    public void setDisplay(boolean aValue) {
        if (aValue != this.isDisplayed) {
            this.isDisplayed = aValue;
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_DISPLAY_CHANGE);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ValueItemEnumBasicType (get/set)">
    /**
     * Basic Type
     *
     * @return Basic Type
     */
    public ValueItemEnumBasicType getBasicType() {
        return this.basicType;
    }

    /**
     * Basic type
     *
     * @param aBasicType Basic type
     */
    public void setBasicType(ValueItemEnumBasicType aBasicType) {
        this.basicType = aBasicType;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- BlockName (get/set)">
    /**
     * Block name
     *
     * @return Block name
     */
    public String getBlockName() {
        return this.blockName;
    }

    /**
     * Block name
     *
     * @param aBlockName Block name
     */
    public void setBlockName(String aBlockName) {
        if (aBlockName != null && aBlockName.length() > 0) {
            this.blockName = aBlockName;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ConstantSumColumn related properties">
    /**
     * Checks constant sum column for insert
     *
     * @return True: Constant sum column for insert is correct and greater -1, false:
     * Otherwise
     */
    public boolean checkConstantSumColumn() {
        return this.constantSumColumn > -1 && this.constantSumColumn < this.getMatrixColumnCount() && this.isNumeric(this.constantSumColumn);
    }

    /**
     * Column with constant sum of numerical values for an insert operation or
     * -1 (then not defined)
     *
     * @return Constant sum column for insert
     */
    public int getConstantSumColumn() {
        return this.constantSumColumn;
    }

    /**
     * Column with constant sum of numerical values for an insert operation or
     * -1 (then not defined)
     *
     * @param aValue Constant sum column for insert of -1 (then not defined)
     */
    public void setConstantSumColumn(int aValue) {
        this.constantSumColumn = aValue;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Description (get/set)">
    /**
     * Description
     *
     * @return Describes the value of the input item
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * True: Value item has description, false: Otherwise
     *
     * @return True: Value item has description, false: Otherwise
     */
    public boolean hasDescription() {
        return this.description != null && this.description.length() > 0;
    }

    /**
     * Description
     *
     * @param aDescription Describes the value of the input item
     */
    public void setDescription(String aDescription) {
        this.description = aDescription;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DisplayName (get/set)">
    /**
     * Display name
     *
     * @return An display name.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Display name
     *
     * @param anDisplayName An display name.
     */
    public void setDisplayName(String anDisplayName) {
        this.displayName = anDisplayName;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- EditableColumn (get)">
    /**
     * Returns if value item has editable column
     *
     * @return True: Value item has editable column, false: Otherwise
     */
    public boolean hasEditableColumn() {
        return this.getFirstEditableColumn() != -1;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Error related properties">
    /**
     * Error
     *
     * @return Error
     */
    public String getError() {
        return this.error;
    }

    /**
     * True: Value item has error, false: Otherwise
     *
     * @return True: Value item has error, false: Otherwise
     */
    public boolean hasError() {
        return this.error.length() > 0;
    }

    /**
     * Removes error
     */
    public void removeError() {
        if (this.error.length() > 0) {
            this.error = "";
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_ERROR_CHANGE);
        }
    }

    /**
     * Error
     *
     * @param anError Error
     * @throws IllegalArgumentException Thrown if argument is null
     */
    public void setError(String anError) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anError == null) {
            throw new IllegalArgumentException("Argument anError is null.");
        }

        // </editor-fold>
        if (!this.error.equals(anError)) {
            this.error = anError;
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_ERROR_CHANGE);
            // Remove (possible) hint
            this.removeHint();
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Essential (get/set)">
    /**
     * True: Value item is essential, false: Otherwise
     *
     * @return True: The value item is essential. False: Otherwise
     */
    public boolean isEssential() {
        return this.isEssential;
    }

    /**
     * True: Value item is essential, false: Otherwise
     *
     * @param aValue True: The value item is essential. False: Otherwise
     */
    public void setEssential(boolean aValue) {
        this.isEssential = aValue;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ExclusiveSelectionTextColumn related properties">
    /**
     * Index of exclusive selection text column or -1
     *
     * @return Index of exclusive selection text column or -1
     */
    public int getExclusiveSelectionTextColumn() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.matrix == null || this.matrix.length == 0) {
            return -1;
        }

        // </editor-fold>
        for (int i = 0; i < this.getMatrixColumnCount(); i++) {
            if (this.isExclusiveSelectionTextColumn(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * True: Value item has exclusive selection text column, false: Otherwise
     *
     * @return True: Value item has exclusive selection text column, false:
     * Otherwise
     */
    public boolean hasExclusiveSelectionTextColumn() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.matrix == null || this.matrix.length == 0) {
            return false;
        }

        // </editor-fold>
        for (int i = 0; i < this.getMatrixColumnCount(); i++) {
            if (this.isExclusiveSelectionTextColumn(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True: Column i is exclusive selection text column, false: Otherwise
     *
     * @param aColumn Column
     * @return True: Column i is exclusive selection text column, false:
     * Otherwise
     */
    public boolean isExclusiveSelectionTextColumn(int aColumn) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.matrix == null || this.matrix.length == 0) {
            return false;
        }
        if (aColumn < 0 || aColumn >= this.getMatrixColumnCount()) {
            return false;
        }

        // </editor-fold>
        for (int i = 0; i < this.getMatrixRowCount(); i++) {
            if (!this.getTypeFormat(i, aColumn).hasExclusiveSelectionTexts()) {
                return false;
            }
        }
        return true;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Hint related properties">
    /**
     * Hint
     *
     * @return Hint
     */
    public String getHint() {
        return this.hint;
    }

    /**
     * True: Value item has hint, false: Otherwise
     *
     * @return True: Value item has hint, false: Otherwise
     */
    public boolean hasHint() {
        return this.hint.length() > 0;
    }

    /**
     * Removes hint
     */
    public void removeHint() {
        if (this.hint.length() > 0) {
            this.hint = "";
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_HINT_CHANGE);
        }
    }

    /**
     * Hint
     *
     * @param aHint Hint
     * @throws IllegalArgumentException Thrown if argument is null
     */
    public void setHint(String aHint) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aHint == null) {
            throw new IllegalArgumentException("Argument aHint is null.");
        }

        // </editor-fold>
        // NOTE: Do NOT set a hint if value item already has an error
        if (!this.hasError() && !this.hint.equals(aHint)) {
            this.hint = aHint;
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_HINT_CHANGE);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- FileTypeEnding">
    /**
     * File type ending for data type FILE
     *
     * @return File type ending for data type FILE
     */
    public String getFileTypeEnding() {
        return this.fileTypeEnding;
    }

    /**
     * True: Value item has file type ending, false: Otherwise
     *
     * @return True: Value item has file type ending, false: Otherwise
     */
    public boolean hasFileTypeEnding() {
        return this.fileTypeEnding.length() > 0;
    }

    /**
     * Removes file type ending
     */
    public void removeFileTypeEnding() {
        if (this.fileTypeEnding.length() > 0) {
            this.fileTypeEnding = "";
        }
    }

    /**
     * File type ending for data type FILE
     *
     * @param aFileTypeEnding File type ending
     * @throws IllegalArgumentException Thrown if argument is null
     */
    public void setFileTypeEnding(String aFileTypeEnding) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFileTypeEnding == null) {
            throw new IllegalArgumentException("Argument aFileTypeEnding is null.");
        }

        // </editor-fold>
        this.fileTypeEnding = aFileTypeEnding;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Locked (get/set)">
    /**
     * Lock status of value item: True: Locked, false: Unlocked
     *
     * @return True: Locked, false: Unlocked
     */
    public boolean isLocked() {
        return this.isLocked;
    }

    /**
     * Lock status of value item: True: Locked, false: Unlocked
     *
     * @param aValue True: Locked, false: Unlocked
     */
    public void setLocked(boolean aValue) {
        if (this.isLocked != aValue) {
            this.isLocked = aValue;
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_LOCK_STATUS_CHANGE);
            // Locked value item can not be active
            if (this.isLocked) {
                this.setActivity(false);
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JdpdInput (get/set)">
    /**
     * True: Value item is used for Jdpd input, false: Otherwise
     *
     * @return True: Value item is used for Jdpd input, false: Otherwise
     */
    public boolean isJdpdInput() {
        return this.isJdpdInput;
    }

    /**
     * True: Value item is used for Jdpd input, false: Otherwise
     *
     * @param aValue True: Value item is used for Jdpd input, false:
     * Otherwise
     */
    public void setJdpdInput(boolean aValue) {
        if (this.isJdpdInput != aValue) {
            this.isJdpdInput = aValue;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- LastClonedMatrix (get)">
    /**
     * Last cloned matrix
     *
     * @return Last cloned matrix
     */
    public ValueItemMatrixElement[][] getLastClonedMatrix() {
        return this.lastClonedMatrix;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MatrixClonedBeforeChange (get/set)">
    /**
     * True: Matrix will be cloned before change into this.lastClonedMatrix,
     * false: this.lastClonedMatrix will always be null
     *
     * @return True: Matrix will be cloned before change into
     * this.lastClonedMatrix, false: this.lastClonedMatrix will always be null
     */
    public boolean isMatrixClonedBeforeChange() {
        return this.isMatrixClonedBeforeChange;
    }

    /**
     * True: Matrix will be cloned before change into this.lastClonedMatrix,
     * false: this.lastClonedMatrix will always be null
     *
     * @param aValue True: Matrix will be cloned before change into
     * this.lastClonedMatrix, false: this.lastClonedMatrix will always be null
     */
    public void setMatrixClonedBeforeChange(boolean aValue) {
        this.isMatrixClonedBeforeChange = aValue;
        if (!this.isMatrixClonedBeforeChange) {
            this.lastClonedMatrix = null;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Matrix related properties">
    // <editor-fold defaultstate="collapsed" desc="-- Matrix">
    // <editor-fold defaultstate="collapsed" desc="--- Column names">
    /**
     * Column names of matrix
     *
     * @return Column names of matrix
     */
    public String[] getMatrixColumnNames() {
        return this.matrixColumnNames;
    }

    /**
     * Column name of matrix at specified position. NOTE: No checks are
     * performed.
     *
     * @param aColumn Column
     * @return Column name of matrix at specified position
     */
    public String getMatrixColumnName(int aColumn) {
        return this.matrixColumnNames[aColumn];
    }

    /**
     * Column names of matrix
     *
     * @param aColumnNames Column names of matrix
     */
    public void setMatrixColumnNames(String[] aColumnNames) {
        this.matrixColumnNames = aColumnNames;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Column widths">
    /**
     * Column width of matrix at specified position as int. NOTE: No checks are
     * performed.
     *
     * @param aColumn Column
     * @return Column width of matrix at specified position
     */
    public int getMatrixColumnWidthAsInt(int aColumn) {
        return Integer.valueOf(this.matrixColumnWidths[aColumn]);
    }

    /**
     * Matrix column widths
     *
     * @return True: Matrix column widths exist, false: Otherwise
     */
    public boolean hasMatrixColumnWidths() {
        return this.matrixColumnWidths != null;
    }

    /**
     * Column widths of matrix
     *
     * @param aColumnWidths Column widths of matrix
     */
    public void setMatrixColumnWidths(String[] aColumnWidths) {
        this.matrixColumnWidths = aColumnWidths;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Row/column count">
    /**
     * Returns row count of matrix
     *
     * @return Row count of matrix
     */
    public int getMatrixRowCount() {
        if (this.matrix != null) {
            return this.matrix.length;
        } else {
            return 0;
        }
    }

    /**
     * Returns column count of matrix
     *
     * @return Column count of matrix
     */
    public int getMatrixColumnCount() {
        if (this.matrix != null) {
            return this.matrix[0].length;
        } else {
            return 0;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Maximum number of rows">
    /**
     * Maximum number of rows of matrix
     *
     * @return Maximum number of rows of matrix
     */
    public int getMatrixMaximumNumberOfRows() {
        return this.matrixMaximumNumberOfRows;
    }

    /**
     * Maximum number of rows of matrix
     *
     * @param aMaximumNumberOfRows Maximum number of rows
     */
    public void setMatrixMaximumNumberOfRows(int aMaximumNumberOfRows) {
        this.matrixMaximumNumberOfRows = aMaximumNumberOfRows;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Matrix (get/set) and ValueItemMatrixElement (get)">
    /**
     * Matrix
     *
     * @return Matrix of this instance
     */
    public ValueItemMatrixElement[][] getMatrix() {
        return this.matrix;
    }

    /**
     * Matrix
     *
     * @param aMatrix A jagged ValueItemMatrixElement array.
     */
    public void setMatrix(ValueItemMatrixElement[][] aMatrix) {
        if (aMatrix == null) {
            // <editor-fold defaultstate="collapsed" desc="aMatrix is null">
            if (this.matrix != null) {
                // <editor-fold defaultstate="collapsed" desc="Clone matrix before change if specified">
                this.setLastClonedMatrixWithClonedCurrentMatrix();

                // </editor-fold>
                this.matrix = null;
                this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_MATRIX_CHANGE);
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="aMatrix is not null">
            // <editor-fold defaultstate="collapsed" desc="Clone matrix before change if specified">
            this.setLastClonedMatrixWithClonedCurrentMatrix();
            // </editor-fold>
            this.matrix = aMatrix;
            // <editor-fold defaultstate="collapsed" desc="Corrections">
            // Correct exclusive selection texts if necessary
            ValueItemUtils.correctExclusiveSelectionTexts(this.getExclusiveSelectionTextMatrixElementColumn(), null);
            // Correct unique value columns
            ValueItemUtils.correctUniqueValueColumns(this.matrix, -1);
            ValueItemUtils.correctPendingZeros(this.matrix);
            // </editor-fold>
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_MATRIX_CHANGE);
            // <editor-fold defaultstate="collapsed" desc="Set default column names">
            if (this.matrixColumnNames == null) {
                this.matrixColumnNames = new String[this.matrix[0].length];
                for (int i = 0; i < this.matrix[0].length; i++) {
                    this.matrixColumnNames[i] = "Column " + String.valueOf(i);
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Correct current row">
            // Set current row to last row of matrix
            if (this.currentRow >= this.matrix.length) {
                this.currentRow = this.matrix.length - 1;
            }
            // </editor-fold>
            // </editor-fold>
        }
    }

    /**
     * ValueItemMatrixElement at current matrix position (NOTE: Position is not
     * checked)
     *
     * @return ValueItemMatrixElement at current matrix position
     */
    public ValueItemMatrixElement getValueItemMatrixElement() {
        return this.matrix[this.currentRow][this.currentColumn];
    }

    /**
     * ValueItemMatrixElement at specified matrix position (NOTE: Position is
     * not checked)
     *
     * @param aRow Row
     * @param aColumn Column
     * @return ValueItemMatrixElement at specified matrix position
     */
    public ValueItemMatrixElement getValueItemMatrixElement(int aRow, int aColumn) {
        return this.matrix[aRow][aColumn];
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Diagram">
    /**
     * Returns if matrix codes a diagram
     *
     * @return True: Matrix codes a diagram, false: Otherwise
     */
    public boolean hasMatrixDiagram() {
        if (this.matrixDiagramXValueColumn < 0 || this.matrixDiagramYValueColumn < 0) {
            return false;
        }
        if (this.matrix == null) {
            return false;
        }
        if (this.matrixDiagramXValueColumn >= this.getMatrixColumnCount() || this.matrixDiagramYValueColumn >= this.getMatrixColumnCount()) {
            return false;
        }
        if (this.matrixDiagramXValueColumn >= 0 && this.matrixDiagramYValueColumn >= 0) {
            for (int i = 0; i < this.getMatrixRowCount(); i++) {
                if (!(this.getTypeFormat(i, this.matrixDiagramXValueColumn).getDataType() == ValueItemEnumDataType.TEXT
                        || this.getTypeFormat(i, this.matrixDiagramXValueColumn).getDataType() == ValueItemEnumDataType.NUMERIC)
                        || this.getTypeFormat(i, this.matrixDiagramYValueColumn).getDataType() != ValueItemEnumDataType.NUMERIC) {
                    return false;
                }
            }
        }
        // Check if there is at least one text and one numeric column or two numeric columns
        int tmpTextColumnCounter = 0;
        int tmpNumericColumnCounter = 0;
        for (int i = 0; i < this.getMatrixColumnCount(); i++) {
            if (this.getTypeFormat(i).getDataType() == ValueItemEnumDataType.TEXT) {
                tmpTextColumnCounter++;
            }
            if (this.getTypeFormat(i).getDataType() == ValueItemEnumDataType.NUMERIC) {
                tmpNumericColumnCounter++;
            }
        }
        if (tmpNumericColumnCounter < 1) {
            return false;
        }
        if (tmpNumericColumnCounter == 1 && tmpTextColumnCounter < 1) {
            return false;
        }
        return true;
    }

    /**
     * Matrix diagram columns
     *
     * @param aXValueColumn Column for x-value of matrix diagram
     * @param aYValueColumn Column for y-value of matrix diagram
     */
    public void setMatrixDiagramColumns(int aXValueColumn, int aYValueColumn) {
        this.matrixDiagramXValueColumn = aXValueColumn;
        this.matrixDiagramYValueColumn = aYValueColumn;
    }

    /**
     * Matrix diagram x-value column (default = -1)
     *
     * @return Matrix diagram x-value column (default = -1)
     */
    public int getMatrixDiagramXValueColumn() {
        return this.matrixDiagramXValueColumn;
    }

    /**
     * Matrix diagram y-value column (default = -1)
     *
     * @return Matrix diagram y-value column (default = -1)
     */
    public int getMatrixDiagramYValueColumn() {
        return this.matrixDiagramYValueColumn;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Has & current position">
    /**
     * Returns if value item has value(s)
     *
     * @return True: Value item has value(s), false: Otherwise
     */
    public boolean hasValue() {
        return this.matrix != null;
    }

    /**
     * Sets current position in Matrix
     *
     * @param aRow Row
     * @param aColumn Column
     */
    public void setCurrentMatrixPosition(int aRow, int aColumn) {
        this.currentRow = aRow;
        this.currentColumn = aColumn;
    }

    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Single value">
    // <editor-fold defaultstate="collapsed" desc="-- Get">
    /**
     * Value of matrix at current matrix position (NOTE: Position is not
     * checked)
     *
     * @return Value of matrix at current matrix position
     */
    public String getValue() {
        return this.matrix[this.currentRow][this.currentColumn].getValue();
    }

    /**
     * Formatted value of matrix at current matrix position (NOTE: Position is
     * not checked)
     *
     * @return Formatted Value of matrix at current matrix position
     */
    public String getFormattedValue() {
        return this.matrix[this.currentRow][this.currentColumn].getFormattedValue();
    }

    /**
     * Value of matrix at specified matrix position (NOTE: Position is not
     * checked)
     *
     * @param aRow Row
     * @param aColumn Column
     * @return Value of matrix at specified matrix position
     */
    public String getValue(int aRow, int aColumn) {
        return this.matrix[aRow][aColumn].getValue();
    }

    /**
     * Formatted value of matrix at specified matrix position (NOTE: Position is
     * not checked)
     *
     * @param aRow Row
     * @param aColumn Column
     * @return Formatted value of matrix at specified matrix position
     */
    public String getFormattedValue(int aRow, int aColumn) {
        return this.matrix[aRow][aColumn].getFormattedValue();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Get int, long, double">
    /**
     * Value is returned as a double value. NOTE: No checks are performed.
     *
     * @return Double value
     */
    public double getValueAsDouble() {
        return Double.parseDouble(this.matrix[this.currentRow][this.currentColumn].getValue());
    }

    /**
     * Value at specified matrix position is returned as a double value. NOTE:
     * No checks are performed.
     *
     * @param aRow Row
     * @param aColumn Column
     * @return Double value at specified matrix position
     */
    public double getValueAsDouble(int aRow, int aColumn) {

        // Debug: System.out.println("String = " + this.matrix[aRow][aColumn].getValue());
        return Double.parseDouble(this.matrix[aRow][aColumn].getValue());
    }

    /**
     * Value is returned as a float value. NOTE: No checks are performed.
     *
     * @return Float value
     */
    public float getValueAsFloat() {
        return Float.parseFloat(this.matrix[this.currentRow][this.currentColumn].getValue());
    }

    /**
     * Value at specified matrix position is returned as a float value. NOTE: No
     * checks are performed.
     *
     * @param aRow Row
     * @param aColumn Column
     * @return Float value at specified matrix position
     */
    public float getValueAsFloat(int aRow, int aColumn) {
        return Float.parseFloat(this.matrix[aRow][aColumn].getValue());
    }

    /**
     * Value is returned as a int value. NOTE: No checks are performed.
     *
     * @return Int value
     */
    public int getValueAsInt() {
        return Integer.parseInt(this.matrix[this.currentRow][this.currentColumn].getValue());
    }

    /**
     * Value at specified matrix position is returned as a int value. NOTE: No
     * checks are performed.
     *
     * @param aRow Row
     * @param aColumn Column
     * @return Int value at specified matrix position
     */
    public int getValueAsInt(int aRow, int aColumn) {
        return Integer.parseInt(this.matrix[aRow][aColumn].getValue());
    }

    /**
     * Value is returned as a long value. NOTE: No checks are performed.
     *
     * @return Long value
     */
    public long getValueAsLong() {
        return Long.parseLong(this.matrix[this.currentRow][this.currentColumn].getValue());
    }

    /**
     * Value at specified matrix position is returned as a boolean value. 
     * NOTE: No checks are performed.
     *
     * @param aRow Row
     * @param aColumn Column
     * @return Boolean value at specified matrix position
     */
    public boolean getValueAsBoolean(int aRow, int aColumn) {
        return Boolean.parseBoolean(this.matrix[aRow][aColumn].getValue());
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Set">
    /**
     * Value of matrix element at current position (NOTE: Position is not
     * checked in advance)
     *
     * @param aValue The value of matrix element at current position
     * @return True: Matrix changed due to set-operation, false: Otherwise
     */
    public boolean setValue(String aValue) {
        if (aValue != null) {
            if (this.matrix == null) {
                this.basicType = ValueItemEnumBasicType.SCALAR;
                ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
                tmpMatrix[0][0] = new ValueItemMatrixElement(aValue, new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
                this.setMatrix(tmpMatrix);
                return true;
            } else {
                return this.setValue(aValue, this.currentRow, this.currentColumn);
            }
        }
        return false;
    }

    /**
     * Value of matrix element at the specified position (NOTE: Position is not
     * checked in advance)
     *
     * @param aRow Row
     * @param aColumn Column
     * @param aValue The value of matrix element at the specified position
     * @return True: Matrix changed due to set-operation, false: Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public boolean setValue(String aValue, int aRow, int aColumn) throws IllegalArgumentException {
        if (aValue != null) {
            if (this.matrix[aRow][aColumn] == null || !this.matrix[aRow][aColumn].getValue().equals(aValue)) {
                // <editor-fold defaultstate="collapsed" desc="Clone matrix before change if specified">
                this.setLastClonedMatrixWithClonedCurrentMatrix();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Check exclusive selection texts">
                if (this.matrix[aRow][aColumn].getTypeFormat().hasExclusiveSelectionTexts()) {
                    ValueItemDataTypeFormat tmpTypeFormat = this.matrix[aRow][aColumn].getTypeFormat();
                    if (tmpTypeFormat.hasSelectionText(aValue)) {
                        // Correct default value of type format
                        tmpTypeFormat.setDefaultValue(aValue);
                    } else {
                        throw new IllegalArgumentException("aValue is not in exclusive selection texts.");
                    }
                }
                // </editor-fold>
                this.matrix[aRow][aColumn].setValue(aValue);
                // <editor-fold defaultstate="collapsed" desc="Corrections">
                // Correct exclusive selection texts if necessary
                ValueItemUtils.correctExclusiveSelectionTexts(this.getExclusiveSelectionTextMatrixElementColumn(), null);
                // Correct unique value columns
                ValueItemUtils.correctUniqueValueColumns(this.matrix, aRow);
                // Correct first row editable only columns
                ValueItemUtils.correctFirstRowEditableOnlyColumns(this.matrix);
                ValueItemUtils.correctPendingZerosColumn(this.matrix, aColumn);
                // </editor-fold>
                this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_VALUE_CHANGE);
                return true;
            }
        }
        return false;
    }

    /**
     * Sets default value of matrix element at the specified position 
     * (NOTE: Position is NOT checked in advance)
     *
     * @param aRow Row
     * @param aColumn Column
     * @return True: Matrix changed due to set-operation, false: Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public boolean setDefaultValue(int aRow, int aColumn) throws IllegalArgumentException {
        return this.setValue(matrix[aRow][aColumn].getTypeFormat().getDefaultValue(), aRow, aColumn);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Is/Has">
    /**
     * Returns if value at specified matrix position has numeric null value.
     * NOTE: No checks are performed.
     *
     * @param aRow Row
     * @param aColumn Column
     * @return True: Value at specified matrix position has numeric null value,
     * false: otherwise
     */
    public boolean hasNumericNullValue(int aRow, int aColumn) {
        return this.matrix[aRow][aColumn].hasNumericNullValue();
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- ValueItemDataTypeFormat">
    // <editor-fold defaultstate="collapsed" desc="--- Get">
    /**
     * ValueItemEnumDataType and format of current position. NOTE: Position is
     * not checked.
     *
     * @return ValueItemEnumDataType and format of current position
     */
    public ValueItemDataTypeFormat getTypeFormat() {
        return this.matrix[this.currentRow][this.currentColumn].getTypeFormat();
    }

    /**
     * ValueItemEnumDataType and format of specified current position. NOTE:
     * Position is not checked.
     *
     * @param aColumn Column
     * @return ValueItemEnumDataType and format of specified current position
     */
    public ValueItemDataTypeFormat getTypeFormat(int aColumn) {
        return this.matrix[this.currentRow][aColumn].getTypeFormat();
    }

    /**
     * ValueItemEnumDataType and format of specified position. NOTE: Position is
     * not checked.
     *
     * @param aRow Row
     * @param aColumn Column
     * @return ValueItemEnumDataType and format of specified current position
     */
    public ValueItemDataTypeFormat getTypeFormat(int aRow, int aColumn) {
        return this.matrix[aRow][aColumn].getTypeFormat();
    }

    /**
     * Returns first editable column or -1 if there is no editable column
     *
     * @return First editable column or -1 if there is no editable column
     */
    public int getFirstEditableColumn() {
        for (int i = 0; i < this.getMatrixColumnCount(); i++) {
            boolean tmpIsEditable = true;
            for (int k = 0; k < this.getMatrixRowCount(); k++) {
                if (!this.matrix[k][i].getTypeFormat().isEditable()) {
                    tmpIsEditable = false;
                    break;
                }
            }
            if (tmpIsEditable) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns if column is numeric
     *
     * @param aColumn Column
     * @return True: Column is numeric, false: Otherwise
     */
    public boolean isNumeric(int aColumn) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColumn < 0 || aColumn >= this.getMatrixColumnCount()) {
            return false;
        }

        // </editor-fold>
        for (int i = 0; i < this.matrix.length; i++) {
            if (this.matrix[i][aColumn].getTypeFormat().getDataType() != ValueItemEnumDataType.NUMERIC) {
                return false;
            }
        }
        return true;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Set default and initialize">
    /**
     * Matrix type formats. NOTE: Replaces this.matrix.
     *
     * @param aTypeFormats ValueItemEnumDataType formats
     */
    public void setDefaultTypeFormats(ValueItemDataTypeFormat[] aTypeFormats) {
        if (aTypeFormats != null) {
            this.matrix = new ValueItemMatrixElement[1][aTypeFormats.length];
            for (int i = 0; i < aTypeFormats.length; i++) {
                this.matrix[0][i] = new ValueItemMatrixElement(aTypeFormats[i]);
            }
            // NOTE: Do NOT notify update since this is an initializing method
        }
    }

    /**
     * ValueItemEnumDataType format of matrix of current position. NOTE:
     * Replaces this.matrix.
     *
     * @param aTypeFormat ValueItemEnumDataType format of value item of current
     * position
     */
    public void setDefaultTypeFormat(ValueItemDataTypeFormat aTypeFormat) {
        if (aTypeFormat != null) {
            this.matrix = new ValueItemMatrixElement[1][1];
            this.matrix[0][0] = new ValueItemMatrixElement(aTypeFormat);
            // NOTE: Do NOT notify update since this is an initializing method
        }
    }
    // </editor-fold>
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MatrixOutputOmitColumns related properties (get/set)">
    /**
     * Array with matrix output omit columns: matrixOutputOmitColumns[i] = true:
     * Column i has to be omitted in output
     *
     * @return Array with matrix output omit columns
     */
    public boolean[] getMatrixOutputOmitColumns() {
        return this.matrixOutputOmitColumns;
    }

    /**
     * Array with matrix output omit columns: matrixOutputOmitColumns[i] = true:
     * Column i has to be omitted in output
     *
     * @param aMatrixOutputOmitColumns Array with matrix output omit columns
     */
    public void setMatrixOutputOmitColumns(boolean[] aMatrixOutputOmitColumns) {
        this.matrixOutputOmitColumns = aMatrixOutputOmitColumns;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Name (get/set)">
    /**
     * Name of the input item
     *
     * @return The name of the input item
     */
    public String getName() {
        return this.name;
    }

    /**
     * Name of the input item
     *
     * @param aName The name of the input item
     */
    public void setName(String aName) {
        if (aName != null && aName.length() > 0) {
            this.name = aName;
            if (this.displayName == null || this.displayName.isEmpty()) {
                this.displayName = this.name;
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NameOfDataValueItem (get/set)">
    /**
     * Name of data value item for this display value item
     *
     * @return Name of data value item for this display value item
     */
    public String getNameOfDataValueItem() {
        return this.nameOfDataValueItem;
    }

    /**
     * Name of data value item for this display value item
     *
     * @param aName Name of data value item for this display value item
     */
    public void setNameOfDataValueItem(String aName) {
        this.nameOfDataValueItem = aName;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NameOfDisplayValueItem (get/set)">
    /**
     * Name of display value item for this data value item
     *
     * @return Name of display value item for this data value item
     */
    public String getNameOfDisplayValueItem() {
        return this.nameOfDisplayValueItem;
    }

    /**
     * Name of display value item for this data value item
     *
     * @param aName Name of display value item for this data value item
     */
    public void setNameOfDisplayValueItem(String aName) {
        this.nameOfDisplayValueItem = aName;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NodeNames (get/set)">
    /**
     * Array with node names
     *
     * @return Array with node names or null if none exists
     */
    public String[] getNodeNames() {
        return this.nodeNames;
    }

    /**
     * Last node name
     *
     * @return Last node name or null if none exists
     */
    public String getLastNodeName() {
        if (this.nodeNames == null || this.nodeNames.length == 0) {
            return null;
        } else {
            return this.nodeNames[this.nodeNames.length - 1];
        }
    }

    /**
     * Array with node names
     *
     * @param anNodeNames Array with node names
     */
    public void setNodeNames(String[] anNodeNames) {
        this.nodeNames = anNodeNames;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Selected (get/set)">
    /**
     * True: Selected, false: Otherwise
     *
     * @return True: Selected, false: Otherwise
     */
    public boolean isSelected() {
        return this.isSelected;
    }

    /**
     * True: Selected, false: Otherwise
     *
     * @param aValue True: Selected, false: Otherwise
     */
    public void setSelected(boolean aValue) {
        this.isSelected = aValue;
        if (this.isSelected) {
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_SELECTED_CHANGE);
        } else {
            this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_DESELECTED_CHANGE);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CompartmentContainer (get/set)">
    /**
     * Compartment container
     *
     * @return Compartment container
     */
    public CompartmentContainer getCompartmentContainer() {
        return this.compartmentContainer;
    }

    /**
     * Returns if compartment container exists
     *
     * @return True: Compartment container exists, false: Otherwise
     */
    public boolean hasCompartments() {
        return this.compartmentContainer != null && this.compartmentContainer.hasCompartments();
    }

    /**
     * Compartment container. NOTE: If not null then basicType must be
     * ValueItemEnumBasicType.COMPARTMENT_CONTAINER or is automatically switched
     * to.
     *
     * @param aCompartmentContainer Compartment container
     */
    public void setCompartmentContainer(CompartmentContainer aCompartmentContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCompartmentContainer == null) {
            return;
        }
        // </editor-fold>
        // Remove old compartment container first (necessary to remove this value item as a change receiver)
        if (this.hasCompartments()) {
            this.compartmentContainer.removeSingleChangeReceiver(this);
            this.compartmentContainer = null;
        }
        this.compartmentContainer = aCompartmentContainer;
        if (this.basicType != ValueItemEnumBasicType.COMPARTMENT_CONTAINER) {
            this.basicType = ValueItemEnumBasicType.COMPARTMENT_CONTAINER;
        }
        this.compartmentContainer.addChangeReceiver(this);
        this.notifyChangeReceiver(ChangeTypeEnum.VALUE_ITEM_COMPARTMENT_CHANGE);
        if (this.compartmentContainer.hasError()) {
            this.setError(ModelMessage.get("ValueItem.Error.CompartmentError"));
        } else if (this.hasError()) {
            this.removeError();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Object (get/set)">
    /**
     * Object
     *
     * @return Object
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * Returns if object instance exists
     *
     * @return True: Object instance exists, false: Otherwise
     */
    public boolean hasObject() {
        return this.object != null;
    }

    /**
     * Removes object instance
     */
    public void removeObject() {
        this.object = null;
    }

    /**
     * Object. NOTE: If not null then basicType must be
     * ValueItemEnumBasicType.OBJECT or is automatically switched to.
     *
     * @param anObject Object
     */
    public void setObject(Object anObject) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anObject == null) {
            return;
        }

        // </editor-fold>
        this.object = anObject;
        if (this.basicType != ValueItemEnumBasicType.OBJECT) {
            this.basicType = ValueItemEnumBasicType.OBJECT;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SupplementaryData (get/set)">
    /**
     * Supplementary data
     *
     * @return Supplementary data
     */
    public String[] getSupplementaryData() {
        return this.supplementaryData;
    }

    /**
     * Returns if supplementary data exists
     *
     * @return True: Supplementary data exists, false: Otherwise
     */
    public boolean hasSupplementaryData() {
        return this.supplementaryData != null;
    }

    /**
     * Removes supplementary data
     */
    public void removeSupplementaryData() {
        this.supplementaryData = null;
    }

    /**
     * Supplementary data
     *
     * @param aSupplementaryData Supplementary data
     */
    public void setSupplementaryData(String[] aSupplementaryData) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSupplementaryData == null || aSupplementaryData.length == 0) {
            return;
        }

        // </editor-fold>
        this.supplementaryData = aSupplementaryData;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- UpdateNotifier (get/set)">
    /**
     * Update notifier
     *
     * @return True: Value item is update notifier, false: Otherwise
     */
    public boolean isUpdateNotifier() {
        return this.isUpdateNotifier;
    }

    /**
     * Update notifier
     *
     * @param aValue True: Value item is update notifier, false: Otherwise
     */
    public void setUpdateNotifier(boolean aValue) {
        this.isUpdateNotifier = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ValueItemContainer (get/set)">
    /**
     * Value item valueItemContainer
     *
     * @return The valueItemContainer containing this item
     */
    public ValueItemContainer getValueItemContainer() {
        return this.valueItemContainer;
    }

    /**
     * Removes value item container
     */
    public void removeValueItemContainer() {
        if (this.valueItemContainer != null) {
            this.valueItemContainer = null;
        }
    }

    /**
     * Value item valueItemContainer
     *
     * @param aContainer The valueItemContainer containing this item
     */
    public void setValueItemContainer(ValueItemContainer aContainer) {
        this.valueItemContainer = aContainer;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- VerticalPosition (get/set)">
    /**
     * Vertical position
     *
     * @return The line number in the inputfile
     */
    public int getVerticalPosition() {
        return this.verticalPosition;
    }

    /**
     * Vertical position
     *
     * @param aVerticalPosition The line number in the inputfile
     */
    public void setVerticalPosition(int aVerticalPosition) {
        this.verticalPosition = aVerticalPosition;
    }
    
    /**
     * Increments vertical position
     */
    public void incrementVerticalPosition() {
        this.verticalPosition++;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- ChangeReceiverInterface related methods">
    /**
     * Notifies change receivers about a change
     *
     * @param aChangeTye Change type
     */
    private void notifyChangeReceiver(ChangeTypeEnum aChangeTye) {
        // IMPORTANT: Set this.hasChangeDetected
        this.hasChangeDetected = true;
        this.changeInformation.setChangeType(aChangeTye);
        super.notifyChangeReceiver(this, this.changeInformation);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Initialize method">
    /**
     * Initializes class variables
     */
    private void initialize() {
        // NOTE: This order corresponds to order in method
        // readXmlInformationV...()
        this.name = "";
        this.displayName = "";
        this.nodeNames = null;
        this.matrixOutputOmitColumns = null;
        this.matrix = null;
        this.matrixColumnNames = null;
        this.matrixColumnWidths = null;
        this.supplementaryData = null;
        this.currentRow = 0;
        this.currentColumn = 0;
        this.matrixDiagramXValueColumn = -1;
        this.matrixDiagramYValueColumn = -1;
        this.matrixMaximumNumberOfRows = Integer.MAX_VALUE;
        this.isActive = true;
        this.isDisplayed = true;
        this.basicType = ValueItemEnumBasicType.SCALAR;
        this.blockName = "";
        this.valueItemContainer = null;
        this.description = "";
        this.error = "";
        this.hint = "";
        this.verticalPosition = -1;
        this.isEssential = true;
        this.isLocked = false;
        this.isJdpdInput = false;
        this.isMatrixClonedBeforeChange = false;
        this.lastClonedMatrix = null;
        this.isUpdateNotifier = false;
        this.constantSumColumn = -1;
        this.hasChangeDetected = false;
        this.compartmentContainer = null;
        this.changeInformation = new ChangeInformation(ChangeTypeEnum.NONE, this);
        this.isSelected = false;
        this.object = null;
        this.nameOfDataValueItem = "";
        this.nameOfDisplayValueItem = "";
        this.fileTypeEnding = "";
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns exclusive selection text matrix element column
     *
     * @return Exclusive selection text matrix element column or null if none
     * exists
     */
    private ValueItemMatrixElement[] getExclusiveSelectionTextMatrixElementColumn() {
        int tmpExclusiveSelectionTextColumn = this.getExclusiveSelectionTextColumn();
        if (tmpExclusiveSelectionTextColumn > -1) {
            ValueItemMatrixElement[] tmpMatrixElementColumn = new ValueItemMatrixElement[this.matrix.length];
            for (int i = 0; i < this.matrix.length; i++) {
                tmpMatrixElementColumn[i] = this.matrix[i][tmpExclusiveSelectionTextColumn];
            }
            return tmpMatrixElementColumn;
        } else {
            return null;
        }
    }

    /**
     * Sets last cloned matrix with cloned current matrix if flag
     * this.isMatrixClonedBeforeChange is true
     */
    private void setLastClonedMatrixWithClonedCurrentMatrix() {
        if (this.isMatrixClonedBeforeChange) {
            this.lastClonedMatrix = ValueItemUtils.getClonedMatrixOfValueItemMatrixElements(this.matrix);
        }
    }

    /**
     * Sorts string array where each string represents a number
     *
     * @param aNumericStringArray String array where each string represents a
     * number
     * @return Numerically sorted string array or sorted original string array
     * if sort process could not performed
     */
    private String[] getSortedNumericStrings(String[] aNumericStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumericStringArray == null || aNumericStringArray.length == 0 || aNumericStringArray.length == 1) {
            return aNumericStringArray;
        }
        // </editor-fold>
        try {
            HashMap<Double, String> tmpMap = new HashMap<>(aNumericStringArray.length);
            double[] tmpNumericArray = new double[aNumericStringArray.length];
            for (int i = 0; i < aNumericStringArray.length; i++) {
                tmpNumericArray[i] = Double.valueOf(aNumericStringArray[i]);
                tmpMap.put(tmpNumericArray[i], aNumericStringArray[i]);
            }
            Arrays.sort(tmpNumericArray);
            String[] tmpSortedNumericStringArray = new String[aNumericStringArray.length];
            for (int i = 0; i < aNumericStringArray.length; i++) {
                tmpSortedNumericStringArray[i] = tmpMap.get(tmpNumericArray[i]);
            }
            return tmpSortedNumericStringArray;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            Arrays.sort(aNumericStringArray);
            return aNumericStringArray;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Adds matrix to a XML element
     *
     * @param anElement XML element to add the matrix to
     */
    private void addMatrixToXmlElement(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.matrix == null || anElement == null) {
            return;
        }

        // </editor-fold>
        Element tmpMatrixElement = new Element(ValueItemXmlName.MATRIX);
        for (int i = 0; i < this.matrix.length; i++) {
            Element tmpRowElement = new Element(ValueItemXmlName.MATRIX_ROW);
            for (int j = 0; j < matrix[i].length; j++) {
                tmpRowElement.addContent(this.matrix[i][j].getAsXmlElement());
            }
            tmpMatrixElement.addContent(tmpRowElement);
        }
        anElement.addContent(tmpMatrixElement);
    }

    /**
     * Reads matrix from XML representation
     *
     * @param anElement The XML element containing the matrix
     * @return Matrix or null if XML element does not contain a matrix
     */
    private ValueItemMatrixElement[][] getMatrixFromXml(Element anElement) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return null;
        }

        // </editor-fold>
        Element tmpMatrixElement = anElement.getChild(ValueItemXmlName.MATRIX);
        if (tmpMatrixElement == null) {
            return null;
        }
        List<?> tmpRowList = tmpMatrixElement.getChildren(ValueItemXmlName.MATRIX_ROW);
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpRowList.size()][];
        for (int i = 0; i < tmpRowList.size(); i++) {
            Element tmpXmlRow = (Element) tmpRowList.get(i);
            List<?> tmpColumnList = tmpXmlRow.getChildren(ValueItemMatrixElementXmlName.MATRIX_ELEMENT);
            ValueItemMatrixElement[] tmpMatrixRow = new ValueItemMatrixElement[tmpColumnList.size()];
            for (int j = 0; j < tmpColumnList.size(); j++) {
                tmpMatrixRow[j] = new ValueItemMatrixElement((Element) tmpColumnList.get(j));
            }
            tmpMatrix[i] = tmpMatrixRow;
        }
        return tmpMatrix;
    }

    /**
     * Adds matrix output omit columns to XML element
     *
     * @param anElement XML element to add the matrix output omit columns to
     */
    private void addMatrixOutputOmitColumnsToXmlElement(Element anElement) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.matrixOutputOmitColumns == null || this.matrixOutputOmitColumns.length == 0 || anElement == null) {
            return;
        }

        // </editor-fold>
        Element tmpMatrixOutputOmitColumns = new Element(ValueItemXmlName.MATRIX_OUTPUT_OMIT_COLUMNS);
        for (boolean tmpSingleMatrixOutputOmitColumn : this.matrixOutputOmitColumns) {
            tmpMatrixOutputOmitColumns.addContent(new Element(ValueItemXmlName.MATRIX_OUTPUT_OMIT_SINGLE_COLUMNS).addContent(String.valueOf(tmpSingleMatrixOutputOmitColumn)));
        }
        anElement.addContent(tmpMatrixOutputOmitColumns);
    }

    /**
     * Reads matrix output omit columns
     *
     * @param anElement The XML element containing the matrix output omit
     * columns
     * @return Array of matrix output omit columns or null if XML element does
     * not contain matrix output omit columns
     */
    private boolean[] getMatrixOutputOmitColumnsFromXml(Element anElement) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return null;
        }

        // </editor-fold>
        Element tmpMatrixOutputOmitColumnsElement = anElement.getChild(ValueItemXmlName.MATRIX_OUTPUT_OMIT_COLUMNS);
        if (tmpMatrixOutputOmitColumnsElement == null) {
            return null;
        }
        List<?> tmpMatrixOutputOmitColumnList = tmpMatrixOutputOmitColumnsElement.getChildren();
        boolean[] tmpMatrixOutputOmitColumns = new boolean[tmpMatrixOutputOmitColumnList.size()];
        for (int i = 0; i < tmpMatrixOutputOmitColumnList.size(); i++) {
            tmpMatrixOutputOmitColumns[i] = Boolean.parseBoolean(((Element) tmpMatrixOutputOmitColumnList.get(i)).getText());
        }
        return tmpMatrixOutputOmitColumns;
    }

    /**
     * Adds compartment container to XML element
     *
     * @param anElement XML element to add the compartment container to
     */
    private void addCompartmentContainer(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.compartmentContainer == null || anElement == null) {
            return;
        }

        // </editor-fold>
        try {
            anElement.addContent(this.compartmentContainer.getAsXmlElement());
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        }
    }

    /**
     * Reads compartment container
     *
     * @param anElement The XML element containing the compartment container
     * @return Compartment container or null if XML element does not contain a
     * compartment container
     */
    private CompartmentContainer getCompartmentContainerFromXML(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return null;
        }

        // </editor-fold>
        Element tmpCompartmentContainerElement = anElement.getChild(CompartmentContainerXmlName.COMPARTMENT_CONTAINER);
        if (tmpCompartmentContainerElement == null) {
            return null;
        } else {
            return new CompartmentContainer(tmpCompartmentContainerElement);
        }
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
        if (anElement.getChild(ValueItemXmlName.VERSION) == null) {
            return false;
        }

        // </editor-fold>
        String tmpVersion = anElement.getChild(ValueItemXmlName.VERSION).getText();
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
            this.setName(String.valueOf(anElement.getChild(ValueItemXmlName.NAME).getText()));
            this.setDisplayName(String.valueOf(anElement.getChild(ValueItemXmlName.DISPLAY_NAME).getText()));
            this.setNameOfDataValueItem(String.valueOf(anElement.getChild(ValueItemXmlName.NAME_OF_DATA_VALUE_ITEM).getText()));
            this.setNameOfDisplayValueItem(String.valueOf(anElement.getChild(ValueItemXmlName.NAME_OF_DISPLAY_VALUE_ITEM).getText()));
            this.setNodeNames(ModelUtils.getStringArrayFromXml(anElement, ValueItemXmlName.NODE_NAMES));
            this.setMatrixOutputOmitColumns(this.getMatrixOutputOmitColumnsFromXml(anElement));
            this.setMatrix(this.getMatrixFromXml(anElement));
            this.setMatrixColumnNames(ModelUtils.getStringArrayFromXml(anElement, ValueItemXmlName.MATRIX_COLUMN_NAMES));
            this.setMatrixColumnWidths(ModelUtils.getStringArrayFromXml(anElement, ValueItemXmlName.MATRIX_COLUMN_WIDTHS));
            this.setSupplementaryData(ModelUtils.getStringArrayFromXml(anElement, ValueItemXmlName.SUPPLEMENTARY_DATA));
            this.setMatrixDiagramColumns(Integer.valueOf(anElement.getChild(ValueItemXmlName.MATRIX_DIAGRAM_X_VALUE_COLUMN).getText()), Integer.valueOf(anElement.getChild(
                    ValueItemXmlName.MATRIX_DIAGRAM_Y_VALUE_COLUMN).getText()));
            this.setMatrixMaximumNumberOfRows(Integer.valueOf(anElement.getChild(ValueItemXmlName.MATRIX_MAXIMUM_NUMBER_OF_ROWS).getText()));
            this.setActivity(Boolean.valueOf(anElement.getChild(ValueItemXmlName.IS_ACTIVE).getText()));
            this.setDisplay(Boolean.valueOf(anElement.getChild(ValueItemXmlName.IS_DISPLAYED).getText()));
            this.setBasicType(ValueItemEnumBasicType.valueOf(anElement.getChild(ValueItemXmlName.BASIC_TYPE).getText()));
            this.setBlockName(String.valueOf(anElement.getChild(ValueItemXmlName.BLOCK_NAME).getText()));
            this.setDescription(String.valueOf(anElement.getChild(ValueItemXmlName.DESCRIPTION).getText()));
            this.setError(String.valueOf(anElement.getChild(ValueItemXmlName.ERROR).getText()));
            this.setHint(String.valueOf(anElement.getChild(ValueItemXmlName.HINT).getText()));
            this.setVerticalPosition(Integer.valueOf(anElement.getChild(ValueItemXmlName.VERTICAL_POSITION).getText()));
            this.setEssential(Boolean.valueOf(anElement.getChild(ValueItemXmlName.IS_ESSENTIAL).getText()));
            this.setLocked(Boolean.valueOf(anElement.getChild(ValueItemXmlName.IS_LOCKED).getText()));
            this.setJdpdInput(Boolean.valueOf(anElement.getChild(ValueItemXmlName.IS_JDPD_INPUT).getText()));
            this.setMatrixClonedBeforeChange(Boolean.valueOf(anElement.getChild(ValueItemXmlName.IS_MATRIX_CLONED_BEFORE_CHANGE).getText()));
            this.setUpdateNotifier(Boolean.valueOf(anElement.getChild(ValueItemXmlName.IS_UPDATE_NOTIFIER).getText()));
            this.setConstantSumColumn(Integer.valueOf(anElement.getChild(ValueItemXmlName.CONSTANT_SUM_COLUMN).getText()));
            this.setCompartmentContainer(this.getCompartmentContainerFromXML(anElement));
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
