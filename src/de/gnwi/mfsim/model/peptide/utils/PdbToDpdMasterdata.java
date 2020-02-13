/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
package de.gnwi.mfsim.model.peptide.utils;

import de.gnwi.mfsim.model.peptide.base.AminoAcid;
import de.gnwi.mfsim.model.peptide.base.AminoAcidsUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

/**
 * Masterdata class for the PdbToDPD class.
 *
 * @author Andreas Truszkowski
 */
public class PdbToDpdMasterdata extends AbstractMasterdata {

    private static final String ORIGINAL_PDB = "ORIGINAL_PDB";

    private static final String ACTIVE_CHAINS = "ACTIVE_CHAINS";

    private static final String PH_VALUE = "PH_VALUE";

    private static final String RADIUS = "RADIUS";

    private static final String CENTER = "CENTER";

    private static final String ROTATION = "ROTATION";

    private static final String DEFAULT_ROTATION = "DEFAULT_ROTATION";

    private static final String LAST_INDEX = "LAST_INDEX";

    private static final String LAST_CALPHA_INDEX = "LAST_CALPHA_INDEX";

    private static final String NUMBER_OF_DECIMALS_FOR_PARAMETERS = "NUMBER_OF_DECIMALS_FOR_COORDINATES";

    private static final String CA_ATOM_INDEX_PROBE_MAP = "CA_ATOM_INDEX_PROBE_MAP";

    private static final String BACKBONE_PARTICLE_STATUS = "BACKBONE_PARTICLE_STATUS";

    private static final String BACKBONE_PARTICLE_SEGMENTS = "BACKBONE_PARTICLE_SEGMENTS";

    private static final String IS_CIRCULAR = "IS_CIRCULAR";

    private static final String OVERRIDDEN_SEQUENCES = "OVERRIDDEN_SEQUENCES";

    private static final String BIOLOGICAL_ASSEMBLY = "BIOLOGICAL_ASSEMBLY";

    private static final String BIOLOGICAL_ASSEMBLY_FILTER = "BIOLOGICAL_ASSEMBLY_FILTER";

    private static final String NUMBER_OF_MODELS_ASSEMBLY = "NUMBER_OF_MODELS_ASSEMBLY";

    private static final String USE_SPICES_FREQUENCIES = "USE_FSMILES_FREQUENCIES";

    private static final String SEED = "SEED";

    private static final String AMINO_ACIDS_DEFINITION = "AMINO_ACIDS_DEFINITION";

    /**
     * Creates a new instance.
     *
     * @param aXmlMasterdata Serialzed master data
     * @throws DpdPeptideException DpdPeptideException
     */
    public PdbToDpdMasterdata(String aXmlMasterdata) throws DpdPeptideException {
        super(aXmlMasterdata, "PDBTODPD_MASTERDATA");
    }

    /**
     * Creates a new instance.
     */
    public PdbToDpdMasterdata() {
        super("PDBTODPD_MASTERDATA");
        // Use frequency Spices by default:
        this.put(PdbToDpdMasterdata.USE_SPICES_FREQUENCIES, String.valueOf(true));
        this.put(PdbToDpdMasterdata.VERSION, "1.0.0.0");
    }

    /**
     * Gets whether to use frequency Spices.
     *
     * @return False (default), for non-frequency Spices.
     */
    public boolean isUseOfFrequencySpices() {
        if (!this.containsKey(PdbToDpdMasterdata.USE_SPICES_FREQUENCIES)) {
            return false;
        } else {
            return Boolean.valueOf(this.get(PdbToDpdMasterdata.USE_SPICES_FREQUENCIES));
        }
    }

    /**
     * Sets whether to use frequency Spices.
     *
     * @param anUseFrequencySpices False (default), for non-frequency Spices..
     */
    public void setUseOfFrequencySpices(boolean anUseFrequencySpices) {
        this.put(PdbToDpdMasterdata.USE_SPICES_FREQUENCIES, String.valueOf(anUseFrequencySpices));
    }

    /**
     * Sets the number of decimals for parameters.
     *
     * @param aDecimals Number of decimals for parameters.
     */
    public void setNumberOfDecimalsForParameters(Integer aDecimals) {
        this.put(PdbToDpdMasterdata.NUMBER_OF_DECIMALS_FOR_PARAMETERS, String.valueOf(aDecimals));
    }

