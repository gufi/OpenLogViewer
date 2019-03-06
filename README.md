# OpenLogViewer

OpenLogViewer, or OLV for short, is an open source project originally conceived
by Bryan Harris. He says that he created it due to boredom, but that he intends
for it to be reasonably easy to use. Ben Fenner, and to a lesser extent, Fred
Cooke have done significant work to turn that dream into reality with UI and
performance enhancements across the board.

Gufi, as Bryan is known to his friends, would love for the program to become
mainstream in the Automotive Datalog area as a tool. We feel that it's well on
its way to doing just that.

### Where do I get it?

Download the latest release from the official [OLV website](http://olv.diyefi.org)
or get the latest development version from the [build server!](http://builds.freeems.org)
. Until 0.0.3 is released, the development version is a far better choice.

### Prerequisites

#### Running a precompiled version

For using the precompiled application you only need a Java Runtime Environment
(JRE) version 6 or above.

#### Building the latest from source

If you want to build the latest from source to make customisations, then you will
need a Java Development Kit (JDK) version 6 or above AND maven 3. Maven 2 is
not suitable as the project is configured for a number of plugins which are not
compatible with maven 2. Additionally maven 2 does not provide the 100% stable
build environment, which maven 3 does. Get maven from this link and follow the
install instructions further down the page.

http://maven.apache.org/download.html

Additionally, you need to install the included settings.xml file in your ~/.m2
directory. This allows your build to include artifacts that are only available
from the DIYEFI.org Maven repository.

You can get the source from one of the following repositories; use the network
function to figure out whose is newest (though not necessarily best!):

https://github.com/FreeEMS/OpenLogViewer - Tested commits
https://github.com/fredcooke/OpenLogViewer - Latest from Fred
https://github.com/BenFenner/OpenLogViewer - Latest from Ben

A direct link to the network function:

https://github.com/FreeEMS/OpenLogViewer/network

Use git to obtain the source in an efficient way, for example:

git clone git://github.com/FreeEMS/OpenLogViewer.git

#### Using Ant

Ant is deprecated in the professional development world, for good reasons, and
as such is not supported as a build tool for this project.

### Running OLV

To run an OLV jar in a graphical desktop environment such as Microsoft Windows
Gnome, KDE, or Mac OS X, simply double click the jar file. To run it from the
command line use the following command:

java -jar OLV.jar

Where "OLV.jar" is replaced with the name of the file that you downloaded.

If you're using Maven to build from source and run OLV, then the following
simple command is all you need:

mvn -DskipTests

This does the same as "mvn install" and "mvn exec:exec" back to back. You may
wish to clean the build directory before building and running, as you'd expect
the required command is simply this:

mvn clean

Up to date documentation can be generated using maven in the following way:

mvn site:site

You can find the html index file in the target/site/ directory once complete.

### OLV Website

If you don't want to build the latest from source then you can pick up the last
release from the extremely basic OLV website below. Automatically generated
documentation is available from the same site.

http://olv.diyefi.org

### OLV Status

Currently the application has an excellent mouse-based navigation UI. Adding
and associating fields with traces is still sub-par, but should be fixed soon.

The OLV development team are commited to regular releases of working software.
This is the agile way, and as such have a clear roadmap for future releases.

http://issues.freeems.org/roadmap_page.php?project_id=15

Thanks for using this tool! We hope you love it as much as we do.

The OLV Team.

