package com.keytiles.bdd;

import com.keytiles.bdd.tests.TestsRunner;
import com.sixt.tool.bdd_testsuite.runners.api.context.SystemPropertyInjectionHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ServiceTester {

    private final static Logger LOG = LoggerFactory.getLogger(ServiceTester.class);

    private static final String CLASSPATH_RESOURCE_PREFIX = "classpath:";

    private AbstractApplicationContext springServiceContainerContext;
    private File setupFolder, springFolder, storiesFolder, compositeStepsFolder, filesFolder;

    public ServiceTester(File setupFolder, File springFolder, File storiesFolder, File compositeStepsFolder, File filesFolder) {
        this.springFolder = springFolder;
        this.setupFolder = setupFolder;
        this.storiesFolder = storiesFolder;
        this.compositeStepsFolder = compositeStepsFolder;
        this.filesFolder = filesFolder;

        SystemPropertyInjectionHandler sysPropInjector = new SystemPropertyInjectionHandler();
        sysPropInjector.registerSystemProperty("filesFolder", filesFolder.getAbsolutePath().replace("\\", "/"), "main");
    }

    public void start() {
        File contextFile = new File(springFolder, "tests.context.xml");
        springServiceContainerContext = buildSpringContext(Arrays.asList("file:" + contextFile.getAbsolutePath()), null);

        TestsRunner runner = new TestsRunner(springServiceContainerContext, setupFolder, storiesFolder, compositeStepsFolder);
        runner.runTests();

        stop();
    }

    public void stop() {
        if(springServiceContainerContext == null) {
            // nothing to close
            LOG.warn("not running - stop() invocation is skipped");
            return;
        }
        IOUtils.closeQuietly(springServiceContainerContext, null);
        springServiceContainerContext = null;
    }



    private AbstractApplicationContext buildSpringContext(List<String> contextFilePaths, @Nullable ApplicationContext parentSpringContainer) {
        LOG.info("bringing up tests SpringContainer context based on files {} ...", contextFilePaths);

        AbstractApplicationContext springContext = null;

        String[] configLocations = contextFilePaths.toArray(new String[0]);
        if (configLocations[0].startsWith(CLASSPATH_RESOURCE_PREFIX)) {
            LOG.info("classpath based file locations detected - using ClassPathXmlApplicationContext ...");

            for (int i = 0; i < configLocations.length; i++) {
                configLocations[i] = configLocations[i].substring(CLASSPATH_RESOURCE_PREFIX.length());
            }
            springContext = parentSpringContainer == null
                    ? new ClassPathXmlApplicationContext(configLocations)
                    : new ClassPathXmlApplicationContext(configLocations, parentSpringContainer);
        } else {
            LOG.info("real file locations detected - using FileSystemXmlApplicationContext ...");

            springContext = parentSpringContainer == null
                    ? new FileSystemXmlApplicationContext(configLocations)
                    : new FileSystemXmlApplicationContext(configLocations, parentSpringContainer);
        }

        // this is required to fire up destroy hooks
        springContext.registerShutdownHook();

        return springContext;
    }
}
