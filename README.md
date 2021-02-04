# [Java Microbenchmark Harness](http://openjdk.java.net/projects/code-tools/jmh/) for [JQuante](https://github.com/mjw99/JQuante) ![Travis_status](https://travis-ci.org/mjw99/jquante-jmh.svg?branch=master)

# QuickStart

## [Debian Wheezy](http://www.debian.org/releases/wheezy/) / [Ubuntu Precise](http://releases.ubuntu.com/precise/) / [Ubuntu Trusty](http://releases.ubuntu.com/trusty/)

1. Install JQuante locally
```bash
git clone https://github.com/mjw99/jquante.git ; cd jquante ; mvn clean install 
```
2. Compile and run the benchmark:

```bash
git clone https://github.com/mjw99/jquante-jmh.git
cd jquante-jmh
# Run the CPU at the maximum frequency. 
sudo cpupower frequency-set -g performance
mvn clean package ; java -jar ./target/benchmarks.jar
```

This will produce output similar to the following:
```bash
Benchmark                             Mode  Cnt      Score    Error  Units
HGPTwoElectronTermBenchmark.sd        avgt   10  13225.011 ±  1.317  ns/op
HGPTwoElectronTermBenchmark.sp        avgt   10   1327.111 ±  0.622  ns/op
HGPTwoElectronTermBenchmark.ss        avgt   10    183.107 ±  1.605  ns/op
HuzinagaTwoElectronTermBenchmark.sd   avgt   10   2630.198 ±  3.026  ns/op
HuzinagaTwoElectronTermBenchmark.sp   avgt   10   1114.593 ±  4.186  ns/op
HuzinagaTwoElectronTermBenchmark.ss   avgt   10    512.553 ±  0.096  ns/op
RysTwoElectronTermBenchmark.sd        avgt   10    643.282 ±  0.783  ns/op
RysTwoElectronTermBenchmark.sp        avgt   10    377.770 ±  0.457  ns/op
RysTwoElectronTermBenchmark.ss        avgt   10    214.362 ±  0.203  ns/op
SinglePointHFWater631gdp.doBenchmark  avgt   10      0.046 ±  0.001   s/op
SinglePointHFWaterSTO3G.doBenchmark   avgt   10      0.003 ±  0.001   s/op
SinglePointHFWaterccpvtz.doBenchmark  avgt   10      1.220 ±  0.300   s/op
```

# Advanced #

## Basic Profiling ##

Basic stack based profile:
```bash
java -jar ./target/benchmarks.jar -prof stack SinglePointHFWaterccpvtz
```
This will produce output similar to the following:
```bash
Secondary result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark:·stack":
Stack profiler:

....[Thread state distributions]....................................................................
 48.4%         WAITING
 48.2%         RUNNABLE
  3.5%         TIMED_WAITING

....[Thread state: WAITING].........................................................................
 24.8%  51.3% java.lang.Object.wait
 23.5%  48.7% jdk.internal.misc.Unsafe.park

....[Thread state: RUNNABLE]........................................................................
 15.8%  32.7% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.int1d
 10.2%  21.3% name.mjw.jquante.math.qm.GMatrix.lambda$makeGMatrix$0
  3.9%   8.2% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.selectRoots
  3.7%   7.7% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.processGn
  3.0%   6.2% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.processGm
  2.9%   6.1% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.finaliseG
  1.7%   3.6% java.lang.Object.wait
  0.9%   1.9% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.rNode
  0.8%   1.7% org.hipparchus.linear.ArrayRealVector.<init>
  0.7%   1.5% java.util.stream.ForEachOps$ForEachTask.compute
  4.4%   9.2% <other>

....[Thread state: TIMED_WAITING]...................................................................
  2.9%  84.1% java.lang.Object.wait
  0.5%  15.9% jdk.internal.misc.Unsafe.park
```


## Profiling with perfasm (Ubuntu 18.04)
This profiles using [kernel based](https://en.wikipedia.org/wiki/Perf_(Linux)) counters.

1) Install userspace perf tools and kernel modules
```bash
sudo apt-get install linux-tools-common linux-tools-generic
```

2) Note "-XX:CompileCommand=print" will not print assembly without the hsdis-amd64.so library. To solve this under Ubuntu 18.04:
```bash
sudo apt-get install libhsdis0-fcml
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/jvm/java-9-openjdk-amd64/lib/amd64
```

3) Perform perfasm profile:
```bash
sudo sh -c 'echo -1 >/proc/sys/kernel/perf_event_paranoid'

java -jar ./target/benchmarks.jar -prof 'perfasm:intelSyntax=true;tooBigThreshold=1500;top=3' SinglePointHFWaterccpvtz
```
This will generate much more detail, including assembly code of the hot regions:

