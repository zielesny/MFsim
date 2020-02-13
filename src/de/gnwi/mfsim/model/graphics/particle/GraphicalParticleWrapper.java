/**
 * SPICES (Simplified Particle Input ConnEction Specification)
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
package de.gnwi.mfsim.model.graphics.particle;

import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;
import java.awt.Color;

/**
 * Wrapper for Graphical particle interface
 *
 * @author Achim Zielesny
 *
 */
public class GraphicalParticleWrapper implements IGraphicalParticle {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Particle
     */
    private final String particle;

    /**
     * The name of the particle
     */
    private final String particleName;

    /**
     * The radius of the particle
     */
    private final double particleRadius;

    /**
     * The color of the particle
     */
    private final Color particleColor;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor. NOTE: No checks are performed
     *
     * @param aParticle Particle
     * @param aParticleName The name of the particle
     * @param aParticleColor The color of the particle
     * @param aParticleRadius The radius of the particle
     */
    public GraphicalParticleWrapper(
        String aParticle, 
        String aParticleName, 
        Color aParticleColor, 
        double aParticleRadius
    ) {
        this.particle = aParticle;
        this.particleName = aParticleName;
        this.particleColor = aParticleColor;
        this.particleRadius = aParticleRadius;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Particle
     *
     * @return Particle
     */
    @Override
    public String getParticle() {
        return this.particle;
    }

    /**
     * Radius of the particle
     *
     * @return Radius of the particle
     */
    @Override
    public double getParticleRadius() {
        return this.particleRadius;
    }

    /**
     * Name of the particle
     *
     * @return Name of the particle
     */
    public String getParticleName() {
        return this.particleName;
    }
    // </editor-fold>

}
