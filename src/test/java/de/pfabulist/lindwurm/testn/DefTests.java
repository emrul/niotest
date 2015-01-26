package de.pfabulist.lindwurm.testn;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import de.pfabulist.kleinod.os.OS;
import de.pfabulist.kleinod.paths.Pathss;
import de.pfabulist.lindwurm.niotest.testsn.AllTests;
import de.pfabulist.lindwurm.niotest.testsn.setup.CapBuilder00;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import org.junit.BeforeClass;

import static de.pfabulist.lindwurm.niotest.testsn.setup.CapBuilder00.FSType.NTFS;
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
public class DefTests extends AllTests {

    private static Capa capa;

    @BeforeClass
    public static void before() {
        
        OS os = new OS();

        capa = typ( os.isWindows() ? NTFS : CapBuilder00.FSType.EXT2 ).yes().
                playground(Pathss.getTmpDir("DefaultFileSystemTest")).
                time().lastAccessTime(!os.isWindows()).yes().
                closeable().no().
                otherProviderPlayground(Jimfs.newFileSystem(Configuration.unix().toBuilder().setAttributeViews("basic", "owner", "posix", "unix").build()).getPath("/other")).
                symlinks().onOff(!os.isWindows()). // privilege problem
                bug("testEveryChannelWriteUpdatesLastModifiedTime", os.isWindows()).
                bug("testIsSameFileOfDifferentPathNonExistingFileIsNot").
                bug( "testIsSameFileOfDifferentPathNonExistingFile2IsNot").
                bug( "testDeleteWatchedDirCancelsKeys" ).
                bug( "testDeleteWatchedDirCancelsKeys" ).
                bug( "testDeleteWatchedDirCancelsKeys" ).
                nitpick( "testIsSameFileOtherProvider", "strange anyway" ).
                build();
        }
                
//                new FSDescription().
//                    playground( Pathss.getTmpDir("DefaultFileSystemTest")).
//                    otherProviderPlayground( Jimfs.newFileSystem(Configuration.unix().toBuilder().setAttributeViews("basic", "owner", "posix", "unix").build()).getPath("/other")).
//                    closable().no().
//                    windows().onOff( os.isWindows() ).
//                    unix( os.isUnix() ).
//                    hardLinks().toDirs( false ).yes().
//                    symLinks().onOff( !os.isWindows()).
//                    time().lastAccessTime( false ).yes().
//                    bug( "testMovedWatchedDirCancelsKeys", os.isWindows()).
//                    bug( "testWatchADeleteFromAMove", os.isWindows()).
//                    bug( "testIsSameFileOfDifferentPathNonExistingFileThrows").
//                    bug( "testIsSameFileOfDifferentPathNonExistingFile2Throws").
//                    nitpick( "testIsSameFileOtherProvider" );



    public DefTests() {
        super( capa );
    }

}
