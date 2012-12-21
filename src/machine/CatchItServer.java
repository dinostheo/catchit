package machine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.management.InstanceAlreadyExistsException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;

/**
 * Servlet implementation class CatchItServer
 */
public class CatchItServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static int globalRound = 0;  // the global number of rounds
	private static boolean calculateFlag = true; // to be shure not to call calculateGlobalState twise
	private static ArrayList<Rounds> tempRoundStorage = new ArrayList<Rounds>(); // temporal storage of Roundss in the server
	
	// Starts a new game
	public void startGame(){
		// get a random number
		int ourNumber = randomNumber();
		// initialize global round
		globalRound = 0;
		
		Util utilAll = getUtil();
		// Make new first game in clients
		utilAll.addFunctionCall("makeTheGame", ourNumber, globalRound);
		// Disable the play button
		utilAll.addFunctionCall("disablebutton");
	}
	
	// Only one session calls this function
	//public synchronized void setWinner(String winner, int round, String diffTime){
	public synchronized void setWinner(int round){
		// if is the first process calling the method, otherwise drop the process
		
		if (round == globalRound )
		{
			globalRound++;
			calculateFlag = true;
			Util utilAll = getUtil();	
			utilAll.addFunctionCall("localState");
		}
			
	}
	
	// function that gets all the local states, calculates the global state and sends it back to clients
	public synchronized void globalState(String session, int roundNum, int time){
		//store the diffTimes for all the winners
		Rounds round = new Rounds(roundNum, session, time);
		tempRoundStorage.add(round);
		
		DAOMongodb dataBase = new DAOMongodb();
		//Calculate the actual Winner if I have received replies from all the clients
		if (dataBase.countPlayers() == tempRoundStorage.size()){
			if (calculateFlag){
				calculateFlag = false;
				calculateGlobalState();	
			}		
		}
	}
	
	// function that calculates the global state and sends it to the clients
	public void calculateGlobalState(){
		Rounds roundWinner;
		roundWinner = getWinner();
		int ourNumber = randomNumber(); // random position for the ball
		
		String output1 = "Round # " + globalRound + " <br> Winner name= " + roundWinner.getSession() + "<br> Winner time= " + roundWinner.getTime() + "";
		tempRoundStorage.clear();
		
		String output = "<ul> Score ";
		
		DAOMongodb dataBase = new DAOMongodb();
		ArrayList<Player> arrPlayers = dataBase.getArrayOfPlayers();
		
		for(int i=0; i<dataBase.countPlayers(); i++){
			output = output + "<li>" + arrPlayers.get(i).getName() + ": " + arrPlayers.get(i).getScore() + "</li>";
		}
		output =output + "</ul>";
		
		Util utilAll = getUtil();
		utilAll.removeAllOptions("resultData");
		utilAll.setValue("resultData", output1);
		utilAll.removeAllOptions("PlayerList");
		utilAll.setValue("PlayerList", output);
		// end of the game, a player has 20 points
		if (roundWinner.getNumber() == 19){
			utilAll.addFunctionCall("endGame",roundWinner.getSession());	
		}
		
		else{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			utilAll.addFunctionCall("makeTheGame", ourNumber, globalRound );
		}		
		
	}
	
	// TODO:Function that calculates the causal ordering of the messages
	public void causalOrdering(){

	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Useful functions
	
	// function that returns a Util 
	public Util getUtil(){
		WebContext wctx = WebContextFactory.get();
		String currentPage = wctx.getCurrentPage();
		//For all the browsers in the current page:
		Collection sessions = wctx.getScriptSessionsByPage(currentPage);
		Util tempUtil = new Util(sessions);
		return tempUtil;
	}
	
	
	// function that generates random numbers
	public int randomNumber(){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
	    for(int i=0; i<450; i++){
	    	numbers.add(i+1);
	    }
	    
	    //shuffle the numbers (positions) in the list
	    Collections.shuffle(numbers);
		return numbers.get(0);
	}
	
	public Rounds getWinner(){
			
		int minTime = 999999999;
		String winner = "";
		
		for(int i=0; i<tempRoundStorage.size(); i++){
			if (minTime > tempRoundStorage.get(i).getTime()){
				if (tempRoundStorage.get(i).getTime() != 0){
					minTime = tempRoundStorage.get(i).getTime();
					winner =  tempRoundStorage.get(i).getSession();
				}
			}
		}
		
		
		String winnerName = "";
		int endPoint = 0;
		
		DAOMongodb dataBase = new DAOMongodb();
		ArrayList<Player> arrPlayers = dataBase.getArrayOfPlayers();	
		dataBase.addRound(globalRound, winner, minTime);
		
		dataBase.addPoint(winner);
		
		for(int i=0; i<arrPlayers.size(); i++){
			if (winner.equals(arrPlayers.get(i).getSession())){

				//int temp =arrPlayers.get(i).getScore();
				//temp++;				
				//arrPlayers.get(i).setScore(temp);
				winnerName = arrPlayers.get(i).getName();
				
				// If the winner gets 20 points send the message in the round value of tempRound
				if (arrPlayers.get(i).getScore() == 19){
					endPoint = 19;					
				}
			}
		}		
		
		//Rounds tempRound = new Rounds(globalRound,winner,minTime);
		
		// I am sending here the winner name in place of the session
		// This is just a temporal class, so no problem if I F**K it up a little
		Rounds tempRound = new Rounds(endPoint,winnerName,minTime);
		
		return tempRound;
	}

	public synchronized void registerPlayer(String session, String name){
		//Player player = new Player(name, session, 0);
		
		DAOMongodb dataBase = new DAOMongodb();
		ArrayList<Player> arrPlayers = dataBase.getArrayOfPlayers();
		
		// Contolar si el usuario ya existe en la base de datos
		if (arrPlayers.isEmpty())
		{
			//storePlayers.add(player);
			dataBase.addPlayer(name, session,0);
		}
		else
		{
			boolean notfound = true;
			for(int i=0; i<arrPlayers.size(); i++){
				if (session.equals(arrPlayers.get(i).getSession())){
					dataBase.removePlayerBySession(session);
					dataBase.addPlayer(name, session,arrPlayers.get(i).getScore());
					notfound = false;
				}
			}
			if (notfound){
				//storePlayers.add(player);
				dataBase.addPlayer(name, session,0);
			}
				
		}
		
		String output = "<ul> Score ";
		//update the array of players
		arrPlayers = dataBase.getArrayOfPlayers();
		
		for(int i=0; i<arrPlayers.size(); i++){
			output = output + "<li>" + arrPlayers.get(i).getName() + ": " + arrPlayers.get(i).getScore() + "</li>";
		}
		output =output + "</ul>";

		Util utilAll = getUtil();
		utilAll.removeAllOptions("PlayerList");
		utilAll.setValue("PlayerList", output);
		
		// Disable the start game button if this is not the round 0
		if (globalRound != 0){
		utilAll.addFunctionCall("disablebutton");
		}
		// Enable the start game button if there are  2 players and 
		if (arrPlayers.size() == 2){
			if (globalRound == 0){
			utilAll.addFunctionCall("enablebutton");
			}
		}
	}
	
	public void resetGame(){
		DAOMongodb dataBase = new DAOMongodb();
		dataBase.clearPlayers();
		dataBase.clearRounds();
		globalRound = 0;
		Util utilAll = getUtil();
		utilAll.addFunctionCall("restartGame");		
	}
	
}

