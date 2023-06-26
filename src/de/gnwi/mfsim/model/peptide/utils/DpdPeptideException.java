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
package de.gnwi.mfsim.model.peptide.utils;

import de.gnwi.mfsim.model.message.ModelMessage;

/**
 * DpdPeptideException class.
 *
 * @author Andreas Truszkowski
 */
public class DpdPeptideException extends Exception {

    /**
     * Constructs an instance of
     * <code>DpdPeptideException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DpdPeptideException(String msg) {
        super(msg.startsWith("Peptide.") ? ModelMessage.get(msg) : msg);
    }

    /**
     * Constructs an instance of
     * <code>DpdPeptideException</code> with the specified detail message.
     *
     * @param msg the detail message.
     * @param cause Inner exception.
     */
    public DpdPeptideException(String msg, Throwable cause) {
        super(msg.startsWith("Peptide.") ? ModelMessage.get(msg) : msg, cause);
    }

    /**
     * Gets a load amino acids exception.
     *
     * @return LoadAminoAcidsException.
     */
    public static DpdPeptideException LoadAminoAcidsException() {
        String msg = ModelMessage.get("Peptide.ErrorInitAminoAcidData");
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a not valid amino acid exception.
     *
     * @param aValue String representing the invalid value.
     * @return NotValidAminoAcidException
     */
    public static DpdPeptideException NotValidAminoAcidException(String aValue) {
        String msg = String.format(ModelMessage.get("Peptide.InvalidAminoAcid"), aValue);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a not valid amino acid SPICES exception.
     *
     * @param aValue String representing the invalid value.
     * @return InvalidAminoAcidSpicesException
     */
    public static DpdPeptideException InvalidAminoAcidSpicesException(String aValue) {
        String msg = String.format(ModelMessage.get("Peptide.InvalidAminoAcidSpices"), aValue);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a not valid amino acid exception.
     *
     * @param aValue String representing the invalid value.
     * @param aPosition Position in peptide definition sequence.
     * @return NotValidAminoAcidException
     */
    public static DpdPeptideException NotValidAminoAcidException(String aValue, int aPosition) {
        String msg = String.format(ModelMessage.get("Peptide.InvalidAminoAcidWithPos"), aValue, aPosition);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets an incorrect disulfide bond count exception.
     *
     * @param aIndex Disulfide bond index.
     * @return IncorrectDisulfideBondCountException
     */
    public static DpdPeptideException IncorrectDisulfideBondCountException(int aIndex) {
        String msg = String.format(ModelMessage.get("Peptide.IncorrectDisulfideBondCount"), aIndex);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets an incorrect disulfide bond defintion exception.
     *
     * @param aPosition Position in peptide definition sequence.
     * @return IncorrectDisulfideBondDefinitionException
     */
    public static DpdPeptideException IncorrectDisulfideBondDefinitionException(int aPosition) {
        String msg = String.format(ModelMessage.get("Peptide.IncorrectDisulfideBondDefinition"), aPosition);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets an incorrect charge argument exception.
     *
     * @param anArgument Incorrect charge argument.
     * @return IncorrectChargeArgumentException
     */
    public static DpdPeptideException IncorrectChargeArgumentException(String anArgument) {
        String msg = String.format(ModelMessage.get("Peptide.IncorrectChargeArgument"), anArgument);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a missing amino acid exception.
     *
     * @param aAminoAcid Name of missing amino acid.
     * @return MissingAminoAcidSpicesException
     */
    public static DpdPeptideException MissingAminoAcidSpicesException(String aAminoAcid) {
        String msg = String.format(ModelMessage.get("Peptide.MissingSpices"), aAminoAcid);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a missing data exception.
     *
     * @param aName Name of missing data
     * @return MissingAminoAcidSpicesException
     */
    public static DpdPeptideException MissingData(String aName) {
        String msg = String.format(ModelMessage.get("Peptide.ErrorMissingData"), aName);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a not all brackets closed exception.
     *
     * @param aBracketType String representing the bracket type.
     * @return NotAllBracketsClosedException
     */
    public static DpdPeptideException NotAllBracketsClosedException(String aBracketType) {
        String msg = String.format(ModelMessage.get("Peptide.NotAllBracketsClosed"), aBracketType);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a no protein data available exception.
     *
     * @return NoProteinDataException
     */
    public static DpdPeptideException NoProteinDataException() {
        String msg = ModelMessage.get("Peptide.NoProteinData");
        return new DpdPeptideException(msg);
    }

    /**
     * Gets a syntax error exception.
     *
     * @param aPosition Position in peptide definition sequence.
     * @return SyntaxErrorException
     */
    public static DpdPeptideException SyntaxErrorException(int aPosition) {
        String msg = String.format(ModelMessage.get("Peptide.SyntaxError"), aPosition);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets an unknown amino acid exception.
     *
     * @param aAminoAcid Unknown amino acid.
     * @return UnknownAminoAcidException
     */
    public static DpdPeptideException UnknownAminoAcidException(String aAminoAcid) {
        String msg = String.format(ModelMessage.get("Peptide.UnknownAminoAcid"), aAminoAcid);
        return new DpdPeptideException(msg);
    }

    /**
     * Gets an illegal argument exception.
     *
     * @param anArgument Illegal argument.
     * @return IllegalArguemntException
     */
    public static DpdPeptideException IllegalArgumentException(String anArgument) {
        String msg = String.format(ModelMessage.get("Peptide.IllegalArgument"), anArgument);
        return new DpdPeptideException(msg);
    }
}
