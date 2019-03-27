package JMS;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Shared.Serializer;
import client.RatingReply;
import client.RatingRequest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class JMSJMSAppGateway {
    private MessageSenderGateway clientSender;
    private MessageReceiverGateway clientReceiver;
    private Serializer serializer;
    private MessageReceiverGateway scraperReceiver;
    private MessageSenderGateway rottenSender;
    private MessageSenderGateway imbdSender;
    private JMSController controller;

    public JMSJMSAppGateway(JMSController controller) throws IOException {
        this.controller = controller;
        clientSender = new MessageSenderGateway("RatingReplyChannel");
        clientReceiver = new MessageReceiverGateway("RatingRequestChannel");
        scraperReceiver = new MessageReceiverGateway("ScraperReplyChannel");
        rottenSender = new MessageSenderGateway("RottenTomatoRequestChannel");
        imbdSender = new MessageSenderGateway("IMBDRequestChannel");
        serializer = new Serializer();
        Thread thread = new Thread(() -> SetListener());
        thread.start();
    }


    public void RequestRating(RatingRequest request) {
        try {
            clientSender.SendMessage(serializer.requestToString(request), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void SetListener(){
        Object monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();
                RatingRequest request = null;
                try {
                    request = serializer.StringToRequest(new String(delivery.getBody(), "UTF-8"));
                }
                catch (RuntimeException e){
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {
                    final String corrId = UUID.randomUUID().toString();
                    String replyQueueName = null;
                    try {
                        replyQueueName = scraperReceiver.getChannel().queueDeclare().getQueue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AMQP.BasicProperties props = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(corrId)
                            .replyTo(replyQueueName)
                            .build();
                    try {
                        System.out.println(" send corrid: " + corrId);
                        rottenSender.SendMessage(serializer.requestToString(request), props);
                        imbdSender.SendMessage(serializer.requestToString(request), props);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    RatingReply reply = null;
                    try {
                         reply = createAverageReply(setCorrIdListener(corrId));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(replyProps.getCorrelationId());
                    try {
                        clientSender.SendMessage(serializer.replyToString(reply), replyProps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    synchronized (monitor){
                        monitor.notify();
                    }
                }
        };
        clientReceiver.setListener(deliverCallback);
        while(true){
            synchronized (monitor){
                try{
                    monitor.wait();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public List<RatingReply> setCorrIdListener(String corrId) throws InterruptedException, IOException {
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(2);
        List<RatingReply> ratings = new ArrayList<>();
        String ctag = scraperReceiver.setListener((consumerTag, delivery) -> {
            System.out.println(delivery.getProperties().getCorrelationId() + " = " + corrId + " sender: " + serializer.stringToReply(new String(delivery.getBody(), "UTF-8")).getProviders());
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        });
        for(int i = 0; i <2 ; i++){
            String result = response.take();

            ratings.add(serializer.stringToReply(result));
        }
        scraperReceiver.getChannel().basicCancel(ctag);
        return ratings;
    }
    public RatingReply createAverageReply(List<RatingReply> ratings){
        List<Double> movieRatings = new ArrayList<>();
        Double sum = 0.0;
        int counter = 0;
        String providers = "";
        for (RatingReply reply: ratings) {
            if(reply.getMovieRating() != null){
                counter++;
                movieRatings.add(reply.getMovieRating());
                sum += reply.getMovieRating();
                providers += reply.getProviders() + " ";
            }
        }
        return new RatingReply(ratings.get(0).getMovieName(), sum / counter, providers);
    }

}
