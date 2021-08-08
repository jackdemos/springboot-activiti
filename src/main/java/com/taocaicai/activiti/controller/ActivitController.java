package com.taocaicai.activiti.controller;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @project taocaicai
 * @author Oakley
 * @created 2021-08-08 08:03:8:03
 * @package com.taocaicai.activiti.controller
 * @description TODO
 * @version: 0.0.0.1
 */
@Controller
public class ActivitController {

  @Autowired
  private ProcessRuntime processRuntime;


  @PostMapping("/documents")
  @ResponseBody
  public String processFile(@RequestBody String content) {

    ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
            .start()
            .withProcessDefinitionKey("evection")
            .withVariable("fileContent",
                    content)
            .build());
    String message = ">>> Created Process Instance: " + processInstance;
    System.out.println(message);
    return message;
  }

  @GetMapping("/process-definitions")
  @ResponseBody
  public List<ProcessDefinition> getProcessDefinition(){
    return processRuntime.processDefinitions(Pageable.of(0, 100)).getContent();
  }


}
