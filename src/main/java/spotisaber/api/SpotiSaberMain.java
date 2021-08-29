package spotisaber.api;

import spotisaber.api.spotify.SpotifyManager;
import lombok.Getter;
import me.frosty.docsplus.annotation.About;
import me.frosty.docsplus.annotation.Note;

@Getter //Can be called through the bootstrap implementation.
public class SpotiSaberMain {

    @About("An instance of the main application.")
    @Getter private static SpotiSaberMain instance;

    @Note("Managers allow us to reference code non-statically from the main instance.")
    private final SpotifyManager spotifyManager;

    @Note("Main constructor.")
    public SpotiSaberMain() {
        instance = this;

        this.spotifyManager = new SpotifyManager(this);
    }
}