package com.sk.rangecontainer.queryengine.payroll;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sk.rangecontainer.Ids;

/**
 * Iterator for collection of Ids. 
 * @author Sheetal Kanade
 *
 */
public class PayrollResultIterator implements Ids {
	
	private static final Logger log = Logger.getLogger(PayrollResultIterator.class);
	final Iterator<PayrollResult> ids;
	
	public PayrollResultIterator(Iterable<PayrollResult>ids) {
		if(ids != null) {
			long startTime = System.nanoTime();
			this.ids = ids.iterator();
			log.info(String.format("Time taken to get Iterator : %s", (double) (System.nanoTime() - startTime) / 1000000.0));
		} else {
			this.ids = new ArrayList<PayrollResult>(1).iterator();
		}
		
	}
	
	@Override
	public synchronized short nextId()
	{
		return (ids.hasNext())? (short)ids.next().getId() : END_OF_IDS;
	}

}