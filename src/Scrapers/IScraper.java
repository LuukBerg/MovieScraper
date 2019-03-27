package Scrapers;

import client.RatingRequest;

public interface IScraper {
    Double getRating(RatingRequest Request) throws InterruptedException;
}
