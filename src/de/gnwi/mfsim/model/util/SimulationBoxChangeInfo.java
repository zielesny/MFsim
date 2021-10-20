/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
package de.gnwi.mfsim.model.util;

import de.gnwi.mfsim.model.graphics.BoxSizeInfo;

/**
 * Info about simulation box change for movie generation. Note: No checks are
 * performed!
 *
 * @author Achim Zielesny
 */
public class SimulationBoxChangeInfo {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Angle change for rotation around x axis in degree
     */
    private int rotationChangeAroundXaxisAngle;

    /**
     * Angle change for rotation around y axis in degree
     */
    private int rotationChangeAroundYaxisAngle;

    /**
     * Angle change for rotation around z axis in degree
     */
    private int rotationChangeAroundZaxisAngle;

    /**
     * Change of particle shift in percent of simulation box length along x-axis
     */
    private int particleShiftChangeX;

    /**
     * Change of particle shift in percent of simulation box length along y-axis
     */
    private int particleShiftChangeY;

    /**
     * Change of particle shift in percent of simulation box length along z-axis
     */
    private int particleShiftChangeZ;
    
    /**
     * Change for x view shift
     */
    private int xShiftChange;

    /**
     * Change for y view shift
     */
    private int yShiftChange;

    /**
     * Simulation box magnification percentage change
     */
    private int simulationBoxMagnificationPercentageChange;

    /**
     * Depth attenuation change of slices for depth impression in simulation box
     * of slicer. 0.0 = Minimum (no attenuation)
     */
    private double depthAttenuationChangeSlicer;

    /**
     * Target exclusion box size info
     */
    private BoxSizeInfo targetExclusionBoxSizeInfo;

