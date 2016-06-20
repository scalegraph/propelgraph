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

/**
 * This class provides helper methods that simplify creation and
 * opening of graphs while still supporting the 
 * LocatableGraphFactory family of interfaces. 
 *  
 * <pre> 
 * {@code 
 *   Graph g = CreateGraph.openGraph( CreateGraph.GRAPH_TINKERMEM, ...)
 * } 
 * </pre> 
 * 
 * @author ccjason (11/11/2014)
 */
public class CreateGraph {

	/**
	 * @deprecated
	 * 
	 * @author ccjason (11/11/2014)
	 */
	public static final String GRAPHHINT_IS_EMPTY = "---graphhint_is_empty";
	
	// for new graphs that only contain a sample graph
	/**
	 * @deprecated
	 * 
	 * @author ccjason (11/11/2014)
	 */
	public static final String GRAPHHINT_IS_NEWLYPOPULATED = "---graphhint_is_newlypopulated";

	/**
	 * An HBase-based graph from IBM Research.  This value can be be
	 * passed to various methods of this class to indicate that that 
	 * a graph of that type be created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_GBASE = "gbase";
	/**
	 * A Neo4j graph. This value can be be passed to various methods
	 * of this class to indicate that that a graph of that type be 
	 * created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_NEO4J = "neo4j";
	/**
	 * A HBase-based Titan graph. This value can be be passed to
	 * various methods of this class to indicate that that a graph 
	 * of that type be created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_TITANHBASE = "titanhbase";
	/**
	 * A Cassandra-based Titan graph. This value can be be passed to
	 * various methods of this class to indicate that that a graph 
	 * of that type be created or opened. 
	 * 
	 * @author ccjason (06/20/2016)
	 */
	public static final String GRAPH_TITANCASSANDRA = "titancass";
	/**
	 * A BerkeleyDB-based Titan graph. This value can be be passed 
	 * to various methods of this class to indicate that that a 
	 * graph of that type be created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_TITANBERK = "titanberk";
	//public static final String GRAPH_TINKERGRAPHPRELOADED = "tinkergraphreloaded";

	/**
	 * An in-memory TinkerPop TinkerGraph graph. This value can be
	 * be passed to various methods of this class to indicate that 
	 * that a graph of that type be created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_TINKERMEM = "tinkermem";
	public static final String GRAPH_DB2RDF = "db2rdf";
	/**
	 * A JNI-based graph from IBM Research. This value can be be
	 * passed to various methods of this class to indicate that that 
	 * a graph of that type be created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_NATIVESTORE = "nativestore";
	/**
	 * An in-memory JNI-based graph from IBM Research. This value 
	 * can be be passed to various methods of this class to indicate
	 * that that a graph of that type be created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_NATIVEMEM = "nativemem";
	/**
	 * A transactional JNI-based graph from IBM Research. This value
	 * can be be passed to various methods of this class to indicate
	 * that that a graph of that type be created or opened. 
	 * 
	 * @author ccjason (03/09/2016)
	 */
	public static final String GRAPH_SGTRANS = "sgtrans";
	/**
	 * An in-memory graph from the PropelGraph project. This value
	 * can be be passed to various methods of this class to indicate 
	 * that that a graph of that type be created or opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_PROPELMEM = "propelmem";
	/**
	 * An nativemem graph initialized with the authors sample graph. 
	 * This value can be be passed to various methods of this class 
	 * to indicate that that a graph of that type be created or 
	 * opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_NATIVEMEMAUTHORS = "nativemem_authors";
	/**
	 * An nativemem graph initialized with the authors sample graph. 
	 * This value can be be passed to various methods of this class 
	 * to indicate that that a graph of that type be created or 
	 * opened. 
	 * 
	 * @author ccjason (11/30/2014)
	 */
	public static final String GRAPH_SGTRANSAUTHORS = "sgtrans_authors";

