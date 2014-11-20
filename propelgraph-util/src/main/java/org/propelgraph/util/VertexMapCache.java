package org.propelgraph.util;

import com.tinkerpop.blueprints.Vertex;
import java.util.HashMap;


/**
 * Maps from String to Vertex but can limit size of cache by 
 * removing least recently used entries.  Each get or re-put to 
 * an entry causes the entry to be touched and delays its 
 * removal. 
 *  
 * Note: this class is not visible outside this package.  It's 
 * not intended for general use and is subject to change of 
 * interface. 
 * 
 * @author ccjason (7/1/2014)
 */
class VertexMapCache  {

	class VEntry {
		String key;
		Vertex vert;
		VEntry next;
		VEntry prev;
		VEntry(String k, Vertex v) {
			key = k;
			vert = v;
		}
	}

	HashMap<String,VEntry> map = new HashMap<String,VEntry>();
	VEntry first;
	int maxsize;

	public VertexMapCache( int maxsize ) {
		if (maxsize<=0) throw new RuntimeException("why would you create such a small cache? "+maxsize);
		this.maxsize = maxsize;
		first = new VEntry(null, null);  first.next = first;  first.prev = first;
	}

	public void put(String key, Vertex val) {
		VEntry ve = map.get(key);
		if (null==ve) {
			if (map.size()<maxsize) {
				ve = new VEntry(key,val);
			} else {
				ve = first.prev;  ve.prev.next = first;   first.prev = ve.prev;
				map.remove(ve.key);
				ve.key = key;
				ve.vert = val;
			}
		} else {
			ve.prev.next = ve.next;
			ve.next.prev = ve.prev;
		}
		ve.next = first.next; first.next.prev = ve;
		ve.prev = first; first.next = ve;
		map.put(key,ve);
	}

	public Vertex get(String key) {
		VEntry ret = map.get(key);
		if (null==ret) return null;
		if (first.next != ret) {
			ret.prev.next = ret.next;
			ret.next.prev = ret.prev;
			ret.next = first.next;
			ret.prev = first;
			ret.next.prev = ret;
			ret.prev.next = ret;
		}
		return ret.vert;
	}

}
