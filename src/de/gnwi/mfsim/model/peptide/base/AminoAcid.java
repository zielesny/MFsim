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
package de.gnwi.mfsim.model.peptide.base;

import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import de.gnwi.mfsim.model.peptide.utils.Tools;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Class which represents a single amino acid.
 *
 * @author Andreas Truszkowski
 */
public final class AminoAcid {

    // Fields
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Regex pattern describing the amino acid one letter code.
     */
    private final Pattern oneLetterPattern = Pattern.compile("[ARNDCEQGHILKMFPSTWYV]");

    /**
     * Regex pattern describing the amino acid three letter code.
     */
    private final Pattern threeLetterPattern = Pattern.compile("[A-Z][A-Za-z][A-Za-z]");

    /**
     * Regex pattern for single particle to match. NOTE: This definition MUST be
     * identical to DefinitionBasic.PARTICLE_REGEX_PATTERN_STRING.
     */
    private final Pattern singleParticlePattern = Pattern.compile("^[A-Z][A-Za-z0-9]{0,9}$");

    /**
     * Regex pattern for single particle plus sidechain to match. NOTE: This
     * definition MUST be consistent with
     * DefinitionBasic.PARTICLE_REGEX_PATTERN_STRING.
     */
    private final Pattern sideChainParticlePattern = Pattern.compile("^[A-Z][A-Za-z0-9]{0,9}\\(.+\\)$");

    /**
     * Name of the amino acid.
     */
    private String name;

    /**
     * One letter code of the amino acid.
     */
    private String oneLetter;

    /**
     * Spices string of the amino acid.
     */
    private String spicesString = null;

    /**
     * Three letter code of the amino acid.
     */
    private String threeLetter;

    /**
     * ArrayList holding the charge settings.
     */
    private ArrayList<ChargeSetting> chargeSettings = null;

    /**
     * Amino acid definition string
     */
    private String aminoAcidDefinition;

    /**
     * Amino acids utility
     */
    private AminoAcidsUtility aminoAcidsUtility;

    /**
     * True: Amino acid is represented by single-particle Spices, false:
     * Otherwise
     */
    private boolean isSingleParticleSpices;

    //
    // Constructor /////////////////////////////////////////////////////////////
    /**
     * Creates a new instance.
     *
     * @param aName Name of the amino acid.
     * @param anOneLetter One letter code of the amino acid.
     * @param aThreeLetterCode Three letter code of the amino acid.
     * @param aSpices Spices describing gthe amino acid in particle notation.
     * @param aChargeSettings String defining the charge settings for this amino
     * acid. Definition: (AAAA BBBB ? CCCC : DDDD) - AAAA: Target group in the amino
     * acid (see ChargeType class) - BBBB: The pKs-value - CCCC: Protonated
     * particle - DDDD: Unprotonated particle
     * @throws DpdPeptideException DpdPeptideException
     */
    public AminoAcid(String aName, String anOneLetter, String aThreeLetterCode, String aSpices, String aChargeSettings) throws DpdPeptideException {
        // Check forbidden characters Tools.GENERAL_LINE_SEPARATOR and Tools.GENERAL_SEPARATOR
        if (Tools.GENERAL_LINE_SEPARATOR_PATTERN.matcher(aName).matches()
                || Tools.GENERAL_LINE_SEPARATOR_PATTERN.matcher(anOneLetter).matches()
                || Tools.GENERAL_LINE_SEPARATOR_PATTERN.matcher(aThreeLetterCode).matches()
                || Tools.GENERAL_LINE_SEPARATOR_PATTERN.matcher(aSpices).matches()
                || Tools.GENERAL_LINE_SEPARATOR_PATTERN.matcher(aChargeSettings).matches()) {
            throw new DpdPeptideException("Peptide.InvalidLineSeparatorCharacter");
        }
        if (Tools.GENERAL_SEPARATOR_PATTERN.matcher(aName).matches()
                || Tools.GENERAL_SEPARATOR_PATTERN.matcher(anOneLetter).matches()
                || Tools.GENERAL_SEPARATOR_PATTERN.matcher(aThreeLetterCode).matches()
                || Tools.GENERAL_SEPARATOR_PATTERN.matcher(aSpices).matches()
                || Tools.GENERAL_SEPARATOR_PATTERN.matcher(aChargeSettings).matches()) {
            throw new DpdPeptideException("Peptide.InvalidSeparatorCharacter");
        }
        this.name = aName;
        this.setOneLetterCode(anOneLetter);
        this.setThreeLetterCode(aThreeLetterCode);
        this.setSpices(aSpices);
        this.setChargeSettings(aChargeSettings);
        String tmpVersion = "1.0.0.0";
        this.aminoAcidDefinition
                = tmpVersion + Tools.GENERAL_LINE_SEPARATOR
                + aName + Tools.GENERAL_LINE_SEPARATOR
                + anOneLetter + Tools.GENERAL_LINE_SEPARATOR
                + aThreeLetterCode + Tools.GENERAL_LINE_SEPARATOR
                + aSpices + Tools.GENERAL_LINE_SEPARATOR
                + aChargeSettings;
        this.aminoAcidsUtility = AminoAcids.getInstance().getAminoAcidsUtility();
    }

