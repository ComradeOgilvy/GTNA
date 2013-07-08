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
 * UniformRandomWalker.java
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
package gtna.transformation.sampling.uniformsampling;

import java.util.Collection;
import java.util.Random;

import gtna.graph.Node;
import gtna.transformation.sampling.AWalker;
import gtna.transformation.sampling.AWalkerController;

/**
 * @author Tim
 *
 */
public class UniformRandomWalker extends AWalker {

    /**
     * @param key
     * @param value
     * @param awc
     */
    public UniformRandomWalker(String key, String value, AWalkerController awc) {
	super(key, value, awc);
    }

    /* (non-Javadoc)
     * @see gtna.transformation.sampling.AWalker#selectNextNode(java.util.Collection)
     */
    @Override
    protected Node selectNextNode(Collection<Node> candidates) {
	Random r = new Random();
	
	int next = r.nextInt() % candidates.size();
	
	return candidates.toArray(new Node[0])[next];
	
    }

}