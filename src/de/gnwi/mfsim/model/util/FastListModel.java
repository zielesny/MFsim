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

import javax.swing.DefaultListModel;

/**
 * Extended default list model for fast addElement-operations with deactivated fireContentsChanged event. See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4121430. This simple subclass of
 * DefaultListModel allows one to disable list notifications, e.g. while making a big change to the model. The DefaultListModels fireXXX methods have been promoted from protected to public in this
 * class so that clients can fire an appropriate ListDataEvent after the modifying the model with listeners disabled.
 *
 * @author Achim Zielesny
 */
public class FastListModel extends DefaultListModel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * true: Listeners are enabled, false: Otherwise
     */
    private boolean listenersEnabled;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000001L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public FastListModel() {
        super();
        // Enable listeners as a default
        this.listenersEnabled = true;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * fireContentsChanged
     *
     * @param source Source
     * @param index0 Index 0
     * @param index1 Index 1
     */
    public void fireContentsChanged(Object source, int index0, int index1) {
        if (this.getListenersEnabled()) {
            super.fireContentsChanged(source, index0, index1);
        }
    }

    /**
     * fireIntervalAdded
     *
     * @param source Source
     * @param index0 Index 0
     * @param index1 Index 1
     */
    public void fireIntervalAdded(Object source, int index0, int index1) {
        if (this.getListenersEnabled()) {
            super.fireIntervalAdded(source, index0, index1);
        }
    }

    /**
     * fireIntervalRemoved
     *
     * @param source Source
     * @param index0 Index 0
     * @param index1 Index 1
     */
    public void fireIntervalRemoved(Object source, int index0, int index1) {
        if (this.getListenersEnabled()) {
            super.fireIntervalAdded(source, index0, index1);
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Get enabling status of listeners
     *
     * @return true: Listeners are enabled, false: Otherwise
     */
    public boolean getListenersEnabled() {
        return this.listenersEnabled;
    }

    /**
     * Set enabling status of listeners
     *
     * @param enabled true: Listeners will be enabled, false: Otherwise
     */
    public void setListenersEnabled(boolean enabled) {
        this.listenersEnabled = enabled;
    }

    // </editor-fold>
}
