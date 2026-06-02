package name.mjw.jquante.benchmarks;

import name.mjw.jquante.math.qm.integral.HGPTwoElectronTerm;
import name.mjw.jquante.math.qm.integral.TwoElectronTerm;

public class HGPTwoElectronTermBenchmark extends AbstractTwoElectronTermBenchmark {

	@Override
	TwoElectronTerm newTwoElectronTerm() {
		return new HGPTwoElectronTerm();
	}

}
