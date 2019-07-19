package edu.kit.informatik.pcc.android.network;

import java.io.File;

public interface IClientVideoUpload {
    void uploadVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKey, String authenticationToken, IRequestCompletion<UploadResult> completion);

    enum UploadResult {

        //! The request was successful.
        SUCCESS,

        //! Network not reachable
        NETWORK_FAILURE,

        //! The MultiPartForm had not the right input for this kind of request.
        INPUT_FAILURE,

        //! An error occurred editing the video.
        EDITING_FAILURE,

        //! Token invalid or not existing
        UNAUTHENTICATED,

        //! Something unexpected went wrong.
        FAILURE_OTHER
    }
}