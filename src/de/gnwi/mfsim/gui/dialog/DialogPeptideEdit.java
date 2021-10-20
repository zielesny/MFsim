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
package de.gnwi.mfsim.gui.dialog;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.control.CustomPanelPeptideEdit;
import de.gnwi.mfsim.gui.control.CustomPanelPeptideEditController;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.peptide.PeptideToSpices;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.commons.lang3.StringUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Peptide edit dialog
 *
 * @author Achim Zielesny
 */
public class DialogPeptideEdit extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelPeptideEdit customPeptidePanel;

    private JPanel mainPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    
    /**
     * Spices string of peptide
     */
    private static String peptideSpicesString;
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000051L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogPeptideEdit() {
        super();
        this.setName(GuiMessage.get("DialogPeptideEdit.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogPeptideEditWidth(), Preferences.getInstance().getDialogPeptideEditHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new BorderLayout());
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="customPeptidePanel">
            {
                this.customPeptidePanel = new CustomPanelPeptideEdit();
                this.mainPanel.add(this.customPeptidePanel, BorderLayout.CENTER);
            }

            // </editor-fold>
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomPeptidePanel
     * 
     * @return CustomPeptidePanel
     */
    public CustomPanelPeptideEdit getCustomPeptidePanel() {
        return customPeptidePanel;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="ChangeReceiverInterface notifyChange method">
    
    /**
     * Notify method for this instance as a change receiver
     *
     * @param aChangeInfo Change information
     * @param aChangeNotifier Object that notifies change
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeNotifier instanceof CustomPanelPeptideEditController) {
            switch (aChangeInfo.getChangeType()) {
                case PEPTIDE_SYNTAX_CORRECT_CHANGE:
                    if (!this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(true);
                    }
                    break;
                case PEPTIDE_SYNTAX_NO_CHANGE:
                case PEPTIDE_SYNTAX_ERROR_CHANGE:
                    if (this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(false);
                    }
                    break;
            }
        }
    }

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static edit() method">
    
    /**
     * Returns Spices string of peptide or empty string if no peptide was defined
     *
     * @return Spices string of peptide or empty string if no peptide was defined
     */
    public static String getPeptideSpices() {
        // <editor-fold defaultstate="collapsed" desc="Set static variables">
        DialogPeptideEdit.peptideSpicesString = "";

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Instantiate dialog">
            final PeptideToSpices tmpPeptideToSpices = new PeptideToSpices();
            final DialogPeptideEdit tmpDialogPeptideEdit = new DialogPeptideEdit();
            tmpDialogPeptideEdit.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpDialogPeptideEdit);
            // Make apply-button invisible
            tmpDialogPeptideEdit.getApplyButton().setVisible(false);
            // Set dialog title
            tmpDialogPeptideEdit.setTitle(GuiMessage.get("DialogPeptideEdit.title"));
            // Instantiate CustomPanelPeptideEditController
            final CustomPanelPeptideEditController tmpCustomPanelPeptideEditController = new CustomPanelPeptideEditController(tmpDialogPeptideEdit.getCustomPeptidePanel(), tmpPeptideToSpices);
            // Add tmpDialogPeptideEdit as change receiver
            tmpCustomPanelPeptideEditController.addChangeReceiver(tmpDialogPeptideEdit);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpDialogPeptideEdit.addWindowListener(new WindowAdapter() {

                public void windowClosing(final WindowEvent e) {
                    try {
                        // Confirm cancel operation if peptide changed
                        if (tmpDialogPeptideEdit.getCustomPeptidePanel().getPeptideTextArea().getText().isEmpty()
                                || GuiUtils.getYesNoDecision(GuiMessage.get("DialogPeptideEdit.Cancel.Title"), GuiMessage.get("DialogPeptideEdit.Cancel"))) {
                            // Set dialog size in BasicPreferences
                            Preferences.getInstance().setDialogPeptideEditHeightWidth(tmpDialogPeptideEdit.getHeight(), tmpDialogPeptideEdit.getWidth());
                            // Set result before dispose()
                            DialogPeptideEdit.peptideSpicesString = "";
                            tmpDialogPeptideEdit.dispose();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpDialogPeptideEdit.getApplyButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set previous definition
                        Preferences.getInstance().addPreviousPeptide(tmpDialogPeptideEdit.getCustomPeptidePanel().getPeptideTextArea().getText());
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogPeptideEditHeightWidth(tmpDialogPeptideEdit.getHeight(), tmpDialogPeptideEdit.getWidth());
                        // Set wait cursor (since conversion to Spices may take some time)
                        MouseCursorManagement.getInstance().setWaitCursor();
                        // Set result before dispose()
                        boolean tmpDoCarriageReturn = true;
                        DialogPeptideEdit.peptideSpicesString = tmpPeptideToSpices.convertOneLetterPeptideToSpices(
                                StringUtils.deleteWhitespace(tmpDialogPeptideEdit.getCustomPeptidePanel().getPeptideTextArea().getText()),
                                tmpDoCarriageReturn);
                                // Old code:
                                // tmpDialogPeptideEdit.getCustomPeptidePanel().getPeptideTextArea().getText().replaceAll("\\s+", ""),
                        // Set default cursor
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        tmpDialogPeptideEdit.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpDialogPeptideEdit.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Confirm cancel operation if peptide changed
                        if (tmpDialogPeptideEdit.getCustomPeptidePanel().getPeptideTextArea().getText().isEmpty()
                                || GuiUtils.getYesNoDecision(GuiMessage.get("DialogPeptideEdit.Cancel.Title"), GuiMessage.get("DialogPeptideEdit.Cancel"))) {
                            // Set dialog size in BasicPreferences
                            Preferences.getInstance().setDialogPeptideEditHeightWidth(tmpDialogPeptideEdit.getHeight(), tmpDialogPeptideEdit.getWidth());
                            // Set result before dispose()
                            DialogPeptideEdit.peptideSpicesString = "";
                            tmpDialogPeptideEdit.dispose();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpDialogPeptideEdit.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpDialogPeptideEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogPeptideEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpDialogPeptideEdit.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpDialogPeptideEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogPeptideEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpDialogPeptideEdit.getCenterDialogButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpDialogPeptideEdit);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpDialogPeptideEdit.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpDialogPeptideEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogPeptideEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpDialogPeptideEdit.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpDialogPeptideEdit);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogPeptideEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            GuiUtils.checkDialogSize(tmpDialogPeptideEdit);
            GuiUtils.centerDialogOnScreen(tmpDialogPeptideEdit);
            tmpDialogPeptideEdit.setVisible(true);

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Return dialog result">
            return DialogPeptideEdit.peptideSpicesString;

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "edit()", "DialogPeptideEdit"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return "";
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
