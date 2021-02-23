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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Panel for compartment graphics
 *
 * @author Achim Zielesny
 */
public class CustomPanelCompartmentGraphics extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private SpringLayout settingsButtonPanelSpringLayout;

    private JPanel settingsButtonPanel;

    private JButton defaultGraphicsButton;

    private ButtonGroup configureSettingsButtonGroup = new ButtonGroup();

    private JRadioButton transparencyRadioButton;

    private JRadioButton fogRadioButton;

    private JRadioButton bodyAttenuationRadioButton;

    private JSlider valueSlider;

    private JButton configureSettingsButton;

    private SpringLayout settingsPanelSpringLayout;

    private JLabel infoLabel;

    private JPanel settingsPanel;

    private CustomPanelImage boxViewImagePanel;

    private JRadioButton xyBottomRadioButton;

    private JRadioButton xyTopRadioButton;

    private JRadioButton yzRightRadioButton;

    private JRadioButton yzLeftRadioButton;

    private ButtonGroup boxViewButtonGroup = new ButtonGroup();

    private JRadioButton xzBackRadioButton;

    private JRadioButton xzFrontRadioButton;

    private SpringLayout simulationBoxPanelSpringLayout;

    private JPanel simulationBoxPanel;

    private SpringLayout boxViewPanelSpringLayout;

    private JPanel boxViewPanel;

    private JSlider thirdDimensionSlider;

    private JLabel coordinatesLabel;

    private CustomPanelSimulationBoxDraw simulationBoxDrawPanel;

    private SpringLayout springLayout;

    private JLabel geometryInfoLabel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000034L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelCompartmentGraphics() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);
        // <editor-fold defaultstate="collapsed" desc="simulationBoxPanel">
        {
            this.simulationBoxPanel = new JPanel();
            this.simulationBoxPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelCompartmentGraphics.simulationBoxPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.simulationBoxPanelSpringLayout = new SpringLayout();
            this.simulationBoxPanel.setLayout(this.simulationBoxPanelSpringLayout);
            this.simulationBoxPanel.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.simulationBoxPanel.toolTipText")); 
            add(this.simulationBoxPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.simulationBoxPanel, -230, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.simulationBoxPanel, 10, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxPanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxPanel, 10, SpringLayout.NORTH, this);
            {
                this.simulationBoxDrawPanel = new CustomPanelSimulationBoxDraw();
                this.simulationBoxPanel.add(this.simulationBoxDrawPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.simulationBoxDrawPanel, -65, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.simulationBoxDrawPanel, 10, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxDrawPanel, -65, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxDrawPanel, 40, SpringLayout.NORTH, this.simulationBoxPanel);
            }
            {
                this.coordinatesLabel = new JLabel();
                this.coordinatesLabel.setHorizontalAlignment(SwingConstants.CENTER);
                this.coordinatesLabel.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.coordinatesLabel.toolTipText")); 
                this.coordinatesLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                this.coordinatesLabel.setText(GuiMessage.get("CustomPanelCompartmentGraphics.coordinatesLabel.text")); 
                this.simulationBoxPanel.add(this.coordinatesLabel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.coordinatesLabel, -65, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.coordinatesLabel, 10, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.coordinatesLabel, 35, SpringLayout.NORTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.coordinatesLabel, 10, SpringLayout.NORTH, this.simulationBoxPanel);
            }
            {
                this.thirdDimensionSlider = new JSlider();
                this.thirdDimensionSlider.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.thirdDimensionSlider.toolTipText")); 
                this.thirdDimensionSlider.setMaximum(1000);
                this.thirdDimensionSlider.setValue(0);
                this.thirdDimensionSlider.setOrientation(SwingConstants.VERTICAL);
                this.thirdDimensionSlider.setPaintTicks(true);
                this.thirdDimensionSlider.setMinorTickSpacing(20);
                this.thirdDimensionSlider.setMajorTickSpacing(100);
                this.simulationBoxPanel.add(this.thirdDimensionSlider);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.thirdDimensionSlider, -40, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.thirdDimensionSlider, 40, SpringLayout.NORTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.thirdDimensionSlider, -10, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.thirdDimensionSlider, -55, SpringLayout.EAST, this.simulationBoxPanel);
            }
            {
                this.infoLabel = new JLabel();
                this.infoLabel.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.infoLabel.toolTipText")); 
                this.infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                this.infoLabel.setText(GuiMessage.get("CustomPanelCompartmentGraphics.infoLabel.text")); 
                this.infoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                this.simulationBoxPanel.add(this.infoLabel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.infoLabel, -65, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.infoLabel, 10, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.infoLabel, -35, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.infoLabel, -60, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.geometryInfoLabel = new JLabel();
                this.geometryInfoLabel.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.geometryInfoLabel.toolTipText")); 
                this.geometryInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                this.geometryInfoLabel.setText(GuiMessage.get("CustomPanelCompartmentGraphics.geometryInfoLabel.text")); 
                this.geometryInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                this.simulationBoxPanel.add(this.geometryInfoLabel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.geometryInfoLabel, -65, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.geometryInfoLabel, 10, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.geometryInfoLabel, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.geometryInfoLabel, -35, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="boxViewPanel">
        {
            this.boxViewPanel = new JPanel();
            this.boxViewPanel.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.boxViewPanel.toolTipText")); 
            this.boxViewPanelSpringLayout = new SpringLayout();
            this.boxViewPanel.setLayout(this.boxViewPanelSpringLayout);
            this.boxViewPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelCompartmentGraphics.boxViewPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            add(this.boxViewPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.boxViewPanel, 289, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.boxViewPanel, 10, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.boxViewPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.boxViewPanel, -225, SpringLayout.EAST, this);
            {
                this.xzFrontRadioButton = new JRadioButton();
                this.xzFrontRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.xzFrontRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xzFrontRadioButton);
                this.xzFrontRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.xzFrontRadioButton.text")); 
                this.boxViewPanel.add(this.xzFrontRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xzFrontRadioButton, 90, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xzFrontRadioButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xzFrontRadioButton, 25, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xzFrontRadioButton, 5, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xzBackRadioButton = new JRadioButton();
                this.xzBackRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.xzBackRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.xzBackRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.xzBackRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xzBackRadioButton);
                this.xzBackRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.xzBackRadioButton.text")); 
                this.boxViewPanel.add(this.xzBackRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xzBackRadioButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xzBackRadioButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xzBackRadioButton, 25, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xzBackRadioButton, 5, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.yzLeftRadioButton = new JRadioButton();
                this.yzLeftRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.yzLeftRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.yzLeftRadioButton);
                this.yzLeftRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.yzLeftRadioButton.text")); 
                this.boxViewPanel.add(this.yzLeftRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.yzLeftRadioButton, 90, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.yzLeftRadioButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.yzLeftRadioButton, 50, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.yzLeftRadioButton, 30, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.yzRightRadioButton = new JRadioButton();
                this.yzRightRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.yzRightRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.yzRightRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.yzRightRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.yzRightRadioButton);
                this.yzRightRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.yzRightRadioButton.text")); 
                this.boxViewPanel.add(this.yzRightRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.yzRightRadioButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.yzRightRadioButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.yzRightRadioButton, 50, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.yzRightRadioButton, 30, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xyTopRadioButton = new JRadioButton();
                this.xyTopRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.xyTopRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xyTopRadioButton);
                this.xyTopRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.xyTopRadioButton.text")); 
                this.boxViewPanel.add(this.xyTopRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xyTopRadioButton, 90, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xyTopRadioButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xyTopRadioButton, 75, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xyTopRadioButton, 55, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xyBottomRadioButton = new JRadioButton();
                this.xyBottomRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.xyBottomRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.xyBottomRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.xyBottomRadioButton.toolTipText")); 
                this.boxViewButtonGroup.add(this.xyBottomRadioButton);
                this.xyBottomRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.xyBottomRadioButton.text")); 
                this.boxViewPanel.add(this.xyBottomRadioButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xyBottomRadioButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xyBottomRadioButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xyBottomRadioButton, 75, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xyBottomRadioButton, 55, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.boxViewImagePanel = new CustomPanelImage();
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
            this.settingsPanelSpringLayout = new SpringLayout();
            this.settingsPanel.setLayout(this.settingsPanelSpringLayout);
            this.settingsPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelCompartmentGraphics.settingsPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.settingsPanel.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.settingsPanel.toolTipText")); 
            add(this.settingsPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.settingsPanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.settingsPanel, -175, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.settingsPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.settingsPanel, -225, SpringLayout.EAST, this);
            {
                this.valueSlider = new JSlider();
                this.valueSlider.setMajorTickSpacing(2);
                this.valueSlider.setMinorTickSpacing(1);
                this.valueSlider.setMaximum(10);
                this.valueSlider.setPaintTicks(true);
                this.valueSlider.setOrientation(SwingConstants.VERTICAL);
                this.valueSlider.setValue(0);
                this.valueSlider.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.valueSlider.toolTipText")); 
                this.settingsPanel.add(this.valueSlider);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.valueSlider, 75, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.valueSlider, 5, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.valueSlider, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.valueSlider, -50, SpringLayout.EAST, this.settingsPanel);
            }
            {
                this.fogRadioButton = new JRadioButton();
                this.fogRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.fogRadioButton.toolTipText")); 
                configureSettingsButtonGroup.add(fogRadioButton);
                this.fogRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.fogRadioButton.text")); 
                this.settingsPanel.add(this.fogRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fogRadioButton, 145, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fogRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fogRadioButton, 25, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fogRadioButton, 5, SpringLayout.NORTH, this.settingsPanel);
            }
            {
                this.transparencyRadioButton = new JRadioButton();
                this.transparencyRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.transparencyRadioButton.toolTipText")); 
                this.configureSettingsButtonGroup.add(transparencyRadioButton);
                this.transparencyRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.transparencyRadioButton.text")); 
                this.settingsPanel.add(this.transparencyRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.transparencyRadioButton, 145, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.transparencyRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.transparencyRadioButton, 50, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.transparencyRadioButton, 30, SpringLayout.NORTH, this.settingsPanel);
            }
            {
                this.bodyAttenuationRadioButton = new JRadioButton();
                this.bodyAttenuationRadioButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.bodyAttenuationRadioButton.toolTipText")); 
                this.configureSettingsButtonGroup.add(bodyAttenuationRadioButton);
                this.bodyAttenuationRadioButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.bodyAttenuationRadioButton.text")); 
                this.settingsPanel.add(this.bodyAttenuationRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.bodyAttenuationRadioButton, 145, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.bodyAttenuationRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.bodyAttenuationRadioButton, 75, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.bodyAttenuationRadioButton, 55, SpringLayout.NORTH, this.settingsPanel);
            }
            {
                this.settingsButtonPanel = new JPanel();
                this.settingsButtonPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.settingsButtonPanelSpringLayout = new SpringLayout();
                this.settingsButtonPanel.setLayout(this.settingsButtonPanelSpringLayout);
                this.settingsPanel.add(this.settingsButtonPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.settingsButtonPanel, 125, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.settingsButtonPanel, 85, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.settingsButtonPanel, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.settingsButtonPanel, 10, SpringLayout.WEST, this.settingsPanel);
                {
                    this.defaultGraphicsButton = new JButton();
                    this.defaultGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.defaultSettingsButton.toolTipText")); 
                    this.defaultGraphicsButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.defaultSettingsButton.text")); 
                    this.settingsButtonPanel.add(this.defaultGraphicsButton);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.defaultGraphicsButton, 35, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.defaultGraphicsButton, 0, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.defaultGraphicsButton, 90, SpringLayout.WEST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.defaultGraphicsButton, 0, SpringLayout.WEST, this.settingsButtonPanel);
                }
                {
                    this.configureSettingsButton = new JButton();
                    this.configureSettingsButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentGraphics.configureSettingsButton.toolTipText")); 
                    this.configureSettingsButton.setText(GuiMessage.get("CustomPanelCompartmentGraphics.configureSettingsButton.text")); 
                    this.settingsButtonPanel.add(this.configureSettingsButton);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.configureSettingsButton, 35, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.configureSettingsButton, 0, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.configureSettingsButton, 0, SpringLayout.EAST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.configureSettingsButton, -90, SpringLayout.EAST, this.settingsButtonPanel);
                }
            }
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public JLabel getCoordinatesLabel() {
        return coordinatesLabel;
    }

    public JSlider getThirdDimensionSlider() {
        return thirdDimensionSlider;
    }

    public CustomPanelSimulationBoxDraw getSimulationBoxDrawPanel() {
        return this.simulationBoxDrawPanel;
    }

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

    public JLabel getInfoLabel() {
        return infoLabel;
    }

    public JButton getConfigureSettingsButton() {
        return this.configureSettingsButton;
    }

    public JSlider getValueSlider() {
        return valueSlider;
    }

    public JRadioButton getTransparencyRadioButton() {
        return transparencyRadioButton;
    }

    public JRadioButton getFogRadioButton() {
        return fogRadioButton;
    }

    public JRadioButton getBodyAttenuationRadioButton() {
        return bodyAttenuationRadioButton;
    }

    public JButton getDefaultSettingsButton() {
        return this.defaultGraphicsButton;
    }

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    public JLabel getGeometryInfoLabel() {
        return geometryInfoLabel;
    }
    // </editor-fold>

}
