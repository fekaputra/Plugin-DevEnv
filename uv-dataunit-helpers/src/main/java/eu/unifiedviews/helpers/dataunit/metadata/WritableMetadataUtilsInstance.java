/*******************************************************************************
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
 *******************************************************************************/
package eu.unifiedviews.helpers.dataunit.metadata;

import eu.unifiedviews.dataunit.DataUnitException;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Manipulator for writing metadata.
 *
 * Sample usage:
 * <pre>
 * {@code
 * WritableManipulatorInstance manipulator = Manipulator.create(filesDataUnit, null);
 * // Read virtual path for
 * manipulator.setEntry(fileEntry).set(VirtualPathHelper.PREDICATE_VIRTUAL_PATH, "myDirectory/file.dat");
 * }
 * </pre>
 *
 * @author Škoda Petr
 */
public class WritableMetadataUtilsInstance extends MetadataUtilsInstance<WritableMetadataUtilsInstance> {

    /**
     * %s - with clause
     * %s - using clause
     */
    private static final String UPDATE_QUERY
            = "%s DELETE {?s ?" + PREDICATE_BINDING + " ?o } "
            + "INSERT {?s ?" + PREDICATE_BINDING + " ?" + OBJECT_BINDING + " } "
            + "%s WHERE { "
            + "?s <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + "OPTIONAL { ?s ?" + PREDICATE_BINDING + " ?o } "
            + " } ";

    /**
     * %s - with clause
     * %s - using clause
     */
    private static final String INSERT_QUERY
            = "%s INSERT { ?s ?" + PREDICATE_BINDING + " ?" + OBJECT_BINDING + " } "
            + "%s WHERE { "
            + "?s <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
            + " } ";

    /**
     * String version of {@link #dataset}.
     */
    private final String datasetWithClause;

    WritableMetadataUtilsInstance(RepositoryConnection connection, Set<URI> readGraphs, URI writeGraph,
            String symbolicName, boolean closeConnectionOnClose) throws DataUnitException {
        super(connection, readGraphs, symbolicName, closeConnectionOnClose);
        if (useDataset()) {
            this.dataset.setDefaultInsertGraph(writeGraph);
            this.dataset.addDefaultRemoveGraph(writeGraph);
            datasetWithClause = null;
        } else {
            datasetWithClause = "WITH <" + writeGraph.toString() + ">\n";
        }
    }

    /**
     * Add string (object) and predicate to metadata of current {@link #symbolicName}. If called multiple
     * times with same parameters (or predicate) then triples are only added, never deleted.
     *
     * This function can be used to add multiple informations under a single predicate.
     *
     * Also as this method return 'this' pointer it can be used in chained expressions.
     *
     * @param predicate
     * @param value
     * @return
     * @throws DataUnitException
     */
    public WritableMetadataUtilsInstance add(URI predicate, String value) throws DataUnitException {
        try {
            final String query;
            if (useDataset()) {
                query = String.format(INSERT_QUERY, "", "");
            } else {
                query = String.format(INSERT_QUERY, datasetWithClause, datasetUsingClause);
            }
            final ValueFactory valueFactory = connection.getValueFactory();
            final Update update = connection.prepareUpdate(QueryLanguage.SPARQL, query);
            update.setBinding(SYMBOLIC_NAME_BINDING, valueFactory.createLiteral(symbolicName));
            update.setBinding(PREDICATE_BINDING, predicate);
            update.setBinding(OBJECT_BINDING, valueFactory.createLiteral(value));
            if (useDataset()) {
                update.setDataset(dataset);
            }
            update.execute();
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException ex) {
            throw new DataUnitException("Failed to execute update.", ex);
        }
        return this;
    }

    /**
     * Set string (object) for given predicate. For metadata under current {@link #symbolicName}. All data
     * under the same predicate and for same {@link #symbolicName} are deleted.
     *
     * Use this if you want the given string to be the only object (information) stored under given predicate.
     *
     * Also as this method return 'this' pointer it can be used in chained expressions.
     *
     * @param predicate
     * @param value
     * @return
     * @throws DataUnitException
     */
    public WritableMetadataUtilsInstance set(URI predicate, String value) throws DataUnitException {
        try {
            final String query;
            if (useDataset()) {
                query = String.format(UPDATE_QUERY, "", "");
            } else {
                query = String.format(UPDATE_QUERY, datasetWithClause, datasetUsingClause);
            }
            final ValueFactory valueFactory = connection.getValueFactory();
            final Update update = connection.prepareUpdate(QueryLanguage.SPARQL, query);
            update.setBinding(SYMBOLIC_NAME_BINDING, valueFactory.createLiteral(symbolicName));
            update.setBinding(PREDICATE_BINDING, predicate);
            update.setBinding(OBJECT_BINDING, valueFactory.createLiteral(value));
            if (useDataset()) {
                update.setDataset(dataset);
            }
            update.execute();
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException ex) {
            throw new DataUnitException("Failed to execute update.", ex);
        }
        return this;
    }

}
