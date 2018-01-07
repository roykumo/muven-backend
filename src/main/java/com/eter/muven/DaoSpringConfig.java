package com.eter.muven;

import com.eter.cake.persistence.entity.ProductCategory;
import com.eter.cake.persistence.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

	@Bean
	public InventoryOutDaoService inventoryOutDaoService() {return new InventoryOutDaoServiceImpl();}

	@Bean
	public InventoryItemOutDaoService inventoryItemOutDaoService() {
		return new InventoryItemOutDaoServiceImpl();
	}

	@Bean
	public ProductCategoryDaoService productCategoryDaoService() {
		return new ProductCategoryDaoServiceImpl();
	}

}
