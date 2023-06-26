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
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Input panel for peptides
 *
 * @author Achim Zielesny
 */
public class CustomPanelPeptideEdit extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JPanel peptideInfoActionPanel;
    /**
     * GUI element
     */
    private JPanel peptideInfoActionSubPanel;
    /**
     * GUI element
     */
    private JButton phButton;
    /**
     * GUI element
     */
    private CustomPanelImage aminoAcidImagePanel;
    /**
     * GUI element
     */
    private JButton undoButton;
    /**
     * GUI element
     */
    private JPanel peptidePanel;
    /**
     * GUI element
     */
    private SpringLayout peptidePanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel aminoAcidsPanel;
    /**
     * GUI element
     */
    private SpringLayout aminoAcidsPanelSpringLayout;
    /**
     * GUI element
     */
    private JButton appendAminoAcidButton;
    /**
     * GUI element
     */
    private JButton clearPeptideButton;
    /**
     * GUI element
     */
    private JButton dischargeButton;
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
    private JPanel threeLetterCodePanel;
    /**
     * GUI element
     */
    private SpringLayout threeLetterCodePanelSpringLayout;
    /**
     * GUI element
     */
    private JScrollPane previousDefinitionsScrollPanel;
    /**
     * GUI element
     */
    private JScrollPane peptideScrollPanel;
    /**
     * GUI element
     */
    private SpringLayout selectPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel selectPanel;
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
    private JTabbedPane infoTabbedPanel;
    /**
     * GUI element
     */
    private JTextArea descriptionTextArea;
    /**
     * GUI element
     */
    private JTextArea threeLetterCodeTextArea;
    /**
     * GUI element
     */
    private JList aminoAcidsInfoList;
    /**
     * GUI element
     */
    private JScrollPane descriptionScrollPanel;
    /**
     * GUI element
     */
    private JScrollPane threeLetterCodeScrollPanel;
    /**
     * GUI element
     */
    private JScrollPane aminoAcidsInfoScrollPanel;
    /**
     * GUI element
     */
    private JLabel peptideInfoLabel;
    /**
     * GUI element
     */
    private JTextArea peptideTextArea;
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
    static final long serialVersionUID = 1000000000000000052L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelPeptideEdit constructor">
    /**
     * Create the panel
     */
    public CustomPanelPeptideEdit() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="peptidePanel">

        {
            this.peptidePanel = new JPanel();
            final TitledBorder jobInputsPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED),
                    GuiMessage.get("CustomPanelPeptideEdit.peptidePanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR);
            this.peptidePanel.setBorder(jobInputsPanelTitledBorder);
            this.peptidePanelSpringLayout = new SpringLayout();
            this.peptidePanel.setLayout(this.peptidePanelSpringLayout);
            this.add(this.peptidePanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.peptidePanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.peptidePanel, 10, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.peptidePanel, -285, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.peptidePanel, 10, SpringLayout.WEST, this);

            // <editor-fold defaultstate="collapsed" desc="peptideScrollPanel">

            this.peptideScrollPanel = new JScrollPane();
            this.peptideScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.peptidePanel.add(this.peptideScrollPanel);
            this.peptidePanelSpringLayout.putConstraint(SpringLayout.EAST, this.peptideScrollPanel, -10, SpringLayout.EAST, this.peptidePanel);
            this.peptidePanelSpringLayout.putConstraint(SpringLayout.WEST, this.peptideScrollPanel, 10, SpringLayout.WEST, this.peptidePanel);
            this.peptidePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.peptideScrollPanel, 185, SpringLayout.NORTH, this.peptidePanel);
            this.peptidePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.peptideScrollPanel, 10, SpringLayout.NORTH, this.peptidePanel);
            {
                this.peptideTextArea = new JTextArea();
                this.peptideTextArea.setLineWrap(true);
                this.peptideScrollPanel.setViewportView(this.peptideTextArea);
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="peptideInfoActionPanel">

            {
                this.peptideInfoActionPanel = new JPanel();
                this.peptideInfoActionPanel.setLayout(new BorderLayout(10, 0));
                this.peptidePanel.add(peptideInfoActionPanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.EAST, peptideInfoActionPanel, -10, SpringLayout.EAST, this.peptidePanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.WEST, peptideInfoActionPanel, 15, SpringLayout.WEST, this.peptidePanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.SOUTH, peptideInfoActionPanel, 225, SpringLayout.NORTH, this.peptidePanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.NORTH, peptideInfoActionPanel, 190, SpringLayout.NORTH, this.peptidePanel);

                // <editor-fold defaultstate="collapsed" desc="structureInfoLabel">

                {
                    this.peptideInfoLabel = new JLabel();
                    this.peptideInfoLabel.setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
                    this.peptideInfoLabel.setText(GuiMessage.get("CustomPanelPeptideEdit.peptideInfoLabel.text")); 
                    this.peptideInfoLabel.setMaximumSize(new Dimension(0, 35));
                    this.peptideInfoLabel.setMinimumSize(new Dimension(0, 35));
                    this.peptideInfoLabel.setPreferredSize(new Dimension(0, 35));
                    this.peptideInfoActionPanel.add(this.peptideInfoLabel, BorderLayout.CENTER);
                }

                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="peptideInfoActionSubPanel">

                {
                    this.peptideInfoActionSubPanel = new JPanel();
                    this.peptideInfoActionSubPanel.setLayout(new BorderLayout());
                    this.peptideInfoActionPanel.add(this.peptideInfoActionSubPanel, BorderLayout.LINE_END);

                    // <editor-fold defaultstate="collapsed" desc="dischargeButton">

                    {
                        this.dischargeButton = new JButton();
                        this.dischargeButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.dischargeButton.toolTipText")); 
                        this.dischargeButton.setText(GuiMessage.get("CustomPanelPeptideEdit.dischargeButton.text")); 
                        this.dischargeButton.setMaximumSize(new Dimension(90, 35));
                        this.dischargeButton.setMinimumSize(new Dimension(90, 35));
                        this.dischargeButton.setPreferredSize(new Dimension(90, 35));
                        this.peptideInfoActionSubPanel.add(this.dischargeButton, BorderLayout.LINE_START);
                    }

                    // </editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="clearPeptideButton">

                    {
                        this.clearPeptideButton = new JButton();
                        this.clearPeptideButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.clearPeptideButton.toolTipText")); 
                        this.clearPeptideButton.setText(GuiMessage.get("CustomPanelPeptideEdit.clearPeptideButton.text")); 
                        this.clearPeptideButton.setMaximumSize(new Dimension(90, 35));
                        this.clearPeptideButton.setMinimumSize(new Dimension(90, 35));
                        this.clearPeptideButton.setPreferredSize(new Dimension(90, 35));
                        this.peptideInfoActionSubPanel.add(this.clearPeptideButton, BorderLayout.LINE_END);
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
                this.peptidePanel.add(this.selectPanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectPanel, -10, SpringLayout.EAST, this.peptidePanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectPanel, 10, SpringLayout.WEST, this.peptidePanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectPanel, -10, SpringLayout.SOUTH, this.peptidePanel);
                this.peptidePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectPanel, 235, SpringLayout.NORTH, this.peptidePanel);

                // <editor-fold defaultstate="collapsed" desc="infoTabbedPanel">

                {
                    this.infoTabbedPanel = new JTabbedPane();
                    this.selectPanel.add(this.infoTabbedPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.infoTabbedPanel, -5, SpringLayout.SOUTH, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.infoTabbedPanel, 5, SpringLayout.NORTH, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.EAST, this.infoTabbedPanel, -5, SpringLayout.EAST, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.WEST, this.infoTabbedPanel, 5, SpringLayout.WEST, this.selectPanel);

                    // <editor-fold defaultstate="collapsed" desc="threeLetterCodePanel">

                    {
                        this.threeLetterCodePanel = new JPanel();
                        this.threeLetterCodePanelSpringLayout = new SpringLayout();
                        this.threeLetterCodePanel.setLayout(this.threeLetterCodePanelSpringLayout);
                        this.infoTabbedPanel.addTab(GuiMessage.get("CustomPanelPeptideEdit.threeLetterCodePanel.title"), null, this.threeLetterCodePanel,
                                GuiMessage.get("CustomPanelPeptideEdit.threeLetterCodePanel.tooltip"));  //$NON-NLS-2$
                        {
                            this.threeLetterCodeScrollPanel = new JScrollPane();
                            this.threeLetterCodeScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                            this.threeLetterCodePanel.add(this.threeLetterCodeScrollPanel);
                            this.threeLetterCodePanelSpringLayout.putConstraint(SpringLayout.EAST, this.threeLetterCodeScrollPanel, -10, SpringLayout.EAST, this.threeLetterCodePanel);
                            this.threeLetterCodePanelSpringLayout.putConstraint(SpringLayout.WEST, this.threeLetterCodeScrollPanel, 10, SpringLayout.WEST, this.threeLetterCodePanel);
                            this.threeLetterCodePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.threeLetterCodeScrollPanel, -10, SpringLayout.SOUTH, this.threeLetterCodePanel);
                            this.threeLetterCodePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.threeLetterCodeScrollPanel, 10, SpringLayout.NORTH, this.threeLetterCodePanel);
                            {
                                this.threeLetterCodeTextArea = new JTextArea();
                                this.threeLetterCodeTextArea.setLineWrap(true);
                                this.threeLetterCodeTextArea.setWrapStyleWord(true);
                                this.threeLetterCodeTextArea.setText(GuiMessage.get("CustomPanelPeptideEdit.threeLetterCodeTextArea.text")); 
                                this.threeLetterCodeTextArea.setOpaque(false);
                                this.threeLetterCodeTextArea.setEditable(false);
                                this.threeLetterCodeScrollPanel.setViewportView(this.threeLetterCodeTextArea);
                            }

                        }
                    }

                    // </editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="descriptionScrollPanel">

                    {
                        this.descriptionsPanel = new JPanel();
                        this.descriptionsPanelSpringLayout = new SpringLayout();
                        this.descriptionsPanel.setLayout(this.descriptionsPanelSpringLayout);
                        this.infoTabbedPanel.addTab(GuiMessage.get("CustomPanelPeptideEdit.descriptionsPanel.title"), null, this.descriptionsPanel,
                                GuiMessage.get("CustomPanelPeptideEdit.descriptionsPanel.tooltip"));  //$NON-NLS-2$
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
                                this.descriptionTextArea.setText(GuiMessage.get("CustomPanelPeptideEdit.descriptionTextArea.text")); 
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
                        this.infoTabbedPanel.addTab(GuiMessage.get("CustomPanelPeptideEdit.previousDefinitionsPanel.title"), null, this.previousDefinitionsPanel, GuiMessage.get("CustomPanelPeptideEdit.previousDefinitionsPanel.tooltip"));  //$NON-NLS-2$
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
                                this.previousDefinitionsList.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.previousDefinitionsList.toolTipText")); 
                                this.previousDefinitionsScrollPanel.setViewportView(this.previousDefinitionsList);
                            }
                        }
                        {
                            this.usePreviousDefinitionButton = new JButton();
                            this.usePreviousDefinitionButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.usePreviousDefinitionButton.toolTipText")); 
                            this.usePreviousDefinitionButton.setText(GuiMessage.get("CustomPanelPeptideEdit.usePreviousDefinitionButton.text")); 
                            this.previousDefinitionsPanel.add(this.usePreviousDefinitionButton);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.usePreviousDefinitionButton, -5, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.usePreviousDefinitionButton, -40, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.usePreviousDefinitionButton, 90, SpringLayout.WEST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.usePreviousDefinitionButton, 10, SpringLayout.WEST, this.previousDefinitionsPanel);
                        }
                        {
                            this.removePreviousDefinitionButton = new JButton();
                            this.removePreviousDefinitionButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.removePreviousDefinitionButton.toolTipText")); 
                            this.removePreviousDefinitionButton.setText(GuiMessage.get("CustomPanelPeptideEdit.removePreviousDefinitionButton.text")); 
                            this.previousDefinitionsPanel.add(this.removePreviousDefinitionButton);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.removePreviousDefinitionButton, -10, SpringLayout.EAST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.removePreviousDefinitionButton, -90, SpringLayout.EAST, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.removePreviousDefinitionButton, -5, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                            this.previousDefinitionsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.removePreviousDefinitionButton, -40, SpringLayout.SOUTH, this.previousDefinitionsPanel);
                        }
                        {
                            this.clearPreviousDefinitionsButton = new JButton();
                            this.clearPreviousDefinitionsButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.clearPreviousDefinitionsButton.toolTipText")); 
                            this.clearPreviousDefinitionsButton.setText(GuiMessage.get("CustomPanelPeptideEdit.clearPreviousDefinitionsButton.text")); 
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

        // <editor-fold defaultstate="collapsed" desc="aminoAcidsPanel">

        {
            this.aminoAcidsPanel = new JPanel();
            final TitledBorder jobInputsPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED),
                    GuiMessage.get("CustomPanelPeptideEdit.aminoAcidsPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR);
            this.aminoAcidsPanel.setBorder(jobInputsPanelTitledBorder);
            this.aminoAcidsPanelSpringLayout = new SpringLayout();
            this.aminoAcidsPanel.setLayout(this.aminoAcidsPanelSpringLayout);
            this.add(this.aminoAcidsPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.aminoAcidsPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.aminoAcidsPanel, -280, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.aminoAcidsPanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.aminoAcidsPanel, 10, SpringLayout.NORTH, this);

            // <editor-fold defaultstate="collapsed" desc="aminoAcidImagePanel">

            {
                this.aminoAcidImagePanel = new CustomPanelImage();
                this.aminoAcidImagePanel.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.aminoAcidImagePanel.toolTipText")); 
                this.aminoAcidImagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.aminoAcidsPanel.add(this.aminoAcidImagePanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.aminoAcidImagePanel, 185, SpringLayout.NORTH, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.aminoAcidImagePanel, 10, SpringLayout.NORTH, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.aminoAcidImagePanel, -10, SpringLayout.EAST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.aminoAcidImagePanel, 10, SpringLayout.WEST, this.aminoAcidsPanel);
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="appendAminoAcidButton">

            {
                this.appendAminoAcidButton = new JButton();
                this.appendAminoAcidButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.appendAminoAcidButton.toolTipText")); 
                this.appendAminoAcidButton.setText(GuiMessage.get("CustomPanelPeptideEdit.appendAminoAcidButton.text")); 
                this.aminoAcidsPanel.add(this.appendAminoAcidButton);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.appendAminoAcidButton, -170, SpringLayout.EAST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.appendAminoAcidButton, -250, SpringLayout.EAST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.appendAminoAcidButton, 225, SpringLayout.NORTH, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.appendAminoAcidButton, 190, SpringLayout.NORTH, this.aminoAcidsPanel);
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="phButton">

            {
                this.phButton = new JButton();
                this.phButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.phButton.toolTipText")); 
                this.phButton.setText(GuiMessage.get("CustomPanelPeptideEdit.phButton.text")); 
                this.aminoAcidsPanel.add(this.phButton);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.phButton, -90, SpringLayout.EAST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.phButton, -170, SpringLayout.EAST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.phButton, 225, SpringLayout.NORTH, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.phButton, 190, SpringLayout.NORTH, this.aminoAcidsPanel);
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="undoButton">

            {
                this.undoButton = new JButton();
                this.undoButton.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.undoButton.toolTipText")); 
                this.undoButton.setText(GuiMessage.get("CustomPanelPeptideEdit.undoButton.text")); 
                this.aminoAcidsPanel.add(this.undoButton);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.undoButton, 225, SpringLayout.NORTH, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.undoButton, 190, SpringLayout.NORTH, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.undoButton, -10, SpringLayout.EAST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.undoButton, -90, SpringLayout.EAST, this.aminoAcidsPanel);
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="aminoAcidsInfoScrollPanel">

            {
                this.aminoAcidsInfoScrollPanel = new JScrollPane();
                this.aminoAcidsInfoScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.aminoAcidsPanel.add(this.aminoAcidsInfoScrollPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.aminoAcidsInfoScrollPanel, -10, SpringLayout.EAST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.aminoAcidsInfoScrollPanel, 10, SpringLayout.WEST, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.aminoAcidsInfoScrollPanel, -10, SpringLayout.SOUTH, this.aminoAcidsPanel);
                this.aminoAcidsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.aminoAcidsInfoScrollPanel, 235, SpringLayout.NORTH, this.aminoAcidsPanel);
                {
                    this.aminoAcidsInfoList = new JList();
                    this.aminoAcidsInfoList.setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.aminoAcidsInfoList.toolTipText")); 
                    this.aminoAcidsInfoScrollPanel.setViewportView(this.aminoAcidsInfoList);
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
     * PeptideTextArea
     * 
     * @return PeptideTextArea
     */
    public JTextArea getPeptideTextArea() {
        return peptideTextArea;
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
     * ThreeLetterCodeTextArea
     * 
     * @return ThreeLetterCodeTextArea
     */
    public JTextArea getThreeLetterCodeTextArea() {
        return threeLetterCodeTextArea;
    }

    /**
     * PeptideInfoLabel
     * 
     * @return PeptideInfoLabel
     */
    public JLabel getPeptideInfoLabel() {
        return this.peptideInfoLabel;
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
     * AminoAcidsInfoList
     * 
     * @return AminoAcidsInfoList
     */
    public JList getAminoAcidsInfoList() {
        return aminoAcidsInfoList;
    }

    /**
     * ClearPeptideButton
     * 
     * @return ClearPeptideButton
     */
    public JButton getClearPeptideButton() {
        return clearPeptideButton;
    }

    /**
     * DischargeButton
     * 
     * @return DischargeButton
     */
    public JButton getDischargeButton() {
        return dischargeButton;
    }

    /**
     * AppendAminoAcidButton
     * 
     * @return AppendAminoAcidButton
     */
    public JButton getAppendAminoAcidButton() {
        return this.appendAminoAcidButton;
    }

    /**
     * AminoAcidImagePanel
     * 
     * @return AminoAcidImagePanel
     */
    public CustomPanelImage getAminoAcidImagePanel() {
        return aminoAcidImagePanel;
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
     * PhButton
     * 
     * @return PhButton
     */
    public JButton getPhButton() {
        return phButton;
    }
    // </editor-fold>
}
