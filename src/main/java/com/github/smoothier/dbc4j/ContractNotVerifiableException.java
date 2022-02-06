package com.github.smoothier.dbc4j;

public class ContractNotVerifiableException extends RuntimeException {
	
	public ContractNotVerifiableException(Exception e) {
		super("", e);
	}
}
