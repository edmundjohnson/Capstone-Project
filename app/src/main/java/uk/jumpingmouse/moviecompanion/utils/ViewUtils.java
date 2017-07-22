package uk.jumpingmouse.moviecompanion.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
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
import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Class containing utility methods related to the view.
 * @author Edmund Johnson
 */
public final class ViewUtils {

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
     * @param isUpArrowDisplayed whether the up arrow is to be displayed in the app bar
     * @param backgroundResId the resource id for the background colour.  If this is 0, the
     *                        default background colour is used
     */
    public void initialiseAppBar(@NonNull final AppCompatActivity activity,
                                 @SuppressWarnings("SameParameterValue") @IdRes final int toolbarResId,
                                 @Nullable final String titleText,
                                 final boolean isUpArrowDisplayed, @ColorRes int backgroundResId) {
        Toolbar toolbar = initialiseToolbar(activity, toolbarResId, titleText, backgroundResId);

        // Set the app bar to be the toolbar
        activity.setSupportActionBar(toolbar);
        ActionBar appBar = activity.getSupportActionBar();
        // If required, display the up arrow in the app bar
        if (appBar != null) {
            displayUpArrowInAppBar(appBar, isUpArrowDisplayed);
        }
    }

    /**
     * Initialises and returns a toolbar, i.e. sets its title text and font.
     * @param activity the activity containing the toolbar
     * @param toolbarResId the resource identifier of the toolbar View, e.g. R.id.tbAppBar
     * @param titleText the title text
     * @param backgroundResId the resource id for the background colour.  If this is 0, the
     *                        default background colour is used
     * @return the initialised toolbar
     */
    @Nullable
    private Toolbar initialiseToolbar(@NonNull final Activity activity, @IdRes final int toolbarResId,
                                      @Nullable final String titleText, @ColorRes int backgroundResId) {
        // Find the toolbar
        Toolbar toolbar = activity.findViewById(toolbarResId);
        if (toolbar != null) {
            // Set the title text
            toolbar.setTitle(titleText);
            // Set the background
            if (backgroundResId != 0) {
                toolbar.setBackgroundResource(backgroundResId);
            }
        }
        return toolbar;
    }

    /**
     * If required, display an up arrow in an app bar, for navigating to the parent activity.
     * @param appBar the app bar
     * @param isUpArrowDisplayed whether the up arrow is to be displayed in the app bar
     */
    private void displayUpArrowInAppBar(@Nullable final ActionBar appBar, final boolean isUpArrowDisplayed) {
        if (appBar != null) {
            appBar.setDisplayHomeAsUpEnabled(isUpArrowDisplayed);
        }
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
                InputMethodManager imm = (InputMethodManager)
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE);
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

    //---------------------------------------------------------------------
    // Conversion of field values into displayable strings

    /**
     * Returns the runtime text corresponding to a runtime value.
     * @param context the context
     * @param runtime the runtime in minutes, e.g. 114
     * @return the runtime text corresponding to runtime, e.g. "114 mins", or an empty
     *         string if the runtime is unknown
     */
    public String getRuntimeText(@Nullable Context context, int runtime) {
        if (context == null || context.getResources() == null) {
            return "?";
        }
        if (runtime == Movie.RUNTIME_UNKNOWN || runtime <= 0) {
            return "";
        }
        return context.getResources().getQuantityString(R.plurals.runtime_text, runtime, runtime);
    }

