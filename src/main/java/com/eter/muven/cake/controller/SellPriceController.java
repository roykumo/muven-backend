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

import com.eter.cake.persistence.entity.Product;
import com.eter.cake.persistence.entity.SellPrice;
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.service.InventoryDaoService;
import com.eter.cake.persistence.service.SellPriceDaoService;
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
@RequestMapping("/sellPrice")
public class SellPriceController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(SellPriceController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Autowired(required = true)
	private SellPriceDaoService sellPriceService;

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
        	SellPrice sellPrice = sellPriceService.getById(id);
        	
        	CommonResponse<SellPrice> response = commonResponseGenerator.generateCommonResponse(sellPrice);
        	
        	logger.debug("response getById: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }

	@RequestMapping(method=RequestMethod.POST, value=ADD)
	@ResponseBody
    public String add(@RequestBody SellPrice sellPrice) throws JsonProcessingException, UserException{
		try {
			logger.debug("add: {}", JsonUtil.generateJson(sellPrice));
        	sellPrice.setId(null);
        	if(sellPrice.getProduct()!=null){
        		Product product = productDaoService.getById(sellPrice.getProduct().getId());
        		
        		if(product==null){
        			throw new UserException("06", "Product not found");
        		}
        		
        		sellPrice.setProduct(product);
        	}
        	
        	sellPriceService.save(sellPrice);
        	
    		CommonResponse<SellPrice> response = commonResponseGenerator.generateCommonResponse(sellPrice);
        	
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
    public String addBulk(@RequestBody List<SellPrice> sellPrices)  throws JsonProcessingException, UserException {
		try {
        	return null;
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }
	
	@RequestMapping(method = RequestMethod.POST, value = UPDATE)
	@ResponseBody
    public String update( @RequestBody SellPrice sellPrice) throws JsonProcessingException, UserException {
		try {
			logger.debug("update: {}", JsonUtil.generateJson(sellPrice));
			
			SellPrice checkSellPrice = sellPriceService.getById(sellPrice.getId());
			if(checkSellPrice==null){
				throw new UserException("06", "SellPrice not found");
			}else{
				checkSellPrice.setProduct(productDaoService.getById(sellPrice.getId()));
				checkSellPrice.setDate(sellPrice.getDate());
				checkSellPrice.setBuyPrice(sellPrice.getBuyPrice());
				checkSellPrice.setSellingPrice(sellPrice.getSellingPrice());
				checkSellPrice.setRemarks(sellPrice.getRemarks());
				checkSellPrice.setSale(sellPrice.getSale());
				
				sellPriceService.save(checkSellPrice);
			}
			
    		CommonResponse<SellPrice> response = commonResponseGenerator.generateCommonResponse(sellPrice);
        	
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
        	SellPrice sellPrice = sellPriceService.getById(id);
        	
        	if(sellPrice==null){
        		throw new UserException("06", "SellPrice not found");
        	}
        	
        	CommonResponse<SellPrice> response = commonResponseGenerator.generateCommonResponse(sellPrice);
        	
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
                                    @RequestParam(name = "sortDir", defaultValue = Constant.ORDER_DESC) String sortDir,
                                    @RequestParam(name = "sort", defaultValue = "date") String sort,
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

        CommonPaging<SellPrice> userPage = sellPriceService.getPaging(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<SellPrice> resp = new CommonResponsePaging<SellPrice>(userPage);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }

	@GetMapping(path = "/product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String getLatestByProduct(@PathVariable("id") String id) throws JsonProcessingException, UserException{
    	try {
    		logger.debug("getLatestByProduct: {}", id);
    		
    		Product product = productDaoService.getById(id);
    		if(product==null){
    			throw new UserException("06", "product not found");
    		}
    		
        	SellPrice sellPrice = sellPriceService.getLatestPriceByProduct(product);
        	
        	CommonResponse<SellPrice> response = commonResponseGenerator.generateCommonResponse(sellPrice);
        	
        	logger.debug("response getLatestByProduct: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (UserException e) {
    		throw e;
        } catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }

}
