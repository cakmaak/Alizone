package com.Alizone.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Alizone.Dto.DtoProduct;
import com.Alizone.Entity.Product;

public interface IProductService {
	
	public Product addProduct(Product product);
	public List<Product> getallproduct();
	public List<DtoProduct> getallproductdto();
	public DtoProduct findProductByid(Long id);
	public DtoProduct deleteproduct(Long id);
	public Product referenceProduct(Long id);
	public Product findEntityById(Long id);
	public String teklifilesatilir(Long id);
	public String setactiveproduct(Long id);
	
	
	
	//FOR ADMİN
	public Product deleteproductbyadmin(Long id);
	public Product updateproductprice(Long id,BigDecimal newprice);
	public Product updateProductimage(Long id,List<String> newurl);
	public Product updateStockquantity(Long id,Integer newquantity);
	public Integer updatebtuproduct(Long id,Integer newbtu);
	public String setProductteklifilesatilir(Long id);
	public String setcategory(Long id);
	public String setnamebyProduct(Long id,String name);
	public String setnotes(Long id,List<String> newnotes);
	public String setMontage(Long id);
	public String setdeMontage(Long id);
	
	

}
