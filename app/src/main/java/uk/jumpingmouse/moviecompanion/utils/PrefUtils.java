package uk.jumpingmouse.moviecompanion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.ViewAward;

/**
 * Preferences utility methods.
 * @author Edmund Johnson
 */
public class PrefUtils {

    /** Private constructor to prevent instantiation. */
    private PrefUtils() {
    }

    /**
     * Returns the value of a String user preference.
     * @param context the context
     * @param prefKeyResId the string resource id of the key of the shared preference
     * @param defaultValue the value to return if the preference has not been assigned a value
     * @return the value of the user preference
     */
    @Nullable
    private static String getSharedPreferenceString(@NonNull Context context,
                                @StringRes int prefKeyResId, @Nullable String defaultValue) {
        String prefKey = context.getString(prefKeyResId);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(prefKey, defaultValue);
    }

    /**
     * Returns the value of a boolean user preference.
     * @param context the context
     * @param prefKeyResId the string resource id of the key of the shared preference to be updated
     * @param defaultValue the value to return if the preference has not been assigned a value
     * @return the value of the user preference
     */
    private static boolean getSharedPreferenceBoolean(@NonNull Context context,
                                              @StringRes int prefKeyResId, boolean defaultValue) {
        String prefKey = context.getString(prefKeyResId);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(prefKey, defaultValue);
    }

    /**
     * Sets a shared preference to a supplied value.
     * @param context the context
     * @param prefKeyResId the string resource id of the key of the shared preference to be updated
     * @param value the value to which the shared preference is to be set
     */
    private static void setSharedPreferenceString(@NonNull Context context,
                                 @StringRes int prefKeyResId, @NonNull String value) {
        String prefKey = context.getString(prefKeyResId);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString(prefKey, value);
        sharedPrefEditor.apply();
    }

    /**
     * Sets a shared preference to a supplied value.
     * @param context the context
     * @param prefKeyResId the string resource id of the key of the shared preference to be updated
     * @param value the value to set the preference to
     */
    private static void setSharedPreferenceBoolean(@NonNull Context context,
                                                   @StringRes int prefKeyResId, boolean value) {
        String prefKey = context.getString(prefKeyResId);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putBoolean(prefKey, value);
        sharedPrefEditor.apply();
    }

    /**
     * Sets the list sort order shared preference to a supplied value.
     * @param context the context
     * @param value the new value for the list sort order
     */
    public static void setAwardListSortOrder(@NonNull Context context, @NonNull String value) {
        setSharedPreferenceString(context, R.string.pref_award_list_sort_order_key, value);
    }

    /**
     * Returns the list sort order shared preference.
     * @param context the context
     */
    public static String getAwardListSortOrder(@NonNull Context context) {
        return getSharedPreferenceString(
                context, R.string.pref_award_list_sort_order_key, ViewAward.SORT_ORDER_DEFAULT);
    }

    /**
     * Returns whether a string has the same value as the award list sort order preference key.
     * @param context the context
     * @param value the value to compare to the award list sort order preference key
     * @return true if value is the award list sort order preference key, false otherwise
     */
    public static boolean isAwardListSortOrderKey(@NonNull Context context, @Nullable String value) {
        return value != null
                && value.equals(context.getString(R.string.pref_award_list_sort_order_key));
    }

}
