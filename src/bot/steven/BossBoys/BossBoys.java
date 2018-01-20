package bot.steven.BossBoys;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;

import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import bot.steven.ChatCommands.ChatCommander.CommandStates;

/*
 * BossBoys:
 * living the dream. a fully automated bot with interface to take , receive, and command Jug interaction.
 */
@ScriptManifest(author = "Steven Ventura", info = "My Water Boys", logo = "", name = "BossBoys", version = 0)
public class BossBoys extends Script{
	
	enum MASTERSTATES {
		Idle,
		Travelling,
		Distributing,
		Collecting,
		
	};
	
	//TODO: in the UI, prevent him from distributing and collecting if he is not in falador.
	enum DISTRIBUTING {
		PopulatingCustomerArray,
		 SendingTradeRequestToEach,
		 Done
	}
	DISTRIBUTING distributingState;
	
	enum COLLECTING {
		PopulatingCustomerArray,
		 SendingTradeRequestToEach,
		 Done
	}
	
	COLLECTING collectingState;
	
	MASTERSTATES master = MASTERSTATES.Idle;
	
	class Travelling {
		
		TravelNode currentDestination;
		
		class TravelNode {
			TravelNode towardsFalador=null, towardsGE=null;
			private int x, y;
			boolean finalDestination;
			public TravelNode(int x, int y) {
				this.x=x;this.y=y;
				finalDestination=false;
			}
		}
		CITY destination;
		//TODO: put things in path thing manually. maybe use arraylist idk whatever works for easiest inputing w helper function
		public Travelling(CITY destination) {
			this.destination=destination;
			defineTree();
		}
		private void defineTree() {
			//TODO: populate the tree so its usable
			
		}
		
		
	}
	
	Travelling traveller = null;
	
	TreeMap<String,JugPlayer> playerboys;
	
	
	Stack<String> tradeboys;
	
	private void travelToCity(CITY city) {
		traveller = new Travelling(city);
		master = MASTERSTATES.Travelling;
		
		
	}
	enum CITY {
		Falador,
		GrandExchange
	};
	
	//the city of this bot
	enum CURRENTCITY {
		Unknown,
		Falador,
		GrandExchange,
		Transit
	}
	CURRENTCITY currentCity = CURRENTCITY.Unknown;
	
	private void printPlayerDataToFile() {
		//TODO: location,numEmptyJugs,numFullJugs
		
		try{
			File f = new File(getDirectoryData() + getParameters() + ".bossData");
			
			PrintWriter p = new PrintWriter(f);
			p.println(""+new Date());
			p.println(myPlayer().getName());
			//DateFormat.parse( );
			p.println(""+myPlayer().getX());
			p.println(""+myPlayer().getY());
			p.println(""+numEmptyJugs);
			p.println(""+numFullJugs);
			p.close();
			
			
		}catch(Exception e) {e.printStackTrace();}
		
	}
	
	public void onMessage(Message message)
	{
		final int CLANCHAT = 9, WHISPER = 3;
		String text = message.getMessage();
		
		//the game messages
		if (text.contains("wishes to trade with")) {
			String name = text.split(" ")[0];
			//dont allow duplicates
			if (!tradeboys.contains(name)) {
				if ()
				
				
			}
			
		}
		
		//the whisper commands
		if (message.getTypeId() == CLANCHAT 
				|| message.getTypeId() == WHISPER) {
			
			String phrase = text.split(" ")[0];
			
			if (phrase == null)
				return;
			
			switch (phrase) {
			case "Travelfalador":
				break;
			case "Travelge":
				
				break;
			}
			
			
			
		}
		
		
		
		
	}
	@Override
	public void onExit() {
		
	}
	
	public void onPaint(Graphics2D g) {
		g.setPaint(Color.ORANGE);
		
		switch (master) {
		case Collecting:
			g.drawString("JugBoy=" + collectingState,10,60);
			break;
		case Distributing:
			g.drawString("JugBoy=" + distributingState,10,60);
			break;
		default:
			g.drawString("JugBoy=" + master,10,60);
			break;
		
		}
		
		g.drawString(""+numEmptyJugs,10,80);
		g.drawString(""+numFullJugs,10,100);
		
	}
	int numEmptyJugs=-1,numFullJugs=-1;
	private long CT=System.currentTimeMillis(),LT=System.currentTimeMillis();
	@Override
	public int onLoop() throws InterruptedException {
		CT = System.currentTimeMillis();
		if (CT - LT > 30 * 1000) {
			LT = CT;
			printPlayerDataToFile();
		}
		
		switch (master) {
		case Idle:
			//do nothing
			break;
		case Travelling:
			//TODO: travel to next node until done
			//TODO: output the done signal
			break;
		case Distributing:
			stateMachineDistributing();
			break;
		case Collecting:
			stateMachineCollecting();
			break;
		
		
		}
		
		
		return 150;
	}
	
	class JugPlayer {
		
		public JugPlayer(File f) {
			try{
			Scanner scan = new Scanner(f);
			this.postTime = DateFormat.getDateInstance().parse(scan.nextLine());
			this.name = scan.nextLine();
			this.x = Integer.parseInt(scan.nextLine());
			this.y = Integer.parseInt(scan.nextLine());
			this.numEmptyJugs = Integer.parseInt(scan.nextLine());
			this.numFullJugs = Integer.parseInt(scan.nextLine());
			scan.close();
			donetrading = false;
			}catch(Exception e) {e.printStackTrace();}
		}
		public void setTradeWith(boolean trademe) {
			
		}
		boolean donetrading = false;
		String name;
		Date postTime;
		int x,y;
		int numEmptyJugs;
		int numFullJugs;
		
	}
	
