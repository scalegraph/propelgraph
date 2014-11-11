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
package org.propelgraph.memgraph;

import org.propelgraph.LocatableGraph;
import org.propelgraph.AlreadyExistsException;
import org.propelgraph.NotFoundException;
import org.propelgraph.UnsupportedFActionException;
import org.propelgraph.LocatableGraphFactory;
import org.propelgraph.DeleteableGraphFactory;
import org.propelgraph.LocatableGraphFactoryFactoryImpl;
import com.tinkerpop.blueprints.Graph;

public class MemLocatableGraphFactory implements LocatableGraphFactory , DeleteableGraphFactory {
	static final String PREFIX = LocatableGraphFactory.SCHEME_PGGRAPH+":"+MemLocatableGraphFactory.class.getName()+"/?";
	
	@Override
	public void delete(String urlPath) {
		if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
		int idx0 = PREFIX.length();
		String graphname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&graphname=", urlPath);
		String dirpath = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&dirpath=", urlPath);
		String mggraphtype = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&mggraphtype=", urlPath, "PERSISTENT");

		try {
			Runtime.getRuntime().exec("rm -Rf "+dirpath+"/"+graphname+"/");
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}


	@Override
	public LocatableGraph open(String urlPath, String faction, String fmode) throws UnsupportedFActionException {
		if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
		if (!faction.equals(LocatableGraphFactory.FACTION_NEW_NEW)) throw new UnsupportedFActionException();
		int idx0 = PREFIX.length();
		String graphname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&graphname=", urlPath);
		String dirpath = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&dirpath=", urlPath);
		String mggraphtype = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&mggraphtype=", urlPath, "PERSISTENT");
		try {
			MemGraph retval = new MemGraph(dirpath, graphname, mggraphtype.equals("INMEMORY")?MemGraph.MemGGraphType.INMEMORY:MemGraph.MemGGraphType.PERSISTENT);

			return retval;
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	@Override
	public String getGraphURL(Graph graph) {
		// TODO Auto-generated method stub
		if (!(graph instanceof MemGraph)) throw new RuntimeException("wrong graph class");
		MemGraph g2 = (MemGraph)graph;
		String dirpath = g2.getDirPathString();
                String graphname = g2.getGraphName();
		String retval = PREFIX+"&graphname="+graphname+"&dirpath="+dirpath;
		return retval;
	}

}