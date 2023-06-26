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
package de.gnwi.mfsim.model.particle;

import de.gnwi.mfsim.model.util.StandardColorEnum;
import java.util.Locale;
import java.util.regex.Pattern;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Particle description
 *
 * @author Achim Zielesny
 */
public class StandardParticleDescription {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Pattern for single particle to match
     */
    private final Pattern particlePattern = Pattern.compile(ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING);
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Particle
     */
    private String particle;
    /**
     * Name/description of particle
     */
    private String name;
    /**
     * Molecular weight of particle in DPD units
     */
    private String massDpd;
    /**
     * Molecular weight of particle in g/mol
     */
    private String massGMol;
    /**
     * Charge of particle
     */
    private String charge;
    /**
     * Charge value of particle
     */
    private double chargeValue;
    /**
     * Volume of particle
     */
    private String volume;
    /**
     * Graphics radius of particle
     */
    private String graphicsRadius;
    /**
     * standard color of particle
     */
    private String standardColor;
    /**
     * Particle description string
     */
    private String particleDescriptionString;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aParticle Particle
     * @param aName Name/description of particle
     * @param aMolWeightInDpdUnits Molecular weight of particle in DPD units
     * @param aCharge Charge of particle
     * @param aMolWeightInGMol Molecular weight of particle in g/mol
     * @param aVolume Volume of particle
     * @param aGraphicsRadius Graphics radius of particle
     * @param aStandardColor standard color of particle
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public StandardParticleDescription(
        String aParticle, 
        String aName, 
        String aMolWeightInDpdUnits, 
        String aCharge, 
        String aMolWeightInGMol, 
        String aVolume,
        String aGraphicsRadius, 
        String aStandardColor
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aParticle == null || aParticle.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aParticle).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aName == null || aName.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aName).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aMolWeightInDpdUnits == null || aMolWeightInDpdUnits.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aMolWeightInDpdUnits).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aMolWeightInGMol == null || aMolWeightInGMol.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aMolWeightInGMol).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aCharge == null || aCharge.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aCharge).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aVolume == null || aVolume.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aVolume).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aGraphicsRadius == null || aGraphicsRadius.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aGraphicsRadius).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aStandardColor == null || aStandardColor.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aStandardColor).matches()
                || !StandardColorEnum.isStandardColor(aStandardColor.toUpperCase(Locale.ENGLISH))) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        if (!this.particlePattern.matcher(aParticle.trim()).matches()) {
            throw new IllegalArgumentException("aParticle is illegal.");
        } else {
            this.particle = aParticle.trim();
        }

        this.name = aName;
        this.massDpd = aMolWeightInDpdUnits;
        this.charge = aCharge;
        this.chargeValue = Double.valueOf(this.charge);
        this.massGMol = aMolWeightInGMol;
        this.volume = aVolume;
        this.graphicsRadius = aGraphicsRadius;
        this.standardColor = aStandardColor.toUpperCase(Locale.ENGLISH);
        // Set particle description string
        this.particleDescriptionString = this.particle // Index 0
                + ModelDefinitions.GENERAL_SEPARATOR + this.name // Index 1
                + ModelDefinitions.GENERAL_SEPARATOR + this.massDpd // Index 2
                + ModelDefinitions.GENERAL_SEPARATOR + this.massGMol // Index 3
                + ModelDefinitions.GENERAL_SEPARATOR + this.charge // Index 4
                + ModelDefinitions.GENERAL_SEPARATOR + this.volume // Index 5
                + ModelDefinitions.GENERAL_SEPARATOR + this.graphicsRadius // Index 6
                + ModelDefinitions.GENERAL_SEPARATOR + this.standardColor; // Index 7
    }

    /**
     * Constructor
     *
     * @param aParticleDescriptionString Particle description string
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public StandardParticleDescription(String aParticleDescriptionString) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aParticleDescriptionString == null || aParticleDescriptionString.isEmpty()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        String[] tmpParticleDataArray = ModelDefinitions.GENERAL_SEPARATOR_PATTERN.split(aParticleDescriptionString);
        if (tmpParticleDataArray == null || tmpParticleDataArray.length != 8) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        this.particle = tmpParticleDataArray[0].trim();
        this.name = tmpParticleDataArray[1].trim();
        this.massDpd = tmpParticleDataArray[2].trim();
        this.massGMol = tmpParticleDataArray[3].trim();
        this.charge = tmpParticleDataArray[4].trim();
        this.chargeValue = Double.valueOf(this.charge);
        this.volume = tmpParticleDataArray[5].trim();
        this.graphicsRadius = tmpParticleDataArray[6].trim();
        this.standardColor = tmpParticleDataArray[7].trim();
        this.particleDescriptionString = aParticleDescriptionString;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns particle description string
     *
     * @return Particle description string
     */
    public String getParticleDescriptionString() {
        return this.particleDescriptionString;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Particle
     *
     * @return Particle
     */
    public String getParticle() {
        return this.particle;
    }

    /**
     * Name/description of particle
     *
     * @return Name/description of particle
     */
    public String getName() {
        return this.name;
    }

    /**
     * Molecular weight of particle in DPD units
     *
     * @return Molecular weight of particle in DPD units
     */
    public String getMolWeightInDpdUnits() {
        return this.massDpd;
    }

    /**
     * Molecular weight of particle in g/mol
     *
     * @return Molecular weight of particle in g/mol
     */
    public String getMolWeightInGMol() {
        return this.massGMol;
    }

    /**
     * Charge of particle
     *
     * @return Charge of particle
     */
    public String getCharge() {
        return this.charge;
    }

    /**
     * Charge value of particle
     *
     * @return Charge value of particle
     */
    public double getChargeValue() {
        return this.chargeValue;
    }

    /**
     * Volume of particle
     *
     * @return Volume of particle
     */
    public String getVolume() {
        return this.volume;
    }

    /**
     * Graphics radius of particle
     *
     * @return Graphics radius of particle
     */
    public String getGraphicsRadius() {
        return this.graphicsRadius;
    }

    /**
     * standard color of particle
     *
     * @return standard color of particle
     */
    public String getStandardColor() {
        return this.standardColor;
    }
    // </editor-fold>
}
