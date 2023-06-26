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

/**
 * Class representing a charge setting of an amino acid. It contains
 * informations about the pKs-value, de- / protonated particles, charges, charge
 * type and helper methods.
 *
 * @author Andreas Truszkowski
 */
public class ChargeSetting {

    /**
     * Charge type (Terminal C/N, Sidechain free/bound).
     */
    private ChargeType type;
    /**
     * pKs-value.
     */
    private double pKs;
    /**
     * Name of the deprotonated particle.
     */
    private String deprotonatedParticle;
    /**
     * Name of the protonated particle.
     */
    private String protonatedParticle;
    /**
     * Corresponding charge argument (S+, C-, N+, ...).
     */
    private String chargeArgument;
    /**
     * True is the protonated species is charged. False for the deprotonated
     * species.
     */
    private boolean isProtonatedCharged;

    /**
     * Creates a new instance.
     *
     * @param aType Charge type (Terminal C/N, Sidechain free/bound).
     * @param aPKs pKs-value.
     * @param aDeprotonatedParticle Name of the deprotonated particle.
     * @param aProtonatedParticle Name of the protonated particle.
     * @param aCharge Charge.
     * @param anIsProtonatedCharged True if the protonated species is charged.
     * False for the deprotonated species.
     * @throws DpdPeptideException DpdPeptideException
     */
    public ChargeSetting(ChargeType aType, double aPKs, String aDeprotonatedParticle, String aProtonatedParticle,
            Character aCharge, boolean anIsProtonatedCharged) throws DpdPeptideException {
        this.type = aType;
        this.pKs = aPKs;
        this.deprotonatedParticle = aDeprotonatedParticle;
        this.protonatedParticle = aProtonatedParticle;
        this.setIsProtonatedCharged(anIsProtonatedCharged);
        this.setCharge(aCharge);
    }

    /**
     * Gets the deprotonated particle.
     *
     * @return The deprotonated particle.
     */
    public String getDeprotonatedParticle() {
        return this.deprotonatedParticle;
    }

    /**
     * Gets the pKs-value
     *
     * @return The pKs-value.
     */
    public double getpKs() {
        return this.pKs;
    }

    /**
     * Gets the protonated particle.
     *
     * @return The protonated particle.
     */
    public String getProtonatedParticle() {
        return this.protonatedParticle;
    }

    /**
     * Gets the charge type.
     *
     * @return The charge type (Terminal C/N, Sidechain free/bound).
     */
    public ChargeType getType() {
        return this.type;
    }

    /**
     * Gets the charged particle.
     *
     * @return The charged particle.
     */
    public String getChargedParticle() {
        if (this.isProtonatedCharged()) {
            return this.protonatedParticle;
        } else {
            return this.deprotonatedParticle;
        }
    }

    /**
     * Gets the uncharged particle.
     *
     * @return The uncharged particle.
     */
    public String getUnchargedParticle() {
        if (this.isProtonatedCharged()) {
            return this.deprotonatedParticle;
        } else {
            return this.protonatedParticle;
        }
    }

    /**
     * Gets the cahrge argument of this setting.
     *
     * @return Corresponding charge argument (S+, C-, N+, ...).
     */
    public String getChargeArgument() {
        return this.chargeArgument;
    }

    /**
     * Sets the charge.
     *
     * @param aCharge The charge (+/-)
     * @throws DpdPeptideException DpdPeptideException
     */
    public final void setCharge(Character aCharge) throws DpdPeptideException {
        if (this.type.equals(ChargeType.SS)) {
            return;
        }
        if (!aCharge.equals('+') && !aCharge.equals('-')) {
            throw DpdPeptideException.IllegalArgumentException(Character.toString(aCharge));
        }
        if (this.type.compareTo(ChargeType.TC) == 0) {
            this.chargeArgument = "C" + aCharge;
        } else if (this.type.compareTo(ChargeType.TN) == 0) {
            this.chargeArgument = "N" + aCharge;
        } else if (this.type.compareTo(ChargeType.SCF) == 0) {
            this.chargeArgument = "S" + aCharge;
        } else if (this.type.compareTo(ChargeType.SCB) == 0) {
            this.chargeArgument = "S" + aCharge;
        }
    }

    /**
     * Gets whether the protonated species is charged.
     *
     * @return True if the protonated species is charged. False for the
     * deprotonated species.
     */
    public boolean isProtonatedCharged() {
        return isProtonatedCharged;
    }

    /**
     * Sets whether the protonated species is charged.
     *
     * @param isProtonatedCharged True if the protonated species is charged.
     * False for the deprotonated species.
     */
    public final void setIsProtonatedCharged(boolean isProtonatedCharged) {
        this.isProtonatedCharged = isProtonatedCharged;
    }
}
