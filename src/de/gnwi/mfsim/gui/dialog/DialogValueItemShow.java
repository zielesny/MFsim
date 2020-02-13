/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2020  Achim Zielesny (achim.zielesny@googlemail.com)
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
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.control.CustomPanelValueItemShow;
import de.gnwi.mfsim.gui.control.CustomPanelValueItemShowController;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Value item show dialog
 *
 * @author Achim Zielesny
 */
public class DialogValueItemShow extends CustomDialogApplyCancelSize {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelValueItemShow customValueItemShowPanel;
    private SpringLayout mainPanelSpringLayout;
    private JPanel mainPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000025L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogValueItemShow() {
        super(false, true);
        this.setName(GuiMessage.get("DialogValueItemShow.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogValueItemShowWidth(), Preferences.getInstance().getDialogValueItemShowHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="customValueItemShowPanel">
            {
                this.customValueItemShowPanel = new CustomPanelValueItemShow();
                final TitledBorder selectedFeaturePanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("DialogValueItemShow.selectedFeaturePanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR);
                this.customValueItemShowPanel.getSelectedFeaturePanel().setBorder(selectedFeaturePanelTitledBorder); 
                final TitledBorder featureOverviewPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("DialogValueItemShow.featureOverviewPanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR); 
                this.customValueItemShowPanel.getFeatureOverviewPanel().setBorder(featureOverviewPanelTitledBorder);
                this.mainPanel.add(this.customValueItemShowPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customValueItemShowPanel, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customValueItemShowPanel, 10, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customValueItemShowPanel, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customValueItemShowPanel, 10, SpringLayout.NORTH, this.mainPanel);
            }

            // </editor-fold>
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public CustomPanelValueItemShow getCustomValueItemShowPanel() {
        return customValueItemShowPanel;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static show() method">
    
    /**
     * Show
     *
     * @param aTitle Title for dialog
     * @param aValueItemContainer Value item container
     * @param aRemoveHintTab True: Hint tab is removed, false: Hint tab is shown
     * @param aRemoveErrorTab True: Error tab is removed, false: Error tab is
     * shown
     */
    public static void show(String aTitle, ValueItemContainer aValueItemContainer, boolean aRemoveHintTab, boolean aRemoveErrorTab) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTitle == null || aTitle.isEmpty() || aValueItemContainer == null) {
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Set final variables">
            final DialogValueItemShow tmpValueItemShowDialog = new DialogValueItemShow();
            tmpValueItemShowDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpValueItemShowDialog);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Configure dialog">
            tmpValueItemShowDialog.setTitle(aTitle);
            // Remove tabs if necessary
            // NOTE: FIRST remove tab with higher index ...
            if (aRemoveErrorTab) {
                tmpValueItemShowDialog.getCustomValueItemShowPanel().getSelectedFeatureTabbedPanel().remove(3);
            }
            // ... then tab with lower index
            if (aRemoveHintTab) {
                tmpValueItemShowDialog.getCustomValueItemShowPanel().getSelectedFeatureTabbedPanel().remove(2);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Instantiate CustomPanelValueItemShowController">
            // NOTE: Only ACTIVE value items are shown
            new CustomPanelValueItemShowController(tmpValueItemShowDialog.getCustomValueItemShowPanel(), aValueItemContainer);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpValueItemShowDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogValueItemShowHeightWidth(tmpValueItemShowDialog.getHeight(), tmpValueItemShowDialog.getWidth());
                        tmpValueItemShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"),
                                "actionPerformed()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpValueItemShowDialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogValueItemShowHeightWidth(tmpValueItemShowDialog.getHeight(), tmpValueItemShowDialog.getWidth());
                        tmpValueItemShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()",
                                "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpValueItemShowDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpValueItemShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemShowDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"),
                                "actionPerformed()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpValueItemShowDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpValueItemShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemShowDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"),
                                "actionPerformed()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpValueItemShowDialog.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpValueItemShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"),
                                "actionPerformed()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpValueItemShowDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpValueItemShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemShowDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"),
                                "actionPerformed()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpValueItemShowDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpValueItemShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"),
                                "actionPerformed()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            GuiUtils.checkDialogSize(tmpValueItemShowDialog);
            GuiUtils.centerDialogOnScreen(tmpValueItemShowDialog);
            // Show dialog - Wait
            tmpValueItemShowDialog.setVisible(true);
            // </editor-fold>
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogValueItemShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return;
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
