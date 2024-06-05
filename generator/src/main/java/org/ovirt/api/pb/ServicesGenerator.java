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
import org.ovirt.api.metamodel.concepts.Service;

/**
 * This class is responsible for generating the classes that represent the services of the model.
 */
public class ServicesGenerator implements PbGenerator {

    private static String PACKAGE_NAME = "services";

    // The directory were the output will be generated:
    protected File out;

    // Reference to the objects used to generate the code:
    @Inject private PbNames pbNames;

    // The buffer used to generate the code:
    private PbBuffer buffer;

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

        buffer.addImport("requests.proto");
        buffer.addImport("responses.proto");

        buffer.addOptions(PbBuffer.DEFAULT_OPTIONS);

        // Generate the code:
        generateServices(model);

        // Write the file:
        try {
            buffer.write(out);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Error writing services module", exception);
        }
    }

    private void generateServices(Model model) {
        model.services().forEach(this::generateService);
    }

    private void generateService(Service service) {
        // Begin class:
        PbClassName serviceName = new PbClassName(); //getServiceName(service);
        serviceName.setSimpleName(pbNames.getServiceClassName(service.getName()));

        // Generate the Request and Response messages for each method
        List<Method>methods = service.methods().sorted().collect(toCollection(ArrayList::new));

        // Generate service definition
        buffer.addLine("service %1$s {", serviceName.getSimpleName());
        
        for (Method method : methods) {
            generateRpcs(method, service);
        }

        buffer.addLine("}");
        buffer.addLine();
    }

    /**
     * Generates the code that corresponds to a single rpc method.
     */
    private void generateRpcs(Method method, Service service) {
        // Generate the RPC methods
        buffer.addLine("rpc %1$s (%2$s) returns (%3$s) {};",
            pbNames.getRpcName(pbNames.getFullName(method)),
            pbNames.getRequestClassName(method, service),
            pbNames.getResponseClassName(method, service));
    }

}
