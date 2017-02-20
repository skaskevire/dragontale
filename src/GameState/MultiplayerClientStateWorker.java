package GameState;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hypefiend.javagamebook.common.GameEvent;
import com.hypefiend.javagamebook.common.GameEventDefault;
import com.hypefiend.javagamebook.common.NIOUtils;

public class MultiplayerClientStateWorker implements Runnable
{
	private MultiplayerClientState mcs;
	private boolean running;
	
	public boolean isRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	public MultiplayerClientStateWorker(MultiplayerClientState mcs)
	{
		this.mcs = mcs;
	}

	@Override
	public void run()
	{
		running = true;
		while(running)
		{
			try
			{
				processIncomingEvents();
				writeOutgoingEvents();
				Thread.sleep(10l);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		
	}
	
	
	 protected void processIncomingEvents() {
			GameEvent inEvent;
			while (mcs.inQueue.size() > 0) {
			    try {
				inEvent = mcs.inQueue.deQueue();
				if(inEvent != null)
				{
					
				
				switch (inEvent.getType()) {
				case GameEventDefault.S_UPDATE_PLAYER_COORDINATES:
					mcs.updatePlayerCoordinates(inEvent.getMessage());
				    break;
				case GameEventDefault.S_UPDATE_ENEMY_COORDINATES:
//					System.out.println(inEvent.getMessage());
					mcs.addOrUpdateEnemies(inEvent.getMessage());
				    break;
				case GameEventDefault.S_LOGIN_ACK_OK:
				    break;
				case GameEventDefault.SB_LOGIN:
				    stdOut( "login: " + inEvent.getMessage());
				    break;
				case GameEventDefault.SB_LOGOUT:
				    stdOut( "logout: " + inEvent.getMessage());
				    break;
				case GameEventDefault.SB_CHAT_MSG:
				    stdOut( inEvent.getPlayerId() + ": " + inEvent.getMessage());
				    break;
				case GameEventDefault.S_DISCONNECT:
				    stdErr( "disconnected from server: " + inEvent.getMessage());
				    shutdown();
				    break;
				case GameEventDefault.S_JOIN_GAME_ACK_OK:
				    stdOut( inEvent.getMessage());
				    mcs.inGame = true;
				    break;
				case GameEventDefault.S_JOIN_GAME_ACK_FAIL:
				    stdOut( inEvent.getMessage());
				    mcs.inGame = false;
				    break;
				case GameEventDefault.SB_PLAYER_QUIT:
					mcs.removePlayer(inEvent.getMessage());
				    stdOut( inEvent.getMessage());
				    break;		    
				default:
				    stdOut( inEvent.getMessage());
				    break;
				}
				}
			    }
			    catch (InterruptedException ie) {
			    	ie.printStackTrace();
			    }
			}
		    }
	 
	    /**
	     * shutdown the client
	     * stop our readers and close the channel
	     */
	    protected void shutdown() {
		//running = false;
	    	mcs.netReader.shutdown();
		//	consoleReader.shutdown();
		try {
			mcs.channel.close();
		}
		catch (IOException ioe) {
		  //  log.error("exception while closing channel", ioe);
		}
	    }    
	 
	    public void stdOut(String str) {
		if ((str != null) && !str.equals(""))
		    System.out.println("\n" + str);
		if (mcs.inGame)
		    System.out.print( mcs.playerId + " vs. " + mcs.opponentId + " > " );
		else
		    System.out.print( mcs.playerId + " > " );

	    }   
	    
	    public void stdErr(String str) {
		System.err.println("\n" + str);
	    }
	    
	    public void setOpponent(String opp) {
	    	mcs.opponentId = opp;
	    }
	
	 private void writeOutgoingEvents() {
			GameEvent outEvent;
			while (mcs.outQueue.size() > 0) {
			    try {
				outEvent = mcs.outQueue.deQueue();
				//System.out.println(outEvent.getMessage());
				
				writeEvent(outEvent);
			    }
			    catch (InterruptedException ie) {
			    	
			    	
			    	ie.printStackTrace();
			    }
			}	
		    }
	
	 
	 /** 
	     * send an event to the server 
	     */
	    protected void writeEvent(GameEvent ge) {
		// set the gamename and player id
	    	ByteBuffer writeBuffer = ByteBuffer.allocateDirect(5000);
	    	
	    	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	    	ge.setGameName("RPS");
	    	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		ge.setPlayerId(mcs.playerId);

		NIOUtils.prepBuffer(ge, writeBuffer);
		NIOUtils.channelWrite(mcs.channel, writeBuffer);
	    }

}
