This project depend on the project

FinchWelcome

from the directory:

../framework/FinchFramework

If it does not build, you may need to set up FinchWelcome as a library:

1) import FinchWelcome into your workspace
2) for FinchWelcome, check Properties > Android and verify that it is
   a library project (bottom half of the dialog)
3) in the project, use Android > Properties to add that project
   to this one, as a library
   
Using the normal eclipse project dependency mechanism will not work.
