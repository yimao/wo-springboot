package com.mudcode.springboot.test;

import com.mudcode.springboot.common.util.ZipFileUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ZipFileTest {

    private static final Logger logger = LoggerFactory.getLogger(ZipFileTest.class);

    @Test
    public void tgzTest() throws IOException {
        File src = new File("/Users/yimao/tmp/111");
        String zipFilePath = src.getAbsolutePath() + ".tar.gz";
        Files.deleteIfExists(new File(zipFilePath).toPath());

        ZipFileUtil.tarWithGzip(src, zipFilePath);
    }

    @Test
    public void unzipTest() throws IOException {
        File src = new File("/Users/yimao/tmp/eb019736_11_a9e9dd77_bpi-web-3.6.1-bpi-hotfix-20210816.zip");
        File unPackageDir = new File("/Users/yimao/tmp/111");
        FileUtils.forceDelete(unPackageDir);

        ZipFileUtil.unzipPackage(src, unPackageDir.getAbsolutePath());
    }

}
