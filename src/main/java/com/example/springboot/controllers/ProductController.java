package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDTO;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.validation.Valid;

@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDTO productRecordDTO){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));

    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){
		List<ProductModel> productsList = productRepository.findAll();
		if(!productsList.isEmpty()) {
			for(ProductModel product : productsList) {
				UUID id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getOne(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(productsList);
	}

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOne(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{'error': 'Produto não encontrado'}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(product0.get());

    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id , @RequestBody @Valid ProductRecordDTO productRecordDTO){
        Optional<ProductModel> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{'error': 'Produto não encontrado'}");
        }
        var productModel = existingProduct.get();
        BeanUtils.copyProperties(productRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProductById(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> existingProduct = productRepository.findById(id);

        if (existingProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{'error': 'Produto não encontrado'}");
        }

        productRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}