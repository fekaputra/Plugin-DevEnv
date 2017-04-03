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
package eu.unifiedviews.helpers.dataunit.rdf;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;

import eu.unifiedviews.helpers.dataunit.virtualgraph.VirtualGraphHelper;

/**
 * Vocabulary definition for RDF.
 *
 * @author Škoda Petr
 */
public class RdfVocabulary {

    public static final String STR_UV_VIRTUAL_URI = VirtualGraphHelper.PREDICATE_VIRTUAL_GRAPH;

    public static final IRI UV_VIRTUAL_URI;

    static {
        final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

        UV_VIRTUAL_URI = valueFactory.createIRI(STR_UV_VIRTUAL_URI);
    }

}
