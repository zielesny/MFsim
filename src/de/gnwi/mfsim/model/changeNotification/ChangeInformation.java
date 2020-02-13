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
package de.gnwi.mfsim.model.changeNotification;
/**
 * Change information
 *
 * @author Achim Zielesny
 */
public class ChangeInformation {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Change type
     */
    private ChangeTypeEnum changeType;
    /**
     * Change informantion
     */
    private Object info;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     */
    public ChangeInformation() {
        this.changeType = ChangeTypeEnum.NONE;
        this.info = null;
    }

    /**
     * Constructor
     *
     * @param aChangeType Change type
     * @param aChangeInfo Change information
     */
    public ChangeInformation(ChangeTypeEnum aChangeType, Object aChangeInfo) {
        this.changeType = aChangeType;
        this.info = aChangeInfo;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * Change type
     *
     * @return Change type
     */
    public ChangeTypeEnum getChangeType() {
        return this.changeType;
    }

    /**
     * Change type
     *
     * @param aChangeType Change type
     */
    public void setChangeType(ChangeTypeEnum aChangeType) {
        this.changeType = aChangeType;
    }

    /**
     * Change information
     *
     * @return Change information
     */
    public Object getInfo() {
        return this.info;
    }

    /**
     * Change information
     *
     * @param anInfo Change information
     */
    public void setInfo(Object anInfo) {
        this.info = anInfo;
    }
    // </editor-fold>
}
