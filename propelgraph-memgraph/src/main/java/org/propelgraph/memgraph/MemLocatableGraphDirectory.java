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
package org.propelgraph.memgraph;

import org.propelgraph.LocatableGraphFactory;
import org.propelgraph.LocatableGraphDirectory;
import org.propelgraph.LocatableGraphDirectoryRecord;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MemLocatableGraphDirectory implements LocatableGraphDirectory {
    static final String PREFIXD = LocatableGraphDirectory.SCHEME_PGGRAPHDIR+":"+MemLocatableGraphDirectory.class.getName()+"/";
    static final String PREFIXG = LocatableGraphFactory.SCHEME_PGGRAPH+":"+MemLocatableGraphFactory.class.getName()+"/?";
    String dirpath;
	
    @Override
	public void init(String urlPath) {
		if (!urlPath.startsWith(PREFIXD)) throw new RuntimeException("unsupported dirPath: "+urlPath);
		int idx0 = PREFIXD.length();
		dirpath = urlPath.substring(idx0);
		int idx1 = dirpath.indexOf('?');
		if (idx1>=0) {
		    dirpath = dirpath.substring(0,idx1);
		}
	}

    @Override
	public List<LocatableGraphDirectoryRecord> getRecords() {
		// TODO Auto-generated method stub
                File dir = new File(dirpath);
                LinkedList<LocatableGraphDirectoryRecord> retval = new LinkedList<LocatableGraphDirectoryRecord>();
                String fns[] = dir.list();
                final String suffix = ".info";
                for (String fn: fns) {
                    if (fn.endsWith(suffix)) {
                        String graphname = fn.substring(0,fn.length()-suffix.length());
                        String url = PREFIXG+"&graphname="+graphname+"&dirpath="+dirpath;
                        DirRecord dr = new DirRecord(graphname, url);
                        retval.add(dr);
                    }
                }
		return retval;
	}
    class DirRecord implements LocatableGraphDirectoryRecord {
        protected String graphname;
        protected String url;
        protected DirRecord( String graphname, String url) {
            this.graphname = graphname;
            this.url = url;
        }
        @Override
        public String getURL() { return url; }
        @Override
        public String getGraphName() { return graphname; }
    }

}