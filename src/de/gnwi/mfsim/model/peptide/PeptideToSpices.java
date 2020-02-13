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
package de.gnwi.mfsim.model.peptide;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.peptide.base.AminoAcid;
import de.gnwi.mfsim.model.peptide.base.AminoAcids;
import de.gnwi.mfsim.model.peptide.base.AminoAcidsUtility;
import de.gnwi.mfsim.model.peptide.base.ChargeSetting;
import de.gnwi.mfsim.model.peptide.base.ChargeType;
import de.gnwi.mfsim.model.peptide.base.SidechainBehaviour;
import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for converting peptide definitions into to a particle-based SPICES.
 *
 * @author Andreas Truszkowski
 */
public class PeptideToSpices {

    // Fields
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Enum representing the amino acid code types.
     */
    private enum CodeType {

        TYPE_THREE_LETTER, TYPE_ONE_LETTER

    }

    /**
     * Regex pattern specifying allowed signs in one letter peptide definitions.
     */
    private Pattern peptideAllowedOneLetterCharactersPattern = Pattern.compile("[\\[\\]0-9ARNDCQEGHILKMFPSTWYV]");

    /**
     * Amino acids utility
     */
    private AminoAcidsUtility aminoAcidsUtility = null;

    // Constructor
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a new instance.
     *
     * @throws DpdPeptideException DpdPeptideException
     */
    public PeptideToSpices() throws DpdPeptideException {
        this.aminoAcidsUtility = new AminoAcidsUtility(AminoAcids.getInstance().getAminoAcidsUtility().getAminoAcidsDefinition());
    }

    /**
     * Creates a new instance.
     *
     * @param anAminoAcidsUtility Amino acids utility
     * @throws DpdPeptideException DpdPeptideException
     */
    public PeptideToSpices(AminoAcidsUtility anAminoAcidsUtility) throws DpdPeptideException {
        if (anAminoAcidsUtility == null || !anAminoAcidsUtility.isCompletelyInitialised()) {
            throw new DpdPeptideException("Peptide.MissingAminoAcidData");
        }
        this.aminoAcidsUtility = anAminoAcidsUtility;
    }

    // Methods
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Checks whether an correct amino acid is located at the defined index in
     * the given character sequence.
     *
     * @param aSequence Peptide character sequence
     * @param anIndex Position in the sequence.
     * @param aCodeType The amino acid code type.
     * @return True when a correct amino acid could be identified.
     */
    private boolean checkAminoAcid(char[] aSequence, int anIndex, CodeType aCodeType) throws DpdPeptideException {
        if (aCodeType == CodeType.TYPE_ONE_LETTER) {
            return this.aminoAcidsUtility.isOneLetterAminoAcid(aSequence[anIndex]);
        } else {
            String tmpAminoAcid = this.getThreeLetterAminoAcidFromSequence(aSequence, anIndex);
            return this.aminoAcidsUtility.isThreeLetterAminoAcid(tmpAminoAcid);
        }

    }

    /**
     * Checks whether given peptide definition in one letter code is valid.
     *
     * @param aDefinition The peptide definition. See description for further
     * details.
     * @return An error message or an empty string when no errors could be
     * found.
     */
    public String checkPeptideDefinitonOneLetterCode(String aDefinition) {
        return this.checkPeptideDefiniton(aDefinition, CodeType.TYPE_ONE_LETTER);
    }

    /**
     * Checks whether given peptide definition in three letter code is valid.
     *
     * @param aDefinition The peptide definition. See description for further
     * details.
     * @return An error message or an empty string when no errors could be
     * found.
     */
    public String checkPeptideDefinitonThreeLetterCode(String aDefinition) {
        return this.checkPeptideDefiniton(aDefinition, CodeType.TYPE_THREE_LETTER);
    }

