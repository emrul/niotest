package de.pfabulist.lindwurm.niotest.matcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;

import static de.pfabulist.kleinod.nio.PathIKWID.childGetParent;
import static de.pfabulist.kleinod.nio.PathIKWID.namedGetFilename;

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
//@SuppressFBWarnings( "FindNullDeref" )
public class WatchKeyMatcher extends TypeSafeMatcher<WatchKey> {

    private final Path file;
    private final WatchEvent.Kind<Path> kind;

    @SuppressFBWarnings( value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "findbugs misses the null check before")
    public WatchKeyMatcher( Path file, WatchEvent.Kind<Path> kind ) {
        this.file = file;
        this.kind = kind;

        if( file == null ) {
            throw new NullPointerException( "file is null" );
        }

        if( file.getParent() == null ) {
            throw new NullPointerException( "file is root" );
        }
    }

    @Override
    public boolean matchesSafely( WatchKey key ) {
        if( key == null ) {
            return false;
        }
        if( !key.isValid() ) {
            return false;
        }

        if( !childGetParent( file ).equals( key.watchable() ) ) {
            return false;
        }

        List<WatchEvent<?>> events = key.pollEvents();

        if( events.size() != 1 ) {
            return false;
        }

        WatchEvent<?> event = events.get( 0 );

        if( !namedGetFilename( file ).equals( event.context() ) ) {
            return false;
        }

        return event.kind().equals( kind );
    }

    @Override
    protected void describeMismatchSafely( WatchKey key, Description mismatchDescription ) {
        if( key == null ) {
            mismatchDescription.appendText( "key is null" );
            return;
        }
        if( !key.isValid() ) {
            mismatchDescription.appendText( "key is not valid" );
            return;
        }

        if( !childGetParent( file ).equals( key.watchable() ) ) {
            mismatchDescription.appendText( "key watches the wrong dir. expected: " + file.getParent() + " got: " + key.watchable() );
            return;
        }

        mismatchDescription.appendText( "key reports wrong # of events OR key reports wrong file OR wrong event kind" );
    }

    @Override
    public void describeTo( Description description ) {
        description.appendText( "watchkey is not correct" );
    }

    @Factory
    public static Matcher<WatchKey> correctKey( Path file, WatchEvent.Kind<Path> kind ) {
        return new WatchKeyMatcher( file, kind );
    }

}
