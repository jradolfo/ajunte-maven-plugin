package com.github.jradolfo.ajunte;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jradolfo.ajunte.processor.TagProcessor;

public class Tokenizer {

    public static final String DEFAULT_TAG_START = "<!--";
    public static final String DEFAULT_TAG_END = "-->";
    public static final String DEFAULT_TAG_NAME = "bundle";
    public static final String DEFAULT_SEPARATOR = ":";

    final String tagStart;
    final String tagEnd;
    final String tagName;
    final String separator;
    private Map<String, TagProcessor> tagProcessors = new HashMap<>();


    public Tokenizer() {
        this.tagStart = DEFAULT_TAG_START;
        this.tagEnd = DEFAULT_TAG_END;
        this.tagName = DEFAULT_TAG_NAME;
        this.separator = DEFAULT_SEPARATOR;
    }

    public void registerProcessor(TagProcessor tagProcessor) {
        String tagType = tagProcessor.getType();
        if (this.tagProcessors.containsKey(tagType)) {
            throw new IllegalStateException("Processor for tag type: '" + tagType + "' is already registered");
        }
        this.tagProcessors.put(tagType, tagProcessor);        
    }

    public String process(String content) {
        StringBuilder sb = new StringBuilder();
        String regex = "\\Q" + tagStart + "\\E\\s*\\Q" + tagName + separator + "\\E([^ ]*)\\s*(.*?)\\Q" + tagEnd + "\\E" +
                "(.*?)" +
                "\\Q" + tagStart + "\\E\\s*\\Q/" + tagName + "\\E\\s*\\Q" + tagEnd + "\\E";
        Pattern tagPattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = tagPattern.matcher(content);
        int previousIndex = 0;
        while (m.find(previousIndex)) {
            String type = m.group(1);
            String attributeContent = m.group(2);
            String tagContent = m.group(3);
            String[] attributes = extractAttributes(attributeContent);
            Tag tag = new Tag(tagContent, type, attributes);

            sb.append(content.substring(previousIndex, m.start()));
            TagProcessor processor = tagProcessors.get(tag.getType());
            if (processor == null) {
                throw new IllegalArgumentException("Tag type: " + tag.getType() + " is not supported");
            }

            sb.append(processor.process(tag));
            previousIndex = m.end();
        }
        sb.append(content.substring(previousIndex, content.length()));
        return sb.toString();
    }

    private String[] extractAttributes(String attributeContent) {
        return attributeContent == null || "".equals(attributeContent) ?
                new String[]{} :
                attributeContent.split("\\s+");
    }

}
