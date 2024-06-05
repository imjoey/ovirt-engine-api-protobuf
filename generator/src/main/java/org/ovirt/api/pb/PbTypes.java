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

import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.EnumType;
import org.ovirt.api.metamodel.concepts.ListType;
import org.ovirt.api.metamodel.concepts.Model;
import org.ovirt.api.metamodel.concepts.PrimitiveType;
import org.ovirt.api.metamodel.concepts.StructType;
import org.ovirt.api.metamodel.concepts.Type;

/**
 * This class calculates the type references for the protobuf structs generated from the model.
 */
public class PbTypes {

    // Reference to the object used to do computations with words.
    @Inject private PbNames pbNames;

    public PbClassName getClassName(Type type) {
        if (type instanceof PrimitiveType) {
            return getPrimitiveTypeName((PrimitiveType) type);
        }
        if (type instanceof StructType || type instanceof EnumType) {
            PbClassName typeName = new PbClassName();
            typeName.setSimpleName(pbNames.getMessageName(type.getName()));
            return typeName;
        }
        throw new RuntimeException("Don't know how to calculate the Java type name for type \"" + type + "\"");
    }

    private PbClassName getPrimitiveTypeName(PrimitiveType type) {
        PbClassName name = new PbClassName();
        Model model = type.getModel();
        if (type == model.getBooleanType()) {
            name.setSimpleName("bool");
        }
        else if (type == model.getIntegerType()) {
            name.setSimpleName("int64");
        }
        else if (type == model.getDecimalType()) {
            name.setSimpleName("double");
        }
        else if (type == model.getStringType()) {
            name.setSimpleName("string");
        }
        else if (type == model.getDateType()) {
            name.setPackageName("google/protobuf/timestamp.proto");
            name.setSimpleName("google.protobuf.Timestamp");
        }
        else {
            throw new RuntimeException("Don't know how to calculate the Java type reference for type \"" + type + "\"");
        }
        return name;
    }

    private PbTypeReference getTypeReference(Type type, String packageName) {
        if (type instanceof PrimitiveType) {
            PbClassName name = getPrimitiveTypeName((PrimitiveType) type);
            PbTypeReference reference = new PbTypeReference();
            reference.addImport(name.getPackageName());
            reference.setText(name.getSimpleName());
            return reference;
        }
        if (type instanceof StructType) {
            return getStructReference((StructType) type, packageName);
        }
        if (type instanceof EnumType) {
            return getEnumReference((EnumType) type, packageName);
        }
        if (type instanceof ListType) {
            return getListReference((ListType) type, packageName);
        }
        throw new RuntimeException("Don't know how to calculate the Java type reference for type \"" + type + "\"");
    }

    public PbTypeReference getTypeReferenceAsField(Type type, String packageName) {
        PbTypeReference reference = getTypeReference(type, packageName);
        return reference;
    }

    private PbTypeReference getStructReference(StructType type, String packageName) {
        PbClassName name = new PbClassName();
        name.setPackageName(packageName);
        name.setSimpleName(pbNames.getMessageName(type.getName()));

        PbTypeReference reference = new PbTypeReference();
        reference.setText(name.getFullName());
        return reference;
    }

    private PbTypeReference getEnumReference(EnumType type, String packageName) {
        PbClassName name = new PbClassName();
        name.setPackageName(packageName);
        name.setSimpleName(pbNames.getEnumName(type.getName()));

        PbTypeReference reference = new PbTypeReference();
        reference.setText(name.getFullName());
        return reference;
    }

    private PbTypeReference getListReference(ListType type, String packageName) {
        PbTypeReference reference = new PbTypeReference();
        Type elementType = type.getElementType();
        PbTypeReference elementTypeReference = getTypeReference(elementType, packageName);

        reference.setImports(elementTypeReference.getImports());
        reference.setText("repeated " + elementTypeReference.getText());
        return reference;
    }
}
