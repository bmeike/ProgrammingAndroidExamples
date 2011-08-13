
Probably the simplest way to set up this project is to use the project and
classpath files in the tools/ide/eclipse folders.  The more complete description
is that need to set up the Finch Framework project
($root/finch/framework/FinchFramework).  You need to be sure that the directory
lib-src is on the build path.  You will also need to set up FinchWelcome as a library:

1) import FinchWelcome into your workspace
2) for FinchWelcome, check Properties > Android and verify that it is
   a library project (bottom half of the dialog)
3) in the project, use Android > Properties to add that project
   to this one, as a library
   
Using the normal eclipse project dependency mechanism will not work.

