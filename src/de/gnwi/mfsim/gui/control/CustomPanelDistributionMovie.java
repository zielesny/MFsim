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
import javax.swing.border.BevelBorder;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * A panel displaying a distribution movie
 *
 * @author Jan-Mathis Hein
 */
public class CustomPanelDistributionMovie extends JPanel{

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private SpringLayout springLayout;
    
    private JPanel mainPanel;
    
    private SpringLayout mainPanelSpringLayout;

    private JSlider domainSlider;
    
    private JButton playAnimationButton;

    private JPanel imageAndInformationPanel;

    private SpringLayout imageAndInformationPanelSpringLayout;
    
    private CustomPanelImage imagePanel;
    
    private JLabel informationLabel;

    private JPanel movieSettingsPanel;
    
    private SpringLayout movieSettingsPanelSpringLayout;

    private JButton copyGraphicsButton;

    private JButton saveGraphicsButton;
    
    private JButton zoomButton;
    
    private JButton restoreButton;
    
    private JCheckBox varyingStatisticsCheckBox;
    
    private JButton createMovieButton;
    
    private JRadioButton animationRadioButton;
    
    private JRadioButton movieRadioButton;
    
    private ButtonGroup movieAnimationButtonGroup = new ButtonGroup();
    
    private JButton firstMovieSettingsButton;
    
    private JButton secondMovieSettingsButton;
    
    private JPanel loadingPanel;
    
    private SpringLayout loadingPanelSpringLayout;
    
