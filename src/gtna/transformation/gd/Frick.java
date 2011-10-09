/* ===========================================================
 * GTNA : Graph-Theoretic Network Analyzer
 * ===========================================================
 *
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors
 *
 * Project Info:  http://www.p2p.tu-darmstadt.de/research/gtna/
 *
 * GTNA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GTNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * ---------------------------------------
 * Frick.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: Nico;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.transformation.gd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import gtna.graph.Graph;
import gtna.graph.GraphProperty;
import gtna.graph.Node;
import gtna.id.md.MDIdentifier;
import gtna.id.md.MDIdentifierSpaceSimple;
import gtna.id.md.MDPartitionSimple;
import gtna.plot.Gephi;
import gtna.transformation.Transformation;
import gtna.transformation.id.RandomMDIDSpaceSimple;
import gtna.util.MDVector;

/**
 * @author Nico
 * 
 */
public class Frick extends ForceDrivenAbstract implements Transformation {
	/*
	 * How many iterations should the algorithm run?
	 */
	private int maxIterations;

	/*
	 * Maximal area for the drawing, as defined per the moduli
	 */
	private double area;

	/*
	 * Optimal distance, named k by FR and E_des by Frick
	 */
	private double eDes;

	/*
	 * Storing additional data for vertices
	 */
	private VertexData[] vertexData;

	/*
	 * Current global temperature, desired minimal temperature and upper
	 * temperature limit
	 */
	private double tGlobal = 256;
	private double tMin = 0.1;
	private double tMax = 64;

	/*
	 * Angles for oscillation and rotation detection, and the according
	 * sensitivity factors. They were not given explicitly in Fricks paper :(
	 */
	private double alphaOscillation = Math.PI / 4;
	private double alphaRotation = Math.PI / 3;
	private double sigmaOscillation = 1.5;
	private double sigmaRotation = 0.5;

	/*
	 * Gravitational constant for the force that is excerted towards the
	 * barycenter
	 */
	private final double gamma = 0.0625;

	private Transformation initialPositions;
	private Random rand;

	public Frick() {
		this("GDA_FRICK", new String[] {}, new String[] {});
	}

	public Frick(String key, String[] configKeys, String[] configValues) {
		super(key, configKeys, configValues);
	}

	/*
	 * Constructor for the case that we already have set the idspace
	 */
	public Frick(Gephi plotter) {
		this("GDA_FRICK", new String[] {}, new String[] {});
		this.gephi = plotter;
		this.initialPositions = null;
	}

	public Frick(int realities, double[] moduli, Boolean wrapAround, Gephi plotter) {
		this("GDA_FRICK", new String[] {}, new String[] {});
		this.realities = realities;
		this.moduli = moduli;
		this.wrapAround = wrapAround;
		this.gephi = plotter;
		initialPositions = new RandomMDIDSpaceSimple(this.realities, this.moduli, this.wrapAround);
	}

	@Override
	public Graph transform(Graph g) {
		rand = new Random();

		if (initialPositions != null) {
			/*
			 * First step: create an idspace
			 */
			g = initialPositions.transform(g);
		}

		/*
		 * idspace is now given, extract it
		 */
		initIDSpace(g);

		double[] moduli = this.idSpace.getModuli();
		this.area = 1;
		for (double singleModulus : moduli)
			this.area = this.area * singleModulus;
		eDes = Math.sqrt(this.area / this.partitions.length) / 2;

		Node[] nodeList = g.getNodes();
		maxIterations = 8 * nodeList.length;

		vertexData = new VertexData[nodeList.length];
		for (int i = 0; i < nodeList.length; i++) {
			vertexData[i] = new VertexData(tGlobal);
		}

		int currIteration = 0;
		while (tGlobal > tMin && currIteration < maxIterations) {
			System.out.println("\n\n   >>> in iteration " + currIteration + " <<<");
			if (gephi != null && currIteration % 50 == 0) {
				gephi.Plot(g, "frick" + currIteration + ".svg");
			}

			if (currIteration % nodeList.length == 0) {
				/*
				 * Shuffle the list of nodes to be sure that every node is
				 * visited
				 */
				Collections.shuffle(Arrays.asList(nodeList));
			}

			Node v = nodeList[currIteration % nodeList.length];
			System.out.println("Choose " + v.getIndex());

			g = this.doIteration(g, v);
			currIteration++;
		}
		gephi.Plot(g, "frick-end.svg");

		return g;
	}

