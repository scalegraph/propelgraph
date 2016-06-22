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
package org.propelgraph;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.propelgraph.AlreadyExistsException;
import org.propelgraph.NotFoundException;

/**
 * This class implements most of the glue needed to implement
 * the LocatableGraphFactory support. 
 * 
 * @author ccjason (11/9/2014)
 */
public class LocatableGraphFactoryFactoryImpl {
	static final String URLPREFIX = LocatableGraphFactory.SCHEME_PGGRAPH+":";
	static final String URLPREFIXD = LocatableGraphDirectory.SCHEME_PGGRAPHDIR+":";
	static final LocatableGraphFactoryFactoryImpl lgffi = new LocatableGraphFactoryFactoryImpl();

    /**
     * the value of the specified optional parameter of the 
     * specified url. Unlike the other method with this name, this 
     * one lets the caller pass in a default value to be returned if 
     * the parameter was not found.   If the parameter is specified 
     * more than once in the url, the value of the last instance is 
     * returned. 
     * 
     * @author ccjason (11/9/2014)
     * 
     * @param strP 
     * @param urlPath 
     * @param defaultval 
     * 
     * @return String 
     */
    public static final String parseForURLParameter( String strP, String urlPath, String defaultval ) {
		//String strP = "&hostname=";
		int idx0 = urlPath.indexOf('?');  if (idx0<0) return null;
		int idx1 = urlPath.lastIndexOf(strP,idx0);
		if (idx1<idx0) return defaultval;
		int idx2 = idx1+(strP.length());
		int idx3 = urlPath.indexOf('&',idx2);
		if (idx3<0) idx3 = urlPath.length();
		String retval = urlPath.substring(idx1+(strP.length()), idx3);
		if ("null".equals(retval)) {
			retval = null;
		}
		return retval;
    }
	
	/**
	 * returns the value for the specified parameter encoded in the 
	 * specified url string.  This helper method is useful to 
	 * implementations of LocatableGraphFactory. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param paramprefix   the parameter string preceding the value
	 *             of interest. For example, if one wants the
	 *             "hostname" parameter of a url, the value passed
	 *             here should be "&hostname=";
	 * @param urlstring
	 * 
	 * @return String 
	 *  
	 * @throws RuntimeException thrown if the parameter was not 
	 *        found.
	 */
	public static final String parseForURLParameter( String paramprefix, String urlstring ) {
		//String strP = "&hostname=";
		int idx0 = urlstring.indexOf('?');  if (idx0<0) return null;
		int idx1 = urlstring.indexOf(paramprefix,idx0);
		if (idx1<0) throw new RuntimeException("invalid url");
		int idx2 = idx1+(paramprefix.length());
		int idx3 = urlstring.indexOf('&',idx2);
		if (idx3<0) idx3 = urlstring.length();
		String retval = urlstring.substring(idx1+(paramprefix.length()), idx3);
		if ("null".equals(retval)) {
			retval = null;
		}
		return retval;
	}
	
	private static LocatableGraphFactory getGraphFactoryFromFactoryClassName(String classname) {
		ClassLoader clldr = classname.getClass().getClassLoader();
		Object obj = null;
		try {
			obj = clldr.loadClass(classname).newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		LocatableGraphFactory lgf = (LocatableGraphFactory)obj;
		return lgf;
	}
	
	/**
	 * returns the graph factory that could be used to reconstruct 
	 * this graph if passed the proper graph url.   
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param graph 
	 * 
	 * @return LocatableGraphFactory 
	 */
	protected static LocatableGraphFactory getGraphFactory(Graph graph ) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		//if (null==graphurl) throw new NullPointerException();
		String classname; 
		if (graph instanceof LocatableGraph) {
			LocatableGraph g2 = (LocatableGraph)graph;
			classname = g2.getFactoryClassName();
		} else {
			String classname2 = graph.getClass().getName();
			if ("com.thinkaurelius.titan.core.TitanGraph".equals(classname2)) {
				classname = "blahblah";
				throw new RuntimeException("we don't yet support locatable urls for that graph type: "+classname2);
			} else if ("com.tinkerpop.blueprints.impls.tg.TinkerGraph".equals(classname2)) {
				classname = "blahblah";
				throw new RuntimeException("we don't yet support locatable urls for that graph type: "+classname2);
			} else if ("com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph".equals(classname2)) {
				classname = "blahblah";
				throw new RuntimeException("we don't yet support locatable urls for that graph type: "+classname2);
			} else {
				throw new RuntimeException("unlocatable graph");
			}
		}
		LocatableGraphFactory lgf = getGraphFactoryFromFactoryClassName(classname);
		return lgf;
	}

	/**
	 * returns a LocatableGraphFactory that is capable of 
	 * rehydrating the graph specified by the graph url. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param graphurl 
	 * 
	 * @return LocatableGraphFactory 
	 */
	public static LocatableGraphFactory getGraphFactory(String graphurl ) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		//if (null==graphurl) throw new NullPointerException();
		
