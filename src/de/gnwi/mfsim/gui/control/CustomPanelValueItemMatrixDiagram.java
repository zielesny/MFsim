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
import javax.swing.border.BevelBorder;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * A panel with an image panel that displays a value item matrix as a diagram 
 * and various different controll elements for manipulating the diagram
 * 
 * @author Jan-Mathis Hein, Achim Zielesny
 */
public class CustomPanelValueItemMatrixDiagram extends JPanel{

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private SpringLayout springLayout;

    private JSlider domainSlider;
    
    private JButton playAnimationButton;
    
    private JRadioButton chartSettingsRadioButton;
    
    private JRadioButton movieSettingsRadioButton;
    
    private ButtonGroup changeSettingsButtonGroup = new ButtonGroup();

    private JPanel imageAndInformationPanel;

    private SpringLayout imageAndInformationPanelSpringLayout;
    
    private CustomPanelImage imagePanel;
    
    private JLabel informationLabel;

    private JPanel chartSettingsPanel;
    
    private SpringLayout chartSettingsPanelSpringLayout;
    
    private JButton restoreButton;

    private JButton zoomButton;

    private JButton discardButton;
    
    private JButton defaultButton;

    private JButton copyGraphicsButton;

    private JButton saveGraphicsButton;

    private JButton resetAverageButton;
    
    private JCheckBox outlineCheckBox;

    private JCheckBox whiteCheckBox;

    private JCheckBox shapesCheckBox;

    private JCheckBox thickCheckBox;
    
    private JCheckBox invertedCheckBox;
    
    private JCheckBox trendLineCheckBox;
    
    private JCheckBox markLastPointCheckBox;
    
    private JComboBox averageComboBox;

    private JPanel movieSettingsPanel;
    
    private SpringLayout movieSettingsPanelSpringLayout;
    
    private JButton createMovieButton;
    
    private JCheckBox reducedStatisticsCheckBox;
    
    private JCheckBox reducedTrendLineCheckBox;
    
    private JRadioButton animationRadioButton;
    
    private JRadioButton movieRadioButton;
    
    private ButtonGroup movieAnimationButtonGroup = new ButtonGroup();
    
    private JButton firstMovieSettingsButton;
    
