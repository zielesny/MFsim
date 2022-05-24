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
package de.gnwi.mfsim.model.util;

import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.MoleculeSelectionManager;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;

/**
 * Configuration for movie slicer
 *
 * @author Achim Zielesny
 */
public class MovieSlicerConfiguration {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Value item container for molecule/particles display settings
     */
    private ValueItemContainer moleculeDisplaySettingsValueItemContainer;

    /**
     * Exclusion box size info
     */
    private BoxSizeInfo exclusionBoxSizeInfo;

    /**
     * Molecule selection manager
     */
    private MoleculeSelectionManager moleculeSelectionManager;
    
    /**
     * Chart configuration
     */
    private ChartConfiguration chartConfiguration;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public MovieSlicerConfiguration() {
        this.clear();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Clears configuration
     */
    public void clear() {
        this.moleculeDisplaySettingsValueItemContainer = null;
        this.exclusionBoxSizeInfo = null;
        this.moleculeSelectionManager = null;
        this.chartConfiguration = null;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- MoleculeDisplaySettingsValueItemContainer">
    /**
     * Value item container for molecule/particles display settings
     *
     * @return Value item container for molecule/particles display settings
     */
    public ValueItemContainer getMoleculeDisplaySettingsValueItemContainer() {
        return this.moleculeDisplaySettingsValueItemContainer;
    }

    /**
     * Value item container for molecule/particles display settings
     *
     * @param aMoleculeDisplaySettingsValueItemContainer Value item container for molecule/particles display settings
     */
    public void setMoleculeDisplaySettingsValueItemContainer(ValueItemContainer aMoleculeDisplaySettingsValueItemContainer) {
        this.moleculeDisplaySettingsValueItemContainer = aMoleculeDisplaySettingsValueItemContainer;
    }

    /**
     * Returns if value item container for molecule/particles display settings is set
     *
     * @return True: Value item container for molecule/particles display settings is set, false: Otherwise
     */
    public boolean hasMoleculeDisplaySettingsValueItemContainer() {
        return this.moleculeDisplaySettingsValueItemContainer != null;
    }

    /**
     * Clears value item container for molecule/particles display settings
     */
    public void clearMoleculeDisplaySettingsValueItemContainer() {
        this.moleculeDisplaySettingsValueItemContainer = null;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ExclusionBoxSizeInfo">
    /**
     * Exclusion box size info
     *
     * @return Exclusion box size info
     */
    public BoxSizeInfo getExclusionBoxSizeInfo() {
        return this.exclusionBoxSizeInfo;
    }

    /**
     * Exclusion box size info
     *
     * @param anExclusionBoxSizeInfo Exclusion box size info
     */
    public void setExclusionBoxSizeInfo(BoxSizeInfo anExclusionBoxSizeInfo) {
        this.exclusionBoxSizeInfo = anExclusionBoxSizeInfo;
    }

    /**
     * Returns if exclusion box size info is set
     *
     * @return True: Exclusion box size info is set, false: Otherwise
     */
    public boolean hasExclusionBoxSizeInfo() {
        return this.exclusionBoxSizeInfo != null;
    }

    /**
     * Removes exclusion box size info
     */
    public void removeExclusionBoxSizeInfo() {
        this.exclusionBoxSizeInfo = null;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MoleculeSelectionManager">
    /**
     * Molecule selection manager
     *
     * @return Molecule selection manager
     */
    public MoleculeSelectionManager getMoleculeSelectionManager() {
        return this.moleculeSelectionManager;
    }

    /**
     * Molecule selection manager
     *
     * @param aMoleculeSelectionManager Molecule selection manager
     */
    public void setMoleculeSelectionManager(MoleculeSelectionManager aMoleculeSelectionManager) {
        this.moleculeSelectionManager = aMoleculeSelectionManager;
    }

    /**
     * Returns if molecule selection manager is set
     *
     * @return True: Molecule selection manager is set, false: Otherwise
     */
    public boolean hasMoleculeSelectionManager() {
        return this.moleculeSelectionManager != null;
    }

    /**
     * Removes molecule selection manager
     */
    public void removeMoleculeSelectionManager() {
        this.moleculeSelectionManager = null;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ChartConfiguration">
    /**
     * Chart configuration
     *
     * @return Chart configuration
     */
    public ChartConfiguration getChartConfiguration() {
        return this.chartConfiguration;
    }

    /**
     * Chart configuration
     *
     * @param aChartConfiguration Chart configuration
     */
    public void setChartConfiguration(ChartConfiguration aChartConfiguration) {
        this.chartConfiguration = aChartConfiguration;
    }

    /**
     * Returns if chart configuration is set
     *
     * @return True: Chart configuration is set, false: Otherwise
     */
    public boolean hasChartConfiguration() {
        return this.chartConfiguration != null;
    }

    /**
     * Removes chart configuration
     */
    public void removeChartConfiguration() {
        this.chartConfiguration = null;
    }
    // </editor-fold>
    // </editor-fold>

}