    /**
     * Change of index of first slice for simulation box slicer
     */
    private int firstSliceIndexChange;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public SimulationBoxChangeInfo() {
        this.rotationChangeAroundXaxisAngle = 0;
        this.rotationChangeAroundYaxisAngle = 0;
        this.rotationChangeAroundZaxisAngle = 0;
        this.particleShiftChangeX = 0;
        this.particleShiftChangeY = 0;
        this.particleShiftChangeZ = 0;
        this.xShiftChange = 0;
        this.yShiftChange = 0;
        this.simulationBoxMagnificationPercentageChange = 0;
        this.depthAttenuationChangeSlicer = 0.0;
        this.targetExclusionBoxSizeInfo = null;
        this.firstSliceIndexChange = 0;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Clears all properties except this.targetExclusionBoxSizeInfo
     */
    public void clearWithoutTargetExclusionBoxSizeInfo() {
        // Do NOT change this.targetExclusionBoxSizeInfo
        this.rotationChangeAroundXaxisAngle = 0;
        this.rotationChangeAroundYaxisAngle = 0;
        this.rotationChangeAroundZaxisAngle = 0;
        this.particleShiftChangeX = 0;
        this.particleShiftChangeY = 0;
        this.particleShiftChangeZ = 0;
        this.xShiftChange = 0;
        this.yShiftChange = 0;
        this.simulationBoxMagnificationPercentageChange = 0;
        this.depthAttenuationChangeSlicer = 0.0;
        this.firstSliceIndexChange = 0;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Properties (get/set)">
    /**
     * Angle change for rotation around x axis in degree
     *
     * @return Angle change for rotation around x axis in degree
     */
    public int getRotationChangeAroundXaxisAngle() {
        return this.rotationChangeAroundXaxisAngle;
    }

    /**
     * Angle change for rotation around x axis in degree
     *
     * @param aValue Angle change for rotation around x axis in degree
     */
    public void setRotationChangeAroundXaxisAngle(int aValue) {
        this.rotationChangeAroundXaxisAngle = aValue;
    }

    /**
     * Angle change for rotation around y axis in degree
     *
     * @return Angle change for rotation around y axis in degree
     */
    public int getRotationChangeAroundYaxisAngle() {
        return this.rotationChangeAroundYaxisAngle;
    }

    /**
     * Angle change for rotation around y axis in degree
     *
     * @param aValue Angle change for rotation around y axis in degree
     */
    public void setRotationChangeAroundYaxisAngle(int aValue) {
        this.rotationChangeAroundYaxisAngle = aValue;
    }

    /**
     * Angle change for rotation around z axis in degree
     *
     * @return Angle change for rotation around z axis in degree
     */
    public int getRotationChangeAroundZaxisAngle() {
        return this.rotationChangeAroundZaxisAngle;
    }

    /**
     * Angle change for rotation around z axis in degree
     *
     * @param aValue Angle change for rotation around z axis in degree
     */
    public void setRotationChangeAroundZaxisAngle(int aValue) {
        this.rotationChangeAroundZaxisAngle = aValue;
    }

    /**
     * Change of particle shift in percent of simulation box length along x-axis
     *
     * @return Change of particle shift in percent of simulation box length along x-axis
     */
    public int getParticleShiftChangeX() {
        return this.particleShiftChangeX;
    }

    /**
     * Change of particle shift in percent of simulation box length along x-axis
     *
     * @param aValue Change of particle shift in percent of simulation box 
     * length along x-axis
     */
    public void setParticleShiftChangeX(int aValue) {
        this.particleShiftChangeX = aValue;
    }

    /**
     * Change of particle shift in percent of simulation box length along y-axis
     *
     * @return Change of particle shift in percent of simulation box length along y-axis
     */
    public int getParticleShiftChangeY() {
        return this.particleShiftChangeY;
    }

    /**
     * Change of particle shift in percent of simulation box length along y-axis
     *
     * @param aValue Change of particle shift in percent of simulation box 
     * length along y-axis
     */
    public void setParticleShiftChangeY(int aValue) {
        this.particleShiftChangeY = aValue;
    }

    /**
     * Change of particle shift in percent of simulation box length along z-axis
     *
     * @return Change of particle shift in percent of simulation box length along z-axis
     */
    public int getParticleShiftChangeZ() {
        return this.particleShiftChangeZ;
    }

    /**
     * Change of particle shift in percent of simulation box length along z-axis
     *
     * @param aValue Change of particle shift in percent of simulation box 
     * length along z-axis
     */
    public void setParticleShiftChangeZ(int aValue) {
        this.particleShiftChangeZ = aValue;
    }
    
    /**
     * Change for x view shift
     * 
     * @param aValue A value
     */
    public void setXshiftChange(int aValue) {
        this.xShiftChange = aValue;
    }

    /**
     * Change for x view shift
     * 
     * @return Change for x view shift
     */
    public int getXshiftChange() {
        return this.xShiftChange;
    }

    /**
     * Change for y view shift
     * 
     * @param aValue A value
     */
    public void setYshiftChange(int aValue) {
        this.yShiftChange = aValue;
    }

    /**
     * Change for y view shift
     * 
     * @return Change for y view shift
     */
    public int getYshiftChange() {
        return this.yShiftChange;
    }
    
    /**
     * Simulation box magnification percentage change
     *
     * @return Simulation box magnification percentage change
     */
    public int getSimulationBoxMagnificationPercentageChange() {
        return this.simulationBoxMagnificationPercentageChange;
    }

    /**
     * Simulation box magnification percentage change
     *
     * @param aValue Simulation box magnification percentage change
     */
    public void setSimulationBoxMagnificationPercentageChange(int aValue) {
        this.simulationBoxMagnificationPercentageChange = aValue;
    }

    /**
     * Depth attenuation change of slices for depth impression in simulation box
     * of slicer. 0.0 = Minimum (no attenuation)
     *
     * @return Depth attenuation change of slices
     */
    public double getDepthAttenuationChangeSlicer() {
        return this.depthAttenuationChangeSlicer;
    }

    /**
     * Depth attenuation change of slices for depth impression in simulation box
     * of slicer. 0.0 = Minimum (no attenuation)
     *
     * @param aValue Depth attenuation change of slices
     */
    public void setDepthAttenuationChangeSlicer(double aValue) {
        this.depthAttenuationChangeSlicer = aValue;
    }

    /**
     * Target exclusion box size info
     *
     * @return Target exclusion box size info
     */
    public BoxSizeInfo getTargetExclusionBoxSizeInfo() {
        return this.targetExclusionBoxSizeInfo;
    }

    /**
     * Target exclusion box size info
     *
     * @param aTargetExclusionBoxSizeInfo Target exclusion box size info
     */
    public void setTargetExclusionBoxSizeInfo(BoxSizeInfo aTargetExclusionBoxSizeInfo) {
        this.targetExclusionBoxSizeInfo = aTargetExclusionBoxSizeInfo;
    }

    /**
     * Change of index of first slice for simulation box slicer
     *
     * @return Change of index of first slice for simulation box slicer
     */
    public int getFirstSliceIndexChange() {
        return this.firstSliceIndexChange;
    }

    /**
     * Change of index of first slice for simulation box slicer
     *
     * @param aValue Change of index of first slice for simulation box slicer
     */
    public void setFirstSliceIndexChange(int aValue) {
        this.firstSliceIndexChange = aValue;
    }
    // </editor-fold>
    
}
