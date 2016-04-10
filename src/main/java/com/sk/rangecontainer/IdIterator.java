package com.sk.rangecontainer;

import java.util.Collections;
import java.util.Iterator;

/**
 * Iterator for collection of Ids. 
 * @author Sheetal Kanade
 *
 */
public class IdIterator implements Ids {
	final Iterator<Short> ids;
	
	public IdIterator(Iterable<Short>ids) {
		if(ids != null) {
			this.ids = ids.iterator();
		} else {
			this.ids = Collections.emptyIterator(); // new ArrayList<Short>().iterator();
		}
		
	}
	
	@Override
	public short nextId()
	{
		return (ids.hasNext())? ids.next() : END_OF_IDS;
	}
	
}