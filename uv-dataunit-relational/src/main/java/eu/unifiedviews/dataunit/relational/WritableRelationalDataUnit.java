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

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;

public interface WritableRelationalDataUnit extends RelationalDataUnit, WritableMetadataDataUnit {

    /**
     * Adds an existing database table with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * 
     * @param symbolicName
     *            symbolic name under which the table will be stored (must be unique in scope of this data unit)
     * @param dbTableName
     *            name of existing database table
     * @throws DataUnitException
     */
    void addExistingDatabaseTable(String symbolicName, String dbTableName) throws DataUnitException;

    /**
     * Generates unique database table with prefix and unique index as postfix. Returns generated database table name
     * Creates a new database table with an unique name and adds it into data unit
     * 
     * @param symbolicName
     * @return Name of created database table
     * @throws DataUnitException
     */
    String addNewDatabaseTable(String symbolicName) throws DataUnitException;

    /**
     * Update an existing table symbolic name with a new real database table name
     * The symbolic name must exists in data unit prior to calling this method.
     * <p/>
     * The typical use for this method is when it is necessary to copy entry from input data unit to output data unit while retaining its symbolic name and all
     * the metadata, but the target physical table has to be different from the source one - the data changed. To realize this, CopyHelper is used and then
     * WritableRelationalDataUnit.updateExistingTableName() is called.
     * <p/>
     * An alternative to WritableRelationalDataUnit.updateExistingTableName(sn, dbTable) would be to call addExistingDbTable(sn, dbTable) and then use
     * CopyHelper to copy all metadata except of the physical table name from input to output. But this approach has the problems that: current implementation
     * of addExistingDbTable(sn, dbTable) will add second entry with the same symbolic name (which is not what is intended), and, further, the entry in the
     * output data unit will have different ID to which metadata properties are attached, so simple CopyHelper is not enough, but cloning is needed. The
     * alternative approach is possible future work.
     * 
     * @param symbolicName
     *            symbolic name under which the table is stored (must be unique in scope of this data unit)
     * @param dbTableName
     *            new database table name
     * @throws DataUnitException
     */
    void updateExistingTableName(String symbolicName, String dbTableName) throws DataUnitException;
}
