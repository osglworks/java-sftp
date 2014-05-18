package org.osgl.sftp.result;

import org.osgl.util.E;

/**
 * Created by luog on 18/05/2014.
 */
public class SftpError extends SftpResult {
    public SftpError(Throwable cause, String msg, Object... args) {
        super(cause, msg, args);
    }

    @Override
    public <T> T get() {
        throw E.unsupport();
    }
}