    /**
     * Checks whether given peptide definition is valid.
     *
     * @param aDefinition The peptide definition. See description for further
     * details.
     * @param aCodeType The amino acid code type.
     * @throws DpdPeptideException Thrown when the definition contains errors.
     */
    private String checkPeptideDefiniton(String aDefinition, CodeType aCodeType) {
        if (aDefinition.isEmpty()) {
            return "";
        }
        // Old code:
        // char[] tmpDefinition = aDefinition.toUpperCase().replaceAll("\\s+", "").toCharArray();
        char[] tmpDefinition = StringUtils.deleteWhitespace(aDefinition.toUpperCase()).toCharArray();
        int tmpIndex = 0;
        int tmpCountRingClosures = 0;
        HashMap<Integer, Integer> tmpDisulfideBondMap = new HashMap<Integer, Integer>();
        boolean tmpIsFirstAminoAcid = true;
        int tmpIndexOfLastAminoAcid = 0;
        while (tmpIndex < tmpDefinition.length) {
            if (!Character.isLetter(tmpDefinition[tmpIndex])) {
                tmpIndex++;
            } else {
                tmpIndexOfLastAminoAcid = tmpIndex;
                if (aCodeType == CodeType.TYPE_ONE_LETTER) {
                    tmpIndex++;
                } else {
                    tmpIndex += 3;
                }
            }
        }
        tmpIndex = 0;
        while (tmpIndex < tmpDefinition.length) {
            try {
                // if current aa def starts with a digit
                // -> Skip all digits
                // -> next char has to be a valid aa
                if (Character.isDigit(tmpDefinition[tmpIndex])) {
                    while (Character.isDigit(tmpDefinition[tmpIndex])) {
                        tmpIndex++;
                    }
                    if (!this.checkAminoAcid(tmpDefinition, tmpIndex, aCodeType)) {
                        return String.format(ModelMessage.get("Peptide.SyntaxError"), tmpIndex + 1);
                    }
                    // Handle ")" Bracket
                } else if (!this.checkAminoAcid(tmpDefinition, tmpIndex, aCodeType)) {
                    return String.format(ModelMessage.get("Peptide.SyntaxError"), tmpIndex + 1);
                }
                if (aCodeType == CodeType.TYPE_ONE_LETTER) {
                    tmpIndex++;
                } else {
                    tmpIndex += 3;
                }
                if (tmpIndex >= tmpDefinition.length) {
                    break;
                }
                // Handle "[" Bracket
                if (Character.isDigit(tmpDefinition[tmpIndex]) || this.checkAminoAcid(tmpDefinition, tmpIndex, aCodeType)) {
                    tmpIsFirstAminoAcid = false;
                    continue;
                } else if (tmpDefinition[tmpIndex] == '[' || tmpDefinition[tmpIndex] == '{') {
                    while (tmpIndex < tmpDefinition.length && (tmpDefinition[tmpIndex] == '[' || tmpDefinition[tmpIndex] == '{')) {
                        if (tmpDefinition[tmpIndex] == '[') {
                            tmpIndex++;
                            // Next sign has to be a digit.
                            if (Character.isDigit(tmpDefinition[tmpIndex])) {
                                // Preceding amino acid has to be a Cystein (C) but note: There may be a charge definition in {}-brackets
                                int tmpInnerIndex = tmpIndex;
                                boolean tmpIsInnerIndexDecrease = true;
                                boolean tmpIsChargeDefinition = false;
                                while (tmpIsInnerIndexDecrease || tmpIsChargeDefinition) {
                                    if (tmpDefinition[tmpInnerIndex] == '}') {
                                        tmpIsChargeDefinition = true;
                                    }
                                    if (tmpDefinition[tmpInnerIndex] == '{') {
                                        tmpIsChargeDefinition = false;
                                    }
                                    if (!tmpIsChargeDefinition && this.checkAminoAcid(tmpDefinition, tmpInnerIndex, aCodeType)) {
                                        tmpIsInnerIndexDecrease = false;
                                    } else {
                                        tmpInnerIndex--;
                                    }
                                }
                                if (aCodeType == CodeType.TYPE_ONE_LETTER) {
                                    if (tmpDefinition[tmpInnerIndex] != 'C') {
                                        return String.format(ModelMessage.get("Peptide.IncorrectDisulfideBondDefinition"), tmpIndex + 1);
                                    }
                                } else {
                                    String tmpAminoAcid = this.getThreeLetterAminoAcidFromSequence(tmpDefinition, tmpInnerIndex);
                                    if (!tmpAminoAcid.equalsIgnoreCase("cys")) {
                                        return String.format(ModelMessage.get("Peptide.IncorrectDisulfideBondDefinition"), tmpIndex + 1);
                                    }
                                }
                                // Count number of defintions
                                Integer tmpBondIndex = Integer.parseInt(Character.toString(tmpDefinition[tmpIndex]));
                                Integer tmpValue = tmpDisulfideBondMap.get(tmpBondIndex);
                                if (tmpValue == null) {
                                    tmpValue = 0;
                                }
                                tmpValue++;
                                tmpDisulfideBondMap.put(tmpBondIndex, tmpValue);
                                tmpIndex++;
                                if (tmpDefinition[tmpIndex] != ']') {
                                    return String.format(ModelMessage.get("Peptide.SyntaxError"), tmpIndex + 1);
                                }
                                tmpIndex++;
                            } else if (tmpDefinition[tmpIndex] == '*') {
                                if (!tmpIsFirstAminoAcid && tmpIndex < tmpIndexOfLastAminoAcid) {
                                    return String.format(ModelMessage.get("Peptide.ErrorIllegalRingClosure"), tmpIndex + 1);
                                }
                                tmpIndex++;
                                if (tmpDefinition[tmpIndex] != ']') {
                                    return String.format(ModelMessage.get("Peptide.SyntaxError"), tmpIndex + 1);
                                }
                                tmpCountRingClosures++;
                                tmpIndex++;
                            }
                        } else if (tmpDefinition[tmpIndex] == '{') {
                            AminoAcid tmpAminoAcid;
                            if (aCodeType == CodeType.TYPE_ONE_LETTER) {
                                tmpAminoAcid = this.aminoAcidsUtility.getAminoAcidFromOneLetterCode(tmpDefinition[tmpIndex - 1]);
                            } else {
                                String tmpCode = "";
                                for (int i = tmpIndex - 3; i < tmpIndex; i++) {
                                    tmpCode += tmpDefinition[i];
                                }
                                tmpAminoAcid = this.aminoAcidsUtility.getAminoAcidFromThreeLetterCode(tmpCode);
                            }
                            String tmpChargeArgument = "";
                            tmpIndex++;
                            while (tmpDefinition[tmpIndex] != '}') {
                                tmpChargeArgument += tmpDefinition[tmpIndex];
                                tmpIndex++;
                            }
                            if (!tmpAminoAcid.isValidChargeArgument(tmpChargeArgument)) {
                                return String.format(ModelMessage.get("Peptide.IncorrectChargeArgument"), tmpChargeArgument);
                            }
                            tmpIndex++;
                        }
                    }
                } else {
                    return String.format(ModelMessage.get("Peptide.SyntaxError"), tmpIndex + 1);
                }
                tmpIsFirstAminoAcid = false;
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                return String.format(ModelMessage.get("Peptide.SyntaxError"), tmpIndex + 1);
            }
        }
        if (tmpCountRingClosures != 0 && tmpCountRingClosures != 2) {
            return ModelMessage.get("Peptide.ErrorIllegalNumberRingClosures");
        }
        // Check whther there are exact two aas in disulfide bond
        for (Entry<Integer, Integer> tmpEntry : tmpDisulfideBondMap.entrySet()) {
            Integer tmpKey = tmpEntry.getKey();
            Integer tmpValue = tmpEntry.getValue();
            if (tmpValue != 2) {
                return String.format(ModelMessage.get("Peptide.IncorrectDisulfideBondCount"), tmpKey);
            }
        }
        return "";
    }