    /**
     * Returns the award category text corresponding to an award category code.
     * @param context the context
     * @param categoryCode the award category code, e.g. "M"
     * @return the award category text corresponding to the award category code, e.g. "Movie of the Week"
     */
    public String getCategoryText(@Nullable Context context, @Nullable String categoryCode) {
        if (context == null) {
            return "?";
        } else if (categoryCode == null) {
            return context.getString(R.string.category_text_unknown);
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
     * Returns the category resource id corresponding to a category code.
     * @param categoryCode the category code, e.g. "M"
     * @return the category resource id corresponding to the category code
     */
    public @DrawableRes int getCategoryRes(@Nullable String categoryCode) {
        if (categoryCode == null) {
            return R.drawable.ic_local_movies_24dp;
        } else {
            switch (categoryCode) {
                case Award.CATEGORY_DVD:
                    return R.drawable.ic_album_24dp;
                case Award.CATEGORY_MOVIE:
                default:
                    return R.drawable.ic_local_movies_24dp;
            }
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
        Date dateAwardDate = JavaUtils.toDate(AWARD_DATE_FORMAT_STORED, awardDate);
        return dateAwardDate == null
                ? "?" : JavaUtils.toString(AWARD_DATE_FORMAT_DISPLAYED, dateAwardDate);
    }

    /**
     * Generates and returns a lighter version of a colour.
     * @param color the colour to lighten
     * @return the lighter version of the colour
     */
    public int lightenColor(int color) {
        // Determine the factor to lighten by, based on the darkest component
        int darkest = Math.min(Color.red(color),
                Math.min(Color.green(color), Color.blue(color)));
        float factor = getLighteningFactor(darkest);

        float r = Color.red(color) * factor;
        float g = Color.green(color) * factor;
        float b = Color.blue(color) * factor;
        int ir = Math.min(255, (int) r);
        int ig = Math.min(255, (int) g);
        int ib = Math.min(255, (int) b);
        int ia = Color.alpha(color);
        return Color.argb(ia, ir, ig, ib);
    }

    /**
     * Determine and return the lightening factor for a colour component.
     * @param colourComponent the value of the colour component (red, green or blue)
     * @return the lightening factor for the colour component: less than 1.0 is darker,
     *         1.0 is unchanged, and greater than 1.0 is lighter
     */
    private float getLighteningFactor(int colourComponent) {
        if (colourComponent < 112) {
            return 2.0f;
        } else if (colourComponent < 144) {
            return 1.8f;
        } else if (colourComponent < 176) {
            return 1.6f;
        } else if (colourComponent < 192) {
            return 1.4f;
        } else if (colourComponent < 208) {
            return 1.2f;
        } else {
            return 1.0f;
        }
    }

    //---------------------------------------------------------------------
    // Message display methods

    /**
     * Display an informational message as a short toast.
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
     * @param longDuration if true, a long duration toast is displayed,
     *                    otherwise a short duration one
     */
    public void displayInfoMessage(@Nullable Context context, @StringRes int messageResId,
                                    @SuppressWarnings("SameParameterValue") boolean longDuration) {
        if (context != null) {
            displayToast(context, context.getString(messageResId), longDuration);
        }
    }

    /**
     * Display an informational message as a short toast.
     * @param applicationContext the application context
     * @param message the message to be displayed
     */
    public void displayInfoMessage(@Nullable Context applicationContext, @NonNull String message) {
        displayInfoMessage(applicationContext, message, false);
    }

    /**
     * Display an informational message as a toast.
     * @param applicationContext the application context
     * @param message the message to be displayed
     * @param longDuration if true, a long duration toast is displayed,
     *                    otherwise a short duration one
     */
    public void displayInfoMessage(@Nullable Context applicationContext, @NonNull String message,
                                   @SuppressWarnings("SameParameterValue") boolean longDuration) {
        if (applicationContext != null) {
            displayToast(applicationContext, message, longDuration);
        }
    }

//    /**
//     * Display a message as a short toast.
//     * @param applicationContext the application context
//     * @param message the message to be displayed
//     */
//    private void displayToast(@NonNull Context applicationContext, @NonNull String message) {
//        displayToast(applicationContext, message, false);
//    }

    /**
     * Display a message as a toast.
     * @param applicationContext the application context
     * @param message the message to be displayed
     * @param longDuration if true, a long duration toast is displayed,
     *                    otherwise a short duration one
     */
    private void displayToast(@NonNull Context applicationContext, @NonNull String message, boolean longDuration) {
        int length = longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(applicationContext, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Display an error message in a dialog.
     * @param activity the activity
     * @param messageResId the string resource id of the message to be displayed
     */
    public void displayErrorMessage(@Nullable Activity activity, @StringRes int messageResId) {
        if (activity != null) {
            displayAlertDialog(activity, R.string.error_title, activity.getString(messageResId));
        }
    }

    /**
     * Display an error message in a dialog.
     * @param activity the activity
     * @param message the message to be displayed
     */
    public void displayErrorMessage(@Nullable Activity activity, @NonNull String message) {
        if (activity != null) {
            displayAlertDialog(activity, R.string.error_title, message);
        }
    }

    /**
     * Display an alert dialog.
     * @param activity the activity
     * @param titleResId the string resource id of the dialog title
     * @param message the dialog message
     */
    private static void displayAlertDialog(@NonNull Activity activity,
            @SuppressWarnings("SameParameterValue") @StringRes int titleResId, @NonNull String message) {

        if (!activity.isFinishing()) {
            new AlertDialog.Builder(activity)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(titleResId)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).show();
        }
    }

}
