package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.topics.Basic;
import de.pfabulist.lindwurm.niotest.tests.topics.NotDefaultFileSystem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import static de.pfabulist.kleinod.nio.PathIKWID.absoluteGetRoot;
import static de.pfabulist.kleinod.nio.PathIKWID.childGetParent;
import static de.pfabulist.kleinod.nio.PathIKWID.namedGetFilename;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2015, Stephan Pfab
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

@Category( Basic.class )
@SuppressWarnings( "PMD.ExcessivePublicCount" )
public abstract class Tests01NoContent extends Tests00Setup {

    public Tests01NoContent( FSDescription capa ) {
        super( capa );
    }

    @Test
    public void testGetNameSimple() {
        assertThat( relAB().getName( 0 ) ).isEqualTo( relA() );
        assertThat( relAB().getName( 1 ) ).isEqualTo( relB() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRootHasNoName() {
        defaultRoot().getName( 0 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetNameWithNegativeIndex() {
        relAB().getName( -1 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetNameWithTooLargeIndex() {
        relAB().getName( 5 );
    }

    @Test
    public void testGetNameOfDefaultPathIsItself() {
        assertThat( pathDefault().getName( 0 ) ).isEqualTo( pathDefault() );
    }

    @Test
    public void testResultOfGetNameIsRelative() {
        assertThat( relAB().getName( 0 ) ).isRelative();
    }

    @Test
    public void testNameDoesNotIncludeSeparator() throws Exception {
        assertThat( relAB().getName( 1 ).toString() ).doesNotContain( FS.getSeparator() );
    }

    @Test
    public void testNameCountOfNameIs1() throws Exception {
        assertThat( relABC().getName( 2 ).getNameCount() ).isEqualTo( 1 );
    }

    @Test
    public void testGetNameCountSimple() {
        assertThat( relAB().getNameCount() ).isEqualTo( 2 );
    }

    @Test
    public void testGetNameIsIdempontent() {
        assertThat( relA().getName( 0 ) ).isEqualTo( relA() );
    }

    @Test
    public void testRootNameCountIs0() {
        assertThat( defaultRoot().getNameCount() ).isEqualTo( 0 );
    }

    @Test
    public void testDefaultHasNameCount1() {
        assertThat( pathDefault().getNameCount() ).isEqualTo( 1 );
    }

    @Test
    public void testEndsWithSimple() {
        assertThat( relABC().endsWith( relBC() ) ).isTrue();
        assertThat( relABC().endsWith( relAB() ) ).isFalse();
    }

    @Test
    public void testEndsWithStringSimple() {
        Path abc = relABC();
        String bcStr = relBC().toString();
        String abStr = relAB().toString();

        assertThat( abc.endsWith( bcStr ) ).isTrue();
        assertThat( abc.endsWith( abStr ) ).isFalse();
    }

    @Test
    public void testStartsWithSimple() {
        Path abc = relABC();
        Path bc = relBC();
        Path ab = relAB();

        assertThat( abc.startsWith( bc ) ).isFalse();
        assertThat( abc.startsWith( ab ) ).isTrue();
    }

    @Test
    public void testAbsolutePathDoesNotStartsWithARelativePath() {
        Path abcAbso = absABC();
        Path abRel = relAB();

        assertThat( abcAbso.startsWith( abRel ) ).isFalse();
    }

    @Test
    public void testAbsolutePathDoesStartsWithAnAbsolutePath() {
        Path abcAbso = absABC();
        Path abAbso = absAB();

        assertThat( abcAbso.startsWith( abAbso ) ).isTrue();
    }

    @Test
    public void testAbsolutePathDoesStartsWithRoot() {
        assertThat( absABC().startsWith( defaultRoot() ) ).isTrue();
    }

    @Test
    public void testNoNonEmptyPathStartsWithDefault() {
        assertThat( relABC().startsWith( pathDefault() ) ).isFalse();
    }

    @Test
    public void testStartsWithStringSimple() {
        Path abc = relABC();
        String bcStr = relBC().toString();
        String abStr = relAB().toString();

        assertThat( abc.startsWith( bcStr ) ).isFalse();
        assertThat( abc.startsWith( abStr ) ).isTrue();
    }

    @Test
    public void testSubPathSimple() throws Exception {
        assertThat( relBC() ).isEqualTo( relABC().subpath( 1, 3 ) );
    }

    @Test
    public void testSubPathIsRelative() throws Exception {
        assertThat( relABC().subpath( 1, 3 ) ).isRelative();
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathNegativeStart() throws Exception {
        relABC().subpath( -1, 2 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathStartTooLarge() throws Exception {
        relAB().subpath( 7, 9 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathEndBeforeStart() throws Exception {
        relABC().subpath( 1, 1 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSubPathEndTooLarge() throws Exception {
        relABC().subpath( 1, 12 );
    }

    @Test
    public void testFileNameHasNameCountOf1() {
        assertThat( absAB().getFileName().getNameCount() ).isEqualTo( 1 );
    }

    @Test
    public void testGetFileNameIsIdempotent() throws Exception {
        Path fileName = namedGetFilename( relAB() );
        assertThat( fileName.getFileName() ).isEqualTo( fileName );
    }

    @Test
    public void testFileNameIsRelative() throws Exception {
        assertThat( absAB().getFileName() ).isRelative();
    }

    @Test
    public void testRootHasNoFileName() throws Exception {
        assertThat( defaultRoot().getFileName() ).isNull();
    }

    @Test
    public void testFileNameStringIsPathPartString() {
        assertThat( relA().getFileName().toString() ).isEqualTo( nameA() );
    }

    @Test
    public void testFileNameIsLastName() throws Exception {
        assertThat( relABC().getFileName() ).isEqualTo( relABC().getName( 2 ) );
    }

    @Test
    public void testDefaultHasANameAndItsItself() throws Exception {
        assertThat( pathDefault() ).isEqualTo( pathDefault().getFileName() );
    }

    @Test
    public void testResolveOfNameStrEndsWithThatName() {
        Path rel = pathDefault();
        Path res = rel.resolve( nameA() );

        assertThat( res.endsWith( nameA() ) ).isTrue();
    }

    @Test
    public void testResolveOfARelativePathIsRelative() throws Exception {
        assertThat( pathDefault().resolve( nameA() ) ).isRelative();
    }

    @Test
    public void testResolveOfAbsoluteIsAbsolute() throws Exception {
        assertThat( absAB().resolve( nameA() ) ).isAbsolute();
    }

    @Test
    public void testResolveWithDefaultIsNop() {
        assertThat( relABC() ).isEqualTo( relABC().resolve( pathDefault() ) );
    }

    @Test
    public void testResolveWithNameIsSameAsStr() throws Exception {
        assertThat( relAB().resolve( nameD() ) ).
                isEqualTo( relAB().resolve( relD() ) );

    }

    @Test
    public void testResolveWorksLikeGetPathOnStringsWithSeparator() throws Exception {
        String str = nameA() + FS.getSeparator() + nameB();

        assertThat( pathDefault().resolve( str ) ).isEqualTo( FS.getPath( str ) );

    }

    @Test
    public void testResolveWithStringWithSeparatorsSameAsWithPath() throws Exception {
        String str = nameA() + FS.getSeparator() + nameB();
        Path asPath = FS.getPath( nameA() + FS.getSeparator() + nameB() );

        assertThat( pathDefault().resolve( str ) ).isEqualTo(
                pathDefault().resolve( asPath ) );
    }

    @Test
    public void testResolveIterative() throws Exception {

        Path two = relA().resolve( relB() );

        assertThat( relA().resolve( two ) ).isEqualTo(
                relA().resolve( relA().resolve( relB() ) ) );
    }

    @Test
    public void testResolveWithAbsoluteArgReturnArg() throws Exception {
        assertThat( absAB() ).isEqualTo( relA().resolve( absAB() ) );
    }

    @Test
    public void testResolveSiblingIsGetParentResolve() {
        assertThat( relABC().resolveSibling( relA() ) ).
                isEqualTo( relAB().resolve( relA() ) );
    }

    @Test
    public void testResolveSiblingWithAbsoluteArgIsThatArg() throws Exception {
        assertThat( relA().resolveSibling( absAB() ) ).isEqualTo( absAB() );
    }

    @Test
    public void testResolveSiblingOnRootReturnsArg() throws Exception {
        assertThat( defaultRoot().resolveSibling( relAB() ) ).isEqualTo( relAB() );
    }

    @Test
    public void testResolveSiblingOnDefaultReturnsArg() throws Exception {
        assertThat( pathDefault().resolveSibling( relAB() ) ).isEqualTo( relAB() );
    }

    @Test
    public void testResolveSiblingOnNameReturnsArg() throws Exception {
        assertThat( relA().resolveSibling( relAB() ) ).isEqualTo( relAB() );
    }

    @Test
    public void testResolveSiblingWorksWithStringAndPath() throws Exception {
        assertThat( relAB().resolveSibling( relB() ) ).isEqualTo(
                relAB().resolveSibling( nameB() ) );
    }

    @Test
    public void testResolveSiblingOnRootWithEmptyIsEmpty() throws Exception {
        assertThat( defaultRoot().resolveSibling( pathDefault() ) ).
                isEqualTo( pathDefault() );
    }

    @Test
    public void testResolveSiblingOnEmptyWithEmptyIsEmpty() throws Exception {
        assertThat( pathDefault().resolveSibling( pathDefault() ) ).
                isEqualTo( pathDefault() );
    }

    @Test
    public void testResolveSiblingOnChildWithEmptyIsParent() throws Exception {
        assertThat( absABC().resolveSibling( pathDefault() ) ).
                isEqualTo( absAB() );
    }

    @Test
    public void testGetPathIgnoresEmptyStringAsFirstParameter() throws Exception {
        assertThat( FS.getPath( nameA() ) ).isEqualTo(
                FS.getPath( "", nameA() ) );

    }

    @Test
    public void testGetPathIgnoresEmptyStringInAnyParameter() throws Exception {
        assertThat( FS.getPath( nameA() ) ).isEqualTo(
                FS.getPath( "", "", nameA(), "", "" ) );

    }

    @Test
    public void testGetPathWithSeveralNamesIsSameAsWithOneStringWithSeparators() throws Exception {
        assertThat( FS.getPath( nameA(), nameB(), nameC() ) ).isEqualTo(
                FS.getPath( nameA() + FS.getSeparator() + nameB() + FS.getSeparator() + nameC() ) );
    }

    @Test
    public void testpathAllowsMixedArguments() throws Exception {
        assertThat( FS.getPath( nameA() + FS.getSeparator() + nameB(), nameC() ) ).isEqualTo(
                FS.getPath( nameA(), nameB() + FS.getSeparator() + nameC() ) );
    }

    @Test
    public void testpathAndToStringAreOpposites() throws Exception {

        assertThat( FS.getPath( relABC().toString() ) ).isEqualTo( relABC() );
//        assertThat( relABC(), FS.getPath( relABC().toString() ));

        String str = nameC() + FS.getSeparator() + nameD();

        assertThat( FS.getPath( str ).toString() ).isEqualTo( str );
    }

    // todo default fs bugs, /
//    @Test
//    public void testGetPathOfSeparatorCreatesRoot() throws Exception {
//        Path proot = FS.getPath( FS.getSeparator() + "dud");
//        System.out.println(proot);
//        System.out.println(isRoot(proot) + " " + proot.isAbsolute());
//        assertThat( isRoot( proot)).isEqualTo( true ));
//    }

    @Test
    public void testGetPathNotStartingWithRootStringIsRelative() throws Exception {
        assertThat( FS.getPath( nameC() ) ).isRelative();
    }

    // only for unix systems
    // otherwise unclear what root looks like
//    @Test
//    public void testGetPathStartingWithRootStringIsAbsolute() throws Exception {
//        assumeThat( capabilities.);
//        assertThat( FS.getPath( FS.getSeparator() + getName(2))).isAbsolute();
//    }

    @Test
    public void testRelativize() {
        Path shrt = FS.getPath( nameA() );
        Path lng = FS.getPath( nameA(), nameB(), nameC() );

        assertThat( lng ).isEqualTo( shrt.resolve( shrt.relativize( lng ) ) );
    }

    @Test
    public void testRelativizeAbsolute() {
        Path root = defaultRoot();
        Path lng = root.resolve( nameA() ).resolve( nameB() ).resolve( nameC() );
        Path lngRel = FS.getPath( nameA(), nameB(), nameC() );

        assertThat( lngRel ).isEqualTo( root.relativize( lng ) );
    }

    @Test
    public void testRelativizeFromDefaultAbsoluteIsInverseOfToAbsoluteNormalize() {

        Path abs = absABC();
        Path defAbs = pathDefault().toAbsolutePath();

        assertThat( defAbs.relativize( abs ).toAbsolutePath().normalize() ).isEqualTo( abs );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRelativizeAbsToRel() {
        Path shrt = FS.getPath( nameA() );
        Path lng = FS.getPath( nameA(), nameB(), nameC() ).toAbsolutePath();

        shrt.relativize( lng );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRelativizeRelToAbs() {
        Path shrt = FS.getPath( nameA() ).toAbsolutePath();
        Path lng = FS.getPath( nameA(), nameB(), nameC() );

        shrt.relativize( lng );
    }

    @Test
    public void testRelativizePathWithOtherRootFails() {
        for( Path root : FS.getRootDirectories() ) {
            if( !root.equals( defaultRoot() ) ) {
                assertThatThrownBy( () -> root.relativize( defaultRoot() ) ).
                        isInstanceOf( IllegalArgumentException.class );
            }
        }
    }

    @Test
    public void testGetParent() {
        Path rel = FS.getPath( nameA() );
        assertThat( rel.getParent() ).isNull();

        Path abs = FS.getPath( nameA(), nameC() ).toAbsolutePath();
        assertThat( abs ).isEqualTo( abs.resolve( nameA() ).getParent() );
    }

    @Test
    public void testGetParentOfRootIsNull() throws Exception {
        assertThat( defaultRoot().getParent() ).isNull();
    }

    @Test
    public void testGetParentOfNameIsNull() throws Exception {
        assertThat( relA().getParent() ).isNull();
    }

    @Test
    public void testGetParentOfDefaultIsNull() throws Exception {
        assertThat( pathDefault().getParent() ).isNull();
    }

    @Test
    public void testGetParentOfLongerRelativeNameIsNotNull() throws Exception {
        assertThat( relAB().getParent() ).isNotNull();
    }

    @Test
    public void testGetParentIsInverseOfResolve() throws Exception {
        assertThat( relA() ).isEqualTo( childGetParent( relA().resolve( relB() ) ) );
        assertThat( relAB() ).isEqualTo( childGetParent( relAB() ).resolve( relB() ) );
    }

    @Test
    public void testGetParentOfRelativeIsRelative() throws Exception {
        assertThat( childGetParent( relAB() ) ).isRelative();
    }

    @Test
    public void testgetParentOfAbsoluteIsAbsolute() throws Exception {
        assertThat( childGetParent( absAB() ) ).isAbsolute();
    }

    @Test
    public void testNormalizeWildAbsPaths() {
        Path abs = absAB();

        assertThat( abs.resolve( ".." + FS.getSeparator() + "bb" + FS.getSeparator() + ".." + FS.getSeparator() + "." ).normalize() ).
                isEqualTo( childGetParent( abs ) );

        assertThat( relA().resolve( ".." ).normalize().toString() ).isEqualTo( "" );
    }

    @Test
    public void testNormalizeWildRelPaths() {
        assertThat( relA().resolve( ".." ).normalize().toString() ).isEqualTo( "" );
    }

    @Test
    public void testNormalize1Dot() {
        Path path1 = relAB();
        Path path2 = path1.resolve( "." );

        assertThat( path1 ).isNotEqualTo( path2 );
        assertThat( path1 ).isEqualTo( path2.normalize() );
    }

    @Test
    public void testNormalize2Dots() {
        Path path1 = relAB();
        Path path2 = path1.resolve( nameD() ).resolve( ".." );

        assertThat( path1 ).isNotEqualTo( path2 );

        assertThat( path1 ).isEqualTo( path2.normalize() );
    }

    @Test
    public void testNormalizeIsIdempotent() throws Exception {
        Path path = relABC().resolve( ".." ).resolve( nameC() );
        assertThat( path.normalize().normalize() ).isEqualTo( path.normalize() );
    }

    @Test
    public void testNormlizeParentOfRoot() {
        assertThat( defaultRoot().resolve( ".." ).normalize() ).isEqualTo( defaultRoot() );
    }

    @Test
    public void testNormalizeRelativePath() {
        Path rel = FS.getPath( ".." ).resolve( nameA() );
        assertThat( rel.normalize() ).isEqualTo( rel );
    }

    @Test
    public void testNormalizeRelativePathEmptyHm() {
        Path rel = FS.getPath( nameA() ).resolve( ".." );
        assertThat( rel.normalize() ).isEqualTo( pathDefault() );
    }

    @Test
    public void testPathIterator() {

        int i = 0;
        for( Path kid : relABC() ) {
            switch( i ) {
                case 0:
                    assertThat( kid ).isEqualTo( relA() );
                    break;
                case 1:
                    assertThat( kid ).isEqualTo( relB() );
                    break;
                case 2:
                    assertThat( kid ).isEqualTo( relC() );
                    break;
                default:
                    // nop
            }

            i++;
        }

        assertThat( i ).isEqualTo( 3 );
    }

    @Test
    public void testRootOfRelativeIsNull() {
        assertThat( relABC().getRoot() ).isNull();
    }

    @Test
    public void testRootIsOneOfTheRoots() {
        assertThat( defaultRoot() ).isIn( FS.getRootDirectories() );
    }

    @Test
    public void testRootOfAbsolutePathIsAbsolute() throws Exception {
        assertThat( absAB().getRoot() ).isAbsolute();
    }

    @Test
    public void testdefaultRootIsIdempotent() throws Exception {
        assertThat( defaultRoot() ).isEqualTo( defaultRoot().getRoot() );
    }

    @Test
    public void testToAbsoluteProducesAnAbsolutePath() throws Exception {
        assertThat( relABC().toAbsolutePath() ).isAbsolute();
    }

    @Test
    public void testToAbsoluteIsIdempotent() throws Exception {
        assertThat( absAB() ).isEqualTo( absAB().toAbsolutePath() );
    }

    @Test
    public void testDefaultIsRelative() throws Exception {
        assertThat( pathDefault() ).isRelative();
    }

    @Test
    public void testRelativePathToStringDoesNotStartWithSeparator() throws Exception {
        assertThat( relAB().toString().startsWith( FS.getSeparator() ) ).isFalse();
    }

    @Test
    public void testPathWith2NamesHasSeparatorInToString() throws Exception {
        assertThat( relAB().toString().contains( FS.getSeparator() ) ).isTrue();
    }

    @Test
    @Category( NotDefaultFileSystem.class )
    public void testPathsWithSamePathElementsButDifferentProviderAreDifferent() throws Exception {
        Path myABC = FS.getPath( nameA(), nameB(), nameC() );
        Path otherABC = FileSystems.getDefault().getPath( nameA(), nameB(), nameC() );

        assertThat( myABC ).isNotEqualTo( otherABC );
    }

// todo
//
//    @Test
//    public void testCapi() throws Exception {
//
//        Path upper = FS.getPath( "A", "B" );
//        Path lower = FS.getPath( "a", "b" );
//
//        assertThat( !p.getCapabilities().isCaseSensitive(),
//                upper.equals( lower ));
//    }
//
//    @Test
//    public void testFileNameAlpha() throws Exception {
//        assertThat( p.getCapabilities().isCorrectFileName( "azYw" )).isEqualTo( true ) );
//    }
//
//    @Test
//    public void testGeneratedPathNamesAreOK() throws Exception {
//        for ( int i = 0; i < 10; i++ ) {
//            assertThat( p.getCapabilities().isCorrectFileName( nameStr[i] )).isEqualTo( true ) );
//        }
//    }

    @Test( expected = UnsupportedOperationException.class )
    @Category( NotDefaultFileSystem.class )
    public void testToFileOnNonDefaultFSThrows() throws Exception {
        absAB().toFile();
    }

    @Test
    public void testPathMatcherKnowsGlob() {
        assertThat( FS.getPathMatcher( "glob:*" ) ).isNotNull();
    }

    @Test
    public void testPathMatcherKnowsRegex() {
        assertThat( FS.getPathMatcher( "regex:.*" ) ).isNotNull();
    }

    @Test
    public void testPathMatcherThrowsOnUnknownSyntax() {
        assertThatThrownBy( () -> FS.getPathMatcher( "thisisarellysillysyntax:*" ) ).isInstanceOf( UnsupportedOperationException.class );
    }

    @Test
    public void testPathMatherRegex() throws Exception {

        PathMatcher pm = FS.getPathMatcher( "regex:.*" + nameC() + ".*" );

        assertThat( pm.matches( FS.getPath( nameC() ).toAbsolutePath() ) ).isTrue();
        assertThat( pm.matches( FS.getPath( nameC() ) ) ).isTrue();
        assertThat( pm.matches( FS.getPath( nameC(), "da" ) ) ).isTrue();
        assertThat( pm.matches( FS.getPath( "du", nameC(), "da" ) ) ).isTrue();
        assertThat( pm.matches( FS.getPath( "du", nameC() + nameA(), "da" ) ) ).isTrue();

    }

    @Test
    public void testPathMatherGlob() throws Exception {

        PathMatcher pm = FS.getPathMatcher( "glob:*.{" + nameC() + "," + nameD() + "}" );

        assertThat( pm.matches( FS.getPath( /*nameA(), */nameE() + "." + nameD() ) ) ).isTrue();
        assertThat( pm.matches( FS.getPath( /*nameA(), */nameE() + "." + nameC() ) ) ).isTrue();
        assertThat( pm.matches( FS.getPath( /*nameA(), */nameE() + nameC() ) ) ).isFalse();

    }

    @Test
    @Category( NotDefaultFileSystem.class )
    public void testCompareToDifferentProviderThrows() throws Exception {
        assertThatThrownBy( () -> relABC().compareTo( FileSystems.getDefault().getPath( nameA() ) ) ).isInstanceOf( ClassCastException.class );
    }

    @Test
    public void testCompareToOfEqualPathsIs0() throws Exception {
        assertThat( relABC().compareTo( relABC() ) ).isEqualTo( 0 );
    }

    @Test
    public void testCompareToShortPathIsPositive() throws Exception {
        assertThat( relABC().compareTo( relAB() ) > 0 ).isTrue();
    }

    @Test
    public void testCompareToLongerPathIsNegative() throws Exception {
        assertThat( relAB().compareTo( relABC() ) < 0 ).isTrue();
    }

    @Test
    public void testRelativePathIsNotEqualtoAbsoluteWithSamePathElements() throws Exception {
        assertThat( relAB().compareTo( absAB() ) ).isNotEqualTo( 0 );
    }

    private int restrictTo1( int in ) {
        return in < 0 ? -1 : 1;
    }

    @Test
    public void testRelativePathsAreInDifferentSpaceThanAbsolutes() throws Exception {

        int diff = restrictTo1( relABC().compareTo( absABC() ) );

        assertThat( restrictTo1( relAB().compareTo( absABC() ) ) ).isEqualTo( diff );
        assertThat( restrictTo1( relABC().compareTo( absAB() ) ) ).isEqualTo( diff );
        assertThat( restrictTo1( relABC().compareTo( absD() ) ) ).isEqualTo( diff );
    }


    @Test
    @SuppressFBWarnings( "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT" ) // how will findbugs know that there are no sideeffects ?
    public void testPathIsImmutableToToAbsolute() throws Exception {
        Path rel = relAB();
        rel.toAbsolutePath();

        assertThat( rel ).isRelative();
    }

    @Test
    public void testPathIsImmutableToNormalize() throws Exception {
        Path unnom = relAB().resolve( ".." );
        assertThat( unnom.toString().contains( ".." ) ).isTrue();
    }

    // only tests class Files
    @Test
    public void testNonExistingAbsolutePathIsNotAFile() throws IOException {
        assertThat( Files.isRegularFile( relAB() ) ).isFalse();
    }

    // only tests class Files
    @Test
    public void testNonExistingRelativePathIsNotAFile() throws IOException {
        assertThat( Files.isRegularFile( relA() ) ).isFalse();
    }

    // todo
//    @Test
//    public void testIllegalCharsInPathThrows() {
//
//        for ( Character ill : capabilities.getPathIllegalCharacters() ) {
//            try {
//                FS.getPath("" + ill);
//                assertThat("illegal character allowed: <" + ill + ">").isEqualTo(""));
//            } catch ( InvalidPathException e ) {
//            }
//        }
//    }

    @Test
    public void testSeparatorIsNoFileName() {
        Path path = FS.getPath( nameD() + FS.getSeparator() + nameA() );

        for( Path elem : path ) {
            assertThat( elem.toString() ).doesNotContain( FS.getSeparator() );
        }

    }

    @Test
    public void testResolveNull() throws IOException {
        assertThatThrownBy( () ->relAB().resolve( (String) null )).isInstanceOf( NullPointerException.class );
    }

    @Test
    public void testNullPath() {
        assertThatThrownBy( () ->FS.getPath( null )).isInstanceOf( NullPointerException.class );
    }

    /*
     * ------------------------------------------------------------------------------------------------------
     */

    protected String[] name = { "aaa", "bbbb", "cccc", "ddddd", "eeeeee" };

    public Path pathDefault() {
        return FS.getPath( "" );
    }

    public Path defaultRoot() {
        return absoluteGetRoot( FS.getPath( "" ).toAbsolutePath() );
    }

    public Path relA() {
        return FS.getPath( nameA() );
    }

    public Path relB() {
        return FS.getPath( nameB() );
    }

    public Path relC() {
        return FS.getPath( nameC() );
    }

    public Path relD() {
        return FS.getPath( nameD() );
    }

    public Path relAB() {
        return FS.getPath( nameA(), nameB() );
    }

    public Path relBC() {
        return FS.getPath( nameB(), nameC() );
    }

    public Path relABC() {
        return FS.getPath( nameA(), nameB(), nameC() );
    }

    public String nameA() {
        return name[ 0 ];
    }

    public String nameB() {
        return name[ 1 ];
    }

    public String nameC() {
        return name[ 2 ];
    }

    public String nameD() {
        return name[ 3 ];
    }

    public String nameE() {
        return name[ 4 ];
    }

    public Path absAB() {
        return defaultRoot().resolve( nameA() ).resolve( nameB() );
    }

    public Path absABC() {
        return defaultRoot().resolve( nameA() ).resolve( nameB() ).resolve( nameC() );
    }

    public Path absD() {
        return defaultRoot().resolve( nameD() );
    }

}
