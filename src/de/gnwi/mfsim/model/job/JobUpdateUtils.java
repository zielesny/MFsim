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
package de.gnwi.mfsim.model.job;

import de.gnwi.jdpd.utilities.Constants;
import de.gnwi.jdpd.utilities.Factory;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemUtils;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.valueItem.ValueItemUpdateNotifierInterface;
import de.gnwi.mfsim.model.particleStructure.Molecule;
import de.gnwi.mfsim.model.particleStructure.ParticleInfoContainer;
import de.gnwi.mfsim.model.particleStructure.MoleculeConcentrationType;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.mfsim.model.particleStructure.MoleculeContainer;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.spices.SpicesConstants;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.particle.StandardParticleDescription;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.peptide.PdbToDpd;
import de.gnwi.mfsim.model.peptide.PdbToDpdPool;
import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import de.gnwi.mfsim.model.preference.Preferences;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Methods for update of value items of JobInput instances
 *
 * @author Achim Zielesny
 */
public class JobUpdateUtils implements ValueItemUpdateNotifierInterface {

    // <editor-fold defaultstate="collapsed" desc="Private static final class variables">
    /**
     * Comma separator
     */
    private static final String COMMA_SEPARATOR = ", ";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Miscellaneous utility methods
     */
    private final MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * Job utility methods
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public JobUpdateUtils() {
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="notifyDependentValueItemsForUpdate() method">
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
        // <editor-fold defaultstate="collapsed" desc="Update notifier ParticleTable">
        if (anUpdateNotifierValueItem.getName().equals("ParticleTable")) {
            // <editor-fold defaultstate="collapsed" desc="Set often used value items">
            ValueItem tmpParticleTableValueItem = anUpdateNotifierValueItem;
            ValueItem tmpBonds12TableValueItem = tmpParticleTableValueItem.getValueItemContainer().getValueItem("Bonds12Table");
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver ParticlePairRdfCalculation">
            this.updateParticlePairRdfCalculation(tmpParticleTableValueItem, tmpParticleTableValueItem.getValueItemContainer().getValueItem("ParticlePairRdfCalculation"));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver ParticlePairDistanceCalculation">
            this.updateParticlePairDistanceCalculation(tmpParticleTableValueItem, tmpParticleTableValueItem.getValueItemContainer().getValueItem("ParticlePairDistanceCalculation"));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver InteractionTable">
            // NOTE: Parameter "false" initiates check if update is necessary
            this.updateInteractionTable(tmpParticleTableValueItem, tmpParticleTableValueItem.getValueItemContainer().getValueItem("InteractionTable"), tmpParticleTableValueItem.getValueItemContainer().getValueItem("Temperature"), tmpParticleTableValueItem.getValueItemContainer().getValueItem("Density"), false);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver Bonds12Table">
            this.updateParticlePairsInBonds12(tmpParticleTableValueItem, tmpBonds12TableValueItem);
            // </editor-fold>
            // IMPORTANT: Do NOT call this.updatePhysicalTimePeriods() or this.updateParticleNumber()!
            //            This could lead to an internal error since Quantity and other settings may not already be performed!
            //            this.updatePhysicalTimePeriods() and this.updateParticleNumber() are correctly called by other value items.
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier MonomerTable">
        if (anUpdateNotifierValueItem.getName().equals("MonomerTable")) {
            // <editor-fold defaultstate="collapsed" desc="Set often used value items">
            ValueItem tmpMonomerTableValueItem = anUpdateNotifierValueItem;
            ValueItem tmpParticleTableValueItem = tmpMonomerTableValueItem.getValueItemContainer().getValueItem("ParticleTable");
            ValueItem tmpMoleculeTableValueItem = tmpMonomerTableValueItem.getValueItemContainer().getValueItem("MoleculeTable");
            ValueItem tmpBonds12TableValueItem = tmpMonomerTableValueItem.getValueItemContainer().getValueItem("Bonds12Table");
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="FIRST update ParticleTable value item">
            tmpParticleTableValueItem.initializeChangeDetection();
            this.updateParticleTableValueItemWithMonomerAndMoleculeParticles(tmpParticleTableValueItem, tmpMonomerTableValueItem, tmpMoleculeTableValueItem);
            if (tmpParticleTableValueItem.hasChangeDetected()) {
                tmpParticleTableValueItem.notifyDependentValueItemsForUpdate();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiverMoleculeTable">
            tmpMoleculeTableValueItem.initializeChangeDetection();
            this.checkMonomerShortcutsOfMolecules(tmpMonomerTableValueItem, tmpMoleculeTableValueItem);
            if (tmpMoleculeTableValueItem.hasChangeDetected()) {
                tmpMoleculeTableValueItem.notifyDependentValueItemsForUpdate();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver Bonds12Table">
            this.updateVolumeBasedBondLengthsInBonds12(tmpBonds12TableValueItem);
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier MoleculeTable">
        if (anUpdateNotifierValueItem.getName().equals("MoleculeTable")) {
            // <editor-fold defaultstate="collapsed" desc="Setup often used value items">
            ValueItem tmpMoleculeTableValueItem = anUpdateNotifierValueItem;
            ValueItem tmpParticleTableValueItem = tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("ParticleTable");
            ValueItem tmpConcentrationValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Concentration");
            ValueItem tmpMonomerTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MonomerTable");
            ValueItem tmpQuantityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Quantity");
            ValueItem tmpDensityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Density");
            ValueItem tmpBoxSizeValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("BoxSize");
            ValueItem tmpBonds12TableValueItem = tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("Bonds12Table");
            ValueItem tmpMoleculeParticlePairRdfCalculationValueItem = tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("MoleculeParticlePairRdfCalculation");
            ValueItem tmpMoleculeParticlePairDistanceCalculationValueItem = tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("MoleculeParticlePairDistanceCalculation");
            ValueItem tmpProteinDistanceForcesValueItem = tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("ProteinDistanceForces");
            ValueItem tmpBoundaryValueItem = tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("MoleculeBoundary");
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="FIRST update ParticleTable value item">
            tmpParticleTableValueItem.initializeChangeDetection();
            this.updateParticleTableValueItemWithMonomerAndMoleculeParticles(tmpParticleTableValueItem, tmpMonomerTableValueItem, tmpMoleculeTableValueItem);
            if (tmpParticleTableValueItem.hasChangeDetected()) {
                tmpParticleTableValueItem.notifyDependentValueItemsForUpdate();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check itself">
            if (!tmpMoleculeTableValueItem.hasError()) {
                // NOTE: Do ONLY call checkMonomerShortcutsOfMolecules() if tmpMoleculeTableValueItem has NO error: Otherwise errors may overwrite!
                this.checkMonomerShortcutsOfMolecules(tmpMonomerTableValueItem, tmpMoleculeTableValueItem);
                this.checkMoleculeColorsForHint(tmpMoleculeTableValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="1. Update molecule names etc.">
            // <editor-fold defaultstate="collapsed" desc="- Update receiver Concentration">
            this.updateMoleculeNameFixed(tmpMoleculeTableValueItem, tmpConcentrationValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update receiver Quantity">
            this.updateMoleculeNameFixed(tmpMoleculeTableValueItem, tmpQuantityValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update receiver MoleculeFixation">
            this.updateMoleculeNameFixed(tmpMoleculeTableValueItem, tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("MoleculeFixation"));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update receiver MoleculeBoundary">
            this.updateBoundary(tmpBoundaryValueItem, tmpMoleculeTableValueItem, tmpBoxSizeValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update receiver MoleculeFixedVelocity">
            this.updateMoleculeNameFixed(tmpMoleculeTableValueItem, tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("MoleculeFixedVelocity"));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update receiver MoleculeAcceleration">
            this.updateMoleculeNameFixed(tmpMoleculeTableValueItem, tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("MoleculeAcceleration"));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update receiver RadiusOfGyration">
            this.updateMoleculeNameFixed(tmpMoleculeTableValueItem, tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("RadiusOfGyration"));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Update receiver NearestNeighborParticle">
            this.updateNearestNeighborParticleValueItem(tmpMonomerTableValueItem, tmpMoleculeTableValueItem, tmpMoleculeTableValueItem.getValueItemContainer().getValueItem("NearestNeighborParticle"));
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="2. Notify other value items">
            // NOTE: Value items Concentration, Quantity must be notified.
            if (tmpConcentrationValueItem.isActive()) {
                tmpConcentrationValueItem.notifyDependentValueItemsForUpdate();
            }
            tmpQuantityValueItem.notifyDependentValueItemsForUpdate();
            // Update box size (NOTE: Although Quantity might be unchanged there may be a change in the number of particles in the simulation box due to a new molecular structure!)
            tmpBoxSizeValueItem.initializeChangeDetection();
            this.updateBoxSize(tmpBoxSizeValueItem, tmpDensityValueItem, tmpMonomerTableValueItem, tmpMoleculeTableValueItem, tmpQuantityValueItem, tmpParticleTableValueItem);
            if (tmpBoxSizeValueItem.hasChangeDetected()) {
                tmpBoxSizeValueItem.notifyDependentValueItemsForUpdate();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="3. Set molecule display settings value items">
            // NOTE: Molecule display settings MUST be AFTER update of MonomerTable, MoleculeTable, Density, Quantity and ParticleTable value item
            this.updateMoleculeDisplaySettings(tmpMoleculeTableValueItem.getValueItemContainer());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="4. Update MoleculeParticlePairRdfCalculation">
            // NOTE: MUST be AFTER update of MonomerTable, MoleculeTable and ParticleTable value item
            this.updateMoleculeParticlePairRdfCalculation(tmpMonomerTableValueItem, tmpMoleculeTableValueItem, tmpMoleculeParticlePairRdfCalculationValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="5. Update MoleculeParticlePairDistanceCalculation">
            // NOTE: MUST be AFTER update of MonomerTable, MoleculeTable and ParticleTable value item
            this.updateMoleculeParticlePairDistanceCalculation(tmpMonomerTableValueItem, tmpMoleculeTableValueItem, tmpMoleculeParticlePairDistanceCalculationValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="6. Update ProteinDistanceForces">
            boolean tmpHasProteinData = false;
            for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
                // Column 1: Molecular structure with possible protein data
                if (tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    tmpHasProteinData = true;
                    break;
                }
            }
            if (tmpHasProteinData) {
                // Protein definition exists
                this.updateProteinDistanceForces(tmpMoleculeTableValueItem, tmpProteinDistanceForcesValueItem);
            } else {
                // No protein definition: Lock aProteinDistanceForcesValueItem
                tmpProteinDistanceForcesValueItem.setLocked(true);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="7. Update receiver MoleculeBackboneForces">
            // IMPORTANT: Call of this.updateMoleculeBackboneForces() MUST be a late call since already updated other value items are necessary for calculation
            this.updateMoleculeBackboneForces(tmpMoleculeTableValueItem.getValueItemContainer());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="8. Update receiver ProteinBackboneForces">
            // IMPORTANT: Call of this.updateMoleculeBackboneForces() MUST be a late call since already updated other value items are necessary for calculation
            this.updateProteinBackboneForces(tmpMoleculeTableValueItem.getValueItemContainer());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="9. Update receiver TimeStepNumber and TimeStepLength">
            // IMPORTANT: Call of this.updatePhysicalTimePeriods() MUST be a late call since already updated other value items are necessary for calculation
            this.updatePhysicalTimePeriods(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="10. Update receiver ParticleNumber">
            // IMPORTANT: Call of this.updateParticleNumber() MUST be a late call since already updated other value items are necessary for calculation
            this.updateParticleNumber(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="11. Update Bonds12Table">
            this.updateVolumeBasedBondLengthsInBonds12(tmpBonds12TableValueItem);
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier Density">
        if (anUpdateNotifierValueItem.getName().equals("Density")) {
            // <editor-fold defaultstate="collapsed" desc="Set often used value items">
            ValueItem tmpParticleTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("ParticleTable");
            ValueItem tmpQuantityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Quantity");
            ValueItem tmpMonomerTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MonomerTable");
            ValueItem tmpMoleculeTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MoleculeTable");
            ValueItem tmpBoxSizeValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("BoxSize");
            ValueItem tmpCompartmentsValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Compartments");
            ValueItem tmpTemperatureValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Temperature");
            ValueItem tmpInteractionTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("InteractionTable");
            ValueItem tmpMoleculeBackboneForcesValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MoleculeBackboneForces");
            ValueItem tmpProteinBackboneForcesValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("ProteinBackboneForces");
            ValueItem tmpTimeStepNumberValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("TimeStepNumber");
            ValueItem tmpTimeStepLengthValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("TimeStepLength");
            ValueItem tmpBonds12TableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Bonds12Table");
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver Compartments">
            this.updateCompartments(tmpCompartmentsValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver BoxSize">
            // IMPORTANT: BoxSize MUST be updated FIRST
            tmpBoxSizeValueItem.initializeChangeDetection();
            this.updateBoxSize(tmpBoxSizeValueItem, anUpdateNotifierValueItem, tmpMonomerTableValueItem, tmpMoleculeTableValueItem, tmpQuantityValueItem, tmpParticleTableValueItem);
            if (tmpBoxSizeValueItem.hasChangeDetected()) {
                tmpBoxSizeValueItem.notifyDependentValueItemsForUpdate();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver InteractionTable">
            this.updateInteractionTableWithNewDensityAndTemperature(anUpdateNotifierValueItem, tmpTemperatureValueItem, tmpParticleTableValueItem, tmpInteractionTableValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver Bonds12Table">
            this.updateVolumeBasedBondLengthsInBonds12(tmpBonds12TableValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver MoleculeBackboneForces, ProteinBackboneForces, TimeStepNumber, TimeStepLength and ParticleNumber">
            // IMPORTANT: Call of the following methods MUST be the LAST call since already updated other value items are necessary for calculation
            double[] tmpResultArray = this.jobUtilityMethods.getLengthAndTimeConversionFactorsFromDpdToPhysicalUnits(anUpdateNotifierValueItem.getValueItemContainer());
            double tmpLengthConversionFactor = tmpResultArray[0];
            double tmpTimeConversionFactor = tmpResultArray[1];
            this.updateMoleculeBackboneForcesDpdLength(tmpMoleculeBackboneForcesValueItem, tmpLengthConversionFactor);
            this.updateProteinBackboneForcesDpdLength(tmpProteinBackboneForcesValueItem, tmpLengthConversionFactor);
            this.updatePhysicalTimePeriods(tmpTimeStepNumberValueItem, tmpTimeStepLengthValueItem, tmpTimeConversionFactor);
            this.updateParticleNumber(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier Concentration">
        if (anUpdateNotifierValueItem.getName().equals("Concentration")) {
            // <editor-fold defaultstate="collapsed" desc="Check itself">
            this.checkConcentration(anUpdateNotifierValueItem);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver Quantity">
            ValueItem tmpQuantityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Quantity");
            tmpQuantityValueItem.initializeChangeDetection();
            this.calculateQuantity(anUpdateNotifierValueItem, tmpQuantityValueItem, anUpdateNotifierValueItem.getValueItemContainer().getValueItem("ParticleTable"),
                    anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MonomerTable"), anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MoleculeTable"),
                    anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Density"), anUpdateNotifierValueItem.getValueItemContainer().getValueItem("BoxSize"));
            if (tmpQuantityValueItem.hasChangeDetected()) {
                tmpQuantityValueItem.notifyDependentValueItemsForUpdate();
            }
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier Quantity">
        if (anUpdateNotifierValueItem.getName().equals("Quantity")) {
            // <editor-fold defaultstate="collapsed" desc="Set often used value items">
            ValueItem tmpParticleTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("ParticleTable");
            ValueItem tmpConcentrationValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Concentration");
            ValueItem tmpMonomerTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MonomerTable");
            ValueItem tmpMoleculeTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MoleculeTable");
            ValueItem tmpBoxSizeValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("BoxSize");
            ValueItem tmpDensityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Density");
            ValueItem tmpCompartmentsValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Compartments");
            ValueItem tmpMoleculeBackboneForcesValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MoleculeBackboneForces");
            ValueItem tmpProteinBackboneForcesValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("ProteinBackboneForces");
            ValueItem tmpTimeStepNumberValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("TimeStepNumber");
            ValueItem tmpTimeStepLengthValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("TimeStepLength");
            ValueItem tmpBonds12TableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Bonds12Table");
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver Compartments">
            this.updateCompartments(tmpCompartmentsValueItem);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update itself and BoxSize">
            // IMPORTANT: FIRST update itself and BoxSize
            tmpBoxSizeValueItem.initializeChangeDetection();
            this.updateQuantityItself(tmpConcentrationValueItem, anUpdateNotifierValueItem, tmpParticleTableValueItem, tmpMonomerTableValueItem, tmpMoleculeTableValueItem, tmpDensityValueItem,
                    tmpBoxSizeValueItem);
            if (tmpBoxSizeValueItem.hasChangeDetected()) {
                tmpBoxSizeValueItem.notifyDependentValueItemsForUpdate();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver Bonds12Table">
            this.updateVolumeBasedBondLengthsInBonds12(tmpBonds12TableValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver MoleculeBackboneForces, ProteinBackboneForces, TimeStepNumber, TimeStepLength and ParticleNumber">
            // IMPORTANT: Call of the following methods MUST be the LAST call since already updated other value items are necessary for calculation
            double[] tmpResultArray = this.jobUtilityMethods.getLengthAndTimeConversionFactorsFromDpdToPhysicalUnits(anUpdateNotifierValueItem.getValueItemContainer());
            double tmpLengthConversionFactor = tmpResultArray[0];
            double tmpTimeConversionFactor = tmpResultArray[1];
            this.updateMoleculeBackboneForcesDpdLength(tmpMoleculeBackboneForcesValueItem, tmpLengthConversionFactor);
            this.updateProteinBackboneForcesDpdLength(tmpProteinBackboneForcesValueItem, tmpLengthConversionFactor);
            this.updatePhysicalTimePeriods(tmpTimeStepNumberValueItem, tmpTimeStepLengthValueItem, tmpTimeConversionFactor);
            this.updateParticleNumber(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier BoxSize">
        if (anUpdateNotifierValueItem.getName().equals("BoxSize")) {
            // <editor-fold defaultstate="collapsed" desc="Set often used value items">
            ValueItem tmpQuantityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Quantity");
            ValueItem tmpCompartmentsValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Compartments");
            ValueItem tmpParticleTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("ParticleTable");
            ValueItem tmpMonomerTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MonomerTable");
            ValueItem tmpMoleculeTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MoleculeTable");
            ValueItem tmpConcentrationValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Concentration");
            ValueItem tmpDensityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Density");
            ValueItem tmpBoundaryValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MoleculeBoundary");
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update itself">
            // IMPORTANT: Update BoxSize FIRST!
            double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(
                    tmpParticleTableValueItem,
                    tmpMonomerTableValueItem,
                    tmpMoleculeTableValueItem,
                    tmpQuantityValueItem,
                    tmpDensityValueItem);
            this.updateBoxSizeItself(anUpdateNotifierValueItem, tmpLengthConversionFactor);
            // </editor-fold>
            if (tmpConcentrationValueItem.isActive()) {
                // <editor-fold defaultstate="collapsed" desc="Recalculate Quantity">
                // IMPORTANT: NO further updates at this point since only quantites are recalculated. This may be necessary due to roundoff errors in box size recalcualtion!
                this.calculateQuantity(
                        tmpConcentrationValueItem,
                        tmpQuantityValueItem,
                        tmpParticleTableValueItem,
                        tmpMonomerTableValueItem,
                        tmpMoleculeTableValueItem,
                        tmpDensityValueItem,
                        anUpdateNotifierValueItem
                );
                // </editor-fold>
            }
            // IMPORTANT: Update Compartments AFTER QUANTITIY
            // <editor-fold defaultstate="collapsed" desc="Update receiver Compartments">
            this.updateCompartments(tmpCompartmentsValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver MoleculeBoundary">
            this.updateBoundary(tmpBoundaryValueItem, tmpMoleculeTableValueItem, anUpdateNotifierValueItem);
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier MoleculeBoundary">
        if (anUpdateNotifierValueItem.getName().equals("MoleculeBoundary")) {
            this.updateBoundaryItself(anUpdateNotifierValueItem);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier Temperature">
        if (anUpdateNotifierValueItem.getName().equals("Temperature")) {
            // <editor-fold defaultstate="collapsed" desc="Set often used value items">
            ValueItem tmpParticleTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("ParticleTable");
            ValueItem tmpDensityValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("Density");
            ValueItem tmpInteractionTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("InteractionTable");

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver InteractionTable">
            this.updateInteractionTableWithNewDensityAndTemperature(tmpDensityValueItem, anUpdateNotifierValueItem, tmpParticleTableValueItem, tmpInteractionTableValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver TimeStepNumber and TimeStepLength">
            // IMPORTANT: Call of this.updatePhysicalTimePeriods() MUST be the LAST call since already updated other value items are necessary for calculation
            this.updatePhysicalTimePeriods(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier MoleculeBackboneForces">
        if (anUpdateNotifierValueItem.getName().equals("MoleculeBackboneForces")) {
            // <editor-fold defaultstate="collapsed" desc="Update receiver MoleculeBackboneForces">
            // IMPORTANT: Call of this.updateMoleculeBackboneForcesItself() MUST be a late call since already updated other value items are necessary for calculation
            this.updateMoleculeBackboneForcesItself(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier ProteinBackboneForces">
        if (anUpdateNotifierValueItem.getName().equals("ProteinBackboneForces")) {
            // <editor-fold defaultstate="collapsed" desc="Update receiver ProteinBackboneForces">
            // IMPORTANT: Call of this.updateProteinBackboneForcesItself() MUST be a late call since already updated other value items are necessary for calculation
            this.updateProteinBackboneForcesItself(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier TimeStepNumber">
        if (anUpdateNotifierValueItem.getName().equals("TimeStepNumber")) {
            // <editor-fold defaultstate="collapsed" desc="Update receiver TimeStepNumber and TimeStepLength">
            // IMPORTANT: Call of this.updatePhysicalTimePeriods() MUST be the LAST call since already updated other value items are necessary for calculation
            this.updatePhysicalTimePeriods(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier TimeStepLength">
        if (anUpdateNotifierValueItem.getName().equals("TimeStepLength")) {
            // <editor-fold defaultstate="collapsed" desc="Update receiver TimeStepNumber and TimeStepLength">
            // IMPORTANT: Call of this.updatePhysicalTimePeriods() MUST be a late call since already updated other value items are necessary for calculation
            this.updatePhysicalTimePeriods(anUpdateNotifierValueItem.getValueItemContainer());
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier IntegrationType">
        if (anUpdateNotifierValueItem.getName().equals("IntegrationType")) {
            this.updateIntegrationTypeItself(anUpdateNotifierValueItem);
            return;
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public utility methods">
    /**
     * Returns incompatible particles of job input with current particle set. If
     * null is returned all particles of job input are compatible with current
     * particle set.
     *
     * @param aJobInput Job input (may be null then null is returned)
     * @return Incompatible particles of job input with current particle set. If
     * null is returned all particles of job input are compatible with current
     * particle set.
     */
    public String getIncompatibleParticlesForCurrentParticleSet(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpParticleTableValueItem = aJobInput.getValueItemContainer().getValueItem("ParticleTable");
        ValueItem tmpTemperatureValueItem = aJobInput.getValueItemContainer().getValueItem("Temperature");
        if (tmpParticleTableValueItem == null || tmpTemperatureValueItem == null) {
            return null;
        }
        return this.getIncompatibleParticles(tmpParticleTableValueItem, tmpTemperatureValueItem);
    }

    /**
     * Corrects particles and particle interactions of job input according to
     * available particle scripts. NOTE: If correction is necessary all updates
     * of dependent value items are performed.
     *
     * @param aJobInput Job input
     * @return Corrected job input (if correction was necessary)
     */
    public JobInput correctParticlesAndInteractions(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return null;
        }
        // </editor-fold>
        ValueItem tmpParticleTableValueItem = aJobInput.getValueItemContainer().getValueItem("ParticleTable");
        if (tmpParticleTableValueItem == null) {
            return aJobInput;
        }
        // Update particle matrix itself ...
        this.updateParticleMatrixItself(tmpParticleTableValueItem);
        // ... and particle interactions a(ij) (NOTE: Parameter "true" enforces update):
        this.updateInteractionTable(
            tmpParticleTableValueItem, 
            tmpParticleTableValueItem.getValueItemContainer().getValueItem("InteractionTable"),
            tmpParticleTableValueItem.getValueItemContainer().getValueItem("Temperature"), 
            tmpParticleTableValueItem.getValueItemContainer().getValueItem("Density"), 
            true
        );
        // Update particle set filename
        aJobInput.getValueItemContainer().getValueItem("ParticleSetFilename").setValue(Preferences.getInstance().getCurrentParticleSetFilename());
        return aJobInput;
    }
    
    /**
     * Updates value item "MoleculeAcceleration" for maximum time step in specified job input
     * 
     * @param aJobInput Job input to be updated
     */
    public void updateMoleculeAccelerationForMaxTimeStep(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return;
        }
        // </editor-fold>
        ValueItem tmpMoleculeAccelerationValueItem = aJobInput.getValueItemContainer().getValueItem("MoleculeAcceleration");
        // Maximum time step is defined at row with index 5 (i.e. the 6th position)
        if (tmpMoleculeAccelerationValueItem == null || tmpMoleculeAccelerationValueItem.getMatrixColumnCount() > 5) {
            return;
        }
        ValueItem tmpUpdatedMoleculeAccelerationValueItem = JdpdValueItemDefinition.getInstance().getClonedJdpdInputFileValueItem("MoleculeAcceleration");
        ValueItemMatrixElement[][] tmpMatrix = tmpMoleculeAccelerationValueItem.getMatrix();
        ValueItemMatrixElement[][] tmpUpdatedMatrix = new ValueItemMatrixElement[tmpMatrix.length][];
        ValueItemDataTypeFormat tmpMaxTimeStepTypeFormat = 
            new ValueItemDataTypeFormat(
                "1.0",
                0,
                1,
                Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
            );
        for (int i = 0; i < tmpMatrix.length; i++) {
            tmpUpdatedMatrix[i] = new ValueItemMatrixElement[tmpMatrix[0].length + 1];
            for (int k = 0; k < tmpMatrix[0].length; k++) {
                tmpUpdatedMatrix[i][k] = tmpMatrix[i][k];
            }
            tmpUpdatedMatrix[i][tmpMatrix[0].length] = new ValueItemMatrixElement(String.valueOf(Constants.MAXIMUM_NUMBER_OF_TIME_STEPS), tmpMaxTimeStepTypeFormat);
        }
        tmpUpdatedMoleculeAccelerationValueItem.setMatrix(tmpUpdatedMatrix);
        aJobInput.getValueItemContainer().replaceValueItemWithKeptVerticalPositionAndNodeNames(tmpUpdatedMoleculeAccelerationValueItem);
    }
    
    /**
     * Updates value item "MoleculeFixation" for maximum time step in specified job input
     * 
     * @param aJobInput Job input to be updated
     */
    public void updateMoleculeFixationForMaxTimeStep(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return;
        }
        // </editor-fold>
        ValueItem tmpMoleculeFixationValueItem = aJobInput.getValueItemContainer().getValueItem("MoleculeFixation");
        // Maximum time step is defined at row with index 4 (i.e. the 5th position)
        if (tmpMoleculeFixationValueItem == null || tmpMoleculeFixationValueItem.getMatrixColumnCount() > 4) {
            return;
        }
        ValueItem tmpUpdatedMoleculeAccelerationValueItem = JdpdValueItemDefinition.getInstance().getClonedJdpdInputFileValueItem("MoleculeFixation");
        ValueItemMatrixElement[][] tmpMatrix = tmpMoleculeFixationValueItem.getMatrix();
        ValueItemMatrixElement[][] tmpUpdatedMatrix = new ValueItemMatrixElement[tmpMatrix.length][];
        ValueItemDataTypeFormat tmpMaxTimeStepTypeFormat = 
            new ValueItemDataTypeFormat(
                "1.0",
                0,
                1,
                Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
            );
        for (int i = 0; i < tmpMatrix.length; i++) {
            tmpUpdatedMatrix[i] = new ValueItemMatrixElement[tmpMatrix[0].length + 1];
            for (int k = 0; k < tmpMatrix[0].length; k++) {
                tmpUpdatedMatrix[i][k] = tmpMatrix[i][k];
            }
            tmpUpdatedMatrix[i][tmpMatrix[0].length] = new ValueItemMatrixElement(String.valueOf(Constants.MAXIMUM_NUMBER_OF_TIME_STEPS), tmpMaxTimeStepTypeFormat);
        }
        tmpUpdatedMoleculeAccelerationValueItem.setMatrix(tmpUpdatedMatrix);
        aJobInput.getValueItemContainer().replaceValueItemWithKeptVerticalPositionAndNodeNames(tmpUpdatedMoleculeAccelerationValueItem);
    }
    
    /**
     * Updates value item "MoleculeBoundary" for maximum time step in specified job input
     * 
     * @param aJobInput Job input to be updated
     */
    public void updateMoleculeBoundaryForMaxTimeStep(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return;
        }
        // </editor-fold>
        ValueItem tmpMoleculeBoundaryValueItem = aJobInput.getValueItemContainer().getValueItem("MoleculeBoundary");
        // Maximum time step is defined at row with index 16 (i.e. the 17th position)
        if (tmpMoleculeBoundaryValueItem == null || tmpMoleculeBoundaryValueItem.getMatrixColumnCount() > 16) {
            return;
        }
        ValueItem tmpUpdatedMoleculeAccelerationValueItem = JdpdValueItemDefinition.getInstance().getClonedJdpdInputFileValueItem("MoleculeBoundary");
        ValueItemMatrixElement[][] tmpMatrix = tmpMoleculeBoundaryValueItem.getMatrix();
        ValueItemMatrixElement[][] tmpUpdatedMatrix = new ValueItemMatrixElement[tmpMatrix.length][];
        ValueItemDataTypeFormat tmpMaxTimeStepTypeFormat = 
            new ValueItemDataTypeFormat(
                "1.0",
                0,
                1,
                Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
            );
        for (int i = 0; i < tmpMatrix.length; i++) {
            tmpUpdatedMatrix[i] = new ValueItemMatrixElement[tmpMatrix[0].length + 1];
            for (int k = 0; k < tmpMatrix[0].length; k++) {
                tmpUpdatedMatrix[i][k] = tmpMatrix[i][k];
            }
            tmpUpdatedMatrix[i][tmpMatrix[0].length] = new ValueItemMatrixElement(String.valueOf(Constants.MAXIMUM_NUMBER_OF_TIME_STEPS), tmpMaxTimeStepTypeFormat);
        }
        tmpUpdatedMoleculeAccelerationValueItem.setMatrix(tmpUpdatedMatrix);
        aJobInput.getValueItemContainer().replaceValueItemWithKeptVerticalPositionAndNodeNames(tmpUpdatedMoleculeAccelerationValueItem);
    }

    /**
     * Updates value item "MoleculeFixedVelocity" for maximum time step in specified job input
     * 
     * @param aJobInput Job input to be updated
     */
    public void updateMoleculeFixedVelocityForMaxTimeStep(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return;
        }
        // </editor-fold>
        ValueItem tmpMoleculeFixedVelocityValueItem = aJobInput.getValueItemContainer().getValueItem("MoleculeFixedVelocity");
        // Maximum time step is defined at row with index 7 (i.e. the 8th position)
        if (tmpMoleculeFixedVelocityValueItem == null || tmpMoleculeFixedVelocityValueItem.getMatrixColumnCount() > 7) {
            return;
        }
        ValueItem tmpUpdatedMoleculeAccelerationValueItem = JdpdValueItemDefinition.getInstance().getClonedJdpdInputFileValueItem("MoleculeFixedVelocity");
        ValueItemMatrixElement[][] tmpMatrix = tmpMoleculeFixedVelocityValueItem.getMatrix();
        ValueItemMatrixElement[][] tmpUpdatedMatrix = new ValueItemMatrixElement[tmpMatrix.length][];
        ValueItemDataTypeFormat tmpMaxTimeStepTypeFormat = 
            new ValueItemDataTypeFormat(
                "1.0",
                0,
                1,
                Constants.MAXIMUM_NUMBER_OF_TIME_STEPS
            );
        for (int i = 0; i < tmpMatrix.length; i++) {
            tmpUpdatedMatrix[i] = new ValueItemMatrixElement[tmpMatrix[0].length + 1];
            for (int k = 0; k < tmpMatrix[0].length; k++) {
                tmpUpdatedMatrix[i][k] = tmpMatrix[i][k];
            }
            tmpUpdatedMatrix[i][tmpMatrix[0].length] = new ValueItemMatrixElement(String.valueOf(Constants.MAXIMUM_NUMBER_OF_TIME_STEPS), tmpMaxTimeStepTypeFormat);
        }
        tmpUpdatedMoleculeAccelerationValueItem.setMatrix(tmpUpdatedMatrix);
        aJobInput.getValueItemContainer().replaceValueItemWithKeptVerticalPositionAndNodeNames(tmpUpdatedMoleculeAccelerationValueItem);
    }

    /**
     * Updates velocity scaling information in specified job input
     * 
     * @param aJobInput Job input to be updated
     */
    public void updateVelocityScalingInformation(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            return;
        }
        // </editor-fold>
        // Update old boolean velocity scaling information
        ValueItem tmpOldVelocityScalingValueItem = aJobInput.getValueItemContainer().getValueItem("IsVelocityScaling");
        if (tmpOldVelocityScalingValueItem == null) {
            return;
        }
        ValueItem tmpInitialVelocityScalingStepsValueItem = JdpdValueItemDefinition.getInstance().getClonedJdpdInputFileValueItem("InitialVelocityScalingSteps");
        if (tmpOldVelocityScalingValueItem.getValue().equals(ModelMessage.get("JdpdInputFile.parameter.true"))) {
            tmpInitialVelocityScalingStepsValueItem.setValue(String.valueOf(Constants.MAXIMUM_NUMBER_OF_TIME_STEPS));
        }
        tmpInitialVelocityScalingStepsValueItem.setVerticalPosition(tmpOldVelocityScalingValueItem.getVerticalPosition());
        tmpInitialVelocityScalingStepsValueItem.setNodeNames(tmpOldVelocityScalingValueItem.getNodeNames());
        aJobInput.getValueItemContainer().removeValueItem(tmpOldVelocityScalingValueItem.getName());
        aJobInput.getValueItemContainer().addValueItem(tmpInitialVelocityScalingStepsValueItem);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    // NOTE: All of these methods do NOT invoke an update notification of any value item.
    // <editor-fold defaultstate="collapsed" desc="-- MoleculeTable as update receiver">
    /**
     * Checks monomer shortcuts of molecules
     *
     * @param aMonomerTableValueItem MonomerTable value item (is NOT changed)
     * @param aMoleculeTableValueItem MoleculeTable value item (may be changed)
     */
    private void checkMonomerShortcutsOfMolecules(ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable") || aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }

        // </editor-fold>
        LinkedList<String> tmpMonomerShortcutList = new LinkedList<String>();
        // Fill tmpMonomerShortcutList only when monomer value item is active
        if (aMonomerTableValueItem.isActive()) {
            for (int i = 0; i < aMonomerTableValueItem.getMatrixRowCount(); i++) {
                // IMPORTANT: Add monomer shortcut identifier
                tmpMonomerShortcutList.addLast("#" + aMonomerTableValueItem.getValue(i, 0));
            }
        }
        LinkedList<String> tmpMissingMonomerShortcutList = new LinkedList<String>();
        for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
            // Old code:
            // this.spices.setInputStructure(aMoleculeTableValueItem.getValue(i, 1));
            // String[] tmpMonomerShortcuts = this.spices.getMonomers();
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMoleculeTableValueItem.getValue(i, 1));
            String[] tmpMonomerShortcuts = tmpSpices.getMonomers();
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            if (tmpMonomerShortcuts != null) {
                for (String tmpSingleMonomerShortcut : tmpMonomerShortcuts) {
                    if (!tmpMonomerShortcutList.contains(tmpSingleMonomerShortcut)) {
                        tmpMissingMonomerShortcutList.addLast(tmpSingleMonomerShortcut);
                    }
                }
            }
        }
        if (tmpMissingMonomerShortcutList.size() == 0) {
            if (aMoleculeTableValueItem.hasError()) {
                aMoleculeTableValueItem.removeError();
            }
        } else {
            StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_TINY);
            for (String tmpSingleMissingMonomerShortcut : tmpMissingMonomerShortcutList) {
                if (tmpBuffer.length() == 0) {
                    tmpBuffer.append(tmpSingleMissingMonomerShortcut);
                } else {
                    tmpBuffer.append(JobUpdateUtils.COMMA_SEPARATOR);
                    tmpBuffer.append(tmpSingleMissingMonomerShortcut);
                }
            }
            aMoleculeTableValueItem.setError(String.format(ModelMessage.get("ValueItem.Error.MissingMonomers"), tmpBuffer.toString()));
        }
    }

    /**
     * Checks molecule colors for hint
     *
     * @param aMoleculeTableValueItem MoleculeTable value item (may be changed)
     */
    private void checkMoleculeColorsForHint(ValueItem aMoleculeTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }
        // </editor-fold>
        if (aMoleculeTableValueItem.getMatrixRowCount() > 1) {
            HashMap<String, String> tmpMoleculeColorMap = new HashMap<String, String>(aMoleculeTableValueItem.getMatrixRowCount());
            for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
                String tmpMoleculeColor = aMoleculeTableValueItem.getValue(i, 2);
                if (tmpMoleculeColorMap.containsKey(tmpMoleculeColor)) {
                    aMoleculeTableValueItem.setHint(ModelMessage.get("ValueItem.Hint.EqualMoleculeColors"));
                    return;
                } else {
                    tmpMoleculeColorMap.put(tmpMoleculeColor, tmpMoleculeColor);
                }
            }
        }
        aMoleculeTableValueItem.removeHint();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- MoleculeTable as update notifier">
    /**
     * Updates molecule names of update receiver value item (see code)
     *
     * @param aMoleculeTableValueItem MoleculeTable value item (is NOT changed)
     * @param anUpdateReceiverValueItem Receiver value item (may be changed)
     * @return True: Molecule names of update receiver value item were changed,
     * false: Molecule names of update receiver value item were renamed only or
     * update receiver was not changed at all
     */
    private boolean updateMoleculeNameFixed(ValueItem aMoleculeTableValueItem, ValueItem anUpdateReceiverValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable") || anUpdateReceiverValueItem == null) {
            // anUpdateReceiverValueItem is not changed
            return false;
        }

        // </editor-fold>
        if (aMoleculeTableValueItem.getMatrixRowCount() != anUpdateReceiverValueItem.getMatrixRowCount()) {
            // <editor-fold defaultstate="collapsed" desc="Update">
            // <editor-fold defaultstate="collapsed" desc="Constant sum column calculations if necessary">
            // Get old sum of column values
            double tmpOldSum = -1;
            int tmpConstantSumColumn = -1;
            if (anUpdateReceiverValueItem.checkConstantSumColumn()) {
                tmpOldSum = 0;
                tmpConstantSumColumn = anUpdateReceiverValueItem.getConstantSumColumn();
                for (int i = 0; i < anUpdateReceiverValueItem.getMatrixRowCount(); i++) {
                    tmpOldSum += anUpdateReceiverValueItem.getValueAsDouble(i, tmpConstantSumColumn);
                }
            }
            // </editor-fold>
            int tmpNewRowIndex = -1;
            ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[aMoleculeTableValueItem.getMatrixRowCount()][];
            for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
                boolean tmpIsMatch = false;
                for (int k = 0; k < anUpdateReceiverValueItem.getMatrixRowCount(); k++) {
                    if (aMoleculeTableValueItem.getValue(i, 0).equals(anUpdateReceiverValueItem.getValue(k, 0))) {
                        tmpNewMatrix[i] = anUpdateReceiverValueItem.getMatrix()[k];
                        tmpIsMatch = true;
                        break;
                    }
                }
                if (!tmpIsMatch) {
                    if (tmpNewRowIndex == -1) {
                        tmpNewRowIndex = i;
                    }
                    tmpNewMatrix[i] = anUpdateReceiverValueItem.getDefaultMatrixElementRow();
                    tmpNewMatrix[i][0].setValue(aMoleculeTableValueItem.getValue(i, 0));
                    // <editor-fold defaultstate="collapsed" desc="Correct constant sum column value">
                    if (tmpConstantSumColumn > -1) {
                        // NOTE: Correct constant sum column may not have correct default value
                        tmpNewMatrix[i][tmpConstantSumColumn].setValue(String.valueOf(tmpOldSum / (double) anUpdateReceiverValueItem.getMatrixRowCount()));
                    }

                    // </editor-fold>
                }
            }
            // <editor-fold defaultstate="collapsed" desc="Correct constant sum column if necessary">
            if (tmpConstantSumColumn > -1) {
                // Get new sum of column values
                double tmpNewSum = 0;
                for (int i = 0; i < tmpNewMatrix.length; i++) {
                    tmpNewSum += Double.parseDouble(tmpNewMatrix[i][tmpConstantSumColumn].getValue());
                }
                // Correct every column value with factor tmpOldSum /
                // tmpNewSum
                // to old sum
                double tmpFactor = tmpOldSum / tmpNewSum;
                double tmpTestSum = 0;
                for (int i = 0; i < tmpNewMatrix.length; i++) {
                    tmpNewMatrix[i][tmpConstantSumColumn].setValue(String.valueOf(Double.parseDouble(tmpNewMatrix[i][tmpConstantSumColumn].getValue()) * tmpFactor));
                    tmpTestSum += Double.parseDouble(tmpNewMatrix[i][tmpConstantSumColumn].getValue());
                }
                // Check if there is a roundoff error (values may be integer
                // and
                // are formatted)
                if (tmpTestSum != tmpOldSum) {
                    if (tmpNewRowIndex == -1) {
                        // Row was removed: Row 0 will be corrected if
                        // necessary
                        tmpNewRowIndex = 0;
                    }
                    tmpNewMatrix[tmpNewRowIndex][tmpConstantSumColumn].setValue(String.valueOf(Double.parseDouble(tmpNewMatrix[tmpNewRowIndex][tmpConstantSumColumn].getValue()) - (tmpTestSum - tmpOldSum)));
                }
                // Set new default value of all column matrix element format
                // types to mean of all column values
                String tmpNewDefaultValue = String.valueOf(tmpOldSum / (double) tmpNewMatrix.length);
                for (int i = 0; i < tmpNewMatrix.length; i++) {
                    tmpNewMatrix[i][tmpConstantSumColumn].getTypeFormat().setDefaultValue(tmpNewDefaultValue);
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Correct first row editable only columns if necessary">
            ValueItemUtils.correctFirstCellOfFirstRowEditableOnlyColumns(tmpNewMatrix);
            // </editor-fold>
            anUpdateReceiverValueItem.setMatrix(tmpNewMatrix);
            return true;
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Rename if necessary">
            for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
                if (!aMoleculeTableValueItem.getValue(i, 0).equals(anUpdateReceiverValueItem.getValue(i, 0))) {
                    anUpdateReceiverValueItem.setValue(aMoleculeTableValueItem.getValue(i, 0), i, 0);
                    return false;
                }
            }
            return false;
            // </editor-fold>
        }
    }

    /**
     * Updates molecule name and particle of nearest-neighbor particle value item (see code)
     *
     * @param aMonomerTableValueItem Monomers value item (is NOT changed)
     * @param aMoleculeTableValueItem Molecules value item (is NOT changed)
     * @param aNearestNeighborParticleValueItem Nearest-neighbor particle value item (may be changed)
     */
    private void updateNearestNeighborParticleValueItem(ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem, ValueItem aNearestNeighborParticleValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }
        if (aNearestNeighborParticleValueItem == null || !aNearestNeighborParticleValueItem.getName().equals("NearestNeighborParticle")) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update and set inactive">
        LinkedList<String> tmpSortedMoleculeParticleList = this.jobUtilityMethods.getSortedMoleculeParticleList(aMonomerTableValueItem, aMoleculeTableValueItem);
        if (tmpSortedMoleculeParticleList == null || tmpSortedMoleculeParticleList.size() == 0) {
            return;
        }
        boolean tmpIsUpdate = true;
        if (aNearestNeighborParticleValueItem.getMatrixRowCount() == tmpSortedMoleculeParticleList.size()) {
            tmpIsUpdate = false;
            int tmpRowIndex = 0;
            for (String tmpMoleculeParticle : tmpSortedMoleculeParticleList) {
                String[] tmpMoleculeNameAndParticle = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(tmpMoleculeParticle);
                String tmpMoleculeName = tmpMoleculeNameAndParticle[0];
                String tmpParticle = tmpMoleculeNameAndParticle[1];
                if (!aNearestNeighborParticleValueItem.getValue(tmpRowIndex, 0).equals(tmpMoleculeName)) {
                    tmpIsUpdate = true;
                    break;
                }
                if (!aNearestNeighborParticleValueItem.getValue(tmpRowIndex, 1).equals(tmpParticle)) {
                    tmpIsUpdate = true;
                    break;
                }
                tmpRowIndex++;
            }
        } 
        if (tmpIsUpdate) {
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpSortedMoleculeParticleList.size()][];
            ValueItemDataTypeFormat tmpNonEditableTextTypeFormat = new ValueItemDataTypeFormat(false);
            ValueItemDataTypeFormat tmpActivityTypeFormat = 
                new ValueItemDataTypeFormat(
                    ModelMessage.get("JdpdInputFile.parameter.false"), 
                    new String[]{
                        ModelMessage.get("JdpdInputFile.parameter.true"),
                        ModelMessage.get("JdpdInputFile.parameter.false")
                    }
                );
            int tmpRowIndex = 0;
            for (String tmpMoleculeParticle : tmpSortedMoleculeParticleList) {
                String[] tmpMoleculeNameAndParticle = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(tmpMoleculeParticle);
                String tmpMoleculeName = tmpMoleculeNameAndParticle[0];
                String tmpParticle = tmpMoleculeNameAndParticle[1];
                ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[3];
                tmpRow[0] = new ValueItemMatrixElement(tmpMoleculeName, tmpNonEditableTextTypeFormat);
                tmpRow[1] = new ValueItemMatrixElement(tmpParticle, tmpNonEditableTextTypeFormat);
                tmpRow[2] = new ValueItemMatrixElement(tmpActivityTypeFormat);
                tmpMatrix[tmpRowIndex] = tmpRow;
                tmpRowIndex++;
            }
            aNearestNeighborParticleValueItem.setMatrix(tmpMatrix);
        }
        // </editor-fold>
    }
    
    /**
     * Updates molecule name selections of update receiver value item (see code)
     *
     * @param aMoleculeTableValueItem MoleculeTable value item (is NOT changed)
     * @param anUpdateReceiverValueItem Receiver value item (may be changed)
     * @return True: Molecule name selections of update receiver value item were
     * changed, false: Molecule name selections of update receiver value item
     * were renamed only or update receiver was not changed at all
     */
    private boolean updateMoleculeNameSelection(ValueItem aMoleculeTableValueItem, ValueItem anUpdateReceiverValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable") || anUpdateReceiverValueItem == null) {
            // anUpdateReceiverValueItem is not changed
            return false;
        }
        if (!anUpdateReceiverValueItem.getTypeFormat(0).hasExclusiveSelectionTexts()) {
            // anUpdateReceiverValueItem is not changed
            return false;
        }

        // </editor-fold>
        LinkedList<String> tmpCombinedSelectionTextList = anUpdateReceiverValueItem.getCombinedExclusiveSelectionTextList();
        if (tmpCombinedSelectionTextList.size() != aMoleculeTableValueItem.getMatrixRowCount()) {
            // <editor-fold defaultstate="collapsed" desc="Update">
            String[] tmpCompleteSelectionTexts = new String[aMoleculeTableValueItem.getMatrixRowCount()];
            HashMap<String, String> tmpCompleteSelectionTextTable = new HashMap<String, String>(ModelDefinitions.DEFAULT_NUMBER_OF_SELECTION_TEXT_VALUES);
            for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
                String tmpValue = aMoleculeTableValueItem.getValue(i, 0);
                tmpCompleteSelectionTexts[i] = tmpValue;
                if (!tmpCompleteSelectionTextTable.containsKey(tmpValue)) {
                    tmpCompleteSelectionTextTable.put(tmpValue, tmpValue);
                }
            }
            ValueItemMatrixElement[] tmpDefaultMatrixElementRow = anUpdateReceiverValueItem.getDefaultMatrixElementRow();
            ValueItemMatrixElement[][] tmpReceiverMatrix = anUpdateReceiverValueItem.getMatrix();
            int tmpCounter = tmpReceiverMatrix.length;
            for (int i = 0; i < tmpReceiverMatrix.length; i++) {
                if (!tmpCompleteSelectionTextTable.containsKey(tmpReceiverMatrix[i][0].getValue())) {
                    tmpReceiverMatrix[i] = null;
                    tmpCounter--;
                }
            }
            if (tmpCounter == 0) {
                ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][];
                tmpMatrix[0] = tmpDefaultMatrixElementRow;
                // IMPORTANT: Set exclusive selection texts (Parameter true)
                tmpMatrix[0][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpCompleteSelectionTexts, true));
                anUpdateReceiverValueItem.setMatrix(tmpMatrix);
            } else {
                ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[tmpCounter][];
                int tmpIndex = 0;
                for (int i = 0; i < tmpReceiverMatrix.length; i++) {
                    if (tmpReceiverMatrix[i] != null) {
                        tmpNewMatrix[tmpIndex++] = tmpReceiverMatrix[i];
                    }
                }
                // <editor-fold defaultstate="collapsed" desc="Correct exclusive selection texts if necessary">
                ValueItemMatrixElement[] tmpMatrixElementColumn = new ValueItemMatrixElement[tmpNewMatrix.length];
                for (int i = 0; i < tmpNewMatrix.length; i++) {
                    tmpMatrixElementColumn[i] = tmpNewMatrix[i][0];
                }
                ValueItemUtils.correctExclusiveSelectionTextsWithCompleteTexts(tmpMatrixElementColumn, tmpCompleteSelectionTexts);

                // </editor-fold>
                anUpdateReceiverValueItem.setMatrix(tmpNewMatrix);
            }
            return true;
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Rename if necessary">
            // Rename value and selection texts
            String tmpNewSelectionText = null;
            String tmpOldSelectionText = null;
            for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
                if (tmpCombinedSelectionTextList.contains(aMoleculeTableValueItem.getValue(i, 0))) {
                    tmpCombinedSelectionTextList.remove(aMoleculeTableValueItem.getValue(i, 0));
                } else {
                    tmpNewSelectionText = aMoleculeTableValueItem.getValue(i, 0);
                }
            }
            if (tmpCombinedSelectionTextList.size() > 0) {
                // <editor-fold defaultstate="collapsed" desc="Rename is necessary">
                tmpOldSelectionText = tmpCombinedSelectionTextList.getFirst();
                for (int i = 0; i < anUpdateReceiverValueItem.getMatrixRowCount(); i++) {
                    ValueItemDataTypeFormat tmpTypeFormat = anUpdateReceiverValueItem.getTypeFormat(i, 0);
                    String[] tmpSelectionTexts = tmpTypeFormat.getSelectionTexts();
                    boolean tmpIsMatch = false;
                    for (int k = 0; k < tmpSelectionTexts.length; k++) {
                        if (tmpOldSelectionText.equals(tmpSelectionTexts[k])) {
                            tmpSelectionTexts[k] = tmpNewSelectionText;
                            tmpIsMatch = true;
                        }
                    }
                    if (tmpIsMatch) {
                        Arrays.sort(tmpSelectionTexts);
                        if (tmpOldSelectionText.equals(anUpdateReceiverValueItem.getValue(i, 0))) {
                            // Set type format FIRST ...
                            tmpTypeFormat.setDefaultValueAndSelectionTexts(tmpNewSelectionText, tmpSelectionTexts);
                            // ... THEN set value (since validity with type
                            // format is checked)
                            anUpdateReceiverValueItem.setValue(tmpNewSelectionText, i, 0);
                        } else {
                            tmpTypeFormat.setSelectionTexts(tmpSelectionTexts);
                        }
                    }
                }

                // </editor-fold>
            }
            return false;
            // </editor-fold>
        }
    }

    /**
     * Updates molecule display settings value items in
     * aJobInputValueItemContainer
     *
     * @param aJobInputValueItemContainer Job Input value item container
     */
    private void updateMoleculeDisplaySettings(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return;
        }

        // </editor-fold>
        ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainer = this.jobUtilityMethods.getNewMoleculeDisplaySettingsValueItemContainer(aJobInputValueItemContainer);
        if (tmpMoleculeDisplaySettingsValueItemContainer == null) {
            return;
        }
        ValueItem[] tmpMoleculeDisplaySettingsValueItems = tmpMoleculeDisplaySettingsValueItemContainer.getValueItemsOfContainer();
        if (tmpMoleculeDisplaySettingsValueItems == null) {
            return;
        }
        for (ValueItem tmpSingleMoleculeDisplaySettingValueItem : tmpMoleculeDisplaySettingsValueItems) {
            ValueItem tmpJobInputValueItem = aJobInputValueItemContainer.getValueItem(tmpSingleMoleculeDisplaySettingValueItem.getName());
            if (tmpJobInputValueItem != null) {
                tmpJobInputValueItem.setMatrix(tmpSingleMoleculeDisplaySettingValueItem.getMatrix());
                // IMPORTANT: Molecule display settings value items use supplementary data so copy these too!
                if (tmpSingleMoleculeDisplaySettingValueItem.hasSupplementaryData()) {
                    tmpJobInputValueItem.setSupplementaryData(tmpSingleMoleculeDisplaySettingValueItem.getSupplementaryData());
                }
            }
        }
    }

    /**
     * Updates molecule-particle-pair RDF calculation value item in specific
     * way with ParticleTable value item (see code)
     *
     * @param aMonomerTableValueItem Monomers value item (is NOT changed)
     * @param aMoleculeTableValueItem Molecules value item (is NOT changed)
     * @param aMoleculeParticlePairRdfCalculationValueItem
     * Molecule-particle-pair RDF calculation value item (may be changed)
     */
    private void updateMoleculeParticlePairRdfCalculation(ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem, ValueItem aMoleculeParticlePairRdfCalculationValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }
        if (aMoleculeParticlePairRdfCalculationValueItem == null || !aMoleculeParticlePairRdfCalculationValueItem.getName().equals("MoleculeParticlePairRdfCalculation")) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update and set inactive">
        LinkedList<String> tmpSortedMoleculeParticleList = this.jobUtilityMethods.getSortedMoleculeParticleList(aMonomerTableValueItem, aMoleculeTableValueItem);
        if (tmpSortedMoleculeParticleList == null || tmpSortedMoleculeParticleList.size() == 0) {
            return;
        }
        String[] tmpSortedMoleculeParticleArray = tmpSortedMoleculeParticleList.toArray(new String[0]);
        aMoleculeParticlePairRdfCalculationValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
            new ValueItemDataTypeFormat(tmpSortedMoleculeParticleArray), // Particle_in_molecule_1
            new ValueItemDataTypeFormat(tmpSortedMoleculeParticleArray), // Particle_in_molecule_2
        });
        aMoleculeParticlePairRdfCalculationValueItem.setActivity(false);
        // </editor-fold>
    }

    /**
     * Updates molecule-particle-pair distance calculation value item in
     * specific way with ParticleTable value item (see code)
     *
     * @param aMonomerTableValueItem Monomers value item (is NOT changed)
     * @param aMoleculeTableValueItem Molecules value item (is NOT changed)
     * @param aMoleculeParticlePairDistanceCalculationValueItem
     * Molecule-particle-pair distance calculation value item (may be
     * changed)
     */
    private void updateMoleculeParticlePairDistanceCalculation(ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem, ValueItem aMoleculeParticlePairDistanceCalculationValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }
        if (aMoleculeParticlePairDistanceCalculationValueItem == null || !aMoleculeParticlePairDistanceCalculationValueItem.getName().equals("MoleculeParticlePairDistanceCalculation")) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update and set inactive">
        LinkedList<String> tmpSortedMoleculeParticleList = this.jobUtilityMethods.getSortedMoleculeParticleList(aMonomerTableValueItem, aMoleculeTableValueItem);
        if (tmpSortedMoleculeParticleList == null || tmpSortedMoleculeParticleList.size() == 0) {
            return;
        }
        String[] tmpSortedMoleculeParticleArray = tmpSortedMoleculeParticleList.toArray(new String[0]);
        aMoleculeParticlePairDistanceCalculationValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
            new ValueItemDataTypeFormat(tmpSortedMoleculeParticleArray), // Particle_in_molecule_1
            new ValueItemDataTypeFormat(tmpSortedMoleculeParticleArray), // Particle_in_molecule_2
        });
        aMoleculeParticlePairDistanceCalculationValueItem.setActivity(false);

        // </editor-fold>
    }

    /**
     * Updates protein distance force constants
     * 
     * @param aMoleculeTableValueItem Molecules value item (is NOT changed)
     * @param aProteinDistanceForcesValueItem Back distance force
     * constants value item (may be changed)
     */
    private void updateProteinDistanceForces(ValueItem aMoleculeTableValueItem, ValueItem aProteinDistanceForcesValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }
        if (aProteinDistanceForcesValueItem == null || !aProteinDistanceForcesValueItem.getName().equals("ProteinDistanceForces")) {
            return;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Determine tmpTotalMaximumDistance">
            int tmpTotalMaximumDistanceType = -1;
            LinkedList<String> tmpProteinNameList = new LinkedList<>();
            HashMap<String, Integer> tmpProteinNameToMaximumDistanceMap = new HashMap<>(aMoleculeTableValueItem.getMatrixRowCount());
            for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
                // Column 1: Molecular structure with possible protein data
                if (aMoleculeTableValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    // Column 0: Name of protein
                    String tmpSingleProteinName = aMoleculeTableValueItem.getValue(i, 0);
                    tmpProteinNameList.add(tmpSingleProteinName);
                    PdbToDpd tmpPdbToDPD = PdbToDpdPool.getInstance().getPdbToDpd(aMoleculeTableValueItem.getValueItemMatrixElement(i, 1).getProteinData());
                    int tmpSingleProteinMaximumDistanceType = tmpPdbToDPD.getMaxDistanceTypeOfProteinDistanceForces();
                    PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDPD, aMoleculeTableValueItem.getValueItemMatrixElement(i, 1).getProteinData());
                    tmpProteinNameToMaximumDistanceMap.put(tmpSingleProteinName, tmpSingleProteinMaximumDistanceType);
                    if (tmpSingleProteinMaximumDistanceType > tmpTotalMaximumDistanceType) {
                        tmpTotalMaximumDistanceType = tmpSingleProteinMaximumDistanceType;
                    }
                }
            }
            // </editor-fold>
            if (tmpTotalMaximumDistanceType <= 0) {
                // No protein definition: Lock aProteinDistanceForcesValueItem
                aProteinDistanceForcesValueItem.setLocked(true);
            } else {
                // Protein definition exists: Unlock, set and activate aProteinDistanceForcesValueItem
                // <editor-fold defaultstate="collapsed" desc="Set column names and widths">
                LinkedList<String> tmpColumnNameList = new LinkedList<>();
                LinkedList<String> tmpColumnWidthList = new LinkedList<>();
                tmpColumnNameList.add(ModelMessage.get("JdpdInputFile.parameter.proteinBackboneDistancesType"));
                tmpColumnWidthList.add(ModelDefinitions.CELL_WIDTH_TEXT_150);
                for (String tmpProteinName : tmpProteinNameList) {
                    tmpColumnNameList.add(String.format(ModelMessage.get("JdpdInputFile.parameter.proteinDistanceForceConstantFormat"), tmpProteinName));
                    tmpColumnWidthList.add(ModelDefinitions.CELL_WIDTH_TEXT_200);
                }
                String[] tmpColumnNameArray = tmpColumnNameList.toArray(new String[0]);
                String[] tmpColumnWidthArray = tmpColumnWidthList.toArray(new String[0]);
                aProteinDistanceForcesValueItem.setMatrixColumnNames(tmpColumnNameArray);
                aProteinDistanceForcesValueItem.setMatrixColumnWidths(tmpColumnWidthArray);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set matrix">
                ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpTotalMaximumDistanceType][1 + tmpProteinNameList.size()];
                // Parameter false: Non-editable
                ValueItemDataTypeFormat tmpNonEditableTextTypeFormat = new ValueItemDataTypeFormat(false);
                ValueItemDataTypeFormat tmpEditableNumericTypeFormat = new ValueItemDataTypeFormat("0.00", 6, 0.0, Double.MAX_VALUE);
                ValueItemDataTypeFormat tmpNonEditableNumericTypeFormat = new ValueItemDataTypeFormat("0.00", 6, false);
                for (int i = 0; i < tmpTotalMaximumDistanceType; i++) {
                    // Set distance type (NOTE: Distance type = 2 means 1-2 distance, which is distance value 1 in PdbToDpd
                    tmpMatrix[i][0] = new ValueItemMatrixElement(String.valueOf(i + 2), tmpNonEditableTextTypeFormat);
                    int tmpColumnIndex = 1;
                    for (String tmpProteinName : tmpProteinNameList) {
                        int tmpSingleProteinMaximumDistance = tmpProteinNameToMaximumDistanceMap.get(tmpProteinName);
                        if (i < tmpSingleProteinMaximumDistance) {
                            // IMPORTANT: Set force constant to 0.0
                            tmpMatrix[i][tmpColumnIndex++] = new ValueItemMatrixElement("0.00", tmpEditableNumericTypeFormat);
                        } else {
                            // IMPORTANT: Set force constant to 0.0
                            tmpMatrix[i][tmpColumnIndex++] = new ValueItemMatrixElement("0.00", tmpNonEditableNumericTypeFormat);
                        }
                    }
                }
                // </editor-fold>
                aProteinDistanceForcesValueItem.setMatrix(tmpMatrix);
                aProteinDistanceForcesValueItem.setLocked(false);
                aProteinDistanceForcesValueItem.setActivity(true);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- ParticleTable as update notifier">
    /**
     * Updates particle matrix value item if necessary with particles from
     * monomers and molecules value item
     *
     * @param aParticleTableValueItem Particle matrix value item (may be
     * changed)
     * @param aMonomerTableValueItem Monomers value item (is NOT changed)
     * @param aMoleculeTableValueItem Molecules value item (is NOT changed)
     */
    private void updateParticleTableValueItemWithMonomerAndMoleculeParticles(ValueItem aParticleTableValueItem, ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create array of all particles of monomers and molecules">
        String[] tmpParticlesOfMonomers = null;
        String[] tmpParticlesOfMolecules = null;
        HashMap<String, String> tmpParticlesToCheckHashMap = null;
        if (aMonomerTableValueItem.isActive()) {
            tmpParticlesOfMonomers = JobUtils.getAllParticlesOfMonomerTableValueItem(aMonomerTableValueItem);
            tmpParticlesOfMolecules = JobUtils.getAllParticlesOfMoleculeTableValueItem(aMoleculeTableValueItem);
            tmpParticlesToCheckHashMap = new HashMap<String, String>(tmpParticlesOfMonomers.length + tmpParticlesOfMolecules.length);
            for (String tmpParticle : tmpParticlesOfMonomers) {
                if (!tmpParticlesToCheckHashMap.containsKey(tmpParticle)) {
                    tmpParticlesToCheckHashMap.put(tmpParticle, tmpParticle);
                }
            }
        } else {
            tmpParticlesOfMolecules = JobUtils.getAllParticlesOfMoleculeTableValueItem(aMoleculeTableValueItem);
            tmpParticlesToCheckHashMap = new HashMap<String, String>(tmpParticlesOfMolecules.length);
        }
        for (String tmpParticle : tmpParticlesOfMolecules) {
            if (!tmpParticlesToCheckHashMap.containsKey(tmpParticle)) {
                tmpParticlesToCheckHashMap.put(tmpParticle, tmpParticle);
            }
        }
        String[] tmpParticlesToCheck = tmpParticlesToCheckHashMap.values().toArray(new String[0]);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check if update of particle matrix is necessary">
        boolean tmpIsUpdateNecessary = false;
        if (aParticleTableValueItem.getMatrixRowCount() != tmpParticlesToCheck.length) {
            tmpIsUpdateNecessary = true;
        } else {
            HashMap<String, String> tmpParticlesOfParticleMatrixHashMap = JobUtils.getParticleMatrixParticlesHashMap(aParticleTableValueItem);
            for (String tmpParticle : tmpParticlesToCheck) {
                if (!tmpParticlesOfParticleMatrixHashMap.containsKey(tmpParticle)) {
                    tmpIsUpdateNecessary = true;
                    break;
                }
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update particle matrix if necessary">
        if (tmpIsUpdateNecessary) {
            ValueItem tmpUpdatedParticleTableValueItem = StandardParticleInteractionData.getInstance().getParticleTableValueItemWithDefinedParticles(tmpParticlesToCheck);
            aParticleTableValueItem.setMatrix(tmpUpdatedParticleTableValueItem.getMatrix());
        }

        // </editor-fold>
    }

    /**
     * Updates ParticleTable value item itself.
     *
     * @param aParticleTableValueItem Value item ParticleTable (may be
     * changed)
     */
    private void updateParticleMatrixItself(ValueItem aParticleTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }

        // </editor-fold>
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            String tmpParticle = aParticleTableValueItem.getValue(i, 0);
            StandardParticleDescription tmpParticleData = StandardParticleInteractionData.getInstance().getParticleDescription(tmpParticle);
            if (tmpParticleData == null) {
                // This is a fatal error: Current particle descriptions do NOT contain particle of earlier definition. This should never happen!
                // <editor-fold defaultstate="collapsed" desc="Fatal error message">
                JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("ValueItem.Error.MissingCorruptParticleData"), tmpParticle),
                        ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.appendToLogfile(true, "UtilityJobUpdate: MissingCorruptParticleData in if (tmpParticleData == null)");
                ModelUtils.exitApplication(-1);
                // </editor-fold>
            } else {
                aParticleTableValueItem.setValue(tmpParticleData.getName(), i, 1);
                aParticleTableValueItem.setValue(tmpParticleData.getMolWeightInDpdUnits(), i, 2);
                aParticleTableValueItem.setValue(tmpParticleData.getCharge(), i, 3);
                aParticleTableValueItem.setValue(tmpParticleData.getMolWeightInGMol(), i, 4);
                aParticleTableValueItem.setValue(tmpParticleData.getVolume(), i, 5);
                aParticleTableValueItem.setValue(tmpParticleData.getGraphicsRadius(), i, 6);
                aParticleTableValueItem.setValue(tmpParticleData.getStandardColor(), i, 7);
            }
        }
    }

    /**
     * Updates interaction table value item in specific way with
     * ParticleTable value item (see code)
     *
     * @param aParticleTableValueItem Particle matrix value item (is NOT
     * changed)
     * @param anInteractionTableValueItem Interaction table value item (may be
     * changed)
     * @param aTemperatureValueItem Temperature value item (is NOT changed)
     * @param aDensityValueItem Density value item (is NOT changed)
     * @param anIsEnforced True: Update is enforced and NOT checked, False:
     * Update is ONLY performed if necessary after check
     */
    private void updateInteractionTable(ValueItem aParticleTableValueItem, ValueItem anInteractionTableValueItem, ValueItem aTemperatureValueItem, ValueItem aDensityValueItem, boolean anIsEnforced) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (anInteractionTableValueItem == null || !anInteractionTableValueItem.getName().equals("InteractionTable")) {
            return;
        }
        if (aTemperatureValueItem == null || !aTemperatureValueItem.getName().equals("Temperature")) {
            return;
        }
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create particle pairs">
        LinkedList<String> tmpParticlePairList = new LinkedList<String>();
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            for (int k = 0; k <= i; k++) {
                tmpParticlePairList.add(aParticleTableValueItem.getValue(i, 0) + SpicesConstants.PARTICLE_SEPARATOR + aParticleTableValueItem.getValue(k, 0));
            }
        }
        Collections.sort(tmpParticlePairList);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check if update is necessary">
        boolean tmpIsUpdateNecessary = false;
        if (anIsEnforced) {
            tmpIsUpdateNecessary = true;
        } else {
            if (tmpParticlePairList.size() != anInteractionTableValueItem.getMatrixRowCount()) {
                tmpIsUpdateNecessary = true;
            } else {
                int tmpIndex = 0;
                for (String tmpSinglePair : tmpParticlePairList) {
                    if (!tmpSinglePair.equals(anInteractionTableValueItem.getValue(tmpIndex++, 0))) {
                        tmpIsUpdateNecessary = true;
                        break;
                    }
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Perform update if necessary">
        if (tmpIsUpdateNecessary) {
            ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[tmpParticlePairList.size()][];
            int tmpIndex = 0;
            boolean tmpHasError = false;
            for (String tmpSinglePair : tmpParticlePairList) {
                boolean tmpIsMatch = false;
                if (!anIsEnforced) {
                    for (int k = 0; k < anInteractionTableValueItem.getMatrixRowCount(); k++) {
                        if (tmpSinglePair.equals(anInteractionTableValueItem.getValue(k, 0))) {
                            tmpNewMatrix[tmpIndex] = anInteractionTableValueItem.getMatrix()[k];
                            tmpIsMatch = true;
                            break;
                        }
                    }
                }
                if (!tmpIsMatch) {
                    tmpNewMatrix[tmpIndex] = anInteractionTableValueItem.getDefaultMatrixElementRow();
                    tmpNewMatrix[tmpIndex][0].setValue(tmpSinglePair);
                    String tmpInteractionValue = ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString");
                    if (StandardParticleInteractionData.getInstance().hasInteraction(tmpSinglePair, aTemperatureValueItem.getValue())) {
                        // <editor-fold defaultstate="collapsed" desc="Calculate interaction parameter">
                        String[] tmpParticles = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(tmpSinglePair);
                        String tmpParticleA = tmpParticles[0];
                        String tmpParticleB = tmpParticles[1];
                        tmpInteractionValue = StandardParticleInteractionData.getInstance().getInteraction(tmpParticleA, tmpParticleB, aTemperatureValueItem.getValue());
                        if (tmpInteractionValue == null) {
                            tmpInteractionValue = ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString");
                            tmpHasError = true;
                        } else {
                            tmpInteractionValue = this.stringUtilityMethods.formatDoubleValue(tmpInteractionValue, ModelDefinitions.INTERACTION_NUMBER_OF_DECIMALS);
                        }
                        // </editor-fold>
                    } else {
                        tmpHasError = true;
                    }
                    tmpNewMatrix[tmpIndex][1].setValue(tmpInteractionValue);
                }
                tmpIndex++;
            }
            anInteractionTableValueItem.setMatrix(tmpNewMatrix);
            if (tmpHasError) {
                anInteractionTableValueItem.setError(ModelMessage.get("StandardParticleInteractionData.MissingInteractionValue"));
            } else {
                anInteractionTableValueItem.removeError();
            }
        }
        // </editor-fold>
    }

    /**
     * Updates particle-pair RDF calculation value item in specific way with
     * ParticleTable value item (see code)
     *
     * @param aParticleTableValueItem Particle matrix value item (is NOT
     * changed)
     * @param aParticlePairRdfCalculationValueItem Particle-pair RDF calculation
     * value item (may be changed)
     */
    private void updateParticlePairRdfCalculation(ValueItem aParticleTableValueItem, ValueItem aParticlePairRdfCalculationValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (aParticlePairRdfCalculationValueItem == null || !aParticlePairRdfCalculationValueItem.getName().equals("ParticlePairRdfCalculation")) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create particle list">
        LinkedList<String> tmpParticleList = new LinkedList<String>();
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            tmpParticleList.add(aParticleTableValueItem.getValue(i, 0));
        }
        Collections.sort(tmpParticleList);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update and set inactive">
        String[] tmpParticles = tmpParticleList.toArray(new String[0]);
        aParticlePairRdfCalculationValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
            new ValueItemDataTypeFormat(tmpParticles), // Particle_1
            new ValueItemDataTypeFormat(tmpParticles), // Particle_2
        });
        aParticlePairRdfCalculationValueItem.setActivity(false);

        // </editor-fold>
    }

    /**
     * Updates particle-pair distance calculation value item in specific way
     * with ParticleTable value item (see code)
     *
     * @param aParticleTableValueItem Particle matrix value item (is NOT
     * changed)
     * @param aParticlePairDistanceCalculationValueItem Particle-pair distance
     * calculation value item (may be changed)
     */
    private void updateParticlePairDistanceCalculation(ValueItem aParticleTableValueItem, ValueItem aParticlePairDistanceCalculationValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (aParticlePairDistanceCalculationValueItem == null || !aParticlePairDistanceCalculationValueItem.getName().equals("ParticlePairDistanceCalculation")) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create particle list">
        LinkedList<String> tmpParticleList = new LinkedList<String>();
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            tmpParticleList.add(aParticleTableValueItem.getValue(i, 0));
        }
        Collections.sort(tmpParticleList);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update and set inactive">
        String[] tmpParticles = tmpParticleList.toArray(new String[0]);
        aParticlePairDistanceCalculationValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
            new ValueItemDataTypeFormat(tmpParticles), // Particle_1
            new ValueItemDataTypeFormat(tmpParticles), // Particle_2
        });
        aParticlePairDistanceCalculationValueItem.setActivity(false);

        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Density and Temperature as update notifier">
    /**
     * Updates InteractionTable value item in specific way with Density and
     * Temperature value item (see code)
     *
     * @param aDensityValueItem Density value item (is NOT changed)
     * @param aTemperatureValueItem Temperature value item (is NOT changed)
     * @param aParticleTableValueItem Particle matrix value item (is NOT changed)
     * @param anInteractionTableValueItem InteractionTable value item (may be
     * changed)
     */
    private void updateInteractionTableWithNewDensityAndTemperature(ValueItem aDensityValueItem, ValueItem aTemperatureValueItem, ValueItem aParticleTableValueItem,
            ValueItem anInteractionTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return;
        }
        if (aTemperatureValueItem == null || !aTemperatureValueItem.getName().equals("Temperature")) {
            return;
        }
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (anInteractionTableValueItem == null || !anInteractionTableValueItem.getName().equals("InteractionTable")) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Perform update">
        ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[anInteractionTableValueItem.getMatrixRowCount()][];
        boolean tmpHasError = false;
        for (int i = 0; i < anInteractionTableValueItem.getMatrixRowCount(); i++) {
            tmpNewMatrix[i] = anInteractionTableValueItem.getDefaultMatrixElementRow();
            String tmpSinglePair = anInteractionTableValueItem.getValue(i, 0);
            tmpNewMatrix[i][0].setValue(tmpSinglePair);
            String tmpInteractionValue = ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString");
            if (StandardParticleInteractionData.getInstance().hasInteraction(tmpSinglePair, aTemperatureValueItem.getValue())) {
                // <editor-fold defaultstate="collapsed" desc="Calculate interaction parameter">
                String[] tmpParticles = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(tmpSinglePair);
                String tmpParticleA = tmpParticles[0];
                String tmpParticleB = tmpParticles[1];
                tmpInteractionValue = StandardParticleInteractionData.getInstance().getInteraction(tmpParticleA, tmpParticleB, aTemperatureValueItem.getValue());
                if (tmpInteractionValue == null) {
                    tmpInteractionValue = ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString");
                    tmpHasError = true;
                } else {
                    tmpInteractionValue = this.stringUtilityMethods.formatDoubleValue(tmpInteractionValue, 4);
                }

                // </editor-fold>
            } else {
                tmpHasError = true;
            }
            tmpNewMatrix[i][1].setValue(tmpInteractionValue);
        }
        anInteractionTableValueItem.setMatrix(tmpNewMatrix);
        if (tmpHasError) {
            anInteractionTableValueItem.setError(ModelMessage.get("StandardParticleInteractionData.MissingInteractionValue"));
        } else {
            anInteractionTableValueItem.removeError();
        }
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- BoxSize as update notifier/receiver">
    /**
     * Updates BoxSize value item itself (see code). NOTE: Volume of box is not
     * changed.
     *
     * @param aBoxSizeValueItem Box size value item (may be changed)
     * @param aLengthConversionFactor Length conversion factor from DPD length
     * to physical length in Angstrom
     */
    private void updateBoxSizeItself(ValueItem aBoxSizeValueItem, double aLengthConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoxSizeValueItem == null || !aBoxSizeValueItem.getName().equals("BoxSize")) {
            return;
        }
        if (aLengthConversionFactor < 0.0) {
            return;
        }

        // </editor-fold>
        double tmpX = aBoxSizeValueItem.getValueAsDouble(0, 0);
        double tmpY = aBoxSizeValueItem.getValueAsDouble(0, 1);
        double tmpZ = aBoxSizeValueItem.getValueAsDouble(0, 2);
        double tmpVolume = tmpX * tmpY * tmpZ;
        this.updateBoxSizeWithNewVolume(aBoxSizeValueItem, tmpVolume, aLengthConversionFactor);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- BoxSize as update receiver">
    /**
     * Updates box size value item (see code)
     *
     * @param aBoxSizeValueItem Box size value item (may be changed)
     * @param aDensityValueItem Density value item (is NOT changed)
     * @param aMonomerTableValueItem Value item MonomerTable (is NOT changed)
     * @param aMoleculeTableValueItem Value itemMoleculeTable (is NOT changed)
     * @param aQuantityValueItem Value item Quantity (is NOT changed)
     * @param aParticleTableValueItem Value item ParticleTable (is NOT
     * changed)
     */
    private void updateBoxSize(
        ValueItem aBoxSizeValueItem, 
        ValueItem aDensityValueItem, 
        ValueItem aMonomerTableValueItem, 
        ValueItem aMoleculeTableValueItem, 
        ValueItem aQuantityValueItem,
        ValueItem aParticleTableValueItem
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoxSizeValueItem == null || !aBoxSizeValueItem.getName().equals("BoxSize")) {
            return;
        }
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return;
        }
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return;
        }
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (aDensityValueItem.hasError() || aMonomerTableValueItem.hasError() || aMoleculeTableValueItem.hasError() || aQuantityValueItem.hasError()) {
            aBoxSizeValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
            return;
        }

        // </editor-fold>
        // First remove error if necessary
        if (aBoxSizeValueItem.hasError()) {
            aBoxSizeValueItem.removeError();
        }
        double tmpDensity = aDensityValueItem.getValueAsDouble();
        double tmpTotalNumberOfParticles = this.jobUtilityMethods.getTotalNumberOfParticlesOfSimulation(aMonomerTableValueItem, aMoleculeTableValueItem, aQuantityValueItem);
        double tmpNewVolume = tmpTotalNumberOfParticles / tmpDensity;
        double tmpLengthConversionFactor = 
            this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(
                aParticleTableValueItem, 
                aMonomerTableValueItem, 
                aMoleculeTableValueItem,
                aQuantityValueItem, 
                aDensityValueItem
            );
        if (tmpLengthConversionFactor == -1.0) {
            return;
        }
        this.updateBoxSizeWithNewVolume(aBoxSizeValueItem, tmpNewVolume, tmpLengthConversionFactor);
    }

    /**
     * Updates BoxSize value item with new volume (see code)
     *
     * @param aBoxSizeValueItem BoxSize value item (may be changed)
     * @param aNewVolume New volume
     * @param aLengthConversionFactor Length conversion factor from DPD length
     * to physical length in Angstrom
     */
    private void updateBoxSizeWithNewVolume(ValueItem aBoxSizeValueItem, double aNewVolume, double aLengthConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoxSizeValueItem == null || !aBoxSizeValueItem.getName().equals("BoxSize")) {
            return;
        }
        if (aLengthConversionFactor == -1.0) {
            return;
        }
        // </editor-fold>
        // Three box sides can NOT be fixed
        if (aBoxSizeValueItem.getValue(0, 3).equals(ModelMessage.get("JdpdInputFile.parameter.fixed")) 
            && aBoxSizeValueItem.getValue(0, 5).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))
        ) {
            aBoxSizeValueItem.setValue(ModelMessage.get("JdpdInputFile.parameter.flexible"), 0, 7);
        }
        double tmpFix1 = -1.0;
        double tmpFix2 = -1.0;
        if (aBoxSizeValueItem.getValue(0, 3).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
            tmpFix1 = aBoxSizeValueItem.getValueAsDouble(0, 4) / aLengthConversionFactor;
            if (aBoxSizeValueItem.getValue(0, 5).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
                tmpFix2 = aBoxSizeValueItem.getValueAsDouble(0, 6) / aLengthConversionFactor;
            } else if (aBoxSizeValueItem.getValue(0, 7).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
                tmpFix2 = aBoxSizeValueItem.getValueAsDouble(0, 8) / aLengthConversionFactor;
            }
        } else if (aBoxSizeValueItem.getValue(0, 5).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
            tmpFix1 = aBoxSizeValueItem.getValueAsDouble(0, 6) / aLengthConversionFactor;
            if(aBoxSizeValueItem.getValue(0, 7).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
                tmpFix2 = aBoxSizeValueItem.getValueAsDouble(0, 8) / aLengthConversionFactor;
            }
        } else if (aBoxSizeValueItem.getValue(0, 7).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
            tmpFix1 = aBoxSizeValueItem.getValueAsDouble(0, 8) / aLengthConversionFactor;
        }
        double tmpNew1 = -1.0;
        double tmpNew2 = -1.0;
        double tmpNew3 = -1.0;
        if (tmpFix1 > 0.0) {
            if (tmpFix2 > 0.0) {
                // Two fixed box sides
                tmpNew1 = aNewVolume / (tmpFix1 * tmpFix2);
            } else {
                // One fixed box side
                tmpNew1 = Math.sqrt(aNewVolume / tmpFix1);
                tmpNew2 = tmpNew1;
            }
        } else {
            // No fixed box side
            tmpNew1 = Math.cbrt(aNewVolume);
            tmpNew2 = tmpNew1; 
            tmpNew3 = tmpNew1; 
        }
        if (aBoxSizeValueItem.getValue(0, 3).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
            aBoxSizeValueItem.setValue(String.valueOf(tmpFix1), 0, 0);
            aBoxSizeValueItem.setValue(String.valueOf(tmpFix1 * aLengthConversionFactor), 0, 4);
            if (aBoxSizeValueItem.getValue(0, 5).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
                aBoxSizeValueItem.setValue(String.valueOf(tmpFix2), 0, 1);
                aBoxSizeValueItem.setValue(String.valueOf(tmpFix2 * aLengthConversionFactor), 0, 6);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1), 0, 2);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1 * aLengthConversionFactor), 0, 8);
            } else if (aBoxSizeValueItem.getValue(0, 7).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1), 0, 1);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1 * aLengthConversionFactor), 0, 6);
                aBoxSizeValueItem.setValue(String.valueOf(tmpFix2), 0, 2);
                aBoxSizeValueItem.setValue(String.valueOf(tmpFix2 * aLengthConversionFactor), 0, 8);
            } else {
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1), 0, 1);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1 * aLengthConversionFactor), 0, 6);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew2), 0, 2);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew2 * aLengthConversionFactor), 0, 8);
            }
        } else if (aBoxSizeValueItem.getValue(0, 5).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
            aBoxSizeValueItem.setValue(String.valueOf(tmpFix1), 0, 1);
            aBoxSizeValueItem.setValue(String.valueOf(tmpFix1 * aLengthConversionFactor), 0, 6);
            if(aBoxSizeValueItem.getValue(0, 7).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1), 0, 0);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1 * aLengthConversionFactor), 0, 4);
                aBoxSizeValueItem.setValue(String.valueOf(tmpFix2), 0, 2);
                aBoxSizeValueItem.setValue(String.valueOf(tmpFix2 * aLengthConversionFactor), 0, 8);
            } else {
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1), 0, 0);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew1 * aLengthConversionFactor), 0, 4);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew2), 0, 2);
                aBoxSizeValueItem.setValue(String.valueOf(tmpNew2 * aLengthConversionFactor), 0, 8);
            }
        } else if (aBoxSizeValueItem.getValue(0, 7).equals(ModelMessage.get("JdpdInputFile.parameter.fixed"))) {
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew1), 0, 0);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew1 * aLengthConversionFactor), 0, 4);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew2), 0, 1);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew2 * aLengthConversionFactor), 0, 6);
            aBoxSizeValueItem.setValue(String.valueOf(tmpFix1), 0, 2);
            aBoxSizeValueItem.setValue(String.valueOf(tmpFix1 * aLengthConversionFactor), 0, 8);
        } else {
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew1), 0, 0);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew1 * aLengthConversionFactor), 0, 4);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew2), 0, 1);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew2 * aLengthConversionFactor), 0, 6);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew3), 0, 2);
            aBoxSizeValueItem.setValue(String.valueOf(tmpNew3 * aLengthConversionFactor), 0, 8);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Concentration as update notifier/receiver">
    /**
     * Checks concentration
     *
     * @param aConcentrationValueItem Concentration value item (may be changed)
     */
    private void checkConcentration(ValueItem aConcentrationValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aConcentrationValueItem == null || !aConcentrationValueItem.getName().equals("Concentration")) {
            return;
        }
        // </editor-fold>
        if (aConcentrationValueItem.getValue(0, 1).equals(ModelMessage.get("JdpdInputFile.parameter.weightPercent"))
            || aConcentrationValueItem.getValue(0, 1).equals(ModelMessage.get("JdpdInputFile.parameter.molarPercent"))
        ) {
            // <editor-fold defaultstate="collapsed" desc="Sum of values must be 100%">
            double tmpSum = 0;
            for (int i = 0; i < aConcentrationValueItem.getMatrixRowCount(); i++) {
                tmpSum += aConcentrationValueItem.getValueAsDouble(i, 2);
            }
            // IMPORTANT: Round tmpSum for comparison otherwise small roundoff errors may cause problems
            if (ModelUtils.roundDoubleValue(tmpSum, ModelDefinitions.CONCENTRATION_NUMBER_OF_DECIMALS) != 100.0) {
                aConcentrationValueItem.setError(String.format(ModelMessage.get("ValueItem.Error.WrongConcentration"), String.valueOf(tmpSum)));
            } else {
                aConcentrationValueItem.removeError();
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Sum of values is irrelevant">
            aConcentrationValueItem.removeError();
            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Quantity as update notifier/receiver">
    /**
     * Updates Quantity value item itself. BoxSize value item may be updated
     * too. NOTE: Update notification of any value item is suppressed.
     *
     * @param aConcentrationValueItem Value item Concentration (is NOT changed)
     * @param aQuantityValueItem Value item Quantity (may be changed)
     * @param aParticleTableValueItem Value item ParticleTable (is NOT
     * changed)
     * @param aMonomerTableValueItem Value item MonomerTable (is NOT changed)
     * @param aMoleculeTableValueItem Value itemMoleculeTable (is NOT changed)
     * @param aDensityValueItem Value item Density (is NOT changed)
     * @param aBoxSizeValueItem Value item BoxSize (may be changed)
     */
    private void updateQuantityItself(ValueItem aConcentrationValueItem, ValueItem aQuantityValueItem, ValueItem aParticleTableValueItem, ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem,
            ValueItem aDensityValueItem, ValueItem aBoxSizeValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return;
        }
        if (aConcentrationValueItem == null || !aConcentrationValueItem.getName().equals("Concentration") || aParticleTableValueItem == null
                || !aParticleTableValueItem.getName().equals("ParticleTable") || aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable") || aMoleculeTableValueItem == null
                || !aMoleculeTableValueItem.getName().equals("MoleculeTable") || aDensityValueItem == null || !aDensityValueItem.getName().equals("Density") || aBoxSizeValueItem == null
                || !aBoxSizeValueItem.getName().equals("BoxSize")) {
            aQuantityValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
            return;
        }
        if (aConcentrationValueItem.hasError() || aParticleTableValueItem.hasError() || aMonomerTableValueItem.hasError() || aMoleculeTableValueItem.hasError() || aDensityValueItem.hasError()
                || aBoxSizeValueItem.hasError()) {
            aQuantityValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check if last cloned matrix differs from current matrix">
        int tmpIndexOfRowChanged = -1;

        ValueItemMatrixElement[][] tmpLastClonedMatrix = aQuantityValueItem.getLastClonedMatrix();
        ValueItemMatrixElement[][] tmpCurrentMatrix = aQuantityValueItem.getMatrix();
        if (tmpLastClonedMatrix == null || tmpLastClonedMatrix.length != tmpCurrentMatrix.length) {
            tmpIndexOfRowChanged = tmpCurrentMatrix.length - 1;
        } else {
            for (int i = 0; i < tmpCurrentMatrix.length; i++) {
                if (!tmpCurrentMatrix[i][1].getFormattedValue().equals(tmpLastClonedMatrix[i][1].getFormattedValue())) {
                    tmpIndexOfRowChanged = i;
                    break;
                }
            }
        }
        if (tmpIndexOfRowChanged == -1) {
            // No change in values
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate new box size and quantities">
        double tmpNewQuantityOfChangedRow = aQuantityValueItem.getValueAsDouble(tmpIndexOfRowChanged, 1);
        double tmpLastQuantityOfChangedRow = -1.0;
        // NOTE: This loop iterates until values of aQuantityValueItem are self consistent (this is necessary due to roundoff errors to produce consistent results).
        //       tmpMaximumNumberOfTrials is necessary since iterative process may not converge but oscillate.
        int tmpMaximumNumberOfTrials = 100;
        for (int k = 0; k < tmpMaximumNumberOfTrials; k++) {
            if (tmpLastQuantityOfChangedRow == aQuantityValueItem.getValueAsDouble(tmpIndexOfRowChanged, 1)) {
                aQuantityValueItem.removeHint();
                return;
            } else {
                tmpLastQuantityOfChangedRow = aQuantityValueItem.getValueAsDouble(tmpIndexOfRowChanged, 1);
                aQuantityValueItem.setValue(String.valueOf(tmpNewQuantityOfChangedRow), tmpIndexOfRowChanged, 1);
            }
            int tmpTotalNumberOfParticlesOfSimulation = this.jobUtilityMethods.getTotalNumberOfParticlesOfSimulation(aMonomerTableValueItem, aMoleculeTableValueItem, aQuantityValueItem);
            double tmpNewVolume = (double) tmpTotalNumberOfParticlesOfSimulation / aDensityValueItem.getValueAsDouble();
            double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(aParticleTableValueItem, aMonomerTableValueItem, aMoleculeTableValueItem,
                    aQuantityValueItem, aDensityValueItem);
            if (tmpLengthConversionFactor == -1.0) {
                return;
            }
            this.updateBoxSizeWithNewVolume(aBoxSizeValueItem, tmpNewVolume, tmpLengthConversionFactor);
            if (aConcentrationValueItem.isActive()) {
                this.calculateQuantity(aConcentrationValueItem, aQuantityValueItem, aParticleTableValueItem, aMonomerTableValueItem, aMoleculeTableValueItem, aDensityValueItem, aBoxSizeValueItem);
            } else {
                // <editor-fold defaultstate="collapsed" desc="Remove possible error of quantity value item">
                if (aQuantityValueItem.hasError()) {
                    aQuantityValueItem.removeError();
                }

                // </editor-fold>
            }
        }
        aQuantityValueItem.setHint(ModelMessage.get("ValueItem.Hint.QuantityNotConverged"));
        // </editor-fold>
    }

    /**
     * Calculates quantities of value item Quantity. NOTE: Update notification
     * of any value item is suppressed.
     *
     * @param aConcentrationValueItem Value item Concentration (is NOT changed)
     * @param aQuantityValueItem Value item Quantity (may be changed)
     * @param aParticleTableValueItem Value item ParticleTable (is NOT changed)
     * @param aMonomerTableValueItem Value item MonomerTable (is NOT changed)
     * @param aMoleculeTableValueItem Value itemMoleculeTable (is NOT changed)
     * @param aDensityValueItem Value item Density (is NOT changed)
     * @param aBoxSizeValueItem Value item BoxSize (is NOT changed)
     */
    private void calculateQuantity(ValueItem aConcentrationValueItem, ValueItem aQuantityValueItem, ValueItem aParticleTableValueItem, ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem,
            ValueItem aDensityValueItem, ValueItem aBoxSizeValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return;
        }
        if (aConcentrationValueItem == null || !aConcentrationValueItem.getName().equals("Concentration") || aParticleTableValueItem == null
                || !aParticleTableValueItem.getName().equals("ParticleTable") || aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable") || aMoleculeTableValueItem == null
                || !aMoleculeTableValueItem.getName().equals("MoleculeTable") || aDensityValueItem == null || !aDensityValueItem.getName().equals("Density") || aBoxSizeValueItem == null
                || !aBoxSizeValueItem.getName().equals("BoxSize")) {
            aQuantityValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
            return;
        }
        if (aConcentrationValueItem.hasError() || aParticleTableValueItem.hasError() || aMonomerTableValueItem.hasError() || aMoleculeTableValueItem.hasError() || aDensityValueItem.hasError()
                || aBoxSizeValueItem.hasError()) {
            aQuantityValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
            return;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="1. Get density">
            double tmpDpdDensity = aDensityValueItem.getValueAsDouble();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="2. Calculate box volume">
            double tmpBoxVolume = aBoxSizeValueItem.getValueAsDouble(0, 0) * aBoxSizeValueItem.getValueAsDouble(0, 1) * aBoxSizeValueItem.getValueAsDouble(0, 2);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="3. Set particle container">
            ParticleInfoContainer tmpParticleInfoContainer = new ParticleInfoContainer();
            JobUtils.addToParticleInfoContainer(tmpParticleInfoContainer, aParticleTableValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="4. Replace monomer shortcuts">
            ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem;
            if (aMonomerTableValueItem.isActive()) {
                // First clone molecules value item
                tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem.getClone();
                // Second replace in cloned molecules value item
                this.jobUtilityMethods.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, aMonomerTableValueItem);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="5. Get concentration type">
            MoleculeConcentrationType tmpConcentrationType = MoleculeConcentrationType.toMoleculeConcentrationType(aConcentrationValueItem.getValue(0, 1));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="6. Set molecule container">
            MoleculeContainer tmpMoleculeContainer = new MoleculeContainer(tmpParticleInfoContainer, tmpBoxVolume, tmpDpdDensity, tmpConcentrationType);
            for (int i = 0; i < tmpMoleculesWithoutMonomerShortcutsValueItem.getMatrixRowCount(); i++) {
                Molecule tmpMolecule = new Molecule(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1), // Structure
                        tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0) // Molecule_Name
                );
                // NOTE: A Row in tmpMoleculesWithoutMonomerShortcutsValueItem corresponds to row in aConcentrationValueItem
                if (!tmpMoleculeContainer.addMolecule(tmpMolecule, // Molecule
                        aConcentrationValueItem.getValueAsDouble(i, 2) // Concentration
                )) {
                    aQuantityValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
                    return;
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="7. Calculate quantities">
            if (!tmpMoleculeContainer.calculateConcentrationProperties()) {
                aQuantityValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
                return;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="8. Set quantities">
            for (int i = 0; i < aQuantityValueItem.getMatrixRowCount(); i++) {
                int tmpValue = tmpMoleculeContainer.getMoleculeInfo(aQuantityValueItem.getValue(i, 0)).getNumberOfMolecules();
                if (tmpValue < 1) {
                    // Correct: There must at least be 1 molecule of any type
                    tmpValue = 1;
                }
                aQuantityValueItem.setValue(String.valueOf(tmpValue), i, 1);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="9. Remove possible error">
            if (aQuantityValueItem.hasError()) {
                aQuantityValueItem.removeError();
            }

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            aQuantityValueItem.setError(ModelMessage.get("ValueItem.Error.NoCalculation"));
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Compartments as update receiver">
    /**
     * Updates Compartments value item in specific way
     */
    private void updateCompartments(ValueItem aCompartmentsValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCompartmentsValueItem == null || !aCompartmentsValueItem.getName().equals("Compartments")) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get other value items">
        ValueItem tmpMonomerTableValueItem = aCompartmentsValueItem.getValueItemContainer().getValueItem("MonomerTable");
        ValueItem tmpMoleculeTableValueItem = aCompartmentsValueItem.getValueItemContainer().getValueItem("MoleculeTable");
        ValueItem tmpBoxSizeValueItem = aCompartmentsValueItem.getValueItemContainer().getValueItem("BoxSize");
        ValueItem tmpDensityValueItem = aCompartmentsValueItem.getValueItemContainer().getValueItem("Density");
        ValueItem tmpQuantityValueItem = aCompartmentsValueItem.getValueItemContainer().getValueItem("Quantity");
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check error and remove compartments">
        boolean tmpHasError = tmpMonomerTableValueItem.hasError() || tmpMoleculeTableValueItem.hasError() || tmpBoxSizeValueItem.hasError() || tmpDensityValueItem.hasError()
                || tmpQuantityValueItem.hasError();
        if (!tmpHasError) {
            CompartmentContainer tmpModifiedCompartmentContainer = aCompartmentsValueItem.getCompartmentContainer().getModifiedCompartmentContainer(aCompartmentsValueItem.getValueItemContainer());
            if (tmpModifiedCompartmentContainer == null) {
                aCompartmentsValueItem.setCompartmentContainer(new CompartmentContainer(aCompartmentsValueItem.getValueItemContainer()));
            } else {
                aCompartmentsValueItem.setCompartmentContainer(tmpModifiedCompartmentContainer);
            }
        }
        aCompartmentsValueItem.setActivity(!tmpHasError);
        aCompartmentsValueItem.setLocked(tmpHasError);
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Boundary as update receiver">
    /**
     * Update boundary value item itself (see code).
     * 
     * @param aBoundaryValueItem Boundary value item (may be changed)
     */
    private void updateBoundaryItself(ValueItem aBoundaryValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoundaryValueItem == null || !aBoundaryValueItem.getName().equals("MoleculeBoundary")) {
            return;
        }
        // </editor-fold>
        double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(aBoundaryValueItem.getValueItemContainer());
        for (int i = 0; i < aBoundaryValueItem.getMatrixRowCount(); i++) {
            aBoundaryValueItem.setValue(String.valueOf(aBoundaryValueItem.getValueAsDouble(i, 2) / tmpLengthConversionFactor), i, 3);
            aBoundaryValueItem.setValue(String.valueOf(aBoundaryValueItem.getValueAsDouble(i, 4) / tmpLengthConversionFactor), i, 5);
            
            aBoundaryValueItem.setValue(String.valueOf(aBoundaryValueItem.getValueAsDouble(i, 7) / tmpLengthConversionFactor), i, 8);
            aBoundaryValueItem.setValue(String.valueOf(aBoundaryValueItem.getValueAsDouble(i, 9) / tmpLengthConversionFactor), i, 10);
            
            aBoundaryValueItem.setValue(String.valueOf(aBoundaryValueItem.getValueAsDouble(i, 12) / tmpLengthConversionFactor), i, 13);
            aBoundaryValueItem.setValue(String.valueOf(aBoundaryValueItem.getValueAsDouble(i, 14) / tmpLengthConversionFactor), i, 15);
        }
    }
    
    /**
     * Updates boundary value item (see code)
     *
     * @param aBoundaryValueItem Boundary value item (may be changed)
     * @param aMoleculeTableValueItem Value itemMoleculeTable (is NOT changed)
     * @param aBoxSizeValueItem Box size value item (is NOT changed)
     */
    private void updateBoundary(
        ValueItem aBoundaryValueItem, 
        ValueItem aMoleculeTableValueItem, 
        ValueItem aBoxSizeValueItem
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoundaryValueItem == null || !aBoundaryValueItem.getName().equals("MoleculeBoundary")) {
            return;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return;
        }
        if (aBoxSizeValueItem == null || !aBoxSizeValueItem.getName().equals("BoxSize")) {
            return;
        }
        // </editor-fold>
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[aMoleculeTableValueItem.getMatrixRowCount()][];
        // <editor-fold defaultstate="collapsed" desc="ValueItemDataTypeFormat">
        ValueItemDataTypeFormat tmpActivityDataTypeFormat =
            new ValueItemDataTypeFormat(
                ModelMessage.get("JdpdInputFile.parameter.false"), 
                new String[] { 
                    ModelMessage.get("JdpdInputFile.parameter.true"),
                    ModelMessage.get("JdpdInputFile.parameter.false")
                }
            );
        double tmpBoundaryMinimum = 0.0;
        ValueItemDataTypeFormat tmpXminAngstromDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(tmpBoundaryMinimum),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 4)
            );
        ValueItemDataTypeFormat tmpXminDpdDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(tmpBoundaryMinimum),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 0),
                false,
                false
            );
        ValueItemDataTypeFormat tmpXmaxAngstromDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(aBoxSizeValueItem.getValueAsDouble(0, 4)),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 4)
            );
        ValueItemDataTypeFormat tmpXmaxDpdDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(aBoxSizeValueItem.getValueAsDouble(0, 0)),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 0),
                false,
                false
            );

        ValueItemDataTypeFormat tmpYminAngstromDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(tmpBoundaryMinimum),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 6)
            );
        ValueItemDataTypeFormat tmpYminDpdDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(tmpBoundaryMinimum),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 1),
                false,
                false
            );
        ValueItemDataTypeFormat tmpYmaxAngstromDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(aBoxSizeValueItem.getValueAsDouble(0, 6)),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 6)
            );
        ValueItemDataTypeFormat tmpYmaxDpdDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(aBoxSizeValueItem.getValueAsDouble(0, 1)),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 1),
                false,
                false
            );

        ValueItemDataTypeFormat tmpZminAngstromDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(tmpBoundaryMinimum),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 8)
            );
        ValueItemDataTypeFormat tmpZminDpdDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(tmpBoundaryMinimum),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 2),
                false,
                false
            );
        ValueItemDataTypeFormat tmpZmaxAngstromDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(aBoxSizeValueItem.getValueAsDouble(0, 8)),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 8)
            );
        ValueItemDataTypeFormat tmpZmaxDpdDataTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(aBoxSizeValueItem.getValueAsDouble(0, 2)),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                tmpBoundaryMinimum,
                aBoxSizeValueItem.getValueAsDouble(0, 2),
                false,
                false
            );
        // </editor-fold>
        for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
            ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[16];
            // Column 0: moleculeName
            tmpRow[0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(aMoleculeTableValueItem.getValue(i, 0), false));

            // Column 1: active
            tmpRow[1] = new ValueItemMatrixElement(tmpActivityDataTypeFormat);
            // Column 2: xMinAngstrom
            tmpRow[2] = new ValueItemMatrixElement(tmpXminAngstromDataTypeFormat);
            // Column 3: xMinDPD
            tmpRow[3] = new ValueItemMatrixElement(tmpXminDpdDataTypeFormat);
            // Column 4: xMaxAngstrom
            tmpRow[4] = new ValueItemMatrixElement(tmpXmaxAngstromDataTypeFormat);
            // Column 5: xMaxDPD
            tmpRow[5] = new ValueItemMatrixElement(tmpXmaxDpdDataTypeFormat);

            // Column 6: active
            tmpRow[6] = new ValueItemMatrixElement(tmpActivityDataTypeFormat);
            // Column 7: yMinAngstrom
            tmpRow[7] = new ValueItemMatrixElement(tmpYminAngstromDataTypeFormat);
            // Column 8: yMinDPD
            tmpRow[8] = new ValueItemMatrixElement(tmpYminDpdDataTypeFormat);
            // Column 9: yMaxAngstrom
            tmpRow[9] = new ValueItemMatrixElement(tmpYmaxAngstromDataTypeFormat);
            // Column 10: yMaxDPD
            tmpRow[10] = new ValueItemMatrixElement(tmpYmaxDpdDataTypeFormat);

            // Column 11: active
            tmpRow[11] = new ValueItemMatrixElement(tmpActivityDataTypeFormat);
            // Column 12: yMinAngstrom
            tmpRow[12] = new ValueItemMatrixElement(tmpZminAngstromDataTypeFormat);
            // Column 13: yMinDPD
            tmpRow[13] = new ValueItemMatrixElement(tmpZminDpdDataTypeFormat);
            // Column 14: yMaxAngstrom
            tmpRow[14] = new ValueItemMatrixElement(tmpZmaxAngstromDataTypeFormat);
            // Column 15: yMaxDPD
            tmpRow[15] = new ValueItemMatrixElement(tmpZmaxDpdDataTypeFormat);
            
            tmpMatrix[i] = tmpRow;
        }
        aBoundaryValueItem.setMatrix(tmpMatrix);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Bonds12Table as update receiver">
    /**
     * Updates particle pairs in Bonds12Table value item
     *
     * @param aParticleTableValueItem Particle matrix value item (is NOT changed)
     * @param aBonds12TableValueItem Receiver value item (may be changed)
     */
    private void updateParticlePairsInBonds12(ValueItem aParticleTableValueItem, ValueItem aBonds12TableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (aBonds12TableValueItem == null || !aBonds12TableValueItem.getName().equals("Bonds12Table")) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create particle pairs">
        LinkedList<String> tmpParticlePairList = new LinkedList<String>();
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            for (int k = 0; k <= i; k++) {
                tmpParticlePairList.add(aParticleTableValueItem.getValue(i, 0) + SpicesConstants.PARTICLE_SEPARATOR + aParticleTableValueItem.getValue(k, 0));
            }
        }
        Collections.sort(tmpParticlePairList);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check if update is necessary">
        boolean tmpIsUpdateNecessary = false;
        if (tmpParticlePairList.size() != aBonds12TableValueItem.getMatrixRowCount()) {
            tmpIsUpdateNecessary = true;
        } else {
            int tmpIndex = 0;
            for (String tmpSinglePair : tmpParticlePairList) {
                if (!tmpSinglePair.equals(aBonds12TableValueItem.getValue(tmpIndex++, 0))) {
                    tmpIsUpdateNecessary = true;
                    break;
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Perform update if necessary">
        if (tmpIsUpdateNecessary) {
            ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[tmpParticlePairList.size()][];
            int tmpIndex = 0;
            for (String tmpSinglePair : tmpParticlePairList) {
                boolean tmpIsMatch = false;
                for (int k = 0; k < aBonds12TableValueItem.getMatrixRowCount(); k++) {
                    if (tmpSinglePair.equals(aBonds12TableValueItem.getValue(k, 0))) {
                        tmpNewMatrix[tmpIndex] = aBonds12TableValueItem.getMatrix()[k];
                        tmpIsMatch = true;
                        break;
                    }
                }
                if (!tmpIsMatch) {
                    tmpNewMatrix[tmpIndex] = aBonds12TableValueItem.getDefaultMatrixElementRow();
                    tmpNewMatrix[tmpIndex][0].setValue(tmpSinglePair);
                    // Set bond length to 1.0 DPD units
                    tmpNewMatrix[tmpIndex][1].setValue("1.000");
                }
                tmpIndex++;
            }
            aBonds12TableValueItem.setMatrix(tmpNewMatrix);
        }
        // </editor-fold>
    }
    
    /**
     * Updates volume-based bond length defaults in Bonds12Table value item
     * 
     * @param aBonds12TableValueItem Bonds12Table value item (may be changed)
     */
    private void updateVolumeBasedBondLengthsInBonds12(ValueItem aBonds12TableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBonds12TableValueItem == null || !aBonds12TableValueItem.getName().equals("Bonds12Table")) {
            return;
        }
        // </editor-fold>
        double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(aBonds12TableValueItem.getValueItemContainer());
        if (tmpLengthConversionFactor == -1.0) {
            return;
        }
        for (int i = 0; i < aBonds12TableValueItem.getMatrixRowCount(); i++) {
            String[] tmpParticles = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(aBonds12TableValueItem.getValue(i, 0));
            double tmpVolumeBasedBondLengthInAngstrom = StandardParticleInteractionData.getInstance().getParticlePairVolumeBasedBondLength(tmpParticles[0], tmpParticles[1]);
            double tmpVolumeBasedBondLengthInDpd = tmpVolumeBasedBondLengthInAngstrom / tmpLengthConversionFactor;
            if (tmpVolumeBasedBondLengthInDpd < Preferences.getInstance().getMinimumBondLengthDpd()) {
                // Set bond length to minimum bond length in DPD units
                aBonds12TableValueItem.setValue(String.valueOf(Preferences.getInstance().getMinimumBondLengthDpd()), i, 1);
                aBonds12TableValueItem.getTypeFormat(i, 1).setDefaultValue(String.valueOf(Preferences.getInstance().getMinimumBondLengthDpd()));
            } else {
                // Set bond length to volume-based bond length
                aBonds12TableValueItem.setValue(String.valueOf(tmpVolumeBasedBondLengthInDpd), i, 1);
                aBonds12TableValueItem.getTypeFormat(i, 1).setDefaultValue(String.valueOf(tmpVolumeBasedBondLengthInDpd));
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- MoleculeBackboneForces as update receiver">
    /**
     * Updates MoleculeBackboneForces value item 
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density 
     * value item (value items are NOT changed) as well as
     * MoleculeBackboneForces value item (this may be
     * changed)
     */
    private void updateMoleculeBackboneForces(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return;
        }
        ValueItem tmpMoleculeBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("MoleculeBackboneForces");
        if (tmpMoleculeBackboneForcesValueItem == null) {
            return;
        }
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        if (tmpMoleculeTableValueItem == null) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check if molecule with backbone attributes exists">
        boolean tmpHasMoleculeWithBackboneForces = false;
        if (tmpMoleculeTableValueItem.getMatrixRowCount() > 1) {
            for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
                String tmpMolecularStructureString = tmpMoleculeTableValueItem.getValue(i, 1);
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                tmpHasMoleculeWithBackboneForces = tmpSpices.hasBackboneParticle();
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                if (tmpHasMoleculeWithBackboneForces) {
                    break;
                }
            }
        }
        // </editor-fold>
        if (tmpHasMoleculeWithBackboneForces) {
            // <editor-fold defaultstate="collapsed" desc="Molecules with backbone attributes exist">
            String tmpDefaultMoleculeName = null;
            int tmpMaxBackboneIndex = -1;
            LinkedList<String> tmpMoleculeNameList = new LinkedList<>();
            for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
                String tmpMolecularStructureString = tmpMoleculeTableValueItem.getValue(i, 1);
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                if (tmpSpices.hasBackboneParticle()) {
                    if (tmpMoleculeNameList.isEmpty()) {
                        tmpDefaultMoleculeName = tmpMoleculeTableValueItem.getValue(i, 0);
                        tmpMaxBackboneIndex = tmpSpices.getMaxBackboneIndex();
                    }
                    tmpMoleculeNameList.add(tmpMoleculeTableValueItem.getValue(i, 0));
                }
                // Set Spices for re-use
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            }
            String[] tmpMoleculeNames = tmpMoleculeNameList.toArray(new String[0]);
            // NOTE: First backbone index starts with 1 but does NOT contain the last element
            String[] tmpBackboneAttributes1 = this.miscUtilityMethods.removeLastElementFromStringArray(this.miscUtilityMethods.getIndexStringArray(tmpMaxBackboneIndex));
            // NOTE: tmpBackboneAttributes2 contains all attributes except the first default one (1)
            String[] tmpBackboneAttributes2 = this.miscUtilityMethods.removeFirstElementFromStringArray(this.miscUtilityMethods.getIndexStringArray(tmpMaxBackboneIndex));
            ValueItemDataTypeFormat[] tmpDefaultTypeFormats =
                new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(tmpDefaultMoleculeName, tmpMoleculeNames),          // moleculeName
                    new ValueItemDataTypeFormat(tmpBackboneAttributes1[0], tmpBackboneAttributes1), // backboneAttribute1
                    new ValueItemDataTypeFormat(tmpBackboneAttributes2[0], tmpBackboneAttributes2), // backboneAttribute2
                    new ValueItemDataTypeFormat("0", 6, 0.0, Double.MAX_VALUE),                     // backboneDistanceAngstrom
                    new ValueItemDataTypeFormat("0", 6, 0.0, Double.MAX_VALUE, false, false),       // backboneDistanceDpd
                    new ValueItemDataTypeFormat("0", 6, 0.0, Double.MAX_VALUE)                      // backboneForceConstant
                };
            tmpMoleculeBackboneForcesValueItem.setDefaultTypeFormats(tmpDefaultTypeFormats);
            // Recalculate DPD-length (since Spices may be re-used):
            this.updateMoleculeBackboneForcesDpdLength(tmpMoleculeTableValueItem.getValueItemContainer());
            // </editor-fold>
            tmpMoleculeBackboneForcesValueItem.setLocked(false);
            tmpMoleculeBackboneForcesValueItem.setActivity(true);
        } else {
            tmpMoleculeBackboneForcesValueItem.setLocked(true);
        }
    }

    /**
     * Updates MoleculeBackboneForces value item itself
     * 
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density 
     * value item (value items are NOT changed) as well as
     * MoleculeBackboneForces value item (this may be
     * changed)
     */
    private void updateMoleculeBackboneForcesItself(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return;
        }
        ValueItem tmpMoleculeBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("MoleculeBackboneForces");
        if (tmpMoleculeBackboneForcesValueItem == null) {
            return;
        }
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        if (tmpMoleculeTableValueItem == null) {
            return;
        }
        // </editor-fold>
        HashMap<String, String> tmpMoleculeNameToMolecularStructureStringMap = new HashMap<>(tmpMoleculeTableValueItem.getMatrixRowCount());
        for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
            tmpMoleculeNameToMolecularStructureStringMap.put(tmpMoleculeTableValueItem.getValue(i, 0), tmpMoleculeTableValueItem.getValue(i, 1));
        }
        boolean tmpIsHint = false;
        for (int i = 0; i < tmpMoleculeBackboneForcesValueItem.getMatrixRowCount(); i++) {
            String tmpMoleculeName = tmpMoleculeBackboneForcesValueItem.getValue(i, 0);
            String tmpMolecularStructureString = tmpMoleculeNameToMolecularStructureStringMap.get(tmpMoleculeName);
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
            int tmpMaxBackboneIndex = tmpSpices.getMaxBackboneIndex();
            String[] tmpBackboneAttributes1 = this.miscUtilityMethods.removeLastElementFromStringArray(this.miscUtilityMethods.getIndexStringArray(tmpMaxBackboneIndex));
            // Check backbone attribute 1
            if (tmpMoleculeBackboneForcesValueItem.getValueAsInt(i, 1) > tmpMaxBackboneIndex - 1
                    || tmpMoleculeBackboneForcesValueItem.getMatrix()[i][1].getTypeFormat().getSelectionTexts().length != tmpMaxBackboneIndex - 1) {
                tmpMoleculeBackboneForcesValueItem.setValue(tmpBackboneAttributes1[0], i, 1);
                tmpMoleculeBackboneForcesValueItem.getMatrix()[i][1].getTypeFormat().setSelectionTexts(tmpBackboneAttributes1);
            }
            // Check backbone attribute 2
            String[] tmpBackboneAttributes2 = this.miscUtilityMethods.removeUpToElementFromStringArray(this.miscUtilityMethods.getIndexStringArray(tmpMaxBackboneIndex), tmpMoleculeBackboneForcesValueItem.getValue(i, 1));
            if (!this.miscUtilityMethods.areStringArraysEqual(tmpBackboneAttributes2, tmpMoleculeBackboneForcesValueItem.getMatrix()[i][2].getTypeFormat().getSelectionTexts())) {
                tmpMoleculeBackboneForcesValueItem.getMatrix()[i][2].getTypeFormat().setSelectionTexts(tmpBackboneAttributes2);
                tmpMoleculeBackboneForcesValueItem.getMatrix()[i][2].getTypeFormat().setDefaultValue(tmpBackboneAttributes2[0]);
                tmpMoleculeBackboneForcesValueItem.setValue(String.valueOf(tmpMoleculeBackboneForcesValueItem.getValueAsInt(i, 1) + 1), i, 2);
            }
            if (tmpMoleculeBackboneForcesValueItem.getValueAsInt(i, 2) <= tmpMoleculeBackboneForcesValueItem.getValueAsInt(i, 1)
                    || tmpMoleculeBackboneForcesValueItem.getValueAsInt(i, 2) > tmpMaxBackboneIndex) {
                tmpMoleculeBackboneForcesValueItem.setValue(String.valueOf(tmpMoleculeBackboneForcesValueItem.getValueAsInt(i, 1) + 1), i, 2);
            }
            // Check hint
            if (tmpMoleculeBackboneForcesValueItem.getValueAsDouble(i, 3) == 0.0 && tmpMoleculeBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                tmpIsHint = true;
            }
            // Set Spices for re-use
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        }
        // Update DPD lengths
        this.updateMoleculeBackboneForcesDpdLength(aJobInputValueItemContainer);
        // Set hint if force constant != 0 but distance = 0
        if (tmpIsHint) {
            tmpMoleculeBackboneForcesValueItem.setHint(ModelMessage.get("ValueItem.Hint.ZeroDistance"));
        } else {
            tmpMoleculeBackboneForcesValueItem.removeHint();
        }
    }
    
    /**
     * Updates DPD lengths for molecule backbone forces value item
     * 
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density 
     * value item (value items are NOT changed) as well as
     * MoleculeBackboneForces value item (this may be
     * changed)
     */
    private void updateMoleculeBackboneForcesDpdLength(ValueItemContainer aJobInputValueItemContainer) {
        if (aJobInputValueItemContainer != null) {
            double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
            if (tmpLengthConversionFactor != -1.0) {
                ValueItem tmpMoleculeBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("MoleculeBackboneForces");
                if (tmpMoleculeBackboneForcesValueItem == null) {
                    return;
                }
                this.updateMoleculeBackboneForcesDpdLength(tmpMoleculeBackboneForcesValueItem, tmpLengthConversionFactor);
            }
        }
    }
    
    /**
     * Updates DPD lengths for molecule backbone forces value item
     * 
     * @param aMoleculeBackboneForcesValueItem MoleculeBackboneForces value item (may be changed)
     * @param aLengthConversionFactor Length conversion factor from JobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength()
     */
    private void updateMoleculeBackboneForcesDpdLength(ValueItem aMoleculeBackboneForcesValueItem, double aLengthConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeBackboneForcesValueItem == null || !aMoleculeBackboneForcesValueItem.getName().equals("MoleculeBackboneForces")) {
            return;
        }
        if (aLengthConversionFactor <= 0.0) {
            return;
        }
        // </editor-fold>
        // Molecule backbone force value item columns:
        // Index 0 = Molecule name
        // Index 1 = Backbone attribute 1
        // Index 2 = Backbone attribute 2
        // Index 3 = Backbone distance in Angstrom
        // Index 4 = Backbone distance in DPD unit
        // Index 5 = Backbone force constant
        for (int i = 0; i < aMoleculeBackboneForcesValueItem.getMatrixRowCount(); i++) {
            double tmpLengthInAngstrom = aMoleculeBackboneForcesValueItem.getValueAsDouble(i, 3);
            double tmpLengthInDpd = tmpLengthInAngstrom/aLengthConversionFactor;
            aMoleculeBackboneForcesValueItem.setValue(String.valueOf(tmpLengthInDpd), i, 4);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- ProteinBackboneForces as update receiver">
    /**
     * Updates ProteinBackboneForces value item 
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density 
     * value item (value items are NOT changed) as well as
     * ProteinBackboneForces value item (this may be
     * changed)
     * @throws DpdPeptideException Thrown if exception in PdbToDpd occurs
     */
    private void updateProteinBackboneForces(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return;
        }
        ValueItem tmpProteinBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinBackboneForces");
        if (tmpProteinBackboneForcesValueItem == null) {
            return;
        }
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        if (tmpMoleculeTableValueItem == null) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check if protein exists">
        boolean tmpHasProteinData = false;
        if (tmpMoleculeTableValueItem.getMatrixRowCount() > 1) {
            for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
                if (tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    tmpHasProteinData = true;
                    break;
                }
            }
        }
        // </editor-fold>
        if (tmpHasProteinData) {
            // <editor-fold defaultstate="collapsed" desc="Proteins exist">
            HashMap<String, String> tmpProteinNameToProteinDataMap = new HashMap<>(tmpMoleculeTableValueItem.getMatrixRowCount());
            HashMap<String, String[]> tmpProteinNameToIndexedCAlphaKeysMap = new HashMap<>(tmpMoleculeTableValueItem.getMatrixRowCount());
            String tmpDefaultProteinName = null;
            LinkedList<String> tmpProteinNameList = new LinkedList<>();
            for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
                if (tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    String tmpProteinName = tmpMoleculeTableValueItem.getValue(i, 0);
                    String tmpProteinData = tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                    if (tmpProteinNameList.isEmpty()) {
                        tmpDefaultProteinName = tmpProteinName;
                    }
                    tmpProteinNameToProteinDataMap.put(tmpProteinName, tmpProteinData);
                    PdbToDpd tmpPdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                    String[] tmpIndexedCAlphaKeys = tmpPdbToDpd.getIndexedCAlphaKeys();
                    PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                    tmpProteinNameToIndexedCAlphaKeysMap.put(tmpProteinName, tmpIndexedCAlphaKeys);
                    tmpProteinNameList.add(tmpProteinName);
                }
            }
            String[] tmpProteinNames = tmpProteinNameList.toArray(new String[0]);
            // NOTE: tmpAminoAcidBackboneParticleArray1 contains all amino acid backbone particles except the last one
            String[] tmpAminoAcidBackboneParticleArray1 = this.miscUtilityMethods.removeLastElementFromStringArray(tmpProteinNameToIndexedCAlphaKeysMap.get(tmpProteinNames[0]));
            // NOTE: tmpAminoAcidBackboneParticleArray2 contains all amino acid backbone particles except the first default one
            String[] tmpAminoAcidBackboneParticleArray2 = this.miscUtilityMethods.removeFirstElementFromStringArray(tmpProteinNameToIndexedCAlphaKeysMap.get(tmpProteinNames[0]));
            ValueItemDataTypeFormat[] tmpDefaultTypeFormats =
                new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(tmpDefaultProteinName, tmpProteinNames),                                    // proteinName
                    new ValueItemDataTypeFormat(tmpAminoAcidBackboneParticleArray1[0], tmpAminoAcidBackboneParticleArray1), // aminoAcidBackboneParticle1
                    new ValueItemDataTypeFormat(tmpAminoAcidBackboneParticleArray2[0], tmpAminoAcidBackboneParticleArray2), // aminoAcidBackboneParticle2
                    new ValueItemDataTypeFormat("0", 6, 0.0, Double.MAX_VALUE),                                             // backboneDistanceAngstrom
                    new ValueItemDataTypeFormat("0", 6, 0.0, Double.MAX_VALUE, false, false),                               // backboneDistanceDpd
                    new ValueItemDataTypeFormat("0", 6, 0.0, Double.MAX_VALUE)                                              // backboneForceConstant
                };
            tmpProteinBackboneForcesValueItem.setDefaultTypeFormats(tmpDefaultTypeFormats);
            // Recalculate DPD-length
            this.updateProteinBackboneForcesDpdLength(tmpMoleculeTableValueItem.getValueItemContainer());
            // </editor-fold>
            tmpProteinBackboneForcesValueItem.setLocked(false);
            tmpProteinBackboneForcesValueItem.setActivity(true);
        } else {
            tmpProteinBackboneForcesValueItem.setLocked(true);
        }
    }

    /**
     * Updates ProteinBackboneForces value item itself
     * 
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density 
     * value item (value items are NOT changed) as well as
     * ProteinBackboneForces value item (this may be
     * changed)
     */
    private void updateProteinBackboneForcesItself(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return;
        }
        ValueItem tmpProteinBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinBackboneForces");
        if (tmpProteinBackboneForcesValueItem == null) {
            return;
        }
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        if (tmpMoleculeTableValueItem == null) {
            return;
        }
        // </editor-fold>
        HashMap<String, String[]> tmpProteinNameToIndexedCAlphaKeysMap = new HashMap<>(tmpMoleculeTableValueItem.getMatrixRowCount());
        for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
            if (tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                String tmpProteinName = tmpMoleculeTableValueItem.getValue(i, 0);
                String tmpProteinData = tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                PdbToDpd tmpPdbToDPD = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                String[] tmpIndexedCAlphaKeys = tmpPdbToDPD.getIndexedCAlphaKeys();
                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDPD, tmpProteinData);
                tmpProteinNameToIndexedCAlphaKeysMap.put(tmpProteinName, tmpIndexedCAlphaKeys);
            }
        }
        boolean tmpIsHint = false;
        for (int i = 0; i < tmpProteinBackboneForcesValueItem.getMatrixRowCount(); i++) {
            String tmpProteinName = tmpProteinBackboneForcesValueItem.getValue(i, 0);
            String[] tmpAminoAcidBackboneParticleArray1 = this.miscUtilityMethods.removeLastElementFromStringArray(tmpProteinNameToIndexedCAlphaKeysMap.get(tmpProteinName));
            // Check amino acid backbone particle 1
            if (!this.miscUtilityMethods.containsElement(tmpAminoAcidBackboneParticleArray1, tmpProteinBackboneForcesValueItem.getValue(i, 1))) {
                tmpProteinBackboneForcesValueItem.setValue(tmpAminoAcidBackboneParticleArray1[0], i, 1);
                tmpProteinBackboneForcesValueItem.getMatrix()[i][1].getTypeFormat().setSelectionTexts(tmpAminoAcidBackboneParticleArray1);
            }
            // Check amino acid backbone particle 2
            // NOTE: tmpAminoAcidBackboneParticleArray2 contains all amino acid backbone particles upper the one chosen from tmpAminoAcidBackboneParticleArray1
            String[] tmpAminoAcidBackboneParticleArray2 = this.miscUtilityMethods.removeUpToElementFromStringArray(tmpProteinNameToIndexedCAlphaKeysMap.get(tmpProteinName), tmpProteinBackboneForcesValueItem.getValue(i, 1));
            if (!this.miscUtilityMethods.areStringArraysEqual(tmpAminoAcidBackboneParticleArray2, tmpProteinBackboneForcesValueItem.getMatrix()[i][2].getTypeFormat().getSelectionTexts())) {
                tmpProteinBackboneForcesValueItem.getMatrix()[i][2].getTypeFormat().setSelectionTexts(tmpAminoAcidBackboneParticleArray2);
                tmpProteinBackboneForcesValueItem.getMatrix()[i][2].getTypeFormat().setDefaultValue(tmpAminoAcidBackboneParticleArray2[0]);
                tmpProteinBackboneForcesValueItem.setValue(tmpAminoAcidBackboneParticleArray2[0], i, 2);
            }
            if (tmpProteinBackboneForcesValueItem.getValue(i, 2).equals(tmpProteinBackboneForcesValueItem.getValue(i, 1))
                    || !this.miscUtilityMethods.containsElement(tmpAminoAcidBackboneParticleArray2, tmpProteinBackboneForcesValueItem.getValue(i, 2))) {
                tmpProteinBackboneForcesValueItem.setValue(tmpAminoAcidBackboneParticleArray2[0], i, 2);
            }
            // Check hint
            if (tmpProteinBackboneForcesValueItem.getValueAsDouble(i, 3) == 0.0 && tmpProteinBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                tmpIsHint = true;
            }
        }
        // Update DPD lengths
        this.updateProteinBackboneForcesDpdLength(aJobInputValueItemContainer);
        // Set hint if force constant != 0 but distance = 0
        if (tmpIsHint) {
            tmpProteinBackboneForcesValueItem.setHint(ModelMessage.get("ValueItem.Hint.ZeroDistance"));
        } else {
            tmpProteinBackboneForcesValueItem.removeHint();
        }
    }
    
    /**
     * Updates DPD lengths for protein backbone forces value item
     * 
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density 
     * value item (value items are NOT changed) as well as
     * ProteinBackboneForces value item (this may be
     * changed)
     */
    private void updateProteinBackboneForcesDpdLength(ValueItemContainer aJobInputValueItemContainer) {
        if (aJobInputValueItemContainer != null) {
            double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
            if (tmpLengthConversionFactor != -1.0) {
                ValueItem tmpProteinBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinBackboneForces");
                if (tmpProteinBackboneForcesValueItem == null) {
                    return;
                }
                this.updateProteinBackboneForcesDpdLength(tmpProteinBackboneForcesValueItem, tmpLengthConversionFactor);
            }
        }
    }
    
    /**
     * Updates DPD lengths for protein backbone forces value item
     * 
     * @param aProteinBackboneForcesValueItem ProteinBackboneForces value item (may be changed)
     * @param aLengthConversionFactor Length conversion factor from JobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength()
     */
    private void updateProteinBackboneForcesDpdLength(ValueItem aProteinBackboneForcesValueItem, double aLengthConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aProteinBackboneForcesValueItem == null || !aProteinBackboneForcesValueItem.getName().equals("ProteinBackboneForces")) {
            return;
        }
        if (aLengthConversionFactor <= 0.0) {
            return;
        }
        // </editor-fold>
        // Protein backbone force value item columns:
        // Index 0 = Protein name
        // Index 1 = Amino acid backbone particle 1
        // Index 2 = Amino acid backbone particle 2
        // Index 3 = Backbone distance in Angstrom
        // Index 4 = Backbone distance in DPD unit
        // Index 5 = Backbone force constant
        for (int i = 0; i < aProteinBackboneForcesValueItem.getMatrixRowCount(); i++) {
            double tmpLengthInAngstrom = aProteinBackboneForcesValueItem.getValueAsDouble(i, 3);
            double tmpLengthInDpd = tmpLengthInAngstrom/aLengthConversionFactor;
            aProteinBackboneForcesValueItem.setValue(String.valueOf(tmpLengthInDpd), i, 4);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- TimeStepNumber and TimeStepLength as update receiver">
    /**
     * Updates physical time periods in value items TimeStepNumber and
     * TimeStepLength
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity, Density and
     * Temperature value item (value items are NOT changed) as well as
     * TimeStepNumber and TimeStepLength value item (these may be
     * changed)
     */
    private void updatePhysicalTimePeriods(ValueItemContainer aJobInputValueItemContainer) {
        if (aJobInputValueItemContainer != null) {
            double tmpTimeConversionFactor = this.jobUtilityMethods.getTimeConversionFactorFromDpdToPhysicalTime(aJobInputValueItemContainer);
            if (tmpTimeConversionFactor != -1.0) {
                ValueItem tmpTimeStepNumberValueItem = aJobInputValueItemContainer.getValueItem("TimeStepNumber");
                if (tmpTimeStepNumberValueItem == null) {
                    return;
                }
                ValueItem tmpTimeStepLengthValueItem = aJobInputValueItemContainer.getValueItem("TimeStepLength");
                if (tmpTimeStepLengthValueItem == null) {
                    return;
                }
                this.updatePhysicalTimePeriods(tmpTimeStepNumberValueItem, tmpTimeStepLengthValueItem, tmpTimeConversionFactor);
            }
        }
    }
    
    /**
     * Updates physical time periods in value items TimeStepNumber and
     * TimeStepLength
     * 
     * @param aTimeStepNumberValueItem TimeStepNumber value item (may be changed)
     * @param aTimeStepLengthValueItem TimeStepLength value item (may be changed)
     * @param aTimeConversionFactor Time conversion factor from JobUtilityMethods.getTimeConversionFactorFromDpdToPhysicalTime()
     */
    private void updatePhysicalTimePeriods(ValueItem aTimeStepNumberValueItem, ValueItem aTimeStepLengthValueItem, double aTimeConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTimeStepNumberValueItem == null || !aTimeStepNumberValueItem.getName().equals("TimeStepNumber")) {
            return;
        }
        if (aTimeStepLengthValueItem == null || !aTimeStepLengthValueItem.getName().equals("TimeStepLength")) {
            return;
        }
        if (aTimeConversionFactor <= 0.0) {
            return;
        }
        // </editor-fold>
        double tmpStepInNanoseconds = aTimeStepLengthValueItem.getValueAsDouble(0, 0) * aTimeConversionFactor;
        double tmpSimulationInNanoseconds = tmpStepInNanoseconds * aTimeStepNumberValueItem.getValueAsDouble(0, 0);

        aTimeStepNumberValueItem.setValue(String.valueOf(tmpStepInNanoseconds), 0, 1);
        aTimeStepNumberValueItem.setValue(String.valueOf(tmpSimulationInNanoseconds), 0, 2);

        aTimeStepLengthValueItem.setValue(String.valueOf(tmpStepInNanoseconds), 0, 1);
        aTimeStepLengthValueItem.setValue(String.valueOf(tmpSimulationInNanoseconds), 0, 2);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- ParticleNumber as update receiver">
    private void updateParticleNumber(ValueItemContainer aJobInputValueItemContainer) {
        if (aJobInputValueItemContainer != null) {
            int tmpTotalNumberOfParticlesInSimulation = this.jobUtilityMethods.getTotalNumberOfParticlesInSimulation(aJobInputValueItemContainer);
            ValueItem tmpParticleNumberValueItem = aJobInputValueItemContainer.getValueItem("ParticleNumber");
            if (tmpParticleNumberValueItem == null) {
                return;
            }
            tmpParticleNumberValueItem.setValue(String.valueOf(tmpTotalNumberOfParticlesInSimulation));
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- IntegrationType as update notifier/receiver">
    /**
     * Updates IntegrationType value item itself (see code).
     *
     * @param anIntegrationTypeValueItem IntegrationType value item (may be changed)
     */
    private void updateIntegrationTypeItself(ValueItem anIntegrationTypeValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIntegrationTypeValueItem == null || !anIntegrationTypeValueItem.getName().equals("IntegrationType")) {
            return;
        }
        // </editor-fold>
        ValueItemMatrixElement[][] tmpLastClonedMatrix = anIntegrationTypeValueItem.getLastClonedMatrix();
        String tmpLastIntegrationType = tmpLastClonedMatrix[0][0].getValue();
        if (!tmpLastIntegrationType.equals(anIntegrationTypeValueItem.getValue(0, 0))) {
            if (anIntegrationTypeValueItem.getValue(0, 0).equals(Factory.IntegrationType.GWMVV.toString())) {
                // <editor-fold defaultstate="collapsed" desc="GWMVV">
                anIntegrationTypeValueItem.getMatrix()[0][1].setTypeFormat(new ValueItemDataTypeFormat("0.65", 2, 0.0, 1.0)); // Parameter1
                anIntegrationTypeValueItem.getMatrix()[0][1].setValue("0.65");
                anIntegrationTypeValueItem.getMatrix()[0][2].setTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.none"), false)); // Parameter2
                anIntegrationTypeValueItem.getMatrix()[0][2].setValue(ModelMessage.get("JdpdInputFile.parameter.none"));
                // </editor-fold>
            } else if (anIntegrationTypeValueItem.getValue(0, 0).equals(Factory.IntegrationType.SCMVV.toString())) {
                // <editor-fold defaultstate="collapsed" desc="SCMVV">
                anIntegrationTypeValueItem.getMatrix()[0][1].setTypeFormat(new ValueItemDataTypeFormat("5", 0, 1, Double.POSITIVE_INFINITY)); // Parameter1
                anIntegrationTypeValueItem.getMatrix()[0][1].setValue("5");
                anIntegrationTypeValueItem.getMatrix()[0][2].setTypeFormat(new ValueItemDataTypeFormat(
                        new String[] {
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    )
                ); // Parameter2
                anIntegrationTypeValueItem.getMatrix()[0][2].setValue(ModelMessage.get("JdpdInputFile.parameter.true"));
                // </editor-fold>
            } else if (anIntegrationTypeValueItem.getValue(0, 0).equals(Factory.IntegrationType.S1MVV.toString())) {
                // <editor-fold defaultstate="collapsed" desc="S1MVV">
                anIntegrationTypeValueItem.getMatrix()[0][1].setTypeFormat(new ValueItemDataTypeFormat(
                        new String[] {
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    )
                ); // Parameter1
                anIntegrationTypeValueItem.getMatrix()[0][1].setValue(ModelMessage.get("JdpdInputFile.parameter.true"));
                anIntegrationTypeValueItem.getMatrix()[0][2].setTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("JdpdInputFile.parameter.none"), false)); // Parameter2
                anIntegrationTypeValueItem.getMatrix()[0][2].setValue(ModelMessage.get("JdpdInputFile.parameter.none"));
                // </editor-fold>
            } else if (anIntegrationTypeValueItem.getValue(0, 0).equals(Factory.IntegrationType.PNHLN.toString())) {
                // <editor-fold defaultstate="collapsed" desc="PNHLN">
                anIntegrationTypeValueItem.getMatrix()[0][1].setTypeFormat(new ValueItemDataTypeFormat("100.000", 3, 0.001, Double.POSITIVE_INFINITY)); // Parameter1
                anIntegrationTypeValueItem.getMatrix()[0][1].setValue("100.000");
                anIntegrationTypeValueItem.getMatrix()[0][2].setTypeFormat(new ValueItemDataTypeFormat(
                        new String[] {
                            ModelMessage.get("JdpdInputFile.parameter.true"),
                            ModelMessage.get("JdpdInputFile.parameter.false")
                        }
                    )
                ); // Parameter2
                anIntegrationTypeValueItem.getMatrix()[0][2].setValue(ModelMessage.get("JdpdInputFile.parameter.true"));
                // </editor-fold>
            }
        }
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns incompatible particles.
     *
     * @param aParticleTableValueItem Value item ParticleTable (is NOT changed)
     * @param aTemperatureValueItem  Value item Temperature (is NOT changed)
     * @return Incompatible particles or null if no particle is incompatible
     */
    private String getIncompatibleParticles(ValueItem aParticleTableValueItem, ValueItem aTemperatureValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return null;
        }
        if (aTemperatureValueItem == null || !aTemperatureValueItem.getName().equals("Temperature")) {
            return null;
        }
        // </editor-fold>
        StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_TINY);
        String tmpTemperatureRepresentation = aTemperatureValueItem.getValue();
        if (!StandardParticleInteractionData.getInstance().hasTemperature(tmpTemperatureRepresentation)) {
            for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
                String tmpParticle = aParticleTableValueItem.getValue(i, 0);
                StandardParticleDescription tmpParticleData = StandardParticleInteractionData.getInstance().getParticleDescription(tmpParticle);
                if (tmpParticleData == null) {
                    if (tmpBuffer.length() == 0) {
                        tmpBuffer.append(tmpParticle);
                    } else {
                        tmpBuffer.append(JobUpdateUtils.COMMA_SEPARATOR);
                        tmpBuffer.append(tmpParticle);
                    }
                } else {
                    if (tmpBuffer.length() == 0) {
                        tmpBuffer.append(String.format(ModelMessage.get("Error.MissingParticleTemperatureDataFormat"), tmpParticle, tmpTemperatureRepresentation));
                    } else {
                        tmpBuffer.append(JobUpdateUtils.COMMA_SEPARATOR);
                        tmpBuffer.append(String.format(ModelMessage.get("Error.MissingParticleTemperatureDataFormat"), tmpParticle, tmpTemperatureRepresentation));
                    }
                }
            }
        } else {
            for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
                String tmpParticle = aParticleTableValueItem.getValue(i, 0);
                StandardParticleDescription tmpParticleData = StandardParticleInteractionData.getInstance().getParticleDescription(tmpParticle);
                if (tmpParticleData == null) {
                    if (tmpBuffer.length() == 0) {
                        tmpBuffer.append(tmpParticle);
                    } else {
                        tmpBuffer.append(JobUpdateUtils.COMMA_SEPARATOR);
                        tmpBuffer.append(tmpParticle);
                    }
                }
            }
        }
        if (tmpBuffer.length() > 0) {
            return tmpBuffer.toString();
        } else {
            return null;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
