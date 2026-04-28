package com.example;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

public class SoundPlayer
{
    public static void play(String fileName, float percentVolume)
    {
        try
        {
            // Resolve base directory (jar vs dev)
            File location = new File(
                    SoundPlayer.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );

            File baseDir;

            if (location.isFile())
            {
                // Running from shadow jar (REAL USERS)
                baseDir = location.getParentFile();
            }
            else
            {
                // Running from IntelliJ (DEV)
                baseDir = new File(System.getProperty("user.dir"));
            }

            File soundFile = new File(baseDir, "assets/sounds/" + fileName);

            if (!soundFile.exists())
            {
                System.out.println("Sound not found: " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Clamp volume to safe range
            percentVolume = Math.max(0.0001f, Math.min(percentVolume, 1.0f));

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
            {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                float dB = (float) (Math.log10(percentVolume) * 20.0);
                gainControl.setValue(dB);
            }

            clip.start();

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP)
                {
                    clip.close();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}