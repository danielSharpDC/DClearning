package apps.dcc.com.dclearning;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class Courses extends AppCompatActivity {
    private static final String TAG = "CoursesActivity";
    private String __COURSE = Environment.getExternalStorageDirectory().getPath() + "/DClearning/Media/Courses/";
    private String __URL = "http://192.168.43.151/projet/Android/getCourses.php";
    private String __PDF_URL = "http://192.168.43.151/projet/cours/";
    private String __PDF_FILE = "";
    private String filePath = "/DClearning/Media/";
    private String fileName ="courses.json";
    private String path = Environment.getExternalStorageDirectory().getPath();

    Dialog dialog;
    TextView pdf_url;
    ImageView imageView;
    ImageView close_dialog_btn;

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private int currentpage =0;

    File course;
    File courseJSON;

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        mListView = (ListView) findViewById(R.id.list_courses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        String subTitle = "All Courses of #DClearning";
        actionbar.setSubtitle(subTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_white);

        course = new File(__COURSE);
        if (!course.exists()) {
            course.mkdir();
        }
        courseJSON = new File(path+filePath+fileName);
        if(!courseJSON.exists()) {
            loadCourses();
        }else{
            WriteObjectFile writeObjectFile = new WriteObjectFile(getApplicationContext());
            JSONArray myJson = writeObjectFile.readObject("Media/"+fileName);
            try {
                afficherListeTweets(myJson.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Dialog Function
        dialog = new Dialog(Courses.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pdf_reader);
        dialog.setCancelable(true);
    }

    private List<ListItem> genererTweets(final String json) throws JSONException {
            List<ListItem> tweets = new ArrayList<ListItem>();
            final JSONArray jProductArray = new JSONArray(json);
            int[] colors = {Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.GRAY};

            for (int i = 0; i < jProductArray.length(); i++) {
                if(i < colors.length) {
                    tweets.add(new ListItem(R.drawable.ic_picture_pdf, jProductArray.optJSONObject(i).optString("c_nom"), jProductArray.optJSONObject(i).optString("c_dep"), jProductArray.optJSONObject(i).optString("c_adress")));
                }else{
                    tweets.add(new ListItem(R.drawable.ic_picture_pdf, jProductArray.optJSONObject(i).optString("c_nom"), jProductArray.optJSONObject(i).optString("c_dep"), jProductArray.optJSONObject(i).optString("c_adress")));
                }
            }
            return tweets;

    }

    public void loadCourses(){ new GetMethodDemo().execute(__URL);}
    private void afficherListeTweets(String response) throws IOException, JSONException {
        String Result = response;
        //Toast.makeText(getApplicationContext(), Result, Toast.LENGTH_LONG).show();
        List<ListItem> tweets = genererTweets(Result);

        ListItemAdaptar adapter = new ListItemAdaptar(Courses.this, tweets);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem item = (ListItem) parent.getItemAtPosition(position);
                __PDF_FILE = __COURSE+item.getPdfFile();
                if(!(new File(__PDF_FILE)).exists())
                    new DownloadFileFromURL().execute(__PDF_URL+item.getPdfFile());
                else
                    dialog_action(__PDF_FILE);
                //Toast.makeText(getApplicationContext(), item.getPdfFile(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dc, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Courses.this.finish();
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

    public class GetMethodDemo extends AsyncTask<String , Void ,String> {
        String server_response, returnVal = "";

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                WriteObjectFile writeObjectFile = new WriteObjectFile(Courses.this);
                JSONArray myJson = new JSONArray(server_response);
                writeObjectFile.writeObject(myJson, filePath, fileName);
                afficherListeTweets(server_response);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(Courses.this, server_response, Toast.LENGTH_LONG).show();
            Log.e("Response", "" + server_response);

        }
    }
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    // Function for Digital Signature
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dialog_action(String pdf) {
        close_dialog_btn = (ImageView)dialog.findViewById(R.id.close_dialog);
        close_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        pdf_url = (TextView) dialog.findViewById(R.id.pdfView);
        pdf_url.setText(pdf);
        render(pdf, dialog);
        dialog.show();
    }

    // Download pdf
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String uri = Uri.parse(f_url[0]).buildUpon().build().toString();
                URL u = new URL(uri);
                InputStream is = u.openStream();

                DataInputStream dis = new DataInputStream(is);

                byte[] buffer = new byte[20971520];
                int length;

                File myFile = new File(__PDF_FILE);
                try {
                    if(!myFile.exists()) {
                        myFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(new File(__PDF_FILE));
                        while ((length = dis.read(buffer))>0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                    else
                        return "";
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            dialog_action(__PDF_FILE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void render(String pdf, Dialog v) {

        try {

            imageView = (ImageView) v.findViewById(R.id.pdf_image);

            int width = imageView.getWidth();
            int height = imageView.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

            File file = new File(pdf);

            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

            if(currentpage<0){
                currentpage =0;

            }else if(currentpage>renderer.getPageCount()){


                currentpage = renderer.getPageCount() -1;

                Matrix matrix = imageView.getImageMatrix();

                Rect rect = new Rect(0,0, width , height);

                renderer.openPage(currentpage).render(bitmap,rect,matrix , PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY );

                imageView.setImageMatrix(matrix);
                imageView.setImageBitmap(bitmap);
                imageView.invalidate();

            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
