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
package de.gnwi.mfsim.gui.control;

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import de.gnwi.mfsim.model.util.ModelUtils;

/**
 * Controller class for CustomPanelCompartmentEdit
 *
 * @author Achim Zielesny
 */
public class CustomPanelCompartmentEditController implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * CustomPanelValueItemEditController
     */
    private CustomPanelValueItemEditController valueItemEditController;

    /**
     * CustomPanelCompartmentEdit this controller controls
     */
    private CustomPanelCompartmentEdit compartmentEditPanel;

    /**
     * Compartment container
     */
    private CompartmentContainer compartmentContainer;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelCompartmentEdit Panel this controller is made for
     * @param aCompartmentContainer Compartment container
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelCompartmentEditController(CustomPanelCompartmentEdit aCustomPanelCompartmentEdit, CompartmentContainer aCompartmentContainer) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelCompartmentEdit == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aCompartmentContainer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.compartmentEditPanel = aCustomPanelCompartmentEdit;
            this.compartmentContainer = aCompartmentContainer;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.compartmentEditPanel.getEditCompartmentNameButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelCompartmentEditController.this.editCompartmentName();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelCompartmentEditController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.compartmentEditPanel.getAddCompartmentSphereButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelCompartmentEditController.this.addCompartmentSphere();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String
                                .format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelCompartmentEditController"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.compartmentEditPanel.getAddCompartmentXyLayerButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelCompartmentEditController.this.addCompartmentXyLayer();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String
                                .format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelCompartmentEditController"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentEditPanel.getRemoveCompartmentButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelCompartmentEditController.this.removeSelectedCompartment();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String
                                .format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelCompartmentEditController"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Configure ...getCustomPanelValueItemEdit()">
            // Select status panel is invisible, error and hint tab are removed, table-data schema management is available
            GuiUtils.configureCustomPanelValueItemEdit(this.compartmentEditPanel.getCustomValueItemEditPanel(), true, false, false, true);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this.valueItemEditController">
            this.valueItemEditController = new CustomPanelValueItemEditController(this.compartmentEditPanel.getCustomValueItemEditPanel(), this.compartmentContainer.getValueItemContainer());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add this.valueItemEditController as change receiver">
            this.valueItemEditController.addChangeReceiver(this);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update remove button visibility">
            this.updateRemoveButton();

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor()", "CustomPanelCompartmentEditController"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a change receiver
     *
     * @param aChangeInfo Change information
     * @param aChangeNotifier Object that notifies change
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        try {
            if (aChangeNotifier == this.valueItemEditController) {
                switch (aChangeInfo.getChangeType()) {
                    case CUSTOM_PANEL_VALUE_ITEM_EDIT_CONTROLLER_TREE_SELECTION_CHANGE:
                        this.updateRemoveButton();
                        break;
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "notifyChange()", "CustomPanelCompartmentEditController"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Value item edit controller
     *
     * @return Value item edit controller
     */
    public CustomPanelValueItemEditController getValueItemEditController() {
        return this.valueItemEditController;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Actions">
    /**
     * Edit compartment name
     */
    private void editCompartmentName() {
        ValueItemContainer tmpCompartmentNameValueItemContainer = new ValueItemContainer(null);
        String[] tmpNodeNames = new String[]{GuiMessage.get("CustomPanelCompartmentEditController.Compartments.root")};
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName("COMPARTMENT_NAME");
        tmpValueItem.setDisplayName(GuiMessage.get("CustomPanelCompartmentEditController.Compartments.editCompartmentName.displayName"));
        tmpValueItem.setDescription(GuiMessage.get("CustomPanelCompartmentEditController.Compartments.editCompartmentName.description"));
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT_EMPTY));
        tmpValueItem.setValue(this.compartmentContainer.getCompartmentName());
        tmpValueItem.setVerticalPosition(0);
        tmpCompartmentNameValueItemContainer.addValueItem(tmpValueItem);
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("CustomPanelCompartmentEditController.Compartments.editCompartmentName.title"), tmpCompartmentNameValueItemContainer)) {
            String tmpCompartmentName = tmpCompartmentNameValueItemContainer.getValueOfValueItem("COMPARTMENT_NAME");
            if (tmpCompartmentName.isEmpty()) {
                this.compartmentContainer.removeCompartmentName();
                this.compartmentEditPanel.getAddCompartmentSphereButton().setText(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentSphereButton.text"));
                this.compartmentEditPanel.getAddCompartmentXyLayerButton().setText(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentXyLayerButton.text"));
            } else {
                this.compartmentContainer.setCompartmentName(tmpCompartmentName);
                this.compartmentEditPanel.getAddCompartmentSphereButton().setText(String.format(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentSphereButton.textFormat"), tmpCompartmentName)
                );
                this.compartmentEditPanel.getAddCompartmentXyLayerButton().setText(String.format(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentXyLayerButton.textFormat"), tmpCompartmentName)
                );
            }
        }
    }
    
    /**
     * Adds a spherical compartment
     */
    private void addCompartmentSphere() {
        this.compartmentContainer.addCompartmentSphere();
    }

    /**
     * Adds a xy-layer compartment
     */
    private void addCompartmentXyLayer() {
        this.compartmentContainer.addCompartmentXyLayer();
    }

    /**
     * Removes selected compartment
     */
    private void removeSelectedCompartment() {
        ValueItem tmpCurrentValueItem = this.valueItemEditController.getCurrentValueItem();
        if (tmpCurrentValueItem != null) {
            this.compartmentContainer.removeCompartment(tmpCurrentValueItem.getBlockName());
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    /**
     * Updates visibility of remove button
     */
    private void updateRemoveButton() {
        ValueItem tmpCurrentValueItem = this.valueItemEditController.getCurrentValueItem();
        if (tmpCurrentValueItem != null) {
            this.compartmentEditPanel.getRemoveCompartmentButton().setVisible(this.compartmentContainer.isCompartment(tmpCurrentValueItem.getBlockName()));
        } else {
            this.compartmentEditPanel.getRemoveCompartmentButton().setVisible(false);
        }
    }
    // </editor-fold>
    // </editor-fold>

}
