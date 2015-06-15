package eu.unifiedviews.helpers.dataunit.resource;


public class ResourceMerger {
    public static Resource merge(Resource primary, Resource secondary) {
        Resource result = new Resource();
        result.setCreated(secondary.getCreated());
        result.setDescription(secondary.getDescription());
        result.setFormat(secondary.getFormat());
        result.setHash(secondary.getHash());
        result.setLast_modified(secondary.getLast_modified());
        result.setMimetype(secondary.getMimetype());
        result.setMimetype_inner(secondary.getMimetype_inner());
        result.setName(secondary.getName());
        result.setResource_type(secondary.getResource_type());
        result.setSize(secondary.getSize());
        result.setUrl(secondary.getUrl());
        result.getExtras().getMap().putAll(secondary.getExtras().getMap());
        
        if (primary.getCreated() != null) {
            result.setCreated(primary.getCreated());
        }
        if (primary.getDescription() != null) {
            result.setDescription(primary.getDescription());
        }
        if (primary.getFormat() != null) {
            result.setFormat(primary.getFormat());
        }
        if (primary.getHash() != null) {
            result.setHash(primary.getHash());
        }
        if (primary.getLast_modified() != null) {
            result.setLast_modified(primary.getLast_modified());
        }
        if (primary.getMimetype() != null) {
            result.setMimetype(primary.getMimetype());
        }
        if (primary.getMimetype_inner() != null) {
            result.setMimetype_inner(primary.getMimetype_inner());
        }
        if (primary.getName() != null) {
            result.setName(primary.getName());
        }
        if (primary.getResource_type() != null) {
            result.setResource_type(primary.getResource_type());
        }
        if (primary.getSize() != null) {
            result.setSize(primary.getSize());
        }
        if (primary.getUrl() != null) {
            result.setUrl(primary.getUrl());
        }
        
        return result;
    }

}
