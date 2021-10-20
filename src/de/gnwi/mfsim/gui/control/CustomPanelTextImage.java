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
package de.gnwi.mfsim.gui.control;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

/**
 * Panel with label that changes its image according to mouse position
 *
 * @author Achim Zielesny
 */
public class CustomPanelTextImage extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JLabel textLabel;
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    
    /**
     * Image for mouse entered
     */
    private Image mouseEnteredImage;
    /**
     * Image for mouse exited
     */
    private Image mouseExitedImage;
    /**
     * true: Mouse entered, false: Mouse exited
     */
    private boolean isMouseEntered;
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000008L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the panel
     */
    public CustomPanelTextImage() {
        super();
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(final MouseEvent e) {
                isMouseEntered = true;
                repaint();
            }

            public void mouseExited(final MouseEvent e) {
                isMouseEntered = false;
                repaint();
            }
        });
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="textLabel">
        {
            this.textLabel = new JLabel();
            this.textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.textLabel.setText("Text");
            add(this.textLabel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.textLabel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.textLabel, 0, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.textLabel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.textLabel, 0, SpringLayout.WEST, this);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        
        this.isMouseEntered = false;
        this.mouseEnteredImage = null;
        this.mouseExitedImage = null;

        
        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * TextLabel
     * 
     * @return TextLabel
     */
    public JLabel getTextLabel() {
        return textLabel;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    
    /**
     * Paint component
     *
     * @param g Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.isMouseEntered && this.mouseEnteredImage != null) {
            g.drawImage(this.mouseEnteredImage, 0, 0, this);
        } else if (!this.isMouseEntered && this.mouseExitedImage != null) {
            g.drawImage(this.mouseExitedImage, 0, 0, this);
        }
    }

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    
    // <editor-fold defaultstate="collapsed" desc="MouseEnteredImage">
    /**
     * Get mouse-entered image
     *
     * @return Mouse-entered image
     */
    public Image getMouseEnteredImage() {
        return this.mouseEnteredImage;
    }

    /**
     * Set mouse-entered image
     *
     * @param anImage Image
     */
    public void setMouseEnteredImage(Image anImage) {
        this.mouseEnteredImage = anImage;
        repaint();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="MouseExitedImage">
    /**
     * Get mouse-exited image
     *
     * @return Mouse-exited image
     */
    public Image getMouseExitedImage() {
        return this.mouseExitedImage;
    }

    /**
     * Set mouse-exited image
     *
     * @param anImage Image
     */
    public void setMouseExitedImage(Image anImage) {
        this.mouseExitedImage = anImage;
        repaint();
    }
    // </editor-fold>
    
    // </editor-fold>
}
