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
package de.gnwi.mfsim.model.graphics;

import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticleWrapper;
import java.awt.Color;
import java.util.HashMap;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;

/**
 * Test class for class SpicesGraphics
 *
 * @author Mirco Daniel, Achim Zielesny
 */
public class TestSpicesGraphics extends TestCase {

    /**
     * Test of GetParticleCoordinates
     */
    public void testGetParticleCoordinates() {
        String tmpStructure = "M(EtA-6M-CiB-M-CiB-5M)-M(EtA-6M-CiB-M-CiB-5M)-M-DMP-M-MeOH-M-DMP-M-M(EtA-6M-CiB-M-CiB-5M)-M(EtA-6M-CiB-M-CiB-5M)";
        SpicesGraphics tmpSpices = new SpicesGraphics(tmpStructure);
        GraphicalParticlePosition[] tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        GraphicalParticlePosition[] tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        double tmpBondLength = 1.0;
        HashMap<String, IGraphicalParticle> tmpHashMap = new HashMap<>();
        tmpHashMap.put("M", new GraphicalParticleWrapper("M", "Methan", Color.gray, 0.5));
        tmpHashMap.put("EtA", new GraphicalParticleWrapper("EtA", "EtAcetate", Color.gray, 0.5));
        tmpHashMap.put("CiB", new GraphicalParticleWrapper("CiB", "CisButen", Color.gray, 0.5));
        tmpHashMap.put("DMP", new GraphicalParticleWrapper("DMP", "DMP", Color.gray, 0.5));
        tmpHashMap.put("MeOH", new GraphicalParticleWrapper("MeOH", "MeOH", Color.gray, 0.5));
        GraphicalParticlePosition[][] tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        assertTrue("Test1001", tmpResult[0].length == 71);

        tmpStructure = "<A-B><C-D-E>";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("A", new GraphicalParticleWrapper("A", "ParticleA", Color.gray, 0.5));
        tmpHashMap.put("B", new GraphicalParticleWrapper("B", "ParticleB", Color.gray, 0.5));
        tmpHashMap.put("C", new GraphicalParticleWrapper("C", "ParticleC", Color.gray, 0.5));
        tmpHashMap.put("D", new GraphicalParticleWrapper("D", "ParticleD", Color.gray, 0.5));
        tmpHashMap.put("E", new GraphicalParticleWrapper("E", "ParticleE", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        assertTrue("Test1001a", tmpResult[0].length == 5);

        tmpStructure = "A";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[2];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[2];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpFirstParticleCoordinates[1] = new GraphicalParticlePosition(20.0, 20.0, 20.0);
        tmpLastParticleCoordinates[1] = new GraphicalParticlePosition(30.0, 30.0, 30.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("A", new GraphicalParticleWrapper("A", "ParticleA", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        assertTrue("Test1001b", tmpResult.length == 2);
        assertTrue("Test1001c", tmpResult[0].length == 1);
        assertTrue("Test1001d", tmpResult[1].length == 1);
        assertNotNull("Test1001e", tmpResult[0][0]);
        assertNotNull("Test1001f", tmpResult[1][0]);

        tmpStructure = "<A-B><C-D-E>";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[2];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[2];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpFirstParticleCoordinates[1] = new GraphicalParticlePosition(20.0, 20.0, 20.0);
        tmpLastParticleCoordinates[1] = new GraphicalParticlePosition(30.0, 30.0, 30.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("A", new GraphicalParticleWrapper("A", "ParticleA", Color.gray, 0.5));
        tmpHashMap.put("B", new GraphicalParticleWrapper("B", "ParticleB", Color.gray, 0.5));
        tmpHashMap.put("C", new GraphicalParticleWrapper("C", "ParticleC", Color.gray, 0.5));
        tmpHashMap.put("D", new GraphicalParticleWrapper("D", "ParticleD", Color.gray, 0.5));
        tmpHashMap.put("E", new GraphicalParticleWrapper("E", "ParticleE", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        assertTrue("Test1002a", tmpResult.length == 2);
        assertTrue("Test1002b", tmpResult[0].length == 5);
        assertTrue("Test1002c", tmpResult[1].length == 5);
        assertTrue("Test1002d", tmpResult[1].length == 5);
        assertTrue("Test1002e", tmpResult[1].length == 5);
        assertTrue("Test1002f", tmpResult[1].length == 5);
        assertTrue("Test1002g", tmpResult[0][0].getX() == 0.0
                && tmpResult[0][0].getY() == 0.0
                && tmpResult[0][0].getZ() == 0.0
                && tmpResult[0][1].getX() == 0.5773502691896257
                && tmpResult[0][1].getY() == 0.5773502691896257
                && tmpResult[0][1].getZ() == 0.5773502691896257
                && tmpResult[0][2].getX() == 0.0
                && tmpResult[0][2].getY() == 0.0
                && tmpResult[0][2].getZ() == 0.0
                && tmpResult[0][3].getX() == 0.5773502691896257
                && tmpResult[0][3].getY() == 0.5773502691896257
                && tmpResult[0][3].getZ() == 0.5773502691896257
                && tmpResult[0][4].getX() == 1.1547005383792515
                && tmpResult[0][4].getY() == 1.1547005383792515
                && tmpResult[0][4].getZ() == 1.1547005383792515
                && tmpResult[1][0].getX() == 20.0
                && tmpResult[1][0].getY() == 20.0
                && tmpResult[1][0].getZ() == 20.0
                && tmpResult[1][1].getX() == 20.5773502691896257
                && tmpResult[1][1].getY() == 20.5773502691896257
                && tmpResult[1][1].getZ() == 20.5773502691896257
                && tmpResult[1][2].getX() == 20.0
                && tmpResult[1][2].getY() == 20.0
                && tmpResult[1][2].getZ() == 20.0
                && tmpResult[1][3].getX() == 20.5773502691896257
                && tmpResult[1][3].getY() == 20.5773502691896257
                && tmpResult[1][3].getZ() == 20.5773502691896257
                && tmpResult[1][4].getX() == 21.1547005383792515
                && tmpResult[1][4].getY() == 21.1547005383792515
                && tmpResult[1][4].getZ() == 21.1547005383792515);

        tmpStructure = "<A><B>";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[2];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[2];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpFirstParticleCoordinates[1] = new GraphicalParticlePosition(20.0, 20.0, 20.0);
        tmpLastParticleCoordinates[1] = new GraphicalParticlePosition(30.0, 30.0, 30.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("A", new GraphicalParticleWrapper("A", "ParticleA", Color.gray, 0.5));
        tmpHashMap.put("B", new GraphicalParticleWrapper("B", "ParticleB", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        assertTrue("Test1003a", tmpResult.length == 2);
        assertTrue("Test1003b", tmpResult[0].length == 2);
        assertTrue("Test1003c", tmpResult[1].length == 2);
        assertTrue("Test1002d", tmpResult[0][0].getX() == 0.0
                && tmpResult[0][0].getY() == 0.0
                && tmpResult[0][0].getZ() == 0.0
                && tmpResult[0][1].getX() == 0.0
                && tmpResult[0][1].getY() == 0.0
                && tmpResult[0][1].getZ() == 0.0
                && tmpResult[1][0].getX() == 20.0
                && tmpResult[1][0].getY() == 20.0
                && tmpResult[1][0].getZ() == 20.0
                && tmpResult[1][1].getX() == 20.0
                && tmpResult[1][1].getY() == 20.0
                && tmpResult[1][1].getZ() == 20.0);

        tmpStructure = "M(EtA-6M-CiB-M-CiB-5M)-M(EtA-6M-CiB-M-CiB-5M)-M-DMP[START]-M-MeOH-M-DMP-M-M(EtA-6M-CiB-M-CiB-5M)-M(EtA-6M-CiB-M-CiB-5M[END])";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("M", new GraphicalParticleWrapper("M", "Methan", Color.gray, 0.5));
        tmpHashMap.put("EtA", new GraphicalParticleWrapper("EtA", "EtAcetate", Color.gray, 0.5));
        tmpHashMap.put("CiB", new GraphicalParticleWrapper("CiB", "CisButen", Color.gray, 0.5));
        tmpHashMap.put("DMP", new GraphicalParticleWrapper("DMP", "DMP", Color.gray, 0.5));
        tmpHashMap.put("MeOH", new GraphicalParticleWrapper("MeOH", "MeOH", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        assertTrue("Test1002", tmpResult[0].length == 71);

        tmpStructure = "MetNH2bb(MetOH-C6H5OH)-HCOOH-MetNH2bb(MetOH-C6H5OH)-HCOOH-MetNH2bb(MetSH[1])-HCOOH-MetNH2bb(MetSH[2])-HCOOH"
                + "-Pyrroline-HCOOH-MetNH2bb(MetOH)-HCOOH-MetNH2bb(Met(Met)(Met-Met))-HCOOH-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH-MetNH2bb(Met-Met-Met-Guanidine)-HCOOH-MetNH2bb(MetOH)-HCOOH-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH-MetNH2bb(Met-Formamide)-HCOOH-MetNH2bb(Met(Met)(Met))-HCOOH-MetNH2bb(MetSH[3])-HCOOH"
                + "-MetNH2bb(Met-Met-Met-Guanidine)-HCOOH-MetNH2bb(Met-Met(Met)(Met))-HCOOH-Pyrroline-HCOOH-MetNH2-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH-Pyrroline-HCOOH-MetNH2bb(Met-Met-HCOOH)-HCOOH-MetNH2bb(Met)-HCOOH-MetNH2bb(Met(Met)(Met-Met))-HCOOH"
                + "-MetNH2bb(MetSH[3])-HCOOH-MetNH2bb(Met)-HCOOH-MetNH2bb(MetOH-C6H5OH)-HCOOH-MetNH2bb(Met-C6H5OH)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH-MetNH2-HCOOH-MetNH2bb(MetSH[2])-HCOOH-MetNH2bb(Met(Met)(Met-Met))-HCOOH"
                + "-MetNH2bb(Met(Met)(Met-Met))-HCOOH-MetNH2bb(Met(Met)(Met-Met))-HCOOH-Pyrroline-HCOOH-MetNH2-HCOOH"
                + "-MetNH2bb(Met)-HCOOH-MetNH2bb(MetOH-C6H5OH)-HCOOH-MetNH2bb(MetSH[1])-HCOOH-Pyrroline-HCOOH"
                + "-MetNH2-HCOOH-MetNH2bb(Met-HCOOH)-HCOOH-MetNH2bb(Met-C6H5OH)-HCOOH-MetNH2bb(Met)-HCOOH-MetNH2bb(Met-Formamide)-HCOOH";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("MetNH2bb", new GraphicalParticleWrapper("MetNH2bb", "MetNH2bb", Color.gray, 0.5));
        tmpHashMap.put("MetOH", new GraphicalParticleWrapper("MetOH", "MetOH", Color.gray, 0.5));
        tmpHashMap.put("C6H5OH", new GraphicalParticleWrapper("C6H5OH", "C6H5OH", Color.gray, 0.5));
        tmpHashMap.put("HCOOH", new GraphicalParticleWrapper("HCOOH", "HCOOH", Color.gray, 0.5));
        tmpHashMap.put("MetSH", new GraphicalParticleWrapper("MetSH", "MetSH", Color.gray, 0.5));
        tmpHashMap.put("Pyrroline", new GraphicalParticleWrapper("Pyrroline", "Pyrroline", Color.gray, 0.5));
        tmpHashMap.put("Met", new GraphicalParticleWrapper("Met", "Met", Color.gray, 0.5));
        tmpHashMap.put("C6H6", new GraphicalParticleWrapper("C6H6", "C6H6", Color.gray, 0.5));
        tmpHashMap.put("Formamide", new GraphicalParticleWrapper("Formamide", "Formamide", Color.gray, 0.5));
        tmpHashMap.put("MetNH2", new GraphicalParticleWrapper("MetNH2", "MetNH2", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        boolean tmpArrayHasNull = false;
        for (GraphicalParticlePosition[] tmpResultItem : tmpResult) {
            if (tmpResultItem == null) {
                tmpArrayHasNull = true;
                break;
            }
        }
        assertFalse("Test1002a", tmpArrayHasNull);

        tmpStructure = "TriMeNH2(HAcN)(HAcN)(HAcN)";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("TriMeNH2", new GraphicalParticleWrapper("TriMeNH2", "TriMeNH2", Color.gray, 0.5));
        tmpHashMap.put("HAcN", new GraphicalParticleWrapper("HAcN", "HAcN", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        boolean tmpDoesTriMeNH2ParticleExist = false;
        for (GraphicalParticlePosition tmpSingleGraphicalParticlePosition : tmpResult[0]) {
            GraphicalParticleWrapper tmpGraphicalParticle = (GraphicalParticleWrapper) tmpSingleGraphicalParticlePosition.getGraphicalParticle();
            String tmpParticleName = tmpGraphicalParticle.getParticleName();
            if (tmpParticleName.equals("TriMeNH2")) {
                tmpDoesTriMeNH2ParticleExist = true;
            }
        }
        assertTrue("Test: TriMeNH2 particle does not exist in tmpResult", tmpDoesTriMeNH2ParticleExist);

        // Parts bug in getParticleCoordinates() 
        tmpStructure = "<6Et-MeOH-CisButene-Pr-Et-ZnAc-Et-Pr-CisButene-MeOH-6Et><TriMeNH2(HAcN)(HAcN)(HAcN)>";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("Et", new GraphicalParticleWrapper("Et", "Et", Color.gray, 0.5));
        tmpHashMap.put("MeOH", new GraphicalParticleWrapper("MeOH", "MeOH", Color.gray, 0.5));
        tmpHashMap.put("CisButene", new GraphicalParticleWrapper("CisButene", "CisButene", Color.gray, 0.5));
        tmpHashMap.put("Pr", new GraphicalParticleWrapper("Pr", "Pr", Color.gray, 0.5));
        tmpHashMap.put("ZnAc", new GraphicalParticleWrapper("ZnAc", "ZnAc", Color.gray, 0.5));
        tmpHashMap.put("TriMeNH2", new GraphicalParticleWrapper("TriMeNH2", "TriMeNH2", Color.gray, 0.5));
        tmpHashMap.put("HAcN", new GraphicalParticleWrapper("HAcN", "HAcN", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        tmpStructure = "(GLY-ILE-VAL-GLU-GLN-CYS[3]-CYS[1]-THR-SER-ILE-CYS[3]-SER-LEU-TYR-GLN-LEU-GLU-ASN-TYR-CYS[2]-ASN)"
                + "(PHE-VAL-ASN-GLN-HIS-LEU-CYS[1]-GLY-ASP-HIS-LEU-VAL-GLU-ALA-LEU-TYR-LEU-VAL-CYS[2]-GLY-GLU-ARG-GLY-PHE-PHE-TYR-THR-PRO-LYS-THR)";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("GLY", new GraphicalParticleWrapper("GLY", "Glycin", Color.gray, 0.5));
        tmpHashMap.put("ILE", new GraphicalParticleWrapper("ILE", "Isoleucin", Color.gray, 0.5));
        tmpHashMap.put("VAL", new GraphicalParticleWrapper("VAL", "Valin", Color.gray, 0.5));
        tmpHashMap.put("GLU", new GraphicalParticleWrapper("GLU", "Glutamins�ure", Color.gray, 0.5));
        tmpHashMap.put("GLN", new GraphicalParticleWrapper("GLN", "Glutamin", Color.gray, 0.5));
        tmpHashMap.put("CYS", new GraphicalParticleWrapper("CYS", "Cystein", Color.gray, 0.5));
        tmpHashMap.put("THR", new GraphicalParticleWrapper("THR", "Threonin", Color.gray, 0.5));
        tmpHashMap.put("SER", new GraphicalParticleWrapper("SER", "Serin", Color.gray, 0.5));
        tmpHashMap.put("LEU", new GraphicalParticleWrapper("LEU", "Leucin", Color.gray, 0.5));
        tmpHashMap.put("TYR", new GraphicalParticleWrapper("TYR", "Tyrosin", Color.gray, 0.5));
        tmpHashMap.put("ASN", new GraphicalParticleWrapper("ASN", "Asparagin", Color.gray, 0.5));
        tmpHashMap.put("PHE", new GraphicalParticleWrapper("PHE", "Phenylalanin", Color.gray, 0.5));
        tmpHashMap.put("HIS", new GraphicalParticleWrapper("HIS", "Histidin", Color.gray, 0.5));
        tmpHashMap.put("ASP", new GraphicalParticleWrapper("ASP", "Asparagin", Color.gray, 0.5));
        tmpHashMap.put("ALA", new GraphicalParticleWrapper("ALA", "Alanin", Color.gray, 0.5));
        tmpHashMap.put("ARG", new GraphicalParticleWrapper("ARG", "Arginin", Color.gray, 0.5));
        tmpHashMap.put("TYR", new GraphicalParticleWrapper("TYR", "Tyrosin", Color.gray, 0.5));
        tmpHashMap.put("PRO", new GraphicalParticleWrapper("PRO", "Prolin", Color.gray, 0.5));
        tmpHashMap.put("LYS", new GraphicalParticleWrapper("LYS", "LYsin", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        tmpStructure = "<V-L-S-P-A-D-K-T-N-V-K-A-A-W-G-K-V-G-A-H-A-G-E-Y-G-A-E-A-L-E-R-M-F-L-S-F-P-T-T-K-T-Y-F-P-H-F-D-L-S-H-G-S-A-Q-V-K-G-H-G-K-K-V-A-D-A-L-"
                + "T-N-A-V-A-H-V-D-D-M-P-N-A-L-S-A-L-S-D-L-H-A-H-K-L-R-V-D-P-V-N-F-K-L-L-S-H-C-L-L-V-T-L-A-A-H-L-P-A-E-F-T-P-A-V-H-A-S-L-D-K-F-L-A-S-V-S-T-V-L-T-S-K-Y-R>"
                + "<V-H-L-T-P-E-E-K-S-A-V-T-A-L-W-G-K-V-N-V-D-E-V-G-G-E-A-L-G-R-L-L-V-V-Y-P-W-T-Q-R-F-F-E-S-F-G-D-L-S-T-P-D-A-V-M-G-N-P-K-V-K-A-H-G-K-K-V-L-G-A-F-S-D-G-L"
                + "-A-H-L-D-N-L-K-G-T-F-A-T-L-S-E-L-H-C-D-K-L-H-V-D-P-E-N-F-R-L-L-G-N-V-L-V-C-V-L-A-H-H-F-G-K-E-F-T-P-P-V-Q-A-A-Y-Q-K-V-V-A-G-V-A-N-A-L-A-H-K-Y-H>"
                + "<V-L-S-P-A-D-K-T-N-V-K-A-A-W-G-K-V-G-A-H-A-G-E-Y-G-A-E-A-L-E-R-M-F-L-S-F-P-T-T-K-T-Y-F-P-H-F-D-L-S-H-G-S-A-Q-V-K-G-H-G-K-K-V-A-D-A-L-T-N-A-V-A-H-V-D-D-"
                + "M-P-N-A-L-S-A-L-S-D-L-H-A-H-K-L-R-V-D-P-V-N-F-K-L-L-S-H-C-L-L-V-T-L-A-A-H-L-P-A-E-F-T-P-A-V-H-A-S-L-D-K-F-L-A-S-V-S-T-V-L-T-S-K-Y-R>"
                + "<V-H-L-T-P-E-E-K-S-A-V-T-A-L-W-G-K-V-N-V-D-E-V-G-G-E-A-L-G-R-L-L-V-V-Y-P-W-T-Q-R-F-F-E-S-F-G-D-L-S-T-P-D-A-V-M-G-N-P-K-V-K-A-H-G-K-K-V-L-G-A-F-S-D-G-L-"
                + "A-H-L-D-N-L-K-G-T-F-A-T-L-S-E-L-H-C-D-K-L-H-V-D-P-E-N-F-R-L-L-G-N-V-L-V-C-V-L-A-H-H-F-G-K-E-F-T-P-P-V-Q-A-A-Y-Q-K-V-V-A-G-V-A-N-A-L-A-H-K-Y-H>";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("A", new GraphicalParticleWrapper("A", "A", Color.gray, 0.5));
        tmpHashMap.put("C", new GraphicalParticleWrapper("C", "C", Color.gray, 0.5));
        tmpHashMap.put("D", new GraphicalParticleWrapper("D", "D", Color.gray, 0.5));
        tmpHashMap.put("E", new GraphicalParticleWrapper("E", "E", Color.gray, 0.5));
        tmpHashMap.put("F", new GraphicalParticleWrapper("F", "F", Color.gray, 0.5));
        tmpHashMap.put("G", new GraphicalParticleWrapper("G", "G", Color.gray, 0.5));
        tmpHashMap.put("H", new GraphicalParticleWrapper("H", "H", Color.gray, 0.5));
        tmpHashMap.put("K", new GraphicalParticleWrapper("K", "K", Color.gray, 0.5));
        tmpHashMap.put("L", new GraphicalParticleWrapper("L", "L", Color.gray, 0.5));
        tmpHashMap.put("M", new GraphicalParticleWrapper("M", "M", Color.gray, 0.5));
        tmpHashMap.put("N", new GraphicalParticleWrapper("N", "N", Color.gray, 0.5));
        tmpHashMap.put("Q", new GraphicalParticleWrapper("Q", "Q", Color.gray, 0.5));
        tmpHashMap.put("R", new GraphicalParticleWrapper("R", "R", Color.gray, 0.5));
        tmpHashMap.put("S", new GraphicalParticleWrapper("S", "S", Color.gray, 0.5));
        tmpHashMap.put("T", new GraphicalParticleWrapper("T", "T", Color.gray, 0.5));
        tmpHashMap.put("V", new GraphicalParticleWrapper("V", "V", Color.gray, 0.5));
        tmpHashMap.put("W", new GraphicalParticleWrapper("W", "W", Color.gray, 0.5));
        tmpHashMap.put("Y", new GraphicalParticleWrapper("Y", "Y", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        tmpStructure = "A-A(B(C)(D-E))-F";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("A", new GraphicalParticleWrapper("A", "A", Color.gray, 0.5));
        tmpHashMap.put("C", new GraphicalParticleWrapper("C", "C", Color.gray, 0.5));
        tmpHashMap.put("D", new GraphicalParticleWrapper("D", "D", Color.gray, 0.5));
        tmpHashMap.put("E", new GraphicalParticleWrapper("E", "E", Color.gray, 0.5));
        tmpHashMap.put("F", new GraphicalParticleWrapper("F", "F", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        tmpStructure
                = "MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetSH[1])-HAc"
                + "-MetNH2(MetSH[2])-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(MetSH[3])-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(MetSH[3])-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(MetSH[2])-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetSH[1])-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("MetNH2", new GraphicalParticleWrapper("MetNH2", "MetNH2", Color.gray, 0.5));
        tmpHashMap.put("MetOH", new GraphicalParticleWrapper("MetOH", "MetOH", Color.gray, 0.5));
        tmpHashMap.put("Phenol", new GraphicalParticleWrapper("Phenol", "Phenol", Color.gray, 0.5));
        tmpHashMap.put("HAc", new GraphicalParticleWrapper("HAc", "HAc", Color.gray, 0.5));
        tmpHashMap.put("MetSH", new GraphicalParticleWrapper("MetSH", "MetSH", Color.gray, 0.5));
        tmpHashMap.put("ProRing", new GraphicalParticleWrapper("ProRing", "ProRing", Color.gray, 0.5));
        tmpHashMap.put("Met", new GraphicalParticleWrapper("Met", "Met", Color.gray, 0.5));
        tmpHashMap.put("Acetamide", new GraphicalParticleWrapper("Acetamide", "Acetamide", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        int tmpLineNumber = 0;
        tmpSpices = new SpicesGraphics(tmpStructure, tmpLineNumber, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        tmpStructure = "MetNH2(Met-HAcSC)-HAcPD1"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(MetSH)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetSH)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(MetSH)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(MetSH)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-ProRing-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAcPD2"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAcPD3"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(MetSH)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetSH)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAcPD4"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(MetSH)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Pyrrole-Benzene)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met-Imidazole)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Acetamide)-HAcPD5"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-MetSH)-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Phenol)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Met-Acetamide)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH)-HAc"
                + "-MetNH2(Met-Acetamide)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Benzene)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(MetOH-Phenol)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-HAcSC)-HAc"
                + "-MetNH2-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met-Met-Guanidine)-HAc"
                + "-MetNH2(Met-Met-HAcSC)-HAc"
                + "-MetNH2(Met(Met)(Met-Met))-HAc"
                + "-MetNH2(Met-Met(Met)(Met))-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-MetNH2(Met-Met-Met-MetNH2)-HAc"
                + "-ProRing-HAc"
                + "-MetNH2(Met(Met)(Met))-HAc";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("MetNH2", new GraphicalParticleWrapper("MetNH2", "MetNH2", Color.gray, 0.5));
        tmpHashMap.put("MetOH", new GraphicalParticleWrapper("MetOH", "MetOH", Color.gray, 0.5));
        tmpHashMap.put("Phenol", new GraphicalParticleWrapper("Phenol", "Phenol", Color.gray, 0.5));
        tmpHashMap.put("HAc", new GraphicalParticleWrapper("HAc", "HAc", Color.gray, 0.5));
        tmpHashMap.put("MetSH", new GraphicalParticleWrapper("MetSH", "MetSH", Color.gray, 0.5));
        tmpHashMap.put("ProRing", new GraphicalParticleWrapper("ProRing", "ProRing", Color.gray, 0.5));
        tmpHashMap.put("Met", new GraphicalParticleWrapper("Met", "Met", Color.gray, 0.5));
        tmpHashMap.put("Acetamide", new GraphicalParticleWrapper("Acetamide", "Acetamide", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        tmpLineNumber = 0;
        tmpSpices = new SpicesGraphics(tmpStructure, tmpLineNumber, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);

        tmpStructure
                = "<MetNH3bb(Met(Met)(Met))-PROBE1"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Pyrrole-C6H6)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-C6H5OH)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-Guanidine)-HCOOH"
                + "-MetNH2bb(Met-Met-MetSH)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-MetSH)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-Met-Guanidine)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(MetSH)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-Guanidine)-PROBE2"
                + ">"
                + "<MetNH3bb(Met(Met)(Met))-PROBE3"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Pyrrole-C6H6)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-Met-Guanidine)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-C6H5OH)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met-Pyrrole-C6H6)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-Guanidine)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-MetSH)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetOH)-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(MetSH)-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-HCOO)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-Guanidine)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(MetSH)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-Met-HCOO)-HCOOH"
                + "-MetNH2bb(Met-C6H6)-HCOOH"
                + "-MetNH2bb(MetOH-C6H5OH)-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-Pyrroline-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met-Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2-HCOOH"
                + "-MetNH2bb(Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Formamide)-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Met(Met)(Met))-HCOOH"
                + "-MetNH2bb(Met)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-HCOOH"
                + "-MetNH2bb(Met-Met-Met-MetNH3)-HCOOH"
                + "-MetNH2bb(Met-C6H5OH)-HCOOH"
                + "-MetNH2bb(Met-Imidazole)-PROBE4"
                + ">";
        tmpSpices = new SpicesGraphics(tmpStructure);
        tmpFirstParticleCoordinates = new GraphicalParticlePosition[1];
        tmpLastParticleCoordinates = new GraphicalParticlePosition[1];
        tmpFirstParticleCoordinates[0] = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        tmpLastParticleCoordinates[0] = new GraphicalParticlePosition(10.0, 10.0, 10.0);
        tmpBondLength = 1.0;
        tmpHashMap = new HashMap<>();
        tmpHashMap.put("Guanidine", new GraphicalParticleWrapper("Guanidine", "Guanidine", Color.gray, 0.5));
        tmpHashMap.put("Imidazole", new GraphicalParticleWrapper("Imidazole", "Imidazole", Color.gray, 0.5));
        tmpHashMap.put("Formamide", new GraphicalParticleWrapper("Formamide", "Formamide", Color.gray, 0.5));
        tmpHashMap.put("MetNH3bb", new GraphicalParticleWrapper("MetNH3bb", "MetNH3bb", Color.gray, 0.5));
        tmpHashMap.put("MetNH3", new GraphicalParticleWrapper("MetNH3", "MetNH3", Color.gray, 0.5));
        tmpHashMap.put("MetNH2", new GraphicalParticleWrapper("MetNH2", "MetNH2", Color.gray, 0.5));
        tmpHashMap.put("MetNH2bb", new GraphicalParticleWrapper("MetNH2bb", "MetNH2bb", Color.gray, 0.5));
        tmpHashMap.put("MetOH", new GraphicalParticleWrapper("MetOH", "MetOH", Color.gray, 0.5));
        tmpHashMap.put("MetSH", new GraphicalParticleWrapper("MetSH", "MetSH", Color.gray, 0.5));
        tmpHashMap.put("C6H6", new GraphicalParticleWrapper("C6H6", "C6H6", Color.gray, 0.5));
        tmpHashMap.put("C6H5OH", new GraphicalParticleWrapper("C6H5OH", "C6H5OH", Color.gray, 0.5));
        tmpHashMap.put("HCOOH", new GraphicalParticleWrapper("HCOOH", "HCOOH", Color.gray, 0.5));
        tmpHashMap.put("MetSH", new GraphicalParticleWrapper("MetSH", "MetSH", Color.gray, 0.5));
        tmpHashMap.put("ProRing", new GraphicalParticleWrapper("ProRing", "ProRing", Color.gray, 0.5));
        tmpHashMap.put("Met", new GraphicalParticleWrapper("Met", "Met", Color.gray, 0.5));
        tmpHashMap.put("Acetamide", new GraphicalParticleWrapper("Acetamide", "Acetamide", Color.gray, 0.5));
        tmpHashMap.put("Pyrrole", new GraphicalParticleWrapper("Pyrrole", "Pyrrole", Color.gray, 0.5));
        tmpHashMap.put("Pyrroline", new GraphicalParticleWrapper("Pyrroline", "Pyrroline", Color.gray, 0.5));
        tmpHashMap.put("HCOO", new GraphicalParticleWrapper("HCOO", "HCOO", Color.gray, 0.5));
        tmpHashMap.put("PROBE1", new GraphicalParticleWrapper("PROBE1", "PROBE1", Color.gray, 0.5));
        tmpHashMap.put("PROBE2", new GraphicalParticleWrapper("PROBE1", "PROBE1", Color.gray, 0.5));
        tmpHashMap.put("PROBE3", new GraphicalParticleWrapper("PROBE1", "PROBE1", Color.gray, 0.5));
        tmpHashMap.put("PROBE4", new GraphicalParticleWrapper("PROBE1", "PROBE1", Color.gray, 0.5));
        tmpResult = tmpSpices.getParticleCoordinates(tmpHashMap, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
        tmpLineNumber = 0;
        tmpSpices = new SpicesGraphics(tmpStructure, tmpLineNumber, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
    }
    
}
