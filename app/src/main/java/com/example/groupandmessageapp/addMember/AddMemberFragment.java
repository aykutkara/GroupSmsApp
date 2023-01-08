package com.example.groupandmessageapp.addMember;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupandmessageapp.ContactModel;
import com.example.groupandmessageapp.GroupModel;
import com.example.groupandmessageapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddMemberFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    RecyclerView recyclerView_addMember_groups, recyclerView_addMember_contacts;
    TextView addMember_groupName;

    GroupModel selectedGroup;
    ArrayList<GroupModel> groupModelList;
    ArrayList<ContactModel> contactModelList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_member, container, false);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        recyclerView_addMember_groups = view.findViewById(R.id.recyclerView_addMember_groups);
        recyclerView_addMember_contacts = view.findViewById(R.id.recyclerView_addMember_contacts);
        addMember_groupName = view.findViewById(R.id.addMember_groupName);

        groupModelList = new ArrayList<>();
        contactModelList = new ArrayList<>();

        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGrant -> {
            if (isGrant) {
                fetchContacts();
            } else {
                Toast.makeText(getContext(), "Rehber izni gerekli", Toast.LENGTH_SHORT).show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(Manifest.permission.READ_CONTACTS);
        }
        else {
            fetchContacts();
        }
        fetchGroups();
        return view;
    }
    private void fetchGroups(){
        String userId = mAuth.getCurrentUser().getUid();

        mStore.collection("/users/" + userId + "/groups").get().addOnSuccessListener(queryDocumentSnapshots -> {
            groupModelList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                GroupModel groupModel = new GroupModel(documentSnapshot.getString("grupAdı"), documentSnapshot.getString("grupAciklamasi"),
                        documentSnapshot.getString("grupResmi"), (List<String>)documentSnapshot.get("numaralar"),documentSnapshot.getId());
                groupModelList.add(groupModel);
            }
            recyclerView_addMember_groups.setAdapter(new GroupAdapter(groupModelList,position -> {
                selectedGroup = groupModelList.get(position);
                addMember_groupName.setText("Seçili Grup : "+ selectedGroup.getGroupName());
            }));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_addMember_groups.setLayoutManager(linearLayoutManager);

        });
    }
    private void fetchContacts(){
        Cursor cursor = getContext().getContentResolver().query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        contactModelList.clear();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
            @SuppressLint("Range") String photo = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            ContactModel contactModel = new ContactModel(name, phoneNumber, photo);
            contactModelList.add(contactModel);
        }

        recyclerView_addMember_contacts.setAdapter(new ContactAdapter(contactModelList,position -> {
            ContactModel contactModel = contactModelList.get(position);
            if (selectedGroup != null){
                new AlertDialog.Builder(getContext())
                        .setTitle("Kişiyi Gruba Ekle")
                        .setMessage(contactModel.getName() + " adlı kişiyi " + selectedGroup.getGroupName() + " grubuna eklemek istiyor musunuz?")
                        .setPositiveButton("Evet", (dialog, which) -> {
                            mStore.collection("/users/" + mAuth.getCurrentUser().getUid() + "/groups").document(selectedGroup.getGroupId()).update(new HashMap<String, Object>() {{
                                put("numaralar", FieldValue.arrayUnion(contactModel.getNumber()));
                            }}).addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Kişi Gruba Eklendi", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .setNegativeButton("Hayır", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                Toast.makeText(getContext(), contactModel.getName() + " " + contactModel.getNumber(), Toast.LENGTH_SHORT).show();
            }
        }));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView_addMember_contacts.setLayoutManager(linearLayoutManager);
    }
}