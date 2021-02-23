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
package de.gnwi.mfsim.model.jmolViewer.data;

import de.gnwi.mfsim.model.jmolViewer.setting.JmolSettings;
import java.io.*;
import java.util.UUID;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticlePosition;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dBoxController;

/**
 * Abstract manager class conducting DPD data for the Jmol viewer.
 *
 * @author Andreas Truszkowski
 */
public abstract class JmolDataManager extends Thread {

    /**
     * The finally executed script.
     */
    private String finalScript = null;
    /**
     * Jmol viewer controller.
     */
    protected Jmol3dBoxController owner = null;
    /**
     * Data provider. Adapter managing the data interface.
     */
    protected IDataProvider dataProvider = null;
    /**
     * Currently processed simulation step.
     */
    private int stepNumber = 1;
    /**
     * Currently displayed structure data.
     */
    private String currentDataScript = null;
    /**
     * UUID used to identify current set particle data.
     */
    private UUID currentDataScriptUUID = null;
    /**
     * True if structure orientation shall be restored.
     */
    protected boolean restoreOrientation = false;

    /**
     * Creates a new instance.
     *
     * @param aDataProvider Data provider.
     * @param anOwner Jmol viewer controller.
     */
    public JmolDataManager(IDataProvider aDataProvider, Jmol3dBoxController anOwner) {
        this.owner = anOwner;
        this.dataProvider = aDataProvider;
    }

    @Override
    public void run() {
        this.convertData();
    }

    /**
     * Converts given data (GraphicalParticlePosition, InputStream) into the XYZ
     * format.
     *
     * @param aDpdData DPD data object.
     * @return Converted data
     * @throws FileNotFoundException FileNotFoundException
     * @throws IOException IOException
     */
    protected String convertDpdDataToXyz(Object aDpdData)
            throws FileNotFoundException, IOException {
        if (aDpdData instanceof IGraphicalParticlePosition[]) {
            return this.convertDpdDataToXyz((IGraphicalParticlePosition[]) aDpdData);
        }
        if (aDpdData instanceof InputStream) {
            return this.convertDpdDataToXyz((InputStream) aDpdData);
        }
        return null;
    }

    /**
     * Converts given dGraphicalParticlePosition data into the XYZ format.
     *
     * @param aDpdData GraphicalParticlePositionInterface data object.
     * @return Converted data.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String convertDpdDataToXyz(IGraphicalParticlePosition[] aDpdData)
            throws FileNotFoundException, IOException {
        StringBuilder tmpXyzDataBuilder = new StringBuilder();
        JmolSettings tmpSettings = JmolSettings.getInstance();
        tmpXyzDataBuilder.append(aDpdData.length);
        tmpXyzDataBuilder.append("\n");
        tmpXyzDataBuilder.append("Step ").append(this.stepNumber);
        tmpXyzDataBuilder.append("\n");
        for (IGraphicalParticlePosition tmpParticlePosition : aDpdData) {
            String tmpParticleID = ((GraphicalParticle) tmpParticlePosition.getGraphicalParticle()).getMoleculeParticleString();
            tmpXyzDataBuilder.append(tmpSettings.getMoleculeParticleElementMap().get(tmpParticleID));
            tmpXyzDataBuilder.append(" ");
            tmpXyzDataBuilder.append(tmpParticlePosition.getX()).append(" ");
            tmpXyzDataBuilder.append(tmpParticlePosition.getY()).append(" ");
            tmpXyzDataBuilder.append(tmpParticlePosition.getZ()).append(" ");
            tmpXyzDataBuilder.append("\n");
        }
        this.stepNumber++;
        return tmpXyzDataBuilder.toString();
    }

    /**
     * Converts given InputStream into the XYZ format. Before executing this
 method GraphicalParticle data has to be set in the Settings object .
     *
     * @param aDataStream GraphicalParticlePositionInterface data object.
     * @return Converted data.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String convertDpdDataToXyz(InputStream aDataStream)
            throws FileNotFoundException, IOException {
        JmolSettings tmpSettings = JmolSettings.getInstance();
        LineNumberReader tmpReader = new LineNumberReader(new InputStreamReader(aDataStream));
        // Skipt first line;
        tmpReader.readLine();
        String tmpLine;
        int tmpTotalNumberOfParticles = 0;
        StringBuilder tmpXyzDataBuilder = new StringBuilder();
        tmpXyzDataBuilder.append("Frame ");
        tmpXyzDataBuilder.append(this.stepNumber);
        tmpXyzDataBuilder.append("\n");
        while ((tmpLine = tmpReader.readLine()) != null) {
            // First line: Molecule name
            String tmpMoleculeName = tmpLine;
            // Second line: Particle name
            String tmpParticleName = tmpReader.readLine();
            String tmpParticleID = String.format("%s_%s", tmpMoleculeName, tmpParticleName);
            // Third line: Number of particles
            int tmpNumberOfParticles = Integer.parseInt(tmpReader.readLine());
            for (int i = 0; i < tmpNumberOfParticles; i++) {
                tmpXyzDataBuilder.append(tmpSettings.getMoleculeParticleElementMap().get(tmpParticleID));
                tmpXyzDataBuilder.append(" ");
                for (int j = 0; j < 3; j++) {
                    tmpXyzDataBuilder.append(tmpReader.readLine());
                    tmpXyzDataBuilder.append(" ");
                }
                tmpXyzDataBuilder.append("\n");
            }
            tmpTotalNumberOfParticles += tmpNumberOfParticles;
        }
        tmpReader.close();
        tmpXyzDataBuilder.insert(0, "\n");
        tmpXyzDataBuilder.insert(0, tmpTotalNumberOfParticles);
        this.stepNumber++;
        return tmpXyzDataBuilder.toString();
    }

    /**
     * Display structure (simualtion box) in the Jmol viewer.
     *
     * @param aXyzData Jmol data script.
     */
    protected void setStructure(String aXyzData) {
        this.setStructure(aXyzData, false);
    }

