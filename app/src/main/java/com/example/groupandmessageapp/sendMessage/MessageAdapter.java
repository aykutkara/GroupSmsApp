package com.example.groupandmessageapp.sendMessage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupandmessageapp.MessageModel;
import com.example.groupandmessageapp.OnClickItem;
import com.example.groupandmessageapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<MessageModel> messageModelList;
    OnClickItem onClickItem;

    public MessageAdapter(List<MessageModel> messageModelList, OnClickItem onClickItem) {
        this.messageModelList = messageModelList;
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageViewHolder messageViewHolder = new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sendmessage_messages, parent, false), onClickItem);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);
        holder.setData(messageModel);
    }


    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageName, message;

        OnClickItem onClickItem;

        public MessageViewHolder(View itemView, OnClickItem onClickItem) {
            super(itemView);

            messageName = itemView.findViewById(R.id.item_sendMessage_messageTitle);
            message = itemView.findViewById(R.id.item_sendMessage_message);

            this.onClickItem = onClickItem;
            itemView.setOnClickListener(this);
        }

        public void setData(MessageModel messageModel) {
            messageName.setText(messageModel.getMessageName());
            message.setText(messageModel.getMessage());
        }


        @Override
        public void onClick(View view) {
            onClickItem.onClickItem(getAdapterPosition());
        }
    }
}



