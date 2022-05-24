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
package de.gnwi.mfsim.model.util;

import java.io.File;

/**
 * Directory information
 * 
 * @author Achim Zielesny
 */
public class DirectoryInformation {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Time utility methods
     */
    private final TimeUtilityMethods timeUtilityMethods = new TimeUtilityMethods();

    /**
     * Directory path
     */
    private String directoryPath;

    /**
     * Timestamp
     */
    private String timestamp;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aDirectoryPath
     *            Directory path
     * @param aTimestamp
     *            Timestamp
     * @throws IllegalArgumentException
     *             Is thrown if an argument is illegal
     */
    public DirectoryInformation(
        String aDirectoryPath, 
        String aTimestamp
    ) throws IllegalArgumentException
    {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDirectoryPath == null || aDirectoryPath.isEmpty() || !(new File(aDirectoryPath)).isDirectory()) {
            throw new IllegalArgumentException("Illegal argument aDirectoryPath: " + " '" + aDirectoryPath + "'");
        }
        if (!this.timeUtilityMethods.isValidTimestampInStandardFormat(aTimestamp)) {
            throw new IllegalArgumentException("Illegal argument aTimestamp: " + " '" + aTimestamp + "'");
        }
        // </editor-fold>
        this.directoryPath = aDirectoryPath;
        this.timestamp = aTimestamp;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns directory path
     * 
     * @return Directory path
     */
    public String getDirectoryPath() {
        return this.directoryPath;
    }

    /**
     * Returns timestamp
     * 
     * @return Timestamp
     */
    public String getTimestamp() {
        return this.timestamp;
    }
    // </editor-fold>
    
}
