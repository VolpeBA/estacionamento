package com.volpe.exception;

public class SectorFullException extends RuntimeException {

	public SectorFullException(final String sector) {
		super("Sector '" + sector + "' is at full capacity");
	}
}
