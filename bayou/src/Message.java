import java.io.Serializable;


public class Message implements Serializable {
	
	String procid;
	String dest;
	
	MessageType type;
	
	public Message(String id){
		procid = id;
	}

}
