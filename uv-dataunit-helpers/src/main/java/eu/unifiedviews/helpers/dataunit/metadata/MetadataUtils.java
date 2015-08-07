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
package eu.unifiedviews.helpers.dataunit.metadata;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import org.openrdf.repository.RepositoryConnection;

import eu.unifiedviews.dpu.DPUException;

/**
 * Provides easy way how to set/get metadata (predicate/object) for given symbolic name.
 *
 * This class can be used for a simple one shot actions with metadata, or
 * {@link MetadataUtilsInstance}/{@link WritableMetadataUtilsInstance} can be created and reused to prevent object
 * creation and deletion.
 *
 * <pre>
 * {@code
 * // set path to given file with respective symbolicName
 * Manipulator.add(writableFilesDataUnit, symbolicName, VirtualPathHelper.PREDICATE_VIRTUAL_PATH, path);
 * }
 * </pre>
 *
 * @author Škoda Petr
 */
public class MetadataUtils {

    private MetadataUtils() {

    }

    /**
     * Close must be called on returned class after usage. Also before first usage, symbolic name
     * must be set.
     *
     * @param dataUnit
     * @return
     * @throws DataUnitException
     */
    public static MetadataUtilsInstance create(MetadataDataUnit dataUnit)
            throws DataUnitException {
        return new MetadataUtilsInstance<>(dataUnit.getConnection(), dataUnit.getMetadataGraphnames(),
                null, true);
    }


    /**
     * Close must be called on returned class after usage. Also before first usage, symbolic name must be set.
     *
     * @param dataUnit
     * @return
     * @throws DataUnitException
     */
    public static WritableMetadataUtilsInstance create(WritableMetadataDataUnit dataUnit)
            throws DataUnitException {
        return new WritableMetadataUtilsInstance(dataUnit.getConnection(),
                dataUnit.getMetadataGraphnames(),
                dataUnit.getMetadataWriteGraphname(), null, true);
    }

    /**
     * Close must be called on returned class after usage.
     *
     * @param dataUnit
     * @param symbolicName
     * @return
     * @throws DataUnitException
     */
    public static MetadataUtilsInstance create(MetadataDataUnit dataUnit, String symbolicName)
            throws DataUnitException {
        return new MetadataUtilsInstance<>(dataUnit.getConnection(), dataUnit.getMetadataGraphnames(),
                symbolicName, true);
    }

    /**
     * Does not close given connection.
     *
     * @param dataUnit
     * @param symbolicName
     * @param connection
     * @return
     * @throws DataUnitException
     */
    public static MetadataUtilsInstance create(MetadataDataUnit dataUnit, String symbolicName,
            RepositoryConnection connection) throws DataUnitException {
        return new MetadataUtilsInstance<>(connection, dataUnit.getMetadataGraphnames(), symbolicName, false);
    }

    /**
     * Close must be called on returned class after usage.
     *
     * @param dataUnit
     * @param entry
     * @return
     * @throws DataUnitException
     */
    public static MetadataUtilsInstance create(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry)
            throws DataUnitException {
        return create(dataUnit, entry.getSymbolicName());
    }

    /**
     *
     * Does not close given connection.
     *
     * @param dataUnit
     * @param entry
     * @param connection
     * @return
     * @throws DataUnitException
     */
    public static MetadataUtilsInstance create(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry,
            RepositoryConnection connection) throws DataUnitException {
        return create(dataUnit, entry.getSymbolicName(), connection);
    }

    /**
     * Close must be called on returned class after usage.
     *
     * @param dataUnit
     * @param symbolicName
     * @return
     * @throws DataUnitException
     */
    public static WritableMetadataUtilsInstance create(WritableMetadataDataUnit dataUnit, String symbolicName)
            throws DataUnitException {
        return new WritableMetadataUtilsInstance(dataUnit.getConnection(),
                dataUnit.getMetadataGraphnames(),
                dataUnit.getMetadataWriteGraphname(), symbolicName, true);
    }

    /**
     *
     * Does not close given connection.
     *
     * @param dataUnit
     * @param symbolicName
     * @param connection
     * @return
     * @throws DataUnitException
     */
    public static WritableMetadataUtilsInstance create(WritableMetadataDataUnit dataUnit, String symbolicName,
            RepositoryConnection connection) throws DataUnitException {
        return new WritableMetadataUtilsInstance(connection,
                dataUnit.getMetadataGraphnames(),
                dataUnit.getMetadataWriteGraphname(), symbolicName, false);
    }

