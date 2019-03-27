package Scrapers.RottenTomato;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Scrapers.IScraper;
import Scrapers.MockScraper;
import Shared.Serializer;
import client.RatingReply;
import client.RatingRequest;
import client.RequestReply;
import com.rabbitmq.client.AMQP;

import java.util.HashMap;

public class RottenTomatoAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private Serializer serializer;
    private HashMap<String, RequestReply> map;
    private IScraper scraper;

    public RottenTomatoAppGateway(){
        scraper = new MockScraper();
        sender = new MessageSenderGateway("ScraperReplyChannel");
        receiver = new MessageReceiverGateway("RottenTomatoRequestChannel");
        serializer = new Serializer();
        receiver.setListener((consumerTag, delivery) -> {
            RatingRequest request = serializer.StringToRequest(new String(delivery.getBody(), "UTF-8"));
            System.out.println(delivery.getProperties().getCorrelationId());
            String corrId = delivery.getProperties().getCorrelationId();
            Double rating = 0.0;
            try {
                rating = scraper.getRating(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RatingReply reply = new RatingReply(request.getMovieName(), rating, "RottenTomato");
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .build();
            sender.SendMessage(serializer.replyToString(reply), replyProps);
        });

    }


}



