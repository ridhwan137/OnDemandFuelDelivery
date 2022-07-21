package my.edu.utem.ftmk.ondemandfueldelivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerOrderRVAdapter extends RecyclerView.Adapter<CustomerOrderRVAdapter.ViewHolder>{
    private ArrayList<Order> orderArrayList;
    private Context context;
    private RecordRVAdapter.ItemClickListener mClickListener;



    // creating constructor for our adapter class
    public CustomerOrderRVAdapter(ArrayList<Order> orderArrayList, Context context) {
        this.orderArrayList = orderArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public  CustomerOrderRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.order_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerOrderRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Order order = orderArrayList.get(position);

        Timestamp firebaseTimestampObject = order.getDate();
        Date javaDate = firebaseTimestampObject.toDate();

        holder.txtOrderNumber.setText(order.getOrderNumber());
        holder.txtCustomerName.setText(order.getFullName());
        holder.txtWorkerName.setText(order.getWorkername());
        holder.txtPetrolStation.setText(order.getPetrolstation());
        holder.txtTypeOfFuel.setText(order.getTypeoffuel());
        holder.txtPricePerUnit.setText("RM"+order.getPriceunit());
        holder.txtTotalPrice.setText("RM"+order.getPrice());
        holder.txtTime.setText(getTime(javaDate));
        holder.txtDate.setText(getDate(javaDate));






    }

    String getDate(Date javaDate) {
        /* Calendar cal = Calendar.getInstance();*/

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String localDate = sdf.format(javaDate);
        Log.d("tngok tarikh", localDate);

        return localDate;
    }

    String getTime(Date javaDate) {
        /* Calendar cal = Calendar.getInstance();*/

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        String localTime = sdf.format(javaDate);

        return localTime;
    }


    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return orderArrayList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.

        TextView txtCustomerName;
        TextView txtOrderNumber;
        TextView txtWorkerName;
        TextView txtPetrolStation;
        TextView txtTypeOfFuel;
        TextView txtPricePerUnit;
        TextView txtTotalPrice, txtDate, txtTime;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            txtOrderNumber = itemView.findViewById(R.id.txtOrderNumber);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtWorkerName = itemView.findViewById(R.id.txtWorkerName);
            txtPetrolStation= itemView.findViewById(R.id.txtPetrolStation);
            txtTypeOfFuel= itemView.findViewById(R.id.txtTypeOfFuel);
            txtPricePerUnit= itemView.findViewById(R.id.txtPricePerUnit);
            txtTotalPrice= itemView.findViewById(R.id.txtTotalPrice);
            txtDate= itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);




        }
       /* @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }*/
    }

/*

    // allows clicks events to be caught
    void setClickListener(CustomerOrderRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
*/

    // convenience method for getting data at click position
    public Order getItem(int id) {
        return orderArrayList.get(id);
    }

}
