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
package de.gnwi.mfsim.model.graphics;

import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.gui.util.SpinStepInfo;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import de.gnwi.spices.IPointInSpace;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Static graphics utility methods
 *
 * @author Achim Zielesny
 */
public final class GraphicsUtils {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String utility methods
     */
    private static final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Simulation box slicer related methods">
    /**
     * Returns key for scaled slice image cache
     *
     * @param aBoxView Box view
     * @param aSliceIndex Index of slice
     * @param aWidthInPixel Width of return image in pixel
     * @param aHeightInPixel Height of return image in pixel
     * @return Key for scaled slice image cache
     */
    public static String getKeyForScaledSliceImageCache(SimulationBoxViewEnum aBoxView, int aSliceIndex, int aWidthInPixel, int aHeightInPixel) {
        return aBoxView.name() + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(aSliceIndex) + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(aWidthInPixel)
                + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(aHeightInPixel);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Image related methods">
    // <editor-fold defaultstate="collapsed" desc="- createImage">
    /**
     * Creates a buffered image from an image
     *
     * @param anImage Image
     * @return Corresponding buffered image or null
     */
    public static BufferedImage createBufferedImage(Image anImage) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImage == null) {
            return null;
        }

        // </editor-fold>
        // NOTE: Use BufferedImage.TYPE_INT_RGB and not
        // BufferedImage.TYPE_INT_ARGB since BufferedImage.TYPE_INT_RGB results
        // in faster performance and smaller images
        BufferedImage tmpBufferedImage = new BufferedImage(anImage.getWidth(null), anImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D tmpGraphics2D = tmpBufferedImage.createGraphics();
        tmpGraphics2D.drawImage(anImage, 0, 0, null);
        tmpGraphics2D.dispose();
        return tmpBufferedImage;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- readImageFromFile">
    /**
     * Returns image with specified pathname. NOTE: ImageIO class is NOT thread
     * safe! (thus synchronized)
     *
     * @param aFilePathname The pathname of the image file
     * @return Image with specified pathname or null if image is not available
     */
    public static synchronized BufferedImage readImageFromFile(String aFilePathname) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFilePathname == null || aFilePathname.isEmpty()) {
            return null;
        }

        // </editor-fold>
        File tmpImageFile = new File(aFilePathname);
        if (!tmpImageFile.isFile()) {
            return null;
        } else {
            try {
                return ImageIO.read(tmpImageFile);
            } catch (IOException anException) {
                ModelUtils.appendToLogfile(true, anException);
                return null;
            }
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- writeImageToFile">
    /**
     * Writes an image to file. NOTE: ImageIO class is NOT thread safe! (thus synchronized)
     *
     * @param aDestinationDirectory Destination directory
     * @param aFilename File name (without directory information)
     * @param anImage Image
     * @param anImageFileType Image file type
     * @return True: Operation was successful, false: Otherwise
     */
    public static synchronized boolean writeImageToFile(String aDestinationDirectory, String aFilename, BufferedImage anImage, ImageFileType anImageFileType) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDestinationDirectory == null || aDestinationDirectory.isEmpty() || !(new File(aDestinationDirectory)).isDirectory()) {
            return false;
        }
        if (aFilename == null || aFilename.isEmpty()) {
            return false;
        }
        if (anImage == null) {
            return false;
        }

