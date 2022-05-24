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
package de.gnwi.mfsim.model.particle;

import de.gnwi.mfsim.model.particle.StandardParticleDescription;
import junit.framework.TestCase;

/**
 * Tests class StandardParticleDescription
 *
 * @author Achim Zielesny
 *
 */
public class TestStandardParticleDescription extends TestCase {
    // <editor-fold defaultstate="collapsed" desc="Public test methods">
    /**
     * Tests class StandardParticleDescription
     */
    public void testParticleData() {
        String tmpParticle = "F";
        String tmpName = "Name";
        String tmpMolWeightInDpdUnits = "MolWeightInDpdUnits";
        String tmpMolWeightInGMol = "MolWeightInGMol";
        String tmpCharge = "Charge";
        String tmpVolume = "Volume";
        String tmpGraphicsRadius = "GraphicsRadius";
        String tmpStandardColor = "BLUE";
        StandardParticleDescription tmpParticleData = new StandardParticleDescription(tmpParticle, tmpName, tmpMolWeightInDpdUnits, tmpCharge, tmpMolWeightInGMol, tmpVolume, tmpGraphicsRadius, tmpStandardColor);
        String tmpParticleDataString = tmpParticleData.getParticleDescriptionString();
        StandardParticleDescription tmpNewParticleData = new StandardParticleDescription(tmpParticleDataString);
        assertTrue("Test1", tmpParticle.equals(tmpNewParticleData.getParticle()));
        assertTrue("Test2", tmpName.equals(tmpNewParticleData.getName()));
        assertTrue("Test3", tmpMolWeightInDpdUnits.equals(tmpNewParticleData.getMolWeightInDpdUnits()));
        assertTrue("Test4", tmpMolWeightInGMol.equals(tmpNewParticleData.getMolWeightInGMol()));
        assertTrue("Test5", tmpCharge.equals(tmpNewParticleData.getCharge()));
        assertTrue("Test6", tmpVolume.equals(tmpNewParticleData.getVolume()));
        assertTrue("Test7", tmpGraphicsRadius.equals(tmpNewParticleData.getGraphicsRadius()));
        assertTrue("Test8", tmpStandardColor.equals(tmpNewParticleData.getStandardColor()));
    }
    // </editor-fold>
}
