package uk.jumpingmouse.moviecompanion;

import android.support.annotation.NonNull;

import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.model.MasterDatabaseFirebaseAdmin;
import uk.jumpingmouse.moviecompanion.moviedb.MovieDbHandler;
import uk.jumpingmouse.moviecompanion.moviedb.MovieDbHandlerTmdb;
import uk.jumpingmouse.moviecompanion.moviedb.MovieDbReceiver;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtilsAdmin;

/**
 * A factory class which generates implementations of objects used in the app.
 * This class contains factory methods specific to the 'admin' product flavour.
 * Dependency injection could be used with this class at some point.
 * @author Edmund Johnson
 */
public class ObjectFactory extends ObjectFactoryBase {

    /**
     * Returns an implementation of MasterDatabase.
     * Currently, the Firebase implementation is returned.
     * @return an implementation of MasterDatabase
     */
    @NonNull
    public static MasterDatabase getMasterDatabase() {
        return MasterDatabaseFirebaseAdmin.getInstance();
    }

    /**
     * Convenience method which returns a reference to a new MovieDbHandler object.
     * A new MovieDbHandler object must be created for each instance of each client class.
     * @param movieDbReceiver the MovieDbReceiver to which the MovieDbHandler will pass any
     *                        data read from the remote database
     * @return a reference to a MovieDbHandler object
     */
    @NonNull
    public static MovieDbHandler newMovieDbHandler(@NonNull MovieDbReceiver movieDbReceiver) {
        // OMDb
        //return MovieDbHandlerOmdb.newInstance(movieDbReceiver);
        // TMDb
        return MovieDbHandlerTmdb.newInstance(movieDbReceiver);
    }


    /**
     * Returns a reference to a NavUtils object.
     * @return a reference to a NavUtils object
     */
    @NonNull
    public static NavUtils getNavUtils() {
        return NavUtilsAdmin.getInstance();
    }

}
