package com.hypefiend.javagamebook.common;

import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 * EventQueue.java
 *
 * Blocking queue of GameEvents.
 *
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class EventQueue {
    private Logger log;
    private LinkedList events;
    private int count = 0;
    private String name;

    /** 
     * Constructor.  Initializes the logger and event list
     */
    public EventQueue (String name){
    	this.name = name;
	log = Logger.getLogger("EventQueue: " + name);
	events = new LinkedList();
    }
    
    /** 
     * add an event to the queue
     */
    public void enQueue(GameEvent event) {
    	synchronized(this)
    	{
    		//	log.debug("enQueue " + event.hashCode());
    		events.addLast(event);
    		notifyAll();
    	}


    }

    /** 
     * blocks until an event is available 
     * and then removes and returns the first 
     * available event
     */
    public  GameEvent deQueue() throws InterruptedException {
    	synchronized(this)
    	{
    		while (events.size() == 0) {
    		    count++;
    		    //	    log.debug("waiting, count: " + count);
    		    wait();
    		    count --;
    		}
    		System.out.println(name + " | " + events.size());
    		GameEvent e = (GameEvent) events.removeFirst();
    		return e;
    	}
    }

    /**
     * get the current # of events in the queue
     */
    public int size() {

    return events.size();
    		
    		
    }

}
