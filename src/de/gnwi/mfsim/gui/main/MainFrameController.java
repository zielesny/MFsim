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
package de.gnwi.mfsim.gui.main;

import de.gnwi.jdpd.utilities.FileOutputStrings;
import de.gnwi.mfsim.model.util.FileDeletionTask;
import de.gnwi.mfsim.model.util.ArchiveTask;
import de.gnwi.mfsim.model.util.FastListModel;
import de.gnwi.mfsim.model.util.ExtensionFileFilter;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.job.JobManager;
import de.gnwi.mfsim.model.job.JobResultExecutionTask;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.gui.dialog.DialogProgress;
import de.gnwi.mfsim.gui.dialog.DialogTableDataSchemataManage;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.dialog.DialogValueItemShow;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.job.JobInput;
import de.gnwi.mfsim.model.job.JobResult;
import de.gnwi.mfsim.model.job.JdpdValueItemDefinition;
import de.gnwi.mfsim.model.job.JobUpdateUtils;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.peptide.PdbToDpdPool;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Controller class for MainFrame
 *
 * @author Achim Zielesny
 */
public class MainFrameController implements PropertyChangeListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * File utility methods
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Job update utility methods
     */
    private JobUpdateUtils jobUpdateUtils = new JobUpdateUtils();

    /**
     * JobResult execution list model (DefaultListModel) for jobExecutionList
     * (JList)
     */
    private FastListModel jobExecutionListModel;

    /**
     * JobInput list model (DefaultListModel) for job design
     */
    private FastListModel jobDesignInputListModel;

    /**
     * JobInput list model (DefaultListModel) for job execution
     */
    private FastListModel jobExecutionInputListModel;

    /**
     * JobResult list model (DefaultListModel) for jobRestartList (JList)
     */
    private FastListModel jobRestartListModel;

    /**
     * JobResult list model (DefaultListModel) for jobResultList (JList)
     */
    private FastListModel jobResultListModel;

    /**
     * Form this controller is made for
     */
    private MainFrame mainFrame;

    /**
     * List of job result execution tasks
     */
    private ConcurrentLinkedQueue<JobResultExecutionTask> jobResultExecutionTaskList;

    /**
     * List of job archive background tasks
     */
    private ConcurrentLinkedQueue<ArchiveTask> jobArchiveTaskList;

    /**
     * Executor service for job archiving tasks
     */
    private ExecutorService jobArchiveExecutorService;
    
    /**
     * Executor service for job result execution tasks
     */
    private ExecutorService jobResultExecutorService;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aMainFrame Form this controller is made for
     */
    public MainFrameController(MainFrame aMainFrame) {
        try {
            this.mainFrame = aMainFrame;
            MouseCursorManagement.getInstance().pushMouseCursorComponent(this.mainFrame);
            this.jobArchiveExecutorService = null;
            this.jobResultExecutorService = null;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="PropertyChangeListener propertyChange">
    /**
     * PropertyChangeListener property change
     *
     * @param anEvent PropertyChangeEvent
     */
    @Override
    public void propertyChange(PropertyChangeEvent anEvent) {
        try {
            // <editor-fold defaultstate="collapsed" desc="Property change due to progress of jobResultExecutionTask or jobArchiveTask">
            if (anEvent.getPropertyName().equals(ModelDefinitions.PROPERTY_CHANGE_PROGRESS)) {
                int tmpProgressValue = (Integer) anEvent.getNewValue();
                if (tmpProgressValue == 100) {
                    Object tmpObject = anEvent.getSource();
                    if (tmpObject instanceof JobResultExecutionTask) {
                        // <editor-fold defaultstate="collapsed" desc="jobResultExecutionTask">
                        JobResultExecutionTask tmpJobResultExecutionTask = (JobResultExecutionTask) tmpObject;
                        // Remove this instance as propertyChangeListener
                        tmpJobResultExecutionTask.removePropertyChangeListener(this);
                        // Progress 100%
                        // Update job results in JobManager.getInstance().getJobResultManager() since jobs results may have changed
                        JobManager.getInstance().getJobResultManager().updateJobResults();
                        // Remove finished jobs
                        this.removeFinishedJobs();
                        // Fill jobResultListModel/jobRestartListModel since jobs results may have changed
                        this.fillJobResultRelatedListModels();
                        // Start remaining job execution tasks ...
                        this.startRemainingJobExecutionTasks();
                        // ... or shutdown this.jobResultExecutorService if no job is working
                        this.shutdownJobResultExecutorService();
                        // Set display
                        this.updateJobExecutionDisplay();
                        this.updateMenuDisplay();
                        // </editor-fold>
                    } else if (tmpObject instanceof ArchiveTask) {
                        // <editor-fold defaultstate="collapsed" desc="jobArchiveTask">
                        ArchiveTask tmpJobArchiveTask = (ArchiveTask) tmpObject;
                        // Remove this instance as propertyChangeListener
                        tmpJobArchiveTask.removePropertyChangeListener(this);
                        // Remove finished job archive background tasks
                        this.removeFinishedJobArchiveTasks();
                        // Shutdown this.jobArchiveExecutorService if no job is archived
                        this.shutdownJobArchiveExecutorService();
                        // </editor-fold>
                    }
                    this.updateJobResultsDisplay();
                    this.updateStatusDisplay();
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Property change due to internal error in jobResultExecutionTask or jobArchiveTask">
            if (anEvent.getPropertyName().equals(ModelDefinitions.PROPERTY_CHANGE_ERROR)) {
                Object tmpObject = anEvent.getSource();
                if (tmpObject instanceof JobResultExecutionTask) {
                    // <editor-fold defaultstate="collapsed" desc="jobResultExecutionTask">
                    JobResultExecutionTask tmpJobResultExecutionTask = (JobResultExecutionTask) tmpObject;
                    // Remove this instance as propertyChangeListener
                    tmpJobResultExecutionTask.removePropertyChangeListener(this);
                    // <editor-fold defaultstate="collapsed" desc="Message about error">
                    JOptionPane.showMessageDialog(null, 
                        GuiMessage.get("Error.ErrorDuringJobExecution"), 
                        GuiMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    // </editor-fold>
                    this.stopAllSubmittedWorkingJobs();
                    // </editor-fold>
                } else if (tmpObject instanceof ArchiveTask) {
                    // <editor-fold defaultstate="collapsed" desc="jobArchiveTask">
                    ArchiveTask tmpJobArchiveTask = (ArchiveTask) tmpObject;
                    // Remove this instance as propertyChangeListener
                    tmpJobArchiveTask.removePropertyChangeListener(this);
                    // Remove finished job archive background tasks
                    this.removeFinishedJobArchiveTasks();
                    // Shutdown this.jobArchiveExecutorService if no job is archived
                    this.shutdownJobArchiveExecutorService();
                    // <editor-fold defaultstate="collapsed" desc="Message about error">
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Error.ErrorDuringJobArchiving"), GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE);

                    // </editor-fold>
                    // </editor-fold>
                }
                this.updateJobResultsDisplay();
                this.updateStatusDisplay();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Property change due to new "Job is alive" information in jobResultExecutionTask">
            if (anEvent.getPropertyName().equals(ModelDefinitions.PROPERTY_CHANGE_JOB_IS_ALIVE)) {
                this.mainFrame.getJobExecutionList().repaint();
                this.updateStatusDisplay();
            }
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "propertyChange()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Application related methods">
    /**
     * Checks frame size
     */
    public void checkFrameSize() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            GuiUtils.checkFrameSize(this.mainFrame);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "checkFrameSize()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Application title
     *
     * @return Application title or empty string if error occurs
     */
    public String getApplicationTitle() {
        try {
            return String.format(
                GuiMessage.get("Application.TitleFormat.FullClient"),
                GuiMessage.get("About.Name"),
                ModelDefinitions.APPLICATION_VERSION,
                ModelDefinitions.JAVA_VERSION
            );
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "getApplicationTitle()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return "";
        }
    }

    /**
     * Initialize
     */
    public void initialize() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Set main frame title (MUST be set AFTER application mode setting)">
            this.mainFrame.setTitle(this.getApplicationTitle());

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set particle set filename in jobInputsPanelTitledBorder">
            this.mainFrame.getJobInputsPanelTitledBorder().setTitle(String.format(GuiMessage.get("JobInputsPanelTitledBorder.TitleFormat"),
                            GuiMessage.get("MainFrame.jobInputsPanelTitledBorder.title"),
                            Preferences.getInstance().getCurrentParticleSetFilename()));
            // Note:
            // this.mainFrame.getJobInputsPanel().repaint();
            // is NOT necessary since application starts with Home tab.

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set and check MainFrame size">
            this.mainFrame.setSize(Preferences.getInstance().getMainFrameWidth(), Preferences.getInstance().getMainFrameHeight());
            GuiUtils.checkFrameSize(this.mainFrame);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set home panel images">
            this.setHomePanelImage();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set info label for job restart steps">
            this.mainFrame.getStepsForRestartInfoLabel().setText(String.format(GuiMessage.get("MainFrame.stepsForRestartInfoLabel.text"), String.valueOf(Preferences.getInstance().getNumberOfAdditionalStepsForJobRestart())));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize models and related lists">
            // <editor-fold defaultstate="collapsed" desc="- Initialize job input list models and lists">
            if (this.jobDesignInputListModel == null) {
                this.jobDesignInputListModel = new FastListModel();
            }
            this.mainFrame.getJobInputsSelectionPanel().getList().setModel(this.jobDesignInputListModel);
            if (this.jobExecutionInputListModel == null) {
                this.jobExecutionInputListModel = new FastListModel();
            }
            this.mainFrame.getSelectExecutionJobInputSelectionPanel().getList().setModel(this.jobExecutionInputListModel);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initialize jobResultListModel and list">
            if (this.jobResultListModel == null) {
                this.jobResultListModel = new FastListModel();
            }
            this.mainFrame.getSelectJobResultPanel().getList().setModel(this.jobResultListModel);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initialize jobRestartListModel and list">
            if (this.jobRestartListModel == null) {
                this.jobRestartListModel = new FastListModel();
            }
            this.mainFrame.getSelectJobForRestartSelectionPanel().getList().setModel(this.jobRestartListModel);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initialize jobExecutionListModel and list">
            if (this.jobExecutionListModel == null) {
                this.jobExecutionListModel = new FastListModel();
            }
            this.mainFrame.getJobExecutionList().setModel(this.jobExecutionListModel);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize GUI">
            // <editor-fold defaultstate="collapsed" desc="- Initialize job design panel">
            this.updateJobDesignDisplay();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initialize job execution panel">
            this.updateJobExecutionDisplay();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initialize job result panel">
            this.updateJobResultsDisplay();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initialize menu">
            this.updateMenuDisplay();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initialize status display">
            this.updateStatusDisplay();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set ParticleUpdateForJobInput flag">
            this.mainFrame.getParticleUpdateForJobInputMenuItem().setSelected(Preferences.getInstance().isParticleUpdateForJobInput());
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Fill job input and job result models">
            this.fillJobInputRelatedListModels();
            this.fillJobResultRelatedListModels();
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "initialize()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Status display related command methods">
    /**
     * Updates status display
     */
    private void updateStatusDisplay() {
        int tmpNumberOfWorkingJobResultExecutionTasks = this.getNumberOfSubmittedWorkingJobResultExecutionTasks();
        // IMPORTANT: Set tmpNumberOfWorkingJobResultExecutionTasks in PreferencesBasic
        Preferences.getInstance().setNumberOfWorkingJobResultExecutionTasks(tmpNumberOfWorkingJobResultExecutionTasks);
        int tmpNumberOfActiveJobArchiveTasks = this.getNumberOfSubmittedJobArchiveTasks();
        String tmpMFsimJobPath = Preferences.getInstance().getInternalMFsimJobPath();
        if (tmpMFsimJobPath == null || tmpMFsimJobPath.isEmpty()) {
            tmpMFsimJobPath = Preferences.getInstance().getDpdDataPath();
        }
        if (tmpNumberOfWorkingJobResultExecutionTasks > 0) {
            if (tmpNumberOfActiveJobArchiveTasks == 0) {
                this.mainFrame.getStatusInformationLabel().setText(String.format(GuiMessage.get("StatusInformation.WorkingSimulation"),
                        tmpMFsimJobPath,
                        String.valueOf(tmpNumberOfWorkingJobResultExecutionTasks)
                    )
                );
            } else {
                this.mainFrame.getStatusInformationLabel().setText(String.format(GuiMessage.get("StatusInformation.WorkingSimulationAndArchiving"),
                        tmpMFsimJobPath,
                        String.valueOf(tmpNumberOfWorkingJobResultExecutionTasks),
                        String.valueOf(tmpNumberOfActiveJobArchiveTasks)
                    )
                );
            }
        } else {
            if (tmpNumberOfActiveJobArchiveTasks == 0) {
                this.mainFrame.getStatusInformationLabel().setText(String.format(GuiMessage.get("StatusInformation.NoSimulation"),
                        tmpMFsimJobPath
                    )
                );
            } else {
                this.mainFrame.getStatusInformationLabel().setText(String.format(GuiMessage.get("StatusInformation.NoSimulationAndArchiving"),
                        tmpMFsimJobPath,
                        String.valueOf(tmpNumberOfActiveJobArchiveTasks)
                    )
                );
            }
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Menu related command methods">
    // <editor-fold defaultstate="collapsed" desc="- Actions">
    // <editor-fold defaultstate="collapsed" desc="-- Menu Application">
    // <editor-fold defaultstate="collapsed" desc="--- Preferences">
    /**
     * Displays preferences dialog
     */
    public void displayEditPreferencesDialog() {
        try {
            ValueItemContainer tmpEditablePreferencesValueItemContainer = Preferences.getInstance().getAllEditablePreferencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesDialog.title"), tmpEditablePreferencesValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                // Set changes in Preferences
                Preferences.getInstance().setEditablePreferences(tmpEditablePreferencesValueItemContainer);
                this.doActionsAfterChangingPreferences();
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayEditPreferencesDialog()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Sets default preferences
     */
    public void setDefaultPreferences() {
        try {
            if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
                // <editor-fold defaultstate="collapsed" desc="Set default preferences">
                MouseCursorManagement.getInstance().setWaitCursor();
                // Set default preferences in Preferences
                Preferences.getInstance().setDefaultValues();
                this.doActionsAfterChangingPreferences();
                MouseCursorManagement.getInstance().setDefaultCursor();
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setDefaultPreferences()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Schemata">
    /**
     * Manage table-data schemata
     */
    public void manageSchemata() {
        try {
            DialogTableDataSchemataManage.manage();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "manageSchemata()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Cache">
    /**
     * Show number of Spices in SpicesPool
     */
    public void showCache() {
        JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Cache.ShowFormat"), String.valueOf(SpicesPool.getInstance().getPoolSize()),
                        String.valueOf(PdbToDpdPool.getInstance().getPoolSize())),
                GuiMessage.get("Cache.ShowTitle"), 
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Clear SpicesPool
     */
    public void clearCache() {
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Cache.ClearTitle"), 
                String.format(GuiMessage.get("Cache.ClearMessageFormat"), String.valueOf(SpicesPool.getInstance().getPoolSize()), 
                String.valueOf(PdbToDpdPool.getInstance().getPoolSize())))) {
            SpicesPool.getInstance().clear();
            PdbToDpdPool.getInstance().clear();
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Exit">
    /**
     * Exit
     */
    public void exit() {
        try {
            if (this.isSubmittedJobResultExecutionTaskWorking() || this.getNumberOfSubmittedJobArchiveTasks() > 0) {
                // <editor-fold defaultstate="collapsed" desc="Show info that application cannot exit">
                JOptionPane.showMessageDialog(null, GuiMessage.get("Exit.CannotExit.Message"), GuiMessage.get("Exit.CannotExit.FrameTitle"),
                        JOptionPane.INFORMATION_MESSAGE);
                // </editor-fold>
                return;
            }
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Save MainFrame window size">
            Preferences.getInstance().setMainFrameHeightWidth(this.mainFrame.getHeight(), this.mainFrame.getWidth());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Save preferences">
            Preferences.getInstance().writePersistenceXmlInformation();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Close application logic">
            JobManager.getInstance().closeEngineLayer();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check log events">
            // false: NO log event
            ModelUtils.appendToLogfile(false, ModelDefinitions.MFSIM_SESSION_END);
            if (Preferences.getInstance().isLogEvent()) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                if (GuiUtils.getYesNoDecision(GuiMessage.get("ViewLogInformation.Title"), GuiMessage.get("ViewLogInformation.Message"))) {
                    GuiUtils.startViewer(Preferences.getInstance().getLogfilePathname());
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Shutdown executors">
            if (this.jobArchiveExecutorService != null) {
                this.jobArchiveExecutorService.shutdown();
            }
            if (this.jobResultExecutorService != null) {
                this.jobResultExecutorService.shutdown();
            }
            // </editor-fold>
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Exit">
            // Exit without error
            ModelUtils.exitApplication(0);
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "exit()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        }
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Menu Particles">
    // <editor-fold defaultstate="collapsed" desc="--- Particle set">
    /**
     * Display choose-particle-set dialog
     */
    public void displayChooseParticleSetDialog() {
        try {
            ValueItemContainer tmpParticleSetValueItemContainer = Preferences.getInstance().getParticleSetEditablePreferencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("ParticleSetChooseDialog.title"), tmpParticleSetValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                Preferences.getInstance().setEditablePreferences(tmpParticleSetValueItemContainer);
                // Reset job related value items to react on changes
                JdpdValueItemDefinition.getInstance().reset();
                // Set current particle set if necessary (may be changed)
                this.mainFrame.getJobInputsPanelTitledBorder().setTitle(String.format(GuiMessage.get("JobInputsPanelTitledBorder.TitleFormat"),
                        GuiMessage.get("MainFrame.jobInputsPanelTitledBorder.title"),
                        Preferences.getInstance().getCurrentParticleSetFilename()));
                this.mainFrame.getJobInputsPanel().repaint();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayEditParticleSetDialog()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Particles">
    /**
     * Display duplicate-particles dialog
     */
    public void displayDuplicateParticlesDialog() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpParticlesDuplicateValueItemContainer = StandardParticleInteractionData.getInstance().getParticlesValueItemContainerForDuplicate();
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("ParticlesDuplicateDialog.title"), tmpParticlesDuplicateValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                StandardParticleInteractionData.getInstance().duplicateParticles(tmpParticlesDuplicateValueItemContainer);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayDuplicateParticlesDialog()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Display remove-particles dialog
     */
    public void displayRemoveParticlesDialog() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpParticlesRemoveValueItemContainer = StandardParticleInteractionData.getInstance().getParticlesValueItemContainerForRemove();
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("ParticlesRemoveDialog.title"), tmpParticlesRemoveValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                StandardParticleInteractionData.getInstance().removeParticles(tmpParticlesRemoveValueItemContainer);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayRemoveParticlesDialog()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Display morph-particle dialog
     */
    public void displayMorphParticleDialog() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpMorphParticleValueItemContainer = StandardParticleInteractionData.getInstance().getParticlesValueItemContainerForMorph();
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("MorphParticleDialog.title"), tmpMorphParticleValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                StandardParticleInteractionData.getInstance().morphParticle(tmpMorphParticleValueItemContainer);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(
                null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayMorphParticleDialog()", "MainFrameController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Display rescale-Vmin dialog
     */
    public void displayRescaleVminDialog() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpVminRescaleValueItemContainer = StandardParticleInteractionData.getInstance().getValueItemContainerForVminRescale();
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("VminRescaleDialog.title"), tmpVminRescaleValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                StandardParticleInteractionData.getInstance().rescaleVmin(tmpVminRescaleValueItemContainer);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(
                null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayRescaleVminDialog()", "MainFrameController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Display rescale-repulsions dialog
     * 
     * @param isIndividualScaling True: Every temperature is individually 
     * scaled, false: All temperatures are scaled with global scaling factor
     */
    public void displayRescaleRepulsionsDialog(boolean isIndividualScaling) {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpRepulsionsRescaleValueItemContainer = StandardParticleInteractionData.getInstance().getValueItemContainerForRepulsionsRescale();
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("RescaleRepulsionsDialog.title"), tmpRepulsionsRescaleValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                StandardParticleInteractionData.getInstance().rescaleRepulsions(tmpRepulsionsRescaleValueItemContainer, isIndividualScaling);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(
                null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayRescaleRepulsionsDialog()", "MainFrameController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    
    /**
     * Display increment-probe-particles dialog
     */
    public void displayIncrementProbeParticlesDialog() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpValueItemContainerForProbeParticleIncrement = 
                StandardParticleInteractionData.getInstance().getValueItemContainerForProbeParticleIncrement();
            if (tmpValueItemContainerForProbeParticleIncrement == null) {
                JOptionPane.showMessageDialog(
                    null, 
                    GuiMessage.get("proteinBackboneProbeParticles.NoProbeParticles"),
                    GuiMessage.get("proteinBackboneProbeParticles.NoProbeParticlesDialogTitle"), 
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("ParticlesIncrementDialog.title"), tmpValueItemContainerForProbeParticleIncrement)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                if (!StandardParticleInteractionData.getInstance().incrementProbeParticles(tmpValueItemContainerForProbeParticleIncrement)) {
                    JOptionPane.showMessageDialog(null, GuiMessage.get("proteinBackboneProbeParticles.NoProbeParticleIncrement"),
                            GuiMessage.get("proteinBackboneProbeParticles.NoProbeParticleIncrementDialogTitle"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayIncrementProbeParticlesDialog()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Display edit-particles dialog
     */
    public void displayEditParticlesDialog() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // Initialize change detection since particles value items directly operate on singleton StandardParticleInteractionData (this is necessary due to the size of particle interaction data
            // which may be megabytes)
            StandardParticleInteractionData.getInstance().initChangeDetection();
            ValueItemContainer tmpParticlesValueItemContainer = StandardParticleInteractionData.getInstance().getParticlesValueItemContainerForEdit();
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("ParticlesEditDialog.title"), tmpParticlesValueItemContainer)) {
                // <editor-fold defaultstate="collapsed" desc="Make changed data of singleton StandardParticleInteractionData persistent">
                MouseCursorManagement.getInstance().setWaitCursor();
                // Reset job related value items to react on changes
                JdpdValueItemDefinition.getInstance().reset();

                // </editor-fold>
            } else if (StandardParticleInteractionData.getInstance().hasChanged()) {
                // <editor-fold defaultstate="collapsed" desc="Singleton StandardParticleInteractionData has changed data but dialog was closed by a cancel operation: Reset">
                MouseCursorManagement.getInstance().setWaitCursor();
                // Reset singleton StandardParticleInteractionData with persistent data
                StandardParticleInteractionData.getInstance().reset();

                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "displayEditParticlesDialog()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Menu Window">
    /**
     * Centers frame
     */
    public void centerFrame() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            GuiUtils.centerFrameOnScreen(this.mainFrame);
            MouseCursorManagement.getInstance().setDefaultCursor();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "centerFrame()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Fits frame to screen so that aspect ratio of home panel image remains
     * unchanged
     */
    public void fitFrameToScreen() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();

            // <editor-fold defaultstate="collapsed" desc="Set Preferences if necessary">
            if (Preferences.getInstance().getMainFrameMaximumHeight() == Preferences.getInstance().getDefaultMainFrameMaximumHeight()
                    && Preferences.getInstance().getMainFrameMaximumWidth() == Preferences.getInstance().getDefaultMainFrameMaximumWidth()) {
                GuiUtils.setMainFrameMaximumHeightAndWidthInBasicPreferences();
            }

            // </editor-fold>
            GuiUtils.setSizeOfFrame(this.mainFrame, Preferences.getInstance().getMainFrameMaximumWidth(), Preferences.getInstance().getMainFrameMaximumHeight());
            GuiUtils.centerFrameOnScreen(this.mainFrame);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "fitFrameHeight()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Sets default frame size
     */
    public void setDefaultFrameSize() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            GuiUtils.setSizeOfFrame(this.mainFrame, Preferences.getInstance().getDefaultMainFrameWidth(), Preferences.getInstance().getDefaultMainFrameHeight());
            GuiUtils.centerFrameOnScreen(this.mainFrame);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setDefaultFrameSize()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Menu Help">
    /**
     * Browse tutorials directory
     */
    public void browseTutorialsDirectory() {
        if ((new File(Preferences.getInstance().getDpdSourceTutorialsPath())).isDirectory()) {
            GuiUtils.startViewer(Preferences.getInstance().getDpdSourceTutorialsPath());
        }
    }

    /**
     * Browse MFsim info directory
     */
    public void browseMfsimInfoDirectory() {
        if ((new File(Preferences.getInstance().getDpdSourcePath())).isDirectory()) {
            GuiUtils.startViewer(Preferences.getInstance().getDpdSourceInfoPath());
        }
    }

    /**
     * Browse MFsim source directory
     */
    public void browseMfsimSourceDirectory() {
        if ((new File(Preferences.getInstance().getDpdSourcePath())).isDirectory()) {
            GuiUtils.startViewer(Preferences.getInstance().getDpdSourcePath());
        }
    }

    /**
     * Browse MFsim data directory
     */
    public void browseMFsimDataDirectory() {
        if ((new File(Preferences.getInstance().getDpdDataPath())).isDirectory()) {
            GuiUtils.startViewer(Preferences.getInstance().getDpdDataPath());
        }
    }

    /**
     * Browse MFsim job directory
     */
    public void browseMFsimJobDirectory() {
        if (!Preferences.getInstance().getInternalMFsimJobPath().isEmpty()) {
            GuiUtils.startViewer(Preferences.getInstance().getInternalMFsimJobPath());
        }
    }

    /**
     * Browse MFsim temporary directory
     */
    public void browseMFsimTempDirectory() {
        if (!Preferences.getInstance().getTempPath().isEmpty()) {
            GuiUtils.startViewer(Preferences.getInstance().getTempPath());
        }
    }

    /**
     * Browse MFsim log file
     */
    public void browseMFsimLogfile() {
        GuiUtils.startViewer(Preferences.getInstance().getLogfilePathname());        
    }

    /**
     * Resets log file
     */
    public void resetLogfile() {
        ModelUtils.resetLogfile();
    }
    
    /**
     * About information
     */
    public void aboutApplication() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Message AboutInfo">
            String tmpDisplay = 
                String.format(
                    GuiMessage.get("Application.TitleFormat.FullClient"),
                    GuiMessage.get("About.Name"),
                    ModelDefinitions.APPLICATION_VERSION,
                    ModelDefinitions.JAVA_VERSION
                ) + 
                "\n\n" +
                "using" + "\n" +
                "- Apache Commons Lang Version 3.4" + "\n" +
                "- Apache Commons Math Version 3.6.1" + "\n" +
                "- Apache Commons RNG Version 1.4" + "\n" +
                "- BioJava Version 3.0.8" + "\n" +
                "- FFmpeg (Static) Version 3.2.4" + "\n" +
                "- GraphStream Version 1.3" + "\n" +
                "- Jama Version 1.0.3" + "\n" +
                "- JCommon Version 1.0.9" + "\n" +
                "- JDOM Version 2.0.6" + "\n" +
                "- Jdpd Version 1.6.0.0" + "\n" +
                "- JFreeChart Version 1.0.5" + "\n" +
                "- Jmol Version 14.2.7" + "\n" +
                "- SPICES Version 1.0.0.0" + "\n" +
                "- Vecmath Version 1.5.2" + "\n\n" +
                "Compatibility:" + "\n" +
                "Job Input version " + ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION + " to " + ModelDefinitions.APPLICATION_VERSION  + "\n" +
                "Job Result version " + ModelDefinitions.MINIMUM_JOB_RESULT_APPLICATION_VERSION + " to " + ModelDefinitions.APPLICATION_VERSION  + "\n\n" +
                GuiMessage.get("About.StartInfo") + "\n\n" +
                GuiMessage.get("About.DevelopmentInfo") + "\n\n" +
                GuiMessage.get("About.PoweredBy") + "\n\n" +
                GuiMessage.get("About.GnuGplSourceCode");
            JOptionPane.showMessageDialog(null, tmpDisplay, GuiMessage.get("Help.VersionFrameTitle"), JOptionPane.INFORMATION_MESSAGE);

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "aboutApplication()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update display">
    /**
     * Updates menu display
     */
    private void updateMenuDisplay() {
        this.updateMFsimJobPathDisplay();
    }

    /**
     * Updates MFsim job path menu display
     */
    private void updateMFsimJobPathDisplay() {
        this.mainFrame.geMFsimJobMenu().setVisible(!Preferences.getInstance().getInternalMFsimJobPath().isEmpty());
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Main tab change related command methods">
    /**
     * Main tab change reaction
     */
    public void reactOnMainTabChange() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpIndex = this.mainFrame.getMainTabbedPanel().getSelectedIndex();
            switch (tmpIndex) {
                // <editor-fold defaultstate="collapsed" desc="Home panel">
                case 0:
                    break;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Job design panel">
                case 1:
                    this.updateJobDesignDisplay();
                    break;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Job execution panel">
                case 2:
                    this.updateJobExecutionDisplay();
                    break;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Job results panel">
                case 3:
                    this.updateJobResultsDisplay();
                    break;
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "reactOnMainTabChange()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Home related command methods">
    // <editor-fold defaultstate="collapsed" desc="- Actions">
    /**
     * Browse MFsim GitHub repository
     */
    public void browseGitHubRepository() {
        try {
            GuiUtils.startBrowser(ModelDefinitions.MFSIM_GITHUB_URL);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), 
                    "browseGitHubRepository()", 
                    "MainFrameController"
                ), 
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Private methods">
    /**
     * Sets home panel image
     */
    private void setHomePanelImage() {
        // <editor-fold defaultstate="collapsed" desc="HOME_PANEL_IMAGE_FILENAME">
        BufferedImage tmpImage = GuiUtils.getImageOfResource(GuiDefinitions.HOME_IMAGE_FILENAME_PREFIX, GuiDefinitions.RESOURCE_IMAGE_ENDING);
        if (tmpImage != null) {
            this.mainFrame.getHomeImagePanel().setBasicImage(tmpImage);
        }
        // </editor-fold>
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Job design related command methods">
    // <editor-fold defaultstate="collapsed" desc="- Actions">
    /**
     * Archives selected job input
     */
    public void archiveJobInput() {
        try {
            int tmpSelectedIndex = this.mainFrame.getJobInputsSelectionPanel().getList().getSelectedIndex();
            if (tmpSelectedIndex >= 0) {
                String tmpArchiveFilePathname = 
                    GuiUtils.selectSingleFileForSave(
                        GuiMessage.get("Chooser.selectSingleJobArchiveChooser"),
                        GuiDefinitions.JOB_INPUT_ARCHIVE_FILE_EXTENSION
                    );
                if (tmpArchiveFilePathname != null) {
                    try {
                        // <editor-fold defaultstate="collapsed" desc="Move job input to archive file">
                        JobInput tmpJobInputToBeArchived = (JobInput) this.mainFrame.getJobInputsSelectionPanel().getList().getSelectedValue();
                        // Parameter null: No exclusions for archiving
                        String tmpFileExclusionRegexPatternString = null;
                        // Compression is used
                        boolean tmpIsUncompressed = false;
                        ArchiveTask tmpArchiveTask = 
                            new ArchiveTask(
                                tmpJobInputToBeArchived.getJobInputPath(), 
                                tmpArchiveFilePathname, 
                                tmpFileExclusionRegexPatternString, 
                                tmpIsUncompressed
                            );
                        DialogProgress.hasCanceled(GuiMessage.get("Archiving"), tmpArchiveTask);
                        // </editor-fold>
                    } catch (Exception anExeption) {
                        // <editor-fold defaultstate="collapsed" desc="Show error message">
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SaveOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "archiveJobInput()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Creates new Jdpd job input
     */
    public void createNewJobInput() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpJobInputValueItemJobContainer = JdpdValueItemDefinition.getInstance().getClonedJobInputValueItemContainer();
            tmpJobInputValueItemJobContainer.setUpdateNotificationObject(ModelDefinitions.JOB_UPDATE_UTILS);
            // Set current timestamp
            tmpJobInputValueItemJobContainer.setValueOfValueItem("Timestamp", ModelUtils.getTimestampInStandardFormat());
            // Set default description
            tmpJobInputValueItemJobContainer.setValueOfValueItem("Description", GuiMessage.get("Information.NewJob"));
            // Set Compartments value item with initial compartment container
            CompartmentContainer tmpCompartmentContainer = new CompartmentContainer(tmpJobInputValueItemJobContainer);
            ValueItem tmpCompartmentsValueItem = tmpJobInputValueItemJobContainer.getValueItem("Compartments");
            tmpCompartmentsValueItem.setCompartmentContainer(tmpCompartmentContainer);
            // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Clear movie slicer configuration in Preferences">
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().clear();
            // </editor-fold>
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("EditNewJobInput.Title"), tmpJobInputValueItemJobContainer, true, false, false, true)) {
                this.saveJobInput(new JobInput(tmpJobInputValueItemJobContainer));
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "createNewJobInput()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Imports job input(s) from archive file(s)
     */
    public void importJobInputs() {
        try {
            String[] tmpExtensions;
            if (GuiDefinitions.JOB_RESULT_ARCHIVE_FILE_EXTENSION.equals(GuiDefinitions.JOB_INPUT_ARCHIVE_FILE_EXTENSION)) {
                tmpExtensions = new String[]{GuiDefinitions.JOB_RESULT_ARCHIVE_FILE_EXTENSION};
            } else {
                tmpExtensions = new String[]{GuiDefinitions.JOB_RESULT_ARCHIVE_FILE_EXTENSION, GuiDefinitions.JOB_INPUT_ARCHIVE_FILE_EXTENSION};
            }
            String[] tmpImportFilePathnames = 
                GuiUtils.selectMultipleFiles(Preferences.getInstance().getLastSelectedPath(), 
                    GuiMessage.get("Chooser.selectSingleJobImportChooser"),
                    new ExtensionFileFilter(tmpExtensions)
                );
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (tmpImportFilePathnames == null) {
                return;
            }
            for (String tmpImportFilePathname : tmpImportFilePathnames) {
                if (tmpImportFilePathname == null || tmpImportFilePathname.isEmpty() || !(new File(tmpImportFilePathname)).isFile()) {
                    return;
                }
                if (!tmpImportFilePathname.toLowerCase(Locale.ENGLISH).endsWith(GuiDefinitions.JOB_INPUT_ARCHIVE_FILE_EXTENSION.toLowerCase(Locale.ENGLISH))
                        && !tmpImportFilePathname.toLowerCase(Locale.ENGLISH).endsWith(GuiDefinitions.JOB_RESULT_ARCHIVE_FILE_EXTENSION.toLowerCase(Locale.ENGLISH))) {
                    // <editor-fold defaultstate="collapsed" desc="Unknown file type">
                    JOptionPane.showMessageDialog(null, 
                        GuiMessage.get("Error.UnknownJobInputFileType"), 
                        GuiMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                    // </editor-fold>
                }
                String tmpMainDirectoryName = this.fileUtilityMethods.getDirectoryNameFromZipFile(tmpImportFilePathname);
                if (tmpMainDirectoryName == null || tmpMainDirectoryName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, 
                        GuiMessage.get("Error.NoJobInputZipMainDirectory"), 
                        GuiMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (!tmpMainDirectoryName.toLowerCase(Locale.ENGLISH).startsWith(ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY.toLowerCase(Locale.ENGLISH))
                    && !tmpMainDirectoryName.toLowerCase(Locale.ENGLISH).startsWith(ModelDefinitions.PREFIX_OF_JOB_RESULT_DIRECTORY.toLowerCase(Locale.ENGLISH))) {
                    // <editor-fold defaultstate="collapsed" desc="Invalid archived directory prefix">
                    JOptionPane.showMessageDialog(null, 
                        GuiMessage.get("Error.NoJobInputZipMainDirectory"), 
                        GuiMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                    // </editor-fold>
                }
            }
            // </editor-fold>
            try {
                MouseCursorManagement.getInstance().setWaitCursor();
                for (String tmpImportFilePathname : tmpImportFilePathnames) {
                    // <editor-fold defaultstate="collapsed" desc="Import job input archive file or job input from job result archive file">
                    String tmpMainDirectoryName = this.fileUtilityMethods.getDirectoryNameFromZipFile(tmpImportFilePathname);
                    if (tmpMainDirectoryName.toLowerCase(Locale.ENGLISH).startsWith(ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY.toLowerCase(Locale.ENGLISH))) {
                        // <editor-fold defaultstate="collapsed" desc="Import job input archive file">
                        try {
                            String tmpNewJobInputDirectoryPath = Preferences.getInstance().getJobInputPath() + File.separatorChar + tmpMainDirectoryName;
                            if ((new File(tmpNewJobInputDirectoryPath)).isDirectory()) {
                                MouseCursorManagement.getInstance().setDefaultCursor();
                                JOptionPane.showMessageDialog(null, GuiMessage.get("Information.JobInputAlreadyExists"), 
                                    GuiMessage.get("Information.InformationFrameTitle"),
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                                return;
                            }
                            if (!this.fileUtilityMethods.extractDirectoryFromZipFile(Preferences.getInstance().getJobInputPath(), tmpImportFilePathname)) {
                                MouseCursorManagement.getInstance().setDefaultCursor();
                                JOptionPane.showMessageDialog(null, 
                                    GuiMessage.get("Error.CanNotExtractJobInputZipFile"), 
                                    GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }
                            // Update job inputs from job input path
                            JobManager.getInstance().getJobInputManager().updateJobInputs();
                            // JobInputs may have changed so fill job input related list models again!
                            this.fillJobInputRelatedListModels();
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);
                            MouseCursorManagement.getInstance().setDefaultCursor();
                            JOptionPane.showMessageDialog(null, 
                                GuiMessage.get("Error.CanNotExtractJobInputZipFile"), 
                                GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }
                        // </editor-fold>
                    } else if (tmpMainDirectoryName.toLowerCase(Locale.ENGLISH).startsWith(ModelDefinitions.PREFIX_OF_JOB_RESULT_DIRECTORY.toLowerCase(Locale.ENGLISH))) {
                        // <editor-fold defaultstate="collapsed" desc="Import job input from job result archive file">
                        try {
                            String tmpJobInputDirectoryName = tmpJobInputDirectoryName = this.fileUtilityMethods.getSingleDirectoryNameFromZipFile(tmpImportFilePathname, ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY);
                            String tmpNewJobInputDirectoryPath = Preferences.getInstance().getJobInputPath() + File.separatorChar + tmpJobInputDirectoryName;
                            if ((new File(tmpNewJobInputDirectoryPath)).isDirectory()) {
                                MouseCursorManagement.getInstance().setDefaultCursor();
                                JOptionPane.showMessageDialog(null, 
                                    GuiMessage.get("Information.JobInputAlreadyExists"), 
                                    GuiMessage.get("Information.InformationFrameTitle"),
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                                return;
                            }
                            if (!this.fileUtilityMethods.getSingleDirectoryFromZipFile(tmpImportFilePathname, ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY, Preferences.getInstance().getJobInputPath())) {
                                MouseCursorManagement.getInstance().setDefaultCursor();
                                JOptionPane.showMessageDialog(null, 
                                    GuiMessage.get("Error.NoJobInputInformationInJobResultZipArchive"),
                                    GuiMessage.get("Error.ErrorNotificationTitle"), 
                                    JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }
                            // Update job inputs from job input path
                            JobManager.getInstance().getJobInputManager().updateJobInputs();
                            // JobInputs may have changed so fill job input related list models again!
                            this.fillJobInputRelatedListModels();
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);
                            MouseCursorManagement.getInstance().setDefaultCursor();
                            JOptionPane.showMessageDialog(null, 
                                GuiMessage.get("Error.NoJobInputInformationInJobResultZipArchive"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), 
                                JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }
                        // </editor-fold>
                    }
                    // </editor-fold>
                }
            } finally {
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "importJobInput()", "MainFrameController"), 
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }

    /**
     * Removes all selected job inputs
     */
    public void removeSelectedJobInputs() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int[] tmpSelectedIndices = this.mainFrame.getJobInputsSelectionPanel().getList().getSelectedIndices();
            if (tmpSelectedIndices.length > 0 && tmpSelectedIndices[0] >= 0) {
                // <editor-fold defaultstate="collapsed" desc="Confirm dialog and job removal">
                if (GuiUtils.getYesNoDecision(GuiMessage.get("RemoveJobInputs.FrameTitle"), GuiMessage.get("RemoveJobInputs.Message"))) {
                    // Important: Proceed form highest to lowest index!
                    for (int k = tmpSelectedIndices.length - 1; k >= 0; k--) {
                        JobInput tmpJobInputToBeRemoved = (JobInput) this.mainFrame.getJobInputsSelectionPanel().getList().getModel().getElementAt(tmpSelectedIndices[k]);
                        if (!JobManager.getInstance().getJobInputManager().removeJobInputInJobInputPath(tmpJobInputToBeRemoved)) {
                            // <editor-fold defaultstate="collapsed" desc="Message NoSelectedJobInputRemoval">
                            JOptionPane.showMessageDialog(null, GuiMessage.get("Error.NoSelectedJobInputRemoval"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE);

                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="Remove from this.jobExecutionInputListModel">
                            for (int i = 0; i < this.jobExecutionInputListModel.getSize(); i++) {
                                JobInput tmpJobInputToCompare = (JobInput) this.jobExecutionInputListModel.getElementAt(i);
                                if (tmpJobInputToBeRemoved.getJobInputId().equals(tmpJobInputToCompare.getJobInputId())) {
                                    this.jobExecutionInputListModel.remove(i);
                                    // IMPORTANT: Set selected index in this.mainFrame.getSelectExecutionJobInputSelectionPanel().getList()
                                    this.mainFrame.getSelectExecutionJobInputSelectionPanel().getList().setSelectedIndex(0);
                                    break;
                                }
                            }

                            // </editor-fold>
                            this.jobDesignInputListModel.remove(tmpSelectedIndices[k]);
                        }
                    }
                    // Clean job input path in background after removal (i.e. rename of job inputs to be removed)
                    JobManager.getInstance().getJobInputManager().cleanJobInputPathInBackground();
                    if (tmpSelectedIndices.length == 1) {
                        if (tmpSelectedIndices[0] == this.jobDesignInputListModel.getSize()) {
                            this.mainFrame.getJobInputsSelectionPanel().getList().setSelectedIndex(tmpSelectedIndices[0] - 1);
                        } else {
                            this.mainFrame.getJobInputsSelectionPanel().getList().setSelectedIndex(tmpSelectedIndices[0]);
                        }
                    } else {
                        this.mainFrame.getJobInputsSelectionPanel().getList().setSelectedIndex(0);
                    }
                    this.updateJobDesignDisplay();
                    this.updateJobExecutionDisplay();
                }

                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "removeSelectedJobInput()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Selects existing job input of job input path for edit of job data
     */
    public void selectExistingJobInputForEdit() {
        JobInput tmpSelectedJobInput = null;
        try {
            // NOTE: No setCursor() since this is performed in this.saveJobInput()
            int tmpSelectedIndex = this.mainFrame.getJobInputsSelectionPanel().getList().getSelectedIndex();
            if (tmpSelectedIndex < 0) {
                // <editor-fold defaultstate="collapsed" desc="List is empty, create new job input">
                this.createNewJobInput();
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Check version of job input">
                tmpSelectedJobInput = (JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex);
                String tmpVersionOfJobInput = tmpSelectedJobInput.getMFsimApplicationVersion();
                if (tmpVersionOfJobInput == null || !ModelUtils.isVersionOneEqualOrHigher(tmpVersionOfJobInput, ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION)) {
                    // Job input application version is NOT allowed
                    JOptionPane.showMessageDialog(null, 
                        String.format(GuiMessage.get("Error.SelectedJobInputForbiddenVersionFormat"), ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION), 
                        GuiMessage.get("Information.NotificationTitle"),
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Check if job is in execution: Then edit is NOT allowed!">
                if (this.jobExecutionListModel.getSize() > 0) {
                    tmpSelectedJobInput = (JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex);
                    String tmpUniqueIdOfSelectedJob = tmpSelectedJobInput.getJobInputId();
                    for (int i = 0; i < this.jobExecutionListModel.getSize(); i++) {
                        JobResult tmpJobResult = null;
                        try {
                            tmpJobResult = (JobResult) this.jobExecutionListModel.get(i);
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);
                            continue;
                        }
                        if (tmpJobResult != null) {
                            if (tmpJobResult.getJobInput() == null) {
                                // A job is in execution queue but NOT started: Do NOT allow any Edit process!
                                JOptionPane.showMessageDialog(null, 
                                    GuiMessage.get("Error.NonStartedJobInExecutionQueue"), 
                                    GuiMessage.get("Information.NotificationTitle"),
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                                return;
                            } else {
                                String tmpUniqueIdOfSelectedJobInExecution = tmpJobResult.getJobInput().getJobInputId();
                                if (tmpUniqueIdOfSelectedJobInExecution.equals(tmpUniqueIdOfSelectedJob)) {
                                    // Job is in execution: Do NOT allow edit process!
                                    JOptionPane.showMessageDialog(null, 
                                        GuiMessage.get("Error.SelectedJobInExecution"), 
                                        GuiMessage.get("Information.NotificationTitle"),
                                        JOptionPane.INFORMATION_MESSAGE
                                    );
                                    return;
                                }
                            }
                        }
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="List is NOT empty, edit selected job input">
                MouseCursorManagement.getInstance().setWaitCursor();
                // Add and set current particle set file if necessary
                if (!Preferences.getInstance().isParticleUpdateForJobInput()) {
                    if (ModelUtils.addAndSetCurrentParticleSet(((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex)))) {
                        // Show information about change of particle set
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Information.ParticleSetChangeFormat"), Preferences.getInstance().getCurrentParticleSetFilename()), 
                            GuiMessage.get("Information.ParticleSetChange.Title"),
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        // Reset job related value items to react on changes
                        JdpdValueItemDefinition.getInstance().reset();
                        // Set current particle set if necessary (may be changed)
                        this.mainFrame.getJobInputsPanelTitledBorder().setTitle(String.format(GuiMessage.get("JobInputsPanelTitledBorder.TitleFormat"),
                                GuiMessage.get("MainFrame.jobInputsPanelTitledBorder.title"), 
                                Preferences.getInstance().getCurrentParticleSetFilename()
                            )
                        );
                        this.mainFrame.getJobInputsPanel().repaint();
                    }
                }
                // Check particle set
                String tmpParticleSetFilenameOfJobInput = ((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex)).getParticleSetFilename();
                if (tmpParticleSetFilenameOfJobInput != null) {
                    if (!tmpParticleSetFilenameOfJobInput.equals(Preferences.getInstance().getCurrentParticleSetFilename())) {
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        if (!GuiUtils.getYesNoDecision(GuiMessage.get("DifferentParticleSets.Title"),
                                String.format(GuiMessage.get("DifferentParticleSets.MessageFormat"), tmpParticleSetFilenameOfJobInput, Preferences.getInstance().getCurrentParticleSetFilename())
                            )) {
                            return;
                        }
                        MouseCursorManagement.getInstance().setWaitCursor();
                    }
                }
                // Check compatibility of particles of job input for current particle set
                String tmpIncompatibleParticles = this.jobUpdateUtils.getIncompatibleParticlesForCurrentParticleSet((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex));
                if (tmpIncompatibleParticles != null) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    JOptionPane.showMessageDialog(null, 
                        String.format(GuiMessage.get("Error.MissingParticle"), tmpIncompatibleParticles),
                        GuiMessage.get("Error.ErrorNotificationTitle"), 
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                // Update complete particle related information if specified
                if (Preferences.getInstance().isParticleUpdateForJobInput()) {
                    tmpSelectedJobInput = this.jobUpdateUtils.correctParticlesAndInteractions((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex));
                } else {
                    tmpSelectedJobInput = (JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex);
                }
                // Update possible old job input
                this.updateJobInput(tmpSelectedJobInput);
                ValueItemContainer tmpValueItemJobContainer = tmpSelectedJobInput.getValueItemContainer();
                String tmpJobInputPath = tmpSelectedJobInput.getJobInputPath();
                String tmpTimestamp = tmpSelectedJobInput.getTimestamp();
                String tmpUniqueId = tmpSelectedJobInput.getJobInputId();
                // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Clear movie slicer configuration in Preferences">
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().clear();
                // </editor-fold>
                MouseCursorManagement.getInstance().setDefaultCursor();
                if (DialogValueItemEdit.hasChanged(String.format(GuiMessage.get("KeyValue"), GuiMessage.get("EditJobInput.Title"),
                        tmpSelectedJobInput.getDescription()), tmpValueItemJobContainer, true, false, false, true)) {
                    JobInput tmpNewJobInput = new JobInput(tmpValueItemJobContainer, tmpJobInputPath, tmpTimestamp, tmpUniqueId);
                    // NOTE: Job input will overwrite existing information when saved
                    this.saveJobInput(tmpNewJobInput);
                    // Select new job input in job input list
                    GuiUtils.selectJobInputInList(this.mainFrame.getJobInputsSelectionPanel().getList(), tmpNewJobInput);
                }
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "selectExistingJobInputForEdit()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (tmpSelectedJobInput != null) {
                // IMPORTANT: Set value item container of job input to null so it will be re-instantiated from file information (otherwise changes may be persistent)
                tmpSelectedJobInput.releaseMemoryOfValueItemContainer();
            }
        }
    }

    /**
     * Uses existing job input of job input path as template for edit of job
     * data
     */
    public void useExistingJobInputAsTemplateForEdit() {
        JobInput tmpSelectedJobInput = null;
        try {
            // NOTE: No this.mainFrame.setCursor() since this is performed in this.saveJobInput()
            int tmpSelectedIndex = this.mainFrame.getJobInputsSelectionPanel().getList().getSelectedIndex();
            if (tmpSelectedIndex >= 0) {
                // <editor-fold defaultstate="collapsed" desc="Check version of job input">
                tmpSelectedJobInput = (JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex);
                String tmpVersionOfJobInput = tmpSelectedJobInput.getMFsimApplicationVersion();
                if (tmpVersionOfJobInput == null || !ModelUtils.isVersionOneEqualOrHigher(tmpVersionOfJobInput, ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION)) {
                    // Job input application version is NOT allowed
                    JOptionPane.showMessageDialog(null, 
                        String.format(GuiMessage.get("Error.SelectedJobInputForbiddenVersionFormat"), ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION), 
                        GuiMessage.get("Information.NotificationTitle"),
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
                // </editor-fold>
                MouseCursorManagement.getInstance().setWaitCursor();
                // Add and set current particle set file if necessary
                if (!Preferences.getInstance().isParticleUpdateForJobInput()) {
                    if (ModelUtils.addAndSetCurrentParticleSet(((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex)))) {
                        // Show information about change of particle set
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Information.ParticleSetChangeFormat"), Preferences.getInstance().getCurrentParticleSetFilename()), 
                            GuiMessage.get("Information.ParticleSetChange.Title"),
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        // Reset job related value items to react on changes
                        JdpdValueItemDefinition.getInstance().reset();
                        // Set current particle set if necessary (may be changed)
                        this.mainFrame.getJobInputsPanelTitledBorder().setTitle(String.format(GuiMessage.get("JobInputsPanelTitledBorder.TitleFormat"),
                                GuiMessage.get("MainFrame.jobInputsPanelTitledBorder.title"),
                                Preferences.getInstance().getCurrentParticleSetFilename()));
                        this.mainFrame.getJobInputsPanel().repaint();
                    }
                }
                // Check particle set
                String tmpParticleSetFilenameOfJobInput = ((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex)).getParticleSetFilename();
                if (tmpParticleSetFilenameOfJobInput != null) {
                    if (!tmpParticleSetFilenameOfJobInput.equals(Preferences.getInstance().getCurrentParticleSetFilename())) {
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        if (!GuiUtils.getYesNoDecision(GuiMessage.get("DifferentParticleSets.Title"),
                                String.format(GuiMessage.get("DifferentParticleSets.MessageFormat"), tmpParticleSetFilenameOfJobInput, Preferences.getInstance().getCurrentParticleSetFilename()))) {
                            return;
                        }
                        MouseCursorManagement.getInstance().setWaitCursor();
                    }
                }
                // Check compatibility of particles of job input for current particle set
                String tmpIncompatibleParticles = this.jobUpdateUtils.getIncompatibleParticlesForCurrentParticleSet((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex));
                if (tmpIncompatibleParticles != null) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    JOptionPane.showMessageDialog(null, 
                        String.format(GuiMessage.get("Error.MissingParticle"), tmpIncompatibleParticles),
                        GuiMessage.get("Error.ErrorNotificationTitle"), 
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                // Update complete particle related information if specified
                if (Preferences.getInstance().isParticleUpdateForJobInput()) {
                    tmpSelectedJobInput = this.jobUpdateUtils.correctParticlesAndInteractions((JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex));
                } else {
                    tmpSelectedJobInput = (JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex);
                }
                // Update possible old job input
                this.updateJobInput(tmpSelectedJobInput);
                ValueItemContainer tmpValueItemJobContainer = tmpSelectedJobInput.getValueItemContainer();
                // IMPORTANT: Set current (new) timestamp
                tmpValueItemJobContainer.setValueOfValueItem("Timestamp", ModelUtils.getTimestampInStandardFormat());
                // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Clear movie slicer configuration in Preferences">
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().clear();
                // </editor-fold>
                MouseCursorManagement.getInstance().setDefaultCursor();
                if (DialogValueItemEdit.hasChanged(GuiMessage.get("UseJobInput.Title"), tmpValueItemJobContainer, true, false, false, true)) {
                    this.saveJobInput(new JobInput(tmpValueItemJobContainer));
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "selectExistingJobInputAsTemplate()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
            if (tmpSelectedJobInput != null) {
                // IMPORTANT: Set value item container of job input to null so it will be reinstantiated from file information (otherwise changes may be persistent)
                tmpSelectedJobInput.releaseMemoryOfValueItemContainer();
            }
        }
    }

    /**
     * Saves job input
     *
     * @param aJobInput Job input
     */
    private void saveJobInput(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return;
        }

        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // Save current job input
            if (!aJobInput.save()) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "saveJobInput()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                JobManager.getInstance().getJobInputManager().removeJobInputInJobInputPath(aJobInput);
            }
            // Update job inputs from job input path
            JobManager.getInstance().getJobInputManager().updateJobInputs();
            // JobInputs may have changed so fill job input related list models again!
            this.fillJobInputRelatedListModels();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "saveJobInput()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * View selected job input of job input path
     */
    public void viewSelectedJobInput() {
        try {
            // NOTE: No this.mainFrame.setCursor() since this is performed in this.saveJobInput()
            int tmpSelectedIndex = this.mainFrame.getJobInputsSelectionPanel().getList().getSelectedIndex();
            if (tmpSelectedIndex >= 0) {
                JobInput tmpSelectedJobInput = (JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex);
                ValueItemContainer tmpValueItemJobContainer = tmpSelectedJobInput.getValueItemContainer();
                // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Clear movie slicer configuration in Preferences">
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().clear();
                // </editor-fold>
                DialogValueItemShow.show(String.format(GuiMessage.get("KeyValue"), GuiMessage.get("ShowJobInput.Title"), tmpSelectedJobInput.getDescription()),
                        tmpValueItemJobContainer, false, false);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "viewSelectedJobInput()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Browse selected Job Input folder
     */
    public void browseJobInputFolder() {
        try {
            int tmpSelectedIndex = this.mainFrame.getJobInputsSelectionPanel().getList().getSelectedIndex();
            if (tmpSelectedIndex >= 0) {
                JobInput tmpSelectedJobInput = (JobInput) this.jobDesignInputListModel.getElementAt(tmpSelectedIndex);
                if ((new File(tmpSelectedJobInput.getJobInputPath())).isDirectory()) {
                    GuiUtils.startViewer(tmpSelectedJobInput.getJobInputPath());
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "browseJobInputFolder()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update display">
    /**
     * Updates job design panel display
     */
    private void updateJobDesignDisplay() {
        int tmpIndexOfJobDesignPanel = 1;
        this.mainFrame.getMainTabbedPanel().setEnabledAt(tmpIndexOfJobDesignPanel, true);
        if (!JobManager.getInstance().getJobInputManager().hasJobInputsInInputPath()) {
            // <editor-fold defaultstate="collapsed" desc="No job inputs">
            this.mainFrame.getEditExistingJobInputButton().setVisible(false);
            this.mainFrame.getEditTemplateJobInputButton().setVisible(false);
            this.mainFrame.getViewSelectedJobInputButton().setVisible(false);
            this.mainFrame.getJobInputArchiveButton().setVisible(false);
            this.mainFrame.getBrowseJobInputFolderButton().setVisible(false);
            this.mainFrame.getRemoveSelectedJobInputButton().setVisible(false);
            // </editor-fold>
        } else if (this.jobDesignInputListModel.size() == 0) {
            // <editor-fold defaultstate="collapsed" desc="No filtered job inputs">
            this.mainFrame.getEditExistingJobInputButton().setVisible(false);
            this.mainFrame.getEditTemplateJobInputButton().setVisible(false);
            this.mainFrame.getViewSelectedJobInputButton().setVisible(false);
            this.mainFrame.getJobInputArchiveButton().setVisible(false);
            this.mainFrame.getBrowseJobInputFolderButton().setVisible(false);
            this.mainFrame.getRemoveSelectedJobInputButton().setVisible(false);
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Filtered job inputs exist">
            this.mainFrame.getEditExistingJobInputButton().setVisible(true);
            this.mainFrame.getEditTemplateJobInputButton().setVisible(true);
            this.mainFrame.getViewSelectedJobInputButton().setVisible(true);
            this.mainFrame.getJobInputArchiveButton().setVisible(true);
            this.mainFrame.getBrowseJobInputFolderButton().setVisible(true);
            this.mainFrame.getRemoveSelectedJobInputButton().setVisible(true);
            // </editor-fold>
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Job execution related command methods">
    // <editor-fold defaultstate="collapsed" desc="- Actions">
    /**
     * Edit number of steps for job restart
     */
    public void editNumberOfAdditionalStepsForJobRestart() {
        try {
            ValueItemContainer tmpNumberOfAdditionalStepsForJobRestartValueItemContainer = Preferences.getInstance().getNumberOfAdditionalStepsForJobRestartValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesJobRestartSettingsDialog.title"), tmpNumberOfAdditionalStepsForJobRestartValueItemContainer)) {
                Preferences.getInstance().setEditablePreferences(tmpNumberOfAdditionalStepsForJobRestartValueItemContainer);
                this.mainFrame.getStepsForRestartInfoLabel().setText(String.format(GuiMessage.get("MainFrame.stepsForRestartInfoLabel.text"), String.valueOf(Preferences.getInstance().getNumberOfAdditionalStepsForJobRestart())));
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "editNumberOfAdditionalStepsForJobRestart()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Adds all selected jobs for restart to job execution queue
     */
    public void addSelectedRestartJobsToJobExecutionQueue() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int[] tmpSelectedIndices = this.mainFrame.getSelectJobForRestartSelectionPanel().getList().getSelectedIndices();
            if (tmpSelectedIndices.length > 0 && tmpSelectedIndices[0] >= 0) {
                for (int k = 0; k < tmpSelectedIndices.length; k++) {
                    JobResult tmpSelectedJob = (JobResult) this.mainFrame.getSelectJobForRestartSelectionPanel().getList().getModel().getElementAt(tmpSelectedIndices[k]);
                    // Clone job result ...
                    JobResult tmpJobToBeRestarted = tmpSelectedJob.getClone();
                    // Set job input value item container
                    ValueItemContainer tmpJobInputValueItemContainer = tmpJobToBeRestarted.getJobInput().getValueItemContainer();
                    // ... and add to job execution queue:
                    this.addRestartJobToJobExecutionQueue(tmpJobToBeRestarted, tmpJobInputValueItemContainer);
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "addSelectedRestartJobToJobExecutionQueue()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Edits and adds selected job for restart to job execution queue
     */
    public void editSelectedRestartJobAndAddToJobExecutionQueue() {
        try {
            JobResult tmpSelectedJob = (JobResult) this.mainFrame.getSelectJobForRestartSelectionPanel().getList().getSelectedValue();
            if (tmpSelectedJob == null) {
                return;
            }
            // Clone job result ...
            JobResult tmpJobToBeRestarted = tmpSelectedJob.getClone();
            // <editor-fold defaultstate="collapsed" desc="Edit Job Input (for new input file of Jdpd) of selected job result to be restarted">
            // Set job input value item container
            ValueItemContainer tmpJobInputValueItemContainer = tmpJobToBeRestarted.getJobInput().getValueItemContainer();
            // Set value item display for job restart edit
            HashMap<String, String> tmpDisabledDisplayValueItems = tmpJobInputValueItemContainer.setDefinedDisplay(JdpdValueItemDefinition.getInstance().getValueItemNameMapForJobRestartEdit());
            // Edit Job Input value items
            DialogValueItemEdit.hasChanged(GuiMessage.get("EditJobInputForJobRestart.Title"), tmpJobInputValueItemContainer, true, false, false, true);
            // IMPORTANT: Restore display of value items which were deactivated with tmpJobInputValueItemContainer.setDefinedDisplay() for edit process!
            tmpJobInputValueItemContainer.restoreDefinedDisplay(tmpDisabledDisplayValueItems);
            // </editor-fold>
            MouseCursorManagement.getInstance().setWaitCursor();
            // ... and add to job execution queue:
            this.addRestartJobToJobExecutionQueue(tmpJobToBeRestarted, tmpJobInputValueItemContainer);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "addSelectedRestartJobToJobExecutionQueue()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Adds all selected job inputs to job execution queue
     */
    public void addSelectedJobInputsToJobExecutionQueue() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int[] tmpSelectedIndices = this.mainFrame.getSelectExecutionJobInputSelectionPanel().getList().getSelectedIndices();
            if (tmpSelectedIndices.length > 0 && tmpSelectedIndices[0] >= 0) {
                for (int k = 0; k < tmpSelectedIndices.length; k++) {
                    JobInput tmpSelectedJobInput = (JobInput) this.mainFrame.getSelectExecutionJobInputSelectionPanel().getList().getModel().getElementAt(tmpSelectedIndices[k]);
                    // Create new job result and add job to jobExecutionListModel
                    JobResult tmpJobResult = new JobResult(tmpSelectedJobInput.getDescription(), tmpSelectedJobInput.getJobInputPath());
                    this.jobExecutionListModel.addElement(tmpJobResult);
                    // Select latest job
                    this.mainFrame.getJobExecutionList().setSelectedIndex(this.jobExecutionListModel.getSize() - 1);
                    // Update job execution display
                    this.updateJobExecutionDisplay();
                    // Create job result execution task, add to list and try to start added job if already in simulation
                    if (this.isSubmittedJobResultExecutionTaskWorking()) {
                        JobResultExecutionTask tmpJobResultExecutionTask = new JobResultExecutionTask(tmpJobResult);
                        tmpJobResultExecutionTask.addPropertyChangeListener(this);
                        this.jobResultExecutionTaskList.add(tmpJobResultExecutionTask);
                        this.startRemainingJobExecutionTasks();
                        this.updateStatusDisplay();
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "addSelectedJobInputToJobExecutionQueue()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Cleans job execution queue
     */
    public void cleanJobExecutionQueue() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (this.jobExecutionListModel.getSize() == 0) {
                return;
            }

            // </editor-fold>
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Delete possible additional steps for restart">
            for (int i = 0; i < this.jobExecutionListModel.getSize(); i++) {
                JobResult tmpJobResult = (JobResult) this.jobExecutionListModel.get(i);
                // Set isRestarted
                tmpJobResult.setToBeRestarted(false);
                // Set nsteps for restart
                tmpJobResult.setAdditionalStepsForRestart(0);
            }
            // Repaint: JobResult description changed since job is no longer "to be restarted"
            this.mainFrame.getSelectJobForRestartSelectionPanel().getList().repaint();

            // </editor-fold>
            this.jobExecutionListModel.clear();
            this.updateJobExecutionQueueDisplay();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "cleanJobExecutionQueue()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Decrements position of selected job in job list if possible
     */
    public void decrementPositionInJobList() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpSelectedPosition = this.mainFrame.getJobExecutionList().getSelectedIndex();
            if (tmpSelectedPosition > 0) {
                this.jobExecutionListModel.insertElementAt(this.jobExecutionListModel.get(tmpSelectedPosition), tmpSelectedPosition - 1);
                this.jobExecutionListModel.remove(tmpSelectedPosition + 1);
                this.mainFrame.getJobExecutionList().setSelectedIndex(tmpSelectedPosition - 1);
            }
            this.updateJobExecutionQueueDisplay();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "decrementPositionInJobList()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Kills selected job in execution queue
     */
    public void stopSelectedJobInJobExecutionQueue() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpSelectedPosition = this.mainFrame.getJobExecutionList().getSelectedIndex();
            if (tmpSelectedPosition >= 0 && tmpSelectedPosition < this.jobExecutionListModel.size()) {
                JobResult tmpJobResult = (JobResult) this.jobExecutionListModel.get(tmpSelectedPosition);
                if (this.isSubmittedJobResultWorkingNonStopped(tmpJobResult)) {
                    // <editor-fold defaultstate="collapsed" desc="Stop working job result execution task if agreed">
                    if (!GuiUtils.getYesNoDecision(GuiMessage.get("StopSelectedJob.Title"), GuiMessage.get("StopSelectedJob.Message"))) {
                        return;
                    }
                    this.stopJobResult(tmpJobResult);
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Remove non-started from execution queue">
                    this.removeSelectedNonSubmittedJobFromExecutionQueue();
                    // </editor-fold>
                }
            }
            this.updateJobExecutionQueueDisplay();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "killSelectedJobInJobExecutionQueue()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Starts jobs in execution queue
     */
    public void startJobsInJobExecutionQueue() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // Clear temporary directory in advance
            // NOTE: Clear-operation may take seconds up to minutes, so it is performed with separated thread
            new FileDeletionTask((new File(Preferences.getInstance().getTempPath())).listFiles()).start();
            if (this.jobExecutionListModel.getSize() > 0) {
                this.jobResultExecutionTaskList = new ConcurrentLinkedQueue<JobResultExecutionTask>();
                for (int i = 0; i < this.jobExecutionListModel.getSize(); i++) {
                    JobResult tmpJobResultToBeExecuted = (JobResult) this.jobExecutionListModel.get(i);
                    if (!tmpJobResultToBeExecuted.hasValidJobInputPath()) {
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.NonExistingJobInputInJobExecutionQueue"), tmpJobResultToBeExecuted.toString()), 
                            GuiMessage.get("Information.NotificationTitle"),
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    JobResultExecutionTask tmpJobResultExecutionTask = new JobResultExecutionTask(tmpJobResultToBeExecuted);
                    tmpJobResultExecutionTask.addPropertyChangeListener(this);
                    this.jobResultExecutionTaskList.add(tmpJobResultExecutionTask);
                }
                this.startRemainingJobExecutionTasks();
                this.updateJobExecutionDisplay();
                this.updateStatusDisplay();
                this.updateMenuDisplay();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "startJobsInJobExecutionQueue()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Increments position of selected job in job list if possible
     */
    public void incrementPositionInJobList() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpSelectedPosition = this.mainFrame.getJobExecutionList().getSelectedIndex();
            if (tmpSelectedPosition >= 0 && tmpSelectedPosition < this.jobExecutionListModel.getSize() - 1) {
                this.jobExecutionListModel.insertElementAt(this.jobExecutionListModel.get(tmpSelectedPosition), tmpSelectedPosition + 2);
                this.jobExecutionListModel.remove(tmpSelectedPosition);
                this.mainFrame.getJobExecutionList().setSelectedIndex(tmpSelectedPosition + 1);
            }
            this.updateJobExecutionQueueDisplay();
            MouseCursorManagement.getInstance().setDefaultCursor();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "incrementPositionInJobList()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Removes selected job in job execution queue
     */
    public void removeSelectedJobInJobExecutionQueue() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpSelectedPosition = this.mainFrame.getJobExecutionList().getSelectedIndex();
            if (tmpSelectedPosition >= 0) {

                // <editor-fold defaultstate="collapsed" desc="Delete possible additional steps for restart">
                JobResult tmpJobResult = (JobResult) this.jobExecutionListModel.get(tmpSelectedPosition);
                // Set isRestarted
                tmpJobResult.setToBeRestarted(false);
                // Set nsteps for restart
                tmpJobResult.setAdditionalStepsForRestart(0);
                // Repaint: JobResult description changed since job is no longer "to be restarted"
                this.mainFrame.getSelectJobForRestartSelectionPanel().getList().repaint();

                // </editor-fold>
                this.jobExecutionListModel.remove(tmpSelectedPosition);
                if (tmpSelectedPosition == this.jobExecutionListModel.getSize()) {
                    this.mainFrame.getJobExecutionList().setSelectedIndex(tmpSelectedPosition - 1);
                } else {
                    this.mainFrame.getJobExecutionList().setSelectedIndex(tmpSelectedPosition);
                }
            }
            this.updateJobExecutionQueueDisplay();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "removeSelectedJobInJobExecutionQueue()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Shows progress
     */
    public void showProgress() {
        JobResult tmpSelectedJobResult = null;
        try {
            // Do ONLY show progress if jobs are executed
            if (this.isSubmittedJobResultExecutionTaskWorking()) {
                MouseCursorManagement.getInstance().setWaitCursor();
                int tmpSelectedPosition = this.mainFrame.getJobExecutionList().getSelectedIndex();
                if (tmpSelectedPosition >= 0 && tmpSelectedPosition < this.jobExecutionListModel.size()) {
                    tmpSelectedJobResult = (JobResult) this.jobExecutionListModel.get(tmpSelectedPosition);
                    // IMPORTANT: Lock job result path during view!
                    String tmpJobResultPath = tmpSelectedJobResult.getJobResultPath();
                    tmpSelectedJobResult.lockResultPath();
                    if (tmpSelectedJobResult.getJobInput() != null) {
                        // A job is in execution queue AND started
                        // Write simulation step properties for progress view
                        if (tmpSelectedJobResult.hasJdpdFileOutput()) {
                            tmpSelectedJobResult.getJdpdFileOutput().writeSimulationStepProperties();
                        }
                        // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Clear movie slicer configuration in Preferences">
                        Preferences.getInstance().getSimulationMovieSlicerConfiguration().clear();
                        // </editor-fold>
                        boolean tmpIsNearestNeighbors = false;
                        if (Preferences.getInstance().isNearestNeighborEvaluationInclusion() && tmpSelectedJobResult.hasNearestNeighbors()) {
                            MouseCursorManagement.getInstance().setDefaultCursor();
                            if (GuiUtils.getYesNoDecision(GuiMessage.get("NearestNeighborEvaluation.Title"), GuiMessage.get("NearestNeighborEvaluation.Message"))) {
                                tmpIsNearestNeighbors = true;
                            }
                            MouseCursorManagement.getInstance().setWaitCursor();
                        }
                        ValueItemContainer tmpResultValueItemContainerForJobResult = tmpSelectedJobResult.getResultValueItemContainerForJobResult(tmpIsNearestNeighbors);
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        DialogValueItemShow.show(
                            String.format(
                                GuiMessage.get("KeyValue"), 
                                GuiMessage.get("SimulationProgress.ViewTitle"), 
                                tmpSelectedJobResult.getDescription()
                            ), 
                            tmpResultValueItemContainerForJobResult, 
                            true, 
                            true
                        );
                        // Unlock job result path
                        tmpSelectedJobResult.unlockResultPath();
                        // IMPORTANT: Delete job result path if job was finished during progress view
                        if (this.isJobResultFinished(tmpSelectedJobResult)) {
                            new FileDeletionTask(new File[]{new File(tmpJobResultPath)}).start();
                        }
                    } else {
                        // A job is in execution queue but NOT started
                        JOptionPane.showMessageDialog(null, 
                            GuiMessage.get("Information.JobIsNotStarted"), 
                            GuiMessage.get("Information.NotificationTitle"),
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showProgress()", "MainFrameController"), 
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            // Unlock job result path
            if (tmpSelectedJobResult != null) {
                tmpSelectedJobResult.unlockResultPath();
            }
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update display">
    /**
     * Updates job execution display
     */
    private void updateJobExecutionDisplay() {
        int tmpIndexOfJobExecutionPanel = 2;
        if (this.jobExecutionInputListModel.size() == 0 && 
            this.jobRestartListModel.size() == 0 &&
            !this.isSubmittedJobResultExecutionTaskWorking()
        ) {
            // <editor-fold defaultstate="collapsed" desc="Disable job execution tab">
            // No Jdpd available or no job inputs and no jobs for restart: Disable job execution tab
            this.mainFrame.getMainTabbedPanel().setEnabledAt(tmpIndexOfJobExecutionPanel, false);
            // ... and switch to job design tab if job execution tab is selected
            if (this.mainFrame.getMainTabbedPanel().getSelectedIndex() == tmpIndexOfJobExecutionPanel) {
                int tmpIndexOfJobDesignPanel = 1;
                this.mainFrame.getMainTabbedPanel().setSelectedIndex(tmpIndexOfJobDesignPanel);
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Enable job execution tab">
            // There are jobs in result path: Enable job execution tab
            this.mainFrame.getMainTabbedPanel().setEnabledAt(tmpIndexOfJobExecutionPanel, true);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update display">
            this.updateJobExecutionSelectJobInputDisplay();
            this.updateJobExecutionSelectJobForRestartDisplay();
            this.updateJobExecutionQueueDisplay();
            // </editor-fold>
        }
    }

    /**
     * Updates job execution display: selectTabbedPanel - Job input display
     */
    private void updateJobExecutionSelectJobInputDisplay() {
        if (this.jobExecutionInputListModel.size() > 0) {
            // <editor-fold defaultstate="collapsed" desc="Correct job inputs exists">
            this.mainFrame.getSelectTabbedPanel().setEnabledAt(0, true);
            this.mainFrame.getAddExecutionJobInputButton().setVisible(true);
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="No job inputs">
            this.mainFrame.getSelectTabbedPanel().setEnabledAt(0, false);
            if (this.mainFrame.getSelectTabbedPanel().getSelectedIndex() == 0) {
                this.mainFrame.getSelectTabbedPanel().setSelectedIndex(1);
            }
            // </editor-fold>
        }
    }

    /**
     * Updates job execution display: selectTabbedPanel - Job for restart
     * display
     */
    private void updateJobExecutionSelectJobForRestartDisplay() {
        if (this.jobRestartListModel.size() > 0) {
            // <editor-fold defaultstate="collapsed" desc="Job results exists">
            this.mainFrame.getSelectTabbedPanel().setEnabledAt(1, true);
            this.mainFrame.getAddExecutionJobForRestartButton().setVisible(true);
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="No job results">
            this.mainFrame.getSelectTabbedPanel().setEnabledAt(1, false);
            if (this.mainFrame.getSelectTabbedPanel().getSelectedIndex() == 1) {
                this.mainFrame.getSelectTabbedPanel().setSelectedIndex(0);
            }
            // </editor-fold>
        }
    }

    /**
     * Updates job execution display: jobExecutionQueuePanel
     */
    public void updateJobExecutionQueueDisplay() {
        try {
            if (this.isSubmittedJobResultExecutionTaskWorking()) {
                // <editor-fold defaultstate="collapsed" desc="Job is in simulation">
                this.mainFrame.getPositionDownInJobExecutionQueueButton().setVisible(false);
                this.mainFrame.getPositionUpInJobExecutionQueueButton().setVisible(false);
                this.mainFrame.getRemoveJobfromJobExecutionQueueButton().setVisible(false);
                this.mainFrame.getCleanJobExecutionQueueButton().setVisible(false);
                this.mainFrame.getStartJobExecutionButton().setVisible(false);
                if (this.getNumberOfNonSubmittedJobResultExecutionTasks() > 0) {
                    this.mainFrame.getStopSelectedJobButton().setVisible(true);
                    if (this.getNumberOfNonStoppedJobResultExecutionTasks() > 0) {
                        this.mainFrame.getShowProgressButton().setVisible(true);
                    } else {
                        this.mainFrame.getShowProgressButton().setVisible(false);
                    }
                } else {
                    if (this.getNumberOfNonStoppedJobResultExecutionTasks() > 0) {
                        this.mainFrame.getShowProgressButton().setVisible(true);
                        this.mainFrame.getStopSelectedJobButton().setVisible(true);
                    } else {
                        this.mainFrame.getShowProgressButton().setVisible(false);
                        this.mainFrame.getStopSelectedJobButton().setVisible(false);
                    }
                }

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="No job in simulation">
                this.mainFrame.getShowProgressButton().setVisible(false);
                this.mainFrame.getStopSelectedJobButton().setVisible(false);
                if (this.jobExecutionListModel.getSize() == 0) {
                    this.mainFrame.getStartJobExecutionButton().setVisible(false);
                    this.mainFrame.getCleanJobExecutionQueueButton().setVisible(false);
                    this.mainFrame.getRemoveJobfromJobExecutionQueueButton().setVisible(false);
                    this.mainFrame.getPositionUpInJobExecutionQueueButton().setVisible(false);
                    this.mainFrame.getPositionDownInJobExecutionQueueButton().setVisible(false);
                } else if (this.jobExecutionListModel.getSize() == 1) {
                    this.mainFrame.getStartJobExecutionButton().setVisible(true);
                    this.mainFrame.getCleanJobExecutionQueueButton().setVisible(true);
                    this.mainFrame.getRemoveJobfromJobExecutionQueueButton().setVisible(true);
                    this.mainFrame.getPositionUpInJobExecutionQueueButton().setVisible(false);
                    this.mainFrame.getPositionDownInJobExecutionQueueButton().setVisible(false);
                } else {
                    this.mainFrame.getStartJobExecutionButton().setVisible(true);
                    this.mainFrame.getCleanJobExecutionQueueButton().setVisible(true);
                    this.mainFrame.getRemoveJobfromJobExecutionQueueButton().setVisible(true);
                    if (this.mainFrame.getJobExecutionList().getSelectedIndex() == 0) {
                        this.mainFrame.getPositionUpInJobExecutionQueueButton().setVisible(false);
                    } else {
                        this.mainFrame.getPositionUpInJobExecutionQueueButton().setVisible(true);
                    }
                    if (this.mainFrame.getJobExecutionList().getSelectedIndex() == this.jobExecutionListModel.getSize() - 1) {
                        this.mainFrame.getPositionDownInJobExecutionQueueButton().setVisible(false);
                    } else {
                        this.mainFrame.getPositionDownInJobExecutionQueueButton().setVisible(true);
                    }
                }

                // </editor-fold>
            }
            // Set selection of job execution list if necessary
            if (this.jobExecutionListModel.getSize() > 0) {
                if (this.mainFrame.getJobExecutionList().getSelectedIndex() < 0 || this.mainFrame.getJobExecutionList().getSelectedIndex() >= this.jobExecutionListModel.getSize()) {
                    this.mainFrame.getJobExecutionList().setSelectedIndex(0);
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "updateJobExecutionQueueDisplay()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Job results related command methods">
    // <editor-fold defaultstate="collapsed" desc="- Actions">
    /**
     * Archives selected job result
     */
    public void archiveJobResult() {
        try {
            int tmpSelectedIndex = this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex();
            if (tmpSelectedIndex >= 0) {
                String tmpArchiveFilePathname = 
                    GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectSingleJobArchiveChooser"),
                        GuiDefinitions.JOB_RESULT_ARCHIVE_FILE_EXTENSION
                    );
                if (tmpArchiveFilePathname != null) {
                    try {
                        // <editor-fold defaultstate="collapsed" desc="Move job input to archive file">
                        JobResult tmpJobResultToBeArchived = (JobResult) this.mainFrame.getSelectJobResultPanel().getList().getSelectedValue();
                        String tmpFileExclusionRegexPatternString = null;
                        if (!Preferences.getInstance().isJobResultArchiveStepFileInclusion()) {
                            // Exclude particle position step files from archiving
                            tmpFileExclusionRegexPatternString = "^" + FileOutputStrings.PARTICLE_POSITIONS_SIMULATION_STEP_FILE_PREFIX + ".*";
                        }
                        boolean tmpIsUncompressed = Preferences.getInstance().isJobResultArchiveFileUncompressed();
                        if (Preferences.getInstance().isJobResultArchiveProcessParallelInBackground()) {
                            ArchiveTask tmpJobArchiveTask = 
                                new ArchiveTask(
                                    tmpJobResultToBeArchived.getJobResultPath(), 
                                    tmpArchiveFilePathname, 
                                    tmpFileExclusionRegexPatternString, 
                                    tmpIsUncompressed
                                );
                            tmpJobArchiveTask.addPropertyChangeListener(this);
                            if (this.jobArchiveTaskList == null) {
                                this.jobArchiveTaskList = new ConcurrentLinkedQueue<>();
                            }
                            this.jobArchiveTaskList.add(tmpJobArchiveTask);
                            if (this.jobArchiveExecutorService == null) {
                                this.jobArchiveExecutorService = Executors.newSingleThreadExecutor();
                            }
                            tmpJobArchiveTask.setSubmittedToExecutorService();
                            this.jobArchiveExecutorService.submit(tmpJobArchiveTask);
                            this.updateStatusDisplay();
                        } else {
                            ArchiveTask tmpArchiveTask = 
                                new ArchiveTask(
                                    tmpJobResultToBeArchived.getJobResultPath(), 
                                    tmpArchiveFilePathname, 
                                    tmpFileExclusionRegexPatternString, 
                                    tmpIsUncompressed
                                );
                            DialogProgress.hasCanceled(GuiMessage.get("Archiving"), tmpArchiveTask);
                        }
                        // </editor-fold>
                    } catch (Exception anExeption) {
                        // <editor-fold defaultstate="collapsed" desc="Show error message">
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SaveOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "archiveJobInput()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Edit job result archive preferences
     */
    public void editJobResultArchivePreferences() {
        try {
            ValueItemContainer tmpJobResultArchiveSettingsValueItemContainer = Preferences.getInstance().getJobResultArchiveEditablePrefencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesJobResultArchiveSettingsDialog.title"), tmpJobResultArchiveSettingsValueItemContainer)) {
                Preferences.getInstance().setEditablePreferences(tmpJobResultArchiveSettingsValueItemContainer);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "editJobResultArchivePreferences()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Edit job result settings preferences
     */
    public void editJobResultSettingsPreferences() {
        try {
            ValueItemContainer tmpJobResultSettingsValueItemContainer = Preferences.getInstance().getJobResultSettingsEditablePrefencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesJobResultSettingsDialog.title"), tmpJobResultSettingsValueItemContainer)) {
                Preferences.getInstance().setEditablePreferences(tmpJobResultSettingsValueItemContainer);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "editJobResultSettingsPreferences()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        }
    }

    /**
     * Imports job result(s) from archive file(s)
     */
    public void importJobResultsFromArchiveZipFiles() {
        try {
            String[] tmpJobResultImportFilePathnames = 
                GuiUtils.selectMultipleFiles(Preferences.getInstance().getLastSelectedPath(),
                    GuiMessage.get("Chooser.selectSingleJobImportChooser"), 
                    new ExtensionFileFilter(new String[]{ GuiDefinitions.JOB_RESULT_ARCHIVE_FILE_EXTENSION })
                );
            // <editor-fold defaultstate="collapsed" desc="Checks">
            for (String tmpJobResultImportFilePathname : tmpJobResultImportFilePathnames) {
                if (tmpJobResultImportFilePathname == null || tmpJobResultImportFilePathname.isEmpty() || !(new File(tmpJobResultImportFilePathname)).isFile()) {
                    return;
                }
                if (!tmpJobResultImportFilePathname.toLowerCase(Locale.ENGLISH).endsWith(GuiDefinitions.JOB_RESULT_ARCHIVE_FILE_EXTENSION.toLowerCase(Locale.ENGLISH))) {
                    // <editor-fold defaultstate="collapsed" desc="Unknown file type">
                    JOptionPane.showMessageDialog(null, 
                        GuiMessage.get("Error.UnknownJobResultFileType"), 
                        GuiMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                    // </editor-fold>
                }
                String tmpJobResultDirectoryName = this.fileUtilityMethods.getDirectoryNameFromZipFile(tmpJobResultImportFilePathname);
                if (tmpJobResultDirectoryName == null || tmpJobResultDirectoryName.isEmpty() || !tmpJobResultDirectoryName.startsWith(ModelDefinitions.PREFIX_OF_JOB_RESULT_DIRECTORY)) {
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Error.NoJobResultZipMainDirectory"), 
                        GuiMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                String tmpNewJobResultDirectoryPath = Preferences.getInstance().getJobResultPath() + File.separatorChar + tmpJobResultDirectoryName;
                if ((new File(tmpNewJobResultDirectoryPath)).isDirectory()) {
                    JOptionPane.showMessageDialog(null, 
                        GuiMessage.get("Information.JobResultAlreadyExists"), 
                        GuiMessage.get("Information.InformationFrameTitle"),
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            }
            // </editor-fold>
            try {
                MouseCursorManagement.getInstance().setWaitCursor();
                for (String tmpJobResultImportFilePathname : tmpJobResultImportFilePathnames) {
                    // <editor-fold defaultstate="collapsed" desc="Import job result archive file">
                    try {
                        if (!this.fileUtilityMethods.extractDirectoryFromZipFile(Preferences.getInstance().getJobResultPath(), tmpJobResultImportFilePathname)) {
                            MouseCursorManagement.getInstance().setDefaultCursor();
                            JOptionPane.showMessageDialog(null, 
                                GuiMessage.get("Error.CanNotExtractJobResultZipFile"), 
                                GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }
                        // Update job results from job result path
                        JobManager.getInstance().getJobResultManager().updateJobResults();
                        // Fill jobResultListModel/jobRestartListModel since jobs results have changed
                        this.fillJobResultRelatedListModels();
                        this.updateJobExecutionDisplay();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Error.CanNotExtractJobInputZipFile"), 
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                    // </editor-fold>
                }
            } finally {
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "importJobInput()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        }
    }

    /**
     * Removes all selected jobs in job result list
     */
    public void removeSelectedJobResults() {
        try {
            int[] tmpSelectedIndices = this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndices();
            if (tmpSelectedIndices.length > 0 && tmpSelectedIndices[0] >= 0) {
                // <editor-fold defaultstate="collapsed" desc="Confirm dialog and job removal">
                if (GuiUtils.getYesNoDecision(GuiMessage.get("RemoveJobResults.FrameTitle"), GuiMessage.get("RemoveJobResults.Message"))) {
                    MouseCursorManagement.getInstance().setWaitCursor();
                    // Important: Proceed form highest to lowest index!
                    for (int k = tmpSelectedIndices.length - 1; k >= 0; k--) {
                        JobResult tmpJobToBeRemoved = (JobResult) this.mainFrame.getSelectJobResultPanel().getList().getModel().getElementAt(tmpSelectedIndices[k]);
                        if (!JobManager.getInstance().getJobResultManager().removeJobInResultPath(tmpJobToBeRemoved)) {
                            // <editor-fold defaultstate="collapsed" desc="Message NoSelectedJobRemoval">
                            JOptionPane.showMessageDialog(null, GuiMessage.get("Error.NoSelectedJobResultRemoval"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE);
                            // </editor-fold>
                        } else {
                            this.jobResultListModel.remove(tmpSelectedIndices[k]);
                        }
                    }
                    // Clean job result path in background after removal
                    JobManager.getInstance().getJobResultManager().cleanJobResultPathInBackground();
                    if (tmpSelectedIndices.length == 1) {
                        if (tmpSelectedIndices[0] == this.jobResultListModel.getSize()) {
                            this.mainFrame.getSelectJobResultPanel().getList().setSelectedIndex(tmpSelectedIndices[0] - 1);
                        } else {
                            this.mainFrame.getSelectJobResultPanel().getList().setSelectedIndex(tmpSelectedIndices[0]);
                        }
                    } else {
                        this.mainFrame.getSelectJobResultPanel().getList().setSelectedIndex(0);
                    }
                    // Job results changed: Update/re-fill jobRestartListModel
                    this.fillJobRestartListModel();
                    this.updateJobResultsDisplay();
                    this.updateJobExecutionDisplay();
                }
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "removeSelectedJobResult()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * View selected job result in job result list
     */
    public void viewSelectedJobResult() {
        try {
            if (this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex() >= 0 && this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex() < this.jobResultListModel.size()) {
                MouseCursorManagement.getInstance().setWaitCursor();
                int tmpSelectedIndex = this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex();
                JobResult tmpSelectedJobResult = (JobResult) this.jobResultListModel.getElementAt(tmpSelectedIndex);
                // <editor-fold defaultstate="collapsed" desc="Check version of job result">
                String tmpVersionOfJobResult = tmpSelectedJobResult.getMFsimApplicationVersion();
                if (tmpVersionOfJobResult == null || !ModelUtils.isVersionOneEqualOrHigher(tmpVersionOfJobResult, ModelDefinitions.MINIMUM_JOB_RESULT_APPLICATION_VERSION)) {
                    // Job input application version is NOT allowed
                    JOptionPane.showMessageDialog(null, 
                        String.format(GuiMessage.get("Error.SelectedJobResultForbiddenVersionFormat"), ModelDefinitions.MINIMUM_JOB_RESULT_APPLICATION_VERSION), 
                        GuiMessage.get("Information.NotificationTitle"),
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Clear movie slicer configuration in Preferences">
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().clear();
                // </editor-fold>
                boolean tmpIsNearestNeighbors = false;
                if (Preferences.getInstance().isNearestNeighborEvaluationInclusion() && tmpSelectedJobResult.hasNearestNeighbors()) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    if (GuiUtils.getYesNoDecision(GuiMessage.get("NearestNeighborEvaluation.Title"), GuiMessage.get("NearestNeighborEvaluation.Message"))) {
                        tmpIsNearestNeighbors = true;
                    }
                    MouseCursorManagement.getInstance().setWaitCursor();
                }
                ValueItemContainer tmpResultValueItemContainerForJobResult = tmpSelectedJobResult.getResultValueItemContainerForJobResult(tmpIsNearestNeighbors);
                MouseCursorManagement.getInstance().setDefaultCursor();
                DialogValueItemShow.show(
                    String.format(
                        GuiMessage.get("KeyValue"), 
                        GuiMessage.get("JobResults.ViewTitle"), 
                        tmpSelectedJobResult.getDescription()
                    ),
                    tmpResultValueItemContainerForJobResult, 
                    true, 
                    true
                );
                // Remove job input after dialog to free memory
                tmpSelectedJobResult.removeJobInput();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "viewSelectedJobResult()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Browse selected Job Result folder
     */
    public void browseJobResultFolder() {
        try {
            if (this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex() >= 0 && this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex() < this.jobResultListModel.size()) {
                int tmpSelectedIndex = this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex();
                JobResult tmpSelectedJobResult = (JobResult) this.jobResultListModel.getElementAt(tmpSelectedIndex);
                if ((new File(tmpSelectedJobResult.getJobResultPath())).isDirectory()) {
                    GuiUtils.startViewer(tmpSelectedJobResult.getJobResultPath());
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "browseJobResultFolder()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * View Jdpd log file
     */
    public void viewLogfile() {
        try {
            if (this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex() >= 0 && this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex() < this.jobResultListModel.size()) {
                int tmpSelectedIndex = this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndex();
                JobResult tmpSelectedJobResult = (JobResult) this.jobResultListModel.getElementAt(tmpSelectedIndex);
                if ((new File(tmpSelectedJobResult.getJdpdLogfilePathname())).isFile()) {
                    GuiUtils.startViewer(tmpSelectedJobResult.getJdpdLogfilePathname());
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "viewLogfile()", "MainFrameController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }

    /**
     * Copies job inputs of selected job results to job design
     */
    public void copySelectedJobInputs() {
        try {
            int[] tmpSelectedIndices = this.mainFrame.getSelectJobResultPanel().getList().getSelectedIndices();
            if (tmpSelectedIndices.length > 0 && tmpSelectedIndices[0] >= 0) {
                MouseCursorManagement.getInstance().setWaitCursor();
                // <editor-fold defaultstate="collapsed" desc="Copy job inputs of selected job results">
                boolean hasCopied = false;
                for (int k = tmpSelectedIndices.length - 1; k >= 0; k--) {
                    JobResult tmpSelectedJobResult = (JobResult) this.mainFrame.getSelectJobResultPanel().getList().getModel().getElementAt(tmpSelectedIndices[k]);
                    // NOTE: Job input directory MUST exist!
                    String tmpJobInputDirectoryPathInJobResult = null;
                    String[] tmpJobInputDirectoryPathInJobResultArray = this.fileUtilityMethods.getDirectoryPathsWithPrefix(tmpSelectedJobResult.getJobResultPath(), ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY);
                    if (tmpJobInputDirectoryPathInJobResultArray != null) {
                        tmpJobInputDirectoryPathInJobResult = tmpJobInputDirectoryPathInJobResultArray[0];
                    }
                    String tmpJobInputDirectoryName = (new File(tmpJobInputDirectoryPathInJobResult)).getName();
                    String tmpJobInputDirectoryPath = Preferences.getInstance().getJobInputPath() + File.separatorChar + tmpJobInputDirectoryName;
                    if (!(new File(tmpJobInputDirectoryPath).exists())) {
                        this.fileUtilityMethods.copyIntoDirectory(tmpJobInputDirectoryPathInJobResult, tmpJobInputDirectoryPath);
                        hasCopied = true;
                    }
                }
                if (hasCopied) {
                    // Update job inputs from job input path
                    JobManager.getInstance().getJobInputManager().updateJobInputs();
                    // JobInputs may have changed so fill job input related list models again!
                    this.fillJobInputRelatedListModels();
                } else {
                    // Show message that nothing was copied
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Application.NoCopyOfJobInputsInformation"),
                            GuiMessage.get("Application.NoCopyOfJobInputsInformation.Title"), JOptionPane.INFORMATION_MESSAGE);
                }
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "copySelectedJobInputs()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update display">
    /**
     * Updates job results display
     */
    private void updateJobResultsDisplay() {
        this.mainFrame.getRemoveSelectedJobResultButton().setVisible(this.jobResultListModel.size() > 0 && !this.isJobArchivingTaskSubmitted());
        this.mainFrame.getViewSelectedJobResultButton().setVisible(this.jobResultListModel.size() > 0);
        this.mainFrame.getJobResultArchiveButton().setVisible(this.jobResultListModel.size() > 0);
        this.mainFrame.getJobResultArchivePreferencesEditButton().setVisible(this.jobResultListModel.size() > 0);
        this.mainFrame.getJobResultSettingsPreferencesEditButton().setVisible(this.jobResultListModel.size() > 0);
        this.mainFrame.getCopySelectedJobInputsButton().setVisible(this.jobResultListModel.size() > 0);
        this.mainFrame.getBrowseJobResultFolderButton().setVisible(this.jobResultListModel.size() > 0);
        this.mainFrame.getViewLogfileButton().setVisible(this.jobResultListModel.size() > 0);
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Directories related methods">
    /**
     * Edit directories
     */
    public void editDirectories() {
        try {
            ValueItemContainer tmpDirectoriesValueItemContainer = Preferences.getInstance().getDirectoriesEditablePreferencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesDirectoriesDialog.title"), tmpDirectoriesValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                Preferences.getInstance().setEditablePreferences(tmpDirectoriesValueItemContainer);
                this.doActionsAfterChangingPreferences();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "editDirectories()", "MainFrameController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Filter related edit/clear methods">
    /**
     * Clears job input filter
     */
    public void clearJobInputFilter() {
        try {
            if (GuiUtils.getYesNoDecision(GuiMessage.get("JobInputFilter.ClearFrameTitle"), GuiMessage.get("JobInputFilter.ClearMessage"))) {
                MouseCursorManagement.getInstance().setWaitCursor();
                if (Preferences.getInstance().clearJobInputFilter()) {
                    this.fillJobInputRelatedListModels();
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "clearJobInputFilter()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Clears job result filter
     */
    public void clearJobResultFilter() {
        try {
            if (GuiUtils.getYesNoDecision(GuiMessage.get("JobResultFilter.ClearFrameTitle"), GuiMessage.get("JobResultFilter.ClearMessage"))) {
                MouseCursorManagement.getInstance().setWaitCursor();
                if (Preferences.getInstance().clearJobResultFilter()) {
                    this.fillJobResultRelatedListModels();
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "clearJobResultFilter()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Edit job input filter
     */
    public void editJobInputFilter() {
        try {
            ValueItemContainer tmpJobInputFilterValueItemContainer = Preferences.getInstance().getJobInputFilterEditablePreferencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesJobInputFilterDialog.title"), tmpJobInputFilterValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                Preferences.getInstance().setEditablePreferences(tmpJobInputFilterValueItemContainer);
                this.fillJobInputRelatedListModels();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "editJobInputFilter()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Edit job result filter
     */
    public void editJobResultFilter() {
        try {
            ValueItemContainer tmpJobResultFilterValueItemContainer = Preferences.getInstance().getJobResultFilterEditablePreferencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesJobResultFilterDialog.title"), tmpJobResultFilterValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                Preferences.getInstance().setEditablePreferences(tmpJobResultFilterValueItemContainer);
                this.fillJobResultRelatedListModels();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "editJobResultFilter()", "MainFrameController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Job update related methods">
    /**
     * Updates (legacy) job input for compatibility if necessary
     * 
     * @param tmpJobInput Job input to be updated
     */
    private void updateJobInput(JobInput tmpJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (tmpJobInput == null) {
            return;
        }
        // </editor-fold>
        // Do NOT change the order of following methods!
        this.jobUpdateUtils.updateMoleculeAccelerationForMaxTimeStep(tmpJobInput);
        this.jobUpdateUtils.updateMoleculeFixationForMaxTimeStep(tmpJobInput);
        this.jobUpdateUtils.updateMoleculeBoundaryForMaxTimeStep(tmpJobInput);
        this.jobUpdateUtils.updateMoleculeFixedVelocityForMaxTimeStep(tmpJobInput);
        this.jobUpdateUtils.updateVelocityScalingInformation(tmpJobInput);
        this.jobUpdateUtils.updateRandomNumberGenerator(tmpJobInput);
        this.jobUpdateUtils.updateMoleculeBackboneForcesWithBehaviour(tmpJobInput);
        this.jobUpdateUtils.updateProteinBackboneForcesWithBehaviour(tmpJobInput);
        this.jobUpdateUtils.insertGeometryRandomSeedValueItem(tmpJobInput);
        this.jobUpdateUtils.updateInitialPotentialEnergyMinimizationStepNumber(tmpJobInput);
        this.jobUpdateUtils.updateElectrostatics(tmpJobInput);
        this.jobUpdateUtils.addMoleculeCharge(tmpJobInput);
        this.jobUpdateUtils.updateMoleculeBoundaryForMoleculeNameSelectionTexts(tmpJobInput);
        this.jobUpdateUtils.insertMoleculeSphereValueItem(tmpJobInput);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Preference change related methods">
    /**
     * Performs several actions directly after changing preferences
     */
    private void doActionsAfterChangingPreferences() {
        // <editor-fold defaultstate="collapsed" desc="Update display of job design, execution and result display">
        this.updateJobDesignDisplay();
        this.updateJobExecutionDisplay();
        this.updateJobResultsDisplay();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set main frame title (MUST be set AFTER application mode setting)">
        this.mainFrame.setTitle(this.getApplicationTitle());
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set particle set filename in jobInputsPanelTitledBorder">
        this.mainFrame.getJobInputsPanelTitledBorder().setTitle(String.format(GuiMessage.get("JobInputsPanelTitledBorder.TitleFormat"),
                        GuiMessage.get("MainFrame.jobInputsPanelTitledBorder.title"),
                        Preferences.getInstance().getCurrentParticleSetFilename()));
        this.mainFrame.getJobInputsPanel().repaint();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Manage possible job input changes">
        // Update job input path related variables and displays since job input path may have changed
        JobManager.getInstance().getJobInputManager().updateJobInputs();
        // Clean possible removed jobs inputs in background
        JobManager.getInstance().getJobInputManager().cleanJobInputPathInBackground();
        // Fill fillExistingJobInputsListModel since jobs inputs may have changed
        this.fillJobInputRelatedListModels();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Manage possible job result changes">
        // Reset job result path related variables and displays since job result path may have changed
        JobManager.getInstance().getJobResultManager().updateJobResults();
        // Clean possible removed jobs results in background
        JobManager.getInstance().getJobResultManager().cleanJobResultPathInBackground();
        // Fill jobResultListModel/jobRestartListModel since jobs results may have changed
        this.fillJobResultRelatedListModels();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update menu display">
        this.updateMenuDisplay();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update status display">
        this.updateStatusDisplay();
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job execution related methods">
    /**
     * Adds selected job for restart to job execution queue
     *
     * @param aJobToBeRestarted Job to be restarted (may be null then nothing
     * happens)
     * @param aJobInputValueItemContainer Job input value item container (may be
     * null then nothing happens)
     */
    private void addRestartJobToJobExecutionQueue(JobResult aJobToBeRestarted, ValueItemContainer aJobInputValueItemContainer) {
        if (aJobToBeRestarted != null && aJobInputValueItemContainer != null) {
            // Set (possible) new Jdpd input text
            aJobToBeRestarted.setNewJdpdInputTextWithJobInputValueItemContainer(aJobInputValueItemContainer);
            // Set isRestarted
            aJobToBeRestarted.setToBeRestarted(true);
            // Set additional steps for restart
            aJobToBeRestarted.setAdditionalStepsForRestart(Preferences.getInstance().getNumberOfAdditionalStepsForJobRestart());
            // Repaint: JobResult description changed since job is now "to be restarted"
            this.mainFrame.getSelectJobForRestartSelectionPanel().getList().repaint();
            // Add job to jobExecutionListModel
            this.jobExecutionListModel.addElement(aJobToBeRestarted);
            // Select latest job
            this.mainFrame.getJobExecutionList().setSelectedIndex(this.jobExecutionListModel.getSize() - 1);
            // Update job execution display
            this.updateJobExecutionDisplay();
            // Create job result execution task, add to list and try to start added job if already in simulation
            if (this.isSubmittedJobResultExecutionTaskWorking()) {
                JobResultExecutionTask tmpJobResultExecutionTask = new JobResultExecutionTask(aJobToBeRestarted);
                tmpJobResultExecutionTask.addPropertyChangeListener(this);
                this.jobResultExecutionTaskList.add(tmpJobResultExecutionTask);
                this.startRemainingJobExecutionTasks();
                this.updateStatusDisplay();
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Fill methods for job list models">

    /**
     * Fills existing job inputs list models with all jobs inputs of job input
     * path
     */
    private void fillJobInputRelatedListModels() {
        int tmpDesignJobInputCounter = 0;
        int tmpExecutionJobInputCounter = 0;
        if (JobManager.getInstance().getJobInputManager().hasJobInputsInInputPath()) {
            // <editor-fold defaultstate="collapsed" desc="Get all filtered jobs of job input path">
            JobInput[] tmpJobInputs = ModelUtils.getFilteredJobInputs(JobManager.getInstance().getJobInputManager().getSortedJobInputsOfJobInputPath());
            // <editor-fold defaultstate="collapsed" desc="Set filter related items">
            String tmpJobInputFilterDescription;
            if (tmpJobInputs != null) {
                tmpJobInputFilterDescription = ModelUtils.getJobInputFilterDescription(JobManager.getInstance().getJobInputManager().getNumberOfJobInputsOfJobInputPath() - tmpJobInputs.length);
            } else {
                tmpJobInputFilterDescription = ModelUtils.getJobInputFilterDescription(JobManager.getInstance().getJobInputManager().getNumberOfJobInputsOfJobInputPath());
            }
            this.mainFrame.getJobInputsSelectionPanel().getFilterPanel().getFilterInformationLabel().setText(tmpJobInputFilterDescription);
            this.mainFrame.getSelectExecutionJobInputSelectionPanel().getFilterPanel().getFilterInformationLabel().setText(tmpJobInputFilterDescription);
            Boolean tmpHasJobInputFilter = Preferences.getInstance().hasJobInputFilter();
            this.mainFrame.getJobInputsSelectionPanel().getFilterPanel().getClearButton().setVisible(tmpHasJobInputFilter);
            this.mainFrame.getSelectExecutionJobInputSelectionPanel().getFilterPanel().getClearButton().setVisible(tmpHasJobInputFilter);
            // </editor-fold>
            if (tmpJobInputs == null) {
                // <editor-fold defaultstate="collapsed" desc="Clear models">
                this.jobDesignInputListModel.clear();
                this.jobExecutionInputListModel.clear();

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Update display">
                this.updateJobDesignDisplay();
                this.updateJobExecutionDisplay();
                this.updateMenuDisplay();
                // </editor-fold>
                return;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Disable listeners">
            this.jobDesignInputListModel.setListenersEnabled(false);
            this.jobExecutionInputListModel.setListenersEnabled(false);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Clear and add job inputs">
            this.jobDesignInputListModel.clear();
            this.jobExecutionInputListModel.clear();
            // Job inputs are sorted ascending according to their timestamp: Set job inputs in reverse order (i.e. latest job will have index 0)
            for (int i = tmpJobInputs.length - 1; i >= 0; i--) {
                this.jobDesignInputListModel.addElement(tmpJobInputs[i]);
                tmpDesignJobInputCounter++;
                if (!tmpJobInputs[i].hasError() && ModelUtils.isVersionOneEqualOrHigher(tmpJobInputs[i].getMFsimApplicationVersion(), ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION)) {
                    this.jobExecutionInputListModel.addElement(tmpJobInputs[i]);
                    tmpExecutionJobInputCounter++;
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Enable listeners again">
            this.jobDesignInputListModel.setListenersEnabled(true);
            this.jobDesignInputListModel.fireIntervalAdded(this.jobDesignInputListModel, 0, tmpDesignJobInputCounter);
            this.jobExecutionInputListModel.setListenersEnabled(true);
            this.jobExecutionInputListModel.fireIntervalAdded(this.jobExecutionInputListModel, 0, tmpExecutionJobInputCounter);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Select first job in all lists">
            this.mainFrame.getJobInputsSelectionPanel().getList().setSelectedIndex(0);
            this.mainFrame.getSelectExecutionJobInputSelectionPanel().getList().setSelectedIndex(0);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update display">
            this.updateJobDesignDisplay();
            this.updateJobExecutionDisplay();
            this.updateMenuDisplay();

            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Clear models">
            this.jobDesignInputListModel.clear();
            this.jobExecutionInputListModel.clear();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update display">
            this.updateJobDesignDisplay();
            this.updateJobExecutionDisplay();
            this.updateMenuDisplay();

            // </editor-fold>
        }
    }

    /**
     * Fills job result and job restart list models with all jobs of result path
     */
    private void fillJobResultRelatedListModels() {
        int tmpCounter = 0;
        if (JobManager.getInstance().getJobResultManager().hasFinishedJobsInResultPath()) {
            // <editor-fold defaultstate="collapsed" desc="Get all filtered jobs of result path">
            JobResult[] tmpResultJobs = ModelUtils.getFilteredJobResults(JobManager.getInstance().getJobResultManager().getSortedJobsOfResultPath());
            // <editor-fold defaultstate="collapsed" desc="Set filter related items">
            String tmpJobResultFilterDescription;
            if (tmpResultJobs != null) {
                tmpJobResultFilterDescription = ModelUtils.getJobResultFilterDescription(JobManager.getInstance().getJobResultManager().getNumberOfJobResultsOfJobResultsPath()
                        - tmpResultJobs.length);
            } else {
                tmpJobResultFilterDescription = ModelUtils.getJobResultFilterDescription(JobManager.getInstance().getJobResultManager().getNumberOfJobResultsOfJobResultsPath());
            }
            this.mainFrame.getSelectJobResultPanel().getFilterPanel().getFilterInformationLabel().setText(tmpJobResultFilterDescription);
            this.mainFrame.getSelectJobForRestartSelectionPanel().getFilterPanel().getFilterInformationLabel().setText(tmpJobResultFilterDescription);
            Boolean tmpHasJobResultFilter = Preferences.getInstance().hasJobResultFilter();
            this.mainFrame.getSelectJobResultPanel().getFilterPanel().getClearButton().setVisible(tmpHasJobResultFilter);
            this.mainFrame.getSelectJobForRestartSelectionPanel().getFilterPanel().getClearButton().setVisible(tmpHasJobResultFilter);
            // </editor-fold>
            if (tmpResultJobs == null) {
                // <editor-fold defaultstate="collapsed" desc="Clear models">
                this.jobResultListModel.clear();
                this.jobRestartListModel.clear();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Update display">
                this.updateJobExecutionDisplay();
                this.updateJobResultsDisplay();
                this.updateMenuDisplay();
                // </editor-fold>
                return;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Job result list">
            // Disable listeners of model
            this.jobResultListModel.setListenersEnabled(false);
            // Clear model and add jobs
            this.jobResultListModel.clear();
            // Jobs are sorted ascending according to their timestampEnd:
            // Set jobs in reverse order (i.e. latest job will have index 0)
            for (int i = tmpResultJobs.length - 1; i >= 0; i--) {
                this.jobResultListModel.addElement(tmpResultJobs[i]);
                tmpCounter++;
            }
            // Enable listeners of model again
            this.jobResultListModel.setListenersEnabled(true);
            this.jobResultListModel.fireIntervalAdded(this.jobResultListModel, 0, tmpCounter);
            // Select first job in list
            this.mainFrame.getSelectJobResultPanel().getList().setSelectedIndex(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Job restart list">
            tmpResultJobs = ModelUtils.getVersionCheckedJobResults(tmpResultJobs);
            if (tmpResultJobs == null) {
                this.jobRestartListModel.clear();
            } else {
                // Disable listeners of model
                this.jobRestartListModel.setListenersEnabled(false);
                // Clear model and add jobs
                this.jobRestartListModel.clear();
                // Jobs are sorted ascending according to their timestampEnd:
                // Set jobs in reverse order (i.e. latest job will have index 0)
                for (int i = tmpResultJobs.length - 1; i >= 0; i--) {
                    this.jobRestartListModel.addElement(tmpResultJobs[i]);
                    tmpCounter++;
                }
                // Enable listeners of model again
                this.jobRestartListModel.setListenersEnabled(true);
                this.jobRestartListModel.fireIntervalAdded(this.jobRestartListModel, 0, tmpCounter);
                // Select first job in list
                this.mainFrame.getSelectJobForRestartSelectionPanel().getList().setSelectedIndex(0);
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Clear models">
            this.jobResultListModel.clear();
            this.jobRestartListModel.clear();
            // </editor-fold>
        }
        // <editor-fold defaultstate="collapsed" desc="Update display">
        this.updateJobExecutionDisplay();
        this.updateJobResultsDisplay();
        this.updateMenuDisplay();
        // </editor-fold>
    }

    /**
     * Fills job restart list model with all jobs of result path
     */
    private void fillJobRestartListModel() {
        int tmpCounter = 0;
        if (JobManager.getInstance().getJobResultManager().hasFinishedJobsInResultPath()) {
            // <editor-fold defaultstate="collapsed" desc="Get all filtered and version-checked jobs of result path">
            JobResult[] tmpResultJobs = ModelUtils.getFilteredJobResults(JobManager.getInstance().getJobResultManager().getSortedJobsOfResultPath());
            tmpResultJobs = ModelUtils.getVersionCheckedJobResults(tmpResultJobs);
            // <editor-fold defaultstate="collapsed" desc="Set filter related items">
            if (tmpResultJobs != null) {
                this.mainFrame.getSelectJobForRestartSelectionPanel().getFilterPanel().getFilterInformationLabel().setText(ModelUtils.getJobResultFilterDescription(JobManager.getInstance().getJobResultManager().getNumberOfJobResultsOfJobResultsPath() - tmpResultJobs.length));
            } else {
                this.mainFrame.getSelectJobForRestartSelectionPanel().getFilterPanel().getFilterInformationLabel().setText(ModelUtils.getJobResultFilterDescription(JobManager.getInstance().getJobResultManager().getNumberOfJobResultsOfJobResultsPath()));
            }
            this.mainFrame.getSelectJobForRestartSelectionPanel().getFilterPanel().getClearButton().setVisible(Preferences.getInstance().hasJobResultFilter());
            // </editor-fold>
            if (tmpResultJobs == null) {
                // <editor-fold defaultstate="collapsed" desc="Clear model">
                this.jobRestartListModel.clear();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Update display">
                this.updateJobExecutionDisplay();
                this.updateMenuDisplay();
                // </editor-fold>
                return;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Disable listeners of jobRestartListModel">
            this.jobRestartListModel.setListenersEnabled(false);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Clear jobRestartListModel and add jobs">
            this.jobRestartListModel.clear();
            // Jobs are sorted ascending according to their timestampEnd:
            // Set jobs in reverse order (i.e. latest job will have index 0)
            for (int i = tmpResultJobs.length - 1; i >= 0; i--) {
                this.jobRestartListModel.addElement(tmpResultJobs[i]);
                tmpCounter++;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Enable listeners of jobRestartListModel again">
            this.jobRestartListModel.setListenersEnabled(true);
            this.jobRestartListModel.fireIntervalAdded(this.jobRestartListModel, 0, tmpCounter);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Select first job in list">
            this.mainFrame.getSelectJobForRestartSelectionPanel().getList().setSelectedIndex(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update display">
            this.updateJobExecutionDisplay();
            this.updateMenuDisplay();
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Clear model">
            this.jobRestartListModel.clear();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update display">
            this.updateJobExecutionDisplay();
            this.updateMenuDisplay();
            // </editor-fold>
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job execution task related methods">
    /**
     * Starts remaining job execution tasks
     */
    private void startRemainingJobExecutionTasks() {
        for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
            int tmpNumberOfSubmittedJobResultExecutionTasks = this.getNumberOfSubmittedJobResultExecutionTasks();
            if (!tmpJobResultExecutionTask.isSubmittedToExecutorService() 
                && tmpNumberOfSubmittedJobResultExecutionTasks < Preferences.getInstance().getNumberOfParallelSimulations()
            ) {
                if (this.jobResultExecutorService == null) {
                    this.jobResultExecutorService = Executors.newFixedThreadPool(Preferences.getInstance().getNumberOfParallelSimulations());
                }
                tmpJobResultExecutionTask.setSubmittedToExecutorService();
                this.jobResultExecutorService.submit(tmpJobResultExecutionTask);
                // IMPORTANT: Delay start of next job execution task
                // NOTE: There may occur subtle errors if several job execution tasks start at the "same time"
                if (Preferences.getInstance().getDelayForJobStartInMilliseconds() > 0L) {
                    try {
                        Thread.sleep(Preferences.getInstance().getDelayForJobStartInMilliseconds());
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        continue;
                    }
                }
            }
        }
    }

    /**
     * Stops all working submitted jobs and shutdown this.jobResultExecutorService
     */
    private void stopAllSubmittedWorkingJobs() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            if (this.jobResultExecutionTaskList != null && this.jobResultExecutionTaskList.size() > 0) {
                for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
                    if (tmpJobResultExecutionTask.isSubmittedToExecutorService() 
                        && !tmpJobResultExecutionTask.isFinished()
                        && !tmpJobResultExecutionTask.getJobResult().isStopped()
                    ) {
                        tmpJobResultExecutionTask.stop();
                    }
                }
                // Wait for jobs to be stopped
                while (this.isSubmittedJobResultExecutionTaskWorking()) {
                    try {
                        Thread.sleep(Preferences.getInstance().getTimerIntervalInMilliseconds());
                    } catch (InterruptedException anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // This should never happen!
                        return;
                    }
                }
                // Remove finished jobs
                this.removeFinishedJobs();
                // Shutdown this.jobResultExecutorService
                this.shutdownJobResultExecutorService();
                // Update display
                this.updateJobExecutionQueueDisplay();
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Returns number of working job result execution tasks
     *
     * @return Number of working job result execution tasks
     */
    private int getNumberOfSubmittedWorkingJobResultExecutionTasks() {
        int tmpCounter = 0;
        if (this.jobResultExecutionTaskList != null && this.jobResultExecutionTaskList.size() > 0) {
            for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
                if (tmpJobResultExecutionTask.isSubmittedToExecutorService()
                    && !tmpJobResultExecutionTask.isFinished()
                ) {
                    tmpCounter++;
                }
            }
        }
        return tmpCounter;
    }

    /**
     * Returns number of submitted job result execution tasks
     *
     * @return Number of submitted job result execution tasks
     */
    private int getNumberOfSubmittedJobResultExecutionTasks() {
        int tmpCounter = 0;
        if (this.jobResultExecutionTaskList != null && this.jobResultExecutionTaskList.size() > 0) {
            for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
                if (tmpJobResultExecutionTask.isSubmittedToExecutorService()) {
                    tmpCounter++;
                }
            }
        }
        return tmpCounter;
    }

    /**
     * Returns if submitted job result execution tasks is working
     *
     * @return True: job result execution tasks is working, false: Otherwise
     */
    private boolean isSubmittedJobResultExecutionTaskWorking() {
        if (this.jobResultExecutionTaskList != null && this.jobResultExecutionTaskList.size() > 0) {
            for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
                if (tmpJobResultExecutionTask.isSubmittedToExecutorService() 
                    && !tmpJobResultExecutionTask.isFinished()
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Shutdown for this.jobResultExecutorService if no job is working
     */
    private void shutdownJobResultExecutorService() {
        if (!this.isSubmittedJobResultExecutionTaskWorking()) {
            try {
                if (this.jobResultExecutorService != null) {
                    this.jobResultExecutorService.shutdown();
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
            } finally {
                this.jobResultExecutorService = null;
            }
        }
    }
    
    /**
     * Returns number of non-stopped working job result execution tasks
     *
     * @return Number of non-stopped working job result execution tasks
     */
    private int getNumberOfNonStoppedJobResultExecutionTasks() {
        int tmpCounter = 0;
        if (this.jobResultExecutionTaskList != null && this.jobResultExecutionTaskList.size() > 0) {
            for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
                if (tmpJobResultExecutionTask.isSubmittedToExecutorService() 
                    && !tmpJobResultExecutionTask.getJobResult().isStopped()
                ) {
                    tmpCounter++;
                }
            }
        }
        return tmpCounter;
    }

    /**
     * Returns number of non-submitted job result execution tasks
     *
     * @return Number of non-submitted working job result execution tasks
     */
    private int getNumberOfNonSubmittedJobResultExecutionTasks() {
        int tmpCounter = 0;
        if (this.jobResultExecutionTaskList != null && this.jobResultExecutionTaskList.size() > 0) {
            for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
                if (!tmpJobResultExecutionTask.isSubmittedToExecutorService()) {
                    tmpCounter++;
                }
            }
        }
        return tmpCounter;
    }

    /**
     * Returns if specified submitted job result is working non-stopped
     *
     * @return True: Specified submitted job result is working non-stopped, 
     * false: Otherwise
     */
    private boolean isSubmittedJobResultWorkingNonStopped(JobResult aJobResult) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResult == null) {
            return false;
        }
        // </editor-fold>
        boolean tmpIsWorkingNonStopped = false;
        for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
            if (tmpJobResultExecutionTask.getJobResult().getJobResultId().equals(aJobResult.getJobResultId())) {
                if (tmpJobResultExecutionTask.isSubmittedToExecutorService() 
                    && !tmpJobResultExecutionTask.isFinished()
                    && !tmpJobResultExecutionTask.getJobResult().isStopped()
                ) {
                    tmpIsWorkingNonStopped = true;
                    break;
                }
            }
        }
        return tmpIsWorkingNonStopped;
    }

    /**
     * Returns if specified job result is finished
     *
     * @return True: Specified job result is finished, false:
     * Otherwise
     */
    private boolean isJobResultFinished(JobResult aJobResult) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResult == null) {
            return false;
        }
        // </editor-fold>
        boolean tmpIsFinished = true;
        for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
            if (tmpJobResultExecutionTask.getJobResult().getJobResultId().equals(aJobResult.getJobResultId())) {
                if (!tmpJobResultExecutionTask.isFinished()) {
                    tmpIsFinished = false;
                    break;
                }
            }
        }
        return tmpIsFinished;
    }

    /**
     * Stops job result execution task of specified job result
     */
    private void stopJobResult(JobResult aJobResult) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResult == null) {
            return;
        }
        // </editor-fold>
        for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
            if (tmpJobResultExecutionTask.getJobResult().getJobResultId().equals(aJobResult.getJobResultId())) {
                if (tmpJobResultExecutionTask.isSubmittedToExecutorService() 
                    && !tmpJobResultExecutionTask.isFinished() 
                    && !tmpJobResultExecutionTask.getJobResult().isStopped()
                ) {
                    tmpJobResultExecutionTask.stop();
                    break;
                }
            }
        }
    }

    /**
     * Removes successfully finished job from entities
     */
    private void removeFinishedJobs() {
        Iterator<JobResultExecutionTask> tmpIterator = this.jobResultExecutionTaskList.iterator();
        while (tmpIterator.hasNext()) {
            JobResultExecutionTask tmpJobResultExecutionTask = tmpIterator.next();
            if (tmpJobResultExecutionTask.isFinished()) {
                tmpIterator.remove();
                this.jobExecutionListModel.removeElement(tmpJobResultExecutionTask.getJobResult());
            }
        }
    }

    /**
     * Removes non-submitted job result from execution queue
     */
    private void removeSelectedNonSubmittedJobFromExecutionQueue() {
        int tmpSelectedPosition = this.mainFrame.getJobExecutionList().getSelectedIndex();
        if (tmpSelectedPosition >= 0) {
            JobResult tmpJobResult = (JobResult) this.jobExecutionListModel.get(tmpSelectedPosition);
            for (JobResultExecutionTask tmpJobResultExecutionTask : this.jobResultExecutionTaskList) {
                if (tmpJobResultExecutionTask.getJobResult().getJobResultId().equals(tmpJobResult.getJobResultId()) && 
                    !tmpJobResultExecutionTask.isSubmittedToExecutorService()
                ) {
                    this.jobResultExecutionTaskList.remove(tmpJobResultExecutionTask);
                    // <editor-fold defaultstate="collapsed" desc="Delete possible additional steps for restart">
                    // Set isRestarted
                    tmpJobResult.setToBeRestarted(false);
                    // Set nsteps for restart
                    tmpJobResult.setAdditionalStepsForRestart(0);
                    // Repaint: JobResult description changed since job is no longer "to be restarted"
                    this.mainFrame.getSelectJobForRestartSelectionPanel().getList().repaint();

                    // </editor-fold>
                    this.jobExecutionListModel.remove(tmpSelectedPosition);
                    if (tmpSelectedPosition == this.jobExecutionListModel.getSize()) {
                        this.mainFrame.getJobExecutionList().setSelectedIndex(tmpSelectedPosition - 1);
                    } else {
                        this.mainFrame.getJobExecutionList().setSelectedIndex(tmpSelectedPosition);
                    }
                    break;
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job archive background task related methods">
    /**
     * Removes successfully finished job archive background tasks
     */
    private void removeFinishedJobArchiveTasks() {
        Iterator<ArchiveTask> tmpIterator = this.jobArchiveTaskList.iterator();
        while (tmpIterator.hasNext()) {
            ArchiveTask tmpJobArchiveTask = tmpIterator.next();
            if (tmpJobArchiveTask.isFinished()) {
                tmpIterator.remove();
            }
        }
    }

    /**
     * Returns number of submitted job archive background tasks
     *
     * @return Number of submitted job archive background tasks
     */
    private int getNumberOfSubmittedJobArchiveTasks() {
        int tmpCounter = 0;
        if (this.jobArchiveTaskList != null && this.jobArchiveTaskList.size() > 0) {
            for (ArchiveTask tmpJobArchiveTask : this.jobArchiveTaskList) {
                if (tmpJobArchiveTask.isSubmittedToExecutorService()) {
                    tmpCounter++;
                }
            }
        }
        return tmpCounter;
    }

    /**
     * Returns if at least one job result is currently submitted for archiving 
     * in background.
     *
     * @return True: At least one job result is currently submitted for 
     * archiving in background, false: Otherwise
     */
    private boolean isJobArchivingTaskSubmitted() {
        if (this.jobArchiveTaskList != null && this.jobArchiveTaskList.size() > 0) {
            for (ArchiveTask tmpJobArchiveTask : this.jobArchiveTaskList) {
                if (tmpJobArchiveTask.isSubmittedToExecutorService()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Shutdown for this.jobArchiveExecutorService if no job is archived
     */
    private void shutdownJobArchiveExecutorService() {
        if (!this.isJobArchivingTaskSubmitted()) {
            try {
                if (this.jobArchiveExecutorService != null) {
                    this.jobArchiveExecutorService.shutdown();
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
            } finally {
                this.jobArchiveExecutorService = null;
            }
        }
    }
    // </editor-fold>
    // </editor-fold>

}
