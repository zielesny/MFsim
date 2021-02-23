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
package de.gnwi.mfsim.model.changeNotification;
/**
 * Change types for change receivers
 *
 * @author Achim Zielesny
 */
public enum ChangeTypeEnum {

    // <editor-fold defaultstate="collapsed" desc="None">
    /**
     * None
     */
    NONE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="ValueItem changes">
    /**
     * Value item: Value change
     */
    VALUE_ITEM_VALUE_CHANGE,
    /**
     * Value item: Matrix change
     */
    VALUE_ITEM_MATRIX_CHANGE,
    /**
     * Value item: Activity change
     */
    VALUE_ITEM_ACTIVITY_CHANGE,
    /**
     * Value item: Display change
     */
    VALUE_ITEM_DISPLAY_CHANGE,
    /**
     * Value item: Lock status change
     */
    VALUE_ITEM_LOCK_STATUS_CHANGE,
    /**
     * Value item: Deselected change
     */
    VALUE_ITEM_DESELECTED_CHANGE,
    /**
     * Value item: Selected change
     */
    VALUE_ITEM_SELECTED_CHANGE,
    /**
     * Value item: Hint change
     */
    VALUE_ITEM_HINT_CHANGE,
    /**
     * Value item: Error change
     */
    VALUE_ITEM_ERROR_CHANGE,
    /**
     * Value item: Change of compartment
     */
    VALUE_ITEM_COMPARTMENT_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="ValueItemContainer changes">

    /**
     * Value item container: Number of value items change
     */
    VALUE_ITEM_CONTAINER_NUMBER_CHANGE,
    /**
     * Value item container: Sort value items change
     */
    VALUE_ITEM_CONTAINER_SORT_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Structure syntax changes">

    /**
     * Structure syntax: Error change
     */
    STRUCTURE_SYNTAX_ERROR_CHANGE,
    /**
     * Structure syntax: Correct change
     */
    STRUCTURE_SYNTAX_CORRECT_CHANGE,
    /**
     * Structure syntax: No change
     */
    STRUCTURE_SYNTAX_NO_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Protein definition changes">

    /**
     * Protein definition: Correct change
     */
    PROTEIN_DEFINITION_CORRECT_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Peptide syntax changes">

    /**
     * Peptide syntax: Error change
     */
    PEPTIDE_SYNTAX_ERROR_CHANGE,
    /**
     * Peptide syntax: Correct change
     */
    PEPTIDE_SYNTAX_CORRECT_CHANGE,
    /**
     * Peptide syntax: No change
     */
    PEPTIDE_SYNTAX_NO_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="BodyInterface changes">

    /**
     * Body interface: Position change
     */
    BODY_INTERFACE_POSITION_CHANGE,
    /**
     * Body interface: Deselect body change
     */
    BODY_INTERFACE_DESELECT_BODY_CHANGE,
    /**
     * Body interface: Select body change
     */
    BODY_INTERFACE_SELECT_BODY_CHANGE,
    /**
     * Body interface: Size change
     */
    BODY_INTERFACE_SIZE_CHANGE,
    /**
     * Body interface: View change
     */
    BODY_INTERFACE_BOX_VIEW_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CompartmentContainer changes">

    /**
     * Compartment container: Change
     */
    COMPARTMENT_CONTAINER_CHANGE,
    /**
     * Compartment container: Error change
     */
    COMPARTMENT_CONTAINER_ERROR_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CompartmentBox changes">

    /**
     * Compartment box: Change
     */
    COMPARTMENT_BOX_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelValueItemEditController changes">

    /**
     * Tree selection changed
     */
    CUSTOM_PANEL_VALUE_ITEM_EDIT_CONTROLLER_TREE_SELECTION_CHANGE,
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Internal error">

    /**
     * Internal error
     */
    INTERNAL_ERROR
    // </editor-fold>

}
