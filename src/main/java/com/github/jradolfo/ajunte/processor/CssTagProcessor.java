package com.github.jradolfo.ajunte.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.github.jradolfo.ajunte.RegexBasedTagProcessor;
import com.github.jradolfo.ajunte.common.GeneralConfig;
import com.github.jradolfo.ajunte.util.ResourceAccess;

/**
 * Usage:
 * 
 * <pre>
 * {@code
 *     <!-- build:js inline app.min.js -->
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
public class CssTagProcessor extends RegexBasedTagProcessor {

	private static final String TAG_REGEX = "\\Q<link\\E.*?href\\=\"(.*?)\".*?\\>";
	private static final String OUTPUT_FORMAT = "<link rel=\"stylesheet\" href=\"%s\" />";
    

    public CssTagProcessor(GeneralConfig config, Log log, ResourceAccess resourceAccess) {
		super(config, log, resourceAccess);
	}

    @Override
    public String getType() {
        return "css";
    }

    @Override
    public String createBundledTag(String fileName) {
        return String.format(OUTPUT_FORMAT, fileName);
    }

    @Override
    protected String tagRegex() {
        return TAG_REGEX;
    }

    @Override
    protected String preprocessTagContent(String targetCssPath, String content, String sourceCssPath) {
        StringBuilder sb = new StringBuilder();

        Pattern urlPattern = Pattern.compile("url\\(\\s*(['\"]?)\\s*(.*?)\\s*(\\1)\\s*\\)", Pattern.DOTALL);
        Matcher m = urlPattern.matcher(content);
        int previousIndex = 0;
        while (m.find(previousIndex)) {
            String quote = m.group(1);
            String resourcePath = m.group(2);
            sb.append(content.substring(previousIndex, m.start()));
            String relativizedResourcePathUrl = pathNormalizator.relativizeResourcePath(targetCssPath, sourceCssPath, resourcePath, config.getWebappSourceDir(), config.getWebappTargetDir());
            sb.append("url(").append(quote).append(relativizedResourcePathUrl).append(quote).append(")");
            previousIndex = m.end();
        }
        sb.append(content.substring(previousIndex, content.length()));
        return sb.toString();
    }

}
