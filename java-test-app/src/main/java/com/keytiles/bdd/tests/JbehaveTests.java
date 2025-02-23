package com.keytiles.bdd.tests;

import com.google.common.collect.Lists;
import com.keytiles.bdd.plugins.MillisTimestampJBehaveTableValuePlugin;
import com.sixt.tool.bdd_testsuite.CommonJBehaveTest;
import com.sixt.tool.bdd_testsuite.jbehave.common.JBehaveTableValuePluginProvider;
import com.sixt.tool.bdd_testsuite.jbehave.common.JBehaveUtil;
import com.sixt.tool.bdd_testsuite.jbehave.plugin.EmptyStringJBehaveTableValuePlugin;
import com.sixt.tool.bdd_testsuite.jbehave.plugin.JBehaveTableValuePlugin;
import com.sixt.tool.bdd_testsuite.jbehave.plugin.ObjectStoreJBehaveTableValuePlugin;
import com.sixt.tool.bdd_testsuite.jbehave.steps.DockerSteps;
import com.sixt.tool.bdd_testsuite.jbehave.steps.HttpSteps;
import com.sixt.tool.bdd_testsuite.jbehave.steps.WaitSteps;
import com.sixt.tool.bdd_testsuite.jbehave.steps.WiremockSteps;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.io.LoadFromRelativeFile;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

public class JbehaveTests extends CommonJBehaveTest {

    private final static Logger LOG = LoggerFactory.getLogger(JbehaveTests.class);

    public JbehaveTests() {
        super();
    }

    protected List<String> storyPaths() {
        File storiesFolder = TestsRunner.storiesFolder;
        String baseFolderPath = TestsRunner.testSetupFolder.getAbsolutePath();
        // we need all files in it recursively!
        String[] extensions = {"story"};
        Iterator<File> filesIterator = FileUtils.iterateFiles(storiesFolder, extensions, true);
        List<String> storyPaths = new ArrayList<>();
        while(filesIterator.hasNext()) {
            File file = filesIterator.next();
            // we need a relative path to the stories folder
            String relPath = file.getAbsolutePath();
            relPath = relPath.substring(baseFolderPath.length() + 1);
            storyPaths.add(relPath);
        }

        return storyPaths;
    }

    protected List<String> compositeStepsPaths() {
        List<String> compositeStepsPaths = new ArrayList<>();
        File compositeStepsFolder = TestsRunner.compositeStepsFolder;
        if(compositeStepsFolder.exists()) {
            String baseFolderPath = TestsRunner.testSetupFolder.getAbsolutePath();
            // we need all files in it recursively!
            String[] extensions = {"steps", "story"};
            Iterator<File> filesIterator = FileUtils.iterateFiles(compositeStepsFolder, extensions, true);

            while(filesIterator.hasNext()) {
                File file = filesIterator.next();
                // we need a relative path to the stories folder
                String relPath = file.getAbsolutePath();
                relPath = relPath.substring(baseFolderPath.length() + 1);
                compositeStepsPaths.add(relPath);
            }
        }
        return compositeStepsPaths;
    }


    @Override
    protected void customizeConfiguration(Configuration configuration) {
        try {
            configuration //
                    //.useStoryLoader(new LoadFromRelativeFile(TestsRunner.storiesFolder.toURI().toURL())) //
                    .useStoryLoader(new LoadFromRelativeFile(TestsRunner.testSetupFolder.toURI().toURL())) //
                    .useCompositePaths(new HashSet<>(compositeStepsPaths()));

        } catch (MalformedURLException e) {
            throw new IllegalStateException("Oops we failed to configure jBehave! It looks there is a problem with 'stories' folder... "+e, e);
        }
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        AbstractApplicationContext testsApplicationContext = TestsRunner.springServiceContainerContext;

        Map<String, JBehaveTableValuePlugin> jBehaveTableValuePlugins = new HashMap<>();
        jBehaveTableValuePlugins.put("store", new ObjectStoreJBehaveTableValuePlugin());
        jBehaveTableValuePlugins.put("emptyString", new EmptyStringJBehaveTableValuePlugin());
        jBehaveTableValuePlugins.put("millisTS", new MillisTimestampJBehaveTableValuePlugin());

        JBehaveTableValuePluginProvider pluginProvider = new JBehaveTableValuePluginProvider(jBehaveTableValuePlugins, testsApplicationContext);
        JBehaveUtil jbehaveUtil = new JBehaveUtil(testsApplicationContext, pluginProvider);

        InjectableStepsFactory stepsFactory = new InstanceStepsFactory(configuration(), Lists.newArrayList( //
                WiremockSteps.createFromContext(testsApplicationContext, jbehaveUtil),
                HttpSteps.createFromContext(testsApplicationContext, jbehaveUtil),
                DockerSteps.createFromContext(testsApplicationContext, jbehaveUtil),
                WaitSteps.createFromContext(testsApplicationContext, jbehaveUtil)
        ));
        return stepsFactory;
    }

    public void runTests() {
        LOG.info("starting tests!");
    }
}
