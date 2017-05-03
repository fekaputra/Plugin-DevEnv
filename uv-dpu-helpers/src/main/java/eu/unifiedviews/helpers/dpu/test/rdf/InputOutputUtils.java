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
package eu.unifiedviews.helpers.dpu.test.rdf;

import eu.unifiedviews.helpers.dpu.test.resources.ResourceUtils;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide functions to load/store values from {@link RDFDataUnit}/{@link WritableRDFDataUnit}.
 *
 * To obtain file from resource use {@link ResourceUtils}.
 *
 * @author Škoda Petr
 */
public final class InputOutputUtils {

    private static final Logger LOG = LoggerFactory.getLogger(InputOutputUtils.class);

    private InputOutputUtils() {
    }

    /**
     * Extract triples from given file and add them into repository, into the fixed graph name.
     *
     * @param source
     * @param format
     * @param dataunit
     * @param target Target entry into which data should be extracted from file.
     */
    public static void extractFromFile(File source, RDFFormat format, WritableRDFDataUnit dataunit, 
            RDFDataUnit.Entry target) {
        
        RepositoryConnection connection = null;
        try {
            final IRI graphUri = target.getDataGraphURI();

            connection = dataunit.getConnection();
            connection.begin();
            connection.add(source, "http://default-base/", format, graphUri);
            connection.commit();

            LOG.info("{} triples have been extracted from {}", connection.size(), source.toString());
            
        } catch (IOException | RepositoryException | RDFParseException | DataUnitException e) {
            LOG.error("Extraction failed.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException e) {
                    LOG.warn("Failed to close repository", e);
                }
            }
        }
    }

    /**
     * Load data from given {@link RDFDataUnit} into given file.
     *
     * @param source
     * @param target
     * @param format
     */
    public static void loadToFile(RDFDataUnit source, File target, RDFFormat format) {
        RepositoryConnection connection = null;

        // Get all contexts.
        final List<IRI> uris = new LinkedList<>();
        try {
            final RDFDataUnit.Iteration iter = source.getIteration();
            while (iter.hasNext()) {
                uris.add(iter.next().getDataGraphURI());
            }
        } catch (DataUnitException ex) {
            LOG.error("Faield to get graph list.", ex);
            return;
        }

        // Load from file to obtaned contexts.
        final IRI[] sourceContexts = uris.toArray(new IRI[0]);
        try (FileOutputStream out = new FileOutputStream(target);
                OutputStreamWriter os = new OutputStreamWriter(out, Charset.forName("UTF-8"));) {
            connection = source.getConnection();
            final RDFWriter rdfWriter = Rio.createWriter(format, os);
            connection.export(rdfWriter, sourceContexts);
        } catch (DataUnitException | RepositoryException | IOException | RDFHandlerException e) {
            LOG.error("Loading failed.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException e) {
                    LOG.warn("Failed to close repository", e);
                }
            }
        }
    }

}
