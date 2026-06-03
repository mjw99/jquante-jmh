package name.mjw.jquante.benchmarks;

import org.hipparchus.geometry.euclidean.threed.Vector3D;

import name.mjw.jquante.molecule.Atom;
import name.mjw.jquante.molecule.Molecule;
import name.mjw.jquante.molecule.impl.MoleculeImpl;

/**
 * Shared molecular geometry for the benchmarks, so the two-electron term
 * micro-benchmarks and the full single-point SCF benchmark measure the same
 * physical system rather than two copies that can silently drift apart.
 *
 * <p>The atom insertion order is significant: it fixes the order of the basis
 * functions that {@link AbstractTwoElectronTermBenchmark} selects from, so it
 * should not be changed casually.
 */
final class Geometry {

	private Geometry() {
	}

	/**
	 * The water molecule shared by every benchmark, with coordinates in
	 * Angstrom. A fresh instance is returned on each call.
	 *
	 * @return a new water {@link Molecule}.
	 */
	static Molecule water() {
		Atom o = new Atom("O", new Vector3D(0.00000000, 0.000000, 0.119748));
		Atom h1 = new Atom("H", new Vector3D(0.00000000, 0.761561, -0.478993));
		Atom h2 = new Atom("H", new Vector3D(0.00000000, -0.761561, -0.478993));

		Molecule water = new MoleculeImpl("water");
		water.addAtom(h1);
		water.addAtom(o);
		water.addAtom(h2);

		return water;
	}
}
