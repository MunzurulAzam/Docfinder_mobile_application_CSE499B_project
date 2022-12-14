package com.nearbydoctor.doctorfinder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nearbydoctor.MainActivity_Ai;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.nearbydoctor.BuildConfig;
import com.nearbydoctor.R;
import com.nearbydoctor.utils.GPSTracker;
import com.nearbydoctor.utils.LanguageSelectore;

import static com.nearbydoctor.utils.AdMobIntegration.loadAdmobInterstial;
import static com.nearbydoctor.utils.AdMobIntegration.shouldDisplayAds;
import static com.nearbydoctor.utils.AdMobIntegration.showAdmobInterstial;


public class Home extends AppCompatActivity {

    private static final String MyPREFERENCES = "DoctorPrefrance";
    private static final String CityId1 = "CityID";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static Typeface TF_ralewayRegular;
    public static Typeface TF_opensansRegular;
    private static Typeface TF_opensansBold;
    private static Typeface TF_ralewayMidium;
    public static Typeface TF_ralewaybold;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProgressDialog progressDialog;
    private String cityID;
    private String cityName;
    private InterstitialAd interstialAd;
    private Button button;


    private void errorDialog(final Activity activity) {

        final Dialog dialog = new Dialog( activity );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setCancelable( false );
        dialog.setContentView( R.layout.error_dialog );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        Button btn_ok = dialog.findViewById( R.id.btn_ok );

        TextView txt_error_description = dialog.findViewById( R.id.txt_error_description );
        TextView txt_dialog_title = dialog.findViewById( R.id.txt_dialog_title );

        txt_error_description.setText( getString( R.string.internet_message ) );

        txt_error_description.setTypeface( TF_ralewayRegular );
        txt_dialog_title.setTypeface( TF_ralewayRegular );
        btn_ok.setTypeface( TF_ralewayRegular );

        btn_ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        LanguageSelectore.setLanguageIfAR( Home.this );
        setContentView( R.layout.activity_home );


//        start
        button = (Button) findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 openActivity();
            }
        });
