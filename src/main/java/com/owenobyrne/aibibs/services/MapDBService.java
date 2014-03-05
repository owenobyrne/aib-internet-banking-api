package com.owenobyrne.aibibs.services;

import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DBMaker;
import org.springframework.stereotype.Service;

@Service
public class MapDBService implements StorageService {
	private ConcurrentNavigableMap<String, String> mapDB;
	
	public MapDBService() {
        System.out.println("Firing up new MapDB in tmp directory...");
        mapDB = DBMaker.newTempTreeMap();
        
    }

	@Override
	public void addData(String name, String value, int ttl) {
		mapDB.put(name, value);
	}

	@Override
	public String getData(String name) {
		return mapDB.get(name);
	}

	@Override
	public void deleteData(String name) {
		mapDB.remove(name);
	}

}
