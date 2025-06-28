package com.example.moviebooking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CinesGPTActivity extends AppCompatActivity {

    EditText etMessage;
    Button btnSend;
    TextView tvResponse;

    // Duy trì hội thoại giữa người dùng và AI
    StringBuilder conversationHistory = new StringBuilder();

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinesgpt);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvResponse = findViewById(R.id.tvResponse);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPrompt = etMessage.getText().toString();
                if (!userPrompt.isEmpty()) {
                    sendPromptToOllama(userPrompt);
                } else {
                    tvResponse.setText("Please enter your question.");
                }
            }
        });
    }

    private void sendPromptToOllama(String prompt) {
        try {
            // Thêm câu hỏi mới vào lịch sử hội thoại
            String fullPrompt = conversationHistory.toString() + "User: " + prompt + "\nAI:";

            JSONObject json = new JSONObject();
            json.put("model", "llama3"); // đảm bảo model này đã được pull: ollama pull llama3
            json.put("prompt", fullPrompt);
            json.put("stream", false);  // để response trả về đầy đủ

            String jsonString = json.toString();
            Log.d("RequestJSON", jsonString); // debug log

            RequestBody body = RequestBody.create(
                    jsonString, MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:11434/api/generate") // IP mặc định cho Android emulator
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("OllamaError", "Connection error: ", e);
                    runOnUiThread(() -> tvResponse.setText("Connection error: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Log.d("OllamaRaw", "Code: " + response.code() + "\nResp: " + responseText);

                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> tvResponse.setText("AI error (Code " + response.code() + "): " + responseText));
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(responseText);
                            String aiReply = jsonObject.optString("response", "(no response)").trim();

                            // Lưu vào lịch sử hội thoại
                            conversationHistory.append("User: ").append(prompt).append("\n");
                            conversationHistory.append("AI: ").append(aiReply).append("\n");

                            // Giới hạn chiều dài nếu quá dài
                            if (conversationHistory.length() > 3000) {
                                conversationHistory = new StringBuilder(conversationHistory.substring(conversationHistory.length() - 2000));
                            }

                            runOnUiThread(() -> tvResponse.setText(aiReply));
                        } catch (Exception e) {
                            Log.e("JSONParse", "JSON parsing error", e);
                            runOnUiThread(() -> tvResponse.setText("Failed to process AI response."));
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("RequestError", "JSON creation error", e);
            tvResponse.setText("Failed to create request.");
        }
    }
}
