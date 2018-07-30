package com.github.jradolfo.ajunte;

import java.io.File;
import java.util.function.Supplier;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.jradolfo.ajunte.common.GeneralConfig;
import com.github.jradolfo.ajunte.common.OpitimizerEngines;
import com.github.jradolfo.ajunte.common.YuiConfig;
import com.github.jradolfo.ajunte.processor.CssTagProcessor;
import com.github.jradolfo.ajunte.processor.JsTagProcessor;
import com.github.jradolfo.ajunte.processor.TagProcessor;
import com.github.jradolfo.ajunte.processor.YuiCssTagProcessor;
import com.github.jradolfo.ajunte.processor.YuiJsTagProcessor;
import com.github.jradolfo.ajunte.util.ResourceAccess;

/**
 * Generate package bundles.
 */
@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class AjunteMojo extends AbstractMojo {

    /**
     * Input file.
     */
    @Parameter(property = "inputFile", required = true)
    private File inputFilePath;

    /**
     * Location of the output file.
     */
    @Parameter(property = "outputFile", required = true)
    private File outputFilePath;
    
    /**
     * Location of the base of the web application. This will be used to enable the processing of JS / CSS resources that 
     * contains EL Expressions like #{request.contextPath} or #{facescontext.externalContext.request.contextPath}. Those 
     * expressions will be replaced by the webappBaseDir for processing purpose.
     * If those EL Expressions are found but no webappdir is defined an exception will be thrown.
     */
    @Parameter(property = "webappSourceDir", defaultValue = "${project.basedir}/src/main/webapp")
    private File webappSourceDir;

    /**
     * Webapp target directory.
     */
    @Parameter(property = "webappTargetDir", defaultValue = "${project.build.directory}/${project.build.finalName}")
    private File webappTargetDir;


	/**
     * Hashing Algrithm. Possible values for shipped providers: MD5, SHA-1, SHA-256.
     */
    @Parameter(property = "hashingAlgorithm", defaultValue = "MD5")
    private String hashingAlgorithm;

    /**
     * The engine used to minimize and bundle CSS files. Default is YUI Compressor.
     */
    @Parameter(property = "cssOptimizer", defaultValue = "YUI")
    private String cssOptimizer;

    /**
     * The engine used to minimize, optimize and bundle JavaScript files. Default is YUI Compressor.
     */
    @Parameter(property = "jsOptimizer", defaultValue = "YUI")
    private String jsOptimizer;    
    
    /**
     * <p>Some source control tools don't like files containing lines too long. The line-break option is used in that 
     * case to split long lines after a specific column. It can also be used to make the code more readable and easier 
     * to debug.</p> 
     * <p>Specify {@code 0} to get a line break after each semi-colon in JavaScript, and
     * after each rule in CSS. Specify {@code -1} to disallow line breaks.</p>
     */
    @Parameter(property = "lineBreak", defaultValue = "-1")
    private int lineBreak;
    
    /**
     * If the compressor should shorten local variable names when possible. Default is true.
     */                            
    @Parameter(property = "munge", defaultValue = "true")
    private boolean munge;

    /**
     * If detailed execution information should be logged.
     */
    @Parameter(property = "verbose", defaultValue = "false")
    private boolean verbose;

    /**
     * Preserve needless semicolons (such as the ones right before a '}'). 
     */ 
    @Parameter(property = "preserveAllSemiColons", defaultValue = "false")
    private boolean preserveAllSemiColons;

    /**
     * Disables all  micro optimizations done by the YUI Compressor. 
     */
    @Parameter(property = "disableOptimizations", defaultValue = "false")
    private boolean disableOptimizations;

    
    private YuiConfig fillYuiConfig() {
        return new YuiConfig(lineBreak, munge, preserveAllSemiColons, disableOptimizations);
    }
    
    private GeneralConfig fillGeneralConfig() {
    	return new GeneralConfig(inputFilePath, outputFilePath, webappSourceDir, webappTargetDir, hashingAlgorithm, verbose, cssOptimizer, jsOptimizer);
    }

    public void execute() {
    	GeneralConfig generalConfig = fillGeneralConfig();
    	    	
        Tokenizer tokenizer = new Tokenizer();        
        tokenizer.registerProcessor(createJsTagProcessor(generalConfig));
        tokenizer.registerProcessor(createCssTagProcessor(generalConfig));

        FileProcessor fileProcessor = new FileProcessor(tokenizer);
        fileProcessor.process(inputFilePath.toPath(), outputFilePath.toPath());
    }
    
    
    private TagProcessor createCssTagProcessor(final GeneralConfig generalConfig) {    	
    	return createTagProcessor(generalConfig.getCssOptimizer(), 
								  () -> new YuiCssTagProcessor(generalConfig, getLog(), new ResourceAccess()), 
								  () -> new CssTagProcessor(generalConfig, getLog(), new ResourceAccess()));
    }
    
    private TagProcessor createJsTagProcessor(final GeneralConfig generalConfig) {
    	return createTagProcessor(generalConfig.getJsOptimizer(), 
    							  () -> new YuiJsTagProcessor(fillYuiConfig(), generalConfig, getLog(), new ResourceAccess()) , 
    							  () -> new JsTagProcessor(generalConfig, getLog(), new ResourceAccess()));    	
    }
    
    private TagProcessor createTagProcessor(String opitimizer, Supplier<TagProcessor> yuiTagProcessor, Supplier<TagProcessor> defaultTagProcessor) {
    	TagProcessor tagProcessor;
    	OpitimizerEngines choosenEngine = OpitimizerEngines.valueOf(opitimizer);
    	    	
    	switch (choosenEngine) {
			case YUI:
				tagProcessor = yuiTagProcessor.get();
				break;
	
			case NONE:
				tagProcessor = defaultTagProcessor.get();
				break;
				
			default:
				throw new RuntimeException("Optimizer option not available yet!");				
		}
    	
    	return tagProcessor;
    }
    
}