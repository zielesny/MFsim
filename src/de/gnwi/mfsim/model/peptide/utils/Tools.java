/*
 * To change this template, choose Tools | Templates
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
package de.gnwi.mfsim.model.peptide.utils;

import de.gnwi.mfsim.model.peptide.utils.configuration.Point3D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.regex.Pattern;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Andreas Truszkowski
 */
public class Tools {

    /**
     * General line-separator string
     */
    public static final String GENERAL_LINE_SEPARATOR = "~";

    /**
     * Regex pattern for GENERAL_LINE_SEPARATOR string
     */
    public static final Pattern GENERAL_LINE_SEPARATOR_PATTERN = Pattern.compile("\\" + Tools.GENERAL_LINE_SEPARATOR);

    /**
     * General separator string
     */
    public static final String GENERAL_SEPARATOR = "|";

    /**
     * Regex pattern for GENERAL_SEPARATOR string
     */
    public static final Pattern GENERAL_SEPARATOR_PATTERN = Pattern.compile("\\" + Tools.GENERAL_SEPARATOR);

    /**
     * Reads a file into a String.
     *
     * @param path File
     * @return String containing file data.
     * @throws IOException IOException
     */
    public static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

    /**
     * Generates a uniform distributed quaternion.
     *
     * @param aRandom Pseudorandom generator.
     * @return Random quaternion.
     */
    public static Quat4d randomQuaternion(Random aRandom) {
        double u1 = aRandom.nextDouble();
        double u2 = aRandom.nextDouble();
        double u3 = aRandom.nextDouble();

        double u1sqrt = Math.sqrt(u1);
        double u1m1sqrt = Math.sqrt(1 - u1);
        double x = u1m1sqrt * Math.sin(2 * Math.PI * u2);
        double y = u1m1sqrt * Math.cos(2 * Math.PI * u2);
        double z = u1sqrt * Math.sin(2 * Math.PI * u3);
        double w = u1sqrt * Math.cos(2 * Math.PI * u3);

        return new Quat4d(x, y, w, z);
    }

    /**
     * Converts a quaternion in to Euler angles.
     *
     * @param aQuaternion Quaternion to convert.
     * @return Vector containing Euler angles.
     */
    public static Vector3d quaternionToEulerAngles(Quat4d aQuaternion) {
        Vector3d tmpRotation = new Vector3d();
        double sqw = aQuaternion.w * aQuaternion.w;
        double sqx = aQuaternion.x * aQuaternion.x;
        double sqy = aQuaternion.y * aQuaternion.y;
        double sqz = aQuaternion.z * aQuaternion.z;
        double unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
        double test = aQuaternion.x * aQuaternion.y + aQuaternion.z * aQuaternion.w;
        if (test > 0.499 * unit) { // singularity at north pole
            tmpRotation.y = 2 * Math.atan2(aQuaternion.x, aQuaternion.w);
            tmpRotation.z = Math.PI / 2;
            tmpRotation.x = 0;
            return tmpRotation;
        }
        if (test < -0.499 * unit) { // singularity at south pole
            tmpRotation.y = -2 * Math.atan2(aQuaternion.x, aQuaternion.w);
            tmpRotation.z = -Math.PI / 2;
            tmpRotation.x = 0;
            return tmpRotation;
        }
        tmpRotation.y = Math.atan2(2 * aQuaternion.y * aQuaternion.w - 2 * aQuaternion.x * aQuaternion.z, sqx - sqy - sqz + sqw);
        tmpRotation.z = Math.asin(2 * test / unit);
        tmpRotation.x = Math.atan2(2 * aQuaternion.x * aQuaternion.w - 2 * aQuaternion.y * aQuaternion.z, -sqx + sqy - sqz + sqw);
        return tmpRotation;
    }

