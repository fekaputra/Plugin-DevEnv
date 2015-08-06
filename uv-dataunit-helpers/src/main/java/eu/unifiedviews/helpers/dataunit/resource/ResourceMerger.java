package eu.unifiedviews.helpers.dataunit.resource;

/**
 * Class provides merging of two {@link Resource} entities, where not-null properties from primary resource are taken at first and when some property is not set in primary resource, it is taken from secondary one.
 * 
 * If some property is set in both primary and secondary resource, it is taken from primary resource.
 * If some property is set in primary and not secondary resource, it is taken from primary resource.
 * If some property is set in secondary and not primary resource, it is taken from secondary resource.
 * If some property is null in both resources, it is null in result. 
 *
 */
public class ResourceMerger {
    /**
     * Merges two {@link Resource} entities.
     *  
     * @param primary Primary resource, whose property values have precedence over secondary resource
     * @param secondary Secondary resource, whose properties are used only when primary resource property is null
     * @return new instance of {@link Resource} class (different from primary and secondary instances - so you can change them after calling this method, withou affecting the resulting {@link Resource}).
     */
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
        result.setId(secondary.getId());

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
        if (primary.getId() != null) {
            result.setId(primary.getId());
        }

        return result;
    }

}
