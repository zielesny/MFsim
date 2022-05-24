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

import de.gnwi.mfsim.gui.chart.XySeriesManipulator;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.dialog.DialogProgress;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.model.util.ExtensionFileFilter;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.gui.control.CustomPanelImage;
import de.gnwi.mfsim.gui.control.CustomPanelValueItemEdit;
import de.gnwi.mfsim.model.util.SimulationBoxChangeInfo;
import de.gnwi.mfsim.model.job.JobInput;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumStatus;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.peptide.base.AminoAcid;
import de.gnwi.mfsim.model.peptide.PdbToDpd;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import javax.swing.tree.*;
import javax.vecmath.Vector3d;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.graphics.IImageProvider;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Utility class with static utitlity methods for GUI
 *
 * @author Achim Zielesny
 */
public final class GuiUtils {

    // <editor-fold defaultstate="collapsed" desc="Private static final class variables">
    /**
     * Utility string methods
     */
    private static final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Utility file methods
     */
    private static final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();
    
    /**
     * Tiny threshold
     */
    private static final double TINY_THRESHOLD = 1E-6;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Buffered image inversion filter
     */
    private static BufferedImageOp bufferedImageInversionFilter;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Static definition blocks">
    static {
        short[] tmpColorInversionTable = new short[256];
        for (int i = 0; i < 256; i++) {
            tmpColorInversionTable[i] = (short) (255 - i);
        }
        GuiUtils.bufferedImageInversionFilter = new LookupOp(new ShortLookupTable(0, tmpColorInversionTable), null);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static synchronized methods">
    // <editor-fold defaultstate="collapsed" desc="CustomPanelImage related methods">
    /**
     * Sets image of resource. NOTE: ImageIO is not thread safe! (thus synchronized)
     *
     * @param anImagePanel CustomPanelImage instance
     * @param anImageResourceFilename Resource filename of image
     */
    public static synchronized void setResourceImage(CustomPanelImage anImagePanel, String anImageResourceFilename) {
        InputStream tmpInputStream = null;
        try {
            String imageResourcePathname = GuiDefinitions.RESOURCE_LOCATION + anImageResourceFilename;
            tmpInputStream = GuiUtils.fileUtilityMethods.getResourceInputStream(GuiUtils.class, imageResourcePathname);
            if (tmpInputStream != null) {
                BufferedImage tmpImage = ImageIO.read(tmpInputStream);
                anImagePanel.setBasicImage(tmpImage);
            }
        } catch (IOException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return;
                }
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Resource file related methods">
    /**
     * Returns specified image of resource. NOTE: ImageIO is not thread safe!
     * (thus synchronized)
     *
     * @param anImageResourceFilename Image filename that is in resource
     * @return Image of resource or null if specified versioned image does not
     * exist
     */
    public static synchronized BufferedImage getImageOfResource(String anImageResourceFilename) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImageResourceFilename == null || anImageResourceFilename.isEmpty()) {
            return null;
        }

        // </editor-fold>
        String imageResourceFilename = GuiDefinitions.RESOURCE_LOCATION + anImageResourceFilename;
        InputStream tmpInputStream = null;
        try {
            tmpInputStream = GuiUtils.fileUtilityMethods.getResourceInputStream(GuiUtils.class, imageResourceFilename);
            if (tmpInputStream != null) {
                BufferedImage tmpImage = ImageIO.read(tmpInputStream);
                return tmpImage;
            } else {
                return null;
            }
        } catch (IOException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }

    /**
     * Returns specified versioned image of resource. NOTE: ImageIO is not
     * thread safe! (thus synchronized)
     *
     * @param anImageResourceFilenamePrefix Prefix of versioned image filename
     * that is in resource
     * @param anImageResourceEnding Ending of versioned image filename that is
     * in resource
     * @param aVersion Version
     * @return Specified versioned image of resource or null if specified
     * versioned image does not exist
     */
    public static synchronized BufferedImage getVersionedImageOfResource(String anImageResourceFilenamePrefix, String anImageResourceEnding, int aVersion) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImageResourceFilenamePrefix == null || anImageResourceFilenamePrefix.isEmpty()) {
            return null;
        }
        if (anImageResourceEnding == null || anImageResourceEnding.isEmpty()) {
            return null;
        }
        // </editor-fold>
        String imageResourceFilename = GuiDefinitions.RESOURCE_LOCATION + anImageResourceFilenamePrefix + String.valueOf(aVersion) + anImageResourceEnding;
        InputStream tmpInputStream = null;
        try {
            tmpInputStream = GuiUtils.fileUtilityMethods.getResourceInputStream(GuiUtils.class, imageResourceFilename);
            if (tmpInputStream != null) {
                BufferedImage tmpImage = ImageIO.read(tmpInputStream);
                return tmpImage;
            } else {
                return null;
            }
        } catch (IOException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            }
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static methods">
    // <editor-fold defaultstate="collapsed" desc="External Browser/viewer start">
    /**
     * Starts default browser with specified URI
     *
     * @param aURI URI
     * @return false: Startup process failed, true: Otherwise
     */
    public static boolean startBrowser(String aURI) {
        try {
            Desktop.getDesktop().browse(new URI(aURI));
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Starts default browser with specified URI
     *
     * @param aURI URI, e.g. created by (new File(aFullDirectoryPath)).toURI()
     * @return false: Startup process failed, true: Otherwise
     */
    public static boolean startBrowser(URI aURI) {
        try {
            Desktop.getDesktop().browse(aURI);
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Starts default viewer with specified file pathname
     *
     * @param aPathname File pathname
     * @return false: Startup process failed, true: Otherwise
     */
    public static boolean startViewer(String aPathname) {
        try {
            Desktop.getDesktop().open(new File(aPathname));
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Frame and dialog related methods">
    /**
     * Centers dialog on screen
     *
     * @param aDialog Dialog to be centered
     */
    public static void centerDialogOnScreen(JDialog aDialog) {
        aDialog.setLocationRelativeTo(null);
    }

    /**
     * Centers frame on screen
     *
     * @param aFrame Frame to be centered
     */
    public static void centerFrameOnScreen(JFrame aFrame) {
        Dimension tmpScreenSizeDimension = Toolkit.getDefaultToolkit().getScreenSize();
        aFrame.setLocation((tmpScreenSizeDimension.width - aFrame.getSize().width) / 2, (tmpScreenSizeDimension.height - aFrame.getSize().height) / 2);
    }

    /**
     * Checks dialog size against screen size and corrects if necessary
     *
     * @param aDialog Dialog to be checked
     * @return True: Dialog was resized, false: Otherwise
     */
    public static boolean checkDialogSize(JDialog aDialog) {
        int tmpDialogWidth = aDialog.getWidth();
        int tmpDialogHeight = aDialog.getHeight();
        Dimension tmpScreenSizeDimension = Toolkit.getDefaultToolkit().getScreenSize();
        boolean tmpIsResize = false;
        if (tmpDialogWidth > tmpScreenSizeDimension.width) {
            tmpDialogWidth = tmpScreenSizeDimension.width;
            tmpIsResize = true;
        }
        if (tmpDialogHeight > tmpScreenSizeDimension.height) {
            tmpDialogHeight = tmpScreenSizeDimension.height;
            tmpIsResize = true;
        }
        if (tmpIsResize) {
            aDialog.setSize(tmpDialogWidth, tmpDialogHeight);
        }
        return tmpIsResize;
    }

    /**
     * Checks frame size against screen size and corrects if necessary
     *
     * @param aFrame Frame to be checked
     */
    public static void checkFrameSize(JFrame aFrame) {
        int tmpFrameWidth = aFrame.getWidth();
        int tmpFrameHeight = aFrame.getHeight();
        Dimension tmpScreenSizeDimension = Toolkit.getDefaultToolkit().getScreenSize();
        boolean tmpIsResize = false;
        if (tmpFrameWidth > tmpScreenSizeDimension.width) {
            tmpFrameWidth = tmpScreenSizeDimension.width;
            tmpIsResize = true;
        }
        if (tmpFrameHeight > tmpScreenSizeDimension.height) {
            tmpFrameHeight = tmpScreenSizeDimension.height;
            tmpIsResize = true;
        }
        if (tmpIsResize) {
            aFrame.setSize(tmpFrameWidth, tmpFrameHeight);
        }
    }

    /**
     * Minimizes dialog size to default
     *
     * @param aDialog Dialog to be minimized
     * @return True: Dialog was resized, false: Otherwise
     */
    public static boolean minimizeDialogSize(JDialog aDialog) {
        int tmpDialogWidth = aDialog.getWidth();
        int tmpDialogHeight = aDialog.getHeight();
        boolean tmpIsResize = false;
        if (tmpDialogWidth != ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            tmpDialogWidth = ModelDefinitions.MINIMUM_DIALOG_WIDTH;
            tmpIsResize = true;
        }
        if (tmpDialogHeight != ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            tmpDialogHeight = ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
            tmpIsResize = true;
        }
        if (tmpIsResize) {
            aDialog.setSize(tmpDialogWidth, tmpDialogHeight);
        }
        return tmpIsResize;
    }

    /**
     * Maximizes dialog size to screen size
     *
     * @param aDialog Dialog to be maximized
     * @return True: Dialog was resized, false: Otherwise
     */
    public static boolean maximizeDialogSize(JDialog aDialog) {
        int tmpDialogWidth = aDialog.getWidth();
        int tmpDialogHeight = aDialog.getHeight();
        Dimension tmpScreenSizeDimension = Toolkit.getDefaultToolkit().getScreenSize();
        boolean tmpIsResize = false;
        if (tmpDialogWidth != (int) Math.floor(tmpScreenSizeDimension.width * GuiDefinitions.WINDOW_MAXIMUM_SIZE_REDUCTION_FACTOR)) {
            tmpDialogWidth = (int) Math.floor(tmpScreenSizeDimension.width * GuiDefinitions.WINDOW_MAXIMUM_SIZE_REDUCTION_FACTOR);
            tmpIsResize = true;
        }
        if (tmpDialogHeight != (int) Math.floor(tmpScreenSizeDimension.height * GuiDefinitions.WINDOW_MAXIMUM_SIZE_REDUCTION_FACTOR)) {
            tmpDialogHeight = (int) Math.floor(tmpScreenSizeDimension.height * GuiDefinitions.WINDOW_MAXIMUM_SIZE_REDUCTION_FACTOR);
            tmpIsResize = true;
        }
        if (tmpIsResize) {
            aDialog.setSize(tmpDialogWidth, tmpDialogHeight);
        }
        return tmpIsResize;
    }

    /**
     * Set custom dialog size
     *
     * @param aDialog Dialog to be set to custom size
     * @return True: Dialog was resized, false: Otherwise
     */
    public static boolean setCustomDialogSize(JDialog aDialog) {
        int tmpDialogWidth = aDialog.getWidth();
        int tmpDialogHeight = aDialog.getHeight();
        boolean tmpIsResize = false;
        if (tmpDialogWidth != Preferences.getInstance().getCustomDialogWidth()) {
            tmpDialogWidth = Preferences.getInstance().getCustomDialogWidth();
            tmpIsResize = true;
        }
        if (tmpDialogHeight != Preferences.getInstance().getCustomDialogHeight()) {
            tmpDialogHeight = Preferences.getInstance().getCustomDialogHeight();
            tmpIsResize = true;
        }
        if (tmpIsResize) {
            aDialog.setSize(tmpDialogWidth, tmpDialogHeight);
            if (GuiUtils.checkDialogSize(aDialog)) {
                Preferences.getInstance().setCustomDialogWidth(aDialog.getWidth());
                Preferences.getInstance().setCustomDialogHeight(aDialog.getHeight());
            }
        }
        return tmpIsResize;
    }

    /**
     * Set custom dialog size preferences
     *
     * @param aDialog Dialog
     * @return True: Dialog was resized, false: Otherwise
     */
    public static boolean setCustomDialogSizePreferences(JDialog aDialog) {
        Preferences.getInstance().setCustomDialogWidth(aDialog.getWidth());
        Preferences.getInstance().setCustomDialogHeight(aDialog.getHeight());
        ValueItemContainer tmpCustomDialogSizePreferencesValueItemContainer = Preferences.getInstance().getCustomDialogSizePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("CustomDialogSize.Title"), tmpCustomDialogSizePreferencesValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpCustomDialogSizePreferencesValueItemContainer);
            GuiUtils.setCustomDialogSize(aDialog);
            GuiUtils.centerDialogOnScreen(aDialog);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets top level display of frame according to flag
     *
     * @param aFrame Frame to be set
     * @param aFlag true: Frame is always on top, false: Frame behaves normal
     */
    public static void setFrameAlwaysOnTop(JFrame aFrame, boolean aFlag) {
        aFrame.setAlwaysOnTop(aFlag);
    }

    /**
     * Sets values for maximum height and width of MainFrame in Preferences
 so that aspect ratio of home panel image remains unchanged
     *
     * @return true: Operation was successful, false: Otherwise
     */
    public static boolean setMainFrameMaximumHeightAndWidthInBasicPreferences() {

        // <editor-fold defaultstate="collapsed" desc="Calculation info">
        // Outer rectangle: c: height, d: width (= GUI-MainFrame)
        // Inner rectangle: a: height, b: width (= Image-Panel)
        //
        // r = a/b : ratio height/width
        //
        // NOTE: (c + deltaY)/(d + deltaX) -> (a + deltaY)/(b + deltaX) = r
        // -> deltaY = r*(b + deltaX) - a
        // -> deltaX = (a + deltaY)/r - b
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get home panel image width and height">
        double tmpHomePanelImageHeight;
        double tmpHomePanelImageWidth;
        BufferedImage tmpImage = GuiUtils.getImageOfResource(GuiDefinitions.HOME_IMAGE_FILENAME_PREFIX, GuiDefinitions.RESOURCE_IMAGE_ENDING);
        if (tmpImage == null) {
            return false;
        } else {
            tmpHomePanelImageHeight = (double) tmpImage.getHeight(null);
            tmpHomePanelImageWidth = (double) tmpImage.getWidth(null);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate maximum width and height of MainFrame">
        Dimension tmpScreenSizeDimension = Toolkit.getDefaultToolkit().getScreenSize();
        double tmpScreenRatioOfHeightToWidth = (double) tmpScreenSizeDimension.height / (double) tmpScreenSizeDimension.width;
        double tmpDefaultRatioOfHeightToWidth = tmpHomePanelImageHeight / tmpHomePanelImageWidth;
        int tmpDeltaX;
        int tmpDeltaY;
        if (tmpScreenRatioOfHeightToWidth >= tmpDefaultRatioOfHeightToWidth) {
            tmpDeltaX = (int) Math.floor(tmpScreenSizeDimension.width * GuiDefinitions.MAIN_FRAME_MAXIMUM_SIZE_REDUCTION_FACTOR) - ModelDefinitions.MINIMUM_FRAME_WIDTH;
            tmpDeltaY = (int) Math.floor((tmpHomePanelImageHeight / tmpHomePanelImageWidth) * (tmpHomePanelImageWidth + (double) tmpDeltaX) - tmpHomePanelImageHeight);
        } else {
            tmpDeltaY = (int) Math.floor(tmpScreenSizeDimension.height * GuiDefinitions.MAIN_FRAME_MAXIMUM_SIZE_REDUCTION_FACTOR) - ModelDefinitions.MINIMUM_FRAME_HEIGHT;
            tmpDeltaX = (int) Math.floor((double) (tmpHomePanelImageHeight + tmpDeltaY) / (tmpHomePanelImageHeight / tmpHomePanelImageWidth) - tmpHomePanelImageWidth);
        }
        Preferences.getInstance().setMainFrameMaximumWidth(ModelDefinitions.MINIMUM_FRAME_WIDTH + tmpDeltaX);
        Preferences.getInstance().setMainFrameMaximumHeight(ModelDefinitions.MINIMUM_FRAME_HEIGHT + tmpDeltaY);
        return true;

        // </editor-fold>
    }

    /**
     * Sets size of frame
     *
     * @param aFrame The frame which size is to be set
     * @param aWidth Desired width
     * @param aHeight Desired height
     */
    public static void setSizeOfFrame(JFrame aFrame, int aWidth, int aHeight) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFrame == null || aWidth < 0 || aHeight < 0) {
            return;
        }

        // </editor-fold>
        aFrame.setSize(aWidth, aHeight);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Screen resolution related methods">
    /**
     * Checks screen size
     *
     * @return True: Screen resolution is sufficient, false: Otherwise
     */
    public static boolean checkScreenSize() {
        int tmpMinimumScreenSizeWidth = ModelDefinitions.MINIMUM_FRAME_WIDTH > ModelDefinitions.MINIMUM_DIALOG_WIDTH ? ModelDefinitions.MINIMUM_FRAME_WIDTH : ModelDefinitions.MINIMUM_DIALOG_WIDTH;
        int tmpMinimumScreenSizeHeight = ModelDefinitions.MINIMUM_FRAME_HEIGHT > ModelDefinitions.MINIMUM_DIALOG_HEIGHT ? ModelDefinitions.MINIMUM_FRAME_HEIGHT : ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
        Dimension tmpScreenSizeDimension = Toolkit.getDefaultToolkit().getScreenSize();
        return !(tmpScreenSizeDimension.width < tmpMinimumScreenSizeWidth || tmpScreenSizeDimension.height < tmpMinimumScreenSizeHeight);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Text field related methods">
    /**
     * Removes key and document listeners from text field
     *
     * @param aTextField Text field
     */
    public static void removeKeyAndDocumentListeners(JTextField aTextField) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTextField == null) {
            return;
        }

        // </editor-fold>
        KeyListener[] tmpKeyListeners = aTextField.getKeyListeners();
        if (tmpKeyListeners != null && tmpKeyListeners.length > 0) {
            for (KeyListener tmpSingleKeyListener : tmpKeyListeners) {
                aTextField.removeKeyListener(tmpSingleKeyListener);
            }
        }
        PlainDocument tmpDocument = (PlainDocument) aTextField.getDocument();
        DocumentListener[] tmpDocumentListeners = tmpDocument.getDocumentListeners();
        if (tmpDocumentListeners != null && tmpDocumentListeners.length > 0) {
            for (DocumentListener tmpSingleDocumentListener : tmpDocumentListeners) {
                tmpDocument.removeDocumentListener(tmpSingleDocumentListener);
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Combo box related methods">
    /**
     * Removes item listeners from combo box
     *
     * @param aComboBox Combo box
     */
    public static void removeItemListeners(JComboBox aComboBox) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aComboBox == null) {
            return;
        }

        // </editor-fold>
        ItemListener[] tmpItemListeners = aComboBox.getItemListeners();
        if (tmpItemListeners != null && tmpItemListeners.length > 0) {
            for (ItemListener tmpSingleItemListener : tmpItemListeners) {
                aComboBox.removeItemListener(tmpSingleItemListener);
            }
        }
    }

    /**
     * Sets combo box for time step display selection. NOTE: This methods MUST
 correspond to GuiUtils.evaluateComboBoxForTimeStepDisplaySelection()
     *
     * @param aComboBox Combo box to be set
     */
    public static void setComboBoxForTimeStepDisplaySelection(JComboBox aComboBox) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aComboBox == null) {
            return;
        }

        // </editor-fold>
        String tmpPrefix = ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Display") + " ";
        // IMPORTANT: ModelDefinitions.MINIMUM_TIME_STEP_DISPLAY_SLICER must be 1
        String[] tmpTimeStepDisplayArray = new String[ModelDefinitions.MAXIMUM_TIME_STEP_DISPLAY_SLICER];
        for (int i = 1; i <= ModelDefinitions.MAXIMUM_TIME_STEP_DISPLAY_SLICER; i++) {
            tmpTimeStepDisplayArray[i - 1] = tmpPrefix + String.valueOf(i);
        }
        aComboBox.setModel(new DefaultComboBoxModel(tmpTimeStepDisplayArray));
        aComboBox.setSelectedItem(tmpPrefix + String.valueOf(Preferences.getInstance().getTimeStepDisplaySlicer()));
    }

    /**
     * Evaluates combo box for time step display selection. NOTE: This methods
 MUST correspond to GuiUtils.setComboBoxForTimeStepDisplaySelection()
     *
     * @param aComboBox Combo box to be set
     */
    public static void evaluateComboBoxForTimeStepDisplaySelection(JComboBox aComboBox) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aComboBox == null) {
            return;
        }

        // </editor-fold>
        String tmpSelectedItem = aComboBox.getSelectedItem().toString();
        String tmpPrefix = ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Display") + " ";
        // Isolate time step display at end of string
        String tmpTimeStepDisplayString = tmpSelectedItem.substring(tmpPrefix.length());
        Preferences.getInstance().setTimeStepDisplaySlicer(Integer.valueOf(tmpTimeStepDisplayString));
    }

    /**
     * Sets combo box for simulation box display selection. NOTE: This methods
 MUST correspond to
 GuiUtils.evaluateComboBoxForSimulationBoxDisplaySelection()
     *
     * @param aComboBox Combo box to be set
     */
    public static void setComboBoxForSimulationBoxDisplaySelection(JComboBox aComboBox) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aComboBox == null) {
            return;
        }

        // </editor-fold>
        aComboBox.setModel(new DefaultComboBoxModel(new String[]{
            ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Slicer"),
            ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.JmolViewer")
        }));
        if (Preferences.getInstance().isSimulationBoxSlicer()) {
            aComboBox.setSelectedItem(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Slicer"));
        } else {
            aComboBox.setSelectedItem(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.JmolViewer"));
        }
    }

    /**
     * Evaluates combo box for simulation box display selection. NOTE: This
 methods MUST correspond to
 GuiUtils.setComboBoxForSimulationBoxDisplaySelection()
     *
     * @param aComboBox Combo box to be set
     */
    public static void evaluateComboBoxForSimulationBoxDisplaySelection(JComboBox aComboBox) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aComboBox == null) {
            return;
        }

        // </editor-fold>
        String tmpSelectedItem = aComboBox.getSelectedItem().toString();
        if (tmpSelectedItem.equals(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Slicer"))) {
            Preferences.getInstance().setSimulationBoxSlicer(true);
        } else if (tmpSelectedItem.equals(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.JmolViewer"))) {
            Preferences.getInstance().setSimulationBoxSlicer(false);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Tree related methods">
    /**
     * If expand is true all nodes of the tree are expanded. Otherwise all nodes
     * of the tree are collapsed. Retains selection.
     *
     * @param aTree Tree
     * @param anExpandFlag True: Expands all nodes in the tree , collapses
     * otherwise.
     */
    public static void expandAndRetainSelection(JTree aTree, boolean anExpandFlag) {
        TreePath tmpTreePath = aTree.getSelectionPath();
        GuiUtils.expandAllNodesOfTree(aTree, anExpandFlag);
        if (tmpTreePath == null) {
            GuiUtils.selectFirstLeaf(aTree);
        } else {
            aTree.setSelectionPath(tmpTreePath);
            aTree.scrollPathToVisible(tmpTreePath);
            if (!anExpandFlag) {
                // NOTE: A double call of scrollPathToVisible() seems to be
                // necessary for correct function in specific situations when
                // tree is collapsed
                aTree.scrollPathToVisible(tmpTreePath);
            }
        }
    }

    /**
     * If expand is true all nodes of the tree are expanded. Otherwise all nodes
     * of the tree are collapsed. Selects first leaf.
     *
     * @param aTree Tree
     * @param anExpandFlag True: Expands all nodes in the tree , collapses
     * otherwise.
     */
    public static void expandAndSelectFirstLeaf(JTree aTree, boolean anExpandFlag) {
        GuiUtils.expandAllNodesOfTree(aTree, anExpandFlag);
        GuiUtils.selectFirstLeaf(aTree);
    }

    /**
     * If expand is true all nodes of the tree are expanded. Otherwise all nodes
     * of the tree are collapsed. Selects first leaf/value item with block name.
     *
     * @param aTree Tree
     * @param anExpandFlag True: Expands all nodes in the tree, collapses
     * otherwise
     * @param aBlockName Block name
     */
    public static void expandAndSelectFirstOfBlock(JTree aTree, boolean anExpandFlag, String aBlockName) {
        GuiUtils.expandAllNodesOfTree(aTree, anExpandFlag);
        GuiUtils.selectFirstOfBlock(aTree, aBlockName);
    }

    /**
     * If expand is true all nodes of the tree are expanded. Otherwise all nodes
     * of the tree are collapsed. Selects leaf/value item with defined name.
     *
     * @param aTree Tree
     * @param anExpandFlag True: Expands all nodes in the tree, collapses
     * otherwise
     * @param aValueItemName Value item name
     */
    public static void expandAndSelectDefinedLeaf(JTree aTree, boolean anExpandFlag, String aValueItemName) {
        GuiUtils.expandAllNodesOfTree(aTree, anExpandFlag);
        GuiUtils.selectDefinedLeaf(aTree, aValueItemName);
    }

    /**
     * Fills tree with value items and selects first value item. NOTE: All value
     * items MUST have a node name array with identical first string which will
     * be the root of the tree.
     *
     * @param aTree Tree
     * @param aValueItemArray Value item array (NOTE: All value items MUST have
     * a node name array with identical first string which will be the root of
     * the tree)
     * @param aValueItemStatus Value item status: Only value items that match
     * status are filled into tree
     */
    public static void fillTreeWithValueItemsAndSelectFirstLeaf(JTree aTree, ValueItem[] aValueItemArray, ValueItemEnumStatus aValueItemStatus) {
        if (GuiUtils.fillTreeWithValueItems(aTree, aValueItemArray, aValueItemStatus)) {
            GuiUtils.expandAndSelectFirstLeaf(aTree, false);
        }
    }

    /**
     * Fills tree with value items and selects first value item of defined
     * block. NOTE: All value items MUST have a node name array with identical
     * first string which will be the root of the tree.
     *
     * @param aTree Tree
     * @param aValueItemArray Value item array (NOTE: All value items MUST have
     * a node name array with identical first string which will be the root of
     * the tree)
     * @param aValueItemStatus Value item status: Only value items that match
     * status are filled into tree
     * @param aBlockName Block name
     */
    public static void fillTreeWithValueItemsAndSelectFirstOfBlock(JTree aTree, ValueItem[] aValueItemArray, ValueItemEnumStatus aValueItemStatus, String aBlockName) {
        if (GuiUtils.fillTreeWithValueItems(aTree, aValueItemArray, aValueItemStatus)) {
            GuiUtils.expandAndSelectFirstOfBlock(aTree, false, aBlockName);
        }
    }

    /**
     * Fills tree with value items and selects value item with defined name.
     * NOTE: All value items MUST have a node name array with identical first
     * string which will be the root of the tree.
     *
     * @param aTree Tree
     * @param aValueItemArray Value item array (NOTE: All value items MUST have
     * a node name array with identical first string which will be the root of
     * the tree)
     * @param aValueItemStatus Value item status: Only value items that match
     * status are filled into tree
     * @param aValueItemName Value item name
     */
    public static void fillTreeWithValueItemsAndSelectDefinedLeaf(JTree aTree, ValueItem[] aValueItemArray, ValueItemEnumStatus aValueItemStatus, String aValueItemName) {
        if (GuiUtils.fillTreeWithValueItems(aTree, aValueItemArray, aValueItemStatus)) {
            GuiUtils.expandAndSelectDefinedLeaf(aTree, false, aValueItemName);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Private tree related methods">
    /**
     * If expand is true, expands all nodes in the tree. Otherwise, collapses
     * all nodes in the tree.
     *
     * @param aTree Tree
     * @param anExpandFlag True: Expands all nodes in the tree, collapses
     * otherwise
     */
    private static void expandAllNodesOfTree(JTree aTree, boolean anExpandFlag) {
        TreeNode tmpRoot = (TreeNode) aTree.getModel().getRoot();
        // Traverse tree from root
        GuiUtils.expandAllNodesOfTree(aTree, new TreePath(tmpRoot), anExpandFlag);
    }

    /**
     * Supplement method for expandAllNodesOfTree(JTree aTree, boolean
     * anExpandFlag)
     *
     * @param aTree Tree
     * @param aParentPath Parent tree path
     * @param anExpandFlag True: Expands all nodes in the tree, collapses
     * otherwise
     */
    private static void expandAllNodesOfTree(JTree aTree, TreePath aParentPath, boolean anExpandFlag) {
        // Traverse children
        TreeNode tmpLastNode = (TreeNode) aParentPath.getLastPathComponent();
        if (tmpLastNode.getChildCount() >= 0) {
            for (Enumeration<?> tmpEnum = tmpLastNode.children(); tmpEnum.hasMoreElements();) {
                TreeNode tmpNode = (TreeNode) tmpEnum.nextElement();
                TreePath tmpPath = aParentPath.pathByAddingChild(tmpNode);
                GuiUtils.expandAllNodesOfTree(aTree, tmpPath, anExpandFlag);
            }
        }
        // Expansion or collapse must be done bottom-up
        if (anExpandFlag) {
            aTree.expandPath(aParentPath);
        } else {
            aTree.collapsePath(aParentPath);
        }
    }

    /**
     * Fills tree with value items. NOTE: All value items MUST have a node name
     * array with identical first string which will be the root of the tree.
     *
     * @param aTree Tree
     * @param aValueItemArray Value item array (NOTE: All value items MUST have
     * a node name array with identical first string which will be the root of
     * the tree)
     * @param aValueItemStatus Value item status: Only value items that match
     * status are filled into tree
     * @return True: Operation successful, false: Otherwise
     */
    private static boolean fillTreeWithValueItems(JTree aTree, ValueItem[] aValueItemArray, ValueItemEnumStatus aValueItemStatus) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTree == null || aValueItemArray == null || aValueItemArray.length == 0) {
            DefaultTreeModel tmpTreeModel = (DefaultTreeModel) aTree.getModel();
            tmpTreeModel.setRoot(null);
            return false;
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Clear tree">
        DefaultTreeModel tmpTreeModel = (DefaultTreeModel) aTree.getModel();
        tmpTreeModel.setRoot(null);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check status and display mode of value items">
        LinkedList<ValueItem> tmpValueItemList = new LinkedList<ValueItem>();
        for (int i = 0; i < aValueItemArray.length; i++) {
            if (aValueItemArray[i].isDisplayed() && aValueItemArray[i].hasStatus(aValueItemStatus) && aValueItemArray[i].getNodeNames() != null) {
                tmpValueItemList.addLast(aValueItemArray[i]);
            }
        }
        ValueItem[] tmpValueItemArray = null;
        if (tmpValueItemList.size() > 0) {
            tmpValueItemArray = tmpValueItemList.toArray(new ValueItem[0]);
        } else {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set root">
        DefaultMutableTreeNode tmpRoot = new DefaultMutableTreeNode(tmpValueItemArray[0].getNodeNames()[0]);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Treat first value item">
        DefaultMutableTreeNode tmpCurrentTreeNode = tmpRoot;
        for (int i = 1; i < tmpValueItemArray[0].getNodeNames().length; i++) {
            DefaultMutableTreeNode tmpNewNode = new DefaultMutableTreeNode(tmpValueItemArray[0].getNodeNames()[i]);
            tmpCurrentTreeNode.add(tmpNewNode);
            tmpCurrentTreeNode = tmpNewNode;
        }
        tmpCurrentTreeNode.add(new DefaultMutableTreeNode(tmpValueItemArray[0]));

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Treat following value items">
        for (int i = 1; i < tmpValueItemArray.length; i++) {
            if (tmpValueItemArray[i].getNodeNames().length == tmpValueItemArray[i - 1].getNodeNames().length) {
                // <editor-fold defaultstate="collapsed" desc="Node namess have same length">
                int tmpIdentityIndex = ModelUtils.getIdentityIndex(tmpValueItemArray[i].getNodeNames(), tmpValueItemArray[i - 1].getNodeNames());
                if (tmpIdentityIndex < 0) {
                    return false;
                }
                if (tmpIdentityIndex == tmpValueItemArray[i].getNodeNames().length - 1) {
                    // <editor-fold defaultstate="collapsed" desc="Node namess are identical">
                    tmpCurrentTreeNode.add(new DefaultMutableTreeNode(tmpValueItemArray[i]));

                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Node namess are different">
                    int tmpDifference = tmpValueItemArray[i].getNodeNames().length - 1 - tmpIdentityIndex;
                    for (int k = 0; k < tmpDifference; k++) {
                        tmpCurrentTreeNode = (DefaultMutableTreeNode) tmpCurrentTreeNode.getParent();
                    }
                    for (int k = 0; k < tmpDifference; k++) {
                        DefaultMutableTreeNode tmpNewNode = new DefaultMutableTreeNode(tmpValueItemArray[i].getNodeNames()[tmpIdentityIndex + 1 + k]);
                        tmpCurrentTreeNode.add(tmpNewNode);
                        tmpCurrentTreeNode = tmpNewNode;
                    }
                    tmpCurrentTreeNode.add(new DefaultMutableTreeNode(tmpValueItemArray[i]));

                    // </editor-fold>
                }

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Node names have different length">
                int tmpDifference;
                int tmpIdentityIndex;
                if (tmpValueItemArray[i].getNodeNames().length > tmpValueItemArray[i - 1].getNodeNames().length) {

                    // <editor-fold defaultstate="collapsed" desc="Current node names is longer">
                    tmpIdentityIndex = ModelUtils.getIdentityIndex(tmpValueItemArray[i].getNodeNames(), tmpValueItemArray[i - 1].getNodeNames());
                    if (tmpIdentityIndex < 0) {
                        return false;
                    }
                    if (tmpIdentityIndex == tmpValueItemArray[i - 1].getNodeNames().length - 1) {

                        // <editor-fold defaultstate="collapsed" desc="... but identical to previous one">
                        tmpDifference = tmpValueItemArray[i].getNodeNames().length - 1 - tmpIdentityIndex;

                        // </editor-fold>
                    } else {

                        // <editor-fold defaultstate="collapsed" desc="... but different to previous one">
                        tmpDifference = tmpValueItemArray[i - 1].getNodeNames().length - 1 - tmpIdentityIndex;
                        for (int k = 0; k < tmpDifference; k++) {
                            tmpCurrentTreeNode = (DefaultMutableTreeNode) tmpCurrentTreeNode.getParent();
                        }
                        tmpDifference = tmpValueItemArray[i].getNodeNames().length - 1 - tmpIdentityIndex;

                        // </editor-fold>
                    }

                    // </editor-fold>
                } else {

                    // <editor-fold defaultstate="collapsed" desc="Current node names is smaller">
                    tmpIdentityIndex = ModelUtils.getIdentityIndex(tmpValueItemArray[i].getNodeNames(), tmpValueItemArray[i - 1].getNodeNames());
                    if (tmpIdentityIndex < 0) {
                        return false;
                    }
                    tmpDifference = tmpValueItemArray[i - 1].getNodeNames().length - 1 - tmpIdentityIndex;
                    for (int k = 0; k < tmpDifference; k++) {
                        tmpCurrentTreeNode = (DefaultMutableTreeNode) tmpCurrentTreeNode.getParent();
                    }
                    tmpDifference = tmpValueItemArray[i].getNodeNames().length - 1 - tmpIdentityIndex;

                    // </editor-fold>
                }
                for (int k = 0; k < tmpDifference; k++) {
                    DefaultMutableTreeNode tmpNewNode = new DefaultMutableTreeNode(tmpValueItemArray[i].getNodeNames()[tmpIdentityIndex + 1 + k]);
                    tmpCurrentTreeNode.add(tmpNewNode);
                    tmpCurrentTreeNode = tmpNewNode;
                }
                tmpCurrentTreeNode.add(new DefaultMutableTreeNode(tmpValueItemArray[i]));

                // </editor-fold>
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set root in tree model">
        tmpTreeModel.setRoot(tmpRoot);

        // </editor-fold>
        return true;
    }

    /**
     * Traverses tree to the first leaf and selects the corresponding tree path
     *
     * @param aTree Tree
     */
    private static void selectFirstLeaf(JTree aTree) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTree == null) {
            return;
        }

        // </editor-fold>
        TreeModel tmpTreeModel = aTree.getModel();
        if (tmpTreeModel == null) {
            return;
        }
        Object tmpRoot = tmpTreeModel.getRoot();
        DefaultMutableTreeNode tmpFirstLeaf = GuiUtils.getFirstLeaf(tmpTreeModel, tmpRoot);
        if (tmpFirstLeaf == null) {
            return;
        }
        TreePath tmpNewTreePath = new TreePath(tmpFirstLeaf.getPath());
        aTree.setSelectionPath(tmpNewTreePath);
        aTree.scrollPathToVisible(tmpNewTreePath);
    }

    /**
     * Selects first value item of specified block in tree. If not found the
     * first leaf/value item in tree is selected.
     *
     * @param aTree Tree
     * @param aBlockName Block name
     */
    private static void selectFirstOfBlock(JTree aTree, String aBlockName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTree == null) {
            return;
        }
        if (aBlockName == null || aBlockName.isEmpty()) {
            return;
        }

        // </editor-fold>
        TreeModel tmpTreeModel = aTree.getModel();
        if (tmpTreeModel == null) {
            return;
        }
        Object tmpRoot = tmpTreeModel.getRoot();
        DefaultMutableTreeNode tmpLeafWithValueItem = GuiUtils.getFirstOfBlock(tmpTreeModel, tmpRoot, aBlockName);
        if (tmpLeafWithValueItem != null) {
            TreePath tmpNewTreePath = new TreePath(tmpLeafWithValueItem.getPath());
            aTree.setSelectionPath(tmpNewTreePath);
            aTree.scrollPathToVisible(tmpNewTreePath);
        } else {
            // Value item was not found: Select first leaf/value item in tree
            GuiUtils.selectFirstLeaf(aTree);
        }
    }

    /**
     * Selects leaf/value item with specified name in tree. If not found the
     * first leaf/value item in tree is selected.
     *
     * @param aTree Tree
     * @param aValueItemName Value item name
     */
    private static void selectDefinedLeaf(JTree aTree, String aValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTree == null) {
            return;
        }
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return;
        }

        // </editor-fold>
        TreeModel tmpTreeModel = aTree.getModel();
        if (tmpTreeModel == null) {
            return;
        }
        Object tmpRoot = tmpTreeModel.getRoot();
        DefaultMutableTreeNode tmpLeafWithValueItem = GuiUtils.getDefinedLeaf(tmpTreeModel, tmpRoot, aValueItemName);
        if (tmpLeafWithValueItem != null) {
            TreePath tmpNewTreePath = new TreePath(tmpLeafWithValueItem.getPath());
            aTree.setSelectionPath(tmpNewTreePath);
            aTree.scrollPathToVisible(tmpNewTreePath);
        } else {
            // Value item was not found: Select first leaf/value item in tree
            GuiUtils.selectFirstLeaf(aTree);
        }
    }

    /**
     * Returns first leaf of tree. NOTE: NO checks of parameters are performed.
     *
     * @param aTreeModel Tree model
     * @param anObject Tree object
     * @return First leaf object or null if none could be found
     */
    private static DefaultMutableTreeNode getFirstLeaf(TreeModel aTreeModel, Object anObject) {
        Object tmpChild = aTreeModel.getChild(anObject, 0);
        if (aTreeModel.isLeaf(tmpChild)) {
            return (DefaultMutableTreeNode) tmpChild;
        } else {
            return GuiUtils.getFirstLeaf(aTreeModel, tmpChild);
        }
    }

    /**
     * Returns first leaf/value item with block name. NOTE: NO checks of
     * parameters are performed.
     *
     * @param aTreeModel Tree model
     * @param anObject Tree object
     * @param aBlockName Block name
     * @return Leaf object with value item with specified block name or null if
     * none could be found
     */
    private static DefaultMutableTreeNode getFirstOfBlock(TreeModel aTreeModel, Object anObject, String aBlockName) {
        int tmpChildCount = aTreeModel.getChildCount(anObject);
        for (int i = 0; i < tmpChildCount; i++) {
            Object tmpChild = aTreeModel.getChild(anObject, i);
            if (aTreeModel.isLeaf(tmpChild)) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) tmpChild;
                if (tmpNode.getUserObject() instanceof ValueItem) {
                    ValueItem tmpValueItem = (ValueItem) tmpNode.getUserObject();
                    if (tmpValueItem.getBlockName().equals(aBlockName)) {
                        return tmpNode;
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            } else {
                DefaultMutableTreeNode tmpNode = GuiUtils.getFirstOfBlock(aTreeModel, tmpChild, aBlockName);
                if (tmpNode != null) {
                    return tmpNode;
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * Returns leaf/value item with specified name. NOTE: NO checks of
     * parameters are performed.
     *
     * @param aTreeModel Tree model
     * @param anObject Tree object
     * @param aValueItemName Value item name
     * @return Leaf object with value item with specified name or null if none
     * could be found
     */
    private static DefaultMutableTreeNode getDefinedLeaf(TreeModel aTreeModel, Object anObject, String aValueItemName) {
        int tmpChildCount = aTreeModel.getChildCount(anObject);
        for (int i = 0; i < tmpChildCount; i++) {
            Object tmpChild = aTreeModel.getChild(anObject, i);
            if (aTreeModel.isLeaf(tmpChild)) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) tmpChild;
                if (tmpNode.getUserObject() instanceof ValueItem) {
                    ValueItem tmpValueItem = (ValueItem) tmpNode.getUserObject();
                    if (tmpValueItem.getName().equals(aValueItemName)) {
                        return tmpNode;
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            } else {
                DefaultMutableTreeNode tmpNode = GuiUtils.getDefinedLeaf(aTreeModel, tmpChild, aValueItemName);
                if (tmpNode != null) {
                    return tmpNode;
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="FileChooser related methods">
    /**
     * Selects a directory with file chooser
     *
     * @param aStartDirectory Start directory
     * @param aTitleLabel Title/Label for file chooser window and approve button
     * @return Selected directory (with full path) or null
     */
    public static String selectDirectory(String aStartDirectory, String aTitleLabel) {
        JFileChooser tmpFileChooser = new JFileChooser(aStartDirectory);
        tmpFileChooser.setPreferredSize(ModelDefinitions.FILE_CHOOSER_DIMENSION);
        tmpFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int tmpStatus = tmpFileChooser.showDialog(null, aTitleLabel);
        if (tmpStatus == JFileChooser.APPROVE_OPTION) {
            Preferences.getInstance().setLastSelectedPath(tmpFileChooser.getSelectedFile().getAbsolutePath());
            return tmpFileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Selects multiple files with file chooser
     *
     * @param aStartDirectory Start directory
     * @param aTitleLabel Title/Label for file chooser window and approve button
     * @param anExtensionFileFilter FileFilter for extensions (may be null)
     * @return String array with full paths of the selected files
     */
    public static String[] selectMultipleFiles(
        String aStartDirectory, 
        String aTitleLabel, 
        ExtensionFileFilter anExtensionFileFilter
    ) {
        JFileChooser tmpFileChooser = new JFileChooser(aStartDirectory);
        tmpFileChooser.setPreferredSize(ModelDefinitions.FILE_CHOOSER_DIMENSION);
        tmpFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        tmpFileChooser.setMultiSelectionEnabled(true);
        if (anExtensionFileFilter != null) {
            tmpFileChooser.setFileFilter(anExtensionFileFilter);
        }
        int tmpStatus = tmpFileChooser.showDialog(null, aTitleLabel);
        if (tmpStatus == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = tmpFileChooser.getSelectedFiles();
            String[] tmpAbsolutePathnames = new String[tmpFiles.length];
            for (int i = 0; i < tmpFiles.length; i++) {
                tmpAbsolutePathnames[i] = tmpFiles[i].getAbsolutePath();
            }
            Preferences.getInstance().setLastSelectedPath((new File(tmpAbsolutePathnames[0])).getParent());
            return tmpAbsolutePathnames;
        } else {
            return null;
        }
    }

    /**
     * Selects a single file with file chooser
     *
     * @param aStartDirectory Start directory
     * @param aTitleLabel Title/Label for file chooser window and approve button
     * @param anExtensionFileFilter FileFilter for extensions (may be null)
     * @return Full path of single selected file
     */
    public static String selectSingleFile(
        String aStartDirectory, 
        String aTitleLabel, 
        ExtensionFileFilter anExtensionFileFilter
    ) {
        JFileChooser tmpFileChooser = new JFileChooser(aStartDirectory);
        tmpFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        tmpFileChooser.setPreferredSize(ModelDefinitions.FILE_CHOOSER_DIMENSION);
        tmpFileChooser.setMultiSelectionEnabled(false);
        if (anExtensionFileFilter != null) {
            tmpFileChooser.setFileFilter(anExtensionFileFilter);
        }
        int tmpStatus = tmpFileChooser.showDialog(null, aTitleLabel);

        // Ensure valid directory (may suppress invalid file specifications 
        // with "/" or "\" characters)
        while (tmpStatus == JFileChooser.APPROVE_OPTION && 
            !(new File ((new File(tmpFileChooser.getSelectedFile().getAbsolutePath())).getParent()).exists())
        ) {
            JOptionPane.showMessageDialog(
                null, 
                GuiMessage.get("Error.InvalidFilename"), 
                GuiMessage.get("Error.ErrorNotificationTitle"),
                JOptionPane.ERROR_MESSAGE
            );
            tmpFileChooser.setSelectedFile(new File(""));
            tmpStatus = tmpFileChooser.showDialog(null, aTitleLabel);
        }

        if (tmpStatus == JFileChooser.APPROVE_OPTION) {
            Preferences.getInstance().setLastSelectedPath((new File(tmpFileChooser.getSelectedFile().getAbsolutePath())).getParent());
            return tmpFileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Manages complete selection for file save procedure: Selection of filename
     * and possible overwrite
     *
     * @param aTitleLabel Title/Label for file chooser window and approve button
     * @param anExtension Extension of file
     * @return Full pathname of file to be saved to or null if file cannot be
     * saved with selected name
     */
    public static String selectSingleFileForSave(String aTitleLabel, String anExtension) {
        ExtensionFileFilter tmpFileFilter = null;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTitleLabel == null || aTitleLabel.isEmpty()) {
            return null;
        }
        try {
            tmpFileFilter = new ExtensionFileFilter(new String[]{anExtension});
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
        // </editor-fold>
        String tmpFilePathName = GuiUtils.selectSingleFile(Preferences.getInstance().getLastSelectedPath(), aTitleLabel, tmpFileFilter);
        if (tmpFilePathName != null && tmpFilePathName.length() > 0) {
            tmpFilePathName = ModelUtils.correctExtension(tmpFilePathName, anExtension);
            // <editor-fold defaultstate="collapsed" desc="Overwrite">
            if ((new File(tmpFilePathName)).exists()) {
                if (GuiUtils.getYesNoDecision(GuiMessage.get("File.OverwriteTitle"), GuiMessage.get("File.Overwrite"))) {
                    // <editor-fold defaultstate="collapsed" desc="Delete file for overwrite">
                    if (!(new File(tmpFilePathName)).delete()) {
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Error.NoFileOverwrite"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Return null (do not overwrite)">
                    return null;
                    // </editor-fold>
                }
            }
            // </editor-fold>
            return tmpFilePathName;
        } else {
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="JList related methods">
    /**
     * Selects job input in list if possible
     *
     * @param aJobInputList List of job inputs
     * @param aJobInputToBeSelected Job input to be selected
     */
    public static void selectJobInputInList(JList aJobInputList, JobInput aJobInputToBeSelected) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputList == null || aJobInputToBeSelected == null) {
            return;
        }
        // </editor-fold>
        int tmpIndexToBeSelected = 0;
        for (int i = 0; i < aJobInputList.getModel().getSize(); i++) {
            JobInput tmpJobInput = (JobInput) aJobInputList.getModel().getElementAt(i);
            if (tmpJobInput.toString().equals(aJobInputToBeSelected.toString())) {
                tmpIndexToBeSelected = i;
                break;
            }
        }
        aJobInputList.setSelectedIndex(tmpIndexToBeSelected);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="JFreeChart related methods">
    /**
     * Creates bar or XY chart from value item data. NOTE: There are no checks
     * performed.
     *
     * @param aValueItem Value item
     * @param aXValueColumn X-value column
     * @param aYValueColumn Y-value column
     * @param aNumberOfDiscardedInitialPoints Number of initial points to be
     * discarded
     * @param aZoomValues {xMin, xMax, yMin, yMax} for zoom
     * @param anIsThickLines True: Thick lines, false: Thin lines
     * @param anIsShapePaint True: Shapes are painted, false: Shapes are not
     * painted
     * @param anIsFillColorWhite If shapes are painted: True: Fill color of
     * shapes is white, false: Fill color of shapes is default color
     * @param anIsOutlinePaintWhite If shapes are painted: True: Outline of
     * shapes is white, false: Outline of shapes is default color
     * @param aNumberToAverage Number of y-values to average
     * @return Chart or null if chart could not be created
     */
    public static JFreeChart createChart(
        ValueItem aValueItem, 
        int aXValueColumn, 
        int aYValueColumn, 
        int aNumberOfDiscardedInitialPoints, 
        double[] aZoomValues, 
        boolean anIsThickLines,
        boolean anIsShapePaint, 
        boolean anIsFillColorWhite, 
        boolean anIsOutlinePaintWhite,
        int aNumberToAverage
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null || aValueItem.getMatrixRowCount() <= aNumberOfDiscardedInitialPoints) {
            return null;
        }
        if (aZoomValues == null) {
            return null;
        }
        // </editor-fold>
        boolean tmpIsZoom = true;
        for (double tmpSingleCoordinate : aZoomValues) {
            if (tmpSingleCoordinate == -Double.MAX_VALUE || tmpSingleCoordinate == Double.MAX_VALUE) {
                tmpIsZoom = false;
                break;
            }
        }
        try {
            // Safeguard
            if (aNumberOfDiscardedInitialPoints < 0) {
                aNumberOfDiscardedInitialPoints = 0;
            }
            switch (aValueItem.getTypeFormat(aXValueColumn).getDataType()) {
                case NUMERIC:
                    // <editor-fold defaultstate="collapsed" desc="XY chart">
                    // <editor-fold defaultstate="collapsed" desc="- Create data series">
                    XySeriesManipulator tmpXySeries = new XySeriesManipulator();

                    double tmpXmin;
                    double tmpXmax;
                    double tmpYmin;
                    double tmpYmax;
                    if (!tmpIsZoom) {
                        tmpXmin = Double.MAX_VALUE;
                        tmpXmax = -Double.MAX_VALUE;
                        tmpYmin = Double.MAX_VALUE;
                        tmpYmax = -Double.MAX_VALUE;
                    } else {
                        tmpXmin = aZoomValues[0];
                        tmpXmax = aZoomValues[1];
                        tmpYmin = aZoomValues[2];
                        tmpYmax = aZoomValues[3];
                    }
                    for (int i = aNumberOfDiscardedInitialPoints; i < aValueItem.getMatrixRowCount(); i++) {
                        // Safeguard for NaN values in value item
                        double tmpX = 0.0;
                        double tmpY = 0.0;
                        try {
                            tmpX = aValueItem.getValueAsDouble(i, aXValueColumn);
                            tmpY = aValueItem.getValueAsDouble(i, aYValueColumn);
                            if (Double.isNaN(tmpX) || Double.isNaN(tmpY)) {
                                continue;
                            }
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);
                            continue;
                        }
                        if (!tmpIsZoom) {
                            tmpXmin = Math.min(tmpXmin, tmpX);
                            tmpXmax = Math.max(tmpXmax, tmpX);
                            tmpYmin = Math.min(tmpYmin, tmpY);
                            tmpYmax = Math.max(tmpYmax, tmpY);
                            tmpXySeries.add(tmpX, tmpY);
                        } else if (tmpX >= tmpXmin && tmpX <= tmpXmax && tmpY >= tmpYmin && tmpY <= tmpYmax) {
                            tmpXySeries.add(tmpX, tmpY);
                        }
                    }
                    double tmpXoffset;
                    if (Math.abs(tmpXmax - tmpXmin) < tmpXmax * GuiUtils.TINY_THRESHOLD) {
                        tmpXoffset = (tmpXmax + tmpXmin) * 0.5 * 0.05;
                    } else {
                        tmpXoffset = (tmpXmax - tmpXmin) * 0.05;
                    }
                    double tmpX1 = tmpXmin - tmpXoffset;
                    double tmpX2 = tmpXmax + tmpXoffset;
                    double tmpYoffset;
                    if (Math.abs(tmpYmax - tmpYmin) < tmpYmax * GuiUtils.TINY_THRESHOLD) {
                        tmpYoffset = (tmpYmax + tmpYmin) * 0.5 * 0.05;
                    } else {
                        tmpYoffset = (tmpYmax - tmpYmin) * 0.05;
                    }
                    double tmpY1 = tmpYmin - tmpYoffset;
                    double tmpY2 = tmpYmax + tmpYoffset;

                    XYDataset tmpXyDataset = new XYSeriesCollection(tmpXySeries.getXySeries(aNumberToAverage));
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Create chart">
                    JFreeChart tmpChart = 
                        ChartFactory.createXYLineChart(
                            aValueItem.getDisplayName(), // Title
                            aValueItem.getMatrixColumnNames()[aXValueColumn], // xAxisLabel
                            aValueItem.getMatrixColumnNames()[aYValueColumn], // yAxisLabel
                            tmpXyDataset, // dataset
                            PlotOrientation.VERTICAL, // orientation
                            false, // legend flag
                            false, // tooltips flag
                            false  // URLs flag
                        ); 
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set axis ranges">
                    XYPlot tmpXyPlot = (XYPlot) tmpChart.getPlot();
                    ValueAxis tmpXaxis = tmpXyPlot.getDomainAxis();
                    tmpXaxis.setRange(tmpX1, tmpX2);
                    ValueAxis tmpYaxis = tmpXyPlot.getRangeAxis();
                    tmpYaxis.setRange(tmpY1, tmpY2);
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set outline, shapes and thickness">
                    XYLineAndShapeRenderer tmpRenderer = (XYLineAndShapeRenderer) tmpXyPlot.getRenderer();
                    tmpRenderer.setPaint(Color.BLACK);
                    if (anIsThickLines) {
                        tmpRenderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                    }
                    if (anIsShapePaint) {
                        tmpRenderer.setShape(new Ellipse2D.Float(-5.0f, -5.0f, 10.0f, 10.0f));
                        tmpRenderer.setShapesVisible(true);
                        tmpRenderer.setShapesFilled(true);
                        if (anIsFillColorWhite) {
                            tmpRenderer.setUseFillPaint(true);
                            tmpRenderer.setFillPaint(Color.white);
                        } else if (anIsOutlinePaintWhite) {
                            tmpRenderer.setOutlinePaint(Color.white);
                            tmpRenderer.setUseOutlinePaint(true);
                        }
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Return XY chart">
                    return tmpChart;
                // </editor-fold>
                // </editor-fold>
                case TEXT:
                    // <editor-fold defaultstate="collapsed" desc="Bar chart">
                    DefaultCategoryDataset tmpCategoryDataset = new DefaultCategoryDataset();
                    for (int i = aNumberOfDiscardedInitialPoints; i < aValueItem.getMatrixRowCount(); i++) {
                        tmpCategoryDataset.addValue(aValueItem.getValueAsDouble(i, aYValueColumn), "BarChartData", aValueItem.getValue(i, aXValueColumn));
                    }
                    return ChartFactory.createBarChart(
                        aValueItem.getDisplayName(), // Title
                        aValueItem.getMatrixColumnNames()[aXValueColumn], // xAxisLabel
                        aValueItem.getMatrixColumnNames()[aYValueColumn], // yAxisLabel
                        tmpCategoryDataset, // dataset
                        PlotOrientation.VERTICAL, // orientation
                        false, // legend flag
                        false, // tooltips flag
                        false  // URLs flag
                    );
                // </editor-fold>
                default:
                    return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns double array with { xMin, xMax, yMin, yMax}
     *
     * @param aValueItem Value item
     * @param aXValueColumn X-value column
     * @param aYValueColumn Y-value column
     * @param aNumberOfDiscardedInitialPoints Number of initial points to be
     * discarded
     * @return Double array with { xMin, xMax, yMin, yMax} or null if array
     * could not be created
     */
    public static double[] getDataBoundariesOfValueItemMatrix(ValueItem aValueItem, int aXValueColumn, int aYValueColumn, int aNumberOfDiscardedInitialPoints) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null || aValueItem.getMatrixRowCount() <= aNumberOfDiscardedInitialPoints) {
            return null;
        }

        // </editor-fold>
        try {
            // Safeguard
            if (aNumberOfDiscardedInitialPoints < 0) {
                aNumberOfDiscardedInitialPoints = 0;
            }
            switch (aValueItem.getTypeFormat(aXValueColumn).getDataType()) {
                case NUMERIC:
                    // <editor-fold defaultstate="collapsed" desc="XY chart">
                    double tmpXmin = Double.MAX_VALUE;
                    double tmpXmax = -Double.MAX_VALUE;
                    double tmpYmin = Double.MAX_VALUE;
                    double tmpYmax = -Double.MAX_VALUE;
                    for (int i = aNumberOfDiscardedInitialPoints; i < aValueItem.getMatrixRowCount(); i++) {
                        // Safeguard for NaN values in value item
                        double tmpX = 0.0;
                        double tmpY = 0.0;
                        try {
                            tmpX = aValueItem.getValueAsDouble(i, aXValueColumn);
                            tmpY = aValueItem.getValueAsDouble(i, aYValueColumn);
                            if (Double.isNaN(tmpX) || Double.isNaN(tmpY)) {
                                continue;
                            }
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);
                            continue;
                        }
                        tmpXmin = Math.min(tmpXmin, tmpX);
                        tmpXmax = Math.max(tmpXmax, tmpX);
                        tmpYmin = Math.min(tmpYmin, tmpY);
                        tmpYmax = Math.max(tmpYmax, tmpY);
                    }
                    return new double[]{tmpXmin, tmpXmax, tmpYmin, tmpYmax};

                // </editor-fold>
                case TEXT:
                    return null;
                default:
                    return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Creates statistics string from value item data (see code). NOTE: There
     * are no checks performed.
     *
     * @param aValueItem Value item
     * @param aXValueColumn X-value column
     * @param aYValueColumn Y-value column
     * @param aNumberOfDiscardedInitialPoints Number of initial points to be
     * discarded
     * @param aZoomValues {xMin, xMax, yMin, yMax} for zoom
     * @return Statistics string or null if chart could not be created
     */
    public static String getStatisticsString(ValueItem aValueItem, int aXValueColumn, int aYValueColumn, int aNumberOfDiscardedInitialPoints, double[] aZoomValues) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null || aValueItem.getMatrixRowCount() <= aNumberOfDiscardedInitialPoints) {
            return null;
        }
        if (aZoomValues == null) {
            return null;
        }

        // </editor-fold>
        try {
            // Safeguard
            if (aNumberOfDiscardedInitialPoints < 0) {
                aNumberOfDiscardedInitialPoints = 0;
            }
            switch (aValueItem.getTypeFormat(aXValueColumn).getDataType()) {
                case NUMERIC:
                    // <editor-fold defaultstate="collapsed" desc="XY chart">
                    // <editor-fold defaultstate="collapsed" desc="- Calculate statistics">
                    double tmpXZoomMin = aZoomValues[0];
                    double tmpXZoomMax = aZoomValues[1];
                    double tmpYZoomMin = aZoomValues[2];
                    double tmpYZoomMax = aZoomValues[3];

                    double tmpSumX = 0.0;
                    double tmpSumY = 0.0;
                    double tmpSumXSquare = 0.0;
                    double tmpSumYSquare = 0.0;
                    double tmpSumXY = 0.0;
                    double tmpYmin = Double.MAX_VALUE;
                    double tmpYmax = -Double.MAX_VALUE;

                    double tmpX;
                    double tmpY;
                    int tmpN = 0;
                    for (int i = aNumberOfDiscardedInitialPoints; i < aValueItem.getMatrixRowCount(); i++) {
                        // Safeguard for NaN values in value item
                        try {
                            tmpX = aValueItem.getValueAsDouble(i, aXValueColumn);
                            tmpY = aValueItem.getValueAsDouble(i, aYValueColumn);
                            if (Double.isNaN(tmpX) || Double.isNaN(tmpY)) {
                                continue;
                            }
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);
                            continue;
                        }
                        // Zoom
                        if (tmpX >= tmpXZoomMin && tmpX <= tmpXZoomMax && tmpY >= tmpYZoomMin && tmpY <= tmpYZoomMax) {
                            tmpN++;
                            tmpSumX += tmpX;
                            tmpSumY += tmpY;
                            tmpSumXSquare += tmpX * tmpX;
                            tmpSumYSquare += tmpY * tmpY;
                            tmpSumXY += tmpX * tmpY;
                            tmpYmin = Math.min(tmpY, tmpYmin);
                            tmpYmax = Math.max(tmpY, tmpYmax);
                        }
                    }
                    if (tmpN < 2) {
                        return GuiMessage.get("StatisticsInformation.NoInformation");
                    }
                    double tmpSampleMean = tmpSumY / tmpN;
                    double tmpSxx = tmpSumXSquare - tmpSumX * tmpSumX / (double) tmpN;
                    double tmpSxy = tmpSumXY - tmpSumX * tmpSumY / (double) tmpN;
                    double tmpSampleStandardDeviation = Math.sqrt((tmpSumYSquare - tmpSumY * tmpSumY / (double) tmpN) / (double) (tmpN - 1));
                    double tmpSampleMeanError = tmpSampleStandardDeviation / Math.sqrt(tmpN);
                    double tmpLinearRegressionSlope = tmpSxy / tmpSxx;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Return statistics string">
                    return String.format(GuiMessage.get("StatisticsInformation.Format"),
                            GuiUtils.stringUtilityMethods.formatDoubleValue(tmpYmin, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                            GuiUtils.stringUtilityMethods.formatDoubleValue(tmpSampleMean, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                            GuiUtils.stringUtilityMethods.formatDoubleValue(tmpSampleMeanError, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                            GuiUtils.stringUtilityMethods.formatDoubleValue(tmpYmax, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                            GuiUtils.stringUtilityMethods.formatDoubleValue(tmpSampleStandardDeviation, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                            GuiUtils.stringUtilityMethods.formatDoubleValue(tmpLinearRegressionSlope, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_SLOPE)
                    );
                // </editor-fold>
                // </editor-fold>
                case TEXT:
                    // <editor-fold defaultstate="collapsed" desc="Bar chart">
                    return GuiMessage.get("StatisticsInformation.NoInformation");
                // </editor-fold>
                default:
                    return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Inverts image
     * 
     * @param aSourceImage Source image to be inverted (is NOT changed)
     * @return Inverted image
     */
    public static BufferedImage invertImage(final BufferedImage aSourceImage) {
        final int tmpWidth = aSourceImage.getWidth();
        final int tmpHeight = aSourceImage.getHeight();
        final BufferedImage tmpDestinationImage = new BufferedImage(tmpWidth, tmpHeight, BufferedImage.TYPE_INT_RGB);
        return GuiUtils.bufferedImageInversionFilter.filter(aSourceImage, tmpDestinationImage);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="JOptionPane related methods">

    /**
     * Returns yes/no decision
     *
     * @param aTitle Title of message
     * @param aMessage Message
     * @return True: Yes, false: No
     */
    public static boolean getYesNoDecision(String aTitle, String aMessage) {
        Object[] tmpOptions = {GuiMessage.get("Decision.Yes"), GuiMessage.get("Decision.No")};
        boolean tmpDecision = JOptionPane.showOptionDialog(null, aMessage, aTitle, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, tmpOptions, tmpOptions[1]) == JOptionPane.YES_OPTION;
        return tmpDecision;
    }

    /**
     * Under-construction information display
     */
    public static void showUnderConstructionInformation() {
        JOptionPane.showMessageDialog(null, 
            GuiMessage.get("Information.UnderConstruction"), 
            GuiMessage.get("Information.UnderConstructionFrameTitle"),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CustomPanelValueItemEdit related methods">
    /**
     * Configures CustomPanelValueItemEdit
     *
     * @param aCustomPanelValueItemEdit CustomPanelValueItemEdit to configure
     * @param anIsValueItemStatusSelectionShown True: Value items status
     * selections are shown, false: Otherwise
     * @param anIsHintTabRemoved True: Hint tab is removed, false: Hint tab is
     * shown
     * @param anIsErrorTabRemoved True: Error tab is removed, false: Error tab
     * is shown
     * @param anIsSchemaManagement True: Schema management is available, false:
     * Schema management not shown/available
     */
    public static void configureCustomPanelValueItemEdit(CustomPanelValueItemEdit aCustomPanelValueItemEdit, boolean anIsValueItemStatusSelectionShown, boolean anIsHintTabRemoved,
            boolean anIsErrorTabRemoved, boolean anIsSchemaManagement) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelValueItemEdit == null) {
            return;
        }

        // </editor-fold>
        // Hide value item status panel if specified
        aCustomPanelValueItemEdit.getSelectValueItemStatusPanel().setVisible(anIsValueItemStatusSelectionShown);
        // Remove tabs if necessary
        // NOTE: FIRST remove tab with higher index ...
        if (anIsErrorTabRemoved) {
            aCustomPanelValueItemEdit.getSelectedFeatureTabbedPanel().remove(3);
        }
        // ... then tab with lower index
        if (anIsHintTabRemoved) {
            aCustomPanelValueItemEdit.getSelectedFeatureTabbedPanel().remove(2);
        }
        // Hide table-data schema panel if specified
        aCustomPanelValueItemEdit.getTableDataSchemaPanel().setVisible(anIsSchemaManagement);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CustomPanelImage related methods">
    /**
     * Sets compartments image
     *
     * @param anImagePanel CustomPanelImage instance
     */
    public static void setCompartmentsImage(CustomPanelImage anImagePanel) {
        BufferedImage tmpImage = GuiUtils.getImageOfResource(GuiDefinitions.COMPARTMENTS_IMAGE_FILENAME_PREFIX, GuiDefinitions.RESOURCE_IMAGE_ENDING);
        if (tmpImage != null) {
            anImagePanel.setBasicImage(tmpImage);
        }
    }

    /**
     * Sets simulation box image
     *
     * @param anImagePanel CustomPanelImage instance
     */
    public static void setSimulationBoxImage(CustomPanelImage anImagePanel) {
        BufferedImage tmpImage = GuiUtils.getImageOfResource(GuiDefinitions.SIMULATION_BOX_IMAGE_FILENAME_PREFIX, GuiDefinitions.RESOURCE_IMAGE_ENDING);
        if (tmpImage != null) {
            anImagePanel.setBasicImage(tmpImage);
        }
    }

    /**
     * Sets simulation movie image
     *
     * @param anImagePanel CustomPanelImage instance
     */
    public static void setSimulationMovieImage(CustomPanelImage anImagePanel) {
        BufferedImage tmpImage = GuiUtils.getImageOfResource(GuiDefinitions.SIMULATION_MOVIE_IMAGE_FILENAME_PREFIX, GuiDefinitions.RESOURCE_IMAGE_ENDING);
        if (tmpImage != null) {
            anImagePanel.setBasicImage(tmpImage);
        }
    }

    /**
     * Sets distribution movie image
     *
     * @param anImagePanel CustomPanelImage instance
     */
    public static void setDistributionMovieImage(CustomPanelImage anImagePanel) {
        BufferedImage tmpImage = GuiUtils.getImageOfResource(GuiDefinitions.PARTICLE_DISTRIBUTION_MOVIE_IMAGE_FILENAME_PREFIX, GuiDefinitions.RESOURCE_IMAGE_ENDING);
        if (tmpImage != null) {
            anImagePanel.setBasicImage(tmpImage);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Resource file related methods">
    /**
     * Checks if specified image of resource exists
     *
     * @param anImageResourceFilename Image filename to be in resource
     * @return true: Specified image of resource exists, false: Otherwise
     */
    public static boolean hasImageOfResource(String anImageResourceFilename) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImageResourceFilename == null || anImageResourceFilename.isEmpty()) {
            return false;
        }

        // </editor-fold>
        String imageResourcePathname = GuiDefinitions.RESOURCE_LOCATION + anImageResourceFilename;
        InputStream tmpInputStream = null;
        try {
            tmpInputStream = GuiUtils.fileUtilityMethods.getResourceInputStream(GuiUtils.class, imageResourcePathname);
            return tmpInputStream != null;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return false;
                }
            }
        }
    }

    /**
     * Returns image of resource with current of default version
     *
     * @param anImageResourceFilenamePrefix Prefix of versioned image filename
     * that is in resource
     * @param anImageResourceEnding Ending of versioned image filename that is
     * in resource
     * @return Specified image of resource with current of default version or
     * null if specified image does not exist
     */
    public static BufferedImage getImageOfResource(String anImageResourceFilenamePrefix, String anImageResourceEnding) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImageResourceFilenamePrefix == null || anImageResourceFilenamePrefix.isEmpty()) {
            return null;
        }
        if (anImageResourceEnding == null || anImageResourceEnding.isEmpty()) {
            return null;
        }
        // </editor-fold>
        BufferedImage tmpImage = GuiUtils.getVersionedImageOfResource(anImageResourceFilenamePrefix, anImageResourceEnding, ModelDefinitions.DEFAULT_IMAGE_VERSION);
        return tmpImage;
    }

    /**
     * Checks if specified versioned image of resource exists
     *
     * @param anImageResourceFilenamePrefix Prefix of versioned image filename
     * to be in resource
     * @param anImageResourceEnding Ending of versioned image filename to be in
     * resource
     * @param aVersion Version
     * @return true: Specified versioned image of resource exists, false:
     * Otherwise
     */
    public static boolean hasVersionedImageOfResource(String anImageResourceFilenamePrefix, String anImageResourceEnding, int aVersion) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImageResourceFilenamePrefix == null || anImageResourceFilenamePrefix.isEmpty()) {
            return false;
        }
        if (anImageResourceEnding == null || anImageResourceEnding.isEmpty()) {
            return false;
        }

        // </editor-fold>
        String imageResourcePathname = GuiDefinitions.RESOURCE_LOCATION + anImageResourceFilenamePrefix + String.valueOf(aVersion) + anImageResourceEnding;
        InputStream tmpInputStream = null;
        try {
            tmpInputStream = GuiUtils.fileUtilityMethods.getResourceInputStream(GuiUtils.class, imageResourcePathname);
            return tmpInputStream != null;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpInputStream != null) {
                try {
                    tmpInputStream.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return false;
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Copy text to clipboard">
    /**
     * Copies text to clipboard
     *
     * @param aText Text
     */
    public static void copyTextToClipboard(String aText) {
        StringSelection tmpStringSelection = new StringSelection(aText);
        Clipboard tmpClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        tmpClipboard.setContents(tmpStringSelection, null);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Protein edit and show related methods">
    /**
     * Returns ActiveProteinChainsValueItem
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return ActiveProteinChainsValueItem or null if none could be created
     */
    public static ValueItem getActiveProteinChainsValueItem(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpActiveProteinChainsValueItem = new ValueItem();
            tmpActiveProteinChainsValueItem.setName("ACTIVE_PROTEIN_CHAINS");
            tmpActiveProteinChainsValueItem.setDisplayName(GuiMessage.get("ActiveProteinChains.DisplayName"));
            tmpActiveProteinChainsValueItem.setDescription(GuiMessage.get("ActiveProteinChains.Description"));

            ValueItemDataTypeFormat tmpActiveProteinChainsSelectionTypeFormat = new ValueItemDataTypeFormat(GuiMessage.get("ActiveProteinChains.Used"), new String[]{
                GuiMessage.get("ActiveProteinChains.Used"), GuiMessage.get("ActiveProteinChains.Excluded")});
            tmpActiveProteinChainsValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpActiveProteinChainsValueItem.setMatrixColumnNames(new String[]{GuiMessage.get("ActiveProteinChains.Chain"),
                GuiMessage.get("ActiveProteinChains.Status")});
            tmpActiveProteinChainsValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_400, // Chain
                ModelDefinitions.CELL_WIDTH_TEXT_100}); // Status

            String[] tmpChainNameArray = aPdbToDPD.getCompoundNames();
            HashMap<String, String> tmpChainNameToIdMap = aPdbToDPD.getNameChainIDMap();
            ArrayList<String> tmpActiveChainIdList = aPdbToDPD.getMasterdata().getActiveChains();

            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpChainNameArray.length][];
            int tmpIndex = 0;
            for (String tmpChainName : tmpChainNameArray) {
                tmpMatrix[tmpIndex] = new ValueItemMatrixElement[2];
                // Set chain name. Parameter false: Not editable
                tmpMatrix[tmpIndex][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpChainName, ValueItemEnumDataType.TEXT, false));
                // Set status
                String tmpStatus = null;
                String tmpChainId = tmpChainNameToIdMap.get(tmpChainName);
                if (tmpActiveChainIdList.contains(tmpChainId)) {
                    tmpStatus = GuiMessage.get("ActiveProteinChains.Used");
                } else {
                    tmpStatus = GuiMessage.get("ActiveProteinChains.Excluded");
                }
                tmpMatrix[tmpIndex][1] = new ValueItemMatrixElement(tmpStatus, tmpActiveProteinChainsSelectionTypeFormat);
                tmpIndex++;
            }
            tmpActiveProteinChainsValueItem.setMatrix(tmpMatrix);
            return tmpActiveProteinChainsValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns ChainSegmentAssignmentValueItem
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return ChainSegmentAssignmentValueItem or null if none could be created
     */
    public static ValueItem getChainSegmentAssignmentValueItem(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpChainSegmentAssignmentValueItem = new ValueItem();
            tmpChainSegmentAssignmentValueItem.setName("CHAIN_SEGMENT_ASSIGNMENT");
            tmpChainSegmentAssignmentValueItem.setDisplayName(GuiMessage.get("ChainSegmentsAssignment.DisplayName"));
            tmpChainSegmentAssignmentValueItem.setDescription(GuiMessage.get("ChainSegmentsAssignment.Description"));
            int[] tmpBackboneParticleSegmentArray = aPdbToDPD.getBackboneParticleSegmentArray();
            int[] tmpDefinedBackboneParticleSegments = aPdbToDPD.getBackboneParticleSegmentsChainsApplied();
            boolean tmpAreBackboneParticleSegmentsDefined = true;
            for (int i = 0; i < tmpBackboneParticleSegmentArray.length; i++) {
                if (tmpBackboneParticleSegmentArray[i] != tmpDefinedBackboneParticleSegments[i]) {
                    tmpAreBackboneParticleSegmentsDefined = false;
                    break;
                }
            }
            tmpChainSegmentAssignmentValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(new String[]{
                GuiMessage.get("ChainSegmentsAssignment.False"),
                GuiMessage.get("ChainSegmentsAssignment.True")
            }));
            if (tmpAreBackboneParticleSegmentsDefined) {
                tmpChainSegmentAssignmentValueItem.setValue(GuiMessage.get("ChainSegmentsAssignment.True"));
            } else {
                tmpChainSegmentAssignmentValueItem.setValue(GuiMessage.get("ChainSegmentsAssignment.False"));
            }
            return tmpChainSegmentAssignmentValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns PhValueItem
     *
     * @param aLastPhValue Last pH value (is NOT changed)
     * @return PhValueItem or null if none could be created
     */
    public static ValueItem getPhValueItem(String aLastPhValue) {
        try {
            ValueItem tmpPhValueItem = new ValueItem();
            tmpPhValueItem.setName("PH_VALUE");
            tmpPhValueItem.setDisplayName(GuiMessage.get("pH.DisplayName"));
            tmpPhValueItem.setDescription(GuiMessage.get("pH.ProteinDescription"));
            // Set editable and numeric-null allowed
            tmpPhValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(1, 0.0, 14.0, true, true));
            if (aLastPhValue == null || aLastPhValue.isEmpty()) {
                // Set to numeric-null
                tmpPhValueItem.setValue(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"));
            } else {
                // Set to last pH value
                tmpPhValueItem.setValue(aLastPhValue);
            }
            return tmpPhValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns ProteinBackboneProbesValueItem
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return ProteinBackboneProbesValueItem or null if none could be created
     */
    public static ValueItem getProteinBackboneProbesValueItem(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpProteinBackboneProbesValueItem = null;
            boolean tmpIsComplete = true;
            HashMap<String, String[]> tmpParticleToProbesMap = null;
            String[] tmpCAlphaParticles = aPdbToDPD.getCAlphaParticles();
            if (tmpCAlphaParticles != null && tmpCAlphaParticles.length > 0) {
                tmpParticleToProbesMap = new HashMap<String, String[]>(tmpCAlphaParticles.length);
                for (String tmpSingleCAlphaParticle : tmpCAlphaParticles) {
                    if (tmpSingleCAlphaParticle != null && tmpSingleCAlphaParticle.length() > 0) {
                        if (!tmpParticleToProbesMap.containsKey(tmpSingleCAlphaParticle)) {
                            String[] tmpSpecificProbes = StandardParticleInteractionData.getInstance().getSpecificProteinBackboneProbeParticles(tmpSingleCAlphaParticle);
                            if (tmpSpecificProbes != null && tmpSpecificProbes.length > 0) {
                                tmpParticleToProbesMap.put(tmpSingleCAlphaParticle,
                                        (new StringUtilityMethods()).getConcatenatedStringArrays(new String[]{GuiMessage.get("ProteinBackboneProbeParticles.NoProbe")}, tmpSpecificProbes));
                            } else {
                                tmpIsComplete = false;
                                break;
                            }
                        }
                    } else {
                        tmpIsComplete = false;
                        break;
                    }
                }
            } else {
                tmpIsComplete = false;
            }

            if (tmpIsComplete) {
                tmpProteinBackboneProbesValueItem = new ValueItem();
                tmpProteinBackboneProbesValueItem.setName("PROTEIN_BACKBONE_PROBE_PARTICLES");
                tmpProteinBackboneProbesValueItem.setDisplayName(GuiMessage.get("ProteinBackboneProbeParticles.DisplayName"));
                tmpProteinBackboneProbesValueItem.setDescription(GuiMessage.get("ProteinBackboneProbeParticles.Description"));
                tmpProteinBackboneProbesValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                tmpProteinBackboneProbesValueItem.setMatrixColumnNames(new String[]{
                    GuiMessage.get("ProteinBackboneProbeParticles.Index"),
                    GuiMessage.get("ProteinBackboneProbeParticles.AminoAcid"),
                    GuiMessage.get("ProteinBackboneProbeParticles.BackboneParticle"),
                    GuiMessage.get("ProteinBackboneProbeParticles.ProbeParticle")});
                tmpProteinBackboneProbesValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // Index
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // AminoAcid
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // BackboneParticle
                    ModelDefinitions.CELL_WIDTH_TEXT_150}); // ProbeParticle

                String[] tmpProteinBackboneParticleIndexArray = aPdbToDPD.getCAlphaKeys();
                HashMap<String, String> tmpProteinBackboneProbeParticlesMap = aPdbToDPD.getProbes();
                ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpProteinBackboneParticleIndexArray.length][];
                ValueItemDataTypeFormat tmpIndexTypeFormat = new ValueItemDataTypeFormat(false);
                ValueItemDataTypeFormat tmpAminoAcidTypeFormat = new ValueItemDataTypeFormat(false);
                for (int i = 0; i < tmpProteinBackboneParticleIndexArray.length; i++) {
                    tmpMatrix[i] = new ValueItemMatrixElement[4];
                    // NOTE: Index of AminoAcid starts with "1".
                    tmpMatrix[i][0] = new ValueItemMatrixElement(String.valueOf(i + 1), tmpIndexTypeFormat);
                    // NOTE: Index of AminoAcid starts with "1".
                    AminoAcid tmpAminoAcid = aPdbToDPD.getAminoAcid(i + 1);
                    tmpMatrix[i][1] = new ValueItemMatrixElement(tmpAminoAcid.getName(), tmpAminoAcidTypeFormat);
                    // Set protein backbone particle index. Parameter false: Not editable
                    tmpMatrix[i][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpProteinBackboneParticleIndexArray[i], ValueItemEnumDataType.TEXT, false));
                    String tmpProbeValue = GuiMessage.get("ProteinBackboneProbeParticles.NoProbe");
                    if (tmpProteinBackboneProbeParticlesMap != null
                            && tmpProteinBackboneProbeParticlesMap.containsKey(tmpProteinBackboneParticleIndexArray[i])) {
                        tmpProbeValue = tmpProteinBackboneProbeParticlesMap.get(tmpProteinBackboneParticleIndexArray[i]);
                    }
                    ValueItemDataTypeFormat tmpProteinBackboneProbeParticlesSelectionTypeFormat = new ValueItemDataTypeFormat(GuiMessage.get("ProteinBackboneProbeParticles.NoProbe"),
                            tmpParticleToProbesMap.get(tmpCAlphaParticles[i]));
                    tmpMatrix[i][3] = new ValueItemMatrixElement(tmpProbeValue, tmpProteinBackboneProbeParticlesSelectionTypeFormat);
                }
                tmpProteinBackboneProbesValueItem.setMatrix(tmpMatrix);
            }
            return tmpProteinBackboneProbesValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns ProteinRotationValueItem
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return ProteinRotationValueItem or null if none could be created
     */
    public static ValueItem getProteinRotationValueItem(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpProteinRotationValueItem = new ValueItem();
            tmpProteinRotationValueItem.setName("PROTEIN_ROTATION");
            tmpProteinRotationValueItem.setDisplayName(GuiMessage.get("ProteinRotation.DisplayName"));
            tmpProteinRotationValueItem.setDescription(GuiMessage.get("ProteinRotation.Description"));
            tmpProteinRotationValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpProteinRotationValueItem.setMatrixColumnNames(new String[]{GuiMessage.get("ProteinRotation.AroundXaxis"),
                GuiMessage.get("ProteinRotation.AroundYaxis"),
                GuiMessage.get("ProteinRotation.AroundZaxis")});
            tmpProteinRotationValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_120, // x-axis-rotation-angle
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // y-axis-rotation-angle
                ModelDefinitions.CELL_WIDTH_NUMERIC_120});
            tmpProteinRotationValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.ROTATION_AROUND_X_AXIS_ANGLE_DEFAULT), 0, ModelDefinitions.ROTATION_AROUND_X_AXIS_ANGLE_MINIMUM,
                ModelDefinitions.ROTATION_AROUND_X_AXIS_ANGLE_MAXIMUM), // x-axis-rotation-angle
                new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.ROTATION_AROUND_Y_AXIS_ANGLE_DEFAULT), 0, ModelDefinitions.ROTATION_AROUND_Y_AXIS_ANGLE_MINIMUM,
                ModelDefinitions.ROTATION_AROUND_Y_AXIS_ANGLE_MAXIMUM), // y-axis-rotation-angle
                new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.ROTATION_AROUND_Z_AXIS_ANGLE_DEFAULT), 0, ModelDefinitions.ROTATION_AROUND_Z_AXIS_ANGLE_MINIMUM,
                ModelDefinitions.ROTATION_AROUND_Z_AXIS_ANGLE_MAXIMUM) // z-axis-rotation-angle
            });
            Vector3d tmpRotationInRadians = aPdbToDPD.getRotation();
            Vector3d tmpRotationInAngles = new Vector3d(
                    ModelUtils.roundDoubleValue(tmpRotationInRadians.x * 180.0 / Math.PI, 0),
                    ModelUtils.roundDoubleValue(tmpRotationInRadians.y * 180.0 / Math.PI, 0),
                    ModelUtils.roundDoubleValue(tmpRotationInRadians.z * 180.0 / Math.PI, 0));
            tmpProteinRotationValueItem.setValue(String.valueOf(tmpRotationInAngles.x), 0, 0);
            tmpProteinRotationValueItem.setValue(String.valueOf(tmpRotationInAngles.y), 0, 1);
            tmpProteinRotationValueItem.setValue(String.valueOf(tmpRotationInAngles.z), 0, 2);
            return tmpProteinRotationValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns ProteinBackboneStatusValueItem
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return ProteinBackboneStatusValueItem or null if none could be created
     */
    public static ValueItem getProteinBackboneStatusValueItem(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpProteinBackboneStatusValueItem = null;
            boolean[] tmpBackboneParticleStatusArray = aPdbToDPD.getBackboneParticleStatusArray();
            int[] tmpBackboneParticleSegmentArray = aPdbToDPD.getBackboneParticleSegmentArray();
            if (tmpBackboneParticleStatusArray != null && tmpBackboneParticleSegmentArray != null && tmpBackboneParticleSegmentArray.length == tmpBackboneParticleStatusArray.length) {
                tmpProteinBackboneStatusValueItem = new ValueItem();
                tmpProteinBackboneStatusValueItem.setName("PROTEIN_BACKBONE_STATUS");
                tmpProteinBackboneStatusValueItem.setDisplayName(GuiMessage.get("ProteinBackboneStatus.DisplayName"));
                tmpProteinBackboneStatusValueItem.setDescription(GuiMessage.get("ProteinBackboneStatus.Description"));
                tmpProteinBackboneStatusValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                tmpProteinBackboneStatusValueItem.setMatrixColumnNames(new String[]{
                    GuiMessage.get("ProteinBackboneStatus.Index"),
                    GuiMessage.get("ProteinBackboneStatus.AminoAcid"),
                    GuiMessage.get("ProteinBackboneStatus.BackboneParticle"),
                    GuiMessage.get("ProteinBackboneStatus.Status"),
                    GuiMessage.get("ProteinBackboneStatus.Segment")});
                tmpProteinBackboneStatusValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // Index
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // AminoAcid
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // BackboneParticle
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Status
                    ModelDefinitions.CELL_WIDTH_NUMERIC_80}); // Segment
                ValueItemDataTypeFormat tmpIndexTypeFormat = new ValueItemDataTypeFormat(false);
                ValueItemDataTypeFormat tmpAminoAcidTypeFormat = new ValueItemDataTypeFormat(false);
                ValueItemDataTypeFormat tmpStatusTypeFormat = new ValueItemDataTypeFormat(
                        GuiMessage.get("ProteinBackboneStatus.Status.On"),
                        new String[]{GuiMessage.get("ProteinBackboneStatus.Status.On"), GuiMessage.get("ProteinBackboneStatus.Status.Off")}
                );
                String[] tmpProteinBackboneParticleIndexArray = aPdbToDPD.getCAlphaKeys();
                ValueItemDataTypeFormat tmpSegmentTypeFormat = new ValueItemDataTypeFormat("0", 0, 0.0, Double.POSITIVE_INFINITY);
                ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpBackboneParticleStatusArray.length][];
                for (int i = 0; i < tmpBackboneParticleStatusArray.length; i++) {
                    tmpMatrix[i] = new ValueItemMatrixElement[5];
                    // NOTE: Index of AminoAcid starts with "1".
                    tmpMatrix[i][0] = new ValueItemMatrixElement(String.valueOf(i + 1), tmpIndexTypeFormat);
                    // NOTE: Index of AminoAcid starts with "1".
                    AminoAcid tmpAminoAcid = aPdbToDPD.getAminoAcid(i + 1);
                    tmpMatrix[i][1] = new ValueItemMatrixElement(tmpAminoAcid.getName(), tmpAminoAcidTypeFormat);
                    // Set protein backbone particle index. Parameter false: Not editable
                    tmpMatrix[i][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpProteinBackboneParticleIndexArray[i], ValueItemEnumDataType.TEXT, false));
                    if (tmpBackboneParticleStatusArray[i]) {
                        tmpMatrix[i][3] = new ValueItemMatrixElement(GuiMessage.get("ProteinBackboneStatus.Status.On"), tmpStatusTypeFormat);
                    } else {
                        tmpMatrix[i][3] = new ValueItemMatrixElement(GuiMessage.get("ProteinBackboneStatus.Status.Off"), tmpStatusTypeFormat);
                    }
                    tmpMatrix[i][4] = new ValueItemMatrixElement(String.valueOf(tmpBackboneParticleSegmentArray[i]), tmpSegmentTypeFormat);
                }
                tmpProteinBackboneStatusValueItem.setMatrix(tmpMatrix);
            }
            return tmpProteinBackboneStatusValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns SequenceLoopValueItem
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return SequenceLoopValueItem or null if none could be created
     */
    public static ValueItem getSequenceLoopValueItem(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpSequenceLoopValueItem = new ValueItem();
            tmpSequenceLoopValueItem.setName("SEQUENCE_LOOP");
            tmpSequenceLoopValueItem.setDisplayName(GuiMessage.get("SequenceLoop.DisplayName"));
            tmpSequenceLoopValueItem.setDescription(GuiMessage.get("SequenceLoop.Description"));
            tmpSequenceLoopValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(new String[]{
                GuiMessage.get("SequenceLoop.NoClosedLoop"),
                GuiMessage.get("SequenceLoop.HasClosedLoop")
            }));
            if (aPdbToDPD.isCircular()) {
                tmpSequenceLoopValueItem.setValue(GuiMessage.get("SequenceLoop.HasClosedLoop"));
            } else {
                tmpSequenceLoopValueItem.setValue(GuiMessage.get("SequenceLoop.NoClosedLoop"));
            }
            return tmpSequenceLoopValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns MutantValueItem for edit purposes
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return MutantValueItem for edit purposes or null if none could be created
     */
    public static ValueItem getMutantValueItemForEdit(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpMutantValueItem = new ValueItem();
            tmpMutantValueItem.setName("PROTEIN_MUTANT");
            tmpMutantValueItem.setDisplayName(GuiMessage.get("ProteinMutant.DisplayName"));
            tmpMutantValueItem.setDescription(GuiMessage.get("ProteinMutant.Description"));

            tmpMutantValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpMutantValueItem.setMatrixColumnNames(new String[]{GuiMessage.get("ProteinMutant.Chain"),
                GuiMessage.get("ProteinMutant.Index"),
                GuiMessage.get("ProteinMutant.AminoAcid"),
                GuiMessage.get("ProteinMutant.Replacement")});
            tmpMutantValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_400, // Chain
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Index
                ModelDefinitions.CELL_WIDTH_TEXT_150, // AminoAcid
                ModelDefinitions.CELL_WIDTH_TEXT_150}); // Replacement
            String[] tmpReplacementSelectionTexts = StandardParticleInteractionData.getInstance().getSortedAminoAcidNames();

            String[] tmpChainNameArray = aPdbToDPD.getCompoundNames();
            HashMap<String, String> tmpChainNameToIdMap = aPdbToDPD.getNameChainIDMap();
            ArrayList<String> tmpActiveChainIdList = aPdbToDPD.getMasterdata().getActiveChains();
            HashMap<String, AminoAcid[]> tmpOriginalAminoAcidSequenceMap = aPdbToDPD.getOriginalAminoAcidSequence();
            HashMap<String, AminoAcid[]> tmpCurrentAminoAcidSequenceMap = aPdbToDPD.getCurrentAminoAcidSequence();
            int tmpNumberOfAminoAcids = 0;
            for (String tmpChainName : tmpChainNameArray) {
                String tmpId = tmpChainNameToIdMap.get(tmpChainName);
                if (tmpActiveChainIdList.contains(tmpId)) {
                    tmpNumberOfAminoAcids += tmpOriginalAminoAcidSequenceMap.get(tmpId).length;
                }
            }
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpNumberOfAminoAcids][];
            int tmpIndex = 0;
            for (String tmpChainName : tmpChainNameArray) {
                String tmpId = tmpChainNameToIdMap.get(tmpChainName);
                if (tmpActiveChainIdList.contains(tmpId)) {
                    AminoAcid[] tmpOriginalAminoAcids = tmpOriginalAminoAcidSequenceMap.get(tmpId);
                    AminoAcid[] tmpCurrentAminoAcids = tmpCurrentAminoAcidSequenceMap.get(tmpId);
                    for (int i = 0; i < tmpOriginalAminoAcids.length; i++) {
                        tmpMatrix[tmpIndex] = new ValueItemMatrixElement[4];
                        // Set chain name. Parameter false: Not editable
                        tmpMatrix[tmpIndex][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpChainName, ValueItemEnumDataType.TEXT, false));
                        // Set amino acid index (NOTE: Starts with index = 1). Parameter false: Not editable
                        tmpMatrix[tmpIndex][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(i + 1), ValueItemEnumDataType.TEXT, false));
                        // Set original amino acid. Parameter false: Not editable
                        tmpMatrix[tmpIndex][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpOriginalAminoAcids[i].getName(), ValueItemEnumDataType.TEXT, false));
                        // Set replacement
                        tmpMatrix[tmpIndex][3] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpCurrentAminoAcids[i].getName(), tmpReplacementSelectionTexts));
                        // Highlight replacement if different to original amino acid
                        tmpMatrix[tmpIndex][3].getTypeFormat().setHightlight(!tmpOriginalAminoAcids[i].getName().equals(tmpCurrentAminoAcids[i].getName()));
                        tmpIndex++;
                    }
                }
            }
            // IMPORTANT: Notify update to highlight amino acid replacements
            tmpMutantValueItem.setUpdateNotifier(true);
            tmpMutantValueItem.setMatrix(tmpMatrix);
            return tmpMutantValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns MutantValueItem for show purposes
     *
     * @param aPdbToDPD A PdbToDpd instance (is NOT changed)
     * @return MutantValueItem for show purposes or null if none could be created
     */
    public static ValueItem getMutantValueItemForShow(PdbToDpd aPdbToDPD) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPdbToDPD == null) {
            return null;
        }
        // </editor-fold>
        try {
            ValueItem tmpMutantValueItem = new ValueItem();
            tmpMutantValueItem.setName("PROTEIN_MUTANT");
            tmpMutantValueItem.setDisplayName(GuiMessage.get("ProteinMutant.DisplayName"));
            tmpMutantValueItem.setDescription(GuiMessage.get("ProteinMutant.Description"));

            tmpMutantValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpMutantValueItem.setMatrixColumnNames(new String[]{GuiMessage.get("ProteinMutant.Chain"),
                GuiMessage.get("ProteinMutant.Index"),
                GuiMessage.get("ProteinMutant.AminoAcid"),
                GuiMessage.get("ProteinMutant.Replacement")});
            tmpMutantValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_400, // Chain
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Index
                ModelDefinitions.CELL_WIDTH_TEXT_150, // AminoAcid
                ModelDefinitions.CELL_WIDTH_TEXT_150}); // Replacement

            String[] tmpChainNameArray = aPdbToDPD.getCompoundNames();
            HashMap<String, String> tmpChainNameToIdMap = aPdbToDPD.getNameChainIDMap();
            ArrayList<String> tmpActiveChainIdList = aPdbToDPD.getMasterdata().getActiveChains();
            HashMap<String, AminoAcid[]> tmpOriginalAminoAcidSequenceMap = aPdbToDPD.getOriginalAminoAcidSequence();
            HashMap<String, AminoAcid[]> tmpCurrentAminoAcidSequenceMap = aPdbToDPD.getCurrentAminoAcidSequence();
            int tmpNumberOfAminoAcids = 0;
            for (String tmpChainName : tmpChainNameArray) {
                String tmpId = tmpChainNameToIdMap.get(tmpChainName);
                if (tmpActiveChainIdList.contains(tmpId)) {
                    tmpNumberOfAminoAcids += tmpOriginalAminoAcidSequenceMap.get(tmpId).length;
                }
            }
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpNumberOfAminoAcids][];
            int tmpIndex = 0;
            for (String tmpChainName : tmpChainNameArray) {
                String tmpId = tmpChainNameToIdMap.get(tmpChainName);
                if (tmpActiveChainIdList.contains(tmpId)) {
                    AminoAcid[] tmpOriginalAminoAcids = tmpOriginalAminoAcidSequenceMap.get(tmpId);
                    AminoAcid[] tmpCurrentAminoAcids = tmpCurrentAminoAcidSequenceMap.get(tmpId);
                    for (int i = 0; i < tmpOriginalAminoAcids.length; i++) {
                        tmpMatrix[tmpIndex] = new ValueItemMatrixElement[4];
                        // Set chain name. Parameter false: Not editable
                        tmpMatrix[tmpIndex][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpChainName, ValueItemEnumDataType.TEXT, false));
                        // Set amino acid index (NOTE: Starts with index = 1). Parameter false: Not editable
                        tmpMatrix[tmpIndex][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(i + 1), ValueItemEnumDataType.TEXT, false));
                        // Set original amino acid. Parameter false: Not editable
                        tmpMatrix[tmpIndex][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpOriginalAminoAcids[i].getName(), ValueItemEnumDataType.TEXT, false));
                        // Set replacement
                        tmpMatrix[tmpIndex][3] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpCurrentAminoAcids[i].getName(), ValueItemEnumDataType.TEXT, false));
                        // Highlight replacement if different to original amino acid
                        tmpMatrix[tmpIndex][3].getTypeFormat().setHightlight(!tmpOriginalAminoAcids[i].getName().equals(tmpCurrentAminoAcids[i].getName()));
                        tmpIndex++;
                    }
                }
            }
            tmpMutantValueItem.setMatrix(tmpMatrix);
            return tmpMutantValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Movie creation related methods">
    /**
     * Creates movie images
     * NOTE: No further checks are performed.
     *
     * @param anImageProvider IImageProvider instance
     * @param anIsBackwards True: Images are additionally supplied in backward
     * @param anImageDirectoryPathForMovies Image directory path for movies
     * order, false: Otherwise (forward only)
     */
    public static void createMovieImages(
        IImageProvider anImageProvider, 
        boolean anIsBackwards,
        String anImageDirectoryPathForMovies
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImageDirectoryPathForMovies == null || anImageDirectoryPathForMovies.isEmpty() || !(new File(anImageDirectoryPathForMovies)).isDirectory()) {
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Movie.MissingSetting"), 
                GuiMessage.get("Movie.MissingSetting.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        int tmpNumberOfMovieImages = GuiUtils.fileUtilityMethods.getMaximumImageNumberOfImageDirectoryForMovies(anImageDirectoryPathForMovies);
        if (tmpNumberOfMovieImages > 0) {
            if (!GuiUtils.getYesNoDecision(GuiMessage.get("AppendMovieImages.Title"), String.format(GuiMessage.get("AppendMovieImages.Message"), tmpNumberOfMovieImages))) {
                return;
            }
        }
        // </editor-fold>
        // Parameter true: Supply images backwards in addition
        MovieStartupTask tmpMovieStartupTask = new MovieStartupTask(anImageProvider, anIsBackwards, anImageDirectoryPathForMovies);
        DialogProgress.hasCanceled(GuiMessage.get("MovieStartup"), tmpMovieStartupTask);
        String[] tmpImageDimensionData = new String[] {
            String.valueOf(anImageProvider.getImage(0).getWidth()),
            String.valueOf(anImageProvider.getImage(0).getHeight())
        };
        String tmpMovieImageDimensionDataFilePathname = anImageDirectoryPathForMovies + File.separatorChar + ModelDefinitions.MOVIE_IMAGE_DIMENSION_DATA_FILENAME;
        GuiUtils.fileUtilityMethods.writeDefinedStringArrayToFile(tmpImageDimensionData, tmpMovieImageDimensionDataFilePathname);
    }

    /**
     * Creates movie
     * NOTE: No further checks are performed.
     * 
     * @param aMovieImagePath Movie image path
     * @param anImageDirectoryPathForMovies Image directory path for movies
     * @param aMovieDirectoryPathForMovies  Movie directory path for movies
     */
    public static void createMovie(
        String aMovieImagePath, 
        String anImageDirectoryPathForMovies, 
        String aMovieDirectoryPathForMovies
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!ModelUtils.isWindowsOperatingSystem()) {
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Movie.NonSupportedOS"), 
                GuiMessage.get("Movie.NonSupportedOS.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        if (!Preferences.getInstance().hasFFmpegInWinUtils()) {
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Movie.MissingWindowsMovieCreationTool"), 
                GuiMessage.get("Movie.MissingWindowsMovieCreationTool.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        if (aMovieImagePath == null || aMovieImagePath.isEmpty() || !(new File(aMovieImagePath)).isDirectory()
            || anImageDirectoryPathForMovies == null || anImageDirectoryPathForMovies.isEmpty() || !(new File(anImageDirectoryPathForMovies)).isDirectory()
            || aMovieDirectoryPathForMovies == null || aMovieDirectoryPathForMovies.isEmpty() || !(new File(aMovieDirectoryPathForMovies)).isDirectory()
        ) {
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Movie.MissingSelection"), 
                GuiMessage.get("Movie.MissingSelection.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        int tmpNumberOfMovieImages = GuiUtils.fileUtilityMethods.getMaximumImageNumberOfImageDirectoryForMovies(anImageDirectoryPathForMovies);
        if (tmpNumberOfMovieImages == 0) {
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Movie.MissingImages"), 
                GuiMessage.get("Movie.MissingImages.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Create movie">
            // <editor-fold defaultstate="collapsed" desc="- Copy FFmpeg to image directory">
            String tmpPathnameOfFFmpegInImageDirectory = anImageDirectoryPathForMovies + File.separatorChar + ModelDefinitions.FFMPEG_FILE_NAME;
            // Delete Jdpd if necessary
            if ((new File(tmpPathnameOfFFmpegInImageDirectory)).isFile()) {
                GuiUtils.fileUtilityMethods.deleteSingleFile(tmpPathnameOfFFmpegInImageDirectory);
            }
            // Copy FFmpeg executable file from winUtils directory to image directory
            if (!GuiUtils.fileUtilityMethods.copySingleFile(Preferences.getInstance().getFFmpegFilePathnameInWinUtils(), tmpPathnameOfFFmpegInImageDirectory)) {
                JOptionPane.showMessageDialog(null, GuiMessage.get("Error.NoMovieCreation"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set movie filename">
            int tmpMaximumImageNumber = GuiUtils.fileUtilityMethods.getMaximumImageNumberOfImageDirectoryForMovies(anImageDirectoryPathForMovies);
            String tmpMovieFilename = ModelDefinitions.PREFIX_OF_MOVIE_FILENAME + "1_" + String.valueOf(tmpMaximumImageNumber) + ModelDefinitions.MOVIE_FILE_ENDING;
            String tmpMovieFileSourcePathname = anImageDirectoryPathForMovies + File.separatorChar + tmpMovieFilename;
            String tmpMovieFileDestinationPathname = aMovieDirectoryPathForMovies+ File.separatorChar + tmpMovieFilename;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Create MP4 movie with FFmpeg">
            // NOTE: All images do have the same size
            String tmpMovieImageDimensionDataFilePathname = anImageDirectoryPathForMovies + File.separatorChar + ModelDefinitions.MOVIE_IMAGE_DIMENSION_DATA_FILENAME;
            if (!(new File(tmpMovieImageDimensionDataFilePathname)).isFile()) {
                JOptionPane.showMessageDialog(null, 
                    GuiMessage.get("Movie.MissingImageDimensionData"), 
                    GuiMessage.get("Movie.MissingImageDimensionData.Title"),
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            // tmpImageProviderDimensionData was written with UtiliyGui.createMovieImages()
            String[] tmpImageDimensionData = GuiUtils.fileUtilityMethods.readDefinedStringArrayFromFile(tmpMovieImageDimensionDataFilePathname);
            int tmpImageWidth = Integer.valueOf(tmpImageDimensionData[0]);
            int tmpImageHeight = Integer.valueOf(tmpImageDimensionData[1]);
            // NOTE: tmpImageWidth and tmpImageHeight MUST be multiples of 2, i.e. even
            if (tmpImageWidth % 2 != 0) {
                tmpImageWidth--;
            }
            if (tmpImageHeight % 2 != 0) {
                tmpImageHeight--;
            }
            // Format e.g. "480x480"
            String tmpImageResolution = String.valueOf(tmpImageWidth) + "x" + String.valueOf(tmpImageHeight);
            // Image format with zeros number string, e.g. "%09d.jpg"
            String tmpImageFormat = "%0" + ModelDefinitions.NUMBER_OF_DIGITS_FOR_ZEROS_NUMBER_STRING + "d.jpg";
            // e.g. "FFmpeg.exe -r 20 -i %%09d.jpg -s 480x480 -vcodec libx264 -crf 18 test_crf18.mp4"
            String[] tmpCommands = new String[]{
                tmpPathnameOfFFmpegInImageDirectory,
                "-r",
                String.valueOf(Preferences.getInstance().getAnimationSpeed()),
                "-i",
                tmpImageFormat,
                "-s",
                tmpImageResolution,
                "-vcodec",
                "libx264",
                "-crf",
                String.valueOf(Preferences.getInstance().getMovieQuality()),
                tmpMovieFilename
            };
            ProcessBuilder tmpProcessBuilder = new ProcessBuilder(tmpCommands);
            tmpProcessBuilder.redirectErrorStream(true);
            tmpProcessBuilder.directory(new File(anImageDirectoryPathForMovies));
            // Debug
            // System.out.println("Command = " + tmpProcessBuilder.command());
            Process tmpProcess = tmpProcessBuilder.start();
            BufferedReader tmpBufferedReader = new BufferedReader(new InputStreamReader(tmpProcess.getInputStream()));
            String tmpLine = null;
            while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                // Debug
                // System.out.println("Line = " + tmpLine);
            }
            int tmpExitValue = tmpProcess.waitFor();
            // Debug
            // System.out.println("Exit value = " + String.valueOf(tmpExitValue));
            if (tmpExitValue < 0) {
                JOptionPane.showMessageDialog(null, GuiMessage.get("Error.NoMovieCreation"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Move MP4 movie to movies folder">
            GuiUtils.fileUtilityMethods.deleteSingleFile(tmpMovieFileDestinationPathname);
            if (!GuiUtils.fileUtilityMethods.moveSingleFile(tmpMovieFileSourcePathname, tmpMovieFileDestinationPathname)) {
                JOptionPane.showMessageDialog(null, GuiMessage.get("Error.NoMovieCreation"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Delete FFmpeg">
            GuiUtils.fileUtilityMethods.deleteSingleFile(tmpPathnameOfFFmpegInImageDirectory);
            // </editor-fold>
            // </editor-fold>
            MouseCursorManagement.getInstance().setDefaultCursor();
            GuiUtils.startViewer(tmpMovieFileDestinationPathname);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Error.NoMovieCreation"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Checks if movie can be created.
     * 
     * @param aMovieImagePath Movie image path
     * @return True: Movie can be created, false: Otherwise
     */
    public static boolean canCreateMovie(String aMovieImagePath) {
        if (!ModelUtils.isWindowsOperatingSystem()) {
            return false;
        }
        if (!Preferences.getInstance().hasFFmpegInWinUtils()) {
            return false;
        }
        if (aMovieImagePath == null || aMovieImagePath.isEmpty() || !(new File(aMovieImagePath)).isDirectory()) {
            return false;
        }
        int tmpNumberOfMovieImages = 
            GuiUtils.fileUtilityMethods.getMaximumImageNumberOfImageDirectoryForMovies(Preferences.getInstance().getImageDirectoryPathForSimulationMovies()
            );
        return tmpNumberOfMovieImages != 0;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Value item related methods">
    /**
     * Adds rotation change value item with name 
     * ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition Vertical position of rotation change value item
     * to be added
     * @param aNodeNames Node names for rotation change value item
     * @param aValueItemName Name of value item
     * @param aSimulationBoxChangeInfo Simulation box change info with rotation
     * change information
     */
    public static void addRotationChangeValueItem(
        ValueItemContainer aValueItemContainer,
        int aVerticalPosition,
        String[] aNodeNames,
        String aValueItemName,
        SimulationBoxChangeInfo aSimulationBoxChangeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return;
        }
        if (aVerticalPosition < 0) {
            return;
        }
        if (aNodeNames == null || aNodeNames.length == 0) {
            return;
        }
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return;
        }
        if (aSimulationBoxChangeInfo == null) {
            return;
        }
        // </editor-fold>
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setName(aValueItemName);
        tmpValueItem.setDisplayName(GuiMessage.get("SimulationBoxChange.RotationAngleChange"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(new String[]{
            GuiMessage.get("SimulationBoxChange.RotationAngleChange.AroundXaxis"),
            GuiMessage.get("SimulationBoxChange.RotationAngleChange.AroundYaxis"),
            GuiMessage.get("SimulationBoxChange.RotationAngleChange.AroundZaxis")});
        tmpValueItem.setMatrixColumnWidths(new String[]{
            ModelDefinitions.CELL_WIDTH_NUMERIC_120, // x-axis-rotation-angle
            ModelDefinitions.CELL_WIDTH_NUMERIC_120, // y-axis-rotation-angle
            ModelDefinitions.CELL_WIDTH_NUMERIC_120}); // z-axis-rotation-angle
        tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
            new ValueItemDataTypeFormat("0.0", 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), // x-axis-rotation-change-angle
            new ValueItemDataTypeFormat("0.0", 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), // y-axis-rotation-change-angle
            new ValueItemDataTypeFormat("0.0", 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)}); // z-axis-rotation-change-angle
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getRotationChangeAroundXaxisAngle()), 0, 0);
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getRotationChangeAroundYaxisAngle()), 0, 1);
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getRotationChangeAroundZaxisAngle()), 0, 2);
        tmpValueItem.setDescription(GuiMessage.get("SimulationBoxChange.RotationAngleChange.Description"));
        tmpValueItem.setVerticalPosition(aVerticalPosition);
        aValueItemContainer.addValueItem(tmpValueItem);
    }

    /**
     * Adds particle shift change value item with name 
     * ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME to value item 
     * container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition Vertical position of particle shift change 
     * value item to be added
     * @param aNodeNames Node names for particle shift change value item
     * @param aValueItemName Name of value item
     * @param aSimulationBoxChangeInfo Simulation box change info with particle 
     * shift change information
     */
    public static void addParticleShiftChangeValueItem(
        ValueItemContainer aValueItemContainer,
        int aVerticalPosition,
        String[] aNodeNames,
        String aValueItemName,
        SimulationBoxChangeInfo aSimulationBoxChangeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return;
        }
        if (aVerticalPosition < 0) {
            return;
        }
        if (aNodeNames == null || aNodeNames.length == 0) {
            return;
        }
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return;
        }
        if (aSimulationBoxChangeInfo == null) {
            return;
        }
        // </editor-fold>
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setName(aValueItemName);
        tmpValueItem.setDisplayName(GuiMessage.get("SimulationBoxChange.ParticleShiftChange"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(
            new String[]{
                GuiMessage.get("SimulationBoxChange.ParticleShiftChange.AlongXaxis"),
                GuiMessage.get("SimulationBoxChange.ParticleShiftChange.AlongYaxis"),
                GuiMessage.get("SimulationBoxChange.ParticleShiftChange.AlongZaxis")
            }
        );
        tmpValueItem.setMatrixColumnWidths(
            new String[]{
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // x-shift-change
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // y-shift-change
                ModelDefinitions.CELL_WIDTH_NUMERIC_120  // z-shift-change
            }
        );
        tmpValueItem.setDefaultTypeFormats(
            new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat("0.0", 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), // x-shift-change
                new ValueItemDataTypeFormat("0.0", 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), // y-shift-change
                new ValueItemDataTypeFormat("0.0", 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)  // z-shift-change
            }
        );
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getParticleShiftChangeX()), 0, 0);
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getParticleShiftChangeY()), 0, 1);
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getParticleShiftChangeZ()), 0, 2);
        tmpValueItem.setDescription(GuiMessage.get("SimulationBoxChange.ParticleShiftChange.Description"));
        tmpValueItem.setVerticalPosition(aVerticalPosition);
        aValueItemContainer.addValueItem(tmpValueItem);
    }

    /**
     * Adds shift change value item with name
 ModelDefinitions.SHIFT_CHANGE_VALUE_ITEM_NAME to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition Vertical position of shift change value item
     * to be added
     * @param aNodeNames Node names for shift change value item
     * @param aValueItemName Name of value item
     * @param aSimulationBoxChangeInfo Simulation box change info with shift
     * change information
     */
    public static void addShiftChangeValueItem(
        ValueItemContainer aValueItemContainer,
        int aVerticalPosition,
        String[] aNodeNames,
        String aValueItemName,
        SimulationBoxChangeInfo aSimulationBoxChangeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return;
        }
        if (aVerticalPosition < 0) {
            return;
        }
        if (aNodeNames == null || aNodeNames.length == 0) {
            return;
        }
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return;
        }
        if (aSimulationBoxChangeInfo == null) {
            return;
        }
        // </editor-fold>
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setName(aValueItemName);
        tmpValueItem.setDisplayName(GuiMessage.get("SimulationBoxChange.ShiftChange"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(new String[]
            {
                GuiMessage.get("SimulationBoxChange.ShiftChange.AlongXview"),
                GuiMessage.get("SimulationBoxChange.ShiftChange.AlongYview")
            }
        );
        tmpValueItem.setMatrixColumnWidths(new String[]
            {
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // AlongXview
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // AlongYview
            }
        );
        tmpValueItem.setDefaultTypeFormats(
            new ValueItemDataTypeFormat[]
            {
                new ValueItemDataTypeFormat(
                    "0.0", 
                    0, 
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY
                ), // AlongXview
                new ValueItemDataTypeFormat(
                    "0.0", 
                    0, 
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY
                ) // AlongYview
            }
        );
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getXshiftChange()), 0, 0);
        tmpValueItem.setValue(String.valueOf(aSimulationBoxChangeInfo.getYshiftChange()), 0, 1);
        tmpValueItem.setDescription(GuiMessage.get("SimulationBoxChange.ShiftChange.Description"));
        tmpValueItem.setVerticalPosition(aVerticalPosition);
        aValueItemContainer.addValueItem(tmpValueItem);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Version related methods">
    /**
     * Returns if job input has allowed application version for this version of MFsim
     * 
     * @param aJobInput Job input
     * @return True: Job input has allowed version, false: Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public static boolean isAllowedJobInputApplicationVersion(JobInput aJobInput) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInput == null) {
            throw new IllegalArgumentException("Argument is illegal");
        }
        // </editor-fold>
        String tmpVersionOfJobInput = aJobInput.getMFsimApplicationVersion();
        if (tmpVersionOfJobInput == null || !ModelUtils.isVersionOneEqualOrHigher(tmpVersionOfJobInput, ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION)) {
            // Job input application version is NOT allowed
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.SelectedJobInputForbiddenVersionFormat"), ModelDefinitions.MINIMUM_JOB_INPUT_APPLICATION_VERSION), 
                GuiMessage.get("Information.NotificationTitle"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return false;
        } else {
            return true;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Zoom related methods">
    /**
     * Returns zoom boundaries
     * Note: In case of internal settings problems a notification is displayed.
     * 
     * @param aDataBoundaryValues Data boundary values (will be unchanged)
     * @param aCurrentDataBoundaryValues Current data boundary values (will be unchanged)
     * @return New data boundary values or null if none could be set
     */
    public static double[] getZoomBoundaries(double[] aDataBoundaryValues, double[] aCurrentDataBoundaryValues) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDataBoundaryValues == null) {
            return null;
        }
        if (aCurrentDataBoundaryValues == null) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Set data boundary values">
            double tmpXmin = aDataBoundaryValues[0];
            double tmpXmax = aDataBoundaryValues[1];
            double tmpYmin = aDataBoundaryValues[2];
            double tmpYmax = aDataBoundaryValues[3];
            double tmpCurrentXmin = aCurrentDataBoundaryValues[0];
            double tmpCurrentXmax = aCurrentDataBoundaryValues[1];
            double tmpCurrentYmin = aCurrentDataBoundaryValues[2];
            double tmpCurrentYmax = aCurrentDataBoundaryValues[3];
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set data item container for edit">
            ValueItem tmpValueItem;
            String[] tmpNodeNames;
            ValueItemContainer tmpValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 0;
            int tmpNumberOfDecimals = 3;

            tmpNodeNames = new String[]{GuiMessage.get("Zoom.Boundaries.Root")};

            tmpValueItem = new ValueItem();
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                GuiUtils.stringUtilityMethods.formatDoubleValue(tmpXmin, tmpNumberOfDecimals), 
                tmpNumberOfDecimals, 
                -Double.MAX_VALUE, 
                Double.MAX_VALUE
            ));
            tmpValueItem.setName("ZOOM_BOUNDARIES_X_MIN");
            tmpValueItem.setDescription(GuiMessage.get("Zoom.Boundaries.Description"));
            tmpValueItem.setDisplayName(GuiMessage.get("Zoom.Boundaries.xMin"));
            if (tmpCurrentXmin == -Double.MAX_VALUE) {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpXmin, tmpNumberOfDecimals));
            } else {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpCurrentXmin, tmpNumberOfDecimals));
            }
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItemContainer.addValueItem(tmpValueItem);

            tmpValueItem = new ValueItem();
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                GuiUtils.stringUtilityMethods.formatDoubleValue(tmpXmax, tmpNumberOfDecimals), 
                tmpNumberOfDecimals, 
                -Double.MAX_VALUE, 
                Double.MAX_VALUE
            ));
            tmpValueItem.setName("ZOOM_BOUNDARIES_X_MAX");
            tmpValueItem.setDescription(GuiMessage.get("Zoom.Boundaries.Description"));
            tmpValueItem.setDisplayName(GuiMessage.get("Zoom.Boundaries.xMax"));
            if (tmpCurrentXmax == Double.MAX_VALUE) {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpXmax, tmpNumberOfDecimals));
            } else {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpCurrentXmax, tmpNumberOfDecimals));
            }
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItemContainer.addValueItem(tmpValueItem);

            tmpValueItem = new ValueItem();
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                GuiUtils.stringUtilityMethods.formatDoubleValue(tmpYmin, tmpNumberOfDecimals),
                tmpNumberOfDecimals, 
                -Double.MAX_VALUE, 
                Double.MAX_VALUE
            ));
            tmpValueItem.setName("ZOOM_BOUNDARIES_Y_MIN");
            tmpValueItem.setDescription(GuiMessage.get("Zoom.Boundaries.Description"));
            tmpValueItem.setDisplayName(GuiMessage.get("Zoom.Boundaries.yMin"));
            if (tmpCurrentYmin == -Double.MAX_VALUE) {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpYmin, tmpNumberOfDecimals));
            } else {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpCurrentYmin, tmpNumberOfDecimals));
            }
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItemContainer.addValueItem(tmpValueItem);

            tmpValueItem = new ValueItem();
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                GuiUtils.stringUtilityMethods.formatDoubleValue(tmpYmax, tmpNumberOfDecimals), 
                tmpNumberOfDecimals, 
                -Double.MAX_VALUE, 
                Double.MAX_VALUE
            ));
            tmpValueItem.setName("ZOOM_BOUNDARIES_Y_MAX");
            tmpValueItem.setDescription(GuiMessage.get("Zoom.Boundaries.Description"));
            tmpValueItem.setDisplayName(GuiMessage.get("Zoom.Boundaries.yMax"));
            if (tmpCurrentYmax == Double.MAX_VALUE) {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpYmax, tmpNumberOfDecimals));
            } else {
                tmpValueItem.setValue(GuiUtils.stringUtilityMethods.formatDoubleValue(tmpCurrentYmax, tmpNumberOfDecimals));
            }
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpValueItemContainer.addValueItem(tmpValueItem);
            // </editor-fold>
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("Zoom.Boundaries.Title"), tmpValueItemContainer)) {
                // <editor-fold defaultstate="collapsed" desc="Evaluate changed zoom boundaries">
                tmpXmin = tmpValueItemContainer.getValueItem("ZOOM_BOUNDARIES_X_MIN").getValueAsDouble();
                tmpXmax = tmpValueItemContainer.getValueItem("ZOOM_BOUNDARIES_X_MAX").getValueAsDouble();
                tmpYmin = tmpValueItemContainer.getValueItem("ZOOM_BOUNDARIES_Y_MIN").getValueAsDouble();
                tmpYmax = tmpValueItemContainer.getValueItem("ZOOM_BOUNDARIES_Y_MAX").getValueAsDouble();
                if (tmpXmin < tmpXmax && tmpYmin < tmpYmax) {
                    return new double[] {tmpXmin, tmpXmax, tmpYmin, tmpYmax};
                } else {
                    JOptionPane.showMessageDialog(null, 
                        GuiMessage.get("Zoom.Boundaries.Invalid"), 
                        GuiMessage.get("Information.NotificationTitle"),
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return null;
                }
                // </editor-fold>
            } else {
                return null;
            }
        } catch (Exception anException) {
            return null;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
