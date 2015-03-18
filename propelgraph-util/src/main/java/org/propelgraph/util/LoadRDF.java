package org.propelgraph.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import org.propelgraph.RDFHTTPLoadingGraph;

import org.propelgraph.ClearableGraph;
import org.propelgraph.GraphExternalVertexIdSupport;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
//import com.ibm.research.systemg.nativestore.JNIGen;

/**
 * This class provides functionality for loading RDF Turtle ( 
 * and n-triple formatted files. It will attempt to take 
 * advantage of RDF loading functionality built in to a given
 * graph implementation if it uses a known public open 
 * interface.  If the graph has no such method, then this code 
 * implements the Turtle (and n-triple) loading.  It 
 * nevertheless still tries to take advantage of other known 
 * public interfaces the graph has that might speed up the 
 * process. 
 * 
 * @author ccjason (11/6/2014)
 */
public class LoadRDF {

    static protected Pattern patCurieWhole = Pattern.compile("^([a-z]+):([a-zA-Z]+)$");
    static protected Pattern patCurieMid = Pattern.compile("([a-z]+):([a-zA-Z]*)");


    /**
     * Set to true if even statements with literal objects will be 
     * represented as edges.  If false, properties are used to 
     * represent these triples. 
     *  
     * Note: Using properties will not work right if the RDF a given
     * subject has multiple values for a given "property/predicate" 
     * because TinkerPop allows only one property with a given name 
     * on a given vertex.) 
     * 
     * @author ccjason (3/31/2014)
     */
    public boolean boolAlwaysEdges = true; 


    class LParser {
	String line;
	int idxNext;
	LParser(String str) { line = str; idxNext = 0; //System.out.println(line); 
	}
	public String eol = "<<<<eol>>>>";

	Object parseNTurtleElement() {
	    if (idxNext>=line.length()) return eol;
	    while ((line.charAt(idxNext)==' ') || (line.charAt(idxNext)=='\t')) {
		idxNext++;
		if (idxNext == line.length()) return eol;
	    }
	    char ch = line.charAt(idxNext);
	    Object el;
	    if ('<'==ch) {
		int idx = line.indexOf('>', idxNext+1);
		if (idx<0) throw new RuntimeException("trouble parsing line: "+line);
		el = line.substring(idxNext,idx+1);
		idxNext = idx+1;
	    } else if ('"'==ch) {
		int idxMax = line.length();
		int idx = idxNext+1;
		while (idx < idxMax) {
		    char c = line.charAt(idx);
		    if ('\\'==c) {
			idx++;
		    } else if ('"'==c) {
			break;
		    }
		    idx++;
		}
		if (idx>=idxMax) throw new RuntimeException("trouble parsing line: "+line);
		// let's see if it's followed up by a ^^
		idx++;
		int idxAfterSecondQuote = idx;
		if ((idx+3<=line.length()) && ('^'==line.charAt(idx)) && ('^'==line.charAt(idx+1)) ) {
		    // we expect curies and <url> value, but let's just scan to the next space.
		    Matcher matsuf = patCurieMid.matcher(line);
		    if (matsuf.find(idx+2) && (idx+2 == matsuf.start()) ) {
			idx = matsuf.end();
		    } else {
			if ('<'==line.charAt(idx+2)) {
			    int i2 = idx+3;
			    while (line.charAt(i2)!='>') {
				i2++;
			    }
			    idx = i2+1;
			} else {
			    throw new RuntimeException( "didnt understand what comes after ^^ in "+line);
			}
		    }
		} else if ( (idx+3<=line.length()) && ('@'==line.charAt(idx)) && (' '!=line.charAt(idx+1)) && (' '!=line.charAt(idx+2)) && ('\t'!=line.charAt(idx+1)) && ('\t'!=line.charAt(idx+2)) ) {
		    //   ex. "hi there"@zh   or @fil  or @zh-sz 
		    idx += 3;
		    while ((idx<line.length()) && (!(line.charAt(idx)==' ')) && (!(line.charAt(idx)=='\t')) ) {
			idx++;
		    }
		}

		el = line.substring(idxNext,idx);
		idxNext = idx;
	    } else if ('.'==ch) {
		if (idxNext+1==line.length()) {
		} else {
		    //throw new RuntimeException("don't understand the dot in middle of line when parsing line: "+line);
		}
		el = ".";
		idxNext++;
	    } else if (';'==ch) {
		if (idxNext+1==line.length()) {
		} else {
		    //throw new RuntimeException("don't understand the dot in middle of line when parsing line: "+line);
		}
		el = ";";
		idxNext++;
	    } else if ('@'==ch) {
		int idx = idxNext; 
		while ((idx<line.length()) && (line.charAt(idx)!='\t') && (line.charAt(idx)!=' ')) idx++;
		String sss = line.substring(idxNext,idx);
		idxNext = idx;
		return sss;
	    } else if ( ((ch>='0') && (ch<='9')) || (ch=='-')) {
		int idx = idxNext; 
		while ((idx<line.length()) && (line.charAt(idx)!=' ') && (line.charAt(idx)!='\t')) idx++;
		String sss = line.substring(idxNext,idx);
		try {
		    Double dd = Double.parseDouble(sss);
		    idxNext = idx;
		    return dd;
		} catch (NumberFormatException exc) {
		}
		try {
		    Integer ii = Integer.parseInt(sss);
		    idxNext = idx;
		    return ii;
		} catch (NumberFormatException exc) {
		}
		throw new RuntimeException("didnt understand numeric like element: "+line);
	    } else {
		Matcher mat = LoadRDF.patCurieMid.matcher(line);
		//System.out.println(mat.find(idxNext));
		if (mat.find(idxNext) && (mat.start()==idxNext) ) {
		    int eee = mat.end();
		    el = line.substring(idxNext,eee);
		    idxNext = eee;
		} else {
		    throw new RuntimeException("trouble parsing line: "+line+"  at index "+idxNext);
		}
	    }
	    return el;
	}
    }



