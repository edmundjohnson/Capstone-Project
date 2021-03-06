package uk.jumpingmouse.moviecompanion;

import android.support.annotation.NonNull;

import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.model.MasterDatabaseFirebaseFree;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtilsFree;

/**
 * A factory class which generates implementations of objects used in the app.
 * This class contains factory methods specific to the 'free' product flavour.
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
        return MasterDatabaseFirebaseFree.getInstance();
    }

    /**
     * Returns a reference to a NavUtils object.
     * @return a reference to a NavUtils object
     */
    @NonNull
    public static NavUtils getNavUtils() {
        return NavUtilsFree.getInstance();
    }

}
