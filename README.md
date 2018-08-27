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

Benchmark                             Mode  Cnt    Score   Error  Units
HGPTwoElectronTermBenchmark.sd        avgt   10  123.195 ± 0.630  ns/op
HGPTwoElectronTermBenchmark.sp        avgt   10   53.317 ± 0.920  ns/op
HGPTwoElectronTermBenchmark.ss        avgt   10   17.285 ± 0.327  ns/op
HuzinagaTwoElectronTermBenchmark.sd   avgt   10    7.713 ± 0.137  ns/op
HuzinagaTwoElectronTermBenchmark.sp   avgt   10    7.870 ± 0.251  ns/op
HuzinagaTwoElectronTermBenchmark.ss   avgt   10    7.534 ± 0.161  ns/op
SinglePointHFWater631gdp.doBenchmark  avgt   10    0.826 ± 0.083   s/op
SinglePointHFWaterSTO3G.doBenchmark   avgt   10    0.055 ± 0.002   s/op
SinglePointHFWaterccpvtz.doBenchmark  avgt   10   31.324 ± 1.567   s/op
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


## Profiling with perfasm (Ubuntu 16.04)
This profiles using [kernel based](https://en.wikipedia.org/wiki/Perf_(Linux)) counters.

1) Install userspace perf tools and kernel modules
```
sudo apt-get install linux-tools-common linux-tools-generic
```

2) Note "-XX:CompileCommand=print" will not print assembly without the hsdis-amd64.so library. To solve this under Ubuntu 16.04:
```
sudo apt-get install libhsdis0-fcml
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH=:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64
```

3) Perform perfasm profile:
```
java -jar ./target/benchmarks.jar -prof 'perfasm:intelSyntax=true;tooBigThreshold=1500;top=3'
```
This will generate much more detail, including assembly level code of the hot regions:

```
Hottest code regions (>10.00% "cycles" events):

....[Hottest Region 1]..............................................................................
perf-8311.map, [unknown] (772 bytes) 

 <no assembly is recorded, native region>
....................................................................................................
 35.69%   45.02%  <total for region 1>

....[Hottest Region 2]..............................................................................
c2, level 4, name.mjw.jquante.math.qm.integral.IntegralsUtil::gammaIncomplete, version 1099 (1359 bytes) 

                    0x00007fadd1696f14 (offset:  148): 0xf4f4f4f4
                    0x00007fadd1696f18 (offset:  152): 0xf4f4f4f4   0xf4f4f4f4f4f4f4f4
                    0x00007fadd1696f1c (offset:  156): 0xf4f4f4f4
                  RIP: 0x7fadd1696f20 Code size: 0x00000898
                  [Entry Point]
                  [Verified Entry Point]
                    # {method} {0x00007fad71608f00} &apos;gammaIncomplete&apos; &apos;(DD)D&apos; in &apos;name/mjw/jquante/math/qm/integral/IntegralsUtil&apos;
                    # parm0:    xmm0:xmm0   = double
                    # parm1:    xmm1:xmm1   = double
                    #           [sp+0x70]  (sp of caller)
  0.00%    0.01%    0x00007fadd1696f20: mov     dword ptr [rsp+0fffffffffffec000h],eax
                                                                  ;   {no_reloc}
  0.06%    0.02%    0x00007fadd1696f27: push    rbp
  0.00%    0.01%    0x00007fadd1696f28: sub     rsp,60h           ;*synchronization entry
                                                                  ; - name.mjw.jquante.math.qm.integral.IntegralsUtil::gammaIncomplete@-1 (line 119)
  0.05%    0.02%    0x00007fadd1696f2c: vmovsd  qword ptr [rsp],xmm1
  0.01%    0.01%    0x00007fadd1696f31: vmovsd  qword ptr [rsp+28h],xmm0
  0.00%    0.01%    0x00007fadd1696f37: mov     r13d,1h
  0.00%    0.00%    0x00007fadd1696f3d: vaddsd  xmm0,xmm0,mmword ptr [7fadd1696e98h]
                                                                  ;*dadd {reexecute=0 rethrow=0 return_oop=0}
                                                                  ; - name.mjw.jquante.math.qm.integral.IntegralsUtil::logGamma@6 (line 184)
                                                                  ; - name.mjw.jquante.math.qm.integral.IntegralsUtil::gammaIncomplete@1 (line 119)
                                                                  ;   {section_word}

```

