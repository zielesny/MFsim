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
package de.gnwi.mfsim.gui.control;

import de.gnwi.mfsim.gui.message.GuiMessage;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
public class CustomPanelSlicer extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JButton particleShiftRemoveButton;
    /**
     * GUI element
     */
    private JPanel fogSettingsPanel;
    /**
     * GUI element
     */
    private SpringLayout fogSettingsPanelSpringLayout;
    /**
     * GUI element
     */
    private JButton fog5Button;
    /**
     * GUI element
     */
    private JButton fog4Button;
    /**
     * GUI element
     */
    private JButton fog3Button;
    /**
     * GUI element
     */
    private JButton fog2Button;
    /**
     * GUI element
     */
    private JButton fog1Button;
    /**
     * GUI element
     */
    private JButton fog0Button;
    /**
     * GUI element
     */
    private JButton setFirstSliceButton;
    /**
     * GUI element
     */
    private JButton zoomFrequencyDistributionsButton;
    /**
     * GUI element
     */
    private JButton zoomOutButton;
    /**
     * GUI element
     */
    private JButton zoomInBoxVolumeShapeButton;
    /**
     * GUI element
     */
    private JButton zoomInEllipsoidVolumeShapeButton;
    /**
     * GUI element
     */
    private JButton setZoomButton;
    /**
     * GUI element
     */
    private JButton volumeBinsSettingsButton;
    /**
     * GUI element
     */
    private SpringLayout zoomPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel zoomPanel;
    /**
     * GUI element
     */
    private JButton fitMagnificationButton;
    /**
     * GUI element
     */
    private JButton originalDisplayButton;
    /**
     * GUI element
     */
    private JButton boxFrameButton;
    /**
     * GUI element
     */
    private CustomPanelSimulationBoxSlicer simulationBoxPanel;
    /**
     * GUI element
     */
    private JButton secondSettingsButton;
    /**
     * GUI element
     */
    private JButton firstSettingsButton;
    /**
     * GUI element
     */
    private SpringLayout settingsButtonPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel settingsButtonPanel;
    /**
     * GUI element
     */
    private JRadioButton movieRadioButton;
    /**
     * GUI element
     */
    private JRadioButton selectionRadioButton;
    /**
     * GUI element
     */
    private JRadioButton animationRadioButton;
    /**
     * GUI element
     */
    private JRadioButton graphicsRadioButton;
    /**
     * GUI element
     */
    private JRadioButton moleculesRadioButton;
    /**
     * GUI element
     */
    private JRadioButton rotationAndShiftRadioButton;
    /**
     * GUI element
     */
    private ButtonGroup settingsButtonGroup = new ButtonGroup();
    /**
     * GUI element
     */
    private SpringLayout settingsPanelSpringLayout;
    /**
     * GUI element
     */
    private SpringLayout boxSettingsPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel settingsPanel;
    /**
     * GUI element
     */
    private JPanel boxSettingsPanel;
    /**
     * GUI element
     */
    private CustomPanelImage boxViewImagePanel;
    /**
     * GUI element
     */
    private JRadioButton xyBottomRadioButton;
    /**
     * GUI element
     */
    private JRadioButton xyTopRadioButton;
    /**
     * GUI element
     */
    private JRadioButton yzRightRadioButton;
    /**
     * GUI element
     */
    private JRadioButton yzLeftRadioButton;
    /**
     * GUI element
     */
    private ButtonGroup boxViewButtonGroup = new ButtonGroup();
    /**
     * GUI element
     */
    private JRadioButton xzBackRadioButton;
    /**
     * GUI element
     */
    private JRadioButton xzFrontRadioButton;
    /**
     * GUI element
     */
    private SpringLayout boxViewPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel boxViewPanel;
    /**
     * GUI element
     */
    private SpringLayout springLayout;
    /**
     * GUI element
     */
    private JButton noRotationAndShiftButton;
    /**
     * GUI element
     */
    private JButton noMagnificationButton;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000038L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelSlicer() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        int tmpOffset = 2;
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
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.boxViewPanel, 280, SpringLayout.NORTH, this);
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
        // <editor-fold defaultstate="collapsed" desc="particleShiftRemoveButton">
        {
            this.particleShiftRemoveButton = new JButton();
            this.particleShiftRemoveButton.setText(GuiMessage.get("CustomPanelSlicer.particleShiftRemoveButton.text")); 
            this.particleShiftRemoveButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.particleShiftRemoveButton.toolTipText")); 
            this.add(this.particleShiftRemoveButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.particleShiftRemoveButton, 315, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.particleShiftRemoveButton, 280, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.particleShiftRemoveButton, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.particleShiftRemoveButton, -215, SpringLayout.EAST, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="boxSettingsPanel">
        {
            this.boxSettingsPanel = new JPanel();
            this.boxSettingsPanel.setToolTipText(GuiMessage.get("CustomPanelSlicer.boxSettingsPanel.toolTipText"));  
            this.boxSettingsPanel.setBorder(new TitledBorder(
                    new BevelBorder(BevelBorder.RAISED), 
                    GuiMessage.get("CustomPanelSlicer.boxSettingsPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, 
                    null, 
                    GuiDefinitions.PANEL_TITLE_COLOR
                )
            );
            this.boxSettingsPanelSpringLayout = new SpringLayout();
            this.boxSettingsPanel.setLayout(this.boxSettingsPanelSpringLayout);
            this.add(this.boxSettingsPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.boxSettingsPanel, -165, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.boxSettingsPanel, -415, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.boxSettingsPanel, -135, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.boxSettingsPanel, -215, SpringLayout.EAST, this);
            {
                this.setFirstSliceButton = new JButton();
                this.setFirstSliceButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.setFirstSliceButton.toolTipText")); 
                this.setFirstSliceButton.setText(GuiMessage.get("CustomPanelSlicer.setFirstSliceButton.text")); 
                this.boxSettingsPanel.add(this.setFirstSliceButton);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.setFirstSliceButton, -tmpOffset, SpringLayout.EAST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.setFirstSliceButton, tmpOffset, SpringLayout.WEST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.setFirstSliceButton, 5, SpringLayout.NORTH, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.setFirstSliceButton, 40, SpringLayout.NORTH, this.boxSettingsPanel);
            }
            {
                this.noRotationAndShiftButton = new JButton();
                this.noRotationAndShiftButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.noRotationAndShiftButton.toolTipText")); 
                this.noRotationAndShiftButton.setText(GuiMessage.get("CustomPanelSlicer.noRotationAndShiftButton.text")); 
                this.boxSettingsPanel.add(this.noRotationAndShiftButton);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.noRotationAndShiftButton, -tmpOffset, SpringLayout.EAST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.noRotationAndShiftButton, tmpOffset, SpringLayout.WEST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.noRotationAndShiftButton, 40, SpringLayout.NORTH, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.noRotationAndShiftButton, 75, SpringLayout.NORTH, this.boxSettingsPanel);
            }
            {
                this.fitMagnificationButton = new JButton();
                this.fitMagnificationButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.fitMagnificationButton.toolTipText")); 
                this.fitMagnificationButton.setText(GuiMessage.get("CustomPanelSlicer.fitMagnificationButton.text")); 
                this.boxSettingsPanel.add(this.fitMagnificationButton);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fitMagnificationButton, -tmpOffset, SpringLayout.EAST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fitMagnificationButton, tmpOffset, SpringLayout.WEST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fitMagnificationButton, 75, SpringLayout.NORTH, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fitMagnificationButton, 110, SpringLayout.NORTH, this.boxSettingsPanel);
            }
            {
                this.noMagnificationButton = new JButton();
                this.noMagnificationButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.noMagnificationButton.toolTipText")); 
                this.noMagnificationButton.setText(GuiMessage.get("CustomPanelSlicer.noMagnificationButton.text")); 
                this.boxSettingsPanel.add(this.noMagnificationButton);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.noMagnificationButton, -tmpOffset, SpringLayout.EAST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.noMagnificationButton, tmpOffset, SpringLayout.WEST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.noMagnificationButton, 110, SpringLayout.NORTH, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.noMagnificationButton, 145, SpringLayout.NORTH, this.boxSettingsPanel);
            }
            {
                this.originalDisplayButton = new JButton();
                this.originalDisplayButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.originalDisplayButton.toolTipText")); 
                this.originalDisplayButton.setText(GuiMessage.get("CustomPanelSlicer.originalDisplayButton.text")); 
                this.boxSettingsPanel.add(this.originalDisplayButton);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.originalDisplayButton, -tmpOffset, SpringLayout.EAST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.originalDisplayButton, tmpOffset, SpringLayout.WEST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.originalDisplayButton, 145, SpringLayout.NORTH, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.originalDisplayButton, 180, SpringLayout.NORTH, this.boxSettingsPanel);
            }
            {
                this.boxFrameButton = new JButton();
                this.boxFrameButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.boxFrameButton.toolTipText")); 
                this.boxFrameButton.setText(GuiMessage.get("CustomPanelSlicer.boxFrameButton.text")); 
                this.boxSettingsPanel.add(this.boxFrameButton);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.boxFrameButton, -tmpOffset, SpringLayout.EAST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.boxFrameButton, tmpOffset, SpringLayout.WEST, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.boxFrameButton, 180, SpringLayout.NORTH, this.boxSettingsPanel);
                this.boxSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.boxFrameButton, 215, SpringLayout.NORTH, this.boxSettingsPanel);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="fogPanel">
        {
            this.fogSettingsPanel = new JPanel();
            this.fogSettingsPanel.setToolTipText(GuiMessage.get("CustomPanelSlicer.fogSettingsPanel.toolTipText")); 
            this.fogSettingsPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelSlicer.fogSettingsPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.fogSettingsPanelSpringLayout = new SpringLayout();
            this.fogSettingsPanel.setLayout(this.fogSettingsPanelSpringLayout);
            this.add(this.fogSettingsPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.fogSettingsPanel, -165, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.fogSettingsPanel, -415, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.fogSettingsPanel, -80, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.fogSettingsPanel, -135, SpringLayout.EAST, this);
            {
                this.fog0Button = new JButton();
                this.fog0Button.setToolTipText(GuiMessage.get("CustomPanelSlicer.fog0Button.toolTipText")); 
                this.fog0Button.setText(GuiMessage.get("CustomPanelSlicer.fog0Button.text")); 
                this.fogSettingsPanel.add(this.fog0Button);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fog0Button, -tmpOffset, SpringLayout.EAST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fog0Button, tmpOffset, SpringLayout.WEST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fog0Button, 5, SpringLayout.NORTH, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fog0Button, 40, SpringLayout.NORTH, this.fogSettingsPanel);
            }
            {
                this.fog1Button = new JButton();
                this.fog1Button.setToolTipText(GuiMessage.get("CustomPanelSlicer.fog1Button.toolTipText")); 
                this.fog1Button.setText(GuiMessage.get("CustomPanelSlicer.fog1Button.text")); 
                this.fogSettingsPanel.add(this.fog1Button);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fog1Button, -tmpOffset, SpringLayout.EAST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fog1Button, tmpOffset, SpringLayout.WEST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fog1Button, 40, SpringLayout.NORTH, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fog1Button, 75, SpringLayout.NORTH, this.fogSettingsPanel);
            }
            {
                this.fog2Button = new JButton();
                this.fog2Button.setToolTipText(GuiMessage.get("CustomPanelSlicer.fog2Button.toolTipText")); 
                this.fog2Button.setText(GuiMessage.get("CustomPanelSlicer.fog2Button.text")); 
                this.fogSettingsPanel.add(this.fog2Button);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fog2Button, -tmpOffset, SpringLayout.EAST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fog2Button, tmpOffset, SpringLayout.WEST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fog2Button, 75, SpringLayout.NORTH, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fog2Button, 110, SpringLayout.NORTH, this.fogSettingsPanel);
            }
            {
                this.fog3Button = new JButton();
                this.fog3Button.setToolTipText(GuiMessage.get("CustomPanelSlicer.fog3Button.toolTipText")); 
                this.fog3Button.setText(GuiMessage.get("CustomPanelSlicer.fog3Button.text")); 
                this.fogSettingsPanel.add(this.fog3Button);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fog3Button, -tmpOffset, SpringLayout.EAST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fog3Button, tmpOffset, SpringLayout.WEST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fog3Button, 110, SpringLayout.NORTH, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fog3Button, 145, SpringLayout.NORTH, this.fogSettingsPanel);
            }
            {
                this.fog4Button = new JButton();
                this.fog4Button.setToolTipText(GuiMessage.get("CustomPanelSlicer.fog4Button.toolTipText")); 
                this.fog4Button.setText(GuiMessage.get("CustomPanelSlicer.fog4Button.text")); 
                this.fogSettingsPanel.add(this.fog4Button);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fog4Button, -tmpOffset, SpringLayout.EAST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fog4Button, tmpOffset, SpringLayout.WEST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fog4Button, 145, SpringLayout.NORTH, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fog4Button, 180, SpringLayout.NORTH, this.fogSettingsPanel);
            }
            {
                this.fog5Button = new JButton();
                this.fog5Button.setToolTipText(GuiMessage.get("CustomPanelSlicer.fog5Button.toolTipText")); 
                this.fog5Button.setText(GuiMessage.get("CustomPanelSlicer.fog5Button.text")); 
                this.fogSettingsPanel.add(this.fog5Button);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.fog5Button, -tmpOffset, SpringLayout.EAST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.fog5Button, tmpOffset, SpringLayout.WEST, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fog5Button, 180, SpringLayout.NORTH, this.fogSettingsPanel);
                this.fogSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fog5Button, 215, SpringLayout.NORTH, this.fogSettingsPanel);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="zoomPanel">
        {
            this.zoomPanel = new JPanel();
            this.zoomPanel.setToolTipText(GuiMessage.get("CustomPanelSlicer.zoomPanel.toolTipText")); 
            this.zoomPanel.setBorder(
                new TitledBorder(
                    new BevelBorder(BevelBorder.RAISED), 
                    GuiMessage.get("CustomPanelSlicer.zoomPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, 
                    null, 
                    GuiDefinitions.PANEL_TITLE_COLOR
                )
            );
            this.zoomPanelSpringLayout = new SpringLayout();
            this.zoomPanel.setLayout(this.zoomPanelSpringLayout);
            add(this.zoomPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.zoomPanel, -165, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.zoomPanel, -415, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.zoomPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.zoomPanel, -80, SpringLayout.EAST, this);
            {
                this.setZoomButton = new JButton();
                this.setZoomButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.setZoomButton.toolTipText")); 
                this.setZoomButton.setText(GuiMessage.get("CustomPanelSlicer.setZoomButton.text")); 
                this.zoomPanel.add(this.setZoomButton);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.EAST, this.setZoomButton, -tmpOffset, SpringLayout.EAST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.WEST, this.setZoomButton, tmpOffset, SpringLayout.WEST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.setZoomButton, 5, SpringLayout.NORTH, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.setZoomButton, 40, SpringLayout.NORTH, this.zoomPanel);
            }
            {
                this.zoomInBoxVolumeShapeButton = new JButton();
                this.zoomInBoxVolumeShapeButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.zoomInBoxVolumeShapeButton.toolTipText")); 
                this.zoomInBoxVolumeShapeButton.setText(GuiMessage.get("CustomPanelSlicer.zoomInBoxVolumeShapeButton.text")); 
                this.zoomPanel.add(this.zoomInBoxVolumeShapeButton);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.EAST, this.zoomInBoxVolumeShapeButton, -tmpOffset, SpringLayout.EAST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.WEST, this.zoomInBoxVolumeShapeButton, tmpOffset, SpringLayout.WEST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.zoomInBoxVolumeShapeButton, 40, SpringLayout.NORTH, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.zoomInBoxVolumeShapeButton, 75, SpringLayout.NORTH, this.zoomPanel);
            }
            {
                this.zoomInEllipsoidVolumeShapeButton = new JButton();
                this.zoomInEllipsoidVolumeShapeButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.zoomInEllipsoidVolumeShapeButton.toolTipText")); 
                this.zoomInEllipsoidVolumeShapeButton.setText(GuiMessage.get("CustomPanelSlicer.zoomInEllipsoidVolumeShapeButton.text")); 
                this.zoomPanel.add(this.zoomInEllipsoidVolumeShapeButton);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.EAST, this.zoomInEllipsoidVolumeShapeButton, -tmpOffset, SpringLayout.EAST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.WEST, this.zoomInEllipsoidVolumeShapeButton, tmpOffset, SpringLayout.WEST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.zoomInEllipsoidVolumeShapeButton, 75, SpringLayout.NORTH, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.zoomInEllipsoidVolumeShapeButton, 110, SpringLayout.NORTH, this.zoomPanel);
            }
            {
                this.zoomOutButton = new JButton();
                this.zoomOutButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.zoomOutButton.toolTipText")); 
                this.zoomOutButton.setText(GuiMessage.get("CustomPanelSlicer.zoomOutButton.text")); 
                this.zoomPanel.add(this.zoomOutButton);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.EAST, this.zoomOutButton, -tmpOffset, SpringLayout.EAST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.WEST, this.zoomOutButton, tmpOffset, SpringLayout.WEST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.zoomOutButton, 110, SpringLayout.NORTH, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.zoomOutButton, 145, SpringLayout.NORTH, this.zoomPanel);
            }
            {
                this.volumeBinsSettingsButton = new JButton();
                this.volumeBinsSettingsButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.volumeBinsSettingsButton.toolTipText")); 
                this.volumeBinsSettingsButton.setText(GuiMessage.get("CustomPanelSlicer.volumeBinsSettingsButton.text")); 
                this.zoomPanel.add(this.volumeBinsSettingsButton);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.EAST, this.volumeBinsSettingsButton, -tmpOffset, SpringLayout.EAST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.WEST, this.volumeBinsSettingsButton, tmpOffset, SpringLayout.WEST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.volumeBinsSettingsButton, 145, SpringLayout.NORTH, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.volumeBinsSettingsButton, 180, SpringLayout.NORTH, this.zoomPanel);
            }
            {
                this.zoomFrequencyDistributionsButton = new JButton();
                this.zoomFrequencyDistributionsButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.zoomFrequencyDistributionsButton.toolTipText")); 
                this.zoomFrequencyDistributionsButton.setText(GuiMessage.get("CustomPanelSlicer.zoomFrequencyDistributionsButton.text")); 
                this.zoomPanel.add(this.zoomFrequencyDistributionsButton);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.EAST, this.zoomFrequencyDistributionsButton, -tmpOffset, SpringLayout.EAST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.WEST, this.zoomFrequencyDistributionsButton, tmpOffset, SpringLayout.WEST, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.zoomFrequencyDistributionsButton, 180, SpringLayout.NORTH, this.zoomPanel);
                this.zoomPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.zoomFrequencyDistributionsButton, 215, SpringLayout.NORTH, this.zoomPanel);
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
            this.springLayout.putConstraint(SpringLayout.NORTH, this.settingsPanel, -160, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.settingsPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.settingsPanel, -215, SpringLayout.EAST, this);
            {
                this.rotationAndShiftRadioButton = new JRadioButton();
                this.rotationAndShiftRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.rotationRadioButton.toolTipText")); 
                this.settingsButtonGroup.add(rotationAndShiftRadioButton);
                this.rotationAndShiftRadioButton.setText(GuiMessage.get("CustomPanelSlicer.rotationRadioButton.text")); 
                this.settingsPanel.add(this.rotationAndShiftRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.rotationAndShiftRadioButton, 25, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.rotationAndShiftRadioButton, 5, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.rotationAndShiftRadioButton, 100, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.rotationAndShiftRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
            }
            {
                this.moleculesRadioButton = new JRadioButton();
                this.settingsButtonGroup.add(moleculesRadioButton);
                this.moleculesRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.moleculesRadioButton.toolTipText")); 
                this.moleculesRadioButton.setText(GuiMessage.get("CustomPanelSlicer.moleculesRadioButton.text")); 
                this.settingsPanel.add(this.moleculesRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.moleculesRadioButton, 50, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.moleculesRadioButton, 30, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.moleculesRadioButton, 90, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.moleculesRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
            }
            {
                this.graphicsRadioButton = new JRadioButton();
                this.settingsButtonGroup.add(graphicsRadioButton);
                this.graphicsRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.graphicsRadioButton.toolTipText")); 
                this.graphicsRadioButton.setText(GuiMessage.get("CustomPanelSlicer.graphicsRadioButton.text")); 
                this.settingsPanel.add(this.graphicsRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsRadioButton, 75, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsRadioButton, 55, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsRadioButton, 90, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
            }
            {
                this.animationRadioButton = new JRadioButton();
                this.settingsButtonGroup.add(animationRadioButton);
                this.animationRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.animationRadioButton.toolTipText")); 
                this.animationRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.animationRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.animationRadioButton.setText(GuiMessage.get("CustomPanelSlicer.animationRadioButton.text")); 
                this.settingsPanel.add(this.animationRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.animationRadioButton, 25, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.animationRadioButton, 5, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.animationRadioButton, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.animationRadioButton, -100, SpringLayout.EAST, this.settingsPanel);
            }
            {
                this.movieRadioButton = new JRadioButton();
                this.settingsButtonGroup.add(movieRadioButton);
                this.movieRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.movieRadioButton.toolTipText")); 
                this.movieRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.movieRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.movieRadioButton.setText(GuiMessage.get("CustomPanelSlicer.movieRadioButton.text")); 
                this.settingsPanel.add(this.movieRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.movieRadioButton, 50, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.movieRadioButton, 30, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.movieRadioButton, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.movieRadioButton, -100, SpringLayout.EAST, this.settingsPanel);
            }
            {
                this.selectionRadioButton = new JRadioButton();
                this.settingsButtonGroup.add(selectionRadioButton);
                this.selectionRadioButton.setToolTipText(GuiMessage.get("CustomPanelSlicer.selectionRadioButton.toolTipText")); 
                this.selectionRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.selectionRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.selectionRadioButton.setText(GuiMessage.get("CustomPanelSlicer.selectionRadioButton.text")); 
                this.settingsPanel.add(this.selectionRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectionRadioButton, 75, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectionRadioButton, 55, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectionRadioButton, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectionRadioButton, -100, SpringLayout.EAST, this.settingsPanel);
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
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.settingsButtonPanel, 125, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.settingsButtonPanel, 85, SpringLayout.NORTH, this.settingsPanel);
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
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * XzFrontRadioButton
     * 
     * @return XzFrontRadioButton
     */
    public JRadioButton getXzFrontRadioButton() {
        return xzFrontRadioButton;
    }

    /**
     * XzBackRadioButton
     * 
     * @return XzBackRadioButton
     */
    public JRadioButton getXzBackRadioButton() {
        return xzBackRadioButton;
    }

    /**
     * YzLeftRadioButton
     * 
     * @return YzLeftRadioButton
     */
    public JRadioButton getYzLeftRadioButton() {
        return yzLeftRadioButton;
    }

    /**
     * YzRightRadioButton
     * 
     * @return YzRightRadioButton
     */
    public JRadioButton getYzRightRadioButton() {
        return yzRightRadioButton;
    }

    /**
     * XyTopRadioButton
     * 
     * @return XyTopRadioButton
     */
    public JRadioButton getXyTopRadioButton() {
        return xyTopRadioButton;
    }

    /**
     * XyBottomRadioButton
     * 
     * @return XyBottomRadioButton
     */
    public JRadioButton getXyBottomRadioButton() {
        return xyBottomRadioButton;
    }

    /**
     * BoxViewImagePanel
     * 
     * @return BoxViewImagePanel
     */
    public CustomPanelImage getBoxViewImagePanel() {
        return boxViewImagePanel;
    }

    /**
     * RotationAndShiftRadioButton
     * 
     * @return RotationAndShiftRadioButton
     */
    public JRadioButton getRotationAndShiftRadioButton() {
        return rotationAndShiftRadioButton;
    }

    /**
     * MoleculesRadioButton
     * 
     * @return MoleculesRadioButton
     */
    public JRadioButton getMoleculesRadioButton() {
        return moleculesRadioButton;
    }

    /**
     * GraphicsRadioButton
     * 
     * @return GraphicsRadioButton
     */
    public JRadioButton getGraphicsRadioButton() {
        return graphicsRadioButton;
    }

    /**
     * AnimationRadioButton
     * 
     * @return AnimationRadioButton
     */
    public JRadioButton getAnimationRadioButton() {
        return animationRadioButton;
    }

    /**
     * MovieRadioButton
     * 
     * @return MovieRadioButton
     */
    public JRadioButton getMovieRadioButton() {
        return movieRadioButton;
    }

    /**
     * SelectionRadioButton
     * 
     * @return SelectionRadioButton
     */
    public JRadioButton getSelectionRadioButton() {
        return selectionRadioButton;
    }

    /**
     * FirstSettingsButton
     * 
     * @return FirstSettingsButton
     */
    public JButton getFirstSettingsButton() {
        return firstSettingsButton;
    }

    /**
     * SecondSettingsButton
     * 
     * @return SecondSettingsButton
     */
    public JButton getSecondSettingsButton() {
        return secondSettingsButton;
    }

    /**
     * BoxViewPanel
     * 
     * @return BoxViewPanel
     */
    public JPanel getBoxViewPanel() {
        return boxViewPanel;
    }

    /**
     * SettingsPanel
     * 
     * @return SettingsPanel
     */
    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    /**
     * SimulationBoxPanel
     * 
     * @return SimulationBoxPanel
     */
    public CustomPanelSimulationBoxSlicer getSimulationBoxPanel() {
        return simulationBoxPanel;
    }

    /**
     * BoxSettingsPanel
     * 
     * @return BoxSettingsPanel
     */
    public JPanel getBoxSettingsPanel() {
        return boxSettingsPanel;
    }

    /**
     * FogSettingsPanel
     * 
     * @return FogSettingsPanel
     */
    public JPanel getFogSettingsPanel() {
        return fogSettingsPanel;
    }

    /**
     * NoRotationAndShiftButton
     * 
     * @return NoRotationAndShiftButton
     */
    public JButton getNoRotationAndShiftButton() {
        return noRotationAndShiftButton;
    }

    /**
     * SetFirstSliceButton
     * 
     * @return SetFirstSliceButton
     */
    public JButton getSetFirstSliceButton() {
        return setFirstSliceButton;
    }

    /**
     * NoMagnificationButton
     * 
     * @return NoMagnificationButton
     */
    public JButton getNoMagnificationButton() {
        return noMagnificationButton;
    }

    /**
     * OriginalDisplayButton
     * 
     * @return OriginalDisplayButton
     */
    public JButton getOriginalDisplayButton() {
        return originalDisplayButton;
    }

    /**
     * BoxFrameButton
     * 
     * @return BoxFrameButton
     */
    public JButton getBoxFrameButton() {
        return boxFrameButton;
    }

    /**
     * ZoomPanel
     * 
     * @return ZoomPanel
     */
    public JPanel getZoomPanel() {
        return zoomPanel;
    }

    /**
     * SetZoomButton
     * 
     * @return SetZoomButton
     */
    public JButton getSetZoomButton() {
        return setZoomButton;
    }

    /**
     * ZoomInBoxVolumeShapeButton
     * 
     * @return ZoomInBoxVolumeShapeButton
     */
    public JButton getZoomInBoxVolumeShapeButton() {
        return zoomInBoxVolumeShapeButton;
    }

    /**
     * ZoomInEllipsoidVolumeShapeButton
     * 
     * @return ZoomInEllipsoidVolumeShapeButton
     */
    public JButton getZoomInEllipsoidVolumeShapeButton() {
        return zoomInEllipsoidVolumeShapeButton;
    }

    /**
     * ZoomOutButton
     * 
     * @return ZoomOutButton
     */
    public JButton getZoomOutButton() {
        return zoomOutButton;
    }

    /**
     * VolumeBinsSettingsButton
     * 
     * @return VolumeBinsSettingsButton
     */
    public JButton getVolumeBinsSettingsButton() {
        return volumeBinsSettingsButton;
    }

    /**
     * ZoomFrequencyDistributionsButton
     * 
     * @return ZoomFrequencyDistributionsButton
     */
    public JButton getZoomFrequencyDistributionsButton() {
        return zoomFrequencyDistributionsButton;
    }

    /**
     * FitMagnificationButton
     * 
     * @return FitMagnificationButton
     */
    public JButton getFitMagnificationButton() {
        return fitMagnificationButton;
    }

    /**
     * Fog0Button
     * 
     * @return Fog0Button
     */
    public JButton getFog0Button() {
        return fog0Button;
    }

    /**
     * Fog1Button
     * 
     * @return Fog1Button
     */
    public JButton getFog1Button() {
        return fog1Button;
    }

    /**
     * Fog2Button
     * 
     * @return Fog2Button
     */
    public JButton getFog2Button() {
        return fog2Button;
    }

    /**
     * Fog3Button
     * 
     * @return Fog3Button
     */
    public JButton getFog3Button() {
        return fog3Button;
    }

    /**
     * Fog4Button
     * 
     * @return Fog4Button
     */
    public JButton getFog4Button() {
        return fog4Button;
    }

    /**
     * Fog5Button
     * 
     * @return Fog5Button
     */
    public JButton getFog5Button() {
        return fog5Button;
    }

    /**
     * ParticleShiftRemoveButton
     * 
     * @return ParticleShiftRemoveButton
     */
    public JButton getParticleShiftRemoveButton() {
        return particleShiftRemoveButton;
    }
    // </editor-fold>

}
