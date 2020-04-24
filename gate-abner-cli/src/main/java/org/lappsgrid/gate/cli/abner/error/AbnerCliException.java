package org.lappsgrid.gate.cli.abner.error;

/**
 *
 */
public class AbnerCliException extends Exception {
	public AbnerCliException() {
	}

	public AbnerCliException(String message) {
		super(message);
	}

	public AbnerCliException(String message, Throwable cause) {
		super(message, cause);
	}

	public AbnerCliException(Throwable cause) {
		super(cause);
	}

	public AbnerCliException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
