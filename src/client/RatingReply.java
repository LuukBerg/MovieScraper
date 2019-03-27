package client;

import java.io.Serializable;

public class RatingReply implements Serializable {
    private String movieName;
    private Double movieRating;
    private String providers;
    public RatingReply() {
    }

    public RatingReply(String movieName, Double movieRating, String providers) {
        this.movieName = movieName;
        this.movieRating = movieRating;
        this.providers = providers;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Double getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(double movieRating) {
        this.movieRating = movieRating;
    }

    public String getProviders() {
        return providers;
    }

    public void setProviders(String providers) {
        this.providers = providers;
    }

    @Override
    public String toString() {
        return "Rating: " + movieRating + " from: " + providers;
    }
}
