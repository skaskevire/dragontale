package com.hypefiend.javagamebook.server;

import com.hypefiend.javagamebook.common.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

/**
 * EventWriter.java
 *
 * 
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class EventWriter extends Wrap {
    /** reference to the GameServer */
    private static GameServer gameServer;
    private Selector selector;
    
    /** 
     * contructor.
     */
    public EventWriter(GameServer gameServer, int numWorkers) {
	this.gameServer = gameServer;
	selector = gameServer.getSelectAndRead().getSelector();
	initWrap(numWorkers);
    }

    /** 
     * note we override the Wrap's run method here
     * doing essentially the same thing, but 
     * first we allocate a ByteBuffer for this
     * thread to use
     */
    public void run() {
    	ByteBuffer writeBuffer = ByteBuffer.allocateDirect(Globals.MAX_EVENT_SIZE);

	
	running = true;
	while (running) {
		GameEvent event = null;

		try
		{
			if ((event = eventQueue.deQueue()) != null) {
				
				
				
        		if (event.getRecipients() != null)
    			{
    				// System.out.println(event.getRecipients().length);
    			}
    			
    		    processEvent(event, writeBuffer, selector);
				
				
				
				
				/*ExecutorService executor = Executors.newSingleThreadExecutor();
				Future<Void> future = executor.submit(new Task(event, writeBuffer));
				future.get(100, TimeUnit.MILLISECONDS);
				executor.shutdownNow();*/
				
				
				
			}
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TimeoutException e)
		{
			e.printStackTrace();
		}*/
		
		
	

	   

	}
    }
    
   /* class Task implements Callable<Void> {
    	private GameEvent gameEvent;
    	private ByteBuffer writeBuffer;
    	
    	public Task(GameEvent gameEvent, ByteBuffer buffer)
    	{
    		this.gameEvent = gameEvent;
    		this.writeBuffer = buffer;
    	}
    	
        @Override
        public Void call() {
        	try
        	{
        		if (gameEvent.getRecipients() != null)
    			{
    				 System.out.println(gameEvent.getRecipients().length);
    			}
    			
    		    processEvent(gameEvent, writeBuffer);
        	} catch (IllegalArgumentException iae)
        	{
        		System.out.println("dasdasd");
        		iae.printStackTrace();
        	}
        	
        	
			return null;
        }
    }
    
    class MyThread extends Thread
    {
    	private GameEvent gameEvent;
    	private ByteBuffer writeBuffer;
    	
    	public MyThread(GameEvent gameEvent)
    	{
    		this.gameEvent = gameEvent;
    		this.writeBuffer = ByteBuffer.allocateDirect(Globals.MAX_EVENT_SIZE);
    	}
    	
    	@Override
    	public void run()
    	{
    		if (gameEvent.getRecipients() != null)
			{
				 System.out.println(gameEvent.getRecipients().length);
			}
			
		    processEvent(gameEvent, writeBuffer);
    	}
    }*/

    /** unused */
    protected void processEvent(GameEvent event) {
    }

    /** 
     * our own version of processEvent that takes 
     * the additional parameter of the writeBuffer 
     */
    protected void processEvent(GameEvent event, ByteBuffer writeBuffer, Selector selector) {
    	
	NIOUtils.prepBuffer(event, writeBuffer);

	
	String[] recipients = event.getRecipients();
	if (recipients == null) {
	    //log.info("writeEvent: type=" + event.getType() + ", id=" + 
		//     event.getPlayerId() + ", msg=" + event.getMessage());
	    String playerId = event.getPlayerId();
	    write(playerId, writeBuffer, selector);
	}
	else {
		for(String recipient : Arrays.asList(recipients))
		{
			if(recipient != null)
			{
				write(recipient, writeBuffer, selector);
			}
		}
	
		
	//	.parallelStream()
	//	.filter(recipient -> recipient != null)
	//	.forEach(recipient -> );
	//	System.out.println("");

	}

	
    }
    
    
    
    /**
     * write the event to the given playerId's channel
     */
    private void write( String playerId, ByteBuffer writeBuffer, Selector selector) {
	Player player = gameServer.getPlayerById(playerId);
	SocketChannel channel = player.getChannel();
	
	
	if (channel == null || !channel.isConnected()) {
	   // log.error("writeEvent: client channel null or not connected");
	    return;
	}
	/*try
	{
		if(selector != null)
		{
			channel.write(writeBuffer);
			
			
		//	channel.register( selector, SelectionKey.OP_READ, new Attachment());
		}
		
		
	}
	catch (ClosedChannelException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	catch (IOException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	catch (Exception e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	*/
	
	NIOUtils.channelWrite(channel, writeBuffer, selector);
	if(selector != null)
	{
		/*try
		{
			channel.register( selector, SelectionKey.OP_READ, new Attachment());
		}
		catch (ClosedChannelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
    }
    
}// EventWriter



