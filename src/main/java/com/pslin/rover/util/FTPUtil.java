package com.pslin.rover.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author plin
 */
public class FTPUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPUtil.class);

    public static boolean uploadFiles(List<File> files, String path) {
        FTPClient ftp = new FTPClient();

        try {
            int reply;
            ftp.connect("FTP.SOMEHOST.COM");
            ftp.login("LOGINFORFTP", "PASSWORD");
            reply = ftp.getReplyCode();

            if(!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }

            for(File file : files) {
                InputStream inputStream = new FileInputStream(file);
                ftp.storeFile("some_dir/" + path + "/" + file.getName(), inputStream);
            }
        } catch (IOException e) {
            LOGGER.error("There was an error uploading files.", e);
            return false;
        }

        return true;
    }
}
