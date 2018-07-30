package com.github.jradolfo.ajunte;


import java.nio.file.Path;

import com.github.jradolfo.ajunte.util.ResourceAccess;

class FileProcessor {

    Tokenizer tokenizer;
    ResourceAccess resourceAccess = new ResourceAccess();


    public FileProcessor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void process(Path inputFilePath, Path outputFilePath) {
        String inputFileContent = resourceAccess.read(inputFilePath);
        String outputFileContent = tokenizer.process(inputFileContent);
        resourceAccess.write(outputFilePath, outputFileContent);
    }
}
