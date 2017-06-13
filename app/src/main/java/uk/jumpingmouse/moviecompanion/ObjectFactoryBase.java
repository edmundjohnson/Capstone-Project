package uk.jumpingmouse.moviecompanion;

import android.support.annotation.NonNull;

import uk.jumpingmouse.moviecompanion.model.LocalDatabase;
import uk.jumpingmouse.moviecompanion.model.LocalDatabaseInMemory;
import uk.jumpingmouse.moviecompanion.security.SecurityManager;
import uk.jumpingmouse.moviecompanion.security.SecurityManagerFirebase;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtilsImpl;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * A factory class which generates implementations of objects used in the app.
 * This is the superclass containing factory methods common to all product flavours.
 * Dependency injection could be used with this class at some point.
 * @author Edmund Johnson
 */
public abstract class ObjectFactoryBase {

    /**
     * Returns an implementation of SecurityManager.
     * Currently, the Firebase implementation is returned.
     * @return an implementation of SecurityManager
     */
    @NonNull
    public static SecurityManager getSecurityManager() {
        return SecurityManagerFirebase.getInstance();
    }

//    /**
//     * Returns an implementation of MasterDatabase.
//     * Currently, the Firebase implementation is returned.
//     * @return an implementation of MasterDatabase
//     */
//    @NonNull
//    public static MasterDatabase getMasterDatabase() {
//        return MasterDatabaseFirebase.getInstance();
//    }

    /**
     * Returns an implementation of LocalDatabase.
     * Currently, the in-memory implementation of LocalDatabase is returned.
     * The local database is used to store a copy of the subset of Firebase
     * realtime database data which is relevant to the current user.
     * @return an implementation of LocalDatabase.
     */
    @NonNull
    public static LocalDatabase getLocalDatabase() {
        return LocalDatabaseInMemory.getInstance();
    }

    /**
     * Returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    public static ViewUtils getViewUtils() {
        return ViewUtils.getInstance();
    }

    /**
     * Returns a reference to a NavUtils object.
     * @return a reference to a NavUtils object
     */
    @NonNull
    public static NavUtils getNavUtils() {
        return NavUtilsImpl.getInstance();
    }

}
