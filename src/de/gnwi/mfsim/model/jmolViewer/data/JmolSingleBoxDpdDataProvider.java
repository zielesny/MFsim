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
package de.gnwi.mfsim.model.jmolViewer.data;

import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticlePosition;

/**
 * Data provider handling single box GrpahicalParticlePosition data.
 *
 * @author Andreas Truszkowski
 */
public class JmolSingleBoxDpdDataProvider implements IDataProvider {

    /**
     * Graphical particle position data.
     */
    private IGraphicalParticlePosition[] data;

    /**
     * Creates a new instance.
     *
     * @param aData Graphical particle position data.
     */
    public JmolSingleBoxDpdDataProvider(IGraphicalParticlePosition[] aData) {
        this.data = aData;
    }

    @Override
    public IGraphicalParticlePosition[] getNextWork(int aStep) throws Exception {
        return this.data;
    }
}
