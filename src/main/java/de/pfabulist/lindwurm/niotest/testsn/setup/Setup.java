package de.pfabulist.lindwurm.niotest.testsn.setup;

import de.pfabulist.kleinod.paths.Pathss;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

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
public abstract class Setup {
    
    protected final Capa capa;

    protected String[] name = {"aaa", "bbbb", "cccc", "ddddd", "eeeeee"};
    protected final FileSystem FS;
    protected Path otherProvidorPlayground;

    @Rule
    public TestName testMethodName = new TestName();


    protected Setup( Capa capa ) {
        this.capa = capa;
        FS = capa.get( Path.class, "playground" ).getFileSystem();
    }

    @Before
    public void filter() {
        assumeThat( message(), capa.isPossible( testMethodName ), is( true ));
    }



    // TODO
//    @AfterClass
//    public static void afterClass() {
//        if ( dontDelete != null ) {
//            PathUtils.delete( play );
//        }
//    }

    protected String message() {
        if ( capa.isBug( testMethodName )) {
            return "known bugs " + testMethodName.getMethodName();
        }

        return "";
    }
    
    protected Path getOtherProviderPlayground() {
        
        if ( otherProvidorPlayground == null ) {
            Path other = capa.get(Path.class, "otherProviderPlayground");
            if (other != null) {
                otherProvidorPlayground = other;
            } else {
                if ( FS.equals(FileSystems.getDefault())) {
                    throw new IllegalStateException();
                }
     
                otherProvidorPlayground = Pathss.getTmpDir("other");
            }
        }
        
        return otherProvidorPlayground;
    }
}
