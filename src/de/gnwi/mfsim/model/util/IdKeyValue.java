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
package de.gnwi.mfsim.model.util;

import de.gnwi.mfsim.model.preference.ModelDefinitions;
import java.util.LinkedList;

/**
 * Utility class for an ID that is associated with multiple key-value pairs
 * 
 * @author Achim Zielesny
 */
public class IdKeyValue {
    
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * ID string
     */
    private final String id;

    /**
     * ID parts
     */
    private final String[] idParts;
    
    /**
     * Hash map that maps double key to double value
     */
    private final LinkedList<Double> keyValueList;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param anId ID string
     */
    public IdKeyValue(String anId) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anId == null || anId.isEmpty()) {
            throw new IllegalArgumentException("anID is invalid.");
        }
        // </editor-fold>
        this.id = anId;
        this.idParts = new String[] {anId};
        this.keyValueList = new LinkedList<>();
    }
    
    /**
     * Constructor
     * 
     * @param anIdParts ID string
     */
    public IdKeyValue(String[] anIdParts) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIdParts == null || anIdParts.length == 0) {
            throw new IllegalArgumentException("idParts is invalid.");
        }
        for (String tmpIdPart : anIdParts) {
            if (tmpIdPart == null || tmpIdPart.isEmpty()) {
                throw new IllegalArgumentException("idParts is invalid.");
            }
        }
        // </editor-fold>
        String tmpId = anIdParts[0];
        for (int i = 1; i < anIdParts.length; i++) {
            tmpId += ModelDefinitions.GENERAL_SEPARATOR;
            tmpId += anIdParts[i];
        }
        this.id = tmpId;
        this.idParts = anIdParts;
        this.keyValueList = new LinkedList<>();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Add a key-value pair
     * 
     * @param aKey Key
     * @param aValue Value
     */
    public void add(double aKey, double aValue) {
        this.keyValueList.add(aKey);
        this.keyValueList.add(aValue);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Property (get)">
    /**
     * ID string
     * 
     * @return ID string
     */
    public String getId() {
        return this.id;
    }

    /**
     * ID parts
     * 
     * @return ID parts
     */
    public String[] getIdParts() {
        return this.idParts;
    }
    
    /**
     * Linked list with double key and double value
     * 
     * @return Linked list with double key and double value
     */
    public LinkedList<Double> getKeyValueList() {
        return this.keyValueList;
    }
    // </editor-fold>
    
}
