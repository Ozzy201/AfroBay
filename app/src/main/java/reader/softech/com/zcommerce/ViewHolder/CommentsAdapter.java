package reader.softech.com.zcommerce.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.text.BreakIterator;
import java.util.List;

import reader.softech.com.zcommerce.HomeActivity;
import reader.softech.com.zcommerce.Model.Comments;
import reader.softech.com.zcommerce.Model.Users;
import reader.softech.com.zcommerce.Prevelant.Prevelant;
import reader.softech.com.zcommerce.R;
import reader.softech.com.zcommerce.SettingsActivity;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.Viewholder>{

    private Context mContext;
    private List<Comments> mComments;

    private FirebaseUser firebaseUser;



    public CommentsAdapter(Context mContext, List<Comments> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,viewGroup, false);

        return new CommentsAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder viewholder, int i) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comments comments = mComments.get(i);

        viewholder.commentme.setText(comments.getComments());
        getUserInfo(viewholder.image_profile, viewholder.username,comments.getPublisher());

        viewholder.commentme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherid",comments.getPublisher());
                mContext.startActivity(intent);

            }
        });


        viewholder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherid",comments.getPublisher());
                mContext.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{


        public ImageView image_profile;
        public TextView username,commentme;

        public Viewholder(View itemView) {
            super(itemView);

            image_profile=itemView.findViewById(R.id.user_profile_comment);
            username=itemView.findViewById(R.id.username_Comment);
            commentme=itemView.findViewById(R.id.comment);


        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, final String publisherId)
    {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                Users users=dataSnapshot.getValue(Users.class);
               // Picasso.get().load(SettingsActivigetImage).into(imageView);
                username.setText(publisherId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
