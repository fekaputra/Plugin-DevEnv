package eu.unifiedviews.dataunit.relational;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.dpu.DPU;

public interface WritableRelationalDataUnit extends RelationalDataUnit, WritableMetadataDataUnit {
    
    /**
     * Get base database table name (string) which should be used to create all new database tables.
     * Each data unit gets its own, unique table name.
     * For all database tables created during execution of {@link DPU} which are to be added into data unit,
     * names should start with this prefix to ensure uniqueness of database table across the all DPUs  
     * @return Base table name unique for this data unit
     * @throws DataUnitException
     */
    String getBaseDbTableName() throws DataUnitException;
    
    /**
     * Adds an existing database table with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * @param symbolicName symbolic name under which the table will be stored (must be unique in scope of this data unit)
     * @param dbTableName name of existing database table 
     * @throws DataUnitException
     */
    void addExistingDbTable(String symbolicName, String dbTableName) throws DataUnitException;
    
    /**
     * Generates unique database table with prefix {@link WritableRelationalDataUnit#getBaseDbTableName()}
     * Returns generated database table name
     * Creates a new database table with an unique name and adds it into data unit 
     * @param symbolicName 
     * @return Name of created database table
     * @throws DataUnitException
     */
    String addNewDbTable(String symbolicName) throws DataUnitException;
    
    /**
     * Update an existing table symbolic name with a new real database table name
     * The symbolic name must exists in data unit prior to calling this method. 
     * @param symbolicName symbolic name under which the table is stored (must be unique in scope of this data unit)
     * @param dbTableName new database table name 
     * @throws DataUnitException
     */
    void updateExistingTableName(String symbolicName, String dbTableName) throws DataUnitException;

}