    /**
     * Converts Euler angles into a quaternion.
     *
     * @param aEulerAngles Vector containing the Euler angles.
     * @return Quaternion.
     */
    public static Quat4d eulerAnglesToQuaternion(Vector3d aEulerAngles) {
        Quat4d tmpQuaternion = new Quat4d();
        double c1 = Math.cos(aEulerAngles.y / 2);
        double s1 = Math.sin(aEulerAngles.y / 2);
        double c2 = Math.cos(aEulerAngles.z / 2);
        double s2 = Math.sin(aEulerAngles.z / 2);
        double c3 = Math.cos(aEulerAngles.x / 2);
        double s3 = Math.sin(aEulerAngles.x / 2);
        double c1c2 = c1 * c2;
        double s1s2 = s1 * s2;
        tmpQuaternion.w = c1c2 * c3 - s1s2 * s3;
        tmpQuaternion.x = c1c2 * s3 + s1s2 * c3;
        tmpQuaternion.y = s1 * c2 * c3 + c1 * s2 * s3;
        tmpQuaternion.z = c1 * s2 * c3 - s1 * c2 * s3;
        return tmpQuaternion;
    }

    /**
     * Calculates the center of mass of given coordiantes.
     *
     * @param aCoordinates List of coordinates.
     * @return The center of mass.
     */
    public static Point3d getCenter(Point3d[] aCoordinates) {
        double x = 0;
        double y = 0;
        double z = 0;
        for (Point3d aCoordinate : aCoordinates) {
            x += aCoordinate.x;
            y += aCoordinate.y;
            z += aCoordinate.z;
        }
        x /= aCoordinates.length;
        y /= aCoordinates.length;
        z /= aCoordinates.length;
        return new Point3d(x, y, z);
    }

    /**
     * Convertes a javax.vecmath.Point3d into
     * de.truszkowski.peptide.utilities.absoluteorientation.Point3D.
     *
     * @param aPoint Converted point.
     * @return Converted point
     */
    public static Point3D pointConverter(Point3d aPoint) {
        return new Point3D(aPoint.getX(), aPoint.getY(), aPoint.getZ());
    }

    /**
     * Gets all particles from query SPICES.
     *
     * @param aSpices SPICES to parse.
     * @return Particle names.
     */
    public static String[] getParticlesFromSpices(String aSpices) {
        // Old code:
        // return aSpices.replaceAll("<|\\)", "").split(">|-|\\(");
        String tmpReplacedSpices = StringUtils.replace(StringUtils.replace(aSpices, "<", ""), ")", "");
        return tmpReplacedSpices.split(">|-|\\(");
    }

    /**
     * Gets the max diameter of given coordinates.
     *
     * @param aCoordinates The coordinates.
     * @return Diameter of given coordinates.
     */
    public static double getDiameter(Point3d[] aCoordinates) {
        double tmpDiameter = 0;
        for (int i = 0; i < aCoordinates.length; i++) {
            for (int j = 0; j < aCoordinates.length; j++) {
                if (aCoordinates[i].distance(aCoordinates[j]) > tmpDiameter) {
                    tmpDiameter = aCoordinates[i].distance(aCoordinates[j]);
                }
            }
        }
        return tmpDiameter;
    }

    /**
     * Creates a scaling matrix from given ratio.
     *
     * @param aRatio Scaling ratio.
     * @return Scaling matrix.
     */
    public static Matrix3d createScalingMatrix(double aRatio) {
        Matrix3d tmpScaleMatrix = new Matrix3d();
        tmpScaleMatrix.setZero();
        tmpScaleMatrix.setElement(0, 0, aRatio);
        tmpScaleMatrix.setElement(1, 1, aRatio);
        tmpScaleMatrix.setElement(2, 2, aRatio);
        return tmpScaleMatrix;
    }

    /**
     * Creates a translation matrix from given vector.
     *
     * @param tmpVector Translation vector.
     * @return Translation matrix.
     */
    public static Matrix4d createTranslationMatrix(Vector3d tmpVector) {
        Matrix4d tmpTranslationMatrix = new Matrix4d();
        tmpTranslationMatrix.setZero();
        tmpTranslationMatrix.setElement(0, 0, 1);
        tmpTranslationMatrix.setElement(1, 1, 1);
        tmpTranslationMatrix.setElement(2, 2, 1);
        tmpTranslationMatrix.setElement(3, 3, 1);
        tmpTranslationMatrix.setElement(0, 3, tmpVector.getX());
        tmpTranslationMatrix.setElement(1, 3, tmpVector.getY());
        tmpTranslationMatrix.setElement(2, 3, tmpVector.getZ());
        return tmpTranslationMatrix;
    }
}
