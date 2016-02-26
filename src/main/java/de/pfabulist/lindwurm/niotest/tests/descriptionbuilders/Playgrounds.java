package de.pfabulist.lindwurm.niotest.tests.descriptionbuilders;

import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.Tests09WrongProvider;
import de.pfabulist.lindwurm.niotest.tests.topics.Closable;
import de.pfabulist.lindwurm.niotest.tests.topics.NotDefaultFileSystem;
import de.pfabulist.lindwurm.niotest.tests.topics.Readonly;
import de.pfabulist.lindwurm.niotest.tests.topics.SameFSDifferentStore;
import de.pfabulist.lindwurm.niotest.tests.topics.SecondFileSystem;
import de.pfabulist.lindwurm.niotest.tests.topics.SizeLimit;
import de.pfabulist.lindwurm.niotest.tests.topics.WorkingDirectoryInPlaygroundTree;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static de.pfabulist.kleinod.nio.PathIKWID.absoluteGetRoot;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2016, Stephan Pfab
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

public class Playgrounds<T> extends DescriptionBuilder<T> {
    public Playgrounds( FSDescription descr, T t ) {
        super( descr, t );
    }

    public Playgrounds<T> std( Path root ) {
        descr.removeTopic( Readonly.class );
        descr.props.put( "playground", root );

        if( !root.isAbsolute() ) {
            throw new IllegalArgumentException( "root path must be nonnull and absolute " + root );
        }

        if( !absoluteGetRoot( root ).equals( absoluteGetRoot( root.getFileSystem().getPath( "" ).toAbsolutePath() ))) {
            descr.removeTopic( WorkingDirectoryInPlaygroundTree.class );
        }

        if( root.getFileSystem().equals( FileSystems.getDefault() ) ) {
            descr.removeTopic( NotDefaultFileSystem.class );
        }

        return this;
    }

    public ReadonlyPlayground<Playgrounds<T>> readonly( Path root ) {
        return new ReadonlyPlayground<>( descr, this );
    }

    public Playgrounds<T> closable( Path root ) {
        descr.closedFSVars = new FSDescription.ClosedFSVars( root );
        return this;
    }

    public Playgrounds<T> noClosable() {
        descr.removeTopic( Closable.class );
        return this;
    }

    public Playgrounds<T> differentProvider( Path root ) {
        descr.props.put( Tests09WrongProvider.OTHER_PROVIDER_PLAYGROUND, root );
        return this;
    }

    public Playgrounds<T> sameProviderDifferentFileSystem( Path root ) {
        descr.props.put( SecondFS.PLAYGROUND2, root );

        if( !root.isAbsolute() ) {
            throw new IllegalArgumentException( "root path must be nonull and absolute " + root );
        }
        return this;
    }

    public Playgrounds<T> noSameProviderDifferentFileSystem() {
        descr.removeTopic( SecondFileSystem.class );
        return this;
    }

    public Playgrounds<T> sizeLimitedPlayground( Path limited ) {
        descr.props.put( "sizeLimitedPlayground", limited );
        return this;
    }

    public Playgrounds<T> noSizeLimit() {
        descr.removeTopic( SizeLimit.class );
        return this;
    }

    public Playgrounds<T> sameFileSystemDifferentStore( Path path ) {
        descr.props.put( "differentStore", path );
        return this;
    }

    public Playgrounds<T> noSameFileSystemDifferentStore() {
        descr.removeTopic( SameFSDifferentStore.class );
        return this;
    }


    public T next() {
        return t;
    }
}
