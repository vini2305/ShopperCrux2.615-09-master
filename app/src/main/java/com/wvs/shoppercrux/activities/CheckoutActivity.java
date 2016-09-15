package com.wvs.shoppercrux.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.wvs.shoppercrux.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity implements OnItemSelectedListener{

    public Toolbar toolbar;
    private Spinner spinner;
    private TextInputLayout nameLabel,phoneLanel,addressLabel;
    private EditText name,phone,address;
    private Button confirm;
    JsonArrayRequest jsonArrayRequest;
    String GET_LAT_LNG="http://shoppercrux.com/shopper_android_api/getLatLng.php";
    String PINCODE;
    public static String LOCATION_PIN="LocationPin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Book Now");

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        nameLabel = (TextInputLayout) findViewById(R.id.input_name_label);
        phoneLanel = (TextInputLayout) findViewById(R.id.input_phone_label);
        addressLabel = (TextInputLayout) findViewById(R.id.input_address_label);
        confirm = (Button) findViewById(R.id.btn_checkout);

        name = (EditText) findViewById(R.id.input_name);
        phone = (EditText) findViewById(R.id.input_phone);
        address = (EditText) findViewById(R.id.input_address);

        nameLabel.setTypeface(font);
        phoneLanel.setTypeface(font);
        addressLabel.setTypeface(font);
        name.setTypeface(font);
        phone.setTypeface(font);
        address.setTypeface(font);


        spinner = (Spinner) findViewById(R.id.pincode_spinner);
        ArrayAdapter<CharSequence> pinCodes = ArrayAdapter.createFromResource(this,
                R.array.pincode_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        pinCodes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(pinCodes);
        spinner.setOnItemSelectedListener(this);

        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = name.getText().toString().trim();
                String userphone = phone.getText().toString().trim();
                String useraddress = address.getText().toString().trim();
                String pincode = spinner.getSelectedItem().toString();
                Log.d("Pincode","Pin code:"+pincode);

                SharedPreferences preferences= getSharedPreferences(LOCATION_PIN,MODE_PRIVATE);
                String setPinCode =preferences.getString("Pincode",null);
                Log.d("Pincode","Set Pin code:"+setPinCode);
                if(username.isEmpty() || userphone.isEmpty() || useraddress.isEmpty() || pincode.equals("Select Pincode") ){
                    Toast.makeText(getApplicationContext(),
                            "All the fields are mandatory!", Toast.LENGTH_LONG)
                            .show();
                } else if (!isValidPhoneNumber(userphone)){
                    Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
                else if((setPinCode != null) && (!pincode.equals(setPinCode))) {
                Toast.makeText(getApplicationContext(),"You are choosing a different location to deliver",Toast.LENGTH_SHORT).show();
                } else if((setPinCode != null) && (pincode.equals(setPinCode))) {
                    Intent intent = new Intent(CheckoutActivity.this,UserVerify.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void bookMyOrder(String pincode) {

        PINCODE = GET_LAT_LNG+"?pin="+pincode;
        jsonArrayRequest = new JsonArrayRequest(PINCODE, new Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject json = null;
                try {
                    for(int i = 0; i<response.length(); i++) {
                    json = response.getJSONObject(i);
                    String latitude = json.getString("latitude");
                    String longitude = json.getString("longitude");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    public static double calculateDistance( double lat1, double lng1, double lat2, double lng2) {
            int r = 6371; // average radius of the earth in km
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lng2 - lng1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = r * c;
            return d;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
       // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
