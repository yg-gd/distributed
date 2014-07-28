import java.io.Serializable;


public class PrintLogMessage extends Message implements Serializable{

	public PrintLogMessage(String id) {
		super(id);
		
		type = MessageType.PRINTLOG;
		// TODO Auto-generated constructor stub
	}

}
