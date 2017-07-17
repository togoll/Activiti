package org.activiti.client.model.resources;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class ProcessModelResource extends Resource<String> {

    public ProcessModelResource(String content,
                                Link... links) {
        super(content,
              links);
    }
}