    /**
     * Get a string stored under given predicate and symbolicName.
     * 
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @return
     * @throws DataUnitException
     * @throws DPUException If more then one object is found for given predicate and symbolicName.
     */
    public static String get(MetadataDataUnit dataUnit, String symbolicName, URI predicate)
            throws DataUnitException, DPUException {
        try (MetadataUtilsInstance instance = create(dataUnit, symbolicName)) {
            final Value value = instance.get(predicate);
            if (value == null) {
                return null;
            } else {
                return value.stringValue();
            }
        }
    }

    /**
     * Get a string stored under given predicate and symbolicName.
     * 
     * @param dataUnit
     * @param entry
     * @param predicate
     * @return
     * @throws DataUnitException
     * @throws DPUException If more then one object is found for given predicate and symbolicName.
     */
    public static String get(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate)
            throws DataUnitException, DPUException {
        try (MetadataUtilsInstance instance = create(dataUnit, entry)) {
            final Value value = instance.get(predicate);
            if (value == null) {
                return null;
            } else {
                return value.stringValue();
            }
        }
    }

    /**
     * Get a string stored under given predicate and symbolicName.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param connection
     * @return
     * @throws DataUnitException
     * @throws DPUException If more then one object is found for given predicate and symbolicName.
     */
    public static String get(MetadataDataUnit dataUnit, String symbolicName, URI predicate,
            RepositoryConnection connection) throws DataUnitException, DPUException {
        try (MetadataUtilsInstance instance = create(dataUnit, symbolicName, connection)) {
            final Value value = instance.get(predicate);
            if (value == null) {
                return null;
            } else {
                return value.stringValue();
            }
        }
    }

