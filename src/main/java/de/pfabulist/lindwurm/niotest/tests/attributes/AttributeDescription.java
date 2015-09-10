package de.pfabulist.lindwurm.niotest.tests.attributes;

import de.pfabulist.lindwurm.niotest.tests.topics.Topic;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
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
@SuppressFBWarnings()
public class AttributeDescription {

    private final String name;
    private final Class<? extends BasicFileAttributeView>  viewType;
    private final Class<? extends BasicFileAttributes>     readType;
    Map<String, Function<BasicFileAttributes,Object>>      read = new HashMap<>();
    Map<String, BiConsumer<BasicFileAttributeView,Object>> view = new HashMap<>();
    private final Class<? extends Topic> topic;

    public AttributeDescription( Class<? extends Topic> topic, String name, Class<? extends BasicFileAttributeView> bfav, Class<? extends BasicFileAttributes> bfas ) {
        this.topic = topic;
        this.name = name;
        this.viewType = bfav;
        this.readType = bfas;
    }

    public String      getName() {
        return name;
    }

    public Set<String> getAttributeNames() {
        return read.keySet();
    }

    public Class<? extends BasicFileAttributeView>    getViewType() {
        return viewType;
    }

    public Class<? extends BasicFileAttributes>    getReadType() {
        return readType;
    }

    public <R extends BasicFileAttributes> Object get( R bfas, String attiName ) {
        if ( !read.containsKey( attiName ) ) {
            throw new IllegalArgumentException( "no such atti " + attiName );
        }

        return read.get( attiName ).apply( bfas );
    }

//    public <V extends BasicFileAttributeView> void set( V bfav, String attiName, Object value ) {
//        if ( !view.containsKey( attiName ) ) {
//            throw new IllegalArgumentException( "no such atti " + attiName );
//        }
//
//        try {
//            view.get( attiName ).accept( bfav, value );
//        } catch (Exception e) {
//            throw Sneaky.sneakyThrow(e);
//        }
//    }

    public Class<? extends Topic> getTopic() {
        return topic;
    }

    void addReader(  String name, Function<BasicFileAttributes,Object> getter ) {
        read.put( name, getter );
    }
}
