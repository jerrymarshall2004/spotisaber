package spotisaber.api.spotify.impl;
import spotisaber.api.SpotiSaberMain;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public class AuthorizationToken {

    public final SpotifyApi spotifyApi = SpotiSaberMain.getInstance().getSpotifyManager().getAuthorizationCodeUri().getSpotifyApi();

    private final ClientCredentialsRequest clientCredentialsRequest = this.spotifyApi.clientCredentials().build();

    public void clientCredentials() {
        try {
            final ClientCredentials clientCredentials = this.clientCredentialsRequest.execute();

            this.spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            System.out.println("Client Token: " + clientCredentials.getAccessToken());
            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
            clientCredentials.getAccessToken();

        } catch (IOException | SpotifyWebApiException | ParseException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
