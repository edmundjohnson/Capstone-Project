package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
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
            final Spinner spnFilterGenre = (Spinner) view.findViewById(R.id.spnFilterGenre);
            View frameFilterGenre = view.findViewById(R.id.frameFilterGenre);
            spinnerSetProperties(context, spnFilterGenre, frameFilterGenre);

            // Set an adapter for the spinner
            spinnerSetAdapter(context, spnFilterGenre, R.array.filter_genre_pref_display);

            // Set the spinner to the position of the current filter value
            final String[] filterGenrePrefValues =
                    context.getResources().getStringArray(R.array.filter_genre_pref_key);
            String filterGenreValue = PrefUtils.getAwardListFilterGenre(context);
            int positionGenre = JavaUtils.getPositionInArray(
                    filterGenrePrefValues, filterGenreValue);
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
            final Spinner spnFilterWishlist = (Spinner) view.findViewById(R.id.spnFilterWishlist);
            View frameFilterWishlist = view.findViewById(R.id.frameFilterWishlist);
            spinnerSetProperties(context, spnFilterWishlist, frameFilterWishlist);

            // Set an adapter for the spinner
            spinnerSetAdapter(context, spnFilterWishlist, R.array.filter_wishlist_pref_display);

            // Set the spinner to the position of the current filter value
            final String[] filterWishlistPrefValues =
                    context.getResources().getStringArray(R.array.filter_wishlist_pref_key);
            String filterWishlistValue = PrefUtils.getAwardListFilterWishlist(context);
            int positionWishlist = JavaUtils.getPositionInArray(
                    filterWishlistPrefValues, filterWishlistValue);
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
            final Spinner spnFilterWatched = (Spinner) view.findViewById(R.id.spnFilterWatched);
            View frameFilterWatched = view.findViewById(R.id.frameFilterWatched);
            spinnerSetProperties(context, spnFilterWatched, frameFilterWatched);

            // Set an adapter for the spinner
            spinnerSetAdapter(context, spnFilterWatched, R.array.filter_watched_pref_display);

            // Set the spinner to the position of the current filter value
            String filterWatchedValue = PrefUtils.getAwardListFilterWatched(context);
            final String[] filterWatchedPrefValues =
                    context.getResources().getStringArray(R.array.filter_watched_pref_key);
            int positionWatched = JavaUtils.getPositionInArray(
                    filterWatchedPrefValues, filterWatchedValue);
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
            final Spinner spnFilterFavourite = (Spinner) view.findViewById(R.id.spnFilterFavourite);
            View frameFilterFavourite = view.findViewById(R.id.frameFilterFavourite);
            spinnerSetProperties(context, spnFilterFavourite, frameFilterFavourite);

            // Set an adapter for the spinner
            spinnerSetAdapter(context, spnFilterFavourite, R.array.filter_favourite_pref_display);

            // Set the spinner to the position of the current filter value
            String filterFavouriteValue = PrefUtils.getAwardListFilterFavourite(context);
            final String[] filterFavouritePrefValues =
                    context.getResources().getStringArray(R.array.filter_favourite_pref_key);
            int positionFavourite = JavaUtils.getPositionInArray(
                    filterFavouritePrefValues, filterFavouriteValue);
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
            final Spinner spnFilterCategory = (Spinner) view.findViewById(R.id.spnFilterCategory);
            View frameFilterCategory = view.findViewById(R.id.frameFilterCategory);
            spinnerSetProperties(context, spnFilterCategory, frameFilterCategory);

            // Set an adapter for the spinner
            spinnerSetAdapter(context, spnFilterCategory, R.array.filter_category_pref_display);

            // Set the spinner to the position of the current filter value
            String filterCategoryValue = PrefUtils.getAwardListFilterCategory(context);
            final String[] filterCategoryPrefValues =
                    context.getResources().getStringArray(R.array.filter_category_pref_key);
            int positionCategory = JavaUtils.getPositionInArray(
                    filterCategoryPrefValues, filterCategoryValue);
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

        }

        return view;
    }

    /**
     * Creates an array adapter and applies it to a spinner.
     * @param context the context
     * @param spinner the spinner to which the adapter is to be applied
     * @param textArrayResId an array of text strings to be displayed by the spinner
     */
    private void spinnerSetAdapter(@NonNull Context context, @NonNull Spinner spinner,
                                   @ArrayRes int textArrayResId) {
        // Create an ArrayAdapter using an array of strings to display and the default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                textArrayResId, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    /**
     * Set the properties of a spinner.
     * @param context the context
     * @param spinner the spinner
     * @param spinnerContainer the view that contains the spinner
     */
    private void spinnerSetProperties(@NonNull final Context context, @NonNull final Spinner spinner,
                                      final View spinnerContainer) {

        // For D-Pad operation, change the background colour of the spinner container when the
        // spinner has focus.
        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                @ColorRes int backgroundResId;
                if (hasFocus) {
                    backgroundResId = R.color.green_100;
                } else {
                    backgroundResId = R.color.green_50;
                }
                spinnerContainer.setBackgroundColor(context.getResources().getColor(backgroundResId));
            }
        });
    }

}
