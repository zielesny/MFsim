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
package de.gnwi.mfsim.model.particle;

import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.valueItem.ValueItemUpdateNotifierInterface;
import java.util.LinkedList;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Methods for update of particle data and particle interaction related value items
 *
 * @author Achim Zielesny
 */
public class StandardParticleUpdateUtils implements ValueItemUpdateNotifierInterface {

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public StandardParticleUpdateUtils() {
        // Do nothing
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="notifyDependentValueItemsForUpdate() method">
    /**
     * Notify dependent value items of container for update
     *
     * @param anUpdateNotifierValueItem Value item that notifies update
     */
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
            ValueItem tmpInteractionTableValueItem = anUpdateNotifierValueItem.getValueItemContainer().getValueItem("MFSIM_SOURCE_INTERACTIONS_MATRIX");

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update itself">
            this.updateParticleMatrixItself(anUpdateNotifierValueItem);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver MFSIM_SOURCE_INTERACTIONS_MATRIX">
            this.updateInteractionMatrix(anUpdateNotifierValueItem, tmpInteractionTableValueItem);
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier MFSIM_SOURCE_INTERACTIONS_MATRIX">
        if (anUpdateNotifierValueItem.getName().equals("MFSIM_SOURCE_INTERACTIONS_MATRIX")) {
            // <editor-fold defaultstate="collapsed" desc="Update itself">
            this.updateInteractionMatrixItself(anUpdateNotifierValueItem);
            // </editor-fold>
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier PARTICLE_DUPLICATE_MATRIX">
        if (anUpdateNotifierValueItem.getName().equals("PARTICLE_DUPLICATE_MATRIX")) {
            // <editor-fold defaultstate="collapsed" desc="Update itself">
            this.updateParticleDuplicateMatrixItself(anUpdateNotifierValueItem);
            // </editor-fold>
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    // NOTE: All of these methods do NOT invoke an update notification of any value item.
    // <editor-fold defaultstate="collapsed" desc="-- ParticleTable as update notifier">
    /**
     * Updates Quantity value item itself. BoxSize value item may be updated too. NOTE: Update notification of any value item is suppressed.
     *
     * @param aParticleTableValueItem Value item ParticleTable (may be changed)
     */
    private void updateParticleMatrixItself(ValueItem aParticleTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }

        // </editor-fold>
        ValueItemMatrixElement[][] tmpLastClonedMatrix = aParticleTableValueItem.getLastClonedMatrix();
        ValueItemMatrixElement[][] tmpCurrentMatrix = aParticleTableValueItem.getMatrix();

        // <editor-fold defaultstate="collapsed" desc="Change in single row">
        int tmpChangedRow = -1;
        for (int i = 0; i < tmpLastClonedMatrix.length; i++) {
            for (int k = 0; k < tmpLastClonedMatrix[0].length; k++) {
                if (!tmpLastClonedMatrix[i][k].getFormattedValue().equals(tmpCurrentMatrix[i][k].getFormattedValue())) {
                    tmpChangedRow = i;
                    break;
                }
            }
            if (tmpChangedRow >= 0) {
                break;
            }
        }
        if (tmpChangedRow >= 0) {
            ValueItemMatrixElement[] tmpNewRow = tmpCurrentMatrix[tmpChangedRow];
            StandardParticleDescription tmpNewParticleData = new StandardParticleDescription(tmpNewRow[0].getValue(), // aParticle
                    tmpNewRow[1].getValue(), // aName
                    tmpNewRow[2].getValue(), // aMolWeightInDpdUnits
                    tmpNewRow[3].getValue(), // aMolWeightInGMol
                    tmpNewRow[4].getValue(), // aCharge
                    tmpNewRow[5].getValue(), // aVolume
                    tmpNewRow[6].getValue(), // aGraphicsRadius
                    tmpNewRow[7].getValue()); // aStandardColor
            if (!tmpLastClonedMatrix[tmpChangedRow][0].getFormattedValue().equals(tmpCurrentMatrix[tmpChangedRow][0].getFormattedValue())) {
                // Particle changed: Remove old particle
                StandardParticleInteractionData.getInstance().removeParticleWithAllData(tmpLastClonedMatrix[tmpChangedRow][0].getFormattedValue());
            }
            StandardParticleInteractionData.getInstance().updateParticleDescription(tmpNewParticleData);
        }

        // </editor-fold>
    }

    /**
     * Updates interaction matrix value item in specific way with ParticleTable value item (see code)
     *
     * @param aParticleTableValueItem Particle matrix value item (is NOT changed)
     * @param anInteractionTableValueItem Interaction matrix value item (may be changed)
     * @param aTemperatureValueItem Temperature value item (is NOT changed)
     *
     */
    private void updateInteractionMatrix(ValueItem aParticleTableValueItem, ValueItem anInteractionTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return;
        }
        if (anInteractionTableValueItem == null || !anInteractionTableValueItem.getName().equals("MFSIM_SOURCE_INTERACTIONS_MATRIX")) {
            return;
        }

        // </editor-fold>
        anInteractionTableValueItem.setMatrix(StandardParticleInteractionData.getInstance().getInteractionsMatrixValueItem().getMatrix());
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- MFSIM_SOURCE_INTERACTIONS_MATRIX as update notifier">
    /**
     * Updates MFSIM_SOURCE_INTERACTIONS_MATRIX value item in specific way (see code)
     *
     * @param anInteractionTableValueItem MFSIM_SOURCE_INTERACTIONS_MATRIX value item (may be changed)
     */
    private void updateInteractionMatrixItself(ValueItem anInteractionTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anInteractionTableValueItem == null || !anInteractionTableValueItem.getName().equals("MFSIM_SOURCE_INTERACTIONS_MATRIX")) {
            return;
        }

        // </editor-fold>
        ValueItemMatrixElement[][] tmpLastClonedMatrix = anInteractionTableValueItem.getLastClonedMatrix();
        ValueItemMatrixElement[][] tmpCurrentMatrix = anInteractionTableValueItem.getMatrix();
        String tmpFirstParticle = tmpCurrentMatrix[0][0].getFormattedValue();
        String tmpTemperature = tmpCurrentMatrix[0][2].getFormattedValue();

        // <editor-fold defaultstate="collapsed" desc="First particle changed">
        if (!tmpLastClonedMatrix[0][0].getFormattedValue().equals(tmpCurrentMatrix[0][0].getFormattedValue())) {
            anInteractionTableValueItem.setMatrix(StandardParticleInteractionData.getInstance().getInteractionsMatrixValueItem(tmpFirstParticle, tmpTemperature).getMatrix());
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Temperature changed">
        if (!tmpLastClonedMatrix[0][2].getFormattedValue().equals(tmpCurrentMatrix[0][2].getFormattedValue())) {
            anInteractionTableValueItem.setMatrix(StandardParticleInteractionData.getInstance().getInteractionsMatrixValueItem(tmpFirstParticle, tmpTemperature).getMatrix());
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="DPD interaction parameter changed">
        for (int i = 0; i < tmpCurrentMatrix.length; i++) {
            if (!tmpLastClonedMatrix[i][3].getFormattedValue().equals(tmpCurrentMatrix[i][3].getFormattedValue())) {
                String tmpSecondParticle = tmpCurrentMatrix[i][1].getFormattedValue();
                StandardParticleInteractionData.getInstance().updateInteraction(tmpFirstParticle, tmpSecondParticle, tmpTemperature,
                        tmpCurrentMatrix[i][3].getFormattedValue());
                break;
            }
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- PARTICLE_DUPLICATE_MATRIX as update notifier">
    /**
     * Updates PARTICLE_DUPLICATE_MATRIX value item in specific way (see code)
     *
     * @param aParticleDuplicateMatrixValueItem PARTICLE_DUPLICATE_MATRIX value item (may be changed)
     */
    private void updateParticleDuplicateMatrixItself(ValueItem aParticleDuplicateMatrixValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleDuplicateMatrixValueItem == null || !aParticleDuplicateMatrixValueItem.getName().equals("PARTICLE_DUPLICATE_MATRIX")) {
            return;
        }

        // </editor-fold>
        LinkedList<String> tmpForbiddenParticlesList = new LinkedList<String>();
        for (int i = 0; i < aParticleDuplicateMatrixValueItem.getMatrixRowCount(); i++) {
            String tmpOldNewParticle = aParticleDuplicateMatrixValueItem.getValue(i, 0).trim();
            tmpForbiddenParticlesList.add(tmpOldNewParticle);
            String tmpNewParticle = aParticleDuplicateMatrixValueItem.getValue(i, 2).trim();
            if (tmpNewParticle != null && !tmpNewParticle.isEmpty()) {
                tmpForbiddenParticlesList.add(tmpNewParticle);
            }
        }
        String[] tmpForbiddenTexts = tmpForbiddenParticlesList.toArray(new String[0]);
        String tmpAllowedCharacters = ModelDefinitions.PARTICLE_ALLOWED_CHARACTERS_REGEX_STRING;
        String tmpAllowedMatch = ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING;
        ValueItemDataTypeFormat tmpNewParticleTypeFormat = new ValueItemDataTypeFormat(tmpAllowedCharacters, tmpAllowedMatch, tmpForbiddenTexts);
        ValueItemMatrixElement[][] tmpMatrix = aParticleDuplicateMatrixValueItem.getMatrix();
        for (int i = 0; i < tmpMatrix.length; i++) {
            tmpMatrix[i][2].setValueAndTypeFormat(tmpMatrix[i][2].getValue(), tmpNewParticleTypeFormat);
        }
        aParticleDuplicateMatrixValueItem.setMatrix(tmpMatrix);
    }
    // </editor-fold>
    // </editor-fold>
    // </editor-fold>

}
