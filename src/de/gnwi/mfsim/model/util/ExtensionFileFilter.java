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
package de.gnwi.mfsim.model.util;

import java.io.File;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * FileFilter for extensions (like ".txt" etc.)
 * 
 * @author Achim Zielesny
 */
public class ExtensionFileFilter extends FileFilter {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">

    /**
     * Extensions
     */
    private String[] extensions;

    // </editor-fold>

    //

    // <editor-fold defaultstate="collapsed" desc="Constructor">

    /**
     * Constructor
     * 
     * @param aExtensions
     *            Extensions (like ".txt" etc.)
     * @throws IllegalArgumentException
     *             Thrown if aExtensions is illegal
     */
    public ExtensionFileFilter (String[] aExtensions) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aExtensions == null || aExtensions.length == 0) {
            throw new IllegalArgumentException("aExtensions is illegal.");
        }
        for (String tmpExtension : aExtensions) {
            if (tmpExtension == null || tmpExtension.isEmpty() || !tmpExtension.startsWith(".")) {
                throw new IllegalArgumentException("aExtensions is illegal.");
            }
        }

        // </editor-fold>

        this.extensions = aExtensions;
    }

    // </editor-fold>

    //

    // <editor-fold defaultstate="collapsed" desc="Public methods">

    /**
     * Accept
     * 
     * @param tmpFile
     *            File
     * @return True: Accept, false: Otherwise
     */
    public boolean accept(File tmpFile) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (tmpFile == null) {
            return false;
        }

        // </editor-fold>

        if (tmpFile.isDirectory()) {
            return true;
        }
        for (String tmpSingleExtension : this.extensions) {
            if (tmpFile.getName().toLowerCase(Locale.ENGLISH).endsWith(tmpSingleExtension.toLowerCase(Locale.ENGLISH))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns description
     * 
     * @return Description
     */
    public String getDescription() {
        StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_TINY);
        for (String tmpSingleExtension : this.extensions) {
            if (tmpBuffer.length() > 0) {
                tmpBuffer.append(";");
            }
            tmpBuffer.append("*");
            tmpBuffer.append(tmpSingleExtension.toLowerCase(Locale.ENGLISH));
        }
        return tmpBuffer.toString();
    }

    // </editor-fold>

}
