package com.eter.muven.cake.controller;

import com.eter.cake.persistence.entity.Product;
import com.eter.cake.persistence.entity.ProductType;
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.entity.rest.ProductStock;
import com.eter.cake.persistence.entity.rest.Transaction;
import com.eter.cake.persistence.service.ProductDaoService;
import com.eter.cake.persistence.service.ProductTypeDaoService;
import com.eter.cake.persistence.service.TransactionDaoService;
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
@RequestMapping("/transaction")
public class TransactionController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(TransactionController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");

	@Autowired
	private TransactionDaoService transactionDaoService;

	@Autowired
	private CommonResponseGenerator commonResponseGenerator;

	@RequestMapping(method = RequestMethod.GET, value = "/list")
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

        CommonPaging<Transaction> transactionCommonPaging = transactionDaoService.getTransactions(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<Transaction> resp = new CommonResponsePaging<Transaction>(transactionCommonPaging);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }

}
