package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.HomeModel;

@Service
public class HomeService {
  @Autowired
	@Qualifier("jdbcproxmox")
	JdbcTemplate myProxmox;
 
  @Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;

  /**
   * GET THE AMOUNT OF A GROUP
   * @param groupId
   * @return
   */
	public Map<String, Object> getHome(Integer groupId) {
		return HomeModel.getHome(myAccounting, groupId);
	}

  /**
   * GET HOME n OF MACHINES
   * @param groupId
   * @return
  */
  public Map<String, Object> getNMachines(String groupId) {
    return HomeModel.getNMachines(myAccounting, myProxmox, groupId);
  }

  public void provaCron() {
    HomeModel.provaCron(myAccounting, myProxmox);
  }
}
