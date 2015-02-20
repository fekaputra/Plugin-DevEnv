#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.cuni.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.cuni.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.cuni.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.cuni.dpu.exec.AutoInitializer;
import eu.unifiedviews.helpers.cuni.extensions.FaultTolerance;

/**
 * Main data processing unit class.
 *
 * @author ${author}
 */
@DPU.As${dpu_type}
public class ${dpu_name} extends AbstractDpu<${dpu_name}Config_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(${dpu_name}.class);
		
    @AutoInitializer.Init
    public FaultTolerance faultTolerance;

	public ${dpu_name}() {
		super(${dpu_name}VaadinDialog.class, ConfigHistory.noHistory(${dpu_name}Config_V1.class));
	}
		
    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "${dpu_name}.message");
        
    }
	
}
