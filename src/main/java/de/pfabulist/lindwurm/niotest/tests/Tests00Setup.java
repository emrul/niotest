package de.pfabulist.lindwurm.niotest.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2015, Stephan Pfab
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Stephan Pfab BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * **** END LICENSE BLOCK ****
 */

public abstract class Tests00Setup {

    public final FSDescription description;
    protected final FileSystem FS;

    @Rule
    public TestAnnotated annotated = new TestAnnotated();

    @Rule
    public TestName testMethodName = new TestName();

//    @Rule
//    @SuppressFBWarnings()
//    public Timeout globalTimeout = Timeout.seconds( 40 );

    protected Tests00Setup( FSDescription description ) {
        this.description = description;
        FS = description.get( Path.class, "playground" ).getFileSystem();
    }

    @Before
    public void setup() {
        description.markHits( testMethodName );
        String name = testMethodName.getMethodName();

        if( null != annotated.getCats() ) {
            for( int i = 0; i < annotated.getCats().value().length; i++ ) {
                Class<?> top = annotated.getCats().value()[ i ];
                assumeThat( "test: " + name + " not run because FS is/has  not: " + top.getSimpleName(), description.provides( top ), is( true ));
            }
        }

        assumeThat( "known bug: " +  name,description.isBug( testMethodName ), is( false ) );

    }

    public Path getCommon() {
        return description.get( Path.class, "playground" ).resolve( "niotestCommon" );
    }

}