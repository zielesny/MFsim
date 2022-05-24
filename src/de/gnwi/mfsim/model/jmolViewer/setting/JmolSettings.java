/**
 * MFsim - Molecular Fragment DPD Simulation Environment
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
package de.gnwi.mfsim.model.jmolViewer.setting;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.jmol.util.Elements;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticlePosition;

/**
 * Singleton class holding the Jmolviewer settings.
 *
 * @author Andreas Truszkowski
 */
public class JmolSettings {

    /**
     * The settings instance.
     */
    private static JmolSettings instance = null;
    /**
     * Grpahical particle settings.
     */
    private HashMap<String, IGraphicalParticle> graphicalParticles = null;

    /**
     * Molecule-particle element map.
     */
    private HashMap<String, String> moleculeParticleElementMap = null;
    /**
     * Scene Graphic settings.
     */
    private JmolGraphicsSettings graphicSettings = null;
    /**
     * Slab settings.
     */
    private JmolSlabSettings slabSettings = null;

    /**
     * Creates a new instance.
     */
    private JmolSettings() {
        this.moleculeParticleElementMap = new HashMap<String, String>();
        this.graphicalParticles = new HashMap<String, IGraphicalParticle>();
        this.graphicSettings = new JmolGraphicsSettings();
        this.slabSettings = new JmolSlabSettings();
    }

    /**
     * Gets a settings instance.
     *
     * @return Settings object.
     */
    public static synchronized JmolSettings getInstance() {
        if (instance == null) {
            instance = new JmolSettings();
        }
        return instance;
    }

    /**
     * Set graphical particle settings.
     *
     * @param aGraphicalParticles GraphicalParticle settings.
     */
    public void setGraphicalParticles(IGraphicalParticle[] aGraphicalParticles) {
        for (IGraphicalParticle tmpGraphicalParticle : aGraphicalParticles) {
            this.setGraphicalParticle(tmpGraphicalParticle);
        }
    }

    /**
     * Set graphical particle settings.
     *
     * @param aGraphicalParticlesPositions Graphical particle settings.
     */
    public void setGraphicalParticles(IGraphicalParticlePosition[] aGraphicalParticlesPositions) {
        for (IGraphicalParticlePosition tmpGraphicalParticlePosition : aGraphicalParticlesPositions) {
            this.setGraphicalParticle(tmpGraphicalParticlePosition.getGraphicalParticle());
        }
    }

    /**
     * Sets a graphical particle setting.
     *
     * @param aGraphicalParticle Graphical particle setting.
     */
    public void setGraphicalParticle(IGraphicalParticle aGraphicalParticle) {
        
        this.graphicalParticles.put(((GraphicalParticle) aGraphicalParticle).getMoleculeParticleString(), aGraphicalParticle);
    }

    /**
     * Gets all graphical particle settings mapped with the molecule-particle
     * name.
     *
     * @return HashMap for graphical particles
     */
    public HashMap<String, IGraphicalParticle> getGraphicalParticles() {
        return graphicalParticles;
    }

    /**
     * Configures the Java.Awt.Color to Jmol element symbol mapping.
     */
    public void configureColors() {
        int tmpFakeElementID = 1;
        HashMap<String, String> tmpColorElementMap = new HashMap<String, String>();
        for (Map.Entry<String, IGraphicalParticle> tmpEntry : this.graphicalParticles.entrySet()) {
            GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpEntry.getValue();
            Color tmpColor = tmpGraphicalParticle.getCurrentParticleColor();
            boolean tmpVisible = tmpGraphicalParticle.isVisible();
            double tmpRadius = tmpGraphicalParticle.getCurrentParticleRadius();
            String tmpColorID = String.format("%d_%d_%d_%b_%.2f", tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue(), tmpVisible, tmpRadius);
            String tmpParticleID = tmpGraphicalParticle.getMoleculeParticleString();
            String tmpFakeElement = tmpColorElementMap.get(tmpColorID);
            if (tmpFakeElement == null) {
                tmpFakeElement = Elements.elementSymbolFromNumber(tmpFakeElementID);
                tmpFakeElementID++;
                tmpColorElementMap.put(tmpColorID, tmpFakeElement);
            }
            this.moleculeParticleElementMap.put(tmpParticleID, tmpFakeElement);
        }
    }

