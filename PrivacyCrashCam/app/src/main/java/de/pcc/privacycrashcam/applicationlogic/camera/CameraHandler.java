package de.pcc.privacycrashcam.applicationlogic.camera;

import de.pcc.privacycrashcam.data.Metadata;

/**
 * Interface each camera handler needs to implement. Clients will communicate through this interface
 * with the camera handler. The camera handler is then responsible for setting up, maintaining,
 * managing and cleaning up data structures and camera calls.
 *
 * <p>The camera handler has a lifecycle which is related to the lifecycle
 * {@link android.app.Activity Activities} have. This way, activities and UI components can
 * synchronize their life cycle with the camera handler.<br>
 * The camera handlers methods should be called in the following order to make the handler work
 * properly:
 * <ul>
 *     <li>{@link #createHandler()}</li>
 *     <li>{@link #resumeHandler()}</li>
 *     <li>Now {@link #schedulePersisting()} or {@link #updateMetadata(Metadata)} can be called</li>
 *     <li>{@link #pauseHandler()}</li>
 *     <li>{@link #destroyHandler()}</li>
 * </ul>
 * After {@link #destroyHandler()} was called you will need to call createHandler again in order to
 * reuse the handler.
 *
 * </p>
 *
 * @author Giorgio Gross
 */
public interface CameraHandler {

    /**
     * Should be called before {@link #resumeHandler()}. Performs memory related preparation tasks
     * or sets up variables which need to be set up once at start up.
     */
    void createHandler();

    /**
     * Resumes the camera handler. Called when UI becomes visible to the user. Makes camera and
     * recorder available and starts buffering video chunks.
     */
    void resumeHandler();

    /**
     * Updates the camera handlers metadata reference.
     * @param metadata new meta data object
     */
    void updateMetadata(Metadata metadata);

    /**
     * Tells the camera handler to start recording. The camera handler should decide on his own
     * when to actually persist video data.
     */
    void schedulePersisting();

    /**
     * Pauses the handler. Called when the UI becomes invisible to the user. Releases camera and
     * recorder and stops buffering video chunks.
     */
    void pauseHandler();

    /**
     * Releases all resources allocated by the handler. The handler gets into an invalid state after
     * making this call and thus cannot be reused.<br>
     * Call {@link #pauseHandler()} if you wish to reuse your handler instance instead.<br>
     * Call {@link #createHandler()} if you want to restore and reuse this camera handler.
     */
    void destroyHandler();

}
