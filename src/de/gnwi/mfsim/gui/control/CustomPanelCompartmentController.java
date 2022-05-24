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
package de.gnwi.mfsim.gui.control;

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Controller class for CustomPanelCompartment
 *
 * @author Achim Zielesny
 */
public class CustomPanelCompartmentController implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Compartment panel this controller is made for
     */
    private CustomPanelCompartment compartmentPanel;

    /**
     * Compartment container
     */
    private CompartmentContainer compartmentContainer;

    /**
     * Compartment edit controller
     */
    private CustomPanelCompartmentEditController compartmentEditController;

    /**
     * Compartment graphics controller
     */
    private CustomPanelCompartmentGraphicsController compartmentGraphicsController;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCompartmentPanel Compartment panel this controller is made for
     * @param aCompartmentContainer Compartment container
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelCompartmentController(CustomPanelCompartment aCompartmentPanel, CompartmentContainer aCompartmentContainer)
            throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aCompartmentPanel == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aCompartmentContainer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">

            this.compartmentPanel = aCompartmentPanel;
            this.compartmentContainer = aCompartmentContainer;
            this.compartmentEditController = new CustomPanelCompartmentEditController(this.compartmentPanel.getCompartmentEditPanel(),
                    this.compartmentContainer);
            this.compartmentGraphicsController = new CustomPanelCompartmentGraphicsController(this.compartmentPanel.getCompartmentGraphicsPanel(),
                    this.compartmentContainer);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listener">
            this.compartmentPanel.getMainTabbedPanel().addChangeListener(new ChangeListener() {

                public void stateChanged(final ChangeEvent e) {
                    try {
                        CustomPanelCompartmentController.this.reactOnMainTabChange();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()",
                                "CustomPanelCompartmentController"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add this instance as change receiver">
            this.compartmentContainer.addChangeReceiver(this);

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor()",
                    "CustomPanelCompartmentController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a changeInformation receiver
     *
     * @param aChangeNotifier Object that notifies changeInformation
     * @param aChangeInfo Change information
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        try {
            if (aChangeNotifier == this.compartmentContainer && aChangeInfo.getChangeType() == ChangeTypeEnum.COMPARTMENT_CONTAINER_ERROR_CHANGE) {
                if (this.compartmentContainer.hasError()) {
                    this.compartmentPanel.getMainTabbedPanel().setEnabledAt(1, false);
                } else {
                    this.compartmentPanel.getMainTabbedPanel().setEnabledAt(1, true);
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "notifyChange()",
                    "CustomPanelCompartmentController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Reacts on main tab change
     */
    private void reactOnMainTabChange() {
        int tmpIndex = this.compartmentPanel.getMainTabbedPanel().getSelectedIndex();
        switch (tmpIndex) {
            case 0:
                // <editor-fold defaultstate="collapsed" desc="Compartments panel">
                if (this.compartmentContainer.getCompartmentBox().getSelectedBody() != null) {
                    this.compartmentEditController.getValueItemEditController().selectValueItem(
                        this.compartmentContainer.getCompartmentBox().getSelectedBody().getGeometryDataValueItem().getNameOfDisplayValueItem());
                }
                // </editor-fold>
                break;
            case 1:
                // <editor-fold defaultstate="collapsed" desc="Graphics panel">
                if (this.graphicsUtilityMethods.isGeometryValueItem(this.compartmentEditController.getValueItemEditController().getCurrentValueItem())) {
                    this.compartmentEditController.getValueItemEditController().getCurrentValueItem().getDataValueItem().setSelected(true);
                }
                this.compartmentGraphicsController.showInformation();
                // </editor-fold>
                break;
        }
    }
    // </editor-fold>

}
