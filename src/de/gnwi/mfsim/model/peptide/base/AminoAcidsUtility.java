/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2022  Achim Zielesny (achim.zielesny@googlemail.com)
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
import java.util.HashSet;

/**
 * Class which manages all amino acids. NOTE: Syntax of amino acid Spices is
 * single-particle Spices = Backbone or Spices = Backbone(Sidechain), i.e. an
 * amino acids backbone is always represented by 1 particle
 *
 * @author Achim Zielesny
 */
public class AminoAcidsUtility {

    // Fields
    ////////////////////////////////////////////////////////////////////////////
    /**
     * All available amino acids.
     */
    private final ArrayList<AminoAcid> aminoAcids = new ArrayList<>();

    /**
     * List of all available one letter codes.
     */
    private final HashSet<String> oneLetterAminoAcids = new HashSet<>();

    /**
     * List of all available three letter codes.
     */
    private final HashSet<String> threeLetterAminoAcids = new HashSet<>();

    /**
     * True: All amino acids are represented by SPICES with
     * only one single particle, false: Otherwise (i.e. at least one amino acid
     * is represented by SPICES with two or more particles
     */
    private boolean isOnlySingleParticleAminoAcids = true;

    // Constructor
    // /////////////////////////////////////////////////////////////////////////
    /**
     * Constructor for this instance
     */
    public AminoAcidsUtility() {
        this.clearAminoAcids();
    }

    /**
     * Constructor for this instance
     *
     * @param anAminoAcidsDefinition Amino acids definition
     * @throws DpdPeptideException DpdPeptideException
     */
    public AminoAcidsUtility(String anAminoAcidsDefinition) throws DpdPeptideException {
        if (anAminoAcidsDefinition == null || anAminoAcidsDefinition.isEmpty()) {
            throw new DpdPeptideException("Peptide.InvalidAminoAcidsDefinition");
        }
        String[] tmpItems = Tools.GENERAL_SEPARATOR_PATTERN.split(anAminoAcidsDefinition);
        if (tmpItems == null || tmpItems.length != 21) {
            throw new DpdPeptideException("Peptide.InvalidAminoAcidsDefinition");
        }
        String tmpVersion = tmpItems[0];
        if (tmpVersion.equals("1.0.0.0")) {
            for (int i = 1; i < tmpItems.length; i++) {
                AminoAcid tmpAminoAcid = new AminoAcid(tmpItems[i], this);
                this.addAminoAcid(tmpAminoAcid);
            }
        } else {
            throw new DpdPeptideException("Peptide.InvalidAminoAcidsDefinition");
        }
    }

    // Methods
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Converts a one letter amino acid code into the corresponding three letter
     * code.
     *
     * @param aCode Given one letter code
     * @return Corresponding three letter code.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertOneToThreeLetterCode(Character aCode) throws DpdPeptideException {
        return this.convertOneToThreeLetterCode(Character.toString(aCode));
    }

    /**
     * Converts a one letter amino acid code into the corresponding three letter
     * code.
     *
     * @param aCode Given one letter code
     * @return Corresponding three letter code.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertOneToThreeLetterCode(String aCode) throws DpdPeptideException {
        if (!this.isOneLetterAminoAcid(aCode)) {
            throw DpdPeptideException.NotValidAminoAcidException(aCode);
        }
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (tmpAminoAcid.getOneLetterCode().equals(aCode)) {
                return tmpAminoAcid.getThreeLetterCode();
            }
        }
        return null;
    }

    /**
     * Converts a three letter amino acid code into the corresponding one letter
     * code.
     *
     * @param aCode Given three letter code
     * @return Corresponding three one code.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String convertThreeToOneLetterCode(String aCode) throws DpdPeptideException {
        if (!this.isThreeLetterAminoAcid(aCode)) {
            throw DpdPeptideException.NotValidAminoAcidException(aCode);
        }
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (tmpAminoAcid.getThreeLetterCode().equals(aCode)) {
                return tmpAminoAcid.getOneLetterCode();
            }
        }
        return null;
    }

    // Properties
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Clears all amino acids.
     */
    public final void clearAminoAcids() {
        this.aminoAcids.clear();
        this.isOnlySingleParticleAminoAcids = true;
        this.oneLetterAminoAcids.clear();
        this.threeLetterAminoAcids.clear();
    }

    /**
     * Checks whether given one letter code is valid.
     *
     * @param aCode One letter code
     * @return True if given code is valid.
     */
    public boolean isOneLetterAminoAcid(Character aCode) {
        return this.isOneLetterAminoAcid(Character.toString(aCode));
    }

    /**
     * Checks whether given one letter code is valid.
     *
     * @param aCode One letter code
     * @return True if given code is valid.
     */
    public boolean isOneLetterAminoAcid(String aCode) {
        return this.oneLetterAminoAcids.contains(aCode.toUpperCase());
    }

