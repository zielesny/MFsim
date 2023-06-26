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
package de.gnwi.mfsim.model.job;

import de.gnwi.spices.PointInSpace;

/**
 * Utility class with static utility methods for determine the coordinates of spheres in various packing in a cuboid and in a globe, respectively.
 * 
 * @author Mirco Daniel
 */
public class SpherePackingUtils {

	private double SQRT3 = Math.sqrt(3);
	private double SQRT6 = Math.sqrt(6);

	/** 
	 * Default constructor
	 */
	public SpherePackingUtils() {
	}
	
	/**
	 * Determine the x-, y- and z-coordinates of the particles (midpoints), which are packed in simple cubic (sc) in a cuboid
	 * 
	 * @param rParticle: radius of particle
	 * @param xCuboid: width of cuboid 
	 * @param yCuboid: length of cuboid 
	 * @param zCuboid: height of cuboid
	 * @return x-, y- and z-coordinates of particles in cuboid
	 */
	public PointInSpace[] particleCoordinatesSimpleCubicInCuboid(double rParticle, double xCuboid, double yCuboid, double zCuboid) {
		if (rParticle <= 0 || xCuboid <= 0 || yCuboid <= 0 || zCuboid <= 0)
			return null;
		int nX = (int) (xCuboid / 2 / rParticle + 1);
		int nY = (int) (yCuboid / 2 / rParticle + 1);
		int nZ = (int) (zCuboid / 2 / rParticle + 1);
		int nSpheres = nX * nY * nZ;
		int iSpheres = 0;
		PointInSpace[] coordinates = new PointInSpace[nSpheres];
		for (int i = 0; i < nZ; i++) {
			for (int j = 0; j < nY; j++) {
				for (int k = 0; k < nX; k++) {
					coordinates[iSpheres] = new PointInSpace(2 * rParticle * k, 2 * rParticle * j, 2 * rParticle * i);
					iSpheres++;
				}
			}
		}
		return coordinates;
	}

	/**
	 * Overloaded method 
	 * 
	 * @param rParticle; radius of particle
	 * @param xCuboid: width of cuboid
	 * @param yCuboid: length of cuboid
	 * @param zCuboid: height of cuboid
	 * @param startVector: startvector of cuboid
	 * @return x-, y- and z-coordinates of particles in cuboid
	 */
	public PointInSpace[] particleCoordinatesSimpleCubicInCuboid(double rParticle, double xCuboid, double yCuboid, double zCuboid, PointInSpace startVector) {
		return this.moveVector(particleCoordinatesSimpleCubicInCuboid(rParticle, xCuboid, yCuboid, zCuboid), startVector);
	}
	
	/**
	 * Determine the x-, y- and z-coordinates of particles (midpoints), which are packed in simple cubic (sc) in sphere
	 * 
	 * @param rParticle: radius of particle 
	 * @param rSphere: radius of sphere
	 * @return x-, y- and z-coordinates of particles in sphere
	 */
	public PointInSpace[] particleCoordinatesSimpleCubicInSphere(double rParticle, double rSphere) {
		if (rParticle <= 0 || rSphere <= 0)
			return null;
		// Determine how many particles fit in a row between midpoint and edge of the sphere.
		// Due to the symmetry only one octant is necessary.
		double d = 2 * rParticle;
		int nX = (int) (rSphere / d + 1); // max. spheres between midpoint to the edge
		int nY = nX;
		int nZ = nX;
		int nMax = nX; // max. particles between current center axis and edge
		int[][] nParticlesInRow = new int[nZ][nY];
		int[] nParticlesInArea = new int[nZ];
		int nParticlesInSphere = 0;
		nParticlesInRow[0][0] = nX;
		nParticlesInArea[0] = nX;

		// Determine numbers of particles in each rows and in each layer of one octant
		for (int i = 0; i < nZ; i++) {
			int jBegin;
			if (i == 0)
				jBegin = 1;
			else
				jBegin = 0;
			nMax = nParticlesInRow[0][i];
			for (int j = jBegin; j < nY; j++) {
				while (nMax > 0 && !this.IsInGlobe(new PointInSpace(d * (nMax - 1), d * j, d * i), rSphere)) {
					nMax--;
				}
				nParticlesInRow[i][j] = nMax;
				nParticlesInArea[i] += nMax;
			}
		}

		// Determine numbers of particles in sphere
		nParticlesInSphere = 4 * (nParticlesInArea[0] - nParticlesInRow[0][0]) + 1;
		for (int i = 1; i < nZ; i++) {
			nParticlesInSphere += 8 * (nParticlesInArea[i] - nParticlesInRow[i][0]) + 2;
		}

		// Determine coordinates of particles
		PointInSpace[] coordinates = new PointInSpace[nParticlesInSphere];
		int iParticle = 0;
		coordinates[iParticle] = new PointInSpace(0, 0, 0);
		iParticle++;
		for (int i = 0; i < nY; i++) {
			for (int j = 1; j < nParticlesInRow[0][i]; j++) {
				coordinates[iParticle] = new PointInSpace(j * d, i * d, 0);
				iParticle++;
				coordinates[iParticle] = new PointInSpace(i * d, -j * d, 0);
				iParticle++;
				coordinates[iParticle] = new PointInSpace(-j * d, -i * d, 0);
				iParticle++;
				coordinates[iParticle] = new PointInSpace(-i * d, j * d, 0);
				iParticle++;
			}
		}
		for (int i = 1; i < nZ; i++) {
			coordinates[iParticle] = new PointInSpace(0, 0, i * d);
			iParticle++;
			coordinates[iParticle] = new PointInSpace(0, 0, -i * d);
			iParticle++;
			for (int j = 0; j < nY; j++) {
				for (int k = 1; k < nParticlesInRow[i][j]; k++) {
					coordinates[iParticle] = new PointInSpace(k * d, j * d, i * d);
					iParticle++;
					coordinates[iParticle] = new PointInSpace(j * d, -k * d, i * d);
					iParticle++;
					coordinates[iParticle] = new PointInSpace(-k * d, -j * d, i * d);
					iParticle++;
					coordinates[iParticle] = new PointInSpace(-j * d, k * d, i * d);
					iParticle++;
					coordinates[iParticle] = new PointInSpace(k * d, j * d, -i * d);
					iParticle++;
					coordinates[iParticle] = new PointInSpace(j * d, -k * d, -i * d);
					iParticle++;
					coordinates[iParticle] = new PointInSpace(-k * d, -j * d, -i * d);
					iParticle++;
					coordinates[iParticle] = new PointInSpace(-j * d, k * d, -i * d);
					iParticle++;
				}
			}
		}
		return coordinates;
	}

