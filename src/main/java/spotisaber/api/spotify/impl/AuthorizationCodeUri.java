package spotisaber.api.spotify.impl;

import spotisaber.api.spotify.SpotifyManager;
import spotisaber.api.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import lombok.Getter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class AuthorizationCodeUri {

    private final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:1000");

    public String authCodeURI = "";

    private final CountDownLatch countdownLatch = new CountDownLatch(1);

    private final SpotifyManager manager;

    public final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(SpotifyManager.clientID).setClientSecret(SpotifyManager.clientSecret).setRedirectUri(this.redirectUri).build();

    private final AuthorizationCodeUriRequest authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri().build();

    public AuthorizationCodeUri(SpotifyManager manager) {
        this.manager = manager;
    }

    private URI authorizationCodeUri() {
        return (this.authorizationCodeUriRequest.execute());
    }

    //frosty server best ww!
    public void createAuthServer(URI request) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        HttpServer authServer = null;
        try {
            authServer = HttpServer.create(new InetSocketAddress(1000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert authServer != null;
        authServer.createContext("/", (HttpExchange h) -> {
            this.authCodeURI = h.getRequestURI().getQuery().substring(5);
            spotifyApi.authorizationCode(authCodeURI).build();

            System.out.println("Auth Code: " + this.authCodeURI);

            h.sendResponseHeaders(200, 0);

            final OutputStream responseBody = h.getResponseBody();

            responseBody.write("<h1>SPOTISABER</h1><h2>Spotify Linked Successfully</h2><h3>Please Return to SpotiSaber</h3></html>".getBytes());

            responseBody.close();

            this.countdownLatch.countDown();
        });

        authServer.setExecutor(executorService);
        authServer.start();
        Utils.openLinkInBrowser(request);

        try {
            this.countdownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(1, TimeUnit.HOURS); //Didnt ask xd
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        authServer.stop(0);

    }

    public void start() {
        this.createAuthServer(this.authorizationCodeUri());
    }
}
