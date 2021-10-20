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
package de.gnwi.mfsim.model.graphics.compartment;

import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayer;
import de.gnwi.mfsim.model.graphics.body.BodySphere;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemContainerXmlName;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.valueItem.ValueItemUpdateNotifierInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.mfsim.model.job.JobUtilityMethods;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Container for compartments of simulation box
 *
 * @author Achim Zielesny
 */
public class CompartmentContainer extends ChangeNotifier implements ChangeReceiverInterface, ValueItemUpdateNotifierInterface {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Job utility methods
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * Utility graphics methods
     */
    private final GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Change Information
     */
    private ChangeInformation changeInformation;

    /**
     * Error change Information
     */
    private ChangeInformation errorChangeInformation;

    /**
     * Value item container
     */
    private ValueItemContainer valueItemContainer;

    /**
     * Simulation box
     */
    private CompartmentBox compartmentBox;

    /**
     * Factor that converts DPD length to physical length (Angstrom since
     * particle volumes are in Angstrom^3)
     */
    private double lengthConversionFactor;

    /**
     * Compartment name
     */
    private String compartmentName;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for BoxSize, MonomerTable, MoleculeTable, Density, Quantity, 
     * GeometryRandomSeed and ParticleTable (value items are not changed)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CompartmentContainer(ValueItemContainer aValueItemContainer) {
        super();
        // <editor-fold defaultstate="collapsed" desc="Initialize">
        this.initialize();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set value item container">
        this.valueItemContainer = new ValueItemContainer(this);
        // <editor-fold defaultstate="collapsed" desc="- Hidden value items (vertical positions 0 to 5)">
        // <editor-fold defaultstate="collapsed" desc="-- tmpGeometryRandomSeedValueItem (vertical position 0)">
        this.valueItemContainer.addValueItem(
            this.getGeometryRandomSeedValueItem(
                aValueItemContainer.getValueItem("GeometryRandomSeed").getValueAsLong()
            )
        );
        // </editor-fold>
        // NOTE: tmpLengthConversionValueItem MUST be second value item since tmpLengthConversionFactor is used in the following
        // <editor-fold defaultstate="collapsed" desc="-- tmpLengthConversionValueItem (vertical position 1)">
        // NOTE: Length conversion value item is NOT displayed and SHOULD be last value item
        ValueItem tmpLengthConversionValueItem = new ValueItem();
        tmpLengthConversionValueItem.setName(ModelDefinitions.COMPARTMENT_LENGTH_CONVERSION_NAME);
        // IMPORTANT: tmpLengthConversionValueItem is NOT displayed
        tmpLengthConversionValueItem.setDisplay(false);
        tmpLengthConversionValueItem.setVerticalPosition(1);
        tmpLengthConversionValueItem.setBlockName(ModelDefinitions.COMPARTMENT_BLOCK_HIDDEN);
        double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(aValueItemContainer);
        tmpLengthConversionValueItem.setValue(String.valueOf(tmpLengthConversionFactor));
        this.valueItemContainer.addValueItem(tmpLengthConversionValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- aBoxInfoValueItem (vertical position 2)">
        ValueItem tmpBoxInfoValueItem = this.jobUtilityMethods.createBoxInfoValueItem(aValueItemContainer);
        if (tmpBoxInfoValueItem == null) {
            throw new IllegalArgumentException("aBoxInfoValueItem is illegal.");
        }
        tmpBoxInfoValueItem.setVerticalPosition(2);
        // IMPORTANT: tmpParticleInfoValueItem is NOT displayed
        tmpBoxInfoValueItem.setDisplay(false);
        tmpBoxInfoValueItem.setBlockName(ModelDefinitions.COMPARTMENT_BLOCK_HIDDEN);
        this.valueItemContainer.addValueItem(tmpBoxInfoValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- aDensityInfoValueItem (vertical position 3)">
        ValueItem tmpDensityInfoValueItem = this.jobUtilityMethods.createDensityInfoValueItem(aValueItemContainer);
        if (tmpDensityInfoValueItem == null) {
            throw new IllegalArgumentException("aDensityInfoValueItem is illegal.");
        }
        tmpDensityInfoValueItem.setVerticalPosition(3);
        // IMPORTANT: tmpParticleInfoValueItem is NOT displayed
        tmpDensityInfoValueItem.setDisplay(false);
        tmpDensityInfoValueItem.setBlockName(ModelDefinitions.COMPARTMENT_BLOCK_HIDDEN);
        this.valueItemContainer.addValueItem(tmpDensityInfoValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- aMoleculeInfoValueItem (vertical position 4)">
        ValueItem tmpMoleculeInfoValueItem = this.jobUtilityMethods.createMoleculeInfoValueItem(aValueItemContainer);
        if (tmpMoleculeInfoValueItem == null) {
            throw new IllegalArgumentException("aMoleculeInfoValueItem is illegal.");
        }
        tmpMoleculeInfoValueItem.setVerticalPosition(4);
        // IMPORTANT: tmpParticleInfoValueItem is NOT displayed
        tmpMoleculeInfoValueItem.setDisplay(false);
        tmpMoleculeInfoValueItem.setBlockName(ModelDefinitions.COMPARTMENT_BLOCK_HIDDEN);
        this.valueItemContainer.addValueItem(tmpMoleculeInfoValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- aParticleInfoValueItem (vertical position 5)">
        // IMPORTANT: tmpParticleInfoValueItem is NOT displayed
        ValueItem tmpParticleInfoValueItem = this.jobUtilityMethods.createParticleInfoValueItem(aValueItemContainer, tmpLengthConversionFactor);
        if (tmpParticleInfoValueItem == null) {
            throw new IllegalArgumentException("aParticleInfoValueItem is illegal.");
        }
        tmpParticleInfoValueItem.setDisplay(false);
        tmpParticleInfoValueItem.setVerticalPosition(5);
        tmpParticleInfoValueItem.setBlockName(ModelDefinitions.COMPARTMENT_BLOCK_HIDDEN);
        this.valueItemContainer.addValueItem(tmpParticleInfoValueItem);
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Bulk info value item (vertical position 6)">
        String[] tmpNodeNames = new String[]{ModelMessage.get("CompartmentContainer.section.SimulationBox"), ModelMessage.get("CompartmentContainer.section.Bulk")};
        ValueItem tmpBulkInfoValueItem = this.getBulkInfoValueItemFromMoleculeInfo(tmpMoleculeInfoValueItem);
        tmpBulkInfoValueItem.setVerticalPosition(6);
        tmpBulkInfoValueItem.setNodeNames(tmpNodeNames);
        tmpBulkInfoValueItem.setDisplayName(ModelMessage.get("CompartmentContainer.valueItem.displayName.BULK"));
        tmpBulkInfoValueItem.setBlockName(ModelDefinitions.COMPARTMENT_BLOCK_BULK);
        tmpBulkInfoValueItem.setDescription(ModelMessage.get("CompartmentContainer.valueItem.description.BULK"));
        this.valueItemContainer.addValueItem(tmpBulkInfoValueItem);
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set compartment box">
        this.setCompartmentBox();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add this instance as changeInformation receiver for value item container">
        this.valueItemContainer.addChangeReceiver(this);
        // </editor-fold>
    }

    /**
     * Constructor
     *
     * @param aXmlElement The XML element containing the CompartmentContainer
     * data
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public CompartmentContainer(Element aXmlElement) {
        super();
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXmlElement == null) {
            throw new IllegalArgumentException("aXmlElement is null.");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialize">
        this.initialize();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set value item container">
        if (!this.readXmlInformation(aXmlElement)) {
            throw new IllegalArgumentException("Can not read XML information.");
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set compartment box">
        this.setCompartmentBox();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add this instance as changeInformation receiver for value item container">
        this.valueItemContainer.addChangeReceiver(this);

        // </editor-fold>
    }

    /**
     * Constructor
     */
    private CompartmentContainer() {
        super();
        this.initialize();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a changeInformation receiver
     *
     * @param aChangeNotifier Object that notifies changeInformation
     * @param aChangeInfo Change information
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_ERROR_CHANGE) {
            super.notifyChangeReceiver(this, this.errorChangeInformation);
        } else {
            super.notifyChangeReceiver(this, this.changeInformation);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyDependentValueItemsForUpdate() method">
    /**
     * Notify dependent value items of container for update
     *
     * @param anUpdateNotifierValueItem Value item that notifies update
     */
    @Override
    public void notifyDependentValueItemsForUpdate(ValueItem anUpdateNotifierValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anUpdateNotifierValueItem == null) {
            return;
        }
        if (anUpdateNotifierValueItem.getValueItemContainer() == null) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Geometry data value item">
        if (anUpdateNotifierValueItem.getName().startsWith(ModelDefinitions.GEOMETRY_PREFIX_NAME)) {
            this.checkCompartmentGeometry(anUpdateNotifierValueItem);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Chemical composition value item">
        if (anUpdateNotifierValueItem.getName().startsWith(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME)) {
            // <editor-fold defaultstate="collapsed" desc="Determine change">
            ValueItemMatrixElement[][] tmpLastClonedMatrix = anUpdateNotifierValueItem.getLastClonedMatrix();
            if (tmpLastClonedMatrix == null) {
                return;
            }
            // Chemical composition changeInformation of compartment
            boolean tmpIsCompositionChange = false;
            int tmpChangedRow = -1;
            for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                // Column 2: Chemical composition
                if (!tmpLastClonedMatrix[i][2].getFormattedValue().equals(anUpdateNotifierValueItem.getFormattedValue(i, 2))) {
                    tmpChangedRow = i;
                    tmpIsCompositionChange = true;
                    break;
                }
            }
            // Volume/surface distribution changeInformation
            if (!tmpIsCompositionChange) {
                for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                    // Column 4: Volume/surface distribution changeInformation
                    if (!tmpLastClonedMatrix[i][4].getFormattedValue().equals(anUpdateNotifierValueItem.getFormattedValue(i, 4))) {
                        tmpChangedRow = i;
                        break;
                    }
                }
                if (tmpChangedRow < 0) {
                    // Nothing changed: This should never happen!
                    return;
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Perform updates">
            if (tmpIsCompositionChange) {
                // <editor-fold defaultstate="collapsed" desc="- Necessary value items">
                ValueItem tmpBulkValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem(ModelDefinitions.COMPARTMENT_BULK_NAME);
                ValueItem tmpDensityInfoValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem(ModelDefinitions.DENSITY_INFO_NAME);
                ValueItem tmpMoleculeInfoValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem(ModelDefinitions.MOLECULE_INFO_NAME);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Update bulk and compartment composition">
                // IMPORTANT: All composition values must have the same number of decimals! Set new percentages
                double tmpDifference = Double.parseDouble(tmpLastClonedMatrix[tmpChangedRow][2].getValue()) - anUpdateNotifierValueItem.getValueAsDouble(tmpChangedRow, 2);
                double tmpOldBulkValue = tmpBulkValueItem.getValueAsDouble(tmpChangedRow, 2);
                double tmpNewBulkValue = tmpOldBulkValue + tmpDifference;
                tmpBulkValueItem.setValue(String.valueOf(tmpNewBulkValue), tmpChangedRow, 2);
                // Set new number of molecules
                int tmpSumOfMolecules = anUpdateNotifierValueItem.getValueAsInt(tmpChangedRow, 3) + tmpBulkValueItem.getValueAsInt(tmpChangedRow, 3);
                double tmpBulkFraction = tmpBulkValueItem.getValueAsDouble(tmpChangedRow, 2)
                        / (tmpBulkValueItem.getValueAsDouble(tmpChangedRow, 2) + anUpdateNotifierValueItem.getValueAsDouble(tmpChangedRow, 2));
                int tmpBulkMolecules = (int) Math.floor(tmpBulkFraction * (double) tmpSumOfMolecules);
                int tmpUpdateNotifierMolecules = tmpSumOfMolecules - tmpBulkMolecules;
                tmpBulkValueItem.setValue(String.valueOf(tmpBulkMolecules), tmpChangedRow, 3);
                anUpdateNotifierValueItem.setValue(String.valueOf(tmpUpdateNotifierMolecules), tmpChangedRow, 3);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Update compartment geometry">
                // <editor-fold defaultstate="collapsed" desc="-- Compartment Sphere">
                if (anUpdateNotifierValueItem.getName().startsWith(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_SPHERE)) {
                    ValueItem tmpBoxInfoValueItem = this.getBoxInfoValueItem();
                    if (tmpBoxInfoValueItem == null) {
                        return;
                    }
                    ValueItem tmpGeometryDataValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItemOfBlock(anUpdateNotifierValueItem.getBlockName(),
                            ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_DATA_PREFIX_NAME);
                    int tmpTotalNumberOfParticles = 0;
                    for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                        // Number of molecules * Number of particles per molecule
                        tmpTotalNumberOfParticles += anUpdateNotifierValueItem.getValueAsInt(i, 3) * tmpMoleculeInfoValueItem.getValueAsInt(i, 3);
                    }
                    // NOTE: Radius is cut (!) after tmpNumberOfDecimals decimals so radius is always a little smaller than in "reality": This avoids possible drawing problems.
                    double tmpRadius = this.jobUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticles, tmpDensityInfoValueItem.getValueAsDouble(),
                            tmpGeometryDataValueItem.getTypeFormat(0, 3).getNumberOfDecimals());
                    // Update geometry data AND display value item. NOTE: Geometry data value item is in DPD units ...
                    ValueItemMatrixElement[][] tmpMatrixOfGeometryDataValueItem = tmpGeometryDataValueItem.getMatrix();
                    tmpMatrixOfGeometryDataValueItem[0][3].getTypeFormat().setMinimumValue(Math.min(tmpBoxInfoValueItem.getValueAsDouble(0, 0), Math.min(tmpBoxInfoValueItem.getValueAsDouble(0, 1), tmpBoxInfoValueItem.getValueAsDouble(0, 2))) * ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR);
                    tmpMatrixOfGeometryDataValueItem[0][3].getTypeFormat().setDefaultValue(String.valueOf(tmpRadius));
                    tmpGeometryDataValueItem.setValue(String.valueOf(tmpRadius), 0, 3);
                    // ... and geometry display value item in physical units (Angstrom)
                    ValueItemMatrixElement[][] tmpMatrixOfGeometryDisplayValueItem = tmpGeometryDataValueItem.getDisplayValueItem().getMatrix();
                    // NOTE: Use value from tmpGeometryDataValueItem to avoid roundoff errors
                    tmpMatrixOfGeometryDisplayValueItem[0][3].getTypeFormat().setMinimumValue(tmpGeometryDataValueItem.getMatrix()[0][3].getTypeFormat().getMinimumValue() * this.getLengthConversionFactor());
                    // NOTE: Default value is NOT truly affected by roundoff errors 
                    tmpMatrixOfGeometryDisplayValueItem[0][3].getTypeFormat().setDefaultValue(String.valueOf(tmpRadius * this.getLengthConversionFactor()));
                    tmpGeometryDataValueItem.getDisplayValueItem().setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, 3) * this.getLengthConversionFactor()), 0, 3);
                    this.checkCompartmentGeometry(tmpGeometryDataValueItem);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="-- Compartment xy-layer">
                if (anUpdateNotifierValueItem.getName().startsWith(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_XY_LAYER)) {
                    ValueItem tmpBoxInfoValueItem = this.getBoxInfoValueItem();
                    if (tmpBoxInfoValueItem == null) {
                        return;
                    }
                    ValueItem tmpGeometryDataValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItemOfBlock(anUpdateNotifierValueItem.getBlockName(),
                            ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_DATA_PREFIX_NAME);
                    int tmpTotalNumberOfParticles = 0;
                    for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                        // Number of molecules * Number of particles per molecule
                        tmpTotalNumberOfParticles += anUpdateNotifierValueItem.getValueAsInt(i, 3) * tmpMoleculeInfoValueItem.getValueAsInt(i, 3);
                    }
                    // NOTE: Z-length is cut (!) after tmpNumberOfDecimals decimals so z-length is always a little smaller than in "reality": This avoids possible drawing problems.
                    double tmpZLength = this.jobUtilityMethods.getZLengthOfBoxOfParticlesInDpdBox(tmpTotalNumberOfParticles, tmpDensityInfoValueItem.getValueAsDouble(),
                            tmpGeometryDataValueItem.getValueAsDouble(0, 3), tmpGeometryDataValueItem.getValueAsDouble(0, 4), tmpGeometryDataValueItem.getTypeFormat(0, 3).getNumberOfDecimals());
                    // Update geometry data AND display value item. NOTE: Geometry data value item is in DPD units ...
                    ValueItemMatrixElement[][] tmpMatrixOfGeometryDataValueItem = tmpGeometryDataValueItem.getMatrix();
                    tmpMatrixOfGeometryDataValueItem[0][5].getTypeFormat().setMinimumValue(tmpBoxInfoValueItem.getValueAsDouble(0, 2) * ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR);
                    tmpMatrixOfGeometryDataValueItem[0][5].getTypeFormat().setDefaultValue(String.valueOf(tmpZLength));
                    tmpGeometryDataValueItem.setValue(String.valueOf(tmpZLength), 0, 5);
                    // ... and geometry display value item in physical units (Angstrom)
                    ValueItemMatrixElement[][] tmpMatrixOfGeometryDisplayValueItem = tmpGeometryDataValueItem.getDisplayValueItem().getMatrix();
                    tmpMatrixOfGeometryDisplayValueItem[0][5].getTypeFormat().setMinimumValue(tmpGeometryDataValueItem.getMatrix()[0][5].getTypeFormat().getMinimumValue() * this.getLengthConversionFactor());
                    // NOTE: Default value is NOT truly affected by roundoff errors 
                    tmpMatrixOfGeometryDisplayValueItem[0][5].getTypeFormat().setDefaultValue(String.valueOf(tmpZLength * this.getLengthConversionFactor()));
                    tmpGeometryDataValueItem.getDisplayValueItem().setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, 5) * this.getLengthConversionFactor()), 0, 5);
                    this.checkCompartmentGeometry(tmpGeometryDataValueItem);
                }
                // </editor-fold>
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Update composition percentage value ranges">
                this.updateCompositionPercentageValueRange(tmpBulkValueItem, tmpChangedRow);
                // </editor-fold>
            }
            // <editor-fold defaultstate="collapsed" desc="- Update compartment distribution">
            int tmpNumberOfSurfaceMolecules = (int) Math.floor(anUpdateNotifierValueItem.getValueAsDouble(tmpChangedRow, 4) / 100.0 * anUpdateNotifierValueItem.getValueAsDouble(tmpChangedRow, 3));
            int tmpNumberOfVolumeMolecules = anUpdateNotifierValueItem.getValueAsInt(tmpChangedRow, 3) - tmpNumberOfSurfaceMolecules;
            anUpdateNotifierValueItem.setValue(String.valueOf(tmpNumberOfVolumeMolecules), tmpChangedRow, 5);
            anUpdateNotifierValueItem.setValue(String.valueOf(tmpNumberOfSurfaceMolecules), tmpChangedRow, 6);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update orientation">
            // <editor-fold defaultstate="collapsed" desc="-- Definitions">
            String[] tmpRandomSingleAllCubicArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopBottomSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzLeftRightSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzFrontBackSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyBottomSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzLeftSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzRightSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzFrontSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzBackSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomAllSurfacesOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice")
                };
            String[] tmpRandomSingleAllArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopBottomSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzLeftRightSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzFrontBackSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyBottomSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzLeftSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzRightSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzFrontSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzBackSurfaceOfXyLayer"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomAllSurfacesOfXyLayer")
                };
            String[] tmpRandomCubicArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.random"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice")
                };
            String[] tmpRandomOnlyArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.random")
                };
            String[] tmpNoneArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.none")
                };
            String[] tmpRandom3dArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.random"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom")
                };
            String[] tmpWholeSphereSurfaceArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomWholeSurfaceOfSphere")
                };
            String[] tmpWholeUpperMiddleSphereSurfaceArray = 
                new String[]{
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomWholeSurfaceOfSphere"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomUpperSurfaceOfSphere"),
                    ModelMessage.get("CompartmentContainer.parameter.Orientation.randomMiddleSurfaceOfSphere")
                };
            // </editor-fold>
            if (tmpNumberOfVolumeMolecules == 0 && tmpNumberOfSurfaceMolecules == 0) {
                // <editor-fold defaultstate="collapsed" desc="-- No molecules in compartment: Set orientation to none">
                ValueItemMatrixElement[][] tmpMatrix = anUpdateNotifierValueItem.getMatrix();
                // Orientation is in column with index 7
                tmpMatrix[tmpChangedRow][7] = 
                    new ValueItemMatrixElement(
                        new ValueItemDataTypeFormat(
                            ModelMessage.get("CompartmentContainer.parameter.Orientation.none"), 
                            tmpNoneArray
                        )
                    );
                // <editor-fold defaultstate="collapsed" desc="- Compartment xy-layer">
                if (anUpdateNotifierValueItem.getName().startsWith(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_XY_LAYER)) {
                    if (!this.isLatticeGeometryInXyLayerPossible(anUpdateNotifierValueItem)) {
                        // <editor-fold defaultstate="collapsed" desc="Remove possible simple cubic lattice from other rows">
                        for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                            if (i != tmpChangedRow && !anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.none"))) {
                                if (this.isQuantityOnSurface(anUpdateNotifierValueItem, i)) {
                                    if (anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice"))) {
                                        tmpMatrix[i][7] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), tmpRandomSingleAllArray));
                                    } else {
                                        tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomSingleAllArray);
                                    }
                                } else {
                                    if (anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice"))) {
                                        tmpMatrix[i][7] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(ModelMessage.get("CompartmentContainer.parameter.Orientation.random"),
                                                tmpRandomOnlyArray));
                                    } else {
                                        if (anUpdateNotifierValueItem.isProteinDataInMatrixRow(i)) {
                                            tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandom3dArray);
                                        } else {
                                            tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomOnlyArray);
                                        }
                                    }
                                }
                            }
                        }
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Add possible simple cubic lattice to other rows">
                        for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                            if (i != tmpChangedRow && !anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.none"))) {
                                if (this.isQuantityOnSurface(anUpdateNotifierValueItem, i)) {
                                    tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomSingleAllCubicArray);
                                } else {
                                    tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomCubicArray);
                                }
                            }
                        }
                        // </editor-fold>
                    }
                }
                // </editor-fold>
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="-- Compartment Sphere">
                if (anUpdateNotifierValueItem.getName().startsWith(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_SPHERE)) {
                    ValueItemMatrixElement[][] tmpMatrix = anUpdateNotifierValueItem.getMatrix();
                    // Molecular structure with possible protein data is in column with index 1
                    if (tmpMatrix[tmpChangedRow][1].hasProteinData()) {
                        if (!tmpMatrix[tmpChangedRow][1].getTypeFormat().hasSelectionText(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"))) {
                            // Orientation is in column with index 7
                            if (tmpNumberOfVolumeMolecules == 1) {
                                tmpMatrix[tmpChangedRow][7] = 
                                    new ValueItemMatrixElement(
                                        new ValueItemDataTypeFormat(
                                            ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"), 
                                            tmpRandom3dArray
                                        )
                                    );
                            } else {
                                tmpMatrix[tmpChangedRow][7] = 
                                    new ValueItemMatrixElement(
                                        new ValueItemDataTypeFormat(
                                            ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"), 
                                            tmpRandom3dArray
                                        )
                                    );
                            }
                        }
                    } else {
                        if (tmpNumberOfSurfaceMolecules == 0) {
                            tmpMatrix[tmpChangedRow][7] = 
                                new ValueItemMatrixElement(
                                    new ValueItemDataTypeFormat(
                                        ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), 
                                        tmpRandomOnlyArray
                                    )
                                );
                        } else {
                            // Orientation is in column with index 7
                            tmpMatrix[tmpChangedRow][7] = 
                                new ValueItemMatrixElement(
                                    new ValueItemDataTypeFormat(
                                        ModelMessage.get("CompartmentContainer.parameter.Orientation.randomWholeSurfaceOfSphere"), 
                                        tmpWholeUpperMiddleSphereSurfaceArray
                                    )
                                );
                        }
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="-- Compartment xy-layer">
                if (anUpdateNotifierValueItem.getName().startsWith(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_XY_LAYER)) {
                    boolean tmpIsLatticeGeometryInXyLayerPossible = this.isLatticeGeometryInXyLayerPossible(anUpdateNotifierValueItem);
                    boolean tmpIsQuantityOnSurface = this.isQuantityOnSurface(anUpdateNotifierValueItem, tmpChangedRow);
                    // Orientation is in column with index 7
                    String tmpCurrentSelection = anUpdateNotifierValueItem.getValue(tmpChangedRow, 7);
                    ValueItemMatrixElement[][] tmpMatrix = anUpdateNotifierValueItem.getMatrix();
                    if (tmpCurrentSelection.equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.none"))) {
                        // <editor-fold defaultstate="collapsed" desc="Orientation was none">
                        if (tmpIsLatticeGeometryInXyLayerPossible) {
                            // <editor-fold defaultstate="collapsed" desc="Simple cubic lattice is possible">
                            if (tmpIsQuantityOnSurface) {
                                tmpMatrix[tmpChangedRow][7] = 
                                    new ValueItemMatrixElement(
                                        new ValueItemDataTypeFormat(
                                            ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopBottomSurfaceOfXyLayer"), 
                                            tmpRandomSingleAllCubicArray
                                        )
                                    );
                            } else {
                                tmpMatrix[tmpChangedRow][7] = 
                                    new ValueItemMatrixElement(
                                        new ValueItemDataTypeFormat(
                                            ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), 
                                            tmpRandomCubicArray
                                        )
                                    );
                            }
                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="No simple cubic lattice possible">
                            if (tmpIsQuantityOnSurface) {
                                tmpMatrix[tmpChangedRow][7] = 
                                    new ValueItemMatrixElement(
                                        new ValueItemDataTypeFormat(
                                            ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopBottomSurfaceOfXyLayer"),
                                            tmpRandomSingleAllArray
                                        )
                                    );
                            } else {
                                if (anUpdateNotifierValueItem.isProteinDataInMatrixRow(tmpChangedRow)) {
                                    tmpMatrix[tmpChangedRow][7] = 
                                        new ValueItemMatrixElement(
                                            new ValueItemDataTypeFormat(
                                                ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"), 
                                                tmpRandom3dArray
                                            )
                                        );
                                } else {
                                    tmpMatrix[tmpChangedRow][7] = 
                                        new ValueItemMatrixElement(
                                            new ValueItemDataTypeFormat(
                                                ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), 
                                                tmpRandomOnlyArray
                                            )
                                        );
                                }
                            }
                            // </editor-fold>
                        }
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Orientation was NOT none">
                        if (tmpIsLatticeGeometryInXyLayerPossible) {
                            // <editor-fold defaultstate="collapsed" desc="Simple cubic lattice is possible">
                            if (tmpCurrentSelection.equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice"))) {
                                if (tmpIsQuantityOnSurface) {
                                    tmpMatrix[tmpChangedRow][7] = 
                                        new ValueItemMatrixElement(
                                            new ValueItemDataTypeFormat(
                                                tmpCurrentSelection, 
                                                tmpRandomSingleAllCubicArray
                                            )
                                        );
                                }
                            } else {
                                if (tmpIsQuantityOnSurface) {
                                    tmpMatrix[tmpChangedRow][7] = 
                                        new ValueItemMatrixElement(
                                            new ValueItemDataTypeFormat(
                                                ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopBottomSurfaceOfXyLayer"), 
                                                tmpRandomSingleAllCubicArray
                                            )
                                        );
                                } else {
                                    tmpMatrix[tmpChangedRow][7] = 
                                        new ValueItemMatrixElement(
                                            new ValueItemDataTypeFormat(
                                                ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), 
                                                tmpRandomCubicArray
                                            )
                                        );
                                }
                            }
                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="No simple cubic lattice possible">
                            if (tmpIsQuantityOnSurface) {
                                tmpMatrix[tmpChangedRow][7] = 
                                    new ValueItemMatrixElement(
                                        new ValueItemDataTypeFormat(
                                            ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopBottomSurfaceOfXyLayer"),
                                            tmpRandomSingleAllArray
                                        )
                                    );
                            } else {
                                if (anUpdateNotifierValueItem.isProteinDataInMatrixRow(tmpChangedRow)) {
                                    if (tmpCurrentSelection.equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"))) {
                                        tmpMatrix[tmpChangedRow][7] = 
                                            new ValueItemMatrixElement(
                                                new ValueItemDataTypeFormat(
                                                    ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"), 
                                                    tmpRandom3dArray
                                                )
                                            );
                                    } else if (tmpCurrentSelection.equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"))) {
                                        tmpMatrix[tmpChangedRow][7] = 
                                            new ValueItemMatrixElement(
                                                new ValueItemDataTypeFormat(
                                                    ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"), 
                                                    tmpRandom3dArray
                                                )
                                            );
                                    } else {
                                        tmpMatrix[tmpChangedRow][7] = 
                                            new ValueItemMatrixElement(
                                                new ValueItemDataTypeFormat(
                                                    ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), 
                                                    tmpRandom3dArray
                                                )
                                            );
                                    }
                                } else {
                                    tmpMatrix[tmpChangedRow][7] = 
                                        new ValueItemMatrixElement(
                                            new ValueItemDataTypeFormat(
                                                ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), 
                                                tmpRandomOnlyArray
                                            )
                                        );
                                }
                            }
                            // </editor-fold>
                        }
                        // </editor-fold>
                    }
                    if (!tmpIsLatticeGeometryInXyLayerPossible) {
                        // <editor-fold defaultstate="collapsed" desc="No simple cubic lattice possible: Remove possible simple cubic lattice from other rows">
                        for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                            if (i != tmpChangedRow && !anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.none"))) {
                                if (this.isQuantityOnSurface(anUpdateNotifierValueItem, i)) {
                                    if (anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice"))) {
                                        tmpMatrix[i][7] = 
                                            new ValueItemMatrixElement(new ValueItemDataTypeFormat(ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), tmpRandomSingleAllArray));
                                    } else {
                                        tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomSingleAllArray);
                                    }
                                } else {
                                    if (anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice"))) {
                                        tmpMatrix[i][7] = 
                                            new ValueItemMatrixElement(new ValueItemDataTypeFormat(ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), tmpRandomOnlyArray));
                                    } else {
                                        if (anUpdateNotifierValueItem.isProteinDataInMatrixRow(i)) {
                                            tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandom3dArray);
                                        } else {
                                            tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomOnlyArray);
                                        }
                                    }
                                }
                            }
                        }
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Simple cubic lattice is possible: Add possible simple cubic lattice to other rows">
                        for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                            if (i != tmpChangedRow && !anUpdateNotifierValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.none"))) {
                                if (this.isQuantityOnSurface(anUpdateNotifierValueItem, i)) {
                                    tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomSingleAllCubicArray);
                                } else {
                                    tmpMatrix[i][7].getTypeFormat().setSelectionTexts(tmpRandomCubicArray);
                                }
                            }
                        }
                        // </editor-fold>
                    }
                }
                // </editor-fold>
            }
            // </editor-fold>
            // </editor-fold>
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Compartment container related methods">
    /**
     * Returns modified compartment container according to changed quantities
     * (for details see code)
     *
     * @param aModifiedValueItemContainer Value item container with modified
     * value items
     * @return Modified compartment container or null if compartment container
     * could not be modified
     */
    public CompartmentContainer getModifiedCompartmentContainer(ValueItemContainer aModifiedValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        // A compartment container has to contain compartments to be modified
        if (!this.hasCompartments()) {
            return null;
        }

        if (aModifiedValueItemContainer == null) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Check modification possibility">
            // Modifications are only possible if molecules are unchanged
            CompartmentContainer tmpModifiedCompartmentContainer = new CompartmentContainer(aModifiedValueItemContainer);
            ValueItem tmpMoleculeInfoValueItem = this.getMoleculeInfoValueItem();
            ValueItem tmpModifiedMoleculeInfoValueItem = tmpModifiedCompartmentContainer.getMoleculeInfoValueItem();
            if (!jobUtilityMethods.areMoleculesEqual(tmpMoleculeInfoValueItem, tmpModifiedMoleculeInfoValueItem)) {
                return null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Modify compartments">
            LinkedList<String> tmpBlockNameList = this.valueItemContainer.getBlockNames();
            // Important: Sort block names so that new creation and modification follows the same order
            Collections.sort(tmpBlockNameList);
            for (String tmpBlockName : tmpBlockNameList) {
                // <editor-fold defaultstate="collapsed" desc="Modify single compartment (sphere or xy-layer)">
                // <editor-fold defaultstate="collapsed" desc="- Create empty compartment in modified compartment container">
                String tmpNewBlockNameInModifiedCompartmentContainer = null;
                if (this.isSphereCompartment(tmpBlockName)) {
                    ValueItem tmpCompartmentSpecifiedNameValueItem = this.getSphereCompartmentSpecifiedNameValueItemOfBlock(tmpBlockName);
                    String tmpCompartmentName = "";
                    if (tmpCompartmentSpecifiedNameValueItem != null) {
                        tmpCompartmentName = tmpCompartmentSpecifiedNameValueItem.getValue();
                    }
                    // Sphere compartment with tmpBlockName is added (see sort of tmpBlockNameList above)
                    tmpNewBlockNameInModifiedCompartmentContainer = tmpModifiedCompartmentContainer.addCompartmentSphere(tmpCompartmentName);
                } else if (this.isXyLayerCompartment(tmpBlockName)) {
                    ValueItem tmpCompartmentSpecifiedNameValueItem = this.getXyLayerCompartmentSpecifiedNameValueItemOfBlock(tmpBlockName);
                    String tmpCompartmentName = "";
                    if (tmpCompartmentSpecifiedNameValueItem != null) {
                        tmpCompartmentName = tmpCompartmentSpecifiedNameValueItem.getValue();
                    }
                    // Xy-Layer compartment with tmpBlockName is added (see sort of tmpBlockNameList above)
                    tmpNewBlockNameInModifiedCompartmentContainer = tmpModifiedCompartmentContainer.addCompartmentXyLayer(tmpCompartmentName);
                } else {
                    continue;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Modify empty compartment in modified compartment container">
                ValueItem tmpChemicalCompositionValueItem = this.getChemicalCompositionValueItemOfBlock(tmpBlockName);
                ValueItem tmpModifiedChemicalCompositionValueItem = 
                    tmpModifiedCompartmentContainer.getChemicalCompositionValueItemOfBlock(tmpNewBlockNameInModifiedCompartmentContainer);
                ValueItem tmpModifiedBulkInfoValueItem = tmpModifiedCompartmentContainer.getBulkInfoValueItem();
                ValueItem tmpModifiedDensityInfoValueItem = tmpModifiedCompartmentContainer.getDensityInfoValueItem();
                for (int i = 0; i < tmpChemicalCompositionValueItem.getMatrixRowCount(); i++) {
                    // Column 2: Chemical composition in percent, column 4: Volume/surface distribution in percent
                    if (tmpChemicalCompositionValueItem.getValueAsDouble(i, 2) != tmpModifiedChemicalCompositionValueItem.getValueAsDouble(i, 2)
                        || tmpChemicalCompositionValueItem.getValueAsDouble(i, 4) != tmpModifiedChemicalCompositionValueItem.getValueAsDouble(i, 4)) {
                        // <editor-fold defaultstate="collapsed" desc="-- Update bulk and compartment composition">
                        // IMPORTANT: All composition values must have the same number of decimals! Set new percentages
                        double tmpNewBulkValue = tmpModifiedBulkInfoValueItem.getValueAsDouble(i, 2) - tmpChemicalCompositionValueItem.getValueAsDouble(i, 2);
                        tmpModifiedBulkInfoValueItem.setValue(String.valueOf(tmpNewBulkValue), i, 2);
                        tmpModifiedChemicalCompositionValueItem.setValue(tmpChemicalCompositionValueItem.getValue(i, 2), i, 2);
                        // Set new number of molecules
                        int tmpSumOfMolecules = tmpModifiedChemicalCompositionValueItem.getValueAsInt(i, 3) + tmpModifiedBulkInfoValueItem.getValueAsInt(i, 3);
                        double tmpBulkFraction = tmpModifiedBulkInfoValueItem.getValueAsDouble(i, 2)
                                / (tmpModifiedBulkInfoValueItem.getValueAsDouble(i, 2) + tmpModifiedChemicalCompositionValueItem.getValueAsDouble(i, 2));
                        int tmpBulkMolecules = (int) Math.floor(tmpBulkFraction * (double) tmpSumOfMolecules);
                        int tmpCompartmentMolecules = tmpSumOfMolecules - tmpBulkMolecules;
                        tmpModifiedBulkInfoValueItem.setValue(String.valueOf(tmpBulkMolecules), i, 3);
                        tmpModifiedChemicalCompositionValueItem.setValue(String.valueOf(tmpCompartmentMolecules), i, 3);
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="-- Update compartment geometry">
                        // <editor-fold defaultstate="collapsed" desc="--- Compartment Sphere">
                        if (this.isSphereCompartment(tmpBlockName)) {
                            ValueItem tmpModifiedGeometryDataValueItem = 
                                tmpModifiedCompartmentContainer.getValueItemContainer().getValueItemOfBlock(
                                    tmpNewBlockNameInModifiedCompartmentContainer,
                                    ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_DATA_PREFIX_NAME
                                );
                            int tmpTotalNumberOfParticles = 0;
                            for (int k = 0; k < tmpModifiedChemicalCompositionValueItem.getMatrixRowCount(); k++) {
                                // Number of molecules * Number of particles per molecule
                                tmpTotalNumberOfParticles += tmpModifiedChemicalCompositionValueItem.getValueAsInt(k, 3) * tmpModifiedMoleculeInfoValueItem.getValueAsInt(k, 3);
                            }
                            // NOTE: Radius is cut (!) after tmpNumberOfDecimals decimals so radius is always a little smaller than in "reality": This avoids possible drawing problems.
                            double tmpRadius = this.jobUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticles, tmpModifiedDensityInfoValueItem.getValueAsDouble(),
                                    tmpModifiedGeometryDataValueItem.getTypeFormat(0, 3).getNumberOfDecimals());
                            // Update geometry data AND display value item. NOTE: Geometry data value item is in DPD units ...
                            tmpModifiedGeometryDataValueItem.setValue(String.valueOf(tmpRadius), 0, 3);
                            // ... and geometry display value item in physical units (Angstrom)
                            tmpModifiedGeometryDataValueItem.getDisplayValueItem().setValue(String.valueOf(tmpRadius * tmpModifiedCompartmentContainer.getLengthConversionFactor()), 0, 3);
                            tmpModifiedCompartmentContainer.checkCompartmentGeometry(tmpModifiedGeometryDataValueItem);
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="--- Compartment xy-layer">
                        if (this.isXyLayerCompartment(tmpBlockName)) {
                            ValueItem tmpGeometryDataValueItem = 
                                this.getValueItemContainer().getValueItemOfBlock(
                                    tmpBlockName,
                                    ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_DATA_PREFIX_NAME
                                );
                            ValueItem tmpModifiedGeometryDataValueItem = 
                                tmpModifiedCompartmentContainer.getValueItemContainer().getValueItemOfBlock(
                                    tmpNewBlockNameInModifiedCompartmentContainer,
                                    ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_DATA_PREFIX_NAME
                                );
                            int tmpTotalNumberOfParticles = 0;
                            for (int k = 0; k < tmpModifiedChemicalCompositionValueItem.getMatrixRowCount(); k++) {
                                // Number of molecules * Number of particles per molecule
                                tmpTotalNumberOfParticles += tmpModifiedChemicalCompositionValueItem.getValueAsInt(k, 3) * tmpModifiedMoleculeInfoValueItem.getValueAsInt(k, 3);
                            }
                            // NOTE: Z-length is cut (!) after tmpNumberOfDecimals decimals so z-length is always a little smaller than in "reality": This avoids possible drawing problems.
                            double tmpZLength = this.jobUtilityMethods.getZLengthOfBoxOfParticlesInDpdBox(tmpTotalNumberOfParticles, tmpModifiedDensityInfoValueItem.getValueAsDouble(),
                                    tmpModifiedGeometryDataValueItem.getValueAsDouble(0, 3), tmpModifiedGeometryDataValueItem.getValueAsDouble(0, 4),
                                    tmpModifiedGeometryDataValueItem.getTypeFormat(0, 3).getNumberOfDecimals());
                            // Update geometry data AND display value item. NOTE: Geometry data value item is in DPD units ...
                            tmpModifiedGeometryDataValueItem.setValue(String.valueOf(tmpZLength), 0, 5);
                            // ... and geometry display value item in physical units (Angstrom)
                            tmpModifiedGeometryDataValueItem.getDisplayValueItem().setValue(String.valueOf(tmpZLength * tmpModifiedCompartmentContainer.getLengthConversionFactor()), 0, 5);

                            // Move modified xy-layder compartment to top or bottom if it was there before
                            if (this.isXyLayerCompartmentNearTop(tmpGeometryDataValueItem)) {
                                tmpModifiedCompartmentContainer.moveXyLayerCompartmentToTop(tmpModifiedGeometryDataValueItem);
                            } else if (this.isXyLayerCompartmentNearBottom(tmpGeometryDataValueItem)) {
                                tmpModifiedCompartmentContainer.moveXyLayerCompartmentToBottom(tmpModifiedGeometryDataValueItem);
                            }

                            tmpModifiedCompartmentContainer.checkCompartmentGeometry(tmpModifiedGeometryDataValueItem);
                        }

                        // </editor-fold>
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="-- Update composition percentage value ranges">
                        tmpModifiedCompartmentContainer.updateCompositionPercentageValueRange(tmpModifiedBulkInfoValueItem, i);
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="-- Update compartment distribution">
                        // Set percentage on surface
                        tmpModifiedChemicalCompositionValueItem.setValue(tmpChemicalCompositionValueItem.getValue(i, 4), i, 4);
                        int tmpNumberOfSurfaceMolecules = 
                            (int) Math.floor(tmpModifiedChemicalCompositionValueItem.getValueAsDouble(i, 4) / 100.0 * tmpModifiedChemicalCompositionValueItem.getValueAsDouble(i, 3));
                        int tmpNumberOfVolumeMolecules = tmpModifiedChemicalCompositionValueItem.getValueAsInt(i, 3) - tmpNumberOfSurfaceMolecules;
                        tmpModifiedChemicalCompositionValueItem.setValue(String.valueOf(tmpNumberOfVolumeMolecules), i, 5);
                        tmpModifiedChemicalCompositionValueItem.setValue(String.valueOf(tmpNumberOfSurfaceMolecules), i, 6);
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="-- Update orientation">
                        ValueItemMatrixElement[][] tmpModifiedMatrix = tmpModifiedChemicalCompositionValueItem.getMatrix();
                        // Orientation is in column with index 7
                        tmpModifiedMatrix[i][7] = tmpChemicalCompositionValueItem.getMatrix()[i][7];
                        // </editor-fold>
                    }
                }
                // </editor-fold>
                // </editor-fold>
            }
            // </editor-fold>
            return tmpModifiedCompartmentContainer;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, "Compartment container could not be modified due to an internal error. As a safeguard all compartments are removed.");
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Compartment related methods">
    /**
     * Adds a spherical compartment
     */
    public void addCompartmentSphere() {
        this.addCompartmentSphere(this.compartmentName);
    }

    /**
     * Adds a xy-layer compartment
     */
    public void addCompartmentXyLayer() {
        this.addCompartmentXyLayer(this.compartmentName);
    }

    /**
     * Returns whether compartment container has defined compartments
     *
     * @return True: Compartment container has defined compartments, false:
     * Otherwise
     */
    public boolean hasCompartments() {
        return this.compartmentBox.hasBodies();
    }

    /**
     * Returns if a block name is a compartment
     *
     * @param aBlockName Block name
     * @return True: Block name is a compartment, false: Otherwise
     */
    public boolean isCompartment(String aBlockName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return aBlockName.startsWith(ModelDefinitions.COMPARTMENT_BLOCK_SPHERE) || aBlockName.startsWith(ModelDefinitions.COMPARTMENT_BLOCK_XY_LAYER);
    }

    /**
     * Returns if a block name is a sphere compartment
     *
     * @param aBlockName Block name
     * @return True: Block name is a sphere compartment, false: Otherwise
     */
    public boolean isSphereCompartment(String aBlockName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return aBlockName.startsWith(ModelDefinitions.COMPARTMENT_BLOCK_SPHERE);
    }

    /**
     * Returns if a block name is a xy-layer compartment
     *
     * @param aBlockName Block name
     * @return True: Block name is a xy-layer compartment, false: Otherwise
     */
    public boolean isXyLayerCompartment(String aBlockName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return aBlockName.startsWith(ModelDefinitions.COMPARTMENT_BLOCK_XY_LAYER);
    }

    /**
     * Removes specified compartment
     *
     * @param aBlockName Block name
     * @return True: Operation successful (compartment was removed), false:
     * Otherwise (nothing changed)
     */
    public boolean removeCompartment(String aBlockName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Return molecules to bulk">
        ValueItem tmpChemicalCompositionValueItem = this.getChemicalCompositionValueItemOfBlock(aBlockName);
        if (tmpChemicalCompositionValueItem == null) {
            return false;
        }
        ValueItem tmpBulkInfoValueItem = this.getBulkInfoValueItem();
        for (int i = 0; i < tmpBulkInfoValueItem.getMatrixRowCount(); i++) {
            // Add percentages to bulk
            double tmpPercentageComposition = tmpChemicalCompositionValueItem.getValueAsDouble(i, 2);
            double tmpPercentageBulk = tmpBulkInfoValueItem.getValueAsDouble(i, 2);
            tmpBulkInfoValueItem.setValue(String.valueOf(tmpPercentageBulk + tmpPercentageComposition), i, 2);
            // Add molecules to bulk
            int tmpMoleculesComposition = tmpChemicalCompositionValueItem.getValueAsInt(i, 3);
            int tmpMoleculesBulk = tmpBulkInfoValueItem.getValueAsInt(i, 3);
            tmpBulkInfoValueItem.setValue(String.valueOf(tmpMoleculesBulk + tmpMoleculesComposition), i, 3);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update composition percentage value ranges">
        this.updateCompositionPercentageValueRange(tmpBulkInfoValueItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Remove corresponding body from simulation box">
        ValueItem tmpGeometryDataValueItem = this.getGeometryDataValueItemOfBlock(aBlockName);
        this.compartmentBox.removeBody(tmpGeometryDataValueItem);

        // </editor-fold>
        return this.valueItemContainer.removeValueItemsOfBlock(aBlockName);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods">
    /**
     * Returns box info value item
     *
     * @return Box info value item or null if none exists
     */
    public ValueItem getBoxInfoValueItem() {
        return this.valueItemContainer.getValueItem(ModelDefinitions.BOX_INFO_NAME);
    }

    /**
     * Returns density info value item
     *
     * @return Density info value item or null if none exists
     */
    public ValueItem getDensityInfoValueItem() {
        return this.valueItemContainer.getValueItem(ModelDefinitions.DENSITY_INFO_NAME);
    }

    /**
     * Returns bulk info value item
     *
     * @return Bulk info value item or null if none exists
     */
    public ValueItem getBulkInfoValueItem() {
        return this.valueItemContainer.getValueItem(ModelDefinitions.COMPARTMENT_BULK_NAME);
    }

    /**
     * Returns length conversion value item
     *
     * @return Length conversion value item or null if none exists
     */
    public ValueItem getLengthConversionValueItem() {
        return this.valueItemContainer.getValueItem(ModelDefinitions.COMPARTMENT_LENGTH_CONVERSION_NAME);
    }

    /**
     * Returns molecule info value item
     *
     * @return Molecule info value item or null if none exists
     */
    public ValueItem getMoleculeInfoValueItem() {
        return this.valueItemContainer.getValueItem(ModelDefinitions.MOLECULE_INFO_NAME);
    }

    /**
     * Returns particle info value item
     *
     * @return Particle info value item or null if none exists
     */
    public ValueItem getParticleInfoValueItem() {
        return this.valueItemContainer.getValueItem(ModelDefinitions.PARTICLE_INFO_NAME);
    }

    /**
     * Returns geometry data value item of block
     *
     * @param aBlockName Name of block
     * @return Geometry data value item of block or null if none exists
     */
    public ValueItem getGeometryDataValueItemOfBlock(String aBlockName) {
        ValueItem tmpGeometryValueItem = this.valueItemContainer.getValueItemOfBlock(aBlockName, ModelDefinitions.GEOMETRY_PREFIX_NAME);
        if (this.graphicsUtilityMethods.isSphereGeometryDataValueItem(tmpGeometryValueItem)) {
            return tmpGeometryValueItem;
        }
        if (this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(tmpGeometryValueItem)) {
            return tmpGeometryValueItem;
        }
        if (this.graphicsUtilityMethods.isSphereGeometryDisplayValueItem(tmpGeometryValueItem)) {
            return tmpGeometryValueItem.getDataValueItem();
        }
        if (this.graphicsUtilityMethods.isXyLayerGeometryDisplayValueItem(tmpGeometryValueItem)) {
            return tmpGeometryValueItem.getDataValueItem();
        }
        return null;
    }

    /**
     * Returns sphere compartment specified name value item of block
     *
     * @param aBlockName Name of block
     * @return Sphere compartment specified name value item of block or null if 
     * none exists
     */
    public ValueItem getSphereCompartmentSpecifiedNameValueItemOfBlock(String aBlockName) {
        return this.valueItemContainer.getValueItemOfBlock(aBlockName, ModelDefinitions.COMPARTMENT_SPHERE_SPECIFIED_NAME);
    }

    /**
     * Returns xy-layer compartment specified name value item of block
     *
     * @param aBlockName Name of block
     * @return xy-Layer compartment specified name value item of block or null if none exists
     */
    public ValueItem getXyLayerCompartmentSpecifiedNameValueItemOfBlock(String aBlockName) {
        return this.valueItemContainer.getValueItemOfBlock(aBlockName, ModelDefinitions.COMPARTMENT_XY_LAYER_SPECIFIED_NAME);
    }

    /**
     * Returns chemical composition value item of block
     *
     * @param aBlockName Name of block
     * @return Chemical composition value item of block or null if none exists
     */
    public ValueItem getChemicalCompositionValueItemOfBlock(String aBlockName) {
        return this.valueItemContainer.getValueItemOfBlock(aBlockName, ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME);
    }

    /**
     * Returns sphere chemical composition value item of block
     *
     * @param aBlockName Name of block
     * @return Sphere chemical composition value item of block or null if none
     * exists
     */
    public ValueItem getSphereChemicalCompositionValueItemOfBlock(String aBlockName) {
        return this.valueItemContainer.getValueItemOfBlock(aBlockName, ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_SPHERE);
    }

    /**
     * Returns xy-layer chemical composition value item of block
     *
     * @param aBlockName Name of block
     * @return Xy-layer chemical composition value item of block or null if none
     * exists
     */
    public ValueItem getXyLayerChemicalCompositionValueItemOfBlock(String aBlockName) {
        return this.valueItemContainer.getValueItemOfBlock(aBlockName, ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_XY_LAYER);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Composition update related methods">
    /**
     * Updates ranges of percentage values of composition value items of value
     * item container of bulk value item
     *
     * @param aBulkValueItem Bulk value item
     */
    public void updateCompositionPercentageValueRange(ValueItem aBulkValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBulkValueItem == null) {
            return;
        }

        // </editor-fold>
        for (int i = 0; i < aBulkValueItem.getMatrixRowCount(); i++) {
            this.updateCompositionPercentageValueRange(aBulkValueItem, i);
        }
    }

    /**
     * Updates ranges of percentage values of specified row of composition value
     * items of value item container of bulk value item
     *
     * @param aBulkValueItem Bulk value item
     * @param aRow Row
     */
    public void updateCompositionPercentageValueRange(ValueItem aBulkValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBulkValueItem == null) {
            return;
        }
        if (aRow < 0 || aRow >= aBulkValueItem.getMatrixRowCount()) {
            return;
        }
        // </editor-fold>
        LinkedList<ValueItem> tmpChemicalCompositionValueItemList = 
            aBulkValueItem.getValueItemContainer().getValueItems(
                new String[]{
                    ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME
                }
            );
        for (ValueItem tmpChemicalCompositionValueItem : tmpChemicalCompositionValueItemList) {
            double tmpCurrentValue = tmpChemicalCompositionValueItem.getValueAsDouble(aRow, 2);
            double tmpCurrentBulkValue = aBulkValueItem.getValueAsDouble(aRow, 2);
            double tmpNewMaximumValue = tmpCurrentValue + tmpCurrentBulkValue;
            tmpChemicalCompositionValueItem.getTypeFormat(aRow, 2).setMaximumValue(tmpNewMaximumValue);
        }
    }

    /**
     * Checks if lattice geometry is possible in xy-layer
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @return True: Lattice geometry is possible, false: Otherwise
     */
    public boolean isLatticeGeometryInXyLayerPossible(ValueItem aChemicalCompositionValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }

        // </editor-fold>
        // Only one molecule type is allowed for lattice geometry
        int tmpCounter = 0;
        int tmpSingleRowWithMolecules = 0;
        for (int i = 0; i < aChemicalCompositionValueItem.getMatrixRowCount(); i++) {
            if (aChemicalCompositionValueItem.getValueAsDouble(i, 2) > 0.0) {
                tmpCounter++;
                tmpSingleRowWithMolecules = i;
            }
        }
        if (tmpCounter == 0 || tmpCounter > 1) {
            return false;
        }

        // Molecular structure (NOTE: Molecular structure does NOT contain any monomer shortcuts)
        String tmpMolecularStructureString = aChemicalCompositionValueItem.getValue(tmpSingleRowWithMolecules, 1);
        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
        // Molecule for lattice geometry is allowed to contain one particle only
        boolean tmpReturnValue = tmpSpices.getTotalNumberOfParticles() == 1;
        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        return tmpReturnValue;
    }

    /**
     * Checks if quantity is on surface
     *
     * @param aChemicalCompositionValueItem Chemical composition value item (is
     * NOT changed)
     * @param aRow Row in aChemicalCompositionValueItem
     * @return True: Quantity is on surface, false: Otherwise
     */
    public boolean isQuantityOnSurface(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        // Column 4: Pecentage on Surface
        return aChemicalCompositionValueItem.getValueAsDouble(aRow, 4) > 0.0;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns clone of this instance
     *
     * @return Clone of this instance
     */
    public CompartmentContainer getClone() {
        CompartmentContainer tmpCompartmentContainer = new CompartmentContainer();
        tmpCompartmentContainer.setValueItemContainer(this.valueItemContainer.getClone());
        return tmpCompartmentContainer;
    }

    /**
     * Returns standard particle radius in DPD units
     *
     * @return standard particle radius in DPD units
     */
    public double getStandardParticleRadius() {
        return this.jobUtilityMethods.getStandardParticleRadius(this.getDensityInfoValueItem());
    }

    /**
     * Factor that converts DPD length to physical length (Angstrom since
     * particle volumes are in Angstrom^3). NOTE: Value is cached for multiple
     * fast calls of this value.
     *
     * @return Factor that converts DPD length to physical length (Angstrom
     * since particle volumes are in Angstrom^3)
     */
    public double getLengthConversionFactor() {
        if (this.lengthConversionFactor == -1.0) {
            this.lengthConversionFactor = this.getLengthConversionValueItem().getValueAsDouble();
        }
        return this.lengthConversionFactor;
    }

    /**
     * Return hashmap that maps molecule name to its particle hashmap that maps
     * a particle to its graphical particle
     *
     * @return HashMap that maps molecule name to its particle hashmap that maps
     * a particle to its graphical particle
     */
    public HashMap<String, HashMap<String, IGraphicalParticle>> getMoleculeToParticlesMap() {
        return this.jobUtilityMethods.getMoleculeToParticlesMap(this.getDensityInfoValueItem(), this.getMoleculeInfoValueItem(), this.getParticleInfoValueItem());
    }

    /**
     * Returns total number of particles for simulation
     *
     * @return Total number of particles for simulation
     */
    public int getTotalNumberOfParticles() {
        return this.jobUtilityMethods.getTotalNumberOfParticlesInSimulation(this.getMoleculeInfoValueItem());
    }

    /**
     * Returns if at least one value item of value item container has an error
     *
     * @return True: At least one value item of value item container has an
     * error, false: Otherwise
     */
    public boolean hasError() {
        return this.valueItemContainer.hasError();
    }

    /**
     * Checks compartment geometry
     *
     * @param aCompartmentGeometryValueItem Compartment geometry value item
     */
    public void checkCompartmentGeometry(ValueItem aCompartmentGeometryValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCompartmentGeometryValueItem == null) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Compartment Sphere">
        if (this.graphicsUtilityMethods.isSphereGeometryValueItem(aCompartmentGeometryValueItem)) {
            ValueItem tmpGeometryDataValueItem = null;
            ValueItem tmpGeometryDisplayValueItem = null;
            if (this.graphicsUtilityMethods.isSphereGeometryDataValueItem(aCompartmentGeometryValueItem)) {
                // <editor-fold defaultstate="collapsed" desc="aCompartmentGeometryValueItem = Geometry data value item">
                tmpGeometryDataValueItem = aCompartmentGeometryValueItem;
                tmpGeometryDisplayValueItem = tmpGeometryDataValueItem.getDisplayValueItem();
                // Update values of display value item
                for (int i = 0; i < 4; i++) {
                    tmpGeometryDisplayValueItem.setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, i) * this.getLengthConversionFactor()), 0, i);
                }
                int tmpIndex = 0;
                for (int i = 4; i < 8; i++) {
                    tmpGeometryDisplayValueItem.setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, tmpIndex++)), 0, i);
                }
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="aCompartmentGeometryValueItem = Geometry display value item">
                tmpGeometryDisplayValueItem = aCompartmentGeometryValueItem;
                tmpGeometryDataValueItem = tmpGeometryDisplayValueItem.getDataValueItem();
                // Update values of data value item
                for (int i = 0; i < 4; i++) {
                    tmpGeometryDataValueItem.setValue(String.valueOf(tmpGeometryDisplayValueItem.getValueAsDouble(0, i) / this.getLengthConversionFactor()), 0, i);
                }
                // ... and display value item
                int tmpIndex = 0;
                for (int i = 4; i < 8; i++) {
                    tmpGeometryDisplayValueItem.setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, tmpIndex++)), 0, i);
                }
                // </editor-fold>
            }
            // <editor-fold defaultstate="collapsed" desc="Check geometry: Does compartment peer out of box?">
            if (this.compartmentBox.isOutOfBox(tmpGeometryDataValueItem)) {
                tmpGeometryDataValueItem.setError(ModelMessage.get("CompartmentContainer.valueItem.error.outOfBox"));
                tmpGeometryDisplayValueItem.setError(ModelMessage.get("CompartmentContainer.valueItem.error.outOfBox"));
            } else {
                tmpGeometryDataValueItem.removeError();
                tmpGeometryDisplayValueItem.removeError();
            }
            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Compartment xy-layer">
        if (this.graphicsUtilityMethods.isXyLayerGeometryValueItem(aCompartmentGeometryValueItem)) {
            ValueItem tmpGeometryDataValueItem = null;
            ValueItem tmpGeometryDisplayValueItem = null;
            if (this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(aCompartmentGeometryValueItem)) {
                // <editor-fold defaultstate="collapsed" desc="aCompartmentGeometryValueItem = Geometry data value item">
                tmpGeometryDataValueItem = aCompartmentGeometryValueItem;
                tmpGeometryDisplayValueItem = tmpGeometryDataValueItem.getDisplayValueItem();
                // Update values of display value item
                for (int i = 0; i < 6; i++) {
                    tmpGeometryDisplayValueItem.setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, i) * this.getLengthConversionFactor()), 0, i);
                }
                int tmpIndex = 0;
                for (int i = 6; i < 12; i++) {
                    tmpGeometryDisplayValueItem.setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, tmpIndex++)), 0, i);
                }
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="aCompartmentGeometryValueItem = Geometry display value item">
                tmpGeometryDisplayValueItem = aCompartmentGeometryValueItem;
                tmpGeometryDataValueItem = tmpGeometryDisplayValueItem.getDataValueItem();
                // Update values of data value item
                for (int i = 0; i < 6; i++) {
                    tmpGeometryDataValueItem.setValue(String.valueOf(tmpGeometryDisplayValueItem.getValueAsDouble(0, i) / this.getLengthConversionFactor()), 0, i);
                }
                // ... and display value item
                int tmpIndex = 0;
                for (int i = 6; i < 12; i++) {
                    tmpGeometryDisplayValueItem.setValue(String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, tmpIndex++)), 0, i);
                }
                // </editor-fold>
            }
            // <editor-fold defaultstate="collapsed" desc="Check geometry: Does compartment peer out of box?">
            if (this.compartmentBox.isOutOfBox(tmpGeometryDataValueItem)) {
                tmpGeometryDataValueItem.setError(ModelMessage.get("CompartmentContainer.valueItem.error.outOfBox"));
                tmpGeometryDisplayValueItem.setError(ModelMessage.get("CompartmentContainer.valueItem.error.outOfBox"));
            } else {
                tmpGeometryDataValueItem.removeError();
                tmpGeometryDisplayValueItem.removeError();
            }
            // </editor-fold>
        }
        // </editor-fold>
    }

    /**
     * Checks if xy-layer compartment is near top of simulation box
     *
     * @param aXyLayerGeometryDataValueItem xy-layer compartment geometry data
     * value item
     * @return True: Xy-layer compartment is near top of simulation box, false:
     * Otherwise
     */
    public boolean isXyLayerCompartmentNearTop(ValueItem aXyLayerGeometryDataValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyLayerGeometryDataValueItem == null || !this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(aXyLayerGeometryDataValueItem)) {
            return false;
        }

        // </editor-fold>
        return this.compartmentBox.isNearTopOfBox(aXyLayerGeometryDataValueItem);
    }

    /**
     * Checks if xy-layer compartment is near bottom of simulation box
     *
     * @param aXyLayerGeometryDataValueItem xy-layer compartment geometry data
     * value item
     * @return True: Xy-layer compartment is near bottom of simulation box,
     * false: Otherwise
     */
    public boolean isXyLayerCompartmentNearBottom(ValueItem aXyLayerGeometryDataValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyLayerGeometryDataValueItem == null || !this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(aXyLayerGeometryDataValueItem)) {
            return false;
        }

        // </editor-fold>
        return this.compartmentBox.isNearBottomOfBox(aXyLayerGeometryDataValueItem);
    }

    /**
     * Move xy-layer compartment to top of simulation box
     *
     * @param aXyLayerGeometryDataValueItem xy-layer compartment geometry data
     * value item
     */
    public void moveXyLayerCompartmentToTop(ValueItem aXyLayerGeometryDataValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyLayerGeometryDataValueItem == null || !this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(aXyLayerGeometryDataValueItem)) {
            return;
        }

        // </editor-fold>
        this.compartmentBox.moveToTopOfBox(aXyLayerGeometryDataValueItem);
    }

    /**
     * Move xy-layer compartment to bottom of simulation box
     *
     * @param aXyLayerGeometryDataValueItem xy-layer compartment geometry data
     * value item
     */
    public void moveXyLayerCompartmentToBottom(ValueItem aXyLayerGeometryDataValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyLayerGeometryDataValueItem == null || !this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(aXyLayerGeometryDataValueItem)) {
            return;
        }

        // </editor-fold>
        this.compartmentBox.moveToBottomOfBox(aXyLayerGeometryDataValueItem);
    }

    /**
     * True: Compartment container has protein data, false: Otherwise
     *
     * @return True: Compartment container has protein data, false: Otherwise
     */
    public boolean hasProteinData() {
        ValueItem tmpBulkInfoValueItem = this.getBulkInfoValueItem();
        if (tmpBulkInfoValueItem == null) {
            return false;
        } else {
            for (int i = 0; i < tmpBulkInfoValueItem.getMatrixRowCount(); i++) {
                // Column 1: Molecular structure with possible protein data
                if (tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    return true;
                }
            }
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Returns a XML element representation of this ValueItem instance
     *
     * @return A XML element representation of this ValueItem instance
     * @throws Exception Thrown if error occurs
     */
    public Element getAsXmlElement() throws Exception {
        try {
            Element tmpRoot = new Element(CompartmentContainerXmlName.COMPARTMENT_CONTAINER);
            // IMPORTANT: Set version of this XML definition
            tmpRoot.addContent(new Element(CompartmentContainerXmlName.VERSION).addContent("Version 1.0.0"));
            // Add value item container
            tmpRoot.addContent(this.valueItemContainer.getAsXmlElement());
            return tmpRoot;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new Exception("XML element could not be created.", anException);
        }
    }

    /**
     * Returns a XML string representation of this ValueItem instance
     *
     * @return A XML string representation of this ValueItem instance
     * @throws Exception Thrown if error occurs
     */
    public String getAsXmlString() throws Exception {
        try {
            Element tmpRoot = this.getAsXmlElement();
            XMLOutputter tmpOutputter = new XMLOutputter(Format.getPrettyFormat());
            Document tmpDocument = new Document();
            tmpDocument.setRootElement(tmpRoot);
            return tmpOutputter.outputString(tmpDocument);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new Exception("XML string could not be created.", anException);
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Simulation box
     *
     * @return Simulation box
     */
    public CompartmentBox getCompartmentBox() {
        return this.compartmentBox;
    }

    /**
     * Returns value item container
     *
     * @return Value item container
     */
    public ValueItemContainer getValueItemContainer() {
        return this.valueItemContainer;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * Compartment name
     * 
     * @return Compartment name
     */
    public String getCompartmentName() {
        return this.compartmentName;
    }
    
    /**
     * Compartment name
     * 
     * @param aCompartmentName Compartment name (may be null/empty)
     */
    public void setCompartmentName(String aCompartmentName) {
        if (aCompartmentName == null) {
            this.compartmentName = "";
        } else {
            this.compartmentName = aCompartmentName;
        }
    }
    
    /**
     * Removes compartment name
     */
    public void removeCompartmentName() {
        this.compartmentName = "";
    }
    
    /**
     * Returns geometry random seed value
     * 
     * @return Geometry random seed value
     */
    public int getGeometryRandomSeed() {
        return this.valueItemContainer.getValueItem(ModelDefinitions.COMPARTMENT_GEOMETRY_RANDOM_SEED_NAME).getValueAsInt();
    }
    
    /**
     * Set geometry random seed value
     * 
     * @param aSeedValue Random seed value 
     */
    public void setGeometryRandomSeed(long aSeedValue) {
        this.valueItemContainer.getValueItem(ModelDefinitions.COMPARTMENT_GEOMETRY_RANDOM_SEED_NAME).setValue(String.valueOf(aSeedValue));
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Initialize method">
    /**
     * Initializes class variables
     */
    private void initialize() {
        this.changeInformation = new ChangeInformation(ChangeTypeEnum.COMPARTMENT_CONTAINER_CHANGE, null);
        this.errorChangeInformation = new ChangeInformation(ChangeTypeEnum.COMPARTMENT_CONTAINER_ERROR_CHANGE, null);
        this.compartmentBox = null;
        this.valueItemContainer = null;
        this.lengthConversionFactor = -1.0;
        this.compartmentName = "";
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Bulk related methods">
    /**
     * Returns compartment bulk value item
     *
     * @param aMoleculeInfoValueItem Value item with molecule info
     * @return Compartment bulk value item or null if none could be created
     */
    private ValueItem getBulkInfoValueItemFromMoleculeInfo(ValueItem aMoleculeInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeInfoValueItem == null) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set matrix">
        // Number of matrix rows are equal to those of aMoleculeInfoValueItem
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[aMoleculeInfoValueItem.getMatrixRowCount()][];
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            // <editor-fold defaultstate="collapsed" desc="Set row with 5 columns">
            ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[5];
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 0: Molecule name">
            // Column is equal to column 0 of aMoleculeInfoValueItem
            tmpRow[0] = new ValueItemMatrixElement(aMoleculeInfoValueItem.getValue(i, 0), aMoleculeInfoValueItem.getTypeFormat(i, 0));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 1: Molecular structure and possible protein data">
            // Column is equal to column 1 of aMoleculeInfoValueItem
            ValueItemMatrixElement tmpMolecularStructureValueItemMatrixElement = new ValueItemMatrixElement(aMoleculeInfoValueItem.getValue(i, 1), aMoleculeInfoValueItem.getTypeFormat(i, 1));
            if (aMoleculeInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                tmpMolecularStructureValueItemMatrixElement.setProteinData(aMoleculeInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData());
            }
            tmpRow[1] = tmpMolecularStructureValueItemMatrixElement;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 2: Percentage">
            tmpRow[2] = new ValueItemMatrixElement("100.00", new ValueItemDataTypeFormat(ModelDefinitions.NUMBER_OF_DECIMALS_FOR_COMPOSITIONS, false, false));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 3: Quantity">
            // Column is equal to column 2 of aMoleculeInfoValueItem
            tmpRow[3] = new ValueItemMatrixElement(aMoleculeInfoValueItem.getValue(i, 2), aMoleculeInfoValueItem.getTypeFormat(i, 2));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 4: Orientation">
            if (tmpMolecularStructureValueItemMatrixElement.hasProteinData()) {
                tmpRow[4] = new ValueItemMatrixElement(
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"), 
                        new String[]{
                            ModelMessage.get("CompartmentContainer.parameter.Orientation.random"),
                            ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"),
                            ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom")
                        }
                    )
                );
            } else {
                tmpRow[4] = new ValueItemMatrixElement(
                    new ValueItemDataTypeFormat(
                        ModelMessage.get("CompartmentContainer.parameter.Orientation.random"), 
                        new String[]{
                            ModelMessage.get("CompartmentContainer.parameter.Orientation.random")
                        }
                    )
                );
            }
            // </editor-fold>
            tmpMatrix[i] = tmpRow;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set bulk value item">
        ValueItem tmpBulkInfoValueItem = new ValueItem();
        tmpBulkInfoValueItem.setName(ModelDefinitions.COMPARTMENT_BULK_NAME);
        tmpBulkInfoValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpBulkInfoValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("CompartmentContainer.parameter.moleculeName"),
                ModelMessage.get("CompartmentContainer.parameter.molecularStructure"), 
                ModelMessage.get("CompartmentContainer.parameter.percentage"),
                ModelMessage.get("CompartmentContainer.parameter.quantity"),
                ModelMessage.get("CompartmentContainer.parameter.Orientation")
            }
        );
        tmpBulkInfoValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_100,   // Molecule_name
                ModelDefinitions.CELL_WIDTH_TEXT_150,   // Molecular_structure
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Percentage
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Quantity
                ModelDefinitions.CELL_WIDTH_TEXT_150    // Orientation
            }
        );
        tmpBulkInfoValueItem.setMatrix(tmpMatrix);
        // </editor-fold>
        return tmpBulkInfoValueItem;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Chemical composition value item related methods">
    /**
     * Returns chemical composition value item for compartment
     *
     * @return Chemical composition value item for compartment or null if value
     * item can not be created
     */
    private ValueItem getCompartmentChemicalCompositionValueItem() {
        // <editor-fold defaultstate="collapsed" desc="Get bulk info value item">
        ValueItem tmpBulkInfoValueItem = this.getBulkInfoValueItem();
        if (tmpBulkInfoValueItem == null) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set matrix">
        // Number of matrix rows are equal to those of tmpMoleculeInfoValueItem
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpBulkInfoValueItem.getMatrixRowCount()][];
        for (int i = 0; i < tmpBulkInfoValueItem.getMatrixRowCount(); i++) {
            // <editor-fold defaultstate="collapsed" desc="Set row with 8 columns">
            ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[8];
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 0: Molecule name">
            // Column is equal to column 0 of tmpBulkValueItem
            tmpRow[0] = new ValueItemMatrixElement(tmpBulkInfoValueItem.getValue(i, 0), tmpBulkInfoValueItem.getTypeFormat(i, 0));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 1: Molecular structure and possible protein data">
            // Column is equal to column 1 of tmpBulkValueItem
            ValueItemMatrixElement tmpMolecularStructureValueItemMatrixElement = new ValueItemMatrixElement(tmpBulkInfoValueItem.getValue(i, 1), tmpBulkInfoValueItem.getTypeFormat(i, 1));
            if (tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                tmpMolecularStructureValueItemMatrixElement.setProteinData(tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData());
            }
            tmpRow[1] = tmpMolecularStructureValueItemMatrixElement;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 2: Percentage">
            tmpRow[2] = new ValueItemMatrixElement("0.00", new ValueItemDataTypeFormat("0.00", ModelDefinitions.NUMBER_OF_DECIMALS_FOR_COMPOSITIONS, 0, tmpBulkInfoValueItem.getValueAsDouble(i, 2)));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 3: Quantity">
            tmpRow[3] = new ValueItemMatrixElement("0", new ValueItemDataTypeFormat(0, false, false));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 4: Percentage on surface">
            tmpRow[4] = new ValueItemMatrixElement("0.00", new ValueItemDataTypeFormat("0.00", ModelDefinitions.NUMBER_OF_DECIMALS_FOR_COMPOSITIONS, 0, 100));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 5: Quantity in volume">
            tmpRow[5] = new ValueItemMatrixElement("0", new ValueItemDataTypeFormat(0, false, false));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 6: Quantity on surface">
            tmpRow[6] = new ValueItemMatrixElement("0", new ValueItemDataTypeFormat(0, false, false));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Column 7: Orientation">
            tmpRow[7] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(ModelMessage.get("CompartmentContainer.parameter.Orientation.none"), new String[]{
                ModelMessage.get("CompartmentContainer.parameter.Orientation.none")}));
            // </editor-fold>
            tmpMatrix[i] = tmpRow;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set value item">
        ValueItem tmpChemicalCompositionValueItem = new ValueItem();
        tmpChemicalCompositionValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpChemicalCompositionValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("CompartmentContainer.parameter.moleculeName"),
                ModelMessage.get("CompartmentContainer.parameter.molecularStructure"), 
                ModelMessage.get("CompartmentContainer.parameter.percentage"),
                ModelMessage.get("CompartmentContainer.parameter.quantity"), 
                ModelMessage.get("CompartmentContainer.parameter.percentageOnSurface"),
                ModelMessage.get("CompartmentContainer.parameter.quantityInVolume"), 
                ModelMessage.get("CompartmentContainer.parameter.quantityOnSurface"),
                ModelMessage.get("CompartmentContainer.parameter.Orientation")
            }
        );
        tmpChemicalCompositionValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_100,   // Molecule_name
                ModelDefinitions.CELL_WIDTH_TEXT_150,   // Molecular_structure
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Percentage
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Quantity
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Percentage_on_surface
                ModelDefinitions.CELL_WIDTH_TEXT_150,   // Quantity_in_volume
                ModelDefinitions.CELL_WIDTH_TEXT_150,   // Quantity_on_surface
                ModelDefinitions.CELL_WIDTH_TEXT_150    // Orientation
            }
        );
        // Set matrix
        tmpChemicalCompositionValueItem.setMatrix(tmpMatrix);
        // IMPORTANT: Clone matrix before changeInformation
        tmpChemicalCompositionValueItem.setMatrixClonedBeforeChange(true);
        // IMPORTANT: Set update notifier
        tmpChemicalCompositionValueItem.setUpdateNotifier(true);
        // </editor-fold>
        return tmpChemicalCompositionValueItem;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Compartment related methods">
    /**
     * Adds a spherical compartment
     * 
     * @param aCompartmentName Compartment name
     * @return New block name of compartment or null if compartment can not be added
     */
    private String addCompartmentSphere(String aCompartmentName) {
        // <editor-fold defaultstate="collapsed" desc="Get box info value item">
        ValueItem tmpBoxInfoValueItem = this.getBoxInfoValueItem();
        if (tmpBoxInfoValueItem == null) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine new block name and index">
        // NOTE: Slow quadratic implementation
        String tmpNewBlockName = null;
        LinkedList<String> tmpBlockNameList = this.valueItemContainer.getBlockNames();
        int tmpNewBlockNameIndex = 0;
        boolean tmpIsNewName = false;
        while (!tmpIsNewName) {
            tmpIsNewName = true;
            tmpNewBlockName = 
                ModelDefinitions.COMPARTMENT_BLOCK_SPHERE
                + this.stringUtilityMethods.createSortablePositiveIntegerRepresentation(++tmpNewBlockNameIndex, ModelDefinitions.MAXIMUM_NUMBER_OF_COMPARTMENTS);
            for (String tmpSingleBlockName : tmpBlockNameList) {
                if (tmpSingleBlockName.equals(tmpNewBlockName)) {
                    tmpIsNewName = false;
                    break;
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set sphere name">
        String tmpSphereName = null;
        if (aCompartmentName.isEmpty()) {
            tmpSphereName = String.format(ModelMessage.get("CompartmentContainer.section.Sphere"), tmpNewBlockNameIndex);
        } else {
            tmpSphereName = String.format(ModelMessage.get("CompartmentContainer.section.NamedSphere"), aCompartmentName, tmpNewBlockNameIndex);
        }
        String[] tmpNodeNames = 
            new String[]
            {
                ModelMessage.get("CompartmentContainer.section.SimulationBox"), 
                ModelMessage.get("CompartmentContainer.section.SphereCompartments"),
                tmpSphereName
            };
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set chemical composition value item (vertical position 0)">
        ValueItem tmpChemicalCompositionValueItem = this.getCompartmentChemicalCompositionValueItem();
        tmpChemicalCompositionValueItem.setVerticalPosition(0);
        tmpChemicalCompositionValueItem.setName(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_SPHERE + String.valueOf(tmpNewBlockNameIndex));
        tmpChemicalCompositionValueItem.setNodeNames(tmpNodeNames);
        tmpChemicalCompositionValueItem.setDisplayName(ModelMessage.get("CompartmentContainer.valueItem.displayName.SPHERE_CHEMICAL_COMPOSITION"));
        tmpChemicalCompositionValueItem.setBlockName(tmpNewBlockName);
        tmpChemicalCompositionValueItem.setDescription(ModelMessage.get("CompartmentContainer.valueItem.description.SPHERE_CHEMICAL_COMPOSITION"));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set geometry data and display value items (vertical position 1 and 2)">
        // <editor-fold defaultstate="collapsed" desc="- Set geometry data value item (vertical position 1)">
        // <editor-fold defaultstate="collapsed" desc="-- Set matrix">
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][4];
        // NOTE: Geometry data value item contains DPD units
        // <editor-fold defaultstate="collapsed" desc="-- Column 0: X-Position">
        tmpMatrix[0][0] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0)
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 1: Y-Position">
        tmpMatrix[0][1] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1)
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 2: Z-Position">
        tmpMatrix[0][2] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 2) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.000",
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 2)
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 3: Radius">
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][3] = new ValueItemMatrixElement("0.00", new ValueItemDataTypeFormat(2, false, false));
        // ---------
        tmpMatrix[0][3] = 
            new ValueItemMatrixElement(
                "0.0", 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0,
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Set value item">
        ValueItem tmpGeometryDataValueItem = new ValueItem();
        tmpGeometryDataValueItem.setVerticalPosition(1);
        tmpGeometryDataValueItem.setName(ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_DATA_PREFIX_NAME + String.valueOf(tmpNewBlockNameIndex));
        tmpGeometryDataValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        // IMPORTANT: Geometry data value item is NOT to be displayed
        tmpGeometryDataValueItem.setDisplay(false);
        // IMPORTANT: Set node names (is used for graphics display)
        tmpGeometryDataValueItem.setNodeNames(tmpNodeNames);
        tmpGeometryDataValueItem.setBlockName(tmpNewBlockName);
        tmpGeometryDataValueItem.setMatrix(tmpMatrix);
        // IMPORTANT: Set update notifier
        tmpGeometryDataValueItem.setUpdateNotifier(true);
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Set geometry display value item (vertical position 2)">
        // <editor-fold defaultstate="collapsed" desc="-- Set matrix">
        tmpMatrix = new ValueItemMatrixElement[1][8];
        // NOTE: Geometry display value item contains physical units (Angstrom)
        // <editor-fold defaultstate="collapsed" desc="-- Column 0 (editable): X-Position (in Angstrom)">
        tmpMatrix[0][0] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) / 2.0 * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0) * this.getLengthConversionFactor()
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 1 (editable): Y-Position (in Angstrom)">
        tmpMatrix[0][1] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) / 2.0 * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1) * this.getLengthConversionFactor()
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 2 (editable): Z-Position (in Angstrom)">
        tmpMatrix[0][2] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 2) / 2.0 * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 2) * this.getLengthConversionFactor()
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 3 (editable): Radius (in Angstrom)">
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][3] = new ValueItemMatrixElement("0.00", new ValueItemDataTypeFormat(2, false, false));
        // ---------
        tmpMatrix[0][3] = 
            new ValueItemMatrixElement(
                "0.0", 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0,
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 4 (non-editable): X-Position (in DPD units)">
        tmpMatrix[0][4] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0),
                    false
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 5 (non-editable): Y-Position (in DPD units)">
        tmpMatrix[0][5] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1),
                    false
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 6 (non-editable): Z-Position (in DPD units)">
        tmpMatrix[0][6] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 2) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.000",
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 2),
                    false
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 7 (non-editable): Radius (in DPD units)">
        tmpMatrix[0][7] = 
            new ValueItemMatrixElement(
                "0.0", 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0,
                    Double.MAX_VALUE,
                    false, // isEditable
                    false  // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Set value item">
        ValueItem tmpGeometryDisplayValueItem = new ValueItem();
        tmpGeometryDisplayValueItem.setVerticalPosition(2);
        tmpGeometryDisplayValueItem.setName(ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_DISPLAY_PREFIX_NAME + String.valueOf(tmpNewBlockNameIndex));
        tmpGeometryDisplayValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpGeometryDisplayValueItem.setMatrixColumnNames(new String[] {
                ModelMessage.get("CompartmentContainer.parameter.xPositionInAngstrom"),
                ModelMessage.get("CompartmentContainer.parameter.yPositionInAngstrom"), 
                ModelMessage.get("CompartmentContainer.parameter.zPositionInAngstrom"),
                ModelMessage.get("CompartmentContainer.parameter.radiusInBoxInAngstrom"),
                ModelMessage.get("CompartmentContainer.parameter.xPositionInDPD"),
                ModelMessage.get("CompartmentContainer.parameter.yPositionInDPD"), 
                ModelMessage.get("CompartmentContainer.parameter.zPositionInDPD"),
                ModelMessage.get("CompartmentContainer.parameter.radiusInBoxInDPD")
            }
        );
        tmpGeometryDisplayValueItem.setMatrixColumnWidths(new String[] {
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // radius in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100  // radius in DPD units
            }
        );
        tmpGeometryDisplayValueItem.setNodeNames(tmpNodeNames);
        tmpGeometryDisplayValueItem.setDisplayName(ModelMessage.get("CompartmentContainer.valueItem.displayName.SPHERE_GEOMETRY"));
        tmpGeometryDisplayValueItem.setBlockName(tmpNewBlockName);
        tmpGeometryDisplayValueItem.setDescription(ModelMessage.get("CompartmentContainer.valueItem.description.SPHERE_GEOMETRY"));
        tmpGeometryDisplayValueItem.setMatrix(tmpMatrix);
        // IMPORTANT: Set update notifier
        tmpGeometryDisplayValueItem.setUpdateNotifier(true);
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Set corresponding display and data value item names">
        tmpGeometryDataValueItem.setNameOfDisplayValueItem(tmpGeometryDisplayValueItem.getName());
        tmpGeometryDisplayValueItem.setNameOfDataValueItem(tmpGeometryDataValueItem.getName());
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set compartment specified name value item (vertical position 3)">
        ValueItem tmpCompartmentSpecifiedNameValueItem = new ValueItem();
        tmpCompartmentSpecifiedNameValueItem.setVerticalPosition(3);
        tmpCompartmentSpecifiedNameValueItem.setName(ModelDefinitions.COMPARTMENT_SPHERE_SPECIFIED_NAME + String.valueOf(tmpNewBlockNameIndex));
        // IMPORTANT: Value item is NOT to be displayed
        tmpCompartmentSpecifiedNameValueItem.setDisplay(false);
        // IMPORTANT: Set node names (is used for graphics display)
        tmpCompartmentSpecifiedNameValueItem.setNodeNames(tmpNodeNames);
        tmpCompartmentSpecifiedNameValueItem.setBlockName(tmpNewBlockName);
        tmpCompartmentSpecifiedNameValueItem.setValue(aCompartmentName);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add to value item container and sort">
        // Parameter false: Notify changeInformation receivers if necessary
        this.valueItemContainer.addValueItemsAndSortBlocks(
            new ValueItem[] { 
                tmpChemicalCompositionValueItem, 
                tmpGeometryDataValueItem, 
                tmpGeometryDisplayValueItem,
                tmpCompartmentSpecifiedNameValueItem
            }, 
            false
        );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add corresponding body to simulation box">
        this.compartmentBox.addBody(new BodySphere(tmpGeometryDataValueItem));
        // </editor-fold>
        return tmpNewBlockName;
    }

    /**
     * Adds a xy-layer compartment
     * 
     * @param aCompartmentName Compartment name
     * @return New block name of compartment or null if compartment can not be added
     */
    private String addCompartmentXyLayer(String aCompartmentName) {
        // <editor-fold defaultstate="collapsed" desc="Get box info value item">
        ValueItem tmpBoxInfoValueItem = this.getBoxInfoValueItem();
        if (tmpBoxInfoValueItem == null) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine new block name and index">
        // NOTE: Slow quadratic implementation
        String tmpNewBlockName = null;
        LinkedList<String> tmpBlockNameList = this.valueItemContainer.getBlockNames();
        int tmpNewBlockNameIndex = 0;
        boolean tmpIsNewName = false;
        while (!tmpIsNewName) {
            tmpIsNewName = true;
            tmpNewBlockName = 
                ModelDefinitions.COMPARTMENT_BLOCK_XY_LAYER
                + this.stringUtilityMethods.createSortablePositiveIntegerRepresentation(++tmpNewBlockNameIndex, ModelDefinitions.MAXIMUM_NUMBER_OF_COMPARTMENTS);
            for (String tmpSingleBlockName : tmpBlockNameList) {
                if (tmpSingleBlockName.equals(tmpNewBlockName)) {
                    tmpIsNewName = false;
                    break;
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set xy-layer name">
        String tmpXyLayerName = null;
        if (aCompartmentName.isEmpty()) {
            tmpXyLayerName = String.format(ModelMessage.get("CompartmentContainer.section.XyLayer"), tmpNewBlockNameIndex);
        } else {
            tmpXyLayerName = String.format(ModelMessage.get("CompartmentContainer.section.NamedXyLayer"), aCompartmentName, tmpNewBlockNameIndex);
        }
        String[] tmpNodeNames = 
            new String[]
            {
                ModelMessage.get("CompartmentContainer.section.SimulationBox"), 
                ModelMessage.get("CompartmentContainer.section.XyLayerCompartments"),
                tmpXyLayerName
            };
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set chemical composition value item (vertical position 0)">
        ValueItem tmpChemicalCompositionValueItem = this.getCompartmentChemicalCompositionValueItem();
        tmpChemicalCompositionValueItem.setVerticalPosition(0);
        tmpChemicalCompositionValueItem.setName(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME_COMPARTMENT_XY_LAYER + String.valueOf(tmpNewBlockNameIndex));
        tmpChemicalCompositionValueItem.setNodeNames(tmpNodeNames);
        tmpChemicalCompositionValueItem.setDisplayName(ModelMessage.get("CompartmentContainer.valueItem.displayName.XY_LAYER_CHEMICAL_COMPOSITION"));
        tmpChemicalCompositionValueItem.setBlockName(tmpNewBlockName);
        tmpChemicalCompositionValueItem.setDescription(ModelMessage.get("CompartmentContainer.valueItem.description.XY_LAYER_CHEMICAL_COMPOSITION"));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set geometry data and display value items (vertical position 1 and 2)">
        // <editor-fold defaultstate="collapsed" desc="- Set geometry data value item (vertical position 1)">
        // <editor-fold defaultstate="collapsed" desc="-- Set matrix">
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][6];
        // NOTE: Geometry data value item contains DPD units
        // <editor-fold defaultstate="collapsed" desc="-- Column 0: X-Position">
        tmpMatrix[0][0] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0", 
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0),
                    true
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 1: Y-Position">
        tmpMatrix[0][1] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0", 
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1),
                    true
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 2: Z-Position">
        tmpMatrix[0][2] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 2) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0", 
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 2),
                    true
                )
            );

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 3: X-Length">
        // X-length is equal to x-length of simulation box
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][3] = new ValueItemMatrixElement(tmpBoxInfoValueItem.getValue(0, 0), new ValueItemDataTypeFormat(2, false, false));
        // ---------
        tmpMatrix[0][3] = 
            new ValueItemMatrixElement(
                tmpBoxInfoValueItem.getValue(0, 0), 
                new ValueItemDataTypeFormat(
                    tmpBoxInfoValueItem.getValue(0, 0),
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(),
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0) * ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR,
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 4: Y-Length">
        // Y-length is equal to y-length of simulation box
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][4] = new ValueItemMatrixElement(tmpBoxInfoValueItem.getValue(0, 1), new ValueItemDataTypeFormat(2, false, false));
        // ---------
        tmpMatrix[0][4] = 
            new ValueItemMatrixElement(
                tmpBoxInfoValueItem.getValue(0, 1), 
                new ValueItemDataTypeFormat(
                    tmpBoxInfoValueItem.getValue(0, 1),
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(),
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1) * ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR,
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 5: Z-Length">
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][5] = new ValueItemMatrixElement("0.00", new ValueItemDataTypeFormat(2, false, false));
        // ---------
        tmpMatrix[0][5] = 
            new ValueItemMatrixElement(
                "0.0", 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(),
                    0.0,
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Set value item">
        ValueItem tmpGeometryDataValueItem = new ValueItem();
        tmpGeometryDataValueItem.setVerticalPosition(1);
        tmpGeometryDataValueItem.setName(ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_DATA_PREFIX_NAME + String.valueOf(tmpNewBlockNameIndex));
        tmpGeometryDataValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        // IMPORTANT: Geometry data value item is NOT to be displayed
        tmpGeometryDataValueItem.setDisplay(false);
        // IMPORTANT: Set node names (is used for graphics display)
        tmpGeometryDataValueItem.setNodeNames(tmpNodeNames);
        tmpGeometryDataValueItem.setBlockName(tmpNewBlockName);
        tmpGeometryDataValueItem.setMatrix(tmpMatrix);
        // IMPORTANT: Set update notifier
        tmpGeometryDataValueItem.setUpdateNotifier(true);
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Set geometry display value item (vertical position 2)">
        // <editor-fold defaultstate="collapsed" desc="-- Set matrix">
        tmpMatrix = new ValueItemMatrixElement[1][12];
        // NOTE: Geometry display value item contains physical units (Angstrom)
        // <editor-fold defaultstate="collapsed" desc="-- Column 0 (editable): X-Position (in Angstrom)">
        tmpMatrix[0][0] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) / 2.0 * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0) * this.getLengthConversionFactor()
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 1 (editable): Y-Position (in Angstrom)">
        tmpMatrix[0][1] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) / 2.0 * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1) * this.getLengthConversionFactor()
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 2 (editable): Z-Position (in Angstrom)">
        tmpMatrix[0][2] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 2) / 2.0 * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 2) * this.getLengthConversionFactor()
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 3 (editable): X-Length (in Angstrom)">
        // X-length is equal to x-length of simulation box
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][3] = new ValueItemMatrixElement(String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) * this.getLengthConversionFactor()), new ValueItemDataTypeFormat(2, false, false));
        // ---------
        // NOTE: Use value from tmpGeometryDataValueItem to avoid roundoff errors
        tmpMatrix[0][3] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, 3) * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) * this.getLengthConversionFactor()),
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    tmpGeometryDataValueItem.getMatrix()[0][3].getTypeFormat().getMinimumValue() * this.getLengthConversionFactor(),
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 4 (editable): Y-Length (in Angstrom)">
        // Y-length is equal to y-length of simulation box
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][4] = new ValueItemMatrixElement(String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) * this.getLengthConversionFactor()), new ValueItemDataTypeFormat(2, false, false));
        // ---------
        // NOTE: Use value from tmpGeometryDataValueItem to avoid roundoff errors
        tmpMatrix[0][4] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpGeometryDataValueItem.getValueAsDouble(0, 4) * this.getLengthConversionFactor()), 
                new ValueItemDataTypeFormat(
                    String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) * this.getLengthConversionFactor()),
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(), 
                    tmpGeometryDataValueItem.getMatrix()[0][4].getTypeFormat().getMinimumValue() * this.getLengthConversionFactor(),
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 5 (editable): Z-Length (in Angstrom)">
        // ---------
        // Old code:
        // NOTE: Not editable and not NUMERIC_NULL
        // tmpMatrix[0][5] = new ValueItemMatrixElement("0.00", new ValueItemDataTypeFormat(2, false, false));
        // ---------
        tmpMatrix[0][5] = 
            new ValueItemMatrixElement(
                "0.0", 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(), 
                    0,
                    Double.MAX_VALUE,
                    true, // isEditable
                    false // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 6 (non-editable): X-Position (in DPD units)">
        tmpMatrix[0][6] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 0) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0", 
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0),
                    false
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 7 (non-editable): Y-Position (in DPD units)">
        tmpMatrix[0][7] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 1) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0", 
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1),
                    false
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 8 (non-editable): Z-Position (in DPD units)">
        tmpMatrix[0][8] = 
            new ValueItemMatrixElement(
                String.valueOf(tmpBoxInfoValueItem.getValueAsDouble(0, 2) / 2.0), 
                new ValueItemDataTypeFormat(
                    "0.0", 
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(), 
                    0, 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 2),
                    false
                )
            );

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 9 (non-editable): X-Length (in DPD units)">
        // X-length is equal to x-length of simulation box
        tmpMatrix[0][9] = 
            new ValueItemMatrixElement(
                tmpBoxInfoValueItem.getValue(0, 0), 
                new ValueItemDataTypeFormat(
                    tmpBoxInfoValueItem.getValue(0, 0),
                    tmpBoxInfoValueItem.getTypeFormat(0, 0).getNumberOfDecimals(),
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0) * ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR,
                    Double.MAX_VALUE,
                    false, // isEditable
                    false  // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 10 (non-editable): Y-Length (in DPD units)">
        // Y-length is equal to y-length of simulation box
        tmpMatrix[0][10] = 
            new ValueItemMatrixElement(
                tmpBoxInfoValueItem.getValue(0, 1), 
                new ValueItemDataTypeFormat(
                    tmpBoxInfoValueItem.getValue(0, 1),
                    tmpBoxInfoValueItem.getTypeFormat(0, 1).getNumberOfDecimals(),
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1) * ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR,
                    Double.MAX_VALUE,
                    false, // isEditable
                    false  // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Column 11 (non-editable): Z-Length (in DPD units)">
        tmpMatrix[0][11] = 
            new ValueItemMatrixElement(
                "0.0", 
                new ValueItemDataTypeFormat(
                    "0.0",
                    tmpBoxInfoValueItem.getTypeFormat(0, 2).getNumberOfDecimals(),
                    0.0,
                    Double.MAX_VALUE,
                    false, // isEditable
                    false  // isNumericNullAllowed
                )
            );
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Set value item">
        ValueItem tmpGeometryDisplayValueItem = new ValueItem();
        tmpGeometryDisplayValueItem.setVerticalPosition(2);
        tmpGeometryDisplayValueItem.setName(ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_DISPLAY_PREFIX_NAME + String.valueOf(tmpNewBlockNameIndex));
        tmpGeometryDisplayValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpGeometryDisplayValueItem.setMatrixColumnNames(new String[] {
                ModelMessage.get("CompartmentContainer.parameter.xPositionInAngstrom"),
                ModelMessage.get("CompartmentContainer.parameter.yPositionInAngstrom"), 
                ModelMessage.get("CompartmentContainer.parameter.zPositionInAngstrom"),
                ModelMessage.get("CompartmentContainer.parameter.xLengthInAngstrom"), 
                ModelMessage.get("CompartmentContainer.parameter.yLengthInAngstrom"),
                ModelMessage.get("CompartmentContainer.parameter.zLengthInAngstrom"),
                ModelMessage.get("CompartmentContainer.parameter.xPositionInDPD"),
                ModelMessage.get("CompartmentContainer.parameter.yPositionInDPD"), 
                ModelMessage.get("CompartmentContainer.parameter.zPositionInDPD"),
                ModelMessage.get("CompartmentContainer.parameter.xLengthInDPD"), 
                ModelMessage.get("CompartmentContainer.parameter.yLengthInDPD"),
                ModelMessage.get("CompartmentContainer.parameter.zLengthInDPD")
            }
        );
        tmpGeometryDisplayValueItem.setMatrixColumnWidths(new String[] { 
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Length in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Length in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Length in Angstrom
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Length in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Length in DPD units
                ModelDefinitions.CELL_WIDTH_NUMERIC_100  // z-Length in DPD units
            }
        );
        tmpGeometryDisplayValueItem.setNodeNames(tmpNodeNames);
        tmpGeometryDisplayValueItem.setDisplayName(ModelMessage.get("CompartmentContainer.valueItem.displayName.XY_LAYER_GEOMETRY"));
        tmpGeometryDisplayValueItem.setBlockName(tmpNewBlockName);
        tmpGeometryDisplayValueItem.setDescription(ModelMessage.get("CompartmentContainer.valueItem.description.XY_LAYER_GEOMETRY"));
        tmpGeometryDisplayValueItem.setMatrix(tmpMatrix);
        // IMPORTANT: Set update notifier
        tmpGeometryDisplayValueItem.setUpdateNotifier(true);
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Set corresponding display and data value item names">
        tmpGeometryDataValueItem.setNameOfDisplayValueItem(tmpGeometryDisplayValueItem.getName());
        tmpGeometryDisplayValueItem.setNameOfDataValueItem(tmpGeometryDataValueItem.getName());
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set compartment specified name value item (vertical position 3)">
        ValueItem tmpCompartmentSpecifiedNameValueItem = new ValueItem();
        tmpCompartmentSpecifiedNameValueItem.setVerticalPosition(3);
        tmpCompartmentSpecifiedNameValueItem.setName(ModelDefinitions.COMPARTMENT_XY_LAYER_SPECIFIED_NAME + String.valueOf(tmpNewBlockNameIndex));
        // IMPORTANT: Value item is NOT to be displayed
        tmpCompartmentSpecifiedNameValueItem.setDisplay(false);
        // IMPORTANT: Set node names (is used for graphics display)
        tmpCompartmentSpecifiedNameValueItem.setNodeNames(tmpNodeNames);
        tmpCompartmentSpecifiedNameValueItem.setBlockName(tmpNewBlockName);
        tmpCompartmentSpecifiedNameValueItem.setValue(aCompartmentName);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add to value item container and sort">
        // Parameter false: Notify changeInformation receivers if necessary
        this.valueItemContainer.addValueItemsAndSortBlocks(
            new ValueItem[] { 
                tmpChemicalCompositionValueItem, 
                tmpGeometryDataValueItem, 
                tmpGeometryDisplayValueItem,
                tmpCompartmentSpecifiedNameValueItem
            }, 
            false
        );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add corresponding body to simulation box">
        this.compartmentBox.addBody(new BodyXyLayer(tmpGeometryDataValueItem));
        // </editor-fold>
        return tmpNewBlockName;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Sets compartment box with information from this.valueItemContainer
     */
    private void setCompartmentBox() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.valueItemContainer == null) {
            return;
        }

        // </editor-fold>
        ValueItem tmpBoxInfoValueItem = this.valueItemContainer.getValueItem(ModelDefinitions.BOX_INFO_NAME);
        if (tmpBoxInfoValueItem != null) {
            // boxInfoValueItem.getValueAsDouble(0, 0) : X-length of box
            // boxInfoValueItem.getValueAsDouble(0, 1) : Y-length of box
            // boxInfoValueItem.getValueAsDouble(0, 2) : Z-length of box
            this.compartmentBox = 
                new CompartmentBox(
                    tmpBoxInfoValueItem.getValueAsDouble(0, 0), 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 1), 
                    tmpBoxInfoValueItem.getValueAsDouble(0, 2)
                );
            // Set geometry information
            LinkedList<ValueItem> tmpGeometryDataValueItemList = this.valueItemContainer.getValueItems(new String[]{ModelDefinitions.GEOMETRY_PREFIX_NAME});
            if (tmpGeometryDataValueItemList != null) {
                for (ValueItem tmpGeometryDataValueItem : tmpGeometryDataValueItemList) {
                    // <editor-fold defaultstate="collapsed" desc="Spheres">
                    if (this.graphicsUtilityMethods.isSphereGeometryDataValueItem(tmpGeometryDataValueItem)) {
                        this.compartmentBox.addBody(new BodySphere(tmpGeometryDataValueItem));
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="xy-layer">
                    if (this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(tmpGeometryDataValueItem)) {
                        this.compartmentBox.addBody(new BodyXyLayer(tmpGeometryDataValueItem));
                    }
                    // </editor-fold>
                }
            }
        }
    }
    
    /**
     * Return geometry random seed value item with vertical position 0
     * 
     * @param aSeedValue Seed value
     * @return Geometry random seed value item
     */
    private ValueItem getGeometryRandomSeedValueItem(long aSeedValue) {
        ValueItem tmpGeometryRandomSeedValueItem = new ValueItem();
        tmpGeometryRandomSeedValueItem.setName(ModelDefinitions.COMPARTMENT_GEOMETRY_RANDOM_SEED_NAME);
        // IMPORTANT: tmpGeometryRandomSeedValueItem is NOT displayed
        tmpGeometryRandomSeedValueItem.setDisplay(false);
        tmpGeometryRandomSeedValueItem.setVerticalPosition(0);
        tmpGeometryRandomSeedValueItem.setBlockName(ModelDefinitions.COMPARTMENT_BLOCK_HIDDEN);
        tmpGeometryRandomSeedValueItem.setValue(String.valueOf(aSeedValue));
        return tmpGeometryRandomSeedValueItem;
    }
    
    /**
     * Updates geometry display value items with data from corresponding 
     * geometry data value items if necessary
     */
    private void updateGeometryDisplayValueItems() {
        ValueItem[] tmpValueItems = this.valueItemContainer.getValueItemsOfContainer();
        if (tmpValueItems != null && tmpValueItems.length > 0) {
            for (ValueItem tmpValueItem : tmpValueItems) {
                if (this.graphicsUtilityMethods.isSphereGeometryDisplayValueItem(tmpValueItem)) {
                    if (tmpValueItem.getMatrixColumnCount() == 4) {
                        ValueItem tmpDataValueItem = tmpValueItem.getDataValueItem();
                        tmpValueItem.setMatrixColumnNames(new String[] {
                                ModelMessage.get("CompartmentContainer.parameter.xPositionInAngstrom"),
                                ModelMessage.get("CompartmentContainer.parameter.yPositionInAngstrom"), 
                                ModelMessage.get("CompartmentContainer.parameter.zPositionInAngstrom"),
                                ModelMessage.get("CompartmentContainer.parameter.radiusInBoxInAngstrom"),
                                ModelMessage.get("CompartmentContainer.parameter.xPositionInDPD"),
                                ModelMessage.get("CompartmentContainer.parameter.yPositionInDPD"), 
                                ModelMessage.get("CompartmentContainer.parameter.zPositionInDPD"),
                                ModelMessage.get("CompartmentContainer.parameter.radiusInBoxInDPD")
                            }
                        );
                        tmpValueItem.setMatrixColumnWidths(new String[] {
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // radius in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100  // radius in DPD units
                            }
                        );
                        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][8];
                        tmpMatrix[0][0] = tmpValueItem.getMatrix()[0][0];
                        tmpMatrix[0][1] = tmpValueItem.getMatrix()[0][1];
                        tmpMatrix[0][2] = tmpValueItem.getMatrix()[0][2];
                        tmpMatrix[0][3] = tmpValueItem.getMatrix()[0][3];
                        tmpMatrix[0][4] = tmpDataValueItem.getMatrix()[0][0].getClone();
                        tmpMatrix[0][4].getTypeFormat().setEditable(false);
                        tmpMatrix[0][5] = tmpDataValueItem.getMatrix()[0][1].getClone();
                        tmpMatrix[0][5].getTypeFormat().setEditable(false);
                        tmpMatrix[0][6] = tmpDataValueItem.getMatrix()[0][2].getClone();
                        tmpMatrix[0][6].getTypeFormat().setEditable(false);
                        tmpMatrix[0][7] = tmpDataValueItem.getMatrix()[0][3].getClone();
                        tmpMatrix[0][7].getTypeFormat().setEditable(false);
                        tmpValueItem.setMatrix(tmpMatrix);
                    }
                } else if (this.graphicsUtilityMethods.isXyLayerGeometryDisplayValueItem(tmpValueItem)) {
                    if (tmpValueItem.getMatrixColumnCount() == 6) {
                        ValueItem tmpDataValueItem = tmpValueItem.getDataValueItem();
                        tmpValueItem.setMatrixColumnNames(new String[] {
                                ModelMessage.get("CompartmentContainer.parameter.xPositionInAngstrom"),
                                ModelMessage.get("CompartmentContainer.parameter.yPositionInAngstrom"), 
                                ModelMessage.get("CompartmentContainer.parameter.zPositionInAngstrom"),
                                ModelMessage.get("CompartmentContainer.parameter.xLengthInAngstrom"), 
                                ModelMessage.get("CompartmentContainer.parameter.yLengthInAngstrom"),
                                ModelMessage.get("CompartmentContainer.parameter.zLengthInAngstrom"),
                                ModelMessage.get("CompartmentContainer.parameter.xPositionInDPD"),
                                ModelMessage.get("CompartmentContainer.parameter.yPositionInDPD"), 
                                ModelMessage.get("CompartmentContainer.parameter.zPositionInDPD"),
                                ModelMessage.get("CompartmentContainer.parameter.xLengthInDPD"), 
                                ModelMessage.get("CompartmentContainer.parameter.yLengthInDPD"),
                                ModelMessage.get("CompartmentContainer.parameter.zLengthInDPD")
                            }
                        );
                        tmpValueItem.setMatrixColumnWidths(new String[] { 
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Length in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Length in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Length in Angstrom
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Position in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Position in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // z-Position in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // x-Length in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100, // y-Length in DPD units
                                ModelDefinitions.CELL_WIDTH_NUMERIC_100  // z-Length in DPD units
                            }
                        );
                        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][12];
                        tmpMatrix[0][0] = tmpValueItem.getMatrix()[0][0];
                        tmpMatrix[0][1] = tmpValueItem.getMatrix()[0][1];
                        tmpMatrix[0][2] = tmpValueItem.getMatrix()[0][2];
                        tmpMatrix[0][3] = tmpValueItem.getMatrix()[0][3];
                        tmpMatrix[0][4] = tmpValueItem.getMatrix()[0][4];
                        tmpMatrix[0][5] = tmpValueItem.getMatrix()[0][5];
                        tmpMatrix[0][6] = tmpDataValueItem.getMatrix()[0][0].getClone();
                        tmpMatrix[0][6].getTypeFormat().setEditable(false);
                        tmpMatrix[0][7] = tmpDataValueItem.getMatrix()[0][1].getClone();
                        tmpMatrix[0][7].getTypeFormat().setEditable(false);
                        tmpMatrix[0][8] = tmpDataValueItem.getMatrix()[0][2].getClone();
                        tmpMatrix[0][8].getTypeFormat().setEditable(false);
                        tmpMatrix[0][9] = tmpDataValueItem.getMatrix()[0][3].getClone();
                        tmpMatrix[0][9].getTypeFormat().setEditable(false);
                        tmpMatrix[0][10] = tmpDataValueItem.getMatrix()[0][4].getClone();
                        tmpMatrix[0][10].getTypeFormat().setEditable(false);
                        tmpMatrix[0][11] = tmpDataValueItem.getMatrix()[0][5].getClone();
                        tmpMatrix[0][11].getTypeFormat().setEditable(false);
                        tmpValueItem.setMatrix(tmpMatrix);
                    }
                }
            }
        }
    }
    
    /**
     * Corrects composition related value items due to strange error with 
     * differently sorted rows
     * 
     * @throws Exception Thrown if error occurs
     */
    private void correctCompositionRelatedValueItems() throws Exception {
        try {
            ValueItem tmpMoleculeInfoValueItem = this.valueItemContainer.getValueItem(ModelDefinitions.MOLECULE_INFO_NAME);
            ValueItem tmpBulkInfoValueItem = this.valueItemContainer.getValueItem(ModelDefinitions.COMPARTMENT_BULK_NAME);
            LinkedList<ValueItem> tmpChemicalCompositionValueItemList = this.valueItemContainer.getSortedValueItemsOfContainer(ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME);
            String[] tmpMoleculeNameArray = new String[tmpMoleculeInfoValueItem.getMatrixRowCount()];
            for (int i = 0; i < tmpMoleculeInfoValueItem.getMatrixRowCount(); i++) {
                // Column 0 - Molecule name
                tmpMoleculeNameArray[i] = tmpMoleculeInfoValueItem.getValue(i, 0);
            }
            // Check tmpBulkInfoValueItem
            for (int i = 0; i < tmpMoleculeNameArray.length; i++) {
                // Column 0 - Molecule name
                if (!tmpMoleculeNameArray[i].equals(tmpBulkInfoValueItem.getValue(i, 0))) {
                    this.correctValueItemRowOrderWithMoleculeNames(tmpBulkInfoValueItem, tmpMoleculeNameArray);
                    break;
                }
            }
            // Check tmpChemicalCompositionValueItemList
            for (ValueItem tmpChemicalCompositionValueItem : tmpChemicalCompositionValueItemList) {
                for (int i = 0; i < tmpMoleculeNameArray.length; i++) {
                    // Column 0 - Molecule name
                    if (!tmpMoleculeNameArray[i].equals(tmpChemicalCompositionValueItem.getValue(i, 0))) {
                        this.correctValueItemRowOrderWithMoleculeNames(tmpChemicalCompositionValueItem, tmpMoleculeNameArray);
                        break;
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new Exception("Composition related value items could not be corrected (this should never happen).", anException);
        }
    }
    
    /**
     * Auxiliary method that corrects value item row order according to molecule names
     * 
     * @param aValueItem Value item
     * @param aMoleculeNameArray Array of molecule names
     */
    private void correctValueItemRowOrderWithMoleculeNames(ValueItem aValueItem, String[] aMoleculeNameArray) {
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[aMoleculeNameArray.length][];
        for (int i = 0; i < aMoleculeNameArray.length; i++) {
            for (int k = 0; k < aValueItem.getMatrixRowCount(); k++) {
                // Column 0 - Molecule name
                if (aValueItem.getValue(k, 0).equals(aMoleculeNameArray[i])) {
                    tmpMatrix[i] = aValueItem.getMatrix()[k];
                    break;
                }
            }
        }
        aValueItem.setMatrix(tmpMatrix);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Reads XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformation(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }
        if (!anElement.getName().equals(CompartmentContainerXmlName.COMPARTMENT_CONTAINER)) {
            return false;
        }
        if (anElement.getChild(CompartmentContainerXmlName.VERSION) == null) {
            return false;
        }
        // </editor-fold>
        String tmpVersion = anElement.getChild(CompartmentContainerXmlName.VERSION).getText();
        if (tmpVersion == null || tmpVersion.isEmpty()) {
            return false;
        }
        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
        if (tmpVersion.equals("Version 1.0.0")) {
            return this.readXmlInformationV_1_0_0(anElement);
        }
        // </editor-fold>
        return false;
    }

    /**
     * Reads versioned XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformationV_1_0_0(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }

        // </editor-fold>
        try {
            Element tmpValueItemContainerElement = anElement.getChild(ValueItemContainerXmlName.VALUE_ITEM_CONTAINER);
            if (tmpValueItemContainerElement == null) {
                return false;
            } else {
                this.valueItemContainer = new ValueItemContainer(tmpValueItemContainerElement, this);
                // Add geometry random seed value item for compatibility if not present
                if (!this.valueItemContainer.hasValueItem(ModelDefinitions.COMPARTMENT_GEOMETRY_RANDOM_SEED_NAME)) {
                    this.valueItemContainer.addValueItem(
                        this.getGeometryRandomSeedValueItem(ModelDefinitions.DETERMINISTIC_RANDOM_SEED_DEFAULT)
                    );
                }
                // Update geometry display value items if necessary
                this.updateGeometryDisplayValueItems();
                // Corrects composition related value items due to possible strange error with differently sorted rows
                this.correctCompositionRelatedValueItems();
                return true;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private properties (set)">
    /**
     * Value item container. (No checks are performed)
     *
     * @param aValueItemContainer ValueItemContainer
     */
    private void setValueItemContainer(ValueItemContainer aValueItemContainer) {
        this.valueItemContainer = aValueItemContainer;
        // IMPORTANT: Set this instance as UpdateNotificationObject
        this.valueItemContainer.setUpdateNotificationObject(this);
        // <editor-fold defaultstate="collapsed" desc="Set compartment box">
        this.setCompartmentBox();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add this instance as changeInformation receiver">
        this.valueItemContainer.addChangeReceiver(this);
        // </editor-fold>
    }
    // </editor-fold>

}
