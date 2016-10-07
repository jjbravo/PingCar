package com.edurobotica.pingcar.pingcar;

import java.util.ArrayList;
import java.util.Set;

import com.edurobotica.pingcar.pingcar.ConexionBT;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class pingCarActivityMain extends AppCompatActivity implements  OnTouchListener {



    private Button up,down,righ,left;
    //private TextView dato;
    //private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    private int dato_read;

    public static final String TAG ="LanchaAndroid";
    public static final boolean D=true;
    //Tipos de mensajes enviados y recibidos dsde el handler de conexion
    public static final int Mensaje_Estado_Cambiado =1;
    public static final int Mensaje_Leido=2;
    public static final int Mensaje_Escrito=3;
    public static final int Mensaje_Nombre_Dispositivo=4;
    public static final int Mensaje_TOAST=5;
    public static final int MESSAGE_Desconectado=6;
    public static final int REQUEST_ENABLE_TB=7;
    public static String DEVICE_NAME="device_name";
    public static final String TOAST = "toast";
    private boolean seleccionador = false;
    public static   String stateConnect="0";
    private Vibrator vibrador;
    //public static final String address="20:13:08:09:05:27";
    //nombre del dispositivo
    private String mConnectedDeviceName=null;
    //adaptador bluetooth local
    private BluetoothAdapter AdaptadorBT=null;
    //objeto miembro para el servicio de ConexionBT
    private ConexionBT Servicio_BT = null;
    // Insert your server's MAC address
    private static String MAC = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_car_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        up =(Button) findViewById(R.id.adelante);
        down =(Button) findViewById(R.id.atras);
        left =(Button) findViewById(R.id.izquierda);
        righ =(Button) findViewById(R.id.derecha);



        up.setOnTouchListener(this);
        down.setOnTouchListener(this);
        left.setOnTouchListener(this);
        righ.setOnTouchListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            case R.id.idconectar:

                if(stateConnect=="1"){
                    if(Servicio_BT != null)

                        item.setTitle("Conectar");
                    Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show();
                    Servicio_BT.stop();

                    stateConnect ="0";
                }else{
                    pairedDevices= AdaptadorBT.getBondedDevices();
                    //pairedDevices=BA.getBondedDevices();
                    ArrayList list = new ArrayList();
                    for(BluetoothDevice bt : pairedDevices)
                        list.add(bt.getName()+"\n"+bt.getAddress());
                    Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();
                    final ArrayAdapter adapter = new ArrayAdapter
                            (this,android.R.layout.simple_list_item_single_choice,list);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Conectar con:").setSingleChoiceItems(adapter,-1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            String[] dato=null;
                            String mac=null;

                            mac=(String) adapter.getItem(arg1);
                            dato=mac.split("\n");

                            MAC=dato[1];
                            DEVICE_NAME=dato[0];

                        }
                    }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id){

                            connect(MAC);

                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });

                    item.setTitle("Desconectar");
                    builder.show();
                }

                return true;

            case R.id.idayuda:
                AlertDialog.Builder builders = new AlertDialog.Builder(this);
                builders.setTitle("Ayuda");
                builders.setMessage("Comandos de envio:\n" +
                        "Al presionar Adelante envia  'a' en ASCII = 97\n" +
                        "Al presionar Atrás envia 'b' en ASCII = 98\n" +
                        "Al presionar Izquierda envia 'c' en ASCII = 99\n" +
                        "Al presionar Derecha envia 'd' en ASCII = 100\n" +
                        "Al Soltar un boton envia 'e' en ASCII = 101\n");
                builders.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(getApplication(), "Yes", Toast.LENGTH_SHORT).show();
                    }
                });

                builders.show();
                return true;
            case R.id.idacercade:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Acerca de Ping Car");
                builder.setMessage("Ping Car es una aplicación básica para uso con la board Pinguino 2550 o Arduino. \nLa aplicación esta creada con " +
                        "fines educativos por Yohon Jairo Bravo Castro. bajo los terminos de Licencia Open Source GNU\n" +
                        "bramasterweb@gmail.com\n");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(getApplication(), "Yes", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
                return true;
            case R.id.idsalir:
                //lblMensaje.setText("Opcion 3 pulsada!");;
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setMessage("¿Desea salir?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Servicio_BT.stop();
                        finish();

                    }
                });
                dialog.setNegativeButton("No",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        switch(v.getId()){
            case R.id.adelante:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d("Entrada","Entramos al boton UP");
                    sendMessage("a");
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    Log.d("Salida","Salimos del boton UP");
                    sendMessage("e");

                }

                //Toast.makeText(getBaseContext(),  "a", Toast.LENGTH_SHORT).show();

                if(dato_read==1){
                    Toast.makeText(getBaseContext(),  "el dato recibido es uno (1)", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, ""+dato_read);
                    vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrador.vibrate(100);
                }
                break;
            case R.id.atras:

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d("Entrada","Entramos al boton DOWN");
                    sendMessage("b");
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    Log.d("Salida","Salimos del boton DOWN");
                    sendMessage("e");

                }
                if(dato_read==2){
                    Toast.makeText(getBaseContext(),  "el dato recibido es uno (1)", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, ""+dato_read);
                    vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrador.vibrate(100);
                }
                // Toast.makeText(getBaseContext(),"b", Toast.LENGTH_SHORT).show();
                break;
            case R.id.izquierda:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d("Entrada","Entramos al boton LEFT");
                    sendMessage("c");
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    Log.d("Salida","Salimos del boton LEFT");
                    sendMessage("e");

                }
                //  Toast.makeText(getBaseContext(),"d", Toast.LENGTH_SHORT).show();
                break;
            case R.id.derecha:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d("Entrada","Entramos al boton RIGH");
                    sendMessage("d");
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    Log.d("Salida","Salimos del boton RIGH");
                    sendMessage("e");

                }
                //   Toast.makeText(getBaseContext(),"c", Toast.LENGTH_SHORT).show();
                break;


        }
        return false;
    }
    private void sendMessage(String message) {
        // TODO Auto-generated method stub
        if(Servicio_BT.getState()==ConexionBT.STATE_CONNECTED){
            if(message.length()>0){
                byte[] send = message.getBytes(); //obtenemos los bytes del mensaje
                if(D) Log.e(TAG,"Mensaje enviado: "+message);
                Servicio_BT.write(send);
            }
        }
    }// fin del mensaje



    public void onStart(){
        super.onStart();
        ConfigBT();
    }
    public void onDestroy(){
        super.onDestroy();
        if(Servicio_BT!=null)
            Servicio_BT.stop(); //desconectamos el servicio
    }
    public void ConfigBT() {
        // TODO Auto-generated method stub
        //obtenemos el adaptador bluetooth
        AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
        if(AdaptadorBT.isEnabled()){ // si el BT esta encendido
            if(Servicio_BT == null){ // y el servicio es nulo, invocamos el Servicio_BT
                Servicio_BT= new ConexionBT(this,mHandler);
            }
        }else{
            if(D) Log.e("Setup","ConexionBT apagado..");
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth,REQUEST_ENABLE_TB);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // una vez que se ha realizado una actividad regresa un resulado
        switch(requestCode){
            case REQUEST_ENABLE_TB: // respuesta de intento de encendido
                if(resultCode == Activity.RESULT_OK){ // BT esta acitvado e iniciamos servicios
                    ConfigBT();
                }else{
                    finish();
                }
                break;
        }
    }

    private void connect(String mMAC) {
        // TODO Auto-generated method stub
        if(stateConnect=="0"){
            if(D) Log.e("Conexion","Conectando");
            vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrador.vibrate(200);
            BluetoothDevice device=AdaptadorBT.getRemoteDevice(mMAC);
            Servicio_BT.connect(device);

            //btn_connect.setImageResource(R.drawable.bluon);


            stateConnect="1";
        }else{
            if(Servicio_BT != null)


                Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show();

            Servicio_BT.stop();
            stateConnect ="0";
        }

        return;
    }

    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case Mensaje_Escrito:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    if(D) Log.e(TAG,"Message_write =w= "+writeMessage);
                    break;

                case Mensaje_Leido:
                    byte[] readBuf = (byte[]) msg.obj;
                    //Construye un Sting de los bytes validos en el buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    dato_read= Integer.parseInt(readMessage);


                    if(D) Log.e(TAG, "Message_read =R= "+readMessage);
                    break;
                case Mensaje_Nombre_Dispositivo:
                    mConnectedDeviceName=msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Conectado con "+mConnectedDeviceName,Toast.LENGTH_SHORT).show();
                    seleccionador= true;
                    break;
                case Mensaje_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_Desconectado:
                    if(D) Log.e("Conexion","Desconectados");
                    seleccionador = false;
                    break;
                case Mensaje_Estado_Cambiado:
                    if(D) Log.e("Estado BT","Escuchando conexiones entrantes");





                    break;
            }
        }
    };

}
