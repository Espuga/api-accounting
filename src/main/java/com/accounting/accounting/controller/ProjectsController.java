package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.ProjectData;
import com.accounting.accounting.service.ProjectsService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/projects")
public class ProjectsController {
  @Autowired
  ProjectsService projectsService;

  /**
   * GET GROUPS PROJECTS
   * @param id
   * @param start
   * @param end
   * @return
   */
  @GetMapping("/getProjects")
  public Map<String, Object> getProjects(
    @RequestParam String id, 
    @RequestParam String start,
    @RequestParam String end
    ){
    return projectsService.getProjects(id, start, end);
  }

  /**
   * GET USERS CHART
   * @param id
   * @param start
   * @param end
   * @return
  */
  @GetMapping("/getChart")
  public Map<String, Object> getChart(
    @RequestParam String id,
    @RequestParam String start,
    @RequestParam String end
  ) {
    return projectsService.getChart(id, start, end);
  }

  /**
   * CREATE NEW PROJECT + ADD USERS HOURS
   * @param projectData
   * @return
   */
  @PostMapping("/newProject")
  public boolean newProject(@RequestBody ProjectData projectData) {
    return projectsService.newProject(projectData);
  }

  /**
   * GET THE USER HOURS OF THE SPRINT BY PROJECTS
   * @param groupId
   * @param userId
   * @param start
   * @param end
   * @return
   */
  @GetMapping("/getUserInfo")
  public Map<String, Object> getUserInfo(
    @RequestParam Integer groupId, 
    @RequestParam Integer userId, 
    @RequestParam String start,
    @RequestParam String end
  ) {
    return projectsService.getUserInfo(groupId, userId, start, end);
  }

  /**
   * GET THE PROJECT INFO
   * @param projectId
   * @return
   */
  @GetMapping("/getProject")
  public Map<String, Object> getProject(@RequestParam Integer projectId) {
    return projectsService.getProject(projectId);
  }

  /**
   * SAVE THE PROJECTS UPDATES
   * @param projectData
   * @return
   */
  @PostMapping("/saveProject")
  public boolean saveProject(@RequestBody ProjectData projectData) {
    return projectsService.saveProject(projectData);
  }

  /**
   * DELETE THE PROJECT
   * @param id
   * @return
   */
  @DeleteMapping("/deleteProject/{id}")
  public boolean deleteProject(@PathVariable String id) {
    return projectsService.deleteProject(id);
  }
}
