# [Java Microbenchmark Harness](http://openjdk.java.net/projects/code-tools/jmh/) for [JQuante](https://github.com/mjw99/JQuante) ![Travis_status](https://travis-ci.org/mjw99/jquante-jmh.svg?branch=master)

# QuickStart

## [Debian Wheezy](http://www.debian.org/releases/wheezy/) / [Ubuntu Precise](http://releases.ubuntu.com/precise/) / [Ubuntu Trusty](http://releases.ubuntu.com/trusty/)

1. Install JQuante locally
```
git clone https://github.com/mjw99/jquante.git ; cd jquante ; mvn clean install 
```
2. Compile and run the benchmark:

```
git clone https://github.com/mjw99/jquante-jmh.git
cd jquante-jmh
mvn clean package ; java -jar ./target/benchmarks.jar
```

This will produce output similar to the following:
```
# Run complete. Total time: 00:10:13

Benchmark                             Mode  Cnt      Score    Error  Units
HGPTwoElectronTermBenchmark.sd        avgt   10  11052.156 ± 80.407  ns/op
HGPTwoElectronTermBenchmark.sp        avgt   10   1106.256 ± 14.595  ns/op
HGPTwoElectronTermBenchmark.ss        avgt   10    148.472 ±  0.365  ns/op
HuzinagaTwoElectronTermBenchmark.sd   avgt   10   2235.083 ±  8.704  ns/op
HuzinagaTwoElectronTermBenchmark.sp   avgt   10   1024.677 ±  3.096  ns/op
HuzinagaTwoElectronTermBenchmark.ss   avgt   10    393.514 ±  5.337  ns/op
RysTwoElectronTermBenchmark.sd        avgt   10    848.371 ± 12.474  ns/op
RysTwoElectronTermBenchmark.sp        avgt   10    565.969 ±  2.814  ns/op
RysTwoElectronTermBenchmark.ss        avgt   10    361.427 ± 14.465  ns/op
SinglePointHFWater631gdp.doBenchmark  avgt   10      0.108 ±  0.003   s/op
SinglePointHFWaterSTO3G.doBenchmark   avgt   10      0.006 ±  0.001   s/op
SinglePointHFWaterccpvtz.doBenchmark  avgt   10      2.475 ±  0.287   s/op
```

# Advanced #

## Basic Profiling ##

Basic stack based profile:
```
java -jar ./target/benchmarks.jar -prof stack SinglePointHFWaterccpvtz
```
This will produce output similar to the following:
```
Secondary result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark:·stack":
Stack profiler:

....[Thread state distributions]....................................................................
 64.2%         WAITING
 31.6%         RUNNABLE
  4.2%         TIMED_WAITING

....[Thread state: WAITING].........................................................................
 51.7%  80.6% java.lang.Object.wait
 12.4%  19.4% jdk.internal.misc.Unsafe.park

....[Thread state: RUNNABLE]........................................................................
 13.5%  42.6% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.initialiseG
  5.1%  16.0% name.mjw.jquante.math.qm.GMatrix.lambda$makeGMatrix$0
  4.7%  14.7% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.shift
  2.5%   8.0% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.processG
  1.4%   4.4% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.selectRoots
  0.9%   2.7% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.coulombRepulsion
  0.8%   2.6% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.finaliseG
  0.3%   1.1% java.lang.Object.wait
  0.3%   0.9% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.rNode
  0.3%   0.9% java.util.stream.ForEachOps$ForEachTask.compute
  1.9%   6.0% <other>
```


## Profiling with perfasm (Ubuntu 18.04)
This profiles using [kernel based](https://en.wikipedia.org/wiki/Perf_(Linux)) counters.

1) Install userspace perf tools and kernel modules
```
sudo apt-get install linux-tools-common linux-tools-generic
```

2) Note "-XX:CompileCommand=print" will not print assembly without the hsdis-amd64.so library. To solve this under Ubuntu 18.04:
```
sudo apt-get install libhsdis0-fcml
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/jvm/java-9-openjdk-amd64/lib/amd64
```

3) Perform perfasm profile:
```
sudo sh -c 'echo -1 >/proc/sys/kernel/perf_event_paranoid'

java -jar ./target/benchmarks.jar -prof 'perfasm:intelSyntax=true;tooBigThreshold=1500;top=3' SinglePointHFWaterccpvtz
```
This will generate much more detail, including assembly code of the hot regions:

```
# Processing profiler results: LinuxPerfAsmProfiler 


Result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark":
  2.819 ±(99.9%) 0.808 s/op [Average]
  (min, avg, max) = (2.390, 2.819, 4.258), stdev = 0.535
  CI (99.9%): [2.010, 3.627] (assumes normal distribution)

Secondary result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark:·asm":
PrintAssembly processed: 1094133 total address lines.
Perf output processed (skipped 84.804 seconds):
 Column 1: cycles (1105240 events)

Hottest code regions (>10.00% "cycles" events):

....[Hottest Region 1]..............................................................................
c2, level 4, name.mjw.jquante.math.qm.GMatrix::lambda$makeGMatrix$0, version 2762 (1699 bytes) 

           0x00007f081ed53440: test    edi,edi
           0x00007f081ed53442: mov     r8d,edi
           0x00007f081ed53445: cmovnl  r8d,r11d
           0x00007f081ed53449: mov     eax,edx
           0x00007f081ed5344b: inc     eax
           0x00007f081ed5344d: imul    eax,edx           ;*imul {reexecute=0 rethrow=0 return_oop=0}
                                                         ; - name.mjw.jquante.math.qm.integral.IntegralsUtil::ijkl2intindex@30 (line 75)
                                                         ; - name.mjw.jquante.math.qm.GMatrix::lambda$makeGMatrix$0@64 (line 96)
           0x00007f081ed53450: mov     r11d,eax
           0x00007f081ed53453: sar     r11d,1fh
  0.00%    0x00007f081ed53457: shr     r11d,1fh
           0x00007f081ed5345b: add     r11d,eax
           0x00007f081ed5345e: sar     r11d,1h
           0x00007f081ed53461: add     r11d,r8d          ;*iadd {reexecute=0 rethrow=0 return_oop=0}
                                                         ; - name.mjw.jquante.math.qm.integral.IntegralsUtil::ijkl2intindex@34 (line 75)
                                                         ; - name.mjw.jquante.math.qm.GMatrix::lambda$makeGMatrix$0@64 (line 96)
  0.00%    0x00007f081ed53464: mov     dword ptr [rsp+3ch],r11d
           0x00007f081ed53469: mov     r11d,dword ptr [rsp+28h]
           0x00007f081ed5346e: inc     r11d
           0x00007f081ed53471: vmovd   xmm3,r11d
  0.00%    0x00007f081ed53476: xor     r11d,r11d
  0.00%    0x00007f081ed53479: xor     r8d,r8d

......

....................................................................................................
 14.62%  <total for region 1>

....[Hottest Regions]...............................................................................
 14.62%         c2, level 4  name.mjw.jquante.math.qm.GMatrix::lambda$makeGMatrix$0, version 2762 (1699 bytes) 
  7.35%           libjvm.so  JVM_MonitorWait (215 bytes) 
  5.12%         c2, level 4  name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::coulombRepulsion, version 2435 (1300 bytes) 
 72.92%  <...other 3533 warm regions...>
....................................................................................................
100.00%  <totals>

```

