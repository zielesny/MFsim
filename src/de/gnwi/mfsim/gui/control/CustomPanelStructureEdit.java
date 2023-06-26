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
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Input panel for structures and monomers
 *
 * @author Achim Zielesny
 */
public class CustomPanelStructureEdit extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JComboBox selectPartComboBox;
    /**
     * GUI element
     */
    private JLabel structureTooComplexLabel;
    /**
     * GUI element
     */
    private JButton peptidesButton;
    /**
     * GUI element
     */
    private CustomPanelImage particleImagePanel;
    /**
     * GUI element
     */
    private JButton undoButton;
    /**
     * GUI element
     */
    private JPanel molecularStructurePanel;
    /**
     * GUI element
     */
    private SpringLayout molecularStructurePanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel particlesPanel;
    /**
     * GUI element
     */
    private SpringLayout particlesPanelSpringLayout;
    /**
     * GUI element
     */
    private JButton appendParticleButton;
    /**
     * GUI element
     */
    private JButton clearStructureButton;
    /**
     * GUI element
     */
    private JPanel descriptionsPanel;
    /**
     * GUI element
     */
    private SpringLayout descriptionsPanelSpringLayout;
    /**
     * GUI element
     */
    private JScrollPane previousDefinitionsScrollPanel;
    /**
     * GUI element
     */
    private JScrollPane structureScrollPanel;
    /**
     * GUI element
     */
    private SpringLayout selectPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel structureInfoActionPanel;
    /**
     * GUI element
     */
    private JPanel structureInfoActionSubPanel;
    /**
     * GUI element
     */
    private JPanel selectPanel;
    /**
     * GUI element
     */
    private JCheckBox reducedCheckBox;
    /**
     * GUI element
     */
    private JButton clearPreviousDefinitionsButton;
    /**
     * GUI element
     */
    private JButton removePreviousDefinitionButton;
    /**
     * GUI element
     */
    private JButton usePreviousDefinitionButton;
    /**
     * GUI element
     */
    private JList previousDefinitionsList;
    /**
     * GUI element
     */
    private SpringLayout previousDefinitionsPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel previousDefinitionsPanel;
    /**
     * GUI element
     */
    private JButton graphicsResetButton;
    /**
     * GUI element
     */
    private JButton graphicsCopyButton;
    /**
     * GUI element
     */
    private JButton graphicsSaveButton;
    /**
     * GUI element
     */
    private SingleGraph graph;
    /**
     * GUI element
     */
    private Viewer graphViewer;
    /**
     * GUI element
     */
    private ViewPanel graphStreamViewPanel;
    /**
     * GUI element
     */
    private SpringLayout graphicsPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel graphicsPanel;
    /**
     * GUI element
     */
    private JTabbedPane infoTabbedPanel;
    /**
     * GUI element
     */
    private JTextArea descriptionTextArea;
    /**
     * GUI element
     */
    private JList particlesInfoList;
    /**
     * GUI element
     */
    private JScrollPane descriptionScrollPanel;
    /**
     * GUI element
     */
    private JScrollPane particlesInfoScrollPanel;
    /**
     * GUI element
     */
    private JLabel structureInfoLabel;
    /**
     * GUI element
     */
    private JTextArea structureTextArea;
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
    static final long serialVersionUID = 1000000000000000021L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelStructureEdit constructor">
    /**
     * Constructor
     */
    public CustomPanelStructureEdit() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        // <editor-fold defaultstate="collapsed" desc="molecularStructurePanel">
        {
            this.molecularStructurePanel = new JPanel();
            final TitledBorder molecularStructurePanelTitledBorder = 
                new TitledBorder(
                    new BevelBorder(BevelBorder.RAISED),
                    GuiMessage.get("CustomPanelStructureEdit.molecularStructurePanel.title"), 
                    TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, 
                    null, 
                    GuiDefinitions.PANEL_TITLE_COLOR
                );
            this.molecularStructurePanel.setBorder(molecularStructurePanelTitledBorder);
            this.molecularStructurePanelSpringLayout = new SpringLayout();
            this.molecularStructurePanel.setLayout(this.molecularStructurePanelSpringLayout);
            this.add(this.molecularStructurePanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.molecularStructurePanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.molecularStructurePanel, 10, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.molecularStructurePanel, -285, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.molecularStructurePanel, 10, SpringLayout.WEST, this);
            // <editor-fold defaultstate="collapsed" desc="structureScrollPanel">
            {
                this.structureScrollPanel = new JScrollPane();
                this.structureScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.molecularStructurePanel.add(this.structureScrollPanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.structureScrollPanel, -10, SpringLayout.EAST, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.structureScrollPanel, 10, SpringLayout.WEST, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.structureScrollPanel, 185, SpringLayout.NORTH, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.structureScrollPanel, 10, SpringLayout.NORTH, this.molecularStructurePanel);
                {
                    this.structureTextArea = new JTextArea();
                    this.structureTextArea.setLineWrap(true);
                    this.structureScrollPanel.setViewportView(this.structureTextArea);
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="structureInfoActionPanel">
            {
                this.structureInfoActionPanel = new JPanel();
                this.structureInfoActionPanel.setLayout(new BorderLayout(10, 0));
                this.molecularStructurePanel.add(structureInfoActionPanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, structureInfoActionPanel, -10, SpringLayout.EAST, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, structureInfoActionPanel, 15, SpringLayout.WEST, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, structureInfoActionPanel, 225, SpringLayout.NORTH, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, structureInfoActionPanel, 190, SpringLayout.NORTH, this.molecularStructurePanel);
                // <editor-fold defaultstate="collapsed" desc="structureInfoLabel">
                {
                    this.structureInfoLabel = new JLabel();
                    this.structureInfoLabel.setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
                    this.structureInfoLabel.setText(GuiMessage.get("CustomPanelStructureEdit.structureInfoLabel.text")); 
                    this.structureInfoLabel.setMaximumSize(new Dimension(0, 35));
                    this.structureInfoLabel.setMinimumSize(new Dimension(0, 35));
                    this.structureInfoLabel.setPreferredSize(new Dimension(0, 35));
                    this.structureInfoActionPanel.add(this.structureInfoLabel, BorderLayout.CENTER);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="structureInfoActionSubPanel">
                {
                    this.structureInfoActionSubPanel = new JPanel();
                    this.structureInfoActionSubPanel.setLayout(new BorderLayout());
                    this.structureInfoActionPanel.add(this.structureInfoActionSubPanel, BorderLayout.LINE_END);
                    // <editor-fold defaultstate="collapsed" desc="peptidesButton">
                    {
                        this.peptidesButton = new JButton();
                        this.peptidesButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.peptidesButton.toolTipText")); 
                        this.peptidesButton.setText(GuiMessage.get("CustomPanelStructureEdit.peptidesButton.text")); 
                        this.peptidesButton.setMaximumSize(new Dimension(80, 35));
                        this.peptidesButton.setMinimumSize(new Dimension(80, 35));
                        this.peptidesButton.setPreferredSize(new Dimension(80, 35));
                        this.structureInfoActionSubPanel.add(this.peptidesButton, BorderLayout.LINE_START);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="clearStructureButton">
                    {
                        this.clearStructureButton = new JButton();
                        this.clearStructureButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.clearStructureButton.toolTipText")); 
                        this.clearStructureButton.setText(GuiMessage.get("CustomPanelStructureEdit.clearStructureButton.text")); 
                        this.clearStructureButton.setMaximumSize(new Dimension(80, 35));
                        this.clearStructureButton.setMinimumSize(new Dimension(80, 35));
                        this.clearStructureButton.setPreferredSize(new Dimension(80, 35));
                        this.structureInfoActionSubPanel.add(this.clearStructureButton, BorderLayout.LINE_END);
                    }
                    // </editor-fold>
                }
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="selectPanel">
            {
                this.selectPanel = new JPanel();
                this.selectPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.selectPanelSpringLayout = new SpringLayout();
                this.selectPanel.setLayout(this.selectPanelSpringLayout);
                this.molecularStructurePanel.add(this.selectPanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectPanel, -10, SpringLayout.EAST, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectPanel, 10, SpringLayout.WEST, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectPanel, -10, SpringLayout.SOUTH, this.molecularStructurePanel);
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectPanel, 235, SpringLayout.NORTH, this.molecularStructurePanel);
                // <editor-fold defaultstate="collapsed" desc="infoTabbedPanel">
                {
                    this.infoTabbedPanel = new JTabbedPane();
                    this.selectPanel.add(this.infoTabbedPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.infoTabbedPanel, -5, SpringLayout.SOUTH, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.infoTabbedPanel, 5, SpringLayout.NORTH, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.EAST, this.infoTabbedPanel, -5, SpringLayout.EAST, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.WEST, this.infoTabbedPanel, 5, SpringLayout.WEST, this.selectPanel);
                    // <editor-fold defaultstate="collapsed" desc="graphicsPanel">
                    {
                        this.graphicsPanel = new JPanel();
                        this.graphicsPanelSpringLayout = new SpringLayout();
                        this.graphicsPanel.setLayout(this.graphicsPanelSpringLayout);
                        this.infoTabbedPanel.addTab(GuiMessage.get("CustomPanelStructureEdit.graphicsPanel.title"), null, this.graphicsPanel, GuiMessage.get("CustomPanelStructureEdit.graphicsPanel.tooltip"));  //$NON-NLS-2$
                        {
                            this.graph = new SingleGraph("tmpGraphForGraphStreamViewPanel");
                            this.graphViewer = new Viewer(this.graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
                            this.graphViewer.enableAutoLayout(new SpringBox());
                            this.graphStreamViewPanel = this.graphViewer.addView("graphStreamViewPanel", new J2DGraphRenderer(), false);
                            this.graphicsPanel.add(this.graphStreamViewPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphStreamViewPanel, -45, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphStreamViewPanel, 10, SpringLayout.NORTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphStreamViewPanel, -10, SpringLayout.EAST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphStreamViewPanel, 10, SpringLayout.WEST, this.graphicsPanel);
                        }
                        {
                            this.structureTooComplexLabel = new JLabel();
                            this.structureTooComplexLabel.setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
                            this.structureTooComplexLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            this.structureTooComplexLabel.setText(GuiMessage.get("CustomPanelStructureEdit.structureTooComplexLabel.text")); 
                            this.graphicsPanel.add(this.structureTooComplexLabel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.structureTooComplexLabel, 45, SpringLayout.NORTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.structureTooComplexLabel, 10, SpringLayout.NORTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.structureTooComplexLabel, -10, SpringLayout.EAST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.structureTooComplexLabel, 10, SpringLayout.WEST, this.graphicsPanel);
                        }
                        {
                            this.graphicsSaveButton = new JButton();
                            this.graphicsSaveButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.graphicsSaveButton.toolTipText")); 
                            this.graphicsSaveButton.setText(GuiMessage.get("CustomPanelStructureEdit.graphicsSaveButton.text")); 
                            this.graphicsPanel.add(this.graphicsSaveButton);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsSaveButton, 90, SpringLayout.WEST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsSaveButton, 10, SpringLayout.WEST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsSaveButton, -5, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsSaveButton, -40, SpringLayout.SOUTH, this.graphicsPanel);
                        }
                        {
                            this.graphicsCopyButton = new JButton();
                            this.graphicsCopyButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.graphicsCopyButton.toolTipText")); 
                            this.graphicsCopyButton.setText(GuiMessage.get("CustomPanelStructureEdit.graphicsCopyButton.text")); 
                            this.graphicsPanel.add(this.graphicsCopyButton);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsCopyButton, -5, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsCopyButton, -40, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsCopyButton, 170, SpringLayout.WEST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsCopyButton, 90, SpringLayout.WEST, this.graphicsPanel);
                        }
                        {
                            this.selectPartComboBox = new JComboBox();
                            this.selectPartComboBox.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.selectPartComboBox.toolTipText")); 
                            this.graphicsPanel.add(this.selectPartComboBox);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectPartComboBox, -220, SpringLayout.EAST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectPartComboBox, 190, SpringLayout.WEST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectPartComboBox, -5, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectPartComboBox, -40, SpringLayout.SOUTH, this.graphicsPanel);
                        }
                        {
                            this.reducedCheckBox = new JCheckBox();
                            this.reducedCheckBox.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.reducedCheckBox.toolTipText")); 
                            this.reducedCheckBox.setText(GuiMessage.get("CustomPanelStructureEdit.reducedCheckBox.text")); 
                            this.graphicsPanel.add(this.reducedCheckBox);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.reducedCheckBox, -5, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.reducedCheckBox, -40, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.reducedCheckBox, -110, SpringLayout.EAST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.reducedCheckBox, -190, SpringLayout.EAST, this.graphicsPanel);
                        }
                        {
                            this.graphicsResetButton = new JButton();
                            this.graphicsResetButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.graphicsResetButton.toolTipText")); 
                            this.graphicsResetButton.setText(GuiMessage.get("CustomPanelStructureEdit.graphicsResetButton.text")); 
                            this.graphicsPanel.add(this.graphicsResetButton);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsResetButton, -5, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsResetButton, -40, SpringLayout.SOUTH, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsResetButton, -10, SpringLayout.EAST, this.graphicsPanel);
                            this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsResetButton, -90, SpringLayout.EAST, this.graphicsPanel);
                        }
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="descriptionScrollPanel">

                    {
                        this.descriptionsPanel = new JPanel();
                        this.descriptionsPanelSpringLayout = new SpringLayout();
                        this.descriptionsPanel.setLayout(this.descriptionsPanelSpringLayout);
                        this.infoTabbedPanel.addTab(GuiMessage.get("CustomPanelStructureEdit.descriptionsPanel.title"), null, this.descriptionsPanel, GuiMessage.get("CustomPanelStructureEdit.descriptionsPanel.tooltip"));  //$NON-NLS-2$
                        {
                            this.descriptionScrollPanel = new JScrollPane();
                            this.descriptionScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                            this.descriptionsPanel.add(this.descriptionScrollPanel);
                            this.descriptionsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.descriptionScrollPanel, -10, SpringLayout.EAST, this.descriptionsPanel);
                            this.descriptionsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.descriptionScrollPanel, 10, SpringLayout.WEST, this.descriptionsPanel);
                            this.descriptionsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.descriptionScrollPanel, -10, SpringLayout.SOUTH, this.descriptionsPanel);
                            this.descriptionsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.descriptionScrollPanel, 10, SpringLayout.NORTH, this.descriptionsPanel);
                            {
                                this.descriptionTextArea = new JTextArea();
                                this.descriptionTextArea.setLineWrap(true);
                                this.descriptionTextArea.setWrapStyleWord(true);
                                this.descriptionTextArea.setText(GuiMessage.get("CustomPanelStructureEdit.descriptionTextArea.text")); 
                                this.descriptionTextArea.setOpaque(false);
                                this.descriptionTextArea.setEditable(false);
                                this.descriptionScrollPanel.setViewportView(this.descriptionTextArea);
                            }

                        }
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="previousDefinitionsPanel">

                    {
                        this.previousDefinitionsPanel = new JPanel();
                        this.previousDefinitionsPanelSpringLayout = new SpringLayout();
                        this.previousDefinitionsPanel.setLayout(this.previousDefinitionsPanelSpringLayout);
                        this.infoTabbedPanel.addTab(GuiMessage.get("CustomPanelStructureEdit.previousDefinitionsPanel.title"), null, this.previousDefinitionsPanel, GuiMessage.get("CustomPanelStructureEdit.previousDefinitionsPanel.tooltip"));  //$NON-NLS-2$
                        {
                            this.previousDefinitionsScrollPanel = new JScrollPane();
                            this.previousDefinitionsScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                            this.previousDefinitionsPanel.add(this.previousDefinitionsScrollPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.previousDefinitionsScrollPanel, -10, SpringLayout.EAST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.previousDefinitionsScrollPanel, 10, SpringLayout.WEST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.previousDefinitionsScrollPanel, -45, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.previousDefinitionsScrollPanel, 10, SpringLayout.NORTH, this.previousDefinitionsPanel);
                            {
                                this.previousDefinitionsList = new JList();
                                this.previousDefinitionsList.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.previousDefinitionsList.toolTipText")); 
                                this.previousDefinitionsScrollPanel.setViewportView(this.previousDefinitionsList);
                            }
                        }
                        {
                            this.usePreviousDefinitionButton = new JButton();
                            this.usePreviousDefinitionButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.usePreviousDefinitionButton.toolTipText")); 
                            this.usePreviousDefinitionButton.setText(GuiMessage.get("CustomPanelStructureEdit.usePreviousDefinitionButton.text")); 
                            this.previousDefinitionsPanel.add(this.usePreviousDefinitionButton);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.usePreviousDefinitionButton, -5, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.usePreviousDefinitionButton, -40, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.usePreviousDefinitionButton, 90, SpringLayout.WEST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.usePreviousDefinitionButton, 10, SpringLayout.WEST, this.previousDefinitionsPanel);
                        }
                        {
                            this.removePreviousDefinitionButton = new JButton();
                            this.removePreviousDefinitionButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.removePreviousDefinitionButton.toolTipText")); 
                            this.removePreviousDefinitionButton.setText(GuiMessage.get("CustomPanelStructureEdit.removePreviousDefinitionButton.text")); 
                            this.previousDefinitionsPanel.add(this.removePreviousDefinitionButton);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.removePreviousDefinitionButton, -10, SpringLayout.EAST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.removePreviousDefinitionButton, -90, SpringLayout.EAST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.removePreviousDefinitionButton, -5, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.removePreviousDefinitionButton, -40, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                        }
                        {
                            this.clearPreviousDefinitionsButton = new JButton();
                            this.clearPreviousDefinitionsButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.clearPreviousDefinitionsButton.toolTipText")); 
                            this.clearPreviousDefinitionsButton.setText(GuiMessage.get("CustomPanelStructureEdit.clearPreviousDefinitionsButton.text")); 
                            this.previousDefinitionsPanel.add(this.clearPreviousDefinitionsButton);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.clearPreviousDefinitionsButton, -90, SpringLayout.EAST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.clearPreviousDefinitionsButton, -170, SpringLayout.EAST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.clearPreviousDefinitionsButton, -5, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.clearPreviousDefinitionsButton, -40, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                        }
                    }

                    // </editor-fold>
                }
                // </editor-fold>
            }
            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="particlesPanel">
        {
            this.particlesPanel = new JPanel();
            final TitledBorder jobInputsPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED),
                    GuiMessage.get("CustomPanelStructureEdit.particlesPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR);
            this.particlesPanel.setBorder(jobInputsPanelTitledBorder);
            this.particlesPanelSpringLayout = new SpringLayout();
            this.particlesPanel.setLayout(this.particlesPanelSpringLayout);
            this.add(this.particlesPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.particlesPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.particlesPanel, -280, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.particlesPanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.particlesPanel, 10, SpringLayout.NORTH, this);
            // <editor-fold defaultstate="collapsed" desc="particleImagePanel">
            {
                this.particleImagePanel = new CustomPanelImage();
                this.particleImagePanel.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.particleImagePanel.toolTipText")); 
                this.particleImagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.particlesPanel.add(this.particleImagePanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.particleImagePanel, 185, SpringLayout.NORTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.particleImagePanel, 10, SpringLayout.NORTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.EAST, this.particleImagePanel, -10, SpringLayout.EAST, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.WEST, this.particleImagePanel, 10, SpringLayout.WEST, this.particlesPanel);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="appendParticleButton">
            {
                this.appendParticleButton = new JButton();
                this.appendParticleButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.appendParticleButton.toolTipText")); 
                this.appendParticleButton.setText(GuiMessage.get("CustomPanelStructureEdit.appendParticleButton.text")); 
                this.particlesPanel.add(this.appendParticleButton);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.EAST, this.appendParticleButton, 90, SpringLayout.WEST, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.WEST, this.appendParticleButton, 10, SpringLayout.WEST, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.appendParticleButton, 225, SpringLayout.NORTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.appendParticleButton, 190, SpringLayout.NORTH, this.particlesPanel);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="undoButton">
            {
                this.undoButton = new JButton();
                this.undoButton.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.undoButton.toolTipText")); 
                this.undoButton.setText(GuiMessage.get("CustomPanelStructureEdit.undoButton.text")); 
                this.particlesPanel.add(this.undoButton);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.undoButton, 225, SpringLayout.NORTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.undoButton, 190, SpringLayout.NORTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.EAST, this.undoButton, -10, SpringLayout.EAST, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.WEST, this.undoButton, -90, SpringLayout.EAST, this.particlesPanel);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="particlesInfoScrollPanel">

            {
                this.particlesInfoScrollPanel = new JScrollPane();
                this.particlesInfoScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.particlesPanel.add(this.particlesInfoScrollPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.EAST, this.particlesInfoScrollPanel, -10, SpringLayout.EAST, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.WEST, this.particlesInfoScrollPanel, 10, SpringLayout.WEST, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.particlesInfoScrollPanel, -10, SpringLayout.SOUTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.particlesInfoScrollPanel, 235, SpringLayout.NORTH, this.particlesPanel);
                {
                    this.particlesInfoList = new JList();
                    this.particlesInfoList.setToolTipText(GuiMessage.get("CustomPanelStructureEdit.particlesInfoList.toolTipText")); 
                    this.particlesInfoScrollPanel.setViewportView(this.particlesInfoList);
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
     * StructureTextArea
     * 
     * @return StructureTextArea
     */
    public JTextArea getStructureTextArea() {
        return structureTextArea;
    }

    /**
     * DescriptionTextArea
     * 
     * @return DescriptionTextArea
     */
    public JTextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    /**
     * StructureInfoLabel
     * 
     * @return StructureInfoLabel
     */
    public JLabel getStructureInfoLabel() {
        return this.structureInfoLabel;
    }

    /**
     * GraphStreamGraph
     * 
     * @return GraphStreamGraph
     */
    public SingleGraph getGraphStreamGraph() {
        return this.graph;
    }

    /**
     * GraphStreamGraphViewer
     * 
     * @return GraphStreamGraphViewer
     */
    public Viewer getGraphStreamGraphViewer() {
        return this.graphViewer;
    }

    /**
     * GraphStreamViewPanel
     * 
     * @return GraphStreamViewPanel
     */
    public ViewPanel getGraphStreamViewPanel() {
        return graphStreamViewPanel;
    }

    /**
     * GraphicsSaveButton
     * 
     * @return GraphicsSaveButton
     */
    public JButton getGraphicsSaveButton() {
        return graphicsSaveButton;
    }

    /**
     * GraphicsCopyButton
     * 
     * @return GraphicsCopyButton
     */
    public JButton getGraphicsCopyButton() {
        return graphicsCopyButton;
    }

    /**
     * GraphicsResetButton
     * 
     * @return GraphicsResetButton
     */
    public JButton getGraphicsResetButton() {
        return graphicsResetButton;
    }

    /**
     * InfoTabbedPanel
     * 
     * @return InfoTabbedPanel
     */
    public JTabbedPane getInfoTabbedPanel() {
        return infoTabbedPanel;
    }

    /**
     * PreviousDefinitionsList
     * 
     * @return PreviousDefinitionsList
     */
    public JList getPreviousDefinitionsList() {
        return previousDefinitionsList;
    }

    /**
     * UsePreviousDefinitionButton
     * 
     * @return UsePreviousDefinitionButton
     */
    public JButton getUsePreviousDefinitionButton() {
        return usePreviousDefinitionButton;
    }

    /**
     * RemovePreviousDefinitionButton
     * 
     * @return RemovePreviousDefinitionButton
     */
    public JButton getRemovePreviousDefinitionButton() {
        return removePreviousDefinitionButton;
    }

    /**
     * ClearPreviousDefinitionsButton
     * 
     * @return ClearPreviousDefinitionsButton
     */
    public JButton getClearPreviousDefinitionsButton() {
        return clearPreviousDefinitionsButton;
    }

    /**
     * ReducedCheckBox
     * 
     * @return ReducedCheckBox
     */
    public JCheckBox getReducedCheckBox() {
        return reducedCheckBox;
    }

    /**
     * ParticlesInfoList
     * 
     * @return ParticlesInfoList
     */
    public JList getParticlesInfoList() {
        return particlesInfoList;
    }

    /**
     * ClearStructureButton
     * 
     * @return ClearStructureButton
     */
    public JButton getClearStructureButton() {
        return clearStructureButton;
    }

    /**
     * AppendParticleButton
     * 
     * @return AppendParticleButton
     */
    public JButton getAppendParticleButton() {
        return this.appendParticleButton;
    }

    /**
     * ParticleImagePanel
     * 
     * @return ParticleImagePanel
     */
    public CustomPanelImage getParticleImagePanel() {
        return particleImagePanel;
    }

    /**
     * UndoButton
     * 
     * @return UndoButton
     */
    public JButton getUndoButton() {
        return undoButton;
    }

    /**
     * PeptidesButton
     * 
     * @return PeptidesButton
     */
    public JButton getPeptidesButton() {
        return peptidesButton;
    }

    /**
     * StructureTooComplexLabel
     * 
     * @return StructureTooComplexLabel
     */
    public JLabel getStructureTooComplexLabel() {
        return structureTooComplexLabel;
    }

    /**
     * SelectPartComboBox
     * 
     * @return SelectPartComboBox
     */
    public JComboBox getSelectPartComboBox() {
        return selectPartComboBox;
    }
    // </editor-fold>

}
