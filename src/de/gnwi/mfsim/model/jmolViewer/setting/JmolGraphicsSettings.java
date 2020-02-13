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
package de.gnwi.mfsim.model.jmolViewer.setting;

import java.awt.Color;

/**
 * Graphic settings object.
 *
 * @author Andreas Truszkowski
 */
public class JmolGraphicsSettings {

    /**
     * Ambient light percentage.
     */
    private int ambientPercent = 50;
    /**
     * Diffuse light percentage.
     */
    private int diffusePercent = 80;
    /**
     * Specular exponent.
     */
    private int specularExponent = 3;
    /**
     * Specular reflection percentage.
     */
    private int specularPercent = 100;
    /**
     * Specular reflection power.
     */
    private int specularPower = 50;
    /**
     * Background color.
     */
    private Color backgroundColor = Color.black;

    /**
     * Creates a new instance.
     */
    public JmolGraphicsSettings() {
    }

    /**
     * Creates a new instance.
     *
     * @param anAmbientPercent Ambient light percentage.
     * @param aDiffusePercent Diffuse light percentage.
     * @param aSpecularExponent Specular exponent.
     * @param aSpecularPercent Specular reflection percentage.
     * @param aSpecularPower Specular reflection power.
     */
    public JmolGraphicsSettings(int anAmbientPercent, int aDiffusePercent,
            int aSpecularExponent, int aSpecularPercent, int aSpecularPower) {
        this.setAmbientPercent(anAmbientPercent);
        this.setDiffusePercent(aDiffusePercent);
        this.setSpecularExponent(aSpecularExponent);
        this.setSpecularPercent(aSpecularPercent);
        this.setSpecularPower(aSpecularPower);
    }

    /**
     * Gets the ambient light percentage.
     *
     * @return Ambient light percentage.
     */
    public int getAmbientPercent() {
        return ambientPercent;
    }

    /**
     * Sets the ambient ligth percentage.
     *
     * @param anAmbientPercent Ambient light percentage. Range 0 - 100.
     */
    public final void setAmbientPercent(int anAmbientPercent) {
        if (anAmbientPercent < 0 || anAmbientPercent > 100) {
            throw new IllegalArgumentException("Ambient light percentage out of range.");
        }
        this.ambientPercent = anAmbientPercent;
    }

    /**
     * Gets the current background color.
     *
     * @return The background color.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the current background color.
     *
     * @param backgroundColor The new background color.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets the diffuse light percenatge.
     *
     * @return Diffuse light percenatge.
     */
    public int getDiffusePercent() {
        return diffusePercent;
    }

    /**
     * Sets the diffuse light percenatge.
     *
     * @param aDiffusePercent Diffuse light percenatge. Range 0 - 100.
     */
    public final void setDiffusePercent(int aDiffusePercent) {
        if (aDiffusePercent < 0 || aDiffusePercent > 100) {
            throw new IllegalArgumentException("Diffuse light percentage out of range.");
        }
        this.diffusePercent = aDiffusePercent;
    }

    /**
     * Gets the specular reflection exponent.
     *
     * @return Specular reflection exponent.
     */
    public int getSpecularExponent() {
        return specularExponent;
    }

    /**
     * Sets the specular reflection exponent.
     *
     * @param aSpecularExponent Specular reflection exponent. Range 1 - 10.
     */
    public final void setSpecularExponent(int aSpecularExponent) {
        if (aSpecularExponent < 1 || aSpecularExponent > 10) {
            throw new IllegalArgumentException("Specular reflection exponent out of range.");
        }
        this.specularExponent = aSpecularExponent;
    }

    /**
     * Gets the specular reflection percentage.
     *
     * @return Specular reflection percentage.
     */
    public int getSpecularPercent() {
        return specularPercent;
    }

    /**
     * Sets the specular reflection percentage.
     *
     * @param aSpecularPercent Specular reflection percentage. Range 0 - 100.
     */
    public final void setSpecularPercent(int aSpecularPercent) {
        if (aSpecularPercent < 0 || aSpecularPercent > 100) {
            throw new IllegalArgumentException("Specular reflection percentage out of range.");
        }
        this.specularPercent = aSpecularPercent;
    }

    /**
     * Gets the specular reflection power.
     *
     * @return Specular reflection power.
     */
    public int getSpecularPower() {
        return specularPower;
    }

    /**
     * Sets the specular reflection power.
     *
     * @param aSpecularPower Specular reflection power. Range 0 - 100.
     */
    public final void setSpecularPower(int aSpecularPower) {
        if (aSpecularPower < 0 || aSpecularPower > 100) {
            throw new IllegalArgumentException("Specular reflection power out of range.");
        }
        this.specularPower = aSpecularPower;
    }
}
