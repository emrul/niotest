niotest  [![Build Status](https://travis-ci.org/openCage/niotest.svg?branch=master)](https://travis-ci.org/openCage/niotest)

=======

Niotest is a framework for testing java 7 nio2 based virtual filesystem as specified in [JSR-203](https://jcp.org/en/jsr/detail?id=203). It should eventually cover the full api with all special cases. To get any reasonable results a readwrite implementation with a decent coverage of the API is needed.

### Setup

#### Maven:

    <dependency>
        <groupId>de.pfabulist.lindwurm</groupId>
        <artifactId>niotest</artifactId>
        <version>0.28</version>
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
 
 


