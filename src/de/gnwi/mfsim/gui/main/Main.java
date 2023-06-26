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
package de.gnwi.mfsim.gui.main;

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.job.JobManager;
import de.gnwi.mfsim.model.job.JdpdValueItemDefinition;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import java.io.File;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * MFsim entry class with main method.
 * 
 * @author Achim Zielesny
 */
public class Main {

    /**
     * Main method
     *
     * @param args Arguments
     */
    public static void main(String args[]) {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set Look and feel">
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(laf.getName())) {
                    UIManager.setLookAndFeel(laf.getClassName());
                    break;
                }
            }
            // Set JFileChooser cancel button text
            UIManager.put("FileChooser.fileNameLabelText", GuiMessage.get("FileChooser.fileNameLabelText"));
            UIManager.put("FileChooser.acceptAllFileFilterText", GuiMessage.get("FileChooser.acceptAllFileFilterText"));
            UIManager.put("FileChooser.cancelButtonText", GuiMessage.get("FileChooser.cancelButtonText"));
            UIManager.put("FileChooser.cancelButtonToolTipText", GuiMessage.get("FileChooser.cancelButtonToolTipText"));
            UIManager.put("FileChooser.helpButtonText", GuiMessage.get("FileChooser.helpButtonText"));
            UIManager.put("FileChooser.helpButtonToolTipText", GuiMessage.get("FileChooser.helpButtonToolTipText"));
            UIManager.put("FileChooser.openButtonText", GuiMessage.get("FileChooser.openButtonText"));
            UIManager.put("FileChooser.openButtonToolTipText", GuiMessage.get("FileChooser.openButtonToolTipText"));
            UIManager.put("FileChooser.saveButtonText", GuiMessage.get("FileChooser.saveButtonText"));
            UIManager.put("FileChooser.saveButtonToolTipText", GuiMessage.get("FileChooser.saveButtonToolTipText"));
            UIManager.put("FileChooser.updateButtonText", GuiMessage.get("FileChooser.updateButtonText"));
            UIManager.put("FileChooser.updateButtonToolTipText", GuiMessage.get("FileChooser.updateButtonToolTipText"));
            UIManager.put("FileChooser.lookInLabelText", GuiMessage.get("FileChooser.lookInLabelText"));
            UIManager.put("FileChooser.filesOfTypeLabelText", GuiMessage.get("FileChooser.filesOfTypeLabelText"));
            UIManager.put("FileChooser.upFolderToolTipText", GuiMessage.get("FileChooser.upFolderToolTipText"));
            UIManager.put("FileChooser.upFolderAccessibleName", GuiMessage.get("FileChooser.upFolderAccessibleName"));
            UIManager.put("FileChooser.DesktopFolderToolTipText", GuiMessage.get("FileChooser.DesktopFolderToolTipText"));
            UIManager.put("FileChooser.newFolderToolTipText", GuiMessage.get("FileChooser.newFolderToolTipText"));
            UIManager.put("FileChooser.listViewButtonToolTipText", GuiMessage.get("FileChooser.listViewButtonToolTipText"));
            UIManager.put("FileChooser.detailsViewButtonToolTipText", GuiMessage.get("FileChooser.detailsViewButtonToolTipText"));
            UIManager.put("FileChooser.refreshActionLabelText", GuiMessage.get("FileChooser.refreshActionLabelText"));
            UIManager.put("FileChooser.fileNameHeaderText", GuiMessage.get("FileChooser.fileNameHeaderText"));
            UIManager.put("FileChooser.fileSizeHeaderText", GuiMessage.get("FileChooser.fileSizeHeaderText"));
            UIManager.put("FileChooser.fileTypeHeaderText", GuiMessage.get("FileChooser.fileTypeHeaderText"));
            UIManager.put("FileChooser.fileDateHeaderText", GuiMessage.get("FileChooser.fileDateHeaderText"));
            UIManager.put("FileChooser.fileAttrHeaderText", GuiMessage.get("FileChooser.fileAttrHeaderText"));
            UIManager.put("FileChooser.sortMenuLabelText", GuiMessage.get("FileChooser.sortMenuLabelText"));
            UIManager.put("FileChooser.openDialogTitleText", GuiMessage.get("FileChooser.openDialogTitleText"));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check Java version">
            // NOTE: This comparison is error prone for higher Java versions - but works when compared to Java 1.8!
            if (ModelDefinitions.JAVA_VERSION.compareTo(ModelDefinitions.MINIMUM_JAVA_VERSION) < 0) {
                // <editor-fold defaultstate="collapsed" desc="Insufficient Java version">
                JOptionPane.showMessageDialog(null, 
                    String.format(GuiMessage.get("Error.InsufficientJavaVersion"), ModelDefinitions.MINIMUM_JAVA_VERSION), 
                    GuiMessage.get("Error.ErrorNotificationTitle"), 
                    JOptionPane.ERROR_MESSAGE
                );
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.exitApplication(-1);
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Assure single instance">
            String tmpSingleInstanceFilePathname = ModelUtils.getSingleInstanceFilePathname();
            if (tmpSingleInstanceFilePathname == null) {
                JOptionPane.showMessageDialog(null, 
                    GuiMessage.get("Error.NoSingleInstanceFilePathname"), 
                    GuiMessage.get("Error.ErrorNotificationTitle"),
                    JOptionPane.ERROR_MESSAGE
                );
            }
            if ((new File(tmpSingleInstanceFilePathname)).exists()) {
                if (!GuiUtils.getYesNoDecision(GuiMessage.get("Error.SecondInstanceTitle"), GuiMessage.get("Error.SecondInstanceMessage"))) {
                    // Do NOT use Utility.exitApplication(-1) since this would delete single instance file
                    System.exit(-1);
                }
            } else {
                ModelUtils.createDirectory(ModelUtils.getDpdDataPath());
                ModelUtils.createEmptyFile(tmpSingleInstanceFilePathname);
            }
            // No other application instance is running
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check screen resolution">
            if (!GuiUtils.checkScreenSize()) {
                // <editor-fold defaultstate="collapsed" desc="Insufficient screen size">
                JOptionPane.showMessageDialog(null, GuiMessage.get("Error.InsufficientScreenSize"), GuiMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.exitApplication(-1);

                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize Preferences">
            try {
                Preferences.getInstance();
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                // <editor-fold defaultstate="collapsed" desc="Fatal error message">
                JOptionPane.showMessageDialog(null, ModelMessage.get("Error.NoPreferencesBasicInitialisation"), ModelMessage.get("Error.ErrorNotificationTitle"),
                        JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.exitApplication(-1);

                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Log MFsim session start">
            // false: NO log event
            ModelUtils.appendToLogfile(false, String.format(ModelDefinitions.MFSIM_SESSION_START_FORMAT, ModelDefinitions.APPLICATION_VERSION));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Get particle related data if necessary">
            // <editor-fold defaultstate="collapsed" desc="- Check MFsim_Source particles directory">
            if (!(new File(Preferences.getInstance().getDpdSourceParticlesPath())).isDirectory()) {
                // <editor-fold defaultstate="collapsed" desc="Fatal error message">
                JOptionPane.showMessageDialog(null, ModelMessage.get("Error.MissingCorruptParticleData"), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.appendToLogfile(true, "Main: MissingCorruptParticleData in if (!(new File(PreferenceBasic.getInstance().getDpdSourceParticlesPath())).isDirectory())");
                ModelUtils.exitApplication(-1);
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Check MFsim_Source particle set file">
            String tmpCurrentParticleSetFilePathname = Preferences.getInstance().getCurrentParticleSetFilePathname();
            if (tmpCurrentParticleSetFilePathname == null || tmpCurrentParticleSetFilePathname.isEmpty()) {
                // <editor-fold defaultstate="collapsed" desc="Fatal error message">
                JOptionPane.showMessageDialog(null, ModelMessage.get("Error.MissingCorruptParticleData"), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.appendToLogfile(true, "Main: MissingCorruptParticleData in if (tmpCurrentParticleSetFilePathname == null || tmpCurrentParticleSetFilePathname.isEmpty())");
                ModelUtils.exitApplication(-1);
                // </editor-fold>
            }
            boolean tmpIsFatalParticleFileError = !(new File(tmpCurrentParticleSetFilePathname)).isFile();
            if (tmpIsFatalParticleFileError) {
                // <editor-fold defaultstate="collapsed" desc="Fatal error message">
                JOptionPane.showMessageDialog(null, ModelMessage.get("Error.MissingCorruptParticleData"), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
                ModelUtils.appendToLogfile(true, "Main: MissingCorruptParticleData in if (tmpIsFatalParticleFileError)");
                ModelUtils.exitApplication(-1);
                // </editor-fold>
            }
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize StandardParticleInteractionData">
            // If initialisation of StandardParticleInteractionData fails application is exited
            StandardParticleInteractionData.getInstance();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize JdpdValueItemDefinition (uses StandardParticleInteractionData)">
            // If initialisation of JdpdValueItemDefinition fails application is exited
            JdpdValueItemDefinition.getInstance();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize engine communication">
            // If initialisation of JobManager fails application is exited
            JobManager.getInstance();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Instantiate MainFrame frame object">
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    MainFrame tmpMainFrame = new MainFrame();
                    tmpMainFrame.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
                    GuiUtils.checkFrameSize(tmpMainFrame);
                    GuiUtils.centerFrameOnScreen(tmpMainFrame);
                    tmpMainFrame.setVisible(true);
                }
            });
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(null, 
                ModelMessage.get("Error.UnknownError"), 
                ModelMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);
            // </editor-fold>
        }
    }

}
