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
package de.gnwi.mfsim.model.valueItem;

import de.gnwi.mfsim.model.message.ModelMessage;
import java.util.HashMap;

/**
 * Defines status of value item
 *
 * @author Achim Zielesny
 */
public enum ValueItemEnumStatus {

    // <editor-fold defaultstate="collapsed" desc="Definitions">
    /**
     * Not defined
     */
    ALL,
    /**
     * Active
     */
    ACTIVE,
    /**
     * Inactive
     */
    INACTIVE,
    /**
     * Locked
     */
    LOCKED,
    /**
     * Jdpd input
     */
    JDPD_INPUT,
    /**
     * Unlocked
     */
    UNLOCKED,
    /**
     * Has error
     */
    HAS_ERROR,
    /**
     * Has no error
     */
    HAS_NO_ERROR,
    /**
     * Has hint
     */
    HAS_HINT,
    /**
     * Has no hint
     */
    HAS_NO_HINT,
    /**
     * Is selected
     */
    SELECTED,
    /**
     * Is deselected
     */
    DESELECTED,
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
    private static HashMap<String, ValueItemEnumStatus> representationToValueItemEnumStatusMap;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="toRepresentation()">
    /**
     * Returns ValueItemEnumStatus representation
     *
     * @return ValueItemEnumStatus representation
     */
    public String toRepresentation() {
        switch (this) {
            case ALL:
                return ModelMessage.get("ValueItemEnumStatus.All");
            case ACTIVE:
                return ModelMessage.get("ValueItemEnumStatus.Active");
            case INACTIVE:
                return ModelMessage.get("ValueItemEnumStatus.Inactive");
            case LOCKED:
                return ModelMessage.get("ValueItemEnumStatus.Locked");
            case JDPD_INPUT:
                return ModelMessage.get("ValueItemEnumStatus.JdpdInput");
            case UNLOCKED:
                return ModelMessage.get("ValueItemEnumStatus.Unlocked");
            case HAS_ERROR:
                return ModelMessage.get("ValueItemEnumStatus.HasError");
            case HAS_NO_ERROR:
                return ModelMessage.get("ValueItemEnumStatus.HasNoError");
            case HAS_HINT:
                return ModelMessage.get("ValueItemEnumStatus.HasHint");
            case HAS_NO_HINT:
                return ModelMessage.get("ValueItemEnumStatus.HasNoHint");
            case SELECTED:
                return ModelMessage.get("ValueItemEnumStatus.Selected");
            case DESELECTED:
                return ModelMessage.get("ValueItemEnumStatus.Deselected");
            case UNDEFINED:
                return ModelMessage.get("ValueItemEnumStatus.Undefined");
            default:
                return ModelMessage.get("ValueItemEnumStatus.Undefined");
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="toValueItemStatus()">
    /**
     * Returns ValueItemEnumStatus of representation
     *
     * @param aRepresentation Value item status representation
     * @return ValueItemEnumStatus
     */
    public static ValueItemEnumStatus toValueItemStatus(String aRepresentation) {

        // <editor-fold defaultstate="collapsed" desc="Initialize if necessary">

        if (ValueItemEnumStatus.representationToValueItemEnumStatusMap == null) {
            ValueItemEnumStatus.representationToValueItemEnumStatusMap = new HashMap<String, ValueItemEnumStatus>(12);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.All"), ValueItemEnumStatus.ALL);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.Active"), ValueItemEnumStatus.ACTIVE);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.Inactive"),
                    ValueItemEnumStatus.INACTIVE);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.Locked"), ValueItemEnumStatus.LOCKED);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.JdpdInput"), ValueItemEnumStatus.JDPD_INPUT);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.Unlocked"),
                    ValueItemEnumStatus.UNLOCKED);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.HasError"),
                    ValueItemEnumStatus.HAS_ERROR);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.HasNoError"),
                    ValueItemEnumStatus.HAS_NO_ERROR);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.HasHint"),
                    ValueItemEnumStatus.HAS_HINT);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.HasNoHint"),
                    ValueItemEnumStatus.HAS_NO_HINT);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.Selected"),
                    ValueItemEnumStatus.SELECTED);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.Deselected"),
                    ValueItemEnumStatus.DESELECTED);
            ValueItemEnumStatus.representationToValueItemEnumStatusMap.put(ModelMessage.get("ValueItemEnumStatus.Undefined"),
                    ValueItemEnumStatus.UNDEFINED);
        }

        // </editor-fold>

        if (ValueItemEnumStatus.representationToValueItemEnumStatusMap.containsKey(aRepresentation)) {
            return ValueItemEnumStatus.representationToValueItemEnumStatusMap.get(aRepresentation);
        } else {
            return ValueItemEnumStatus.UNDEFINED;
        }
    }
    // </editor-fold>
}
