/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
package de.gnwi.mfsim.model.jmolViewer.popupMenu;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Andreas Truszkowski
 */
public class JmolPeptidePopupMenuView extends JPopupMenu {

    // Note: "Show hydrogens" is deactivated since no hydrogens are added 
    //       with "pdbAddHydrogens" in 
    //       Jmol3dPeptideController.showProteinFromMasterdata()

    /**
     * GUI element
     */
    JCheckBoxMenuItem axesMenuItem = new JCheckBoxMenuItem("Show axes");
    /**
     * GUI element
     */
    JCheckBoxMenuItem cartoonMenuItem = new JCheckBoxMenuItem("Show cartoon");
    /**
     * GUI element
     */
    JCheckBoxMenuItem moleculeMenuItem = new JCheckBoxMenuItem("Show molecule");

    // JCheckBoxMenuItem hydrogensMenuItem = new JCheckBoxMenuItem("Show hydrogens");

    /**
     * GUI element
     */
    JRadioButtonMenuItem normalMenuItem = new JRadioButtonMenuItem("Normal");
    /**
     * GUI element
     */
    JRadioButtonMenuItem polarityMenuItem = new JRadioButtonMenuItem("Polarity");
    /**
     * GUI element
     */
    JRadioButtonMenuItem chargeMenuItem = new JRadioButtonMenuItem("Charge");
    /**
     * GUI element
     */
    JRadioButtonMenuItem backboneMenuItem = new JRadioButtonMenuItem("Backbone");
    /**
     * GUI element
     */
    JRadioButtonMenuItem cAlphaMenuItem = new JRadioButtonMenuItem("C-alpha atoms");

    /**
     * JmolPeptidePopupMenuView
     */    
    public JmolPeptidePopupMenuView() {
        this.normalMenuItem.setSelected(true);
        this.moleculeMenuItem.setSelected(true);
        this.add(this.cartoonMenuItem);
        this.add(this.moleculeMenuItem);
        this.addSeparator();
        ButtonGroup tmpGroup = new ButtonGroup();
        tmpGroup.add(this.normalMenuItem);
        tmpGroup.add(this.polarityMenuItem);
        tmpGroup.add(this.chargeMenuItem);
        tmpGroup.add(this.backboneMenuItem);
        tmpGroup.add(this.cAlphaMenuItem);
        this.add(this.normalMenuItem);
        this.add(this.polarityMenuItem);
        this.add(this.chargeMenuItem);
        this.add(this.backboneMenuItem);
        this.add(this.cAlphaMenuItem);
        this.addSeparator();
        this.add(this.axesMenuItem);
        // this.add(this.hydrogensMenuItem);
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JCheckBoxMenuItem getAxesMenuItem() {
        return axesMenuItem;
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JCheckBoxMenuItem getCartoonMenuItem() {
        return cartoonMenuItem;
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JCheckBoxMenuItem getMoleculeMenuItem() {
        return moleculeMenuItem;
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JRadioButtonMenuItem getNormalMenuItem() {
        return normalMenuItem;
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JRadioButtonMenuItem getPolarityMenuItem() {
        return polarityMenuItem;
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JRadioButtonMenuItem getChargeMenuItem() {
        return chargeMenuItem;
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JRadioButtonMenuItem getBackboneMenuItem() {
        return backboneMenuItem;
    }

    /**
     * JmolPeptidePopupMenuView
     * 
     * @return Menu item
     */    
    public JRadioButtonMenuItem getcAlphaMenuItem() {
        return cAlphaMenuItem;
    }

    // public JCheckBoxMenuItem getHydrogensMenuItem() {
    //     return hydrogensMenuItem;
    // }
   
}
