/**
 * SPICES (Simplified Particle Input ConnEction Specification)
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
package de.gnwi.mfsim.model.graphics.particle;

import de.gnwi.spices.IPointInSpace;

/**
 * Interface for graphical particle position
 *
 * @author Achim Zielesny
 */
public interface IGraphicalParticlePosition extends IPointInSpace {

    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- GraphicalParticle">
    /**
     * Graphical particle
     *
     * @return Graphical particle
     */
    public IGraphicalParticle getGraphicalParticle();

    /**
     * Graphical particle
     *
     * @param aGraphicalParticle Graphical particle
     */
    public void setGraphicalParticle(IGraphicalParticle aGraphicalParticle);
    // </editor-fold>
    // </editor-fold>    

}
