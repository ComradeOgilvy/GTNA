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
 * FruchtermanReingold.java
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
import java.util.Random;

import gtna.graph.Edge;
import gtna.graph.Graph;
import gtna.graph.GraphProperty;
import gtna.graph.Node;
import gtna.id.md.MDIdentifier;
import gtna.id.md.MDIdentifierSpaceSimple;
import gtna.id.md.MDPartitionSimple;
import gtna.plot.Gephi;
import gtna.transformation.Transformation;
import gtna.transformation.TransformationImpl;
import gtna.transformation.id.RandomMDIDSpaceSimple;
import gtna.util.Config;
import gtna.util.MDVector;

/**
 * @author Nico
 *
 */
public class FruchtermanReingold extends TransformationImpl implements Transformation {
	/*
	 * IDSpace and Partitions we care about
	 */
	protected MDIdentifierSpaceSimple idSpace;
	protected MDPartitionSimple[] partitions;
	private int realities;
	private double[] moduli;
	private Boolean wrapAround;
	
		/*
		 * How many iterations should the algorithm run?
		 */
	private int iterations;
	
		/*
		 * Maximal area for the drawing, as defined per the moduli
		 */
	private double area;
	
		/*
		 * Optimal distance, named k by FR
		 */
	private double k;
	
		/*
		 * A factor to increase the attraction force and
		 * the cooling factor which is applied onto the temperature
		 * in each step
		 */	
	private double attractionFactor = 1.0;
	private double coolingFactor = 0.95;
	
		/*
		 * Global cooling temperature
		 */
	private double t;
	
		/*
		 * Internally, we have to use a biased MDVector - so we need to
		 * store that bias
		 */
	private MDVector bias;

	private Gephi gephi;
	private Transformation initialPositions;
	
	public FruchtermanReingold() {
		this("GDA_FRUCHTERMAN_REINGOLD", new String[]{}, new String[]{});
	}
	
	public FruchtermanReingold(String key, String[] configKeys,
			String[] configValues) {
		super(key, configKeys, configValues);
		this.iterations = Config.getInt("GDA_FRUCHTERMAN_REINGOLD_ITERATIONS");
	}
		
		/*
		 * Constructor for the case that we already have set the idspace
		 */
	public FruchtermanReingold(Gephi plotter) {
		this("GDA_FRUCHTERMAN_REINGOLD", new String[]{}, new String[]{});
		this.gephi = plotter;
		this.initialPositions = null;
	}
	
	public FruchtermanReingold(int realities, double[] moduli, Boolean wrapAround, Gephi plotter) {
		this("GDA_FRUCHTERMAN_REINGOLD", new String[]{}, new String[]{});
		this.realities = realities;
		this.moduli = moduli;
		this.wrapAround = wrapAround;
		this.gephi = plotter;
		initialPositions = new RandomMDIDSpaceSimple( this.realities, this.moduli, this.wrapAround);
	}

	@Override
	public boolean applicable(Graph g) {
		return true;
	}
	
	@Override
	public Graph transform(Graph g) {
		if ( initialPositions != null ) {
				/*
				 * First step: create an idspace
				 */
			g = initialPositions.transform(g);
		}
		
			/*
			 * idspace is now given, extract it
			 */
		for (GraphProperty p : g.getProperties("ID_SPACE")) {
			if (p instanceof MDIdentifierSpaceSimple) {
				MDIdentifier id = (MDIdentifier) ((MDIdentifierSpaceSimple) p)
				.randomID( new Random() );
				if (!(id instanceof MDIdentifier)) {
					throw new RuntimeException("Okay, why do we have a MDIDSpace without a MDIdentifier?");
				}
					/*
					 * good question: how do we retrieve the number of realities from a given space?
					 */
				this.moduli = ((MDIdentifierSpaceSimple) p).getModuli();
				this.wrapAround = ((MDIdentifierSpaceSimple) p).isWrapAround();
				this.idSpace = (MDIdentifierSpaceSimple) p;
				
				this.partitions = (MDPartitionSimple[]) this.idSpace.getPartitions();
				double[] moduli = this.idSpace.getModuli();
				this.area = 1;
				for ( double singleModulus: moduli ) this.area = this.area * singleModulus;
				k = Math.sqrt( this.area / this.partitions.length );
				System.out.println("Best distance: " + k);
				
				double maxModulus = 0;
				for ( int i = 0; i < moduli.length; i++ ) {
					maxModulus = Math.max(maxModulus, moduli[i]);
				}
				this.t = maxModulus;
			}
		}

			/*
			 * A bias is needed as the internal algorithm works on coordinates
			 * between (-modulus/2) and (+modulus/2) for each dimension
			 */
		bias = new MDVector(this.moduli.length);
		String moduliString = "";
		for ( int i = 0; i < this.moduli.length; i++ ) {
			moduliString = moduliString + ", " + this.moduli[i];
			bias.setCoordinate(i, this.moduli[i] / 2);
		}
		
		for ( int i = 0; i < this.iterations; i++ ) {
			System.out.println("\n\n   >>> in iteration " + i + " <<<");
			if ( gephi != null && i % 50 == 0 ) {
				gephi.Plot ( g, "test" + i + ".svg" );
			}
			g = this.doIteration ( g );
		}
		gephi.Plot ( g, "test" + this.iterations + ".svg" );
		
		return g;
	}
	
