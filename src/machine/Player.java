package machine;

public class Player {
	private String pl_name;//name on screen
	private String pl_session;//client session
	private int pl_score;//player's score
	
	public Player(String pl_name, String pl_session, int pl_score){
		this.pl_name = pl_name;
		this.pl_session = pl_session;
		this.pl_score = pl_score;
	}
	
	//getters & setters
	public void setName(String name){
		pl_name = name;
	}
	
	public String getName(){
		return pl_name;
	}
	
	public void setSession(String session){
		pl_session = session;
	}
	
	public String getSession(){
		return pl_session;
	}
	
	public void setScore(int score){
		pl_score = score;
	}
	
	public int getScore(){
		return pl_score;
	}
	//END of getters & setters
	
	//increase the score of the winner by 1
	public void increaseScore(){
		pl_score++;
	}
}
