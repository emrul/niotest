niotest
=======

framework for testing java 7 nio2 based virtual filesystem

#### Maven:

    <repositories>
        <repository>
            <id>opencage-releases</id>
                <url>https://raw.github.com/openCage/mavenrepo/master/releases</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>


    <dependency>
        <groupId>org.opencage.lindwurm</groupId>
        <artifactId>niotest</artifactId>
        <version>0.15.1</version>
    </dependency>

#### Use

Extend PathTestIT and show it playgound in your new FilesSystem.
If not all tests pass because some features are not available yet they can be ignored by added the tests name to the notSupported map. For examples see the tests in test/java/org/opencage/lindwurm/niotest. The test for the default is below:


public class DefaultFileSystemTest extends PathTestIT {

    @BeforeClass
    public static void setUp() {
        setPlay( PathUtils.getTmpDir( "DefaultFileSystemTest" ));
    }

    public DefaultFileSystemTest() {

        capabilities.setClosable( false );

        notSupported.put( "testCreateDirectoryRoot", "throws different exception" );
        notSupported.put( "testCreateDirectoryRootThrowsWrongException", 
                          "throws different exception" );

        notSupported.put( "testDeleteFileDoesNotChangeParentCreationTime", 
                          "creation is lastmodifietime (spec allows is but underlying fs can do it)" );
        notSupported.put( "testDeleteDirNotChangeParentsCreationTime", 
                          "creation is lastmodifiedtime (spec allows is but underlying fs can do it)" );

        notSupported.put( "testSetCreationTimeViaString", "can't set creationTime" );
        notSupported.put( "testSetCreationTimeViaView", "can't set creationTime" );

        notSupported.put( "testCreateDirSetsLastAccessTimeOfParent", "access time is unchanged" );
        notSupported.put( "testCreateFileSetsLastAccessTimeOfParent", "access time is unchanged" );
        notSupported.put( "testOverwriteSetLastAccessTime", "access time is unchanged" );

    }
}