    /**
     * Get a string stored under given predicate and symbolicName.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param connection
     * @return
     * @throws DataUnitException
     * @throws DPUException If more then one object is found for given predicate and symbolicName.
     */
    public static String get(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate,
            RepositoryConnection connection) throws DataUnitException, DPUException {
        try (MetadataUtilsInstance instance = create(dataUnit, entry, connection)) {
            final Value value = instance.get(predicate);
            if (value == null) {
                return null;
            } else {
                return value.stringValue();
            }
        }
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @return
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static String getFirst(MetadataDataUnit dataUnit, String symbolicName, String predicate)
            throws DataUnitException {
        return getFirst(dataUnit, symbolicName, ValueFactoryImpl.getInstance().createURI(predicate));
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @return
     * @throws DataUnitException
     */
    public static String getFirst(MetadataDataUnit dataUnit, String symbolicName, URI predicate)
            throws DataUnitException {
        try (MetadataUtilsInstance instance = create(dataUnit, symbolicName)) {
            final Value value = instance.getFirst(predicate);
            if (value == null) {
                return null;
            } else {
                return value.stringValue();
            }
        }
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @return
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static String getFirst(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, String predicate)
            throws DataUnitException {
        return getFirst(dataUnit, entry.getSymbolicName(), predicate);
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @return
     * @throws DataUnitException
     */
    public static String getFirst(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate)
            throws DataUnitException {
        return getFirst(dataUnit, entry.getSymbolicName(), predicate);
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param connection
     * @return
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static String getFirst(MetadataDataUnit dataUnit, String symbolicName, String predicate,
            RepositoryConnection connection) throws DataUnitException {
        return getFirst(dataUnit, symbolicName, ValueFactoryImpl.getInstance().createURI(predicate),
                connection);
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param connection
     * @return
     * @throws DataUnitException
     */
    public static String getFirst(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate,
            RepositoryConnection connection) throws DataUnitException {
        return getFirst(dataUnit, entry.getSymbolicName(), predicate, connection);
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param connection
     * @return
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static String getFirst(MetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, String predicate,
            RepositoryConnection connection) throws DataUnitException {
        return getFirst(dataUnit, entry.getSymbolicName(), predicate, connection);
    }

    /**
     * Get a strings stored under given predicate and symbolicName.
     *
     * If more strings are stored under given predicate then one of them is returned.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param connection
     * @return
     * @throws DataUnitException
     */
    public static String getFirst(MetadataDataUnit dataUnit, String symbolicName, URI predicate,
            RepositoryConnection connection) throws DataUnitException {
        try (MetadataUtilsInstance instance = create(dataUnit, symbolicName, connection)) {
            final Value value = instance.getFirst(predicate);
            if (value == null) {
                return null;
            } else {
                return value.stringValue();
            }
        }
    }

    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void set(WritableMetadataDataUnit dataUnit, String symbolicName, String predicate,
            String value) throws DataUnitException {
        set(dataUnit, symbolicName, ValueFactoryImpl.getInstance().createURI(predicate), value);
    }

    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @throws DataUnitException
     */
    public static void set(WritableMetadataDataUnit dataUnit, String symbolicName, URI predicate,
            String value) throws DataUnitException {
        try (WritableMetadataUtilsInstance instance = create(dataUnit, symbolicName)) {
            instance.set(predicate, value);
        }
    }

    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void set(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, String predicate,
            String value) throws DataUnitException {
        set(dataUnit, entry.getSymbolicName(), predicate, value);
    }

    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @throws DataUnitException
     */
    public static void set(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate,
            String value) throws DataUnitException {
        set(dataUnit, entry.getSymbolicName(), predicate, value);
    }


    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void set(WritableMetadataDataUnit dataUnit, String symbolicName, String predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        set(dataUnit, symbolicName, ValueFactoryImpl.getInstance().createURI(predicate), value, connection);
    }

    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     */
    public static void set(WritableMetadataDataUnit dataUnit, String symbolicName, URI predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        try (WritableMetadataUtilsInstance instance = create(dataUnit, symbolicName, connection)) {
            instance.set(predicate, value);
        }
    }

    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void set(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, String predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        set(dataUnit, entry.getSymbolicName(), predicate, value, connection);
    }

    /**
     * Set metadata under given predicate If the predicate is already set then is replaced. To add multiple
     * metadata under same predicate use
     * {@link #add(eu.unifiedviews.dataunit.WritableMetadataDataUnit, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     */
    public static void set(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        set(dataUnit, entry.getSymbolicName(), predicate, value, connection);
    }

    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void add(WritableMetadataDataUnit dataUnit, String symbolicName, String predicate,
            String value) throws DataUnitException {
        add(dataUnit, symbolicName, ValueFactoryImpl.getInstance().createURI(predicate), value);
    }

    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @throws DataUnitException
     */
    public static void add(WritableMetadataDataUnit dataUnit, String symbolicName, URI predicate,
            String value) throws DataUnitException {
        try (WritableMetadataUtilsInstance instance = create(dataUnit, symbolicName)) {
            instance.add(predicate, value);
        }
    }

    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void add(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, String predicate,
            String value) throws DataUnitException {
        add(dataUnit, entry.getSymbolicName(), predicate, value);
    }

    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @throws DataUnitException
     */
    public static void add(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate,
            String value) throws DataUnitException {
        add(dataUnit, entry.getSymbolicName(), predicate, value);
    }


    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void add(WritableMetadataDataUnit dataUnit, String symbolicName, String predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        add(dataUnit, symbolicName, ValueFactoryImpl.getInstance().createURI(predicate), value, connection);
    }

    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param symbolicName
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     */
    public static void add(WritableMetadataDataUnit dataUnit, String symbolicName, URI predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        try (WritableMetadataUtilsInstance instance = create(dataUnit, symbolicName, connection)) {
            instance.add(predicate, value);
        }
    }

    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     * @deprecated Use version with {@link URI} instead.
     */
    @Deprecated
    public static void add(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, String predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        add(dataUnit, entry.getSymbolicName(), predicate, value, connection);
    }

    /**
     * Add metadata for given symbolic name. The old data under same predicate are not deleted. Use to add
     * multiple metadata of same meaning.
     *
     * @param dataUnit
     * @param entry
     * @param predicate
     * @param value
     * @param connection
     * @throws DataUnitException
     */
    public static void add(WritableMetadataDataUnit dataUnit, MetadataDataUnit.Entry entry, URI predicate,
            String value, RepositoryConnection connection) throws DataUnitException {
        add(dataUnit, entry.getSymbolicName(), predicate, value, connection);
    }

}
