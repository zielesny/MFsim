/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
package de.gnwi.mfsim.model.util.test;

import de.gnwi.mfsim.model.util.StringUtilityMethods;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Achim
 */
public class TestUtilityStringMethods extends TestCase {

    StringUtilityMethods stringUtilityMethods;
    
    public TestUtilityStringMethods() {
        this.stringUtilityMethods = new StringUtilityMethods();
    }
    
    /**
     * Test getFirstProteinBackboneForceIndex()
     */
    public void test_getFirstProteinBackboneForceIndex() {
        String tmpLine = "1 myParticle <23> 1.000 2.000 3.000";
        String tmpFirstProteinBackboneForceIndex = this.stringUtilityMethods.getFirstProteinBackboneForceIndex(tmpLine);
        assertEquals(tmpFirstProteinBackboneForceIndex, "23");
    }

    /**
     * Test replaceFirstProteinBackboneForceIndex()
     */
    public void test_replaceFirstProteinBackboneForceIndex() {
        String tmpLine = "1 myParticle <23> 1.000 2.000 3.000";
        String tmpReplacement = "53";
        String tmpReplacedLine = this.stringUtilityMethods.replaceFirstProteinBackboneForceIndex(tmpLine, tmpReplacement);
        assertEquals(tmpReplacedLine, "1 myParticle 53 1.000 2.000 3.000");
    }
    
    /**
     * Test StringUtils
     */
    public void test_StringUtils() {
        String tmpStringToBeReplaced = "This is #AZ with his hat.";
        String tmpSearchString = "#" + "AZ";
        String tmpReplacement = "Achim";
        String tmpReplacedString = StringUtils.replace(tmpStringToBeReplaced, tmpSearchString, tmpReplacement);
        assertEquals(tmpReplacedString, "This is Achim with his hat.");
    }
}
