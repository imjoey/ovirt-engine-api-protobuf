# oVirt API Protobuf

## Introduction

The oVirt API Protobuf use ovirt-engine-api-model to auto-generate the protobuf
definitions, which could be used to for gRPC APIs.

> IMPORTANT: This document describes how to generate, build and test the
SDK. If you are interested in how to use it read the `README.md` file
in the [oVirt/go-ovirt](https://github.com/oVirt/go-ovirt) repository instead.

## Building


```bash
$ git clone git@github.com:imjoey/ovirt-engine-api-protobuf.git
$ cd ovirt-engine-api-protobuf
$ mvn package
```

This will build the code generator, run it to generate the protobuf
definitions of the API that corresponds to the branch of the api model
that you are using.

If you need to generate it for a different version of the API then you
can use the `model.version` property. For example, if you need to
generate the SDK for version `4.1.0` of the SDK you can use this
command:
```bash
$ mvn package -Dmodel.version=4.1.0
```

