package Scrapers.IMBD;

import Serializer.MonitorGateway;
import client.ClientJMSAppGateway;
import client.RatingReply;
import client.RatingRequest;
import client.RequestReply;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javax.swing.*;
import java.io.IOException;

public class IMBDController {

    private DefaultListModel<RequestReply<RatingRequest, RatingReply>> listModel = new DefaultListModel<RequestReply<RatingRequest, RatingReply>>();
    private JList<RequestReply<RatingRequest, RatingReply>> requestReplyList;


    @FXML
    private ListView requests;

    private  IMBDAppGateway gateway;

    public IMBDController() {

        gateway = new IMBDAppGateway(this);
        new MonitorGateway("IMBDScraper");
    }

    public void SendReply(){
        RequestReply rr = (RequestReply) requests.getSelectionModel().getSelectedItem();
        try {
            gateway.sendReply((RatingRequest) rr.getRequest());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToRequests(RatingRequest request){
        addToListView(new RequestReply<RatingRequest, RatingReply>(request, null));
    }

    public RequestReply<RatingRequest, RatingReply> getRequestReply(RatingRequest request) {

        for (int i = 0 ; i < requests.getItems().size(); i++){
            RequestReply<RatingRequest,RatingReply> rr = (RequestReply<RatingRequest, RatingReply>) requests.getItems().get(i);
            if(rr.getRequest() == request){
                return rr;
            }
        }
        return null;
    }
    private void addToListView(RequestReply rr){
        ObservableList observableList= requests.getItems();
        observableList.add(rr);
        requests.setItems(observableList);
    }
}
