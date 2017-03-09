package com.hypefiend.javagamebook.common;

import java.net.SocketOption;
import java.nio.*;
import java.nio.channels.*;

/**
 * NIOUtils.java
 *
 * Misc utility functions to simplify dealing w/NIO channels and buffers
 *
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class NIOUtils {

    /** 
     * first, writes the header, then the 
     * event into the given ByteBuffer
     * in preparation for the channel write
     */
    public static void prepBuffer(GameEvent event, ByteBuffer writeBuffer) {
	// write header
	writeBuffer.clear();
	writeBuffer.putInt(0); // todo: clientId
	if (event.getGameName() != null)
	    writeBuffer.putInt(event.getGameName().hashCode()); 
	else
	    writeBuffer.putInt(0); 
	int sizePos = writeBuffer.position();
	writeBuffer.putInt(0);// placeholder for payload size
	// write event
	try
	{

		int payloadSize = event.write(writeBuffer);
		// insert the payload size in the placeholder spot 
		writeBuffer.putInt(sizePos, payloadSize); 
		
		// prepare for a channel.write
		writeBuffer.flip(); 
	}
	catch(BufferOverflowException e)
	{
		e.printStackTrace();
	}
	

	
    }
    
    /** 
     * write the contents of a ByteBuffer to the given SocketChannel
     */
    public static void channelWrite(SocketChannel channel, ByteBuffer writeBuffer, Selector selector) {
    	//System.out.println(channel.isBlocking());
	long nbytes = 0;
	long toWrite = writeBuffer.remaining();
	
	
	int count = 0;

	// loop on the channel.write() call since it will not necessarily
	// write all bytes in one shot
	try {
		System.out.println(toWrite);
	    while (nbytes != toWrite) {
	    	
	    /*	if(toWrite > 100)
	    	{

	    			System.out.println("Error!");
	    			System.out.println(count);
	    			System.exit(1);
	    	}*/
	    	int bytesWritten = channel.write(writeBuffer);
	    	if(bytesWritten == 0)
	    	{
	    		System.out.println("Error");
	    		return;
	    	}
	    	nbytes += bytesWritten;
		System.out.println(nbytes + " count " + count + " " + Thread.currentThread().getName());
		System.out.println(channel.socket().getReceiveBufferSize());
		count++;
		
		try {
		    Thread.sleep(1l);
		}
		catch (InterruptedException e) {
			
			System.out.println(e);
			System.exit(1);
		}
	    }
	}
	catch (ClosedChannelException cce) {
		System.out.println(cce);
		System.exit(1);
	}
	catch (Exception e) {
		System.out.println(e);
		System.exit(1);
	} 
	
	// get ready for another write if needed
	writeBuffer.rewind();
    }

    /**
     * write a String to a ByteBuffer, 
     * prepended with a short integer representing the length of the String
     */
    public static void putStr(ByteBuffer buff, String str) {
	if (str == null) {
	    buff.putShort((short)0);
	}
	else {
	    buff.putShort((short)str.length());
	    buff.put(str.getBytes());
	}
    }

    /**
     * read a String from a ByteBuffer 
     * that was written w/the putStr method
     */
    public static String getStr(ByteBuffer buff) {
	short len = buff.getShort();
	if (len == 0) {
	    return null;
	}
	else {
	    byte[] b = new byte[len];
	    buff.get(b);
	    return new String(b);
	}
    }


}
