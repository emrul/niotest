package org.opencage.niotest;


import org.opencage.kleinod.paths.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.opencage.kleinod.emergent.Todo.todo;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2013, Stephan Pfab
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
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
public abstract class StdTmpResource implements TmpResource<Path> {


    private final int kidCount;
    protected boolean createDirs = false;
    protected Path outer;
    protected boolean doCleanup = true;

    public StdTmpResource( int kidCount ) {
        this.kidCount = kidCount;
    }

    @Override
    public Path get( String prefix ) {

        Path root = null;
        try {
            root = getRoot( prefix );
        } catch( Exception e ) {
            todo(e);
        }

        if ( root == null ) {
            throw new IllegalArgumentException( "bad root" );
        }

        try {
            if ( createDirs ) {
                Files.createDirectories( root );
            }

            for ( int i = 0; i < kidCount; i++ ) {
                Files.createDirectory( root.resolve( "kid-" + i ) );
            }
        } catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            todo();
        }

        return root;
    }

    public abstract Path getRoot( String prefix ) throws Exception;

    @Override
    public void cleanup() {
        if ( outer != null && doCleanup) {
            PathUtils.delete( outer );
        }
        outer = null;
    }
}
