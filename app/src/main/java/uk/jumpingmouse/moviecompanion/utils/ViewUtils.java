package uk.jumpingmouse.moviecompanion.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import uk.jumpingmouse.moviecompanion.R;

/**
 * Class containing utility methods related to the view.
 * @author Edmund Johnson
 */
public class ViewUtils {

    /** The singleton instance of this class. */
    private static ViewUtils sViewUtils = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static ViewUtils getInstance() {
        if (sViewUtils == null) {
            sViewUtils = new ViewUtils();
        }
        return sViewUtils;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private ViewUtils() {
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Sets the visibility of a view to VISIBLE.
     * @param view the view
     */
    public void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the visibility of a view to GONE.
     * @param view the view
     */
    public void hideView(View view) {
        view.setVisibility(View.GONE);
    }

//    /**
//     * Toggles the visibility of a view between VISIBLE and GONE.
//     * @param view the view
//     */
//    public void toggleVisibility(View view) {
//        if (view.getVisibility() == View.VISIBLE) {
//            hideView(view);
//        } else {
//            showView(view);
//        }
//    }

    /**
     * Display an informational message as a toast.
     * @param context the context
     * @param messageResId the string resource id of the message to be displayed
     */
    public void displayInfoMessage(@Nullable Context context, @StringRes int messageResId) {
        if (context != null) {
            Toast toast = Toast.makeText(context, messageResId, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * Display an error message in a dialog.
     * @param context the context
     * @param messageResId the string resource id of the message to be displayed
     */
    public void displayErrorMessage(Context context, @StringRes int messageResId) {
        displayAlertDialog(context, R.string.error_title, messageResId);
    }

    /**
     * Display an alert dialog.
     * @param context the context
     * @param titleResId the string resource id of the dialog title
     * @param messageResId the string resource id of the dialog text
     */
    private static void displayAlertDialog(@Nullable Context context,
            @SuppressWarnings("SameParameterValue") @StringRes int titleResId,
            @StringRes int messageResId) {
        if (context != null) {
            new AlertDialog.Builder(context)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(titleResId)
                    .setMessage(messageResId)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }
    }

}
