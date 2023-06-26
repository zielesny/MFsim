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
package de.gnwi.mfsim.model.jmolViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

/**
 * Jmol rendering panel.
 *
 * @author Andreas Truszkowski
 */
public class Jmol3dPanel extends JPanel {

    /**
     * Jmol panel.
     */
    private JmolPanel jmolPanel;

    /**
     * Class providing the jmol viewer.
     */
    public static class JmolPanel extends JPanel {

        /**
         * Jmol scene viewer.
         */
        JmolViewer viewer;
        /**
         * Image background renderer.
         */
        JmolViewer renderer;
        /**
         * Scene size.
         */
        private final Dimension currentSize = new Dimension();

        /**
         * Creates a new instance.
         */
        @SuppressWarnings("LeakingThisInConstructor")
        JmolPanel() {
            renderer = JmolViewer.allocateViewer(new JPanel(), new SmarterJmolAdapter());
            viewer = JmolViewer.allocateViewer(this, new SmarterJmolAdapter());
        }

        @Override
        public void paint(Graphics g) {
            getSize(currentSize);
            viewer.renderScreenImage(g, currentSize.width, currentSize.height);
        }
    }

    /**
     * Creates a new instance.
     */
    public Jmol3dPanel() {
        this.jmolPanel = new Jmol3dPanel.JmolPanel();
        this.setLayout(new BorderLayout());
        this.add(this.jmolPanel, BorderLayout.CENTER);
    }

    /**
     * Gets the jmol panel containing the Jmol viewer.
     *
     * @return Jmol panel.
     */
    public JmolPanel getJmolPanel() {
        return jmolPanel;
    }

    /**
     * Sets the Jmol panel.
     *
     * @param jmolPanel jmolPanel
     */
    public void setJmolPanel(JmolPanel jmolPanel) {
        this.jmolPanel = jmolPanel;
    }

    /**
     * Gets the Jmol scene viewer.
     *
     * @return Jmol viewer.
     */
    public JmolViewer getJmolViewer() {
        return jmolPanel.viewer;
    }

    /**
     * Gets the image background renderer.
     *
     * @return Background renderer.
     */
    public JmolViewer getJmolRenderer() {
        return jmolPanel.renderer;
    }

}