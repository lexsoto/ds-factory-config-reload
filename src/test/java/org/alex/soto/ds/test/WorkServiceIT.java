package org.alex.soto.ds.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PaxExam.class)
@ExamReactorStrategy(org.ops4j.pax.exam.spi.reactors.PerClass.class)
public class WorkServiceIT {

	private static Logger log = LoggerFactory.getLogger(WorkServiceIT.class);

	@Inject
	private ConfigurationAdmin configurationAdmin;
	@Inject
	private Booter booter;

	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
		return probe;
	}

	@Configuration
	public Option[] config() {

		MavenUrlReference repo = maven()
				.groupId("org.alex.soto")
				.artifactId("blueprint-ds-config-reload")
				.versionAsInProject()
				.classifier("features")
				.type("xml");

		Option[] options = new Option[] {
				karafConfig(),
				keepRuntimeFolder(), logLevel(LogLevelOption.LogLevel.INFO),

				features(repo, "worker-feature")
		};
		return options;
	}

	private Option karafConfig() {
		return karafDistributionConfiguration().frameworkUrl(
				maven()
						.groupId("org.apache.karaf")
						.artifactId("apache-karaf")
						.versionAsInProject()
						.type("tar.gz"))
				.karafVersion(
						MavenUtils.getArtifactVersion("org.apache.karaf", "apache-karaf"))
				.name("Apache Karaf")
				.unpackDirectory(new File("target/exam/"));
	}

	@Test
	public void testConfigChange() throws Exception {
		assertEquals(null, booter.doWork());
		changeWorkToBeDone("rest");
		Thread.sleep(30_000);
		assertEquals("rest", booter.doWork());
	}

	private void changeWorkToBeDone(String workToBeDone) throws Exception {
		org.osgi.service.cm.Configuration configuration = configurationAdmin.getConfiguration("my.config", null);
		assertNotNull(configuration);
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put("work.to.be.done", workToBeDone);
		configuration.update(properties);
		log.info("Changed the configuration of job to: " + workToBeDone);
	}
}
