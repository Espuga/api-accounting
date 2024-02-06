package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.accounting.accounting.model.InvoiceModel;
import com.accounting.accounting.model.PricesModel;
import com.accounting.accounting.model.VmachinesModel;

@Service
public class VmachinesService {
    @Autowired
	@Qualifier("jdbcproxmox")
	JdbcTemplate myProxmox;

    @Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;

    public Map<String, Object> getSelectors() {
        return VmachinesModel.getSelectors(myAccounting);
    }

    public Map<String, Object> getDataTable(String groupId, String start, String end) {
        return VmachinesModel.getDataTable(myAccounting, myProxmox, groupId, start, end);
    }

    public Map<String, Object> getGroupsDataTable(String groupsId, String start, String end) {
        return VmachinesModel.getGroupsDataTable(myAccounting, myProxmox, groupsId, start, end);
    }

    public boolean savePrices(PricesModel pricesModel) {
        return VmachinesModel.savePrices(myAccounting, pricesModel);
    }

    public boolean doInvoice(InvoiceModel invoiceModel) {
        return VmachinesModel.doInvoice(myAccounting, invoiceModel);
    }
    
    public Map<String, Object> getNMachines(String groupId) {
    	return VmachinesModel.getNMachines(myAccounting, myProxmox, groupId);
    }
}
