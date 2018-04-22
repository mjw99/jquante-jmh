package name.mjw.jquante.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import name.mjw.jquante.math.qm.basis.ContractedGaussian;
import name.mjw.jquante.math.qm.basis.Power;
import name.mjw.jquante.math.qm.integral.HuzinagaTwoElectronTerm;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

@State(Thread)
@OutputTimeUnit(NANOSECONDS)
@BenchmarkMode(AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)

public class HuzinagaTwoElectronTermBenchmark {

	static final int NUMBER_OF_LOOPS = 10000000;

	HuzinagaTwoElectronTerm huzinagaTwoElectronTerm;
	ContractedGaussian s;
	ContractedGaussian p;
	ContractedGaussian d;

	@Setup
	public void setup() {

		huzinagaTwoElectronTerm = new HuzinagaTwoElectronTerm();
		s = new ContractedGaussian(new Vector3D(0, 0, 0), new Power(0, 0, 0));
		p = new ContractedGaussian(new Vector3D(0, 0, 0), new Power(1, 0, 0));
		d = new ContractedGaussian(new Vector3D(0, 0, 0), new Power(2, 0, 0));

	}

	@Benchmark
	@OperationsPerInvocation(NUMBER_OF_LOOPS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void ss() {
		for (int i = 0; i < NUMBER_OF_LOOPS; i++) {
			huzinagaTwoElectronTerm.coulomb(s, s, s, s);

		}

	}

	@Benchmark
	@OperationsPerInvocation(NUMBER_OF_LOOPS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void sp() {
		for (int i = 0; i < NUMBER_OF_LOOPS; i++) {
			huzinagaTwoElectronTerm.coulomb(s, s, p, p);

		}

	}

	@Benchmark
	@OperationsPerInvocation(NUMBER_OF_LOOPS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void sd() {
		for (int i = 0; i < NUMBER_OF_LOOPS; i++) {
			huzinagaTwoElectronTerm.coulomb(s, s, d, d);

		}

	}

}
