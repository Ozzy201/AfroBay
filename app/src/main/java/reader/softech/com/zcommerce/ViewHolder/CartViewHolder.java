package reader.softech.com.zcommerce.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import reader.softech.com.zcommerce.Interface.ItemeClickListner;
import reader.softech.com.zcommerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductPrice,txtProductQuantity;
    private ItemeClickListner itemeClickListner;

    public CartViewHolder(View itemView)
    {
        super(itemView);
        txtProductName=itemView.findViewById(R.id.cart_product_name);
        txtProductPrice=itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity=itemView.findViewById(R.id.cart_product_quantity);


    }

    @Override
    public void onClick(View view) {

        itemeClickListner.onClick(view,getAdapterPosition(),false);
    }

    public void setItemeClickListner(ItemeClickListner itemeClickListner) {
        this.itemeClickListner = itemeClickListner;
    }
}
