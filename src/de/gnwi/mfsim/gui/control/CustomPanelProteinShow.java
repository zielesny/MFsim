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
import de.gnwi.mfsim.model.jmolViewer.Jmol3dPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Show panel for proteins
 *
 * @author Achim Zielesny
 */
public class CustomPanelProteinShow extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JLabel bioAssemblyInfoLabel;

    private JPanel proteinDefinitionActionSubPanel;

    private FlowLayout proteinDefinitionActionSubPanelFlowLayout;

    private JButton showChainButton;

    private JButton showPhButton;

    private JButton showProteinPropertiesButton;

    private JButton showMutantButton;

    private JButton graphicsPreferencesButton;

    private JPanel proteinDefinitionActionPanel;

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

    private JButton showProteinRotationButton;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000061L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelProteinShow constructor">
    /**
     * Constructor
     */
    public CustomPanelProteinShow() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);
        {
            this.proteinDefinitionPanel = new JPanel();
            final TitledBorder proteinDefinitionPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED),
                    GuiMessage.get("CustomPanelProteinShow.proteinDefinitionPanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION,
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
                    this.proteinDefinitionInfoLabel.setText(GuiMessage.get("CustomPanelProteinShow.proteinDefinitionInfoLabel.text")); 
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

                    // <editor-fold defaultstate="collapsed" desc="bioAssemblyInfoLabel">
                    {
                        this.bioAssemblyInfoLabel = new JLabel();
                        this.bioAssemblyInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        this.bioAssemblyInfoLabel.setToolTipText(GuiMessage.get("CustomPanelProteinShow.bioAssemblyInfoLabel.toolTipText")); 
                        this.bioAssemblyInfoLabel.setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
                        this.bioAssemblyInfoLabel.setText(GuiMessage.get("CustomPanelProteinShow.bioAssemblyInfoLabel.text")); 
                        this.bioAssemblyInfoLabel.setMaximumSize(new Dimension(160, 35));
                        this.bioAssemblyInfoLabel.setMinimumSize(new Dimension(160, 35));
                        this.bioAssemblyInfoLabel.setPreferredSize(new Dimension(160, 35));
                        this.proteinDefinitionActionSubPanel.add(this.bioAssemblyInfoLabel);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="showChainButton">
                    {
                        this.showChainButton = new JButton();
                        this.showChainButton.setToolTipText(GuiMessage.get("CustomPanelProteinShow.showChainButton.toolTipText")); 
                        this.showChainButton.setText(GuiMessage.get("CustomPanelProteinShow.showChainButton.text")); 
                        this.showChainButton.setMaximumSize(new Dimension(80, 35));
                        this.showChainButton.setMinimumSize(new Dimension(80, 35));
                        this.showChainButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.showChainButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="showMutantButton">
                    {
                        this.showMutantButton = new JButton();
                        this.showMutantButton.setToolTipText(GuiMessage.get("CustomPanelProteinShow.showMutantButton.toolTipText")); 
                        this.showMutantButton.setText(GuiMessage.get("CustomPanelProteinShow.showMutantButton.text")); 
                        this.showMutantButton.setMaximumSize(new Dimension(80, 35));
                        this.showMutantButton.setMinimumSize(new Dimension(80, 35));
                        this.showMutantButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.showMutantButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="showPhButton">
                    {
                        this.showPhButton = new JButton();
                        this.showPhButton.setToolTipText(GuiMessage.get("CustomPanelProteinShow.showPhButton.toolTipText")); 
                        this.showPhButton.setText(GuiMessage.get("CustomPanelProteinShow.showPhButton.text")); 
                        this.showPhButton.setMaximumSize(new Dimension(80, 35));
                        this.showPhButton.setMinimumSize(new Dimension(80, 35));
                        this.showPhButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.showPhButton);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="showProteinPropertiesButton">
                    {
                        this.showProteinPropertiesButton = new JButton();
                        this.showProteinPropertiesButton.setToolTipText(GuiMessage.get("CustomPanelProteinShow.showProteinPropertiesButton.toolTipText")); 
                        this.showProteinPropertiesButton.setText(GuiMessage.get("CustomPanelProteinShow.showProteinPropertiesButton.text")); 
                        this.showProteinPropertiesButton.setMaximumSize(new Dimension(80, 35));
                        this.showProteinPropertiesButton.setMinimumSize(new Dimension(80, 35));
                        this.showProteinPropertiesButton.setPreferredSize(new Dimension(80, 35));
                        this.proteinDefinitionActionSubPanel.add(this.showProteinPropertiesButton);
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
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinShow.property3dStructurePanel.title"), this.property3dStructurePanel); 
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
                            this.graphicsPreferencesButton.setToolTipText(GuiMessage.get("CustomPanelProteinShow.graphicsPreferencesButton.toolTipText")); 
                            this.graphicsPreferencesButton.setText(GuiMessage.get("CustomPanelProteinShow.graphicsPreferencesButton.text")); 
                            this.property3dStructurePanel.add(this.graphicsPreferencesButton);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsPreferencesButton, -5, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsPreferencesButton, -40, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsPreferencesButton, 90, SpringLayout.WEST, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsPreferencesButton, 10, SpringLayout.WEST, this.property3dStructurePanel);
                        }
                        {
                            this.showProteinRotationButton = new JButton();
                            this.showProteinRotationButton.setToolTipText(GuiMessage.get("CustomPanelProteinShow.showProteinRotationButton.toolTipText")); 
                            this.showProteinRotationButton.setText(GuiMessage.get("CustomPanelProteinShow.showProteinRotationButton.text")); 
                            this.property3dStructurePanel.add(this.showProteinRotationButton);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.showProteinRotationButton, -5, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.showProteinRotationButton, -40, SpringLayout.SOUTH, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.EAST, this.showProteinRotationButton, -10, SpringLayout.EAST, this.property3dStructurePanel);
                            this.property3dStructurePanelSpringLayout.putConstraint(SpringLayout.WEST, this.showProteinRotationButton, -90, SpringLayout.EAST, this.property3dStructurePanel);
                        }
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="propertySequencesPanel">
                    {
                        this.propertySequencesPanel = new JPanel();
                        this.propertySequencesPanelSpringLayout = new SpringLayout();
                        this.propertySequencesPanel.setLayout(this.propertySequencesPanelSpringLayout);
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinShow.propertySequencesPanel.title"), this.propertySequencesPanel); 
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
                                this.propertySequencesTextArea.setText(GuiMessage.get("CustomPanelProteinShow.propertySequencesTextArea.text")); 
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
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinShow.propertySpicesPanel.title"), this.propertySpicesPanel); 
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
                                this.propertySpicesTextArea.setText(GuiMessage.get("CustomPanelProteinShow.propertySpicesTextArea.text")); 
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
                        this.propertyTabbedPanel.addTab(GuiMessage.get("CustomPanelProteinShow.propertyPdbFileContentPanel.title"), this.propertyPdbFileContentPanel); 
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
                                this.propertyPdbFileContentTextArea.setText(GuiMessage.get("CustomPanelProteinShow.propertyPdbFileContentTextArea.text")); 
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

    public JButton getGraphicsPreferencesButton() {
        return graphicsPreferencesButton;
    }

    public JButton getShowProteinRotationButton() {
        return showProteinRotationButton;
    }

    public JButton getShowChainButton() {
        return this.showChainButton;
    }

    public JButton getShowPhButton() {
        return this.showPhButton;
    }

    public JButton getShowProteinPropertiesButton() {
        return this.showProteinPropertiesButton;
    }

    public JButton getShowMutantButton() {
        return this.showMutantButton;
    }

    public JLabel getBioAssemblyInfoLabel() {
        return bioAssemblyInfoLabel;
    }

    // </editor-fold>
}
