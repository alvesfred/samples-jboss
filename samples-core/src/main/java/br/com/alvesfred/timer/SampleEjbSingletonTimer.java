package br.com.alvesfred.timer;

import java.util.UUID;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.inject.Inject;

import br.com.alvesfred.statefull.SampleEjbStatefull;

/**
 * Singleton EJB Timer
 *
 * @author fred
 *
 */
@Singleton
public class SampleEjbSingletonTimer {

	@Inject
	private SampleEjbStatefull sampleEjbStateful;

	@Schedule(dayOfWeek="*", second = "10", minute = "*", hour = "*", persistent = false)
	void scheduler10SecondsStatefull() {
		System.out.println("Print for each 10 seconds...");
		sampleEjbStateful.addMessage(UUID.randomUUID().toString());
	}

	@Timeout
	public void programmaticTimeout(Timer timer) {
		System.out.println("Programmatic timeout occurred.");
	}
}
