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
package de.gnwi.mfsim.model.job;

import de.gnwi.spices.PointInSpace;
import java.util.Arrays;

/**
 * Utility class for calculation of the frequency of spatial distances between
 * particles in the simulation box used for RDF calculations (PBC in all
 * directions are assumed)
 *
 * @author Mirco Daniel, Achim Zielesny
 */
public class DistanceDistributionUtils {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Length of distance segment
     */
    private double segmentLength;
    /**
     * Length of box (x)
     */
    private double boxLengthX;
    /**
     * Length of box (y)
     */
    private double boxLengthY;
    /**
     * Length of box (z)
     */
    private double boxLengthZ;
    /**
     * Half length of box (x)
     */
    private double halfBoxLengthX;
    /**
     * Half length of box (y)
     */
    private double halfBoxLengthY;
    /**
     * Half length of box (z)
     */
    private double halfBoxLengthZ;
    /**
     * Minimum half box length
     */
    private double minimumHalfBoxLength;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aSegmentLength Length of distance segment
     * @param aBoxLengthX Length of box (x)
     * @param aBoxLengthY Width of box (y)
     * @param aBoxLengthZ Height of box (z)
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public DistanceDistributionUtils(double aSegmentLength, double aBoxLengthX, double aBoxLengthY, double aBoxLengthZ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSegmentLength <= 0) {
            throw new IllegalArgumentException("An argument is illegal");
        }
        if (aBoxLengthX <= 0 || aBoxLengthY <= 0 || aBoxLengthZ <= 0) {
            throw new IllegalArgumentException("An argument is illegal");
        }

        // </editor-fold>
        this.segmentLength = aSegmentLength;
        this.boxLengthX = aBoxLengthX;
        this.boxLengthY = aBoxLengthY;
        this.boxLengthZ = aBoxLengthZ;

        this.halfBoxLengthX = 0.5 * this.boxLengthX;
        this.halfBoxLengthY = 0.5 * this.boxLengthY;
        this.halfBoxLengthZ = 0.5 * this.boxLengthZ;

