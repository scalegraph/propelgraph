package com.ibm.research.systemg.tinkerpop.blueprints.titan;

import java.io.File;


import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import org.apache.commons.configuration.BaseConfiguration;
import org.propelgraph.LocatableGraphFactory;
import org.propelgraph.DeleteableGraphFactory;
import org.propelgraph.LocatableGraphFactoryFactoryImpl;
import org.propelgraph.UnsupportedFActionException;

public class TitanHBaseLocatableGraphFactory implements LocatableGraphFactory, DeleteableGraphFactory {
	static final String PREFIX = LocatableGraphFactory.SCHEME_PGGRAPH+":"+TitanHBaseLocatableGraphFactory.class.getName()+"/?";


    @Override
    public void delete(String urlPath) {
	if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
	Graph g;
	try {
	    g = open(urlPath, org.propelgraph.LocatableGraphFactory.FACTION_CREATE_OPEN, org.propelgraph.LocatableGraphFactory.FMODE_WRITE);
	} catch (UnsupportedFActionException exc) {
	    throw new RuntimeException(exc);
	}
	TitanGraph gg = (TitanGraph)g;
	TitanCleanup tc = new TitanCleanup();
	gg.shutdown();
	tc.clear(gg);
    }

	
    @Override
	public Graph open(String urlPath, String faction, String fmode) throws UnsupportedFActionException {
		if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
		int idx0 = PREFIX.length();
                String graphname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&graphname=", urlPath);
		String store = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&store=", urlPath);
		if (!faction.equals(org.propelgraph.LocatableGraphFactory.FACTION_CREATE_OPEN))	throw new UnsupportedFActionException();
		if (store.equals("berkdb")) {
			if (null!=new Object()) throw new RuntimeException("dead code error");  // should not reach this code
			System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
			String dirpath = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&dirpath=", urlPath);
			File fiDirGraph = new File(dirpath+"/"+graphname);
			boolean boolAlreadyExists = fiDirGraph.exists();
			BaseConfiguration conf = new BaseConfiguration();
			conf.setProperty("storage.directory", fiDirGraph.getAbsoluteFile().toString());
			conf.setProperty("storage.backend", "berkeleyje");
			TitanGraph g = TitanFactory.open(conf);
			if (!boolAlreadyExists) {
				//g.createKeyIndex(idPropForId, Vertex.class);
			}
			//Logger log = LoggerFactory.getLogger(QueryProcessor.class);                                                                                                                       
			//log.setLevel()                                                                                                                                                                    
			System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
			return g;
		} else if (store.equals("hbase")) {
			String hostname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&hostname=", urlPath);
			if ("localhost".equals(hostname)) hostname = null;
			BaseConfiguration conf = new BaseConfiguration();
			conf.setProperty("storage.backend","hbase");
			conf.setProperty("storage.hostname",hostname);
			conf.setProperty("storage.tablename","titan"+graphname);
			TitanGraph g = TitanFactory.open(conf);
			//g.createKeyIndex(idPropForId, Vertex.class);
			//g.loadGraphML('data/onevertex.xml')                                                                                                                                       
			return g;
		} else {
			throw new RuntimeException("unknown store");
		}
	}

	@Override
	public String getGraphURL(Graph graph) {
		// we don't know how to determine the graph file being used if all we have is the Graph object
		throw new RuntimeException("don't currently support this method");
	}

}
