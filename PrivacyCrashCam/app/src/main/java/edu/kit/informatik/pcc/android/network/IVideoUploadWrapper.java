package edu.kit.informatik.pcc.android.network;

public interface IVideoUploadWrapper {
    void uploadVideo(int videoId, IRequestCompletion<IClientVideoUpload.UploadResult> completion);
}
