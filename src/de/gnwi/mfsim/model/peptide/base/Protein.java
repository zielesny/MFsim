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

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.peptide.PeptideToSpices;
import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.biojava.bio.structure.*;

/**
 * Class representing a protein.
 *
 * @author Andreas Truszkowski
 */
public class Protein {

    // Fields
    ////////////////////////////////////////////////////////////////////////////
    /**
     * The basic protein.
     */
    private Structure proteinStructure = null;

    /**
     * Map linking the chain ID with the corresponding coordinates.
     */
    private HashMap<String, Point3d[]> chainIDCoordinates = null;

    /**
     * Map linking the chain ID with the corresponding CA coordinates.
     */
    private HashMap<String, Point3d[]> chainIDCAlphaCoordinates = null;

    /**
     * Map linking the chain ID with corresponding chain.
     */
    private HashMap<String, Chain> chainIDChainMap = null;

    private HashMap<String, Integer> chainIDStartResidueNumberMap = null;

    /**
     * Chain id list.
     */
    private ArrayList<String> chainIDs = null;

    /**
     * Map linking the chain ID to its overridden sequences.
     */
    private HashMap<String, String> overriddenSequences = new HashMap<String, String>();

    /**
     * List of all SS bonds.
     */
    private HashMap<Integer, SSBond> serNumToSsBondsMap = null;

    /**
     * Amino acids utility
     */
    private AminoAcidsUtility aminoAcidsUtility;

    // Constructor
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a new instance.
     *
     * @param aProtein BioJava protein structure.
     * @param anAminoAcidsUtility Amino acids utility
     * @throws DpdPeptideException DpdPeptideException
     */
    public Protein(Structure aProtein, AminoAcidsUtility anAminoAcidsUtility) throws DpdPeptideException {
        if (anAminoAcidsUtility == null || !anAminoAcidsUtility.isCompletelyInitialised()) {
            throw new DpdPeptideException("Peptide.MissingAminoAcidData");
        }
        this.proteinStructure = aProtein;
        this.aminoAcidsUtility = anAminoAcidsUtility;
        // Default number of SS-bonds is 50 which is sufficient in almost all cases
        this.serNumToSsBondsMap = new HashMap<>(50);
        this.chainIDChainMap = new HashMap<String, Chain>();
        this.chainIDStartResidueNumberMap = new HashMap<String, Integer>();
        this.chainIDs = new ArrayList<String>();
        if (this.proteinStructure.isBiologicalAssembly()
                || ((this.proteinStructure.getCompounds().isEmpty() || this.proteinStructure.getCompounds().get(0).getChains().isEmpty()) && this.proteinStructure.nrModels() > 0)) {
            for (int i = 0; i < this.proteinStructure.nrModels(); i++) {
                List<SSBond> tmpSSBonds = this.proteinStructure.getModel(i).get(0).getParent().getSSBonds();
                for (SSBond tmpSSBond : tmpSSBonds) {
                    String tmpChainID1 = String.format("%s/%d", tmpSSBond.getChainID1(), i + 1);
                    String tmpChainID2 = String.format("%s/%d", tmpSSBond.getChainID2(), i + 1);
                    tmpSSBond.setChainID1(tmpChainID1);
                    tmpSSBond.setChainID2(tmpChainID2);
                    if (!this.serNumToSsBondsMap.containsKey(tmpSSBond.getSerNum())) {
                        this.serNumToSsBondsMap.put(tmpSSBond.getSerNum(), tmpSSBond);
                    }
                }
                for (int j = 0; j < this.proteinStructure.getModel(i).size(); j++) {
                    Chain tmpChain = this.proteinStructure.getModel(i).get(j);
                    String tmpChainID = String.format("%s/%d", tmpChain.getChainID(), i + 1);
                    tmpChain.setChainID(tmpChainID);
                    this.chainIDChainMap.put(tmpChainID, tmpChain);
                    this.chainIDs.add(tmpChainID);
                    this.chainIDStartResidueNumberMap.put(tmpChainID, tmpChain.getAtomGroup(0).getResidueNumber().getSeqNum());
                }
            }
        } else {
            for (int i = 0; i < this.proteinStructure.getCompounds().size(); i++) {
                Compound tmpCompound = this.proteinStructure.getCompounds().get(i);
                for (SSBond tmpSSBond : this.proteinStructure.getSSBonds()) {
                    if (!this.serNumToSsBondsMap.containsKey(tmpSSBond.getSerNum())) {
                        this.serNumToSsBondsMap.put(tmpSSBond.getSerNum(), tmpSSBond);
                    }
                }
                for (int j = 0; j < tmpCompound.getChains().size(); j++) {
                    Chain tmpChain = tmpCompound.getChains().get(j);
                    String tmpChainID = tmpChain.getChainID();
                    this.chainIDChainMap.put(tmpChainID, tmpChain);
                    this.chainIDs.add(tmpChainID);
                    this.chainIDStartResidueNumberMap.put(tmpChainID, tmpChain.getAtomGroup(0).getResidueNumber().getSeqNum());
                }
            }
        }
        Collections.sort(this.chainIDs);
    }

