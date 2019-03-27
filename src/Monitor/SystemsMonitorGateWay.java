package Monitor;

import Gateway.MessageReceiverGateway;
import Gateway.MessageSenderGateway;
import Serializer.Serializer;
import client.RatingRequest;

import java.io.IOException;
import java.util.*;

public class SystemsMonitorGateWay {
    private MessageReceiverGateway receiver;
    private Serializer serializer;
    private HashMap<String, Date> systemList;
    private HashMap<String, List<Boolean>> strikes;
    private HashMap<String, Date> lastDate;
    public SystemsMonitorGateWay() {
        serializer = new Serializer();
        receiver = new MessageReceiverGateway("MonitorReplyChannel");
        systemList = new HashMap<>();
        strikes = new HashMap<>();
        lastDate = new HashMap<>();
        Date date = new Date();
        date.setTime(0);
        systemList.put("Client", date);
        strikes.put("Client", new ArrayList<Boolean>());
        lastDate.put("Client", null);
        receiver.setListener((consumerTag, delivery) -> {
            LifeSign lifeSign = serializer.stringToLifeSign(new String(delivery.getBody(), "UTF-8"));
            systemList.replace(lifeSign.getName(), lifeSign.getDate());
        });
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < 50 ; i++) System.out.println("");
                System.out.flush();
                for (Map.Entry<String, Date> entry : systemList.entrySet()) {

                    if(strikes.get(entry.getKey()).size() != 3){
                        System.out.println("name: " + entry.getKey() + " date: " + entry.getValue());
                    }
                   else {
                        System.out.println("name: " + entry.getKey() + " DOWN");
                    }
                    if(lastDate.get(entry.getKey()) == null || entry.getValue() != lastDate.get(entry.getKey())){
                        lastDate.replace(entry.getKey(),entry.getValue());
                        strikes.get(entry.getKey()).clear();
                    }
                    else{
                        strikes.get(entry.getKey()).add(true);
                    }

                }
            }
        });
        thread.start();



    }

}
