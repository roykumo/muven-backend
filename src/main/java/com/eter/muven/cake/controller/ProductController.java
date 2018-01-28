package com.eter.muven.cake.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.eter.cake.persistence.entity.ProductCategory;
import com.eter.cake.persistence.service.ProductCategoryDaoService;
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
import com.eter.cake.persistence.entity.ProductType;
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.entity.rest.ProductStock;
import com.eter.cake.persistence.service.ProductDaoService;
import com.eter.cake.persistence.service.ProductTypeDaoService;
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
@RequestMapping("/product")
public class ProductController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(ProductController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Autowired(required = true)
	private ProductDaoService productService;

	@Autowired(required = true)
	private ProductTypeDaoService productTypeService;

	@Autowired
	private ProductCategoryDaoService productCategoryDaoService;
	
	@Autowired
	private CommonResponseGenerator commonResponseGenerator;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String get(@PathVariable("id") String id) throws JsonProcessingException, UserException{
    	try {
    		logger.debug("getById: {}", id);
        	Product product = productService.getById(id);
        	
        	CommonResponse<Product> response = commonResponseGenerator.generateCommonResponse(product);
        	
        	logger.debug("response getById: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }

	@RequestMapping(method=RequestMethod.POST, value=ADD)
	@ResponseBody
    public String add(@RequestBody Product product) throws JsonProcessingException, UserException{
		try {
			logger.debug("add: {}", JsonUtil.generateJson(product));
        	product.setId(null);
        	if(product.getCategory()!=null){
        		ProductCategory productCategory = productCategoryDaoService.getById(product.getCategory().getId());
        		
        		if(productCategory==null){
        			throw new UserException("06", "Product Category not found");
        		}
        		
        		product.setCategory(productCategory);
        	}
        	
        	productService.save(product);
        	
    		CommonResponse<Product> response = commonResponseGenerator.generateCommonResponse(product);
        	
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
    public String addBulk(@RequestBody List<Product> products) {
		try {
        	return null;
    	} catch (Exception e) {
            return null;
        }
    }
	
	@RequestMapping(method = RequestMethod.POST, value = UPDATE)
	@ResponseBody
    public String update( @RequestBody Product product) throws JsonProcessingException, UserException {
		try {
			logger.debug("update: {}", JsonUtil.generateJson(product));
			
			Product checkProduct = productService.getById(product.getId());
			if(checkProduct==null){
				throw new UserException("06", "Product not found");
			}else{
				checkProduct.setCode(product.getCode());
				checkProduct.setBarcode(product.getBarcode());
				checkProduct.setName(product.getName());
				checkProduct.setProductGroup(product.getProductGroup());
				checkProduct.setCategory(product.getCategory());
				checkProduct.setAlertRed(product.getAlertRed());
				checkProduct.setAlertYellow(product.getAlertYellow());
				checkProduct.setAlertGreen(product.getAlertGreen());
				checkProduct.setAlertBlue(product.getAlertBlue());
				
				if(product.getCategory()!=null){
					checkProduct.setCategory(productCategoryDaoService.getById(product.getCategory().getId()));
				}
				
				productService.save(checkProduct);
			}
			
    		CommonResponse<Product> response = commonResponseGenerator.generateCommonResponse(product);
        	
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
        	Product product = productService.getById(id);
        	
        	if(product==null){
        		throw new UserException("06", "Product not found");
        	}
        	
        	productService.delete(product);
        	
        	CommonResponse<Product> response = commonResponseGenerator.generateCommonResponse(product);
        	
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
                                    @RequestParam(name = "sort", defaultValue = Product.Constant.CREATED_DATE_FIELD) String sort,
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

        CommonPaging<Product> userPage = productService.getPaging(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<Product> resp = new CommonResponsePaging<Product>(userPage);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }
	
	@RequestMapping(method = RequestMethod.GET, value = GET_PRODUCT_STOCK)
	@ResponseBody
    public String getStock( @RequestParam(name = "type", defaultValue = "") String type,
                            @RequestParam(name = "code", defaultValue = "") String code,
                            @RequestParam(name = "category", defaultValue = "") String category,
                            @RequestParam(name = "group", defaultValue = "") String group)
            throws Exception {
		
		ProductType productType = new ProductType();
		productType.setId(type);

		ProductCategory productCategory = null;
		if(!StringUtils.isEmpty(category)){
		    productCategory = new ProductCategory();
		    productCategory.setId(category);
        }

		List<ProductStock> stocks = productService.getProductStock(productType, productCategory, code, group);

		CommonResponse<List<ProductStock>> resp = new CommonResponse<List<ProductStock>>(stocks);
		
		return JsonUtil.generateJson(resp);
    }

	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public String getProduct(@RequestParam(name = "barcode", defaultValue = "") String barcode)
			throws Exception {

		try {
			logger.debug("getProduct by Barcode: {}", barcode);
			Product product = productService.getByBarcode(barcode);
			if(product==null){
				product = productService.getByCode(barcode);
			}

			CommonResponse<Product> response = commonResponseGenerator.generateCommonResponse(product);

			logger.debug("response getProduct by Barcode: {}", JsonUtil.generateJson(response));
			return JsonUtil.generateJson(response);
		} catch (Exception e) {
			throw new UserException("06", e.getMessage());
		}
	}
}
