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
import de.gnwi.mfsim.model.jmolViewer.Jmol3dPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Input panel for proteins from PDB files
 *
 * @author Achim Zielesny
 */
public class CustomPanelProteinEdit extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JComboBox bioAssemblyComboBox;

    private JButton setCurrentRotationButton;

    private JButton setChainButton;

    private JButton setMutantButton;

    private JButton clearSettingsButton;

    private JButton graphicsPreferencesButton;

    private JButton setProteinRotationButton;

    private FlowLayout proteinDefinitionActionSubPanelFlowLayout;

    private JButton loadPdbFileButton;

    private JButton setProteinPropertiesButton;

    private JPanel proteinDefinitionActionPanel;

    private JPanel proteinDefinitionActionSubPanel;

    private JPanel proteinDefinitionPanel;

    private SpringLayout proteinDefinitionPanelSpringLayout;

    private SpringLayout springLayout;

    private JLabel proteinDefinitionInfoLabel;

    private JPanel proteinPropertyPanel;

    private SpringLayout proteinPropertyPanelSpringLayout;

    private JTabbedPane propertyTabbedPanel;

    private JPanel propertySpicesPanel;

    private SpringLayout propertySpicesPanelSpringLayout;

    private JScrollPane propertySpicesScrollPanel;

    private JTextArea propertySpicesTextArea;

    private JPanel propertySequencesPanel;

    private SpringLayout propertySequencesPanelSpringLayout;

    private JScrollPane propertySequencesScrollPanel;

    private JTextArea propertySequencesTextArea;

    private JPanel property3dStructurePanel;

    private SpringLayout property3dStructurePanelSpringLayout;

    private Jmol3dPanel property3dStructureJmol3dPanel;

    private JPanel propertyPdbFileContentPanel;

    private SpringLayout propertyPdbFileContentPanelSpringLayout;

    private JScrollPane propertyPdbFileContentScrollPanel;

    private JTextArea propertyPdbFileContentTextArea;

    private JButton setPhButton;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000060L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelProteinEdit constructor">
    /**
     * Constructor
     */
    public CustomPanelProteinEdit() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);
        {
            this.proteinDefinitionPanel = new JPanel();
            final TitledBorder proteinDefinitionPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED),
                    GuiMessage.get("CustomPanelProteinEdit.proteinDefinitionPanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION,
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR); 
            this.proteinDefinitionPanel.setBorder(proteinDefinitionPanelTitledBorder);
            this.proteinDefinitionPanelSpringLayout = new SpringLayout();
            this.proteinDefinitionPanel.setLayout(this.proteinDefinitionPanelSpringLayout);
            add(this.proteinDefinitionPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.proteinDefinitionPanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.proteinDefinitionPanel, 10, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.proteinDefinitionPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.proteinDefinitionPanel, 10, SpringLayout.WEST, this);

            // <editor-fold defaultstate="collapsed" desc="proteinDefinitionActionPanel">
            {
                this.proteinDefinitionActionPanel = new JPanel();
                this.proteinDefinitionActionPanel.setLayout(new BorderLayout(10, 0));
                this.proteinDefinitionPanel.add(this.proteinDefinitionActionPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.proteinDefinitionActionPanel, -10, SpringLayout.EAST, this.proteinDefinitionPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.proteinDefinitionActionPanel, 10, SpringLayout.WEST, this.proteinDefinitionPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.proteinDefinitionActionPanel, 45, SpringLayout.NORTH, this.proteinDefinitionPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.proteinDefinitionActionPanel, 10, SpringLayout.NORTH, this.proteinDefinitionPanel);

                // <editor-fold defaultstate="collapsed" desc="proteinDefinitionInfoLabel">
                {
                    this.proteinDefinitionInfoLabel = new JLabel();
                    this.proteinDefinitionInfoLabel.setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
                    this.proteinDefinitionInfoLabel.setText(GuiMessage.get("CustomPanelProteinEdit.proteinDefinitionInfoLabel.text")); 
                    this.proteinDefinitionInfoLabel.setMaximumSize(new Dimension(0, 35));
                    this.proteinDefinitionInfoLabel.setMinimumSize(new Dimension(0, 35));
                    this.proteinDefinitionInfoLabel.setPreferredSize(new Dimension(0, 35));
                    this.proteinDefinitionActionPanel.add(this.proteinDefinitionInfoLabel, BorderLayout.CENTER);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="proteinDefinitionActionSubPanel">
                {
                    this.proteinDefinitionActionSubPanel = new JPanel();
                    this.proteinDefinitionActionSubPanelFlowLayout = new FlowLayout();
                    this.proteinDefinitionActionSubPanelFlowLayout.setVgap(0);
                    this.proteinDefinitionActionSubPanelFlowLayout.setHgap(0);
                    this.proteinDefinitionActionSubPanel.setLayout(this.proteinDefinitionActionSubPanelFlowLayout);
                    this.proteinDefinitionActionPanel.add(this.proteinDefinitionActionSubPanel, BorderLayout.LINE_END);

                    // <editor-fold defaultstate="collapsed" desc="bioAssemblyComboBox">
                    {
                        this.bioAssemblyComboBox = new JComboBox();
                        this.bioAssemblyComboBox.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.bioAssemblyComboBox.toolTipText")); 
                        this.bioAssemblyComboBox.setMaximumSize(new Dimension(160, 35));
                        this.bioAssemblyComboBox.setMinimumSize(new Dimension(160, 35));
                        this.bioAssemblyComboBox.setPreferredSize(new Dimension(160, 35));
                        this.proteinDefinitionActionSubPanel.add(this.bioAssemblyComboBox);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="setChainButton">
                    {
                        this.setChainButton = new JButton();
                        this.setChainButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.setChainButton.toolTipText")); 
                        this.setChainButton.setText(GuiMessage.get("CustomPanelProteinEdit.setChainButton.text")); 
                        this.setChainButton.setMaximumSize(new Dimension(80, 35));
                        this.setChainButton.setMinimumSize(new Dimension(80, 35));
                        this.setChainButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.setChainButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="setMutantButton">
                    {
                        this.setMutantButton = new JButton();
                        this.setMutantButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.setMutantButton.toolTipText")); 
                        this.setMutantButton.setText(GuiMessage.get("CustomPanelProteinEdit.setMutantButton.text")); 
                        this.setMutantButton.setMaximumSize(new Dimension(80, 35));
                        this.setMutantButton.setMinimumSize(new Dimension(80, 35));
                        this.setMutantButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.setMutantButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="setPhButton">
                    {
                        this.setPhButton = new JButton();
                        this.setPhButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.setPhButton.toolTipText")); 
                        this.setPhButton.setText(GuiMessage.get("CustomPanelProteinEdit.setPhButton.text")); 
                        this.setPhButton.setMaximumSize(new Dimension(80, 35));
                        this.setPhButton.setMinimumSize(new Dimension(80, 35));
                        this.setPhButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.setPhButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="setProteinPropertiesButton">
                    {
                        this.setProteinPropertiesButton = new JButton();
                        this.setProteinPropertiesButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.setProteinPropertiesButton.toolTipText")); 
                        this.setProteinPropertiesButton.setText(GuiMessage.get("CustomPanelProteinEdit.setProteinPropertiesButton.text")); 
                        this.setProteinPropertiesButton.setMaximumSize(new Dimension(80, 35));
                        this.setProteinPropertiesButton.setMinimumSize(new Dimension(80, 35));
                        this.setProteinPropertiesButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.setProteinPropertiesButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="setClearButton">
                    {
                        this.clearSettingsButton = new JButton();
                        this.clearSettingsButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.clearSettingsButton.toolTipText")); 
                        this.clearSettingsButton.setText(GuiMessage.get("CustomPanelProteinEdit.clearSettingsButton.text")); 
                        this.clearSettingsButton.setMaximumSize(new Dimension(80, 35));
                        this.clearSettingsButton.setMinimumSize(new Dimension(80, 35));
                        this.clearSettingsButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.clearSettingsButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="loadPdbFileButton">
                    {
                        this.loadPdbFileButton = new JButton();
                        this.loadPdbFileButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.loadPdbFileButton.toolTipText")); 
                        this.loadPdbFileButton.setText(GuiMessage.get("CustomPanelProteinEdit.loadPdbFileButton.text")); 
                        this.loadPdbFileButton.setMaximumSize(new Dimension(80, 35));
                        this.loadPdbFileButton.setMinimumSize(new Dimension(80, 35));
                        this.loadPdbFileButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.loadPdbFileButton);
                    }

                    // </editor-fold>
                }

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="proteinPropertyPanel">
            {
                this.proteinPropertyPanel = new JPanel();
                this.proteinPropertyPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.proteinPropertyPanelSpringLayout = new SpringLayout();
                this.proteinPropertyPanel.setLayout(this.proteinPropertyPanelSpringLayout);
                this.proteinDefinitionPanel.add(this.proteinPropertyPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.proteinPropertyPanel, -10, SpringLayout.EAST, this.proteinDefinitionPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.proteinPropertyPanel, 10, SpringLayout.WEST, this.proteinDefinitionPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.proteinPropertyPanel, -10, SpringLayout.SOUTH, this.proteinDefinitionPanel);
                this.proteinDefinitionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.proteinPropertyPanel, 55, SpringLayout.NORTH, this.proteinDefinitionPanel);

                // <editor-fold defaultstate="collapsed" desc="propertyTabbedPanel">
                {
                    this.propertyTabbedPanel = new JTabbedPane();
                    this.proteinPropertyPanel.add(this.propertyTabbedPanel);
                    this.proteinPropertyPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.propertyTabbedPanel, -5, SpringLayout.SOUTH, this.proteinPropertyPanel);
                    this.proteinPropertyPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.propertyTabbedPanel, 5, SpringLayout.NORTH, this.proteinPropertyPanel);
                    this.proteinPropertyPanelSpringLayout.putConstraint(SpringLayout.EAST, this.propertyTabbedPanel, -5, SpringLayout.EAST, this.proteinPropertyPanel);
                    this.proteinPropertyPanelSpringLayout.putConstraint(SpringLayout.WEST, this.propertyTabbedPanel, 5, SpringLayout.WEST, this.proteinPropertyPanel);

                    // <editor-fold defaultstate="collapsed" desc="property3dStructurePanel">
                    {
                        this.property3dStructurePanel = new JPanel();
                        this.property3dStructurePanelSpringLayout = new SpringLayout();
                        this.property3dStructurePanel.setLayout(this.property3dStructurePanelSpringLayout);
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinEdit.property3dStructurePanel.title"), this.property3dStructurePanel); 
                        {
                            this.property3dStructureJmol3dPanel = new Jmol3dPanel();
                            this.property3dStructurePanel.add(this.property3dStructureJmol3dPanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.property3dStructureJmol3dPanel, -10, SpringLayout.EAST, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.property3dStructureJmol3dPanel, 10, SpringLayout.WEST, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.property3dStructureJmol3dPanel, -45, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.property3dStructureJmol3dPanel, 10, SpringLayout.NORTH, this.property3dStructurePanel);
                        }
                        {
                            this.graphicsPreferencesButton = new JButton();
                            this.graphicsPreferencesButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.graphicsPreferencesButton.toolTipText")); 
                            this.graphicsPreferencesButton.setText(GuiMessage.get("CustomPanelProteinEdit.graphicsPreferencesButton.text")); 
                            this.property3dStructurePanel.add(this.graphicsPreferencesButton);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsPreferencesButton, -5, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsPreferencesButton, -40, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsPreferencesButton, 90, SpringLayout.WEST, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsPreferencesButton, 10, SpringLayout.WEST, this.property3dStructurePanel);
                        }
                        {
                            this.setCurrentRotationButton = new JButton();
                            this.setCurrentRotationButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.setCurrentRotationButton.toolTipText")); 
                            this.setCurrentRotationButton.setText(GuiMessage.get("CustomPanelProteinEdit.setCurrentRotationButton.text")); 
                            this.property3dStructurePanel.add(this.setCurrentRotationButton);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.setCurrentRotationButton, -5, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.setCurrentRotationButton, -40, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.setCurrentRotationButton, -90, SpringLayout.EAST, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.setCurrentRotationButton, -170, SpringLayout.EAST, this.property3dStructurePanel);
                        }
                        {
                            this.setProteinRotationButton = new JButton();
                            this.setProteinRotationButton.setToolTipText(GuiMessage.get("CustomPanelProteinEdit.setProteinRotationButton.toolTipText")); 
                            this.setProteinRotationButton.setText(GuiMessage.get("CustomPanelProteinEdit.setProteinRotationButton.text")); 
                            this.property3dStructurePanel.add(this.setProteinRotationButton);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.setProteinRotationButton, -5, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.setProteinRotationButton, -40, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.setProteinRotationButton, -10, SpringLayout.EAST, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.setProteinRotationButton, -90, SpringLayout.EAST, this.property3dStructurePanel);
                        }
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="propertySequencesPanel">
                    {
                        this.propertySequencesPanel = new JPanel();
                        this.propertySequencesPanelSpringLayout = new SpringLayout();
                        this.propertySequencesPanel.setLayout(this.propertySequencesPanelSpringLayout);
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinEdit.propertySequencesPanel.title"), this.propertySequencesPanel); 
                        {
                            this.propertySequencesScrollPanel = new JScrollPane();
                            this.propertySequencesScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                            this.propertySequencesPanel.add(this.propertySequencesScrollPanel);
                            this.propertySequencesPanelSpringLayout.putConstraint(SpringLayout.EAST, this.propertySequencesScrollPanel, -10, SpringLayout.EAST, this.propertySequencesPanel);
                            this.propertySequencesPanelSpringLayout.putConstraint(SpringLayout.WEST, this.propertySequencesScrollPanel, 10, SpringLayout.WEST, this.propertySequencesPanel);
                            this.propertySequencesPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.propertySequencesScrollPanel, -10, SpringLayout.SOUTH, this.propertySequencesPanel);
                            this.propertySequencesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.propertySequencesScrollPanel, 10, SpringLayout.NORTH, this.propertySequencesPanel);
                            {
                                this.propertySequencesTextArea = new JTextArea();
                                this.propertySequencesTextArea.setLineWrap(true);
                                this.propertySequencesTextArea.setWrapStyleWord(true);
                                this.propertySequencesTextArea.setText(GuiMessage.get("CustomPanelProteinEdit.propertySequencesTextArea.text")); 
                                this.propertySequencesTextArea.setOpaque(false);
                                this.propertySequencesTextArea.setEditable(false);
                                this.propertySequencesScrollPanel.setViewportView(this.propertySequencesTextArea);
                            }

                        }
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="propertySpicesPanel">
                    {
                        this.propertySpicesPanel = new JPanel();
                        this.propertySpicesPanelSpringLayout = new SpringLayout();
                        this.propertySpicesPanel.setLayout(this.propertySpicesPanelSpringLayout);
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinEdit.propertySpicesPanel.title"), this.propertySpicesPanel); 
                        {
                            this.propertySpicesScrollPanel = new JScrollPane();
                            this.propertySpicesScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                            this.propertySpicesPanel.add(this.propertySpicesScrollPanel);
                            this.propertySpicesPanelSpringLayout.putConstraint(SpringLayout.EAST, this.propertySpicesScrollPanel, -10, SpringLayout.EAST, this.propertySpicesPanel);
                            this.propertySpicesPanelSpringLayout.putConstraint(SpringLayout.WEST, this.propertySpicesScrollPanel, 10, SpringLayout.WEST, this.propertySpicesPanel);
                            this.propertySpicesPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.propertySpicesScrollPanel, -10, SpringLayout.SOUTH, this.propertySpicesPanel);
                            this.propertySpicesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.propertySpicesScrollPanel, 10, SpringLayout.NORTH, this.propertySpicesPanel);
                            {
                                this.propertySpicesTextArea = new JTextArea();
                                this.propertySpicesTextArea.setLineWrap(true);
                                this.propertySpicesTextArea.setWrapStyleWord(true);
                                this.propertySpicesTextArea.setText(GuiMessage.get("CustomPanelProteinEdit.propertySpicesTextArea.text")); 
                                this.propertySpicesTextArea.setOpaque(false);
                                this.propertySpicesTextArea.setEditable(false);
                                this.propertySpicesScrollPanel.setViewportView(this.propertySpicesTextArea);
                            }

                        }
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="propertyPdbFileContentPanel">
                    {
                        this.propertyPdbFileContentPanel = new JPanel();
                        this.propertyPdbFileContentPanelSpringLayout = new SpringLayout();
                        this.propertyPdbFileContentPanel.setLayout(this.propertyPdbFileContentPanelSpringLayout);
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinEdit.propertyPdbFileContentPanel.title"), this.propertyPdbFileContentPanel); 
                        {
                            this.propertyPdbFileContentScrollPanel = new JScrollPane();
                            this.propertyPdbFileContentScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                            this.propertyPdbFileContentPanel.add(this.propertyPdbFileContentScrollPanel);
                            this.propertyPdbFileContentPanelSpringLayout.putConstraint(SpringLayout.EAST, this.propertyPdbFileContentScrollPanel, -10, SpringLayout.EAST, this.propertyPdbFileContentPanel);
                            this.propertyPdbFileContentPanelSpringLayout.putConstraint(SpringLayout.WEST, this.propertyPdbFileContentScrollPanel, 10, SpringLayout.WEST, this.propertyPdbFileContentPanel);
                            this.propertyPdbFileContentPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.propertyPdbFileContentScrollPanel, -10, SpringLayout.SOUTH, this.propertyPdbFileContentPanel);
                            this.propertyPdbFileContentPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.propertyPdbFileContentScrollPanel, 10, SpringLayout.NORTH, this.propertyPdbFileContentPanel);
                            {
                                this.propertyPdbFileContentTextArea = new JTextArea();
                                this.propertyPdbFileContentTextArea.setLineWrap(true);
                                this.propertyPdbFileContentTextArea.setWrapStyleWord(true);
                                this.propertyPdbFileContentTextArea.setText(GuiMessage.get("CustomPanelProteinEdit.propertyPdbFileContentTextArea.text")); 
                                this.propertyPdbFileContentTextArea.setOpaque(false);
                                this.propertyPdbFileContentTextArea.setEditable(false);
                                this.propertyPdbFileContentScrollPanel.setViewportView(this.propertyPdbFileContentTextArea);
                            }

                        }
                    }

                    // </editor-fold>
                }

                // </editor-fold>
            }

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">

    public JLabel getProteinDefinitionInfoLabel() {
        return this.proteinDefinitionInfoLabel;
    }

    public JButton getLoadPdbFileButton() {
        return this.loadPdbFileButton;
    }

    public JButton getSetProteinPropertiesButton() {
        return this.setProteinPropertiesButton;
    }

    public JButton getSetPhButton() {
        return this.setPhButton;
    }

    public JButton getSetChainButton() {
        return this.setChainButton;
    }

    public JButton getSetMutantButton() {
        return this.setMutantButton;
    }

    public JTextArea getPropertySpicesTextArea() {
        return this.propertySpicesTextArea;
    }

    public JTextArea getPropertyPdbFileContentTextArea() {
        return this.propertyPdbFileContentTextArea;
    }

    public JTextArea getPropertyTextArea() {
        return this.propertySpicesTextArea;
    }

    public JTextArea getPropertySequencesTextArea() {
        return this.propertySequencesTextArea;
    }

    public JPanel getProteinPropertyPanel() {
        return this.proteinPropertyPanel;
    }

    public Jmol3dPanel getProperty3dStructureJmol3dPanel() {
        return this.property3dStructureJmol3dPanel;
    }

    public JButton getSetProteinRotationButton() {
        return setProteinRotationButton;
    }

    public JButton getGraphicsPreferencesButton() {
        return graphicsPreferencesButton;
    }

    public JButton getSetCurrentRotationButton() {
        return setCurrentRotationButton;
    }

    public JButton getClearSettingsButton() {
        return clearSettingsButton;
    }

    public JComboBox getBioAssemblyComboBox() {
        return bioAssemblyComboBox;
    }
    // </editor-fold>

}
