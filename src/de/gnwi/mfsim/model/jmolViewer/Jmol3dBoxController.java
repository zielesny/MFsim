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
package de.gnwi.mfsim.model.jmolViewer;

import de.gnwi.mfsim.model.jmolViewer.data.JmolSingelBoxDataManager;
import de.gnwi.mfsim.model.jmolViewer.data.IDataProvider;
import de.gnwi.mfsim.model.jmolViewer.data.JmolStaggeredMultiBoxDataManager;
import de.gnwi.mfsim.model.jmolViewer.data.JmolDpdResultFileDataProvider;
import de.gnwi.mfsim.model.jmolViewer.data.JmolSingleBoxDpdDataProvider;
import de.gnwi.mfsim.model.jmolViewer.data.JmolDataManager;
import de.gnwi.mfsim.model.jmolViewer.command.JmolDpdBoxCommands;
import de.gnwi.mfsim.model.jmolViewer.base.Jmol3dController;
import de.gnwi.mfsim.model.jmolViewer.setting.JmolGraphicsSettings;
import de.gnwi.mfsim.model.jmolViewer.setting.JmolSettings;
import de.gnwi.mfsim.model.jmolViewer.setting.JmolSlabSettings;
import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import de.gnwi.mfsim.model.peptide.utils.PdbToDpdMasterdata;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipException;
import org.jmol.api.JmolViewer;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticlePosition;

/**
 * Controller class for the DpdJmolViewer.
 *
 * @author Andreas Truszkowski
 */
public class Jmol3dBoxController extends Jmol3dController {

    /**
     * ID of the slab changed event.
     */
    public static final String SLAB_CHANGED = "SLAB_CHANGED";
    /**
     * Datamanager managing incoming DPD data.
     */
    private JmolDataManager dataManager = null;
    /**
     * Currently set structure data UUID of the renderer.
     */
    private UUID currentSetRenderDataUUID = null;

    /**
     * Creates a new instance.
     *
     * @param aView Jmol viewer view.
     */
    public Jmol3dBoxController(Jmol3dPanel aView) {
        super(aView);
    }

    /**
     * Creates a new instance.
     */
    public Jmol3dBoxController() {
        this(new Jmol3dPanel());
    }

    /**
     * Gets an image of the current scene state with given slab settings. The
     * size is equal to the rendering panel.
     *
     * @param aSlabSettings Slab settings
     * @return Image of the scene
     * @throws Exception Exception
     */
    public BufferedImage getSlabImageQuiet(JmolSlabSettings aSlabSettings) throws Exception {
        Dimension tmpSize = this.view.getJmolPanel().getSize();
        return this.getSlabImageQuiet(aSlabSettings, (int) tmpSize.getWidth(), (int) tmpSize.getHeight());
    }

    /**
     * Gets an image of the current scene state with given slab settings.
     *
     * @param aSlabSettings Slab settings
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @return Image of the scene.
     * @throws Exception Exception
     */
    public BufferedImage getSlabImageQuiet(JmolSlabSettings aSlabSettings, int aWidth, int aHeight) throws Exception {
        String tmpStructureScript = this.dataManager.getStructureScript();
        if (tmpStructureScript == null) {
            return null;
        }
        UUID tmpStructureScriptUUID = this.dataManager.getStructureScriptUUID();
        StringBuilder tmpScript = new StringBuilder();
        if (this.currentSetRenderDataUUID == null || !tmpStructureScriptUUID.equals(this.currentSetRenderDataUUID)) {
            this.currentSetRenderDataUUID = tmpStructureScriptUUID;
            String tmpOrientationScript = this.getOrientationScript();
            JmolSettings tmpSettings = JmolSettings.getInstance();
            // Create data script          
            tmpScript.append("set autobond OFF;set refreshing false;");
            tmpScript.append(tmpStructureScript);
            tmpScript.append(tmpOrientationScript);
            tmpScript.append(tmpSettings.getParticleSettingScript());
            tmpScript.append(tmpSettings.getGraphicsSettingsScript());
            tmpScript.append(aSlabSettings.toScript());
            tmpScript.append("set refreshing true;");
        } else {
            tmpScript.append(aSlabSettings.toScript());
        }
        JmolViewer tmpRenderer = this.view.getJmolRenderer();
        tmpRenderer.scriptWait(tmpScript.toString());
        BufferedImage tmpImage = new BufferedImage(aWidth, aHeight, BufferedImage.TYPE_INT_RGB);
        tmpRenderer.renderScreenImage(tmpImage.getGraphics(), aWidth, aHeight);
        return tmpImage;
    }

