package eu.unifiedviews.helpers.dataunit.rdfhelper;

import java.util.LinkedHashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.Dataset;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;

public class RDFHelper {
    public static Set<RDFDataUnit.Entry> getGraphs(RDFDataUnit rdfDataUnit) throws DataUnitException {
        RDFDataUnit.Iteration iteration = rdfDataUnit.getIteration();
        Set<RDFDataUnit.Entry> resultSet = new LinkedHashSet<>();
        try {
            while (iteration.hasNext()) {
                RDFDataUnit.Entry entry = iteration.next();
                resultSet.add(entry);
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }
    
    public static Set<URI> getGraphsURISet(RDFDataUnit rdfDataUnit) throws DataUnitException {
        RDFDataUnit.Iteration iteration = rdfDataUnit.getIteration();
        Set<URI> resultSet = new LinkedHashSet<>();
        try {
            while (iteration.hasNext()) {
                RDFDataUnit.Entry entry = iteration.next();
                resultSet.add(entry.getDataGraphURI());
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }

    public static URI[] getGraphsURIArray(RDFDataUnit rdfDataUnit) throws DataUnitException {
        return getGraphsURISet(rdfDataUnit).toArray(new URI[0]);
    }
    
    public static Dataset getDatasetWithDefaultGraphs(RDFDataUnit rdfDataUnit) throws DataUnitException {
        Set<URI> graphsUriSet = RDFHelper.getGraphsURISet(rdfDataUnit);
        if (graphsUriSet.isEmpty()) {
            throw new DataUnitException("Trying to build dataset from dataunit, which contains no data graphs");
        }
        return new DatasetBuilder().withDefaultGraphs(graphsUriSet).build();
    }
}
