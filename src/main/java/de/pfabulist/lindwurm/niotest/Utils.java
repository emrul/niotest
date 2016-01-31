package de.pfabulist.lindwurm.niotest;

import de.pfabulist.kleinod.nio.Filess;

import java.nio.file.Path;

import static de.pfabulist.kleinod.nio.PathIKWID.childGetParent;

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
public class Utils {

    private Utils() {}

    @SuppressWarnings( "PMD.UnusedLocalVariable" )
    public static <E> int getSize( Iterable<E> it ) {
        int size = 0;
        for( E e : it ) { // NOSONAR
            size++;
        }
        return size;
    }

    public static Path createFile( Path root, byte[] content, String one, String... names ) {
        Path ret = root.resolve( one );
        for( String name : names ) {
            ret = ret.resolve( name );
        }

        Filess.createDirectories( childGetParent( ret));
        Filess.write( ret, content );

        return ret;

    }

    public static Path createDir( Path root, String one, String... names ) {
        Path ret = root.resolve( one );
        for( String name : names ) {
            ret = ret.resolve( name );
        }

        Filess.createDirectories( ret );

        return ret;
    }
}
