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
import java.util.TreeMap;

/**
 * Utility class for an ID that accumulates values for a key
 * 
 * @author Achim Zielesny
 */
public class IdKeyValueAccumulator {
    
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
    private final TreeMap<Double, LinkedList<Double>> keyToValuesMap;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param anId ID string
     */
    public IdKeyValueAccumulator(String anId) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anId == null || anId.isEmpty()) {
            throw new IllegalArgumentException("anID is invalid.");
        }
        // </editor-fold>
        this.id = anId;
        this.idParts = new String[] {anId};
        this.keyToValuesMap = new TreeMap<>();
    }
    
    /**
     * Constructor
     * 
     * @param anIdParts ID string
     */
    public IdKeyValueAccumulator(String[] anIdParts) {
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
        this.keyToValuesMap = new TreeMap<>();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Add a value for key
     * 
     * @param aKey Key
     * @param aValue Value
     */
    public void add(double aKey, double aValue) {
        if (this.keyToValuesMap.containsKey(aKey)) {
            LinkedList<Double> tmpList = this.keyToValuesMap.get(aKey);
            tmpList.add(aValue);
        } else {
            LinkedList<Double> tmpList = new LinkedList<>();
            tmpList.add(aValue);
            this.keyToValuesMap.put(aKey, tmpList);
        }
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
     * TreeMap with double keys and linked lists with accumulated double values
     * 
     * @return TreeMap with double keys and linked lists with accumulated double values
     */
    public TreeMap<Double, LinkedList<Double>> getKeyToValuesMap() {
        return this.keyToValuesMap;
    }
    // </editor-fold>
    
}
