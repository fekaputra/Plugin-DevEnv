package eu.unifiedviews.helpers.dataunit.maphelper;

import java.util.Map;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.helpers.dataunit.dataset.CleverDataset;

public class MapHelpers {

    private static final MapHelpers selfie = new MapHelpers();

    private MapHelpers() {

    }

    public static MapHelper create(MetadataDataUnit dataUnit) {
        return selfie.new MapHelperImpl(dataUnit);
    }

    public static MapHelper create(WritableMetadataDataUnit dataUnit) {
        return selfie.new WritableMapHelperImpl(dataUnit);
    }

    public static Map<String, String> getMap(MetadataDataUnit dataUnit, String symbolicName, String mapName) throws DataUnitException {
        MapHelper helper = create(dataUnit);
        Map<String, String> result = helper.getMap(symbolicName, mapName);
        helper.close();
        return result;
    }

    public static void putMap(WritableMetadataDataUnit dataUnit, String symbolicName, String mapName, Map<String, String> map) throws DataUnitException {
        MapHelper helper = create(dataUnit);
        helper.putMap(symbolicName, mapName, map);
        helper.close();
    }

    private class MapHelperImpl implements MapHelper {
        private final Logger LOG = LoggerFactory.getLogger(MapHelperImpl.class);

        private MetadataDataUnit dataUnit;

        protected static final String SYMBOLIC_NAME_BINDING_NAME = "symbolicName";

        protected RepositoryConnection connection = null;

        public MapHelperImpl(MetadataDataUnit dataUnit) {
            this.dataUnit = dataUnit;
        }

        @Override
        public Map<String, String> getMap(String symbolicName, String mapName) throws DataUnitException {
            TupleQueryResult tupleQueryResult = null;
            Map<String, String> result = null;
            try {
                if (connection == null) {
                    connection = dataUnit.getConnection();
                }
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, "SOME MAGIC SELECT");
                tupleQuery.setBinding(SYMBOLIC_NAME_BINDING_NAME, connection.getValueFactory().createLiteral(symbolicName));
                CleverDataset dataset = new CleverDataset();
                dataset.addDefaultGraphs(dataUnit.getMetadataGraphnames());
                tupleQuery.setDataset(dataset);
                tupleQueryResult = tupleQuery.evaluate();
//                if (tupleQueryResult.hasNext()) {
//                    BindingSet bindingSet = tupleQueryResult.next();
//                    result = bindingSet.getBinding(VIRTUAL_PATH_BINDING_NAME).getValue().stringValue();
//                }
            } catch (QueryEvaluationException | RepositoryException | MalformedQueryException ex) {
                throw new DataUnitException("", ex);
            } finally {
                if (tupleQueryResult != null) {
                    try {
                        tupleQueryResult.close();
                    } catch (QueryEvaluationException ex) {
                        LOG.warn("Error in close.", ex);
                    }
                }
            }
            return result;
        }

        @Override
        public void putMap(String symbolicName, String mapName, Map<String, String> map) throws DataUnitException {
            throw new UnsupportedOperationException("Cannot put map into read only dataunit");
        }

        @Override
        public void close() throws DataUnitException {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
    }

    private class WritableMapHelperImpl extends MapHelperImpl {
        private final Logger LOG = LoggerFactory.getLogger(WritableMapHelperImpl.class);

        private WritableMetadataDataUnit dataUnit;

        public WritableMapHelperImpl(WritableMetadataDataUnit dataUnit) {
            super(dataUnit);
        }

        @Override
        public void putMap(String symbolicName, String mapName, Map<String, String> map) throws DataUnitException {
            try {
                if (connection == null) {
                    connection = dataUnit.getConnection();
                }
                Update update = connection.prepareUpdate(QueryLanguage.SPARQL, "SOME MAGICAL UPDATES");
                update.setBinding(SYMBOLIC_NAME_BINDING_NAME, connection.getValueFactory().createLiteral(symbolicName));
                CleverDataset dataset = new CleverDataset();
                dataset.addDefaultGraphs(dataUnit.getMetadataGraphnames());
                update.setDataset(dataset);
                update.execute();
            } catch (RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
                throw new DataUnitException("", ex);
            } finally {
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
}
