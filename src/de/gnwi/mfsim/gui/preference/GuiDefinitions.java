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
package de.gnwi.mfsim.gui.preference;

import java.awt.Color;

/**
 * GUI definitions
 *
 * @author Achim Zielesny
 */
public interface GuiDefinitions {

    // <editor-fold defaultstate="collapsed" desc="Color definitions">
    /**
     * Molecular structure/monomer default cell background color
     */
    Color TABLE_CELL_DEFAULT_MONOMER_STRUCTURE_BACKGROUND_COLOR = new Color(245, 245, 245); // Light grey

    /**
     * Molecular structure/monomer non-editable cell background color
     */
    Color TABLE_CELL_NON_EDITABLE_MONOMER_STRUCTURE_BACKGROUND_COLOR = new Color(245, 245, 245); // Light grey

    /**
     * Standard cell background color
     */
    Color TABLE_CELL_DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    /**
     * Standard cell highlight foreground color
     */
    Color TABLE_CELL_HIGHLIGHT_FOREGROUND_COLOR = Color.BLACK;

    /**
     * Standard cell highlight background color
     */
    Color TABLE_CELL_HIGHLIGHT_BACKGROUND_COLOR = Color.LIGHT_GRAY;

    /**
     * Standard cell foreground color
     */
    Color TABLE_CELL_DEFAULT_FOREGROUND_COLOR = Color.BLACK;

    /**
     * Non-editable cell background color
     */
    Color TABLE_CELL_NON_EDITABLE_BACKGROUND_COLOR = new Color(242, 242, 189); // Nimbus info color

    /**
     * Non-editable cell foreground color
     */
    Color TABLE_CELL_NON_EDITABLE_FOREGROUND_COLOR = Color.BLACK;

    /**
     * Color for panel titles
     */
    Color PANEL_TITLE_COLOR = new Color(0, 128, 255);

    /**
     * Color for correct panel info
     */
    Color PANEL_INFO_CORRECT_COLOR = new Color(0, 128, 255);

    /**
     * Color for wrong panel info
     */
    Color PANEL_INFO_WRONG_COLOR = Color.RED;

    /**
     * Color for correct text field foreground
     */
    Color TEXT_FIELD_FOREGROUND_CORRECT_COLOR = Color.BLACK;

    /**
     * Color for correct text field background
     */
    Color TEXT_FIELD_BACKGROUND_CORRECT_COLOR = Color.WHITE;

    /**
     * Color for wrong text field foreground
     */
    Color TEXT_FIELD_FOREGROUND_WRONG_COLOR = Color.WHITE;

    /**
     * Color for wrong text field background
     */
    Color TEXT_FIELD_BACKGROUND_WRONG_COLOR = Color.RED;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="File import/export related definitions">
    /**
     * Extension of job input archive file
     */
    String JOB_INPUT_ARCHIVE_FILE_EXTENSION = ".zip";

    /**
     * Extension of job input archive file
     */
    String JOB_RESULT_ARCHIVE_FILE_EXTENSION = ".zip";

    /**
     * PDB file extension
     */
    String PDB_FILE_EXTENSION = ".pdb";

    /**
     * XML file extension
     */
    String XML_FILE_EXTENSION = ".xml";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="BMP image related definitions">
    /**
     * Graphics file type
     */
    String IMAGE_BMP_TYPE = "bmp";

