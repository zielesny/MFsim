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

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Defines standard colors
 *
 * @author Achim Zielesny
 */
public enum StandardColorEnum {

    // <editor-fold defaultstate="collapsed" desc="Definitions">
    /**
     * BLACK
     */
    BLACK,
    /**
     * WHITE
     */
    WHITE,
    /**
     * RED
     */
    RED,
    /**
     * GREEN
     */
    GREEN,
    /**
     * BLUE
     */
    BLUE,
    /**
     * YELLOW
     */
    YELLOW,
    /**
     * MAGENTA
     */
    MAGENTA,
    /**
     * CYAN
     */
    CYAN,
    /**
     * VIOLET (same color as PURPLE)
     */
    VIOLET,
    /**
     * PURPLE (same color as VIOLET)
     */
    PURPLE,
    /**
     * GREY
     */
    GREY,
    /**
     * GREYdim
     */
    GREYdim,
    /**
     * BEIGE
     */
    BEIGE,
    /**
     * ORANGE
     */
    ORANGE,
    /**
     * PINK
     */
    PINK,
    /**
     * MINT
     */
    MINT,
    /**
     * INDIGO
     */
    INDIGO,
    /**
     * GOLD
     */
    GOLD,
    /**
     * OLIVE
     */
    OLIVE,
    /**
     * COBALT
     */
    COBALT,
    /**
     * BROWN
     */
    BROWN,
    /**
     * PLUM
     */
    PLUM,
    /**
     * BANANA
     */
    BANANA,
    /**
     * CARROT
     */
    CARROT,
    /**
     * GUI
     */
    GUI,
    /**
     * Undefined
     */
    UNDEFINED;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Map of representation to enum value
     */
    private static HashMap<String, StandardColorEnum> representationToStandardColorEnumMap;

    /**
     * Colors
     */
    private static final Color BLACK_Color = new Color(0.0f, 0.0f, 0.0f);

    private static final Color WHITE_Color = new Color(1.0f, 1.0f, 1.0f);

    private static final Color RED_Color = new Color(1.0f, 0.0f, 0.0f);

    private static final Color GREEN_Color = new Color(0.0f, 1.0f, 0.0f);

    private static final Color BLUE_Color = new Color(0.0f, 0.0f, 1.0f);

    private static final Color YELLOW_Color = new Color(1.0f, 1.0f, 0.0f);

    private static final Color MAGENTA_Color = new Color(1.0f, 0.0f, 1.0f);

    private static final Color CYAN_Color = new Color(0.0f, 1.0f, 1.0f);

    private static final Color VIOLET_Color = new Color(0.56f, 0.37f, 0.6f);

    private static final Color PURPLE_Color = new Color(0.56f, 0.37f, 0.6f);

    private static final Color GREY_Color = new Color(0.7529f, 0.7529f, 0.7529f);

    private static final Color GREYdim_Color = new Color(0.3294f, 0.3294f, 0.3294f);

    private static final Color BEIGE_Color = new Color(0.64f, 0.58f, 0.5f);

    private static final Color ORANGE_Color = new Color(1.0f, 0.5f, 0.0f);

    private static final Color PINK_Color = new Color(1.0f, 0.7529f, 0.7961f);

    private static final Color MINT_Color = new Color(0.74f, 0.99f, 0.79f);

    private static final Color INDIGO_Color = new Color(0.03f, 0.18f, 0.33f);

    private static final Color GOLD_Color = new Color(1.0f, 0.8431f, 0.0f);

    private static final Color OLIVE_Color = new Color(0.23f, 0.37f, 0.17f);

    private static final Color COBALT_Color = new Color(0.24f, 0.35f, 0.67f);

    private static final Color BROWN_Color = new Color(0.5f, 0.1647f, 0.1647f);

    private static final Color PLUM_Color = new Color(0.8667f, 0.6275f, 0.8667f);

    private static final Color BANANA_Color = new Color(0.89f, 0.81f, 0.34f);

    private static final Color CARROT_Color = new Color(0.93f, 0.57f, 0.13f);

