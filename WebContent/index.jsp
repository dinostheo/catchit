<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Catchit!</title>
<link rel=StyleSheet href="css/style.css" type="text/css">
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.js"></script>
  <script type='text/javascript' src='/DistributedGame/dwr/interface/CatchItServer.js'></script>
  <script type='text/javascript' src='/DistributedGame/dwr/engine.js'></script>
  <script type='text/javascript' src='/DistributedGame/dwr/util.js'></script>
<script type="text/javascript">

var beginTime = 0; // Initial time for catching the ball
var round = 0; // local value for round
var diffTime = 0; // value for the time difference
var quitterTime;

// Creates a new empty table
 function makeTable() {
	 var tableID = "Table2";
	 var cellID = "n";
	 var target = "BigTable2";
	 row=new Array();
	 cell=new Array();

	 row_num=15; //edit this value to the desired one
	 cell_num=30; //edit this value to the desired one

	 tab=document.createElement('table');
	 tab.setAttribute('id',tableID);

	 tbo=document.createElement('tbody');
	 var counter=1;
	 for(c=0;c<row_num;c++){
		 row[c]=document.createElement('tr');
	
		 for(k=0;k<cell_num;k++) {
			 cell[k]=document.createElement('td');
			 
			 cell[k].setAttribute('id', cellID+counter );
			 counter++;
			 row[c].appendChild(cell[k]);
		 }
		 tbo.appendChild(row[c]);
	 }
	 
	 tab.appendChild(tbo);
	 $("#"+target).html(tab);
}

//function that starts the game in round 1 
 function startGame(){
 	CatchItServer.startGame();
 }

// creates the new game 
function makeTheGame(number,roundNumber){
	makeTable();
	
	round = roundNumber;
	// init this values for every new round
	now = new Date();
	beginTime = now.getTime();
	diffTime = 0;
	clearTimeout(quitterTime); 
	locateSpot(number);
}

// Locates the ball in the table
function locateSpot(position){
	 var target = "#n"+position;
	 	 
	 //test function to print the begintime for each browser
	 //document.getElementById('beginTime').innerText = beginTime;
	 
	 $(target).html("<img id='redspot' src='images/redball.png'/>");
	 $("#redspot").click(function(){
		 $(target).html("");
		 winnerMessage();
	 });
}

// function launched when user clicks the ball
function winnerMessage(){
		 
	// save the real diffTime in the client
	now = new Date();
	endTime = now.getTime();
	diffTime = endTime - beginTime;
	// display my local diffTime in browser
	document.getElementById('myTime').innerHTML = "My time = " + diffTime ;
	    
    //CatchItServer.setWinner(winnerSession,round,diffTime);
	CatchItServer.setWinner(round);
}
	
// function to send the local state to the server
function localState(){
	//make the ball disapear
	makeTable();
	quitterTime = setTimeout("quitter()",5000);
	var session = "<%= session.getId() %>";
	CatchItServer.globalState(session,round,diffTime);
		
}
	
	
// function that disables the button start after user press it
function disablebutton(){
	document.getElementById("start").disabled=true;
	document.getElementById("start").innerHTML = "";
	document.getElementById("start").className = "start1";
}
	
// Function that enables the button after there are more than 1 user
function enablebutton(){
	document.getElementById("start").disabled=false;
	document.getElementById("start").innerHTML = "";
	document.getElementById("start").className = "start";
}
	
// Function called to register new players in the game
function registerPlayer(){
	var name = $('#registerID').val();
	var session = "<%= session.getId() %>";
	if ($('#registerID').val().length == 0) {
	      $('#warning-text').show();
	}else{
		$('#registerPlayer').hide();
		$('#main-game').show(500);
		$('#side-b').show(500);
	}


	CatchItServer.registerPlayer(session, name);
}
	
function endGame(theName)
{
	makeTable();
	clearTimeout(quitterTime); 
	$('#main-game').hide();
	$('#winner-game').show(500);
	document.getElementById('winnerName').innerText = "The Winner is " + theName + "";	
}

// Functionto reser the game in the server
function resetGame()
{
	CatchItServer.resetGame();
}

//Functionto reser the game in the client
function restartGame(){
	$('#main-game').hide();
	$('#winner-game').hide();
	$('#quitter-game').hide();
	$('#registerPlayer').show(500);
	document.getElementById('myTime').innerHTML = "";
	document.getElementById('PlayerList').innerHTML = "";
	document.getElementById('resultData').innerHTML = "";
	beginTime = 0; // Initial time for catching the ball
	round = 0; // local value for round
	diffTime = 0; // 
	clearTimeout(quitterTime); 
	window.location.reload(true);
}

// Function caller if a cliente quits the game
function quitter()
{
	makeTable();
	$('#main-game').hide();
	$('#quitter-game').show(500);
	//document.getElementById('quitter').innerText = "We have a Quitter";	
}
</script>

</head>
<body onload="dwr.engine.setActiveReverseAjax(true); makeTable();">
	<div id="wrapper1">
	<div id="header">
		<div>
			<a href="#"><img id='banner' src='images/logo-small.png'/></a>
		</div>
	</div>
	<div id="container">
		<div id="content">
			<div id="registerPlayer">
				<div id="warning-text"><i>Name field is Empty. Please enter a name.</i></div>
				<div><input type="text" class="submissionfield" name="register" id="registerID" value="" /></div>
				<div><button id="register-button" onclick="registerPlayer();"></button></div>
			</div>
		<div id="main-game">
			<div id="BigTable2" class="seira">
			</div>
			<div id="startbutton">
				<button id="start" class="start1" disabled="disabled" onClick="startGame();">Waiting Players</button>
			</div>
		</div>
		<div id="winner-game">
			<div>
				<img id='winner' src='images/winner.png'/>
			</div>
			<div>
				<label id="winnerName"></label> 
			</div>
		</div>
		<div id="quitter-game">
			<div>
			<img id='quitter' src='images/quitter.png'/>
			</div>
		</div>
		
		</div>
		<div id="side-b">
			<div id="roundbox">
				<div>&nbsp;&nbsp;Score Table</div>
				<div id="PlayerList" style="text-align: left;">
				</div>
			</div>
			<br /><br />
			<div id="roundbox2" style="text-align: left;">
				<div>&nbsp;&nbsp;Last round results</div>
				<div id="Console">
					<div id="resultData"></div>
					<div id="myTime"></div>
				</div>
			</div>
		</div>
		<div id="footer">
			<img src="images/rug.png" /><br />
			developed by <i>K.Tselios, M.Martiarena, K.Theodorou.</i><br />
			powered by:<br />
			<img src="images/Java_logo.png" />&nbsp;&nbsp;
			<img src="images/tomcat.png" />&nbsp;&nbsp;
			<img src="images/mongodb_logo.png" />
			<div><button id="reset" onClick="resetGame();">ResetGame</button></div>

		</div>
	</div>
</body>
</html>