package io.artcreativity.monpremierprojet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.artcreativity.monpremierprojet.adapters.ProductAdapter;
import io.artcreativity.monpremierprojet.dao.DataBaseHelper;
import io.artcreativity.monpremierprojet.dao.DataBaseRoom;
import io.artcreativity.monpremierprojet.dao.ProductDao;
import io.artcreativity.monpremierprojet.dao.ProductRoomDao;
import io.artcreativity.monpremierprojet.databinding.ActivityProductBinding;
import io.artcreativity.monpremierprojet.entities.Product;

public class ProductActivity extends AppCompatActivity {

    private ActivityProductBinding binding;
    private List<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;
    final static int MAIN_CALL = 120;
    final static int EDIT_SECOND=130;
    private ProductDao productDao;
    private ProductRoomDao productRoomDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        productDao = new ProductDao(this);
        productRoomDao = DataBaseRoom.getInstance(getApplicationContext()).productRoomDao();
        generateProducts();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivityIfNeeded(intent, MAIN_CALL);
            }
        });

//        binding.ourListView.setAdapter(new ArrayAdapter<Product>(this, R.layout.simple_product_item, products.toArray(new Product[]{})));
//        buildSimpleAdapterData();
        buildCustomAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MAIN_CALL) {
            if(resultCode== Activity.RESULT_OK) {
                Log.e("TAG", "onActivityResult: " + data.getSerializableExtra("MY_PROD"));
                // TODO: 18/11/2021 Ajout d'un nouveau produit dans la liste
                Product product = (Product) data.getSerializableExtra("MY_PROD");
                Product product_update= (Product) data.getSerializableExtra("MY_PROD_UPDATE");
//                products= Collections.singletonList((productDao.insert(new Product(product.name, product.description, product.price, product.quantityInStock, product.alertQuantity))));
//                products = productDao.findAll();
//                Thread thread=new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        productRoomDao.insert(product);
//                        productRoomDao.findAll();
//                        runOnUiThread(()->{
//                            products.add(product);
//                            productAdapter.notifyDataSetChanged();
//                        });
//                    }
//                });
//                thread.start();
                if (product!=null){
                    products.add(product);
                    productAdapter.notifyDataSetChanged();
                }
                if (product_update!=null){
                    products.set(products.indexOf(product_update),product_update);
                    productAdapter.notifyDataSetChanged();
                }

                buildCustomAdapter();
            }
        }
        else if (requestCode==EDIT_SECOND){
            if (resultCode==Activity.RESULT_OK){
//                int index= (int) data.getSerializableExtra("object");
//                Toast.makeText(this, "onActivityResult: "+data.getSerializableExtra("production")+"Position:"+index, Toast.LENGTH_SHORT).show();
                Product product= (Product) data.getSerializableExtra("Refresh");
                Product product1_id= (Product) data.getSerializableExtra("product_id");

//                Toast.makeText(getApplicationContext(), "fuctionne bien", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Refresh"+product, Toast.LENGTH_SHORT).show();
                if (product!=null){
                    products.set(products.indexOf(product),product);
                    productAdapter.notifyDataSetChanged();
                }
                else {
                    products.remove(products.indexOf(product1_id));
                    productAdapter.notifyDataSetChanged();
                }

                buildCustomAdapter();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void buildCustomAdapter() {
        productAdapter = new ProductAdapter(this, products);
        binding.ourListView.setAdapter(productAdapter);
        binding.ourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product= (Product) binding.ourListView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Position:"+(position+1)+" Product :"+product, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ProductActivity.this,ProductDetailActivity.class);
                intent.putExtra("PRODUCT",product);
                intent.putExtra("index",position+1);
                startActivityIfNeeded(intent,EDIT_SECOND);

            }
        });
        
        binding.ourListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Product product= (Product) binding.ourListView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "long click", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(ProductActivity.this);
                builder.setNeutralButton("Edietr", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         Intent intent=new Intent(ProductActivity.this,MainActivity.class);
                         intent.putExtra("Editer",product);
                         startActivityIfNeeded(intent,MAIN_CALL);
                    }
                }).setNegativeButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          new Thread(new Runnable() {
                              @Override
                              public void run() {
                                  productRoomDao.delete(product);
                                  runOnUiThread(()->{
                                      products.remove(products.indexOf(product));
                                      productAdapter.notifyDataSetChanged();
                                  });
                              }
                          }).start();

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
                return true;
            }
        });
    }


    private void buildSimpleAdapterData() {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Product product :
                products) {
            Map<String, String> map = new HashMap<>();
            map.put("name", product.name);
            map.put("price", "XOF " + product.price);
            map.put("quantity",  product.quantityInStock + " disponible" +
                    (product.quantityInStock>1 ? "s" : ""));
            mapList.add(map);
        }
        binding.ourListView.setAdapter(new SimpleAdapter(this, mapList, R.layout.regular_product_item,
                new String[]{"name", "quantity", "price"}, new int[]{R.id.name, R.id.quantity_in_stock, R.id.price}));
    }

    private void generateProducts() {
//        products = productDao.findAll();
//        if(products.isEmpty()) {
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
//            productDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//
//            products = productDao.findAll();
//        }

        Thread thread = new Thread(new Runnable() {
            final List<Product> localProducts = new ArrayList<>();
            @Override
            public void run() {
                localProducts.addAll(productRoomDao.findAll());
                if(localProducts.isEmpty()) {
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
                    productRoomDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));

                    localProducts.addAll(productRoomDao.findAll());
                }
                runOnUiThread(()->{
                    products.addAll(localProducts);
                });
            }
        });
        thread.start();
//        products = productRoomDao.findAll();
//        if(products.isEmpty()) {
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
//            productRoomDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//
//            products = productRoomDao.findAll();
//        }
;

    }

}