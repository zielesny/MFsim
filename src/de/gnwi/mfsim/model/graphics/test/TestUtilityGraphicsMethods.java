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
package de.gnwi.mfsim.model.graphics.test;

import de.gnwi.mfsim.model.graphics.body.BodySphere;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayer;
import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * Test class for GraphicsUtilityMethods
 *
 * @author Achim Zielesny
 */
public class TestUtilityGraphicsMethods extends TestCase {

    //<editor-fold defaultstate="collapsed" desc="getPointsInSpaceAlongStraightLine Tests">
    /**
     * Test method for 'de.gnwi.utility.GraphicsUtilityMethods.getPointsInSpaceAlongStraightLine()'
     */
    public void testGetPointsInSpaceAlongStraightLine() {
        GraphicsUtilityMethods tmpUtilityGraphicsMethods = new GraphicsUtilityMethods();
        PointInSpace tmpStartPoint;
        PointInSpace tmpEndPoint;
        double tmpStepDistance;
        LinkedList<PointInSpace> tmpResultList;
        
        tmpStartPoint = new PointInSpace(0.0, 0.0, 0.0);
        tmpEndPoint = new PointInSpace(10.0, 0.0, 0.0);
        tmpStepDistance = 1.0;
        tmpResultList = tmpUtilityGraphicsMethods.getPointsInSpaceAlongStraightLine(tmpStartPoint, tmpEndPoint, tmpStepDistance);
        assertNotNull("Test1", tmpResultList);

        tmpStartPoint = new PointInSpace(0.0, 0.0, 0.0);
        tmpEndPoint = new PointInSpace(10.0, 0.0, 0.0);
        tmpStepDistance = 1.3;
        tmpResultList = tmpUtilityGraphicsMethods.getPointsInSpaceAlongStraightLine(tmpStartPoint, tmpEndPoint, tmpStepDistance);
        assertNotNull("Test2", tmpResultList);

        tmpStartPoint = new PointInSpace(0.0, 0.0, 0.0);
        tmpEndPoint = new PointInSpace(10.0, 0.0, 0.0);
        tmpStepDistance = 2.0;
        tmpResultList = tmpUtilityGraphicsMethods.getPointsInSpaceAlongStraightLine(tmpStartPoint, tmpEndPoint, tmpStepDistance);
        assertNotNull("Test3", tmpResultList);

        tmpStartPoint = new PointInSpace(0.0, 0.0, 0.0);
        tmpEndPoint = new PointInSpace(10.0, 0.0, 0.0);
        tmpStepDistance = 2.0001;
        tmpResultList = tmpUtilityGraphicsMethods.getPointsInSpaceAlongStraightLine(tmpStartPoint, tmpEndPoint, tmpStepDistance);
        assertNotNull("Test4", tmpResultList);
    }
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="isSphereXyLayerOverlap Tests">
    /**
     * Test 1 of isSphereXyLayerOverlap method, of class GraphicsUtilityMethods.
     * Overlap
     */
    public void testIsSphereXyLayerOverlap1() {
        System.out.println("isSphereXyLayerOverlap1");
        BodySphere tmpSphere = new BodySphere(10, new PointInSpace(0, 0, -9));
        BodyXyLayer tmpXyLayer = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = true;
        boolean tmpResult = tmpInstance.isSphereXyLayerOverlap(tmpSphere, tmpXyLayer);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 2 of isSphereXyLayerOverlap method, of class GraphicsUtilityMethods.
     * Overlap (touching surface on surface)
     */
    public void testIsSphereXyLayerOverlap2() {
        System.out.println("isSphereXyLayerOverlap2");
        BodySphere tmpSphere = new BodySphere(10, new PointInSpace(-10, 20, 10));
        BodyXyLayer tmpXyLayer = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = true;
        boolean tmpResult = tmpInstance.isSphereXyLayerOverlap(tmpSphere, tmpXyLayer);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 3 of isSphereXyLayerOverlap method, of class GraphicsUtilityMethods.
     * Overlap (Sphere inside XyLayer)
     */
    public void testIsSphereXyLayerOverlap3() {
        System.out.println("isSphereXyLayerOverlap3");
        BodySphere tmpSphere = new BodySphere(5, new PointInSpace(0, 0, 10));
        BodyXyLayer tmpXyLayer = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = true;
        boolean tmpResult = tmpInstance.isSphereXyLayerOverlap(tmpSphere, tmpXyLayer);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 4 of isSphereXyLayerOverlap method, of class GraphicsUtilityMethods.
     * Overlap (XyLayer inside Sphere)
     */
    public void testIsSphereXyLayerOverlap4() {
        System.out.println("isSphereXyLayerOverlap4");
        BodySphere tmpSphere = new BodySphere(20, new PointInSpace(0, 0, 10));
        BodyXyLayer tmpXyLayer = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = true;
        boolean tmpResult = tmpInstance.isSphereXyLayerOverlap(tmpSphere, tmpXyLayer);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 5 of isSphereXyLayerOverlap method, of class GraphicsUtilityMethods.
     * No Overlap 1
     */
    public void testIsSphereXyLayerOverlap5() {
        System.out.println("isSphereXyLayerOverlap5");
        BodySphere tmpSphere = new BodySphere(20, new PointInSpace(-20, 10, 20));
        BodyXyLayer tmpXyLayer = new BodyXyLayer(20, 20, 20, new PointInSpace(20, 0, -10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = false;
        boolean tmpResult = tmpInstance.isSphereXyLayerOverlap(tmpSphere, tmpXyLayer);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 6 of isSphereXyLayerOverlap method, of class GraphicsUtilityMethods.
     * No Overlap 2
     */
    public void testIsSphereXyLayerOverlap6() {
        System.out.println("isSphereXyLayerOverlap6");
        BodySphere tmpSphere = new BodySphere(10, new PointInSpace(-15, 15, 28));
        BodyXyLayer tmpXyLayer = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = false;
        boolean tmpResult = tmpInstance.isSphereXyLayerOverlap(tmpSphere, tmpXyLayer);
        assertEquals(tmpExpResult, tmpResult);
    }
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="isXyLayerXyLayerOverlap Tests">

    /**
     * Test 1 of isXyLayerXyLayerOverlap method, of class
 GraphicsUtilityMethods. Overlap
     */
    public void testIsXyLayerXyLayerOverlap1() {
        System.out.println("isXyLayerXyLayerOverlap1");
        BodyXyLayer tmpXyLayer1 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        BodyXyLayer tmpXyLayer2 = new BodyXyLayer(20, 20, 20, new PointInSpace(10, 10, 0));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = true;
        boolean tmpResult = tmpInstance.isXyLayerXyLayerOverlap(tmpXyLayer1, tmpXyLayer2);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 2 of isXyLayerXyLayerOverlap method, of class
 GraphicsUtilityMethods. Overlap (Surface on Surface)
     */
    public void testIsXyLayerXyLayerOverlap2() {
        System.out.println("isXyLayerXyLayerOverlap2");
        BodyXyLayer tmpXyLayer1 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        BodyXyLayer tmpXyLayer2 = new BodyXyLayer(20, 20, 20, new PointInSpace(10, 10, -10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = true;
        boolean tmpResult = tmpInstance.isXyLayerXyLayerOverlap(tmpXyLayer1, tmpXyLayer2);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 3 of isXyLayerXyLayerOverlap method, of class
 GraphicsUtilityMethods. Overlap (Identical)
     */
    public void testIsXyLayerXyLayerOverlap3() {
        System.out.println("isXyLayerXyLayerOverlap3");
        BodyXyLayer tmpXyLayer1 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        BodyXyLayer tmpXyLayer2 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 10));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = true;
        boolean tmpResult = tmpInstance.isXyLayerXyLayerOverlap(tmpXyLayer1, tmpXyLayer2);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 4 of isXyLayerXyLayerOverlap method, of class
 GraphicsUtilityMethods. No Overlap
     */
    public void testIsXyLayerXyLayerOverlap4() {
        System.out.println("isXyLayerXyLayerOverlap4");
        BodyXyLayer tmpXyLayer1 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 0));
        BodyXyLayer tmpXyLayer2 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 21));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = false;
        boolean tmpResult = tmpInstance.isXyLayerXyLayerOverlap(tmpXyLayer1, tmpXyLayer2);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 5 of isXyLayerXyLayerOverlap method, of class
 GraphicsUtilityMethods. No Overlap
     */
    public void testIsXyLayerXyLayerOverlap5() {
        System.out.println("isXyLayerXyLayerOverlap5");
        BodyXyLayer tmpXyLayer1 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 0));
        BodyXyLayer tmpXyLayer2 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 21, 0));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = false;
        boolean tmpResult = tmpInstance.isXyLayerXyLayerOverlap(tmpXyLayer1, tmpXyLayer2);
        assertEquals(tmpExpResult, tmpResult);
    }

    /**
     * Test 6 of isXyLayerXyLayerOverlap method, of class
 GraphicsUtilityMethods. No Overlap
     */
    public void testIsXyLayerXyLayerOverlap6() {
        System.out.println("isXyLayerXyLayerOverlap6");
        BodyXyLayer tmpXyLayer1 = new BodyXyLayer(20, 20, 20, new PointInSpace(0, 0, 0));
        BodyXyLayer tmpXyLayer2 = new BodyXyLayer(20, 20, 20, new PointInSpace(21, 0, 0));
        GraphicsUtilityMethods tmpInstance = new GraphicsUtilityMethods();
        boolean tmpExpResult = false;
        boolean tmpResult = tmpInstance.isXyLayerXyLayerOverlap(tmpXyLayer1, tmpXyLayer2);
        assertEquals(tmpExpResult, tmpResult);
    }
    //</editor-fold>
    
}
