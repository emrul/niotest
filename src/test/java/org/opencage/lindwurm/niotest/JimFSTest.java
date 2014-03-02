package org.opencage.lindwurm.niotest;

import com.google.jimfs.Configuration;
import com.google.jimfs.Jimfs;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.opencage.lindwurm.niotest.tests.PathTestIT;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: stephan
 * Date: 28/02/14
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
// todo: complete
@Ignore
public class JimFSTest extends PathTestIT {

    //Jimfs.newFileSystem(Configuration.unix())

    @BeforeClass
    public static void setUp() throws IOException {
        setPlay( Jimfs.newFileSystem(Configuration.unix()).getPath("/play"));

//                getOrCreate( "marschall" ).getPath( "play" ).toAbsolutePath() );
//        setClosablePlay( getOrCreate( "marschallClose" ).getPath( "play" ).toAbsolutePath() );
//        set2ndPlay(getOrCreate( "marschall22" ).getPath( "play" ).toAbsolutePath() );
    }

}
