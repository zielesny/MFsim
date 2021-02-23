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

/**
 * Utility class for calculation of the average distance between particles in
 * the simulation box
 *
 * @author Mirco Daniel, Achim Zielesny
 */
public class DistanceCalculationUtils {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
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
     * True: Periodic boundary condition in in x-direction, false: Otherwise
     */
    private boolean isPeriodicBoundaryX;
    /**
     * True: Periodic boundary condition in in y-direction, false: Otherwise
     */
    private boolean isPeriodicBoundaryY;
    /**
     * True: Periodic boundary condition in in z-direction, false: Otherwise
     */
    private boolean isPeriodicBoundaryZ;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aBoxLengthX Length of box (x)
     * @param aBoxLengthY Width of box (y)
     * @param aBoxLengthZ Height of box (z)
     * @param anIsPeriodicBoundaryX True: Periodic boundary condition in in
     * x-direction, false: Otherwise
     * @param anIsPeriodicBoundaryY True: Periodic boundary condition in in
     * y-direction, false: Otherwise
     * @param anIsPeriodicBoundaryZ True: Periodic boundary condition in in
     * z-direction, false: Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public DistanceCalculationUtils(double aBoxLengthX, double aBoxLengthY, double aBoxLengthZ, boolean anIsPeriodicBoundaryX, boolean anIsPeriodicBoundaryY,
            boolean anIsPeriodicBoundaryZ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoxLengthX <= 0 || aBoxLengthY <= 0 || aBoxLengthZ <= 0) {
            throw new IllegalArgumentException("An argument is illegal");
        }

        // </editor-fold>
        this.boxLengthX = aBoxLengthX;
        this.boxLengthY = aBoxLengthY;
        this.boxLengthZ = aBoxLengthZ;
        this.isPeriodicBoundaryX = anIsPeriodicBoundaryX;
        this.isPeriodicBoundaryY = anIsPeriodicBoundaryY;
        this.isPeriodicBoundaryZ = anIsPeriodicBoundaryZ;

        this.halfBoxLengthX = 0.5 * this.boxLengthX;
        this.halfBoxLengthY = 0.5 * this.boxLengthY;
        this.halfBoxLengthZ = 0.5 * this.boxLengthZ;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns the average distance between equal particle-pair A-A
     *
     * @param aParticlePositions Positions of particles A
     * @return Average distance or -1.0 if equal particle-pair A-A average
     * distance can not be calculated
     */
    public double getEqualParticlePairAverageDistance(PointInSpace[] aParticlePositions) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePositions == null) {
            return -1.0;
        }
        if (aParticlePositions.length < 1) {
            return -1.0;
        }

        // </editor-fold>
        int tmpNumberOfParticlesOfTypeI = aParticlePositions.length;
        int tmpNumberOfParticlesOfTypeJ = aParticlePositions.length;

        // NOTE: Use long type for integer arithmetics to avoid possible overflow to NEGATIVE (!) minimum value Integer.MIN_VALUE!
        double tmpAccumulatedDistance = 0.0;
        long tmpNumberOfDistances = 0;
        for (int k = 0; k < tmpNumberOfParticlesOfTypeI; k++) {
            PointInSpace tmpSinglePositionOfTypeI = aParticlePositions[k];
            for (int l = k + 1; l < tmpNumberOfParticlesOfTypeJ; l++) {
                PointInSpace tmpSinglePositionOfTypeJ = aParticlePositions[l];
                double tmpDistance = this.calculateDistance(tmpSinglePositionOfTypeI, tmpSinglePositionOfTypeJ);
                tmpAccumulatedDistance += tmpDistance;
                tmpNumberOfDistances++;
            }
        }
        return tmpAccumulatedDistance / (double) tmpNumberOfDistances;
    }

    /**
     * Returns the average distance between different particle-pair A-B
     *
     * @param aParticlePositionsA Positions of particles A
     * @param aParticlePositionsB Positions of particles B
     * @return Average distance or -1.0 if different particle-pair A-B
     * distribution can not be calculated
     */
    public double getDifferentParticlePairAverageDistance(PointInSpace[] aParticlePositionsA, PointInSpace[] aParticlePositionsB) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticlePositionsA == null) {
            return -1.0;
        }
        if (aParticlePositionsA.length < 1) {
            return -1.0;
        }
        if (aParticlePositionsB == null) {
            return -1.0;
        }
        if (aParticlePositionsB.length < 1) {
            return -1.0;
        }

        // </editor-fold>
        int tmpNumberOfParticlesOfTypeI = aParticlePositionsA.length;
        int tmpNumberOfParticlesOfTypeJ = aParticlePositionsB.length;

        // NOTE: Use long type for integer arithmetics to avoid possible overflow to NEGATIVE (!) minimum value Integer.MIN_VALUE!
        double tmpAccumulatedDistance = 0.0;
        long tmpNumberOfDistances = 0;
        for (int k = 0; k < tmpNumberOfParticlesOfTypeI; k++) {
            PointInSpace tmpSinglePositionOfTypeI = aParticlePositionsA[k];
            for (int l = 0; l < tmpNumberOfParticlesOfTypeJ; l++) {
                PointInSpace tmpSinglePositionOfTypeJ = aParticlePositionsB[l];
                double tmpDistance = this.calculateDistance(tmpSinglePositionOfTypeI, tmpSinglePositionOfTypeJ);
                tmpAccumulatedDistance += tmpDistance;
                tmpNumberOfDistances++;
            }
        }
        return tmpAccumulatedDistance / (double) tmpNumberOfDistances;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Calculates the distances between a particle and another particle
     *
     * @param aParticlePosition Position of particle
     * @param anotherParticlePosition Another particle position
     * @return Distance of particle to another particle
     */
    private double calculateDistance(PointInSpace aParticlePosition, PointInSpace anotherParticlePosition) {
        double tmpDx = aParticlePosition.getX() - anotherParticlePosition.getX();
        double tmpDy = aParticlePosition.getY() - anotherParticlePosition.getY();
        double tmpDz = aParticlePosition.getZ() - anotherParticlePosition.getZ();

        if (this.isPeriodicBoundaryX) {
            if (tmpDx > this.halfBoxLengthX) {
                tmpDx -= this.boxLengthX;
            } else if (tmpDx < (-this.halfBoxLengthX)) {
                tmpDx += this.boxLengthX;
            }
        }
        if (this.isPeriodicBoundaryY) {
            if (tmpDy > this.halfBoxLengthY) {
                tmpDy -= this.boxLengthY;
            } else if (tmpDy < (-this.halfBoxLengthY)) {
                tmpDy += this.boxLengthY;
            }
        }
        if (this.isPeriodicBoundaryZ) {
            if (tmpDz > this.halfBoxLengthZ) {
                tmpDz -= this.boxLengthZ;
            } else if (tmpDz < (-this.halfBoxLengthZ)) {
                tmpDz += this.boxLengthZ;
            }
        }

        return Math.sqrt(tmpDx * tmpDx + tmpDy * tmpDy + tmpDz * tmpDz);
    }
    // </editor-fold>

}
