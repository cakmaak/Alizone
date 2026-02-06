package com.Alizone.Conroller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.DtoProduct;
import com.Alizone.Entity.Product;

public interface IProductController {
	
	
	public ResponseEntity<List<Product>> getallproduct();
	public ResponseEntity<List<DtoProduct>> getallproductdto();
	public DtoProduct findProductByid(Long id);
	
	
	

}
