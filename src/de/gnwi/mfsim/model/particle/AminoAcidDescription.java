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

import java.util.regex.Pattern;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Amino acid description
 *
 * @author Achim Zielesny
 */
public class AminoAcidDescription {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * One-letter code
     */
    private String oneLetterCode;
    /**
     * Three-letter code
     */
    private String threeLetterCode;
    /**
     * Name
     */
    private String name;
    /**
     * Spices string
     */
    private String spicesString;
    /**
     * Charge settings
     */
    private String chargeSettings;
    /**
     * Amino acid description string
     */
    private String aminoAcidDescriptionString;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private Regex patterns">
    private Pattern oneLetterCodePattern = Pattern.compile("[A-Z]");
    private Pattern threeLetterCodePattern = Pattern.compile("[A-Z][a-z][a-z]");
            
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aOneLetterCode One-letter code
     * @param aThreeLetterCode Three-letter code
     * @param aName Name
     * @param aSpices Spices
     * @param aChargeSettings Charge settings
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public AminoAcidDescription(String aOneLetterCode, String aThreeLetterCode, String aName, String aSpices, String aChargeSettings) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aOneLetterCode == null || aOneLetterCode.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aOneLetterCode).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (!this.oneLetterCodePattern.matcher(aOneLetterCode).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aThreeLetterCode == null || aThreeLetterCode.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aThreeLetterCode).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (!this.threeLetterCodePattern.matcher(aThreeLetterCode).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aName == null || aName.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aName).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aSpices == null || aSpices.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aSpices).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aChargeSettings == null || aChargeSettings.isEmpty() || ModelDefinitions.GENERAL_SEPARATOR_PATTERN.matcher(aChargeSettings).matches()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>

        this.oneLetterCode = aOneLetterCode;
        this.threeLetterCode = aThreeLetterCode;
        this.name = aName;
        this.spicesString = aSpices;
        this.chargeSettings = aChargeSettings;
        // Set particle description string
        this.aminoAcidDescriptionString = this.oneLetterCode // Index 0
                + ModelDefinitions.GENERAL_SEPARATOR + this.threeLetterCode // Index 1
                + ModelDefinitions.GENERAL_SEPARATOR + this.name // Index 2
                + ModelDefinitions.GENERAL_SEPARATOR + this.spicesString // Index 3
                + ModelDefinitions.GENERAL_SEPARATOR + this.chargeSettings; // Index 4
    }

    /**
     * Constructor
     *
     * @param anAminoAcidDescriptionString Amino acid description string
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public AminoAcidDescription(String anAminoAcidDescriptionString) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (anAminoAcidDescriptionString == null || anAminoAcidDescriptionString.isEmpty()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>

        String[] tmpAminoAcidDataArray = ModelDefinitions.GENERAL_SEPARATOR_PATTERN.split(anAminoAcidDescriptionString);
        if (tmpAminoAcidDataArray == null || tmpAminoAcidDataArray.length != 5) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        this.oneLetterCode = tmpAminoAcidDataArray[0].trim();
        this.threeLetterCode = tmpAminoAcidDataArray[1].trim();
        this.name = tmpAminoAcidDataArray[2].trim();
        this.spicesString = tmpAminoAcidDataArray[3].trim();
        this.chargeSettings = tmpAminoAcidDataArray[4].trim();
        
        this.aminoAcidDescriptionString = anAminoAcidDescriptionString;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns amino acid description string
     *
     * @return Amino acid description string
     */
    public String getAminoAcidDescriptionString() {
        return this.aminoAcidDescriptionString;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * One-letter code
     *
     * @return One-letter code
     */
    public String getOneLetterCode() {
        return this.oneLetterCode;
    }

    /**
     * Three-letter code
     *
     * @return Three-letter code
     */
    public String getThreeLetterCode() {
        return this.threeLetterCode;
    }

    /**
     * Name
     *
     * @return Name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Spices string
     *
     * @return Spices string
     */
    public String getSpicesString() {
        return this.spicesString;
    }

    /**
     * Charge settings
     *
     * @return Charge settings
     */
    public String getChargeSettings() {
        return this.chargeSettings;
    }

    // </editor-fold>

}
