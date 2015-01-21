package eu.unifiedviews.dataunit.tabular;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * {@link TabularDataUnit} stores metadata entries and each entry is enhanced by database table name.
 *  This data unit stores db tables, along with any metadata attached to table entries.
 */
public interface TabularDataUnit extends MetadataDataUnit {
    
    /**
     * Value: {@value #PREDICATE_DB_TABLE_NAME}, predicate used to store metadata about the dataunit (DB table name).
     * Format of RDF metadata (see {@link MetadataDataUnit}) is then extended to at least two triples:
     * <p><blockquote><pre>
     * &lt;subject&gt; &lt;{@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME}&gt; "name literal"
     * &lt;subject&gt &lt;{@value #PREDICATE_DB_TABLE_NAME}&gt; &lt;some_unique_db_table_name&gt;
     * </pre></blockquote></p>
     */
    static final String PREDICATE_DB_TABLE_NAME = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/TabularDataUnit/dbTableName";
    
    interface Entry extends MetadataDataUnit.Entry {
        /**
         * Get the database table name which can be then used to access data stored in the database
         *
         * @return Name of database table inside storage where data are located.
         * @throws DataUnitException when something fails
         */
        String getTableName() throws DataUnitException;
    }
    
    interface Iteration extends MetadataDataUnit.Iteration {

        @Override
        TabularDataUnit.Entry next() throws DataUnitException;
    }

    /**
     * List the tables.
     *
     * @return iteration
     * @throws DataUnitException
     */
    @Override
    TabularDataUnit.Iteration getIteration() throws DataUnitException;
    

}
