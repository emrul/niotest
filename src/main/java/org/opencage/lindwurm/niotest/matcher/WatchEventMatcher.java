package org.opencage.lindwurm.niotest.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by stephan on 03/04/14.
 */
public class WatchEventMatcher extends TypeSafeMatcher<WatchEvent>  {
    private final Path file;
    private final WatchEvent.Kind<Path> kind;

    public WatchEventMatcher(Path file, WatchEvent.Kind<Path> kind) {
        this.file = file.getFileName();
        this.kind = kind;
    }

    @Override
    protected boolean matchesSafely(WatchEvent event) {
        if ( !file.equals(event.context())) {
            return false;
        }

        return event.kind().equals( kind );
    }

    @Override
    public void describeTo(Description description) {

    }

    @Factory
    public static <T> Matcher<WatchEvent> isEvent( Path file, WatchEvent.Kind<Path> kind ) {
        return new WatchEventMatcher( file, kind );
    }


    @Override
    public String toString() {
        return "WatchEventMatcher{" +
                "file=" + file +
                ", kind=" + kind +
                '}';
    }
}
