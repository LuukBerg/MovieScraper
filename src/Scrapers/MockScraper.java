package Scrapers;

import client.RatingRequest;

import java.util.Random;

public class MockScraper implements IScraper {
    @Override
    public Double getRating(RatingRequest request) throws InterruptedException {
        Random r = new Random();

        int sleep = r.nextInt(2000);
        System.out.println("sleeping for: " + sleep);
        Thread.sleep(sleep);
        if(sleep > 1500){
            return null;
        }
        double rating = (Math.random() * ((10 - 1) + 1));
        System.out.println("Rating: " + rating);
        return Math.round(rating * 10) / 10.0;
    }
}
