package com.github.smoothier.dbc4j;

public class InvariantViolatedException extends ContractViolationException {
	
	public InvariantViolatedException(final IllegalStateException e) {
		super(e);
	}
	
	public InvariantViolatedException(final IllegalArgumentException e) {
		super(e);
	}
}
