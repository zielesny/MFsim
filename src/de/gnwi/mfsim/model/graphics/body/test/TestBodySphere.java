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

import de.gnwi.mfsim.model.graphics.body.BodySphere;
import de.gnwi.spices.PointInSpace;
import junit.framework.TestCase;

/**
 * Test class for BodySphere
 * 
 * @author Achim Zielesny
 */
public class TestBodySphere extends TestCase {

    /**
     * Test method for 'de.gnwi.utility.BodySphere.getRandomValueInVolume()'
     */
    public void testGetRandomValueInVolume() {
        PointInSpace testPoint = new PointInSpace(0, 0, 0);
        BodySphere testSphere = new BodySphere(10, 20, 30, 40);
        for (int loop = 0; loop < 100; ++loop) {
            testPoint = testSphere.getRandomPointInVolume();
            assertTrue(testSphere.isInVolume(testPoint));
        }
        PointInSpace tmpPoint[] = testSphere.getRandomPointsInVolume(100);
        for (int loop = 0; loop < 100; ++loop) {
            assertTrue(testSphere.isInVolume(tmpPoint[loop]));
        }
    }

    /**
     * Test method for 'de.gnwi.utility.BodySphere.getRandomPointOnSurface()'
     */
    public void testgetRandomPointOnSurface() {
        PointInSpace testPoint;
        BodySphere testSphere = new BodySphere(10, 20, 30, 40);
        for (int loop = 0; loop < 100; ++loop) {
            testPoint = testSphere.getRandomPointOnSurface();
            assertTrue(testSphere.isInVolume(testPoint));
        }
        PointInSpace tmpPoint[] = testSphere.getRandomPointsOnSurface(100);
        for (int loop = 0; loop < 100; ++loop) {
            assertTrue(testSphere.isInVolume(tmpPoint[loop]));
        }
    }

    /**
     * Test method for 'de.gnwi.utility.BodySphere.isInVolume()'
     */
    public void testIsInVolume() {
        PointInSpace tmpCenterOfTestSphere = new PointInSpace(0, 0, 0);
        BodySphere testSphere = new BodySphere(10, tmpCenterOfTestSphere);
        PointInSpace tmpPointInsideSphere = new PointInSpace(3, 2, 1);
        PointInSpace tmpPointOutsideSphere = new PointInSpace(11, 4, 5);
        PointInSpace tmpPointOnSurfaceOfSphere = new PointInSpace(10, 0, 0);
        assertTrue(testSphere.isInVolume(tmpPointInsideSphere));
        assertFalse(testSphere.isInVolume(tmpPointOutsideSphere));
        assertTrue(testSphere.isInVolume(tmpPointOnSurfaceOfSphere));
    }

    /**
     * Test method for 'de.gnwi.utility.BodySphere.isOutsideVolume()'
     */
    public void testIsOutsideVolume() {
        PointInSpace tmpCenterOfTestSphere = new PointInSpace(0, 0, 0);
        BodySphere testSphere = new BodySphere(10, tmpCenterOfTestSphere);
        PointInSpace tmpPointInsideSphere = new PointInSpace(3, 2, 1);
        PointInSpace tmpPointOutsideSphere = new PointInSpace(11, 4, 5);
        PointInSpace tmpPointOnSurfaceOfSphere = new PointInSpace(10, 0, 0);
        assertTrue(!testSphere.isInVolume(tmpPointOutsideSphere));
        assertFalse(!testSphere.isInVolume(tmpPointInsideSphere));
        assertFalse(!testSphere.isInVolume(tmpPointOnSurfaceOfSphere));
    }

}