    enum State { BASE, SP, PO, OD };
    State state   = State.BASE;
    class Stmt {
	String s, p; Object o;
	Stmt(String s, String p, Object o) { this.s = s; this.p = p;  this.o = o; }
    }
    String subject;
    String predicate;

    List<Stmt> parseLine(String line) {
	LinkedList<Stmt> retval = new LinkedList<Stmt>();
	int idx = 0;
	LParser lparser = new LParser(line);
	while (true) {
	    Object el = lparser.parseNTurtleElement();
	    if (lparser.eol == el) return retval;
	    //System.out.println("state is "+state);
	    if (false) {
	    } else if (state==State.BASE) {
		if ( (".".equals(el)) || (";".equals(el))) {
		    throw new RuntimeException("can not parse . token where expecting subject");
		}
		subject = el.toString();
		state = State.SP;
	    } else if (state==State.SP) {
		if (".".equals(el)) {
		    state = State.BASE;
		} else if ( (";".equals(el)) ) {
		    throw new RuntimeException("can not parse "+el+" token where expecting an predicate: "+line);
		} else {
		    predicate = el.toString();
		    state = State.PO;
		}
	    } else if (state==State.PO) {
		if ( (".".equals(el)) || (";".equals(el))) {
		    throw new RuntimeException("can not parse "+el+" token where expecting an object");
		}
		retval.add(new Stmt(subject, predicate, el));
		state = State.OD;
	    } else if (state==State.OD) {
		if ( ".".equals(el) ) {
		    state = State.BASE;
		} else if ( ";".equals(el)) {
		    state = State.SP;
		} else {
		    throw new RuntimeException("can not parse "+el+" token where expecting a . or ; ");
		}
	    }
	}
    }

