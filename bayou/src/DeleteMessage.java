
public class DeleteMessage extends Message {

	String song;
	String url;
	
	public DeleteMessage(String id, String s, String u) {
		super(id);
		
		song = s;
		url = u;
		
		this.type = MessageType.DELETE;
	}
}
