package com.github.jradolfo.ajunte.processor;

import org.apache.maven.plugin.logging.Log;

import com.github.jradolfo.ajunte.Tag;
import com.github.jradolfo.ajunte.common.GeneralConfig;
import com.github.jradolfo.ajunte.util.ResourceAccess;

public abstract class TagProcessor {

    protected GeneralConfig config;
    protected Log log;
    protected ResourceAccess resourceAccess;

    public abstract String getType();

    public abstract String process(Tag tag);
    
    public TagProcessor(GeneralConfig config, Log log, ResourceAccess resourceAccess){
        this.config = config;
        this.log = log;
        this.resourceAccess = resourceAccess;
    }

}
