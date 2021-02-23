/**
 * SPICES (Simplified Particle Input ConnEction Specification)
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
package de.gnwi.mfsim.model.graphics.particle;

import de.gnwi.spices.PointInSpace;

/**
 * Position of a specific graphical particle in the simulation box
 *
 * @author Stefan Neumann, Achim Zielesny
 *
 */
public class GraphicalParticlePosition extends PointInSpace implements IGraphicalParticlePosition {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Graphical particle
     */
    private IGraphicalParticle graphicalParticle;

    /**
     * True: Particle is in bulk, false: Otherwise
     */
    private boolean isInBulk;

    /**
     * True: Particle is in frame, false: Otherwise
     */
    private boolean isInFrame;
    
    /**
     * Particle index
     */
    private int particleIndex;
    
    /**
     * Molecule index
     */
    private int moleculeIndex;

    /**
     * X-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     */
    private int middlePositionInPixelX;

    /**
     * Y-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     */
    private int middlePositionInPixelY;

    /**
     * Z-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     */
    private int middlePositionInPixelZ;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public constructors">
    /**
     * Constructor
     *
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     *
     */
    public GraphicalParticlePosition(
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate
    ) {
        super(aXCoordinate, aYCoordinate, aZCoordinate);
        this.graphicalParticle = null;
        // NOTE: "-1" means that this.particleIndex and this.moleculeIndex are 
        //       NOT defined.
        this.particleIndex = -1;
        this.moleculeIndex = -1;
        this.initialize();
    }

    /**
     * Constructor
     *
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     *
     */
    public GraphicalParticlePosition(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate
    ) {
        super(aXCoordinate, aYCoordinate, aZCoordinate);
        this.graphicalParticle = aGraphicalParticle;
        // NOTE: "-1" means that this.particleIndex and this.moleculeIndex are 
        //       NOT defined.
        this.particleIndex = -1;
        this.moleculeIndex = -1;
        this.initialize();
    }
    
    /**
     * Constructor
     *
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     * @param anIsInFrame True: Particle is in frame, false: Otherwise
     */
    public GraphicalParticlePosition(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate,
        boolean anIsInFrame
    ) {
        super(aXCoordinate, aYCoordinate, aZCoordinate);
        this.graphicalParticle = aGraphicalParticle;
        this.isInFrame = anIsInFrame;
        // NOTE: "-1" means that this.particleIndex and this.moleculeIndex are 
        //       NOT defined.
        this.particleIndex = -1;
        this.moleculeIndex = -1;
        this.initializeWithoutFrame();
    }

