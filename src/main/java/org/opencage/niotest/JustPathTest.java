package org.opencage.niotest;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.opencage.kleinod.emergent.Todo.todo;
import static org.opencage.niotest.matcher.Matches.matches;
import static org.opencage.niotest.matcher.PathAbsolute.absolute;
import static org.opencage.niotest.matcher.PathAbsolute.relative;
import static org.opencage.niotest.matcher.PathEndsWith.endsWith;
import static org.opencage.niotest.matcher.PathIsDirectory.isDirectory;
import static org.opencage.niotest.matcher.PathStartsWith.startsWith;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2013, Stephan Pfab
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
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
public class JustPathTest extends FirstTest {

    @Test
    public void testGetNameSimple() {
        Path path = getPathAB();

        assertEquals( getPathA(), path.getName( 0 ));
        assertEquals( getPathB(), path.getName( 1 ));
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRootHasNoName() {
        getRoot().getName( 0 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetNameWithNegativeIndex() {
        getPathAB().getName( -1 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetNameWithTooLargeIndex() {
        getPathAB().getName( 5 );
    }

    @Test
    public void testGetNameOfDefaultPathIsItself() {
        assertEquals( getDefaultPath(), getDefaultPath().getName( 0 ) );
    }

    @Test
    public void testResultOfGetNameIsRelative() {
        assertThat( getPathAB().getName( 0 ), relative() );
    }

    @Test
    public void testGetNameCountSimple() {
        assertEquals( 2, getPathAB().getNameCount() );
    }

    @Test
    public void testRootNameCountIs0() {
        assertEquals( 0, getRoot().getNameCount() );
    }

    @Test
    public void testDefaultHasNameCount1() {
        assertEquals( 1, getDefaultPath().getNameCount() );
    }


    @Test
    public void testEndsWithSimple() {
        Path abc = getPathABC();
        Path bc = getPathBC();
        Path ab = getPathAB();

        assertThat( abc, endsWith( bc ));
        assertThat( abc, not( endsWith( ab )));
    }

    @Test
    public void testEndsWithStringSimple() {
        Path abc     = getPathABC();
        String bcStr = getPathBC().toString();
        String abStr = getPathAB().toString();

        assertThat( abc, endsWith( bcStr ));
        assertThat( abc, not( endsWith( abStr )));
    }

    @Test
    public void testStartsWithSimple() {
        Path abc = getPathABC();
        Path bc = getPathBC();
        Path ab = getPathAB();

        assertThat( abc, not(startsWith( bc )));
        assertThat( abc, startsWith( ab ));
    }

    @Test
    public void testAbsolutePathDoesNotStartsWithARelativePath() {
        Path abcAbso = getPathRABC();
        Path abRel   = getPathAB();

        assertThat( abcAbso, not( startsWith( abRel ) ));
    }

    @Test
    public void testAbsolutePathDoesStartsWithAnAbsolutePath() {
        Path abcAbso = getPathRABC();
        Path abAbso = getPathRAB();

        assertThat( abcAbso.startsWith( abAbso ), is(true) );
    }

    @Test
    public void testAbsolutePathDoesStartsWithRoot() {
        assertThat( getPathRABC(), startsWith( getRoot() ));
    }

    @Test
    public void testNoNonEmptyPathStartsWithDefault() {
        assertThat( getPathABC(), not( startsWith( getDefaultPath() ) ));
    }

    @Test
    public void testStartsWithStringSimple() {
        Path abc     = getPathABC();
        String bcStr = getPathBC().toString();
        String abStr = getPathAB().toString();

        assertThat( abc, not(startsWith( bcStr )));
        assertThat( abc.startsWith( abStr ), is( true ) );
    }

    @Test
    public void testSubPathSimple() throws Exception {
        assertEquals( getPathBC(), getPathABC().subpath( 1, 3 ) );
    }

    @Test
    public void testSubPathIsRelative() throws Exception {
        assertThat( getPathABC().subpath( 1,3 ), relative() );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testSubPathNegativeStart() throws Exception {
        getPathABC().subpath( -1, 2 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathStartTooLarge() throws Exception {
        getPathAB().subpath( 7, 9 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathEndBeforeStart() throws Exception {
        getPathABC().subpath( 1, 1 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathEndTooLarge() throws Exception {
        getPathABC().subpath( 1, 12 );
    }

    @Test
    public void testFileNameHasNameCountOf1() {
        assertThat( getPathRAB().getFileName().getNameCount(), is( 1 ) );
    }

    @Test
    public void testGetFileNameIsIdempotent() throws Exception {
        Path fileName = getPathAB().getFileName();
        assertEquals( fileName, fileName.getFileName() );
    }

    @Test
    public void testFileNameIsRelative() throws Exception {
        assertThat( getPathRAB().getFileName(), relative() );
    }

    @Test
    public void testRootHasNoFileName() throws Exception {
        assertThat( getRoot().getFileName(), nullValue() );
    }

    @Test
    public void testFileNameStringIsPathPartString() {
        assertEquals( p.getLegalPathElement(), getPathA().getFileName().toString() );
    }


    @Test
    public void testFileNameIsLastName() throws Exception {
        assertEquals( getPathABC().getName( 2 ), getPathABC().getFileName() );
    }


    @Test
    public void testDefaultHasANameAndItsItself() throws Exception {
        assertEquals( getDefaultPath(), getDefaultPath().getFileName() );
    }

    @Test
    public void testResolveOfNameStrEndsWithThatName() {
        Path rel = getDefaultPath();
        Path res  = rel.resolve( p.getNameStr( 0 ) );

        assertThat( res, endsWith( p.getNameStr( 0 ) ));
    }

    @Test
    public void testResolveOfARelativePathIsRelative() throws Exception {
        assertThat( getDefaultPath().resolve( p.getNameStr( 0 ) ), relative() );
    }


    @Test
    public void testResolveOfAbosluteIsAbsolute() throws Exception {
        assertThat( getPathRAB().resolve( p.getNameStr( 0 ) ), absolute() );
    }


    @Test
    public void testResolveWithDefaultIsNop() {
        assertEquals( getPathABC(), getPathABC().resolve( getDefaultPath() ) );
    }

    @Test
    public void testResolveWithNameIsSameAsStr() throws Exception {
        assertEquals( getPathAB().resolve( p.getNameStr( 3 ) ),
                      getPathAB().resolve( p.getName( 3 ) ));

    }

    @Test
    public void testResolveWorksLikeGetPathOnStringsWithSeparator() throws Exception {
        String str = p.getNameStr( 0 ) + getSeparator() + p.getNameStr( 1 );

        assertEquals( getDefaultPath().resolve( str ),
                      p.readOnlyFileSystem.getPath( str  ));

    }

    @Test
    public void testResolveWithStringWithSeparatorsSameAsWithPath() throws Exception {
        String str = p.getNameStr( 0 ) + getSeparator() + p.getNameStr( 1 );
        Path asPath = p.readOnlyFileSystem.getPath( p.getNameStr( 0 ) + getSeparator() + p.getNameStr( 1 ));

        assertEquals( getDefaultPath().resolve( str ),
                      getDefaultPath().resolve( asPath ));
    }

    @Test
    public void testResolveIterative() throws Exception {

        Path two = p.getName().resolve( p.getName( 1 ) );

        assertEquals( getPathA().resolve( two ),
                      getPathA().resolve( p.getName()).resolve( p.getName( 1 ) ) );
    }

    @Test
    public void testResolveWithAbsoluteArgReturnArg() throws Exception {
        assertEquals( getPathRAB(), getPathA().resolve( getPathRAB() ) );
    }

    @Test
    public void testResolveSiblingIsGetParentResolve() throws Exception {
        assertEquals( getPathAB().resolve( p.getName() ),
                      getPathABC().resolveSibling( p.getName()));
    }

    @Test
    public void testResolveSiblingWithAbsoluteArgIsThatArg() throws Exception {
        assertEquals( getPathRAB(), getPathA().resolveSibling( getPathRAB() ) );
    }

    @Test
    public void testResolveSiblingOnRootReturnsArg() throws Exception {
        assertEquals( getPathAB(), getRoot().resolveSibling( getPathAB() ) );
    }

    @Test
    public void testResolveSiblingOnDefaultReturnsArg() throws Exception {
        assertEquals( getPathAB(), getDefaultPath().resolveSibling( getPathAB() ) );
    }

    @Test
    public void testResolveSiblingOnNameReturnsArg() throws Exception {
        assertEquals( getPathAB(), getPathA().resolveSibling( getPathAB() ) );
    }

    @Test
    public void testResolveSiblingWorksWithStringAndPath() throws Exception {
        assertEquals( getPathAB().resolveSibling( p.getName(1) ),
                      getPathAB().resolveSibling( p.getNameStr( 1 )));

    }

    @Test
    public void testGetPathIgnoresEmptyStringAsFirstParameter() throws Exception {
        assertEquals( p.readOnlyFileSystem.getPath( p.getNameStr() ),
                      p.readOnlyFileSystem.getPath( "", p.getNameStr() ));

    }

    @Test
    public void testGetPathIgnoresEmptyStringInAnyParameter() throws Exception {
        assertEquals( p.readOnlyFileSystem.getPath( p.getNameStr() ),
                      p.readOnlyFileSystem.getPath( "", "", p.getNameStr(), "", "" ));

    }

    @Test
    public void testGetPathWithSeveralNamesIsSameAsWithOneStringWithSeparators() throws Exception {
        assertEquals( p.readOnlyFileSystem.getPath( p.getNameStr(0),                   p.getNameStr(1),                   p.getNameStr(2)),
                      p.readOnlyFileSystem.getPath( p.getNameStr(0) + getSeparator() + p.getNameStr(1) + getSeparator() + p.getNameStr(2)));
    }

    @Test
    public void testGetPathAllowsMixedArguments() throws Exception {
        assertEquals( p.readOnlyFileSystem.getPath( p.getNameStr(0) + getSeparator() + p.getNameStr(1),                   p.getNameStr(2)),
                      p.readOnlyFileSystem.getPath( p.getNameStr(0),                   p.getNameStr(1) + getSeparator() + p.getNameStr(2)));
    }

    @Test
    public void testGetPathAndToStringAreOposites() throws Exception {
        assertEquals( getPathABC(), p.readOnlyFileSystem.getPath( getPathABC().toString() ));

        String str = p.getNameStr(2) + getSeparator() + p.getNameStr(3);

        assertEquals( str, p.readOnlyFileSystem.getPath( str ).toString() );
    }

    @Test
    public void testGetPathOfSeparatorCreatesRoot() throws Exception {
        assumeTrue( p.getCapabilities().rootIsSeparator());

        Path proot = p.readOnlyFileSystem.getPath( getSeparator() );
        assertEquals( proot, proot.getRoot() );
    }

    @Test
    public void testGetPathNotStartingWithRootStringIsRelative() throws Exception {
        assertThat( p.readOnlyFileSystem.getPath( p.getNameStr( 2 ) ), relative() );
    }

    @Test
    public void testGetPathStartingWithRootStringIsAbsolute() throws Exception {
        assertThat( p.readOnlyFileSystem.getPath( getSeparator() + p.getName(2)), absolute() );
    }

    @Test
    public void testRelativize() {
        Path shrt = p.readOnlyFileSystem.getPath( p.getLegalPathElement() );
        Path lng = p.readOnlyFileSystem.getPath( p.getLegalPathElement(), p.getLegalPathElement(1), p.getLegalPathElement(2) );


        assertEquals( lng, shrt.resolve( shrt.relativize( lng ) ) );
    }

    @Test
    public void testRelativizeAbsolute() {
        Path root = getRoot();
        Path lng = p.readOnlyFileSystem.getPath( p.readOnlyFileSystem.getSeparator(), p.getLegalPathElement(), p.getLegalPathElement(1), p.getLegalPathElement(2) );
        Path lngRel = p.readOnlyFileSystem.getPath( p.getLegalPathElement(), p.getLegalPathElement(1), p.getLegalPathElement(2) );

        assertEquals( lngRel, root.relativize( lng ) );

    }


    @Test( expected = IllegalArgumentException.class )
    public void testRelativizeMixed() {
        Path shrt = p.readOnlyFileSystem.getPath( p.getLegalPathElement() );
        Path lng = p.readOnlyFileSystem.getPath( p.getLegalPathElement(), p.getLegalPathElement(1), p.getLegalPathElement(2) ).toAbsolutePath();


        shrt.relativize( lng );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRelativizeMixed2() {
        Path shrt = p.readOnlyFileSystem.getPath( p.getLegalPathElement() ).toAbsolutePath();
        Path lng = p.readOnlyFileSystem.getPath( p.getLegalPathElement(), p.getLegalPathElement(1), p.getLegalPathElement(2) );


        shrt.relativize( lng );
    }


    @Test
    public void testGetParent() {
        Path rel = p.readOnlyFileSystem.getPath( p.getLegalPathElement() );
        assertThat( rel.getParent(), IsNull.nullValue() );

        Path abs = p.readOnlyFileSystem.getPath( p.getLegalPathElement(), p.getLegalPathElement(2) ).toAbsolutePath();
        assertEquals( abs, abs.resolve( p.getLegalPathElement() ).getParent() );


    }

    @Test
    public void testGetParentOfRootIsNull() throws Exception {
        assertThat( getRoot().getParent(), IsNull.nullValue());
    }

    @Test
    public void testGetParentOfNameIsNull() throws Exception {
        assertThat( p.getName().getParent(), nullValue() );
    }

    @Test
    public void testGetParentOfDefaultIsNull() throws Exception {
        assertThat( getDefaultPath().getParent(), nullValue() );
    }

    @Test
    public void testGetParentOfLongerRelativeNameIsNotNull() throws Exception {
        assertThat( getPathAB().getParent(), notNullValue() );
    }

    @Test
    public void testGetParentIsInverseOfResolve() throws Exception {
        assertEquals( getPathA(), getPathA().resolve( p.getName(1) ).getParent() );
        assertEquals( getPathAB(), getPathAB().getParent().resolve( p.getName( 1 ) ) );
    }

    @Test
    public void testGetParentOfRelativeIsRelative() throws Exception {
        assertThat( getPathAB().getParent(), relative() );
    }

    @Test
    public void testgetParentOfAbsoluteIsAbsolute() throws Exception {
        assertThat( getPathRAB().getParent(), absolute() );
    }

    @Test
    public void testNormalizeWildPaths() {
        Path abs = p.readOnlyFileSystem.getPath( p.getLegalPathElement() ).toAbsolutePath();

        assertEquals( abs.getParent(), abs.resolve( "../aa/../." ).normalize() );

        Path rel = p.readOnlyFileSystem.getPath( p.getLegalPathElement() );

        assertEquals( "", rel.resolve( ".." ).normalize().toString() );
    }

    @Test
    public void testNormalize1Dot() {
        Path path1 = getPathAB();
        Path path2 = path1.resolve( "." );

        assertThat( path1, not( is (path2) ) ); // todo move to equal test

        assertThat( path1, is( path2.normalize()));
    }

    @Test
    public void testNormalize2Dots() {
        Path path1 = getPathAB();
        Path path2 = path1.resolve( p.getLegalPathElement(3) ).resolve( ".." );

        assertThat( path1, not( is (path2) ) ); // todo move to equal test

        assertThat( path1, is( path2.normalize()));
    }

    @Test
    public void testNormalizeIsIdempotent() throws Exception {
        Path path = getPathABC().resolve( ".." ).resolve( p.getLegalPathElement(2) );
        assertEquals( path.normalize(), path.normalize().normalize() );
    }

    @Test
    public void testPathIterator() {
        Path pathABC = getPathABC();

        int i = 0;
        for ( Path kid : pathABC ) {
            assertEquals( p.readOnlyFileSystem.getPath( p.getLegalPathElement( i ) ), kid );
            i++;
        }

        assertEquals( 3, i );
    }

    @Test
    public void testRootOfRelativeIsNull() {
        assertThat( getPathABC().getRoot(), nullValue() );
    }

    @Test
    public void testRootIsOneOfTheRoots() {
        Path root =  getRoot();

        boolean found = false;

        for ( Path aroot : p.readOnlyFileSystem.getRootDirectories() ) {
            assertThat( aroot.getNameCount(), is(0));

            found |= aroot.equals( root );
        }

        assertTrue( "root of default path is not one of the roots", found );
    }


    @Test
    public void testRootOfAbsolutePathIsAbsolute() throws Exception {
        assertThat( getPathRAB().getRoot(), absolute() );
    }

    @Test
    public void testGetRootIsIdempotent() throws Exception {
        assertEquals( getRoot(), getRoot().getRoot() );
    }

    @Test
    public void testGetRootIsaRootDirectory() throws Exception {

        Path root = getRoot();

        boolean found = false;

        for ( Path aroot : p.readOnlyFileSystem.getRootDirectories() ) {
            found |= aroot.equals( root );
        }

        assertTrue( "root of default path is not one of the roots", found );
    }


    @Test
    public void testToAbsoluteProducesAnAbsolutePath() throws Exception {
        assertThat( getPathABC().toAbsolutePath(), absolute() );
    }

    @Test
    public void testToAbsoluteIsIdempotent() throws Exception {
        assertThat( getPathRAB(), is( getPathRAB().toAbsolutePath() ));
    }

    @Test
    public void testDefaultIsRelative() throws Exception {
        assertThat( getDefaultPath(), relative() );
    }


    @Test
    public void testRelativePathToStringDoesNotStartWithSeparator() throws Exception {
        assertThat( getPathAB().toString().startsWith( getSeparator() ), is(false) );
    }

    @Test
    public void testPathWith2NamesHasSeparatorInToString() throws Exception {
        assertThat( getPathAB().toString().contains( getSeparator() ), is(true) );
    }

    @Test
    public void testPathsWithSamePathElementsButDifferentProviderAreDifferent() throws Exception {
        Path myABC = p.readOnlyFileSystem.getPath( "A", "B", "C" );
        Path otherABC = p.getOtherProviderFS().getPath( "A", "B", "C" );

        assertThat( myABC, not( is( otherABC )));
    }

    @Test
    public void testCapi() throws Exception {

        Path upper = p.readOnlyFileSystem.getPath( "A", "B" );
        Path lower = p.readOnlyFileSystem.getPath( "a", "b" );

        assertEquals( !p.getCapabilities().isCaseSensitive(),
                      upper.equals( lower ));
    }

    @Test
    public void testFileNameAlpha() throws Exception {
        assertThat( p.getCapabilities().isCorrectFileName( "azYw" ), is( true ) );
    }

    @Test
    public void testGeneratedPathNamesAreOK() throws Exception {
        for ( int i = 0; i < 10; i++ ) {
            assertThat( p.getCapabilities().isCorrectFileName( p.getNameStr( i ) ), is( true ) );
        }
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testToFileOnNonDefaultFSThrows() throws Exception {
        assumeTrue( !p.readOnlyFileSystem.equals( FileSystems.getDefault() ));

        getPathRAB().toFile();
    }

    @Test
    public void testName4() throws Exception {

        PathMatcher pm = p.readOnlyFileSystem.getPathMatcher( "regex:.*foo.*" );

        assumeTrue( pm.matches( p.readOnlyFileSystem.getPath( "/foo" ) ));
        assumeTrue( pm.matches( p.readOnlyFileSystem.getPath( "foo" ) ));
        assumeTrue( pm.matches( p.readOnlyFileSystem.getPath( "foo/da" ) ));
    }

    @Test
    public void testRegexPathMatcherWorkWithRegex() throws Exception {
        PathMatcher pm = p.readOnlyFileSystem.getPathMatcher( "regex:.*foo.*" );

        assumeThat( p.readOnlyFileSystem.getPath( "efaer/grgsfoo/da" ), matches( pm ) );
    }

    @Test( expected = ClassCastException.class )
    public void testCompareToDifferentProviderThrows() throws Exception {
        getPathABC().compareTo( p.getOtherProviderFS().getPath( p.getNameStr() ) );
    }

    @Test
    public void testCompareToOfEqualPathsIs0() throws Exception {
        assertEquals( 0, getPathABC().compareTo( getPathABC() ));
    }

    @Test
    public void testCompareToShortPathIsPositive() throws Exception {
        assertTrue( getPathABC().compareTo( getPathAB() ) > 0 );
    }

    @Test
    public void testCompareToLongetPathIsNegative() throws Exception {
        assertTrue( getPathAB().compareTo( getPathABC() ) < 0 );
    }

    @Test
    public void testPathIsImmutabeToAbsolute() throws Exception {
        Path rel = getPathAB();
        rel.toAbsolutePath();

        assertThat( rel, relative() );
    }

    @Test
    public void testPathIsImmutabeNormalize() throws Exception {
        Path unnom = getPathAB().resolve( ".." );
        assertTrue( unnom.toString().contains( ".." ) );
    }
}
