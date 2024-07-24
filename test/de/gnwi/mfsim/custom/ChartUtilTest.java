package de.gnwi.mfsim.custom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for class ChartUtil
 * 
 * @author Achim Zielesny
 */
public class ChartUtilTest {
    
    public ChartUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createEnergyGraphics method, of class ChartUtil.
     */
    @Test
    public void testCreateEnergyGraphics() {
        String aEnergyDataPathName = "P:\\MFsim\\Code\\MFsim\\test\\de\\gnwi\\mfsim\\custom\\HAc_HAc_dist_vs_energy.dat";
        String anEnergyGraphicsPrefix = "HAc_HAc_";
        ChartUtil tmpChartUtil = new ChartUtil();
        boolean tmpIsOperationSuccessful = 
            tmpChartUtil.createEnergyGraphics(
                aEnergyDataPathName, 
                anEnergyGraphicsPrefix
            );
        assertTrue(tmpIsOperationSuccessful);
    }
    
}
