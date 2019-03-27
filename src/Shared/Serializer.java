package Shared;

import Monitor.LifeSign;
import client.RatingReply;
import client.RatingRequest;
import com.owlike.genson.Genson;

public class Serializer {

    private Genson genson = new Genson();

    public String requestToString(RatingRequest request){
        return genson.serialize(request);
    }
    public RatingRequest StringToRequest(String str){
        return genson.deserialize(str, RatingRequest.class);
    }
    public String replyToString(RatingReply reply){return genson.serialize(reply);}
    public RatingReply stringToReply(String str){return genson.deserialize(str, RatingReply.class);}
    public LifeSign stringToLifeSign(String str){return genson.deserialize(str, LifeSign.class);}
    public String lifeSingToString(LifeSign lifeSign){return genson.serialize(lifeSign);}
}
