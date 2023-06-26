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
package de.gnwi.mfsim.model.peptide.base;
/**
 * Singleton for amino acid management.
 *
 * @author Andreas Truszkowski, Achim Zielesny
 */
public class AminoAcids {

    // Fields
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Holds the instance of the singleton.
     */
    private static AminoAcids SINGLETON_INSTANCE = null;

    /**
     * Amino acids utility
     */
    private final AminoAcidsUtility aminoAcidsUtility;

    // Constructor
    // /////////////////////////////////////////////////////////////////////////
    private AminoAcids() {
        this.aminoAcidsUtility = new AminoAcidsUtility();
    }

    // Methods
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Returns singleton instance.
     *
     * @return Singleton instance
     */
    public static synchronized AminoAcids getInstance() {
        if (SINGLETON_INSTANCE == null) {
            SINGLETON_INSTANCE = new AminoAcids();
        }
        return SINGLETON_INSTANCE;
    }

    /**
     * Returns amino acids utility
     *
     * @return Amino acids utility
     */
    public AminoAcidsUtility getAminoAcidsUtility() {
        return this.aminoAcidsUtility;
    }

}
