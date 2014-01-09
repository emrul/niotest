package org.opencage.niotest.lindwurm.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.nio.file.attribute.FileTime;
import java.util.Date;

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
public class FileTimeMatcher extends TypeSafeMatcher<FileTime> {

    private FileTime from = FileTime.fromMillis( 100 );
    private FileTime to   = FileTime.fromMillis( new Date().getTime() + ( 24* 60 * 60 * 1000 )) ;

    public FileTimeMatcher( FileTime from, FileTime to ) {
        this.from = from;
        this.to = to;
    }
    public FileTimeMatcher( FileTime from ) {
        this.from = from;
    }

    @Override
    public boolean matchesSafely( FileTime ft ) {

        return from.compareTo( ft ) <= 0 && ft.compareTo( to ) <= 0;
    }

    @Override
    public void describeTo( Description description ) {
        description.appendText( "filetime is not in [" + from + ", " + to + "] " );
    }

    @Factory
    public static <T> Matcher<FileTime> after( FileTime other ) {
        return new FileTimeMatcher( other );
    }

}
