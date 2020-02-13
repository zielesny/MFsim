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
package de.gnwi.mfsim.gui.util;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import de.gnwi.mfsim.model.util.ModelUtils;

/**
 * Virtual slider
 * 
 * @author Achim Zielesny
 */
public class VirtualSlider {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Slider
     */
    JSlider slider;
    /**
     * Minimum value
     */
    int minimum;
    /**
     * Maximum value
     */
    int maximum;
    /**
     * Major tick spacing
     */
    int majorTickSpacing;
    /**
     * Minor tick spacing
     */
    int minorTickSpacing;
    /**
     * Value
     */
    int value;
    /**
     * True: Values of virtual slider are different from those of slider, false: Otherwise (both are identical, i.e. nothing is to be mapped)
     */
    boolean isDifferent;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aSlider Slider
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public VirtualSlider(JSlider aSlider) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSlider == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        this.slider = aSlider;
        this.slider.setSnapToTicks(true);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Set methods">
    /**
     * Sets slider parameters
     *
     * @param aNumberOfTicks Number of ticks
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void setSliderParameters(int aNumberOfTicks) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfTicks < 1) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        this.minimum = 0;
        this.maximum = aNumberOfTicks;
        this.majorTickSpacing = 2;
        this.minorTickSpacing = 1;
        if (aNumberOfTicks <= 100) {
            this.slider.setMinimum(this.minimum);
            this.slider.setMaximum(this.maximum);
            this.slider.setMajorTickSpacing(this.majorTickSpacing);
            this.slider.setMinorTickSpacing(this.minorTickSpacing);
            this.isDifferent = false;
        } else {
            this.slider.setMinimum(0);
            this.slider.setMaximum(100);
            this.slider.setMajorTickSpacing(2);
            this.slider.setMinorTickSpacing(1);
            this.isDifferent = true;
        }
    }

    /**
     * Sets value
     *
     * @param aValue Value
     */
    public void setValue(int aValue) {
        if (aValue > this.maximum - this.minorTickSpacing) {
            this.value = this.maximum - this.minorTickSpacing;
        } else if (aValue < 0) {
            this.value = 0;
        } else {
            this.value = aValue;
        }
        if (this.isDifferent) {
            int tmpMin1 = this.minimum;
            int tmpMin2 = this.slider.getMinimum();
            int tmpMax1 = this.maximum;
            int tmpMax2 = this.slider.getMaximum();
            int tmpSliderValue = this.getLinearMappedValue(aValue, tmpMin1, tmpMax1, tmpMin2, tmpMax2);
            if (tmpSliderValue > this.slider.getMaximum() - this.slider.getMinorTickSpacing()) {
                tmpSliderValue = this.slider.getMaximum() - this.slider.getMinorTickSpacing();
            }
            this.slider.setValue(tmpSliderValue);
        } else {
            this.slider.setValue(this.value);
        }
    }

    /**
     * Sets value without stateChanged event of this.slider
     *
     * @param aValue Value
     */
    public void setValueWithoutStateChangedEvent(int aValue) {
        ChangeListener[] tmpChangeListeners = this.slider.getChangeListeners();
        if (tmpChangeListeners == null || tmpChangeListeners.length == 0) {
            this.setValue(aValue);
        } else {
            for (ChangeListener tmpChangeListener : tmpChangeListeners) {
                this.slider.removeChangeListener(tmpChangeListener);
            }
            this.setValue(aValue);
            for (ChangeListener tmpChangeListener : tmpChangeListeners) {
                this.slider.addChangeListener(tmpChangeListener);
            }
        }
    }

    /**
     * Sets value with corresponding slider value
     */
    public void setValueFromSlider() {
        if (this.slider.getValue() > this.slider.getMaximum() - this.slider.getMinorTickSpacing()) {
            ChangeListener[] tmpChangeListeners = this.slider.getChangeListeners();
            if (tmpChangeListeners == null || tmpChangeListeners.length == 0) {
                this.slider.setValue(this.slider.getMaximum() - this.slider.getMinorTickSpacing());
            } else {
                for (ChangeListener tmpChangeListener : tmpChangeListeners) {
                    this.slider.removeChangeListener(tmpChangeListener);
                }
                this.slider.setValue(this.slider.getMaximum() - this.slider.getMinorTickSpacing());
                for (ChangeListener tmpChangeListener : tmpChangeListeners) {
                    this.slider.addChangeListener(tmpChangeListener);
                }
            }
        }
        if (this.isDifferent) {
            int tmpMin1 = this.slider.getMinimum();
            int tmpMin2 = this.minimum;
            int tmpMax1 = this.slider.getMaximum();
            int tmpMax2 = this.maximum;
            this.value = this.getLinearMappedValue(this.slider.getValue(), tmpMin1, tmpMax1, tmpMin2, tmpMax2);
        } else {
            this.value = this.slider.getValue();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Get methods">
    /**
     * Returns value
     *
     * @return Returns value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns maximum value
     *
     * @return Returns maximum value
     */
    public int getMaximum() {
        return this.maximum;
    }

    /**
     * Returns minor tick spacing
     *
     * @return Returns minor tick spacing
     */
    public int getMinorTickSpacing() {
        return this.minorTickSpacing;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Performs linear transformation of aValue of interval 1 [aMin1, aMax1] into interval 2 [aMin2, aMax2]
     *
     * @param aValue Value
     * @param aMin1 Minimum value of interval 1
     * @param aMin2 Minimum value of interval 2
     * @param aMax1 Maximum value of interval 1
     * @param aMax2 Maximum value of interval 2
     */
    private int getLinearMappedValue(int aValue, int aMin1, int aMax1, int aMin2, int aMax2) {
        return (int) ModelUtils.roundDoubleValue((double) aMax2 - ((double) aMax2 - (double) aMin2) / ((double) aMax1 - (double) aMin1) * ((double) aMax1 - (double) aValue), 0);
    }
    // </editor-fold>

}
