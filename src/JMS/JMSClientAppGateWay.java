package JMS;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Serializer.Serializer;
import client.RatingReply;
import client.RatingRequest;
import client.RequestReply;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import javafx.application.Platform;

import java.io.IOException;
import java.util.HashMap;

public class JMSClientAppGateWay {

    private JMSController controller;
    private MessageSenderGateway clientSender;
    private MessageReceiverGateway clientReceiver;
    private Serializer serializer;
    private HashMap<RatingRequest, String> clientMap;
    public JMSClientAppGateWay(JMSController controller) {
        this.controller = controller;
        clientMap = new HashMap<>();
        clientSender = new MessageSenderGateway("RatingReplyChannel");
        clientReceiver = new MessageReceiverGateway("RatingRequestChannel");
        serializer = new Serializer();
        initReceiver();
    }

    public void initReceiver(){
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            RatingRequest request = serializer.StringToRequest(new String(delivery.getBody(), "UTF-8"));

            clientMap.put(request,delivery.getProperties().getCorrelationId());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        controller.addToRequests(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        };
        clientReceiver.setListener(deliverCallback);
    }
    public void sendReply(RequestReply rr){
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(clientMap.get(rr.getRequest()))
                .build();
        try {
            clientSender.SendMessage(serializer.replyToString((RatingReply)rr.getReply()), replyProps);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
