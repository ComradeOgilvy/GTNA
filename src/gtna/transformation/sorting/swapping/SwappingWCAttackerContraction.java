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
 * SwappingWCAttackerContraction.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: "Benjamin Schiller";
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.transformation.sorting.swapping;

import java.util.Random;

/**
 * @author "Benjamin Schiller"
 *
 */
public class SwappingWCAttackerContraction extends SwappingWCNode {

	public SwappingWCAttackerContraction(int index, double pos,
			Swapping swapping) {
		super(index, pos, swapping);
	}

	public void turn(Random rand) {
		// TODO implement
		System.out.println("performing turn @ SwappingWCAttackerContraction " + this.toString());
	}

	protected double ask(SwappingWCNode caller, double callerID,
			double[] callerNeighborIDs, Random rand) {
		// TODO implement
		return this.getID().pos;
	}

	protected double ask(SwappingWCNode caller, double callerID,
			double[] callerNeighborIDs, double initiatorID,
			double[] initiatorNeighborIDs, Random rand) {
		// TODO implement
		return this.getID().pos;
	}

	protected double swap(double callerID, double[] callerNeighborIDs, int ttl,
			Random rand) {
		// TODO implement
		return SwappingNode.NO_SWAP;
	}

}