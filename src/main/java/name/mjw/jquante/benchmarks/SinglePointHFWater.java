/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package name.mjw.jquante.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import name.mjw.jquante.molecule.Molecule;
import name.mjw.jquante.math.qm.basis.BasisSetLibrary;
import name.mjw.jquante.math.qm.OneElectronIntegrals;
import name.mjw.jquante.math.qm.TwoElectronIntegrals;
import name.mjw.jquante.math.qm.SCFType;
import name.mjw.jquante.math.qm.SCFMethod;
import name.mjw.jquante.math.qm.SCFMethodFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

/**
 * Full single-point Hartree-Fock energy of water, parameterised over the basis
 * set. JMH runs {@link #doBenchmark()} once per {@code basisSet} value.
 */
@State(Thread)
@OutputTimeUnit(SECONDS)
@BenchmarkMode(AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class SinglePointHFWater {

    @Param({ "sto-3g", "6-31gss", "cc-pvtz" })
    public String basisSet;

    BasisSetLibrary bsl = null;
    Molecule water = null;

    @Setup
    public void setup() throws Exception {

        water = Geometry.water();

        bsl = new BasisSetLibrary(water, basisSet);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE) // makes looking at assembly easier
    public double doBenchmark() {

        // compute integrals
        OneElectronIntegrals e1 = new OneElectronIntegrals(bsl, water);
        TwoElectronIntegrals e2 = new TwoElectronIntegrals(bsl);

        // do SCF
        SCFMethod scfm = SCFMethodFactory.getInstance().getSCFMethod(water, e1,
                e2, SCFType.HARTREE_FOCK);
        scfm.scf();

        // return the converged energy so JMH sinks it; guards against
        // dead-code elimination of the whole SCF
        return scfm.getEnergy();
    }

}
