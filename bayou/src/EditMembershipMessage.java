import java.io.Serializable;


public class EditMembershipMessage extends Message implements Serializable{

	String newproc;
	boolean joining;
	
	public EditMembershipMessage(String id,String newproc, boolean b) {
		super(id);
		
		type = MessageType.EDITMEMBERSHIP;
		this.newproc = newproc;
		joining = b;
		// TODO Auto-generated constructor stub
	}

}
