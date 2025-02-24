package com.keytiles.bdd;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Result;

import com.google.common.base.Preconditions;

/**
 * This Test class has only one test - the one which bootstraps the {@link ServiceTester} and executes all configured .story files.
 * But this way you can basically just execute 'mvn test' in the project. Might be useful...
 * 
 * What is important is that you can pass the 'testsSetupFolder' as a system property so something like this:
 * 'mvn test -DtestsSetupFolder=d:/workspace/keytiles/go-projects/notification/tests/bdd-integration-test/testsuite-config'
 */
public class ExecuteAllStoriesTest {

    private final static String SYSPROP_TEST_SETUP_FOLDER = "testsSetupFolder";

    private static void validateFolderExists(File folder) {
        Preconditions.checkState(folder.exists(), "folder '%s' does not exist!", folder);
        Preconditions.checkState(folder.isDirectory(), "folder '%s' actually is not a folder... it seems it is a file", folder);
    }


    //@Test
    public void executeTests() {

        try {
            String testSetupFolderPath = System.getProperty(SYSPROP_TEST_SETUP_FOLDER);
            Preconditions.checkArgument(StringUtils.isNotBlank(testSetupFolderPath), "You did not provide the -D%s=<the folder> system property - it is mandatory to add this and tell where the test setup is!", SYSPROP_TEST_SETUP_FOLDER);
            File testSetupFolder = new File(testSetupFolderPath);
            validateFolderExists(testSetupFolder);
            File storiesFolder = new File(FilenameUtils.concat(testSetupFolderPath, ServiceTesterCmd.STORIES_SUBFOLDER));
            validateFolderExists(storiesFolder);
            File springFolder = new File(FilenameUtils.concat(testSetupFolderPath, ServiceTesterCmd.SPRING_SUBFOLDER));
            validateFolderExists(springFolder);
            File compositeStepsFolder = new File(FilenameUtils.concat(testSetupFolderPath, ServiceTesterCmd.COMPOSITE_STEPS_SUBFOLDER));
            validateFolderExists(compositeStepsFolder);
            File filesFolder = new File(FilenameUtils.concat(testSetupFolderPath, ServiceTesterCmd.FILES_SUBFOLDER));
            validateFolderExists(filesFolder);

            ServiceTester serviceTester = new ServiceTester(testSetupFolder, springFolder, storiesFolder, compositeStepsFolder, filesFolder);
            serviceTester.start();

            Result results = serviceTester.getLastResult();
            if (results == null) {
                Assert.fail("tests did not return test results... this must be a failure");
            } else if (results.getFailureCount() > 0) {
                Assert.fail("tests reported failure result - num of failures: " + results.getFailureCount());
            }

        } catch (Exception e) {
            
            Assert.fail("Tests failed with exception: " + e);
        }
    }
}
