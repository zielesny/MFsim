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
package de.gnwi.mfsim.gui.control;

import de.gnwi.mfsim.gui.message.GuiMessage;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Panel for slicer graphics
 *
 * @author Achim Zielesny
 */
public class CustomPanelStepSlicer extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JLabel generalInfoLabel;
    private CustomPanelSimulationBoxSlicer simulationBoxPanel;
    private JButton secondSettingsButton;
    private JButton firstSettingsButton;
    private SpringLayout settingsButtonPanelSpringLayout;
    private JPanel settingsButtonPanel;
    private JRadioButton movieRadioButton;
    private JRadioButton animationRadioButton;
    private ButtonGroup settingsButtonGroup = new ButtonGroup();
    private SpringLayout settingsPanelSpringLayout;
    private JPanel settingsPanel;
    private CustomPanelImage boxViewImagePanel;
    private JRadioButton xyBottomRadioButton;
    private JRadioButton xyTopRadioButton;
    private JRadioButton yzRightRadioButton;
    private JRadioButton yzLeftRadioButton;
    private ButtonGroup boxViewButtonGroup = new ButtonGroup();
    private JRadioButton xzBackRadioButton;
    private JRadioButton xzFrontRadioButton;
    private SpringLayout boxViewPanelSpringLayout;
    private JPanel boxViewPanel;
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000056L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelStepSlicer() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="simulationBoxPanel">
        {
            this.simulationBoxPanel = new CustomPanelSimulationBoxSlicer();
            this.add(this.simulationBoxPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.simulationBoxPanel, -220, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.simulationBoxPanel, 0, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxPanel, 0, SpringLayout.NORTH, this);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="boxViewPanel">
        {
            this.boxViewPanel = new JPanel();
            this.boxViewPanel.setToolTipText(GuiMessage.get("CustomPanelSlicer.boxViewPanel.toolTipText")); 
            this.boxViewPanelSpringLayout = new SpringLayout();
            this.boxViewPanel.setLayout(this.boxViewPanelSpringLayout);
            this.boxViewPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelSlicer.boxViewPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.add(this.boxViewPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.boxViewPanel, 279, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.boxViewPanel, 0, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.boxViewPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.boxViewPanel, -215, SpringLayout.EAST, this);
            {
                this.xzFrontRadioButton = new JRadioButton();
                this.xzFrontRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.xzFrontRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xzFrontRadioButton);
                this.xzFrontRadioButton.setText(GuiMessage.get("CustomPanelSlicer.xzFrontRadioButton.text")); 
                this.boxViewPanel.add(this.xzFrontRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xzFrontRadioButton, 90, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xzFrontRadioButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xzFrontRadioButton, 25, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xzFrontRadioButton, 5, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xzBackRadioButton = new JRadioButton();
                this.xzBackRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.xzBackRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.xzBackRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.xzBackRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xzBackRadioButton);
                this.xzBackRadioButton.setText(GuiMessage.get("CustomPanelSlicer.xzBackRadioButton.text")); 
                this.boxViewPanel.add(this.xzBackRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xzBackRadioButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xzBackRadioButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xzBackRadioButton, 25, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xzBackRadioButton, 5, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.yzLeftRadioButton = new JRadioButton();
                this.yzLeftRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.yzLeftRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.yzLeftRadioButton);
                this.yzLeftRadioButton.setText(GuiMessage.get("CustomPanelSlicer.yzLeftRadioButton.text")); 
                this.boxViewPanel.add(this.yzLeftRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.yzLeftRadioButton, 90, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.yzLeftRadioButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.yzLeftRadioButton, 50, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.yzLeftRadioButton, 30, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.yzRightRadioButton = new JRadioButton();
                this.yzRightRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.yzRightRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.yzRightRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.yzRightRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.yzRightRadioButton);
                this.yzRightRadioButton.setText(GuiMessage.get("CustomPanelSlicer.yzRightRadioButton.text")); 
                this.boxViewPanel.add(this.yzRightRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.yzRightRadioButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.yzRightRadioButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.yzRightRadioButton, 50, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.yzRightRadioButton, 30, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xyTopRadioButton = new JRadioButton();
                this.xyTopRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.xyTopRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xyTopRadioButton);
                this.xyTopRadioButton.setText(GuiMessage.get("CustomPanelSlicer.xyTopRadioButton.text")); 
                this.boxViewPanel.add(this.xyTopRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xyTopRadioButton, 90, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xyTopRadioButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xyTopRadioButton, 75, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xyTopRadioButton, 55, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xyBottomRadioButton = new JRadioButton();
                this.xyBottomRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.xyBottomRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.xyBottomRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.xyBottomRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xyBottomRadioButton);
                this.xyBottomRadioButton.setText(GuiMessage.get("CustomPanelSlicer.xyBottomRadioButton.text")); 
                this.boxViewPanel.add(this.xyBottomRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xyBottomRadioButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xyBottomRadioButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xyBottomRadioButton, 75, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xyBottomRadioButton, 55, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.boxViewImagePanel = new CustomPanelImage();
                this.boxViewImagePanel.setToolTipText(GuiMessage.get("CustomPanelSlicer.boxViewImagePanel.toolTipText")); 
                this.boxViewImagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.boxViewPanel.add(this.boxViewImagePanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.boxViewImagePanel, 240, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.boxViewImagePanel, 85, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.boxViewImagePanel, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.boxViewImagePanel, 10, SpringLayout.WEST, this.boxViewPanel);
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="settingsPanel">
        {
            this.settingsPanel = new JPanel();
            this.settingsPanel.setToolTipText(GuiMessage.get("CustomPanelSlicer.settingsPanel.toolTipText")); 
            this.settingsPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelSlicer.settingsPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.settingsPanelSpringLayout = new SpringLayout();
            this.settingsPanel.setLayout(this.settingsPanelSpringLayout);
            this.add(this.settingsPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.settingsPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.settingsPanel, -110, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.settingsPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.settingsPanel, -215, SpringLayout.EAST, this);
            {
                this.animationRadioButton = new JRadioButton();
                settingsButtonGroup.add(animationRadioButton);
                this.animationRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.animationRadioButton.toolTipText")); 
                this.animationRadioButton.setText(GuiMessage.get("CustomPanelSlicer.animationRadioButton.text")); 
                this.settingsPanel.add(this.animationRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.animationRadioButton, 100, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.animationRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.animationRadioButton, 25, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.animationRadioButton, 5, SpringLayout.NORTH, this.settingsPanel);
            }
            {
                this.movieRadioButton = new JRadioButton();
                settingsButtonGroup.add(movieRadioButton);
                this.movieRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.movieRadioButton.toolTipText")); 
                this.movieRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.movieRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.movieRadioButton.setText(GuiMessage.get("CustomPanelSlicer.movieRadioButton.text")); 
                this.settingsPanel.add(this.movieRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.movieRadioButton, 25, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.movieRadioButton, 5, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.movieRadioButton, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.movieRadioButton, -90, SpringLayout.EAST, this.settingsPanel);
            }
            {
                this.settingsButtonPanel = new JPanel();
                this.settingsButtonPanel.setToolTipText(GuiMessage.get("CustomPanelSlicer.settingsButtonPanel.toolTipText")); 
                this.settingsButtonPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.settingsButtonPanelSpringLayout = new SpringLayout();
                this.settingsButtonPanel.setLayout(this.settingsButtonPanelSpringLayout);
                this.settingsPanel.add(this.settingsButtonPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.settingsButtonPanel, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.settingsButtonPanel, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.settingsButtonPanel, 75, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.settingsButtonPanel, 35, SpringLayout.NORTH, this.settingsPanel);
                {
                    this.firstSettingsButton = new JButton();
                    this.firstSettingsButton.setText(GuiMessage.get("CustomPanelSlicer.firstSettingsButton.text")); 
                    this.settingsButtonPanel.add(this.firstSettingsButton);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.firstSettingsButton, 35, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.firstSettingsButton, 0, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.firstSettingsButton, 90, SpringLayout.WEST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.firstSettingsButton, 0, SpringLayout.WEST, this.settingsButtonPanel);
                }
                {
                    this.secondSettingsButton = new JButton();
                    this.secondSettingsButton.setText(GuiMessage.get("CustomPanelSlicer.secondSettingsButton.text")); 
                    this.settingsButtonPanel.add(this.secondSettingsButton);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.secondSettingsButton, 0, SpringLayout.EAST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.secondSettingsButton, -90, SpringLayout.EAST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.secondSettingsButton, 35, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.secondSettingsButton, 0, SpringLayout.NORTH, this.settingsButtonPanel);
                }
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="generalInfoLabel">
        {
            this.generalInfoLabel = new JLabel();
            this.generalInfoLabel.setText(GuiMessage.get("CustomPanelSlicer.generalInfoLabel.text")); 
            this.generalInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.generalInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
            add(this.generalInfoLabel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.generalInfoLabel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.generalInfoLabel, 10, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.generalInfoLabel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.generalInfoLabel, -35, SpringLayout.SOUTH, this);
        }
        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public JRadioButton getXzFrontRadioButton() {
        return xzFrontRadioButton;
    }

    public JRadioButton getXzBackRadioButton() {
        return xzBackRadioButton;
    }

    public JRadioButton getYzLeftRadioButton() {
        return yzLeftRadioButton;
    }

    public JRadioButton getYzRightRadioButton() {
        return yzRightRadioButton;
    }

    public JRadioButton getXyTopRadioButton() {
        return xyTopRadioButton;
    }

    public JRadioButton getXyBottomRadioButton() {
        return xyBottomRadioButton;
    }

    public CustomPanelImage getBoxViewImagePanel() {
        return boxViewImagePanel;
    }

    public JRadioButton getAnimationRadioButton() {
        return animationRadioButton;
    }

    public JRadioButton getMovieRadioButton() {
        return movieRadioButton;
    }

    public JButton getFirstSettingsButton() {
        return firstSettingsButton;
    }

    public JButton getSecondSettingsButton() {
        return secondSettingsButton;
    }

    public JPanel getBoxViewPanel() {
        return boxViewPanel;
    }

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    public CustomPanelSimulationBoxSlicer getSimulationBoxPanel() {
        return simulationBoxPanel;
    }

    public JLabel getGeneralInfoLabel() {
        return generalInfoLabel;
    }
    // </editor-fold>

}
