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
package de.gnwi.mfsim.model.job;

import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.job.SpherePackingUtils;
import junit.framework.TestCase;

/**
 * Test class for class SpherePackingUtils
 * 
 * @author Mirco Daniel
 */
public class TestUtilitySpherePacking extends TestCase {
	private static double SQRT3 = Math.sqrt(3);
	private static double SQRT6 = Math.sqrt(6);
	private static double EPS = 0.000000000000001;

	/**
	 * Test testParticleCoordinatesSimpelCubicInCuboid
	 */
	public void testParticleCoordinatesSimpleCubicInCuboid() {
		SpherePackingUtils particle = new SpherePackingUtils();
		assertEquals(null, particle.particleCoordinatesSimpleCubicInCuboid(0, 0, 0, 0));

		PointInSpace[] testPackingCoordinates = particle.particleCoordinatesSimpleCubicInCuboid(1.0, 1.0, 1.0, 1.0);
		assertEquals(1, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());

		testPackingCoordinates = particle.particleCoordinatesSimpleCubicInCuboid(1.0, 4.0, 1.0, 1.0);
		assertEquals(3, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesSimpleCubicInCuboid(1.0, 4.0, 4.0, 4.0);
		assertEquals(27, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesSimpleCubicInCuboid(1.0, 3.1, 3.0, 1.9);
		assertEquals(4, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(0.0, testPackingCoordinates[1].getY());
		assertEquals(0.0, testPackingCoordinates[1].getZ());
		assertEquals(0.0, testPackingCoordinates[2].getX());
		assertEquals(2.0, testPackingCoordinates[2].getY());
		assertEquals(0.0, testPackingCoordinates[2].getZ());
		assertEquals(2.0, testPackingCoordinates[3].getX());
		assertEquals(2.0, testPackingCoordinates[3].getY());
		assertEquals(0.0, testPackingCoordinates[3].getZ());
		
		// test of the overload method
		testPackingCoordinates = particle.particleCoordinatesSimpleCubicInCuboid(1.0, 3.1, 3.0, 1.9, new PointInSpace(1.0, 2.0, 3.0));
		assertEquals(4, testPackingCoordinates.length);
		assertEquals(1.0, testPackingCoordinates[0].getX());
		assertEquals(2.0, testPackingCoordinates[0].getY());
		assertEquals(3.0, testPackingCoordinates[0].getZ());
		assertEquals(3.0, testPackingCoordinates[1].getX());
		assertEquals(2.0, testPackingCoordinates[1].getY());
		assertEquals(3.0, testPackingCoordinates[1].getZ());
		assertEquals(1.0, testPackingCoordinates[2].getX());
		assertEquals(4.0, testPackingCoordinates[2].getY());
		assertEquals(3.0, testPackingCoordinates[2].getZ());
		assertEquals(3.0, testPackingCoordinates[3].getX());
		assertEquals(4.0, testPackingCoordinates[3].getY());
		assertEquals(3.0, testPackingCoordinates[3].getZ());
	}
	
	/**
	 * Test testParticleCoordinatesSimpelCubicInSphere
	 */
	public void testParticleCoordinatesSimpleCubicInSphere() {
		SpherePackingUtils particle = new SpherePackingUtils();
		assertEquals(null, particle.particleCoordinatesSimpleCubicInSphere(0, 0));

		PointInSpace[] testPackingCoordinates = particle.particleCoordinatesSimpleCubicInSphere(1.0, 1.0);
		assertEquals(1, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());

		testPackingCoordinates = particle.particleCoordinatesSimpleCubicInSphere(1, 8);
		assertEquals(257, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesSimpleCubicInSphere(1, 2);
		assertEquals(7, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(0.0, testPackingCoordinates[1].getY());
		assertEquals(0.0, testPackingCoordinates[1].getZ());
		assertEquals(0.0, testPackingCoordinates[2].getX());
		assertEquals(-2.0, testPackingCoordinates[2].getY());
		assertEquals(0.0, testPackingCoordinates[2].getZ());
		assertEquals(-2.0, testPackingCoordinates[3].getX());
		assertEquals(0.0, testPackingCoordinates[3].getY());
		assertEquals(0.0, testPackingCoordinates[3].getZ());
		assertEquals(0.0, testPackingCoordinates[4].getX());
		assertEquals(2.0, testPackingCoordinates[4].getY());
		assertEquals(0.0, testPackingCoordinates[4].getZ());
		assertEquals(0.0, testPackingCoordinates[5].getX());
		assertEquals(0.0, testPackingCoordinates[5].getY());
		assertEquals(2.0, testPackingCoordinates[5].getZ());
		assertEquals(0.0, testPackingCoordinates[6].getX());
		assertEquals(0.0, testPackingCoordinates[6].getY());
		assertEquals(-2.0, testPackingCoordinates[6].getZ());
		
		// test overload method
		testPackingCoordinates = particle.particleCoordinatesSimpleCubicInSphere(1, 2, new PointInSpace(2.0, 3.0, 4.0));
		assertEquals(7, testPackingCoordinates.length);
		assertEquals(2.0, testPackingCoordinates[0].getX());
		assertEquals(3.0, testPackingCoordinates[0].getY());
		assertEquals(4.0, testPackingCoordinates[0].getZ());
		assertEquals(4.0, testPackingCoordinates[1].getX());
		assertEquals(3.0, testPackingCoordinates[1].getY());
		assertEquals(4.0, testPackingCoordinates[1].getZ());
		assertEquals(2.0, testPackingCoordinates[2].getX());
		assertEquals(1.0, testPackingCoordinates[2].getY());
		assertEquals(4.0, testPackingCoordinates[2].getZ());
		assertEquals(0.0, testPackingCoordinates[3].getX());
		assertEquals(3.0, testPackingCoordinates[3].getY());
		assertEquals(4.0, testPackingCoordinates[3].getZ());
		assertEquals(2.0, testPackingCoordinates[4].getX());
		assertEquals(5.0, testPackingCoordinates[4].getY());
		assertEquals(4.0, testPackingCoordinates[4].getZ());
		assertEquals(2.0, testPackingCoordinates[5].getX());
		assertEquals(3.0, testPackingCoordinates[5].getY());
		assertEquals(6.0, testPackingCoordinates[5].getZ());
		assertEquals(2.0, testPackingCoordinates[6].getX());
		assertEquals(3.0, testPackingCoordinates[6].getY());
		assertEquals(2.0, testPackingCoordinates[6].getZ());
	}

	/**
	 * Test testParticleCoordinatesHexagonalCloseInCuboid
	 */
	public void testParticleCoordinatesHexagonalCloseInCuboid() {
		SpherePackingUtils particle = new SpherePackingUtils();
		assertEquals(null, particle.particleCoordinatesHexagonalCloseInCuboid(0, 0, 0, 0));

		PointInSpace[] testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 0.5, 0.5, 0.5);
		assertEquals(1, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 4.0, 0.5, 0.5);
		assertEquals(3, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(0.0, testPackingCoordinates[1].getY());
		assertEquals(0.0, testPackingCoordinates[1].getZ());
		assertEquals(4.0, testPackingCoordinates[2].getX());
		assertEquals(0.0, testPackingCoordinates[2].getY());
		assertEquals(0.0, testPackingCoordinates[2].getZ());

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 1.0, 1.74, 0.5);
		assertEquals(2, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 1.0, 3.46, 0.5);
		assertEquals(2, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 1.0, 3.47, 0.5);
		assertEquals(3, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(1.0, testPackingCoordinates[1].getX());
		assertEquals(SQRT3, testPackingCoordinates[1].getY());
		assertEquals(0.0, testPackingCoordinates[2].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[2].getY());

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 4.0, 4.0, 0.5);
		assertEquals(8, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(0.0, testPackingCoordinates[1].getY());
		assertEquals(4.0, testPackingCoordinates[2].getX());
		assertEquals(0.0, testPackingCoordinates[2].getY());
		assertEquals(1.0, testPackingCoordinates[3].getX());
		assertEquals(SQRT3, testPackingCoordinates[3].getY());
		assertEquals(3.0, testPackingCoordinates[4].getX());
		assertEquals(SQRT3, testPackingCoordinates[4].getY());
		assertEquals(0.0, testPackingCoordinates[5].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[5].getY());
		assertEquals(2.0, testPackingCoordinates[6].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[6].getY());
		assertEquals(4.0, testPackingCoordinates[7].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[7].getY());

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 1.0, 1.0, 4.0);
		assertEquals(3, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());
		assertEquals(1.0, testPackingCoordinates[1].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[1].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[1].getZ());
		assertEquals(0.0, testPackingCoordinates[2].getX());
		assertEquals(0.0, testPackingCoordinates[2].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[2].getZ());

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 4.0, 4.0, 4.0);
		assertEquals(21, testPackingCoordinates.length);
		assertEquals(1.0, testPackingCoordinates[8].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[8].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[8].getZ());
		assertEquals(3.0, testPackingCoordinates[9].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[9].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[9].getZ());
		assertEquals(0.0, testPackingCoordinates[10].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[10].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[10].getZ());
		assertEquals(2.0, testPackingCoordinates[11].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[11].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[11].getZ());
		assertEquals(4.0, testPackingCoordinates[12].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[12].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[12].getZ());
		assertEquals(0.0, testPackingCoordinates[13].getX());
		assertEquals(0.0, testPackingCoordinates[13].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[13].getZ());
		assertEquals(2.0, testPackingCoordinates[14].getX());
		assertEquals(0.0, testPackingCoordinates[14].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[14].getZ());
		assertEquals(4.0, testPackingCoordinates[15].getX());
		assertEquals(0.0, testPackingCoordinates[15].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[15].getZ());
		assertEquals(1.0, testPackingCoordinates[16].getX());
		assertEquals(SQRT3, testPackingCoordinates[16].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[16].getZ());
		assertEquals(3.0, testPackingCoordinates[17].getX());
		assertEquals(SQRT3, testPackingCoordinates[17].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[17].getZ());
		assertEquals(0.0, testPackingCoordinates[18].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[18].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[18].getZ());
		assertEquals(2.0, testPackingCoordinates[19].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[19].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[19].getZ());
		assertEquals(4.0, testPackingCoordinates[20].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[20].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[20].getZ());
		
		// Test overloaded method 
		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInCuboid(1.0, 1.0, 1.0, 4.0, new PointInSpace(1.0, 2.0, 3.0));
		assertEquals(3, testPackingCoordinates.length);
		assertEquals(1.0, testPackingCoordinates[0].getX());
		assertEquals(2.0, testPackingCoordinates[0].getY());
		assertEquals(3.0, testPackingCoordinates[0].getZ());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(2.0 +  SQRT3 / 3, testPackingCoordinates[1].getY());
		assertEquals(3.0 + 2.0 * SQRT6 / 3, testPackingCoordinates[1].getZ());
		assertEquals(1.0, testPackingCoordinates[2].getX());
		assertEquals(2.0, testPackingCoordinates[2].getY());
		assertEquals(3.0 + 4.0 * SQRT6 / 3, testPackingCoordinates[2].getZ());
	}

	/**
	 * Test testParticleCoordinatesHexagonalCloseInCuboid
	 */
	public void testParticleCoordinatesHexagonalCloseInSphere() {
		SpherePackingUtils particle = new SpherePackingUtils();
		assertEquals(null, particle.particleCoordinatesHexagonalCloseInSphere(0, 0));
		PointInSpace[] testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInSphere(1.0, 1.9);
		assertEquals(1, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());

		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInSphere(1.0, 4.0);
		assertEquals(57, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(0.0, testPackingCoordinates[1].getY());
		assertEquals(0.0, testPackingCoordinates[1].getZ());
		assertEquals(-2.0, testPackingCoordinates[2].getX());
		assertEquals(0.0, testPackingCoordinates[2].getY());
		assertEquals(0.0, testPackingCoordinates[2].getZ());
		assertEquals(4.0, testPackingCoordinates[3].getX());
		assertEquals(0.0, testPackingCoordinates[3].getY());
		assertEquals(0.0, testPackingCoordinates[3].getZ());
		assertEquals(-4.0, testPackingCoordinates[4].getX());
		assertEquals(0.0, testPackingCoordinates[4].getY());
		assertEquals(0.0, testPackingCoordinates[4].getZ());
		assertEquals(1.0, testPackingCoordinates[5].getX());
		assertEquals(SQRT3, testPackingCoordinates[5].getY());
		assertEquals(0.0, testPackingCoordinates[5].getZ());
		assertEquals(1.0, testPackingCoordinates[6].getX());
		assertEquals(-SQRT3, testPackingCoordinates[6].getY());
		assertEquals(0.0, testPackingCoordinates[6].getZ());
		assertEquals(-1.0, testPackingCoordinates[7].getX());
		assertEquals(SQRT3, testPackingCoordinates[7].getY());
		assertEquals(0.0, testPackingCoordinates[7].getZ());
		assertEquals(-1.0, testPackingCoordinates[8].getX());
		assertEquals(-SQRT3, testPackingCoordinates[8].getY());
		assertEquals(0.0, testPackingCoordinates[8].getZ());
		assertEquals(3.0, testPackingCoordinates[9].getX());
		assertEquals(SQRT3, testPackingCoordinates[9].getY());
		assertEquals(0.0, testPackingCoordinates[9].getZ());
		assertEquals(3.0, testPackingCoordinates[10].getX());
		assertEquals(-SQRT3, testPackingCoordinates[10].getY());
		assertEquals(0.0, testPackingCoordinates[10].getZ());
		assertEquals(-3.0, testPackingCoordinates[11].getX());
		assertEquals(SQRT3, testPackingCoordinates[11].getY());
		assertEquals(0.0, testPackingCoordinates[11].getZ());
		assertEquals(-3.0, testPackingCoordinates[12].getX());
		assertEquals(-SQRT3, testPackingCoordinates[12].getY());
		assertEquals(0.0, testPackingCoordinates[12].getZ());
		assertEquals(0.0, testPackingCoordinates[13].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[13].getY());
		assertEquals(0.0, testPackingCoordinates[13].getZ());
		assertEquals(0.0, testPackingCoordinates[14].getX());
		assertEquals(-2 * SQRT3, testPackingCoordinates[14].getY());
		assertEquals(0.0, testPackingCoordinates[14].getZ());
		assertEquals(2.0, testPackingCoordinates[15].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[15].getY());
		assertEquals(0.0, testPackingCoordinates[15].getZ());
		assertEquals(2.0, testPackingCoordinates[16].getX());
		assertEquals(-2 * SQRT3, testPackingCoordinates[16].getY());
		assertEquals(0.0, testPackingCoordinates[16].getZ());
		assertEquals(-2.0, testPackingCoordinates[17].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[17].getY());
		assertEquals(0.0, testPackingCoordinates[17].getZ());
		assertEquals(-2.0, testPackingCoordinates[18].getX());
		assertEquals(-2 * SQRT3, testPackingCoordinates[18].getY());
		assertEquals(0.0, testPackingCoordinates[18].getZ());
		assertEquals(1.0, testPackingCoordinates[19].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[19].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[19].getZ());
		assertEquals(1.0, testPackingCoordinates[20].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[20].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[20].getZ());
		assertEquals(-1.0, testPackingCoordinates[21].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[21].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[21].getZ());
		assertEquals(-1.0, testPackingCoordinates[22].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[22].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[22].getZ());
		assertEquals(3.0, testPackingCoordinates[23].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[23].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[23].getZ());
		assertEquals(3.0, testPackingCoordinates[24].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[24].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[24].getZ());
		assertEquals(-3.0, testPackingCoordinates[25].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[25].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[25].getZ());
		assertEquals(-3.0, testPackingCoordinates[26].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[26].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[26].getZ());
		assertEquals(0.0, testPackingCoordinates[27].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[27].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[27].getZ());
		assertEquals(0.0, testPackingCoordinates[28].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[28].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[28].getZ());
		assertEquals(2.0, testPackingCoordinates[29].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[29].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[29].getZ());
		assertEquals(2.0, testPackingCoordinates[30].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[30].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[30].getZ());
		assertEquals(-2.0, testPackingCoordinates[31].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[31].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[31].getZ());
		assertEquals(-2.0, testPackingCoordinates[32].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[32].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[32].getZ());
		assertEquals(0.0, testPackingCoordinates[33].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[33].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[33].getZ());
		assertEquals(0.0, testPackingCoordinates[34].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[34].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[34].getZ());
		assertEquals(2.0, testPackingCoordinates[35].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[35].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[35].getZ());
		assertEquals(2.0, testPackingCoordinates[36].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[36].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[36].getZ());
		assertEquals(-2.0, testPackingCoordinates[37].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[37].getY());
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[37].getZ());
		assertEquals(-2.0, testPackingCoordinates[38].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[38].getY());
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[38].getZ());
		assertEquals(1.0, testPackingCoordinates[39].getX());
		assertEquals(-5.0 * SQRT3 / 3.0, testPackingCoordinates[39].getY(), EPS);
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[39].getZ());
		assertEquals(1.0, testPackingCoordinates[40].getX());
		assertEquals(-5 * SQRT3 / 3, testPackingCoordinates[40].getY(), EPS);
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[40].getZ());
		assertEquals(-1.0, testPackingCoordinates[41].getX());
		assertEquals(-5 * SQRT3 / 3, testPackingCoordinates[41].getY(), EPS);
		assertEquals(2 * SQRT6 / 3, testPackingCoordinates[41].getZ());
		assertEquals(-1.0, testPackingCoordinates[42].getX());
		assertEquals(-5 * SQRT3 / 3, testPackingCoordinates[42].getY(), EPS);
		assertEquals(-2 * SQRT6 / 3, testPackingCoordinates[42].getZ());
		assertEquals(0.0, testPackingCoordinates[43].getX());
		assertEquals(0.0, testPackingCoordinates[43].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[43].getZ());
		assertEquals(0.0, testPackingCoordinates[44].getX());
		assertEquals(0.0, testPackingCoordinates[44].getY());
		assertEquals(-4 * SQRT6 / 3, testPackingCoordinates[44].getZ());
		assertEquals(2.0, testPackingCoordinates[45].getX());
		assertEquals(0.0, testPackingCoordinates[45].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[45].getZ());
		assertEquals(2.0, testPackingCoordinates[46].getX());
		assertEquals(0.0, testPackingCoordinates[46].getY());
		assertEquals(-4 * SQRT6 / 3, testPackingCoordinates[46].getZ());
		assertEquals(-2.0, testPackingCoordinates[47].getX());
		assertEquals(0.0, testPackingCoordinates[47].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[47].getZ());
		assertEquals(-2.0, testPackingCoordinates[48].getX());
		assertEquals(0.0, testPackingCoordinates[48].getY());
		assertEquals(-4 * SQRT6 / 3, testPackingCoordinates[48].getZ());
		assertEquals(1.0, testPackingCoordinates[49].getX());
		assertEquals(SQRT3, testPackingCoordinates[49].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[49].getZ());
		assertEquals(1.0, testPackingCoordinates[50].getX());
		assertEquals(-SQRT3, testPackingCoordinates[50].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[50].getZ());
		assertEquals(1.0, testPackingCoordinates[51].getX());
		assertEquals(SQRT3, testPackingCoordinates[51].getY());
		assertEquals(-4 * SQRT6 / 3, testPackingCoordinates[51].getZ());
		assertEquals(1.0, testPackingCoordinates[52].getX());
		assertEquals(-SQRT3, testPackingCoordinates[52].getY());
		assertEquals(-4 * SQRT6 / 3, testPackingCoordinates[52].getZ());
		assertEquals(-1.0, testPackingCoordinates[53].getX());
		assertEquals(SQRT3, testPackingCoordinates[53].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[53].getZ());
		assertEquals(-1.0, testPackingCoordinates[54].getX());
		assertEquals(-SQRT3, testPackingCoordinates[54].getY());
		assertEquals(4 * SQRT6 / 3, testPackingCoordinates[54].getZ());
		assertEquals(-1.0, testPackingCoordinates[55].getX());
		assertEquals(SQRT3, testPackingCoordinates[55].getY());
		assertEquals(-4 * SQRT6 / 3, testPackingCoordinates[55].getZ());
		assertEquals(-1.0, testPackingCoordinates[56].getX());
		assertEquals(-SQRT3, testPackingCoordinates[56].getY());
		assertEquals(-4 * SQRT6 / 3, testPackingCoordinates[56].getZ());
		
		//Test overloaded method
		testPackingCoordinates = particle.particleCoordinatesHexagonalCloseInSphere(1.0, 4.0, new PointInSpace(1.0, 2.0, 3.0));
		assertEquals(57, testPackingCoordinates.length);
		assertEquals(1.0, testPackingCoordinates[0].getX());
		assertEquals(2.0, testPackingCoordinates[0].getY());
		assertEquals(3.0, testPackingCoordinates[0].getZ());
		assertEquals(3.0, testPackingCoordinates[1].getX());
		assertEquals(2.0, testPackingCoordinates[1].getY());
		assertEquals(3.0, testPackingCoordinates[1].getZ());
		assertEquals(-1.0, testPackingCoordinates[2].getX());
		assertEquals(2.0, testPackingCoordinates[2].getY());
		assertEquals(3.0, testPackingCoordinates[2].getZ());
		assertEquals(5.0, testPackingCoordinates[3].getX());
		assertEquals(2.0, testPackingCoordinates[3].getY());
		assertEquals(3.0, testPackingCoordinates[3].getZ());
		assertEquals(-3.0, testPackingCoordinates[4].getX());
		assertEquals(2.0, testPackingCoordinates[4].getY());
		assertEquals(3.0, testPackingCoordinates[4].getZ());
		assertEquals(2.0, testPackingCoordinates[5].getX());
		assertEquals(2.0 + SQRT3, testPackingCoordinates[5].getY());
		assertEquals(3.0, testPackingCoordinates[5].getZ());
	}