    // Methods
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Gets all chain IDs.
     *
     * @return Chain Ids.
     */
    public ArrayList<String> getChainIDs() {
        return this.chainIDs;
    }

    /**
     * Gets the protein name;
     *
     * @return The name of the protein.
     */
    public String getName() {
        return this.proteinStructure.getName();
    }

    /**
     * Gets the Pdb code.
     *
     * @return The Pdb code.
     */
    public String getPdbCode() {
        return this.proteinStructure.getPDBCode();
    }

    /**
     * Gets all ZMatrices for given protein data.
     *
     * @return Chain ID / z-matrices map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, ZMatrix> getZMatrices() throws DpdPeptideException {
        return this.getZMatrices(this.chainIDs);
    }

    /**
     * Gets the residue number of the first amino acid of given chain.
     *
     * @param aChainID Chain ID.
     * @return Residue number of the first amino acid.
     */
    public int getStartResidueNumberForChain(String aChainID) {
        return this.chainIDStartResidueNumberMap.get(aChainID);
    }

    /**
     * Gets the ZMatrices matching given chain IDs.
     *
     * @param aChainIDs List of chains to consider.
     * @return Chain ID / z-matrices map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, ZMatrix> getZMatrices(ArrayList<String> aChainIDs) throws DpdPeptideException {
        HashMap<String, ZMatrix> tmpChainIDZMatrices = new HashMap<String, ZMatrix>();
        for (String tmpChainID : aChainIDs) {
            try {
                Chain tmpChain = this.chainIDChainMap.get(tmpChainID);
                Atom[] tmpCAAtoms = StructureTools.getAtomArray(tmpChain, new String[]{"CA"});
                ZMatrix tmpZMatrix = this.calculateZMatrix(tmpCAAtoms);
                tmpChainIDZMatrices.put(tmpChainID, tmpZMatrix);

            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                throw new DpdPeptideException("Peptide.ErrorCreatingZMatrix", anException);
            }
        }
        return tmpChainIDZMatrices;
    }

    /**
     * Gets a single ZMatrix for given protein chains.
     *
     * @param aChainIDs Active chains IDs.
     * @return Calculated ZMatrix.
     * @throws DpdPeptideException DpdPeptideException
     */
    public ZMatrix getZMatrix(ArrayList<String> aChainIDs) throws DpdPeptideException {
        ZMatrix tmpZMatrix = null;
        try {
            ArrayList<Atom> tmpCAAtomsList = new ArrayList<Atom>();
            for (String tmpChainID : aChainIDs) {
                Chain tmpChain = this.chainIDChainMap.get(tmpChainID);
                tmpCAAtomsList.addAll(Arrays.asList(StructureTools.getAtomArray(tmpChain, new String[]{"CA"})));
            }
            Atom[] tmpAtoms = new Atom[tmpCAAtomsList.size()];
            tmpAtoms = tmpCAAtomsList.toArray(tmpAtoms);
            tmpZMatrix = this.calculateZMatrix(tmpAtoms);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new DpdPeptideException("Peptide.ErrorCreatingZMatrix", anException);
        }
        return tmpZMatrix;
    }

