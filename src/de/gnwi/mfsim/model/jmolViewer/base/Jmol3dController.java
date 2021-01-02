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
package de.gnwi.mfsim.model.jmolViewer.base;

import de.gnwi.mfsim.model.jmolViewer.command.IJmolCommands;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dPanel;
import de.gnwi.mfsim.model.peptide.utils.Tools;
import javajs.util.P4;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import org.apache.commons.lang3.StringUtils;

/**
 * Controller class for the DpdJmolViewer.
 *
 * @author Andreas Truszkowski
 */
public abstract class Jmol3dController {

    /**
     * Jmol panel.
     */
    protected Jmol3dPanel view = null;
    AWTEventListener eventListener = new AWTEventListener() {
        @Override
        public void eventDispatched(AWTEvent e) {
            if (e.getSource().equals(view.getJmolPanel())) {
                if (e instanceof MouseWheelEvent) {
                    MouseWheelEvent tmpMouseWheelEvent = (MouseWheelEvent) e;
                    Jmol3dController.this.handleMouseWheelEvent(tmpMouseWheelEvent);
                } else if (e instanceof MouseEvent) {
                    MouseEvent tmpMouseEvent = (MouseEvent) e;
                    Jmol3dController.this.handleMouseEvent(tmpMouseEvent);
                }
            }
        }
    };
    private EventListenerList eventListenerList = new EventListenerList();

