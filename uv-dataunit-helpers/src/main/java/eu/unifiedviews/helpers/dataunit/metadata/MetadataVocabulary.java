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
package eu.unifiedviews.helpers.dataunit.metadata;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Common vocabulary definition.
 *
 * @author Škoda Petr
 */
public class MetadataVocabulary {

    public static final String STR_UV_SYMBOLIC_NAME = MetadataDataUnit.PREDICATE_SYMBOLIC_NAME;

    public static final IRI UV_SYMBOLIC_NAME;

    static {
        final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

        UV_SYMBOLIC_NAME = valueFactory.createIRI(STR_UV_SYMBOLIC_NAME);
    }

}
