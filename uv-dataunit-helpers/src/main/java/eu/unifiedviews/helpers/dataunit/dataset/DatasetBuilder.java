package eu.unifiedviews.helpers.dataunit.dataset;

import java.util.LinkedHashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.impl.DatasetImpl;

public class DatasetBuilder {

    private Set<URI> defaultRemoveGraphs = new LinkedHashSet<URI>();

    private URI defaultInsertGraph;

    private Set<URI> defaultGraphs = new LinkedHashSet<URI>();;

    private Set<URI> namedGraphs = new LinkedHashSet<URI>();

    public DatasetBuilder() {
    }

    public DatasetBuilder withDefaultRemoveGraphs(Set<URI> defaultRemoveGraphs) {
        this.defaultRemoveGraphs.addAll(defaultRemoveGraphs);
        return this;
    }
    
    public DatasetBuilder addDefaultRemoveGraph(URI defaultRemoveGraph) {
        this.defaultRemoveGraphs.add(defaultRemoveGraph);
        return this;
    }

    public DatasetBuilder withInsertGraph(URI defaultInsertGraph) {
        this.defaultInsertGraph = defaultInsertGraph;
        return this;
    }

    public DatasetBuilder withDefaultGraphs(Set<URI> defaultGraphs) {
        this.defaultGraphs.addAll(defaultGraphs);
        return this;
    }

    public DatasetBuilder addDefaultGraphs(URI defaultGraph) {
        this.defaultGraphs.add(defaultGraph);
        return this;
    }

    public DatasetBuilder withNamedGraphs(Set<URI> namedGraphs) {
        this.namedGraphs.addAll(namedGraphs);
        return this;
    }

    public DatasetBuilder addNamedGraph(URI namedGraph) {
        this.namedGraphs.add(namedGraph);
        return this;
    }

    public Dataset build() {
        DatasetImpl dataset = new DatasetImpl();
        for (URI graphURI : defaultRemoveGraphs) {
            dataset.addDefaultRemoveGraph(graphURI);
        }
        dataset.setDefaultInsertGraph(defaultInsertGraph);
        for (URI graphURI : defaultGraphs) {
            dataset.addDefaultGraph(graphURI);
        }
        for (URI graphURI : namedGraphs) {
            dataset.addNamedGraph(graphURI);
        }
        return dataset;
    }
}