    /**
     * Creates a new instance.
     *
     * @param anAminoAcidDefinition Amino acid definition
     * @param anAminoAcidsUtility Amino acids utility
     * @throws DpdPeptideException DpdPeptideException
     */
    public AminoAcid(String anAminoAcidDefinition, AminoAcidsUtility anAminoAcidsUtility) throws DpdPeptideException {
        if (anAminoAcidDefinition == null || anAminoAcidDefinition.isEmpty()) {
            throw new DpdPeptideException("Peptide.InvalidAminoAcidDefinition");
        }
        String[] tmpItems = Tools.GENERAL_LINE_SEPARATOR_PATTERN.split(anAminoAcidDefinition);
        if (tmpItems == null || tmpItems.length != 6) {
            throw new DpdPeptideException("Peptide.InvalidAminoAcidDefinition");
        }
        String tmpVersion = tmpItems[0];
        if (tmpVersion.equals("1.0.0.0")) {
            this.name = tmpItems[1];
            this.setOneLetterCode(tmpItems[2]);
            this.setThreeLetterCode(tmpItems[3]);
            this.setSpices(tmpItems[4]);
            this.setChargeSettings(tmpItems[5]);
            this.aminoAcidDefinition = anAminoAcidDefinition;
        } else {
            throw new DpdPeptideException("Peptide.InvalidAminoAcidDefinition");
        }
        if (anAminoAcidsUtility == null) {
            throw new DpdPeptideException("Peptide.MissingAminoAcidsUtility");
        }
        this.aminoAcidsUtility = anAminoAcidsUtility;
    }

    //
    // Methods /////////////////////////////////////////////////////////////
    /**
     * Partitionizes the given charge argument list into the corresponding
     * single arguments.
     *
     * @param aChargeArgument Charge argument.
     * @return Array containing single charge arguments (S+, N+, C-,...)
     */
    private String[] partitionizeChargeArgument(String aChargeArgument) {
        String[] tmpArguments = new String[aChargeArgument.length() / 2];
        for (int i = 0; i < aChargeArgument.length() / 2; i++) {
            tmpArguments[i] = aChargeArgument.substring(i * 2, i * 2 + 2);
        }
        return tmpArguments;
    }

    /**
     * Adds a charge setting to given one amino acid code.
     *
     * @param aPH given pH-value
     * @param aCode One letter amino acid code
     * @param aSetting Charge setting to add
     * @return String containing the charge setting.
     */
    private String addChargeSetting(double aPH, String aCode, ChargeSetting aSetting) {
        if (aPH <= aSetting.getpKs()) {
            if (aSetting.isProtonatedCharged()) {
                if (!aCode.contains("{")) {
                    aCode += "{";
                }
                aCode += aSetting.getChargeArgument();
            }
        } else {
            if (!aSetting.isProtonatedCharged()) {
                if (!aCode.contains("{")) {
                    aCode += "{";
                }
                aCode += aSetting.getChargeArgument();
            }
        }
        return aCode;
    }

