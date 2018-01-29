package com.eter.muven.cake.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.eter.cake.persistence.service.InventoryOutDaoService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.service.InventoryDaoService;
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
@RequestMapping("/inventory")
public class InventoryController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(InventoryController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Autowired(required = true)
	private InventoryDaoService inventoryService;

    @Autowired(required = true)
    private InventoryOutDaoService inventoryOutService;

	@Autowired
	private CommonResponseGenerator commonResponseGenerator;
	
	@Autowired
	private VelocityEngine velocityEngine;

    private static final DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String get(@PathVariable("id") String id) throws JsonProcessingException, UserException{
    	try {
    		logger.debug("getById: {}", id);
        	Inventory inventory = inventoryService.getById(id);
        	
        	CommonResponse<Inventory> response = commonResponseGenerator.generateCommonResponse(inventory);
        	
        	logger.debug("response getById: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }

	@RequestMapping(method=RequestMethod.POST, value=ADD)
	@ResponseBody
    public String add(@RequestBody Inventory inventory) throws JsonProcessingException, UserException{
		try {
			logger.debug("add: {}", JsonUtil.generateJson(inventory));
        	inventory.setId(null);
        	
        	inventoryService.save(inventory);
        	
    		CommonResponse<Inventory> response = commonResponseGenerator.generateCommonResponse(inventory);
        	
        	logger.debug("response add: {}", JsonUtil.generateJson(response));
        	return JsonUtil.generateJson(response);
    	} catch (Exception e) {
    		throw new UserException("06", e.getMessage());
        }
    }
	
	@RequestMapping(method=RequestMethod.POST, value=ADD_BULK)
	@ResponseBody
    public String addBulk(@RequestBody List<Inventory> inventorys) {
		try {
        	return null;
    	} catch (Exception e) {
            return null;
        }
    }
	
	@RequestMapping(method = RequestMethod.POST, value = UPDATE)
	@ResponseBody
    public String update( @RequestBody Inventory inventory) throws JsonProcessingException, UserException {
		try {
			logger.debug("update: {}", JsonUtil.generateJson(inventory));
			
			Inventory checkInventory = inventoryService.getById(inventory.getId());
			if(checkInventory==null){
				throw new UserException("06", "Inventory not found");
			}else{
				checkInventory.setTransactionCode(inventory.getTransactionCode());
				checkInventory.setInvoice(inventory.getInvoice());
				checkInventory.setSupplier(inventory.getSupplier());
				checkInventory.setDate(inventory.getDate());
				checkInventory.setItems(inventory.getItems());
				
				inventoryService.save(checkInventory);
			}
			
    		CommonResponse<Inventory> response = commonResponseGenerator.generateCommonResponse(inventory);
        	
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
        	Inventory inventory = inventoryService.getById(id);
        	
        	if(inventory==null){
        		throw new UserException("06", "Inventory not found");
        	}
        	
        	CommonResponse<Inventory> response = commonResponseGenerator.generateCommonResponse(inventory);
        	
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
                                    @RequestParam(name = "sort", defaultValue = Inventory.Constant.CREATED_DATE_FIELD) String sort,
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

        CommonPaging<Inventory> userPage = inventoryService.getPaging(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<Inventory> resp = new CommonResponsePaging<Inventory>(userPage);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }

    private static final Set<String> TrxTypeIn = new HashSet<>(Arrays.asList( new String[] {"PU", "ST", "RE"} ));
    private static final Set<String> TrxTypeOut = new HashSet<>(Arrays.asList( new String[] {"SA", "CR", "EX"} ));

    @RequestMapping(method = RequestMethod.GET, value = "/maxTrxNumber")
    @ResponseBody
    public String get(@RequestParam(name = "type") String type, @RequestParam(name = "date", required = false) String date
                    ) throws JsonProcessingException, UserException{
        try {
            logger.debug("getMaxNumber: type({}), date({})", type, date);

            int number = 0;

            List<KeyValue> filters = new ArrayList<>();

            KeyValue keyValue1 = new KeyValue();
            keyValue1.setKey("type");
            keyValue1.setValue(type);
            filters.add(keyValue1);

            //if(date!=null){
                Date startDate = Calendar.getInstance().getTime();

                KeyValue keyValue2 = new KeyValue();
                keyValue2.setKey("date");
                keyValue2.setValue(String.valueOf(startDate.getTime()));
                filters.add(keyValue2);
            //}

            number = (TrxTypeIn.contains(type) ? inventoryService.countListPaging(filters) : inventoryOutService.countListPaging(filters))+1;

            CommonResponse<Integer> response = commonResponseGenerator.generateCommonResponse(number);

            logger.debug("response getById: {}", JsonUtil.generateJson(response));
            return JsonUtil.generateJson(response);
        } catch (Exception e) {
            throw new UserException("06", e.getMessage());
        }
    }
}
