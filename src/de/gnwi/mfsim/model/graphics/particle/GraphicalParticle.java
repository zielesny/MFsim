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
package de.gnwi.mfsim.model.graphics.particle;

import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import java.awt.Color;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Graphical particle for display in simulation box
 *
 * @author Stefan Neumann, Achim Zielesny
 *
 */
public class GraphicalParticle implements IGraphicalParticle {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Particle
     */
    private String particle;

    /**
     * The name of the particle
     */
    private String particleName;

    /**
     * The color of the particle
     */
    private Color particleColor;

    /**
     * The current color of the particle
     */
    private Color currentParticleColor;

    /**
     * The radius of the particle
     */
    private double particleRadius;

    /**
     * The current radius of the particle
     */
    private double currentParticleRadius;

    /**
     * The current transparency of the particle
     */
    private float currentParticleTransparency;

    /**
     * The particleName of the molecule the particle belongs to
     */
    private String moleculeName;

    /**
     * The color of the molecule the particle belongs to
     */
    private Color moleculeColor;

    /**
     * Selection color
     */
    private StandardColorEnum selectionColor;

    /**
     * Selection info string
     */
    private String selectionInfoString;

    /**
     * The color of molecule particle
     */
    private Color moleculeParticleColor;

    /**
     * The graphics object of the graphical particle/sphere
     */
    private Object graphicsObject;

    /**
     * The radius of the graphical particle/sphere in pixel
     */
    private int radiusInPixel;

    /**
     * Molecule name plus particle string for comparison
     */
    private String moleculeParticleString;

    /**
     * Frequency of particle in specified molecule
     */
    private int moleculeParticleFrequency;

