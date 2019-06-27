package reader.softech.com.zcommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reader.softech.com.zcommerce.Model.Comments;
import reader.softech.com.zcommerce.Model.Users;
import reader.softech.com.zcommerce.Prevelant.Prevelant;
import reader.softech.com.zcommerce.ViewHolder.CommentsAdapter;

public class CommentsActivity extends AppCompatActivity {

    EditText addComments;
    ImageView ProfileImage;
    TextView post;

    String postID,publisherID,receiver_userId;

    FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comments> commentsList;
    private DatabaseReference NotificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar=findViewById(R.id.comments_toolbar);

        recyclerView=findViewById(R.id.comments_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentsList=new ArrayList<>();
        commentsAdapter=new CommentsAdapter(this,commentsList);
        recyclerView.setAdapter(commentsAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addComments=findViewById(R.id.comments_add_comments);
        ProfileImage=findViewById(R.id.comments_image_profile);
        post=findViewById(R.id.comments_post);

        NotificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");

        Intent intent=getIntent();
        postID=intent.getStringExtra("pid");
        publisherID=intent.getStringExtra("name");

        receiver_userId=intent.getStringExtra("adminNumber");


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addComments.getText().toString().equals(""))
                {
                    Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                }
                else{
                    addtheComments();
                }
            }
        });

        readComments();
    }

    private void addtheComments() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comments", addComments.getText().toString());
        hashMap.put("publisher", Prevelant.currentOnlineUser.getName());
       // hashMap.put("publisher phone", Prevelant.currentOnlineUser.getPhone());



        reference.push().setValue(hashMap);
        addComments.setText("");


        HashMap<String,String> commentsNotificationMap=new HashMap<>();
        commentsNotificationMap.put("from",Prevelant.currentOnlineUser.getName());
        commentsNotificationMap.put("to",receiver_userId);
        //commentsNotificationMap.put("Commentors",Prevelant.currentOnlineUser.getPhone());


        NotificationRef.child(receiver_userId).push()
                .setValue(commentsNotificationMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CommentsActivity.this, "Comment sent", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

        //getting numbers for the post comments



    }



   /* private void getImage()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
               // Users user= dataSnapshot.getValue(Users.class);
                //Glide.with(getApplicationContext()).load(user.getImage()).into(ProfileImage);

                Picasso.get().load(model.getImage()).into(ProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

   private void readComments()
   {
       DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postID);

       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               commentsList.clear();
               for(DataSnapshot snapshot:dataSnapshot.getChildren())
               {
                   Comments comments=snapshot.getValue(Comments.class);
                   commentsList.add(comments);






               }
               commentsAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

   }
}
