import java.io.Serializable;


public class LogEntry implements Serializable {
	
	String song;
	String url;
	
	String originServ;
	int timestamp;
	
	int csn;
	
	public LogEntry(String s, String u, String id, int time){
		
		song = s;
		url = u;
		originServ = id;
		timestamp = time;
		
		csn = -1;
		
	}
	
	public boolean equals(Object o){
		if(!(o instanceof LogEntry)){
			return false;
		} else {
			LogEntry other = (LogEntry)o;
		    boolean ret = song.equals(other.song) &&
					url.equals(other.url) &&
					originServ.equals(other.originServ) &&
					timestamp == other.timestamp;

            return ret;
		}
	}
	
	public String toString(){
		return "Song: "+song + "\n\tURL: " + url + "\n\tOriginServ: "+originServ + "\n\tTimeStamp: " + timestamp + "\n\tCSN:" + csn;
	}

}
