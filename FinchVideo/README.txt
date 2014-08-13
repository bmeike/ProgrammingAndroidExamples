The simplest way to set up this project is to use the project and classpath
files in the tools/ide/eclipse folders.  To do this:
1) copy project and classpath files for the FinchFramework project into the
   project root as .project and .classpath, respectively.
2) import the FinchFramework project into your eclipse workspace
   (be sure to use "Import Existing Project into Workspace", not "Import
   Existing Android Code into Workspace"!)
3) repeat steps 1 and 2 for the FinchVideo project


A more complete description is that you need to set up the FinchFramework
project as an Android library:
1) Set up this project.  It will have compile errors.
2) import the FinchFramework into your workspace
3) verify that the FinchFramework project is a library project:
   check Properties > Android and look in the bottom half of the dialog
4) Use Android > Properties > Add... in the lower pane, to add the
   FinchFramework project to this one, as a library
   
Using the normal eclipse project dependency mechanism will not work!
