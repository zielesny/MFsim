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
package de.gnwi.mfsim.gui.dialog;

import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.control.CustomPanelValueItemMatrixDiagram;
import de.gnwi.mfsim.gui.control.CustomPanelValueItemMatrixDiagramController;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Dialog that shows a chart of a value item matrix. The chart can be altered by
 * the user and the creation of movies is possible.
 *
 * @author Achim Zielesny, Jan-Mathis Hein
 */
public class DialogValueItemMatrixDiagram extends CustomDialogApplyCancelSize {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JPanel mainPanel;
    /**
     * GUI element
     */
    private SpringLayout mainPanelSpringLayout;
    /**
     * GUI element
     */
    private CustomPanelValueItemMatrixDiagram customPanelValueItemMatrixDiagram;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000016L;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogValueItemMatrixDiagram() {
        // Apply button is not visible, size related buttons are visible
        super(false, true);
        this.setName(GuiMessage.get("DialogValueItemMatrixDiagram.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogValueItemMatrixDiagramWidth(), 
            Preferences.getInstance().getDialogValueItemMatrixDiagramHeight()
        );
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            this.getContentPane().add(this.mainPanel, BorderLayout.CENTER);
            {
                this.customPanelValueItemMatrixDiagram = new CustomPanelValueItemMatrixDiagram();
                this.mainPanel.add(this.customPanelValueItemMatrixDiagram);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customPanelValueItemMatrixDiagram, -12, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customPanelValueItemMatrixDiagram, 12, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customPanelValueItemMatrixDiagram, -12, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customPanelValueItemMatrixDiagram, 12, SpringLayout.NORTH, this.mainPanel);
            }
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomPanelValueItemMatrixDiagram
     * 
     * @return CustomPanelValueItemMatrixDiagram
     */
    public CustomPanelValueItemMatrixDiagram getCustomPanelValueItemMatrixDiagram(){
        return this.customPanelValueItemMatrixDiagram;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static show() method">
    /**
     * Shows the dialog. Returns if there is an error or a parameter is null.
     *
     * @param aTitle Title for the dialog
     * @param aMatrixDiagramValueItem Value item with matrix for a diagram
     */
    public static void show(String aTitle, ValueItem aMatrixDiagramValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTitle == null || aTitle.isEmpty() || aMatrixDiagramValueItem == null || !aMatrixDiagramValueItem.hasMatrixDiagram()) {
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null,
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogValueItemMatrixDiagram"),
                GuiMessage.get("Error.ErrorNotificationTitle"),
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
            return;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup">
            // <editor-fold defaultstate="collapsed" desc="- Set final variables">
            final DialogValueItemMatrixDiagram tmpValueItemMatrixDiagramDialog = new DialogValueItemMatrixDiagram();
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpValueItemMatrixDiagramDialog);
            MouseCursorManagement.getInstance().setWaitCursor();
            final CustomPanelValueItemMatrixDiagramController tmpCustomPanelValueItemMatrixDiagramController = 
                new CustomPanelValueItemMatrixDiagramController(
                    tmpValueItemMatrixDiagramDialog.getCustomPanelValueItemMatrixDiagram(), 
                    aMatrixDiagramValueItem, 
                    ImageFileType.JPG
                );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set icon image">
            tmpValueItemMatrixDiagramDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set title">
            tmpValueItemMatrixDiagramDialog.setTitle(aTitle);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpValueItemMatrixDiagramDialog.addWindowListener(new WindowAdapter() {

                public void windowClosing(final WindowEvent e) {
                    try {
                        // Set dialog size in Preferences
                        Preferences.getInstance().setDialogValueItemMatrixDiagramHeightWidth(
                            tmpValueItemMatrixDiagramDialog.getCustomPanelValueItemMatrixDiagram().getHeight(), 
                            tmpValueItemMatrixDiagramDialog.getCustomPanelValueItemMatrixDiagram().getWidth()
                        );
                        tmpValueItemMatrixDiagramDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogValueItemMatrixDiagram"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpValueItemMatrixDiagramDialog.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in Preferences
                        Preferences.getInstance().setDialogValueItemMatrixDiagramHeightWidth(
                            tmpValueItemMatrixDiagramDialog.getCustomPanelValueItemMatrixDiagram().getHeight(), 
                            tmpValueItemMatrixDiagramDialog.getCustomPanelValueItemMatrixDiagram().getWidth()
                        );
                        tmpValueItemMatrixDiagramDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemMatrixDiagram"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpValueItemMatrixDiagramDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpValueItemMatrixDiagramDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemMatrixDiagramDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemMatrixDiagram"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpValueItemMatrixDiagramDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpValueItemMatrixDiagramDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemMatrixDiagramDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemMatrixDiagram"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpValueItemMatrixDiagramDialog.getCenterDialogButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpValueItemMatrixDiagramDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemMatrixDiagram"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpValueItemMatrixDiagramDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpValueItemMatrixDiagramDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemMatrixDiagramDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemMatrixDiagram"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            tmpValueItemMatrixDiagramDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpValueItemMatrixDiagramDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemMatrixDiagram"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Show">
            // Check dialog size and center dialog
            GuiUtils.checkDialogSize(tmpValueItemMatrixDiagramDialog);
            GuiUtils.centerDialogOnScreen(tmpValueItemMatrixDiagramDialog);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // Show dialog - Wait
            tmpValueItemMatrixDiagramDialog.setVisible(true);
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogValueItemMatrixDiagram"), 
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    // </editor-fold>

}
