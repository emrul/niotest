package de.pfabulist.lindwurm.niotest.testsn.setup;

import org.hamcrest.TypeSafeMatcher;

import java.nio.file.Path;

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
public class CapBuilder00 {

    
    public static enum SimOS {
        NEUTRAL, WINDOWS, UNIX, OSX
    }
    
    public static enum FSType {
        NEUTRAL, NTFS, HFSplus, WINDOWS_STYLE, UNIX_STYLE, EXT2
    }

//    public static AllCapabilitiesBuilder os(SimOS os) {
//        return new AllCapabilitiesBuilder(os);
//    }
    
    public static TypeBuilder typ( FSType type ) {
        return new TypeBuilder(type);
    }

    public static class TypeBuilder extends DetailBuilder {
        public TypeBuilder(FSType type) {
            super( new AllCapabilitiesBuilder());
            switch ( type ) {
                case NEUTRAL:
                    break;
                case NTFS:
                    capa.attributes.put( "maxFilenameLength", 255 );
                    capa.attributes.put( "maxPathLength", 32000 );
                    capa.addFeature("Unix", false);
                    capa.addFeature("Posix", false );
                    capa.addFeature("Windows", true);
                    capa.addFeature("CaseIgnorant", true);
                    capa.addFeature("CaseRemembering", true);
                    capa.addFeature("UNC", true);
                    break;
                case HFSplus:
                    break;
                case WINDOWS_STYLE:
                    capa.attributes.put( "maxFilenameLength", 255 );
                    capa.attributes.put( "maxPathLength", 32000 );
                    capa.addFeature("Unix", false);
                    capa.addFeature("Posix", false );
                    capa.addFeature("Windows", false );
                    capa.addFeature("CaseIgnorant", true);
                    capa.addFeature("CaseRemembering", true);
                    capa.addFeature("UNC", false );
                    capa.addFeature("RootComponent", false);
                    break;
                case UNIX_STYLE:
                    capa.attributes.put( "maxFilenameLength", 255 );
                    capa.addFeature("Unix", true);
                    capa.addFeature("Windows", false);
                    capa.addFeature( "Posix", false );
                    capa.addFeature("CaseIgnorant", false);
                    capa.addFeature("CaseRemembering", false );
                    capa.addFeature("UNC", false);
                    capa.addFeature("RootComponent", false);
                    break;
                case EXT2:
                    capa.attributes.put( "maxFilenameLength", 255 );
                    capa.addFeature("Unix", true);
                    capa.addFeature("Windows", false);
                    capa.addFeature( "Posix", true );
                    capa.addFeature("CaseIgnorant", false);
                    capa.addFeature("CaseRemembering", false );
                    capa.addFeature("UNC", false);
                    capa.addFeature("RootComponent", false);
                    break;
            }
        }
        
        public TypeBuilder unc( boolean cond ) {
            capa.addFeature("UNC", cond );
            return this;
        }
        
        public TypeBuilder rootComponent( boolean cnd ) {
            capa.addFeature("RootComponent", cnd );
            return this;
        }

        @Override
        public AllCapabilitiesBuilder onOff(boolean val) {
            return builder;
        }
    }
    

//    public static AllCapabilitiesBuilder windows() {
//        return os( SimOS.WINDOWS );
//    }
//
//    public static AllCapabilitiesBuilder unix() {
//        return os( SimOS.UNIX );
//        
//    }

//    private final SimOS os;
    public final Capa capa = new Capa();

//    public CapBuilder00(SimOS os) {
//        this.os = os;
//        switch ( os ) {
//            case NEUTRAL:
//                break;
//            case WINDOWS: {
//                capa.attributes.put( "maxFilenameLength", 255 );
//                capa.attributes.put( "maxPathLength", 32000 );
//                capa.addFeature("Unix", false);
//                capa.addFeature("Posix", false );
//                capa.addFeature("Windows", true);
//                capa.addFeature("CaseIgnorant", true);
//                capa.addFeature("CaseRemembering", true);
//                capa.addFeature("UNC", true);
//                break;
//            }
//            case UNIX:
//                capa.attributes.put( "maxFilenameLength", 255 );
//                capa.addFeature("Unix", true);
//                capa.addFeature("Windows", false);
//                capa.addFeature( "Posix", true );
//                capa.addFeature("CaseIgnorant", false);
//                capa.addFeature("CaseRemembering", false );
//                capa.addFeature("UNC", false);
//                break;
//            case OSX:
//                capa.attributes.put( "maxFilenameLength", 255 );
//                capa.addFeature( "Unix", true );
//                capa.addFeature("Windows", false);
//                capa.addFeature("Posix", true);
//                capa.addFeature("CaseIgnorant", false );
//                capa.addFeature("CaseRemembering", false );
//                capa.addFeature("UNC", false);
//                break;
//        }
//    }

    
    public AllCapabilitiesBuilder playground( Path path ) {
        capa.attributes.put( "playground", path );
        return (AllCapabilitiesBuilder) this;
    }

    public AllCapabilitiesBuilder bug( String testMethodName, boolean cond ) {
        if ( cond ) {
            capa.bugs.add( testMethodName );
        }
        return (AllCapabilitiesBuilder) this;
    }

    public AllCapabilitiesBuilder bug( String testMethodName ) {
        return bug( testMethodName, true );
    }
    
    public AllCapabilitiesBuilder bugScheme( String substr ) {
        capa.addBugSchemes(substr);
        return (AllCapabilitiesBuilder) this;
    }
    
    public AllCapabilitiesBuilder nitpick( String testMethod, String reason ) {
        return bug( testMethod );
    }


    public Capa build() {
        return capa;
    }

}
