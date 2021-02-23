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
package de.gnwi.mfsim.model.particleStructure;
/**
 * Particle information
 * 
 * @author Achim Zielesny
 */
public class ParticleInfo implements Comparable<ParticleInfo> {

	// <editor-fold defaultstate="collapsed" desc="Private class variables">

	/**
	 * Particle
	 */
	private String particle;

	/**
	 * Volume of particle
	 */
	private double volume;

	/**
	 * Molar weight of particle
	 */
	private double molarWeight;

	/**
	 * Scaling factor
	 */
	private double scalingFactor;

	// </editor-fold>
	//
	// <editor-fold defaultstate="collapsed" desc="Constructor">

	/**
	 * Constructor
	 * 
	 * @param aParticle
	 *            Particle (not allowed to be null/empty)
	 * @param aVolume
	 *            Volume of particle (must be greater 0)
	 * @param aMolarWeight
	 *            Molar weight of particle (must be greater 0)
	 * @throws IllegalArgumentException
	 *             Thrown if an argument is illegal
	 */
	public ParticleInfo(String aParticle, double aVolume, double aMolarWeight) throws IllegalArgumentException {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aParticle == null || aParticle.isEmpty() || aVolume < 0 || aMolarWeight < 0) {
			throw new IllegalArgumentException("An argument is illegal.");
		}

		// </editor-fold>

		this.particle = aParticle;
		this.volume = aVolume;
		this.molarWeight = aMolarWeight;
		this.scalingFactor = 0;
	}

	// </editor-fold>
	//
	// <editor-fold defaultstate="collapsed" desc="Public methods">

	/**
	 * Standard compareTo
	 * 
	 * @param aParticleInfo
	 *            Particle number to compare
	 * @return Standard compareTo result
	 */
	public int compareTo(ParticleInfo aParticleInfo) {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aParticleInfo == null) {
			throw new IllegalArgumentException("aParticleInfo is null.");
		}

		// </editor-fold>

		return this.particle.compareTo(aParticleInfo.getParticle());
	}

	// </editor-fold>
	//
	// <editor-fold defaultstate="collapsed" desc="Public properties">
	// <editor-fold defaultstate="collapsed" desc="- Particle (get)">

	/**
	 * Particle
	 * 
	 * @return Particle
	 */
	public String getParticle() {
		return this.particle;
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="- Volume (get)">

	/**
	 * Volume of particle
	 * 
	 * @return Volume of particle
	 */
	public double getVolume() {
		return this.volume;
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="- MolarWeight (get)">

	/**
	 * Molar weight of particle
	 * 
	 * @return Molar weight of particle
	 */
	public double getMolarWeight() {
		return this.molarWeight;
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="- ScalingFactor (get/set)">
	/**
	 * Scaling factor
	 * 
	 * @return Scaling factor
	 */
	public double getScalingFactor() {
		return this.scalingFactor;
	}
	/**
	 * Scaling factor
	 * 
	 * @param aScalingFactor
	 *            Scaling factor
	 */
	public void setScalingFactor(double aScalingFactor) {
		this.scalingFactor = aScalingFactor;
	}
	// </editor-fold>
	// </editor-fold>

}
