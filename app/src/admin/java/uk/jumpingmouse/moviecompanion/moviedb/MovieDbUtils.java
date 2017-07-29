package uk.jumpingmouse.moviecompanion.moviedb;

import android.support.annotation.StringRes;

import uk.jumpingmouse.moviecompanion.R;

/**
 * Utilities related to the remote movie database.
 * @author Edmund Johnson
 */
class MovieDbUtils {

    /**
     * Returns a string resource for a message corresponding to a MovieDbHandler status code.
     * @param status a MovieDbHandler status code
     * @return a string resource for a message corresponding to status
     */
    static @StringRes int getErrorMessageForStatus(int status) {
        switch (status) {
            case MovieDbHandler.STATUS_RECEIVER_CONTEXT_IS_NULL:
                return R.string.receiver_context_is_null;
            case MovieDbHandler.STATUS_NETWORK_UNAVAILABLE:
                return R.string.network_unavailable;
            case MovieDbHandler.STATUS_OMDB_API_KEY_KEY_NOT_FOUND:
                return R.string.omdbapi_key_missing;
            case MovieDbHandler.STATUS_SUCCESS:
                return R.string.db_handler_success;
            default:
                return R.string.unrecognised_error_status;
        }
    }

}
