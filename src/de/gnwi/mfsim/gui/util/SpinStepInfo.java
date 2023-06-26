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
package de.gnwi.mfsim.gui.util;

import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import java.awt.image.BufferedImage;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.StringUtilityMethods;

/**
 * Info for single spin step
 *
 * @author Achim Zielesny
 */
public class SpinStepInfo {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility string methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Info for spin step
     */
    private String spinStepInfo;

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
     * Full pathname of image file for spin step
     */
    private String spinStepImageFilePathname;

    /**
     * Compressed spin step image
     */
    private byte[] spinStepImageByteArray;

    /**
     * Spin step image
     */
    private BufferedImage spinStepImage;

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
     * Offset for angle for rotation around x axis in degree
     */
    private double rotationAroundXaxisOffset;

    /**
     * Offset for angle for rotation around y axis in degree
     */
    private double rotationAroundYaxisOffset;

    /**
     * Offset for angle for rotation around z axis in degree
     */
    private double rotationAroundZaxisOffset;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aBoxViewIndex Index for simulation box view
     */
    public SpinStepInfo(int aBoxViewIndex) {
        // NOTE: No checks are performed!
        this.boxViewIndex = aBoxViewIndex;
        this.rotationAroundXaxisAngle = 0.0;
        this.rotationAroundYaxisAngle = 0.0;
        this.rotationAroundZaxisAngle = 0.0;
        this.rotationAroundXaxisOffset = 0.0;
        this.rotationAroundYaxisOffset = 0.0;
        this.rotationAroundZaxisOffset = 0.0;
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
        return this.spinStepImageFilePathname != null || this.spinStepImageByteArray != null || this.spinStepImage != null;
    }

    /**
     * Returns image
     *
     * @return Image or null if image is not available
     */
    public BufferedImage getImage() {
        switch (Preferences.getInstance().getImageStorageMode()) {
            case HARDDISK_COMPRESSED:
                if (this.spinStepImageFilePathname != null) {
                    return GraphicsUtils.readImageFromFile(this.spinStepImageFilePathname);
                } else {
                    return null;
                }
            case MEMORY_COMPRESSED:
                if (this.spinStepImageByteArray != null) {
                    try {
                        return GraphicsUtils.convertJpegEncodedByteArrayToBufferedImage(this.spinStepImageByteArray);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        return null;
                    }
                } else {
                    return null;
                }
            case MEMORY_UNCOMPRESSED:
                if (this.spinStepImage != null) {
                    return this.spinStepImage;
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
        this.spinStepImageFilePathname = null;
        this.spinStepImageByteArray = null;
        this.spinStepImage = null;
        this.graphicalParticlePositionInfo = null;
        this.spinStepInfo = "";
        // NOTE: Do NOT clear this.rotationAroundXaxisAngle, this.rotationAroundYaxisAngle and this.rotationAroundZaxisAngle
        // NOTE: Do NOT clear this.rotationAroundXaxisOffset, this.rotationAroundYaxisOffset and this.rotationAroundZaxisOffset
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
    //
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
    //
    // <editor-fold defaultstate="collapsed" desc="- StepImageFilePathname">
    /**
     * Returns full pathname of image file for simulation step
     *
     * @return Full pathname of image file for simulation step
     */
    public String getStepImageFilePathname() {
        return this.spinStepImageFilePathname;
    }

    /**
     * Full pathname of image file for simulation step
     *
     * @param aStepImageFilePathname Full pathname of image file for simulation
     * step
     */
    public void setStepImageFilePathname(String aStepImageFilePathname) {
        this.spinStepImageFilePathname = aStepImageFilePathname;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- StepImageByteArray">
    /**
     * Returns compressed step image
     *
     * @return Compressed step image
     */
    public byte[] getStepImageByteArray() {
        return this.spinStepImageByteArray;
    }

    /**
     * Compressed step image
     *
     * @param aStepImageByteArray Compressed step image
     */
    public void setStepImageByteArray(byte[] aStepImageByteArray) {
        this.spinStepImageByteArray = aStepImageByteArray;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- StepImage">
    /**
     * Returns step image
     *
     * @return Step image
     */
    public BufferedImage getStepImage() {
        return this.spinStepImage;
    }

    /**
     * Step image
     *
     * @param aStepImage Step image
     */
    public void setStepImage(BufferedImage aStepImage) {
        this.spinStepImage = aStepImage;
    }
    // </editor-fold>
    //
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
    //
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
    //
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
    //
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundXaxisOffset">
    /**
     * Offset for angle for rotation around x axis in degree
     *
     * @return Offset for angle for rotation around x axis in degree
     */
    public double getRotationAroundXaxisOffset() {
        return this.rotationAroundXaxisOffset;
    }

    /**
     * Default angle for rotation around x axis in degree
     *
     * @param aValue Value
     */
    public void setRotationAroundXaxisOffset(double aValue) {
        this.rotationAroundXaxisOffset = aValue;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundYaxisOffset">
    /**
     * Offset for angle for rotation around y axis in degree
     *
     * @return Offset for angle for rotation around y axis in degree
     */
    public double getRotationAroundYaxisOffset() {
        return this.rotationAroundYaxisOffset;
    }

    /**
     * Default angle for rotation around y axis in degree
     *
     * @param aValue Value
     */
    public void setRotationAroundYaxisOffset(double aValue) {
        this.rotationAroundYaxisOffset = aValue;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundZaxisOffset">
    /**
     * Offset for angle for rotation around z axis in degree
     *
     * @return Offset for angle for rotation around z axis in degree
     */
    public double getRotationAroundZaxisOffset() {
        return this.rotationAroundZaxisOffset;
    }

    /**
     * Offset for angle for rotation around z axis in degree
     *
     * @param aValue Value
     */
    public void setRotationAroundZaxisOffset(double aValue) {
        this.rotationAroundZaxisOffset = aValue;
    }

    // </editor-fold>
    //
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
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Returns info for step
     *
     * @return Info for step
     */
    public String getStepInfo() {
        this.spinStepInfo = String.format(ModelMessage.get("SpinStepInfo.stepInfoFormat"),
                this.stringUtilityMethods.formatDoubleValue((Preferences.getInstance().getRotationAroundXaxisAngle() + this.rotationAroundXaxisAngle + this.rotationAroundXaxisOffset)%360.0, 2),
                this.stringUtilityMethods.formatDoubleValue((Preferences.getInstance().getRotationAroundYaxisAngle() + this.rotationAroundYaxisAngle + this.rotationAroundYaxisOffset)%360.0, 2),
                this.stringUtilityMethods.formatDoubleValue((Preferences.getInstance().getRotationAroundZaxisAngle() + this.rotationAroundZaxisAngle + this.rotationAroundZaxisOffset)%360.0, 2));
        return this.spinStepInfo;
    }
    // </editor-fold>

}
