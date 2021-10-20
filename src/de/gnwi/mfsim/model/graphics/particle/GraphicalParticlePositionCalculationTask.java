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
package de.gnwi.mfsim.model.graphics.particle;

import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayer;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayerSingleSurfaceEnum;
import de.gnwi.mfsim.model.graphics.body.BodySphere;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.peptide.PdbToDpd;
import de.gnwi.mfsim.model.peptide.PdbToDpdPool;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import de.gnwi.mfsim.model.util.ProgressTaskInterface;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.jdpd.interfaces.IRandom;

/**
 * Task for graphical particle position calculation
 *
 * @author Achim Zielesny
 *
 */
public class GraphicalParticlePositionCalculationTask implements ProgressTaskInterface {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Utility misc methods
     */
    private final MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * Utility graphics methods
     */
    private final GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Property change support
     */
    private final PropertyChangeSupport propertyChangeSupport;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Task was started, false: Otherwise
     */
    private boolean isStarted;

    /**
     * True: Task was stopped, false: Otherwise
     */
    private boolean isStopped;

    /**
     * True: Task finished, false: Otherwise
     */
    private boolean isFinished;

    /**
     * Calculated graphical particle positions
     */
    private GraphicalParticlePosition[] graphicalParticlePositions;

    /**
     * Compartment container
     */
    private CompartmentContainer compartmentContainer;

    /**
     * HashMap that maps molecule name to its particle hashtable that maps a
     * particle to its graphical particle
     */
    private HashMap<String, HashMap<String, IGraphicalParticle>> moleculeToParticlesMap;

    /**
     * Pattern for single particle to match
     */
    private Pattern particlePattern = Pattern.compile(ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING);

    /**
     * GraphicalParticlePositionInfo instance
     */
    private GraphicalParticlePositionInfo graphicalParticlePositionInfo;
    
