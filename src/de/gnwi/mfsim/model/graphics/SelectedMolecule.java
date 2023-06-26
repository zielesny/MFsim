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

import de.gnwi.mfsim.model.util.StandardColorEnum;

/**
 * Selected molecule
 * 
 * @author Achim Zielesny
 */
public class SelectedMolecule implements Comparable<SelectedMolecule> {
    
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Molecule name
     */
    private final String moleculeName;
    
    /**
     * Molecule index
     */
    private final int  moleculeIndex;

    /**
     * (Inclusive) Minimum particle index of molecule
     */
    private final int minParticleIndex;
    
    /**
     * Exclusive max particle index of molecule
     */
    private final int exclusiveMaxParticleIndex;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Selection color is to be used, false: Otherwise
     */
    private boolean isSelectionColorUsage;

    /**
     * Selection color
     */
    private StandardColorEnum selectionColor;
    
    /**
     * Selection transparency
     */
    private float selectionTransparency;
    
    /**
     * Entry index
     */
    private long entryIndex;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aMoleculeName Molecule name
     * @param aMoleculeIndex Molecule index
     * @param aMinParticleIndex (Inclusive) Minimum particle index of molecule
     * @param anExclusiveMaxParticleIndex Exclusive max particle index of molecule
     * @param anIsSelectionColorUsage True: Selection color is to be used, false: Otherwise
     * @param aSelectionColor Selection color
     * @param aSelectionTransparency Selection transparency
     */
    public SelectedMolecule(
        String aMoleculeName,
        int aMoleculeIndex,
        int aMinParticleIndex,
        int anExclusiveMaxParticleIndex,
        boolean anIsSelectionColorUsage,
        StandardColorEnum aSelectionColor,
        float aSelectionTransparency
    ) {
        this.moleculeName = aMoleculeName;
        this.moleculeIndex = aMoleculeIndex;
        this.minParticleIndex = aMinParticleIndex;
        this.exclusiveMaxParticleIndex = anExclusiveMaxParticleIndex;
        this.isSelectionColorUsage = anIsSelectionColorUsage;
        this.selectionColor = aSelectionColor;
        this.selectionTransparency = aSelectionTransparency;
        this.entryIndex = -1;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public compareTo() method">
    /**
     * Standard compareTo method for entry index
     *
     * @param aSelectedMolecule Selected molecule to be compared
     * @return Standard compareTo result for entry index
     * @throws IllegalArgumentException Thrown if parameter aValueItem is
     * invalid
     */
    @Override
    public int compareTo(SelectedMolecule aSelectedMolecule) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectedMolecule == null) {
            throw new IllegalArgumentException("aSelectedMolecule is null.");
        }
        // </editor-fold>
        if (this.entryIndex < aSelectedMolecule.getEntryIndex()) {
            return -1;
        } else if (this.entryIndex > aSelectedMolecule.getEntryIndex()) {
            return 1;
        } else {
            return 0;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get)">
    /**
     * Molecule name
     * 
     * @return Molecule name
     */
    public String getMoleculeName() {
        return this.moleculeName;
    }

    /**
     * Molecule index
     * 
     * @return Molecule index
     */
    public int getMoleculeIndex() {
        return this.moleculeIndex;
    }

    /**
     * (Inclusive) Minimum particle index of molecule
     * 
     * @return (Inclusive) Minimum particle index of molecule
     */
    public int getMinParticleIndex() {
        return this.minParticleIndex;
    }
    
    /**
     * Exclusive max particle index of molecule
     * 
     * @return Exclusive max particle index of molecule
     */
    public int getExclusiveMaxParticleIndex() {
        return this.exclusiveMaxParticleIndex;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * True: Selection color is to be used, false: Otherwise
     * 
     * @return True: Selection color is to be used, false: Otherwise
     */
    public boolean isSelectionColorUsage() {
        return this.isSelectionColorUsage;
    }

    /**
     * True: Selection color is to be used, false: Otherwise
     * 
     * @param aValue True: Selection color is to be used, false: Otherwise
     */
    public void setSelectionColorUsage(boolean aValue) {
        this.isSelectionColorUsage = aValue;
    }
    
    /**
     * Selection color
     * 
     * @return Selection color
     */
    public StandardColorEnum getSelectionColor() {
        return this.selectionColor;
    }
    
    /**
     * Selection color
     * 
     * @return Selection color
     */
    public StandardColorEnum getSelectionColorAsStandardColorEnum() {
        return this.selectionColor;
    }
    
    /**
     * Selection color
     * 
     * @param aSelectionColor Selection color
     */
    public void setSelectionColor(StandardColorEnum aSelectionColor) {
        this.selectionColor = aSelectionColor;
    }
    
    /**
     * Selection transparency
     * 
     * @return Selection transparency
     */
    public float getSelectionTransparency() {
        return this.selectionTransparency;
    }
    
    /**
     * Selection transparency
     * 
     * @param aSelectionTransparency Selection transparency
     */
    public void setSelectionTransparency(float aSelectionTransparency) {
        this.selectionTransparency = aSelectionTransparency;
    }

    /**
     * Entry index
     * 
     * @return Entry index
     */
    public long getEntryIndex() {
        return this.entryIndex;
    }

    /**
     * Entry index
     * 
     * @param aValue Entry index
     */
    public void setEntryIndex(long aValue) {
        this.entryIndex = aValue;
    }
    // </editor-fold>

}
