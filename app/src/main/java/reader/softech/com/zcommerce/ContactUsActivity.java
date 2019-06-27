package reader.softech.com.zcommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Pattern;

import reader.softech.com.zcommerce.Prevelant.Prevelant;

public class ContactUsActivity extends AppCompatActivity {

    private EditText messageInput;
    private  EditText emailinput;
    private Button submitContacUsBtn;
    private DatabaseReference userInfo;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

       emailinput=(EditText) findViewById(R.id.email_input);
       messageInput=(EditText) findViewById(R.id.message_input);
       submitContacUsBtn=(Button) findViewById(R.id.submit_contact_us_btn);
       loadingBar= new ProgressDialog(this);

        userInfo=FirebaseDatabase.getInstance().getReference().child("Contact Us");

       submitContacUsBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               validateInputAreas();
           }
       });
    }

    private void validateInputAreas() {
        String emaiilinput=emailinput.getText().toString();
        String messageArea=messageInput.getText().toString();

        if(TextUtils.isEmpty(emaiilinput))
        {
            Toast.makeText(this, "Please Enter your email address.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(messageArea))
        {
            Toast.makeText(this, "Please Enter message.", Toast.LENGTH_SHORT).show();
        }
        else{
            isValidEmail(emaiilinput);




        }

    }

    private void sendmessage() {

        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Sending Message.....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        saveContactInfo();


        emailinput.setText("");
        messageInput.setText("");



    }

    private void saveContactInfo() {

        HashMap<String, Object> contactMap=new HashMap<>();
        contactMap.put("userEmail",emailinput.getText().toString());
        contactMap.put("userMessage",messageInput.getText().toString());


        userInfo.child(Prevelant.currentOnlineUser.getPhone()).updateChildren(contactMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(ContactUsActivity.this,HomeActivity.class);
                            finish();
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(ContactUsActivity.this, "Message has been sent!", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(ContactUsActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }









    public  boolean isValidEmail(CharSequence target) {

        if(!Patterns.EMAIL_ADDRESS.matcher(target).matches())
        {
            emailinput.setError("Please enter a valid email address");
            return false;

        }
        else {
            sendmessage();

        }


        return true;
    }

}
