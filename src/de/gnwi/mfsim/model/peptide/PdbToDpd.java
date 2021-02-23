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
package de.gnwi.mfsim.model.peptide;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.peptide.base.AminoAcid;
import de.gnwi.mfsim.model.peptide.base.AminoAcids;
import de.gnwi.mfsim.model.peptide.base.AminoAcidsUtility;
import de.gnwi.mfsim.model.peptide.base.Protein;
import de.gnwi.mfsim.model.peptide.base.ZMatrix;
import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import de.gnwi.mfsim.model.peptide.utils.PdbToDpdMasterdata;
import de.gnwi.mfsim.model.peptide.utils.Tools;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.biojava.bio.structure.AminoAcidImpl;
import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Element;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.SSBond;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.biojava.bio.structure.io.PDBFileParser;
import org.biojava.bio.structure.quaternary.BiologicalAssemblyBuilder;
import org.biojava.bio.structure.quaternary.BiologicalAssemblyTransformation;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticlePosition;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Class converting PDB data to a valid format for MFsim.
 *
 * @author Andreas Truszkowski, Achim Zielesny
 */
public final class PdbToDpd {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility string methods
     */
    private StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();
    
    /**
     * Structure representing a DPD protein.
     */
    private Protein protein = null;

    /**
     * Random number generator
     */
    private Random random = null;

    /**
     * Masterdata object.
     */
    private PdbToDpdMasterdata masterdata = null;

    /**
     * Amino acids utility
     */
    private AminoAcidsUtility aminoAcidsUtility = null;

    /**
     * Amino acids definition
     */
    private String aminoAcidsDefinition;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private Regex patterns">
    /**
     * Bracketed expression pattern
     */
    private final Pattern bracketedExpressionPattern = Pattern.compile("\\[.*\\]");

    /**
     * Bracket-close newline pattern
     */
    private final Pattern bracketCloseNewlinePattern = Pattern.compile("\\)|\\n");

    /**
     * Bracketed digits pattern
     */
    private final Pattern bracketedDigitsPattern = Pattern.compile("\\[\\d\\]");
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance. Graphical particles are NOT persistent.
     *
     * @throws DpdPeptideException DpdPeptideException
     */
    public PdbToDpd() throws DpdPeptideException {
        this.masterdata = new PdbToDpdMasterdata();
        // Get amino acids definition from amino acid singleton
        this.aminoAcidsDefinition = AminoAcids.getInstance().getAminoAcidsUtility().getAminoAcidsDefinition();
        this.masterdata.setAminoAcidsDefinition(this.aminoAcidsDefinition);
        this.aminoAcidsUtility = new AminoAcidsUtility(this.aminoAcidsDefinition);
    }