    /**
     * Calculates a ZMatrix from givn atom array.
     *
     * @param aCAAtoms Atom array
     * @return Calculated ZMatrix.
     * @throws DpdPeptideException
     * @throws StructureException
     */
    private ZMatrix calculateZMatrix(Atom[] aCAAtoms) throws DpdPeptideException, StructureException {
        ZMatrix tmpZMatrix = new ZMatrix();
        // Distance between the first atoms
        tmpZMatrix.addFirstDistance(Calc.getDistance(aCAAtoms[0], aCAAtoms[1]));
        // Distance between the second and the third atom and angle between the first three atoms
        double tmpDistance = Calc.getDistance(aCAAtoms[1], aCAAtoms[2]);
        double tmpAngle = this.calcAngle(aCAAtoms[0], aCAAtoms[1], aCAAtoms[2]);
        tmpZMatrix.addFirstDistanceAnglePair(tmpDistance, tmpAngle);
        // Fill ZMatrix for remaining atoms.
        double tmpDihedralAngle;
        for (int k = 0; k < aCAAtoms.length - 3; k++) {
            tmpDistance = Calc.getDistance(aCAAtoms[k + 3], aCAAtoms[k + 2]);
            tmpAngle = this.calcAngle(aCAAtoms[k + 3], aCAAtoms[k + 2], aCAAtoms[k + 1]);
            if (tmpAngle < 0.0) {
                tmpAngle += Math.PI * 2;
            }
            tmpDihedralAngle = Calc.torsionAngle(aCAAtoms[k + 3], aCAAtoms[k + 2],
                    aCAAtoms[k + 1], aCAAtoms[k]) * Calc.radiansPerDegree;
            if (tmpDihedralAngle < 0.0) {
                tmpDihedralAngle += Math.PI * 2;
            }
            tmpZMatrix.addDistanceAngleDihedralAngleTriplet(tmpDistance, tmpAngle, tmpDihedralAngle);
        }
        return tmpZMatrix;
    }

    /**
     * Calculates the angle in radion between three atoms.
     *
     * @param anOne Atom one.
     * @param aTwo Atom two.
     * @param aThree Atom three
     * @return The angle in radians.
     */
    private double calcAngle(Atom anOne, Atom aTwo, Atom aThree) {
        Vector3d tmpVectorA = new Vector3d(anOne.getX() - aTwo.getX(), anOne.getY() - aTwo.getY(),
                anOne.getZ() - aTwo.getZ());
        Vector3d tmpVectorB = new Vector3d(aThree.getX() - aTwo.getX(), aThree.getY() - aTwo.getY(),
                aThree.getZ() - aTwo.getZ());
        return tmpVectorA.angle(tmpVectorB);
    }

    /**
     * Gets all chain names mapping their IDs.
     *
     * @return Chain names / chain ID map.
     */
    public HashMap<String, String> getChainNames() {
        HashMap<String, String> tmpNameChainIDs = new HashMap<String, String>();
        for (String tmpChainID : this.chainIDs) {
            tmpNameChainIDs.put(this.createCompoundString(tmpChainID), tmpChainID);
        }
        return tmpNameChainIDs;
    }

    /**
     * Gets the compound (chain) names of this protein.
     *
     * @return The compound names
     */
    public String[] getCompoundNames() {
        String[] tmpCompundNames = new String[this.chainIDs.size()];
        for (int i = 0; i < this.chainIDs.size(); i++) {
            tmpCompundNames[i] = this.createCompoundString(this.chainIDs.get(i));
        }
        return tmpCompundNames;
    }

    /**
     * Gets all coordinates of all C-alpha-atoms mapped by chain ID.
     *
     * @return Chain ID / CA-coordinates map.
     */
    public HashMap<String, Point3d[]> getCAlphaCoordinates() {
        return this.getCAlphaCoordinates(this.chainIDs);
    }

