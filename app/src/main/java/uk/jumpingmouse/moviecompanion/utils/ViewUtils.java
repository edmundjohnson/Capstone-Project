package uk.jumpingmouse.moviecompanion.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
     * Dismisses the keyboard if it is currently being displayed.
     * @param activity the current activity
     */
    public void dismissKeyboard(@Nullable Activity activity) {
        if (activity != null) {
            View view = activity.getWindow().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

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
            displayToast(context, context.getString(messageResId));
        }
    }

    /**
     * Display an informational message as a toast.
     * @param context the context
     * @param message the message to be displayed
     */
    public void displayInfoMessage(@Nullable Context context, @NonNull String message) {
        if (context != null) {
            displayToast(context, message);
        }
    }

    /**
     * Display a message as a toast.
     * @param context the context
     * @param message the message to be displayed
     */
    private void displayToast(@NonNull Context context, @NonNull String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Display an error message in a dialog.
     * @param context the context
     * @param messageResId the string resource id of the message to be displayed
     */
    public void displayErrorMessage(@Nullable Context context, @StringRes int messageResId) {
        if (context != null) {
            displayAlertDialog(context, R.string.error_title, context.getString(messageResId));
        }
    }

    /**
     * Display an error message in a dialog.
     * @param context the context
     * @param message the message to be displayed
     */
    public void displayErrorMessage(@Nullable Context context, @NonNull String message) {
        if (context != null) {
            displayAlertDialog(context, R.string.error_title, message);
        }
    }

    /**
     * Display an alert dialog.
     * @param context the context
     * @param titleResId the string resource id of the dialog title
     * @param message the dialog message
     */
    private static void displayAlertDialog(@NonNull Context context,
            @SuppressWarnings("SameParameterValue") @StringRes int titleResId, @NonNull String message) {

        new AlertDialog.Builder(context)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(titleResId)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

}
