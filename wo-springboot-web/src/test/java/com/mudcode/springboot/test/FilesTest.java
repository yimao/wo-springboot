package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesTest {

    private static final Logger logger = LoggerFactory.getLogger(FilesTest.class);

    @Test
    public void test() throws IOException {
        Path path = Paths.get("/Users/yimao/woniu/tmp");
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                logger.debug("dir: {}", dir.toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                logger.debug("file: {}", file.toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                logger.error("file: " + file.getFileName() + " , error: " + exc.getMessage(), exc);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test
    public void testPath() {
        Path path = Paths.get("/Users/yimao/woniu/tmp/test.key");
        logger.debug("path: {}", path);
        String fileName = path.getFileName().toString();
        logger.debug("file name: {}", fileName);
    }

}
