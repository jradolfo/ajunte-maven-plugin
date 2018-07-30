package com.github.jradolfo.ajunte.processor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.github.jradolfo.ajunte.common.GeneralConfig;
import com.github.jradolfo.ajunte.common.YuiConfig;
import com.github.jradolfo.ajunte.util.ResourceAccess;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class YuiJsTagProcessor extends JsTagProcessor {

	private YuiConfig yuiConfig;
	
	public YuiJsTagProcessor(YuiConfig yuiConfig, GeneralConfig config, Log log, ResourceAccess resourceAccess) {
		super(config, log, resourceAccess);		
		this.yuiConfig = yuiConfig;
	}
	
	@Override
	protected String postProcessOutputFileContent(String content) {
	
		if (content.isEmpty()) {
			return content;
		}

		try {
			StringWriter out = new StringWriter();
			
			JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(content), new ToolErrorReporter(true));
			compressor.compress(out, -1, yuiConfig.isMunge(), 
										 config.isVerbose(), 
										 yuiConfig.isPreserveSemicolons(),
										 yuiConfig.isDisableOptimizations());
			
			return out.toString();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

}