    /**
     * add triples to the specified graph based on the content found
     * in the provided BufferedReader. 
     * 
     * @author ccjason (11/20/2014)
     * 
     * @param g the graph to update
     * @param brWeb a buffer reader containing the turtle to parse.
     * @param whichstream a short label for this load that will be 
     *  		  listed in logs
     * @param max maximum number of triples to process
     */
    public void populateFromRDFStream(Graph g, BufferedReader brWeb, String whichstream, long max) throws FileNotFoundException, IOException {
	final int dographops = 2;
	final boolean boolUseExternalId = true;
	state = State.BASE;
	String line;
	long cntLines = 0;
	long cntProps = 0;
	long cntEdges = 0;
	long cntVerts = 0;
	long cntStatements = 0;
	long t0 = System.currentTimeMillis();
	VertexMapCache tm = new VertexMapCache(100);
	HashMap<String,String> mapCuriePrefix2URI = new HashMap<String,String>();


	//System.out.println("before readline");
	while ((line=brWeb.readLine())!=null) { //System.out.println("popFOF126 "+line);
	    cntLines++;
	    //System.out.println(line);
	    if (line.startsWith("#")) continue;
	    //if (line.startsWith("@prefix")) continue;
	    List<Stmt> stmts = parseLine(line);
	    if (dographops>0) {
		for (Stmt stmt : stmts) {
		    Iterator<Vertex> it;  //JNIGen.println("popFOF143");
		    //System.out.println(stmt.s);
		    if ((stmt.s.equals("@prefix")) || (stmt.s.equals("@PREFIX"))) {
			mapCuriePrefix2URI.put(stmt.p, stmt.o.toString());  //System.out.println( "prefix "+stmt.p+" "+stmt.o);
			continue;
		    }
		    Matcher mats = patCurieWhole.matcher(stmt.s);
		    if (mats.matches()) {
			String curiestr = stmt.s;
			String prefix = mats.group(1);
			String nn = mapCuriePrefix2URI.get(prefix+":"); 
			stmt.s = nn.substring(0,nn.length()-1) + mats.group(2) +">";
			//System.out.println( "curie remapped "+curiestr+" to "+stmt.s);
		    }
		    Vertex vSubject;
		    if ((vSubject = tm.get(stmt.s))==null) {
			if (boolUseExternalId && (g instanceof GraphExternalVertexIdSupport)) {
			    vSubject = ((GraphExternalVertexIdSupport)g).getVertexByExternalId(stmt.s);
			} else {
			    it = g.getVertices("_id",stmt.s).iterator(); //JNIGen.println("popFOF144");
			    vSubject = it.hasNext() ? it.next() : null; //JNIGen.println("popFOF145");
			}
			if (vSubject!=null) tm.put(stmt.s, vSubject);
		    }
		    if (null==vSubject) { //JNIGen.println("popFOF146");
			if (boolUseExternalId) {
			    vSubject = g.addVertex(stmt.s); 
			} else {
			    vSubject = g.addVertex(null);
			    vSubject.setProperty("_id",stmt.s);
			    cntProps++;
			}
			cntVerts++;
			tm.put(stmt.s, vSubject);
		    } //JNIGen.println("popFOF151");
		    if (stmt.p instanceof String) {
			Matcher matp = patCurieWhole.matcher(stmt.p.toString());
			if (matp.matches()) {
			    String curiestr = stmt.p.toString();
			    String prefix = matp.group(1);
			    String nn = mapCuriePrefix2URI.get(prefix+":"); 
			    stmt.p = nn.substring(0,nn.length()-1) + matp.group(2) +">";
			    //System.out.println( "curie remapped "+curiestr+" to "+stmt.p);
			}
		    }
		    if (stmt.o instanceof String) {
			Matcher mato = patCurieWhole.matcher(stmt.o.toString());
			if (mato.matches()) {
			    String curiestr = stmt.o.toString();
			    String prefix = mato.group(1);
			    String nn = mapCuriePrefix2URI.get(prefix+":"); 
			    stmt.o = nn.substring(0,nn.length()-1) + mato.group(2) +">";
			    //System.out.println( "curie remapped "+curiestr+" to "+stmt.o);
			}
		    }
		    String ooo = (stmt.o instanceof String) ? stmt.o.toString() : null;
		    if (boolAlwaysEdges) { //JNIGen.println("popFOF152");
			Vertex vObject;
			if ((ooo==null) || (vObject = tm.get(ooo))==null) {
			    if (boolUseExternalId && (g instanceof GraphExternalVertexIdSupport)) {
				vObject = ((GraphExternalVertexIdSupport)g).getVertexByExternalId(stmt.o.toString());
			    } else {
				it = g.getVertices("_id",stmt.o).iterator(); //JNIGen.println("popFOF144");
				vObject = it.hasNext() ? it.next() : null; //JNIGen.println("popFOF145");
			    }
			    if ((vObject!=null) && (ooo!=null)) tm.put(ooo, vObject);
			}
			if (null==vObject) {
			    if (boolUseExternalId && (g instanceof GraphExternalVertexIdSupport)) {
				vObject = g.addVertex(stmt.o.toString());
			    } else {
				vObject = g.addVertex(null);
				vObject.setProperty("_id",stmt.o);
				cntProps++;
			    }
			    cntVerts++;
			    if (ooo!=null) tm.put(ooo, vObject);
			}
			if (dographops>1) {
			    g.addEdge(null,vSubject,vObject,stmt.p);
			}
			cntEdges++;
		    } else {//JNIGen.println("popFOF163");
			if (stmt.o.toString().startsWith("\"")) {//JNIGen.println("popFOF164");
			    // this is a literal, not 
			    if (dographops>1) {
				vSubject.setProperty(stmt.p,stmt.o);//JNIGen.println("popFOF166");
				cntProps++;
			    }
			} else if (stmt.o.toString().startsWith("<")) {//JNIGen.println("popFOF168");
			    Vertex vObject;
			    if (boolUseExternalId && (g instanceof GraphExternalVertexIdSupport)) {
				vObject = ((GraphExternalVertexIdSupport)g).getVertexByExternalId(stmt.o.toString());
			    } else {
				it = g.getVertices("_id",stmt.o).iterator();//JNIGen.println("popFOF169");
				vObject = it.hasNext() ? it.next() : null;//JNIGen.println("popFOF170");
			    }
			    if (null==vObject) {//JNIGen.println("popFOF171");
				if (dographops>0) {
				    if (boolUseExternalId && (g instanceof GraphExternalVertexIdSupport)) {
					vObject = g.addVertex(stmt.o.toString());
				    } else {
					vObject = g.addVertex(null); // todo: make this work with systems like gbase which require an app-provided id
					if (dographops>1) {
					    vObject.setProperty("_id",stmt.o);//JNIGen.println("popFOF173");
					    cntProps++;
					}
				    }
				    cntVerts++;
				}
			    }//JNIGen.println("popFOF176");
			    if (dographops>1) {
				Edge ePred = g.addEdge(null,vSubject,vObject,stmt.p); //JNIGen.println("popFOF177");
			    }
			    cntEdges++;
			} else if ( stmt.o instanceof Double ) {
			    if (dographops>1) {
				vSubject.setProperty(stmt.p,stmt.o);//JNIGen.println("popFOF166");
				cntProps++;
			    }
			} else if ( stmt.o instanceof Integer ) {
			    if (dographops>1) {
				vSubject.setProperty(stmt.p,stmt.o);//JNIGen.println("popFOF166");
				cntProps++;
			    }
			} else {
			    throw new RuntimeException("internal error: recheck input file and parsing code");
			}
		    }
		}
	    }
	    cntStatements++;
	    if (cntStatements>=max) break;
	}//JNIGen.println("popFOF185");
	long t1 = System.currentTimeMillis();   System.out.printf("loadtime=%8d ms     edges=%8d     verts=%8d(%7d/sec)    props=%8d   lines=%8d  %s \n", (t1-t0), cntEdges, cntVerts, cntVerts*1000L/(t1-t0), cntProps, cntLines, whichstream  );
    }

