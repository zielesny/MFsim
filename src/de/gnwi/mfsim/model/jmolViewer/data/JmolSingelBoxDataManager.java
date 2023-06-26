/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
package de.gnwi.mfsim.model.jmolViewer.data;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dBoxController;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Single box data manager.
 *
 * @author Andreas Truszkowski
 */
public class JmolSingelBoxDataManager extends JmolDataManager {

    /**
     * Creates a new instance.
     * @param aDataProvider Data provider. Adapter managing the data interface.
     * @param anOwner Jmol viewer controller.
     */
    public JmolSingelBoxDataManager(IDataProvider aDataProvider, Jmol3dBoxController anOwner) {
        super(aDataProvider, anOwner);
    }

    @Override
    protected void convertData() {
        try {
            String tmpXyzData = super.convertDpdDataToXyz(this.dataProvider.getNextWork(0));
            this.setStructure(tmpXyzData, this.restoreOrientation);              
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            Logger.getLogger(JmolSingelBoxDataManager.class.getName()).log(Level.SEVERE, null, anException);
        }
    }

    @Override
    public void setSimulationStep(int aStepIndex) throws IOException {
    }
    
    
}
