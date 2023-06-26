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
package de.gnwi.mfsim.model.graphics;

import java.util.HashMap;

import de.gnwi.mfsim.model.message.ModelMessage;

/**
 * Simulation box views
 * 
 * @author Achim Zielesny
 */
public enum SimulationBoxViewEnum {

    // <editor-fold defaultstate="collapsed" desc="Definitions">
    /**
     * Front view
     */
    XZ_FRONT,
    /**
     * Back view
     */
    XZ_BACK,
    /**
     * Left view
     */
    YZ_LEFT,
    /**
     * Right view
     */
    YZ_RIGHT,
    /**
     * Top view
     */
    XY_TOP,
    /**
     * Bottom view
     */
    XY_BOTTOM,
    /**
     * Undefined
     */
    UNDEFINED;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Map of representation to enum value
     */
    private static HashMap<String, SimulationBoxViewEnum> representationToSimulationBoxViewEnumMap;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="toRepresentation()">
    /**
     * Returns string representation of enum value
     * 
     * @return String representation of enum value
     */
    public String toRepresentation() {
        switch (this) {
        case XZ_FRONT:
            return ModelMessage.get("BoxViewXZFront");
        case XZ_BACK:
            return ModelMessage.get("BoxViewXZBack");
        case YZ_LEFT:
            return ModelMessage.get("BoxViewYZLeft");
        case YZ_RIGHT:
            return ModelMessage.get("BoxViewYZRight");
        case XY_TOP:
            return ModelMessage.get("BoxViewXYTop");
        case XY_BOTTOM:
            return ModelMessage.get("BoxViewXYBottom");
        default:
            return ModelMessage.get("BoxViewUnknown");
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="toSimulationBoxViewEnum()">
    /**
     * Returns SimulationBoxViewEnum for name
     * 
     * @param aRepresentation Representation from method toRepresentation()
     * @return SimulationBoxViewEnum for representation or 
     * SimulationBoxViewEnum.UNDEFINED if representation is not known
     */
    public static SimulationBoxViewEnum toSimulationBoxViewEnum(String aRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Initialize if necessary">
        if (SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap == null) {
            SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap = new HashMap<String, SimulationBoxViewEnum>(6);
            SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.put(
                SimulationBoxViewEnum.XZ_FRONT.toRepresentation(),
                SimulationBoxViewEnum.XZ_FRONT
            );
            SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.put(
                SimulationBoxViewEnum.XZ_BACK.toRepresentation(),
                SimulationBoxViewEnum.XZ_BACK
            );
            SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.put(
                SimulationBoxViewEnum.YZ_LEFT.toRepresentation(),
                SimulationBoxViewEnum.YZ_LEFT
            );
            SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.put(
                SimulationBoxViewEnum.YZ_RIGHT.toRepresentation(),
                SimulationBoxViewEnum.YZ_RIGHT
            );
            SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.put(
                SimulationBoxViewEnum.XY_TOP.toRepresentation(),
                SimulationBoxViewEnum.XY_TOP
            );
            SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.put(
                SimulationBoxViewEnum.XY_BOTTOM.toRepresentation(),
                SimulationBoxViewEnum.XY_BOTTOM
            );
        }
        // </editor-fold>
        if (SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.containsKey(aRepresentation)) {
            return SimulationBoxViewEnum.representationToSimulationBoxViewEnumMap.get(aRepresentation);
        } else {
            return SimulationBoxViewEnum.UNDEFINED;
        }
    }
    // </editor-fold>

}
