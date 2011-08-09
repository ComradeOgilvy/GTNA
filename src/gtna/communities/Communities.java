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
 * Communities.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: benni;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.communities;

import gtna.graph.Graph;
import gtna.graph.GraphProperty;
import gtna.io.Filereader;
import gtna.io.Filewriter;
import gtna.util.Config;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author benni
 * 
 */
public class Communities implements GraphProperty {
	private Community[] communities;

	public Communities() {
		this.communities = new Community[] {};
	}

	public Communities(HashMap<Integer, Community> communities) {
		this.communities = new Community[communities.size()];
		int index = 0;
		for (Community c : communities.values()) {
			this.communities[index++] = c;
		}
	}

	public Communities(ArrayList<Community> communities) {
		this.communities = new Community[communities.size()];
		for (int i = 0; i < communities.size(); i++) {
			this.communities[i] = communities.get(i);
		}
	}

	public Communities(Community[] communities) {
		this.communities = communities;
	}

	@Override
	public boolean write(String filename, String key) {
		Filewriter fw = new Filewriter(filename);

		// CLASS
		fw.writeComment(Config.get("GRAPH_PROPERTY_CLASS"));
		fw.writeln(this.getClass().toString());

		// KEYS
		fw.writeComment(Config.get("GRAPH_PROPERTY_KEY"));
		fw.writeln(key);

		// # OF COMMUNITIES
		fw.writeComment("Communities");
		fw.writeln(this.communities.length);

		fw.writeln();

		// LIST OF COMMUNITIES
		for (Community community : this.communities) {
			fw.writeln(community.getStringRepresentation());
		}

		return fw.close();
	}

	@Override
	public void read(String filename, Graph graph) {
		Filereader fr = new Filereader(filename);

		// CLASS
		fr.readLine();

		// KEYS
		String key = fr.readLine();

		// # OF COMMUNITIES
		int communities = Integer.parseInt(fr.readLine());
		this.communities = new Community[communities];

		// COMMUNITIES
		String line = null;
		int index = 0;
		while ((line = fr.readLine()) != null) {
			this.communities[index++] = new Community(line);
		}

		fr.close();

		graph.addProperty(key, this);
	}
}
