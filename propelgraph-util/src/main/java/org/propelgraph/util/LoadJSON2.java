package org.propelgraph.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import org.propelgraph.LabeledVertexGraph;
import org.propelgraph.GraphExternalVertexIdSupport;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import org.propelgraph.KeyIndexableGraphSupport;
import org.propelgraph.KeyIndexableGraphSupportFactoryFactoryImpl;
import static org.jasonnet.logln.Logln.logln;

/**
 * This class provides functionality for loading json graph 
 * files of the form...
 *
 * <pre>
[

    {
    "node_id": "139146904",
    "node_type": "user",
    "user_name": "Jane Doe",
    "user_screen_name": "ABCDEconsulting",
    "user_location": "Atlanta",
    "user_followers_count": 1464,
    "user_profile_image_url": "http://pbs.mmm.com/what.png"
    },
    {
    "edge_id": "139146904_572070087009669891",
    "edge_type": "create",
    "source_node": "139146994",
    "end_node": "572070087009969121",
    "end_node_type": "tweet",
    "time": "2015-03-01T16:25:11",
    "timestamp_ms": 1425227133348
    },
    ...
]
 * </pre>
 * 
 * @author ccjason (03/16/2015)
 */
public class LoadJSON2 {

	/**
	 * 
	 * 
	 * @author ccjason (4/24/2015)
	 */
	static final String TITAN_PREFIX_PROPERTY = "p";
	static final String TITAN_PREFIX_LABEL = "l";
	boolean boolNeedPrefixes = false;

	/**
	 * configure the loader to prefix property names and label names 
	 * with 'p' and 'l' respectively before loading.  This is to 
	 * work around the limitation of graphs like Titan's that don't 
	 * allow a graph's set of property names and label names to 
	 * overlap. 
	 *  
	 * By default this flag is configured as false, but the caller 
	 * should set this to true before invoing the loader if they are 
	 * using a graph implementation like Titan's that has this 
	 * restriction and the read graph might contain combinations of 
	 * labels and property names that prevent the graph from being 
	 * loaded by the graph implementation being used.
	 * 
	 * @author ccjason (4/24/2015)
	 * 
	 * @param newval 
	 */
	public void configureNeedPrefixes( boolean newval ) {
		boolNeedPrefixes = true;
	}



	HashSet<String> hsRecentEdgeExternalIds1 = new HashSet<String>();
	HashSet<String> hsRecentEdgeExternalIds2 = new HashSet<String>();
	HashMap<JsonParser.Feature,Boolean> htFeatures = new HashMap<JsonParser.Feature,Boolean>();
	/**
	 * adjust the JSON parser used by this class.  We have found 
	 * that the following is often helpful: 
	 * 
	 * <pre>
	 * {@code
	 * lj.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
	 * lj.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	 * }
	 * </pre>
	 * 
	 * @author ccjason (4/6/2015)
	 * 
	 * @param feature 
	 * @param val 
	 */
	public void configure( JsonParser.Feature feature, boolean val ) {
		htFeatures.put( feature, val );
	}



