package apps.dcc.com.dclearning;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private final String __DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/DClearning/";
    private final String __MEDIA = "Media/";
    private final String __CHAT = "Chats/";
    private final String __USER = "User/";
    File rep;
    File rep1;
    File rep2;
    File rep3;

    // input
    private EditText input_search;
    // btn
    private ImageButton btn_search;
    private ImageButton btn_course;
    private ImageButton btn_exercise;
    private ImageButton btn_forums;
    private ImageButton btn_orientation;
    private ImageButton btn_contest;
    private ImageButton btn_quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDirectory();

        // get a reference to the already created main layout
        NavigationView mainLayout = (NavigationView) findViewById(R.id.nav_view);
        // inflate (create) another copy of our custom layout
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.nav_header, mainLayout, false);
        // add our custom layout to the main layout
        mainLayout.addView(myLayout);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_dcc);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();
                    switch (menuItem.getItemId()){
                        case android.R.id.home:
                            return true;
                        case R.id.profile:
                            return true;
                        case R.id.disc:
                            return true;
                        case R.id.Groups:
                            return true;
                    }

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                }
            });
        // get btn and set action on click
        btn_course = (ImageButton) findViewById(R.id.btn_all_courses);
        btn_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCourses(Courses.class);
            }
        });
        btn_quiz = (ImageButton) findViewById(R.id.btn_all_quiz);
        btn_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCourses(Quiz.class);
            }
        });
    }

    public void createDirectory(){
        rep = new File(__DIRECTORY);
        if (!rep.exists()) {
            rep.mkdir();
        }
        rep1 = new File(__DIRECTORY+__MEDIA);
        if (!rep1.exists()) {
            rep1.mkdir();
        }
        rep2 = new File(__DIRECTORY+__CHAT);
        if (!rep2.exists()) {
            rep2.mkdir();
        }
        rep3 = new File(__DIRECTORY+__USER);
        if (!rep3.exists()) {
            rep3.mkdir();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dc, menu);
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (distanceY > 0) {
            // Scrolled upward
            if (e2.getAction() == MotionEvent.ACTION_UP) {
                // The pointer has gone up, ending the gesture
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.file:
                //newGame();
                return true;
            case R.id.ctc:
                //showHelp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Goto course activity
    public void gotoCourses(Class to) {
        // Do something in response to button
        Intent intent = new Intent(this, to);
        String mes="";
        intent.putExtra(EXTRA_MESSAGE, mes);
        startActivity(intent);
    }
}
