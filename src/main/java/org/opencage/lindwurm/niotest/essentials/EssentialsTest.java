package org.opencage.lindwurm.niotest.essentials;

import org.junit.Test;
import org.opencage.kleinod.type.ImmuDate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

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
public class EssentialsTest {

    protected EssentialParams ep;



    @Test
    public void testAAA() throws Exception {

        assertThat( "extend EssentialsTest for concrete FileSystem and set protected member", ep, notNullValue() );
        assertThat( "set a base dir path in ep", ep.getBase(), notNullValue() );

    }

    @Test
    public void testNotExists() throws Exception {

        assertThat( ep.getBase().resolve( "notthereeee" ), not( exists() ) );

    }

    @Test
    public void testCreateDir() throws IOException {

        assumeTrue( ep != null );

        Path dir = ep.getBase().resolve( ep.getDirName() + ImmuDate.now().toStringFSFriendly());
        Files.createDirectory( dir );

        assertThat( dir, exists());
    }

    @Test
    public void testRemoveDir() throws IOException {
        assumeTrue( ep != null );

        Path dir = ep.getBase().resolve( ep.getDirName() + ImmuDate.now().toStringFSFriendly());
        Files.createDirectory( dir );
        assertThat( dir, exists());

        Files.delete( dir );

        assertThat( dir, not( exists()) );

    }
}
