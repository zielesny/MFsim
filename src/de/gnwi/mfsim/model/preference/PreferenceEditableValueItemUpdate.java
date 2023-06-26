/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2023  Achim Zielesny (achim.zielesny@googlemail.com)
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
package de.gnwi.mfsim.model.preference;

import de.gnwi.mfsim.model.preference.PreferenceEditableEnum;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemUpdateNotifierInterface;

/**
 * Methods for update of value items of Preferences
 *
 * @author Achim Zielesny
 */
public class PreferenceEditableValueItemUpdate implements ValueItemUpdateNotifierInterface {

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public PreferenceEditableValueItemUpdate() {
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
        // <editor-fold defaultstate="collapsed" desc="Update notifier NUMBER_OF_SLICES">
        if (anUpdateNotifierValueItem.getName().equals(PreferenceEditableEnum.NUMBER_OF_SLICES.name())) {
            // <editor-fold defaultstate="collapsed" desc="Set often used value items">
            ValueItem tmpNumberOfSlicesPerViewValueItem = anUpdateNotifierValueItem;
            ValueItem tmpFirstSliceIndexValueItem = tmpNumberOfSlicesPerViewValueItem.getValueItemContainer().getValueItem(PreferenceEditableEnum.FIRST_SLICE_INDEX.name());

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Update receiver FIRST_SLICE_INDEX">
            this.updateFirstSliceIndex(tmpNumberOfSlicesPerViewValueItem, tmpFirstSliceIndexValueItem);
            // </editor-fold>
            return;
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    // NOTE: All of these methods do NOT invoke an update notification of any value item.
    // <editor-fold defaultstate="collapsed" desc="-- FIRST_SLICE_INDEX as update receiver">
    /**
     * Updates first slice index
     *
     * @param aNumberOfSlicesPerViewValueItem NUMBER_OF_SLICES value item (is
     * NOT changed)
     * @param aFirstSliceIndexValueItem FIRST_SLICE_INDEX value item (may be
     * changed)
     */
    private void updateFirstSliceIndex(ValueItem aNumberOfSlicesPerViewValueItem, ValueItem aFirstSliceIndexValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfSlicesPerViewValueItem == null 
                || !aNumberOfSlicesPerViewValueItem.getName().equals(PreferenceEditableEnum.NUMBER_OF_SLICES.name()) 
                || aFirstSliceIndexValueItem == null
                || !aFirstSliceIndexValueItem.getName().equals(PreferenceEditableEnum.FIRST_SLICE_INDEX.name())) {
            return;
        }
        // </editor-fold>
        aFirstSliceIndexValueItem.getTypeFormat().setMaximumValue(aNumberOfSlicesPerViewValueItem.getValueAsDouble() - 1.0);
        if (aFirstSliceIndexValueItem.getValueAsInt() > aNumberOfSlicesPerViewValueItem.getValueAsInt() - 1) {
            aFirstSliceIndexValueItem.setValue(String.valueOf(Preferences.getInstance().getDefaultFirstSliceIndex()));
        }
    }
    // </editor-fold>
    // </editor-fold>
    // </editor-fold>

}
