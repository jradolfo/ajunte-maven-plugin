package com.github.jradolfo.ajunte;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.github.jradolfo.ajunte.common.GeneralConfig;
import com.github.jradolfo.ajunte.processor.TagProcessor;
import com.github.jradolfo.ajunte.util.HashGenerator;
import com.github.jradolfo.ajunte.util.PathNormalizator;
import com.github.jradolfo.ajunte.util.ResourceAccess;

public abstract class RegexBasedTagProcessor extends TagProcessor {

	/**
	 * Placeholder used in the filename to indicate that the hash of the content should be calculated
	 * and placed in the filename.
	 */
	public static final String HASH_PLACEHOLDER = "#hash#";
    private static final Charset CHARSET = StandardCharsets.UTF_8;    
    private static final String MINIFIED_KEYWORD = ".min.";
    
    protected PathNormalizator pathNormalizator = new PathNormalizator();
    
    /**
     * Construct tag which will be outputted as a result of bundle
     *
     * @param fileName output fileName
     * @return output tag
     */
    protected abstract String createBundledTag(String fileName);
    
    /**
     * Regex that represents inner tags that will be processed and bundled.
     * It MUST return first capturing group which represents partial file path to read
     *
     * @return inner tag regex
     */
    protected abstract String tagRegex();

    
    public RegexBasedTagProcessor(GeneralConfig config, Log log, ResourceAccess resourceAccess) {
		super(config, log, resourceAccess);	
	}

	@Override
    public String process(Tag tag) {
        log.info("----------------------------------------");
        
        //Filename the minified file should have 
        String fileName = extractFileName(tag);
        
        log.info("Processing bundling tag: " + fileName);
        
        //The path of the file owner of the tag you're processing now
        Path parentSrcPath = config.getInputFilePath().getAbsoluteFile().toPath().getParent();
        
        String tagContent = tag.getContent();

        log.debug("FileName=" + fileName);
        log.debug("ParentSrcPath=" + parentSrcPath);
        log.debug("TagContent=\n" + tagContent.trim());

        try {
        	String optimizedContent = processTagContent(fileName, parentSrcPath, tagContent);
            
            fileName = verifyAndReplaceHashPlaceholder(fileName, optimizedContent);

            Path tagDestPath = pathNormalizator.getAbsolutResourcePath(fileName, 
            														   config.getOutputFilePath().getAbsoluteFile().toPath().getParent(), 
            														   config.getWebappTargetDir().getAbsoluteFile().toPath());
            
            log.info("Writing to file: " + tagDestPath);
            
            resourceAccess.write(tagDestPath, optimizedContent);
            
            String bundledTag = createBundledTag(fileName);
            
            log.info("Done!");
            
            return bundledTag;
            
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        } finally {
            log.info("----------------------------------------");
        }
    }

	private String processTagContent(String fileName, Path parentSrcPath, String tagContent) {
		
		List<TagSource> tagSources = processTags(fileName, parentSrcPath, tagContent);
		
		log.info("Optimizing...");

		StringBuilder outputBuilder = new StringBuilder();
		int totalLengthBeforeCompress = 0;
		int totalLengthAfterCompress = 0;
		
		for (TagSource tagSource : tagSources) {
		    String srcContent = tagSource.getSrcContent();
		    
		    int fileLengthBeforeCompress = srcContent.getBytes(CHARSET).length;
		    int fileLengthAfterCompress = 0;
		    
		    totalLengthBeforeCompress += fileLengthBeforeCompress;
		    
		    try {
		        // If the filename indicates that the content has been minified, we don't need to optimize it again.
		        String processedContent;
		        
		        if (tagSource.getSrcPath().getFileName().toString().contains(MINIFIED_KEYWORD)) {
		        	log("Skip optimizing %s because it's already been minified.", tagSource.getSrcPath());
		            processedContent = srcContent;
		        } else {		        	
		            processedContent = postProcessOutputFileContent(srcContent);
		            
		            fileLengthAfterCompress = processedContent != null ? processedContent.getBytes(CHARSET).length : 0;

		            double fileCompressionRatio = fileLengthAfterCompress != 0 ? 1 - (double) fileLengthAfterCompress / fileLengthBeforeCompress : 0;
		            log("%s. CompressionRate: %d%%", tagSource.getSrcPath(), (int) (fileCompressionRatio * 100));
		        }
		        
		        outputBuilder.append(processedContent).append("\n");
		        		        		        
		        totalLengthAfterCompress += fileLengthAfterCompress;
		        
		    } catch (Exception ex) {
		        log.error("Failed to optimize data. Use it directly. File=" + tagSource.getSrcPath(), ex);
		    }
		    
		}
		
		double compressionRatio = totalLengthAfterCompress != 0 ? 1 - (double) totalLengthAfterCompress / totalLengthBeforeCompress : 0;
		log.info(String.format("Total optmization: %d -> %d bytes. CompressionRatio: %d%%", totalLengthBeforeCompress, totalLengthAfterCompress, (int) (compressionRatio * 100)));
		
		return outputBuilder.toString();
	}

	
    /**
     * Verifies if the filename contains the #{@link RegexBasedTagProcessor#HASH_PLACEHOLDER} and if so, calculates the
     * hash and replaces it the filename's placeholder.
     * @param fileName
     * @param content
     * @return The filename with the placeholder replaced or the untouched filename if no placeholder found
     */
	private String verifyAndReplaceHashPlaceholder(String fileName, String content) {
		
		if (fileName.contains(HASH_PLACEHOLDER)) {
		    String hashValue = HashGenerator.computeHash(content, config.getHashingAlgorithm());
		    fileName = fileName.replace(HASH_PLACEHOLDER, hashValue);
		}
		
		return fileName;
	}

    
    /**
     * Template method allowing enhance output file content
     *
     * @param content output file content
     * @return enhanced output file content
     */
    protected String postProcessOutputFileContent(String content) {
        return content;
    }

    private String extractFileName(Tag tag) {
        String[] attributes = tag.getAttributes();
        String fileName = attributes == null || attributes.length == 0 ? null : attributes[0];

        if (fileName == null) {
            throw new IllegalArgumentException("File Name attribute is required");
        }

        return fileName;
    }

    private List<TagSource> processTags(String fileName, Path parentSrcPath, String tagContent) {
        Pattern tagPattern = Pattern.compile(tagRegex(), Pattern.DOTALL);
        Matcher m = tagPattern.matcher(tagContent);
        List<TagSource> tagSources = new ArrayList<>();
        
        while (m.find()) {            
        	String src = m.group(1);            
        	Path tagSrcPath = pathNormalizator.getAbsolutResourcePath(src, parentSrcPath, config.getWebappSourceDir().getAbsoluteFile().toPath());            
            String srcContent = resourceAccess.read(tagSrcPath);
            srcContent = preprocessTagContent(fileName, srcContent, src);
            
           log("Loading %s. Length=%d", tagSrcPath, srcContent.getBytes(CHARSET).length);            
        
            tagSources.add(new TagSource(tagSrcPath, srcContent));
        }
        
        return tagSources;
    }

    protected String preprocessTagContent(String fileName, String srcContent, String src) {
        return srcContent;
    }
        
    protected void log(String text, Object...args) {
    	 if (config.isVerbose()) {
             log.info(String.format(text, args));
         }
    }
    
    private static class TagSource {

        private Path srcPath;
        private String srcContent;

        public TagSource(Path srcPath, String srcContent) {
            this.srcPath = srcPath;
            this.srcContent = srcContent;
        }

        public Path getSrcPath() {
            return srcPath;
        }
       
        public String getSrcContent() {
            return srcContent;
        }

    }
    
}
