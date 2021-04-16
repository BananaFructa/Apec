package Apec.Components.Gui.Menu.TexturePackMenu;

import Apec.Utils.ParameterizedRunnable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownloadProcess {

    private URL url;
    private File file;
    private final Object threadLock = new Object();
    public String tpname;

    public float downloadProgress = 0;

    private ParameterizedRunnable<Integer> callback;

    public DownloadProcess(URL url,String texturePackName,String fileName) {
        this.url = url;
        this.tpname = texturePackName;
        new File("resourcepacks").mkdirs(); // in case there is no resorscpacks folder ? idk
        this.file = new File("resourcepacks/" + fileName);
    }

    public void setCallback(ParameterizedRunnable<Integer> callback) {
        this.callback = callback;
    }

    public void startDownload() {
        try {
            downloadProgress = 0;
            URLConnection connection = url.openConnection();
            final long fileSize = connection.getContentLength();
            final BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream,512);
            // Download thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (threadLock) {
                            byte[] data = new byte[512];
                            long downloadedBytes = 0;
                            int bytesCount = 0;
                            while ((bytesCount = inputStream.read(data, 0, 512)) >= 0) {
                                outputStream.write(data, 0, bytesCount);
                                downloadedBytes += bytesCount;
                                downloadProgress = (float) downloadedBytes / (float) fileSize;
                            }
                            outputStream.close();
                            inputStream.close();
                            if (callback != null) callback.run(0);
                        }
                    } catch (Exception err) {
                        if (callback != null) callback.run(-1);
                        err.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception exception) {
            if (callback != null) callback.run(-1);
            exception.printStackTrace();
        }
    }

    public float getProgress() {
        return downloadProgress;
    }
}
