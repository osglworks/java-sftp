package org.osgl.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import org.osgl._;
import org.osgl.exception.NotAppliedException;
import org.osgl.sftp.result.SftpError;
import org.osgl.sftp.result.SftpResult;
import org.osgl.util.E;

/**
 * Created by luog on 18/05/2014.
 */
public abstract class SftpCmd<T> extends _.F0<T> {

    private boolean needsNewChannel;
    protected final String path;
    protected ChannelSftp ch;
    private Sftp sftp;

    protected SftpCmd(String path, Sftp sftp) {
        this(path, sftp, false);
    }

    protected SftpCmd(String path, Sftp sftp, boolean needsNewChannel) {
        E.NPE(sftp, path);
        this.sftp = sftp;
        this.path = ensurePath(path);
        this.needsNewChannel = needsNewChannel;
    }

    protected SftpCmd(String path, ChannelSftp ch) {
        this(path, ch, false);
    }

    protected SftpCmd(String path, ChannelSftp ch, boolean needsNewChannel) {
        E.NPE(path, ch);
        this.ch = ch;
        this.path = ensurePath(path);
        this.needsNewChannel = needsNewChannel;
    }

    @Override
    public T apply() throws NotAppliedException, _.Break {
        try {
            execute();
        } catch (JSchException e) {
            throw E.unexpected(e);
        } catch (SftpError error) {
            throw error;
        } catch (SftpResult result) {
            return result.get();
        } finally {
            Sftp.clearOneTimeContext();
        }
        return null;
    }

    private Sftp sftp() {
        return sftp;
    }

    protected final ChannelSftp channel() throws JSchException {
        if (null != ch && ch.isConnected()) return ch;
        return needsNewChannel ? sftp().newChannel() : sftp().channel();
    }

    protected abstract void execute() throws SftpResult, JSchException;

    protected final void release(ChannelSftp ch) {
        if (needsNewChannel) {
            ch.disconnect();
        } else {
            // ignore as we want to reuse the channel
        }
    }

    protected final String ensurePath(String path) {
        return sftp().getConfig().ensurePath(path);
    }
}
