package de.pfabulist.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;
import static de.pfabulist.lindwurm.niotest.matcher.PathExists.exists;

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
public abstract class PathTest19HardLinksIT extends PathTest18FileChannelsIT {

    @Test
    public void testHardLinkCreate() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();

        Files.createLink( link, orig );

        assertThat( link, exists());
    }

    @Test
    public void testHardLinkChangeOneChangesTheOther() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();

        Files.createLink( link, orig );
        Files.write( link, CONTENT_OTHER );

        assertThat( Files.readAllBytes( orig), is(CONTENT_OTHER ));
    }

    @Test
    public void testHardLinkDeleteOrigDoesNotAffectTheOther() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();

        Files.createLink(link, orig);
        Files.delete( orig );

        assertThat(Files.readAllBytes(link), is(CONTENT));
    }

    @Test
    public void testHardLinkDeleteLinkDoesNotAffectTheOther() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();

        Files.createLink(link, orig);
        Files.delete( link );

        assertThat(Files.readAllBytes(orig), is(CONTENT));
    }

    @Test
    public void testHardLinkModifyOneModifiedDateOfOtherChanged() throws IOException, InterruptedException {
        Path orig = getPathPABf();
        Path link = getPathPAC();
        Files.createLink(link, orig);
        FileTime before = Files.getLastModifiedTime( orig );
        Thread.sleep(1000);

        Files.write(link, CONTENT_OTHER);

        assertThat( Files.getLastModifiedTime(orig), greaterThan( before));
    }

    @Test( expected = FileAlreadyExistsException.class )
    public void testHardLinkToExistingFileThrows() throws IOException, InterruptedException {
        Path orig = getPathPABf();
        Path link = getPathPACf();
        Files.createLink(link, orig);
    }

    @Test( expected = Exception.class )
    public void testHardLinkToOtherThrows() throws IOException {
        assumeThat( capabilities.has2ndFileSystem(), is(true));

        Path orig = getPathOtherPAf();
        Path link = getPathPAC();
        Files.createLink(link, orig);
    }

    @Test( expected = FileSystemException.class)
    public void testHardLinkToDirThrows() throws IOException {
        Path orig = getPathPAd();
        Path link = getPathPB();

        Files.createLink(link, orig);
    }


    @Test
    public void testHardLinkToHardLink() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();
        Path link2 = getPathPA().resolve(nameStr[0]);

        Files.createLink(link, orig);
        Files.createLink(link2, link);

        assertThat( link2, exists());
    }

    @Test
    public void test2ndHardLink() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();
        Path link2 = getPathPA().resolve(nameStr[0]);

        Files.createLink(link, orig);
        Files.createLink(link2, orig);

        assertThat( link2, exists());
    }

    @Test
    public void testIsSameFileWithHardLink() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();
        Files.createLink(link, orig);

        assertThat( Files.isSameFile( link, orig ), is(true));
    }

    @Test
    public void testHardLinkHasSameFileKey() throws IOException {
        Path orig = getPathPABf();
        Path link = getPathPAC();
        Files.createLink(link, orig);

        Object fk1 = Files.readAttributes( orig, BasicFileAttributes.class ).fileKey();
        Object fk2 = Files.readAttributes( link, BasicFileAttributes.class ).fileKey();

        assertThat( fk1, is(fk2));
    }

}
