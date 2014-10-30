package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;
import static org.opencage.lindwurm.niotest.matcher.PathExists.exists;

/**
 * Created by spfab on 28.10.2014.
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




}
