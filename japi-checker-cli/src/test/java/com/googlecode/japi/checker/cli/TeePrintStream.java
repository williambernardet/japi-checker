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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Tee to a PrintStream and an OuputStream.
 *
 * @author William Bernardet
 *
 */
public class TeePrintStream extends PrintStream {
	private PrintStream parent;
	
	public TeePrintStream(PrintStream orig, OutputStream os, boolean autoFlush)
		      throws IOException {
		super(os, autoFlush);
		parent = orig;
	}
	
	@Override
	public void write(int b) {
		parent.write(b);
		super.write(b);
	}

	public void write(byte[] x, int o, int l) {
		parent.write(x, o, l);
		super.write(x, o, l);
	}

	@Override
	public void flush() {
		parent.flush();
		super.flush();
	}

	@Override
	public void close() {
		parent.close();
		super.close();
	}
	
}
