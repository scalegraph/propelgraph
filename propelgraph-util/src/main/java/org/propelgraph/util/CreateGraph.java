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
package org.propelgraph.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.propelgraph.ClearableGraph;
import org.propelgraph.LocatableGraphFactoryFactoryImpl;
import org.propelgraph.LocatableGraphFactory;
import org.propelgraph.DeleteableGraphFactory;
//import org.propelgraph.PreloadableGraph;
import org.propelgraph.AlreadyExistsException;
import org.propelgraph.NotFoundException;
import org.propelgraph.UnsupportedFActionException;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.KeyIndexableGraph;

/**
 * This class is copied from the SGAPISamples repo/project.
 * 
 * @author ccjason (3/25/2014)
 */
public class CreateGraph {

    public static final String GRAPHHINT_IS_EMPTY = "---graphhint_is_empty";
    //public static final String GRAPHHINT_IS_NEWLYCREATED = "---graphhint_is_newlycreated"; // removed this because don't know where callled and definition not documented here

    // for new graphs that only contain a sample graph
    public static final String GRAPHHINT_IS_NEWLYPOPULATED = "---graphhint_is_newlypopulated";


    public static final String GRAPH_GBASE = "gbase";
    public static final String GRAPH_NEO4J = "neo4j";
    public static final String GRAPH_TITANHBASE = "titanhbase";
    public static final String GRAPH_TITANBERK = "titanberk";
    //public static final String GRAPH_TINKERGRAPHPRELOADED = "tinkergraphreloaded";
    public static final String GRAPH_TINKERMEMGRAPH = "tinkermemgraph";
    public static final String GRAPH_DB2RDF = "db2rdf";
    public static final String GRAPH_NATIVESTORE = "nativestore";
    public static final String GRAPH_NATIVEMEM = "nativemem";
    public static final String GRAPH_NATIVEMEMAUTHORS = "nativemem_authors";

