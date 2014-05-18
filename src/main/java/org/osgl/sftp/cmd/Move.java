package org.osgl.sftp.cmd;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.osgl.sftp.Sftp;
import org.osgl.sftp.SftpCmd;
import org.osgl.sftp.result.SftpError;
import org.osgl.sftp.result.SftpResult;
import org.osgl.util.E;
import org.osgl.util.S;

public class Move extends SftpCmd<Void> {

    private String source;

    public Move(String src, String dest,  Sftp sftp) {
        super(dest, sftp);
        E.illegalArgumentIf(S.empty(src));
        source = ensurePath(src);
    }

    private String destination() {
        return path;
    }

    private String source() {
        return source;
    }

    @Override
    protected void execute() throws SftpResult, JSchException {
        ChannelSftp ch = channel();
        try {
            ch.rename(source(), destination());
        } catch (SftpException e) {
            throw new SftpError(e, "Error moving file[%s] to [%s]", source, path);
        } finally {
            release(ch);
        }
    }

}
