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
package de.gnwi.mfsim.model.peptide.utils.configuration;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Implementation of a Frame/Rigid/Euclidean transformation.
 * All angles are in radians (to translate between radians and degrees use
 * Math.toRadians(), Math.toDegrees() ).
 * @author Ziv Yaniv
 */
public class Frame implements Serializable {
    /**
     * Small angle
     */
    public static final double SMALL_ANGLE = 0.008726535498373935;

    /**
     * Support variable
     */
    private double[][] rotation;
    /**
     * Support variable
     */
    private double[] translation;            //0.5 degrees

    /**
     * Construct the identity transformation.
     */
    public Frame() {
    translation = new double[3];

    rotation = new double[3][3];
    rotation[0][0] = 1;
    rotation[1][1] = 1;
    rotation[2][2] = 1;
    }
    
    /**
     * Copy Constructor, create a frame which is a copy of the given frame.
     * @param f This frame is copied.
     */
    public Frame(Frame f) {
    translation = new double[3];
    System.arraycopy(f.translation,0,translation,0,3);

    rotation = new double[3][3];
    for(int i=0; i<3; i++)
        System.arraycopy(f.rotation[i],0,rotation[i],0,3);
    }

    /**
     * Create a frame with the given translations and rotation angles.
     * @param x Translation in the x direction.
     * @param y Translation in the y direction.
     * @param z Translation in the z direction.
     * @param az Angle of rotation around the z axis.
     * @param ay Angle of rotation around the y axis.
     * @param ax Angle of rotation around the x axis.
     */
    public Frame(double x, double y, double z,
         double az, double ay, double ax) {
    translation = new double[3];
    rotation = new double[3][3];

    setTranslation(x,y,z);  
    setRotation(az,ay,ax);
    }

    /**
     * Create a frame with the given translations and rotation around the
     * given axis with the given angle. Axis of rotation is assumed to
     * be normalized.
     * @param x Translation in the x direction.
     * @param y Translation in the y direction.
     * @param z Translation in the z direction.
     * @param axis Axis of rotation.
     * @param angle Angle of rotation around the given axis.
     */
    public Frame(double x, double y, double z,
         double[] axis, double angle) {
    translation = new double[3];
    rotation = new double[3][3];

    setTranslation(x,y,z);
    setRotation(axis,angle);
    }

    /**
     * Create a frame with the given translations and rotation around the
     * given axis and angle. The norm of the axis is the size of the
     * rotation angle.
     * @param x Translation in the x direction.
     * @param y Translation in the y direction.
     * @param z Translation in the z direction.
     * @param axisAngle The direction of this vector is the axis of rotation
     *                  and its norm is the size of the rotation angle.
     */
    public Frame(double x, double y, double z,
         double []axisAngle) {
    translation = new double[3];
    rotation = new double[3][3];

    setTranslation(x,y,z);
    setRotation(axisAngle);
    }

    /**
     * Create a frame with the given translations and rotation according
     * to the given quaternion. Assumes that the quaternion is a unit
     * quaternion (s^2 + qx^2 + qy^2 + qz^2 = 1).
     * @param x Translation in the x direction.
     * @param y Translation in the y direction.
     * @param z Translation in the z direction.
     * @param s Scalar part of the quaternion.
     * @param qx x coordinate of the quaternion.
     * @param qy y coordinate of the quaternion.
     * @param qz z coordinate of the quaternion.
     */
    public Frame(double x, double y, double z,
         double s, double qx, double qy, double qz) {
    translation = new double[3];
    rotation = new double[3][3];

    setTranslation(x,y,z);
    setRotation(s,qx,qy,qz);
    }

