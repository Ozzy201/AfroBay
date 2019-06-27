package reader.softech.com.zcommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import reader.softech.com.zcommerce.Model.Admins;

import reader.softech.com.zcommerce.Model.Products;
import reader.softech.com.zcommerce.Prevelant.AdminPrevelant;
import reader.softech.com.zcommerce.Prevelant.Prevelant;

public class ProductDetailsActivity extends AppCompatActivity {


    private Button addtocartButton;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,ProductDescription,productName,productPhone;
    private String productId="",state="Normal";
   // private String adminPhone;
    private ProgressDialog loadingBar;
    AdView mAdview;
    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        MobileAds.initialize(this, "ca-app-pub-6882357503825534/9321668436");

        mAdview=(AdView) findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);



        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("ca-app-pub-6882357503825534/5435177166");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        productId=getIntent().getStringExtra("pid");
        loadingBar=new ProgressDialog(this);


        //adminPhone=AdminPrevelant.currentOnlineAdmin.getPhone();
       //adminPhone=getIntent().getStringExtra("adminNumber");

        addtocartButton=(Button) findViewById(R.id.pd_add_to_cart_btn);
        productImage=(ImageView) findViewById(R.id.product_image_details);
        numberButton=(ElegantNumberButton) findViewById(R.id.number_btn);
        productName=(TextView) findViewById(R.id.product_name_details);
        ProductDescription=(TextView) findViewById(R.id.product_description_details);
        productPrice=(TextView) findViewById(R.id.product_price_details);
        productPhone=(TextView) findViewById(R.id.product_phone);

        getProductDetails(productId);

        addtocartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(state.equals("Orders Placed") || state.equals("Orders Shipped"))
                {
                    Toast.makeText(ProductDetailsActivity.this, "You can place more orders once your orders has been confirmed", Toast.LENGTH_LONG).show();

                }



                else
                    {
                    addingToCartList();

                }
            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();




       checkOrderState();

    }

    private void addingToCartList()
    {

        loadingBar.setTitle("Adding to Cart");
        loadingBar.setMessage("Please wait.......");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String saveCurrentTime, saveCurrentDate;
        Calendar callForDate= Calendar.getInstance();


        SimpleDateFormat currentDate= new SimpleDateFormat("MMM  dd, yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(callForDate.getTime());

       final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap= new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");
       cartMap.put("adminNumber",productPhone.getText().toString());

        cartListRef.child("User View").child(Prevelant.currentOnlineUser.getPhone()).child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(Prevelant.currentOnlineUser.getPhone()).child("Products").child(productId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) 
                                        {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this, "Added to cart List", Toast.LENGTH_SHORT).show();

                                                Intent intent=new Intent(ProductDetailsActivity.this,HomeActivity.class);

                                                startActivity(intent);

                                                finish();

                                            }

                                        }
                                    });
                        }
                    }
                });

    }

    private void getProductDetails(String productId) {

        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    productName.setText(products.getPname());
                    ProductDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                  productPhone.setText("Call: "+ products.getAdminNumber());

                    RequestOptions options = new RequestOptions();
                    options.centerInside();


                    Glide.with(getApplicationContext()).load(products.getImage()).apply(options).into(productImage);


                    // Picasso.get().load(products.getImage()).memoryPolicy(MemoryPolicy.NO_STORE).fit().centerCrop().into(productImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkOrderState()
    {
        DatabaseReference ordersRef;

           /* String inf;
            inf=productPhone.getText().toString();
*/


            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevelant.currentOnlineUser.getPhone());

            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        String shippingState = dataSnapshot.child("state").getValue().toString();

                        if (shippingState.equals("shipped")) {
                            state = "Orders Shipped";

                        } else if (shippingState.equals("not shipped")) {
                            state = "Orders Placed";

                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }
}
