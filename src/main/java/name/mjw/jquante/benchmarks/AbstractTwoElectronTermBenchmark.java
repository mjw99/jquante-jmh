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

import org.hipparchus.geometry.euclidean.threed.Vector3D;

import name.mjw.jquante.math.qm.basis.ContractedGaussian;
import name.mjw.jquante.math.qm.basis.Power;
import name.mjw.jquante.math.qm.integral.TwoElectronTerm;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

/**
 * Shared driver for the {@link TwoElectronTerm} coulomb() benchmarks. Concrete
 * subclasses only supply the implementation under test via
 * {@link #newTwoElectronTerm()}; JMH discovers the {@code ss}/{@code sp}/
 * {@code sd} benchmarks on each concrete subclass.
 */
@State(Thread)
@OutputTimeUnit(NANOSECONDS)
@BenchmarkMode(AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public abstract class AbstractTwoElectronTermBenchmark {

	TwoElectronTerm twoElectronTerm;
	ContractedGaussian s;
	ContractedGaussian p;
	ContractedGaussian d;

	/**
	 * Supplies the concrete {@link TwoElectronTerm} implementation under test.
	 *
	 * @return a fresh two-electron term implementation.
	 */
	abstract TwoElectronTerm newTwoElectronTerm();

	@Setup
	public void setup() {

		twoElectronTerm = newTwoElectronTerm();

		s = new ContractedGaussian(new Vector3D(0, 0, 0), new Power(0, 0, 0));
		s.addPrimitive(1.0, 1.0);
		s.normalize();

		p = new ContractedGaussian(new Vector3D(0, 0, 0), new Power(1, 0, 0));
		p.addPrimitive(1.0, 1.0);
		p.normalize();

		d = new ContractedGaussian(new Vector3D(0, 0, 0), new Power(2, 0, 0));
		d.addPrimitive(1.0, 1.0);
		d.normalize();

	}

	@Benchmark
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public double ss() {
		return twoElectronTerm.coulomb(s, s, s, s);
	}

	@Benchmark
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public double sp() {
		return twoElectronTerm.coulomb(s, s, p, p);
	}

	@Benchmark
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public double sd() {
		return twoElectronTerm.coulomb(s, s, d, d);
	}

}
