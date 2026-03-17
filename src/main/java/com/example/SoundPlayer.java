package com.example;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundPlayer
{
    public static void play(String path)
    {
        try
        {
            InputStream inputStream = SoundPlayer.class.getResourceAsStream(path);

            if (inputStream == null)
                return;

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(inputStream)
            );

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}