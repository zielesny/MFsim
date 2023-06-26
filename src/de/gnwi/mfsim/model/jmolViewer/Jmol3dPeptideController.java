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
package de.gnwi.mfsim.model.jmolViewer;

import de.gnwi.mfsim.model.jmolViewer.popupMenu.JmolPeptidePopupMenuController;
import de.gnwi.mfsim.model.jmolViewer.command.IJmolCommands;
import de.gnwi.mfsim.model.jmolViewer.base.Jmol3dController;
import de.gnwi.mfsim.model.peptide.utils.DpdPeptideException;
import de.gnwi.mfsim.model.peptide.utils.PdbToDpdMasterdata;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Jmol peptide controller.
 *
 * @author Andreas Truszkowski
 */
public class Jmol3dPeptideController extends Jmol3dController {

    private boolean isShowHydrogens = false;
    private JmolPeptidePopupMenuController menuController = null;
    private ArrayList<String> chainIDs = null;
    private boolean isBiologicalAssembly = false;
    private int numberOfModels = 1;

    /**
     * Creates a new instance.
     */
    public Jmol3dPeptideController() {
        this(new Jmol3dPanel(), true);
    }

    /**
     * Creates a new instance.
     *
     * @param aView Jmol viewer view.
     */
    public Jmol3dPeptideController(Jmol3dPanel aView) {
        this(aView, true);
    }

    /**
     * Creates a new instance.
     *
     * @param aView Jmol viewer view.
     * @param isShowPopupMenu True, if popoup menu shall be shown.
     */
    public Jmol3dPeptideController(Jmol3dPanel aView, boolean isShowPopupMenu) {
        super(aView);
        if (isShowPopupMenu) {
            this.menuController = new JmolPeptidePopupMenuController(this);
        }
    }

    /**
     * Sets the Pdb string contaning the protein data.
     *
     * @param aPdb Pdb string.
     */
    public void setPdb(String aPdb) {
        this.chainIDs = null;
        this.menuController = new JmolPeptidePopupMenuController(this);
        StringBuilder tmpScript = new StringBuilder();
        tmpScript.append("reset All;set refreshing false;set pdbAddHydrogens true;");
        tmpScript.append("load data \"PDB\"\n");
        tmpScript.append(aPdb);
        tmpScript.append("end \"PDB\"; show data;");
        this.executeScript(tmpScript.toString());
        this.displayChains();
        this.executeScript("set refreshing true;");
    }

    /**
     * Sets the Mol string containing the molecule data.
     *
     * @param file Mol string.
     */
    public void setMolFile(String file) {
        StringBuilder tmpScript = new StringBuilder();
        tmpScript.append(String.format("set refreshing false; reset all; load files \"%s\";set refreshing true;", file));
        this.executeScript(tmpScript.toString());
    }

    /**
     * Displays given protein. Not active chains are hidden. Rotation is
     * applied. The protein is centered on screen.
     *
     * @param aMasterdata PdbToDPD masterdata object.
     * @throws DpdPeptideException DpdPeptideException
     */
    public void showProteinFromMasterdata(PdbToDpdMasterdata aMasterdata) throws DpdPeptideException {
        if (aMasterdata.getOriginalPdb() == null) {
            throw new DpdPeptideException("Peptide.ErrorProteinNotSet");
        }
        this.menuController = new JmolPeptidePopupMenuController(this);
        this.chainIDs = aMasterdata.getActiveChains();
        this.isBiologicalAssembly = !aMasterdata.getBiologicalAssembly().equals("Asymmetric Unit");
        this.numberOfModels = aMasterdata.getNumberOfModelsInAssembly();
        StringBuilder tmpScript = new StringBuilder();
        // NOTE: "set pdbAddHydrogens true;" causes problems with proteins like 3bmp.pdb which contain H2O molecules etc.
        tmpScript.append("reset All;set refreshing false;");
        // Old code:
        // tmpScript.append("reset All;set refreshing false;set pdbAddHydrogens true;");
        tmpScript.append("load data \"PDB\"\n");
        tmpScript.append(aMasterdata.getOriginalPdb());
        tmpScript.append("end \"PDB\"");
        if (aMasterdata.getBiologicalAssemblyFilter() != null) {
            tmpScript.append(aMasterdata.getBiologicalAssemblyFilter());
        }
        tmpScript.append(";show data;");
        this.executeScript(tmpScript.toString());
        this.displayChains();
        if (aMasterdata.getRotationScript() != null) {
            this.executeScript(aMasterdata.getRotationScript());
        }
        this.executeScript("set refreshing true;");
    }

