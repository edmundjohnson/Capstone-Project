package uk.jumpingmouse.moviecompanion.security;

/**
 * This class is responsible for generating instances of SecurityManager.
 * @author Edmund Johnson
 */

public class SecurityManagerFactory {

    /**
     * Return an implementation of SecurityManager.
     * Currently, a Firebase implementation is always returned.
     * @return an implementation of SecurityManager
     */
    public static SecurityManager getSecurityManager() {
        return SecurityManagerFirebase.getInstance();
    }


}
