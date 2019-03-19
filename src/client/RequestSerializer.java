package client;

import com.owlike.genson.Genson;

public class RequestSerializer {

    private Genson genson = new Genson();

    public String requestToString(RatingRequest request){
        return genson.serialize(request);
    }
    public RatingRequest StringToRequest(String str){
        return genson.deserialize(str, RatingRequest.class);
    }
}
