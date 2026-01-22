package com.Alizone.Conroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Alizone.Dto.DtoProduct;
import com.Alizone.Entity.Product;
import com.Alizone.Service.IProductService;

@RestController
@RequestMapping("/alizone/product")
public class ProductController implements IProductController {
	@Autowired
	private IProductService productService;
	

	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getallproduct")
	@Override
	public ResponseEntity<List<Product>> getallproduct() {
		List<Product> productList=productService.getallproduct();
		return ResponseEntity.ok(productList);
		
		
		
	}
	
	@GetMapping("/getalldtoproduct")
	@Override
	public ResponseEntity<List<DtoProduct>> getallproductdto() {
		List<DtoProduct> dtoProductList=productService.getallproductdto();
		return ResponseEntity.ok(dtoProductList);
		
		
	}
	
	@GetMapping("/getproduct/{id}")
	@Override
	public DtoProduct findProductByid(@PathVariable Long id) {
		
		return productService.findProductByid(id);
	}

}
