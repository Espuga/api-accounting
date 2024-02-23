package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.accounting.accounting.model.ProjectData;
import com.accounting.accounting.model.ProjectsModel;

@Service
public class ProjectsService {
  @Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;

  /**
   * GET GROUPS PROJECTS
   * @param id
   * @return
   */
  public Map<String, Object> getProjects(String id, String start, String end) {
    return ProjectsModel.getProjects(myAccounting, id, start, end);
  }

  /**
   * GET USERS CHART
   * @param id
   * @param start
   * @param end
   * @return
   */
  public Map<String, Object> getChart(String id, String start, String end) {
    return ProjectsModel.getChart(myAccounting, id, start, end);
  }

  /**
   * CREATE NEW PROJECT + ADD USERS HOURS
   * @param projectData
   * @return
   */
  public boolean newProject(ProjectData projectData) {
    return ProjectsModel.newProject(myAccounting, projectData);
  }

  /**
   * GET THE USER HOURS OF THE SPRINT BY PROJECTS
   * @param groupId
   * @param userId
   * @param start
   * @param end
   * @return
   */
  public Map<String, Object> getUserInfo(Integer groupId, Integer userId, String start, String end) {
    return ProjectsModel.getUserInfo(myAccounting, groupId, userId, start, end);
  }

  /**
   * GET THE PROJECT INFO
   * @param projectId
   * @return
   */
  public Map<String, Object> getProject(Integer projectId) {
    return ProjectsModel.getProject(myAccounting, projectId);
  }

  /**
   * SAVE THE PROJECTS UPDATES
   * @param projectData
   * @return
   */
  public boolean saveProject(ProjectData projectData) {
    return ProjectsModel.saveProject(myAccounting, projectData);
  }

  /**
   * DELETE THE PROJECT
   * @param id
   * @return
   */
  public boolean deleteProject(String id) {
    return ProjectsModel.deleteProject(myAccounting, id);
  }
}
