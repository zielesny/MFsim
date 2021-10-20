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

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

/**
 * Dialog base class
 *
 * @author Achim Zielesny
 */
public class CustomDialogApplyCancelSize extends JDialog {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JButton centerDialogButton;
    private JButton minimizeDialogSizeButton;
    private JButton maximizeDialogSizeButton;
    private JButton customDialogSizeButton;
    private JButton setCustomDialogPreferencesButton;
    private JButton applyButton;
    private JButton cancelButton;
    private SpringLayout buttonPanelSpringLayout;
    private JPanel buttonPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000002L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Base dialog constructor
     */
    public CustomDialogApplyCancelSize() {
        this(true, true);
    }

    /**
     * Base dialog constructor
     * 
     * @param anIsApply True: Apply button is visible, false: Otherwise
     * @param anIsSize True: Size related buttons are visible, false: Otherwise
     */
    public CustomDialogApplyCancelSize(
        boolean anIsApply,
        boolean anIsSize
    ) {
        setName(GuiMessage.get("CustomDialogApplyCancelSize.name")); 
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(Preferences.getInstance().getDialogValueItemEditWidth(), Preferences.getInstance().getDialogValueItemEditHeight());
        // <editor-fold defaultstate="collapsed" desc="buttonPanel">
        {
            this.buttonPanel = new JPanel();
            this.buttonPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.buttonPanel.setMinimumSize(new Dimension(0, 50));
            this.buttonPanelSpringLayout = new SpringLayout();
            this.buttonPanel.setLayout(this.buttonPanelSpringLayout);
            this.buttonPanel.setPreferredSize(new Dimension(0, 50));
            getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);
            // <editor-fold defaultstate="collapsed" desc="centerDialogButton">
            if (anIsSize) {
                this.centerDialogButton = new JButton();
                this.centerDialogButton.setToolTipText(GuiMessage.get("CustomDialogApplyCancelSize.centerDialogButton.toolTipText")); 
                this.centerDialogButton.setText(GuiMessage.get("CustomDialogApplyCancelSize.centerDialogButton.text")); 
                this.buttonPanel.add(this.centerDialogButton);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.centerDialogButton, -5, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.centerDialogButton, -40, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.centerDialogButton, 85, SpringLayout.WEST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.centerDialogButton, 5, SpringLayout.WEST, this.buttonPanel);
            } else {
                this.centerDialogButton = null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="minimizeDialogSizeButton">
            if (anIsSize) {
                this.minimizeDialogSizeButton = new JButton();
                this.minimizeDialogSizeButton.setToolTipText(GuiMessage.get("CustomDialogApplyCancelSize.minimizeDialogSizeButton.toolTipText")); 
                this.minimizeDialogSizeButton.setText(GuiMessage.get("CustomDialogApplyCancelSize.minimizeDialogSizeButton.text")); 
                this.buttonPanel.add(this.minimizeDialogSizeButton);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.minimizeDialogSizeButton, -5, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.minimizeDialogSizeButton, -40, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.minimizeDialogSizeButton, 165, SpringLayout.WEST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.minimizeDialogSizeButton, 85, SpringLayout.WEST, this.buttonPanel);
            } else {
                this.minimizeDialogSizeButton = null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="maximizeDialogSizeButton">
            if (anIsSize) {
                this.maximizeDialogSizeButton = new JButton();
                this.maximizeDialogSizeButton.setToolTipText(GuiMessage.get("CustomDialogApplyCancelSize.maximizeDialogSizeButton.toolTipText")); 
                this.maximizeDialogSizeButton.setText(GuiMessage.get("CustomDialogApplyCancelSize.maximizeDialogSizeButton.text")); 
                this.buttonPanel.add(this.maximizeDialogSizeButton);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.maximizeDialogSizeButton, -5, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.maximizeDialogSizeButton, -40, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.maximizeDialogSizeButton, 245, SpringLayout.WEST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.maximizeDialogSizeButton, 165, SpringLayout.WEST, this.buttonPanel);
            } else {
                this.maximizeDialogSizeButton = null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="customDialogSizeButton">
            if (anIsSize) {
                this.customDialogSizeButton = new JButton();
                this.customDialogSizeButton.setToolTipText(GuiMessage.get("CustomDialogApplyCancelSize.customDialogSizeButton.toolTipText")); 
                this.customDialogSizeButton.setText(GuiMessage.get("CustomDialogApplyCancelSize.customDialogSizeButton.text")); 
                this.buttonPanel.add(this.customDialogSizeButton);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customDialogSizeButton, -5, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customDialogSizeButton, -40, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customDialogSizeButton, 325, SpringLayout.WEST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customDialogSizeButton, 245, SpringLayout.WEST, this.buttonPanel);
            } else {
                this.customDialogSizeButton = null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="setCustomDialogPreferencesButton">
            if (anIsSize) {
                this.setCustomDialogPreferencesButton = new JButton();
                this.setCustomDialogPreferencesButton.setToolTipText(GuiMessage.get("CustomDialogApplyCancelSize.setCustomDialogPreferencesButton.toolTipText")); 
                this.setCustomDialogPreferencesButton.setText(GuiMessage.get("CustomDialogApplyCancelSize.setCustomDialogPreferencesButton.text")); 
                this.buttonPanel.add(this.setCustomDialogPreferencesButton);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.setCustomDialogPreferencesButton, -5, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.setCustomDialogPreferencesButton, -40, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.setCustomDialogPreferencesButton, 405, SpringLayout.WEST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.setCustomDialogPreferencesButton, 325, SpringLayout.WEST, this.buttonPanel);
            } else {
                this.setCustomDialogPreferencesButton = null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="applyButton">
            if (anIsApply) {
                this.applyButton = new JButton();
                this.applyButton.setMnemonic(KeyEvent.VK_A);
                this.applyButton.setToolTipText(GuiMessage.get("CustomDialogApplyCancelSize.applyButton.toolTipText")); 
                this.applyButton.setText(GuiMessage.get("CustomDialogApplyCancelSize.applyButton.text")); 
                this.buttonPanel.add(this.applyButton);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.applyButton, -85, SpringLayout.EAST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.applyButton, -165, SpringLayout.EAST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.applyButton, -5, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.applyButton, -40, SpringLayout.SOUTH, this.buttonPanel);
            } else {
                this.applyButton = null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="cancelButton">

            {
                this.cancelButton = new JButton();
                this.cancelButton.setMnemonic(KeyEvent.VK_C);
                this.cancelButton.setToolTipText(GuiMessage.get("CustomDialogApplyCancelSize.cancelButton.toolTipText")); 
                this.cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                this.cancelButton.setText(GuiMessage.get("CustomDialogApplyCancelSize.cancelButton.text")); 
                this.buttonPanel.add(this.cancelButton);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.cancelButton, -5, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.cancelButton, -40, SpringLayout.SOUTH, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.cancelButton, -5, SpringLayout.EAST, this.buttonPanel);
                this.buttonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.cancelButton, -85, SpringLayout.EAST, this.buttonPanel);
            }

            // </editor-fold>
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Exposed methods">
    /**
     * CenterDialogButton
     * 
     * @return CenterDialogButton
     */
    public JButton getCenterDialogButton() {
        if (this.centerDialogButton != null) {
            return centerDialogButton;
        } else {
            ModelUtils.appendToLogfile(true, "CustomDialogApplyCancelSize.getCenterDialogButton: Button is used but was not defined.");
            return null;
        }
    }

    /**
     * MinimizeDialogSizeButton
     * 
     * @return MinimizeDialogSizeButton
     */
    public JButton getMinimizeDialogSizeButton() {
        if (this.minimizeDialogSizeButton != null) {
            return minimizeDialogSizeButton;
        } else {
            ModelUtils.appendToLogfile(true, "CustomDialogApplyCancelSize.getMinimizeDialogSizeButton: Button is used but was not defined.");
            return null;
        }
    }

    /**
     * MaximizeDialogSizeButton
     * 
     * @return MaximizeDialogSizeButton
     */
    public JButton getMaximizeDialogSizeButton() {
        if (this.maximizeDialogSizeButton != null) {
            return maximizeDialogSizeButton;
        } else {
            ModelUtils.appendToLogfile(true, "CustomDialogApplyCancelSize.getMaximizeDialogSizeButton: Button is used but was not defined.");
            return null;
        }
    }

    /**
     * CustomDialogSizeButton
     * 
     * @return CustomDialogSizeButton
     */
    public JButton getCustomDialogSizeButton() {
        if (this.customDialogSizeButton != null) {
            return customDialogSizeButton;
        } else {
            ModelUtils.appendToLogfile(true, "CustomDialogApplyCancelSize.getCustomDialogSizeButton: Button is used but was not defined.");
            return null;
        }
    }

    /**
     * CustomDialogPreferencesButton
     * 
     * @return CustomDialogPreferencesButton
     */
    public JButton getCustomDialogPreferencesButton() {
        if (this.setCustomDialogPreferencesButton != null) {
            return setCustomDialogPreferencesButton;
        } else {
            ModelUtils.appendToLogfile(true, "CustomDialogApplyCancelSize.getCustomDialogPreferencesButton: Button is used but was not defined.");
            return null;
        }
    }

    /**
     * ApplyButton
     * 
     * @return ApplyButton
     */
    protected JButton getApplyButton() {
        if (this.applyButton != null) {
            return this.applyButton;
        } else {
            ModelUtils.appendToLogfile(true, "CustomDialogApplyCancelSize.getApplyButton: Button is used but was not defined.");
            return null;
        }
    }

    /**
     * CancelButton
     * 
     * @return CancelButton
     */
    protected JButton getCancelButton() {
        return cancelButton;
    }
    // </editor-fold>

}
