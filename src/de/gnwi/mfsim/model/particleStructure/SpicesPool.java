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
package de.gnwi.mfsim.model.particleStructure;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pool for graphical Spices
 *
 * @author Achim Zielesny
 */
public class SpicesPool {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * SpicesPool instance
     */
    private static final SpicesPool spicesPool = new SpicesPool();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Molecular structure string to Spices map
     */
    private ConcurrentHashMap<String, SpicesGraphics> spicesMap;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private singleton constructor">
    /**
     * Singleton constructor
     */
    private SpicesPool() {
        // NOTE: 100 Spices instances are sufficient for all practical purposes
        this.spicesMap = new ConcurrentHashMap<>(100);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public singleton instance method">
    /**
     * Singleton initialisation and instance method
     *
     * @return SpicesPool instance
     */
    public static SpicesPool getInstance() {
        return SpicesPool.spicesPool;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns Spices instance
     *
     * @param aMolecularStructureString Molecular structure string
     * @return Spices instance or null if molecular structure string is invalid
     */
    public synchronized SpicesGraphics getSpices(String aMolecularStructureString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMolecularStructureString == null || aMolecularStructureString.isEmpty()) {
            // Utility.appendToLogfile(true, "SpicesPool.getSpices: Null");
            return null;
        }
        // </editor-fold>
        try {
            SpicesGraphics tmpSpices = this.spicesMap.remove(aMolecularStructureString);
            if (tmpSpices == null) {
                tmpSpices = new SpicesGraphics(aMolecularStructureString);
                if (tmpSpices.isValid()) {
                    // Utility.appendToLogfile(true, "SpicesPool.getSpices - New: "
                    //         + aMolecularStructureString.substring(0, aMolecularStructureString.length() > 20 ? 20 : aMolecularStructureString.length()));
                    return tmpSpices;
                } else {
                    // Utility.appendToLogfile(true, "SpicesPool.getSpices: Null");
                    return null;
                }
            } else {
                // Utility.appendToLogfile(true, "SpicesPool.getSpices - Pooled: "
                //        + aMolecularStructureString.substring(0, aMolecularStructureString.length() > 20 ? 20 : aMolecularStructureString.length()));
                return tmpSpices;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // Utility.appendToLogfile(true, "SpicesPool.getSpices: Null");
            return null;
        }
    }

    /**
     * Set Spices instance for reuse
     *
     * @param aSpices Spices instance to be reused
     */
    public void setSpicesForReuse(SpicesGraphics aSpices) {
        if (aSpices != null) {
            // Utility.appendToLogfile(true, "SpicesPool.setSpicesForReuse: "
            //         + aSpices.getInputStructure().substring(0, aSpices.getInputStructure().length() > 20 ? 20 : aSpices.getInputStructure().length()));
            this.spicesMap.putIfAbsent(aSpices.getInputStructure(), aSpices);
            // Utility.appendToLogfile(true, "SpicesPool Size = " + String.valueOf(this.spicesMap.size()));
        }
    }

    /**
     * Returns number of pooled Spices instances
     *
     * @return Number of pooled Spices instances
     */
    public int getPoolSize() {
        return this.spicesMap.size();
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Clears pool
     */
    public void clear() {
        // Utility.appendToLogfile(true, "SpicesPool.clear");
        this.spicesMap.clear();
    }
    // </editor-fold>
    
}