    private JButton secondMovieSettingsButton;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000067L;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelValueItemMatrixDiagram() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        // NOTE: Distances between components are incremented by two to make
        // up for the use of bevel borders in contrast to titled borders
        // <editor-fold defaultstate="collapsed" desc="chartSettingsPanel">
        {
            this.chartSettingsPanel = new JPanel();
            this.chartSettingsPanelSpringLayout = new SpringLayout();
            this.chartSettingsPanel.setLayout(this.chartSettingsPanelSpringLayout);
            this.chartSettingsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.add(this.chartSettingsPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.chartSettingsPanel, -12, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.chartSettingsPanel, 82, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.chartSettingsPanel, -12, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.chartSettingsPanel, -96, SpringLayout.SOUTH, this);
            {
                this.saveGraphicsButton = new JButton();
                this.saveGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.saveGraphicsButton.toolTipText")); 
                this.saveGraphicsButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.saveGraphicsButton.text")); 
                this.chartSettingsPanel.add(this.saveGraphicsButton);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.saveGraphicsButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.saveGraphicsButton, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.saveGraphicsButton, 85, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.saveGraphicsButton, 5, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.copyGraphicsButton = new JButton();
                this.copyGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.copyGraphicsButton.toolTipText")); 
                this.copyGraphicsButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.copyGraphicsButton.text")); 
                this.chartSettingsPanel.add(this.copyGraphicsButton);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copyGraphicsButton, -5, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copyGraphicsButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copyGraphicsButton, 85, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copyGraphicsButton, 5, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.thickCheckBox = new JCheckBox();
                this.thickCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.thickCheckBox.toolTipText")); 
                this.thickCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.thickCheckBox.text")); 
                this.chartSettingsPanel.add(this.thickCheckBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.thickCheckBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.thickCheckBox, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.thickCheckBox, 175, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.thickCheckBox, 105, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.shapesCheckBox = new JCheckBox();
                this.shapesCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.shapesCheckBox.toolTipText")); 
                this.shapesCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.shapesCheckBox.text")); 
                this.chartSettingsPanel.add(this.shapesCheckBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.shapesCheckBox, 245, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.shapesCheckBox, 175, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.shapesCheckBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.shapesCheckBox, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
            }
            {
                this.whiteCheckBox = new JCheckBox();
                this.whiteCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.whiteCheckBox.toolTipText"));
                this.whiteCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.whiteCheckBox.text")); 
                this.chartSettingsPanel.add(this.whiteCheckBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.whiteCheckBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.whiteCheckBox, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.whiteCheckBox, 315, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.whiteCheckBox, 245, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.outlineCheckBox = new JCheckBox();
                this.outlineCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.outlineCheckBox.toolTipText")); 
                this.outlineCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.outlineCheckBox.text")); 
                this.chartSettingsPanel.add(this.outlineCheckBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.outlineCheckBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.outlineCheckBox, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.outlineCheckBox, 385, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.outlineCheckBox, 315, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.invertedCheckBox = new JCheckBox();
                this.invertedCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.invertedCheckBox.toolTipText")); 
                this.invertedCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.invertedCheckBox.text")); 
                this.chartSettingsPanel.add(this.invertedCheckBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.invertedCheckBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.invertedCheckBox, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.invertedCheckBox, 455, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.invertedCheckBox, 385, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.trendLineCheckBox = new JCheckBox();
                this.trendLineCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.trendLineCheckBox.toolTipText"));
                this.trendLineCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.trendLineCheckBox.text"));
                this.chartSettingsPanel.add(this.trendLineCheckBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.trendLineCheckBox, -5, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.trendLineCheckBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.trendLineCheckBox, 175, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.trendLineCheckBox, 105, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.markLastPointCheckBox = new JCheckBox();
                this.markLastPointCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.markLastPointCheckBox.toolTipText"));
                this.markLastPointCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.markLastPointCheckBox.text"));
                this.chartSettingsPanel.add(this.markLastPointCheckBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.markLastPointCheckBox, -5, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.markLastPointCheckBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.markLastPointCheckBox, 245, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.markLastPointCheckBox, 175, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.averageComboBox = new JComboBox();
                this.averageComboBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.averageComboBox.toolTipText")); 
                this.chartSettingsPanel.add(this.averageComboBox);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.averageComboBox, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.averageComboBox, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.averageComboBox, 555, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.averageComboBox, 475, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.resetAverageButton = new JButton();
                this.resetAverageButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.resetAverageButton.toolTipText")); 
                this.resetAverageButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.resetAverageButton.text")); 
                this.chartSettingsPanel.add(this.resetAverageButton);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.resetAverageButton, -5, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.resetAverageButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.resetAverageButton, 555, SpringLayout.WEST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.resetAverageButton, 475, SpringLayout.WEST, this.chartSettingsPanel);
            }
            {
                this.discardButton = new JButton();
                this.discardButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.discardButton.toolTipText")); 
                this.discardButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.discardButton.text")); 
                this.chartSettingsPanel.add(this.discardButton);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.discardButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.discardButton, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.discardButton, -5, SpringLayout.EAST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.discardButton, -85, SpringLayout.EAST, this.chartSettingsPanel);
            }
            {
                this.zoomButton = new JButton();
                this.zoomButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.zoomButton.toolTipText")); 
                this.zoomButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.zoomButton.text")); 
                this.chartSettingsPanel.add(this.zoomButton);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.zoomButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.zoomButton, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.zoomButton, -85, SpringLayout.EAST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.zoomButton, -165, SpringLayout.EAST, this.chartSettingsPanel);
            }
            {
                this.restoreButton = new JButton();
                this.restoreButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.restoreButton.toolTipText")); 
                this.restoreButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.restoreButton.text")); 
                this.chartSettingsPanel.add(this.restoreButton);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.restoreButton, -5, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.restoreButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.restoreButton, -85, SpringLayout.EAST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.restoreButton, -165, SpringLayout.EAST, this.chartSettingsPanel);
            }
            {
                this.defaultButton = new JButton();
                this.defaultButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.defaultButton.toolTipText"));
                this.defaultButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.defaultButton.Text"));
                this.chartSettingsPanel.add(this.defaultButton);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.defaultButton, -5, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.defaultButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.defaultButton, -5, SpringLayout.EAST, this.chartSettingsPanel);
                this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.defaultButton, -85, SpringLayout.EAST, this.chartSettingsPanel);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="movieSettingsPanel">
        {
            this.movieSettingsPanel = new JPanel();
            this.movieSettingsPanelSpringLayout = new SpringLayout();
            this.movieSettingsPanel.setLayout(this.movieSettingsPanelSpringLayout);
            this.movieSettingsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.add(this.movieSettingsPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.movieSettingsPanel, -12, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.movieSettingsPanel, 82, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.movieSettingsPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.movieSettingsPanel, 0, SpringLayout.SOUTH, this);
            {
                this.reducedTrendLineCheckBox = new JCheckBox();
                this.reducedTrendLineCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.reducedTrendLineCheckBox.toolTipText"));
                this.reducedTrendLineCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.reducedTrendLineCheckBox.text"));
                this.movieSettingsPanel.add(this.reducedTrendLineCheckBox);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.reducedTrendLineCheckBox, -5, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.reducedTrendLineCheckBox, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.reducedTrendLineCheckBox, 270, SpringLayout.WEST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.reducedTrendLineCheckBox, 105, SpringLayout.WEST, this.movieSettingsPanel);
            }
            {
                this.reducedStatisticsCheckBox = new JCheckBox();
                this.reducedStatisticsCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.reducedStatisticsCheckBox.toolTipText"));
                this.reducedStatisticsCheckBox.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.reducedStatisticsCheckBox.text"));
                this.movieSettingsPanel.add(this.reducedStatisticsCheckBox);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.reducedStatisticsCheckBox, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.reducedStatisticsCheckBox, -75, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.reducedStatisticsCheckBox, 270, SpringLayout.WEST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.reducedStatisticsCheckBox, 105, SpringLayout.WEST, this.movieSettingsPanel);
            }
            {
                this.movieRadioButton = new JRadioButton();
                this.movieRadioButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.movieRadioButton.toolTipText"));
                this.movieRadioButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.movieRadioButton.text"));
                this.movieRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
                this.movieRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                this.movieAnimationButtonGroup.add(this.movieRadioButton);
                this.movieSettingsPanel.add(this.movieRadioButton);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.movieRadioButton, -5, SpringLayout.EAST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.movieRadioButton, -95, SpringLayout.EAST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.movieRadioButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.movieRadioButton, -75, SpringLayout.SOUTH, this.movieSettingsPanel);
            }
            {
                this.animationRadioButton = new JRadioButton();
                this.animationRadioButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.animationRadioButton.toolTipText"));
                this.animationRadioButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.animationRadioButton.text"));
                this.movieAnimationButtonGroup.add(this.animationRadioButton);
                this.movieSettingsPanel.add(this.animationRadioButton);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.animationRadioButton, -95, SpringLayout.EAST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.animationRadioButton, -185, SpringLayout.EAST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.animationRadioButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.animationRadioButton, -75, SpringLayout.SOUTH, this.movieSettingsPanel);
            }
            {
                this.createMovieButton = new JButton();
                this.createMovieButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.createMovieButton.toolTipText"));
                this.createMovieButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.createMovieButton.text"));
                this.movieSettingsPanel.add(this.createMovieButton);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.createMovieButton, -5, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.createMovieButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.createMovieButton, -205, SpringLayout.EAST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.createMovieButton, -295, SpringLayout.EAST, this.movieSettingsPanel);
            }
            {
                this.firstMovieSettingsButton = new JButton();
                this.firstMovieSettingsButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.toolTipText1"));
                this.firstMovieSettingsButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.text1"));
                this.movieSettingsPanel.add(this.firstMovieSettingsButton);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.firstMovieSettingsButton, -5, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.firstMovieSettingsButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.firstMovieSettingsButton, -95, SpringLayout.EAST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.firstMovieSettingsButton, -185, SpringLayout.EAST, this.movieSettingsPanel);
            }
            {
                this.secondMovieSettingsButton = new JButton();
                this.secondMovieSettingsButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.toolTipText1"));
                this.secondMovieSettingsButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.text1"));
                this.movieSettingsPanel.add(this.secondMovieSettingsButton);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.secondMovieSettingsButton, -5, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.secondMovieSettingsButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.secondMovieSettingsButton, -5, SpringLayout.EAST, this.movieSettingsPanel);
                this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.secondMovieSettingsButton, -95, SpringLayout.EAST, this.movieSettingsPanel);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="imageAndInformationPanel">
        {
            this.imageAndInformationPanel = new JPanel();
            this.imageAndInformationPanelSpringLayout = new SpringLayout();
            this.imageAndInformationPanel.setLayout(this.imageAndInformationPanelSpringLayout);
            this.imageAndInformationPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.add(this.imageAndInformationPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.imageAndInformationPanel, -12, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.imageAndInformationPanel, 12, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.imageAndInformationPanel, -108, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.imageAndInformationPanel, 12, SpringLayout.NORTH, this);
            {
                this.imagePanel = new CustomPanelImage();
                this.imageAndInformationPanel.add(this.imagePanel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.imagePanel, -35, SpringLayout.SOUTH, this.imageAndInformationPanel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.imagePanel, 0, SpringLayout.NORTH, this.imageAndInformationPanel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.EAST, this.imagePanel, 0, SpringLayout.EAST, this.imageAndInformationPanel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.WEST, this.imagePanel, 0, SpringLayout.WEST, this.imageAndInformationPanel);
            }
            {
                this.informationLabel = new JLabel();
                this.informationLabel.setOpaque(true);
                this.informationLabel.setHorizontalAlignment(SwingConstants.CENTER);
                this.informationLabel.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.informationLabel.text"));
                this.informationLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                this.imageAndInformationPanel.add(this.informationLabel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.informationLabel, -5, SpringLayout.SOUTH, this.imageAndInformationPanel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.informationLabel, -30, SpringLayout.SOUTH, this.imageAndInformationPanel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.EAST, this.informationLabel, 0, SpringLayout.EAST, this.imageAndInformationPanel);
                this.imageAndInformationPanelSpringLayout.putConstraint(SpringLayout.WEST, this.informationLabel, 0, SpringLayout.WEST, this.imageAndInformationPanel);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="domainSlider">
        {
            this.domainSlider = new JSlider(JSlider.VERTICAL);
            this.domainSlider.setSnapToTicks(true);
            this.domainSlider.setPaintTicks(true);
            this.domainSlider.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.domainSlider.toolTipText"));
            this.add(this.domainSlider);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.domainSlider, -153, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.domainSlider, 17, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.domainSlider, -0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.domainSlider, -0, SpringLayout.EAST, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="playAnimationButton">
        {
            this.playAnimationButton = new JButton();
            this.playAnimationButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.toolTipText1"));
            this.playAnimationButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.text1"));
            this.add(this.playAnimationButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.playAnimationButton, -108, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.playAnimationButton, -143, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.playAnimationButton, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.playAnimationButton, 0, SpringLayout.EAST, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="chartSettingsRadioButton">
        {
            this.chartSettingsRadioButton = new JRadioButton();
            this.chartSettingsRadioButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstSettingsRadioButton.toolTipText"));
            this.chartSettingsRadioButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstSettingsRadioButton.text"));
            this.changeSettingsButtonGroup.add(this.chartSettingsRadioButton);
            this.add(this.chartSettingsRadioButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.chartSettingsRadioButton, -54, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.chartSettingsRadioButton, -89, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.chartSettingsRadioButton, 72, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.chartSettingsRadioButton, 12, SpringLayout.WEST, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="movieSettingsRadioButton">
        {
            this.movieSettingsRadioButton = new JRadioButton();
            this.movieSettingsRadioButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondSettingsRadioButton.toolTipText"));
            this.movieSettingsRadioButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondSettingsRadioButton.text"));
            this.changeSettingsButtonGroup.add(this.movieSettingsRadioButton);
            this.add(this.movieSettingsRadioButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.movieSettingsRadioButton, -19, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.movieSettingsRadioButton, -54, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.movieSettingsRadioButton, 72, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.movieSettingsRadioButton, 12, SpringLayout.WEST, this);
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * ImagePanel
     * 
     * @return ImagePanel
     */
    public CustomPanelImage getImagePanel() {
        return this.imagePanel;
    }

    /**
     * CopyGraphicsButton
     * 
     * @return CopyGraphicsButton
     */
    public JButton getCopyGraphicsButton() {
        return this.copyGraphicsButton;
    }
    
    /**
     * CreatMovieButton
     * 
     * @return CreatMovieButton
     */
    public JButton getCreatMovieButton() {
        return this.createMovieButton;
    }

    /**
     * DefaultButton
     * 
     * @return DefaultButton
     */
    public JButton getDefaultButton() {
        return this.defaultButton;
    }

    /**
     * DiscardButton
     * 
     * @return DiscardButton
     */
    public JButton getDiscardButton() {
        return this.discardButton;
    }

    /**
     * FirstMovieSettingsButton
     * 
     * @return FirstMovieSettingsButton
     */
    public JButton getFirstMovieSettingsButton() {
        return this.firstMovieSettingsButton;
    }

    /**
     * PlayAnimationButton
     * 
     * @return PlayAnimationButton
     */
    public JButton getPlayAnimationButton() {
        return this.playAnimationButton;
    }

    /**
     * ResetAverageButton
     * 
     * @return ResetAverageButton
     */
    public JButton getResetAverageButton() {
        return this.resetAverageButton;
    }

    /**
     * SaveGraphicsButton
     * 
     * @return SaveGraphicsButton
     */
    public JButton getSaveGraphicsButton() {
        return this.saveGraphicsButton;
    }
    
    /**
     * SecondMovieSettingsButton
     * 
     * @return SecondMovieSettingsButton
     */
    public JButton getSecondMovieSettingsButton() {
        return this.secondMovieSettingsButton;
    }

    /**
     * ZoomButton
     * 
     * @return ZoomButton
     */
    public JButton getZoomButton() {
        return this.zoomButton;
    }

    /**
     * RestoreButton
     * 
     * @return RestoreButton
     */
    public JButton getRestoreButton() {
        return this.restoreButton;
    }

    /**
     * InvertedCheckBox
     * 
     * @return InvertedCheckBox
     */
    public JCheckBox getInvertedCheckBox() {
        return this.invertedCheckBox;
    }

    /**
     * MarkLastPointCheckBox
     * 
     * @return MarkLastPointCheckBox
     */
    public JCheckBox getMarkLastPointCheckBox() {
        return this.markLastPointCheckBox;
    }

    /**
     * ReducedTrendLineCheckBox
     * 
     * @return ReducedTrendLineCheckBox
     */
    public JCheckBox getReducedTrendLineCheckBox() {
        return this.reducedTrendLineCheckBox;
    }

    /**
     * OutlineCheckBox
     * 
     * @return OutlineCheckBox
     */
    public JCheckBox getOutlineCheckBox() {
        return this.outlineCheckBox;
    }

    /**
     * ShapesCheckBox
     * 
     * @return ShapesCheckBox
     */
    public JCheckBox getShapesCheckBox() {
        return this.shapesCheckBox;
    }

    /**
     * ThickCheckBox
     * 
     * @return ThickCheckBox
     */
    public JCheckBox getThickCheckBox() {
        return this.thickCheckBox;
    }

    /**
     * TrendLineCheckBox
     * 
     * @return TrendLineCheckBox
     */
    public JCheckBox getTrendLineCheckBox() {
        return this.trendLineCheckBox;
    }

    /**
     * ReducedStatisticsCheckBox
     * 
     * @return ReducedStatisticsCheckBox
     */
    public JCheckBox getReducedStatisticsCheckBox() {
        return this.reducedStatisticsCheckBox;
    }

    /**
     * WhiteCheckBox
     * 
     * @return WhiteCheckBox
     */
    public JCheckBox getWhiteCheckBox() {
        return this.whiteCheckBox;
    }

    /**
     * AverageComboBox
     * 
     * @return AverageComboBox
     */
    public JComboBox getAverageComboBox() {
        return this.averageComboBox;
    }

    /**
     * InformationLabel
     * 
     * @return InformationLabel
     */
    public JLabel getInformationLabel() {
        return this.informationLabel;
    }

    /**
     * ChartSettingsPanel
     * 
     * @return ChartSettingsPanel
     */
    public JPanel getChartSettingsPanel() {
        return this.chartSettingsPanel;
    }

    /**
     * AnimationRadioButton
     * 
     * @return AnimationRadioButton
     */
    public JRadioButton getAnimationRadioButton() {
        return this.animationRadioButton;
    }

    /**
     * ChartSettingsRadioButton
     * 
     * @return ChartSettingsRadioButton
     */
    public JRadioButton getChartSettingsRadioButton() {
        return this.chartSettingsRadioButton;
    }

    /**
     * MovieRadioButton
     * 
     * @return MovieRadioButton
     */
    public JRadioButton getMovieRadioButton() {
        return this.movieRadioButton;
    }

    /**
     * MovieSettingsRadioButton
     * 
     * @return MovieSettingsRadioButton
     */
    public JRadioButton getMovieSettingsRadioButton() {
        return this.movieSettingsRadioButton;
    }

    /**
     * DomainSlider
     * 
     * @return DomainSlider
     */
    public JSlider getDomainSlider() {
        return this.domainSlider;
    }

    /**
     * SpringLayout
     * 
     * @return SpringLayout
     */
    public SpringLayout getSpringLayout() {
        return this.springLayout;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * If aHasChartSettings is true the chart settings panel will be displayed 
     * else the movie settings panel will be displayed
     * 
     * @param aHasChartSettings True: Display chart settings, false: Display 
     * movie settings
     */
    public void showChartSettingsPanel(boolean aHasChartSettings) {
        if (aHasChartSettings) {
            // <editor-fold defaultstate="collapsed" desc="chartSettingsPanel">
            {
                this.chartSettingsPanel.setVisible(true);
                this.springLayout.putConstraint(SpringLayout.EAST, this.chartSettingsPanel, -12, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.chartSettingsPanel, 82, SpringLayout.WEST, this);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.chartSettingsPanel, -12, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.chartSettingsPanel, -96, SpringLayout.SOUTH, this);
                {
                    this.chartSettingsPanel.add(this.copyGraphicsButton);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copyGraphicsButton, -5, SpringLayout.SOUTH, this.chartSettingsPanel);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copyGraphicsButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copyGraphicsButton, 85, SpringLayout.WEST, this.chartSettingsPanel);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copyGraphicsButton, 5, SpringLayout.WEST, this.chartSettingsPanel);
                }
                {
                    this.chartSettingsPanel.add(this.saveGraphicsButton);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.saveGraphicsButton, -40, SpringLayout.SOUTH, this.chartSettingsPanel);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.saveGraphicsButton, -75, SpringLayout.SOUTH, this.chartSettingsPanel);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.saveGraphicsButton, 85, SpringLayout.WEST, this.chartSettingsPanel);
                    this.chartSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.saveGraphicsButton, 5, SpringLayout.WEST, this.chartSettingsPanel);
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="movieSettingsPanel">
            {
                this.movieSettingsPanel.setVisible(false);
                this.springLayout.putConstraint(SpringLayout.EAST, this.movieSettingsPanel, -12, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.movieSettingsPanel, 82, SpringLayout.WEST, this);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.movieSettingsPanel, 0, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.movieSettingsPanel, 0, SpringLayout.SOUTH, this);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="imageAndInformationPanel">
            {
                this.springLayout.putConstraint(SpringLayout.EAST, this.imageAndInformationPanel, -12, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.imageAndInformationPanel, 12, SpringLayout.WEST, this);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.imageAndInformationPanel, -108, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.imageAndInformationPanel, 12, SpringLayout.NORTH, this);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="domainSlider">
            {
                this.domainSlider.setVisible(false);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.domainSlider, -153, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.domainSlider, 17, SpringLayout.NORTH, this);
                this.springLayout.putConstraint(SpringLayout.EAST, this.domainSlider, 0, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.domainSlider, 0, SpringLayout.EAST, this);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="playAnimationButton">
            {
                this.playAnimationButton.setVisible(false);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.playAnimationButton, -108, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.playAnimationButton, -143, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.EAST, this.playAnimationButton, 0, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.playAnimationButton, 0, SpringLayout.EAST, this);
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="chartSettingsPanel">
            {
                this.chartSettingsPanel.setVisible(false);
                this.springLayout.putConstraint(SpringLayout.EAST, this.chartSettingsPanel, -12, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.chartSettingsPanel, 82, SpringLayout.WEST, this);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.chartSettingsPanel, 0, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.chartSettingsPanel, 0, SpringLayout.SOUTH, this);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="movieSettingsPanel">
            {
                this.movieSettingsPanel.setVisible(true);
                this.springLayout.putConstraint(SpringLayout.EAST, this.movieSettingsPanel, -12, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.movieSettingsPanel, 82, SpringLayout.WEST, this);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.movieSettingsPanel, -12, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.movieSettingsPanel, -96, SpringLayout.SOUTH, this);
                {
                    this.movieSettingsPanel.add(this.copyGraphicsButton);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copyGraphicsButton, -5, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copyGraphicsButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copyGraphicsButton, 85, SpringLayout.WEST, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copyGraphicsButton, 5, SpringLayout.WEST, this.movieSettingsPanel);
                }
                {
                    this.movieSettingsPanel.add(this.saveGraphicsButton);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.saveGraphicsButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.saveGraphicsButton, -75, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.saveGraphicsButton, 85, SpringLayout.WEST, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.saveGraphicsButton, 5, SpringLayout.WEST, this.movieSettingsPanel);
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="imageAndInformationPanel">
            {
                this.springLayout.putConstraint(SpringLayout.EAST, this.imageAndInformationPanel, -79, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.imageAndInformationPanel, 12, SpringLayout.WEST, this);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.imageAndInformationPanel, -108, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.imageAndInformationPanel, 12, SpringLayout.NORTH, this);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="domainSlider">
            {
                this.domainSlider.setVisible(true);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.domainSlider, -153, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.domainSlider, 17, SpringLayout.NORTH, this);
                this.springLayout.putConstraint(SpringLayout.EAST, this.domainSlider, -17, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.domainSlider, -62, SpringLayout.EAST, this);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="playAnimationButton">
            {
                this.playAnimationButton.setVisible(true);
                this.springLayout.putConstraint(SpringLayout.SOUTH, this.playAnimationButton, -108, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.NORTH, this.playAnimationButton, -143, SpringLayout.SOUTH, this);
                this.springLayout.putConstraint(SpringLayout.EAST, this.playAnimationButton, -12, SpringLayout.EAST, this);
                this.springLayout.putConstraint(SpringLayout.WEST, this.playAnimationButton, -67, SpringLayout.EAST, this);
            }
            // </editor-fold>
        }
    }
    // </editor-fold>

}