    /**
     * True: All amino acids are represented by SPICES with
     * only one single particle, false: Otherwise (i.e. at least one amino acid
     * is represented by SPICES with two or more particles
     *
     * @return True: All amino acids are represented by SPICES
     * with only one single particle, false: Otherwise (i.e. at least
     * one amino acid is represented by SPICES with two or more particles
     */
    public boolean isOnlySingleParticleAminoAcids() {
        return this.isOnlySingleParticleAminoAcids;
    }

    /**
     * Checks whether given three letter code is valid.
     *
     * @param aCode Three letter code
     * @return True if given code is valid.
     */
    public boolean isThreeLetterAminoAcid(String aCode) {
        return this.threeLetterAminoAcids.contains(aCode.toUpperCase());
    }

    /**
     * Gets the particle Spices from given one letter code.
     *
     * @param aCode Three letter code
     * @return Spices depending on given one letter code
     * @throws DpdPeptideException DpdPeptideException
     */
    public String getSpicesFromOneLetterCode(Character aCode) throws DpdPeptideException {
        return this.getSpicesFromOneLetterCode(Character.toString(aCode));
    }

    /**
     * Gets the particle Spices from given one letter code. The Spices consits
     * only of uncharged particles.
     *
     * @param aCode One letter code
     * @return Spices depending on given one letter code.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String getSpicesFromOneLetterCode(String aCode) throws DpdPeptideException {
        AminoAcid tmpAminoAcid = this.getAminoAcidFromOneLetterCode(aCode);
        String tmpSpices = tmpAminoAcid.getSpicesString();
        if (tmpSpices == null) {
            throw DpdPeptideException.MissingAminoAcidSpicesException(aCode);
        }
        return tmpSpices;
    }

    /**
     * Gets the particle Spices from given one letter code. The Spices
     * consists of charged and uncharged particles depending on given charge
     * setting.
     *
     * @param aCode One letter code.
     * @param aChargeArgument A charge argument (S+C-N+, ...)
     * @return Charged Spices
     * @throws DpdPeptideException DpdPeptideException
     */
    public String getChargedSpicesFromOneLetterCode(Character aCode, String aChargeArgument) throws DpdPeptideException {
        return this.getChargedSpicesFromOneLetterCode(Character.toString(aCode), aChargeArgument);
    }

    /**
     * Gets the particle Spices from given one letter code. The Spices consits
     * of charged and uncharged particles depending on given charge setting.
     *
     * @param aCode One letter code.
     * @param aChargeArgument A charge argument (S+C-N+, ...)
     * @return Charged Spices
     * @throws DpdPeptideException DpdPeptideException
     */
    public String getChargedSpicesFromOneLetterCode(String aCode, String aChargeArgument) throws DpdPeptideException {
        AminoAcid tmpAminoAcid = this.getAminoAcidFromOneLetterCode(aCode);
        String tmpSpices = tmpAminoAcid.getChargedSpices(aChargeArgument);
        if (tmpSpices == null) {
            throw DpdPeptideException.MissingAminoAcidSpicesException(aCode);
        }
        return tmpSpices;
    }

    /**
     * Adds a new amino acid to the amino acid collection.
     *
     * @param anAminoAcid Amino acid to add.
     */
    public final void addAminoAcid(AminoAcid anAminoAcid) {
        this.aminoAcids.add(anAminoAcid);
        if (!anAminoAcid.isSingleParticleSpices()) {
            this.isOnlySingleParticleAminoAcids = false;
        }
        this.oneLetterAminoAcids.add(anAminoAcid.getOneLetterCode());
        this.threeLetterAminoAcids.add(anAminoAcid.getThreeLetterCode());
    }

    /**
     * Returns all amino acids
     *
     * @return All amino acids
     */
    public ArrayList<AminoAcid> getAminoAcids() {
        return this.aminoAcids;
    }

    /**
     * Checks if this instance is completely initialised with 20 amino acids.
     *
     * @return True: This instance is completely initialised with 20 amino
     * acids, false: Otherwise
     */
    public boolean isCompletelyInitialised() {
        return this.aminoAcids != null && this.aminoAcids.size() == 20;
    }

