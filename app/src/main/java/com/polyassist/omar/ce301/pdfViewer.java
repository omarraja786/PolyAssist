package com.polyassist.omar.ce301;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class pdfViewer extends AppCompatActivity {

    PDFView pdfView;
    private TextToSpeech myTTS;
    String parsedText = "";
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    speech(parsedText);

                } else {
                    Log.e("TTS", "Initialisation Failed!");
                }
            }
        });


        pdfView = (PDFView) findViewById(R.id.pdf_viewer);


        //https://stackoverflow.com/questions/29763405/android-get-text-from-pdf
        if (getIntent() != null) {
            String viewType = getIntent().getStringExtra("ViewType");
            if (viewType != null || TextUtils.isEmpty(viewType)) {
                if (viewType.equals("storage")) {
                    Uri pdfFile = Uri.parse(getIntent().getStringExtra("FileUri"));
                    pdfView.fromUri(pdfFile)
                            .password(null)
                            .defaultPage(0)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .onPageError(new OnPageErrorListener() {
                                @Override
                                public void onPageError(int page, Throwable t) {
                                    Toast.makeText(pdfViewer.this, "Error Opening PDF", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .onRender(new OnRenderListener() {
                                @Override
                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                    pdfView.fitToWidth();
                                }
                            })
                            .enableAnnotationRendering(true)
                            .load();
                    try {
                        String filePath = PathUtil.getPath(this, pdfFile);
                        PdfReader reader = new PdfReader(filePath);
                        int n = reader.getNumberOfPages();
                        for (int i = 0; i < n; i++) {
                            parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim().toLowerCase() + "\n"; //Extracting the content from the different pages
                        }
                        System.out.println(parsedText);
                        reader.close();
                        //speech(parsedText);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tts_stop,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.tts_stop) {
            myTTS.stop();
            return true;
        }
        if(item.getItemId() == R.id.tts_start) {
            speech(parsedText);
            return true;
        }

        else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void speech(String charSequence) {
        ///
        Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher reMatcher = re.matcher(charSequence);
        /////
        int position=0 ;
        int sizeOfChar= charSequence.length();
        String testStri= charSequence.substring(position,sizeOfChar);
        while(reMatcher.find()) {
            String temp="";

            try {

                temp = testStri.substring(charSequence.lastIndexOf(reMatcher.group()), charSequence.indexOf(reMatcher.group())+reMatcher.group().length());
                myTTS.speak(temp, TextToSpeech.QUEUE_ADD, null,"speak");


            } catch (Exception e) {
                temp = testStri.substring(0, testStri.length());
                myTTS.speak(temp, TextToSpeech.QUEUE_ADD, null);
                break;

            }

        }

    }

    @Override
    public void onDestroy() {
        if (myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
    }
}
