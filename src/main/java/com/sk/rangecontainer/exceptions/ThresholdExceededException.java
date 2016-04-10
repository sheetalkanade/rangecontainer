package com.sk.rangecontainer.exceptions;

public class ThresholdExceededException extends Exception
{
	private static final long serialVersionUID = 2461969339759657853L;

	public ThresholdExceededException()
	{
		super();
	}
	
	public ThresholdExceededException(String message)
	{
		super(message);
	}
	
	public ThresholdExceededException(Throwable throwable)
	{
		super(throwable);
	}
}
