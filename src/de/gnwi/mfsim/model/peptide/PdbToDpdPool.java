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
package de.gnwi.mfsim.model.peptide;

import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PdbToDpd pool
 *
 * @author Achim Zielesny
 */
public class PdbToDpdPool {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * PdbToDpdPool instance
     */
    private static final PdbToDpdPool pdbToDPDPool = new PdbToDpdPool();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Protein data to PdbToDpd map
     */
    private ConcurrentHashMap<String, PdbToDpd> pdbToDPDMap;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private singleton constructor">
    /**
     * Singleton constructor
     */
    private PdbToDpdPool() {
        // NOTE: 100 PdbToDpd instances are sufficient for all practical purposes
        this.pdbToDPDMap = new ConcurrentHashMap<>(100);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public singleton instance method">
    /**
     * Singleton initialisation and instance method
     *
     * @return PdbToDpdPool instance
     */
    public static PdbToDpdPool getInstance() {
        return PdbToDpdPool.pdbToDPDPool;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns PdbToDpd instance
     *
     * @param aProteinData Protein data
     * @return PdbToDpd instance or null if protein data are invalid
     */
    public synchronized PdbToDpd getPdbToDpd(String aProteinData) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aProteinData == null || aProteinData.isEmpty()) {
            return null;
        }
        // </editor-fold>
        try {
            PdbToDpd tmpPdbToDPD = this.pdbToDPDMap.remove(aProteinData);
            if (tmpPdbToDPD == null) {
                tmpPdbToDPD = new PdbToDpd(aProteinData);
            }
            return tmpPdbToDPD;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Set PdbToDpd instance for reuse
     *
     * @param aPdbToDPD PdbToDpd instance to be reused
     * @param aProteinData Protein data of aPdbToDPD
     */
    public void setPdbToDpdForReuse(PdbToDpd aPdbToDPD, String aProteinData) {
        try{
            if (aPdbToDPD != null) {
                this.pdbToDPDMap.putIfAbsent(aProteinData, aPdbToDPD);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // Do nothing
        }
    }

    /**
     * Returns number of pooled PdbToDpd instances
     *
     * @return Number of pooled PdbToDpd instances
     */
    public int getPoolSize() {
        return this.pdbToDPDMap.size();
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Clears pool
     */
    public void clear() {
        this.pdbToDPDMap.clear();
    }
    // </editor-fold>
    
}
