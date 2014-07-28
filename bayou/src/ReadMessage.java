
public class ReadMessage extends Message {

	String song;
	
	public ReadMessage(String id, String s) {
		super(id);
		
		song  = s;
		
		this.type = MessageType.READ;
		// TODO Auto-generated constructor stub
	}

}
