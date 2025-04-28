package com.keytiles.bdd.tests;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import com.sixt.tool.bdd_testsuite.CommonJBehaveTest;

public class TestsRunner {

    private final static Logger LOG = LoggerFactory.getLogger(TestsRunner.class);

    // TODO this static way is really ugly but will do for now
    static AbstractApplicationContext springServiceContainerContext;
    static File testSetupFolder;
    static File storiesFolder;
    static File compositeStepsFolder;

    private Result result;

    public TestsRunner(AbstractApplicationContext springServiceContainerContext, File testSetupFolder,
            File storiesFolder, File compositeStepsFolder) {
        CommonJBehaveTest.setTestsSpringContext(springServiceContainerContext);
        TestsRunner.springServiceContainerContext = springServiceContainerContext;
        TestsRunner.testSetupFolder = testSetupFolder;
        TestsRunner.storiesFolder = storiesFolder;
        TestsRunner.compositeStepsFolder = compositeStepsFolder;

        /*
         * attilaw - reconsidered this
         * As this is not possible with all class loaders for now let's do not do this!
         * 
         * We have introduced the "fsfile://" url schema usable in .story files next to
         * "file://" - so maybe we do not need this hack at all?
         * 
         * // let's add the main folder to the classpath - this way files can be
         * addressed with resource loader
         * addFolderToClassPath(testSetupFolder);
         */
    }

    private void addFolderToClassPath(File folder) {
        // source: https://gist.github.com/simonwoo/0338424508496b6e171e
        try {
            ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
            if (sysClassLoader instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class urlClass = URLClassLoader.class;
                Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
                method.setAccessible(true);
                method.invoke(urlClassLoader, new Object[] { folder.toURI().toURL() });
            } else {
                LOG.warn(
                        "could not add tests folder '{}' to the classpath... system class loader is not the class type we support! class loader class: {}",
                        folder, sysClassLoader.getClass());
            }

        } catch (Exception e) {
            throw new RuntimeException("Oops! It looks we failed to add the tests folder '" + folder.getAbsolutePath()
                    + "' to the classpath... error: " + e, e);
        }
    }

    public void runTests() {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        Result result = junit.run(
                JbehaveTests.class);

        resultReport(result);
    }

    public Result getResult() {
        return result;
    }

    private void resultReport(Result result) {
        LOG.info("Finished. Result: Failures: {}, Ignored: {}. Tests run: {}. Time: {} ms.", result.getFailureCount(),
                result.getIgnoreCount(), result.getRunCount(), result.getRunTime());
        int idx = 1;
        for (Failure failure : result.getFailures()) {
            LOG.error("failure #{}: {}", idx, failure);
            idx++;
        }
        this.result = result;
    }
}