    /**
     * Renders given simulation step in the background.
     *
     * @param aStep Simulation step to render. (0,1,2,...)
     * @param aWidth Width of the image.
     * @param aHeight Height of the image.
     * @return Box image of given simulation step.
     * @throws Exception Exception
     */
    public BufferedImage getStepImageQuiet(int aStep, int aWidth, int aHeight) throws Exception {
        String tmpStructureScript = this.dataManager.getStructureScript(aStep);
        StringBuilder tmpScript = new StringBuilder();
        String tmpOrientationScript = this.getOrientationScript();
        JmolSettings tmpSettings = JmolSettings.getInstance();
        // Create data script          
        tmpScript.append("set autobond OFF;set refreshing false;");
        tmpScript.append(tmpStructureScript);
        tmpScript.append(tmpOrientationScript);
        tmpScript.append(tmpSettings.getParticleSettingScript());
        tmpScript.append(tmpSettings.getGraphicsSettingsScript());
        tmpScript.append(tmpSettings.getSlabSettingsScript());
        tmpScript.append("set refreshing true;");

        //tmpScript.append("frank off;background white; set zShade OFF; boundbox 3; set showBoundBox true;moveto 0 bottom;");
        // Render Image
        JmolViewer tmpRenderer = this.view.getJmolRenderer();
        tmpRenderer.scriptWait(tmpScript.toString());
        BufferedImage tmpImage = new BufferedImage(aWidth, aHeight, BufferedImage.TYPE_INT_RGB);
        tmpRenderer.renderScreenImage(tmpImage.getGraphics(), aWidth, aHeight);
        return tmpImage;
    }

    /**
     * Displays DPD data for a single simulation box.
     *
     * @param aData DPD data.
     */
    public void showSingleStepBox(IGraphicalParticlePosition[] aData) {
        this.showSingleStepBox(aData, JmolDpdBoxCommands.MoveToXZFront.getScript());
    }

    /**
     * Displays DPD data for a single simulation box.
     *
     * @param aData DPD data.
     * @param aFinalScript Script which is directly executed before the
     * simulation box becomes visible.
     */
    public void showSingleStepBox(IGraphicalParticlePosition[] aData, String aFinalScript) {
        JmolSettings.getInstance().resetParticleData();
        JmolSettings.getInstance().setGraphicalParticles(aData);
        JmolSettings.getInstance().configureColors();
        if (aData == null) {
            this.executeCommand(JmolDpdBoxCommands.ResetAll);
        } else {
            this.dataManager = new JmolSingelBoxDataManager(new JmolSingleBoxDpdDataProvider(aData), this);
            this.dataManager.setFinalScript(aFinalScript);
            this.dataManager.start();
        }
    }

    /**
     * Displays DPD data for a single simulation box.
     *
     * @param aData DPD data.
     * @param aMasterdata Script which is directly executed before the
     * simulation box becomes visible.
     * @throws DpdPeptideException DpdPeptideException
     */
    public void showSingleStepBox(IGraphicalParticlePosition[] aData, PdbToDpdMasterdata aMasterdata) throws DpdPeptideException {
        if (aMasterdata.getRotationScript() == null) {
            throw DpdPeptideException.MissingData("rotation");
        }
        this.showSingleStepBox(aData, aMasterdata.getRotationScript());
    }