    public static String createGraphURL(String graphtype, String graphname, Map<String,String> mapParams ) throws IOException, InterruptedException {
	if (GRAPH_GBASE.equals(graphtype)) {
	    return "pggraph:org.propelgraph.gbase.SGBPLocatableGraphFactory/?&graphname="+graphname+"&hostname="+(mapParams.get("--hostname")) ;
	} else if (GRAPH_NEO4J.equals(graphtype)) {
	    return "pggraph:org.propelgraph.neo4j.Neo4jLocatableGraphFactory/?&graphname="+graphname; 
	} else if (GRAPH_DB2RDF.equals(graphtype)) {
	    throw new RuntimeException("do not yet support this type of constructor for db2rdf graph");
	} else if (GRAPH_TITANHBASE.equals(graphtype)) {
	    //if (null!=new Object()) throw new RuntimeException("have not tested this type of constructor for titan hbase graph");
	    return "pggraph:org.propelgraph.titan.TitanHBaseLocatableGraphFactory/?&graphname="+graphname+"&store=hbase&hostname="+(mapParams.get("--hostname"));
	    //BaseConfiguration conf = new BaseConfiguration();
	    //conf.setProperty("storage.backend","hbase");
	    //conf.setProperty("storage.hostname",hbasehost);
	    //conf.setProperty("storage.tablename","titan"+graphname);
	    //TitanGraph g = TitanFactory.open(conf);
	    //g.createKeyIndex(idPropForId, Vertex.class);
	    //return g;
	} else if (GRAPH_TITANBERK.equals(graphtype)) {
	    return "pggraph:org.propelgraph.titan.TitanBerkeleyLocatableGraphFactory/?&graphname="+graphname+"&store=berkdb&dirpath=titanbstores";
	    //throw new RuntimeException("do not yet support this type of constructor for titan berkeley graph");
	    //System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
	    //File fiDirGraph = new File("titanbstores/"+graphname);
	    //boolean boolAlreadyExists = fiDirGraph.exists();
	    //BaseConfiguration conf = new BaseConfiguration();
	    //conf.setProperty("storage.directory", fiDirGraph.getAbsoluteFile().toString());
	    //conf.setProperty("storage.backend", "berkeleyje");
	    //TitanGraph g = TitanFactory.open(conf);
	    //if (!boolAlreadyExists) {
	    //      g.createKeyIndex(idPropForId, Vertex.class);
	    //}
	    ////Logger log = LoggerFactory.getLogger(QueryProcessor.class);
	    ////log.setLevel()
	    //System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
	    //return g;
	} else if (GRAPH_TINKERMEMGRAPH.equals(graphtype)) {
	    return "pggraph:org.propelgraph.TinkerGraphLocatableGraphFactory/?&graphname=memory"; 
	} else if (GRAPH_NATIVESTORE.equals(graphtype)) {
	    return "pggraph:com.ibm.research.systemg.nativestore.tinkerpop.NSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=nativestore&nsgraphtype=PERSISTENT";
	} else if (GRAPH_NATIVEMEM.equals(graphtype)) {
	    return "pggraph:com.ibm.research.systemg.nativestore.tinkerpop.NSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=nativemem&nsgraphtype=INMEMORY";
	} else if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype)) {
	    return "pggraph:com.ibm.research.systemg.nativestore.tinkerpop.NSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=nativemem&nsgraphtype=INMEMORY&initializewith=authors";
	} else {
	    throw new RuntimeException("illegal graph store: "+graphtype);
	}
    }

    public static String createGraphFAction( Map<String,String> mapParams, String graphtype ) {
	if (GRAPH_GBASE.equals(graphtype)) {
		return (null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
	    } else if (GRAPH_NEO4J.equals(graphtype)) {
		return (null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
	    } else if (GRAPH_DB2RDF.equals(graphtype)) {
		throw new RuntimeException("do not yet support this type of constructor for db2rdf graph");
	    } else if (GRAPH_TITANHBASE.equals(graphtype)) {
		return (null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
	    } else if (GRAPH_TITANBERK.equals(graphtype)) {
		return (null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
	    } else if (GRAPH_TINKERMEMGRAPH.equals(graphtype)) {
		return LocatableGraphFactory.FACTION_NEW_NEW;
	    } else if (GRAPH_NATIVESTORE.equals(graphtype)) {
		return (null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
	    } else if (GRAPH_NATIVEMEM.equals(graphtype)) {
		return LocatableGraphFactory.FACTION_NEW_NEW;
	    } else if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype)) {
		return LocatableGraphFactory.FACTION_NEW_NEW;
	    } else {
		throw new RuntimeException("illegal graph store: "+graphtype);
	    }
    }

    public static Graph recreateGraph(String graphtype, String graphname ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
	return recreateGraph(graphtype,graphname,new HashMap<String,String>());
    }
    public static Graph recreateGraph(String graphtype, String graphname, Map<String,String> mapParams ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
	String graphurl = createGraphURL(graphtype, graphname, mapParams); //System.out.println("graph url: "+graphurl);
	LocatableGraphFactory gf = LocatableGraphFactoryFactoryImpl.getGraphFactory(graphurl);
	if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype) || false ) {
	    Graph retval = openGraph(graphtype,graphname,mapParams);
	    //mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_NEWLYCREATED);
	    return retval;
	} else if (gf instanceof DeleteableGraphFactory) {
	    ((DeleteableGraphFactory)gf).delete(graphurl);
	    Graph retval = openGraph(graphtype,graphname,mapParams);
	    mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
	    return retval;
	} else if (GRAPH_GBASE.equals(graphtype)) {
	    Graph retval = openGraph(graphtype,graphname,mapParams);
	    ((ClearableGraph)retval).clear();
	    mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
	    return retval;
	} else if (GRAPH_TINKERMEMGRAPH.equals(graphtype)) {
	    Graph retval = openGraph(graphtype,graphname,mapParams);
	    return retval;
	} else {
	    Graph retval = openGraph(graphtype,graphname,mapParams);
	    {
		Iterable<Vertex> itbl = retval.getVertices();
		if (itbl.iterator().hasNext()) {
		    retval.shutdown();
		    throw new RuntimeException( "we have not yet implemented a way to insure that a "+graphtype+" graph is empty and this one clearly is not empty.  You should drop the titan"+graphname+" table in the hbase shell");
		}
	    }
	    return retval;
	}
    }

    public static void addAuthorInfo(Graph g) {
	Vertex vAnneHathaway = g.addVertex(null); vAnneHathaway.setProperty("name","Anne Hathaway");
	Vertex vShakespeare = g.addVertex(null);
	vShakespeare.setProperty("name","William Shakespeare");
	vShakespeare.setProperty("yearOfDeath", new Integer(1616) );
	Vertex vPlayRomeo = g.addVertex(null);    vPlayRomeo.setProperty("name", "Romeo And Juliet");  vPlayRomeo.setProperty("yearPublished", new Integer(1599) );
	vShakespeare.addEdge("wrote",vPlayRomeo);
	Vertex vPlayJulius = g.addVertex(null);   vPlayJulius.setProperty("name", "Julius Caesear");   vPlayJulius.setProperty("yearPublished", new Integer(1599) );
	vShakespeare.addEdge("wrote",vPlayJulius);
	Vertex vPlayMacbeth = g.addVertex(null);  vPlayMacbeth.setProperty("name","Macbeth");         vPlayMacbeth.setProperty("yearPublished", new Integer(1623));
	vShakespeare.addEdge("wrote",vPlayMacbeth);
	vShakespeare.addEdge("married",vAnneHathaway).setProperty("yearOfWedding", new Integer(1582) );
	Vertex vMelville = g.addVertex(null);     vMelville.setProperty("name", "Herman Melville");  vMelville.addEdge("influencedBy", vShakespeare);  vMelville.setProperty("yearOfBirth", new Integer(1819) );
	Vertex vDickens = g.addVertex(null);      vDickens.setProperty("name", "Charles Dickens"); vDickens.addEdge("influencedBy", vShakespeare);  vDickens.setProperty("yearOfBirth", new Integer(1812) );
	vAnneHathaway.addEdge("likes",vShakespeare).setProperty("weight",new Double(0.78));
	vMelville.addEdge("likes",vShakespeare).setProperty("weight",new Double(0.58));

    }

    public static Graph openGraph(String graphtype, String graphname ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
	return openGraph(graphtype,graphname,new HashMap<String,String>());
    }

    /**
     * opens a graph.  WE're keeping this private until we know that
     * we have a need for it.
     * 
     * @author ccjason (2013)
     * 
     * @param graphtype 
     * @param graphname 
     * @param mapParams 
     * 
     * @return Graph 
     */
    private static Graph _openGraph(String graphtype, String graphname, Map<String,String> mapParams ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
	String graphurl = createGraphURL(graphtype, graphname, mapParams); //System.out.println("graph url: "+graphurl);
	String faction = createGraphFAction( mapParams, graphtype ); //System.out.println("graph url: "+graphurl);
	
	LocatableGraphFactory gf = LocatableGraphFactoryFactoryImpl.getGraphFactory(graphurl);
	Graph g = gf.open(graphurl, faction, LocatableGraphFactory.FMODE_WRITE);
	return g;
    }

    /**
     * creates a graph.  
     * 
     * @author ccjason (2013)
     * 
     * @param graphtype 
     * @param graphname 
     * @param mapParams 
     * 
     * @return Graph 
     */
    public static Graph openGraph(String graphtype, String graphname, Map<String,String> mapParams ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
	Graph g = _openGraph(graphtype, graphname, mapParams);
	long countval = 40000; // 40k default
	{
	    String argval = mapParams.get("--count");
	    if (argval==null) {
	    } else if (argval.equals("all")) {
		countval = 0x7fffffffffffffffL;
	    } else {
		countval = Long.parseLong(argval);
	    }
	}

	if (GRAPH_GBASE.equals(graphtype)) {
	    //((PreloadableGraph)g).preLoad();
	    if (null!=mapParams.get("---cleargraph")) {
		((ClearableGraph)g).clear();
		mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
	    }
	    return g;
	} else if (GRAPH_NEO4J.equals(graphtype)) {
	    //File fiDirGraph = new File("neo4jstores/"+graphname);
	    //boolean boolAlreadyExists = fiDirGraph.exists();
	    //Neo4jGraph g = new Neo4jGraph(fiDirGraph.getAbsolutePath());
	    //if (!boolAlreadyExists) {
	    //      g.createKeyIndex(idPropForId, Vertex.class);
	    //      //g.createKeyIndex(idPropForPeople, Vertex.class);
	    //}
	    return g;
	} else if (GRAPH_DB2RDF.equals(graphtype)) {
	    // tbd
	    throw new RuntimeException("db2rdf constructor not yet implemented");
	} else if (GRAPH_TITANHBASE.equals(graphtype)) {
	    //BaseConfiguration conf = new BaseConfiguration();
	    //conf.setProperty("storage.backend","hbase");
	    //conf.setProperty("storage.hostname",hbasehost);
	    //conf.setProperty("storage.tablename","titan"+graphname);
	    //TitanGraph g = TitanFactory.open(conf);
	    //g.createKeyIndex(idPropForId, Vertex.class);
	    //g.loadGraphML('data/onevertex.xml')
	    return g;
	} else if (GRAPH_TITANBERK.equals(graphtype)) {
	    //((KeyIndexableGraph)g).createKeyIndex("name", Vertex.class);  // this is not the right place for this
	    //((KeyIndexableGraph)g).createKeyIndex("mmn", Vertex.class);   // this is not the right place for this

	    //System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
	    //File fiDirGraph = new File("titanbstores/"+graphname);
	    //boolean boolAlreadyExists = fiDirGraph.exists();
	    //BaseConfiguration conf = new BaseConfiguration();
	    //conf.setProperty("storage.directory", fiDirGraph.getAbsoluteFile().toString());
	    //conf.setProperty("storage.backend", "berkeleyje");
	    //TitanGraph g = TitanFactory.open(conf);
	    //if (!boolAlreadyExists) {
	    //      g.createKeyIndex(idPropForId, Vertex.class);
	    //}
	    //Logger log = LoggerFactory.getLogger(QueryProcessor.class);
	    //log.setLevel()
	    //System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
	    return g;
	} else if (GRAPH_TINKERMEMGRAPH.equals(graphtype)) {
	    mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
	    return g;
	} else if (GRAPH_NATIVEMEM.equals(graphtype)) {
	    mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
	    return g;
	} else if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype)) {
	    mapParams.put(GRAPHHINT_IS_NEWLYPOPULATED,GRAPHHINT_IS_NEWLYPOPULATED);
	    addAuthorInfo(g);
	    return g;
	} else if (GRAPH_NATIVESTORE.equals(graphtype)) {
	    return g;
	} else {
	    throw new RuntimeException("illegal graph store: "+graphtype);
	}
    }



    
}

