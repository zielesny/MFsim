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
package de.gnwi.mfsim.model.graphics;

import java.awt.image.BufferedImage;

/**
 * Interface for image providers
 * 
 * @author Achim Zielesny
 */
public interface IImageProvider {

	/**
	 * Returns image file type
	 * 
	 * @return Image file type
	 */
	ImageFileType getImageFileType();

	/**
	 * Returns number of images
	 * 
	 * @return Number of images
	 */
	int getNumberOfImages();

	/**
	 * Returns image with specified index
	 * 
	 * @param anIndex
	 *            Index of image
	 * @return Image with specified index or null if index is invalid
	 */
	BufferedImage getImage(int anIndex);

}
