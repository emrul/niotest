package de.pfabulist.lindwurm.niotest.tests.descriptionbuilders;

import de.pfabulist.kleinod.os.OS;
import de.pfabulist.kleinod.os.PathLimits;
import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.Tests10PathWithContent;
import de.pfabulist.lindwurm.niotest.tests.topics.CaseInsensitive;
import de.pfabulist.lindwurm.niotest.tests.topics.DosAttributesT;
import de.pfabulist.lindwurm.niotest.tests.topics.NonCasePreserving;
import de.pfabulist.lindwurm.niotest.tests.topics.NotOSX;
import de.pfabulist.lindwurm.niotest.tests.topics.PermissionChecks;
import de.pfabulist.lindwurm.niotest.tests.topics.Posix;
import de.pfabulist.lindwurm.niotest.tests.topics.Windows;

import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.function.Function;

import static de.pfabulist.lindwurm.niotest.tests.attributes.AttributeDescriptionBuilder.attributeBuilding;

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

public class UnixBuilder<T> extends DescriptionBuilder<T>{
    public UnixBuilder( FSDescription descr, T t ) {
        super( descr, t );

        PathLimits pathLimits = new PathLimits( OS.UNIX );
        descr.props.put( Tests10PathWithContent.ONE_CHAR_COUNT, pathLimits.getBigChar() );
        descr.props.put( Tests10PathWithContent.MAX_FILENAME_LENGTH, pathLimits.getMaxFilenameLength() );
        descr.props.put( Tests10PathWithContent.MAX_PATH_LENGTH, pathLimits.getMaxPathLength() );
        descr.props.put( Tests10PathWithContent.GET_FILENAME_LENGTH, (Function<String,Integer>)pathLimits::filenameCount );
        descr.props.put( Tests10PathWithContent.GET_PATH_LENGTH, (Function<String,Integer>)pathLimits::pathCount );


//        descr.removeTopic( LimitedPath.class ); theory but linux c limits
        descr.removeTopic( Windows.class );
        descr.removeTopic( DosAttributesT.class );
        descr.removeTopic( CaseInsensitive.class );
        descr.removeTopic( NonCasePreserving.class );

        descr.attributeDescriptions.put( "posix", attributeBuilding( Posix.class, "posix", PosixFileAttributeView.class, PosixFileAttributes.class ).
                addAttribute( "owner", PosixFileAttributes::owner ).
                addAttribute( "permissions", PosixFileAttributes::permissions ).
                addAttribute( "group", PosixFileAttributes::group ).
                build());

    }

    public UnixBuilder<T> noPosix() {
        descr.removeTopic( Posix.class );
        return this;
    }

    public UnixBuilder<T> noPermissionChecks() {
        descr.removeTopic( PermissionChecks.class );
        return this;
    }

    public UnixBuilder<T> otherUser() {
        descr.removeTopic( PermissionChecks.class );
        return this;
    }

    public UnixBuilder<T> hfsPlus() {
        PathLimits pathLimits = new PathLimits( OS.OSX );
        descr.props.put( Tests10PathWithContent.ONE_CHAR_COUNT, pathLimits.getBigChar() );
        descr.props.put( Tests10PathWithContent.MAX_FILENAME_LENGTH, pathLimits.getMaxFilenameLength() );
        descr.props.put( Tests10PathWithContent.MAX_PATH_LENGTH, pathLimits.getMaxPathLength() );
        descr.props.put( Tests10PathWithContent.GET_FILENAME_LENGTH, (Function<String,Integer>)pathLimits::filenameCount );
        descr.props.put( Tests10PathWithContent.GET_PATH_LENGTH, (Function<String,Integer>)pathLimits::pathCount );

        descr.addTopic( CaseInsensitive.class );
        descr.removeTopic( NotOSX.class );

        // todo : is seperator (in a way)
        return this;
    }

    public UnixBuilder<T> osx() {
        return hfsPlus(); // default for osx
    }
}