    /**
     * Gets a three character subsequence from given peptide character sequence.
     *
     * @param aSequence The peptide character sequence.
     * @param anIndex Position in the sequence.
     * @return THree character long subsequence.
     */
    private String getThreeLetterAminoAcidFromSequence(char[] aSequence, int anIndex) {
        char[] tmpAminoAcidarray = Arrays.copyOfRange(aSequence, anIndex, anIndex + 3);
        String tmpAminoAcid = "";
        for (char tmpChar : tmpAminoAcidarray) {
            tmpAminoAcid += tmpChar;
        }
        return tmpAminoAcid;
    }

    /**
     * Converts given peptide defintion from three letter code into one letter
     * code.
     *
     * @param aDefinition The peptide definition. See description for further
     * details.
     * @return Peptide defintion in one letter code.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertOneToThreeLetterDefinition(String aDefinition) throws DpdPeptideException {
        boolean tmpIsChargeBlock = false;
        if (aDefinition == null || aDefinition.isEmpty()) {
            return "";
        }
        String tmpConvertedSequence = "";
        // Old code:
        // char[] tmpDefinition = aDefinition.replaceAll("\\s+", "").toUpperCase().toCharArray();
        char[] tmpDefinition = StringUtils.deleteWhitespace(aDefinition).toUpperCase().toCharArray();
        int tmpIndex = 0;
        while (tmpIndex < tmpDefinition.length) {
            if (Character.isLetter(tmpDefinition[tmpIndex]) && !tmpIsChargeBlock) {
                String tmpThreeLetterCode = this.aminoAcidsUtility.convertOneToThreeLetterCode(tmpDefinition[tmpIndex]);
                tmpThreeLetterCode = Character.toUpperCase(tmpThreeLetterCode.charAt(0)) + tmpThreeLetterCode.substring(1).toLowerCase();
                tmpConvertedSequence += tmpThreeLetterCode;
                if (tmpIndex != tmpDefinition.length - 1 && tmpDefinition[tmpIndex + 1] != '{') {
                    tmpConvertedSequence += " ";
                }
            } else {
                // do not convert digits and special signs
                tmpConvertedSequence += tmpDefinition[tmpIndex];
                if (tmpDefinition[tmpIndex] == '{') {
                    tmpIsChargeBlock = true;
                } else if (tmpDefinition[tmpIndex] == '}') {
                    tmpIsChargeBlock = false;
                    if (tmpIndex != tmpDefinition.length - 1) {
                        tmpConvertedSequence += " ";
                    }
                }
            }
            tmpIndex++;
        }
        return tmpConvertedSequence;
    }

    /**
     * Converts given one letter peptide defintion in a fragemnt based Spices.
     *
     * @param aDefinition The peptide definition. See description for further
     * details.
     * @return Particle Spices.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertOneLetterPeptideToSpices(String aDefinition) throws DpdPeptideException {
        return this.convertOneLetterPeptideToSpices(aDefinition, false);
    }

    /**
     * Converts given one letter peptide defintion in a fragemnt based Spices.
     *
     * @param aDefinition The peptide definition. See description for further
     * details.
     * @param aDoCarriageReturn True if after every amino acid a carriage return
     * shall be added.
     * @return Particle Spices.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertOneLetterPeptideToSpices(String aDefinition, boolean aDoCarriageReturn) throws DpdPeptideException {
        String tmpSpices = "";
        if (!this.aminoAcidsUtility.isCompletelyInitialised()) {
            throw new DpdPeptideException("Peptide.MissingAminoAcidData");
        }
        // Old code:
        // char[] tmpDefinition = aDefinition.replaceAll("\\s+", "").toCharArray();
        char[] tmpDefinition = StringUtils.deleteWhitespace(aDefinition).toCharArray();
        int tmpIndex = 0;
        int tmpLastAminoAcidIndex = 0;
        boolean tmpIsFirstAminoAcid = true;
        boolean tmpIsChargeArgument = false;
        boolean tmpIsCircular = aDefinition.contains("[*]");
        boolean tmpFirstCircularIndexAdded = false;
        for (int i = 0; i < tmpDefinition.length; i++) {
            if (tmpDefinition[i] == '{') {
                tmpIsChargeArgument = true;
            } else if (tmpDefinition[i] == '}') {
                tmpIsChargeArgument = false;
            } else if (Character.isLetter(tmpDefinition[i]) && !tmpIsChargeArgument) {
                tmpLastAminoAcidIndex = i;
            }
        }
        // Find used ring indices
        int tmpFreeRingIndex = 0;
        for (int i = 0; i < tmpDefinition.length; i++) {
            if (tmpDefinition[i] == '[' && tmpDefinition[i + 1] != '*') {
                i++;
                String tmpDigits = "";
                while (tmpDefinition[i] != ']') {
                    tmpDigits += tmpDefinition[i];
                    i++;
                }
                int tmpRingIndex = Integer.valueOf(tmpDigits);
                if (tmpFreeRingIndex < tmpRingIndex) {
                    tmpFreeRingIndex = tmpRingIndex;
                }
            }
        }
        tmpFreeRingIndex++;
        while (tmpIndex < tmpDefinition.length) {
            try {
                // Detect disulfide bonds
                boolean tmpIsDisulfideBond = false;
                // Pattern "C[" but not "C[*"
                if (tmpDefinition[tmpIndex] == 'C' && tmpDefinition.length > tmpIndex + 2 && tmpDefinition[tmpIndex + 1] == '[' && tmpDefinition[tmpIndex + 2] != '*') {
                    tmpIsDisulfideBond = true;
                }
                // Pattern "C.*.["
                if (tmpDefinition[tmpIndex] == 'C' && tmpDefinition.length > tmpIndex + 4 && tmpDefinition[tmpIndex + 2] == '*' && tmpDefinition[tmpIndex + 4] == '[') {
                    tmpIsDisulfideBond = true;
                }
                // Pattern "C{..}["
                if (tmpDefinition[tmpIndex] == 'C' && tmpDefinition.length > tmpIndex + 5 && tmpDefinition[tmpIndex + 4] == '}' && tmpDefinition[tmpIndex + 5] == '[') {
                    tmpIsDisulfideBond = true;
                }
                int tmpFrequency = 1;
                if (Character.isDigit(tmpDefinition[tmpIndex])) {
                    String tmpPrefix = "";
                    while (Character.isDigit(tmpDefinition[tmpIndex])) {
                        tmpPrefix += tmpDefinition[tmpIndex];
                        tmpIndex++;
                    }
                    tmpFrequency = Integer.parseInt(tmpPrefix);
                }
                if (this.aminoAcidsUtility.isOneLetterAminoAcid(tmpDefinition[tmpIndex])) {
                    String tmpChargeSetting = null;
                    Character tmpAminoAcidCode = tmpDefinition[tmpIndex];
                    int tmpAminoAcidIndex = tmpIndex;
                    if (tmpIndex < tmpDefinition.length - 1 && tmpDefinition[tmpIndex + 1] == '{') {
                        tmpIndex += 2;
                        tmpChargeSetting = "";
                        while (tmpDefinition[tmpIndex] != '}') {
                            tmpChargeSetting += tmpDefinition[tmpIndex];
                            tmpIndex++;
                        }
                    }
                    for (int i = 0; i < tmpFrequency; i++) {
                        boolean tmpIsLastAminoAcid = tmpAminoAcidIndex == tmpLastAminoAcidIndex && i == tmpFrequency - 1;
                        tmpSpices += this.addAminoAcid(tmpAminoAcidCode, tmpChargeSetting, tmpIsFirstAminoAcid, tmpIsLastAminoAcid, tmpIsDisulfideBond, tmpIsCircular);
                        tmpIsFirstAminoAcid = false;
                        if (aDoCarriageReturn && (!tmpIsLastAminoAcid || i < tmpFrequency - 1)) {
                            tmpSpices += "\n";
                        }
                        if (i < tmpFrequency - 1) {
                            tmpSpices += "-";
                        }
                    }
                } else if (tmpDefinition[tmpIndex] == '[') {
                    if (tmpDefinition[tmpIndex + 1] == '*') {
                        int tmpInsertIndex = -1;
                        tmpInsertIndex = tmpSpices.lastIndexOf('(');
                        if (tmpInsertIndex < 0 && tmpSpices.endsWith("\n")) {
                            tmpInsertIndex = tmpSpices.length() - 1;
                        }
                        if (tmpInsertIndex >= 0) {
                            tmpSpices = tmpSpices.substring(0, tmpInsertIndex) + "[" + tmpFreeRingIndex + "]"
                                    + tmpSpices.substring(tmpInsertIndex);
                        } else {
                            tmpSpices += "[" + tmpFreeRingIndex + "]";
                        }
                        tmpIndex += 2;
                    } else {
                        int tmpSIndex = tmpSpices.lastIndexOf(")");
                        // NOTE: There may be no sidechain that ends with ")" thus only remove "carriage return" at end
                        if (tmpSIndex < 0) {
                            // Test carriage return (CR)
                            if (tmpSpices.endsWith("\n")) {
                                tmpSIndex = tmpSpices.length() - 1;
                            } else {
                                // Last amino acid: Go to preceding particle
                                tmpSIndex = tmpSpices.lastIndexOf("-");
                                if (tmpSIndex < 0) {
                                    tmpSIndex = tmpSpices.length();
                                }
                            }
                        }
                        String tmpAlteredSpices = tmpSpices.substring(0, tmpSIndex);
                        String tmpDigits = "";
                        int tmpDigitsIndex = 1;
                        while (tmpDefinition[tmpIndex + tmpDigitsIndex] != ']') {
                            tmpDigits += tmpDefinition[tmpIndex + tmpDigitsIndex];
                            tmpDigitsIndex++;
                        }
                        tmpAlteredSpices += "[";
                        tmpAlteredSpices += tmpDigits;
                        tmpAlteredSpices += "]";
                        tmpAlteredSpices += tmpSpices.substring(tmpSIndex);
                        tmpSpices = tmpAlteredSpices;
                        tmpIndex += tmpDigits.length() + 1;
                    }
                } else {
                    throw DpdPeptideException.SyntaxErrorException(tmpIndex + 1);
                }
                tmpIndex++;
                if (tmpIndex >= tmpDefinition.length) {
                    break;
                }
                if (Character.isDigit(tmpDefinition[tmpIndex]) || this.aminoAcidsUtility.isOneLetterAminoAcid(tmpDefinition[tmpIndex])) {
                    tmpSpices += "-";
                }
            } catch (DpdPeptideException anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw anException;
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                anException.printStackTrace();
                throw DpdPeptideException.SyntaxErrorException(tmpIndex + 1);
            }
        }
        return tmpSpices;
    }

    private String addAminoAcid(Character aAminoAcidCode, String aChargeArgument, boolean aIsFirstAminoAcid, boolean aIsLastAminoAcid, boolean anIsDisulfideBond, boolean anIsCircular) throws DpdPeptideException {
        AminoAcid tmpAminoAcid = this.aminoAcidsUtility.getAminoAcidFromOneLetterCode(aAminoAcidCode);
        String tmpSpices;
        if (aChargeArgument == null) {
            if (anIsDisulfideBond) {
                tmpSpices = tmpAminoAcid.getSpicesInDisulfideBond();
            } else {
                tmpSpices = tmpAminoAcid.getSpicesString();
            }
        } else {
            tmpSpices = tmpAminoAcid.getChargedSpices(aChargeArgument, aIsFirstAminoAcid, aIsLastAminoAcid, anIsDisulfideBond);
        }
        if (aIsFirstAminoAcid && !anIsCircular) {
            if (aChargeArgument == null || !aChargeArgument.contains("N")) {
                ChargeSetting tmpChargeSetting = tmpAminoAcid.getChargeSetting(ChargeType.TN);
                String tmpFirstParticle = tmpChargeSetting.isProtonatedCharged() ? tmpChargeSetting.getDeprotonatedParticle() : tmpChargeSetting.getProtonatedParticle();
                if (tmpAminoAcid.isSingleParticleSpices()) {
                    tmpSpices = tmpFirstParticle + "-" + tmpSpices;
                } else {
                    tmpSpices = tmpSpices.replace(tmpAminoAcid.getBackboneParticle(), tmpFirstParticle);
                }
            }
        }
        if (aIsLastAminoAcid && !anIsCircular) {
            if (aChargeArgument == null || !aChargeArgument.contains("C")) {
                ChargeSetting tmpChargeSetting = tmpAminoAcid.getChargeSetting(ChargeType.TC);
                String tmpLastParticle = tmpChargeSetting.isProtonatedCharged() ? tmpChargeSetting.getDeprotonatedParticle() : tmpChargeSetting.getProtonatedParticle();
                tmpSpices += "-" + tmpLastParticle;
            }
        }
        return tmpSpices;
    }

    /**
     * Adds charge arguments (C-, S+, N+,...) to the given one letter peptide
     * amino acid sequence depending on given pH-value. Already applied charges
     * will be removed first.
     *
     * @param aDefinition Peptide one letter amino acid sequence.
     * @param aPH The pH-value.
     * @return The charged peptide definition.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String chargeOneLetterPeptide(String aDefinition, Double aPH) throws DpdPeptideException {
        return this.chargeOneLetterPeptide(aDefinition, aPH, false);
    }

    /**
     * Adds charge arguments (C-, S+, N+,...) to the given one letter peptide
     * amino acid sequence depending on given pH-value. Already applied charges
     * will be removed first.
     *
     * @param aDefinition Peptide one letter amino acid sequence.
     * @param aPH The pH-value.
     * @param anIsCircular Is circular peptide.
     * @return The charged peptide definition.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String chargeOneLetterPeptide(String aDefinition, Double aPH, boolean anIsCircular) throws DpdPeptideException {
        String tmpCode = "";
        if (this.hasOneLetterCharge(aDefinition)) {
            aDefinition = this.removeOneLetterCharges(aDefinition);
        }
        // Old code:
        // char[] tmpDefinition = aDefinition.replaceAll("\\s+", "").toCharArray();
        char[] tmpDefinition = StringUtils.deleteWhitespace(aDefinition).toCharArray();
        if (tmpDefinition.length == 1) {
            AminoAcid tmpAminoAcid = this.aminoAcidsUtility.getAminoAcidFromOneLetterCode(tmpDefinition[0]);
            return tmpAminoAcid.getOneLetterCodeCharged(aPH);
        }
        int tmpIndex = 0;
        boolean tmpIsFirstAmnioAcid = true;
        int tmpLastAminoAcidIndex = 0;
        boolean tmpIsChargeBracketOpen = false;
        for (int i = 0; i < tmpDefinition.length; i++) {
            if (Character.isLetter(tmpDefinition[i])) {
                tmpLastAminoAcidIndex = i;
            }
        }
        tmpDefinition = this.handleFrequenciesBeforeCharging(tmpDefinition, tmpLastAminoAcidIndex).toCharArray();
        for (int i = 0; i < tmpDefinition.length; i++) {
            if (Character.isLetter(tmpDefinition[i])) {
                tmpLastAminoAcidIndex = i;
            }
        }
        while (tmpIndex < tmpDefinition.length) {
            try {
                if (this.aminoAcidsUtility.isOneLetterAminoAcid(tmpDefinition[tmpIndex]) && !tmpIsChargeBracketOpen) {
                    AminoAcid tmpAminoAcid = this.aminoAcidsUtility.getAminoAcidFromOneLetterCode(tmpDefinition[tmpIndex]);
                    SidechainBehaviour tmpSidechainBehaviour = SidechainBehaviour.Bound;
                    // Do not charge side chain if cystein is bound to disulfide bond
                    if (Character.toUpperCase(tmpDefinition[tmpIndex]) == 'C'
                            && tmpIndex < tmpDefinition.length - 1
                            && tmpDefinition[tmpIndex + 1] == '[') {
                        tmpSidechainBehaviour = SidechainBehaviour.Ignore;
                    }
                    if (tmpIsFirstAmnioAcid && !anIsCircular) {
                        tmpCode += tmpAminoAcid.getOneLetterCodeCharged(aPH, tmpSidechainBehaviour, true, false);
                        tmpIsFirstAmnioAcid = false;
                    } else if (tmpIndex == tmpLastAminoAcidIndex && !anIsCircular) {
                        tmpCode += tmpAminoAcid.getOneLetterCodeCharged(aPH, tmpSidechainBehaviour, false, true);
                    } else {
                        tmpCode += tmpAminoAcid.getOneLetterCodeCharged(aPH, tmpSidechainBehaviour, true, true);
                    }
                } else {
                    if (tmpDefinition[tmpIndex] == '{') {
                        tmpIsChargeBracketOpen = true;
                    }
                    if (tmpDefinition[tmpIndex] == '}') {
                        tmpIsChargeBracketOpen = false;
                    }
                    tmpCode += tmpDefinition[tmpIndex];
                }
                tmpIndex++;
            } catch (DpdPeptideException anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw anException;
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw DpdPeptideException.SyntaxErrorException(tmpIndex + 1);
            }
        }
        return tmpCode;
    }

    /**
     * Handles frequencies to describe correct N- or C-terminal particle charge.
     *
     * @param aDefinition Amino aced sequence in one leter code.
     * @param aLastAminoAcidIndex Index of last amino acid.
     * @return Reworked sequence.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String handleFrequenciesBeforeCharging(char[] aDefinition, int aLastAminoAcidIndex) throws DpdPeptideException {
        String tmpDefinition = "";
        boolean tmpIsFirstAminoAcid = true;
        int tmpIndex = 0;
        String tmpFrequencyString = "";
        boolean tmpIsChargeBracketOpen = false;
        boolean tmpIsDisulfidBridgeDefinition = false;
        while (tmpIndex < aDefinition.length) {
            if (aDefinition[tmpIndex] == '[') {
                tmpDefinition += '[';
                tmpIsDisulfidBridgeDefinition = true;
                tmpIndex++;
                continue;
            }
            if (tmpIsDisulfidBridgeDefinition) {
                tmpDefinition += aDefinition[tmpIndex];
                if (aDefinition[tmpIndex] == ']') {
                    tmpIsDisulfidBridgeDefinition = false;
                }
                tmpIndex++;
                continue;
            }
            if (aDefinition[tmpIndex] == '{') {
                tmpIsChargeBracketOpen = true;
            }
            if (aDefinition[tmpIndex] == '}') {
                tmpIsChargeBracketOpen = false;
            }
            if (Character.isDigit(aDefinition[tmpIndex])) {
                tmpFrequencyString += aDefinition[tmpIndex];
            }
            if (!tmpIsFirstAminoAcid && tmpIndex != aLastAminoAcidIndex) {
                if (this.aminoAcidsUtility.isOneLetterAminoAcid(aDefinition[tmpIndex]) && !tmpIsChargeBracketOpen) {
                    tmpIsFirstAminoAcid = false;
                    tmpDefinition += tmpFrequencyString;
                    tmpFrequencyString = "";
                }
                if (!Character.isDigit(aDefinition[tmpIndex])) {
                    tmpDefinition += aDefinition[tmpIndex];
                }
            } else {
                if (tmpFrequencyString.isEmpty()) {

                    tmpDefinition += aDefinition[tmpIndex];
                    if (this.aminoAcidsUtility.isOneLetterAminoAcid(aDefinition[tmpIndex]) && !tmpIsChargeBracketOpen) {
                        tmpIsFirstAminoAcid = false;
                    }
                } else {
                    if (this.aminoAcidsUtility.isOneLetterAminoAcid(aDefinition[tmpIndex]) && !tmpIsChargeBracketOpen) {
                        int tmpFrequency = Integer.parseInt(tmpFrequencyString);
                        if (tmpIsFirstAminoAcid && tmpIndex == aLastAminoAcidIndex) {
                            tmpDefinition += aDefinition[tmpIndex];
                            if (tmpFrequency > 2) {
                                tmpDefinition += tmpFrequency - 2 > 1 ? tmpFrequency - 2 : "";
                                tmpDefinition += aDefinition[tmpIndex];
                            }
                            tmpDefinition += aDefinition[tmpIndex];
                            tmpIsFirstAminoAcid = false;
                        } else if (tmpIsFirstAminoAcid) {
                            tmpDefinition += aDefinition[tmpIndex];
                            tmpDefinition += tmpFrequency - 1 > 1 ? tmpFrequency - 1 : "";
                            tmpDefinition += aDefinition[tmpIndex];
                            tmpIsFirstAminoAcid = false;
                        } else {
                            tmpDefinition += tmpFrequency - 1 > 1 ? tmpFrequency - 1 : "";
                            tmpDefinition += aDefinition[tmpIndex];
                            tmpDefinition += aDefinition[tmpIndex];
                        }
                        tmpFrequencyString = "";
                    } else {
                        if (!Character.isDigit(aDefinition[tmpIndex])) {
                            tmpDefinition += aDefinition[tmpIndex];
                        }
                    }
                }
            }
            if (!Character.isDigit(aDefinition[tmpIndex])) {
                tmpDefinition += tmpFrequencyString;
                tmpFrequencyString = "";
            }
            tmpIndex++;
        }
        return tmpDefinition;
    }

    /**
     * Converts given three letter peptide defintion in a fragemnt based
     * Spices.
     *
     * @param aDefintion The peptide definition. See description for further
     * details.
     * @return Particle Spices.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertThreeLetterPeptideToSpices(String aDefintion) throws DpdPeptideException {
        String tmpDefintion = this.convertThreeToOneLetterDefinition(aDefintion);
        return this.convertOneLetterPeptideToSpices(tmpDefintion);
    }

    /**
     * Converts given peptide defintion from three letter code into one letter
     * code.
     *
     * @param aDefinition The peptide definition. See description for further
     * details.
     * @return Peptide definition in one letter code.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertThreeToOneLetterDefinition(String aDefinition) throws DpdPeptideException {
        String tmpConvertedSequence = "";
        char[] tmpDefinition = aDefinition.toUpperCase().toCharArray();
        int tmpIndex = 0;
        while (tmpIndex < tmpDefinition.length) {
            if (Character.isLetter(tmpDefinition[tmpIndex])) {
                String tmpAminoAcid = this.getThreeLetterAminoAcidFromSequence(tmpDefinition, tmpIndex);
                tmpConvertedSequence += this.aminoAcidsUtility.convertThreeToOneLetterCode(tmpAminoAcid);
                tmpIndex += 3;
            } else {
                // do not convert digits and special signs
                tmpConvertedSequence += tmpDefinition[tmpIndex];
                tmpIndex++;
            }
        }
        return tmpConvertedSequence;
    }

    /**
     * Removes charges from given one letter code.
     *
     * @param aDefinition Peptide definition in one letter code.
     * @return Charge free one letter code.
     */
    public String removeOneLetterCharges(String aDefinition) {
        String tmpCode = "";
        char[] tmpDefinition = aDefinition.toCharArray();
        int tmpIndex = 0;
        boolean tmpIsChargeBracketOpen = false;
        while (tmpIndex < tmpDefinition.length) {
            if (tmpDefinition[tmpIndex] == '{') {
                tmpIsChargeBracketOpen = true;
            }
            if (tmpDefinition[tmpIndex] == '}') {
                tmpIsChargeBracketOpen = false;
                tmpIndex++;
            }
            if (tmpIndex < tmpDefinition.length && !tmpIsChargeBracketOpen) {
                tmpCode += tmpDefinition[tmpIndex];
            }
            tmpIndex++;
        }
        return tmpCode;
    }

