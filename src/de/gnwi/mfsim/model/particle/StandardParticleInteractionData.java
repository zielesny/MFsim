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

import de.gnwi.jdpd.utilities.FileOutputStrings;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.spices.SpicesConstants;
import de.gnwi.mfsim.model.peptide.base.AminoAcid;
import de.gnwi.mfsim.model.peptide.base.AminoAcids;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import javax.swing.JOptionPane;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Singleton that manages data for particles and particle-particle interactions.
 * NOTE: If possible this singleton also initialises AminoAcids singleton.
 *
 * @author Achim Zielesny
 */
public final class StandardParticleInteractionData {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * StandardParticleInteractionData instance
     */
    private static StandardParticleInteractionData standardParticleInteractionData = new StandardParticleInteractionData();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Miscellaneous utility methods
     */
    private final MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * HashMap that maps particle to particle descriptions
     */
    private HashMap<String, StandardParticleDescription> particleToDescriptionMap;
    
    /**
     * Particle pair to volume-based bond length map
     */
    private HashMap<String, Double> particlePairToBondLengthMap;

    /**
     * HashMap that maps particle-pair-temperature descriptor (key) to
     * interaction a(ij) (value)
     */
    private HashMap<String, String> particlePairTemperatureToInteractionMap;

    /**
     * Available temperatures as formatted strings
     */
    private String[] temperatureStrings;

    /**
     * HashMap that maps one-letter-code of amino acid to its description
     */
    private HashMap<String, AminoAcidDescription> aminoAcidToDescriptionMap;

    /**
     * HashMap that maps trivial name of amino acid to its description
     */
    private HashMap<String, AminoAcidDescription> aminoAcidNameToDescriptionMap;

    /**
     * Change detection flag. True: Particle data changed, false: Otherwise
     */
    private boolean hasChanged;
    
    /**
     * Minimum a(ij) interaction values for temperatures
     */
    private double[] minInteractionValueForTemperatures;
    
    /**
     * Maximum a(ij) interaction values for temperatures
     */
    private double[] maxInteractionValueForTemperatures;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private singleton constructor">
    /**
     * Singleton constructor
     */
    private StandardParticleInteractionData() {
        if (!this.readParticleSetFile()) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(
                null, 
                ModelMessage.get("Error.MissingCorruptParticleData"), 
                ModelMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.appendToLogfile(true, "StandardParticleInteractionData.Constructor: Missing or corrupt particle data");
            ModelUtils.exitApplication(-1);
            // </editor-fold>
        }
        // Check default H2O (water) particle
        if (!this.getDefaultWaterParticle().toUpperCase(Locale.ENGLISH).equals("H2O")) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(
                null, 
                ModelMessage.get("Error.NoDefaultWaterParticle"), 
                ModelMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.appendToLogfile(true, "StandardParticleInteractionData.Constructor: Default H2O particle is missing");
            ModelUtils.exitApplication(-1);
            // </editor-fold>
        }
        // Initialize change detection flag
        this.hasChanged = false;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public singleton instance method">
    /**
     * Singleton initialisation and instance method
     *
     * @return Preferences instance
     */
    public static StandardParticleInteractionData getInstance() {
        return StandardParticleInteractionData.standardParticleInteractionData;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Initialisation related methods">
    /**
     * Resets all particle related data to persistent values
     */
    public void reset() {
        // IMPORTANT: FIRST clear amino acids singleton ...
        AminoAcids.getInstance().getAminoAcidsUtility().clearAminoAcids();
        // ... THEN create new singleton instance
        StandardParticleInteractionData.standardParticleInteractionData = new StandardParticleInteractionData();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle related methods">
    /**
     * Adds particle data
     *
     * @param aParticleDescription Particle data
     * @return True: Particle data changed, false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public boolean addParticleDescription(StandardParticleDescription aParticleDescription) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleDescription == null) {
            throw new IllegalArgumentException("aParticleDescription is null.");
        }

        // </editor-fold>
        if (!this.particleToDescriptionMap.containsKey(aParticleDescription.getParticle())) {
            this.particleToDescriptionMap.put(aParticleDescription.getParticle(), aParticleDescription);
            // Change in data occurred: Set change detection flag to true!
            this.hasChanged = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Update particle data
     *
     * @param aParticleDescription Particle data
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void updateParticleDescription(StandardParticleDescription aParticleDescription) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleDescription == null) {
            throw new IllegalArgumentException("aParticleDescription is null.");
        }

        // </editor-fold>
        if (this.particleToDescriptionMap.containsKey(aParticleDescription.getParticle())) {
            if (!aParticleDescription.getParticleDescriptionString().equals(this.particleToDescriptionMap.get(aParticleDescription.getParticle()).getParticleDescriptionString())) {
                this.particleToDescriptionMap.remove(aParticleDescription.getParticle());
                this.particleToDescriptionMap.put(aParticleDescription.getParticle(), aParticleDescription);
                // Volume may have changed: Clear particle pair to bond length map
                this.particlePairToBondLengthMap.clear();
                // Change in data occurred: Set change detection flag to true!
                this.hasChanged = true;
            }
        } else {
            this.particleToDescriptionMap.put(aParticleDescription.getParticle(), aParticleDescription);
            // Change in data occurred: Set change detection flag to true!
            this.hasChanged = true;
        }
    }

    /**
     * Returns particle data
     *
     * @param aParticle Particle
     * @return Returns particle data or null if none are available
     */
    public StandardParticleDescription getParticleDescription(String aParticle) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || aParticle.isEmpty()) {
            return null;
        }

        // </editor-fold>
        return this.particleToDescriptionMap.get(aParticle);
    }

    /**
     * Checks if particle is available
     *
     * @param aParticle Particle
     * @return True: Particle is available, false: Otherwise
     */
    public boolean hasParticle(String aParticle) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || aParticle.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.particleToDescriptionMap.containsKey(aParticle);
    }

    /**
     * Returns all particles sorted ascending
     *
     * @return All particles sorted ascending or null if none are available
     */
    public String[] getAllParticlesSortedAscending() {
        if (this.particleToDescriptionMap.size() == 0) {
            return null;
        } else {
            String[] tmpParticles = this.particleToDescriptionMap.keySet().toArray(new String[0]);
            Arrays.sort(tmpParticles);
            return tmpParticles;
        }
    }

    /**
     * Returns Hashmap with all particles (key: Particle, value: Name)
     *
     * @return HashMap with all particles (key: Particle, value: Name)
     */
    public HashMap<String, String> getAvailableParticles() {
        String[] tmpParticles = this.getAllParticlesSortedAscending();
        HashMap<String, String> tmpAvailableParticles = new HashMap<String, String>(tmpParticles.length);
        for (String tmpParticle : tmpParticles) {
            // Key: Particle, value: Name of particle
            tmpAvailableParticles.put(tmpParticle, this.getParticleDescription(tmpParticle).getName());
        }
        return tmpAvailableParticles;
    }

    /**
     * Returns sorted protein backbone probe particles
     *
     * @return Sorted protein backbone probe particles or null if none are
     * available
     */
    public String[] getAllSortedProteinBackboneProbeParticles() {
        String[] tmpSortedParticles = this.getAllParticlesSortedAscending();
        if (tmpSortedParticles == null) {
            return null;
        } else {
            LinkedList<String> tmpProteinBackboneProbeParticleList = new LinkedList<String>();
            for (String tmpSingleParticle : tmpSortedParticles) {
                if (tmpSingleParticle.matches(ModelDefinitions.PARTICLE_PROTEIN_BACKBONE_PROBE_PATTERN_STRING)) {
                    tmpProteinBackboneProbeParticleList.add(tmpSingleParticle);
                }
            }
            if (tmpProteinBackboneProbeParticleList.size() > 0) {
                return tmpProteinBackboneProbeParticleList.toArray(new String[0]);
            } else {
                return null;
            }
        }
    }

    /**
     * Returns if current particle set has protein backbone probe particles
     *
     * @return True: current particle set has protein backbone probe particles,
     * false: otherwise
     */
    public boolean hasProteinBackboneProbeParticles() {
        String[] tmpSortedParticles = this.getAllParticlesSortedAscending();
        if (tmpSortedParticles == null) {
            return false;
        } else {
            for (String tmpSingleParticle : tmpSortedParticles) {
                if (tmpSingleParticle.matches(ModelDefinitions.PARTICLE_PROTEIN_BACKBONE_PROBE_PATTERN_STRING)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Returns specific protein backbone probe particles for specified backbone
     * particle
     *
     * @param aBackboneParticle Backbone particle to return probe particles for
     * @return Specific protein backbone probe particles for specified particle
     * or null if none are available
     */
    public String[] getSpecificProteinBackboneProbeParticles(String aBackboneParticle) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBackboneParticle == null || !aBackboneParticle.matches(ModelDefinitions.PARTICLE_PROTEIN_BACKBONE_PARTICLE_PATTERN_STRING)) {
            return null;
        }
        // </editor-fold>
        String tmpParticle = aBackboneParticle.substring(0, aBackboneParticle.length() - 2);
        String tmpProbePatternString = tmpParticle + ModelDefinitions.PARTICLE_PROTEIN_BACKBONE_PROBE_PATTERN_ENDING_STRING;
        String[] tmpProbeParticles = this.getAllSortedProteinBackboneProbeParticles();
        if (tmpProbeParticles == null) {
            return null;
        }
        LinkedList<String> tmpSpecificProbeParticleList = new LinkedList<String>();
        for (String tmpSingleProbeParticle : tmpProbeParticles) {
            if (tmpSingleProbeParticle.matches(tmpProbePatternString)) {
                tmpSpecificProbeParticleList.add(tmpSingleProbeParticle);
            }
        }
        if (tmpSpecificProbeParticleList.isEmpty()) {
            return null;
        } else {
            return tmpSpecificProbeParticleList.toArray(new String[0]);
        }
    }

    /**
     * Returns default "H2O" (water) particle
     * 
     * @return Default "H2O" (water) particle or null if none is defined
     */
    public String getDefaultWaterParticle() {
        String[] tmpParticles = this.getAllParticlesSortedAscending();
        if (tmpParticles == null) {
            return null;
        }
        // Select water (H2O) particle as default if possible
        String tmpDefaultParticle = null;
        for (String tmpParticle : tmpParticles) {
            if (tmpParticle.toUpperCase(Locale.ENGLISH).equals("H2O")) {
                tmpDefaultParticle = "H2O";
                break;
            }
        }
        return tmpDefaultParticle;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Amino acid description related methods">
    /**
     * Adds amino acid data
     *
     * @param anAminoAcidDescription Amino acid data
     * @return True: Amino acid data changed, false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public boolean addAminoAcidDescription(AminoAcidDescription anAminoAcidDescription) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anAminoAcidDescription == null) {
            throw new IllegalArgumentException("anAminoAcidDescription is null.");
        }
        if (this.aminoAcidToDescriptionMap.containsKey(anAminoAcidDescription.getOneLetterCode()) && !this.aminoAcidNameToDescriptionMap.containsKey(anAminoAcidDescription.getName())
                || !this.aminoAcidToDescriptionMap.containsKey(anAminoAcidDescription.getOneLetterCode()) && this.aminoAcidNameToDescriptionMap.containsKey(anAminoAcidDescription.getName())) {
            throw new IllegalArgumentException("anAminoAcidDescription is corrupt.");
        }

        // </editor-fold>
        if (!this.aminoAcidToDescriptionMap.containsKey(anAminoAcidDescription.getOneLetterCode())) {
            this.aminoAcidToDescriptionMap.put(anAminoAcidDescription.getOneLetterCode(), anAminoAcidDescription);
            this.aminoAcidNameToDescriptionMap.put(anAminoAcidDescription.getName(), anAminoAcidDescription);
            // Change in data occurred: Set change detection flag to true!
            this.hasChanged = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns if amino acid definitions are available at all
     *
     * @return True: Amino acid definitions are available, false: Otherwise
     */
    public boolean hasAminoAcidDefinitions() {
        return this.aminoAcidToDescriptionMap != null;
    }

    /**
     * Checks if amino acid is available
     *
     * @param aOneLetterCode One-letter code
     * @return True: Particle is available, false: Otherwise
     */
    public boolean hasAminoAcidDescription(String aOneLetterCode) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aOneLetterCode == null || aOneLetterCode.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.aminoAcidToDescriptionMap.containsKey(aOneLetterCode);
    }

    /**
     * Returns amino acid description for name
     *
     * @param aName Name
     * @return Returns amino acid data or null if none are available
     */
    public AminoAcidDescription getAminoAcidDescriptionForName(String aName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aName == null || aName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        return this.aminoAcidNameToDescriptionMap.get(aName);
    }

    /**
     * Returns amino acid description for one-letter code
     *
     * @param aOneLetterCode One-letter code
     * @return Returns amino acid data or null if none are available
     */
    public AminoAcidDescription getAminoAcidDescriptionForOneLetterCode(String aOneLetterCode) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aOneLetterCode == null || aOneLetterCode.isEmpty()) {
            return null;
        }

        // </editor-fold>
        return this.aminoAcidToDescriptionMap.get(aOneLetterCode);
    }

    /**
     * Returns array with complete amino acid descriptions
     *
     * @return Returns array with complete amino acid descriptions or null if
     * none are available
     */
    public AminoAcidDescription[] getArrayOfAllAminoAcidDescriptions() {
        if (this.aminoAcidToDescriptionMap == null) {
            return null;
        } else {
            return this.aminoAcidToDescriptionMap.values().toArray(new AminoAcidDescription[0]);
        }
    }

    /**
     * Returns collection with complete amino acid descriptions
     *
     * @return Returns collection with complete amino acid descriptions or null
     * if none are available
     */
    public Collection<AminoAcidDescription> getCollectionOfAllAminoAcidDescriptions() {
        if (this.aminoAcidToDescriptionMap == null) {
            return null;
        } else {
            return this.aminoAcidToDescriptionMap.values();
        }
    }

    /**
     * Returns array with complete sorted amino acid names
     *
     * @return Returns array with complete sorted amino acid names or null if
     * none are available
     */
    public String[] getSortedAminoAcidNames() {
        if (this.aminoAcidNameToDescriptionMap == null) {
            return null;
        } else {
            String[] tmpAminoAcidNames = this.aminoAcidNameToDescriptionMap.keySet().toArray(new String[0]);
            Arrays.sort(tmpAminoAcidNames);
            return tmpAminoAcidNames;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Interactions a(ij) related methods">
    /**
     * Add interaction a(ij) as String
     *
     * @param aParticle1 Particle 1 of pair
     * @param aParticle2 Particle 2 of pair
     * @param aTemperatureRepresentation Temperature representation
     * @param anInteraction Interaction a(ij)
     * @return True: Interactions a(ij) changed, false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public boolean addInteraction(String aParticle1, String aParticle2, String aTemperatureRepresentation, String anInteraction) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle1 == null || aParticle1.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle1)) {
            throw new IllegalArgumentException("aParticle1 is illegal.");
        }
        if (aParticle2 == null || aParticle2.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle2)) {
            throw new IllegalArgumentException("aParticle2 is is illegal.");
        }
        if (aTemperatureRepresentation == null || aTemperatureRepresentation.isEmpty()) {
            throw new IllegalArgumentException("aTemperatureRepresentation is is illegal.");
        }
        if (anInteraction == null || anInteraction.isEmpty()) {
            throw new IllegalArgumentException("anInteraction is is illegal.");
        }

        // </editor-fold>
        String tmpParticlePairTemperatureKey = this.getParticlePairTemperatureKey(aParticle1, aParticle2, this.getTemperatureString(aTemperatureRepresentation));
        if (!this.particlePairTemperatureToInteractionMap.containsKey(tmpParticlePairTemperatureKey)) {
            this.particlePairTemperatureToInteractionMap.put(tmpParticlePairTemperatureKey, anInteraction);
            // Change in data occurred: Set change detection flag to true!
            this.hasChanged = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns interaction a(ij) as String
     *
     * @param aParticlePair Particle pair
     * @param aTemperatureRepresentation Temperature representation
     * @return Interaction a(ij) as String or null if interaction is not
     * available (check with method hasInteraction())
     * @throws IllegalArgumentException if an argument is illegal
     */
    public String getInteraction(String aParticlePair, String aTemperatureRepresentation) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePair == null || aParticlePair.isEmpty()) {
            throw new IllegalArgumentException("aParticlePair is illegal.");
        }
        if (aTemperatureRepresentation == null || aTemperatureRepresentation.isEmpty()) {
            throw new IllegalArgumentException("aTemperatureRepresentation is illegal.");
        }

        // </editor-fold>
        String[] tmpParticles = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(aParticlePair);
        if (tmpParticles.length != 2) {
            throw new IllegalArgumentException("aParticlePair is illegal.");
        }
        return this.particlePairTemperatureToInteractionMap.get(this.getParticlePairTemperatureKey(tmpParticles[0], tmpParticles[1], this.getTemperatureString(aTemperatureRepresentation)));
    }

    /**
     * Returns interaction a(ij) as String
     *
     * @param aParticle1 Particle 1 of pair
     * @param aParticle2 Particle 2 of pair
     * @param aTemperatureRepresentation Temperature representation
     * @return Interaction a(ij) as String or null if interaction is not
     * available (check with method hasInteraction())
     */
    public String getInteraction(String aParticle1, String aParticle2, String aTemperatureRepresentation) {
        return this.particlePairTemperatureToInteractionMap.get(this.getParticlePairTemperatureKey(aParticle1, aParticle2, this.getTemperatureString(aTemperatureRepresentation)));
    }