    /**
     * Gets the number of decimals for parameters.
     *
     * @return Number of decimals for parameters.
     */
    public Integer getNumberOfDecimalsForParameters() {
        if (!this.containsKey(PdbToDpdMasterdata.NUMBER_OF_DECIMALS_FOR_PARAMETERS)) {
            return 6;
        } else {
            return Integer.valueOf(this.get(PdbToDpdMasterdata.NUMBER_OF_DECIMALS_FOR_PARAMETERS));
        }
    }

    /**
     * Sets the active chains. Only active chains will be processed.
     *
     * @param anActiveChains List containing chain IDs.
     */
    public void setActiveChains(ArrayList<String> anActiveChains) {
        String tmpActiveChainsString = "";
        for (String s : anActiveChains) {
            tmpActiveChainsString += s + ";";
        }
        this.put(PdbToDpdMasterdata.ACTIVE_CHAINS, tmpActiveChainsString);
    }

    /**
     * Gets the active chains.
     *
     * @return List of active chain IDs.
     */
    public ArrayList<String> getActiveChains() {
        if (!this.containsKey(PdbToDpdMasterdata.ACTIVE_CHAINS)) {
            return null;
        }
        String[] tmpActiveChainTokens = ((String) this.get(PdbToDpdMasterdata.ACTIVE_CHAINS)).split(";");
        ArrayList<String> tmpActiveChains = new ArrayList<String>(Arrays.asList(tmpActiveChainTokens));
        Collections.sort(tmpActiveChains);
        return tmpActiveChains;
    }

    /**
     * Gets the center of the protein.
     *
     * @return The center of the protein. Null if not set.
     */
    public Point3d getCenter() {
        if (!this.containsKey(PdbToDpdMasterdata.CENTER)) {
            return null;
        }
        String[] tmpPointTokens = ((String) this.get(PdbToDpdMasterdata.CENTER)).split(";");
        double[] tmpPointData = {Double.parseDouble(tmpPointTokens[0]), Double.parseDouble(tmpPointTokens[1]),
            Double.parseDouble(tmpPointTokens[2])};
        return new Point3d(tmpPointData);
    }

    /**
     * Sets the center of the protein.
     *
     * @param aCenter The center of the protein.
     */
    public void setCenter(Point3d aCenter) {
        String tmpPointString = String.format(Locale.US, "%f;%f;%f", aCenter.x, aCenter.y, aCenter.z);
        this.put(PdbToDpdMasterdata.CENTER, tmpPointString);
    }

    /**
     * Sets the last index.
     *
     * @param aLastIndex Last index.
     */
    public void setLastIndex(int aLastIndex) {
        this.put(PdbToDpdMasterdata.LAST_INDEX, String.valueOf(aLastIndex));
    }

    /**
     * Gets the last index.
     *
     * @return Last index. 0 if not set.
     */
    public int getLastIndex() {
        if (!this.containsKey(PdbToDpdMasterdata.LAST_INDEX)) {
            return 0;
        } else {
            return Integer.valueOf(this.get(PdbToDpdMasterdata.LAST_INDEX));
        }
    }

    /**
     * Sets the last index.
     *
     * @param aLastIndex Last index.
     */
    public void setLastCAlphaIndex(int aLastIndex) {
        this.put(PdbToDpdMasterdata.LAST_CALPHA_INDEX, String.valueOf(aLastIndex));
    }

    /**
     * Gets the last C-alpha index.
     *
     * @return Last C-alpha index. 0 if not set.
     */
    public int getLastCAlphaIndex() {
        if (!this.containsKey(PdbToDpdMasterdata.LAST_CALPHA_INDEX)) {
            return 0;
        } else {
            return Integer.valueOf(this.get(PdbToDpdMasterdata.LAST_CALPHA_INDEX));
        }
    }

    /**
     * Gets the original Pdb string.
     *
     * @return Pdb string.
     */
    public String getOriginalPdb() {
        return this.containsKey(PdbToDpdMasterdata.ORIGINAL_PDB) ? (String) this.get(PdbToDpdMasterdata.ORIGINAL_PDB) : null;
    }

