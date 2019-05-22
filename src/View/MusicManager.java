package View;

import javafx.scene.media.AudioClip;

 class MusicManager {
    final static String MENU_THEME = "resources/Sounds/title_theme.mp3";
    final static String MAIN_THEME = "resources/Sounds/Doot.mp3";
    final static String CLICK_SOUND = "resources/Sounds/clickSound.mp3";

    private AudioClip currentlyPlaying;

    MusicManager() {
        currentlyPlaying = new AudioClip(this.getClass().getResource(MAIN_THEME).toExternalForm());
    }

    void playMainTheme() {
        currentlyPlaying.stop();
        currentlyPlaying = new AudioClip(this.getClass().getResource(MAIN_THEME).toExternalForm());
        currentlyPlaying.setCycleCount(AudioClip.INDEFINITE);
        currentlyPlaying.play(0.3);
    }

    void playMenuTheme() {
        currentlyPlaying.stop();
        currentlyPlaying = new AudioClip(this.getClass().getResource(MENU_THEME).toExternalForm());
        currentlyPlaying.setCycleCount(AudioClip.INDEFINITE);
        currentlyPlaying.play(0.3);
    }

    void stopMusic() {
        currentlyPlaying.stop();
    }

    void playClickSound() {
        AudioClip click = new AudioClip(this.getClass().getResource(CLICK_SOUND).toExternalForm());
        click.setCycleCount(1);
        click.play(0.4);
    }
}
