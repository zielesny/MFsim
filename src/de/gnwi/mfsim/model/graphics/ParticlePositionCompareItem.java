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
package de.gnwi.mfsim.model.graphics;

import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Sort item for particle positions
 *
 * @author Achim Zielesny
 *
 */
public class ParticlePositionCompareItem implements Comparable<ParticlePositionCompareItem> {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * x-Position
     */
    private double xPosition;
    /**
     * y-Position
     */
    private double yPosition;
    /**
     * z-Position
     */
    private double zPosition;
    /**
     * Particle
     */
    private String particle;
    /**
     * Moelcule name
     */
    private String molecule;
    /**
     * Molecule name plus particle string for comparison
     */
    private String moleculeParticleString;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor. (No checks are performed)
     *
     * @param aXPosition x-Position
     * @param aYPosition y-Position
     * @param aZPosition z-Position
     * @param aParticle Particle
     * @param aMolecule Molecule name
     */
    public ParticlePositionCompareItem(double aXPosition, double aYPosition, double aZPosition, String aParticle, String aMolecule) {
        this.xPosition = aXPosition;
        this.yPosition = aYPosition;
        this.zPosition = aZPosition;
        this.particle = aParticle;
        this.molecule = aMolecule;

        // Compare string is molecule + particle with separator
        this.moleculeParticleString = aMolecule + ModelDefinitions.GENERAL_SEPARATOR + aParticle;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * compareTo() method for Comparable interface.
     * Compares moleculeParticleString.
     *
     * @param anotherParticlePositionComapreItem ParticlePositionComapreItem
     * instance
     * @return Standard compareTo-result
     */
    public int compareTo(ParticlePositionCompareItem anotherParticlePositionComapreItem) {
        return this.moleculeParticleString.compareTo(anotherParticlePositionComapreItem.getMoleculeParticleString());
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Molecule name plus particle string for comparison
     *
     * @return Molecule name plus particle string for comparison
     */
    public String getMoleculeParticleString() {
        return this.moleculeParticleString;
    }

    /**
     * x-Position
     *
     * @return x-Position
     */
    public double getXPosition() {
        return this.xPosition;
    }

    /**
     * y-Position
     *
     * @return y-Position
     */
    public double getYPosition() {
        return this.yPosition;
    }

    /**
     * z-Position
     *
     * @return z-Position
     */
    public double getZPosition() {
        return this.zPosition;
    }

    /**
     * Particle
     *
     * @return particle
     */
    public String getParticle() {
        return this.particle;
    }

    /**
     * Molecule
     *
     * @return Molecule
     */
    public String getMolecule() {
        return this.molecule;
    }

	// </editor-fold>

}
