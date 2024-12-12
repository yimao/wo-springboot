package com.mudcode.springboot.common.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(ZipFileUtil.class);

    private ZipFileUtil() {
    }

    public static void unzipPackage(File src, String unzipFilePath) throws IOException {
        File unzipFile = new File(unzipFilePath);
        FileUtils.forceMkdir(unzipFile);
        try (ZipFile zipFile = new ZipFile(src)) {
            Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                File entryFile = new File(unzipFile, entry.getName());
                Path path = entryFile.toPath();
                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(entryFile);
                } else {
                    FileUtils.forceMkdirParent(entryFile);
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = Files.newOutputStream(path)) {
                        IOUtils.copy(in, out);
                    }
                }
                int mode = entry.getUnixMode();
                if (mode > 0) {
                    Set<PosixFilePermission> permissions = permissions(mode);
                    Files.setPosixFilePermissions(path, permissions);
                }
            }
            logger.debug("unzip file: {}", src.getAbsolutePath());
        }
    }

    public static void zip(File src, String zipFilePath) throws IOException {
        try (OutputStream fout = Files.newOutputStream(Paths.get(zipFilePath));
             BufferedOutputStream bout = new BufferedOutputStream(fout);
             ZipOutputStream zout = new ZipOutputStream(bout)) {
            Files.walkFileTree(src.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    putArchiveEntry(zout, dir, src.toPath());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    putArchiveEntry(zout, file, src.toPath());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.error("tar failed: " + file.getFileName() + " , error: " + exc.getMessage(), exc);
                    return FileVisitResult.TERMINATE;
                }
            });
        }
    }

    public static void tarWithGzip(File src, String zipFilePath) throws IOException {
        boolean withSrcDir = true;
        try (OutputStream fout = Files.newOutputStream(Paths.get(zipFilePath));
             BufferedOutputStream bout = new BufferedOutputStream(fout);
             GzipCompressorOutputStream gzout = new GzipCompressorOutputStream(bout);
             TarArchiveOutputStream tout = new TarArchiveOutputStream(gzout)) {

            tout.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            tout.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);

            Files.walkFileTree(src.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    putArchiveEntry(tout, dir, src.toPath(), withSrcDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    putArchiveEntry(tout, file, src.toPath(), withSrcDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.error("tar failed: " + file.getFileName() + " , error: " + exc.getMessage(), exc);
                    return FileVisitResult.TERMINATE;
                }
            });
        }
    }

    private static void putArchiveEntry(ZipOutputStream zout, Path file, Path root) throws IOException {
        Path targetFile = root.relativize(file);
        String newTargetPath = targetFile.toString();
        if (file.toFile().isDirectory()) {
            newTargetPath = newTargetPath + "/";
        }
        ZipEntry zipEntry = new ZipEntry(newTargetPath);
        zout.putNextEntry(zipEntry);
        if (!file.toFile().isDirectory()) {
            Files.copy(file, zout);
        }
        zout.closeEntry();
    }

    private static void putArchiveEntry(TarArchiveOutputStream tout, Path file, Path root, boolean withSrcDir)
            throws IOException {
        Path targetFile = root.relativize(file);
        String newTargetPath = targetFile.toString();
        if (root.toFile().isDirectory() && withSrcDir) {
            newTargetPath = root.toFile().getName() + "/" + newTargetPath;
        }

        TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), newTargetPath);
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file);
        tarEntry.setMode(toUnixMode(permissions));
        tout.putArchiveEntry(tarEntry);
        if (!file.toFile().isDirectory()) {
            Files.copy(file, tout);
        }
        tout.closeArchiveEntry();
    }

    private static Set<PosixFilePermission> permissions(int unixMode) {
        int mode = unixMode & 511;
        Set<PosixFilePermission> permissions = EnumSet.noneOf(PosixFilePermission.class);
        if ((mode & 256) > 0) {
            permissions.add(PosixFilePermission.OWNER_READ);
        }
        if ((mode & 128) > 0) {
            permissions.add(PosixFilePermission.OWNER_WRITE);
        }
        if ((mode & 64) > 0) {
            permissions.add(PosixFilePermission.OWNER_EXECUTE);
        }
        if ((mode & 32) > 0) {
            permissions.add(PosixFilePermission.GROUP_READ);
        }
        if ((mode & 16) > 0) {
            permissions.add(PosixFilePermission.GROUP_WRITE);
        }
        if ((mode & 8) > 0) {
            permissions.add(PosixFilePermission.GROUP_EXECUTE);
        }
        if ((mode & 4) > 0) {
            permissions.add(PosixFilePermission.OTHERS_READ);
        }
        if ((mode & 2) > 0) {
            permissions.add(PosixFilePermission.OTHERS_WRITE);
        }
        if ((mode & 1) > 0) {
            permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        }
        return permissions;
    }

    private static int toUnixMode(Set<PosixFilePermission> permissions) {
        int mode = 0;
        if (permissions == null || permissions.isEmpty()) {
            return mode;
        }
        for (PosixFilePermission permission : permissions) {
            if (permission == null) {
                throw new NullPointerException();
            }
            switch (permission) {
                case OWNER_READ:
                    mode |= 256;
                    break;
                case OWNER_WRITE:
                    mode |= 128;
                    break;
                case OWNER_EXECUTE:
                    mode |= 64;
                    break;
                case GROUP_READ:
                    mode |= 32;
                    break;
                case GROUP_WRITE:
                    mode |= 16;
                    break;
                case GROUP_EXECUTE:
                    mode |= 8;
                    break;
                case OTHERS_READ:
                    mode |= 4;
                    break;
                case OTHERS_WRITE:
                    mode |= 2;
                    break;
                case OTHERS_EXECUTE:
                    mode |= 1;
            }
        }
        return mode;
    }

}
