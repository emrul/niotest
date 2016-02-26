//package de.pfabulist.lindwurm.niotest.matcher;
//
//import de.pfabulist.unchecked.functiontypes.RunnableE;
//import org.hamcrest.Description;
//import org.hamcrest.Factory;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//
//
///**
// * ** BEGIN LICENSE BLOCK *****
// * BSD License (2 clause)
// * Copyright (c) 2006 - 2015, Stephan Pfab
// * All rights reserved.
// * <p>
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// * * Redistributions of source code must retain the above copyright
// * notice, this list of conditions and the following disclaimer.
// * * Redistributions in binary form must reproduce the above copyright
// * notice, this list of conditions and the following disclaimer in the
// * documentation and/or other materials provided with the distribution.
// * <p>
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL Stephan Pfab BE LIABLE FOR ANY
// * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// * **** END LICENSE BLOCK ****
// */
//public class ExceptionMatcher<T extends Exception> extends TypeSafeMatcher<RunnableE<T>> {
//
//    private final Class<Exception> exp;
//
//    public ExceptionMatcher( Class<Exception> e ) {
//        this.exp = e;
//    }
//
//    @Factory
//    public static <E extends Exception> Matcher<RunnableE<Exception>> throwsException( Class<E> e) {
//        return new ExceptionMatcher((Class) e );
//    }
//
//    @Override
//    protected boolean matchesSafely( RunnableE<T> f ) {
//        try {
//            f.run();  // NOSONAR
//        } catch ( Exception e ) { // NOSONAR
//            if ( exp.isAssignableFrom( e.getClass() )) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void describeTo( Description description ) {
//        description.appendText( "lambda does not throw expected exception " + exp.getName() );
//    }
//
//}
