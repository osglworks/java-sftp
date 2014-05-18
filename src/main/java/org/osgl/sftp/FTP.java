package org.osgl.sftp;

import org.osgl.storage.impl.SObject;

import java.io.File;
import java.io.InputStream;

/**
 * Created by luog on 19/05/2014.
 */
public interface FTP {
    boolean exists(String path);

    void put(String path, String content);

    void put(String path, InputStream content);

    void put(String path, File content);

    void put(String path, byte[] content);

    boolean rm(String path);

    SObject get(String path);

    boolean mkdir(String path);

    boolean rmdir(String path);

    void move(String src, String dst);
}
