package de.pfabulist.lindwurm.niotest.tests.attributes;

import de.pfabulist.lindwurm.niotest.tests.topics.Topic;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

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
public class AttributeDescriptionBuilder<V extends BasicFileAttributeView, R extends BasicFileAttributes> {

    AttributeDescription connection;

    public AttributeDescriptionBuilder( Class<? extends Topic> topic, String name, Class<V> bfav, Class<R> bfas ) {
        connection = new AttributeDescription( topic, name, bfav, bfas );
    }

    public static <VV extends BasicFileAttributeView, RR extends BasicFileAttributes> AttributeDescriptionBuilder<VV, RR>
                    attributeBuilding( Class<? extends Topic> topic, String name, Class<VV> bfav, Class<RR> bfas ) {
        return new AttributeDescriptionBuilder<>( topic, name, bfav, bfas );
    }

//    public AttributeDescriptionBuilder<V, R> addAttribute( String name, Function<R, Object> get, BiConsumer<V, Object> set ) {
//        connection.read.put( name, get );
//        connection.view.put( name, set );
//        return this;
//    }

    public AttributeDescriptionBuilder<V, R> addAttribute( String name, Function<R, Object> get ) {
        connection.read.put( name, (Function<BasicFileAttributes, Object>) get );
        return this;
    }

    public AttributeDescription build() {
        return connection;
    }

}
