package de.pfabulist.lindwurm.niotest;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import de.pfabulist.lindwurm.niotest.tests.AllTests;
import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.Tests05URI;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.Collections;

import static de.pfabulist.lindwurm.niotest.tests.descriptionbuilders.CombinedBuilder.build;

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

    private static FSDescription descr;

    @BeforeClass
    public static void before() {

        descr = build().
                unix().next().
                playground().set( Jimfs.newFileSystem( Configuration.unix().toBuilder().setAttributeViews( "basic", "owner", "posix", "unix" ).build() ).getPath( "/play" ) ).
                time().noLastAccessTime().next().
                closable().playground( Jimfs.newFileSystem( Configuration.unix().toBuilder().setAttributeViews( "basic", "owner", "posix", "unix" ).build() ).getPath( "/play" ) ).
                pathConstraints().noMaxFilenameLength().next().
                //symlinks().toOtherProviders(false).relativeTargets(false).yes().
                watchable().delay( 5500 ).
                fsCreation().
                    uri( Tests05URI::toURIWithoutPath ).
                    env( Collections.singletonMap( "config",  Configuration.unix().toBuilder().setAttributeViews( "basic", "owner", "posix", "unix" ).build() ) ).
                    next().
                nitpickScheme( "UnsupportedAttributeThrows", "IllegalArg instead Unsupported" ).
                bug( "testCloseDirStreamInTheMiddleOfIteration" ).
                bug("testClosedFSGetFileStore").
                bug( "testCopySymLinkToItself").
                bug( "testCopyBrokenSymLinkToItself").
                bug( "testMoveARelSymLink").
                bug( "testMoveARelSymLink2").
//                bug( "testIsSameFileWithSpecialUnnormalizedPath" ).
                bug( "testSymLinkToUnnormalizedRelPath" ).
                bug( "testGetFileStoreOfNonExistent" ).
                bug( "testGetFileStoreOfBrokenSymLink" ).
                bug( "testIsSameFileOfDifferentPathNonExistingFileIsNot" ).
                bug( "testWatchAModify" ).
                nitpick( "testReadChannelOfDir", "who cares" ).
                nitpick( "testRegisterWatchServiceOfClosedFS", "different exception" ).
                nitpick("testAppendAndReadThrows", "IllegalArg instead Unsupported").
                done();
                
//                new FSDescription().
//                closable().yes().
//                hardLinks().toDirs(false).yes().
//                unix(true).
//                watchService().delay(5500).yes().
//                symLinks().toOtherProviders(false).yes().
    }

    @AfterClass
    public static void after() {
        descr.printUnused();
    }

    public JimTest() {
        super( descr );
    }

//    @Test( expected = FileSystemAlreadyExistsException.class )
//    public void testNewFileSystemOfExistingThrows() throws IOException {
//        Map<String,Object> env = new HashMap<>();
//        env.put(
//
//                FS.provider().newFileSystem( toURI( FS ), env );
//    }




}
