package com.example.adminecom.callbacks;


import com.example.adminecom.models.ProductModel;

import java.util.List;

public interface OnProductQueryCompleteListener {
    void onProductQueryComplete(List<ProductModel> items);
}