    /**
     * Creates a new instance.
     *
     * @param aView Jmol viewer view.
     */
    public Jmol3dController(Jmol3dPanel aView) {
        view = aView;
        Toolkit.getDefaultToolkit().addAWTEventListener(eventListener,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        this.executeScript("set showfrank false;");
    }

    /**
     * Executes given command.
     *
     * @param aCommand Command.
     */
    public void executeCommand(IJmolCommands aCommand) {
        if (aCommand.isWait()) {
            this.executeScriptWait(aCommand.getScript());
        } else {
            this.executeScript(aCommand.getScript());
        }
    }

    /**
     * Queues given Jmol script.
     *
     * @param aScript Jmol script.
     */
    public final void executeScript(String aScript) {
        this.view.getJmolViewer().script(aScript);
    }

    /**
     * Queues given Jmol scipt and waits for its execution.
     *
     * @param aScript Jmol script.
     */
    public final void executeScriptWait(String aScript) {
        this.view.getJmolViewer().scriptWait(aScript);
    }

    /**
     * Gets a buffered image from actual scene. The size is equal to the
     * rendering panel.
     *
     * @return Scene image.
     */
    public BufferedImage getScreenImage() {
        Dimension tmpSize = this.view.getJmolPanel().getSize();
        return this.getScreenImage((int) tmpSize.getWidth(), (int) tmpSize.getHeight());
    }

    /**
     * Gets a buffered image from actual scene.
     *
     * @param width Width of the image.
     * @param height Height of the image.
     * @return Scene image.
     */
    public BufferedImage getScreenImage(int width, int height) {
        BufferedImage tmpImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.view.getJmolViewer().renderScreenImage(tmpImage.getGraphics(), width, height);
        return tmpImage;
    }

    /**
     * Rotates the scene by given vector (in degrees).
     *
     * @param aRotation Rotation vector consisting of euler angles.
     */
    public void setRotation(Vector3d aRotation) {
        this.setRotation((int) aRotation.x, (int) aRotation.y, (int) aRotation.z);
    }

    /**
     * Rotates the scene by given values.
     *
     * @param aX X direction in degrees.
     * @param aY Y direction in degrees.
     * @param aZ Z direction in degrees.
     */
    public void setRotation(int aX, int aY, int aZ) {
        String tmpRotationScript = "reset;";
        tmpRotationScript += String.format("rotate x %d;", aX);
        tmpRotationScript += String.format("rotate y %d;", aY);
        tmpRotationScript += String.format("rotate z %d;", aZ);
        this.executeScriptWait(tmpRotationScript);
    }

    /**
     * Gets the rotation information as a quaternion.
     *
     * @return The rotation quaternion.
     */
    public Quat4d getRotation() {
        Quat4d tmpRotation = new Quat4d();
        P4 quat = (P4) this.view.getJmolViewer().evaluateExpressionAsVariable("quaternion()").value;
        tmpRotation.set(new double[] { quat.x, quat.y, quat.z, quat.w });

//        ArrayList tmpOrientationObject = (ArrayList) this.view.getJmolViewer().scriptWaitStatus("show orientation rotation",
//                "scriptEcho");
//        // Old code:
//        // String tmpQuaternionString = ((ArrayList) ((ArrayList) tmpOrientationObject.get(0)).get(0)).get(3).toString().replaceAll("\\{|\\}", "");
//        String tmpQuaternionString = StringUtils.replace(StringUtils.replace(((ArrayList) ((ArrayList) tmpOrientationObject.get(0)).get(0)).get(3).toString(), "{", ""), "}", "");
//        String[] tmpQuaternionTokens = tmpQuaternionString.split("\\s");
//        double[] tmpQuaternionData = {Double.parseDouble(tmpQuaternionTokens[0]), Double.parseDouble(tmpQuaternionTokens[1]),
//            Double.parseDouble(tmpQuaternionTokens[2]), Double.parseDouble(tmpQuaternionTokens[3])};
//        
//        tmpRotation.set(tmpQuaternionData);
        Quat4d tmpNomalizeToDpd = new Quat4d(Math.sin(Math.PI / 4.0), 0, 0, Math.cos(Math.PI / 4.0));
        tmpRotation.mul(tmpNomalizeToDpd);
        return tmpRotation;
    }

    /**
     * Gets the current rotation in Euler angles.
     *
     * @return Current rotation vector.
     */
    public Vector3d getRotationInEuler() {
        return Tools.quaternionToEulerAngles(this.getRotation());
    }

    /**
     * Method for handling mouse events. Only events from the Jmol rendering
     * panel will be routed.
     *
     * @param e Mouse event.
     */
    protected abstract void handleMouseEvent(MouseEvent e);

    /**
     * Method for handling mousewheel events. Only events from the Jmol
     * rendering panel will be routed.
     *
     * @param e Mousewheel event.
     */
    protected abstract void handleMouseWheelEvent(MouseWheelEvent e);

    /**
     * Adds a PropertyChangeListener to the listener list.
     *
     * @param aListener The listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.eventListenerList.add(PropertyChangeListener.class, aListener);
    }

    /**
     * Removes a PropertyChangeListener to the listener list.
     *
     * @param aListener The listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener aListener) {
        this.eventListenerList.remove(PropertyChangeListener.class, aListener);
    }

    /**
     * Fires a property change event.
     *
     * @param aEvent Event to be fired.
     */
    protected void firePropertyChangeEvent(PropertyChangeEvent aEvent) {
        Object[] listeners = this.eventListenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == PropertyChangeListener.class) {
                ((PropertyChangeListener) listeners[i + 1]).propertyChange(aEvent);
            }
        }
    }

    /**
     * Gets the JmolViewer view.
     *
     * @return Jmol view.
     */
    public Jmol3dPanel getView() {
        return this.view;
    }

    /**
     * Gets the orientation script.
     *
     * @return The orientation script.
     */
    public String getOrientationScript() {
        // Get orientation. Uuuuuhh Ugly.... 
        ArrayList tmpOrientationObject = (ArrayList) this.view.getJmolViewer().scriptWaitStatus("show orientation",
                "scriptEcho");
        String[] tmpScriptTokens = ((ArrayList) ((ArrayList) tmpOrientationObject.get(0)).get(0)).get(3).toString().split(
                "\n");
        return tmpScriptTokens[tmpScriptTokens.length - 1];
    }

    /**
     * Gets the rotation script.
     *
     * @return The orientation script.
     */
    public String getRotationScript() {
        // Get orientation. Uuuuuhh Ugly.... 
        ArrayList tmpOrientationObject = (ArrayList) this.view.getJmolViewer().scriptWaitStatus("show orientation rotation",
                "scriptEcho");
        String[] tmpScriptTokens = ((ArrayList) ((ArrayList) tmpOrientationObject.get(0)).get(0)).get(3).toString().split(
                "\n");
        return String.format("rotate QUATERNION %s", tmpScriptTokens[tmpScriptTokens.length - 1]);
    }
}
