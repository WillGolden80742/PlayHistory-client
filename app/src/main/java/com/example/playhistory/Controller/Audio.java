package com.example.playhistory.Controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import com.example.playhistory.Model.ConnectionFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Audio {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static final HashMap<String, Boolean> downloading = new HashMap<>();
    private final String padraoNomeArquivo = "audio_descricao_";
    private final String localDeArmazenamento = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/TourOut/Audio_Descricao/";
    private ConnectionFactory connection;
    private String fileName;
    private File file;
    private final String configFile = "config.ini";
    private final String configFilePath = localDeArmazenamento + configFile;
    private final Runnable downloadMidia = () -> {
        writeToFile(fileName, connection.getContentBytes());
    };

    public Audio(Context context, String url) {
        connection = new ConnectionFactory(url);
        String[] nome = url.split("=");
        fileName = padraoNomeArquivo + nome[nome.length - 1] + ".mp3";
        file = new File(localDeArmazenamento + fileName);
        if (!file.exists() || file.length() < 1024) {
            if (file.length() < 1024) {
                file.delete();
            }
            downloading.put(fileName, true);
            mediaPlayer = MediaPlayer.create(context, Uri.parse(url));
            new Thread(downloadMidia).start();
        } else {
            downloading.put(fileName, false);
            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file));
        }
    }

    public Audio(Context context, int id) {
        mediaPlayer = MediaPlayer.create(context, id);
    }

    public Audio() {
    }

    public boolean isDownloading() {
        for (Map.Entry<String, Boolean> pair : downloading.entrySet()) {
            boolean response = Boolean.parseBoolean(pair.getKey());
            if (response) {
                return true;
            }
        }
        return false;
    }

    public void setDownloaded() {
        writeToFile(configFile, "downloaded:true");
    }

    public boolean isDownloaded() {
        String line = "";
        boolean downloaded = false;
        File file = new File(configFilePath);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    boolean hashDownloaded = line.contains("downloaded") && line.split(":")[1].equals("true");
                    if (hashDownloaded) {
                        downloaded = true;
                    }
                }
                br.close();
                return downloaded;
            } catch (IOException e) {
                return false;
            }
        } else {
            downloaded = false;
        }
        return downloaded;
    }

    public void writeToFile(String fileName, String content) {
        File path = new File(localDeArmazenamento);
        File newDir = new File(String.valueOf(path));
        try {
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            FileOutputStream writer = new FileOutputStream(new File(path, fileName));
            writer.write(content.getBytes());
            writer.close();
            downloading.put(fileName, false);
        } catch (IOException e) {
        }
    }

    public void writeToFile(String fileName, byte[] content) {
        File path = new File(localDeArmazenamento);
        File newDir = new File(String.valueOf(path));
        try {
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            FileOutputStream writer = new FileOutputStream(new File(path, fileName));
            writer.write(content);
            writer.close();
            downloading.put(fileName, false);
        } catch (IOException e) {
        }
    }

    public void play() {
        mediaPlayer.start();
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }


    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void reset() {
        mediaPlayer.reset();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public float getPercent() {
        return ((float) getPosition() / getDuration()) * 100;
    }
}