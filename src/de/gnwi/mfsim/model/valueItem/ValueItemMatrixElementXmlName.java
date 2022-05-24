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
package de.gnwi.mfsim.model.valueItem;
/**
 * String constants for a ValueItemMatrixElement instance XML representation
 *
 * @author Achim Zielesny
 */
public interface ValueItemMatrixElementXmlName {

    /**
     * <code>MATRIX_ELEMENT</code>: Name of matrix element
     */
    String MATRIX_ELEMENT = "ValueItemMatrixElement";
    /**
     * <code>MATRIX_ELEMENT_VALUE</code>: Name of matrix element value
     */
    String VALUE = "Value";
    /**
     * <code>MATRIX_ELEMENT_VALUE</code>: Name of matrix element protein data
     */
    String PROTEIN_DATA = "ProteinData";
    /**
     * <code>VERSION</code>: Name of version
     */
    String VERSION = "Version";
}
