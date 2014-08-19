package eu.unifiedviews.helpers.dataunit.dataset;

import java.util.LinkedHashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.impl.DatasetImpl;

/**
 * Dynamic creational class for creating {@link Dataset} instances in .withX() notation.
 * <p>
 * Example usage:
 * {@code
 * Set<URI> someSet = obtainRemoveGraphsSomehow();
 * Query query = connection.prepareQuery();
 * query.setDataset(new DatasetBuilder()
 *   .withInsertGraph("http://default")
 *   .withDefaultRemoveGraphs(someSet)
 *   .addDefaultGraph("http://nondefault")
 *   .build());
 * query.evaluate();
 * }
 * <p>
 * Fields are by default empty (empty set or null in case of insert graph).
 */
public class DatasetBuilder {

    private Set<URI> defaultRemoveGraphs = new LinkedHashSet<URI>();

    private URI defaultInsertGraph;

    private Set<URI> defaultGraphs = new LinkedHashSet<URI>();;

    private Set<URI> namedGraphs = new LinkedHashSet<URI>();

    public DatasetBuilder() {
    }

    /**
     * Add all provided graphs to an existing set of defaultRemoveGraphs. Does not replace previously added graphs.
     * @param defaultRemoveGraphs graph URIs to add to defaultRemoveGraphs property
     * @return this
     */
    public DatasetBuilder withDefaultRemoveGraphs(Set<URI> defaultRemoveGraphs) {
        this.defaultRemoveGraphs.addAll(defaultRemoveGraphs);
        return this;
    }

    /**
     * Add provided graph to an existing set of defaultRemoveGraphs. Does not replace previously added graphs.
     * @param defaultRemoveGraph graph URI to add to defaultRemoveGraphs property
     * @return this
     */
    public DatasetBuilder addDefaultRemoveGraph(URI defaultRemoveGraph) {
        this.defaultRemoveGraphs.add(defaultRemoveGraph);
        return this;
    }

    /**
     * Sets defaultInsertGraph property (rewrites previous value)
     * @param defaultInsertGraph graph URI to set to defaultInsertGraph property
     * @return this
     */
    public DatasetBuilder withInsertGraph(URI defaultInsertGraph) {
        this.defaultInsertGraph = defaultInsertGraph;
        return this;
    }

    /**
     * Add all provided graphs to an existing set of defaultGraphs. Does not replace previously added graphs.
     * @param defaultGraphs graph URIs to add to defaultGraphs property
     * @return this
     */
    public DatasetBuilder withDefaultGraphs(Set<URI> defaultGraphs) {
        this.defaultGraphs.addAll(defaultGraphs);
        return this;
    }

    /**
     * @deprecated Do not use. Wrong name! Will be removed in future versions.
     * @param defaultGraph
     * @return
     */
    @Deprecated
    public DatasetBuilder addDefaultGraphs(URI defaultGraph) {
        this.defaultGraphs.add(defaultGraph);
        return this;
    }

    /**
     * Add provided graph to an existing set of defaultGraphs. Does not replace previously added graphs.
     * @param defaultGraph graph URI to add to defaultGraphs property
     * @return this
     */
    public DatasetBuilder addDefaultGraph(URI defaultGraph) {
        this.defaultGraphs.add(defaultGraph);
        return this;
    }

    /**
     * Add all provided graphs to an existing set of namedGraphs. Does not replace previously added graphs.
     * @param namedGraphs graph URIs to add to namedGraphs property
     * @return this
     */
    public DatasetBuilder withNamedGraphs(Set<URI> namedGraphs) {
        this.namedGraphs.addAll(namedGraphs);
        return this;
    }

    /**
     * Add provided graph to an existing set of namedGraphs. Does not replace previously added graphs.
     * @param namedGraph graph URI to add to namedGraphs property
     * @return this
     */
    public DatasetBuilder addNamedGraph(URI namedGraph) {
        this.namedGraphs.add(namedGraph);
        return this;
    }

    /**
     * Build the {@link Dataset} instance from current state of builder class.
     * @return mutable {@link Dataset} implementation which is independent of this class (deep copies values)
     */
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