    /**
     * Sets the original Pdb string.
     *
     * @param aOriginalPdb Pdb string.
     */
    public void setOriginalPdb(String aOriginalPdb) {
        this.put(PdbToDpdMasterdata.ORIGINAL_PDB, aOriginalPdb);
    }

    /**
     * Gets the rotation as quaternion.
     *
     * @return Rotation quaternion. Null if not set.
     */
    public Quat4d getRotation() {
        if (!this.containsKey(PdbToDpdMasterdata.ROTATION)) {
            return null;
        }
        String[] tmpQuaternionTokens = ((String) this.get(PdbToDpdMasterdata.ROTATION)).split(";");
        double[] tmpQuaternionData = {Double.parseDouble(tmpQuaternionTokens[0]), Double.parseDouble(tmpQuaternionTokens[1]),
            Double.parseDouble(tmpQuaternionTokens[2]), Double.parseDouble(tmpQuaternionTokens[3])};
        return new Quat4d(tmpQuaternionData);
    }

    /**
     * Gets the Jmol rotation script.
     *
     * @return Rotation script. Null if not set.
     */
    public String getRotationScript() {
        if (!this.containsKey(PdbToDpdMasterdata.ROTATION)) {
            return null;
        }
        // Convert aqngle back to Jmol.
        Quat4d tmpRotation = this.getRotation();
        Quat4d tmpNomalizeToJmol = new Quat4d(Math.sin(Math.PI / 4.0), 0, 0, -Math.cos(Math.PI / 4.0));
        tmpRotation.mul(tmpNomalizeToJmol);
        String tmpRotationString = String.format(Locale.US, "rotate QUATERNION {%f %f %f %f}", tmpRotation.x, tmpRotation.y, tmpRotation.z, tmpRotation.w);
        return tmpRotationString;
    }

    /**
     * Sets the rotation as quaternion.
     *
     * @param aRotation Roation quaternion.
     */
    public void setRotation(Quat4d aRotation) {
        String tmpRotationString = String.format(Locale.US, "%f;%f;%f;%f", aRotation.x, aRotation.y, aRotation.z, aRotation.w);
        this.put(PdbToDpdMasterdata.ROTATION, tmpRotationString);
    }

    /**
     * Copies current rotation to default rotation
     */
    public void copyRotationToDefault() {
        if (this.containsKey(PdbToDpdMasterdata.ROTATION)) {
            this.put(PdbToDpdMasterdata.DEFAULT_ROTATION, this.get(PdbToDpdMasterdata.ROTATION));
        } else {
            return;
        }
    }
    
    /**
     * Sets rotation to default rotation
     */
    public void setDefaultRotation() {
        if (this.containsKey(PdbToDpdMasterdata.DEFAULT_ROTATION)) {
            this.put(PdbToDpdMasterdata.ROTATION, this.get(PdbToDpdMasterdata.DEFAULT_ROTATION));
        } else {
            return;
        }
    }
    
    /**
     * Gets the active biological assembly.
     *
     * @return Biological assembly.
     */
    public String getBiologicalAssembly() {
        return this.containsKey(PdbToDpdMasterdata.BIOLOGICAL_ASSEMBLY) ? (String) this.get(PdbToDpdMasterdata.BIOLOGICAL_ASSEMBLY) : "Asymmetric Unit";
    }

    /**
     * Sets the active biological assembly.
     *
     * @param aBiologicalAssembly Biological assembly.
     */
    public void setBiologicalAssembly(String aBiologicalAssembly) {
        this.put(PdbToDpdMasterdata.BIOLOGICAL_ASSEMBLY, aBiologicalAssembly);
    }

    /**
     * Gets the active biological assembly filter.
     *
     * @return Biological assembly filter.
     */
    public String getBiologicalAssemblyFilter() {
        return this.containsKey(PdbToDpdMasterdata.BIOLOGICAL_ASSEMBLY_FILTER) ? (String) this.get(PdbToDpdMasterdata.BIOLOGICAL_ASSEMBLY_FILTER) : null;
    }

    /**
     * Sets the active biological assembly filter.
     *
     * @param aBiologicalAssemblyFilter Biological assembly filter.
     */
    public void setBiologicalAssemblyFilter(String aBiologicalAssemblyFilter) {
        if (aBiologicalAssemblyFilter == null) {
            this.remove(PdbToDpdMasterdata.BIOLOGICAL_ASSEMBLY_FILTER);
        } else {
            this.put(PdbToDpdMasterdata.BIOLOGICAL_ASSEMBLY_FILTER, aBiologicalAssemblyFilter);
        }
    }

