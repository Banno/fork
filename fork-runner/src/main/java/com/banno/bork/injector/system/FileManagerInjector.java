package com.banno.bork.injector.system;

import com.banno.bork.system.io.FileManager;

import static com.banno.bork.injector.ConfigurationInjector.configuration;

public class FileManagerInjector {

    private FileManagerInjector() {}

    public static FileManager fileManager() {
        return new FileManager(configuration().getOutput());
    }
}
