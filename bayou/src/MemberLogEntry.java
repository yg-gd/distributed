public class MemberLogEntry extends LogEntry {

	String member;
	boolean joining;

	public MemberLogEntry(String mem,boolean join, String id, int time) {
		super("", "", id, time);
		// TODO Auto-generated constructor stub
		member = mem;
		joining = join;
	}

	public boolean equals( Object o ){
		
		return(o instanceof MemberLogEntry &&
				super.equals(o) &&
				member.equals(((MemberLogEntry)o).member)&&
				joining == ((MemberLogEntry)o).joining);
	
	}

	public String toString(){
		return "Member: "+member  + "\n\tStatus: " + (joining?" joining":" leaving ")+ "\n\tOriginServ: "+originServ + "\n\tTimeStamp: " + timestamp + "\n\tCSN:" + csn;
	}
}
