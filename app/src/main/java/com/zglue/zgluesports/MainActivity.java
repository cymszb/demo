package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPager mViewPager;
    private PagerTabStrip mPageHeader;
    private BottomNavigationViewExtra mNavigation;
    private BluetoothDataManager bdManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_activity:
                    //mTextMessage.setText(R.string.title_dashboard);
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_health:
                    //mTextMessage.setText(R.string.title_notifications);
                    mViewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_me:
                    //mTextMessage.setText(R.string.title_notifications);
                    mViewPager.setCurrentItem(3);
                    return true;
                default:
                    return false;
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.content_pager);
        mFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mNavigation.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        mNavigation.setSelectedItemId(R.id.navigation_activity);
                        break;
                    case 2:
                        mNavigation.setSelectedItemId(R.id.navigation_health);
                        break;
                    case 3:
                        mNavigation.setSelectedItemId(R.id.navigation_me);
                        break;
                    default:
                        mNavigation.setSelectedItemId(R.id.navigation_home);
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }



        });
        bdManager = BluetoothDataManager.getInstance(this.getApplicationContext());
        //mTextMessage = (TextView) findViewById(R.id.message);
        mNavigation = (BottomNavigationViewExtra) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*
        ArrayList<String> ips = getIpv4Addresses();
        Log.e("Demo","Current active network: " + isConnect(this) + "; interface: " + activeNetwork(this));
        Log.e("Demo","Interface info: " + ips.size() );
        for(String ip:ips){
            Log.e("Demo","ip:" + ip);
        }

        getDefaultNetwork(this);
        */
    }

    @Override
    public void onStop(){
        super.onStop();
        bdManager.disconnect();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bdManager.close();
    }

    @SuppressLint("NewApi")
    private String[] getDefaultNetwork(Context context)  {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


        //LinkProperties network = connectivity.getActiveLinkProperties();
        Network[] allNetworks = connectivity.getAllNetworks();
        for(Network net:allNetworks){
            LinkProperties network = connectivity.getLinkProperties(net);
            String gateway = null;
            for (RouteInfo route : network.getRoutes()) {
                //Log.e("Demo","network gateway: " + route.getGateway().getHostAddress());
                // Currently legacy VPN only works on IPv4.
                if (route.isDefaultRoute() && route.getGateway() instanceof Inet4Address) {
                    gateway = route.getGateway().getHostAddress();
                    Log.e("Demo","default gateway: " + gateway);
                    break;
                }
            }

        }

        return null;
/*
        if (network == null) {
            throw new IllegalStateException("Network is not available");
        }
        String interfaze = network.getInterfaceName();
        if (interfaze == null) {
            throw new IllegalStateException("Cannot get the default interface");
        }
        String gateway = null;
        for (RouteInfo route : network.getRoutes()) {
            // Currently legacy VPN only works on IPv4.
            if (route.isDefaultRoute() && route.getGateway() instanceof Inet4Address) {
                gateway = route.getGateway().getHostAddress();
                break;
            }
        }
        if (gateway == null) {
            throw new IllegalStateException("Cannot get the default gateway");
        }
        return new String[] {interfaze, gateway};
        */
    }


    public static String activeNetwork(Context context){
        String intf = new String("Ignore");
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        intf = info.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Demo","Exception: " +e.getMessage() );
        }

        return intf;
    }

    public static boolean isConnect(Context context) {

        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Demo","Exception: " +e.getMessage() );
        }

        return false;

    }

    public static ArrayList<String> getIpv4Addresses() {
        ArrayList<String> ips = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();  ){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) {
                        ips.add( inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Demo", ex.getMessage());
        }
        return ips;
    }
}
