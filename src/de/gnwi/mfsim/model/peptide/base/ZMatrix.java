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
package de.gnwi.mfsim.model.peptide.base;

import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import java.util.ArrayList;

/**
 * Class for storing a chemical ZMatrix.
 *
 * @author Andreas Truszkowski
 */
public class ZMatrix {

    // Fields
    ////////////////////////////////////////////////////////////////////////////
    /**
     * ZMatrix value container.
     */
    ArrayList<Double[]> zMatrix = null;

    // Constructor
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a new instance.
     */
    public ZMatrix() {
        this.zMatrix = new ArrayList<Double[]>();
    }

    // Methods
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Sets the distance between the first two atoms. Has to be the first entry
     * in the ZMatrix.
     *
     * @param aDistance Distance in Angstrom.
     */
    public void addFirstDistance(double aDistance) {
        this.zMatrix.clear();
        Double[] tmpDistanceArray = new Double[1];
        tmpDistanceArray[0] = aDistance;
        this.zMatrix.add(tmpDistanceArray);
    }

    /**
     * Sets the distance between the second and the third atom and the angle
     * between the first three atoms. Has to be the second entry in the ZMatrix.
     *
     * @param aDistance Distance in Angstrom.
     * @param anAngle Angle in degrees.
     * @throws DpdPeptideException DpdPeptideException
     */
    public void addFirstDistanceAnglePair(double aDistance, double anAngle) throws DpdPeptideException {
        if (this.zMatrix.size() != 1) {
            throw new DpdPeptideException("Peptide.IllegalZMatrixStateA");
        }
        Double[] tmpDistanceArray = new Double[2];
        tmpDistanceArray[0] = aDistance;
        tmpDistanceArray[1] = anAngle;
        this.zMatrix.add(tmpDistanceArray);
    }

    /**
     * Sets the distance, the angle and the dihedral angle from atom four and
     * higher. Entry three or greater in the ZMatrix.
     *
     * @param aDistance Distance in Angstrom.
     * @param anAngle Angle in degrees.
     * @param aDihedralAngle Dihedral angle in degrees.
     * @throws DpdPeptideException DpdPeptideException
     */
    public void addDistanceAngleDihedralAngleTriplet(double aDistance, double anAngle, double aDihedralAngle) throws DpdPeptideException {
        if (this.zMatrix.size() < 2) {
            throw new DpdPeptideException("Peptide.IllegalZMatrixStateB");
        }
        Double[] tmpDistanceArray = new Double[3];
        tmpDistanceArray[0] = aDistance;
        tmpDistanceArray[1] = anAngle;
        tmpDistanceArray[2] = aDihedralAngle;
        this.zMatrix.add(tmpDistanceArray);
    }

    @Override
    public String toString() {
        String tmpZMatrixString = "";
        tmpZMatrixString += String.format("%6.2f", this.zMatrix.get(0)[0]) + "\n";
        tmpZMatrixString += String.format("%6.2f", this.zMatrix.get(1)[0]) + " "
                + String.format("%6.2f", this.zMatrix.get(1)[1]) + " "
                + "\n";
        for (int i = 2; i < this.zMatrix.size(); i++) {
            tmpZMatrixString += String.format("%6.2f", this.zMatrix.get(i)[0]) + " "
                    + String.format("%6.2f", this.zMatrix.get(i)[1]) + " "
                    + String.format("%6.2f", this.zMatrix.get(i)[2]) + " "
                    + "\n";
        }
        return tmpZMatrixString;
    }

    /**
     * Gets the Z-Matrix. First index: Index of the C-alpha-atoms. Second index:
     * 0 = Atom distance; 1 = Torsion angle; 2 = Dihedral Angle;
     *
     * @return The Z-MAtrix.
     */
    public Double[][] getZMatrix() {
        Double[][] tmpZMatrix = new Double[this.zMatrix.size()][];
        for (int i = 0; i < this.zMatrix.size(); i++) {
            tmpZMatrix[i] = this.zMatrix.get(i);
        }
        return tmpZMatrix;
    }

    /**
     * Gets the size of the ZMatrix.
     *
     * @return Size of the ZMatrix.
     */
    public int size() {
        return this.zMatrix.size();
    }
}
