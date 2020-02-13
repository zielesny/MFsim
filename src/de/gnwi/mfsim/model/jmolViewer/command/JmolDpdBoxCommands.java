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
package de.gnwi.mfsim.model.jmolViewer.command;

import de.gnwi.mfsim.model.jmolViewer.command.IJmolCommands;

/**
 * Dpd box view Jmol command enum.
 *
 * @author Andreas Truszkowski
 */
public enum JmolDpdBoxCommands implements IJmolCommands {

    /**
     * Show axes command.
     */
    ShowAxes("set showaxes true;", true),
    /**
     * Hide axes command.
     */
    HideAxes("set showaxes false;", true),
    /**
     * Show boundbox command.
     */
    ShowBoundbox("set showboundbox true;", true),
    /**
     * Hide boundbox command
     */
    HideBoundbox("set showboundbox false", false),
    /**
     * Move to XZ front command.
     */
    MoveToXZFront("moveto 0 bottom;", false),
    /**
     * Move to XZ bak command.
     */
    MoveToXZBack("moveto 0 top; rotate z 180;", false),
    /**
     * Move to YZ left command.
     */
    MoveToYZLeft("moveto 0 left;rotate z 90;", false),
    /**
     * Move to YZ right command.
     */
    MoveToYZRight("moveto 0 right;rotate -z 90;", false),
    /**
     * Move to XY top command.
     */
    MoveToXYTop("moveto 0 front;", false),
    /**
     * Move to XY bottom command.
     */
    MoveToXYBottom("moveto 0 back;rotate -z 180;", false),
    /**
     * Spin on command.
     */
    SpinOn("spin on;", false),
    /**
     * Spin off command.
     */
    SpinOff("spin off;", false),
    /**
     * Deletes all distance measurements.
     */
    DeleteDistanceMeasurements("measure DELETE;", false),
    /**
     * Resets all user data.
     */
    ResetAll("reset ALL;display none;", false);
    /**
     * Jmol command sript.
     */
    private String script;
    /**
     * True when script wait shall be executed.
     */
    private boolean wait;

    /**
     * Creates a new instance.
     *
     * @param aScript Jmol command sript.
     * @param aWait True when script wait shall be executed.
     */
    private JmolDpdBoxCommands(String aScript, boolean aWait) {
        this.script = aScript;
        this.wait = aWait;
    }

    @Override
    public String getScript() {
        return this.script;
    }

    @Override
    public boolean isWait() {
        return this.wait;
    }
}
