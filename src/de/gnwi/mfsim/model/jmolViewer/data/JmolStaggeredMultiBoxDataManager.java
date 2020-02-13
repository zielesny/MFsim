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
package de.gnwi.mfsim.model.jmolViewer.data;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dBoxController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data manager loading providing data successively.
 *
 * @author Andreas Truszkowski
 */
public class JmolStaggeredMultiBoxDataManager extends JmolDataManager {

    /**
     * Buffer size.
     */
    private static final int BUFFER_SIZE = 8196;
    /**
     * Next simulation step to show.
     */
    private Integer showStep = 0;

    /**
     * Creates a new instance.
     * @param aDataProvider Data provider.
     * @param anOwner Jmol viewer controller.
     */
    public JmolStaggeredMultiBoxDataManager(IDataProvider aDataProvider, Jmol3dBoxController anOwner) {
        super(aDataProvider, anOwner);
    }

    @Override
    protected void convertData() {
        while (!this.isInterrupted()) {
            while (this.showStep != null) {
                try {
                    Object tmpWork = this.dataProvider.getNextWork(this.showStep);
                    this.showStep = null;
                    String tmpXyzData = super.convertDpdDataToXyz(tmpWork);
                    this.setStructure(tmpXyzData, this.restoreOrientation);
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    Logger.getLogger(JmolStaggeredMultiBoxDataManager.class.getName()).log(Level.SEVERE, null, anException);
                }
            }
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException anException) {
                ModelUtils.appendToLogfile(true, anException);
                Logger.getLogger(JmolStaggeredMultiBoxDataManager.class.getName()).log(Level.SEVERE, null, anException);
            }
        }
    }

    @Override
    public void setSimulationStep(int aStepIndex) throws IOException {
        this.showStep = aStepIndex;
        this.restoreOrientation = true;
        synchronized (this) {
            this.notify();
        }
    }
}
