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

import java.io.File;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesVocabulary;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

/**
 * Wraps {@link WritableFilesDataUnit} to provide more user friendly way how to write files to {@link WritableFilesDataUnit}.
 * Adds write functionality to {@link SimpleFiles}.
 * 
 * @author Škoda Petr
 */
public class WritableSimpleFiles extends SimpleFiles {

    private static final Logger LOG = LoggerFactory.getLogger(WritableSimpleFiles.class);

    private WritableFilesDataUnit writableDataUnit = null;

    /**
     * Add file.
     * 
     * @param file
     * @param fileName
     * @throws DPUException
     */
    public void add(final File file, final String fileName) throws DPUException {
        LOG.debug("adding file: {} as {}", file, fileName);
        if (faultTolerance == null) {
            try {
                writableDataUnit.addExistingFile(fileName, file.toURI().toString());
                // Add metadata - Virtual path.
                MetadataUtils.set(writableDataUnit, fileName, FilesVocabulary.UV_VIRTUAL_PATH, fileName);
            } catch (DataUnitException ex) {
                throw new DPUException("Failed to add file.", ex);
            }
        } else {
            // First add file.
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    writableDataUnit.addExistingFile(fileName, file.toURI().toString());
                }
            });
            // Add metadata - Virtual path.
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    MetadataUtils.set(writableDataUnit, fileName, FilesVocabulary.UV_VIRTUAL_PATH, fileName);
                }
            });
        }
    }

    /**
     * Create new file.
     * 
     * @param fileName
     * @return
     * @throws DPUException
     */
    public File create(final String fileName) throws DPUException {
        final File result;
        if (faultTolerance == null) {
            try {
                result = new File(java.net.URI.create(writableDataUnit.addNewFile(fileName)));
                // Add metadata - Virtual path.
                MetadataUtils.set(writableDataUnit, fileName, FilesVocabulary.UV_VIRTUAL_PATH, fileName);
            } catch (DataUnitException ex) {
                throw new DPUException("Failed to add file.", ex);
            }
        } else {
            // First create a file.
            result = faultTolerance.execute(new FaultTolerance.ActionReturn<File>() {

                @Override
                public File action() throws Exception {
                    return new File(java.net.URI.create(writableDataUnit.addNewFile(fileName)));
                }
            });
            // Add metadata - Virtual path.
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    MetadataUtils.set(writableDataUnit, fileName, FilesVocabulary.UV_VIRTUAL_PATH, fileName);
                }
            });
        }
        return result;
    }

    @Override
    public void preInit(String param) throws DPUException {
        super.preInit(param);
    }

    @Override
    public void afterInit(Context context) throws DPUException {
        super.afterInit(context);
        if (context instanceof ExecContext) {
            final ExecContext execContext = (ExecContext) context;
            afterInitExecution(execContext);
        }
    }

    private void afterInitExecution(ExecContext execContext) throws DPUException {
        // Get underliyng RDFDataUnit.
        final Object dpu = execContext.getDpu();
        final Field field;
        try {
            field = dpu.getClass().getField(dataUnitName);
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new DPUException("Wrong initial parameters for SimpleRdf: " + dataUnitName
                    + ". Can't access such field.", ex);
        }
        try {
            final Object value = field.get(dpu);
            if (value == null) {
                return;
            }
            if (WritableFilesDataUnit.class.isAssignableFrom(value.getClass())) {
                writableDataUnit = (WritableFilesDataUnit) value;
            } else {
                throw new DPUException("Class" + value.getClass().getCanonicalName()
                        + " can't be assigned to WritableFilesDataUnit.");
            }
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new DPUException("Can't get value for: " + dataUnitName, ex);
        }
    }

}