    //
    // Properties //////////////////////////////////////////////////////////////
    /**
     * Sets the charge setting of current amino acid.
     *
     * @param aChargeSettings Line notation:
     * ChargeType pKs ? protonatedParticle : deprotonatedParticle;
     * @throws DpdPeptideException DpdPeptideException
     */
    public void setChargeSettings(String aChargeSettings) throws DpdPeptideException {
        // Handle charge settings
        this.chargeSettings = new ArrayList<ChargeSetting>();
        String[] tmpSettings = aChargeSettings.split(";");
        for (String tmpSetting : tmpSettings) {
            ChargeType tmpType = Enum.valueOf(ChargeType.class, tmpSetting.substring(0, tmpSetting.indexOf("&")));
            if (tmpType.equals(ChargeType.SS)) {
                String tmpParticle = tmpSetting.substring(tmpSetting.indexOf("&") + 1, tmpSetting.indexOf("?"));
                String tmpDisulfideBondParticle = tmpSetting.substring(tmpSetting.indexOf("?") + 1);
                this.chargeSettings.add(new ChargeSetting(tmpType, 0, tmpDisulfideBondParticle, tmpParticle, null, false));
            } else {
                double tmpPKs = Double.parseDouble(tmpSetting.substring(tmpSetting.indexOf("&") + 1, tmpSetting.indexOf("?")));
                String tmpProtonatedParticle = tmpSetting.substring(tmpSetting.indexOf("?") + 1, tmpSetting.indexOf(":"));
                String tmpDeprotonatedParticle = tmpSetting.substring(tmpSetting.indexOf(":") + 1);
                Character tmpCharge;
                boolean tmpIsProtonated;
                if (tmpProtonatedParticle.endsWith("+") || tmpProtonatedParticle.contains("-")) {
                    tmpCharge = tmpProtonatedParticle.contains("+") ? '+' : '-';
                    tmpIsProtonated = true;
                    // Old code:
                    // tmpProtonatedParticle = tmpProtonatedParticle.replaceAll("\\+", "").replaceAll("-", "");
                    tmpProtonatedParticle = StringUtils.replace(StringUtils.replace(tmpProtonatedParticle, "+", ""), "-", "");
                } else {
                    tmpCharge = tmpDeprotonatedParticle.contains("+") ? '+' : '-';
                    tmpIsProtonated = false;
                    // Old code:
                    // tmpDeprotonatedParticle = tmpDeprotonatedParticle.replaceAll("\\+", "").replaceAll("-", "");
                    tmpDeprotonatedParticle = StringUtils.replace(StringUtils.replace(tmpDeprotonatedParticle, "+", ""), "-", "");
                }
                this.chargeSettings.add(new ChargeSetting(tmpType, tmpPKs, tmpDeprotonatedParticle, tmpProtonatedParticle, tmpCharge, tmpIsProtonated));
            }
        }
    }

    /**
     *
     * @return Gets the charge settings of current amino acid.
     */
    public ArrayList<ChargeSetting> getChargeSettings() {
        return this.chargeSettings;
    }

    /**
     * Returns the name of the amino acid.
     * 
     * @return Gets the name of the amino acid.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the amino acid.
     *
     * @param aName Name of the amino acid
     */
    public void setName(String aName) {
        this.name = aName;
    }

    /**
     *
     * @return Gets the one letter code of the amino acid.
     */
    public String getOneLetterCode() {
        return this.oneLetter;
    }