    /**
     * Sets the C-alpha index probe map.
     *
     * @return C-alpha atom indices mapped to probe particle names.
     */
    public HashMap<String, String> getCAlphaAtomIndexProbeMap() {
        if (!this.containsKey(PdbToDpdMasterdata.CA_ATOM_INDEX_PROBE_MAP)) {
            return new HashMap<String, String>();
        }
        HashMap<String, String> tmpCAAtomIndexProbeMap = new HashMap<String, String>();
        String tmpValues = (String) this.get(PdbToDpdMasterdata.CA_ATOM_INDEX_PROBE_MAP);
        for (String tmpEntry : tmpValues.split(";")) {
            String[] tmpEntryTokens = tmpEntry.split("_");
            tmpCAAtomIndexProbeMap.put(tmpEntryTokens[0], tmpEntryTokens[1]);
        }
        return tmpCAAtomIndexProbeMap;
    }

    /**
     * Sets the C-alpha index probe map.
     *
     * @param aCAAtomIndexProbeMap Container mapping C-alpha-indices to probe particles.
     */
    public void setCAlphaAtomIndexProbeMap(HashMap<String, String> aCAAtomIndexProbeMap) {
        if (aCAAtomIndexProbeMap == null || aCAAtomIndexProbeMap.isEmpty()) {
            this.remove(PdbToDpdMasterdata.CA_ATOM_INDEX_PROBE_MAP);
            return;
        }
        StringBuilder tmpValues = new StringBuilder();
        for (Map.Entry<String, String> tmpEntry : aCAAtomIndexProbeMap.entrySet()) {
            tmpValues.append(String.format("%s_%s;", tmpEntry.getKey(), tmpEntry.getValue()));
        }
        this.put(PdbToDpdMasterdata.CA_ATOM_INDEX_PROBE_MAP, tmpValues.toString());
    }

    /**
     * Gets the protein radius.
     *
     * @return Protein radius.
     */
    public Double getRadius() {
        return this.containsKey(PdbToDpdMasterdata.RADIUS)
                ? Double.parseDouble((String) this.get(PdbToDpdMasterdata.RADIUS)) : null;
    }

    /**
     * Sets the protein radius.
     *
     * @param aRadius The protein radius. Null if not set.
     */
    public void setRadius(Double aRadius) {
        if (aRadius == null) {
            this.remove(PdbToDpdMasterdata.RADIUS);
        } else {
            this.put(PdbToDpdMasterdata.RADIUS, aRadius.toString());
        }
    }

    /**
     * Gets the pH-value.
     *
     * @return The pH-value.
     */
    public Double getPhValue() {
        return this.containsKey(PdbToDpdMasterdata.PH_VALUE)
                ? Double.parseDouble((String) this.get(PdbToDpdMasterdata.PH_VALUE)) : null;
    }

    /**
     * Sets the pH-value.
     *
     * @param aPhValue The pH-value. Null if not set.
     */
    public void setPhValue(Double aPhValue) {
        if (aPhValue == null) {
            this.remove(PdbToDpdMasterdata.PH_VALUE);
        } else {
            this.put(PdbToDpdMasterdata.PH_VALUE, aPhValue.toString());
        }
    }

    /**
     * Gets the data version.
     *
     * @return Data version
     */
    public String getVersion() {
        return (String) this.get(PdbToDpdMasterdata.VERSION);
    }

    /**
     * Sets the backbone particle status.
     *
     * @param aBackboneParticleStatusArray The backbone particle status.
     */
    public void setBackboneParticleStatusArray(boolean[] aBackboneParticleStatusArray) {
        if (aBackboneParticleStatusArray == null) {
            this.remove(PdbToDpdMasterdata.BACKBONE_PARTICLE_STATUS);
        } else {
            StringBuilder tmpBuilder = new StringBuilder();
            for (int i = 0; i < aBackboneParticleStatusArray.length; i++) {
                tmpBuilder.append(String.format("%b;", aBackboneParticleStatusArray[i]));
            }
            this.put(PdbToDpdMasterdata.BACKBONE_PARTICLE_STATUS, tmpBuilder.toString());
        }
    }

