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
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static java.security.AccessController.getContext;

public class Quiz extends AppCompatActivity {
    private static final String TAG = "CoursesActivity";
    private String __QUIZ = Environment.getExternalStorageDirectory().getPath() + "/DClearning/Media/Quiz/";
    private String __URL = "http://192.168.43.151/projet/Android/getListQuiz.php";
    private String __QUIZ_URL = "http://192.168.43.151/projet/Android/getQuiz.php";
    private String __QUIZ_FILE = "";
    private String filePath = "/DClearning/Media/";
    private String fileName ="quiz_";
    private String path = Environment.getExternalStorageDirectory().getPath();

    Dialog dialog;
    TextView quiz_name;
    ListView list_quiz_answer;
    ImageView close_dialog_btn;

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private int currentpage =0;

    File quiz;
    File quizJSON;

    ListView mListView;
    ListView iListView;
    ListView pListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mListView = (ListView) findViewById(R.id.list_quiz_maths);
        iListView = (ListView) findViewById(R.id.list_quiz_infos);
        pListView = (ListView) findViewById(R.id.list_quiz_physcs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        String subTitle = "All Quiz of #DClearning";
        actionbar.setSubtitle(subTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_white);

        quiz = new File(__QUIZ);
        if (!quiz.exists()) {
            quiz.mkdir();
        }
        quizJSON = new File(path+filePath+fileName+"maths.json");
        if(!quizJSON.exists()) {
             try {
                 JSONObject dataM = new JSONObject();
                 dataM.put("dep", "Mathematiques");

                 JSONObject dataI = new JSONObject();
                 dataI.put("dep", "Informatique");

                 JSONObject dataP = new JSONObject();
                 dataP.put("dep", "Physique");
                 loadQuiz(dataM, mListView, "maths.json");
                 loadQuiz(dataI, iListView, "infos.json");
                 loadQuiz(dataP, pListView, "physics.json");
             }catch (JSONException e){
                 e.printStackTrace();
             }
        }else{
            WriteObjectFile writeObjectFile = new WriteObjectFile(getApplicationContext());
            JSONArray myJsonM = writeObjectFile.readObject("Media/"+fileName+"maths.json");
            JSONArray myJsonI = writeObjectFile.readObject("Media/"+fileName+"infos.json");
            JSONArray myJsonP = writeObjectFile.readObject("Media/"+fileName+"physics.json");
            try {
                afficherListeTweets(myJsonM.toString(), mListView, "maths.json");
                afficherListeTweets(myJsonI.toString(), iListView, "infos.json");
                afficherListeTweets(myJsonP.toString(), pListView, "physics.json");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Dialog Function
        dialog = new Dialog(Quiz.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quiz);
        dialog.setCancelable(true);
    }

    private List<ListItem> genererTweets(final String json) throws JSONException {
        List<ListItem> tweets = new ArrayList<ListItem>();
        final JSONArray jProductArray = new JSONArray(json);
        int[] colors = {Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.GRAY};

        for (int i = 0; i < jProductArray.length(); i++) {
            if(i < colors.length) {
                tweets.add(new ListItem(R.drawable.ic_assignment_green, jProductArray.optJSONObject(i).optString("name"), jProductArray.optJSONObject(i).optString("niveau"), jProductArray.optJSONObject(i).optString("name")));
            }else{
                tweets.add(new ListItem(R.drawable.ic_assignment_green, jProductArray.optJSONObject(i).optString("name"), jProductArray.optJSONObject(i).optString("niveau"), jProductArray.optJSONObject(i).optString("name")));
            }
        }
        return tweets;

    }

    public void loadQuiz(JSONObject json, ListView view, String str){ new SendPostRequest(json, view, str).execute(__URL);}
    private void afficherListeTweets(String response, ListView listView, String str) throws IOException, JSONException {
        String Result = response;
        //Toast.makeText(getApplicationContext(), Result, Toast.LENGTH_LONG).show();
        List<ListItem> tweets = genererTweets(Result);

        ListItemAdaptar adapter = new ListItemAdaptar(Quiz.this, tweets);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem item = (ListItem) parent.getItemAtPosition(position);
                __QUIZ_FILE = __QUIZ+item.getPdfFile();
                if(!(new File(__QUIZ_FILE)).exists()) {
                    try {
                        JSONObject dataP = new JSONObject();
                        dataP.put("op", "0");
                        dataP.put("name", item.getPdfFile());
                        new DownloadFileFromURL(dataP).execute(__QUIZ_URL);
                    }catch (JSONException e){}
                }
                else
                    dialog_action(__QUIZ_URL);
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
                Quiz.this.finish();
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


    public void dialog_action(String path) {
        close_dialog_btn = (ImageView)dialog.findViewById(R.id.close_dialog);
        close_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        quiz_name = (TextView) dialog.findViewById(R.id.quiz_title);
        list_quiz_answer = (ListView) dialog.findViewById(R.id.list_quiz_answer);
        String[] nameTab = path.split("/");
        String name = nameTab[nameTab.length-1];
        quiz_name.setText(name.split("\\.")[0]);
        QuizManager quizManager = new QuizManager(list_quiz_answer, quiz_name, getApplicationContext());
        quizManager.getQuiz(path);
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
        private JSONObject params;
        public DownloadFileFromURL(JSONObject json){
            this.params = json;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(arg0[0]); // here is your URL path

                Log.e("params",params.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

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
        protected void onPostExecute(String result) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            //File saveQ = new File(__QUIZ_FILE+".txt");

            try {
                String json = "{\"quiz\":"+result+"}";
                JSONObject obj = new JSONObject(json);

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(__QUIZ_FILE+".txt"));
                bufferedWriter.write(obj.get("quiz").toString());
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                try {
                    String val = e.toString();
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(__QUIZ_FILE+".txt"));
                    bufferedWriter.write(val);
                    bufferedWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            dialog_action(__QUIZ_FILE+".txt");
        }

    }



    public class SendPostRequest extends AsyncTask<String, Void, String> {
        private JSONObject postDataParams;
        private ListView listView;
        private String file_n;

        public SendPostRequest(JSONObject params, ListView listV, String str){
            this.postDataParams = params;
            this.listView = listV;
            this.file_n = str;
        }

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(arg0[0]); // here is your URL path

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WriteObjectFile writeObjectFile = new WriteObjectFile(Quiz.this);
                JSONArray myJson = new JSONArray(result);
                writeObjectFile.writeObject(myJson, filePath, fileName+file_n);
                afficherListeTweets(result, listView, file_n);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
