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
package eu.unifiedviews.helpers.dpu.extension.rdf.simple;

import java.lang.reflect.Field;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.extension.Extension;

/**
 * Wrap for {@link RDFDataUnit} aims to provide more user friendly way how to handler RDF functionality and
 * also reduce code duplicity.
 *
 * If this class is initialized with {@link cz.cuni.mff.xrg.uv.boost.dpu.initialization.AutoInitializer}
 * then:
 * <ul>
 * <li>If {@link FaultTolerance} is presented and initialized then it's automatically use in this class.</li>
 * </ul>
 * @author Škoda Petr
 */
public class SimpleRdf implements Extension {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleRdf.class);

    private RDFDataUnit readDataUnit = null;

    /**
     * Name of field that should be bound as a data unit.
     */
    protected String dataUnitName;

    private ValueFactory valueFactory = null;

    protected FaultTolerance faultTolerance = null;

    public RDFDataUnit getReadDataUnit() {
        return readDataUnit;
    }

    /**
     *
     * @return True if this object is active, initialized and can be used.
     */
    public boolean isActive() {
        return readDataUnit != null;
    }

    @Override
    public void preInit(String param) throws DPUException {
        dataUnitName = param;
    }

    @Override
    public void afterInit(Context context) throws DPUException {
        if (context instanceof ExecContext) {
            final ExecContext execContext = (ExecContext)context;
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
            if (RDFDataUnit.class.isAssignableFrom(value.getClass())) {
                readDataUnit = (RDFDataUnit)value;
            } else {
                throw new DPUException("Class" + value.getClass().getCanonicalName()
                        + " can't be assigned to RDFDataUnit.");
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
    public ValueFactory getValueFactory() throws DPUException {
        if (faultTolerance == null) {
            try {
                return getValueFactoryInner();
            } catch (DataUnitException ex) {
                throw new DPUException(ex);
            }
        } else {
            return faultTolerance.execute(new FaultTolerance.ActionReturn<ValueFactory>() {

                @Override
                public ValueFactory action() throws Exception {
                    return getValueFactoryInner();
                }

            });
        }
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
