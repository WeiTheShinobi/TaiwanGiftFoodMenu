package tw.weitheshinobi.taiwangiftfoodmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();
    private String[] from = {"Name","SalePlace","ProduceOrg","SpecAndPrice"};
    private int[] to = {R.id.item_name,R.id.item_sp,R.id.item_so,R.id.item_sap};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        initList();
        queue = Volley.newRequestQueue(this);
        fetchData();
    }

    private void  initList(){
        simpleAdapter = new SimpleAdapter(this,data,R.layout.item,from,to);
        listView.setAdapter(simpleAdapter);
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

                HashMap<String,String> rowData = new HashMap<>();
                rowData.put(from[0],name);
                rowData.put(from[1],salePlace);
                rowData.put(from[2],sroduceOrg);
                rowData.put(from[3],specAndPrice);
                data.add(rowData);
            }
            simpleAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Log.v("test",e.toString());
        }
    }
}