    /**
     * Gets all cloned (!) coordinates of all C-alpha-atoms matching given chain IDs.
     *
     * @param aChainIDs List of chains to consider.
     * @return Chain-ID/CAlpha-coordinates map with cloned (!) single coordinate 
     * points.
     */
    public HashMap<String, Point3d[]> getCAlphaCoordinates(ArrayList<String> aChainIDs) {
        if (this.chainIDCAlphaCoordinates == null) {
            this.chainIDCAlphaCoordinates = new HashMap<String, Point3d[]>();
        }
        HashMap<String, Point3d[]> tmpChainIDCACoordinates = new HashMap<String, Point3d[]>();
        for (String tmpChainID : aChainIDs) {
            if (this.chainIDCAlphaCoordinates.get(tmpChainID) == null) {
                Atom[] tmpCAAtoms = StructureTools.getAtomArray(this.chainIDChainMap.get(tmpChainID), new String[]{"CA"});
                Point3d[] tmpCoordinates = new Point3d[tmpCAAtoms.length];
                for (int k = 0; k < tmpCAAtoms.length; k++) {
                    tmpCoordinates[k] = new Point3d(tmpCAAtoms[k].getCoords());
                }
                this.chainIDCAlphaCoordinates.put(tmpChainID, tmpCoordinates);
            }
            Point3d[] tmpCoordinates = this.chainIDCAlphaCoordinates.get(tmpChainID);
            Point3d[] tmpClonedCoordinates = new Point3d[tmpCoordinates.length];
            for (int i = 0; i < tmpCoordinates.length; i++) {
                tmpClonedCoordinates[i] = new Point3d(tmpCoordinates[i]);
            }
            tmpChainIDCACoordinates.put(tmpChainID, tmpClonedCoordinates);
        }
        return tmpChainIDCACoordinates;
    }

    /**
     * Return map of chains to C-alpha atom arrays.
     *
     * @param aChainIDs List of chains to consider.
     * @return Map of chains to C-alpha atom arrays.
     */
    public HashMap<String, Atom[]> getChainToCAlphaAtomsMap(ArrayList<String> aChainIDs) {
        HashMap<String, Atom[]> tmpChainIDCAAtoms = new HashMap<>();
        for (String tmpChainID : aChainIDs) {
            Atom[] tmpCAlphaAtoms = StructureTools.getAtomArray(this.chainIDChainMap.get(tmpChainID), new String[]{"CA"});
            tmpCAlphaAtoms = this.getCheckedCAlphaAtoms(tmpCAlphaAtoms);
            tmpChainIDCAAtoms.put(tmpChainID, tmpCAlphaAtoms);
        }
        return tmpChainIDCAAtoms;
    }

    /**
     * Filters all atoms which have not a valid amino acid as parent.
     *
     * @param aAtoms Atom list to filter.
     * @return Filtered atom array.
     */
    private Atom[] getCheckedCAlphaAtoms(Atom[] aAtoms) {
        ArrayList<Atom> tmpCheckedAtoms = new ArrayList<Atom>();
        for (int i = 0; i < aAtoms.length; i++) {
            if (this.aminoAcidsUtility.isThreeLetterAminoAcid(aAtoms[i].getGroup().getPDBName())) {
                tmpCheckedAtoms.add(aAtoms[i]);
            }
        }
        return tmpCheckedAtoms.toArray(new Atom[tmpCheckedAtoms.size()]);
    }

    /**
     * Gets the C-alpha atom array.
     *
     * @return C-alpha atom array.
     * @throws DpdPeptideException DpdPeptideException
     */
    public Atom[] getCAlphaAtoms() throws DpdPeptideException {
        ArrayList<Atom> tmpCAlphaAtoms = new ArrayList<Atom>();
        for (String tmpChainID : this.chainIDs) {
            tmpCAlphaAtoms.addAll(Arrays.asList(StructureTools.getAtomArray(this.chainIDChainMap.get(tmpChainID), new String[]{"CA"})));
        }
        return this.getCheckedCAlphaAtoms(tmpCAlphaAtoms.toArray(new Atom[tmpCAlphaAtoms.size()]));
    }

    /**
     * Gets all coordinates of all atoms mapped by chain ID.
     *
     * @return Chain ID / Coordinates map.
     */
    public HashMap<String, Point3d[]> getCoordinates() {
        return this.getCoordinates(this.chainIDs);
    }

