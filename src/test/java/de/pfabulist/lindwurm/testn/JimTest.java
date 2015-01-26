package de.pfabulist.lindwurm.testn;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import de.pfabulist.lindwurm.niotest.testsn.AllTests;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import org.junit.BeforeClass;

import static de.pfabulist.lindwurm.niotest.testsn.setup.CapBuilder00.FSType.EXT2;
import static de.pfabulist.lindwurm.niotest.testsn.setup.CapBuilder00.typ;

/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2014, Stephan Pfab
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
public class JimTest extends AllTests {

    private static Capa capa;

    @BeforeClass
    public static void before() {
        capa = typ(EXT2).yes().
                playground(Jimfs.newFileSystem(Configuration.unix().toBuilder().setAttributeViews("basic", "owner", "posix", "unix").build()).getPath("/play")).
                time().lastAccessTime(false).yes().
                closeable().playground(Jimfs.newFileSystem(Configuration.unix().toBuilder().setAttributeViews("basic", "owner", "posix", "unix").build()).getPath("/play")).yes().
                pathConstraints().noMaxFilenameLength().yes().
                symlinks().toOtherProviders(false).relativeTargets(false).yes().
                watchService().delay(5500).yes().
                bugScheme("UnsupportedAttributeThrows").
                bugScheme("Unnormalized").
                bugScheme( "HardLinkToSymLink" ). // todo
                bug("testCloseDirStreamInTheMiddleOfIteration").
                bug("testClosedFSGetFileStore").
                bug( "testCopySymLinkToItself").
                bug( "testCopyBrokenSymLinkToItself").
                nitpick( "testRegisterWatchServiceOfClosedFS", "different exception" ).
                nitpick("testAppendAndReadThrows", "IllegalArg instead Unsupported").
                build();
                
//                new FSDescription().
//                closable().yes().
//                hardLinks().toDirs(false).yes().
//                unix(true).
//                watchService().delay(5500).yes().
//                symLinks().toOtherProviders(false).yes().
    }

    public JimTest() {
        super( capa );
    }


}