//        end

        MobileAds.initialize( this, "ca-app-pub-3940256099942544~3347511713" );
        FirebaseApp.initializeApp( this );
        FirebaseInstanceId.getInstance().getInstanceId();
        TF_opensansBold = Typeface.createFromAsset( Home.this.getAssets(), "fonts/OpenSans-Bold.ttf" );
        TF_ralewayMidium = Typeface.createFromAsset( Home.this.getAssets(), "fonts/Raleway-Medium_2.ttf" );
        TF_ralewayRegular = Typeface.createFromAsset( Home.this.getAssets(), "fonts/Raleway-Regular_4.ttf" );
        TF_opensansRegular = Typeface.createFromAsset( Home.this.getAssets(), "fonts/OpenSans-Regular.ttf" );
        TF_ralewaybold = Typeface.createFromAsset( Home.this.getAssets(), "fonts/Raleway-Bold_0.ttf" );

        /*try {
            PackageInfo info;
            info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
        //get Firebase Id;
/*
        byte[] sha1={(byte) 0xE8,0x0C,0x27, (byte) 0xA6, (byte) 0xAB, (byte) 0xFA, (byte) 0x9C, (byte) 0xAE,0x6E, (byte) 0xD0,0x05,0x4C, (byte) 0x9D, (byte) 0xED, (byte) 0xA6, (byte) 0xA1, (byte) 0x9B, (byte) 0xE2,0x39,0x00};
        Log.e( "Hashkey", "onCreate: "+ "keyhashSignIn:" + Base64.encodeToString(sha1, Base64.NO_WRAP ) );*/

        byte[] sha1 = {0x15, 0x4F, 0x42, 0x35, 0x57, (byte) 0x8A, (byte) 0xA0, (byte) 0x80, 0x2E, (byte) 0xB5, 0x16, 0x25, 0x22, 0x62, (byte) 0xC5, 0x44, 0x7F, (byte) 0xE8, (byte) 0xC8, 0x07};
        Log.e( "Hashkey", "onCreate:dfsfsfd " + "FacebooksignIn " + Base64.encodeToString( sha1, Base64.NO_WRAP ) );
        FacebookSdk.sdkInitialize( getApplicationContext() );
        AppEventsLogger.activateApp( this );
        BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals( Config.REGISTRATION_COMPLETE )) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic( Config.TOPIC_GLOBAL );

                    displayFirebaseRegId();

                }

            }
        };
        displayFirebaseRegId();


        createSideMenu();

        // SET LAYOUT CREATE BUTTONS

        setLayout();

        // LOCATION PERMISSION

        if (checkLocationPermission()) {
            gpsgetlocation();
        }


        GetDefaultNamefromServer();
    }

    private void GetDefaultNamefromServer() {
        SharedPreferences sharedpreferences = Home.this.getSharedPreferences( MyPREFERENCES, Context.MODE_PRIVATE );
        String CityId = sharedpreferences.getString( CityId1, null );
        if (CityId == null) {
            new GetDataAsyncTask().execute();
        }

        //initial adMobSdk
        MobileAds.initialize( Home.this, getString( R.string.adMobId ) );

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Load Interstial AD
        Boolean ad = shouldDisplayAds( Home.this );
        if (ad && isOnline()) interstialAd = loadAdmobInterstial( Home.this );
    }

    public class GetDataAsyncTask extends AsyncTask <Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog( Home.this );
            progressDialog.setMessage( getString( R.string.loading ) );
            progressDialog.setCancelable( true );
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            try {

                hp = new URL( getString( R.string.link ) + "city.php" );
                Log.d( "URL", "" + hp );

                // URL Connection

                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                Log.d( "input", "" + input );
                BufferedReader r = new BufferedReader( new InputStreamReader( input ) );
                String x;
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append( x );
                    x = r.readLine();
                }
                Log.d( "URL", "" + total );

                // Json Parsing
                JSONArray jObject = new JSONArray( total.toString() );
                Log.d( "URL12", "" + jObject );
                JSONObject Obj;
                Obj = jObject.getJSONObject( 0 );
                Log.e( "Obj", Obj.toString() );
                JSONArray jarr = Obj.getJSONArray( "Cities" );
                JSONObject Obj1;
                Obj1 = jarr.getJSONObject( 0 );
                Log.e( "Obj1", Obj1.toString() );
                cityID = Obj1.getString( "id" );
                cityName = Obj1.getString( "name" );

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (NullPointerException e) {
                // TODO: handle exception
                e.printStackTrace();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute( aVoid );
            progressDialog.dismiss();

            if (cityID != null) {
                SharedPreferences sharedpreferences = getSharedPreferences( MyPREFERENCES, MODE_PRIVATE );
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString( "CityName", cityName );
                editor.putString( "CityID", cityID );
                editor.apply();
            }
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences( Config.SHARED_PREF, 0 );
        String regId = pref.getString( "regId", null );
        //new PostDataAsyncTask().execute();
        Log.e( "RegID: ", "" + regId );
        if (pref.getBoolean( "firstrun", true )) {
            if (regId != null) {
                postRegistrationId( regId );
            }
            pref.edit().putBoolean( "firstrun", false ).apply();
        }
    }

    private void postRegistrationId(final String registationid) {
        String postRegId = getString( R.string.link ) + "tokendata.php";
        RequestQueue queue = Volley.newRequestQueue( Home.this );
        StringRequest sr = new StringRequest( Request.Method.POST, postRegId, new Response.Listener <String>() {
            @Override
            public void onResponse(String response) {
                Log.e( "Respone", response );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e( "Respone", error.getMessage() );
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } ) {
            @Override
            protected Map <String, String> getParams() {
                Map <String, String> params = new HashMap <String, String>();
                params.put( "device_id", registationid );
                params.put( "device-Type", "android" );
                return params;
            }

           /* @Override
            public Map <String, String> getHeaders() throws AuthFailureError {
                Map <String, String> params = new HashMap <String, String>();
                params.put( "Content-Type", "application/x-www-form-urlencoded" );
                return params;
            }*/
        };
        queue.add( sr );
    }

    private void createSideMenu() {
        Button btn_logout = findViewById( R.id.logout );
        TextView txt_name = findViewById( R.id.txt_name );
        ImageView img_profile = findViewById( R.id.img_profile );
        SharedPreferences prefs = getSharedPreferences( MyPREFERENCES, MODE_PRIVATE );

        if (!isOnline()) {
            errorDialog( Home.this );
        }

        if (prefs.getString( "userid", null ) != null) {
            String uservalue = prefs.getString( "userid", null );
            if (uservalue.equals( "delete" )) {
                img_profile.setImageResource( R.drawable.profile );
                btn_logout.setVisibility( View.GONE );
                txt_name.setText( getString( R.string.sign_in_txt ) );
                txt_name.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iv = new Intent( Home.this, Login.class );
                        startActivity( iv );
                    }
                } );
            } else {
                String image = prefs.getString( "picture", "" );
                Log.e( "Image", "createSideMenu: " + image );
                String click = prefs.getString( "type", "" );
                String uname = prefs.getString( "username", "" );
                uname = uname.replace( "%20", " " );
                btn_logout.setVisibility( View.VISIBLE );
                btn_logout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = new Dialog( Home.this );
                        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                        dialog.setCancelable( false );
                        dialog.setContentView( R.layout.alertdialog );

                        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                        TextView txt_dialog_title = dialog.findViewById( R.id.txt_dialog_title );
                        TextView errormsg = dialog.findViewById( R.id.txt_error_description );
                        txt_dialog_title.setText( getString( R.string.logout_title ) );
                        errormsg.setText( getString( R.string.logout_message ) );
                        errormsg.setTypeface( TF_ralewayRegular );
                        txt_dialog_title.setTypeface( TF_opensansRegular );

                        Button btn_yes = dialog.findViewById( R.id.btn_yes );
                        Button btn_no = dialog.findViewById( R.id.btn_no );
                        btn_yes.setTypeface( TF_ralewayRegular );
                        btn_no.setTypeface( TF_ralewayRegular );

                        btn_yes.setText( getString( R.string.button_yes ) );
                        btn_no.setText( getString( R.string.button_no ) );

                        btn_no.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        } );

                        btn_yes.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String userid = "delete";
                                SharedPreferences.Editor editor = getSharedPreferences( MyPREFERENCES, MODE_PRIVATE ).edit();
                                editor.putString( "userid", "" + userid );
                                editor.apply();
                                Login.logout();
                                Intent iv = new Intent( Home.this, Home.class );
                                startActivity( iv );
                            }
                        } );
                        dialog.show();
                    }
                } );


                if (click.equals( "signin" )) {
                    Picasso.with(getApplicationContext()).load( getString( R.string.link1 ) + "uploads/" + image ).into( img_profile );
                } else if (click.equals( "fb" )) {
                    Picasso.with(getApplicationContext()).load( "https://"+image ).into( img_profile );

                } else {
                    Picasso.with(getApplicationContext()).load( getString( R.string.link1 ) + "uploads/" + image ).into( img_profile );
                }
                txt_name.setText( uname );
            }
        } else {
            img_profile.setImageResource( R.drawable.profile );
            btn_logout.setVisibility( View.GONE );
            txt_name.setText( getString( R.string.sign_in_txt ) );
            txt_name.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iv = new Intent( Home.this, Login.class );
                    startActivity( iv );
                }
            } );
        }

        //drawer
        TextView txt_profile = findViewById( R.id.txt_profile );
        txt_profile.setTypeface( TF_ralewayMidium );

        txt_name.setTypeface( TF_opensansRegular );
        TextView txt_home = findViewById( R.id.txt_favorite );
        txt_home.setTypeface( TF_opensansRegular );
        TextView txt_fav = findViewById( R.id.txt_about );
        txt_fav.setTypeface( TF_opensansRegular );
        TextView txt_about = findViewById( R.id.txt_terms );
        txt_about.setTypeface( TF_opensansRegular );
        TextView txt_social = findViewById( R.id.txt_terms1 );
        txt_social.setTypeface( TF_opensansRegular );
        TextView txt_settings = findViewById( R.id.txt_cart );
        txt_settings.setTypeface( TF_opensansRegular );

        // SET FONT TO ALL TEXTVIEW
        mDrawerLayout = findViewById( R.id.drawer_layout );
        mDrawerLayout.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );
        String mActivityTitle = getTitle().toString();
        setupDrawer();
        drawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( true );
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled( true );
        actionBar.setDisplayShowHomeEnabled( false );
        actionBar.setDisplayShowCustomEnabled( true );
        actionBar.setCustomView( R.layout.homeheader );
        Bitmap bitmap = BitmapFactory.decodeResource( Home.this.getResources(), R.drawable.title_fragmentbg );
        Drawable drawable = new BitmapDrawable( getResources(), bitmap );
