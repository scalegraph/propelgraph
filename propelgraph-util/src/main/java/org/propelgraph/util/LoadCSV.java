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

import org.propelgraph.ClearableGraph;
import org.propelgraph.GraphExternalVertexIdSupport;
import org.propelgraph.CSVFileLoadingGraph;
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
public class LoadCSV {
    static final int commit_frequency = 0x40000000;  
    //static final int commit_frequency = 0x0001000;  // Titan likes a value of about 32, others prefer to make this infinite


    LinkedList<String> splitLine(String line) {
	int state = 0; // start of field
	// = 1;  // have created field, but might have some characters before the comma or eol
	LinkedList<String> lls = new LinkedList<String>();
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i<line.length(); i++) {
	    char c = line.charAt(i);
	    if (c=='"') {
		i++;
		while (true) {
		    // note: if the terminating double quote is missing, an exception probably will be thrown here.  
		    c = line.charAt(i);
		    if (false && '\\'==c) {
			i++; c = line.charAt(i);
			sb.append(c);
		    } else if ('"'==c) {
			if (((i+1)<line.length()) && (line.charAt(i+1)=='"')) { // System.out.println(" inline quote");
			    //  Ex: "This is what I said ""Hello"""    // LibreOffice will save CSV's this way
			    sb.append('"');
			    i++;
			} else {
			    lls.add(sb.toString()); sb.setLength(0);
			    i++;
			    while ((i<line.length()) && (' '==line.charAt(i))) i++;
			    if (i>=line.length()) return  lls;
			    if (','!=line.charAt(i)) {
				System.out.println("it wasn't a comma?"); throw new RuntimeException(line);
			    }
			    // leave i pointing at comma because we're about to increment
			    break;
			}
		    } else {
			// ordinary character
			sb.append(c);
		    }
		    i++;
		}
	    } else if (','==c) {
        	// blank field
		lls.add(sb.toString());
		// leave i pointing at comma because we're about to increment
	    } else {
		while ((i<line.length()) && (','!=(c=line.charAt(i)))) {
		    sb.append(c);
		    i++;
		}
		lls.add(sb.toString()); sb.setLength(0);
		if (i>=line.length()) {
		    return lls;
		} else {
		    // leave i pointing at comma because we're about to increment
		}
	    }
	}
	// blank final field  Ex:  Hi,there,   // this is how LibreOffice expresses it.  There really is a cell there.
	lls.add(sb.toString());
	return lls;
    }
    String[] splitLineIntoArray(String line) {
	LinkedList<String> lls = splitLine(line);
	String retval[] = new String[lls.size()];
	int i = 0;
	for ( String ss : lls ) {
	    retval[i++] = ss;
	}
	return retval;
    }


    /**
     * populates a graph with vertices defined in a provided CSV 
     * stream.  The first column should be the external id of the 
     * specified vertex. 
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g The graph to modify.
     * @param brWeb The stream providing the CSV content
     * @param max The maximum number of CSV lines to consume
     * @param whichstream A label for the stream for the sake of 
     *  		  logging to stdout
     */
    public void populateFromCSVVertexStream(Graph g, BufferedReader brWeb, long max, String whichstream) throws FileNotFoundException, IOException {
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

	if (!boolSupportsExIds) {
	    System.out.println("Warning: This graph implementation does not support external ids so we will be using the _id property instead.  We'll try to request indexing of that column here.  That really should be done in the caller if the graph might already contain content.");
	    if (g instanceof KeyIndexableGraph) {
		((KeyIndexableGraph)g).createKeyIndex("_id", Vertex.class);
	    }
	    
	}

	//System.out.println("before readline");
	while ((line=brWeb.readLine())!=null) { //System.out.println("popFOF126 "+line);
	    cntLines++;
	    if (0==(cntLines%10000)) System.out.println("line number "+cntLines);
	    if (0==(cntLines%commit_frequency)) {
		if (g instanceof TransactionalGraph) {
		    // for the sake of Titan which run indexed lookup slow unless we commit occuasionally... even if just reading
		    ((TransactionalGraph)g).commit();
		}
	    }
	    //System.out.println(line);
	    //if (line.startsWith("#")) continue;
	    //if (line.startsWith("@prefix")) continue;
	    String fields[] = splitLineIntoArray(line);
	    if (cntLines==1) {
		fieldnames = fields;
	    } else {
		Vertex vert; 
		if (boolSupportsExIds) {
		    if (graph2!=null) {
			vert = graph2.getVertexByExternalId(fields[idxId]);
		    } else {
			vert = g.getVertex(fields[idxId]);
		    }
		} else {
		    Iterable<Vertex> viter = g.getVertices("_id", fields[idxId]);
		    vert = null;
		    for (Vertex vv : viter) { vert = vv; break; }
		}
		if (vert==null) {
		    vert = g.addVertex(fields[idxId]);  cntVerts++;
		    if (!boolSupportsExIds) {
			vert.setProperty("_id",fields[idxId]);
		    }
		}
		for (int i = 0; i<fieldnames.length; i++) {
		    if (i==idxId) {
			// skip it
		    } else {
			vert.setProperty(fieldnames[i],fields[i]); cntProps++;
		    }
		}
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
     * Populate a graph from the provided CSV-formatted edge stream. 
     * The columns should include a source vertex (external id), 
     * destination vertex (external id), label.  All remaining 
     * columns will be treated as properties on the edge. 
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g 
     * @param brWeb 
     * @param max 
     * @param whichstream 
     */
    public void populateFromCSVEdgeStream(Graph g, BufferedReader brWeb, long max, String whichstream) throws FileNotFoundException, IOException {
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
	int  idxSrc = 0;
	int idxDst = 1;
	int idxLabel = 2;
	GraphExternalVertexIdSupport graph2 = g instanceof GraphExternalVertexIdSupport ? (GraphExternalVertexIdSupport)g : null;
	boolean boolSupportsExIds = (! g.getFeatures().ignoresSuppliedIds) || (graph2 != null);

	if (!boolSupportsExIds) {
	    System.out.println("Warning: This graph implementation does not support external ids so we will be using the _id property instead.  We'll try to request indexing of that column here.  That really should be done in the caller if the graph might already contain content.");
	    if (g instanceof KeyIndexableGraph) {
		((KeyIndexableGraph)g).createKeyIndex("_id", Vertex.class);
	    }

	}

	//System.out.println("before readline");
	while ((line=brWeb.readLine())!=null) { //System.out.println("popFOF126 "+line);
	    cntLines++;
	    if (0==(cntLines%10000)) System.out.println("line number "+cntLines);
	    if (0==(cntLines%commit_frequency)) {
		if (g instanceof TransactionalGraph) {
		    // for the sake of Titan which run indexed lookup slow unless we commit occuasionally... even if just reading
		    ((TransactionalGraph)g).commit();
		}
	    }
	    //System.out.println(line);
	    //if (line.startsWith("#")) continue;
	    //if (line.startsWith("@prefix")) continue;
	    String fields[] = splitLineIntoArray(line);
	    if (cntLines==1) {
		fieldnames = fields;
	    } else {
		Vertex vertSrc = null; 
		Vertex vertDst = null; 
		if (boolSupportsExIds) {
		    if (graph2!=null) {
			vertSrc = graph2.getVertexByExternalId(fields[idxSrc]);
		    } else {
			vertSrc = g.getVertex(fields[idxSrc]);
		    }
		    if (g instanceof GraphExternalVertexIdSupport) {
			vertDst = ((GraphExternalVertexIdSupport)g).getVertexByExternalId(fields[idxDst]);
		    } else {
			vertDst = g.getVertex(fields[idxDst]);
		    }
		} else {
		    Iterable<Vertex> viter = g.getVertices("_id", fields[idxSrc]);
		    for (Vertex vv : viter) { vertSrc = vv; break; }
		    viter = g.getVertices("_id", fields[idxDst]);
		    for (Vertex vv : viter) { vertDst = vv; break; }
		}
		if (vertSrc==null) {
		    vertSrc = g.addVertex(fields[idxSrc]);  cntVerts++;
		    if (!boolSupportsExIds) {
			vertSrc.setProperty("_id", fields[idxSrc]);
		    }
		}
		if (vertDst==null) {
		    vertDst = g.addVertex(fields[idxDst]);  cntVerts++;
		    if (!boolSupportsExIds) {
			vertDst.setProperty("_id", fields[idxDst]);
		    }
		}
		// we generate an id because graphs like TinkerGraph require a unique id.  Hopefully this will do the job
		String edgeid = "a"+vertSrc.getId().toString()+" "+fields[idxLabel]+" "+vertDst.getId().toString();  
		edgeid = line;  // a lot of lines would be duplicates with the id above and for today's test, we we think we want to treat them as different
		//if (edgeid.equals("aDICT_ASSAY_1.1_1030_PUBCHEM_BIOASSAY VALIDATES DICT_CHEMICAL_1.1_C[C@H]1CCCC(=O)CCCC=Cc2cc(O)cc(O)c2C(=O)O1")) 
		Edge edge = g.addEdge(edgeid, vertSrc, vertDst, fields[idxLabel]);  cntEdges++;
		for (int i = 0; i<fieldnames.length; i++) {
		    if ( (i==idxSrc) || (i==idxDst) || (i==idxLabel) ) {
			// skip it
		    } else if (i>=fields.length) {
			System.err.println("skipping cell: extra columns found on line: "+line);
		    } else {
			edge.setProperty(fieldnames[i],fields[i]); cntProps++;
		    }
		}
	    }
	    if (cntLines>max) break;
	    if (cntLines%100000==5) {
		long t3 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d   lines=%8d  commit_freq=%d %s \n", (t3-t0), cntEdges, cntVerts, cntVerts*1000L/(t3-t0), cntProps, cntLines, commit_frequency, whichstream  );
	    }
	}
	long t1;
	t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d   lines=%8d  %s   (before commit) \n", (t1-t0), cntEdges, cntVerts, cntVerts*1000L/(t1-t0), cntProps, cntLines, whichstream  );
	if (g instanceof TransactionalGraph) {
	    // for the sake of Titan which run indexed lookup slow unless we commit occuasionally
	    ((TransactionalGraph)g).commit();
	}
	t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d   lines=%8d  %s   (after commit) \n", (t1-t0), cntEdges, cntVerts, cntVerts*1000L/(t1-t0), cntProps, cntLines, whichstream  );
    }

    /**
     * Populates the specified graph with csv-formatted vertex 
     * information obtained from the specified web url.
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g 
     * @param strURL 
     * @param graphshortname 
     * @param max 
     */
    public void populateFromVertexURL(Graph g, String strURL, String graphshortname, long max) throws FileNotFoundException, IOException {
	long t0 = System.currentTimeMillis();
	URL url; 
	{
	    //http://sg01.rescloud.ibm.com/ccjason/ldbm_csvs/parseout.www.php?filename=place.csv_ap
	    //url = new URL("http://sg01.rescloud.ibm.com/ccjason/census_small/"+whichstatements+".ttl");
	    url = new URL(strURL);
	}
	System.out.println("fetching url: "+url + " with Java CSV URL loader");
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
	LoadCSV lcsv = new LoadCSV(); 
	lcsv.populateFromCSVVertexStream(g, brWeb, max, graphshortname); //JNIGen.println("ln 378");
	brWeb.close();
	isWeb.close();
	if (g instanceof TransactionalGraph) {
	    TransactionalGraph tg = (TransactionalGraph)g;
	    tg.commit();
	} long t1 = System.currentTimeMillis();  System.out.println("pFEU240 time="+(t1-t0)+"ms");
    }

    /**
     * Populates the specified graph with csv-formatted vertex 
     * information obtained from the specified file.
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g 
     * @param strURL 
     * @param graphshortname 
     * @param max 
     */
    public void populateFromVertexFile(Graph g, String fn, String graphshortname, long max) throws FileNotFoundException, IOException {
	long t0 = System.currentTimeMillis();
	boolean done = false;
	if (true) {
	    if (g instanceof CSVFileLoadingGraph) {
		System.out.println("fetching file: "+fn + " with C++ CSV file loader");
		((CSVFileLoadingGraph)g).addCSVVertexFile(fn, max);
		done = true;
		long t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms    %s   \n", (t1-t0), graphshortname  );
	    }
	}
	if (!done) {
	    System.out.println("fetching file: "+fn + " with Java CSV file loader");
	    InputStream is;
	    is = new FileInputStream(fn);
	    BufferedReader br = new BufferedReader( new InputStreamReader(is, "UTF-8")); //JNIGen.println("ln 376");
	    LoadCSV lcsv = new LoadCSV(); 
	    lcsv.populateFromCSVVertexStream(g, br, max, graphshortname); //JNIGen.println("ln 378");
	    br.close();
	    is.close();
	}
	if (g instanceof TransactionalGraph) {
	    TransactionalGraph tg = (TransactionalGraph)g;
	    tg.commit();
	} long t1 = System.currentTimeMillis();  System.out.println("pFVF263 time="+(t1-t0)+"ms");
    }

    /**
     * Populates a graph from a CSV edge file at the specified Web 
     * URL. 
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g 
     * @param strURL 
     * @param graphshortname 
     * @param max 
     */
    public void populateFromEdgeURL(Graph g, String strURL, String graphshortname, long max) throws FileNotFoundException, IOException {
	long t0 = System.currentTimeMillis();
	URL url; 
	{
	    //http://sg01.rescloud.ibm.com/ccjason/ldbm_csvs/parseout.www.php?filename=place.csv_ap
	    //url = new URL("http://sg01.rescloud.ibm.com/ccjason/census_small/"+whichstatements+".ttl");
	    url = new URL(strURL);
	}
	System.out.println("fetching url: "+url + " with Java CSV URL loader");
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
	LoadCSV lcsv = new LoadCSV(); 
	lcsv.populateFromCSVEdgeStream(g, brWeb, max, graphshortname); //JNIGen.println("ln 378");
	brWeb.close();
	isWeb.close();
	if (g instanceof TransactionalGraph) {
	    TransactionalGraph tg = (TransactionalGraph)g;
	    tg.commit();
	} long t1 = System.currentTimeMillis();  System.out.println("pFEU308 time="+(t1-t0)+"ms");
    }
    /**
     * Populates graph with content from a CSV file.
     * 
     * @author ccjason (11/6/2014)
     * 
     * @param g 
     * @param fn 
     * @param graphshortname 
     * @param max 
     */
    public void populateFromEdgeFile(Graph g, String fn, String graphshortname, long max) throws FileNotFoundException, IOException {
	long t0 = System.currentTimeMillis();
	boolean done = false;
	if (true) {
	    if (g instanceof CSVFileLoadingGraph) {
		System.out.println("fetching file: "+fn + " with C++ CSV file loader");
		((CSVFileLoadingGraph)g).addCSVEdgeFile(fn, max);
		done = true;
		long t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms    %s   \n", (t1-t0), graphshortname  );
	    }
	}
	if (!done) {
	    System.out.println("fetching file: "+fn + " with Java CSV file loader");
	    InputStream is;
	    is = new FileInputStream(fn);
	    BufferedReader br = new BufferedReader( new InputStreamReader(is, "UTF-8")); //JNIGen.println("ln 376");
	    LoadCSV lcsv = new LoadCSV(); 
	    lcsv.populateFromCSVEdgeStream(g, br, max, graphshortname); //JNIGen.println("ln 378");
	    br.close();
	    is.close();
	}
	if (g instanceof TransactionalGraph) {
	    TransactionalGraph tg = (TransactionalGraph)g;
	    tg.commit();
	}   long t1 = System.currentTimeMillis();  System.out.println("pFEF331 time="+(t1-t0)+"ms");
    }

}
