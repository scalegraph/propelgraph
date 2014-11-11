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
package org.propelgraph.neo4j;

import java.io.File;

import org.propelgraph.LocatableGraphFactoryFactoryImpl;
import org.propelgraph.LocatableGraphFactory;
import org.propelgraph.DeleteableGraphFactory;
import org.propelgraph.UnsupportedFActionException;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;


public class Neo4jLocatableGraphFactory implements LocatableGraphFactory, DeleteableGraphFactory {
	static final String PREFIX = LocatableGraphFactory.SCHEME_PGGRAPH+":"+Neo4jLocatableGraphFactory.class.getName()+"/?";

	@Override
	public void delete(String urlPath) {
		if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
		int idx0 = PREFIX.length();
		String graphname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&graphname=", urlPath);
		String hostname = null;	// hostname = byLocatableGraphFactoryFactoryImpl.parseForURLParameter("&hostname=", urlPath);
		try {
			Runtime.getRuntime().exec("rm -Rf neo4jstores/"+graphname+"/");
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}


	@Override
	public Graph open(String urlPath, String faction, String fmode) throws UnsupportedFActionException {
		if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
		int idx0 = PREFIX.length();
		String graphname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&graphname=", urlPath);
		String hostname = null;	// hostname = byLocatableGraphFactoryFactoryImpl.parseForURLParameter("&hostname=", urlPath);
		if (!faction.equals(org.propelgraph.LocatableGraphFactory.FACTION_CREATE_OPEN))	throw new UnsupportedFActionException();
		try {
			File fiDirGraph = new File("neo4jstores/"+graphname);
			boolean boolAlreadyExists = fiDirGraph.exists();
			Neo4jGraph g = new Neo4jGraph(fiDirGraph.getAbsolutePath());
			if (!boolAlreadyExists) {
				g.createKeyIndex("_id", Vertex.class);
				//g.createKeyIndex(idPropForPeople, Vertex.class);
			}
			return g;
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	@Override
	public String getGraphURL(Graph graph) {
		// we don't know how to determine the graph file being used if all we have is the Graph object
		throw new RuntimeException("don't currently support this method");
	}

}
