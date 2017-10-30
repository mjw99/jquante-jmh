Java Microbenchmark Harness for jquante.

# QuickStart #

## [Debian Wheezy](http://www.debian.org/releases/wheezy/) / [Ubuntu Precise](http://releases.ubuntu.com/precise/) / [Ubuntu Trusty](http://releases.ubuntu.com/trusty/)

This will compile and run the benchmark:

    git clone https://mjw99@bitbucket.org/mjw99/jquante-jmh.git
    cd jquante-jmh
    mvn clean package ; java -jar ./target/benchmarks.jar

Note "-XX:CompileCommand=print" will not print assembly without hsdis-amd64.so. To solve this under Ubuntu 16.04:
```
sudo apt-get install libhsdis0-fcml
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH=:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64
java -jar ./target/benchmarks.jar
```


