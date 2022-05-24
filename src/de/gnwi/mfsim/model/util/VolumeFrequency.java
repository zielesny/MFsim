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
package de.gnwi.mfsim.model.util;

import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Volume-slice frequencies for molecules, molecule-particle strings and particles in x, y and z dimension
 * 
 * @author Achim Zielesny
 */
public class VolumeFrequency {

    // <editor-fold defaultstate="collapsed" desc="Public enum VolumeAxis">
    /**
     * x, y and z axis in volume
     */
    public enum VolumeAxis {
        X,
        Y,
        Z;
        
        /**
         * Returns array with volume axes
         * 
         * @return Array with volume axes
         */
        public static VolumeAxis[] getVolumeAxisArray() {
            return 
                new VolumeAxis[]
                    {
                        VolumeAxis.X,
                        VolumeAxis.Y,
                        VolumeAxis.Z
                    };
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class VolumeSliceFrequency1D">
    /**
     * Volume-slice frequencies for molecules, molecule-particle strings and particles in one dimension
     * 
     * @author Achim Zielesny
     */
    private class VolumeSliceFrequency1D {

        // <editor-fold defaultstate="collapsed" desc="Private class variables">
        /**
         * Minimum length
         */
        private double minLength;

        /**
         * Maximum length
         */
        private double maxLength;

        /**
         * Number of volume-slices
         */
        private int numberOfVolumeSlices;

        /**
         * Length of volume-slices
         */
        private double volumeSliceLength;

        /**
         * Array with positions in Angstrom of the middle of the volume-slice
         */
        private double[] volumeSlicePositionInAngstromArray;

        /**
         * Map of molecule name to hash map that maps molecule-particle string to its volume-slice frequency
         */
        private HashMap<String, HashMap<String, int[]>> moleculeNameToMoleculeParticleFrequencyMap;

        /**
         * Map of molecule-particle string to frequency of molecule particle
         */
        private HashMap<String, Integer> moleculeParticleStringToMoleculeParticleFrequencyMap;

        /**
         * Map of particle to volume-slice frequencies
         */
        private HashMap<String, int[]> particleToFrequencyMap;

        /**
         * Factor that converts DPD length to physical length (Angstrom since
         * particle volumes are in Angstrom^3)
         */
        private double lengthConversionFactor;
        // </editor-fold>
        //
        // <editor-fold defaultstate="collapsed" desc="Constructor">
        /**
         * Constructor
         * 
         * @param aMinLength Minimum length
         * @param aMaxLength Maximum length
         * @param aNumberOfVolumeSlices Number of volume-slices
         * @param aLengthConversionFactor Factor that converts DPD length to
         *                                physical length (Angstrom since particle 
         *                                volumes are in Angstrom^3)
         * @throws IllegalArgumentException Thrown if an argument is illegal
         */
        public VolumeSliceFrequency1D(double aMinLength, double aMaxLength, int aNumberOfVolumeSlices, double aLengthConversionFactor) {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (aMaxLength <= aMinLength) {
                throw new IllegalArgumentException("An argument is illegal");
            }
            if (aNumberOfVolumeSlices < 2) {
                throw new IllegalArgumentException("An argument is illegal");
            }
            if (aLengthConversionFactor < 0.0) {
                throw new IllegalArgumentException("An argument is illegal");
            }
            // </editor-fold>
            this.minLength = aMinLength;
            this.maxLength = aMaxLength;
            this.numberOfVolumeSlices = aNumberOfVolumeSlices;
            this.lengthConversionFactor = aLengthConversionFactor;
            // Capacity 20 should be sufficient for common number of molecules
            this.moleculeNameToMoleculeParticleFrequencyMap = new HashMap<>(20);
            // Capacity 1000 should be sufficient for common number of 20 molecules with 50 particles each
            this.moleculeParticleStringToMoleculeParticleFrequencyMap = new HashMap<>(1000);
            // Capacity 100 should be sufficient for common number of particles
            this.particleToFrequencyMap = new HashMap<>(100);
            this.initialCalculations();
        }
        // </editor-fold>
        //
        // <editor-fold defaultstate="collapsed" desc="Public methods">
        /**
         * Increments all counters in volume-slice that corresponds to aPosition
         * 
         * @param aGraphicalParticle Graphical particle
         * @param aPosition Position
         * @throws IllegalArgumentException Thrown if argument is illegal
         */
        public void incrementCounters(GraphicalParticle aGraphicalParticle, double aPosition) {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (aGraphicalParticle == null) {
                throw new IllegalArgumentException("aGraphicalParticle is null.");
            }
            if (aPosition < this.minLength || aPosition > this.maxLength) {
                throw new IllegalArgumentException("aPosition is illegal");
            }
            // </editor-fold>
            HashMap<String, int[]> tmpMoleculeParticleStringToFrequencyMap = null;
            if (!this.moleculeNameToMoleculeParticleFrequencyMap.containsKey(aGraphicalParticle.getMoleculeName())) {
                // Capacity 50 should be sufficient for common number of molecule-particle strings
                tmpMoleculeParticleStringToFrequencyMap = new HashMap<>(50);
                tmpMoleculeParticleStringToFrequencyMap.put(aGraphicalParticle.getMoleculeParticleString(), new int[this.numberOfVolumeSlices]);
                this.moleculeNameToMoleculeParticleFrequencyMap.put(aGraphicalParticle.getMoleculeName(), tmpMoleculeParticleStringToFrequencyMap);
                this.moleculeParticleStringToMoleculeParticleFrequencyMap.put(aGraphicalParticle.getMoleculeParticleString(), aGraphicalParticle.getMoleculeParticleFrequency());
            } else {
                tmpMoleculeParticleStringToFrequencyMap = this.moleculeNameToMoleculeParticleFrequencyMap.get(aGraphicalParticle.getMoleculeName());
                if (!tmpMoleculeParticleStringToFrequencyMap.containsKey(aGraphicalParticle.getMoleculeParticleString())) {
                    tmpMoleculeParticleStringToFrequencyMap.put(aGraphicalParticle.getMoleculeParticleString(), new int[this.numberOfVolumeSlices]);
                    this.moleculeParticleStringToMoleculeParticleFrequencyMap.put(aGraphicalParticle.getMoleculeParticleString(), aGraphicalParticle.getMoleculeParticleFrequency());
                }
            }
            double tmpDistance = aPosition - this.minLength;
            int tmpVolumeSliceIndex = this.getVolumeSliceIndex(tmpDistance);
            // NOTE: Correct for possible illegal maximum tmpVolumeSliceIndex if aGraphicalParticle is exactly on the right boundary
            if (tmpVolumeSliceIndex == this.numberOfVolumeSlices) {
                tmpVolumeSliceIndex = this.numberOfVolumeSlices - 1;
            }
            tmpMoleculeParticleStringToFrequencyMap.get(aGraphicalParticle.getMoleculeParticleString())[tmpVolumeSliceIndex]++;
            this.particleToFrequencyMap.putIfAbsent(aGraphicalParticle.getParticle(), new int[this.numberOfVolumeSlices]);
            this.particleToFrequencyMap.get(aGraphicalParticle.getParticle())[tmpVolumeSliceIndex]++;
        }

        /**
         * Returns molecule names sorted ascending
         * 
         * @return Molecule names sorted ascending or null if none are available
         */
        public String[] getSortedMoleculeNames() {
            if (this.moleculeNameToMoleculeParticleFrequencyMap.isEmpty()) {
                return null;
            } else {
                String[] tmpMoleculeNameArray = this.moleculeNameToMoleculeParticleFrequencyMap.keySet().toArray(new String[0]);
                Arrays.sort(tmpMoleculeNameArray);
                return tmpMoleculeNameArray;
            }
        }

        /**
         * Returns particles sorted ascending
         * 
         * @return Particles sorted ascending or null if none are available
         */
        public String[] getSortedParticles() {
            if (this.particleToFrequencyMap.isEmpty()) {
                return null;
            } else {
                String[] tmpParticleArray = this.particleToFrequencyMap.keySet().toArray(new String[0]);
                Arrays.sort(tmpParticleArray);
                return tmpParticleArray;
            }
        }

        /**
         * Returns volume-slice frequency array for particle.
         * NOTE: The positions of each volume-slice are available via method getVolumeSlicePositionsInAngstrom()
         * 
         * @param aParticle Particle
         * @return Volume-slice frequency array for particle or null if none is available
         */
        public int[] getVolumeSliceParticleFrequencies(String aParticle) {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (aParticle == null || aParticle.isEmpty()) {
                throw new IllegalArgumentException("An argument is illegal");
            }
            // </editor-fold>
            if (this.particleToFrequencyMap.containsKey(aParticle)) {
                return this.particleToFrequencyMap.get(aParticle);
            } else {
                return null;
            }
        }

        /**
         * Returns molecule-particle strings sorted ascending for molecule name
         * 
         * @param aMoleculeName Molecule name
         * @return Molecule-particle strings sorted ascending for molecule name or null if none are available
         */
        public String[] getSortedMoleculeParticleStrings(String aMoleculeName) {
            if (!this.moleculeNameToMoleculeParticleFrequencyMap.containsKey(aMoleculeName)) {
                return null;
            } else {
                String[] tmpMoleculeParticleStringArray = this.moleculeNameToMoleculeParticleFrequencyMap.get(aMoleculeName).keySet().toArray(new String[0]);
                Arrays.sort(tmpMoleculeParticleStringArray);
                return tmpMoleculeParticleStringArray;
            }
        }

        /**
         * Returns volume-slice frequency array for molecule-particle string.
         * NOTE: The positions of each volume-slice are available via method getVolumeSlicePositionsInAngstrom()
         * 
         * @param aMoleculeName Molecule name
         * @param aMoleculeParticleString Molecule-particle string
         * @return Volume-slice frequency array for molecule-particle string or null if none is available
         */
        public int[] getVolumeSliceMoleculeParticleFrequencies(String aMoleculeName, String aMoleculeParticleString) {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (aMoleculeName == null || aMoleculeName.isEmpty()) {
                throw new IllegalArgumentException("An argument is illegal");
            }
            if (aMoleculeParticleString == null || aMoleculeParticleString.isEmpty()) {
                throw new IllegalArgumentException("An argument is illegal");
            }
            // </editor-fold>
            if (this.moleculeNameToMoleculeParticleFrequencyMap.containsKey(aMoleculeName)) {
                HashMap<String, int[]> tmpMoleculeParticleStringToFrequencyMap = this.moleculeNameToMoleculeParticleFrequencyMap.get(aMoleculeName);
                if (tmpMoleculeParticleStringToFrequencyMap.containsKey(aMoleculeParticleString)) {
                    return tmpMoleculeParticleStringToFrequencyMap.get(aMoleculeParticleString);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        /**
         * Returns volume-slice frequency array for molecule.
         * NOTE: The positions of each volume-slice are available via method getVolumeSlicePositionsInAngstrom()
         * 
         * @param aMoleculeName Molecule name
         * @return Volume-slice frequency array for molecule or null if none is available
         */
        public double[] getVolumeSliceMoleculeFrequencies(String aMoleculeName) {
            // <editor-fold defaultstate="collapsed" desc="Checks">
            if (aMoleculeName == null || aMoleculeName.isEmpty()) {
                throw new IllegalArgumentException("An argument is illegal");
            }
            // </editor-fold>
            if (this.moleculeNameToMoleculeParticleFrequencyMap.containsKey(aMoleculeName)) {
                double[] tmpMoleculeFrequenceArray = new double[this.numberOfVolumeSlices];
                Arrays.fill(tmpMoleculeFrequenceArray, 0.0);
                HashMap<String, int[]> tmpMoleculeParticleStringToFrequencyMap = this.moleculeNameToMoleculeParticleFrequencyMap.get(aMoleculeName);
                int tmpTotalNumberOfMoleculeParticlesInMolecule = 0;
                for (String tmpMoleculeParticleString : tmpMoleculeParticleStringToFrequencyMap.keySet()) {
                    tmpTotalNumberOfMoleculeParticlesInMolecule += this.moleculeParticleStringToMoleculeParticleFrequencyMap.get(tmpMoleculeParticleString);
                }
                for (String tmpMoleculeParticleString : tmpMoleculeParticleStringToFrequencyMap.keySet()) {
                    int[] tmpMoleculeParticleStringFrequencyArray = tmpMoleculeParticleStringToFrequencyMap.get(tmpMoleculeParticleString);
                    for (int i = 0; i < this.numberOfVolumeSlices; i++) {
                        tmpMoleculeFrequenceArray[i] += (double) tmpMoleculeParticleStringFrequencyArray[i] / (double) tmpTotalNumberOfMoleculeParticlesInMolecule;
                    }
                }
                return tmpMoleculeFrequenceArray;
            } else {
                return null;
            }
        }

        /**
         * Returns volume-slice position in Angstrom array
         * 
         * @return Volume-slice position in Angstrom array
         */
        public double[] getVolumeSlicePositionsInAngstrom() {
            return this.volumeSlicePositionInAngstromArray;
        }
        // </editor-fold>
        //
        // <editor-fold defaultstate="collapsed" desc="Private methods">
        /**
         * Initial calculations
         */
        private void initialCalculations() {
            this.volumeSliceLength = (this.maxLength - this.minLength)/(double) this.numberOfVolumeSlices;
            double tmpPosition = this.volumeSliceLength/2.0 + this.minLength;
            this.volumeSlicePositionInAngstromArray = new double[this.numberOfVolumeSlices];
            for (int i = 0; i < this.numberOfVolumeSlices; i++) {
                this.volumeSlicePositionInAngstromArray[i] = tmpPosition * this.lengthConversionFactor;
                tmpPosition += this.volumeSliceLength;
            }
        }

        /**
         * Returns index of volume-slice of distance according to volume-slice length.
         * Volume-slice 0 contains all distances from 0 to volume-slice length, 
         * volume-slice 1 all distances from volume-slice length to 2 volume-slice lengths etc.
         *
         * @param aDistance
         * @return Index of volume-slice of distance according to volume-slice length
         */
        private int getVolumeSliceIndex(double aDistance) {
            // aDistance and this.volumeSliceLength are positive:
            return (int) (aDistance / this.volumeSliceLength);
            // Slower:
            // return (int) Math.floor(aDistance / this.volumeSliceLength);
        }
        // </editor-fold>

    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * VolumeSliceFrequency1D in x-direction
     */
    private VolumeSliceFrequency1D volumeSliceFrequencyX;

    /**
     * VolumeSliceFrequency1D in y-direction
     */
    private VolumeSliceFrequency1D volumeSliceFrequencyY;
    
    /**
     * VolumeSliceFrequency1D in z-direction
     */
    private VolumeSliceFrequency1D volumeSliceFrequencyZ;
    
    /**
     * Map from molecule-particle string to its frequency
     */
    private HashMap<String, Integer> moleculeParticleStringToFrequencyMap;

    /**
     * Map from molecule-particle string to its graphical particle
     */
    private HashMap<String, GraphicalParticle> moleculeParticleStringToGraphicalParticleMap;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aBoxSizeInfo Box size info
     * @param aNumberOfVolumeSlices Number of volume-slices
     * @param aLengthConversionFactor Factor that converts DPD length to
     *                                physical length (Angstrom since particle 
     *                                volumes are in Angstrom^3)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public VolumeFrequency(BoxSizeInfo aBoxSizeInfo, int aNumberOfVolumeSlices, double aLengthConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoxSizeInfo == null) {
            throw new IllegalArgumentException("An argument is illegal");
        }
        if (aNumberOfVolumeSlices < 2) {
            throw new IllegalArgumentException("An argument is illegal");
        }
        if (aLengthConversionFactor < 0.0) {
            throw new IllegalArgumentException("An argument is illegal");
        }
        // </editor-fold>
        this.volumeSliceFrequencyX = new VolumeSliceFrequency1D(aBoxSizeInfo.getXMin(), aBoxSizeInfo.getXMax(), aNumberOfVolumeSlices, aLengthConversionFactor);
        this.volumeSliceFrequencyY = new VolumeSliceFrequency1D(aBoxSizeInfo.getYMin(), aBoxSizeInfo.getYMax(), aNumberOfVolumeSlices, aLengthConversionFactor);
        this.volumeSliceFrequencyZ = new VolumeSliceFrequency1D(aBoxSizeInfo.getZMin(), aBoxSizeInfo.getZMax(), aNumberOfVolumeSlices, aLengthConversionFactor);
        // Capacity 1000 should be sufficient for common number of 20 molecules with 50 particles each
        this.moleculeParticleStringToFrequencyMap = new HashMap<>(1000);
        this.moleculeParticleStringToGraphicalParticleMap = new HashMap<>(1000);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Increments all counters in volume-slice
     * 
     * @param aGraphicalParticlePosition Graphical particle position
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void incrementCounters(GraphicalParticlePosition aGraphicalParticlePosition) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aGraphicalParticlePosition == null) {
            throw new IllegalArgumentException("An argument is illegal");
        }
        // </editor-fold>
        GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) aGraphicalParticlePosition.getGraphicalParticle();
        // Increment volume-slices along axes
        this.volumeSliceFrequencyX.incrementCounters(tmpGraphicalParticle, aGraphicalParticlePosition.getX());
        this.volumeSliceFrequencyY.incrementCounters(tmpGraphicalParticle, aGraphicalParticlePosition.getY());
        this.volumeSliceFrequencyZ.incrementCounters(tmpGraphicalParticle, aGraphicalParticlePosition.getZ());
        // Increment whole volume counters
        this.moleculeParticleStringToGraphicalParticleMap.putIfAbsent(tmpGraphicalParticle.getMoleculeParticleString(), tmpGraphicalParticle);
        this.moleculeParticleStringToFrequencyMap.put(
            tmpGraphicalParticle.getMoleculeParticleString(), 
            this.moleculeParticleStringToFrequencyMap.getOrDefault(tmpGraphicalParticle.getMoleculeParticleString(), 0) + 1
        );
    }

    /**
     * Increment counter of specified axis
     * 
     * @param aVolumeAxis Axis along which particles are counted
     * @param aGraphicalParticlePosition Graphical particle position
     */
    public void incrementCounters(VolumeAxis aVolumeAxis, GraphicalParticlePosition aGraphicalParticlePosition) {
        GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) aGraphicalParticlePosition.getGraphicalParticle();
        switch (aVolumeAxis) {
            case X:
                this.volumeSliceFrequencyX.incrementCounters(tmpGraphicalParticle, aGraphicalParticlePosition.getX());
                break;
            case Y:
                this.volumeSliceFrequencyY.incrementCounters(tmpGraphicalParticle, aGraphicalParticlePosition.getY());
                break;
            case Z:
                this.volumeSliceFrequencyZ.incrementCounters(tmpGraphicalParticle, aGraphicalParticlePosition.getZ());
                break;
        }
        // Increment whole volume counters
        this.moleculeParticleStringToGraphicalParticleMap.putIfAbsent(tmpGraphicalParticle.getMoleculeParticleString(), tmpGraphicalParticle);
        this.moleculeParticleStringToFrequencyMap.put(
            tmpGraphicalParticle.getMoleculeParticleString(), 
            this.moleculeParticleStringToFrequencyMap.getOrDefault(tmpGraphicalParticle.getMoleculeParticleString(), 0) + 1
        );
    }
    
    /**
     * Returns molecule-particle strings sorted ascending
     * 
     * @return Molecule-particle strings sorted ascending or null if none are available
     */
    public String[] getSortedMoleculeParticleStrings() {
        if (this.moleculeParticleStringToGraphicalParticleMap.isEmpty()) {
            return null;
        } else {
            String[] tmpMoleculeParticleStringArray = this.moleculeParticleStringToGraphicalParticleMap.keySet().toArray(new String[0]);
            Arrays.sort(tmpMoleculeParticleStringArray);
            return tmpMoleculeParticleStringArray;
        }
    }
    
    /**
     * Returns graphical particle that corresponds to molecule-particle string
     * 
     * @param aMoleculeParticleString Molecule-particle string
     * @return Graphical particle that corresponds to molecule-particle string or null if none is available
     */
    public GraphicalParticle getGraphicalParticle(String aMoleculeParticleString) {
        if (aMoleculeParticleString == null || aMoleculeParticleString.isEmpty() || !this.moleculeParticleStringToGraphicalParticleMap.containsKey(aMoleculeParticleString)) {
            return null;
        } else {
            return this.moleculeParticleStringToGraphicalParticleMap.get(aMoleculeParticleString);
        }
    }
    
    /**
     * Returns molecule-particle string frequency in whole volume
     * 
     * @param aMoleculeParticleString Molecule-particle string
     * @return Molecule-particle string frequency in whole volume or 0 if none is available
     */
    public int getVolumeFrequency(String aMoleculeParticleString) {
        if (aMoleculeParticleString == null || aMoleculeParticleString.isEmpty() || !this.moleculeParticleStringToFrequencyMap.containsKey(aMoleculeParticleString)) {
            return 0;
        } else {
            return this.moleculeParticleStringToFrequencyMap.get(aMoleculeParticleString);
        }
    }
    
    /**
     * Returns molecule names sorted ascending for axis
     * 
     * @param aVolumeAxis Volume axis
     * @return Molecule names sorted ascending for axis or null if none are available
     */
    public String[] getSortedMoleculeNames(VolumeAxis aVolumeAxis) {
        switch(aVolumeAxis) {
            case X:
                return this.volumeSliceFrequencyX.getSortedMoleculeNames();
            case Y:
                return this.volumeSliceFrequencyY.getSortedMoleculeNames();
            case Z:
                return this.volumeSliceFrequencyZ.getSortedMoleculeNames();
        }
        return null;
    }
    
    /**
     * Returns particles sorted ascending for axis
     * 
     * @param aVolumeAxis Volume axis
     * @return Particles sorted ascending for axis or null if none are available
     */
    public String[] getSortedParticles(VolumeAxis aVolumeAxis) {
        switch(aVolumeAxis) {
            case X:
                return this.volumeSliceFrequencyX.getSortedParticles();
            case Y:
                return this.volumeSliceFrequencyY.getSortedParticles();
            case Z:
                return this.volumeSliceFrequencyZ.getSortedParticles();
        }
        return null;
    }

    /**
     * Returns volume-slice frequency array for axis and molecule.
     * NOTE: The positions of each volume-slice are available via method getVolumeSlicePositionsInAngstrom()
     * 
     * @param aVolumeAxis Volume axis
     * @param aMoleculeName Molecule name
     * @return Volume-slice frequency array for axis and molecule or null if none is available
     */
    public double[] getVolumeSliceMoleculeFrequencies(VolumeAxis aVolumeAxis, String aMoleculeName) {
        switch(aVolumeAxis) {
            case X:
                return this.volumeSliceFrequencyX.getVolumeSliceMoleculeFrequencies(aMoleculeName);
            case Y:
                return this.volumeSliceFrequencyY.getVolumeSliceMoleculeFrequencies(aMoleculeName);
            case Z:
                return this.volumeSliceFrequencyZ.getVolumeSliceMoleculeFrequencies(aMoleculeName);
        }
        return null;
    }
    
    /**
     * Returns volume-slice frequency array for axis and particle.
     * NOTE: The positions of each volume-slice are available via method getVolumeSlicePositionsInAngstrom()
     * 
     * @param aVolumeAxis Volume axis
     * @param aParticle Particle
     * @return Volume-slice frequency array for axis and particle or null if none is available
     */
    public int[] getVolumeSliceParticleFrequencies(VolumeAxis aVolumeAxis, String aParticle) {
        switch(aVolumeAxis) {
            case X:
                return this.volumeSliceFrequencyX.getVolumeSliceParticleFrequencies(aParticle);
            case Y:
                return this.volumeSliceFrequencyY.getVolumeSliceParticleFrequencies(aParticle);
            case Z:
                return this.volumeSliceFrequencyZ.getVolumeSliceParticleFrequencies(aParticle);
        }
        return null;
    }

    /**
     * Returns molecule-particle strings sorted ascending for axis and molecule name
     * 
     * @param aVolumeAxis Volume axis
     * @param aMoleculeName Molecule name
     * @return Molecule-particle strings sorted ascending for axis and molecule name or null if none are available
     */
    public String[] getSortedMoleculeParticleStrings(VolumeAxis aVolumeAxis, String aMoleculeName) {
        switch(aVolumeAxis) {
            case X:
                return this.volumeSliceFrequencyX.getSortedMoleculeParticleStrings(aMoleculeName);
            case Y:
                return this.volumeSliceFrequencyY.getSortedMoleculeParticleStrings(aMoleculeName);
            case Z:
                return this.volumeSliceFrequencyZ.getSortedMoleculeParticleStrings(aMoleculeName);
        }
        return null;
    }

    /**
     * Returns volume-slice frequency array for axis and molecule-particle string.
     * NOTE: The positions of each volume-slice are available via method getVolumeSlicePositionsInAngstrom()
     * 
     * @param aVolumeAxis Volume axis
     * @param aMoleculeName Molecule name
     * @param aMoleculeParticleString Molecule-particle string
     * @return Volume-slice frequency array for axis and molecule-particle string or null if none is available
     */
    public int[] getVolumeSliceMoleculeParticleFrequencies(VolumeAxis aVolumeAxis, String aMoleculeName, String aMoleculeParticleString) {
        switch(aVolumeAxis) {
            case X:
                return this.volumeSliceFrequencyX.getVolumeSliceMoleculeParticleFrequencies(aMoleculeName, aMoleculeParticleString);
            case Y:
                return this.volumeSliceFrequencyY.getVolumeSliceMoleculeParticleFrequencies(aMoleculeName, aMoleculeParticleString);
            case Z:
                return this.volumeSliceFrequencyZ.getVolumeSliceMoleculeParticleFrequencies(aMoleculeName, aMoleculeParticleString);
        }
        return null;
    }

    /**
     * Returns volume-slice position in Angstrom array for axis
     * 
     * @param aVolumeAxis Volume axis
     * @return Volume-slice position in Angstrom array for axis
     */
    public double[] getVolumeSlicePositionsInAngstrom(VolumeAxis aVolumeAxis) {
        switch(aVolumeAxis) {
            case X:
                return this.volumeSliceFrequencyX.getVolumeSlicePositionsInAngstrom();
            case Y:
                return this.volumeSliceFrequencyY.getVolumeSlicePositionsInAngstrom();
            case Z:
                return this.volumeSliceFrequencyZ.getVolumeSlicePositionsInAngstrom();
        }
        return null;
    }
    // </editor-fold>

}
