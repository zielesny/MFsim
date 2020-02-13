/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
package de.gnwi.mfsim.model.peptide.utils;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Abstract masterdata.
 *
 * @author Andreas Truszkowski
 */
public abstract class AbstractMasterdata extends HashMap<String, String> {

    /**
     * Tag.
     */
    protected static String TAG;

    /**
     * Version.
     */
    protected static final String VERSION = "VERSION";

    /**
     * Data stream.
     */
    private ByteArrayOutputStream outStream = null;
    
    /**
     * XML event writer.
     */
    private XMLEventWriter eventWriter = null;

    /**
     * Creates a new instance.
     *
     * @param aXmlMasterdata Masterdata XML
     * @param aTag Tag
     * @throws DpdPeptideException DpdPeptideException
     */
    public AbstractMasterdata(String aXmlMasterdata, String aTag) throws DpdPeptideException {
        try {
            TAG = aTag;
            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            XMLEventReader eventReader = inputFactory.createXMLEventReader(new StringReader(aXmlMasterdata));
            // Read the XML document
            String tmpKey;
            StringBuffer tmpData = null;
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    tmpData = null;
                }
                if (event.isCharacters()) {
                    if (tmpData == null) {
                        tmpData = new StringBuffer();
                    }
                    tmpData.append(event.asCharacters().getData());
                }
                if (event.isEndElement()) {
                    tmpKey = event.asEndElement().getName().toString();
                    if (!tmpKey.equals(PdbToDpdMasterdata.TAG)) {
                        this.put(tmpKey, tmpData.toString());
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new DpdPeptideException(ModelMessage.get("Peptide.ErrorReadMasterdata"), anException);
        }
    }

    /**
     * Creates a new instance.
     *
     * @param aTag Tag.
     */
    public AbstractMasterdata(String aTag) {
        TAG = aTag;
    }

    /**
     * Creates a new node.
     *
     * @param name Name of the data node.
     * @param value Corresponding string value.
     * @throws XMLStreamException
     */
    private void createNode(String name, String value) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        // Create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        this.eventWriter.add(sElement);
        // Create Content
        Characters characters = eventFactory.createCharacters(value);
        this.eventWriter.add(characters);
        // Create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        this.eventWriter.add(eElement);
        this.eventWriter.add(end);

    }

    /**
     * Generates the masterdata XML string.
     *
     * @return XML masterdata string
     * @throws XMLStreamException XMLStreamException
     */
    public String toXml() throws XMLStreamException {
        //this.tag = aTag;// Create a XMLOutputFactory
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        // Create XMLEventWriter
        this.outStream = new ByteArrayOutputStream();
        this.eventWriter = outputFactory.createXMLEventWriter(this.outStream);
        // Create a EventFactory
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\r\n");
        // Create and write Start Tag
        //    StartDocument startDocument = eventFactory.createStartDocument();
        //    eventWriter.add(startDocument);
        // Create open tag
        StartElement startElement = eventFactory.createStartElement("", "", PdbToDpdMasterdata.TAG);
        eventWriter.add(startElement);
        eventWriter.add(end);
        // Get alphabetical sorted key list
        ArrayList<String> tmpSortedKeys = new ArrayList<String>();
        for (Map.Entry<String, String> tmpEntry : this.entrySet()) {
            tmpSortedKeys.add(tmpEntry.getKey());
        }
        Collections.sort(tmpSortedKeys);
        // Write data. Non-string objects are ignored.
        for (String tmpKey : tmpSortedKeys) {
            this.createNode(tmpKey, this.get(tmpKey));
        }
        // Create end tag
        eventWriter.add(eventFactory.createEndElement("", "", PdbToDpdMasterdata.TAG));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
        String tmpXML = this.outStream.toString();
        // NOTE: The following replaceAll is NOT in a loop so a priori compilation is NOT necessary
        return tmpXML.replaceAll("\\r\\n|\\r|\\n", "\r\n");
    }
    
}