    /**
     * Gets all coordinates of all atoms matching given chain IDs.
     *
     * @param aChainIDs List of chains to consider.
     * @return Chain ID / Coordinates map.
     */
    public HashMap<String, Point3d[]> getCoordinates(ArrayList<String> aChainIDs) {
        if (this.chainIDCoordinates == null) {
            this.chainIDCoordinates = new HashMap<String, Point3d[]>();
        }
        HashMap<String, Point3d[]> tmpChainIDsCoordinates = new HashMap<String, Point3d[]>();
        for (String tmpChainID : aChainIDs) {
            if (this.chainIDCoordinates.get(tmpChainID) == null) {
                Atom[] tmpAtoms = this.getAtomArray(this.chainIDChainMap.get(tmpChainID));
                Point3d[] tmpCoordinates = new Point3d[tmpAtoms.length];
                for (int k = 0; k < tmpAtoms.length; k++) {
                    tmpCoordinates[k] = new Point3d(tmpAtoms[k].getCoords());
                }
                this.chainIDCoordinates.put(tmpChainID, tmpCoordinates);
            }
            tmpChainIDsCoordinates.put(tmpChainID, this.chainIDCoordinates.get(tmpChainID));
        }
        return tmpChainIDsCoordinates;
    }

    /**
     * Getsall atoms of given chain.
     *
     * @param aChain Protein chain.
     * @return Atom array.
     */
    public Atom[] getAtomArray(Chain aChain) {
        List<Group> tmpGroups = aChain.getAtomGroups();
        List<Atom> tmpAtoms = new ArrayList<Atom>();
        for (Group g : tmpGroups) {
            for (Atom a : g.getAtoms()) {
                tmpAtoms.add(a);
            }
        }
        return (Atom[]) tmpAtoms.toArray(new Atom[tmpAtoms.size()]);
    }

    /**
     * Getsall atoms of given chain.
     *
     * @param aChainID Protein chain ID.
     * @return Atom array.
     */
    public Atom[] getAtomArray(String aChainID) {
        return this.getAtomArray(this.chainIDChainMap.get(aChainID));
    }

