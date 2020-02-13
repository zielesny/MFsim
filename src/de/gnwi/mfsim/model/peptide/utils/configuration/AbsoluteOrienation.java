/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2020  Achim Zielesny (achim.zielesny@googlemail.com)
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

import Jama.*;
import java.util.List;

/**
 * Given the coordinates of 'n greater/equal 3' points in two coordinate systems
 * find the transformation between them.
 * We call the coordinate systems "left" and "right".
 * We seek the rigid transformation T such that T*P_left = P_right
 * This implementation is based on the paper "Closed-form solution of 
 * absolute orientation using unit quaternions", B.K.P. Horn,
 * Journal of the Optical Society of America, Vol. 4(4), pp 629--642, 1987.
 * 
 * @author: Ziv Yaniv 
 */
public class AbsoluteOrienation {
    /**
     * This method returns the rigid transformation between the left and right
     * coordinate systems.
     * @param left Vector of 3D points corresponding to the points found in
     *             the 'right' vector.
     * @param right Vector of 3D points corresponding to the points found in
     *              the 'left' vector.
     * @return Returns the rigid transformation between the coordinate systems T*left = right.
     * @see Point3D
     */
    public static Frame compute(List<Point3D> left, List<Point3D> right) {
        int i, n = left.size();
        Point3D meanLeft, meanRight;
    
                //compute the mean of both point sets
        meanLeft = new Point3D();
        meanRight = new Point3D();
        for (i=0; i<n; i++) {
            meanLeft.add((Point3D)left.get(i));
            meanRight.add((Point3D)right.get(i));
        }
        meanLeft.scale(1.0/n);
        meanRight.scale(1.0/n);

                    //compute the matrix muLmuR
        Matrix muLmuR = new Matrix(3,3);
        muLmuR.set(0,0,meanLeft.data[0]*meanRight.data[0]);
        muLmuR.set(0,1,meanLeft.data[0]*meanRight.data[1]);
        muLmuR.set(0,2,meanLeft.data[0]*meanRight.data[2]);
        muLmuR.set(1,0,meanLeft.data[1]*meanRight.data[0]);
        muLmuR.set(1,1,meanLeft.data[1]*meanRight.data[1]);
        muLmuR.set(1,2,meanLeft.data[1]*meanRight.data[2]);
        muLmuR.set(2,0,meanLeft.data[2]*meanRight.data[0]);
        muLmuR.set(2,1,meanLeft.data[2]*meanRight.data[1]);
        muLmuR.set(2,2,meanLeft.data[2]*meanRight.data[2]);

                        //compute the matrix M
        Matrix curMat = new Matrix(3,3);
        Matrix M = new Matrix(3,3);
        for (i=0; i<n; i++) {
            Point3D leftPoint = (Point3D) left.get(i);
            Point3D rightPoint = (Point3D) right.get(i);
            curMat.set(0,0,leftPoint.data[0]*rightPoint.data[0]);
            curMat.set(0,1,leftPoint.data[0]*rightPoint.data[1]);
            curMat.set(0,2,leftPoint.data[0]*rightPoint.data[2]);
            curMat.set(1,0,leftPoint.data[1]*rightPoint.data[0]);
            curMat.set(1,1,leftPoint.data[1]*rightPoint.data[1]);
            curMat.set(1,2,leftPoint.data[1]*rightPoint.data[2]);
            curMat.set(2,0,leftPoint.data[2]*rightPoint.data[0]);
            curMat.set(2,1,leftPoint.data[2]*rightPoint.data[1]);
            curMat.set(2,2,leftPoint.data[2]*rightPoint.data[2]);  
            M.plusEquals(curMat);
        }
        M.plusEquals(muLmuR.times(-n));

                //compute the matrix N  
        Matrix tmpMat = new Matrix(3,3);
        Matrix N = new Matrix(4,4);
        double traceM = M.trace();
        double A23, A31, A12;

        tmpMat.set(0,0,-traceM);
        tmpMat.set(1,1,-traceM);
        tmpMat.set(2,2,-traceM);
        tmpMat.plusEquals(M.plus(M.transpose()));

        A23 = M.get(1,2) - M.get(2,1);
        A31 = M.get(2,0) - M.get(0,2);
        A12 = M.get(0,1) - M.get(1,0);

        N.set(0,0,traceM); N.set(0,1,A23); N.set(0,2,A31); N.set(0,3,A12);
        N.set(1,0,A23);
        N.set(2,0,A31);
        N.set(3,0,A12);
        N.setMatrix(1,3,1,3,tmpMat); 

                //compute N's eigenvectors,eigenvalues
        EigenvalueDecomposition eigDecomp = N.eig();
                 //eigenVectors are the columns of the matrix corresponding to
                // the eigenvectors in ascending order
        Matrix eigenVectors = eigDecomp.getV();

        Frame result = new Frame(); 
                       //rotation quaternion is the eigenvector 
                      //corresponding to the largest eigenvalue
        int maxEigenValueIndex = 3; 
        result.setRotation(eigenVectors.get(0,maxEigenValueIndex),
                           eigenVectors.get(1,maxEigenValueIndex),
                           eigenVectors.get(2,maxEigenValueIndex),
                           eigenVectors.get(3,maxEigenValueIndex));

                    //translation is between the mean's of the point sets
        Point3D translation = new Point3D(meanRight);
        result.apply(meanLeft);
        translation.sub(meanLeft);
        result.setTranslation(translation.data); 
        return result;
    }
}