    /**
     * Constructor
     *
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     * @param aParticleIndex Particle index
     * @param aMoleculeIndex Molecule index
     */
    public GraphicalParticlePosition(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate,
        int aParticleIndex,
        int aMoleculeIndex
    ) {
        super(aXCoordinate, aYCoordinate, aZCoordinate);
        this.graphicalParticle = aGraphicalParticle;
        this.particleIndex = aParticleIndex;
        this.moleculeIndex = aMoleculeIndex;
        this.initialize();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private constructors">
    /**
     * Constructor
     *
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     * @param aMiddlePositionInPixelX X-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     * @param aMiddlePositionInPixelY Y-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     * @param aMiddlePositionInPixelZ Z-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     * @param anIsInBulk True: Particle is in bulk, false: Otherwise
     * @param anIsInFrame True: Particle is in frame, false: Otherwise
     * @param aParticleIndex Particle index
     * @param aMoleculeIndex Molecule index
     */
    private GraphicalParticlePosition(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate,
        int aMiddlePositionInPixelX, 
        int aMiddlePositionInPixelY, 
        int aMiddlePositionInPixelZ, 
        boolean anIsInBulk, 
        boolean anIsInFrame,
        int aParticleIndex,
        int aMoleculeIndex
    ) {
        super(aXCoordinate, aYCoordinate, aZCoordinate);
        this.graphicalParticle = aGraphicalParticle;
        this.middlePositionInPixelX = aMiddlePositionInPixelX;
        this.middlePositionInPixelY = aMiddlePositionInPixelY;
        this.middlePositionInPixelZ = aMiddlePositionInPixelZ;
        this.isInBulk = anIsInBulk;
        this.isInFrame = anIsInFrame;
        this.particleIndex = aParticleIndex;
        this.moleculeIndex = aMoleculeIndex;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns cloned graphical particle position with reference to the same (!) graphical particle and pixels instance
     *
     * @return Cloned graphical particle position with reference to the same (!) graphical particle and pixels instance
     */
    @Override
    public GraphicalParticlePosition getClone() {
        return new GraphicalParticlePosition(
            this.graphicalParticle, 
            this.xCoordinate, 
            this.yCoordinate, 
            this.zCoordinate, 
            this.middlePositionInPixelX,
            this.middlePositionInPixelY, 
            this.middlePositionInPixelZ, 
            this.isInBulk, 
            this.isInFrame,
            this.particleIndex,
            this.moleculeIndex
        );
    }
    
    /**
     * Resets this instance with supplied parameters
     * 
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     * @param aParticleIndex Particle index
     * @param aMoleculeIndex Molecule index
     */
    public void reset(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate,
        int aParticleIndex,
        int aMoleculeIndex
    ) {
        this.graphicalParticle = aGraphicalParticle;
        this.xCoordinate = aXCoordinate;
        this.yCoordinate = aYCoordinate;
        this.zCoordinate = aZCoordinate;
        this.particleIndex = aParticleIndex;
        this.moleculeIndex = aMoleculeIndex;
        this.initialize();
    }
    
    /**
     * Resets this instance with supplied parameters
     * 
     * @param aGraphicalParticle Graphical particle
     * @param aXCoordinate X coordinate of point in space, not allowed to be negative
     * @param aYCoordinate Y coordinate of point in space, not allowed to be negative
     * @param aZCoordinate Z coordinate of point in space, not allowed to be negative
     * @param anIsInFrame True: Particle is in frame, false: Otherwise
     */
    public void reset(
        IGraphicalParticle aGraphicalParticle, 
        double aXCoordinate, 
        double aYCoordinate, 
        double aZCoordinate,
        boolean anIsInFrame
    ) {
        this.graphicalParticle = aGraphicalParticle;
        this.xCoordinate = aXCoordinate;
        this.yCoordinate = aYCoordinate;
        this.zCoordinate = aZCoordinate;
        this.particleIndex = -1;
        this.moleculeIndex = -1;
        this.isInFrame = anIsInFrame;
        this.initializeWithoutFrame();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- GraphicalParticle">
    /**
     * Graphical particle
     *
     * @return Graphical particle
     */
    @Override
    public IGraphicalParticle getGraphicalParticle() {
        return this.graphicalParticle;
    }

    /**
     * Graphical particle
     *
     * @param aGraphicalParticle Graphical particle
     */
    @Override
    public void setGraphicalParticle(IGraphicalParticle aGraphicalParticle) {
        this.graphicalParticle = aGraphicalParticle;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MiddlePositionInPixel">
    /**
     * X-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     * @return X-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     */
    public int getMiddlePositionInPixelX() {
        return this.middlePositionInPixelX;
    }

    /**
     * X-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     * @param aValue X-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     */
    public void setMiddlePositionInPixelX(int aValue) {
        this.middlePositionInPixelX = aValue;
    }

    /**
     * Y-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     * @return Y-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     */
    public int getMiddlePositionInPixelY() {
        return this.middlePositionInPixelY;
    }

    /**
     * Y-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     * @param aValue Y-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     */
    public void setMiddlePositionInPixelY(int aValue) {
        this.middlePositionInPixelY = aValue;
    }

    /**
     * Z-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     * @return Z-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     */
    public int getMiddlePositionInPixelZ() {
        return this.middlePositionInPixelZ;
    }

    /**
     * Z-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     *
     * @param aValue Z-value of the middle position of the rectangular region of the graphical particle/sphere in pixel
     */
    public void setMiddlePositionInPixelZ(int aValue) {
        this.middlePositionInPixelZ = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- InBulk">
    /**
     * True: Particle is in bulk, false: Otherwise
     *
     * @return True: Particle is in bulk, false: Otherwise
     */
    public boolean isInBulk() {
        return this.isInBulk;
    }

    /**
     * True: Particle is in bulk, false: Otherwise
     *
     * @param aValue True: Particle is in bulk, false: Otherwise
     */
    public void setInBulk(boolean aValue) {
        this.isInBulk = aValue;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get)">
    /**
     * Particle index
     *
     * @return Particle index
     */
    public int getParticleIndex() {
        return this.particleIndex;
    }

    /**
     * Molecule index
     *
     * @return Molecule index
     */
    public int getMoleculeIndex() {
        return this.moleculeIndex;
    }

    /**
     * True: Particle is in frame, false: Otherwise
     *
     * @return True: Particle is in frame, false: Otherwise
     */
    public boolean isInFrame() {
        return this.isInFrame;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialise
     */
    private void initialize() {
        this.isInFrame = false;
        this.initializeWithoutFrame();
    }

    /**
     * Initialise
     */
    private void initializeWithoutFrame() {
        this.isInBulk = false;
        this.middlePositionInPixelX = -1;
        this.middlePositionInPixelY = -1;
        this.middlePositionInPixelZ = -1;
    }
    // </editor-fold>

}
