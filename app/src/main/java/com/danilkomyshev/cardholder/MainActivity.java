package com.danilkomyshev.cardholder;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danilkomyshev.cardholder.nfc.OutcomingNfcManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OutcomingNfcManager.NfcActivity {

    public String outMessage;

    ArrayList<String> cards = new ArrayList<>();
    RecyclerView mMessagesRecycler;

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMessagesRecycler = findViewById(R.id.messages_recycler);

        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

        // encapsulate sending logic in a separate class
        OutcomingNfcManager outcomingNfccallback = new OutcomingNfcManager(this);
        this.nfcAdapter.setOnNdefPushCompleteCallback(outcomingNfccallback, this);
        this.nfcAdapter.setNdefPushMessageCallback(outcomingNfccallback, this);


        mMessagesRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOutcomingMessage();
            }
        });
    }

    public void click1(View v) {
        EditText work = (EditText) findViewById(R.id.work);
        EditText name = (EditText) findViewById(R.id.name);
        EditText mob = (EditText) findViewById(R.id.mob);

        outMessage = "Должность: " + work.getText()+"\n"+ "Имя: " +name.getText()+"\n"+"Телефон: "+mob.getText();
        Toast.makeText(MainActivity.this, "Визитка готова к отправке", Toast.LENGTH_LONG).show();
    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // read nfc tag
        if (getIntent().hasExtra(NfcAdapter.EXTRA_TAG)) {

            NdefMessage ndefMessage = this.getNdefMessageFromIntent(getIntent());

            if (ndefMessage.getRecords().length > 0) {

                NdefRecord ndefRecord = ndefMessage.getRecords()[0];

                String payload = new String(ndefRecord.getPayload());

                Toast.makeText(this, payload, Toast.LENGTH_SHORT).show();

            }
        }
        enableForegroundDispatchSystem();
    }

    public NdefMessage getNdefMessageFromIntent(Intent intent) {
        NdefMessage ndefMessage = null;
        Parcelable[] extra = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (extra != null && extra.length > 0) {
            ndefMessage = (NdefMessage) extra[0];
        }
        return ndefMessage;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String action = intent.getAction();
        String inMessage = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord ndefRecord_0 = inNdefRecords[0];

            //Полученное сообщение
            inMessage = new String(ndefRecord_0.getPayload());
            Toast.makeText(this, inMessage, Toast.LENGTH_SHORT).show();
        }
        setTextInRelo(inMessage);
    }

    private void setTextInRelo(String inMessage) {
        cards.add(inMessage);
        mMessagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapter dataAdapter = new DataAdapter(this, cards);
        mMessagesRecycler.setAdapter(dataAdapter);
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
                Toast.makeText(this, R.string.message_beaming_complete, Toast.LENGTH_SHORT).show());
    }

}
