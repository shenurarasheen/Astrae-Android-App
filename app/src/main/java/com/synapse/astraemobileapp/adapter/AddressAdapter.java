package com.synapse.astraemobileapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder>{

    List<Address> addresses;
    String permanentAddressId;

    public AddressAdapter(List<Address> addresses, String permanentAddressId) {
        this.addresses = addresses;
        this.permanentAddressId = permanentAddressId;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addresses.get(position);
        holder.title.setText("Address " + (position + 1));
        holder.line1.setText(address.getAddressLine1());
        holder.line2.setText(address.getAddressLine2());
        holder.city.setText(address.getCity());
        holder.postalCode.setText(address.getPostalCode());
        holder.mobile.setText("Tel - " + address.getMobile());

        holder.radioBtn.setVisibility(View.GONE);

        String currentId = address.getAddressId();

        if (permanentAddressId != null && permanentAddressId.equals(currentId)) {
            holder.permanentBadge.setVisibility(View.VISIBLE);
        } else {
            holder.permanentBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView title, line1, line2, city, postalCode, mobile, permanentBadge;
        RadioButton radioBtn;
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_address_title);
            line1 = itemView.findViewById(R.id.item_address_line1);
            line2 = itemView.findViewById(R.id.item_address_line2);
            city = itemView.findViewById(R.id.item_address_city);
            postalCode = itemView.findViewById(R.id.item_address_postal_code);
            mobile = itemView.findViewById(R.id.item_address_mobile);
            radioBtn = itemView.findViewById(R.id.item_address_radio_btn);
            permanentBadge = itemView.findViewById(R.id.item_address_permanent_badge);
        }
    }
}
