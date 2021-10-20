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
package de.gnwi.mfsim.model.job;

import de.gnwi.jdpd.utilities.Constants;
import de.gnwi.jdpd.utilities.Factory;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemUtils;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.spices.SpicesConstants;
import de.gnwi.mfsim.model.particle.StandardParticleDescription;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.JOptionPane;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Source for value item clones of an Jdpd input file
 *
 * @author Achim Zielesny
 */
public class JdpdValueItemDefinition {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * JdpdValueItemDefinition instance
     */
    private static JdpdValueItemDefinition jdpdValueItemDefinition = new JdpdValueItemDefinition();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String job methods
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * Value items. KEY: Value item name, Value: Value item instance (for
     * cloning purposes)
     */
    private HashMap<String, ValueItem> nameToValueItemMap;

    /**
     * Job input value item container
     */
    private ValueItemContainer jobInputValueItemContainer;

    /**
     * Hash map with names of value items for job restart edit
     */
    HashMap<String, String> valueItemNameMapForJobRestartEdit;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private singleton constructor">
    /**
     * Singleton constructor
     */
    private JdpdValueItemDefinition() {
        try {
            if (!this.initializeJdpdValueItems()) {
                // <editor-fold defaultstate="collapsed" desc="Jdpd value items are not available">
                JOptionPane.showMessageDialog(null, ModelMessage.get("Error.JdpdParameterInitialisationFailed"), ModelMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.exitApplication(-1);
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Jdpd value items are not available">
            JOptionPane.showMessageDialog(null, ModelMessage.get("Error.JdpdParameterInitialisationFailed"), ModelMessage.get("Error.ErrorNotificationTitle"),
                    JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);
            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public singleton instance method">
    /**
     * Singleton initialisation and instance method
     *
     * @return JdpdValueItemDefinition instance
     */
    public static JdpdValueItemDefinition getInstance() {
        return JdpdValueItemDefinition.jdpdValueItemDefinition;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Initialisation related methods">
    /**
     * Resets all definitions
     */
    public void reset() {
        JdpdValueItemDefinition.jdpdValueItemDefinition = new JdpdValueItemDefinition();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Get/is methods">
    /**
     * Returns clone of Jdpd input file value item with specified name
     *
     * @param aValueItemName Name of value item
     * @return Clone of Jdpd input file value item with specified name or
     * null if value item with specified name does not exist
     */
    public ValueItem getClonedJdpdInputFileValueItem(String aValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpValueItem = this.nameToValueItemMap.get(aValueItemName);
        if (tmpValueItem != null) {
            return tmpValueItem.getClone();
        } else {
            return null;
        }
    }

    /**
     * Returns cloned sorted Jdpd input file value items
     *
     * @return Cloned sorted Jdpd input file value items
     */
    public LinkedList<ValueItem> getSortedClonedJdpdInputFileValueItems() {
        LinkedList<ValueItem> tmpValueItemList = new LinkedList<ValueItem>();
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            tmpValueItemList.add(tmpSingleValueItem.getClone());
        }
        if (tmpValueItemList.size() > 0) {
            ValueItemUtils.sortValueItems(tmpValueItemList);
            return tmpValueItemList;
        } else {
            return null;
        }
    }

    /**
     * Returns cloned job input value item container
     *
     * @return Cloned job input value item container or null if none is
     * available
     */
    public ValueItemContainer getClonedJobInputValueItemContainer() {
        if (this.jobInputValueItemContainer == null) {
            return null;
        } else {
            return this.jobInputValueItemContainer.getClone();
        }
    }

    /**
     * Returns cloned sorted Jdpd input file value items of specified
     * block
     *
     * @param aBlockName Block name
     * @return Cloned sorted Jdpd input file value items of specified
     * block or null
     */
    public LinkedList<ValueItem> getSortedClonedJdpdInputFileValueItemsOfBlock(String aBlockName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        LinkedList<ValueItem> tmpValueItemList = new LinkedList<ValueItem>();
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            if (tmpSingleValueItem.getBlockName().equals(aBlockName)) {
                tmpValueItemList.add(tmpSingleValueItem.getClone());
            }
        }
        if (tmpValueItemList.size() > 0) {
            ValueItemUtils.sortValueItems(tmpValueItemList);
            return tmpValueItemList;
        } else {
            return null;
        }
    }

    /**
     * Returns if specified Jdpd input file value item is defined
     *
     * @param aValueItemName Name of value item
     * @return true: Jdpd input file value item is defined, false:
     * Otherwise
     */
    public boolean isJdpdInputFileValueItemDefined(String aValueItemName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return nameToValueItemMap.containsKey(aValueItemName);
    }

    /**
     * Returns hash map with value item names for job restart edit
     * 
     * @return Value item name map for job restart
     */
    public HashMap<String, String> getValueItemNameMapForJobRestartEdit() {
        return this.valueItemNameMapForJobRestartEdit;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialise value items
     *
     * @return True: Initialisation successful, false: Otherwise
     */
    private boolean initializeJdpdValueItems() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Initial definitions">
            // <editor-fold defaultstate="collapsed" desc="- General definitions">
            StringUtilityMethods tmpUtilityStringMethods = new StringUtilityMethods();
            ValueItem tmpValueItem;
            String[] tmpNodeNames;
            this.nameToValueItemMap = new HashMap<>(200);
            this.jobInputValueItemContainer = new ValueItemContainer(null);
            this.valueItemNameMapForJobRestartEdit = new HashMap<>(20);
            int tmpVerticalPosition = 1;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Particles, density, temperature, interactions">
            // <editor-fold defaultstate="collapsed" desc="-- Particles">
            String[] tmpParticles = StandardParticleInteractionData.getInstance().getAllParticlesSortedAscending();
            if (tmpParticles == null) {
                return false;
            }
            // Select water (H2O) particle as default if possible
            String tmpDefaultMoleculeName;
            String tmpDefaultParticle = StandardParticleInteractionData.getInstance().getDefaultWaterParticle();
            if (tmpDefaultParticle.toUpperCase(Locale.ENGLISH).equals("H2O")) {
                tmpDefaultMoleculeName = ModelMessage.get("JdpdInputFile.parameter.defaultMoleculeWater");
            } else {
                tmpDefaultMoleculeName = ModelMessage.get("JdpdInputFile.parameter.defaultMoleculeName");
            }
            StandardParticleDescription tmpDefaultParticleDescription = StandardParticleInteractionData.getInstance().getParticleDescription(tmpDefaultParticle);
            if (tmpDefaultParticleDescription == null) {
                return false;
            }
            String tmpDefaultParticlePair = tmpDefaultParticleDescription.getParticle() + SpicesConstants.PARTICLE_SEPARATOR + tmpDefaultParticleDescription.getParticle();
            String tmpDefaultParticlePairBondLength = "0.86";
            String tmpDefaultMoleculeParticle = tmpDefaultMoleculeName + SpicesConstants.PARTICLE_SEPARATOR + tmpDefaultParticle;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Density">
            String[] tmpDensities = new String[]{"3"};
            String tmpDefaultDensity = tmpDensities[0];
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Temperature">
            String tmpDefaultTemperature = StandardParticleInteractionData.getInstance().getTemperatureStrings()[0];
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Interactions">
            // Set to numeric null value
            String tmpInteractionValueErrorInformation = null;
            String tmpDefaultInteractionValue = StandardParticleInteractionData.getInstance().getInteraction(tmpDefaultParticleDescription.getParticle(), tmpDefaultParticleDescription.getParticle(),
                    tmpDefaultTemperature);
            if (tmpDefaultInteractionValue == null) {
                // Set error since interaction value is missing or may not be calculated
                tmpDefaultInteractionValue = ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString");
                tmpInteractionValueErrorInformation = ModelMessage.get("StandardParticleInteractionData.MissingInteractionValue");
            } else {
                tmpDefaultInteractionValue = tmpUtilityStringMethods.formatDoubleValue(tmpDefaultInteractionValue, ModelDefinitions.INTERACTION_NUMBER_OF_DECIMALS);
            }
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Box size related definitions">
            double tmpDefaultBoxSize = 20.0;
            double tmpBoxSizeMinimum = 0.1;
            double tmpBoxSizeMaximum = Double.POSITIVE_INFINITY;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Time step number related definitions">
            double tmpDefaultTimeStepNumber = 1000;
            String tmpDefaultTimeStepNumberRepresentation = "1000";
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Time step length related definitions">
            double tmpDefaultTimeStepLength = 0.04;
            String tmpDefaultTimeStepLengthRepresentation = "0.040";
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="General Job Description (block 0)">
            tmpNodeNames = new String[]{ModelMessage.get("JdpdInputFile.section.jobInput"), ModelMessage.get("JdpdInputFile.section.generalJobDescription")};
            // <editor-fold defaultstate="collapsed" desc="- Description (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Description");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Description"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_0);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Description"));
            // NOTE: No Jdpd input
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Timestamp (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Timestamp");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Timestamp"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TIMESTAMP));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_0);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Timestamp"));
            // NOTE: No Jdpd input
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- ParticleSetFilename (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("ParticleSetFilename");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.ParticleSetFilename"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(false));
            tmpValueItem.setValue(Preferences.getInstance().getCurrentParticleSetFilename());
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_0);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.ParticleSetFilename"));
            // NOTE: No Jdpd input
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- ApplicationVersion (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("ApplicationVersion");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.ApplicationVersion"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(false));
            tmpValueItem.setValue(String.format(ModelMessage.get("ApplicationVersionFormat"), ModelDefinitions.APPLICATION_VERSION));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_0);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.ApplicationVersion"));
            // NOTE: No Jdpd input
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Section PARTICLE_DESCRIPTION (block 1)">
            // tmpNodeNames are NOT necessary for ParticleTable since this value item is not displayed
            // <editor-fold defaultstate="collapsed" desc="- ParticleTable">
            tmpValueItem = StandardParticleInteractionData.getInstance().getParticleTableValueItemWithDefinedParticles(new String[]{tmpDefaultParticleDescription.getParticle()});
            // ParticleTable is NOT displayed
            tmpValueItem.setDisplay(false);
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_1);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Section CHEMICAL_SYSTEM_DESCRIPTION (block 2)">
            tmpNodeNames = 
                new String[] { 
                    ModelMessage.get("JdpdInputFile.section.jobInput"), 
                    ModelMessage.get("JdpdInputFile.section.chemicalSystemDescription")
                };
            // <editor-fold defaultstate="collapsed" desc="- MonomerTable (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MonomerTable");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MonomerTable"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setEssential(false);
            tmpValueItem.setActivity(false);
            tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JdpdInputFile.parameter.monomerName"),
                ModelMessage.get("JdpdInputFile.parameter.monomerStructure")});
            tmpValueItem.setMatrixColumnWidths(
                new String[] {
                    ModelDefinitions.CELL_WIDTH_TEXT_150,   // Monomer_Name
                    ModelDefinitions.CELL_WIDTH_TEXT_150    // Monomer_Structure
                }
            );
            // NOTE: Monomer_Name must have unique default (Parameter true).
            // IMPORTANT: See code of method ValueItemDataTypeFormat.getUniqueDefaultValue() to define reasonable default value and constraints. Otherwise method might fail.
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.defaultMonomerName"), true, "[A-Za-z0-9]", "^[A-Z][A-Za-z0-9]*"), // Monomer_Name
                new ValueItemDataTypeFormat(String.format(ModelMessage.get("JdpdInputFile.parameter.defaultMonomer"), tmpDefaultParticle), ValueItemEnumDataType.MONOMER_STRUCTURE) // Monomer_Structure
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MonomerTable"));
            // NOTE: No Jdpd input
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeTable">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeTable");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeTable"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"),
                    ModelMessage.get("JdpdInputFile.parameter.moleculeStructure"), 
                    ModelMessage.get("JdpdInputFile.parameter.standardColor")
                }
            );
            tmpValueItem.setMatrixColumnWidths(
                new String[] {
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecular_Structure
                    ModelDefinitions.CELL_WIDTH_TEXT_100  // Standard_Color
                }
            );
            // NOTE: Molecule_Name must have unique default (Parameter true). IMPORTANT: See code of method ValueItemDataTypeFormat.getUniqueDefaultValue() to define reasonable default value and
            // constraints. Otherwise method might fail. A molecule name is only allowed to contain 100 characters (this corresponds to the definition in the Jdpd).
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[] {
                    new ValueItemDataTypeFormat(tmpDefaultMoleculeName, true, "[A-Za-z0-9]", "^[A-Z][A-Za-z0-9]{0,99}"), // Molecule_Name
                    new ValueItemDataTypeFormat(tmpDefaultParticle, ValueItemEnumDataType.MOLECULAR_STRUCTURE), // Molecular_Structure
                    new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.defaultColor"), StandardColorEnum.getAllColorRepresentations()) // Graphics_Color
                }
            );
            tmpValueItem.setMatrixOutputOmitColumns(new boolean[]{
                false, // Molecule_Name
                false, // Molecular_Structure/PositionsBondsFile
                true   // Standard_Color
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeTable"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeCharge (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeCharge");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeCharge"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setUpdateNotifier(false);
            tmpValueItem.setMatrixColumnNames(
                new String[] {
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.positiveCharge"),
                    ModelMessage.get("JdpdInputFile.parameter.negativeCharge"),
                    ModelMessage.get("JdpdInputFile.parameter.netCharge")
                }
            );
            tmpValueItem.setMatrixColumnWidths(
                new String[] {
                    ModelDefinitions.CELL_WIDTH_TEXT_150,   // moleculeName
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80, // positiveCharge
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80, // negativeCharge
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80, // netCharge
                }
            );
            // NOTE: Molecule name is NOT editable
            // NOTE: Quantity 24000 comes from box size (20) and density (3): 20*20*20 = 8000, 8000*3 = 24000
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultMoleculeName, ValueItemEnumDataType.TEXT, false), // Molecule_Name
                new ValueItemDataTypeFormat("0", ValueItemEnumDataType.TEXT, false),                    // positiveCharge
                new ValueItemDataTypeFormat("0", ValueItemEnumDataType.TEXT, false),                    // negativeCharge
                new ValueItemDataTypeFormat("0", ValueItemEnumDataType.TEXT, false),                    // netCharge
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeCharge"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            tmpNodeNames = 
                new String[] { 
                    ModelMessage.get("JdpdInputFile.section.jobInput"), 
                    ModelMessage.get("JdpdInputFile.section.chemicalSystemDescription"),
                    ModelMessage.get("JdpdInputFile.section.simulationBox"), 
                    ModelMessage.get("JdpdInputFile.section.composition")
                };
            // <editor-fold defaultstate="collapsed" desc="- Density (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Density");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Density"));
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(tmpDefaultDensity, tmpDensities));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Density"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Concentration (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Concentration");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Concentration"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setEssential(false);
            tmpValueItem.setActivity(false);
            tmpValueItem.setMatrixColumnNames(
                new String[] {
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.dimension"),
                    ModelMessage.get("JdpdInputFile.parameter.value")
                }
            );
            tmpValueItem.setMatrixColumnWidths(
                new String[] {
                    ModelDefinitions.CELL_WIDTH_TEXT_150,   // Molecule_Name
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Dimension
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80  // Value
                }
            );
            // NOTE: Dimension has isFirstRowEditableOnly set to true (see corresponding constructor of ValueItemDataTypeFormat)
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[] {
                    new ValueItemDataTypeFormat(tmpDefaultMoleculeName, ValueItemEnumDataType.TEXT, false), // Molecule_Name
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.mol"), new
                        String[] {
                            ModelMessage.get("JdpdInputFile.parameter.weightPercent"), 
                            ModelMessage.get("JdpdInputFile.parameter.molarPercent"),
                            ModelMessage.get("JdpdInputFile.parameter.gram"), 
                            ModelMessage.get("JdpdInputFile.parameter.mol")
                        }, 
                        false, 
                        true
                    ), // Dimension
                    new ValueItemDataTypeFormat("1", 6, 1.0E-6, Double.MAX_VALUE) // Value
                }
            );
            tmpValueItem.setMatrixDiagramColumns(0, 2);
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Concentration"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Quantity (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Quantity");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Quantity"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setMatrixColumnNames(
                new String[] {
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.value")
                }
            );
            tmpValueItem.setMatrixColumnWidths(
                new String[] {
                    ModelDefinitions.CELL_WIDTH_TEXT_150,  // moleculeName
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80 // value
                }
            );
            // NOTE: Molecule name is NOT editable
            // NOTE: Quantity 24000 comes from box size (20) and density (3): 20*20*20 = 8000, 8000*3 = 24000
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultMoleculeName, ValueItemEnumDataType.TEXT, false), // Molecule_Name
                new ValueItemDataTypeFormat("24000", 0, 1, 1000000000) // Value
            });
            tmpValueItem.setMatrixClonedBeforeChange(true);
            tmpValueItem.setMatrixDiagramColumns(0, 1);
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Quantity"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- BoxSize">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("BoxSize");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.BoxSize"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setMatrixColumnNames(new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.xLengthDpd"),
                    ModelMessage.get("JdpdInputFile.parameter.yLengthDpd"),
                    ModelMessage.get("JdpdInputFile.parameter.zLengthDpd"),
                    ModelMessage.get("JdpdInputFile.parameter.xState"),
                    ModelMessage.get("JdpdInputFile.parameter.xLengthAngstrom"),
                    ModelMessage.get("JdpdInputFile.parameter.yState"),
                    ModelMessage.get("JdpdInputFile.parameter.yLengthAngstrom"),
                    ModelMessage.get("JdpdInputFile.parameter.zState"),
                    ModelMessage.get("JdpdInputFile.parameter.zLengthAngstrom")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[] { 
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-length_DPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-length_DPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-length_DPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80,  // State_x
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-length_Angstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80,  // State_y
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-length_Angstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80,  // State_z
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100  // z-length_Angstrom
                }
            );
            ValueItem tmpParticleTableValueItem = this.nameToValueItemMap.get("ParticleTable");
            ValueItem tmpMonomerTableValueItem = this.nameToValueItemMap.get("MonomerTable");
            ValueItem tmpMoleculeTableValueItem = this.nameToValueItemMap.get("MoleculeTable");
            ValueItem tmpQuantityValueItem = this.nameToValueItemMap.get("Quantity");
            ValueItem tmpDensityValueItem = this.nameToValueItemMap.get("Density");
            double tmpLengthConversionFactor = 
                this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(
                    tmpParticleTableValueItem,
                    tmpMonomerTableValueItem,
                    tmpMoleculeTableValueItem,
                    tmpQuantityValueItem,
                    tmpDensityValueItem
                );
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[] {
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize), 
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS, 
                        tmpBoxSizeMinimum, 
                        tmpBoxSizeMaximum, 
                        false, 
                        false
                    ), // x-length_DPD
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize), 
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS, 
                        tmpBoxSizeMinimum, 
                        tmpBoxSizeMaximum, 
                        false, 
                        false
                    ), // y-length_DPD
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize), 
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS, 
                        tmpBoxSizeMinimum, 
                        tmpBoxSizeMaximum, 
                        false, 
                        false
                    ), // z-length_DPD
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.flexible"),
                        new String[] {
                            ModelMessage.get("JdpdInputFile.parameter.flexible"),
                            ModelMessage.get("JdpdInputFile.parameter.fixed")
                        }
                    ), // State_x
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize * tmpLengthConversionFactor), 
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS, 
                        tmpBoxSizeMinimum, 
                        tmpBoxSizeMaximum
                    ), // x-length_Angstrom
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.flexible"),
                        new String[] {
                            ModelMessage.get("JdpdInputFile.parameter.flexible"),
                            ModelMessage.get("JdpdInputFile.parameter.fixed")
                        }
                    ), // State_y
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize * tmpLengthConversionFactor), 
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS, 
                        tmpBoxSizeMinimum, 
                        tmpBoxSizeMaximum
                    ), // y-length_Angstrom
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.flexible"),
                        new String[] {
                            ModelMessage.get("JdpdInputFile.parameter.flexible"),
                            ModelMessage.get("JdpdInputFile.parameter.fixed")
                        }
                    ), // State_z
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize * tmpLengthConversionFactor), 
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS, 
                        tmpBoxSizeMinimum, 
                        tmpBoxSizeMaximum
                    ) // z-length_Angstrom
                }
            );
            tmpValueItem.setMatrixOutputOmitColumns(
                new boolean[]{
                    false, // x-length_DPD
                    false, // x-length_DPD
                    false, // x-length_DPD
                    true,  // State_x
                    true,  // x-length_Angstrom
                    true,  // State_y
                    true,  // y-length_Angstrom
                    true,  // State_z
                    true   // z-length_Angstrom
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.BoxSize"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            tmpNodeNames = 
                new String[] { 
                    ModelMessage.get("JdpdInputFile.section.jobInput"), 
                    ModelMessage.get("JdpdInputFile.section.chemicalSystemDescription"),
                    ModelMessage.get("JdpdInputFile.section.simulationBox")
                };
            // <editor-fold defaultstate="collapsed" desc="- Molecule display settings related value items (NOTE: No Jdpd input)">
            // NOTE: Molecule display settings MUST be AFTER definition of MonomerTable, MoleculeTable, Density, Quantity and ParticleTable value item
            // NOTE: No Jdpd input
            tmpVerticalPosition = this.jobUtilityMethods.addMoleculeDisplaySettingsValueItems(this.jobInputValueItemContainer, tmpVerticalPosition, tmpNodeNames);
            if (tmpVerticalPosition < 0) {
                // <editor-fold defaultstate="collapsed" desc="Fatal error message">
                JOptionPane.showMessageDialog(null, ModelMessage.get("Error.NoMoleculeDisplaySettings"), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.exitApplication(-1);
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- GeometryRandomSeed (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("GeometryRandomSeed");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.GeometryRandomSeed"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.DETERMINISTIC_RANDOM_SEED_DEFAULT), 0, 1, Double.POSITIVE_INFINITY));
            // Note: A change of the geometry random seed must be transferred
            // to a corresponding change of the geometry random seed of the 
            // compartment container, so update notification must be activated
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.GeometryRandomSeed"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Compartments (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Compartments");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Compartments"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.COMPARTMENT_CONTAINER);
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Compartments"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            tmpNodeNames = 
                new String[] { 
                    ModelMessage.get("JdpdInputFile.section.jobInput"), 
                    ModelMessage.get("JdpdInputFile.section.chemicalSystemDescription"),
                    ModelMessage.get("JdpdInputFile.section.simulationBox"), 
                    ModelMessage.get("JdpdInputFile.section.movement")
                };
            // <editor-fold defaultstate="collapsed" desc="- MoleculeFixation">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeFixation");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeFixation"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(
                new String[]
                    {
                        ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                        ModelMessage.get("JdpdInputFile.parameter.x"),
                        ModelMessage.get("JdpdInputFile.parameter.y"), 
                        ModelMessage.get("JdpdInputFile.parameter.z"),
                        ModelMessage.get("JdpdInputFile.parameter.MaxTimeStep")
                    }
            );
            tmpValueItem.setMatrixColumnWidths(
                new String[]
                    {
                        ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
                        ModelDefinitions.CELL_WIDTH_NUMERIC_80, // x
                        ModelDefinitions.CELL_WIDTH_NUMERIC_80, // y
                        ModelDefinitions.CELL_WIDTH_NUMERIC_80, // z
                        ModelDefinitions.CELL_WIDTH_NUMERIC_140  // MaxTimeStep
                    }
            ); 
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[]
                    {
                        new ValueItemDataTypeFormat(
                            tmpDefaultMoleculeName, 
                            ValueItemEnumDataType.TEXT, 
                            false
                        ), // Molecule_Name
                        new ValueItemDataTypeFormat(
                            ModelMessage.get("JdpdInputFile.parameter.false"), 
                            new String[]
                                {
                                    ModelMessage.get("JdpdInputFile.parameter.true"),
                                    ModelMessage.get("JdpdInputFile.parameter.false")
                                }
                        ), // x
                        new ValueItemDataTypeFormat(
                            ModelMessage.get("JdpdInputFile.parameter.false"), 
                            new String[]
                                {
                                    ModelMessage.get("JdpdInputFile.parameter.true"),
                                    ModelMessage.get("JdpdInputFile.parameter.false")
                                }
                        ), // y
                        new ValueItemDataTypeFormat(
                            ModelMessage.get("JdpdInputFile.parameter.false"), 
                            new String[]
                                {
                                    ModelMessage.get("JdpdInputFile.parameter.true"),
                                    ModelMessage.get("JdpdInputFile.parameter.false")
                                }
                        ),  // z
                        new ValueItemDataTypeFormat(
                            "1.0",
                            0,
                            1,
                            Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
                        ) // MaxTimeStep
                    }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeFixation"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeBoundary">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeBoundary");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeBoundary"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.isXboundary"),
                    ModelMessage.get("JdpdInputFile.parameter.xMinAngstrom"), 
                    ModelMessage.get("JdpdInputFile.parameter.xMinDPD"), 
                    ModelMessage.get("JdpdInputFile.parameter.xMaxAngstrom"), 
                    ModelMessage.get("JdpdInputFile.parameter.xMaxDPD"), 
                    ModelMessage.get("JdpdInputFile.parameter.isYboundary"),
                    ModelMessage.get("JdpdInputFile.parameter.yMinAngstrom"), 
                    ModelMessage.get("JdpdInputFile.parameter.yMinDPD"), 
                    ModelMessage.get("JdpdInputFile.parameter.yMaxAngstrom"), 
                    ModelMessage.get("JdpdInputFile.parameter.yMaxDPD"), 
                    ModelMessage.get("JdpdInputFile.parameter.isZboundary"),
                    ModelMessage.get("JdpdInputFile.parameter.zMinAngstrom"), 
                    ModelMessage.get("JdpdInputFile.parameter.zMinDPD"), 
                    ModelMessage.get("JdpdInputFile.parameter.zMaxAngstrom"),
                    ModelMessage.get("JdpdInputFile.parameter.zMaxDPD"),
                    ModelMessage.get("JdpdInputFile.parameter.MaxTimeStep")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[] { 
                    ModelDefinitions.CELL_WIDTH_TEXT_150,   // moleculeName
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // ActiveState
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // xMinAngstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // xMinDPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // xMaxAngstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // xMaxDPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // ActiveState
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // yMinAngstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // yMinDPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // yMaxAngstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // yMaxDPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // ActiveState
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // zMinAngstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // zMinDPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // zMaxAngstrom
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // zMaxDPD
                    ModelDefinitions.CELL_WIDTH_NUMERIC_140  // MaxTimeStep
            }); 
            double tmpBoundaryMinimum = 0.0;
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[] { 
                    new ValueItemDataTypeFormat(
                        tmpDefaultMoleculeName, 
                        false
                    ), // moleculeName
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.false"), 
                        new String[] { 
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    ), // ActiveState
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpBoundaryMinimum),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize * tmpLengthConversionFactor
                    ), // xMinAngstrom
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpBoundaryMinimum),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize,
                        false,
                        false
                    ), // xMinDPD
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize * tmpLengthConversionFactor),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize * tmpLengthConversionFactor
                    ), // xMaxAngstrom
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize,
                        false,
                        false
                    ), // xMaxDPD
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.false"), 
                        new String[] { 
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    ), // ActiveState
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpBoundaryMinimum),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize * tmpLengthConversionFactor
                    ), // yMinAngstrom
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpBoundaryMinimum),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize,
                        false,
                        false
                    ), // yMinDPD
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize * tmpLengthConversionFactor),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize * tmpLengthConversionFactor
                    ), // yMaxAngstrom
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize,
                        false,
                        false
                    ), // yMaxDPD
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.false"), 
                        new String[] { 
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    ), // ActiveState
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpBoundaryMinimum),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize * tmpLengthConversionFactor
                    ), // zMinAngstrom
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpBoundaryMinimum),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize,
                        false,
                        false
                    ), // zMinDPD
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize * tmpLengthConversionFactor),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize * tmpLengthConversionFactor
                    ), // zMaxAngstrom
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpDefaultBoxSize),
                        ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                        tmpBoundaryMinimum,
                        tmpDefaultBoxSize,
                        false,
                        false
                    ), // zMaxDPD
                    new ValueItemDataTypeFormat(
                        "1.0",
                        0,
                        1,
                        Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
                    ) // MaxTimeStep
                }
            );
            tmpValueItem.setMatrixOutputOmitColumns(
                new boolean[]{
                    false, // moleculeName
                    false, // ActiveState
                    true,  // xMinAngstrom
                    false, // xMinDPD
                    true,  // xMaxAngstrom
                    false, // xMaxDPD
                    false, // ActiveState
                    true,  // yMinAngstrom
                    false, // yMinDPD
                    true,  // yMaxAngstrom
                    false, // yMaxDPD
                    false, // ActiveState
                    true,  // zMinAngstrom
                    false, // zMinDPD
                    true,  // zMaxAngstrom
                    false, // zMaxDPD
                    false  // MaxTimeStep
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeBoundary"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeFixedVelocity">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeFixedVelocity");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeFixedVelocity"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.isXvelocity"),
                    ModelMessage.get("JdpdInputFile.parameter.xVelocity"), 
                    ModelMessage.get("JdpdInputFile.parameter.isYvelocity"),
                    ModelMessage.get("JdpdInputFile.parameter.yVelocity"), 
                    ModelMessage.get("JdpdInputFile.parameter.isZvelocity"),
                    ModelMessage.get("JdpdInputFile.parameter.zVelocity"),
                    ModelMessage.get("JdpdInputFile.parameter.MaxTimeStep")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[] { 
                    ModelDefinitions.CELL_WIDTH_TEXT_150,    // Molecule_Name
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // isXvelocity
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80,  // x-Velocity
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // isYvelocity
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80,  // y-Velocity
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // isZvelocity
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80,   // z-Velocity
                    ModelDefinitions.CELL_WIDTH_NUMERIC_140  // MaxTimeStep
                }
            );
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultMoleculeName, ValueItemEnumDataType.TEXT, false), // Molecule_Name
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.false"), new String[]{ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")}), // Active
                new ValueItemDataTypeFormat("0.0", 4, -5, 5), // x-Velocity
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.false"), new String[]{ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")}), // Active
                new ValueItemDataTypeFormat("0.0", 4, -5, 5), // y-Velocity
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.false"), new String[]{ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")}), // Active
                new ValueItemDataTypeFormat("0.0", 4, -5, 5), // z-Velocity
                new ValueItemDataTypeFormat(
                    "1.0",
                    0,
                    1,
                    Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
                ) // MaxTimeStep
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeFixedVelocity"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeAcceleration">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeAcceleration");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeAcceleration"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.x"),
                    ModelMessage.get("JdpdInputFile.parameter.y"), 
                    ModelMessage.get("JdpdInputFile.parameter.z"),
                    ModelMessage.get("JdpdInputFile.parameter.frequency"),
                    ModelMessage.get("JdpdInputFile.parameter.MaxTimeStep")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[] { 
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z
                    ModelDefinitions.CELL_WIDTH_NUMERIC_100, // Frequency
                    ModelDefinitions.CELL_WIDTH_NUMERIC_140  // MaxTimeStep
                }
            ); 
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[] {
                    new ValueItemDataTypeFormat(
                        tmpDefaultMoleculeName, 
                        ValueItemEnumDataType.TEXT, 
                        false
                    ), // Molecule_Name
                    new ValueItemDataTypeFormat(
                        "0.0",
                        6,
                        -1000.0,
                        1000.0
                    ), // x
                    new ValueItemDataTypeFormat(
                        "0.0",
                        6,
                        -1000.0,
                        1000.0
                    ), // y
                    new ValueItemDataTypeFormat(
                        "0.0",
                        6,
                        -1000.0,
                        1000.0
                    ), // z
                    new ValueItemDataTypeFormat(
                        "1.0",
                        0,
                        1,
                        Double.POSITIVE_INFINITY
                    ), // Frequency
                    new ValueItemDataTypeFormat(
                        "1.0",
                        0,
                        1,
                        Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
                    ) // MaxTimeStep
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeAcceleration"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            tmpNodeNames = 
                new String[] { 
                    ModelMessage.get("JdpdInputFile.section.jobInput"), 
                    ModelMessage.get("JdpdInputFile.section.chemicalSystemDescription"),
                    ModelMessage.get("JdpdInputFile.section.propertyCalculation")
                };
            // <editor-fold defaultstate="collapsed" desc="- ParticlePairRdfCalculation (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("ParticlePairRdfCalculation");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.ParticlePairRdfCalculation"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setEssential(false);
            tmpValueItem.setActivity(false);
            tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JdpdInputFile.parameter.particle1"), ModelMessage.get("JdpdInputFile.parameter.particle2")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_150, // particle1
                ModelDefinitions.CELL_WIDTH_TEXT_150  // particle2
            });
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(new String[]{tmpDefaultParticle}), // particle1
                new ValueItemDataTypeFormat(new String[]{tmpDefaultParticle}), // particle2
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.ParticlePairRdfCalculation"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeParticlePairRdfCalculation (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeParticlePairRdfCalculation");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeParticlePairRdfCalculation"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setEssential(false);
            tmpValueItem.setActivity(false);
            tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JdpdInputFile.parameter.moleculeParticle1"),
                ModelMessage.get("JdpdInputFile.parameter.moleculeParticle2")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_in_molecule_1
                ModelDefinitions.CELL_WIDTH_TEXT_150});
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(new String[]{tmpDefaultMoleculeParticle}), // Particle_in_molecule_1
                new ValueItemDataTypeFormat(new String[]{tmpDefaultMoleculeParticle}), // Particle_in_molecule_2
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeParticlePairRdfCalculation"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- ParticlePairDistanceCalculation (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("ParticlePairDistanceCalculation");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.ParticlePairDistanceCalculation"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setEssential(false);
            tmpValueItem.setActivity(false);
            tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JdpdInputFile.parameter.particle1"), ModelMessage.get("JdpdInputFile.parameter.particle2")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_1
                ModelDefinitions.CELL_WIDTH_TEXT_150});
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(new String[]{tmpDefaultParticle}), // Particle_1
                new ValueItemDataTypeFormat(new String[]{tmpDefaultParticle}), // Particle_2
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.ParticlePairDistanceCalculation"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeParticlePairDistanceCalculation (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeParticlePairDistanceCalculation");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeParticlePairDistanceCalculation"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setEssential(false);
            tmpValueItem.setActivity(false);
            tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JdpdInputFile.parameter.moleculeParticle1"),
                ModelMessage.get("JdpdInputFile.parameter.moleculeParticle2")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_in_molecule_1
                ModelDefinitions.CELL_WIDTH_TEXT_150});
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(new String[]{tmpDefaultMoleculeParticle}), // Particle_in_molecule_1
                new ValueItemDataTypeFormat(new String[]{tmpDefaultMoleculeParticle}), // Particle_in_molecule_2
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeParticlePairDistanceCalculation"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- RadiusOfGyration">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("RadiusOfGyration");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.RadiusOfGyration"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.isCalculation")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
                    ModelDefinitions.CELL_WIDTH_TEXT_100  // Activity
                }
            );
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultMoleculeName, ValueItemEnumDataType.TEXT, false), // Molecule_Name
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.false"), new String[]{ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")}), // Activity
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.RadiusOfGyration"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- NearestNeighborParticle">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("NearestNeighborParticle");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.NearestNeighborParticle"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"), 
                    ModelMessage.get("JdpdInputFile.parameter.particle"), 
                    ModelMessage.get("JdpdInputFile.parameter.isCalculation")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // Particle
                    ModelDefinitions.CELL_WIDTH_TEXT_100  // Activity
                }
            );
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(tmpDefaultMoleculeName, ValueItemEnumDataType.TEXT, false), // Molecule_Name
                    new ValueItemDataTypeFormat(tmpDefaultParticle, ValueItemEnumDataType.TEXT, false),     // Particle
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.false"), 
                        new String[]{
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    ), // Activity
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.NearestNeighborParticle"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- NearestNeighborDistance">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("NearestNeighborDistance");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.NearestNeighborDistance"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("2.00", 2, 0.0, Double.POSITIVE_INFINITY));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_2);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.NearestNeighborDistance"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Section INTERACTION_DESCRIPTION (block 3)">
            tmpNodeNames = 
                new String[] { 
                    ModelMessage.get("JdpdInputFile.section.jobInput"), 
                    ModelMessage.get("JdpdInputFile.section.interactionDescription")
                };
            // <editor-fold defaultstate="collapsed" desc="- Temperature">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Temperature");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Temperature"));
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(StandardParticleInteractionData.getInstance().getTemperatureStrings()));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Temperature"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- DpdSigma">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("DpdSigma");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.DpdSigma"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("3.00", 2, 0.0, Double.POSITIVE_INFINITY));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.DpdSigma"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- IsGaussianRandomDpdForce">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("IsGaussianRandomDpdForce");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.IsGaussianRandomDpdForce"));
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.false"), new String[] {ModelMessage.get("JdpdInputFile.parameter.true"),
                ModelMessage.get("JdpdInputFile.parameter.false")}));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.IsGaussianRandomDpdForce"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- InteractionTable">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("InteractionTable");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.InteractionTable"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JdpdInputFile.parameter.particlePair"), ModelMessage.get("JdpdInputFile.parameter.interaction")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_200, // Particle_Pair
                ModelDefinitions.CELL_WIDTH_TEXT_100 // Interaction_a(ij)
            });
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultParticlePair, ValueItemEnumDataType.TEXT, false), // Particle_Pair
                new ValueItemDataTypeFormat("0.000000", ModelDefinitions.INTERACTION_NUMBER_OF_DECIMALS, -1000, 1000, true, true) // Interaction_a(ij)
            });
            // Set default value: tmpDefaultInteractionValue
            tmpValueItem.setValue(tmpDefaultInteractionValue, 0, 1);
            if (tmpInteractionValueErrorInformation != null) {
                tmpValueItem.setError(tmpInteractionValueErrorInformation);
            }
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.InteractionTable"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- MoleculeBackboneForces (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("MoleculeBackboneForces");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MoleculeBackboneForces"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setEssential(false);
            tmpValueItem.setLocked(true);
            tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("JdpdInputFile.parameter.moleculeName"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneAttribute1"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneAttribute2"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneDistanceAngstrom"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneDistanceDpd"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneForceConstant"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneBehaviour")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // moleculeName
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneAttribute1
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneAttribute2
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneDistanceAngstrom
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneDistanceDpd
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneForceConstant
                    ModelDefinitions.CELL_WIDTH_TEXT_150  // backboneBehaviour
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MoleculeBackboneForces"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- ProteinBackboneForces (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("ProteinBackboneForces");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.ProteinBackboneForces"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setEssential(false);
            tmpValueItem.setLocked(true);
            tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("JdpdInputFile.parameter.proteinName"),
                    ModelMessage.get("JdpdInputFile.parameter.aminoAcidBackboneParticle1"),
                    ModelMessage.get("JdpdInputFile.parameter.aminoAcidBackboneParticle2"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneDistanceAngstrom"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneDistanceDpd"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneForceConstant"),
                    ModelMessage.get("JdpdInputFile.parameter.backboneBehaviour")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // proteinName
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // aminoAcidBackboneParticle1
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // aminoAcidBackboneParticle2
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneDistanceAngstrom
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneDistanceDpd
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // backboneForceConstant
                    ModelDefinitions.CELL_WIDTH_TEXT_150  // backboneBehaviour
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.ProteinBackboneForces"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- ProteinDistanceForces (NOTE: No Jdpd input)">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("ProteinDistanceForces");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.ProteinDistanceForces"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setEssential(false);
            tmpValueItem.setLocked(true);
            // NOTE: These definitions are arbitrary and will be updated before value item is unlocked
            tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("JdpdInputFile.parameter.proteinBackboneDistancesType"),
                    String.format(ModelMessage.get("JdpdInputFile.parameter.proteinDistanceForceConstantFormat"), "NoProtein")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // Distance_type
                    ModelDefinitions.CELL_WIDTH_TEXT_100  // Force_constant
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            // NOTE: No Jdpd input
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.ProteinDistanceForces"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Electrostatics">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Electrostatics");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Electrostatics"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setEssential(false);
            tmpValueItem.setActivity(false);
            tmpValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsForceCutoff"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsForceMaximumValue"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsForceEffectiveExponent"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsForceDampingDistance"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsForceDampingFactor"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsCouplingConstant"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsChargeDistributionType"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsDecayLength"),
                ModelMessage.get("JdpdInputFile.parameter.electrostaticsSplittingType")
            });
            tmpValueItem.setMatrixColumnWidths(
                new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // electrostaticsForceCutoff
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // electrostaticsForceMaximumValue
                    ModelDefinitions.CELL_WIDTH_TEXT_120, // electrostaticsForceEffectiveExponent
                    ModelDefinitions.CELL_WIDTH_TEXT_120, // electrostaticsForceDampingDistance
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // electrostaticsForceDampingFactor
                    ModelDefinitions.CELL_WIDTH_TEXT_120, // electrostaticsCouplingConstant
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // electrostaticsChargeDistributionType
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // electrostaticsDecayLength
                    ModelDefinitions.CELL_WIDTH_TEXT_100  // electrostaticsSplittingType
                }
            );
            ValueItem tmpTemperatureValueItem = this.nameToValueItemMap.get("Temperature");
            double tmpElectrostaticsCouplingConstant = 
                this.jobUtilityMethods.getElectrostaticsCouplingConstant(
                    tmpParticleTableValueItem,
                    tmpMonomerTableValueItem,
                    tmpMoleculeTableValueItem,
                    tmpQuantityValueItem,
                    tmpDensityValueItem,
                    tmpTemperatureValueItem
                );
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[]
                    {
                        new ValueItemDataTypeFormat("5.00", 2, 0.0, Double.POSITIVE_INFINITY),   // electrostaticsForceCutoff
                        new ValueItemDataTypeFormat("1000.0", 2, 0.0, Double.POSITIVE_INFINITY), // electrostaticsForceMaximumValue
                        new ValueItemDataTypeFormat("2.00", 2, 2.0, Double.POSITIVE_INFINITY),   // electrostaticsForceEffectiveExponent
                        new ValueItemDataTypeFormat("5.00", 2, 0.0, Double.POSITIVE_INFINITY),   // electrostaticsForceDampingDistance
                        new ValueItemDataTypeFormat("1.00", 2, 0.0, 1.0),                        // electrostaticsForceDampingFactor
                        // electrostaticsCouplingConstant
                        new ValueItemDataTypeFormat(
                            String.valueOf(tmpElectrostaticsCouplingConstant), 
                            6, 
                            0.0, 
                            Double.POSITIVE_INFINITY
                        ),
                        // electrostaticsChargeDistributionType
                        new ValueItemDataTypeFormat(
                            ModelMessage.get("JdpdInputFile.parameter.electrostaticsChargeDistributionType.Alejandre"),
                            new String[] {
                                ModelMessage.get("JdpdInputFile.parameter.electrostaticsChargeDistributionType.None"),
                                ModelMessage.get("JdpdInputFile.parameter.electrostaticsChargeDistributionType.Alejandre")
                            }
                        ),
                        // electrostaticsDecayLength
                        new ValueItemDataTypeFormat(
                            String.valueOf(tmpElectrostaticsCouplingConstant), 
                            6, 
                            0.0, 
                            Double.POSITIVE_INFINITY
                        ),
                        // electrostaticsSplittingType
                        new ValueItemDataTypeFormat(
                            ModelMessage.get("JdpdInputFile.parameter.electrostaticsSplittingType.Fanourgakis"),
                            new String[] {
                                ModelMessage.get("JdpdInputFile.parameter.electrostaticsSplittingType.None"),
                                ModelMessage.get("JdpdInputFile.parameter.electrostaticsSplittingType.Fanourgakis")
                            }
                        )
                    }
                ); 
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Electrostatics"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Bonds12Table">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("Bonds12Table");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.Bonds12"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(
                new String[] {
                    ModelMessage.get("JdpdInputFile.parameter.particlePair"),
                    ModelMessage.get("JdpdInputFile.parameter.bondLength"), 
                    ModelMessage.get("JdpdInputFile.parameter.forceConstant"),
                    ModelMessage.get("JdpdInputFile.parameter.BondRepulsion")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_200, // Particle_Pair
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Bond_Length
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Force_Constant
                ModelDefinitions.CELL_WIDTH_TEXT_100  // Is_Repulsion
            });
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultParticlePair, ValueItemEnumDataType.TEXT, false), // Particle_Pair
                new ValueItemDataTypeFormat(tmpDefaultParticlePairBondLength, 3, 0.001, 100), // Bond_Length
                new ValueItemDataTypeFormat("4.000", 3, 0.001, Double.MAX_VALUE), // Force_Constant
                new ValueItemDataTypeFormat(
                    ModelMessage.get("JdpdInputFile.parameter.true"), 
                    new String[]
                        {
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                ) // Is_Repulsion
            });
            // Set default particle-pair bond length to 1.0
            tmpValueItem.setValue("1.000", 0, 1);
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.Bonds12"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- GravitationalAcceleration">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("GravitationalAcceleration");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.GravitationalAcceleration"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setMatrixColumnNames(new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.x"), 
                    ModelMessage.get("JdpdInputFile.parameter.y"),
                    ModelMessage.get("JdpdInputFile.parameter.z")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[] { 
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // x
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // y
                    ModelDefinitions.CELL_WIDTH_TEXT_100  // z
                }
            );
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(
                        "0.0",
                        6,
                        -1000.0,
                        1000.0
                    ), // x
                    new ValueItemDataTypeFormat(
                        "0.0",
                        6,
                        -1000.0,
                        1000.0
                    ), // y
                    new ValueItemDataTypeFormat(
                        "0.0",
                        6,
                        -1000.0,
                        1000.0
                    ) // z
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_3);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.GravitationalAcceleration"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Section SIMULATION_DESCRIPTION (block 4)">
            tmpNodeNames = new String[]{ModelMessage.get("JdpdInputFile.section.jobInput"), ModelMessage.get("JdpdInputFile.section.simulationDescription")};
            // <editor-fold defaultstate="collapsed" desc="- TimeStepNumber">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("TimeStepNumber");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.TimeStepNumber"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("JdpdInputFile.parameter.timeStepNumber"),
                ModelMessage.get("JdpdInputFile.parameter.timeOfStep"),
                ModelMessage.get("JdpdInputFile.parameter.timeOfSimulation")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // timeStepNumber
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // timeOfStep
                ModelDefinitions.CELL_WIDTH_NUMERIC_120  // timeOfSimulation
            });
            tmpTemperatureValueItem = this.nameToValueItemMap.get("Temperature");
            double tmpTimeConversionFactor = 
                this.jobUtilityMethods.getTimeConversionFactorFromDpdToPhysicalTime(
                    tmpParticleTableValueItem,
                    tmpMonomerTableValueItem,
                    tmpMoleculeTableValueItem,
                    tmpQuantityValueItem,
                    tmpDensityValueItem,
                    tmpTemperatureValueItem
                );
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultTimeStepNumberRepresentation, 0, 1, Constants.MAXIMUM_NUMBER_OF_TIME_STEPS), // timeStepNumber
                new ValueItemDataTypeFormat(String.valueOf(tmpDefaultTimeStepLength * tmpTimeConversionFactor), 3, 0, Double.MAX_VALUE, false, false), // timeOfStep
                new ValueItemDataTypeFormat(String.valueOf(tmpDefaultTimeStepNumber * tmpDefaultTimeStepLength * tmpTimeConversionFactor), 3, 0, Double.MAX_VALUE, false, false) // timeOfSimulation
            });
            tmpValueItem.setMatrixOutputOmitColumns(new boolean[]{
                false, // timeStepNumber
                true, // timeOfStep
                true // timeOfSimulation
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.TimeStepNumber"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- TimeStepLength">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("TimeStepLength");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.TimeStepLength"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("JdpdInputFile.parameter.timeStepLength"),
                ModelMessage.get("JdpdInputFile.parameter.timeOfStep"),
                ModelMessage.get("JdpdInputFile.parameter.timeOfSimulation")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // timeStepLength
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // timeOfStep
                ModelDefinitions.CELL_WIDTH_NUMERIC_120});
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpDefaultTimeStepLengthRepresentation, 3, 0.001, Double.POSITIVE_INFINITY), // timeStepLength
                new ValueItemDataTypeFormat(String.valueOf(tmpDefaultTimeStepLength * tmpTimeConversionFactor), 3, 0, Double.MAX_VALUE, false, false), // timeOfStep
                new ValueItemDataTypeFormat(String.valueOf(tmpDefaultTimeStepNumber * tmpDefaultTimeStepLength * tmpTimeConversionFactor), 3, 0, Double.MAX_VALUE, false, false) // timeOfSimulation
            });
            tmpValueItem.setMatrixOutputOmitColumns(new boolean[]{
                false, // timeStepLength
                true, // timeOfStep
                true // timeOfSimulation
            });
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.TimeStepLength"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- TimeStepFrequencyForOutput">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("TimeStepFrequencyForOutput");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.TimeStepFrequencyForOutput"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("100", 0, 1, 10000));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.TimeStepFrequencyForOutput"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- IntegrationType">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("IntegrationType");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setUpdateNotifier(true);
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.IntegrationType"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("JdpdInputFile.parameter.type"), 
                    ModelMessage.get("JdpdInputFile.parameter.parameter1"),
                    ModelMessage.get("JdpdInputFile.parameter.parameter2")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Type
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Parameter1
                ModelDefinitions.CELL_WIDTH_TEXT_150  // Parameter2
            });
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(
                        Factory.IntegrationType.getDefaultIntegrationTypeRepresentation(), 
                        Factory.IntegrationType.getIntegrationTypeRepresentations()
                    ), // Type
                    new ValueItemDataTypeFormat("0.65", 2, 0.0, 1.0), // Parameter1
                    new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.none"), false)  // Parameter2
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setMatrixClonedBeforeChange(true);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.IntegrationType"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- InitialPotentialEnergyMinimizationStepNumber">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("InitialPotentialEnergyMinimizationStepNumber");
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.InitialPotentialEnergyMinimizationStepNumber"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("JdpdInputFile.parameter.steps"), 
                    ModelMessage.get("JdpdInputFile.parameter.allForces")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_100, // steps
                ModelDefinitions.CELL_WIDTH_TEXT_100  // all forces
            });
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(
                        "100", 
                        0, 
                        0, 
                        Double.POSITIVE_INFINITY
                    ), // steps
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("JdpdInputFile.parameter.false"),
                        new String[] {
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    ) // all forces
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.InitialPotentialEnergyMinimizationStepNumber"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- IsInitialPotentialEnergyMinimizationStepOutput">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("IsInitialPotentialEnergyMinimizationStepOutput");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.IsInitialPotentialEnergyMinimizationStepOutput"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.false"), new String[] {ModelMessage.get("JdpdInputFile.parameter.true"),
                ModelMessage.get("JdpdInputFile.parameter.false")}));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.IsInitialPotentialEnergyMinimizationStepOutput"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- PeriodicBoundaries">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("PeriodicBoundaries");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.PeriodicBoundaries"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JdpdInputFile.parameter.x"), ModelMessage.get("JdpdInputFile.parameter.y"),
                ModelMessage.get("JdpdInputFile.parameter.z")});
            tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_100, // x
                ModelDefinitions.CELL_WIDTH_TEXT_100, // y
                ModelDefinitions.CELL_WIDTH_TEXT_100});
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.true"), new String[]{ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")}),
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.true"), new String[]{ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")}),
                new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.true"), new String[]{ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")})});
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.PeriodicBoundaries"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- IsDpdUnitMass">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("IsDpdUnitMass");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.IsDpdUnitMass"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.true"), new String[] {ModelMessage.get("JdpdInputFile.parameter.true"),
                ModelMessage.get("JdpdInputFile.parameter.false")}));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.IsDpdUnitMass"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- IsVelocityScaling (NO LONGER USED)">
            //    tmpValueItem = new ValueItem();
            //    tmpValueItem.setNodeNames(tmpNodeNames);
            //    tmpValueItem.setName("IsVelocityScaling");
            //    this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            //    tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.IsVelocityScaling"));
            //    tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
            //            ModelMessage.get("JdpdInputFile.parameter.false"), 
            //            new String[] {
            //                ModelMessage.get("JdpdInputFile.parameter.true"),
            //                ModelMessage.get("JdpdInputFile.parameter.false")
            //            }
            //        )
            //    );
            //    tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            //    tmpValueItem.setJdpdInput(true);
            //    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            //    tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.IsVelocityScaling"));
            //    this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            //    this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- InitialVelocityScalingSteps">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("InitialVelocityScalingSteps");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.InitialVelocityScalingSteps"));
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("100", 0, 0, Constants.MAXIMUM_NUMBER_OF_TIME_STEPS));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.InitialVelocityScalingSteps"));
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- RandomNumberGenerator">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setName("RandomNumberGenerator");
            this.valueItemNameMapForJobRestartEdit.put(tmpValueItem.getName(), tmpValueItem.getName());
            tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.RandomNumberGenerator"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("JdpdInputFile.parameter.type"), 
                    ModelMessage.get("JdpdInputFile.parameter.seed"),
                    ModelMessage.get("JdpdInputFile.parameter.warmup")
                }
            );
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_250, // Type
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Seed
                ModelDefinitions.CELL_WIDTH_TEXT_100  // Warm-up
            });
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(
                        Factory.RandomType.getDefaultRandomNumberGeneratorTypeRepresentation(), 
                        Factory.RandomType.getRandomNumberGeneratorTypeRepresentations()
                    ), // Type
                    new ValueItemDataTypeFormat("1", 0, 1, 999999), // Seed
                    new ValueItemDataTypeFormat("10000", 0, 0, 10000000) // Warm-up
                }
            );
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_4);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            String[] tmpJumpableRngInfo = Factory.RandomType.getJumpableRandomNumberGeneratorInfo();
            tmpValueItem.setDescription(
                String.format(
                    ModelMessage.get("JdpdInputFile.valueItem.description.RandomNumberGenerator"), 
                    tmpJumpableRngInfo[0], 
                    tmpJumpableRngInfo[1], 
                    tmpJumpableRngInfo[2]
                )
            );
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Section SIMULATION_COUNTS (block 5)">
            // tmpNodeNames are NOT necessary for ParticleNumber since this value item is not displayed
            // NOTE: ParticleNumber depends on definitions before thus it must be the last value item to be set
            // <editor-fold defaultstate="collapsed" desc="- ParticleNumber">
            tmpValueItem = new ValueItem();
            tmpValueItem.setName("ParticleNumber");
            // ParticleNumber is NOT displayed
            tmpValueItem.setDisplay(false);
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(false));
            int tmpTotalNumberOfParticlesInSimulation = this.jobUtilityMethods.getTotalNumberOfParticlesInSimulation(this.jobInputValueItemContainer);
            tmpValueItem.setValue(String.valueOf(tmpTotalNumberOfParticlesInSimulation));
            tmpValueItem.setBlockName(ModelDefinitions.JDPD_INPUT_BLOCK_5);
            tmpValueItem.setJdpdInput(true);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            this.nameToValueItemMap.put(tmpValueItem.getName(), tmpValueItem);
            this.jobInputValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>

}
