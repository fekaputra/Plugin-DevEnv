package eu.unifiedviews.helpers.dataunit.maphelper;

import java.util.Map;

import eu.unifiedviews.dataunit.MetadataDataUnit;

public class MapHelpers {

    private static final MapHelpers selfie = new MapHelpers();
    
    private MapHelpers() {
        
    }
    
    private class MapHelperImpl implements MapHelper {
        private MetadataDataUnit dataUnit;
        
        public MapHelperImpl() {
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public Map<String, String> getMap(String symbolicName, String mapName) {
            return null;
        }
        
    }
}
