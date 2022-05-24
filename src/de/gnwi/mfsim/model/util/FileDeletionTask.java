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
package de.gnwi.mfsim.model.util;

import java.io.File;
import javax.swing.SwingWorker;

/**
 * Task for file and directory deletion
 *
 * @author Achim Zielesny
 *
 */
public class FileDeletionTask extends SwingWorker<Boolean, Integer> {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Task was started, false: Otherwise
     */
    private boolean isStarted;

    /**
     * True: Task finished, false: Otherwise
     */
    private boolean isFinished;

    /**
     * Files and directories for deletion
     */
    private File[] filesAndDirectoriesForDeletion;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates a new instance
     *
     * @param aFilesAndDirectoriesForDeletion Array of files and directories for
     * deletion
     */
    public FileDeletionTask(File[] aFilesAndDirectoriesForDeletion) {
        this.isStarted = false;
        this.isFinished = false;
        this.filesAndDirectoriesForDeletion = aFilesAndDirectoriesForDeletion;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- isFinished">
    /**
     * True: Task has successfully finished operations, false: Otherwise
     *
     * @return True: Task has successfully finished operations, false:
     * Otherwise
     */
    public boolean isFinished() {
        return this.isStarted && this.isFinished;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- isWorking">
    /**
     * True: Task is executing jobs, false: Otherwise
     *
     * @return True: Task is executing jobs, false: Otherwise
     */
    public boolean isWorking() {
        return this.isStarted && !this.isFinished;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- start">
    /**
     * This method starts the task
     */
    public void start() {
        this.isStarted = true;
        this.execute();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- stop">
    /**
     * Stops execution of task
     */
    public void stop() {
        this.cancel(true);
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Protected (override) methods">
    // <editor-fold defaultstate="collapsed" desc="- doInBackground">
    /**
     * This method will be called when the task is executed. The method
     * performs files and directory deletions.
     *
     * @return True if operation was successful, otherwise false.
     */
    @Override
    protected Boolean doInBackground() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set variables">
            FileUtilityMethods tmpUtilityFileMethods = new FileUtilityMethods();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task starts. Set progress in percent to 0.">
            this.setProgress(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Perform operations">
            Boolean tmpIsOperationSuccessful = true;
            if (this.filesAndDirectoriesForDeletion != null && this.filesAndDirectoriesForDeletion.length > 0) {
                tmpIsOperationSuccessful = tmpUtilityFileMethods.deleteDefinedFilesAndDirectories(this.filesAndDirectoriesForDeletion);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this.isFinished to true BEFORE setting final progress in percent to 100">
            this.isFinished = true;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task has successfully finished. IMPORTANT: Set progress in percent to 100.">
            this.setProgress(100);
            // </editor-fold>
            return tmpIsOperationSuccessful;
        } finally {
            this.releaseMemory();
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Release memory
     */
    private void releaseMemory() {
        this.filesAndDirectoriesForDeletion = null;
    }
    // </editor-fold>
    
}
