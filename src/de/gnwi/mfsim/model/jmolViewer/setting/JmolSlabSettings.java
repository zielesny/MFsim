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
package de.gnwi.mfsim.model.jmolViewer.setting;
/**
 * Slab settings class.
 *
 * @author Andreas Truszkowski
 */
public class JmolSlabSettings {

    /**
     * Percentage of the molecule to be displayed.
     */
    private int slab = 0;
    /**
     * Z Shade power.
     */
    private int zShadePower = 1;
    /**
     * Remove partially clipped atoms.
     */
    private boolean slabByAtom = true;
    /**
     * Use Z Shade.
     */
    private boolean useZShade = true;
    
    /**
     * Create a new instance.
     */
    public JmolSlabSettings() {
    }

    /**
     * Create a new instance.
     *
     * @param aSlab Percentage of the molecule to be displayed.
     * @param aZShadePower Z Shade power.
     * @param aSlabByAtom Remove partially clipped atoms.
     */
    public JmolSlabSettings(int aSlab, int aZShadePower, boolean aSlabByAtom) {
        this.slab = aSlab;
        this.zShadePower = aZShadePower;
        this.slabByAtom = aSlabByAtom;
    }

    /**
     * Gets the percentage of the molecule to be displayed.
     *
     * @return Percentage of the molecule to be displayed.
     */
    public int getSlab() {
        return slab;
    }

    /**
     * Sets the percentage of the molecule to be displayed.
     *
     * @param aSlab Percentage of the molecule to be displayed. Range 0 - 100.
     */
    public void setSlab(int aSlab) {
        if (aSlab < 0 || aSlab > 100) {
            throw new IllegalArgumentException("Slab percentage out of range.");
        }
        this.slab = aSlab;
    }

    /**
     * Gets whether the Z Shade is used.
     * @return True: Z shade is used, false: Otherwise
     */
    public boolean isUseZShade() {
        return useZShade;
    }

    /**
     * Sets whether the Z Shade is used.
     * @param useZShade Ture if used.
     */
    public void setUseZShade(boolean useZShade) {
        this.useZShade = useZShade;
    }

    /**
     * Gets the zShade power.
     *
     * @return zShade power.
     */
    public int getZShadePower() {
        return zShadePower;
    }

    /**
     * Sets the zShade power.
     *
     * @param aZShadePower zShade power. Range: 1 - 3.
     */
    public final void setZShadePower(int aZShadePower) {
        if (aZShadePower < 1 || aZShadePower > 3) {
            throw new IllegalArgumentException("ZShade power out of range.");
        }
        this.zShadePower = aZShadePower;
    }

    /**
     * Are partially clipped atoms removed.
     *
     * @return True if partially clipped atoms will be removed.
     */
    public boolean isSlabByAtom() {
        return slabByAtom;
    }

    /**
     * Sets whether partially clipped atoms will be removed.
     *
     * @param slabByAtom True if partially clipped atoms shall be removed.
     */
    public void setSlabByAtom(boolean slabByAtom) {
        this.slabByAtom = slabByAtom;
    }

    /**
     * Gets the slab settings script.
     *
     * @return Slab script.
     */
    public String toScript() {
        StringBuilder tmpSlabScript = new StringBuilder();
        tmpSlabScript.append(String.format("slab ON;set zshade %s;", this.useZShade ? "on" : "off"));
        tmpSlabScript.append(String.format("set slabByAtom %b;", this.isSlabByAtom()));
        tmpSlabScript.append(String.format("set zShadePower %d;", this.getZShadePower()));
        tmpSlabScript.append(String.format("slab %d;", 100 - this.getSlab()));
        tmpSlabScript.append(String.format("set zslab %d;", 90 - this.getSlab()));
        return tmpSlabScript.toString();
    }
}
