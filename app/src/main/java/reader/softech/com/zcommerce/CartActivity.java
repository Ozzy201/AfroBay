package reader.softech.com.zcommerce;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import reader.softech.com.zcommerce.Model.Cart;
import reader.softech.com.zcommerce.Model.Admins;
import reader.softech.com.zcommerce.Model.Products;


import reader.softech.com.zcommerce.Prevelant.Prevelant;
import reader.softech.com.zcommerce.ViewHolder.CartViewHolder;


public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView totalText,txtmsg1;
    private Button nextBtn;
    private int overalTotalPrice=0;
   private String productId="";
   // private String adminPhone;
    private ProgressDialog loadingBar;
    AdView mAdview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        MobileAds.initialize(this, "ca-app-pub-6882357503825534/9321668436");

        mAdview=(AdView) findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);



        //adminPhone=getIntent().getStringExtra("adminNumber");
        productId=getIntent().getStringExtra("pid");

        loadingBar=new ProgressDialog(this);

        recyclerView=findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextBtn=(Button) findViewById(R.id.next_btn);
        totalText=(TextView) findViewById(R.id.Total_price_txt);
        txtmsg1=(TextView) findViewById(R.id.msg1);




        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loadingBar.setTitle("Loading");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                totalText.setText("Total Price = K"+String.valueOf(overalTotalPrice));

                Intent intent=new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overalTotalPrice));
                startActivity(intent);
                finish();

                /*Toast.makeText(CartActivity.this, "GOOD JOB", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(CartActivity.this,SettingsActivity.class);
                startActivity(intent);*/
            }
        });

    }

    private void checkCartList() {



           DatabaseReference cartsR;
           cartsR = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("User View")
                   .child(Prevelant.currentOnlineUser.getPhone());


           cartsR.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                   if (!dataSnapshot.exists()) {


                       recyclerView.setVisibility(View.GONE);
                       txtmsg1.setVisibility(View.VISIBLE);
                       txtmsg1.setText("");
                       nextBtn.setVisibility(View.GONE);

                   }


               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });




    }

    @Override
    protected void onStart()
    {
        super.onStart();



        checkCartList();

       checkOrderState();






        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Prevelant.currentOnlineUser.getPhone())
                        .child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                holder.txtProductPrice.setText("Price = K"+model.getPrice());
                holder.txtProductName.setText(model.getPname());

                int oneTypeProductTprice=((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overalTotalPrice=overalTotalPrice+oneTypeProductTprice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                   "Edit",
                                   "Remove"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i==0){



                                    Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    finish();
                                    startActivity(intent);

                                }
                                if(i==1)
                                {

                                    loadingBar.setTitle("Removing Item");
                                    loadingBar.setMessage("Please wait........");
                                    loadingBar.setCanceledOnTouchOutside(false);
                                    loadingBar.show();

                                    //price user view
                                    cartListRef.child("User View")
                                            .child(Prevelant.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid()).child(model.getPrice())
                                            .removeValue();



                                    cartListRef.child("User View")
                                            .child(Prevelant.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) 
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "Item has been removed", Toast.LENGTH_SHORT).show();

                                                        Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                                                        finish();
                                                        startActivity(intent);

                                                    }

                                                }
                                            });



                                    //price admin view
                                    cartListRef.child("User View")
                                            .child(Prevelant.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid()).child(model.getPrice())
                                            .removeValue();


                                    //Admin View

                                    cartListRef.child("Admin View")
                                            .child(Prevelant.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "Item has been removed from admin view", Toast.LENGTH_SHORT).show();

                                                        //Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                                                       // startActivity(intent);
                                                    }

                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void checkOrderState()
    {


        DatabaseReference ordersRef;
        ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevelant.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.exists())
                {
                    String shippingState=dataSnapshot.child("state").getValue().toString();
                    String userName=dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {

                        totalText.setText("Dear "+userName+"\n order is shipped successfully!");
                        recyclerView.setVisibility(View.GONE);
                        txtmsg1.setVisibility(View.VISIBLE);
                        txtmsg1.setText("Congratulations your final order has been shipped successfully! Soon you will receive your order at your door step.");
                        nextBtn.setVisibility(View.GONE);
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        totalText.setText("Shipping state pending");
                        recyclerView.setVisibility(View.GONE);
                        txtmsg1.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.GONE);

                    }

                }



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
