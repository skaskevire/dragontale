package com.hypefiend.javagamebook.client;

import com.hypefiend.javagamebook.common.*;

import GameState.MultiplayerClientState;

import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.io.*;

import org.apache.log4j.*;

/**
 * NIOEventReader.java
 *
 * Reads GameEvents from the server.
 * 
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class NIOEventReader extends Thread {
    /** log4j logger */
    private Logger log = Logger.getLogger(NIOEventReader.class);

    /** reference to the game client */
    MultiplayerClientState gamePanel;

    /** connection to server */
    private SocketChannel channel;
    
    /** queue for incoming events */
    private EventQueue queue;

    /** selector to manage server connection */
    private Selector selector;
    
    /** still running? */
    private  boolean running;
 
    public boolean isRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	/** 
     * constructor.
     */
    public NIOEventReader(MultiplayerClientState gc, SocketChannel channel, EventQueue queue) {
	super("NIOEventReader");
	this.gamePanel = gc;
	this.queue = queue;
	this.channel = channel;
    }

    /** 
     * this is nearly identical to the SelectAndRead.select() method
     */ 
    public void run() {
	try {
	    selector = Selector.open();
	    channel.register(selector, SelectionKey.OP_READ, new Attachment());
	}
	catch (ClosedChannelException cce) {
	    log.error("closedchannelexception while registering channel with selector", cce);
	    return;
	}
	catch (IOException ioe) {
	    log.error("ioexception while registering channel with selector", ioe);
	    return;
	}

	running = true;
	while (running) {
	    try {
		selector.select();
		Set<SelectionKey> readyKeys = selector.selectedKeys();
		readyKeys.parallelStream().forEach(this::p);

	    }
	    catch (IOException ioe2) {
		log.warn("error during select(): " + ioe2.getMessage());
	    }
	    catch (Exception e) {
		log.error("exception during select()", e);
	    }
	}
    }
    
    private void p( SelectionKey key)
    {
    	 SocketChannel channel = (SocketChannel) key.channel();
		    Attachment attachment = (Attachment) key.attachment();

		    try {
			long nbytes = channel.read(attachment.readBuff);
			if (nbytes == -1) {
			    channel.close();
			    
			    GameEvent event = gamePanel.createDisconnectEvent("end-of-stream");
			    queue.enQueue(event);
			}
			
			try {
			    if (attachment.readBuff.position() >= attachment.HEADER_SIZE) {
				attachment.readBuff.flip();
				
				while(attachment.eventReady()) {
				GameEvent event = getEvent(attachment);
				queue.enQueue(event);
				attachment.reset();
				}
				attachment.readBuff.compact();
			    }
			}
			catch (IllegalArgumentException e) {
			    log.error("illegalargument while parsing incoming event", e);
			}
		    }
		    catch (IOException ioe) {
			log.warn("IOException during read(), closing channel:" + channel.socket().getInetAddress());
			try
			{
				channel.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
    }

   private GameEvent getEvent(Attachment attachment) {
	GameEvent event = null;
	ByteBuffer bb = ByteBuffer.wrap(attachment.payload);

	// tell the game client to instantiate the event for us
	event = gamePanel.createGameEvent();
	event.read(bb);

	return event;
    }  

    public void shutdown() {
	running = false;
	// force the selector to unblock
	selector.wakeup();
    }
}

