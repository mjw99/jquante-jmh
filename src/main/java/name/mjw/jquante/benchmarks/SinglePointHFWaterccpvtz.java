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
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import name.mjw.jquante.molecule.Atom;
import name.mjw.jquante.molecule.Molecule;
import name.mjw.jquante.molecule.impl.MoleculeImpl;
import name.mjw.jquante.math.qm.BasisFunctions;
import name.mjw.jquante.math.qm.OneElectronIntegrals;
import name.mjw.jquante.math.qm.TwoElectronIntegrals;
import name.mjw.jquante.math.qm.SCFType;
import name.mjw.jquante.math.qm.SCFMethod;
import name.mjw.jquante.math.qm.SCFMethodFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Thread;



@State(Thread)
@OutputTimeUnit(SECONDS)
@BenchmarkMode(AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)

public class SinglePointHFWaterccpvtz {

    BasisFunctions bf = null;
    Molecule water = null;

    @Setup
    public void setup(){

                // Create molecule
                Atom O = new Atom("O", 6.0, new Vector3D(0.00000000, 0.000000, 0.119748));
                Atom H1 = new Atom("H", 1.0, new Vector3D(0.00000000, 0.761561,
                                -0.478993));
                Atom H2 = new Atom("H", 1.0, new Vector3D(0.00000000, -0.761561,
                                -0.478993));

                water = new MoleculeImpl("water");
                water.addAtom(H1);
                water.addAtom(O);
                water.addAtom(H2);

                try {
                        bf = new BasisFunctions(water, "cc-pvtz");

                } catch (Exception e) {

                        e.printStackTrace();
                }



   }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE) //makes looking at assembly easier
    public void doBenchmark() {

                // compute integrals
                OneElectronIntegrals e1 = new OneElectronIntegrals(bf, water);
                TwoElectronIntegrals e2 = new TwoElectronIntegrals(bf);

                // do SCF
                SCFMethod scfm = SCFMethodFactory.getInstance().getSCFMethod(water, e1,
                                e2, SCFType.HARTREE_FOCK);
                scfm.scf();

    }

}
