/*******************************************************************************
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
 *******************************************************************************/
package eu.unifiedviews.dataunit.files;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * {@link FilesDataUnit} stores metadata entries and each entry is enhanced by file location.
 * This data unit stores files therefore, along with any metadata attached to file entries.
 */
public interface FilesDataUnit extends MetadataDataUnit {

    /**
     * Value: {@value #PREDICATE_FILE_URI}, predicate used to store metadata about file (the URI location of the file) inside storage.
     * Each metadata entry is then represented at least by triples:
     * <p><blockquote><pre>
     * &lt;subject&gt &lt;{@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME}&gt; "name literal"
     * &lt;subject&gt; &lt;{@value #PREDICATE_FILE_URI}&gt; &lt;file://c:/Users/uv/some/location/main.xls&gt;
     * </pre></blockquote></p>
     */
    static final String PREDICATE_FILE_URI = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/FilesDataUnit/fileURI";

    interface Entry extends MetadataDataUnit.Entry {

        /**
         * String stored URI representing real file location
         *
         * @return URI (as string) of the real file location, for example: "http://example.com/my_file.png" or "file://c:/Users/example/myDoc.doc"
         * @throws eu.unifiedviews.dataunit.DataUnitException
         */
        String getFileURIString() throws DataUnitException;
    }

    interface Iteration extends MetadataDataUnit.Iteration {

        @Override
        FilesDataUnit.Entry next() throws DataUnitException;
    }

    /**
     * List the files.
     *
     * @return iteration
     * @throws DataUnitException
     */
    @Override
    FilesDataUnit.Iteration getIteration() throws DataUnitException;
}
