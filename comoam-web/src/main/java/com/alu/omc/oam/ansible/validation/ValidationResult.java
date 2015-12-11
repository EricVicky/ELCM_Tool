package com.alu.omc.oam.ansible.validation;

import java.io.Serializable;

public class ValidationResult implements Serializable{
/**
	 * 
	 */
private static final long serialVersionUID = -743014706041617274L;
boolean succeed = true;
boolean exist = true;
String message;
public boolean isSucceed() {
	return succeed;
}
public void setSucceed(boolean succeed) {
	this.succeed = succeed;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}

public boolean isExist() {
	return exist;
}
public void setExist(boolean exist) {
	this.exist = exist;
}
public ValidationResult(){
	
}

public ValidationResult(boolean succeed, String message){
    this.succeed = succeed;
    this.message = message;
    this.exist = exist;
}
}
