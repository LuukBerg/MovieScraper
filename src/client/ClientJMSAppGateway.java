package client;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Serializer.Serializer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientJMSAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private Serializer serializer;
    private ListView ratings;
    private HashMap<String, RatingRequest> map;
    private ClientController controller;
    public ClientJMSAppGateway(ClientController controller){
        this.controller = controller;
        sender = new MessageSenderGateway("RatingRequestChannel");
        receiver = new MessageReceiverGateway("RatingReplyChannel");
        serializer = new Serializer();
        initReceiver();
        map = new HashMap<>();
    }

    public void initReceiver(){
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            RatingReply reply = serializer.stringToReply(new String(delivery.getBody(), "UTF-8"));
            RatingRequest request = map.get(delivery.getProperties().getCorrelationId());
                Platform.runLater(() -> controller.add(reply,request));
            };
        receiver.setListener(deliverCallback);
        };


    public void RequestRating(RatingRequest request) {
        try {
            final String corrId = UUID.randomUUID().toString();
            String replyQueueName = receiver.getChannel().queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();
            sender.SendMessage(serializer.requestToString(request), props);
            map.put(corrId, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setCorrIdListener(String corrId) throws InterruptedException, IOException {
       receiver.setListener((consumerTag, delivery) -> {
            RatingRequest request = map.get(delivery.getProperties().getCorrelationId());
            if (request != null) {
                RatingReply reply = serializer.stringToReply(new String(delivery.getBody(), "UTF-8"));
                controller.getRequestReply(request).setReply(reply);
                controller.repaint();
            }
        });

    }



}
