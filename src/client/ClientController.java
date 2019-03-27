package client;

import Shared.MonitorGateway;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javax.swing.*;

public class ClientController {

    private DefaultListModel<RequestReply<RatingRequest, RatingReply>> listModel = new DefaultListModel<RequestReply<RatingRequest, RatingReply>>();
    private JList<RequestReply<RatingRequest, RatingReply>> requestReplyList;

    @FXML
    private ComboBox<String> movies;

    @FXML
    private ListView ratings;

    private ClientJMSAppGateway  clientJMSAppGateway;

    public ClientController() {
        clientJMSAppGateway = new ClientJMSAppGateway(this);
        new MonitorGateway("Client");

    }
    @FXML
    public void SendRequest(){
        RatingRequest request = new RatingRequest(movies.getValue());
        addToListView(new RequestReply<RatingRequest, RatingReply>(request, null));
                clientJMSAppGateway.RequestRating(request);

        //((Runnable) () -> clientJMSAppGateway.RequestRating(request)).run();

    }


    /**
     * This method returns the RequestReply line that belongs to the request from requestReplyList (JList).
     * You can call this method when an reply arrives in order to add this reply to the right request in requestReplyList.
     *
     * @param request
     * @return
     */
    public RequestReply<RatingRequest, RatingReply> getRequestReply(RatingRequest request) {

        for (int i = 0 ; i < ratings.getItems().size(); i++){
            RequestReply<RatingRequest,RatingReply> rr = (RequestReply<RatingRequest, RatingReply>) ratings.getItems().get(i);
            if(rr.getRequest() == request){
                return rr;
            }
        }
        return null;
    }
    private void addToListView(RequestReply rr){
        ObservableList observableList= ratings.getItems();
        observableList.add(rr);
        ratings.setItems(observableList);
    }
    public void repaint(){
        ratings.refresh();
    }
    public void add(RatingReply reply, RatingRequest request){
        RequestReply rr = getRequestReply(request);
        rr.setReply(reply);
        repaint();
    }
}
