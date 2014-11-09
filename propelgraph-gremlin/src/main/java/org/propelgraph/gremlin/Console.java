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
package org.propelgraph.gremlin;

import com.tinkerpop.gremlin.Imports;
import com.tinkerpop.gremlin.groovy.Gremlin;
import jline.History;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.InteractiveShellRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.tinkerpop.gremlin.groovy.console.ErrorHookClosure;
import com.tinkerpop.gremlin.groovy.console.NullResultHookClosure;
import com.tinkerpop.gremlin.groovy.console.PromptClosure;
import com.tinkerpop.gremlin.groovy.console.ResultHookClosure;

/**
 * A pretty standard implementation of a Gremlin shell.  
 */
public class Console {

    private static final String HISTORY_FILE = ".gremlin_groovy_history";
    private static final String STANDARD_INPUT_PROMPT = "gremlin> ";
    private static final String STANDARD_RESULT_PROMPT = "==>";

    public Console(final IO io, final String inputPrompt, final String resultPrompt, final String initScriptFile) {
        io.out.println();
        io.out.println("         \\,,,/");
        io.out.println("         (o o)");
        io.out.println("-----oOOo-(_)-oOOo-----");

        final Groovysh groovy = new Groovysh();
        groovy.setResultHook(new NullResultHookClosure(groovy));
        for (String imps : Imports.getImports()) {
            groovy.execute("import " + imps);
        }
        groovy.execute("import com.tinkerpop.gremlin.Tokens.T");
        groovy.execute("import com.tinkerpop.gremlin.groovy.*");
        groovy.execute("import groovy.grape.Grape");

        groovy.execute("import org.propelgraph.util.CreateGraph");
        groovy.execute("import org.propelgraph.util.LoadCSV");
        groovy.execute("import org.propelgraph.util.LoadMetis");
        groovy.execute("import org.propelgraph.util.analytics.CollaborativeFilter");

        groovy.setResultHook(new ResultHookClosure(groovy, io, resultPrompt));
        groovy.setHistory(new History());

        final InteractiveShellRunner runner = new InteractiveShellRunner(groovy, new PromptClosure(groovy, inputPrompt));
        runner.setErrorHandler(new ErrorHookClosure(runner, io));
        try {
            runner.setHistory(new History(new File(System.getProperty("user.home") + "/" + HISTORY_FILE)));
        } catch (IOException e) {
            io.err.println("Unable to create history file: " + HISTORY_FILE);
        }

        Gremlin.load();
        initializeShellWithScript(io, initScriptFile, groovy);

        try {
            runner.run();
        } catch (Error e) {
            //System.err.println(e.getMessage());
        }
    }

    public Console() {
        // opted to keep this constructor after adding the one with the initialization script file in case something
        // is using it...not sure if anything is.  if not, it can go.
        this(null);
    }

    public Console(final String initScriptFile) {
        this(new IO(System.in, System.out, System.err), STANDARD_INPUT_PROMPT, STANDARD_RESULT_PROMPT, initScriptFile);
    }

    private void initializeShellWithScript(final IO io, final String initScriptFile, final Groovysh groovy) {
        if (initScriptFile != null) {
            String line = "";
            try {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(
                                                                                       new FileInputStream(initScriptFile), Charset.forName("UTF-8")));
                while ((line = reader.readLine()) != null) {
                    groovy.execute(line);
                }

                reader.close();
            } catch (FileNotFoundException fnfe) {
                io.err.println(String.format("Gremlin initialization file not found at [%s].", initScriptFile));
                System.exit(1);
            } catch (IOException ioe) {
                io.err.println(String.format("Bad line in Gremlin initialization file at [%s].", line));
                System.exit(1);
            }
        }
    }

    public static void main(final String[] args) {
        new Console(args.length == 1 ? args[0] : null);
    }
}
