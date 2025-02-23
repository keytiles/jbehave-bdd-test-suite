# jbehave-bdd-test-suite

Brings Gherkin language based .story files to facilitate no-code tests at Integration test level against Java/Go services.

# How to use

Don't worry you don't need to do any Java coding! ;-) But you will need to be able to execute Java code. 

Follow these steps to get it work:
 1. Download and install Java 20+ runtime evironment
    For you actually JRE is enough but you can also download JDK (Java Development Kit) - does not matter
	1. Go to Oracle (https://www.oracle.com/java/technologies/downloads/) or OpenJdk (https://jdk.java.net/20/) and fetch the .zip or .tar.gz packaged format
	1. Unzip somewhere
	1. Assuming you unzipped into folder named "<java-20-folder>" make sure you add the "<java-20-folder>/bin" subfolder to the path
	1. Open a shell and test: "java -version" should work and return the version info
 1. You need the .jar of this tool. You need to compile it (with Maven) or you can also download precompiled .jar-s (with all dependencies, Java 20 compile) available on our Nexus:
    * For release candidates: https://nexus.keytiles.com/nexus/content/repositories/public-snapshots/com/keytiles/tool/jbehave-bdd-test-suite/
    * For releases: https://nexus.keytiles.com/nexus/content/repositories/public-releases/com/keytiles/tool/jbehave-bdd-test-suite/
 1. You need to put together a test setup in a folder on your machine.  
    It follows a certain format and content - see below!
 1. Start the tool with "java -jar <path-to-jar-file> -testsSetupFolder <path-to-tests-setup-folder"

# Test setup folder

TODO document how it works!
