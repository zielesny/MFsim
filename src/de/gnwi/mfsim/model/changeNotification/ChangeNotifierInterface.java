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
package de.gnwi.mfsim.model.changeNotification;
/**
 * Change notifier interface
 *
 * @author Achim Zielesny
 */
public interface ChangeNotifierInterface {

    // <editor-fold defaultstate="collapsed" desc="Public external methods">
    /**
     * Adds a change receiver
     *
     * @param aChangeReceiver Change receiver
     * @throws IllegalStateException Thrown when change receiver operation is locked
     */
    void addChangeReceiver(ChangeReceiverInterface aChangeReceiver) throws IllegalStateException;

    /**
     * Removes all change receivers
     *
     * @throws IllegalStateException Thrown when change receiver operation is locked
     */
    void removeAllChangeReceivers() throws IllegalStateException;

    /**
     * Removes a change receiver
     *
     * @param aChangeReceiver Change receiver
     * @throws IllegalStateException Thrown when change receiver operation is locked
     */
    void removeSingleChangeReceiver(ChangeReceiverInterface aChangeReceiver) throws IllegalStateException;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * True: Notification is suppressed, false: Otherwise
     *
     * @return True: Notification is suppressed, false: Otherwise
     */
    boolean isNotificationSuppressed();

    /**
     * True: Notification is suppressed, false: Otherwise
     *
     * @param aValue Value
     */
    void setNotificationSuppressed(boolean aValue);
    // </editor-fold>
}