    /**
     * Gets the backbone particle status.
     *
     * @return The backbone particle status.
     */
    public boolean[] getBackboneParticleStatusArray() {
        if (!this.containsKey(PdbToDpdMasterdata.BACKBONE_PARTICLE_STATUS)) {
            return null;
        } else {
            String[] tmpBackboneFagmentStatusTokenArray = String.valueOf(this.get(PdbToDpdMasterdata.BACKBONE_PARTICLE_STATUS)).split(";");
            boolean[] tmpBackboneParticleStatusArray = new boolean[tmpBackboneFagmentStatusTokenArray.length];
            for (int i = 0; i < tmpBackboneFagmentStatusTokenArray.length; i++) {
                tmpBackboneParticleStatusArray[i] = Boolean.parseBoolean(tmpBackboneFagmentStatusTokenArray[i]);
            }
            return tmpBackboneParticleStatusArray;
        }
    }

    /**
     * Sets the backbone particle segment array.
     *
     * @param aBackboneParticleSegmentArray The backbone particle segment array.
     */
    public void setBackboneParticleSegmentArray(int[] aBackboneParticleSegmentArray) {
        if (aBackboneParticleSegmentArray == null) {
            this.remove(PdbToDpdMasterdata.BACKBONE_PARTICLE_SEGMENTS);
        } else {
            StringBuilder tmpBuilder = new StringBuilder();
            for (int i = 0; i < aBackboneParticleSegmentArray.length; i++) {
                tmpBuilder.append(String.format("%d;", aBackboneParticleSegmentArray[i]));
            }
            this.put(PdbToDpdMasterdata.BACKBONE_PARTICLE_SEGMENTS, tmpBuilder.toString());
        }
    }

    /**
     * Gets the backbone particle segment array.
     *
     * @return The backbone particle segment array.
     */
    public int[] getBackboneParticleSegmentArray() {
        if (!this.containsKey(PdbToDpdMasterdata.BACKBONE_PARTICLE_SEGMENTS)) {
            return null;
        } else {
            String[] tmpBackboneFagmentStatusTokenArray = String.valueOf(this.get(PdbToDpdMasterdata.BACKBONE_PARTICLE_SEGMENTS)).split(";");
            int[] tmpBackboneParticleStatusArray = new int[tmpBackboneFagmentStatusTokenArray.length];
            for (int i = 0; i < tmpBackboneFagmentStatusTokenArray.length; i++) {
                tmpBackboneParticleStatusArray[i] = Integer.parseInt(tmpBackboneFagmentStatusTokenArray[i]);
            }
            return tmpBackboneParticleStatusArray;
        }
    }

    /**
     * Gets the Pseudorandom generator seed.
     *
     * @return Pseudorandom generator seed.
     */
    public long getSeed() {
        if (!this.containsKey(PdbToDpdMasterdata.SEED)) {
            return 1;
        } else {
            return Long.valueOf(this.get(PdbToDpdMasterdata.SEED));
        }
    }

    /**
     * Sets the Pseudorandom generator seed.
     *
     * @param aSeed Pseudorandom generator seed.
     */
    public void setSeed(long aSeed) {
        this.put(PdbToDpdMasterdata.SEED, String.valueOf(aSeed));
    }

    /**
     * Gets amino acids definition.
     *
     * @return Amino acids definition.
     */
    public String getAminoAcidsDefinition() {
        if (!this.containsKey(PdbToDpdMasterdata.AMINO_ACIDS_DEFINITION)) {
            return null;
        } else {
            return this.get(PdbToDpdMasterdata.AMINO_ACIDS_DEFINITION);
        }
    }

    /**
     * Sets amino acids definition.
     *
     * @param anAminoAcidsDefinition Amino acids definition..
     */
    public void setAminoAcidsDefinition(String anAminoAcidsDefinition) {
        this.put(PdbToDpdMasterdata.AMINO_ACIDS_DEFINITION, anAminoAcidsDefinition);
    }

    /**
     * Sets whether the amino acid sequence is circular.
     *
     * @param anIsCircular True if amino acid sequence is circular.
     */
    public void setCircular(boolean anIsCircular) {
        this.put(PdbToDpdMasterdata.IS_CIRCULAR, String.valueOf(anIsCircular));
    }

