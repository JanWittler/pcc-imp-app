package de.pcc.privacycrashcam.applicationlogic.camera;

import de.pcc.privacycrashcam.data.Metadata;

/**
 * Interface each camera handler needs to implement. Used to communicate with UI components.
 */
public interface CameraHandler {

    /**
     * Tells the camera handler to start recording. The camera handler should decide on his own
     * when to actually persist video data.
     */
    void schedulePersisting();

    /**
     * Updates the camera handlers metadata reference.
     * @param metadata new meta data object
     */
    void setMetadata(Metadata metadata);

    /**
     * Resumes the camera handler. Called when UI becomes visible to the user.
     */
    void resumeHandler();

    /**
     * Pauses the handler. Called when the UI becomes invisible to the user.
     */
    void pauseHandler();

}
