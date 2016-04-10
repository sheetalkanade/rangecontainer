package com.sk.rangecontainer.queryengine.payroll;

/**
 * Base result object that has the identifier field
 * @author Sheetal Kanade
 *
 */
public class ResultBase implements Result
{
	private long id;
	
	public ResultBase(final long id)
	{
		this.id = id;
	}

	@Override
	public long getId()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultBase other = (ResultBase) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
