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

import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.dialog.DialogValueItemShow;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.ExtensionFileFilter;
import de.gnwi.mfsim.model.util.FastListModel;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JOptionPane;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Controller class for CustomPanelTableDataSchemataManage
 *
 * @author Achim
 */
public class CustomPanelTableDataSchemataManageController {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Panel this controller is made
     */
    private CustomPanelTableDataSchemataManage customTableDataSchemataManagePanel;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomTableDataSchemataManagePanel Panel this controller is made
     * for
     * @throws IllegalArgumentException Thrown if argument is null
     */
    public CustomPanelTableDataSchemataManageController(CustomPanelTableDataSchemataManage aCustomTableDataSchemataManagePanel) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomTableDataSchemataManagePanel == null) {
            throw new IllegalArgumentException("An argument is null or empty.");

        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.customTableDataSchemataManagePanel = aCustomTableDataSchemataManagePanel;
            this.updateTableDataSchemataList(null);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listener">
            this.customTableDataSchemataManagePanel.getTableDataSchemataList().addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        // Double click: View schema
                        CustomPanelTableDataSchemataManageController.this.viewFirstSelectedSchema();
                    }
                }

            });
            this.customTableDataSchemataManagePanel.getViewSchemaButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelTableDataSchemataManageController.this.viewFirstSelectedSchema();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelTableDataSchemataManageController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customTableDataSchemataManagePanel.getEditSchemaButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelTableDataSchemataManageController.this.editFirstSelectedSchema();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelTableDataSchemataManageController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customTableDataSchemataManagePanel.getLoadSchemataButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelTableDataSchemataManageController.this.loadSchemata();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null,
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelTableDataSchemataManageController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customTableDataSchemataManagePanel.getSaveSchemataButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelTableDataSchemataManageController.this.saveSchemata();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null,
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelTableDataSchemataManageController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customTableDataSchemataManagePanel.getMergeSchemataButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelTableDataSchemataManageController.this.mergeSchemata();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null,
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelTableDataSchemataManageController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customTableDataSchemataManagePanel.getClearSchemataButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelTableDataSchemataManageController.this.clearSchemata();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null,
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelTableDataSchemataManageController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customTableDataSchemataManagePanel.getRemoveSchemataButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelTableDataSchemataManageController.this.removeSelectedSchemata();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null,
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelTableDataSchemataManageController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelTableDataSchemataManageController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Action methods">
    /**
     * View first selected schema
     */
    private void viewFirstSelectedSchema() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            String tmpSelectedSchemaName = (String) this.customTableDataSchemataManagePanel.getTableDataSchemataList().getSelectedValue();
            if (tmpSelectedSchemaName == null) {
                return;
            }
            // NOTE: Preferences.getInstance().getNameToSchemaValueItemMap() cannot be null
            // NOTE: Clone tmpSelectedSchemaValueItem since it is modified
            ValueItem tmpSelectedSchemaValueItem = Preferences.getInstance().getSchemaValueItem(tmpSelectedSchemaName).getClone();

            ValueItemContainer tmpSelectedSchemaValueItemContainer = new ValueItemContainer(null);
            String[] tmpNodeNames = new String[]{GuiMessage.get("Schemata.RootNode")};
            int tmpVerticalPosition = 0;

            ValueItem tmpSchemaDisplayNameValueItem = new ValueItem();
            tmpSchemaDisplayNameValueItem.setNodeNames(tmpNodeNames);
            tmpSchemaDisplayNameValueItem.setName("SCHEMA_DISPLAY_NAME");
            tmpSchemaDisplayNameValueItem.setDisplayName(GuiMessage.get("Schemata.DisplayName"));
            tmpSchemaDisplayNameValueItem.setDescription(GuiMessage.get("Schemata.DisplayName.Description"));
            tmpSchemaDisplayNameValueItem.setValue(tmpSelectedSchemaValueItem.getDisplayName());
            tmpSchemaDisplayNameValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpSelectedSchemaValueItemContainer.addValueItem(tmpSchemaDisplayNameValueItem);

            tmpSelectedSchemaValueItem.setNodeNames(tmpNodeNames);
            tmpSelectedSchemaValueItem.setName("TABLE_DATA_SCHEMA");
            tmpSelectedSchemaValueItem.setDisplayName(GuiMessage.get("Schemata.SelectedTableDataSchema"));
            tmpSelectedSchemaValueItem.setDescription(GuiMessage.get("Schemata.SelectedTableDataSchema.Description"));
            tmpSelectedSchemaValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpSelectedSchemaValueItemContainer.addValueItem(tmpSelectedSchemaValueItem);

            MouseCursorManagement.getInstance().setDefaultCursor();
            DialogValueItemShow.show(GuiMessage.get("Schemata.SelectedTableDataSchema.Title"), tmpSelectedSchemaValueItemContainer, true, true);
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Edit first selected schema
     */
    private void editFirstSelectedSchema() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            String tmpSelectedSchemaName = (String) this.customTableDataSchemataManagePanel.getTableDataSchemataList().getSelectedValue();
            if (tmpSelectedSchemaName == null) {
                return;
            }
            // NOTE: Preferences.getInstance().getNameToSchemaValueItemMap() cannot be null
            // NOTE: Clone tmpSelectedSchemaValueItem since it is modified
            ValueItem tmpSelectedSchemaValueItem = Preferences.getInstance().getSchemaValueItem(tmpSelectedSchemaName).getClone();

            ValueItemContainer tmpSelectedSchemaValueItemContainer = new ValueItemContainer(null);
            String[] tmpNodeNames = new String[]{GuiMessage.get("Schemata.RootNode")};
            int tmpVerticalPosition = 0;

            ValueItem tmpSchemaDisplayNameValueItem = new ValueItem();
            tmpSchemaDisplayNameValueItem.setNodeNames(tmpNodeNames);
            tmpSchemaDisplayNameValueItem.setName("SCHEMA_DISPLAY_NAME");
            tmpSchemaDisplayNameValueItem.setDisplayName(GuiMessage.get("Schemata.DisplayName"));
            tmpSchemaDisplayNameValueItem.setDescription(GuiMessage.get("Schemata.DisplayName.Description"));
            tmpSchemaDisplayNameValueItem.setValue(tmpSelectedSchemaValueItem.getDisplayName());
            tmpSchemaDisplayNameValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpSelectedSchemaValueItemContainer.addValueItem(tmpSchemaDisplayNameValueItem);

            tmpSelectedSchemaValueItem.setNodeNames(tmpNodeNames);
            tmpSelectedSchemaValueItem.setName("TABLE_DATA_SCHEMA");
            tmpSelectedSchemaValueItem.setDisplayName(GuiMessage.get("Schemata.SelectedTableDataSchema"));
            tmpSelectedSchemaValueItem.setDescription(GuiMessage.get("Schemata.SelectedTableDataSchema.Description"));
            tmpSelectedSchemaValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpSelectedSchemaValueItemContainer.addValueItem(tmpSelectedSchemaValueItem);

            MouseCursorManagement.getInstance().setDefaultCursor();
            // Parameter false: Hide table-data schema management
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("Schemata.SelectedTableDataSchema.Title"), tmpSelectedSchemaValueItemContainer, false)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                String tmpNewName = tmpSelectedSchemaValueItemContainer.getValueOfValueItem("SCHEMA_DISPLAY_NAME");
                if (!tmpNewName.equals(tmpSelectedSchemaName)) {
                    ValueItem tmpNewSchemaValueItem = Preferences.getInstance().getSchemaValueItem(tmpSelectedSchemaName).getClone();
                    // NOTE: Use getCreationStandardTimeStampAppendString() to make schema unique
                    tmpNewSchemaValueItem.setName((new TimeUtilityMethods()).getCreationStandardTimeStampAppendString(tmpNewName));
                    tmpNewSchemaValueItem.setDisplayName(tmpNewName);
                    Preferences.getInstance().replaceSchemaValueItem(tmpSelectedSchemaName, tmpNewSchemaValueItem);
                    this.updateTableDataSchemataList(tmpNewName);
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Loads table-data schemata from file
     */
    public void loadSchemata() {
        try {
            if (Preferences.getInstance().hasSchemaValueItems()) {
                if (!GuiUtils.getYesNoDecision(GuiMessage.get("Schemata.LoadSchemataTitle"), GuiMessage.get("Schemata.LoadSchemataMessage"))) {
                    return;
                }
            }
            String tmpSchemataFilePathname = GuiUtils.selectSingleFile(Preferences.getInstance().getLastSelectedPath(),
                    GuiMessage.get("Chooser.loadTableDataSchemataFileChooser"), new ExtensionFileFilter(new String[]{GuiDefinitions.XML_FILE_EXTENSION}));
            if (tmpSchemataFilePathname != null && !tmpSchemataFilePathname.isEmpty() && (new File(tmpSchemataFilePathname)).isFile()) {
                MouseCursorManagement.getInstance().setWaitCursor();
                if (!Preferences.getInstance().readSchemaValueItemContainerFromFile(tmpSchemataFilePathname)) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SchemataLoadFailed"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                }
                this.updateTableDataSchemataList(null);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "loadSchemata()", "CustomPanelTableDataSchemataManageController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Merges table-data schemata from file
     */
    public void mergeSchemata() {
        try {
            String tmpSelectedSchemaName = (String) this.customTableDataSchemataManagePanel.getTableDataSchemataList().getSelectedValue();
            String tmpSchemataFilePathname = GuiUtils.selectSingleFile(Preferences.getInstance().getLastSelectedPath(),
                    GuiMessage.get("Chooser.mergeTableDataSchemataFileChooser"), new ExtensionFileFilter(new String[]{GuiDefinitions.XML_FILE_EXTENSION}));
            if (tmpSchemataFilePathname != null && !tmpSchemataFilePathname.isEmpty() && (new File(tmpSchemataFilePathname)).isFile()) {
                MouseCursorManagement.getInstance().setWaitCursor();
                if (!Preferences.getInstance().mergeSchemaValueItemContainerFromFile(tmpSchemataFilePathname)) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SchemataMergeFailed"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                }
                this.updateTableDataSchemataList(tmpSelectedSchemaName);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mergeSchemata()", "CustomPanelTableDataSchemataManageController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Saves table-data schemata to file
     */
    public void saveSchemata() {
        try {
            String tmpSchemataFilePathname = GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.saveTableDataSchemataFileChooser"),
                    GuiDefinitions.XML_FILE_EXTENSION);
            if (tmpSchemataFilePathname != null && !tmpSchemataFilePathname.isEmpty()) {
                try {
                    MouseCursorManagement.getInstance().setWaitCursor();
                    if (!Preferences.getInstance().writeSchemaValueItemContainerToFile(tmpSchemataFilePathname)) {
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SchemataSaveFailed"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception anExeption) {
                    // <editor-fold defaultstate="collapsed" desc="Show error message">
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SaveOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE);

                    // </editor-fold>
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "loadSchemata()", "CustomPanelTableDataSchemataManageController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Clears table-data schemata
     */
    public void clearSchemata() {
        try {
            if (GuiUtils.getYesNoDecision(GuiMessage.get("Schemata.ClearSchemataTitle"), GuiMessage.get("Schemata.ClearSchemataMessage"))) {
                MouseCursorManagement.getInstance().setWaitCursor();
                Preferences.getInstance().removeAllSchemaValueItems();
                this.updateTableDataSchemataList(null);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "cleanSchemata()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Removes selected table-data schemata
     */
    public void removeSelectedSchemata() {
        try {
            int[] tmpSelectedIndices = this.customTableDataSchemataManagePanel.getTableDataSchemataList().getSelectedIndices();
            if (tmpSelectedIndices.length > 0 && tmpSelectedIndices[0] >= 0) {
                if (GuiUtils.getYesNoDecision(GuiMessage.get("Schemata.RemoveSelectedSchemataTitle"), GuiMessage.get("Schemata.RemoveSelectedSchemataMessage"))) {
                    MouseCursorManagement.getInstance().setWaitCursor();
                    String[] tmpSchemaNameArrayForRemoval = new String[tmpSelectedIndices.length];
                    for (int i = 0; i < tmpSelectedIndices.length; i++) {
                        tmpSchemaNameArrayForRemoval[i] = (String) this.customTableDataSchemataManagePanel.getTableDataSchemataList().getModel().getElementAt(tmpSelectedIndices[i]);
                    }
                    Preferences.getInstance().removeSchemaValueItems(tmpSchemaNameArrayForRemoval);
                    if (tmpSelectedIndices[0] == 0) {
                        this.updateTableDataSchemataList(null);
                    } else {
                        this.updateTableDataSchemataList((String) this.customTableDataSchemataManagePanel.getTableDataSchemataList().getModel().getElementAt(tmpSelectedIndices[0] - 1));
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "removeSelectedSchemata()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    /**
     * Updates table-data schemata list
     *
     * @param aSchemaNameToBeSelected Schema name to be selected (may be
     * null/empty then first index is selected)
     */
    private void updateTableDataSchemataList(String aSchemaNameToBeSelected) {
        FastListModel tmpTableDataSchemataModel = new FastListModel();
        this.customTableDataSchemataManagePanel.getTableDataSchemataList().setModel(tmpTableDataSchemataModel);
        String[] tmpSortedSchemaNameArray = Preferences.getInstance().getSortedSchemaValueItemNames();
        if (tmpSortedSchemaNameArray != null) {
            tmpTableDataSchemataModel.setListenersEnabled(false);
            tmpTableDataSchemataModel.setListenersEnabled(true);
            for (String tmpSchemaName : tmpSortedSchemaNameArray) {
                tmpTableDataSchemataModel.addElement(tmpSchemaName);
            }
            tmpTableDataSchemataModel.fireIntervalAdded(tmpTableDataSchemataModel, 0, tmpSortedSchemaNameArray.length);
            if (aSchemaNameToBeSelected == null || aSchemaNameToBeSelected.isEmpty()) {
                this.customTableDataSchemataManagePanel.getTableDataSchemataList().setSelectedIndex(0);
            } else {
                this.customTableDataSchemataManagePanel.getTableDataSchemataList().setSelectedValue(aSchemaNameToBeSelected, true);
            }
            this.customTableDataSchemataManagePanel.getViewSchemaButton().setVisible(true);
            this.customTableDataSchemataManagePanel.getEditSchemaButton().setVisible(true);
            this.customTableDataSchemataManagePanel.getSaveSchemataButton().setVisible(true);
            this.customTableDataSchemataManagePanel.getMergeSchemataButton().setVisible(true);
            this.customTableDataSchemataManagePanel.getClearSchemataButton().setVisible(true);
            this.customTableDataSchemataManagePanel.getRemoveSchemataButton().setVisible(true);
        } else {
            this.customTableDataSchemataManagePanel.getViewSchemaButton().setVisible(false);
            this.customTableDataSchemataManagePanel.getEditSchemaButton().setVisible(false);
            this.customTableDataSchemataManagePanel.getSaveSchemataButton().setVisible(false);
            this.customTableDataSchemataManagePanel.getMergeSchemataButton().setVisible(false);
            this.customTableDataSchemataManagePanel.getClearSchemataButton().setVisible(false);
            this.customTableDataSchemataManagePanel.getRemoveSchemataButton().setVisible(false);
        }
    }
    // </editor-fold>
    // </editor-fold>

}
