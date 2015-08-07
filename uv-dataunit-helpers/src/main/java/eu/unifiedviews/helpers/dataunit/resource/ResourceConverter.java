/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.unifiedviews.helpers.dataunit.resource;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.unifiedviews.helpers.dataunit.utils.ConvertUtils;

public class ResourceConverter {
    public static Map<String, String> resourceToMap(Resource resource) {
        Map<String, String> result = new LinkedHashMap<>();
        if (resource.getCreated() != null) {
            result.put("created", ConvertUtils.dateToString(resource.getCreated()));
        }
        if (resource.getDescription() != null) {
            result.put("description", resource.getDescription());
        }
        if (resource.getFormat() != null) {
            result.put("format", resource.getFormat());
        }
        if (resource.getHash() != null) {
            result.put("hash", resource.getHash());
        }
        if (resource.getLast_modified() != null) {
            result.put("last_modified", ConvertUtils.dateToString(resource.getLast_modified()));
        }
        if (resource.getMimetype() != null) {
            result.put("mimetype", resource.getMimetype());
        }
        if (resource.getMimetype_inner() != null) {
            result.put("mimetype_inner", resource.getMimetype_inner());
        }
        if (resource.getName() != null) {
            result.put("name", resource.getName());
        }
        if (resource.getResource_type() != null) {
            result.put("resource_type", resource.getResource_type());
        }
        if (resource.getSize() != null) {
            result.put("size", String.valueOf(resource.getSize()));
        }
        if (resource.getUrl() != null) {
            result.put("url", resource.getUrl());
        }
        if (resource.getId() != null) {
            result.put("id", resource.getId());
        }
        return result;
    }

    public static Resource resourceFromMap(Map<String, String> map) throws ParseException {
        Resource resource = new Resource();
        if (map.containsKey("created")) {
            resource.setCreated(ConvertUtils.stringToDate(map.get("created")));
        }
        if (map.containsKey("description")) {
            resource.setDescription(map.get("description"));
        }
        if (map.containsKey("format")) {
            resource.setFormat(map.get("format"));
        }
        if (map.containsKey("hash")) {
            resource.setHash(map.get("hash"));
        }
        if (map.containsKey("last_modified")) {
            resource.setLast_modified(ConvertUtils.stringToDate(map.get("last_modified")));
        }
        if (map.containsKey("mimetype")) {
            resource.setMimetype(map.get("mimetype"));
        }
        if (map.containsKey("mimetype_inner")) {
            resource.setMimetype_inner(map.get("mimetype_inner"));
        }
        if (map.containsKey("name")) {
            resource.setName(map.get("name"));
        }
        if (map.containsKey("resource_type")) {
            resource.setResource_type(map.get("resource_type"));
        }
        if (map.containsKey("size")) {
            resource.setSize(Long.valueOf(map.get("size")));
        }
        if (map.containsKey("url")) {
            resource.setUrl(map.get("url"));
        }
        if (map.containsKey("id")) {
            resource.setId(map.get("id"));
        }
        return resource;
    }

    public static Map<String, String> extrasToMap(Extras extras) {
        Map<String, String> result = new LinkedHashMap<>();
        result.putAll(extras.getMap());
        return result;
    }

    public static Extras extrasFromMap(Map<String, String> map) throws ParseException {
        Extras extras = new Extras();
        extras.getMap().putAll(map);
        return extras;
    }

}
