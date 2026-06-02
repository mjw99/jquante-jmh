package name.mjw.jquante.benchmarks;

import name.mjw.jquante.math.qm.integral.HuzinagaTwoElectronTerm;
import name.mjw.jquante.math.qm.integral.TwoElectronTerm;

public class HuzinagaTwoElectronTermBenchmark extends AbstractTwoElectronTermBenchmark {

	@Override
	TwoElectronTerm newTwoElectronTerm() {
		return new HuzinagaTwoElectronTerm();
	}

}
