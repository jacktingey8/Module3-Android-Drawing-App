package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private final int[] colors = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawing_view);
        Button btnColor = findViewById(R.id.btn_color);
        Button btnClear = findViewById(R.id.btn_clear);
        SeekBar seekBarSize = findViewById(R.id.seek_bar_size);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnGallery = findViewById(R.id.btn_gallery);

        btnColor.setOnClickListener(v -> showColorPickerDialog());

        btnClear.setOnClickListener(v -> drawingView.clear());

        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.setStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> saveDrawing());

        btnGallery.setOnClickListener(v -> checkPermissionAndOpenGallery());
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 101);
            } else {
                openGallery();
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 100) {
                saveDrawing();
            } else if (requestCode == 101) {
                openGallery();
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void showColorPickerDialog() {
        String[] colorNames = {"Black", "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_a_color);
        builder.setItems(colorNames, (dialog, which) -> drawingView.setPaintColor(colors[which]));
        builder.show();
    }

    private void saveDrawing() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
        }

        Bitmap bitmap = drawingView.getBitmap();
        String fileName = "drawing_" + System.currentTimeMillis() + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DrawingApp");
        }

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    Toast.makeText(this, R.string.saved_to_gallery, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.failed_to_save) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
