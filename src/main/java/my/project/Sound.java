package my.project;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

public class Sound {
    private Clip musicClip;
    private static final String[] SOUND_FILE_NAMES = { "beep-6-96243.wav",
        "jump.wav", "move.wav", "game-over.wav", "hit.wav", "big-hit.wav"};

    public void play(int number, boolean music) {
        try {
            AudioInputStream ais = AudioSystem
                    .getAudioInputStream(getClass().getClassLoader().getResource(SOUND_FILE_NAMES[number]));
            Clip clip = AudioSystem.getClip();
            if (music) {
                musicClip = clip;
            }
            clip.open(ais);
            clip.addLineListener((event) -> {
                if (event.getType() == Type.STOP)
                    clip.close();
            });
            ais.close();
            clip.start();
        } catch (Exception e) {
            System.out.println("Something went bad.");
            throw new RuntimeException(e);
        }
    }

    public void loop() {
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        musicClip.stop();
        musicClip.close();
    }

}
