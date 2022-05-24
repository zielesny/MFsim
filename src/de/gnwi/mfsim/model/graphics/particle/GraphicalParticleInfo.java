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

import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.Color;
import java.util.*;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Graphical particles with additional information
 *
 * @author Achim Zielesny
 */
public class GraphicalParticleInfo {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * HashMap that maps molecule name to its particle hashmap that maps a
     * particle to its graphical particle
     */
    private HashMap<String, HashMap<String, IGraphicalParticle>> moleculeToParticlesMap;

    /**
     * True: Compartment and bulk related value items are used, false: Otherwise
     */
    private boolean hasCompartments;

    /**
     * List of particles
     */
    private LinkedList<String> particleList;

    /**
     * List with names of molecules
     */
    private LinkedList<String> moleculeList;

    /**
     * List with molecule-particle strings (see definition in class
 GraphicalParticle)
     */
    private LinkedList<String> moleculeParticleStringList;

    /**
     * List with all graphical particles of moleculeToParticlesMap for all
     * molecules
     */
    private LinkedList<IGraphicalParticle> graphicalParticleList;

    /**
     * HashMap that maps particle to its particle name
     */
    private HashMap<String, String> particleToParticleNameMap;

    /**
     * HashMap that maps particle to its current color
     */
    private HashMap<String, Color> particleToCurrentColorMap;

    /**
     * HashMap that maps particle to its initial color
     */
    private HashMap<String, Color> particleToInitialColorMap;

    /**
     * HashMap that maps molecule to its current color
     */
    private HashMap<String, Color> moleculeToCurrentColorMap;

    /**
     * HashMap that maps molecule to its initial color
     */
    private HashMap<String, Color> moleculeToInitialColorMap;

    /**
     * HashMap that maps molecule-particle string to its current color
     */
    private HashMap<String, Color> moleculeParticleStringToCurrentColorMap;

    /**
     * HashMap that maps molecule-particle string to its initial color
     */
    private HashMap<String, Color> moleculeParticleStringToInitialColorMap;

    /**
     * HashMap that maps molecule-particle string to its current radius scale
     */
    private HashMap<String, Double> moleculeParticleStringToCurrentRadiusScale;

    /**
     * HashMap that maps molecule-particle string to its current transparency
     */
    private HashMap<String, Float> moleculeParticleStringToCurrentTransparency;

    /**
     * HashMap that contains excluded particles (key = value = particle)
     */
    private HashMap<String, String> excludedParticlesTable;

    /**
     * HashMap that contains excluded molecules (key = value = molecule) as well
     * as info about bulk/compartments display if available
     */
    private HashMap<String, String> excludedMoleculesTable;

    /**
     * HashMap that contains excluded molecule-particles (key = value =
 MoleculeParticleString as defined in class GraphicalParticle)
     */
    private HashMap<String, String> excludedMoleculeParticleStringTable;

    /**
     * HashMap that maps molecule (name) to array of its graphical particles
     */
    private HashMap<String, GraphicalParticle[]> moleculeToGraphicalParticleArrayMap;