	/**
	 * Overloaded method 
	 * 
	 * @param rParticle: Radius of particle
	 * @param rSphere: Radius of Sphere
	 * @param startVector: start vector
	 * @return particle coordinates within moved by start vector
	 */
	public PointInSpace[] particleCoordinatesSimpleCubicInSphere(double rParticle, double rSphere, PointInSpace startVector){
		return this.moveVector(particleCoordinatesSimpleCubicInSphere(rParticle, rSphere), startVector);
	}

	/**
	 * Determine the x-, y- and z-coordinates of the spheres (midpoints), which are packed in hexagonal close (hcp) in a cuboid. This method do not consider whether the result packing is a solution
	 * with best available with most spheres in the cuboid.
	 * 
	 * @param rParticle: Radius of the spheres 
	 * @param xCuboid: Width of the cuboid 
	 * @param yCuboid: Length of the cuboid 
	 * @param zCuboid: Height of the cuboid
	 * @return x-, y- and z-coordinates of the particles in the cuboid
	 */
	public PointInSpace[] particleCoordinatesHexagonalCloseInCuboid(double rParticle, double xCuboid, double yCuboid, double zCuboid) {
		if (rParticle <= 0 || xCuboid <= 0 || yCuboid <= 0 || zCuboid <= 0)
			return null;
		double d = 2 * rParticle;
		int nX = 0;
		int nY = 0;
		int nXAOdd = (int) (xCuboid / d + 1); 		// Numbers of particles in x-direction in the odd number of A-layer
		int nXAEven = (int) (xCuboid / d + 0.5); 	// Numbers of particles in x-direction in the even number of A-layer
		int nXBOdd = nXAEven; 						// Numbers of particles in x-direction in the odd number of B-layer
		int nXBEven = nXAOdd; 						// Numbers of particles in x-direction in the even number of B-layer
		int nYA = (int) (yCuboid / (this.SQRT3 * rParticle) + 1); // Numbers of particles in y-direction of A-layer
		int nYB = (int) ((yCuboid - this.SQRT3 / 3 * rParticle) / (this.SQRT3 * rParticle) + 1); // Numbers of particles in y-direction of B-layer
		int nZ = (int) (zCuboid / (2 * this.SQRT6 * rParticle / 3) + 1); // Numbers of particles in z-direction;
		boolean isOddRow;
		boolean isOddLayer;
		boolean hasSameParticlesX;
		boolean hasSameParticlesY;
		if (nXAOdd == nXAEven)
			hasSameParticlesX = true;
		else
			hasSameParticlesX = false;
		if (nYA == nYB)
			hasSameParticlesY = true;
		else
			hasSameParticlesY = false;

		// Determine how many particles fit in the cuboid
		int nParticle;
		if (hasSameParticlesX) {
			nX = nXAOdd;
			if (hasSameParticlesY)
				nParticle = nX * nYA * nZ;
			else
				nParticle = nX * nYA * ((int) ((nZ + 1) / 2) + nXAEven * nYB * (int) (nZ / 2));
		} else if (hasSameParticlesY)
			nParticle = (nXAOdd * (int) ((nYA + 1) / 2) + nXAEven * (int) (nYA / 2)) * nZ;
		else
			nParticle = (nXAOdd * (int) ((nYA + 1) / 2) + nXAEven * (int) (nYA / 2)) * (int) ((nZ + 1) / 2) + (nXBOdd * (int) ((nYB + 1) / 2) + nXBEven * (int) (nYB / 2)) * (int) (nZ / 2);

		// Determine the coordinates of particles
		int iParticle = 0;
		PointInSpace[] coordinate = new PointInSpace[nParticle];
		for (int i = 0; i < nZ; i++) {
			if (i % 2 == 0)
				isOddLayer = true;
			else
				isOddLayer = false;
			if (hasSameParticlesY)
				nY = nYA;
			else {
				if (i % 2 == 0)
					nY = nYA;
				else
					nY = nYA - 1;
			}
			for (int j = 0; j < nY; j++) {
				if (j % 2 == 0)
					isOddRow = true;
				else
					isOddRow = false;
				if (!hasSameParticlesX) {
					if (isOddLayer) {
						if (isOddRow)
							nX = nXAOdd;
						else
							nX = nXAOdd - 1;
					} else {
						if (isOddRow)
							nX = nXAOdd - 1;
						else
							nX = nXAOdd;
					}
				}
				for (int k = 0; k < nX; k++) {
					if (isOddLayer) {
						if (isOddRow)
							coordinate[iParticle] = new PointInSpace(d * k, this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
						else
							coordinate[iParticle] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
					} else {
						if (isOddRow)
							coordinate[iParticle] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
						else
							coordinate[iParticle] = new PointInSpace(d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
					}
					iParticle++;
				}
			}
		}
		return coordinate;
	}

	/**
	 * Overloaded method
	 * 
	 * @param rParticle: Radius of particle
	 * @param xCuboid: Width of cuboid
	 * @param yCuboid: Length of cuboid
	 * @param zCuboid: Height of cuboid
	 * @param startVector: Start vector
	 * @return Coordinates of particles
	 */
	public PointInSpace[] particleCoordinatesHexagonalCloseInCuboid(double rParticle, double xCuboid, double yCuboid, double zCuboid, PointInSpace startVector){
		return this.moveVector(particleCoordinatesHexagonalCloseInCuboid(rParticle, xCuboid, yCuboid, zCuboid), startVector);
	}
		
	/**
	 * Determine the x-, y- and z-coordinates of the spheres (midpoints), which are packed in hexagonal close (hcp) in a globe. This method do not consider whether the result packing is a solution
	 * with best available with most spheres in the globe.
	 * 
	 * @param rParticle radius of the sphere 
	 * @param rSphere radius of sphere
	 * @return x-, y- and z-coordinates of the particles in globe
	 */
	public PointInSpace[] particleCoordinatesHexagonalCloseInSphere(double rParticle, double rSphere) {
		if (rParticle <= 0 || rSphere <= 0)
			return null;
		// Determine how many particles fit in a row between midpoint and edge of sphere.
		double d = 2 * rParticle;
		double x = 0;
		double y = 0;
		double z = 0;
		int nX = (int)(rSphere / d + 1);	//max. spheres between midpoint to the edge
		int nMax = nX; 						//max. spheres between current center axis and edge
		int nXLimit = nX;
		int nYPlus = (int)(rSphere / this.SQRT3 / rParticle + 1);
		if (nYPlus % 2 == 0 && nYPlus > 0)
			if (!this.IsInGlobe(new PointInSpace(rParticle, (nYPlus - 1) * this.SQRT3 * rParticle, 0), rSphere))
				nYPlus--;
		int nYMinus = nYPlus;
		int nZ = (int) (3 * rSphere / this.SQRT6 / d + 1);
		if (nZ % 2 == 0 && nZ > 0)
			if (!this.IsInGlobe(new PointInSpace(rParticle, this.SQRT3 * rParticle / 3, (nZ - 1) * 2 * this.SQRT6 / 3 * rParticle), rSphere))
				nZ--;
		int iLayer = 0;
		boolean isEvenRow = true;;
		boolean hasnMaxChanged = false;
		
		// Determine numbers of spheres in each rows and in each layer of a quadrant
		int[][] nSpheresOctant1 = new int[nZ][nYPlus];
		int[][] nSpheresOctant4 = new int[nZ][nYMinus];
		int nSpheresInSphere = 0;
		nSpheresOctant1[0][0] = nX;
		for (int i = 0; i < nZ; i++){
			nMax = nXLimit;
			hasnMaxChanged = false;
			int jBegin;
			if (i == 0) 
				jBegin = 1;
			else
				jBegin = 0;
			if (i % 2 == 0)
				iLayer = 1;
			else
				iLayer = 2;
					
			// Octant 1
			for (int j = jBegin; j < nYPlus; j++){
				if (j % 2 == 0)
					isEvenRow = true;
				else
					isEvenRow = false;
				do{
					if (iLayer == 1){
						if (isEvenRow){
							x = d * (nMax - 1);
						}
						else{
							x = d * (nMax - 1) + rParticle;
						}	
						if (hasnMaxChanged)
							y = this.SQRT3 * rParticle * j;
					}
					else{
						if (isEvenRow){
							x = d * (nMax - 1) + rParticle;
						}
						else{
							x = d * (nMax - 1);
							
						}
						if (hasnMaxChanged)
							y = this.SQRT3 * rParticle * (1.0 / 3 + j);
					}
					if (hasnMaxChanged)
						z = 2.0 * this.SQRT6 / 3 * rParticle * i;
					if (!this.IsInGlobe(new PointInSpace(x, y, z), rSphere)){
						nMax--;
						hasnMaxChanged = true;
					}
					else
						break;
				}while (nMax > 0);
				if (nMax == 0)
					break;
				if (iLayer == 1)
					nXLimit = nMax;
				nSpheresOctant1[i][j] = nMax;
			}
			// Octant 4
			nXLimit = nX;
			if (iLayer == 2){
				nMax = nXLimit;
				for (int j = 0; j < nYMinus; j++){
					if (j % 2 == 0)
						isEvenRow = true;
					else
						isEvenRow = false;
					do{
						if (isEvenRow){
							x = d * (nMax - 1);
						}
						else{
							x = d * (nMax - 1) + rParticle; 
						}
						if (hasnMaxChanged){
							y = this.SQRT3 * rParticle * (2.0 / 3 + j);
							z = 2.0 * this.SQRT6 / 3 * rParticle * i; 
						}
						if (!this.IsInGlobe(new PointInSpace(x, y, z), rSphere)){
							nMax--;
							hasnMaxChanged = true;
						}
						else
							break;
					}while (nMax > 0);
					if (nMax == 0)
						break;
					if (iLayer == 1)
						nXLimit = nMax;
					nSpheresOctant4[i][j] = nMax;
				}
			}
		}
		
		// Counts all spheres in globe
		int iFactor = 1;	//If equatorial layer then iFactor is 1 otherwise 2
		for (int i = 0; i < nZ; i++){
			if (i == 0)
				iFactor = 1;
			else
				iFactor = 2;
			for (int j = 0; j < nYPlus; j++){
				if (nSpheresOctant1[i][j] == 0)
					continue;
				if (i % 2 == 0){
					if (j % 2 == 0){
						if (j == 0){
							nSpheresInSphere += iFactor * (2 * nSpheresOctant1[i][j] - 1);
						}
						else {
							nSpheresInSphere += iFactor * 2 * (2 * nSpheresOctant1[i][j] - 1);
						}
					}
					else {
						nSpheresInSphere += iFactor * 4 * nSpheresOctant1[i][j];
					}
				}
				else {
					if (j % 2 == 0){
						nSpheresInSphere += iFactor * 2 * nSpheresOctant1[i][j];
					}
					else {
						nSpheresInSphere += iFactor * (2 * nSpheresOctant1[i][j] - 1);
					}
				}
			}
			if (i % 2 == 1){
				for (int j = 0; j < nYMinus; j++){
					if (nSpheresOctant1[i][j] == 0)
						continue;
					if (j % 2 == 0){
						nSpheresInSphere += iFactor * (2 * nSpheresOctant4[i][j] - 1);
					}
					else{
						nSpheresInSphere += iFactor * 2 * nSpheresOctant4[i][j] ;
					}
				}
			}
		}

		// Determine coordinates of the spheres
		PointInSpace[] coordinate = new PointInSpace[nSpheresInSphere];
		int iSphere = 0;
		boolean isEquatorialLayer;
		boolean isEvenLayer;
		for (int i = 0; i < nZ; i++){
			if (i == 0)
				isEquatorialLayer = true;
			else
				isEquatorialLayer = false;
			isEvenLayer = this.IsEvenRow(i);
			for (int j = 0; j < nYPlus; j++){
				isEvenRow = this.IsEvenRow(j);
				if (nSpheresOctant1[i][j] == 0)
					continue;
				for (int k = 0; k < nSpheresOctant1[i][j]; k++){
					if (isEquatorialLayer){
						if (isEvenRow){
							if (j == 0){
								coordinate[iSphere] = new PointInSpace(k * d, 0, 0);
								iSphere++;
								if (k > 0){
									coordinate[iSphere] = new PointInSpace(-k * d, 0, 0);
									iSphere++;
								}
							}
							else{
								coordinate[iSphere] = new PointInSpace(k * d, this.SQRT3 * rParticle * j , 0);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(k * d, -this.SQRT3 * rParticle * j , 0);
								iSphere++;
								if (k > 0){
									coordinate[iSphere] = new PointInSpace(-k * d, this.SQRT3 * rParticle * j , 0);
									iSphere++;
									coordinate[iSphere] = new PointInSpace(-k * d, -this.SQRT3 * rParticle * j , 0);
									iSphere++;
								}
							}
						}
						else{
							coordinate[iSphere] = new PointInSpace(k  * d + rParticle, this.SQRT3 * rParticle * j , 0);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(k  * d + rParticle, -this.SQRT3 * rParticle * j , 0);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), this.SQRT3 * rParticle * j , 0);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), -this.SQRT3 * rParticle * j , 0);
							iSphere++;
						}
					}
					else{
						if (isEvenLayer){
							if (isEvenRow){
								if (j == 0){
									coordinate[iSphere] = new PointInSpace(k * d, 0, this.SQRT6 / 3 * d * i);
									iSphere++;
									coordinate[iSphere] = new PointInSpace(k * d, 0, -this.SQRT6 / 3 * d * i);
									iSphere++;
									if (k > 0){
										coordinate[iSphere] = new PointInSpace(-k * d, 0, this.SQRT6 / 3 * d * i);
										iSphere++;
										coordinate[iSphere] = new PointInSpace(-k * d, 0, -this.SQRT6 / 3 * d * i);
										iSphere++;
									}
								}
								else{
									coordinate[iSphere] = new PointInSpace(k * d, this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
									iSphere++;
									coordinate[iSphere] = new PointInSpace(k * d, -this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
									iSphere++;
									coordinate[iSphere] = new PointInSpace(k * d, this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
									iSphere++;
									coordinate[iSphere] = new PointInSpace(k * d, -this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
									iSphere++;
									if (k > 0){
										coordinate[iSphere] = new PointInSpace(-k * d, this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
										iSphere++;
										coordinate[iSphere] = new PointInSpace(-k * d, -this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
										iSphere++;
										coordinate[iSphere] = new PointInSpace(-k * d, this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
										iSphere++;
										coordinate[iSphere] = new PointInSpace(-k * d, -this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
										iSphere++;
									}
								}
							}
							else{
								coordinate[iSphere] = new PointInSpace(k  * d + rParticle, this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(k  * d + rParticle, -this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(k  * d + rParticle, this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(k  * d + rParticle, -this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), -this.SQRT3 * rParticle * j , this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), -this.SQRT3 * rParticle * j , -this.SQRT6 / 3 * d * i);
								iSphere++;
							}
						}
						else{
							if (isEvenRow){
								coordinate[iSphere] = new PointInSpace(k  * d + rParticle, this.SQRT3 * rParticle * (1.0 / 3 + j), this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(k  * d + rParticle, this.SQRT3 * rParticle * (1.0 / 3 + j), -this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), this.SQRT3 * rParticle * (1.0 / 3 + j), this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(-(k  * d + rParticle), this.SQRT3 * rParticle * (1.0 / 3 + j), -this.SQRT6 / 3 * d * i);
								iSphere++;
							}
							else{
								coordinate[iSphere] = new PointInSpace(k * d, this.SQRT3 * rParticle * (1.0 / 3 + j) , this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(k * d, this.SQRT3 * rParticle * (1.0 / 3 + j) , -this.SQRT6 / 3 * d * i);
								iSphere++;
								if (k > 0){
									coordinate[iSphere] = new PointInSpace(-(k  * d), this.SQRT3 * rParticle * (1.0 / 3 + j) , this.SQRT6 / 3 * d * i);
									iSphere++;
									coordinate[iSphere] = new PointInSpace(-(k  * d), this.SQRT3 * rParticle * (1.0 / 3 + j) , -this.SQRT6 / 3 * d * i);
									iSphere++;
								}
							}
						}
					}
				}
			}
			if (!isEvenLayer){
				for (int j = 0; j < nYMinus; j++){
					if (j % 2 == 0)
						isEvenRow = true;
					else
						isEvenRow = false;
					if (nSpheresOctant4[i][j] == 0)
						continue;
					for (int k = 0; k < nSpheresOctant4[i][j]; k++){
						if (isEvenRow){
							coordinate[iSphere] = new PointInSpace(k * d, -this.SQRT3 * rParticle * (2.0 / 3 + j) , this.SQRT6 / 3 * d * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(k * d, -this.SQRT3 * rParticle * (2.0 / 3 + j) , -this.SQRT6 / 3 * d * i);
							iSphere++;
							if (k > 0){
								coordinate[iSphere] = new PointInSpace(-k * d, -this.SQRT3 * rParticle * (2.0 / 3 + j) , this.SQRT6 / 3 * d * i);
								iSphere++;
								coordinate[iSphere] = new PointInSpace(-k * d, -this.SQRT3 * rParticle * (2.0 / 3 + j) , -this.SQRT6 / 3 * d * i);
								iSphere++;
							}
						}
						else{
							coordinate[iSphere] = new PointInSpace(k * d + rParticle, -this.SQRT3 * rParticle * (2.0 / 3 + j) , this.SQRT6 / 3 * d * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(k * d + rParticle, -this.SQRT3 * rParticle * (2.0 / 3 + j) , -this.SQRT6 / 3 * d * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(k * d + rParticle), -this.SQRT3 * rParticle * (2.0 / 3 + j) , this.SQRT6 / 3 * d * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(k * d + rParticle), -this.SQRT3 * rParticle * (2.0 / 3 + j) , -this.SQRT6 / 3 * d * i);
							iSphere++;
						}
					}	
				}
			}
		}
		return coordinate;
	}

	/**
	 * Overloaded method
	 * 
	 * @param rParticle: Radius of particle
	 * @param rSphere: Radius of sphere
	 * @param startVector: Start vector
	 * @return Coordinates of particles
	 */
	public PointInSpace[] particleCoordinatesHexagonalCloseInSphere(double rParticle, double rSphere, PointInSpace startVector){
		return this.moveVector(particleCoordinatesHexagonalCloseInSphere(rParticle, rSphere), startVector);
	}

	/**
	 * Determine the x-, y- and z-coordinates of particle (midpoints), which are packed in face-centered close (fcc) in a cuboid. This method do not consider whether the result packing is a
	 * solution with best available with most particles in the cuboid.
	 * 
	 * @param rParticle: radius of particle 
	 * @param xCuboid width of cuboid 
	 * @param yCuboid length of cuboid 
	 * @param zCuboid height of cuboid
	 * @return x-, y- and z-coordinates of spheres in the cuboid
	 */
	public PointInSpace[] particleCoordinatesFaceCenteredCubicInCuboid(double rParticle, double xCuboid, double yCuboid, double zCuboid) {
		if (rParticle <= 0 || xCuboid <= 0 || yCuboid <= 0 || zCuboid <= 0)
			return null;
		double d = rParticle * 2;
		int nX = 0;
		int nY = 0;
		int nXA0 = (int) (xCuboid / d + 1); 	// Numbers of particles in x-direction in the odd number of A-layer
		int nXA1 = (int) (xCuboid / d + 0.5); 	// Numbers of particles in x-direction in the even number of A-layer
		int nXB0 = nXA1; // Numbers of particles in x-direction in the odd number of B-layer
		int nXB1 = nXA0; // Numbers of particles in x-direction in the even number of B-layer
		int nXC0 = nXA0; // Numbers of particles in x-direction in the odd number of C-layer
		int nXC1 = nXA1; // Numbers of particles in x-direction in the even number of C-layer
		int nYA = (int) (yCuboid / (this.SQRT3 * rParticle) + 1); // Numbers of particles in y-direction of A-layer
		int nYB = (int) ((yCuboid - this.SQRT3 / 3 * rParticle) / (this.SQRT3 * rParticle) + 1); // Numbers of particles in y-direction of B-layer
		int nYC = (int) ((yCuboid - d * this.SQRT3 / 3) / (this.SQRT3 * rParticle) + 1); // Numbers of particles in y-direction of C-layer
		int nZ = (int) (zCuboid / (d * this.SQRT6 / 3) + 1); // Numbers of particles in z-direction;
		int nZA = (int) ((nZ + 2) / 3); // Numbers of A-Layer
		int nZB = (int) ((nZ + 1) / 3); // Numbers of B-Layer
		int nZC = (int) (nZ / 3); // Numbers of C-Layer

		// Determine how many particles fit in the cuboid
		int nSpheres;
		int nA; // Numbers of all particles in A-Layers
		int nB; // Numbers of all particles in B-Layers
		int nC; // Numbers of all particles in C-Layers
		if (nYA % 2 == 0) {
			nA = (nXA0 + nXA1) * nYA / 2 * nZA;
		} else {
			nA = (nXA0 * ((int) (nYA / 2) + 1) + nXA1 * (int) (nYA / 2)) * nZA;
		}
		if (nYB % 2 == 0) {
			nB = (nXB0 + nXB1) * nYB / 2 * nZB;
		} else {
			nB = (nXB0 * ((int) (nYB / 2) + 1) + nXB1 * (int) (nYB / 2)) * nZB;
		}
		if (nYC % 2 == 0) {
			nC = (nXC0 + nXC1) * nYC / 2 * nZC;
		} else {
			nC = (nXC0 * ((int) (nYC / 2) + 1) + nXC1 * (int) (nYC / 2)) * nZC;
		}
		nSpheres = nA + nB + nC;
		PointInSpace coordinate[] = new PointInSpace[nSpheres];

		// Determine the coordinates of particles
		int iLayer = 0;
		int iSphere = 0;
		boolean isRowEven;
		for (int i = 0; i < nZ; i++) {
			switch (i % 3) {
			case 0:
				iLayer = 1;
				nY = nYA;
				break;
			case 1:
				iLayer = 2;
				nY = nYB;
				break;
			case 2:
				iLayer = 3;
				nY = nYC;
				break;
			}
			for (int j = 0; j < nY; j++) {
				if (j % 2 == 0) {
					isRowEven = true;
					switch (iLayer) {
					case 1:
						nX = nXA0;
						break;
					case 2:
						nX = nXB0;
						break;
					case 3:
						nX = nXC0;
						break;
					}
				} else {
					isRowEven = false;
					switch (iLayer) {
					case 1:
						nX = nXA1;
						break;
					case 2:
						nX = nXB1;
						break;
					case 3:
						nX = nXC1;
						break;
					}
				}
				for (int k = 0; k < nX; k++) {
					switch (iLayer) {
					case 1:
						if (isRowEven) {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
							iSphere++;
						}
						break;
					case 2:
						if (isRowEven) {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						}
						break;
					case 3:
						if (isRowEven) {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							break;
						}
					}
				}
			}
		}
		return coordinate;
	}
	
	/**
	 * Overloaded method
	 * 
	 * @param rParticle: Radius of particle
	 * @param xCuboid: Width of cuboid
	 * @param yCuboid: Length of cuboid
	 * @param zCuboid: Height of cuboid
	 * @param startVector: Start vector
	 * @return Coordinates of particles
	 */
	public PointInSpace[] particleCoordinatesFaceCenteredCubicInCuboid(double rParticle, double xCuboid, double yCuboid, double zCuboid, PointInSpace startVector){
		return this.moveVector(particleCoordinatesFaceCenteredCubicInCuboid(rParticle, xCuboid, yCuboid, zCuboid), startVector);
	}
	
	/**
	 * Determine the x-, y- and z-coordinates of the particles (midpoints), which are packed in face centered close (fcc) in a sphere. 
	 * This method do not consider whether the result packing is a solution
	 * with best available with most particles in the sphere.
	 * 
	 * @param rParticle Radius of the particle
	 * @param rSphere Radius of the sphere
	 * @return x-, y- and z-coordinates of the particles in the sphere
	 */
	public PointInSpace[] particleCoordinatesFaceCenteredCubicInSphere(double rParticle, double rSphere) {
		if (rParticle <= 0 || rSphere <= 0)
			return null;

		// Determine how many particles fit in a row between midpoint and edge of the sphere.
		double d = 2 * rParticle;
		int nX = (int) (rSphere / d + 1); // Max. particles between midpoint to the edge
		int nYPlus = (int) (rSphere / this.SQRT3 / rParticle + 1);
		if (nYPlus % 2 == 0 && nYPlus > 0)
			if (!this.IsInGlobe(new PointInSpace(rParticle, (nYPlus - 1) * this.SQRT3 * rParticle, 0), rSphere))
				nYPlus--;
		int nYMinus = nYPlus;
		int nZPlus = (int) (3 * rSphere / this.SQRT6 / d + 1);
		int nZMinus = nZPlus;
		switch (nZPlus % 3) {
		case 2: // Layer B
			if (!this.IsInGlobe(new PointInSpace(rParticle, this.SQRT3 * rParticle / 3, (nZPlus - 1) * 2 * this.SQRT6 / 3 * rParticle), rSphere))
				nZPlus--;
			break;
		case 0: // Layer C
			if (!this.IsInGlobe(new PointInSpace(0, rParticle, (nZPlus - 1) * 2 * this.SQRT6 / 3 * rParticle), rSphere))
				nZPlus--;
			break;
		}
		switch (nZMinus % 3) {
		case 1: // Layer C
			if (!this.IsInGlobe(new PointInSpace(0, rParticle, nZMinus * 2 * this.SQRT6 / 3 * rParticle), rSphere))
				nZMinus--;
			break;
		case 2: // Layer B
			if (!this.IsInGlobe(new PointInSpace(rParticle, 2 * this.SQRT3 * rParticle / 3, nZMinus * 2 * this.SQRT6 / 3 * rParticle), rSphere))
				nZMinus--;
			break;
		}

		// Determine numbers of particles in each rows and in each layer
		int nMax = 0; // Max. particles between current center axis and edge in equatorial layer
		int nXLimit = nX;
		int[][] nParticleOctant1 = new int[nZPlus][nYPlus];
		int[][] nParticleOctant4 = new int[nZPlus][nYMinus];
		int[][] nParticleOctant5 = new int[nZMinus][nYPlus];
		int[][] nParticleOctant8 = new int[nZMinus][nYMinus];
		int nParticleInSphere = 0;
		int iLayer = 0;
		double x = 0;
		double y = 0;
		double z = 0;
		boolean isEvenRow;
		boolean hasnMaxChanged;
		// Octant 1
		nParticleOctant1[0][0] = nX;
		for (int i = 0; i < nZPlus; i++) {
			nMax = nXLimit;
			int jBegin;
			if (i == 0)
				jBegin = 1;
			else
				jBegin = 0;
			iLayer = i % 3;
			for (int j = jBegin; j < nYPlus; j++) {
				hasnMaxChanged = false;
				isEvenRow = this.IsEvenRow(j);
				do {
					switch (iLayer) {
					case 0: // A-Layer
						if (isEvenRow)
							x = d * (nMax - 1);
						else
							x = d * (nMax - 1) + rParticle;
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * j;
						break;
					case 1: // B-Layer
						if (isEvenRow)
							x = d * (nMax - 1) + rParticle;
						else
							x = d * (nMax - 1);
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (1.0 / 3 + j);
						break;
					case 2: // C_layer
						if (isEvenRow)
							x = d * (nMax - 1);
						else
							x = d * (nMax - 1) + rParticle;
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (2.0 / 3 + j);
						break;
					}
					if (!hasnMaxChanged)
						z = this.SQRT6 * 2.0 * rParticle / 3 * i;
					if (!this.IsInGlobe(new PointInSpace(x, y, z), rSphere)) {
						nMax--;
						hasnMaxChanged = true;
					} else
						break;
				} while (nMax > 0);
				if (nMax == 0)
					break;
				nParticleOctant1[i][j] = nMax;
				if (iLayer == 0 && j == nYPlus - 1)
					nXLimit = nParticleOctant1[i][0];
				if (iLayer == 1 && j == 0)
					nMax++;
			}
		}
		// Octant 4
		nXLimit = nX;
		for (int i = 0; i < nZPlus; i++) {
			nMax = nXLimit;
			iLayer = i % 3;
			for (int j = 0; j < nYMinus; j++) {
				hasnMaxChanged = false;
				isEvenRow = this.IsEvenRow(j);
				do {
					switch (iLayer) {
					case 0: // A-Layer
						if (isEvenRow)
							x = d * (nMax - 1) + rParticle;
						else
							x = d * (nMax - 1);
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (1 + j);
						break;

					case 1: // B-Layer
						if (isEvenRow)
							x = d * (nMax - 1);
						else
							x = d * (nMax - 1) + rParticle;
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (2.0/3.0 + j);
						break;
					case 2: // C_layer
						if (isEvenRow)
							x = d * (nMax - 1) + rParticle;
						else
							x = d * (nMax - 1);
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (1.0/3.0 + j);
						break;
					}
					if (!hasnMaxChanged)
						z = this.SQRT6 * 2.0 * rParticle / 3 * i;
					if (!this.IsInGlobe(new PointInSpace(x, y, z), rSphere)) {
						nMax--;
						hasnMaxChanged = true;
					} else
						break;
				} while (nMax > 0);
				if (nMax == 0)
					break;
				nParticleOctant4[i][j] = nMax;
				if (iLayer == 0 && j == nYMinus - 1)
					nXLimit = nParticleOctant1[i][0];
				if ((iLayer == 0 || iLayer == 2) && j == 0)
					nMax++;
			}
		}
		// Octant 5
		nXLimit = nX;
		for (int i = 0; i < nZMinus; i++) {
			nMax = nXLimit;
			iLayer = i % 3;
			for (int j = 0; j < nYPlus; j++) {
				hasnMaxChanged = false;
				isEvenRow = this.IsEvenRow(j);
				do {
					switch (iLayer) {
					case 0: // C-layer
						if (isEvenRow)
							x = d * (nMax - 1);
						else
							x = d * (nMax - 1) + rParticle;
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (2.0 / 3 + j);
						break;
					case 1: // B-Layer
						if (isEvenRow)
							x = d * (nMax - 1) + rParticle;
						else
							x = d * (nMax - 1);
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (1.0 / 3 + j);
						break;
					case 2: // A-Layer
						if (isEvenRow)
							x = d * (nMax - 1);
						else
							x = d * (nMax - 1) + rParticle;
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * j;
						break;
					}
					if (!hasnMaxChanged)
						z = this.SQRT6 * 2.0 * rParticle / 3 * (i + 1);
					if (!this.IsInGlobe(new PointInSpace(x, y, z), rSphere)) {
						nMax--;
						hasnMaxChanged = true;
					} else
						break;
				} while (nMax > 0);
				if (nMax == 0)
					break;
				nParticleOctant5[i][j] = nMax;
				if (iLayer == 0 && j == nYPlus - 1)
					nXLimit = nParticleOctant1[i][0];
				if (iLayer == 1 && j == 0)
					nMax++;
			}
		}
		// Octant 8
		nXLimit = nX;
		for (int i = 0; i < nZMinus; i++) {
			nMax = nXLimit;
			iLayer = i % 3;
			for (int j = 0; j < nYMinus; j++) {
				hasnMaxChanged = false;
				isEvenRow = this.IsEvenRow(j);
				do {
					switch (iLayer) {
					case 0: // C_layer
						if (isEvenRow)
							x = d * (nMax - 1) + rParticle;
						else
							x = d * (nMax - 1);
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (1.0 / 3 + j);
						break;
					case 1: // B-Layer
						if (isEvenRow)
							x = d * (nMax - 1);
						else
							x = d * (nMax - 1) + rParticle;
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (2.0 / 3 + j);
						break;
					case 2: // A-Layer
						if (isEvenRow)
							x = d * (nMax - 1) + rParticle;
						else
							x = d * (nMax - 1);
						if (!hasnMaxChanged)
							y = this.SQRT3 * rParticle * (j + 1);
						break;
					}
					if (!hasnMaxChanged)
						z = this.SQRT6 * 2.0 * rParticle / 3 * (i + 1);
					if (!this.IsInGlobe(new PointInSpace(x, y, z), rSphere)) {
						nMax--;
						hasnMaxChanged = true;
					} else
						break;
				} while (nMax > 0);
				if (nMax == 0)
					break;
				nParticleOctant8[i][j] = nMax;
				if (iLayer == 0 && j == nYMinus - 1)
					nXLimit = nParticleOctant1[i][0];
				if ((iLayer == 0 || iLayer == 2) && j == 0)
					nMax++;

			}
		}
		// Counts all spheres in globe
		for (int i = 0; i < nZPlus; i++) {
			switch (i % 3) {
			case 0:
				iLayer = 1;
				break;
			case 1:
				iLayer = 2;
				break;
			case 2:
				iLayer = 3;
				break;
			}
			// Octatnt 1
			for (int j = 0; j < nYPlus; j++) {
				if (nParticleOctant1[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				switch (iLayer) {
				case 1:
				case 3:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant1[i][j] - 1;
					else
						nParticleInSphere += 2 * nParticleOctant1[i][j];
					break;
				case 2:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant1[i][j];
					else
						nParticleInSphere += 2 * nParticleOctant1[i][j] - 1;
					break;
				}
			}
			// Octant 4
			for (int j = 0; j < nYMinus; j++) {
				if (nParticleOctant4[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				switch (iLayer) {
				case 1:
				case 3:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant4[i][j];
					else
						nParticleInSphere += 2 * nParticleOctant4[i][j] - 1;
					break;
				case 2:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant4[i][j] - 1;
					else
						nParticleInSphere += 2 * nParticleOctant4[i][j];
					break;
				}
			}
		}
		for (int i = 0; i < nZMinus; i++) {
			switch (i % 3) {
			case 0:
				iLayer = 3;
				break;
			case 1:
				iLayer = 2;
				break;
			case 2:
				iLayer = 1;
				break;
			}
			// Octatnt 5
			for (int j = 0; j < nYPlus; j++) {
				if (nParticleOctant5[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				switch (iLayer) {
				case 1:
				case 3:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant5[i][j] - 1;
					else
						nParticleInSphere += 2 * nParticleOctant5[i][j];
					break;
				case 2:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant5[i][j];
					else
						nParticleInSphere += 2 * nParticleOctant5[i][j] - 1;
					break;
				}
			}
			// Octant 8
			for (int j = 0; j < nYMinus; j++) {
				if (nParticleOctant8[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				switch (iLayer) {
				case 1:
				case 3:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant8[i][j];
					else
						nParticleInSphere += 2 * nParticleOctant8[i][j] - 1;
					break;
				case 2:
					if (isEvenRow)
						nParticleInSphere += 2 * nParticleOctant8[i][j] - 1;
					else
						nParticleInSphere += 2 * nParticleOctant8[i][j];
					break;
				}
			}
		}

		// Determine coordinates of the spheres
		PointInSpace[] coordinate = new PointInSpace[nParticleInSphere];
		int iSphere = 0;
		for (int i = 0; i < nZPlus; i++) {
			switch (i % 3) {
			case 0:
				iLayer = 1;
				break;
			case 1:
				iLayer = 2;
				break;
			case 2:
				iLayer = 3;
				break;
			}
			// Octatnt 1
			for (int j = 0; j < nYPlus; j++) {
				if (nParticleOctant1[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				for (int k = 0; k < nParticleOctant1[i][j]; k++) {
					switch (iLayer) {
					case 1:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
								iSphere++;
							}
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), this.SQRT3 * rParticle * j, d * this.SQRT6 / 3 * i);
							iSphere++;
						}
						break;
					case 2:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
								iSphere++;
							}
						}
						break;
					case 3:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
								iSphere++;
							}
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						}
						break;
					}
				}
			}
			// Octatnt 4
			for (int j = 0; j < nYMinus; j++) {
				if (nParticleOctant4[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				for (int k = 0; k < nParticleOctant4[i][j]; k++) {
					switch (iLayer) {
					case 1:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, -this.SQRT3 * rParticle * (1 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), -this.SQRT3 * rParticle * (1 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(d * k, -this.SQRT3 * rParticle * (1 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, -this.SQRT3 * rParticle * (1 + j), d * this.SQRT6 / 3 * i);
								iSphere++;
							}
						}
						break;
					case 2:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(d * k, -this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, -this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
								iSphere++;
							}
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, -this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), -this.SQRT3 * rParticle * (2.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						}
						break;
					case 3:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, -this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), -this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(d * k, -this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, -this.SQRT3 * rParticle * (1.0 / 3 + j), d * this.SQRT6 / 3 * i);
								iSphere++;
							}
						}
						break;
					}
				}
			}
		}
		for (int i = 0; i < nZMinus; i++) {
			switch (i % 3) {
			case 0:
				iLayer = 3;
				break;
			case 1:
				iLayer = 2;
				break;
			case 2:
				iLayer = 1;
				break;
			}
			// Octatnt 5
			for (int j = 0; j < nYPlus; j++) {
				if (nParticleOctant5[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				for (int k = 0; k < nParticleOctant5[i][j]; k++) {
					switch (iLayer) {
					case 1:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
								iSphere++;
							}
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
						}
						break;
					case 2:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
								iSphere++;
							}
						}
						break;
					case 3:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
								iSphere++;
							}
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
						}
						break;
					}
				}
			}
			// Octatnt 8
			for (int j = 0; j < nYMinus; j++) {
				if (nParticleOctant8[i][j] == 0)
					continue;
				isEvenRow = this.IsEvenRow(j);
				for (int k = 0; k < nParticleOctant8[i][j]; k++) {
					switch (iLayer) {
					case 1:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, -this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), -this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(d * k, -this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, -this.SQRT3 * rParticle * j, -d * this.SQRT6 / 3 * (i + 1));
								iSphere++;
							}
						}
						break;
					case 2:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(d * k, -this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, -this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
								iSphere++;
							}
						} else {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, -this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), -this.SQRT3 * rParticle * (2.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
						}
						break;
					case 3:
						if (isEvenRow) {
							coordinate[iSphere] = new PointInSpace(rParticle + d * k, -this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							coordinate[iSphere] = new PointInSpace(-(rParticle + d * k), -this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
						} else {
							coordinate[iSphere] = new PointInSpace(d * k, -this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
							iSphere++;
							if (k > 0) {
								coordinate[iSphere] = new PointInSpace(-d * k, -this.SQRT3 * rParticle * (1.0 / 3 + j), -d * this.SQRT6 / 3 * (i + 1));
								iSphere++;
							}
						}
						break;
					}
				}
			}
		}
		return coordinate;
	}

	/**
	 * Overloaded method
	 * 
	 * @param rParticle Radius of particle
	 * @param rSphere Radius of sphere
	 * @param startVector Start vector
	 * @return Points
	 */
	public PointInSpace[] particleCoordinatesFaceCenteredCubicInSphere(double rParticle, double rSphere, PointInSpace startVector){
		return this.moveVector(particleCoordinatesFaceCenteredCubicInSphere(rParticle, rSphere), startVector);
	}
	
	/**
	 * Determine whether the number of row is even or not
	 * 
	 * @param iRow
	 *            : number of row
	 * @return True, if the row number is even
	 */
	private boolean IsEvenRow(int iRow) {
		if (iRow % 2 == 0)
			return true;
		else
			return false;
	}

	/**
	 * Determine whether the point p is in the globe with radius r. The midpoint of sphere has the coordinate (0,0,0).
	 * 
	 * @param p
	 *            : x-, y- and z-coordinate of the point p r: radius of the sphere
	 * @return True, if the point p is in the globe with radius r, otherwise false.
	 */
	private boolean IsInGlobe(PointInSpace p, double rSphere) {
		if (rSphere <= 0)
			return false;
		if (Math.sqrt(p.getX() * p.getX() + p.getY() * p.getY() + p.getZ() * p.getZ()) <= rSphere)
			return true;
		return false;
	}
	
	/**
	 * Moves the coordinates by startVector
	 * 
	 * @param coordinates: coordinates of particles
	 * @param startVector: start vector of sphere (midpoint)
	 * @return coordinates within moved by the startVector
	 */
	private PointInSpace[] moveVector(PointInSpace[] coordinates, PointInSpace startVector){
		for (int i = 0; i < coordinates.length; i++){
			coordinates[i].setX(coordinates[i].getX() + startVector.getX());
			coordinates[i].setY(coordinates[i].getY() + startVector.getY());
			coordinates[i].setZ(coordinates[i].getZ() + startVector.getZ());
		}
		return coordinates;
	}

}

