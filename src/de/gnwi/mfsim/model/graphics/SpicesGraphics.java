/**
 * SPICES (Simplified Particle Input ConnEction Specification)
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
package de.gnwi.mfsim.model.graphics;

import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;
import de.gnwi.spices.Spices;
import de.gnwi.spices.PointInSpace;
import java.util.HashMap;

/**
 * Spices for graphics
 *
 * @author Mirco Daniel, Achim Zielesny
 */
public class SpicesGraphics extends Spices {

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (NOT allowed to be a monomer
     * or to contain monomer shortcuts)
     */
    public SpicesGraphics(String anInputStructure) {
        super(anInputStructure);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (NOT allowed to be a monomer
     * or to contain monomer shortcuts)
     * @param anAvailableParticles Available particles
     */
    public SpicesGraphics(String anInputStructure, HashMap<String, String> anAvailableParticles) {
        super(anInputStructure, anAvailableParticles);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     */
    public SpicesGraphics(String anInputStructure, boolean anIsMonomer) {
        super(anInputStructure, anIsMonomer);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     */
    public SpicesGraphics(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer) {
        super(anInputStructure, anAvailableParticles, anIsMonomer);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     */
    public SpicesGraphics(String anInputStructure, boolean anIsMonomer, int aStartIndex) {
        super(anInputStructure, anIsMonomer, aStartIndex);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     */
    public SpicesGraphics(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer, int aStartIndex) {
        super(anInputStructure, anAvailableParticles, anIsMonomer, aStartIndex);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     * @param aFirstParticle Cartesian coordinate of a single first particle
     * @param aLastParticle Cartesian coordinate of a single last particle
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public SpicesGraphics(String anInputStructure, boolean anIsMonomer, int aStartIndex, PointInSpace aFirstParticle, PointInSpace aLastParticle, double aBondLength) {
        super(anInputStructure, anIsMonomer, aStartIndex, aFirstParticle, aLastParticle, aBondLength);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     * @param aFirstParticle Cartesian coordinate of a single first particle
     * @param aLastParticle Cartesian coordinate of a single last particle
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public SpicesGraphics(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer, int aStartIndex, PointInSpace aFirstParticle,
            PointInSpace aLastParticle, double aBondLength) {
        super(anInputStructure, anAvailableParticles, anIsMonomer, aStartIndex, aFirstParticle, aLastParticle, aBondLength);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (NOT allowed to be a monomer
     * or to contain monomer shortcuts)
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     * @param aFirstParticle Cartesian coordinates of first particles
     * @param aLastParticle Cartesian coordinates of last particles
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public SpicesGraphics(String anInputStructure, int aStartIndex, PointInSpace[] aFirstParticle, PointInSpace[] aLastParticle, double aBondLength) {
        super(anInputStructure, aStartIndex, aFirstParticle, aLastParticle, aBondLength);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     * @param aFirstParticle Cartesian coordinates of first particles
     * @param aLastParticle Cartesian coordinates of last particles
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public SpicesGraphics(String anInputStructure, boolean anIsMonomer, int aStartIndex, PointInSpace[] aFirstParticle, PointInSpace[] aLastParticle,
            double aBondLength) {
        super(anInputStructure, anIsMonomer, aStartIndex, aFirstParticle, aLastParticle, aBondLength);
    }

    /**
     * ** Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     * @param aFirstParticle Cartesian coordinates of first particles
     * @param aLastParticle Cartesian coordinates of last particles
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public SpicesGraphics(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer, int aStartIndex, PointInSpace[] aFirstParticle,
            PointInSpace[] aLastParticle, double aBondLength) {
        super(anInputStructure, anAvailableParticles, anIsMonomer, aStartIndex, aFirstParticle, aLastParticle, aBondLength);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public GraphicalParticlePosition related methods">
    /**
     * Returns coordinates of particles
     *
     * @param aFirstParticleCoordinate Coordinate of the first particle
     * @param aLastParticleCoordinate Coordinate of the last particle
     * @param aBondLength BondLength
     * @return Coordinates of the particles (index of particle can be examined
     * by getParticlesIndex() method). First index is index of molecular
     * structure, second index is index of particle. If PointInSpaceInterface is
     * unknown or different in aFirstParticleCoordinate and
     * aLastParticleCoordinate null is returned.
     */
    public GraphicalParticlePosition[][] getParticleCoordinates(GraphicalParticlePosition aFirstParticleCoordinate, GraphicalParticlePosition aLastParticleCoordinate,
            double aBondLength) {
        return (GraphicalParticlePosition[][])this.getParticleCoordinates(new PointInSpace[]{aFirstParticleCoordinate},
                    new PointInSpace[]{aLastParticleCoordinate}, aBondLength);
    }

    /**
     * Returns graphical particle positions of particles
     *
     * @param aParticleToGraphicalParticleMap HashMap that maps particle to its
     * corresponding graphicalParticle
     * @param aFirstParticleCoordinate Coordinate of the first particle
     * @param aLastParticleCoordinate Coordinate of the last particle
     * @param aBondLength BondLength
     * @return Array of arrays of GraphicalParticlePositions. The index of a
 particle can be examined by getParticlesIndex() method. First index is
 the index of the molecular structure, second index is the index of the
 GraphicalParticlePosition object for the particle position.
     */
    public GraphicalParticlePosition[][] getParticleCoordinates(HashMap<String, IGraphicalParticle> aParticleToGraphicalParticleMap,
            GraphicalParticlePosition aFirstParticleCoordinate, 
            GraphicalParticlePosition aLastParticleCoordinate, double aBondLength) {
        return this.getParticleCoordinates(aParticleToGraphicalParticleMap, new GraphicalParticlePosition[]{aFirstParticleCoordinate},
                new GraphicalParticlePosition[]{aLastParticleCoordinate}, aBondLength);
    }

    /**
     * Returns graphical particle positions of particles
     *
     * @param aParticleToGraphicalParticleMap HashMap that maps particle to its
     * corresponding graphicalParticle
     * @param aFirstParticleCoordinates Coordinates of the first particle
     * @param aLastParticleCoordinates Coordinates of the last particle
     * @param aBondLength BondLength
     * @return Array of arrays of GraphicalParticlePositions. The index of a
 particle can be examined by getParticlesIndex() method. First index is
 the index of the molecular structure, second index is the index of the
 GraphicalParticlePosition object for the particle position.
     */
    public GraphicalParticlePosition[][] getParticleCoordinates(HashMap<String, IGraphicalParticle> aParticleToGraphicalParticleMap,
            GraphicalParticlePosition[] aFirstParticleCoordinates, 
            GraphicalParticlePosition[] aLastParticleCoordinates, double aBondLength) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.inputStructure == null || this.inputStructure.isEmpty()) {
            return null;
        }
        if (aParticleToGraphicalParticleMap == null || aParticleToGraphicalParticleMap.isEmpty()) {
            return null;
        }
        if (aFirstParticleCoordinates == null || aFirstParticleCoordinates.length == 0) {
            return null;
        }
        if (aLastParticleCoordinates == null || aLastParticleCoordinates.length == 0) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        int tmpPartsCount;
        int tmpPartLength;
        int tmpTargetIndexPosition = 0;
        GraphicalParticlePosition[][] tmpGraphicalParticlePositions = new GraphicalParticlePosition[aFirstParticleCoordinates.length][];
        tmpPartsCount = this.partOfSpices.length;
        for (int i = 0; i < aFirstParticleCoordinates.length; i++) {
            tmpGraphicalParticlePositions[i] = new GraphicalParticlePosition[this.numberOfTotalParticles];
        }
        // </editor-fold>
        if (this.numberOfTotalParticles == 1) {
            // <editor-fold defaultstate="collapsed" desc="1 particle only in molecular structure">
            for (int i = 0; i < aFirstParticleCoordinates.length; i++) {
                aFirstParticleCoordinates[i].setGraphicalParticle(aParticleToGraphicalParticleMap.get(this.partOfSpices[0].getInnerParticles()[0]));
                tmpGraphicalParticlePositions[i] = new GraphicalParticlePosition[]{aFirstParticleCoordinates[i]};
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Several particles in molecular structure">
            for (int i = 0; i < tmpPartsCount; i++) {
                tmpPartLength = this.partOfSpices[i].getInnerParticles().length;
                for (int j = 0; j < aFirstParticleCoordinates.length; j++) {
                    GraphicalParticlePosition[] tmpInterimResult = new GraphicalParticlePosition[aFirstParticleCoordinates.length];
                    if (this.partOfSpices[i].getInnerParticles().length == 1) {
                        aFirstParticleCoordinates[j].setGraphicalParticle(aParticleToGraphicalParticleMap.get(this.partOfSpices[i].getInnerParticles()[0]));
                        tmpInterimResult = new GraphicalParticlePosition[]{aFirstParticleCoordinates[j]};
                    } else {
                        tmpInterimResult = this.getCoordinatesOfTokens(this, i, aParticleToGraphicalParticleMap,
                                aFirstParticleCoordinates[j], aLastParticleCoordinates[j], aBondLength);
                    }
                    System.arraycopy(tmpInterimResult, 0, tmpGraphicalParticlePositions[j], tmpTargetIndexPosition, tmpInterimResult.length);
                }
                tmpTargetIndexPosition += tmpPartLength;
            }
            // </editor-fold>
        }
        return tmpGraphicalParticlePositions;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private GraphicalParticlePosition related methods">
    /**
     * Get coordinate of tokens. NOTE: No checks are performed due to
     * performance reasons.
     *
     * @param aSpices: A Spices
     * @param aPartIndex: Index of the part
     * @param aParticleToGraphicalParticleMap HashMap that maps particle to its
     * corresponding graphicalParticle
     * @param aFirstParticleCoordinate First particle coordinate
     * @param aLastParticleCoordinate Last particle coordinate
     * @param aBondLength Bond length
     * @return GraphicalParticlePosition of each particle of the chemical
 structure that is represented by aTokens
     */
    private GraphicalParticlePosition[] getCoordinatesOfTokens(
        SpicesGraphics aSpices, 
        int aPartIndex,
        HashMap<String, IGraphicalParticle> aParticleToGraphicalParticleMap, 
        GraphicalParticlePosition aFirstParticleCoordinate,
        GraphicalParticlePosition aLastParticleCoordinate, 
        double aBondLength) {
        int[] tmpParticleIndices = aSpices.partOfSpices[aPartIndex].getInnerParticleIndices();
        PointInSpace tmpFirstParticleCoordinate = (PointInSpace)aFirstParticleCoordinate;
        PointInSpace tmpLastParticleCoordinate = (PointInSpace)aLastParticleCoordinate;        
        PointInSpace[] tmpParticleCoordinates = this.getCoordinatesOfTokens(aSpices, aPartIndex, tmpFirstParticleCoordinate, tmpLastParticleCoordinate, aBondLength);
        GraphicalParticlePosition[] tmpParticlePosition = new GraphicalParticlePosition[tmpParticleCoordinates.length];
        
        for (int i = 0; i < tmpParticleCoordinates.length; i++) {
            tmpParticlePosition[i] =new GraphicalParticlePosition(
                aParticleToGraphicalParticleMap.get(aSpices.outerStructureTokens[tmpParticleIndices[i]]),
                tmpParticleCoordinates[i].getX(),
                tmpParticleCoordinates[i].getY(),
                tmpParticleCoordinates[i].getZ()
            );
        }
        return tmpParticlePosition;
    }
    // </editor-fold>

}
