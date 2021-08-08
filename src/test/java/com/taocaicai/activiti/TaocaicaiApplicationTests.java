package com.taocaicai.activiti;

import com.taocaicai.activiti.utils.SecurityUtil;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.ClaimTaskPayloadBuilder;
import org.activiti.api.task.model.builders.CompleteTaskPayloadBuilder;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.model.builders.UpdateTaskPayloadBuilder;
import org.activiti.api.task.model.payloads.CompleteTaskPayload;
import org.activiti.api.task.model.payloads.UpdateTaskPayload;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TaocaicaiApplicationTests {

  @Autowired RepositoryService repositoryService;
  @Autowired RuntimeService runtimeService;
  @Autowired ProcessRuntime processRuntime;
  @Autowired SecurityUtil securityUtil;
  @Autowired TaskRuntime taskRuntime;

  @Test
  void contextLoads() {
    System.out.println("test Spring boot init");
    System.out.println(runtimeService);
  }

  /** 查询流程定义的数量 */
  @Test
  void getProcessDefinition() {
    securityUtil.logInAs("salaboy");
    Page<ProcessDefinition> processDefinitionPage =
        processRuntime.processDefinitions(Pageable.of(0, 10));
    System.out.println("可用流程定义数量: " + processDefinitionPage.getTotalItems());
    List<ProcessDefinition> content = processDefinitionPage.getContent();
    for (ProcessDefinition processDefinition : content) {
      System.out.println(
          processDefinition.getId()
              + "\t"
              + processDefinition.getId()
              + "\t"
              + processDefinition.getName());
    }
  }

  /** 部署流程 */
  @Test
  public void deployment() {
    Deployment deploy =
        repositoryService
            .createDeployment()
            .addClasspathResource("processes/my-evection.bpmn")
            .addClasspathResource("processes/my-evection.png")
            .deploy();
    System.out.println("部署ID: " + deploy.getId());
    System.out.println("部署名称: " + deploy.getName());
  }

  /** 启动一个流程实例 */
  @Test
  public void startProcessInstance() {
    securityUtil.logInAs("salaboy");
    ProcessInstance processInstance =
        processRuntime.start(
            ProcessPayloadBuilder.start().withProcessDefinitionKey("evection").build());
    System.out.println("流程实例ID: " + processInstance.getId());
    System.out.println("流程实例名称: " + processInstance.getName());
  }

  /** 任务查询及任务拾取及完成操作 */
  @Test
  public void complete() throws InterruptedException {
    securityUtil.logInAs("jack");
    Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
    int totalItems = tasks.getTotalItems();
    if (totalItems == 0) {
      System.out.println("未查询任务");
      return;
    }
    List<Task> taskList = tasks.getContent();
    for (Task task : taskList) {
      /** 拾取任务 */
      taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
      System.out.println("拾取任务:" + tasks.getContent());
      /** 归还任务 */
      // taskRuntime.release(TaskPayloadBuilder.release().withTaskId(task.getId()).build());
      // System.out.println("归还任务:" + tasks.getContent());
      /** 完成任务 */
      taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
    }
    Page<Task> results = taskRuntime.tasks(Pageable.of(0, 10));
    if (results.getTotalItems() > 0) {
      System.out.println("完成任务:" + results.getContent());
    }
  }
}