    /**
     * Progress value
     */
    private int progressValue;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates a new instance
     *
     * @param aCompartmentContainer Compartment container
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public GraphicalParticlePositionCalculationTask(CompartmentContainer aCompartmentContainer) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCompartmentContainer == null) {
            throw new IllegalArgumentException("Argument is null.");
        }

        // </editor-fold>
        this.isStarted = false;
        this.isStopped = false;
        this.isFinished = false;
        this.compartmentContainer = aCompartmentContainer;
        this.moleculeToParticlesMap = null;
        this.graphicalParticlePositions = null;
        this.graphicalParticlePositionInfo = null;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.progressValue = -1;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns calculated GraphicalParticlePositionInfo instance. NOTE: The
     * returned instance is NOT cached. NOTE: This method MUST be called AFTER
     * task has successfully finished.
     *
     * @return Calculated GraphicalParticlePositionInfo instance. NOTE: The
 returned instance is NOT cached. NOTE: This method MUST be called AFTER
 task has successfully finished.
     */
    public GraphicalParticlePositionInfo getGraphicalParticlePositionInfo() {
        // Parameter true: Compartments/Bulk exist
        return this.graphicalParticlePositionInfo;
    }

    /**
     * True: Task has successfully finished operations, false: Otherwise
     *
     * @return True: Task has successfully finished operations, false:
     * Otherwise
     */
    public boolean isFinished() {
        return this.isStarted && this.isFinished;
    }

    /**
     * True: Task is executing jobs, false: Otherwise
     *
     * @return True: Task is executing jobs, false: Otherwise
     */
    public boolean isWorking() {
        return this.isStarted && !this.isFinished;
    }

    /**
     * True: Task has started operations, false: Otherwise
     *
     * @return True: Task has started operations, false: Otherwise
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    /**
     * Stops execution of task
     */
    public void stop() {
        this.isStopped = true;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public property change support methods">
    /**
     * Add property change listener
     * 
     * @param aListener Listener
     */
    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.addPropertyChangeListener(aListener);
    }

    /**
     * Remove property change listener
     * 
     * @param aListener Listener
     */
    public void removePropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.removePropertyChangeListener(aListener);
    }    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public (overriden) methods">
    // <editor-fold defaultstate="collapsed" desc="- call">
    /**
     * This method will be called when the task is executed. The methods
     * calculates the graphical particle positions.
     *
     * @return True if the graphical particle positions have been calculated
     * successfully, otherwise false.
     * @throws Exception Thrown when an error occurred
     */
    @Override
    public Boolean call() throws Exception {
        try {
            // <editor-fold defaultstate="collapsed" desc="Task starts. Set progress in percent to 0.">
            this.isStarted = true;
            this.setProgressValue(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set variables">
            // Miscellaneous utility methods
            MiscUtilityMethods tmpMiscUtilityMethods = new MiscUtilityMethods();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set random number generator and seed">
            int tmpRandomSeed = this.compartmentContainer.getGeometryRandomSeed();
            IRandom tmpRandomNumberGenerator = this.miscUtilityMethods.getRandomNumberGenerator(tmpRandomSeed);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set bond length">
            // Bond length = 2 * radius of single particle in DPD units
            double tmpBondLength = 2.0 * this.compartmentContainer.getStandardParticleRadius();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize particles and molecules data structures">
            this.moleculeToParticlesMap = this.compartmentContainer.getMoleculeToParticlesMap();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize this.graphicalParticlePositions">
            int tmpTotalNumberOfParticlesInSimulation = this.compartmentContainer.getTotalNumberOfParticles();
            this.graphicalParticlePositions = new GraphicalParticlePosition[tmpTotalNumberOfParticlesInSimulation];
            int tmpGraphicalParticlePositionsIndex = 0;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="GraphicalParticlePositions in compartments">
            // IMPORTANT: Clear possible old lists of excluded spheres of all bodies
            this.compartmentContainer.getCompartmentBox().clearExcludedSphereListsOfBodies();
            ArrayList<BodyInterface> tmpBodyList = this.compartmentContainer.getCompartmentBox().getBodies();
            if (tmpBodyList.size() > 0) {
                ValueItem tmpGeometryDataValueItem = null;
                ValueItem tmpChemicalCompositionValueItem = null;
                for (BodyInterface tmpBody : tmpBodyList) {
                    switch (tmpBody.getBodyType()) {
                        // <editor-fold defaultstate="collapsed" desc="Spheres">
                        case SPHERE:
                            // <editor-fold defaultstate="collapsed" desc="Set value items">
                            BodySphere tmpSphere = (BodySphere) tmpBody;
                            // Sphere contains geometry data value item
                            tmpGeometryDataValueItem = tmpSphere.getGeometryDataValueItem();
                            if (tmpGeometryDataValueItem == null) {
                                // Fire property change to notify property change listeners about cancellation due to internal error
                                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                return returnCancelled();
                            }
                            // Get corresponding chemical composition value item
                            tmpChemicalCompositionValueItem = this.compartmentContainer.getSphereChemicalCompositionValueItemOfBlock(tmpGeometryDataValueItem.getBlockName());
                            if (tmpChemicalCompositionValueItem == null) {
                                // Fire property change to notify property change listeners about cancellation due to internal error
                                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                return returnCancelled();
                            }
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Sort matrix rows with protein data first">
                            // IMPORTANT in sphere: Sort so that rows with protein data come first
                            tmpChemicalCompositionValueItem.sortMatrixRowsWithProteinDataRowsFirst();
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Create GraphicalParticlePositions">
                            for (int i = 0; i < tmpChemicalCompositionValueItem.getMatrixRowCount(); i++) {
                                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                if (this.isStopped) {
                                    // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                    tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                    return returnCancelled();
                                }
                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                                // Molecule name
                                String tmpMoleculeName = tmpChemicalCompositionValueItem.getValue(i, 0);
                                // Molecular structure and possible protein data
                                String tmpMolecularStructureString = tmpChemicalCompositionValueItem.getValue(i, 1);
                                boolean tmpIsSingleParticleMolecule = this.particlePattern.matcher(tmpMolecularStructureString).matches();
                                String tmpProteinData = "";
                                if (tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                                    tmpProteinData = tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                                }
                                // Quantity in volume
                                int tmpQuantityInVolume = tmpChemicalCompositionValueItem.getValueAsInt(i, 5);
                                // Quantity on surface
                                int tmpQuantityOnSurface = tmpChemicalCompositionValueItem.getValueAsInt(i, 6);
                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="Set GraphicalParticlePositions">
                                if (tmpQuantityInVolume > 0 || tmpQuantityOnSurface > 0) {
                                    if (this.graphicsUtilityMethods.isProtein3dStructureInSphere(tmpChemicalCompositionValueItem, i)) {
                                        // <editor-fold defaultstate="collapsed" desc="Protein data are used for 3D structure">
                                        if (tmpQuantityOnSurface > 0 || tmpProteinData.isEmpty()) {
                                            // Fire property change to notify property change listeners about cancellation due to internal error
                                            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                            return returnCancelled();
                                        }
                                        // Amino acids MUST already be initialized
                                        // Initialize tmpPdbToDpd
                                        PdbToDpd tmpPdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                                        boolean tmpIsProteinRandom3DOrientation = false;
                                        if (this.graphicsUtilityMethods.isRandom3dStructureGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                            tmpIsProteinRandom3DOrientation = true;
                                            tmpPdbToDpd.setSeed(tmpRandomSeed);
                                            tmpPdbToDpd.setRandomNumberGenerator(tmpRandomNumberGenerator);
                                        } else {
                                            tmpPdbToDpd.setDefaultRotation();
                                        }
                                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                        if (this.isStopped) {
                                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                            return returnCancelled();
                                        }
                                        // </editor-fold>
                                        if (tmpQuantityInVolume == 1) {
                                            // <editor-fold defaultstate="collapsed" desc="1 protein in sphere">
                                            tmpPdbToDpd.setCenter(tmpSphere.getBodyCenter().getX(), tmpSphere.getBodyCenter().getY(), tmpSphere.getBodyCenter().getZ());
                                            tmpPdbToDpd.setRadius(tmpSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                                            if (tmpIsProteinRandom3DOrientation) {
                                                tmpPdbToDpd.setRandomOrientation();
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                return returnCancelled();
                                            }

                                            // </editor-fold>
                                            // Set tmpProteinGraphicalParticlePositionsArray
                                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                            int tmpTotalNumberOfParticlesOfProtein = tmpSpices.getTotalNumberOfParticles();
                                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                            HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleMap = this.moleculeToParticlesMap.get(tmpMoleculeName);
                                            HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleInterfaceMap = new HashMap<String, IGraphicalParticle>(tmpParticleToGraphicalParticleMap.size());
                                            for (String tmpParticle : tmpParticleToGraphicalParticleMap.keySet()) {
                                                tmpParticleToGraphicalParticleInterfaceMap.put(tmpParticle, tmpParticleToGraphicalParticleMap.get(tmpParticle));
                                            }
                                            GraphicalParticlePosition[] tmpProteinGraphicalParticlePositionsArray = new GraphicalParticlePosition[tmpTotalNumberOfParticlesOfProtein];
                                            double tmpInitialValue = 0.0;
                                            for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                                tmpProteinGraphicalParticlePositionsArray[j] = new GraphicalParticlePosition(tmpInitialValue, tmpInitialValue, tmpInitialValue);
                                            }
                                            tmpPdbToDpd.getGraphicalParticlePositions(tmpParticleToGraphicalParticleInterfaceMap, tmpProteinGraphicalParticlePositionsArray);
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                return returnCancelled();
                                            }

                                            // </editor-fold>
                                            // Copy to this.graphicalParticlePositions
                                            for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpProteinGraphicalParticlePositionsArray[j];
                                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(false);
                                            }
                                            // </editor-fold>
                                        } else {
                                            // <editor-fold defaultstate="collapsed" desc="Multiple proteins in sphere">
                                            // Determine radius of protein
                                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                            int tmpTotalNumberOfParticlesOfProtein = tmpSpices.getTotalNumberOfParticles();
                                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                            double tmpRadiusOfProtein = this.miscUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticlesOfProtein,
                                                    this.compartmentContainer.getDensityInfoValueItem().getValueAsDouble());
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            // Set tmpParticleToGraphicalParticleInterfaceMap
                                            HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleMap = this.moleculeToParticlesMap.get(tmpMoleculeName);
                                            HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleInterfaceMap
                                                    = new HashMap<String, IGraphicalParticle>(tmpParticleToGraphicalParticleMap.size());
                                            for (String tmpParticle : tmpParticleToGraphicalParticleMap.keySet()) {
                                                tmpParticleToGraphicalParticleInterfaceMap.put(tmpParticle, tmpParticleToGraphicalParticleMap.get(tmpParticle));
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            // Determine non-overlapping spheres for proteins
                                            LinkedList<BodySphere> tmpSphereList = 
                                                tmpSphere.getNonOverlappingRandomSpheres(
                                                    tmpQuantityInVolume, 
                                                    tmpRadiusOfProtein,
                                                    Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                    tmpRandomNumberGenerator
                                                );
                                            for (BodySphere tmpSingleSphere : tmpSphereList) {
                                                tmpPdbToDpd.setCenter(tmpSingleSphere.getBodyCenter().getX(), tmpSingleSphere.getBodyCenter().getY(), tmpSingleSphere.getBodyCenter().getZ());
                                                tmpPdbToDpd.setRadius(tmpSingleSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                                                // Set tmpProteinGraphicalParticlePositionsArray
                                                GraphicalParticlePosition[] tmpProteinGraphicalParticlePositionsArray = new GraphicalParticlePosition[tmpTotalNumberOfParticlesOfProtein];
                                                double tmpInitialValue = 0.0;
                                                for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                                    tmpProteinGraphicalParticlePositionsArray[j] = new GraphicalParticlePosition(tmpInitialValue, tmpInitialValue, tmpInitialValue);
                                                }
                                                if (tmpIsProteinRandom3DOrientation) {
                                                    tmpPdbToDpd.setRandomOrientation();
                                                }
                                                tmpPdbToDpd.getGraphicalParticlePositions(tmpParticleToGraphicalParticleInterfaceMap, tmpProteinGraphicalParticlePositionsArray);
                                                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                                if (this.isStopped) {
                                                    // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                    tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                    PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                    return returnCancelled();
                                                }
                                                // </editor-fold>
                                                // Copy to this.graphicalParticlePositions
                                                for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                                    this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpProteinGraphicalParticlePositionsArray[j];
                                                    this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(false);
                                                }
                                            }
                                            // </editor-fold>
                                        }
                                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                        if (this.isStopped) {
                                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                            return returnCancelled();
                                        }

                                        // </editor-fold>
                                        PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                        // </editor-fold>
                                    } else {
                                        // <editor-fold defaultstate="collapsed" desc="No protein data are used">
                                        GraphicalParticlePosition[] tmpFirstParticleCoordinates = new GraphicalParticlePosition[tmpQuantityInVolume + tmpQuantityOnSurface];
                                        GraphicalParticlePosition[] tmpLastParticleCoordinates = new GraphicalParticlePosition[tmpQuantityInVolume + tmpQuantityOnSurface];
                                        if (tmpQuantityInVolume > 0) {
                                            if (tmpIsSingleParticleMolecule) {
                                                tmpSphere.fillRandomPointsInVolumeWithExcludingSpheres(
                                                    tmpFirstParticleCoordinates, 
                                                    0, 
                                                    tmpQuantityInVolume, 
                                                    Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                    tmpRandomNumberGenerator
                                                );
                                                tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                                            } else {
                                                tmpSphere.fillRandomPointsInVolumeWithExcludingSpheres(
                                                    tmpFirstParticleCoordinates, 
                                                    tmpLastParticleCoordinates, 
                                                    0, 
                                                    tmpQuantityInVolume,
                                                    tmpBondLength, 
                                                    Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                    tmpRandomNumberGenerator
                                                );
                                            }
                                        }
                                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                        if (this.isStopped) {
                                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                            return returnCancelled();
                                        }
                                        // </editor-fold>
                                        if (tmpQuantityOnSurface > 0) {
                                            if (this.graphicsUtilityMethods.isUpperSurfaceGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                                tmpSphere.fillUpperRandomPointsOnSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                            } else if (this.graphicsUtilityMethods.isMiddleSurfaceGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                                tmpSphere.fillMiddleRandomPointsOnSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                            } else {
                                                tmpSphere.fillRandomPointsOnSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                            }
                                            // Center of sphere
                                            GraphicalParticlePosition tmpCenterOfSphere = new GraphicalParticlePosition(tmpSphere.getBodyCenter().getX(), tmpSphere.getBodyCenter().getY(),
                                                    tmpSphere.getBodyCenter().getZ());
                                            Arrays.fill(tmpLastParticleCoordinates, tmpQuantityInVolume, tmpQuantityInVolume + tmpQuantityOnSurface, tmpCenterOfSphere);
                                        }
                                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                        if (this.isStopped) {
                                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                            return returnCancelled();
                                        }
                                        // </editor-fold>
                                        // Calculate coordinates of molecular particles
                                        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                        GraphicalParticlePosition[][] tmpGraphicalParticlePositionsArray = tmpSpices.getParticleCoordinates(this.moleculeToParticlesMap.get(tmpMoleculeName),
                                                tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
                                        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                        // Copy to this.graphicalParticlePositions
                                        for (int j = 0; j < tmpGraphicalParticlePositionsArray.length; j++) {
                                            for (int k = 0; k < tmpGraphicalParticlePositionsArray[j].length; k++) {
                                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpGraphicalParticlePositionsArray[j][k];
                                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(false);
                                            }
                                        }
                                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                        if (this.isStopped) {
                                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                            return returnCancelled();
                                        }
                                        // </editor-fold>
                                        // </editor-fold>
                                    }
                                }
                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="Set progress">
                                this.setProgressValue(tmpMiscUtilityMethods.getPercentWithMax99(tmpGraphicalParticlePositionsIndex + 1, tmpTotalNumberOfParticlesInSimulation));
                                // </editor-fold>
                            }
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Restore matrix rows that were sorted with protein data first">
                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                            // </editor-fold>
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="xy-Layers">
                        case XY_LAYER:
                            // <editor-fold defaultstate="collapsed" desc="Set value items">
                            BodyXyLayer tmpXyLayer = (BodyXyLayer) tmpBody;
                            // Xy-layer contains geometry data value item
                            tmpGeometryDataValueItem = tmpXyLayer.getGeometryDataValueItem();
                            if (tmpGeometryDataValueItem == null) {
                                // Fire property change to notify property change listeners about cancellation due to internal error
                                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                return returnCancelled();
                            }
                            // Corresponding geometry display value item
                            ValueItem tmpGeometryDisplayValueItem = tmpGeometryDataValueItem.getDisplayValueItem();
                            if (tmpGeometryDisplayValueItem == null) {
                                // Fire property change to notify property change listeners about cancellation due to internal error
                                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                return returnCancelled();
                            }
                            // Get corresponding chemical composition value item
                            tmpChemicalCompositionValueItem = this.compartmentContainer.getXyLayerChemicalCompositionValueItemOfBlock(tmpGeometryDataValueItem.getBlockName());
                            if (tmpChemicalCompositionValueItem == null) {
                                // Fire property change to notify property change listeners about cancellation due to internal error
                                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                return returnCancelled();
                            }

                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Sort matrix rows with protein data first">
                            // IMPORTANT in xy-layer: Sort so that rows with protein data come first
                            tmpChemicalCompositionValueItem.sortMatrixRowsWithProteinDataRowsFirst();
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Create GraphicalParticlePositions">
                            if (this.graphicsUtilityMethods.isLatticeGeometryInXyLayer(tmpChemicalCompositionValueItem)) {
                                // <editor-fold defaultstate="collapsed" desc="Lattice positions in xy-layer">
                                for (int i = 0; i < tmpChemicalCompositionValueItem.getMatrixRowCount(); i++) {
                                    // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                    if (this.isStopped) {
                                        // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                        tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                        return returnCancelled();
                                    }
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                                    // Molecule name
                                    String tmpMoleculeName = tmpChemicalCompositionValueItem.getValue(i, 0);
                                    // Molecular structure
                                    String tmpMolecularStructureString = tmpChemicalCompositionValueItem.getValue(i, 1);
                                    // Quantity in volume
                                    int tmpQuantityInVolume = tmpChemicalCompositionValueItem.getValueAsInt(i, 5);
                                    // Quantity on surface
                                    int tmpQuantityOnSurface = tmpChemicalCompositionValueItem.getValueAsInt(i, 6);

                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="Set GraphicalParticlePositions">
                                    if (tmpQuantityInVolume > 0 || tmpQuantityOnSurface > 0) {
                                        GraphicalParticlePosition[] tmpFirstParticleCoordinates = new GraphicalParticlePosition[tmpQuantityInVolume + tmpQuantityOnSurface];
                                        tmpXyLayer.getSimpleCubicLatticePointsInBuffer(tmpFirstParticleCoordinates, tmpBondLength);
                                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                        if (this.isStopped) {
                                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                            return returnCancelled();
                                        }
                                        // </editor-fold>
                                        // Calculate coordinates of molecular particles
                                        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                        GraphicalParticlePosition[][] tmpGraphicalParticlePositionsArray = tmpSpices.getParticleCoordinates(this.moleculeToParticlesMap.get(tmpMoleculeName),
                                                tmpFirstParticleCoordinates, tmpFirstParticleCoordinates, tmpBondLength);
                                        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                        // Copy to this.graphicalParticlePositions
                                        for (int j = 0; j < tmpGraphicalParticlePositionsArray.length; j++) {
                                            for (int k = 0; k < tmpGraphicalParticlePositionsArray[j].length; k++) {
                                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpGraphicalParticlePositionsArray[j][k];
                                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(false);
                                            }
                                        }
                                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                        if (this.isStopped) {
                                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                            return returnCancelled();
                                        }

                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="Set progress">
                                        this.setProgressValue(tmpMiscUtilityMethods.getPercentWithMax99(tmpGraphicalParticlePositionsIndex + 1, tmpTotalNumberOfParticlesInSimulation));
                                        // </editor-fold>
                                        break;
                                    }
                                    // </editor-fold>
                                }

                                // </editor-fold>
                            } else {
                                // <editor-fold defaultstate="collapsed" desc="Random positions in xy-layer">
                                for (int i = 0; i < tmpChemicalCompositionValueItem.getMatrixRowCount(); i++) {
                                    // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                    if (this.isStopped) {
                                        // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                        tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                        return returnCancelled();
                                    }
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                                    // Molecule name
                                    String tmpMoleculeName = tmpChemicalCompositionValueItem.getValue(i, 0);
                                    // Molecular structure and possible protein data
                                    String tmpMolecularStructureString = tmpChemicalCompositionValueItem.getValue(i, 1);
                                    boolean tmpIsSingleParticleMolecule = this.particlePattern.matcher(tmpMolecularStructureString).matches();
                                    String tmpProteinData = "";
                                    if (tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                                        tmpProteinData = tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                                    }
                                    // Quantity in volume
                                    int tmpQuantityInVolume = tmpChemicalCompositionValueItem.getValueAsInt(i, 5);
                                    // Quantity on surface
                                    int tmpQuantityOnSurface = tmpChemicalCompositionValueItem.getValueAsInt(i, 6);
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="Set GraphicalParticlePositions">
                                    if (tmpQuantityInVolume > 0 || tmpQuantityOnSurface > 0) {
                                        if (this.graphicsUtilityMethods.is3dStructureGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                            // <editor-fold defaultstate="collapsed" desc="Protein data are used for 3D structure in spheres in xy-layer">
                                            if (tmpQuantityOnSurface > 0 || tmpProteinData.isEmpty()) {
                                                // Fire property change to notify property change listeners about cancellation due to internal error
                                                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                                return returnCancelled();
                                            }
                                            // Amino acids MUST already be initialized
                                            // Initialize tmpPdbToDPD
                                            PdbToDpd tmpPdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                                            boolean tmpIsProteinRandom3DOrientation = false;
                                            if (this.graphicsUtilityMethods.isRandom3dStructureGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                tmpIsProteinRandom3DOrientation = true;
                                                tmpPdbToDpd.setSeed(tmpRandomSeed);
                                                tmpPdbToDpd.setRandomNumberGenerator(tmpRandomNumberGenerator);
                                            } else {
                                                tmpPdbToDpd.setDefaultRotation();
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            // Determine radius of protein
                                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                            int tmpTotalNumberOfParticlesOfProtein = tmpSpices.getTotalNumberOfParticles();
                                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                            double tmpRadiusOfProtein = this.miscUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticlesOfProtein,
                                                    this.compartmentContainer.getDensityInfoValueItem().getValueAsDouble());
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            // Set tmpParticleToGraphicalParticleInterfaceMap
                                            HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleMap = this.moleculeToParticlesMap.get(tmpMoleculeName);
                                            HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleInterfaceMap
                                                    = new HashMap<String, IGraphicalParticle>(tmpParticleToGraphicalParticleMap.size());
                                            for (String tmpParticle : tmpParticleToGraphicalParticleMap.keySet()) {
                                                tmpParticleToGraphicalParticleInterfaceMap.put(tmpParticle, tmpParticleToGraphicalParticleMap.get(tmpParticle));
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            // Determine non-overlapping spheres for proteins
                                            LinkedList<BodySphere> tmpSphereList = 
                                                tmpXyLayer.getNonOverlappingRandomSpheres(
                                                    tmpQuantityInVolume, 
                                                    tmpRadiusOfProtein,
                                                    Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                    tmpRandomNumberGenerator
                                                );
                                            for (BodySphere tmpSingleSphere : tmpSphereList) {
                                                tmpPdbToDpd.setCenter(tmpSingleSphere.getBodyCenter().getX(), tmpSingleSphere.getBodyCenter().getY(), tmpSingleSphere.getBodyCenter().getZ());
                                                tmpPdbToDpd.setRadius(tmpSingleSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                                                // Set tmpProteinGraphicalParticlePositionsArray
                                                GraphicalParticlePosition[] tmpProteinGraphicalParticlePositionsArray = new GraphicalParticlePosition[tmpTotalNumberOfParticlesOfProtein];
                                                double tmpInitialValue = 0.0;
                                                for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                                    tmpProteinGraphicalParticlePositionsArray[j] = new GraphicalParticlePosition(tmpInitialValue, tmpInitialValue, tmpInitialValue);
                                                }
                                                if (tmpIsProteinRandom3DOrientation) {
                                                    tmpPdbToDpd.setRandomOrientation();
                                                }
                                                tmpPdbToDpd.getGraphicalParticlePositions(tmpParticleToGraphicalParticleInterfaceMap, tmpProteinGraphicalParticlePositionsArray);
                                                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                                if (this.isStopped) {
                                                    // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                    tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                    PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                    return returnCancelled();
                                                }
                                                // </editor-fold>
                                                // Copy to this.graphicalParticlePositions
                                                for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                                    this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpProteinGraphicalParticlePositionsArray[j];
                                                    this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(false);
                                                }
                                            }
                                            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                return returnCancelled();
                                            }

                                            // </editor-fold>
                                            // </editor-fold>
                                        } else {
                                            // <editor-fold defaultstate="collapsed" desc="No protein data">
                                            GraphicalParticlePosition[] tmpFirstParticleCoordinates = new GraphicalParticlePosition[tmpQuantityInVolume + tmpQuantityOnSurface];
                                            GraphicalParticlePosition[] tmpLastParticleCoordinates = new GraphicalParticlePosition[tmpQuantityInVolume + tmpQuantityOnSurface];
                                            // Center of xy-layer
                                            double tmpXyLayerCenterXCoordinate = tmpXyLayer.getBodyCenter().getX();
                                            double tmpXyLayerCenterYCoordinate = tmpXyLayer.getBodyCenter().getY();
                                            double tmpXyLayerCenterZCoordinate = tmpXyLayer.getBodyCenter().getZ();
                                            double tmpHalfXLength = tmpXyLayer.getXLength() / 2.0;
                                            double tmpHalfYLength = tmpXyLayer.getYLength() / 2.0;
                                            double tmpHalfZLength = tmpXyLayer.getZLength() / 2.0;
                                            double tmpOffsetX = tmpXyLayerCenterXCoordinate;
                                            double tmpOffsetY = tmpXyLayerCenterYCoordinate;
                                            double tmpOffsetZ = tmpXyLayerCenterZCoordinate;
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            if (tmpQuantityInVolume > 0) {
                                                if (tmpIsSingleParticleMolecule) {
                                                    tmpXyLayer.fillRandomPointsInVolumeWithExcludingSpheres(
                                                        tmpFirstParticleCoordinates, 
                                                        0, 
                                                        tmpQuantityInVolume, 
                                                        Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                        tmpRandomNumberGenerator
                                                    );
                                                    tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                                                } else {
                                                    tmpXyLayer.fillRandomPointsInVolumeWithExcludingSpheres(
                                                        tmpFirstParticleCoordinates, 
                                                        tmpLastParticleCoordinates, 
                                                        0, 
                                                        tmpQuantityInVolume,
                                                        tmpBondLength, 
                                                        Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                        tmpRandomNumberGenerator
                                                    );
                                                }
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            if (tmpQuantityOnSurface > 0) {
                                                if (this.graphicsUtilityMethods.isAllSurfacesGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                    tmpXyLayer.fillRandomPointsOnAllSurfaces(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                    GraphicalParticlePosition tmpCenterOfXyLayer = new GraphicalParticlePosition(
                                                            tmpXyLayer.getBodyCenter().getX(), tmpXyLayer.getBodyCenter().getY(), tmpXyLayer.getBodyCenter().getZ());
                                                    Arrays.fill(tmpLastParticleCoordinates, tmpQuantityInVolume, tmpQuantityInVolume + tmpQuantityOnSurface, tmpCenterOfXyLayer);
                                                } else {
                                                    if (this.graphicsUtilityMethods.isSingleSurfaceGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                        // <editor-fold defaultstate="collapsed" desc="Single surface geometry">
                                                        if (this.graphicsUtilityMethods.isSingleSurfaceXyTopGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XY_TOP;
                                                            tmpOffsetZ = tmpXyLayerCenterZCoordinate - tmpHalfZLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpFirstParticleCoordinates[j].getX(), tmpFirstParticleCoordinates[j].getY(), tmpOffsetZ);
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceXyBottomGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XY_BOTTOM;
                                                            tmpOffsetZ = tmpXyLayerCenterZCoordinate + tmpHalfZLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpFirstParticleCoordinates[j].getX(), tmpFirstParticleCoordinates[j].getY(), tmpOffsetZ);
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceYzLeftGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.YZ_LEFT;
                                                            tmpOffsetX = tmpXyLayerCenterXCoordinate + tmpHalfXLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpOffsetX, tmpFirstParticleCoordinates[j].getY(), tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceYzRightGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.YZ_RIGHT;
                                                            tmpOffsetX = tmpXyLayerCenterXCoordinate - tmpHalfXLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpOffsetX, tmpFirstParticleCoordinates[j].getY(), tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceXzFrontGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XZ_FRONT;
                                                            tmpOffsetY = tmpXyLayerCenterYCoordinate + tmpHalfYLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpFirstParticleCoordinates[j].getX(), tmpOffsetY, tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceXzBackGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XZ_BACK;
                                                            tmpOffsetY = tmpXyLayerCenterYCoordinate - tmpHalfYLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpFirstParticleCoordinates[j].getX(), tmpOffsetY, tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        }
                                                        // </editor-fold>
                                                    } else {
                                                        // <editor-fold defaultstate="collapsed" desc="xy top and bottom surface geometry">
                                                        if (this.graphicsUtilityMethods.isXyTopBottomGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            tmpXyLayer.fillRandomPointsOnTopBottomSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpFirstParticleCoordinates[j].getX(), tmpFirstParticleCoordinates[j].getY(), tmpOffsetZ);
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isYzLeftRightGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            tmpXyLayer.fillRandomPointsOnLeftRightSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpOffsetX, tmpFirstParticleCoordinates[j].getY(), tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isXzFrontBackGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            tmpXyLayer.fillRandomPointsOnFrontBackSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new GraphicalParticlePosition(tmpFirstParticleCoordinates[j].getX(), tmpOffsetY, tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        }
                                                        // </editor-fold>
                                                    }
                                                }
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            // Calculate coordinates of molecular particles
                                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                            GraphicalParticlePosition[][] tmpGraphicalParticlePositionsArray = 
                                                tmpSpices.getParticleCoordinates(
                                                    this.moleculeToParticlesMap.get(tmpMoleculeName),
                                                    tmpFirstParticleCoordinates, 
                                                    tmpLastParticleCoordinates, 
                                                    tmpBondLength
                                                );
                                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                            // Copy to this.graphicalParticlePositions
                                            for (int j = 0; j < tmpGraphicalParticlePositionsArray.length; j++) {
                                                for (int k = 0; k < tmpGraphicalParticlePositionsArray[j].length; k++) {
                                                    this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpGraphicalParticlePositionsArray[j][k];
                                                    this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(false);
                                                }
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                            if (this.isStopped) {
                                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                                tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                                                return returnCancelled();
                                            }
                                            // </editor-fold>
                                            // </editor-fold>
                                        }
                                    }
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="Set progress">
                                    this.setProgressValue(tmpMiscUtilityMethods.getPercentWithMax99(tmpGraphicalParticlePositionsIndex + 1, tmpTotalNumberOfParticlesInSimulation));
                                    // </editor-fold>
                                }
                                // Clear list for possible spheres in xy-layer
                                tmpXyLayer.clearExcludedSphereList();
                                // </editor-fold>
                            }
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Restore matrix rows that were sorted with protein data first">
                            tmpChemicalCompositionValueItem.restoreOriginalMatrixRowsAfterSorting();
                            // </editor-fold>
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Unknown body">
                        default:
                            // Unknown body type
                            // Fire property change to notify property change listeners about cancellation due to internal error
                            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                            return returnCancelled();
                        // </editor-fold>
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
            if (this.isStopped) {
                return returnCancelled();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="GraphicalParticlePositions in bulk">
            ValueItem tmpBulkInfoValueItem = this.compartmentContainer.getBulkInfoValueItem();
            if (tmpBulkInfoValueItem == null) {
                // Fire property change to notify property change listeners about cancellation due to internal error
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                return returnCancelled();
            }
            // Set NEW list for possible spheres in bulk
            this.compartmentContainer.getCompartmentBox().setExcludedSphereList(new LinkedList<BodySphere>());                
            // <editor-fold defaultstate="collapsed" desc="Sort matrix rows with protein data first">
            // IMPORTANT in bulk: Sort so that rows with protein data come first
            tmpBulkInfoValueItem.sortMatrixRowsWithProteinDataRowsFirst();
            // </editor-fold>
            for (int i = 0; i < tmpBulkInfoValueItem.getMatrixRowCount(); i++) {
                // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                // Molecule name
                String tmpMoleculeName = tmpBulkInfoValueItem.getValue(i, 0);
                // Molecular structure
                String tmpMolecularStructureString = tmpBulkInfoValueItem.getValue(i, 1);
                boolean tmpIsSingleParticleMolecule = this.particlePattern.matcher(tmpMolecularStructureString).matches();
                String tmpProteinData = "";
                if (tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    tmpProteinData = tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                }
                // Quantity
                int tmpQuantity = tmpBulkInfoValueItem.getValueAsInt(i, 3);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set graphical particle positions">
                if (tmpQuantity > 0) {
                    if (this.graphicsUtilityMethods.isProtein3dStructureInBulk(tmpBulkInfoValueItem, i)) {
                        // <editor-fold defaultstate="collapsed" desc="Protein data are used for 3D structure in bulk">
                        if (tmpProteinData.isEmpty()) {
                            // Fire property change to notify property change listeners about cancellation due to internal error
                            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                            return returnCancelled();
                        }
                        // Amino acids MUST already be initialized
                        // Initialize tmpPdbToDPD
                        PdbToDpd tmpPdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                        boolean tmpIsProteinRandom3DOrientation = false;
                        if (this.graphicsUtilityMethods.isRandom3dStructureGeometryInBulk(tmpBulkInfoValueItem, i)) {
                            tmpIsProteinRandom3DOrientation = true;
                            tmpPdbToDpd.setSeed(tmpRandomSeed);
                            tmpPdbToDpd.setRandomNumberGenerator(tmpRandomNumberGenerator);
                        } else {
                            tmpPdbToDpd.setDefaultRotation();
                        }
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                            return returnCancelled();
                        }
                        // </editor-fold>
                        // Determine radius of protein
                        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                        int tmpTotalNumberOfParticlesOfProtein = tmpSpices.getTotalNumberOfParticles();
                        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                        double tmpRadiusOfProtein = this.miscUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticlesOfProtein,
                                this.compartmentContainer.getDensityInfoValueItem().getValueAsDouble());
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                            return returnCancelled();
                        }
                        // </editor-fold>
                        // Set tmpParticleToGraphicalParticleInterfaceMap
                        HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleMap = this.moleculeToParticlesMap.get(tmpMoleculeName);
                        HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleInterfaceMap
                                = new HashMap<String, IGraphicalParticle>(tmpParticleToGraphicalParticleMap.size());
                        for (String tmpParticle : tmpParticleToGraphicalParticleMap.keySet()) {
                            tmpParticleToGraphicalParticleInterfaceMap.put(tmpParticle, tmpParticleToGraphicalParticleMap.get(tmpParticle));
                        }
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                            return returnCancelled();
                        }
                        // </editor-fold>
                        // Determine non-overlapping spheres for proteins
                        LinkedList<BodySphere> tmpNonOverlappingSphereList = 
                            this.compartmentContainer.getCompartmentBox().getNonOverlappingRandomSpheres(
                                tmpQuantity, 
                                tmpRadiusOfProtein,
                                Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                tmpRandomNumberGenerator
                            );
                        for (BodySphere tmpSingleSphere : tmpNonOverlappingSphereList) {
                            tmpPdbToDpd.setCenter(tmpSingleSphere.getBodyCenter().getX(), tmpSingleSphere.getBodyCenter().getY(), tmpSingleSphere.getBodyCenter().getZ());
                            tmpPdbToDpd.setRadius(tmpSingleSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                            // Set tmpProteinGraphicalParticlePositionsArray
                            GraphicalParticlePosition[] tmpProteinGraphicalParticlePositionsArray = new GraphicalParticlePosition[tmpTotalNumberOfParticlesOfProtein];
                            double tmpInitialValue = 0.0;
                            for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                tmpProteinGraphicalParticlePositionsArray[j] = new GraphicalParticlePosition(tmpInitialValue, tmpInitialValue, tmpInitialValue);
                            }
                            if (tmpIsProteinRandom3DOrientation) {
                                tmpPdbToDpd.setRandomOrientation();
                            }
                            tmpPdbToDpd.getGraphicalParticlePositions(tmpParticleToGraphicalParticleInterfaceMap, tmpProteinGraphicalParticlePositionsArray);
                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                            if (this.isStopped) {
                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                return returnCancelled();
                            }
                            // </editor-fold>
                            // Copy to this.graphicalParticlePositions
                            for (int j = 0; j < tmpProteinGraphicalParticlePositionsArray.length; j++) {
                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpProteinGraphicalParticlePositionsArray[j];
                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(true);
                            }
                        }
                        PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                            return returnCancelled();
                        }
                        // </editor-fold>
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="No protein data">
                        GraphicalParticlePosition[] tmpFirstParticleCoordinates = new GraphicalParticlePosition[tmpQuantity];
                        if (tmpIsSingleParticleMolecule) {
                            this.compartmentContainer.getCompartmentBox().fillFreeVolumeRandomPoints(
                                tmpFirstParticleCoordinates, 
                                0, 
                                tmpQuantity, 
                                Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                tmpRandomNumberGenerator
                            );
                        } else {
                            this.compartmentContainer.getCompartmentBox().fillFreeVolumeRandomPoints(
                                tmpFirstParticleCoordinates, 
                                0, 
                                tmpQuantity, 
                                Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                tmpRandomNumberGenerator
                            );
                        }
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                            return returnCancelled();
                        }
                        // </editor-fold>
                        GraphicalParticlePosition[] tmpLastParticleCoordinates;
                        if (tmpIsSingleParticleMolecule) {
                            tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                        } else {
                            tmpLastParticleCoordinates = new GraphicalParticlePosition[tmpQuantity];
                            this.compartmentContainer.getCompartmentBox().fillFreeVolumeRandomPoints(
                                tmpLastParticleCoordinates, 
                                0, 
                                tmpQuantity, 
                                Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                tmpRandomNumberGenerator
                            );
                        }
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                            return returnCancelled();
                        }
                        // </editor-fold>
                        // Calculate coordinates of molecular particles
                        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                        GraphicalParticlePosition[][] tmpGraphicalParticlePositionsArray = 
                            tmpSpices.getParticleCoordinates(
                                this.moleculeToParticlesMap.get(tmpMoleculeName), 
                                tmpFirstParticleCoordinates,
                                tmpLastParticleCoordinates, 
                                tmpBondLength
                            );
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            // IMPORTANT: Restore matrix rows that were sorted with protein data first
                            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                            return returnCancelled();
                        }
                        // </editor-fold>
                        for (int j = 0; j < tmpGraphicalParticlePositionsArray.length; j++) {
                            // NOTE: Particle in bulk is not allowed to be in compartment: Correct errors
                            // <editor-fold defaultstate="collapsed" desc="Correct coordinates">
                            GraphicalParticlePosition[] tmpCorrectGraphicalParticlePositions = tmpGraphicalParticlePositionsArray[j];
                            boolean tmpIsCorrect = false;
                            while (!tmpIsCorrect) {
                                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                                if (this.isStopped) {
                                    // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                    tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                                    return returnCancelled();
                                }
                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="Check coordinates">
                                int tmpCorrectIndex = 0;
                                for (int k = 0; k < tmpCorrectGraphicalParticlePositions.length; k++) {
                                    if (!this.compartmentContainer.getCompartmentBox().isInFreeVolume(tmpCorrectGraphicalParticlePositions[k])) {
                                        break;
                                    } else {
                                        tmpCorrectIndex = k;
                                    }
                                }
                                tmpIsCorrect = tmpCorrectIndex == tmpCorrectGraphicalParticlePositions.length - 1;
                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="Correct if necessary">
                                if (!tmpIsCorrect) {
                                    if (tmpCorrectIndex > 0) {
                                        // <editor-fold defaultstate="collapsed" desc="Shrink molecule">
                                        // NOTE: tmpCorrectGraphicalParticlePositions[0] MUST be correct since it is deduced from 
                                        //       tmpFirstParticleCoordinates which are all in free volume by definition
                                        GraphicalParticlePosition[][] tmpNewGraphicalParticlePositionArray = 
                                            tmpSpices.getParticleCoordinates(
                                                this.moleculeToParticlesMap.get(tmpMoleculeName),
                                                tmpCorrectGraphicalParticlePositions[0], 
                                                tmpCorrectGraphicalParticlePositions[tmpCorrectIndex], 
                                                tmpBondLength
                                            );
                                        tmpCorrectGraphicalParticlePositions = tmpNewGraphicalParticlePositionArray[0];
                                        tmpIsCorrect = true;
                                        // </editor-fold>
                                    } else {
                                        // <editor-fold defaultstate="collapsed" desc="Get other orientation">
                                        GraphicalParticlePosition tmpNewLastParticleCoordinate = 
                                            this.compartmentContainer.getCompartmentBox().getRandomPositionInFreeVolume(tmpRandomNumberGenerator);
                                        // NOTE: tmpCorrectGraphicalParticlePositions[0] MUST be correct since it is deduced from 
                                        //       tmpFirstParticleCoordinates which are all in free volume by definition
                                        GraphicalParticlePosition[][] tmpNewGraphicalParticlePositionArray = 
                                            tmpSpices.getParticleCoordinates(
                                                this.moleculeToParticlesMap.get(tmpMoleculeName),
                                                tmpCorrectGraphicalParticlePositions[0], 
                                                tmpNewLastParticleCoordinate, 
                                                tmpBondLength
                                            );
                                        tmpCorrectGraphicalParticlePositions = tmpNewGraphicalParticlePositionArray[0];
                                        // </editor-fold>
                                    }
                                }
                                // </editor-fold>
                            }
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                            if (this.isStopped) {
                                // IMPORTANT: Restore matrix rows that were sorted with protein data first
                                tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                                return returnCancelled();
                            }
                            // </editor-fold>
                            for (int k = 0; k < tmpCorrectGraphicalParticlePositions.length; k++) {
                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex] = tmpCorrectGraphicalParticlePositions[k];
                                this.graphicalParticlePositions[tmpGraphicalParticlePositionsIndex++].setInBulk(true);
                            }
                        }
                        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                        // </editor-fold>
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                if (this.isStopped) {
                    // IMPORTANT: Restore matrix rows that were sorted with protein data first
                    tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
                    return returnCancelled();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set progress">
                this.setProgressValue(tmpMiscUtilityMethods.getPercentWithMax99(tmpGraphicalParticlePositionsIndex + 1, tmpTotalNumberOfParticlesInSimulation));
                // </editor-fold>
            }
            // <editor-fold defaultstate="collapsed" desc="Restore matrix rows that were sorted with protein data first">
            tmpBulkInfoValueItem.restoreOriginalMatrixRowsAfterSorting();
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check if this.graphicalParticlePositions is completely filled">
            if (this.graphicalParticlePositions[this.graphicalParticlePositions.length - 1] == null) {
                // Fire property change to notify property change listeners about cancellation due to internal error
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                return returnCancelled();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this.graphicalParticlePositionInfo">
            // Parameter true: Compartments/Bulk exist
            // Parameter -1: NOT defined
            this.graphicalParticlePositionInfo = 
                new GraphicalParticlePositionInfo(
                    new GraphicalParticleInfo(
                        this.moleculeToParticlesMap, 
                        true
                    ), 
                    this.graphicalParticlePositions,
                    this.compartmentContainer.getCompartmentBox().getBoxSizeInfo(), 
                    this.compartmentContainer.getLengthConversionFactor(),
                    -1,
                    -1
                );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this.isFinished to true BEFORE setting final progress in percent to 100">
            this.isFinished = true;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task has successfully finished. IMPORTANT: Set progress in percent to 100">
            this.setProgressValue(100);
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // Fire property change to notify property change listeners about cancellation due to internal error
            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
            return this.returnCancelled();
        } finally {
            // <editor-fold defaultstate="collapsed" desc="Release memory">
            this.releaseMemory();
            // </editor-fold>
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Sets internal variables according to internal cancellation and returns
     * false
     *
     * @return False
     */
    private boolean returnCancelled() {
        this.releaseMemory();
        this.isFinished = true;
        return false;
    }
    
    /**
     * Release memory
     */
    private void releaseMemory() {
        this.moleculeToParticlesMap = null;
        this.graphicalParticlePositions = null;
        this.compartmentContainer = null;
        this.particlePattern = null;
        // NOTE: Do NOT release this.graphicalParticlePositionInfo since this is a return property
    }

    /**
     * Set progress value and fire property change
     * 
     * @param aNewValue New value
     */
    private void setProgressValue(int aNewValue) {
        int tmpOldValue = this.progressValue;
        this.progressValue = aNewValue;
        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_PROGRESS, tmpOldValue, this.progressValue);
    }
    // </editor-fold>

}