    /**
     * Extension of graphics file. NOTE: This extension MUST correspond to
     * GRAPHICS_TYPE.
     */
    String IMAGE_BMP_FILE_EXTENSION = ".bmp";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Resource related definitions">
    // <editor-fold defaultstate="collapsed" desc="- Resource location">
    /**
     * Resource location (absolute)
     */
    String RESOURCE_LOCATION = "/de/gnwi/mfsim/gui/resource/";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Resource image ending">
    /**
     * JPEG resource: Ending of JPEG filename
     */
    String RESOURCE_IMAGE_ENDING = ".jpg";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle images">
    /**
     * Particle image filename prefix
     */
    String PARTICLE_IMAGE_FILENAME_PREFIX = "Fragment_";
    /**
     * Particle symbol filename
     */
    String PARTICLE_SYMBOL_FILENAME = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + "Symbol" + GuiDefinitions.RESOURCE_IMAGE_ENDING;
    /**
     * Particle monomer filename
     */
    String PARTICLE_MONOMER_FILENAME = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + "Monomer" + GuiDefinitions.RESOURCE_IMAGE_ENDING;
    /**
     * Particle "particle" filename
     */
    String PARTICLE_PARTICLE_FILENAME = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + "Particle" + GuiDefinitions.RESOURCE_IMAGE_ENDING;
    /**
     * Particle "Empty" filename
     */
    String PARTICLE_EMPTY_FILENAME = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + "Empty" + GuiDefinitions.RESOURCE_IMAGE_ENDING;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Amino acid images">
    /**
     * Amino acid image filename prefix
     */
    String AMINO_ACID_IMAGE_FILENAME_PREFIX = "AminoAcid_";
    /**
     * Amino acid symbol filename
     */
    String AMINO_ACID_SYMBOL_FILENAME = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + "Symbol" + GuiDefinitions.RESOURCE_IMAGE_ENDING;
    /**
     * Amino acid "AminoAcid" filename
     */
    String AMINO_ACID_AMINO_ACID_FILENAME = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + "AminoAcid" + GuiDefinitions.RESOURCE_IMAGE_ENDING;
    /**
     * Amino acid empty filename
     */
    String AMINO_ACID_EMPTY_FILENAME = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + "Empty" + GuiDefinitions.RESOURCE_IMAGE_ENDING;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous image filename prefixes">
    /**
     * JPEG resource: Main frame home image filename
     */
    String HOME_IMAGE_FILENAME_PREFIX = "MFsimHome";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Compartments image filename prefixes">
    /**
     * JPEG resource: Compartments image filename
     */
    String COMPARTMENTS_IMAGE_FILENAME_PREFIX = "MFsimCompartments";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Simulation box image filename prefixes">
    /**
     * JPEG resource: Simulation box image filename
     */
    String SIMULATION_BOX_IMAGE_FILENAME_PREFIX = "MFsimSimulationBox";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Simulation movie image filename prefixes">
    /**
     * JPEG resource: Simulation movie image filename
     */
    String SIMULATION_MOVIE_IMAGE_FILENAME_PREFIX = "MFsimSimulationMovie";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle distribution movie image filename prefixes">
    /**
     * JPEG resource: Particle distribution movie image filename
     */
    String PARTICLE_DISTRIBUTION_MOVIE_IMAGE_FILENAME_PREFIX = "MFsimDistributionMovie";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Simulation box view image filenames">
    /**
     * JPEG resource: Simulation box top view image filename
     */
    String BOX_VIEW_TOP_IMAGE_FILENAME = "MFsimBoxViewTop.jpg";
    /**
     * JPEG resource: Simulation box bottom view image filename
     */
    String BOX_VIEW_BOTTOM_IMAGE_FILENAME = "MFsimBoxViewBottom.jpg";
    /**
     * JPEG resource: Simulation box left view image filename
     */
    String BOX_VIEW_LEFT_IMAGE_FILENAME = "MFsimBoxViewLeft.jpg";
    /**
     * JPEG resource: Simulation box right view image filename
     */
    String BOX_VIEW_RIGHT_IMAGE_FILENAME = "MFsimBoxViewRight.jpg";
    /**
     * JPEG resource: Simulation box front view image filename
     */
    String BOX_VIEW_FRONT_IMAGE_FILENAME = "MFsimBoxViewFront.jpg";
    /**
     * JPEG resource: Simulation box back view image filename
     */
    String BOX_VIEW_BACK_IMAGE_FILENAME = "MFsimBoxViewBack.jpg";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Icon filename">
    /**
     * JPEG resource: Icon image filename
     */
    String ICON_IMAGE_FILENAME = "MFsimIcon.jpg";
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Default value definitions">
    /**
     * Minimum number of steps for job restart
     */
    int MINIMUM_NUMBER_OF_STEPS_FOR_JOB_RESTART = 10;

    /**
     * Maximum number of steps for job restart
     */
    int MAXIMUM_NUMBER_OF_ADDITIONAL_STEPS_FOR_JOB_RESTART = 1000000000;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Table definitions">
    /**
     * Table row height
     */
    int TABLE_ROW_HEIGHT = 30;

    /**
     * Table row margin
     */
    int TABLE_ROW_MARGIN = 2;

    /**
     * Table header height
     */
    int TABLE_HEADER_HEIGHT = 30;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Miscellaneous">
    /**
     * Reduction factor for maximum size of windows (frames and dialogs)
     */
    double WINDOW_MAXIMUM_SIZE_REDUCTION_FACTOR = 1.0;

    /**
     * Reduction factor for maximum size of main frame
     */
    double MAIN_FRAME_MAXIMUM_SIZE_REDUCTION_FACTOR = 0.9;

    /**
     * Delay for progress control
     */
    int PROGRESS_CONTROL_DELAY = 100;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="URLs">
    /**
     * CAM-D URL
     */
    String CAMD_URL = "http://www.molecular-dynamics.de/";
    /**
     * GNWI URL
     */
    String GNWI_URL = "http://www.gnwi.de/";
    // </editor-fold>

}