    /**
     * Display structure (simualtion box) in the Jmol viewer.
     *
     * @param aXyzData Jmol data script.
     * @param aRememberOrienation True if current structure orientation shall be
     * remembered
     */
    protected void setStructure(String aXyzData, boolean aRememberOrienation) {
        JmolSettings tmpSettings = JmolSettings.getInstance();
        this.currentDataScript = this.generateStructureScript(aXyzData);
        this.currentDataScriptUUID = UUID.randomUUID();
        StringBuilder tmpStructureScript = new StringBuilder();
        tmpStructureScript.append("set autobond OFF;set refreshing false;");
        if (aRememberOrienation) {
            tmpStructureScript.append("save orientation tmpOrientation;");
        }
        tmpStructureScript.append(this.currentDataScript);
        if (aRememberOrienation) {
            tmpStructureScript.append("restore orientation tmpOrientation;");
        }
        tmpStructureScript.append(tmpSettings.getParticleSettingScript());
        tmpStructureScript.append(tmpSettings.getSlabSettingsScript());
        if (this.finalScript != null) {
            tmpStructureScript.append(String.format("%s;", this.finalScript));
        }
        tmpStructureScript.append("set refreshing true;hover off;");
        this.owner.executeScriptWait(tmpStructureScript.toString());
    }

    private String generateStructureScript(String aXyzData) {
        StringBuilder tmpDataScript = new StringBuilder();
        tmpDataScript.append("load DATA \"model data\"\n");
        tmpDataScript.append(aXyzData);
        tmpDataScript.append("end \"model data\"\n");
        return tmpDataScript.toString();
    }

    /**
     * Gets the data script of currently set scene.
     *
     * @return Data script
     * @throws Exception Exception
     */
    public String getStructureScript() throws Exception {
        return this.getStructureScript(this.stepNumber);
    }

    /**
     * Gets the UUID of current set scene data.
     *
     * @return Data UUID.
     */
    public UUID getStructureScriptUUID() {
        return this.currentDataScriptUUID;
    }

    /**
     * Gets the structure script for given step.
     *
     * @param aStep Simualtion step
     * @return XYZ data script
     * @throws Exception Exception
     */
    public String getStructureScript(int aStep) throws Exception {
        Object tmpWork = this.dataProvider.getNextWork(aStep);
        String tmpXyzData = this.convertDpdDataToXyz(tmpWork);
        return this.generateStructureScript(tmpXyzData);
    }

    /**
     * Convert data method. Will be executed if the thread has been started.
     */
    protected abstract void convertData();

    /**
     * Displays given simulation step in the Jmol viewer.
     *
     * @param aStepIndex Simulation step index
     * @throws IOException IOException
     */
    public abstract void setSimulationStep(int aStepIndex) throws IOException;

    /**
     * Sets the finally executed script.
     *
     * @param aFinalScript The finally executed script.
     */
    public void setFinalScript(String aFinalScript) {
        this.finalScript = aFinalScript;
    }

    /**
     * Gets the current data provider.
     *
     * @return Data provider.
     */
    public IDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets whether to restore orientation.
     *
     * @param restoreOrientation True when orientation shall be resored.
     */
    public void setRestoreOrientation(boolean restoreOrientation) {
        this.restoreOrientation = restoreOrientation;
    }

}
