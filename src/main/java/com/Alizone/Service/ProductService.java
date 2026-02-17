package com.Alizone.Service;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import com.Alizone.Dto.DtoProduct;
import com.Alizone.Entity.Product;
import com.Alizone.Enum.CATEGORY;
import com.Alizone.Exception.BusinessException;
import com.Alizone.Repository.ProductRepository;

@Service
public class ProductService implements IProductService {

    private final SecurityFilterChain filterChain;
	@Autowired
	private ProductRepository productRepository;

    ProductService(SecurityFilterChain filterChain) {
        this.filterChain = filterChain;
    }

	@Override
	public Product addProduct(Product product) {
		
			return productRepository.save(product);

	}

	@Override
	public List<Product> getallproduct() {
			return productRepository.findAll();
		
	}

	@Override
	public List<DtoProduct> getallproductdto() {
		List<Product> productlist=productRepository.findAll();
		List<DtoProduct> dtoProductlist=new ArrayList<>();
	
		for (Product product : productlist) {
			if (product.isAktif()==true) {
				
			
			DtoProduct dtoProduct=new DtoProduct();
			dtoProduct.setBoyutlar(product.getBoyutlar());
			dtoProduct.setDigerOzellikler(product.getDigerOzellikler());
			dtoProduct.setEklenmeTarihi(product.getEklenmeTarihi());
			dtoProduct.setEnerji(product.getEnerji());
			dtoProduct.setFiyat(product.getFiyat());
			dtoProduct.setGarantiAy(product.getGarantiAy());
			dtoProduct.setId(product.getId());
			dtoProduct.setInverter(product.getInverter());
			dtoProduct.setIsim(product.getIsim());
			dtoProduct.setKapasite(product.getKapasite());
			dtoProduct.setKategori(product.getKategori());
			dtoProduct.setStokadeti(product.getStokAdeti());
			dtoProduct.setMarka(product.getMarka());
			dtoProduct.setModel(product.getModel());
			dtoProduct.setNotlar(product.getNotlar());
			dtoProduct.setOnemliOzellikler(product.getOnemliOzellikler());
			dtoProduct.setRefrigerant(product.getRefrigerant());
			dtoProduct.setRenk(product.getRenk());
			dtoProduct.setResimler(product.getResimler());
			dtoProduct.setSertifikalar(product.getSertifikalar());
			dtoProduct.setStokDurumu(product.getStokDurumu());
			dtoProduct.setTeklifilesatilir(product.isTeklifilesatilir());
			dtoProduct.setBtu(product.getBtu());
			dtoProduct.setMontage(product.isMontage());
			
			
			dtoProductlist.add(dtoProduct);
			
			}
			
			
		}
		
		return dtoProductlist;
	}

	@Override
	public DtoProduct findProductByid(Long id) {
	    Product product = productRepository.findById(id)
	        .orElseThrow(() -> new BusinessException("Ürün bulunamadı: " + id));

	    if (product.getFiyat() == null) {
	        throw new BusinessException("Ürün fiyatı boş olamaz: " + product.getIsim());
	    }

	    DtoProduct dtoProduct = new DtoProduct();
	    dtoProduct.setId(product.getId());
	    dtoProduct.setBoyutlar(product.getBoyutlar());
	    dtoProduct.setDigerOzellikler(product.getDigerOzellikler());
	    dtoProduct.setEklenmeTarihi(product.getEklenmeTarihi() != null ? product.getEklenmeTarihi() : LocalDateTime.now());
	    dtoProduct.setEnerji(product.getEnerji());
	    dtoProduct.setFiyat(product.getFiyat());
	    dtoProduct.setGarantiAy(product.getGarantiAy());
	    dtoProduct.setInverter(product.getInverter());
	    dtoProduct.setIsim(product.getIsim());
	    dtoProduct.setKapasite(product.getKapasite());
	    dtoProduct.setKategori(product.getKategori());
	    dtoProduct.setMarka(product.getMarka());
	    dtoProduct.setModel(product.getModel());
	    dtoProduct.setNotlar(product.getNotlar());
	    dtoProduct.setOnemliOzellikler(product.getOnemliOzellikler());
	    dtoProduct.setRefrigerant(product.getRefrigerant());
	    dtoProduct.setRenk(product.getRenk());
	    dtoProduct.setResimler(product.getResimler());
	    dtoProduct.setSertifikalar(product.getSertifikalar());
	    dtoProduct.setBtu(product.getBtu());
	    return dtoProduct;
	}
	@Override
	public DtoProduct deleteproduct(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		DtoProduct dtoproduct=new DtoProduct();
		product.setAktif(false);
		productRepository.save(product);
		BeanUtils.copyProperties(product, dtoproduct);
		return dtoproduct;
		
	
	}

	@Override
	public Product referenceProduct(Long id) {
		//Optional<Product> optional=productRepository.findById(id);
		//Product product=optional.get();
		if (!productRepository.existsById(id)) {
		    throw new BusinessException("Ürün bulunamadı");
		}
		return productRepository.getReferenceById(id);
	}

	@Override
	public Product findEntityById(Long id) {
		
		return productRepository.findById(id)
		        .orElseThrow(() -> new BusinessException("Ürün bulunamadı"));
		}

	@Override
	public Product deleteproductbyadmin(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product =optional.get();
		DtoProduct dtoproduct=new DtoProduct();
		product.setAktif(false);
		productRepository.save(product);
		
		return product;
		
	}

	@Override
	public Product updateproductprice(Long id, BigDecimal newprice) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setFiyat(newprice);
		return productRepository.save(product);
	}

	@Override
	public Product updateProductimage(Long id, List<String> newurl) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setResimler(newurl);
		return productRepository.save(product);
	}
		

	@Override
	public Product updateStockquantity(Long id, Integer newquantity) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setStokAdeti(newquantity);
		return productRepository.save(product);
	}

	@Override
	public Integer updatebtuproduct(Long id, Integer newbtu) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setBtu(newbtu);
		productRepository.save(product);
		

		
		return newbtu;
	}

	@Override
	public String teklifilesatilir(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setTeklifilesatilir(false);
		productRepository.save(product);
		return "ürün teklif vere alınmıştır";
	}

	@Override
	public String setactiveproduct(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setAktif(true);
		productRepository.save(product);
		return "Ürün aktifleşirildi";
	}

	@Override
	public String setProductteklifilesatilir(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setTeklifilesatilir(false);
		productRepository.save(product);
		return "başarıyla güncellendi refresh atarak kontrol edebilirsiniz";
	}

	@Override
	public String setcategory(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setKategori(CATEGORY.TICARI);
		productRepository.save(product);
		return "işleminiz başarıyla gerçekleşti";
		
		
		
	}

	@Override
	public String setnamebyProduct(Long id, String name) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setIsim(name);		
		productRepository.save(product);
		return "Başarıyla değiştirldi";
	}

	@Override
	public String setnotes(Long id, List<String> newnotes) {
		Optional<Product> optional = productRepository.findById(id);

	    if (optional.isEmpty()) {
	        return "Ürün bulunamadı";
	    }

	    Product product = optional.get();
	    product.setNotlar(newnotes);
	    productRepository.save(product);

	    return "Notlar Güncellendi";
	}

	@Override
	public String setMontage(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setMontage(true);
		productRepository.save(product);
		return "başarıyla değiştirildi";
	}

	@Override
	public String setdeMontage(Long id) {
		Optional<Product> optional=productRepository.findById(id);
		Product product=optional.get();
		product.setMontage(false);
		productRepository.save(product);

		return null;
	}
		
	
		
		
	}
	
	


