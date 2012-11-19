package de.tudresden.inf.rn.mobilis.xmpp.beans.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Class DoubleKeyMap is like a normal HashMap with two keys.
 * 
 * @param <K>
 *            the key type
 * @param <SK>
 *            the second key type
 * @param <V>
 *            the value type
 * 
 *            e.g. namespace, version, MobilisServiceContainer
 */
public class DoubleKeyMap< K, SK, V > {

	/** The inner map structure. */
	private Map< K, Map< SK, V >> _innerDoubleMap;

	/**
	 * Instantiates a new double key map.
	 * 
	 * @param sync
	 *            true if the map should be synchronized for threads
	 */
	public DoubleKeyMap(boolean sync) {
		_innerDoubleMap = sync ? Collections.synchronizedMap( new HashMap< K, Map< SK, V > >() )
				: new HashMap< K, Map< SK, V > >();
	}

	/**
	 * Clears the complete map.
	 */
	public void clear() {
		_innerDoubleMap.clear();
	}

	/**
	 * Checks if the map contains a main key.
	 * 
	 * @param mainKey
	 *            the main key which should be checked for
	 * @return true, if main key is available in map
	 */
	public boolean containsMainKey( K mainKey ) {
		return _innerDoubleMap.containsKey( mainKey );
	}

	/**
	 * Checks if the map contains a main key sub key combination.
	 * 
	 * @param mainKey
	 *            the main key which should be checked for
	 * @param subKey
	 *            the sub key which should be checked for
	 * @return true, if main key sub key combination is available in map
	 */
	public boolean containsSubKey( K mainKey, SK subKey ) {
		return this.containsMainKey( mainKey )
				&& _innerDoubleMap.get( mainKey ).containsKey( subKey );
	}

	/**
	 * Checks if the map contains a value while ignoring the keys
	 * 
	 * @param value
	 *            the value which should be checked
	 * @return true, if the map contains the value
	 */
	public boolean containsValue( V value ) {
		for ( Map< SK, V > subContainer : _innerDoubleMap.values() ) {
			for ( V storedValue : subContainer.values() ) {
				if ( storedValue.equals( value ) ) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the value of a main key sub key combination.
	 * 
	 * @param mainKey
	 *            the main key
	 * @param subKey
	 *            the sub key
	 * @return the value of this combination
	 */
	public V get( K mainKey, SK subKey ) {
		V obj = null;

		if ( this.containsSubKey( mainKey, subKey ) ) {
			obj = _innerDoubleMap.get( mainKey ).get( subKey );
		}

		return obj;
	}

	/**
	 * Gets a list of all values in this map.
	 * 
	 * @return a list of all values
	 */
	public List< V > getListOfAllValues() {
		List< V > valueList = new ArrayList< V >();

		for ( Map< SK, V > subContainer : _innerDoubleMap.values() ) {
			valueList.addAll( subContainer.values() );
		}

		return valueList;
	}

	/**
	 * Gets the raw maps as defined in _innerDoubleMap.
	 * 
	 * @return the raw structure maps
	 */
	public Map< ? extends K, ? extends Map< SK, V >> getRawStructureMaps() {
		return _innerDoubleMap;
	}

	/**
	 * Gets a set of the main keys.
	 * 
	 * @return the main key set
	 */
	public Set< K > getMainKeySet() {
		return _innerDoubleMap.keySet();
	}

	/**
	 * Gets a set of the sub keys.
	 * 
	 * @param mainKey
	 *            the main key of the set of subkeys
	 * @return the sub key set
	 */
	public Set< SK > getSubKeySet( K mainKey ) {
		return this.containsMainKey( mainKey ) ? _innerDoubleMap.get( mainKey ).keySet()
				: new HashSet< SK >();
	}

	/**
	 * Gets a map of sub key value combination of a main key.
	 * 
	 * @param mainKey
	 *            the main key which should be regarded
	 * @return the sub key value map
	 */
	public Map< SK, V > getSubKeyValueMap( K mainKey ) {
		return _innerDoubleMap.get( mainKey );
	}

	/**
	 * Checks if this map is empty.
	 * 
	 * @return true, if it is empty
	 */
	public boolean isEmpty() {
		return _innerDoubleMap.isEmpty();
	}

	/**
	 * Delivers a key set of main key sub key combination.
	 * 
	 * @return the map of keys
	 */
	public Map< K, Set< SK >> keySet() {
		Map< K, Set< SK >> keySet = new HashMap< K, Set< SK > >();

		for ( Map.Entry< K, Map< SK, V > > entry : _innerDoubleMap.entrySet() ) {
			keySet.put( entry.getKey(), entry.getValue().keySet() );
		}

		return keySet;
	}

	/**
	 * Puts a value into the map.
	 * 
	 * @param mainKey
	 *            the main key for this value
	 * @param subKey
	 *            the sub key for this value
	 * @param value
	 *            the value
	 * @return the value
	 */
	public V put( K mainKey, SK subKey, V value ) {
		if ( !_innerDoubleMap.containsKey( mainKey ) ) {
			_innerDoubleMap.put( mainKey, new HashMap< SK, V >() );
		}

		_innerDoubleMap.get( mainKey ).put( subKey, value );

		return this.get( mainKey, subKey );
	}

	/**
	 * Puts a complete DoubleKeyMap into this map.
	 * 
	 * @param dkMap
	 *            the DoubleKeyMap
	 */
	public void putAll( DoubleKeyMap< K, SK, V > dkMap ) {
		_innerDoubleMap.putAll( dkMap.getRawStructureMaps() );
	}

	/**
	 * Removes a value.
	 * 
	 * @param mainKey
	 *            the main key
	 * @param subKey
	 *            the sub key
	 * @return the value which was deleted
	 */
	public V remove( K mainKey, SK subKey ) {
		V obj = null;

		if ( this.containsSubKey( mainKey, subKey ) ) {
			obj = _innerDoubleMap.get( mainKey ).remove( subKey );

			if ( _innerDoubleMap.get( mainKey ).isEmpty() ) {
				_innerDoubleMap.remove( mainKey );
			}
		}

		return obj;
	}

	/**
	 * Size of main keys.
	 * 
	 * @return the size of main keys
	 */
	public int sizeMainKeys() {
		return _innerDoubleMap.size();
	}

	/**
	 * Size of sub keys.
	 * 
	 * @param mainKey
	 *            the main key of the sub key set
	 * @return the size of sub keys of a main key
	 */
	public int sizeSubKeys( K mainKey ) {
		return this.containsMainKey( mainKey ) ? _innerDoubleMap.get( mainKey ).size() : 0;
	}

	/**
	 * Gets all values of a main key by ignoring the sub key.
	 * 
	 * @param mainKey
	 *            the main key
	 * @return the collection of all values in this main key
	 */
	public Collection< V > valuesOfMainKey( K mainKey ) {
		return this.containsMainKey( mainKey ) ? _innerDoubleMap.get( mainKey ).values()
				: new HashSet< V >();
	}

}
