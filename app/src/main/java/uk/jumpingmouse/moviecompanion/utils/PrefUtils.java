package uk.jumpingmouse.moviecompanion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.ViewAwardListParameters;

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
    @NonNull
    private static String getSharedPreferenceString(@Nullable Context context,
                                @StringRes int prefKeyResId, @NonNull String defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        String prefKey = context.getString(prefKeyResId);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(prefKey, defaultValue);
    }

//    /**
//     * Returns the value of an integer user preference.
//     * @param context the context
//     * @param prefKeyResId the string resource id of the key of the shared preference to be updated
//     * @param defaultValue the value to return if the preference has not been assigned a value
//     * @return the value of the user preference
//     */
//    private static int getSharedPreferenceInt(@Nullable Context context,
//                                              @StringRes int prefKeyResId, int defaultValue) {
//        if (context == null) {
//            return defaultValue;
//        }
//        String prefKey = context.getString(prefKeyResId);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        return sharedPref.getInt(prefKey, defaultValue);
//    }

    /**
     * Returns the value of a boolean user preference.
     * @param context the context
     * @param prefKeyResId the string resource id of the key of the shared preference to be updated
     * @param defaultValue the value to return if the preference has not been assigned a value
     * @return the value of the user preference
     */
    private static boolean getSharedPreferenceBoolean(@Nullable Context context,
                                              @StringRes int prefKeyResId, boolean defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        String prefKey = context.getString(prefKeyResId);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(prefKey, defaultValue);
    }

    /**
     * Sets a shared preference to a supplied String value.
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

//    /**
//     * Sets a shared preference to a supplied integer value.
//     * @param context the context
//     * @param prefKeyResId the string resource id of the key of the shared preference to be updated
//     * @param value the value to which the shared preference is to be set
//     */
//    private static void setSharedPreferenceInt(@NonNull Context context,
//                                               @StringRes int prefKeyResId, int value) {
//        String prefKey = context.getString(prefKeyResId);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
//        sharedPrefEditor.putInt(prefKey, value);
//        sharedPrefEditor.apply();
//    }

    /**
     * Sets a shared preference to a supplied boolean value.
     * @param context the context
     * @param prefKeyResId the string resource id of the key of the shared preference to be updated
     * @param value the value to which the shared preference is to be set
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
     * Returns the list sort order shared preference.
     * @param context the context
     */
    public static String getAwardListSortOrder(@Nullable Context context) {
        return getSharedPreferenceString(context, R.string.pref_award_list_sort_order_key,
                ViewAwardListParameters.SORT_ORDER_DEFAULT);
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
     * Returns whether a string has the same value as the award list sort order preference key.
     * @param context the context
     * @param value the value to compare to the award list sort order preference key
     * @return true if value is the award list sort order preference key, false otherwise
     */
    public static boolean isAwardListSortOrderKey(@NonNull Context context, @Nullable String value) {
        return value != null && value.equals(context.getString(R.string.pref_award_list_sort_order_key));
    }

    /**
     * Returns the award list wishlist filter shared preference.
     * @param context the context
     */
    @NonNull
    public static String getAwardListFilterWishlist(@Nullable Context context) {
        return getSharedPreferenceString(context, R.string.pref_award_list_filter_wishlist_key,
                ViewAwardListParameters.FILTER_WISHLIST_DEFAULT);
    }

    /**
     * Sets the award list wishlist filter shared preference to a supplied value.
     * @param context the context
     * @param value the new value for the award list wishlist filter
     */
    public static void setAwardListFilterWishlist(@NonNull Context context, @NonNull String value) {
        setSharedPreferenceString(context, R.string.pref_award_list_filter_wishlist_key, value);
    }

    /**
     * Returns whether a string has the same value as the award list wishlist filter preference key.
     * @param context the context
     * @param value the value to compare to the award list wishlist filter preference key
     * @return true if value is the award list wishlist filter preference key, false otherwise
     */
    public static boolean isAwardListFilterWishlistKey(@NonNull Context context, @Nullable String value) {
        return value != null && value.equals(context.getString(R.string.pref_award_list_filter_wishlist_key));
    }

    /**
     * Returns the award list watched filter shared preference.
     * @param context the context
     */
    @NonNull
    public static String getAwardListFilterWatched(@Nullable Context context) {
        return getSharedPreferenceString(context, R.string.pref_award_list_filter_watched_key,
                ViewAwardListParameters.FILTER_WATCHED_DEFAULT);
    }

    /**
     * Sets the award list watched filter shared preference to a supplied value.
     * @param context the context
     * @param value the new value for the award list watched filter
     */
    public static void setAwardListFilterWatched(@NonNull Context context, @NonNull String value) {
        setSharedPreferenceString(context, R.string.pref_award_list_filter_watched_key, value);
    }

    /**
     * Returns whether a string has the same value as the award list watched filter preference key.
     * @param context the context
     * @param value the value to compare to the award list watched filter preference key
     * @return true if value is the award list watched filter preference key, false otherwise
     */
    public static boolean isAwardListFilterWatchedKey(@NonNull Context context, @Nullable String value) {
        return value != null && value.equals(context.getString(R.string.pref_award_list_filter_watched_key));
    }

    /**
     * Returns the award list favourite filter shared preference.
     * @param context the context
     */
    @NonNull
    public static String getAwardListFilterFavourite(@Nullable Context context) {
        return getSharedPreferenceString(context, R.string.pref_award_list_filter_favourite_key,
                ViewAwardListParameters.FILTER_FAVOURITE_DEFAULT);
    }

    /**
     * Sets the award list favourite filter shared preference to a supplied value.
     * @param context the context
     * @param value the new value for the award list favourite filter
     */
    public static void setAwardListFilterFavourite(@NonNull Context context, @NonNull String value) {
        setSharedPreferenceString(context, R.string.pref_award_list_filter_favourite_key, value);
    }

    /**
     * Returns whether a string has the same value as the award list favourite filter preference key.
     * @param context the context
     * @param value the value to compare to the award list favourite filter preference key
     * @return true if value is the award list favourite filter preference key, false otherwise
     */
    public static boolean isAwardListFilterFavouriteKey(@NonNull Context context, @Nullable String value) {
        return value != null && value.equals(context.getString(R.string.pref_award_list_filter_favourite_key));
    }

}
