package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

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

            // Check the radio button which corresponds to the existing wishlist filter
            RadioGroup radioGroupFilterWishlist = (RadioGroup) view.findViewById(R.id.radioGroupFilterWishlist);
            String previousFilterWishlist = PrefUtils.getAwardListFilterWishlist(context);
            checkRadioButtonForFilterValue(
                    radioGroupFilterWishlist, previousFilterWishlist, R.id.radioWishlistAny);

            radioGroupFilterWishlist.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                /**
                 * Handle the clicking of a radio button.
                 * @param group the radio group containing the button that was clicked
                 * @param checkedId the resource id of the button that was clicked
                 */
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    // Change the wishlist filter shared preference
                    Context context = getActivity();
                    if (context != null) {
                        String newFilterWishlist = getFilterValueForRadioButton(
                                checkedId, ViewAwardListParameters.FILTER_WISHLIST_DEFAULT);
                        PrefUtils.setAwardListFilterWishlist(context, newFilterWishlist);
                    }
                    // Dismiss the dialog
                    dismiss();
                }
            });
        }
        return view;
    }

    /**
     * Check the radio button corresponding to a filter value.
     * @param radioGroup a radio group containing all the filter radio buttons
     * @param filterValue the filter value, e.g. ViewAward.FILTER_WISHLIST_ONLY
     */
    private void checkRadioButtonForFilterValue(@NonNull RadioGroup radioGroup, @Nullable String filterValue,
                                                @IdRes int defaultRadioButton) {
        @IdRes int radioButtonResId = getRadioButtonForFilter(filterValue, R.id.radioWishlistAny);
        radioGroup.check(radioButtonResId);
    }

    /**
     * Returns the radio button corresponding to a filter value.
     * @param filterValue the filter value
     * @param defaultRadioButton the value to return if the radio button cannot be determined
     * @return the resource identifier of the radio button corresponding to the filter
     */
    private @IdRes int getRadioButtonForFilter(@Nullable String filterValue, @IdRes int defaultRadioButton) {
        if (filterValue == null) {
            return defaultRadioButton;
        }
        switch (filterValue) {
            case ViewAwardListParameters.FILTER_WISHLIST_ANY:
                return  R.id.radioWishlistAny;
            case ViewAwardListParameters.FILTER_WISHLIST_ONLY:
                return  R.id.radioWishlistOnly;
            case ViewAwardListParameters.FILTER_WISHLIST_EXCLUDE:
                return  R.id.radioWishlistExclude;
            default:
                return defaultRadioButton;
        }
    }

    /**
     * Returns the filter value corresponding to a radio button.
     * @param radioButtonResId the resource identifier of a radio button representing a filter value
     * @param defaultValue the value to return if the filter value cannot be determined
     * @return the filter value corresponding to the radio button
     */
    @NonNull
    private String getFilterValueForRadioButton(@IdRes int radioButtonResId, @NonNull String defaultValue) {
        switch (radioButtonResId) {
            case R.id.radioWishlistAny:
                return ViewAwardListParameters.FILTER_WISHLIST_ANY;
            case R.id.radioWishlistOnly:
                return ViewAwardListParameters.FILTER_WISHLIST_ONLY;
            case R.id.radioWishlistExclude:
                return ViewAwardListParameters.FILTER_WISHLIST_EXCLUDE;
            default:
                return defaultValue;
        }
    }
}
