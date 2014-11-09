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

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;

import org.propelgraph.GraphExternalVertexIdSupport;
//import org.propelgraph.MetisFileLoadingGraph;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
//import com.ibm.research.systemg.nativestore.JNIGen;

/**
 * This class provides functionality for loading CSV files.  It 
 * attempts to take advantage of CSV loading functionality built 
 * in to a given graph implementation if it uses a known public 
 * open interface.  If the graph has no such method, then this 
 * code implements the CSV loading.  It nevertheless still tries 
 * to take advantage of other known public interfaces the graph 
 * has that might speed up the process. 
 * 
 * @author ccjason (11/6/2014)
 */
public class LoadMetis {
    static final int commit_frequency = 0x40000000;  
    //static final int commit_frequency = 0x0001000;  // Titan likes a value of about 32, others prefer to make this infinite

    LinkedList<Integer> splitLine(String line) {
	LinkedList<Integer> lls = new LinkedList<Integer>();
	int i = 0;
	while ((i<line.length()) && (line.charAt(i)==' ')) i++;  // skipping spaces
	while ( i<line.length()) {
	    int xx = 0;
	    while ((i<line.length()) && (line.charAt(i)!=' ')) {
		xx = (xx*10) + (line.charAt(i)-'0');
		i++;  //
	    }
	    lls.add(new Integer(xx));
	    while ((i<line.length()) && (line.charAt(i)==' ')) i++;  // skipping spaces
	}
	return lls;
    }
    Integer[] splitLineIntoArray(String line) {
	LinkedList<Integer> lls = splitLine(line);
	Integer retval[] = new Integer[lls.size()];
	int i = 0;
	for ( Integer ii : lls ) {
	    retval[i++] = ii;
	}
	return retval;
    }


    /**
     * populates a graph with vertices and edges defined in a 
     * provided Metis stream. 
     * http://people.sc.fsu.edu/~jburkardt/data/metis_graph/metis_graph.html 
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g The graph to modify.
     * @param brWeb The stream providing the Metis content
     * @param max The maximum number of Metis lines to consume
     * @param whichstream A label for the stream for the sake of 
     *  		  logging to stdout
     */
    public void populateFromMetisStream(Graph g, BufferedReader brWeb, long max, String whichstream) throws FileNotFoundException, IOException {
	//final int dographops = 2;
	final boolean boolUseExternalId = true;
	String line;
	long cntLines = 0;
	long cntProps = 0;
	long cntEdges = 0;
	long cntVerts = 0;
	//long cntStatements = 0;
	long t0 = System.currentTimeMillis();
	String fieldnames[] = null;
	int  idxId = 0;
	GraphExternalVertexIdSupport graph2 = g instanceof GraphExternalVertexIdSupport ? (GraphExternalVertexIdSupport)g : null;
	boolean boolSupportsExIds = (! g.getFeatures().ignoresSuppliedIds) || (graph2 != null);
	System.out.println( "xxx "+boolSupportsExIds+"  "+graph2);

	if (!boolSupportsExIds) {
	    System.out.println("Warning: This graph implementation does not support external ids so we will be using the _id property instead.  We'll try to request indexing of that column here.  That really should be done in the caller if the graph might already contain content.");
	    if (g instanceof KeyIndexableGraph) {
		((KeyIndexableGraph)g).createKeyIndex("_id", Vertex.class);
	    }
	    
	}
	int intExpectVertexWeights = 0;
	boolean boolExpectEdgeWeights = false;

	//System.out.println("before readline");
	while ((line=brWeb.readLine())!=null) { //System.out.println("popFOF126 "+line);
	    if (5==(cntLines%10000)) System.out.println("line number "+cntLines);
	    if (5==(cntLines%commit_frequency)) {
		if (g instanceof TransactionalGraph) {
		    // for the sake of Titan which run indexed lookup slow unless we commit occuasionally... even if just reading
		    ((TransactionalGraph)g).commit();
		}
	    }
	    if (line.startsWith("%")) continue; // comment line

	    //System.out.println(line);
	    //if (line.startsWith("#")) continue;
	    //if (line.startsWith("@prefix")) continue;
	    Integer fields[] = splitLineIntoArray(line);
	    if (cntLines==0) {
		if (fields.length>2) {
		    int fv2 = fields[2].intValue();
		    if ((fv2!=0) && (fv2!=1) && (fv2!=10) && (fv2!=11)) throw new RuntimeException("likely bad first line of Metis file: "+line);
		    if ((fv2==10) || (fv2==11)) {
			if (fields.length>3) {
			    intExpectVertexWeights = fields[3].intValue();
			    if ((intExpectVertexWeights<1) || (intExpectVertexWeights>100)) throw new RuntimeException("likely bad first line of Metis file: "+line);
			} else {
			    intExpectVertexWeights = 1;
			}
		    }
		    boolExpectEdgeWeights =  ((fv2==1) || (fv2==11));
		}
		if (boolExpectEdgeWeights || (intExpectVertexWeights>0) ) System.err.println("Do not support loading Metis weights.  Weights will be ignored");
		cntLines++;
	    } else {
		if (fields.length<(intExpectVertexWeights+(boolExpectEdgeWeights?1:0))) throw new RuntimeException("likely bad data line: "+line);
		Vertex vertLine; 
		{ // get the vertex for this line
		    String exvidLine = ""+cntLines;
		    if (boolSupportsExIds) {
			if (graph2!=null) {
			    vertLine = graph2.getVertexByExternalId(exvidLine);
			} else {
			    vertLine = g.getVertex(exvidLine);
			}
		    } else {
			Iterable<Vertex> viter = g.getVertices("_id", exvidLine);
			vertLine = null;
			for (Vertex vv : viter) { vertLine = vv; break; }
		    }
		    if (vertLine==null) {
			vertLine = g.addVertex(exvidLine);  cntVerts++;
			if (!boolSupportsExIds) {
			    vertLine.setProperty("_id",exvidLine);
			}
		    }
		}
		int ifield = 0;
		ifield += intExpectVertexWeights; // skipping vertex weights
		while (ifield<fields.length) {
		    Integer intvid = fields[ifield++];
		    Integer intvid2 = intvid.intValue();
		    if (boolExpectEdgeWeights && (ifield>=fields.length)) throw new RuntimeException("likely bad data line: "+line);
		    if (boolExpectEdgeWeights) ifield++;
		    { // get the vertex for this line
			Vertex vert2;
			String exvid2 = ""+intvid;
			if (boolSupportsExIds) {
			    if (graph2!=null) {
				vert2 = graph2.getVertexByExternalId(exvid2);
			    } else {
				vert2 = g.getVertex(exvid2);
			    }
			} else {
			    Iterable<Vertex> viter = g.getVertices("_id", exvid2);
			    vert2 = null;
			    for (Vertex vv : viter) { vert2 = vv; break; }
			}
			if (vert2==null) {
			    vert2 = g.addVertex(exvid2);  cntVerts++;
			    if (!boolSupportsExIds) {
				vert2.setProperty("_id",exvid2);
			    }
			}
			{ // assume directed graph. otherwise we'd only add it if we didn't already add it
			    Edge edge = vertLine.addEdge("edge", vert2);
			    // if we were to support weights, we'd add them here.
			    cntEdges++;
			}
		    }
		}
		cntLines++;
	    }
	    if (cntLines>max) break;
	    if (cntLines%100000==5) { 
		long t3 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d   lines=%8d  commit_freq=%d  %s \n", (t3-t0), cntEdges, cntVerts, cntVerts*1000L/(t3-t0), cntProps, cntLines, commit_frequency , whichstream );
	    }

	}
	long t1;
	t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d   lines=%8d  %s   (before commit) \n", (t1-t0), cntEdges, cntVerts, cntVerts*1000L/(t1-t0), cntProps, cntLines, whichstream );
	if (g instanceof TransactionalGraph) {
	    // for the sake of Titan which run indexed lookup slow unless we commit occuasionally
	    ((TransactionalGraph)g).commit();
	}
	t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d   lines=%8d  %s   (after commit)  \n", (t1-t0), cntEdges, cntVerts, cntVerts*1000L/(t1-t0), cntProps, cntLines, whichstream );
    }