    /**
     * Displays DPD data for a multi step simulation. As source serves a DPD
     * result Zip file.
     *
     * @param aDpdFile Dpd result Zip file. All Data from the "Steps" folder
     * will be visualised.
     * @param aGraphicalParticles Graphical particle data.
     * @throws ZipException ZipException
     * @throws IOException IOException
     */
    public void showMultiStepBox(File aDpdFile, IGraphicalParticle[] aGraphicalParticles) throws ZipException, IOException {
        JmolSettings.getInstance().resetParticleData();
        JmolSettings.getInstance().setGraphicalParticles(aGraphicalParticles);
        JmolSettings.getInstance().configureColors();
        this.dataManager = new JmolStaggeredMultiBoxDataManager(new JmolDpdResultFileDataProvider(aDpdFile), this);
        this.dataManager.start();
    }

    /**
     * Sets graphic settings to configure the Jmol particle presentation.
     *
     * @param aGraphicSettings Graphic settings.
     */
    public void setGraphicSettings(JmolGraphicsSettings aGraphicSettings) {
        JmolSettings tmpSettings = JmolSettings.getInstance();
        tmpSettings.setGraphicSettings(aGraphicSettings);
        this.executeScriptWait(tmpSettings.getGraphicsSettingsScript());
    }

    /**
     * Sets the percentage of the particle box to be displayed.
     *
     * @param aSlabSettings Slab: Range 0 - 100; zShadePower: Range 1 - 3
     */
    public void setSlabSettings(JmolSlabSettings aSlabSettings) {
        JmolSettings tmpSettings = JmolSettings.getInstance();
        tmpSettings.setSlabSettings(aSlabSettings);
        this.executeScriptWait(tmpSettings.getSlabSettingsScript());
    }

    /**
     * Sets graphical particles data representing particle properties like
     * color, radius, visibility, ...
     *
     * @param aGraphicalParticles Graphical particle data
     * @throws Exception Exception
     */
    public void setGraphicalParticles(IGraphicalParticle[] aGraphicalParticles) throws Exception {
        JmolSettings tmpSettings = JmolSettings.getInstance();
        tmpSettings.resetParticleData();
        tmpSettings.setGraphicalParticles(aGraphicalParticles);
        tmpSettings.configureColors();
        IDataProvider tmpDataprovider = this.dataManager.getDataProvider();
        this.dataManager = this.dataManager.getClass().getConstructor(IDataProvider.class, Jmol3dBoxController.class)
                .newInstance(tmpDataprovider, this);
        this.dataManager.setRestoreOrientation(true);
        this.dataManager.start();
    }

    /**
     * Sets the simulation step to display.
     *
     * @param aStep Simulation step to display
     * @throws IOException IOException
     */
    public void setSimulationStep(int aStep) throws IOException {
        this.dataManager.setSimulationStep(aStep);
    }

    @Override
    protected void handleMouseEvent(MouseEvent e) {
        // Suppress mouse double-click for possible measurement
        if (e.getClickCount() == 2) {
            e.consume();
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            e.consume();
        }
        if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
            e.consume();
        }
    }

    @Override
    protected void handleMouseWheelEvent(MouseWheelEvent e) {
        if (!e.isShiftDown()) {
            JmolSettings tmpSettings = JmolSettings.getInstance();
            JmolSlabSettings tmpSlabSettings = tmpSettings.getSlabSettings();
            int tmpSlab = tmpSlabSettings.getSlab();
            int tmpNewSlab = tmpSlab - e.getWheelRotation();
            tmpNewSlab = tmpNewSlab < 0 ? 0 : tmpNewSlab;
            tmpNewSlab = tmpNewSlab > 100 ? 100 : tmpNewSlab;
            tmpSlabSettings.setSlab(tmpNewSlab);
            tmpSettings.setSlabSettings(tmpSlabSettings);
            this.executeScript(tmpSettings.getSlabSettingsScript());
            if (tmpSlab != tmpNewSlab) {
                this.firePropertyChangeEvent(new PropertyChangeEvent(this, Jmol3dBoxController.SLAB_CHANGED,
                        tmpSlab, tmpNewSlab));
            }
            e.consume();
        }
    }
}
