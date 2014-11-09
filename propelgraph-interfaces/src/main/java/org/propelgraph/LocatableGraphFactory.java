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
	static final String  SCHEME_PGGRAPH = "pggraph";


	/**
	 * If graph doesn't exist, die.  Otherwise open it.
	 */
	static final String FACTION_NEW_NEW = "nn"; 

	/**
	 * If graph doesn't exist, die.  Otherwise open it.
	 */
	static final String FACTION_DIE_OPEN = "do"; 

	/**
	 * If graph doesn't exist, create it. Otherwise empty it.
	 */
	static final String FACTION_CREATE_EMPTY = "ce";

	/**
	 * If graph does not exist, create it.  Otherwise open it.
	 */
	static final String FACTION_CREATE_OPEN = "co"; 

	/**
	 * If graph does not exist, create it.  Otherwise die.
	 */
	static final String FACTION_CREATE_DIE = "cd";  

	/**
	 * Open graph for read access. 
	 */
	static final String FMODE_READ = "r"; 

	/**
	 * Open graph for write access.
	 */
	static final String FMODE_WRITE = "rw"; 

	/**
	 * Open graph for read-add access.
	 */
	static final String FMODE_ADD = "a";


	/**
	 * This method creates a LocatableGraph object indicated by the provided urlPath.
	 * 
	 * @param urlPath
	 * @param faction - The action to take if the graph doesn't 
	 *      	  exist or does exist.  See the FACTION
	 *      	  constants of this interface.  If the action is
	 *      	  "die", then a AlreadyExistsException or
	 *      	  NotFoundException is thrown.
	 * @param fmode  - The mode the graph is in once it is opened. 
	 *      	 See the FMODE_* constants of this interface.
	 * @return
	 */
	Graph open(String urlPath, String faction, String fmode) throws AlreadyExistsException, NotFoundException, UnsupportedFActionException;
	
	/**
	 *  This method returns a string that this class would like passed to it's create method in order
	 *  for the caller to later reinstantiate the graph object.
	 *   
	 *  The calling code needs the returned string to be in a format that will allow the calling code to do 
	 *  an operation like the following to form a url:
	 * 
	 * url = "pggraph:"+classname+urlpath+"?extraparam1=val1&extraparam2=val2"
	 * 
	 * where urlpath includes a leading /.
	 * 
	 * @return  The returned url string must comply with the example above.
	 */
	String getGraphURL(Graph graph);
}
