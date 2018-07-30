package com.github.jradolfo.ajunte.util;

import java.io.File;
import java.nio.file.Path;

public class PathNormalizator {	
	
	private static final String REQUEST_CONTEXTPATH_EL_EXPRESSION_REGEX = "#\\{request.contextPath\\}/";
	private static final String FACES_REQUEST_CONTEXTPATH_EL_EXPRESSION_REGEX = "#\\{facesContext.externalContext.request.contextPath\\}/";
	

	public String relativizeResourcePath(String targetCssPath, String sourceCssPath, String resourcePath, File webappSourceDir, File webappTargetDir) {
        if (isUrlAbsolute(resourcePath) || resourcePath.startsWith("data:")) {
            return resourcePath;
        }

        String queryString = "";
        int queryStartIndex = resourcePath.indexOf('?');
        if (queryStartIndex != -1) {
            queryString = resourcePath.substring(queryStartIndex);
            resourcePath = resourcePath.substring(0, queryStartIndex);
        }
        
        //Calculates the source path
        Path absoluteSourceCssPath = resolveContextPath(sourceCssPath, webappSourceDir.getAbsoluteFile().toPath());        
        Path absoluteSourceResourcePath = absoluteSourceCssPath.getParent().resolve(resourcePath).normalize();        
        Path relativeSourceResourcePath = webappSourceDir.getAbsoluteFile().toPath().relativize(absoluteSourceResourcePath);
        
        //Calculates the target path
        Path absoluteTargetCssPath = resolveContextPath(targetCssPath, webappTargetDir.getAbsoluteFile().toPath()).normalize();
        Path absoluteTargetResourcePath = webappTargetDir.getAbsoluteFile().toPath().resolve(relativeSourceResourcePath);                               
        Path relativeTargetResourcePath = absoluteTargetCssPath.getParent().relativize(absoluteTargetResourcePath);
        
        return relativeTargetResourcePath.normalize() + queryString; 
    }

    public Path getAbsolutResourcePath(String srcPath, Path parentSrcPath, Path webappSrcDir) {
    	String src = srcPath.replaceAll(REQUEST_CONTEXTPATH_EL_EXPRESSION_REGEX, "").replaceAll(FACES_REQUEST_CONTEXTPATH_EL_EXPRESSION_REGEX, "");    	
    	return (src.equals(srcPath)) ? parentSrcPath.resolve(srcPath) : webappSrcDir.resolve(src);
    }
	
    private Path resolveContextPath(String srcPath, Path webappBaseDir) {
    	return getAbsolutResourcePath(srcPath, webappBaseDir, webappBaseDir);
    } 
	
    private boolean isUrlAbsolute(String url) {
        return url.startsWith("/");
    }

    
}
