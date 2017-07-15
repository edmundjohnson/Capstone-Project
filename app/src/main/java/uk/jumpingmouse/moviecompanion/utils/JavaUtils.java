package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Class for Java utilities.
 * @author Edmund Johnson
 */
public final class JavaUtils {

    //---------------------------------------------------------------------
    // Date utilities

    /** Private default constructor to prevent instantiation. */
    private JavaUtils() {
    }

    /**
     * Returns a Date object representing a supplied String.
     * @param format the SimpleDateFormat used for strDate, e.g. "dd MMM yyyy"
     * @param strDate a String representing a date
     * @return a Date object representing the supplied String,
     *         or null if the String could not be parsed as a date
     */
    @Nullable
    static Date toDate(@SuppressWarnings("SameParameterValue") @NonNull SimpleDateFormat format,
                       @Nullable final String strDate) {
        if (strDate == null) {
            return null;
        }
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            Timber.e("toDate: Cannot parse String \"" + strDate + "\" to a Date using format: \""
                    + format + "\"", e);
            return null;
        }
    }

    /**
     * Returns a Date as a String.
     * @param dateFormat the date format used for the returned String, e.g. "dd MMM yyyy"
     * @param date the Date
     * @return the Date as a String
     */
    @NonNull
    public static String toString(@SuppressWarnings("SameParameterValue") @NonNull SimpleDateFormat dateFormat,
                                  @NonNull final Date date) {
        return dateFormat.format(date);
    }

    //---------------------------------------------------------------------
    // Type conversions

    /**
     * Converts a String to an int and returns the int.
     * @param str the String to convert
     * @param defaultValue the value to return if the String cannot be parsed as an int
     * @return the String as an int, or defaultValue if the String could not be parsed as an int
     */
    public static int toInt(@Nullable String str, int defaultValue) {
        if (str != null && !str.trim().isEmpty()) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                Timber.w("Exception while converting String to int", e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    //---------------------------------------------------------------------
    // Collection utilities

    /**
     * Creates and returns a sorted list from the values of a SparseArray
     * @param sparseArray the SparseArray
     * @param comparator the comparator to use for sorting the list
     * @param <C> the class of the objects in the SparseArray
     * @return the sorted list created from the values of the SparseArray
     */
    public static <C> List<C> asSortedList(SparseArray<C> sparseArray, Comparator<? super C> comparator) {
        List<C> arrayList = asList(sparseArray);
        Collections.sort(arrayList, comparator);
        return arrayList;
    }

    /**
     * Creates and returns a list from the values of a SparseArray
     * @param sparseArray the SparseArray
     * @param <C> the class of the objects in the SparseArray
     * @return the list created from the values of the SparseArray
     */
    private static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    /**
     * Returns the position of a value in a string array.
     * @param valueArray the string array
     * @param value the value whose position is required
     * @return the array index of value in the array, or -1 if value is not found
     */
    public static int getPositionInArray(@NonNull String[] valueArray, @Nullable String value) {
        if (value != null) {
            for (int position = 0; position < valueArray.length; position++) {
                if (valueArray[position].equals(value)) {
                    return position;
                }
            }
        }
        return -1;
    }

}
