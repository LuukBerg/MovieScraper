package JMS;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Serializer.RequestSerializer;
import client.RatingRequest;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;

public class JMSJMSAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private RequestSerializer serializer;

    public JMSJMSAppGateway(){
        sender = new MessageSenderGateway("RatingRequestChannel");
        receiver = new MessageReceiverGateway("RatingRequestChannel");
        serializer = new RequestSerializer();
        receiver.setListener((consumerTag, delivery) -> {
            RatingRequest request = serializer.StringToRequest(new String(delivery.getBody(), "UTF-8"));
            System.out.println(" [x] Received '" + request.toString());
        });
    }

    public void RequestRating(RatingRequest request) {
        try {
            sender.SendRequest(serializer.requestToString(request));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
