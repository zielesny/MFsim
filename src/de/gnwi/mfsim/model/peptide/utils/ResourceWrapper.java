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
package de.gnwi.mfsim.model.peptide.utils;
/**
 *
 * @author Andreas Truszkowski
 */

class ResourceWrapper
{
    /** file separator string */
    public final String SEP = "/";
    /** file separator character */
    public final char SEPC = '/';
    /** file name */
    final private String fileName;
    /** absolute path in package tree with leading file separator */
    final private String absPath;
    /** absolute path in package tree with NO leading file separator */
    final private String relToRootPath;

    /**
     * @param fileName file name
     * @param absPath absolute path of the resource
     */
    public ResourceWrapper(String fileName, String absPath)
    {
        this.fileName = fileName.trim();
        String path = absPath.trim().replace('/', SEPC).replace('\\', SEPC);
        if (path.length() == 0 || (path.charAt(0) != SEPC))
        {
            path = SEPC + path;
        }
        if (path.length() > 1 && path.charAt(path.length() - 1) == SEPC)
        {
            path.substring(0, path.length() - 2);
        }

        this.absPath = path;
        this.relToRootPath = this.absPath.substring(1);
    }

    /**
     * @return  the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @return resource path relative to the root
     */
    public String getNameRelativeToRoot()
    {
        if (absPath.equals(SEP))
        {
            return this.fileName;
        }
        return relToRootPath.concat(SEP).concat(this.fileName);
    }

    /**
     * @return absolute resource path
     */
    public String getAbsoluteName()
    {
        if (absPath.equals(SEP))
        {
            return absPath.concat(this.fileName);
        }

        return absPath.concat(SEP).concat(this.fileName);
    }

    /**
     * @param c class
     * @return relative path
     */
    public String getNameRelativeTo(Class<?> c)
    {
        String pkPath = c.getPackage().getName().replace('.', SEPC);

        String regexp = "[/\\\\]";
        String[] relToRootPathElems = this.relToRootPath.split(regexp);
        String[] pkPathElems = pkPath.split(regexp);

        int identicalLevels = 0;
        for (int i = 0; i < relToRootPathElems.length && i < pkPathElems.length; i++)
        {
            if (relToRootPathElems[i].equals(pkPathElems[i]))
            {
                identicalLevels++;
            }
            else
            {
                break;
            }
        }

        StringBuffer relPath = new StringBuffer();
        // append the return levels
        for (int i = identicalLevels; i < pkPathElems.length; i++)
        {
            relPath.append("..").append(SEP);
        }
        // append the resource branch
        for (int i = identicalLevels; i < relToRootPathElems.length; i++)
        {
            if (relToRootPathElems[i].equals(""))
            {
                continue;
            }
            relPath.append(relToRootPathElems[i]).append(SEP);
        }

        // add the file name
        relPath.append(this.fileName);
        return relPath.toString();
    }
}

