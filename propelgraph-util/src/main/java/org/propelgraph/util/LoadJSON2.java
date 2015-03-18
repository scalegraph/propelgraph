package org.propelgraph.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import org.propelgraph.LabeledVertexGraph;
import org.propelgraph.GraphExternalVertexIdSupport;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
//import static org.jasonnet.logln.Logln.logln;

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
	 * add vertices and edges to the specified graph based on the content found
	 * in the provided InputStream
	 * 
	 * @author ccjason (03/16/2015)
	 * 
	 * @param g the graph to update
	 * @param is is the input stream
	 * @param max maximum number of elements to process
	 */
	void populateFromJSONStream(Graph g, InputStream is, long maxElements) throws IOException {
        GraphExternalVertexIdSupport graph2 = g instanceof GraphExternalVertexIdSupport ? (GraphExternalVertexIdSupport)g : null;
        boolean boolSupportsExIds = (! g.getFeatures().ignoresSuppliedIds) || (graph2 != null);
        if (!boolSupportsExIds) {
            System.out.println("Warning: This graph implementation does not support external ids so we will be using the _id property instead.  We'll try to request indexing of that column here.  That really should be done in the caller if the graph might already contain content.");
            if (g instanceof KeyIndexableGraph) {
                ((KeyIndexableGraph)g).createKeyIndex("_id", Vertex.class);
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

		JsonFactory jsonF = new JsonFactory();
		JsonParser jp = jsonF.createJsonParser(is);

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
						Iterable<Vertex> viter = g.getVertices("_id", node_id);
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
						// todo: set vertex class/label
					} else {
						cntPartialVerts--;
					}
					//logln("got vertex 2?");
					for (Object kobj : hmRecord.keySet()) {
						String key = (String)kobj;
						//logln("set prop "+key);
						Object val = hmRecord.get(key);
						v.setProperty(key,val);
						cntProps++;
					}
					//logln("set props");
					cntWholeVerts++;
				} else if (hmRecord.containsKey("edge_type")) {
					String edge_id = (String)hmRecord.get("edge_id");     hmRecord.remove("edge_id");
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
						Iterable<Vertex> viter = g.getVertices("_id", end_node);
						vEnd = null;
						for (Vertex vv : viter) { vEnd = vv; break; }
						viter = g.getVertices("_id", source_node);
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
					Edge edge = g.addEdge(edge_id, vSource, vEnd, edge_type);
					for (Object kobj : hmRecord.keySet()) {
						String key = (String)kobj;
						Object val = hmRecord.get(key);
						edge.setProperty(key,val);
						cntProps++;
					}
					cntEdges++;
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
	}



	/**
	 * populate graph from the JSON data at the specified url.
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
			//InputStreamReader isr;
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


}
