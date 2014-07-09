package eu.unifiedviews.dataunit.rdf;

import eu.unifiedviews.dataunit.DataUnitException;
import org.openrdf.model.URI;

public interface WritableRDFDataUnit extends RDFDataUnit {

    interface WritableGraphIteration extends RDFDataUnit.GraphIteration {

        public void remove() throws DataUnitException;
    }

    @Override
    WritableRDFDataUnit.WritableGraphIteration getGraphIteration() throws DataUnitException;

    /**
     * Get name of the context which is considered to be owned by data unit and
     * is the only one into which write operations are performed.
     *
     * @return
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    URI getWriteGraph() throws DataUnitException;

    /**
     * Add all data from given DataUnit into this DataUnit. The method must not
     * modify the current parameter (unit).
     *
     * @param dataunit {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} to add
     * from
     * @throws IllegalArgumentException if some property of an element of the
     * specified dataunit prevents it from being added to this dataunit
     */
    void addAll(RDFDataUnit dataunit) throws DataUnitException;

}
