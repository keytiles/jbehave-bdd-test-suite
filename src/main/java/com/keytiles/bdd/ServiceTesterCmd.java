package com.keytiles.bdd;

import com.google.common.base.Preconditions;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ServiceTesterCmd {

    private final static Logger LOG = LoggerFactory.getLogger(ServiceTesterCmd.class);

    private final static String OPT_TEST_SETUP_FOLDER = "testsSetupFolder";

    public final static String STORIES_SUBFOLDER = "stories";
    public final static String SPRING_SUBFOLDER = "spring";
    public final static String COMPOSITE_STEPS_SUBFOLDER = "compositeSteps";
    public final static String FILES_SUBFOLDER = "files";

    private static void validateFolderExists(File folder) {
        Preconditions.checkState(folder.exists(), "folder '%s' does not exist!", folder);
        Preconditions.checkState(folder.isDirectory(), "folder '%s' actually is not a folder... it seems it is a file", folder);
    }

    public static void main(String[] args) {
        LOG.info("hello! tester is starting...");

        Options opts = new Options() //
                .addOption(Option.builder("h").desc("displays help").hasArg(false).build())
                .addOption(Option.builder(OPT_TEST_SETUP_FOLDER).desc("The folder where the test setup files can be found (Spring .xml and .properties files)").hasArg(true).required().build());

        CommandLineParser cmdParser = new DefaultParser();
        CommandLine cmdLine;

        try {
            cmdLine = cmdParser.parse(opts, args);

            if (cmdLine.hasOption("h")) {
                printUsage(ServiceTesterCmd.class.getCanonicalName(), opts, System.out);
                return;
            }

            String testSetupFolderPath = cmdLine.getOptionValue(OPT_TEST_SETUP_FOLDER);
            File testSetupFolder = new File(testSetupFolderPath);
            validateFolderExists(testSetupFolder);
            File storiesFolder = new File(FilenameUtils.concat(testSetupFolderPath, STORIES_SUBFOLDER));
            validateFolderExists(storiesFolder);
            File springFolder = new File(FilenameUtils.concat(testSetupFolderPath, SPRING_SUBFOLDER));
            validateFolderExists(springFolder);
            File compositeStepsFolder = new File(FilenameUtils.concat(testSetupFolderPath, COMPOSITE_STEPS_SUBFOLDER));
            validateFolderExists(compositeStepsFolder);
            File filesFolder = new File(FilenameUtils.concat(testSetupFolderPath, FILES_SUBFOLDER));
            validateFolderExists(filesFolder);

            ServiceTester serviceTester = new ServiceTester(testSetupFolder, springFolder, storiesFolder, compositeStepsFolder, filesFolder);
            serviceTester.start();

        } catch (ParseException pe) {
            printUsage(ServiceTesterCmd.class.getCanonicalName(), opts, System.out);
        } catch (Exception e) {
            LOG.error("error occured during startup! shutting down...", e);
        }
    }

    private static void printUsage(final String applicationName, final Options options, final OutputStream out) {
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.setNewLine("\n");
        usageFormatter.printUsage(writer, 80, applicationName, options);
        usageFormatter.printOptions(writer, 80, options, 2, 4);
        writer.close();
    }
}
