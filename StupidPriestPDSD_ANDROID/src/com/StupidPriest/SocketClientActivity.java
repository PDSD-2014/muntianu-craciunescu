/*
 * This is a simple Android mobile client
 * This application read any string messege typed on the text field and
 * send it to the server when the Send button is pressed
 */
 
package com.StupidPriest;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
 
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
public class SocketClientActivity extends Activity {
 
 private Socket client;
 private PrintWriter printwriter;
 private EditText textField;
 private Button button;
 private String messsage;
 
 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.client);
 
  textField = (EditText) findViewById(R.id.editText1); //reference to the text field
  button = (Button) findViewById(R.id.button1);   //reference to the send button
 
  //Button press event listener
  button.setOnClickListener(new View.OnClickListener() {
 
   public void onClick(View v) {
 
    messsage = textField.getText().toString(); //get the text message on the text field
    textField.setText("");      //Reset the text field to blank
 
    try {
 
     client = new Socket("10.0.2.2", 4444);  //connect to server
     printwriter = new PrintWriter(client.getOutputStream(),true);
     printwriter.write(messsage);  //write the message to output stream
 
     printwriter.flush();
     printwriter.close();
     client.close();   //closing the connection
 
    } catch (UnknownHostException e) {
     e.printStackTrace();
    } catch (IOException e) {
     e.printStackTrace();
    }
   }
  });
 
 }
}
