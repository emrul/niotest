package org.opencage.lindwurm.niotest.tests;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * Created by stephan on 04/06/14.
 */
public abstract class PathTest14Principals extends PathTest13FileStoreIT {

    @Test
    public void testHasUserPrincipalLookupService() {
        assumeThat( capabilities.supportsPrincipals(), is(true));

        assertThat(FS.getUserPrincipalLookupService(), notNullValue());
    }


    @Test
    public void testDefaultOwnerisFoundInLookpupService() throws IOException {
        assumeThat( capabilities.supportsPrincipals(), is(true));
        // expect no throw
        //System.out.println(Files.readAttributes(getDefaultPath(), PosixFileAttributes.class).owner());

        UserPrincipal owner = Files.readAttributes(getDefaultPath(), PosixFileAttributes.class).owner();

        assertThat( owner, is(FS.getUserPrincipalLookupService().lookupPrincipalByName(owner.getName())));
    }

    @Test
    public void testOwnerByTwoMethods() throws IOException {
        assumeThat( capabilities.supportsPrincipals(), is(true));

        assertThat( Files.getOwner( getDefaultPath()),
                    is(Files.readAttributes(getDefaultPath(), PosixFileAttributes.class).owner()));
    }

}
