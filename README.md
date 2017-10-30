Java Microbenchmark Harness for jquante.

# QuickStart #

## [Debian Wheezy](http://www.debian.org/releases/wheezy/) / [Ubuntu Precise](http://releases.ubuntu.com/precise/) / [Ubuntu Trusty](http://releases.ubuntu.com/trusty/)

This will compile and run the benchmark:

```
git clone https://mjw99@bitbucket.org/mjw99/jquante-jmh.git
cd jquante-jmh
mvn clean package ; java -jar ./target/benchmarks.jar
```


# Advanced #

## Basic Profiling ##

Basic stack based profile:
```
java -jar ./target/benchmarks.jar -prof stack
```
This will produce output similar to the following:
```
Secondary result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark:·stack":
Stack profiler:

....[Thread state distributions]....................................................................
 74.7%         RUNNABLE
 12.9%         TIMED_WAITING
 12.3%         WAITING

....[Thread state: RUNNABLE]........................................................................
 72.8%  97.4% name.mjw.jquante.math.qm.integral.IntegralsUtil.computeFGamma
  0.7%   1.0% name.mjw.jquante.math.qm.integral.HuzinagaTwoElectronTerm.coulomb
  0.4%   0.5% name.mjw.jquante.math.qm.integral.HuzinagaTwoElectronTerm.constructBArray
  0.2%   0.3% java.util.stream.AbstractPipeline.copyInto
  0.2%   0.2% name.mjw.jquante.math.qm.GMatrix.compute
  0.1%   0.1% name.mjw.jquante.math.qm.integral.HuzinagaTwoElectronTerm.coulombRepulsion
  0.1%   0.1% <stack is empty, everything is filtered?>
  0.0%   0.1% name.mjw.jquante.math.qm.integral.Integrals.coulomb
  0.0%   0.0% name.mjw.jquante.math.qm.GMatrix.makeGMatrix
  0.0%   0.0% name.mjw.jquante.math.qm.integral.HuzinagaTwoElectronTerm.functionB
  0.2%   0.3% <other>

```


## Profiling with perfasm ##
This profiles using [kernel based](https://en.wikipedia.org/wiki/Perf_(Linux) counters.

1) Install userspace perf tools and kernel modules
```
sudo apt-get install linux-tools-common
sudo apt-get install linux-tools-generic
sudo apt-get install linux-tools-4.4.0-96-generic
```

2) Note "-XX:CompileCommand=print" will not print assembly without hsdis-amd64.so. To solve this under Ubuntu 16.04:
```
sudo apt-get install libhsdis0-fcml
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH=:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64
java -jar ./target/benchmarks.jar
```

3)
Basic stack perfasm profile:
```
java -jar ./target/benchmarks.jar -prof perfasm
```

