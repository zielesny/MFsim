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
package de.gnwi.mfsim.model.job;

import de.gnwi.mfsim.model.message.ModelMessage;

/**
 * JobResult processing status
 * 
 * @author Achim Zielesny
 */
public enum JobResultProcessingStatusEnum {

	/**
	 * JobResult was stopped
	 */
	JOB_STOPPED,
	/**
	 * JobResult finished with failure
	 */
	JOB_FINISHED_WITH_FAILURE,
	/**
	 * JobResult finished with sucess
	 */
	JOB_FINISHED_WITH_SUCCESS,
	/**
	 * JobResult process is in simulation
	 */
	JOB_IN_SIMULATION,
	/**
	 * No job is in simulation
	 */
	NO_JOB_IN_SIMULATION,
	/**
	 * No status defined (should be default)
	 */
	NO_STATUS_DEFINED;

	/**
	 * Returns job processing status representation
	 * 
	 * @return JobResult processing status representation
	 */
	public String toRepresentation() {
		switch (this) {
		case JOB_STOPPED:
			return ModelMessage.get("JobResultProcessingStatusEnum.JobStopped");
		case JOB_FINISHED_WITH_FAILURE:
			return ModelMessage.get("JobResultProcessingStatusEnum.JobFinishedWithFailure");
		case JOB_FINISHED_WITH_SUCCESS:
			return ModelMessage.get("JobResultProcessingStatusEnum.JobFinishedWithSuccess");
		case JOB_IN_SIMULATION:
			return ModelMessage.get("JobResultProcessingStatusEnum.JobInSimulation");
		case NO_JOB_IN_SIMULATION:
			return ModelMessage.get("JobResultProcessingStatusEnum.NoJobInSimulation");
		case NO_STATUS_DEFINED:
			return ModelMessage.get("JobResultProcessingStatusEnum.NoStatusDefined");
		default:
			return ModelMessage.get("JobResultProcessingStatusEnum.UnknownFeature");
		}
	}

}
