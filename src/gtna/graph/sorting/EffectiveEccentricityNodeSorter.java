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
 * EffectiveEccentricityNodeSorter.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: truong;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.graph.sorting;

import gtna.graph.Graph;
import gtna.graph.Node;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/**
 * @author truong
 * 
 */
public class EffectiveEccentricityNodeSorter extends NodeSorter {

	private HashMap<Node, Double> map = new HashMap<Node, Double>();

	public EffectiveEccentricityNodeSorter(NodeSorterMode mode) {
		super("ECCENTRICITY", mode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gtna.graph.sorting.NodeSorter#sort(gtna.graph.Graph,
	 * java.util.Random)
	 */
	@Override
	public Node[] sort(Graph g, Random rand) {
		this.calculate();
		Node[] sorted = this.clone(g.getNodes());
		Arrays.sort(sorted, new DegreeAsc());
		this.randomize(sorted, rand);
		if (this.mode == NodeSorterMode.DESC) {
			sorted = this.reverse(sorted);
		}
		return sorted;
	}

	/**
	 * 
	 */
	private void calculate() {
		// TODO Auto-generated method stub

	}

	private class DegreeAsc implements Comparator<Node> {
		public int compare(Node n1, Node n2) {
			double point1 = map.get(n1).doubleValue();
			double point2 = map.get(n2).doubleValue();
			int result;
			if (point1 == point2) {
				result = 0;
			} else if (point1 > point2) {
				result = 1;
			} else {
				result = -1;
			}
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gtna.graph.sorting.NodeSorter#applicable(gtna.graph.Graph)
	 */
	@Override
	public boolean applicable(Graph g) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gtna.graph.sorting.NodeSorter#isPropertyEqual(gtna.graph.Node,
	 * gtna.graph.Node)
	 */
	@Override
	protected boolean isPropertyEqual(Node n1, Node n2) {
		return map.get(n1).doubleValue() == map.get(n2).doubleValue();
	}

}