	/**
	 * add vertices and edges to the specified graph based on the content found
	 * in the provided InputStream 
	 *  
	 * This method skips adding vertices and edges that it's already 
	 * seen.  In the case of edges, it checks for only the edges 
	 * added in the previous two invocation of this method of this 
	 * object. 
	 * 
	 * @author ccjason (03/16/2015)
	 * 
	 * @param g the graph to update
	 * @param is is the input stream
	 * @param max maximum number of elements to process
	 */
	public void populateFromJSONStream(Graph g, InputStream is, long maxElements) throws IOException {
		GraphExternalVertexIdSupport graph2 = g instanceof GraphExternalVertexIdSupport ? (GraphExternalVertexIdSupport)g : null;
		boolean boolSupportsExIds = (! g.getFeatures().ignoresSuppliedIds) || (graph2 != null);
		if (!boolSupportsExIds) {
			System.out.println("Warning: This graph implementation does not support external ids so we will be using the _id property instead.  We'll try to request indexing of that column here.  That really should be done in the caller if the graph might already contain content.");
			KeyIndexableGraphSupport kk = KeyIndexableGraphSupportFactoryFactoryImpl.getKeyIndexableGraphSupport(g);
			if (kk != null) {
				kk.createKeyIndex("_id", Vertex.class);
				System.out.println(" _id index created");
			} else {
				System.out.println("graph doesn't support the KeyIndexableGraph interface, so we can't index the _id column and must abort"); return;
			}
		}
		LabeledVertexGraph lvgraph = null;
		if (g instanceof LabeledVertexGraph) {
			lvgraph = (LabeledVertexGraph)g;
		}

		final int dographops = 2;
		final boolean boolUseExternalId = true;
		long cntPartialVerts = 0;
		long cntWholeVerts = 0;
		long cntProps = 0;
		long cntEdges = 0;
		long cntWholeElements = 0;
		long t0 = System.currentTimeMillis();
		//VertexMapCache tm = new VertexMapCache(100);
		HashSet<String> hsNewRecentEdgeExternalIds = new HashSet<String>();

		JsonFactory jsonF = new JsonFactory();
		JsonParser jp = jsonF.createParser(is);
		for (JsonParser.Feature feat : htFeatures.keySet()) {
		    jp.configure( feat, htFeatures.get(feat).booleanValue());
		}
		if (jp.nextToken() != JsonToken.START_ARRAY) {
			throw new IOException("Expected data to start with an Array");
		}
		HashMap<String,Object> hmRecord = new HashMap<String,Object>();
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			//logln("s");
			if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
				hmRecord.clear();
				while (jp.nextToken() != JsonToken.END_OBJECT) {
					String fieldName = jp.getCurrentName();
					//logln("fieldName= "+fieldName);
					// Let's move to value
					JsonToken tok = jp.nextToken();

					if (tok == JsonToken.VALUE_NUMBER_INT) {
						hmRecord.put(fieldName, new Integer((int)jp.getLongValue()));
					} else if (tok == JsonToken.VALUE_STRING) {
						hmRecord.put(fieldName, jp.getText());
					} else if (tok == JsonToken.VALUE_NUMBER_FLOAT) {
						hmRecord.put(fieldName, new Double(jp.getDoubleValue()));
					} else { // ignore, or signal error?
						throw new IOException("Unrecognized Json type: "+tok);
					}
				}
				if (hmRecord.containsKey("node_type")) {
					String node_id = (String)hmRecord.get("node_id");      hmRecord.remove("node_id");
					String node_type = (String)hmRecord.get("node_type");  hmRecord.remove("node_type");
					//logln("getting vertex");
					Vertex v;
					if (boolSupportsExIds) {
						if (graph2!=null) {
							v = graph2.getVertexByExternalId(node_id);
						} else {
							v = g.getVertex(node_id);
						}
					} else {
						Iterable<Vertex> viter;
						if (boolNeedPrefixes) {
							viter = g.getVertices( TITAN_PREFIX_PROPERTY+"_id", node_id);
						} else {
							viter = g.getVertices("_id", node_id);
						}
						v = null;
						for (Vertex vv : viter) { v = vv; break; }
					}
					//logln("got vertex?");
					if (v==null) {
						if ((lvgraph==null) || (node_type==null)) {
							v = g.addVertex(node_id);
						} else {
							v = lvgraph.addLabeledVertex(node_id,node_type);
						}
						if (!boolSupportsExIds) {
							if (boolNeedPrefixes) {
								v.setProperty(TITAN_PREFIX_PROPERTY+"_id", node_id);
							} else {
								v.setProperty("_id", node_id);
							}
						}
					} else {
						cntPartialVerts--;
					}
					//logln("got vertex 2?");
					for (Object kobj : hmRecord.keySet()) {
						String key = (String)kobj;
						//logln("set prop "+key);
						Object val = hmRecord.get(key);
						if (boolNeedPrefixes) {
							v.setProperty(TITAN_PREFIX_PROPERTY + key, val);
						} else {
							v.setProperty(key,val);
						}
						cntProps++;
					}
					//logln("set props");
					cntWholeVerts++;
				} else if (hmRecord.containsKey("edge_type")) {
					String edge_id = (String)hmRecord.get("edge_id");     hmRecord.remove("edge_id");
					if (hsRecentEdgeExternalIds1.contains(edge_id) || hsRecentEdgeExternalIds2.contains(edge_id)) {
						// redundant.  'skip.
						//logln("skipping edge with exid of "+edge_id);
					} else { 
						//logln("not skipping edge with exid of "+edge_id);
						hsNewRecentEdgeExternalIds.add(edge_id);
						String edge_type = (String)hmRecord.get("edge_type"); hmRecord.remove("edge_type");
						String end_node_type = (String)hmRecord.get("end_node_type"); hmRecord.remove("end_node_type");
						String end_node = (String)hmRecord.get("end_node"); hmRecord.remove("end_node");
						String source_node = (String)hmRecord.get("source_node"); hmRecord.remove("source_node");
						Vertex vEnd, vSource; // = g.getVertex(end_node);
						if (boolSupportsExIds) {
							if (graph2!=null) {
								vEnd = graph2.getVertexByExternalId(end_node);
								vSource = graph2.getVertexByExternalId(source_node);
							} else {
								vEnd = g.getVertex(end_node);
								vSource = g.getVertex(source_node);
							}
						} else {
							Iterable<Vertex> viter;
							if (boolNeedPrefixes) {
								viter = g.getVertices( TITAN_PREFIX_PROPERTY+"_id", end_node);
							} else {
								viter = g.getVertices("_id", end_node);
							}
							vEnd = null;
							for (Vertex vv : viter) { vEnd = vv; break; }
							if (boolNeedPrefixes) {
								viter = g.getVertices( TITAN_PREFIX_PROPERTY+"_id", source_node);
							} else {
								viter = g.getVertices("_id", source_node);
							}
							vSource = null;
							for (Vertex vv : viter) { vSource = vv; break; }
						}
						if (vEnd==null) {
							if ((lvgraph==null) || (end_node_type==null)) {
								vEnd = g.addVertex(end_node);
							} else {
								vEnd = lvgraph.addLabeledVertex(end_node,end_node_type);
							}
							// todo: set vertex class/label
							cntPartialVerts++;
						}
						//Vertex vSource = g.getVertex(source_node); // required to be already defined earlier in the file
						if (vSource==null) {
							System.out.println("missing source vertex, skipping edge creation"); // we let it continue to run for the sake of debugging parsing
						} else {
							Edge edge;
							if (boolNeedPrefixes) {
								 edge = g.addEdge(edge_id, vSource, vEnd, TITAN_PREFIX_LABEL + edge_type);
							} else {
								edge = g.addEdge(edge_id, vSource, vEnd, edge_type);
							}
							hsNewRecentEdgeExternalIds.add(edge_id);
							for (Object kobj : hmRecord.keySet()) {
								String key = (String)kobj;
								Object val = hmRecord.get(key);
								if (boolNeedPrefixes) {
									edge.setProperty(key, TITAN_PREFIX_PROPERTY + val);
								} else {
									edge.setProperty(key,val);
								}
								cntProps++;
							}
						}
						cntEdges++;
					}
				} else {
					throw new RuntimeException("not an edge or vertex?");
				}
			} else {
				throw new RuntimeException("expected to start a json object");
			}
			cntWholeElements++;
			if ((cntWholeElements%10000)==0){
				long cntVerts = cntPartialVerts+cntWholeVerts;
				long t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d \n", (t1-t0), cntEdges, cntVerts, cntVerts*1000L/(1+t1-t0), cntProps  );
			}
			if (cntWholeElements>=maxElements)	break;
		}
		jp.close();	// closes parser and underlying stream
		{
			long cntVerts = cntPartialVerts+cntWholeVerts;
			long t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d \n", (t1-t0), cntEdges, cntVerts, cntVerts*1000L/(1+t1-t0), cntProps  );
		}
		hsRecentEdgeExternalIds2 = hsRecentEdgeExternalIds1; // 
		hsRecentEdgeExternalIds1 = hsNewRecentEdgeExternalIds; // 
	}



	/**
	 * populate graph from the JSON data at the specified url. 
	 *  
	 * @see populateFromJSONStream(Graph, InputStream, long )
	 * 
	 * @author ccjason (03/16/2015)
	 * 
	 * @param g the graph
	 * @param strURL 
	 * @param graphshortname short name for this load that will be 
	 *  		     listed in logs
	 * @param max maximum number of triples to process
	 */
	public void populateFromURL(Graph g, String strURL, String graphshortname, long max) throws FileNotFoundException, IOException {
		long t0 = System.currentTimeMillis();
		URL url; 
		{
			url = new URL(strURL);
		}
		/*
		if (true && (g instanceof RDFHTTPLoadingGraph)) {
			System.out.println("fetching url: "+url + " with C++ RDF URL loader");
			//((com.ibm.research.systemg.nativestore.tinkerpop.NSGraph)g).add_turtle_rdf_data_from_http_url(url.toString(), max);
			((RDFHTTPLoadingGraph)g).add_turtle_rdf_data_from_http_url(url.toString(), max);
			if (null!=new Object())	return;	// 
		} else {
		}
		*/
		System.out.println("fetching url: "+url + " with Java JSON2 URL loader");
		InputStream isWeb;
		if (false) {
			isWeb = url.openStream();
		} else { //JNIGen.println("ln 362");
			URLConnection connection  = url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Encoding", "gzip");
			//OutputStream osW = connection.getOutputStream();
			InputStream isR1 = connection.getInputStream();
			String sContEncoding = connection.getHeaderField("Content-Encoding");
			if ((null!=sContEncoding) && sContEncoding.equals("gzip")) {
				isWeb = new GZIPInputStream(isR1);
			} else {  //System.out.println("bbb");
				isWeb = isR1;
			}
		}
		populateFromJSONStream(g,isWeb,max);
		if (g instanceof TransactionalGraph) {
			TransactionalGraph tg = (TransactionalGraph)g;
			tg.commit();
		}
	}


	/**
	 * populate graph from the JSON data at the specified file.
	 *  
	 * @see populateFromJSONStream(Graph, InputStream, long )
	 * 
	 * @author ccjason (03/16/2015)
	 * 
	 * @param g the graph
	 * @param strFN filename
	 * @param graphshortname short name for this load that will be 
	 *  		     listed in logs
	 * @param max maximum number of triples to process
	 */
	public void populateFromFile(Graph g, String strFN, String graphshortname, long max) throws FileNotFoundException, IOException {
		long t0 = System.currentTimeMillis();
		System.out.println("fetching file: "+strFN + " with Java JSON2 file loader");
		InputStream is = new FileInputStream(strFN);
		populateFromJSONStream(g,is,max);
		if (g instanceof TransactionalGraph) {
			TransactionalGraph tg = (TransactionalGraph)g;
			tg.commit();
		}
	}


}