	/**
	 * Test testParticleCoordinatesFaceCenteredCubicInCuboid
	 */
	public void testParticleCoordinatesFaceCenteredCubicInCuboid() {
		SpherePackingUtils particle = new SpherePackingUtils();
		assertEquals(null, particle.particleCoordinatesHexagonalCloseInCuboid(0, 0, 0, 0));

		PointInSpace[] testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInCuboid(1.0, 1.0, 1.0, 1.0);
		assertEquals(1, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInCuboid(1.0, 10.0, 1.0, 1.0);
		assertEquals(6, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInCuboid(1.0, 10.0, 10.0, 1.0);
		assertEquals(33, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInCuboid(1.0, 10.0, 11.0, 1.0);
		assertEquals(39, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInCuboid(1.0, 10.0, 10.0, 10.0);
		assertEquals(231, testPackingCoordinates.length);

		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInCuboid(1.0, 4.0, 4.0, 4.0);
		assertEquals(18, testPackingCoordinates.length);
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(0.0, testPackingCoordinates[1].getY());
		assertEquals(0.0, testPackingCoordinates[1].getZ());
		assertEquals(4.0, testPackingCoordinates[2].getX());
		assertEquals(0.0, testPackingCoordinates[2].getY());
		assertEquals(0.0, testPackingCoordinates[2].getZ());
		assertEquals(1.0, testPackingCoordinates[3].getX());
		assertEquals(SQRT3, testPackingCoordinates[3].getY());
		assertEquals(0.0, testPackingCoordinates[3].getZ());
		assertEquals(3.0, testPackingCoordinates[4].getX());
		assertEquals(SQRT3, testPackingCoordinates[4].getY());
		assertEquals(0.0, testPackingCoordinates[4].getZ());
		assertEquals(0.0, testPackingCoordinates[5].getX());
		assertEquals(2.0 * SQRT3, testPackingCoordinates[5].getY());
		assertEquals(0.0, testPackingCoordinates[5].getZ());
		assertEquals(2.0, testPackingCoordinates[6].getX());
		assertEquals(2.0 * SQRT3, testPackingCoordinates[6].getY());
		assertEquals(0.0, testPackingCoordinates[6].getZ());
		assertEquals(4.0, testPackingCoordinates[7].getX());
		assertEquals(2.0 * SQRT3, testPackingCoordinates[7].getY());
		assertEquals(0.0, testPackingCoordinates[7].getZ());
		assertEquals(1.0, testPackingCoordinates[8].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[8].getY());
		assertEquals(2.0 * SQRT6 / 3, testPackingCoordinates[8].getZ());
		assertEquals(3.0, testPackingCoordinates[9].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[9].getY());
		assertEquals(2.0 * SQRT6 / 3, testPackingCoordinates[9].getZ());
		assertEquals(0.0, testPackingCoordinates[10].getX());
		assertEquals(4.0 * SQRT3 / 3, testPackingCoordinates[10].getY());
		assertEquals(2.0 * SQRT6 / 3, testPackingCoordinates[10].getZ());
		assertEquals(2.0, testPackingCoordinates[11].getX());
		assertEquals(4.0 * SQRT3 / 3, testPackingCoordinates[11].getY());
		assertEquals(2.0 * SQRT6 / 3, testPackingCoordinates[11].getZ());
		assertEquals(4.0, testPackingCoordinates[12].getX());
		assertEquals(4.0 * SQRT3 / 3, testPackingCoordinates[12].getY());
		assertEquals(2.0 * SQRT6 / 3, testPackingCoordinates[12].getZ());
		assertEquals(0.0, testPackingCoordinates[13].getX());
		assertEquals(2.0 * SQRT3 / 3, testPackingCoordinates[13].getY());
		assertEquals(4.0 * SQRT6 / 3, testPackingCoordinates[13].getZ());
		assertEquals(2.0, testPackingCoordinates[14].getX());
		assertEquals(2.0 * SQRT3 / 3, testPackingCoordinates[14].getY());
		assertEquals(4.0 * SQRT6 / 3, testPackingCoordinates[14].getZ());
		assertEquals(4.0, testPackingCoordinates[15].getX());
		assertEquals(2.0 * SQRT3 / 3, testPackingCoordinates[15].getY());
		assertEquals(4.0 * SQRT6 / 3, testPackingCoordinates[15].getZ());
		assertEquals(1.0, testPackingCoordinates[16].getX());
		assertEquals(5.0 * SQRT3 / 3, testPackingCoordinates[16].getY(), EPS);
		assertEquals(4.0 * SQRT6 / 3, testPackingCoordinates[16].getZ());
		assertEquals(3.0, testPackingCoordinates[17].getX());
		assertEquals(5.0 * SQRT3 / 3, testPackingCoordinates[17].getY(), EPS);
		assertEquals(4.0 * SQRT6 / 3, testPackingCoordinates[17].getZ());
		
		// Test of overloaded method
		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInCuboid(1.0, 4.0, 4.0, 4.0, new PointInSpace(1.0, 2.0, 3.0));
		assertEquals(18, testPackingCoordinates.length);
		assertEquals(1.0, testPackingCoordinates[0].getX());
		assertEquals(2.0, testPackingCoordinates[0].getY());
		assertEquals(3.0, testPackingCoordinates[0].getZ());
		assertEquals(3.0, testPackingCoordinates[1].getX());
		assertEquals(2.0, testPackingCoordinates[1].getY());
		assertEquals(3.0, testPackingCoordinates[1].getZ());
		assertEquals(5.0, testPackingCoordinates[2].getX());
		assertEquals(2.0, testPackingCoordinates[2].getY());
		assertEquals(3.0, testPackingCoordinates[2].getZ());
		assertEquals(2.0, testPackingCoordinates[3].getX());
		assertEquals(2.0 + SQRT3, testPackingCoordinates[3].getY());
		assertEquals(3.0, testPackingCoordinates[3].getZ());
		assertEquals(4.0, testPackingCoordinates[4].getX());
		assertEquals(2.0 + SQRT3, testPackingCoordinates[4].getY());
		assertEquals(3.0, testPackingCoordinates[4].getZ());
		assertEquals(1.0, testPackingCoordinates[5].getX());
		assertEquals(2.0 + 2.0 * SQRT3, testPackingCoordinates[5].getY());
		assertEquals(3.0, testPackingCoordinates[5].getZ());
		assertEquals(3.0, testPackingCoordinates[6].getX());
		assertEquals(2.0 + 2.0 * SQRT3, testPackingCoordinates[6].getY());
		assertEquals(3.0, testPackingCoordinates[6].getZ());
	}

	/**
	 * Test testParticleCoordinatesFaceCenteredCublicInSphere
	 */
	public void testParticleCoordinatesFaceCenteredCublicInSphere() {
		SpherePackingUtils particle = new SpherePackingUtils();
		assertEquals(null, particle.particleCoordinatesFaceCenteredCubicInSphere(0, 0));
		PointInSpace[] testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInSphere(1.0, 4.0);
		assertEquals(55, testPackingCoordinates.length);
		// Octant 1&4
		assertEquals(0.0, testPackingCoordinates[0].getX());
		assertEquals(0.0, testPackingCoordinates[0].getY());
		assertEquals(0.0, testPackingCoordinates[0].getZ());
		assertEquals(2.0, testPackingCoordinates[1].getX());
		assertEquals(0.0, testPackingCoordinates[1].getY());
		assertEquals(0.0, testPackingCoordinates[1].getZ());
		assertEquals(-2.0, testPackingCoordinates[2].getX());
		assertEquals(0.0, testPackingCoordinates[2].getY());
		assertEquals(0.0, testPackingCoordinates[2].getZ());
		assertEquals(4.0, testPackingCoordinates[3].getX());
		assertEquals(0.0, testPackingCoordinates[3].getY());
		assertEquals(0.0, testPackingCoordinates[3].getZ());
		assertEquals(-4.0, testPackingCoordinates[4].getX());
		assertEquals(0.0, testPackingCoordinates[4].getY());
		assertEquals(0.0, testPackingCoordinates[4].getZ());
		assertEquals(1.0, testPackingCoordinates[5].getX());
		assertEquals(SQRT3, testPackingCoordinates[5].getY());
		assertEquals(0.0, testPackingCoordinates[5].getZ());
		assertEquals(-1.0, testPackingCoordinates[6].getX());
		assertEquals(SQRT3, testPackingCoordinates[6].getY());
		assertEquals(0.0, testPackingCoordinates[6].getZ());
		assertEquals(3.0, testPackingCoordinates[7].getX());
		assertEquals(SQRT3, testPackingCoordinates[7].getY());
		assertEquals(0.0, testPackingCoordinates[7].getZ());
		assertEquals(-3.0, testPackingCoordinates[8].getX());
		assertEquals(SQRT3, testPackingCoordinates[8].getY());
		assertEquals(0.0, testPackingCoordinates[8].getZ());
		assertEquals(0.0, testPackingCoordinates[9].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[9].getY());
		assertEquals(0.0, testPackingCoordinates[9].getZ());
		assertEquals(2.0, testPackingCoordinates[10].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[10].getY());
		assertEquals(0.0, testPackingCoordinates[10].getZ());
		assertEquals(-2.0, testPackingCoordinates[11].getX());
		assertEquals(2 * SQRT3, testPackingCoordinates[11].getY());
		assertEquals(0.0, testPackingCoordinates[11].getZ());
		assertEquals(1.0, testPackingCoordinates[12].getX());
		assertEquals(-SQRT3, testPackingCoordinates[12].getY());
		assertEquals(0.0, testPackingCoordinates[12].getZ());
		assertEquals(-1.0, testPackingCoordinates[13].getX());
		assertEquals(-SQRT3, testPackingCoordinates[13].getY());
		assertEquals(0.0, testPackingCoordinates[13].getZ());
		assertEquals(3.0, testPackingCoordinates[14].getX());
		assertEquals(-SQRT3, testPackingCoordinates[14].getY());
		assertEquals(0.0, testPackingCoordinates[14].getZ());
		assertEquals(-3.0, testPackingCoordinates[15].getX());
		assertEquals(-SQRT3, testPackingCoordinates[15].getY());
		assertEquals(0.0, testPackingCoordinates[15].getZ());
		assertEquals(0.0, testPackingCoordinates[16].getX());
		assertEquals(-2 * SQRT3, testPackingCoordinates[16].getY());
		assertEquals(0.0, testPackingCoordinates[16].getZ());
		assertEquals(2.0, testPackingCoordinates[17].getX());
		assertEquals(-2 * SQRT3, testPackingCoordinates[17].getY());
		assertEquals(0.0, testPackingCoordinates[17].getZ());
		assertEquals(-2.0, testPackingCoordinates[18].getX());
		assertEquals(-2 * SQRT3, testPackingCoordinates[18].getY());
		assertEquals(0.0, testPackingCoordinates[18].getZ());
		assertEquals(1.0, testPackingCoordinates[19].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[19].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[19].getZ());
		assertEquals(-1.0, testPackingCoordinates[20].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[20].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[20].getZ());
		assertEquals(3.0, testPackingCoordinates[21].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[21].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[21].getZ());
		assertEquals(-3.0, testPackingCoordinates[22].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[22].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[22].getZ());
		assertEquals(0.0, testPackingCoordinates[23].getX());
		assertEquals(4.0 * SQRT3 / 3, testPackingCoordinates[23].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[23].getZ());
		assertEquals(2.0, testPackingCoordinates[24].getX());
		assertEquals(4.0 * SQRT3 / 3, testPackingCoordinates[24].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[24].getZ());
		assertEquals(-2.0, testPackingCoordinates[25].getX());
		assertEquals(4.0 * SQRT3 / 3, testPackingCoordinates[25].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[25].getZ());
		assertEquals(0.0, testPackingCoordinates[26].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[26].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[26].getZ());
		assertEquals(2.0, testPackingCoordinates[27].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[27].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[27].getZ());
		assertEquals(-2.0, testPackingCoordinates[28].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[28].getY());
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[28].getZ());
		assertEquals(1.0, testPackingCoordinates[29].getX());
		assertEquals(-5 * SQRT3 / 3, testPackingCoordinates[29].getY(), EPS);
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[29].getZ());
		assertEquals(-1.0, testPackingCoordinates[30].getX());
		assertEquals(-5 * SQRT3 / 3, testPackingCoordinates[30].getY(), EPS);
		assertEquals(2.0 / 3 * SQRT6, testPackingCoordinates[30].getZ());
		assertEquals(0.0, testPackingCoordinates[31].getX());
		assertEquals(2 * SQRT3 / 3, testPackingCoordinates[31].getY());
		assertEquals(4.0 / 3 * SQRT6, testPackingCoordinates[31].getZ());
		assertEquals(2.0, testPackingCoordinates[32].getX());
		assertEquals(2 * SQRT3 / 3, testPackingCoordinates[32].getY());
		assertEquals(4.0 / 3 * SQRT6, testPackingCoordinates[32].getZ());
		assertEquals(-2.0, testPackingCoordinates[33].getX());
		assertEquals(2 * SQRT3 / 3, testPackingCoordinates[33].getY());
		assertEquals(4.0 / 3 * SQRT6, testPackingCoordinates[33].getZ());
		assertEquals(1.0, testPackingCoordinates[34].getX());
		assertEquals(-SQRT3 / 3, testPackingCoordinates[34].getY());
		assertEquals(4.0 / 3 * SQRT6, testPackingCoordinates[34].getZ());
		assertEquals(-1.0, testPackingCoordinates[35].getX());
		assertEquals(-SQRT3 / 3, testPackingCoordinates[35].getY());
		assertEquals(4.0 / 3 * SQRT6, testPackingCoordinates[35].getZ());
		assertEquals(0.0, testPackingCoordinates[36].getX());
		assertEquals(-4 * SQRT3 / 3, testPackingCoordinates[36].getY());
		assertEquals(4.0 / 3 * SQRT6, testPackingCoordinates[36].getZ());
		// Octant 5&8
		assertEquals(0.0, testPackingCoordinates[37].getX());
		assertEquals(2 * SQRT3 / 3, testPackingCoordinates[37].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[37].getZ());
		assertEquals(2.0, testPackingCoordinates[38].getX());
		assertEquals(2 * SQRT3 / 3, testPackingCoordinates[38].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[38].getZ());
		assertEquals(-2.0, testPackingCoordinates[39].getX());
		assertEquals(2 * SQRT3 / 3, testPackingCoordinates[39].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[39].getZ());
		assertEquals(1.0, testPackingCoordinates[40].getX());
		assertEquals(5 * SQRT3 / 3, testPackingCoordinates[40].getY(), EPS);
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[40].getZ());
		assertEquals(-1.0, testPackingCoordinates[41].getX());
		assertEquals(5 * SQRT3 / 3, testPackingCoordinates[41].getY(), EPS);
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[41].getZ());
		assertEquals(1.0, testPackingCoordinates[42].getX());
		assertEquals(-SQRT3 / 3, testPackingCoordinates[42].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[42].getZ());
		assertEquals(-1.0, testPackingCoordinates[43].getX());
		assertEquals(-SQRT3 / 3, testPackingCoordinates[43].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[43].getZ());
		assertEquals(3.0, testPackingCoordinates[44].getX());
		assertEquals(-SQRT3 / 3, testPackingCoordinates[44].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[44].getZ());
		assertEquals(-3.0, testPackingCoordinates[45].getX());
		assertEquals(-SQRT3 / 3, testPackingCoordinates[45].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[45].getZ());
		assertEquals(0.0, testPackingCoordinates[46].getX());
		assertEquals(-4 * SQRT3 / 3, testPackingCoordinates[46].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[46].getZ());
		assertEquals(2.0, testPackingCoordinates[47].getX());
		assertEquals(-4 * SQRT3 / 3, testPackingCoordinates[47].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[47].getZ());
		assertEquals(-2.0, testPackingCoordinates[48].getX());
		assertEquals(-4 * SQRT3 / 3, testPackingCoordinates[48].getY());
		assertEquals(-2.0 / 3 * SQRT6, testPackingCoordinates[48].getZ());
		assertEquals(1.0, testPackingCoordinates[49].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[49].getY());
		assertEquals(-4.0 / 3 * SQRT6, testPackingCoordinates[49].getZ());
		assertEquals(-1.0, testPackingCoordinates[50].getX());
		assertEquals(SQRT3 / 3, testPackingCoordinates[50].getY());
		assertEquals(-4.0 / 3 * SQRT6, testPackingCoordinates[50].getZ());
		assertEquals(0.0, testPackingCoordinates[51].getX());
		assertEquals(4 * SQRT3 / 3, testPackingCoordinates[51].getY());
		assertEquals(-4.0 / 3 * SQRT6, testPackingCoordinates[51].getZ());
		assertEquals(0.0, testPackingCoordinates[52].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[52].getY());
		assertEquals(-4.0 / 3 * SQRT6, testPackingCoordinates[52].getZ());
		assertEquals(2.0, testPackingCoordinates[53].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[53].getY());
		assertEquals(-4.0 / 3 * SQRT6, testPackingCoordinates[53].getZ());
		assertEquals(-2.0, testPackingCoordinates[54].getX());
		assertEquals(-2 * SQRT3 / 3, testPackingCoordinates[54].getY());
		assertEquals(-4.0 / 3 * SQRT6, testPackingCoordinates[54].getZ());

		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInSphere(1.0, 5.0);
		assertEquals(87, testPackingCoordinates.length);
		
		// Test overloaded method
		testPackingCoordinates = particle.particleCoordinatesFaceCenteredCubicInSphere(1.0, 4.0, new PointInSpace(1.0, 2.0, 3.0));
		assertEquals(55, testPackingCoordinates.length);
		// Octant 1&4
		assertEquals(1.0, testPackingCoordinates[0].getX());
		assertEquals(2.0, testPackingCoordinates[0].getY());
		assertEquals(3.0, testPackingCoordinates[0].getZ());
		assertEquals(3.0, testPackingCoordinates[1].getX());
		assertEquals(2.0, testPackingCoordinates[1].getY());
		assertEquals(3.0, testPackingCoordinates[1].getZ());
		assertEquals(-1.0, testPackingCoordinates[2].getX());
		assertEquals(2.0, testPackingCoordinates[2].getY());
		assertEquals(3.0, testPackingCoordinates[2].getZ());
		assertEquals(5.0, testPackingCoordinates[3].getX());
		assertEquals(2.0, testPackingCoordinates[3].getY());
		assertEquals(3.0, testPackingCoordinates[3].getZ());
		assertEquals(-3.0, testPackingCoordinates[4].getX());
		assertEquals(2.0, testPackingCoordinates[4].getY());
		assertEquals(3.0, testPackingCoordinates[4].getZ());
		assertEquals(2.0, testPackingCoordinates[5].getX());
		assertEquals(2.0 + SQRT3, testPackingCoordinates[5].getY());
		assertEquals(3.0, testPackingCoordinates[5].getZ());
		assertEquals(0.0, testPackingCoordinates[6].getX());
		assertEquals(2.0 + SQRT3, testPackingCoordinates[6].getY());
		assertEquals(3.0, testPackingCoordinates[6].getZ());
	}
}
