package de.pfabulist.lindwurm.niotest.tests.descriptionbuilders;

import de.pfabulist.lindwurm.niotest.tests.FSDescription;
import de.pfabulist.lindwurm.niotest.tests.topics.LimitedPath;
import de.pfabulist.lindwurm.niotest.tests.topics.MaxFilename;
import de.pfabulist.lindwurm.niotest.tests.topics.MaxPath;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2015, Stephan Pfab
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

public class PathConstraints<T> extends DescriptionBuilder<T> {
    public PathConstraints( FSDescription descr, T t ) {
        super( descr, t );
    }

    public PathConstraints<T> noMaxFilenameLength() {
        descr.removeTopic( MaxFilename.class );
        return this;
    }

    public PathConstraints<T> noMaxPathLength() {
        descr.removeTopic( MaxPath.class );
        return this;
    }

    public PathConstraints<T> unlimitedPathLength() {
        descr.props.put( "maxPathLength", 45000 );
        descr.removeTopic( LimitedPath.class );
        return this;
    }

    public PathConstraints<T> pathLength( int len ) {
        descr.props.put( "maxPathLength", len );
        return this;
    }

    public PathConstraints<T> filenameLength( int len ) {
        descr.props.put( "maxFilenameLength", len );
        return this;
    }

    public PathConstraints<T> unlimitedFilenameLength() {
        descr.props.put( "maxFilenameLength", 42000 );
        return this;
    }

}
