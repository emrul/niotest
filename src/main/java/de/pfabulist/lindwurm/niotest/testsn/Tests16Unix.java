package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2014, Stephan Pfab
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
*
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
public abstract class Tests16Unix extends Tests13FileStore {
    public Tests16Unix( Capa capa) {
        super(capa);
//        attributes.put("Unix", capa::isUnix);
//        attributes.put( "Posix", capa::hasPosixAttributes );
    }

//    @Test
//    public void testDefaultOwnerIsFoundInLookpupService() throws IOException {
//        assumeThat( capabilities.supportsPrincipals(), is(true));
//        assumeThat( capabilities.supportsPosixAttributes(), is(true));
//
//        UserPrincipal owner = Files.readAttributes(getDefaultPath(), PosixFileAttributes.class).owner();
//
//        assertThat( owner, is(FS.getUserPrincipalLookupService().lookupPrincipalByName(owner.getName())));
//    }
//
//    @Test
//    public void testOwnerByTwoMethods() throws IOException {
//        assumeThat( capabilities.supportsPrincipals(), is(true));
//        assumeThat( capabilities.supportsPosixAttributes(), is(true));
//
//        assertThat( Files.getOwner( getDefaultPath()),
//                is(Files.readAttributes(getDefaultPath(), PosixFileAttributes.class).owner()));
//    }
//
    @Test
    public void testPosixGetAttributeView() throws IOException {
        PosixFileAttributes pfa = Files.readAttributes( fileTA(), PosixFileAttributes.class);

        assertThat( pfa, notNullValue());
    }

    @Test
    public void testUnixSeparatorIsSlash() {
        assertThat( FS.getSeparator(), is("/"));
    }


}
