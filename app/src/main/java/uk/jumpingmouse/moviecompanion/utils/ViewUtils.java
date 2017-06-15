package uk.jumpingmouse.moviecompanion.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Award;

/**
 * Class containing utility methods related to the view.
 * @author Edmund Johnson
 */
public class ViewUtils {

    /** The singleton instance of this class. */
    private static ViewUtils sViewUtils = null;

    /** The date format in which award dates are stored in the database, e.g. "170602". */
    private static final SimpleDateFormat AWARD_DATE_FORMAT_STORED =
            new SimpleDateFormat("yyMMdd", Locale.getDefault());
    /** The date format in which award dates are displayed, e.g. "02 Jun 17. */
    private static final SimpleDateFormat AWARD_DATE_FORMAT_DISPLAYED =
            new SimpleDateFormat("dd MMM yy", Locale.getDefault());

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
    // Action bar

    /**
     * Initialise the app bar for an AppCompatActivity which is using a Toolbar.
     * @param activity the activity for which the app bar is displayed
     * @param toolbarResId the resource identifier of the toolbar View, e.g. R.id.tbAppBar
     * @param titleText the app bar title text, e.g. "Daily Benefit Settings"
     */
    public void initialiseActionBar(@NonNull final AppCompatActivity activity,
                                    @SuppressWarnings("SameParameterValue") @IdRes final int toolbarResId,
                                    @Nullable final String titleText) {
        // Set the app bar to be the toolbar
        Toolbar toolbar = initialiseToolbar(activity, toolbarResId, titleText);
        activity.setSupportActionBar(toolbar);

        // If required, display the up arrow in the app bar
        ActionBar appBar = activity.getSupportActionBar();
    }

    /**
     * Initialises and returns a toolbar, i.e. sets its title text and font.
     * @param activity the activity containing the toolbar
     * @param toolbarResId the resource identifier of the toolbar View, e.g. R.id.tbAppBar
     * @param titleText the title text
     * @return the initialised toolbar
     */
    @Nullable
    private Toolbar initialiseToolbar(@NonNull final Activity activity, @IdRes final int toolbarResId,
                                      @Nullable final String titleText) {
        // Find the toolbar
        Toolbar toolbar = (Toolbar) activity.findViewById(toolbarResId);
        if (toolbar != null) {
            // Set the title text
            toolbar.setTitle(titleText);
        }
        return toolbar;
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

    //---------------------------------------------------------------------
    // Conversion of field values into displayable strings

    /**
     * Returns the displayable category text corresponding to a category code.
     * @param context the context
     * @param categoryCode the category code, e.g. "M"
     * @return the category text corresponding to the category code, e.g. "Movie of the Week"
     */
    public String getCategoryDisplayable(@Nullable Context context, @Nullable String categoryCode) {
        if (context == null || categoryCode == null) {
            return "?";
        }
        switch (categoryCode) {
            case Award.CATEGORY_MOVIE:
                return context.getString(R.string.category_text_movie);
            case Award.CATEGORY_DVD:
                return context.getString(R.string.category_text_dvd);
            default:
                return context.getString(R.string.category_text_unknown);
        }
    }

    /**
     * Returns the award date in a displayable format.
     * @param awardDate the award date as stored in the database, e.g. "170602"
     * @return the award date in a displayable format, e.g. "02 Jun 17"
     */
    public String getAwardDateDisplayable(@Nullable String awardDate) {
        if (awardDate == null) {
            return "?";
        }
        Date dateAwardDate = DateUtils.toDate(AWARD_DATE_FORMAT_STORED, awardDate);
        return dateAwardDate == null ? "?" :
                DateUtils.toString(AWARD_DATE_FORMAT_DISPLAYED, dateAwardDate);
    }

    //---------------------------------------------------------------------
    // Message display methods

    /**
     * Display an informational message as a toast.
     * @param context the context
     * @param messageResId the string resource id of the message to be displayed
     */
    public void displayInfoMessage(@Nullable Context context, @StringRes int messageResId) {
        displayInfoMessage(context, messageResId, false);
    }

    /**
     * Display an informational message as a toast.
     * @param context the context
     * @param messageResId the string resource id of the message to be displayed
     * @param longDuration if true, a long duration message is displayed,
     *                    otherwise a short duration one
     */
    private void displayInfoMessage(@Nullable Context context, @StringRes int messageResId,
                                    @SuppressWarnings("SameParameterValue") boolean longDuration) {
        if (context != null) {
            displayToast(context, context.getString(messageResId), longDuration);
        }
    }

    /**
     * Display an informational message as a toast.
     * @param context the context
     * @param message the message to be displayed
     */
    public void displayInfoMessage(@Nullable Context context, @NonNull String message) {
        displayInfoMessage(context, message, false);
    }

    /**
     * Display an informational message as a toast.
     * @param context the context
     * @param message the message to be displayed
     * @param longDuration if true, a long duration message is displayed,
     *                    otherwise a short duration one
     */
    public void displayInfoMessage(@Nullable Context context, @NonNull String message,
                                   boolean longDuration) {
        if (context != null) {
            displayToast(context, message, longDuration);
        }
    }

    /**
     * Display a message as a short toast.
     * @param context the context
     * @param message the message to be displayed
     */
    private void displayToast(@NonNull Context context, @NonNull String message) {
        displayToast(context, message, false);
    }

    /**
     * Display a message as a toast.
     * @param context the context
     * @param message the message to be displayed
     * @param longDuration if true, a long duration toast is displayed,
     *                    otherwise a short duration one
     */
    private void displayToast(@NonNull Context context, @NonNull String message, boolean longDuration) {
        int length = longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, length);
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
