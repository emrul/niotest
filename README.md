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
        <version>0.16.1</version>
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

            capabilities.notClosable();
            capabilities.doesNotSupportCreationTime();

            bug( "testCreateDirectoryRoot", "bugCreateDirectoryRootThrowsWrongException" );

            // todo: is osx only ?
            bug( "testCreateDirSetsLastAccessTimeOfParent", "bugCreateDirDoesNotSetLastAccessTimeOfParent" );
            bug( "testCreateFileSetsLastAccessTimeOfParent", "bugCreateFileDoesNotSetLastAccessTimeOfParent" );
            bug( "testOverwriteSetLastAccessTime", "bugOverwriteDoesNotSetLastAccessTime" );

            bug( "testCreateDirectoryUnnormalizedPath", "bugCreateDirectoryUnnormalizedPath" );
            bug( "testGetFileStoreUnnormalizedPath",    "bugGetFileStoreUnnormalizedPath" );
            bug( "testNewByteChannelUnnormalizedPath",  "bugNewByteChannelUnnormalizedPath" );
            bug( "testNewDirectoryStreamUnnormalizedPath", "bugNewDirectoryStreamUnnormalizedPath"  );

        }
    }


