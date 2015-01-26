package de.pfabulist.lindwurm.niotest.testsn;


import de.pfabulist.lindwurm.niotest.testsn.setup.AllCapabilitiesBuilder;
import de.pfabulist.lindwurm.niotest.testsn.setup.Capa;

/**
* ** BEGIN LICENSE BLOCK *****
* BSD License (2 clause)
* Copyright (c) 2006 - 2014, Stephan Pfab
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
*
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
public abstract class Tests18FileChannels extends Tests17Windows {
    public Tests18FileChannels( Capa capa) {
        super(capa);
//        features.put("FileChannel", capa::hasFileChannels );
    }
    
    public static class CapaBuilder18 extends CapaBuilder13 {

        public AllCapabilitiesBuilder fileChannels(boolean val) {
            capa.addFeature( "FileChannel", val );
            return (AllCapabilitiesBuilder) this;
        }
    }
    

//    @Test
//    public void testFileChannelSimple() throws IOException {
//        try ( FileChannel fch = FS.provider().newFileChannel(getPathPABf(), Sets.asSet(StandardOpenOption.WRITE, StandardOpenOption.READ))) {
//            try ( FileLock lock = fch.tryLock()) {
//                System.out.println(lock);
//            }
//
//        }
//    }


}
