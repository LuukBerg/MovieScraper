package client;

import Gateway.MessageSenderGateway;
import Serializer.RequestSerializer;

import java.io.IOException;

public class ClientJMSAppGateway {
    private MessageSenderGateway sender;
    private RequestSerializer serializer;

    public ClientJMSAppGateway(){
        sender = new MessageSenderGateway("RatingRequestChannel");
        serializer = new RequestSerializer();
    }

    public void RequestRating(RatingRequest request) {
        try {
            sender.SendRequest(serializer.requestToString(request));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
