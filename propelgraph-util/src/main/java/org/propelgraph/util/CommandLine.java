package org.propelgraph.util;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;

/**
 * A simple command line parser.   This parser is very simple in
 * part due to the fact that it supports a very simple command 
 * line syntax convention. 
 *  
 * <pre>
 * Syntaxes: 
 *  
 * <code>---flag</code> a named flag 
 * <code>---xx...</code> a commented out simple named flag
 * <code>--key value</code> a key value pair 
 * <code>--xxkey value</code> a commented out key value pair 
 * <code>-l</code> (or <code>-xyz</code>) single-letter flag(s) 
 * </pre> 
 *  
 * Everything else is an position-sensitive parameter. 
 * Position-sensitive parameters can be treated as key-value 
 * parameters if the names of the positions is passed in to the 
 * parser method. 
 * 
 * @author ccjason (12/15/2014)
 */
public class CommandLine {
    public static final String NAME_TOOMANYARGUMENTS = "_too_many_arguments_to_many_arguments";

    /**
     * parse a command line, returning a map of the value extracted 
     * from the command line and an ordered array of 
     * position-sensitive parameter values. 
     *  
     * @author ccjason (12/15/2014)
     * 
     * @param args the array of strings passed to the main method
     * @param mapOptions a map to which this method will add found 
     *  		 key value pairs.  Found simple named flags
     *  		 are also added and their mapped value will
     *  		 be equal to "true".  Found single-character
     *  		 flags will be represented with a key equal
     *  		 to that single letter and a value of
     *  		 "true".
     * @param argnames an optional list of parameter names for the 
     *  	       positional parameters found.  A value of null
     *  	       is acceptable in which case none of the
     *  	       positional parameters is added to the
     *  	       mapOptions map. Within the array passed in a
     *  	       null is also acceptable in which case the
     *  	       parameter value at that position is not
     *  	       assigned to the mapOptions array.  If a
     *  	       value in this array is equal to 
     *  	       NAME_TOOMANYARGUMENTS static value pointer
     *  	       and positional parameter value is found for
     *  	       that position, then an ArrayIndexOutOfBounds
     *  	       exception is thrown. This should only be
     *  	       passed in if there is a known maximum number
     *  	       of parameters allowed.  If the
     *  	       number of found positional parameter values
     *  	       exceeds the number of named positional
     *  	       parameters, the extra parameter values will
     *  	       not be added to the mapOptions params. 
     * 
     * @return String[] and ordered sequence of found positional 
     *         parameters.  
     */
    public static String[] parse_command_args( String args[], Map<String,String> mapOptions, String argnames[]) throws ArrayIndexOutOfBoundsException {
        List<String> parms = new LinkedList<String>();
        int idx = 0;
        int argcnt = 0;
        while (idx<args.length) {
            String arg = args[idx];  //System.out.println("arg is "+arg);
            if (false) {
	    } else if (arg.startsWith("---xx")) {  // allows the caller to temporarily comment out this unary argument
	    } else if (arg.startsWith("---")) { // unary parameter
		mapOptions.put(arg,"true");
	    } else if (arg.startsWith("--xx")) {  // allows the caller to temporarily comment out this key-val arguments
		idx++;
	    } else if (arg.startsWith("--")) {  // allows the caller to temporarily comment out this key-val arguments
		String arg2 = args[idx+1]; idx++;
		mapOptions.put(arg, arg2);
	    } else if (arg.startsWith("-")) {  // one or more single-character parameters
		for (int i=1; i<arg.length(); i++) {
		    char c = arg.charAt(i);
		    mapOptions.put(""+c, "true");
		}
            } else {
                parms.add(arg);  // add it the the positional argument list
		if (null!=argnames) {  // optionally add it to the named parameter map
		    if (argcnt>=argnames.length) {
			// don't attempt to treat it as a named parameter
		    } else if (NAME_TOOMANYARGUMENTS==argnames[argcnt]) { 
			throw new ArrayIndexOutOfBoundsException("Too many parameters: "+arg);
		    } else if (null!=argnames[argcnt]) mapOptions.put(argnames[argcnt],arg);
		} else {
		    // leave unnamed                                                                                                                                                                                     
		}
                argcnt++;
            }
            idx++;
        }
        String retval[] = new String[parms.size()];
        for (int i = 0; i<retval.length; i++ ) retval[i] = parms.get(i);
        return retval;
    }

}
