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
package de.gnwi.mfsim.gui.control;

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentBox;
import de.gnwi.mfsim.model.graphics.shape.ShapeBox;
import de.gnwi.mfsim.model.graphics.shape.ShapeCircle;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.util.LinkedList;
import javax.swing.JPanel;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Panel for fast display of image
 *
 * @author Achim Zielesny
 */
public class CustomPanelSimulationBoxGraphics extends JPanel implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    
    /**
     * Utility graphics methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();
    /**
     * Fractions
     */
    private float[] fractions;
    /**
     * Colors
     */
    private Color[] colors;
    /**
     * Radius magnification
     */
    private float radiusMagnification;
    /**
     * Focus factor
     */
    private float focusFactor;
    /**
     * Compartment box
     */
    private CompartmentBox compartmentBox;
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000035L;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelSimulationBoxGraphics() {
        super();

        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        

        this.compartmentBox = null;
        this.colors = new Color[3];
        this.fractions = new float[]{0.0f, 0.1f, 1.0f};
        this.radiusMagnification = 1.5f;
        this.focusFactor = 0.7f;

        
        // </editor-fold>

    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a change receiver
     *
     * @param aChangeInfo Change information
     * @param aChangeNotifier Object that notifies change
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeNotifier == this.compartmentBox) {
            this.repaint();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="paintComponent method">
    /**
     * Paint component
     *
     * @param g Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.compartmentBox != null) {
            Graphics2D tmpGraphics2D = (Graphics2D) g;
            // <editor-fold defaultstate="collapsed" desc="Paint background">
            tmpGraphics2D.setColor(ModelDefinitions.COLOR_SIMULATION_BOX_BACKGROUND);
            tmpGraphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Paint body shapes">
            LinkedList<BodyInterface> tmpBodyList = this.compartmentBox.getBodiesForDisplay();
            if (tmpBodyList != null) {
                float tmpX;
                float tmpY;
                int tmpWidth;
                int tmpHeight;
                Color[] tmpAttenuatedColors;
                for (BodyInterface tmpBody : tmpBodyList) {
                    if (tmpBody.getVolume() > 0) {
                        switch (tmpBody.getShapeInBoxView().getShapeType()) {
                            case CIRCLE:
                                // <editor-fold defaultstate="collapsed" desc="Circle shape">
                                ShapeCircle tmpShapeCircle = (ShapeCircle) tmpBody.getShapeInBoxView();
                                tmpX = (float) tmpShapeCircle.getCorrectedXRatio() * (float) this.getWidth();
                                tmpY = (float) tmpShapeCircle.getCorrectedYRatio() * (float) this.getHeight();
                                float tmpRadius = (float) tmpShapeCircle.getCorrectedRadiusXRatio() * (float) this.getWidth();
                                tmpWidth = (int) (2.0f * tmpRadius);
                                if (tmpBody.isSelected()) {
                                    // <editor-fold defaultstate="collapsed" desc="Sphere is selected">
                                    this.graphicsUtilityMethods.setGradientColorsForCompartments(ModelDefinitions.COLOR_GRADIENT_SELECTED_COMPARTMENT, this.colors);
                                    // </editor-fold>
                                } else {
                                    // <editor-fold defaultstate="collapsed" desc="Sphere is not selected">
                                    this.graphicsUtilityMethods.setGradientColorsForCompartments(ModelDefinitions.COLOR_GRADIENT_SPHERE, this.colors);
                                    // </editor-fold>
                                }
                                RadialGradientPaint tmpRadialGradientPaint = new RadialGradientPaint(
                                        tmpX + tmpRadius * this.focusFactor,
                                        tmpY + tmpRadius * this.focusFactor,
                                        tmpRadius * this.radiusMagnification,
                                        this.fractions,
                                        this.graphicsUtilityMethods.getAttenuatedColors(this.colors, Color.black, tmpShapeCircle.getAttenuation()));
                                tmpGraphics2D.setPaint(tmpRadialGradientPaint);
                                // NOTE: ColorTransparency is defined to be 0 for opaque bodies and 1 for fully transparent bodies: Convert!
                                tmpGraphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - Preferences.getInstance().getColorTransparencyCompartment()));
                                tmpGraphics2D.fillOval((int) tmpX, (int) tmpY, tmpWidth, tmpWidth);
                                // </editor-fold>
                                break;
                            case BOX:
                                // <editor-fold defaultstate="collapsed" desc="Box shape">
                                ShapeBox tmpShapeBox = (ShapeBox) tmpBody.getShapeInBoxView();
                                tmpX = (float) tmpShapeBox.getCorrectedXRatio() * (float) this.getWidth();
                                tmpY = (float) tmpShapeBox.getCorrectedYRatio() * (float) this.getHeight();
                                float tmpHalfXLength = (float) tmpShapeBox.getCorrectedXLengthRatio() * (float) this.getWidth() / 2.0f;
                                tmpWidth = (int) (2.0f * tmpHalfXLength);
                                float tmpHalfYLength = (float) tmpShapeBox.getCorrectedYLengthRatio() * (float) this.getHeight() / 2.0f;
                                tmpHeight = (int) (2.0f * tmpHalfYLength);
                                if (tmpBody.isSelected()) {
                                    // <editor-fold defaultstate="collapsed" desc="xy-Layer is selected">
                                    this.graphicsUtilityMethods.setGradientColorsForCompartments(ModelDefinitions.COLOR_GRADIENT_SELECTED_COMPARTMENT, this.colors);
                                    // </editor-fold>
                                } else {
                                    // <editor-fold defaultstate="collapsed" desc="xy-Layer is not selected">
                                    this.graphicsUtilityMethods.setGradientColorsForCompartments(ModelDefinitions.COLOR_GRADIENT_XY_LAYER, this.colors);
                                    // </editor-fold>
                                }
                                tmpAttenuatedColors = this.graphicsUtilityMethods.getAttenuatedColors(this.colors, Color.black, tmpShapeBox.getAttenuation());
                                // Upper half box
                                GradientPaint tmpGradientPaintTop = new GradientPaint(tmpX, tmpY - tmpHalfYLength, tmpAttenuatedColors[0], tmpX, tmpY + tmpHalfYLength, tmpAttenuatedColors[1], false);
                                tmpGraphics2D.setPaint(tmpGradientPaintTop);
                                // NOTE: ColorTransparency is defined to be 0 for opaque bodies and 1 for fully transparent bodies: Convert!
                                tmpGraphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - Preferences.getInstance().getColorTransparencyCompartment()));
                                tmpGraphics2D.fillRect((int) tmpX, (int) tmpY, tmpWidth, tmpHeight / 2);
                                // Lower half box
                                GradientPaint tmpGradientPaintBottom = new GradientPaint(tmpX, tmpY + 3.0f * tmpHalfYLength, tmpAttenuatedColors[0], tmpX, tmpY + tmpHalfYLength,
                                        tmpAttenuatedColors[1], false);
                                tmpGraphics2D.setPaint(tmpGradientPaintBottom);
                                tmpGraphics2D.fillRect((int) tmpX, (int) tmpY + tmpHeight / 2, tmpWidth, tmpHeight / 2);
                                // </editor-fold>
                                break;
                        }
                    }
                }
            }

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    
    // <editor-fold defaultstate="collapsed" desc="CompartmentBox">
    /**
     * Compartment box
     *
     * @return Compartment box
     */
    public CompartmentBox getCompartmentBox() {
        return this.compartmentBox;
    }

    /**
     * Compartment box
     *
     * @param aCompartmentBox Compartment box
     */
    public void setCompartmentBox(CompartmentBox aCompartmentBox) {
        this.compartmentBox = aCompartmentBox;
        this.compartmentBox.addChangeReceiver(this);
    }
    // </editor-fold>
    
    // </editor-fold>
}
