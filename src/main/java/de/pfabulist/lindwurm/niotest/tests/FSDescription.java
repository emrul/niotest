package de.pfabulist.lindwurm.niotest.tests;

import de.pfabulist.lindwurm.niotest.tests.attributes.AttributeDescription;
import de.pfabulist.lindwurm.niotest.tests.topics.Basic;
import de.pfabulist.lindwurm.niotest.tests.topics.Topic;
import org.junit.rules.TestName;

import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static de.pfabulist.lindwurm.niotest.tests.attributes.AttributeDescriptionBuilder.attributeBuilding;

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

public class FSDescription {

    private final List<Class<? extends Topic>> notProvidedTopics = new ArrayList<>();
    public final Map<String, Object> props = new HashMap<>();
    private final Set<String> bugs = new HashSet<>();
    private final Set<String> bugSchemes = new HashSet<>();

    public ClosedFSVars closedFSVars;
    public Path otherProviderPlayground;

    private Set<String> usedBugs = new HashSet<>();
    private Set<String> usedSchemes = new HashSet<>();
    public final List<AttributeDescription> attributeDescriptions = new ArrayList<>();

    public FSDescription() {
        attributeDescriptions.add( attributeBuilding( Basic.class, "basic", BasicFileAttributeView.class, BasicFileAttributes.class ).
                addAttribute( "lastModifiedTime", BasicFileAttributes::lastModifiedTime ).
                addAttribute( "creationTime", BasicFileAttributes::creationTime ).
                addAttribute( "size", BasicFileAttributes::size ).
                addAttribute( "lastAccessTime", BasicFileAttributes::lastAccessTime ).
                addAttribute( "isDirectory", BasicFileAttributes::isDirectory ).
                addAttribute( "isSymbolicLink", BasicFileAttributes::isSymbolicLink ).
                addAttribute( "isOther", BasicFileAttributes::isOther ).
                build());
    }

    public <T> T get( Class<T> klass, String key ) {
        Object val = props.get( key );

        if ( val == null ) {
            throw new IllegalStateException( "niotest error: no value for " + key );
        }

        if ( val.getClass().isAssignableFrom( klass )) {
            throw new IllegalStateException( "niotest error: wrong class for: " + key + " expected: " + klass + " got: " + val.getClass());
        }

        return klass.cast( val );
    }

    public int getInt( String key ) {
        Object ret = props.get( key );
        if ( ret == null ) {
            return 0;
        }

        return (Integer)ret;
    }

    public FSDescription addTopic( Class<? extends Topic> clazz ) {
        notProvidedTopics.remove( clazz );
        return this;
    }

    public FSDescription removeTopic( Class<? extends Topic> clazz ) {
        notProvidedTopics.add( clazz );
        return this;
    }

    public boolean provides( Class<?> clazz ) {
        //boolean foo = notProvidedTopics.stream().findFirst( t -> clazz.isAssignableFrom( t )).isPresent();
        return !notProvidedTopics.contains( clazz );
    }

    public Object get( String key ) {
        return props.get( key );
    }

    public boolean isBug( TestName testMethodName ) {
        return bugs.contains( testMethodName.getMethodName() ) ||
               bugSchemes.stream().anyMatch( scheme -> testMethodName.getMethodName().contains( scheme ) );

    }

    public void addBug( String name ) {
        bugs.add( name );
    }

    public void addBugScheme( String scheme ) {
        bugSchemes.add( scheme );
    }

    public void markHits( TestName testMethodName ) {
        Optional<String> found = bugs.stream().filter( name -> name.equals( testMethodName.getMethodName() ) ).findFirst();

        if ( found.isPresent() ) {
            usedBugs.add( found.get() );
            return;
        }

        Optional<String> usedScheme =  bugSchemes.stream().filter( scheme -> testMethodName.getMethodName().contains( scheme ) ).findFirst();

        if ( usedScheme.isPresent()) {
            usedSchemes.add( usedScheme.get());
        }
    }

    public void printUnused() {
        for ( String bug : bugs ) {
            if ( !usedBugs.contains( bug )) {
                System.out.println( "not found method called " + bug );
            }
        }

        for ( String scheme : bugSchemes ) {
            if ( !usedSchemes.contains( scheme )) {
                System.out.println( "bug scheme did not apply :  " + scheme );
            }
        }
    }

    public Stream<AttributeDescription> getAttributeDescriptions() {
        return attributeDescriptions.stream().filter( ad -> !notProvidedTopics.contains( ad.getTopic()) );

    }


    public static class ClosedFSVars {

        public FileSystem fs;
        public Path                  play;
        public Path fileA;
        public Path dirB;
        public Path                  pathCf;
        public SeekableByteChannel readChannel;
        public URI uri;
        public DirectoryStream<Path> dirStream;
        public WatchService watchService;
        public FileSystemProvider provider;

        public ClosedFSVars(Path play) {
            this.play = play;
        }
    }
}
