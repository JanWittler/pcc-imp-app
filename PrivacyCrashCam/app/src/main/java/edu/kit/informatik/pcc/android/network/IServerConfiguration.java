package edu.kit.informatik.pcc.android.network;

public interface IServerConfiguration {
    String scheme();
    String host();
    String path();
    int port();
}
