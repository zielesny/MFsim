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
package de.gnwi.mfsim.model.particleStructure;

import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.mfsim.model.particleStructure.ParticleInfo;
import de.gnwi.mfsim.model.particleStructure.ParticleInfoContainer;
import de.gnwi.mfsim.model.particleStructure.Molecule;
import de.gnwi.mfsim.model.particleStructure.MoleculeConcentrationType;
import de.gnwi.mfsim.model.particleStructure.MoleculeContainer;
import junit.framework.TestCase;

/**
 * Test class for class MoleculeContainer
 *
 * @author Achim Zielesny
 */
public class TestMoleculeContainer extends TestCase {

    // <editor-fold defaultstate="collapsed" desc="Public test methods">
    /**
     * Test method
     */
    public void test_System_C10E8_H2O() {
        // DPD density
        double tmpDpdDensity = 3.0;
        // Box: 30 x 30 x 30
        double tmpBoxVolume = 27000.0;
        // Particle information
        ParticleInfo[] tmpParticleInfoArray = new ParticleInfo[4];
        tmpParticleInfoArray[0] = new ParticleInfo("A", 269.99, 42.0);
        tmpParticleInfoArray[1] = new ParticleInfo("B", 241.72, 44.0);
        tmpParticleInfoArray[2] = new ParticleInfo("C", 182.15, 31.0);
        tmpParticleInfoArray[3] = new ParticleInfo("W", 117.30, 18.0);
        ParticleInfoContainer tmpParticleInfoContainer = new ParticleInfoContainer();
        tmpParticleInfoContainer.addParticleInfo(tmpParticleInfoArray);
        // Molecules
        Molecule tmpC10E8 = new Molecule("3A-8B-C", "C10E8");
        Molecule tmpH2O = new Molecule("W", "H2O");
        // Parameter true: Weight percent will be specified
        MoleculeContainer tmpMoleculeContainer = new MoleculeContainer(tmpParticleInfoContainer,
                tmpBoxVolume, tmpDpdDensity, MoleculeConcentrationType.WEIGHT_PERCENT);
        tmpMoleculeContainer.addMolecule(tmpC10E8, 0.1);
        tmpMoleculeContainer.addMolecule(tmpH2O, 0.9);
        // Calculate concentration properties
        assertTrue("Test1", tmpMoleculeContainer.calculateConcentrationProperties());
        int tmpNumberOfC10E8 = tmpMoleculeContainer.getMoleculeInfo("C10E8")
                .getNumberOfMolecules();
        assertTrue("Test2", tmpNumberOfC10E8 == 602);
        int tmpNumberOfH2O = tmpMoleculeContainer.getMoleculeInfo("H2O").getNumberOfMolecules();
        assertTrue("Test3", tmpNumberOfH2O == 73769);
        // Total numbers of particles must be DpdDensity*BoxVolume = 81000
        int tmpTotalNumberOfParticles = tmpNumberOfC10E8 * SpicesPool.getInstance().getSpices(
                tmpMoleculeContainer.getMoleculeInfo("C10E8").getMolecule().getMolecularStructureString()).getTotalNumberOfParticles()
                + tmpNumberOfH2O * SpicesPool.getInstance().getSpices(
                        tmpMoleculeContainer.getMoleculeInfo("H2O").getMolecule().getMolecularStructureString()).getTotalNumberOfParticles();
        assertTrue("Test4", tmpTotalNumberOfParticles == 80993);
    }

    // </editor-fold>
}
