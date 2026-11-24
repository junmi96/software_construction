package edu.hitsz.application;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class MusicThread extends Thread {

    private static final int BUFFER_SIZE = 4096;

    private final String filename;
    private final boolean loop;
    private volatile boolean running = true;
    private volatile SourceDataLine dataLine;

    public MusicThread(String filename) {
        this(filename, false);
    }

    public MusicThread(String filename, boolean loop) {
        this.filename = filename;
        this.loop = loop;
        setName("music-thread-" + new File(filename).getName());
        setDaemon(true);
    }

    public void stopMusic() {
        running = false;
        interrupt();
        closeCurrentLine();
    }

    @Override
    public void run() {
        while (running) {
            playOnce();
            if (!loop) {
                break;
            }
        }
    }

    private void playOnce() {
        File audioFile = new File(filename);
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine openedLine = (SourceDataLine) AudioSystem.getLine(info);
            openedLine.open(audioFormat);
            dataLine = openedLine;
            openedLine.start();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while (running && (bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                openedLine.write(buffer, 0, bytesRead);
            }
            if (running) {
                openedLine.drain();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            running = false;
        } finally {
            closeCurrentLine();
        }
    }

    private void closeCurrentLine() {
        SourceDataLine line = dataLine;
        dataLine = null;
        if (line != null) {
            line.stop();
            line.flush();
            line.close();
        }
    }
}
