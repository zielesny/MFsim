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
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Custom panel for edit of value items
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemEdit extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JButton showBoxPropertiesButton;
    /**
     * GUI element
     */
    private JButton showTableDataSchemaButton;
    /**
     * GUI element
     */
    private SpringLayout tableDataSchemaPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel tableDataSchemaPanel;
    /**
     * GUI element
     */
    private JComboBox tableDataSchemaComboBox;
    /**
     * GUI element
     */
    private JButton applyTableDataSchemaButton;
    /**
     * GUI element
     */
    private JButton removeTableDataSchemaButton;
    /**
     * GUI element
     */
    private JButton setCurrentTableDataSchemaButton;
    /**
     * GUI element
     */
    private JComboBox selectSimulationBoxDisplayComboBox;
    /**
     * GUI element
     */
    private JLabel fileNameInfoLabel;
    /**
     * GUI element
     */
    private JLabel fileNameLabel;
    /**
     * GUI element
     */
    private JLabel fileInfoLabel;
    /**
     * GUI element
     */
    private JTextField fileTextField;
    /**
     * GUI element
     */
    private JButton fileBrowseButton;
    /**
     * GUI element
     */
    private JPanel selectedFeatureFileValuePanel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureFileValuePanelSpringLayout;
    /**
     * GUI element
     */
    private JLabel textShowContentLabel;
    /**
     * GUI element
     */
    private JLabel textShowContentInfoLabel;
    /**
     * GUI element
     */
    private JLabel textShowNameLabel;
    /**
     * GUI element
     */
    private JLabel textShowNameInfoLabel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureTextShowPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureTextShowPanel;
    /**
     * GUI element
     */
    private JButton compartmentRemoveButton;
    /**
     * GUI element
     */
    private JButton compartmentViewButton;
    /**
     * GUI element
     */
    private JButton compartmentEditButton;
    /**
     * GUI element
     */
    private CustomPanelImage compartmentImagePanel;
    /**
     * GUI element
     */
    private JLabel compartmentNameInfoLabel;
    /**
     * GUI element
     */
    private JLabel compartmentNameLabel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureCompartmentPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureCompartmentPanel;
    /**
     * GUI element
     */
    private JButton selectHintStatusButton;
    /**
     * GUI element
     */
    private JButton selectErrorStatusButton;
    /**
     * GUI element
     */
    private JComboBox selectValueItemStatusComboBox;
    /**
     * GUI element
     */
    private JPanel selectValueItemStatusPanel;
    /**
     * GUI element
     */
    private JPanel featureOverviewSelectPanel;
    /**
     * GUI element
     */
    private JLabel selectedFeatureSelectionTextInfoLabel;
    /**
     * GUI element
     */
    private JPanel selectedFeatureSelectionTextInfoPanel;
    /**
     * GUI element
     */
    private CustomPanelValueItemFlexibleMatrix selectedFeatureFlexibleMatrixPanel;
    /**
     * GUI element
     */
    private JLabel selectedFeatureNumericValueInfoLabel;
    /**
     * GUI element
     */
    private JPanel selectedFeatureNumericValueInfoPanel;
    /**
     * GUI element
     */
    private JButton matrixDiagramButton;
    /**
     * GUI element
     */
    private JComboBox selectionTextComboBox;
    /**
     * GUI element
     */
    private JLabel selectionTextInfoLabel;
    /**
     * GUI element
     */
    private JLabel selectionTextNameLabel;
    /**
     * GUI element
     */
    private JLabel selectionTextNameInfoLabel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureSelectionTextPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureSelectionTextPanel;
    /**
     * GUI element
     */
    private JLabel matrixNameLabel;
    /**
     * GUI element
     */
    private JLabel matrixNameInfoLabel;
    /**
     * GUI element
     */
    private JLabel directoryNameLabel;
    /**
     * GUI element
     */
    private JLabel directoryNameInfoLabel;
    /**
     * GUI element
     */
    private JLabel timestampNameLabel;
    /**
     * GUI element
     */
    private JLabel timestampNameInfoLabel;
    /**
     * GUI element
     */
    private JLabel textNameLabel;
    /**
     * GUI element
     */
    private JLabel textNameInfoLabel;
    /**
     * GUI element
     */
    private JLabel valueNameLabel;
    /**
     * GUI element
     */
    private JLabel valueNameInfoLabel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureInfoPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureInfoPanel;
    /**
     * GUI element
     */
    private CustomPanelDescription selectedFeatureDescriptionPanel;
    /**
     * GUI element
     */
    private CustomPanelDescription selectedFeatureHintPanel;
    /**
     * GUI element
     */
    private CustomPanelDescription selectedFeatureErrorPanel;
    /**
     * GUI element
     */
    private JTabbedPane selectedFeatureTabbedPanel;
    /**
     * GUI element
     */
    private JCheckBox activityCheckBox;
    /**
     * GUI element
     */
    private JButton directoryBrowseButton;
    /**
     * GUI element
     */
    private JTextField directoryTextField;
    /**
     * GUI element
     */
    private JLabel directoryInfoLabel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureDirectoryValuePanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureDirectoryValuePanel;
    /**
     * GUI element
     */
    private JButton timestampNowButton;
    /**
     * GUI element
     */
    private JTextField timestampTextField;
    /**
     * GUI element
     */
    private JLabel timestampInfoLabel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureDateValuePanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureTimestampValuePanel;
    /**
     * GUI element
     */
    private SpringLayout springLayout;
    /**
     * GUI element
     */
    private BorderLayout selectedFeatureMatrixPanelBorderLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureMatrixPanel;
    /**
     * GUI element
     */
    private SpringLayout matrixDisplayPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel matrixDisplayPanel;
    /**
     * GUI element
     */
    private JLabel textContentInfoLabel;
    /**
     * GUI element
     */
    private JTextField textTextField;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureTextValuePanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureTextValuePanel;
    /**
     * GUI element
     */
    private JTextField valueTextField;
    /**
     * GUI element
     */
    private JLabel valueInfoLabel;
    /**
     * GUI element
     */
    private SpringLayout selectedFeatureNumericValuePanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectedFeatureNumericValuePanel;
    /**
     * GUI element
     */
    private JPanel selectedFeatureShowNothingPanel;
    /**
     * GUI element
     */
    private JPanel selectedFeatureCardsPanel;
    /**
     * GUI element
     */
    private JButton expandTreeButton;
    /**
     * GUI element
     */
    private JButton collapseTreeButton;
    /**
     * GUI element
     */
    private JPanel selectedFeaturePanel;
    /**
     * GUI element
     */
    private JTree featureOverviewTree;
    /**
     * GUI element
     */
    private JScrollPane featureOverviewTreeScrollPanel;
    /**
     * GUI element
     */
    private SpringLayout featureOverviewPanelSpringLayout;
    /**
     * GUI element
     */
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
    static final long serialVersionUID = 1000000000000000011L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelValueItemEdit() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="featureOverviewPanel">
        {
            this.featureOverviewPanel = new JPanel();
            this.featureOverviewPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.featureOverviewPanel.toolTipText")); 
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
                this.expandTreeButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.expandTreeButton.toolTipText")); 
                this.featureOverviewPanel.add(this.expandTreeButton);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.expandTreeButton, 90, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.expandTreeButton, 10, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.expandTreeButton, 45, SpringLayout.NORTH, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.expandTreeButton, 10, SpringLayout.NORTH, this.featureOverviewPanel);
                this.expandTreeButton.setText(GuiMessage.get("CustomPanelValueItemEdit.expandTreeButton.text")); 
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="collapseTreeButton">
            {
                this.collapseTreeButton = new JButton();
                this.collapseTreeButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.collapseTreeButton.toolTipText")); 
                this.featureOverviewPanel.add(this.collapseTreeButton);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.collapseTreeButton, 170, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.collapseTreeButton, 90, SpringLayout.WEST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.collapseTreeButton, 45, SpringLayout.NORTH, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.collapseTreeButton, 10, SpringLayout.NORTH, this.featureOverviewPanel);
                this.collapseTreeButton.setText(GuiMessage.get("CustomPanelValueItemEdit.collapseTreeButton.text")); 
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="activityCheckBox">
            {
                this.activityCheckBox = new JCheckBox();
                this.activityCheckBox.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.activityCheckBox.toolTipText")); 
                this.activityCheckBox.setText(GuiMessage.get("CustomPanelValueItemEdit.activityCheckBox.text")); 
                this.featureOverviewPanel.add(this.activityCheckBox);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.activityCheckBox, -10, SpringLayout.EAST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.activityCheckBox, -65, SpringLayout.EAST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.activityCheckBox, 45, SpringLayout.NORTH, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.activityCheckBox, 10, SpringLayout.NORTH, this.featureOverviewPanel);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="featureOverviewSelectPanel">
            {
                this.featureOverviewSelectPanel = new JPanel();
                final BorderLayout featureOverviewSelectPanelBorderLayout = new BorderLayout();
                featureOverviewSelectPanelBorderLayout.setVgap(5);
                this.featureOverviewSelectPanel.setLayout(featureOverviewSelectPanelBorderLayout);
                this.featureOverviewPanel.add(this.featureOverviewSelectPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.featureOverviewSelectPanel, -10, SpringLayout.SOUTH, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.featureOverviewSelectPanel, 50, SpringLayout.NORTH, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.EAST, this.featureOverviewSelectPanel, -10, SpringLayout.EAST, this.featureOverviewPanel);
                this.featureOverviewPanelSpringLayout.putConstraint(SpringLayout.WEST, this.featureOverviewSelectPanel, 10, SpringLayout.WEST, this.featureOverviewPanel);

                // <editor-fold defaultstate="collapsed" desc="featureOverviewTreeScrollPanel & tree">
                {
                    this.featureOverviewTreeScrollPanel = new JScrollPane();
                    this.featureOverviewTreeScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                    this.featureOverviewSelectPanel.add(this.featureOverviewTreeScrollPanel);
                    {
                        this.featureOverviewTree = new JTree();
                        this.featureOverviewTreeScrollPanel.setViewportView(this.featureOverviewTree);
                    }
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="selectValueItemStatusPanel">
                {
                    this.selectValueItemStatusPanel = new JPanel();
                    final BorderLayout borderLayout = new BorderLayout();
                    this.selectValueItemStatusPanel.setLayout(borderLayout);
                    this.featureOverviewSelectPanel.add(this.selectValueItemStatusPanel, BorderLayout.SOUTH);

                    // <editor-fold defaultstate="collapsed" desc="selectValueItemStatusComboBox">
                    {
                        this.selectValueItemStatusComboBox = new JComboBox();
                        this.selectValueItemStatusComboBox.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectValueItemStatusComboBox.toolTipText")); 
                        this.selectValueItemStatusComboBox.setMaximumSize(new Dimension(0, 35));
                        this.selectValueItemStatusComboBox.setMinimumSize(new Dimension(0, 35));
                        this.selectValueItemStatusComboBox.setPreferredSize(new Dimension(0, 35));
                        this.selectValueItemStatusPanel.add(this.selectValueItemStatusComboBox, BorderLayout.CENTER);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectErrorStatusButton">
                    {
                        this.selectErrorStatusButton = new JButton();
                        this.selectErrorStatusButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectErrorStatusButton.toolTipText")); 
                        this.selectErrorStatusButton.setMaximumSize(new Dimension(80, 35));
                        this.selectErrorStatusButton.setMinimumSize(new Dimension(80, 35));
                        this.selectErrorStatusButton.setPreferredSize(new Dimension(80, 35));
                        this.selectErrorStatusButton.setText(GuiMessage.get("CustomPanelValueItemEdit.selectErrorStatusButton.text")); 
                        this.selectValueItemStatusPanel.add(this.selectErrorStatusButton, BorderLayout.LINE_START);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectHintStatusButton">
                    {
                        this.selectHintStatusButton = new JButton();
                        this.selectHintStatusButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectHintStatusButton.toolTipText")); 
                        this.selectHintStatusButton.setMaximumSize(new Dimension(80, 35));
                        this.selectHintStatusButton.setMinimumSize(new Dimension(80, 35));
                        this.selectHintStatusButton.setPreferredSize(new Dimension(80, 35));
                        this.selectHintStatusButton.setText(GuiMessage.get("CustomPanelValueItemEdit.selectHintStatusButton.text")); 
                        this.selectValueItemStatusPanel.add(this.selectHintStatusButton, BorderLayout.LINE_END);
                    }

                    // </editor-fold>
                }

                // </editor-fold>
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
                this.selectedFeatureInfoPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureInfoPanel.toolTipText")); 
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
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureCardsPanel.title"), null, this.selectedFeatureCardsPanel, 
                                null);

                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureShowNothingPanel">
                        {
                            this.selectedFeatureShowNothingPanel = new JPanel();
                            this.selectedFeatureShowNothingPanel.setLayout(new BorderLayout());
                            this.selectedFeatureShowNothingPanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureShowNothingPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureShowNothingPanel, this.selectedFeatureShowNothingPanel.getName());
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureNumericValuePanel">
                        {
                            this.selectedFeatureNumericValuePanel = new JPanel();
                            this.selectedFeatureNumericValuePanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureNumericValuePanel.toolTipText")); 
                            this.selectedFeatureNumericValuePanel.setMinimumSize(new Dimension(0, 0));
                            this.selectedFeatureNumericValuePanel.setMaximumSize(new Dimension(0, 0));
                            this.selectedFeatureNumericValuePanel.setPreferredSize(new Dimension(0, 0));
                            this.selectedFeatureNumericValuePanelSpringLayout = new SpringLayout();
                            this.selectedFeatureNumericValuePanel.setLayout(this.selectedFeatureNumericValuePanelSpringLayout);
                            this.selectedFeatureNumericValuePanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureNumericValuePanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureNumericValuePanel, this.selectedFeatureNumericValuePanel.getName());
                            {
                                this.valueNameInfoLabel = new JLabel();
                                this.valueNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.valueNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.valueNameInfoLabel.text")); 
                                this.selectedFeatureNumericValuePanel.add(this.valueNameInfoLabel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.valueNameInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.valueNameInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.valueNameInfoLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.valueNameInfoLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureNumericValuePanel);
                            }
                            {
                                this.valueNameLabel = new JLabel();
                                this.valueNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.valueNameLabel.text")); 
                                this.selectedFeatureNumericValuePanel.add(this.valueNameLabel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.valueNameLabel, 45, SpringLayout.NORTH, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.valueNameLabel, 10, SpringLayout.NORTH, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.valueNameLabel, -10, SpringLayout.EAST, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.valueNameLabel, 60, SpringLayout.WEST, this.selectedFeatureNumericValuePanel);
                            }
                            {
                                this.valueInfoLabel = new JLabel();
                                this.valueInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.valueInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.valueInfoLabel.text")); 
                                this.selectedFeatureNumericValuePanel.add(this.valueInfoLabel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.valueInfoLabel, 85, SpringLayout.NORTH, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.valueInfoLabel, 50, SpringLayout.NORTH, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.valueInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.valueInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureNumericValuePanel);
                            }
                            {
                                this.valueTextField = new JTextField();
                                this.selectedFeatureNumericValuePanel.add(this.valueTextField);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.valueTextField, -10, SpringLayout.EAST, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.valueTextField, 60, SpringLayout.WEST, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.valueTextField, 85, SpringLayout.NORTH, this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.valueTextField, 50, SpringLayout.NORTH, this.selectedFeatureNumericValuePanel);
                            }
                            {
                                this.selectedFeatureNumericValueInfoPanel = new JPanel();
                                this.selectedFeatureNumericValueInfoPanel.setLayout(new BorderLayout());
                                this.selectedFeatureNumericValuePanel.add(this.selectedFeatureNumericValueInfoPanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectedFeatureNumericValueInfoPanel, -10, SpringLayout.EAST,
                                        this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectedFeatureNumericValueInfoPanel, 10, SpringLayout.WEST,
                                        this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectedFeatureNumericValueInfoPanel, -10, SpringLayout.SOUTH,
                                        this.selectedFeatureNumericValuePanel);
                                this.selectedFeatureNumericValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectedFeatureNumericValueInfoPanel, -35, SpringLayout.SOUTH,
                                        this.selectedFeatureNumericValuePanel);
                                {
                                    this.selectedFeatureNumericValueInfoLabel = new JLabel();
                                    this.selectedFeatureNumericValueInfoLabel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureNumericValueInfoLabel.toolTipText")); 
                                    this.selectedFeatureNumericValueInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                    this.selectedFeatureNumericValueInfoLabel.setOpaque(true);
                                    this.selectedFeatureNumericValueInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                    this.selectedFeatureNumericValueInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureNumericValueInfoLabel.text")); 
                                    this.selectedFeatureNumericValueInfoPanel.add(this.selectedFeatureNumericValueInfoLabel);
                                }
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureTextValuePanel">
                        {
                            this.selectedFeatureTextValuePanel = new JPanel();
                            this.selectedFeatureTextValuePanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTextValuePanel.toolTipText")); 
                            this.selectedFeatureTextValuePanelSpringLayout = new SpringLayout();
                            this.selectedFeatureTextValuePanel.setLayout(this.selectedFeatureTextValuePanelSpringLayout);
                            this.selectedFeatureTextValuePanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTextValuePanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureTextValuePanel, this.selectedFeatureTextValuePanel.getName());
                            {
                                this.textNameInfoLabel = new JLabel();
                                this.textNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.textNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.textNameInfoLabel.text")); 
                                this.selectedFeatureTextValuePanel.add(this.textNameInfoLabel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.textNameInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.textNameInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textNameInfoLabel, 45, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textNameInfoLabel, 10, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                            }
                            {
                                this.textNameLabel = new JLabel();
                                this.textNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.textNameLabel.text")); 
                                this.selectedFeatureTextValuePanel.add(this.textNameLabel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.textNameLabel, -10, SpringLayout.EAST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.textNameLabel, 60, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textNameLabel, 45, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textNameLabel, 10, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                            }
                            {
                                this.textContentInfoLabel = new JLabel();
                                this.textContentInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.textContentInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.textContentInfoLabel.text")); 
                                this.selectedFeatureTextValuePanel.add(this.textContentInfoLabel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textContentInfoLabel, 85, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textContentInfoLabel, 50, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.textContentInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.textContentInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                            }
                            {
                                this.textTextField = new JTextField();
                                this.textTextField.setInheritsPopupMenu(true);
                                this.selectedFeatureTextValuePanel.add(this.textTextField);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textTextField, 85, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textTextField, 50, SpringLayout.NORTH, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.textTextField, -10, SpringLayout.EAST, this.selectedFeatureTextValuePanel);
                                this.selectedFeatureTextValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.textTextField, 60, SpringLayout.WEST, this.selectedFeatureTextValuePanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureTimestampValuePanel">
                        {
                            this.selectedFeatureTimestampValuePanel = new JPanel();
                            this.selectedFeatureTimestampValuePanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTimestampValuePanel.toolTipText")); 
                            this.selectedFeatureDateValuePanelSpringLayout = new SpringLayout();
                            this.selectedFeatureTimestampValuePanel.setLayout(this.selectedFeatureDateValuePanelSpringLayout);
                            this.selectedFeatureTimestampValuePanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTimestampValuePanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureTimestampValuePanel, this.selectedFeatureTimestampValuePanel.getName());
                            {
                                this.timestampNameInfoLabel = new JLabel();
                                this.timestampNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.timestampNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.timestampNameInfoLabel.text")); 
                                this.selectedFeatureTimestampValuePanel.add(this.timestampNameInfoLabel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.timestampNameInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.timestampNameInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.timestampNameInfoLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.timestampNameInfoLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                            }
                            {
                                this.timestampNameLabel = new JLabel();
                                this.timestampNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.timestampNameLabel.text")); 
                                this.selectedFeatureTimestampValuePanel.add(this.timestampNameLabel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.timestampNameLabel, -10, SpringLayout.EAST,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout
                                        .putConstraint(SpringLayout.WEST, this.timestampNameLabel, 60, SpringLayout.WEST, this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.timestampNameLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.timestampNameLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                            }
                            {
                                this.timestampInfoLabel = new JLabel();
                                this.timestampInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.timestampInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.timestampInfoLabel.text")); 
                                this.selectedFeatureTimestampValuePanel.add(this.timestampInfoLabel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.timestampInfoLabel, 85, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.timestampInfoLabel, 50, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout
                                        .putConstraint(SpringLayout.EAST, this.timestampInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout
                                        .putConstraint(SpringLayout.WEST, this.timestampInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureTimestampValuePanel);
                            }
                            {
                                this.timestampTextField = new JTextField();
                                this.selectedFeatureTimestampValuePanel.add(this.timestampTextField);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.timestampTextField, 85, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.timestampTextField, 50, SpringLayout.NORTH,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.timestampTextField, -90, SpringLayout.EAST,
                                        this.selectedFeatureTimestampValuePanel);
                                this.selectedFeatureDateValuePanelSpringLayout
                                        .putConstraint(SpringLayout.WEST, this.timestampTextField, 60, SpringLayout.WEST, this.selectedFeatureTimestampValuePanel);
                            }
                            {
                                this.timestampNowButton = new JButton();
                                this.timestampNowButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.timestampNowButton.toolTipText")); 
                                this.timestampNowButton.setText(GuiMessage.get("CustomPanelValueItemEdit.timestampNowButton.text")); 
                                this.selectedFeatureTimestampValuePanel.add(this.timestampNowButton);
                                selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.EAST, timestampNowButton, -10, SpringLayout.EAST, selectedFeatureTimestampValuePanel);
                                selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.WEST, timestampNowButton, -90, SpringLayout.EAST, selectedFeatureTimestampValuePanel);
                                selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, timestampNowButton, 85, SpringLayout.NORTH, selectedFeatureTimestampValuePanel);
                                selectedFeatureDateValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, timestampNowButton, 50, SpringLayout.NORTH, selectedFeatureTimestampValuePanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureDirectoryValuePanel">
                        {
                            this.selectedFeatureDirectoryValuePanel = new JPanel();
                            this.selectedFeatureDirectoryValuePanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureDirectoryValuePanel.toolTipText")); 
                            this.selectedFeatureDirectoryValuePanelSpringLayout = new SpringLayout();
                            this.selectedFeatureDirectoryValuePanel.setLayout(this.selectedFeatureDirectoryValuePanelSpringLayout);
                            this.selectedFeatureDirectoryValuePanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureDirectoryValuePanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureDirectoryValuePanel, this.selectedFeatureDirectoryValuePanel.getName());
                            {
                                this.directoryNameInfoLabel = new JLabel();
                                this.directoryNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.directoryNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.directoryNameInfoLabel.text")); 
                                this.selectedFeatureDirectoryValuePanel.add(this.directoryNameInfoLabel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.directoryNameInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.directoryNameInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.directoryNameInfoLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.directoryNameInfoLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                            }
                            {
                                this.directoryNameLabel = new JLabel();
                                this.directoryNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.directoryNameLabel.text")); 
                                this.selectedFeatureDirectoryValuePanel.add(this.directoryNameLabel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.directoryNameLabel, -10, SpringLayout.EAST,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.directoryNameLabel, 60, SpringLayout.WEST,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.directoryNameLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.directoryNameLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                            }
                            {
                                this.directoryInfoLabel = new JLabel();
                                this.directoryInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.directoryInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.directoryInfoLabel.text")); 
                                this.selectedFeatureDirectoryValuePanel.add(this.directoryInfoLabel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.directoryInfoLabel, 85, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.directoryInfoLabel, 50, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.directoryInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.directoryInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureDirectoryValuePanel);
                            }
                            {
                                this.directoryTextField = new JTextField();
                                this.selectedFeatureDirectoryValuePanel.add(this.directoryTextField);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.directoryTextField, 85, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.directoryTextField, 50, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.directoryTextField, -90, SpringLayout.EAST,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.directoryTextField, 60, SpringLayout.WEST,
                                        this.selectedFeatureDirectoryValuePanel);
                            }
                            {
                                this.directoryBrowseButton = new JButton();
                                this.directoryBrowseButton.setText(GuiMessage.get("CustomPanelValueItemEdit.directoryBrowseButton.text")); 
                                this.selectedFeatureDirectoryValuePanel.add(this.directoryBrowseButton);
                                selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.EAST, directoryBrowseButton, -10, SpringLayout.EAST, selectedFeatureDirectoryValuePanel);
                                selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.WEST, directoryBrowseButton, -90, SpringLayout.EAST, selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.directoryBrowseButton, 85, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                                this.selectedFeatureDirectoryValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.directoryBrowseButton, 50, SpringLayout.NORTH,
                                        this.selectedFeatureDirectoryValuePanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureFileValuePanel">
                        {
                            this.selectedFeatureFileValuePanel = new JPanel();
                            this.selectedFeatureFileValuePanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureFileValuePanel.toolTipText")); 
                            this.selectedFeatureFileValuePanelSpringLayout = new SpringLayout();
                            this.selectedFeatureFileValuePanel.setLayout(this.selectedFeatureFileValuePanelSpringLayout);
                            this.selectedFeatureFileValuePanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureFileValuePanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureFileValuePanel, this.selectedFeatureFileValuePanel.getName());
                            {
                                this.fileNameInfoLabel = new JLabel();
                                this.fileNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.fileNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.fileNameInfoLabel.text")); 
                                this.selectedFeatureFileValuePanel.add(this.fileNameInfoLabel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.fileNameInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.fileNameInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fileNameInfoLabel, 45, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fileNameInfoLabel, 10, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                            }
                            {
                                this.fileNameLabel = new JLabel();
                                this.fileNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.fileNameLabel.text")); 
                                this.selectedFeatureFileValuePanel.add(this.fileNameLabel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.fileNameLabel, -10, SpringLayout.EAST, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.fileNameLabel, 60, SpringLayout.WEST, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fileNameLabel, 45, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fileNameLabel, 10, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                            }
                            {
                                this.fileInfoLabel = new JLabel();
                                this.fileInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.fileInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.fileInfoLabel.text")); 
                                this.selectedFeatureFileValuePanel.add(this.fileInfoLabel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fileInfoLabel, 85, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fileInfoLabel, 50, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.fileInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.fileInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureFileValuePanel);
                            }
                            {
                                this.fileTextField = new JTextField();
                                this.selectedFeatureFileValuePanel.add(this.fileTextField);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fileTextField, 85, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fileTextField, 50, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.fileTextField, -90, SpringLayout.EAST, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.fileTextField, 60, SpringLayout.WEST, this.selectedFeatureFileValuePanel);
                            }
                            {
                                this.fileBrowseButton = new JButton();
                                this.fileBrowseButton.setText(GuiMessage.get("CustomPanelValueItemEdit.fileBrowseButton.text")); 
                                this.selectedFeatureFileValuePanel.add(this.fileBrowseButton);
                                selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.EAST, fileBrowseButton, -10, SpringLayout.EAST, selectedFeatureFileValuePanel);
                                selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.WEST, fileBrowseButton, -90, SpringLayout.EAST, selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.fileBrowseButton, 85, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                                this.selectedFeatureFileValuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.fileBrowseButton, 50, SpringLayout.NORTH, this.selectedFeatureFileValuePanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureSelectionTextPanel">
                        {
                            this.selectedFeatureSelectionTextPanel = new JPanel();
                            this.selectedFeatureSelectionTextPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureSelectionTextPanel.toolTipText")); 
                            this.selectedFeatureSelectionTextPanelSpringLayout = new SpringLayout();
                            this.selectedFeatureSelectionTextPanel.setLayout(this.selectedFeatureSelectionTextPanelSpringLayout);
                            this.selectedFeatureSelectionTextPanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureSelectionTextPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureSelectionTextPanel, this.selectedFeatureSelectionTextPanel.getName());
                            {
                                this.selectionTextNameInfoLabel = new JLabel();
                                this.selectionTextNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.selectionTextNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.selectionTextNameInfoLabel.text")); 
                                this.selectedFeatureSelectionTextPanel.add(this.selectionTextNameInfoLabel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectionTextNameInfoLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectionTextNameInfoLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectionTextNameInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectionTextNameInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureSelectionTextPanel);
                            }
                            {
                                this.selectionTextNameLabel = new JLabel();
                                this.selectionTextNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.selectionTextNameLabel.text")); 
                                this.selectedFeatureSelectionTextPanel.add(this.selectionTextNameLabel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectionTextNameLabel, -10, SpringLayout.EAST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectionTextNameLabel, 60, SpringLayout.WEST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectionTextNameLabel, 45, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectionTextNameLabel, 10, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                            }
                            {
                                this.selectionTextInfoLabel = new JLabel();
                                this.selectionTextInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.selectionTextInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.selectionTextInfoLabel.text")); 
                                this.selectedFeatureSelectionTextPanel.add(this.selectionTextInfoLabel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectionTextInfoLabel, 50, SpringLayout.WEST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectionTextInfoLabel, 12, SpringLayout.WEST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectionTextInfoLabel, 85, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectionTextInfoLabel, 50, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                            }
                            {
                                this.selectionTextComboBox = new JComboBox();
                                this.selectedFeatureSelectionTextPanel.add(this.selectionTextComboBox);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectionTextComboBox, -10, SpringLayout.EAST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectionTextComboBox, 60, SpringLayout.WEST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectionTextComboBox, 85, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectionTextComboBox, 50, SpringLayout.NORTH,
                                        this.selectedFeatureSelectionTextPanel);
                            }
                            {
                                this.selectedFeatureSelectionTextInfoPanel = new JPanel();
                                this.selectedFeatureSelectionTextInfoPanel.setLayout(new BorderLayout());
                                this.selectedFeatureSelectionTextPanel.add(this.selectedFeatureSelectionTextInfoPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectedFeatureSelectionTextInfoPanel, -10, SpringLayout.EAST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectedFeatureSelectionTextInfoPanel, 10, SpringLayout.WEST,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectedFeatureSelectionTextInfoPanel, -10, SpringLayout.SOUTH,
                                        this.selectedFeatureSelectionTextPanel);
                                this.selectedFeatureSelectionTextPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectedFeatureSelectionTextInfoPanel, -35, SpringLayout.SOUTH,
                                        this.selectedFeatureSelectionTextPanel);
                                {
                                    this.selectedFeatureSelectionTextInfoLabel = new JLabel();
                                    this.selectedFeatureSelectionTextInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                    this.selectedFeatureSelectionTextInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                    this.selectedFeatureSelectionTextInfoLabel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureSelectionTextInfoLabel.toolTipText")); 
                                    this.selectedFeatureSelectionTextInfoLabel.setOpaque(true);
                                    this.selectedFeatureSelectionTextInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureSelectionTextInfoLabel.text")); 
                                    this.selectedFeatureSelectionTextInfoPanel.add(this.selectedFeatureSelectionTextInfoLabel, BorderLayout.CENTER);
                                }
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureMatrixPanel">
                        {
                            this.selectedFeatureMatrixPanel = new JPanel();
                            this.selectedFeatureMatrixPanelBorderLayout = new BorderLayout();
                            this.selectedFeatureMatrixPanelBorderLayout.setVgap(10);
                            this.selectedFeatureMatrixPanel.setLayout(this.selectedFeatureMatrixPanelBorderLayout);
                            this.selectedFeatureMatrixPanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureMatrixPanel, this.selectedFeatureMatrixPanel.getName());

                            // <editor-fold defaultstate="collapsed" desc="matrixDisplayPanel">
                            {
                                this.matrixDisplayPanel = new JPanel();
                                this.matrixDisplayPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.matrixDisplayPanel.toolTipText")); 
                                this.matrixDisplayPanelSpringLayout = new SpringLayout();
                                this.matrixDisplayPanel.setLayout(this.matrixDisplayPanelSpringLayout);
                                this.selectedFeatureMatrixPanel.add(this.matrixDisplayPanel, BorderLayout.CENTER);
                                {
                                    this.matrixNameInfoLabel = new JLabel();
                                    this.matrixNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                    this.matrixNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.matrixNameInfoLabel.text")); 
                                    this.matrixDisplayPanel.add(this.matrixNameInfoLabel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.EAST, this.matrixNameInfoLabel, 50, SpringLayout.WEST, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.WEST, this.matrixNameInfoLabel, 12, SpringLayout.WEST, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.matrixNameInfoLabel, 45, SpringLayout.NORTH, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.matrixNameInfoLabel, 10, SpringLayout.NORTH, this.matrixDisplayPanel);
                                }
                                {
                                    this.matrixNameLabel = new JLabel();
                                    this.matrixNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.matrixNameLabel.text")); 
                                    this.matrixDisplayPanel.add(this.matrixNameLabel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.matrixNameLabel, 45, SpringLayout.NORTH, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.matrixNameLabel, 10, SpringLayout.NORTH, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.EAST, this.matrixNameLabel, -105, SpringLayout.EAST, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.WEST, this.matrixNameLabel, 60, SpringLayout.WEST, this.matrixDisplayPanel);
                                }
                                {
                                    this.matrixDiagramButton = new JButton();
                                    this.matrixDiagramButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.matrixDiagramButton.toolTipText")); 
                                    this.matrixDiagramButton.setText(GuiMessage.get("CustomPanelValueItemEdit.matrixDiagramButton.text")); 
                                    this.matrixDisplayPanel.add(this.matrixDiagramButton);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.matrixDiagramButton, 45, SpringLayout.NORTH, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.matrixDiagramButton, 10, SpringLayout.NORTH, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.EAST, this.matrixDiagramButton, -10, SpringLayout.EAST, this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.WEST, this.matrixDiagramButton, -90, SpringLayout.EAST, this.matrixDisplayPanel);
                                }
                                {
                                    this.selectedFeatureFlexibleMatrixPanel = new CustomPanelValueItemFlexibleMatrix();
                                    this.matrixDisplayPanel.add(this.selectedFeatureFlexibleMatrixPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectedFeatureFlexibleMatrixPanel, 0, SpringLayout.SOUTH,
                                            this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectedFeatureFlexibleMatrixPanel, 40, SpringLayout.NORTH,
                                            this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectedFeatureFlexibleMatrixPanel, 0, SpringLayout.EAST,
                                            this.matrixDisplayPanel);
                                    this.matrixDisplayPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectedFeatureFlexibleMatrixPanel, 0, SpringLayout.WEST,
                                            this.matrixDisplayPanel);
                                }
                            }

                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="tableDataSchemaPanel">
                            {
                                this.tableDataSchemaPanel = new JPanel();
                                this.tableDataSchemaPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("CustomPanelValueItemEdit.tableDataSchemaPanel.title"), 
                                        TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR));
                                this.tableDataSchemaPanelSpringLayout = new SpringLayout();
                                this.tableDataSchemaPanel.setLayout(this.tableDataSchemaPanelSpringLayout);
                                this.tableDataSchemaPanel.setPreferredSize(new Dimension(0, 80));
                                this.tableDataSchemaPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.tableDataSchemaPanel.toolTipText")); 
                                this.selectedFeatureMatrixPanel.add(this.tableDataSchemaPanel, BorderLayout.SOUTH);
                                {
                                    this.setCurrentTableDataSchemaButton = new JButton();
                                    this.setCurrentTableDataSchemaButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.setCurrentTableDataSchemaButton.toolTipText")); 
                                    this.setCurrentTableDataSchemaButton.setText(GuiMessage.get("CustomPanelValueItemEdit.setCurrentTableDataSchemaButton.text")); 
                                    this.tableDataSchemaPanel.add(this.setCurrentTableDataSchemaButton);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.EAST, this.setCurrentTableDataSchemaButton, 90, SpringLayout.WEST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.WEST, this.setCurrentTableDataSchemaButton, 10, SpringLayout.WEST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.setCurrentTableDataSchemaButton, -10, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.setCurrentTableDataSchemaButton, -45, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                }
                                {
                                    this.tableDataSchemaComboBox = new JComboBox();
                                    this.tableDataSchemaComboBox.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.tableDataSchemaComboBox.toolTipText")); 
                                    this.tableDataSchemaPanel.add(this.tableDataSchemaComboBox);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.EAST, this.tableDataSchemaComboBox, -250, SpringLayout.EAST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.WEST, this.tableDataSchemaComboBox, 90, SpringLayout.WEST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.tableDataSchemaComboBox, -10, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.tableDataSchemaComboBox, -45, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                }
                                {
                                    this.showTableDataSchemaButton = new JButton();
                                    this.showTableDataSchemaButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.showTableDataSchemaButton.toolTipText")); 
                                    this.showTableDataSchemaButton.setText(GuiMessage.get("CustomPanelValueItemEdit.showTableDataSchemaButton.text")); 
                                    this.tableDataSchemaPanel.add(this.showTableDataSchemaButton);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.EAST, this.showTableDataSchemaButton, -170, SpringLayout.EAST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.WEST, this.showTableDataSchemaButton, -250, SpringLayout.EAST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.showTableDataSchemaButton, -10, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.showTableDataSchemaButton, -45, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                }
                                {
                                    this.applyTableDataSchemaButton = new JButton();
                                    this.applyTableDataSchemaButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.applyTableDataSchemaButton.toolTipText")); 
                                    this.applyTableDataSchemaButton.setText(GuiMessage.get("CustomPanelValueItemEdit.applyTableDataSchemaButton.text")); 
                                    this.tableDataSchemaPanel.add(this.applyTableDataSchemaButton);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.EAST, this.applyTableDataSchemaButton, -90, SpringLayout.EAST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.WEST, this.applyTableDataSchemaButton, -170, SpringLayout.EAST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.applyTableDataSchemaButton, -10, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.applyTableDataSchemaButton, -45, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                }
                                {
                                    this.removeTableDataSchemaButton = new JButton();
                                    this.removeTableDataSchemaButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.removeTableDataSchemaButton.toolTipText")); 
                                    this.removeTableDataSchemaButton.setText(GuiMessage.get("CustomPanelValueItemEdit.removeTableDataSchemaButton.text")); 
                                    this.tableDataSchemaPanel.add(this.removeTableDataSchemaButton);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.EAST, this.removeTableDataSchemaButton, -10, SpringLayout.EAST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.WEST, this.removeTableDataSchemaButton, -90, SpringLayout.EAST, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.removeTableDataSchemaButton, -10, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                    this.tableDataSchemaPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.removeTableDataSchemaButton, -45, SpringLayout.SOUTH, this.tableDataSchemaPanel);
                                }
                            }
                        }

                        // </editor-fold>
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureCompartmentPanel">
                        {
                            this.selectedFeatureCompartmentPanel = new JPanel();
                            this.selectedFeatureCompartmentPanelSpringLayout = new SpringLayout();
                            this.selectedFeatureCompartmentPanel.setLayout(this.selectedFeatureCompartmentPanelSpringLayout);
                            this.selectedFeatureCompartmentPanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureCompartmentPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureCompartmentPanel, this.selectedFeatureCompartmentPanel.getName());
                            {
                                this.compartmentNameInfoLabel = new JLabel();
                                this.compartmentNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.compartmentNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.compartmentNameInfoLabel.text")); 
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
                                this.compartmentNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.compartmentNameLabel.text")); 
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
                                this.compartmentEditButton = new JButton();
                                this.compartmentEditButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.compartmentEditButton.toolTipText")); 
                                this.compartmentEditButton.setText(GuiMessage.get("CustomPanelValueItemEdit.compartmentEditButton.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.compartmentEditButton);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentEditButton, 85, SpringLayout.NORTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentEditButton, 50, SpringLayout.NORTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentEditButton, 92, SpringLayout.WEST,
                                        this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentEditButton, 12, SpringLayout.WEST,
                                        this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.compartmentRemoveButton = new JButton();
                                this.compartmentRemoveButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.compartmentRemoveButton.toolTipText")); 
                                this.compartmentRemoveButton.setText(GuiMessage.get("CustomPanelValueItemEdit.compartmentRemoveButton.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.compartmentRemoveButton);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentRemoveButton, -10, SpringLayout.EAST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentRemoveButton, -90, SpringLayout.EAST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentRemoveButton, 85, SpringLayout.NORTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentRemoveButton, 50, SpringLayout.NORTH, this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.selectSimulationBoxDisplayComboBox = new JComboBox();
                                this.selectSimulationBoxDisplayComboBox.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectSimulationBoxDisplayComboBox.toolTipText")); 
                                this.selectedFeatureCompartmentPanel.add(this.selectSimulationBoxDisplayComboBox);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectSimulationBoxDisplayComboBox, -90, SpringLayout.EAST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectSimulationBoxDisplayComboBox, -250, SpringLayout.EAST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectSimulationBoxDisplayComboBox, -5, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectSimulationBoxDisplayComboBox, -40, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                            }
                            {
                                this.compartmentViewButton = new JButton();
                                this.compartmentViewButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.compartmentViewButton.toolTipText")); 
                                this.compartmentViewButton.setText(GuiMessage.get("CustomPanelValueItemEdit.compartmentViewButton.text")); 
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
                                this.showBoxPropertiesButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.showBoxPropertiesButton.toolTipText")); 
                                this.showBoxPropertiesButton.setText(GuiMessage.get("CustomPanelValueItemEdit.showBoxPropertiesButton.text")); 
                                this.selectedFeatureCompartmentPanel.add(this.showBoxPropertiesButton);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.showBoxPropertiesButton, 92, SpringLayout.WEST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.showBoxPropertiesButton, 12, SpringLayout.WEST, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.showBoxPropertiesButton, -5, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                                this.selectedFeatureCompartmentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.showBoxPropertiesButton, -40, SpringLayout.SOUTH, this.selectedFeatureCompartmentPanel);
                            }
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="selectedFeatureTextShowPanel">
                        {
                            this.selectedFeatureTextShowPanel = new JPanel();
                            this.selectedFeatureTextShowPanelSpringLayout = new SpringLayout();
                            this.selectedFeatureTextShowPanel.setLayout(this.selectedFeatureTextShowPanelSpringLayout);
                            this.selectedFeatureTextShowPanel.setName(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureTextShowPanel.name")); 
                            this.selectedFeatureCardsPanel.add(this.selectedFeatureTextShowPanel, this.selectedFeatureTextShowPanel.getName());
                            {
                                this.textShowNameInfoLabel = new JLabel();
                                this.textShowNameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.textShowNameInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.textShowNameInfoLabel.text")); 
                                this.selectedFeatureTextShowPanel.add(this.textShowNameInfoLabel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textShowNameInfoLabel, 45, SpringLayout.NORTH, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textShowNameInfoLabel, 10, SpringLayout.NORTH, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.EAST, this.textShowNameInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.WEST, this.textShowNameInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureTextShowPanel);
                            }
                            {
                                this.textShowNameLabel = new JLabel();
                                this.textShowNameLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.textShowNameLabel.text")); 
                                this.selectedFeatureTextShowPanel.add(this.textShowNameLabel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.EAST, this.textShowNameLabel, -10, SpringLayout.EAST, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.WEST, this.textShowNameLabel, 60, SpringLayout.WEST, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textShowNameLabel, 45, SpringLayout.NORTH, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textShowNameLabel, 10, SpringLayout.NORTH, this.selectedFeatureTextShowPanel);
                            }
                            {
                                this.textShowContentInfoLabel = new JLabel();
                                this.textShowContentInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                this.textShowContentInfoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.textShowContentInfoLabel.text")); 
                                this.selectedFeatureTextShowPanel.add(this.textShowContentInfoLabel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.EAST, this.textShowContentInfoLabel, 50, SpringLayout.WEST, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.WEST, this.textShowContentInfoLabel, 12, SpringLayout.WEST, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textShowContentInfoLabel, 85, SpringLayout.NORTH,
                                        this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textShowContentInfoLabel, 50, SpringLayout.NORTH,
                                        this.selectedFeatureTextShowPanel);
                            }
                            {
                                this.textShowContentLabel = new JLabel();
                                this.selectedFeatureTextShowPanel.add(this.textShowContentLabel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.EAST, this.textShowContentLabel, -10, SpringLayout.EAST, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.WEST, this.textShowContentLabel, 60, SpringLayout.WEST, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.textShowContentLabel, 85, SpringLayout.NORTH, this.selectedFeatureTextShowPanel);
                                this.selectedFeatureTextShowPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.textShowContentLabel, 50, SpringLayout.NORTH, this.selectedFeatureTextShowPanel);
                            }
                        }

                        // </editor-fold>
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectedFeatureDescriptionPanel">
                    {
                        this.selectedFeatureDescriptionPanel = new CustomPanelDescription();
                        this.selectedFeatureDescriptionPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureDescriptionPanel.toolTipText")); 
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureDescriptionPanel.title"), null, 
                                this.selectedFeatureDescriptionPanel, null);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectedFeatureHintPanel">
                    {
                        this.selectedFeatureHintPanel = new CustomPanelDescription();
                        this.selectedFeatureHintPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureHintPanel.toolTipText")); 
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureHintPanel.title"), null, 
                                this.selectedFeatureHintPanel, null);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="selectedFeatureErrorPanel">
                    {
                        this.selectedFeatureErrorPanel = new CustomPanelDescription();
                        this.selectedFeatureErrorPanel.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureErrorPanel.toolTipText")); 
                        this.selectedFeatureTabbedPanel.addTab(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureErrorPanel.title"), null, 
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
     * ValueTextField
     * 
     * @return ValueTextField
     */
    public JTextField getValueTextField() {
        return valueTextField;
    }

    /**
     * TextTextField
     * 
     * @return TextTextField
     */
    public JTextField getTextTextField() {
        return textTextField;
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
     * TimestampTextField
     * 
     * @return TimestampTextField
     */
    public JTextField getTimestampTextField() {
        return this.timestampTextField;
    }

    /**
     * TimestampNowButton
     * 
     * @return TimestampNowButton
     */
    public JButton getTimestampNowButton() {
        return this.timestampNowButton;
    }

    /**
     * DirectoryTextField
     * 
     * @return DirectoryTextField
     */
    public JTextField getDirectoryTextField() {
        return directoryTextField;
    }

    /**
     * DirectoryBrowseButton
     * 
     * @return DirectoryBrowseButton
     */
    public JButton getDirectoryBrowseButton() {
        return directoryBrowseButton;
    }

    /**
     * ActivityCheckBox
     * 
     * @return ActivityCheckBox
     */
    public JCheckBox getActivityCheckBox() {
        return activityCheckBox;
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
     * ValueNameLabel
     * 
     * @return ValueNameLabel
     */
    public JLabel getValueNameLabel() {
        return valueNameLabel;
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
     * DirectoryNameLabel
     * 
     * @return DirectoryNameLabel
     */
    public JLabel getDirectoryNameLabel() {
        return directoryNameLabel;
    }

    /**
     * TimestampNameLabel
     * 
     * @return TimestampNameLabel
     */
    public JLabel getTimestampNameLabel() {
        return timestampNameLabel;
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
     * SelectionTextNameLabel
     * 
     * @return SelectionTextNameLabel
     */
    public JLabel getSelectionTextNameLabel() {
        return selectionTextNameLabel;
    }

    /**
     * SelectionTextComboBox
     * 
     * @return SelectionTextComboBox
     */
    public JComboBox getSelectionTextComboBox() {
        return selectionTextComboBox;
    }

    /**
     * SelectedFeatureSelectionTextPanel
     * 
     * @return SelectedFeatureSelectionTextPanel
     */
    public JPanel getSelectedFeatureSelectionTextPanel() {
        return selectedFeatureSelectionTextPanel;
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
     * SelectedFeatureNumericValueInfoLabel
     * 
     * @return SelectedFeatureNumericValueInfoLabel
     */
    public JLabel getSelectedFeatureNumericValueInfoLabel() {
        return selectedFeatureNumericValueInfoLabel;
    }

    /**
     * SelectedFeatureFlexibleMatrixPanel
     * 
     * @return SelectedFeatureFlexibleMatrixPanel
     */
    public CustomPanelValueItemFlexibleMatrix getSelectedFeatureFlexibleMatrixPanel() {
        return selectedFeatureFlexibleMatrixPanel;
    }

    /**
     * SelectedFeatureSelectionTextInfoLabel
     * 
     * @return SelectedFeatureSelectionTextInfoLabel
     */
    public JLabel getSelectedFeatureSelectionTextInfoLabel() {
        return selectedFeatureSelectionTextInfoLabel;
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
     * SelectValueItemStatusComboBox
     * 
     * @return SelectValueItemStatusComboBox
     */
    public JComboBox getSelectValueItemStatusComboBox() {
        return selectValueItemStatusComboBox;
    }

    /**
     * SelectErrorStatusButton
     * 
     * @return SelectErrorStatusButton
     */
    public JButton getSelectErrorStatusButton() {
        return selectErrorStatusButton;
    }

    /**
     * SelectHintStatusButton
     * 
     * @return SelectHintStatusButton
     */
    public JButton getSelectHintStatusButton() {
        return selectHintStatusButton;
    }

    /**
     * SelectValueItemStatusPanel
     * 
     * @return SelectValueItemStatusPanel
     */
    public JPanel getSelectValueItemStatusPanel() {
        return selectValueItemStatusPanel;
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
     * CompartmentImagePanel
     * 
     * @return CompartmentImagePanel
     */
    public CustomPanelImage getCompartmentImagePanel() {
        return compartmentImagePanel;
    }

    /**
     * CompartmentEditButton
     * 
     * @return CompartmentEditButton
     */
    public JButton getCompartmentEditButton() {
        return compartmentEditButton;
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
     * CompartmentRemoveButton
     * 
     * @return CompartmentRemoveButton
     */
    public JButton getCompartmentRemoveButton() {
        return compartmentRemoveButton;
    }

    /**
     * TextShowContentLabel
     * 
     * @return TextShowContentLabel
     */
    public JLabel getTextShowContentLabel() {
        return textShowContentLabel;
    }

    /**
     * TextShowNameLabel
     * 
     * @return TextShowNameLabel
     */
    public JLabel getTextShowNameLabel() {
        return textShowNameLabel;
    }

    /**
     * FileNameLabel
     * 
     * @return FileNameLabel
     */
    public JLabel getFileNameLabel() {
        return fileNameLabel;
    }

    /**
     * FileTextField
     * 
     * @return FileTextField
     */
    public JTextField getFileTextField() {
        return fileTextField;
    }

    /**
     * FileBrowseButton
     * 
     * @return FileBrowseButton
     */
    public JButton getFileBrowseButton() {
        return fileBrowseButton;
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
     * SetCurrentTableDataSchemaButton
     * 
     * @return SetCurrentTableDataSchemaButton
     */
    public JButton getSetCurrentTableDataSchemaButton() {
        return setCurrentTableDataSchemaButton;
    }

    /**
     * TableDataSchemaComboBox
     * 
     * @return TableDataSchemaComboBox
     */
    public JComboBox getTableDataSchemaComboBox() {
        return tableDataSchemaComboBox;
    }

    /**
     * ApplyTableDataSchemaButton
     * 
     * @return ApplyTableDataSchemaButton
     */
    public JButton getApplyTableDataSchemaButton() {
        return applyTableDataSchemaButton;
    }

    /**
     * RemoveTableDataSchemaButton
     * 
     * @return RemoveTableDataSchemaButton
     */
    public JButton getRemoveTableDataSchemaButton() {
        return removeTableDataSchemaButton;
    }

    /**
     * ShowTableDataSchemaButton
     * 
     * @return ShowTableDataSchemaButton
     */
    public JButton getShowTableDataSchemaButton() {
        return showTableDataSchemaButton;
    }

    /**
     * TableDataSchemaPanel
     * 
     * @return TableDataSchemaPanel
     */
    public JPanel getTableDataSchemaPanel() {
        return tableDataSchemaPanel;
    }

    /**
     * ShowBoxPropertiesButton
     * 
     * @return ShowBoxPropertiesButton
     */
    public JButton getShowBoxPropertiesButton() {
        return showBoxPropertiesButton;
    }
    // </editor-fold>
}
