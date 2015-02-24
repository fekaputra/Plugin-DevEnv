package eu.unifiedviews.helpers.dataunit.virtualpath;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;

/**
 * Helper to get and set virtualPath metadata to a single symbolicName stored in {@link MetadataDataUnit}.
 *
 * VirtualPath is considered a metadata telling some relative file path under which the data could be stored if needed to be stored in file.
 * If data are never to be stored in files, it will be ignored probably, but plugins working with files should try to check,
 * whether the entry in {@link MetadataDataUnit} does not have any virtual path set, and if so, use it to generate filename for storing data.
 * <p>
 * For example consider following pipeline:
 * <pre>
 * ------------------      ----------------      ----------------      ----------------
 * |                |      |              |      |              |      |              |
 * | E-HttpDownload | ---> | T-FilesToRdf | ---> | T-RdfToFiles | ---> | L-FilesToScp |
 * |                |      |              |      |              |      |              |
 * ------------------      ----------------      ----------------      ----------------
 * </pre>
 * <ol>
 * <li> E-HttpDownload downloads example file http://example.com/new/things/list.rdf and adds it as entry to FilesDataUnit with
 * symbolicName "list" and virtualPath "new/things/list.rdf". </li>
 * <li> T-FilesToRdf extracts the contents of the file to triplestore, it creates graph with symbolicName "list",
 * some graph name (generated by {@link RDFDataUnit}) and copies any additional metadata from file entry to graph entry.</li>
 * <li> T-RdfToFiles stores triples back to file. Using symbolicName "list" and copying all the metadata to outputDataUnit.</li>
 * <li> L-FilesToScp copies files to remote host, and here it has to decide the filename to use on remote host. The real filename
 * on local disk is unusable, it is unique, generated name for temporary purposes. Symbolic name would be useful, but wait, if there is
 * virtual path metadata set on the entry, we know everything we need. So it copies the file contents on remote host into file
 * ${configurationBaseDirectory}/new/things/list.rdf
 * </li>
 * </ol>
 * Thats it. And pipeline designer can configure {@link DPU}s using the shorter symbolic name "list" in configuration dialogs, it does not
 * interfere with the virtualPath which smoothly passes by all {@link DPU}s on the way.
 * <p>
 * Each {@link DPU} which works with files, should choose reasonable name to set as virtualPath when creating files and try to use the
 * virtualPath metadata when outputing files.
 */
public interface VirtualPathHelper extends AutoCloseable {
    /**
     * Value: {@value #PREDICATE_VIRTUAL_PATH}, predicate for storing virtual path. Virtual path is stored as single triple:
     * <p><blockquote><pre>
     * &lt;subject&gt; &lt;{@value #PREDICATE_VIRTUAL_PATH}&gt; "new/things/list.rdf"
     * </pre></blockquote></p>
     */
    public static final String PREDICATE_VIRTUAL_PATH = "http://unifiedviews.eu/VirtualPathHelper/virtualPath";

    /**
     * Get virtual path set for symbolicName
     * @param symbolicName name
     * @return virtual path (e.g. "new/book/pages.csv")
     * @throws DataUnitException
     */
    String getVirtualPath(String symbolicName) throws DataUnitException;

    /**
     * Set virtual path to symbolicName
     * @param symbolicName name
     * @param virtualPath virtual path (e.g. "new/book/pages.csv")
     * @throws DataUnitException
     */
    void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException;

    @Override
    public void close();
}