    /**
     * Gets a script configuring the particles.
     *
     * @return Jmol particle setting script.
     */
    public String getParticleSettingScript() {
        StringBuilder tmpParticleSettingsScript = new StringBuilder();
        HashSet<String> tmpVisibleElements = new HashSet<String>();
        for (Map.Entry<String, IGraphicalParticle> tmpSettingEntry : this.graphicalParticles.entrySet()) {
            String tmpParticleID = tmpSettingEntry.getKey();
            GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpSettingEntry.getValue();
            String tmpElement = this.moleculeParticleElementMap.get(tmpParticleID);
            Color tmpColor = tmpGraphicalParticle.getCurrentParticleColor();
            if (tmpGraphicalParticle.isVisible()) {
                tmpVisibleElements.add(tmpElement);
            }
            String tmpColorSetting = "color _" + tmpElement + " [" + tmpColor.getRed() + "," + tmpColor.getGreen() + "," + tmpColor.getBlue() + "];";
            tmpParticleSettingsScript.append(tmpColorSetting);
            String tmpRadiusSetting = "{_" + tmpElement + "}.radius = " + tmpGraphicalParticle.getCurrentParticleRadius() + ";";
            tmpParticleSettingsScript.append(tmpRadiusSetting);
        }
        tmpParticleSettingsScript.append("display ");
        boolean tmpFirst = true;
        for (String tmpElement : tmpVisibleElements) {
            if (!tmpFirst) {
                tmpParticleSettingsScript.append(" OR ");
            } else {
                tmpFirst = false;
            }
            tmpParticleSettingsScript.append("_").append(tmpElement);
        }
        tmpParticleSettingsScript.append(";");
        return tmpParticleSettingsScript.toString();
    }

    /**
     * Gets the molecule-particle element map.
     *
     * @return Molecule-particle element map.
     */
    public HashMap<String, String> getMoleculeParticleElementMap() {
        return this.moleculeParticleElementMap;
    }

    /**
     * Sets the scene graphic settings.
     *
     * @param graphicSettings Graphic settings.
     */
    public void setGraphicSettings(JmolGraphicsSettings graphicSettings) {
        this.graphicSettings = graphicSettings;
    }

    /**
     * Gets the scene graphic setting Jmol script.
     *
     * @return Jmol graphic setting script.
     */
    public String getGraphicsSettingsScript() {
        StringBuilder tmpGraphicsScript = new StringBuilder();
        tmpGraphicsScript.append("set ambientPercent ").append(this.graphicSettings.getAmbientPercent()).append(";");
        tmpGraphicsScript.append("set diffusePercent ").append(this.graphicSettings.getDiffusePercent()).append(";");
        //   tmpGraphicsScript.append("set phongExponent ").append(this.graphicSettings.getPhongExponent()).append(";");
        tmpGraphicsScript.append("set specularExponent ").append(this.graphicSettings.getSpecularExponent()).append(";");
        tmpGraphicsScript.append("set specularPercent ").append(this.graphicSettings.getSpecularPercent()).append(";");
        tmpGraphicsScript.append("set specularPower ").append(this.graphicSettings.getSpecularPower()).append(";");
        Color backgroundColor = this.graphicSettings.getBackgroundColor();
        tmpGraphicsScript.append(String.format("background [%d,%d,%d];", backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue()));
        return tmpGraphicsScript.toString();
    }

    /**
     * Sets the slab settings.
     *
     * @param slabSettings Slab settings.
     */
    public void setSlabSettings(JmolSlabSettings slabSettings) {
        this.slabSettings = slabSettings;
    }

    /**
     * Gets the slab settings.
     *
     * @return Slab settings.
     */
    public JmolSlabSettings getSlabSettings() {
        return slabSettings;
    }

    /**
     * Gets the slab settings Jmol script.
     *
     * @return Slab settings Jmol script.
     */
    public String getSlabSettingsScript() {
        return this.slabSettings.toScript();
    }

    /**
     * Resets the particle cache data.
     */
    public synchronized void resetParticleData() {
        this.moleculeParticleElementMap = new HashMap<String, String>();
        this.graphicalParticles = new HashMap<String, IGraphicalParticle>();
    }
}
