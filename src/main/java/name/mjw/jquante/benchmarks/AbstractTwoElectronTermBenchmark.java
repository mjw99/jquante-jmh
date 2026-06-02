package name.mjw.jquante.benchmarks;

import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import org.hipparchus.geometry.euclidean.threed.Vector3D;

import name.mjw.jquante.math.qm.basis.BasisSetLibrary;
import name.mjw.jquante.math.qm.basis.ContractedGaussian;
import name.mjw.jquante.math.qm.integral.TwoElectronTerm;
import name.mjw.jquante.molecule.Atom;
import name.mjw.jquante.molecule.Molecule;
import name.mjw.jquante.molecule.impl.MoleculeImpl;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

/**
 * Shared driver for the {@link TwoElectronTerm} coulomb() benchmarks. Concrete
 * subclasses only supply the implementation under test via
 * {@link #newTwoElectronTerm()}; JMH discovers the {@code ss}/{@code sp}/
 * {@code sd} benchmarks on each concrete subclass.
 *
 * <p>The quartets are built from <em>real</em> contracted Gaussians taken from a
 * water 6-31G** basis rather than single-primitive functions all sharing the
 * origin. This matters because coincident centres drive the inter-electron
 * separation (and hence the Boys/Rys argument) to zero, which only ever
 * exercises the cheapest {@code T -> 0} special-case branch of each integral
 * package, and a single primitive removes the contraction loop that dominates
 * real integral cost. Using displaced, multi-primitive functions makes the
 * timings representative of the integrals an SCF actually evaluates.
 */
@State(Thread)
@OutputTimeUnit(NANOSECONDS)
@BenchmarkMode(AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public abstract class AbstractTwoElectronTermBenchmark {

	TwoElectronTerm twoElectronTerm;

	/** s, p and (polarisation) d functions centred on oxygen. */
	ContractedGaussian sO;
	ContractedGaussian pO;
	ContractedGaussian dO;

	/** s function centred on a hydrogen, so each quartet spans more than one centre. */
	ContractedGaussian sH;

	/**
	 * Supplies the concrete {@link TwoElectronTerm} implementation under test.
	 *
	 * @return a fresh two-electron term implementation.
	 */
	abstract TwoElectronTerm newTwoElectronTerm();

	@Setup
	public void setup() throws Exception {

		twoElectronTerm = newTwoElectronTerm();

		Atom o = new Atom("O", new Vector3D(0.00000000, 0.000000, 0.119748));
		Atom h1 = new Atom("H", new Vector3D(0.00000000, 0.761561, -0.478993));
		Atom h2 = new Atom("H", new Vector3D(0.00000000, -0.761561, -0.478993));

		Molecule water = new MoleculeImpl("water");
		water.addAtom(h1);
		water.addAtom(o);
		water.addAtom(h2);

		List<ContractedGaussian> bfs = new BasisSetLibrary(water, "6-31gss").getBasisFunctions();

		sO = firstFunction(bfs, "O", 0);
		pO = firstFunction(bfs, "O", 1);
		dO = firstFunction(bfs, "O", 2);
		sH = firstFunction(bfs, "H", 0);
	}

	/**
	 * Returns the first contracted Gaussian centred on an atom of the given
	 * element symbol with the given total angular momentum (0 = s, 1 = p, 2 = d).
	 *
	 * @param bfs             the basis functions to search
	 * @param symbol          the element symbol of the centring atom
	 * @param angularMomentum the total angular momentum (l + m + n)
	 * @return the first matching contracted Gaussian
	 * @throws IllegalStateException if no matching function is present
	 */
	private static ContractedGaussian firstFunction(List<ContractedGaussian> bfs, String symbol, int angularMomentum) {
		for (ContractedGaussian cg : bfs) {
			Atom atom = cg.getCenteredAtom();
			if (atom != null && symbol.equals(atom.getSymbol())
					&& cg.getTotalAngularMomentum() == angularMomentum) {
				return cg;
			}
		}
		throw new IllegalStateException(
				"No " + symbol + " function with total angular momentum " + angularMomentum + " in basis");
	}

	@Benchmark
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public double ss() {
		return twoElectronTerm.coulomb(sO, sH, sO, sO);
	}

	@Benchmark
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public double sp() {
		return twoElectronTerm.coulomb(sO, sH, pO, pO);
	}

	@Benchmark
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public double sd() {
		return twoElectronTerm.coulomb(sO, sH, dO, dO);
	}

}
