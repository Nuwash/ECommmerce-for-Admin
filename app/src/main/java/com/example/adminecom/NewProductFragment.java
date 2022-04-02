package com.example.adminecom;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.adminecom.databinding.FragmentNewProductBinding;
import com.example.adminecom.models.ProductModel;
import com.example.adminecom.models.PurchaseModel;
import com.example.adminecom.pickers.DatePickerDialogFragment;
import com.example.adminecom.utils.Constants;
import com.example.adminecom.viewmodels.ProductViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class NewProductFragment extends Fragment {
    private List<String> categories = new ArrayList<>();
    private FragmentNewProductBinding binding;
    private ProductViewModel productViewModel;
    String dateString, imageUrl, category;
    int year, month, day;
    private ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.
                            StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Uri photoUri = result.getData().getData();
                                if (photoUri != null) {
                                    binding.productImageView.setImageURI(photoUri);
                                }else {
                                    final Bitmap bitmap = (Bitmap) result.getData()
                                            .getExtras().get("data");
                                    binding.productImageView.setImageBitmap(bitmap);
                                }

                                binding.saveBtn.setText("Please wait");
                                binding.saveBtn.setEnabled(false);
                                uploadImage(photoUri);
                            }
                        }
                    });


    public NewProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentNewProductBinding.inflate(inflater,container,false);
        productViewModel=new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        productViewModel.categoryListLiveData
                .observe(getViewLifecycleOwner(), catList -> {
                    categories.clear();
                    categories.add("Select category");
                    categories.addAll(catList);
                    final ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    categories);
                    binding.categorySpinner.setAdapter(adapter);
                });

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    category = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.dateBtn.setOnClickListener(v -> {
            new DatePickerDialogFragment()
                    .show(getChildFragmentManager(), null);
        });

        binding.cameraBtn.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                launcher.launch(takePictureIntent);
            }catch (ActivityNotFoundException e) {

            }

        });
        binding.galleryBtn.setOnClickListener(v -> {
            final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
        binding.saveBtn.setOnClickListener(v -> {
            saveProduct();
        });

        getChildFragmentManager()
                .setFragmentResultListener(
                        Constants.REQUEST_KEY,
                        this, new FragmentResultListener() {
                            @Override
                            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                                if (result.containsKey(Constants.DATE_KEY)) {
                                    dateString = result.getString(Constants.DATE_KEY);
                                    day = result.getInt(Constants.DAY);
                                    month = result.getInt(Constants.MONTH);
                                    year = result.getInt(Constants.YEAR);
                                    binding.dateBtn.setText(dateString);
                                }
                            }
                        });

        return binding.getRoot();
    }

    private void saveProduct() {
        final String name = binding.prductNameInputET.getText().toString();
        final String purchasePrice = binding.purchasePriceInputET.getText().toString();
        final String salePrice = binding.salePriceInputET.getText().toString();
        final String description = binding.descriptionInputET.getText().toString();
        final String quantity = binding.quantityInputET.getText().toString();

        if (name.isEmpty()) {
            binding.prductNameInputET.setError(Constants.EMPTY_FIELD_ERROR_MSG);
            binding.prductNameInputET.setFocusable(true);
            return;
        }

        if (purchasePrice.isEmpty()) {
            binding.purchasePriceInputET.setError(Constants.EMPTY_FIELD_ERROR_MSG);
            binding.purchasePriceInputET.setFocusable(true);
            return;
        }

        if (salePrice.isEmpty()) {
            binding.salePriceInputET.setError(Constants.EMPTY_FIELD_ERROR_MSG);
            binding.salePriceInputET.setFocusable(true);
            return;
        }

        if (quantity.isEmpty()) {
            binding.quantityInputET.setError(Constants.EMPTY_FIELD_ERROR_MSG);
            binding.quantityInputET.setFocusable(true);
            return;
        }

        if (category == null) {
            Toast.makeText(getActivity(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dateString == null) {
            Toast.makeText(getActivity(), "Please select a purchase date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUrl == null) {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProductModel productModel = new ProductModel(
                null, name, category,
                Double.parseDouble(salePrice), imageUrl,
                description, true
        );

        final PurchaseModel purchaseModel = new PurchaseModel(
                null, null, dateString, year, month, day,
                Double.parseDouble(purchasePrice),
                Integer.parseInt(quantity)
        );
        productViewModel.addProduct(productModel, purchaseModel);
        resetViewsAndRefs();
    }

    private void resetViewsAndRefs() {
        binding.prductNameInputET.setText("");
        binding.purchasePriceInputET.setText("");
        binding.salePriceInputET.setText("");
        binding.descriptionInputET.setText("");
        binding.quantityInputET.setText("");
        category = null;
        dateString = null;
        imageUrl = null;
        binding.categorySpinner.setSelection(0);
        binding.dateBtn.setText("Select Date");
        binding.productImageView.setImageResource(R.mipmap.ic_launcher);

    }
    private void uploadImage(Uri photoUri) {
        Log.e("firebasestorage", photoUri.toString());
        final StorageReference photoRef =
                FirebaseStorage.getInstance().getReference()
                        .child("images/"+System.currentTimeMillis());

        // Get the data from an ImageView as bytes
        binding.productImageView.setDrawingCacheEnabled(true);
        binding.productImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) binding.productImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebasestorage", exception.getLocalizedMessage());
                binding.saveBtn.setText("Save");
                binding.saveBtn.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebasestorage", "Uploaded");
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    binding.saveBtn.setText("Save");
                    binding.saveBtn.setEnabled(true);
                    Log.e("firebasestorage", downloadUri.toString());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
}