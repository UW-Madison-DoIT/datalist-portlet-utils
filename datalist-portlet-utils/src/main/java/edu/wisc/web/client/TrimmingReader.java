/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.wisc.web.client;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Performs the same function as {@link String#trim()} on the BEGINING of the input. No trimming is done at the end.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class TrimmingReader extends FilterReader {
    private boolean trimming = true;

    public TrimmingReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        while (trimming) {
            final int r = super.read();
            if (r == -1) {
                return r;
            }
            if (((char)r) > ' ') {
                trimming = false;
                return r;
            }
        }
        
        return super.read();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        while (trimming) {
            final int r = super.read(cbuf, off, len);
            if (r == -1) {
                return r;
            }
            
            for (int trim = 0; trim < len; trim++) {
                if (cbuf[trim + off] > ' ') {
                    trimming = false;
                    System.arraycopy(cbuf, trim + off, cbuf, off, len - trim);
                    int r2 = super.read(cbuf, len - (off + trim), trim);
                    
                    return (r - trim) + Math.max(r2, 0);
                }
            }
        }
        
        return super.read(cbuf, off, len);
    }
}