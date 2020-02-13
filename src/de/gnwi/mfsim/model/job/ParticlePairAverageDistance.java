/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2020  Achim Zielesny (achim.zielesny@googlemail.com)
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
package de.gnwi.mfsim.model.job;

import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Particle pair average distance
 *
 * @author Achim Zielesny
 */
public class ParticlePairAverageDistance {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Particle pair
     */
    private String[] particlePair;
    /**
     * Underscore ("_") concatenated particle pair
     */
    private String underscoreConcatenatedParticlePair;
    /**
     * Average distance in Angstrom
     */
    private double averageDistance;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">

    /**
     * Constructor
     *
     * @param aParticlePair Particle pair. Index 0: First particle, Index 1: Second particle
     * @param anAverageDistance Average distance
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ParticlePairAverageDistance(String[] aParticlePair, double anAverageDistance) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aParticlePair == null || aParticlePair.length != 2) {
            throw new IllegalArgumentException("aParticlePair is illegal");
        }
        if (aParticlePair[0] == null || aParticlePair[0].isEmpty()) {
            throw new IllegalArgumentException("aParticlePair[0] is illegal");
        }
        if (aParticlePair[1] == null || aParticlePair[1].isEmpty()) {
            throw new IllegalArgumentException("aParticlePair[1] is illegal");
        }
        if (anAverageDistance < 0.0) {
            throw new IllegalArgumentException("anAverageDistance is illegal");
        }

        // </editor-fold>

        this.particlePair = aParticlePair;
        this.averageDistance = anAverageDistance;
        this.underscoreConcatenatedParticlePair = null;
    }

    /**
     * Constructor
     *
     * @param aTokenString Token string generated with this.getTokenString()
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ParticlePairAverageDistance(String aTokenString) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aTokenString == null || aTokenString.isEmpty()) {
            throw new IllegalArgumentException("aTokenString is illegal");
        }

        // </editor-fold>

        String[] tmpTokens = ModelDefinitions.GENERAL_SEPARATOR_PATTERN.split(aTokenString);
        if (tmpTokens.length != 3) {
            throw new IllegalArgumentException("aTokenString is illegal");
        }
        
        this.particlePair = new String[]{tmpTokens[0], tmpTokens[1]};
        this.averageDistance = Double.valueOf(tmpTokens[2]);
        this.underscoreConcatenatedParticlePair = null;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get)">

    /**
     * Returns token string
     *
     * @return Token string
     */
    public String getTokenString() {
        return this.getFirstParticle() + ModelDefinitions.GENERAL_SEPARATOR + this.getSecondParticle() + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(this.averageDistance);
    }

    /**
     * Particle pair as string array
     *
     * @return Particle pair
     */
    public String[] getParticlePair() {
        return this.particlePair;
    }

    /**
     * Particle pair concatenated by an underscore "_"
     *
     * @return Particle pair concatenated by an underscore "_"
     */
    public String getUnderscoreConcatenatedParticlePair() {
        if (this.underscoreConcatenatedParticlePair == null) {
            this.underscoreConcatenatedParticlePair = this.getFirstParticle() + "_" + this.getSecondParticle();
        }
        return this.underscoreConcatenatedParticlePair;
    }

    /**
     * First particle
     *
     * @return First particle
     */
    public String getFirstParticle() {
        return this.particlePair[0];
    }

    /**
     * Second particle
     *
     * @return Second particle
     */
    public String getSecondParticle() {
        return this.particlePair[1];
    }

    /**
     * Average distance in Angstrom
     *
     * @return Average distance in Angstrom
     */
    public double getAverageDistance() {
        return this.averageDistance;
    }
    // </editor-fold>
}
