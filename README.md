# [Java Microbenchmark Harness](http://openjdk.java.net/projects/code-tools/jmh/) for [JQuante](https://github.com/mjw99/JQuante) ![Travis_status](https://travis-ci.org/mjw99/jquante-jmh.svg?branch=master)

# QuickStart

## [Debian Bullseye](https://www.debian.org/releases/bullseye/) 

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
HGPTwoElectronTermBenchmark.sd        avgt   10  13245.889 ± 23.475  ns/op
HGPTwoElectronTermBenchmark.sp        avgt   10   1315.202 ±  3.614  ns/op
HGPTwoElectronTermBenchmark.ss        avgt   10    182.214 ±  0.255  ns/op
HuzinagaTwoElectronTermBenchmark.sd   avgt   10   2708.537 ±  9.896  ns/op
HuzinagaTwoElectronTermBenchmark.sp   avgt   10   1131.101 ±  6.763  ns/op
HuzinagaTwoElectronTermBenchmark.ss   avgt   10    506.775 ±  0.991  ns/op
RysTwoElectronTermBenchmark.sd        avgt   10    629.575 ±  3.787  ns/op
RysTwoElectronTermBenchmark.sp        avgt   10    337.277 ±  1.054  ns/op
RysTwoElectronTermBenchmark.ss        avgt   10    185.916 ±  0.661  ns/op
SinglePointHFWater631gdp.doBenchmark  avgt   10      0.041 ±  0.001   s/op
SinglePointHFWaterSTO3G.doBenchmark   avgt   10      0.003 ±  0.001   s/op
SinglePointHFWaterccpvtz.doBenchmark  avgt   10      1.132 ±  0.044   s/op
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
 72.1%         RUNNABLE
 21.9%         WAITING
  6.1%         TIMED_WAITING

....[Thread state: RUNNABLE]........................................................................
 23.8%  33.0% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.int1d
 16.0%  22.1% name.mjw.jquante.math.qm.GMatrix.lambda$makeGMatrix$0
  5.6%   7.7% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.processGn
  5.0%   7.0% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.selectRoots
  5.0%   6.9% <stack is empty, everything is filtered?>
  4.3%   6.0% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.finaliseG
  4.3%   6.0% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.processGm
  1.3%   1.8% name.mjw.jquante.math.qm.integral.RysTwoElectronTerm.rNode
  1.0%   1.4% java.util.stream.ForEachOps$ForEachTask.compute
  0.9%   1.3% org.hipparchus.linear.ArrayRealVector.dotProduct
  4.9%   6.8% <other>

....[Thread state: WAITING].........................................................................
 21.9% 100.0% jdk.internal.misc.Unsafe.park

....[Thread state: TIMED_WAITING]...................................................................
  5.0%  82.5% java.lang.Object.wait
  1.1%  17.5% jdk.internal.misc.Unsafe.park
```


## Profiling with perfasm (Ubuntu 22.04)
This profiles using [kernel based](https://en.wikipedia.org/wiki/Perf_(Linux)) counters.

1) Install userspace perf tools and kernel modules
```bash
sudo apt-get install linux-tools-common linux-tools-generic
```

2) Note "-XX:CompileCommand=print" will not print assembly without the hsdis-amd64.so library. To solve this under Ubuntu 20.04:
```bash
sudo apt-get install libhsdis0-fcml
```
Or, one can obtain a more recent version from [here](https://builds.shipilev.net/hsdis/) or [here](https://chriswhocodes.com/hsdis/).

3) Perform perfasm profile:
```bash
sudo sh -c 'echo -1 >/proc/sys/kernel/perf_event_paranoid'

java -jar ./target/benchmarks.jar -prof 'perfasm:intelSyntax=true;tooBigThreshold=1500;top=3' SinglePointHFWaterccpvtz
```
This will generate much more detail, including assembly code of the hot regions:

```asm
# Processing profiler results: LinuxPerfAsmProfiler 


Result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark":
  1.136 ±(99.9%) 0.033 s/op [Average]
  (min, avg, max) = (1.093, 1.136, 1.167), stdev = 0.022
  CI (99.9%): [1.103, 1.168] (assumes normal distribution)

Secondary result "name.mjw.jquante.benchmarks.SinglePointHFWaterccpvtz.doBenchmark:·asm":
PrintAssembly processed: 1103591 total address lines.
Perf output processed (skipped 63.433 seconds):
 Column 1: cycles (1333215 events)

Hottest code regions (>10.00% "cycles" events):
 Event counts are percents of total event count.

