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

import java.util.LinkedList;

/**
 * Change notifier
 *
 * @author Achim Zielesny
 */
public class ChangeNotifier implements ChangeNotifierInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Change receiver list
     */
    private LinkedList<ChangeReceiverInterface> changeReceiverList;

    /**
     * True: Change receiver operation is locked, false: Otherwise
     */
    private boolean isChangeReceiverOperationLocked;

    /**
     * True: Notification is suppressed, false: Otherwise
     */
    private boolean isNotificationSuppressed;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     */
    public ChangeNotifier() {
        this.changeReceiverList = new LinkedList<ChangeReceiverInterface>();
        this.isChangeReceiverOperationLocked = false;
        this.isNotificationSuppressed = false;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public external methods">
    /**
     * Adds a change receiver
     *
     * @param aChangeReceiver Change receiver
     * @throws IllegalStateException Thrown when change receiver operation is locked
     */
    public void addChangeReceiver(ChangeReceiverInterface aChangeReceiver) throws IllegalStateException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.isChangeReceiverOperationLocked) {
            throw new IllegalStateException("Change receiver operation is locked.");
        }
        if (aChangeReceiver == null) {
            return;
        }
        for (ChangeReceiverInterface tmpSingleChangeReceiver : this.changeReceiverList) {
            if (tmpSingleChangeReceiver == aChangeReceiver) {
                return;
            }
        }

        // </editor-fold>
        this.changeReceiverList.addLast(aChangeReceiver);
    }

    /**
     * Removes all change receivers
     *
     * @throws IllegalStateException Thrown when change receiver operation is locked
     */
    public void removeAllChangeReceivers() throws IllegalStateException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.isChangeReceiverOperationLocked) {
            throw new IllegalStateException("Change receiver operation is locked.");
        }

        // </editor-fold>
        this.changeReceiverList.clear();
    }

    /**
     * Removes a change receiver
     *
     * @param aChangeReceiver Change receiver
     * @throws IllegalStateException Thrown when change receiver operation is locked
     */
    public void removeSingleChangeReceiver(ChangeReceiverInterface aChangeReceiver) throws IllegalStateException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.isChangeReceiverOperationLocked) {
            throw new IllegalStateException("Change receiver operation is locked.");
        }
        if (aChangeReceiver == null) {
            return;
        }

        // </editor-fold>
        for (int i = 0; i < this.changeReceiverList.size(); i++) {
            if (this.changeReceiverList.get(i) == aChangeReceiver) {
                this.changeReceiverList.remove(i);
                break;
            }
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Protected internal methods">
    /**
     * Notifies change receivers about a change
     *
     * @param aChangeNotifier Change notifier
     * @param aChangeInfo Change
     */
    protected void notifyChangeReceiver(Object aChangeNotifier, ChangeInformation aChangeInfo) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.changeReceiverList.size() == 0) {
            return;
        }
        if (this.isNotificationSuppressed) {
            return;
        }

        // </editor-fold>
        // IMPORTANT: Lock operations
        this.isChangeReceiverOperationLocked = true;
        for (ChangeReceiverInterface tmpSingleChangeReceiver : this.changeReceiverList) {
            if (tmpSingleChangeReceiver != null) {
                tmpSingleChangeReceiver.notifyChange(aChangeNotifier, aChangeInfo);
            }
        }
        this.isChangeReceiverOperationLocked = false;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * True: Notification is suppressed, false: Otherwise
     *
     * @return True: Notification is suppressed, false: Otherwise
     */
    public boolean isNotificationSuppressed() {
        return this.isNotificationSuppressed;
    }

    /**
     * True: Notification is suppressed, false: Otherwise
     *
     * @param aValue Value
     */
    public void setNotificationSuppressed(boolean aValue) {
        this.isNotificationSuppressed = aValue;
    }
    // </editor-fold>

}
