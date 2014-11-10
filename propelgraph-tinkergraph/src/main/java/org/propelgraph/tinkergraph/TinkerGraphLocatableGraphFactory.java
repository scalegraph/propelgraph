/*
 * This file is part of the ScaleGraph?PropelGraph project (http://scalegraph.org).
 *
 * This file is licensed to You under the Eclipse Public License (EPL);
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright ScaleGraph Team 2014.
 */
package org.propelgraph.tinkergraph;

import java.io.File;
import java.util.HashMap;
import org.propelgraph.LocatableGraph;
import org.propelgraph.LocatableGraphFactory;
import org.propelgraph.LocatableGraphFactoryFactoryImpl;
import org.propelgraph.UnsupportedFActionException;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class TinkerGraphLocatableGraphFactory implements LocatableGraphFactory {
	static HashMap<TinkerGraph,String> hmMappingOfExistingGraphs = new HashMap<TinkerGraph,String>();

	static final String PREFIX = LocatableGraphFactory.SCHEME_PGGRAPH+":"+TinkerGraphLocatableGraphFactory.class.getName()+"/?";

	@Override
	public Graph open(String urlstring, String faction, String fmode) throws UnsupportedFActionException {
		if (!urlstring.startsWith(PREFIX)) throw new RuntimeException("unsupported urlstring: "+urlstring);
		int idx0 = PREFIX.length();
		String graphname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&graphname=", urlstring);
		//String dirpath = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&dirpath=", urlPath);
		String tggraphtype = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&tggraphtype=", urlstring, "PERSISTENT");
		try {
			TinkerGraph g;
			if ("INMEMORY".equals(tggraphtype)) {
				if (!faction.equals(LocatableGraphFactory.FACTION_NEW_NEW))	throw new UnsupportedFActionException();
				g = new TinkerGraph();
			} else { // tggraphtype: "PERSISTENT"
				File fiDirGraph = new File("tinkergraphstores/"+graphname);
				g = new TinkerGraph(fiDirGraph.getAbsolutePath());
				hmMappingOfExistingGraphs.put(g,urlstring);
			}
			return g;
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	@Override
	public String getGraphURL(Graph graph) {
		if (!(graph instanceof TinkerGraph)) throw new RuntimeException("error: only TinkerGraph supported");
		TinkerGraph tgraph = (TinkerGraph)graph;

		// we don't know how to determine the graph file being used if all we have is the Graph object, we've added code below that works around this on some occasions.
		if (!(hmMappingOfExistingGraphs.containsKey(tgraph))) throw new RuntimeException("error: we are unable to generate urls for TinkerGraphs that were not generated from urls");
		String retval = hmMappingOfExistingGraphs.get(tgraph);
		return retval;
	}

}
