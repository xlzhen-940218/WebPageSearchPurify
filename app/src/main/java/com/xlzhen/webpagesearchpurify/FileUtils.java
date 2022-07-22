package com.xlzhen.webpagesearchpurify;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for various static file utilities.
 *
 * @author Tuomas Tikka
 */
public class FileUtils {

    /**
     * Is the file a video file? Supported video files are: MP4
     *
     * @param filename The filename
     * @return True if the file extension indicates a supported video type
     */
    public static boolean isVideo(String filename) {
        if (filename == null) {
            return (false);
        } else {
            try {
                return URLConnection.getFileNameMap().getContentTypeFor(filename).startsWith("video");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return false;
    }

    /**
     * Is the file a video file? Supported video files are: MP4
     *
     * @param file The file object
     * @return True if the file extension indicates a supported video type
     */
    public static boolean isVideo(File file) {
        return (file != null && isVideo(file.getName()));
    }

    /**
     * Is the file an image file? Supported image files are: PNG, JPG, JPEG
     *
     * @param filename The filename
     * @return True if the file extension indicates a supported image type
     */
    public static boolean isImage(String filename) {
        if (filename == null) {
            return (false);
        } else {
            String lc = filename.toLowerCase();
            return (lc.endsWith(".png") || lc.endsWith("jpg") || lc.endsWith("jpeg") || lc.endsWith("heic"));
        }
    }

    /**
     * Is the file an image file? Supported image files are: PNG, JPG, JPEG
     *
     * @param file The file object
     * @return True if the file extension indicates a supported image type
     */
    public static boolean isImage(File file) {
        return (file != null && isImage(file.getName()));
    }

    public static boolean isAudio(File file) {
        return (file != null && isAudio(file.getName()));
    }

    private static boolean isAudio(String filename) {
        if (filename == null) {
            return (false);
        } else {
            try {
                return URLConnection.getFileNameMap().getContentTypeFor(filename).startsWith("audio");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean delete(String path) {
        File f = new File(path);
        try {
            if (f.exists()) {
                if (f.isDirectory()) {
                    deleteFolder(path);
                } else {
                    f.delete();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFolder(String path) {
        boolean deleteResult = true;
        String deleteCmd = "rm -r " + path;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(deleteCmd);
        } catch (IOException e) {
            deleteResult = false;
        }
        return deleteResult;
    }


    public static boolean saveFile(String savePath, String data) {
        try {
            File file = new File(savePath);
            if (!file.exists()) {//如果文件不存在，则创建一个新文件
                //如果它的父级目录都不存在，则创建目录
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

            } else {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file, true);

            outputStream.write(data != null ? data.getBytes(StandardCharsets.UTF_8) : "".getBytes());
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveFile(String savePath, byte[] data) {
        try {
            File file = new File(savePath);
            if (!file.exists()) {//如果文件不存在，则创建一个新文件
                //如果它的父级目录都不存在，则创建目录
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file, true);

            outputStream.write(data);
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] readFile(String path) {
        File file = new File(path);
        if (!file.exists())
            return null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[fileInputStream.available()];
            fileInputStream.read(data);
            fileInputStream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static String sizeFormat(long length) {
        if (length < 1024) {
            return String.format("%s B", length);
        } else if (length < 1024 * 1024) {
            return String.format("%s K", length / 1024);
        } else if (length < 1024 * 1024 * 1024) {
            return String.format("%s M", length / 1024 / 1024);
        }
        return String.format("%s G", length / 1024 / 1024 / 1024);
    }
}
