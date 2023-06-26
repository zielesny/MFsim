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
import de.gnwi.mfsim.model.jmolViewer.Jmol3dPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Panel for Jmol viewer
 *
 * @author Achim Zielesny
 */
public class CustomPanelJmolSimulationBox extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JCheckBox frameCheckBox;
    /**
     * GUI element
     */
    private JCheckBox spinCheckBox;
    /**
     * GUI element
     */
    private SpringLayout imageInformationPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel imageInformationPanel;
    /**
     * GUI element
     */
    private JButton copyGraphicsButton;
    /**
     * GUI element
     */
    private JButton saveGraphicsButton;
    /**
     * GUI element
     */
    private JButton playAnimationButton;
    /**
     * GUI element
     */
    private SpringLayout simulationBoxPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel simulationBoxPanel;
    /**
     * GUI element
     */
    private JSlider thirdDimensionSlider;
    /**
     * GUI element
     */
    private Jmol3dPanel jmol3dPanel;
    /**
     * GUI element
     */
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000054L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelJmolSimulationBox() {
        super();
        JCheckBox spinCheckBox;
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="simulationBoxPanel">

        {
            this.simulationBoxPanel = new JPanel();
            this.simulationBoxPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelJmolSimulationBox.simulationBoxPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
            this.simulationBoxPanelSpringLayout = new SpringLayout();
            this.simulationBoxPanel.setLayout(this.simulationBoxPanelSpringLayout);
            this.simulationBoxPanel.setToolTipText(GuiMessage.get("CustomPanelJmolSimulationBox.simulationBoxPanel.toolTipText")); 
            add(this.simulationBoxPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.simulationBoxPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.simulationBoxPanel, 0, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxPanel, 0, SpringLayout.NORTH, this);
            {
                this.thirdDimensionSlider = new JSlider();
                this.thirdDimensionSlider.setSnapToTicks(true);
                this.thirdDimensionSlider.setToolTipText(GuiMessage.get("CustomPanelJmolSimulationBox.thirdDimensionSlider.toolTipText")); 
                this.thirdDimensionSlider.setMaximum(1000);
                this.thirdDimensionSlider.setValue(0);
                this.thirdDimensionSlider.setOrientation(SwingConstants.VERTICAL);
                this.thirdDimensionSlider.setPaintTicks(true);
                this.thirdDimensionSlider.setMinorTickSpacing(20);
                this.thirdDimensionSlider.setMajorTickSpacing(100);
                this.simulationBoxPanel.add(this.thirdDimensionSlider);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.thirdDimensionSlider, -55, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.thirdDimensionSlider, 15, SpringLayout.NORTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.thirdDimensionSlider, -15, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.thirdDimensionSlider, -60, SpringLayout.EAST, this.simulationBoxPanel);
            }
            {
                this.saveGraphicsButton = new JButton();
                this.saveGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelJmolSimulationBox.saveGraphicsButton.toolTipText")); 
                this.saveGraphicsButton.setText(GuiMessage.get("CustomPanelJmolSimulationBox.saveGraphicsButton.text")); 
                this.simulationBoxPanel.add(this.saveGraphicsButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.saveGraphicsButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.saveGraphicsButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.saveGraphicsButton, 90, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.saveGraphicsButton, 10, SpringLayout.WEST, this.simulationBoxPanel);
            }
            {
                this.copyGraphicsButton = new JButton();
                this.copyGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelJmolSimulationBox.copyGraphicsButton.toolTipText")); 
                this.copyGraphicsButton.setText(GuiMessage.get("CustomPanelJmolSimulationBox.copyGraphicsButton.text")); 
                this.simulationBoxPanel.add(this.copyGraphicsButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copyGraphicsButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copyGraphicsButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copyGraphicsButton, 170, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copyGraphicsButton, 90, SpringLayout.WEST, this.simulationBoxPanel);
            }
            {
                this.frameCheckBox = new JCheckBox();
                this.frameCheckBox.setToolTipText(GuiMessage.get("CustomPanelJmolSimulationBox.frameCheckBox.toolTipText")); 
                this.frameCheckBox.setText(GuiMessage.get("CustomPanelJmolSimulationBox.frameCheckBox.text")); 
                this.simulationBoxPanel.add(this.frameCheckBox);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.frameCheckBox, -75, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.frameCheckBox, -150, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.frameCheckBox, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.frameCheckBox, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.spinCheckBox = new JCheckBox();
                this.spinCheckBox.setToolTipText(GuiMessage.get("CustomPanelJmolSimulationBox.spinCheckBox.toolTipText")); 
                this.spinCheckBox.setText(GuiMessage.get("CustomPanelJmolSimulationBox.spinCheckBox.text")); 
                this.simulationBoxPanel.add(this.spinCheckBox);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.spinCheckBox, -150, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.spinCheckBox, -220, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.spinCheckBox, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.spinCheckBox, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.playAnimationButton = new JButton();
                this.playAnimationButton.setToolTipText(GuiMessage.get("CustomPanelJmolSimulationBox.playAnimationButton.toolTipText")); 
                this.playAnimationButton.setText(GuiMessage.get("CustomPanelJmolSimulationBox.playAnimationButton.textPlay")); 
                this.simulationBoxPanel.add(this.playAnimationButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.playAnimationButton, -10, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.playAnimationButton, -65, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.playAnimationButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.playAnimationButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.imageInformationPanel = new JPanel();
                this.imageInformationPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.imageInformationPanelSpringLayout = new SpringLayout();
                this.imageInformationPanel.setLayout(this.imageInformationPanelSpringLayout);
                this.simulationBoxPanel.add(this.imageInformationPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.imageInformationPanel, -75, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.imageInformationPanel, 10, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.imageInformationPanel, -50, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.imageInformationPanel, 10, SpringLayout.NORTH, this.simulationBoxPanel);
                {
                    this.jmol3dPanel = new Jmol3dPanel();
                    this.imageInformationPanel.add(this.jmol3dPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jmol3dPanel, -10, SpringLayout.EAST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jmol3dPanel, 10, SpringLayout.WEST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jmol3dPanel, -10, SpringLayout.SOUTH, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jmol3dPanel, 10, SpringLayout.NORTH, this.imageInformationPanel);
                }
            }
        }

        // </editor-fold>

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * ThirdDimensionSlider
     * 
     * @return ThirdDimensionSlider
     */
    public JSlider getThirdDimensionSlider() {
        return thirdDimensionSlider;
    }

    /**
     * Jmol3dPanel
     * 
     * @return Jmol3dPanel
     */
    public Jmol3dPanel getJmol3dPanel() {
        return this.jmol3dPanel;
    }

    /**
     * PlayAnimationButton
     * 
     * @return PlayAnimationButton
     */
    public JButton getPlayAnimationButton() {
        return playAnimationButton;
    }

    /**
     * SaveGraphicsButton
     * 
     * @return SaveGraphicsButton
     */
    public JButton getSaveGraphicsButton() {
        return saveGraphicsButton;
    }

    /**
     * CopyGraphicsButton
     * 
     * @return CopyGraphicsButton
     */
    public JButton getCopyGraphicsButton() {
        return copyGraphicsButton;
    }

    /**
     * FrameCheckBox
     * 
     * @return FrameCheckBox
     */
    public JCheckBox getFrameCheckBox() {
        return frameCheckBox;
    }

    /**
     * SpinCheckBox
     * 
     * @return SpinCheckBox
     */
    public JCheckBox getSpinCheckBox() {
        return spinCheckBox;
    }
    // </editor-fold>
}
