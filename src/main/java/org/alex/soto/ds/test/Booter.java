package org.alex.soto.ds.test;

import java.util.Map;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = Booter.class)
public class Booter {

	final private static Logger log = LoggerFactory.getLogger(Booter.class);

	@Reference
	private ServiceComponentRuntime scr;
	private Worker worker;

	@Activate
	private void init() {
		log.info("Activated");
	}

	@Reference(
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY,
			target = "(component.factory=my.factory)",
			unbind = "unbindFactory")
	synchronized public void bindFactory(ComponentFactory factory, final Map<Object, Object> props) {
		log.info("Added factory");
		ComponentInstance ci = factory.newInstance(null);
		worker = (Worker) ci.getInstance();
	}

	synchronized public void unbindFactory(ComponentFactory factory) throws Exception {
		log.info("Removed factory");
		this.worker = null;
	}

	synchronized public String doWork() {
		if (worker == null) throw new RuntimeException("worker is null");
		return worker.doWork();
	}

}
