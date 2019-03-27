package JMS;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Shared.Serializer;
import client.RatingReply;
import client.RatingRequest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import javafx.application.Platform;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class JMSScraperAppGateWay {
    private JMSController controller;
    private MessageReceiverGateway scraperReceiver;
    private MessageSenderGateway rottenSender;
    private MessageSenderGateway imbdSender;
    private Serializer serializer;
    private HashMap<String, RatingRequest> scraperMap;
    private HashMap<String, Integer> aggragationMap;
    private ScraperRouter router;
    public JMSScraperAppGateWay(JMSController controller) {
        router = new ScraperRouter(2);
        this.controller = controller;
        scraperMap = new HashMap<>();
        aggragationMap = new HashMap<>();
        scraperReceiver = new MessageReceiverGateway("ScraperReplyChannel");
        rottenSender = new MessageSenderGateway("RottenTomatoRequestChannel");
        imbdSender = new MessageSenderGateway("IMBDRequestChannel");
        serializer = new Serializer();
        initReceiver();
    }
    public void initReceiver(){
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            RatingReply reply = serializer.stringToReply(new String(delivery.getBody(), "UTF-8"));
            RatingRequest request = scraperMap.get(delivery.getProperties().getCorrelationId());
            RatingReply replyResult = router.addReply(reply,delivery.getProperties().getCorrelationId());
            if(replyResult != null){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                            controller.add(replyResult,request);
                    }
                });
            };
        };
        scraperReceiver.setListener(deliverCallback);
    }
    public void sendRequest(RatingRequest request) throws IOException {
        final String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .build();
        rottenSender.SendMessage(serializer.requestToString(request), replyProps);
        imbdSender.SendMessage(serializer.requestToString(request), replyProps);
        scraperMap.put(corrId, request);
        aggragationMap.put(corrId, 2);

    }

}
