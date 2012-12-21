package machine;

public class Rounds {
	private int rd_number;//round number
	private String rd_session;//client session
	private int rd_time;//diff time in round
	
	public Rounds(int rd_number, String rd_session, int rd_time){
		this.rd_number = rd_number;
		this.rd_session = rd_session;
		this.rd_time = rd_time;
	}
	
	//getters & setters
	public void setNumber(int number){
		rd_number = number;
	}
	
	public int getNumber(){
		return rd_number;
	}
	
	public void setSession(String session){
		rd_session = session;
	}
	
	public String getSession(){
		return rd_session;
	}
	
	public void setTime(int time){
		rd_time = time;
	}
	
	public int getTime(){
		return rd_time;
	}
	//END of getters & setters
}
