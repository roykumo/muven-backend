package com.eter.muven.cake.exception;

import java.io.Serializable;

public class BoxRegistrationException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BoxRegistrationException(Throwable throwable){
		super(throwable);
	}

}
