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
package de.gnwi.mfsim.model.job;

import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.awt.image.BufferedImage;
import de.gnwi.mfsim.model.preference.Preferences;

/**
 * Info for single simulation time step
 *
 * @author Achim Zielesny
 */
public class TimeStepInfo {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Info for step
     */
    private String stepInfo;

    /**
     * Full pathname of graphical particle positions file of Job Result
     */
    private String jobResultParticlePositionsFilePathname;

    /**
     * Simulation box view
     */
    private SimulationBoxViewEnum boxView;

    /**
     * Index for simulation box view
     */
    private int boxViewIndex;

    /**
     * Full pathname of image file for simulation step
     */
    private String stepImageFilePathname;

    /**
     * Compressed step image
     */
    private byte[] stepImageByteArray;

    /**
     * Step image
     */
    private BufferedImage stepImage;

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
     * Y-shift in pixel
     */
    private int yShiftInPixel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aStepInfo Info for step
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aBoxViewIndex Index for simulation box view
     */
    public TimeStepInfo(String aStepInfo, String aJobResultParticlePositionsFilePathname, int aBoxViewIndex) {
        // NOTE: No checks are performed!
        this.stepInfo = aStepInfo;
        this.jobResultParticlePositionsFilePathname = aJobResultParticlePositionsFilePathname;
        this.boxViewIndex = aBoxViewIndex;
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
        return this.stepImageFilePathname != null || this.stepImageByteArray != null || this.stepImage != null;
    }

    /**
     * Returns image
     *
     * @return Image or null if image is not available
     */
    public BufferedImage getImage() {
        switch (Preferences.getInstance().getImageStorageMode()) {
            case HARDDISK_COMPRESSED:
                if (this.stepImageFilePathname != null) {
                    return GraphicsUtils.readImageFromFile(this.stepImageFilePathname);
                } else {
                    return null;
                }
            case MEMORY_COMPRESSED:
                if (this.stepImageByteArray != null) {
                    try {
                        return GraphicsUtils.convertJpegEncodedByteArrayToBufferedImage(this.stepImageByteArray);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        return null;
                    }
                } else {
                    return null;
                }
            case MEMORY_UNCOMPRESSED:
                if (this.stepImage != null) {
                    return this.stepImage;
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
        this.stepImageFilePathname = null;
        this.stepImageByteArray = null;
        this.stepImage = null;
    }

    /**
     * Returns clone of this instance
     *
     * @return Clone of this instance
     */
    public TimeStepInfo clone() {
        TimeStepInfo tmpClonedInstance = new TimeStepInfo(this.stepInfo, this.jobResultParticlePositionsFilePathname, this.boxViewIndex);
        tmpClonedInstance.setBoxView(this.boxView);
        tmpClonedInstance.setStepImage(this.stepImage);
        tmpClonedInstance.setStepImageByteArray(this.stepImageByteArray);
        tmpClonedInstance.setStepImageFilePathname(this.stepImageFilePathname);
        return tmpClonedInstance;
    }

    /**
     * Returns partial clone of this instance
     *
     * @return Partial clone of this instance
     */
    public TimeStepInfo clonePartially() {
        TimeStepInfo tmpClonedInstance = new TimeStepInfo(this.stepInfo, this.jobResultParticlePositionsFilePathname, this.boxViewIndex);
        return tmpClonedInstance;
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
     * @param aBoxView Box view
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
        return this.stepImageFilePathname;
    }

    /**
     * Full pathname of image file for simulation step
     *
     * @param aStepImageFilePathname Full pathname of image file for simulation
     * step
     */
    public void setStepImageFilePathname(String aStepImageFilePathname) {
        this.stepImageFilePathname = aStepImageFilePathname;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- StepImageByteArray">
    /**
     * Returns compressed step image
     *
     * @return Compressed step image
     */
    public byte[] getStepImageByteArray() {
        return this.stepImageByteArray;
    }

    /**
     * Compressed step image
     *
     * @param aStepImageByteArray Compressed step image
     */
    public void setStepImageByteArray(byte[] aStepImageByteArray) {
        this.stepImageByteArray = aStepImageByteArray;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- StepImage">
    /**
     * Returns step image
     *
     * @return Step image
     */
    public BufferedImage getStepImage() {
        return this.stepImage;
    }

    /**
     * Step image
     *
     * @param aStepImage Step image
     */
    public void setStepImage(BufferedImage aStepImage) {
        this.stepImage = aStepImage;
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
     * @return X-shift in pixel
     */
    public int getXshiftInPixel() {
        return this.xShiftInPixel;
    }

    /**
     * X-shift in pixel
     *
     * @param aValue Value
     */
    public void setXshiftInPixel(int aValue) {
        this.xShiftInPixel = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- YshiftInPixel">
    /**
     * Y-shift in pixel
     *
     * @return Y-shift in pixel
     */
    public int getYshiftInPixel() {
        return this.yShiftInPixel;
    }

    /**
     * Y-shift in pixel
     *
     * @param aValue Value
     */
    public void setYshiftInPixel(int aValue) {
        this.yShiftInPixel = aValue;
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
        return this.stepInfo;
    }

    /**
     * Returns full pathname of graphical particle positions file of Job Result
     *
     * @return Full pathname of graphical particle positions file of Job Result
     */
    public String getJobResultParticlePositionsFilePathname() {
        return this.jobResultParticlePositionsFilePathname;
    }
    // </editor-fold>

}
