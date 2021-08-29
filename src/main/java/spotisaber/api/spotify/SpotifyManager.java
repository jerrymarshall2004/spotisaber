package spotisaber.api.spotify;

import spotisaber.api.SpotiSaberMain;
import spotisaber.api.BeatsaverSearch;
import spotisaber.api.spotify.impl.AuthorizationCodeUri;
import spotisaber.api.spotify.impl.AuthorizationToken;
import spotisaber.api.spotify.impl.GetPlaylistItems;
import lombok.Getter;
import lombok.SneakyThrows;
import me.frosty.application.ApplicationBootstrap;

@Getter
public class SpotifyManager {

    public static final String clientID = "redacted";
    public static final String clientSecret = "redacted";

    private final SpotiSaberMain main;

    private String[] beatSaverKeys;

    private AuthorizationCodeUri authorizationCodeUri;
    private AuthorizationToken authorizationToken;
    private GetPlaylistItems getPlaylistItems;

    public SpotifyManager(SpotiSaberMain main) {
        this.main = main;
    }

    public void load() {
        this.authorizationCodeUri = new AuthorizationCodeUri(this);
        this.authorizationToken = new AuthorizationToken();
        this.getPlaylistItems = new GetPlaylistItems();
    }

    @SneakyThrows
    public void fetchPlaylist(String playlistURL) {
        this.authorizationCodeUri.start();

        this.authorizationToken.clientCredentials();
        String[][] playlistItems = this.getPlaylistItems.start(playlistURL);
        BeatsaverSearch beatsaverSearch = new BeatsaverSearch();
        String[] beatSaverKeys = new String[0];

        for (String[] playlistItem : playlistItems) {

            final String currentKey = beatsaverSearch.run(playlistItem[0], playlistItem[1], playlistItem[2]);

            if (currentKey != null) {
                //n = The Length of "beatSaverKeys" plus One
                final int n = beatSaverKeys.length + 1;
                //"newRefinedSongs" has a length of n
                final String[] newBeatSaverKeys = new String[n];
                //Copy "beatSaverKeys" into "newBeatSaverKeys"
                System.arraycopy(beatSaverKeys, 0, newBeatSaverKeys, 0, n - 1);
                //The Key is Added to "newBeatSaverKeys"
                newBeatSaverKeys[n - 1] = currentKey;
                //Replace "refinedSongs" with "newRefinedSongs"
                beatSaverKeys = newBeatSaverKeys;
                this.beatSaverKeys = beatSaverKeys;
                ApplicationBootstrap.getInstance().getWebView().getEngine().executeScript("foundSong('" + playlistItem[2] + "','" + authorizationToken.spotifyApi.getAccessToken() + "')");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        ApplicationBootstrap.getInstance().getWebView().getEngine().executeScript("downloadButton()");
    }
}