    /**
     * Apply the transformation on the given point and update the
     * point coordinates accordingly.
     * @param p The point on which the transformation is applied.
     * @see Point3D
     */
    public void apply(Point3D p) {
    double[] res = new double[3]; 
    res[0] = rotation[0][0] * p.data[0] + 
             rotation[0][1] * p.data[1] + 
             rotation[0][2] * p.data[2] + 
             translation[0];
    res[1] = rotation[1][0] * p.data[0] + 
             rotation[1][1] * p.data[1] + 
             rotation[1][2] * p.data[2] + 
             translation[1];
    res[2] = rotation[2][0] * p.data[0] + 
             rotation[2][1] * p.data[1] + 
             rotation[2][2] * p.data[2] + 
             translation[2];
    p.set(res);
    }

    /**
     * Apply the transformation on the given point and set the results in
     * the second point.
     * @param p The point on which the transformation is applied.
     * @param pTransformed The result of applying the transformation on point
     *                     p.
     * @see Point3D
     */
    public void apply(Point3D p, Point3D pTransformed) {
    double[] res = new double[3]; 
    res[0] = rotation[0][0] * p.data[0] + 
             rotation[0][1] * p.data[1] + 
             rotation[0][2] * p.data[2] + 
             translation[0];
    res[1] = rotation[1][0] * p.data[0] + 
             rotation[1][1] * p.data[1] + 
             rotation[1][2] * p.data[2] + 
             translation[1];
    res[2] = rotation[2][0] * p.data[0] + 
             rotation[2][1] * p.data[1] + 
             rotation[2][2] * p.data[2] + 
             translation[2];
    pTransformed.set(res);
    }

    /**
     * Apply the inverse transformation on the given point and update the
     * point coordinates accordingly.
     * If the inverse transformation is applied many times 
     * it is computationally more efficient to invert the frame and 
     * then use the inverted frame's apply() method.
     * @param p The point on which the transformation is applied.
     * @see Point3D
     */
    public void applyInverse(Point3D p) {
    Frame f = new Frame();
    invert(f);
    f.apply(p);
    }
    /**
     * Apply the inverse transformation on the given point and update the
     * point coordinates accordingly.
     * If the inverse transformation is applied many times 
     * it is computationally more efficient to invert the frame and 
     * then use the inverted frame's apply() method.
     * @param p The point on which the transformation is applied.
     * @param pTransformed The result of applying the transformation on point
     *                     p.
     * @see Point3D
     */
    public void applyInverse(Point3D p, Point3D pTransformed) {
    Frame f = new Frame();
    invert(f);
    f.apply(p,pTransformed);
    }
    
    /**
     * Compute the composition/multiplication (f1 o f2)
     * of two frames and store the result in this frame.
     * @param f1 Left operand of the composition.
     * @param f2 Right operand of the composition.
     */
    public void mul(Frame f1, Frame f2) {
      int i,j;
      double[][] tmpRotation = new double[3][3];
      double[] tmpTranslation = new double[3];

      for (i=0;i<3;i++) {
    for (j=0;j<3;j++) {
      tmpRotation[i][j]=f1.rotation[i][0]*f2.rotation[0][j] +
                        f1.rotation[i][1]*f2.rotation[1][j] +
                        f1.rotation[i][2]*f2.rotation[2][j];
    }
      }
      for(i=0; i<3; i++) 
    tmpTranslation[i] = f1.rotation[i][0]*f2.translation[0] +
                        f1.rotation[i][1]*f2.translation[1] +
                        f1.rotation[i][2]*f2.translation[2] +
                        f1.translation[i];
      rotation = tmpRotation;
      translation = tmpTranslation;
    } 

    /**
     * Compute the composition/multiplication (this o f)
     * of two frames and store the result in this frame.
     * @param f Right operand of the composition.
     */
    public void mul(Frame f) {
      int i,j;
      double[][] tmpRotation = new double[3][3];
      double[] tmpTranslation = new double[3];
            
      for (i=0;i<3;i++) {
    for (j=0;j<3;j++) {
      tmpRotation[i][j]=rotation[i][0]*f.rotation[0][j] +
                        rotation[i][1]*f.rotation[1][j] +
                        rotation[i][2]*f.rotation[2][j];
    }
      }
      for(i=0; i<3; i++) 
    tmpTranslation[i] = rotation[i][0]*f.translation[0] +
                        rotation[i][1]*f.translation[1] +
                        rotation[i][2]*f.translation[2] +
                        translation[i];
      rotation = tmpRotation;
      translation = tmpTranslation;
    }
    
