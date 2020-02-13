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

import de.gnwi.mfsim.gui.dialog.DialogValueItemShow;
import de.gnwi.mfsim.gui.dialog.DialogValueItemMatrixDiagram;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.dialog.DialogSimulationMovieSlicerShow;
import de.gnwi.mfsim.gui.dialog.DialogJmolViewerShow;
import de.gnwi.mfsim.gui.dialog.DialogProgress;
import de.gnwi.mfsim.gui.dialog.DialogSlicerShow;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionCalculationTask;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.job.TimeStepInfo;
import de.gnwi.mfsim.model.graphics.slice.SlicingTypeEnum;
import de.gnwi.mfsim.model.util.VolumeFrequency;
import de.gnwi.mfsim.gui.dialog.DialogDistributionMovieShow;
import de.gnwi.mfsim.model.job.JobResult;
import de.gnwi.mfsim.model.job.JobUtilityMethods;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumStatus;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Controller class for CustomPanelValueItemShow
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemShowController {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    // Job utility methods
    JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * CustomPanelValueItemShow instance
     */
    private CustomPanelValueItemShow customValueItemShowPanel;

    /**
     * Current value item instance
     */
    private ValueItem currentValueItem;

    /**
     * Value item container
     */
    private ValueItemContainer valueItemContainer;

    /**
     * Controller for this.customValueItemShowPanel
     */
    private CustomPanelValueItemMatrixShowController valueItemMatrixShowPanelController;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomValueItemShowPanel Panel this controller is made for
     * @param aValueItemContainer Value item container
     * @throws IllegalArgumentException Thrown if an argument is null/empty
     */
    public CustomPanelValueItemShowController(CustomPanelValueItemShow aCustomValueItemShowPanel, ValueItemContainer aValueItemContainer) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomValueItemShowPanel == null || aValueItemContainer == null || aValueItemContainer.getSize() == 0) {
            throw new IllegalArgumentException("An argument is null or empty.");
        }

        // </editor-fold>
        try {
            this.customValueItemShowPanel = aCustomValueItemShowPanel;
            this.valueItemContainer = aValueItemContainer;
            // Set combo box for simulation box display selection
            GuiUtils.setComboBoxForTimeStepDisplaySelection(this.customValueItemShowPanel.getSelectTimeStepDisplayComboBox());
            GuiUtils.setComboBoxForSimulationBoxDisplaySelection(this.customValueItemShowPanel.getSelectSimulationBoxDisplayComboBox());
            GuiUtils.setComboBoxForSimulationBoxDisplaySelection(this.customValueItemShowPanel.getSelectSimulationBoxDisplayCompartmentComboBox());

            // NOTE: Add listener FIRST ...
            // <editor-fold defaultstate="collapsed" desc="Add listener">
            this.customValueItemShowPanel.getCollapseTreeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.collapseAllNodesOfTree();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getExpandTreeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.expandAllNodesOfTree();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getFeatureOverviewTree().addTreeSelectionListener(new TreeSelectionListener() {

                public void valueChanged(TreeSelectionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.showSelectedValueItem(e.getNewLeadSelectionPath());
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "valueChanged", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getMatrixDiagramButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.showMatrixDiagram();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getCopyMatrixDiagramButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.copyMatrixDiagram();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getCompartmentShowButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.showCompartments();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getCompartmentViewButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.viewParticlesInCompartments();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getShowBoxPropertiesButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.showBoxProperties();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getSlicerTimeStepPreferencesEditButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.editSlicerTimeStepPreferences();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getViewButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemShowController.this.viewSimulationBoxOrMovies();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed", "CustomPanelValueItemShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getSelectTimeStepDisplayComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        GuiUtils.evaluateComboBoxForTimeStepDisplaySelection(CustomPanelValueItemShowController.this.customValueItemShowPanel.getSelectTimeStepDisplayComboBox());
                    } catch (Exception anExcpetion) {
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelValueItemShowController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getSelectSimulationBoxDisplayComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        GuiUtils.evaluateComboBoxForSimulationBoxDisplaySelection(CustomPanelValueItemShowController.this.customValueItemShowPanel.getSelectSimulationBoxDisplayComboBox());
                    } catch (Exception anExcpetion) {
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelValueItemShowController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customValueItemShowPanel.getSelectSimulationBoxDisplayCompartmentComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        GuiUtils.evaluateComboBoxForSimulationBoxDisplaySelection(CustomPanelValueItemShowController.this.customValueItemShowPanel.getSelectSimulationBoxDisplayCompartmentComboBox());
                    } catch (Exception anExcpetion) {
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelValueItemShowController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // ... then fill tree (otherwise the selection of the first row of the tree will not lead to correct display)
            // <editor-fold defaultstate="collapsed" desc="Fill tree with ACTIVE value items of container">
            GuiUtils.fillTreeWithValueItemsAndSelectFirstLeaf(this.customValueItemShowPanel.getFeatureOverviewTree(), this.valueItemContainer.getSortedValueItemsOfContainer(),
                    ValueItemEnumStatus.ACTIVE);

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelValueItemShowController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Compartment related action methods">
    /**
     * View particles in simulation box
     */
    private void viewParticlesInCompartments() {
        // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Initialize random value generation">
        // Initialize random value generation: This means generating the same graphical particle positions for deterministic random preference
        this.currentValueItem.getCompartmentContainer().getCompartmentBox().initializeRandomValueGeneration();

        // </editor-fold>
        GraphicalParticlePositionCalculationTask tmpProgressTask = new GraphicalParticlePositionCalculationTask(this.currentValueItem.getCompartmentContainer());
        if (!DialogProgress.hasCanceled(GuiMessage.get("GraphicalParticlePositionCalculation"), tmpProgressTask)) {
            // <editor-fold defaultstate="collapsed" desc="Set value item container for molecule/particles display settings in Preferences">
            ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainer
                    = tmpProgressTask.getGraphicalParticlePositionInfo().getGraphicalParticleInfo().getMoleculeDisplaySettingsValueItemContainer();
            ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainerFromJobInput
                    = this.jobUtilityMethods.getMoleculeDisplaySettingsValueItemContainer(this.currentValueItem.getValueItemContainer());
            for (ValueItem tmpMoleculeDisplaySettingsValueItemFromJobInput : tmpMoleculeDisplaySettingsValueItemContainerFromJobInput.getValueItemsOfContainer()) {
                // NOTE: A cloning of tmpMoleculeDisplaySettingsValueItemFromJobInput is NOT necessary since tmpMoleculeDisplaySettingsValueItemContainerFromJobInput already contains cloned value 
                //       items from job input
                tmpMoleculeDisplaySettingsValueItemContainer.replaceValueItemWithKeptVerticalPositionAndNodeNames(tmpMoleculeDisplaySettingsValueItemFromJobInput);
            }
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().setMoleculeDisplaySettingsValueItemContainer(tmpMoleculeDisplaySettingsValueItemContainer);

            // </editor-fold>
            if (Preferences.getInstance().isSimulationBoxSlicer()) {
                DialogSlicerShow.show(String.format(GuiMessage.get("DialogSlicerShow.titleFormat"), this.currentValueItem.getDisplayName()),
                        tmpProgressTask.getGraphicalParticlePositionInfo());
            } else {
                // Save and remove rotation
                Preferences.getInstance().saveAndRemoveRotation();
                DialogJmolViewerShow.show(String.format(GuiMessage.get("DialogJmolViewerShow.titleFormat"), this.currentValueItem.getDisplayName()),
                        tmpProgressTask.getGraphicalParticlePositionInfo());
                // Restore rotation
                Preferences.getInstance().restoreRotation();
            }
        }
    }

    /**
     * Show compartment information
     */
    private void showCompartments() {
        if (this.currentValueItem.hasCompartments()) {
            CompartmentContainer tmpCompartmentContainer = this.currentValueItem.getCompartmentContainer();
            DialogValueItemShow.show(GuiMessage.get("DialogCompartmentShow.title"), tmpCompartmentContainer.getValueItemContainer(), false, false);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Simulation box related action methods">
    /**
     * View simulation box or movies
     */
    private void viewSimulationBoxOrMovies() {
        try {
            Object[] tmpObjectArray = (Object[]) this.currentValueItem.getObject();
            SlicingTypeEnum tmpSlicingType = (SlicingTypeEnum) tmpObjectArray[0];
            ValueItemContainer tmpJobInputValueItemContainer = (ValueItemContainer) tmpObjectArray[2];
            if (tmpSlicingType == SlicingTypeEnum.SIMULATION_BOX || tmpSlicingType == SlicingTypeEnum.SIMULATION_MOVIE) {
                // <editor-fold defaultstate="collapsed" desc="Set value item container for molecule/particles display settings in Preferences if unset">
                if (!Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasMoleculeDisplaySettingsValueItemContainer()) {
                    ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainerFromJobInput = 
                        this.jobUtilityMethods.getMoleculeDisplaySettingsValueItemContainer(tmpJobInputValueItemContainer);
                    Preferences.getInstance().getSimulationMovieSlicerConfiguration().
                        setMoleculeDisplaySettingsValueItemContainer(tmpMoleculeDisplaySettingsValueItemContainerFromJobInput);
                }
                // IMPORTANT: Delete possible compartment related value items which may have survived from earlier viewParticlesInCompartments()
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().getMoleculeDisplaySettingsValueItemContainer().
                    removeValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_BULK");
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().getMoleculeDisplaySettingsValueItemContainer().
                    removeValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_COMPARTMENTS");
                // </editor-fold>
            }
            TimeStepInfo[] tmpFullTimeStepInfoArray;
            TimeStepInfo[] tmpTimeStepInfoArray;
            ExecutorService tmpExecutorService;
            switch (tmpSlicingType) {
                case SIMULATION_BOX:
                    // <editor-fold defaultstate="collapsed" desc="Simulation box slicing">
                    String tmpJobResultParticlePositionsFilePathname = (String) tmpObjectArray[1];
                    // IMPORTANT: tmpJobResultParticlePositionsFilePathname may NOT be available (e.g. if simulation has finished)
                    if (!(new File(tmpJobResultParticlePositionsFilePathname)).isFile()) {
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Error.CanNotFindGraphicalParticlePositions"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                    } else {
                        MouseCursorManagement.getInstance().setWaitCursor();
                        // Maximum of 10 repetitions with 1 second delay for each repetition
                        GraphicalParticlePositionInfo tmpGraphicalParticlePositionInfo = 
                            this.jobUtilityMethods.readGraphicalParticlePositionsWithRepetitions(tmpJobResultParticlePositionsFilePathname,
                                tmpJobInputValueItemContainer,
                                ModelDefinitions.NUMBER_OF_GRAPHICAL_PARTICLE_POSITION_FILE_READ_REPETITIONS,
                                ModelDefinitions.GRAPHICAL_PARTICLE_POSITION_FILE_READ_DELAY
                            );
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        if (tmpGraphicalParticlePositionInfo == null) {
                            JOptionPane.showMessageDialog(null, 
                                GuiMessage.get("Information.GraphicalParticlePositionsAtWork.Message"), 
                                GuiMessage.get("Information.GraphicalParticlePositionsAtWork.Title"),
                                JOptionPane.INFORMATION_MESSAGE
                            );
                        } else if (Preferences.getInstance().isSimulationBoxSlicer()) {
                            // Simulation box slicer
                            DialogSlicerShow.show(String.format(GuiMessage.get("DialogSlicerShow.titleFormat"), 
                                    this.currentValueItem.getDisplayName()
                                ), 
                                tmpGraphicalParticlePositionInfo
                            );
                        } else {
                            // Simulation box Jmol viewer
                            // Save and remove rotation
                            Preferences.getInstance().saveAndRemoveRotation();
                            DialogJmolViewerShow.show(String.format(GuiMessage.get("DialogJmolViewerShow.titleFormat"), 
                                    this.currentValueItem.getDisplayName()
                                ),
                                tmpGraphicalParticlePositionInfo
                            );
                            // Restore rotation
                            Preferences.getInstance().restoreRotation();
                        }
                    }
                    // </editor-fold>
                    break;
                case SIMULATION_MOVIE:
                    // <editor-fold defaultstate="collapsed" desc="Simulation movie slicing">
                    tmpFullTimeStepInfoArray = (TimeStepInfo[]) tmpObjectArray[1];
                    // <editor-fold defaultstate="collapsed" desc="- Set time step range">
                    tmpTimeStepInfoArray = this.getTimeStepInfoArray(tmpFullTimeStepInfoArray);
                    if (tmpTimeStepInfoArray.length < ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS) {
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Information.NotEnoughTimeStepsFormat"), 
                                ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS, 
                                tmpTimeStepInfoArray.length
                            ),
                            GuiMessage.get("Information.NotEnoughTimeStepsTitle"), 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    // </editor-fold>                    
                    // <editor-fold defaultstate="collapsed" desc="- Set time step display">
                    tmpTimeStepInfoArray = this.getTimeStepInfoArrayWithTimeStepDisplay(tmpTimeStepInfoArray);
                    if (tmpTimeStepInfoArray.length < ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS) {
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Information.NotEnoughTimeStepsFormat"), 
                                ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS, 
                                tmpTimeStepInfoArray.length
                            ),
                            GuiMessage.get("Information.NotEnoughTimeStepsTitle"), 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set axis rotation if defined">
                    double tmpRotationAroundXincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundXaxisAngle() / (double) tmpTimeStepInfoArray.length;
                    double tmpRotationAroundYincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundYaxisAngle() / (double) tmpTimeStepInfoArray.length;
                    double tmpRotationAroundZincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundZaxisAngle() / (double) tmpTimeStepInfoArray.length;
                    for (int i = 0; i < tmpTimeStepInfoArray.length; i++) {
                        tmpTimeStepInfoArray[i].setRotationAroundXaxisAngle((double) Preferences.getInstance().getRotationAroundXaxisAngle() + tmpRotationAroundXincrement * (double) i);
                        tmpTimeStepInfoArray[i].setRotationAroundYaxisAngle((double) Preferences.getInstance().getRotationAroundYaxisAngle() + tmpRotationAroundYincrement * (double) i);
                        tmpTimeStepInfoArray[i].setRotationAroundZaxisAngle((double) Preferences.getInstance().getRotationAroundZaxisAngle() + tmpRotationAroundZincrement * (double) i);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set axis shift if defined">
                    double tmpShiftAlongXincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getXshiftChange() / (double) tmpTimeStepInfoArray.length;
                    double tmpShiftAlongYincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getYshiftChange() / (double) tmpTimeStepInfoArray.length;
                    for (int i = 0; i < tmpTimeStepInfoArray.length; i++) {
                        tmpTimeStepInfoArray[i].setXshiftInPixel((int) (Preferences.getInstance().getXshiftInPixelSlicer() + tmpShiftAlongXincrement * (double) i));
                        tmpTimeStepInfoArray[i].setYshiftInPixel((int) (Preferences.getInstance().getYshiftInPixelSlicer() + tmpShiftAlongYincrement * (double) i));
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set particle shift if defined">
                    double tmpParticleShiftXincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeX()/ (double) tmpTimeStepInfoArray.length;
                    double tmpParticleShiftYincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeY()/ (double) tmpTimeStepInfoArray.length;
                    double tmpParticleShiftZincrement = (double) Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeZ()/ (double) tmpTimeStepInfoArray.length;
                    for (int i = 0; i < tmpTimeStepInfoArray.length; i++) {
                        tmpTimeStepInfoArray[i].setParticleShiftX((double) Preferences.getInstance().getParticleShiftX() + tmpParticleShiftXincrement * (double) i);
                        tmpTimeStepInfoArray[i].setParticleShiftY((double) Preferences.getInstance().getParticleShiftY() + tmpParticleShiftYincrement * (double) i);
                        tmpTimeStepInfoArray[i].setParticleShiftZ((double) Preferences.getInstance().getParticleShiftZ() + tmpParticleShiftZincrement * (double) i);
                    }
                    // </editor-fold>
                    tmpExecutorService = Executors.newFixedThreadPool(Preferences.getInstance().getNumberOfParallelSlicers());
                    DialogSimulationMovieSlicerShow.show(tmpExecutorService,
                        GuiMessage.get("DialogSimulationMovieSlicerShow.title"), 
                        tmpTimeStepInfoArray, 
                        tmpJobInputValueItemContainer
                    );
                    try {
                        tmpExecutorService.shutdown();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                    }
                    if (Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundXaxisAngle() != 0
                        || Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundYaxisAngle() != 0
                        || Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundZaxisAngle() != 0
                        || Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getXshiftChange() != 0
                        || Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getYshiftChange() != 0
                        || Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeX() != 0
                        || Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeY() != 0
                        || Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeZ() != 0
                    ) {
                        // <editor-fold defaultstate="collapsed" desc="Rotation">
                        Preferences.getInstance().setRotationAroundXaxisAngle(
                            ((int) (Preferences.getInstance().getRotationAroundXaxisAngle() + 
                                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundXaxisAngle())) % 360
                        );
                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                ((int) (Preferences.getInstance().getRotationAroundYaxisAngle() + 
                                    Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundYaxisAngle())) % 360
                        );
                        Preferences.getInstance().setRotationAroundZaxisAngle(
                            ((int) (Preferences.getInstance().getRotationAroundZaxisAngle() 
                                + Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getRotationChangeAroundZaxisAngle())) % 360
                        );
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Shift">
                        Preferences.getInstance().setXshiftInPixelSlicer(
                            (int) (Preferences.getInstance().getXshiftInPixelSlicer() + 
                                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getXshiftChange())
                        );
                        Preferences.getInstance().setYshiftInPixelSlicer(
                            (int) (Preferences.getInstance().getYshiftInPixelSlicer() + 
                                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getYshiftChange())
                        );
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Particle shift">
                        Preferences.getInstance().setParticleShiftX(
                            ((int) (Preferences.getInstance().getParticleShiftX() + 
                                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeX())) % 100
                        );
                        Preferences.getInstance().setParticleShiftY(
                            ((int) (Preferences.getInstance().getParticleShiftY() + 
                                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeY())) % 100
                        );
                        Preferences.getInstance().setParticleShiftZ(
                            ((int) (Preferences.getInstance().getParticleShiftZ() + 
                                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().getParticleShiftChangeZ())) % 100
                        );
                        // </editor-fold>
                    }
                    // </editor-fold>
                    break;
                case DISTRIBUTION_MOVIE:
                    // <editor-fold defaultstate="collapsed" desc="Distribution movie slicing">
                    tmpFullTimeStepInfoArray = (TimeStepInfo[]) tmpObjectArray[1];
                    // <editor-fold defaultstate="collapsed" desc="- Set time step range">
                    tmpTimeStepInfoArray = this.getTimeStepInfoArray(tmpFullTimeStepInfoArray);
                    if (tmpTimeStepInfoArray.length < ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS) {
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Information.NotEnoughTimeStepsFormat"), 
                                ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS, 
                                tmpTimeStepInfoArray.length
                            ),
                            GuiMessage.get("Information.NotEnoughTimeStepsTitle"), 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    // </editor-fold>                    
                    // <editor-fold defaultstate="collapsed" desc="- Set time step display">
                    tmpTimeStepInfoArray = this.getTimeStepInfoArrayWithTimeStepDisplay(tmpTimeStepInfoArray);
                    if (tmpTimeStepInfoArray.length < ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS) {
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Information.NotEnoughTimeStepsFormat"), 
                                ModelDefinitions.MINIMUM_NUMBER_OF_TIME_STEPS, 
                                tmpTimeStepInfoArray.length
                            ),
                            GuiMessage.get("Information.NotEnoughTimeStepsTitle"), 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    // </editor-fold>
                    VolumeFrequency.VolumeAxis tmpVolumeAxis = (VolumeFrequency.VolumeAxis) tmpObjectArray[3];
                    JobResult.ParticleType tmpParticleType = (JobResult.ParticleType) tmpObjectArray[4];
                    String tmpParticleTypeDescriptionString = (String) tmpObjectArray[5];
                    tmpExecutorService = Executors.newFixedThreadPool(Preferences.getInstance().getNumberOfParallelSlicers());
                    DialogDistributionMovieShow.show(
                        tmpExecutorService,
                        Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasExclusionBoxSizeInfo() 
                            ? GuiMessage.get("DialogDistributionMovieShow.titleWithVolume") : GuiMessage.get("DialogDistributionMovieShow.title"), 
                        tmpTimeStepInfoArray, 
                        tmpJobInputValueItemContainer,
                        tmpVolumeAxis,
                        tmpParticleType,
                        tmpParticleTypeDescriptionString
                    );
                    try {
                        tmpExecutorService.shutdown();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                    }
                    // </editor-fold>
                    break;
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Sets and returns time step info array
     * @param aFullTimeStepInfoArray Full time step info array
     * @return Time step info Array 
     */
    private TimeStepInfo[] getTimeStepInfoArray(TimeStepInfo[] aFullTimeStepInfoArray) {
        // Set time step range
        TimeStepInfo[] tmpTimeStepInfoArray = null;
        if (Preferences.getInstance().getFirstStepInfo() == null) {
            tmpTimeStepInfoArray = aFullTimeStepInfoArray;
        } else {
            LinkedList<TimeStepInfo> tmpTimeStepInfoList = new LinkedList<>();
            int tmpFirstStepIndex = -1;
            int tmpLastStepIndex = -1;
            int tmpIndex = 0;
            boolean tmpIsStart = false;
            for (TimeStepInfo tmpTimeStepInfo : aFullTimeStepInfoArray) {
                boolean tmpIsFirstStepMatch = tmpTimeStepInfo.getStepInfo().equals(Preferences.getInstance().getFirstStepInfo());
                if (tmpIsFirstStepMatch) {
                    tmpFirstStepIndex = tmpIndex;
                }
                boolean tmpIsLastStepMatch = tmpTimeStepInfo.getStepInfo().equals(Preferences.getInstance().getLastStepInfo());
                if (tmpIsLastStepMatch) {
                    tmpLastStepIndex = tmpIndex;
                }
                boolean tmpIsMatch = tmpIsFirstStepMatch || tmpIsLastStepMatch;
                if (tmpIsMatch && tmpIsStart) {
                    tmpTimeStepInfoList.add(tmpTimeStepInfo);
                    break;
                }
                if (tmpIsMatch && !tmpIsStart) {
                    tmpIsStart = true;
                }
                if (tmpIsStart) {
                    tmpTimeStepInfoList.add(tmpTimeStepInfo);
                }
                tmpIndex++;
            }
            if (tmpFirstStepIndex > tmpLastStepIndex) {
                Collections.reverse(tmpTimeStepInfoList);
            }
            tmpTimeStepInfoArray = tmpTimeStepInfoList.toArray(new TimeStepInfo[0]);
        }
        return tmpTimeStepInfoArray;
    }
    
    /**
     * Set and return time step display
     * @param aTimeStepInfo Time step info array
     * @return Time step info array with time step display
     */
    private TimeStepInfo[] getTimeStepInfoArrayWithTimeStepDisplay(TimeStepInfo[] aTimeStepInfoArray) {
        // NOTE: Minimum of time step display MUST be 1
        if (Preferences.getInstance().getTimeStepDisplaySlicer() > 1) {
            LinkedList<TimeStepInfo> tmpTimeStepInfoList = new LinkedList<>();
            aTimeStepInfoArray[0].setBoxViewIndex(0);
            tmpTimeStepInfoList.add(aTimeStepInfoArray[0]);
            int tmpBoxViewIndex = 1;
            boolean tmpIsLastStepAdded = false;
            for (int i = 1; i < aTimeStepInfoArray.length; i += Preferences.getInstance().getTimeStepDisplaySlicer()) {
                TimeStepInfo tmpTimeStepInfo = aTimeStepInfoArray[i];
                tmpTimeStepInfo.setBoxViewIndex(tmpBoxViewIndex++);
                tmpTimeStepInfoList.add(tmpTimeStepInfo);
                if (i == aTimeStepInfoArray.length - 1) {
                    tmpIsLastStepAdded = true;
                }
            }
            if (!tmpIsLastStepAdded) {
                TimeStepInfo tmpTimeStepInfo = aTimeStepInfoArray[aTimeStepInfoArray.length - 1];
                tmpTimeStepInfo.setBoxViewIndex(tmpBoxViewIndex);
                tmpTimeStepInfoList.add(tmpTimeStepInfo);
            }
            return tmpTimeStepInfoList.toArray(new TimeStepInfo[0]);
        } else {
            // Box view indizes may have changed: Reset!
            for (int i = 0; i < aTimeStepInfoArray.length; i++) {
                aTimeStepInfoArray[i].setBoxViewIndex(i);
            }
            return aTimeStepInfoArray;
        }
    }
    
    /**
     *
     */
    private void editSlicerTimeStepPreferences() {
        Object[] tmpObjectArray = (Object[]) this.currentValueItem.getObject();
        SlicingTypeEnum tmpSlicingType = (SlicingTypeEnum) tmpObjectArray[0];
        
        ValueItemContainer tmpValueItemContainer = null;
        if (tmpSlicingType == SlicingTypeEnum.SIMULATION_MOVIE) {
            // <editor-fold defaultstate="collapsed" desc="Simulation movie">
            tmpValueItemContainer = 
                Preferences.getInstance().getSlicerTimeStepsEditablePrefencesValueItemContainer();
            int tmpVerticalPosition = tmpValueItemContainer.getMaximumVerticalPosition() + 1;
            String[] tmpNodeNames = tmpValueItemContainer.getMaximumVerticalPositionValueItemNodeNames();
            GuiUtils.addRotationChangeValueItem(
                tmpValueItemContainer,
                tmpVerticalPosition++,
                tmpNodeNames,
                ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME,
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo()
            );
            GuiUtils.addShiftChangeValueItem(
                tmpValueItemContainer,
                tmpVerticalPosition,
                tmpNodeNames,
                ModelDefinitions.SHIFT_CHANGE_VALUE_ITEM_NAME,
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo()
            );
            GuiUtils.addParticleShiftChangeValueItem(
                tmpValueItemContainer,
                tmpVerticalPosition++,
                tmpNodeNames,
                ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME,
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo()
            );
            // </editor-fold>
        } else if (tmpSlicingType == SlicingTypeEnum.DISTRIBUTION_MOVIE) {
            // <editor-fold defaultstate="collapsed" desc="Distribution movie">
            tmpValueItemContainer = 
                Preferences.getInstance().getSlicerTimeStepsAndVolumeBinsEditablePrefencesValueItemContainer();
            // </editor-fold>
        }
        if (DialogValueItemEdit.hasChanged(
                GuiMessage.get("PreferencesSlicerTimeStepsSettingsDialog.title"),
                tmpValueItemContainer)
            ) {
            Preferences.getInstance().setEditablePreferences(tmpValueItemContainer);
            if (tmpSlicingType == SlicingTypeEnum.SIMULATION_MOVIE) {
                // Set simulation box change info
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setRotationChangeAroundXaxisAngle(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 0));
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setRotationChangeAroundYaxisAngle(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 1));
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setRotationChangeAroundZaxisAngle(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 2));
                
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setXshiftChange(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 0));
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setYshiftChange(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 1));

                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setParticleShiftChangeX(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 0));
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setParticleShiftChangeY(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 1));
                Preferences.getInstance().getTimeStepSimulationBoxChangeInfo().setParticleShiftChangeZ(
                    tmpValueItemContainer.getValueItem(ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 2));
            }
            // Time step display selection may have changed: Set combo box for simulation box display selection
            GuiUtils.setComboBoxForTimeStepDisplaySelection(this.customValueItemShowPanel.getSelectTimeStepDisplayComboBox());
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Show and copy methods">
    /**
     * Shows matrix diagram
     */
    private void showMatrixDiagram() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.currentValueItem == null || !this.currentValueItem.hasValue()) {
            JOptionPane.showMessageDialog(null, GuiMessage.get("Information.NoDataForDiagram"), GuiMessage.get("Information.NotificationTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // </editor-fold>
        DialogValueItemMatrixDiagram.show("Diagram", this.currentValueItem);
    }

    /**
     * Copies matrix diagram data to clipboard
     */
    private void copyMatrixDiagram() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.currentValueItem == null || !this.currentValueItem.hasValue()) {
            JOptionPane.showMessageDialog(null, GuiMessage.get("Information.NoDataForDiagram"), GuiMessage.get("Information.NotificationTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // </editor-fold>
        GuiUtils.copyTextToClipboard(this.currentValueItem.getTabSeparatedDiagramData());
    }

    /**
     * Shows data of selected value item of job data tree
     *
     * @param aSelectedTreePath Selected tree path
     */
    private void showSelectedValueItem(TreePath aSelectedTreePath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectedTreePath == null) {
            this.customValueItemShowPanel.getSelectedFeatureInfoPanel().setVisible(false);
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get card layout and tree node">
        CardLayout tmpCardLayout = (CardLayout) this.customValueItemShowPanel.getSelectedFeatureCardsPanel().getLayout();
        DefaultMutableTreeNode tmpTreeNode = null;
        try {
            tmpTreeNode = (DefaultMutableTreeNode) aSelectedTreePath.getPathComponent(aSelectedTreePath.getPathCount() - 1);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            this.customValueItemShowPanel.getSelectedFeatureInfoPanel().setVisible(false);
            return;
        }

        // </editor-fold>
        if (tmpTreeNode.getUserObject() instanceof ValueItem) {
            // <editor-fold defaultstate="collapsed" desc="Value item">
            // <editor-fold defaultstate="collapsed" desc="Set current value item related items">
            this.currentValueItem = (ValueItem) tmpTreeNode.getUserObject();
            this.customValueItemShowPanel.getSelectedFeatureInfoPanel().setVisible(true);

            // <editor-fold defaultstate="collapsed" desc="Set description">
            this.customValueItemShowPanel.getSelectedFeatureDescriptionPanel().getNameLabel().setText(this.currentValueItem.getDisplayName());
            if (this.currentValueItem.hasDescription()) {
                this.customValueItemShowPanel.getSelectedFeatureDescriptionPanel().getTextArea().setText(this.currentValueItem.getDescription());
            } else {
                this.customValueItemShowPanel.getSelectedFeatureDescriptionPanel().getTextArea().setText(GuiMessage.get("Information.NoDescription"));
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set hint">
            // NOTE: Check if hint tab is not removed
            if (this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().getComponentCount() > 2) {
                if (this.currentValueItem.hasHint()) {
                    this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().setEnabledAt(2, true);
                    this.customValueItemShowPanel.getSelectedFeatureHintPanel().getNameLabel().setText(this.currentValueItem.getDisplayName());
                    this.customValueItemShowPanel.getSelectedFeatureHintPanel().getTextArea().setText(this.currentValueItem.getHint());
                } else {
                    this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().setEnabledAt(2, false);
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set error">
            // NOTE: Check if error tab is not removed
            if (this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().getComponentCount() > 3) {
                if (this.currentValueItem.hasError()) {
                    this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().setEnabledAt(3, true);
                    this.customValueItemShowPanel.getSelectedFeatureErrorPanel().getNameLabel().setText(this.currentValueItem.getDisplayName());
                    this.customValueItemShowPanel.getSelectedFeatureErrorPanel().getTextArea().setText(this.currentValueItem.getError());
                } else {
                    this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().setEnabledAt(3, false);
                }
            }

            // </editor-fold>
            // </editor-fold>
            if (this.currentValueItem.isActive() && !this.currentValueItem.isLocked()) {
                // <editor-fold defaultstate="collapsed" desc="Active unlocked value item">
                // <editor-fold defaultstate="collapsed" desc="Enable feature panel">
                this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().setEnabledAt(0, true);
                this.customValueItemShowPanel.getSelectedFeatureTabbedPanel().setSelectedIndex(0);

                // </editor-fold>
                switch (this.currentValueItem.getBasicType()) {
                    // <editor-fold defaultstate="collapsed" desc="VECTOR, MATRIX, FLEXIBLE_MATRIX">
                    case VECTOR:
                    case MATRIX:
                    case FLEXIBLE_MATRIX:
                        this.customValueItemShowPanel.getMatrixNameLabel().setText(this.currentValueItem.getDisplayName());
                        this.customValueItemShowPanel.getMatrixDiagramButton().setVisible(this.currentValueItem.hasMatrixDiagram());
                        this.customValueItemShowPanel.getCopyMatrixDiagramButton().setVisible(this.currentValueItem.hasMatrixDiagram());
                        if (this.valueItemMatrixShowPanelController == null) {
                            this.valueItemMatrixShowPanelController = new CustomPanelValueItemMatrixShowController(this.customValueItemShowPanel.getSelectedFeatureMatrixShowPanel(), this.currentValueItem);
                        } else {
                            this.valueItemMatrixShowPanelController.setValueItem(this.currentValueItem);
                        }
                        tmpCardLayout.show(this.customValueItemShowPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemShow.selectedFeatureMatrixPanel.name"));
                        break;

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="SCALAR">
                    case SCALAR:
                        switch (this.currentValueItem.getTypeFormat().getDataType()) {
                            // <editor-fold defaultstate="collapsed" desc="MOLECULAR_STRUCTURE, MONOMER_STRUCTURE">
                            case MOLECULAR_STRUCTURE:
                            case MONOMER_STRUCTURE:
                                // Not defined for SCALAR value item
                                tmpCardLayout.show(this.customValueItemShowPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemShow.selectedFeatureShowNothingPanel.name"));
                                break;

                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="SELECTION_TEXT, TEXT, NUMERIC etc.">
                            case TEXT:
                            case NUMERIC:
                            case NUMERIC_NULL:
                            case DIRECTORY:
                            case FILE:
                            case TIMESTAMP:
                            case TIMESTAMP_EMPTY:
                            case SELECTION_TEXT:
                                // <editor-fold defaultstate="collapsed" desc="- Set label and value">
                                this.customValueItemShowPanel.getTextNameLabel().setText(this.currentValueItem.getDisplayName());
                                this.customValueItemShowPanel.getContentLabel().setText(this.currentValueItem.getValue());
                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="- Show card">
                                tmpCardLayout.show(this.customValueItemShowPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemShow.selectedFeatureTextValuePanel.name"));
                                // </editor-fold>
                                break;
                            // </editor-fold>                            // </editor-fold>
                        }
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="COMPARTMENT_CONTAINER">
                    case COMPARTMENT_CONTAINER:
                        // <editor-fold defaultstate="collapsed" desc="Settings">
                        this.customValueItemShowPanel.getCompartmentNameLabel().setText(this.currentValueItem.getDisplayName());
                        this.updateCompartmentPanel();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Show card">
                        tmpCardLayout.show(this.customValueItemShowPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemShow.selectedFeatureCompartmentPanel.name"));

                        // </editor-fold>
                        break;

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="OBJECT">
                    case OBJECT:
                        // <editor-fold defaultstate="collapsed" desc="Settings">
                        this.customValueItemShowPanel.getSimulationBoxNameLabel().setText(this.currentValueItem.getDisplayName());
                        this.updateSimulationBoxPanel();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Show card">
                        tmpCardLayout.show(this.customValueItemShowPanel.getSelectedFeatureCardsPanel(), GuiMessage.get("CustomPanelValueItemShow.selectedFeatureSimulationBoxPanel.name"));
                        // </editor-fold>
                        break;
                    // </editor-fold>                    // </editor-fold>
                }
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Inactive and/or locked value item">
                this.customValueItemShowPanel.getSelectedFeatureInfoPanel().setVisible(false);

                // </editor-fold>
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="No value item">
            this.customValueItemShowPanel.getSelectedFeatureInfoPanel().setVisible(false);

            // </editor-fold>
        }
    }

    /**
     * Shows box properties
     */
    private void showBoxProperties() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpJobInputValueItemContainer = this.currentValueItem.getValueItemContainer();
            ValueItemContainer tmpBoxPropertiesValueItemContainer = this.jobUtilityMethods.getBoxPropertiesSummaryValueItemContainer(tmpJobInputValueItemContainer);
            if (tmpBoxPropertiesValueItemContainer == null) {
                JOptionPane.showMessageDialog(null, GuiMessage.get("Information.NoBoxProperties"), GuiMessage.get("Information.NotificationTitle"),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                MouseCursorManagement.getInstance().setDefaultCursor();
                DialogValueItemShow.show(GuiMessage.get("ShowBoxProperties.Title"), tmpBoxPropertiesValueItemContainer, false, false);
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    /**
     * Updates compartment panel
     */
    private void updateCompartmentPanel() {
        try {
            if (this.currentValueItem != null) {
                // Set wait cursor since graphics panel operation may cause a delay
                MouseCursorManagement.getInstance().setWaitCursor();
                if (!this.customValueItemShowPanel.getCompartmentImagePanel().hasBasicImage()) {
                    GuiUtils.setCompartmentsImage(this.customValueItemShowPanel.getCompartmentImagePanel());
                }
                this.customValueItemShowPanel.getCompartmentShowButton().setVisible(this.currentValueItem.hasCompartments());
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Updates simulation box panel
     */
    private void updateSimulationBoxPanel() {
        try {
            if (this.currentValueItem != null) {
                // Set wait cursor since graphics panel operation may cause a delay
                MouseCursorManagement.getInstance().setWaitCursor();
                // Set visibility of simulation box display selection
                Object[] tmpObjectArray = (Object[]) this.currentValueItem.getObject();
                SlicingTypeEnum tmpSlicingType = (SlicingTypeEnum) tmpObjectArray[0];
                // Set image
                switch (tmpSlicingType) {
                    case SIMULATION_BOX:
                        GuiUtils.setSimulationBoxImage(this.customValueItemShowPanel.getSimulationBoxImagePanel());
                        break;
                    case SIMULATION_MOVIE:
                        GuiUtils.setSimulationMovieImage(this.customValueItemShowPanel.getSimulationBoxImagePanel());
                        break;
                    case DISTRIBUTION_MOVIE:
                        GuiUtils.setDistributionMovieImage(this.customValueItemShowPanel.getSimulationBoxImagePanel());
                        break;
                }
                switch (tmpSlicingType) {
                    case SIMULATION_BOX:
                        // IMPORTANT: Remove time step info array
                        Preferences.getInstance().removeStepInfoArray();
                        // IMPORTANT: Clear time step simulation box change info
                        Preferences.getInstance().clearTimeStepSimulationBoxChangeInfo();
                        this.customValueItemShowPanel.getSlicerTimeStepPreferencesEditButton().setVisible(false);
                        this.customValueItemShowPanel.getSelectTimeStepDisplayComboBox().setVisible(false);
                        this.customValueItemShowPanel.getSelectSimulationBoxDisplayComboBox().setVisible(true);
                        this.customValueItemShowPanel.getViewButton().setToolTipText(GuiMessage.get("CustomPanelValueItemShow.viewButton.SimulationBox.toolTipText"));
                        break;
                    case SIMULATION_MOVIE:
                    case DISTRIBUTION_MOVIE:
                        // IMPORTANT: Set time step info array
                        TimeStepInfo[] tmpTimeStepInfoArray = (TimeStepInfo[]) tmpObjectArray[1];
                        String[] tmpStepInfoArray = new String[tmpTimeStepInfoArray.length];
                        for (int i = 0; i < tmpTimeStepInfoArray.length; i++) {
                            tmpStepInfoArray[i] = tmpTimeStepInfoArray[i].getStepInfo();
                        }
                        Preferences.getInstance().setStepInfoArray(tmpStepInfoArray);
                        this.customValueItemShowPanel.getSlicerTimeStepPreferencesEditButton().setVisible(true);
                        this.customValueItemShowPanel.getSelectTimeStepDisplayComboBox().setVisible(true);
                        this.customValueItemShowPanel.getSelectSimulationBoxDisplayComboBox().setVisible(false);
                        if (tmpSlicingType == SlicingTypeEnum.SIMULATION_MOVIE) {
                            this.customValueItemShowPanel.getViewButton().setToolTipText(GuiMessage.get("CustomPanelValueItemShow.viewButton.SimulationMovie.toolTipText"));
                        } else if (tmpSlicingType == SlicingTypeEnum.DISTRIBUTION_MOVIE) {
                            this.customValueItemShowPanel.getViewButton().setToolTipText(GuiMessage.get("CustomPanelValueItemShow.viewButton.DistributionMovie.toolTipText"));
                        }
                        break;
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Tree related action methods">
    /**
     * Collapses all nodes of job data tree
     */
    private void collapseAllNodesOfTree() {
        GuiUtils.expandAndRetainSelection(this.customValueItemShowPanel.getFeatureOverviewTree(), false);
    }

    /**
     * Expands all nodes of job data tree
     */
    private void expandAllNodesOfTree() {
        GuiUtils.expandAndRetainSelection(this.customValueItemShowPanel.getFeatureOverviewTree(), true);
    }
    // </editor-fold>
    // </editor-fold>

}
