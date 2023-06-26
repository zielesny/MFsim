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
package de.gnwi.mfsim.model.jmolViewer.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;

/**
 * Data provider for DPD result Zip files.
 *
 * @author Andreas Truszkowski
 */
public class JmolDpdResultFileDataProvider implements IDataProvider {

    /**
     * Array containing simulation step numbers.
     */
    private ArrayList<Integer> steps = null;
    /**
     * Hashmap mapping step number and zipEntry.
     */
    private HashMap<Integer, ZipEntry> stepZipEntryMap = null;
    /**
     * Dpd Zip file handle.
     */
    private ZipFile zipFile = null;

    /**
     * Creates a new instance.
     *
     * @param aDpdResultFile Dpd result Zip file.
     * @throws ZipException ZipException
     * @throws IOException IOException
     */
    public JmolDpdResultFileDataProvider(File aDpdResultFile) throws ZipException, IOException {
        this.steps = new ArrayList<Integer>();
        this.stepZipEntryMap = new HashMap<Integer, ZipEntry>();
        this.zipFile = new ZipFile(aDpdResultFile);
        Enumeration<? extends ZipEntry> tmpEntries = this.zipFile.entries();
        ZipEntry tmpEntry;
        while (tmpEntries.hasMoreElements()) {
            tmpEntry = tmpEntries.nextElement();
            if (!tmpEntry.isDirectory() && tmpEntry.getName().contains("\\Steps\\")) {
                String[] nameParts = tmpEntry.getName().split("PP");
                // Old code:
                // int tmpStep = Integer.parseInt(nameParts[1].replaceAll(".gz", ""));
                int tmpStep = Integer.parseInt(StringUtils.replace(nameParts[1], ".gz", ""));
                this.steps.add(tmpStep);
                this.stepZipEntryMap.put(tmpStep, tmpEntry);
            }
        }
        Collections.sort(this.steps);
    }

    @Override
    public InputStream getNextWork(int aStep) throws Exception {
        if(aStep < 0 || aStep >= this.steps.size()) {
            throw new IndexOutOfBoundsException(String.format("Step %d not available. Only %d steps read.", aStep, this.steps.size()));
        }
        ZipEntry tmpEntry = this.stepZipEntryMap.get(this.steps.get(aStep));
        InputStream tmpDataStream = this.zipFile.getInputStream(tmpEntry);
        GZIPInputStream tmpGzipStream = new GZIPInputStream(tmpDataStream);
        return tmpGzipStream;
    }
}
