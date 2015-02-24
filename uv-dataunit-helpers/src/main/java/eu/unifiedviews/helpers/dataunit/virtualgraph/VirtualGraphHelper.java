package eu.unifiedviews.helpers.dataunit.virtualgraph;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;

/**
 * Helper to get and set virtualGraph metadata to a single symbolicName stored in {@link MetadataDataUnit}.
 *
 * virtualGraph is considered a metadata telling some definitive graph name under which the data could be stored if needed to be stored in external RDF storage.
 * If data are never to be stored in external RDF storage, it will be ignored probably, but plugins working with RDF graphs should try to check,
 * whether the entry in {@link MetadataDataUnit} does not have any virtual graph set, and if so, use it to generate destination (external) graph name for storing data.
 * <p>
 * For example consider following pipeline:
 * <pre>
 * ------------------      ----------------      ----------------      ---------------------
 * |                |      |              |      |              |      |                   |
 * | E-HttpDownload | ---> | T-FilesToRdf | ---> | T-RdfToFiles | ---> | L-FilesToVirtuoso |
 * |                |      |              |      |              |      |                   |
 * ------------------      ----------------      ----------------      ---------------------
 * </pre>
 * <ol>
 * <li> E-HttpDownload downloads example file http://example.com/new/things/list.rdf and adds it as entry to FilesDataUnit with
 * symbolicName "list" and virtualGraph "http://myNewGraphName". </li>
 * <li> T-FilesToRdf extracts the contents of the file to triplestore, it creates graph with symbolicName "list",
 * some internal graph name (generated by {@link RDFDataUnit}) and copies any additional metadata from file entry to graph entry.</li>
 * <li> T-RdfToFiles stores triples back to file. Using symbolicName "list" and copying all the metadata to outputDataUnit.</li>
 * <li> L-FilesToVirtuoso imports files to Virtuoso RDF Storage, and here it has to decide the graph name to use inside Virtuoso. The real internal
 * graph name in internal RDF storage is unusable, since it is unique, generated name for temporary purposes. Symbolic name would be useful, but wait, if there is
 * virtual graph metadata set on the entry, we know everything we need. So it imports the file contents to Virtuoso RDF Storage under
 * <http://myNewGraphName> graph name
 * </li>
 * </ol>
 * Thats it. And pipeline designer can configure {@link DPU}s using the shorter symbolic name "list" in configuration dialogs, it does not
 * interfere with the virtualGraph which smoothly passes by all {@link DPU}s on the way.
 * <p>
 * Each {@link DPU} which works with graphs, should choose reasonable name to set as virtualGraph when creating graphs and try to use the
 * virtualGraph metadata when outputing graphs.
 */
public interface VirtualGraphHelper extends AutoCloseable {
    /**
     * Value: {@value #PREDICATE_VIRTUAL_GRAPH}, predicate for storing virtual graph. Virtual graph is stored as single triple:
     * <p><blockquote><pre>
     * &lt;subject&gt; &lt;{@value #PREDICATE_VIRTUAL_GRAPH}&gt; "http://myNewGraphName"
     * </pre></blockquote></p>
     */
    public static final String PREDICATE_VIRTUAL_GRAPH = "http://unifiedviews.eu/VirtualGraphHelper/virtualGraph";

    /**
     * Get virtual graph set for symbolicName
     * @param symbolicName name
     * @return virtual graph (e.g.  "http://myNewGraphName")
     * @throws DataUnitException
     */
    String getVirtualGraph(String symbolicName) throws DataUnitException;

    /**
     * Set virtual graph to symbolicName
     * @param symbolicName name
     * @param virtualGraph virtual graph (e.g.  "http://myNewGraphName")
     * @throws DataUnitException
     */
    void setVirtualGraph(String symbolicName, String virtualGraph) throws DataUnitException;

    @Override
    public void close();
}
