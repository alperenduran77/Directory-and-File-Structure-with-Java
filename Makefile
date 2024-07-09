# Define a variable for compiler flags (JFLAGS)
# -g : generate all debugging info
# -d : specify where to place generated class files
JFLAGS = -g -d ./bin

# Define a variable for the Java compiler
JC = javac

# Define a variable for the Java documentation generator
JD = javadoc

# Define where to place the generated documentation
DOCDIR = ./doc

# Clear any default targets for building .class files from .java
.SUFFIXES: .java .class

# Define a rule for how a .class file is created from a .java file
.java.class:
	$(JC) $(JFLAGS) $*.java

# Define the classes that are part of the application
CLASSES = \
	Directory.java \
	File.java \
	FileSystem.java \
	FileSystemElement.java \
	Main.java

# The default make target entry
default: classes javadoc run

# This target entry builds all the classes
classes: $(CLASSES:.java=.class)

# This target entry runs the application
run: $(CLASSES:.java=.class)
	java -cp ./bin Main

# This target entry creates the Javadoc
javadoc: $(CLASSES)
	$(JD) -d $(DOCDIR) $(CLASSES)

# Define a clean action
clean:
	$(RM) -r ./bin/*.class
	$(RM) -r $(DOCDIR)

# Tell make that "clean", "run", "default", and "javadoc" are not file names!
.PHONY: default run classes clean javadoc
