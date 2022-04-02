package com.example.adminecom.repos;

import android.util.Log;

import com.example.adminecom.callbacks.OnCategoryQueryCompleteListener;
import com.example.adminecom.callbacks.OnCheckAdminListener;
import com.example.adminecom.callbacks.OnProductQueryCompleteListener;
import com.example.adminecom.models.ProductModel;
import com.example.adminecom.models.PurchaseModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminRepository {
    private final String TAG = AdminRepository.class.getSimpleName();
    private static final String COLLECTION_ADMIN = "Admins";
    private static final String COLLECTION_CATEGORY = "Categories";
    private static final String COLLECTION_PRODUCT = "Products";
    private static final String COLLECTION_PURCHASE = "Purchases";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addNewProduct(ProductModel productModel, PurchaseModel purchaseModel) {
        final DocumentReference pDoc = db.collection(COLLECTION_PRODUCT).document();
        productModel.setProductId(pDoc.getId());
        final DocumentReference purDoc = db.collection(COLLECTION_PURCHASE).document();
        purchaseModel.setPurchaseId(purDoc.getId());
        purchaseModel.setProductId(pDoc.getId());

        final WriteBatch writeBatch = db.batch();
        writeBatch.set(pDoc, productModel);
        writeBatch.set(purDoc, purchaseModel);
        writeBatch.commit().addOnCompleteListener(task -> {
            Log.e(TAG, "saved");
        }).addOnFailureListener(e -> {
            Log.e(TAG, "failed");

        });

    }

    public void getAllCategories(OnCategoryQueryCompleteListener listener) {
        db.collection(COLLECTION_CATEGORY)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    final List<String> temp = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        temp.add(doc.get("name", String.class));
                    }
                    Collections.sort(temp);
                    listener.onCategoryQueryComplete(temp);
                });
    }

    public void getAllProducts(OnProductQueryCompleteListener listener) {
        db.collection(COLLECTION_PRODUCT)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    final List<ProductModel> temp = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        temp.add(doc.toObject(ProductModel.class));
                    }
                    listener.onProductQueryComplete(temp);
                });
    }

    public void checkAdmin(String uid, OnCheckAdminListener listener) {
        final boolean status;
        db.collection(COLLECTION_ADMIN)
                .document(uid)
                .get()
                .addOnCompleteListener(task ->
                        listener.onCheckAdmin(task.getResult().exists()));

    }
}
