package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogIgnition;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.Ignition;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.Vibration;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;

public class LogIgnitionAdapter extends RecyclerView.Adapter<LogIgnitionAdapter.MyViewHolder> {
    private List<Ignition> logIgnitionList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_log_activity, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Ignition logVibration = logIgnitionList.get(position);
        String title_text = logVibration.getTitle();
        String detail_text= logVibration.getDetail();
        holder.title.setText(title_text);
        holder.detail.setText(detail_text);
        holder.imageSymbol.setImageResource(R.drawable.ic_ignition);
    }

    @Override
    public int getItemCount() {
        return logIgnitionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail;
        public ImageView imageSymbol;

        public MyViewHolder(View view) {
            super(view);
            title  = (TextView) view.findViewById(R.id.title);
            detail = (TextView) view.findViewById(R.id.detail);
            imageSymbol = (ImageView) view.findViewById(R.id.image_symbol);
        }
    }

    public LogIgnitionAdapter(List<Ignition> logActivityList){
        this.logIgnitionList = logActivityList;
    }


}