    /**
     * Gets target amino acid from given name.
     *
     * @param aName Name of the amino acid.
     * @return Target amino acid.
     * @throws DpdPeptideException DpdPeptideException
     */
    public AminoAcid getAminoAcidFromName(String aName) throws DpdPeptideException {
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (tmpAminoAcid.getName().equals(aName)) {
                return tmpAminoAcid;
            }
        }
        throw DpdPeptideException.NotValidAminoAcidException(aName);
    }

    /**
     * Gets the specific amino acid depending on given one letter code.
     *
     * @param aCode One letter code.
     * @return Amino acid
     * @throws DpdPeptideException DpdPeptideException
     */
    public AminoAcid getAminoAcidFromOneLetterCode(Character aCode) throws DpdPeptideException {
        return this.getAminoAcidFromOneLetterCode(Character.toString(aCode));
    }

    /**
     * Gets the specific amino acid depending on given one letter code.
     *
     * @param aCode One letter code.
     * @return Amino acid
     * @throws DpdPeptideException DpdPeptideException
     */
    public AminoAcid getAminoAcidFromOneLetterCode(String aCode) throws DpdPeptideException {
        if (!this.isOneLetterAminoAcid(aCode)) {
            throw DpdPeptideException.NotValidAminoAcidException(aCode);
        }
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (tmpAminoAcid.getOneLetterCode().equals(aCode)) {
                return tmpAminoAcid;
            }
        }
        return null;
    }

    /**
     * Gets the specific amino acid depending on given three letter code.
     *
     * @param aCode One letter code.
     * @return Amino acid
     * @throws DpdPeptideException DpdPeptideException
     */
    public AminoAcid getAminoAcidFromThreeLetterCode(String aCode) throws DpdPeptideException {
        if (!this.isThreeLetterAminoAcid(aCode)) {
            throw DpdPeptideException.NotValidAminoAcidException(aCode);
        }
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (tmpAminoAcid.getThreeLetterCode().equals(aCode)) {
                return tmpAminoAcid;
            }
        }
        return null;
    }

    /**
     * Sets the particle Spices to the specified amino acid. The Spices
     * describing an amino acid can only consist of the particle names and
     * branches "( )".
     *
     * @param anAminoAcid Name of the amino acid.
     * @param aSpices Spices to set.
     * @throws DpdPeptideException DpdPeptideException
     */
    public void setSpicesForAminoAcid(String anAminoAcid, String aSpices) throws DpdPeptideException {
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (tmpAminoAcid.getName().equalsIgnoreCase(anAminoAcid)) {
                tmpAminoAcid.setSpices(aSpices);
                return;
            }
        }
        throw DpdPeptideException.UnknownAminoAcidException(anAminoAcid);
    }

    /**
     * Returns the set of particles used to describe the amino acids. The
     * Spices describing an amino acid can only consist of the particle names
     * and branches "( )".
     *
     * @return Set of particles.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String[] getUsedParticles() throws DpdPeptideException {
        HashSet<String> tmpParticles = new HashSet<>();
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (tmpAminoAcid.getSpicesString() == null) {
                throw DpdPeptideException.MissingAminoAcidSpicesException(tmpAminoAcid.getName());
            }
            char[] tmpSpices = tmpAminoAcid.getSpicesString().toCharArray();
            int i = 0;
            while (i < tmpSpices.length) {
                String tmpParticle = "";
                // Get particle name
                if (Character.isLetter(tmpSpices[i])) {
                    while (i < tmpSpices.length && (Character.isLetter(tmpSpices[i]) || Character.isDigit(tmpSpices[i]))) {
                        tmpParticle += tmpSpices[i];
                        i++;
                    }
                }
                if (!tmpParticles.contains(tmpParticle)) {
                    tmpParticles.add(tmpParticle);
                }
                if (i >= tmpSpices.length) {
                    break;
                }
                // Skip special character "(", ")", "-" and digits.
                while (i < tmpSpices.length && !Character.isLetter(tmpSpices[i])) {
                    i++;
                }
            }
            for (ChargeSetting setting : tmpAminoAcid.getChargeSettings()) {
                if (!tmpParticles.contains(setting.getDeprotonatedParticle())) {
                    tmpParticles.add(setting.getDeprotonatedParticle());
                }
                if (!tmpParticles.contains(setting.getProtonatedParticle())) {
                    tmpParticles.add(setting.getProtonatedParticle());
                }
            }
        }
        String[] tmpResult = new String[0];
        return tmpParticles.toArray(tmpResult);
    }

    /**
     * Checks whether given amino acid is already defined.
     *
     * @param anAminoAcid Amino acid to check.
     * @return True if amino acid is defined.
     */
    public boolean hasAminoAcid(AminoAcid anAminoAcid) {
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (anAminoAcid.getOneLetterCode().equals(tmpAminoAcid.getOneLetterCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether amino acid with passed one-letter-code is already defined.
     *
     * @param aOneLetterCode Amino acid one-letter-code to check.
     * @return True if amino acid with specified one-letter-code is defined.
     */
    public boolean hasAminoAcid(String aOneLetterCode) {
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            if (aOneLetterCode.equals(tmpAminoAcid.getOneLetterCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether 20 amino acids are already defined.
     *
     * @return True if 20 amino acid are defined.
     */
    public boolean hasAminoAcids() {
        return this.aminoAcids.size() == 20;
    }

    /**
     * Returns amino acids definition
     *
     * @return Amino acids definition
     */
    public String getAminoAcidsDefinition() {
        StringBuilder tmpBuffer = new StringBuilder(5000);
        tmpBuffer.append("1.0.0.0");
        for (AminoAcid tmpAminoAcid : this.aminoAcids) {
            tmpBuffer.append(Tools.GENERAL_SEPARATOR);
            tmpBuffer.append(tmpAminoAcid.getAminoAcidDefinition());
        }
        return tmpBuffer.toString();
    }
}
