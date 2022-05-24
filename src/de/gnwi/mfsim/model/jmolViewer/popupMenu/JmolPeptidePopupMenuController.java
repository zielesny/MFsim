/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
package de.gnwi.mfsim.model.jmolViewer.popupMenu;

import de.gnwi.mfsim.model.jmolViewer.command.JmolDpdPeptideCommands;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dPeptideController;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Peptide popup menu controller.
 *
 * @author Andreas Truszkowski
 */
public class JmolPeptidePopupMenuController {

    private JmolPeptidePopupMenuView view = null;
    private Jmol3dPeptideController owner = null;
    private ActionListener popupMenuListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(view.getAxesMenuItem())) {
                if (view.getAxesMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ShowAxes);
                } else {
                    owner.executeCommand(JmolDpdPeptideCommands.HideAxes);
                }
            } else if (e.getSource().equals(view.getCartoonMenuItem())) {
                if (view.getCartoonMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ShowCartoon);
                } else {
                    owner.executeCommand(JmolDpdPeptideCommands.HideCartoon);
                }
            } else if (e.getSource().equals(view.getMoleculeMenuItem())) {
                if (view.getMoleculeMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ShowMolecule);
                } else {
                    owner.executeCommand(JmolDpdPeptideCommands.HideMolecule);
                }
            }
            // Note: "Show hydrogens" is deactivated since no hydrogens are added 
            //       with "pdbAddHydrogens" in 
            //       Jmol3dPeptideController.showProteinFromMasterdata()
            // Old code:
            // } else if (e.getSource().equals(view.getHydrogensMenuItem())) {
            //     if (view.getHydrogensMenuItem().isSelected()) {
            //         owner.setShowHydrogens(true);
            //     } else {
            //        owner.setShowHydrogens(false);
            //     }
            // }
            if (view.getMoleculeMenuItem().isSelected()) {
                if (view.getNormalMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ColorNormal);
                } else if (view.getChargeMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ColorCharge);
                } else if (view.getPolarityMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ColorPolarity);
                } else if (view.getBackboneMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ColorBackbone);
                } else if (view.getcAlphaMenuItem().isSelected()) {
                    owner.executeCommand(JmolDpdPeptideCommands.ColorCAlpha);
                }
            }
        }
    };

    /**
     * Creates a new instance.
     *
     * @param anOwner Controller owner.
     */
    public JmolPeptidePopupMenuController(Jmol3dPeptideController anOwner) {
        this.owner = anOwner;
        this.view = new JmolPeptidePopupMenuView();
        this.view.getAxesMenuItem().addActionListener(this.popupMenuListener);
        this.view.getCartoonMenuItem().addActionListener(this.popupMenuListener);
        this.view.getMoleculeMenuItem().addActionListener(this.popupMenuListener);
        this.view.getNormalMenuItem().addActionListener(this.popupMenuListener);
        this.view.getChargeMenuItem().addActionListener(this.popupMenuListener);
        this.view.getPolarityMenuItem().addActionListener(this.popupMenuListener);
        this.view.getBackboneMenuItem().addActionListener(this.popupMenuListener);
        this.view.getcAlphaMenuItem().addActionListener(this.popupMenuListener);
        // Note: "Show hydrogens" is deactivated since no hydrogens are added 
        //       with "pdbAddHydrogens" in 
        //       Jmol3dPeptideController.showProteinFromMasterdata()
        // Old code:
        // this.view.getHydrogensMenuItem().addActionListener(this.popupMenuListener);
    }

    /**
     * Shows the popup menu at given coordinates.
     *
     * @param aComponent Parent component.
     * @param aX X-coordinate.
     * @param aY Y-coordinate.
     */
    public void showMenu(Component aComponent, int aX, int aY) {
        this.view.show(aComponent, aX, aY);
    }
}
