package org.lappsgrid.gate.core;

/**
 *
 */
public class GateCoreException extends Exception
{
	public GateCoreException()
	{

	}

	public GateCoreException(String message)
	{
		super(message);
	}

	public GateCoreException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GateCoreException(Throwable cause)
	{
		super(cause);
	}

	public GateCoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
