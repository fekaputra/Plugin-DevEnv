package eu.unifiedviews.helpers.dataunit.maphelper;

import java.util.Map;

import eu.unifiedviews.dataunit.DataUnitException;

public interface MapHelper extends AutoCloseable {
    public static final String PREDICATE_HAS_MAP = "http://unifiedviews.eu/MapHelper/hasMap";
    // Consider using dcterms:title
    public static final String PREDICATE_MAP_TITLE = "http://unifiedviews.eu/MapHelper/map/title"; 
    public static final String PREDICATE_MAP_CONTAINS = "http://unifiedviews.eu/MapHelper/map/contains";
    public static final String PREDICATE_MAP_ENTRY_KEY = "http://unifiedviews.eu/MapHelper/map/entry/key";
    public static final String PREDICATE_MAP_ENTRY_VALUE = "http://unifiedviews.eu/MapHelper/map/entry/value";
    
    Map<String, String> getMap(String symbolicName, String mapName) throws DataUnitException;

    void putMap(String symbolicName, String mapName, Map<String, String> map) throws DataUnitException;

    @Override
    public void close() throws DataUnitException;
}
