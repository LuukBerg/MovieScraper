package JMS;

import Shared.MonitorGateway;
import client.RatingReply;
import client.RatingRequest;
import client.RequestReply;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


import java.io.IOException;

public class JMSController {

    private JMSClientAppGateWay clientGateway;
    private JMSScraperAppGateWay scraperGateway;
    @FXML
    ListView requestReplyList;


    public JMSController() throws IOException {

        clientGateway = new JMSClientAppGateWay(this);
        scraperGateway = new JMSScraperAppGateWay(this);
        new MonitorGateway("JMS");
    }


    public void addToRequests(RatingRequest request) throws IOException {
        addToListView(new RequestReply<RatingRequest, RatingReply>(request, null));
        scraperGateway.sendRequest(request);
    }
    public RequestReply<RatingRequest, RatingReply> getRequestReply(RatingRequest request) {

        for (int i = 0 ; i < requestReplyList.getItems().size(); i++){
            RequestReply<RatingRequest,RatingReply> rr = (RequestReply<RatingRequest, RatingReply>) requestReplyList.getItems().get(i);
            if(rr.getRequest() == request){
                return rr;
            }
        }
        return null;
    }
    private void addToListView(RequestReply rr){
        ObservableList observableList= requestReplyList.getItems();
        observableList.add(rr);
        requestReplyList.setItems(observableList);
    }
    public void repaint(){
        requestReplyList.refresh();
    }
    public void add(RatingReply reply, RatingRequest request){
        RequestReply rr = getRequestReply(request);
        rr.setReply(reply);
        repaint();
        clientGateway.sendReply(rr);

    }
}
