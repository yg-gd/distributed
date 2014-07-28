
public class AddMessage extends Message {

	String song;
	String url;
	
	public AddMessage(String id, String s, String u) {
		super(id);
		
		song = s;
		url = u;
		
		this.type = MessageType.ADD;
	}

}
