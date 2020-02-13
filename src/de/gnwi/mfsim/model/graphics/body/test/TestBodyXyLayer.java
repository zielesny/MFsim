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
package de.gnwi.mfsim.model.graphics.body.test;

import de.gnwi.mfsim.model.graphics.body.BodyXyLayer;
import de.gnwi.spices.PointInSpace;
import junit.framework.TestCase;

/**
 * Test class for BodyXyLayer
 * 
 * @author Achim Zielesny
 */
public class TestBodyXyLayer extends TestCase {

	/**
	 * Test method for 'de.gnwi.utility.BodyXyLayer.getRandomValueInVolume()'
	 */
	public void testGetRandomValueInVolume() {
		PointInSpace testPoint = new PointInSpace(0, 0, 0);
		BodyXyLayer testXyLayer = new BodyXyLayer(10, 15, 20, 20, 30, 40);
		for (int loop = 0; loop < 100; ++loop) {
			testPoint = testXyLayer.getRandomPointInVolume();
			assertTrue(testXyLayer.isInVolume(testPoint));
		}
		PointInSpace tmpPoint[] = testXyLayer.getRandomPointsInVolume(100);
		for (int loop = 0; loop < 100; ++loop) {
			assertTrue(testXyLayer.isInVolume(tmpPoint[loop]));
		}
	}

	/**
	 * Test method for 'de.gnwi.utility.BodyXyLayer.getRandomPointOnSurface()'
	 */
	public void testgetRandomPointOnSurface() {
            PointInSpace testPoint;
            BodyXyLayer testXyLayer = new BodyXyLayer(10, 15, 20, 20, 30, 40);
            for (int loop = 0; loop < 100; ++loop) {
                testPoint = testXyLayer.getRandomPointOnSurface();
                assertTrue(testXyLayer.isInVolume(testPoint));
            }
            PointInSpace tmpPoint[] = testXyLayer.getRandomPointsOnSurface(100);
            for (int loop = 0; loop < 100; ++loop) {
                assertTrue(testXyLayer.isInVolume(tmpPoint[loop]));
            }
	}

	/**
	 * Test method for 'de.gnwi.utility.BodyXyLayer.isInVolume()'
	 */
	public void testIsInVolume() {
		PointInSpace tmpCenterOfTestXyLayer = new PointInSpace(0, 0, 0);
		BodyXyLayer testXyLayer = new BodyXyLayer(10, 15, 20, tmpCenterOfTestXyLayer);
		PointInSpace tmpPointInsideXyLayer = new PointInSpace(3, 2, 1);
		PointInSpace tmpPointOutsideXyLayer = new PointInSpace(20, 4, 5);
		PointInSpace tmpPointOnSurfaceOfXyLayer = new PointInSpace(5, 0, 10);
		assertTrue(testXyLayer.isInVolume(tmpPointInsideXyLayer));
		assertFalse(testXyLayer.isInVolume(tmpPointOutsideXyLayer));
		assertTrue(testXyLayer.isInVolume(tmpPointOnSurfaceOfXyLayer));
	}

	/**
	 * Test method for 'de.gnwi.utility.BodyXyLayer.isOutsideVolume()'
	 */
	public void testIsOutsideVolume() {
		PointInSpace tmpCenterOfTestXyLayer = new PointInSpace(0, 0, 0);
		BodyXyLayer testXyLayer = new BodyXyLayer(10, 15, 20, tmpCenterOfTestXyLayer);
		PointInSpace tmpPointInsideXyLayer = new PointInSpace(3, 2, 1);
		PointInSpace tmpPointOutsideXyLayer = new PointInSpace(20, 4, 5);
		PointInSpace tmpPointOnSurfaceOfXyLayer = new PointInSpace(5, 0, 10);
		assertTrue(!testXyLayer.isInVolume(tmpPointOutsideXyLayer));
		assertFalse(!testXyLayer.isInVolume(tmpPointInsideXyLayer));
		assertFalse(!testXyLayer.isInVolume(tmpPointOnSurfaceOfXyLayer));
	}

}
