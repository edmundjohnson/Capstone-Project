package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.ViewAwardListParameters;
import uk.jumpingmouse.moviecompanion.utils.PrefUtils;

/**
 * A dialog fragment which allows the user to select the filter options for the award list.
 * @author Edmund Johnson.
 */

public class AwardListFilterFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.award_list_filter_fragment, container);
        Context context = getActivity();

        if (context != null && view != null) {

            // Wishlist filter

            String previousFilterWishlist = PrefUtils.getAwardListFilterWishlist(context);
            Spinner spinnerFilterWishlist = (Spinner) view.findViewById(R.id.spinnerFilterWishlist);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterWishlistAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_wishlist, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterWishlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinnerFilterWishlist.setAdapter(filterWishlistAdapter);
            setSpinnerPositionByValue(spinnerFilterWishlist, previousFilterWishlist);

            spinnerFilterWishlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        // Change the wishlist filter shared preference
                        String newFilterWishlist = ViewAwardListParameters.FILTER_WISHLIST_OPTIONS[position];
                        PrefUtils.setAwardListFilterWishlist(context, newFilterWishlist);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            // Watched filter

            String previousFilterWatched = PrefUtils.getAwardListFilterWatched(context);
            Spinner spinnerFilterWatched = (Spinner) view.findViewById(R.id.spinnerFilterWatched);
            setSpinnerPositionByValue(spinnerFilterWatched, previousFilterWatched);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterWatchedAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_watched, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterWatchedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinnerFilterWatched.setAdapter(filterWatchedAdapter);

            spinnerFilterWatched.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        // Change the watched filter shared preference
                        String newFilterValue = ViewAwardListParameters.FILTER_WATCHED_OPTIONS[position];
                        PrefUtils.setAwardListFilterWatched(context, newFilterValue);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do nothing
                }
            });

            // Favourite filter

            String previousFilterFavourite = PrefUtils.getAwardListFilterFavourite(context);
            Spinner spinnerFilterFavourite = (Spinner) view.findViewById(R.id.spinnerFilterFavourite);
            setSpinnerPositionByValue(spinnerFilterFavourite, previousFilterFavourite);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> filterFavouriteAdapter = ArrayAdapter.createFromResource(context,
                    R.array.filter_favourite, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            filterFavouriteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinnerFilterFavourite.setAdapter(filterFavouriteAdapter);

            spinnerFilterFavourite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // The activity may no longer be there
                    Context context = getActivity();
                    if (context != null) {
                        String previousFilterValue = PrefUtils.getAwardListFilterFavourite(context);
                        // Change the watched filter shared preference
                        String newFilterValue = ViewAwardListParameters.FILTER_FAVOURITE_OPTIONS[position];
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
//                                checkedId, ViewAwardListParameters.FILTER_WISHLIST_DEFAULT);
//                        PrefUtils.setAwardListFilterWishlist(context, newFilterWishlist);
//                    }
//                    // Dismiss the dialog
//                    dismiss();
//                }
//            });

            // OK button
            View txtOk = view.findViewById(R.id.txtOk);
            txtOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        }

        return view;
    }

    private void setSpinnerPositionByValue(@NonNull Spinner spinner, @Nullable String value) {
        if (value == null) {
            return;
        }
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

//    /**
//     * Check the radio button corresponding to a filter value.
//     * @param radioGroup a radio group containing all the filter radio buttons
//     * @param filterValue the filter value, e.g. ViewAward.FILTER_WISHLIST_ONLY
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
//            case ViewAwardListParameters.FILTER_WISHLIST_ANY:
//                return  R.id.radioWishlistAny;
//            case ViewAwardListParameters.FILTER_WISHLIST_ONLY:
//                return  R.id.radioWishlistOnly;
//            case ViewAwardListParameters.FILTER_WISHLIST_EXCLUDE:
//                return  R.id.radioWishlistExclude;
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
//                return ViewAwardListParameters.FILTER_WISHLIST_ANY;
//            case R.id.radioWishlistOnly:
//                return ViewAwardListParameters.FILTER_WISHLIST_ONLY;
//            case R.id.radioWishlistExclude:
//                return ViewAwardListParameters.FILTER_WISHLIST_EXCLUDE;
//            default:
//                return defaultValue;
//        }
//    }

}
