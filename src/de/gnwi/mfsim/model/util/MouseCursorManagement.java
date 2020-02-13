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
package de.gnwi.mfsim.model.util;

import java.awt.Component;
import java.awt.Cursor;
import java.util.LinkedList;

/**
 * Manages components for mouse cursor display
 *
 * @author Achim Zielesny
 */
public final class MouseCursorManagement {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Basic preference instance
     */
    private static MouseCursorManagement mouseCursorManagement = new MouseCursorManagement();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Components for mouse cursor display
     */
    private LinkedList<java.awt.Component> mouseCursorComponentList;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private singleton constructor">
    /**
     * Singleton constructor
     */
    private MouseCursorManagement() {
        this.mouseCursorComponentList = new LinkedList<Component>();
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public singleton instance method">
    /**
     * Singleton initialisation and instance method
     *
     * @return MouseCursorManagement instance
     */
    public static MouseCursorManagement getInstance() {
        return MouseCursorManagement.mouseCursorManagement;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Pushes component for mouse cursor
     *
     * @param aMouseCursorComponent Component for mouse cursor
     */
    public void pushMouseCursorComponent(Component aMouseCursorComponent) {
        this.mouseCursorComponentList.addLast(aMouseCursorComponent);
    }

    /**
     * Pops component for mouse cursor display
     */
    public void popMouseCursorComponent() {
        this.mouseCursorComponentList.removeLast();
    }

    /**
     * Sets mouse wait cursor on current mouse cursor component
     */
    public void setWaitCursor() {
        if (this.mouseCursorComponentList.size() > 0) {
            this.mouseCursorComponentList.getLast().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
    }

    /**
     * Sets default cursor on current mouse cursor component
     */
    public void setDefaultCursor() {
        if (this.mouseCursorComponentList.size() > 0) {
            this.mouseCursorComponentList.getLast().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    // </editor-fold>
}
