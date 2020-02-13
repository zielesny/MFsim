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
package de.gnwi.mfsim.model.job;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

import de.gnwi.mfsim.model.job.JobInput;
import de.gnwi.mfsim.model.util.FileDeletionTask;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Manager for job inputs
 *
 * @author Achim Zielesny
 */
public class JobInputManager {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * List with all job inputs of job input path
     */
    private LinkedList<JobInput> allJobInputsOfJobInputPathList;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public JobInputManager() {
        this.initialize();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public synchronized methods">
    /**
     * Cleans job input path, i.e. removes all directories with REMOVED prefix
     */
    public synchronized void cleanJobInputPath() {
        this.fileUtilityMethods.deleteAllDirectoriesWithPrefix(Preferences.getInstance().getJobInputPath(), ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES);
    }

    /**
     * Cleans job input path in background, i.e. removes all directories with
     * REMOVED prefix
     */
    public synchronized void cleanJobInputPathInBackground() {
        String[] tmpRemovedJobInputPathArray = this.fileUtilityMethods.getDirectoryPathsWithPrefix(Preferences.getInstance().getJobInputPath(), ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES);
        if (tmpRemovedJobInputPathArray != null) {
            File[] tmpRemovedJobInputFileArray = new File[tmpRemovedJobInputPathArray.length];
            for (int i = 0; i < tmpRemovedJobInputPathArray.length; i++) {
                tmpRemovedJobInputFileArray[i] = new File(tmpRemovedJobInputPathArray[i]);
            }
            // NOTE: Clear-operation may take seconds up to minutes, so it is performed with separated thread in background
            new FileDeletionTask(tmpRemovedJobInputFileArray).start();
        }
    }

    /**
     * Update job inputs
     */
    public synchronized void updateJobInputs() {
        this.updateAllJobInputsOfJobInputPathList();
    }

    /**
     * Removes specified job input in job input path
     *
     * @param aJobInputToBeRemoved JobInput to be removed
     * @return true: JobInput was removed successfully, false: Otherwise
     */
    public synchronized boolean removeJobInputInJobInputPath(JobInput aJobInputToBeRemoved) {
        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aJobInputToBeRemoved == null || !(new File(aJobInputToBeRemoved.getJobInputPath())).isDirectory()) {
            return false;
        }

        // </editor-fold>
        if (!this.fileUtilityMethods.renameDirectory(aJobInputToBeRemoved.getJobInputPath(), 
                ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES + (new File(aJobInputToBeRemoved.getJobInputPath())).getName()
            )
        ) {
            return false;
        }
        // Update
        this.updateAllJobInputsOfJobInputPathList();
        return true;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns number of job inputs in job input path
     *
     * @return Number of job inputs in job input path
     */
    public int getNumberOfJobInputsOfJobInputPath() {
        return this.allJobInputsOfJobInputPathList.size();
    }

    /**
     * Returns all jobs inputs of job input path sorted ascending
     *
     * @return All jobs inputs of job input path sorted ascending or null if
     * none exist
     */
    public JobInput[] getSortedJobInputsOfJobInputPath() {
        LinkedList<JobInput> tmpJobInputList = this.getJobInputListOfInputPath();
        if (tmpJobInputList.size() > 0) {
            JobInput[] jobInputArray = tmpJobInputList.toArray(new JobInput[0]);
            Arrays.sort(jobInputArray);
            return jobInputArray;
        } else {
            return null;
        }
    }

    /**
     * Returns job input list with all job inputs of job input path
     *
     * @return Job input list with all job inputs of job input path
     */
    public LinkedList<JobInput> getJobInputListOfInputPath() {
        return this.allJobInputsOfJobInputPathList;
    }

    /**
     * Returns whether correct job inputs exists in job input path
     *
     * @return True: Correct job inputs exists in job input path, false:
     * Otherwise
     */
    public boolean hasCorrectJobInputsInInputPath() {
        for (JobInput tmpSingleJobInput : this.allJobInputsOfJobInputPathList) {
            if (!tmpSingleJobInput.hasError()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether job inputs exists in job input path
     *
     * @return True: Job inputs exists in job input path, false: Otherwise
     */
    public boolean hasJobInputsInInputPath() {
        return this.allJobInputsOfJobInputPathList.size() > 0;
    }

    /**
     * Returns if removed job inputs exists
     *
     * @return True: At least one removed job input exists, false: Otherwise
     */
    public boolean hasRemovedJobInput() {
        return ModelUtils.hasDirectoryWithPrefix(Preferences.getInstance().getJobInputPath(), ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialize this instance
     */
    private void initialize() {
        this.allJobInputsOfJobInputPathList = new LinkedList<JobInput>();
        this.updateAllJobInputsOfJobInputPathList();
        // Clean possible removed jobs inputs in background
        this.cleanJobInputPathInBackground();
    }

    /**
     * Updates this.allJobInputsOfJobInputPathList
     */
    private void updateAllJobInputsOfJobInputPathList() {
        this.allJobInputsOfJobInputPathList.clear();
        String[] jobInputPaths = this.fileUtilityMethods.getDirectoryPathsWithPrefix(Preferences.getInstance().getJobInputPath(), ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY);
        if (jobInputPaths != null) {
            for (String jobInputPath : jobInputPaths) {
                JobInput singleJobInput;
                try {
                    // Create new JobInput in overwrite mode (Parameter true)
                    singleJobInput = new JobInput(jobInputPath, true);
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    singleJobInput = null;
                }
                if (singleJobInput != null) {
                    this.allJobInputsOfJobInputPathList.addLast(singleJobInput);
                }
            }
        }
    }
    // </editor-fold>
}
