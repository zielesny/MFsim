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
package de.gnwi.mfsim.model.job;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

import de.gnwi.mfsim.model.job.JobResult;
import de.gnwi.mfsim.model.util.FileDeletionTask;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Job result manager
 *
 * @author Achim Zielesny
 */
public class JobResultManager {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Cache for all jobs of result path
     */
    private LinkedList<JobResult> allJobResultsOfResultPathList;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public JobResultManager() {
        this.initialize();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public synchronized methods">
    /**
     * Cleans job result path, i.e. removes all directories with REMOVED prefix
     */
    public synchronized void cleanJobResultPath() {
        this.fileUtilityMethods.deleteAllDirectoriesWithPrefix(Preferences.getInstance().getJobResultPath(), ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES);
    }

    /**
     * Updates job results
     */
    public synchronized void updateJobResults() {
        this.updateAllJobsOfResultPathList();
    }

    /**
     * Removes specified job in result path
     *
     * @param aJobToBeRemoved JobResult to be removed
     * @return true: JobResult was removed successfully, false: Otherwise
     */
    public synchronized boolean removeJobInResultPath(JobResult aJobToBeRemoved) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobToBeRemoved == null || !(new File(aJobToBeRemoved.getJobResultPath())).isDirectory()) {
            return false;
        }

        // </editor-fold>
        if (!this.fileUtilityMethods.renameDirectory(aJobToBeRemoved.getJobResultPath(), ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES + (new File(aJobToBeRemoved.getJobResultPath())).getName())) {
            return false;
        }
        // Update job results
        this.updateAllJobsOfResultPathList();
        return true;
    }

    /**
     * Moves specified job from result path to specified destination directory
     *
     * @param aJobToBeMoved JobResult to be removed
     * @param aDirectoryDestinationPath Path of destination directory
     * @return true: JobResult was removed successfully, false: Otherwise
     */
    public synchronized boolean moveJobFromResultPathToDestinationDirectory(JobResult aJobToBeMoved, String aDirectoryDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobToBeMoved == null || !(new File(aJobToBeMoved.getJobResultPath())).isDirectory()) {
            return false;
        }
        if (aDirectoryDestinationPath == null || aDirectoryDestinationPath.isEmpty()) {
            return false;
        }
        if (!(new File(aDirectoryDestinationPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        if (!this.fileUtilityMethods.moveToDirectory(aJobToBeMoved.getJobResultPath(), aDirectoryDestinationPath)) {
            return false;
        }
        // Update job results
        this.updateAllJobsOfResultPathList();
        return true;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Cleans job result path in background, i.e. removes all directories with
     * REMOVED prefix
     */
    public void cleanJobResultPathInBackground() {
        String[] tmpRemovedJobResultPathArray = 
            this.fileUtilityMethods.getDirectoryPathsWithPrefix(Preferences.getInstance().getJobResultPath(), 
                ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES
            );
        if (tmpRemovedJobResultPathArray != null) {
            File[] tmpRemovedJobResultFileArray = new File[tmpRemovedJobResultPathArray.length];
            for (int i = 0; i < tmpRemovedJobResultPathArray.length; i++) {
                tmpRemovedJobResultFileArray[i] = new File(tmpRemovedJobResultPathArray[i]);
            }
            // NOTE: Clear-operation may take seconds up to minutes, so it is performed with separated thread in background
            new FileDeletionTask(tmpRemovedJobResultFileArray).start();
        }
    }

    /**
     * Returns number of job results in job result path
     *
     * @return Number of job results in job result path
     */
    public int getNumberOfJobResultsOfJobResultsPath() {
        return this.allJobResultsOfResultPathList.size();
    }

    /**
     * Returns all jobs of result path sorted ascending
     *
     * @return All jobs of result path sorted ascending or null if none exist
     */
    public JobResult[] getSortedJobsOfResultPath() {
        LinkedList<JobResult> tmpJobList = this.getJobListOfResultPath();
        if (tmpJobList.size() > 0) {
            JobResult[] jobArray = tmpJobList.toArray(new JobResult[0]);
            Arrays.sort(jobArray);
            return jobArray;
        } else {
            return null;
        }
    }

    /**
     * Returns job list with all jobs of result path
     *
     * @return JobResult list with all jobs of result path
     */
    public LinkedList<JobResult> getJobListOfResultPath() {
        return this.allJobResultsOfResultPathList;
    }

    /**
     * Returns if finished jobs in result path exist
     *
     * @return true: Finished jobs do exist in result path, false: Otherwise
     */
    public boolean hasFinishedJobsInResultPath() {
        return this.allJobResultsOfResultPathList.size() > 0;
    }

    /**
     * Returns if removed job results exists
     *
     * @return True: At least one removed job result exists, false: Otherwise
     */
    public boolean hasRemovedJobResult() {
        return ModelUtils.hasDirectoryWithPrefix(Preferences.getInstance().getJobResultPath(), ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="Initialisation methods">
    /**
     * Resets JobResultManager to initial state
     */
    private void initialize() {
        this.allJobResultsOfResultPathList = new LinkedList<JobResult>();
        this.updateAllJobsOfResultPathList();
        // Clean possible removed jobs results in background
        this.cleanJobResultPathInBackground();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Miscellaneous methods">
    /**
     * Updates cache for all jobs of result path (this.allJobsOfResultPathList)
     */
    private void updateAllJobsOfResultPathList() {
        this.allJobResultsOfResultPathList.clear();
        String[] tmpJobPaths = this.fileUtilityMethods.getDirectoryPathsWithPrefix(Preferences.getInstance().getJobResultPath(), ModelDefinitions.PREFIX_OF_JOB_RESULT_DIRECTORY);
        if (tmpJobPaths != null) {
            for (String tmpJobPath : tmpJobPaths) {
                JobResult tmpSingleJob;
                try {
                    tmpSingleJob = new JobResult(tmpJobPath);
                } catch (Exception anException) {
                    // Note: Exception may be thrown since a job result may be
                    // still in copying phase from temporary directory to
                    // JobResults directory thus
                    // ModelUtils.appendToLogfile(true, anException);
                    // should not be thrown
                    tmpSingleJob = null;
                }
                if (tmpSingleJob != null) {
                    this.allJobResultsOfResultPathList.addLast(tmpSingleJob);
                }
            }
        }
    }
    // </editor-fold>
    // </editor-fold>

}