    /**
     * Gets whether the amino acid sequence is circular.
     *
     * @return True if amino acid sequence is circular.
     */
    public boolean isCircular() {
        if (!this.containsKey(PdbToDpdMasterdata.IS_CIRCULAR)) {
            return false;
        }
        return Boolean.valueOf((String) this.get(PdbToDpdMasterdata.IS_CIRCULAR));
    }

    /**
     * Gets the overridden amino acid sequences.
     *
     * @param anAminoAcidsUtility Amino acids utility
     * @return Map linking the chain ID to the overridden Sequence.
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException DpdPeptideException
     */
    public HashMap<String, AminoAcid[]> getOverriddenSequences(AminoAcidsUtility anAminoAcidsUtility) throws DpdPeptideException {
        if (anAminoAcidsUtility == null) {
            throw new DpdPeptideException("Peptide.MissingAminoAcidsUtility");
        }
        if (!this.containsKey(PdbToDpdMasterdata.OVERRIDDEN_SEQUENCES)) {
            return new HashMap<String, AminoAcid[]>();
        }
        HashMap<String, AminoAcid[]> tmpOverriddenSequencesMap = new HashMap<String, AminoAcid[]>();
        String tmpValues = (String) this.get(PdbToDpdMasterdata.OVERRIDDEN_SEQUENCES);
        for (String tmpEntry : tmpValues.split(";")) {
            String[] tmpEntryTokens = tmpEntry.split("_");
            AminoAcid[] tmpSequence = new AminoAcid[tmpEntryTokens[1].length()];
            for (int i = 0; i < tmpEntryTokens[1].length(); i++) {
                tmpSequence[i] = anAminoAcidsUtility.getAminoAcidFromOneLetterCode(tmpEntryTokens[1].charAt(i));
            }
            tmpOverriddenSequencesMap.put(tmpEntryTokens[0], tmpSequence);
        }
        return tmpOverriddenSequencesMap;
    }

    /**
     * Sets an overridden amino acid sequence.
     *
     * @param aChainID Chain ID.
     * @param aSequence The new amino acid sequence.
     * @param anAminoAcidsUtility Amino acids utility
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException DpdPeptideException
     */
    public void setOverriddenSequence(String aChainID, AminoAcid[] aSequence, AminoAcidsUtility anAminoAcidsUtility) throws DpdPeptideException {
        HashMap<String, AminoAcid[]> overriddenSequencesMap = this.getOverriddenSequences(anAminoAcidsUtility);
        overriddenSequencesMap.put(aChainID, aSequence);
        StringBuilder tmpValues = new StringBuilder();
        for (Map.Entry<String, AminoAcid[]> tmpEntry : overriddenSequencesMap.entrySet()) {
            String tmpSequence = "";
            for (AminoAcid tmpAminoAcid : tmpEntry.getValue()) {
                tmpSequence += tmpAminoAcid.getOneLetterCode();
            }
            tmpValues.append(String.format("%s_%s;", tmpEntry.getKey(), tmpSequence));
        }
        this.put(PdbToDpdMasterdata.OVERRIDDEN_SEQUENCES, tmpValues.toString());
    }

    /**
     * Removes the sequence overrides.
     */
    public void clearOverriddenSequences() {
        this.remove(PdbToDpdMasterdata.OVERRIDDEN_SEQUENCES);
    }

    /**
     * Sets the number of models in current assembly.
     *
     * @param aNumberOfModels Number of models in current assembly.
     */
    public void setNumberOfModelsInAssembly(int aNumberOfModels) {
        this.put(PdbToDpdMasterdata.NUMBER_OF_MODELS_ASSEMBLY, String.valueOf(aNumberOfModels));
    }

    /**
     * Gets the number of models in current assembly.
     *
     * @return Number of models in current assembly.
     */
    public int getNumberOfModelsInAssembly() {
        if (!this.containsKey(PdbToDpdMasterdata.NUMBER_OF_MODELS_ASSEMBLY)) {
            return 1;
        } else {
            return Integer.valueOf(this.get(PdbToDpdMasterdata.NUMBER_OF_MODELS_ASSEMBLY));
        }
    }

}
