package com.github.jradolfo.ajunte.common;

import java.io.File;

public class GeneralConfig {

	private File inputFilePath;

	private File outputFilePath;

	private File webappSourceDir;

	private File webappTargetDir;

	private String hashingAlgorithm;

	private boolean verbose;

	private String cssOptimizer;

	private String jsOptimizer;

	
	public GeneralConfig(File inputFilePath, File outputFilePath, File webappSourceDir, File webappTargetDir,
			String hashingAlgorithm, boolean verbose, String cssOptimizer, String jsOptimizer) {
		
		this.inputFilePath = inputFilePath;
		this.outputFilePath = outputFilePath;
		this.webappSourceDir = webappSourceDir;
		this.webappTargetDir = webappTargetDir;
		this.hashingAlgorithm = hashingAlgorithm;
		this.verbose = verbose;
		this.cssOptimizer = cssOptimizer;
		this.jsOptimizer = jsOptimizer;
	}

	public String getCssOptimizer() {
		return cssOptimizer;
	}

	public String getJsOptimizer() {
		return jsOptimizer;
	}
	
	public boolean isVerbose() {
		return verbose;
	}

	public File getInputFilePath() {
		return inputFilePath;
	}

	public File getOutputFilePath() {
		return outputFilePath;
	}

	public File getWebappSourceDir() {
		return webappSourceDir;
	}

	public File getWebappTargetDir() {
		return webappTargetDir;
	}

	public String getHashingAlgorithm() {
		return hashingAlgorithm;
	}

}