		if (!graphurl.startsWith(URLPREFIX)) {
			throw new RuntimeException("incorrect graph url prefix");
		}
		int idxClassNameEnd = graphurl.indexOf('/',URLPREFIX.length());
		if (idxClassNameEnd<=0) {
			throw new RuntimeException("invalid class name in graph url");
		}
		String classname = graphurl.substring(URLPREFIX.length(), idxClassNameEnd );
		Class classNowLoaded; 
		try {
			//ClassLoader clldr = lgffi.getClass().getClassLoader();  System.out.println("classloader is "+clldr);
			//ClassLoader clldr = classname.getClass().getClassLoader();  System.out.println("classloader is "+clldr);
			//classNowLoaded = clldr.loadClass(classname); 
			classNowLoaded = Class.forName(classname); 
		} catch (ClassNotFoundException exc) {
			System.out.println( "You probably need to adjust your classpath.  If you're using maven, uncomment the appropriate part of pom.xml.  The PGAPISamples's pom.xml for help." );
			throw new ClassNotFoundException(exc.getMessage());
		}
		Object obj = classNowLoaded.newInstance();
		LocatableGraphFactory lgf = (LocatableGraphFactory)obj;
		return lgf;
	}
	
	/**
	 * Returns a hydrated graph given the graph url specified. This 
	 * helper method exists because we want to support graph urls on 
	 * some graph implementations that have not yet begun to support 
	 * the LocatableGraph interface. 
	 *  
	 * See the {@link 
	 * LocatableGraphFactory#open(String,String,String)} method for 
	 * information about the semantics of this method. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param graphurl 
	 * @param faction 
	 * @param fmode 
	 * 
	 * @return Graph 
	 */
	public static Graph openGraph( String graphurl, String faction, String fmode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
		LocatableGraphFactory lgf = getGraphFactory(graphurl);
		Graph retval = lgf.open(graphurl,faction,fmode);
		return retval;
	}
	
	/**
	 * the url string that can be used to later rehyrate this graph. 
	 * This is a helper method that attempts to handle even a few 
	 * Graph implementations that don't yet support the 
	 * LocatableGraph interface. 
	 * 
	 * See the {@link 
	 * LocatableGraphFactory#open(String,String,String)} method for 
	 * information about the semantics of this method. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param graph 
	 * 
	 * @return String 
	 */
	public static String getGraphURL(Graph graph) {
		// note: this is implemented in a way that it doesn't need to access any graph classes to compile.
		String strNameOfGraphFactoryClass = null;
		if (graph instanceof LocatableGraph) {
			LocatableGraph lg = (LocatableGraph)graph;
			strNameOfGraphFactoryClass = lg.getFactoryClassName();
		} else {
			Class clsGraph = graph.getClass();
			while (true) {
				String classnameGraph = clsGraph.getName();
				if ("java.lang.Object".equals(classnameGraph)) {
					throw new RuntimeException("graph parameter is not of a yet supported class: "+(graph.getClass().getName()));
				}
				System.out.println(classnameGraph);
				if ("com.tinkerpop.blueprints.impls.tg.TinkerGraph".equals(classnameGraph)) {
					strNameOfGraphFactoryClass = "org.propelgraph.TinkerGraphLocatableGraphFactory";
					break;
				}
				clsGraph = clsGraph.getSuperclass();
			}
			
		}
		LocatableGraphFactory lgf = getGraphFactoryFromFactoryClassName(strNameOfGraphFactoryClass);
		String retval = lgf.getGraphURL(graph);
		return retval;
	}
	
	/**
	 * returns a directory object described by the provided graph 
	 * directory url. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param graphdirurl 
	 * 
	 * @return LocatableGraphDirectory 
	 */
	public static LocatableGraphDirectory getGraphDirectory(String graphdirurl ) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		//if (null==graphdirurl) throw new NullPointerException();
		
		if (!graphdirurl.startsWith(URLPREFIXD)) {
			throw new RuntimeException("incorrect graph dir url prefix");
		}
		int idxClassNameEnd = graphdirurl.indexOf('/',URLPREFIXD.length());
		if (idxClassNameEnd<=0) {
			throw new RuntimeException("invalid class name in graph url");
		}
		String classname = graphdirurl.substring(URLPREFIXD.length(), idxClassNameEnd );
		Class classNowLoaded; 
		try {
			classNowLoaded = Class.forName(classname); 
		} catch (ClassNotFoundException exc) {
			System.out.println( "You probably need to adjust your classpath.  If you're using maven, uncomment the appropriate part of pom.xml.  The PGAPISamples's pom.xml for help." );
			throw new ClassNotFoundException(exc.getMessage());
		}
		Object obj = classNowLoaded.newInstance();
		LocatableGraphDirectory lgd = (LocatableGraphDirectory)obj;
		lgd.init(graphdirurl);
		return lgd;
	}
	

}
