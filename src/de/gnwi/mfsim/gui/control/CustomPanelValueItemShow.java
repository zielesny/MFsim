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
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Custom panel for display of value items
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemShow extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JButton compartmentShowButton;

    private JButton showBoxPropertiesButton;

    private JComboBox selectSimulationBoxDisplayCompartmentComboBox;

    private JComboBox selectSimulationBoxDisplayComboBox;

    private JComboBox selectTimeStepDisplayComboBox;

    private JButton copyMatrixDataButton;

    private JPanel simulationBoxImageActionPanel;

    private JPanel simulationBoxActionPanel;
    
    private JPanel simulationBoxActionPanelWest;
    
    private JPanel simulationBoxActionPanelEast;

    private JButton viewButton;

    private JButton slicerTimeStepPreferencesEditButton;

    private CustomPanelImage simulationBoxImagePanel;

    private JLabel simulationBoxNameInfoLabel;

    private JLabel simulationBoxNameLabel;

    private SpringLayout selectedFeatureSimulationBoxPanelSpringLayout;

    private JPanel selectedFeatureSimulationBoxPanel;

    private JPanel compartmentImageActionPanel;

    private JPanel compartmentActionPanel;

    private JButton compartmentViewButton;

    private CustomPanelImage compartmentImagePanel;

    private JLabel compartmentNameInfoLabel;

    private JLabel compartmentNameLabel;

    private SpringLayout selectedFeatureCompartmentPanelSpringLayout;

    private JPanel selectedFeatureCompartmentPanel;

    private CustomPanelDescription selectedFeatureDescriptionPanel;

    private CustomPanelDescription selectedFeatureHintPanel;

    private CustomPanelDescription selectedFeatureErrorPanel;

    private CustomPanelValueItemMatrixShow selectedFeatureMatrixShowPanel;

    private JButton matrixDiagramButton;

    private JLabel matrixNameLabel;

    private JLabel matrixNameInfoLabel;

    private JLabel textNameLabel;

    private JLabel textNameInfoLabel;

    private SpringLayout selectedFeatureInfoPanelSpringLayout;

    private JPanel selectedFeatureInfoPanel;

    private JTabbedPane selectedFeatureTabbedPanel;

    private SpringLayout springLayout;

    private SpringLayout selectedFeatureMatrixPanelSpringLayout;

    private JPanel selectedFeatureMatrixPanel;

    private JLabel contentInfoLabel;

    private JLabel contentLabel;

    private SpringLayout selectedFeatureTextValuePanelSpringLayout;

    private JPanel selectedFeatureTextValuePanel;

    private JPanel selectedFeatureShowNothingPanel;

    private JPanel selectedFeatureCardsPanel;

    private JButton expandTreeButton;

    private JButton collapseTreeButton;

    private JPanel selectedFeaturePanel;

    private JTree featureOverviewTree;

    private JScrollPane featureOverviewTreeScrollPanel;

    private SpringLayout featureOverviewPanelSpringLayout;

    private JPanel featureOverviewPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000023L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelValueItemShow() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="featureOverviewPanel">
        {
            this.featureOverviewPanel = new JPanel();
            this.featureOverviewPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.featureOverviewPanel.toolTipText")); 
            this.featureOverviewPanelSpringLayout = new SpringLayout();
            this.featureOverviewPanel.setLayout(this.featureOverviewPanelSpringLayout);
            this.add(this.featureOverviewPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.featureOverviewPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.featureOverviewPanel, 0, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.featureOverviewPanel, 370, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.featureOverviewPanel, 0, SpringLayout.WEST, this);

            // <editor-fold defaultstate="collapsed" desc="expandTreeButton">
            {
                this.expandTreeButton = new JButton();
                this.expandTreeButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.expandTreeButton.toolTipText")); 
                this.featureOverviewPanel.add(this.expandTreeButton);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.expandTreeButton, 90, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.expandTreeButton, 10, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.expandTreeButton, 45, SpringLayout.NORTH, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.expandTreeButton, 10, SpringLayout.NORTH, this.featureOverviewPanel);
                this.expandTreeButton.setText(GuiMessage.get("CustomPanelValueItemShow.expandTreeButton.text")); 
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="collapseTreeButton">
            {
                this.collapseTreeButton = new JButton();
                this.collapseTreeButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.collapseTreeButton.toolTipText")); 
                this.featureOverviewPanel.add(this.collapseTreeButton);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.collapseTreeButton, 170, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.collapseTreeButton, 90, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.collapseTreeButton, 45, SpringLayout.NORTH, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.collapseTreeButton, 10, SpringLayout.NORTH, this.featureOverviewPanel);
                this.collapseTreeButton.setText(GuiMessage.get("CustomPanelValueItemShow.collapseTreeButton.text")); 
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="errorInfoPanel">
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="featureOverviewTreeScrollPanel & tree">
            {
                this.featureOverviewTreeScrollPanel = new JScrollPane();
                this.featureOverviewPanel.add(this.featureOverviewTreeScrollPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.featureOverviewTreeScrollPanel, -10, SpringLayout.SOUTH, this.featureOverviewPanel);
                featureOverviewPanelSpringLayout.putConstraint(SpringLayout.NORTH, featureOverviewTreeScrollPanel, 45, SpringLayout.NORTH, featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.featureOverviewTreeScrollPanel, -10, SpringLayout.EAST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.featureOverviewTreeScrollPanel, 10, SpringLayout.WEST, this.featureOverviewPanel);
                {
                    this.featureOverviewTree = new JTree();
                    this.featureOverviewTreeScrollPanel.setViewportView(this.featureOverviewTree);
                }
            }

            // </editor-fold>
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="selectedFeaturePanel">
        {
            this.selectedFeaturePanel = new JPanel();
            this.selectedFeaturePanel.setLayout(new BorderLayout());
            this.add(this.selectedFeaturePanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.selectedFeaturePanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.selectedFeaturePanel, 0, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.selectedFeaturePanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.selectedFeaturePanel, 10, SpringLayout.EAST, this.featureOverviewPanel);

            // <editor-fold defaultstate="collapsed" desc="selectedFeatureInfoPanel">
            {
                this.selectedFeatureInfoPanel = new JPanel();
                this.selectedFeatureInfoPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureInfoPanel.toolTipText")); 
                this.selectedFeatureInfoPanelSpringLayout = new SpringLayout();
                this.selectedFeatureInfoPanel.setLayout(this.selectedFeatureInfoPanelSpringLayout);
                this.selectedFeaturePanel.add(this.selectedFeatureInfoPanel, BorderLayout.CENTER);
                {
                    this.selectedFeatureTabbedPanel = new JTabbedPane();
                    this.selectedFeatureTabbedPanel.setPreferredSize(new Dimension(0, 50));
                    this.selectedFeatureTabbedPanel.setMinimumSize(new Dimension(0, 50));
                    this.selectedFeatureInfoPanel.add(this.selectedFeatureTabbedPanel);
                    this.selectedFeatureInfoPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectedFeatureTabbedPanel, -10, SpringLayout.SOUTH, this.selectedFeatureInfoPanel);
                    this.selectedFeatureInfoPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectedFeatureTabbedPanel, 10, SpringLayout.NORTH, this.selectedFeatureInfoPanel);
                    this.selectedFeatureInfoPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectedFeatureTabbedPanel, -10, SpringLayout.EAST, this.selectedFeatureInfoPanel);
                    this.selectedFeatureInfoPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectedFeatureTabbedPanel, 10, SpringLayout.WEST, this.selectedFeatureInfoPanel);

                    // <editor-fold defaultstate="collapsed" desc="selectedFeatureCardsPanel">
                    {
                        this.selectedFeatureCardsPanel = new JPanel();
                        this.selectedFeatureCardsPanel.setLayout(new CardLayout());
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureCardsPanel.title"), null, this.selectedFeatureCardsPanel, 
                                null);

                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureShowNothingPanel">
                        {
                            this.selectedFeatureShowNothingPanel = new JPanel();
                            this.selectedFeatureShowNothingPanel.setLayout(new BorderLayout());
                            this.selectedFeatureShowNothingPanel.setName(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureShowNothingPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureShowNothingPanel, this.selectedFeatureShowNothingPanel.getName());
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureTextValuePanel">
                        {
                            this.selectedFeatureTextValuePanel = new JPanel();
                            this.selectedFeatureTextValuePanelSpringLayout = new SpringLayout();
                            this.selectedFeatureTextValuePanel.setLayout(this.selectedFeatureTextValuePanelSpringLayout);
                            this.selectedFeatureTextValuePanel.setName(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureTextValuePanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureTextValuePanel, this.selectedFeatureTextValuePanel.getName());
                            {
                                this.textNameInfoLabel = new JLabel();
                                this.textNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.textNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemShow.textNameInfoLabel.text")); 
                                this.selectedFeatureTextValuePanel.add(this.textNameInfoLabel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.textNameInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.textNameInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textNameInfoLabel, 45, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textNameInfoLabel, 10, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                            }
                            {
                                this.textNameLabel = new JLabel();
                                this.textNameLabel.setText(GuiMessage.get("CustomPanelValueItemShow.textNameLabel.text")); 
                                this.selectedFeatureTextValuePanel.add(this.textNameLabel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.textNameLabel, -10, SpringLayout.EAST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.textNameLabel, 60, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textNameLabel, 45, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textNameLabel, 10, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                            }
                            {
                                this.contentInfoLabel = new JLabel();
                                this.contentInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.contentInfoLabel.setText(GuiMessage.get("CustomPanelValueItemShow.contentInfoLabel.text")); 
                                this.selectedFeatureTextValuePanel.add(this.contentInfoLabel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.contentInfoLabel, 85, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.contentInfoLabel, 50, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.contentInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.contentInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                            }
                            {
                                this.contentLabel = new JLabel();
                                this.contentLabel.setInheritsPopupMenu(true);
                                this.selectedFeatureTextValuePanel.add(this.contentLabel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.contentLabel, -10, SpringLayout.EAST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.contentLabel, 60, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.contentLabel, 85, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.contentLabel, 50, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureMatrixPanel">
                        {
                            this.selectedFeatureMatrixPanel = new JPanel();
                            this.selectedFeatureMatrixPanelSpringLayout = new SpringLayout();
                            this.selectedFeatureMatrixPanel.setLayout(this.selectedFeatureMatrixPanelSpringLayout);
                            this.selectedFeatureMatrixPanel.setName(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureMatrixPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureMatrixPanel, this.selectedFeatureMatrixPanel.getName());
                            {
                                this.matrixNameInfoLabel = new JLabel();
                                this.matrixNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.matrixNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemShow.matrixNameInfoLabel.text")); 
                                this.selectedFeatureMatrixPanel.add(this.matrixNameInfoLabel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.EAST, this.matrixNameInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.WEST, this.matrixNameInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.matrixNameInfoLabel, 45, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.matrixNameInfoLabel, 10, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                            }
                            {
                                this.matrixNameLabel = new JLabel();
                                this.matrixNameLabel.setText(GuiMessage.get("CustomPanelValueItemShow.matrixNameLabel.text")); 
                                this.selectedFeatureMatrixPanel.add(this.matrixNameLabel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.matrixNameLabel, 45, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.matrixNameLabel, 10, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.EAST, this.matrixNameLabel, -180, SpringLayout.EAST, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.WEST, this.matrixNameLabel, 60, SpringLayout.WEST, this.selectedFeatureMatrixPanel);
                            }
                            {
                                this.matrixDiagramButton = new JButton();
                                this.matrixDiagramButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.matrixDiagramButton.toolTipText")); 
                                this.matrixDiagramButton.setText(GuiMessage.get("CustomPanelValueItemShow.matrixDiagramButton.text")); 
                                this.selectedFeatureMatrixPanel.add(this.matrixDiagramButton);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.matrixDiagramButton, 45, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.matrixDiagramButton, 10, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.EAST, this.matrixDiagramButton, -90, SpringLayout.EAST, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.WEST, this.matrixDiagramButton, -170, SpringLayout.EAST, this.selectedFeatureMatrixPanel);
                            }
                            {
                                this.copyMatrixDataButton = new JButton();
                                this.copyMatrixDataButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.copyMatrixDataButton.toolTipText")); 
                                this.copyMatrixDataButton.setText(GuiMessage.get("CustomPanelValueItemShow.copyMatrixDataButton.text")); 
                                this.selectedFeatureMatrixPanel.add(this.copyMatrixDataButton);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copyMatrixDataButton, -10, SpringLayout.EAST, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copyMatrixDataButton, -90, SpringLayout.EAST, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copyMatrixDataButton, 45, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copyMatrixDataButton, 10, SpringLayout.NORTH, this.selectedFeatureMatrixPanel);
                            }
                            {
                                this.selectedFeatureMatrixShowPanel = new CustomPanelValueItemMatrixShow();
                                this.selectedFeatureMatrixPanel.add(this.selectedFeatureMatrixShowPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectedFeatureMatrixShowPanel, 0, SpringLayout.SOUTH,
                                        this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectedFeatureMatrixShowPanel, 40, SpringLayout.NORTH,
                                        this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout
                                        .putConstraint(SpringLayout.EAST, this.selectedFeatureMatrixShowPanel, 0, SpringLayout.EAST, this.selectedFeatureMatrixPanel);
                                this.selectedFeatureMatrixPanelSpringLayout
                                        .putConstraint(SpringLayout.WEST, this.selectedFeatureMatrixShowPanel, 0, SpringLayout.WEST, this.selectedFeatureMatrixPanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureCompartmentPanel">
                        {
                            this.selectedFeatureCompartmentPanel = new JPanel();
                            this.selectedFeatureCompartmentPanelSpringLayout = new SpringLayout();
                            this.selectedFeatureCompartmentPanel.setLayout(this.selectedFeatureCompartmentPanelSpringLayout);
                            this.selectedFeatureCompartmentPanel.setName(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureCompartmentPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureCompartmentPanel, this.selectedFeatureCompartmentPanel.getName());
                            {
                                this.compartmentNameInfoLabel = new JLabel();
                                this.compartmentNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.compartmentNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemShow.compartmentNameInfoLabel.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.compartmentNameInfoLabel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentNameInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentNameInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentNameInfoLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentNameInfoLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.compartmentNameLabel = new JLabel();
                                this.compartmentNameLabel.setText(GuiMessage.get("CustomPanelValueItemShow.compartmentNameLabel.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.compartmentNameLabel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentNameLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentNameLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentNameLabel, -105, SpringLayout.EAST,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentNameLabel, 60, SpringLayout.WEST,
                                        this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.compartmentShowButton = new JButton();
                                this.compartmentShowButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.compartmentShowButton.toolTipText")); 
                                this.compartmentShowButton.setText(GuiMessage.get("CustomPanelValueItemShow.compartmentShowButton.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.compartmentShowButton);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentShowButton, 92, SpringLayout.WEST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentShowButton, 12, SpringLayout.WEST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentShowButton, 85, SpringLayout.NORTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentShowButton, 50, SpringLayout.NORTH, this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.compartmentImagePanel = new CustomPanelImage();
                                this.selectedFeatureCompartmentPanel.add(this.compartmentImagePanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentImagePanel, -45, SpringLayout.SOUTH,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentImagePanel, 90, SpringLayout.NORTH,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentImagePanel, -10, SpringLayout.EAST,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentImagePanel, 12, SpringLayout.WEST,
                                        this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.selectSimulationBoxDisplayCompartmentComboBox = new JComboBox();
                                this.selectSimulationBoxDisplayCompartmentComboBox.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.selectSimulationBoxDisplayCompartmentComboBox.toolTipText")); 
                                this.selectedFeatureCompartmentPanel.add(this.selectSimulationBoxDisplayCompartmentComboBox);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectSimulationBoxDisplayCompartmentComboBox, -90, SpringLayout.EAST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectSimulationBoxDisplayCompartmentComboBox, -250, SpringLayout.EAST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectSimulationBoxDisplayCompartmentComboBox, -5, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectSimulationBoxDisplayCompartmentComboBox, -40, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.compartmentViewButton = new JButton();
                                this.compartmentViewButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.compartmentViewButton.toolTipText")); 
                                this.compartmentViewButton.setText(GuiMessage.get("CustomPanelValueItemShow.compartmentViewButton.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.compartmentViewButton);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentViewButton, -5, SpringLayout.SOUTH,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentViewButton, -40, SpringLayout.SOUTH,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentViewButton, -10, SpringLayout.EAST,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentViewButton, -90, SpringLayout.EAST,
                                        this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.showBoxPropertiesButton = new JButton();
                                this.showBoxPropertiesButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.showBoxPropertiesButton.toolTipText")); 
                                this.showBoxPropertiesButton.setText(GuiMessage.get("CustomPanelValueItemShow.showBoxPropertiesButton.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.showBoxPropertiesButton);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.showBoxPropertiesButton, -5, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.showBoxPropertiesButton, -40, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.showBoxPropertiesButton, 92, SpringLayout.WEST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.showBoxPropertiesButton, 12, SpringLayout.WEST, this.selectedFeatureCompartmentPanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureSimulationBoxPanel">
                        {
                            this.selectedFeatureSimulationBoxPanel = new JPanel();
                            this.selectedFeatureSimulationBoxPanelSpringLayout = new SpringLayout();
                            this.selectedFeatureSimulationBoxPanel.setLayout(this.selectedFeatureSimulationBoxPanelSpringLayout);
                            this.selectedFeatureSimulationBoxPanel.setName(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureSimulationBoxPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureSimulationBoxPanel, this.selectedFeatureSimulationBoxPanel.getName());
                            {
                                this.simulationBoxNameInfoLabel = new JLabel();
                                this.simulationBoxNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.simulationBoxNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemShow.simulationBoxNameInfoLabel.text")); 
                                this.selectedFeatureSimulationBoxPanel.add(this.simulationBoxNameInfoLabel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.simulationBoxNameInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.simulationBoxNameInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxNameInfoLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxNameInfoLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureSimulationBoxPanel);
                            }
                            {
                                this.simulationBoxNameLabel = new JLabel();
                                this.simulationBoxNameLabel.setText(GuiMessage.get("CustomPanelValueItemShow.simulationBoxNameLabel.text")); 
                                this.selectedFeatureSimulationBoxPanel.add(this.simulationBoxNameLabel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxNameLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxNameLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.simulationBoxNameLabel, -105, SpringLayout.EAST,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.simulationBoxNameLabel, 60, SpringLayout.WEST,
                                        this.selectedFeatureSimulationBoxPanel);
                            }
                            {
                                this.simulationBoxImageActionPanel = new JPanel();
                                this.simulationBoxImageActionPanel.setLayout(new BorderLayout());
                                this.selectedFeatureSimulationBoxPanel.add(this.simulationBoxImageActionPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxImageActionPanel, -10, SpringLayout.SOUTH,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxImageActionPanel, 50, SpringLayout.NORTH,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.EAST, this.simulationBoxImageActionPanel, -10, SpringLayout.EAST,
                                        this.selectedFeatureSimulationBoxPanel);
                                this.selectedFeatureSimulationBoxPanelSpringLayout.putConstraint(SpringLayout.WEST, this.simulationBoxImageActionPanel, 12, SpringLayout.WEST,
                                        this.selectedFeatureSimulationBoxPanel);
                                {
                                    this.simulationBoxImagePanel = new CustomPanelImage();
                                    this.simulationBoxImageActionPanel.add(this.simulationBoxImagePanel, BorderLayout.CENTER);
                                }
                                {
                                    this.simulationBoxActionPanel = new JPanel();
                                    this.simulationBoxActionPanel.setLayout(new BorderLayout());
                                    this.simulationBoxImageActionPanel.add(this.simulationBoxActionPanel, BorderLayout.SOUTH);
                                    {
                                        this.simulationBoxActionPanelWest = new JPanel();
                                        this.simulationBoxActionPanelWest.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
                                        this.simulationBoxActionPanel.add(this.simulationBoxActionPanelWest, BorderLayout.WEST);
                                        {
                                            this.slicerTimeStepPreferencesEditButton = new JButton();
                                            this.slicerTimeStepPreferencesEditButton.setMaximumSize(new Dimension(80, 35));
                                            this.slicerTimeStepPreferencesEditButton.setMinimumSize(new Dimension(80, 35));
                                            this.slicerTimeStepPreferencesEditButton.setPreferredSize(new Dimension(80, 35));
                                            this.slicerTimeStepPreferencesEditButton.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.slicerTimeStepPreferencesEditButton.toolTipText")); 
                                            this.slicerTimeStepPreferencesEditButton.setText(GuiMessage.get("CustomPanelValueItemShow.slicerTimeStepPreferencesEditButton.text")); 
                                            this.simulationBoxActionPanelWest.add(this.slicerTimeStepPreferencesEditButton);
                                        }
                                    }
                                    {
                                        this.simulationBoxActionPanelEast = new JPanel();
                                        this.simulationBoxActionPanelEast.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
                                        this.simulationBoxActionPanel.add(this.simulationBoxActionPanelEast, BorderLayout.EAST);
                                        {
                                            this.selectTimeStepDisplayComboBox = new JComboBox();
                                            this.selectTimeStepDisplayComboBox.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.selectTimeStepDisplayComboBox.toolTipText")); 
                                            this.selectTimeStepDisplayComboBox.setPreferredSize(new Dimension(160, 35));
                                            this.selectTimeStepDisplayComboBox.setMinimumSize(new Dimension(160, 35));
                                            this.selectTimeStepDisplayComboBox.setMaximumSize(new Dimension(160, 35));
                                            this.simulationBoxActionPanelEast.add(this.selectTimeStepDisplayComboBox);
                                        }
                                        {
                                            this.selectSimulationBoxDisplayComboBox = new JComboBox();
                                            this.selectSimulationBoxDisplayComboBox.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.selectSimulationBoxDisplayComboBox.toolTipText")); 
                                            this.selectSimulationBoxDisplayComboBox.setPreferredSize(new Dimension(160, 35));
                                            this.selectSimulationBoxDisplayComboBox.setMinimumSize(new Dimension(160, 35));
                                            this.selectSimulationBoxDisplayComboBox.setMaximumSize(new Dimension(160, 35));
                                            this.simulationBoxActionPanelEast.add(this.selectSimulationBoxDisplayComboBox);
                                        }
                                        {
                                            this.viewButton = new JButton();
                                            this.viewButton.setMaximumSize(new Dimension(80, 35));
                                            this.viewButton.setMinimumSize(new Dimension(80, 35));
                                            this.viewButton.setPreferredSize(new Dimension(80, 35));
                                            this.viewButton.setText(GuiMessage.get("CustomPanelValueItemShow.viewButton.text")); 
                                            this.simulationBoxActionPanelEast.add(this.viewButton);
                                        }
                                    }
                                }
                            }
                        }

                        // </editor-fold>
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectedFeatureDescriptionPanel">
                    {
                        this.selectedFeatureDescriptionPanel = new CustomPanelDescription();
                        this.selectedFeatureDescriptionPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureDescriptionPanel.toolTipText")); 
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureDescriptionPanel.title"), null, 
                                this.selectedFeatureDescriptionPanel, null);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectedFeatureHintPanel">
                    {
                        this.selectedFeatureHintPanel = new CustomPanelDescription();
                        this.selectedFeatureHintPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureHintPanel.toolTipText")); 
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureHintPanel.title"), null, 
                                this.selectedFeatureHintPanel, null);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectedFeatureErrorPanel">
                    {
                        this.selectedFeatureErrorPanel = new CustomPanelDescription();
                        this.selectedFeatureErrorPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureErrorPanel.toolTipText")); 
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemShow.selectedFeatureErrorPanel.title"), null, 
                                this.selectedFeatureErrorPanel, null);
                    }

                    // </editor-fold>
                }
            }

            // </editor-fold>
        }

        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CollapseTreeButton
     * 
     * @return CollapseTreeButton
     */
    public JButton getCollapseTreeButton() {
        return collapseTreeButton;
    }

    /**
     * ExpandTreeButton
     * 
     * @return ExpandTreeButton
     */
    public JButton getExpandTreeButton() {
        return expandTreeButton;
    }

    /**
     * ContentLabel
     * 
     * @return ContentLabel
     */
    public JLabel getContentLabel() {
        return this.contentLabel;
    }

    /**
     * FeatureOverviewTree
     * 
     * @return FeatureOverviewTree
     */
    public JTree getFeatureOverviewTree() {
        return featureOverviewTree;
    }

    /**
     * SelectedFeatureCardsPanel
     * 
     * @return SelectedFeatureCardsPanel
     */
    public JPanel getSelectedFeatureCardsPanel() {
        return selectedFeatureCardsPanel;
    }

    /**
     * SelectedFeatureInfoPanel
     * 
     * @return SelectedFeatureInfoPanel
     */
    public JPanel getSelectedFeatureInfoPanel() {
        return selectedFeatureInfoPanel;
    }

    /**
     * SelectedFeaturePanel
     * 
     * @return SelectedFeaturePanel
     */
    public JPanel getSelectedFeaturePanel() {
        return selectedFeaturePanel;
    }

    /**
     * SelectedFeatureTabbedPanel
     * 
     * @return SelectedFeatureTabbedPanel
     */
    public JTabbedPane getSelectedFeatureTabbedPanel() {
        return selectedFeatureTabbedPanel;
    }

    /**
     * FeatureOverviewPanel
     * 
     * @return FeatureOverviewPanel
     */
    public JPanel getFeatureOverviewPanel() {
        return featureOverviewPanel;
    }

    /**
     * TextNameLabel
     * 
     * @return TextNameLabel
     */
    public JLabel getTextNameLabel() {
        return textNameLabel;
    }

    /**
     * MatrixNameLabel
     * 
     * @return MatrixNameLabel
     */
    public JLabel getMatrixNameLabel() {
        return matrixNameLabel;
    }

    /**
     * MatrixDiagramButton
     * 
     * @return MatrixDiagramButton
     */
    public JButton getMatrixDiagramButton() {
        return matrixDiagramButton;
    }

    /**
     * SelectedFeatureMatrixShowPanel
     * 
     * @return SelectedFeatureMatrixShowPanel
     */
    public CustomPanelValueItemMatrixShow getSelectedFeatureMatrixShowPanel() {
        return selectedFeatureMatrixShowPanel;
    }

    /**
     * SelectedFeatureDescriptionPanel
     * 
     * @return SelectedFeatureDescriptionPanel
     */
    public CustomPanelDescription getSelectedFeatureDescriptionPanel() {
        return selectedFeatureDescriptionPanel;
    }

    /**
     * SelectedFeatureHintPanel
     * 
     * @return SelectedFeatureHintPanel
     */
    public CustomPanelDescription getSelectedFeatureHintPanel() {
        return selectedFeatureHintPanel;
    }

    /**
     * SelectedFeatureErrorPanel
     * 
     * @return SelectedFeatureErrorPanel
     */
    public CustomPanelDescription getSelectedFeatureErrorPanel() {
        return selectedFeatureErrorPanel;
    }

    /**
     * CompartmentNameLabel
     * 
     * @return CompartmentNameLabel
     */
    public JLabel getCompartmentNameLabel() {
        return compartmentNameLabel;
    }

    /**
     * CompartmentActionPanel
     * 
     * @return CompartmentActionPanel
     */
    public JPanel getCompartmentActionPanel() {
        return compartmentActionPanel;
    }

    /**
     * CompartmentImagePanel
     * 
     * @return CompartmentImagePanel
     */
    public CustomPanelImage getCompartmentImagePanel() {
        return compartmentImagePanel;
    }

    /**
     * CompartmentViewButton
     * 
     * @return CompartmentViewButton
     */
    public JButton getCompartmentViewButton() {
        return compartmentViewButton;
    }

    /**
     * SimulationBoxNameLabel
     * 
     * @return SimulationBoxNameLabel
     */
    public JLabel getSimulationBoxNameLabel() {
        return simulationBoxNameLabel;
    }

    /**
     * SimulationBoxImagePanel
     * 
     * @return SimulationBoxImagePanel
     */
    public CustomPanelImage getSimulationBoxImagePanel() {
        return simulationBoxImagePanel;
    }

    /**
     * ViewButton
     * 
     * @return ViewButton
     */
    public JButton getViewButton() {
        return viewButton;
    }

    /**
     * SlicerTimeStepPreferencesEditButton
     * 
     * @return SlicerTimeStepPreferencesEditButton
     */
    public JButton getSlicerTimeStepPreferencesEditButton() {
        return slicerTimeStepPreferencesEditButton;
    }

    /**
     * CopyMatrixDataButton
     * 
     * @return CopyMatrixDataButton
     */
    public JButton getCopyMatrixDataButton() {
        return copyMatrixDataButton;
    }

    /**
     * SelectSimulationBoxDisplayComboBox
     * 
     * @return SelectSimulationBoxDisplayComboBox
     */
    public JComboBox getSelectSimulationBoxDisplayComboBox() {
        return selectSimulationBoxDisplayComboBox;
    }

    /**
     * SelectTimeStepDisplayComboBox
     * 
     * @return SelectTimeStepDisplayComboBox
     */
    public JComboBox getSelectTimeStepDisplayComboBox() {
        return selectTimeStepDisplayComboBox;
    }

    /**
     * SelectSimulationBoxDisplayCompartmentComboBox
     * 
     * @return SelectSimulationBoxDisplayCompartmentComboBox
     */
    public JComboBox getSelectSimulationBoxDisplayCompartmentComboBox() {
        return selectSimulationBoxDisplayCompartmentComboBox;
    }

    /**
     * ShowBoxPropertiesButton
     * 
     * @return ShowBoxPropertiesButton
     */
    public JButton getShowBoxPropertiesButton() {
        return showBoxPropertiesButton;
    }

    /**
     * CompartmentShowButton
     * 
     * @return CompartmentShowButton
     */
    public JButton getCompartmentShowButton() {
        return compartmentShowButton;
    }
    // </editor-fold>

}
