Java Microbenchmark Harness for jquante.

### QuickStart ###

## [Debian Wheezy](http://www.debian.org/releases/wheezy/) / [Ubuntu Precise](http://releases.ubuntu.com/precise/) / [Ubuntu Trusty](http://releases.ubuntu.com/trusty/)

This will install the JAMBER tools (only amber2openmm at the moment) to the Debian based OS.

    git clone https://mjw99@bitbucket.org/mjw99/jquante-jmh.git
    cd jquante-jmh
    mvn clean package ; java -jar ./target/benchmarks.jar