    /**
     * Gets all charged amino acid seuqences of the protein mapped by chain ID.
     *
     * @return Chain ID / Sequence map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSequences() throws DpdPeptideException {
        return this.getSequences(null, this.chainIDs, false);
    }

    /**
     * Gets all uncharged amino acid seuqences of the protein mapped by chain ID.
     *
     * @param aPH The pH-value.
     * @return Chain ID / Sequence map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSequences(Double aPH) throws DpdPeptideException {
        return this.getSequences(aPH, this.chainIDs, false);
    }

    /**
     * Gets all uncharged amino acid seuqences of the protein matching given chain IDs.
     *
     * @param aChainIDs List of chains to consider.
     * @return Chain ID / Sequence map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSequences(ArrayList<String> aChainIDs) throws DpdPeptideException {
        return this.getSequences(null, aChainIDs, false);
    }

    /**
     * Gets the amino acid seuqences of the protein chains matching given chain IDs. The amino acid sequences are charged according to given pH-value.
     *
     * @param aPH The pH-value.
     * @param aChainIDs List of chains to consider.
     * @return Chain ID / Sequence map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSequences(Double aPH, ArrayList<String> aChainIDs) throws DpdPeptideException {
        return this.getSequences(aPH, aChainIDs, false);
    }

    /**
     * Gets the amino acid seuqences of the protein chains matching given chain IDs. The amino acid sequences are charged according to given pH-value.
     *
     * @param aPH The pH-value.
     * @param aChainIDs List of chains to consider.
     * @param anIsCircular Creates a circular peptide sequence.
     * @return Chain ID / Sequence map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSequences(Double aPH, ArrayList<String> aChainIDs, boolean anIsCircular) throws DpdPeptideException {
        PeptideToSpices tmpPeptideToSpices = null;
        HashMap<String, String> tmpChainIDSequences = new HashMap<String, String>();
        for (String tmpChainID : aChainIDs) {
            String tmpSequence = this.getRawSequence(tmpChainID);
            // Add SS bonds
            for (SSBond tmpSSBond : this.serNumToSsBondsMap.values()) {
                int tmpAminoAcidIndex = this.getStartResidueNumberForChain(tmpChainID) - 1;
                // Find amino acid
                boolean tmpIsChargeBracketOpen = false;
                for (int k = 0; k < tmpSequence.length(); k++) {
                    if (tmpSequence.charAt(k) == '{') {
                        tmpIsChargeBracketOpen = true;
                    }
                    if (tmpSequence.charAt(k) == '}') {
                        tmpIsChargeBracketOpen = false;
                    }
                    if (this.aminoAcidsUtility.isOneLetterAminoAcid(tmpSequence.charAt(k)) && !tmpIsChargeBracketOpen) {
                        tmpAminoAcidIndex++;
                        if (aChainIDs.contains(tmpSSBond.getChainID1()) && aChainIDs.contains(tmpSSBond.getChainID2())) {
                            if (tmpSSBond.getChainID1().equals(tmpChainID)) {
                                if (tmpAminoAcidIndex == Integer.parseInt(tmpSSBond.getResnum1())) {
                                    tmpSequence = tmpSequence.substring(0, k + 1) + "[" + tmpSSBond.getSerNum() + "]"
                                            + tmpSequence.substring(k + 1);
                                }
                            }
                            if (tmpSSBond.getChainID2().equals(tmpChainID)) {
                                if (tmpAminoAcidIndex == Integer.parseInt(tmpSSBond.getResnum2())) {
                                    tmpSequence = tmpSequence.substring(0, k + 1) + "[" + tmpSSBond.getSerNum() + "]"
                                            + tmpSequence.substring(k + 1);
                                }
                            }
                        }
                    }
                }
            }
            // Make circular
            if (anIsCircular) {
                tmpSequence = tmpSequence.substring(0, 1) + "[*]"
                        + tmpSequence.substring(1);
                int tmpLastAminoAcidIndex = 0;
                for (int i = 0; i < tmpSequence.length(); i++) {
                    if (Character.isLetter(tmpSequence.charAt(i))) {
                        tmpLastAminoAcidIndex = i;
                    }
                }
                tmpSequence = tmpSequence.substring(0, tmpLastAminoAcidIndex + 1) + "[*]"
                        + tmpSequence.substring(tmpLastAminoAcidIndex + 1);
            }
            // Apply pH-value
            if (aPH != null) {
                if (tmpPeptideToSpices == null) {
                    tmpPeptideToSpices = new PeptideToSpices(this.aminoAcidsUtility);
                }
                tmpChainIDSequences.put(tmpChainID, tmpPeptideToSpices.chargeOneLetterPeptide(tmpSequence, aPH, anIsCircular));
            } else {
                tmpChainIDSequences.put(tmpChainID, tmpSequence);
            }
        }
        return tmpChainIDSequences;
    }

    /**
     * Gets the currently active amino acid sequence.
     *
     * @param aChainID Chain ID of sequence.
     * @return Amino acid sequence.
     */
    public String getRawSequence(String aChainID) {
        if (this.overriddenSequences.containsKey(aChainID)) {
            return this.overriddenSequences.get(aChainID);
        }
        return this.chainIDChainMap.get(aChainID).getAtomSequence();
    }

    /**
     * Gets the original amino acid sequences.
     *
     * @return Original amino acid sequences.
     */
    public HashMap<String, String> getOriginalSequences() {
        HashMap<String, String> tmpSequenceMap = new HashMap<String, String>();
        for (String tmpChainID : this.chainIDs) {
            String tmpSequence = this.chainIDChainMap.get(tmpChainID).getAtomSequence();
            tmpSequenceMap.put(tmpChainID, tmpSequence);
        }
        return tmpSequenceMap;
    }

    /**
     * Overrides the original amino acid sequences.
     *
     * @param aSequence The new sequence.
     * @param aChainID Chain ID.
     */
    public void overrideSequence(String aChainID, String aSequence) {
        this.overriddenSequences.put(aChainID, aSequence);
    }

    /**
     * Removes all overridden sequences.
     */
    public void clearOverriddenSequences() {
        this.overriddenSequences.clear();
    }