    /**
     * Invert the frame.
     */
    public void invert() {
    double[][] tmpRotation = new double[3][3];
    for(int i=0; i<3; i++)
        for(int j=0; j<3; j++)
        tmpRotation[i][j] = rotation[j][i];
    double[] tmpTranslation = new double[3];
    tmpTranslation[0] = -(tmpRotation[0][0] * translation[0] + 
                          tmpRotation[0][1] * translation[1] + 
                          tmpRotation[0][2] * translation[2]);
    tmpTranslation[1] = -(tmpRotation[1][0] * translation[0] + 
                          tmpRotation[1][1] * translation[1] + 
                          tmpRotation[1][2] * translation[2]);
    tmpTranslation[2] = -(tmpRotation[2][0] * translation[0] + 
                          tmpRotation[2][1] * translation[1] + 
                          tmpRotation[2][2] * translation[2]);
    rotation = tmpRotation;
    translation = tmpTranslation;
    }
    
    /**
     * Invert the given frame and store the result in this frame.
     * @param f The frame whose inverse we set this frame to be.
     */
    public void invert(Frame f) {
    for(int i=0; i<3; i++)
        for(int j=0; j<3; j++)
        f.rotation[i][j] = rotation[j][i];
    f.translation[0] = -(f.rotation[0][0] * translation[0] + 
                 f.rotation[0][1] * translation[1] + 
                 f.rotation[0][2] * translation[2]);
    f.translation[1] = -(f.rotation[1][0] * translation[0] + 
                 f.rotation[1][1] * translation[1] + 
                 f.rotation[1][2] * translation[2]);
    f.translation[2] = -(f.rotation[2][0] * translation[0] + 
                 f.rotation[2][1] * translation[1] + 
                 f.rotation[2][2] * translation[2]);
    }

    /**
     * Set this frame to the identity transformation, no rotation and no
     * translation.
     */
    public void setIdentity() {
    Arrays.fill(translation,0);

    for(int i=0; i<3; i++)
        Arrays.fill(rotation[i],0);
    rotation[0][0] = 1;
    rotation[1][1] = 1;
    rotation[2][2] = 1;
    }

    /**
     * Set this frame's translation to the given translation.
     * @param x Translation in the x direction.
     * @param y Translation in the y direction.
     * @param z Translation in the z direction.
     */
    public void setTranslation(double x, double y, double z) {
    translation[0] = x;
    translation[1] = y;
    translation[2] = z;
    }
    /**
     * Set this frame's translation to the given translation.
     * Assumes that the given array has at least three entries.
     * @param translation Array containing the translation parameters
     *                    [x,y,z].
     */
    public void setTranslation(double[] translation) {
    System.arraycopy(translation,0,this.translation,0,3);
    }
    /**
     * Set this frame's rotation according to the given angles.
     * @param az Angle of rotation around the z axis.
     * @param ay Angle of rotation around the y axis.
     * @param ax Angle of rotation around the x axis.
     */
    public void setRotation(double az, double ay, double ax) {
    double cx, cy, cz, sx, sy, sz;

    cx = Math.cos(ax);
    cy = Math.cos(ay);
    cz = Math.cos(az);
    sx = Math.sin(ax);
    sy = Math.sin(ay);
    sz = Math.sin(az);

    rotation[0][0] = cz*cy; 
    rotation[0][1] = cz*sy*sx - sz*cx;  
    rotation[0][2] = cz*sy*cx+sz*sx;     

    rotation[1][0] = sz*cy; 
    rotation[1][1] = sz*sy*sx + cz*cx ; 
    rotation[1][2] = sz*sy*cx - cz*sx;   

    rotation[2][0] = -sy;   
    rotation[2][1] = cy*sx;             
    rotation[2][2] = cy*cx;
    }

