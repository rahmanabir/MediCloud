package sks.jakfromspace.medicloud;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Coded by JAKfromSpace on 11-Nov-17 for Medicloud.
 */

public class BackgroundProcess extends AsyncTask<String, String, String[][]> {

    Context context;
    String type;

    Intent newIntent;
    boolean loginSuccess = false;

    public BackgroundResponse delegate = null;
    BackgroundProcess (Context c){
        context = c;
    }


    @Override
    protected String[][] doInBackground(String... params) {

        type = params[0];
        String loginURL = "http://192.168.0.109/MEDICLOUD/login.php";
        String registerURL = "http://sks.heliohost.org/register.php";
        String getDocURL = "http://192.168.0.109/MEDICLOUD/listdocs.php";

        if(type.equals("login")) try {
            String username = params[1];
            String password = params[2];
            URL url = new URL(loginURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String postdata = URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+"&"
                    +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
            writer.write(postdata);
            writer.flush();
            writer.close();
            out.close();

            InputStream in = httpURLConnection.getInputStream();
            String result = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            result = sb.toString();
            Log.i("Json",result);
            JSONObject responseJSON = new JSONObject(result);
            boolean isDoc  = responseJSON.getInt("isDoc") > 0;

            loginSuccess = responseJSON.getBoolean("success");
            //isDoc = isDoc;

            reader.close();
            in.close();
            httpURLConnection.disconnect();

            if(!isDoc) {
                String patname = responseJSON.getString("patname");
                String dob = responseJSON.getString("dob");
                String bg = responseJSON.getString("bg");
                String sex = responseJSON.getString("sex");
                String phone = responseJSON.getString("phone");
                String address = responseJSON.getString("address");

                newIntent = new Intent(context, Patientstart.class);
                newIntent.putExtra("patname",patname);
                newIntent.putExtra("dob",dob);
                newIntent.putExtra("bg",bg);
                newIntent.putExtra("sex",sex);
                newIntent.putExtra("phone",phone);
                newIntent.putExtra("address",address);
            }else{
                //newIntent = new Intent(context, Docstart.class);
                Toast toast = Toast.makeText(context, "Hello Doctor " + username, Toast.LENGTH_LONG);
                toast.show();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        else if(type.equals("register")){try {
            //int id = Integer.parseInt(params[1]);
            String name = params[1];
            String dob = params[2];
            String blood_group = params[3];
            String sex = params[4];
            String phone_number = params[5];
            String address = params[6];
            String email = params[7];
            String password = params[8];
            URL url = new URL(registerURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

            String postdata = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+"&"
                    +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8")+"&"
                    +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+"&"
                    +URLEncoder.encode("dob", "UTF-8")+"="+URLEncoder.encode(dob, "UTF-8")+"&"
                    +URLEncoder.encode("blood_group", "UTF-8")+"="+URLEncoder.encode(blood_group, "UTF-8")+"&"
                    +URLEncoder.encode("sex", "UTF-8")+"="+URLEncoder.encode(sex, "UTF-8")+"&"
                    +URLEncoder.encode("phone_number", "UTF-8")+"="+URLEncoder.encode(phone_number, "UTF-8")+"&"
                    +URLEncoder.encode("address", "UTF-8")+"="+URLEncoder.encode(address, "UTF-8");
            System.out.println(postdata);
            writer.write(postdata);
            writer.flush();
            writer.close();
            out.close();

            InputStream in = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"iso-8859-1"));
            String result="", line="";
            while ((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();
            in.close();
            httpURLConnection.disconnect();

            String res2[][] = new String[1][1];
            res2[0][0] = result;
            return res2;

        } catch (IOException e) {
            e.printStackTrace();
        }
        }

        else if(type.equals("getDocList"))try{

            URL url = new URL(getDocURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            InputStream in = httpURLConnection.getInputStream();
            String result;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            in.close();
            result = sb.toString();
            Log.i("Json",result);

            //Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
            //toast.show();

            //JSONObject object = new JSONObject(result);

            JSONArray jsonArray = new JSONArray(result);
            JSONObject arrayObj = jsonArray.getJSONObject(0);
            int jsonlength = arrayObj.length();
            String docInfoArray[][] = new String[jsonlength][7];
            for (int i = 0; i < jsonlength ; i++) {
                try{
                    JSONObject object1 = arrayObj.getJSONObject(String.valueOf(i+1));
                    docInfoArray[i][0] = object1.getString("did");
                    docInfoArray[i][1] = object1.getString("name");
                    docInfoArray[i][2] = object1.getString("spec");
                    docInfoArray[i][3] = object1.getString("qual");
                    docInfoArray[i][4] = object1.getString("sex");
                    docInfoArray[i][5] = object1.getString("phone");
                    docInfoArray[i][6] = object1.getString("clinadd");
                    Log.i("loops", String.valueOf(i));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            reader.close();
            in.close();
            httpURLConnection.disconnect();


            //String endresult[][] = new String[1][1];
            //endresult[0][0] = toastMessage;

            return docInfoArray;


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }




    @Override
    protected void onPreExecute() {
    }
    @Override
    protected void onPostExecute(String[][] result) {
        if(type.equals("login") && loginSuccess) {
            context.startActivity(newIntent);
            Toast toast = Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(type.equals("login") && !loginSuccess){
            Toast toast = Toast.makeText(context, "Login ERROR.\nPlease check and Try Again", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(type.equals(("register"))){
            Toast toast = Toast.makeText(context, result[0][0], Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(type.equals("getDocList")){
            String toastMessage = "";
            for (String[] aDocInfoArray : result) {
                for (int j = 0; j < 7; j++) {
                    toastMessage += aDocInfoArray[j] + ", ";
                }
                toastMessage += " .\n\n";
            }
            Log.i("result",toastMessage);
            Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG);
            toast.show();
            delegate.BGProcessDone(result);
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

}
