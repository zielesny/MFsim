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
package de.gnwi.mfsim.model.particleStructure;

import de.gnwi.mfsim.model.message.ModelMessage;

/**
 * Molecule concentration type
 *
 * @author Achim Zielesny
 */
public enum MoleculeConcentrationType {

    // <editor-fold defaultstate="collapsed" desc="Definitions">
    /**
     * Molar percent
     */
    MOLAR_PERCENT,
    /**
     * Weight percent
     */
    WEIGHT_PERCENT,
    /**
     * Mol per litre
     */
    MOL,
    /**
     * Gram
     */
    GRAM,
    /**
     * Undefined
     */
    UNDEFINED;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns MoleculeConcentrationType representation
     *
     * @return MoleculeConcentrationType representation
     */
    public String toRepresentation() {
        switch (this) {
            case MOLAR_PERCENT:
                return ModelMessage.get("JdpdInputFile.parameter.molarPercent");
            case WEIGHT_PERCENT:
                return ModelMessage.get("JdpdInputFile.parameter.weightPercent");
            case MOL:
                return ModelMessage.get("JdpdInputFile.parameter.mol");
            case GRAM:
                return ModelMessage.get("JdpdInputFile.parameter.gram");
            default:
                return ModelMessage.get("MoleculeConcentrationType.Undefined");
        }
    }

    /**
     * Returns MoleculeConcentrationType of representation
     *
     * @param aRepresentation MoleculeConcentrationType representation
     * @return MoleculeConcentrationType
     */
    public static MoleculeConcentrationType toMoleculeConcentrationType(String aRepresentation) {
        if (aRepresentation.trim().equals(ModelMessage.get("JdpdInputFile.parameter.molarPercent").trim())) {
            return MoleculeConcentrationType.MOLAR_PERCENT;
        }
        if (aRepresentation.trim()
                .equals(ModelMessage.get("JdpdInputFile.parameter.weightPercent").trim())) {
            return MoleculeConcentrationType.WEIGHT_PERCENT;
        }
        if (aRepresentation.trim().equals(ModelMessage.get("JdpdInputFile.parameter.mol").trim())) {
            return MoleculeConcentrationType.MOL;
        }
        if (aRepresentation.trim().equals(ModelMessage.get("JdpdInputFile.parameter.gram").trim())) {
            return MoleculeConcentrationType.GRAM;
        }
        if (aRepresentation.trim().equals(ModelMessage.get("MoleculeConcentrationType.Undefined").trim())) {
            return MoleculeConcentrationType.UNDEFINED;
        }
        return MoleculeConcentrationType.UNDEFINED;
    }
    // </editor-fold>

}
