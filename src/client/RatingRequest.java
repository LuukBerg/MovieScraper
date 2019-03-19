package client;

import java.io.Serializable;

public class RatingRequest implements Serializable {
    private String movieName;

    public RatingRequest() {
    }

    public RatingRequest(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
    @Override
    public String toString() {
        return "movie name=" + movieName;
    }
}
