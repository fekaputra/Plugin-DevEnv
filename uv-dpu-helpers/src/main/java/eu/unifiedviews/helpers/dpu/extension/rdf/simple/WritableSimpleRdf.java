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
package eu.unifiedviews.helpers.dpu.extension.rdf.simple;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.StatementImpl;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.extension.ExtensionException;
import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.ontology.EntityDescription;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.extension.Extension;

/**
 * Wraps {@link WritableRDFDataUnit} to provide more user friendly way how to write RDF graphs to {@link WritableRDFDataUnit}.
 * Adds write functionality to {@link SimpleRdf}.
 * 
 * @author Škoda Petr
 */
public class WritableSimpleRdf extends SimpleRdf implements Extension.Executable {

    public static final String CONFIGURATION_CLASS_URI = "http://uv.xrg.mff.cuni.cz/ontology/dpu/boost/rdf/simple/Configuration";

    @EntityDescription.Entity(type = CONFIGURATION_CLASS_URI)
    public static class Configuration {

        public enum AddPolicy {

            /**
             * Triples are added into repository immediately. For each addition new connection is created.
             * This approach provide immediate reaction on possible problem (repository is offline) but in
             * case of saving greater number of triples can be computationally demanding.
             */
            IMMEDIATE,
            /**
             * Triples are stored in in memory buffer and added into repository once upon a time. This option
             * should be used if larger number of statements should be added into repository. There is a
             * disadvantage in current implementation where a single failure will cause failure there is no
             * guaranteed state of used repository. The buffer is cleared only if all the triples are
             * successfully added into repository.
             */
            BUFFERED,
            /**
             * Data are stored only on user request (call of {@link #flushBuffer()}. In this case
             * {@link #add(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Value)} does
             * not throw.
             */
            ON_DEMAND
        }

        public AddPolicy addPolicy = AddPolicy.BUFFERED;

        /**
         * Commit size in case the {@link AddPolicy#BUFFERED} is used.
         */
        public Integer commitSize = 100000;

    }

    private static final Logger LOG = LoggerFactory.getLogger(WritableSimpleRdf.class);

    private static final String DEFAULT_SYMBOLIC_NAME = "default-output";

    private WritableRDFDataUnit writableDataUnit = null;

    /**
     * Buffer for triples that should be added into wrapped {@link #dataUnit}.
     */
    protected final List<Statement> writeBuffer = new ArrayList<>(100000);

    /**
     * Current write set.
     */
    protected List<IRI> writeContext = new ArrayList<>();

    protected Configuration configuration = new Configuration();

    /**
     * Add triple into repository. Based on current {@link AddPolicy} can add triple in immediate or lazy way.
     * In the second case the {@link #flushBuffer()} method must be called in order to add triples into used
     * repository, until that the triples are stored in inner buffer - the triples are not visible in any read
     * function.
     * 
     * @param s
     * @param p
     * @param o
     * @return
     * @throws SimpleRdfException
     * @throws DPUException
     */
    public WritableSimpleRdf add(Resource s, IRI p, Value o) throws SimpleRdfException, DPUException {
        // Add to buffer.
        writeBuffer.add(new StatementImpl(s, p, o));
        applyFlushBufferPolicy();
        return this;
    }

    /**
     * @param statements
     * @return
     * @throws SimpleRdfException
     * @throws DPUException
     */
    public WritableSimpleRdf add(List<Statement> statements) throws SimpleRdfException, DPUException {
        writeBuffer.addAll(statements);
        applyFlushBufferPolicy();
        return this;
    }

