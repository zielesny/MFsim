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
import javax.swing.*;
import javax.swing.JComboBox;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Show panel for structures and monomers
 *
 * @author Achim Zielesny
 */
public class CustomPanelStructureShow extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JComboBox selectPartComboBox;
    private JLabel structureTooComplexLabel;
    private CustomPanelImage particleImagePanel;
    private JPanel molecularStructurePanel;
    private SpringLayout molecularStructurePanelSpringLayout;
    private JPanel particlesPanel;
    private SpringLayout particlesPanelSpringLayout;
    private JScrollPane structureScrollPanel;
    private SpringLayout selectPanelSpringLayout;
    private JPanel selectPanel;
    private JCheckBox reducedCheckBox;
    private JButton graphicsResetButton;
    private JButton graphicsCopyButton;
    private JButton graphicsSaveButton;
    private SingleGraph graph;
    private Viewer graphViewer;
    private ViewPanel graphStreamViewPanel;
    private SpringLayout graphicsPanelSpringLayout;
    private JPanel graphicsPanel;
    private JList particlesInfoList;
    private JScrollPane particlesInfoScrollPanel;
    private JTextArea structureTextArea;
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
    static final long serialVersionUID = 1000000000000000057L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelStructureShow constructor">
    /**
     * Constructor
     */
    public CustomPanelStructureShow() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        // <editor-fold defaultstate="collapsed" desc="molecularStructurePanel">
        {
            this.molecularStructurePanel = new JPanel();
            final TitledBorder jobInputsPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED),
                    GuiMessage.get("CustomPanelStructureShow.molecularStructurePanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR);
            this.molecularStructurePanel.setBorder(jobInputsPanelTitledBorder);
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
                    this.structureTextArea.setWrapStyleWord(true);
                    this.structureTextArea.setLineWrap(true);
                    this.structureTextArea.setOpaque(false);
                    this.structureTextArea.setEditable(false);
                    this.structureScrollPanel.setViewportView(this.structureTextArea);
                }
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
                this.molecularStructurePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectPanel, 195, SpringLayout.NORTH, this.molecularStructurePanel);
                // <editor-fold defaultstate="collapsed" desc="graphicsPanel">
                {
                    this.graphicsPanel = new JPanel();
                    this.graphicsPanelSpringLayout = new SpringLayout();
                    this.graphicsPanel.setLayout(this.graphicsPanelSpringLayout);
                    this.selectPanel.add(this.graphicsPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsPanel, -10, SpringLayout.SOUTH, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsPanel, 10, SpringLayout.NORTH, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsPanel, -10, SpringLayout.EAST, this.selectPanel);
                    this.selectPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsPanel, 10, SpringLayout.WEST, this.selectPanel);
                    {
                        this.graph = new SingleGraph("tmpGraphForGraphStreamViewPanel");
                        this.graphViewer = new Viewer(this.graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
                        this.graphViewer.enableAutoLayout(new SpringBox());
                        this.graphStreamViewPanel = this.graphViewer.addView("graphStreamViewPanel", new J2DGraphRenderer(), false);
                        this.graphicsPanel.add(this.graphStreamViewPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphStreamViewPanel, -40, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphStreamViewPanel, 0, SpringLayout.NORTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphStreamViewPanel, 0, SpringLayout.EAST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphStreamViewPanel, 0, SpringLayout.WEST, this.graphicsPanel);
                    }
                    {
                        this.structureTooComplexLabel = new JLabel();
                        this.structureTooComplexLabel.setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
                        this.structureTooComplexLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        this.structureTooComplexLabel.setText(GuiMessage.get("CustomPanelStructureShow.structureTooComplexLabel.text")); 
                        this.graphicsPanel.add(this.structureTooComplexLabel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.structureTooComplexLabel, 35, SpringLayout.NORTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.structureTooComplexLabel, 0, SpringLayout.NORTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.structureTooComplexLabel, 0, SpringLayout.EAST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.structureTooComplexLabel, 0, SpringLayout.WEST, this.graphicsPanel);
                    }
                    {
                        this.graphicsSaveButton = new JButton();
                        this.graphicsSaveButton.setToolTipText(GuiMessage.get("CustomPanelStructureShow.graphicsSaveButton.toolTipText")); 
                        this.graphicsSaveButton.setText(GuiMessage.get("CustomPanelStructureShow.graphicsSaveButton.text")); 
                        this.graphicsPanel.add(this.graphicsSaveButton);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsSaveButton, 80, SpringLayout.WEST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsSaveButton, 0, SpringLayout.WEST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsSaveButton, 0, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsSaveButton, -35, SpringLayout.SOUTH, this.graphicsPanel);
                    }
                    {
                        this.graphicsCopyButton = new JButton();
                        this.graphicsCopyButton.setToolTipText(GuiMessage.get("CustomPanelStructureShow.graphicsCopyButton.toolTipText")); 
                        this.graphicsCopyButton.setText(GuiMessage.get("CustomPanelStructureShow.graphicsCopyButton.text")); 
                        this.graphicsPanel.add(this.graphicsCopyButton);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsCopyButton, 0, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsCopyButton, -35, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsCopyButton, 160, SpringLayout.WEST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsCopyButton, 80, SpringLayout.WEST, this.graphicsPanel);
                    }
                    {
                        this.selectPartComboBox = new JComboBox();
                        this.selectPartComboBox.setToolTipText(GuiMessage.get("CustomPanelStructureShow.selectPartComboBox.toolTipText")); 
                        this.graphicsPanel.add(this.selectPartComboBox);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectPartComboBox, -210, SpringLayout.EAST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectPartComboBox, 180, SpringLayout.WEST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectPartComboBox, 0, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectPartComboBox, -35, SpringLayout.SOUTH, this.graphicsPanel);
                    }
                    {
                        this.reducedCheckBox = new JCheckBox();
                        this.reducedCheckBox.setToolTipText(GuiMessage.get("CustomPanelStructureShow.reducedCheckBox.toolTipText")); 
                        this.reducedCheckBox.setText(GuiMessage.get("CustomPanelStructureShow.reducedCheckBox.text")); 
                        this.graphicsPanel.add(this.reducedCheckBox);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.reducedCheckBox, 0, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.reducedCheckBox, -35, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.reducedCheckBox, -100, SpringLayout.EAST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.reducedCheckBox, -180, SpringLayout.EAST, this.graphicsPanel);
                    }
                    {
                        this.graphicsResetButton = new JButton();
                        this.graphicsResetButton.setToolTipText(GuiMessage.get("CustomPanelStructureShow.graphicsResetButton.toolTipText")); 
                        this.graphicsResetButton.setText(GuiMessage.get("CustomPanelStructureShow.graphicsResetButton.text")); 
                        this.graphicsPanel.add(this.graphicsResetButton);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.graphicsResetButton, 0, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.graphicsResetButton, -35, SpringLayout.SOUTH, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.graphicsResetButton, 0, SpringLayout.EAST, this.graphicsPanel);
                        this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.graphicsResetButton, -80, SpringLayout.EAST, this.graphicsPanel);
                    }
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
                    GuiMessage.get("CustomPanelStructureShow.particlesPanel.title"), TitledBorder.DEFAULT_JUSTIFICATION, 
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
                this.particleImagePanel.setToolTipText(GuiMessage.get("CustomPanelStructureShow.particleImagePanel.toolTipText")); 
                this.particleImagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.particlesPanel.add(this.particleImagePanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.particleImagePanel, 185, SpringLayout.NORTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.particleImagePanel, 10, SpringLayout.NORTH, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.EAST, this.particleImagePanel, -10, SpringLayout.EAST, this.particlesPanel);
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.WEST, this.particleImagePanel, 10, SpringLayout.WEST, this.particlesPanel);
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
                this.particlesPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.particlesInfoScrollPanel, 195, SpringLayout.NORTH, this.particlesPanel);
                {
                    this.particlesInfoList = new JList();
                    this.particlesInfoList.setToolTipText(GuiMessage.get("CustomPanelStructureShow.particlesInfoList.toolTipText")); 
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
    public JTextArea getStructureTextArea() {
        return structureTextArea;
    }

    public SingleGraph getGraphStreamGraph() {
        return this.graph;
    }

    public Viewer getGraphStreamGraphViewer() {
        return this.graphViewer;
    }

    public ViewPanel getGraphStreamViewPanel() {
        return graphStreamViewPanel;
    }

    public JButton getGraphicsSaveButton() {
        return graphicsSaveButton;
    }

    public JButton getGraphicsCopyButton() {
        return graphicsCopyButton;
    }

    public JButton getGraphicsResetButton() {
        return graphicsResetButton;
    }

    public JCheckBox getReducedCheckBox() {
        return reducedCheckBox;
    }

    public JList getParticlesInfoList() {
        return particlesInfoList;
    }

    public CustomPanelImage getParticleImagePanel() {
        return particleImagePanel;
    }

    public JLabel getStructureTooComplexLabel() {
        return structureTooComplexLabel;
    }

    public JComboBox getSelectPartComboBox() {
        return selectPartComboBox;
    }
    // </editor-fold>

}
