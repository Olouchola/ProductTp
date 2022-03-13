package io.artcreativity.monpremierprojet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import io.artcreativity.monpremierprojet.dao.DataBaseRoom;
import io.artcreativity.monpremierprojet.dao.ProductRoomDao;
import io.artcreativity.monpremierprojet.databinding.ActivityProductDetailBinding;
import io.artcreativity.monpremierprojet.entities.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView designation;
    private TextView descriptionView;
    private TextView priceView;
    private TextView quantityView;
    private TextView alertquantityView;

    final static int EDIT_ACTIVITE=140;

    private ProductRoomDao productRoomDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Product product = (Product) getIntent().getSerializableExtra("PRODUCT");
        productRoomDao=DataBaseRoom.getInstance(getApplicationContext()).productRoomDao();

        designation=findViewById(R.id.Name);
        descriptionView=findViewById(R.id.Description);
        priceView=findViewById(R.id.Prix);
        quantityView=findViewById(R.id.Quantite);
        alertquantityView=findViewById(R.id.Alert_quantite);

        setView(product);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Product product = (Product) getIntent().getSerializableExtra("PRODUCT");
        switch (item.getItemId()){
            case R.id.add_item:
                Intent intent=new Intent(ProductDetailActivity.this,MainActivity.class);
                intent.putExtra("Editer",product);
                startActivityIfNeeded(intent,EDIT_ACTIVITE);
                return true;
            case R.id.delete:
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        productRoomDao.delete(product);
                        Intent intent=getIntent();
                        intent.putExtra("product_id",product);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }
                });
                thread.start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==EDIT_ACTIVITE){
            if (resultCode==Activity.RESULT_OK){
                Product product= (Product) data.getSerializableExtra("MY_PROD_UPDATE");
                 setView(product);

                Intent intent=getIntent();
                intent.putExtra("Refresh",product);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        }
    }

    public void setView(Product product){
        designation.setText(product.name);
        descriptionView.setText(product.description);
        priceView.setText(String.valueOf(product.price));
        quantityView.setText(String.valueOf(product.quantityInStock));
        alertquantityView.setText(String.valueOf(product.alertQuantity));
    }

}