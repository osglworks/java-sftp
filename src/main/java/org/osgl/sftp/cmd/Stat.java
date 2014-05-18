package org.osgl.sftp.cmd;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.osgl.sftp.Sftp;
import org.osgl.sftp.SftpCmd;
import org.osgl.sftp.result.SftpError;
import org.osgl.sftp.result.SftpResult;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_NO_SUCH_FILE;

/**
 * Created by luog on 18/05/2014.
 */
public class Stat extends SftpCmd<SftpATTRS> {

    public Stat(String path, Sftp sftp) {
        super(path, sftp, false);
    }

    @Override
    protected void execute() throws SftpResult, JSchException {
        ChannelSftp ch = channel();
        try {
            SftpATTRS attrs = ch.stat(path);
            throw new SftpResult(attrs);
        } catch (SftpException e) {
            if (e.id == SSH_FX_NO_SUCH_FILE) {
                throw new SftpResult(null);
            } else {
                throw new SftpError(e, "Error checking stat of %s", path);
            }
        } finally {
            release(ch);
        }
    }

}
