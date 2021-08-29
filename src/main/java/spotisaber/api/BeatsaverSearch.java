package spotisaber.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class BeatsaverSearch {

    @SneakyThrows
    public String run(String songName, String authorName, String songID) {

        String searchRequest;
        String songAuthor = authorName + "%20" + songName;

        if (songAuthor.contains(" ")) {
            searchRequest = songAuthor.replace(" ", "%20");
        } else {
            searchRequest = songAuthor;
        }
        //System.out.println("Searching BeatSaver for: " + songName + " " + authorName);
        //Define BeatSaver API Search URL
        URL url = new URL("https://beatsaver.com/api/search/text/0?q=" + searchRequest);

        //Connect to the API
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.connect();

        //If The HTTP Request is Successful
        if ((con.getResponseCode()) == 200) {
            //System.out.println("Connected Successfully");
            StringBuilder dataBuilder = new StringBuilder();

            //Opens a Connection To The API and "Scans it"
            Scanner webScanner = new Scanner(url.openStream());

            //While There is More Data to Scan, Scan It
            while (webScanner.hasNext()) {
                dataBuilder.append(webScanner.nextLine());
            }
            //Close the Scanner
            webScanner.close();

            //Close the Data Builder
            String data = dataBuilder.toString();

            //Convert the Data to a JSON
            JsonObject jsonData = (JsonObject) JsonParser.parseString(data);
            JsonArray songsJSON = jsonData.getAsJsonArray("docs");

            //Get Important Data Into A List
            String[][] songs = new String[songsJSON.size()][6];
            JsonObject currentSong;
            JsonObject currentSongStats;
            JsonObject currentSongMetadata;
            String currentSongID;
            String currentSongName;
            String currentSongAuthor;
            String currentSongUpVotes;
            String currentSongDownVotes;
            //System.out.println("Songs Found:");
            for (int i = 0; i < songsJSON.size(); i++) {
                currentSong = songsJSON.get(i).getAsJsonObject();
                currentSongStats = currentSong.get("stats").getAsJsonObject();
                currentSongMetadata = currentSong.get("metadata").getAsJsonObject();
                currentSongID = currentSong.get("id").getAsString();
                currentSongName = currentSongMetadata.get("songName").getAsString();
                currentSongAuthor = currentSongMetadata.get("songAuthorName").getAsString();
                currentSongUpVotes = currentSongStats.get("upvotes").getAsString();
                currentSongDownVotes = currentSongStats.get("downvotes").getAsString();
                songs[i][0] = currentSongID;
                songs[i][1] = currentSongName;
                songs[i][2] = currentSongAuthor;
                songs[i][3] = currentSongUpVotes;
                songs[i][4] = currentSongDownVotes;
                songs[i][5] = songID;

                //Print the List of Songs Found
                //System.out.println("ID: " + songs[i][0] + ", Name: " + songs[i][1] + ", Author: " + songs[i][2] + ", Upvotes: " + songs[i][3] + ", Downvotes: " + songs[i][4]);
            }

            String[][] refinedSongs = new String[0][6];
            //Filter the Songs by Name and Artist

            //For Every Song in Songs
            for (String[] song : songs) {
                //If the songs name and artist match the input
                if (song[1].toLowerCase().contains(songName.toLowerCase())// && song[2].toLowerCase().contains(authorName.toLowerCase())
                ) {
                    //n = The Length of "refinedSongs" plus One
                    int n = refinedSongs.length + 1;
                    //"newRefinedSongs" has a length of n
                    String[][] newRefinedSongs = new String[n][6];
                    //Copy "refinedSongs" into "newRefinedSongs"
                    System.arraycopy(refinedSongs, 0, newRefinedSongs, 0, n - 1);
                    //The Approved Song is Added to "newRefinedSongs"
                    newRefinedSongs[n - 1] = song;
                    //Replace "refinedSongs" with "newRefinedSongs"
                    refinedSongs = newRefinedSongs;
                }
            }
            //If there are any songs that have similarities
            if (refinedSongs.length != 0) {
                //Print the List of Refined Songs
                //System.out.println("Refined Songs:");

                //Find Top Voted Song Rating
                int maxRating = (Integer.parseInt(refinedSongs[0][3]) - Integer.parseInt(refinedSongs[0][4]));
                for (String[] refinedSong : refinedSongs) {
                    if ((Integer.parseInt(refinedSong[3]) - Integer.parseInt(refinedSong[4])) > maxRating) {
                        maxRating = (Integer.parseInt(refinedSong[3]) - Integer.parseInt(refinedSong[4]));
                    }
                }

                String[] song = new String[6];

                //Find Song with Top Rating
                for (String[] refinedSong : refinedSongs) {
                    if ((Integer.parseInt(refinedSong[3]) - Integer.parseInt(refinedSong[4])) == maxRating) {
                        song = refinedSong;
                    }
                }

                //Print The Top Song
                System.out.println("Found Song: " + song[1] + ", by: " + song[2] + ", with the key: " + song[0] + ", spotify id: " + song[5]);
                return song[0];
            }


        } else {
            System.out.println("Connection Failed. BeatSaver down?");
        }
        return null;
    }
}
