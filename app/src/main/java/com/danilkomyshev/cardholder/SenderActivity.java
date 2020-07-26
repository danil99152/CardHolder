package com.danilkomyshev.cardholder;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danilkomyshev.cardholder.nfc.OutcomingNfcManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SenderActivity extends AppCompatActivity implements OutcomingNfcManager.NfcActivity {

//    private TextView tvOutcomingMessage;
//    private EditText etOutcomingMessage;

//    TextView btnSetOutcomingMessage = findViewById(R.id.card_item);
    String outMessage = "Android Engineer" + "\nАскар Касимов" + "\nтел: 8199283746728";
    ArrayList<String> cards = new ArrayList<>();
    RecyclerView mMessagesRecycler;

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        mMessagesRecycler = findViewById(R.id.messages_recycler);
        FloatingActionButton addButton = findViewById(R.id.addButton);

        mMessagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapter dataAdapter = new DataAdapter(this, cards);
        mMessagesRecycler.setAdapter(dataAdapter);


        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

        initViews();

        // encapsulate sending logic in a separate class
        OutcomingNfcManager outcomingNfccallback = new OutcomingNfcManager(this);
        this.nfcAdapter.setOnNdefPushCompleteCallback(outcomingNfccallback, this);
        this.nfcAdapter.setNdefPushMessageCallback(outcomingNfccallback, this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SenderActivity.this, "On development", Toast.LENGTH_SHORT).show();
            }
        });
        mMessagesRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOutcomingMessage();
            }
        });
    }

    private void initViews() {
        cards.add(outMessage);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

//    private void setOutGoingMessage() {
////        String outMessage = this.etOutcomingMessage.getText().toString();
//        outMessage = "Android Engineer" + "\nАскар Касимов" + "\nтел: 8199283746728";
////        this.tvOutcomingMessage.setText(outMessage);
//    }

    @Override
    public String getOutcomingMessage() {
        return this.outMessage;
    }

    @Override
    public void signalResult() {
        // this will be triggered when NFC message is sent to a device.
        // should be triggered on UI thread. We specify it explicitly
        // cause onNdefPushComplete is called from the Binder thread
        runOnUiThread(() ->
                Toast.makeText(SenderActivity.this, R.string.message_beaming_complete, Toast.LENGTH_SHORT).show());
    }

}
