package org.osgl.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import org.osgl.sftp.cmd.*;
import org.osgl.storage.impl.SObject;
import org.osgl.util.E;

import java.util.Properties;

public class Sftp {
    private SftpConfig config;
    private Session session;

    public Sftp(Properties conf) {
        this(new SftpConfig(conf));
    }

    public Sftp(SftpConfig config) {
        E.NPE(config);
        this.config = config;
        try {
            startup();
        } catch (JSchException e) {
            throw E.unexpected(e, "cannot set up jsch session");
        }
    }

    SftpConfig getConfig() {
        return config;
    }

    private Session createSession() throws JSchException {
        return config.createSession();
    }

    Session getSession() throws JSchException {
        if (!isActive()) startup();
        return session;
    }

    private static final ThreadLocal<ChannelSftp> current = new ThreadLocal<ChannelSftp>();

    public ChannelSftp channel() throws JSchException {
        ChannelSftp ch = current.get();
        if (null == ch || !ch.isConnected()) {
            ch = newChannel();
            current.set(ch);
        }
        return ch;
    }

    public ChannelSftp newChannel() throws JSchException {
        E.illegalStateIf(!isActive(), "Connection pool is not active");
        ChannelSftp ch = (ChannelSftp) session.openChannel("sftp");
        ch.connect();
        return ch;
    }

    void startup() throws JSchException {
        if (null != session && session.isConnected()) return;
        session = createSession();
    }

    void shutdown() {
        if (!isActive()) return;
        session.disconnect();
        session = null;
    }

    boolean isActive() {
        return null != session;
    }

    public boolean exists(String path) {
        SftpATTRS attrs = new Stat(path, this).apply();
        return null != attrs;
    }

    public void put(String path, String content) {
        new Put(path, this, SObject.valueOf(path, content)).apply();
    }

    public boolean rm(String path) {
        return new Delete(path, this).apply();
    }

    public SObject get(String path) {
        return new Get(path, this).apply();
    }

    public boolean mkdir(String path) {
        return new MkDir(path, this).apply();
    }

    public boolean rmdir(String path) {
        return new DeleteDir(path, this).apply();
    }

    public void move(String src, String dst) {
        new Move(src, dst, this).apply();
    }

}
