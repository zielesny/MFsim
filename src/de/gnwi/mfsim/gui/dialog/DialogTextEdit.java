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

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Text edit dialog
 *
 * @author Achim Zielesny
 */
public class DialogTextEdit extends CustomDialogApplyCancelSize {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JTextArea textArea;
    private JScrollPane textScrollPanel;
    private SpringLayout mainPanelSpringLayout;
    private JPanel mainPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    
    /**
     * True: Ok-button was used, false: Otherwise. NOTE: Static variable is
     * necessary for dialog result since result is disposed.
     */
    private static boolean resultIsOk;
    /**
     * Changed text. NOTE: Static variable is necessary for dialog result since
     * result is disposed.
     */
    private static String changedText;
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000050L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogTextEdit() {
        super();
        this.setName(GuiMessage.get("DialogTextEdit.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogTextEditWidth(), Preferences.getInstance().getDialogTextEditHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="textScrollPanel">
            {
                this.textScrollPanel = new JScrollPane();
                this.textScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.mainPanel.add(this.textScrollPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textScrollPanel, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textScrollPanel, 10, SpringLayout.NORTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.textScrollPanel, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.textScrollPanel, 10, SpringLayout.WEST, this.mainPanel);

                // <editor-fold defaultstate="collapsed" desc="textArea">
                {
                    this.textArea = new JTextArea();
                    this.textArea.setLineWrap(true);
                    this.textArea.setWrapStyleWord(true);
                    this.textArea.setOpaque(false);
                    this.textArea.setEditable(true);
                    this.textScrollPanel.setViewportView(this.textArea);
                }

                // </editor-fold>
            }

            // </editor-fold>
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * TextArea
     * 
     * @return TextArea
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static editText() methods">
    
    /**
     * Returns if dialog has changed value items
     *
     * @param aTitle Title for dialog
     * @param anInitialText Text to be edited
     * @return true: Text is changed, false: Otherwise
     */
    public static String editText(String aTitle, String anInitialText) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTitle == null || aTitle.isEmpty() || anInitialText == null) {

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasChanged()", "DialogTextEdit"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return anInitialText;
        }

        // </editor-fold>
        try {

            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Set final variables">
            final DialogTextEdit tmpTextEditDialog = new DialogTextEdit();
            tmpTextEditDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpTextEditDialog);
            final String tmpInitialText = anInitialText;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Configure dialog">
            // Set title
            tmpTextEditDialog.setTitle(aTitle);
            // Set text
            tmpTextEditDialog.getTextArea().setFont(new java.awt.Font("Monospaced", 0, 12));
            tmpTextEditDialog.getTextArea().setText(tmpInitialText);
            tmpTextEditDialog.getTextArea().setCaretPosition(0);
            // Set Apply-button invisible
            tmpTextEditDialog.getApplyButton().setVisible(false);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpTextEditDialog.getTextArea().getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    try {
                        if (!tmpTextEditDialog.getTextArea().getText().equals(tmpInitialText)) {
                            // Set Apply-button invisible
                            if (!tmpTextEditDialog.getApplyButton().isVisible()) {
                                tmpTextEditDialog.getApplyButton().setVisible(true);
                            }
                        } else {
                            tmpTextEditDialog.getApplyButton().setVisible(false);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "insertUpdate()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void removeUpdate(DocumentEvent e) {
                    try {
                        if (!tmpTextEditDialog.getTextArea().getText().equals(tmpInitialText)) {
                            // Set Apply-button invisible
                            if (!tmpTextEditDialog.getApplyButton().isVisible()) {
                                tmpTextEditDialog.getApplyButton().setVisible(true);
                            }
                        } else {
                            tmpTextEditDialog.getApplyButton().setVisible(false);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "removeUpdate()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void changedUpdate(DocumentEvent e) {
                    // NOTE: Plain text components don't fire these events, so do nothing
                }
            });
            tmpTextEditDialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent e) {
                    try {
                        if (!tmpTextEditDialog.getTextArea().getText().equals(tmpInitialText)
                                && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogTextEdit.Cancel.Title"), GuiMessage.get("DialogTextEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogTextEditHeightWidth(tmpTextEditDialog.getHeight(), tmpTextEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogTextEdit.resultIsOk = false;
                        DialogTextEdit.changedText = tmpTextEditDialog.getTextArea().getText();

                        // </editor-fold>
                        tmpTextEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpTextEditDialog.getApplyButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogTextEditHeightWidth(tmpTextEditDialog.getHeight(), tmpTextEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogTextEdit.resultIsOk = true;
                        DialogTextEdit.changedText = tmpTextEditDialog.getTextArea().getText();

                        // </editor-fold>
                        tmpTextEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpTextEditDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (!tmpTextEditDialog.getTextArea().getText().equals(tmpInitialText)
                                && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogTextEdit.Cancel.Title"), GuiMessage.get("DialogTextEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogTextEditHeightWidth(tmpTextEditDialog.getHeight(), tmpTextEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogTextEdit.resultIsOk = false;
                        DialogTextEdit.changedText = tmpTextEditDialog.getTextArea().getText();

                        // </editor-fold>
                        tmpTextEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpTextEditDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpTextEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpTextEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpTextEditDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpTextEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpTextEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpTextEditDialog.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpTextEditDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpTextEditDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpTextEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpTextEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpTextEditDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpTextEditDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogTextEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            GuiUtils.checkDialogSize(tmpTextEditDialog);
            GuiUtils.centerDialogOnScreen(tmpTextEditDialog);
            // Show dialog - Wait
            tmpTextEditDialog.setVisible(true);

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">
            if (!DialogTextEdit.resultIsOk) {

                // <editor-fold defaultstate="collapsed" desc="Canceled/Closed - return initial text">
                return tmpInitialText;

                // </editor-fold>
            } else {

                // <editor-fold defaultstate="collapsed" desc="Ok - return changed text">
                return DialogTextEdit.changedText;

                // </editor-fold>
            }

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasChanged()", "DialogTextEdit"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return anInitialText;
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>
    
}
