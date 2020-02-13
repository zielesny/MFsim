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
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
public class CustomPanelSimulationBoxSlicer extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JButton boxWaitButton;
    private JButton boxMoveButton;
    private JButton spinAroundXButton;
    private JButton spinAroundYButton;
    private JButton spinAroundZButton;
    private JButton createMovieButton;
    private JButton editMoveAndSpinSettingsButton;
    private JButton redrawButton;
    private JButton viewFullBoxButton;
    private SpringLayout imageInformationPanelSpringLayout;
    private JPanel imageInformationPanel;
    private JButton copyGraphicsButton;
    private JButton saveGraphicsButton;
    private JButton playAnimationButton;
    private JLabel infoLabel;
    private SpringLayout simulationBoxPanelSpringLayout;
    private JPanel simulationBoxPanel;
    private JSlider thirdDimensionSlider;
    private JLabel coordinatesLabel;
    private CustomPanelSlicerImage slicerImagePanel;
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
    static final long serialVersionUID = 1000000000000000047L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelSimulationBoxSlicer() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        // <editor-fold defaultstate="collapsed" desc="simulationBoxPanel">
        {
            this.simulationBoxPanel = new JPanel();
            this.simulationBoxPanel.setBorder(new TitledBorder(
                    new BevelBorder(BevelBorder.RAISED), 
                    GuiMessage.get("CustomPanelSimulationBoxSlicer.simulationBoxPanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, 
                    null, 
                    GuiDefinitions.PANEL_TITLE_COLOR
                )
            );
            this.simulationBoxPanelSpringLayout = new SpringLayout();
            this.simulationBoxPanel.setLayout(this.simulationBoxPanelSpringLayout);
            this.simulationBoxPanel.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.simulationBoxPanel.toolTipText")); 
            this.add(this.simulationBoxPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.simulationBoxPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.simulationBoxPanel, 0, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxPanel, 0, SpringLayout.NORTH, this);
            {
                this.thirdDimensionSlider = new JSlider();
                this.thirdDimensionSlider.setSnapToTicks(true);
                this.thirdDimensionSlider.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.thirdDimensionSlider.toolTipText")); 
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
                this.saveGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.saveGraphicsButton.toolTipText")); 
                this.saveGraphicsButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.saveGraphicsButton.text")); 
                this.simulationBoxPanel.add(this.saveGraphicsButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.saveGraphicsButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.saveGraphicsButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.saveGraphicsButton, 70, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.saveGraphicsButton, 10, SpringLayout.WEST, this.simulationBoxPanel);
            }
            {
                this.copyGraphicsButton = new JButton();
                this.copyGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.copyGraphicsButton.toolTipText")); 
                this.copyGraphicsButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.copyGraphicsButton.text")); 
                this.simulationBoxPanel.add(this.copyGraphicsButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copyGraphicsButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copyGraphicsButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copyGraphicsButton, 130, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copyGraphicsButton, 70, SpringLayout.WEST, this.simulationBoxPanel);
            }
            {
                this.viewFullBoxButton = new JButton();
                this.viewFullBoxButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.viewFullBoxButton.toolTipText")); 
                this.viewFullBoxButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.viewFullBoxButton.text")); 
                this.simulationBoxPanel.add(this.viewFullBoxButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.viewFullBoxButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.viewFullBoxButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.viewFullBoxButton, 190, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.viewFullBoxButton, 130, SpringLayout.WEST, this.simulationBoxPanel);
            }
            {
                this.redrawButton = new JButton();
                this.redrawButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.redrawButton.toolTipText")); 
                this.redrawButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.redrawButton.text")); 
                this.simulationBoxPanel.add(this.redrawButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.redrawButton, 250, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.redrawButton, 190, SpringLayout.WEST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.redrawButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.redrawButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.createMovieButton = new JButton();
                this.createMovieButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.createMovieButton.toolTipText")); 
                this.createMovieButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.createMovieButton.text")); 
                this.simulationBoxPanel.add(this.createMovieButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.createMovieButton, -455, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.createMovieButton, -515, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.createMovieButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.createMovieButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.editMoveAndSpinSettingsButton = new JButton();
                this.editMoveAndSpinSettingsButton.setMnemonic(KeyEvent.VK_E);
                this.editMoveAndSpinSettingsButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.editMoveAndSpinSettingsButton.toolTipText")); 
                this.editMoveAndSpinSettingsButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.editMoveAndSpinSettingsButton.text")); 
                this.simulationBoxPanel.add(this.editMoveAndSpinSettingsButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.editMoveAndSpinSettingsButton, -395, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.editMoveAndSpinSettingsButton, -455, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.editMoveAndSpinSettingsButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.editMoveAndSpinSettingsButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.boxWaitButton = new JButton();
                this.boxWaitButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.boxWaitButton.toolTipText")); 
                this.boxWaitButton.setMnemonic(KeyEvent.VK_W);
                this.boxWaitButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.boxWaitButton.text")); 
                this.simulationBoxPanel.add(this.boxWaitButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.boxWaitButton, -335, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.boxWaitButton, -395, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.boxWaitButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.boxWaitButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.boxMoveButton = new JButton();
                this.boxMoveButton.setMnemonic(KeyEvent.VK_M);
                this.boxMoveButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.boxMoveButton.toolTipText")); 
                this.boxMoveButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.boxMoveButton.text")); 
                this.simulationBoxPanel.add(this.boxMoveButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.boxMoveButton, -275, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.boxMoveButton, -335, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.boxMoveButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.boxMoveButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.spinAroundXButton = new JButton();
                this.spinAroundXButton.setMnemonic(KeyEvent.VK_X);
                this.spinAroundXButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.spinAroundXButton.toolTipText")); 
                this.spinAroundXButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.spinAroundXButton.text")); 
                this.simulationBoxPanel.add(this.spinAroundXButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.spinAroundXButton, -195, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.spinAroundXButton, -255, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.spinAroundXButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.spinAroundXButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.spinAroundYButton = new JButton();
                this.spinAroundYButton.setMnemonic(KeyEvent.VK_Y);
                this.spinAroundYButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.spinAroundYButton.toolTipText")); 
                this.spinAroundYButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.spinAroundYButton.text")); 
                this.simulationBoxPanel.add(this.spinAroundYButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.spinAroundYButton, -135, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.spinAroundYButton, -195, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.spinAroundYButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.spinAroundYButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.spinAroundZButton = new JButton();
                this.spinAroundZButton.setMnemonic(KeyEvent.VK_Z);
                this.spinAroundZButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.spinAroundZButton.toolTipText")); 
                this.spinAroundZButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.spinAroundZButton.text")); 
                this.simulationBoxPanel.add(this.spinAroundZButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.spinAroundZButton, -75, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.spinAroundZButton, -135, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.spinAroundZButton, -10, SpringLayout.SOUTH, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.spinAroundZButton, -45, SpringLayout.SOUTH, this.simulationBoxPanel);
            }
            {
                this.playAnimationButton = new JButton();
                this.playAnimationButton.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText")); 
                this.playAnimationButton.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textPlay")); 
                this.simulationBoxPanel.add(this.playAnimationButton);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.playAnimationButton, -9, SpringLayout.EAST, this.simulationBoxPanel);
                this.simulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.playAnimationButton, -69, SpringLayout.EAST, this.simulationBoxPanel);
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
                    this.slicerImagePanel = new CustomPanelSlicerImage();
                    this.imageInformationPanel.add(this.slicerImagePanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.EAST, this.slicerImagePanel, -10, SpringLayout.EAST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.WEST, this.slicerImagePanel, 10, SpringLayout.WEST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.slicerImagePanel, -35, SpringLayout.SOUTH, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.slicerImagePanel, 35, SpringLayout.NORTH, this.imageInformationPanel);
                }
                {
                    this.coordinatesLabel = new JLabel();
                    this.coordinatesLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    this.coordinatesLabel.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.coordinatesLabel.toolTipText")); 
                    this.coordinatesLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                    this.coordinatesLabel.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.coordinatesLabel.text")); 
                    this.imageInformationPanel.add(this.coordinatesLabel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.EAST, this.coordinatesLabel, -10, SpringLayout.EAST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.WEST, this.coordinatesLabel, 10, SpringLayout.WEST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.coordinatesLabel, 30, SpringLayout.NORTH, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.coordinatesLabel, 5, SpringLayout.NORTH, this.imageInformationPanel);
                }
                {
                    this.infoLabel = new JLabel();
                    this.infoLabel.setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.infoLabel.toolTipText")); 
                    this.infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    this.infoLabel.setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.infoLabel.text")); 
                    this.infoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                    this.imageInformationPanel.add(this.infoLabel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.EAST, this.infoLabel, -10, SpringLayout.EAST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.WEST, this.infoLabel, 10, SpringLayout.WEST, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.infoLabel, -5, SpringLayout.SOUTH, this.imageInformationPanel);
                    this.imageInformationPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.infoLabel, -30, SpringLayout.SOUTH, this.imageInformationPanel);
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

    public CustomPanelSlicerImage getSlicerImagePanel() {
        return this.slicerImagePanel;
    }

    public JLabel getInfoLabel() {
        return infoLabel;
    }

    public JButton getPlayAnimationButton() {
        return playAnimationButton;
    }

    public JButton getSaveGraphicsButton() {
        return saveGraphicsButton;
    }

    public JButton getCopyGraphicsButton() {
        return copyGraphicsButton;
    }

    public JButton getViewFullBoxButton() {
        return viewFullBoxButton;
    }

    public JButton getRedrawButton() {
        return redrawButton;
    }

    public JButton getSpinAroundXButton() {
        return spinAroundXButton;
    }

    public JButton getSpinAroundYButton() {
        return spinAroundYButton;
    }

    public JButton getSpinAroundZButton() {
        return spinAroundZButton;
    }

    public JButton getEditMoveAndSpinSettingsButton() {
        return editMoveAndSpinSettingsButton;
    }

    public JButton getCreateMovieButton() {
        return createMovieButton;
    }

    public JButton getBoxMoveButton() {
        return boxMoveButton;
    }

    public JButton getBoxWaitButton() {
        return boxWaitButton;
    }
    // </editor-fold>

}