    /**
     * Gets whether overridden sequences are available.
     *
     * @return True if sequences are overridden.
     */
    public boolean hasOverriddenSequences() {
        return !this.overriddenSequences.isEmpty();
    }

    /**
     * Gets the SS-bond definitions.
     *
     * @return The SS-bonds.
     */
    public String[] getSSBonds() {
        return this.getSSBonds(this.chainIDs);
    }

    /**
     * Gets the SS-bond definitions matching giving chain IDs.
     *
     * @param aChainIDs List of chains to consider.
     * @return The SS-bonds.
     */
    public String[] getSSBonds(ArrayList<String> aChainIDs) {
        ArrayList<String> tmpSSBondList = new ArrayList<String>();
        for (SSBond tmpSSBond : this.serNumToSsBondsMap.values()) {
            if (!aChainIDs.contains(tmpSSBond.getChainID1())
                    || !aChainIDs.contains(tmpSSBond.getChainID2())) {
                continue;
            }
            String tmpSSBondString = String.format("%s - ResNo.: %s",
                    this.createCompoundString(tmpSSBond.getChainID1()),
                    tmpSSBond.getResnum1());
            tmpSSBondString += " | ";
            tmpSSBondString += String.format("%s - ResNo.: %s",
                    this.createCompoundString(tmpSSBond.getChainID2()),
                    tmpSSBond.getResnum2());
            tmpSSBondList.add(tmpSSBondString);
        }
        String[] tmpSSBonds = new String[tmpSSBondList.size()];
        return tmpSSBondList.toArray(tmpSSBonds);
    }

    /**
     * Gets the raw disulfide bond list.
     *
     * @return List of disulfide bonds.
     */
    public List<SSBond> getRawSSBonds() {
        return new ArrayList(this.serNumToSsBondsMap.values());
    }

    /**
     * Gets a connections list extracted from SS bond data mapping connected ChainIDs.
     *
     * @param aChainIDs Active chains.
     * @return ChainID connection list.
     */
    public String[] getChainConnections(ArrayList<String> aChainIDs) {
        ArrayList<String> tmpConnectionList = new ArrayList<String>();
        for (SSBond tmpSSBond : this.serNumToSsBondsMap.values()) {
            if (!aChainIDs.contains(tmpSSBond.getChainID1())
                    || !aChainIDs.contains(tmpSSBond.getChainID2())) {
                continue;
            }
            String tmpConnectionString = String.format("%s;%s", tmpSSBond.getChainID1(), tmpSSBond.getChainID2());
            tmpConnectionList.add(tmpConnectionString);
        }
        String[] tmpConnections = new String[tmpConnectionList.size()];
        return tmpConnectionList.toArray(tmpConnections);
    }

    /**
     * Gets the Spices of this protein mapped by chain ID.
     *
     * @return Chain ID / Spices map
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSpices() throws DpdPeptideException {
        return this.getSpices(this.chainIDs);
    }

    /**
     * Gets the Spices of this protein matching given chain IDs.
     *
     * @param aChainIDs List of chains to consider.
     * @return Chain ID / Spices map
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSpices(ArrayList<String> aChainIDs) throws DpdPeptideException {
        return this.getSpices(null, aChainIDs);
    }

    /**
     * Gets the Spices of this protein mapped by chain ID. The amino acid Spices are charged according to given pH-value.
     *
     * @param aPH pH-value.
     * @return Chain ID / Spices map
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSpices(Double aPH) throws DpdPeptideException {
        return this.getSpices(aPH, this.chainIDs);
    }

    /**
     * Gets the Spices of this protein. The amino acid Spices are charged according to given pH-value.
     *
     * @param aPH pH-value.
     * @param aChainIDs List of chains to consider.
     * @return Chain ID / Spices map
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getSpices(Double aPH, ArrayList<String> aChainIDs) throws DpdPeptideException {
        return this.getChainIdsToSpicesMap(aPH, aChainIDs, false);
    }

    /**
     * Gets the Spices of this protein. The amino acid Spices are charged according to given pH-value.
     *
     * @param aPH pH-value.
     * @param aChainIDs List of chains to consider.
     * @param anIsCircular When set, creates a circular peptide.
     * @return Chain ID to Spices map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getChainIdsToSpicesMap(Double aPH, ArrayList<String> aChainIDs, boolean anIsCircular) throws DpdPeptideException {
        PeptideToSpices tmpPeptideToSpices = new PeptideToSpices(this.aminoAcidsUtility);
        HashMap<String, String> tmpChainIDsSequences = this.getSequences(aPH, aChainIDs, anIsCircular);
        HashMap<String, String> tmpChainIDsToSpicesMap = new HashMap<String, String>();
        for (String tmpChainID : aChainIDs) {
            String tmpSpices = tmpPeptideToSpices.convertOneLetterPeptideToSpices(tmpChainIDsSequences.get(tmpChainID), true);
            tmpChainIDsToSpicesMap.put(tmpChainID, tmpSpices);
        }
        return tmpChainIDsToSpicesMap;
    }

    /**
     * Calculates the center of mass for every chain.
     *
     * @return Center of mass.
     */
    public Point3d calculateCenterOfMass() {
        return this.calculateCenterOfMass(this.chainIDs);

    }

