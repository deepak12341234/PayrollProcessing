package com.PayrollProcessingSystem.Exception;
public class InvalidFileFormatException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public InvalidFileFormatException(String message) {
        super(message);
    }
}