	private void populateboys() {
		//TODO: add criteria, like they only trade if they have above the threshold of items.
		try{
			File dir = new File(".");
			File [] files = dir.listFiles(new FilenameFilter() {
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".jugData");
			    }
			});

			for (File f : files) {
			    JugPlayer p = new JugPlayer(f);
			    if (Math.abs(new Date().getTime() - p.postTime.getTime() )
			    		< 50*1000) {
			    	log("detected file," + f.getName() + ",created within the last 50 seconds.");
			    	playerboys.put(p.name, p);
			    }
			}
		}catch(Exception e){e.printStackTrace();}

	}
	private boolean WaitForWidgetToDisappear (int arg1, int arg2)
	{
		int loops = 0;
		while (widgets.get(arg1,arg2) != null || widgets.get(arg1,arg2).isVisible()) {
			loops++;
			if (loops > 80)
				return false;
			rsleep(100);
		}
		return true;
	}
	private boolean WaitForWidget (int arg1, int arg2)
	{
		int loops = 0;
		while (widgets.get(arg1,arg2) == null || !widgets.get(arg1,arg2).isVisible()) {
			loops++;
			if (loops > 80)
				return false;
			rsleep(100);
		}
		return true;
	}
	private boolean WaitForWidget (int arg1, int arg2, int arg3)
	{
		int loops = 0;
		while (widgets.get(arg1,arg2,arg3) == null || !widgets.get(arg1,arg2,arg3).isVisible()) {
			loops++;
			if (loops > 80)
				return false;
			rsleep(100);
		}
		return true;
	}
	private void rsleep(long millis)
	{
		try{
			Thread.sleep(millis);
		}catch(Exception e){};
	}
	final boolean LEFTCLICK = false, RIGHTCLICK = true;
	
	
	private void click(int x, int y)
	{
		mouse.move(x,y);
		mouse.click(LEFTCLICK);
	}
	private void rightclick(int x, int y)
	{
		mouse.move(x,y);
		mouse.click(RIGHTCLICK);
	}
	
	String currentTradeBoy = null;
	private void stateMachineCollecting() {
		
		switch(collectingState) {
		case PopulatingCustomerArray:
			//TODO: read from file to see who has stuff ready for me right now
			playerboys = new TreeMap<>();
			tradeboys = new Stack<>();//this stack is populated in the onMessage() thing
			currentTradeBoy = null;
			populateboys();
			collectingState = COLLECTING.SendingTradeRequestToEach;
			break;
		case SendingTradeRequestToEach:
			//TODO: cycle through each person and go through with trade request
			if (!tradeboys.isEmpty()) {
				if (currentTradeBoy == null)
					currentTradeBoy = tradeboys.pop();
			}
			else
			{
				collectingState = COLLECTING.Done;
				break;
			}
			
			//TODO: put this new version of widget handling in jugboys
			Entity tradeboy = players.closest(currentTradeBoy);
			if (tradeboy != null)
				{
					if ( (widgets.get(335,25) == null || widgets.get(335,25).isVisible() == false)
							&& 
						 (widgets.get(334,13) == null || widgets.get(334,13).isVisible() == false)) {
				tradeboy.interact("Trade with");
				rsleep(1000);
					}
				
				//trade screen 1 widget is 335,25
					//trade screen 2 widget is 334,13
				if  ((widgets.get(334,13) != null && widgets.get(334,13).isVisible())
						|| WaitForWidget(335,25)) {
					//wait a second for him to put the goods up
					rsleep(2000);
					//press accept
					click(264,180);
					//wait for him to accept
					if (WaitForWidget(334,13)) {
					//press accept again
					click(215,303);
					//successful trade!
					currentTradeBoy = null;
					}
				}
			}
			
			
			
			
			
			break;
			
		
		case Done:
				master = MASTERSTATES.Idle;
			break;
		}
		
		
	}
	private void stateMachineDistributing() {
		//TODO: add the state where i put the jugs in my inventory and then bring them to falador.
		switch(distributingState) {
		case PopulatingCustomerArray:
			playerboys = new TreeMap<>();
			tradeboys = new Stack<>();//this stack is populated in the onMessage() thing
			currentTradeBoy = null;
			populateboys();
			
			//TODO: read from file to see who is available , and who needs jugs, and how many to give
			distributingState = DISTRIBUTING.SendingTradeRequestToEach;
			break;
		case SendingTradeRequestToEach:
			//TODO: cycle through each person and go through with trade request
			if (!tradeboys.isEmpty()) {
				if (currentTradeBoy == null)
					currentTradeBoy = tradeboys.pop();
			}
			else
			{
				distributingState = DISTRIBUTING.Done;
				break;
			}
			
			//TODO: put this new version of widget handling in jugboys
			Entity tradeboy = players.closest(currentTradeBoy);
			if (tradeboy != null)
				{
					//this if statement is here to skip ahead if he is already in another window
					if ( (widgets.get(335,25) == null || widgets.get(335,25).isVisible() == false)
							&& 
						 (widgets.get(334,13) == null || widgets.get(334,13).isVisible() == false)) {
				tradeboy.interact("Trade with");
				rsleep(1000);
					}
				
				//trade screen 1 widget is 335,25
					//trade screen 2 widget is 334,13
				if  ((widgets.get(334,13) != null && widgets.get(334,13).isVisible())
						|| WaitForWidget(335,25)) {
					//offer my item
					inventory.interact("Offer-All", "Jug");
					//press accept
					click(264,180);
					//wait for him to accept
					if (WaitForWidget(334,13)) {
					//press accept again
					click(215,303);
					//successful trade!
					currentTradeBoy = null;
					}
				}
			}
			
			break;
		case Done:
				master = MASTERSTATES.Idle;
			break;
		}
		
		
	}
	
	
	
	
	
}