    /**
     * added tripls to the specified property graph using the rdf 
     * turtle (or ntriple) content at the specified http url. 
     * 
     * @author ccjason (11/20/2014)
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
	    //http://sg01.rescloud.ibm.com/ccjason/ldbm_csvs/parseout.www.php?filename=place.csv_ap
	    //url = new URL("http://sg01.rescloud.ibm.com/ccjason/census_small/"+whichstatements+".ttl");
	    url = new URL(strURL);
	}
	//if (true && (g instanceof com.ibm.research.systemg.nativestore.tinkerpop.NSGraph)) {
	if (true && (g instanceof RDFHTTPLoadingGraph)) {
	    System.out.println("fetching url: "+url + " with C++ RDF URL loader");
	    //((com.ibm.research.systemg.nativestore.tinkerpop.NSGraph)g).add_turtle_rdf_data_from_http_url(url.toString(), max);
	    ((RDFHTTPLoadingGraph)g).add_turtle_rdf_data_from_http_url(url.toString(), max);
	    if (null!=new Object()) return; // 
	} else {
	    System.out.println("fetching url: "+url + " with Java RDF URL loader");
	}
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
	LoadRDF lrdf = new LoadRDF();  lrdf.boolAlwaysEdges = boolAlwaysEdges; //JNIGen.println("ln 377");
	lrdf.populateFromRDFStream(g, brWeb, graphshortname, max); //JNIGen.println("ln 378");
	brWeb.close();
	isWeb.close();
	if (g instanceof TransactionalGraph) {
	    TransactionalGraph tg = (TransactionalGraph)g;
	    tg.commit();
	}
    }


}