    /**
     * Gets the charged one letter code for given amino acid depending on given
     * pH-value. Thereby the amino acid will be treated as free.
     *
     * @param aPH given pH-value
     * @return One letter code with charge arguments of current amino acid.
     */
    public String getOneLetterCodeCharged(double aPH) {
        return this.getOneLetterCodeCharged(aPH, SidechainBehaviour.Free, false, false);
    }

    /**
     * Gets the charged one letter code for given amino acid depending on given
     * pH-value.
     *
     * @param aPH given pH-value
     * @param aSidechainBehaviour Behaviour of the side chain (free, bound,
     * ignore)
     * @param anIsTerminalCBound Is amino acid bound over the terminal C-group
     * @param anIsTerminalNBound Is amino acid bound over the terminal N-group
     * @return One letter code with charge arguments of current amino acid.
     */
    public String getOneLetterCodeCharged(double aPH, SidechainBehaviour aSidechainBehaviour, boolean anIsTerminalCBound, boolean anIsTerminalNBound) {
        String tmpCode = this.oneLetter;
        for (ChargeSetting setting : this.chargeSettings) {
            switch (setting.getType()) {
                case TC:
                    if (!anIsTerminalCBound) {
                        tmpCode = this.addChargeSetting(aPH, tmpCode, setting);
                    }
                    break;
                case TN:
                    if (!anIsTerminalNBound) {
                        tmpCode = this.addChargeSetting(aPH, tmpCode, setting);
                    }
                    break;
                case SCF:
                    if (aSidechainBehaviour == SidechainBehaviour.Free) {
                        tmpCode = this.addChargeSetting(aPH, tmpCode, setting);
                    }
                    break;
                case SCB:
                    if (aSidechainBehaviour == SidechainBehaviour.Bound) {
                        tmpCode = this.addChargeSetting(aPH, tmpCode, setting);
                    }
                    break;
                default:
                    break;
            }
        }
        if (tmpCode.contains("{")) {
            tmpCode += "}";
        }
        return tmpCode;
    }

