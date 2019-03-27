package Scrapers.RottenTomato;

import Shared.MonitorGateway;

class RottenTomato {

    public static void main(String [] arguments){
        RottenTomatoAppGateway gateway = new RottenTomatoAppGateway();
        new MonitorGateway("RottenTomatoScraper");
    }
}