    /**
     * Updates interaction a(ij) as String
     *
     * @param aParticle1 Particle 1 of pair
     * @param aParticle2 Particle 2 of pair
     * @param aTemperatureRepresentation Temperature representation
     * @param anInteraction Interaction a(ij)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void updateInteraction(String aParticle1, String aParticle2, String aTemperatureRepresentation, String anInteraction) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle1 == null || aParticle1.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle1)) {
            throw new IllegalArgumentException("aParticle1 is null/empty.");
        }
        if (aParticle2 == null || aParticle2.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle2)) {
            throw new IllegalArgumentException("aParticle2 is null/empty.");
        }
        if (aTemperatureRepresentation == null || aTemperatureRepresentation.isEmpty()) {
            throw new IllegalArgumentException("aTemperatureRepresentation is is illegal.");
        }
        if (anInteraction == null || anInteraction.isEmpty()) {
            throw new IllegalArgumentException("anInteraction is is illegal.");
        }

        // </editor-fold>
        String tmpParticlePairTemperatureKey = this.getParticlePairTemperatureKey(aParticle1, aParticle2, this.getTemperatureString(aTemperatureRepresentation));
        if (this.particlePairTemperatureToInteractionMap.containsKey(tmpParticlePairTemperatureKey)) {
            if (!anInteraction.equals(this.particlePairTemperatureToInteractionMap.get(tmpParticlePairTemperatureKey))) {
                this.particlePairTemperatureToInteractionMap.remove(tmpParticlePairTemperatureKey);
                this.particlePairTemperatureToInteractionMap.put(tmpParticlePairTemperatureKey, anInteraction);
                // Change in data occurred: Set change detection flag to true!
                this.hasChanged = true;
            }
        } else {
            this.particlePairTemperatureToInteractionMap.put(tmpParticlePairTemperatureKey, anInteraction);
            // Change in data occurred: Set change detection flag to true!
            this.hasChanged = true;
        }
    }

    /**
     * Checks if interaction a(ij) is available
     *
     * @param aParticlePair Particle pair
     * @param aTemperatureRepresentation Temperature representation
     * @return True: Interaction a(ij) is available, false: Otherwise
     */
    public boolean hasInteraction(String aParticlePair, String aTemperatureRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePair == null || aParticlePair.isEmpty()) {
            return false;
        }

