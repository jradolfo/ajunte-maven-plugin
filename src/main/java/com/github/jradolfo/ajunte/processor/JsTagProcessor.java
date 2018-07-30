package com.github.jradolfo.ajunte.processor;

import org.apache.maven.plugin.logging.Log;

import com.github.jradolfo.ajunte.RegexBasedTagProcessor;
import com.github.jradolfo.ajunte.common.GeneralConfig;
import com.github.jradolfo.ajunte.util.ResourceAccess;

/**
 * Usage:
 * 
 * <pre>
 * {@code
 *     <!-- build:js  app.min.js -->
 *     <script src="my/lib/path/lib.js"></script>
 *     <script src="my/deep/development/path/script.js"></script>
 *     <!-- /build -->
 *
 *     <!-- changed to -->
 *     <script>
 *     // app.min.js code here
 *     </script>
 * }
 * </pre>
 */
public class JsTagProcessor extends RegexBasedTagProcessor {
	
	private static final String TAG_REGEX = "\\Q<script\\E.*?src\\=\"(.*?)\".*?\\>.*?\\Q</script>\\E";
	private static final String OUTPUT_FORMAT = "<script type=\"text/javascript\" src=\"%s\"></script>";
	
    public JsTagProcessor(GeneralConfig config, Log log, ResourceAccess resourceAccess) {
		super(config, log, resourceAccess);
	}
	
    @Override
    public String getType() {
        return "js";
    }

    @Override
    public String createBundledTag(String fileName) {
        return String.format(OUTPUT_FORMAT, fileName);
    }

    @Override
    protected String tagRegex() {
        return TAG_REGEX;
    }

}