        // </editor-fold>
        try {
            File tmpFile = new File(aDestinationDirectory, aFilename);
            if (tmpFile.isFile()) {
                if (!tmpFile.delete()) {
                    return false;
                }
            }
            if (anImageFileType == ImageFileType.JPG) {
                return GraphicsUtils.writeJpegImageToFile(anImage, tmpFile);
            } else {
                return ImageIO.write(anImage, anImageFileType.toFileTypeEnding(), tmpFile);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Writes an image to file. NOTE: ImageIO class is NOT thread safe! (thus synchronized)
     *
     * @param anImage Image
     * @param anImageFileType Image file type
     * @param aFile File
     * @return True: Operation was successful, false: Otherwise
     */
    public static synchronized boolean writeImageToFile(BufferedImage anImage, ImageFileType anImageFileType, File aFile) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFile == null) {
            return false;
        }
        if (anImage == null) {
            return false;
        }

        // </editor-fold>
        try {
            if (aFile.isFile()) {
                if (!aFile.delete()) {
                    return false;
                }
            }
            if (anImageFileType == ImageFileType.JPG) {
                return GraphicsUtils.writeJpegImageToFile(anImage, aFile);
            } else {
                return ImageIO.write(anImage, anImageFileType.toFileTypeEnding(), aFile);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Writes an image to file without checks and possible file delete. NOTE:
     * ImageIO class is NOT thread safe! (thus synchronized)
     *
     * @param anImage Image
     * @param anImageFileType Image file type
     * @param aFile File
     * @return True: Operation was successful, false: Otherwise
     */
    public static synchronized boolean writeImageToFileWithoutChecks(BufferedImage anImage, ImageFileType anImageFileType, File aFile) {
        try {
            if (anImageFileType == ImageFileType.JPG) {
                return GraphicsUtils.writeJpegImageToFileWithoutChecks(anImage, aFile);
            } else {
                return ImageIO.write(anImage, anImageFileType.toFileTypeEnding(), aFile);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Writes a sortable indexed image to file, i.e. anIndex = 3 and
     * aMaximumIndex = 100 the pathname is Path/Image003.ending 
     * NOTE: ImageIO is NOT thread safe! (thus synchronized)
     *
     * @param anImage Image
     * @param anImageFileType Image file type
     * @param aDestinationDirectory Full path of destination directory
     * @param anIndex Index for image
     * @param aMaximumIndex Maximum index for images
     * @return True: Operation was successful, false: Otherwise
     */
    public static synchronized boolean writeSortableIndexedImageToFile(BufferedImage anImage, ImageFileType anImageFileType, String aDestinationDirectory, int anIndex, int aMaximumIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImage == null) {
            return false;
        }
        if (aDestinationDirectory == null || aDestinationDirectory.isEmpty() || !(new File(aDestinationDirectory)).isDirectory()) {
            return false;
        }
        if (anIndex < 0 || aMaximumIndex < anIndex) {
            return false;
        }

        // </editor-fold>
        try {
            String tmpFilePathName = aDestinationDirectory + File.separatorChar + ModelDefinitions.PREFIX_OF_IMAGE_FILENAME
                    + GraphicsUtils.stringUtilityMethods.createSortablePositiveIntegerRepresentation(anIndex, aMaximumIndex) + "." + anImageFileType.toFileTypeEnding();
            if (anImageFileType == ImageFileType.JPG) {
                return GraphicsUtils.writeJpegImageToFile(anImage, new File(tmpFilePathName));
            } else {
                return ImageIO.write(anImage, anImageFileType.toFileTypeEnding(), new File(tmpFilePathName));
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Writes image to final zeros number file (see code). NOTE: ImageIO is NOT
     * thread safe! (thus synchronized)
     *
     * @param anImage Image
     * @param anImageFileType Image file type
     * @param aDestinationDirectory Full path of destination directory
     * @param aNumber Number for initial zeros number string
     * @return True: Operation was successful, false: Otherwise
     */
    public static synchronized boolean writeImageToInitialZerosNumberFile(BufferedImage anImage, ImageFileType anImageFileType, String aDestinationDirectory, int aNumber) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImage == null) {
            return false;
        }
        if (aDestinationDirectory == null || aDestinationDirectory.isEmpty() || !(new File(aDestinationDirectory)).isDirectory()) {
            return false;
        }
        if (aNumber < 0) {
            return false;
        }

        // </editor-fold>
        try {
            String tmpInitialZerosNumberString = GraphicsUtils.stringUtilityMethods.getNumberStringWithInitialZeros(aNumber, ModelDefinitions.NUMBER_OF_DIGITS_FOR_ZEROS_NUMBER_STRING);
            String tmpFilePathName = aDestinationDirectory + File.separatorChar + tmpInitialZerosNumberString + "." + anImageFileType.toFileTypeEnding();
            if (anImageFileType == ImageFileType.JPG) {
                return GraphicsUtils.writeJpegImageToFile(anImage, new File(tmpFilePathName));
            } else {
                return ImageIO.write(anImage, anImageFileType.toFileTypeEnding(), new File(tmpFilePathName));
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Writes JPEG image with defined quality to file. NOTE: ImageIO is NOT
     * thread safe! (thus synchronized)
     *
     * @param anImage Image
     * @param aFile File object
     * @return True: Operation was successful, false: Otherwise
     */
    public static synchronized boolean writeJpegImageToFile(BufferedImage anImage, File aFile) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImage == null) {
            return false;
        }
        if (aFile == null) {
            return false;
        }

        // </editor-fold>
        try {
            if (aFile.isFile()) {
                if (!aFile.delete()) {
                    return false;
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
        try {
            Iterator<ImageWriter> tmpIterator = ImageIO.getImageWritersByFormatName("jpg");
            if (tmpIterator.hasNext()) {
                ImageWriter tmpImageWriter = tmpIterator.next();
                ImageOutputStream tmpIoStream = ImageIO.createImageOutputStream(aFile);
                tmpImageWriter.setOutput(tmpIoStream);
                ImageWriteParam tmpParameters = tmpImageWriter.getDefaultWriteParam();
                tmpParameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                tmpParameters.setCompressionQuality(Preferences.getInstance().getJpegImageQuality());
                tmpImageWriter.write(null, new IIOImage(anImage, null, null), tmpParameters);
                tmpIoStream.flush();
                tmpImageWriter.dispose();
                tmpIoStream.close();
                return true;
            } else {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Writes JPEG image with defined quality to file without checks. NOTE:
     * ImageIO is NOT thread safe! (thus synchronized)
     *
     * @param anImage Image
     * @param aFile File object
     * @return True: Operation was successful, false: Otherwise
     */
    public static synchronized boolean writeJpegImageToFileWithoutChecks(BufferedImage anImage, File aFile) {
        try {
            Iterator<ImageWriter> tmpIterator = ImageIO.getImageWritersByFormatName("jpg");
            if (tmpIterator.hasNext()) {
                ImageWriter tmpImageWriter = tmpIterator.next();
                ImageOutputStream tmpIoStream = ImageIO.createImageOutputStream(aFile);
                tmpImageWriter.setOutput(tmpIoStream);
                ImageWriteParam tmpParameters = tmpImageWriter.getDefaultWriteParam();
                tmpParameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // Set quality of JPEG image (0.0F = low to 1.0F = high)
                tmpParameters.setCompressionQuality(Preferences.getInstance().getJpegImageQuality());
                tmpImageWriter.write(null, new IIOImage(anImage, null, null), tmpParameters);
                tmpIoStream.flush();
                tmpImageWriter.dispose();
                tmpIoStream.close();
                return true;
            } else {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Image to byte array conversion">
    /**
     * Compresses buffered image to JPEG encoded byte array. NOTE: This method
     * MUST be static synchronized otherwise images may be corrupt.
     *
     * @param aBufferedImage Buffered image
     * @return Byte array that JPEG encodes a buffered image or null if byte
     * array could not be created
     */
    public static synchronized byte[] convertBufferedImageToJpegEncodedByteArray(BufferedImage aBufferedImage) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBufferedImage == null) {
            return null;
        }

        // </editor-fold>
        try {
            Iterator<ImageWriter> tmpIterator = ImageIO.getImageWritersByFormatName("jpg");
            if (tmpIterator.hasNext()) {
                ImageWriter tmpImageWriter = tmpIterator.next();
                ByteArrayOutputStream tmpByteArrayOutputStream = new ByteArrayOutputStream();
                ImageOutputStream tmpIoStream = ImageIO.createImageOutputStream(tmpByteArrayOutputStream);
                tmpImageWriter.setOutput(tmpIoStream);
                ImageWriteParam tmpParameters = tmpImageWriter.getDefaultWriteParam();
                tmpParameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // Set quality of JPEG image (0.0F = low to 1.0F = high)
                tmpParameters.setCompressionQuality(Preferences.getInstance().getJpegImageQuality());
                tmpImageWriter.write(null, new IIOImage(aBufferedImage, null, null), tmpParameters);
                tmpIoStream.flush();
                tmpImageWriter.dispose();
                tmpIoStream.close();
                return tmpByteArrayOutputStream.toByteArray();
            } else {
                return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Decompresses JPEG encoded byte array (from method
     * convertBufferedImageToJpegEncodedByteArray()) to buffered image. NOTE:
     * This method MUST be static synchronized otherwise images may be corrupt.
     *
     * @param aJpegEncodedByteArray JPEG encoded byte array (from method
     * convertBufferedImageToJpegEncodedByteArray())
     * @return Buffered image or null if buffered image could not be created
     */
    public static synchronized BufferedImage convertJpegEncodedByteArrayToBufferedImage(byte[] aJpegEncodedByteArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJpegEncodedByteArray == null) {
            return null;
        }

        // </editor-fold>
        try {
            ByteArrayInputStream tmpByteArrayInputStream = new ByteArrayInputStream(aJpegEncodedByteArray);
            return ImageIO.read(tmpByteArrayInputStream);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Distances in plane">
    /**
     * Calculates the distance of two points in a plane in x direction
     *
     * @param aPoint1 Point on plane
     * @param aPoint2 Point on plane
     * @return Distance of two points on a plane in x direction
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public static double getDistanceInXInPlane(PointInPlane aPoint1, PointInPlane aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.abs(aPoint1.getX() - aPoint2.getX());
    }

    /**
     * Calculates the distance of two points in a plane in y direction
     *
     * @param aPoint1 Point on plane
     * @param aPoint2 Point on plane
     * @return Distance of two points on a plane in y direction
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public static double getDistanceInYInPlane(PointInPlane aPoint1, PointInPlane aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.abs(aPoint1.getY() - aPoint2.getY());
    }

    /**
     * Calculates the distance of two points in a plane
     *
     * @param aPoint1 Point on plane
     * @param aPoint2 Point on plane
     * @return Distance of two points on a plane
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public static double getDistanceInPlane(PointInPlane aPoint1, PointInPlane aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.sqrt(Math.pow(aPoint1.getX() - aPoint2.getX(), 2) + Math.pow(aPoint1.getY() - aPoint2.getY(), 2));
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Is in plane">
    /**
     * Returns whether two points are on a plane
     *
     * @param aFirstPoint First point
     * @param aSecondPoint Second poitn
     * @return True: Two points are on a plane, false: Otherwise
     */
    public static boolean isInPlane(IPointInSpace aFirstPoint, IPointInSpace aSecondPoint) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFirstPoint == null || aSecondPoint == null) {
            return false;
        }

        // </editor-fold>
        return aFirstPoint.getX() == aSecondPoint.getX() || aFirstPoint.getY() == aSecondPoint.getY() || aFirstPoint.getZ() == aSecondPoint.getZ();
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Debugging related methods">
    /**
     * Write spin step info information to System.out
     *
     * @param anInfo Info
     * @param aSpinStepInfo Spin step info
     */
    public static synchronized void writeSpinStepInfoInformation(String anInfo, SpinStepInfo aSpinStepInfo) {
        System.out.println(anInfo + ": BoxViewIndex = " + String.valueOf(aSpinStepInfo.getBoxViewIndex()));
        System.out.println(anInfo + ": Angle X      = " + String.valueOf(aSpinStepInfo.getRotationAroundXaxisAngle()));
        System.out.println(anInfo + ": Offset X     = " + String.valueOf(aSpinStepInfo.getRotationAroundXaxisOffset()));
        System.out.println(anInfo + ": Sum X        = " + String.valueOf(aSpinStepInfo.getRotationAroundXaxisAngle() + aSpinStepInfo.getRotationAroundXaxisOffset()));
        System.out.println(anInfo + ": Angle Y      = " + String.valueOf(aSpinStepInfo.getRotationAroundYaxisAngle()));
        System.out.println(anInfo + ": Offset Y     = " + String.valueOf(aSpinStepInfo.getRotationAroundYaxisOffset()));
        System.out.println(anInfo + ": Sum Y        = " + String.valueOf(aSpinStepInfo.getRotationAroundYaxisAngle() + aSpinStepInfo.getRotationAroundYaxisOffset()));
        System.out.println(anInfo + ": Angle Z      = " + String.valueOf(aSpinStepInfo.getRotationAroundZaxisAngle()));
        System.out.println(anInfo + ": Offset Z     = " + String.valueOf(aSpinStepInfo.getRotationAroundZaxisOffset()));
        System.out.println(anInfo + ": Sum Z        = " + String.valueOf(aSpinStepInfo.getRotationAroundZaxisAngle() + aSpinStepInfo.getRotationAroundZaxisOffset()));
        System.out.println("---");
    }

    // </editor-fold>

}
