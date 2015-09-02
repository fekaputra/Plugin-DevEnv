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
package eu.unifiedviews.helpers.dpu.extension.files.simple;

import java.lang.reflect.Field;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.extension.Extension;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

/**
 * Wraps {@link FilesDataUnit} to provide more user friendly way how to read files from {@link FilesDataUnit}.
 * If {@link FaultTolerance} is presented and initialized then it is automatically used by this class.
 * 
 * @author Škoda Petr
 */
public class SimpleFiles implements Extension {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleFiles.class);

    private FilesDataUnit readDataUnit = null;

    /**
     * Name of field that should be bound as a data unit.
     */
    protected String dataUnitName;

    protected FaultTolerance faultTolerance = null;

    private ValueFactory valueFactory = null;

    @Override
    public void preInit(String param) throws DPUException {
        dataUnitName = param;
    }

    @Override
    public void afterInit(Context context) throws DPUException {
        if (context instanceof ExecContext) {
            final ExecContext execContext = (ExecContext) context;
            afterInitExecution(execContext);
        }
    }

    private void afterInitExecution(ExecContext execContext) throws DPUException {
        final Object dpu = execContext.getDpu();
        // Get underlying RDFDataUnit.
        final Field field;
        try {
            field = dpu.getClass().getField(dataUnitName);
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new DPUException("Wrong initial parameter for SimpleRdf: " + dataUnitName
                    + ". Can't access such field.", ex);
        }
        try {
            final Object value = field.get(dpu);
            if (value == null) {
                return;
            }
            if (FilesDataUnit.class.isAssignableFrom(value.getClass())) {
                readDataUnit = (FilesDataUnit) value;
            } else {
                throw new DPUException("Class" + value.getClass().getCanonicalName()
                        + " can't be assigned to FilesDataUnit.");
            }
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new DPUException("Can't get value for: " + dataUnitName, ex);
        }
        // Get FaultTolerance class if presented.
        faultTolerance = (FaultTolerance) execContext.getInstance(FaultTolerance.class);
    }

    /**
     * Cache result. After first successful call does not fail.
     * 
     * @return
     * @throws DataUnitException
     */
    private ValueFactory getValueFactoryInner() throws DataUnitException {
        if (valueFactory == null) {
            RepositoryConnection connection = null;
            try {
                connection = readDataUnit.getConnection();
                valueFactory = connection.getValueFactory();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (RepositoryException ex) {
                    LOG.warn("Can't close connection.", ex);
                }
            }
        }
        return valueFactory;
    }

}