    /**
     * HashMap that maps molecule-particle string (see definition in class
 GraphicalParticle) to its graphical particle
     */
    private HashMap<String, GraphicalParticle> moleculeParticleStringToGraphicalParticleMap;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor. NOTE: There are no checks performed!
     *
     * @param aMoleculeToParticlesMap HashMap that maps molecule name to its
     * particle hashmap that maps a particle to its graphical particle.
     * @param aHasCompartments True: Compartment and bulk related value items
     * are used, false: Otherwise
     */
    public GraphicalParticleInfo(HashMap<String, HashMap<String, IGraphicalParticle>> aMoleculeToParticlesMap, boolean aHasCompartments) {
        this.moleculeToParticlesMap = aMoleculeToParticlesMap;
        this.hasCompartments = aHasCompartments;
        this.initialize();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Clear">
    /**
     * Clear
     */
    public void clear() {
        // <editor-fold defaultstate="collapsed" desc="Set this.moleculeList">
        this.moleculeList = new LinkedList<String>();
        this.moleculeParticleStringList = new LinkedList<String>();
        this.moleculeToGraphicalParticleArrayMap = new HashMap<String, GraphicalParticle[]>();
        // Number of molecule particles is O(100): Set capacity to 1000
        this.moleculeParticleStringToGraphicalParticleMap = new HashMap<String, GraphicalParticle>(1000);
        Set<String> tmpMoleculeSet = this.moleculeToParticlesMap.keySet();
        Iterator<String> tmpMoleculeIterator = tmpMoleculeSet.iterator();
        while (tmpMoleculeIterator.hasNext()) {
            String tmpMolecule = tmpMoleculeIterator.next();
            this.moleculeList.add(tmpMolecule);
            GraphicalParticle[] tmpGraphicalParticlesOfMolecule = this.moleculeToParticlesMap.get(tmpMolecule).values().toArray(new GraphicalParticle[0]);
            this.moleculeToGraphicalParticleArrayMap.put(tmpMolecule, tmpGraphicalParticlesOfMolecule);
            for (GraphicalParticle tmpSingleGraphicalParticle : tmpGraphicalParticlesOfMolecule) {
                this.moleculeParticleStringList.add(tmpSingleGraphicalParticle.getMoleculeParticleString());
                this.moleculeParticleStringToGraphicalParticleMap.put(tmpSingleGraphicalParticle.getMoleculeParticleString(), tmpSingleGraphicalParticle);
            }
        }
        Collections.sort(this.moleculeList);
        Collections.sort(this.moleculeParticleStringList);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.graphicalParticleList, this.particleToParticleNameMap and this.particleList">
        this.particleList = new LinkedList<>();
        this.graphicalParticleList = new LinkedList<>();
        this.particleToParticleNameMap = new HashMap<>();
        Collection<HashMap<String, IGraphicalParticle>> tmpParticleMapCollection = this.moleculeToParticlesMap.values();
        for (HashMap<String, IGraphicalParticle> tmpParticleMap : tmpParticleMapCollection) {
            Collection<IGraphicalParticle> tmpGraphicalParticleCollection = tmpParticleMap.values();
            for (IGraphicalParticle tmpSingleGraphicalParticleWrapper : tmpGraphicalParticleCollection) {
                GraphicalParticle tmpSingleGraphicalParticle = (GraphicalParticle) tmpSingleGraphicalParticleWrapper;
                this.graphicalParticleList.add(tmpSingleGraphicalParticle);
                if (!this.particleToParticleNameMap.containsKey(tmpSingleGraphicalParticle.getParticle())) {
                    this.particleToParticleNameMap.put(tmpSingleGraphicalParticle.getParticle(), tmpSingleGraphicalParticle.getParticleName());
                    this.particleList.add(tmpSingleGraphicalParticle.getParticle());
                }
            }
        }
        Collections.sort(this.particleList);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.excludedParticlesTable">
        this.excludedParticlesTable = new HashMap<String, String>(this.graphicalParticleList.size());

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.excludedMoleculesTable">
        this.excludedMoleculesTable = new HashMap<String, String>(this.moleculeToParticlesMap.size());

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.excludedMoleculeParticlesTable">
        this.excludedMoleculeParticleStringTable = new HashMap<String, String>();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.moleculeToCurrentColorMap">
        this.moleculeToCurrentColorMap = new HashMap<String, Color>(this.moleculeToInitialColorMap.size());
        tmpMoleculeSet = this.moleculeToInitialColorMap.keySet();
        tmpMoleculeIterator = tmpMoleculeSet.iterator();
        while (tmpMoleculeIterator.hasNext()) {
            String tmpMolecule = tmpMoleculeIterator.next();
            this.moleculeToCurrentColorMap.put(tmpMolecule, this.moleculeToInitialColorMap.get(tmpMolecule));
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.particleToCurrentColorMap">
        this.particleToCurrentColorMap = new HashMap<String, Color>(this.particleToInitialColorMap.size());
        Set<String> tmpParticleSet = this.particleToInitialColorMap.keySet();
        Iterator<String> tmpParticleIterator = tmpParticleSet.iterator();
        while (tmpParticleIterator.hasNext()) {
            String tmpParticle = tmpParticleIterator.next();
            this.particleToCurrentColorMap.put(tmpParticle, this.particleToInitialColorMap.get(tmpParticle));
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.moleculeParticleStringToCurrentColorMap, this.moleculeParticleToCurrentRadiusScale and this.moleculeParticleStringToCurrentTransparency">
        this.moleculeParticleStringToCurrentColorMap = new HashMap<String, Color>(this.moleculeParticleStringToInitialColorMap.size());
        this.moleculeParticleStringToCurrentRadiusScale = new HashMap<String, Double>(this.moleculeParticleStringToInitialColorMap.size());
        this.moleculeParticleStringToCurrentTransparency = new HashMap<String, Float>(this.moleculeParticleStringToInitialColorMap.size());
        Set<String> tmpMoleculeParticleStringSet = this.moleculeParticleStringToInitialColorMap.keySet();
        Iterator<String> tmpMoleculeParticleStringIterator = tmpMoleculeParticleStringSet.iterator();
        while (tmpMoleculeParticleStringIterator.hasNext()) {
            String tmpMoleculeParticleString = tmpMoleculeParticleStringIterator.next();
            this.moleculeParticleStringToCurrentColorMap.put(tmpMoleculeParticleString, this.moleculeParticleStringToInitialColorMap.get(tmpMoleculeParticleString));
            // Initialize this.moleculeParticleToCurrentRadiusScale to 100%
            this.moleculeParticleStringToCurrentRadiusScale.put(tmpMoleculeParticleString, Double.valueOf(100.0));
            // Initialize this.moleculeParticleToCurrentTransparency to 0% (NO transparency, i.e. opaque display)
            this.moleculeParticleStringToCurrentTransparency.put(tmpMoleculeParticleString, Float.valueOf(0f));
        }

        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Color and radius scale of particle related methods">
    /**
     * Sets current color and scaled radius of all GraphicalParticle instances
 of this.graphicalParticleList according to
 this.particleToCurrentColorMap, this.moleculeToCurrentColorMap,
 this.moleculeParticleStringToCurrentColorMap,
 this.moleculeParticleStringToCurrentRadiusScale and
 this.moleculeParticleStringToCurrentTransparency
     */
    public void setCurrentColorAndScaledRadiusOfParticles() {
        for (IGraphicalParticle tmpGraphicalParticleWrapper : this.graphicalParticleList) {
            GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpGraphicalParticleWrapper;
            // <editor-fold defaultstate="collapsed" desc="Set color">
            tmpGraphicalParticle.setParticleColor(this.particleToCurrentColorMap.get(tmpGraphicalParticle.getParticle()));
            tmpGraphicalParticle.setMoleculeColor(this.moleculeToCurrentColorMap.get(tmpGraphicalParticle.getMoleculeName()));
            tmpGraphicalParticle.setMoleculeParticleColor(this.moleculeParticleStringToCurrentColorMap.get(tmpGraphicalParticle.getMoleculeParticleString()));
            // FINALLY set current particle color according to preferences AFTER setting of other colors
            tmpGraphicalParticle.setCurrentParticleColor();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set scaled current radius">
            tmpGraphicalParticle.setCurrentParticleRadius(
                tmpGraphicalParticle.getParticleRadius() * this.moleculeParticleStringToCurrentRadiusScale.get(tmpGraphicalParticle.getMoleculeParticleString()).doubleValue() / 100.0
            );

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set current transparency">
            tmpGraphicalParticle.setCurrentParticleTransparency(
                this.moleculeParticleStringToCurrentTransparency.get(tmpGraphicalParticle.getMoleculeParticleString()).floatValue()
            );
            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule display settings value items related methods">
    /**
     * Returns value item container with value items for molecule settings
     *
     * @return Value item container with value items for molecule settings or
     * null if none are available
     */
    public ValueItemContainer getMoleculeDisplaySettingsValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(null);
        int tmpVerticalPosition = 0;
        String[] tmpRootNodeNames = new String[]{ModelMessage.get("MoleculeDisplaySettings.Root")};
        this.graphicsUtilityMethods.addMoleculeDisplaySettingsValueItems(
                tmpValueItemContainer,
                tmpVerticalPosition,
                tmpRootNodeNames,
                this.particleList,
                this.particleToParticleNameMap,
                this.excludedParticlesTable,
                this.particleToCurrentColorMap,
                this.particleToInitialColorMap,
                this.moleculeList,
                this.excludedMoleculesTable,
                this.moleculeToCurrentColorMap,
                this.moleculeToInitialColorMap,
                this.moleculeParticleStringList,
                this.moleculeParticleStringToGraphicalParticleMap,
                this.excludedMoleculeParticleStringTable,
                this.moleculeParticleStringToCurrentColorMap,
                this.moleculeParticleStringToInitialColorMap,
                this.moleculeParticleStringToCurrentRadiusScale,
                this.moleculeParticleStringToCurrentTransparency,
                this.hasCompartments);
        return tmpValueItemContainer;
    }

    /**
     * Fills value item container with value items for molecule display settings
     *
     * @param aValueItemContainer Value item container to be filled with value
     * items for molecule settings
     * @param aVerticalPosition Vertical position for first value item to be
     * added
     * @param aRootNodeNames Root node names for value items to be added
     * @return Vertical position for next value item or -1 if molecule display
     * settings value items could not be added
     */
    public int addMoleculeDisplaySettingsValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition, String[] aRootNodeNames) {
        return this.graphicsUtilityMethods.addMoleculeDisplaySettingsValueItems(
            aValueItemContainer,
            aVerticalPosition,
            aRootNodeNames,
            this.particleList,
            this.particleToParticleNameMap,
            this.excludedParticlesTable,
            this.particleToCurrentColorMap,
            this.particleToInitialColorMap,
            this.moleculeList,
            this.excludedMoleculesTable,
            this.moleculeToCurrentColorMap,
            this.moleculeToInitialColorMap,
            this.moleculeParticleStringList,
            this.moleculeParticleStringToGraphicalParticleMap,
            this.excludedMoleculeParticleStringTable,
            this.moleculeParticleStringToCurrentColorMap,
            this.moleculeParticleStringToInitialColorMap,
            this.moleculeParticleStringToCurrentRadiusScale,
            this.moleculeParticleStringToCurrentTransparency,
            this.hasCompartments
        );
    }

    /**
     * Sets exclusions and current colors according to information in
     * aMoleculeDisplaySettingsValueItemContainer
     *
     * @param aMoleculeDisplaySettingsValueItemContainer Value item container
     * for molecule settings (configured and created with method
     * UtilityGraphics.getMoleculeSettingsValueItemContainer(), is NOT changed)
     */
    public void setExclusionsColorsAndPreferences(ValueItemContainer aMoleculeDisplaySettingsValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeDisplaySettingsValueItemContainer == null || aMoleculeDisplaySettingsValueItemContainer.getSize() == 0) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set excluded molecules and bulk/compartments">
        this.excludedMoleculesTable = new HashMap<String, String>();
        // Set for molecules
        ValueItem tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MoleculeTable");
        if (tmpValueItem != null) {
            // Set for molecules
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                if (tmpValueItem.getValue(i, 1).equals(ModelMessage.get("MoleculeDisplaySettings.Off"))) {
                    this.excludedMoleculesTable.put(tmpValueItem.getValue(i, 0), tmpValueItem.getValue(i, 0));
                }
            }
        }
        // Set for bulk
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_BULK");
        if (tmpValueItem != null && tmpValueItem.getValue().equals(ModelMessage.get("MoleculeDisplaySettings.Off"))) {
            this.excludedMoleculesTable.put(tmpValueItem.getName(), tmpValueItem.getName());
        }
        // Set for compartments
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_COMPARTMENTS");
        if (tmpValueItem != null && tmpValueItem.getValue().equals(ModelMessage.get("MoleculeDisplaySettings.Off"))) {
            this.excludedMoleculesTable.put(tmpValueItem.getName(), tmpValueItem.getName());
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set excluded particles">
        this.excludedParticlesTable = new HashMap<String, String>();
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES");
        if (tmpValueItem != null) {
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                if (tmpValueItem.getValue(i, 2).equals(ModelMessage.get("MoleculeDisplaySettings.Off"))) {
                    this.excludedParticlesTable.put(tmpValueItem.getValue(i, 0), tmpValueItem.getValue(i, 0));
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set excluded molecule-particles">
        this.excludedMoleculeParticleStringTable = new HashMap<String, String>();
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES_IN_MOLECULES");
        if (tmpValueItem != null) {
            // IMPORTANT: Get tmpMoleculeParticleStrings as supplementary data. NOTE: tmpMoleculeParticleStrings[i] corresponds to row i of value item matrix.
            String[] tmpMoleculeParticleStrings = tmpValueItem.getSupplementaryData();
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                if (tmpValueItem.getValue(i, 3).equals(ModelMessage.get("MoleculeDisplaySettings.Off"))) {
                    this.excludedMoleculeParticleStringTable.put(tmpMoleculeParticleStrings[i], tmpMoleculeParticleStrings[i]);
                }
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set current molecule colors">
        this.moleculeToCurrentColorMap = new HashMap<String, Color>();
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MoleculeTable");
        if (tmpValueItem != null) {
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                this.moleculeToCurrentColorMap.put(tmpValueItem.getValue(i, 0), StandardColorEnum.toStandardColor(tmpValueItem.getValue(i, 2)).toColor());
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set current particle colors">
        this.particleToCurrentColorMap = new HashMap<String, Color>();
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES");
        if (tmpValueItem != null) {
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                this.particleToCurrentColorMap.put(tmpValueItem.getValue(i, 0), StandardColorEnum.toStandardColor(tmpValueItem.getValue(i, 3)).toColor());
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set current molecule-particles colors">
        this.moleculeParticleStringToCurrentColorMap = new HashMap<String, Color>(this.moleculeParticleStringToInitialColorMap.size());
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES_IN_MOLECULES");
        if (tmpValueItem != null) {
            // IMPORTANT: Get tmpMoleculeParticleStrings as supplementary data. NOTE: tmpMoleculeParticleStrings[i] corresponds to row i of value item matrix.
            String[] tmpMoleculeParticleStrings = tmpValueItem.getSupplementaryData();
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                this.moleculeParticleStringToCurrentColorMap.put(tmpMoleculeParticleStrings[i], StandardColorEnum.toStandardColor(tmpValueItem.getValue(i, 4)).toColor());
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set current molecule-particles radii scales">
        this.moleculeParticleStringToCurrentRadiusScale = new HashMap<String, Double>(this.moleculeParticleStringToInitialColorMap.size());
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES_IN_MOLECULES");
        if (tmpValueItem != null) {
            // IMPORTANT: Get tmpMoleculeParticleStrings as supplementary data. NOTE: tmpMoleculeParticleStrings[i] corresponds to row i of value item matrix.
            String[] tmpMoleculeParticleStrings = tmpValueItem.getSupplementaryData();
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                this.moleculeParticleStringToCurrentRadiusScale.put(tmpMoleculeParticleStrings[i], Double.valueOf(tmpValueItem.getValueAsDouble(i, 5)));
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set current molecule-particles transparencies">
        this.moleculeParticleStringToCurrentTransparency = new HashMap<String, Float>(this.moleculeParticleStringToInitialColorMap.size());
        tmpValueItem = aMoleculeDisplaySettingsValueItemContainer.getValueItem(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES_IN_MOLECULES");
        if (tmpValueItem != null) {
            // IMPORTANT: Get tmpMoleculeParticleStrings as supplementary data. NOTE: tmpMoleculeParticleStrings[i] corresponds to row i of value item matrix.
            String[] tmpMoleculeParticleStrings = tmpValueItem.getSupplementaryData();
            for (int i = 0; i < tmpValueItem.getMatrixRowCount(); i++) {
                // Safeguard:
                if (tmpValueItem.getMatrixColumnCount() < 7) {
                    this.moleculeParticleStringToCurrentTransparency.put(tmpMoleculeParticleStrings[i], Float.valueOf(0f));
                } else {
                    this.moleculeParticleStringToCurrentTransparency.put(tmpMoleculeParticleStrings[i], Float.valueOf(tmpValueItem.getValueAsFloat(i, 6)));
                }
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set basic preferences">
        Preferences.getInstance().setEditablePreferences(aMoleculeDisplaySettingsValueItemContainer);

        // </editor-fold>
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * HashMap that contains excluded particles (key = value = particle)
     *
     * @return HashMap that contains excluded particles (key = value = particle)
     */
    public HashMap<String, String> getExcludedParticlesTable() {
        return this.excludedParticlesTable;
    }

    /**
     * HashMap that contains excluded molecules (key = value = molecule) as well
     * as info about bulk/compartments display if available
     *
     * @return HashMap that contains excluded molecules (key = value = molecule)
     * as well as info about bulk/compartments display if available
     */
    public HashMap<String, String> getExcludedMoleculesTable() {
        return this.excludedMoleculesTable;
    }

    /**
     * HashMap that contains excluded molecule-particles (key = value =
     * molecule-particle string)
     *
     * @return HashMap that contains excluded molecule-particles (key =
     * value = molecule-particle string) if available
     */
    public HashMap<String, String> getExcludedMoleculeParticleStringTable() {
        return this.excludedMoleculeParticleStringTable;
    }

    /**
     * List with all graphical particles of moleculeToParticlesMap for all
     * molecules
     *
     * @return List with all graphical particles of moleculeToParticlesMap for
     * all molecules
     */
    public LinkedList<IGraphicalParticle> getGraphicalParticleList() {
        return this.graphicalParticleList;
    }

    /**
     * HashMap that maps molecule-particle string (see definition in class 
     * GraphicalParticle) to its graphical particle
     * 
     * @return HashMap that maps molecule-particle string to its graphical particle
     */
    public HashMap<String, GraphicalParticle> getMoleculeParticleStringToGraphicalParticleMap() {
        return this.moleculeParticleStringToGraphicalParticleMap;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialize
     */
    private void initialize() {
        // <editor-fold defaultstate="collapsed" desc="Set this.moleculeToInitialColorMap">
        this.moleculeToInitialColorMap = new HashMap<String, Color>(this.moleculeToParticlesMap.size());
        Set<String> tmpMoleculeSet = this.moleculeToParticlesMap.keySet();
        Iterator<String> tmpMoleculeIterator = tmpMoleculeSet.iterator();
        while (tmpMoleculeIterator.hasNext()) {
            String tmpMolecule = tmpMoleculeIterator.next();
            HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleMap = this.moleculeToParticlesMap.get(tmpMolecule);
            Set<String> tmpParticleSet = tmpParticleToGraphicalParticleMap.keySet();
            Iterator<String> tmpParticleIterator = tmpParticleSet.iterator();
            GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpParticleToGraphicalParticleMap.get(tmpParticleIterator.next());
            this.moleculeToInitialColorMap.put(tmpMolecule, tmpGraphicalParticle.getMoleculeColor());
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.particleToInitialColorMap">
        LinkedList<IGraphicalParticle> tmpGraphicalParticleList = new LinkedList<>();
        Collection<HashMap<String, IGraphicalParticle>> tmpParticleMapCollection = this.moleculeToParticlesMap.values();
        for (HashMap<String, IGraphicalParticle> tmpParticleMap : tmpParticleMapCollection) {
            Collection<IGraphicalParticle> tmpGraphicalParticleCollection = tmpParticleMap.values();
            for (IGraphicalParticle tmpSingleGraphicalParticle : tmpGraphicalParticleCollection) {
                tmpGraphicalParticleList.add(tmpSingleGraphicalParticle);
            }
        }
        this.particleToInitialColorMap = new HashMap<String, Color>(tmpGraphicalParticleList.size());
        for (IGraphicalParticle tmpSingleGraphicalParticleWrapper : tmpGraphicalParticleList) {
            GraphicalParticle tmpSingleGraphicalParticle = (GraphicalParticle) tmpSingleGraphicalParticleWrapper;
            if (!this.particleToInitialColorMap.containsKey(tmpSingleGraphicalParticle.getParticle())) {
                this.particleToInitialColorMap.put(tmpSingleGraphicalParticle.getParticle(), tmpSingleGraphicalParticle.getParticleColor());
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.moleculeParticleStringToInitialColorMap">
        // Number of molecule particle strings is O(100): Set capacity to 1000
        this.moleculeParticleStringToInitialColorMap = new HashMap<String, Color>(1000);
        tmpMoleculeSet = this.moleculeToParticlesMap.keySet();
        tmpMoleculeIterator = tmpMoleculeSet.iterator();
        while (tmpMoleculeIterator.hasNext()) {
            String tmpMolecule = tmpMoleculeIterator.next();
            for (IGraphicalParticle tmpSingleGraphicalParticleWrapper : this.moleculeToParticlesMap.get(tmpMolecule).values()) {
                GraphicalParticle tmpSingleGraphicalParticle = (GraphicalParticle) tmpSingleGraphicalParticleWrapper;
                // Set with molecule color
                this.moleculeParticleStringToInitialColorMap.put(tmpSingleGraphicalParticle.getMoleculeParticleString(), tmpSingleGraphicalParticle.getMoleculeColor());
            }
        }
        // </editor-fold>
        this.clear();
    }
    // </editor-fold>

}
