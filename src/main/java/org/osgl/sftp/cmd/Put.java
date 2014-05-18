package org.osgl.sftp.cmd;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.osgl.sftp.Sftp;
import org.osgl.sftp.SftpCmd;
import org.osgl.sftp.result.SftpError;
import org.osgl.sftp.result.SftpResult;
import org.osgl.storage.impl.SObject;
import org.osgl.util.IO;

import java.io.InputStream;

public class Put extends SftpCmd<Void> {
    private SObject content;

    public Put(String path, Sftp sftp, SObject content) {
        super(path, sftp, false);
        if (null == content) content = SObject.valueOf("", "");
        this.content = content;
    }

    @Override
    protected void execute() throws SftpResult, JSchException {
        InputStream is = null;
        ChannelSftp ch = channel();
        try {
            is = content.asInputStream();
            ch.put(is, path);
        } catch (SftpException e) {
            throw new SftpError(e, "Error putting file[%s]", path);
        } finally {
            IO.close(is);
            release(ch);
        }
    }

}