    /**
     * Updates the rotation information in target masterdata object.
     *
     * @param aMasterdata PdbToDPD masterdata object.
     */
    public void updateRotationInMasterdata(PdbToDpdMasterdata aMasterdata) {
        aMasterdata.setRotation(this.getRotation());
    }

    @Override
    public void executeCommand(IJmolCommands aCommand) {
        //    this.executeScript("set refreshing false;");
        if (aCommand.name().equals("ColorCAlpha")) {
            // hide invisible chains
            String tmpScript = String.format("%s and (", aCommand.getScript());
            List<String> tmpDistinctChains = this.chainIDs.stream()
                    .map(chain -> {
                        if (chain.contains("/")) {
                            return chain.substring(0, chain.lastIndexOf("/"));
                        } else {
                            return chain;
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());
            for (int i = 0; i < tmpDistinctChains.size(); i++) {
                if (i > 0) {
                    tmpScript += " or ";
                }
                tmpScript += "*:" + tmpDistinctChains.get(i);
            }
            tmpScript += ");";
            this.executeScript(tmpScript);
        } else {
            super.executeCommand(aCommand);
            if (!aCommand.getScript().contains("hide")) {
                this.displayChains();
            }
        }
        //    this.executeScript("set refreshing true;");
    }

    /**
     * Displays all given chains and hides the rest.
     *
     * @param aChainIDs Chain IDs to display.
     */
    public void displayChains(ArrayList<String> aChainIDs) {
        this.chainIDs = aChainIDs;
        this.displayChains();
    }

    /**
     * Gets the active chain IDs.
     *
     * @return Acive chain IDs.
     */
    public ArrayList<String> getActiveChains() {
        return this.chainIDs;
    }

    /**
     * Displays all given chains and hides the rest.
     */
    private void displayChains() {
        if (this.chainIDs == null) {
            if (this.isShowHydrogens) {
                this.executeScript("hide not protein");
            } else {
                this.executeScript("hide not protein or hydrogen");
            }
        } else if (this.chainIDs.isEmpty()) {
            this.executeScript("hide all;");
        } else {
            String tmpChainScript = this.isBiologicalAssembly ? "frame all;" : "";
            tmpChainScript += "display (";
            for (int i = 0; i < this.chainIDs.size(); i++) {
                if (i > 0) {
                    tmpChainScript += " or ";
                }
                if (this.isBiologicalAssembly && this.numberOfModels > 1) {
                    String tmpChainID = this.chainIDs.get(i);
                    int tmpUnitCell = Integer.parseInt(tmpChainID.substring(tmpChainID.lastIndexOf("/") + 1));
                    String tmpOrgChainId = tmpChainID.substring(0, tmpChainID.lastIndexOf("/"));
                    tmpChainScript += String.format("(symop=%d and *:%s)", tmpUnitCell, tmpOrgChainId);
                } else {
                    tmpChainScript += "*:" + this.chainIDs.get(i);
                }
            }
            if (this.isShowHydrogens) {
                tmpChainScript += "); restrict protein;";
            } else {
                tmpChainScript += ") and not hydrogen; restrict protein;center {visible};";
            }
            this.executeScript(tmpChainScript);
        }
    }

    /**
     * Sets whether hydrogens should be rendered.
     *
     * @param aShowHydrogens True if hydrogens shall be visible.
     */
    public void setShowHydrogens(boolean aShowHydrogens) {
        this.executeScript("set refreshing false;");
        this.isShowHydrogens = aShowHydrogens;
        this.displayChains();
        this.executeScript("set refreshing true;");
    }

    @Override
    protected void handleMouseEvent(MouseEvent e) {
        if (this.menuController != null && e.isPopupTrigger()) {
            this.menuController.showMenu(e.getComponent(), e.getX(), e.getY());
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            e.consume();
        }
        if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
            e.consume();
        }
    }

    /**
     * Sets the background color.
     *
     * @param aColor The background color.
     */
    public void setBackgroundColor(Color aColor) {
        this.executeScriptWait(String.format("background [%d,%d,%d];", aColor.getRed(), aColor.getGreen(), aColor.getBlue()));
    }

    @Override
    protected void handleMouseWheelEvent(MouseWheelEvent e) {
    }
}
