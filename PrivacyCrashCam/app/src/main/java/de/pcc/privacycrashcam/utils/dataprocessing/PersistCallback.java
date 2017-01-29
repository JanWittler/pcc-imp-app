package de.pcc.privacycrashcam.utils.dataprocessing;

/**
 * Observes persisting of files. Gets notified about state changes of persisting.
 */

public interface PersistCallback {

    /**
     * Called when persisting starts
     */
    void onPersistingStarted();

    /**
     * Called when persisting stops.
     *
     * @param status true if video was persisted, false if there was an error
     */
    void onPersistingStopped(boolean status);
}
