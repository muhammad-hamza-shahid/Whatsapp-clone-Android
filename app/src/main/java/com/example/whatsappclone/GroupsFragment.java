package com.example.whatsappclone;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfGroups = new ArrayList<>();

    private DatabaseReference GroupRef;
    public GroupsFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);


        //To access the database and show the names of groups
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        InitializeFields();

        RetrieveAndDisplay();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String currentGoupName= adapterView.getItemAtPosition(position).toString();

                Intent GroupChatIntent= new Intent(getContext(),GroupChatActivity.class);
                GroupChatIntent.putExtra("groupName",currentGoupName);
                startActivity(GroupChatIntent);
            }
        });

        return groupFragmentView;
    }

    private void RetrieveAndDisplay() {

        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //A hashSet is made to store all the group name from the database and then show them in the list view
                Set<String> set = new HashSet<>();

                //putting the node in iterator
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext())
                {
                    //add the values from databse into HashSet //only the key is being fetchedd not the value
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                //Clear the list first so no repetition occures when a new group name is added on run time
                listOfGroups.clear();

                //Adding the HashSet in the list
                listOfGroups.addAll(set);

                //Notifying the adapter that data is changed
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void InitializeFields() {

        listView = groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter= new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,listOfGroups);
        listView.setAdapter(arrayAdapter);

    }


}
