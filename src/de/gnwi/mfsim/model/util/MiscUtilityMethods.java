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

import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Miscellaneous utility methods to be instantiated
 *
 * @author Achim Zielesny
 */
public class MiscUtilityMethods {

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public MiscUtilityMethods() {
        // Do nothing
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Encryption related methods">
    /**
     * Encrypts/decrypts byte array with key.
     *
     * @param aByteArray Byte array
     * @param aKeyByteArray Key byte array
     * @return Encrypted/decrypted byte array with same length as aByteArray or
     * null if encrypted byte array could not be created
     */
    public byte[] encrypt(byte[] aByteArray, byte[] aKeyByteArray) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aByteArray == null || aByteArray.length == 0 || aKeyByteArray == null || aKeyByteArray.length == 0) {
            return null;
        }

        // </editor-fold>
        byte[] tmpEncryptedByteArray = new byte[aByteArray.length];
        int tmpLength = aKeyByteArray.length;
        for (int i = 0; i < aByteArray.length; i++) {
            tmpEncryptedByteArray[i] = (byte) (aByteArray[i] ^ aKeyByteArray[i % tmpLength]);
        }
        return tmpEncryptedByteArray;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns fraction aCurrentValue of aTotalValue in percent (0 to 100,
     * without decimals)
     *
     * @param aCurrentValue Current value
     * @param aTotalValue Total value
     * @return Fraction aCurrentValue of aTotalValue in percent (0 to 100,
     * without decimals) or -1 if fraction can not be calculated
     */
    public int getPercent(int aCurrentValue, int aTotalValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTotalValue == 0) {
            return -1;
        }
        if (Math.abs(aCurrentValue) > Math.abs(aTotalValue)) {
            return 100;
        }
        // </editor-fold>
        return Math.round((float) Math.abs(aCurrentValue) / (float) Math.abs(aTotalValue) * 100f);
    }

    /**
     * Returns fraction aCurrentValue of aTotalValue in percent (0 to 99,
     * without decimals). NOTE: Maximum return value is 99, also if 100 percent
     * is determined.
     *
     * @param aCurrentValue Current value
     * @param aTotalValue Total value
     * @return Fraction aCurrentValue of aTotalValue in percent (0 to 99,
     * without decimals) or -1 if fraction can not be calculated
     */
    public int getPercentWithMax99(int aCurrentValue, int aTotalValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTotalValue == 0) {
            return -1;
        }
        if (Math.abs(aCurrentValue) > Math.abs(aTotalValue)) {
            return 99;
        }
        // </editor-fold>
        int tmpPercent = Math.round((float) Math.abs(aCurrentValue) / (float) Math.abs(aTotalValue) * 100f);
        if (tmpPercent > 99) {
            return 99;
        } else {
            return tmpPercent;
        }
    }

    /**
     * Cuts aValue after specified number of decimals. NOTE: No checks are
     * performed.
     *
     * @param aValue Value
     * @param aNumberOfDecimals Number of decimals
     * @return Rounded value
     */
    public double cutDoubleValue(double aValue, int aNumberOfDecimals) {
        double tmpFactor = Math.pow(10, aNumberOfDecimals);
        return Math.floor(aValue * tmpFactor) / tmpFactor;
    }

    /**
     * Calculates the radius of a sphere in DPD units with specified number of
     * particles in the DPD simulation box
     *
     * @param aNumberOfParticles Number of particles
     * @param aDpdDensity DPD density
     * @return Radius of a sphere with specified number of particles in the DPD
     * simulation box or 0.0 if radius can not be calculated
     */
    public double getRadiusOfParticlesInDpdBox(int aNumberOfParticles, double aDpdDensity) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfParticles <= 0) {
            return 0.0;
        }
        if (aDpdDensity <= 0.0) {
            return 0.0;
        }

        // </editor-fold>
        try {
            // Volume = Number/Density
            double tmpVolumeInBox = (double) aNumberOfParticles / aDpdDensity;
            // Volume = 4/3*PI*Radius^3
            return Math.cbrt(ModelDefinitions.FACTOR_3_DIV_4_PI * tmpVolumeInBox);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return 0.0;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- String array related methods">
    /**
     * Returns number of elements in specified jagged array of strings
     *
     * @param aJaggedArrayOfStrings Jagged array of strings
     * @return Number of elements in specified jagged array of strings (if no
     * strings are found zero is returned)
     */
    public int getNumberOfElementsOfJaggedArrayOfStrings(String[][] aJaggedArrayOfStrings) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJaggedArrayOfStrings == null) {
            return 0;
        }
        if (aJaggedArrayOfStrings.length == 0) {
            return 0;
        }

        // </editor-fold>
        int tmpCounter = 0;
        for (int i = 0; i < aJaggedArrayOfStrings.length; i++) {
            if (aJaggedArrayOfStrings[i] != null) {
                tmpCounter += aJaggedArrayOfStrings[i].length;
            }
        }
        return tmpCounter;
    }
    
    /**
     * Removes specified element from string array
     * 
     * @param aStringArray String array (is NOT changed but may be returned, see code)
     * @param anElementToBeRemoved String element to be removed
     * @return New array where specified element is removed (may be a String array of length 0 if all elements of aStringArray had to be removed)
     */
    public String[] removeElementFromStringArray(String[] aStringArray, String anElementToBeRemoved) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0) {
            return aStringArray;
        }
        if (anElementToBeRemoved == null || anElementToBeRemoved.isEmpty()) {
            return aStringArray;
        }
        // </editor-fold>
        LinkedList<String> tmpList = new LinkedList<>();
        for (int i = 0; i < aStringArray.length; i++) {
            if (!anElementToBeRemoved.equals(aStringArray[i])) {
                tmpList.add(aStringArray[i]);
            }
        }
        if (tmpList.isEmpty()) {
            return new String[0];
        } else {
            return tmpList.toArray(new String[0]);
        }
    }

    /**
     * Removes elements up to (inclusive!) specified element from string array
     * 
     * @param aStringArray String array (is NOT changed but may be returned, see code)
     * @param anElementToBeRemovedUpToInclusive String element to be removed up to inclusive
     * @return New array where up to (inclusive!) specified element is removed (may be a String array of length 0 if all elements of aStringArray had to be removed)
     */
    public String[] removeUpToElementFromStringArray(String[] aStringArray, String anElementToBeRemovedUpToInclusive) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0) {
            return aStringArray;
        }
        if (anElementToBeRemovedUpToInclusive == null || anElementToBeRemovedUpToInclusive.isEmpty()) {
            return aStringArray;
        }
        // </editor-fold>
        LinkedList<String> tmpList = new LinkedList<>();
        boolean tmpIsRemoved = true;
        for (int i = 0; i < aStringArray.length; i++) {
            if (!tmpIsRemoved) {
                tmpList.add(aStringArray[i]);
            }
            if (tmpIsRemoved && anElementToBeRemovedUpToInclusive.equals(aStringArray[i])) {
                tmpIsRemoved = false;
            }
        }
        if (tmpList.isEmpty()) {
            return new String[0];
        } else {
            return tmpList.toArray(new String[0]);
        }
    }
    
    /**
     * Removes first element from string array
     * 
     * @param aStringArray String array (is NOT changed)
     * @return New array where first element is removed (may be null if aStringArray is null or has length less/equal 1)
     */
    public String[] removeFirstElementFromStringArray(String[] aStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length <= 1) {
            return null;
        }
        // </editor-fold>
        String[] newStringArray = new String[aStringArray.length - 1];
        for (int i = 1; i < aStringArray.length; i++) {
            newStringArray[i - 1] = aStringArray[i];
        }
        return newStringArray;
    }

    /**
     * Removes last element from string array
     * 
     * @param aStringArray String array (is NOT changed)
     * @return New array where last element is removed (may be null if aStringArray is null or has length less/equal 1)
     */
    public String[] removeLastElementFromStringArray(String[] aStringArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length <= 1) {
            return null;
        }
        // </editor-fold>
        String[] newStringArray = new String[aStringArray.length - 1];
        for (int i = 0; i < aStringArray.length - 1; i++) {
            newStringArray[i] = aStringArray[i];
        }
        return newStringArray;
    }

    /**
     * Returns if aStringArray contains anElement
     * 
     * @param aStringArray String array
     * @param anElement Element
     * @return True: aStringArray contains anElement, false: Otherwise
     */
    public boolean containsElement(String[] aStringArray, String anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray == null || aStringArray.length == 0) {
            return false;
        }
        if (anElement == null || anElement.isEmpty()) {
            return false;
        }
        // </editor-fold>
        for (String tmpSingleElement : aStringArray) {
            if (tmpSingleElement.equals(anElement)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns if both string arrays have equal strings. NOTE: If one of the
     * arrays is null or has length 0 false is returned.
     * 
     * @param aStringArray1 String array 1
     * @param aStringArray2 String array 2
     * @return True: Both arrays have equal strings, false: Otherwise
     */
    public boolean areStringArraysEqual(String[] aStringArray1, String[] aStringArray2) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStringArray1 == null || aStringArray1.length == 0) {
            return false;
        }
        if (aStringArray2 == null || aStringArray2.length == 0) {
            return false;
        }
        if (aStringArray1.length != aStringArray2.length) {
            return false;
        }
        // </editor-fold>
        for (int i = 0; i < aStringArray1.length; i++) {
            if (!aStringArray1[i].equals(aStringArray2[i])) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns index string array according to upper index, i.e. {"1", "2", "3"}
     * for anUpperIndex = 3.
     * @param anUpperIndex Upper index
     * @return Index string array or null if anUpperIndex is less than 1. 
     */
    public String[] getIndexStringArray(int anUpperIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anUpperIndex < 1) {
            return null;
        }
        // </editor-fold>
        String[] tmpIndexStringArray = new String[anUpperIndex]; 
        for (int i = 0; i < anUpperIndex; i++) {
            tmpIndexStringArray[i] = String.valueOf(i + 1);
        }
        return tmpIndexStringArray;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Generic sort methods">
    /**
     * Sorts array according to aComparator. 
     * Implements "Heapsort" algorithm based on 
     * W.H. Press et al., Numerical recipes in FORTRAN 77, Cambridge
     * University Press, 1992
     *
     * @param <T> Object provided comparator class
     * @param anArray Array to be sorted (may be null then nothing is done)
     * @param aComparator Comparator class for objects of anArray
     */
    public <T> void sortGenericArray(
        T[] anArray, 
        Comparator<T> aComparator
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray == null) {
            return;
        }
        // </editor-fold>
        this.sortGenericArray(anArray, anArray.length, aComparator);
    }
    
    /**
     * Sorts array according to aComparator. 
     * Implements "Heapsort" algorithm based on 
     * W.H. Press et al., Numerical recipes in FORTRAN 77, Cambridge
     * University Press, 1992
     *
     * @param <T> Object provided comparator class
     * @param anArray Array to be sorted (may be null then nothing is done, length must be greater/equal to anArrayLength)
     * @param anArrayLength Length of initial part of anArray to be sorted
     * @param aComparator Comparator class for objects of anArray
     * @exception IllegalArgumentException Thrown if anArrayLength is greater than length of anArray
     */
    public <T> void sortGenericArray(
        T[] anArray,
        int anArrayLength,
        Comparator<T> aComparator
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anArray == null) {
            return;
        }
        if (anArrayLength <= 1) {
            return;
        }
        if (anArray.length < anArrayLength) {
            throw new IllegalArgumentException("UtilityMiscMethods.sortGenericArray: anArray.length < anArrayLength");
        }
        // </editor-fold>
        int i;
        int j;
        int k;
        int l;
        T tmpElement;

        k = anArrayLength / 2 + 1;
        l = anArrayLength;
        do {
            if (k > 1) {
                k--;
                tmpElement = anArray[k - 1];
            } else {
                tmpElement = anArray[l - 1];
                anArray[l - 1] = anArray[0];
                l--;
                if (l == 1) {
                    anArray[0] = tmpElement;
                    return;
                }
            }
            i = k;
            j = k + k;
            while (j <= l) {
                if (j < l) {
                    if (aComparator.compare(anArray[j - 1], anArray[j]) < 0) {
                        j++;
                    }
                }
                if (aComparator.compare(tmpElement, anArray[j - 1]) < 0) {
                    anArray[i - 1] = anArray[j - 1];
                    i = j;
                    j = j + j;
                } else {
                    j = l + 1;
                }
            }
            anArray[i - 1] = tmpElement;
        } while (true);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Random number related methods">
    /**
     * Returns random number generator according to definitions
     *
     * @return Random number generator
     */
    public Random getRandom() {
        if (Preferences.getInstance().isDeterministicRandom()) {
            return new Random(Preferences.getInstance().getDeterministicRandomSeed());
        } else {
            return new Random();
        }
    }
    // </editor-fold>
    // </editor-fold>

}
