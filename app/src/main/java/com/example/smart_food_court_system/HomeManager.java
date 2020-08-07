package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeManager extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    Button changeProfile, changePassword, logout, cancel;
    Button btnChoose, btnChangeFood, btnCancel, change;
    ListView listFoodView;
    FirebaseListAdapter adapter;
    TextView txtName, txtEmail, txtRole;
    FloatingActionButton btnAddFood;
    ImageView imageFood;
    Bitmap selectedBitmap;
    String imgeEncoded;
    TextView foodStall;

    CircleImageView circleImageView;
    EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    EditText edtName, edtEmailAddress, edtPhoneNumber;
    EditText foodName, foodType, foodDescription, foodPrice, foodRemaining;
    DatabaseReference mDatabase, db;
    String nameFood;
    Food food;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manager);

        Paper.init(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        btnAddFood = (FloatingActionButton)findViewById(R.id.fab_add_food);
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeManager.this, AddFood.class);
                startActivity(intent);
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);

        View headerView = navigationView.getHeaderView(0);
        txtName = (TextView)headerView.findViewById(R.id.txtName);
        txtEmail = (TextView)headerView.findViewById(R.id.txtEmailAddress);
        txtRole = (TextView)headerView.findViewById(R.id.txtRole);

        txtName.setText(Common.currentUser.getName());
        txtEmail.setText(Common.currentUser.getEmailAddress());
        txtRole.setText(Common.currentUser.getRole());

        Query query = null;
        if(Common.currentUser.getRole().equals("manager")) {
            query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Food");
        }
        else if(Common.currentUser.getRole().equals("vendor owner")){
            query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Food").orderByChild("foodStallName").equalTo(Common.currentUser.getStall());
        }

        listFoodView = (ListView) findViewById(R.id.listFoodManagement);

        FirebaseListOptions<Food> options = new FirebaseListOptions.Builder<Food>()
                .setLayout(R.layout.food_management_item)
                .setQuery(query, Food.class)
                .build();

        adapter = new FirebaseListAdapter<Food>(options) {
            protected void populateView(@NonNull View view, @NonNull Food food, final int position) {
                TextView foodName = view.findViewById(R.id.txtFoodName);
                foodName.setText(food.getFoodName());

                TextView foodPrice = view.findViewById(R.id.txtFoodPrice);
                foodPrice.setText(food.getFoodPrice()+" VND");

                ImageView imageFood = view.findViewById(R.id.imageFood);
                byte[] decodedString = Base64.decode(food.getFoodImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageFood.setImageBitmap(decodedByte);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nameFood = adapter.getRef(position).getKey();
                        openChangeFood();
                    }
                });

                Button removeUser = (Button) view.findViewById(R.id.btnRemove);
                removeUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nameFood = adapter.getRef(position).getKey();
                        openRemoveFood();
                    }
                });
            }
        };

        // Now set the adapter with a given layout
        try{
            listFoodView.setAdapter(adapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeCook/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_go_home) {
            return true;
        }
        else if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmailAddress);
        txtName.setText(Common.currentUser.getName());
        txtEmail.setText(Common.currentUser.getEmailAddress());

        int id = item.getItemId();
        if (Common.isConnectedToInternet(getBaseContext())) {
            if (id == R.id.change_profile) {
                openChangeProfile();
            }
            else if (id == R.id.change_password) {
                openChangePassword();
            }
            else if (id == R.id.report) {
                Intent intent = new Intent(HomeManager.this, GenerateReport.class);
                startActivity(intent);
            }
            else if(id == R.id.food_management){
                Toast.makeText(HomeManager.this, "You are in food management!", Toast.LENGTH_SHORT).show();
            }
            else if(id == R.id.staff_management){
                Intent intent = new Intent(HomeManager.this, StaffManagement.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(HomeManager.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.log_out ){
            openDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void openDialog() {

        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.dialogdesign);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        logout = (Button) builder.findViewById(R.id.dialog_ok);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                Common.currentUser = new User();
                HomeManager.super.onBackPressed();
                builder.dismiss();
                Intent loginIntent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(loginIntent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"So you don't want to, Logout !!!",Toast.LENGTH_SHORT).show();
                builder.dismiss();
            }
        });
    }

    public void openChangePassword() {

        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.change_password);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        changePassword = (Button) builder.findViewById(R.id.dialog_change);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        edtOldPassword = (EditText) builder.findViewById(R.id.edtOldPassword);
        edtNewPassword = (EditText) builder.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = (EditText) builder.findViewById(R.id.edtConfirmNewPassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(Common.currentUser.getUserName()).getValue(User.class);
                        if (user.getPassword().equals(edtOldPassword.getText().toString())){
                            if(edtNewPassword.getText().toString().equals(edtConfirmNewPassword.getText().toString())){
                                Toast.makeText(HomeManager.this, Common.changePasswordSuccessMessage, Toast.LENGTH_SHORT).show();
                                user.setPassword(edtNewPassword.getText().toString());
                                Common.currentUser.setPassword(edtNewPassword.getText().toString());
                                EditText password = (EditText)findViewById(R.id.edtPassword);
                                mDatabase.child(Common.currentUser.getUserName()).child("password").setValue(edtNewPassword.getText().toString());
                                builder.dismiss();
                            }
                            else{
                                Toast.makeText(HomeManager.this, Common.confirmPasswordErrorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(HomeManager.this, Common.currentPasswordIsNotCorrectErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    public void openChangeProfile(){
        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.change_profile);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        changeProfile = (Button) builder.findViewById(R.id.dialog_change);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        edtName = (EditText) builder.findViewById(R.id.edtName);
        edtEmailAddress = (EditText) builder.findViewById(R.id.edtEmailAddress);
        edtPhoneNumber = (EditText) builder.findViewById(R.id.edtPhoneNumber);

        edtName.setText("" + Common.currentUser.getName());
        edtEmailAddress.setText("" + Common.currentUser.getEmailAddress());
        edtPhoneNumber.setText("" + Common.currentUser.getPhoneNumber());


        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = new User(
                                edtName.getText().toString(),
                                Common.currentUser.getUserName(),
                                Common.currentUser.getPassword(),
                                edtEmailAddress.getText().toString(),
                                edtPhoneNumber.getText().toString(),
                                Common.currentUser.getRole(),
                                Common.currentUser.getAccountBalance(),
                                Common.currentUser.getStall());

                        if(user.getName().isEmpty() || user.getUserName().isEmpty() || user.getPassword().isEmpty() ||
                                user.getEmailAddress().isEmpty() || user.getPhoneNumber().isEmpty()){
                            Toast.makeText(HomeManager.this, Common.fillAllErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            boolean isEmailAddressExists = false, isPhoneNumberExists = false;
                            for(DataSnapshot item : dataSnapshot.getChildren()){
                                if(user.getEmailAddress().equals(item.child("emailAddress").getValue())){
                                    if(!user.getEmailAddress().equals(Common.currentUser.getEmailAddress())) {
                                        isEmailAddressExists = true;
                                        break;
                                    }
                                }
                            }
                            for(DataSnapshot item : dataSnapshot.getChildren()){
                                if(user.getPhoneNumber().equals(item.child("phoneNumber").getValue())){
                                    if(!user.getPhoneNumber().equals(Common.currentUser.getPhoneNumber())) {
                                        isPhoneNumberExists = true;
                                        break;
                                    }
                                }
                            }
                            if(isEmailAddressExists || isPhoneNumberExists){
                                if(!isPhoneNumberExists){
                                    Toast.makeText(HomeManager.this, Common.emailAddressExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                                else if(!isEmailAddressExists){
                                    Toast.makeText(HomeManager.this, Common.phoneNumberExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(HomeManager.this, Common.emailAddressExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                            //Add to Database
                            else {
                                mDatabase.child(Common.currentUser.getUserName()).setValue(user);
                                Common.currentUser = user;
                                Toast.makeText(HomeManager.this, Common.updateInforSuccessMessage, Toast.LENGTH_SHORT).show();
                                builder.dismiss();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    public void openChangeFood(){
        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.change_food);
        builder.setTitle(R.string.dialog_popup);
        builder.show();

        foodStall = (EditText) builder.findViewById(R.id.txtFoodStallName);
        foodName = (EditText) builder.findViewById(R.id.edtFoodName);
        foodType = (EditText) builder.findViewById(R.id.edtFoodType);
        foodDescription = (EditText) builder.findViewById(R.id.edtFoodDescription);
        foodPrice = (EditText) builder.findViewById(R.id.edtFoodPrice);
        foodRemaining = (EditText) builder.findViewById(R.id.edtFoodRemaining);
        imageFood = (ImageView) builder.findViewById(R.id.imgFood);

        mDatabase = FirebaseDatabase.getInstance().getReference("Duy");
        mDatabase.child("Food").child(nameFood).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                foodStall.setText(food.getFoodStallName());
                foodName.setText(food.getFoodName());
                foodType.setText(food.getFoodType());
                foodDescription.setText(food.getFoodDescription());
                foodPrice.setText(food.getFoodPrice());
                foodRemaining.setText(food.getFoodRemaining());
                byte[] decodedString = Base64.decode(food.getFoodImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageFood.setImageBitmap(decodedByte);
                imgeEncoded = food.getFoodImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnChoose = (Button) builder.findViewById(R.id.btnChoose);
        btnChangeFood = (Button) builder.findViewById(R.id.dialog_change);
        btnCancel = (Button) builder.findViewById(R.id.dialog_cancel);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 200);
            }
        });

        btnChangeFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Food food = new Food(
                                foodStall.getText().toString(),
                                foodName.getText().toString(),
                                foodType.getText().toString(),
                                foodPrice.getText().toString(),
                                foodDescription.getText().toString(),
                                foodRemaining.getText().toString(),
                                imgeEncoded
                                //edtFoodImage.getText().toString()
                        );
                        mDatabase.child("Food").child(nameFood).removeValue();
                        mDatabase.child("Food").child(foodName.getText().toString()).setValue(food);
                        mDatabase.child("FoodStall").child(foodStall.getText().toString()).child("FoodList").child(nameFood).removeValue();
                        mDatabase.child("FoodStall").child(foodStall.getText().toString()).child("FoodList").child(foodName.getText().toString()).setValue(food);
                        Toast.makeText(HomeManager.this, Common.changeFoodSuccessMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                builder.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            try {
                //xử lý lấy ảnh chọn từ điện thoại:
                Uri imageUri = data.getData();
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageFood.setImageBitmap(selectedBitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                imgeEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openRemoveFood() {

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
                mDatabase = FirebaseDatabase.getInstance().getReference("Duy");
                mDatabase.child("Food").child(nameFood).removeValue();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        food = dataSnapshot.child(nameFood).getValue(Food.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mDatabase.child("FoodStall")
                        .child(food.getFoodStallName())
                        .child("FoodList")
                        .child(nameFood).removeValue();
                Toast.makeText(HomeManager.this, "Remove food successfully!", Toast.LENGTH_SHORT).show();
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
}