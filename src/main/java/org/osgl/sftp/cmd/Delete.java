package org.osgl.sftp.cmd;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.osgl.sftp.Sftp;
import org.osgl.sftp.SftpCmd;
import org.osgl.sftp.result.SftpError;
import org.osgl.sftp.result.SftpResult;

public class Delete extends SftpCmd<Boolean> {

    public Delete(String path, Sftp sftp) {
        super(path, sftp, false);
    }

    @Override
    protected void execute() throws SftpResult, JSchException {
        ChannelSftp ch = channel();
        try {
            ch.rm(path);
            throw new SftpResult(true);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new SftpResult(false);
            }
            throw new SftpError(e, "Error putting file[%s]", path);
        } finally {
            release(ch);
        }
    }

}
