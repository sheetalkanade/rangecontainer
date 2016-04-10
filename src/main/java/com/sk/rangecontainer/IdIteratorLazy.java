package com.sk.rangecontainer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Iterator for collection of Ids. 
 * @author Sheetal Kanade
 *
 */
public class IdIteratorLazy implements Ids {
	private List<Short> ids;
	
	public IdIteratorLazy(List<Short> ids) {
		if(ids != null) {
			this.ids = ids;
		} else {
			this.ids = Collections.emptyList();
		}
		
	}
	
	@Override
	public short nextId()
	{
		if(Objects.isNull(ids) || ids.isEmpty()) 
				return END_OF_IDS;
		return ids.remove(getNextId());
	}
	
	private int getNextId() {
		return IntStream.range(0, ids.size())
		         .reduce((i,j) -> ids.get(i) < ids.get(j)? i : j)
		         .getAsInt();
		         
	}

}