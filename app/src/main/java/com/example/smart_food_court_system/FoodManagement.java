package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.smart_food_court_system.common.Text;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.paperdb.Paper;

public class FoodManagement extends AppCompatActivity {
    ListView listFoodView;
    FirebaseListAdapter adapter;
    FloatingActionButton btnAddFood;
    Toolbar toolbar = null;
    Button btnChoose, btnChangeFood, btnCancel, cancel, change;
    ImageView imageFood;
    Bitmap selectedBitmap;
    String imgeEncoded;
    DatabaseReference mDatabase;
    TextView foodStall;
    String nameFood;
    EditText foodName, foodType, foodDescription, foodPrice, foodRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_management);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAddFood = (FloatingActionButton)findViewById(R.id.fab_add_food);
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodManagement.this, AddFood.class);
                startActivity(intent);
            }
        });

        Query query = FirebaseDatabase.getInstance().getReference().child("Duy")
                .child("FoodStall")
                .child(Common.currentUser.getStall())
                .child("FoodList");

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

    public void openChangeFood(){
        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.change_food);
        builder.setTitle(R.string.dialog_popup);
        builder.show();

        foodStall = (TextView) builder.findViewById(R.id.txtFoodStallName);
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
                        Toast.makeText(FoodManagement.this, Common.changeFoodSuccessMessage, Toast.LENGTH_SHORT).show();
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
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
                searchFood(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                searchFood(newQuery);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_go_home) {
            Intent intent = new Intent(FoodManagement.this, HomeCook.class);
            startActivity(intent);
        }
        else if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchFood(String searchText){
        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Food").orderByKey().startAt(searchText).endAt(searchText+"\uf8ff");
        listFoodView = (ListView) findViewById(R.id.listFoodManagement);

        FirebaseListOptions<Food> options = new FirebaseListOptions.Builder<Food>()
                .setLayout(R.layout.food_item)
                .setQuery(query, Food.class)
                .build();

        adapter.stopListening();

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
            }
        };
        adapter.startListening();


        try{
            listFoodView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
        }
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
                mDatabase.child("FoodStall")
                        .child(Common.currentUser.getStall())
                        .child("FoodList")
                        .child(nameFood).removeValue();
                Toast.makeText(FoodManagement.this, "Remove food successfully!", Toast.LENGTH_SHORT).show();
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