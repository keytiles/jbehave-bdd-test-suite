# What is this?

This folder contains an example test setup. In this example we add a Go application as test subject. (Later maybe Python or any other stuff can be added too)

You can find some details here:
https://keytiles.atlassian.net/wiki/spaces/KEYTILES/pages/208961537/BDD+integration-testing+a+Go+microservice


# Folders

 * [spring](spring/) - Contains files for Spring XML. The main file is "tests.context.xml" which boots up and wires together the testing environment.
 * [stories](stories/) - Contains JBehave .story files. All of them will be executed.
 * [files](files/) - You can put .json, .txt, etc files here which then you can refer in into your .story files
 * [compositeSteps](compositeSteps/) - A cool feature of JBehave that you can create composite steps - sometimes veeery useful to bind boilerplate steps together. See the README there!
 