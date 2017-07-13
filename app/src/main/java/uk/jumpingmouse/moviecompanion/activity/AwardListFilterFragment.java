package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;
import uk.jumpingmouse.moviecompanion.utils.PrefUtils;

/**
 * A dialog fragment which allows the user to select the filter options for the award list.
 * @author Edmund Johnson.
 */

public class AwardListFilterFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // do not display the default title bar, use the styled title bar in the layout xml
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.award_list_filter_fragment, container);
        Context context = getActivity();

        if (view != null && context != null && context.getResources() != null) {

            // Genre filter

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterGenreAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_genre_pref_display, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            final Spinner spnFilterGenre = (Spinner) view.findViewById(R.id.spnFilterGenre);
            spnFilterGenre.setAdapter(filterGenreAdapter);

            // Set the spinner to the position of the current filter value
            final String[] filterGenrePrefValues =
                    context.getResources().getStringArray(R.array.filter_genre_pref_key);
            String previousFilterGenre = PrefUtils.getAwardListFilterGenre(context);
            int positionGenre = JavaUtils.getPositionInArray(
                    filterGenrePrefValues, previousFilterGenre);
            spnFilterGenre.setSelection(positionGenre == -1 ? 0 : positionGenre);

            // Add a listener to the spinner
            spnFilterGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        String previousFilterValue = PrefUtils.getAwardListFilterGenre(context);
                        String newFilterValue = filterGenrePrefValues[position];
                        // If the filter has changed, update its shared preference
                        if (!newFilterValue.equals(previousFilterValue)) {
                            PrefUtils.setAwardListFilterGenre(context, newFilterValue);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            // Wishlist filter

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterWishlistAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_wishlist_pref_display, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterWishlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            final Spinner spnFilterWishlist = (Spinner) view.findViewById(R.id.spnFilterWishlist);
            spnFilterWishlist.setAdapter(filterWishlistAdapter);

            // Set the spinner to the position of the current filter value
            final String[] filterWishlistPrefValues =
                    context.getResources().getStringArray(R.array.filter_wishlist_pref_key);
            String previousFilterWishlist = PrefUtils.getAwardListFilterWishlist(context);
            int positionWishlist = JavaUtils.getPositionInArray(
                    filterWishlistPrefValues, previousFilterWishlist);
            spnFilterWishlist.setSelection(positionWishlist == -1 ? 0 : positionWishlist);

            // Add a listener to the spinner
            spnFilterWishlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        String previousFilterValue = PrefUtils.getAwardListFilterWishlist(context);
                        String newFilterValue = filterWishlistPrefValues[position];
                        // If the filter has changed, update its shared preference
                        if (!newFilterValue.equals(previousFilterValue)) {
                            PrefUtils.setAwardListFilterWishlist(context, newFilterValue);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            // Watched filter

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterWatchedAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_watched_pref_display, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterWatchedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            final Spinner spnFilterWatched = (Spinner) view.findViewById(R.id.spnFilterWatched);
            spnFilterWatched.setAdapter(filterWatchedAdapter);

            // Set the spinner to the position of the current filter value
            String previousFilterWatched = PrefUtils.getAwardListFilterWatched(context);
            final String[] filterWatchedPrefValues =
                    context.getResources().getStringArray(R.array.filter_watched_pref_key);
            int positionWatched = JavaUtils.getPositionInArray(
                    filterWatchedPrefValues, previousFilterWatched);
            spnFilterWatched.setSelection(positionWatched == -1 ? 0 : positionWatched);

            // Add a listener to the spinner
            spnFilterWatched.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        String previousFilterValue = PrefUtils.getAwardListFilterWatched(context);
                        String newFilterValue = filterWatchedPrefValues[position];
                        // If the filter has changed, update its shared preference
                        if (!newFilterValue.equals(previousFilterValue)) {
                            PrefUtils.setAwardListFilterWatched(context, newFilterValue);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            // Favourite filter

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterFavouriteAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_favourite_pref_display, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterFavouriteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            final Spinner spnFilterFavourite = (Spinner) view.findViewById(R.id.spnFilterFavourite);
            spnFilterFavourite.setAdapter(filterFavouriteAdapter);

            // Set the spinner to the position of the current filter value
            String previousFilterFavourite = PrefUtils.getAwardListFilterFavourite(context);
            final String[] filterFavouritePrefValues =
                    context.getResources().getStringArray(R.array.filter_favourite_pref_key);
            int positionFavourite = JavaUtils.getPositionInArray(
                    filterFavouritePrefValues, previousFilterFavourite);
            spnFilterFavourite.setSelection(positionFavourite == -1 ? 0 : positionFavourite);

            // Add a listener to the spinner
            spnFilterFavourite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        String previousFilterValue = PrefUtils.getAwardListFilterFavourite(context);
                        String newFilterValue = filterFavouritePrefValues[position];
                        // If the filter has changed, update its shared preference
                        if (!newFilterValue.equals(previousFilterValue)) {
                            PrefUtils.setAwardListFilterFavourite(context, newFilterValue);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            // Category filter

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterCategoryAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_category_pref_display, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            final Spinner spnFilterCategory = (Spinner) view.findViewById(R.id.spnFilterCategory);
            spnFilterCategory.setAdapter(filterCategoryAdapter);

            // Set the spinner to the position of the current filter value
            String previousFilterCategory = PrefUtils.getAwardListFilterCategory(context);
            final String[] filterCategoryPrefValues =
                    context.getResources().getStringArray(R.array.filter_category_pref_key);
            int positionCategory = JavaUtils.getPositionInArray(
                    filterCategoryPrefValues, previousFilterCategory);
            spnFilterCategory.setSelection(positionCategory == -1 ? 0 : positionCategory);

            // Add a listener to the spinner
            spnFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        String previousFilterValue = PrefUtils.getAwardListFilterCategory(context);
                        String newFilterValue = filterCategoryPrefValues[position];
                        // If the filter has changed, update its shared preference
                        if (!newFilterValue.equals(previousFilterValue)) {
                            PrefUtils.setAwardListFilterCategory(context, newFilterValue);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            // OK button
            View txtOk = view.findViewById(R.id.txtOk);
            txtOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            // Clear Filters button
            View txtClearFilters = view.findViewById(R.id.txtClearFilters);
            txtClearFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spnFilterGenre.setSelection(0);
                    spnFilterWishlist.setSelection(0);
                    spnFilterWatched.setSelection(0);
                    spnFilterFavourite.setSelection(0);
                }
            });

//            // Check the radio button which corresponds to the existing wishlist filter
//            RadioGroup radioGroupFilterWishlist = (RadioGroup) view.findViewById(R.id.radioGroupFilterWishlist);
//            checkRadioButtonForFilterValue(
//                    radioGroupFilterWishlist, previousFilterWishlist, R.id.radioWishlistAny);
//
//            radioGroupFilterWishlist.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                /**
//                 * Handle the clicking of a radio button.
//                 *
//                 * @param group     the radio group containing the button that was clicked
//                 * @param checkedId the resource id of the button that was clicked
//                 */
//                @Override
//                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                    // The activity may no longer be there
//                    Context context = getActivity();
//                    if (context != null) {
//                        // Change the wishlist filter shared preference
//                        String newFilterWishlist = getFilterValueForRadioButton(
//                                checkedId, DataContract.ViewAwardEntry.FILTER_WISHLIST_DEFAULT);
//                        PrefUtils.setAwardListFilterWishlist(context, newFilterWishlist);
//                    }
//                    // Dismiss the dialog
//                    dismiss();
//                }
//            });

        }

        return view;
    }

//    /**
//     * Check the radio button corresponding to a filter value.
//     * @param radioGroup a radio group containing all the filter radio buttons
//     * @param filterValue the filter value, e.g. DataContract.ViewAwardEntry.FILTER_WISHLIST_SHOW
//     */
//    private void checkRadioButtonForFilterValue(@NonNull RadioGroup radioGroup, @Nullable String filterValue,
//                                                @IdRes int defaultRadioButton) {
//        @IdRes int radioButtonResId = getRadioButtonForFilter(filterValue, R.id.radioWishlistAny);
//        radioGroup.check(radioButtonResId);
//    }

//    /**
//     * Returns the radio button corresponding to a filter value.
//     * @param filterValue the filter value
//     * @param defaultRadioButton the value to return if the radio button cannot be determined
//     * @return the resource identifier of the radio button corresponding to the filter
//     */
//    private @IdRes int getRadioButtonForFilter(@Nullable String filterValue, @IdRes int defaultRadioButton) {
//        if (filterValue == null) {
//            return defaultRadioButton;
//        }
//        switch (filterValue) {
//            case DataContract.ViewAwardEntry.FILTER_WISHLIST_ANY:
//                return  R.id.radioWishlistAny;
//            case DataContract.ViewAwardEntry.FILTER_WISHLIST_SHOW:
//                return  R.id.radioWishlistShow;
//            case DataContract.ViewAwardEntry.FILTER_WISHLIST_HIDE:
//                return  R.id.radioWishlistHide;
//            default:
//                return defaultRadioButton;
//        }
//    }

//    /**
//     * Returns the filter value corresponding to a radio button.
//     * @param radioButtonResId the resource identifier of a radio button representing a filter value
//     * @param defaultValue the value to return if the filter value cannot be determined
//     * @return the filter value corresponding to the radio button
//     */
//    @NonNull
//    private String getFilterValueForRadioButton(@IdRes int radioButtonResId, @NonNull String defaultValue) {
//        switch (radioButtonResId) {
//            case R.id.radioWishlistAny:
//                return DataContract.ViewAwardEntry.FILTER_WISHLIST_ANY;
//            case R.id.radioWishlistShow:
//                return DataContract.ViewAwardEntry.FILTER_WISHLIST_SHOW;
//            case R.id.radioWishlistHide:
//                return DataContract.ViewAwardEntry.FILTER_WISHLIST_HIDE;
//            default:
//                return defaultValue;
//        }
//    }

}
