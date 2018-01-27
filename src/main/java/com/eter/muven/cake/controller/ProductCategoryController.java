package com.eter.muven.cake.controller;

import com.eter.cake.persistence.entity.ProductCategory;
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.service.ProductCategoryDaoService;
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
@RequestMapping("/productCategory")
public class ProductCategoryController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(ProductCategoryController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Autowired(required = true)
	private ProductCategoryDaoService productCategoryService;
	
	@Autowired
	private CommonResponseGenerator commonResponseGenerator;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String get(@PathVariable("id") String id) throws JsonProcessingException, UserException{
    	try {
    		logger.debug("getById: {}", id);
        	ProductCategory productCategory = productCategoryService.getById(id);
        	
        	CommonResponse<ProductCategory> response = commonResponseGenerator.generateCommonResponse(productCategory);
        	
        	logger.debug("response getById: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }

	@RequestMapping(method=RequestMethod.POST, value=ADD)
	@ResponseBody
    public String add(@RequestBody ProductCategory productCategory) throws JsonProcessingException, UserException{
		try {
			logger.debug("add: {}", JsonUtil.generateJson(productCategory));
        	productCategory.setId(null);
        	
    		CommonResponse<ProductCategory> response = commonResponseGenerator.generateCommonResponse(productCategoryService.save(productCategory));
        	
        	logger.debug("response add: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }
	
	@RequestMapping(method=RequestMethod.POST, value=ADD_BULK)
	@ResponseBody
    public String addBulk(@RequestBody List<ProductCategory> productCategorys) {
		try {
        	return null;
    	} catch (Exception e) {
            return null;
        }
    }
	

	@RequestMapping(method = RequestMethod.POST, value = UPDATE)
	@ResponseBody
    public String update( @RequestBody ProductCategory productCategory) throws JsonProcessingException, UserException {
		try {
			logger.debug("update: {}", JsonUtil.generateJson(productCategory));
			
			ProductCategory checkProductCategory = productCategoryService.getById(productCategory.getId());
			if(checkProductCategory==null){
				throw new UserException("06", "Product Category not found");
			}else{
				checkProductCategory.setCode(productCategory.getCode());
				checkProductCategory.setDescription(productCategory.getDescription());
				checkProductCategory.setParent(productCategory.getParent());
				checkProductCategory.setType(productCategory.getType());
				checkProductCategory.setOrderNo(productCategory.getOrderNo());
				
				productCategoryService.save(checkProductCategory);
			}
			
    		CommonResponse<ProductCategory> response = commonResponseGenerator.generateCommonResponse(productCategory);
        	
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
        	ProductCategory productCategory = productCategoryService.getById(id);
        	
        	if(productCategory==null){
        		throw new UserException("06", "ProductCategory not found");
        	}
        	
        	CommonResponse<ProductCategory> response = commonResponseGenerator.generateCommonResponse(productCategory);
        	
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
                                    @RequestParam(name = "sort", defaultValue = ProductCategory.Constant.CREATED_DATE_FIELD) String sort,
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
        CommonPaging<ProductCategory> userPage = productCategoryService.getPaging(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<ProductCategory> resp = new CommonResponsePaging<ProductCategory>(userPage);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }
	
}
