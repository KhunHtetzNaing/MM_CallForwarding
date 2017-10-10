package com.htetznaing.mmcallforwarding;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edText;
    TextView tView;
    Button btnStart,btnStop;
    String no;
    String code;
    TeleInfo telephonyInfo;
    boolean isSIM1Ready;
    boolean isSIM2Ready;
    boolean isDualSIM;
    Typeface pff;
    AdView Banner;
    InterstitialAd iAd;
    AdRequest adRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        edText = (EditText) findViewById(R.id.edText);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        pff = Typeface.createFromAsset(getAssets(),"pff.ttf");
        tView = (TextView) findViewById(R.id.tView);
        tView.setTypeface(pff);

        telephonyInfo = TeleInfo.getInstance(this);
        isSIM1Ready = telephonyInfo.isSIM1Ready();
        isSIM2Ready = telephonyInfo.isSIM2Ready();
        isDualSIM = telephonyInfo.isDualSIM();

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,"ဖုန္းနံပါတ္တစ္ခုသို႔ ေခၚဆိုလာေသာဖုန္းကို အျခားဖုန္းနံပါတ္တစ္ခုသို႔ လႊဲေျပာင္းေခၚယူေပးႏိုင္ေသာ MM Call Forwarding App ေလးပါ။\n" +
                                "(ဥပမာ ။   ။ သင့္ခ်စ္သူဆီသို႔ေခၚဆိုသမွ်ဖုန္းအားလုံး သင့္ဖုန္းသို႔ဝင္လာေအာင္ လုပ္ေဆာင္ေပးႏိုင္ပါသည္။ )\n" +
                                "\n" +
                                "Download Free at Google Play Store : https://play.google.com/store/apps/details?id=com.htetznaing.mmcallforwarding");
                        startActivity(new Intent(Intent.createChooser(intent,"Call Me Back App")));
            }
        });

        adRequest = new AdRequest.Builder().build();
        Banner = (AdView) findViewById(R.id.adView);
        Banner.loadAd(adRequest);

        iAd = new InterstitialAd(this);
        iAd.setAdUnitId("ca-app-pub-4173348573252986/6731373319");
        iAd.loadAd(adRequest);
        iAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadAD();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                loadAD();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                loadAD();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        showAD();
        switch (v.getId()){
            case R.id.btnStart:
                start();
                break;
            case R.id.btnStop:
                stop();
                break;
        }
    }

    public void start(){
        no = edText.getText().toString();
        code = "*21*"+no+"%23";
        if (!no.isEmpty()) {
            if (no.length() >= 9) {
                if (isDualSIM == true) {
                    chooseSim();
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" +code));
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, 1);
                    }
                    startActivity(intent);
                }
            }else{
                Toast.makeText(MainActivity.this, "Please fill correct Phone Number :)", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "Please fill correct Phone Number :)", Toast.LENGTH_SHORT).show();
        }
    }

    public void stop(){
        code = "%2321*"+no+"%23";
        if (isDualSIM == true) {
            chooseSim();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" +code));
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
            startActivity(intent);
        }
    }

    public void chooseSim(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Please!");
        builder.setMessage("Choose your SIM 1 or 2 ;)");
        builder.setPositiveButton("SIM 1", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simOne();
            }
        });
        builder.setNegativeButton("SIM 2", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simTwo();
            }
        });

        AlertDialog a = builder.create();
        a.show();
    }

    public void simOne(){
        Context context = getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + code));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        for (String s : simSlotName)
            intent.putExtra(s, 0);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        context.startActivity(intent);
    }

    public void simTwo(){
        Context context = getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + code));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        for (String s : simSlotName)
            intent.putExtra(s, 1);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        context.startActivity(intent);
    }

    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };

    public void dev(View view){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("fb://profile/100011339710114"));
            startActivity(intent);
        }catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://m.facebook.com/KHtetzNaing"));
            startActivity(intent);
        }
    }

    public void loadAD(){
        if (!iAd.isLoaded()){
            iAd.loadAd(adRequest);
        }
    }

    public void showAD(){
        if (iAd.isLoaded()){
            iAd.show();
        }else{
            iAd.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to Exit ?");
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image,null);
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAD();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAD();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
