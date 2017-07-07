package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.ViewAwardListParameters;
import uk.jumpingmouse.moviecompanion.utils.PrefUtils;

/**
 * A dialog fragment which allows the user to select a sort order for the award list.
 * @author Edmund Johnson.
 */

public class AwardListSortFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.award_list_sort_fragment, container);
        Context context = getActivity();

        if (context != null && view != null) {
            // Check the radio button which corresponds to the existing sort order
            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupSortOrder);
            String previousSortOrder = PrefUtils.getAwardListSortOrder(context);
            checkSortOrderRadioButton(radioGroup, previousSortOrder);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                /**
                 * Handle the clicking of a radio button.
                 * @param group the radio group containing the button that was clicked
                 * @param checkedId the resource id of the button that was clicked
                 */
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    // Change the sort order shared preference
                    Context context = getActivity();
                    if (context != null) {
                        String newSortOrder = getSortOrderForRadioButton(checkedId);
                        PrefUtils.setAwardListSortOrder(context, newSortOrder);
                    }
                    // Dismiss the dialog
                    dismiss();
                }
            });
        }
        return view;
    }

    /**
     * Check the radio button corresponding to a sort order.
     * @param radioGroup a radio group containing all the sort order radio buttons
     * @param sortOrder a sort order, e.g. "awardDate DESC"
     */
    private void checkSortOrderRadioButton(@NonNull RadioGroup radioGroup, @NonNull String sortOrder) {
        @IdRes int radioButtonResId = getRadioButtonForSortOrder(sortOrder);
        radioGroup.check(radioButtonResId);
    }

    /**
     * Returns the radio button corresponding to a sort order.
     * @param sortOrder a sort order
     * @return the resource identifier of the radio button corresponding to the sort order
     */
    private @IdRes int getRadioButtonForSortOrder(@NonNull String sortOrder) {
        switch (sortOrder) {
            case ViewAwardListParameters.SORT_ORDER_AWARD_DATE_ASC:
                return  R.id.radioAwardDateAsc;
            case ViewAwardListParameters.SORT_ORDER_AWARD_DATE_DESC:
                return  R.id.radioAwardDateDesc;
            case ViewAwardListParameters.SORT_ORDER_TITLE_ASC:
                return R.id.radioTitleAsc;
            case ViewAwardListParameters.SORT_ORDER_TITLE_DESC:
                return R.id.radioTitleDesc;
            case ViewAwardListParameters.SORT_ORDER_RUNTIME_ASC:
                return R.id.radioRuntimeAsc;
            case ViewAwardListParameters.SORT_ORDER_RUNTIME_DESC:
                return R.id.radioRuntimeDesc;
            default:
                return R.id.radioAwardDateDesc;
        }
    }

    /**
     * Returns the sort order corresponding to a radio button.
     * @param radioButtonResId the resource identifier of a radio button representing a sort order
     * @return the sort order corresponding to the radio button
     */
    @NonNull
    private String getSortOrderForRadioButton(@IdRes int radioButtonResId) {
        switch (radioButtonResId) {
            case R.id.radioAwardDateAsc:
                return ViewAwardListParameters.SORT_ORDER_AWARD_DATE_ASC;
            case R.id.radioAwardDateDesc:
                return ViewAwardListParameters.SORT_ORDER_AWARD_DATE_DESC;
            case R.id.radioTitleAsc:
                return ViewAwardListParameters.SORT_ORDER_TITLE_ASC;
            case R.id.radioTitleDesc:
                return ViewAwardListParameters.SORT_ORDER_TITLE_DESC;
            case R.id.radioRuntimeAsc:
                return ViewAwardListParameters.SORT_ORDER_RUNTIME_ASC;
            case R.id.radioRuntimeDesc:
                return ViewAwardListParameters.SORT_ORDER_RUNTIME_DESC;
            default:
                return ViewAwardListParameters.SORT_ORDER_DEFAULT;
        }
    }
}
