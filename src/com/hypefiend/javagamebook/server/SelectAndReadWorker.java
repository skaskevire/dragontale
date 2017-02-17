package com.hypefiend.javagamebook.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hypefiend.javagamebook.common.Attachment;
import com.hypefiend.javagamebook.common.GameEvent;
import com.hypefiend.javagamebook.common.Globals;
import com.hypefiend.javagamebook.common.Player;
import com.hypefiend.javagamebook.server.controller.GameController;

public class SelectAndReadWorker implements Runnable
{
	private static Logger log = Logger.getLogger("SelectAndReadWorker");

	private SocketChannel channel;
	private Selector selector;
	 private GameServer gameServer;
	
	
	
	SelectAndReadWorker(SocketChannel channel, GameServer gameServer)
	{
		this.channel = channel;
		this.gameServer = gameServer;
		try
		{
			this.channel.configureBlocking( false);
			selector = Selector.open();
			this.channel.register( selector, SelectionKey.OP_READ, new Attachment());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	
	@Override
	public void run()
	{
		 while (true) {
				select();
				// sleep just a bit
				try { Thread.sleep(Globals.CHANNEL_WRITE_SLEEP); } catch (InterruptedException e) {}
			    }
	
		
	}

private void select() {
	try {
	    // this is a blocking select call but will 
	    // be interrupted when new clients come in
	    selector.select();
	    Set readyKeys = selector.selectedKeys();

	    Iterator i = readyKeys.iterator();
	    while (i.hasNext()) {
		SelectionKey key = (SelectionKey) i.next();
		i.remove();
		SocketChannel channel = (SocketChannel) key.channel();
		Attachment attachment = (Attachment) key.attachment();

		try {
		// read from the channel
		    long nbytes = channel.read(attachment.readBuff);
		    // check for end-of-stream condition
		    if (nbytes == -1) {
			log.info("disconnect: " + channel.socket().getInetAddress() + 
				 ", end-of-stream");
			channel.close();
		    }

		    // check for a complete event
		    try {
			if (attachment.readBuff.position() >= attachment.HEADER_SIZE) {
			    attachment.readBuff.flip();
			    
			    // read as many events as are available in the buffer
			    while(attachment.eventReady()) {
				GameEvent event = getEvent(attachment);
				delegateEvent(event, channel);
				attachment.reset();
			    }
			    // prepare for more channel reading
			    attachment.readBuff.compact();
			}
		    }
		    catch (IllegalArgumentException e) {
			log.error("illegal argument exception", e);
		    }
		}
		catch (IOException ioe) {
		    log.warn("IOException during read(), closing channel:" + channel.socket().getInetAddress());
		    channel.close();
		}
	    }
	}
	catch (IOException ioe2) {
	    log.warn("IOException during select(): " + ioe2.getMessage());
	}
	catch (Exception e) {
	    log.error("exception during select()", e);
	}
   }

private void delegateEvent(GameEvent event, SocketChannel channel) {
	if (event != null && event.getGameName() == null) {
	    log.error("GameServer.handleEvent() : gameName is null");
	    return;
	}

	GameController gc = gameServer.getGameController(event.getGameName());
	if (gc == null) {
	    log.error("No GameController for gameName: " + event.getGameName());
	    return;
	}

	Player p = gameServer.getPlayerById(event.getPlayerId());
	if (p != null) {
	    if (p.getChannel() != channel) {
		log.warn("player is on a new channel, must be reconnect.");
		p.setChannel(channel);
	    }
	}
	else {
	    // first time we see a playerId, create the Player object
	    // and populate the channel, and also add to our lists
	    p = gc.createPlayer();
	    p.setPlayerId(event.getPlayerId());
	    p.setChannel(channel);
	    gameServer.addPlayer(p);
	    log.debug("delegate event, new player created and channel set, player:" + 
		      p.getPlayerId() + ", channel: " + channel);
	}	
	
	gc.handleEvent(event);
  }

private GameEvent getEvent(Attachment attachment) {
	GameEvent event = null;
	ByteBuffer bb = ByteBuffer.wrap(attachment.payload);

	// get the controller and tell it to instantiate an event for us
	GameController gc = gameServer.getGameControllerByHash(attachment.gameNameHash);
	if (gc == null) {
	    return null;
	}
	event = gc.createGameEvent();
	
	// read the event from the payload
	event.read(bb);	
	return event;
   }  

}