//        BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.title_fragmentbg));
        actionBar.setBackgroundDrawable( drawable );
        setfont();

    }


    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale( this, android.Manifest.permission.ACCESS_FINE_LOCATION )) {


                new AlertDialog.Builder( this ).setTitle( R.string.alertmsg ).setMessage( R.string.alert_msg2 ).setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions( Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION );
                    }
                } ).create().show();
            } else {
                ActivityCompat.requestPermissions( this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION );
            }
            return false;
        } else {
            return true;
        }
    }

    private void gpsgetlocation() {
        GPSTracker gps = new GPSTracker( Home.this );
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private void drawer() {
        LinearLayout ll_home = findViewById( R.id.ll_homen );
        ll_home.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent( Home.this, Home.class );
                startActivity( iv );
            }
        } );

        LinearLayout ll_fav = findViewById( R.id.ll_fav );
        ll_fav.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent( Home.this, Favorite.class );
                startActivity( iv );
            }
        } );

        LinearLayout ll_social = findViewById( R.id.ll_social );
        ll_social.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent( Intent.ACTION_SEND );
                String text1 = getString( R.string.sharemessage );
                String text2 = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                /*   String text2 = getString( R.string.shared_via );*//*+BuildConfig.APPLICATION_ID +"\n\n";*/
                intent.setType( "text/plain" );
                intent.putExtra( android.content.Intent.EXTRA_TEXT, text1 + "\n" + text2 );
                startActivity( Intent.createChooser( intent, getString( R.string.shareapp_txt ) ) );
            }
        } );

        LinearLayout ll_about = findViewById( R.id.ll_terms );
        ll_about.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent( Home.this, Aboutus.class );
                startActivity( iv );
            }
        } );

        LinearLayout ll_setting = findViewById( R.id.ll_setting );
        ll_setting.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent( Home.this, Setting.class );
                startActivity( iv );
            }
        } );
    }

    private void setLayout() {

        RelativeLayout rel_doctor = findViewById( R.id.rel_doctor );
        RelativeLayout rel_pharmacy = findViewById( R.id.rel_pharmacy );
        RelativeLayout rel_hospital = findViewById( R.id.rel_hospital );

        rel_doctor.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iv = new Intent( Home.this, DoctorSpeciality.class );
                iv.putExtra( "doctor_id", "1" );
                startActivity( iv );
                if (shouldDisplayAds( Home.this )) showAdmobInterstial( interstialAd );

            }
        } );
        rel_pharmacy.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent( Home.this, DoctorList.class );
                iv.putExtra( "doctor_id", "2" );
                startActivity( iv );
                if (shouldDisplayAds( Home.this )) showAdmobInterstial( interstialAd );
            }
        } );
        rel_hospital.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent( Home.this, DoctorList.class );
                iv.putExtra( "doctor_id", "3" );
                startActivity( iv );
                if (shouldDisplayAds( Home.this )) showAdmobInterstial( interstialAd );
            }
        } );
    }

    private void setfont() {

        TextView text1 = findViewById( R.id.text1 );
        TextView text2 = findViewById( R.id.text2 );

        TextView txt_doctorname = findViewById( R.id.txt_doctorname );
        TextView txt_doctordesc = findViewById( R.id.txt_doctordesc );
        TextView txt_hospitalname = findViewById( R.id.txt_hospitalname );
        TextView txt_hospitaldesc = findViewById( R.id.txt_hospitaldesc );
        TextView txt_pharmacyname = findViewById( R.id.txt_pharmacyname );
        TextView txt_pharmacydesc = findViewById( R.id.txt_pharmacydesc );

        text1.setTypeface( TF_opensansBold );

        text2.setTypeface( TF_opensansRegular );
        txt_doctordesc.setTypeface( TF_opensansRegular );
        txt_hospitalname.setTypeface( TF_opensansRegular );
        txt_hospitaldesc.setTypeface( TF_opensansRegular );
        txt_pharmacyname.setTypeface( TF_opensansRegular );
        txt_pharmacydesc.setTypeface( TF_opensansRegular );
        txt_doctorname.setTypeface( TF_opensansRegular );
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close ) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened( drawerView );
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed( view );
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled( true );
        mDrawerLayout.setDrawerListener( mDrawerToggle );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate( savedInstanceState );
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged( newConfig );
        mDrawerToggle.onConfigurationChanged( newConfig );
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

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected( item )) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        gpsgetlocation();
                    }

                } else {
                    checkLocationPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


//    start
    public void openActivity(){
        Intent intent = new Intent(this, MainActivity_Ai.class);
         startActivity(intent);
    }
//    end


}