        this.minimumHalfBoxLength = Math.min(this.halfBoxLengthX, Math.min(this.halfBoxLengthY, this.halfBoxLengthZ));
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns the frequencies of the distance bins between equal particle-pair
     * A-A
     *
     * @param aParticlePositions Positions of particles A
     * @return Integer array or null if equal particle-pair A-A distribution can
     * not be calculated. Index = Index of distance segment (histogram bin)
     * starting with 0, Integer value = Frequency of particle within distance
     * segment on average
     */
    public double[] getEqualParticlePairDistanceBinFrequencies(PointInSpace[] aParticlePositions) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePositions == null) {
            return null;
        }
        if (aParticlePositions.length < 1) {
            return null;
        }

        // </editor-fold>
        int tmpNumberOfParticlesOfTypeI = aParticlePositions.length;
        int tmpNumberOfParticlesOfTypeJ = aParticlePositions.length;

        // NOTE: Use long type for integer arithmetics to avoid possible overflow to NEGATIVE (!) minimum value Integer.MIN_VALUE!
        long[] tmpAccumulatedFrequencies = new long[0];
        long[] tmpFrequencies = new long[0];
        long tmpNumberOfAccumulations = 0;
        for (int k = 0; k < tmpNumberOfParticlesOfTypeI; k++) {
            PointInSpace tmpSinglePositionOfTypeI = aParticlePositions[k];
            Arrays.fill(tmpFrequencies, 0);
            for (int l = k + 1; l < tmpNumberOfParticlesOfTypeJ; l++) {
                PointInSpace tmpSinglePositionOfTypeJ = aParticlePositions[l];
                double tmpDistance = this.calculateDistance(tmpSinglePositionOfTypeI, tmpSinglePositionOfTypeJ);
                if (tmpDistance <= this.minimumHalfBoxLength) {
                    int tmpBinIndex = this.getBinIndex(tmpDistance);
                    if (tmpBinIndex + 1 > tmpFrequencies.length) {
                        tmpFrequencies = Arrays.copyOf(tmpFrequencies, tmpBinIndex + 1);
                    }
                    tmpFrequencies[tmpBinIndex] += 2;
                }
            }
            if (tmpFrequencies.length > tmpAccumulatedFrequencies.length) {
                tmpAccumulatedFrequencies = Arrays.copyOf(tmpAccumulatedFrequencies, tmpFrequencies.length);
            }
            for (int u = 0; u < tmpFrequencies.length; u++) {
                tmpAccumulatedFrequencies[u] += tmpFrequencies[u];
            }
            tmpNumberOfAccumulations++;
        }
        // Calculate averaged frequencies
        double[] tmpAveragedFrequencies = new double[tmpAccumulatedFrequencies.length];
        for (int v = 0; v < tmpAccumulatedFrequencies.length; v++) {
            tmpAveragedFrequencies[v] = (double) tmpAccumulatedFrequencies[v] / (double) tmpNumberOfAccumulations;
        }

        return tmpAveragedFrequencies;
    }

    /**
     * Returns the frequencies of the distance bins between different
     * particle-pair A-B
     *
     * @param aParticlePositionsA Positions of particles A
     * @param aParticlePositionsB Positions of particles B
     * @return Integer array or null if different particle-pair A-B distribution
     * can not be calculated. Index = Index of distance segment (histogram bin)
     * starting with 0, Integer value = Frequency of particle B within distance
     * segment on average
     */
    public double[] getDifferentParticlePairDistanceBinFrequencies(PointInSpace[] aParticlePositionsA, PointInSpace[] aParticlePositionsB) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePositionsA == null) {
            return null;
        }
        if (aParticlePositionsA.length < 1) {
            return null;
        }
        if (aParticlePositionsB == null) {
            return null;
        }
        if (aParticlePositionsB.length < 1) {
            return null;
        }

        // </editor-fold>
        int tmpNumberOfParticlesOfTypeI = aParticlePositionsA.length;
        int tmpNumberOfParticlesOfTypeJ = aParticlePositionsB.length;

        // NOTE: Use long type for integer arithmetics to avoid possible overflow to NEGATIVE (!) minimum value Integer.MIN_VALUE!
        long[] tmpAccumulatedFrequencies = new long[0];
        long[] tmpFrequencies = new long[0];
        long tmpNumberOfAccumulations = 0;
        for (int k = 0; k < tmpNumberOfParticlesOfTypeI; k++) {
            PointInSpace tmpSinglePositionOfTypeI = aParticlePositionsA[k];
            Arrays.fill(tmpFrequencies, 0);
            for (int l = 0; l < tmpNumberOfParticlesOfTypeJ; l++) {
                PointInSpace tmpSinglePositionOfTypeJ = aParticlePositionsB[l];
                double tmpDistance = this.calculateDistance(tmpSinglePositionOfTypeI, tmpSinglePositionOfTypeJ);
                if (tmpDistance <= this.minimumHalfBoxLength) {
                    int tmpBinIndex = this.getBinIndex(tmpDistance);
                    if (tmpBinIndex + 1 > tmpFrequencies.length) {
                        tmpFrequencies = Arrays.copyOf(tmpFrequencies, tmpBinIndex + 1);
                    }
                    tmpFrequencies[tmpBinIndex]++;
                }
            }
            if (tmpFrequencies.length > tmpAccumulatedFrequencies.length) {
                tmpAccumulatedFrequencies = Arrays.copyOf(tmpAccumulatedFrequencies, tmpFrequencies.length);
            }
            for (int u = 0; u < tmpFrequencies.length; u++) {
                tmpAccumulatedFrequencies[u] += tmpFrequencies[u];
            }
            tmpNumberOfAccumulations++;
        }
        // Calculate averaged frequencies
        double[] tmpAveragedFrequencies = new double[tmpAccumulatedFrequencies.length];
        for (int v = 0; v < tmpAccumulatedFrequencies.length; v++) {
            tmpAveragedFrequencies[v] = (double) tmpAccumulatedFrequencies[v] / (double) tmpNumberOfAccumulations;
        }

        return tmpAveragedFrequencies;
    }

    // <editor-fold defaultstate="collapsed" desc="Non-used public methods">
    /**
     * Returns the frequencies of the distance bins between particle types
     *
     * @param aParticlePositionMatrix Positions of all particles of simulation
     * box: Index 1 = Index of particle type, index 2 = Position of single
     * particle of specified type
     * @return Integer array or null if particle matrix distribution can not be
     * calculated. Index 1 of array = Index of particle type 1, index 2 = Index
     * of particle type 2, index 3 = index of distance segment (histogram bin)
     * starting with 0, Integer value = Frequency of particle of type 2 within
     * distance segment on average
     */
    public double[][][] getParticleParticleDistanceBinFrequencies(PointInSpace[][] aParticlePositionMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePositionMatrix == null) {
            return null;
        }
        if (aParticlePositionMatrix.length < 1) {
            return null;
        }

        // </editor-fold>
        int tmpNumberOfParticleTypes = aParticlePositionMatrix.length;
        double[][][] tmpBinFrequencies = new double[tmpNumberOfParticleTypes][][];

        // <editor-fold defaultstate="collapsed" desc="FIRST - Equal particle types">
        for (int i = 0; i < tmpNumberOfParticleTypes; i++) {
            tmpBinFrequencies[i] = new double[i + 1][];

            int tmpNumberOfParticlesOfTypeI = aParticlePositionMatrix[i].length;
            int tmpNumberOfParticlesOfTypeJ = aParticlePositionMatrix[i].length;

            int[] tmpAccumulatedFrequencies = new int[0];
            int[] tmpFrequencies = new int[0];
            int tmpNumberOfAccumulations = 0;
            for (int k = 0; k < tmpNumberOfParticlesOfTypeI; k++) {
                PointInSpace tmpSinglePositionOfTypeI = aParticlePositionMatrix[i][k];
                Arrays.fill(tmpFrequencies, 0);
                for (int l = k + 1; l < tmpNumberOfParticlesOfTypeJ; l++) {
                    PointInSpace tmpSinglePositionOfTypeJ = aParticlePositionMatrix[i][l];
                    double tmpDistance = this.calculateDistance(tmpSinglePositionOfTypeI, tmpSinglePositionOfTypeJ);
                    int tmpBinIndex = this.getBinIndex(tmpDistance);
                    if (tmpBinIndex + 1 > tmpFrequencies.length) {
                        tmpFrequencies = Arrays.copyOf(tmpFrequencies, tmpBinIndex + 1);
                    }
                    tmpFrequencies[tmpBinIndex] += 2;
                }
                if (tmpFrequencies.length > tmpAccumulatedFrequencies.length) {
                    tmpAccumulatedFrequencies = Arrays.copyOf(tmpAccumulatedFrequencies, tmpFrequencies.length);
                }
                for (int u = 0; u < tmpFrequencies.length; u++) {
                    tmpAccumulatedFrequencies[u] += tmpFrequencies[u];
                }
                tmpNumberOfAccumulations++;
            }
            // Calculate averaged frequencies
            double[] tmpAveragedFrequencies = new double[tmpAccumulatedFrequencies.length];
            for (int v = 0; v < tmpAccumulatedFrequencies.length; v++) {
                tmpAveragedFrequencies[v] = (double) tmpAccumulatedFrequencies[v] / (double) tmpNumberOfAccumulations;
            }
            tmpBinFrequencies[i][i] = tmpAveragedFrequencies;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="SECOND - Different particle types">
        for (int i = 1; i < tmpNumberOfParticleTypes; i++) {
            int tmpNumberOfParticlesOfTypeI = aParticlePositionMatrix[i].length;
            for (int j = 0; j < i; j++) {
                int tmpNumberOfParticlesOfTypeJ = aParticlePositionMatrix[j].length;
                int[] tmpAccumulatedFrequencies = new int[0];
                int[] tmpFrequencies = new int[0];
                int tmpNumberOfAccumulations = 0;
                for (int k = 0; k < tmpNumberOfParticlesOfTypeI; k++) {
                    PointInSpace tmpSinglePositionOfTypeI = aParticlePositionMatrix[i][k];
                    Arrays.fill(tmpFrequencies, 0);
                    for (int l = 0; l < tmpNumberOfParticlesOfTypeJ; l++) {
                        PointInSpace tmpSinglePositionOfTypeJ = aParticlePositionMatrix[j][l];
                        double tmpDistance = this.calculateDistance(tmpSinglePositionOfTypeI, tmpSinglePositionOfTypeJ);
                        int tmpBinIndex = this.getBinIndex(tmpDistance);
                        if (tmpBinIndex + 1 > tmpFrequencies.length) {
                            tmpFrequencies = Arrays.copyOf(tmpFrequencies, tmpBinIndex + 1);
                        }
                        tmpFrequencies[tmpBinIndex]++;
                    }
                    if (tmpFrequencies.length > tmpAccumulatedFrequencies.length) {
                        tmpAccumulatedFrequencies = Arrays.copyOf(tmpAccumulatedFrequencies, tmpFrequencies.length);
                    }
                    for (int u = 0; u < tmpFrequencies.length; u++) {
                        tmpAccumulatedFrequencies[u] += tmpFrequencies[u];
                    }
                    tmpNumberOfAccumulations++;
                }
                // Calculate averaged frequencies
                double[] tmpAveragedFrequencies = new double[tmpAccumulatedFrequencies.length];
                for (int v = 0; v < tmpAccumulatedFrequencies.length; v++) {
                    tmpAveragedFrequencies[v] = (double) tmpAccumulatedFrequencies[v] / (double) tmpNumberOfAccumulations;
                }
                tmpBinFrequencies[i][j] = tmpAveragedFrequencies;
            }
        }

        // </editor-fold>
        return tmpBinFrequencies;
    }

    /**
     * Returns the frequencies of the distance bins between particle types
     *
     * @param aParticlePositionMatrix Positions of all particles of simulation
     * box: Index 1 = Index of particle type, index 2 = Position of single
     * particle of specified type
     * @return Integer array or null if particle matrix distribution can not be
     * calculated. Index 1 of array = Index of particle type 1, index 2 = Index
     * of particle type 2, index 3 = index of distance segment (histogram bin)
     * starting with 0, Integer value = Frequency of particle of type 2 within
     * distance segment on average
     */
    public double[][][] getParticleParticleDistanceBinFrequencies_Old(PointInSpace[][] aParticlePositionMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePositionMatrix == null) {
            return null;
        }
        if (aParticlePositionMatrix.length < 1) {
            return null;
        }

        // </editor-fold>
        int tmpNumberOfParticleTypes = aParticlePositionMatrix.length;
        double[][][] tmpBinFrequencies = new double[tmpNumberOfParticleTypes][][];

        for (int i = 0; i < tmpNumberOfParticleTypes; i++) {
            tmpBinFrequencies[i] = new double[i + 1][];
            int tmpNumberOfParticlesOfTypeI = aParticlePositionMatrix[i].length;
            for (int j = 0; j <= i; j++) {
                int tmpNumberOfParticlesOfTypeJ = aParticlePositionMatrix[j].length;
                int[] tmpAccumulatedFrequencies = new int[0];
                int[] tmpFrequencies = new int[0];
                int tmpNumberOfAccumulations = 0;
                for (int k = 0; k < tmpNumberOfParticlesOfTypeI; k++) {
                    PointInSpace tmpSinglePositionOfTypeI = aParticlePositionMatrix[i][k];
                    Arrays.fill(tmpFrequencies, 0);
                    for (int l = 0; l < tmpNumberOfParticlesOfTypeJ; l++) {
                        PointInSpace tmpSinglePositionOfTypeJ = aParticlePositionMatrix[j][l];
                        if (i != j || k != l) {
                            double tmpDistance = this.calculateDistance(tmpSinglePositionOfTypeI, tmpSinglePositionOfTypeJ);
                            int tmpBinIndex = this.getBinIndex(tmpDistance);
                            if (tmpBinIndex + 1 > tmpFrequencies.length) {
                                tmpFrequencies = Arrays.copyOf(tmpFrequencies, tmpBinIndex + 1);
                            }
                            tmpFrequencies[tmpBinIndex]++;
                        }
                    }
                    if (tmpFrequencies.length > tmpAccumulatedFrequencies.length) {
                        tmpAccumulatedFrequencies = Arrays.copyOf(tmpAccumulatedFrequencies, tmpFrequencies.length);
                    }
                    for (int u = 0; u < tmpFrequencies.length; u++) {
                        tmpAccumulatedFrequencies[u] += tmpFrequencies[u];
                    }
                    tmpNumberOfAccumulations++;
                }
                // Calculate averaged frequencies
                double[] tmpAveragedFrequencies = new double[tmpAccumulatedFrequencies.length];
                for (int v = 0; v < tmpAccumulatedFrequencies.length; v++) {
                    tmpAveragedFrequencies[v] = (double) tmpAccumulatedFrequencies[v] / (double) tmpNumberOfAccumulations;
                }
                tmpBinFrequencies[i][j] = tmpAveragedFrequencies;
            }
        }
        return tmpBinFrequencies;
    }

    /**
     * Returns the segmented distribution of distances between a particle of
     * specific type and all other particles.
     *
     * @param aParticlePositionMatrix Positions of all particles of simulation
     * box: Index 1 = Index of particle type, index 2 = Position of single
     * particle of specified type
     * @return Integer array or null if particle matrix distribution can not be
     * calculated. Index 1 of array = Index of particle type, index 2 = Index of
     * single particle of type of index 1, index 3 = index of other particle
     * type (to which distances are calculated), index 4 = index of distance
     * segment
     */
    public int[][][][] getParticleDistanceDistribution(PointInSpace[][] aParticlePositionMatrix) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePositionMatrix == null) {
            return null;
        }
        if (aParticlePositionMatrix.length < 2) {
            return null;
        }

        // </editor-fold>
        int tmpNumberOfParticleTypes = aParticlePositionMatrix.length;
        int nStartParticle = 0; // Number of start particles in specific class
        int nEndParticle = 0; // Number of end particles in specific class
        double[][][][] distance = new double[tmpNumberOfParticleTypes][][][];
        for (int i = 0; i < tmpNumberOfParticleTypes; i++) {
            nStartParticle = aParticlePositionMatrix[i].length;
            distance[i] = new double[nStartParticle][][];
            for (int j = 0; j < nStartParticle; j++) {
                distance[i][j] = new double[tmpNumberOfParticleTypes][];
                for (int k = 0; k < tmpNumberOfParticleTypes; k++) {
                    if (i != k) {
                        nEndParticle = aParticlePositionMatrix[k].length;
                        distance[i][j][k] = new double[nEndParticle];
                    }
                }
            }
        }
        for (int i = 0; i < tmpNumberOfParticleTypes - 1; i++) {
            nStartParticle = aParticlePositionMatrix[i].length;
            for (int j = 0; j < nStartParticle; j++) {
                for (int k = i + 1; k < tmpNumberOfParticleTypes; k++) {
                    if (i != k) {
                        distance[i][j][k] = this.calculateDistances(aParticlePositionMatrix[i][j], aParticlePositionMatrix[k]);
                    }
                }
            }
        }

        // Copy all redundant information in to the matrix
        for (int i = tmpNumberOfParticleTypes - 1; i > 0; i--) {
            nStartParticle = aParticlePositionMatrix[i].length;
            for (int j = 0; j < nStartParticle; j++) {
                for (int k = i - 1; k >= 0; k--) {
                    if (i != k) {
                        nEndParticle = aParticlePositionMatrix[k].length;
                        for (int l = 0; l < nEndParticle; l++) {
                            distance[i][j][k][l] = distance[k][l][i][j];
                        }
                    }
                }
            }
        }

        // Determine distribution of distances
        int iParticle = 0;
        int nParticle = 0;
        int iDivision = 0;
        int nDistribution = 0;
        double distanceLimit = this.segmentLength;
        int[][][][] resultDistribution = new int[tmpNumberOfParticleTypes][][][];
        for (int i = 0; i < tmpNumberOfParticleTypes; i++) {
            nStartParticle = aParticlePositionMatrix[i].length;
            resultDistribution[i] = new int[nStartParticle][][];
            for (int j = 0; j < nStartParticle; j++) {
                resultDistribution[i][j] = new int[tmpNumberOfParticleTypes][];
                for (int k = 0; k < tmpNumberOfParticleTypes; k++) {
                    iParticle = 0;
                    iDivision = 0;
                    distanceLimit = this.segmentLength;
                    if (i != k) {
                        Arrays.sort(distance[i][j][k]);
                        nEndParticle = aParticlePositionMatrix[k].length;
                        nDistribution = (int) Math.ceil(distance[i][j][k][distance[i][j][k].length - 1] / this.segmentLength);
                        resultDistribution[i][j][k] = new int[nDistribution];
                        while (iParticle < nEndParticle) {
                            while (iParticle < nEndParticle && distance[i][j][k][iParticle] <= distanceLimit) {
                                iParticle++;
                                nParticle++;
                            }
                            resultDistribution[i][j][k][iDivision] = nParticle;
                            distanceLimit += this.segmentLength;
                            nParticle = 0;
                            iDivision++;
                        }
                    }
                }
            }
        }
        return resultDistribution;
    }
    // </editor-fold>

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Calculates the distances between a particle and another particle. NOTE:
     * PBC in all directions are assumed.
     *
     * @param aParticlePosition Position of particle
     * @param anotherParticlePosition Another particle position
     * @return Distance of particle to another particle
     */
    private double calculateDistance(PointInSpace aParticlePosition, PointInSpace anotherParticlePosition) {
        double tmpDx = aParticlePosition.getX() - anotherParticlePosition.getX();
        double tmpDy = aParticlePosition.getY() - anotherParticlePosition.getY();
        double tmpDz = aParticlePosition.getZ() - anotherParticlePosition.getZ();

        if (tmpDx > this.halfBoxLengthX) {
            tmpDx -= this.boxLengthX;
        } else if (tmpDx < (-this.halfBoxLengthX)) {
            tmpDx += this.boxLengthX;
        }
        if (tmpDy > this.halfBoxLengthY) {
            tmpDy -= this.boxLengthY;
        } else if (tmpDy < (-this.halfBoxLengthY)) {
            tmpDy += this.boxLengthY;
        }
        if (tmpDz > this.halfBoxLengthZ) {
            tmpDz -= this.boxLengthZ;
        } else if (tmpDz < (-this.halfBoxLengthZ)) {
            tmpDz += this.boxLengthZ;
        }

        return Math.sqrt(tmpDx * tmpDx + tmpDy * tmpDy + tmpDz * tmpDz);
    }

    /**
     * Calculates the distances between a particle and a set of other particles.
     * NOTE: PBC in all directions are assumed.
     *
     * @param aParticlePosition Position of particle
     * @param anotherParticlePositions Other particle positions
     * @return Distances of particle to other particles
     */
    private double[] calculateDistances(PointInSpace aParticlePosition, PointInSpace[] anotherParticlePositions) {
        int tmpNumberOfOtherParticles = anotherParticlePositions.length;
        double[] tmpDistances = new double[tmpNumberOfOtherParticles];

        for (int i = 0; i < tmpNumberOfOtherParticles; i++) {
            tmpDistances[i] = this.calculateDistance(aParticlePosition, anotherParticlePositions[i]);
        }
        return tmpDistances;
    }

    /**
     * Returns index of bin of distance according to segment length. Bin(0)
     * contains all distances from 0 to segment length, bin(1) all distances
     * from segment length to 2 segment lengths etc.
     *
     * @param aDistance
     * @return Index of bin of distance according to segment length
     */
    private int getBinIndex(double aDistance) {
        // aDistance and this.segmentLength are positive:
        return (int) (aDistance / this.segmentLength);
        // Slower:
        // return (int) Math.floor(aDistance / this.segmentLength);
    }
    // </editor-fold>
    
}
