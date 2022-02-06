package com.github.smoothier.dbc4j;

public abstract class ContractViolationException extends RuntimeException {
	
	protected ContractViolationException() {
		super();
	}
	
	protected ContractViolationException(final String msg) {
		super(msg);
	}
	
	protected ContractViolationException(final Exception cause) {
		super(cause);
	}
	
	public ContractViolationException(final String msg, final Exception cause) {
		super(msg, cause);
	}
}