	private Graph doIteration(Graph g, Node v) {
		MDVector delta;
		double deltaNorm;

		/*
		 * See 4.3 in Frick's paper about the influence of \Phi
		 */
		double PHI = 1 + (v.getDegree() / 2);

		/*
		 * first step: attraction to center of gravity, which is the zero point
		 * 
		 * This is chosen in contrast to the original algorithm which insists on
		 * using the "real" barycenter between the initially positioned nodes.
		 * But: we want to arrange them around the zero point and not just
		 * "anywhere"
		 */
		System.out.println("Current position: " + getCoordinate(v));
		MDVector p = getCoordinate(v).multiplyWith(-1).multiplyWith(gamma).multiplyWith(PHI);
		System.out.println("initial p with barycenter factor: " + p);
		p.add(getRandomDisturbanceVector());

		/*
		 * Repulsive forces
		 */
		for (Node u : g.getNodes()) {
			if (u.getIndex() == v.getIndex())
				continue;
			delta = getCoordinate(v).subtract(getCoordinate(u));
			deltaNorm = delta.getNorm();
			if (deltaNorm != 0) {
				delta.multiplyWith(eDes * eDes).divideBy(deltaNorm * deltaNorm);
				p.add(delta);
			}
		}
		System.out.println("After all repulsive forces: " + p);

		/*
		 * Attractive forces -- as we should have symmetrical edges,
		 * v.getIncomingEdges() and v.getOutgoingEdges() should be identical
		 */
		for (int i : v.getOutgoingEdges()) {
			delta = getCoordinate(v).subtract(getCoordinate(i));
			deltaNorm = delta.getNorm();
			delta.multiplyWith(deltaNorm * deltaNorm).divideBy(eDes * eDes * PHI);
			p.subtract(delta);
		}
		System.out.println("After all attractive forces: " + p);
		
		/*
		 * End of impulse calculation as done in Fig 2 Following: update the
		 * position and the temperature as in Fig 3
		 */
		if (p.getNorm() != 0) {
			p.divideBy(p.getNorm()).multiplyWith(vertexData[v.getIndex()].localT);
			MDVector vPos = getCoordinate(v);
			System.out.print("Old coord: " + vPos);
			vPos.add(p);
			System.out.print(" -- trying to set new Coord " + vPos);
			setCoordinate(v, vPos);

			vPos = getCoordinate(v);
			System.out.println("New coord: " + vPos);
		}
		if (vertexData[v.getIndex()].lastImpulse.getNorm() != 0) {
			/*
			 * There has been a movement of this node before
			 */
			double beta = p.angleTo(vertexData[v.getIndex()].lastImpulse);
			System.out.println("Angle to former movement: " + beta);
			if (Math.sin(beta) >= Math.sin(Math.toDegrees(Math.PI / 2 + alphaRotation / 2))) {
				/*
				 * This looks like a rotation
				 */
				System.out.println("Rotation?");
				vertexData[v.getIndex()].skewGauge += sigmaRotation * Math.signum(Math.sin(beta));
			}
			if (Math.abs(Math.cos(beta)) >= Math.cos(Math.toDegrees(alphaOscillation / 2))) {
				/*
				 * This looks like an oscillation
				 */
				System.out.println("Old localT: " + vertexData[v.getIndex()].localT);
				vertexData[v.getIndex()].localT = vertexData[v.getIndex()].localT * sigmaOscillation * Math.cos(beta);
				System.out.println("New localT: " + vertexData[v.getIndex()].localT);
			}

			System.out.println("Old localT: " + vertexData[v.getIndex()].localT + ", skewGauge: "
					+ vertexData[v.getIndex()].skewGauge);
			vertexData[v.getIndex()].localT = vertexData[v.getIndex()].localT
					* (1 - Math.abs(vertexData[v.getIndex()].skewGauge));
			vertexData[v.getIndex()].localT = Math.min(vertexData[v.getIndex()].localT, tMax);
			System.out.println("Old localT: " + vertexData[v.getIndex()].localT);
		}
		vertexData[v.getIndex()].lastImpulse = p.clone();

		return g;
	}

	private MDVector getRandomDisturbanceVector() {
		double minModulus = idSpace.getMinModulus();
		return new MDVector(idSpace.getDimensions(), rand.nextDouble() * (minModulus / 10000));
	}

	private class VertexData {
		MDVector lastImpulse;
		double localT;
		double skewGauge;

		public VertexData(double initT) {
			lastImpulse = new MDVector(moduli.length, 0);
			localT = initT;
		}
	}
}