    /**
     * Set this frame's rotation according to the given axis and angle.
     * Axis of rotation is assumed to be normalized.
     * @param axis Axis of rotation.
     * @param angle Angle of rotation around the given axis.
     */ 
    public void setRotation(double[] axis, double angle) {
    double c,s,v;

    c =  Math.cos(angle);
    s =  Math.sin(angle);
    v = 1-c;

    rotation[0][0] = axis[0]*axis[0]*v + c; 
    rotation[0][1] = axis[0]*axis[1]*v - axis[2]*s;  
    rotation[0][2] = axis[0]*axis[2]*v + axis[1]*s;     

    rotation[1][0] = axis[0]*axis[1]*v + axis[2]*s; 
    rotation[1][1] = axis[1]*axis[1]*v + c; 
    rotation[1][2] = axis[1]*axis[2]*v - axis[0]*s;   
    
    rotation[2][0] = axis[0]*axis[2]*v - axis[1]*s;   
    rotation[2][1] = axis[1]*axis[2]*v + axis[0]*s;             
    rotation[2][2] = axis[2]*axis[2]*v + c;
    }
    /**
     * Set this frame's rotation according to the given axis and angle.
     * The norm of the axis is the size of the rotation angle.
     * @param axisAngle The direction of this vector is the axis of rotation
     *                  and its norm is the size of the rotation angle.
     */
    public void setRotation(double []axisAngle) {
    double angle = Math.sqrt(axisAngle[0]*axisAngle[0] +
                       axisAngle[1]*axisAngle[1] +
                       axisAngle[2]*axisAngle[2]);
    double[] axis = new double[3];
    axis[0] = axisAngle[0]/angle;
    axis[1] = axisAngle[1]/angle;
    axis[2] = axisAngle[2]/angle;

    setRotation(axis,angle);
    }
    /**
     * Set this frame's rotation according to the given quaternion.
     * Assumes that the quaternion is a unit quaternion,
     * (s^2 + qx^2 + qy^2 + qz^2 = 1).
     * @param s Scalar part of the quaternion.
     * @param x x coordinate of the quaternion.
     * @param y y coordinate of the quaternion.
     * @param z z coordinate of the quaternion.
     */
    public void setRotation(double s, double x, double y, double z) {
    rotation[0][0] = 1-2*(y*y+z*z);   
    rotation[0][1] = 2*(x*y-s*z);    
    rotation[0][2] = 2*(x*z+s*y);    
    
    rotation[1][0] = 2*(x*y+s*z);     
    rotation[1][1] = 1-2*(x*x+z*z);  
    rotation[1][2] = 2*(y*z-s*x);    
    
    rotation[2][0] = 2*(x*z-s*y);     
    rotation[2][1] = 2*(y*z+s*x);    
    rotation[2][2] = 1-2*(x*x+y*y);  
    }

    /**
     * Set this frame to the given frame.
     * @param f Set the current frame to this frame.
     */
    public void set(Frame f) {
    System.arraycopy(f.rotation[0],0,rotation[0],0,3);
    System.arraycopy(f.rotation[2],0,rotation[2],0,3);
    System.arraycopy(f.rotation[1],0,rotation[1],0,3);
    System.arraycopy(f.translation,0,translation,0,3);
    }

