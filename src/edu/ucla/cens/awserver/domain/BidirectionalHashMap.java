package edu.ucla.cens.awserver.domain;

import java.util.HashMap;
import java.util.Set;

/**
 * Bidirectional hash map for keeping track of a pair of objects and being
 * able to query on either and get the other. This requires that no duplicate
 * keys or values exist in the Map.
 * 
 * This will take time equivalent to that of a standard Java HashMap object,
 * but it will take up twice as much space.
 * 
 * @author John Jenkins
 *
 * @param <K> The type of values to be referenced as keys.
 * 
 * @param <V> The type of values to be referenced as valuse.
 */
public class BidirectionalHashMap<K, V> {
	private HashMap<K, V> _keyToValueMap;
	private HashMap<V, K> _valueToKeyMap;
	
	/**
	 * Default constructor.
	 */
	public BidirectionalHashMap() {
		_keyToValueMap = new HashMap<K, V>();
		_valueToKeyMap = new HashMap<V, K>();
	}

	/**
	 * Clears the map of all keys and values.
	 */
	public void clear() {
		_keyToValueMap.clear();
		_valueToKeyMap.clear();
	}
	
	/**
	 * Returns true if 'key' exists in the map as a key.
	 * 
	 * @param key The value to check for as a key in the map.
	 * 
	 * @return Whether or not 'key' exists in the map as a key.
	 */
	public boolean containsKey(K key) {
		return _keyToValueMap.containsKey(key);
	}
	
	/**
	 * Returns true if 'value' exists in the map as a value.
	 * 
	 * @param value The value to check for as a value in the map.
	 * 
	 * @return Whether or not 'value' exists in the map as a value.
	 */
	public boolean containsValue(V value) {
		return _valueToKeyMap.containsKey(value);
	}
	
	/**
	 * Gets the value associated with 'key' if 'key' exists in the map.
	 * 
	 * @param key They key to search for within the map.
	 * 
	 * @return The value with which 'key' is associated; otherwise, null is
	 * 		   returned.
	 */
	public V getValue(K key) {
		return _keyToValueMap.get(key);
	}
	
	/**
	 * Gets the key associated with 'value' if 'value' exists in the map.
	 * 
	 * @param value They value to search for within the map.
	 * 
	 * @return The key with which 'value' is associated; otherwise, null is
	 * 		   returned.
	 */
	public K getKey(V value) {
		return _valueToKeyMap.get(value);
	}
	
	/**
	 * Creates an association between 'key' and 'value' in the map. If a value
	 * is already associated with 'key', then it is returned. If 'value'
	 * already existed and was associated with another key, that association is
	 * broken and replaced with this new association.
	 * 
	 * Functionally, this is the same as {@link #putValue(Object, Object)},
	 * but it returns only the previous value should one have already existed 
	 * or null if one didn't exist. To retrieve both the old key and the old 
	 * value, it is safer to first use both of the containsValue functions to 
	 * determine if such associations already exist and then to use the 
	 * getValue functions to retrieve those values.
	 *  
	 * @param key The key to be associated with 'value'.
	 * 
	 * @param value The value to be associated with 'key'.
	 * 
	 * @return The value with which 'key' was already associated or null if no
	 * 		   such association already existed.
	 */
	public V putKey(K key, V value) {
		K oldKey = _valueToKeyMap.put(value, key);
		
		if(oldKey != null) {
			_keyToValueMap.remove(oldKey);
		}
		
		V oldValue = _keyToValueMap.put(key, value);
		
		return oldValue;
	}
	
	/**
	 * Creates an association between 'key' and 'value' in the map. If a key
	 * is already associated with 'value', then it is returned. If 'key'
	 * already existed and was associated with another value, that association
	 * is broken and replaced with this new association.
	 * 
	 * Functionally, this is the same as {@link #putKey(Object, Object)},
	 * but it returns only the previous key should one have already existed, 
	 * or null if one didn't exist. To retrieve both the old key and the old 
	 * value, it is safer to first use both of the containsValue functions to 
	 * determine if such associations already exist and then to use the 
	 * getValue functions to retrieve those values.
	 *  
	 * @param key The key to be associated with 'value'.
	 * 
	 * @param value The value to be associated with 'key'.
	 * 
	 * @return The key with which 'value' was already associated or null if no
	 * 		   no such association already existed.
	 */
	public K putValue(K key, V value) {
		V oldValue = _keyToValueMap.put(key, value);
		
		if(oldValue != null) {
			_valueToKeyMap.remove(oldValue);
		}
		
		K oldKey = _valueToKeyMap.put(value, key);
		
		return oldKey;
	}
	
	/**
	 * Returns a Set of all the known keys.
	 * 
	 * @return All of the known keys.
	 */
	public Set<K> keySet() {
		return _keyToValueMap.keySet();
	}
	
	/**
	 * Returns a Set of all the known values.
	 * 
	 * @return All of the known values.
	 */
	public Set<V> valueSet() {
		return _valueToKeyMap.keySet();
	}
}
