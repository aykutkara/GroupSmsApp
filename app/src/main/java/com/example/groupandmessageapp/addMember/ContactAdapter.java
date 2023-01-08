package com.example.groupandmessageapp.addMember;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupandmessageapp.ContactModel;
import com.example.groupandmessageapp.OnClickItem;
import com.example.groupandmessageapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    List<ContactModel> contactModelList;
    OnClickItem onClickItem;

    public ContactAdapter(List<ContactModel> contactModelList, OnClickItem onClickItem){
        this.contactModelList = contactModelList;
        this.onClickItem = onClickItem;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactViewHolder contactViewHolder = new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addmember_contacts, parent, false), onClickItem);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactModel contactModel = contactModelList.get(position);
        holder.setData(contactModel);
    }

    @Override
    public int getItemCount() {
        return contactModelList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView contactImage;
        OnClickItem onClickItem;
        TextView contactName, contactNumber;
        public ContactViewHolder(View itemView , OnClickItem onClickItem) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.item_addMember_contactImage);
            contactName = itemView.findViewById(R.id.item_addMember_contactName);
            contactNumber = itemView.findViewById(R.id.item_addMember_contactNumber);

            this.onClickItem = onClickItem;
            itemView.setOnClickListener(this);
        }
        public void setData(ContactModel contactModel){
            contactName.setText(contactModel.getName());
            contactNumber.setText(contactModel.getNumber());

            if (contactModel.getPhoto() != null){
                Picasso.get().load(contactModel.getPhoto()).into(contactImage);
            }
        }

        @Override
        public void onClick(View view) {
            onClickItem.onClickItem(getAdapterPosition());
        }
    }
}

