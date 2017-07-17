package org.activiti.client.model.resources.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.activiti.client.model.ProcessModel;
import org.activiti.client.model.resources.ProcessModelResource;
import org.activiti.controllers.HomeController;
import org.activiti.controllers.ProcessDefinitionController;
import org.activiti.controllers.ProcessInstanceController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ProcessModelResourceAssembler extends ResourceAssemblerSupport<ProcessModel, ProcessModelResource> {

    public ProcessModelResourceAssembler() {
        super(ProcessDefinitionController.class,
              ProcessModelResource.class);
    }

    @Override
    public ProcessModelResource toResource(ProcessModel processModel) {
        Link selfRel = linkTo(methodOn(ProcessDefinitionController.class).getProcessDefinition(processModel.getId())).withSelfRel();
        Link startProcessLink = linkTo(methodOn(ProcessInstanceController.class).startProcess(null)).withRel("startProcess");
        Link homeLink = linkTo(HomeController.class).withRel("home");
        return new ProcessModelResource(processModel.getXmlValue(),
                                        selfRel,
                                        startProcessLink,
                                        homeLink);
    }
}
