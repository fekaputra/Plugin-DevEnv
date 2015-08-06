package eu.unifiedviews.helpers.dataunit.resource;

import java.util.LinkedHashMap;
import java.util.Map;

public class Extras {
    private Map<String, String> map = new LinkedHashMap<>();

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}