#!/usr/bin/env python3

import subprocess

subprocess.check_call(["flutter", "pub", "run", "pigeon",
  "--input", "pigeons/banuba_sdk.dart",
  "--dart_out", "lib/banuba_sdk_plugin.dart",
  "--experimental_swift_out", "ios/Classes/BanubaSdkPlugin.swift",
  "--java_package", "com.banuba.sdk.flutter",
  "--java_out", "./android/src/main/java/com/banuba/sdk/flutter/BanubaSdkPluginGen.java",
  "--java_package", "com.banuba.sdk.flutter"])
