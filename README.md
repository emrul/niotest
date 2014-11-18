niotest  [![Build Status](https://travis-ci.org/openCage/niotest.svg?branch=master)](https://travis-ci.org/openCage/niotest) (unit test)[![Coverage Status](https://coveralls.io/repos/openCage/niotest/badge.png)](https://coveralls.io/r/openCage/niotest)
                                                                                                                                       1
=======

Niotest is a framework for testing java 7 nio2 based virtual filesystem as specified in [JSR-203](https://jcp.org/en/jsr/detail?id=203). It should eventually cover the full api with all special cases. To get any reasonable results a readwrite implementation with a decent coverage of the API is needed.

### Setup

#### Maven:

    <dependency>
        <groupId>de.pfabulist.lindwurm</groupId>
        <artifactId>niotest</artifactId>
        <version>0.24.0.59</version>
    </dependency>

#### Use

Extend PathTestIT and show it a playground in your new FilesSystem.
Examples are in source under test, e.g. for the DefaultFilesystem


    public class DefaultFileSystemTest extends PathTestIT {

        private static Path playground;

        @BeforeClass
        public static void setUp() {
            // create filesystems just once, not for every test
            playground = PathUtils.getTmpDir( "DefaultFileSystemTest" );
        }

        public DefaultFileSystemTest() {

            describe().
                    playground(playground).
                    fileStores(true).
                    notClosable().
                    lastAccessTime( false ). // OSX only ?
                    creationTime( false ).   // OSX only ?
                    watcherSleepTime( 12000 ).
                    noSecondPlayground().

                    bug( "testCreateDirectoryRoot" ).
                    bug( "testGetIteratorOfClosedDirStream" ).
                    bug( "testWatchAModify").
                    bug( "testWatchATruncate").
                    bug( "testWatchSeveralEvents").
                    bug( "testWatchTwoModifiesOneKey");
        }

    }

#### Howto

Start with:


    public class MyFSTestIT extends PathTestIT {

        private static Path playground;

        @BeforeClass
        public static void setUp() {
            // create filesystems just once, not for every test
            playground = <create filesystem>
        }

        public DefaultFileSystemTest() {

            describe().
                    playground(playground).
                    noClosable().
                    noSecondPlayground();
        }
    }
        
If many tests fail it usually means that some optional areas are not supported. Turn off the whole block.

            describe().
                    playground(playground).
                    noClosable().
                    noSecondPlayground().
                    doesNotSupportWatchService();



If only some tests fail it might make sense to defere their fixing for a later time.

            describe().
                    playground(playground).
                    noClosable().
                    noSecondPlayground().
                    doesNotSupportWatchService().
		            bug( "testReadAttributesViewFutureExistingFile" );

If many tests still fail it is probably the the basics are not complete yet. Niotest uses same basic calls to your new filesystem during setup. The stacktrace of many failed test will look the same in that case. You have to fix these calls first to get meaningfull results form the test suit. Try to get tests from the first 3 NioPathNTestIt to work.

If many WatchService tests fail it might be the default time of 1s to wait for events to move through the system is too short (e.g. OSX default implementation). Wait longer:

            describe().
                    playground(playground).
                    noClosable().
                    noSecondPlayground().
                	setWatcherSleep( 10000 ).
		            bug( "testReadAttributesViewFutureExistingFile" );
	   

## Q&A


#### What to do when a test fails?

* if it is from an unsupported capability (e.g. FileChannel): turn off that capability

    describe().... notClosable();

* if it is a bug (in your filesystem implementation): fix it

* it is a bug, but you don't want that silly edge case to ruin your maven test run: declare it.

    bug("testReadWrite");

* It's a bug in niotest: create an github issue or fix it

#### Why are there differences in the outcome of calls of valid jsr-203 filesystems ?

The JSR-203 API declares may topics as optional some as filesystem dependent. It is also vague at times. Most filesystem have bugs in edge cases which rarly matter (e.g. the default implementation). 


#### Why are there failed tests after a niotest version upgrade ?

A new niotest version usually brings more tests. New capabilities bring a list of new tests that are turned on by default.

#### Why niotest ?

I ended up writing several JSR-203 filesystems. A common test suit saves time and is great for learning about the scope of the API.

#### Can you run the tests independently ?

Yes. There are situations when one failed test spills over into other test. A few resources are shared, e.g. the filesystem.

#### Can you use niotest to understand how to implement a filesystem ?

It can help but it is an integration test suit. It tells about the what not the how. Better look a some implementations e.g. [openCage/memoryfs](https://github.com/openCage/memoryfs), [marschall/memoryfilesystem](https://github.com/marschall/memoryfilesystem), [google/jimfs](https://github.com/google/jimfs), 
[openCage/setec](https://github.com/openCage/setec). The first project (mine) is mostly about learning how to create jsr-203 filesystems.

#### All test are passing. Am I done ?

No. There are topics not covered in niotest yet. There are also areas that are hard/ impossible to test without knowing the specialities of your implementation. You still neeed unit test for this. If all niotest tests pass you have a filesystem that covers basic use.

#### What filesystems can not be testes by niotest ?

* Readonly filessystems
* eventual consistency filesystems (e.g. an amazon s3 filesystem)

The setup will have to be redesigned to handle these types.	


