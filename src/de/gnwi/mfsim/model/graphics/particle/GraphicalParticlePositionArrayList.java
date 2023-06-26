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
package de.gnwi.mfsim.model.graphics.particle;

import java.util.LinkedList;

/**
 * Array and list for GraphicalParticlePosition instances
 * 
 * @author Achim Zielesny
 */
public class GraphicalParticlePositionArrayList {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * List for GraphicalParticlePosition array instances
     */
    private final LinkedList<GraphicalParticlePosition[]> graphicalParticlePositionsList;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GraphicalParticlePosition array
     */
    private GraphicalParticlePosition[] graphicalParticlePositions;
    
    /**
     * Index
     */
    private int index;
    
    /**
     * Size (exclusive end index)
     */
    private int size;

    /**
     * Growth increment
     */
    private int growthIncrement;
    
    /**
     * Maximum index
     */
    private int maxIndex;
    
    /**
     * True: Array is consolidated, false: Otherwise
     */
    private boolean isConsolidated;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param anInitialCapacityAndGrowthIncrement Initial capacity and growth increment
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public GraphicalParticlePositionArrayList(int anInitialCapacityAndGrowthIncrement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anInitialCapacityAndGrowthIncrement < 1) {
            throw new IllegalArgumentException("GraphicalParticlePositionArrayList.Constructor: anInitialCapacityAndGrowthIncrement < 1.");
        }
        // </editor-fold>
        this.growthIncrement = anInitialCapacityAndGrowthIncrement;
        this.graphicalParticlePositionsList = new LinkedList<>();
        this.graphicalParticlePositions = new GraphicalParticlePosition[anInitialCapacityAndGrowthIncrement];
        this.maxIndex = anInitialCapacityAndGrowthIncrement - 1;
        this.reset();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Adds GraphicalParticlePosition instance
     * (No checks are performed)
     * 
     * @param aGraphicalParticlePosition GraphicalParticlePosition instance
     */
    public void add(GraphicalParticlePosition aGraphicalParticlePosition) {
        this.checkArraySize();
        this.graphicalParticlePositions[this.index] = aGraphicalParticlePosition;
        this.index++;
        this.size++;
    }

    /**
     * Adds graphical particle position
     * (No checks are performed)
     * 
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     * @param aParticleIndex Particle index
     * @param aMoleculeIndex Molecule index
     */
    public void add(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate,
        int aParticleIndex,
        int aMoleculeIndex
    ) {
        this.checkArraySize();
        if (this.graphicalParticlePositions[this.index] == null) {
            this.graphicalParticlePositions[this.index] = 
                new GraphicalParticlePosition(
                    aGraphicalParticle, 
                    aXCoordinate, 
                    aYCoordinate, 
                    aZCoordinate, 
                    aParticleIndex, 
                    aMoleculeIndex
                );
        } else {
            this.graphicalParticlePositions[this.index].reset(
                aGraphicalParticle, 
                aXCoordinate, 
                aYCoordinate, 
                aZCoordinate, 
                aParticleIndex, 
                aMoleculeIndex
            );
        }
        this.index++;
        this.size++;
    }

    /**
     * Adds graphical particle position
     * (No checks are performed)
     * 
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     * @param anIsInFrame True: Particle is in frame, false: Otherwise
     */
    public void add(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate,
        boolean anIsInFrame
    ) {
        this.checkArraySize();
        if (this.graphicalParticlePositions[this.index] == null) {
            this.graphicalParticlePositions[this.index] = 
                new GraphicalParticlePosition(
                    aGraphicalParticle, 
                    aXCoordinate, 
                    aYCoordinate, 
                    aZCoordinate, 
                    anIsInFrame
                );
        } else {
            this.graphicalParticlePositions[this.index].reset(
                aGraphicalParticle, 
                aXCoordinate, 
                aYCoordinate, 
                aZCoordinate,
                anIsInFrame
            );
        }
        this.index++;
        this.size++;
    }
    
    /**
     * Resets particle pair parameters
     */
    public final void reset() {
        this.graphicalParticlePositionsList.clear();
        this.index = 0;
        this.size = 0;
        // IMPORTANT: After reset the consolidation status is true 
        this.isConsolidated = true;
    }
    
