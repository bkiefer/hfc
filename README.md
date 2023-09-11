# HFC: HUK's Forward Chainer

[![mvn build](https://github.com/bkiefer/hfc/actions/workflows/maven.yml/badge.svg)](https://github.com/bkiefer/hfc/actions/workflows/maven.yml)

## Introduction

HFC is a bottom-up forward chainer and semantic repository implemented
in Java which have been developed over the last years and which is
comparable to popular systems such as Jena and OWLIM.
HFC supports RDFS and OWL reasoning a la Hayes (2004) and ter Horst
(2005), but at the same time provides a powerful language for defining
custom rules, involving functional and relational variables, complex
tests and actions, and the replacement of triples in favor of tuples
of arbitrary length, going beyond the expressiveness of rule-based
forward chainers and tableaux-based reasoners.

## Installation

The following installation instructions are tested on Ubuntu 22.04

Prerequisites for installing VOnDA are:
- OpenJDK 11, maven build tool, and git
  ```
  sudo apt install openjdk-11-jdk maven git
  ```

- get HFC and compile it
  ```
  git clone https://github.com/bkiefer/hfc.git
  cd hfc
  mvn install
  ```
