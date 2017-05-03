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
package eu.unifiedviews.helpers.dpu.serialization;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.ontology.EntityDescription;
import eu.unifiedviews.helpers.dpu.serialization.rdf.SerializationRdf;

/**
 * Common utility class for serialization.
 *
 * @author Škoda Petr
 */
public class SerializationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SerializationUtils.class);

    private SerializationUtils() {
    }

    /**
     *
     * @param <T>
     * @param clazz
     * @return Instance of given class.
     * @throws SerializationFailure
     */
    public static <T> T createInstance(Class<T> clazz) throws SerializationFailure {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SerializationFailure(e);
        }
    }

    /**
     * Get type of collection.
     *
     * @param genType
     * @return Null if type can not be obtained.
     */
    public static Class<?> getCollectionGenericType(Type genType) {
        if (!(genType instanceof ParameterizedType)) {
            LOG.warn("Superclass it not ParameterizedType");
            return null;
        }
        final Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        // We know there should be just one for Collection.
        if (params.length != 1) {
            LOG.warn("Unexpected number of generic types: {} (1 expected)", params.length);
            return null;
        }
        if (!(params[0] instanceof Class)) {
            LOG.warn("Unexpected type '{}'", params[0].toString());
            return null;
        }
        return (Class<?>) params[0];
    }

    /**
     * Merge (add) data from source to target. Variables are merged based on type:
     * <ul>
     *  <li>Collection - elements from source are added to target.</li>
     *  <li>Map - elements from source are added to target. In case of collision target values ore rewritten.</li>
     *  <li>Other - if source contains non-null and non-default value then it's used instead of target one.</li>
     * </ul>
     * 
     * All null or default values in target are overwritten with values from
     * source, collections are merged.
     *
     * In case of collection the elements from target are added to source.
     *
     * @param <T>
     * @param source
     * @param target
     */
    public static <T> void merge(T source, T target) throws RuntimeException {
        // Create new instance of source, so we can track down default values.
        final T sourceDefault;
        try {
            sourceDefault = (T)createInstance(target.getClass());
        } catch (SerializationFailure ex) {
            throw new RuntimeException("Can't create a default class.", ex);
        }
        // Iterate over fields.
        for (Field field : source.getClass().getDeclaredFields()) {
            try {
                mergeField(field, source, target, sourceDefault);
            } catch (IllegalAccessException | IllegalArgumentException |InvocationTargetException ex) {
                throw new RuntimeException("Problem with reflection during class merge.", ex);
            }
        }
    }

    /**
     * If target field is null, then value from source is used instead. In case of collection all elements
     * from source are added to target.
     *
     * @param <T>
     * @param field
     * @param source
     * @param target
     * @param sourceDefault Default values of source.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static <T> void mergeField(Field field, T source, T target, T sourceDefault) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Object sourceValue, sourceDefaultValue;
        final PropertyDescriptor fieldDesc;
        // Read source value.
        if ((field.getModifiers() & Modifier.PUBLIC) > 0) {
            fieldDesc = null; // We do not use this here.
            sourceValue = field.get(source);
            sourceDefaultValue = field.get(sourceDefault);
        } else {
            try {
                fieldDesc = new PropertyDescriptor(field.getName(), source.getClass());
            } catch (IntrospectionException ex) {
                throw new RuntimeException("Can't get property descriptor for: "
                        + field.getName(), ex);
            }
            sourceValue = fieldDesc.getReadMethod().invoke(source);
            sourceDefaultValue = fieldDesc.getReadMethod().invoke(sourceDefault);
        }
        if (sourceValue == null) {
            // Do not copy, no value in source.
            return;
        }
        if (sourceValue instanceof Collection) {
            // Get representation (as collection) of source and target. The merge here is to add
            // data from source colleciton to target one.
            final Collection sourceCollection = (Collection) sourceValue;
            final Collection targetCollection;
            if (fieldDesc == null) {
                targetCollection = (Collection)field.get(target);
            } else {
                targetCollection = (Collection) fieldDesc.getReadMethod().invoke(target);
            }
            // Add items.
            targetCollection.addAll(sourceCollection);
        } else if (sourceValue instanceof Map) {
            // Same as for collections abovbe.
            final Map sourceMap = (Map) sourceValue;
            final Map targetMap;
            if (fieldDesc == null) {
                targetMap = (Map)field.get(target);
            } else {
                targetMap = (Map) fieldDesc.getReadMethod().invoke(target);
            }            
            // Add items.
            targetMap.putAll(sourceMap);
        } else {
             if (sourceValue.equals(sourceDefaultValue)) {
                 // Default value in source, we keep value in target.
             } else {
                 // Primitive type -> just write.
                 if (fieldDesc == null) {
                     field.set(target, sourceValue);
                 } else {
                     fieldDesc.getWriteMethod().invoke(target, sourceValue);
                 }
             }
        }
    }

    /**
     * Generate RDF serialization configuration (description) for given class.
     *
     * @param clazz
     * @return
     * @throws cz.cuni.mff.xrg.uv.boost.serialization.SerializationFailure
     */
    public static SerializationRdf.Configuration createConfiguration(Class<?> clazz)
            throws SerializationFailure {
        return createConfiguration(clazz, null);
    }

    /**
     *
     * @param clazz
     * @param config Configuration class to use. Use null to use empty configuration instance.
     * @return
     */
    private static SerializationRdf.Configuration createConfiguration(Class<?> clazz,
            SerializationRdf.Configuration config) throws SerializationFailure {
        if (config == null) {
            config = new SerializationRdf.Configuration();
        }
        final String className = clazz.getCanonicalName();
        // Load entity annotation.
        final EntityDescription.Entity entity = clazz.getAnnotation(EntityDescription.Entity.class);
        final SerializationRdf.Configuration.Entity entityConfig;
        final String baseOntologyURI;
        if (entity != null) {
            entityConfig = new SerializationRdf.Configuration.Entity(entity.type());
            // Use type as default ontology prefix.
            baseOntologyURI = entity.type();
        } else {
            throw new SerializationFailure("Missing entity annotaation for class: " + clazz.getSimpleName());
        }
        config.getEntities().put(className, entityConfig);
        // Iterate over fields.
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            final Class<?> fieldClass = field.getType();
            // Load property anottation.
            final EntityDescription.Property property = field.getAnnotation(EntityDescription.Property.class);
            final SerializationRdf.Configuration.Property propertyConfig;
            if (property != null) {
                propertyConfig = new SerializationRdf.Configuration.Property(property.uri());
            } else {
                // We need to generate uri -> we need base IRI.
                if (baseOntologyURI == null) {
                    throw new SerializationFailure("Missing uri for: " + field.getName()
                            + " and no general base URI is provided.");
                }
                propertyConfig = new SerializationRdf.Configuration.Property(
                        baseOntologyURI + "/" + field.getName());
            }
            config.getProperties().put(className + "." + field.getName(), propertyConfig);
            // Check if we do not need to load other object.
            if (Collection.class.isAssignableFrom(fieldClass)) {
                // It's a collection, analyze type.
                createConfiguration(getCollectionGenericType(field.getGenericType()), config);
            } else if (fieldClass.isSynthetic()) {
                createConfiguration(fieldClass, config);
            }
        }
        return config;
    }

}
