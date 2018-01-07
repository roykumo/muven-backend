package com.eter.muven.cake.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eter.cake.persistence.entity.Inventory;
import com.eter.cake.persistence.entity.InventoryItem;
import com.eter.cake.persistence.entity.Product;
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.service.InventoryDaoService;
import com.eter.cake.persistence.service.InventoryItemDaoService;
import com.eter.cake.persistence.service.ProductDaoService;
import com.eter.muven.cake.Constant;
import com.eter.response.CommonResponseGenerator;
import com.eter.response.entity.CommonPaging;
import com.eter.response.entity.CommonResponse;
import com.eter.response.entity.CommonResponsePaging;
import com.eter.response.exception.UserException;
import com.eter.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RestController
@RequestMapping("/inventoryItem")
public class InventoryItemController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(InventoryItemController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Autowired(required = true)
	private InventoryItemDaoService inventoryItemService;

	@Autowired(required = true)
	private InventoryDaoService inventoryService;

	@Autowired(required = true)
	private ProductDaoService productDaoService;;
	
	@Autowired
	private CommonResponseGenerator commonResponseGenerator;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String get(@PathVariable("id") String id) throws JsonProcessingException, UserException{
    	try {
    		logger.debug("getById: {}", id);
        	InventoryItem inventoryItem = inventoryItemService.getById(id);
        	
        	CommonResponse<InventoryItem> response = commonResponseGenerator.generateCommonResponse(inventoryItem);
        	
        	logger.debug("response getById: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }

	@RequestMapping(method=RequestMethod.POST, value=ADD)
	@ResponseBody
    public String add(@RequestBody InventoryItem inventoryItem) throws JsonProcessingException, UserException{
		try {
			logger.debug("add: {}", JsonUtil.generateJson(inventoryItem));
        	inventoryItem.setId(null);
        	if(inventoryItem.getInventory()!=null){
        		Inventory inventory = inventoryService.getById(inventoryItem.getInventory().getId());
        		
        		if(inventory==null){
        			throw new UserException("06", "Inventory not found");
        		}
        		
        		inventoryItem.setInventory(inventory);
        	}
        	if(inventoryItem.getProduct()!=null){
        		
    			Product product = productDaoService.getById(inventoryItem.getProduct().getId());
        		
        		if(product==null){
        			throw new UserException("06", "Product not found");
        		}
        		
        		inventoryItem.setProduct(product);
        	}
        	
        	inventoryItemService.save(inventoryItem);
        	
    		CommonResponse<InventoryItem> response = commonResponseGenerator.generateCommonResponse(inventoryItem);
        	
        	logger.debug("response add: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (UserException e) {
    		throw e;
        } catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }
	
	@RequestMapping(method=RequestMethod.POST, value=ADD_BULK)
	@ResponseBody
    public String addBulk(@RequestBody List<InventoryItem> inventoryItems) {
		try {
        	return null;
    	} catch (Exception e) {
            return null;
        }
    }
	
	@RequestMapping(method = RequestMethod.POST, value = UPDATE)
	@ResponseBody
    public String update( @RequestBody InventoryItem inventoryItem) throws JsonProcessingException, UserException {
		try {
			logger.debug("update: {}", JsonUtil.generateJson(inventoryItem));
			
			InventoryItem checkInventoryItem = inventoryItemService.getById(inventoryItem.getId());
			if(checkInventoryItem==null){
				throw new UserException("06", "InventoryItem not found");
			}else{
				checkInventoryItem.getInventory().setInvoice(inventoryItem.getInventory().getInvoice());

				checkInventoryItem.setProduct(productDaoService.getById(inventoryItem.getProduct().getId()));
				checkInventoryItem.setExpiredDate(inventoryItem.getExpiredDate());
				checkInventoryItem.setQuantity(inventoryItem.getQuantity());
				checkInventoryItem.setPurchasePrice(inventoryItem.getPurchasePrice());
				checkInventoryItem.setRemarks(inventoryItem.getRemarks());
				
				inventoryItemService.save(checkInventoryItem);
			}
			
    		CommonResponse<InventoryItem> response = commonResponseGenerator.generateCommonResponse(inventoryItem);
        	
        	logger.debug("response update: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (UserException e) {
    		throw e;
        } catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }
	
	@GetMapping(path = DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String delete(@PathVariable("id") String id) throws UserException {
		try {
    		logger.debug("delete: {}", id);
        	InventoryItem inventoryItem = inventoryItemService.getById(id);
        	
        	if(inventoryItem==null){
        		throw new UserException("06", "InventoryItem not found");
        	}
        	
        	CommonResponse<InventoryItem> response = commonResponseGenerator.generateCommonResponse(inventoryItem);
        	
        	logger.debug("response delete: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (UserException e) {
    		throw e;
        } catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }
	
	@RequestMapping(method = RequestMethod.GET, value = GET_LIST_FILTER_PAGING)
	@ResponseBody
    public String getPaging( @RequestParam(name = "pageSize", defaultValue = "100000") String pageSize,
                                    @RequestParam(name = "pageNo", defaultValue = "0") String page,
                                    @RequestParam(name = "sortDir", defaultValue = Constant.ORDER_ASC) String sortDir,
                                    @RequestParam(name = "sort", defaultValue = InventoryItem.Constant.CREATED_DATE_FIELD) String sort,
                                    @RequestParam(name = "field", defaultValue = "") String field)
            throws Exception {

		List<KeyValue> filter;
		if(!StringUtils.isEmpty(field)){
			ObjectMapper mapper = new ObjectMapper();
			filter = mapper.readValue(field, new TypeReference<List<KeyValue>>(){});
			
			logger.debug("filter: {} - {}", filter.size(), JsonUtil.generateJson(filter));
		}else{
			filter = new ArrayList<>();
		}

        CommonPaging<InventoryItem> userPage = inventoryItemService.getPaging(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<InventoryItem> resp = new CommonResponsePaging<InventoryItem>(userPage);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }
	
}
