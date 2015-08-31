niotest  [![Build Status](https://travis-ci.org/openCage/niotest.svg?branch=master)](https://travis-ci.org/openCage/niotest) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.pfabulist.lindwurm/niotest/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.pfabulist.lindwurm/niotest)

=======

Niotest is a framework for testing java 7 nio2 based virtual filesystem as specified in [JSR-203](https://jcp.org/en/jsr/detail?id=203). It should eventually cover the full api with all special cases. To get any reasonable results a readwrite implementation with a decent coverage of the API is needed.

### Setup

#### Maven:

    <dependency>
        <groupId>de.pfabulist.lindwurm</groupId>
        <artifactId>niotest</artifactId>
        <version>0.29</version>
    </dependency>

### Design

Describe a filesystem once to let niotest run all the tests. Every test can be run independent and in parallel.


### Use

See examples in test. eg. DefTest or JimFsTest

### Details (documentation for upcoming 1.0 release)

#### Getting Started

To use niotest create class extending Alltests and describe your nio filesystem 

    public class MyFSTest extends AllTests {
        
        private static FSDescription descr;

        public MyFSTest() {
            super( descr );
        }

        @BeforeClass
        public static void before() {
            descr = new FSDescription();
        }
    }

If you run this niotest will complain that it needs a playground, i.e. a root folder in your Filesystem.
You either point it to a writable directory where niotest can create files and directories or you describe some 
existing readonly objects.


## Releases

### 0.31

* (minor) refactored default-filesystem check
* (minor) tagged some tests as slow
* osx: max path length is measured in UTF8 bytes but filename is measured in UTF16 codepoints
       i.e path length measure is >= 2* the size of filename
       but this is only for max testing

### 0.30

* max path tests
* permission tests expects root user 
* osx (hfs+) specific path limits
* case insensitive and case preserving tests 
* windows test are back
* remove filekey tests from windows
* unix / linux with path tests

### 0.29

* FileChannel Tests
  no lock tests
  no asynchronous tests

* FileStores with limited size tests 

* Files.isHidden tests

* DosAttributes tests
  hidden flag: reading and setting
  rest: readonly 
  
* Unix: PosixFileAttributes
  some
  some permission checks
  
* FileOwnerAttributeView
 
* Principals


  
 
 