    /**
     * Populates the specified graph with Metis-formatted 
     * information obtained from the specified web url.
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g 
     * @param strURL 
     * @param graphshortname 
     * @param max 
     */
    public void populateFromMetisURL(Graph g, String strURL, String graphshortname, long max) throws FileNotFoundException, IOException {
	long t0 = System.currentTimeMillis();
	URL url = new URL(strURL);
	System.out.println("fetching url: "+url + " with Java Metis URL loader");
	InputStream isWeb;
	if (false) {
	    isWeb = url.openStream();
	} else { //JNIGen.println("ln 362");
	    URLConnection connection  = url.openConnection();
	    connection.setDoOutput(true);
	    connection.setRequestProperty("Accept-Encoding", "gzip");
	    //OutputStream osW = connection.getOutputStream();
	    InputStream isR1 = connection.getInputStream();
	    InputStreamReader isr;
	    String sContEncoding = connection.getHeaderField("Content-Encoding");
	    if ((null!=sContEncoding) && sContEncoding.equals("gzip")) {
		isWeb = new GZIPInputStream(isR1);
	    } else {  //System.out.println("bbb");
		isWeb = isR1;
	    }
	}
	BufferedReader brWeb = new BufferedReader( new InputStreamReader(isWeb, "UTF-8")); //JNIGen.println("ln 376");
	LoadMetis lcsv = new LoadMetis(); 
	lcsv.populateFromMetisStream(g, brWeb, max, graphshortname); //JNIGen.println("ln 378");
	brWeb.close();
	isWeb.close();
	if (g instanceof TransactionalGraph) {
	    TransactionalGraph tg = (TransactionalGraph)g;
	    tg.commit();
	} long t1 = System.currentTimeMillis();  System.out.println("pFEU240 time="+(t1-t0)+"ms");
    }

    /**
     * Populates the specified graph with Metis-formatted 
     * information obtained from the specified file.
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g 
     * @param strURL 
     * @param graphshortname 
     * @param max 
     */
    public void populateFromMetisFile(Graph g, String fn, String graphshortname, long max) throws FileNotFoundException, IOException {
	long t0 = System.currentTimeMillis();
	boolean done = false;
	if (true) {
	    /*if (g instanceof MetisFileLoadingGraph) {
		System.out.println("fetching file: "+fn + " with C++ CSV file loader");
		((MetisFileLoadingGraph)g).addMetisFile(fn, max);
		done = true;
		long t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms    %s   \n", (t1-t0), graphshortname  );
	    }*/
	}
	if (!done) {
	    System.out.println("fetching file: "+fn + " with Java Metis file loader");
	    InputStream is;
	    is = new FileInputStream(fn);
	    BufferedReader br = new BufferedReader( new InputStreamReader(is, "UTF-8"));
	    LoadMetis lcsv = new LoadMetis(); 
	    lcsv.populateFromMetisStream(g, br, max, graphshortname);
	    br.close();
	    is.close();
	}
	if (g instanceof TransactionalGraph) {
	    TransactionalGraph tg = (TransactionalGraph)g;
	    tg.commit();
	} long t1 = System.currentTimeMillis();  System.out.println("pFVF263 time="+(t1-t0)+"ms");
    }


}
