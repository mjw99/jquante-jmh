package name.mjw.jquante.benchmarks;

import name.mjw.jquante.math.qm.integral.RysTwoElectronTerm;
import name.mjw.jquante.math.qm.integral.TwoElectronTerm;

public class RysTwoElectronTermBenchmark extends AbstractTwoElectronTermBenchmark {

	@Override
	TwoElectronTerm newTwoElectronTerm() {
		return new RysTwoElectronTerm();
	}

}
