/*
Copyright (c) 2017 Joey <majunjiev@gmail.com>.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.ovirt.api.pb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.ovirt.api.metamodel.tool.ReservedWords;

/**
 * This class is a producer of the set of protobuf reserved words
 */
@Singleton
public class PbReservedWords {
    private Set<String> words;

    @PostConstruct
    private void init() {
        // Create the set:
        words = new HashSet<>();

        // definition
        words.add("option");
        words.add("proto3");
        words.add("protobuf");
        words.add("syntax");
        words.add("package");
        words.add("import");
        words.add("message");
        words.add("enum");
        words.add("service");
        words.add("rpc");
        words.add("stream");
        words.add("oneof");

        // type
        words.add("double");
        words.add("float");
        words.add("int32");
        words.add("int64");
        words.add("uint32");
        words.add("uint64");
        words.add("sint32");
        words.add("sint64");
        words.add("fixed32");
        words.add("fixed64");
        words.add("sfixed32");
        words.add("sfixed64");
        words.add("string");
        words.add("bytes");
        words.add("repeated");
        words.add("oneof");
        words.add("map");
        words.add("bool");

        // Wrap the set so that it is unmodifiable:
        words = Collections.unmodifiableSet(words);
    }

    /**
     * Produces the set of protobuf reserved words.
     */
    @Produces
    @ReservedWords(language = "protobuf")
    public Set<String> getWords() {
        return words;
    }
}
