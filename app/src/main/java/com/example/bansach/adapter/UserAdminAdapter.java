//package com.example.bansach.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.AdapterView;
//import android.widget.Spinner;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.bansach.R;
//import com.example.bansach.model.UserDisplayItem;
//
//import java.util.List;
//
//public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.ViewHolder> {
//
//    public interface OnUserActionListener {
//        void onRoleChanged(UserDisplayItem item, String newRole);
//        void onStatusChanged(UserDisplayItem item, boolean isActive);
//    }
//
//    private final Context context;
//    private final List<UserDisplayItem> userList;
//    private final OnUserActionListener listener;
//    private final String[] roles = {"Admin", "Client"};
//
//    public UserAdminAdapter(Context context, List<UserDisplayItem> userList, OnUserActionListener listener) {
//        this.context = context;
//        this.userList = userList;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_user, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        UserDisplayItem item = userList.get(position);
//
//        holder.tvFullName.setText(item.getFullName());
//        holder.tvEmail.setText(item.getEmail());
//
//        // --- Spinner vai trò ---
//        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, roles);
//        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        holder.spinnerRole.setOnItemSelectedListener(null); // tránh gọi callback khi đang gán dữ liệu
//        holder.spinnerRole.setAdapter(roleAdapter);
//
//        int rolePos = item.getRole() != null && item.getRole().equalsIgnoreCase("Admin") ? 0 : 1;
//        holder.spinnerRole.setSelection(rolePos);
//
//        holder.spinnerRole.post(() -> holder.spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                String selectedRole = roles[pos];
//                if (!selectedRole.equalsIgnoreCase(item.getRole())) {
//                    item.setRole(selectedRole);
//                    if (listener != null) listener.onRoleChanged(item, selectedRole);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        }));
//
//        // --- Switch trạng thái ---
//        holder.switchStatus.setOnCheckedChangeListener(null); // tránh gọi callback khi đang gán dữ liệu
//        boolean isActive = "Hoạt động".equalsIgnoreCase(item.getStatus());
//        holder.switchStatus.setChecked(isActive);
//
//        holder.switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            String newStatus = isChecked ? "Hoạt động" : "Bị khóa";
//            item.setStatus(newStatus);
//            if (listener != null) listener.onStatusChanged(item, isChecked);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return userList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tvFullName, tvEmail;
//        Spinner spinnerRole;
//        Switch switchStatus;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvFullName = itemView.findViewById(R.id.tvFullName);
//            tvEmail = itemView.findViewById(R.id.tvEmail);
//            spinnerRole = itemView.findViewById(R.id.spinnerRole);
//            switchStatus = itemView.findViewById(R.id.switchStatus);
//        }
//    }
//}