package de.pfabulist.lindwurm.niotest.tests.descriptionbuilders;

import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.topics.SlowTest;

import java.nio.file.Files;
import java.nio.file.Path;

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

public class CombinedBuilder {

    private final FSDescription descr;

    public CombinedBuilder( FSDescription descr ) {
        this.descr = descr;
    }

    public static CombinedBuilder build() {
        return new CombinedBuilder( new FSDescription() );
    }

    public FSDescription done() {
        return descr;
    }

    public CombinedBuilder bug( String name, boolean actually ) {
        if ( actually ) {
            descr.addBug( name );
        }

        return this;
    }

    public CombinedBuilder bug( String name ) {
        return bug( name, true );
    }

    public CombinedBuilder nitpick( String name, String comment ) {
        return bug( name, true );
    }

    public CombinedBuilder nitpickScheme( String scheme, String comment ) {
        return bugScheme( scheme, true );
    }

    public CombinedBuilder bugScheme( String scheme, boolean val ) {
        if ( val ) {
            descr.addBugScheme( scheme );
        }

        return this;
    }

    public CombinedBuilder waitForFile( Path path ) {
        while ( !Files.exists( path )) {
            try {
                Thread.sleep( 2000 );
            } catch( InterruptedException e ) {

            }
        }

        return this;

    }

    public CombinedBuilder fastOnly() {
        descr.removeTopic( SlowTest.class );
        return this;
    }


    // ----

    public ClosablePlayground<CombinedBuilder> closable() {
        return new ClosablePlayground<>( descr, this );
    }

    public Playground<CombinedBuilder> playground() {
        return new Playground<>( descr, this );
    }

    public ReadonlyPlayground<CombinedBuilder> readonlyPlayground() {
        return new ReadonlyPlayground<>( descr, this );
    }

    public OtherProviderPlayground<CombinedBuilder> otherProviderplayground() {
        return new OtherProviderPlayground<>( descr, this );
    }

    public WatchBuilder<CombinedBuilder> watchable() {
        return new WatchBuilder<>( descr, this );
    }

    public BasicAttributesBuilder<CombinedBuilder> time() {
        return new BasicAttributesBuilder<>( descr, this );
    }
    public BasicAttributesBuilder<CombinedBuilder> basicAttributes() {
        return new BasicAttributesBuilder<>( descr, this );
    }


    public PathConstraints<CombinedBuilder> pathConstraints() {
        return new PathConstraints( descr, this );
    }

    public UnixBuilder<CombinedBuilder> unix() {
        return new UnixBuilder<>( descr, this );
    }

    public WindowsBuilder<CombinedBuilder> windows() {
        return new WindowsBuilder<>( descr, this );
    }

    public SymlinkBuilder<CombinedBuilder> symlinks() {
        return new SymlinkBuilder<>( descr, this );
    }

    public HardLinksBuilder<CombinedBuilder> hardlinks() {
        return new HardLinksBuilder<>( descr, this );
    }

    public FSCreation<CombinedBuilder> fsCreation() {
        return new FSCreation<>( descr, this );
    }

    public FileStoreBuilder<CombinedBuilder> fileStores() {
        return new FileStoreBuilder<>( descr, this );

    }

    public FileChannelBuilder<CombinedBuilder> fileChannel() {
        return new FileChannelBuilder<>( descr, this );
    }

    public AttributeBuilder<CombinedBuilder> attributes() {
        return new AttributeBuilder<>( descr, this );
    }

    public TestEnvironmentBuilder<CombinedBuilder> testEnv() { return new TestEnvironmentBuilder(descr, this); }
}
