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

import de.gnwi.mfsim.model.job.JobResultManager;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import javax.swing.JOptionPane;

/**
 * Singleton class for job management
 *
 * @author Achim Zielesny
 */
public final class JobManager {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Engine communication layer instance
     */
    private static JobManager jobManager = new JobManager();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * JobResultManager
     */
    private JobResultManager jobResultManager = null;
    /**
     * JobInputManager
     */
    private JobInputManager jobInputManager = null;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private singleton constructor">
    /**
     * Singleton constructor
     */
    private JobManager() {
        // Initialize
        if (!this.initializeEngineLayer()) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(null, ModelMessage.get("Error.NoEngineLayerInitialisation"), ModelMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public singleton instance method">
    /**
     * Singleton initialisation and instance method
     *
     * @return JobManager instance
     */
    public static JobManager getInstance() {
        return JobManager.jobManager;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Closes Jdpd layer. NOTE: This does NOT remove workspace directory
     * architecture.
     */
    public void closeEngineLayer() {
        // Clean up
        this.jobResultManager = null;
        this.jobInputManager = null;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    // <editor-fold defaultstate="collapsed" desc="JobResultManager">
    /**
     * Returns JobResultManager
     *
     * @return JobResultManager
     */
    public JobResultManager getJobResultManager() {
        return this.jobResultManager;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="JobInputManager">
    /**
     * Returns Job input manager
     *
     * @return Job input manager
     */
    public JobInputManager getJobInputManager() {
        return this.jobInputManager;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initializes Jdpd layer
     *
     * @return true: Operation was successful, false: Operation failed
     */
    private boolean initializeEngineLayer() {
        try {
            // Create job result manager
            this.jobResultManager = new JobResultManager();
            // Create job input manager
            this.jobInputManager = new JobInputManager();
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>

}
