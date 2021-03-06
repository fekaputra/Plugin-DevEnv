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
package eu.unifiedviews.helpers.dataunit.copy;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.query.impl.SimpleDataset;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;

/**
 * Static helper nutshell for {@link CopyHelper}.
 * <p>
 * The helper can be used in two ways:
 * <ul>
 * <li>static (and ineffective), quick and dirty way {@code CopyHelpers.copyMetadata("name", sourceDataUnit, destinationDataUnit)}.
 * This does the job, but every call opens new connection to the underlying storage and then closes the connection adding a little overhead.</li>
 * <li>dynamic way,
 * <p><blockquote><pre>
 * //first create helper over dataunits
 * CopyHelper helper = CopyHelpers.create(sourceDataUnit, destinationDataUnit);
 * // copy many times (helper holds its connections open)
 * try {
 *   helper.copyMetadata("some symbolic name");
 * } finally {
 *   helper.close();
 * }
 * </pre></blockquote></p>
 * </li>
 * </ul>
 */
public class CopyHelpers {
    private static final CopyHelpers selfie = new CopyHelpers();

    private CopyHelpers() {

    }

    /**
     * Create new {@link CopyHelper} using source and destination dataunits.
     * @param source data unit to copy metadata from
     * @param destination data unit to copy metadata to
     * @return new {@link CopyHelper} instance, don't forget to close it after usage
     */
    public static CopyHelper create(MetadataDataUnit source, WritableMetadataDataUnit destination) {
        return selfie.new CopyHelperImpl(source, destination);
    }

    /**
     * Just copy what i need and don't bother me with create/close. May be ineffective (each call = 1 connection opened+closed).
     *
     * @param symbolicName which metadata unit entry will copied
     * @param source data unit to copy metadata from
     * @param destination data unit to copy metadata to
     * @throws DataUnitException
     */
    public static void copyMetadata(String symbolicName, MetadataDataUnit source, WritableMetadataDataUnit destination) throws DataUnitException {
        CopyHelper helper = null;
        try {
            helper = create(source, destination);
            helper.copyMetadata(symbolicName);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
    }

    private class CopyHelperImpl implements CopyHelper {

        protected static final String SYMBOLIC_NAME_BINDING = "symbolicName";

        /**
         * Copy only first level.
         */
        protected static final String UPDATE =
                "INSERT { ?sA1 ?pA1 ?oA1 . ?oB1 ?pB2 ?oB2 . ?oC2 ?pC3 ?oC3 } WHERE { "
                        + "{ "
                        + "?sA1 ?pA1 ?oA1 . "
                        + "?sA1 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
                        + "} "
                        + "UNION "
                        + "{ "
                        + "?oB1 ?pB2 ?oB2 . "
                        + "?sB1 ?pB1 ?oB1 . "
                        + "?sB1 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
                        + "FILTER ((isURI(?oB1) || isBlank(?oB1))) "
                        + "} "
                        + "UNION "
                        + "{ "
                        + "?oC2 ?pC3 ?oC3 . "
                        + "?oC1 ?pC2 ?oC2 . "
                        + "?sC1 ?pC1 ?oC1 . "
                        + "?sC1 <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . "
                        + "FILTER ((isURI(?oC1) || isBlank(?oC1)) && (isURI(?oC2) || isBlank(?oC2))) "
                        + "} "
                        + "}";

        private final Logger LOG = LoggerFactory.getLogger(CopyHelperImpl.class);

        private MetadataDataUnit source;

        private WritableMetadataDataUnit destination;

        private RepositoryConnection connection = null;

        public CopyHelperImpl(MetadataDataUnit source, WritableMetadataDataUnit destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public void copyMetadata(String symbolicName) throws DataUnitException {
            try {
                if (connection == null) {
                    connection = source.getConnection();
                }
                // Select all triples <bnode> symbolicName "symbolicName"
                // add all of them to destination data unit
                // (we use source connection - both run on same storage).

                final Update update = connection.prepareUpdate(
                        QueryLanguage.SPARQL, UPDATE);

                update.setBinding(SYMBOLIC_NAME_BINDING,
                        connection.getValueFactory().createLiteral(symbolicName));

                final SimpleDataset dataset = new SimpleDataset();
                for (IRI item : source.getMetadataGraphnames()) {
                    dataset.addDefaultGraph(item);
                }
                dataset.setDefaultInsertGraph(
                        destination.getMetadataWriteGraphname());

                update.setDataset(dataset);
                update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException ex) {
                throw new DataUnitException("", ex);
            }
        }

        @Override
        public void close() {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
    }
}
