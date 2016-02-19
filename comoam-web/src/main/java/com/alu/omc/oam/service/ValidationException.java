package com.alu.omc.oam.service;

public class ValidationException extends Exception
{
   /**
      * @Fields serialVersionUID 
      */
    private static final long serialVersionUID = 9078893898218937048L;

public ValidationException(String message, Throwable throwable){
       super(message, throwable);
   }
}
