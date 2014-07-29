package eu.unifiedviews.helpers.dataunit.rdfhelper;

import java.util.LinkedHashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.Dataset;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;

public class RDFHelper {
    public static Set<URI> getGraphs(RDFDataUnit rdfDataUnit) throws DataUnitException {
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

    public static URI[] getGraphsArray(RDFDataUnit rdfDataUnit) throws DataUnitException {
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
        return resultSet.toArray(new URI[0]);
    }
    
    public static Dataset getDatasetWithDefaultGraphs(RDFDataUnit rdfDataUnit) throws DataUnitException {
        return new DatasetBuilder().withDefaultGraphs(RDFHelper.getGraphs(rdfDataUnit)).build();
    }
}
