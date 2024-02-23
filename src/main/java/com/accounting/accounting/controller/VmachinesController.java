package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.InvoiceModel;
import com.accounting.accounting.model.PricesModel;
import com.accounting.accounting.service.VmachinesService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/vmachines")
public class VmachinesController {
  @Autowired
  VmachinesService vmachinesService;

  /**
   * GET VMACHINES SELECTORS
   * @return
   */
  @GetMapping("/getSelectors")
  public Map<String, Object> getSelectors() {
    return vmachinesService.getSelectors();
  }

  /**
   * GET GROUP's PROXMOX DATA
   * @param groupId
   * @param start
   * @param end
   * @return
   */
  @GetMapping("/getDataTable")
  public Map<String, Object> getDataTable(
    @RequestParam("groupId") String groupId,
    @RequestParam("start") String start, 
    @RequestParam("end") String end
    ) {
    return vmachinesService.getDataTable(groupId, start, end);
  }

  /**
   * TEACHER GET GROUPS DATA
   * @param groupsId
   * @param start
   * @param end
   * @return
   */
  @GetMapping("/getGroupsDataTable")
  public Map<String, Object> getGroupsDataTable(
    @RequestParam("groupsId") String groupsId,
    @RequestParam("start") String start, 
    @RequestParam("end") String end
  ) {
    return vmachinesService.getGroupsDataTable(groupsId, start, end);
  }

  /**
   * UPDATE PRICES
   * @param pricesModel
   * @return
   */
  @PostMapping("/savePrices")
  public boolean savePrices(@RequestBody PricesModel pricesModel){
    return vmachinesService.savePrices(pricesModel);
  }

  /**
   * DO INVOICE FOR EVERY GROUP
   * @param invoiceModel
   * @return
   */
  @PostMapping("/doInvoice")
  public boolean doInvoice(@RequestBody InvoiceModel invoiceModel) {
    return vmachinesService.doInvoice(invoiceModel);
  }
  
}
