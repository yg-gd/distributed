
public class EditMessage extends Message {

	String song;
	String url;
	
	public EditMessage(String id, String s, String u) {
		super(id);
		
		song = s;
		url = u;
		
		this.type = MessageType.EDIT;
	}

}
