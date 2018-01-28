package com.eter.muven.cake.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import com.eter.cake.persistence.entity.rest.StatusNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eter.cake.persistence.entity.ProductType;
import com.eter.cake.persistence.entity.rest.SaleNotification;
import com.eter.cake.persistence.service.NotificationDaoService;
import com.eter.response.CommonResponseGenerator;
import com.eter.response.entity.CommonResponse;
import com.eter.util.JsonUtil;

@RestController
@RequestMapping("/notification")
public class NotificationController extends BaseController{
    @SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Autowired(required = true)
	private NotificationDaoService notificationDaoService;

	@Autowired
	private CommonResponseGenerator commonResponseGenerator;
	
	@RequestMapping(method = RequestMethod.GET, value = NOTIFICATION_SALE)
	@ResponseBody
    public String getSaleNotifications( @RequestParam(name = "type", defaultValue = "") String type)
            throws Exception {
		
		ProductType productType = new ProductType();
		productType.setId(type);
		List<SaleNotification> saleNotifications = notificationDaoService.getSaleNotifications(productType);
		
		CommonResponse<List<SaleNotification>> resp = new CommonResponse<List<SaleNotification>>(saleNotifications);
		
		return JsonUtil.generateJson(resp);
    }

	@RequestMapping(method = RequestMethod.GET, value = NOTIFICATION_STATUS)
	@ResponseBody
	public String getStatusNotifications( @RequestParam(name = "type", defaultValue = "") String type, @RequestParam(name = "barcode", defaultValue = "") String barcode)
			throws Exception {

		ProductType productType = new ProductType();
		productType.setId(type);
		List<StatusNotification> statusNotifications = notificationDaoService.getStatusNotification(productType, barcode);

		CommonResponse<List<StatusNotification>> resp = new CommonResponse<List<StatusNotification>>(statusNotifications);

		return JsonUtil.generateJson(resp);
	}
}
