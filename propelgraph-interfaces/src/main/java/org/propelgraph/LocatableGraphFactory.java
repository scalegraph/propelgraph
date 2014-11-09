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

/**
 * Implementations of this interface and the LocatableGraph interface work together to help persist and rehydrate graph
 * objects.
 * 
 * @author drewvale
 *
 */
public interface LocatableGraphFactory {
	/**
	 * the scheme of the graph url.  All graph urls should start 
	 * with this value. 
	 * 
	 * @author ccjason (11/9/2014)
	 */
	static final String  SCHEME_PGGRAPH = "pggraph";


	/**
	 * This is the value that should be passed to the open method 
	 * for a non-persistant graph.  In all situations a new graph is
	 * created. 
	 */
	static final String FACTION_NEW_NEW = "nn"; 

	/**
	 * This is the value that should be passed to the open method 
	 * for a persistant graph to indicate if the graph doesn't
	 * exist, die.  Otherwise open it. 
	 */
	static final String FACTION_DIE_OPEN = "do"; 

	/**
	 * This is the value that should be passed to the open method 
	 * for a persistant graph to indicate if the graph doesn't 
	 * exist, create it. Otherwise empty it. 
	 */
	static final String FACTION_CREATE_EMPTY = "ce";

	/**
	 * This is the value that should be passed to the open method 
	 * for a persistant graph to indicate if the graph doesn't 
	 * exist, create it.  Otherwise open it.
	 */
	static final String FACTION_CREATE_OPEN = "co"; 

	/**
	 * This is the value that should be passed to the open method 
	 * for a persistant graph to indicate if the graph doesn't 
	 * exist, create it.  Otherwise die. 
	 */
	static final String FACTION_CREATE_DIE = "cd";  

	/**
	 * This is the value that should be passed to the open method to
	 * specify the graph should be opened in read-only mode.
	 */
	static final String FMODE_READ = "r"; 

	/**
	 * This is the value that should be passed to the open method to
	 * specify the graph should be opened in read-write mode. 
	 */
	static final String FMODE_WRITE = "rw"; 

	/**
	* This is the value that should be passed to the open method to
	* specify the graph should be opened in read-write mode when new 
	* vertices and edges may be added, but none may be removed.
	*  
	* Design: we need to more clearly demonstrate the need for this 
	* mode and the semantics expected.  Until this is done, 
	* this mode is deprecated. 
	*  
	* @deprecated 
	*/
	static final String FMODE_ADD = "a";


	/**
	 * This method hydrates the Graph referenced by the provided
	 * urlPath. 
	 * 
	 * @param urlPath
	 * @param faction the action to take if the graph doesn't exist
	 *      	  or does exist.  See the FACTION_* constants of
	 *      	  this interface.  If the action is "die", then
	 *      	  a AlreadyExistsException or NotFoundException
	 *      	  is thrown.
	 * @param fmode  the mode the graph is in once it is opened. See
	 *      	 the FMODE_* constants of this interface.
	 * @return
	 */
	Graph open(String urlPath, String faction, String fmode) throws AlreadyExistsException, NotFoundException, UnsupportedFActionException;
	
	/**
	 *  returns a string that could be passed to this to the open
	 *  method to rehydrate the graph.  If the graph is a
	 *  non-persistant graph, then the url species that the open
	 *  method would return a newly constructed empty graph.
	 *   
	 *  Implementation Note: The returned value should be in the
	 *  following form in order for our other classes to be able to
	 *  parse and process it:
	 *  
	 *  <pre>
	 *  {@code
	 *  url = "pggraph:"+factoryclassname+urlpath+"?extraparam1=val1&extraparam2=val2"
	 *  }
	 *  </pre>
	 * 
	 * where urlpath includes a leading / character to denote the 
	 * end of the classname.. 
	 * 
	 * @return  a url string
	 */
	String getGraphURL(Graph graph);
}