    /**
     * Immediately store buffered triples into repository. The inner buffer is cleared only if all the triples
     * are added successfully. If throws exception then the state of repository is undefined.
     * If {@link #add(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Value)} is called
     * with {@link AddPolicy#BUFFERED} this method must be called in order to save added statements into
     * repository.
     * If throws, then the buffer remain unchanged so the function can be called again until it does not pass
     * or caller gives up.
     * 
     * @throws SimpleRdfException
     */
    public void flushBuffer() throws SimpleRdfException, DPUException {
        if (faultTolerance == null) {
            flushBufferInner();
        } else {
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    flushBufferInner();
                }
            });
        }
    }

    /**
     * Same as {@link #flushBuffer()}. Reason for this class is easier usage with fault tolerant wrap.
     * 
     * @throws SimpleRdfException
     * @throws DPUException
     */
    private void flushBufferInner() throws SimpleRdfException, DPUException {
        if (writeBuffer.isEmpty()) {
            // Nothing to save into repository.
            return;
        }
        if (writeContext.isEmpty()) {
            createDefaultWriteGraph();
        }
        // Prepare write contexts.
        final IRI[] contexts = writeContext.toArray(new IRI[0]);
        // Get connection and add data.
        RepositoryConnection connection = null;
        try {
            connection = writableDataUnit.getConnection();
            connection.begin();
            for (Statement statemnt : writeBuffer) {
                connection.add(statemnt, contexts);
            }
            connection.commit();
        } catch (DataUnitException ex) {
            throw new SimpleRdfException("Problem with DataUnit.", ex);
        } catch (RepositoryException ex) {
            throw new SimpleRdfException("Problem with Repository.", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Can't close connection.", ex);
                }
            }
        }
        // Data sucesfully stored, clear the buffer.
        writeBuffer.clear();
    }

    /**
     * Set given graph as current output.
     * 
     * @param entry
     * @return
     * @throws cz.cuni.mff.xrg.uv.boost.serialization.rdf.SimpleRdfException
     */
    public WritableSimpleRdf setOutput(RDFDataUnit.Entry entry) throws SimpleRdfException, DPUException {
        return setOutput(Arrays.asList(entry));
    }

    /**
     * Set given graphs as current output.
     * 
     * @param entries
     * @return
     * @throws cz.cuni.mff.xrg.uv.boost.serialization.rdf.SimpleRdfException
     */
    public WritableSimpleRdf setOutput(final List<RDFDataUnit.Entry> entries)
            throws SimpleRdfException, DPUException {
        // Flush buffer first.
        flushBuffer();
        if (faultTolerance == null) {
            setOutputInner(entries);
        } else {
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    setOutputInner(entries);
                }
            });
        }
        return this;
    }

    public void setOutputInner(List<RDFDataUnit.Entry> entries) throws SimpleRdfException {
        // Change target graph.
        final List<IRI> newWriteContext = new ArrayList<>(entries.size());
        for (RDFDataUnit.Entry entry : entries) {
            try {
                newWriteContext.add(entry.getDataGraphURI());
            } catch (DataUnitException ex) {
                throw new SimpleRdfException("Can't read graph name.", ex);
            }
        }
        this.writeContext = newWriteContext;
    }

    public List<IRI> getWriteContext() {
        return writeContext;
    }

    public void setWriteContext(List<IRI> writeContext) {
        this.writeContext = writeContext;
    }

    /**
     * @return If modify the configuration must be set back.
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * If {@link AddPolicy} change from {@link AddPolicy#BUFFERED} to {@link AddPolicy#IMMEDIATE} then {@link #flushBuffer()} is called.
     * 
     * @param configuration
     * @throws cz.cuni.mff.xrg.uv.boost.serialization.rdf.SimpleRdfException
     * @throws DPUException
     */
    public void setConfiguration(Configuration configuration) throws SimpleRdfException, DPUException {
        this.configuration = configuration;
        // Check if we should not flush buffer.
        if (this.configuration.addPolicy == Configuration.AddPolicy.IMMEDIATE) {
            this.flushBuffer();
        }
    }

    /**
     * Create and set new default output graph. Fixed symbolic name is used for output graph.
     * 
     * @throws SimpleRdfException
     * @throws DPUException
     */
    private void createDefaultWriteGraph() throws SimpleRdfException, DPUException {
        LOG.info("Default output graph used.");
        final IRI writeGraphUri;
        if (faultTolerance == null) {
            try {
                writeGraphUri = writableDataUnit.addNewDataGraph(DEFAULT_SYMBOLIC_NAME);
            } catch (DataUnitException ex) {
                throw new SimpleRdfException("Failed to add new graph.", ex);
            }
        } else {
            writeGraphUri = faultTolerance.execute(new FaultTolerance.ActionReturn<IRI>() {

                @Override
                public IRI action() throws Exception {
                    return writableDataUnit.addNewDataGraph(DEFAULT_SYMBOLIC_NAME);
                }
            });
        }
        writeContext.add(writeGraphUri);
    }

    /**
     * Based on policy call {@link #flushBuffer()} if needed.
     * 
     * @throws SimpleRdfException
     * @thrwos DPUException
     */
    private void applyFlushBufferPolicy() throws SimpleRdfException, DPUException {
        switch (configuration.addPolicy) {
            case BUFFERED:
                if (writeBuffer.size() > configuration.commitSize) {
                    flushBuffer();
                }
                break;
            case IMMEDIATE:
                flushBuffer();
                break;
            case ON_DEMAND:
                // No operation here.
                break;
            default:
                throw new RuntimeException("Unknown AddPolicy type: " + configuration.addPolicy.toString());
        }
    }

    @Override
    public void preInit(String param) throws DPUException {
        super.preInit(param);
    }

    @Override
    public void afterInit(Context context) throws DPUException {
        super.afterInit(context);
        if (context instanceof ExecContext) {
            final ExecContext execContext = (ExecContext) context;
            afterInitExecution(execContext);
        }
    }

    private void afterInitExecution(ExecContext execContext) throws DPUException {
        // Get underliyng RDFDataUnit.
        final Object dpu = execContext.getDpu();
        final Field field;
        try {
            field = dpu.getClass().getField(dataUnitName);
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new DPUException("Wrong initial parameters for SimpleRdf: " + dataUnitName
                    + ". Can't access such field.", ex);
        }
        try {
            final Object value = field.get(dpu);
            if (value == null) {
                return;
            }
            if (WritableRDFDataUnit.class.isAssignableFrom(value.getClass())) {
                writableDataUnit = (WritableRDFDataUnit) value;
            } else {
                throw new DPUException("Class" + value.getClass().getCanonicalName()
                        + " can't be assigned to WritableRDFDataUnit.");
            }
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new DPUException("Can't get value for: " + dataUnitName, ex);
        }
    }

    @Override
    public void execute(Extension.ExecutionPoint execPoint) throws ExtensionException {
        if (execPoint == Extension.ExecutionPoint.POST_EXECUTE) {
            // Made sure that all data are saved.
            try {
                flushBuffer();
            } catch (DPUException ex) {
                throw new ExtensionException("Can't flush data at the end of execution.", ex);
            }
        }
    }

    @Override
    public boolean isActive() {
        return super.isActive() && writableDataUnit != null;
    }

}
