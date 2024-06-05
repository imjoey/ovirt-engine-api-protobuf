/*
Copyright (c) 2018 Joey <majunjiev@gmail.com>.

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

import javax.enterprise.context.ApplicationScoped;

/**
 * This class contains the rules used to calculate the names of generated protobuf packages.
 */
@ApplicationScoped
public class PbPackages {

    // The name of the root package:
    private String ROOT_PACKAGE_NAME = "ovirtapi";

    // The version number:
    private String version;

    // root package url prefix
    private String rootPackageUrlPrefix = "";

    public void setRootPackageUrlPrefix(String newRootPackageUrlPrefix) {
        rootPackageUrlPrefix = newRootPackageUrlPrefix;
    }

    public String getRootPackageUrlPrefix() {
        return rootPackageUrlPrefix;
    }

    /**
     * Sets the version.
     */
    public void setVersion(String newVersion) {
        version = newVersion;
    }

    /**
     * Get the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the name of the root package.
     */
    public String getRootPackageName() {
        return ROOT_PACKAGE_NAME;
    }

    /**
     * Get the complete name of the given package.
     */
    public String getPackageName(String... relativeNames) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(ROOT_PACKAGE_NAME);
        if (relativeNames != null && relativeNames.length > 0) {
            for (String relativeName : relativeNames) {
                buffer.append(".");
                buffer.append(relativeName);
            }
        }
        return buffer.toString();
    }

}