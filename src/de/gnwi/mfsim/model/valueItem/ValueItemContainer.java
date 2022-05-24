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

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Value item container
 *
 * @author Achim Zielesny
 */
public class ValueItemContainer extends ChangeNotifier implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility string methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Change information
     */
    private ChangeInformation changeInformation;

    /**
     * Update notification object
     */
    private ValueItemUpdateNotifierInterface updateNotificationObject;

    /**
     * HashMap for value item name to value item object mapping
     */
    private HashMap<String, ValueItem> nameToValueItemMap;

    /**
     * Cash for value items of nameToValueItemMap
     */
    private ValueItem[] cashedValueItemArray;

    /**
     * Cash for name sorted value items of nameToValueItemMap
     */
    private ValueItem[] cashedNameSortedValueItemArray;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param anUpdateNotificationObject Value item update notification object
     * (may be null)
     */
    public ValueItemContainer(ValueItemUpdateNotifierInterface anUpdateNotificationObject) {
        super();
        this.initialize();
        this.updateNotificationObject = anUpdateNotificationObject;
    }

    /**
     * Constructor
     *
     * @param aXmlString XML representation of value item container
     * @param anUpdateNotificationObject Value item update notification object
     * (may be null)
     * @throws IllegalArgumentException Thrown when aXmlString is null/empty or
     * can not be read
     */
    public ValueItemContainer(String aXmlString, ValueItemUpdateNotifierInterface anUpdateNotificationObject) throws IllegalArgumentException {
        super();

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXmlString == null || aXmlString.isEmpty()) {
            throw new IllegalArgumentException("aXmlString is null/empty.");
        }

        // </editor-fold>
        this.initialize();
        try {
            if (!this.readXmlInformation(new SAXBuilder().build(new StringReader(aXmlString)).getRootElement())) {
                throw new IllegalArgumentException("Invalid XML string.");
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new IllegalArgumentException("Invalid XML string.");
        }
        this.updateNotificationObject = anUpdateNotificationObject;
    }

    /**
     * Constructor
     *
     * @param aXmlElement The XML element containing the data of value item
     * container
     * @param anUpdateNotificationObject Value item update notification object
     * (may be null)
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemContainer(Element aXmlElement, ValueItemUpdateNotifierInterface anUpdateNotificationObject) throws IllegalArgumentException {
        super();
        this.initialize();
        if (!this.readXmlInformation(aXmlElement)) {
            throw new IllegalArgumentException("Can not read XML information.");
        }
        this.updateNotificationObject = anUpdateNotificationObject;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a change receiver
     *
     * @param aChangeNotifier Object that notifies change
     * @param aChangeInfo Change information
     */
    @Override
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        super.notifyChangeReceiver(this, aChangeInfo);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Clear method">
    /**
     * Clears the container. NOTE: This method does NOT change
     * this.updateNotificationObject.
     */
    public void clear() {
        this.initialize();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns clone of this instance
     *
     * @return Clone of this instance
     */
    public ValueItemContainer getClone() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(this.updateNotificationObject);
        if (this.nameToValueItemMap.size() > 0) {
            for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
                tmpValueItemContainer.addValueItem(tmpValueItem.getClone());
            }
        }
        return tmpValueItemContainer;
    }

    /**
     * Add index to each value item name
     *
     * @param anIndex Index greater/equal 0
     */
    public void addIndexToValueItemNames(int anIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIndex < 0) {
            return;
        }

        // </editor-fold>
        String tmpIndexValue = String.valueOf(anIndex).trim();
        ValueItem[] tmpValueItems = this.getValueItemsOfContainer();
        for (ValueItem tmpSingleValueItem : tmpValueItems) {
            tmpSingleValueItem.setName(tmpSingleValueItem.getName() + "_" + tmpIndexValue);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Value item related methods">
    // <editor-fold defaultstate="collapsed" desc="-- Change receiver related methods">
    /**
     * Adds a single change receiver to all value items
     *
     * @param aChangeReceiver Change receiver
     */
    public void addSingleChangeReceiverToValueItems(ChangeReceiverInterface aChangeReceiver) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return;
        }

        // </editor-fold>
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            tmpSingleValueItem.addChangeReceiver(aChangeReceiver);
        }
    }

    /**
     * Removes all change receivers of all value items
     */
    public void removeAllChangeReceiversOfValueItems() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return;
        }

        // </editor-fold>
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            tmpSingleValueItem.removeAllChangeReceivers();
        }
        // Set this container as a change receiver of all value items
        this.setThisAsChangeReceiverForValueItems();
    }

    /**
     * Removes single change receiver of all value items
     *
     * @param aChangeReceiver Change receiver
     */
    public void removeSingleChangeReceiverOfValueItems(ChangeReceiverInterface aChangeReceiver) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return;
        }

        // </editor-fold>
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            tmpSingleValueItem.removeSingleChangeReceiver(aChangeReceiver);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Status related methods">
    /**
     * True: At least one value item of container has specified status AND is
     * displayed, false: Otherwise. NOTE: Slow sequential implementation since
     * number of value items is O(10).
     *
     * @param aStatus Value item status
     * @return True: At least one value item of container has specified status
     * AND is displayed, false: Otherwise
     */
    public boolean hasStatus(ValueItemEnumStatus aStatus) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return false;
        }

        // </editor-fold>
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            if (tmpSingleValueItem.hasStatus(aStatus) && tmpSingleValueItem.isDisplayed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if at least one value item of container has an error AND is
     * displayed
     *
     * @return True: At least one value item of container has an error AND is
     * displayed, false: Otherwise
     */
    public boolean hasError() {
        return this.hasStatus(ValueItemEnumStatus.HAS_ERROR);
    }

    /**
     * Returns if at least one value item of container has a hint AND is
     * displayed
     *
     * @return True: At least one value item of container has a hint AND is
     * displayed, false: Otherwise
     */
    public boolean hasHint() {
        return this.hasStatus(ValueItemEnumStatus.HAS_HINT);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Block related methods">
    /**
     * Returns list with names of all blocks of all value items. NOTE: Slow
     * quadratic implementation.
     *
     * @return List with names of all blocks of all value items or null if no
     * value item with a block name exists
     */
    public LinkedList<String> getBlockNames() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return null;
        }

        // </editor-fold>
        LinkedList<String> tmpBlockNameList = new LinkedList<String>();
        for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
            if (!tmpBlockNameList.contains(tmpValueItem.getBlockName())) {
                tmpBlockNameList.addLast(tmpValueItem.getBlockName());
            }
        }
        if (tmpBlockNameList.size() == 0) {
            return null;
        } else {
            return tmpBlockNameList;
        }
    }

    /**
     * Returns sorted value items of specified block (NOTE: Slow sequential
     * implementation: Number of value items in the container may not exceed
     * O(10))
     *
     * @param aBlockName Name of block
     * @return Sorted value items of specified block or null if no value item
     * with specified block name exists
     */
    public LinkedList<ValueItem> getSortedValueItemsOfBlock(String aBlockName) {
        return ValueItemUtils.sortValueItems(this.getValueItemsOfBlock(aBlockName));
    }

    /**
     * Returns sorted value items of specified block with specified status
     * (NOTE: Slow sequential implementation: Number of value items in the
     * container may not exceed O(10))
     *
     * @param aBlockName Name of block
     * @param aStatus Status of value item
     * @return Sorted value items of specified block with specified status or
     * null if no value item with specified block name exists
     */
    public LinkedList<ValueItem> getSortedValueItemsOfBlockWithStatus(String aBlockName, ValueItemEnumStatus aStatus) {
        LinkedList<ValueItem> tmpSortedValueItemsOfBlock = ValueItemUtils.sortValueItems(this.getValueItemsOfBlock(aBlockName));
        LinkedList<ValueItem> tmpValueItemOfBlockWithStatusList = new LinkedList<ValueItem>();
        for (ValueItem tmpSingleValueItem : tmpSortedValueItemsOfBlock) {
            if (tmpSingleValueItem.hasStatus(aStatus)) {
                tmpValueItemOfBlockWithStatusList.add(tmpSingleValueItem);
            }
        }
        return tmpValueItemOfBlockWithStatusList;
    }

    /**
     * Returns value items of specified block (NOTE: Slow sequential
     * implementation: Number of value items in the container may not exceed
     * O(10))
     *
     * @param aBlockName Name of block
     * @return Value items of specified block or null if no value item with
     * specified block name exists
     */
    public LinkedList<ValueItem> getValueItemsOfBlock(String aBlockName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return null;
        }
        if (this.nameToValueItemMap.size() == 0) {
            return null;
        }

        // </editor-fold>
        LinkedList<ValueItem> tmpValueItemList = new LinkedList<ValueItem>();
        for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
            if (tmpValueItem.getBlockName().equals(aBlockName)) {
                tmpValueItemList.addLast(tmpValueItem);
            }
        }
        if (tmpValueItemList.size() == 0) {
            return null;
        } else {
            return tmpValueItemList;
        }
    }

    /**
     * Returns first matching value item of specified block
     *
     * @param aBlockName Name of block
     * @param aValueItemNamePrefix Prefix of name of value item
     * @return First matching value item of specified block or null if none is
     * found
     */
    public ValueItem getValueItemOfBlock(String aBlockName, String aValueItemNamePrefix) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return null;
        }
        if (aValueItemNamePrefix == null || aValueItemNamePrefix.isEmpty()) {
            return null;
        }
        if (this.nameToValueItemMap.isEmpty()) {
            return null;
        }

        // </editor-fold>
        LinkedList<ValueItem> tmpValueItemList = this.getValueItemsOfBlock(aBlockName);
        if (tmpValueItemList != null) {
            for (ValueItem tmpValueItem : tmpValueItemList) {
                if (tmpValueItem.getName().startsWith(aValueItemNamePrefix)) {
                    return tmpValueItem;
                }
            }
        }
        return null;
    }

    /**
     * Removes value items of specified block from container. NOTE: Change in
     * number of value items is notified to change receivers if occurs.
     *
     * @param aBlockName Name of block
     * @return True: Operation changed container, false: Container is unchanged
     */
    public boolean removeValueItemsOfBlock(String aBlockName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBlockName == null || aBlockName.isEmpty()) {
            return false;
        }
        if (this.nameToValueItemMap.size() == 0) {
            return false;
        }

        // </editor-fold>
        LinkedList<ValueItem> tmpRemoveList = new LinkedList<ValueItem>();
        for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
            if (tmpValueItem.getBlockName().equals(aBlockName)) {
                tmpRemoveList.addLast(tmpValueItem);
            }
        }
        if (tmpRemoveList.size() > 0) {
            for (ValueItem tmpValueItemToBeRemoved : tmpRemoveList) {
                tmpValueItemToBeRemoved.removeSingleChangeReceiver(this);
                tmpValueItemToBeRemoved.removeValueItemContainer();
                this.nameToValueItemMap.remove(tmpValueItemToBeRemoved.getName());
            }
            // Clear value item cashes
            this.cashedValueItemArray = null;
            this.cashedNameSortedValueItemArray = null;
            this.changeInformation.setChangeType(ChangeTypeEnum.VALUE_ITEM_CONTAINER_NUMBER_CHANGE);
            super.notifyChangeReceiver(this, this.changeInformation);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sorts value items according to their block names. Within blocks value
     * items retain their original order. NOTE: Sort change is notified to
     * change receivers.
     *
     * @param anIsNotificationSuppressed True: Notification of change receivers
     * is suppressed, false: Otherwise
     */
    public void sortBlocks(boolean anIsNotificationSuppressed) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return;
        }

        // </editor-fold>
        LinkedList<String> tmpBlockNameList = this.getBlockNames();
        Collections.sort(tmpBlockNameList);
        int tmpIndex = 0;
        for (String tmpBlockName : tmpBlockNameList) {
            LinkedList<ValueItem> tmpValueItemList = this.getSortedValueItemsOfBlock(tmpBlockName);
            for (ValueItem tmpValueItem : tmpValueItemList) {
                tmpValueItem.setVerticalPosition(tmpIndex++);
            }
        }
        if (!anIsNotificationSuppressed) {
            // Notify about sort change
            this.changeInformation.setChangeType(ChangeTypeEnum.VALUE_ITEM_CONTAINER_SORT_CHANGE);
            super.notifyChangeReceiver(this, this.changeInformation);
        }
    }

    /**
     * Sets specified display of value items of specified block
     *
     * @param aBlockName Name of block
     * @param anIsDisplayed True: Value items of block are displayed, false:
     * Otherwise
     */
    public void setValueItemDisplayOfBlock(String aBlockName, boolean anIsDisplayed) {
        LinkedList<ValueItem> tmpValueItemList = this.getValueItemsOfBlock(aBlockName);
        for (ValueItem tmpSingleValueItem : tmpValueItemList) {
            tmpSingleValueItem.setDisplay(anIsDisplayed);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Display related methods">
    /**
     * Disables display of value item except those in
     * aValueItemNameMapForDisplay
     *
     * @param aValueItemNameMapForDisplay Map with value item names to be
     * displayed
     * @return Map with names of value items with disabled display or null if no
     * value item was disabled
     */
    public HashMap<String, String> setDefinedDisplay(HashMap<String, String> aValueItemNameMapForDisplay) {
        if (aValueItemNameMapForDisplay != null && aValueItemNameMapForDisplay.size() > 0) {
            HashMap<String, String> tmpDisabledDisplayValueItems = new HashMap<String, String>(aValueItemNameMapForDisplay.size());
            for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
                if (!aValueItemNameMapForDisplay.containsKey(tmpSingleValueItem.getName())) {
                    tmpSingleValueItem.setDisplay(false);
                    tmpDisabledDisplayValueItems.put(tmpSingleValueItem.getName(), tmpSingleValueItem.getName());
                }
            }
            if (tmpDisabledDisplayValueItems.size() > 0) {
                return tmpDisabledDisplayValueItems;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Enables display of value item in aDisabledDisplayValueItems
     *
     * @param aDisabledDisplayValueItems Map with names of value items with
     * disabled display (may be null)
     */
    public void restoreDefinedDisplay(HashMap<String, String> aDisabledDisplayValueItems) {
        if (aDisabledDisplayValueItems != null && aDisabledDisplayValueItems.size() > 0) {
            for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
                if (aDisabledDisplayValueItems.containsKey(tmpSingleValueItem.getName())) {
                    tmpSingleValueItem.setDisplay(true);
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Add">
    /**
     * Adds value item to container. NOTE: NO change in number of value items is
     * notified to change receivers.
     *
     * @param aValueItem Value item
     * @return True: Value item was added, false: Otherwise
     */
    public boolean addValueItem(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }
        if (this.nameToValueItemMap.containsKey(aValueItem.getName())) {
            return false;
        }
        // </editor-fold>
        aValueItem.setValueItemContainer(this);
        aValueItem.addChangeReceiver(this);
        this.nameToValueItemMap.put(aValueItem.getName(), aValueItem);
        // Clear value item cashes
        this.cashedValueItemArray = null;
        this.cashedNameSortedValueItemArray = null;
        return true;
    }

    /**
     * Add value items to container. NOTE: Change in number of value items is
     * notified to change receivers with block name of first value item if
     * necessary.
     *
     * @param aValueItems Value items
     * @param anIsNotificationSuppressed True: Notification of change receivers
     * is suppressed, false: Otherwise
     * @return True: Value items were added, false: Otherwise
     */
    public boolean addValueItems(ValueItem[] aValueItems, boolean anIsNotificationSuppressed) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItems == null || aValueItems.length == 0) {
            return false;
        }
        for (ValueItem tmpSingleValueItem : aValueItems) {
            if (tmpSingleValueItem == null) {
                return false;
            }
            if (this.nameToValueItemMap.containsKey(tmpSingleValueItem.getName())) {
                return false;
            }
        }
        // </editor-fold>
        for (ValueItem tmpSingleValueItem : aValueItems) {
            this.addValueItem(tmpSingleValueItem);
        }
        if (!anIsNotificationSuppressed) {
            // Notify about number of value items change with block name of first added value item
            this.changeInformation.setChangeType(ChangeTypeEnum.VALUE_ITEM_CONTAINER_NUMBER_CHANGE);
            this.changeInformation.setInfo(aValueItems[0].getBlockName());
            super.notifyChangeReceiver(this, this.changeInformation);
        }
        return true;
    }

    /**
     * Add value items to container and sorts blocks. NOTE: Change in number of
     * value items is notified to change receivers with block name of first
     * value item if necessary.
     *
     * @param aValueItems Value items
     * @param anIsNotificationSuppressed True: Notification of change receivers
     * is suppressed, false: Otherwise
     * @return True: Value items were added, false: Otherwise
     */
    public boolean addValueItemsAndSortBlocks(ValueItem[] aValueItems, boolean anIsNotificationSuppressed) {
        // NOTE: Suppress change receiver notification with parameter true
        if (!this.addValueItems(aValueItems, true)) {
            return false;
        }
        // NOTE: Suppress change receiver notification with parameter true
        this.sortBlocks(true);
        if (!anIsNotificationSuppressed) {
            // Notify about number of value items change with block name of first added value item
            this.changeInformation.setChangeType(ChangeTypeEnum.VALUE_ITEM_CONTAINER_NUMBER_CHANGE);
            this.changeInformation.setInfo(aValueItems[0].getBlockName());
            super.notifyChangeReceiver(this, this.changeInformation);
        }
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Get">
    /**
     * Returns value item with specified status. If more than one value item has
     * the specified status it is not determined which one will be returned
     * (arbitrary choice), but it will always be the same if container is not
     * changed.
     *
     * @param aStatus Status of value item
     * @return Value item with specified status or null if none is found
     */
    public ValueItem getValueItemWithStatus(ValueItemEnumStatus aStatus) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return null;
        }

        // </editor-fold>
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            if (tmpSingleValueItem.hasStatus(aStatus)) {
                return tmpSingleValueItem;
            }
        }
        return null;
    }

    /**
     * Returns maximum vertical position of value items of container
     *
     * @return Maximum vertical position of value items of container. If
     * container is empty -1 is returned.
     */
    public int getMaximumVerticalPosition() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return -1;
        }

        // </editor-fold>
        int tmpMaximumVerticalPosition = -Integer.MAX_VALUE;
        for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
            if (tmpValueItem.getVerticalPosition() > tmpMaximumVerticalPosition) {
                tmpMaximumVerticalPosition = tmpValueItem.getVerticalPosition();
            }
        }
        return tmpMaximumVerticalPosition;
    }

    /**
     * Returns value item with maximum vertical position.
     *
     * @return Maximum vertical position value item or null if no value item
     * exists.
     */
    public ValueItem getMaximumVerticalPositionValueItem() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return null;
        }
        // </editor-fold>
        int tmpMaximumVerticalPosition = -Integer.MAX_VALUE;
        ValueItem tmpMaximumVerticalPositionValueItem = null;
        for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
            if (tmpValueItem.getVerticalPosition() > tmpMaximumVerticalPosition) {
                tmpMaximumVerticalPosition = tmpValueItem.getVerticalPosition();
                tmpMaximumVerticalPositionValueItem = tmpValueItem;
            }
        }
        return tmpMaximumVerticalPositionValueItem;
    }

    /**
     * Returns node names of value item with maximum vertical position.
     *
     * @return Node names of value item with maximum vertical position or null
     * if none is available
     */
    public String[] getMaximumVerticalPositionValueItemNodeNames() {
        ValueItem tmpMaximumVerticalPositionValueItem = this.getMaximumVerticalPositionValueItem();
        if (tmpMaximumVerticalPositionValueItem == null) {
            return null;
        } else {
            return tmpMaximumVerticalPositionValueItem.getNodeNames();
        }
    }

    /**
     * Returns value items of container
     *
     * @return Value items of container or null
     */
    public ValueItem[] getValueItemsOfContainer() {
        if (this.cashedValueItemArray == null) {
            if (this.nameToValueItemMap.size() > 0) {
                this.cashedValueItemArray = this.nameToValueItemMap.values().toArray(new ValueItem[0]);
            }
        }
        return this.cashedValueItemArray;
    }

    /**
     * Returns sorted value items of container according to vertical position
     *
     * @return Sorted value items of container or null
     */
    public ValueItem[] getSortedValueItemsOfContainer() {
        if (this.nameToValueItemMap.size() > 0) {
            return ValueItemUtils.sortValueItems(this.getValueItemsOfContainer());
        } else {
            return null;
        }
    }

    /**
     * Returns value items of container sorted according to name in ascending
     * order
     *
     * @return Value items of container sorted according to name in ascending
     * order or null if none are available
     */
    public ValueItem[] getNameSortedValueItemsOfContainer() {
        if (this.cashedNameSortedValueItemArray == null) {
            ValueItem[] tmpValueItems = this.getValueItemsOfContainer();
            if (tmpValueItems == null) {
                this.cashedNameSortedValueItemArray = null;
            } else {
                String[] tmpNames = new String[tmpValueItems.length];
                HashMap<String, ValueItem> tmpNameToValueItemMap = new HashMap<>(tmpValueItems.length);
                for (int i = 0; i < tmpValueItems.length; i++) {
                    tmpNames[i] = tmpValueItems[i].getName() + this.stringUtilityMethods.getGloballyUniqueID();
                    tmpNameToValueItemMap.put(tmpNames[i], tmpValueItems[i]);
                }
                Arrays.sort(tmpNames);
                this.cashedNameSortedValueItemArray = new ValueItem[tmpValueItems.length];
                for (int i = 0; i < tmpValueItems.length; i++) {
                    this.cashedNameSortedValueItemArray[i] = tmpNameToValueItemMap.get(tmpNames[i]);
                }
            }
        }
        return this.cashedNameSortedValueItemArray;
    }

    /**
     * Returns value items of container sorted according to display name in
     * ascending order
     *
     * @return Value items of container sorted according to display name in
     * ascending order or null if none are available
     */
    public ValueItem[] getDisplayNameSortedValueItemsOfContainer() {
        ValueItem[] tmpValueItems = this.getValueItemsOfContainer();
        if (tmpValueItems == null) {
            return null;
        } else {
            String[] tmpDisplayNames = new String[tmpValueItems.length];
            HashMap<String, ValueItem> tmpDisplayNameToValueItemMap = new HashMap<>(tmpValueItems.length);
            for (int i = 0; i < tmpValueItems.length; i++) {
                tmpDisplayNames[i] = tmpValueItems[i].getDisplayName() + this.stringUtilityMethods.getGloballyUniqueID();
                tmpDisplayNameToValueItemMap.put(tmpDisplayNames[i], tmpValueItems[i]);
            }
            Arrays.sort(tmpDisplayNames);
            ValueItem[] tmpSortedValueItems = new ValueItem[tmpValueItems.length];
            for (int i = 0; i < tmpValueItems.length; i++) {
                tmpSortedValueItems[i] = tmpDisplayNameToValueItemMap.get(tmpDisplayNames[i]);
            }
            return tmpSortedValueItems;
        }
    }

    /**
     * Returns sorted value items of container according to vertical position
     * with specified name prefix
     *
     * @param aValueItemNamePrefix Name prefix of value items to be returned
     * @return Sorted value items of container according to vertical position
     * with specified name prefix or null
     */
    public LinkedList<ValueItem> getSortedValueItemsOfContainer(String aValueItemNamePrefix) {
        if (this.nameToValueItemMap.size() > 0) {
            LinkedList<ValueItem> tmpValueItemList = new LinkedList<ValueItem>();
            for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
                if (tmpValueItem.getName().startsWith(aValueItemNamePrefix)) {
                    tmpValueItemList.add(tmpValueItem);
                }
            }
            return ValueItemUtils.sortValueItems(tmpValueItemList);
        } else {
            return null;
        }
    }

    /**
     * Returns sorted value items of container according to vertical position
     * with specified status.
     *
     * @param aStatus Status of value item
     * @return Sorted value items of container with specified status or null
     */
    public LinkedList<ValueItem> getSortedValueItemsWithStatus(ValueItemEnumStatus aStatus) {
        if (this.nameToValueItemMap.size() > 0) {
            ValueItem[] tmpSortedValueItems = ValueItemUtils.sortValueItems(this.getValueItemsOfContainer());
            LinkedList<ValueItem> tmpValueItemWithStatusList = new LinkedList<ValueItem>();
            for (ValueItem tmpSingleValueItem : tmpSortedValueItems) {
                if (tmpSingleValueItem.hasStatus(aStatus)) {
                    tmpValueItemWithStatusList.add(tmpSingleValueItem);
                }
            }
            return tmpValueItemWithStatusList;
        } else {
            return null;
        }
    }

    /**
     * Returns specified value item.
     *
     * @param aValueItemName Name of value item
     * @return Specified value item or null if specified value item does not
     * exist
     */
    public ValueItem getValueItem(String aValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return null;
        }
        // </editor-fold>
        return this.nameToValueItemMap.get(aValueItemName);
    }

    /**
     * Returns all value items with names that start with prefixes.
     *
     * @param aValueItemNamePrefixes Prefixes of names of value items
     * @return Value items with names that start with prefixes or null if
     * specified value items do not exist
     */
    public LinkedList<ValueItem> getValueItems(String[] aValueItemNamePrefixes) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return null;
        }
        if (aValueItemNamePrefixes == null || aValueItemNamePrefixes.length == 0) {
            return null;
        }

        // </editor-fold>
        LinkedList<ValueItem> tmpValueItemList = new LinkedList<ValueItem>();
        for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
            for (String tmpNamePrefix : aValueItemNamePrefixes) {
                if (tmpValueItem.getName().startsWith(tmpNamePrefix)) {
                    tmpValueItemList.addLast(tmpValueItem);
                    break;
                }
            }
        }
        if (tmpValueItemList.size() > 0) {
            return tmpValueItemList;
        } else {
            return null;
        }
    }

    /**
     * Returns value of specified value item.
     *
     * @param aValueItemName Name of value item
     * @return Value of specified value item or null if specified value item
     * does not exist
     */
    public String getValueOfValueItem(String aValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return null;
        }
        if (this.nameToValueItemMap.isEmpty()) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpValueItem = this.nameToValueItemMap.get(aValueItemName);
        if (tmpValueItem != null) {
            return tmpValueItem.getValue();
        } else {
            return null;
        }
    }

    /**
     * Returns sorted string array with display names of value items
     *
     * @return Sorted string array with display names of value items or null if
     * no value items are available
     */
    public String[] getSortedValueItemDisplayNames() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return null;
        }

        // </editor-fold>
        String[] tmpValueItemDisplayNames = new String[this.nameToValueItemMap.size()];
        int tmpIndex = 0;
        for (ValueItem tmpValueItem : this.nameToValueItemMap.values()) {
            tmpValueItemDisplayNames[tmpIndex++] = tmpValueItem.getDisplayName();
        }
        Arrays.sort(tmpValueItemDisplayNames);
        return tmpValueItemDisplayNames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Has">
    /**
     * Returns if specified value item exists
     *
     * @param aValueItemName Name of value item
     * @return True: Specified value item exists, false: Otherwise
     */
    public boolean hasValueItem(String aValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return false;
        }
        if (this.nameToValueItemMap.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.nameToValueItemMap.containsKey(aValueItemName);
    }

    /**
     * Returns if value item with display name exists
     *
     * @param aValueItemDisplayName Value item display name
     * @return True: Value item with display exists, false: Otherwise
     */
    public boolean hasValueItemWithDisplayName(String aValueItemDisplayName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemDisplayName == null || aValueItemDisplayName.isEmpty()) {
            return false;
        }
        if (this.nameToValueItemMap.isEmpty()) {
            return false;
        }
        // </editor-fold>
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            if (aValueItemDisplayName.equals(tmpSingleValueItem.getDisplayName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns if value item with compartments exists
     *
     * @return True: Value item with compartments exists, false: Otherwise
     */
    public boolean hasValueItemWithCompartments() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return false;
        }

        // </editor-fold>
        for (ValueItem tmpSingleValueItem : this.nameToValueItemMap.values()) {
            if (tmpSingleValueItem.hasCompartments()) {
                return true;
            }
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Set">
    /**
     * Sets value of specified value item.
     *
     * @param aValueItemName Name of value item
     * @param aValue Value
     * @return True: Value was successfully set, false: otherwise
     */
    public boolean setValueOfValueItem(String aValueItemName, String aValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemName == null || aValueItemName.isEmpty()) {
            return false;
        }
        if (this.nameToValueItemMap.isEmpty()) {
            return false;
        }

        // </editor-fold>
        ValueItem tmpValueItem = this.nameToValueItemMap.get(aValueItemName);
        if (tmpValueItem != null) {
            if (tmpValueItem.setValue(aValue)) {
                tmpValueItem.notifyDependentValueItemsForUpdate();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set successive vertical positions of all value items from aStartPosition
     * according to old vertical positions.
     *
     * @param aStartPosition Start position
     * @return Returns next non-used vertical position
     */
    public int setSuccessiveVerticalPositions(int aStartPosition) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.nameToValueItemMap.isEmpty()) {
            return aStartPosition;
        }
        // </editor-fold>
        ValueItem[] tmpValueItems = this.getSortedValueItemsOfContainer();
        for (int i = 0; i < tmpValueItems.length; i++) {
            tmpValueItems[i].setVerticalPosition(aStartPosition + i);
        }
        return aStartPosition + tmpValueItems.length;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Replace/Remove/Insert">
    /**
     * Removes value item with specified name from container. NOTE: NO change in
     * number of value items is notified to change receivers.
     *
     * @param aValueItemName Name of value item
     * @return True: Value item was removed, false: Otherwise
     */
    public boolean removeValueItem(String aValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemName == null || aValueItemName.length() == 0) {
            return false;
        }
        if (!this.nameToValueItemMap.containsKey(aValueItemName)) {
            return false;
        }

        // </editor-fold>
        this.nameToValueItemMap.remove(aValueItemName);
        // Clear value item cashes
        this.cashedValueItemArray = null;
        this.cashedNameSortedValueItemArray = null;
        return true;
    }

    /**
     * Replaces value item in container
     *
     * @param aValueItem Value item
     * @return True: Value item was replaced, false: Otherwise
     */
    public boolean replaceValueItem(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }
        if (!this.nameToValueItemMap.containsKey(aValueItem.getName())) {
            return false;
        }
        // </editor-fold>
        this.removeValueItem(aValueItem.getName());
        return this.addValueItem(aValueItem);
    }

    /**
     * Replaces value item in container but keeps vertical position and node
     * names of replaced value item
     *
     * @param aValueItem Value item
     * @return True: Value item was replaced (with kept vertical position and
     * node names of replaced value item), false: Otherwise
     */
    public boolean replaceValueItemWithKeptVerticalPositionAndNodeNames(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }
        if (!this.nameToValueItemMap.containsKey(aValueItem.getName())) {
            return false;
        }
        // </editor-fold>
        ValueItem tmpValueItemToBeReplaced = this.getValueItem(aValueItem.getName());
        aValueItem.setVerticalPosition(tmpValueItemToBeReplaced.getVerticalPosition());
        aValueItem.setNodeNames(tmpValueItemToBeReplaced.getNodeNames());
        this.removeValueItem(aValueItem.getName());
        return this.addValueItem(aValueItem);
    }

    /**
     * Inserts value item with vertical position before value item with 
     * specified name
     *
     * @param aValueItemToBeInserted Value item to be inserted
     * @param aValueItemName Name of value item
     * @return True: Value item is inserted, false: Otherwise
     */    
    public boolean insertValueItemBefore(ValueItem aValueItemToBeInserted, String aValueItemName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemToBeInserted == null) {
            return false;
        }
        if (!this.nameToValueItemMap.containsKey(aValueItemName)) {
            return false;
        }
        // </editor-fold>
        // Existing value items sorted ascending according to their vertical
        // position
        ValueItem[] tmpValueItems = this.getSortedValueItemsOfContainer();
        boolean tmpIsIncrement = false;
        for(ValueItem tmpValueItem : tmpValueItems) {
            if (tmpValueItem.getName().equals(aValueItemName)) {
                tmpIsIncrement = true;
                aValueItemToBeInserted.setVerticalPosition(tmpValueItem.getVerticalPosition());
            }
            if (tmpIsIncrement) {
                tmpValueItem.incrementVerticalPosition();
            }
        }
        return this.addValueItem(aValueItemToBeInserted);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Node names related methods">
    /**
     * Adds specified node name at first position of the node names of all value
     * items of container if that node name at first position does NOT already
     * exist.
     *
     * @param aNodeNameForFirstPosition Node name for first position
     */
    public void addNodeNameAtFirstPosition(String aNodeNameForFirstPosition) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNodeNameForFirstPosition == null || aNodeNameForFirstPosition.isEmpty()) {
            return;
        }

        // </editor-fold>
        ValueItem[] tmpValueItems = this.getValueItemsOfContainer();
        for (ValueItem tmpSingleValueItem : tmpValueItems) {
            String[] tmpNodeNames = tmpSingleValueItem.getNodeNames();
            if (tmpNodeNames != null && !tmpNodeNames[0].equals(aNodeNameForFirstPosition)) {
                tmpSingleValueItem.setNodeNames(this.stringUtilityMethods.addStringAtFirstIndex(tmpNodeNames, aNodeNameForFirstPosition));
            }
        }
    }

    /**
     * Replaces node name at first position of the node names of all value items
     * of container if that node name at first position does NOT already exist.
     *
     * @param aNodeNameForFirstPosition Node name for first position
     */
    public void replaceNodeNameAtFirstPosition(String aNodeNameForFirstPosition) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNodeNameForFirstPosition == null || aNodeNameForFirstPosition.isEmpty()) {
            return;
        }

        // </editor-fold>
        ValueItem[] tmpValueItems = this.getValueItemsOfContainer();
        for (ValueItem tmpSingleValueItem : tmpValueItems) {
            String[] tmpNodeNames = tmpSingleValueItem.getNodeNames();
            if (tmpNodeNames != null && !tmpNodeNames[0].equals(aNodeNameForFirstPosition)) {
                tmpNodeNames[0] = aNodeNameForFirstPosition;
                tmpSingleValueItem.setNodeNames(tmpNodeNames);
            }
        }
    }

    /**
     * Sets node names to all value items of container.
     *
     * @param aNodeNames Node names
     */
    public void setNodeNames(String[] aNodeNames) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNodeNames == null || aNodeNames.length == 0) {
            return;
        }

        // </editor-fold>
        ValueItem[] tmpValueItems = this.getValueItemsOfContainer();
        for (ValueItem tmpSingleValueItem : tmpValueItems) {
            tmpSingleValueItem.setNodeNames(aNodeNames);
        }
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update notifier related methods">

    /**
     * Notifies dependent value items of anUpdateNotifierValueItem of the this
     * container about a value change for update.
     *
     * @param anUpdateNotifierValueItem Value item that notifies update
     */
    public void notifyDependentValueItemsForUpdate(ValueItem anUpdateNotifierValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anUpdateNotifierValueItem == null) {
            return;
        }
        if (this.updateNotificationObject == null) {
            return;
        }

        // </editor-fold>
        this.updateNotificationObject.notifyDependentValueItemsForUpdate(anUpdateNotifierValueItem);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Returns a XML element representation of this instance
     *
     * @return A XML element representation of this instance
     * @throws Exception Thrown if error occurs
     */
    public Element getAsXmlElement() throws Exception {
        try {
            Element tmpRoot = new Element(ValueItemContainerXmlName.VALUE_ITEM_CONTAINER);
            // IMPORTANT: Set version of this XML definition
            tmpRoot.addContent(new Element(ValueItemContainerXmlName.VERSION).addContent("Version 1.0.0"));
            // <editor-fold defaultstate="collapsed" desc="Add XML elements of value items">
            ValueItem[] tmpValueItems = this.getValueItemsOfContainer();
            if (tmpValueItems != null) {
                ValueItemUtils.sortValueItems(tmpValueItems);
                for (ValueItem tmpValueItem : tmpValueItems) {
                    tmpRoot.addContent(tmpValueItem.getAsXmlElement());
                }
            }

            // </editor-fold>
            return tmpRoot;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new Exception("XML element could not be created.", anException);
        }
    }

    /**
     * Returns a XML string representation of this instance
     *
     * @return A XML string representation of this instance
     * @throws Exception Thrown if error occurs
     */
    public String getAsXmlString() throws Exception {
        try {
            Element tmpRoot = this.getAsXmlElement();
            XMLOutputter tmpOutputter = new XMLOutputter(Format.getPrettyFormat());
            Document tmpDocument = new Document();
            tmpDocument.setRootElement(tmpRoot);
            return tmpOutputter.outputString(tmpDocument);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new Exception("XML string could not be created.", anException);
        }
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="- Size">
    /**
     * Returns number of value items in container
     *
     * @return Number of value items in container
     */
    public int getSize() {
        return this.nameToValueItemMap.size();
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (set)">
    // <editor-fold defaultstate="collapsed" desc="- UpdateNotificationObject">
    /**
     * Value item update notification object
     *
     * @param anUpdateNotificationObject Value item update notification object
     * (may be null)
     */
    public void setUpdateNotificationObject(ValueItemUpdateNotifierInterface anUpdateNotificationObject) {
        this.updateNotificationObject = anUpdateNotificationObject;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Value item ChangeReceiverInterface related methods">
    /**
     * Set this container as a change receiver for all value items
     */
    private void setThisAsChangeReceiverForValueItems() {
        this.addSingleChangeReceiverToValueItems(this);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Initialize method">
    /**
     * Initializes class variables
     */
    private void initialize() {
        // NOTE: Do NOT change this.updateNotificationObject
        this.changeInformation = new ChangeInformation();
        this.nameToValueItemMap = new HashMap<String, ValueItem>(ModelDefinitions.DEFAULT_NUMBER_OF_VALUE_ITEMS);
        // Clear value item cashes
        this.cashedValueItemArray = null;
        this.cashedNameSortedValueItemArray = null;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Reads XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformation(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }
        if (!anElement.getName().equals(ValueItemContainerXmlName.VALUE_ITEM_CONTAINER)) {
            return false;
        }
        if (anElement.getChild(ValueItemContainerXmlName.VERSION) == null) {
            return false;
        }

        // </editor-fold>
        String tmpVersion = anElement.getChild(ValueItemContainerXmlName.VERSION).getText();
        if (tmpVersion == null || tmpVersion.isEmpty()) {
            return false;
        }
        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
        if (tmpVersion.equals("Version 1.0.0")) {
            return this.readXmlInformationV_1_0_0(anElement);
        }

        // </editor-fold>
        return false;
    }

    /**
     * Reads versioned XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformationV_1_0_0(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }

        // </editor-fold>
        try {
            // Read value items
            boolean tmpIsSuccessful = true;
            for (Iterator<?> tmpIter = anElement.getChildren(ValueItemXmlName.VALUE_ITEM).iterator(); tmpIter.hasNext();) {
                ValueItem tmpValueItem = new ValueItem((Element) tmpIter.next());
                if (!this.addValueItem(tmpValueItem)) {
                    tmpIsSuccessful = false;
                    break;
                }
            }
            return tmpIsSuccessful;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
