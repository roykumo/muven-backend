package com.eter.muven;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eter.cake.persistence.service.InventoryDaoService;
import com.eter.cake.persistence.service.InventoryDaoServiceImpl;
import com.eter.cake.persistence.service.InventoryItemDaoService;
import com.eter.cake.persistence.service.InventoryItemDaoServiceImpl;
import com.eter.cake.persistence.service.NotificationDaoService;
import com.eter.cake.persistence.service.NotificationDaoServiceImpl;
import com.eter.cake.persistence.service.ProductDaoService;
import com.eter.cake.persistence.service.ProductDaoServiceImpl;
import com.eter.cake.persistence.service.ProductTypeDaoService;
import com.eter.cake.persistence.service.ProductTypeDaoServiceImpl;
import com.eter.cake.persistence.service.SellPriceDaoService;
import com.eter.cake.persistence.service.SellPriceDaoServiceImpl;

@Configuration
public class DaoSpringConfig {

	@Bean
	public ProductDaoService productDaoService() {
		return new ProductDaoServiceImpl();
	}

	@Bean
	public ProductTypeDaoService productTypeDaoService() {
		return new ProductTypeDaoServiceImpl();
	}

	@Bean
	public InventoryDaoService inventoryDaoService() {
		return new InventoryDaoServiceImpl();
	}

	@Bean
	public InventoryItemDaoService inventoryItemDaoService() {
		return new InventoryItemDaoServiceImpl();
	}
	
	@Bean
	public SellPriceDaoService sellPriceDaoService() {
		return new SellPriceDaoServiceImpl();
	}

	@Bean
	public NotificationDaoService notificationDaoService() {
		return new NotificationDaoServiceImpl();
	}
}
