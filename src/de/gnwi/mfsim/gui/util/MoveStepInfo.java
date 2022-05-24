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
package de.gnwi.mfsim.gui.util;

import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import java.awt.image.BufferedImage;
import de.gnwi.mfsim.model.preference.Preferences;

/**
 * Info for single move step
 *
 * @author Achim Zielesny
 */
public class MoveStepInfo {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Graphical particle position info
     */
    private GraphicalParticlePositionInfo graphicalParticlePositionInfo;

    /**
     * Simulation box view
     */
    private SimulationBoxViewEnum boxView;

    /**
     * Index for simulation box view
     */
    private int boxViewIndex;

    /**
     * Full pathname of image file for move step
     */
    private String moveStepImageFilePathname;

    /**
     * Compressed move step image
     */
    private byte[] moveStepImageByteArray;

    /**
     * Move step image
     */
    private BufferedImage moveStepImage;

    /**
     * Angle for rotation around x axis in degree
     */
    private double rotationAroundXaxisAngle;

    /**
     * Angle for rotation around y axis in degree
     */
    private double rotationAroundYaxisAngle;

    /**
     * Angle for rotation around z axis in degree
     */
    private double rotationAroundZaxisAngle;

    /**
     * Particle shift in percent of simulation box length along x-axis
     */
    private double particleShiftX;

    /**
     * Particle shift in percent of simulation box length along y-axis
     */
    private double particleShiftY;

    /**
     * Particle shift in percent of simulation box length along z-axis
     */
    private double particleShiftZ;

    /**
     * X-shift in pixel
     */
    private int xShiftInPixel;

    /**
     * X-shift in pixel
     */
    private int yShiftInPixel;

    /**
     * Simulation box magnification percentage
     */
    private double simulationBoxMagnificationPercentage;

    /**
     * Depth attenuation of slices for depth impression in simulation box of
     * slicer. 0.0 = Minimum (no attenuation)
     */
    private double depthAttenuationSlicer;

    /**
     * Exclusion box size info
     */
    private BoxSizeInfo exclusionBoxSizeInfo;