```asm
# Processing profiler results: LinuxPerfAsmProfiler 


Result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark":
  1.167 ±(99.9%) 0.080 s/op [Average]
  (min, avg, max) = (1.112, 1.167, 1.256), stdev = 0.053
  CI (99.9%): [1.087, 1.246] (assumes normal distribution)

Secondary result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark:·asm":
PrintAssembly processed: 1066136 total address lines.
Perf output processed (skipped 74.811 seconds):
 Column 1: cycles (1335203 events)

Hottest code regions (>10.00% "cycles" events):

....[Hottest Region 1]..............................................................................
c2, level 4, name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur, version 2359 (2468 bytes) 

                                        # parm1:    rsi       = int
                                        # parm2:    rdx       = int
                                        # parm3:    xmm1:xmm1   = double
                                        # parm4:    xmm2:xmm2   = double
                                        # parm5:    xmm3:xmm3   = double
                                        # parm6:    xmm4:xmm4   = double
                                        # parm7:    xmm5:xmm5   = double
                                        # parm8:    xmm6:xmm6   = double
                                        # parm9:    xmm7:xmm7   = double
                                        # parm10:   [sp+0xe0]   = double  (sp of caller)
  0.11%                                 0x00007f4b685a1d60: mov     dword ptr [rsp+0fffffffffffec000h],eax
                                                                                      ;   {no_reloc}
  0.14%                                 0x00007f4b685a1d67: push    rbp
  0.04%                                 0x00007f4b685a1d68: sub     rsp,0d0h          ;*synchronization entry
                                                                                      ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@-1 (line 1703)
  0.07%                                 0x00007f4b685a1d6f: vmovsd  qword ptr [rsp+80h],xmm6
  0.14%                                 0x00007f4b685a1d78: vmovsd  qword ptr [rsp+78h],xmm4
  0.08%                                 0x00007f4b685a1d7e: vmovsd  qword ptr [rsp+70h],xmm3
  0.06%                                 0x00007f4b685a1d84: vmovsd  qword ptr [rsp+68h],xmm2
  0.12%                                 0x00007f4b685a1d8a: vmovsd  qword ptr [rsp+60h],xmm1
  0.06%                                 0x00007f4b685a1d90: mov     dword ptr [rsp+58h],edx
  0.07%                                 0x00007f4b685a1d94: mov     dword ptr [rsp+28h],esi
  0.06%                                 0x00007f4b685a1d98: vmovsd  qword ptr [rsp],xmm0
  0.14%                                 0x00007f4b685a1d9d: vaddsd  xmm0,xmm5,xmm6    ;*dadd {reexecute=0 rethrow=0 return_oop=0}
                                                                                      ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@4 (line 1703)
  0.03%                                 0x00007f4b685a1da1: vaddsd  xmm1,xmm7,mmword ptr [rsp+0e0h]
  0.69%                                 0x00007f4b685a1daa: mov     r10,qword ptr [r15+318h]
                                                                                      ;*invokestatic currentThread {reexecute=0 rethrow=0 return_oop=0}
                                                                                      ; - java.lang.ThreadLocal::get@0 (line 162)
                                                                                      ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::initialiseG@3 (line 1737)
                                                                                      ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@34 (line 1706)
  0.00%                                 0x00007f4b685a1db1: mov     r10d,dword ptr [r10+4ch]
                                                                                      ;*getfield threadLocals {reexecute=0 rethrow=0 return_oop=0}
                                                                                      ; - java.lang.ThreadLocal::getMap@1 (line 254)
                                                                                      ; - java.lang.ThreadLocal::get@6 (line 163)
                                                                                      ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::initialiseG@3 (line 1737)
                                                                                      ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@34 (line 1706)
<----SNIP---->
         ↘↘                             0x00007f4b685a270a: vmovsd  qword ptr [rsp+18h],xmm1
                                        0x00007f4b685a2710: vmovsd  qword ptr [rsp+10h],xmm0
                                        0x00007f4b685a2716: vmovsd  qword ptr [rsp+8h],xmm7
                                        0x00007f4b685a271c: vmovq   rbp,xmm5
                                        0x00007f4b685a2721: mov     rsi,6260a85c8h    ;   {oop(a &apos;java/lang/ThreadLocal$SuppliedThreadLocal&apos;{0x00000006260a85c8})}
                                        0x00007f4b685a272b: call    7f4b608bdd00h     ; ImmutableOopMap{}
                                                                                      ;*invokevirtual setInitialValue {reexecute=0 rethrow=0 return_oop=1}
....................................................................................................
 32.36%  <total for region 1>

<----SNIP---->

....[Hottest Methods (after inlining)]..............................................................
 32.69%         c2, level 4  name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur, version 2359 
 26.18%         c2, level 4  name.mjw.jquante.math.qm.GMatrix::lambda$makeGMatrix$0, version 2721 
 19.26%         c2, level 4  name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::shift, version 2314 
 21.87%  <...other 309 warm methods...>
....................................................................................................
100.00%  <totals>
```