    /**
     * Checks whether given charge argument is valid for current amino acid.
     *
     * @param aChargeArgument Given charge argument
     * @return True if argument is valid.
     */
    public boolean isValidChargeArgument(String aChargeArgument) {
        String[] tmpArguments = this.partitionizeChargeArgument(aChargeArgument);
        for (String tmpArgument : tmpArguments) {
            boolean tmpFound = false;
            for (ChargeSetting tmpChargeSetting : this.chargeSettings) {
                if (tmpArgument.equals(tmpChargeSetting.getChargeArgument())) {
                    tmpFound = true;
                    break;
                }
            }
            if (!tmpFound) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the one letter code of the amino acid.
     *
     * @param anOneLetter One letter code
     * @throws DpdPeptideException DpdPeptideException
     */
    public final void setOneLetterCode(String anOneLetter) throws DpdPeptideException {
        if (!this.oneLetterPattern.matcher(anOneLetter).matches()) {
            throw DpdPeptideException.NotValidAminoAcidException(anOneLetter);
        }
        this.oneLetter = anOneLetter.toUpperCase();
    }

    /**
     * Returns the particle Spices of this amino acid.
     *
     * @return Gets the particle Spices of this amino acid.
     */
    public String getSpicesString() {
        return this.spicesString;
    }

    /**
     * Returns the particle Spices of this amino acid in disulfide bond.
     *
     * @return Gets the particle Spices of this amino acid in disulfide bond.
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException DpdPeptideException
     */
    public String getSpicesInDisulfideBond() throws DpdPeptideException {
        return this.getSpicesInDisulfideBond(this.spicesString);
    }

    /**
     * Returns the backbone forming string. Per definition it is the first
     * particle.
     *
     * @return Gets the backbone forming string. Per definition it is the first
     * particle.
     */
    public String getBackboneParticle() {
        if (!this.spicesString.contains("(") && !this.spicesString.contains("-")) {
            return this.spicesString;
        }
        return this.spicesString.split("\\(|\\-")[0];
    }

    /**
     * Gets the charged particle Spices of this amino acid. NOTE: This method
     * is only used for test cases.
     *
     * @param aPH pH-Value
     * @param aSidechainBehaviour Sets the behaviour of the side chain (free,
     * bound, ignore).
     * @param anIsTerminalCBound True if the terminal C amino bond exists
     * @param anIsTerminalNBound True if the terminal N amino bond exists
     * @return Spices with charged particles.
     */
    public String getChargedSpicesForTesting(double aPH, SidechainBehaviour aSidechainBehaviour, boolean anIsTerminalCBound, boolean anIsTerminalNBound) {
        int tmpIndex;
        String tmpSpices = this.spicesString;
        for (ChargeSetting setting : this.chargeSettings) {
            switch (setting.getType()) {
                case TC:
                    if (!anIsTerminalCBound) {
                        if (tmpSpices.lastIndexOf("-") >= 0 && tmpSpices.lastIndexOf("-") > tmpSpices.lastIndexOf(")")) {
                            tmpSpices = tmpSpices.substring(0, tmpSpices.lastIndexOf("-") + 1);
                        } else {
                            tmpSpices += "-";
                        }
                        if (aPH <= setting.getpKs()) {
                            tmpSpices += setting.getProtonatedParticle();
                        } else {
                            tmpSpices += setting.getDeprotonatedParticle();
                        }
                    }
                    break;
                case TN:
                    if (!anIsTerminalNBound) {
                        if ((tmpSpices.indexOf("-") < tmpSpices.indexOf("(") && tmpSpices.contains("-"))
                                || (tmpSpices.contains("-") && !tmpSpices.contains("("))) {
                            tmpIndex = tmpSpices.indexOf("-");
                        } else {
                            tmpIndex = tmpSpices.indexOf("(");
                        }
                        if (tmpIndex >= 0) {
                            if (aPH <= setting.getpKs()) {
                                tmpSpices = setting.getProtonatedParticle() + tmpSpices.substring(tmpIndex);
                            } else {
                                tmpSpices = setting.getDeprotonatedParticle() + tmpSpices.substring(tmpIndex);
                            }
                        } else {
                            if (aPH <= setting.getpKs()) {
                                tmpSpices = setting.getProtonatedParticle();
                            } else {
                                tmpSpices = setting.getDeprotonatedParticle();
                            }
                        }
                    }
                    break;
                case SCF:
                    if (aSidechainBehaviour == SidechainBehaviour.Free) {
                        tmpIndex = tmpSpices.lastIndexOf(")");
                        if (tmpIndex < 0) {
                            break;
                        }
                        int tmpIndexB = tmpSpices.substring(0, tmpIndex).lastIndexOf("-") > tmpSpices.substring(0, tmpIndex).lastIndexOf("(")
                                ? tmpSpices.substring(0, tmpIndex).lastIndexOf("-") : tmpSpices.substring(0, tmpIndex).lastIndexOf("(");
                        String tmpParticle = aPH <= setting.getpKs()
                                ? setting.getProtonatedParticle() : setting.getDeprotonatedParticle();
                        tmpSpices = tmpSpices.substring(0, tmpIndexB + 1) + tmpParticle + tmpSpices.substring(tmpIndex);
                    }
                    break;
                case SCB:
                    if (aSidechainBehaviour == SidechainBehaviour.Bound) {
                        tmpIndex = tmpSpices.lastIndexOf(")");
                        int tmpIndexB = tmpSpices.substring(0, tmpIndex).lastIndexOf("-") > tmpSpices.substring(0, tmpIndex).lastIndexOf("(")
                                ? tmpSpices.substring(0, tmpIndex).lastIndexOf("-") : tmpSpices.substring(0, tmpIndex).lastIndexOf("(");
                        String tmpParticle = aPH <= setting.getpKs()
                                ? setting.getProtonatedParticle() : setting.getDeprotonatedParticle();
                        tmpSpices = tmpSpices.substring(0, tmpIndexB + 1) + tmpParticle + tmpSpices.substring(tmpIndex);
                    }
                    break;
                default:
                    break;
            }
        }
        return tmpSpices;
    }

    /**
     * Gets the charged Spices depending on given charge argument for this
     * amino acid.
     *
     * @param aChargeArgument A charge argument (S+C-N+, ...)
     * @return Charged Spices
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException DpdPeptideException
     */
    public String getChargedSpices(String aChargeArgument) throws DpdPeptideException {
        return this.getChargedSpices(aChargeArgument, false, false, false);
    }

    /**
     * Gets the charged Spices depending on given charge argument for this
     * amino acid.
     *
     * @param aChargeArgument A charge argument (S+C-N+, ...)
     * @param anIsFirstAminoAcid True: Amino acid is first amino acid, false:
     * Otherwise
     * @param anIsLastAminoAcid True: Amino acid is last amino acid, false:
     * Otherwise
     * @param anIsDisulfideBond True: Amino acid is in disulfide bond, false:
     * Otherwise
     * @return Charged Spices
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException DpdPeptideException
     */
    public String getChargedSpices(String aChargeArgument, boolean anIsFirstAminoAcid, boolean anIsLastAminoAcid, boolean anIsDisulfideBond) throws DpdPeptideException {
        int tmpIndex;
        String tmpSpices = this.spicesString;
        String[] tmpArguments = this.partitionizeChargeArgument(aChargeArgument);
        for (String tmpArgument : tmpArguments) {
            for (ChargeSetting tmpChargeSetting : this.chargeSettings) {
                if (tmpArgument.equals(tmpChargeSetting.getChargeArgument())) {
                    switch (tmpChargeSetting.getType()) {
                        case TC:
                            tmpSpices += "-";
                            tmpSpices += tmpChargeSetting.getChargedParticle();
                            if (anIsDisulfideBond) {
                                tmpSpices = this.getSpicesInDisulfideBond(tmpSpices);
                            }
                            break;
                        case TN:
                            if (this.aminoAcidsUtility.isOnlySingleParticleAminoAcids()) {
                                // Single-particle amino acids: Charged particle must be FIRST!
                                tmpSpices = tmpChargeSetting.getChargedParticle() + "-" + tmpSpices;
                            } else {
                                if (this.isSingleParticleSpices) {
                                    tmpSpices = tmpChargeSetting.getChargedParticle();
                                } else {
                                    if (tmpSpices.indexOf("-") < tmpSpices.indexOf("(") && tmpSpices.contains("-") || !tmpSpices.contains("(")) {
                                        tmpIndex = tmpSpices.indexOf("-");
                                    } else {
                                        tmpIndex = tmpSpices.indexOf("(");
                                    }
                                    if (tmpIndex < 0) {
                                        tmpSpices = tmpChargeSetting.getChargedParticle();
                                    } else {
                                        tmpSpices = tmpChargeSetting.getChargedParticle() + tmpSpices.substring(tmpIndex);
                                    }
                                }
                            }
                            if (anIsDisulfideBond) {
                                tmpSpices = this.getSpicesInDisulfideBond(tmpSpices);
                            }
                            break;
                        case SCB:
                            if (this.isSingleParticleSpices) {
                                if (anIsFirstAminoAcid || anIsLastAminoAcid) {
                                    tmpIndex = tmpSpices.lastIndexOf("-");
                                    if (tmpIndex < 0) {
                                        tmpSpices = tmpChargeSetting.getChargedParticle();
                                    } else {
                                        // Old code:
                                        // tmpSpices = tmpSpices.replaceAll(tmpChargeSetting.getUnchargedParticle(), tmpChargeSetting.getChargedParticle());
                                        tmpSpices = StringUtils.replace(tmpSpices, tmpChargeSetting.getUnchargedParticle(), tmpChargeSetting.getChargedParticle());
                                    }
                                } else {
                                    tmpSpices = tmpChargeSetting.getChargedParticle();
                                }
                            } else {
                                tmpIndex = tmpSpices.lastIndexOf(")");
                                int tmpIndexB = tmpSpices.substring(0, tmpIndex).lastIndexOf("-") > tmpSpices.substring(0, tmpIndex).lastIndexOf("(")
                                        ? tmpSpices.substring(0, tmpIndex).lastIndexOf("-") : tmpSpices.substring(0, tmpIndex).lastIndexOf("(");
                                tmpSpices = tmpSpices.substring(0, tmpIndexB + 1) + tmpChargeSetting.getChargedParticle() + tmpSpices.substring(tmpIndex);
                            }
                            break;
                        default:
                            break;

                    }
                }
            }
        }
        return tmpSpices;
    }

    /**
     * Sets the particle Spices of the amino acid.
     *
     * @param aSpices Spices
     * @throws DpdPeptideException DpdPeptideException
     */
    public final void setSpices(String aSpices) throws DpdPeptideException {
        this.isSingleParticleSpices = this.singleParticlePattern.matcher(aSpices).matches();
        if (this.isSingleParticleSpices) {
            this.spicesString = aSpices;
        } else {
            if (this.sideChainParticlePattern.matcher(aSpices).matches()) {
                this.spicesString = aSpices;
            } else {
                throw DpdPeptideException.InvalidAminoAcidSpicesException(aSpices);
            }
        }
    }

    /**
     * Returns if amino acid is represented by single-particle Spices.
     *
     * @return True: Amino acid is represented by single-particle Spices,
     * false: Otherwise
     */
    public boolean isSingleParticleSpices() {
        return this.isSingleParticleSpices;
    }

    /**
     * Returns the three letter code of the amino acid.
     *
     * @return Gets the three letter code of the amino acid.
     */
    public String getThreeLetterCode() {
        return this.threeLetter;
    }

    /**
     * Sets the three letter code of the amino acid.
     *
     * @param aThreeLetter A three letter code
     * @throws DpdPeptideException DpdPeptideException
     */
    public final void setThreeLetterCode(String aThreeLetter) throws DpdPeptideException {
        if (!this.threeLetterPattern.matcher(aThreeLetter).matches()) {
            throw DpdPeptideException.NotValidAminoAcidException(aThreeLetter);
        }
        this.threeLetter = aThreeLetter.toUpperCase();
    }

    /**
     * Gets the charge setting defined by given charge type.
     *
     * @param aChargeType Charge type.
     * @return Corresponding cahrge setting, null if not found.
     */
    public ChargeSetting getChargeSetting(ChargeType aChargeType) {
        for (ChargeSetting tmpChargeSetting : this.getChargeSettings()) {
            if (tmpChargeSetting.getType().equals(aChargeType)) {
                return tmpChargeSetting;
            }
        }
        return null;
    }

    /**
     * Returns amino acid definition string
     *
     * @return Amino acid definition string
     */
    public String getAminoAcidDefinition() {
        return this.aminoAcidDefinition;
    }

    // Private methods
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Returns Spices in disulfide bond
     *
     * @param aSpices to be converted to be in disulfide bond
     * @return Spices in disulfide bond
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException
     */
    private String getSpicesInDisulfideBond(String aSpices) throws DpdPeptideException {
        AminoAcid tmpCystein = this.aminoAcidsUtility.getAminoAcidFromOneLetterCode('C');
        String tmpParticle = tmpCystein.getChargeSetting(ChargeType.SS).getProtonatedParticle();
        String tmpDisulfideBondParticle = tmpCystein.getChargeSetting(ChargeType.SS).getDeprotonatedParticle();
        // Old code: 
        // return aSpices.replaceAll(tmpParticle, tmpDisulfideBondParticle);
        return StringUtils.replace(aSpices, tmpParticle, tmpDisulfideBondParticle);
    }
}
