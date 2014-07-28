import java.io.Serializable;


public class AckMessage extends Message implements Serializable {

	boolean ack;
	public AckMessage(String id, boolean b) {
		super(id);
		ack = b;
		// TODO Auto-generated constructor stub
	}

}
