package uk.jumpingmouse.moviecompanion;

import android.support.annotation.NonNull;

import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.model.MasterDatabaseFirebaseAdmin;

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

}
