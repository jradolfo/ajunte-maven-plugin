# Ajunte Maven Plugin

[![Build Status](https://travis-ci.org/jradolfo/ajunte-maven-plugin.svg?branch=master)](https://travis-ci.org/jradolfo/ajunte-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jradolfo/ajunte-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jradolfo/ajunte-maven-plugin)

Maven plugin for creating bundle packages of minified JS and CSS files.

The main ideia here is to enable developers to work with the raw versions of the CSS or JS files under the development phase but, at production phase, package a 
concateneted, minified and optmized version of those files in the final WAR.

Another feature is content's hash string at the file's final name wich puts an end on the user's need to press Ctrl+F5 on the browser to reload changes since, after 
a change on those files, the content's hash will change and as consequence the filename, leading the browser to download again the file.

An important feature is the use of EL expression like #{request.contextPath}" and #{facesContext.externalContext.request.contextPath} in the source path of the 
elements in order to address the application context path. Whenever those EL expressions are found, they're replaced by webappInputDir for processing purpose. As 
we use JSF facelets as page templates, becomes hard to maintain a relative relation between resources because we never know where the final rendered page will 
be. That's why the context path el expression becomes necessary.

# Goals

- ```process``` - analyse input html file for special comment block, create bundle resource packages and outputs html file with bundled blocks. Bundled resources are concatenated, minimized, optimized and if requested checksum is computed and used with bundled filename. (see example below)

# Configuration properties

| Property              | Description                                                  | Sample Value                                    				  |
| --------------------- | ------------------------------------------------------------ | ---------------------------------------------------------------- |
| inputFilePath         | The path of a file to be optimized                           | ${project.basedir}/src/main/resources/index.html 				  |
| outputFilePath        | The output path of the optimized file                        | ${project.build.outputDirectory}/index.html     				  |
| inputBaseDir          | The root path of application resources					   | ${project.basedir}/src/main/webapp/ 			  				  |		
| outputBaseDir 	    | The root path to output processed resources				   | ${project.build.outputDirectory}/#{projec.finalName}/resources/  |
| hashingAlgorithm      | The algorithm used to generated hash of the file content to be used in the output file name<br />Possible values: `MD5`(default), `SHA-1`, `SHA-256`, `SHA-384`, `SHA-512` | MD5 |
| verbose               | Whether to enable detailed output of the bundling process<br />Default: `false` | true |
| cssOptimizer          | The name of optimizer used to process CSS files.<br />Possible values: `simple` (default), `yui`, `none`<br />When choosing `none`, no optimization shall be performed. Contents from input files will just be concatenated and saved into the output file. | simple |
| jsOptimizer           | The name of optimizer used to process CSS files.<br />Possible values: `simple` (default), `yui`, `none`<br />When choosing `none`, no optimization shall be performed. Contents from input files will just be concatenated and saved into the output file. | simple |
| munge                 | Should be `true` if the compressor should shorten local variable names when possible.<br />Only works if `jsOptimize` is set to`YUI`.<br />Default: `true` | true |
| preserveAllSemiColons | Should be `true` if the compressor should preserve all semicolons in the code.<br />Only works if `jsOptimize` is set to`YUI`.<br />Default: `true` | true |
| disableOptimizations  | Should be `true` if the compressor should disable all micro optimizations. <br />Only works if `jsOptimize` is set to`yui`.<br />Default: `true` | true |

# Use Case

Consider a Maven project where you files are distributed as follow:

```
${project.basedir}/src/main/webapp/page1.xhtml
${project.basedir}/src/main/webapp/module/page2.xhtml
${project.basedir}/src/main/webapp/templates/template.xhtml
${project.basedir}/src/main/webapp/resources/css/stylesheet1.js
${project.basedir}/src/main/webapp/resources/css/folder/stylesheet2.js
${project.basedir}/src/main/webapp/resources/js/script1.js
${project.basedir}/src/main/webapp/resources/js/folder/script2.js
${project.basedir}/src/main/webapp/resources/images/image1.png
${project.basedir}/src/main/webapp/resources/images/folder/image2.png
```

and we want then to be outputted like this:

```
${project.build.outputDirectory}/#{projec.finalName}/page1.xhtml
${project.build.outputDirectory}/#{projec.finalName}/module/page2.xhtml
${project.build.outputDirectory}/#{projec.finalName}/templates/template.xhtml
${project.build.outputDirectory}/#{projec.finalName}/resources/css/stylesheet-4971211a240c63874c6ae8c82bd0c88c.min.css
${project.build.outputDirectory}/#{projec.finalName}/resources/js/script-0874ac8910c7b3d2e73da106ebca7329.min.js
${project.build.outputDirectory}/#{projec.finalName}/resources/images/image1.png
${project.build.outputDirectory}/#{projec.finalName}/resources/images/folder/image2.png
```

# Usage

Configure plugin:

```xml
     <properties>
	    <processed.files.dir>${project.build.directory}/my-processed-files</processed.files.dir>
     </properties>

      <plugin>
        <groupId>com.github.jradolfo</groupId>
        <artifactId>ajunte-maven-plugin</artifactId>
        <version>0.1</version>
        <executions>
          <execution>
            <id>bundle</id>
            <goals>
                  <goal>process</goal>
            </goals>
            <configuration>             
		  <inputFilePath>${project.basedir}/src/main/webapp/template/template.xhtml</inputFilePath>
		  <outputFilePath>${processed.files.dir}/template/template.xhtml</outputFilePath>
		  <webappSourceDir>${project.basedir}/src/main/webapp/</webappSourceDir>							  <webappTargetDir>${processed.files.dir}/</webappTargetDir>		
            </configuration>
          </execution>
        </executions>
      </plugin>
            
      <plugin>
	     <groupId>org.apache.maven.plugins</groupId>
	     <artifactId>maven-war-plugin</artifactId>
	     <version>3.2.2</version>
	     <configuration>
		    <webResources>
			   <resource>							
				   <directory>${processed.files.dir}</directory>
			   </resource>
		    </webResources>
	     </configuration>
      </plugin>
```

The processed html file will be outputted to ```${processed.files.dir}/templates/template.html``` and later it will be used to override the files used by war-plugin to package the application.


template.xhtml:

```html
<!DOCTYPE html>
<html lang="en">
<body>

<!-- bundle:js #{request.contextPath}/resources/js/script-#hash#.min.js-->
<script src="#{request.contextPath}/resources/js/script1.js"></script>
<script src="#{request.contextPath}/resources/js/folder/script2.js"></script>
<!-- /bundle -->

<!-- bundle:css #{request.contextPath}/resources/css/stylesheet-#hash#.min.css-->
<link href="#{request.contextPath}/resources/css/stylesheet1.css"/>
<link href="#{request.contextPath}/resources/css/folder/stylesheet2.css"/>
<!-- /bundle -->

</body>
</html>
```

stylesheet1.css

```css
.mybackground{
	background: url('../images/image1.png');
}
```

stylesheet2.css

```css
.myotherbackground{
	background: url('../../images/folder/image2.png');
}
```


After running plugin the result outputted will look like:


```html
<!DOCTYPE html>
<html lang="en">
<body>

<script type="text/javascript" src="#{request.contextPath}/resources/js/script-0874ac8910c7b3d2e73da106ebca7329.min.js"></script>
<link rel="stylesheet" href="#{request.contextPath}/resources/css/stylesheet-4971211a240c63874c6ae8c82bd0c88c.min.css" />

</body>
</html>
```

stylesheet-4971211a240c63874c6ae8c82bd0c88c.min.css

```css
.mybackground{background: url('../images/image1.png');}.myotherbackground{background: url('../images/folder/image2.png');}
```

Notice the path normalization in the image source.

# Notes

This plugin is heavily inspired on https://github.com/CH3CHO/bundler-maven-plugin, https://github.com/kospiotr/bundler-maven-plugin and https://github.com/samaxes/minify-maven-plugin.

Bundled files are automatically concatenated and minimized with http://yui.github.io/yuicompressor/.

YUI Compressor has some bugs when dealing with "data:svg+xml" values in CSS and doesn't support ES 6. You can have a try with it and see if it can work with your project.
  
