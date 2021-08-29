package spotisaber.api.spotify.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spotisaber.api.SpotiSaberMain;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Getter
public class GetPlaylistItems {

    private final SpotifyApi spotifyApi = SpotiSaberMain.getInstance().getSpotifyManager().getAuthorizationCodeUri().spotifyApi;

    private GetPlaylistsItemsRequest getPlayListItemsRequest;

    public String[][] getPlaylistItems() {
        try {
            final Paging<PlaylistTrack> playlistTrackPaging = getPlayListItemsRequest.execute();
            PlaylistTrack[] playlistSongs = playlistTrackPaging.getItems();
            String[][] parsedPlaylistSongs = new String[playlistTrackPaging.getTotal()][3];
            for (int i = 0; i < parsedPlaylistSongs.length; i++) {
                parsedPlaylistSongs[i][0] = playlistSongs[i].getTrack().getName();
                parsedPlaylistSongs[i][1] = idToArtist(playlistSongs[i].getTrack().getId());
                parsedPlaylistSongs[i][2] = playlistSongs[i].getTrack().getId();
            }
            return parsedPlaylistSongs;
        } catch (IOException | SpotifyWebApiException | ParseException exception) {
            System.out.println(exception.getMessage());

        }
        return new String[0][];
    }

    @SneakyThrows
    public String idToArtist(String ID) {
        URL url = new URL("https://api.spotify.com/v1/tracks/" + ID);
        //Connect to the API
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Authorization", "Bearer " + spotifyApi.getAccessToken());

        //If The HTTP Request is Successful
        if ((con.getResponseCode()) == 200) {
            InputStream inputStream = con.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder data = new StringBuilder();
            String currentLine;
            while ((currentLine = in.readLine()) != null)
                data.append(currentLine);
            in.close();
            con.disconnect();

            JsonObject jsonData = (JsonObject) JsonParser.parseString(data.toString());
            JsonArray songArtists = jsonData.getAsJsonArray("artists");

            return (songArtists.get(0).getAsJsonObject().get("name").getAsString());
        } else {
            System.out.println(con.getResponseCode());
            return con.getResponseMessage();
        }
    }

    public String[][] start(String url) {
        final URI playlistURL = URI.create(url);
        final String playlistID = playlistURL.getPath().substring(10);
        this.getPlayListItemsRequest = this.spotifyApi.getPlaylistsItems(playlistID).build();
        return getPlaylistItems();
    }
}