    /**
     * Determines whether given one letter code is charged.
     *
     * @param aDefinition Peptide definition in one letter code.
     * @return True if given one letter contains charges.
     */
    public boolean hasOneLetterCharge(String aDefinition) {
        return aDefinition.contains("{") && aDefinition.contains("}");
    }

    // Properties
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Gets a regex matching all allowed peptide defintion characters for one
     * letter code.
     *
     * @return The regex pattern.
     */
    public Pattern getAllowedOneLetterCharactersPattern() {
        return this.peptideAllowedOneLetterCharactersPattern;
    }

    /**
     * Gets the one letter descriptions and symbols within a sorted map. The key
     * corresponds to the description, the value defines the symbol.
     *
     * @return Sorted symbol array.
     * @throws DpdPeptideException DpdPeptideException
     */
    public ArrayList<String> getOneLetterSymbols() throws DpdPeptideException {
        return this.getSymbols(CodeType.TYPE_ONE_LETTER);
    }

    /**
     * Gets the three letter descriptions and symbols within a sorted map. The
     * key corresponds to the description, the value defines the symbol.
     *
     * @return Sorted symbol array.
     * @throws DpdPeptideException DpdPeptideException
     */
    public ArrayList<String> getThreeLetterSymbols() throws DpdPeptideException {
        return this.getSymbols(CodeType.TYPE_THREE_LETTER);
    }

