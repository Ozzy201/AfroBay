package reader.softech.com.zcommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import reader.softech.com.zcommerce.Model.Cart;
import reader.softech.com.zcommerce.Model.Products;
import reader.softech.com.zcommerce.Model.Users;
import reader.softech.com.zcommerce.Prevelant.AdminPrevelant;
import reader.softech.com.zcommerce.Prevelant.Prevelant;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, Description, Price, Pname,saveCurrentDate,saveCurrentTime;
    private Button addNewProductButton;
    private ImageView inputProductImage;
    private EditText inputProductName,inputProductDescription,inputProductPrice;
    private static final int galleryPick=1;
    private Uri ImageUri;
    private String productRandomKey,downloadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

   // private String adminPhone;
    private String userPhone;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);



        //New cODE

        //adminPhone=AdminPrevelant.currentOnlineAdmin.getPhone();


        userPhone=Prevelant.currentOnlineUser.getPhone();

        categoryName=getIntent().getExtras().get("category").toString();
        productImagesRef= FirebaseStorage.getInstance().getReference().child("Product Images");
       productRef=FirebaseDatabase.getInstance().getReference().child("Products");

        //New cODE







        addNewProductButton=(Button) findViewById(R.id.add_new_product);
        inputProductImage=(ImageView) findViewById(R.id.select_product_image);
        inputProductName=(EditText) findViewById(R.id.product_name);
        inputProductDescription=(EditText) findViewById(R.id.product_description);
        inputProductPrice=(EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatProductData();
            }
        });

    }

    private void openGallery() {

        //new code
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);





        if(requestCode==galleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            inputProductImage.setImageURI(ImageUri);
        }

    }

    private void validatProductData()
    {
        Description=inputProductDescription.getText().toString();
        Price=inputProductPrice.getText().toString();
        Pname=inputProductName.getText().toString();

        if(ImageUri==null)
        {
            Toast.makeText(this, "Product Image is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please write product description....", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please write product price....", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this, "Please write product name....", Toast.LENGTH_SHORT).show();
        }
        else {
            storeProductInformatio();
        }

    }

    private void storeProductInformatio()
    {
        loadingBar.setTitle("Add New Product.");
        loadingBar.setMessage("Please wait while we are adding new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productRandomKey=saveCurrentDate + saveCurrentTime;

        final StorageReference filepath= productImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String messeage= e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: "+ messeage, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AdminAddNewProductActivity.this, "Product Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }
                        downloadImageUrl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product Image saved to database successfully!", Toast.LENGTH_SHORT).show();
                            
                            saveNewProductInfoToDatabase();

                        }
                    }
                });
            }
        });
    }

    private void saveNewProductInfoToDatabase() {

        HashMap<String, Object> productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        //NEW CODE
      //productMap.put("adminNumber",adminPhone);
        productMap.put("adminNumber",userPhone);


        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);
                            finish();

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully!", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

//open Me