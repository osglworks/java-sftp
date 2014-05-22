package org.osgl.sftp.cmd;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.osgl.sftp.Sftp;
import org.osgl.sftp.SftpCmd;
import org.osgl.sftp.result.SftpError;
import org.osgl.sftp.result.SftpResult;
import org.osgl.storage.impl.SObject;

import java.io.ByteArrayOutputStream;

public class Get extends SftpCmd<SObject> {

    public Get(String path, Sftp sftp) {
        super(path, sftp, false);
    }

    @Override
    protected void execute() throws SftpResult, JSchException {
        ChannelSftp ch = channel();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ch.get(path, baos);
            throw new SftpResult(SObject.of(path, baos.toByteArray()));
        } catch (SftpException e) {
            throw new SftpError(e, "Error getting file[%s]", path);
        } finally {
            release(ch);
        }
    }

}
