package com.eter.muven.cake.controller;

import com.eter.cake.persistence.entity.InventoryOut;
import com.eter.cake.persistence.entity.InventoryItemOut;
import com.eter.cake.persistence.entity.Product;
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.service.InventoryItemOutDaoService;
import com.eter.cake.persistence.service.InventoryOutDaoService;
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
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/inventoryItem/out")
public class InventoryItemOutController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(InventoryItemOutController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Autowired(required = true)
	private InventoryItemOutDaoService inventoryItemOutDaoService;

	@Autowired(required = true)
	private InventoryOutDaoService inventoryOutDaoService;

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
			InventoryItemOut inventoryItem = inventoryItemOutDaoService.getById(id);
        	
        	CommonResponse<InventoryItemOut> response = commonResponseGenerator.generateCommonResponse(inventoryItem);
        	
        	logger.debug("response getById: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }

	@RequestMapping(method=RequestMethod.POST, value=ADD)
	@ResponseBody
    public String add(@RequestBody InventoryItemOut inventoryItemOut) throws JsonProcessingException, UserException{
		try {
			logger.debug("add: {}", JsonUtil.generateJson(inventoryItemOut));
        	inventoryItemOut.setId(null);
        	if(inventoryItemOut.getInventoryOut()!=null){
        		InventoryOut inventoryOut = inventoryOutDaoService.getById(inventoryItemOut.getInventoryOut().getId());
        		
        		if(inventoryOut==null){
        			throw new UserException("06", "InventoryOut not found");
        		}
        		
        		inventoryItemOut.setInventoryOut(inventoryOut);
        	}
        	if(inventoryItemOut.getProduct()!=null){
        		
    			Product product = productDaoService.getById(inventoryItemOut.getProduct().getId());
        		
        		if(product==null){
        			throw new UserException("06", "Product not found");
        		}
        		
        		inventoryItemOut.setProduct(product);
        	}
        	
        	inventoryItemOutDaoService.save(inventoryItemOut);
        	
    		CommonResponse<InventoryItemOut> response = commonResponseGenerator.generateCommonResponse(inventoryItemOut);
        	
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
    public String addBulk(@RequestBody List<InventoryItemOut> inventoryOutItems) {
		try {
        	return null;
    	} catch (Exception e) {
            return null;
        }
    }
	
	@RequestMapping(method = RequestMethod.POST, value = UPDATE)
	@ResponseBody
    public String update( @RequestBody InventoryItemOut inventoryItemOut) throws JsonProcessingException, UserException {
		try {
			logger.debug("update: {}", JsonUtil.generateJson(inventoryItemOut));

			InventoryItemOut checkInventoryItem = inventoryItemOutDaoService.getById(inventoryItemOut.getId());
			if(checkInventoryItem==null){
				throw new UserException("06", "InventoryItemOut not found");
			}else{
				checkInventoryItem.setProduct(productDaoService.getById(inventoryItemOut.getProduct().getId()));
				checkInventoryItem.setQuantity(inventoryItemOut.getQuantity());
				checkInventoryItem.setPurchasePrice(inventoryItemOut.getPurchasePrice());
				
				inventoryItemOutDaoService.save(checkInventoryItem);
			}
			
    		CommonResponse<InventoryItemOut> response = commonResponseGenerator.generateCommonResponse(inventoryItemOut);
        	
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
			InventoryItemOut inventoryItemOut = inventoryItemOutDaoService.getById(id);

        	if(inventoryItemOut==null){
        		throw new UserException("06", "InventoryItem not found");
        	}
        	
        	CommonResponse<InventoryItemOut> response = commonResponseGenerator.generateCommonResponse(inventoryItemOut);
        	
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
                                    @RequestParam(name = "sort", defaultValue = InventoryItemOut.Constant.CREATED_DATE_FIELD) String sort,
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

        CommonPaging<InventoryItemOut> userPage = inventoryItemOutDaoService.getPaging(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<InventoryItemOut> resp = new CommonResponsePaging<InventoryItemOut>(userPage);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }
	
}
