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
package de.gnwi.mfsim.gui.dialog;

import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.control.CustomPanelStructureShow;
import de.gnwi.mfsim.gui.control.CustomPanelStructureShowController;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Molecular structure show dialog
 *
 * @author Achim Zielesny
 */
public class DialogStructureShow extends CustomDialogApplyCancelSize {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelStructureShow customPanelStructureShow;
    private JPanel mainPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000058L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogStructureShow() {
        super(false, true);
        this.setName(GuiMessage.get("DialogStructureShow.Structure.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogStructureEditWidth(), Preferences.getInstance().getDialogStructureEditHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">

        {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new BorderLayout());
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="customPanelStructureShow">

            {
                this.customPanelStructureShow = new CustomPanelStructureShow();
                this.mainPanel.add(this.customPanelStructureShow, BorderLayout.CENTER);
            }

            // </editor-fold>

        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomPanelStructureShow
     * 
     * @return CustomPanelStructureShow
     */
    public CustomPanelStructureShow getCustomPanelStructureShow() {
        return customPanelStructureShow;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static edit() method">
    
    /**
     * Show structure
     *
     * @param aValueItem Value item
     * @param anIsMonomer True: Monomer is to be edit, false: Structure is to be edit
     * @param anAvailableParticles Available particles (is NOT allowed to be null)
     * @param anAvailableMonomers Available monomers (may be null)
     */
    public static void show(ValueItem aValueItem, boolean anIsMonomer, HashMap<String, String> anAvailableParticles, HashMap<String, String> anAvailableMonomers) {
        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aValueItem == null) {
            return;
        }
        if (anAvailableParticles == null) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set final variables">

        // Set monomer/molecular structure
        final String tmpStructure = aValueItem.getValue();
        // Set monomer flag
        final boolean tmpIsMonomer = anIsMonomer;

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">

            // <editor-fold defaultstate="collapsed" desc="- Instantiate dialog">

            final DialogStructureShow tmpDialogStructureShow = new DialogStructureShow();
            tmpDialogStructureShow.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpDialogStructureShow);
            // Set dialog title
            if (tmpIsMonomer) {
                tmpDialogStructureShow.setTitle(GuiMessage.get("DialogStructureShow.Monomer.title"));
            } else {
                tmpDialogStructureShow.setTitle(GuiMessage.get("DialogStructureShow.Structure.title"));
            }
            // Instantiate CustomPanelStructureShowController
            final CustomPanelStructureShowController tmpCustomPanelStructureShowController = new CustomPanelStructureShowController(tmpDialogStructureShow.getCustomPanelStructureShow(), tmpIsMonomer,
                    anAvailableParticles, anAvailableMonomers, tmpStructure);

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpDialogStructureShow.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogStructureShow.getHeight(), tmpDialogStructureShow.getWidth());
                        tmpDialogStructureShow.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogStructureShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogStructureShow.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogStructureShow.getHeight(), tmpDialogStructureShow.getWidth());
                        tmpDialogStructureShow.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogStructureShow.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpDialogStructureShow)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogStructureShow);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogStructureShow.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpDialogStructureShow)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogStructureShow);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogStructureShow.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpDialogStructureShow);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogStructureShow.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpDialogStructureShow)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogStructureShow);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureShow.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpDialogStructureShow);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">

            GuiUtils.checkDialogSize(tmpDialogStructureShow);
            GuiUtils.centerDialogOnScreen(tmpDialogStructureShow);
            tmpDialogStructureShow.setVisible(true);

            // </editor-fold>

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "edit()", "DialogStructureShow"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
