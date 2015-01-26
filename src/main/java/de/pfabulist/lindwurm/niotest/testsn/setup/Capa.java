package de.pfabulist.lindwurm.niotest.testsn.setup;

import de.pfabulist.lindwurm.niotest.tests.ClosedFSVars;
import org.junit.rules.TestName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class Capa {

    public final Map<String,Object> attributes = new HashMap<>();
    public final Set<String> bugs = new HashSet<>();
    public final Set<String> bugSchemes = new HashSet<>();
    public final Set<String> antiSchemes = new HashSet<>();
    public ClosedFSVars closedFSVars;


    public <T> T get( Class<T> clazz, String key ) {
        return (T) attributes.get( key );
    }

    public int getInt( String key ) {
        Object ret = attributes.get( key );
        if ( ret == null ) {
            return 0;
        }
        
        return (Integer)ret;
    }
    
    public boolean has( String key ) {
        Object val = attributes.get(key);
        
        return val == null || (boolean)val;
    }            
    
    public boolean isBug( TestName testName) {
        
        String name = testName.getMethodName();
        
        if ( bugs.contains( name )) {
            return true;
        }
        
        return bugSchemes.stream().filter( s->s.contains(name)).findFirst().isPresent();
    }
    
    public boolean isPossible( TestName testName ) {
        String name = testName.getMethodName();

        if ( bugs.contains( name )) {
            return false;
        }
        return !antiSchemes.stream().filter( s->name.contains(s)).findFirst().isPresent();
    }


    public void addFeature(String feature, boolean cond ) {
        if ( cond ) {
            antiSchemes.remove( feature );
        } else {
            antiSchemes.add( feature );
        }
    }

    public void addBugSchemes(String substr) {
        antiSchemes.add( substr );
        bugSchemes.add( substr );
    }
}
