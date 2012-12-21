package machine;

import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
//import com.mongodb.util.JSON;

public class DAOMongodb {
	
	//connetion to the database
	public DB connectToMongo(){
		Mongo m;
		try {
			m = new Mongo("localhost" , 27017 );
			DB db = m.getDB( "catchit" );
			System.out.println();
			return db;
		} catch (Exception e) {
			System.out.println("got in the exception mongodb connection");
			return null;
		}
	}
	
	/////// PLAYER ///////////
	
	//add a new player
	public void addPlayer(String name, String session, int score){
		DB db = connectToMongo();
		Integer tempScore = (Integer)score;
		String stScore = tempScore.toString();
				
		DBCollection coll = db.getCollection("players");
		BasicDBObject doc = new BasicDBObject();
		doc.put("name", name);
		doc.put("session", session);
		doc.put("score", stScore);
		
		coll.insert(doc);
	}
	
	//remove all players
	public void clearPlayers(){
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("players");
		
		DBCursor cursor = coll.find();
		while(cursor.hasNext()) {
			coll.remove(cursor.next());
		}
	}
	
	//checks if a player is in the db and returns a boolean
	public boolean checkPlayerSession(String session){
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("players");
		
		DBCursor cursor = coll.find();
		while(cursor.hasNext()) {
			String tempCheck = (String)cursor.next().get("session");
			if(tempCheck.equals(session)){
				return true;
			}
		}
		return false;
	}

	// Removes a player by the number of session
	public void removePlayerBySession(String session){
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("players");
		
		BasicDBObject document = new BasicDBObject();
		document.put("session", session);
		coll.remove(document);
	}	
	
	//returns an array with all the players
	public ArrayList<Player> getArrayOfPlayers(){
		ArrayList<Player> savePlayers = new ArrayList<Player>();
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("players");
		DBCursor cursor = coll.find();
		
			while(cursor.hasNext()) {
				DBObject tobj = cursor.next();
				String tempName = (String)tobj.get("name");
				String tempSession = (String)tobj.get("session");
				String tempStringScore = (String)tobj.get("score");
				
				int tempScore = Integer.parseInt(tempStringScore);
				
				Player player = new Player(tempName,tempSession,tempScore);
				savePlayers.add(player);
			}
		
		
		return savePlayers;
	}
	
	//increases by 1 point the score of the winner given the session
	public void addPoint(String session){
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("players");
		
		DBCursor cursor = coll.find();
		while(cursor.hasNext()) {
			DBObject tobj = cursor.next();
			String tempCheck = (String)tobj.get("session");
			if(tempCheck.equals(session)){
				String tempStringScore = (String)tobj.get("score");
				int tempScore = Integer.parseInt(tempStringScore);
				int newScore = tempScore + 1;
				String newString = Integer.toString(newScore);
				BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append("score", newString));
				coll.update(new BasicDBObject().append("session", session), newDocument);
			}
		}
	}
	
	//count the amount of rounds in the collection
	public int countPlayers(){
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("players");
		
		Integer counter = (int)coll.count();
		
		return counter;
	}
	
	////// ROUND /////////////
	   //add a new round
    public void addRound(int number, String session, int difTime){
        DB db = connectToMongo();
        
        DBCollection coll = db.getCollection("rounds");
        BasicDBObject doc = new BasicDBObject();
        if(secureRound(number)){
            doc.put("round", number);
            doc.put("session", session);
            doc.put("time", difTime);
            
            coll.insert(doc);
        }
    }

    public boolean secureRound(int number){
        DB db = connectToMongo();
        DBCollection coll = db.getCollection("rounds");
        
        DBCursor cursor = coll.find();
        while(cursor.hasNext()) {
            DBObject tobj = cursor.next();
            int tempRound = (Integer)tobj.get("round");
            if(tempRound == number){
                return false;
            }
        }
        return true;
    }
	
	//remove all rounds
	public void clearRounds(){
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("rounds");
		
		DBCursor cursor = coll.find();
		while(cursor.hasNext()) {
			coll.remove(cursor.next());
		}
	}
	
	//count the amount of rounds in the collection
	public int countRounds(){
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("rounds");
		
		Integer counter = (int)coll.count();
		
		return counter;
	}
	
	//returns an array with all the players
	public ArrayList<Rounds> getArrayOfRounds(){
		ArrayList<Rounds> saveRounds = new ArrayList<Rounds>();
		DB db = connectToMongo();
		DBCollection coll = db.getCollection("rounds");
		DBCursor cursor = coll.find();
		
			while(cursor.hasNext()) {
				DBObject tobj = cursor.next();
				String tempRound = (String)tobj.get("round");
				int tempRound2 = Integer.parseInt(tempRound);
				String tempSession = (String)tobj.get("session");
				String tempTime = (String)tobj.get("time");
				int tempTime2 = Integer.parseInt(tempTime);

				Rounds round = new Rounds(tempRound2,tempSession,tempTime2);
				saveRounds.add(round);
			}	
		return saveRounds;
	}
}
