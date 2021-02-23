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
package de.gnwi.mfsim.model.particle;

import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Data structure for information in text line of particle set in the
 * repulsion parameter section
 *
 * @author Achim Zielesny
 */
public class RepulsionParameterLine {
    
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Particles
     */
    private String particles;
    
    /**
     * First particle
     */
    private String firstParticle;

    /**
     * Second particle
     */
    private String secondParticle;
    
    /**
     * Repulsion parameter array
     */
    private double[] repulsionParameterArray;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aRepulsionParameterLineTokens Tokens of repulsion parameter text line
     * @throws IllegalArgumentException Thrown, if parameter is illegal
     */
    public RepulsionParameterLine(String[] aRepulsionParameterLineTokens) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aRepulsionParameterLineTokens == null || aRepulsionParameterLineTokens.length < 2) {
            throw new IllegalArgumentException("RepulsionParameterLine.Constructor: aRepulsionParameterLineTokens is null/has length less 2.");
        }
        // </editor-fold>
        this.particles = aRepulsionParameterLineTokens[0];
        String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(aRepulsionParameterLineTokens[0]);
        this.firstParticle = tmpParticles[0];
        this.secondParticle = tmpParticles[1];
        this.repulsionParameterArray = new double[aRepulsionParameterLineTokens.length - 1];
        for (int i = 0; i < this.repulsionParameterArray.length; i++) {
            this.repulsionParameterArray[i] = Double.valueOf(aRepulsionParameterLineTokens[i + 1]);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Particles
     * 
     * @return Particles
     */
    public String getParticles() {
        return this.particles;
    }

    /**
     * First particle
     * 
     * @return First particle
     */
    public String getFirstParticle() {
        return this.firstParticle;
    }

    /**
     * Second particle
     * 
     * @return Second particle
     */
    public String getSecondParticle() {
        return this.secondParticle;
    }
    
    /**
     * Repulsion parameter array
     * 
     * @return Repulsion parameter array
     */
    public double[] getRepulsionParameterArray() {
        return this.repulsionParameterArray;
    }
    // </editor-fold>
    
}