	private Graph doIteration(Graph g) {
		MDVector delta, currDisp;
		
			/*
			 * Displacement array which will hold the displacement
			 * throughout one single iteration
			 */		
		MDVector[] disp = new MDVector[this.partitions.length];
		
		// First step: repulsive forces
		for ( Node v: g.getNodes() ) {
				// Reset displacement
			disp[v.getIndex()] = new MDVector(idSpace.getDimensions(), 0d);
			
				// Calculate repulsive forces to *all* other nodes
			for ( Node u: g.getNodes() ) {
				if ( u.getIndex() == v.getIndex() ) continue;
				delta = getCoordinate(v);
				delta.subtract ( getCoordinate(u) );
				currDisp = new MDVector(delta.getDimension(), delta.getCoordinates());
				double currDispNorm = currDisp.getNorm(); 
				if ( Double.isNaN(currDispNorm) ) throw new RuntimeException("You broke it");
				currDisp.divideBy(currDispNorm);
				currDisp.multiplyWith(fr ( currDispNorm ) );
				disp[v.getIndex()].add(currDisp);
			}
		}

		// Second step: attractive forces
		for ( Edge e: g.generateEdges() ) {
			delta = getCoordinate( e.getSrc() );
			delta.subtract ( getCoordinate( e.getDst()) );
			currDisp = new MDVector(delta.getDimension(), delta.getCoordinates());
			double currDispNorm = currDisp.getNorm(); 
			if ( Double.isNaN(currDispNorm) ) throw new RuntimeException("You broke it");
			currDisp.divideBy(currDispNorm);
			currDisp.multiplyWith(fa ( currDispNorm ) );

			disp[e.getSrc()].subtract(currDisp);
			disp[e.getDst()].add(currDisp);
		}
		
			// Last but not least: assign new coordinates
		for ( Node v: g.getNodes() ) {
			MDVector vVector = getCoordinate(v);
			
			currDisp = new MDVector(disp[v.getIndex()].getDimension(), disp[v.getIndex()].getCoordinates());
			double currDispNorm = currDisp.getNorm(); 
			currDisp.divideBy(currDispNorm);
			currDisp.multiplyWith( Math.min(currDispNorm, t) );
					
//			System.out.println("Move " + vVector + " by " + currDisp + " (calculated disp: " + disp[v.getIndex()] + ", t: " + t + ")" );
//			System.out.println("Old pos: " + vVector );

			vVector.add(currDisp);
			vVector = setNormalized ( vVector );
			setCoordinate(v, vVector);
			
//			System.out.println("New pos: " + vVector + " for " + v.getIndex());
		}
		
		t = cool ( t );	
		return g;
	}
	
	private MDVector setNormalized ( MDVector v ) {
		for ( int i = 0; i < v.getDimension(); i++ ) {
			double coordinate = Math.min(idSpace.getModulus(i)/2, Math.max(idSpace.getModulus(i)/-2, v.getCoordinate(i)));
			v.setCoordinate(i, coordinate);
		}
		return v;
	}	
	
	private MDVector getCoordinate( Node n ) {
		return getCoordinate(n.getIndex());
	}
	
	private MDVector getCoordinate ( int i ) {
		MDVector iV = ((MDIdentifier) partitions[i].getRepresentativeID()).toMDVector();
//		System.out.print("Retrieving " + iV);
		iV.subtract(bias);
//		System.out.println(" (biased: " + iV + ") for " +i);
		return iV;
	}
	
	private void setCoordinate ( Node v, MDVector newPos ) {
//		System.out.print("Setting " + newPos);
		newPos.add(bias);
//		System.out.println(" (biased: " + newPos + ")");
		((MDIdentifier) partitions[v.getIndex()].getRepresentativeID()).setCoordinates(newPos.getCoordinates());
	}
		
	private double fr ( Double x ) {
		return ( ( k * k ) / x );
	}
	
	private double fa ( Double x ) {
		return attractionFactor * ( ( x * x ) / k );
	}
	
	private double cool ( Double t ) {
		return coolingFactor * t;
	}
}