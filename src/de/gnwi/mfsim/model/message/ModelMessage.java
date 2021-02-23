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
package de.gnwi.mfsim.model.message;

import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Model messages
 *
 * @author Achim Zielesny
 */
public class ModelMessage {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Resource bundle name
     */
    private static final String BUNDLE_NAME = "de.gnwi.mfsim.model.message.ModelMessages";
    /**
     * Resource bundle
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private Constructor">
    /**
     * Private constructor
     */
    private ModelMessage() {
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Return resource string for key
     *
     * @param aKey Key
     * @return Resource string for key
     */
    public static String get(String aKey) {
        try {
            return RESOURCE_BUNDLE.getString(aKey).trim();
        } catch (MissingResourceException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return "Key '" + aKey + "' not found.";
        }
    }
    // </editor-fold>

}
