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
package de.gnwi.mfsim.model.peptide.utils;

import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Andreas Truszkowski
 */
public class ResourceLocator {

    private static String RESOURCE_PACKAGE = "/de/gnwi/mfsim/test/resources";

    /**
     * Sets the resource package.
     *
     * @param aResourcePackage New resouce package.
     */
    public static void setResourcePackage(String aResourcePackage) {
        RESOURCE_PACKAGE = aResourcePackage;
    }

    /**
     * Gets the URL of the resource with the given name.
     *
     * @param name Resource name to locate.
     * @return URL of resource.
     */
    public static URL getResourceURL(String name) {
        ResourceWrapper rw = new ResourceWrapper(name, RESOURCE_PACKAGE);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(rw.getNameRelativeToRoot());
    }

    /**
     * Gets an input stream pointing to the resource with given name.
     *
     * @param name Resource name to locate
     * @return InputStream of resource.
     */
    public static InputStream getResourceStream(String name) {
        ResourceWrapper rw = new ResourceWrapper(name, RESOURCE_PACKAGE);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(rw.getNameRelativeToRoot());
    }
}
