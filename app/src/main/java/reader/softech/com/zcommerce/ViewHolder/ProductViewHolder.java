package reader.softech.com.zcommerce.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import reader.softech.com.zcommerce.Interface.ItemeClickListner;
import reader.softech.com.zcommerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductPhone,Comments;
    public ImageView imageView;
    public ItemeClickListner listener;


    public ProductViewHolder(View itemView) {
        super(itemView);

        txtProductPhone=(TextView) itemView.findViewById(R.id.product_phone);
        imageView=(ImageView) itemView.findViewById(R.id.product_image);






        txtProductName=(TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription=(TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice=(TextView) itemView.findViewById(R.id.product_price);
        Comments=(TextView) itemView.findViewById(R.id.product_comments);
    }

    public void setItemClickListner(ItemeClickListner listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);

    }
}
