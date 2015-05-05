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

import java.util.Set;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.KeyIndexableGraph;

/**
 * This class implements most of the glue needed to implement
 * the KeyIndexableGraphSupportFactory support. 
 * 
 * @author ccjason (05/04/2015)
 */

public class KeyIndexableGraphSupportFactory {

	static class SimpleKeyIndexableGraphSupport implements KeyIndexableGraphSupport {
		KeyIndexableGraph gr;

		// default constructor
		public SimpleKeyIndexableGraphSupport( ) {}

		@Override
		public void setGraph( Graph graph ) {
			if (gr!=null) throw new RuntimeException("the graph has not been set");
			gr = (KeyIndexableGraph)graph;
		}

		@Override
		public <T extends Element> void dropKeyIndex(String key, Class<T> elementClass) {
			gr.dropKeyIndex(key,elementClass);
		}

		@Override
		public <T extends Element> void createKeyIndex(String key, Class<T> elementClass, final Parameter... indexParameters) {
			gr.createKeyIndex(key, elementClass, indexParameters);
		}

		@Override
		public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
			return gr.getIndexedKeys(elementClass);
		}


	}
	/**
	 * returns an object that can act as the 
	 * KeyIndexableGraphSupport object for the specified Graph 
	 * object.
	 * 
	 * @author ccjason (05/04/2015)
	 * 
	 * @return KeyIndexableGraphSupport
	 */
	public static final KeyIndexableGraphSupport getKeyIndexableGraphSupport( Graph graph ) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (graph==null) throw new NullPointerException();
		if (graph instanceof KeyIndexableGraph ) {
			String canname = graph.getClass().getCanonicalName();
			System.out.println("graph class is "+canname);
			KeyIndexableGraphSupport retval;
			if ("com.thinkaurelius.titan.core.TitanGraph".equals(canname) || 
				"com.thinkaurelius.titan.graphdb.database.StandardTitanGraph".equals(canname)
			   ) {
				Class classNowLoaded; 
				try {
					classNowLoaded = Class.forName("org.propelgraph.titan.TitanKeyIndexableGraphSupport"); 
				} catch (ClassNotFoundException exc) {
					System.out.println( "You probably need to adjust your classpath.  If you're using maven, uncomment the appropriate part of pom.xml.  The PGAPISamples's pom.xml for help." );
					throw new ClassNotFoundException(exc.getMessage());
				}
				Object obj = classNowLoaded.newInstance();
				retval = (KeyIndexableGraphSupport)obj;
			} else {
				retval = new SimpleKeyIndexableGraphSupport();
			}
			retval.setGraph( graph );
			return retval;
		} else {
			return null; // unsupported (todo: return a more informative value)
		}
	}   

}
