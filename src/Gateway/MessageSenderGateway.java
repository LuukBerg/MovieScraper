package Gateway;



import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageSenderGateway {
    private Channel channel;
    private String channelName;
    private String exchangeName = "";
    public MessageSenderGateway(String channelName){
        this.channelName = channelName;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(channelName,false,false,false,null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
    public MessageSenderGateway(String channelName, String exchangeName, String mode){
        this.channelName = "";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(exchangeName, mode);
            this.exchangeName = exchangeName;
           // channel.queueDeclare(channelName,false,false,false,null);
           // channel.queueBind(channelName, exchangeName, "");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }
    public void SendMessage(String body, AMQP.BasicProperties props) throws IOException{
        channel.basicPublish(exchangeName,channelName,props, body.getBytes());
    }
    public Channel getChannel(){
        return channel;
    }

}