    /**
     * Index of first slice for simulation box slicer
     */
    private int firstSliceIndex;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aBoxViewIndex Index for simulation box view
     */
    public MoveStepInfo(int aBoxViewIndex) {
        // NOTE: No checks are performed!
        this.boxViewIndex = aBoxViewIndex;
        this.rotationAroundXaxisAngle = 0.0;
        this.rotationAroundYaxisAngle = 0.0;
        this.rotationAroundZaxisAngle = 0.0;
        this.particleShiftX = 0.0;
        this.particleShiftY = 0.0;
        this.particleShiftZ = 0.0;
        this.xShiftInPixel = 0;
        this.yShiftInPixel = 0;
        this.simulationBoxMagnificationPercentage = 0.0;
        this.depthAttenuationSlicer = 0.0;
        this.exclusionBoxSizeInfo = null;
        this.firstSliceIndex = 0;
        this.clearData();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns if this instance contains image information
     *
     * @return True: This instance contains image information, false: Otherwise
     */
    public boolean hasImageInformation() {
        return this.moveStepImageFilePathname != null || this.moveStepImageByteArray != null || this.moveStepImage != null;
    }

    /**
     * Returns image
     *
     * @return Image or null if image is not available
     */
    public BufferedImage getImage() {
        switch (Preferences.getInstance().getImageStorageMode()) {
            case HARDDISK_COMPRESSED:
                if (this.moveStepImageFilePathname != null) {
                    return GraphicsUtils.readImageFromFile(this.moveStepImageFilePathname);
                } else {
                    return null;
                }
            case MEMORY_COMPRESSED:
                if (this.moveStepImageByteArray != null) {
                    try {
                        return GraphicsUtils.convertJpegEncodedByteArrayToBufferedImage(this.moveStepImageByteArray);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        return null;
                    }
                } else {
                    return null;
                }
            case MEMORY_UNCOMPRESSED:
                if (this.moveStepImage != null) {
                    return this.moveStepImage;
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * Clears data
     */
    public void clearData() {
        this.boxView = null;
        this.moveStepImageFilePathname = null;
        this.moveStepImageByteArray = null;
        this.moveStepImage = null;
        this.graphicalParticlePositionInfo = null;
        // NOTE: Do NOT clear this.rotationAroundXaxisAngle, this.rotationAroundYaxisAngle and this.rotationAroundZaxisAngle
        // NOTE: Do NOT clear this.particleShiftX, particleShiftY and this.particleShiftZ
        // NOTE: Do NOT clear this.xShiftChange and this.yShiftChange
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- BoxView">
    /**
     * Returns simulation box view
     *
     * @return Simulation box view
     */
    public SimulationBoxViewEnum getBoxView() {
        return this.boxView;
    }

    /**
     * Simulation box view
     *
     * @param aBoxView Simulation box view
     */
    public void setBoxView(SimulationBoxViewEnum aBoxView) {
        this.boxView = aBoxView;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- BoxViewIndex">
    /**
     * Returns index for simulation box view
     *
     * @return Index for simulation box view
     */
    public int getBoxViewIndex() {
        return this.boxViewIndex;
    }

    /**
     * Index for simulation box view
     *
     * @param aBoxViewIndex Index for simulation box view
     */
    public void setBoxViewIndex(int aBoxViewIndex) {
        this.boxViewIndex = aBoxViewIndex;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- StepImageFilePathname">
    /**
     * Returns full pathname of image file for simulation step
     *
     * @return Full pathname of image file for simulation step
     */
    public String getStepImageFilePathname() {
        return this.moveStepImageFilePathname;
    }

    /**
     * Full pathname of image file for simulation step
     *
     * @param aStepImageFilePathname Full pathname of image file for simulation
     * step
     */
    public void setStepImageFilePathname(String aStepImageFilePathname) {
        this.moveStepImageFilePathname = aStepImageFilePathname;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- StepImageByteArray">
    /**
     * Returns compressed step image
     *
     * @return Compressed step image
     */
    public byte[] getStepImageByteArray() {
        return this.moveStepImageByteArray;
    }

    /**
     * Compressed step image
     *
     * @param aStepImageByteArray Compressed step image
     */
    public void setStepImageByteArray(byte[] aStepImageByteArray) {
        this.moveStepImageByteArray = aStepImageByteArray;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- StepImage">
    /**
     * Returns step image
     *
     * @return Step image
     */
    public BufferedImage getStepImage() {
        return this.moveStepImage;
    }

    /**
     * Step image
     *
     * @param aStepImage Step image
     */
    public void setStepImage(BufferedImage aStepImage) {
        this.moveStepImage = aStepImage;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundXaxisAngle">
    /**
     * Angle for rotation around x axis in degree
     *
     * @return Angle for rotation around x axis in degree
     */
    public double getRotationAroundXaxisAngle() {
        return this.rotationAroundXaxisAngle;
    }

    /**
     * Default angle for rotation around x axis in degree
     *
     * @param aValue Value
     */
    public void setRotationAroundXaxisAngle(double aValue) {
        this.rotationAroundXaxisAngle = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundYaxisAngle">
    /**
     * Angle for rotation around y axis in degree
     *
     * @return Angle for rotation around y axis in degree
     */
    public double getRotationAroundYaxisAngle() {
        return this.rotationAroundYaxisAngle;
    }

    /**
     * Default angle for rotation around y axis in degree
     *
     * @param aValue Value
     */
    public void setRotationAroundYaxisAngle(double aValue) {
        this.rotationAroundYaxisAngle = aValue;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundZaxisAngle">
    /**
     * Angle for rotation around z axis in degree
     *
     * @return Angle for rotation around z axis in degree
     */
    public double getRotationAroundZaxisAngle() {
        return this.rotationAroundZaxisAngle;
    }

    /**
     * Angle for rotation around z axis in degree
     *
     * @param aValue Value
     */
    public void setRotationAroundZaxisAngle(double aValue) {
        this.rotationAroundZaxisAngle = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleShiftX">
    /**
     * Particle shift in percent of simulation box length along x-axis
     *
     * @return Particle shift in percent of simulation box length along x-axis
     */
    public double getParticleShiftX() {
        return this.particleShiftX;
    }

    /**
     * Particle shift in percent of simulation box length along x-axis
     *
     * @param aValue Value
     */
    public void setParticleShiftX(double aValue) {
        this.particleShiftX = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleShiftY">
    /**
     * Particle shift in percent of simulation box length along y-axis
     *
     * @return Particle shift in percent of simulation box length along y-axis
     */
    public double getParticleShiftY() {
        return this.particleShiftY;
    }

    /**
     * Particle shift in percent of simulation box length along y-axis
     *
     * @param aValue Value
     */
    public void setParticleShiftY(double aValue) {
        this.particleShiftY = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleShiftZ">
    /**
     * Particle shift in percent of simulation box length along z-axis
     *
     * @return Particle shift in percent of simulation box length along z-axis
     */
    public double getParticleShiftZ() {
        return this.particleShiftZ;
    }

    /**
     * Particle shift in percent of simulation box length along z-axis
     *
     * @param aValue Value
     */
    public void setParticleShiftZ(double aValue) {
        this.particleShiftZ = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XshiftInPixel">
    /**
     * X-shift in pixel
     * 
     * @param aValue A value
     */
    public void setXshiftInPixel(int aValue) {
        this.xShiftInPixel = aValue;
    }

    /**
     * X-shift in pixel
     * 
     * @return X-shift in pixel
     */
    public int getXshiftInPixel() {
        return this.xShiftInPixel;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- YshiftInPixel">
    /**
     * Y-shift in pixel
     * 
     * @param aValue A value
     */
    public void setYshiftInPixel(int aValue) {
        this.yShiftInPixel = aValue;
    }

    /**
     * Y-shift in pixel
     * 
     * @return Y-shift in pixel
     */
    public int getYshiftInPixel() {
        return this.yShiftInPixel;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- GraphicalParticlePositionInfo">
    /**
     * Graphical particle position info
     *
     * @param aGraphicalParticlePositionInfo Graphical particle position info
     */
    public void setGraphicalParticlePositionInfo(GraphicalParticlePositionInfo aGraphicalParticlePositionInfo) {
        this.graphicalParticlePositionInfo = aGraphicalParticlePositionInfo;
    }

    /**
     * Graphical particle position info
     *
     * @return Graphical particle position info
     */
    public GraphicalParticlePositionInfo getGraphicalParticlePositionInfo() {
        return this.graphicalParticlePositionInfo;
    }
    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="- SimulationBoxMagnificationPercentage">
    /**
     * Simulation box magnification percentage
     *
     * @return Simulation box magnification percentage
     */
    public double getSimulationBoxMagnificationPercentage() {
        return this.simulationBoxMagnificationPercentage;
    }

    /**
     * Simulation box magnification percentage
     *
     * @param aValue Value
     */
    public void setSimulationBoxMagnificationPercentage(double aValue) {
        this.simulationBoxMagnificationPercentage = aValue;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DepthAttenuationSlicer">
    /**
     * Depth attenuation of slices for depth impression in simulation box of
     * slicer. 0.0 = Minimum (no attenuation)
     *
     * @return Depth attenuation of slices for depth impression in simulation
     * box of slicer
     */
    public double getDepthAttenuationSlicer() {
        return this.depthAttenuationSlicer;
    }

    /**
     * Depth attenuation of slices for depth impression in simulation box of
     * slicer. 0.0 = Minimum (no attenuation)
     *
     * @param aValue Value
     */
    public void setDepthAttenuationSlicer(double aValue) {
        this.depthAttenuationSlicer = aValue;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ExclusionBoxSizeInfo">
    /**
     * Exclusion box size info
     *
     * @return Exclusion box size info
     */
    public BoxSizeInfo getExclusionBoxSizeInfo() {
        return this.exclusionBoxSizeInfo;
    }

    /**
     * Exclusion box size info
     *
     * @param anExclusionBoxSizeInfo Exclusion box size info
     */
    public void setExclusionBoxSizeInfo(BoxSizeInfo anExclusionBoxSizeInfo) {
        this.exclusionBoxSizeInfo = anExclusionBoxSizeInfo;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- FirstSliceIndex">
    /**
     * Index of first slice for simulation box slicer
     *
     * @return Index of first slice for simulation box slicer
     */
    public int getFirstSliceIndex() {
        return this.firstSliceIndex;
    }

    /**
     * Index of first slice for simulation box slicer
     *
     * @param aValue Value
     */
    public void setFirstSliceIndex(int aValue) {
        this.firstSliceIndex = aValue;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Returns info for step
     *
     * @return Info for step
     */
    public String getStepInfo() {
        return String.format(ModelMessage.get("MoveStepInfo.stepInfoFormat"), String.valueOf(this.boxViewIndex));
    }
    // </editor-fold>

}