    /**
     * Creates a new instance.
     * Graphical particles are NOT persistent.
     *
     * @param aProteinData Protein data
     * @throws DpdPeptideException DpdPeptideException
     */
    public PdbToDpd(String aProteinData) throws DpdPeptideException {
        String tmpXmlMasterdata = null;
        // NOTE: Base64 strings do NOT contain a '<' character
        if (aProteinData.startsWith("<")) {
            // Legacy code: Old XML text string
            tmpXmlMasterdata = aProteinData;
        } else {
            // New compressed Base64 string
            tmpXmlMasterdata = this.stringUtilityMethods.decompressBase64String(aProteinData);
        }
        this.masterdata = new PdbToDpdMasterdata(tmpXmlMasterdata);
        this.aminoAcidsDefinition = this.masterdata.getAminoAcidsDefinition();
        if (this.aminoAcidsDefinition == null) {
            this.aminoAcidsDefinition = AminoAcids.getInstance().getAminoAcidsUtility().getAminoAcidsDefinition();
            // NOTE: If AminoAcids singleton is NOT properly initialized an DpdPeptideException is thrown
        }
        this.aminoAcidsUtility = new AminoAcidsUtility(this.aminoAcidsDefinition);
        if (!this.readPdbString(this.masterdata.getOriginalPdb())) {
            throw new DpdPeptideException("Peptide.ErrorReadMasterdata");
        }
        for (Map.Entry<String, AminoAcid[]> tmpEntry : this.masterdata.getOverriddenSequences(this.aminoAcidsUtility).entrySet()) {
            this.setAminoAcidSequence(tmpEntry.getKey(), tmpEntry.getValue());
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Loads a pdb file from given Pdb identifer.
     *
     * @param aPdbIndex 4 characters wide Pdb identifier.
     * @return True if successfully loaded.
     */
    public boolean readPdb(String aPdbIndex) {
        try {
            if (aPdbIndex.length() != 4) {
                throw new DpdPeptideException("Peptide.NotAValidPDBID");
            }
            URL website = new URL("http://www.rcsb.org/pdb/files/" + aPdbIndex + ".pdb");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(website.openStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(String.format("%s\n", line));
            }
            bufferedReader.close();
            this.readPdbString(stringBuilder.toString());
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            anException.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Reads a pdb file from a pdb resource url.
     *
     * @param aPdbResource URL of the pdb resource.
     * @return True if successfully loaded.
     */
    public boolean readPdb(URL aPdbResource) {
        try {
            this.readPdbString(Tools.readFile(aPdbResource.toURI().getPath()));
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
        return true;
    }

    /**
     * Reads a pdb file.
     *
     * @param aPdbFile File handle of the pdb resource.
     * @return True if successfully loaded.
     */
    public boolean readPdb(File aPdbFile) {
        try {
            return this.readPdbString(Tools.readFile(aPdbFile.getPath()));
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Gets the charged Spices of the protein depending on given pH-value. If
     * pH-value is set to null the Spices will not be charged. Only set as
     * active chains will be considered.
     *
     * @return The charged Spices.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String getSpices() throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        if (this.masterdata.getActiveChains().isEmpty()) {
            return "";
        }
        HashMap<String, String> tmpChainIdsToSpicesMap = this.protein.getChainIdsToSpicesMap(this.masterdata.getPhValue(), this.masterdata.getActiveChains(), this.isCircular());
        // Check for probe particles
        if (!this.masterdata.getCAlphaAtomIndexProbeMap().isEmpty()) {
            for (String tmpChainID : this.masterdata.getActiveChains()) {
                StringBuilder tmpProbeSpices = new StringBuilder();
                String[] tmpSpices = tmpChainIdsToSpicesMap.get(tmpChainID).split("\n-");
                HashMap<String, Atom[]> tmpCAAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
                for (int i = 0; i < tmpSpices.length; i++) {
                    String tmpCAlphaKey = this.createCAlphaKey(tmpCAAtomsMap.get(tmpChainID)[i]);
                    if (this.masterdata.getCAlphaAtomIndexProbeMap().containsKey(tmpCAlphaKey)) {
                        String tmpProbe = this.masterdata.getCAlphaAtomIndexProbeMap().get(tmpCAlphaKey);
                        String tmpFirstParticle = this.getCAlphaParticles(new ArrayList<String>(Arrays.asList(new String[]{tmpChainID})))[i];
                        tmpSpices[i] = tmpSpices[i].replaceFirst(tmpFirstParticle, tmpProbe);
                    }
                    tmpProbeSpices.append(tmpSpices[i]);
                    if (i < tmpSpices.length - 1) {
                        tmpProbeSpices.append("\n-");
                    }
                }
                if (!tmpProbeSpices.toString().endsWith("\n")) {
                    tmpProbeSpices.append("\n");
                }
                tmpChainIdsToSpicesMap.put(tmpChainID, tmpProbeSpices.toString());
            }
        }
        // Evaluate SS bond connections
        ArrayList<ArrayList<String>> tmpProcessedConnections = this.evaluateSSBondConnections();
        // Build Spices string
        StringBuilder tmpSpicesBuilder = new StringBuilder();
        for (ArrayList<String> tmpConnectionSet : tmpProcessedConnections) {
            if (this.masterdata.getActiveChains().size() > 1) {
                tmpSpicesBuilder.append("<");
            }
            for (int i = 0; i < tmpConnectionSet.size(); i++) {
                String tmpChainID = tmpConnectionSet.get(i);
                StringBuffer tmpRawSpices = new StringBuffer(tmpChainIdsToSpicesMap.get(tmpChainID));
                if (tmpConnectionSet.size() == 1) {
                    tmpSpicesBuilder.append(tmpRawSpices);
                } else {
                    tmpSpicesBuilder.append(String.format("(\n%s)", tmpRawSpices));
                }
            }
            if (this.masterdata.getActiveChains().size() > 1) {
                tmpSpicesBuilder.append(">");
            }
        }
        String tmpFrequencySpices = "";
        if (tmpSpicesBuilder.toString().contains("<") && this.masterdata.isUseOfFrequencySpices()) {
            // Old code:
            // String[] tmpSpicesTokens = tmpSpicesBuilder.toString().replaceAll("<", "").split(">");
            String[] tmpSpicesTokens = StringUtils.replace(tmpSpicesBuilder.toString(), "<", "").split(">");
            if (tmpSpicesTokens.length > 1) {
                int tmpFrequency = 1;
                int tmpIndex = 0;
                while (tmpIndex < tmpSpicesTokens.length - 1) {
                    if (tmpSpicesTokens[tmpIndex].equals(tmpSpicesTokens[tmpIndex + 1]) && tmpIndex != tmpSpicesTokens.length - 2) {
                        tmpFrequency++;
                    } else {
                        if (tmpSpicesTokens[tmpIndex].equals(tmpSpicesTokens[tmpIndex + 1]) && tmpIndex == tmpSpicesTokens.length - 2) {
                            tmpFrequency++;
                        }
                        if (tmpFrequency > 1) {
                            tmpFrequencySpices += tmpFrequency;
                        }
                        tmpFrequencySpices += String.format("<%s>\n", tmpSpicesTokens[tmpIndex]);
                        tmpFrequency = 1;
                        if (!tmpSpicesTokens[tmpIndex].equals(tmpSpicesTokens[tmpIndex + 1]) && tmpIndex == tmpSpicesTokens.length - 2) {
                            tmpFrequencySpices += String.format("<%s>\n", tmpSpicesTokens[tmpIndex + 1]);
                        }
                    }
                    tmpIndex++;
                }
            }
            return tmpFrequencySpices;
        } else {
            return tmpSpicesBuilder.toString();
        }
    }

    /**
     * Gets the amino acid sequences.
     *
     * @return The amino acid sequences.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String getSequences() throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        HashMap<String, String> tmpSequences = this.protein.getSequences(this.masterdata.getPhValue(), this.masterdata.getActiveChains(), this.isCircular());
        StringBuilder tmpBuilder = new StringBuilder();
        for (String tmpChainID : this.masterdata.getActiveChains()) {
            tmpBuilder.append(this.protein.createCompoundString(tmpChainID));
            tmpBuilder.append(":\n");
            tmpBuilder.append(tmpSequences.get(tmpChainID));
            tmpBuilder.append("\n");
        }
        return tmpBuilder.toString();
    }

    /**
     * Returns the coordinate and connection array. The coordinates are translated to the
     * given center und scaled to fit given radius. If rotation is not null the
     * coordinates are rotated around the center of mass. Please set pH-value
     * and active chains before executing this method. Otherwise default values
     * are applied. Protein center and radius has to be set before.
     *
     * @param aStartIndex Start index.
     * @param aCAlphaStartIndex C-alpha start index.
     * @return String representing the coordinates file.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String[] getCoordinateConnectionTableArray(int aStartIndex, int aCAlphaStartIndex) throws DpdPeptideException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        if (this.masterdata.getCenter() == null) {
            throw DpdPeptideException.MissingData("Protein center");
        }
        if (this.masterdata.getRadius() == null) {
            throw DpdPeptideException.MissingData("Protein radius");
        }
        // </editor-fold>
        LinkedList<String> tmpCoordinatesLineList = new LinkedList<>();
        StringBuilder tmpCoordinatesLine = new StringBuilder();
        HashMap<String, String> tmpSpicesMap = this.protein.getChainIdsToSpicesMap(this.masterdata.getPhValue(), this.masterdata.getActiveChains(), this.isCircular());
        HashMap<String, Point3d[]> tmpCAlphaCoordinatesMap = this.protein.getCAlphaCoordinates(this.masterdata.getActiveChains());
        HashMap<String, Atom[]> tmpCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        // Transform ccordinates
        tmpCAlphaCoordinatesMap = this.getTransformedCoordinates(tmpCAlphaCoordinatesMap, this.masterdata.getCenter(), this.masterdata.getRadius(), this.masterdata.getRotation());
        // Create coordinates lines
        int tmpIndex = aStartIndex;
        int tmpCAlphaIndex = aCAlphaStartIndex;
        int tmpSkippedCAlphaIndices = 0;
        boolean[] tmpBackboneParticleStatusArray = this.getBackboneParticleStatusArray();
        HashMap<Integer, LinkedList<Integer>> tmpDisulfideBondMap = new HashMap<Integer, LinkedList<Integer>>();
        for (String tmpChainId : this.masterdata.getActiveChains()) {
            String[] tmpAminoAcidSpicesArray = tmpSpicesMap.get(tmpChainId).split("\n-");
            Point3d[] tmpCAlphaCoordinates = tmpCAlphaCoordinatesMap.get(tmpChainId);
            int tmpCurrentCAlphaAtom = 0;
            for (int i = 0; i < tmpAminoAcidSpicesArray.length; i++) {
                String[] tmpAminoAcidParticleArray;
                if (i == 0 && tmpAminoAcidSpicesArray[i].indexOf("(") < 0) {
                    // FIRST amino acid does NOT contain a side chain: Singe-particle amino acid
                    tmpAminoAcidParticleArray = tmpAminoAcidSpicesArray[i].split("-");
                    if (tmpAminoAcidParticleArray.length > 1) {
                        // Reverse tmpAminoAcidParticleArray to get correct backbone particle in first position
                        Collections.reverse(Arrays.asList(tmpAminoAcidParticleArray));
                    }
                } else {
                    tmpAminoAcidParticleArray = tmpAminoAcidSpicesArray[i].split("-|\\(");
                }
                double[] tmpLayerDepth = new double[tmpAminoAcidParticleArray.length];
                double tmpCurrentLayer = 0;
                int tmpLayerIndex = 1;
                // Create particle layer info
                for (int j = 0; j < tmpAminoAcidSpicesArray[i].length(); j++) {
                    if (tmpAminoAcidSpicesArray[i].charAt(j) == ')') {
                        if (tmpAminoAcidSpicesArray[i].length() > j + 1 && tmpAminoAcidSpicesArray[i].charAt(j + 1) == '(') {
                            // Mark particle layer index as distinct to previous unit:
                            tmpCurrentLayer += 0.1;
                            tmpLayerDepth[tmpLayerIndex] = tmpCurrentLayer;
                            j++;
                            tmpLayerIndex++;
                        } else {
                            tmpCurrentLayer = (int) tmpCurrentLayer;
                            tmpCurrentLayer--;
                        }
                    } else if (tmpAminoAcidSpicesArray[i].charAt(j) == '(') {
                        tmpCurrentLayer++;
                        tmpLayerDepth[tmpLayerIndex] = tmpCurrentLayer;
                        tmpLayerIndex++;
                    } else if (tmpAminoAcidSpicesArray[i].charAt(j) == '-') {
                        tmpLayerDepth[tmpLayerIndex] = tmpCurrentLayer;
                        tmpLayerIndex++;
                    }
                }
                // Build geometry string
                for (int j = 0; j < tmpAminoAcidParticleArray.length; j++) {
                    // Store disulfide bond data
                    if (tmpAminoAcidParticleArray[j].contains("[")) {
                        int tmpDisulfideBondIndex = Integer.parseInt(tmpAminoAcidParticleArray[j].substring(tmpAminoAcidParticleArray[j].indexOf("[") + 1, tmpAminoAcidParticleArray[j].indexOf("]")));
                        LinkedList<Integer> tmpParticleIndices;
                        if (!tmpDisulfideBondMap.containsKey(tmpDisulfideBondIndex)) {
                            tmpParticleIndices = new LinkedList<Integer>();
                            tmpDisulfideBondMap.put(tmpDisulfideBondIndex, tmpParticleIndices);
                        } else {
                            tmpParticleIndices = tmpDisulfideBondMap.get(tmpDisulfideBondIndex);
                        }
                        tmpParticleIndices.add(tmpIndex - aStartIndex + 1);
                        // Old code:
                        // tmpAminoAcidParticleArray[j] = tmpAminoAcidParticleArray[j].replaceAll("\\[.*\\]", "");
                        tmpAminoAcidParticleArray[j] = this.bracketedExpressionPattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll("");
                    }
                    // Add line information (index, particle, ...)
                    // First check if current particle is a C-alpha atom                  
                    if (j != 0) {
                        // NOT a C-alpha particle
                        // IMPORTANT: Do NOT set protein distance force start/end index characters: <...>
                        tmpCoordinatesLine.append(String.format(Locale.US, "%d %s %d %.3f %.3f %.3f",
                            tmpIndex,
                            this.bracketCloseNewlinePattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll(""),
                            0,
                            tmpCAlphaCoordinates[i].x, 
                            tmpCAlphaCoordinates[i].y, 
                            tmpCAlphaCoordinates[i].z)
                        );
                        // Old code:
                        // tmpAminoAcidParticleArray[j].replaceAll("\\)|\\n", ""),
                    } else if (!tmpBackboneParticleStatusArray[tmpCAlphaIndex - aCAlphaStartIndex]) {
                        // Excluded C-alpha particle
                        // Old code:
                        // String tmpCAlphaParticle = tmpAminoAcidParticleArray[j].replaceAll("\\)|\\n", "");
                        String tmpCAlphaParticle = this.bracketCloseNewlinePattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll("");
                        // Exchange particle by probe?
                        String tmpCAlphaKey = this.createCAlphaKey(tmpCAlphaAtomsMap.get(tmpChainId)[tmpCurrentCAlphaAtom]);
                        if (this.masterdata.getCAlphaAtomIndexProbeMap().containsKey(tmpCAlphaKey)) {
                            tmpCAlphaParticle = this.masterdata.getCAlphaAtomIndexProbeMap().get(tmpCAlphaKey);
                        }
                        // IMPORTANT: Set protein distance force start/end index characters: <...>
                        tmpCoordinatesLine.append(String.format(Locale.US, 
                            "%d %s " + ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_START + "%d" + ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_END + " %.3f %.3f %.3f",
                            tmpIndex, 
                            tmpCAlphaParticle, 
                            0,
                            tmpCAlphaCoordinates[i].x, 
                            tmpCAlphaCoordinates[i].y, 
                            tmpCAlphaCoordinates[i].z)
                        );
                        // IMPORTANT: Increment tmpCurrentCAlphaAtom
                        tmpCurrentCAlphaAtom++;
                    } else {
                        // Non-excluded C-alpha particle
                        // Old code:
                        // String tmpCAlphaParticle = tmpAminoAcidParticleArray[j].replaceAll("\\)|\\n", "");
                        String tmpCAlphaParticle = this.bracketCloseNewlinePattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll("");
                        // Exchange particle by probe?
                        String tmpCAlphaKey = this.createCAlphaKey(tmpCAlphaAtomsMap.get(tmpChainId)[tmpCurrentCAlphaAtom]);
                        if (this.masterdata.getCAlphaAtomIndexProbeMap().containsKey(tmpCAlphaKey)) {
                            tmpCAlphaParticle = this.masterdata.getCAlphaAtomIndexProbeMap().get(tmpCAlphaKey);
                        }
                        // IMPORTANT: Set protein distance force start/end index characters: <...>
                        tmpCoordinatesLine.append(String.format(Locale.US,
                            "%d %s " + ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_START + "%d" + ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_END + " %.3f %.3f %.3f",
                            tmpIndex, 
                            tmpCAlphaParticle, 
                            tmpCAlphaIndex - tmpSkippedCAlphaIndices,
                            tmpCAlphaCoordinates[i].x, 
                            tmpCAlphaCoordinates[i].y, 
                            tmpCAlphaCoordinates[i].z)
                        );
                        // IMPORTANT: Increment tmpCurrentCAlphaAtom
                        tmpCurrentCAlphaAtom++;
                    }
                    if (i != 0 && j == 0) {
                        String[] tmpPreviousSpicesTokens = tmpAminoAcidSpicesArray[i - 1].split("-|\\(");
                        tmpCoordinatesLine.append(String.format(Locale.US, " -%d", tmpPreviousSpicesTokens.length));
                    }
                    // Find connections down the stream
                    if (j > 0) {
                        tmpCurrentLayer = tmpLayerDepth[j];
                        for (int k = j - 1; k >= 0; k--) {
                            if (tmpLayerDepth[k] == tmpCurrentLayer || tmpLayerDepth[k] == ((int) tmpCurrentLayer) - 1) {
                                tmpCoordinatesLine.append(String.format(Locale.US, " %d", k - j));
                                break;
                            }
                        }
                    }
                    // Find connections upstream in same layer and layer above
                    if (j < tmpAminoAcidParticleArray.length - 1) {
                        tmpCurrentLayer = tmpLayerDepth[j];
                        HashSet<Double> tmpConnectedHigherLayer = new HashSet<Double>();
                        for (int k = j + 1; k < tmpAminoAcidParticleArray.length; k++) {
                            if (tmpLayerDepth[k] == tmpCurrentLayer) {
                                tmpCoordinatesLine.append(String.format(Locale.US, " %d", k - j));
                                break;
                            }
                            if (((int) tmpLayerDepth[k]) == tmpCurrentLayer + 1) {
                                if (!tmpConnectedHigherLayer.contains(tmpLayerDepth[k])) {
                                    tmpCoordinatesLine.append(String.format(Locale.US, " %d", k - j));
                                    tmpConnectedHigherLayer.add(tmpLayerDepth[k]);
                                }
                            }
                        }
                    }
                    if (i != tmpAminoAcidSpicesArray.length - 1 && j == 0) {
                        tmpCoordinatesLine.append(String.format(" %d", tmpAminoAcidParticleArray.length));
                    }
                    // Add line to list
                    tmpCoordinatesLineList.add(tmpCoordinatesLine.toString());
                    // Clear line
                    tmpCoordinatesLine.setLength(0);
                    tmpIndex++;
                }
                if (!tmpBackboneParticleStatusArray[tmpCAlphaIndex - aCAlphaStartIndex]) {
                    tmpSkippedCAlphaIndices++;
                }
                tmpCAlphaIndex++;
            }
        }
        this.masterdata.setLastIndex(tmpIndex - 1);
        this.masterdata.setLastCAlphaIndex(tmpCAlphaIndex - tmpSkippedCAlphaIndices - 1);
        // Add disulfide bond connections
        String[] tmpCoordinatesLineArray = tmpCoordinatesLineList.toArray(new String[0]);
        for (Map.Entry<Integer, LinkedList<Integer>> tmpEntry : tmpDisulfideBondMap.entrySet()) {
            LinkedList<Integer> tmpParticleIndices = tmpEntry.getValue();
            if (tmpParticleIndices.size() != 2) {
                continue;
            }
            tmpCoordinatesLineArray[tmpParticleIndices.get(0) - 1] += String.format(" %d", tmpParticleIndices.get(1) - tmpParticleIndices.get(0));
            tmpCoordinatesLineArray[tmpParticleIndices.get(1) - 1] += String.format(" %d", tmpParticleIndices.get(0) - tmpParticleIndices.get(1));
        }
        return tmpCoordinatesLineArray;
    }

    /**
     * NOTE: This method is deprecated! Use getCoordinateConnectionTableArray
     *       instead.
     * 
     * Gets the coordinate file string. The coordinates are translated to the
     * given center und scaled to fit given radius. If rotation is not null the
     * coordinates are rotated around the center of mass. Please set pH-value
     * and active chains before executing this method. Otherwise default values
     * are applied. Protein center and radius has to be set before.
     *
     * @param aStartIndex Start index.
     * @param aCAlphaStartIndex C-alpha start index.
     * @return String representing the coordinates file.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String getCoordinateConnectionTable_Deprecated(int aStartIndex, int aCAlphaStartIndex) throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        if (this.masterdata.getCenter() == null) {
            throw DpdPeptideException.MissingData("protein center");
        }
        if (this.masterdata.getRadius() == null) {
            throw DpdPeptideException.MissingData("protein radius");
        }
        StringBuilder tmpCoordinateFile = new StringBuilder();
        HashMap<String, String> tmpSpicesMap = this.protein.getChainIdsToSpicesMap(this.masterdata.getPhValue(), this.masterdata.getActiveChains(), this.isCircular());
        HashMap<String, Point3d[]> tmpCACoordiantesMap = this.protein.getCAlphaCoordinates(this.masterdata.getActiveChains());
        HashMap<String, Atom[]> tmpCAAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        // Transform ccordiantes
        tmpCACoordiantesMap = this.getTransformedCoordinates(tmpCACoordiantesMap,
                this.masterdata.getCenter(), this.masterdata.getRadius(), this.masterdata.getRotation());
        // Create coordinates file
        int tmpIndex = aStartIndex;
        int tmpCAlphaIndex = aCAlphaStartIndex;
        int tmpSkippedCAlphaIndices = 0;
        boolean[] tmpBackboneParticleStatusArray = this.getBackboneParticleStatusArray();
        HashMap<Integer, LinkedList<Integer>> tmpDisulfideBondMap = new HashMap<Integer, LinkedList<Integer>>();
        for (String tmpChainID : this.masterdata.getActiveChains()) {
            String[] tmpAminoAcidSpicesArray = tmpSpicesMap.get(tmpChainID).split("\n-");
            Point3d[] tmpCACoordinates = tmpCACoordiantesMap.get(tmpChainID);
            int tmpCurrentCAlphaAtom = 0;
            for (int i = 0; i < tmpAminoAcidSpicesArray.length; i++) {
                String[] tmpAminoAcidParticleArray;
                if (i == 0 && tmpAminoAcidSpicesArray[i].indexOf("(") < 0) {
                    // FIRST amino acid does NOT contain a side chain: Singe-particle amino acid
                    tmpAminoAcidParticleArray = tmpAminoAcidSpicesArray[i].split("-");
                    if (tmpAminoAcidParticleArray.length > 1) {
                        // Reverse tmpAminoAcidParticleArray to get correct backbone particle in first position
                        Collections.reverse(Arrays.asList(tmpAminoAcidParticleArray));
                    }
                } else {
                    tmpAminoAcidParticleArray = tmpAminoAcidSpicesArray[i].split("-|\\(");
                }
                double[] tmpLayerDepth = new double[tmpAminoAcidParticleArray.length];
                double tmpCurrentLayer = 0;
                int tmpLayerIndex = 1;
                // Create particle layer info
                for (int j = 0; j < tmpAminoAcidSpicesArray[i].length(); j++) {
                    if (tmpAminoAcidSpicesArray[i].charAt(j) == ')') {
                        if (tmpAminoAcidSpicesArray[i].length() > j + 1 && tmpAminoAcidSpicesArray[i].charAt(j + 1) == '(') {
                            tmpCurrentLayer += 0.1; // Mark particle layer index as distinct to previous unit
                            tmpLayerDepth[tmpLayerIndex] = tmpCurrentLayer;
                            j++;
                            tmpLayerIndex++;
                        } else {
                            tmpCurrentLayer = (int) tmpCurrentLayer;
                            tmpCurrentLayer--;
                        }
                    } else if (tmpAminoAcidSpicesArray[i].charAt(j) == '(') {
                        tmpCurrentLayer++;
                        tmpLayerDepth[tmpLayerIndex] = tmpCurrentLayer;
                        tmpLayerIndex++;
                    } else if (tmpAminoAcidSpicesArray[i].charAt(j) == '-') {
                        tmpLayerDepth[tmpLayerIndex] = tmpCurrentLayer;
                        tmpLayerIndex++;
                    }
                }
                // Build geometry string
                for (int j = 0; j < tmpAminoAcidParticleArray.length; j++) {
                    // Store disulfide bond data
                    if (tmpAminoAcidParticleArray[j].contains("[")) {
                        int tmpDisulfideBondIndex = Integer.parseInt(tmpAminoAcidParticleArray[j].substring(tmpAminoAcidParticleArray[j].indexOf("[") + 1, tmpAminoAcidParticleArray[j].indexOf("]")));
                        LinkedList<Integer> tmpParticleIndices;
                        if (!tmpDisulfideBondMap.containsKey(tmpDisulfideBondIndex)) {
                            tmpParticleIndices = new LinkedList<Integer>();
                            tmpDisulfideBondMap.put(tmpDisulfideBondIndex, tmpParticleIndices);
                        } else {
                            tmpParticleIndices = tmpDisulfideBondMap.get(tmpDisulfideBondIndex);
                        }
                        tmpParticleIndices.add(tmpIndex - aStartIndex + 1);
                        // Old code:
                        // tmpAminoAcidParticleArray[j] = tmpAminoAcidParticleArray[j].replaceAll("\\[.*\\]", "");
                        tmpAminoAcidParticleArray[j] = this.bracketedExpressionPattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll("");
                    }
                    // Add particle index, name ...
                    // First check whether current particle is a C-alpha atom                  
                    if (j != 0) {
                        // Not a C-alpha
                        tmpCoordinateFile.append(String.format(Locale.US, "%d %s %d %.3f %.3f %.3f",
                                tmpIndex, this.bracketCloseNewlinePattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll(""), 0,
                                tmpCACoordinates[i].x, tmpCACoordinates[i].y, tmpCACoordinates[i].z));
                                // Old code:
                                // tmpIndex, tmpAminoAcidParticleArray[j].replaceAll("\\)|\\n", ""), 0,
                    } else if (!tmpBackboneParticleStatusArray[tmpCAlphaIndex - aCAlphaStartIndex]) {
                        // C-alpha but excluded
                        tmpCoordinateFile.append(String.format(Locale.US, "%d %s %d %.3f %.3f %.3f",
                                tmpIndex, this.bracketCloseNewlinePattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll(""), 0,
                                tmpCACoordinates[i].x, tmpCACoordinates[i].y, tmpCACoordinates[i].z));
                                // Old code:
                                // tmpIndex, tmpAminoAcidParticleArray[j].replaceAll("\\)|\\n", ""), 0,
                        // IMPORTANT: Increment tmpCurrentCAlphaAtom
                        tmpCurrentCAlphaAtom++;
                    } else {
                        // Non-excluded C-alpha
                        // Old code:
                        // String tmpCAlphaParticle = tmpAminoAcidParticleArray[j].replaceAll("\\)|\\n", "");
                        String tmpCAlphaParticle = this.bracketCloseNewlinePattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll("");
                        // Exchange particle by probe?
                        String tmpCAlphaKey = this.createCAlphaKey(tmpCAAtomsMap.get(tmpChainID)[tmpCurrentCAlphaAtom]);
                        if (this.masterdata.getCAlphaAtomIndexProbeMap().containsKey(tmpCAlphaKey)) {
                            tmpCAlphaParticle = this.masterdata.getCAlphaAtomIndexProbeMap().get(tmpCAlphaKey);
                        }
                        tmpCoordinateFile.append(String.format(Locale.US, "%d %s %d %.3f %.3f %.3f",
                                tmpIndex, tmpCAlphaParticle, tmpCAlphaIndex - tmpSkippedCAlphaIndices,
                                tmpCACoordinates[i].x, tmpCACoordinates[i].y, tmpCACoordinates[i].z));
                        // IMPORTANT: Increment tmpCurrentCAlphaAtom
                        tmpCurrentCAlphaAtom++;
                    }
                    if (i != 0 && j == 0) {
                        String[] tmpPreviousSpicesTokens = tmpAminoAcidSpicesArray[i - 1].split("-|\\(");
                        tmpCoordinateFile.append(String.format(" -%d", tmpPreviousSpicesTokens.length));
                    }
                    // Find connections down the stream
                    if (j > 0) {
                        tmpCurrentLayer = tmpLayerDepth[j];
                        for (int k = j - 1; k >= 0; k--) {
                            if (tmpLayerDepth[k] == tmpCurrentLayer || tmpLayerDepth[k] == ((int) tmpCurrentLayer) - 1) {
                                tmpCoordinateFile.append(String.format(Locale.US, " %d", k - j));
                                break;
                            }
                        }
                    }
                    // Find connections upstream in same layer and layer above
                    if (j < tmpAminoAcidParticleArray.length - 1) {
                        tmpCurrentLayer = tmpLayerDepth[j];
                        HashSet<Double> tmpConnectedHigherLayer = new HashSet<Double>();
                        for (int k = j + 1; k < tmpAminoAcidParticleArray.length; k++) {
                            if (tmpLayerDepth[k] == tmpCurrentLayer) {
                                tmpCoordinateFile.append(String.format(Locale.US, " %d", k - j));
                                break;
                            }
                            if (((int) tmpLayerDepth[k]) == tmpCurrentLayer + 1) {
                                if (!tmpConnectedHigherLayer.contains(tmpLayerDepth[k])) {
                                    tmpCoordinateFile.append(String.format(Locale.US, " %d", k - j));
                                    tmpConnectedHigherLayer.add(tmpLayerDepth[k]);
                                }
                            }
                        }
                    }
                    if (i != tmpAminoAcidSpicesArray.length - 1 && j == 0) {
                        tmpCoordinateFile.append(String.format(" %d", tmpAminoAcidParticleArray.length));
                    }
                    tmpCoordinateFile.append("\n");
                    tmpIndex++;
                }
                if (!tmpBackboneParticleStatusArray[tmpCAlphaIndex - aCAlphaStartIndex]) {
                    tmpSkippedCAlphaIndices++;
                }
                tmpCAlphaIndex++;
            }
        }

        this.masterdata.setLastIndex(tmpIndex - 1);
        this.masterdata.setLastCAlphaIndex(tmpCAlphaIndex - tmpSkippedCAlphaIndices - 1);
        // Add disulfide bond connections
        String[] tmpCoordinateFileLines = tmpCoordinateFile.toString().split("\\n");
        for (Map.Entry<Integer, LinkedList<Integer>> tmpEntry : tmpDisulfideBondMap.entrySet()) {
            LinkedList<Integer> tmpParticleIndices = tmpEntry.getValue();
            if (tmpParticleIndices.size() != 2) {
                continue;
            }
            tmpCoordinateFileLines[tmpParticleIndices.get(0) - 1] += String.format(" %d", tmpParticleIndices.get(1) - tmpParticleIndices.get(0));
            tmpCoordinateFileLines[tmpParticleIndices.get(1) - 1] += String.format(" %d", tmpParticleIndices.get(0) - tmpParticleIndices.get(1));
        }
        tmpCoordinateFile = new StringBuilder(tmpCoordinateFile.length());
        for (String tmpLine : tmpCoordinateFileLines) {
            tmpCoordinateFile.append(String.format("%s\n", tmpLine));
        }
        return tmpCoordinateFile.toString();
    }

    /**
     * Updates the graphical particle position data. The coordinates are
     * translated to the given center und scaled to fit given radius. Please set
     * pH-value and active chains before executing this method. Otherwise
     * default values are applied. Protein center and radius has to be set
     * before.
     *
     * @param aGraphicalParticleMap Graphical particles map.
     * @param aGraphicalParticlePositionInterfaceBuffer Graphical particle
     * position buffer.
     * @return Updated graphical particle positions.
     * @throws DpdPeptideException DpdPeptideException
     */
    public IGraphicalParticlePosition[] getGraphicalParticlePositions(HashMap<String, IGraphicalParticle> aGraphicalParticleMap,
            IGraphicalParticlePosition[] aGraphicalParticlePositionInterfaceBuffer) throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        if (this.masterdata.getCenter() == null) {
            throw DpdPeptideException.MissingData("protein center");
        }
        if (this.masterdata.getRadius() == null) {
            throw DpdPeptideException.MissingData("protein radius");
        }
        HashMap<String, String> tmpSpicesMap = this.protein.getChainIdsToSpicesMap(this.masterdata.getPhValue(), this.masterdata.getActiveChains(), this.isCircular());
        HashMap<String, Point3d[]> tmpCAlphaCoordinatesMap = this.protein.getCAlphaCoordinates(this.masterdata.getActiveChains());
        HashMap<String, Atom[]> tmpCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        int tmpIndex = 0;
        // Transform coordinates
        tmpCAlphaCoordinatesMap = this.getTransformedCoordinates(tmpCAlphaCoordinatesMap, this.masterdata.getCenter(), this.masterdata.getRadius(), this.masterdata.getRotation());
        // Create graphical particle positions
        for (String tmpChainID : this.masterdata.getActiveChains()) {
            String[] tmpAminoAcidSpicesArray = tmpSpicesMap.get(tmpChainID).split("\n-");
            Point3d[] tmpCACoordinates = tmpCAlphaCoordinatesMap.get(tmpChainID);
            int tmpCurrentCAlphaAtom = 0;
            for (int i = 0; i < tmpAminoAcidSpicesArray.length; i++) {
                String[] tmpAminoAcidParticleArray;
                if (i == 0 && tmpAminoAcidSpicesArray[i].indexOf("(") < 0) {
                    // FIRST amino acid does NOT contain a side chain: Singe-particle amino acid
                    tmpAminoAcidParticleArray = tmpAminoAcidSpicesArray[i].split("-");
                    if (tmpAminoAcidParticleArray.length > 1) {
                        // Reverse tmpAminoAcidParticleArray to get correct backbone particle in first position
                        Collections.reverse(Arrays.asList(tmpAminoAcidParticleArray));
                    }
                } else {
                    tmpAminoAcidParticleArray = tmpAminoAcidSpicesArray[i].split("-|\\(");
                }
                for (int j = 0; j < tmpAminoAcidParticleArray.length; j++) {
                    // Remove disulfide bond data
                    if (tmpAminoAcidParticleArray[j].contains("[")) {
                        // Old code:
                        // tmpAminoAcidParticleArray[j] = tmpAminoAcidParticleArray[j].replaceAll("\\[.*\\]", "");
                        tmpAminoAcidParticleArray[j] = this.bracketedExpressionPattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll("");
                    }
                    // Old code:
                    // tmpAminoAcidParticleArray[j] = tmpAminoAcidParticleArray[j].replaceAll("\\)|\\n", "");
                    tmpAminoAcidParticleArray[j] = this.bracketCloseNewlinePattern.matcher(tmpAminoAcidParticleArray[j]).replaceAll("");
                    IGraphicalParticle tmpGraphicalParticle;
                    tmpGraphicalParticle = aGraphicalParticleMap.get(tmpAminoAcidParticleArray[j]);
                    // Is C-alpha particle?
                    if (j == 0) {
                        String tmpCAlphaKey = this.createCAlphaKey(tmpCAlphaAtomsMap.get(tmpChainID)[tmpCurrentCAlphaAtom]);
                        if (this.masterdata.getCAlphaAtomIndexProbeMap().containsKey(tmpCAlphaKey)) {
                            String tmpCAlphaParticle = this.masterdata.getCAlphaAtomIndexProbeMap().get(tmpCAlphaKey);
                            tmpGraphicalParticle = aGraphicalParticleMap.get(tmpCAlphaParticle);
                        }
                        tmpCurrentCAlphaAtom++;
                    }
                    // ((GraphicalParticle) tmpGraphicalParticle).setMoleculeParticleString(String.format("%s-%s",
                    // this.protein.getPdbCode(), tmpGraphicalParticle.getParticle()));
                    aGraphicalParticlePositionInterfaceBuffer[tmpIndex].setGraphicalParticle(tmpGraphicalParticle);
                    aGraphicalParticlePositionInterfaceBuffer[tmpIndex].setX(tmpCACoordinates[i].x);
                    aGraphicalParticlePositionInterfaceBuffer[tmpIndex].setY(tmpCACoordinates[i].y);
                    aGraphicalParticlePositionInterfaceBuffer[tmpIndex].setZ(tmpCACoordinates[i].z);
                    tmpIndex++;
                }
            }
        }
        return aGraphicalParticlePositionInterfaceBuffer;
    }

    /**
     * Gets the maximum number of particles needed.
     *
     * @return Maximum number of Particles needed.
     * @throws DpdPeptideException DpdPeptideException
     */
    public int getMaxNumberOfParticles() throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        int tmpCount = 0;
        HashMap<String, String> tmpSpicesMap = this.protein.getChainIdsToSpicesMap(this.masterdata.getPhValue(), this.masterdata.getActiveChains(), this.isCircular());
        // Create graphical particle positions
        for (String tmpChainID : this.masterdata.getActiveChains()) {
            String[] tmpSpices = tmpSpicesMap.get(tmpChainID).split("\n-");
            for (int i = 0; i < tmpSpices.length; i++) {
                String[] tmpSpicesTokens = tmpSpices[i].split("-|\\(");
                for (int j = 0; j < tmpSpicesTokens.length; j++) {
                    tmpCount++;
                }
            }
        }
        return tmpCount;
    }

    /**
     * Gets the max distance type of protein distance forces.
     *
     * @return The max distance type of protein distance forces.
     * @throws DpdPeptideException DpdPeptideException
     */
    public int getMaxDistanceTypeOfProteinDistanceForces() throws DpdPeptideException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        // </editor-fold>
        HashMap<String, Atom[]> tmpChainToCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        boolean[] tmpBackboneParticleStatusArray = this.getBackboneParticleStatusArray();
        int[] tmpBackboneParticleSegmentArray = this.getBackboneParticleSegmentArray();
        // Determine active backbone particles with status "true"
        int tmpCounter = 0;
        for (int i = 0; i < tmpBackboneParticleStatusArray.length; i++) {
            if (tmpBackboneParticleStatusArray[i]) {
                tmpCounter++;
            }
        }
        if (tmpCounter == 0) {
            return 0;
        }
        // Initialize arrays for active backbone particles
        int[] tmpActiveBackboneParticleIndexArray = new int[tmpCounter];
        int[] tmpActiveBackboneParticleSegmentArray = new int[tmpCounter];
        // Loop over all backbone particles and fill active backbone particle related arrays
        int tmpBackboneParticleIndex = 0;
        int tmpActiveBackboneParticleIndex = 0;
        for (int i = 0; i < this.masterdata.getActiveChains().size(); i++) {
            Atom[] tmpCAlphaAtomArray = tmpChainToCAlphaAtomsMap.get(this.masterdata.getActiveChains().get(i));
            for (int j = 0; j < tmpCAlphaAtomArray.length; j++) {
                if (tmpBackboneParticleStatusArray[tmpBackboneParticleIndex]) {
                    tmpActiveBackboneParticleIndexArray[tmpActiveBackboneParticleIndex] = tmpActiveBackboneParticleIndex + 1;
                    tmpActiveBackboneParticleSegmentArray[tmpActiveBackboneParticleIndex] = tmpBackboneParticleSegmentArray[tmpBackboneParticleIndex];
                    tmpActiveBackboneParticleIndex++;
                }
                tmpBackboneParticleIndex++;
            }
        }
        // Set possible maximum distance
        int tmpPossibleMaximumDistance = tmpActiveBackboneParticleIndexArray.length - 1;
        // Evaluate real maximum distance
        int tmpRealMaximumDistance = 0;
        for (int i = tmpPossibleMaximumDistance; i > 0; i--) {
            for (int k = 0; k < tmpActiveBackboneParticleIndexArray.length - i; k++) {
                if (tmpActiveBackboneParticleSegmentArray[k] == tmpActiveBackboneParticleSegmentArray[k + i]) {
                    tmpRealMaximumDistance = i;
                    break;
                }
            }
            if (tmpRealMaximumDistance > 0) {
                break;
            }
        }
        return tmpRealMaximumDistance;
    }

    /**
     * NOTE: This old methods does NOT return the correct answer!
     * Gets the max distance of backbone forces.
     *
     * @return The max backbone force distance.
     * @throws DpdPeptideException DpdPeptideException
     */
    public int getMaxDistanceTypeOfProteinDistanceForces_Deprecated() throws DpdPeptideException {
        int tmpMaxDistance = Integer.MAX_VALUE;
        HashMap<String, Atom[]> tmpChainToCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        for (String tmpChain : this.masterdata.getActiveChains()) {
            Atom[] tmpCAlphaAtoms = tmpChainToCAlphaAtomsMap.get(tmpChain);
            if (tmpCAlphaAtoms.length - 1 < tmpMaxDistance) {
                tmpMaxDistance = tmpCAlphaAtoms.length - 1;
            }
        }
        return tmpMaxDistance;
    }

    /**
     * Gets the backbone 1 to (aDistanceType + 1) distance forces.
     *
     * @param aDistanceType Distance of the protein distance force.
     * @param anAngstromToDpdConversionFactor  Angstrom to DPD conversion factor
     * @param aForceConstantForDistance Force constant for distance
     * @return 1 to (aDistanceType + 1) distance forces.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String[] getProteinDistanceForcesLineArray(int aDistanceType, double anAngstromToDpdConversionFactor, double aForceConstantForDistance) throws DpdPeptideException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        // </editor-fold>
        HashMap<String, Atom[]> tmpChainToCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        boolean[] tmpBackboneParticleStatusArray = this.getBackboneParticleStatusArray();
        int[] tmpBackboneParticleSegmentArray = this.getBackboneParticleSegmentArray();
        // Determine active backbone particles with status "true"
        int tmpCounter = 0;
        for (int i = 0; i < tmpBackboneParticleStatusArray.length; i++) {
            if (tmpBackboneParticleStatusArray[i]) {
                tmpCounter++;
            }
        }
        if (tmpCounter == 0) {
            return null;
        }
        // Initialize arrays for active backbone particles
        int[] tmpActiveBackboneParticleIndexArray = new int[tmpCounter];
        int[] tmpActiveBackboneParticleSegmentArray = new int[tmpCounter];
        Point3d[] tmpActiveBackboneParticleCoordinateArray = new Point3d[tmpCounter];
        // Loop over all backbone particles and fill active backbone particle related arrays
        int tmpBackboneParticleIndex = 0;
        int tmpActiveBackboneParticleIndex = 0;
        for (int i = 0; i < this.masterdata.getActiveChains().size(); i++) {
            Atom[] tmpCAlphaAtomArray = tmpChainToCAlphaAtomsMap.get(this.masterdata.getActiveChains().get(i));
            for (int j = 0; j < tmpCAlphaAtomArray.length; j++) {
                if (tmpBackboneParticleStatusArray[tmpBackboneParticleIndex]) {
                    tmpActiveBackboneParticleIndexArray[tmpActiveBackboneParticleIndex] = tmpActiveBackboneParticleIndex + 1;
                    tmpActiveBackboneParticleSegmentArray[tmpActiveBackboneParticleIndex] = tmpBackboneParticleSegmentArray[tmpBackboneParticleIndex];
                    tmpActiveBackboneParticleCoordinateArray[tmpActiveBackboneParticleIndex] = new Point3d(tmpCAlphaAtomArray[j].getCoords());
                    tmpActiveBackboneParticleIndex++;
                }
                tmpBackboneParticleIndex++;
            }
        }
        // Check
        if (tmpActiveBackboneParticleIndexArray.length < aDistanceType + 1) {
            return null;
        }
        // Set backbone potential lines
        LinkedList<String> tmpBackboneForceDistanceLineList = new LinkedList<>();
        StringBuilder tmpBackboneForceDistanceLine = new StringBuilder();
        for (int i = 0; i < tmpActiveBackboneParticleIndexArray.length - aDistanceType; i++) {
            if (tmpActiveBackboneParticleSegmentArray[i] == tmpActiveBackboneParticleSegmentArray[i + aDistanceType]) {
                Point3d tmpCoordinateA = tmpActiveBackboneParticleCoordinateArray[i];
                Point3d tmpCoordinateB = tmpActiveBackboneParticleCoordinateArray[i + aDistanceType];
                double tmpSpatialDistance = tmpCoordinateA.distance(tmpCoordinateB) * anAngstromToDpdConversionFactor;
                // IMPORTANT: Set protein distance force start/end index characters: <...>
                tmpBackboneForceDistanceLine.append(String.format(Locale.US, 
                    ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_START + "%d" + ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_END + 
                        " " + 
                        ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_START + "%d" + ModelDefinitions.PROTEIN_BACKBONE_FORCE_INDEX_END + 
                        " ", 
                    tmpActiveBackboneParticleIndexArray[i], 
                    tmpActiveBackboneParticleIndexArray[i + aDistanceType]
                ));
                tmpBackboneForceDistanceLine.append(String.format(Locale.US, 
                    "%." + 
                        this.getNumberOfDecimalsForParameters() + "f %." + 
                        this.getNumberOfDecimalsForParameters() + "f",
                    tmpSpatialDistance, 
                    aForceConstantForDistance
                ));
                // Add line to list
                tmpBackboneForceDistanceLineList.add(tmpBackboneForceDistanceLine.toString());
                // Clear line
                tmpBackboneForceDistanceLine.setLength(0);
            }
        }
        if (tmpBackboneForceDistanceLineList.isEmpty()) {
            return null;
        } else {
            return tmpBackboneForceDistanceLineList.toArray(new String[0]);
        }
    }

    /**
     * NOTE: This old method leads to WRONG answers if status is not consecutive.
     *       It seems to work for other cases.
     * Gets the backbone force 1 - (aDistanceType + 1) distances.
     *
     * @param aDistanceType Distance of the protein distance force.
     * @param anAngstromToDpdConversionFactor  Angstrom to DPD conversion factor
     * @param aForceConstantForDistance Force constant for distance
     * @return 1 to (aDistanceType + 1) distance forces.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String[] getProteinDistanceForcesLineArray_Old(int aDistanceType, double anAngstromToDpdConversionFactor, double aForceConstantForDistance) throws DpdPeptideException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        // </editor-fold>
        HashMap<String, Atom[]> tmpChainToCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        LinkedList<String> tmpBackboneForceDistanceLineList = new LinkedList<>();
        StringBuilder tmpBackboneForceDistanceLine = new StringBuilder();
        boolean[] tmpBackboneParticleStatusArray = this.getBackboneParticleStatusArray();
        int[] tmpBackboneParticleSegmentArray = this.getBackboneParticleSegmentArray();
        int tmpIndex = 1;
        int tmpSkippedParticles = 0;
        for (int i = 0; i < this.masterdata.getActiveChains().size(); i++) {
            Atom[] tmpCAlphaAtoms = tmpChainToCAlphaAtomsMap.get(this.masterdata.getActiveChains().get(i));
            for (int j = 0; j < tmpCAlphaAtoms.length - aDistanceType; j++) {
                if (!tmpBackboneParticleStatusArray[tmpIndex - 1] || !tmpBackboneParticleStatusArray[tmpIndex + aDistanceType - 1]) {
                    tmpSkippedParticles++;
                }
                if ((!tmpBackboneParticleStatusArray[tmpIndex - 1] || !tmpBackboneParticleStatusArray[tmpIndex + aDistanceType - 1])
                        || tmpBackboneParticleSegmentArray[tmpIndex - 1] != tmpBackboneParticleSegmentArray[tmpIndex + aDistanceType - 1]) {
                    tmpIndex++;
                    continue;
                }
                Point3d tmpCoordA = new Point3d(tmpCAlphaAtoms[j].getCoords());
                Point3d tmpCoordB = new Point3d(tmpCAlphaAtoms[j + aDistanceType].getCoords());
                double tmpSpatialDistance = tmpCoordA.distance(tmpCoordB) * anAngstromToDpdConversionFactor;
                // IMPORTANT: Set backbone index in <...>
                tmpBackboneForceDistanceLine.append(String.format(Locale.US, "<%d> <%d> ", 
                    tmpIndex - tmpSkippedParticles, 
                    tmpIndex - tmpSkippedParticles + aDistanceType
                ));
                tmpBackboneForceDistanceLine.append(String.format(Locale.US, "%." + this.getNumberOfDecimalsForParameters() + "f %." + this.getNumberOfDecimalsForParameters() + "f",
                    tmpSpatialDistance, 
                    aForceConstantForDistance
                ));
                // Add line to list
                tmpBackboneForceDistanceLineList.add(tmpBackboneForceDistanceLine.toString());
                // Clear line
                tmpBackboneForceDistanceLine.setLength(0);
                tmpIndex++;
            }
            if (i < this.masterdata.getActiveChains().size() - 1) {
                if (!tmpBackboneParticleStatusArray[tmpIndex - 1] || !tmpBackboneParticleStatusArray[tmpIndex + aDistanceType - 1]) {
                    tmpSkippedParticles += aDistanceType;
                }
                if ((!tmpBackboneParticleStatusArray[tmpIndex - 1] || !tmpBackboneParticleStatusArray[tmpIndex + aDistanceType - 1])
                        || tmpBackboneParticleSegmentArray[tmpIndex - 1] != tmpBackboneParticleSegmentArray[tmpIndex + aDistanceType - 1]) {
                    tmpIndex += aDistanceType;
                    continue;
                }
                Atom[] tmpCAlphaAtomsOfNextChain = tmpChainToCAlphaAtomsMap.get(this.masterdata.getActiveChains().get(i + 1));
                for (int j = 1; j <= aDistanceType; j++) {
                    int tmpFirstIndex = tmpCAlphaAtoms.length - aDistanceType + j - 1;
                    Point3d tmpCoordA = new Point3d(tmpCAlphaAtoms[tmpFirstIndex].getCoords());
                    Point3d tmpCoordB = new Point3d(tmpCAlphaAtomsOfNextChain[j - 1].getCoords());
                    double tmpDistance = tmpCoordA.distance(tmpCoordB) * anAngstromToDpdConversionFactor;
                    // IMPORTANT: Set backbone index in <...>
                    tmpBackboneForceDistanceLine.append(String.format(Locale.US, "<%d> <%d> ", 
                        tmpIndex - tmpSkippedParticles, 
                        tmpIndex - tmpSkippedParticles + aDistanceType
                    ));
                    tmpBackboneForceDistanceLine.append(String.format(Locale.US, "%." + this.getNumberOfDecimalsForParameters() + "f %." + this.getNumberOfDecimalsForParameters() + "f",
                        tmpDistance, 
                        aForceConstantForDistance
                    ));
                    // Add line to list
                    tmpBackboneForceDistanceLineList.add(tmpBackboneForceDistanceLine.toString());
                    // Clear line
                    tmpBackboneForceDistanceLine.setLength(0);
                    tmpIndex++;
                }
            }
        }
        if (tmpBackboneForceDistanceLineList.isEmpty()) {
            return null;
        } else {
            return tmpBackboneForceDistanceLineList.toArray(new String[0]);
        }
    }

    /**
     * Gets the number of backbone force 1 to (aDistanceType + 1) distances. Backbone
     * particle status and backbone particle segments will be considered.
     *
     * @param aDistanceType Distance of the protein distance force.
     * @return Number of 1 - (aDistanceType + 1) distance forces.
     * @throws DpdPeptideException DpdPeptideException
     */
    public int getNumberOfProteinDistanceForces(int aDistanceType) throws DpdPeptideException {
        // NOTE: anAngstromToDpdConversionFactor and aForceConstantForDistance are arbitrarily 
        //       set to -1.0 for visibility since only the number of lines is important
        String[] tmpBackboneForceDistanceLineArray = this.getProteinDistanceForcesLineArray(aDistanceType, -1.0, -1.0);
        if (tmpBackboneForceDistanceLineArray == null) {
            return 0;
        } else {
            return tmpBackboneForceDistanceLineArray.length;
        }
    }

    /**
     * Gets the mean distance of the backbone particles in angstroms.
     *
     * @return The mean distance of the backbone particles in angstroms.
     * @throws DpdPeptideException DpdPeptideException
     */
    public double getMeanBackboneParticleDistanceInAngstrom() throws DpdPeptideException {
        double tmpMeanDistance = 0;
        int tmpNumberOfDistances = 0;
        HashMap<String, Atom[]> tmpCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        for (Map.Entry<String, Atom[]> tmpEntry : tmpCAlphaAtomsMap.entrySet()) {
            Atom[] tmpCAlphaAtoms = tmpEntry.getValue();
            for (int i = 1; i < tmpCAlphaAtoms.length; i++) {
                Point3d tmpPositionAtomA = new Point3d(tmpCAlphaAtoms[i - 1].getCoords());
                Point3d tmpPositionAtomB = new Point3d(tmpCAlphaAtoms[i].getCoords());
                tmpMeanDistance += tmpPositionAtomA.distance(tmpPositionAtomB);
            }
            tmpNumberOfDistances += tmpCAlphaAtoms.length - 1;
        }
        tmpMeanDistance /= tmpNumberOfDistances;

        return tmpMeanDistance;
    }

    /**
     * Gets ZMatrices for each chain organized in a HashMap.
     *
     * @return ChainID/Zmatrix map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, ZMatrix> getZMatrices() throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        return this.protein.getZMatrices(this.masterdata.getActiveChains());
    }

    /**
     * Single ZMAtrix for all CA-atoms of all active chains.
     *
     * @return ZMatrix
     * @throws DpdPeptideException DpdPeptideException
     */
    public ZMatrix getZMatrix() throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        return this.protein.getZMatrix(this.masterdata.getActiveChains());
    }

    /**
     * Gets a hashmap mapping the chain name to its corresponding ID.
     *
     * @return Name/ChainID map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, String> getNameChainIDMap() throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        return this.protein.getChainNames();
    }

    /**
     * Returns the array of C-alpha keys depending on active chains.
     *
     * @return Array of C-alpha keys.
     */
    public String[] getCAlphaKeys() {
        HashMap<String, Atom[]> tmpCAlphaAtomsMap = this.protein.getChainToCAlphaAtomsMap(this.getActiveChains());
        ArrayList<String> tmpCAlphaKeyList = new ArrayList<String>();
        for (String tmpChainID : this.getActiveChains()) {
            Atom[] tmpCAlphaAtoms = tmpCAlphaAtomsMap.get(tmpChainID);
            for (Atom tmpCAlphaAtom : tmpCAlphaAtoms) {
                tmpCAlphaKeyList.add(this.createCAlphaKey(tmpCAlphaAtom));
            }
        }
        return tmpCAlphaKeyList.toArray(new String[tmpCAlphaKeyList.size()]);
    }

    /**
     * Returns array of indexed C-alpha keys.
     * 
     * @return Array of indexed C-alpha keys.
     */
    public String[] getIndexedCAlphaKeys() {
        String[] tmpCAlphaKeyArray = this.getCAlphaKeys();
        String[] tmpIndexedCAlphaKeyArray = new String[tmpCAlphaKeyArray.length];
        for (int i = 0; i < tmpCAlphaKeyArray.length; i++) {
            // NOTE: First token MUST be index (IMPORTANT for later operations elsewhere)!
            tmpIndexedCAlphaKeyArray[i] = String.format("%s - %s", String.valueOf(i + 1), tmpCAlphaKeyArray[i]);
        }
        return tmpIndexedCAlphaKeyArray;
    }
    
    /**
     * Gets an array containing all C-alpha particles depending on active
     * chains.
     *
     * @return Array ccontaining all C-alpha fragmens.
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException DpdPeptideException
     */
    public String[] getCAlphaParticles() throws DpdPeptideException {
        return this.getCAlphaParticles(this.protein.getChainIDs());
    }

    /**
     * Gets an array containing all C-alpha particles depending on given chains.
     *
     * @param aChainIDs Chains to evaluate.
     * @return Array ccontaining all C-alpha fragmens.
     * @throws de.gnwi.mfsim.model.peptide.utils.DpdPeptideException DpdPeptideException
     */
    public String[] getCAlphaParticles(ArrayList<String> aChainIDs) throws DpdPeptideException {
        ArrayList<String> tmpCAlphaParticles = new ArrayList<String>();
        HashMap<String, String> tmpSpicesMap = this.protein.getChainIdsToSpicesMap(this.masterdata.getPhValue(), aChainIDs, this.isCircular());
        for (String tmpChainID : aChainIDs) {
            String[] tmpAminoAcidSpicesStringArray = tmpSpicesMap.get(tmpChainID).split("\n-");
            boolean tmpIsFirst = true;
            for (String tmpAminoAcidSpicesString : tmpAminoAcidSpicesStringArray) {
                String tmpParticle;
                // Special treatment for FIRST tmpAminoAcidSpices
                if (tmpIsFirst && tmpAminoAcidSpicesString.indexOf("(") < 0) {
                    // tmpAminoAcidSpices does NOT contain a sidechain: Single-particle amino acid
                    String[] tmpAminoAcidParticleArray = tmpAminoAcidSpicesString.split("-");
                    // Take last particle (if possible) for FIRST tmpAminoAcidSpices that is a single-particle amino acid
                    if (tmpAminoAcidParticleArray.length > 1) {
                        // Old code:
                        // tmpParticle = tmpAminoAcidParticleArray[tmpAminoAcidParticleArray.length - 1].replaceAll("\\[\\d\\]", "");
                        tmpParticle = this.bracketedDigitsPattern.matcher(tmpAminoAcidParticleArray[tmpAminoAcidParticleArray.length - 1]).replaceAll("");
                    } else {
                        // Old code:
                        // tmpParticle = tmpAminoAcidParticleArray[0].replaceAll("\\[\\d\\]", "");
                        tmpParticle = this.bracketedDigitsPattern.matcher(tmpAminoAcidParticleArray[0]).replaceAll("");
                    }
                } else {
                    // Take first particle for ALL other tmpAminoAcidSpices
                    String[] tmpAminoAcidParticleArray = tmpAminoAcidSpicesString.split("-|\\(");
                    // Old code:
                    // tmpParticle = tmpAminoAcidParticleArray[0].replaceAll("\\[\\d\\]", "");
                    tmpParticle = this.bracketedDigitsPattern.matcher(tmpAminoAcidParticleArray[0]).replaceAll("");
                }
                tmpCAlphaParticles.add(tmpParticle);
                tmpIsFirst = false;
            }
        }
        return tmpCAlphaParticles.toArray(new String[tmpCAlphaParticles.size()]);
    }

    /**
     * Gets the compound names of the protein.
     *
     * @return The compound names.
     * @throws DpdPeptideException DpdPeptideException
     */
    public String[] getCompoundNames() throws DpdPeptideException {
        if (this.protein == null) {
            throw DpdPeptideException.NoProteinDataException();
        }
        return this.protein.getCompoundNames();
    }

    /**
     * Gets the active chains. Only active chains will be processed.
     *
     * @return List containing chain IDs.
     */
    public ArrayList<String> getActiveChains() {
        return this.masterdata.getActiveChains();
    }

    /**
     * Sets the active chains. Only active chains will be processed. Clears all
     * backbone segment and status data.
     *
     * @param anActiveChains Array containing chain IDs.
     */
    public void setActiveChains(String[] anActiveChains) {
        ArrayList<String> tmpActiveChains = new ArrayList<String>();
        tmpActiveChains.addAll(Arrays.asList(anActiveChains));
        this.setActiveChains(tmpActiveChains);
    }

    /**
     * Sets the active chains. Only active chains will be processed. Clears all
     * backbone segment and status data.
     *
     * @param anActiveChains List containing chain IDs.
     */
    public void setActiveChains(ArrayList<String> anActiveChains) {
        Collections.sort(anActiveChains);
        this.masterdata.setActiveChains(anActiveChains);
        this.clearSegments();
        this.clearStatus();
    }

    /**
     * Gets the backbone particle status.
     *
     * @return The backbone particle status.
     * @throws DpdPeptideException DpdPeptideException
     */
    public boolean[] getBackboneParticleStatusArray() throws DpdPeptideException {
        if (this.masterdata.getBackboneParticleStatusArray() == null) {
            boolean[] tmpBackboneParticleStatusArray = new boolean[this.getNumberOfBackboneParticles()];
            Arrays.fill(tmpBackboneParticleStatusArray, true);
            return tmpBackboneParticleStatusArray;
        } else {
            return this.masterdata.getBackboneParticleStatusArray();
        }
    }

    /**
     * Sets the backbone particle status.
     *
     * @param aStatusArray The backbone particle status array.
     */
    public void setBackboneParticleStatusArray(boolean[] aStatusArray) {
        this.masterdata.setBackboneParticleStatusArray(aStatusArray);
    }

    /**
     * Sets the backbone particle segment array.
     *
     * @param aSegmentArray The backbone particle segment array.
     */
    public void setBackboneParticleSegmentArray(int[] aSegmentArray) {
        this.masterdata.setBackboneParticleSegmentArray(aSegmentArray);
    }

    /**
     * Gets the backbone particle segment array.
     *
     * @return The backbone particle segment array.
     * @throws DpdPeptideException DpdPeptideException
     */
    public int[] getBackboneParticleSegmentArray() throws DpdPeptideException {
        if (this.masterdata.getBackboneParticleSegmentArray() == null) {
            return new int[this.getNumberOfBackboneParticles()];
        }
        return this.masterdata.getBackboneParticleSegmentArray();
    }

    /**
     * Gets a backbone segment array. The sequence is depending on the proteins
     * chains (1. Chain = 1, 2. chain = 2, etc). The array has not been applied
     * to the masterdata.
     *
     * @return The backbone particle segment array.
     * @throws DpdPeptideException DpdPeptideException
     */
    public int[] getBackboneParticleSegmentsChainsApplied() throws DpdPeptideException {
        int[] tmpBackboneSegemnts = new int[this.getNumberOfBackboneParticles()];
        HashMap<String, Atom[]> tmpCalphaAtoms = this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains());
        int tmpChainIndex = 1;
        int tmpIndex = 0;
        for (String tmpChain : this.masterdata.getActiveChains()) {
            for (Atom tmpAtom : tmpCalphaAtoms.get(tmpChain)) {
                tmpBackboneSegemnts[tmpIndex] = tmpChainIndex;
                tmpIndex++;
            }
            tmpChainIndex++;
        }
        return tmpBackboneSegemnts;
    }

    /**
     * Gets the amino acid at given sequence index. Only active chains are
     * considered. The first amino acid has the index 1.
     *
     * @param anIndex Index of target amino acid.
     * @return Amino acid object.
     * @throws DpdPeptideException When index is out of range.
     */
    public AminoAcid getAminoAcid(int anIndex) throws DpdPeptideException {
        HashMap<String, String> tmpSequences = this.protein.getSequences(null, this.protein.getChainIDs(), this.isCircular());
        ArrayList<String> tmpActiveChains = this.masterdata.getActiveChains();
        int tmpIndex = 1;
        for (String tmpChain : tmpActiveChains) {
            String tmpSequence = tmpSequences.get(tmpChain);
            for (Character tmpCode : tmpSequence.toCharArray()) {
                if (tmpCode.toString().matches("\\*|\\[|\\]|\\d")) {
                    continue;
                }
                if (tmpIndex == anIndex) {
                    return this.aminoAcidsUtility.getAminoAcidFromOneLetterCode(tmpCode);
                }
                tmpIndex++;
            }
        }
        throw new DpdPeptideException("Peptide.IndexOutOfRange");
    }

    /**
     * Calculates the radius of gyration from given crystal structure. Only
     * active chains are considered.
     *
     * @return Radius of gyration.
     */
    public double calculateRadiusOfGyration() {
        double tmpRadiusOfGyration = 0;
        Point3d tmpCenter = this.protein.calculateCenterOfMass(this.getActiveChains());
        int tmpNumberOfAtoms = 0;
        for (String tmpChainID : this.getActiveChains()) {
            Atom[] tmpAtoms = this.protein.getAtomArray(tmpChainID);
            for (Atom tmpAtom : tmpAtoms) {
                if (tmpAtom.getGroup() instanceof AminoAcidImpl) {
                    double tmpDistance = tmpCenter.distance(new Point3d(tmpAtom.getCoords()));
                    tmpRadiusOfGyration += Math.pow(tmpDistance, 2.0);
                    tmpNumberOfAtoms++;
                }
            }
        }
        return Math.sqrt(tmpRadiusOfGyration / tmpNumberOfAtoms);
    }

    /**
     * Calculates the longest distance between two atoms for active chains only.
     *
     * @return Longest distance between two atoms.
     */
    public double calculateLongestDistanceBetweenAtomsOfActiveChains() {
        double tmpLongestVector = Double.MIN_VALUE;
        for (String tmpChainID : this.getActiveChains()) {
            Atom[] tmpAtoms = this.protein.getAtomArray(tmpChainID);
            for (Atom tmpAtomA : tmpAtoms) {
                for (Atom tmpAtomB : tmpAtoms) {
                    if (tmpAtomA.getGroup() instanceof AminoAcidImpl && tmpAtomB.getGroup() instanceof AminoAcidImpl) {
                        Point3d tmpPointA = new Point3d(tmpAtomA.getCoords());
                        Point3d tmpPointB = new Point3d(tmpAtomB.getCoords());
                        double tmpDistance = tmpPointA.distance(tmpPointB);
                        if (tmpDistance > tmpLongestVector) {
                            tmpLongestVector = tmpDistance;
                        }
                    }
                }
            }
        }
        return tmpLongestVector;
    }

    /**
     * Gets the mean distance of all disulfide bonds.
     *
     * @return Mean distance of disulfide bonds.
     */
    public double getMeanDisulfideBondLength() {
        if (!this.hasDisulfideBonds()) {
            return -1.0;
        }
        double tmpMeanLength = 0;
        List<SSBond> tmpDisulfideBonds = this.protein.getRawSSBonds();
        for (SSBond tmpSSBond : tmpDisulfideBonds) {
            Point3d tmpCoordA = null;
            Point3d tmpCoordB = null;
            Atom[] tmpAtoms = this.protein.getAtomArray(tmpSSBond.getChainID1());
            for (Atom tmpAtom : tmpAtoms) {
                if (tmpAtom.getGroup().getResidueNumber().getSeqNum() == Integer.parseInt(tmpSSBond.getResnum1())
                        && (tmpAtom.getElement().equals(Element.S) || tmpAtom.getName().equals("SG"))) {
                    tmpCoordA = new Point3d(tmpAtom.getCoords());
                }
            }
            tmpAtoms = this.protein.getAtomArray(tmpSSBond.getChainID2());
            for (Atom tmpAtom : tmpAtoms) {
                if (tmpAtom.getGroup().getResidueNumber().getSeqNum() == Integer.parseInt(tmpSSBond.getResnum2())
                        && (tmpAtom.getElement().equals(Element.S) || tmpAtom.getName().equals("SG"))) {
                    tmpCoordB = new Point3d(tmpAtom.getCoords());
                }
            }
            tmpMeanLength += tmpCoordA.distance(tmpCoordB);
        }
        tmpMeanLength /= this.protein.getRawSSBonds().size();
        return tmpMeanLength;
    }

    /**
     * Gets the number of backbone particles.
     *
     * @return The number of backbone particles.
     * @throws DpdPeptideException DpdPeptideException
     */
    public int getNumberOfBackboneParticles() throws DpdPeptideException {
        int numberOfBackboneParticles = 0;
        for (Map.Entry<String, Atom[]> tmpEntry : this.protein.getChainToCAlphaAtomsMap(this.masterdata.getActiveChains()).entrySet()) {
            numberOfBackboneParticles += tmpEntry.getValue().length;
        }
        return numberOfBackboneParticles;
    }

    /**
     * Gets the raw position of a C-alpha atom identified by given index.
     *
     * @param anIndex C-alpha atom index.
     * @return Raw position.
     * @throws DpdPeptideException DpdPeptideException
     */
    public Point3d getRawPositionOfCAlphaAtomByIndex(String anIndex) throws DpdPeptideException {
        Atom[] tmpCAlphaAtoms = this.protein.getCAlphaAtoms();
        for (Atom tmpCAlphaAtom : tmpCAlphaAtoms) {
            String tmpCAlphaKey = this.createCAlphaKey(tmpCAlphaAtom);
            if (tmpCAlphaKey.equals(anIndex)) {
                return new Point3d(tmpCAlphaAtom.getCoords());
            }
        }
        return null;
    }

    /**
     * Gets the original amino acid sequence.
     *
     * @return Chain ID / Amino acid sequence map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, AminoAcid[]> getOriginalAminoAcidSequence() throws DpdPeptideException {
        HashMap<String, AminoAcid[]> tmpOriginalSequence = new HashMap<String, AminoAcid[]>();
        for (String tmpChainID : this.protein.getChainIDs()) {
            String tmpStringSequence = this.protein.getOriginalSequences().get(tmpChainID);
            AminoAcid[] tmpSequence = new AminoAcid[tmpStringSequence.length()];
            for (int i = 0; i < tmpStringSequence.length(); i++) {
                tmpSequence[i] = this.aminoAcidsUtility.getAminoAcidFromOneLetterCode(tmpStringSequence.charAt(i));
            }
            tmpOriginalSequence.put(tmpChainID, tmpSequence);
        }
        return tmpOriginalSequence;
    }

    /**
     * Gets the currently active amino acids sequence.
     *
     * @return Chain ID / Amino acid sequence map.
     * @throws DpdPeptideException DpdPeptideException
     */
    public HashMap<String, AminoAcid[]> getCurrentAminoAcidSequence() throws DpdPeptideException {
        HashMap<String, AminoAcid[]> tmpOriginalSequence = new HashMap<String, AminoAcid[]>();
        for (String tmpChainID : this.protein.getChainIDs()) {
            String tmpStringSequence = this.protein.getRawSequence(tmpChainID);
            AminoAcid[] tmpSequence = new AminoAcid[tmpStringSequence.length()];
            for (int i = 0; i < tmpStringSequence.length(); i++) {
                tmpSequence[i] = this.aminoAcidsUtility.getAminoAcidFromOneLetterCode(tmpStringSequence.charAt(i));
            }
            tmpOriginalSequence.put(tmpChainID, tmpSequence);
        }
        return tmpOriginalSequence;
    }

    /**
     * Overriddes existing amino acid sequence in given chain.
     *
     * @param aChainID Chain ID.
     * @param anAminoAcidSequence Mutated amino acid sequence.
     * @throws DpdPeptideException DpdPeptideException
     */
    public void setAminoAcidSequence(String aChainID, AminoAcid[] anAminoAcidSequence) throws DpdPeptideException {
        if (anAminoAcidSequence.length != this.getOriginalAminoAcidSequence().get(aChainID).length) {
            throw new DpdPeptideException("Peptide.IncorrectAminoAcidCount");
        }
        String tmpSequence = "";
        for (AminoAcid tmpAminoAcid : anAminoAcidSequence) {
            tmpSequence += tmpAminoAcid.getOneLetterCode();
        }
        this.masterdata.setOverriddenSequence(aChainID, anAminoAcidSequence, this.aminoAcidsUtility);
        this.protein.overrideSequence(aChainID, tmpSequence);
    }

    /**
     * Gets whether the current amino acid sequence has been altered.
     *
     * @return True if the sequence contains mutations.
     */
    public boolean hasChangedAminoAcidSequence() {
        return this.protein.hasOverriddenSequences();
    }

    /**
     * Restores the original amino acid sequence.
     */
    public void restoreOriginalAminoAcidSequence() {
        this.masterdata.clearOverriddenSequences();
        this.protein.clearOverriddenSequences();
    }

    /**
     * Gets all available biological assemblies incl. the asymmetric unit(s).
     *
     * @return String array defining all biological assemblies.
     * @throws IOException IOException
     */
    public String[] getBiologicalAssemblies() throws IOException {
        Structure tmpStructure = this.readPdbAsStructure(this.masterdata.getOriginalPdb());
        int tmpNumberTransforamtions = tmpStructure.getPDBHeader().getBioUnitTranformationMap().size();
        String[] tmpBiologicalAssemblies = new String[tmpNumberTransforamtions + 1];
        tmpBiologicalAssemblies[0] = "Asymmetric Unit";
        for (int i = 1; i <= tmpNumberTransforamtions; i++) {
            tmpBiologicalAssemblies[i] = String.format("Biological Assembly %d", i);
        }
        return tmpBiologicalAssemblies;
    }

    /**
     * Gets the currently set biological assembly.
     *
     * @return Current biological assembly.
     */
    public String getBiologicalAssembly() {
        return this.masterdata.getBiologicalAssembly();
    }

    /**
     * Gets Whether a biological assembly is selected.
     *
     * @return True if a biological assembly is selected.
     */
    public boolean isBiologicalAssembly() {
        return !this.masterdata.getBiologicalAssembly().equals("Asymmetric Unit");
    }

    /**
     * Sets an biological assembly.
     *
     * @param aBiologicalAssembly Bilogical assembly.
     * @throws IOException IOException
     */
    public void setBiologicalAssembly(String aBiologicalAssembly) throws Exception {
        Structure tmpStructure = this.readPdbAsStructure(this.masterdata.getOriginalPdb());
        if (!this.masterdata.getBiologicalAssembly().equals(aBiologicalAssembly)) {
            String tmpOriginalPDB = this.masterdata.getOriginalPdb();
            this.masterdata = new PdbToDpdMasterdata();
            this.masterdata.setOriginalPdb(tmpOriginalPDB);
            // IMPORTANT: Set amino acids definition
            this.masterdata.setAminoAcidsDefinition(this.aminoAcidsDefinition);
        }
        if (!aBiologicalAssembly.equals("Asymmetric Unit")) {
            int tmpNumberOfBiologicalAssembly = Integer.valueOf(aBiologicalAssembly.split(" ")[2]);
            List<BiologicalAssemblyTransformation> tmpTransformations = tmpStructure.getPDBHeader().getBioUnitTranformationMap().get(tmpNumberOfBiologicalAssembly);
            if (tmpTransformations != null && !tmpTransformations.isEmpty()) {
                BiologicalAssemblyBuilder builder = new BiologicalAssemblyBuilder();
                tmpStructure = builder.rebuildQuaternaryStructure(tmpStructure, tmpTransformations);
            }
        }
        this.protein = new Protein(tmpStructure, this.aminoAcidsUtility);
        if (this.masterdata.getActiveChains() == null) {
            this.setActiveChains(this.protein.getChainIDs());
        }
        this.masterdata.setBiologicalAssembly(aBiologicalAssembly);
        this.masterdata.setBiologicalAssemblyFilter(this.createBiologicalAssemblyFilter());
        this.masterdata.setNumberOfModelsInAssembly(this.protein.getNumberOfModels());
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Gets wether the current protein defines disulfide bonds.
     *
     * @return True if dilsulfide bonds are defined.
     */
    public boolean hasDisulfideBonds() {
        return this.protein.getSSBonds().length != 0;
    }
    
    /**
     * Gets the Pdb string.
     *
     * @return Pdb string.
     */
    public String getPdb() {
        return this.masterdata.getOriginalPdb();
    }

    /**
     * Gets the protein object.
     *
     * @return Protein object.
     */
    public Protein getProtein() {
        return this.protein;
    }
    
    /**
     * Gets the last index.
     *
     * @return Last index. 0 if not set.
     */
    public int getLastUsedIndex() {
        return this.masterdata.getLastIndex();
    }

    /**
     * Gets the last C-alpha index.
     *
     * @return Last C-alpha index. 0 if not set.
     */
    public int getLastUsedCAlphaIndex() {
        return this.masterdata.getLastCAlphaIndex();
    }
    
    /**
     * Gets the PDB code.
     *
     * @return PDB code.
     */
    public String getPdbCode() {
        return this.protein.getPDBCode();
    }

    /**
     * Gets the title of the protein.
     *
     * @return Pdb title.
     */
    public String getTitle() {
        return this.protein.getTitle();
    }
    
    /**
     * Returns protein data = this.masterdata XML string
     * @return Protein data = this.masterdata XML string
     * @throws XMLStreamException XMLStreamException
     */
    public String getProteinData() throws XMLStreamException {
        return this.stringUtilityMethods.compressIntoBase64String(this.masterdata.toXml());
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (set only)">
    /**
     * Sets the random number generator
     *
     * @param aRandomNumberGenerator Random number generator
     */
    public void setRandomNumberGenerator(Random aRandomNumberGenerator) {
        this.random = aRandomNumberGenerator;
    }
    
    /**
     * Sets the random number generator seed value
     *
     * @param aRandomSeed Random number generator seed
     */
    public void setSeed(long aRandomSeed) {
        this.masterdata.setSeed(aRandomSeed);
    }

    /**
     * Orientates the protein in a random direction.
     */
    public void setRandomOrientation() {
        if (this.random == null) {
            this.random = new Random(this.masterdata.getSeed());
        }
        Quat4d tmpRandomQuaternion = Tools.randomQuaternion(this.random);
        this.setRotation(tmpRandomQuaternion);
    }
    
    /**
     * Sets the radius of the protein.
     *
     * @param aRadius New protein radius.
     */
    public void setRadius(Double aRadius) {
        this.masterdata.setRadius(aRadius);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * Gets the number of decimals for parameters.
     *
     * @return Number of decimals for parameters.
     */
    public Integer getNumberOfDecimalsForParameters() {
        return this.masterdata.getNumberOfDecimalsForParameters();
    }

    /**
     * Sets the number of decimals for parameters.
     *
     * @param aDecimals Number of decimals for parameters.
     */
    public void setNumberOfDecimalsForParameters(Integer aDecimals) {
        this.masterdata.setNumberOfDecimalsForParameters(aDecimals);
    }
    
    /**
     * gets the probes which should be used for given C-alpha indices.
     *
     * @return C-alpha atom indices mapped to probe particle names.
     */
    public HashMap<String, String> getProbes() {
        return this.masterdata.getCAlphaAtomIndexProbeMap();
    }

    /**
     * Sets the probes which should be used for given C-alpha indices.
     *
     * @param aCAAtomIndexProbeMap C-alpha atom indices mapped to probe particle
     * names.
     */
    public void setProbes(HashMap<String, String> aCAAtomIndexProbeMap) {
        this.masterdata.setCAlphaAtomIndexProbeMap(aCAAtomIndexProbeMap);
    }

    /**
     * Removes all probes.
     */
    public void clearProbes() {
        this.masterdata.setCAlphaAtomIndexProbeMap(null);
    }

    /**
     * Gets whether probes are defined.
     *
     * @return True if probes are set.
     */
    public boolean hasProbes() {
        return !this.masterdata.getCAlphaAtomIndexProbeMap().isEmpty();
    }
    
    /**
     * Gets the rotation vector in radians.
     *
     * @return Rotation vector in radians.
     */
    public Vector3d getRotation() {
        Quat4d tmpQuaternion = this.masterdata.getRotation();
        if (tmpQuaternion == null) {
            return new Vector3d();
        }
        return Tools.quaternionToEulerAngles(tmpQuaternion);
    }

    /**
     * Sets the rotation.
     *
     * @param aRotation Rotation as quaternion.
     */
    public void setRotation(Quat4d aRotation) {
        this.masterdata.setRotation(aRotation);
    }

    /**
     * Sets the rotation in Euler angles represented by a Vector3d object.
     * Assuming the angles are in radians.
     *
     * @param aRotation Euler angels in radians.
     */
    public void setRotation(Vector3d aRotation) {
        // Assuming the angles are in radians.
        Quat4d tmpQuaternion = Tools.eulerAnglesToQuaternion(aRotation);
        this.masterdata.setRotation(tmpQuaternion);
    }

    /**
     * Sets the rotation in Euler angles. Assuming the angles are in radians.
     *
     * @param aX X-angle.
     * @param aY Y-angle.
     * @param aZ Z-angle;
     */
    public void setRotation(double aX, double aY, double aZ) {
        this.setRotation(new Vector3d(aX, aY, aZ));
    }
    
    /**
     * Sets rotation to default rotation
     */
    public void setDefaultRotation() {
        this.masterdata.setDefaultRotation();
    }
    
    /**
     * Gets the pH-value.
     *
     * @return The pH-value.
     */
    public Double getPhValue() {
        return this.masterdata.getPhValue();
    }

    /**
     * Sets the pH-value.
     *
     * @param phValue The pH-Value.
     */
    public void setPhValue(Double phValue) {
        this.masterdata.setPhValue(phValue);
    }

    /**
     * Gets the center coordinate.
     *
     * @return Center coordinate
     */
    public Point3d getCenter() {
        return this.masterdata.getCenter();
    }

    /**
     * Sets the center coordinate.
     *
     * @param aCenter Center coordinate.
     */
    public void setCenter(Point3d aCenter) {
        this.masterdata.setCenter(aCenter);
    }

    /**
     * Sets the center coordinate.
     *
     * @param aX x-coordinate of center.
     * @param aY y-coordinate of center.
     * @param aZ z-coordinate of center.
     */
    public void setCenter(double aX, double aY, double aZ) {
        this.masterdata.setCenter(new Point3d(aX, aY, aZ));
    }
    
    /**
     * Gets the masterdata to restore this object.
     *
     * @return The masterdata.
     */
    public PdbToDpdMasterdata getMasterdata() {
        return this.masterdata;
    }
    
    /**
     * Updates the object by given masterdata.
     *
     * @param aMasterdata PdbToDpd masterdata Object.
     */
    public void setMasterdata(PdbToDpdMasterdata aMasterdata) {
        this.masterdata = aMasterdata;
        this.readPdbString(this.masterdata.getOriginalPdb());
    }
    
    /**
     * Sets whether the amino acid sequence is circular.
     *
     * @param anIsCircular True if amino acid sequence is circular.
     */
    public void setCircular(boolean anIsCircular) {
        this.masterdata.setCircular(anIsCircular);
    }

    /**
     * Gets whether the amino acid sequence is circular.
     *
     * @return True if amino acid sequence is circular.
     */
    public boolean isCircular() {
        return this.masterdata.isCircular();
    }
    
    /**
     * Checks whether chains are selected.
     *
     * @return True, if chains are selected.
     */
    public boolean hasChainSelection() {
        if (this.masterdata.getActiveChains() == null
                || this.masterdata.getActiveChains().size() == this.protein.getChainIDs().size()) {
            return false;
        }
        return true;
    }

    /**
     * Clears chain selecteion.
     */
    public void clearChainSelection() {
        this.masterdata.setActiveChains(null);
    }

    /**
     * Checks whether a pH-value is set.
     *
     * @return True, if a pH-value is set.
     */
    public boolean hasPHValue() {
        return this.masterdata.getPhValue() != null;
    }

    /**
     * Clears the pH-value.
     */
    public void clearPHValue() {
        this.masterdata.setPhValue(null);
    }

    /**
     * Checks whether the backbone particle status is set.
     *
     * @return True, if the backbone particle status is set.
     */
    public boolean hasStatus() {
        return this.masterdata.getBackboneParticleStatusArray() != null;
    }

    /**
     * Clears the backbone particle status.
     */
    public void clearStatus() {
        this.masterdata.setBackboneParticleStatusArray(null);
    }

    /**
     * Checks whether the backbone particle status is set.
     *
     * @return True, if the backbone particle status is set.
     */
    public boolean hasSegments() {
        return this.masterdata.getBackboneParticleSegmentArray() != null;
    }

    /**
     * Clears the backbone particle status.
     */
    public void clearSegments() {
        this.masterdata.setBackboneParticleSegmentArray(null);
    }
    
    /**
     * Gets whether to use frequency Spices.
     *
     * @return False (default), for non-frequency Spices.
     */
    public boolean isUseOfFrequencySpices() {
        return this.masterdata.isUseOfFrequencySpices();
    }

    /**
     * Sets whether to use frequency Spices.
     *
     * @param anUseFrequencySpices False (default), for non-frequency Spices.
     */
    public void setUseOfFrequencySpices(boolean anUseFrequencySpices) {
        this.masterdata.setUseOfFrequencySpices(anUseFrequencySpices);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Reads a pdb string.
     *
     * @param aPdbResource Pdb string.
     * @return True if successfully loaded.
     */
    private boolean readPdbString(String aPdbString) {
        try {
            this.masterdata.setOriginalPdb(aPdbString);
            this.setBiologicalAssembly(this.masterdata.getBiologicalAssembly());
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            anException.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Reads a PDB string and returns a BioJava structure object including
     * biological assemblies.
     *
     * @param aPdbString PDB string.
     * @return BioJava tructure object.
     * @throws IOException
     */
    private Structure readPdbAsStructure(String aPdbString) throws IOException {
        FileParsingParameters params = new FileParsingParameters();
        // should secondary structure get parsed from the file
        params.setParseSecStruc(false);
        params.setLoadChemCompInfo(false);
        params.setParseBioAssembly(true);
        PDBFileParser tmpReader = new PDBFileParser();
        tmpReader.setFileParsingParameters(params);
        return tmpReader.parsePDBFile(new ByteArrayInputStream(aPdbString.getBytes()));
    }

    /**
     * Evaluates the SS bond connections and returns a list containing set of
     * distinct connected chains.
     *
     * @return Connected chain sets.
     */
    private ArrayList<ArrayList<String>> evaluateSSBondConnections() {
        ArrayList<HashSet<String>> tmpProcessedConnections = new ArrayList<HashSet<String>>();
        String[] tmpConnections = this.protein.getChainConnections(this.masterdata.getActiveChains());
        for (String tmpConnection : tmpConnections) {
            String[] tmpConnectionTokens = tmpConnection.split(";");
            HashSet<String> tmpFirstOccurance = null;
            boolean tmpFound = false;
            for (int i = tmpProcessedConnections.size() - 1; i >= 0; i--) {
                if (tmpProcessedConnections.get(i).contains(tmpConnectionTokens[0]) || tmpProcessedConnections.get(i).contains(tmpConnectionTokens[1])) {
                    if (tmpFirstOccurance == null) {
                        tmpProcessedConnections.get(i).add(tmpConnectionTokens[0]);
                        tmpProcessedConnections.get(i).add(tmpConnectionTokens[1]);
                        tmpFirstOccurance = tmpProcessedConnections.get(i);
                        tmpFound = true;
                    } else {
                        tmpFirstOccurance.addAll(tmpProcessedConnections.get(i));
                        tmpProcessedConnections.remove(i);
                        tmpFound = true;
                    }
                }
            }
            if (!tmpFound) {
                HashSet<String> tmpProcessedConnection = new HashSet<String>();
                tmpProcessedConnection.add(tmpConnectionTokens[0]);
                tmpProcessedConnection.add(tmpConnectionTokens[1]);
                tmpProcessedConnections.add(tmpProcessedConnection);
            }
        }
        for (String tmpChainID : this.masterdata.getActiveChains()) {
            boolean tmpFound = false;
            for (HashSet<String> tmpConnectionSet : tmpProcessedConnections) {
                if (tmpConnectionSet.contains(tmpChainID)) {
                    tmpFound = true;
                    break;
                }
            }
            if (!tmpFound) {
                HashSet<String> tmpProcessedConnection = new HashSet<String>();
                tmpProcessedConnection.add(tmpChainID);
                tmpProcessedConnections.add(tmpProcessedConnection);
            }
        }
        ArrayList<ArrayList<String>> tmpResult = new ArrayList<ArrayList<String>>();
        for (HashSet<String> tmpConnectionSet : tmpProcessedConnections) {
            ArrayList<String> tmpConnectionList = new ArrayList<String>(tmpConnectionSet);
            Collections.sort(tmpConnectionList);
            tmpResult.add(tmpConnectionList);
        }
        return tmpResult;
    }

    /**
     * Rotates the supplied coordinates in accordance to given quaternion.
     *
     * @param aCoordinates Chain ID coordinates map.
     * @param aRotation Rotation quaternion.
     * @return Rotated chain ID coordinates map.
     */
    private HashMap<String, Point3d[]> getRotatedCoordinates(HashMap<String, Point3d[]> aCoordinates, Quat4d aRotation) {
        Matrix3d tmpRotationMatrix = new Matrix3d();
        tmpRotationMatrix.set(aRotation);
        HashMap<String, Point3d[]> tmpRotatedCoordinates = new HashMap<String, Point3d[]>();
        for (Map.Entry<String, Point3d[]> tmpEntry : aCoordinates.entrySet()) {
            Point3d[] tmpCoordinates = tmpEntry.getValue();
            // Rotate coordinates
            for (int i = 0; i < tmpCoordinates.length; i++) {
                tmpRotationMatrix.transform(tmpCoordinates[i]);
            }
            tmpRotatedCoordinates.put(tmpEntry.getKey(), tmpCoordinates);
        }
        return tmpRotatedCoordinates;
    }

    /**
     * Translates the supplied coordinates to given point representing the new
     * center of mass.
     *
     * @param aCoordinates Chain ID coordinates map.
     * @param aCenter New center of mass.
     * @return Translated chain ID coordinates map.
     */
    private HashMap<String, Point3d[]> getTranslatedCoordinates(HashMap<String, Point3d[]> aCoordinates, Point3d aCenter) {
        ArrayList<Point3d> tmpCombinedCoordinates = new ArrayList<Point3d>();
        for (Map.Entry<String, Point3d[]> tmpEntry : aCoordinates.entrySet()) {
            tmpCombinedCoordinates.addAll(Arrays.asList(tmpEntry.getValue()));
        }
        Point3d tmpCenter = Tools.getCenter(tmpCombinedCoordinates.toArray(new Point3d[tmpCombinedCoordinates.size()]));
        tmpCenter.negate();
        tmpCenter.add(aCenter);
        Matrix4d tmpTranslationMatrix = Tools.createTranslationMatrix(new Vector3d(tmpCenter));
        HashMap<String, Point3d[]> tmpTranslatedCoordinates = new HashMap<String, Point3d[]>();
        for (Map.Entry<String, Point3d[]> tmpEntry : aCoordinates.entrySet()) {
            Point3d[] tmpCoordinates = tmpEntry.getValue();
            // Translate coodinates
            for (int i = 0; i < tmpCoordinates.length; i++) {
                tmpTranslationMatrix.transform(tmpCoordinates[i]);
            }
            tmpTranslatedCoordinates.put(tmpEntry.getKey(), tmpCoordinates);
        }
        return tmpTranslatedCoordinates;
    }

    /**
     * Scales the supplied coordinates to fit given radius.
     *
     * @param aCoordinates Chain ID coordinates map.
     * @param aRadius New radius.
     * @return Scaled chain ID coordinates map.
     */
    private HashMap<String, Point3d[]> getScaledCoordinates(HashMap<String, Point3d[]> aCoordinates, double aRadius) {
        ArrayList<Point3d> tmpCombinedCoordinates = new ArrayList<Point3d>();
        for (Map.Entry<String, Point3d[]> tmpEntry : aCoordinates.entrySet()) {
            tmpCombinedCoordinates.addAll(Arrays.asList(tmpEntry.getValue()));
        }
        double tmpDiameter = Tools.getDiameter(tmpCombinedCoordinates.toArray(new Point3d[tmpCombinedCoordinates.size()]));
        double tmpRatio = (aRadius * 2) / tmpDiameter;
        // Scale matrix            
        Matrix3d tmpScaleMatrix = Tools.createScalingMatrix(tmpRatio);
        HashMap<String, Point3d[]> tmpScaledCoordiantes = new HashMap<String, Point3d[]>();
        for (Map.Entry<String, Point3d[]> tmpEntry : aCoordinates.entrySet()) {
            Point3d[] tmpCoordinates = tmpEntry.getValue();
            // Scale coordinates
            for (int i = 0; i < tmpCoordinates.length; i++) {
                tmpScaleMatrix.transform(tmpCoordinates[i]);
            }
            tmpScaledCoordiantes.put(tmpEntry.getKey(), tmpCoordinates);
        }
        return tmpScaledCoordiantes;
    }

    /**
     * Gets the transformed coordinates in accordance to given center of mass,
     * radius and rotation.
     *
     * @param aCenter New center of the protein.
     * @param aRadius New radius of the protein.
     * @param aRotation Rotation quaternion. Ignored if null.
     * @return Scaled Coordinates.
     * @throws DpdPeptideException
     */
    private HashMap<String, Point3d[]> getTransformedCoordinates(HashMap<String, Point3d[]> aCoordinates, Point3d aCenter,
            double aRadius, Quat4d aRotation) throws DpdPeptideException {
        // Center coordinates
        HashMap<String, Point3d[]> tmpCoordinates = this.getTranslatedCoordinates(aCoordinates, new Point3d(0, 0, 0));
        // Rotate...
        if (aRotation != null) {
            // Adapt Jmol coordinate system to MFsim (z = y; y = -z)
            Vector3d tmpAngles = Tools.quaternionToEulerAngles(aRotation);
            tmpAngles = new Vector3d(tmpAngles.x, -tmpAngles.z, tmpAngles.y);
            tmpCoordinates = this.getRotatedCoordinates(tmpCoordinates, Tools.eulerAnglesToQuaternion(tmpAngles));
        }
        // Scale...
        tmpCoordinates = this.getScaledCoordinates(tmpCoordinates, aRadius);
        // Finally translate....
        tmpCoordinates = this.getTranslatedCoordinates(tmpCoordinates, aCenter);
        return tmpCoordinates;
    }

    /**
     * Creates a C-alpha key string from given C-alpha atom.
     *
     * @param aCAlphaAtom C-alpha atom.
     * @return C-alpha key string.
     */
    private String createCAlphaKey(Atom aCAlphaAtom) {
        Group tmpAtomGroup = aCAlphaAtom.getGroup();
        return String.format("[%s]%d:%s.CA #%d", tmpAtomGroup.getPDBName(), tmpAtomGroup.getResidueNumber().getSeqNum(),
                tmpAtomGroup.getChainId(), aCAlphaAtom.getPDBserial());
    }

    /**
     * Gets the filter command for biological assemblies.
     *
     * @return Filter command.
     */
    private String createBiologicalAssemblyFilter() {
        if (this.masterdata.getBiologicalAssembly().equals("Asymmetric Unit")) {
            return null;
        } else {
            int tmpNumberOfBiologicalAssembly = Integer.valueOf(this.masterdata.getBiologicalAssembly().split(" ")[2]);
            return String.format(" filter \"BIOMOLECULE %d\"", tmpNumberOfBiologicalAssembly);
        }
    }
    // </editor-fold>

}
