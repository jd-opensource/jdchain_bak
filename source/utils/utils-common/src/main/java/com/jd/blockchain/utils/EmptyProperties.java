package com.jd.blockchain.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;

public class EmptyProperties extends Properties {

	private static final long serialVersionUID = 5941797426076447165L;
	
	public static Properties INSTANCE = new EmptyProperties();

	private EmptyProperties() {
	}
	
	@Override
	public String getProperty(String key) {
		return null;
	}
	
	@Override
	public String getProperty(String key, String defaultValue) {
		return defaultValue;
	}
	
	@Override
	public Enumeration<?> propertyNames() {
		return Collections.enumeration(Collections.emptyList());
//		return Collections.emptyEnumeration();
	}
	
	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public synchronized Object get(Object key) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Object> keySet() {
		return Collections.EMPTY_SET;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Object> values() {
		return Collections.EMPTY_SET;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return Collections.EMPTY_SET;
	}
	
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    public void load(Reader reader) throws IOException {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    public Object put(Object key, Object value) {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    public Enumeration<Object> keys() {
    	return Collections.enumeration(Collections.emptyList());
    }
    
    @Override
    public Object setProperty(String key, String value) {
    	throw new UnsupportedOperationException();
    }
    
}
