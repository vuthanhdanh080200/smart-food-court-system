package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class StaffManagement extends AppCompatActivity {
    ListView listUserView;
    FirebaseListAdapter adapter;
    DatabaseReference db;
    String userName="";
    Toolbar toolbar = null;
    Button change, cancel;
    boolean isRemove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_management);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Query query = null;
        if(Common.currentUser.getRole().equals("manager")) {
            query = FirebaseDatabase.getInstance().getReference().child("Duy").child("User");
        }
        else if(Common.currentUser.getRole().equals("vendor owner")){
            query = FirebaseDatabase.getInstance().getReference().child("Duy").child("User").orderByChild("Stall").equalTo(Common.currentUser.getStall());
        }
        listUserView = (ListView) findViewById(R.id.listStaffManagement);
        db=FirebaseDatabase.getInstance().getReference("Duy/User");

        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.user_item)
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseListAdapter<User>(options) {
            protected void populateView(@NonNull View view, @NonNull User user, final int position) {
                TextView txtName = view.findViewById(R.id.txtName);
                txtName.setText("Name: " + user.getName());
                TextView txtRole = view.findViewById(R.id.txtRole);
                txtRole.setText("Role: " + user.getRole());
                TextView txtStall = view.findViewById(R.id.txtStall);
                txtStall.setText("Stall: " + user.getStall());

                Button btnChangeRole = view.findViewById(R.id.btnChangeRole);
                btnChangeRole.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userName = adapter.getRef(position).getKey();
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.child(userName).getValue(User.class);
                                if (!user.getUserName().equals(Common.currentUser.getUserName())) {
                                    if (user.getRole().equals("customer")) {
                                        db.child(userName).child("role").setValue("waitor");
                                        Toast.makeText(StaffManagement.this, "The role has been changed to waitor ", Toast.LENGTH_SHORT).show();
                                    } else if (user.getRole().equals("waitor")) {
                                        db.child(userName).child("role").setValue("cook");
                                        Toast.makeText(StaffManagement.this, "The role has been changed to cook ", Toast.LENGTH_SHORT).show();
                                    } else if (user.getRole().equals("cook")) {
                                        db.child(userName).child("role").setValue("vendor owner");
                                        Toast.makeText(StaffManagement.this, "The role has been changed to vendor owner ", Toast.LENGTH_SHORT).show();
                                    } else if (user.getRole().equals("vendor owner")) {
                                        db.child(userName).child("role").setValue("manager");
                                        Toast.makeText(StaffManagement.this, "The role has been changed to manager ", Toast.LENGTH_SHORT).show();
                                    } else if (user.getRole().equals("manager")) {
                                        db.child(userName).child("role").setValue("it staff");
                                        Toast.makeText(StaffManagement.this, "The role has been changed to itstaff ", Toast.LENGTH_SHORT).show();
                                    } else if (user.getRole().equals("it staff")) {
                                        db.child(userName).child("role").setValue("customer");
                                        Toast.makeText(StaffManagement.this, "The role has been changed to customer ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(StaffManagement.this, "Cannot change", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                });

                Button removeUser = (Button) view.findViewById(R.id.btnRemove);
                removeUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userName = adapter.getRef(position).getKey();
                        openRemoveUser();
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userName = adapter.getRef(position).getKey();
                        openChangeUser();
                    }
                });
            }
        };

        try{
            listUserView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    /*
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    */

    @Override
    protected  void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStaff(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                searchStaff(newQuery);
                return false;
            }
        });

        return true;
    }

    private void searchStaff(String searchText){
        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("User").orderByChild("name").startAt(searchText).endAt(searchText+"\uf8ff");
        listUserView = (ListView) findViewById(R.id.listStaffManagement);

        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.user_item)
                .setQuery(query, User.class)
                .build();

        adapter.stopListening();

        adapter = new FirebaseListAdapter<User>(options) {
            protected void populateView(@NonNull View view, @NonNull User user, final int position) {
                TextView txtName = view.findViewById(R.id.txtName);
                txtName.setText("Name: " + user.getName());
                TextView txtRole = view.findViewById(R.id.txtRole);
                txtRole.setText("Role: " + user.getRole());
                TextView txtStall = view.findViewById(R.id.txtStall);
                txtStall.setText("Stall: " + user.getStall());

                Button btnChangeRole = view.findViewById(R.id.btnChangeRole);
                btnChangeRole.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userName = adapter.getRef(position).getKey();
                        db=FirebaseDatabase.getInstance().getReference("Duy/User");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user=dataSnapshot.child(userName).getValue(User.class);
                                if(user.getRole().equals("customer")){
                                    db.child(userName).child("role").setValue("waitor");
                                    Toast.makeText(StaffManagement.this, "The role has been changed to waitor ", Toast.LENGTH_SHORT).show();
                                }
                                else if(user.getRole().equals("waitor")){
                                    db.child(userName).child("role").setValue("cook");
                                    Toast.makeText(StaffManagement.this, "The role has been changed to cook ", Toast.LENGTH_SHORT).show();
                                }
                                else if(user.getRole().equals("cook")){
                                    db.child(userName).child("role").setValue("vendor owner");
                                    Toast.makeText(StaffManagement.this, "The role has been changed to vendor owner ", Toast.LENGTH_SHORT).show();
                                }
                                else if(user.getRole().equals("vendor owner")){
                                    db.child(userName).child("role").setValue("manager");
                                    Toast.makeText(StaffManagement.this, "The role has been changed to manager ", Toast.LENGTH_SHORT).show();
                                }
                                else if(user.getRole().equals("manager")){
                                    db.child(userName).child("role").setValue("it staff");
                                    Toast.makeText(StaffManagement.this, "The role has been changed to itstaff ", Toast.LENGTH_SHORT).show();
                                }
                                else if(user.getRole().equals("it staff")){
                                    db.child(userName).child("role").setValue("customer");
                                    Toast.makeText(StaffManagement.this, "The role has been changed to customer ", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(StaffManagement.this, "Error change role user ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };

        adapter.startListening();


        try{
            listUserView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_go_home) {
            Intent intent = new Intent(StaffManagement.this, HomeManager.class);
            startActivity(intent);
        }
        else if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openRemoveUser() {

        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.dialog_delete);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        change = (Button) builder.findViewById(R.id.dialog_change);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");
                mDatabase.child(userName).removeValue();
                Toast.makeText(StaffManagement.this, "Remove user successfully!", Toast.LENGTH_SHORT).show();
                builder.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    public void openChangeUser() {
        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.change_user);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        final TextView txtName = (TextView) builder.findViewById(R.id.txtName);
        final TextView txtUserName = (TextView) builder.findViewById(R.id.txtUserName);
        final TextView txtEmailAddress = (TextView) builder.findViewById(R.id.txtEmailAddress);
        final TextView txtPhoneNumber = (TextView) builder.findViewById(R.id.txtPhoneNumber);
        final TextView txtRole = (TextView) builder.findViewById(R.id.txtRole);
        final EditText edtStall = (EditText) builder.findViewById(R.id.edtStall);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Duy");

        mDatabase.child("User").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                txtName.setText(user.getName());
                txtUserName.setText(user.getUserName());
                txtEmailAddress.setText(user.getEmailAddress());
                txtPhoneNumber.setText(user.getPhoneNumber());
                txtRole.setText(user.getRole());
                edtStall.setText(user.getStall());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button btnChange = (Button) builder.findViewById(R.id.dialog_change);
        Button btnCancel = (Button) builder.findViewById(R.id.dialog_cancel);
        final TextView tvNotify = (TextView) builder.findViewById(R.id.tvNotify);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String notifyStall = "";
                        for (DataSnapshot item : dataSnapshot.child("FoodStall").getChildren()) {
                            notifyStall = notifyStall + item.getKey() + "/ ";
                        }

                        for (DataSnapshot item : dataSnapshot.child("FoodStall").getChildren()) {
                            if (!edtStall.getText().toString().equals(item.getKey())) {
                                tvNotify.setText("Stall name must be one of name: " + notifyStall);
                                tvNotify.setMovementMethod(new ScrollingMovementMethod());
                            } else {
                                builder.dismiss();
                                mDatabase.child("User").child(userName).child("stall").setValue(edtStall.getText().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }
}