    /**
     * Returns new GraphicalParticlePositionArrayList with cloned current 
     * graphical particle positions.
     * NOTE: This instance is NOT changed!
     *
     * @return New GraphicalParticlePositionArrayList with cloned current 
     * graphical particle positions.
     */
    public GraphicalParticlePositionArrayList getClone() {
        this.consolidate();
        GraphicalParticlePositionArrayList tmpClonedGraphicalParticlePositionArrayList = new GraphicalParticlePositionArrayList(this.maxIndex + 1);
        for (int i = 0; i < this.size; i++) {
            tmpClonedGraphicalParticlePositionArrayList.add(this.graphicalParticlePositions[i].getClone());
        }
        return tmpClonedGraphicalParticlePositionArrayList;
    }

    /**
     * GraphicalParticlePosition instances
     * NOTE: Size of returned array is this.size
     * 
     * @return GraphicalParticlePosition instances
     */
    public GraphicalParticlePosition[] getSizedGraphicalParticlePositions() {
        if (!this.isConsolidated) {
            this.consolidate();
        }
        GraphicalParticlePosition[] tmpSizedGraphicalParticlePositions = new GraphicalParticlePosition[this.size];
        System.arraycopy(
            this.graphicalParticlePositions, 
            0, 
            tmpSizedGraphicalParticlePositions, 
            0, 
            this.size
        );
        return tmpSizedGraphicalParticlePositions;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get)">
    /**
     * Size (exclusive end index)
     * 
     * @return Size (exclusive end index)
     */
    public int getSize() {
        return this.size;
    }

    /**
     * GraphicalParticlePosition instances
     * NOTE: Size of returned array is (this.maxIndex + 1) and NOT this.size
     * 
     * @return GraphicalParticlePosition instances
     */
    public GraphicalParticlePosition[] getGraphicalParticlePositions() {
        if (!this.isConsolidated) {
            this.consolidate();
        }
        return this.graphicalParticlePositions;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (set)">
    /**
     * Growth increment
     * 
     * @param aValue Growth increment
     */
    public void setGrowthIncrement(int aValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue < 1) {
            throw new IllegalArgumentException("GraphicalParticlePositionArrayList.setGrowthIncrement: aValue < 1.");
        }
        // </editor-fold>
        this.growthIncrement = aValue;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Checks array size and enlarges if necessary
     */
    private void checkArraySize() {
        if (this.index > this.maxIndex) {
            // IMPORTANT: Consolidation status is false
            this.isConsolidated = false;
            this.graphicalParticlePositionsList.add(this.graphicalParticlePositions);
            this.graphicalParticlePositions = new GraphicalParticlePosition[this.growthIncrement];
            this.maxIndex = this.growthIncrement - 1;
            this.index = 0;
        }
    }
    
    /**
     * Consolidates arrays
     * (No checks are performed)
     */
    private void consolidate() {
        if (!this.isConsolidated) {
            int tmpAccumulatedArraySize = 0;
            for (GraphicalParticlePosition[] tmpGraphicalParticlePosition : this.graphicalParticlePositionsList) {
                tmpAccumulatedArraySize += tmpGraphicalParticlePosition.length;
            }
            int tmpNewArraySize = tmpAccumulatedArraySize + this.growthIncrement;
            this.maxIndex = tmpNewArraySize - 1;
            this.graphicalParticlePositions = 
                this.getConsolidatedGraphicalParticlePositionArray(tmpNewArraySize,
                    this.graphicalParticlePositionsList,
                    this.graphicalParticlePositions
                );
            this.graphicalParticlePositionsList.clear();
            this.isConsolidated = true;
        }
    }

    /**
     * Consolidates GraphicalParticlePosition arrays
     * (No checks are performed)
     * 
     * @param tmpNewArraySize New array size
     * @param anArrayList Array list with arrays to consolidate
     * @param aLastArray Last array to be consolidated
     * @return Consolidated integer array
     */
    private GraphicalParticlePosition[] getConsolidatedGraphicalParticlePositionArray(
        int tmpNewArraySize,
        LinkedList<GraphicalParticlePosition[]> anArrayList,
        GraphicalParticlePosition[] aLastArray
    ) {
        GraphicalParticlePosition[] tmpNewArray = new GraphicalParticlePosition[tmpNewArraySize];
        int tmpIndex = 0;
        for (GraphicalParticlePosition[] tmpSingleArray : anArrayList) {
            System.arraycopy(
                tmpSingleArray, 
                0, 
                tmpNewArray, 
                tmpIndex, 
                tmpSingleArray.length
            );
            tmpIndex += tmpSingleArray.length;
        }
        System.arraycopy(
            aLastArray, 
            0, 
            tmpNewArray, 
            tmpIndex, 
            aLastArray.length
        );
        return tmpNewArray;
    }
    // </editor-fold>
        
}
