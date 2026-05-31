package com.example.bansach.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.R;
import com.example.bansach.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchActivity extends BaseActivity {
    private EditText edtSearch;
    private RecyclerView rvSuggestions;
    private LinearLayout layoutHistory;
    private LinearLayout layoutSuggestions;

    private List<String> fullBookListName;
    private List<String> suggestList;
    private SuggestionAdapter suggestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edtSearch);
        rvSuggestions = findViewById(R.id.rvSuggestProducts);
        layoutHistory = findViewById(R.id.layoutHistory);
        layoutSuggestions = findViewById(R.id.layoutSuggestions);

        fullBookListName = new ArrayList<>();
        suggestList = new ArrayList<>();
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        suggestAdapter = new SuggestionAdapter(suggestList);
        rvSuggestions.setAdapter(suggestAdapter);

        fetchOnlineDataProduct();

        edtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
        }
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();

                if (keyword.isEmpty()) {
                    if (layoutHistory != null) layoutHistory.setVisibility(View.VISIBLE);
                    if (layoutSuggestions != null) layoutSuggestions.setVisibility(View.GONE);
                } else {
                    if (layoutHistory != null) layoutHistory.setVisibility(View.GONE);
                    if (layoutSuggestions != null) layoutSuggestions.setVisibility(View.VISIBLE);

                    suggestList.clear();
                    String keywordKhongDau = loaiBoDauTiengViet(keyword);
                    for (String name : fullBookListName) {
                        if (name != null) {
                            String nameKhongDau = loaiBoDauTiengViet(name);
                            if (nameKhongDau.contains(keywordKhongDau)) {
                                suggestList.add(name);
                            }
                        }
                    }
                    suggestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String finalKeyword = edtSearch.getText().toString().trim();
                    if (!finalKeyword.isEmpty()) {

                        luuLichSuTimKiem(finalKeyword);

                        Intent intent = new Intent(SearchActivity.this, SearchProductActivity.class);
                        intent.putExtra("KEYWORD", finalKeyword);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });

        setupHeader();
    }

    private void fetchOnlineDataProduct() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("book");
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullBookListName.clear();
                for (DataSnapshot bookSnap : snapshot.getChildren()) {
                    Book book = bookSnap.getValue(Book.class);
                    if (book != null && book.getTenSP() != null) {
                        fullBookListName.add(book.getTenSP());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public String loaiBoDauTiengViet(String str) {
        if (str == null) return "";
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll("đ", "d");
        } catch (Exception e) {
            return str.toLowerCase();
        }
    }

    private void luuLichSuTimKiem(String tuKhoa) {
        SharedPreferences sharedPref = getSharedPreferences("SearchHistoryPrefs", MODE_PRIVATE);
        String lichSuCu = sharedPref.getString("HISTORY_STRING", "");

        if (!lichSuCu.startsWith(tuKhoa + ",")) {
            String lichSuMoi = tuKhoa + "," + lichSuCu;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("HISTORY_STRING", lichSuMoi);
            editor.apply();
        }
    }
    private class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
        private List<String> listData;

        public SuggestionAdapter(List<String> listData) {
            this.listData = listData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String bookName = listData.get(position);
            holder.tvName.setText(bookName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    luuLichSuTimKiem(bookName);

                    Intent intent = new Intent(SearchActivity.this, SearchProductActivity.class);
                    intent.putExtra("KEYWORD", bookName);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}