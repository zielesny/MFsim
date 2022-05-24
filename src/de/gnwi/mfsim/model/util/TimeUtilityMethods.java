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
package de.gnwi.mfsim.model.util;

import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Time utility methods to be instantiated
 *
 * @author Achim Zielesny
 */
public class TimeUtilityMethods {

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public TimeUtilityMethods() {
        // Do nothing
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Converts timestamp in standard format into timestamp for directory ending
     *
     * @param aTimestampInStandardFormat Timestamp in standard format
     * @return Timestamp for directory ending or null if argument could not be
     * converted
     */
    public String convertTimestampInStandardFormatIntoDirectoryEnding(String aTimestampInStandardFormat) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isValidTimestampInStandardFormat(aTimestampInStandardFormat)) {
            return null;
        }
        // </editor-fold>
        try {
            SimpleDateFormat tmpDateTimeInStandardTimestampFormat = new SimpleDateFormat(ModelDefinitions.STANDARD_TIMESTAMP_FORMAT);
            SimpleDateFormat tmpDateTimeInDirectoryEndingTimestampFormat = new SimpleDateFormat(ModelDefinitions.DIRECTORY_ENDING_TIMESTAMP_FORMAT);
            return tmpDateTimeInDirectoryEndingTimestampFormat.format(tmpDateTimeInStandardTimestampFormat.parse(aTimestampInStandardFormat));
        } catch (ParseException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns difference between timestamps as string in defined format
     *
     * @param aTimestampStart Start timestamp
     * @param aTimestampEnd End timestamp
     * @return String in defined format that contains difference between
     * timestamps or null if arguments are invalid
     */
    public String getDateTimeDifference(String aTimestampStart, String aTimestampEnd) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isValidTimestampInStandardFormat(aTimestampStart) || !this.isValidTimestampInStandardFormat(aTimestampEnd) || aTimestampStart.compareTo(aTimestampEnd) > 0) {
            return null;
        }

        // </editor-fold>
        SimpleDateFormat tmpDateTimeFormat = new SimpleDateFormat(ModelDefinitions.STANDARD_TIMESTAMP_FORMAT);
        try {
            return this.getFormattedTimePeriodString(tmpDateTimeFormat.parse(aTimestampEnd).getTime() - tmpDateTimeFormat.parse(aTimestampStart).getTime());
        } catch (ParseException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns date-time difference in seconds between timestamps
     *
     * @param aTimestampStart Start timestamp
     * @param aTimestampEnd End timestamp
     * @return Date-time difference in seconds between timestamps or "-1" if a
     * parameter is invalid or an exception occurs
     */
    public long getDateTimeDifferenceInSeconds(String aTimestampStart, String aTimestampEnd) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isValidTimestampInStandardFormat(aTimestampStart) || !this.isValidTimestampInStandardFormat(aTimestampEnd) || aTimestampStart.compareTo(aTimestampEnd) > 0) {
            return -1;
        }

        // </editor-fold>
        SimpleDateFormat tmpDateTimeFormat = new SimpleDateFormat(ModelDefinitions.STANDARD_TIMESTAMP_FORMAT);
        try {
            return (long) Math.floor((tmpDateTimeFormat.parse(aTimestampEnd).getTime() - tmpDateTimeFormat.parse(aTimestampStart).getTime()) / 1000);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }

    /**
     * Returns formatted time period string
     *
     * @param aTimePeriodInMilliseconds Time period in milliseconds
     * @return Time period string in defined format or null if none could be
     * generated
     */
    private String getFormattedTimePeriodString(long aTimePeriodInMilliseconds) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTimePeriodInMilliseconds < 0) {
            return null;
        }
        // </editor-fold>
        try {
            int tmpDays = (int) Math.floor(aTimePeriodInMilliseconds / (1000 * 60 * 60 * 24));
            long tmpHourRemainder = aTimePeriodInMilliseconds - tmpDays * 1000 * 60 * 60 * 24;
            int tmpHours = (int) Math.floor(tmpHourRemainder / (1000 * 60 * 60));
            long tmpMinRemainder = tmpHourRemainder - tmpHours * 1000 * 60 * 60;
            int tmpMin = (int) Math.floor(tmpMinRemainder / (1000 * 60));
            long tmpSecondsRemainder = tmpMinRemainder - tmpMin * 1000 * 60;
            int tmpSeconds = (int) Math.floor(tmpSecondsRemainder / 1000);
            if (tmpDays == 0) {
                if (tmpHours == 0) {
                    if (tmpMin == 0) {
                        return String.format(ModelMessage.get("Format.DateTimeDifference.Seconds"), Integer.toString(tmpSeconds));
                    } else {
                        return String.format(ModelMessage.get("Format.DateTimeDifference.Minutes"), Integer.toString(tmpMin), Integer.toString(tmpSeconds));
                    }
                } else {
                    return String.format(ModelMessage.get("Format.DateTimeDifference.Hours"), Integer.toString(tmpHours), Integer.toString(tmpMin), Integer.toString(tmpSeconds));
                }
            } else {
                return String.format(ModelMessage.get("Format.DateTimeDifference.Days"), Integer.toString(tmpDays), Integer.toString(tmpHours), Integer.toString(tmpMin), Integer.toString(tmpSeconds));
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns time period
     *
     * @param aNumberOfSeconds Number of seconds
     * @return Time period in defined format
     */
    public String getTimePeriod(long aNumberOfSeconds) {
        return this.getFormattedTimePeriodString(aNumberOfSeconds * 1000);
    }

    /**
     * Checks if timestamp in standard format is valid (see
 ModelDefinitions.STANDARD_TIMESTAMP_FORMAT)
     *
     * @param aTimestamp Timestamp to be checked
     * @return true: Timestamp is valid, false: Otherwise
     */
    public boolean isValidTimestampInStandardFormat(String aTimestamp) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTimestamp == null || aTimestamp.isEmpty()) {
            return false;
        }
        // </editor-fold>
        SimpleDateFormat tmpDateTimeFormat = new SimpleDateFormat(ModelDefinitions.STANDARD_TIMESTAMP_FORMAT);
        try {
            tmpDateTimeFormat.parse(aTimestamp);
            // NOTE: If aTimestamp can not be parsed a ParseException is thrown
            return ModelDefinitions.STANDARD_TIMESTAMP_REGEX_PATTERN.matcher(aTimestamp).matches();
        } catch (ParseException anException) {
            // Do NOT append to log file since exception is part of the function (not a nice implementation)
            // Utility.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Appends creation standard time stamp to string
     *
     * @param aString String to be appended with creation standard time stamp
     * @return String with appended standard time stamp or null if null was
     * passed
     */
    public String getCreationStandardTimeStampAppendString(String aString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null) {
            return null;
        }

        // </editor-fold>
        return String.format(ModelMessage.get("TimeStampAppendFormat.Creation"), aString, ModelUtils.getTimestampInStandardFormat());
    }
    // </editor-fold>

}