	/**
	 * returns a locatable graph urlstring for the specified 
	 * graphtype.  This helper routine can be handy because graph 
	 * urlstrings are not convenient to remember or type. 
	 * 
	 * @author ccjason (11/11/2014)
	 * 
	 * @param graphtype the type of graph to create.  Values can be 
	 *      	    any of the CreateGraph.GRAPH_* values like
	 *      	    "neo4j" or "tinkermem".
	 * @param graphname 
	 * @param mapParams 
	 * 
	 * @return String 
	 */
	private static String createGraphURL(String graphtype, String graphname, Map<String,String> mapParams ) throws IOException, InterruptedException {
		if (GRAPH_GBASE.equals(graphtype)) {
			return "pggraph:org.propelgraph.gbase.SGBPLocatableGraphFactory/?&graphname="+graphname+"&hostname="+(mapParams.get("--hostname")) ;
		} else if (GRAPH_NEO4J.equals(graphtype)) {
			return "pggraph:org.propelgraph.neo4j.Neo4jLocatableGraphFactory/?&graphname="+graphname; 
		} else if (GRAPH_DB2RDF.equals(graphtype)) {
			throw new RuntimeException("do not yet support this type of constructor for db2rdf graph");
		} else if (GRAPH_TITANHBASE.equals(graphtype)) {
			return "pggraph:org.propelgraph.titan.TitanHBaseLocatableGraphFactory/?&graphname="+graphname+"&store=hbase&hostname="+(mapParams.get("--hostname"));
		} else if (GRAPH_TITANCASSANDRA.equals(graphtype)) {
			return "pggraph:org.propelgraph.titan.TitanCassandraLocatableGraphFactory/?&graphname="+graphname+"&store=cassandra&hostname="+(mapParams.get("--hostname"));
		} else if (GRAPH_TITANBERK.equals(graphtype)) {
			return "pggraph:org.propelgraph.titan.TitanBerkeleyLocatableGraphFactory/?&graphname="+graphname+"&store=berkdb&dirpath=titanbstores";
		} else if (GRAPH_TINKERMEM.equals(graphtype)) {
			return "pggraph:org.propelgraph.tinkergraph.TinkerGraphLocatableGraphFactory/?&graphname=memory&tggraphtype=INMEMORY"; 
		} else if (GRAPH_PROPELMEM.equals(graphtype)) {
			return "pggraph:org.propelgraph.memgraph.MemLocatableGraphFactory/?&graphname=memory&dirpath=propelmem&mggraphtype=INMEMORY";
		} else if (GRAPH_SGTRANS.equals(graphtype)) {
			return "pggraph:com.ibm.research.systemg.transstore.tinkerpop.TSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=transstore&tsgraphtype=PERSISTENT";
		} else if (GRAPH_NATIVESTORE.equals(graphtype)) {
			return "pggraph:com.ibm.research.systemg.nativestore.tinkerpop.NSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=nativestore&nsgraphtype=PERSISTENT";
		} else if (GRAPH_NATIVEMEM.equals(graphtype)) {
			return "pggraph:com.ibm.research.systemg.nativestore.tinkerpop.NSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=nativemem&nsgraphtype=INMEMORY";
		} else if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype)) {
			return "pggraph:com.ibm.research.systemg.nativestore.tinkerpop.NSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=nativemem&nsgraphtype=INMEMORY&initializewith=authors";
		} else if (GRAPH_SGTRANSAUTHORS.equals(graphtype)) {
			return "pggraph:com.ibm.research.systemg.transstore.tinkerpop.TSLocatableGraphFactory/?&graphname="+graphname+"&dirpath=transstore&tsgraphtype=PERSISTENT&initializewith=authors";
		} else {
			throw new RuntimeException("illegal graph store: "+graphtype);
		}
	}

	private static String createGraphFAction( String graphtype, Map<String,String> mapParams  ) {
		if (GRAPH_GBASE.equals(graphtype)) {
			return(null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_NEO4J.equals(graphtype)) {
			return(null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_DB2RDF.equals(graphtype)) {
			throw new RuntimeException("do not yet support this type of constructor for db2rdf graph");
		} else if (GRAPH_TITANHBASE.equals(graphtype)) {
			return(null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_TITANCASSANDRA.equals(graphtype)) {
			return(null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_TITANBERK.equals(graphtype)) {
			return(null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_TINKERMEM.equals(graphtype)) {
			return LocatableGraphFactory.FACTION_NEW_NEW;
		} else if (GRAPH_PROPELMEM.equals(graphtype)) {
			return LocatableGraphFactory.FACTION_NEW_NEW;
		} else if (GRAPH_SGTRANS.equals(graphtype)) {
			return(null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_SGTRANSAUTHORS.equals(graphtype)) {
			return LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_NATIVESTORE.equals(graphtype)) {
			return(null!=mapParams.get("---cleargraph")) ? LocatableGraphFactory.FACTION_CREATE_EMPTY : LocatableGraphFactory.FACTION_CREATE_OPEN;
		} else if (GRAPH_NATIVEMEM.equals(graphtype)) {
			return LocatableGraphFactory.FACTION_NEW_NEW;
		} else if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype)) {
			return LocatableGraphFactory.FACTION_NEW_NEW;
		} else {
			throw new RuntimeException("illegal graph store: "+graphtype);
		}
	}

	/**
	 * recreate the graph from scratch.  
	 * 
	 * @author ccjason (11/30/2014)
	 * 
	 * @param graphtype 
	 * @param graphname 
	 * 
	 * @return Graph 
	 */
	public static Graph recreateGraph(String graphtype, String graphname ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
		return recreateGraph(graphtype,graphname,new HashMap<String,String>());
	}
	/**
	 * recreate the graph from scratch. 
	 * 
	 * @author ccjason (11/30/2014)
	 * 
	 * @param graphtype specifies the type of graph to create.  See 
	 *      	    the GRAPH_* static constants of this class.
	 * @param graphname a short name for the graph.  For persistant 
	 *      	    graphs this will also indicate the location
	 *      	    of the graph.
	 * @param mapParams a set of supplemental parameters that modify 
	 *      	    how the graph is created and return
	 *      	    additional information about how the graph
	 *      	    was created.
	 * 
	 * @return Graph 
	 */
	public static Graph recreateGraph(String graphtype, String graphname, Map<String,String> mapParams ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, /*AlreadyExistsException, NotFoundException,*/ UnsupportedFActionException {
		String graphurl = createGraphURL(graphtype, graphname, mapParams); //System.out.println("graph url: "+graphurl);
		LocatableGraphFactory gf = LocatableGraphFactoryFactoryImpl.getGraphFactory(graphurl);
		try {
		    if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype) || false ) {
			    Graph retval = openGraph(graphtype,graphname,mapParams);
			    //mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_NEWLYCREATED);
			    return retval;
			} else if (GRAPH_SGTRANSAUTHORS.equals(graphtype) || false ) {
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
		    } else if (GRAPH_TINKERMEM.equals(graphtype)) {
			    Graph retval = openGraph(graphtype,graphname,mapParams);
			    return retval;
		    } else if (GRAPH_PROPELMEM.equals(graphtype)) {
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
		} catch (NotFoundException exc) {
		    // note: we probably should not reach here if we've done everything correctly.  We SHOULD simply create the graph if not found.
		    throw new RuntimeException(exc);
		} catch (AlreadyExistsException exc) {
		    // note: we probably should not reach here if we've done everything correctly.  We SHOULD support recreating a graph even if it exists, not throwing an exception.
		    throw new RuntimeException(exc);
		}
	}

	/**
	 * adds vertices and edges related to Shakespeare.  If the graph
	 * was empty, the resulting small graph is suitable for some 
	 * simple demos. 
	 * 
	 * @author ccjason (11/11/2014)
	 * 
	 * @param g 
	 */
	public static void addAuthorInfo(Graph g) {
		Vertex vAnneHathaway = g.addVertex(null); vAnneHathaway.setProperty("name","Anne Hathaway"); vAnneHathaway.setProperty("importance", new Double(33.5));
		Vertex vShakespeare = g.addVertex(null);
		vShakespeare.setProperty("name","William Shakespeare");
		vShakespeare.setProperty("yearOfDeath", new Integer(1616) );
		vShakespeare.setProperty("importance", new Double(55.5) );
		Vertex vPlayRomeo = g.addVertex(null);    vPlayRomeo.setProperty("name", "Romeo And Juliet");  vPlayRomeo.setProperty("yearPublished", new Integer(1599) ); vPlayRomeo.setProperty("importance", new Double(44.3) );
		vShakespeare.addEdge("wrote",vPlayRomeo);
		Vertex vPlayJulius = g.addVertex(null);   vPlayJulius.setProperty("name", "Julius Caesear");   vPlayJulius.setProperty("yearPublished", new Integer(1599) ); vPlayJulius.setProperty("importance", new Double(45.2) );
		vShakespeare.addEdge("wrote",vPlayJulius);
		Vertex vPlayMacbeth = g.addVertex(null);  vPlayMacbeth.setProperty("name","Macbeth");         vPlayMacbeth.setProperty("yearPublished", new Integer(1623)); vPlayMacbeth.setProperty("importance", new Double(49.1) );
		vShakespeare.addEdge("wrote",vPlayMacbeth);
		vShakespeare.addEdge("married",vAnneHathaway).setProperty("yearOfWedding", new Integer(1582) );
		Vertex vMelville = g.addVertex(null);     vMelville.setProperty("name", "Herman Melville");  vMelville.addEdge("influencedBy", vShakespeare);  vMelville.setProperty("yearOfBirth", new Integer(1819) ); vMelville.setProperty("importance", new Double(52.1) );
		Vertex vDickens = g.addVertex(null);      vDickens.setProperty("name", "Charles Dickens"); vDickens.addEdge("influencedBy", vShakespeare);  vDickens.setProperty("yearOfBirth", new Integer(1812) ); vDickens.setProperty("importance", new Double(54.1) );
		vAnneHathaway.addEdge("likes",vShakespeare).setProperty("weight",new Double(0.78));
		vMelville.addEdge("likes",vShakespeare).setProperty("weight",new Double(0.58));

	}

	/**
	 * create or open a graph of the specified graph type with the 
	 * specified name.  This method simplifies the steps of creating 
	 * a graph of various types.   
	 * 
	 * @author ccjason (11/11/2014)
	 * 
	 * @param graphtype the type of graph to create.  Values can be 
	 *      	    any of the CreateGraph.GRAPH_* values like
	 *      	    "neo4j" or "tinkermem".
	 *  
	 *      	     Note: If the code implementing the
	 *      	     specified graph has not already been
	 *      	     placed in the classpath, this method with
	 *      	     throw an exception.
	 *  
	 * @param graphname a short name that should be included in the 
	 *      	    stdout logging of progress. Ex.
	 *      	    "my_family_tree_graph"
	 *  
	 *  
	 * @return Graph 
	 *  
	 * @see  #openGraph(String,String,Map)
	 */
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
		String faction = createGraphFAction( graphtype, mapParams ); //System.out.println("graph url: "+graphurl);

		LocatableGraphFactory gf = LocatableGraphFactoryFactoryImpl.getGraphFactory(graphurl);
		Graph g = gf.open(graphurl, faction, LocatableGraphFactory.FMODE_WRITE);
		return g;
	}

	/**
	 * creates a graph. 
	 *  
	 * Design: this class and method is work in progress.  It is 
	 * subject to change.  For that reason please consider it to be 
	 * deprecated. 
	 *  
	 * @deprecated 
	 * 
	 * @author ccjason (2013)
	 * 
	 * @param graphtype the type of graph to create.  Values can be 
	 *      	    any of the CreateGraph.GRAPH_* values like
	 *      	    "neo4j" or "tinkermem".
	 *  
	 *      	     Note: If the code implementing the
	 *      	     specified graph has not already been
	 *      	     placed in the classpath, this method with
	 *      	     throw an exception.
	 *  
	 * @param graphname a short name that should be included in the 
	 *      	    stdout logging of progress. Ex.
	 *      	    "my_family_tree_graph"
	 *  
	 * @param mapParams a list of configuration or option 
	 *      	    parameters. Some of the values are used by
	 *      	    the graph implementations.  A few are used
	 *      	    by this class itself.  This parameter is
	 *      	    also used to pass information back to the
	 *      	    caller.  The conventions for that are still
	 *      	    work in progress.
	 * 
	 * @return Graph 
	 */
	public static Graph openGraph(String graphtype, String graphname, Map<String,String> mapParams ) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
		Graph g = _openGraph(graphtype, graphname, mapParams);

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
		} else if (GRAPH_TITANCASSANDRA.equals(graphtype)) {
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
		} else if (GRAPH_TINKERMEM.equals(graphtype)) {
			mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
			return g;
		} else if (GRAPH_PROPELMEM.equals(graphtype)) {
			mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
			return g;
		} else if (GRAPH_NATIVEMEM.equals(graphtype)) {
			mapParams.put(GRAPHHINT_IS_EMPTY,GRAPHHINT_IS_EMPTY);
			return g;
		} else if (GRAPH_NATIVEMEMAUTHORS.equals(graphtype)) {
			mapParams.put(GRAPHHINT_IS_NEWLYPOPULATED,GRAPHHINT_IS_NEWLYPOPULATED);
			addAuthorInfo(g);
			return g;
		} else if (GRAPH_SGTRANSAUTHORS.equals(graphtype)) {
			mapParams.put(GRAPHHINT_IS_NEWLYPOPULATED,GRAPHHINT_IS_NEWLYPOPULATED);
			addAuthorInfo(g);
			return g;
		} else if (GRAPH_NATIVESTORE.equals(graphtype)) {
			return g;
		} else if (GRAPH_SGTRANS.equals(graphtype)) {
			return g;
		} else {
			throw new RuntimeException("illegal graph store: "+graphtype);
		}
	}

}
