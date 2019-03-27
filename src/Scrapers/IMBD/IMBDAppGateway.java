package Scrapers.IMBD;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Scrapers.IScraper;
import Scrapers.MockScraper;
import Serializer.Serializer;
import client.RatingReply;
import client.RatingRequest;
import client.RequestReply;
import com.rabbitmq.client.AMQP;
import javafx.application.Platform;
import jdk.nashorn.internal.ir.RuntimeNode;

import java.io.IOException;
import java.util.HashMap;

public class IMBDAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private Serializer serializer;
    private HashMap<RatingRequest, String> map;
    private IScraper scraper;
    private IMBDController controller;

    public IMBDAppGateway(IMBDController controller){
        this.controller = controller;
        map = new HashMap<>();
        scraper = new MockScraper();
        sender = new MessageSenderGateway("ScraperReplyChannel");
        receiver = new MessageReceiverGateway("IMBDRequestChannel");
        serializer = new Serializer();
        receiver.setListener((consumerTag, delivery) -> {
            RatingRequest request = serializer.StringToRequest(new String(delivery.getBody(), "UTF-8"));
            System.out.println(delivery.getProperties().getCorrelationId());
            String corrId = delivery.getProperties().getCorrelationId();
            map.put(request, corrId);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.addToRequests(request);
                }
            });
        });

    }
    public void sendReply(RatingRequest request) throws IOException {
        Double rating = 0.0;
        try {
            rating = scraper.getRating(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RatingReply reply = new RatingReply(request.getMovieName(), rating, "IMBD");

        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(map.get(request))
                .build();
        sender.SendMessage(serializer.replyToString(reply), replyProps);
    }


}
