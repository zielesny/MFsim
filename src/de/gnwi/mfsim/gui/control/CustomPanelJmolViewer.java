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
 * Panel for JMol graphics
 *
 * @author Achim Zielesny
 */
public class CustomPanelJmolViewer extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelJmolSimulationBox jmolSimulationBoxPanel;
    private JButton secondSettingsButton;
    private JButton firstSettingsButton;
    private SpringLayout settingsButtonPanelSpringLayout;
    private JPanel settingsButtonPanel;
    private JRadioButton movieRadioButton;
    private JRadioButton animationRadioButton;
    private JRadioButton graphicsRadioButton;
    private JRadioButton moleculesRadioButton;
    private SpringLayout settingsPanelSpringLayout;
    private JPanel settingsPanel;
    private CustomPanelImage boxViewImagePanel;
    private JButton xyBottomButton;
    private JButton xyTopButton;
    private JButton yzRightButton;
    private JButton yzLeftButton;
    private ButtonGroup settingsButtonGroup = new ButtonGroup();
    private JButton xzBackButton;
    private JButton xzFrontButton;
    private SpringLayout boxViewPanelSpringLayout;
    private JPanel boxViewPanel;
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000053L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelJmolViewer() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="simulationBoxPanel">
        {
            this.jmolSimulationBoxPanel = new CustomPanelJmolSimulationBox();
            this.add(this.jmolSimulationBoxPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.jmolSimulationBoxPanel, -220, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.jmolSimulationBoxPanel, 0, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.jmolSimulationBoxPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.jmolSimulationBoxPanel, 0, SpringLayout.NORTH, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="boxViewPanel">
        {
            this.boxViewPanel = new JPanel();
            this.boxViewPanel.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.boxViewPanel.toolTipText")); 
            this.boxViewPanelSpringLayout = new SpringLayout();
            this.boxViewPanel.setLayout(this.boxViewPanelSpringLayout);
            this.boxViewPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelJmolViewer.boxViewPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.add(this.boxViewPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.boxViewPanel, 310, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.boxViewPanel, 0, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.boxViewPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.boxViewPanel, -215, SpringLayout.EAST, this);
            {
                this.xzFrontButton = new JButton();
                this.xzFrontButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.xzFrontButton.toolTipText")); 
                this.xzFrontButton.setText(GuiMessage.get("CustomPanelJmolViewer.xzFrontButton.text")); 
                this.boxViewPanel.add(this.xzFrontButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xzFrontButton, 100, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xzFrontButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xzFrontButton, 40, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xzFrontButton, 5, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xzBackButton = new JButton();
                this.xzBackButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.xzBackButton.toolTipText")); 
                this.xzBackButton.setText(GuiMessage.get("CustomPanelJmolViewer.xzBackButton.text")); 
                this.boxViewPanel.add(this.xzBackButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xzBackButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xzBackButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xzBackButton, 40, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xzBackButton, 5, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.yzLeftButton = new JButton();
                this.yzLeftButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.yzLeftButton.toolTipText")); 
                this.yzLeftButton.setText(GuiMessage.get("CustomPanelJmolViewer.yzLeftButton.text")); 
                this.boxViewPanel.add(this.yzLeftButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.yzLeftButton, 100, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.yzLeftButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.yzLeftButton, 75, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.yzLeftButton, 40, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.yzRightButton = new JButton();
                this.yzRightButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.yzRightButton.toolTipText")); 
                this.yzRightButton.setText(GuiMessage.get("CustomPanelJmolViewer.yzRightButton.text")); 
                this.boxViewPanel.add(this.yzRightButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.yzRightButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.yzRightButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.yzRightButton, 75, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.yzRightButton, 40, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xyTopButton = new JButton();
                this.xyTopButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.xyTopButton.toolTipText")); 
                this.xyTopButton.setText(GuiMessage.get("CustomPanelJmolViewer.xyTopButton.text")); 
                this.boxViewPanel.add(this.xyTopButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xyTopButton, 100, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xyTopButton, 10, SpringLayout.WEST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xyTopButton, 110, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xyTopButton, 75, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.xyBottomButton = new JButton();
                this.xyBottomButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.xyBottomButton.toolTipText")); 
                this.xyBottomButton.setText(GuiMessage.get("CustomPanelJmolViewer.xyBottomButton.text")); 
                this.boxViewPanel.add(this.xyBottomButton);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.xyBottomButton, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.xyBottomButton, -100, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.xyBottomButton, 110, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.xyBottomButton, 75, SpringLayout.NORTH, this.boxViewPanel);
            }
            {
                this.boxViewImagePanel = new CustomPanelImage();
                this.boxViewImagePanel.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.boxViewImagePanel.toolTipText")); 
                this.boxViewImagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.boxViewPanel.add(this.boxViewImagePanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.boxViewImagePanel, 275, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.boxViewImagePanel, 120, SpringLayout.NORTH, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.boxViewImagePanel, -10, SpringLayout.EAST, this.boxViewPanel);
                this.boxViewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.boxViewImagePanel, 10, SpringLayout.WEST, this.boxViewPanel);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="settingsPanel">

        {
            this.settingsPanel = new JPanel();
            this.settingsPanel.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.settingsPanel.toolTipText")); 
            this.settingsPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelJmolViewer.settingsPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.settingsPanelSpringLayout = new SpringLayout();
            this.settingsPanel.setLayout(this.settingsPanelSpringLayout);
            this.add(this.settingsPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.settingsPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.settingsPanel, -135, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.settingsPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.settingsPanel, -215, SpringLayout.EAST, this);
            {
                this.moleculesRadioButton = new JRadioButton();
                this.moleculesRadioButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.moleculesRadioButton.toolTipText")); 
                this.moleculesRadioButton.setText(GuiMessage.get("CustomPanelJmolViewer.moleculesRadioButton.text")); 
                settingsButtonGroup.add(moleculesRadioButton);
                this.settingsPanel.add(this.moleculesRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.moleculesRadioButton, 90, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.moleculesRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.moleculesRadioButton, 25, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.moleculesRadioButton, 5, SpringLayout.NORTH, this.settingsPanel);
            }
            {
                this.graphicsRadioButton = new JRadioButton();
                this.graphicsRadioButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.graphicsRadioButton.toolTipText")); 
                this.graphicsRadioButton.setText(GuiMessage.get("CustomPanelJmolViewer.graphicsRadioButton.text")); 
                settingsButtonGroup.add(graphicsRadioButton);
                this.settingsPanel.add(this.graphicsRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsRadioButton, 90, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsRadioButton, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsRadioButton, 50, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsRadioButton, 30, SpringLayout.NORTH, this.settingsPanel);
            }
            {
                this.animationRadioButton = new JRadioButton();
                this.animationRadioButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.animationRadioButton.toolTipText")); 
                this.animationRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                settingsButtonGroup.add(animationRadioButton);
                this.animationRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.animationRadioButton.setText(GuiMessage.get("CustomPanelJmolViewer.animationRadioButton.text")); 
                this.settingsPanel.add(this.animationRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.animationRadioButton, 25, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.animationRadioButton, 5, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.animationRadioButton, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.animationRadioButton, -100, SpringLayout.EAST, this.settingsPanel);
            }
            {
                this.movieRadioButton = new JRadioButton();
                this.movieRadioButton.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.movieRadioButton.toolTipText")); 
                this.movieRadioButton.setText(GuiMessage.get("CustomPanelJmolViewer.movieRadioButton.text")); 
                this.movieRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.movieRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                settingsButtonGroup.add(movieRadioButton);
                this.settingsPanel.add(this.movieRadioButton);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.movieRadioButton, 50, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.movieRadioButton, 30, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.movieRadioButton, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.movieRadioButton, -100, SpringLayout.EAST, this.settingsPanel);
            }
            {
                this.settingsButtonPanel = new JPanel();
                this.settingsButtonPanel.setToolTipText(GuiMessage.get("CustomPanelJmolViewer.settingsButtonPanel.toolTipText")); 
                this.settingsButtonPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.settingsButtonPanelSpringLayout = new SpringLayout();
                this.settingsButtonPanel.setLayout(this.settingsButtonPanelSpringLayout);
                this.settingsPanel.add(this.settingsButtonPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.settingsButtonPanel, -10, SpringLayout.EAST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.settingsButtonPanel, 10, SpringLayout.WEST, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.settingsButtonPanel, 100, SpringLayout.NORTH, this.settingsPanel);
                this.settingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.settingsButtonPanel, 60, SpringLayout.NORTH, this.settingsPanel);
                {
                    this.firstSettingsButton = new JButton();
                    this.firstSettingsButton.setText(GuiMessage.get("CustomPanelJmolViewer.firstSettingsButton.text")); 
                    this.settingsButtonPanel.add(this.firstSettingsButton);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.firstSettingsButton, 35, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.firstSettingsButton, 0, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.firstSettingsButton, 90, SpringLayout.WEST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.firstSettingsButton, 0, SpringLayout.WEST, this.settingsButtonPanel);
                }
                {
                    this.secondSettingsButton = new JButton();
                    this.secondSettingsButton.setText(GuiMessage.get("CustomPanelJmolViewer.secondSettingsButton.text")); 
                    this.settingsButtonPanel.add(this.secondSettingsButton);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.EAST, this.secondSettingsButton, 0, SpringLayout.EAST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.WEST, this.secondSettingsButton, -90, SpringLayout.EAST, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.secondSettingsButton, 35, SpringLayout.NORTH, this.settingsButtonPanel);
                    this.settingsButtonPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.secondSettingsButton, 0, SpringLayout.NORTH, this.settingsButtonPanel);
                }
            }
        }

        // </editor-fold>

    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * XzFrontButton
     * 
     * @return XzFrontButton
     */
    public JButton getXzFrontButton() {
        return xzFrontButton;
    }

    /**
     * XzBackButton
     * 
     * @return XzBackButton
     */
    public JButton getXzBackButton() {
        return xzBackButton;
    }

    /**
     * YzLeftButton
     * 
     * @return YzLeftButton
     */
    public JButton getYzLeftButton() {
        return yzLeftButton;
    }

    /**
     * YzRightButton
     * 
     * @return YzRightButton
     */
    public JButton getYzRightButton() {
        return yzRightButton;
    }

    /**
     * XyTopButton
     * 
     * @return XyTopButton
     */
    public JButton getXyTopButton() {
        return xyTopButton;
    }

    /**
     * XyBottomButton
     * 
     * @return XyBottomButton
     */
    public JButton getXyBottomButton() {
        return xyBottomButton;
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
     * JmolSimulationBoxPanel
     * 
     * @return JmolSimulationBoxPanel
     */
    public CustomPanelJmolSimulationBox getJmolSimulationBoxPanel() {
        return jmolSimulationBoxPanel;
    }
    // </editor-fold>
}
