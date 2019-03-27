package Shared;

import Gateway.MessageSenderGateway;
import Monitor.LifeSign;

import java.io.IOException;

public class MonitorGateway {
    private MessageSenderGateway sender;
    private String systemName;
    private Serializer serializer;
    public MonitorGateway(String systemName) {
        this.systemName = systemName;
        serializer = new Serializer();
        sender = new MessageSenderGateway("MonitorReplyChannel");
        Thread senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        sender.SendMessage(serializer.lifeSingToString(new LifeSign(systemName)), null);
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        senderThread.start();
    }
}
