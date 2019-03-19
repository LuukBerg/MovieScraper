package client;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javax.swing.*;

public class Controller {

    private DefaultListModel<RequestReply<RatingRequest, RatingReply>> listModel = new DefaultListModel<RequestReply<RatingRequest, RatingReply>>();
    private JList<RequestReply<RatingRequest, RatingReply>> requestReplyList;

    @FXML
    private ComboBox<String> movies;

    private ClientJMSAppGateway  clientJMSAppGateway;

    public Controller() {
        clientJMSAppGateway = new ClientJMSAppGateway();
    }

    @FXML
    public void SendRequest(){
        RatingRequest request = new RatingRequest(movies.getSelectionModel().toString());
        clientJMSAppGateway.RequestRating(request);
    }


    /**
     * This method returns the RequestReply line that belongs to the request from requestReplyList (JList).
     * You can call this method when an reply arrives in order to add this reply to the right request in requestReplyList.
     *
     * @param request
     * @return
     */
    public RequestReply<RatingRequest, RatingReply> getRequestReply(RatingRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            RequestReply<RatingRequest, RatingReply> rr = listModel.get(i);
            if (rr.getRequest() == request) {
                return rr;
            }
        }

        return null;
    }
    public void repaint(){
        requestReplyList.repaint();
    }
}
