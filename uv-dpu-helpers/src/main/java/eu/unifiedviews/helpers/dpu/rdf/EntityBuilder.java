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
package eu.unifiedviews.helpers.dpu.rdf;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

/**
 * Class designed for easy building of statements about certain RDF resource (entity). Each builder can be used to build just one RDF resource and its
 * properties.
 * As entity builder is in-memory entity is should not be used to build bigger objects (100+ statement).
 * 
 * @author Škoda Petr
 */
public class EntityBuilder {

    /**
     * Store statement about currently constructed object.
     */
    private final List<Statement> statemetns = new ArrayList<>(20);

    /**
     * Uri of currently constructed entity.
     */
    private final URI entityUri;

    /**
     * Used value factory.
     */
    private final ValueFactory valueFactory;

    /**
     * @param entityUri
     * @param valueFactory
     * @param ontology
     *            Ontology used during creation of this object. Can be null but in such case methods
     *            which utilize ontology must no be called.
     */
    public EntityBuilder(URI entityUri, ValueFactory valueFactory) {
        this.entityUri = entityUri;
        this.valueFactory = valueFactory;
    }

    /**
     * Adds property with certain value to the subject. Value of the property is added as string literal without language tag.
     * 
     * @param property
     * @param value
     * @return
     */
    public EntityBuilder property(URI property, String value) {
        statemetns.add(valueFactory.createStatement(entityUri, property, valueFactory.createLiteral(value)));
        return this;
    }

    /**
     * Adds property with certain value to the subject.
     * 
     * @param property
     * @param value
     * @return
     */
    public EntityBuilder property(URI property, Value value) {
        statemetns.add(valueFactory.createStatement(entityUri, property, value));
        return this;
    }

    /**
     * Adds property with certain value to the subject. As a value certain entity URI of given {@link EntityBuilder} is used.
     * So this method should be used to model object properties.
     * 
     * @param property
     * @param entity
     * @return
     */
    public EntityBuilder property(URI property, EntityBuilder entity) {
        statemetns.add(valueFactory.createStatement(entityUri, property, entity.getEntityUri()));
        return this;
    }

    /**
     * Gets all statements about the entity
     * 
     * @return Representation of current entity as a list of subject. Do not modify the returned list.
     */
    public List<Statement> asStatements() {
        return this.statemetns;
    }

    /**
     * Gets URI of the constructed entity
     * 
     * @return URI of entity that is under construction.
     */
    public URI getEntityUri() {
        return entityUri;
    }

}
