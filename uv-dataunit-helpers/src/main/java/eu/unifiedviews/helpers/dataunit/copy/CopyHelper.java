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
package eu.unifiedviews.helpers.dataunit.copy;

import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Helper for copying all metadata related to single symbolicName, that is these triples:
 * <p><blockquote><pre>
 *  &ltsubject&gt &lt;{@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME}&gt; "name"
 *  &ltsubject&gt ?p ?v
 * </pre></blockquote></pre>
 * In future versions it will be improved to copy whole triple-tree rooted at &lt;subject&gt;
 * <p>
 * User of the helper is obliged to close this helper after he finished work with it (closes underlying connections).
 * <p>
 * For example usage see {@link CopyHelpers}.
 */
public interface CopyHelper extends AutoCloseable {
    /**
     * Copy all metadata related to single symbolicName
     * @param symbolicName key to {@link eu.unifiedviews.dataunit.MetadataDataUnit.Entry} which will be copied
     * @throws DataUnitException
     */
    void copyMetadata(String symbolicName) throws DataUnitException;

    @Override
    public void close();
}