    /**
     * True: Molecule particle is visible, false: Otherwise
     */
    private boolean isVisible;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor. NOTE: No checks are performed
     *
     * @param aParticle Particle
     * @param aParticleName The name of the particle
     * @param aParticleColor The color of the particle
     * @param aParticleRadius The radius of the particle
     */
    public GraphicalParticle(
        String aParticle, 
        String aParticleName, 
        Color aParticleColor, 
        double aParticleRadius
    ) {
        this.initialize();
        this.particle = aParticle;
        this.particleName = aParticleName;

        this.particleColor = aParticleColor;
        this.currentParticleColor = aParticleColor;

        this.particleRadius = aParticleRadius;
        this.currentParticleRadius = aParticleRadius;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private constructor">
    /**
     * Constructor
     *
     * @param aParticle Particle
     * @param aParticleName The name of the particle
     * @param aParticleColor The color of the particle
     * @param aCurrentParticleColor The current color of the particle
     * @param aParticleRadius The radius of the particle
     * @param aCurrentParticleRadius Current radius of the particle
     * @param aCurrentParticleTransparency Current transparency of the particle
     * @param aMoleculeName The particleName of the molecule the particle
     * belongs to
     * @param aMoleculeParticleString Molecule name plus particle string for
     * comparison
     * @param aMoleculeParticleFrequency Frequency of particle in specified
     * molecule
     * @param aMoleculeColor The color of the molecule the particle belongs to
     * @param aSelectionColor Selection color
     * @param aSelectionInfoString Selection info string
     * @param aGraphicsObject The graphics object of the graphical
     * particle/sphere
     * @param aRadiusInPixel The radius of the graphical particle/sphere in
     * pixel
     * @param aMoleculeParticleColor The color of molecule particle
     */
    private GraphicalParticle(
        String aParticle, 
        String aParticleName, 
        Color aParticleColor, 
        Color aCurrentParticleColor, 
        double aParticleRadius, 
        double aCurrentParticleRadius, 
        float aCurrentParticleTransparency,
        String aMoleculeName, 
        String aMoleculeParticleString, 
        int aMoleculeParticleFrequency, 
        Color aMoleculeColor,
        StandardColorEnum aSelectionColor,
        String aSelectionInfoString,
        Object aGraphicsObject, 
        int aRadiusInPixel, 
        Color aMoleculeParticleColor, 
        boolean anIsVisible
    ) {
        this.initialize();
        this.particle = aParticle;
        this.particleName = aParticleName;
        this.particleColor = aParticleColor;
        this.currentParticleColor = aCurrentParticleColor;
        this.particleRadius = aParticleRadius;
        this.currentParticleRadius = aCurrentParticleRadius;
        this.currentParticleTransparency = aCurrentParticleTransparency;
        this.moleculeName = aMoleculeName;
        this.moleculeParticleString = aMoleculeParticleString;
        this.moleculeParticleFrequency = aMoleculeParticleFrequency;
        this.moleculeColor = aMoleculeColor;
        this.selectionColor = aSelectionColor;
        this.selectionInfoString = aSelectionInfoString;
        this.graphicsObject = aGraphicsObject;
        this.radiusInPixel = aRadiusInPixel;
        this.moleculeParticleColor = aMoleculeParticleColor;
        this.isVisible = anIsVisible;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Clones this object. NOTE: All internal class variables are NOT cloned but
     * reused for new instance.
     *
     * @return Cloned instance. NOTE: All internal class variables are NOT
     * cloned but reused for new instance.
     */
    public GraphicalParticle getClone() {
        return new GraphicalParticle(
            this.particle, 
            this.particleName, 
            this.particleColor, 
            this.currentParticleColor, 
            this.particleRadius, 
            this.currentParticleRadius, 
            this.currentParticleTransparency, 
            this.moleculeName,
            this.moleculeParticleString, 
            this.moleculeParticleFrequency, 
            this.moleculeColor, 
            this.selectionColor,
            this.selectionInfoString,
            this.graphicsObject, 
            this.radiusInPixel, 
            this.moleculeParticleColor, 
            this.isVisible
        );
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- MoleculeName">
    /**
     * The name of the molecule the particle belongs to
     *
     * @return The name of the molecule the particle belongs to
     */
    public String getMoleculeName() {
        return this.moleculeName;
    }

    /**
     * The name of the molecule the particle belongs to. NOTE: No checks are
     * performed!
     *
     * @param aMoleculeName The name of the molecule the particle belongs to
     */
    public void setMoleculeName(String aMoleculeName) {
        this.moleculeName = aMoleculeName;
        this.moleculeParticleString = this.moleculeName + ModelDefinitions.GENERAL_SEPARATOR + this.particle;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MoleculeParticleFrequency">
    /**
     * Frequency of particle in specified molecule
     *
     * @return Frequency of particle in specified molecule
     */
    public int getMoleculeParticleFrequency() {
        return this.moleculeParticleFrequency;
    }

    /**
     * Frequency of particle in specified molecule
     *
     * @param aMoleculeParticleFrequency Frequency of particle in specified
     * molecule
     */
    public void setMoleculeParticleFrequency(int aMoleculeParticleFrequency) {
        this.moleculeParticleFrequency = aMoleculeParticleFrequency;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleColor">
    /**
     * Color of the particle
     *
     * @return Color of the particle
     */
    public Color getParticleColor() {
        return this.particleColor;
    }

    /**
     * Color of the particle
     *
     * @param aParticleColor Color of the particle
     */
    public void setParticleColor(Color aParticleColor) {
        this.particleColor = aParticleColor;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CurrentParticleColor">
    /**
     * Current color of the particle
     *
     * @return Current color of the particle
     */
    public Color getCurrentParticleColor() {
        return this.currentParticleColor;
    }

    /**
     * Sets current color of the particle according to preferences
     */
    public void setCurrentParticleColor() {
        switch (Preferences.getInstance().getParticleColorDisplayMode()) {
            case MOLECULE_COLOR_MODE:
                this.currentParticleColor = this.moleculeColor;
                break;
            case MOLECULE_PARTICLE_COLOR_MODE:
                this.currentParticleColor = this.moleculeParticleColor;
                break;
            case PARTICLE_COLOR_MODE:
                this.currentParticleColor = this.particleColor;
                break;
        }
    }

    /**
     * Sets current color of the particle
     * 
     * @param aCurrentParticleColor Current color of the particle
     */
    public void setCurrentParticleColor(Color aCurrentParticleColor) {
        this.currentParticleColor = aCurrentParticleColor;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MoleculeColor">
    /**
     * The color of the molecule the particle belongs to
     *
     * @return The color of the molecule the particle belongs to
     */
    public Color getMoleculeColor() {
        return this.moleculeColor;
    }

    /**
     * The color of the molecule the particle belongs to
     *
     * @param aColor The color of the molecule the particle belongs to
     */
    public void setMoleculeColor(Color aColor) {
        this.moleculeColor = aColor;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SelectionColor">
    /**
     * Selection color
     *
     * @return Selection color
     */
    public StandardColorEnum getSelectionColor() {
        return this.selectionColor;
    }

    /**
     * Selection color
     *
     * @param aColor Selection color
     */
    public void setSelectionColor(StandardColorEnum aColor) {
        this.selectionColor = aColor;
    }
    
    /**
     * Returns if selection color is defined
     * 
     * @return True: Selection color is defined, false: Otherwise
     */
    public boolean hasSelectionColor() {
        return this.selectionColor != null;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SelectionInfoString">
    /**
     * Selection info string
     *
     * @return Selection info string
     */
    public String getSelectionInfoString() {
        return this.selectionInfoString;
    }

    /**
     * Selection info string
     *
     * @param aSelectionInfoString Selection info string
     */
    public void setSelectionInfoString(String aSelectionInfoString) {
        this.selectionInfoString = aSelectionInfoString;
    }
    // <editor-fold defaultstate="collapsed" desc="- MoleculeParticleColor">
    /**
     * The color of molecule particle
     *
     * @return The color of molecule particle
     */
    public Color getMoleculeParticleColor() {
        return this.moleculeParticleColor;
    }

    /**
     * The color of molecule particle
     *
     * @param aColor The color of molecule particle
     */
    public void setMoleculeParticleColor(Color aColor) {
        this.moleculeParticleColor = aColor;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CurrentParticleRadius">
    /**
     * Current radius of the particle
     *
     * @return Current radius of the particle
     */
    public double getCurrentParticleRadius() {
        return this.currentParticleRadius;
    }

    /**
     * Current radius of the particle
     *
     * @param aRadius Current radius of the particle
     */
    public void setCurrentParticleRadius(double aRadius) {
        this.currentParticleRadius = aRadius;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CurrentParticleTransparency">
    /**
     * Current transparency of the particle
     *
     * @return Current transparency of the particle
     */
    public float getCurrentParticleTransparency() {
        return this.currentParticleTransparency;
    }

    /**
     * Current transparency of the particle
     *
     * @param aTransparency Current transparency of the particle
     */
    public void setCurrentParticleTransparency(float aTransparency) {
        this.currentParticleTransparency = aTransparency;
    }

    /**
     * Returns if particle is transparent
     *
     * @return True: Particle is transparent, false: Otherwise
     */
    public boolean isParticleTransparent() {
        return this.currentParticleTransparency > 0f;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleRadius">
    /**
     * Radius of the particle
     *
     * @return Radius of the particle
     */
    @Override
    public double getParticleRadius() {
        return this.particleRadius;
    }

    /**
     * Radius of the particle
     *
     * @param aRadius Radius of the particle
     */
    public void setParticleRadius(double aRadius) {
        this.particleRadius = aRadius;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Pixels">
    /**
     * The graphics object of the graphical particle/sphere
     *
     * @return The graphics object of the graphical particle/sphere
     */
    public Object getGraphicsObject() {
        return this.graphicsObject;
    }

    /**
     * The graphics object of the graphical particle/sphere
     *
     * @param aGraphicsObject The graphics object of the graphical particle/sphere
     */
    public void setGraphicsObject(Object aGraphicsObject) {
        this.graphicsObject = aGraphicsObject;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RadiusInPixel">
    /**
     * The radius of the graphical particle/sphere in pixel
     *
     * @return The radius of the graphical particle/sphere in pixel
     */
    public int getRadiusInPixel() {
        return this.radiusInPixel;
    }

    /**
     * The radius of the graphical particle/sphere in pixel
     *
     * @param aRadiusInPixel The radius of the graphical particle/sphere in
     * pixel
     */
    public void setRadiusInPixel(int aRadiusInPixel) {
        this.radiusInPixel = aRadiusInPixel;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Visibility">
    /**
     * Visibility of molecule particle
     *
     * @return True: Molecule particle is visible, false: Otherwise
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Visibility of molecule particle
     *
     * @param anIsVisible True: Molecule particle is visible, false:
     * Otherwise
     */
    public void setVisible(boolean anIsVisible) {
        this.isVisible = anIsVisible;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="- Particle">
    /**
     * Particle
     *
     * @return Particle
     */
    public String getParticle() {
        return this.particle;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- ParticleName">
    /**
     * Name of the particle
     *
     * @return Name of the particle
     */
    public String getParticleName() {
        return this.particleName;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- MoleculeParticleString">
    /**
     * Molecule name plus particle string for comparison
     *
     * @return Molecule name plus particle string for comparison
     */
    public String getMoleculeParticleString() {
        return this.moleculeParticleString;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialises class variables
     */
    private void initialize() {
        this.particle = null;
        this.particleName = null;
        this.particleColor = null;
        this.currentParticleColor = null;
        this.particleRadius = 0.0;
        this.currentParticleRadius = 0.0;
        this.currentParticleTransparency = 0f;
        this.moleculeName = null;
        this.moleculeParticleString = null;
        this.moleculeParticleFrequency = 0;
        this.moleculeColor = null;
        this.selectionColor = null;
        this.selectionInfoString = null;
        this.moleculeParticleColor = null;
        this.isVisible = true;
    }
    // </editor-fold>

}
