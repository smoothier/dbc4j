package com.github.smoothier.dbc4j;

public class PreconditionViolatedException extends ContractViolationException {
	
	public PreconditionViolatedException(final IllegalStateException e) {
		super("", e);
	}
	
	public PreconditionViolatedException(final IllegalArgumentException e) {
		super("", e);
	}
}
