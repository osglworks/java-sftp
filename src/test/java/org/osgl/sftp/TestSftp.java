package org.osgl.sftp;

import java.util.Map;

/**
 * Created by luog on 19/05/2014.
 */
public class TestSftp extends Sftp {
    public TestSftp(Map conf) {
        super(conf);
    }

    public TestSftp(SftpConfig config) {
        super(config);
    }

    void disconnectSession() {
        if (null != session) session.disconnect();
    }
}
