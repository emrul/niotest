package de.pfabulist.lindwurm.niotest.tests;

import java.util.Arrays;
import java.util.List;

import static de.pfabulist.lindwurm.niotest.tests.SymLinkDescriptionBuilder.SymLinkOption.*;

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
public class SymLinkDescriptionBuilder {

    public static enum SymLinkOption {
        TO_OTHER_FILESYSTEM,
        TO_DIRECTORIES,
        IMMEDIATE_LOOP_CHECKING,
        DELAYED_LOOP_CHEFCKING
    }
    
    private final FSDescription descr;
    private boolean foreignLinks  = true;
    private boolean dirLinks      = true;

    public SymLinkDescriptionBuilder( FSDescription descr ) {
        this.descr = descr;
    }

    public FSDescription no() {
        descr.hasSymbolicLinks = false;
        return descr;
    }

    public FSDescription yes() {
        descr.hasSymbolicLinks = true;
        return descr;
    }


    public SymLinkDescriptionBuilder with( SymLinkOption... symLinkOptions) {

        List<SymLinkOption> options = Arrays.asList( symLinkOptions ); 
        
        if ( options.contains( DELAYED_LOOP_CHEFCKING )) {
            this.descr.delayedSymLinkLoopChecking = true;
            this.descr.immediateSymLinkLoopChecking = false;
        }

        if ( options.contains( IMMEDIATE_LOOP_CHECKING )) {
            this.descr.delayedSymLinkLoopChecking = false;
            this.descr.immediateSymLinkLoopChecking = true;
        }

        if ( options.contains( TO_DIRECTORIES )) {
            this.descr.hasDirSymLinks = true;
        }

        if ( options.contains( TO_OTHER_FILESYSTEM )) {
            this.descr.supportsForeignSymLinks = true;
        }
        
        return this;
    }

    public SymLinkDescriptionBuilder withOut( SymLinkOption... symLinkOptions) {
        List<SymLinkOption> options = Arrays.asList( symLinkOptions );

        if ( options.contains( DELAYED_LOOP_CHEFCKING )) {
            this.descr.delayedSymLinkLoopChecking = false;
        }

        if ( options.contains( IMMEDIATE_LOOP_CHECKING )) {
            this.descr.immediateSymLinkLoopChecking = false;
        }

        if ( options.contains( TO_DIRECTORIES )) {
            this.descr.hasDirSymLinks = false;
        }

        if ( options.contains( TO_OTHER_FILESYSTEM )) {
            this.descr.supportsForeignSymLinks = false;
        }
        
        return this;

    }

}