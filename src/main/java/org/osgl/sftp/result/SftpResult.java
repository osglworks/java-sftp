package org.osgl.sftp.result;

import org.osgl._;
import org.osgl.util.S;

public class SftpResult extends RuntimeException {

    Object result;

    public SftpResult(Object result) {
        this.result = result;
    }

    protected SftpResult(Throwable cause, String msg, Object... args) {
        super(S.fmt(msg, args), cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

    public <T> T get() {
        return _.cast(result);
    }
}
