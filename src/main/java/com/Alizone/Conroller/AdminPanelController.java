package com.Alizone.Conroller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Entity.Order;
import com.Alizone.Entity.Product;
import com.Alizone.Service.IOrderItemService;
import com.Alizone.Service.OrderCancelService;
import com.Alizone.Service.ProductService;

@RestController
@RequestMapping("/alizone/adminpanel")
public class AdminPanelController implements IAdminPanelController {
	
	@Autowired
	OrderCancelService orderCancelService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private IOrderItemService orderItemService;
	
	@PutMapping("/deleteproduct/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	public Product deleteproductbyadmin(@PathVariable Long id) {
		
		return productService.deleteproductbyadmin(id);
	}
	
	@PutMapping("/updprice/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	public Product updateproductprice(@PathVariable Long id,@RequestBody BigDecimal newprice) {
		// TODO Auto-generated method stub
		return productService.updateproductprice(id, newprice);
	}
	
	@PutMapping("/updimage/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	public Product updateProductimage(@PathVariable Long id,@RequestBody List<String> newurl) {
		
		return productService.updateProductimage(id, newurl);
	}
	
	@PutMapping("/updstock/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	public Product updateStockquantity(@PathVariable Long id,@RequestBody Integer newquantity) {
		// TODO Auto-generated method stub
		return productService.updateStockquantity(id, newquantity);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/addproduct")
	@Override
	public ResponseEntity<Product> addProduct(@RequestBody Product product) {
		Product addProduct=productService.addProduct(product);
		return ResponseEntity.ok(addProduct);
	
	}
	
	
	@PostMapping("/deliveredtruck/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	public ResponseEntity<?> changeOrderStatus(@PathVariable Long id,@RequestBody String cargoTrackingNo) {
		
		return orderItemService.changeOrderStatus(id, cargoTrackingNo);
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/updatebtu/{id}")
	@Override
	public Integer updatebtuproduct(@PathVariable Long id,@RequestBody Integer newbtu) {
		
		return productService.updatebtuproduct(id, newbtu);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getproducts")
	@Override
	public List<Product> getallproduct() {
		
		return productService.getallproduct();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getorders")
	@Override
	public List<Order> getallorders() {
		
		
		return orderItemService.getallorders();
	}

	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/setteklif/{id}")
	@Override
	public String teklifilesatilir(@PathVariable Long id) {
		return productService.teklifilesatilir(id);
		
		
		
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/setaktif/{id}")
	@Override
	public String setactiveproduct(@PathVariable Long id) {
		
		return productService.setactiveproduct(id);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/refund/{orderId}")
	@Override
	public ResponseEntity<?> startRefund(@PathVariable Long orderId) {
		orderCancelService.startRefundByAdmin(orderId);

	    return ResponseEntity.ok("Refund başlatıldı");
	}
	
	@PostMapping("/refund-callback")
    public ResponseEntity<String> refundCallback(
            @RequestParam Long orderId,
            @RequestParam boolean success
    ) {

        orderCancelService.handleBankRefundCallback(orderId, success);

        return ResponseEntity.ok("OK");
    }
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/setteklifal/{id}")
	@Override
	public String setProductteklifilesatilir(@PathVariable Long id) {
		return productService.setProductteklifilesatilir(id);
		
	}
		
	}


	
	


