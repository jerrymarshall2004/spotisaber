package me.frosty.application.javascript;

import spotisaber.api.SpotiSaberMain;
import spotisaber.api.util.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.frosty.application.ApplicationBootstrap;
import me.frosty.docsplus.annotation.Note;

@Getter
@RequiredArgsConstructor
public class JavaScriptFeedback {

    private final ApplicationBootstrap bootstrap;

    @Note("Called by the feedback and closes the application.")
    public void close() {
        this.bootstrap.terminate();
    }

    @Note("Runs the spotify checker for a playlist by url.")
    public void runPlaylist(String playlistURL) throws InterruptedException {

        Thread.sleep(50);

        this.bootstrap.getSpotiSaberMain().getSpotifyManager().load();

        this.bootstrap.getSpotiSaberMain().getSpotifyManager().fetchPlaylist(playlistURL);

        this.bootstrap.getSpotiSaberMain().getSpotifyManager().getGetPlaylistItems().start(playlistURL);

    }

    public void runDownload() throws InterruptedException {

        for (String beatSaverKey : SpotiSaberMain.getInstance().getSpotifyManager().getBeatSaverKeys()) {

            System.out.println("Downloading: " + beatSaverKey);

            Utils.openLinkInBrowser("https://beatsaver.com/api/download/key/" + beatSaverKey);

            //TODO Change this. This will sleep the entire application (Thats not a good thing)
            Thread.sleep(1500);
        }
    }
}
