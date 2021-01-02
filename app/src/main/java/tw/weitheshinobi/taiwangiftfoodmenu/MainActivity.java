package tw.weitheshinobi.taiwangiftfoodmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;i
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue,imgQueue;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();
    private String[] from = {"Name","SalePlace","ProduceOrg","SpecAndPrice","Column1"};
    private int[] to = {R.id.item_name,R.id.item_sp,R.id.item_so,R.id.item_sap};
    private Dialog dia;
    private ImageView imageView;
    private EditText query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        query = findViewById(R.id.query);

        dia = new Dialog(this);
        dia.setContentView(R.layout.dialog);
        imageView = dia.findViewById(R.id.dialog_img);

        initList();
        queue = Volley.newRequestQueue(this);
        imgQueue = Volley.newRequestQueue(this);
        fetchData();
        initQuery();

    }

    @Override
    protected void onStart() {
        super.onStart();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("農產伴手禮圖鑑").setMessage("點擊項目可顯示圖片\n\n資料來源：行政院農委會");
        builder.show();
    }

    private void initQuery(){
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                simpleAdapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initList(){
        simpleAdapter = new SimpleAdapter(this,data,R.layout.item,from,to);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fetchPhoto(data.get(position).get("Column1"));
                dia.show();
            }
        });
    }

    private void fetchPhoto(String url){
        ImageRequest request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                },
                0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        imgQueue.add(request);
    }

    private void fetchData(){
        StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvAgriculturalProduce.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"網路錯誤",Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(request);
    }

    private void parseJSON(String json){
        data.clear();
        try {
            JSONArray root = new JSONArray(json);
            for(int i = 0; i < root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                String name = row.getString("Name");
                String salePlace = row.getString("SalePlace");
                String sroduceOrg = row.getString("ProduceOrg");
                String specAndPrice = row.getString("SpecAndPrice");
                String column1 = row.getString("Column1");

                HashMap<String,String> rowData = new HashMap<>();
                rowData.put(from[0],name);
                rowData.put(from[1],salePlace);
                rowData.put(from[2],sroduceOrg);
                rowData.put(from[3],specAndPrice);
                rowData.put(from[4],column1);
                data.add(rowData);
            }
            simpleAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"網路錯誤",Toast.LENGTH_LONG).show();
        }
    }
}