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
public class IdIteratorLazy2 implements Ids {
	private List<Short> ids;
	private boolean sorted;
	public IdIteratorLazy2(List<Short> ids) {
		if(ids != null) {
			this.ids = ids;
			sorted = false;
		} else {
			this.ids = Collections.emptyList();
			sorted = true;
		}
		
	}
	
	@Override
	public short nextId()
	{
		if(Objects.isNull(ids) || ids.isEmpty()) 
				return END_OF_IDS;
		if(!sorted) {
			ids.sort((p1,p2) -> p1.compareTo(p2));
			sorted = true;
		}
		return ids.remove(getNextId());
	}
	
	private int getNextId() {
		return 0;
	}

}