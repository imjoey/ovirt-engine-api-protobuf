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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.EnumType;
import org.ovirt.api.metamodel.concepts.EnumValue;
import org.ovirt.api.metamodel.concepts.Model;
import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.StructMember;
import org.ovirt.api.metamodel.concepts.StructType;
import org.ovirt.api.metamodel.concepts.Type;

/**
 * This class is responsible for generating the classes that represent the types of the model.
 */
public class TypesGenerator implements PbGenerator {

    private static String PACKAGE_NAME = "types";

    // The directory were the output will be generated:
    protected File out;

    // Reference to the objects used to generate the code:
    @Inject private PbNames pbNames;

    // The buffer used to generate the code:
    private PbBuffer buffer;

    // Reference to the object used to calculate protobuf types:
    @Inject private PbTypes pbTypes;
    @Inject private PbPackages pbPackges;


    public void setOut(File newOut) {
        out = newOut;
    }

    public void generate(Model model) {
        // Prepare the buffer:
        buffer = new PbBuffer();
        buffer.setPackageName(pbPackges.getPackageName(PACKAGE_NAME));

        buffer.addOptions(PbBuffer.DEFAULT_OPTIONS);

        // Generate the code:
        generateTypes(model);

        // Write the file:
        try {
            buffer.write(out);
        }
        catch (IOException exception) {
            throw new RuntimeException("Error writing types module", exception);
        }
    }

    private void generateTypes(Model model) {
        // Get the list of struct types:
        List<StructType> structs = model.types()
            .filter(StructType.class::isInstance)
            .map(StructType.class::cast)
            .collect(toList());

        // Generate the type:
        structs.forEach(this::generateStruct);

        // Enum types don't need any special order, so we sort them only by name:
        model.types()
            .filter(EnumType.class::isInstance)
            .map(EnumType.class::cast)
            .forEach(this::generateEnum);
    }

    private void generateStruct(StructType type) {
        // Begin class:
        PbClassName typeName = pbTypes.getClassName(type);

        // Define Struct
        buffer.addLine("message %1$s {", typeName.getSimpleName());

        // Constructor with a named parameter for each attribute and link:
        Set<StructMember> allMembers = Stream.concat(type.attributes(), type.links())
            .collect(toSet());
        Set<StructMember> declaredMembers = Stream.concat(type.declaredAttributes(), type.declaredLinks())
            .collect(toSet());
        allMembers.addAll(declaredMembers);

        // sorted by name
        StructMember[] allMembersArray = allMembers.stream().sorted().toArray(StructMember[]::new);
        for (int index  = 0; index < allMembersArray.length; index++) {
            generateField(index + 1, allMembersArray[index]);
        }
        // End class:
        buffer.addLine("}");
        buffer.addLine();
    }

    private void generateEnum(EnumType type) {
        // Begin class:
        PbClassName typeName = pbTypes.getClassName(type);

        buffer.addLine("enum %1$s {", typeName.getSimpleName());
        // Type definition
        buffer.addLine("  %1$s_%2$s = %3$d;",
            typeName.getSimpleName().toUpperCase(),
            "UNSPECIFIED",
            0);

        EnumValue[] enumValueArray = type.values().sorted().toArray(EnumValue[]::new);

        for (int index  = 0; index < enumValueArray.length; index++) {
            generateEnumValue(index + 1, enumValueArray[index]);
        }

        buffer.addLine("}");

        // End definition:
        buffer.addLine();
    }

    private void generateEnumValue(int index, EnumValue value) {
        Name name = value.getName();
        String constantName = pbNames.getConstantStyleName(name);
        String className = pbTypes.getClassName(value.getDeclaringType()).getSimpleName();
        // String constantValue = names.getLowerJoined(name, "_");

        buffer.addLine("  %1$s_%2$s = %3$d;", className.toUpperCase(), constantName, index);
    }

    private void generateField(int index, StructMember member) {
        Type memberType = member.getType();
        PbTypeReference memberTypeReference = pbTypes.getTypeReferenceAsField(memberType, null);
        buffer.addImports(memberTypeReference.getImports());

        buffer.addLine(
            "%1$s %2$s = %3$d;",
            memberTypeReference.getText(),
            pbNames.getFieldName(member.getName()),
            index
        );
    }

}

