package de.pfabulist.lindwurm.niotest.testsn;

import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;
import de.pfabulist.lindwurm.niotest.testsn.setup.Setup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.*;

import static de.pfabulist.lindwurm.niotest.matcher.ExceptionMatcher.throwsException;
import static de.pfabulist.lindwurm.niotest.matcher.IteratorMatcher.isIn;
import static de.pfabulist.lindwurm.niotest.matcher.PathAbsolute.absolute;
import static de.pfabulist.lindwurm.niotest.matcher.PathAbsolute.relative;
import static de.pfabulist.lindwurm.niotest.matcher.PathEndsWith.endsWith;
import static de.pfabulist.lindwurm.niotest.matcher.PathStartsWith.startsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2014, Stephan Pfab
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
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
public abstract class Tests01NoContent extends Setup {


    public Tests01NoContent( Capa capa ) {
        super( capa );
    }
    
    @Test
    @Category( FastTest.class )
    public void testGetNameSimple() {
        MatcherAssert.assertThat(relAB().getName(0), is(relA()));
        MatcherAssert.assertThat(relAB().getName(1), is(relB()));
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRootHasNoName() {
        defaultRoot().getName( 0 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetNameWithNegativeIndex() {
        relAB().getName(-1);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetNameWithTooLargeIndex() {
        relAB().getName(5);
    }

    @Test
    public void testGetNameOfDefaultPathIsItself() {
        assertThat(pathDefault().getName(0), is(pathDefault()));
    }

    @Test
    public void testResultOfGetNameIsRelative() {
        MatcherAssert.assertThat(relAB().getName(0), relative());
    }

    @Test
    public void testNameDoesNotIncludeSeparator() throws Exception {
        MatcherAssert.assertThat(relAB().getName(1).toString().contains(FS.getSeparator()), is(false));
    }

    @Test
    public void testNameCountOfNameIs1() throws Exception {
        MatcherAssert.assertThat(relABC().getName(2).getNameCount(), is(1));
    }

    @Test
    public void testGetNameCountSimple() {
        MatcherAssert.assertThat(relAB().getNameCount(), is(2));
    }

    @Test
    public void testGetNameIsIdempontent() {
        MatcherAssert.assertThat(relA().getName(0), is(relA()));
    }

    @Test
    public void testRootNameCountIs0() {
        MatcherAssert.assertThat(defaultRoot().getNameCount(), is(0));
    }

    @Test
    public void testDefaultHasNameCount1() {
        MatcherAssert.assertThat(pathDefault().getNameCount(), is(1));
    }


    @Test
    public void testEndsWithSimple() {
        MatcherAssert.assertThat(relABC(), endsWith(relBC()));
        MatcherAssert.assertThat(relABC(), not(endsWith(relAB())));
    }

    @Test
    public void testEndsWithStringSimple() {
        Path abc     = relABC();
        String bcStr = relBC().toString();
        String abStr = relAB().toString();

        MatcherAssert.assertThat(abc, endsWith(bcStr));
        MatcherAssert.assertThat(abc, not(endsWith(abStr)));
    }

    @Test
    public void testStartsWithSimple() {
        Path abc = relABC();
        Path bc = relBC();
        Path ab = relAB();

        MatcherAssert.assertThat(abc, not(startsWith(bc)));
        MatcherAssert.assertThat(abc, startsWith(ab));
    }

    @Test
    public void testAbsolutePathDoesNotStartsWithARelativePath() {
        Path abcAbso = absABC();
        Path abRel   = relAB();

        MatcherAssert.assertThat(abcAbso, not(startsWith(abRel)));
    }

    @Test
    public void testAbsolutePathDoesStartsWithAnAbsolutePath() {
        Path abcAbso = absABC();
                Path abAbso = absAB();

        MatcherAssert.assertThat(abcAbso.startsWith(abAbso), is(true));
    }

    @Test
    public void testAbsolutePathDoesStartsWithRoot() {
        MatcherAssert.assertThat(absABC(), startsWith(defaultRoot()));
    }

    @Test
    public void testNoNonEmptyPathStartsWithDefault() {
        MatcherAssert.assertThat(relABC(), not(startsWith(pathDefault())));
    }

    @Test
    public void testStartsWithStringSimple() {
        Path abc     = relABC();
        String bcStr = relBC().toString();
        String abStr = relAB().toString();

        MatcherAssert.assertThat(abc, not(startsWith(bcStr)));
        MatcherAssert.assertThat(abc.startsWith(abStr), is(true));
    }

    @Test
    public void testSubPathSimple() throws Exception {
        assertEquals( relBC(), relABC().subpath(1, 3) );
    }

    @Test
    public void testSubPathIsRelative() throws Exception {
        MatcherAssert.assertThat(relABC().subpath(1, 3), relative());
    }


    @Test( expected = IllegalArgumentException.class )
    public void testSubPathNegativeStart() throws Exception {
        relABC().subpath(-1, 2);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathStartTooLarge() throws Exception {
        relAB().subpath(7, 9);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathEndBeforeStart() throws Exception {
        relABC().subpath(1, 1);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathEndTooLarge() throws Exception {
        relABC().subpath(1, 12);
    }

    @Test
    public void testFileNameHasNameCountOf1() {
        MatcherAssert.assertThat(absAB().getFileName().getNameCount(), is(1));
    }

    @Test
    public void testGetFileNameIsIdempotent() throws Exception {
        Path fileName = relAB().getFileName();
        MatcherAssert.assertThat(fileName.getFileName(), is(fileName));
    }

    @Test
    public void testFileNameIsRelative() throws Exception {
        MatcherAssert.assertThat(absAB().getFileName(), relative());
    }

    @Test
    public void testRootHasNoFileName() throws Exception {
        MatcherAssert.assertThat(defaultRoot().getFileName(), nullValue());
    }

    @Test
    public void testFileNameStringIsPathPartString() {
        MatcherAssert.assertThat(relA().getFileName().toString(), is(nameA()));
    }


    @Test
    public void testFileNameIsLastName() throws Exception {
        MatcherAssert.assertThat(relABC().getFileName(), is(relABC().getName(2)));
    }


    @Test
    public void testDefaultHasANameAndItsItself() throws Exception {
        assertEquals( pathDefault(), pathDefault().getFileName() );
    }

    @Test
    public void testResolveOfNameStrEndsWithThatName() {
        Path rel = pathDefault();
        Path res  = rel.resolve( nameA() );

        MatcherAssert.assertThat(res, endsWith(nameA()));
    }

    @Test
    public void testResolveOfARelativePathIsRelative() throws Exception {
        MatcherAssert.assertThat(pathDefault().resolve(nameA()), relative());
    }


    @Test
    public void testResolveOfAbsoluteIsAbsolute() throws Exception {
        MatcherAssert.assertThat(absAB().resolve(nameA()), absolute());
    }


    @Test
    public void testResolveWithDefaultIsNop() {
        assertEquals( relABC(), relABC().resolve(pathDefault()) );
    }

    @Test
    public void testResolveWithNameIsSameAsStr() throws Exception {
        assertEquals( relAB().resolve(nameD()) ,
                relAB().resolve( relD()));

    }

    @Test
    public void testResolveWorksLikeGetPathOnStringsWithSeparator() throws Exception {
        String str = nameA() + FS.getSeparator() +nameB();

        assertEquals( pathDefault().resolve( str ),
                FS.getPath( str  ));

    }

    @Test
    public void testResolveWithStringWithSeparatorsSameAsWithPath() throws Exception {
        String str = nameA() + FS.getSeparator() +nameB();
        Path asPath = FS.getPath( nameA() + FS.getSeparator() +nameB());

        assertEquals( pathDefault().resolve( str ),
                pathDefault().resolve( asPath ));
    }

    @Test
    public void testResolveIterative() throws Exception {

        Path two = relA().resolve( relB() );

        assertEquals( relA().resolve(two),
                relA().resolve(relA().resolve(relB())));
    }

    @Test
    public void testResolveWithAbsoluteArgReturnArg() throws Exception {
        assertEquals( absAB(), relA().resolve(absAB()) );
    }

    @Test
    public void testResolveSiblingIsGetParentResolve() throws Exception {
        assertEquals( relAB().resolve(relA()),
                relABC().resolveSibling(relA()));
    }

    @Test
    public void testResolveSiblingWithAbsoluteArgIsThatArg() throws Exception {
        assertEquals( absAB(), relA().resolveSibling(absAB()) );
    }

    @Test
    public void testResolveSiblingOnRootReturnsArg() throws Exception {
        assertEquals( relAB(), defaultRoot().resolveSibling( relAB() ) );
    }

    @Test
    public void testResolveSiblingOnDefaultReturnsArg() throws Exception {
        assertEquals( relAB(), pathDefault().resolveSibling( relAB() ) );
    }

    @Test
    public void testResolveSiblingOnNameReturnsArg() throws Exception {
        assertEquals( relAB(), relA().resolveSibling(relAB()) );
    }

    @Test
    public void testResolveSiblingWorksWithStringAndPath() throws Exception {
        assertEquals( relAB().resolveSibling(relB()),
                relAB().resolveSibling(nameB()));

    }

    @Test
    public void testGetPathIgnoresEmptyStringAsFirstParameter() throws Exception {
        assertEquals( FS.getPath( nameA() ),
                FS.getPath( "", nameA() ));

    }

    @Test
    public void testGetPathIgnoresEmptyStringInAnyParameter() throws Exception {
        assertEquals( FS.getPath( nameA() ),
                FS.getPath( "", "", nameA(), "", "" ));

    }

    @Test
    public void testGetPathWithSeveralNamesIsSameAsWithOneStringWithSeparators() throws Exception {
        assertEquals( FS.getPath( nameA(),                  nameB(),                   nameC()),
                FS.getPath( nameA() + FS.getSeparator() +nameB() + FS.getSeparator() + nameC()));
    }

    @Test
    public void testpathAllowsMixedArguments() throws Exception {
        assertEquals( FS.getPath( nameA() + FS.getSeparator() +nameB(),                   nameC()),
                FS.getPath( nameA(),                  nameB() + FS.getSeparator() + nameC()));
    }

    @Test
    public void testpathAndToStringAreOpposites() throws Exception {

        MatcherAssert.assertThat(FS.getPath(relABC().toString()), is(relABC()));
//        assertEquals( relABC(), FS.getPath( relABC().toString() ));

        String str = nameC() + FS.getSeparator() + nameD();

        MatcherAssert.assertThat(FS.getPath(str).toString(), is(str));
    }

    // todo default fs bugs, /
//    @Test
//    public void testGetPathOfSeparatorCreatesRoot() throws Exception {
//        Path proot = FS.getPath( FS.getSeparator() + "dud");
//        System.out.println(proot);
//        System.out.println(isRoot(proot) + " " + proot.isAbsolute());
//        assertThat( isRoot( proot), is( true ));
//    }

    @Test
    public void testGetPathNotStartingWithRootStringIsRelative() throws Exception {
        MatcherAssert.assertThat(FS.getPath(nameC()), relative());
    }

    // only for unix systems
    // otherwise unclear what root looks like
//    @Test
//    public void testGetPathStartingWithRootStringIsAbsolute() throws Exception {
//        assumeThat( capabilities.);
//        assertThat( FS.getPath( FS.getSeparator() + getName(2)), absolute() );
//    }

    @Test
    public void testRelativize() {
        Path shrt = FS.getPath( nameA() );
        Path lng  = FS.getPath( nameA(),nameB(), nameC() );

        assertEquals( lng, shrt.resolve( shrt.relativize( lng ) ) );
    }

    @Test
    public void testRelativizeAbsolute() {
        Path root   = defaultRoot();
        Path lng    = root.resolve( nameA() ).resolve(nameB() ).resolve( nameC() );
        Path lngRel = FS.getPath( nameA(),nameB(), nameC() );

        assertEquals( lngRel, root.relativize( lng ) );
    }

    @Test
    public void testRelativizeFromDefaultAbsoluteIsInverseOfToAbsoluteNormalize() {

        Path abs    = absABC();
        Path defAbs = pathDefault().toAbsolutePath();

        assertThat( defAbs.relativize(abs).toAbsolutePath().normalize(), is(abs));
    }


    @Test
    public void testUnnormalizedBasedOnFile() {
        assertThat( absABC().resolve("..").resolve( nameC()).normalize(), is(absABC()));
    }


    @Test( expected = IllegalArgumentException.class )
    public void testRelativizeAbsToRel() {
        Path shrt = FS.getPath( nameA() );
        Path lng  = FS.getPath( nameA(),nameB(), nameC() ).toAbsolutePath();

        shrt.relativize( lng );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRelativizeRelToAbs() {
        Path shrt = FS.getPath( nameA() ).toAbsolutePath();
        Path lng = FS.getPath( nameA(),nameB(), nameC() );

        shrt.relativize( lng );
    }

    @Test
    public void testRelativizePathWithOtherRootFails() {
        for ( Path root : FS.getRootDirectories() ) {
            if ( !root.equals( defaultRoot() )) {
                MatcherAssert.assertThat(() -> {
                    root.relativize(defaultRoot());
                }, throwsException(IllegalArgumentException.class));
            }
        }
    }


    @Test
    public void testGetParent() {
        Path rel = FS.getPath( nameA() );
        MatcherAssert.assertThat(rel.getParent(), nullValue());

        Path abs = FS.getPath( nameA(), nameC() ).toAbsolutePath();
        assertEquals( abs, abs.resolve( nameA() ).getParent() );
    }

    @Test
    public void testGetParentOfRootIsNull() throws Exception {
        MatcherAssert.assertThat(defaultRoot().getParent(), IsNull.nullValue());
    }

    @Test
    public void testGetParentOfNameIsNull() throws Exception {
        MatcherAssert.assertThat(relA().getParent(), nullValue());
    }

    @Test
    public void testGetParentOfDefaultIsNull() throws Exception {
        MatcherAssert.assertThat(pathDefault().getParent(), nullValue());
    }

    @Test
    public void testGetParentOfLongerRelativeNameIsNotNull() throws Exception {
        MatcherAssert.assertThat(relAB().getParent(), notNullValue());
    }

    @Test
    public void testGetParentIsInverseOfResolve() throws Exception {
        assertEquals( relA(), relA().resolve(relB()).getParent() );
        assertEquals( relAB(), relAB().getParent().resolve( relB() ) );
    }

    @Test
    public void testGetParentOfRelativeIsRelative() throws Exception {
        MatcherAssert.assertThat(relAB().getParent(), relative());
    }

    @Test
    public void testgetParentOfAbsoluteIsAbsolute() throws Exception {
        assertThat( absAB().getParent(), absolute());
    }

    @Test
    public void testNormalizeWildAbsPaths() {
        Path abs = absAB();

        assertThat( abs.resolve(".." + FS.getSeparator() + "bb" + FS.getSeparator() + ".." + FS.getSeparator() + ".").normalize(),
                    is( abs.getParent()) );

        assertEquals("", relA().resolve("..").normalize().toString());
    }

    @Test
    public void testNormalizeWildRelPaths() {
        assertEquals( "", relA().resolve( ".." ).normalize().toString() );
    }

    @Test
    public void testNormalize1Dot() {
        Path path1 = relAB();
        Path path2 = path1.resolve( "." );

        assertThat(path1, not(is(path2)));

        assertThat(path1, is(path2.normalize()));
    }

    @Test
    public void testNormalize2Dots() {
        Path path1 = relAB();
        Path path2 = path1.resolve( nameD() ).resolve( ".." );

        assertThat(path1, not(is(path2)));

        assertThat(path1, is(path2.normalize()));
    }

    @Test
    public void testNormalizeIsIdempotent() throws Exception {
        Path path = relABC().resolve("..").resolve( nameC() );
        assertThat(path.normalize().normalize(), is(path.normalize()));
    }

    @Test
    public void testNormlizeParentOfRoot() {
        assertThat( defaultRoot().resolve("..").normalize(), is( defaultRoot()));
    }

    @Test
    public void testNormalizeRelativePath() {
        Path rel = FS.getPath( "..").resolve( nameA() );
        assertThat( rel.normalize(), is(rel));
    }

    @Test
    public void testNormalizeRelativePathEmptyHm() {
        Path rel = FS.getPath( nameA()).resolve( ".." );
        assertThat( rel.normalize(), is(pathDefault()));
    }



    @Test
    public void testPathIterator() {

        int i = 0;
        for ( Path kid : relABC() ) {
            switch ( i ) {
                case 0: MatcherAssert.assertThat(kid, is(relA())); break;
                case 1: MatcherAssert.assertThat(kid, is(relB())); break;
                case 2: MatcherAssert.assertThat(kid, is(relC())); break;
            }
            
            i++;
        }

        assertEquals( 3, i );
    }

    @Test
    public void testRootOfRelativeIsNull() {
        MatcherAssert.assertThat(relABC().getRoot(), nullValue());
    }

    @Test
    public void testRootIsOneOfTheRoots() {
        MatcherAssert.assertThat(defaultRoot(), isIn(FS.getRootDirectories()));
    }
    

    @Test
    public void testRootOfAbsolutePathIsAbsolute() throws Exception {
        MatcherAssert.assertThat(absAB().getRoot(), absolute());
    }

    @Test
    public void testdefaultRootIsIdempotent() throws Exception {
        assertEquals( defaultRoot(), defaultRoot().getRoot() );
    }

    
    @Test
    public void testToAbsoluteProducesAnAbsolutePath() throws Exception {
        MatcherAssert.assertThat(relABC().toAbsolutePath(), absolute());
    }

    @Test
    public void testToAbsoluteIsIdempotent() throws Exception {
        MatcherAssert.assertThat(absAB(), is(absAB().toAbsolutePath()));
    }

    @Test
    public void testDefaultIsRelative() throws Exception {
        MatcherAssert.assertThat(pathDefault(), relative());
    }


    @Test
    public void testRelativePathToStringDoesNotStartWithSeparator() throws Exception {
        MatcherAssert.assertThat(relAB().toString().startsWith(FS.getSeparator()), is(false));
    }

    @Test
    public void testPathWith2NamesHasSeparatorInToString() throws Exception {
        MatcherAssert.assertThat(relAB().toString().contains(FS.getSeparator()), is(true));
    }

    @Test
    public void testPathsWithSamePathElementsButDifferentProviderAreDifferent() throws Exception {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        Path myABC = FS.getPath( nameA(),nameB(), nameC() );
        Path otherABC = FileSystems.getDefault().getPath( nameA(),nameB(), nameC() );

        MatcherAssert.assertThat(myABC, not(is(otherABC)));
    }
//
//    @Test
//    public void testCapi() throws Exception {
//
//        Path upper = FS.getPath( "A", "B" );
//        Path lower = FS.getPath( "a", "b" );
//
//        assertEquals( !p.getCapabilities().isCaseSensitive(),
//                upper.equals( lower ));
//    }
//
//    @Test
//    public void testFileNameAlpha() throws Exception {
//        assertThat( p.getCapabilities().isCorrectFileName( "azYw" ), is( true ) );
//    }
//
//    @Test
//    public void testGeneratedPathNamesAreOK() throws Exception {
//        for ( int i = 0; i < 10; i++ ) {
//            assertThat( p.getCapabilities().isCorrectFileName( nameStr[i] ), is( true ) );
//        }
//    }

    @Test( expected = UnsupportedOperationException.class )
    public void testToFileOnNonDefaultFSThrows() throws Exception {
        assumeTrue( "default FileSystem does not throw", !FS.equals( FileSystems.getDefault() ));

        absAB().toFile();
    }

    @Test
    public void testPathMatcherKnowsGlob() {
        FS.getPathMatcher( "glob:*" );
    }

    @Test
    public void testPathMatcherKnowsRegex() {
        FS.getPathMatcher( "regex:.*" );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testPathMatcherThrowsOnUnknownSyntax() {
        FS.getPathMatcher( "thisisarellysillysyntax:.*" );
    }

    @Test
    public void testPathMatherRegex() throws Exception {

        PathMatcher pm = FS.getPathMatcher( "regex:.*" + nameC() + ".*" );

        MatcherAssert.assertThat(pm.matches(FS.getPath(nameC()).toAbsolutePath()), is(true));
        MatcherAssert.assertThat(pm.matches(FS.getPath(nameC())), is(true));
        MatcherAssert.assertThat(pm.matches(FS.getPath(nameC(), "da")), is(true));
        MatcherAssert.assertThat(pm.matches(FS.getPath("du", nameC(), "da")), is(true));
        MatcherAssert.assertThat(pm.matches(FS.getPath("du", nameC() + nameA(), "da")), is(true));
    }

    @Test
    public void testPathMatherGlob() throws Exception {

        PathMatcher pm = FS.getPathMatcher( "glob:*.{" + nameC() + "," + nameD() + "}" );


        MatcherAssert.assertThat(pm.matches(FS.getPath( /*nameA(), */nameE() + "." + nameD())), is(true));
        MatcherAssert.assertThat(pm.matches(FS.getPath( /*nameA(), */nameE() + "." + nameC())), is(true));
        MatcherAssert.assertThat(pm.matches(FS.getPath( /*nameA(), */nameE() + nameC())), is(false));

    }

    @Test( expected = ClassCastException.class )
    public void testCompareToDifferentProviderThrows() throws Exception {
        assumeThat( FS, not(is( FileSystems.getDefault())));

        relABC().compareTo(FileSystems.getDefault().getPath(nameA()));
    }

    @Test
    public void testCompareToOfEqualPathsIs0() throws Exception {
        assertEquals( 0, relABC().compareTo(relABC()));
    }

    @Test
    public void testCompareToShortPathIsPositive() throws Exception {
        assertTrue( relABC().compareTo(relAB()) > 0 );
    }

    @Test
    public void testCompareToLongetPathIsNegative() throws Exception {
        assertTrue( relAB().compareTo(relABC()) < 0 );
    }

    @Test
    public void testPathIsImmutabeToAbsolute() throws Exception {
        Path rel = relAB();
        rel.toAbsolutePath();

        MatcherAssert.assertThat(rel, relative());
    }

    @Test
    public void testPathIsImmutableToNormalize() throws Exception {
        Path unnom = relAB().resolve("..");
        assertTrue( unnom.toString().contains( ".." ) );
    }

    // only tests class Files
    @Test
    public void testNonExistingAbsolutePathIsNotAFile() throws IOException {
        assertThat(Files.isRegularFile( relAB()), is(false));
    }

    // only tests class Files
    @Test
    public void testNonExistingRelativePathIsNotAFile() throws IOException {
        assertThat( Files.isRegularFile( relA()), is(false));
    }

    // todo
//    @Test
//    public void testIllegalCharsInPathThrows() {
//
//        for ( Character ill : capabilities.getPathIllegalCharacters() ) {
//            try {
//                FS.getPath("" + ill);
//                assertThat("illegal character allowed: <" + ill + ">", is(""));
//            } catch ( InvalidPathException e ) {
//            }
//        }
//    }

    @Test
    public void testSeparatorIsNoFileName() {
        Path path = FS.getPath( nameD() + FS.getSeparator() + nameA());

        for ( Path elem : path ) {
            assertThat(elem.toString(), not(containsString(FS.getSeparator())));
        }

    }

    @Test( expected = NullPointerException.class )
    public void testResolveNull() throws IOException {
        relAB().resolve((String) null);
    }

    @Test( expected = NullPointerException.class )
    public void testNullPath() {
        FS.getPath(null);
    }

    /*
     * ------------------------------------------------------------------------------------------------------
     */

    public Path pathDefault() {
        return FS.getPath("");
    }

    public Path defaultRoot() {
        return FS.getPath("").toAbsolutePath().getRoot();
    }

    public Path relA() {
        return FS.getPath(nameA());
    }

    public Path relB() {
        return FS.getPath(nameB());
    }
    public Path relC() {
        return FS.getPath(nameC());
    }
    public Path relD() {
        return FS.getPath(nameD());
    }

    public Path relAB() {
        return FS.getPath(nameA(), nameB());
    }

    public Path relBC() {
        return FS.getPath(nameB(), nameC());
    }

    public Path relABC() {
        return FS.getPath( nameA(), nameB(), nameC() );
    }

    public String nameA() {
        return name[0];
    }

    public String nameB() {
        return name[1];
    }

    public String nameC() {
        return name[2];
    }

    public String nameD() {
        return name[3];
    }
    public String nameE() {
        return name[4];
    }

    public Path absAB() {
        return defaultRoot().resolve(nameA()).resolve(nameB());
    }

    public Path absABC() {
        return defaultRoot().resolve(nameA()).resolve(nameB()).resolve(nameC());
    }

    public Path absD() {
        return defaultRoot().resolve(nameD());
    }



}
