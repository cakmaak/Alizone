package com.Alizone.Conroller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Alizone.Entity.Order;
import com.Alizone.Entity.Product;

public interface IAdminPanelController {
	
	public Product deleteproductbyadmin(Long id);
	public Product updateproductprice(Long id,BigDecimal newprice);
	public Product updateProductimage(Long id,List<String> newurl);
	public Product updateStockquantity(Long id,Integer newquantity);
	public ResponseEntity<Product> addProduct(Product product);
	public ResponseEntity<?> changeOrderStatus(Long id, String cargoTrackingNo);
	public Integer updatebtuproduct(Long id, Integer newbtu);
	public List<Product> getallproduct();
	public List<Order> getallorders();
	public String teklifilesatilir(Long id);
	public String setactiveproduct(Long id);
	public ResponseEntity<?> startRefund(Long orderId);
	public String setProductteklifilesatilir(Long id);
}
