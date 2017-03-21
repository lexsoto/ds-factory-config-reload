package org.alex.soto.ds.test;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(configurationPid = "my.config", factory = "my.factory")
public class Worker {

	final private static Logger log = LoggerFactory.getLogger(Worker.class);
	private String workToBeDone;

	@Activate
	public void init(Map<String, String> config) {
		workToBeDone = config.get("work.to.be.done");
		validate();
		log.info("Activated WorkerImpl with job: " + workToBeDone);
	}

	@Deactivate
	public void shutdown() {
		log.info("Shutting down WorkerImpl with job: " + workToBeDone);
		workToBeDone = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alex.soto.ds.test.WorkService#doWork()
	 */
	public String doWork() {
		validate();
		log.info("Doing work: {}.", workToBeDone);
		return workToBeDone;
	}

	private void validate() {
		// if (workToBeDone == null) {
		// throw new RuntimeException("No work to be done configured.");
		// }
	}
}
