package br.com.alvesfred.quartz;

import javax.inject.Inject;

import org.apache.deltaspike.scheduler.api.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import br.com.alvesfred.jms.BusinessJmsService;

/**
 * Job Sample Class
 *
 * @author alvesfred
 *
 */
@DisallowConcurrentExecution
@Scheduled(cronExpression = "0/15 * * * * ?", onStartup = true)
public class SampleJob extends BaseQuartzJob {

	@Inject
	private BusinessJmsService businessJmsService; 

	@Override
	protected void execute() throws Exception {
		// TODO
		businessJmsService.sendMessage("Sample Text");
	}

	@Override
	protected void onBeforeExecute() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onAfterExecute() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onExecutionError(Exception ex) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean isEnabled() {
		return true;
	}
}