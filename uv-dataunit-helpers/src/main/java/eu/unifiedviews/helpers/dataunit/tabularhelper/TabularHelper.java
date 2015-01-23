package eu.unifiedviews.helpers.dataunit.tabularhelper;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.tabular.TabularDataUnit;

/**
 * Helper to make various tasks with {@link TabularDataUnit} friendly.
 */
public class TabularHelper {
    
    /**
     * Exhaust {@link eu.unifiedviews.dataunit.tabular.TabularDataUnit.Iteration} (obtained using {@link eu.unifiedviews.dataunit.tabular.TabularDataUnit#getIteration()}) into one {@link Map} of entries.
     * Beware - if the {@link eu.unifiedviews.dataunit.tabular.TabularDataUnit} contains milions or more entries, storing all of this in single {@link Map} is not a good idea.
     * Only suitable for work with ~100000 of entries (tables)
     *
     * @param tabularDataUnit data unit from which the iteration will be obtained and exhausted
     * @return {@link Map} containing all entries, keys are symbolic names
     * @throws DataUnitException
     */
    public static Map<String, TabularDataUnit.Entry> getTablesMap(TabularDataUnit tabularDataUnit) throws DataUnitException {
        if (tabularDataUnit == null) {
            return new LinkedHashMap<>();
        }
        TabularDataUnit.Iteration iteration = tabularDataUnit.getIteration();
        Map<String, TabularDataUnit.Entry> resultSet = new LinkedHashMap<>();
        try {
            while (iteration.hasNext()) {
                TabularDataUnit.Entry entry = iteration.next();
                resultSet.put(entry.getSymbolicName(), entry);
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }
    
    /**
     * Exhaust {@link eu.unifiedviews.dataunit.tabular.TabularDataUnit.Iteration} (obtained using {@link eu.unifiedviews.dataunit.tabular.TabularDataUnit#getIteration()}) into one {@link Set} of entries.
     * Beware - if the {@link eu.unifiedviews.dataunit.tabular.TabularDataUnit} contains milions or more entries, storing all of this in single {@link Set} is not a good idea.
     * Only suitable for work with ~100000 of entries (tables)
     *
     * @param tabularDataUnit data unit from which the iteration will be obtained and exhausted
     * @return {@link Set} containing all entries, keys are symbolic names
     * @throws DataUnitException
     */
    public static Set<TabularDataUnit.Entry> getTables(TabularDataUnit tabularDataUnit) throws DataUnitException {
        if (tabularDataUnit == null) {
            return new LinkedHashSet<>();
        }
        TabularDataUnit.Iteration iteration = tabularDataUnit.getIteration();
        Set<TabularDataUnit.Entry> resultSet = new LinkedHashSet<>();
        try {
            while (iteration.hasNext()) {
                TabularDataUnit.Entry entry = iteration.next();
                resultSet.add(entry);
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }

}
