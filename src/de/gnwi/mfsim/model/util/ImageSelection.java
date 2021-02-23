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
package de.gnwi.mfsim.model.util;

import de.gnwi.mfsim.model.util.ModelUtils;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * Image selection for clipboard operations
 * 
 * @author Achim Zielesny
 */
public class ImageSelection implements Transferable {

	// <editor-fold defaultstate="collapsed" desc="Private class variables">

	/**
	 * Image
	 */
	private Image image;

	/**
	 * Flavor
	 */
	private DataFlavor imageFlavor;

	/**
	 * Flavors
	 */
	private DataFlavor[] imageFlavors;

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Constructor">

	/**
	 * Constructor
	 * 
	 * @param anImage
	 *            Image
	 * @throws IllegalArgumentException
	 *             Thrown if image is invalid
	 */
	public ImageSelection(Image anImage) throws IllegalArgumentException {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (anImage == null) {
			throw new IllegalArgumentException("Image is invalid.");
		}

		// </editor-fold>

		this.image = anImage;
		this.imageFlavor = DataFlavor.imageFlavor;
		this.imageFlavors = new DataFlavor[] { DataFlavor.imageFlavor };
	}

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Public Transferable methods">

	/**
	 * Standard transferable method
	 * 
	 * @param aFlavor
	 *            Flavor
	 * @return BMP image
	 * @throws UnsupportedFlavorException
	 *             Thrown if flavor is not supported
	 */
	public synchronized Object getTransferData(DataFlavor aFlavor)
			throws UnsupportedFlavorException {
		if (!aFlavor.equals(this.imageFlavor)) {
			throw new UnsupportedFlavorException(aFlavor);
		}
		return this.image;
	}

	/**
	 * Standard transferable method
	 * 
	 * @param aFlavor
	 *            Flavor
	 * @return True: Flavor is supported, false: Otherwise
	 */
	public boolean isDataFlavorSupported(DataFlavor aFlavor) {
		return aFlavor.equals(this.imageFlavor);
	}

	/**
	 * Standard transferable method
	 * 
	 * @return Flavors
	 */
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return this.imageFlavors;
	}

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Public static copy to clipboard method">

	/**
	 * Copies an image to the clipboard in BMP format
	 * 
	 * @param anImage
	 *            Image
	 * @return True: Image was successfully copied to clipboard, false:
	 *         Otherwise
	 */
	public static boolean copyImageToClipboard(Image anImage) {
		try {
			ImageSelection tmpImageSelection = new ImageSelection(anImage);
			Clipboard tmpClipboard = java.awt.Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			tmpClipboard.setContents(tmpImageSelection, null);
			return true;
		} catch (Exception anException) {
		    ModelUtils.appendToLogfile(true, anException);
			return false;
		}
	}

	// </editor-fold>

}
