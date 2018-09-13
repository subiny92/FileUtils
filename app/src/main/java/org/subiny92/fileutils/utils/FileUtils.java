package org.subiny92.fileutils.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.text.TextUtils.isEmpty;

public class FileUtils {

    private static final String TAG = "FileUtils";
    private static final String DEFAULT_FOLDER_NAME = "FileUtils";
    private static final String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static final int DEFAULT_BUFFER_SIZE = 2 * 1024;
    private static FileUtils instance;
    private String folderName;
    private File storageDir;

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }

        return instance;
    }

    /**
     * Creating folders in internal storage
     * @return path
     */
    public void createFileDirectory() {
        String name = DEFAULT_FOLDER_NAME;
        if (folderName != null) {
            name = folderName;
        }

        storageDir = new File(STORAGE_PATH + name);

        Log.d(TAG, name);

        if (!storageDir.isDirectory()) {
            storageDir.mkdirs();
        }

    }

    public boolean isDirectory() {
        return storageDir.isDirectory();
    }

    /**
     * return Created Directory
     * @return storageDirectoryFile
     * @throws FileNotFoundException
     */
    public File getFile() throws FileNotFoundException{
        if (storageDir == null) {
            throw new FileNotFoundException("File not found");
        }
        return storageDir;
    }

    /**
     * return Created Directory Path
     * @return storageDirectory Path
     * @throws FileNotFoundException
     */
    public String getFileAbsolutePath() throws FileNotFoundException {
        if (storageDir == null) {
            throw new FileNotFoundException("File not found");
        }

        return storageDir.getAbsolutePath();
    }

    /**
     * Folder set
     * @param folderName
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void removeDirectory(String folderName) throws FileNotFoundException {
        File file = new File(STORAGE_PATH + folderName);
        if (file == null) {
            throw new FileNotFoundException("File not found");
        }

        if (file.isDirectory()) {

            if (file.list().length > 0) {
                File [] mFileList = file.listFiles();
                for (File mFile : mFileList) {
                    mFile.delete();
                }
            }

            file.delete();

        } else {
            throw new FileNotFoundException("File is not Directory");
        }
    }

    /**
     * 압축
     * @param sourcePath 압축 대상
     * @param output 압축명
     * @throws Exception
     */
    public void zip(String sourcePath, String output) throws Exception {

        File sourceFile = new File(sourcePath);
        if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
            throw new Exception("I can not find the file to compress.");
        }

        if (!(substringAfterLast(output, ".").equals("zip"))) {
            throw new Exception("Please check the extension of the saved file name after compression.");
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(output);
            bos = new BufferedOutputStream(fos);
            zos = new ZipOutputStream(bos);

            zipEntry(sourceFile, sourcePath, zos);
            zos.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zos != null) {
                zos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    private void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zos) throws IOException {

        if (sourceFile.isDirectory()) {
            if (sourceFile.getName().equalsIgnoreCase(".metadata")) {
                // return .metadata directory
                return;
            }

            File [] fileArray = sourceFile.listFiles();
            for (File file : fileArray) {
                zipEntry(file, sourcePath, zos);
            }
        } else {
            BufferedInputStream bis = null;

            try {
                String filePath = sourceFile.getPath();
                Log.d(TAG, filePath);

                StringTokenizer strToken = new StringTokenizer(filePath, "/");

                int len = strToken.countTokens();
                String zipEntryName = strToken.toString();
                while (len != 0) {
                    len--;
                    zipEntryName = strToken.nextToken();
                }

                bis = new BufferedInputStream(new FileInputStream(sourceFile));

                ZipEntry zipEntry = new ZipEntry(zipEntryName);
                zipEntry.setTime(sourceFile.lastModified());
                zos.putNextEntry(zipEntry);


                byte [] buffer = new byte[DEFAULT_BUFFER_SIZE];

                int count = 0;
                while ((count = bis.read(buffer)) != -1) {
                    zos.write(buffer, 0 , count);
                }

                zos.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    bis.close();
                }
            }
        }
    }

    private static String substringAfterLast(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }

        if (isEmpty(separator)) {

            return "";
        }

        final int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == str.length() - separator.length()) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
}