....[Hottest Region 1]..............................................................................
c2, level 4, name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d, version 2, compile id 2291

                             # parm11:   xmm7:xmm7   = double
                             # parm12:   [sp+0x110]   = double  (sp of caller)
                             0x00007f92b4e6e2c0:   mov    r10d,DWORD PTR [rsi+0x8]     ;   {no_reloc}
                             0x00007f92b4e6e2c4:   movabs r11,0x800000000
                             0x00007f92b4e6e2ce:   add    r10,r11
                             0x00007f92b4e6e2d1:   cmp    rax,r10
                             0x00007f92b4e6e2d4:   jne    0x00007f92b45edd80           ;   {runtime_call ic_miss_stub}
                             0x00007f92b4e6e2da:   xchg   ax,ax
                             0x00007f92b4e6e2dc:   nop    DWORD PTR [rax+0x0]
                           [Verified Entry Point]
   0.03%                     0x00007f92b4e6e2e0:   mov    DWORD PTR [rsp-0x14000],eax
   0.09%                     0x00007f92b4e6e2e7:   push   rbp
   0.04%                     0x00007f92b4e6e2e8:   sub    rsp,0x100                    ;*synchronization entry
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d@-1 (line 1688)
   0.09%                     0x00007f92b4e6e2ef:   vmovsd QWORD PTR [rsp+0x68],xmm6
   0.10%                     0x00007f92b4e6e2f5:   vmovsd QWORD PTR [rsp+0x30],xmm0
   0.05%                     0x00007f92b4e6e2fb:   vmovsd QWORD PTR [rsp+0x28],xmm4
   0.04%                     0x00007f92b4e6e301:   vmovsd QWORD PTR [rsp+0x20],xmm3
   0.04%                     0x00007f92b4e6e307:   vmovsd QWORD PTR [rsp+0x18],xmm2
   0.12%                     0x00007f92b4e6e30d:   vmovsd QWORD PTR [rsp+0x10],xmm1
   0.07%                     0x00007f92b4e6e313:   mov    DWORD PTR [rsp+0xc],r9d
   0.09%                     0x00007f92b4e6e318:   mov    DWORD PTR [rsp+0x8],r8d
   0.08%                     0x00007f92b4e6e31d:   mov    DWORD PTR [rsp+0x4],ecx
   0.17%                     0x00007f92b4e6e321:   mov    DWORD PTR [rsp],edx
   0.06%                     0x00007f92b4e6e324:   mov    r8d,edx
   0.00%                     0x00007f92b4e6e327:   add    r8d,ecx                      ;*iadd {reexecute=0 rethrow=0 return_oop=0}
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d@3 (line 1688)
   0.00%                     0x00007f92b4e6e32a:   mov    r10d,DWORD PTR [rsp+0x8]
   0.70%                     0x00007f92b4e6e32f:   add    r10d,r9d                     ;*iadd {reexecute=0 rethrow=0 return_oop=0}
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d@10 (line 1689)
   0.00%                     0x00007f92b4e6e332:   vaddsd xmm6,xmm5,xmm6               ;*dadd {reexecute=0 rethrow=0 return_oop=0}
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@4 (line 1703)
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d@34 (line 1691)
   0.01%                     0x00007f92b4e6e336:   vmovapd xmm0,xmm7
   0.01%                     0x00007f92b4e6e33a:   vaddsd xmm7,xmm7,QWORD PTR [rsp+0x110]
   0.25%                     0x00007f92b4e6e343:   mov    r11,QWORD PTR [r15+0x280]
   0.03%                     0x00007f92b4e6e34a:   mov    rsi,QWORD PTR [r11]          ;*invokestatic currentThread {reexecute=0 rethrow=0 return_oop=0}
                                                                                       ; - java.lang.ThreadLocal::get@0 (line 162)
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::initialiseG@3 (line 1737)
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@34 (line 1706)
                                                                                       ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d@34 (line 1691)

<----SNIP---->

   0.03%                     0x00007f92b4e6e9fa:   cmp    r9d,0x1
   0.01%                ╭    0x00007f92b4e6e9fe:   jbe    0x00007f92b4e6ea1a
   0.03%                │    0x00007f92b4e6ea00:   movsxd rcx,r9d
   0.00%                │    0x00007f92b4e6ea03:   cmp    rbp,rcx
   0.02%                │╭   0x00007f92b4e6ea06:   jae    0x00007f92b4e6ea1a
                        ││   0x00007f92b4e6ea08:   test   r9d,r9d
   0.01%                ││╭  0x00007f92b4e6ea0b:   jbe    0x00007f92b4e6ea1a
   0.00%                │││  0x00007f92b4e6ea0d:   add    rdx,0xfffffffffffffffe
   0.00%                │││  0x00007f92b4e6ea11:   cmp    rdx,rcx
   0.01%                │││  0x00007f92b4e6ea14:   jb     0x00007f92b4e6eae9           ;*if_icmpge {reexecute=0 rethrow=0 return_oop=0}
                        │││                                                            ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::finaliseG@20 (line 1776)
                        │││                                                            ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::recur@229 (line 1729)
                        │││                                                            ; - name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d@34 (line 1691)
                        ↘↘↘  0x00007f92b4e6ea1a:   mov    esi,0xffffff76
                             0x00007f92b4e6ea1f:   mov    ebp,DWORD PTR [rsp]
                             0x00007f92b4e6ea22:   mov    r11d,DWORD PTR [rsp+0x4]
                             0x00007f92b4e6ea27:   mov    DWORD PTR [rsp+0x110],r11d
                             0x00007f92b4e6ea2f:   mov    r9d,DWORD PTR [rsp+0x8]
                             0x00007f92b4e6ea34:   mov    DWORD PTR [rsp+0x114],r9d
                             0x00007f92b4e6ea3c:   mov    r11d,DWORD PTR [rsp+0xc]
....................................................................................................
  30.59%  <total for region 1>


<----SNIP---->


....[Hottest Methods (after inlining)]..............................................................
  33.01%         c2, level 4  name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::int1d, version 2, compile id 2291
  27.41%         c2, level 4  name.mjw.jquante.math.qm.GMatrix::lambda$makeGMatrix$0, version 3, compile id 2654
  19.00%         c2, level 4  name.mjw.jquante.math.qm.integral.RysTwoElectronTerm::shift, version 2, compile id 2232
  20.58%  <...other 228 warm methods...>
....................................................................................................
 100.00%  <totals>

```
