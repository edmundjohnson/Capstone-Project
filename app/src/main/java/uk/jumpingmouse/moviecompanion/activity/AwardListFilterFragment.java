package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;
import uk.jumpingmouse.moviecompanion.utils.PrefUtils;

/**
 * A dialog fragment which allows the user to select the filter options for the award list.
 * @author Edmund Johnson.
 */

public final class AwardListFilterFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // do not display the default title bar, use the styled title bar in the layout xml
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.award_list_filter_fragment, container);
        Context context = getActivity();

        if (view != null && context != null && context.getResources() != null) {

            // Genre filter
            String[] filterPrefValuesGenre =
                    context.getResources().getStringArray(R.array.filter_genre_pref_key);
            ListFilter listFilterGenre = new ListFilter(
                    R.string.pref_award_list_filter_genre_key, filterPrefValuesGenre,
                    DataContract.ViewAwardEntry.FILTER_GENRE_DEFAULT);
            final ListFilterSpinner spinnerGenre = new ListFilterSpinner(
                    context, view, listFilterGenre, R.id.spnFilterGenre,
                    R.id.frameFilterGenre, R.array.filter_genre_pref_display);

            // Wishlist filter
            String[] filterPrefValuesWishlist =
                    context.getResources().getStringArray(R.array.filter_wishlist_pref_key);
            ListFilter listFilterWishlist = new ListFilter(
                    R.string.pref_award_list_filter_wishlist_key, filterPrefValuesWishlist,
                    DataContract.ViewAwardEntry.FILTER_WISHLIST_DEFAULT);
            final ListFilterSpinner spinnerWishlist = new ListFilterSpinner(
                    context, view, listFilterWishlist, R.id.spnFilterWishlist,
                    R.id.frameFilterWishlist, R.array.filter_wishlist_pref_display);

            // Watched filter
            String[] filterPrefValuesWatched =
                    context.getResources().getStringArray(R.array.filter_watched_pref_key);
            ListFilter listFilterWatched = new ListFilter(
                    R.string.pref_award_list_filter_watched_key, filterPrefValuesWatched,
                    DataContract.ViewAwardEntry.FILTER_WATCHED_DEFAULT);
            final ListFilterSpinner spinnerWatched = new ListFilterSpinner(
                    context, view, listFilterWatched,
                    R.id.spnFilterWatched, R.id.frameFilterWatched, R.array.filter_watched_pref_display);

            // Favourite filter
            String[] filterPrefValuesFavourite =
                    context.getResources().getStringArray(R.array.filter_favourite_pref_key);
            ListFilter listFilterFavourite = new ListFilter(
                    R.string.pref_award_list_filter_favourite_key, filterPrefValuesFavourite,
                    DataContract.ViewAwardEntry.FILTER_FAVOURITE_DEFAULT);
            final ListFilterSpinner spinnerFavourite = new ListFilterSpinner(
                    context, view, listFilterFavourite,
                    R.id.spnFilterFavourite, R.id.frameFilterFavourite,
                    R.array.filter_favourite_pref_display);

            // Category filter
            String[] filterPrefValuesCategory =
                    context.getResources().getStringArray(R.array.filter_category_pref_key);
            ListFilter listFilterCategory = new ListFilter(
                    R.string.pref_award_list_filter_category_key, filterPrefValuesCategory,
                    DataContract.ViewAwardEntry.FILTER_CATEGORY_DEFAULT);
            final ListFilterSpinner spinnerCategory = new ListFilterSpinner(
                    context, view, listFilterCategory,
                    R.id.spnFilterCategory, R.id.frameFilterCategory,
                    R.array.filter_category_pref_display);

            // OK button
            View txtOk = view.findViewById(R.id.txtOk);
            txtOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            // Clear Filters button
            View txtClearFilters = view.findViewById(R.id.txtClearFilters);
            txtClearFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinnerGenre.clear();
                    spinnerWishlist.clear();
                    spinnerWatched.clear();
                    spinnerFavourite.clear();
                    spinnerCategory.clear();
                }
            });

        }

        return view;
    }

    /**
     * A class representing a filter for the list.
     */
    private class ListFilter {

        private final @StringRes int mFilterPrefKeyResId;
        private final String[] mFilterValues;
        private final String mFilterValueDefault;

        /**
         * Construct the list filter.
         * @param filterPrefKeyResId the id of the shared preference which stores the filter value
         * @param filterPrefValues an array of Strings containing the valid values of the filter
         * @param filterValueDefault the default value of the filter
         */
        ListFilter(@StringRes final int filterPrefKeyResId, @Nullable String[] filterPrefValues,
                   @NonNull final String filterValueDefault) {

            mFilterPrefKeyResId = filterPrefKeyResId;
            mFilterValues = filterPrefValues == null
                    ? new String[]{ filterValueDefault } : filterPrefValues;
            mFilterValueDefault = filterValueDefault;
        }

        /**
         * Return the filter values.
         * @return the filter values
         */
        @NonNull
        String[] getFilterValues() {
            return mFilterValues;
        }

        /**
         * Return the filter values.
         * @return the filter values
         */
        @NonNull
        String getFilterValueDefault() {
            return mFilterValueDefault;
        }

        /**
         * Sets the filter value.
         * @param context the context
         * @param value the value to which to set the filter
         */
        void setValue(@NonNull Context context, @NonNull String value) {
            PrefUtils.setSharedPreferenceString(context, mFilterPrefKeyResId, value);
        }

        /**
         * Returns the filter value.
         * @param context the context
         * @return the filter value
         */
        @NonNull
        String getValue(@NonNull Context context) {
            return PrefUtils.getSharedPreferenceString(context,
                    mFilterPrefKeyResId, mFilterValueDefault);
        }

        /**
         * Returns the filter value at a position in the filter array.
         * @param index the position in the filter array
         * @return the filter value at position 'index' in the filter array,
         *         or null if index is not valid
         */
        @Nullable
        String getFilterValueForIndex(int index) {
            if (index >= 0 && index < getFilterValues().length) {
                return getFilterValues()[index];
            }
            return null;
        }

        /**
         * Returns the index of the current filter value in the array of filter values.
         * @param context the context
         * @return the index of the current filter value in the array of filter values
         */
        int getFilterIndex(@NonNull Context context) {
            return getFilterIndexForValue(getValue(context));
        }

        /**
         * Returns the index of a filter value in the array of filter values.
         * @param filterValue the filter value
         * @return the index of a filter value in the array of filter values
         */
        private int getFilterIndexForValue(@NonNull String filterValue) {
            return JavaUtils.getPositionInArray(getFilterValues(), filterValue);
        }

    }

    /**
     * A class representing a Spinner which displays the options for a list filter.
     */
    private class ListFilterSpinner {

        private final ListFilter mListFilter;
        private final Spinner mSpinner;

        /**
         * Construct a spinner which allows the list to be filtered.
         * @param context the context
         * @param view a view containing the spinner and its container
         * @param listFilter the list filter on which the spinner is based
         * @param spinnerViewIdRes the view id of the spinner
         * @param containerViewIdRes the view id of the frame containing the spinner
         * @param displayValuesArrayRes the array id of an array of Strings containing the
         *        displayed values of the spinner options, ordered in their display order. The order
         *        of the array must correspond to the order of the filter values in listFilter.
         */
        ListFilterSpinner(@NonNull Context context, @NonNull View view,
                          @NonNull final ListFilter listFilter,
                          @IdRes int spinnerViewIdRes, @IdRes int containerViewIdRes,
                          @ArrayRes int displayValuesArrayRes) {
            // Use the constructor which takes an array for the display values
            this(context, view, listFilter, spinnerViewIdRes, containerViewIdRes,
                    context.getResources().getStringArray(displayValuesArrayRes));
        }

        /**
         * Construct a spinner which allows the list to be filtered.
         * @param context the context
         * @param view a view containing the spinner and its container
         * @param listFilter the list filter on which the spinner is based
         * @param spinnerViewIdRes the view id of the spinner
         * @param containerViewIdRes the view id of the frame containing the spinner
         * @param filterDisplayValuesArray an array of Strings containing the displayed values
         *        of the spinner options, ordered in their display order. The order of the array
         *        must correspond to the order of the filter values in listFilter.
         */
        ListFilterSpinner(@NonNull Context context, @NonNull View view,
                          @NonNull final ListFilter listFilter,
                          @IdRes int spinnerViewIdRes, @IdRes int containerViewIdRes,
                          String[] filterDisplayValuesArray) {

            mListFilter = listFilter;
            mSpinner = view.findViewById(spinnerViewIdRes);
            View spinnerContainer = view.findViewById(containerViewIdRes);

            // Set a listener on the spinner to change its background colour on focus
            setOnFocusChangeListener(context, mSpinner, spinnerContainer);

            // Set an adapter for the spinner
            spinnerSetAdapter(context, mSpinner, filterDisplayValuesArray);

            // Set the spinner to the position of the current filter value
            final int filterIndex = listFilter.getFilterIndex(context);
            mSpinner.setSelection(filterIndex == -1 ? 0 : filterIndex);

            // Add a listener to the spinner to change the filter value on item selection
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        String currentFilterValue = listFilter.getValue(context);
                        String newFilterValue = listFilter.getFilterValueForIndex(position);
                        // If the selected spinner option does not match the current filter value,
                        // update the filter value
                        if (newFilterValue != null && !newFilterValue.equals(currentFilterValue)) {
                            listFilter.setValue(context, newFilterValue);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

        }

        /**
         * Sets the spinner to its default position.
         */
        void clear() {
            int filterIndexDefault =
                    mListFilter.getFilterIndexForValue(mListFilter.getFilterValueDefault());
            mSpinner.setSelection(filterIndexDefault);
        }

        /**
         * Set the OnFocusChangeListener for a filter spinner.
         * This is for D-Pad operation; the background colour of the spinner which has
         * focus is changed, so the user knows which spinner has focus.
         * @param context the context
         * @param spinner the spinner
         * @param spinnerContainer the view that contains the spinner
         */
        private void setOnFocusChangeListener(@NonNull final Context context,
                                  @NonNull final Spinner spinner, final View spinnerContainer) {

            // For D-Pad operation, change the background colour of the spinner container when the
            // spinner has focus.
            spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    @ColorRes int backgroundColorRes;
                    if (hasFocus) {
                        backgroundColorRes = R.color.green_100;
                    } else {
                        backgroundColorRes = R.color.green_50;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        spinnerContainer.setBackgroundColor(context.getResources()
                                .getColor(backgroundColorRes, view.getContext().getTheme()));
                    } else {
                        spinnerContainer.setBackgroundColor(context.getResources()
                                .getColor(backgroundColorRes));
                    }
                }
            });
        }

        /**
         * Creates an array adapter and applies it to a spinner.
         * @param context the context
         * @param spinner the spinner to which the adapter is to be applied
         * @param displayValuesArrayRes an array of strings to be displayed by the spinner
         */
        private void spinnerSetAdapter(@NonNull Context context, @NonNull Spinner spinner,
                                       @ArrayRes int displayValuesArrayRes) {
            String[] textArray = context.getResources().getStringArray(displayValuesArrayRes);
            spinnerSetAdapter(context, spinner, textArray);
        }

        /**
         * Creates an array adapter and applies it to a spinner.
         * @param context the context
         * @param spinner the spinner to which the adapter is to be applied
         * @param displayValuesArray an array of strings to be displayed by the spinner
         */
        private void spinnerSetAdapter(@NonNull Context context, @NonNull Spinner spinner,
                                       String[] displayValuesArray) {
            // Create an ArrayAdapter using the default spinner layout and
            // an array of strings to display
            @SuppressWarnings("unchecked")
            ArrayAdapter<String> adapter = new ArrayAdapter(
                    context, android.R.layout.simple_spinner_item, displayValuesArray);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        }

    }

}
