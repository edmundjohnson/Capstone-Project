package uk.jumpingmouse.moviecompanion.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Class containing utility methods related to internet access.
 * @author Edmund Johnson
 */
public final class NetUtils {
    /**
     * Reference to the connectivity manager object.
     */
    private ConnectivityManager mConnectivityManager = null;

    /**
     * The singleton instance of this class.
     */
    private static NetUtils sNetUtils = null;

    /**
     * Private default constructor to prevent instantiation from other classes.
     */
    private NetUtils() {
    }

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    public static NetUtils getInstance() {
        if (sNetUtils == null) {
            sNetUtils = new NetUtils();
        }
        return sNetUtils;
    }

    /**
     * Returns whether the device is connected to a network.
     * @param context the context
     * @return true if there is a network connection, false otherwise
     */
    public boolean isNetworkAvailable(@Nullable final Context context) {
        if (context == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = getConnectivityManager(context).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Returns a reference to the connectivity manager.
     *
     * @param context the context
     * @return a reference to the connectivity manager
     */
    @NonNull
    private ConnectivityManager getConnectivityManager(@NonNull final Context context) {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return mConnectivityManager;
    }

}
