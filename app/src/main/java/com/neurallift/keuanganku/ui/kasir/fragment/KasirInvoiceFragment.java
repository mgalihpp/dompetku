package com.neurallift.keuanganku.ui.kasir.fragment;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Barang;
import com.neurallift.keuanganku.ui.kasir.adapter.BarangInvoiceAdapter;
import com.neurallift.keuanganku.utils.DateTimeUtils;
import com.neurallift.keuanganku.utils.FormatUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class KasirInvoiceFragment extends Fragment {
    public static final String ARG_TOTAL_PEMBAYARAN = "total_pembayaran";
    public static final String ARG_TOTAL_DIBAYAR = "total_dibayar";
    public static final String ARG_TOTAL_KEMBALIAN = "total_kembalian";
    public static final String ARGS_BARANG_LIST = "barangList";
    public static final String ARGS_JUMLAH_LIST = "jumlahList";

    private double totalPembayaran;
    private double nominalDibayar;
    private double kembalian;
    private final Map<Barang, Integer> selectedBarangMap = new HashMap<>();

    private TextView tv_id_transaksi;
    private TextView tv_tanggal;
    private RecyclerView rv_invoice_items;
    private TextView tv_total_invoice;
    private TextView tv_nominal_dibayar;
    private TextView tv_kembalian_invoice;
    private Button btn_save_image;
    private Button btn_share;
    private Button btn_kembali;
    private BarangInvoiceAdapter barangInvoiceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            ArrayList<Barang> barangList = getArguments().getParcelableArrayList(ARGS_BARANG_LIST);
            int[] jumlahList = getArguments().getIntArray(ARGS_JUMLAH_LIST);
            totalPembayaran = getArguments().getFloat(ARG_TOTAL_PEMBAYARAN);
            nominalDibayar = getArguments().getFloat(ARG_TOTAL_DIBAYAR);
            kembalian = getArguments().getFloat(ARG_TOTAL_KEMBALIAN);

            if (barangList != null && jumlahList != null) {
                for (int i = 0; i < barangList.size(); i++) {
                    selectedBarangMap.put(barangList.get(i), jumlahList[i]);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kasir_invoice, container, false);

        initViews(view);
        setupRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String prefix = "#TRX";
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        int random = new Random().nextInt(9000) + 1000; // 1000–9999
        String id = prefix + date + random;

        tv_id_transaksi.setText(id);
        tv_tanggal.setText( DateTimeUtils.formatDateFull(DateTimeUtils.formatDate(new Date())) + ", " + DateTimeUtils.formatTime(new Date()));
        tv_total_invoice.setText("Total: " + FormatUtils.formatCurrency(totalPembayaran));
        tv_nominal_dibayar.setText("Dibayar: " + FormatUtils.formatCurrency(nominalDibayar));
        tv_kembalian_invoice.setText("Kembalian: " + FormatUtils.formatCurrency(kembalian));

        setupButtonListeners();
    }

    void initViews(View view){
        tv_id_transaksi = view.findViewById(R.id.tv_id_transaksi);
        tv_tanggal = view.findViewById(R.id.tv_tanggal);
        rv_invoice_items = view.findViewById(R.id.rv_invoice_items);
        tv_total_invoice = view.findViewById(R.id.tv_total_invoice);
        tv_nominal_dibayar = view.findViewById(R.id.tv_nominal_dibayar);
        tv_kembalian_invoice = view.findViewById(R.id.tv_kembalian_invoice);
        btn_save_image = view.findViewById(R.id.btn_save_image);
        btn_share = view.findViewById(R.id.btn_share);
        btn_kembali = view.findViewById(R.id.btn_kembali);
    }

    void setupRecyclerView(){
        barangInvoiceAdapter = new BarangInvoiceAdapter();
        rv_invoice_items.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_invoice_items.setAdapter(barangInvoiceAdapter);

        barangInvoiceAdapter.setData(new ArrayList<>(selectedBarangMap.keySet()), new ArrayList<>(selectedBarangMap.values()));
    }

    private void setupButtonListeners() {

        btn_save_image.setOnClickListener(v -> {
            LinearLayout linearLayout = requireView().findViewById(R.id.layout_invoice);
            Bitmap bitmap = getBitmapFromUiView(linearLayout);
            saveBitmapImage(bitmap);
        });

        btn_kembali.setOnClickListener(v -> {

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.navigation_kasir, null, getNavOptions());

        });

        btn_share.setOnClickListener(v -> {
            LinearLayout linearLayout = requireView().findViewById(R.id.layout_invoice);
            Bitmap bitmap = getBitmapFromUiView(linearLayout);
            Uri imageUri = getImageUriFromBitmap(requireContext(), bitmap);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Bagikan gambar via"));
        });
    }

    /**Get Bitmap from any UI View
     * @param view any UI view to get Bitmap of
     * @return returnedBitmap the bitmap of the required UI View */
    private Bitmap getBitmapFromUiView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);

        //return the bitmap
        return returnedBitmap;
    }


    /**Save Bitmap To Gallery
     * @param bitmap The bitmap to be saved in Storage/Gallery*/
    private void saveBitmapImage(Bitmap bitmap) {
        long timestamp = System.currentTimeMillis();

        //Tell the media scanner about the new file so that it is immediately available to the user.
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp);
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name));
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                        } catch (Exception e) {
                            Log.e(TAG, "saveToGallery: ", e);
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContext().getContentResolver().update(uri, values, null, null);

                    Toast.makeText(getContext(), "Saved...", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "saveToGallery: ", e);
                }
            }
        } else {
            File imageFileFolder = new File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name));
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs();
            }
            String mImageName = "" + timestamp + ".png";

            File imageFile = new File(imageFileFolder, mImageName);
            try {
                OutputStream outputStream = new FileOutputStream(imageFile);
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "saveToGallery: ", e);
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
                getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Toast.makeText(getContext(), "Saved...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "saveToGallery: ", e);
            }
        }
    }

    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdirs(); // Buat folder kalau belum ada

        File file = new File(cachePath, "invoice.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap to cache", e);
        }

        return androidx.core.content.FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
    }


    private NavOptions getNavOptions() {
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.enter_from_right)
                .setExitAnim(R.anim.exit_to_left)
                .setPopEnterAnim(R.anim.enter_from_left)
                .setPopExitAnim(R.anim.exit_to_right)
                .build();
    }

}
