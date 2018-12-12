package apps.dcc.com.dclearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Allows the reading/writing of an object from/to a file
 * inside the application's /data directory. Allows for
 * more efficient storing of objects such as arrays instead
 * of within SharedPreferences
 *
 * @author Isaac Whitfield
 * @version 31/07/2013
 */
public class WriteObjectFile {

    private Context parent;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private Object outputObject;
    private JSONArray jsonOutputObject;
    private String filePath;
    private static String repertoire = "DClearning";

    public WriteObjectFile(Context c) {
        parent = c;
    }

    public JSONArray readObject(String fileName) {
        try {
            filePath = Environment.getExternalStorageDirectory() + File.separator + repertoire + File.separator + fileName;
            /*fileIn = new FileInputStream(filePath);
            objectIn = new ObjectInputStream(fileIn);
            outputObject = objectIn.readObject();*/

            BufferedReader input;
            File file = new File(filePath);
            input = new BufferedReader(new FileReader(file));
            String sCurrentLine, fileString = "";

            while ((sCurrentLine = input.readLine()) != null) {
                System.out.println(sCurrentLine);
                fileString += sCurrentLine;
            }
            jsonOutputObject = new JSONArray(fileString);
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonOutputObject;
    }

    public void writeObject(JSONArray inputObject, String filepath, String fileName) {
        try {
            filePath = Environment.getExternalStorageDirectory().getPath()+filepath +fileName;

            Writer output;
            File file = new File(filePath);
            if(!file.exists()) {
                output = new BufferedWriter(new FileWriter(file));
                output.write(inputObject.toString());
                output.close();
            }else {
                WriteObjectFile writeObjectFile = new WriteObjectFile(parent);
                JSONArray destinationArray = writeObjectFile.readObject("Media/"+fileName);
                JSONArray sourceArray = inputObject;
                if(destinationArray.toString() != sourceArray.toString()) {
                    for (int i = 0; i < sourceArray.length(); i++) {
                        destinationArray.put(sourceArray.getJSONObject(i));
                    }
                    output = new BufferedWriter(new FileWriter(file));
                    output.write(destinationArray.toString());
                    output.close();
                }
            }
        } catch (IOException e) {
            Toast.makeText(parent, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    Toast.makeText(parent, e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }
}

