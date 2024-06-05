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

import static java.util.stream.Collectors.joining;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.Method;
import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.Service;
import org.ovirt.api.metamodel.tool.Names;
import org.ovirt.api.metamodel.tool.ReservedWords;
import org.ovirt.api.metamodel.tool.Words;

/**
 * This class contains the rules used to calculate the names of generated protobuf concepts.
 */
@ApplicationScoped
public class PbNames {

    // Reference to the object used to do computations with words.
    @Inject
    private Words words;

    // We need the Go reserved words in order to avoid producing names that aren't legal:
    @Inject
    @ReservedWords(language = "protobuf")
    private Set<String> reservedWords;

    @Inject private Names names;

    /**
     * represention of repeated Name
     */
    public String withRepeated(String name) {
        return "repeated " + name;
    }

    /**
     * Returns a representation of the given name typically used for protobuf service
     */
    public String getServiceClassName(Name name) {
        String result = name.words().map(words::capitalize).collect(joining());
        return renameReserved(result + "Service");
    }

    public String getRequestClassName(Method method, Service service) {
        return getServiceClassName(service.getName()) + 
            getRpcName(getFullName(method)) + "Request";
    }

    public String getResponseClassName(Method method, Service service) {
        return getServiceClassName(service.getName()) + 
            getRpcName(getFullName(method)) + "Response";
    }

    /**
     * Returns a representation of the given name typically used for protobuf message
     */
    public String getMessageName(Name name) {
        String result = name.words().map(words::capitalize).collect(joining());
        return renameReserved(result);
    }

    /**
     * Returns a representation of the given name typically used for protobuf rpc
     */
    public String getRpcName(Name name) {
        String result = name.words().map(words::capitalize).collect(joining());
        return renameReserved(result);
    }

    /**
     * Returns a representation of the given name typically used for protobuf enum
     */
    public String getEnumName(Name name) {
        String result = name.words().map(words::capitalize).collect(joining());
        return renameReserved(result);
    }

    /**
     * Returns a representation of the given name typically used for protobuf field
     */
    public String getFieldName(Name name) {
        String result = name.words().map(String::toLowerCase).collect(joining("_"));
        return renameReserved(result);
    }

    /**
     * Returns a representation of the given name typically used for Go constants.
     */
    public String getConstantStyleName(Name name) {
        return name.words().map(String::toUpperCase).collect(joining("_CONST_"));
    }

    private String renameReserved(String result) {
        if (reservedWords.contains(result)) {
            result += "_";
        }
        return result;
    }

    public Name getFullName(Method method) {
        Method base = method.getBase();
        if (base == null) {
            return method.getName();
        }
        return names.concatenate(getFullName(base), method.getName());
    }
}
