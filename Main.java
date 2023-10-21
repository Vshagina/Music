package com.company;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;


public class Main {

    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\shagi\\Desktop\\music\\inFile.txt";
        String outputFilePath = "C:\\Users\\shagi\\Desktop\\music\\outFile.txt";

        try {
            List<String> musicURLs = readURLsFromFile(inputFilePath);
            writeURLsToFile(outputFilePath, musicURLs);
            downloadMusicFiles(musicURLs);
            playMusicFiles(musicURLs);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            System.err.println("Неподдерживаемый формат аудиофайла: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Линия недоступна: " + e.getMessage());
        }
    }

    private static List<String> readURLsFromFile(String filePath) throws IOException {
        List<String> musicURLs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                musicURLs.add(line);
            }
        }

        return musicURLs;
    }

    private static void writeURLsToFile(String filePath, List<String> musicURLs) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String url : musicURLs) {
                writer.write(url);
                writer.newLine();
            }
        }
    }

    private static void downloadMusicFiles(List<String> musicURLs) throws IOException {
        String downloadDirectory = "C:\\Users\\shagi\\Desktop\\music\\music";
        for (String url : musicURLs) {
            String fileName = getFileNameFromURL(url);
            URL downloadURL = new URL(url);
            Path targetPath = new File(downloadDirectory, fileName).toPath();
            Files.copy(downloadURL.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String getFileNameFromURL(String url) {
        int lastSlashIndex = url.lastIndexOf('/');
        String fileName = url.substring(lastSlashIndex + 1);
        return fileName.endsWith(".mp3") ? fileName : fileName + ".mp3";
    }

    private static void playMusicFiles(List<String> musicURLs) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        for (String url : musicURLs) {
            String fileName = getFileNameFromURL(url);
            String filePath = "C:\\Users\\shagi\\Desktop\\music\\music" + File.separator + fileName;

            File audioFile = new File(filePath);
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip audioClip = (Clip) AudioSystem.getLine(info);
                audioClip.open(audioStream);
                audioClip.start();

                while (audioClip.isOpen()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnsupportedAudioFileException e) {
                System.err.println("Неподдерживаемый формат аудио файла для: " + fileName);
            }
        }
    }
}