    /**
     * Get the translational part of this frame.
     * @param translation An array into which the translation is 
     *                    written [x,y,z]. Has at least three entries.
     */
    public void getTranslation(double[] translation) {
    translation[0] = this.translation[0];
    translation[1] = this.translation[1];
    translation[2] = this.translation[2];
    }

    
    /**
     * Get the angles of rotation from this frame. Rotation angles are
     * for the ZYX Euler angle specification.
     * @param angles An array into which the angles are  
     *                    written [ax,ay,az]. Has at least three entries.
     */
    public void getRotationAngles(double[] angles) {
    angles[1] = Math.atan2(-rotation[2][0],
                          Math.sqrt(rotation[0][0]*
                                                rotation[0][0] +
                                rotation[1][0]*
                                                rotation[1][0]));
    if(Math.abs(angles[1] - Math.PI) > SMALL_ANGLE &&
       Math.abs(angles[1] + Math.PI) > SMALL_ANGLE) {
        double cy = Math.cos(angles[1]);

        angles[0] = Math.atan2(rotation[2][1]/cy, 
                      rotation[0][0]/cy);
        angles[2] = Math.atan2(-rotation[1][0]/cy, 
                      rotation[2][2]/cy);
    }
            //thetaY is approximatly PI or -PI
               //set thetaZ to zero and compute thetaX
    else {
        angles[2] = 0;
        angles[0] =  Math.atan2(rotation[0][2],rotation[1][1]);
    }  
    }

    /**
     * Get the axis and angle of rotation from this frame.
     * @param axisAngle An array into which the angle and the axis are
     *                  written [a,nx,ny,nz]. Has at least four entries.
     */
    public void getRotationAxisAngle(double[] axisAngle) {
    axisAngle[0] =  Math.acos((rotation[0][0] +
                      rotation[1][1] +
                      rotation[2][2] -
                      1)/2);
                 //if the angle is less than SMALL_ANGLE or is less than 
            //(pi - SMALL_ANGLE) the axis is ill defined, 
           //will be set to (0,0,0)
               //The method Math.acos returns an angle in [0,pi]
    if((axisAngle[0] < SMALL_ANGLE) || 
       (axisAngle[0] > (Math.PI - SMALL_ANGLE)))
        axisAngle[1] = axisAngle[2] = axisAngle[3] = 0;
    else {
        double scale = (1.0/(2*Math.sin(axisAngle[0])));
        axisAngle[1] = scale*(rotation[2][1] - rotation[1][2]);
        axisAngle[2] = scale*(rotation[0][2] - rotation[2][0]);
        axisAngle[3] = scale*(rotation[1][0] - rotation[0][1]);
    }
    }

    /**
     * Get the unit quaternion describing the rotation from this frame.
     * @param quaternion An array into which the quaternion parameters are
     *                   written [s,qx,qy,qz]. Has at least four entries.
     */
    public void getRotationQuaternion(double[] quaternion) {
    final double EPSILON = 1e-16;
    quaternion[0] = (0.5*Math.sqrt(rotation[0][0] +
                          rotation[1][1] +
                          rotation[2][2] +
                          1));
    if(Math.abs(quaternion[0]) > EPSILON) {
        double denom = 4*quaternion[0];
        quaternion[1] = (rotation[2][1] - rotation[1][2])/denom;
        quaternion[2] = (rotation[0][2] - rotation[2][0])/denom;
        quaternion[3] = (rotation[1][0] - rotation[0][1])/denom;
    }
    else {
        double tmp = Math.sqrt(rotation[0][0] - 
                   rotation[1][1] - 
                   rotation[2][2] + 
                   1);
        quaternion[1] = 0.5*tmp;

        tmp = 0.5/tmp;
        quaternion[2] = tmp*(rotation[0][1] + rotation[1][0]);
        quaternion[3] = tmp*(rotation[0][3] + rotation[3][0]);
    }
    }

    /**
     * String represntation of a frame.
     * @return Returns the String represnetation of this frame.
     */
    public String toString() {
    return ("[" + rotation[0][0] + 
        "," + rotation[0][1] + 
        "," + rotation[0][2] + 
        "," + translation[0] + "]\n" +
        "[" + rotation[1][0] + 
        "," + rotation[1][1] + 
        "," + rotation[1][2] + 
        "," + translation[1] + "]\n" +
        "[" + rotation[2][0] + 
        "," + rotation[2][1] + 
        "," + rotation[2][2] + 
        "," + translation[2] + "]\n");
    }
}
