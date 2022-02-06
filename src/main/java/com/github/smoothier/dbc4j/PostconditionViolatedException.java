package com.github.smoothier.dbc4j;

public class PostconditionViolatedException extends ContractViolationException {
	
	public PostconditionViolatedException(final IllegalStateException e) {
		super("", e);
	}
	
	public PostconditionViolatedException(final IllegalArgumentException e) {
		super("", e);
	}
}