    /**
     * Calcualtes the center of mass depending on given chainIDs.
     *
     * @param aChainIDs Considered chains.
     * @return Center of mass.
     */
    public Point3d calculateCenterOfMass(ArrayList<String> aChainIDs) {
        int tmpNumberOfAtoms = 0;
        Point3d tmpCenterOfMass = new Point3d();
        for (String tmpChainID : aChainIDs) {
            Atom[] tmpAtoms = this.getAtomArray(tmpChainID);
            for (Atom tmpAtom : tmpAtoms) {
                if (tmpAtom.getGroup() instanceof AminoAcidImpl) {
                    tmpCenterOfMass.add(new Point3d(tmpAtom.getCoords()));
                    tmpNumberOfAtoms++;
                }
            }
        }
        tmpCenterOfMass.setX(tmpCenterOfMass.getX() / tmpNumberOfAtoms);
        tmpCenterOfMass.setY(tmpCenterOfMass.getY() / tmpNumberOfAtoms);
        tmpCenterOfMass.setZ(tmpCenterOfMass.getZ() / tmpNumberOfAtoms);
        return tmpCenterOfMass;
    }

    /**
     * Generates an unique compound name.
     *
     * @param aChainID Chain ID.
     * @return Unique compound name.
     */
    public String createCompoundString(String aChainID) {
        ChainImpl tmpChain = (ChainImpl) this.chainIDChainMap.get(aChainID);
        if (this.chainIDs.size() <= 1) {
            // Original code:
            // return String.format("%s", tmpChain.getHeader().getMolName());
            Compound tmpCompound = tmpChain.getHeader();
            if (tmpCompound != null) {
                return String.format("%s", tmpCompound.getMolName());
            } else {
                return String.format("ChainID: %s", aChainID);
            }
        } else {
            if (tmpChain.getHeader() != null) {
                return String.format("%s - ChainID: %s", tmpChain.getHeader().getMolName(), aChainID);
            } else {
                return String.format("ChainID: %s", aChainID);
            }
        }
    }

    /**
     * Gets the PDB code.
     *
     * @return PDB code.
     */
    public String getPDBCode() {
        return this.proteinStructure.getPDBCode();
    }

    /**
     * Gets the title of the protein.
     *
     * @return Pdb title.
     */
    public String getTitle() {
        if (this.proteinStructure.getPDBHeader().getTitle() != null && !this.proteinStructure.getPDBHeader().getTitle().isEmpty()) {
            return String.format("%s - %s", this.proteinStructure.getPDBCode(), this.proteinStructure.getPDBHeader().getTitle());
        } else if (this.proteinStructure.getName() != null && !this.proteinStructure.getName().isEmpty()) {
            return String.format("%s - %s", this.proteinStructure.getPDBCode(), this.proteinStructure.getName());
        } else {
            return this.proteinStructure.getPDBCode();
        }
    }

    /**
     * Gets the number of models in current assembly.
     *
     * @return Number of models.
     */
    public int getNumberOfModels() {
        return this.proteinStructure.nrModels();
    }

}
