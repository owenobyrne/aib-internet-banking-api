package com.owenobyrne.aibibs.services;


public interface StorageService {

	public void addData(String name, String value, int ttl);
    
	public String getData(String row);
	
	public void deleteData(String row);
    
}
