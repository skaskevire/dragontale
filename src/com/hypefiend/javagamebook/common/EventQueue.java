package com.hypefiend.javagamebook.common;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected final Lock readLock = rwl.readLock();
	protected final Lock writeLock = rwl.writeLock();
	protected final Condition hasData = writeLock.newCondition();
	
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
    	writeLock.lock();
    	
    	events.addLast(event);
    	hasData.signalAll();
    	
    	writeLock.unlock();
    }

    /** 
     * blocks until an event is available 
     * and then removes and returns the first 
     * available event
     */
    public  GameEvent deQueue() throws InterruptedException {
    	writeLock.lock();
    		while (events.size() == 0) {
    		    count++;
    		  // System.out.println(name + " | " + events.size() + "wait");
    		 //   	    log.debug("waiting, count: " + count);
    		    hasData.await();
    		    count --;
    		}
    		
    		GameEvent e = (GameEvent) events.removeFirst();
    		//System.out.println(name + " | " + events.size() + e.getMessage() + " " + e.getType());
    	
    		writeLock.unlock();
    	return e;
    	
    }

    public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	/**
     * get the current # of events in the queue
     */
    public int size() {

    return events.size();
    		
    		
    }

}
