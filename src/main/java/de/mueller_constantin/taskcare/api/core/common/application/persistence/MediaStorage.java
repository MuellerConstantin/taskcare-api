package de.mueller_constantin.taskcare.api.core.common.application.persistence;

public interface MediaStorage {
    void save(String path, String contentType, byte[] data);

    byte[] load(String path);

    void delete(String path);

    boolean exists(String path);

    long size(String path);

    String contentType(String path);
}