    private JLabel progressLabel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000068L;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelDistributionMovie() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            this.mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
            this.add(this.mainPanel);
            // NOTE: Distances between components are incremented by two to make
            // up for the use of bevel borders in contrast to titled borders
            this.springLayout.putConstraint(SpringLayout.EAST, this.mainPanel, -2, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.mainPanel, 2, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.mainPanel, -2, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.mainPanel, 2, SpringLayout.NORTH, this);
            // <editor-fold defaultstate="collapsed" desc="- movieSettingsPanel">
            {
                this.movieSettingsPanel = new JPanel();
                this.movieSettingsPanelSpringLayout = new SpringLayout();
                this.movieSettingsPanel.setLayout(this.movieSettingsPanelSpringLayout);
                this.movieSettingsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.mainPanel.add(this.movieSettingsPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.movieSettingsPanel, -12, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.movieSettingsPanel, 12, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.movieSettingsPanel, -12, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.movieSettingsPanel, -96, SpringLayout.SOUTH, this.mainPanel);
                {
                    this.copyGraphicsButton = new JButton();
                    this.copyGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.copyGraphicsButton.toolTipText")); 
                    this.copyGraphicsButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.copyGraphicsButton.text")); 
                    this.movieSettingsPanel.add(this.copyGraphicsButton);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copyGraphicsButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copyGraphicsButton, -75, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copyGraphicsButton, 85, SpringLayout.WEST, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copyGraphicsButton, 5, SpringLayout.WEST, this.movieSettingsPanel);
                }
                {
                    this.saveGraphicsButton = new JButton();
                    this.saveGraphicsButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.saveGraphicsButton.toolTipText")); 
                    this.saveGraphicsButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.saveGraphicsButton.text")); 
                    this.movieSettingsPanel.add(this.saveGraphicsButton);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.saveGraphicsButton, -5, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.saveGraphicsButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.saveGraphicsButton, 85, SpringLayout.WEST, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.saveGraphicsButton, 5, SpringLayout.WEST, this.movieSettingsPanel);
                }
                {
                    this.zoomButton = new JButton();
                    this.zoomButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.zoomButton.toolTipText")); 
                    this.zoomButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.zoomButton.text")); 
                    this.movieSettingsPanel.add(this.zoomButton);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.zoomButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.zoomButton, -75, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.zoomButton, 165, SpringLayout.WEST, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.zoomButton, 85, SpringLayout.WEST, this.movieSettingsPanel);
                }
                {
                    this.restoreButton = new JButton();
                    this.restoreButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.restoreButton.toolTipText")); 
                    this.restoreButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.restoreButton.text")); 
                    this.movieSettingsPanel.add(this.restoreButton);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.restoreButton, -5, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.restoreButton, -40, SpringLayout.SOUTH, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.restoreButton, 165, SpringLayout.WEST, this.movieSettingsPanel);
                    this.movieSettingsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.restoreButton, 85, SpringLayout.WEST, this.movieSettingsPanel);
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
            // <editor-fold defaultstate="collapsed" desc="- imageAndInformationPanel">
            {
                this.imageAndInformationPanel = new JPanel();
                this.imageAndInformationPanelSpringLayout = new SpringLayout();
                this.imageAndInformationPanel.setLayout(this.imageAndInformationPanelSpringLayout);
                this.imageAndInformationPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.mainPanel.add(this.imageAndInformationPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.imageAndInformationPanel, -79, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.imageAndInformationPanel, 12, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.imageAndInformationPanel, -108, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.imageAndInformationPanel, 12, SpringLayout.NORTH, this.mainPanel);
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
            // <editor-fold defaultstate="collapsed" desc="- domainSlider">
            {
                this.domainSlider = new JSlider(JSlider.VERTICAL);
                this.domainSlider.setSnapToTicks(true);
                this.domainSlider.setPaintTicks(true);
                this.domainSlider.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.domainSlider.toolTipText"));
                this.mainPanel.add(this.domainSlider);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.domainSlider, -153, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.domainSlider, 17, SpringLayout.NORTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.domainSlider, -17, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.domainSlider, -62, SpringLayout.EAST, this.mainPanel);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- playAnimationButton">
            {
                this.playAnimationButton = new JButton();
                this.playAnimationButton.setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.toolTipText1"));
                this.playAnimationButton.setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.text1"));
                this.mainPanel.add(this.playAnimationButton);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.playAnimationButton, -108, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.playAnimationButton, -143, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.playAnimationButton, -12, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.playAnimationButton, -67, SpringLayout.EAST, this.mainPanel);
            }
            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="loadingPanel">
        {
            this.loadingPanel = new JPanel();
            this.loadingPanelSpringLayout = new SpringLayout();
            this.loadingPanel.setLayout(this.loadingPanelSpringLayout);
            this.add(this.loadingPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.loadingPanel, -0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.loadingPanel, 0, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.loadingPanel, -0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.loadingPanel, 0, SpringLayout.NORTH, this);
            // <editor-fold defaultstate="collapsed" desc="- progressLabel">
            {
                this.progressLabel = new JLabel();
                this.progressLabel.setOpaque(true);
                this.progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
                this.progressLabel.setText(GuiMessage.get("DistributionMovie.CreatingCharts"));
                this.progressLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                this.loadingPanel.add(this.progressLabel);
                this.loadingPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.progressLabel, -10, SpringLayout.SOUTH, this.loadingPanel);
                this.loadingPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.progressLabel, -35, SpringLayout.SOUTH, this.loadingPanel);
                this.loadingPanelSpringLayout.putConstraint(SpringLayout.EAST, this.progressLabel, -10, SpringLayout.EAST, this.loadingPanel);
                this.loadingPanelSpringLayout.putConstraint(SpringLayout.WEST, this.progressLabel, 10, SpringLayout.WEST, this.loadingPanel);
            }
            // </editor-fold>
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
     * ProgressLabel
     * 
     * @return ProgressLabel
     */
    public JLabel getProgressLabel() {
        return this.progressLabel;
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
     * CreatMovieButton
     * 
     * @return CreatMovieButton
     */
    public JButton getCreatMovieButton() {
        return this.createMovieButton;
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
     * VaryingStatisticsCheckBox
     * 
     * @return VaryingStatisticsCheckBox
     */
    public JCheckBox getVaryingStatisticsCheckBox() {
        return this.varyingStatisticsCheckBox;
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
     * AnimationRadioButton
     * 
     * @return AnimationRadioButton
     */
    public JRadioButton getAnimationRadioButton() {
        return this.animationRadioButton;
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
     * DomainSlider
     * 
     * @return DomainSlider
     */
    public JSlider getDomainSlider() {
        return this.domainSlider;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Shows the main panel if aHasMainPanel is true, 
     * else show the loading panel
     * 
     * @param aHasMainPanel True: Show the main panel, false: Show the loading 
     * panel
     */
    public void showMainPanel(boolean aHasMainPanel) {
        this.loadingPanel.setVisible(!aHasMainPanel);
        this.mainPanel.setVisible(aHasMainPanel);
    }
    // </editor-fold>

}
