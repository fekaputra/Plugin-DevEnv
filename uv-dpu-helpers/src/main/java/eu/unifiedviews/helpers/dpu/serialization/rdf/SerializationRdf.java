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
package eu.unifiedviews.helpers.dpu.serialization.rdf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

/**
 * Provide serialisation of simple POJO classes into rdf statements and back.
 *
 * Does not support generic classes. The only supported generic are those with whose super type is
 * {@link java.util.Collection}.
 *
 * Collection must be initialised in given object.
 *
 * @author Škoda Petr
 */
public interface SerializationRdf {



    /**
     * Configuration for {@link SerializationRdf}, reflects annotations. If this class is used
     * is must contains full information about every serialized object in a object tree.
     */
    public static class Configuration {

        public static class Property {

            public String uri;

            public Property(String uri) {
                this.uri = uri;
            }
            
        }

        public static class Entity {

            public String type;

            public Entity(String type) {
                this.type = type;
            }

        }

        /**
         * Key is full name of the field: "{canonical name of class}.{field name}"
         */
        private final Map<String, Property> properties = new HashMap<>();

        /**
         * Used only during serialization to RDF.
         */
        private final Map<String, Entity> entities = new HashMap<>();

        public Map<String, Property> getProperties() {
            return properties;
        }

        public Map<String, Entity> getEntities() {
            return entities;
        }

    }

    /**
     *
     * @param connection
     * @param rootResource Root subject for object representation.
     * @param context Graphs from which read triples.
     * @param object Object to load data into.
     * @param config Null to load configuration from object.
     * @throws cz.cuni.mff.xrg.uv.service.serialization.SerializationFailure
     * @throws SerializationRdfFailure
     * @throws eu.unifiedviews.dataunit.DataUnitException
     * @throws org.openrdf.repository.RepositoryException
     */
    public void convert(RepositoryConnection connection, Resource rootResource, 
            List<RDFDataUnit.Entry> context, Object object, Configuration config)
            throws SerializationFailure, SerializationRdfFailure, DataUnitException, RepositoryException;

}
