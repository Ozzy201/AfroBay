package reader.softech.com.zcommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import reader.softech.com.zcommerce.Prevelant.AdminPrevelant;
import reader.softech.com.zcommerce.Prevelant.Prevelant;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText phoneNumbertxt,nametxt,addresstxt,city;
    private Button confirmBtn;
    private String totalAmount="";
    //private String adminPhone;

    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        //adminPhone=getIntent().getExtras().get("adminNumber").toString();


        totalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = K"+ totalAmount, Toast.LENGTH_SHORT).show();



        loadingBar=new ProgressDialog(this);
        phoneNumbertxt=(EditText) findViewById(R.id.order_phone);
        nametxt=(EditText) findViewById(R.id.order_name);
        addresstxt=(EditText) findViewById(R.id.order_address);
        city=(EditText) findViewById(R.id.order_city);
        confirmBtn=(Button) findViewById(R.id.order_confirm);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    private void check()
    {
        if(TextUtils.isEmpty(nametxt.getText().toString()))
        {
            Toast.makeText(this, "Please Enter your full name.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneNumbertxt.getText().toString()))
    {
        Toast.makeText(this, "Please Enter your phone number.", Toast.LENGTH_SHORT).show();
    }

        else if(TextUtils.isEmpty(addresstxt.getText().toString()))
        {
            Toast.makeText(this, "Please Enter your address.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(city.getText().toString()))
        {
            Toast.makeText(this, "Please Enter your City.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            confirmOrder();
        }
    }

    private void confirmOrder()
    {
        loadingBar.setTitle("Confirming Order");
        loadingBar.setMessage("Please wait.......");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final String saveCurrentDate,saveCurrentTime;

        Calendar callForDate= Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM  dd, yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(callForDate.getTime());

        final DatabaseReference ordersRef= FirebaseDatabase.getInstance()
                .getReference().child("Orders").child(Prevelant.currentOnlineUser.getPhone());
        HashMap<String, Object> ordersMap= new HashMap<>();

        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",nametxt.getText().toString());
        ordersMap.put("phone",phoneNumbertxt.getText().toString());
        ordersMap.put("address",addresstxt.getText().toString());
        ordersMap.put("city",city.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state","not shipped");
        //ordersMap.put("adminPhoneNumber",adminPhone);
        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
              if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevelant.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) 
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your order has been placed successfully!", Toast.LENGTH_SHORT).show();

                                        Intent intent=new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });
                }

            }
        });


    }
}
