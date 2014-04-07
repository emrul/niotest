package org.opencage.lindwurm.niotest.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by stephan on 03/04/14.
 */
public class Has<T> extends TypeSafeDiagnosingMatcher<Iterable<? extends T>> {

    private final Matcher<? super T>[] matchers;

    public Has(Matcher<? super T>[] matchers ) {
        this.matchers = matchers;
    }

    @Override
    protected boolean matchesSafely(Iterable<? extends T> elems, Description description) {


        Set<Matcher<? super T>> all = new HashSet<>();
        all.addAll(Arrays.asList(matchers));

        for ( T elem : elems ) {
            Matcher<? super T> found = null;
            for ( Matcher<? super T> matcher : all ) {
                if ( matcher.matches( elem )) {
                    found = matcher;
                }
            }
            if ( found != null ) {
                all.remove( found );
            }
        }

        for ( Matcher<? super T> matcher : all ) {
            description.appendText( matcher + " not found in collection " );
        }

        return all.isEmpty();
    }


    @Override
    public void describeTo(Description description) {
        description.appendText("iterable includes elems for all matchers");
    }



    public static <T> Matcher<Iterable<? extends T>> has(Matcher<? super T> ... items) {
        return new Has( items );
    }


}
