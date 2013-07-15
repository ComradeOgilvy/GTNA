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
 * UniformSamplingWalkerController.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: Tim;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.transformation.sampling.walkercontroller;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
=======
import java.util.Collection;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.Map;
>>>>>>> StartNode initialization
=======
>>>>>>> code format

import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.transformation.sampling.AWalker;
import gtna.transformation.sampling.AWalkerController;
import gtna.transformation.sampling.CandidateFilter;
<<<<<<< HEAD
<<<<<<< HEAD
import gtna.transformation.sampling.StartNodeSelector;
import gtna.transformation.sampling.sample.NetworkSample;

/**
 * @author Tim
 * 
 */
public class UniformSamplingWalkerController extends AWalkerController {

	CandidateFilter cf;
	Collection<AWalker> walkers;

	/**
	 * @param key
	 * @param value
	 * @param w
	 * @param cf
	 */
	public UniformSamplingWalkerController(Collection<AWalker> w,
			CandidateFilter cf) {
		super(w.size() + "x_" + w.toArray(new AWalker[0])[0].getValue(), w, cf);
		if (w.size() != 1) {
			throw new IllegalArgumentException(
					"This Walker Controller is defined for single dimensional usage.");
		}
		this.walkers = w;
		this.cf = cf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gtna.transformation.sampling.AWalkerController#initialize(gtna.graph.
	 * Graph, gtna.graph.Node[])
	 */
	@Override
	public void initialize(Node[] startNodes) {
		AWalker[] wa = walkers.toArray(new AWalker[0]);
		for (int i = 0; i < walkers.size(); i++) {
			// if #walkers > #startNodes assign startnodes with wraparound
			int snid = i % startNodes.length;

			wa[i].setStartNode(startNodes[snid]);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gtna.transformation.sampling.AWalkerController#getActiveWalkers()
	 */
	@Override
	protected Collection<AWalker> getActiveWalkers() {
		return walkers;
	}
	
	
=======
=======
import gtna.transformation.sampling.NetworkSample;
<<<<<<< HEAD
>>>>>>> set parameter values
=======
import gtna.transformation.sampling.StartNodeSelector;
>>>>>>> testing & bugfixing (2)

/**
 * @author Tim
 * 
 */
public class UniformSamplingWalkerController extends AWalkerController {

	CandidateFilter cf;
	Collection<AWalker> walkers;

	/**
	 * @param key
	 * @param value
	 * @param w
	 * @param cf
	 */
	public UniformSamplingWalkerController(Collection<AWalker> w,
			CandidateFilter cf) {
		super(w.size() + "x_" + w.toArray(new AWalker[0])[0].getValue(), w, cf);
		if (w.size() != 1) {
			throw new IllegalArgumentException(
					"This Walker Controller is defined for single dimensional usage.");
		}
		this.walkers = w;
		this.cf = cf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gtna.transformation.sampling.AWalkerController#initialize(gtna.graph.
	 * Graph, gtna.graph.Node[])
	 */
	@Override
	public void initialize(Node[] startNodes) {
		AWalker[] wa = walkers.toArray(new AWalker[0]);
		for (int i = 0; i < walkers.size(); i++) {
			// if #walkers > #startNodes assign startnodes with wraparound
			int snid = i % startNodes.length;

			wa[i].setStartNode(startNodes[snid]);

		}

	}

<<<<<<< HEAD
    /* (non-Javadoc)
     * @see gtna.transformation.sampling.AWalkerController#filterCandidates(java.util.Collection)
     */
    @Override
    public Collection<Node> filterCandidates(Collection<Node> candidates) {
	
	return cf.filterCandidates(candidates, super.getNetworkSample());
    }
>>>>>>> StartNode initialization
=======
	/*
	 * (non-Javadoc)
	 * 
	 * @see gtna.transformation.sampling.AWalkerController#getActiveWalkers()
	 */
	@Override
	protected Collection<AWalker> getActiveWalkers() {
		return walkers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gtna.transformation.sampling.AWalkerController#filterCandidates(java.
	 * util.Collection)
	 */
	@Override
	public Collection<Node> filterCandidates(Collection<Node> candidates) {

		return cf.filterCandidates(candidates, super.getNetworkSample());
	}
>>>>>>> code format

}
