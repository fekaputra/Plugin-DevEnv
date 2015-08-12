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
package eu.unifiedviews.dataunit.relational;

import java.sql.Connection;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * {@link RelationalDataUnit} stores metadata entries and each entry is enhanced by database table name.
 * This data unit stores db tables, along with any metadata attached to table entries.
 */
public interface RelationalDataUnit extends MetadataDataUnit {

    /**
     * Value: {@value #PREDICATE_DB_TABLE_NAME}, predicate used to store metadata about the dataunit (DB table name).
     * Format of RDF metadata (see {@link MetadataDataUnit}) is then extended to at least two triples:
     * <p>
     * <blockquote>
     * 
     * <pre>
     * &lt;subject&gt; &lt;{@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME}&gt; "name literal"
     * &lt;subject&gt &lt;{@value #PREDICATE_DB_TABLE_NAME}&gt; &lt;some_unique_db_table_name&gt;
     * </pre>
     * 
     * </blockquote>
     * </p>
     */
    static final String PREDICATE_DB_TABLE_NAME = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/RelationalDataUnit/dbTableName";

    interface Entry extends MetadataDataUnit.Entry {
        /**
         * Get the database table name which can be then used to access data stored in the database
         *
         * @return Name of database table inside storage where data are located.
         * @throws DataUnitException
         *             when something fails
         */
        String getTableName() throws DataUnitException;
    }

    interface Iteration extends MetadataDataUnit.Iteration {

        @Override
        RelationalDataUnit.Entry next() throws DataUnitException;
    }

    /**
     * List the tables.
     *
     * @return iteration
     * @throws DataUnitException
     */
    @Override
    RelationalDataUnit.Iteration getIteration() throws DataUnitException;

    /**
     * Returns connection to relational database, which is used to store Relational data of this data unit
     * 
     * @return Connection to database where database tables are stored
     * @throws DataUnitException
     */
    Connection getDatabaseConnection() throws DataUnitException;

    /**
     * Returns connection to relational database for specific user. User must exist prior calling this method or exception will be thrown
     * 
     * @return JDBC URL string
     * @throws DataUnitException
     */
    Connection getDatabaseConnectionForUser(String userName, String password) throws DataUnitException;
}
