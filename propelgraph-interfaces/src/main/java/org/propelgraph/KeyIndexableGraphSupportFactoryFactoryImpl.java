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
//import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
//import org.propelgraph.AlreadyExistsException;
//import org.propelgraph.NotFoundException;

/**
 * This class implements most of the glue needed to implement
 * the KeyIndexableGraphSupportFactory support. 
 * 
 * @author ccjason (05/04/2015)
 */
public class KeyIndexableGraphSupportFactoryFactoryImpl {
	//static final String URLPREFIX = LocatableGraphFactory.SCHEME_PGGRAPH+":";
	//static final String URLPREFIXD = LocatableGraphDirectory.SCHEME_PGGRAPHDIR+":";
	//static final LocatableGraphFactoryFactoryImpl lgffi = new LocatableGraphFactoryFactoryImpl();

	static class SimpleKeyIndexableGraphSupport implements KeyIndexableGraphSupport {
		KeyIndexableGraph gr;
		public SimpleKeyIndexableGraphSupport( KeyIndexableGraph gr) {
			this.gr = gr;
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
        public static final KeyIndexableGraphSupport getKeyIndexableGraphSupport( Graph graph ) {
			if (graph==null) throw new NullPointerException();
			if (graph instanceof KeyIndexableGraph ) {
				//String canname = graph.getClass().getCanonicalName();
				SimpleKeyIndexableGraphSupport retval = new SimpleKeyIndexableGraphSupport( (KeyIndexableGraph)graph );
				return retval;
			} else {
				return null; // unsupported (todo: return a more informative value)
			}
		}	

}
