package com.wmiiul.swissknife;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener, SensorEventListener {

    private LocationManager locationManager;

    GoogleMap googleMap;
    LatLng latLng;
    TextView textViewLocation;

    private SensorManager sensorManager;
    private Sensor sensorLight;
    private TextView textViewLight;

    private TextView textViewOrientation;
    private Sensor sensorOrientation;
    private ImageView imgCompass;
    float currentDegree = 0;

    private TextView textViewGyroscope;
    private Sensor sensorGyroscope;
    private float gx,gy,gz;
    public static final float EPSILON = 0.000000001f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgCompass=(ImageView)findViewById(R.id.imgCompass);

        textViewLocation = (TextView)findViewById(R.id.textViewLocation);
        textViewLocation.setText("Location:\n\tNo GPS Signal");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, Float.parseFloat("2"), this);
        }

        if(googleServicesAvaible()){
            initMap();
        }

        textViewLight=(TextView) findViewById(R.id.textViewLight);
        textViewLight.setText("Light Sensor:\n\tNo Data");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        textViewOrientation=(TextView)findViewById(R.id.textViewDegrees);
        textViewOrientation.setText("Roration sensor:\n\tNo Data");
        sensorOrientation=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        textViewGyroscope =(TextView)findViewById(R.id.textViewGyroscope);
        textViewGyroscope.setText("Gyroscope sensor:\n\tNo Data");
        sensorGyroscope =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void onLocationChanged(Location location){
        String msg="Location:\n\t"+location.getLatitude()+"\n\t"+location.getLongitude();
        textViewLocation.setText(msg);
        latLng=new LatLng(location.getLatitude(),location.getLongitude());
        Task task=new Task(googleMap,latLng);
        task.execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        textViewLocation.setText("Location:\n\tNo GPS Signal");
    }

    private void initMap() {
        MapFragment mapFragment=(MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvaible(){
        GoogleApiAvailability api=GoogleApiAvailability.getInstance();
        int isAvaible=api.isGooglePlayServicesAvailable(this);
        if (isAvaible == ConnectionResult.SUCCESS){
            return true;
        }
        else if(api.isUserResolvableError(isAvaible)){
            Dialog dialog=api.getErrorDialog(this, isAvaible,0);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect to play services",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
    }

    public void onClickTrack(View view){
        Intent intent=new Intent(this,TrackActivity.class);
        startActivity(intent);
    }

    private Boolean isFleshlightOn=false;
    public static Camera camera=null;
    public void fleshLight(View view){
        Button buttonFleshlight=(Button)findViewById(R.id.buttonFlashlight);
        Drawable drawable=buttonFleshlight.getBackground();
        if(!isFleshlightOn){
            try {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                    isFleshlightOn=true;
                    drawable.setColorFilter(Color.parseColor("#2db3c9"),PorterDuff.Mode.DARKEN );
                    buttonFleshlight.setBackground(drawable);
                    buttonFleshlight.setTextColor(Color.parseColor("#ffffff"));
                }
            }
            catch (Exception e) {

            }
        }
        else{
            try {
                if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH)) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    isFleshlightOn=false;
                    drawable.setColorFilter(null);
                    buttonFleshlight.setTextColor(Color.parseColor("#000000"));
                }
            }
            catch (Exception e) {

            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor==sensorLight) {
            float lightSensor = event.values[0];
            textViewLight.setText("Light Sensor:\n\t" + lightSensor +" lx");
        }
        else if(event.sensor==sensorOrientation) {
            float degree=Math.round(event.values[0]);
            textViewOrientation.setText("Rotation sensor:\n\t" + Float.toString(degree)+'\u00B0');
            RotateAnimation ra=new RotateAnimation(currentDegree,-degree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            ra.setDuration(120);
            ra.setFillAfter(true);
            imgCompass.startAnimation(ra);
            currentDegree=-degree;

        }
        else if(event.sensor== sensorGyroscope){
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
            float omegaMagnitude = (float)java.lang.Math.sqrt(gx*gx + gy*gy + gz*gz);
            if (omegaMagnitude > EPSILON) {
                gx /= omegaMagnitude;
                gy /= omegaMagnitude;
                gz /= omegaMagnitude;
            }
            String string="Gyroscope sensor:\n\tX: "+event.values[0]+"\n\tY: "+event.values[1]+"\n\tZ: "+event.values[2];
            textViewGyroscope.setText(string);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorOrientation, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
