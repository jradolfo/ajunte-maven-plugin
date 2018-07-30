package com.github.jradolfo.ajunte.processor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.maven.plugin.logging.Log;

import com.github.jradolfo.ajunte.common.GeneralConfig;
import com.github.jradolfo.ajunte.util.ResourceAccess;
import com.yahoo.platform.yui.compressor.CssCompressor;

public class YuiCssTagProcessor extends CssTagProcessor{
    
	public YuiCssTagProcessor(GeneralConfig config, Log log, ResourceAccess resourceAccess) {
		super(config, log, resourceAccess);
		this.config = config;
	}
	
	@Override
    protected String postProcessOutputFileContent(String content) {
		
		if (content.isEmpty()) {
            return content;
        }
        
		try {
            StringWriter out = new StringWriter();
            
            CssCompressor compressor = new CssCompressor(new StringReader(content));            
            compressor.compress(out, -1);
            
            return out.toString();
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		
    }
    
}
