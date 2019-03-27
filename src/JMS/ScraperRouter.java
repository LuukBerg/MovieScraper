package JMS;

import client.RatingReply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScraperRouter {

    private Map<String, List<RatingReply>> replies = new HashMap<>();
    private int scraperSize;
    public ScraperRouter(int scraperSize) {
        this.scraperSize = scraperSize;
    }

    public RatingReply addReply(RatingReply reply, String corrid){
        if(replies.get(corrid) == null){
            List<RatingReply> list = new ArrayList<>();
            list.add(reply);
            replies.put(corrid, list);
        }
        else{
            replies.get(corrid).add(reply);
        }
        if(replies.get(corrid).size() == scraperSize){
            List<RatingReply> rr = replies.get(corrid);
            return getAverageReply(rr);
        }
        return null;
    }
    public RatingReply getAverageReply(List<RatingReply> ratings){
        List<Double> movieRatings = new ArrayList<>();
        Double sum = 0.0;
        int counter = 0;
        String providers = "";
        for (RatingReply reply: ratings) {
            if(reply.getMovieRating() != 0.0){
                counter++;
                movieRatings.add(reply.getMovieRating());
                sum += reply.getMovieRating();
                providers += reply.getProviders() + " ";
            }
        }
        return new RatingReply(ratings.get(0).getMovieName(), sum / counter, providers);
    }
}
