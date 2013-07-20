/*
 * Copyright 2013 William Bernardet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.japi.checker.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the CLI command
 * 
 * @author William Bernardet
 * 
 */
public class TestMain {
	private final PrintStream outDefault = System.out;
	private final PrintStream errDefault = System.err;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private File reference;
	private File newVersion;
	
	/**
	 * Setup the console recording
	 * @throws IOException
	 */
	@Before
	public void setUpStreams() throws IOException {
	    System.setOut(new TeePrintStream(System.out, outContent, false));
	    System.setErr(new TeePrintStream(System.err, errContent, false));
	}
	
	@Before
	public void detectArtifacts() {
        for (String file : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (file.contains("reference-test-jar")) {
                reference = new File(file);
            } else if (file.contains("new-test-jar")) {
                newVersion = new File(file);
            }
        }		
	}
	
	/**
	 * Disable the console recording 
	 */
	@After
	public void cleanUpStreams() {
	    System.setOut(outDefault);
	    System.setErr(errDefault);
	    outContent.reset();
	    errContent.reset();
	}	
	
	/**
	 * The help is display if command line is incomplete
	 */
	@Test
	public void testHelp() {
		String[] args = new String[0];
		int result = new Main(args).run();

		// Check that help is dumped on the console
		assertTrue(outContent.toString().contains("Check API and ABI compatiblity of Java libraries."));
		assertTrue(errContent.toString().contains("Error parsing command line:"));
		assertEquals(-1, result);
	}

	
	@Test
	public void testHelpCmd() {
		String[] args = new String[1];
		args[0] = "--help";
		int result = new Main(args).run();

		// Check that help is dumped on the console
		assertTrue(outContent.toString().contains("usage: japi-checker-cli"));
		assertFalse(outContent.toString().contains("Error:"));
		assertEquals(0, result);
	}
	
	@Test
	public void testHelpCmdShort() {
		String[] args = new String[1];
		args[0] = "-h";
		int result = new Main(args).run();

		// Check that help is dumped on the console
		assertTrue(outContent.toString().contains("usage: japi-checker-cli"));
		assertFalse(outContent.toString().contains("Error:"));
		assertEquals(0, result);
	}

	/**
	 * Basic source compilation issues.
	 */
	@Test
	public void testSimpleValidation() {
		String[] args = new String[2];
		args[0] = reference.getAbsolutePath();
		args[1] = newVersion.getAbsolutePath();
		int result = new Main(args).run();

		// Check that help is dumped on the console
		assertTrue(outContent.toString().contains("Error count: 7"));
		assertTrue(outContent.toString().contains("Warning count: 0"));
		assertTrue(outContent.toString().contains("ERROR:"));
		assertFalse(outContent.toString().contains("WARNING:"));
		assertEquals(-1, result);
	}

	/**
	 * Full binary compatibility check
	 */
	@Test
	public void testBinaryValidation() {
		String[] args = new String[3];
		args[0] = "-bin";
		args[1] = reference.getAbsolutePath();
		args[2] = newVersion.getAbsolutePath();
		int result = new Main(args).run();

		// Check that help is dumped on the console
		assertTrue(outContent.toString().contains("Error count: 65"));
		assertTrue(outContent.toString().contains("Warning count: 3"));
		assertTrue(outContent.toString().contains("ERROR:"));
		assertTrue(outContent.toString().contains("WARNING:"));
		assertEquals(-1, result);
	}
}
