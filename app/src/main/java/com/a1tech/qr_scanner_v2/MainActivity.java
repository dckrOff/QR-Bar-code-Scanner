package com.a1tech.qr_scanner_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_ALLOW_MANUAL_INPUT = "allow_manual_input";

    private boolean allowManualInput;
    private TextView barcodeResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeResultView = findViewById(R.id.barcode_result_view);
    }

    public void onAllowManualInputCheckboxClicked(View view) {
        allowManualInput = ((CheckBox) view).isChecked();
    }

    public void onScanButtonClicked(View view) {
        GmsBarcodeScannerOptions.Builder optionsBuilder = new GmsBarcodeScannerOptions.Builder();
        if (allowManualInput) {
            optionsBuilder.allowManualInput();
        }
        GmsBarcodeScanner gmsBarcodeScanner = GmsBarcodeScanning.getClient(this, optionsBuilder.build());
        gmsBarcodeScanner
                .startScan()
                .addOnSuccessListener(barcode -> barcodeResultView.setText(getSuccessfulMessage(barcode)))
                .addOnFailureListener(e -> barcodeResultView.setText(getErrorMessage((MlKitException) e)))
                .addOnCanceledListener(() -> barcodeResultView.setText("Code scanner is canceled"));
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_ALLOW_MANUAL_INPUT, allowManualInput);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        allowManualInput = savedInstanceState.getBoolean(KEY_ALLOW_MANUAL_INPUT);
    }

    private String getSuccessfulMessage(Barcode barcode) {
        String barcodeValue =
                String.format(
                        Locale.US,
                        "Display Value: %s\nRaw Value: %s\nFormat: %s\nValue Type: %s",
                        barcode.getDisplayValue(),
                        barcode.getRawValue(),
                        barcode.getFormat(),
                        barcode.getValueType());
        return "Barcode result\n" + barcodeValue;
    }

    @SuppressLint("SwitchIntDef")
    private String getErrorMessage(MlKitException e) {
        switch (e.getErrorCode()) {
            case MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED:
                return getString(R.string.error_camera_permission_not_granted);
            case MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE:
                return getString(R.string.error_app_name_unavailable);
            default:
                return getString(R.string.error_default_message, e);
        }
    }
}