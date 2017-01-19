package com.pinterest.secor.uploader;

public abstract class RetryHandle<T> implements Handle<T>{
	
	private static final int MAX_RETRY = 5;
	
	public T get() throws Exception {
		Exception lastException = null;
		for(int i=0; i<=MAX_RETRY; i++){
			try{
	        	 T ret =  this._get();
	        	 return ret;
	          }catch (Exception e){
	        	  lastException = e;
	          }
		}
		throw lastException;
    }
	
	protected abstract T _get() throws Exception;

}