    /**
     * Gets the descriptions and symbols for the given amino acid code type
     * within a sorted map. The key corresponds to the description, the value
     * defines the symbol.
     *
     * @param aCodeType The amino acid code type.
     * @return Sorted symbol array.
     * @throws DpdPeptideException
     */
    private ArrayList<String> getSymbols(CodeType aCodeType) throws DpdPeptideException {
        ArrayList<String> tmpSymbolMap = new ArrayList<String>();
        // Bond brackets
        tmpSymbolMap.add("[ : Disulfide bond open");
        tmpSymbolMap.add("] : Disulfide bond close");
        // Prefixes
        for (int i = 0; i < 10; i++) {
            tmpSymbolMap.add(i + " : Frequency digit");
        }
        // Amino acids
        for (AminoAcid tmpAminoAcid : this.aminoAcidsUtility.getAminoAcids()) {
            String tmpAminoAcidCode;
            if (aCodeType == CodeType.TYPE_ONE_LETTER) {
                tmpAminoAcidCode = tmpAminoAcid.getOneLetterCode().toString();
            } else {
                tmpAminoAcidCode = tmpAminoAcid.getThreeLetterCode();
                tmpAminoAcidCode = Character.toUpperCase(tmpAminoAcidCode.charAt(0)) + tmpAminoAcidCode.substring(1).toLowerCase();
            }
            tmpSymbolMap.add(tmpAminoAcidCode + " : " + tmpAminoAcid.getName());
        }
        // Charged Amino acids
        TreeMap<String, String> tmpChargedAminoAcidSymbols = new TreeMap<String, String>();
        int tmpMaxResultLength = 0;
        for (AminoAcid tmpAminoAcid : this.aminoAcidsUtility.getAminoAcids()) {
            String tmpAminoAcidCode;
            if (aCodeType == CodeType.TYPE_ONE_LETTER) {
                tmpAminoAcidCode = tmpAminoAcid.getOneLetterCode().toString();
            } else {
                tmpAminoAcidCode = tmpAminoAcid.getThreeLetterCode();
                tmpAminoAcidCode = Character.toUpperCase(tmpAminoAcidCode.charAt(0)) + tmpAminoAcidCode.substring(1).toLowerCase();
            }
            ArrayList<ChargeSetting> tmpChargeSettings = tmpAminoAcid.getChargeSettings();
            ArrayList<String> tmpChargeTypes = new ArrayList<String>();
            for (ChargeSetting tmpChargeSetting : tmpChargeSettings) {
                if (!tmpChargeSetting.getType().equals(ChargeType.SS)) {
                    tmpChargeTypes.add(tmpChargeSetting.getChargeArgument());
                }
            }
            String[] tmpResults = this.generateAllPermuations(tmpChargeTypes.toArray(new String[tmpChargeTypes.size()]));
            Arrays.sort(tmpResults);
            for (String tmpResult : tmpResults) {
                String tmpChargedAminoAcidSymbol = tmpAminoAcidCode + "{" + tmpResult + "}";
                if (tmpChargedAminoAcidSymbol.length() > tmpMaxResultLength) {
                    tmpMaxResultLength = tmpChargedAminoAcidSymbol.length();
                }
                tmpChargedAminoAcidSymbols.put(tmpChargedAminoAcidSymbol, tmpAminoAcid.getName());
            }
        }
        for (Entry<String, String> tmpEntry : tmpChargedAminoAcidSymbols.entrySet()) {
            tmpSymbolMap.add(this.alignValue(tmpEntry.getKey(), tmpMaxResultLength)
                    + " : " + tmpEntry.getValue());
        }
        return tmpSymbolMap;
    }

