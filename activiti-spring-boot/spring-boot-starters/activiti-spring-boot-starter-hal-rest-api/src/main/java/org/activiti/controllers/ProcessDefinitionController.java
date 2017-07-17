/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.activiti.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.activiti.client.model.ProcessDefinition;
import org.activiti.client.model.ProcessModel;
import org.activiti.client.model.resources.ProcessDefinitionResource;
import org.activiti.client.model.resources.assembler.ProcessDefinitionResourceAssembler;
import org.activiti.client.model.resources.assembler.ProcessModelResourceAssembler;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.model.converter.ProcessDefinitionConverter;
import org.activiti.services.PageableRepositoryService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "process-definitions", produces = MediaTypes.HAL_JSON_VALUE)
public class ProcessDefinitionController {

    private final RepositoryService repositoryService;

    private final ProcessDefinitionConverter processDefinitionConverter;

    private final ProcessDefinitionResourceAssembler resourceAssembler;

    private final ProcessModelResourceAssembler processModelResourceAssembler;

    private final PageableRepositoryService pageableRepositoryService;

    @Autowired
    public ProcessDefinitionController(RepositoryService repositoryService,
                                       ProcessDefinitionConverter processDefinitionConverter,
                                       ProcessDefinitionResourceAssembler resourceAssembler,
                                       ProcessModelResourceAssembler processModelResourceAssembler,
                                       PageableRepositoryService pageableRepositoryService) {
        this.repositoryService = repositoryService;
        this.processDefinitionConverter = processDefinitionConverter;
        this.resourceAssembler = resourceAssembler;
        this.processModelResourceAssembler = processModelResourceAssembler;
        this.pageableRepositoryService = pageableRepositoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<ProcessDefinitionResource> getProcessDefinitions(Pageable pageable,
                                                                           PagedResourcesAssembler<ProcessDefinition> pagedResourcesAssembler) {
        Page<ProcessDefinition> page = pageableRepositoryService.getProcessDefinitions(pageable);
        return pagedResourcesAssembler.toResource(page,
                                                  resourceAssembler);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ProcessDefinitionResource getProcessDefinition(@PathVariable String id) {
        org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        if (processDefinition == null) {
            throw new ActivitiException("Unable to find process definition for the given id:'" + id + "'");
        }
        return resourceAssembler.toResource(processDefinitionConverter.from(processDefinition));
    }

    @RequestMapping(value = "/{id}/xml", method = RequestMethod.GET)
    public Resource<String> getProcessModelXml(@PathVariable String id) {
        try (final InputStream resourceStream = repositoryService.getProcessModel(id)) {
            String xml = new String(IOUtils.toByteArray(resourceStream),
                                    "UTF-8");
            ProcessModel processModel = new ProcessModel();
            processModel.setId(id);
            processModel.setXmlValue(xml);
            return processModelResourceAssembler.toResource(processModel);
        } catch (IOException e) {
            throw new ActivitiException("IOException occurred.",
                                        e);
        }
    }
}
