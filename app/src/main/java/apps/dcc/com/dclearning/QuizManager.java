package apps.dcc.com.dclearning;

import android.content.Context;
import android.os.Environment;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

public class QuizManager {
    private ListView mListView;
    private TextView mTextView;
    private Context context;
    private String[] topic;
    private String[] body;
    private String[] answer;
    private String[] correct;
    private int[] ordreNaturel;
    private int[] alreadyShow;
    private String nom_quiz = "";
    private int iCount = 0;
    private DCRandom dcRand = new DCRandom();
    private String __REP = Environment.getExternalStorageDirectory().getPath() + "/DClearning/Media/Quiz/quiz_";

    public QuizManager(ListView listView, TextView textView, Context c){
        this.mListView = listView;
        this.mTextView = textView;
        this.context = c;
    }
    public void getQuiz(String name){
        this.nom_quiz = name;

        try{
            // This will reference one line at a time
            String line = null;

            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(nom_quiz);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            // Always close files.
            bufferedReader.close();
            Toast.makeText(context, line, Toast.LENGTH_LONG).show();
            String[] quiz = line.split("&");
            this.topic = quiz[0].split("<<");
            this.body = quiz[1].split("<<");
            this.answer = quiz[2].split("<<");
            this.correct = quiz[3].split("<<");
            for(int x=0; x<body.length-1; x++){
                ordreNaturel[x] = dcRand.getRandom(0, body.length-2);
            }
            Toast.makeText(context, topic.toString(), Toast.LENGTH_LONG).show();
            setQuestion();
        }
        catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            System.out.println(e.toString());
        }
    }

    public void setQuestion(){
        if(iCount < ordreNaturel.length){
            int nb = ordreNaturel[iCount];
            if(Arrays.asList(alreadyShow).indexOf(nb) != -1)
                viewQuestion(mListView, mTextView, nb);
            else
                setQuestion();
        }
    }

    public void viewQuestion(ListView listView, TextView textView, int nb){
        textView.setText(body[nb]);
    }

    public class DCRandom{
        private Integer[] tab;
        private Integer[] tabR;
        private int  compt = 0;

        public DCRandom(){}

        public int getRandom(int min, int max){
            int result = 0, amplitude = max - min, __CONST = 10;
            int x;
            // we check that compt is less than amplitude
            // cause after the amplitude, there could be value that repeats itself
            if(this.compt <= amplitude){
                // if tab is empty
                if((Integer)this.tab[0] == null){
                    // we get the current timestamp
                    x = (int) new Date().getTime();
                    result = x % (max -  min + 1) + min;
                    // initialize arrays
                    this.tab[this.compt] = x;
                    this.tabR[this.compt] = result;
                }else{
                    //  else we get the last timestamp that we add a constant
                    x = this.tab[this.compt-1] + __CONST;
                    // we get a number between min and max and different from the last random number
                    result = calcul(x, max, min, this.tabR);
                    // now we are sure that result isn't in tabR
                    // then we fill the arrays
                    this.tab[this.compt] = x;
                    this.tabR[this.compt] = result;
                }

            }
            // we increment the counter
            this.compt++;
            return result;
        }
        public void resetCounter (){
            this.compt = 0;
            this.tab = null;
            this.tabR = null;
        }
        // function recursiv that returns a number between b and a, and different from val
        public int calcul(int temp, int a, int b, Integer[] val){
            int res = temp % (a -  b + 1) + b;
            if(Arrays.asList(val).indexOf(res) != -1){
                return calcul(temp+1, a, b, val);
            }else{
                return res;
            }
        }
    }
}