    /**
     * Fills target string with white spaces until given length is reached.
     *
     * @param aValue String value.
     * @param aLength Target length.
     * @return
     */
    private String alignValue(String aValue, int aLength) {
        while (aValue.length() < aLength) {
            aValue += " ";
        }
        return aValue;
    }

    /**
     * Implementation of the Steinhaus–Johnson–Trotter algorithm to generate all
     * possible permutations of given values.
     *
     * @param aValues
     * @return
     */
    private String[] generateAllPermuations(String[] aValues) {
        int tmpMaxNumber = (int) Math.pow(2, aValues.length);
        String[] tmpBinaries = new String[tmpMaxNumber - 1];
        for (int i = 1; i < tmpMaxNumber; i++) {
            tmpBinaries[i - 1] = Integer.toBinaryString(i);
            while (tmpBinaries[i - 1].length() < aValues.length) {
                tmpBinaries[i - 1] = "0" + tmpBinaries[i - 1];
            }
        }
        HashSet<String> tmpResultSet = new HashSet<String>();
        for (int i = 0; i < tmpBinaries.length; i++) {
            String tmpResult = "";
            for (int j = 0; j < aValues.length; j++) {
                if (tmpBinaries[i].charAt(j) == '1') {
                    if (!tmpResult.contains(aValues[j])) {
                        tmpResult += aValues[j];
                    }
                }
            }
            tmpResultSet.add(tmpResult);
        }
        String[] tmpResults = new String[tmpResultSet.size()];
        return tmpResultSet.toArray(tmpResults);
    }

}
