package com.example.adminecom.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adminecom.models.ProductModel;
import com.example.adminecom.models.PurchaseModel;
import com.example.adminecom.repos.AdminRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private final AdminRepository repository = new AdminRepository();
    public MutableLiveData<List<String>> categoryListLiveData = new MutableLiveData<>();
    public MutableLiveData<List<ProductModel>> productListLiveData = new MutableLiveData<>();

    public ProductViewModel() {
        getCategories();
        getProducts();
    }

    public void addProduct(ProductModel productModel, PurchaseModel purchaseModel) {
        repository.addNewProduct(productModel, purchaseModel);
    }

    private void getCategories() {
        repository.getAllCategories(items ->
                categoryListLiveData.postValue(items));
    }

    private void getProducts() {
        repository.getAllProducts(items ->
                productListLiveData.postValue(items));
    }
}