        // </editor-fold>
        String[] tmpParticles = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(aParticlePair);
        if (tmpParticles.length != 2) {
            return false;
        }
        return this.hasInteraction(tmpParticles[0], tmpParticles[1], aTemperatureRepresentation);
    }

    /**
     * Checks if interaction a(ij) is available
     *
     * @param aParticle1 Particle 1 of pair
     * @param aParticle2 Particle 2 of pair
     * @param aTemperatureRepresentation Temperature representation
     * @return True: Interaction is available, false: Otherwise
     */
    public boolean hasInteraction(String aParticle1, String aParticle2, String aTemperatureRepresentation) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle1 == null || aParticle1.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle1)) {
            return false;
        }
        if (aParticle2 == null || aParticle2.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle2)) {
            return false;
        }

        // </editor-fold>
        return this.particlePairTemperatureToInteractionMap.containsKey(this.getParticlePairTemperatureKey(aParticle1, aParticle2, this.getTemperatureString(aTemperatureRepresentation)));
    }

    /**
     * Minimum a(ij) interaction values for temperatures
     * 
     * @return Minimum a(ij) interaction values for temperatures
     */
    public double[] getMinInteractionValueForTemperatures() {
        return this.minInteractionValueForTemperatures;
    }

    /**
     * Maximum a(ij) interaction values for temperatures
     * 
     * @return Maximum a(ij) interaction values for temperatures
     */
    public double[] getMaxInteractionValueForTemperatures() {
        return this.maxInteractionValueForTemperatures;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle pair volume-based bond length related methods">
    /**
     * Returns volume-based bond length of particle pair in Angstrom.
     * 
     * @param aParticle1 Particle 1 of pair
     * @param aParticle2 Particle 2 of pair
     * @return Volume-based bond length of particle pair in Angstrom or null if not available
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public double getParticlePairVolumeBasedBondLength(String aParticle1, String aParticle2) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle1 == null || aParticle1.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle1)) {
            throw new IllegalArgumentException("aParticle1 is null/empty.");
        }
        if (aParticle2 == null || aParticle2.isEmpty() || !this.particleToDescriptionMap.containsKey(aParticle2)) {
            throw new IllegalArgumentException("aParticle2 is null/empty.");
        }
        // </editor-fold>
        String tmpParticlePairKey = this.getParticlePairKey(aParticle1, aParticle2);
        if (!this.particlePairToBondLengthMap.containsKey(tmpParticlePairKey)) {
            double tmpVolume1 = Double.parseDouble(this.particleToDescriptionMap.get(aParticle1).getVolume());
            double tmpVolume2 = Double.parseDouble(this.particleToDescriptionMap.get(aParticle2).getVolume());
            // Volume = 4/3*PI*Radius^3
            double tmpR1 = Math.cbrt(ModelDefinitions.FACTOR_3_DIV_4_PI * tmpVolume1);
            double tmpR2 = Math.cbrt(ModelDefinitions.FACTOR_3_DIV_4_PI * tmpVolume2);
            double tmpBondLength = tmpR1 + tmpR2;
            this.particlePairToBondLengthMap.put(tmpParticlePairKey, tmpBondLength);
        }
        return this.particlePairToBondLengthMap.get(tmpParticlePairKey);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Temperatures related methods">
    /**
     * Returns temperature strings sorted ascending
     *
     * @return Returns temperature strings sorted ascending or null if none are defined
     */
    public String[] getTemperatureStrings() {
        return this.temperatureStrings;
    }
    
    /**
     * Returns if temperature is available
     * NOTE: Slow sequential implementation since number of available 
     * temperatures is O(10).
     * 
     * @param aTemperatureRepresentation Temperature representation
     * @return True: Temperature is available, false: Otherwise
     */
    public boolean hasTemperature(String aTemperatureRepresentation) {
        String tmpTemperatureString = this.getTemperatureString(aTemperatureRepresentation);
        for (String tmpSingleTemperature : this.temperatureStrings) {
            if (tmpTemperatureString.equals(tmpSingleTemperature)) {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods for particle duplicate operations">
    /**
     * Returns value item container with value items for particle duplicate
     * display
     *
     * @return Value item container with value items for particle duplicate
     * display
     */
    public ValueItemContainer getParticlesValueItemContainerForDuplicate() {
        String[] tmpNodeNames;
        ValueItemContainer tmpParticlesValueItemContainerForDuplicate = new ValueItemContainer(new StandardParticleUpdateUtils());
        int tmpVerticalPosition = 0;
        String[] tmpParticles = this.getAllParticlesSortedAscending();
        tmpNodeNames = new String[]{ModelMessage.get("ParticleDuplicate.Root")};
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleSetNameValueItem">
        ValueItem tmpParticleSetFilenameValueItem = new ValueItem();
        tmpParticleSetFilenameValueItem.setName("PARTICLE_SET_FILE_NAME");
        tmpParticleSetFilenameValueItem.setDisplayName(ModelMessage.get("ParticleDuplicate.ParticleSetFilenameValueItem.displayName"));
        tmpParticleSetFilenameValueItem.setUpdateNotifier(true);
        tmpParticleSetFilenameValueItem.setBasicType(ValueItemEnumBasicType.SCALAR);

        String tmpCurrentParticleSetFilePathname = Preferences.getInstance().getCurrentParticleSetFilePathname();
        File tmpCurrentParticleSetFile = new File(tmpCurrentParticleSetFilePathname);
        String tmpCurrentParticleSetFileName = tmpCurrentParticleSetFile.getName();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
        tmpMatrix[0][0] = new ValueItemMatrixElement(
                new ValueItemDataTypeFormat(tmpCurrentParticleSetFileName, "[A-Za-z0-9\\_\\(\\)\\-\\.]", "^ParticleSet[A-Za-z0-9\\_\\(\\)\\-\\.][A-Za-z0-9\\_\\(\\)\\-\\.]*\\.txt$")
        );
        tmpParticleSetFilenameValueItem.setMatrix(tmpMatrix);

        tmpParticleSetFilenameValueItem.setDescription(ModelMessage.get("ParticleDuplicate.ParticleSetFilenameValueItem.description"));
        tmpParticleSetFilenameValueItem.setNodeNames(tmpNodeNames);
        tmpParticleSetFilenameValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpParticlesValueItemContainerForDuplicate.addValueItem(tmpParticleSetFilenameValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleDuplicteMatrixValueItem">
        ValueItem tmpParticleDuplicateMatrixValueItem = new ValueItem();
        tmpParticleDuplicateMatrixValueItem.setName("PARTICLE_DUPLICATE_MATRIX");
        tmpParticleDuplicateMatrixValueItem.setUpdateNotifier(true);
        tmpParticleDuplicateMatrixValueItem.setDisplayName(ModelMessage.get("ParticleDuplicate.ParticleTableValueItem.displayName"));
        tmpParticleDuplicateMatrixValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpParticleDuplicateMatrixValueItem.setMatrixColumnNames(
            new String[] {
                ModelMessage.get("JdpdInputFile.parameter.particle"), 
                ModelMessage.get("JdpdInputFile.parameter.particleName"), 
                ModelMessage.get("ParticleDuplicate.NewParticle"), 
                ModelMessage.get("ParticleDuplicate.NewCharge"),
                ModelMessage.get("ParticleDuplicate.NewParticleColor")
            }
        );
        tmpParticleDuplicateMatrixValueItem.setMatrixColumnWidths(
            new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_150,    // Particle
                ModelDefinitions.CELL_WIDTH_TEXT_200,    // Particle_Name
                ModelDefinitions.CELL_WIDTH_TEXT_150,    // New_Particle
                ModelDefinitions.CELL_WIDTH_NUMERIC_140, // New_Charge
                ModelDefinitions.CELL_WIDTH_TEXT_150     // New_Particle_Color
            }
        );
        // <editor-fold defaultstate="collapsed" desc="- Set matrix">
        tmpMatrix = new ValueItemMatrixElement[tmpParticles.length][5];
        ValueItemDataTypeFormat tmpParticleTypeFormat = new ValueItemDataTypeFormat(false);
        ValueItemDataTypeFormat tmpNameTypeFormat = new ValueItemDataTypeFormat(false);
        String tmpAllowedCharacters = ModelDefinitions.PARTICLE_ALLOWED_CHARACTERS_REGEX_STRING;
        String tmpAllowedParticleMatch = ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING;
        String[] tmpForbiddenTexts = tmpParticles;
        ValueItemDataTypeFormat tmpNewParticleTypeFormat = new ValueItemDataTypeFormat(tmpAllowedCharacters, tmpAllowedParticleMatch, tmpForbiddenTexts);
        ValueItemDataTypeFormat tmpChargeTypeFormat = new ValueItemDataTypeFormat("0", 0, -6, 6, true, true);
        ValueItemDataTypeFormat tmpColorTypeFormat= new ValueItemDataTypeFormat(StandardColorEnum.getAllColorRepresentations());
        for (int i = 0; i < tmpParticles.length; i++) {
            StandardParticleDescription tmpParticleDescription = this.getParticleDescription(tmpParticles[i]);
            tmpMatrix[i][0] = new ValueItemMatrixElement(tmpParticleDescription.getParticle(), tmpParticleTypeFormat);
            tmpMatrix[i][1] = new ValueItemMatrixElement(tmpParticleDescription.getName(), tmpNameTypeFormat);
            tmpMatrix[i][2] = new ValueItemMatrixElement("", tmpNewParticleTypeFormat);
            tmpMatrix[i][3] = new ValueItemMatrixElement(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"), tmpChargeTypeFormat);
            tmpMatrix[i][4] = new ValueItemMatrixElement(ModelMessage.get("JdpdInputFile.parameter.defaultColor"), tmpColorTypeFormat);
        }
        tmpParticleDuplicateMatrixValueItem.setMatrix(tmpMatrix);
        // </editor-fold>
        tmpParticleDuplicateMatrixValueItem.setDescription(ModelMessage.get("ParticleDuplicate.ParticleTableValueItem.description"));
        tmpParticleDuplicateMatrixValueItem.setNodeNames(tmpNodeNames);
        tmpParticleDuplicateMatrixValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpParticlesValueItemContainerForDuplicate.addValueItem(tmpParticleDuplicateMatrixValueItem);
        // </editor-fold>
        return tmpParticlesValueItemContainerForDuplicate;
    }

    /**
     * Set duplicate particles if specified
     *
     * @param aParticlesValueItemContainerForDuplicate Value item container for
     * particle duplication
     * @return True: Particles were duplicated, false: Otherwise
     */
    public boolean duplicateParticles(ValueItemContainer aParticlesValueItemContainerForDuplicate) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlesValueItemContainerForDuplicate == null) {
            return false;
        }
        if (!this.isParticleSetVersion("1.0.0.0")) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Read current particle set file">
        LinkedList<String> tmpParticleSetFileLineList = this.fileUtilityMethods.readStringListFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(), null);
        if (tmpParticleSetFileLineList == null || tmpParticleSetFileLineList.size() == 0) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Duplicate particles">
        ValueItem tmpParticleDuplicateMatrixValueItem = aParticlesValueItemContainerForDuplicate.getValueItem("PARTICLE_DUPLICATE_MATRIX");
        if (tmpParticleDuplicateMatrixValueItem == null) {
            return false;
        }
        for (int i = 0; i < tmpParticleDuplicateMatrixValueItem.getMatrixRowCount(); i++) {
            String tmpNewParticle = tmpParticleDuplicateMatrixValueItem.getValue(i, 2).trim();
            if (tmpNewParticle != null && !tmpNewParticle.isEmpty()) {
                String tmpOldParticle = tmpParticleDuplicateMatrixValueItem.getValue(i, 0).trim();
                String tmpNewCharge = null;
                if (!tmpParticleDuplicateMatrixValueItem.hasNumericNullValue(i, 3)) {
                    tmpNewCharge = tmpParticleDuplicateMatrixValueItem.getFormattedValue(i, 3);
                }
                String tmpNewParticleColor = tmpParticleDuplicateMatrixValueItem.getFormattedValue(i, 4);
                tmpParticleSetFileLineList = this.duplicateSingleParticle(
                    tmpParticleSetFileLineList, 
                    tmpOldParticle, 
                    tmpNewParticle, 
                    tmpNewCharge,
                    tmpNewParticleColor
                );
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Save particle set file">
        ValueItem tmpParticleSetFilenameValueItem = aParticlesValueItemContainerForDuplicate.getValueItem("PARTICLE_SET_FILE_NAME");
        String tmpNewParticleSetFilename = tmpParticleSetFilenameValueItem.getValue();
        String tmpNewParticleSetFilePathnameInDpdSourceParticles = Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        String tmpNewParticleSetFilePathnameInCustomParticles = Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        while ((new File(tmpNewParticleSetFilePathnameInCustomParticles)).isFile() || (new File(tmpNewParticleSetFilePathnameInDpdSourceParticles)).isFile()) {
            String tmpNewParticleSetFilenameWithoutEnding = this.fileUtilityMethods.getFilenameWithoutExtension(tmpNewParticleSetFilename);
            tmpNewParticleSetFilename = String.format(ModelMessage.get("ParticleDuplicate.NewParticleFilenameFormat"), tmpNewParticleSetFilenameWithoutEnding) + FileOutputStrings.TEXT_FILE_ENDING;
            tmpNewParticleSetFilePathnameInDpdSourceParticles = Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
            tmpNewParticleSetFilePathnameInCustomParticles = Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        }
        // Write new particle set file
        if (!ModelUtils.writeStringListToFile(tmpParticleSetFileLineList, tmpNewParticleSetFilePathnameInCustomParticles)) {
            return false;
        }
        // </editor-fold>
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods for particle remove operations">
    /**
     * Returns value item container with value items for particle removal
     * display
     *
     * @return Value item container with value items for particle removal
     * display
     */
    public ValueItemContainer getParticlesValueItemContainerForRemove() {
        String[] tmpNodeNames;
        ValueItemContainer tmpParticlesValueItemContainerForRemove = new ValueItemContainer(new StandardParticleUpdateUtils());
        int tmpVerticalPosition = 0;
        String[] tmpParticles = this.getAllParticlesSortedAscending();
        tmpNodeNames = new String[]{ModelMessage.get("ParticleRemove.Root")};
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleSetNameValueItem">
        ValueItem tmpParticleSetFilenameValueItem = new ValueItem();
        tmpParticleSetFilenameValueItem.setName("PARTICLE_SET_FILE_NAME");
        tmpParticleSetFilenameValueItem.setDisplayName(ModelMessage.get("ParticleRemove.ParticleSetFilenameValueItem.displayName"));
        tmpParticleSetFilenameValueItem.setUpdateNotifier(true);
        tmpParticleSetFilenameValueItem.setBasicType(ValueItemEnumBasicType.SCALAR);

        String tmpCurrentParticleSetFilePathname = Preferences.getInstance().getCurrentParticleSetFilePathname();
        File tmpCurrentParticleSetFile = new File(tmpCurrentParticleSetFilePathname);
        String tmpCurrentParticleSetFileName = tmpCurrentParticleSetFile.getName();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
        tmpMatrix[0][0] = new ValueItemMatrixElement(
                new ValueItemDataTypeFormat(tmpCurrentParticleSetFileName, "[A-Za-z0-9\\_\\(\\)\\-\\.]", "^ParticleSet[A-Za-z0-9\\_\\(\\)\\-\\.][A-Za-z0-9\\_\\(\\)\\-\\.]*\\.txt$")
        );
        tmpParticleSetFilenameValueItem.setMatrix(tmpMatrix);

        tmpParticleSetFilenameValueItem.setDescription(ModelMessage.get("ParticleRemove.ParticleSetFilenameValueItem.description"));
        tmpParticleSetFilenameValueItem.setNodeNames(tmpNodeNames);
        tmpParticleSetFilenameValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpParticlesValueItemContainerForRemove.addValueItem(tmpParticleSetFilenameValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleRemoveMatrixValueItem">
        ValueItem tmpParticleRemoveMatrixValueItem = new ValueItem();
        tmpParticleRemoveMatrixValueItem.setName("PARTICLE_REMOVE_MATRIX");
        tmpParticleRemoveMatrixValueItem.setUpdateNotifier(true);
        tmpParticleRemoveMatrixValueItem.setDisplayName(ModelMessage.get("ParticleRemove.ParticleTableValueItem.displayName"));
        tmpParticleRemoveMatrixValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpParticleRemoveMatrixValueItem.setMatrixColumnNames(new String[]{
            ModelMessage.get("JdpdInputFile.parameter.particle"), ModelMessage.get("JdpdInputFile.parameter.particleName"), ModelMessage.get("ParticleRemove.Operation") // Operation
        });
        tmpParticleRemoveMatrixValueItem.setMatrixColumnWidths(new String[]{
            ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle
            ModelDefinitions.CELL_WIDTH_TEXT_200, // Particle_Name
            ModelDefinitions.CELL_WIDTH_TEXT_150 // Operation
        });
        // <editor-fold defaultstate="collapsed" desc="- Set matrix">
        tmpMatrix = new ValueItemMatrixElement[tmpParticles.length][3];
        // <editor-fold defaultstate="collapsed" desc="-- Set type formats">
        ValueItemDataTypeFormat tmpParticleTypeFormat = new ValueItemDataTypeFormat(false);
        ValueItemDataTypeFormat tmpNameTypeFormat = new ValueItemDataTypeFormat(false);
        ValueItemDataTypeFormat tmpOperationTypeFormat = new ValueItemDataTypeFormat(ModelMessage.get("ParticleRemove.Keep"),
                new String[]{ModelMessage.get("ParticleRemove.Keep"), ModelMessage.get("ParticleRemove.Remove")});
        // </editor-fold>
        for (int i = 0; i < tmpParticles.length; i++) {
            StandardParticleDescription tmpParticleDescription = this.getParticleDescription(tmpParticles[i]);
            tmpMatrix[i][0] = new ValueItemMatrixElement(tmpParticleDescription.getParticle(), tmpParticleTypeFormat);
            tmpMatrix[i][1] = new ValueItemMatrixElement(tmpParticleDescription.getName(), tmpNameTypeFormat);
            tmpMatrix[i][2] = new ValueItemMatrixElement(tmpOperationTypeFormat);
        }
        tmpParticleRemoveMatrixValueItem.setMatrix(tmpMatrix);
        // </editor-fold>
        tmpParticleRemoveMatrixValueItem.setDescription(ModelMessage.get("ParticleRemove.ParticleTableValueItem.description"));
        tmpParticleRemoveMatrixValueItem.setNodeNames(tmpNodeNames);
        tmpParticleRemoveMatrixValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpParticlesValueItemContainerForRemove.addValueItem(tmpParticleRemoveMatrixValueItem);
        // </editor-fold>
        return tmpParticlesValueItemContainerForRemove;
    }

    /**
     * Creates particle set file with removed particles
     *
     * @param aParticlesValueItemContainerForRemove Value item container for
     * particle removal
     * @return True: Particles were removed, false: Otherwise
     */
    public boolean removeParticles(ValueItemContainer aParticlesValueItemContainerForRemove) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlesValueItemContainerForRemove == null) {
            return false;
        }
        if (!this.isParticleSetVersion("1.0.0.0")) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Read current particle set file">
        LinkedList<String> tmpParticleSetFileLineList = 
            this.fileUtilityMethods.readStringListFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(), null);
        if (tmpParticleSetFileLineList == null || tmpParticleSetFileLineList.size() == 0) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Remove particles">
        ValueItem tmpParticleRemoveMatrixValueItem = aParticlesValueItemContainerForRemove.getValueItem("PARTICLE_REMOVE_MATRIX");
        if (tmpParticleRemoveMatrixValueItem == null) {
            return false;
        }
        for (int i = 0; i < tmpParticleRemoveMatrixValueItem.getMatrixRowCount(); i++) {
            if (tmpParticleRemoveMatrixValueItem.getValue(i, 2).equals(ModelMessage.get("ParticleRemove.Remove"))) {
                String tmpParticleToBeRemoved = tmpParticleRemoveMatrixValueItem.getValue(i, 0);
                tmpParticleSetFileLineList = this.removeParticle(tmpParticleSetFileLineList, tmpParticleToBeRemoved);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Save particle set file">
        ValueItem tmpParticleSetFilenameValueItem = aParticlesValueItemContainerForRemove.getValueItem("PARTICLE_SET_FILE_NAME");
        String tmpNewParticleSetFilename = tmpParticleSetFilenameValueItem.getValue();
        String tmpNewParticleSetFilePathnameInDpdSourceParticles = Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        String tmpNewParticleSetFilePathnameInCustomParticles = Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;

        while ((new File(tmpNewParticleSetFilePathnameInCustomParticles)).isFile() || (new File(tmpNewParticleSetFilePathnameInDpdSourceParticles)).isFile()) {
            String tmpNewParticleSetFilenameWithoutEnding = this.fileUtilityMethods.getFilenameWithoutExtension(tmpNewParticleSetFilename);
            tmpNewParticleSetFilename = String.format(ModelMessage.get("ParticleRemove.NewParticleFilenameFormat"), tmpNewParticleSetFilenameWithoutEnding) + FileOutputStrings.TEXT_FILE_ENDING;
            tmpNewParticleSetFilePathnameInDpdSourceParticles = Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
            tmpNewParticleSetFilePathnameInCustomParticles = Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        }

        // Write new particle set file
        if (!ModelUtils.writeStringListToFile(tmpParticleSetFileLineList, tmpNewParticleSetFilePathnameInCustomParticles)) {
            return false;
        }

        // </editor-fold>
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods for particle morph operations">
    /**
     * Returns value item container with value items for morph particle
     * display
     *
     * @return Value item container with value items for morph particle
     * display
     */
    public ValueItemContainer getParticlesValueItemContainerForMorph() {
        // TODO: Create code
        String[] tmpNodeNames;
        ValueItemContainer tmpParticlesValueItemContainerForMorph = new ValueItemContainer(new StandardParticleUpdateUtils());
        int tmpVerticalPosition = 0;
        String[] tmpParticles = this.getAllParticlesSortedAscending();
        tmpNodeNames = new String[]{ModelMessage.get("ParticleMorph.Root")};
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleSetNameValueItem">
        ValueItem tmpParticleSetFilenameValueItem = new ValueItem();
        tmpParticleSetFilenameValueItem.setName("PARTICLE_SET_FILE_NAME");
        tmpParticleSetFilenameValueItem.setDisplayName(ModelMessage.get("ParticleMorph.ParticleSetFilenameValueItem.displayName"));
        tmpParticleSetFilenameValueItem.setUpdateNotifier(true);
        tmpParticleSetFilenameValueItem.setBasicType(ValueItemEnumBasicType.SCALAR);

        String tmpCurrentParticleSetFilePathname = Preferences.getInstance().getCurrentParticleSetFilePathname();
        File tmpCurrentParticleSetFile = new File(tmpCurrentParticleSetFilePathname);
        String tmpCurrentParticleSetFileName = tmpCurrentParticleSetFile.getName();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
        tmpMatrix[0][0] = new ValueItemMatrixElement(
                new ValueItemDataTypeFormat(tmpCurrentParticleSetFileName, "[A-Za-z0-9\\_\\(\\)\\-\\.]", "^ParticleSet[A-Za-z0-9\\_\\(\\)\\-\\.][A-Za-z0-9\\_\\(\\)\\-\\.]*\\.txt$")
        );
        tmpParticleSetFilenameValueItem.setMatrix(tmpMatrix);

        tmpParticleSetFilenameValueItem.setDescription(ModelMessage.get("ParticleMorph.ParticleSetFilenameValueItem.description"));
        tmpParticleSetFilenameValueItem.setNodeNames(tmpNodeNames);
        tmpParticleSetFilenameValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpParticlesValueItemContainerForMorph.addValueItem(tmpParticleSetFilenameValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleMorphValueItem">
        ValueItem tmpParticleMorphValueItem = new ValueItem();
        tmpParticleMorphValueItem.setName("PARTICLE_MORPH_VECTOR");
        tmpParticleMorphValueItem.setUpdateNotifier(true);
        tmpParticleMorphValueItem.setDisplayName(ModelMessage.get("ParticleMorph.ParticleMorphValueItem.displayName"));
        tmpParticleMorphValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpParticleMorphValueItem.setMatrixColumnNames(
            new String[] {
                ModelMessage.get("ParticleMorph.ParticleMorphValueItem.source"), 
                ModelMessage.get("ParticleMorph.ParticleMorphValueItem.target"), 
                ModelMessage.get("ParticleMorph.ParticleMorphValueItem.percentage")
            }
        );
        tmpParticleMorphValueItem.setMatrixColumnWidths(
            new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Source
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Target
                ModelDefinitions.CELL_WIDTH_TEXT_150  // Percentage
            }
        );
        // <editor-fold defaultstate="collapsed" desc="- Set matrix">
        tmpMatrix = new ValueItemMatrixElement[1][3];
        // <editor-fold defaultstate="collapsed" desc="-- Set type formats">
        ValueItemDataTypeFormat tmpSourceParticleTypeFormat = new ValueItemDataTypeFormat(tmpParticles);
        ValueItemDataTypeFormat tmpTargetParticleTypeFormat = new ValueItemDataTypeFormat(tmpParticles);
        ValueItemDataTypeFormat tmpPercentageTypeFormat = new ValueItemDataTypeFormat("0.0", 6, 0.0, 100.0);
        // </editor-fold>
        tmpMatrix[0][0] = new ValueItemMatrixElement(tmpSourceParticleTypeFormat);
        tmpMatrix[0][1] = new ValueItemMatrixElement(tmpTargetParticleTypeFormat);
        tmpMatrix[0][2] = new ValueItemMatrixElement(tmpPercentageTypeFormat);
        tmpParticleMorphValueItem.setMatrix(tmpMatrix);
        // </editor-fold>
        tmpParticleMorphValueItem.setDescription(ModelMessage.get("ParticleMorph.ParticleMorphValueItem.description"));
        tmpParticleMorphValueItem.setNodeNames(tmpNodeNames);
        tmpParticleMorphValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpParticlesValueItemContainerForMorph.addValueItem(tmpParticleMorphValueItem);
        // </editor-fold>
        return tmpParticlesValueItemContainerForMorph;
    }

    /**
     * Creates particle set file with morphed particle
     *
     * @param aParticlesValueItemContainerForMorph Value item container for
     * morphing particle
     * @return True: Particle was morphed, false: Otherwise
     */
    public boolean morphParticle(ValueItemContainer aParticlesValueItemContainerForMorph) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlesValueItemContainerForMorph == null) {
            return false;
        }
        if (!this.isParticleSetVersion("1.0.0.0")) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Read current particle set file">
        LinkedList<String> tmpParticleSetFileLineList = 
            this.fileUtilityMethods.readStringListFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(), null);
        if (tmpParticleSetFileLineList == null || tmpParticleSetFileLineList.size() == 0) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Morph particle">
        ValueItem tmpParticleMorphValueItem = aParticlesValueItemContainerForMorph.getValueItem("PARTICLE_MORPH_VECTOR");
        if (tmpParticleMorphValueItem == null) {
            return false;
        }
        String tmpSourceParticle = tmpParticleMorphValueItem.getValue(0, 0);
        String tmpTargetParticle = tmpParticleMorphValueItem.getValue(0, 1);
        double tmpPercentage = tmpParticleMorphValueItem.getValueAsDouble(0, 2);
        LinkedList<String> tmpMorphedParticleSetFileLineList = 
            this.morphParticle(
                tmpParticleSetFileLineList, 
                tmpSourceParticle, 
                tmpTargetParticle, 
                tmpPercentage
            );
        if (tmpMorphedParticleSetFileLineList == null) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Save particle set file">
        ValueItem tmpParticleSetFilenameValueItem = aParticlesValueItemContainerForMorph.getValueItem("PARTICLE_SET_FILE_NAME");
        String tmpNewParticleSetFilename = tmpParticleSetFilenameValueItem.getValue();
        String tmpNewParticleSetFilePathnameInDpdSourceParticles = Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        String tmpNewParticleSetFilePathnameInCustomParticles = Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;

        while ((new File(tmpNewParticleSetFilePathnameInCustomParticles)).isFile() || (new File(tmpNewParticleSetFilePathnameInDpdSourceParticles)).isFile()) {
            String tmpNewParticleSetFilenameWithoutEnding = this.fileUtilityMethods.getFilenameWithoutExtension(tmpNewParticleSetFilename);
            tmpNewParticleSetFilename = String.format(ModelMessage.get("ParticleMorph.NewParticleFilenameFormat"), tmpNewParticleSetFilenameWithoutEnding) + FileOutputStrings.TEXT_FILE_ENDING;
            tmpNewParticleSetFilePathnameInDpdSourceParticles = Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
            tmpNewParticleSetFilePathnameInCustomParticles = Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        }

        // Write morphed particle set file
        if (!ModelUtils.writeStringListToFile(tmpMorphedParticleSetFileLineList, tmpNewParticleSetFilePathnameInCustomParticles)) {
            return false;
        }

        // </editor-fold>
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods for Vmin rescale operations">
    /**
     * Returns value item container with value items for Vmin rescale display
     *
     * @return Value item container with value items for Vmin rescale display
     */
    public ValueItemContainer getValueItemContainerForVminRescale() {
        String[] tmpNodeNames;
        ValueItemContainer tmpValueItemContainerForVminRescale = new ValueItemContainer(new StandardParticleUpdateUtils());
        int tmpVerticalPosition = 0;
        tmpNodeNames = new String[]{ModelMessage.get("VminRescale.Root")};
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleSetNameValueItem">
        ValueItem tmpParticleSetFilenameValueItem = new ValueItem();
        tmpParticleSetFilenameValueItem.setName("PARTICLE_SET_FILE_NAME");
        tmpParticleSetFilenameValueItem.setDisplayName(ModelMessage.get("VminRescale.ParticleSetFilenameValueItem.displayName"));
        tmpParticleSetFilenameValueItem.setUpdateNotifier(true);
        tmpParticleSetFilenameValueItem.setBasicType(ValueItemEnumBasicType.SCALAR);

        String tmpCurrentParticleSetFilePathname = Preferences.getInstance().getCurrentParticleSetFilePathname();
        File tmpCurrentParticleSetFile = new File(tmpCurrentParticleSetFilePathname);
        String tmpCurrentParticleSetFileName = tmpCurrentParticleSetFile.getName();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
        tmpMatrix[0][0] = new ValueItemMatrixElement(
            new ValueItemDataTypeFormat(
                tmpCurrentParticleSetFileName, 
                "[A-Za-z0-9\\_\\(\\)\\-\\.]", 
                "^ParticleSet[A-Za-z0-9\\_\\(\\)\\-\\.][A-Za-z0-9\\_\\(\\)\\-\\.]*\\.txt$"
            )
        );
        tmpParticleSetFilenameValueItem.setMatrix(tmpMatrix);

        tmpParticleSetFilenameValueItem.setDescription(ModelMessage.get("VminRescale.ParticleSetFilenameValueItem.description"));
        tmpParticleSetFilenameValueItem.setNodeNames(tmpNodeNames);
        tmpParticleSetFilenameValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainerForVminRescale.addValueItem(tmpParticleSetFilenameValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpVminRescaleValueItem">
        ValueItem tmpVminRescaleValueItem = new ValueItem();
        tmpVminRescaleValueItem.setName("VMIN_RESCALE");
        tmpVminRescaleValueItem.setUpdateNotifier(true);
        tmpVminRescaleValueItem.setDisplayName(ModelMessage.get("VminRescale.ValueItem.displayName"));
        // 30 A^3 is a good estimate for the molecular volume of water particle H2O
        tmpVminRescaleValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("30.000", 3, 1.0, Double.POSITIVE_INFINITY));        
        tmpVminRescaleValueItem.setDescription(ModelMessage.get("VminRescale.ValueItem.description"));
        tmpVminRescaleValueItem.setNodeNames(tmpNodeNames);
        tmpVminRescaleValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainerForVminRescale.addValueItem(tmpVminRescaleValueItem);
        // </editor-fold>
        return tmpValueItemContainerForVminRescale;
    }

    /**
     * Performs molecule volumes rescale with new Vmin
     *
     * @param aVminRescaleValueItemContainer Value item container for
     * Vmin rescale
     * @return True: Molecular volumes were rescaled with new Vmin, false: Otherwise
     */
    public boolean rescaleVmin(ValueItemContainer aVminRescaleValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aVminRescaleValueItemContainer == null) {
            return false;
        }
        if (!this.isParticleSetVersion("1.0.0.0")) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Read current particle set file">
        LinkedList<String> tmpParticleSetFileLineList = 
            this.fileUtilityMethods.readStringListFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(), null);
        if (tmpParticleSetFileLineList == null || tmpParticleSetFileLineList.size() == 0) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Rescale Vmin">
        ValueItem tmpVminRescaleValueItem = aVminRescaleValueItemContainer.getValueItem("VMIN_RESCALE");
        if (tmpVminRescaleValueItem == null) {
            return false;
        }
        double tmpNewVmin = tmpVminRescaleValueItem.getValueAsDouble();
        tmpParticleSetFileLineList = this.scaleToNewVmin(tmpParticleSetFileLineList, tmpNewVmin);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Save particle set file">
        ValueItem tmpParticleSetFilenameValueItem = aVminRescaleValueItemContainer.getValueItem("PARTICLE_SET_FILE_NAME");
        String tmpNewParticleSetFilename = tmpParticleSetFilenameValueItem.getValue();
        String tmpNewParticleSetFilePathnameInDpdSourceParticles = 
            Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        String tmpNewParticleSetFilePathnameInCustomParticles = 
            Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;

        while ((new File(tmpNewParticleSetFilePathnameInCustomParticles)).isFile() || (new File(tmpNewParticleSetFilePathnameInDpdSourceParticles)).isFile()) {
            String tmpNewParticleSetFilenameWithoutEnding = this.fileUtilityMethods.getFilenameWithoutExtension(tmpNewParticleSetFilename);
            tmpNewParticleSetFilename = 
                String.format(
                    ModelMessage.get("VminRescale.NewParticleFilenameFormat"), 
                    tmpNewParticleSetFilenameWithoutEnding
                ) + FileOutputStrings.TEXT_FILE_ENDING;
            tmpNewParticleSetFilePathnameInDpdSourceParticles = 
                Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
            tmpNewParticleSetFilePathnameInCustomParticles = 
                Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        }

        // Write new particle set file
        if (!ModelUtils.writeStringListToFile(tmpParticleSetFileLineList, tmpNewParticleSetFilePathnameInCustomParticles)) {
            return false;
        }
        // </editor-fold>
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods for repulsions rescale operations">
    /**
     * Returns value item container with value items for repulsions rescale display
     *
     * @return Value item container with value items for repulsions rescale display
     */
    public ValueItemContainer getValueItemContainerForRepulsionsRescale() {
        String[] tmpNodeNames;
        ValueItemContainer tmpValueItemContainerForRepulsionsRescale = new ValueItemContainer(new StandardParticleUpdateUtils());
        int tmpVerticalPosition = 0;
        tmpNodeNames = new String[]{ModelMessage.get("RepulsionsRescale.Root")};
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleSetNameValueItem">
        ValueItem tmpParticleSetFilenameValueItem = new ValueItem();
        tmpParticleSetFilenameValueItem.setName("PARTICLE_SET_FILE_NAME");
        tmpParticleSetFilenameValueItem.setDisplayName(ModelMessage.get("RepulsionsRescale.ParticleSetFilenameValueItem.displayName"));
        tmpParticleSetFilenameValueItem.setUpdateNotifier(true);
        tmpParticleSetFilenameValueItem.setBasicType(ValueItemEnumBasicType.SCALAR);

        String tmpCurrentParticleSetFilePathname = Preferences.getInstance().getCurrentParticleSetFilePathname();
        File tmpCurrentParticleSetFile = new File(tmpCurrentParticleSetFilePathname);
        String tmpCurrentParticleSetFileName = tmpCurrentParticleSetFile.getName();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
        tmpMatrix[0][0] = new ValueItemMatrixElement(
            new ValueItemDataTypeFormat(
                tmpCurrentParticleSetFileName, 
                "[A-Za-z0-9\\_\\(\\)\\-\\.]", 
                "^ParticleSet[A-Za-z0-9\\_\\(\\)\\-\\.][A-Za-z0-9\\_\\(\\)\\-\\.]*\\.txt$"
            )
        );
        tmpParticleSetFilenameValueItem.setMatrix(tmpMatrix);

        tmpParticleSetFilenameValueItem.setDescription(ModelMessage.get("RepulsionsRescale.ParticleSetFilenameValueItem.description"));
        tmpParticleSetFilenameValueItem.setNodeNames(tmpNodeNames);
        tmpParticleSetFilenameValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainerForRepulsionsRescale.addValueItem(tmpParticleSetFilenameValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpTemperatureFactorValueItem">
        ValueItem tmpTemperatureFactorValueItem = new ValueItem();
        tmpTemperatureFactorValueItem.setName("TEMPERATURE_FACTOR");
        tmpTemperatureFactorValueItem.setUpdateNotifier(true);
        tmpTemperatureFactorValueItem.setDisplayName(ModelMessage.get("RepulsionsRescale.TemperaturFactorValueItem.displayName"));
        tmpTemperatureFactorValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(1.0/12.0), 8, 0.0, Double.POSITIVE_INFINITY));        
        tmpTemperatureFactorValueItem.setDescription(ModelMessage.get("RepulsionsRescale.TemperaturFactorValueItem.description"));
        tmpTemperatureFactorValueItem.setNodeNames(tmpNodeNames);
        tmpTemperatureFactorValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainerForRepulsionsRescale.addValueItem(tmpTemperatureFactorValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpLowerRepulsionRangeValueItem">
        ValueItem tmpLowerRepulsionRangeValueItem = new ValueItem();
        tmpLowerRepulsionRangeValueItem.setName("LOWER_REPULSION_RANGE");
        tmpLowerRepulsionRangeValueItem.setUpdateNotifier(true);
        tmpLowerRepulsionRangeValueItem.setDisplayName(ModelMessage.get("RepulsionsRescale.LowerRepulsionRangeValueItem.displayName"));
        tmpLowerRepulsionRangeValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("20.00", 2, 0.0, Double.POSITIVE_INFINITY));        
        tmpLowerRepulsionRangeValueItem.setDescription(ModelMessage.get("RepulsionsRescale.LowerRepulsionRangeValueItem.description"));
        tmpLowerRepulsionRangeValueItem.setNodeNames(tmpNodeNames);
        tmpLowerRepulsionRangeValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainerForRepulsionsRescale.addValueItem(tmpLowerRepulsionRangeValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpChargeOffsetValueItem">
        ValueItem tmpChargeOffsetValueItem = new ValueItem();
        tmpChargeOffsetValueItem.setName("CHARGE_OFFSET");
        tmpChargeOffsetValueItem.setUpdateNotifier(true);
        tmpChargeOffsetValueItem.setDisplayName(ModelMessage.get("RepulsionsRescale.ChargeOffsetValueItem.displayName"));
        tmpChargeOffsetValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("1.00", 2, 0.0, Double.POSITIVE_INFINITY));        
        tmpChargeOffsetValueItem.setDescription(ModelMessage.get("RepulsionsRescale.ChargeOffsetValueItem.description"));
        tmpChargeOffsetValueItem.setNodeNames(tmpNodeNames);
        tmpChargeOffsetValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainerForRepulsionsRescale.addValueItem(tmpChargeOffsetValueItem);
        // </editor-fold>
        return tmpValueItemContainerForRepulsionsRescale;
    }

    /**
     * Performs repulsions rescale with new settings
     *
     * @param aRepulsionsRescaleValueItemContainer Value item container for
     * repulsions rescale
     * @param isIndividualScaling True: Every temperature is individually 
     * scaled, false: All temperatures are scaled with global scaling factor
     * @return True: Repulsions were rescaled with new settings, false: Otherwise
     */
    public boolean rescaleRepulsions(ValueItemContainer aRepulsionsRescaleValueItemContainer, boolean isIndividualScaling) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aRepulsionsRescaleValueItemContainer == null) {
            return false;
        }
        if (!this.isParticleSetVersion("1.0.0.0")) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Read current particle set file">
        LinkedList<String> tmpParticleSetFileLineList = 
            this.fileUtilityMethods.readStringListFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(), null);
        if (tmpParticleSetFileLineList == null || tmpParticleSetFileLineList.size() == 0) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Rescale repulsions">
        ValueItem tmpTemperatureFactorValueItem = aRepulsionsRescaleValueItemContainer.getValueItem("TEMPERATURE_FACTOR");
        if (tmpTemperatureFactorValueItem == null) {
            return false;
        }
        double tmpTempFactor = tmpTemperatureFactorValueItem.getValueAsDouble();
        ValueItem tmpLowerRepulsionRangeValueItem = aRepulsionsRescaleValueItemContainer.getValueItem("LOWER_REPULSION_RANGE");
        if (tmpLowerRepulsionRangeValueItem == null) {
            return false;
        }
        double tmpLowerRepulsionRange = tmpLowerRepulsionRangeValueItem.getValueAsDouble();
        ValueItem tmpChargeOffsetValueItem = aRepulsionsRescaleValueItemContainer.getValueItem("CHARGE_OFFSET");
        if (tmpChargeOffsetValueItem == null) {
            return false;
        }
        double tmpChargeOffset = tmpChargeOffsetValueItem.getValueAsDouble();
        if (isIndividualScaling) {
            tmpParticleSetFileLineList = this.rescaleRepulsionsWithIndividualScalingFactors(tmpParticleSetFileLineList, tmpTempFactor, tmpLowerRepulsionRange, tmpChargeOffset);
        } else {
            tmpParticleSetFileLineList = this.rescaleRepulsionsWithGlobalScalingFactor(tmpParticleSetFileLineList, tmpTempFactor, tmpLowerRepulsionRange, tmpChargeOffset);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Save particle set file">
        ValueItem tmpParticleSetFilenameValueItem = aRepulsionsRescaleValueItemContainer.getValueItem("PARTICLE_SET_FILE_NAME");
        String tmpNewParticleSetFilename = tmpParticleSetFilenameValueItem.getValue();
        String tmpNewParticleSetFilePathnameInDpdSourceParticles = 
            Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        String tmpNewParticleSetFilePathnameInCustomParticles = 
            Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;

        while ((new File(tmpNewParticleSetFilePathnameInCustomParticles)).isFile() || (new File(tmpNewParticleSetFilePathnameInDpdSourceParticles)).isFile()) {
            String tmpNewParticleSetFilenameWithoutEnding = this.fileUtilityMethods.getFilenameWithoutExtension(tmpNewParticleSetFilename);
            tmpNewParticleSetFilename = 
                String.format(
                    ModelMessage.get("RepulsionsRescale.NewParticleFilenameFormat"), 
                    tmpNewParticleSetFilenameWithoutEnding
                ) + FileOutputStrings.TEXT_FILE_ENDING;
            tmpNewParticleSetFilePathnameInDpdSourceParticles = 
                Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
            tmpNewParticleSetFilePathnameInCustomParticles = 
                Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        }

        // Write new particle set file
        if (!ModelUtils.writeStringListToFile(tmpParticleSetFileLineList, tmpNewParticleSetFilePathnameInCustomParticles)) {
            return false;
        }
        // </editor-fold>
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods for incrementing probe particles">
    /**
     * Returns value item container with value items for probe-particle
     * increment display
     *
     * @return Value item container with value items for probe-particle
     * increment display or null if no probe particles are available
     */
    public ValueItemContainer getValueItemContainerForProbeParticleIncrement() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.hasProteinBackboneProbeParticles()) {
            return null;
        }
        // </editor-fold>
        String[] tmpNodeNames;
        ValueItemContainer tmpValueItemContainerForProbeParticleIncrement = new ValueItemContainer(new StandardParticleUpdateUtils());
        int tmpVerticalPosition = 0;
        tmpNodeNames = new String[]{ModelMessage.get("ProteinBackboneProbeParticleIncrementation.Root")};
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleSetNameValueItem">
        ValueItem tmpParticleSetFilenameValueItem = new ValueItem();
        tmpParticleSetFilenameValueItem.setName("PARTICLE_SET_FILE_NAME");
        tmpParticleSetFilenameValueItem.setDisplayName(ModelMessage.get("ProteinBackboneProbeParticleIncrementation.ParticleSetFilenameValueItem.displayName"));
        tmpParticleSetFilenameValueItem.setUpdateNotifier(true);
        tmpParticleSetFilenameValueItem.setBasicType(ValueItemEnumBasicType.SCALAR);

        String tmpCurrentParticleSetFilePathname = Preferences.getInstance().getCurrentParticleSetFilePathname();
        File tmpCurrentParticleSetFile = new File(tmpCurrentParticleSetFilePathname);
        String tmpCurrentParticleSetFileName = tmpCurrentParticleSetFile.getName();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
        tmpMatrix[0][0] = 
            new ValueItemMatrixElement(
                new ValueItemDataTypeFormat(
                    tmpCurrentParticleSetFileName, 
                    "[A-Za-z0-9\\_\\(\\)\\-\\.]", 
                    "^ParticleSet[A-Za-z0-9\\_\\(\\)\\-\\.][A-Za-z0-9\\_\\(\\)\\-\\.]*\\.txt$"
                )
            );
        tmpParticleSetFilenameValueItem.setMatrix(tmpMatrix);

        tmpParticleSetFilenameValueItem.setDescription(ModelMessage.get("ProteinBackboneProbeParticleIncrementation.ParticleSetFilenameValueItem.description"));
        tmpParticleSetFilenameValueItem.setNodeNames(tmpNodeNames);
        tmpParticleSetFilenameValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainerForProbeParticleIncrement.addValueItem(tmpParticleSetFilenameValueItem);
        // </editor-fold>
        return tmpValueItemContainerForProbeParticleIncrement;
    }

    /**
     * Increment probe particles if available
     *
     * @param aValueItemContainerForProbeParticleIncrement Value item container
     * for probe-particle increment operation
     * @return True: Probe particles were incremented, false: Otherwise
     */
    public boolean incrementProbeParticles(ValueItemContainer aValueItemContainerForProbeParticleIncrement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainerForProbeParticleIncrement == null) {
            return false;
        }
        if (!this.isParticleSetVersion("1.0.0.0")) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Read current particle set file">
        LinkedList<String> tmpParticleSetFileLineList = 
            this.fileUtilityMethods.readStringListFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(), null);
        if (tmpParticleSetFileLineList == null || tmpParticleSetFileLineList.size() == 0) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Increment protein backbone probe particles">
        String[] tmpCurrentProteinBackboneProbeParticleArray = this.getAllSortedProteinBackboneProbeParticles();
        if (tmpCurrentProteinBackboneProbeParticleArray == null) {
            return false;
        }
        HashMap<String, String> tmpCurrentIncrementProbeParticleMap = new HashMap<>(tmpCurrentProteinBackboneProbeParticleArray.length);
        for (String tmpCurrentProteinBackboneProbeParticle : tmpCurrentProteinBackboneProbeParticleArray) {
            Matcher tmpProteinBackboneProbeParticleMatcher = 
                ModelDefinitions.PARTICLE_PROTEIN_BACKBONE_PROBE_NUMBER_CAPTURING_PATTERN.matcher(tmpCurrentProteinBackboneProbeParticle);
            if (tmpProteinBackboneProbeParticleMatcher.matches()) {
                String tmpParticlePart = tmpProteinBackboneProbeParticleMatcher.group(1);
                String tmpPD = tmpProteinBackboneProbeParticleMatcher.group(2);
                String tmpNumberString = tmpProteinBackboneProbeParticleMatcher.group(3);
                String tmpIncrementedNumberString = String.valueOf(Integer.valueOf(tmpNumberString) + 1);
                String tmpIncrementedProteinBackboneProbeParticle = tmpParticlePart + tmpPD + tmpIncrementedNumberString;
                if (tmpIncrementedProteinBackboneProbeParticle.matches(ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING) && !this.hasParticle(tmpIncrementedProteinBackboneProbeParticle)) {
                    tmpCurrentIncrementProbeParticleMap.put(tmpCurrentProteinBackboneProbeParticle, tmpIncrementedProteinBackboneProbeParticle);
                }
            }
        }
        if (tmpCurrentIncrementProbeParticleMap.isEmpty()) {
            return false;
        }
        for (String tmpCurrentProteinBackboneProbeParticle : tmpCurrentIncrementProbeParticleMap.keySet()) {
            String tmpIncrementedProteinBackboneProbeParticle = tmpCurrentIncrementProbeParticleMap.get(tmpCurrentProteinBackboneProbeParticle);
            // Use color of current protein backbone probe particle
            String tmpIncrementedProteinBackboneProbeParticleColor = null;
            tmpParticleSetFileLineList = this.duplicateSingleParticle(
                tmpParticleSetFileLineList, 
                tmpCurrentProteinBackboneProbeParticle, 
                tmpIncrementedProteinBackboneProbeParticle, 
                tmpIncrementedProteinBackboneProbeParticleColor
            );
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Save particle set file">
        ValueItem tmpParticleSetFilenameValueItem = aValueItemContainerForProbeParticleIncrement.getValueItem("PARTICLE_SET_FILE_NAME");
        String tmpNewParticleSetFilename = tmpParticleSetFilenameValueItem.getValue();
        String tmpNewParticleSetFilePathnameInDpdSourceParticles = 
            Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        String tmpNewParticleSetFilePathnameInCustomParticles = 
            Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        while ((new File(tmpNewParticleSetFilePathnameInCustomParticles)).isFile() || (new File(tmpNewParticleSetFilePathnameInDpdSourceParticles)).isFile()) {
            String tmpNewParticleSetFilenameWithoutEnding = this.fileUtilityMethods.getFilenameWithoutExtension(tmpNewParticleSetFilename);
            tmpNewParticleSetFilename = 
                String.format(
                    ModelMessage.get("ProteinBackboneProbeParticleIncrementation.NewParticleFilenameFormat"), 
                    tmpNewParticleSetFilenameWithoutEnding
                ) + FileOutputStrings.TEXT_FILE_ENDING;
            tmpNewParticleSetFilePathnameInDpdSourceParticles = Preferences.getInstance().getDpdSourceParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
            tmpNewParticleSetFilePathnameInCustomParticles = Preferences.getInstance().getCustomParticlesPath() + File.separatorChar + tmpNewParticleSetFilename;
        }
        // Write new particle set file
        if (!ModelUtils.writeStringListToFile(tmpParticleSetFileLineList, tmpNewParticleSetFilePathnameInCustomParticles)) {
            return false;
        }
        // </editor-fold>
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods for particle edit operations">
    // NOTE: Since particle data may contain megabytes all change operations process directly on the data so NO setParticlesValueItemContainer() is necessary
    /**
     * Returns value item container with value items for particle edit display
     * 
     * @return Particles value item container
     */
    public ValueItemContainer getParticlesValueItemContainerForEdit() {
        ValueItem tmpValueItem;
        String[] tmpNodeNames;
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(new StandardParticleUpdateUtils());
        int tmpVerticalPosition = 0;
        tmpNodeNames = new String[]{ModelMessage.get("ParticleEdit.Root")};
        // <editor-fold defaultstate="collapsed" desc="ParticleTable">
        tmpValueItem = this.getParticleTableValueItemWithDefinedParticles(this.getAllParticlesSortedAscending());
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setMatrixClonedBeforeChange(true);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="MFSIM_SOURCE_INTERACTIONS_MATRIX">
        tmpValueItem = this.getInteractionsMatrixValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        return tmpValueItemContainer;
    }

    /**
     * Returns particle matrix value item with particles of tmpParticles
     *
     * @param tmpParticles Particles for particle matrix value item
     * @return Particle matrix value item with particles of tmpParticles or null
     * if particle matrix could not be created
     */
    public ValueItem getParticleTableValueItemWithDefinedParticles(String[] tmpParticles) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (tmpParticles == null || tmpParticles.length == 0) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpParticleTableValueItem = new ValueItem();
        tmpParticleTableValueItem.setName("ParticleTable");
        tmpParticleTableValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.ParticleTable"));
        tmpParticleTableValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpParticleTableValueItem.setUpdateNotifier(true);
        tmpParticleTableValueItem.setMatrixColumnNames(new String[]{
            ModelMessage.get("JdpdInputFile.parameter.particle"),
            ModelMessage.get("JdpdInputFile.parameter.particleName"),
            ModelMessage.get("JdpdInputFile.parameter.massDpd"),
            ModelMessage.get("JdpdInputFile.parameter.charge"),
            ModelMessage.get("JdpdInputFile.parameter.massGmol"),
            ModelMessage.get("JdpdInputFile.parameter.volumeA3"),
            ModelMessage.get("JdpdInputFile.parameter.graphicsRadius"),
            ModelMessage.get("JdpdInputFile.parameter.standardColor")
        });
        tmpParticleTableValueItem.setMatrixColumnWidths(new String[]{
            ModelDefinitions.CELL_WIDTH_TEXT_100,   // Particle
            ModelDefinitions.CELL_WIDTH_TEXT_150,   // Particle_Name
            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Mass_[DPD]
            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Charge
            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Mass_[g/mol]
            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Volume_[A^3]
            ModelDefinitions.CELL_WIDTH_TEXT_100,   // Graphics-Radius
            ModelDefinitions.CELL_WIDTH_TEXT_100}); // Standard-Color
        tmpParticleTableValueItem.setMatrixOutputOmitColumns(new boolean[]{
            false, // Particle
            true,  // Particle_Name
            true,  // Mass_[DPD]
            false, // Charge
            false, // Mass_[g/mol]
            true,  // Volume_[A^3]
            true,  // Graphics-Radius
            true   // Standard-Color
        });
        // <editor-fold defaultstate="collapsed" desc="Set matrix">
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpParticles.length][8];
        // <editor-fold defaultstate="collapsed" desc="- Set type formats">
        ValueItemDataTypeFormat tmpParticleTypeFormat = new ValueItemDataTypeFormat(false);
        ValueItemDataTypeFormat tmpNameTypeFormat = new ValueItemDataTypeFormat(false);
        ValueItemDataTypeFormat tmpMolWeightInDpdUnitsTypeFormat = new ValueItemDataTypeFormat("0.01", 2, 0.01, 10000);
        ValueItemDataTypeFormat tmpChargeTypeFormat = new ValueItemDataTypeFormat("0.00", 2, -5, 5);
        ValueItemDataTypeFormat tmpMassGmolTypeFormat = new ValueItemDataTypeFormat("0.01", 2, 0.01, java.lang.Double.MAX_VALUE);
        ValueItemDataTypeFormat tmpVolumeTypeFormat = new ValueItemDataTypeFormat("0.01", 2, 0.01, java.lang.Double.MAX_VALUE);
        ValueItemDataTypeFormat tmpGraphicsRadiusTypeFormat = new ValueItemDataTypeFormat("0.01", 2, 0.01, 10);
        // </editor-fold>
        for (int i = 0; i < tmpParticles.length; i++) {
            StandardParticleDescription tmpParticleDescription = this.getParticleDescription(tmpParticles[i]);
            tmpMatrix[i][0] = new ValueItemMatrixElement(tmpParticleDescription.getParticle(), tmpParticleTypeFormat);
            tmpMatrix[i][1] = new ValueItemMatrixElement(tmpParticleDescription.getName(), tmpNameTypeFormat);
            tmpMatrix[i][2] = new ValueItemMatrixElement(tmpParticleDescription.getMolWeightInDpdUnits(), tmpMolWeightInDpdUnitsTypeFormat);
            tmpMatrix[i][3] = new ValueItemMatrixElement(tmpParticleDescription.getCharge(), tmpChargeTypeFormat);
            tmpMatrix[i][4] = new ValueItemMatrixElement(tmpParticleDescription.getMolWeightInGMol(), tmpMassGmolTypeFormat);
            tmpMatrix[i][5] = new ValueItemMatrixElement(tmpParticleDescription.getVolume(), tmpVolumeTypeFormat);
            tmpMatrix[i][6] = new ValueItemMatrixElement(tmpParticleDescription.getGraphicsRadius(), tmpGraphicsRadiusTypeFormat);
            tmpMatrix[i][7] = new ValueItemMatrixElement(tmpParticleDescription.getStandardColor(), new ValueItemDataTypeFormat(tmpParticleDescription.getStandardColor(),
                    StandardColorEnum.getAllColorRepresentations()));
        }
        tmpParticleTableValueItem.setMatrix(tmpMatrix);
        // </editor-fold>
        tmpParticleTableValueItem.setMatrixMaximumNumberOfRows(1000);
        tmpParticleTableValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.ParticleTable"));
        return tmpParticleTableValueItem;
    }

    /**
     * Returns interactions matrix value item with (sorted) first particle and
     * default temperature
     *
     * @return Interactions matrix value item with (sorted) first particle and
     * default temperature or null if no value item can be created
     */
    public ValueItem getInteractionsMatrixValueItem() {
        return this.getInteractionsMatrixValueItem(this.getAllParticlesSortedAscending()[0], this.getTemperatureStrings()[0]);
    }

    /**
     * Returns interactions matrix value item for specified first particle and
     * temperature
     *
     * @param aFirstParticle First particle
     * @param aTemperature Temperature
     * @return Interactions matrix value item for specified first particle and
     * temperature or null if no value item can be created
     */
    public ValueItem getInteractionsMatrixValueItem(String aFirstParticle, String aTemperature) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFirstParticle == null || aFirstParticle.isEmpty()) {
            return null;
        }
        if (aTemperature == null || aTemperature.isEmpty()) {
            return null;
        }
        if (!this.particleToDescriptionMap.containsKey(aFirstParticle)) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setName("MFSIM_SOURCE_INTERACTIONS_MATRIX");
        tmpValueItem.setDisplayName(ModelMessage.get("JdpdInputFile.valueItem.displayName.MFSIM_SOURCE_INTERACTIONS_MATRIX"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("ParticleInteractionsMatrix.FirstParticle"), ModelMessage.get("ParticleInteractionsMatrix.SecondParticle"),
            ModelMessage.get("ParticleInteractionsMatrix.Temperature"), ModelMessage.get("ParticleInteractionsMatrix.Interaction")});
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_100, // First_Particle
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Second_Particle
            ModelDefinitions.CELL_WIDTH_TEXT_150, // Temperature
            ModelDefinitions.CELL_WIDTH_TEXT_150});

        // <editor-fold defaultstate="collapsed" desc="Set matrix">
        String[] tmpParticles = this.getAllParticlesSortedAscending();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpParticles.length][4];

        // <editor-fold defaultstate="collapsed" desc="- Set type formats">
        // Parameter false: Non-exclusive selection texts (since only first row selection will be allowed, see below)
        ValueItemDataTypeFormat tmpFirstParticleTypeFormat = new ValueItemDataTypeFormat(aFirstParticle, tmpParticles, false);
        // Second particle is not editable and of type TEXT (with no defaults etc., only set internally)
        ValueItemDataTypeFormat tmpSecondParticleTypeFormat = new ValueItemDataTypeFormat(false);
        ValueItemDataTypeFormat tmpTemperatureTypeFormat = new ValueItemDataTypeFormat(aTemperature, this.getTemperatureStrings());
        // Type for non-bonding interaction a(ij) is NUMERIC_NULL
        ValueItemDataTypeFormat tmpInteractionTypeFormat = new ValueItemDataTypeFormat("0.000000", 6, -1000, 1000, true, true);

        // </editor-fold>
        for (int i = 0; i < tmpParticles.length; i++) {
            String tmpFirstParticle = aFirstParticle;
            String tmpSecondParticle = tmpParticles[i];
            String tmpInteraction = ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString");
            if (this.hasInteraction(tmpFirstParticle, tmpSecondParticle, aTemperature)) {
                tmpInteraction = this.getInteraction(tmpFirstParticle, tmpSecondParticle, aTemperature);
            }
            if (i == 0) {
                // First particle and temperature are only editable on first row
                tmpMatrix[i][0] = new ValueItemMatrixElement(tmpFirstParticle, tmpFirstParticleTypeFormat);
                tmpMatrix[i][2] = new ValueItemMatrixElement(aTemperature, tmpTemperatureTypeFormat);
            } else {
                // First particle and temperature are NOT editable on second and following rows
                tmpMatrix[i][0] = new ValueItemMatrixElement(tmpFirstParticle, tmpFirstParticleTypeFormat.getClone());
                tmpMatrix[i][0].getTypeFormat().setEditable(false);
                tmpMatrix[i][2] = new ValueItemMatrixElement(aTemperature, tmpTemperatureTypeFormat.getClone());
                tmpMatrix[i][2].getTypeFormat().setEditable(false);
            }
            tmpMatrix[i][1] = new ValueItemMatrixElement(tmpSecondParticle, tmpSecondParticleTypeFormat);
            tmpMatrix[i][3] = new ValueItemMatrixElement(tmpInteraction, tmpInteractionTypeFormat);
        }
        tmpValueItem.setMatrix(tmpMatrix);

        // </editor-fold>
        tmpValueItem.setMatrixClonedBeforeChange(true);
        tmpValueItem.setDescription(ModelMessage.get("JdpdInputFile.valueItem.description.MFSIM_SOURCE_INTERACTIONS_MATRIX"));
        return tmpValueItem;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Removal related methods">
    /**
     * Removes particle with all data. NOTE: If number of particles is 1 then
     * nothing happens (i.e. one particle MUST remain). NOTE: This method
     * iterates over ALL interaction data and may be EXTREMELY SLOW.
     *
     * @param aParticle Particle
     * @return True: Particle data changed, false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public boolean removeParticleWithAllData(String aParticle) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || aParticle.isEmpty()) {
            return false;
        }
        if (!this.particleToDescriptionMap.containsKey(aParticle)) {
            return false;
        }
        if (this.particleToDescriptionMap.size() <= 1) {
            return false;
        }
        if (!this.isParticleSetVersion("1.0.0.0")) {
            return false;
        }
        // </editor-fold>
        this.particleToDescriptionMap.remove(aParticle);
        // Particle may be removed: Clear particle pair to bond length map
        this.particlePairToBondLengthMap.clear();
        // Change in data occurred: Set change detection flag to true!
        this.hasChanged = true;
        // <editor-fold defaultstate="collapsed" desc="Remove interactions data of removed particle (SLOW sequential removal)">
        Set<String> tmpParticlePairTemperatureKeySet = this.particlePairTemperatureToInteractionMap.keySet();
        Iterator<String> tmpIterator = tmpParticlePairTemperatureKeySet.iterator();
        // IMPORTANT: Use ModelDefinitions.GENERAL_SEPARATOR to make particle in key unique
        String tmpParticleInKeyToBeRemoved = ModelDefinitions.GENERAL_SEPARATOR + aParticle.toUpperCase(Locale.ENGLISH).trim() + ModelDefinitions.GENERAL_SEPARATOR;
        while (tmpIterator.hasNext()) {
            String tmpParticlePairTemperatureKey = tmpIterator.next();
            if (tmpParticlePairTemperatureKey.contains(tmpParticleInKeyToBeRemoved)) {
                this.particlePairTemperatureToInteractionMap.remove(tmpParticlePairTemperatureKey);
            }
        }

        // </editor-fold>
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Change detection related methods">
    /**
     * Sets change detection flag to false
     */
    public void initChangeDetection() {
        this.hasChanged = false;
    }

    /**
     * Returns if change in ed since last call of initChangeDetection()
     *
     * @return True: Change in data occurred since last call of
     * initChangeDetection(), false: Otherwise
     */
    public boolean hasChanged() {
        return this.hasChanged;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    //
    // <editor-fold defaultstate="collapsed" desc="- Particle related operations">
    /**
     * Duplicates specified old particle
     *
     * @param aParticleSetFileLineList Particle set file line
     * list
     * @param anOldParticle Old particle for duplication (not allowed to be
     * null/empty)
     * @param aNewParticle New particle (not allowed to be null/empty)
     * @param aNewCharge New charge (may be null/empty)
     * @param aNewParticleColor New particle color
     * @return New particle set file line list or null if new list could not be
     * created
     */
    private LinkedList<String> duplicateSingleParticle(
        LinkedList<String> aParticleSetFileLineList, 
        String anOldParticle, 
        String aNewParticle, 
        String aNewCharge,
        String aNewParticleColor
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleSetFileLineList == null || aParticleSetFileLineList.isEmpty()) {
            return null;
        }
        if (anOldParticle == null || anOldParticle.isEmpty()) {
            return null;
        }
        if (aNewParticle == null || aNewParticle.isEmpty()) {
            return null;
        }
        if (anOldParticle.trim().equalsIgnoreCase(aNewParticle.trim())) {
            return aParticleSetFileLineList;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="No charge-change defined">
        if (aNewCharge == null || aNewCharge.isEmpty()) {
            return this.duplicateSingleParticle(aParticleSetFileLineList, anOldParticle, aNewParticle, aNewParticleColor);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Charge-change is defined">
        HashMap<String, Integer> tmpParticleToChargeMap = this.getParticleToChargeMap(aParticleSetFileLineList);
        if (tmpParticleToChargeMap.get(anOldParticle) == Integer.valueOf(aNewCharge)) {
            // Charge is defined but not changed
            return this.duplicateSingleParticle(aParticleSetFileLineList, anOldParticle, aNewParticle, aNewParticleColor);
        } else if (tmpParticleToChargeMap.get(anOldParticle) != 0) {
            // A charge can not be changed for a charged particle
            return aParticleSetFileLineList;
        }
        
        // Duplicate particle
        LinkedList<String> tmpDuplicatedParticleSetFileLineList = 
            this.duplicateSingleParticle(aParticleSetFileLineList, anOldParticle, aNewParticle, aNewParticleColor);

        // Correct duplicated particle for charge
        LinkedList<String> tmpNewParticleSetFileLineList = new LinkedList<>();

        String tmpParticleDescriptionStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
        String tmpParticleDescriptionEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
        boolean tmpIsDescriptionSectionStart = false;
        boolean tmpIsDescriptionsSectionEnd = false;

        String tmpParticleInteractionsStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        String tmpParticleInteractionsEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        boolean tmpIsInteractionsSectionStart = false;
        boolean tmpIsInteractionsSectionEnd = false;

        String tmpWaterParticle = this.getDefaultWaterParticle();
        String[] tmpTemperatureStrings = this.getTemperatureStrings();
        double[] tmpMinInteractionValueForTemperatures = this.getMinInteractionValueForTemperatures();
        double[] tmpMaxInteractionValueForTemperatures = this.getMaxInteractionValueForTemperatures();
        
        for (String tmpCurrentLine : tmpDuplicatedParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionEndLine)) {
                tmpIsDescriptionsSectionEnd = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }

            if (tmpIsDescriptionSectionStart && 
                !tmpIsDescriptionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE)
            ) {
                // <editor-fold defaultstate="collapsed" desc="Correct Description section">
                if (this.stringUtilityMethods.getFirstToken(tmpCurrentLine).equals(aNewParticle)) {
                    // Set new charge
                    String tmpNewDescriptionLine = this.stringUtilityMethods.replaceToken(tmpCurrentLine, 3, aNewCharge);
                    tmpNewParticleSetFileLineList.add(tmpNewDescriptionLine);
                } else {
                    tmpNewParticleSetFileLineList.add(tmpCurrentLine);
                }
                // </editor-fold>
            } else if (
                tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE)
            ) {
                // <editor-fold defaultstate="collapsed" desc="Correct Interaction section a(ij) for i != j (diagonal a(ii) is already correctly set)">
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(this.stringUtilityMethods.getFirstToken(tmpCurrentLine));
                if (tmpParticles.length > 1 && !tmpParticles[0].equals(tmpParticles[1])) {
                    if (tmpParticles[0].equals(aNewParticle) || tmpParticles[1].equals(aNewParticle)) {
                        // Determine other particle
                        String tmpOtherParticle = tmpParticles[0];
                        if (tmpParticles[0].equals(aNewParticle)) {
                            tmpOtherParticle = tmpParticles[1];
                        }
                        String tmpNewInteractionLine = tmpCurrentLine;
                        if (tmpOtherParticle.equals(tmpWaterParticle)) {
                            // <editor-fold defaultstate="collapsed" desc="Other particle is water">
                            for (int i = 0; i < tmpTemperatureStrings.length; i++) {
                                double tmpA_OldParticle_Water = Double.valueOf(this.getInteraction(anOldParticle, tmpWaterParticle, tmpTemperatureStrings[i]));
                                double tmpA_Water_Water = Double.valueOf(this.getInteraction(tmpWaterParticle, tmpWaterParticle, tmpTemperatureStrings[i]));
                                double tmpDelta = Math.abs(tmpA_OldParticle_Water - tmpA_Water_Water);
                                double tmpA_NewParticle_Water = tmpA_OldParticle_Water - tmpDelta;
                                tmpNewInteractionLine = 
                                    this.stringUtilityMethods.replaceToken(
                                        tmpNewInteractionLine, 
                                        i + 1, 
                                        this.stringUtilityMethods.formatDoubleValue(tmpA_NewParticle_Water, 6)
                                    );
                            }
                            tmpNewParticleSetFileLineList.add(tmpNewInteractionLine);
                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="Other particle is NOT water">
                            if (tmpParticleToChargeMap.get(tmpOtherParticle) == 0) {
                                // <editor-fold defaultstate="collapsed" desc="Other particle is NOT charged">
                                for (int i = 0; i < tmpTemperatureStrings.length; i++) {
                                    double tmpA_OtherParticle_Water = Double.valueOf(this.getInteraction(tmpOtherParticle, tmpWaterParticle, tmpTemperatureStrings[i]));
                                    double tmpA_Water_Water = Double.valueOf(this.getInteraction(tmpWaterParticle, tmpWaterParticle, tmpTemperatureStrings[i]));
                                    double tmpDelta = Math.abs(tmpA_OtherParticle_Water - tmpA_Water_Water);
                                    double tmpA_OldParticle_OtherParticle = Double.valueOf(this.getInteraction(anOldParticle, tmpOtherParticle, tmpTemperatureStrings[i]));;
                                    double tmpA_NewParticle_OtherParticle = tmpA_OldParticle_OtherParticle + tmpDelta;
                                    if (tmpA_OtherParticle_Water < tmpA_Water_Water) {
                                        tmpA_NewParticle_OtherParticle = tmpA_OldParticle_OtherParticle - tmpDelta;
                                    }
                                    tmpNewInteractionLine = 
                                        this.stringUtilityMethods.replaceToken(
                                            tmpNewInteractionLine, 
                                            i + 1, 
                                            this.stringUtilityMethods.formatDoubleValue(tmpA_NewParticle_OtherParticle, 6)
                                        );
                                }
                                // </editor-fold>
                            } else {
                                // <editor-fold defaultstate="collapsed" desc="Other particle is charged">
                                if (tmpParticleToChargeMap.get(tmpOtherParticle) * Integer.valueOf(aNewCharge) > 0) {
                                    // <editor-fold defaultstate="collapsed" desc="Equal charges">
                                    for (int i = 0; i < tmpTemperatureStrings.length; i++) {
                                        tmpNewInteractionLine = 
                                            this.stringUtilityMethods.replaceToken(
                                                tmpNewInteractionLine, 
                                                i + 1, 
                                                this.stringUtilityMethods.formatDoubleValue(tmpMaxInteractionValueForTemperatures[i], 6)
                                            );
                                    }
                                    // </editor-fold>
                                } else {
                                    // <editor-fold defaultstate="collapsed" desc="Different charges">
                                    for (int i = 0; i < tmpTemperatureStrings.length; i++) {
                                        tmpNewInteractionLine = 
                                            this.stringUtilityMethods.replaceToken(
                                                tmpNewInteractionLine, 
                                                i + 1, 
                                                this.stringUtilityMethods.formatDoubleValue(tmpMinInteractionValueForTemperatures[i], 6)
                                            );
                                    }
                                    // </editor-fold>
                                }
                                // </editor-fold>
                            }
                            // </editor-fold>
                        }
                        tmpNewParticleSetFileLineList.add(tmpNewInteractionLine);
                    } else {
                        tmpNewParticleSetFileLineList.add(tmpCurrentLine);
                    }
                } else {
                    tmpNewParticleSetFileLineList.add(tmpCurrentLine);
                }
                // </editor-fold>
            } else {
                tmpNewParticleSetFileLineList.add(tmpCurrentLine);
            }

            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionStartLine)) {
                tmpIsDescriptionSectionStart = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        return tmpNewParticleSetFileLineList;
        // </editor-fold>
    }

    /**
     * Duplicates specified old particle
     *
     * @param aParticleSetFileLineList Particle set file line list
     * @param anOldParticle Old particle for duplication (not allowed to be
     * null/empty)
     * @param aNewParticle New particle (not allowed to be null/empty)
     * @param aNewParticleColor New particle color (may be null/empty)
     * @return New particle set file line list or null if new list could not be
     * created
     */
    private LinkedList<String> duplicateSingleParticle(
        LinkedList<String> aParticleSetFileLineList, 
        String anOldParticle, 
        String aNewParticle,
        String aNewParticleColor
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleSetFileLineList == null || aParticleSetFileLineList.isEmpty()) {
            return null;
        }
        if (anOldParticle == null || anOldParticle.isEmpty()) {
            return null;
        }
        if (aNewParticle == null || aNewParticle.isEmpty()) {
            return null;
        }
        if (anOldParticle.trim().equalsIgnoreCase(aNewParticle.trim())) {
            return aParticleSetFileLineList;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Duplicate particles">
        // <editor-fold defaultstate="collapsed" desc="- Initial definitions">
        LinkedList<String> tmpNewParticleSetFileLineList = new LinkedList<>();

        String tmpParticleDescriptionStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
        String tmpParticleDescriptionEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
        boolean tmpIsDescriptionSectionStart = false;
        boolean tmpIsDescriptionsSectionEnd = false;

        String tmpParticleInteractionsStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        String tmpParticleInteractionsEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        boolean tmpIsInteractionsSectionStart = false;
        boolean tmpIsInteractionsSectionEnd = false;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Duplicate">
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            tmpNewParticleSetFileLineList.add(tmpCurrentLine);

            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionStartLine)) {
                tmpIsDescriptionSectionStart = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionEndLine)) {
                tmpIsDescriptionsSectionEnd = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }

            // Description section
            if (tmpIsDescriptionSectionStart && 
                !tmpIsDescriptionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE)
            ) {
                if (this.stringUtilityMethods.getFirstToken(tmpCurrentLine).equals(anOldParticle)) {
                    String tmpNewDescriptionLine = this.stringUtilityMethods.replaceToken(tmpCurrentLine, 0, aNewParticle);
                    if (aNewParticleColor != null && !aNewParticleColor.isEmpty()) {
                        tmpNewDescriptionLine = this.stringUtilityMethods.replaceToken(tmpNewDescriptionLine, 7, aNewParticleColor.toLowerCase(Locale.ENGLISH));
                    }
                    tmpNewParticleSetFileLineList.add(tmpNewDescriptionLine);
                }
            }

            // Interaction section
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE)
            ) {
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(this.stringUtilityMethods.getFirstToken(tmpCurrentLine));
                if (tmpParticles.length > 1) {
                    if (tmpParticles[0].equals(anOldParticle) && tmpParticles[1].equals(anOldParticle)) {
                        String tmpTwoMatchLine = this.stringUtilityMethods.replaceToken(tmpCurrentLine, 0, aNewParticle + "_" + aNewParticle);
                        tmpNewParticleSetFileLineList.add(tmpTwoMatchLine);
                        String tmpNewInteractionLine = this.stringUtilityMethods.replaceToken(tmpCurrentLine, 0, aNewParticle + "_" + anOldParticle);
                        tmpNewParticleSetFileLineList.add(tmpNewInteractionLine);
                    } else if (tmpParticles[0].equals(anOldParticle) || tmpParticles[1].equals(anOldParticle)) {
                        String tmpOneMatchLine;
                        if (tmpParticles[0].equals(anOldParticle)) {
                            tmpOneMatchLine = this.stringUtilityMethods.replaceToken(tmpCurrentLine, 0, aNewParticle + "_" + tmpParticles[1]);
                        } else {
                            tmpOneMatchLine = this.stringUtilityMethods.replaceToken(tmpCurrentLine, 0, tmpParticles[0] + "_" + aNewParticle);
                        }
                        tmpNewParticleSetFileLineList.add(tmpOneMatchLine);
                    }
                }
            }
        }
        // </editor-fold>
        // </editor-fold>
        return tmpNewParticleSetFileLineList;
    }
    
    /**
     * Removes specified particle
     *
     * @param aParticleSetFileLineList Particle set file line
     * list
     * @param aParticleToBeRemoved Particle to be removed (not allowed to be
     * null/empty)
     * @return New particle set file line list or null if new list could not be
     * created
     */
    private LinkedList<String> removeParticle(LinkedList<String> aParticleSetFileLineList, String aParticleToBeRemoved) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleSetFileLineList == null || aParticleSetFileLineList.size() == 0) {
            return null;
        }
        if (aParticleToBeRemoved == null || aParticleToBeRemoved.isEmpty()) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Delete particle">
        // <editor-fold defaultstate="collapsed" desc="- Initial definitions">
        LinkedList<String> tmpNewParticleSetFileLineList = new LinkedList<String>();

        String tmpParticleDescriptionStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
        String tmpParticleDescriptionEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
        boolean tmpIsDescriptionSectionStart = false;
        boolean tmpIsDescriptionsSectionEnd = false;

        String tmpParticleInteractionsStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        String tmpParticleInteractionsEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        boolean tmpIsInteractionsSectionStart = false;
        boolean tmpIsInteractionsSectionEnd = false;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Delete">
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            tmpNewParticleSetFileLineList.add(tmpCurrentLine);

            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionStartLine)) {
                tmpIsDescriptionSectionStart = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionEndLine)) {
                tmpIsDescriptionsSectionEnd = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }

            // Description section
            if (tmpIsDescriptionSectionStart && !tmpIsDescriptionsSectionEnd && !tmpCurrentLine.trim().isEmpty()) {
                if (this.stringUtilityMethods.getFirstToken(tmpCurrentLine).equals(aParticleToBeRemoved)) {
                    tmpNewParticleSetFileLineList.removeLast();
                }
            }

            // Interaction section
            if (tmpIsInteractionsSectionStart && !tmpIsInteractionsSectionEnd && !tmpCurrentLine.trim().isEmpty()) {
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(this.stringUtilityMethods.getFirstToken(tmpCurrentLine));
                if (tmpParticles.length > 1) {
                    if (tmpParticles[0].equals(aParticleToBeRemoved) || tmpParticles[1].equals(aParticleToBeRemoved)) {
                        tmpNewParticleSetFileLineList.removeLast();
                    }
                }
            }
        }
        // </editor-fold>
        // </editor-fold>
        return tmpNewParticleSetFileLineList;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Scaling related operations">
    /**
     * Scales all molecular volumes to new minimum volume
     * 
     * @param aParticleSetFileLineList Particle set file line
     * list
     * @param aNewVmin New minimum volume
     * @return New particle set file line list or null if new list could not be
     * created
     */
    private LinkedList<String> scaleToNewVmin(LinkedList<String> aParticleSetFileLineList, double aNewVmin) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleSetFileLineList == null || aParticleSetFileLineList.size() == 0) {
            return null;
        }
        if (aNewVmin <= 0.0) {
            return null;
        }
        // </editor-fold>
        LinkedList<String> tmpNewParticleSetFileLineList = new LinkedList<String>();

        String tmpParticleDescriptionStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
        String tmpParticleDescriptionEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);

        int tmpIndexOfV = 5;
        
        // Get current Vmin
        boolean tmpIsDescriptionSectionStart = false;
        boolean tmpIsDescriptionsSectionEnd = false;
        double tmpCurrentVmin = Double.MAX_VALUE;
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionStartLine)) {
                tmpIsDescriptionSectionStart = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionEndLine)) {
                tmpIsDescriptionsSectionEnd = true;
            }
            
            // Description section
            if (tmpIsDescriptionSectionStart && 
                !tmpIsDescriptionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() && 
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionStartLine)
            ) {
                double tmpCurrentV = Double.valueOf(this.stringUtilityMethods.getToken(tmpCurrentLine, tmpIndexOfV));
                if (tmpCurrentV < tmpCurrentVmin) {
                    tmpCurrentVmin = tmpCurrentV;
                }
            }
            if (tmpIsDescriptionsSectionEnd) {
                break;
            }
        }
        // Scale to new Vmin
        tmpIsDescriptionSectionStart = false;
        tmpIsDescriptionsSectionEnd = false;
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionStartLine)) {
                tmpIsDescriptionSectionStart = true;
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionEndLine)) {
                tmpIsDescriptionsSectionEnd = true;
            }
            
            // Description section
            if (tmpIsDescriptionSectionStart && 
                !tmpIsDescriptionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() && 
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleDescriptionStartLine)
            ) {
                double tmpCurrentV = Double.valueOf(this.stringUtilityMethods.getToken(tmpCurrentLine, tmpIndexOfV));
                double tmpNewV = aNewVmin * tmpCurrentV/tmpCurrentVmin;
                tmpNewParticleSetFileLineList.add(
                    this.stringUtilityMethods.replaceToken(
                        tmpCurrentLine, 
                        tmpIndexOfV, 
                        this.stringUtilityMethods.formatDoubleValue(tmpNewV, 3)
                    )
                );
            } else {
                tmpNewParticleSetFileLineList.add(tmpCurrentLine);
            }
        }
        return tmpNewParticleSetFileLineList;
    }
    
    /**
     * Rescales repulsions according to settings with an individual (!) scaling
     * factor for each (!) temperatures.
     * 
     * @param aParticleSetFileLineList Particle set file line list
     * @param aTempFactor Temperature factor (see code)
     * @param aLowerRepulsionRange Lower repulsion range (see code)
     * @param aChargeOffset Charge offset (see code)
     * @return New particle set file line list or null if new list could not be
     * created
     */
    private LinkedList<String> rescaleRepulsionsWithIndividualScalingFactors(
        LinkedList<String> aParticleSetFileLineList, 
        double aTempFactor, 
        double aLowerRepulsionRange, 
        double aChargeOffset
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleSetFileLineList == null || aParticleSetFileLineList.isEmpty()) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Rescale repulsions">
        String tmpParticleInteractionsStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        String tmpParticleInteractionsEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        
        HashMap<String, Integer> tmpParticleToChargeMap = this.getParticleToChargeMap(aParticleSetFileLineList);
        double[] tmpTemperatures = this.getTemperatures();
        double[] tmpAiiOld = this.getAiiForTemperatures();

        double[] tmpAiiNew = new double[tmpAiiOld.length];
        double[] tmpAiiShift = new double[tmpAiiOld.length];
        for (int i = 0; i < tmpAiiOld.length; i++) {
            tmpAiiNew[i] = tmpTemperatures[i] / 12.0;
            tmpAiiShift[i] = tmpAiiNew[i] - tmpAiiOld[i];
        }

        double[] tmpAijShiftedMin = new double[tmpTemperatures.length];
        Arrays.fill(tmpAijShiftedMin, Double.MAX_VALUE);
        double[] tmpAijShiftedMax = new double[tmpTemperatures.length];
        Arrays.fill(tmpAijShiftedMax, -Double.MAX_VALUE);
        
        // Set a(ii) and shift a(ij) WITHOUT charged/charged pairs
        LinkedList<String> tmpShiftedParticleSetFileLineList = new LinkedList<>();
        boolean tmpIsInteractionsSectionStart = false;
        boolean tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpCurrentLine);
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(tmpItems[0]);
                if (tmpParticleToChargeMap.get(tmpParticles[0]) == 0 || tmpParticleToChargeMap.get(tmpParticles[1]) == 0) {
                    if (tmpParticles[0].equals(tmpParticles[1])) {
                        // Set a(ii)
                        for (int i = 1; i < tmpItems.length; i++) {
                            tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAiiNew[i - 1], 6);
                            if (tmpAiiNew[i - 1] < tmpAijShiftedMin[i - 1]) {
                                tmpAijShiftedMin[i - 1] = tmpAiiNew[i - 1];
                            }
                            if (tmpAiiNew[i - 1] > tmpAijShiftedMax[i - 1]) {
                                tmpAijShiftedMax[i - 1] = tmpAiiNew[i - 1];
                            }
                        }
                    } else {
                        // Shift a(ij)
                        for (int i = 1; i < tmpItems.length; i++) {
                            double tmpSingleShiftedAij = Double.valueOf(tmpItems[i]) + tmpAiiShift[i - 1];
                            if (tmpSingleShiftedAij < tmpAijShiftedMin[i - 1]) {
                                tmpAijShiftedMin[i - 1] = tmpSingleShiftedAij;
                            }
                            if (tmpSingleShiftedAij > tmpAijShiftedMax[i - 1]) {
                                tmpAijShiftedMax[i - 1] = tmpSingleShiftedAij;
                            }
                            tmpItems[i] = String.valueOf(tmpSingleShiftedAij);
                        }
                    }
                    tmpShiftedParticleSetFileLineList.add(this.stringUtilityMethods.concatenateStringsWithSpace(tmpItems));
                } else {
                    tmpShiftedParticleSetFileLineList.add(tmpCurrentLine);
                }
            } else {
                tmpShiftedParticleSetFileLineList.add(tmpCurrentLine);
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        
        // Scale a(ij) WITHOUT charged/charged pairs
        LinkedList<String> tmpScaledParticleSetFileLineList = new LinkedList<>();

        double[] tmpAijScaledMin = new double[tmpTemperatures.length];
        Arrays.fill(tmpAijScaledMin, Double.MAX_VALUE);
        double[] tmpAijScaledMax = new double[tmpTemperatures.length];
        Arrays.fill(tmpAijScaledMax, -Double.MAX_VALUE);

        double[] tmpScalingFactors = new double[tmpAijShiftedMin.length];
        for (int i = 0; i < tmpScalingFactors.length; i++) {
            tmpScalingFactors[i] = aLowerRepulsionRange / (tmpAiiNew[i] - tmpAijShiftedMin[i]);
        }

        tmpIsInteractionsSectionStart = false;
        tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : tmpShiftedParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpCurrentLine);
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(tmpItems[0]);
                if (tmpParticleToChargeMap.get(tmpParticles[0]) == 0 || tmpParticleToChargeMap.get(tmpParticles[1]) == 0) {
                    if (!tmpParticles[0].equals(tmpParticles[1])) {
                        // Scale a(ij)
                        for (int i = 1; i < tmpItems.length; i++) {
                            double tmpAijShifted = Double.valueOf(tmpItems[i]);
                            double tmpAijNew;
                            if (tmpAijShifted < tmpAiiNew[i - 1]) {
                                tmpAijNew = tmpAiiNew[i - 1] - tmpScalingFactors[i - 1] * (tmpAiiNew[i - 1] - tmpAijShifted);
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijNew, 6);
                            } else {
                                tmpAijNew = tmpAiiNew[i - 1] + tmpScalingFactors[i - 1] * (tmpAijShifted - tmpAiiNew[i - 1]);
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijNew, 6);
                            }
                            if (tmpAijNew < tmpAijScaledMin[i - 1]) {
                                tmpAijScaledMin[i - 1] = tmpAijNew;
                            }
                            if (tmpAijNew > tmpAijScaledMax[i - 1]) {
                                tmpAijScaledMax[i - 1] = tmpAijNew;
                            }
                        }
                        tmpScaledParticleSetFileLineList.add(this.stringUtilityMethods.concatenateStringsWithSpace(tmpItems));
                    } else {
                        tmpScaledParticleSetFileLineList.add(tmpCurrentLine);
                    }
                } else {
                    tmpScaledParticleSetFileLineList.add(tmpCurrentLine);
                }
            } else {
                tmpScaledParticleSetFileLineList.add(tmpCurrentLine);
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        
        // Set a(ii) and a(ij) for charged/charged pairs ONLY
        LinkedList<String> tmpNewParticleSetFileLineList = new LinkedList<>();

        tmpNewParticleSetFileLineList.add("# --------------------------------------------------------------------------------");
        tmpNewParticleSetFileLineList.add("# Rescale settings:");
        tmpNewParticleSetFileLineList.add("# Temperature factor     = " + String.valueOf(aTempFactor));
        tmpNewParticleSetFileLineList.add("# Lower repulsion range  = " + String.valueOf(aLowerRepulsionRange));
        tmpNewParticleSetFileLineList.add("# Charge offset          = " + String.valueOf(aChargeOffset));
        tmpNewParticleSetFileLineList.add("#");
        for (int i = 0; i < tmpTemperatures.length; i++) {
            tmpNewParticleSetFileLineList.add("# Temperature         = " + String.valueOf(tmpTemperatures[i]));
            tmpNewParticleSetFileLineList.add("# - a(ii) old         = " + String.valueOf(tmpAiiOld[i]));
            tmpNewParticleSetFileLineList.add("# - a(ii) new         = " + String.valueOf(tmpAiiNew[i]));
            tmpNewParticleSetFileLineList.add("# - a(ii) shift       = " + String.valueOf(tmpAiiShift[i]));
            tmpNewParticleSetFileLineList.add("# - a(ij) shifted min = " + String.valueOf(tmpAijShiftedMin[i]));
            tmpNewParticleSetFileLineList.add("# - a(ij) shifted max = " + String.valueOf(tmpAijShiftedMax[i]));
            tmpNewParticleSetFileLineList.add("# - Scaling factor    = " + String.valueOf(tmpScalingFactors[i]));
            tmpNewParticleSetFileLineList.add("# - a(ij) scaled  min = " + String.valueOf(tmpAijScaledMin[i]));
            tmpNewParticleSetFileLineList.add("# - a(ij) scaled  max = " + String.valueOf(tmpAijScaledMax[i]));
            tmpNewParticleSetFileLineList.add("#");
        }
        tmpNewParticleSetFileLineList.add("# --------------------------------------------------------------------------------");
        tmpNewParticleSetFileLineList.add("#");
        
        tmpIsInteractionsSectionStart = false;
        tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : tmpScaledParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpCurrentLine);
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(tmpItems[0]);
                if (tmpParticleToChargeMap.get(tmpParticles[0]) != 0 && 
                    tmpParticleToChargeMap.get(tmpParticles[1]) != 0
                ) {
                    if (tmpParticles[0].equals(tmpParticles[1])) {
                        for (int i = 1; i < tmpItems.length; i++) {
                            tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAiiNew[i - 1], 6);
                        }
                    } else {
                        if (tmpParticleToChargeMap.get(tmpParticles[0]) * tmpParticleToChargeMap.get(tmpParticles[1]) > 0) {
                            // Equal sign of charges
                            for (int i = 1; i < tmpItems.length; i++) {
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijScaledMax[i - 1] + aChargeOffset, 6);
                            }
                        } else {
                            // Unequal sign of charges
                            for (int i = 1; i < tmpItems.length; i++) {
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijScaledMin[i - 1] - aChargeOffset, 6);
                            }
                        }
                    }
                    tmpNewParticleSetFileLineList.add(this.stringUtilityMethods.concatenateStringsWithSpace(tmpItems));
                } else {
                    tmpNewParticleSetFileLineList.add(tmpCurrentLine);
                }
            } else {
                tmpNewParticleSetFileLineList.add(tmpCurrentLine);
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        // </editor-fold>
        return tmpNewParticleSetFileLineList;
    }

    /**
     * Rescales repulsions according to settings with a single global (!) 
     * scaling factor for all (!) temperatures.
     * 
     * @param aParticleSetFileLineList Particle set file line list
     * @param aTempFactor Temperature factor (see code)
     * @param aLowerRepulsionRange Lower repulsion range (see code)
     * @param aChargeOffset Charge offset (see code)
     * @return New particle set file line list or null if new list could not be
     * created
     */
    private LinkedList<String> rescaleRepulsionsWithGlobalScalingFactor(
        LinkedList<String> aParticleSetFileLineList, 
        double aTempFactor, 
        double aLowerRepulsionRange, 
        double aChargeOffset
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleSetFileLineList == null || aParticleSetFileLineList.isEmpty()) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Rescale repulsions">
        String tmpParticleInteractionsStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        String tmpParticleInteractionsEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        
        HashMap<String, Integer> tmpParticleToChargeMap = this.getParticleToChargeMap(aParticleSetFileLineList);
        double[] tmpTemperatures = this.getTemperatures();
        double[] tmpAiiOld = this.getAiiForTemperatures();

        double[] tmpAiiNew = new double[tmpAiiOld.length];
        double[] tmpAiiShift = new double[tmpAiiOld.length];
        for (int i = 0; i < tmpAiiOld.length; i++) {
            tmpAiiNew[i] = tmpTemperatures[i] / 12.0;
            tmpAiiShift[i] = tmpAiiNew[i] - tmpAiiOld[i];
        }

        double tmpAijShiftedMin = Double.MAX_VALUE;
        double tmpAijShiftedMax = -Double.MAX_VALUE;
        
        // Set a(ii) and shift a(ij) WITHOUT charged/charged pairs
        LinkedList<String> tmpShiftedParticleSetFileLineList = new LinkedList<>();
        boolean tmpIsInteractionsSectionStart = false;
        boolean tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpCurrentLine);
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(tmpItems[0]);
                if (tmpParticleToChargeMap.get(tmpParticles[0]) == 0 || tmpParticleToChargeMap.get(tmpParticles[1]) == 0) {
                    if (tmpParticles[0].equals(tmpParticles[1])) {
                        // Set a(ii)
                        for (int i = 1; i < tmpItems.length; i++) {
                            tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAiiNew[i - 1], 6);
                            if (tmpAiiNew[i - 1] < tmpAijShiftedMin) {
                                tmpAijShiftedMin = tmpAiiNew[i - 1];
                            }
                            if (tmpAiiNew[i - 1] > tmpAijShiftedMax) {
                                tmpAijShiftedMax = tmpAiiNew[i - 1];
                            }
                        }
                    } else {
                        // Shift a(ij)
                        for (int i = 1; i < tmpItems.length; i++) {
                            double tmpSingleShiftedAij = Double.valueOf(tmpItems[i]) + tmpAiiShift[i - 1];
                            if (tmpSingleShiftedAij < tmpAijShiftedMin) {
                                tmpAijShiftedMin = tmpSingleShiftedAij;
                            }
                            if (tmpSingleShiftedAij > tmpAijShiftedMax) {
                                tmpAijShiftedMax = tmpSingleShiftedAij;
                            }
                            tmpItems[i] = String.valueOf(tmpSingleShiftedAij);
                        }
                    }
                    tmpShiftedParticleSetFileLineList.add(this.stringUtilityMethods.concatenateStringsWithSpace(tmpItems));
                } else {
                    tmpShiftedParticleSetFileLineList.add(tmpCurrentLine);
                }
            } else {
                tmpShiftedParticleSetFileLineList.add(tmpCurrentLine);
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        
        // Scale a(ij) WITHOUT charged/charged pairs
        LinkedList<String> tmpScaledParticleSetFileLineList = new LinkedList<>();

        double[] tmpAijScaledMin = new double[tmpTemperatures.length];
        Arrays.fill(tmpAijScaledMin, Double.MAX_VALUE);
        double[] tmpAijScaledMax = new double[tmpTemperatures.length];
        Arrays.fill(tmpAijScaledMax, -Double.MAX_VALUE);

        double[] tmpScalingFactors = new double[tmpTemperatures.length];
        for (int i = 0; i < tmpScalingFactors.length; i++) {
            tmpScalingFactors[i] = aLowerRepulsionRange / (tmpAiiNew[i] - tmpAijShiftedMin);
        }

        tmpIsInteractionsSectionStart = false;
        tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : tmpShiftedParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpCurrentLine);
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(tmpItems[0]);
                if (tmpParticleToChargeMap.get(tmpParticles[0]) == 0 || tmpParticleToChargeMap.get(tmpParticles[1]) == 0) {
                    if (!tmpParticles[0].equals(tmpParticles[1])) {
                        // Scale a(ij)
                        for (int i = 1; i < tmpItems.length; i++) {
                            double tmpAijShifted = Double.valueOf(tmpItems[i]);
                            double tmpAijNew;
                            if (tmpAijShifted < tmpAiiNew[i - 1]) {
                                tmpAijNew = tmpAiiNew[i - 1] - tmpScalingFactors[i - 1] * (tmpAiiNew[i - 1] - tmpAijShifted);
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijNew, 6);
                            } else {
                                tmpAijNew = tmpAiiNew[i - 1] + tmpScalingFactors[i - 1] * (tmpAijShifted - tmpAiiNew[i - 1]);
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijNew, 6);
                            }
                            if (tmpAijNew < tmpAijScaledMin[i - 1]) {
                                tmpAijScaledMin[i - 1] = tmpAijNew;
                            }
                            if (tmpAijNew > tmpAijScaledMax[i - 1]) {
                                tmpAijScaledMax[i - 1] = tmpAijNew;
                            }
                        }
                        tmpScaledParticleSetFileLineList.add(this.stringUtilityMethods.concatenateStringsWithSpace(tmpItems));
                    } else {
                        tmpScaledParticleSetFileLineList.add(tmpCurrentLine);
                    }
                } else {
                    tmpScaledParticleSetFileLineList.add(tmpCurrentLine);
                }
            } else {
                tmpScaledParticleSetFileLineList.add(tmpCurrentLine);
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        
        // Set a(ii) and a(ij) for charged/charged pairs ONLY
        LinkedList<String> tmpNewParticleSetFileLineList = new LinkedList<>();

        tmpNewParticleSetFileLineList.add("# --------------------------------------------------------------------------------");
        tmpNewParticleSetFileLineList.add("# Rescale settings:");
        tmpNewParticleSetFileLineList.add("# Temperature factor     = " + String.valueOf(aTempFactor));
        tmpNewParticleSetFileLineList.add("# Lower repulsion range  = " + String.valueOf(aLowerRepulsionRange));
        tmpNewParticleSetFileLineList.add("# Charge offset          = " + String.valueOf(aChargeOffset));
        tmpNewParticleSetFileLineList.add("#");
        tmpNewParticleSetFileLineList.add("# - a(ij) shifted min = " + String.valueOf(tmpAijShiftedMin));
        tmpNewParticleSetFileLineList.add("# - a(ij) shifted max = " + String.valueOf(tmpAijShiftedMax));
        for (int i = 0; i < tmpTemperatures.length; i++) {
            tmpNewParticleSetFileLineList.add("# Temperature         = " + String.valueOf(tmpTemperatures[i]));
            tmpNewParticleSetFileLineList.add("# - a(ii) old         = " + String.valueOf(tmpAiiOld[i]));
            tmpNewParticleSetFileLineList.add("# - a(ii) new         = " + String.valueOf(tmpAiiNew[i]));
            tmpNewParticleSetFileLineList.add("# - a(ii) shift       = " + String.valueOf(tmpAiiShift[i]));
            tmpNewParticleSetFileLineList.add("# - Scaling factor    = " + String.valueOf(tmpScalingFactors[i]));
            tmpNewParticleSetFileLineList.add("# - a(ij) scaled  min = " + String.valueOf(tmpAijScaledMin[i]));
            tmpNewParticleSetFileLineList.add("# - a(ij) scaled  max = " + String.valueOf(tmpAijScaledMax[i]));
            tmpNewParticleSetFileLineList.add("#");
        }
        tmpNewParticleSetFileLineList.add("# --------------------------------------------------------------------------------");
        tmpNewParticleSetFileLineList.add("#");
        
        tmpIsInteractionsSectionStart = false;
        tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : tmpScaledParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                String[] tmpItems = this.stringUtilityMethods.splitAndTrim(tmpCurrentLine);
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(tmpItems[0]);
                if (tmpParticleToChargeMap.get(tmpParticles[0]) != 0 && 
                    tmpParticleToChargeMap.get(tmpParticles[1]) != 0
                ) {
                    if (tmpParticles[0].equals(tmpParticles[1])) {
                        for (int i = 1; i < tmpItems.length; i++) {
                            tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAiiNew[i - 1], 6);
                        }
                    } else {
                        if (tmpParticleToChargeMap.get(tmpParticles[0]) * tmpParticleToChargeMap.get(tmpParticles[1]) > 0) {
                            // Equal sign of charges
                            for (int i = 1; i < tmpItems.length; i++) {
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijScaledMax[i - 1] + aChargeOffset, 6);
                            }
                        } else {
                            // Unequal sign of charges
                            for (int i = 1; i < tmpItems.length; i++) {
                                tmpItems[i] = this.stringUtilityMethods.formatDoubleValue(tmpAijScaledMin[i - 1] - aChargeOffset, 6);
                            }
                        }
                    }
                    tmpNewParticleSetFileLineList.add(this.stringUtilityMethods.concatenateStringsWithSpace(tmpItems));
                } else {
                    tmpNewParticleSetFileLineList.add(tmpCurrentLine);
                }
            } else {
                tmpNewParticleSetFileLineList.add(tmpCurrentLine);
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        // </editor-fold>
        return tmpNewParticleSetFileLineList;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle morph operations">
    /**
     * Morphs a(ij) repulsions from source particle to target particle according
     * to percentage.
     * 
     * @param aParticleSetFileLineList Particle set file line list
     * @param aSourceParticle Source particle
     * @param aTargetParticle Target particle
     * @param aPercentage Percentage
     * @return Morphed particle set file line list or null if morphed list could 
     * not be created
     */
    private LinkedList<String> morphParticle(
        LinkedList<String> aParticleSetFileLineList, 
        String aSourceParticle,
        String aTargetParticle,
        double aPercentage
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleSetFileLineList == null || aParticleSetFileLineList.isEmpty()) {
            return null;
        }
        if (aSourceParticle.equals(aTargetParticle)) {
            return null;
        }
        if (aPercentage <= 0.0 || aPercentage > 100.0) {
            return null;
        }
        // </editor-fold>
        LinkedList<String> tmpMorphedParticleSetFileLineList = new LinkedList<>();
        tmpMorphedParticleSetFileLineList.add("# --------------------------------------------------------------------------------");
        tmpMorphedParticleSetFileLineList.add("# Settings for morph operation:");
        tmpMorphedParticleSetFileLineList.add("# Source particle = " + aSourceParticle);
        tmpMorphedParticleSetFileLineList.add("# Target particle = " + aTargetParticle);
        tmpMorphedParticleSetFileLineList.add("# Percentage      = " + String.valueOf(aPercentage));
        tmpMorphedParticleSetFileLineList.add("# --------------------------------------------------------------------------------");
        tmpMorphedParticleSetFileLineList.add("#");

        // <editor-fold defaultstate="collapsed" desc="Set tmpTargetParticleAij">
        HashMap<String, double[]> tmpTargetParticleAij = new HashMap<>(aParticleSetFileLineList.size());
        String tmpParticleInteractionsStartLine = String.format(ModelDefinitions.SECTION_START_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        String tmpParticleInteractionsEndLine = String.format(ModelDefinitions.SECTION_END_TAG_FORMAT, ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG);
        boolean tmpIsInteractionsSectionStart = false;
        boolean tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                RepulsionParameterLine tmpLine = new RepulsionParameterLine(this.stringUtilityMethods.splitAndTrim(tmpCurrentLine));
                if (tmpLine.getFirstParticle().equals(aTargetParticle) || tmpLine.getSecondParticle().equals(aTargetParticle)) {
                    if (tmpLine.getFirstParticle().equals(tmpLine.getSecondParticle())) {
                        // a(ii) of target particle
                        tmpTargetParticleAij.put(aTargetParticle, tmpLine.getRepulsionParameterArray());
                    } else {
                        if (tmpLine.getFirstParticle().equals(aTargetParticle)) {
                            tmpTargetParticleAij.put(tmpLine.getSecondParticle(), tmpLine.getRepulsionParameterArray());
                        } else {
                            tmpTargetParticleAij.put(tmpLine.getFirstParticle(), tmpLine.getRepulsionParameterArray());
                        }
                    }
                }
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpMorphedParticleSetFileLineList">
        tmpIsInteractionsSectionStart = false;
        tmpIsInteractionsSectionEnd = false;
        for (String tmpCurrentLine : aParticleSetFileLineList) {
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsEndLine)) {
                tmpIsInteractionsSectionEnd = true;
            }
            if (tmpIsInteractionsSectionStart && 
                !tmpIsInteractionsSectionEnd && 
                !tmpCurrentLine.trim().isEmpty() &&
                !tmpCurrentLine.startsWith(ModelDefinitions.LINE_PREFIX_TO_IGNORE) &&
                !tmpCurrentLine.startsWith("Pair")
            ) {
                RepulsionParameterLine tmpLine = new RepulsionParameterLine(this.stringUtilityMethods.splitAndTrim(tmpCurrentLine));
                if (tmpLine.getFirstParticle().equals(aSourceParticle) || tmpLine.getSecondParticle().equals(aSourceParticle)) {
                    if (tmpLine.getFirstParticle().equals(tmpLine.getSecondParticle())) {
                        tmpMorphedParticleSetFileLineList.add(tmpCurrentLine);
                    } else {
                        String tmpOtherParticle;
                        if (tmpLine.getFirstParticle().equals(aSourceParticle)) {
                            tmpOtherParticle = tmpLine.getSecondParticle();
                        } else {
                            tmpOtherParticle = tmpLine.getFirstParticle();
                        }
                        StringBuilder tmpNewLine = new StringBuilder(100);
                        tmpNewLine.append(tmpLine.getParticles());
                        double[] tmpTargetParticleRepulsionParameterArray = tmpTargetParticleAij.get(tmpOtherParticle);
                        for (int i = 0; i < tmpTargetParticleRepulsionParameterArray.length; i++) {
                            double tmpAbsoluteOffset = Math.abs(tmpLine.getRepulsionParameterArray()[i] - tmpTargetParticleRepulsionParameterArray[i]) * aPercentage / 100.0;
                            tmpNewLine.append(" ");
                            if (tmpTargetParticleRepulsionParameterArray[i] > tmpLine.getRepulsionParameterArray()[i]) {
                                tmpNewLine.append(this.stringUtilityMethods.formatDoubleValue(tmpLine.getRepulsionParameterArray()[i] + tmpAbsoluteOffset, 6));
                            } else {
                                tmpNewLine.append(this.stringUtilityMethods.formatDoubleValue(tmpLine.getRepulsionParameterArray()[i] - tmpAbsoluteOffset, 6));
                            }
                        }
                        tmpMorphedParticleSetFileLineList.add(tmpNewLine.toString());
                    }
                } else {
                    tmpMorphedParticleSetFileLineList.add(tmpCurrentLine);
                }
            } else {
                tmpMorphedParticleSetFileLineList.add(tmpCurrentLine);
            }
            if (tmpCurrentLine.trim().equalsIgnoreCase(tmpParticleInteractionsStartLine)) {
                tmpIsInteractionsSectionStart = true;
            }
        }
        // </editor-fold>

        return tmpMorphedParticleSetFileLineList;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Read particle set related methods">
    /**
     * Reads all particle related data from particle set file
     */
    private boolean readParticleSetFile() {
        try {
            if (this.isParticleSetVersion("1.0.0.0")) {
                return this.readParticleSetFile_1_0_0_0();
            } else {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Reads all particle related data from particle set file
     */
    private boolean readParticleSetFile_1_0_0_0() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Read particle descriptions from particle set file in installation directory">
            String[][] tmpItems = this.fileUtilityMethods.readJaggedStringArrayPartFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(),
                    ModelDefinitions.LINE_PREFIX_TO_IGNORE, ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG);
            // Initialize data structure:
            this.particleToDescriptionMap = new HashMap<String, StandardParticleDescription>(tmpItems.length);
            for (int i = 0; i < tmpItems.length; i++) {
                // For debugging purposes for ill defined particles:
                // System.out.println(tmpItems[i][0]);
                this.addParticleDescription(new StandardParticleDescription(tmpItems[i][0], // aParticle_Shortcut
                        tmpItems[i][1], // aName
                        tmpItems[i][2], // aMolWeightInDpdUnits
                        tmpItems[i][3], // aCharge
                        tmpItems[i][4], // aMolWeightInGMol
                        tmpItems[i][5], // aVolume
                        tmpItems[i][6], // aGraphicsRadius
                        tmpItems[i][7].toUpperCase(Locale.ENGLISH))); // aStandardColor
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Read particle interaction and temperatures from particle set file in installation directory">
            // Initialize data structure (Pairs: 100x100 = 10000):
            this.particlePairTemperatureToInteractionMap = new HashMap<>(10000);
            this.particlePairToBondLengthMap = new HashMap<>(10000);
            tmpItems = 
                this.fileUtilityMethods.readJaggedStringArrayPartFromFile(
                    Preferences.getInstance().getCurrentParticleSetFilePathname(),
                    ModelDefinitions.LINE_PREFIX_TO_IGNORE, 
                    ModelDefinitions.PARTICLE_INTERACTIONS_SECTION_TAG
                );
            // Initialize data structure:
            this.particlePairTemperatureToInteractionMap = new HashMap<String, String>(this.miscUtilityMethods.getNumberOfElementsOfJaggedArrayOfStrings(tmpItems));
            // First line (index 0) is temperature information for particles, first item is "Pair"
            this.temperatureStrings = new String[tmpItems[0].length - 1];
            this.minInteractionValueForTemperatures = new double[tmpItems[0].length - 1];
            Arrays.fill(minInteractionValueForTemperatures, Double.MAX_VALUE);
            this.maxInteractionValueForTemperatures = new double[tmpItems[0].length - 1];
            Arrays.fill(maxInteractionValueForTemperatures, -Double.MAX_VALUE);
            for (int k = 1; k < tmpItems[0].length; k++) {
                this.temperatureStrings[k - 1] = this.getTemperatureString(tmpItems[0][k]);
            }
            Arrays.sort(this.temperatureStrings);
            // Second line (index 1) and following: Interactions a(ij)
            for (int i = 1; i < tmpItems.length; i++) {
                String[] tmpParticles = ModelDefinitions.GENERAL_UNDERSCORE_PATTERN.split(tmpItems[i][0]);
                for (int k = 1; k < tmpItems[i].length; k++) {
                    // NOTE: Interaction description uses names of particles so they must be converted to particle shortcuts
                    this.addInteraction(
                        tmpParticles[0], // particle_shortcut_1
                        tmpParticles[1], // particle_shortcut_2
                        tmpItems[0][k],  // temperature
                        tmpItems[i][k]   // interaction
                    ); 
                    double tmpInteractionValue = Double.valueOf(tmpItems[i][k]);
                    if (tmpInteractionValue < this.minInteractionValueForTemperatures[k - 1]) {
                        this.minInteractionValueForTemperatures[k -1] = tmpInteractionValue;
                    }
                    if (tmpInteractionValue > this.maxInteractionValueForTemperatures[k - 1]) {
                        this.maxInteractionValueForTemperatures[k -1] = tmpInteractionValue;
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Read amino acid descriptions from particle set file in installation directory">
            tmpItems = this.fileUtilityMethods.readJaggedStringArrayPartFromFile(Preferences.getInstance().getCurrentParticleSetFilePathname(),
                    ModelDefinitions.LINE_PREFIX_TO_IGNORE, ModelDefinitions.AMINO_ACID_DESCRIPTION_SECTION_TAG);
            // IMPORTANT: Amino acid descriptions are NOT mandatory in particle set file so check!
            if (tmpItems != null) {
                // Initialize data structures:
                this.aminoAcidToDescriptionMap = new HashMap<String, AminoAcidDescription>(tmpItems.length);
                this.aminoAcidNameToDescriptionMap = new HashMap<String, AminoAcidDescription>(tmpItems.length);
                for (int i = 0; i < tmpItems.length; i++) {
                    AminoAcidDescription tmpAminoAcidDescription = new AminoAcidDescription(tmpItems[i][0], // aOneLetterCode
                            tmpItems[i][1],  // aThreeLetterCode
                            tmpItems[i][2],  // aName
                            tmpItems[i][3],  // aSpices
                            tmpItems[i][4]); // aChargeSettings
                    this.addAminoAcidDescription(tmpAminoAcidDescription);
                    // IMPORTANT: Add tmpAminoAcidDescription to AminoAcids singleton
                    if (!AminoAcids.getInstance().getAminoAcidsUtility().hasAminoAcid(tmpAminoAcidDescription.getOneLetterCode())) {
                        AminoAcid tmpAminoAcid = new AminoAcid(tmpAminoAcidDescription.getName(), tmpAminoAcidDescription.getOneLetterCode(), tmpAminoAcidDescription.getThreeLetterCode(),
                                tmpAminoAcidDescription.getSpicesString(), tmpAminoAcidDescription.getChargeSettings());
                        AminoAcids.getInstance().getAminoAcidsUtility().addAminoAcid(tmpAminoAcid);
                    }
                }
            } else {
                this.aminoAcidToDescriptionMap = null;
                this.aminoAcidNameToDescriptionMap = null;
            }
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns particle-pair-temperature descriptor. NOTE: No checks are
     * performed.
     *
     * @param aParticle1 Particle 1 of pair
     * @param aParticle1 Particle 2 of pair
     * @param aTemperature Temperature
     * @return Particle-pair-temperature descriptor
     */
    private String getParticlePairTemperatureKey(String aParticle1, String aParticle2, String aTemperature) {
        return this.getParticlePairKey(aParticle1, aParticle2) + aTemperature;
    }

    /**
     * Returns particle-pair descriptor. NOTE: No checks are performed.
     *
     * @param aParticle1 Particle 1 of pair
     * @param aParticle1 Particle 2 of pair
     * @return Particle-pair descriptor
     */
    private String getParticlePairKey(String aParticle1, String aParticle2) {
        // Sort particle name so that pair is always unique (avoids different entries for A-B and B-A)
        if (aParticle1.toUpperCase(Locale.ENGLISH).trim().compareTo(aParticle2.toUpperCase(Locale.ENGLISH).trim()) < 0) {
            return ModelDefinitions.GENERAL_SEPARATOR + aParticle1.toUpperCase(Locale.ENGLISH).trim() + ModelDefinitions.GENERAL_SEPARATOR + aParticle2.toUpperCase(Locale.ENGLISH).trim()
                    + ModelDefinitions.GENERAL_SEPARATOR;
        } else {
            return ModelDefinitions.GENERAL_SEPARATOR + aParticle2.toUpperCase(Locale.ENGLISH).trim() + ModelDefinitions.GENERAL_SEPARATOR + aParticle1.toUpperCase(Locale.ENGLISH).trim()
                    + ModelDefinitions.GENERAL_SEPARATOR;
        }
    }

    /**
     * Returns temperature string with 2 decimals
     *
     * @param aTemperatureRepresentation Temperature representation string
     * @return Temperature string with 2 decimals
     */
    private String getTemperatureString(String aTemperatureRepresentation) {
        return this.stringUtilityMethods.formatDoubleValue(aTemperatureRepresentation, 2);
    }

    /**
     * Returns particle-to-charge map from particle set file line list
     * 
     * @param aParticleSetFileLineList Particle set file line list
     * @return Particle-to-charge map or null if none can be created
     */
    private HashMap<String, Integer> getParticleToChargeMap(LinkedList<String> aParticleSetFileLineList) {
        try {
            String[][] tmpItems = 
                this.stringUtilityMethods.readJaggedStringArrayPartFromLineList(
                    aParticleSetFileLineList,
                    ModelDefinitions.LINE_PREFIX_TO_IGNORE, 
                    ModelDefinitions.PARTICLE_DESCRIPTION_SECTION_TAG
                );
            HashMap<String, Integer> tmpParticleToChargeMap = new HashMap<>(tmpItems.length);
            for (String[] tmpItem : tmpItems) {
                tmpParticleToChargeMap.put(
                    tmpItem[0],                 // aParticle_Shortcut
                    Integer.valueOf(tmpItem[3]) // aCharge
                );
            }
            return tmpParticleToChargeMap;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Checks if aVersion is version of current particle set
     * @param aVersion Version to be checked
     * @return True: aVersion is version of current particle set, false: 
     * Otherwise
     */
    private boolean isParticleSetVersion(String aVersion) {
        if (aVersion == null || aVersion.isEmpty()) {
            return false;
        }
        try {
            String[][] tmpItems = 
                this.fileUtilityMethods.readJaggedStringArrayPartFromFile(
                    Preferences.getInstance().getCurrentParticleSetFilePathname(),
                    ModelDefinitions.LINE_PREFIX_TO_IGNORE, 
                    ModelDefinitions.VERSION_SECTION_TAG
                );
            return tmpItems[0][0].equals(aVersion);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    
    /**
     * Temperatures of particle set
     * 
     * @param aParticleSetFileLineList Particle set file line list
     * @return Temperatures of particle set or null if non can be evaluated
     */
    private double[] getTemperatures() {
        try {
            double[] tmpTemperatures = new double[this.temperatureStrings.length];
            for (int i = 0; i < tmpTemperatures.length; i++) {
                tmpTemperatures[i] = Double.valueOf(this.temperatureStrings[i]);
            }
            return tmpTemperatures;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Diagonal repulsions a(ii) of particle set for all temperatures
     * 
     * @param aParticleSetFileLineList Particle set file line list
     * @param aParticleToChargeMap Particle-to-charge map
     * @return Diagonal repulsions a(ii) of particle set for all temperatures 
     * or null if non can be evaluated
     */
    private double[] getAiiForTemperatures() {
        try {
            String[] tmpTemperatureStrings = this.getTemperatureStrings();
            String tmpWaterParticle = this.getDefaultWaterParticle();
            double[] tmpAii = new double[tmpTemperatureStrings.length];
            for (int i = 0; i < tmpAii.length; i++) {
                tmpAii[i] = Double.valueOf(this.getInteraction(tmpWaterParticle, tmpWaterParticle, tmpTemperatureStrings[i]));
            }
            return tmpAii;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
