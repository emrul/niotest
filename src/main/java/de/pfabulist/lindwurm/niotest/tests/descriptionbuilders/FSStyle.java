//package de.pfabulist.lindwurm.niotest.test4.descriptionbuilders;
//
//import de.pfabulist.lindwurm.niotest.tests.FSDescription;
//import de.pfabulist.lindwurm.niotest.tests.topics.Windows;
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
//
//public class FSStyle<T> extends DescriptionBuilder<T> {
//
//    public FSStyle( FSDescription descr, T t ) {
//        super( descr, t );
//    }
//
//    public enum Kinds {
//        UNIX,
//        WINDOWS,
//        BASE
//    }
//
//    public T unixStyle() {
//        descr.props.put( "maxFilenameLength", 255 );
//        descr.removeTopic( Windows.class );
////        capa.addFeature("Unix", true);
////        capa.addFeature( "Posix", false );
////        capa.addFeature("CaseIgnorant", false);
////        capa.addFeature("CaseRemembering", false );
////        capa.addFeature("UNC", false);
////        capa.addFeature("RootComponent", false);
////        capa.addFeature("ReadChannelOfDir", false);
//        return t;
//
//    }
//
//    public T typ( Kinds kind ) {
//        switch( kind ) {
//            case UNIX:
//                return unixStyle();
//            default:
//                return null; // base
//        }
//    }
//
//}
