package com.tinyrssreader.storage.internal;

public class StorageParamsBuilder {
	public static StorageParams buildHasInFile(String sessionId) {
		return new StorageParams().setSessId(sessionId);
	}
	
	public static StorageParams buildHasPosInFile(String sessionId){
		return new StorageParams().setSessId(sessionId);
	}

	public static StorageParams buildSavePos(String sessionId, int pos) {
		return new StorageParams().setSessId(sessionId).setPos(pos);
	}
}
