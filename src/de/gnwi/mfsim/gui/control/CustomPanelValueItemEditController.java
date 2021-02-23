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

import de.gnwi.mfsim.gui.util.valueItem.ValueItemKeyListener;
import de.gnwi.mfsim.gui.dialog.DialogValueItemShow;
import de.gnwi.mfsim.gui.dialog.DialogCompartmentEdit;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.dialog.DialogJmolViewerShow;
import de.gnwi.mfsim.gui.dialog.DialogSlicerShow;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemDocumentListener;
import de.gnwi.mfsim.gui.dialog.DialogValueItemMatrixDiagram;
import de.gnwi.mfsim.gui.dialog.DialogProgress;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemSelectionItemListener;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.util.ComboBoxColorCellRenderer;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemInputVerifier;
import de.gnwi.mfsim.model.util.ExtensionFileFilter;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionCalculationTask;
import de.gnwi.mfsim.model.job.JobUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumStatus;
import java.io.File;
import java.awt.CardLayout;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Controller class for CustomPanelValueItemEdit
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemEditController extends ChangeNotifier implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility job methods
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * Change information
     */
    private ChangeInformation changeInformation;

    /**
     * CustomPanelValueItemEdit instance
     */
    private CustomPanelValueItemEdit valueItemEditPanel;

    /**
     * Current value item instance
     */
    private ValueItem currentValueItem;

    /**
     * Value item container
     */
    private ValueItemContainer valueItemContainer;

    /**
     * Controller of this.customValueItemEditPanel
     */
    private CustomPanelValueItemFlexibleMatrixController valueItemFlexibleMatrixPanelController;

    /**
     * Value item document listener for value item related text fields
     */
    private ValueItemDocumentListener valueItemDocumentListener;

    /**
     * Value item input verifier for value item related text fields
     */
    private ValueItemInputVerifier valueItemInputVerifier;

    /**
     * Value item key listener for value item related text fields
     */
    private ValueItemKeyListener valueItemKeyListener;

    /**
     * Value item item listener for value item related combo boxes
     */
    private ValueItemSelectionItemListener valueItemSelectionItemListener;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomValueItemEditPanel Panel this controller is made for
     * @param aValueItemContainer Value item container
     * @throws IllegalArgumentException Thrown if an argument is null/empty
     */
    public CustomPanelValueItemEditController(CustomPanelValueItemEdit aCustomValueItemEditPanel, ValueItemContainer aValueItemContainer) throws IllegalArgumentException {
        super();

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomValueItemEditPanel == null || aValueItemContainer == null || aValueItemContainer.getSize() == 0) {
            throw new IllegalArgumentException("An argument is null or empty.");
        }

        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.changeInformation = new ChangeInformation(ChangeTypeEnum.CUSTOM_PANEL_VALUE_ITEM_EDIT_CONTROLLER_TREE_SELECTION_CHANGE, null);
            this.valueItemEditPanel = aCustomValueItemEditPanel;
            this.valueItemContainer = aValueItemContainer;
            // Set combo box for simulation box display selection
            GuiUtils.setComboBoxForSimulationBoxDisplaySelection(this.valueItemEditPanel.getSelectSimulationBoxDisplayComboBox());
            // Set table-data schemata
            this.updateTableDataSchemaSettings(null);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            // NOTE: Add listeners FIRST ...
            this.valueItemEditPanel.getCollapseTreeButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.collapseAllNodesOfTree();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getExpandTreeButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.expandAllNodesOfTree();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getSelectErrorStatusButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.setValueItemsWithError();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getSelectHintStatusButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.setValueItemsWithHint();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getFeatureOverviewTree().addTreeSelectionListener(new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.showSelectedValueItem(e.getNewLeadSelectionPath());
                        CustomPanelValueItemEditController.super.notifyChangeReceiver(CustomPanelValueItemEditController.this, CustomPanelValueItemEditController.this.changeInformation);
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "valueChanged()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            if (this.valueItemEditPanel.getSelectValueItemStatusPanel().isVisible()) {
                this.valueItemEditPanel.getSelectValueItemStatusComboBox().addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        try {
                            CustomPanelValueItemEditController.this.checkSelectedValueItemStatus();
                        } catch (Exception anExcpetion) {

                            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                    "CustomPanelValueItemEditController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                            // </editor-fold>
                        }
                    }

                });
            }
            this.valueItemEditPanel.getActivityCheckBox().addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.checkActivityCheckBox();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null,
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemEditController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getTimestampNowButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.setDateNow();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getDirectoryBrowseButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.browseDirectory();
                    } catch (Exception anExcpetion) {
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getFileBrowseButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.browseFile();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getMatrixDiagramButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.showMatrixDiagram();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getCompartmentEditButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.editCompartments();
                    } catch (Exception anExcpetion) {
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getCompartmentViewButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.viewParticlesInCompartments();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getShowBoxPropertiesButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.showBoxProperties();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getCompartmentRemoveButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.removeCompartments();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getSelectSimulationBoxDisplayComboBox().addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    try {
                        GuiUtils.evaluateComboBoxForSimulationBoxDisplaySelection(CustomPanelValueItemEditController.this.valueItemEditPanel.getSelectSimulationBoxDisplayComboBox());
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelValueItemEditController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getSetCurrentTableDataSchemaButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.createNewSchemaWithCurrentTableData();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getApplyTableDataSchemaButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.applySelectedTableDataSchema();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getShowTableDataSchemaButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.showTableDataSchema();
                    } catch (Exception anExcpetion) {
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.valueItemEditPanel.getRemoveTableDataSchemaButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemEditController.this.removeSelectedTableDataSchema();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set value item status select options and fill tree">
            // ... then fill tree (otherwise the tree selection will not lead to correct display)
            if (this.valueItemEditPanel.getSelectValueItemStatusPanel().isVisible()) {
                this.updateValueItemStatusSelectOptions();
            } else {
                GuiUtils.fillTreeWithValueItemsAndSelectFirstLeaf(this.valueItemEditPanel.getFeatureOverviewTree(), this.valueItemContainer.getSortedValueItemsOfContainer(), ValueItemEnumStatus.ALL);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update error information">
            this.updateErrorInformation();
            this.updateHintInformation();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this as change receiver of this.valueItemContainer">
            this.valueItemContainer.addChangeReceiver(this);

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor()", "CustomPanelValueItemEditController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
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
    @Override
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        try {
            // <editor-fold defaultstate="collapsed" desc="Change notifier this.valueItemContainer">
            if (aChangeNotifier == this.valueItemContainer) {
                switch (aChangeInfo.getChangeType()) {
                    case VALUE_ITEM_ACTIVITY_CHANGE:
                    case VALUE_ITEM_LOCK_STATUS_CHANGE:
                        this.updateValueItemStatusSelectOptions();
                        this.repaintTree();
                        break;
                    case VALUE_ITEM_HINT_CHANGE:
                        this.updateHintInformation();
                        this.updateValueItemStatusSelectOptions();
                        this.repaintTree();
                        break;
                    case VALUE_ITEM_ERROR_CHANGE:
                        this.updateErrorInformation();
                        this.updateValueItemStatusSelectOptions();
                        this.repaintTree();
                        break;
                    case VALUE_ITEM_CONTAINER_NUMBER_CHANGE:
                        this.updateHintInformation();
                        this.updateErrorInformation();
                        this.updateValueItemStatusSelectOptions();
                        this.rebuildTreeAfterChange((String) aChangeInfo.getInfo());
                        break;
                    case VALUE_ITEM_CONTAINER_SORT_CHANGE:
                        this.rebuildTreeAfterChange((String) aChangeInfo.getInfo());
                        break;
                    case VALUE_ITEM_COMPARTMENT_CHANGE:
                        this.repaintTree();
                        break;
                    case VALUE_ITEM_MATRIX_CHANGE:
                        this.updateTableDataSchemaSettings(null);
                        break;
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Change notifier this.currentValueItem">
            if (aChangeNotifier == this.currentValueItem) {
                switch (aChangeInfo.getChangeType()) {
                    case VALUE_ITEM_HINT_CHANGE:
                        this.updateHint();
                        break;
                    case VALUE_ITEM_ERROR_CHANGE:
                        this.updateError();
                        break;
                }
                //this.updateTableDataSchemaSettingsForCurrentValueItem();
                this.updateTableDataSchemaSettings(null);
            }
            // </editor-fold>
        } catch (Exception anExcpetion) {
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "notifyChange()", "CustomPanelValueItemEditController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Selects specified value item
     *
     * @param aValueItemName Name of value item
     */
    public void selectValueItem(String aValueItemName) {
        try {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (aValueItemName == null || aValueItemName.isEmpty()) {
                return;
            }
            if (!this.valueItemContainer.hasValueItem(aValueItemName)) {
                return;
            }
            if (this.currentValueItem != null && this.currentValueItem.getName().equals(aValueItemName)) {
                return;
            }
            // </editor-fold>
            GuiUtils.expandAndSelectDefinedLeaf(this.valueItemEditPanel.getFeatureOverviewTree(), false, aValueItemName);
        } catch (Exception anExcpetion) {
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "notifyChange()", "selectValueItem"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="- CurrentValueItem">
    /**
     * Currently selected value item
     *
     * @return Currently selected value item
     */
    public ValueItem getCurrentValueItem() {
        return this.currentValueItem;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Action methods">
    // <editor-fold defaultstate="collapsed" desc="-- Compartment related action methods">
    /**
     * Edits compartments
     */
    private void editCompartments() {
        CompartmentContainer tmpCompartmentContainer = this.currentValueItem.getCompartmentContainer().getClone();
        if (DialogCompartmentEdit.hasChanged(GuiMessage.get("DialogCompartmentEdit.title"), tmpCompartmentContainer)) {
            this.currentValueItem.setCompartmentContainer(tmpCompartmentContainer);
        }
        this.updateCompartmentPanel();
    }

    /**
     * View particles in simulation box
     */
    private void viewParticlesInCompartments() {
        GraphicalParticlePositionCalculationTask tmpProgressTask = new GraphicalParticlePositionCalculationTask(this.currentValueItem.getCompartmentContainer());
        if (!DialogProgress.hasCanceled(GuiMessage.get("GraphicalParticlePositionCalculation"), tmpProgressTask)) {
            // <editor-fold defaultstate="collapsed" desc="Set value item container for molecule/particles display settings in Preferences">
            ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainer
                    = tmpProgressTask.getGraphicalParticlePositionInfo().getGraphicalParticleInfo().getMoleculeDisplaySettingsValueItemContainer();
            ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainerFromJobInput
                    = this.jobUtilityMethods.getMoleculeDisplaySettingsValueItemContainer(this.currentValueItem.getValueItemContainer());
            for (ValueItem tmpMoleculeDisplaySettingsValueItemFromJobInput : tmpMoleculeDisplaySettingsValueItemContainerFromJobInput.getValueItemsOfContainer()) {
                // NOTE: A cloning of tmpMoleculeDisplaySettingsValueItemFromJobInput is NOT necessary since tmpMoleculeDisplaySettingsValueItemContainerFromJobInput already contains cloned value 
                //       items from job input
                tmpMoleculeDisplaySettingsValueItemContainer.replaceValueItemWithKeptVerticalPositionAndNodeNames(tmpMoleculeDisplaySettingsValueItemFromJobInput);
            }
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().setMoleculeDisplaySettingsValueItemContainer(tmpMoleculeDisplaySettingsValueItemContainer);

            // </editor-fold>
            if (Preferences.getInstance().isSimulationBoxSlicer()) {
                DialogSlicerShow.show(String.format(GuiMessage.get("DialogSlicerShow.titleFormat"), this.currentValueItem.getDisplayName()),
                        tmpProgressTask.getGraphicalParticlePositionInfo());
            } else {
                // Save and remove rotation
                Preferences.getInstance().saveAndRemoveRotation();
                DialogJmolViewerShow.show(String.format(GuiMessage.get("DialogJmolViewerShow.titleFormat"), this.currentValueItem.getDisplayName()),
                        tmpProgressTask.getGraphicalParticlePositionInfo());
                // Restore rotation
                Preferences.getInstance().restoreRotation();
            }

        }
    }

    /**
     * Removes compartments
     */
    private void removeCompartments() {
        if (GuiUtils.getYesNoDecision(GuiMessage.get("RemoveCompartments.FrameTitle"), GuiMessage.get("RemoveCompartments.Message"))) {
            this.currentValueItem.setCompartmentContainer(new CompartmentContainer(this.currentValueItem.getValueItemContainer()));
        }
        updateCompartmentPanel();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Miscellaneous action methods">
    /**
     * Browse directory for value item of type DIRECTORY
     */
    private void browseDirectory() {
        try {
            // Set wait cursor since notifyDependentValueItemsForUpdate may cause a delay
            MouseCursorManagement.getInstance().setWaitCursor();
            String tmpDirectoryPath;
            if (this.currentValueItem.getValue() == null || this.currentValueItem.getValue().isEmpty() || !(new File(this.currentValueItem.getValue()).isDirectory())) {
                 tmpDirectoryPath = GuiUtils.selectDirectory(Preferences.getInstance().getLastSelectedPath(), GuiMessage.get("Chooser.selectDirectoryPath"));
            } else {
                 tmpDirectoryPath = GuiUtils.selectDirectory(this.currentValueItem.getValue(), GuiMessage.get("Chooser.selectDirectoryPath"));
            }
            if (tmpDirectoryPath != null && !tmpDirectoryPath.isEmpty() && !tmpDirectoryPath.equalsIgnoreCase(this.currentValueItem.getValue())) {
                // Set directory in text field and correct text color
                this.valueItemEditPanel.getDirectoryTextField().setText(tmpDirectoryPath);
                this.valueItemEditPanel.getDirectoryTextField().setForeground(GuiDefinitions.TEXT_FIELD_FOREGROUND_CORRECT_COLOR);
                // Set directory as value of current value item
                if (this.currentValueItem.setValue(tmpDirectoryPath)) {
                    this.currentValueItem.notifyDependentValueItemsForUpdate();
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Browse file for value item of type FILE
     */
    private void browseFile() {
        try {
            // Set wait cursor since notifyDependentValueItemsForUpdate may cause a delay
            MouseCursorManagement.getInstance().setWaitCursor();
            ExtensionFileFilter tmpExtensionFileFilter = null;
            if (this.currentValueItem.hasFileTypeEnding()) {
                tmpExtensionFileFilter = new ExtensionFileFilter(new String[]{this.currentValueItem.getFileTypeEnding()});
            }
            String tmpSingleFilePathName = GuiUtils.selectSingleFile(Preferences.getInstance().getLastSelectedPath(), GuiMessage.get("Chooser.selectFile"),
                    tmpExtensionFileFilter);
            if (tmpSingleFilePathName != null && !tmpSingleFilePathName.isEmpty() && !tmpSingleFilePathName.equalsIgnoreCase(this.currentValueItem.getValue())) {
                // Set file in text field and correct text color
                this.valueItemEditPanel.getFileTextField().setText(tmpSingleFilePathName);
                this.valueItemEditPanel.getFileTextField().setForeground(GuiDefinitions.TEXT_FIELD_FOREGROUND_CORRECT_COLOR);
                // Set directory as value of current value item
                if (this.currentValueItem.setValue(tmpSingleFilePathName)) {
                    this.currentValueItem.notifyDependentValueItemsForUpdate();
                }

            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Check activity check box
     */
    private void checkActivityCheckBox() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.currentValueItem == null || this.currentValueItem.isActive() == this.valueItemEditPanel.getActivityCheckBox().isSelected()) {
            return;
        }

        // </editor-fold>
        try {
            // Set wait cursor since notifyDependentValueItemsForUpdate may cause a delay
            MouseCursorManagement.getInstance().setWaitCursor();
            if (this.valueItemEditPanel.getActivityCheckBox().isSelected()) {
                this.currentValueItem.setActivity(true);
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(0, true);
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setSelectedIndex(0);
            } else {
                this.currentValueItem.setActivity(false);
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(0, false);
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setSelectedIndex(1);
            }
            // IMPORTANT: Notify dependent value items for update (including current value item itself if necessary)
            this.currentValueItem.notifyDependentValueItemsForUpdate();
            TreePath tmpSelectedTreePath = this.valueItemEditPanel.getFeatureOverviewTree().getSelectionPath();
            this.showSelectedValueItem(tmpSelectedTreePath);
            this.updateValueItemStatusSelectOptions();
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Correct value item status select options (remove possible undefined
     * option)
     */
    private void correctValueItemStatusSelectOptions() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.valueItemEditPanel.getSelectValueItemStatusPanel().isVisible()) {
            return;
        }
        if (ValueItemEnumStatus.toValueItemStatus(this.valueItemEditPanel.getSelectValueItemStatusComboBox().getSelectedItem().toString()) == ValueItemEnumStatus.UNDEFINED) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check if correction is necessary">
        boolean tmpIsCorrectionNecessary = false;
        for (int i = 0; i < this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getSize(); i++) {
            if (ValueItemEnumStatus.toValueItemStatus(this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getElementAt(i).toString()) == ValueItemEnumStatus.UNDEFINED) {
                tmpIsCorrectionNecessary = true;
                break;
            }
        }
        if (!tmpIsCorrectionNecessary) {
            return;
        }

        // </editor-fold>
        String tmpSelectedItem = this.valueItemEditPanel.getSelectValueItemStatusComboBox().getSelectedItem().toString();
        // Remove listeners to avoid events
        GuiUtils.removeItemListeners(this.valueItemEditPanel.getSelectValueItemStatusComboBox());
        // Remove undefined entry
        LinkedList<String> tmpStatusOptionList = new LinkedList<String>();
        for (int i = 0; i < this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getSize(); i++) {
            if (ValueItemEnumStatus.toValueItemStatus(this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getElementAt(i).toString()) != ValueItemEnumStatus.UNDEFINED) {
                tmpStatusOptionList.addLast(this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getElementAt(i).toString());
            }
        }
        this.valueItemEditPanel.getSelectValueItemStatusComboBox().setModel(new DefaultComboBoxModel(tmpStatusOptionList.toArray(new String[0])));
        this.valueItemEditPanel.getSelectValueItemStatusComboBox().setSelectedItem(tmpSelectedItem);
        // Add listener again
        this.valueItemEditPanel.getSelectValueItemStatusComboBox().addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                CustomPanelValueItemEditController.this.checkSelectedValueItemStatus();
            }

        });
    }

    /**
     * Sets value item with error
     */
    private void setValueItemsWithError() {
        this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().setSelectedItem(ValueItemEnumStatus.HAS_ERROR.toRepresentation());
        this.collapseAllNodesOfTree();
    }

    /**
     * Sets value item with hint
     */
    private void setValueItemsWithHint() {
        if (!this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getSelectedItem().toString().equals(ValueItemEnumStatus.HAS_HINT.toRepresentation())) {
            this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().setSelectedItem(ValueItemEnumStatus.HAS_HINT.toRepresentation());
        } else {
            this.checkSelectedValueItemStatus();
        }
        this.collapseAllNodesOfTree();
    }

    /**
     * Set date now for value item of type TIMESTAMP
     */
    private void setDateNow() {
        try {
            // Set wait cursor since notifyDependentValueItemsForUpdate may cause a delay
            MouseCursorManagement.getInstance().setWaitCursor();
            String tmpDateNow = ModelUtils.getTimestampInStandardFormat();
            // Set date in text field and correct text color
            this.valueItemEditPanel.getTimestampTextField().setText(tmpDateNow);
            this.valueItemEditPanel.getTimestampTextField().setForeground(GuiDefinitions.TEXT_FIELD_FOREGROUND_CORRECT_COLOR);
            // Set directory as value of current value item
            if (this.currentValueItem.setValue(tmpDateNow)) {
                this.currentValueItem.notifyDependentValueItemsForUpdate();
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Feature tree related action methods">
    /**
     * Check selected value item status
     */
    private void checkSelectedValueItemStatus() {
        ValueItemEnumStatus tmpValueItemStatus = ValueItemEnumStatus.toValueItemStatus(this.valueItemEditPanel.getSelectValueItemStatusComboBox().getSelectedItem().toString());
        String tmpCurrentValueItemName = null;
        if (this.currentValueItem != null) {
            tmpCurrentValueItemName = this.currentValueItem.getName();
        }
        this.correctValueItemStatusSelectOptions();
        if (tmpCurrentValueItemName == null) {
            GuiUtils.fillTreeWithValueItemsAndSelectFirstLeaf(this.valueItemEditPanel.getFeatureOverviewTree(), this.valueItemContainer.getSortedValueItemsOfContainer(), tmpValueItemStatus);
        } else {
            GuiUtils.fillTreeWithValueItemsAndSelectDefinedLeaf(this.valueItemEditPanel.getFeatureOverviewTree(), this.valueItemContainer.getSortedValueItemsOfContainer(), tmpValueItemStatus,
                    tmpCurrentValueItemName);
        }
    }

    /**
     * Collapses all nodes of job data tree
     */
    private void collapseAllNodesOfTree() {
        GuiUtils.expandAndRetainSelection(this.valueItemEditPanel.getFeatureOverviewTree(), false);
    }

    /**
     * Expands all nodes of job data tree
     */
    private void expandAllNodesOfTree() {
        GuiUtils.expandAndRetainSelection(this.valueItemEditPanel.getFeatureOverviewTree(), true);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Table-data schema related methods">
    /**
     * Removes selected table-data schema
     */
    private void removeSelectedTableDataSchema() {
        if (!GuiUtils.getYesNoDecision(GuiMessage.get("Schemata.RemoveSchemaTitle"), GuiMessage.get("Schemata.RemoveSchemaMessage"))) {
            return;
        }
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItem tmpSelectedSchemaValueItem = (ValueItem) this.valueItemEditPanel.getTableDataSchemaComboBox().getSelectedItem();
            Preferences.getInstance().removeSchemaValueItem(tmpSelectedSchemaValueItem.getName());
            this.updateTableDataSchemaSettings(null);
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Applies selected table-data schema
     */
    private void applySelectedTableDataSchema() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItem tmpSelectedSchemaValueItem = (ValueItem) this.valueItemEditPanel.getTableDataSchemaComboBox().getSelectedItem();
            boolean tmpIsApplied = false;
            boolean tmpIsRowNumberChanged = false;
            if (tmpSelectedSchemaValueItem != null) {
                if (this.currentValueItem.getBasicType() == ValueItemEnumBasicType.FLEXIBLE_MATRIX) {
                    // <editor-fold defaultstate="collapsed" desc="Correct row number">
                    if (this.currentValueItem.getMatrixRowCount() < tmpSelectedSchemaValueItem.getMatrixRowCount()) {
                        // <editor-fold defaultstate="collapsed" desc="Insert rows">
                        int tmpNumberOfRowsToBeInserted = tmpSelectedSchemaValueItem.getMatrixRowCount() - this.currentValueItem.getMatrixRowCount();
                        for (int i = 0; i < tmpNumberOfRowsToBeInserted; i++) {
                            this.valueItemFlexibleMatrixPanelController.insertRow();
                        }
                        tmpIsRowNumberChanged = true;
                        // </editor-fold>
                    } else if (this.currentValueItem.getMatrixRowCount() > tmpSelectedSchemaValueItem.getMatrixRowCount()) {
                        // <editor-fold defaultstate="collapsed" desc="Remove rows">
                        int tmpNumberOfRowsToBeRemoved = this.currentValueItem.getMatrixRowCount() - tmpSelectedSchemaValueItem.getMatrixRowCount();
                        for (int i = 0; i < tmpNumberOfRowsToBeRemoved; i++) {
                            this.valueItemFlexibleMatrixPanelController.removeRow();
                        }
                        tmpIsRowNumberChanged = true;
                        // </editor-fold>
                    }
                    // </editor-fold>
                }
                // Try to apply ALL lines of tmpSelectedSchemaValueItem to
                // ALL lines of this.currentValueItem AFTER last successful
                // application
                int tmpNextRow = 0;
                for (int j = 0; j < tmpSelectedSchemaValueItem.getMatrixRowCount(); j++) {
                    for (int i = tmpNextRow; i < this.currentValueItem.getMatrixRowCount(); i++) {
                        for (int k = 0; k < tmpSelectedSchemaValueItem.getMatrixColumnCount(); k++) {
                            if (!tmpSelectedSchemaValueItem.getValue(j, k).equals(ModelDefinitions.SCHEMA_WILDCARD_STRING)) {
                                if (this.currentValueItem.getValueItemMatrixElement(i, k).getTypeFormat().isEditable()) {
                                    if (this.currentValueItem.getValueItemMatrixElement(i, k).getTypeFormat().isValueAllowed(tmpSelectedSchemaValueItem.getValue(j, k))) {
                                        if (this.currentValueItem.setValue(tmpSelectedSchemaValueItem.getValue(j, k), i, k)) {
                                            this.currentValueItem.notifyDependentValueItemsForUpdate();
                                            tmpIsApplied = true;
                                            tmpNextRow = i + 1;
                                        }
                                    } else {
                                        break;
                                    }
                                } else if (!this.currentValueItem.getValue(i, k).equals(tmpSelectedSchemaValueItem.getValue(j, k))) {
                                    break;
                                }
                            }
                        }
                        if (tmpNextRow > i) {
                            break;
                        }
                    }
                }
            }
            if (tmpIsApplied) {
                // IMPORTANT: For list boxes in table stopCellEditing() + clearSelection() stops possible cell editing and selection to display correct values on repaint
                TableCellEditor tmpTableCellEditor = this.valueItemEditPanel.getSelectedFeatureFlexibleMatrixPanel().getMatrixTable().getCellEditor();
                if (tmpTableCellEditor != null) {
                    tmpTableCellEditor.stopCellEditing();
                }
                this.valueItemEditPanel.getSelectedFeatureFlexibleMatrixPanel().getMatrixTable().clearSelection();
                // IMPORTANT: Repaint after change in this.currentValueItem
                this.valueItemEditPanel.getSelectedFeatureFlexibleMatrixPanel().getMatrixTable().repaint();
            } else if (!tmpIsRowNumberChanged) {
                JOptionPane.showMessageDialog(null, 
                    GuiMessage.get("NoTableDataSchemaApplication"), 
                    GuiMessage.get("NoTableDataSchemaApplication.Title"),
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
            this.updateTableDataSchemaSettings(tmpSelectedSchemaValueItem.getName());
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(
                null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "applySelectedTableDataSchema", "CustomPanelValueItemEditController"), 
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Create new schema with current table-data
     */
    private void createNewSchemaWithCurrentTableData() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            if (this.currentValueItem != null) {
                int tmpIndex = Preferences.getInstance().getNumberOfSchemaValueItems() + 1;
                String tmpNewDisplayName = String.format(GuiMessage.get("TableDataSchemaNameFormat"), String.valueOf(tmpIndex));
                while (Preferences.getInstance().hasSchemaValueItemWithDisplayName(tmpNewDisplayName)) {
                    tmpIndex++;
                    tmpNewDisplayName = String.format(GuiMessage.get("TableDataSchemaNameFormat"), String.valueOf(tmpIndex));
                }
                ValueItem tmpSchemaValueItem = this.currentValueItem.getSchemaValueItem(tmpNewDisplayName);
                if (tmpSchemaValueItem != null) {
                    if (Preferences.getInstance().hasMatchingSchemaValueItem(tmpSchemaValueItem)) {
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        JOptionPane.showMessageDialog(null, GuiMessage.get("MatchingTableDataSchemaAlreadyExists"),
                                GuiMessage.get("MatchingTableDataSchemaAlreadyExists.Title"),
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        Preferences.getInstance().addSchemaValueItem(tmpSchemaValueItem);
                    }
                    this.updateTableDataSchemaSettings(tmpSchemaValueItem.getName());
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Show methods">

    /**
     * Shows matrix diagram
     */
    private void showMatrixDiagram() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.currentValueItem == null || !this.currentValueItem.hasValue()) {
            JOptionPane.showMessageDialog(null, GuiMessage.get("Information.NoDataForDiagram"), GuiMessage.get("Information.NotificationTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // </editor-fold>
        DialogValueItemMatrixDiagram.show("Diagram", this.currentValueItem);
    }

    /**
     * Shows data of selected value item of job data tree
     *
     * @param aSelectedTreePath Selected tree path
     */
    private void showSelectedValueItem(TreePath aSelectedTreePath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectedTreePath == null) {
            this.deactivateCurrentValueItem();
            this.valueItemEditPanel.getSelectedFeatureInfoPanel().setVisible(false);
            this.valueItemEditPanel.getActivityCheckBox().setVisible(false);
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get card layout and tree node">
        CardLayout tmpCardLayout = (CardLayout) this.valueItemEditPanel.getSelectedFeatureCardsPanel().getLayout();
        DefaultMutableTreeNode tmpTreeNode = null;
        try {
            tmpTreeNode = (DefaultMutableTreeNode) aSelectedTreePath.getPathComponent(aSelectedTreePath.getPathCount() - 1);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            this.valueItemEditPanel.getSelectedFeatureInfoPanel().setVisible(false);
            this.valueItemEditPanel.getActivityCheckBox().setVisible(false);
            return;
        }
        // </editor-fold>
        if (tmpTreeNode.getUserObject() instanceof ValueItem) {
            // <editor-fold defaultstate="collapsed" desc="Value item">
            // <editor-fold defaultstate="collapsed" desc="Set current value item related items">
            this.activateCurrentValueItem((ValueItem) tmpTreeNode.getUserObject());
            this.valueItemEditPanel.getSelectedFeatureInfoPanel().setVisible(true);
            if (this.currentValueItem.isLocked()) {
                this.valueItemEditPanel.getActivityCheckBox().setVisible(false);
            } else {
                this.valueItemEditPanel.getActivityCheckBox().setVisible(!this.currentValueItem.isEssential());
                this.valueItemEditPanel.getActivityCheckBox().setSelected(this.currentValueItem.isActive());
            }
            // <editor-fold defaultstate="collapsed" desc="Set description">
            this.valueItemEditPanel.getSelectedFeatureDescriptionPanel().getNameLabel().setText(this.currentValueItem.getDisplayName());
            if (this.currentValueItem.hasDescription()) {
                this.valueItemEditPanel.getSelectedFeatureDescriptionPanel().getTextArea().setText(this.currentValueItem.getDescription());
            } else {
                this.valueItemEditPanel.getSelectedFeatureDescriptionPanel().getTextArea().setText(GuiMessage.get("Information.NoDescription"));
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set hint and error">
            this.updateHint();
            this.updateError();
            // </editor-fold>
            // </editor-fold>
            if (this.currentValueItem.isActive() && !this.currentValueItem.isLocked()) {
                // <editor-fold defaultstate="collapsed" desc="Active unlocked value item">
                // <editor-fold defaultstate="collapsed" desc="Enable feature panel">
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(0, true);
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setSelectedIndex(0);
                // </editor-fold>
                switch (this.currentValueItem.getBasicType()) {
                    // <editor-fold defaultstate="collapsed" desc="VECTOR, MATRIX, FLEXIBLE_MATRIX">
                    case VECTOR:
                    case MATRIX:
                    case FLEXIBLE_MATRIX:
                        this.valueItemEditPanel.getMatrixNameLabel().setText(this.currentValueItem.getDisplayName());
                        this.valueItemEditPanel.getMatrixDiagramButton().setVisible(this.currentValueItem.hasMatrixDiagram());
                        if (this.valueItemFlexibleMatrixPanelController == null) {
                            this.valueItemFlexibleMatrixPanelController = 
                                new CustomPanelValueItemFlexibleMatrixController(
                                    this.valueItemEditPanel.getSelectedFeatureFlexibleMatrixPanel(),
                                    this.currentValueItem
                                );
                        } else {
                            this.valueItemFlexibleMatrixPanelController.setValueItem(this.currentValueItem);
                        }
                        // Set this.valueItemFlexibleMatrixPanelController as a change receiver of this.currentValueItem if necessary
                        if (this.currentValueItem.hasEditableColumn()) {
                            this.currentValueItem.addChangeReceiver(this.valueItemFlexibleMatrixPanelController);
                        }
                        // Configure schema management with appropriate table-data schemata
                        this.updateTableDataSchemaSettings(null);
                        tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixPanel.name"));
                        break;

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="SCALAR">
                    case SCALAR:
                        if (this.currentValueItem.hasEditableColumn()) {
                            // <editor-fold defaultstate="collapsed" desc="Value item is editable">
                            switch (this.currentValueItem.getTypeFormat().getDataType()) {
                                // <editor-fold defaultstate="collapsed" desc="SELECTION_TEXT">
                                case SELECTION_TEXT:
                                    // <editor-fold defaultstate="collapsed" desc="- Set listener">
                                    if (this.valueItemSelectionItemListener == null) {
                                        this.valueItemSelectionItemListener = new ValueItemSelectionItemListener(this.valueItemEditPanel.getSelectionTextComboBox(), this.currentValueItem);
                                        this.valueItemEditPanel.getSelectionTextComboBox().addItemListener(this.valueItemSelectionItemListener);
                                    } else {
                                        this.valueItemSelectionItemListener.setComboBoxAndValueItem(this.valueItemEditPanel.getSelectionTextComboBox(), this.currentValueItem);
                                    }

                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Set combo box">
                                    this.valueItemEditPanel.getSelectionTextComboBox().setModel(new DefaultComboBoxModel(this.currentValueItem.getTypeFormat().getSelectionTexts()));
                                    this.valueItemEditPanel.getSelectionTextComboBox().setRenderer(new ComboBoxColorCellRenderer());
                                    this.valueItemEditPanel.getSelectionTextComboBox().setSelectedItem(this.currentValueItem.getValue());

                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Set name and info label">
                                    this.valueItemEditPanel.getSelectionTextNameLabel().setText(this.currentValueItem.getDisplayName());
                                    this.valueItemEditPanel.getSelectedFeatureSelectionTextInfoLabel().setText(String.format(GuiMessage.get("Information.SelectItem"), this.currentValueItem.getTypeFormat().getDefaultValue()));

                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Show card">
                                    tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureSelectionTextPanel.name"));

                                    // </editor-fold>
                                    break;
                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="TEXT, NUMERIC, DIRECTORY, TIMESTAMPs">
                                case TEXT:
                                case TEXT_EMPTY:
                                case NUMERIC:
                                case NUMERIC_NULL:
                                case DIRECTORY:
                                case FILE:
                                case TIMESTAMP:
                                case TIMESTAMP_EMPTY:
                                    // <editor-fold defaultstate="collapsed" desc="- Set label etc.">
                                    JTextField tmpTextField = null;
                                    switch (this.currentValueItem.getTypeFormat().getDataType()) {
                                        // <editor-fold defaultstate="collapsed" desc="TEXT/TEXT_EMPTY">
                                        case TEXT:
                                        case TEXT_EMPTY:
                                            this.valueItemEditPanel.getTextNameLabel().setText(this.currentValueItem.getDisplayName());
                                            tmpTextField = this.valueItemEditPanel.getTextTextField();
                                            break;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="NUMERIC">
                                        case NUMERIC:
                                            this.valueItemEditPanel.getValueNameLabel().setText(this.currentValueItem.getDisplayName());
                                            tmpTextField = this.valueItemEditPanel.getValueTextField();
                                            if (this.currentValueItem.getTypeFormat().getDefaultValue() == null || this.currentValueItem.getTypeFormat().getDefaultValue().isEmpty()) {
                                                this.valueItemEditPanel.getSelectedFeatureNumericValueInfoLabel().setText(String.format(GuiMessage.get("Information.MinMax"),
                                                                this.currentValueItem.getTypeFormat().getMinimumValueRepresentation(),
                                                                this.currentValueItem.getTypeFormat().getMaximumValueRepresentation()));
                                            } else {
                                                this.valueItemEditPanel.getSelectedFeatureNumericValueInfoLabel().setText(String.format(GuiMessage.get("Information.MinDefaultMax"),
                                                                this.currentValueItem.getTypeFormat().getMinimumValueRepresentation(),
                                                                this.currentValueItem.getTypeFormat().getDefaultValue(),
                                                                this.currentValueItem.getTypeFormat().getMaximumValueRepresentation()));
                                            }
                                            break;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="NUMERIC_NULL">
                                        case NUMERIC_NULL:
                                            this.valueItemEditPanel.getValueNameLabel().setText(this.currentValueItem.getDisplayName());
                                            tmpTextField = this.valueItemEditPanel.getValueTextField();

                                            if (this.currentValueItem.getTypeFormat().getDefaultValue() == null || this.currentValueItem.getTypeFormat().getDefaultValue().isEmpty()) {
                                                this.valueItemEditPanel.getSelectedFeatureNumericValueInfoLabel().setText(String.format(GuiMessage.get("Information.MinMaxOrNull"),
                                                                this.currentValueItem.getTypeFormat().getMinimumValueRepresentation(),
                                                                this.currentValueItem.getTypeFormat().getMaximumValueRepresentation(),
                                                                ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString")));
                                            } else {
                                                this.valueItemEditPanel.getSelectedFeatureNumericValueInfoLabel().setText(String.format(GuiMessage.get("Information.MinDefaultMaxOrNull"),
                                                                this.currentValueItem.getTypeFormat().getMinimumValueRepresentation(),
                                                                this.currentValueItem.getTypeFormat().getDefaultValue(),
                                                                this.currentValueItem.getTypeFormat().getMaximumValueRepresentation(),
                                                                ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString")));
                                            }
                                            break;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="DIRECTORY">
                                        case DIRECTORY:
                                            this.valueItemEditPanel.getDirectoryNameLabel().setText(this.currentValueItem.getDisplayName());
                                            tmpTextField = this.valueItemEditPanel.getDirectoryTextField();
                                            break;

                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="FILE">
                                        case FILE:
                                            this.valueItemEditPanel.getFileNameLabel().setText(this.currentValueItem.getDisplayName());
                                            tmpTextField = this.valueItemEditPanel.getFileTextField();
                                            break;

                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="TIMESTAMP/TIMESTAMP_EMPTY">
                                        case TIMESTAMP:
                                        case TIMESTAMP_EMPTY:
                                            this.valueItemEditPanel.getTimestampNameLabel().setText(this.currentValueItem.getDisplayName());
                                            tmpTextField = this.valueItemEditPanel.getTimestampTextField();
                                            break;

                                        // </editor-fold>
                                    }
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Set this.valueItemKeyListener">
                                    if (this.valueItemKeyListener == null) {
                                        this.valueItemKeyListener = new ValueItemKeyListener(tmpTextField, this.currentValueItem.getTypeFormat());
                                    } else {
                                        this.valueItemKeyListener.setTextFieldAndTypeFormat(tmpTextField, this.currentValueItem.getTypeFormat());
                                    }
                                    if (tmpTextField.getKeyListeners().length == 0) {
                                        tmpTextField.addKeyListener(this.valueItemKeyListener);
                                    } else {
                                        boolean tmpHasKeyListener = false;
                                        for (KeyListener tmpSingleKeyListener : tmpTextField.getKeyListeners()) {
                                            if (tmpSingleKeyListener == this.valueItemKeyListener) {
                                                tmpHasKeyListener = true;
                                                break;
                                            }
                                        }
                                        if (!tmpHasKeyListener) {
                                            tmpTextField.addKeyListener(this.valueItemKeyListener);
                                        }
                                    }

                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Set this.valueItemDocumentListener">
                                    if (this.valueItemDocumentListener == null) {
                                        this.valueItemDocumentListener = new ValueItemDocumentListener(this.currentValueItem, tmpTextField);
                                    } else {
                                        this.valueItemDocumentListener.setValueItemAndTextField(this.currentValueItem, tmpTextField);
                                    }
                                    PlainDocument tmpDocument = (PlainDocument) tmpTextField.getDocument();
                                    if (tmpDocument.getDocumentListeners().length == 0) {
                                        tmpDocument.addDocumentListener(this.valueItemDocumentListener);
                                    } else {
                                        boolean tmpHasDocumentListener = false;
                                        for (DocumentListener tmpSingleDocumentListener : tmpDocument.getDocumentListeners()) {
                                            if (tmpSingleDocumentListener == this.valueItemDocumentListener) {
                                                tmpHasDocumentListener = true;
                                                break;
                                            }
                                        }
                                        if (!tmpHasDocumentListener) {
                                            tmpDocument.addDocumentListener(this.valueItemDocumentListener);
                                        }
                                    }

                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Set this.valueItemInputVerifier">
                                    if (this.valueItemInputVerifier == null) {
                                        this.valueItemInputVerifier = new ValueItemInputVerifier(this.currentValueItem);
                                    } else {
                                        this.valueItemInputVerifier.setValueItem(this.currentValueItem);
                                    }
                                    if (tmpTextField.getInputVerifier() != this.valueItemInputVerifier) {
                                        tmpTextField.setInputVerifier(this.valueItemInputVerifier);
                                    }

                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Set value">
                                    tmpTextField.setText(this.currentValueItem.getValue());
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="- Show card">
                                    switch (this.currentValueItem.getTypeFormat().getDataType()) {
                                        // <editor-fold defaultstate="collapsed" desc="TEXT/TEXT_EMPTY">
                                        case TEXT:
                                        case TEXT_EMPTY:
                                            tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTextValuePanel.name"));
                                            break;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="NUMERIC/NUMERIC_NULL">
                                        case NUMERIC:
                                        case NUMERIC_NULL:
                                            tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureNumericValuePanel.name"));
                                            break;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="DIRECTORY">
                                        case DIRECTORY:
                                            tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureDirectoryValuePanel.name"));
                                            break;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="FILE">
                                        case FILE:
                                            tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureFileValuePanel.name"));
                                            break;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="TIMESTAMP/TIMESTAMP_EMPTY">
                                        case TIMESTAMP:
                                        case TIMESTAMP_EMPTY:
                                            tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTimestampValuePanel.name"));
                                            break;
                                        // </editor-fold>                                        // </editor-fold>
                                    }
                                    // </editor-fold>
                                    break;
                                // </editor-fold>                                // </editor-fold>                                // </editor-fold>                                // </editor-fold>
                            }

                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="Value item is not editable">
                            // <editor-fold defaultstate="collapsed" desc="- Set label and value">
                            this.valueItemEditPanel.getTextShowNameLabel().setText(this.currentValueItem.getDisplayName());
                            this.valueItemEditPanel.getTextShowContentLabel().setText(this.currentValueItem.getValue());

                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="- Show card">
                            tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTextShowPanel.name"));

                            // </editor-fold>
                            // </editor-fold>
                        }
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="COMPARTMENT_CONTAINER">
                    case COMPARTMENT_CONTAINER:
                        // <editor-fold defaultstate="collapsed" desc="Settings">
                        this.valueItemEditPanel.getCompartmentNameLabel().setText(this.currentValueItem.getDisplayName());
                        this.updateCompartmentPanel();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Show card">
                        tmpCardLayout.show(this.valueItemEditPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureCompartmentPanel.name"));

                        // </editor-fold>
                        break;
                    // </editor-fold>                    // </editor-fold>
                }
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Inactive and/or locked value item">
                if (this.currentValueItem.isLocked()) {
                    this.valueItemEditPanel.getActivityCheckBox().setVisible(false);
                } else {
                    this.valueItemEditPanel.getActivityCheckBox().setVisible(true);
                }
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(0, false);
                this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setSelectedIndex(1);

                // </editor-fold>
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="No value item">
            this.deactivateCurrentValueItem();
            this.valueItemEditPanel.getSelectedFeatureInfoPanel().setVisible(false);
            this.valueItemEditPanel.getActivityCheckBox().setVisible(false);
            // </editor-fold>
        }
    }

    /**
     * Show applicable table-data schema (with possible rename)
     */
    private void showTableDataSchema() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();

            ValueItem tmpSelectedSchemaValueItem = (ValueItem) this.valueItemEditPanel.getTableDataSchemaComboBox().getSelectedItem();
            // NOTE: Clone tmpSelectedSchemaValueItem since it is modified
            ValueItem tmpClonedSelectedSchemaValueItem = tmpSelectedSchemaValueItem.getClone();

            ValueItemContainer tmpSelectedSchemaValueItemContainer = new ValueItemContainer(null);
            String[] tmpNodeNames = new String[]{GuiMessage.get("Schemata.RootNode")};
            int tmpVerticalPosition = 0;

            ValueItem tmpSchemaDisplayNameValueItem = new ValueItem();
            tmpSchemaDisplayNameValueItem.setNodeNames(tmpNodeNames);
            tmpSchemaDisplayNameValueItem.setName("SCHEMA_DISPLAY_NAME");
            tmpSchemaDisplayNameValueItem.setDisplayName(GuiMessage.get("Schemata.DisplayName"));
            tmpSchemaDisplayNameValueItem.setDescription(GuiMessage.get("Schemata.DisplayName.Description"));
            tmpSchemaDisplayNameValueItem.setValue(tmpClonedSelectedSchemaValueItem.getDisplayName());
            tmpSchemaDisplayNameValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpSelectedSchemaValueItemContainer.addValueItem(tmpSchemaDisplayNameValueItem);

            tmpClonedSelectedSchemaValueItem.setNodeNames(tmpNodeNames);
            tmpClonedSelectedSchemaValueItem.setName("TABLE_DATA_SCHEMA");
            tmpClonedSelectedSchemaValueItem.setDisplayName(GuiMessage.get("Schemata.SelectedTableDataSchema"));
            tmpClonedSelectedSchemaValueItem.setDescription(GuiMessage.get("Schemata.SelectedTableDataSchema.Description"));
            tmpClonedSelectedSchemaValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpSelectedSchemaValueItemContainer.addValueItem(tmpClonedSelectedSchemaValueItem);

            MouseCursorManagement.getInstance().setDefaultCursor();
            // Parameter false: Hide table-data schema management
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("Schemata.SelectedTableDataSchema.Title"), tmpSelectedSchemaValueItemContainer, false)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                String tmpNewName = tmpSelectedSchemaValueItemContainer.getValueOfValueItem("SCHEMA_DISPLAY_NAME");
                if (!tmpNewName.equals(tmpSelectedSchemaValueItem.getDisplayName())) {
                    ValueItem tmpNewSchemaValueItem = tmpSelectedSchemaValueItem.getClone();
                    // NOTE: Use getCreationStandardTimeStampAppendString() to make schema unique
                    tmpNewSchemaValueItem.setName((new TimeUtilityMethods()).getCreationStandardTimeStampAppendString(tmpNewName));
                    tmpNewSchemaValueItem.setDisplayName(tmpNewName);
                    Preferences.getInstance().replaceSchemaValueItem(tmpSelectedSchemaValueItem.getName(), tmpNewSchemaValueItem);
                    this.updateTableDataSchemaSettings(null);
                    this.valueItemEditPanel.getTableDataSchemaComboBox().setSelectedItem(tmpNewSchemaValueItem);
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Shows box properties
     */
    private void showBoxProperties() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpJobInputValueItemContainer = this.currentValueItem.getValueItemContainer();
            ValueItemContainer tmpBoxPropertiesValueItemContainer = this.jobUtilityMethods.getBoxPropertiesSummaryValueItemContainer(tmpJobInputValueItemContainer);
            MouseCursorManagement.getInstance().setDefaultCursor();
            DialogValueItemShow.show(GuiMessage.get("ShowBoxProperties.Title"), tmpBoxPropertiesValueItemContainer, false, false);
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    /**
     * Rebuilds tree after a change of value items in value item container
     *
     * @param aBlockName Block name of value item to select
     */
    private void rebuildTreeAfterChange(String aBlockName) {
        if (aBlockName == null || aBlockName.isEmpty()) {
            GuiUtils.fillTreeWithValueItemsAndSelectFirstLeaf(this.valueItemEditPanel.getFeatureOverviewTree(), this.valueItemContainer.getSortedValueItemsOfContainer(), ValueItemEnumStatus.ALL);
        } else {
            GuiUtils.fillTreeWithValueItemsAndSelectFirstOfBlock(this.valueItemEditPanel.getFeatureOverviewTree(), this.valueItemContainer.getSortedValueItemsOfContainer(), ValueItemEnumStatus.ALL,
                    aBlockName);
        }
    }

    /**
     * Repaints tree
     */
    private void repaintTree() {
        this.valueItemEditPanel.getFeatureOverviewTree().repaint();
    }

    /**
     * Updates error if possible
     */
    private void updateError() {
        if (this.currentValueItem != null) {
            // NOTE: Check if error tab is not removed
            if (this.valueItemEditPanel.getSelectedFeatureTabbedPanel().getComponentCount() > 3) {
                if (this.currentValueItem.hasError()) {
                    if (!this.valueItemEditPanel.getSelectedFeatureTabbedPanel().isEnabledAt(3)) {
                        this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(3, true);
                    }
                    this.valueItemEditPanel.getSelectedFeatureErrorPanel().getNameLabel().setText(this.currentValueItem.getDisplayName());
                    this.valueItemEditPanel.getSelectedFeatureErrorPanel().getTextArea().setText(this.currentValueItem.getError());
                } else {
                    if (this.valueItemEditPanel.getSelectedFeatureTabbedPanel().isEnabledAt(3)) {
                        this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(3, false);
                    }
                }
            }
        }
    }

    /**
     * Updates error information
     */
    private void updateErrorInformation() {
        if (this.valueItemContainer.hasError()) {
            if (!this.valueItemEditPanel.getSelectErrorStatusButton().isVisible()) {
                this.valueItemEditPanel.getSelectErrorStatusButton().setVisible(true);
            }
        } else {
            if (this.valueItemEditPanel.getSelectErrorStatusButton().isVisible()) {
                this.valueItemEditPanel.getSelectErrorStatusButton().setVisible(false);
            }
        }
    }

    /**
     * Updates hint information
     */
    private void updateHintInformation() {
        if (this.valueItemContainer.hasHint()) {
            if (!this.valueItemEditPanel.getSelectHintStatusButton().isVisible()) {
                this.valueItemEditPanel.getSelectHintStatusButton().setVisible(true);
            }
        } else {
            if (this.valueItemEditPanel.getSelectHintStatusButton().isVisible()) {
                this.valueItemEditPanel.getSelectHintStatusButton().setVisible(false);
            }
        }
    }

    /**
     * Updates hint if possible
     */
    private void updateHint() {
        if (this.currentValueItem != null) {
            // NOTE: Check if hint tab is not removed
            if (this.valueItemEditPanel.getSelectedFeatureTabbedPanel().getComponentCount() > 2) {
                if (this.currentValueItem.hasHint()) {
                    if (!this.valueItemEditPanel.getSelectedFeatureTabbedPanel().isEnabledAt(2)) {
                        this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(2, true);
                    }
                    this.valueItemEditPanel.getSelectedFeatureHintPanel().getNameLabel().setText(this.currentValueItem.getDisplayName());
                    this.valueItemEditPanel.getSelectedFeatureHintPanel().getTextArea().setText(this.currentValueItem.getHint());
                } else {
                    if (this.valueItemEditPanel.getSelectedFeatureTabbedPanel().isEnabledAt(2)) {
                        this.valueItemEditPanel.getSelectedFeatureTabbedPanel().setEnabledAt(2, false);
                    }
                }
            }
        }
    }

    /**
     * Sets value item status options
     */
    private void updateValueItemStatusSelectOptions() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.valueItemEditPanel.getSelectValueItemStatusPanel().isVisible()) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check value item status options">
        LinkedList<String> tmpStatusOptionList = new LinkedList<String>();
        // NOTE: ALL MUST be FIRST entry in tmpOptionList
        tmpStatusOptionList.addLast(ValueItemEnumStatus.ALL.toRepresentation());
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.ACTIVE)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.ACTIVE.toRepresentation());
        }
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.INACTIVE)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.INACTIVE.toRepresentation());
        }
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.UNLOCKED)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.UNLOCKED.toRepresentation());
        }
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.LOCKED)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.LOCKED.toRepresentation());
        }
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.HAS_ERROR)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.HAS_ERROR.toRepresentation());
        }
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.HAS_NO_ERROR)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.HAS_NO_ERROR.toRepresentation());
        }
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.HAS_HINT)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.HAS_HINT.toRepresentation());
        }
        if (this.valueItemContainer.hasStatus(ValueItemEnumStatus.HAS_NO_HINT)) {
            tmpStatusOptionList.addLast(ValueItemEnumStatus.HAS_NO_HINT.toRepresentation());
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set current value item status options">
        if (this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel() != null && this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getSize() > 0) {

            // <editor-fold defaultstate="collapsed" desc="Check if update is necessary">
            if (tmpStatusOptionList.size() == this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getSize()) {
                boolean tmpIsDifferent = false;
                int tmpIndex = 0;
                for (String tmpSingleStatusOption : tmpStatusOptionList) {
                    if (!tmpSingleStatusOption.equals(this.valueItemEditPanel.getSelectValueItemStatusComboBox().getModel().getElementAt(tmpIndex++).toString())) {
                        tmpIsDifferent = true;
                        break;
                    }
                }
                if (!tmpIsDifferent) {
                    // No update necessary
                    return;
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update combo box model">
            String tmpSelectedItem = this.valueItemEditPanel.getSelectValueItemStatusComboBox().getSelectedItem().toString();
            // Remove listeners to establish old selection without events if possible
            GuiUtils.removeItemListeners(this.valueItemEditPanel.getSelectValueItemStatusComboBox());
            // Try to reselect previously selected item
            boolean tmpIsMatch = false;
            for (String tmpSingleStatusOption : tmpStatusOptionList) {
                if (tmpSelectedItem.equals(tmpSingleStatusOption)) {
                    tmpIsMatch = true;
                    break;
                }
            }
            if (tmpIsMatch) {
                this.valueItemEditPanel.getSelectValueItemStatusComboBox().setModel(new DefaultComboBoxModel(tmpStatusOptionList.toArray(new String[0])));
                this.valueItemEditPanel.getSelectValueItemStatusComboBox().setSelectedItem(tmpSelectedItem);
            } else {
                tmpStatusOptionList.addFirst(ValueItemEnumStatus.UNDEFINED.toRepresentation());
                this.valueItemEditPanel.getSelectValueItemStatusComboBox().setModel(new DefaultComboBoxModel(tmpStatusOptionList.toArray(new String[0])));
                this.valueItemEditPanel.getSelectValueItemStatusComboBox().setSelectedIndex(0);
            }
            // Add listener again
            this.valueItemEditPanel.getSelectValueItemStatusComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    CustomPanelValueItemEditController.this.checkSelectedValueItemStatus();
                }

            });

            // </editor-fold>
        } else {

            // <editor-fold defaultstate="collapsed" desc="Initialize combo box model">
            this.valueItemEditPanel.getSelectValueItemStatusComboBox().setModel(new DefaultComboBoxModel(tmpStatusOptionList.toArray(new String[0])));
            this.valueItemEditPanel.getSelectValueItemStatusComboBox().setSelectedIndex(0);
            this.checkSelectedValueItemStatus();

            // </editor-fold>
        }

        // </editor-fold>
    }

    /**
     * Updates compartment panel
     */
    private void updateCompartmentPanel() {
        try {
            if (this.currentValueItem != null) {
                // Set wait cursor since graphics panel operation may cause a delay
                MouseCursorManagement.getInstance().setWaitCursor();
                // NOTE: A standard value item always has a compartment container by default but not necessarily defined compartments
                if (!this.valueItemEditPanel.getCompartmentImagePanel().hasBasicImage()) {
                    this.valueItemEditPanel.getCompartmentEditButton().setVisible(true);
                    this.valueItemEditPanel.getCompartmentViewButton().setVisible(true);
                    this.valueItemEditPanel.getCompartmentRemoveButton().setVisible(this.currentValueItem.hasCompartments());
                    GuiUtils.setCompartmentsImage(this.valueItemEditPanel.getCompartmentImagePanel());
                } else {
                    this.valueItemEditPanel.getCompartmentRemoveButton().setVisible(this.currentValueItem.hasCompartments());
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Updates table-data schema settings
     *
     * @param aSchemaNameToBeSelected Schema name to be selected (may be
     * null/empty then last item is selected)
     */
    private void updateTableDataSchemaSettings(String aSchemaNameToBeSelected) {
        // Do not set Mouse curser since this is done in calling methods
        ValueItem[] tmpNameSortedSchemaValueItems = Preferences.getInstance().getNameSortedSchemaValueItems();
        LinkedList<ValueItem> tmpSchemaValueItemList = new LinkedList<ValueItem>();
        ValueItem tmpMatchingSchemaValueItem = null;
        if (tmpNameSortedSchemaValueItems != null) {
            String tmpMatchingSchemaDisplayName = null;
            for (ValueItem tmpSingleSchemaValueItem : tmpNameSortedSchemaValueItems) {
                if (this.currentValueItem != null && this.currentValueItem.canApplySchemaValueItem(tmpSingleSchemaValueItem)) {
                    if (this.currentValueItem.matchesSchemaValueItem(tmpSingleSchemaValueItem)) {
                        // Get matching schema display name
                        tmpMatchingSchemaDisplayName = tmpSingleSchemaValueItem.getDisplayName();
                        tmpMatchingSchemaValueItem = tmpSingleSchemaValueItem;
                    }
                    tmpSchemaValueItemList.add(tmpSingleSchemaValueItem);
                }
            }
            TitledBorder tmpTitledBorder = (TitledBorder) this.valueItemEditPanel.getTableDataSchemaPanel().getBorder();
            if (tmpMatchingSchemaDisplayName != null && !tmpMatchingSchemaDisplayName.isEmpty()) {
                tmpTitledBorder.setTitle(String.format(GuiMessage.get("CustomPanelValueItemEdit.tableDataSchemaPanel.titleFormat"), tmpMatchingSchemaDisplayName));
            } else {
                tmpTitledBorder.setTitle(GuiMessage.get("CustomPanelValueItemEdit.tableDataSchemaPanel.title"));
            }
            this.valueItemEditPanel.getTableDataSchemaPanel().repaint();
        } else {
            TitledBorder tmpTitledBorder = (TitledBorder) this.valueItemEditPanel.getTableDataSchemaPanel().getBorder();
            tmpTitledBorder.setTitle(GuiMessage.get("CustomPanelValueItemEdit.tableDataSchemaPanel.title"));
            this.valueItemEditPanel.getTableDataSchemaPanel().repaint();
        }
        if (tmpSchemaValueItemList.isEmpty()) {
            this.valueItemEditPanel.getTableDataSchemaComboBox().setVisible(false);
            this.valueItemEditPanel.getApplyTableDataSchemaButton().setVisible(false);
            this.valueItemEditPanel.getRemoveTableDataSchemaButton().setVisible(false);
            this.valueItemEditPanel.getShowTableDataSchemaButton().setVisible(false);
        } else {
            this.valueItemEditPanel.getTableDataSchemaComboBox().setModel(new DefaultComboBoxModel(tmpSchemaValueItemList.toArray(new ValueItem[0])));
            if (aSchemaNameToBeSelected == null || aSchemaNameToBeSelected.isEmpty()) {
                if (tmpMatchingSchemaValueItem != null) {
                    for (int i = 0; i < this.valueItemEditPanel.getTableDataSchemaComboBox().getModel().getSize(); i++) {
                        if (tmpMatchingSchemaValueItem.getName().equals(((ValueItem) this.valueItemEditPanel.getTableDataSchemaComboBox().getModel().getElementAt(i)).getName())) {
                            this.valueItemEditPanel.getTableDataSchemaComboBox().setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    this.valueItemEditPanel.getTableDataSchemaComboBox().setSelectedIndex(tmpSchemaValueItemList.size() - 1);
                }
            } else {
                for (int i = 0; i < this.valueItemEditPanel.getTableDataSchemaComboBox().getModel().getSize(); i++) {
                    if (aSchemaNameToBeSelected.equals(((ValueItem) this.valueItemEditPanel.getTableDataSchemaComboBox().getModel().getElementAt(i)).getName())) {
                        this.valueItemEditPanel.getTableDataSchemaComboBox().setSelectedIndex(i);
                        break;
                    }
                }
            }
            this.valueItemEditPanel.getTableDataSchemaComboBox().setVisible(true);
            this.valueItemEditPanel.getApplyTableDataSchemaButton().setVisible(true);
            this.valueItemEditPanel.getRemoveTableDataSchemaButton().setVisible(true);
            this.valueItemEditPanel.getShowTableDataSchemaButton().setVisible(true);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Current value item related methods">
    /**
     * Activates current value item with specified value item and all necessary
     * change receivers
     *
     * @param aValueItem Value item
     */
    private void activateCurrentValueItem(ValueItem aValueItem) {
        this.deactivateCurrentValueItem();
        this.currentValueItem = aValueItem;
        this.currentValueItem.addChangeReceiver(this);
    }

    /**
     * Removes all change receivers from current value item and sets current
     * value item to null
     */
    private void deactivateCurrentValueItem() {
        if (this.currentValueItem != null) {
            this.currentValueItem.removeSingleChangeReceiver(this);
            this.currentValueItem.removeSingleChangeReceiver(this.valueItemFlexibleMatrixPanelController);
            this.currentValueItem = null;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
