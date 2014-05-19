package org.osgl.sftp;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgl.sftp.cmd.Put;
import org.osgl.sftp.result.SftpError;
import org.osgl.storage.impl.SObject;
import org.osgl.util.S;

import java.util.Properties;

import static org.osgl.sftp.SftpConfig.*;

/**
 * Created by luog on 18/05/2014.
 */
public class SftpTest extends TestBase {

    private TestSftp sftp;

    @Before
    public void setup() throws Exception {
        Properties conf = new Properties();
        conf.setProperty(CONF_BYPASS_HOST_KEY_CHECKING, "true");
        conf.setProperty(CONF_HOST, "osglsftp.strongspace.com");
        conf.setProperty(CONF_USERNAME, "osglsftp");
        conf.setProperty(CONF_PASSWORD, "Qsyj3@-4%Fddfse");
        conf.setProperty(CONF_CONTEXT_PATH, "/strongspace/osglsftp/public");
        sftp = new TestSftp(conf);
        sftp.startup();
    }

    @After
    public void tearDown() {
        sftp.shutdown();
    }

    @Test
    public void testStartupShutdown() {
        yes(sftp.isActive());
        sftp.shutdown();
        no(sftp.isActive());
    }

    @Test
    public void testFileNotExists() {
        final String path = "/something/not/exists" + S.random();
        no(sftp.exists(path));
    }

    @Test
    public void testFileExists() {
        final String path = "/test" + S.random();
        sftp.put(path, "ABC");
        try {
            yes(sftp.exists(path));
        } finally {
            sftp.rm(path);
        }
    }

    @Test
    public void testRemoveNonExisted() {
        no(sftp.rm("nonexists" + S.random()));
    }

    @Test
    public void testPutRemove() {
        final String path = "test" + S.random();
        sftp.put(path, "ABC");
        try {
            yes(sftp.exists(path));
        } finally {
            sftp.rm(path);
            no(sftp.exists(path));
        }
    }

    @Test
    public void testPutGetRemove() {
        final String path = "test" + S.random();
        sftp.put(path, "ABC");
        try {
            yes(sftp.exists(path));
            SObject sobj = sftp.get(path);
            assertNotNull(sobj);
            eq(sobj.asString(), "ABC");
        } finally {
            sftp.rm(path);
            no(sftp.exists(path));
        }
    }

    @Test
    public void testCheckNonExistingFileAndThenPut() {
        no(sftp.exists("/nonexisting" + S.random()));
        final String path = "test" + S.random();
        sftp.put(path, "ABC");
        sftp.rm(path);
        no(sftp.exists(path));
    }

    @Test
    public void testMkdir() {
        final String path = "dtest" + S.random();
        yes(sftp.mkdir(path));
        yes(sftp.exists(path));
        sftp.rmdir(path);
        no(sftp.exists(path));
    }

    @Test
    public void testMove() {
        final String srcPath = "spath" + S.random();
        final String src = srcPath + "/stest" + S.random();
        final String destPath = "dtest" + S.random();
        final String dest = destPath + "/dtest" + S.random();

        sftp.mkdir(srcPath);
        try {
            sftp.put(src, "ABC");
            try {
                sftp.mkdir(destPath);
                try {
                    no(sftp.exists(dest));
                    sftp.move(src, dest);
                    no(sftp.exists(src));
                    yes(sftp.exists(dest));
                    sftp.rm(dest);
                } finally {
                    sftp.rmdir(destPath);
                }
            } finally {
                try {
                    sftp.rm(src);
                } catch (RuntimeException e) {
                    // ignore
                }
            }
        } finally {
            sftp.rmdir(srcPath);
        }
    }

    @Test
    public void testPutOverwrite() {
        final String path = "test" + S.random();
        sftp.put(path, "ABC");
        try {
            eq("ABC", sftp.get(path).asString());
            sftp.put(path, "XYZ");
            eq("XYZ", sftp.get(path).asString());
        } finally {
            sftp.rm(path);
        }
    }

    @Test
    public void testPutAppend() {
        final String path = "test" + S.random();
        sftp.put(path, "ABC");
        try {
            eq("ABC", sftp.get(path).asString());
            sftp.put(path, "XYZ", Put.Mode.APPEND);
            eq("ABCXYZ", sftp.get(path).asString());
        } finally {
            sftp.rm(path);
        }
    }

    @Test
    @Ignore("resume is not testable for the moment")
    public void testPutResume() {
        final String path = "test" + S.random();
        sftp.put(path, "ABC");
        try {
            eq("ABC", sftp.get(path).asString());
            sftp.put(path, "XYZ", Put.Mode.RESUME);
            eq("ABCXYZ", sftp.get(path).asString());
        } finally {
            sftp.rm(path);
        }
    }

    @Test(expected = SftpError.class)
    public void testGetNonExisted() {
        sftp.get("testNonExisted" + S.random());
    }

    @Test(expected = SftpError.class)
    public void testMoveNonExisted() {
        sftp.move("nonExisted" + S.random(), "/abc/test" + S.random());
    }

    @Test
    public void testAutoRecoveryOnGet() {
        sftp.disconnectSession();
        testGetNonExisted();
    }

    @Test
    public void testAutoRecoveryOnPut() {
        sftp.disconnectSession();
        testPutOverwrite();
    }

    @Test
    public void testAutoRecoveryOnDelete() {
        sftp.disconnectSession();
        testRemoveNonExisted();
    }

    @Test
    public void testAutoRecoveryOnStat() {
        sftp.disconnectSession();
        testFileNotExists();
    }

    @Test
    public void AutoRecoveryOnMove() {
        final String srcPath = "spath" + S.random();
        final String src = srcPath + "/stest" + S.random();
        final String destPath = "dtest" + S.random();
        final String dest = destPath + "/dtest" + S.random();

        sftp.mkdir(srcPath);
        try {
            sftp.put(src, "ABC");
            try {
                sftp.mkdir(destPath);
                try {
                    no(sftp.exists(dest));
                    sftp.disconnectSession();
                    sftp.move(src, dest);
                    no(sftp.exists(src));
                    yes(sftp.exists(dest));
                    sftp.rm(dest);
                } finally {
                    sftp.rmdir(destPath);
                }
            } finally {
                try {
                    sftp.rm(src);
                } catch (RuntimeException e) {
                    // ignore
                }
            }
        } finally {
            sftp.rmdir(srcPath);
        }
    }

}
