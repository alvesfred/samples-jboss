package br.com.alvesfred.quartz;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Base Class for Quartz / DeltaSpike support-packages
 *
 * @author alvesfred
 *
 */
public abstract class BaseQuartzJob implements Job { 

	// Logger
	protected final static Logger LOGGER = 
			Logger.getLogger(BaseQuartzJob.class.getName());

	private JobExecutionContext jobExecutionContext;
	private boolean ok = false;

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {


		this.setJobExecutionContext(jobExecutionContext);
		ContextControl ctxCtrl = null;

		try {
		    ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);
    	    ctxCtrl.startContext(RequestScoped.class);

			if (this.isEnabled()) {
				LOGGER.info("Job Class " + this.getClass() + " is executing...");
			
				this.beforeExecute();
				this.execute();
				this.afterExecute();
				
				setOk(true);
				LOGGER.info("Job Class " + this.getClass() + " finished!");
			}

		} catch (Exception t) {
			setOk(false);
			onExecutionError(t);

			throw new JobExecutionException(t);

		} finally {
			if (ctxCtrl != null) {
				ctxCtrl.stopContext(RequestScoped.class);				
			}
		}
	}

	/**
	 * Execution, After and Before
	 *
	 * @throws Exception
	 */
	protected abstract void execute() throws Exception;
	protected abstract void onBeforeExecute() throws Exception;
	protected abstract void onAfterExecute() throws Exception;

	/**
	 * Error Event
	 *
	 * @param obj
	 * @param ex
	 * @throws Exception
	 */
	protected abstract void onExecutionError(Exception ex);
	
	/**
	 * Job Enabled
	 */
	protected abstract boolean isEnabled();
	
	/**
	 * Before Execution
	 *
	 * @throws Exception
	 */
	protected void beforeExecute() throws Exception {
		onBeforeExecute();
	}

	/**
	 * After Execution
	 *
	 * @throws Exception
	 */
	protected void afterExecute() throws Exception {
		onAfterExecute();
	}

	/**
	 * Job Context
	 *
	 * @return
	 */
	protected JobExecutionContext getJobExecutionContext() {
		return jobExecutionContext;
	}

	private void setJobExecutionContext(JobExecutionContext jobExecutionContext) {
		this.jobExecutionContext = jobExecutionContext;
	}

	private void setOk(boolean ok) {
		this.ok = ok;
	}

	/**
	 * Ok Status
	 *
	 * @return
	 */
	protected boolean isOk() {
		return this.ok;
	}
}
