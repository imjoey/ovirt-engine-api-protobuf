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

import static java.util.stream.Collectors.toCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.Method;
import org.ovirt.api.metamodel.concepts.Model;
import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.Parameter;
import org.ovirt.api.metamodel.concepts.Service;

/**
 * This class is responsible for generating the classes that represent the services of the model.
 */
public class ResponsesGenerator implements PbGenerator {

    private static String PACKAGE_NAME = "responses";

    // The directory were the output will be generated:
    protected File out;

    // Reference to the objects used to generate the code:
    @Inject private PbNames pbNames;

    // The buffer used to generate the code:
    private PbBuffer buffer;

    // Reference to the object used to calculate protobuf types:
    @Inject private PbTypes pbTypes;

    @Inject private PbPackages pbPackges;

    /**
     * Set the directory were the output will be generated.
     */
    public void setOut(File newOut) {
        out = newOut;
    }

    public void generate(Model model) {
        // Prepare the buffer:
        buffer = new PbBuffer();
        buffer.setPackageName(pbPackges.getPackageName(PACKAGE_NAME));

        buffer.addImport("types.proto");

        buffer.addOptions(PbBuffer.DEFAULT_OPTIONS);

        // Generate the code:
        generateResponse(model);

        // Write the file:
        try {
            buffer.write(out);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Error writing services module", exception);
        }
    }

    private void generateResponse(Model model) {
        model.services().forEach(this::generateResponse);
    }

    private void generateResponse(Service service) {
        // Generate the Request and Response messages for each method
        List<Method>methods = service.methods().sorted().collect(toCollection(ArrayList::new));
        for (Method method : methods) {
            generateResponse(method, service);
        }
        buffer.addLine();
    }

    private void generateResponse(Method method, Service service) {
        // Begin class
        String request = pbNames.getResponseClassName(method, service);

        buffer.addLine("message %1$s {", request);

        // //      Generate common parameters
        // generateRequestCommonParameter();

        //      Generate the input parameters
        Parameter[] inputParametersArray = method.parameters()
            .filter(Parameter::isIn)
            .sorted().toArray(Parameter[]::new);
        for (int index = 0; index < inputParametersArray.length; index++) {
            generateResponseParameter(index, inputParametersArray[index]);
        }

        buffer.addLine("}");
        // End class
    }

    private void generateResponseParameter(int index, Parameter parameter) {
        // Get parameter name
        Name parameterName = parameter.getName();
        PbTypeReference attrTypeReference = pbTypes.getTypeReferenceAsField(parameter.getType(), null);
        buffer.addImports(attrTypeReference.getImports());

        String arg = pbNames.getFieldName(parameterName);
        // Get parameter type name
        buffer.addLine(
            "%1$s %2$s = %3$d;", attrTypeReference.getText(), arg, index + 1);
    }

}