    private static final Color GUI_Color = new Color(0.839f, 0.851f, 0.875f);

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="getAllColorRepresentations()">
    /**
     * Returns all standard color string representations
     *
     * @return All standard color string representations
     */
    public static String[] getAllColorRepresentations() {
        String[] tmpAllColorRepresentations = new String[]{StandardColorEnum.GUI.toString(), StandardColorEnum.BLACK.toString(), StandardColorEnum.WHITE.toString(), StandardColorEnum.RED.toString(),
            StandardColorEnum.GREEN.toString(), StandardColorEnum.BLUE.toString(),
            StandardColorEnum.YELLOW.toString(), StandardColorEnum.MAGENTA.toString(), StandardColorEnum.CYAN.toString(), StandardColorEnum.VIOLET.toString(), StandardColorEnum.PURPLE.toString(),
            StandardColorEnum.GREY.toString(), StandardColorEnum.GREYdim.toString(), StandardColorEnum.BEIGE.toString(), StandardColorEnum.ORANGE.toString(), StandardColorEnum.PINK.toString(),
            StandardColorEnum.MINT.toString(),
            StandardColorEnum.INDIGO.toString(), StandardColorEnum.GOLD.toString(), StandardColorEnum.OLIVE.toString(), StandardColorEnum.COBALT.toString(), StandardColorEnum.BROWN.toString(),
            StandardColorEnum.PLUM.toString(), StandardColorEnum.BANANA.toString(), StandardColorEnum.CARROT.toString()};
        Arrays.sort(tmpAllColorRepresentations);
        return tmpAllColorRepresentations;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="isStandardColor">
    /**
     * Returns if aRepresentation represents a valid standard color
     *
     * @param aRepresentation Color representation
     * @return True: aRepresentation represents a valid standard color, false:
     * Otherwise
     */
    public static boolean isStandardColor(String aRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Initialize map if necessary">
        StandardColorEnum.initializeMap();

        // </editor-fold>
        return StandardColorEnum.representationToStandardColorEnumMap.containsKey(aRepresentation);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="toColor()">
    /**
     * Returns color
     *
     * @return Color or null if not defined
     */
    public Color toColor() {
        switch (this) {
            case BLACK:
                return new Color(0.0f, 0.0f, 0.0f);
            case WHITE:
                return new Color(1.0f, 1.0f, 1.0f);
            case RED:
                return new Color(1.0f, 0.0f, 0.0f);
            case GREEN:
                return new Color(0.0f, 1.0f, 0.0f);
            case BLUE:
                return new Color(0.0f, 0.0f, 1.0f);
            case YELLOW:
                return new Color(1.0f, 1.0f, 0.0f);
            case MAGENTA:
                return new Color(1.0f, 0.0f, 1.0f);
            case CYAN:
                return new Color(0.0f, 1.0f, 1.0f);
            case VIOLET:
                return new Color(0.56f, 0.37f, 0.6f);
            case PURPLE:
                return new Color(0.56f, 0.37f, 0.6f);
            case GREY:
                return new Color(0.7529f, 0.7529f, 0.7529f);
            case GREYdim:
                return new Color(0.3294f, 0.3294f, 0.3294f);
            case BEIGE:
                return new Color(0.64f, 0.58f, 0.5f);
            case ORANGE:
                return new Color(1.0f, 0.5f, 0.0f);
            case PINK:
                return new Color(1.0f, 0.7529f, 0.7961f);
            case MINT:
                return new Color(0.74f, 0.99f, 0.79f);
            case INDIGO:
                return new Color(0.03f, 0.18f, 0.33f);
            case GOLD:
                return new Color(1.0f, 0.8431f, 0.0f);
            case OLIVE:
                return new Color(0.23f, 0.37f, 0.17f);
            case COBALT:
                return new Color(0.24f, 0.35f, 0.67f);
            case BROWN:
                return new Color(0.5f, 0.1647f, 0.1647f);
            case PLUM:
                return new Color(0.8667f, 0.6275f, 0.8667f);
            case BANANA:
                return new Color(0.89f, 0.81f, 0.34f);
            case CARROT:
                return new Color(0.93f, 0.57f, 0.13f);
            case GUI:
                return new Color(0.839f, 0.851f, 0.875f);
            case UNDEFINED:
                return null;
            default:
                return null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="toForegroundColor()">
    /**
     * Returns (inverse) foreground color for color
     *
     * @return (Inverse) Foreground color or null if not defined
     */
    public Color toForegroundColor() {
        switch (this) {
            case BLACK:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case WHITE:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case RED:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case GREEN:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case BLUE:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case YELLOW:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case MAGENTA:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case CYAN:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case VIOLET:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case PURPLE:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case GREY:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case GREYdim:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case BEIGE:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case ORANGE:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case PINK:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case MINT:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case INDIGO:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case GOLD:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case OLIVE:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case COBALT:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case BROWN:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case PLUM:
                // White
                return new Color(1.0f, 1.0f, 1.0f);
            case BANANA:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case CARROT:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case GUI:
                // Black
                return new Color(0.0f, 0.0f, 0.0f);
            case UNDEFINED:
                return null;
            default:
                return null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="toStandardColor()">
    /**
     * Returns StandardColorEnum for color representation
     *
     * @param aRepresentation Color representation
     * @return StandardColorEnum for string representation or
     * StandardColorEnum.UNDEFINED if representation is not known
     */
    public static StandardColorEnum toStandardColor(String aRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Initialize map if necessary">
        StandardColorEnum.initializeMap();

        // </editor-fold>
        if (StandardColorEnum.representationToStandardColorEnumMap.containsKey(aRepresentation)) {
            return StandardColorEnum.representationToStandardColorEnumMap.get(aRepresentation);
        } else {
            return StandardColorEnum.UNDEFINED;
        }
    }

    /**
     * Returns StandardColorEnum for color
     *
     * @param aColor Color
     * @return StandardColorEnum for color or StandardColorEnum.UNDEFINED if
     * color is no standard color
     */
    public static StandardColorEnum toStandardColor(Color aColor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColor == null) {
            return StandardColorEnum.UNDEFINED;
        }

        // </editor-fold>
        if (aColor.getRGB() == StandardColorEnum.BLACK_Color.getRGB()) {
            return StandardColorEnum.BLACK;
        }
        if (aColor.getRGB() == StandardColorEnum.WHITE_Color.getRGB()) {
            return StandardColorEnum.WHITE;
        }
        if (aColor.getRGB() == StandardColorEnum.RED_Color.getRGB()) {
            return StandardColorEnum.RED;
        }
        if (aColor.getRGB() == StandardColorEnum.GREEN_Color.getRGB()) {
            return StandardColorEnum.GREEN;
        }
        if (aColor.getRGB() == StandardColorEnum.BLUE_Color.getRGB()) {
            return StandardColorEnum.BLUE;
        }
        if (aColor.getRGB() == StandardColorEnum.YELLOW_Color.getRGB()) {
            return StandardColorEnum.YELLOW;
        }
        if (aColor.getRGB() == StandardColorEnum.MAGENTA_Color.getRGB()) {
            return StandardColorEnum.MAGENTA;
        }
        if (aColor.getRGB() == StandardColorEnum.CYAN_Color.getRGB()) {
            return StandardColorEnum.CYAN;
        }
        if (aColor.getRGB() == StandardColorEnum.VIOLET_Color.getRGB()) {
            return StandardColorEnum.VIOLET;
        }
        if (aColor.getRGB() == StandardColorEnum.PURPLE_Color.getRGB()) {
            return StandardColorEnum.PURPLE;
        }
        if (aColor.getRGB() == StandardColorEnum.GREY_Color.getRGB()) {
            return StandardColorEnum.GREY;
        }
        if (aColor.getRGB() == StandardColorEnum.GREYdim_Color.getRGB()) {
            return StandardColorEnum.GREYdim;
        }
        if (aColor.getRGB() == StandardColorEnum.BEIGE_Color.getRGB()) {
            return StandardColorEnum.BEIGE;
        }
        if (aColor.getRGB() == StandardColorEnum.ORANGE_Color.getRGB()) {
            return StandardColorEnum.ORANGE;
        }
        if (aColor.getRGB() == StandardColorEnum.PINK_Color.getRGB()) {
            return StandardColorEnum.PINK;
        }
        if (aColor.getRGB() == StandardColorEnum.MINT_Color.getRGB()) {
            return StandardColorEnum.MINT;
        }
        if (aColor.getRGB() == StandardColorEnum.INDIGO_Color.getRGB()) {
            return StandardColorEnum.INDIGO;
        }
        if (aColor.getRGB() == StandardColorEnum.GOLD_Color.getRGB()) {
            return StandardColorEnum.GOLD;
        }
        if (aColor.getRGB() == StandardColorEnum.OLIVE_Color.getRGB()) {
            return StandardColorEnum.OLIVE;
        }
        if (aColor.getRGB() == StandardColorEnum.COBALT_Color.getRGB()) {
            return StandardColorEnum.COBALT;
        }
        if (aColor.getRGB() == StandardColorEnum.BROWN_Color.getRGB()) {
            return StandardColorEnum.BROWN;
        }
        if (aColor.getRGB() == StandardColorEnum.PLUM_Color.getRGB()) {
            return StandardColorEnum.PLUM;
        }
        if (aColor.getRGB() == StandardColorEnum.BANANA_Color.getRGB()) {
            return StandardColorEnum.BANANA;
        }
        if (aColor.getRGB() == StandardColorEnum.CARROT_Color.getRGB()) {
            return StandardColorEnum.CARROT;
        }
        if (aColor.getRGB() == StandardColorEnum.GUI_Color.getRGB()) {
            return StandardColorEnum.GUI;
        }
        return StandardColorEnum.UNDEFINED;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialises StandardColorEnum.representationToStandardColorEnumMap if
     * necessary
     */
    private static void initializeMap() {
        // Initialize if necessary
        if (StandardColorEnum.representationToStandardColorEnumMap == null) {
            StandardColorEnum.representationToStandardColorEnumMap = new HashMap<String, StandardColorEnum>(50);

            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.BLACK.toString(), StandardColorEnum.BLACK);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.WHITE.toString(), StandardColorEnum.WHITE);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.RED.toString(), StandardColorEnum.RED);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.GREEN.toString(), StandardColorEnum.GREEN);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.BLUE.toString(), StandardColorEnum.BLUE);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.YELLOW.toString(), StandardColorEnum.YELLOW);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.MAGENTA.toString(), StandardColorEnum.MAGENTA);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.CYAN.toString(), StandardColorEnum.CYAN);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.VIOLET.toString(), StandardColorEnum.VIOLET);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.PURPLE.toString(), StandardColorEnum.PURPLE);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.GREY.toString(), StandardColorEnum.GREY);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.GREYdim.toString(), StandardColorEnum.GREYdim);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.BEIGE.toString(), StandardColorEnum.BEIGE);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.ORANGE.toString(), StandardColorEnum.ORANGE);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.PINK.toString(), StandardColorEnum.PINK);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.MINT.toString(), StandardColorEnum.MINT);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.INDIGO.toString(), StandardColorEnum.INDIGO);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.GOLD.toString(), StandardColorEnum.GOLD);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.OLIVE.toString(), StandardColorEnum.OLIVE);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.COBALT.toString(), StandardColorEnum.COBALT);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.BROWN.toString(), StandardColorEnum.BROWN);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.PLUM.toString(), StandardColorEnum.PLUM);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.BANANA.toString(), StandardColorEnum.BANANA);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.CARROT.toString(), StandardColorEnum.CARROT);
            StandardColorEnum.representationToStandardColorEnumMap.put(StandardColorEnum.GUI.toString(), StandardColorEnum.GUI);
        }
    }
    // </editor-fold>

}
