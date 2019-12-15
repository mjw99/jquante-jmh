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
# Run the CPU at the maximum frequency. 
sudo cpupower frequency-set -g performance
mvn clean package ; java -jar ./target/benchmarks.jar
```

This will produce output similar to the following:
```
Benchmark                             Mode  Cnt      Score    Error  Units
HGPTwoElectronTermBenchmark.sd        avgt   10  17176.001 ± 13.178  ns/op
HGPTwoElectronTermBenchmark.sp        avgt   10   1704.445 ±  9.657  ns/op
HGPTwoElectronTermBenchmark.ss        avgt   10    218.328 ±  0.025  ns/op
HuzinagaTwoElectronTermBenchmark.sd   avgt   10   2834.332 ±  1.775  ns/op
HuzinagaTwoElectronTermBenchmark.sp   avgt   10   1251.287 ±  4.440  ns/op
HuzinagaTwoElectronTermBenchmark.ss   avgt   10    480.042 ±  0.493  ns/op
RysTwoElectronTermBenchmark.sd        avgt   10   1197.050 ±  1.118  ns/op
RysTwoElectronTermBenchmark.sp        avgt   10    669.166 ±  1.553  ns/op
RysTwoElectronTermBenchmark.ss        avgt   10    416.458 ±  0.298  ns/op
SinglePointHFWater631gdp.doBenchmark  avgt   10      0.213 ±  0.002   s/op
SinglePointHFWaterSTO3G.doBenchmark   avgt   10      0.011 ±  0.001   s/op
SinglePointHFWaterccpvtz.doBenchmark  avgt   10      5.864 ±  0.093   s/op
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
 68.8%         WAITING
 24.8%         RUNNABLE
  6.5%         TIMED_WAITING

....[Thread state: WAITING].........................................................................
 63.9%  92.9% jdk.internal.misc.Unsafe.park
  4.9%   7.1% java.lang.Object.wait

....[Thread state: RUNNABLE]........................................................................
 11.4%  46.1% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.initialiseG
  5.7%  22.9% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.recur
  1.9%   7.6% name.mjw.jquante.math.qm.GMatrix.lambda$null$0
  1.6%   6.3% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.shift
  1.2%   5.0% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.selectRoots
  0.4%   1.6% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.processG
  0.4%   1.5% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.finaliseG
  0.3%   1.3% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.rNode
  0.3%   1.0% java.lang.Object.wait
  0.2%   0.8% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.int1d
  1.4%   5.7% <other>
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
  5.273 ±(99.9%) 0.112 s/op [Average]
  (min, avg, max) = (5.154, 5.273, 5.405), stdev = 0.074
  CI (99.9%): [5.160, 5.385] (assumes normal distribution)

Secondary result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark:·asm":
PrintAssembly processed: 879179 total address lines.
Perf output processed (skipped 68.650 seconds):
 Column 1: cycles (380574 events)

Hottest code regions (>10.00% "cycles" events):

....[Hottest Region 1]..............................................................................
c2, level 4, name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur, version 1907 (3373 bytes)

           # parm3:    rcx       = int
           # parm4:    r8        = int
           # parm5:    xmm1:xmm1   = double
           # parm6:    xmm2:xmm2   = double
           # parm7:    xmm3:xmm3   = double
           # parm8:    xmm4:xmm4   = double
           # parm9:    xmm5:xmm5   = double
           # parm10:   xmm6:xmm6   = double
           # parm11:   xmm7:xmm7   = double
           # parm12:   [sp+0xd0]   = double  (sp of caller)
  0.08%    0x00007f43ab7ca4a0: mov     dword ptr [rsp+0fffffffffffec000h],eax
                                                         ;   {no_reloc}
  0.04%    0x00007f43ab7ca4a7: push    rbp
  0.03%    0x00007f43ab7ca4a8: sub     rsp,0c0h          ;*synchronization entry
                                                         ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@-1 (line 1687)
  0.08%    0x00007f43ab7ca4af: vmovsd  qword ptr [rsp+40h],xmm4
  0.01%    0x00007f43ab7ca4b5: vmovsd  qword ptr [rsp+38h],xmm3
  0.02%    0x00007f43ab7ca4bb: vmovsd  qword ptr [rsp+30h],xmm2
  0.05%    0x00007f43ab7ca4c1: vmovsd  qword ptr [rsp+28h],xmm1
  0.04%    0x00007f43ab7ca4c7: vmovsd  qword ptr [rsp+58h],xmm7
  0.01%    0x00007f43ab7ca4cd: vmovsd  qword ptr [rsp+50h],xmm6
  0.03%    0x00007f43ab7ca4d3: vmovsd  qword ptr [rsp+48h],xmm5
  0.05%    0x00007f43ab7ca4d9: add     esi,edx           ;*iadd {reexecute=0 rethrow=0 return_oop=0}
                                                         ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@175 (line 1703)
  0.03%    0x00007f43ab7ca4db: add     ecx,r8d           ;*iadd {reexecute=0 rethrow=0 return_oop=0}
                                                         ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@182 (line 1704)
  0.01%    0x00007f43ab7ca4de: vmulsd  xmm1,xmm4,mmword ptr [rsp+0d0h]

......
....................................................................................................
 35.33%  <total for region 1>

....[Hottest Regions]...............................................................................
 35.33%         c2, level 4  name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur, version 1907 (3373 bytes)
  9.02%         c2, level 4  name.mjw.jquante.math.qm.GMatrix::lambda$null$0, version 2284 (1639 bytes)
  6.45%         c2, level 4  name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::shift, version 1934 (2757 bytes)
 49.20%  <...other 2325 warm regions...>
....................................................................................................
100.00%  <totals>


```

