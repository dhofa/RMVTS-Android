package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogActivityMenu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.LogActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;

public class LogActivityAdapter extends RecyclerView.Adapter<LogActivityAdapter.MyViewHolder> {
    private List<LogActivity> logActivityList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_log_activity, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LogActivity logActivity = logActivityList.get(position);
        holder.title.setText(logActivity.getTitle());
        holder.detail.setText(logActivity.getDetail());
    }

    @Override
    public int getItemCount() {
        return logActivityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail;

        public MyViewHolder(View view) {
            super(view);
            title  = (TextView) view.findViewById(R.id.title);
            detail = (TextView) view.findViewById(R.id.detail);
        }
    }

    public LogActivityAdapter(List<LogActivity> logActivityList){
        this.logActivityList = logActivityList;
    }


}
