package com.github.ajunte.bundler;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.github.jradolfo.ajunte.util.PathNormalizator;

@RunWith(Parameterized.class)
public class RelativizeResourcesTest {

    private static final Object[][] CONFIG = new Object[][]{
            {"#{request.contextPath}/resources/css/app.css", "#{request.contextPath}/resources/css/app.min.css", "../images/image.png", "src/main/webapp/", "target/finalName/", "../images/image.png"},
            {"resources/css/app.css", "resources/css/app.min.css", "../images/image.png", "src/main/webapp/", "target/finalName/", "../images/image.png"},
            {"resources/css/lib/app.css", "resources/css/app.min.css", "../../images/image.png", "src/main/webapp/", "target/finalName/", "../images/image.png"}
    };

    @Parameterized.Parameters(name = "{index} {0} {1} {2} {3} {5} {6}")
    public static Collection<Object[]> data() {
        return asList(CONFIG);
    }

    String sourceCssPath;
    String targetCssPath;    
    String resourcePath;
    String webappSourceDir;
    String webappTargetDir;
    String expectedRelativeResourcePath;
        
    public RelativizeResourcesTest(String sourceCssPath, String targetCssPath, String resourcePath, String webappSourceDir, String webappTargetDir, String expectedRelativeResourcePath) {
		this.sourceCssPath = sourceCssPath;
		this.targetCssPath = targetCssPath;
		this.resourcePath = resourcePath;
		this.webappSourceDir = webappSourceDir;
		this.webappTargetDir = webappTargetDir;
		this.expectedRelativeResourcePath = expectedRelativeResourcePath;
	}


    private PathNormalizator pathNormalizator = new PathNormalizator();

    @Test
    public void test() throws Exception {
    	
    	String resultado = pathNormalizator.relativizeResourcePath(targetCssPath, sourceCssPath, resourcePath, absolute(webappSourceDir), absolute(webappTargetDir));    	
        assertThat(resultado).isEqualTo(expectedRelativeResourcePath);
    }

    private File absolute(String relativePath) {
        return FileSystems.getDefault().getPath(relativePath).toAbsolutePath().toFile();
    }

}